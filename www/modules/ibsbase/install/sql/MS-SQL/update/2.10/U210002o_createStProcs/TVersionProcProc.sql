/******************************************************************************
 * All stored procedures regarding the TVersionProc table. <BR>
 * 
 * @version     2.10.0001, 26.01.2001
 *
 * @author      Klaus Reimüller (KR)  010126
 ******************************************************************************
 */

/******************************************************************************
 * Inherit the tuples from one tVersion to another tVersion. <BR>
 * If there are any tVersions currently inheriting their tuples from the second
 * tVersion they will also inherit their tuples from the first tVersion. <BR>
 * This function must be called from within a transaction handled code block
 * because it uses savepoints.
 *
 * @input parameters:
 * @param   ai_majorTVersionId  Id of the major tVersion from which the tuples
 *                              shall be inherited.
 * @param   ai_minorTVersionId  Id of minor tVersion to which the tuples shall
 *                              be inherited.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersionProc$inherit'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersionProc$inherit
(
    -- input parameters:
    @ai_majorTVersionId     TVERSIONID,
    @ai_minorTVersionId     TVERSIONID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INT,            -- counter
    @l_retValue             INT,            -- return value of this function
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the minor
                                            -- tVersion
    @l_code                 NAME,           -- the actually handled procedure
                                            -- code
    @l_inheritedFrom        TVERSIONID      -- id of tVersion from which the
                                            -- actual tVersion/code pair is
                                            -- inherited

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_TVersionProc$inherit

    -- get the data of the tVersion to which to inherit the tuples:
    SELECT  @l_posNoPath = posNoPath
    FROM    ibs_TVersion
    WHERE   id = @ai_minorTVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get minor tVersion data', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0 OR @l_count = 0)  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- define cursor:
    -- get all codes of the major tVersion.
    DECLARE updateCursor CURSOR FOR
        SELECT  code, inheritedFrom
        FROM    ibs_TVersionProc
        WHERE   tVersionId = @ai_majorTVersionId

    -- open the cursor:
    OPEN    updateCursor

    -- get the first object:
    FETCH NEXT FROM updateCursor INTO @l_code, @l_inheritedFrom

    -- loop through all objects:
    WHILE (@@FETCH_STATUS <> -1)        -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
            -- delete the values for the minor tVersion and all
            -- tVersions below which inherit their values from the same
            -- TVersion as that tVersion:
            DELETE  ibs_TVersionProc
            WHERE   tVersionId IN
                    (
                        SELECT  id
                        FROM    ibs_TVersion
                        WHERE   posNoPath LIKE @l_posNoPath + '%'
                    )
                AND code = @l_code
                AND inheritedFrom = @l_inheritedFrom

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'delete for act tVersion and tVersions below', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO cursorException    -- call exception handler
        END -- if

        -- get next object:
        FETCH NEXT FROM updateCursor INTO @l_code, @l_inheritedFrom
    END -- while another object found

    -- close the not longer needed cursor:
    CLOSE updateCursor
    DEALLOCATE updateCursor

    -- add the records to the minor tVersion and all tVersions
    -- below which before inherited from the same tVersion as
    -- the minor tVersion:
    INSERT INTO ibs_TVersionProc
            (tVersionId, code, name, inheritedFrom)
    SELECT  tv.id, p.code, p.name, p.inheritedFrom
    FROM    ibs_TVersionProc p, ibs_TVersion tv
    WHERE   tv.posNoPath LIKE @l_posNoPath + '%'
        AND tv.id NOT IN
            (
                SELECT  tVersionId
                FROM    ibs_TVersionProc
                WHERE   code = p.code
            )
        AND p.tVersionId = @ai_majorTVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'insert for act tVersion and tVersions below', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- return the state value:
    RETURN  @l_retValue

cursorException:                        -- an error occurred within cursor
    -- close the not longer needed cursor:
    CLOSE updateCursor
    DEALLOCATE updateCursor
exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_TVersionProc$inherit
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersionProc$inherit', @l_error, @l_ePos,
            N'ai_majorTVersionId', @ai_majorTVersionId,
            N'', N'',
            N'ai_minorTVersionId', @ai_minorTVersionId
    -- return the error code:
    RETURN  @c_NOT_OK
GO
-- p_TVersionProc$inherit


/******************************************************************************
 * This procedure creates tuples into the TVersionProc table. <BR>
 * If there exists already an entry for the specified procedure within the
 * tVersion it is overwritten with the new value. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion for which the procedures
 *                              shall be defined.
 * @param   ai_code             Unique code of the procedure.
 * @param   ai_name             Name of the procedure.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersionProc$add'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersionProc$add
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID,
    @ai_code                NAME,
    @ai_name                STOREDPROCNAME
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INT,            -- counter
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the tVersion
    @l_inheritedFrom        TVERSIONID      -- tVersion from which the tVersion
                                            -- inherited the entry before

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- get the data of the actual tVersion:
    SELECT  @l_posNoPath = posNoPath
    FROM    ibs_TVersion
    WHERE   id = @ai_tVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get tVersion data', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call common exception handler

    -- get the procedure data of the actual tVersion:
    SELECT  @l_inheritedFrom = inheritedFrom
    FROM    ibs_TVersionProc
    WHERE   tVersionId = @ai_tVersionId
        AND code = @ai_code

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get tVersion procedure data', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call common exception handler

    -- at this point we know that the operation may be done
    BEGIN TRANSACTION                   -- start the transaction block
        -- update the value for the actual tVersion and all tVersions below
        -- which inherit their values from the same TVersion as this tVersion:
        -- all these tVersions inherit now from the actual tVersion
        UPDATE  ibs_TVersionProc
        SET     name = @ai_name,
                inheritedFrom = @ai_tVersionId
        WHERE   tVersionId IN
                (
                    SELECT  id
                    FROM    ibs_TVersion
                    WHERE   posNoPath LIKE @l_posNoPath + '%'
                )
            AND code = @ai_code
            AND inheritedFrom = @l_inheritedFrom

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'update for act tVersion and tVersions below', @l_ePos OUTPUT
        IF (@l_error <> 0)      -- an error occurred?
            GOTO exception      -- call common exception handler

        -- add the record to all tVersions below which currently do not have
        -- this record:
        INSERT INTO ibs_TVersionProc (tVersionId, code, name, inheritedFrom)
        SELECT  id, @ai_code, @ai_name, @ai_tVersionId
        FROM    ibs_TVersion
        WHERE   id NOT IN
                (
                    SELECT DISTINCT tVersionId
                    FROM    ibs_TVersionProc
                    WHERE   code = @ai_code
                )
            AND posNoPath LIKE @l_posNoPath + '%'

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'insert for act tVersion and tVersions below', @l_ePos OUTPUT
        IF (@l_error <> 0)      -- an error occurred?
            GOTO exception      -- call common exception handler

    -- finish the transaction:
    COMMIT TRANSACTION                  -- make changes permanent

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersionProc$add', @l_error, @l_ePos,
            N'ai_tVersionId', @ai_tVersionId,
            N'ai_code', @ai_code,
            N'', 0,
            N'ai_name', @ai_name
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_TVersionProc$add


/******************************************************************************
 * This procedure creates tuples into the TVersionProc table. <BR>
 * It calls p_TVersionProc$add for the actual version of the type which is
 * specified by its code.
 * The tuples are stored for the actual version of that type which is identified
 * by its code.
 *
 * @input parameters:
 * @param   ai_typeCode         Code value of the type.
 * @param   ai_code             Unique code of the procedure.
 * @param   ai_name             Name of the procedure.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersionProc$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersionProc$new
(
    -- input parameters:
    @ai_typeCode            NAME,
    @ai_code                NAME,
    @ai_name                STOREDPROCNAME
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_tVersionId           TVERSIONID      -- actual tVersionId for the type

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1
    
    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_tVersionId = 0

-- body:
    -- get the actual tVersion id for the type code:
    SELECT  @l_tVersionId = actVersion
    FROM    ibs_Type 
    WHERE   code = @ai_typeCode

    -- add the new procedure entry to the table:
    EXEC @l_retValue = p_TVersionProc$add @l_tVersionId, @ai_code, @ai_name

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_TVersionProc$new


/******************************************************************************
 * Delete a tVersion specific entry for a procedure. <BR>
 * The code entry of the super tVersion is inherited to the actual tVersion and
 * all tVersions below which inherited that entry from the actual tVersion.
 * If there is no entry in the super tVersion the entries in the actual
 * tVersion and all tVersions below which inherit from the actual tVersion are
 * deleted. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion for which a procedure
 *                              shall be deleted.
 * @param   ai_code             Unique code of the procedure to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 * c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersionProc$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersionProc$delete
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID,
    @ai_code                NAME
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- tuple not found

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INT,            -- counter
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the tVersion
    @l_name                 STOREDPROCNAME, -- name of procedure in super
                                            -- tVersion
    @l_inheritedFrom        TVERSIONID,     -- tVersion from which the super
                                            -- tVersion inherits the entry
    @l_superTVersionId      TVERSIONID      -- Id of super tVersion of the
                                            -- actual tVersion

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- get the data of the actual tVersion:
    SELECT  @l_posNoPath = posNoPath,
            @l_superTVersionId = superTVersionId
    FROM    ibs_TVersion
    WHERE   id = @ai_TVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get data of actual tVersion', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call exception handler

    -- get the procedure name from the super tVersion:
    SELECT  @l_name = name, @l_inheritedFrom = inheritedFrom
    FROM    ibs_TVersionProc
    WHERE   tVersionId = @l_superTVersionId
        AND code = @ai_code

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get procedure name', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call exception handler

    -- at this point we know that the operation may be done
    BEGIN TRANSACTION                   -- start transaction
        -- check if there exists an entry in the super tVersion:
        IF (@l_count > 0)               -- entry for super tVersion exists?
        BEGIN
            -- inherit the entry from the super tVersion to the actual tVersion
            -- and all tVersions which inherited from the actual tVersion:
            UPDATE  ibs_TVersionProc
            SET     name = @l_name,
                    inheritedFrom = @l_inheritedFrom
            WHERE   tVersionId = @ai_tVersionId
                OR  inheritedFrom = @ai_tVersionId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'inherit entry from super tVersion', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler
        END -- if entry for super tVersion exists?
        else                            -- no entry for super tVersion
        BEGIN
            -- delete the entry from the actual tVersion and all tVersions
            -- which inherit from the actual tVersion:
            DELETE  ibs_TVersionProc
            WHERE   (   tVersionId = @ai_tVersionId
                    OR  inheritedFrom = @ai_tVersionId
                    )
                AND code = @ai_code

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'delete entry', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler
        END -- else no entry for super tVersion

    -- finish the transaction:
    COMMIT TRANSACTION                  -- make changes permanent

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersionProc$delete', @l_error, @l_ePos,
            N'ai_tVersionId', @ai_tVersionId,
            N'ai_code', @ai_code
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_TVersionProc$delete


/******************************************************************************
 * Delete all occurrences of a code out of the TVersionProc table. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_code             The code to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersionProc$deleteCode'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersionProc$deleteCode
(
    -- input parameters:
    @ai_code                NAME
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255)  -- error position description

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    BEGIN TRANSACTION                   -- start transaction
        -- delete the entries of the code from the TVersionProc table:
        DELETE  ibs_TVersionProc
        WHERE   code = @ai_code

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'delete', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

    -- finish the transaction:
    COMMIT TRANSACTION                  -- make changes permanent

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersionProc$deleteCode', @l_error,
            @l_ePos,
            N'', 0,
            N'ai_code', @ai_code
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_TVersionProc$deleteCode


/******************************************************************************
 * Delete all occurrences of a specific tVersion out of the TVersionProc table.
 * <BR>
 * If the tVersion is used to inherit entries to sub tVersions the sub tVersions
 * will inherit their entries from the super tVersions of the tVersion. <BR>
 * This function must be called from within a transaction handled code block
 * because it uses savepoints.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersionProc$deleteTVersion'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersionProc$deleteTVersion
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INT,            -- counter
    @l_superTVersionId      TVERSIONID      -- Id of super tVersion of the
                                            -- actual tVersion

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_TVersionProc$deleteTVersion

    -- get the data of the tVersion:
    SELECT  @l_superTVersionId = superTVersionId
    FROM    ibs_TVersion
    WHERE   id = @ai_tVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get data of tVersion', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- check if the super tVersion id was found:
    IF (@l_count > 0)                   -- super tVersion was found
    BEGIN
        -- inherit all entries from the super tVersion:
        -- the consequence of this action is, that no sub tVersion will have
        -- inherited values from this tVersion
        EXEC @l_retValue =
            p_TVersionProc$inherit @l_superTVersionId, @ai_tVersionId
    END -- if super tVersion was found
/*
    ELSE                                -- the super tVersion was not found
    BEGIN
        -- nothing to do
    END -- else the super tVersion was not found
*/

    -- check if there was an error:
    IF (@l_retValue = @c_ALL_RIGHT)     -- no error thus far?
    BEGIN
        -- delete the entries of the actual tVersion and all entries which were
        -- inherited from this tVersion from the tVersionProc table:
        DELETE  ibs_TVersionProc
        WHERE   tVersionId = @ai_tVersionId
            OR  inheritedFrom = @ai_tVersionId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'delete', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler
    END -- if no error thus far

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_TVersionProc$deleteTVersion
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersionProc$deleteTVersion', @l_error,
            @l_ePos,
            N'ai_tVersionId', @ai_tVersionId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_TVersionProc$deleteTVersion


