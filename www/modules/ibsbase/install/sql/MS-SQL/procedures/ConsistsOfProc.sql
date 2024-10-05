/******************************************************************************
 * All stored procedures regarding the ConsistsOf table. <BR>
 * 
 * @version     1.10.0001, 02.08.1999
 *
 * @author      Klaus Reimüller (KR)  980621
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
EXEC p_dropProc N'p_ConsistsOf$inherit'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$inherit
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
    @l_rowCount             INT,            -- row counter
    @l_retValue             INT,            -- return value of this function
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the minor
                                            -- tVersion
    @l_inheritedFrom        TVERSIONID      -- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries

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
    SAVE TRANSACTION s_ConsistsOf$inherit

    -- get the data of the tVersion to which to inherit the tuples:
    SELECT  @l_posNoPath = MIN (tv.posNoPath),
            @l_inheritedFrom = MIN (c.inheritedFrom)
    FROM    ibs_TVersion tv, ibs_ConsistsOf c
    WHERE   tv.id = @ai_minorTVersionId
        AND c.tVersionId = @ai_minorTVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get minor tVersion data', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0 OR @l_rowCount = 0) -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- delete the values for the minor tVersion and all
    -- tVersions below which inherit their values from the same
    -- TVersion as that tVersion:
    DELETE  ibs_ConsistsOf
    WHERE   tVersionId IN
            (
                SELECT  id
                FROM    ibs_TVersion
                WHERE   id = @ai_minorTVersionId
                    OR  posNoPath LIKE @l_posNoPath + '%'
            )
        AND inheritedFrom = @l_inheritedFrom

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'delete for act tVersion and tVersions below', @l_ePos OUTPUT
    IF (@l_error <> 0)          -- an error occurred?
        GOTO exception          -- call exception handler

    -- add the records to the minor tVersion and all tVersions
    -- below which before inherited from the same tVersion as
    -- the minor tVersion:
    INSERT INTO ibs_ConsistsOf
            (id, tVersionId, tabId, priority, rights, inheritedFrom)
    SELECT  -((tv.id & 0xFFFFF) | (c.tabId & 0xFFF) * 0x100000),
            tv.id, c.tabId, c.priority, c.rights,
            c.inheritedFrom
    FROM    ibs_ConsistsOf c, ibs_TVersion tv
    WHERE   (   tv.id = @ai_minorTVersionId
            OR  tv.posNoPath LIKE @l_posNoPath + '%'
            )
        AND tv.id NOT IN
            (
                SELECT  tVersionId
                FROM    ibs_ConsistsOf
            )
        AND c.tVersionId = @ai_majorTVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'insert for act tVersion and tVersions below', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_ConsistsOf$inherit
    -- log the error:
    EXEC ibs_error.logError 500, N'p_ConsistsOf$inherit', @l_error, @l_ePos,
            N'ai_majorTVersionId', @ai_majorTVersionId,
            N'', N'',
            N'ai_minorTVersionId', @ai_minorTVersionId
    -- return the error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$inherit


/******************************************************************************
 * Add a new tab to a type version. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of type version for which to add a new tab.
 * @param   ai_code             The unique code of the tab.
 *
 * @output parameters:
 * @param   ao_id               Id of the newly generated tuple.
 * @returns A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ConsistsOf$newCode'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$newCode
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID,
    @ai_tabCode             NAME,
    -- output parameters:
    @ao_id                  ID = 0x00000000 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_tabId                ID,             -- the id of the tab
    @l_kind                 INT,            -- kind of the tab
    @l_tVersionId           TVERSIONID,     -- tVersionId of the tab
    @l_fct                  INT,            -- function of the tab
    @l_priority             INT,            -- priority of the tab
    @l_multilangKey         NAME,           -- the language key of the tab
    @l_rights               RIGHTS,         -- the necessary rights to display
                                            -- the tab
    @l_posNoPath            POSNOPATH_VC,   -- posNoPath of actual tVersion
    @l_inheritedFrom        TVERSIONID      -- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

--body:
    -- get the tab data:
    EXEC    @l_retValue = p_Tab$get 0, @ai_tabCode,
                @l_tabId OUTPUT, @l_kind OUTPUT, @l_tVersionId OUTPUT,
                @l_fct OUTPUT, @l_priority OUTPUT, @l_multilangKey OUTPUT,
                @l_rights OUTPUT

    -- check if there occurred an error:
    IF (@l_retValue = @c_ALL_RIGHT)     -- everything o.k.?
    BEGIN
        -- get the data of the actual tVersion:
        SELECT  @l_posNoPath = posNoPath
        FROM    ibs_TVersion
        WHERE   id = @ai_tVersionId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'get tVersion data', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO NonTransactionException -- call common exception handler

        -- get the existing relationship data:
        SELECT  @ao_id = id
        FROM    ibs_ConsistsOf
        WHERE   tVersionId = @ai_tVersionId
            AND inheritedFrom = tVersionId
            AND tabId = @l_tabId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            N'get relationship data', @l_ePos OUTPUT, @l_rowCount OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO NonTransactionException -- call exception handler

        -- check if the relationship already exists:
        IF (@l_rowCount > 0)            -- relationship already exists?
        BEGIN
            -- set error code:
            SELECT  @l_retValue = @c_ALREADY_EXISTS
        END -- if relationship already exists
        ELSE                            -- relationship does not exist yet
        BEGIN
            -- get the consistsOf data of the actual tVersion:
            SELECT  @l_inheritedFrom = MIN (inheritedFrom)
            FROM    ibs_ConsistsOf
            WHERE   tVersionId = @ai_tVersionId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'get tVersion consistsOf data', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO NonTransactionException -- call common exception handler

            -- at this point we know that the operation may be done
            BEGIN TRANSACTION           -- start the transaction
                -- check if the major tVersion currently has own records within
                -- the consists of table or inherits its records from another
                -- tVersion:
                IF (@l_inheritedFrom <> @ai_tVersionId)
                                        -- inherited from another tVersion?
                BEGIN
                    -- delete the entries within the consists of table which
                    -- are inherited from above the actual tVersion to one
                    -- tVersion which is below the actual tVersion or to the
                    -- actual tVersion itself:
                    DELETE  ibs_ConsistsOf
                    WHERE   tVersionId IN
                            (
                                SELECT  id
                                FROM    ibs_TVersion
                                WHERE   id = @ai_tVersionId
                                    OR  posNoPath LIKE @l_posNoPath + '%'
                            )
                        AND inheritedFrom = @l_inheritedFrom

                    -- check if there occurred an error:
                    EXEC @l_error = ibs_error.prepareError @@error,
                        N'delete inherited entries', @l_ePos OUTPUT
                    IF (@l_error <> 0)  -- an error occurred?
                        GOTO exception  -- call common exception handler
                END -- if inherited from another tVersion

                -- define cursor:
                -- get all tVersions for which to set the tab.
                DECLARE updateCursor CURSOR FOR
                    SELECT  id
                    FROM    ibs_TVersion
                    WHERE   posNoPath LIKE @l_posNoPath + '%'
                        AND (   id = @ai_tVersionId
                            OR  id NOT IN
                                (   SELECT  tVersionId
                                    FROM    ibs_ConsistsOf
                                    WHERE   tabId = @l_tabId
                                        OR  inheritedFrom <> @ai_tVersionId
                                )
                            )

                -- open the cursor:
                OPEN    updateCursor

                -- get the first object:
                FETCH NEXT FROM updateCursor INTO @l_tVersionId

                -- loop through all objects:
                WHILE (@@FETCH_STATUS <> -1)-- another object found?
                BEGIN
                    -- Because @@FETCH_STATUS may have one of the three values
                    -- -2, -1, or 0 all of these cases must be checked.
                    -- In this case the tuple is skipped if it was deleted
                    -- during the execution of this procedure.
                    IF (@@FETCH_STATUS <> -2)
                    BEGIN
                        -- insert the new records of the actual tVersion and its
                        -- sub tVersions into the consists of table:
                        INSERT INTO ibs_ConsistsOf
                                (tVersionId, tabId, priority, rights,
                                inheritedFrom)
                        VALUES (@l_tVersionId, @l_tabId, @l_priority, @l_rights,
                                @ai_tVersionId)

                        -- check if there occurred an error:
                        EXEC @l_error = ibs_error.prepareError @@error,
                            N'insert records', @l_ePos OUTPUT
                        IF (@l_error <> 0) -- an error occurred?
                            GOTO CursorException -- call exception handler
                    END -- if

                    -- get next object:
                    FETCH NEXT FROM updateCursor INTO @l_tVersionId
                END -- while another object found

                -- close the not longer needed cursor:
                CLOSE updateCursor
                DEALLOCATE updateCursor

            -- finish the transaction:
            COMMIT TRANSACTION

            -- get the existing relationship data:
            SELECT  @ao_id = id
            FROM    ibs_ConsistsOf
            WHERE   tVersionId = @ai_tVersionId
                AND inheritedFrom = tVersionId
                AND tabId = @l_tabId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'get relationship data2', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO NonTransactionException -- call exception handler
        END -- if relationship does not exist yet
    END -- if everything o.k.

    -- return the state value:
    RETURN  @l_retValue

cursorException:                        -- an error occurred within cursor
    -- close the not longer needed cursor:
    CLOSE updateCursor
    DEALLOCATE updateCursor
exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_ConsistsOf$newCode', @l_error, @l_ePos,
            N'ai_tVersionId', @ai_tVersionId,
            N'ai_tabCode', @ai_tabCode,
            N'l_tVersionId', @l_tVersionId,
            N'', N'',
            N'ao_id', @ao_id
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$newCode


/******************************************************************************
 * Ensure that a specific tab exists. <BR>
 *
 * @input parameters:
 * @param   ai_code             The unique code of the tab.
 * @param   ai_tVersionId       Id of type version if tab shall be an object.
 * @param   ai_description      Description for the created tab.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ConsistsOf$ensureTabExists'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$ensureTabExists
(
    -- input parameters:
    @ai_code                NAME,
    @ai_tVersionId          TVERSIONID,
    @ai_description         DESCRIPTION
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_TK_VIEW              INT,            -- tab kind VIEW
    @c_TK_OBJECT            INT,            -- tab kind OBJECT
    @c_TK_LINK              INT,            -- tab kind LINK
    @c_TK_FUNCTION          INT,            -- tab kind FUNCTION
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_tabId                ID,             -- the id of the tab
    @l_kind                 INT,            -- kind of the tab
    @l_tVersionId           TVERSIONID,     -- tVersionId of the tab
    @l_fct                  INT,            -- function of the tab
    @l_priority             INT,            -- priority of the tab
    @l_multilangKey         NAME,           -- the language key of the tab
    @l_rights               RIGHTS          -- the necessary rights to display
                                            -- the tab

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21,
    @c_TK_VIEW              = 1,
    @c_TK_OBJECT            = 2,
    @c_TK_LINK              = 3,
    @c_TK_FUNCTION          = 4,
    @c_languageId           = 0


    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

--body:
    -- get the tab data:
    EXEC @l_retValue = p_Tab$get 0, @ai_code,
            @l_tabId OUTPUT, @l_kind OUTPUT, @l_tVersionId OUTPUT,
            @l_fct OUTPUT, @l_priority OUTPUT, @l_multilangKey OUTPUT,
            @l_rights OUTPUT

    -- check if the tab was found:
    IF (@l_retValue <> @c_ALL_RIGHT)    -- tab was not found?
    BEGIN
        -- compute the several values:
        IF (@ai_tVersionId <> 0)        -- tab is an own object?
        BEGIN
            SELECT  @l_kind = @c_TK_OBJECT
        END -- if tab is an own object
        ELSE                            -- tab is just a view
        BEGIN
            SELECT  @l_kind = @c_TK_VIEW
        END -- else tab is just a view

        -- add the tab:
        EXEC    @l_retValue = p_Tab$new 0, @ai_code, @l_kind,
                    @ai_tVersionId, 51, 0, @l_multilangKey, 0,
                    @l_tabId OUTPUT

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything o.k.?
        BEGIN
            -- get the tab data:
            EXEC @l_retValue = p_Tab$get 0, @ai_code,
                    @l_tabId OUTPUT, @l_kind OUTPUT, @l_tVersionId OUTPUT,
                    @l_fct OUTPUT, @l_priority OUTPUT, @l_multilangKey OUTPUT,
                    @l_rights OUTPUT

            -- update description for consistency between
            -- language tables and tab table:
            EXEC p_ObjectDesc_01$new
                @c_languageId, @l_multilangKey, @ai_code,
                @ai_description, ''
        END -- if everything o.k.
    END -- if tab was not found

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_ConsistsOf$ensureTabExists',
            @l_error, @l_ePos,
            N'ai_tVersionId', @ai_tVersionId,
            N'ai_code', @ai_code
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$ensureTabExists


/******************************************************************************
 * Create a new entry within ibs_ConsistsOf <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_majorTVersionId  Id of majorType for which to define a minorType.
 * @param   ai_minorTVersionId  Id of minorType belonging to majorType.
 * @param   ai_name             Name of the object to be created of the 
 *                              minorType.
 * @param   ai_description      Description for the created object.
 *
 * @output parameters:
 * @param   ao_id               Id of the newly generated tuple.
 * @returns A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         An entry for this tab already exists.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ConsistsOf$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$new
(
    -- input parameters:
    @ai_majorTVersionId     TVERSIONID,
    @ai_minorTVersionId     TVERSIONID,
    @ai_name                NAME,
    @ai_description         DESCRIPTION,
    -- output parameters:
    @ao_id                  ID = 0x00000000 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_languageId           INT,            -- the actual language

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21,
    @c_languageId           = 0

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

--body:
    -- get the tab data:
    EXEC @l_retValue =
            p_ConsistsOf$ensureTabExists @ai_minorTVersionId, @ai_name

    -- call common procedure for creating a tVersion/tab relationship:
    EXEC @l_retValue =
            p_ConsistsOf$newCode @ai_majorTVersionId, @ai_name, @ao_id OUTPUT

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_ConsistsOf$new', @l_error, @l_ePos,
            N'ai_majorTVersionId', @ai_majorTVersionId,
            N'ai_name', @ai_name,
            N'ai_minorTVersionId', @ai_minorTVersionId,
            N'ai_description', @ai_description,
            N'ao_id', @ao_id
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$new


/******************************************************************************
 * Delete a tab from a tVersion. <BR>
 * If this is the last tab defined for this tVersion and
 * inheritFromUpper is set to 1, the tVersion (and its sub tVersions)
 * automatically inherits the records from its super tVersion.
 * If the required tuple is not found this is no severe error. So the second
 * operation of inheriting from the super tVersion is also done in the same way.
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion for which a procedure
 *                              shall be deleted.
 * @param   ai_tabCode          Unique code of the tab to be deleted.
 * @param   ai_inheritFromSuper In case that there are no more records for
 *                              the tVersion after deleting the requested
 *                              record this parameter tells whether the tVersion
 *                              shall inherit the records from its super
 *                              tVersion or not.
 *                              Default: 1 (= true)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 * c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ConsistsOf$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$delete
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID,
    @ai_tabCode             NAME,
    @ai_inheritFromSuper    BOOL = 1
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
    @l_rowCount             INT,            -- row counter
    @l_tabId                ID,             -- the id of the tab
    @l_kind                 INT,            -- kind of the tab
    @l_tVersionId           TVERSIONID,     -- tVersionId of the tab
    @l_fct                  INT,            -- function of the tab
    @l_priority             INT,            -- priority of the tab
    @l_multilangKey         NAME,           -- the language key of the tab
    @l_rights               RIGHTS,         -- the necessary rights to display
                                            -- the tab
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the tVersion
    @l_inheritedFrom        TVERSIONID,     -- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries
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
    -- get the tab data:
    EXEC    @l_retValue = p_Tab$get 0, @ai_tabCode,
                @l_tabId OUTPUT, @l_kind OUTPUT, @l_tVersionId OUTPUT,
                @l_fct OUTPUT, @l_priority OUTPUT, @l_multilangKey OUTPUT,
                @l_rights OUTPUT

    -- check if there occurred an error:
    IF (@l_retValue = @c_ALL_RIGHT)     -- everything o.k.?
    BEGIN
        -- get the data of the actual tVersion:
        SELECT  @l_posNoPath = tv.posNoPath,
                @l_superTVersionId = tv.superTVersionId,
                @l_inheritedFrom = c.inheritedFrom
        FROM    ibs_TVersion tv, ibs_ConsistsOf c
        WHERE   tv.id = @ai_TVersionId
            AND c.tVersionId = @ai_TVersionId
            AND c.tabId = @l_tabId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            N'get data of actual tVersion', @l_ePos OUTPUT, @l_rowCount OUTPUT
        IF (@l_error <> 0 OR @l_rowCount <= 0) -- an error occurred?
            GOTO NonTransactionException    -- call exception handler

        -- check if the type version currently has own records within the
        -- consists of table or inherits its records from another tVersion:
        IF (@l_inheritedFrom = @ai_tVersionId)
                                        -- not inherited from another tVersion?
        BEGIN
            -- at this point we know that the operation may be done
            BEGIN TRANSACTION           -- start transaction
                -- delete the record in the tVersion itself and all inherited
                -- ones in the sub tVersions:
                DELETE  ibs_ConsistsOf
                WHERE   (   tVersionId = @ai_tVersionId
                        OR  inheritedFrom = @ai_tVersionId
                        )
                    AND tabId = @l_tabId

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'delete', @l_ePos OUTPUT
                IF (@l_error <> 0)  -- an error occurred?
                    GOTO exception  -- call common exception handler

                -- check if there are any records for the actual tVersion left:
                IF NOT EXISTS (
                    SELECT  tVersionId
                    FROM    ibs_ConsistsOf
                    WHERE   tVersionId = @ai_tVersionId
                )                           -- no record left for this tVersion?
                BEGIN
                    -- check if the tVersion shall inherit from the super
                    -- tVersion:
                    IF (@ai_inheritFromSuper = 1 AND @l_superTVersionId <> 0)
                                            -- inherit from super tVersion?
                    BEGIN
                        -- inherit the entries from the super tVersion:
                        EXEC @l_retValue = p_ConsistsOf$inherit
                                @l_superTVersionId, @ai_tVersionId
                    END -- if inherit from super tVersion
                END -- if no record left for this tVersion

            -- finish the transaction:
            IF (@l_retValue <> @c_ALL_RIGHT AND
                @l_retValue <> @c_OBJECTNOTFOUND)
                                            -- there occurred a severe error?
                ROLLBACK TRANSACTION        -- undo changes
            ELSE                            -- there occurred no error
                COMMIT TRANSACTION          -- make changes permanent
        END -- if not inherited from another tVersion
    END -- if everything o.k.

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_ConsistsOf$delete', @l_error, @l_ePos,
            N'ai_tVersionId', @ai_tVersionId,
            N'ai_tabCode', @ai_tabCode,
            N'ai_inheritFromSuper', @ai_inheritFromSuper
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$delete


/******************************************************************************
 * Delete all occurrences of a code out of the ConsistsOf table. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tabCode          The code of the tab to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ConsistsOf$deleteCode'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$deleteCode
(
    -- input parameters:
    @ai_tabCode             NAME
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
        -- delete the entries of the tab from the ConsistsOf table:
        DELETE  ibs_ConsistsOf
        WHERE   tabId IN
                (SELECT id
                FROM    ibs_Tab
                WHERE   code = @ai_tabCode
                )

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
    EXEC ibs_error.logError 500, N'p_ConsistsOf$deleteCode', @l_error,
            @l_ePos,
            N'', 0,
            N'ai_tabCode', @ai_tabCode
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$deleteCode


/******************************************************************************
 * Delete all occurrences of a specific tVersion out of the ConsistsOf table.
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
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ConsistsOf$deleteTVersion'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$deleteTVersion
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
    @l_rowCount             INT,            -- row counter
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
    SAVE TRANSACTION s_ConsistsOf$deleteTVersion

    -- get the data of the tVersion:
    SELECT  @l_superTVersionId = superTVersionId
    FROM    ibs_TVersion
    WHERE   id = @ai_tVersionId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get data of tVersion', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- check if the super tVersion id was found:
    IF (@l_rowCount > 0)                -- super tVersion was found
    BEGIN
        -- inherit all entries from the super tVersion:
        -- the consequence of this action is, that no sub tVersion will have
        -- inherited values from this tVersion
        EXEC @l_retValue =
            p_ConsistsOf$inherit @l_superTVersionId, @ai_tVersionId

        -- check if there was an error:
        IF (@l_retValue <> @c_ALL_RIGHT) -- an error occurred?
        BEGIN
            GOTO exception              -- call common exception handler
        END -- if an error occurred
    END -- if super tVersion was found
    ELSE                                -- the super tVersion was not found
    BEGIN
        -- delete the entries of the actual tVersion and all entries which were
        -- inherited from this tVersion from the consists of table:
        DELETE  ibs_ConsistsOf
        WHERE   tVersionId = @ai_tVersionId
            OR  inheritedFrom = @ai_tVersionId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'delete', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler
    END -- else the super tVersion was not found

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_ConsistsOf$deleteTVersion
    -- log the error:
    EXEC ibs_error.logError 500, N'p_ConsistsOf$deleteTVersion', @l_error,
            @l_ePos,
            N'ai_tVersionId', @ai_tVersionId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$deleteTVersion


/******************************************************************************
 * Delete all occurrences of tVersions belonging to a specific type out of the
 * ConsistsOf table. <BR>
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
EXEC p_dropProc N'p_ConsistsOf$deleteType'
GO

-- create the new procedure:
CREATE PROCEDURE p_ConsistsOf$deleteType
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
        DECLARE updateCursor CURSOR FOR
            SELECT  id
            FROM    ibs_TVersion
            WHERE   typeId = @ai_typeId

        -- open the cursor:
        OPEN    updateCursor

        -- get the first object:
        FETCH NEXT FROM updateCursor INTO @l_tVersionId

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
                EXEC @l_retValue = p_ConsistsOf$deleteTVersion @l_tVersionId
            END -- if

            -- get next object:
            FETCH NEXT FROM updateCursor INTO @l_tVersionId
        END -- while another object found

        -- close the not longer needed cursor:
        CLOSE updateCursor
        DEALLOCATE updateCursor

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
    EXEC ibs_error.logError 500, N'p_ConsistsOf$deleteType', @l_error,
            @l_ePos,
            N'ai_typeId', @ai_typeId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_ConsistsOf$deleteType
