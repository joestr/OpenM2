/******************************************************************************
 * All stored procedures regarding the tab table. <BR>
 * 
 * @version     2.2.1.0008, 19.03.2002 KR
 *
 * @author      Klaus Reimüller (KR)  010202
 ******************************************************************************
 */


/******************************************************************************
 * Create a new tab. <BR>
 * If a tab with the same domainId (or in domain 0) and the specified code
 * exists already nothing is done and c_ALREADY_EXISTS is returned.
 *
 * @input parameters:
 * @param   ai_domainId         Id of domain in which the tab is valid.
 *                              0 ... tab is valid in all domains
 * @param   ai_code             The tab code. This code is unique within each
 *                              domain.
 * @param   ai_kind             Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
 * @param   ai_tVersionId       tVersionId of object which is representing the
 *                              tab (just for tabs of type OBJECT).
 * @param   ai_fct              Function to be performed when tab is selected.
 * @param   ai_priority         Priority of the tab (+oo ... -oo).
 * @param   ai_multilangKey     The code of the tab in the multilang table.
 * @param   ai_rights           Necessary permissions for the tab to be
 *                              displayed.
 *                              0 ... no rights necessary
 * @param   ai_class            the class to show viewtab
 *
 * @output parameters:
 * @param   ao_id               Id of the newly generated tab.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A tab with this code already exists.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Tab$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_Tab$new
(
    -- input parameters:
    @ai_domainId            DOMAINID,
    @ai_code                NAME,
    @ai_kind                INT,
    @ai_tVersionId          TVERSIONID,
    @ai_fct                 INT,
    @ai_priority            INT,
    @ai_multilangKey        NAME,
    @ai_rights              RIGHTS,
    @ai_class               DESCRIPTION,
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
    @l_kind                 INT             -- kind of the tab

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_kind = @ai_kind

--body:
    -- check if the tab exists already:
    SELECT  @ao_id = MAX (id)
    FROM    ibs_Tab
    WHERE   (   @ai_domainId = 0        -- domain independent tab
            AND code = @ai_code
            )
        OR  (   @ai_domainId <> 0       -- domain restricted tab
            AND domainId = @ai_domainId
            AND code = @ai_code
            )

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'check if tab exists already', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call exception handler

    -- check if the tab was found:
    IF (@l_rowCount <= 0 OR @ao_id IS NULL) -- tab was not found?
    BEGIN
        -- the tab may be inserted
        BEGIN TRANSACTION               -- start the transaction
            -- add the tab to the table:
            INSERT INTO ibs_Tab
                    (domainId, code, kind, tVersionId, fct,
                    priority, multilangKey, rights, class)
            VALUES (@ai_domainId, @ai_code, @l_kind, @ai_tVersionId, @ai_fct,
                    @ai_priority, @ai_multilangKey, @ai_rights, @ai_class)
            
            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'insert tab into ibs_Tab', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- get the new tab id:
            SELECT  @ao_id = MAX (id)
            FROM    ibs_Tab
            WHERE   domainId = @ai_domainId
                AND code = @ai_code

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'get new tab id', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
            COMMIT TRANSACTION          -- make changes permanent
        ELSE                            -- an error occured
            ROLLBACK TRANSACTION        -- undo changes
    END -- if tab was not found
    ELSE                                -- the tab exists already
    BEGIN
        -- set return value:
        SELECT  @l_retValue = @c_ALREADY_EXISTS
    END -- else the tab exists already

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Tab$new', @l_error, @l_ePos,
            N'ai_domainId', @ai_domainId,
            N'ai_code', @ai_code,
            N'ai_kind', @ai_kind,
            N'ai_multilangKey', @ai_multilangKey,
            N'ai_tVersionId', @ai_tVersionId,
            N'', N'',
            N'ai_fct', @ai_fct,
            N'', N'',
            N'ai_priority', @ai_priority,
            N'', N'',
            N'ai_rights', @ai_rights,
            N'', N'',
            N'ao_id', @ao_id
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Tab$new


/******************************************************************************
 * Set the attributes of an existing tab. <BR>
 *
 * @input parameters:
 * @param   ai_domainId         Id of domain in which the tab shall be changed.
 *                              0 ... tab is valid in all domains
 * @param   ai_code             The tab code. This code is unique within each
 *                              domain.
 * @param   ai_kind             New Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
 * @param   ai_tVersionId       New tVersionId of object which is representing
 *                              the tab (just for tabs of type OBJECT).
 * @param   ai_fct              New Function to be performed when tab is
 *                              selected.
 * @param   ai_priority         New Priority of the tab (+oo ... -oo).
 * @param   ai_multilangKey     New code of the tab in the multilang table.
 * @param   ai_rights           New permissions for the tab to be
 *                              displayed.
 *                              0 ... no rights necessary
 * @param   ai_class            class to show view tab
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_OBJECTNOTFOUND        The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Tab$set'
GO

-- create the new procedure:
CREATE PROCEDURE p_Tab$set
(
    -- input parameters:
    @ai_domainId            DOMAINID,
    @ai_code                NAME,
    @ai_kind                INT,
    @ai_tVersionId          TVERSIONID,
    @ai_fct                 INT,
    @ai_priority            INT,
    @ai_multilangKey        NAME,
    @ai_rights              RIGHTS,
    @ai_class               DESCRIPTION
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_id                   ID              -- the id of the tab

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

--body:
    -- check if the tab exists already:
    SELECT  @l_id = MAX (id)
    FROM    ibs_Tab
    WHERE   domainId = @ai_domainId
        AND code = @ai_code

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'check if tab exists already', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call exception handler

    -- check if the tab was found:
    IF (@l_rowCount <= 0 OR @l_id IS NULL) -- tab was not found?
    BEGIN
        -- set return value:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- if tab was not found
    ELSE                                -- the tab exists already
    BEGIN
        -- the tab may be inserted
        BEGIN TRANSACTION               -- start the transaction
            -- change the tab data:
            UPDATE  ibs_Tab
            SET     kind = @ai_kind,
                    tVersionId = @ai_tVersionId,
                    fct = @ai_fct,
                    priority = @ai_priority,
                    multilangKey = @ai_multilangKey,
                    rights = @ai_rights,
                    class = @ai_class
            WHERE   id = @l_id
            
            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'change tab data', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
            COMMIT TRANSACTION          -- make changes permanent
        ELSE                            -- an error occured
            ROLLBACK TRANSACTION        -- undo changes
    END -- else the tab exists already

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Tab$set', @l_error, @l_ePos,
            N'ai_domainId', @ai_domainId,
            N'ai_code', @ai_code,
            N'ai_kind', @ai_kind,
            N'ai_multilangKey', @ai_multilangKey,
            N'ai_tVersionId', @ai_tVersionId,
            N'', N'',
            N'ai_fct', @ai_fct,
            N'', N'',
            N'ai_priority', @ai_priority,
            N'', N'',
            N'ai_rights', @ai_rights
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Tab$set


/******************************************************************************
 * Get the data from a given tab. <BR>
 * This procedure gets a tuple out of ibs_Tab by using the domainId and the
 * code together as unique key.
 * If there is no tuple found the return value is c_OBJECTNOTFOUND.
 *
 * @input parameters:
 * @param   ai_domainId         Id of domain in which the tab shall be changed.
 *                              0 ... tab is valid in all domains
 * @param   ai_code             The tab code. This code is unique within each
 *                              domain.
 *
 * @output parameters:
 * @param   ao_id               The id of the tab
 * @param   ao_kind             Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
 * @param   ao_tVersionId       tVersionId of object which is representing
 *                              the tab (just for tabs of type OBJECT).
 * @param   ao_fct              Function to be performed when tab is
 *                              selected.
 * @param   ao_priority         Priority of the tab (+oo ... -oo).
 * @param   ao_multilangKey     Code of the tab in the multilang table.
 * @param   ao_rights           Permissions for the tab to be displayed.
 *                              0 ... no rights necessary
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_OBJECTNOTFOUND        The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Tab$get'
GO

-- create the new procedure:
CREATE PROCEDURE p_Tab$get
(
    -- input parameters:
    @ai_domainId            DOMAINID,
    @ai_code                NAME,
    -- output parameters:
    @ao_id                  ID OUTPUT,
    @ao_kind                INT OUTPUT,
    @ao_tVersionId          TVERSIONID OUTPUT,
    @ao_fct                 INT OUTPUT,
    @ao_priority            INT OUTPUT,
    @ao_multilangKey        NAME OUTPUT,
    @ao_rights              RIGHTS OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

--body:
    -- check if the tab exists:
    -- get the tab data:
    SELECT  @ao_id = id, @ao_kind = kind, @ao_tVersionId = tVersionId,
            @ao_fct = fct, @ao_priority = priority,
            @ao_multilangKey = multilangKey, @ao_rights = rights
    FROM    ibs_Tab
    WHERE   domainId = @ai_domainId
        AND code = @ai_code

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'check if tab exists', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- check if the tab was found:
    IF (@l_rowCount <= 0 OR @ao_id IS NULL) -- tab was not found?
    BEGIN
        -- set return value:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- if tab was not found

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Tab$get', @l_error, @l_ePos,
            N'ai_domainId', @ai_domainId,
            N'ai_code', @ai_code,
            N'ao_id', @ao_id,
            N'ao_multilangKey', @ao_multilangKey,
            N'ao_kind', @ao_kind,
            N'', N'',
            N'ao_tVersionId', @ao_tVersionId,
            N'', N'',
            N'ao_fct', @ao_fct,
            N'', N'',
            N'ao_priority', @ao_priority,
            N'', N'',
            N'ao_rights', @ao_rights
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Tab$get


/******************************************************************************
 * Get the code from a given taboid. <BR>
 * This procedure gets a tuple out of ibs_Tab by using the taboid at ibs_object.
 *
 * If there is no tuple found the return value is c_OBJECTNOTFOUND.
 *
 * @input parameters:
 * @param   ai_tabOid           Oid of the tab to get the typecode
 * 
 *
 * @output parameters:
 * @param   ao_tabCode          code of the tab
 * 
 * 
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_OBJECTNOTFOUND        The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Tab$getCodeFromOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_Tab$getCodeFromOid
(
    -- input parameters:
    @ai_tabOid              OBJECTIDSTRING,
    -- output parameters:
    @ao_tabCode             NAME OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_tabOid               OBJECTID        -- tabOid to hold the oid 

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

--body:
    -- conversions (objectidstring) - all input objectids must be converted
    EXEC p_stringToByte @ai_tabOid, @l_tabOid OUTPUT
    
    -- get the code
	SELECT  @ao_tabCode = code
	FROM    ibs_Tab t, ibs_ConsistsOf c, ibs_Object o
	WHERE   t.id = c.tabId
	    AND c.id = o.consistsOfId
	    AND o.oid = @l_tabOid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'check if tab exists', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- check if the tab was found:
    IF (@l_rowCount <= 0 OR @ao_tabCode LIKE '') -- tab was not found?
    BEGIN
        -- set return value:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- if tab was not found

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Tab$getCodeFromOid', @l_error, @l_ePos,
            N'ai_tabOid', @ai_tabOid,
            N'ao_tabCode', @ao_tabCode
            
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Tab$getCodeFromOid