/******************************************************************************
 * Delete all occurrences of tVersions belonging to a specific type out of the
 * TVersionProc table. <BR>
 * If any tVersion of the type is used to inherit entries to sub tVersions the
 * sub tVersions will inherit their entries from the super tVersions of the
 * specific tVersion. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_typeId           Id of the type to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersionProc$deleteType'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersionProc$deleteType
(
    -- input parameters:
    @ai_typeId              TYPEID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_tVersionId           TVERSIONID      -- id of actual tVersion

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    BEGIN TRANSACTION                   -- start transaction
        -- define cursor:
        -- get all tVersions of the type which shall be deleted.
        DECLARE c_TVersionProc$deleteType CURSOR FOR
            SELECT  id
            FROM    ibs_TVersion
            WHERE   typeId = @ai_typeId

        -- open the cursor:
        OPEN    c_TVersionProc$deleteType

        -- get the first object:
        FETCH NEXT FROM c_TVersionProc$deleteType INTO @l_tVersionId

        -- loop through all objects:
        WHILE (@@FETCH_STATUS <> -1 AND @l_retValue = @c_ALL_RIGHT)
                                        -- another object found?
        BEGIN
            -- Because @@FETCH_STATUS may have one of the three values
            -- -2, -1, or 0 all of these cases must be checked.
            -- In this case the tuple is skipped if it was deleted during
            -- the execution of this procedure.
            IF (@@FETCH_STATUS <> -2)
            BEGIN
                -- delete the entries for the actual tVersion:
                EXEC @l_retValue = p_TVersionProc$deleteTVersion @l_tVersionId
            END -- if

            -- get next object:
            FETCH NEXT FROM c_TVersionProc$deleteType INTO @l_tVersionId
        END -- while another object found

        -- close the not longer needed cursor:
        CLOSE c_TVersionProc$deleteType
        DEALLOCATE c_TVersionProc$deleteType

    -- finish the transaction:
    -- check if there occurred an error:
    IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
        COMMIT TRANSACTION          -- make changes permanent
    ELSE                            -- an error occured
        ROLLBACK TRANSACTION        -- undo changes

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersionProc$deleteType', @l_error,
            @l_ePos,
            N'ai_typeId', @ai_typeId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_TVersionProc$deleteType
