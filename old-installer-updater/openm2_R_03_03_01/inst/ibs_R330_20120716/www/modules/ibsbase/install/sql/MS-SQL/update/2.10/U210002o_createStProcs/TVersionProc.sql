/******************************************************************************
 * All stored procedures regarding the tVersion table. <BR>
 * 
 * @version     2.21.0010, 020508 KR
 *
 * @author      Klaus Reimüller (KR)  980528
 ******************************************************************************
 */


/******************************************************************************
 * Create a new type version. <BR>
 * This procedure contains a dynamic TRANSACTION block, so it is allowed to
 * call it from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_typeId           Type for which a new version shall be created.
 * @param   ai_code             Code of the type.
 * @param   ai_className        Name of java class (incl. packages) which
 *                              implements the business logic of the new type
 *                              version.
 *
 * @output parameters:
 * @return  ao_id               id of the newly created tVersion.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersion$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersion$new
(
    -- input parameters:
    @ai_typeId              TYPEID,
    @ai_code                NAME,
    @ai_className           NAME,
    -- output parameters:
    @ao_id                  TVERSIONID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_noTVersionId         TVERSIONID,     -- no version id

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_retValue             INT,            -- return value of this function
    @l_superTVersionId      TVERSIONID      -- id of actual version of super
                                            -- type

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_noTVersionId         = 0x00000000

    -- initialize local variables:
SELECT
    @l_error                = 0,
    @l_retValue             = @c_ALL_RIGHT

-- body:
    BEGIN TRANSACTION                   -- begin new TRANSACTION
    SAVE TRANSACTION s_TVersion$new     -- set save point for transaction
        -- get the id of the actual version of the super type
        -- (if there exists a super type):
        SELECT  @l_superTVersionId = COALESCE (st.actVersion, @c_noTVersionId)
        FROM    ibs_Type t LEFT OUTER JOIN ibs_Type st ON st.id = t.superTypeId
        WHERE   t.id = @ai_typeId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'SELECT superTVersionId', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- store the tVersion's data in the table:
        -- within this step the following computations are done:
        -- + the state is set to active
        -- + the code is computed
        -- + the idProperty is initialized
        -- + the nextObjectSeq is initialized
        INSERT INTO ibs_TVersion
                (state, typeId, idProperty, superTVersionId,
                className, nextObjectSeq)
        VALUES  (2, @ai_typeId, 0x0, @l_superTVersionId,
                @ai_className, 1)

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'INSERT', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- get the newly created id:
        SELECT  @ao_id = MAX (id)
        FROM    ibs_TVersion
        WHERE   typeId = @ai_typeId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'SELECT ao_id', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- set the code:
        UPDATE  ibs_TVersion
        SET     code = @ai_code + '_' +
                CONVERT (VARCHAR (9), FLOOR (tVersionSeq/10)) +
                CONVERT (VARCHAR (1), tVersionSeq - 10 * FLOOR (tVersionSeq/10))
        WHERE   id = @ao_id

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'set code', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        IF (@l_superTVersionId <> @c_noTVersionId) -- super tVersion exists?
        BEGIN
            -- inherit tVersionProc entries from super tVersion:
            EXEC @l_retValue =
                    p_TVersionProc$inherit @l_superTVersionId, @ao_id
            -- inherit ConsistsOf entries from super tVersion:
            EXEC @l_retValue =
                    p_ConsistsOf$inherit @l_superTVersionId, @ao_id
        END -- if super tVersion exists

    -- check if there occurred an error:
    IF (@l_retValue <> @c_ALL_RIGHT)    -- an error occured
        -- roll back to the save point:
        ROLLBACK TRANSACTION s_TVersion$new -- undo changes

    -- finish the transaction:
    COMMIT TRANSACTION                  -- make changes permanent

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_TVersion$new -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersion$new', @l_error, @l_ePos,
            N'ai_typeId', @ai_typeId,
            N'ai_code', @ai_code,
            N'ai_className', @ai_className,
            N'ao_id', @ao_id
    -- finish the transaction:
    COMMIT TRANSACTION                  -- make changes permanent
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_TVersion$new


/******************************************************************************
 * Add some tabs to a type. <BR>
 * The tabs for the types are defined. There can be up to 10 tabs defined
 * for each type.
 * In the SQL Server version the tab parameters are optional. <BR>
 * If the code of the defaultTab is not defined or it is not valid the tab
 * with the highest priority is set as default tab. <BR>
 * This procedure contains a dynamic TRANSACTION block, so it is allowed to
 * call it from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       The id of the type version for which to add the
 *                              tabs.
 * @param   ai_defaultTab       The code of the default tab.
 * @param   ai_tabCodeX         The code of the tab, i.e. the unique name.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersion$addTabs'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersion$addTabs
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID,
    @ai_defaultTab          NAME = NULL,
    @ai_tabCode1            NAME = NULL,
    @ai_tabCode2            NAME = NULL,
    @ai_tabCode3            NAME = NULL,
    @ai_tabCode4            NAME = NULL,
    @ai_tabCode5            NAME = NULL,
    @ai_tabCode6            NAME = NULL,
    @ai_tabCode7            NAME = NULL,
    @ai_tabCode8            NAME = NULL,
    @ai_tabCode9            NAME = NULL,
    @ai_tabCode10           NAME = NULL
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_id                   INT             -- the actual id

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_retValue             = @c_ALL_RIGHT

-- body:
    BEGIN TRANSACTION                   -- begin new TRANSACTION
    SAVE TRANSACTION s_TVersion$addTabs -- set save point for transaction
/* ################
    KR:
    It is not allowed to delete the currently existing tabs because this
    procedure is used for incremental defining of tabs.
        -- drop all existing tabs:
        EXEC p_ConsistsOf$deleteTVersion @ai_tVersionId
################ */

        -- create the tabs:
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode1 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode1, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode2 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode2, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode3 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode3, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode4 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode4, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode5 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode5, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode6 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode6, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode7 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode7, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode8 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode8, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode9 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode9, @l_id OUTPUT
        END -- if
        IF ((@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
            AND @ai_tabCode10 IS NOT NULL)
        BEGIN
            EXEC @l_retValue = p_ConsistsOf$newCode
                @ai_tVersionId, @ai_tabCode10, @l_id OUTPUT
        END -- if

        -- check for error:
        IF (@l_retValue = @c_ALREADY_EXISTS) -- no severe error?
        BEGIN
            -- everything o.k..
            SELECT  @l_retValue = @c_ALL_RIGHT
        END -- if no severe error

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT)     -- everything o.k.?
        BEGIN
            -- get the data of the default tab:
            SELECT  @l_id = 0               -- initializiation
            SELECT  @l_id = COALESCE (c.id, 0)
            FROM    ibs_ConsistsOf c, ibs_Tab t
            WHERE   t.code = @ai_defaultTab
                AND t.id = c.tabId
                AND c.tVersionId = @ai_tVersionId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                N'get data of default tab', @l_ePos OUTPUT, @l_rowCount OUTPUT
            IF (@l_error <> 0)              -- an error occurred?
                GOTO exception              -- call common exception handler

            -- check if the tab was found:
            IF (@l_rowCount <= 0)           -- the was not found?
            BEGIN
                -- get the tab with the highest priority:
                SELECT  @l_id = COALESCE (MAX (id), 0)
                FROM    ibs_ConsistsOf
                WHERE   tVersionId = @ai_tVersionId
                    AND priority >=
                        (
                            SELECT  MAX (priority)
                            FROM    ibs_ConsistsOf
                            WHERE   tVersionId = @ai_tVersionId
                        )

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                    N'get tab with highest priority',
                    @l_ePos OUTPUT, @l_rowCount OUTPUT
                IF (@l_error <> 0 OR @l_rowCount <= 0) -- an error occurred?
                    GOTO exception          -- call common exception handler
            END -- if the was not found

            -- set the active tab:
            UPDATE  ibs_TVersion
            SET     defaultTab = @l_id
            WHERE   id = @ai_tVersionId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                    N'set the default tab', @l_ePos OUTPUT
            IF (@l_error <> 0)              -- an error occurred?
                GOTO exception              -- call common exception handler
        END -- if everything o.k.

    -- check if there occurred an error:
    IF (@l_retValue <> @c_ALL_RIGHT)    -- an error occured
        -- roll back to the save point:
        ROLLBACK TRANSACTION s_TVersion$addTabs -- undo changes

    -- finish the transaction:
    COMMIT TRANSACTION                  -- make changes permanent

    -- terminate the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_TVersion$addTabs -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_TVersion$addTabs', @l_error, @l_ePos,
            N'ai_tVersionId', @ai_tVersionId,
            N'ai_defaultTab', @ai_defaultTab,
            N'', 0,
            N'ai_tabCode1', @ai_tabCode1,
            N'', 0,
            N'ai_tabCode2', @ai_tabCode2,
            N'', 0,
            N'ai_tabCode3', @ai_tabCode3,
            N'', 0,
            N'ai_tabCode4', @ai_tabCode4,
            N'', 0,
            N'ai_tabCode5', @ai_tabCode5,
            N'', 0,
            N'ai_tabCode6', @ai_tabCode6,
            N'', 0,
            N'ai_tabCode7', @ai_tabCode7,
            N'', 0,
            N'ai_tabCode8', @ai_tabCode8,
            N'', 0,
            N'ai_tabCode9', @ai_tabCode9
    -- finish the transaction:
    COMMIT TRANSACTION                  -- make changes permanent
GO
-- p_TVersion$addTabs


/******************************************************************************
 * Delete all type-versions of given type, including any entries in
 * ibs_ConsistsOf regarding this tVersion. <BR>
 *
 * IMPORTANT: should ONLY be called from p_Type$deletePhysical
 *
 * @input parameters:
 * @param   ai_typeId              Type for which all type versions shall be
 *                                 deleted.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  ALREADY_EXISTS          Found objects with this type(version)
 *                          in ibs_Object - deletion not possible
 */
-- delete existing procedure:
EXEC p_dropProc N'p_TVersion$deletePhysical'
GO

-- create the new procedure:
CREATE PROCEDURE p_TVersion$deletePhysical
(
    -- input parameters:
    @ai_typeid              TYPEID
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_rowCount             INT

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1, 
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- check if there exist objects with tversionid(s) 
    -- of given code in ibs_Object
    SELECT  @l_rowCount = o.oid
    FROM    ibs_Object o, ibs_tVersion t
    WHERE   t.typeId = @ai_typeId
        AND t.id = o.tVersionId
        AND o.state = 2

    IF (@l_rowCount > 0)                -- found tVersion id(s) in ibs_object
    BEGIN
        SELECT  @l_retValue = @c_ALREADY_EXISTS
    END
    ELSE                                -- no active objects exist with given
                                        -- tVersion id(s)
    BEGIN
        -- delete all consistOf entries of given tVersion:
        EXEC @l_retValue = p_ConsistsOf$deleteType @ai_typeId

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything o.k.?
        BEGIN
            -- delete the tVersionProc entries for the tVersions of the type:
            EXEC @l_retValue = p_TVersionProc$deleteType @ai_typeId

            -- check if there occurred an error:
            IF (@l_retValue = @c_ALL_RIGHT) -- everything o.k.?
            BEGIN
                -- delete all entries with given type:
                DELETE  ibs_TVersion
                WHERE   typeId = @ai_typeId
            END -- if everything o.k.
        END -- if everything o.k.
    END -- if no active objects

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_TVersion$deletePhysical
