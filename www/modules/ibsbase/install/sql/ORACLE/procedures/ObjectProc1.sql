/******************************************************************************
 * All stored procedures regarding the ibs_Object table. <BR>
 *
 * @version     $Revision: 1.51 $, $Date: 2002/12/05 19:55:11 $
 *              $Author: kreimueller $
 *
 * @author      Klaus Reimüller (KR)  980426
 ******************************************************************************
 */


/******************************************************************************
 * Get the oid of a specific tab of an object. <BR>
 * If the tab does not exist for this object or the tab itself is not an object
 * there is no oid available an OBJECTNOTFOUND ist returned.
 *
 * @input parameters:
 * @param   ai_oid              Id of the object for which to get the tab oid.
 * @param   ai_tabCode          The code of the tab (as it is in ibs_Tab).
 *
 * @output parameters:
 * @param   ao_tabOid           The oid of the tab object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The tab object was not found.
 */
CREATE OR REPLACE FUNCTION p_Object$getTabOid
(
    -- input parameters:
    ai_oid                  ibs_Object.oid%TYPE,
    ai_tabCode              ibs_Tab.code%TYPE,
    -- output parameters:
    ao_tabOid               OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists
    c_CONT_PARTOF           CONSTANT ibs_Object.containerKind%TYPE := 2;
                                            -- containerKind part of

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows


-- body:
BEGIN
    -- get the oid of the tab object:
    BEGIN
        SELECT  o.oid
        INTO    ao_tabOid
        FROM    ibs_Object o, ibs_ConsistsOf c, ibs_Tab t
        WHERE   o.containerId = ai_oid
            AND o.containerKind = c_CONT_PARTOF
            AND o.consistsOfId = c.id
            AND c.tabId = t.id
            AND t.code = ai_tabCode;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the tab object was not found
            -- set corresponding return value:
            l_retValue := c_OBJECTNOTFOUND;
        -- end when the tab object was not found
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get tab oid';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid = ' || ai_oid ||
            ', ai_tabCode = ' || ai_tabCode ||
            ', ao_tabOid = ' || ao_tabOid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$getTabOid', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$getTabOid;
/

show errors;


/******************************************************************************
 * Create a tab for an object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure ensures that a specific tab for an object exists. If the tab
 * is already there nothing is done.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the tab.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_oid              Id of the object for which the tab shall be
 *                              generated.
 * @param   ai_tVersionId       Type of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          There are no tabs to generate.
 */
CREATE OR REPLACE FUNCTION p_Object$createTab
(
    -- input parameters:
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_oid                  ibs_Object.oid%TYPE,
    ai_tabCode              ibs_Tab.code%TYPE,
    -- output parameters:
    ao_tabOid               OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;-- the object already exists
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
                                            -- no oid as string
    c_TK_OBJECT             CONSTANT ibs_Tab.kind%TYPE := 2; -- tab kind Object
    c_TK_LINK               CONSTANT ibs_Tab.kind%TYPE := 3; -- tab kind Link
    c_PROC_CREATE           CONSTANT ibs_TversionProc.code%TYPE := 'create';
                                            -- code for stored procedure which
                                            -- creates an object

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_oid_s                 VARCHAR2 (18);  -- string representation of oid
    l_tabOid_s              VARCHAR2 (18);  -- string representation of tab oid
    l_consistsOfId          ibs_ConsistsOf.id%TYPE;
                                            -- id of tab in ibs_ConsistsOf
    l_tabTVersionId         ibs_Tab.tVersionId%TYPE;
                                            -- tVersionId of the actual tab
    l_tabName               ibs_ObjectDesc_01.objName%TYPE; -- the tab's name
    l_tabDescription        ibs_ObjectDesc_01.objDesc%TYPE;
                                            -- the tab's description
    l_tabProc               ibs_TVersionProc.name%TYPE;
                                            -- name of stored procedure for
                                            -- creating the tab object
    l_state                 ibs_Object.state%TYPE;
                                            -- the state of the object where
                                            -- the tab belogns to and thus the
                                            -- state of the tab itself
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_rowsProcessed         INTEGER;        -- number of rows of last cmd exec.
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution

    -- exceptions:
    e_tabNotFound           EXCEPTION;
    e_statementExecError    EXCEPTION;

-- body:
BEGIN
    -- convert oid to string:
    p_byteToString (ai_oid, l_oid_s);

    -- get the tab data:
    l_retValue := p_Object$getTabOid (ai_oid, ai_tabCode, ao_tabOid);

    -- check if the tab already exists:
    IF (l_retValue = c_OBJECTNOTFOUND) -- tab does not exist yet?
    THEN
        -- re-initialize the return value:
        l_retValue := c_ALL_RIGHT;

        -- get the data of the tab:
        -- (recognize only known names of tabs which can be constructed by
        -- p_Object$create)
        BEGIN
            SELECT  t.tVersionId, d.objName, d.objDesc,
                    DECODE (p.name, NULL, 'p_Object$create', p.name),
                    c.id, o.state
            INTO    l_tabTVersionId, l_tabName,l_tabDescription, l_tabProc,
                    l_consistsOfId, l_state
            FROM    ibs_Object o, ibs_ConsistsOf c, ibs_Tab t,
                    ibs_ObjectDesc_01 d, ibs_TVersionProc p
            WHERE   o.oid = ai_oid
                AND c.tVersionId = o.tVersionId
                AND c.tabId = t.id
                AND t.kind IN (c_TK_OBJECT, c_TK_LINK)
                AND t.multilangKey = d.name
                AND t.tVersionId = p.tVersionId (+)
                AND c_PROC_CREATE = p.code (+)
                AND t.code = ai_tabCode;
        EXCEPTION
            WHEN OTHERS THEN -- any error
                -- create error entry:
                l_ePos := 'get tab data';
                RAISE;                  -- call common exception handler
        END;

        -- create the tab:
        l_cmdString :=
            ' DECLARE' ||
                ' l_retValue INT;' ||   -- return value of function
                ' l_tabOid_s VARCHAR2 (18);' || -- string representation of tab oid
                ' l_ePos VARCHAR2 (2000);' || -- error position description
                ' l_eText VARCHAR2 (5000);' || -- full error text
            ' BEGIN ' ||
                'l_retValue := ' || l_tabProc || ' (' ||
                    ai_userId || ', ' ||
                    ai_op || ', ' ||
                    l_tabTVersionId || ', ' ||
                    '''' || l_tabName || ''', ' ||
                    '''' || l_oid_s || ''', ' ||
                    '2, 0, ' ||
                    '''' || c_NOOID_s || ''', ' ||
                    '''' || l_tabDescription || ''', ' ||
                    'l_tabOid_s);' ||
                ' IF (l_retValue <> ' || c_ALL_RIGHT || ')' ||
                ' THEN' ||
                    -- create error entry:
                    ' l_ePos := ''create tab error'';' ||
                    -- create error entry:
                    ' l_eText := l_ePos ||' ||
                    ' ''; l_retValue = '' || l_retValue ||' ||
                    ' '', l_tabOid_s = '' || l_tabOid_s ||' ||
                    ' ''; errorcode = '' || SQLCODE ||' ||
                    ' '', errormessage = '' || SQLERRM;' ||
                    ' ibs_error.log_error (ibs_error.error,' ||
                    ' ''p_Object$createTab'', l_eText);' ||
                ' END IF;' ||
            ' END;';
        BEGIN
            -- open the cursor:
            l_cursorId := DBMS_SQL.OPEN_CURSOR;
            -- parse the statement and use the normal behavior of the
            -- database to which we are currently connected:
            DBMS_SQL.PARSE (l_cursorId, l_cmdString, DBMS_SQL.NATIVE);
            -- remember the possible error position:
            l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
            l_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);
            -- remember the possible error position:
            l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
            -- close the cursor:
            DBMS_SQL.CLOSE_CURSOR (l_cursorId);

        EXCEPTION
            WHEN OTHERS THEN
                IF (DBMS_SQL.IS_OPEN (l_cursorId))
                                        -- the cursor is currently open?
                THEN
                    -- close the cursor:
                    DBMS_SQL.CLOSE_CURSOR (l_cursorId);
                END IF; -- the cursor is currently open
                -- create error entry:
                l_ePos := 'Error in creating tab at ' || l_lastErrorPos || '.' ||
                    '(' || l_cmdString || ')';
                RAISE e_statementExecError; -- call common exception handler
        END;

        -- get the oid of the newly created tab object:
        ao_tabOid := c_NOOID;
        BEGIN
            SELECT  MAX (oid)
            INTO    ao_tabOid
            FROM    ibs_Object
            WHERE   containerId = ai_oid
                AND containerKind = 2
                AND tVersionId = l_tabTVersionId;

            -- check if the tab object was found:
            IF (ao_tabOid = c_NOOID)    -- the tab was not found?
            THEN
                -- call corresponding exception handler:
                RAISE NO_DATA_FOUND;
            END IF; -- if the tab was not found
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- the tab was not created
                -- set corresponding return value:
                l_retValue := c_NOT_OK;
            -- end when the tab was not created
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get tab data';
                RAISE;                  -- call common exception handler
        END;

        -- check if there occurred an error:
        IF (l_retValue = c_ALL_RIGHT)   -- everything o.k.?
        THEN
            -- set the tab id and state:
            BEGIN
                UPDATE  ibs_Object
                SET     consistsOfId = l_consistsOfId,
                        state = l_state
                WHERE   oid = ao_tabOid;

                -- no system error.
                -- check if there was a row updated:
                IF (SQL%ROWCOUNT <= 0)  -- no row updated
                THEN
                    -- call corresponding exception handler:
                    RAISE NO_DATA_FOUND;
                END IF; -- if no row updated
            EXCEPTION
                WHEN NO_DATA_FOUND THEN -- tab object not found
                    -- create error entry:
                    l_ePos := 'tab object not found';
                    RAISE;              -- call common exception handler
                -- end when tab object not found
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'update consistsOfId';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- if everything o.k.
    END IF; -- if tab does not exist yet

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId ||
            ', l_oid_s = ' || l_oid_s ||
            ', ai_op = ' || ai_op ||
            ', ai_tabCode = ' || ai_tabCode ||
            ', l_tabOid_s = ' || l_tabOid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$createTab', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$createTab;
/

show errors;


/******************************************************************************
 * Creates tabs for an object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for creating the tabs regarding a business object.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the tabs.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_oid              Id of the object for which the tabs shall be
 *                              generated.
 * @param   ai_tVersionId       Type of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          There are no tabs to generate.
 */
CREATE OR REPLACE FUNCTION p_Object$createTabs
(
    -- input parameters:
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_oid                  ibs_Object.oid%TYPE,
    ai_tVersionId           ibs_Object.tVersionId%TYPE
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_TK_OBJECT             CONSTANT ibs_Tab.kind%TYPE := 2; -- tab kind Object
    c_TK_LINK               CONSTANT ibs_Tab.kind%TYPE := 3; -- tab kind Link

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- number of rows
    l_tabOid                ibs_Object.oid%TYPE; -- the oid of the tab object
    l_tabCode               ibs_Tab.code%TYPE; -- the code for the actual tab
    -- declare cursor:
    -- define cursor for running through all tabs:
    CURSOR tabCursor IS
        SELECT  t.code AS tabCode
        FROM    ibs_ConsistsOf c, ibs_Tab t
        WHERE   c.tVersionId = ai_tVersionId
            AND c.tabId = t.id
            AND t.kind IN (c_TK_OBJECT, c_TK_LINK);

    l_cursorRow             tabCursor%ROWTYPE;

-- body:
BEGIN
    -- loop through the cursor rows:
    FOR l_cursorRow IN tabCursor        -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_tabCode := l_cursorRow.tabCode;

        -- create the tab:
        l_retValue := p_Object$createTab (ai_userId, ai_op, ai_oid,
            l_tabCode, l_tabOid);
    END LOOP; -- while another tuple found

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_oid = ' || ai_oid ||
            ', ai_tVersionId = ' || ai_tVersionId ||
            ', l_tabOid = ' || l_tabOid ||
            ', l_tabCode = ' || l_tabCode ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$createTabs', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$createTabs;
/

show errors;


/******************************************************************************
 * Delete a tab of an object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for deleting a tab from an existing object.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is deleting the tab.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_oid              Id of the object for which the tab shall be
 *                              deleted.
 * @param   ai_tabCode          The (unique) code of the tab.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          There are no tabs to generate.
 */
CREATE OR REPLACE FUNCTION p_Object$deleteTab
(
    -- input parameters:
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_oid                  ibs_Object.oid%TYPE,
    ai_tabCode              ibs_Tab.code%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_PROC_DELETE           CONSTANT ibs_TVersionProc.code%TYPE := 'delete';
                                            -- code for stored procedure which
                                            -- creates an object

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- row counter
    l_tabOid                ibs_Object.oid%TYPE; -- the oid of the tab object
    l_tabOid_s              VARCHAR2 (18);    -- string representation of oid
    l_tabProc               ibs_TVersionProc.name%TYPE;
                                            -- name of stored procedure for
                                            -- creating the tab object
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_rowsProcessed         INTEGER;        -- number of rows of last cmd exec.
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution

    -- exceptions:
    e_statementExecError    EXCEPTION;

-- body:
BEGIN
    -- get the tab data:
    BEGIN
        SELECT  DECODE (p.name, NULL, 'p_Object$delete', p.name), o.oid
        INTO    l_tabProc, l_tabOid
        FROM    ibs_Object o, ibs_ConsistsOf c, ibs_Tab t, ibs_TVersionProc p
        WHERE   o.containerId = ai_oid
            AND t.code = ai_tabCode
            AND o.consistsOfId = c.id
            AND c.tabId = t.id
            AND p.code = c_PROC_DELETE
            AND o.tVersionId = p.tVersionId(+);

        -- at this point we know that the tab object exists.
        -- convert oid to oid_s:
        p_byteToString (l_tabOid, l_tabOid_s);

        -- delete the tab object:
        l_cmdString :=
            'BEGIN ' ||
            ':retValue := ' || l_tabProc || ' (' ||
                '''' || l_tabOid_s || ''', ' ||
                ai_userId || ', ' ||
                ai_op || ');' ||
            'END;';
        BEGIN
            -- open the cursor:
            l_cursorId := DBMS_SQL.OPEN_CURSOR;
            -- parse the statement and use the normal behavior of the
            -- database to which we are currently connected:
            DBMS_SQL.PARSE (l_cursorId, l_cmdString, DBMS_SQL.NATIVE);
            -- bind the variables:
            DBMS_SQL.BIND_VARIABLE (l_cursorId, ':retValue', l_retValue);
            l_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);
            -- get the variable values:
            DBMS_SQL.VARIABLE_VALUE (l_cursorId, ':retValue', l_retValue);
            -- close the cursor:
            DBMS_SQL.CLOSE_CURSOR (l_cursorId);

        EXCEPTION
            WHEN OTHERS THEN
                IF (DBMS_SQL.IS_OPEN (l_cursorId))
                                        -- the cursor is currently open?
                THEN
                    -- close the cursor:
                    DBMS_SQL.CLOSE_CURSOR (l_cursorId);
                END IF; -- the cursor is currently open
                -- get the error position:
                l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
                -- create error entry:
                l_ePos := 'Error in deleting tab at ' || l_lastErrorPos ||
                    ' (' || l_cmdString || ').';
                RAISE e_statementExecError; -- call common exception handler
        END;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the tab object was not found
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
        -- end when the tab object was not found
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'update consistsOfId';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN l_retValue;

 EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId ||
            ', ai_tabCode = ' || ai_tabCode ||
            ', ai_op = ' || ai_op ||
            ', l_tabOid_s = ' || l_tabOid_s ||
            ', l_tabProc = ' || l_tabProc ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$deleteTab', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$deleteTab;
/

show errors;



/******************************************************************************
 * Insert protocol entry for object. <BR>
 *
 * @input parameters:
 * @param   ai_oid              ID of the object for that a protocol entry must
 *                              be inserted.
 * @param   ai_userId           ID of the user who is inserting.
 * @param   ai_op               Operation to be performed.
 * @param   ai_owner            The (new) owner of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
CREATE OR REPLACE FUNCTION p_Object$performInsertProtocol
(
    -- input parameters:
    ai_oid                  ibs_Object.oid%TYPE,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_owner                ibs_User.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_containerId           ibs_Object.oid%TYPE := c_NOOID;
    l_fullName              ibs_User.fullname%TYPE;
    l_icon                  ibs_Object.icon%TYPE;
    l_name                  ibs_Object.name%TYPE;
    l_tVersionId            ibs_Object.tVersionId%TYPE;
    l_containerKind         ibs_Object.containerKind%TYPE;


-- body:
BEGIN
    BEGIN
        -- gather information for protocol entry:
        SELECT  u.fullname, o.icon, o.name, o.tVersionId, o.containerId,
                o.containerKind
        INTO    l_fullName, l_icon, l_name, l_tVersionId, l_containerId,
                l_containerKind
        FROM    ibs_User u, ibs_Object o
        WHERE   u.id = ai_userId
            AND o.oid = ai_oid;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting data';
            RAISE;                      -- call common exception handler
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting data';
            RAISE;                      -- call common exception handler
    END;

    BEGIN
        -- add the new tuple to the ibs_Protocol_01 table:
        INSERT INTO ibs_Protocol_01
                (fullName, userId, oid, objectName, icon, tVersionId,
                containerId, containerKind, owner, action, actionDate)
        VALUES  (l_fullName, ai_userId, ai_oid, l_name, l_icon, l_tVersionId,
                l_containerId, l_containerKind, ai_owner, ai_op, SYSDATE);
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'create protocol entry';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN    l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_owner = ' || ai_owner ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performInsertProtocol',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$performInsertProtocol;
/

show errors;
-- p_Object$performInsertProtocol


/******************************************************************************
 * Insert protocol entry for object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object for that a protocol entry must
 *                              be inserted.
 * @param   ai_userId           ID of the user who is inserting.
 * @param   ai_op               Operation to be performed.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
CREATE OR REPLACE FUNCTION p_Object$insertProtocol
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_oid                   ibs_Object.oid%TYPE;
    l_containerId           ibs_Object.oid%TYPE := c_NOOID;
    l_icon                  ibs_Object.icon%TYPE;
    l_name                  ibs_Object.name%TYPE;
    l_tVersionId            ibs_Object.tVersionId%TYPE;
    l_containerKind         ibs_Object.containerKind%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    -- set the protocol entry:
    l_retValue := p_Object$performInsertProtocol
        (l_oid, ai_userId, ai_op, ai_userId);

    COMMIT WORK;

    -- return the state value:
    RETURN    l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$insertProtocol',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$insertProtocol;
/

show errors;
-- p_Object$insertProtocol


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for creating a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_tVersionId       Type of the new object.
 * @param   ai_name             Name of the object.
 * @param   ai_containerId_s    ID of the container where object shall be
 *                              created in.
 * @param   ai_containerKind    Kind of object/container relationship
 * @param   ai_isLink           Defines if the object is a link
 * @param   ai_linkedObjectId_s If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   ai_description      Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s            String representation of OID of the newly
 *                              created object.
 * [@param  ao_oid]             Oid of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Object$performCreate
(
    -- input parameters:
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_tVersionId           ibs_Object.tVersionId%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        ibs_Object.containerKind%TYPE,
    ai_isLink               ibs_Object.isLink%TYPE,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          ibs_Object.description%TYPE,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2,
    ao_oid                  OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;
    c_EMPTYPOSNOPATH        CONSTANT ibs_Object.posNoPath%TYPE := '0000';

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_id                    ibs_Object.id%TYPE := 0;
    l_typeName              ibs_Object.typeName%TYPE := 'UNKNOWN';
    l_isContainer           ibs_Object.isContainer%TYPE := 0;
    l_showInMenu            ibs_Object.showInMenu%TYPE := 0;
    l_showInNews            ibs_Type.showInNews%TYPE := 0;
    l_icon                  ibs_Object.icon%TYPE := 'icon.gif';
    l_oLevel                ibs_Object.oLevel%TYPE := 1;
                                            -- lowest possible object level
    l_posNo                 ibs_Object.posNo%TYPE := 0;
    l_posNoPath             ibs_Object.posNoPath%TYPE := c_EMPTYPOSNOPATH;
    l_flags                 ibs_Object.flags%TYPE := 0;
    l_rKey                  ibs_Object.rKey%TYPE := 0;
    l_name                  ibs_Object.name%TYPE := ai_name;
    l_description           ibs_Object.description%TYPE := ai_description;
    l_op                    ibs_Operation.id%TYPE := ai_op;
    l_containerId           ibs_Object.containerId%TYPE := c_NOOID;
    l_linkedObjectId        ibs_Object.linkedObjectId%TYPE := c_NOOID;
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_coUserId              ibs_Checkout_01.userId%TYPE := 0;
                                            -- user who checked the object out
    l_check                 ibs_Object.flags%TYPE := 1;
    l_validUntil            ibs_Object.validUntil%TYPE := SYSDATE;

    -- exceptions:
    e_TAB_CREATION_ERROR    EXCEPTION;

-- body:
BEGIN
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    ao_oid := c_NOOID;

    -- retrieve check-out-info for new objects container?
    BEGIN
        -- get the check out data:
        SELECT  co.userId, B_AND (o.flags, c_ISCHECKEDOUT)
        INTO    l_coUserId, l_check
        FROM    ibs_CheckOut_01 co, ibs_Object o
        WHERE   o.oid = l_containerId
            AND co.oid = o.oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_check := 0;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get checkout user data';
            RAISE;                      -- call common exception handler
    END;

    -- cont-object checked out AND checkout-user is NOT current user
    IF (l_check = c_ISCHECKEDOUT AND l_coUserId <> ai_userId)
    THEN
        -- current user is not check-out user
        l_retValue := c_INSUFFICIENT_RIGHTS;
    ELSE                                -- object not checked out or
                                        -- user is check-out user?
        -- checkout-check ok
        -- now check if user has permission to create object

        -- check the container rights:
        l_retValue := p_Rights$checkRights (ao_oid, l_containerId,
                                            ai_userId, l_op, l_rights);

        -- check if the user has the necessary rights:
        IF (l_rights = l_op)            -- the user has the rights?
        THEN
---------
--
-- START get and calculate base-data
--         (old trigger functionality!)
--
            --
            -- 1. compute id and oid for new object
            --
            BEGIN
                -- get new id for new object:
                SELECT  objectIdSeq.NEXTVAL
                INTO    l_id
                FROM    DUAL;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'increasing objectIdSeq';
                    RAISE;              -- call common exception handler
            END;

            -- compute oid; convert
            p_createOid (ai_tVersionId, l_id, ao_oid);
            p_byteToString (ao_oid, ao_oid_s);

            --
            -- 2. compute olevel, posno and posnopath
            --
            -- derive position number from other objects within container:
            -- The posNo is one more than the actual highest posNo within the
            -- container or 1 if there is no object within the container yet.
            BEGIN
                SELECT  DECODE (MAX (posNo), NULL, 1, MAX (posNo) + 1)
                INTO    l_posNo
                FROM    ibs_Object
                WHERE   containerId = l_containerId;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'computing posNo-Information';
                    RAISE;              -- call common exception handler
            END;

            -- derive position level and rkey from container:
            -- The level of an object is the level of the container plus 1
            -- or 0, if there is no container.
            BEGIN
                SELECT  DECODE (oLevel, NULL, 1, oLevel + 1), rKey,
                        validUntil
                INTO    l_oLevel, l_rKey, l_validUntil
                FROM    ibs_Object
                WHERE   oid = l_containerId;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    -- no container found for given object;
                    -- must be root object
                    l_oLevel := 1;
                    l_rKey := 0;
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'computing oLevel information';
                    RAISE;              -- call common exception handler
            END;

            -- calculate new position path:
            IF (l_containerId <> c_NOOID) -- object is within a container?
            THEN
                -- compute the posNoPath as posNoPath of container
                -- concatenated by the posNo of this object:
                BEGIN
                    SELECT  DISTINCT posNoPath || intToRaw (l_posNo, 4)
                    INTO    l_posNoPath
                    FROM    ibs_Object
                    WHERE   oid = l_containerId;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        l_posNoPath := intToRaw (l_posNo, 4);
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'compute posNoPath';
                    RAISE;              -- call common exception handler
                END;
                -- if object is within a container
            ELSE                        -- object is not within a container
                                        -- i.e. it is on top level
                -- compute the posNoPath as posNo of this object:
                l_posNoPath := intToRaw (l_posNo, 4);
            END IF;-- else object is not within a container


            --
            -- 3. get type-info: type name, icon and containerId, showInMenu,
            --                     showInNews
            --
            BEGIN
                SELECT  t.name, t.isContainer, t.showInMenu,
                        t.showInNews * 4, t.icon
                INTO    l_typeName, l_isContainer, l_showInMenu,
                        l_showInNews, l_icon
                FROM    ibs_Type t, ibs_TVersion tv
                WHERE   tv.id = ai_tVersionId
                    AND t.id = tv.typeId;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'selecting type-information';
                    RAISE;              -- call common exception handler
            END;

            --
            -- 4. distinguish between reference/no-reference objects
            --
            IF (ai_isLink = 1)          -- reference object?
            THEN
                --
                -- IMPORTANT: rights-key will be set in here
                --
                -- get data of linked object into new reference object:
                -- If the linked object is itself a link the link shall point
                -- to the original linked object.
                BEGIN
                    SELECT  name, typeName, description, flags, icon, rKey
                    INTO    l_name, l_typeName, l_description, l_flags, l_icon,
                            l_rKey
                    FROM    ibs_Object
                    WHERE   oid = l_linkedObjectId;
                EXCEPTION
                    WHEN OTHERS THEN
                        -- create error entry:
                        l_ePos := 'get link data';
                        RAISE;          -- call common exception handler
                END;
            ELSE
                IF (l_name IS NULL OR l_name = '' OR l_name = ' ')
                THEN
                    l_name := l_typeName;
                END IF;
            END IF; -- if link object

            --
            -- 5. calculate new flags value: add showInNews
            --
            l_flags := B_AND (l_flags, 2147483643) + l_showInNews;

--
-- END get and calculate base data
--
---------

            --
            -- last but not least: insert new information
            --
            BEGIN
                INSERT INTO ibs_Object
                        (id, oid, /*state,*/ tVersionId, typeName,
                        isContainer, name, containerId, containerKind,
                        isLink, linkedObjectId, showInMenu, flags,
                        owner, oLevel, posNo, posNoPath, creationDate,
                        creator, lastChanged, changer, validUntil,
                        description, icon, /*processState,*/ rKey)
                VALUES    (l_id, ao_oid, /*???,*/ ai_tVersionId, l_typeName,
                        l_isContainer, l_name, l_containerId, ai_containerKind,
                        ai_isLink, l_linkedObjectId, l_showInMenu, l_flags,
                        ai_userId, l_oLevel, l_posNo, l_posNoPath, SYSDATE,
                        ai_userId, SYSDATE, ai_userId, l_validUntil,
                        ai_description, l_icon, /*???,*/ l_rKey);
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'INSERT INTO ibs_Object';
                    RAISE;              -- call common exception handler
            END;

            -- check if creation of tabs is necessary:
            IF (ai_containerKind <> 2)  -- object is independent?
            THEN
                -- create tabs for the object:
                l_retValue := p_Object$createTabs
                                (ai_userId, ai_op, ao_oid, ai_tVersionId);

                -- check success:
                IF (l_retValue <> c_ALL_RIGHT)
                THEN
                    RAISE e_TAB_CREATION_ERROR;
                END IF;

                -- set the protocol entry:
                l_retValue := p_Object$performInsertProtocol
                    (ao_oid, ai_userId, ai_op, ai_userId);
            -- end if object is independent
            ELSE                        -- object is a tab
                -- set return value: ok
                l_retValue := c_ALL_RIGHT;
            END IF; -- else object is a tab
        -- if the user has the rights
        ELSE                            -- the user does not have the rights
            -- user has not enough rights!
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights
    END IF; -- else object not checked out or user is check-out user

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId || ', ' ||
            ', ai_op = ' || ai_op || ', ' ||
            ', ai_tVersionId = ' || ai_tVersionId || ', ' ||
            ', ai_name = ' || ai_name || ', ' ||
            ', ai_containerId_s = ' || ai_containerId_s || ', ' ||
            ', ai_containerKind = ' || ai_containerKind || ', ' ||
            ', ai_isLink = ' || ai_isLink ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s ||
            ', ai_description = ' || ai_description ||
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performCreate', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_Object$performCreate;
/

show errors;


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_tVersionId       Type of the new object.
 * @param   ai_name             Name of the object.
 * @param   ai_containerId_s    ID of the container where object shall be
 *                              created in.
 * @param   ai_containerKind    Kind of object/container relationship
 * @param   ai_isLink           Defines if the object is a link
 * @param   ai_linkedObjectId_s If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   ai_description      Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Object$create
(
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_tVersionId           ibs_Object.tVersionId%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        ibs_Object.containerKind%TYPE,
    ai_isLink               ibs_Object.isLink%TYPE,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          ibs_Object.description%TYPE,
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_oid                   ibs_Object.oid%TYPE;

-- body:
BEGIN
    -- perform the operation:
    l_retValue := p_Object$performCreate (
        ai_userId, ai_op, ai_tVersionId, ai_name, ai_containerId_s,
        ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description,
        ao_oid_s, l_oid);

    -- make changes permanent:
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;
END p_Object$create;
/

show errors;

/******************************************************************************
 * Move an existing object to another container. <BR>
 * All sub structures are moved with the object.
 *
 * @input parameters:
 * @param   ai_oid              ID of the object to be moved.
 * @param   ai_targetId         ID of the container where object shall be
 *                              moved to.
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_Object$performMove
(
    -- input parameters:
    ai_oid                  ibs_Object.oid%TYPE,
    ai_targetId             ibs_Object.oid%TYPE
)
AS
    -- constants:
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_posNo                 ibs_Object.posNo%TYPE := 0;
    l_oLevel                ibs_Object.oLevel%TYPE;
    l_oldOLevel             ibs_Object.oLevel%TYPE;
    l_posNoPath             ibs_Object.posNoPath%TYPE;
    l_oldPosNoPath          ibs_Object.posNoPath%TYPE;

-- body:
BEGIN
    BEGIN
        -- get the old values:
        SELECT  oLevel, posNoPath
        INTO    l_oldOLevel, l_oldPosNoPath
        FROM    ibs_Object
        WHERE   oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get oLevel, posNoPath';
            RAISE;                      -- call common exception handler
    END;

    -- get new position number:
    -- The position number is one more than the actual highest position
    -- number of all other objects within the container or 1 if there
    -- is no object within the container yet.
    BEGIN
        SELECT  DECODE (MAX (posNo), NULL, 1, MAX (posNo) + 1)
        INTO    l_posNo
        FROM    ibs_Object
        WHERE   containerId = ai_targetId
            AND oid <> ai_oid;
        -- convert the position number into hex representation:
--          p_IntToHexString (l_posNo, l_posNoHex);
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get position number';
            RAISE;                      -- call common exception handler
    END;

    -- get new level:
    -- The level is one more than the level of the container.
    BEGIN
        SELECT  DECODE (oLevel, NULL, 1, oLevel + 1)
        INTO    l_oLevel
        FROM    ibs_Object
        WHERE   oid = ai_targetId;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get level';
            RAISE;                      -- call common exception handler
    END;


    -- get new position path:
    IF (ai_targetId <> c_NOOID)         -- object is within a container?
    THEN
        -- compute the posNoPath as posNoPath of container concatenated by
        -- the posNo of this object:
        BEGIN
            SELECT  DISTINCT posNoPath || intToRaw (l_posNo, 4)
            INTO    l_posNoPath
            FROM    ibs_Object
            WHERE   oid = ai_targetId;
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'compute posNoPath';
                RAISE;                  -- call common exception handler
        END;
    -- if object is within a container
    ELSE                                -- object is not within a container
                                        -- i.e. it is on top level
        -- compute the posNoPath as posNo of this object:
        l_posNoPath := intToRaw (l_posNo, 4);
    END IF; -- else object is not within a container

    -- update object:
    BEGIN
        UPDATE  ibs_Object
        SET     containerId = ai_targetId,
                oLevel = l_oLevel,
                posNo = l_posNo,
                posNoPath = l_posNoPath
        WHERE   oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'update ibs_Object';
            RAISE;                      -- call common exception handler
    END;

    -- compute and store levels and posNoPaths of underlying objects:
    -- The new posNoPath is the posNoPath of the actual object plus
    -- the rest of the old posNoPath from this object downwards.
    -- The new oLevel is the oLevel of the actual object plus the
    -- difference between the old oLevels of the actual object and
    -- each object.
    BEGIN
        UPDATE  ibs_Object
        SET     posNoPath = l_posNoPath ||
                SUBSTR (posNoPath, l_oldOLevel * 4 + 1),
                oLevel = oLevel + l_oLevel - l_oldOLevel
        WHERE   posNoPath LIKE l_oldPosNoPath || '%'
            AND oid <> ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'update ibs_Object (underlying objects)';
            RAISE;                      -- call common exception handler
    END;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid = ' || ai_oid || ', ' ||
            ', ai_targetId = ' || ai_targetId || ', ' ||
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performMove', l_eText);
END p_Object$performMove;
/

show errors;


/******************************************************************************
 * Updates an existing object. <BR>
 *
 * @input parameters:
 * @param   ai_name             New name of the object.
 * @param   ai_description      New description of the object.
 * @param   ai_icon             New icon of the object.
 * @param   ai_oid              Oid of the object. Used to identify the object.
 * @param   ai_oldContainerId   Oid of container where the object was in before.
 * @param   ai_newContainerId   Oid of container where the object shall be in
 *                              after this operation.
 * @param   ai_oldOLevel        The old oLevel of the object.
 * @param   ai_oldPosNoPath     The old posNoPath of the object.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_ObjectUpdate
(
    -- input parameters:
    ai_name                 ibs_Object.name%TYPE,
    ai_description          ibs_Object.description%TYPE,
    ai_icon                 ibs_Object.icon%TYPE,
    ai_oid                  ibs_Object.linkedObjectId%TYPE,
    ai_oldContainerId       ibs_Object.containerId%TYPE,
    ai_newContainerId       ibs_Object.containerId%TYPE,
    ai_oldOLevel            ibs_Object.oLevel%TYPE,
    ai_oldPosNoPath         ibs_Object.posNoPath%TYPE
)
AS
    -- constants:
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_posNo                 ibs_Object.posNo%TYPE := 0;
    l_oLevel                ibs_Object.oLevel%TYPE;
    l_posNoPath             ibs_Object.posNoPath%TYPE;

-- body:
BEGIN
    -- check if the containerId was changed:
    IF (ai_oldContainerId <> ai_newContainerId) -- containerId changed?
    THEN
        -- move the object to the new container:
        p_Object$performMove (ai_oid, ai_newContainerId);
    END IF; -- containerId changed


    -- set the common attributes of all links pointing to this object:
    BEGIN
        UPDATE  ibs_Object
        SET     name = ai_name,
                description = ai_description,
                icon = ai_icon
        WHERE   isLink = 1
            AND linkedObjectId = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'update ibs_object (last statement)';
            RAISE;                      -- call common exception handler
    END;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_name = ' || ai_name ||
            ', ai_description = ' || ai_description ||
            ', ai_icon = ' || ai_icon ||
            ', ai_oid = ' || ai_oid ||
            ', ai_oldContainerId = ' || ai_oldContainerId ||
            ', ai_newContainerId = ' || ai_newContainerId ||
            ', ai_oldOLevel = ' || ai_oldOLevel ||
            ', ai_oldPosNoPath = ' || ai_oldPosNoPath ||
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ObjectUpdate', l_eText);
END p_ObjectUpdate;
/

show errors;

/******************************************************************************
 * Stores the attributes of an existing object (incl. rights check). <BR>
 * Creates the object if not yet existing.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */

CREATE OR REPLACE FUNCTION p_Object$store
(
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_validUntil           ibs_Object.validUntil%TYPE,
    ai_description          ibs_Object.description%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_EMPTYPOSNOPATH        CONSTANT ibs_Object.posNoPath%TYPE := '0000';

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- row counter
    l_oid                   ibs_Object.oid%TYPE;
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_containerId           ibs_Object.containerId%TYPE := c_NOOID;
    l_icon                  ibs_Object.icon%TYPE := 'icon.gif';
    l_oLevel                ibs_Object.oLevel%TYPE := 1;
                                            -- lowest possible object level
    l_posNoPath             ibs_Object.posNoPath%TYPE := c_EMPTYPOSNOPATH;

-- body:
BEGIN
    -- conversions (OBJECTIDSTRING) -- all input object ids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get container id of object:
        SELECT  containerId, oLevel, icon
        INTO    l_containerId, l_oLevel, l_icon
        FROM    ibs_Object
        WHERE   oid = l_oid;

        -- at this point we know that the object exists.

        -- check rights for the user:
        l_rights := p_Rights$checkRights
            (l_oid, l_containerId, ai_userId, ai_op, l_rights);

        -- check if the user has the necessary rights:
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN
            BEGIN
                UPDATE  ibs_Object
                SET     name = ai_name,
                        lastChanged = SYSDATE,
                        validUntil = ai_validUntil,
                        description = ai_description
                WHERE   oid = l_oid;

                p_ObjectUpdate (ai_name, ai_description, l_icon, l_oid,
                    l_containerId, l_containerId, l_oLevel, l_posNoPath);

                COMMIT WORK;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'update ibs_Object';
                    RAISE;              -- call common exception handler
            END;
        -- if the user has the rights
        ELSE                            -- the user does not have the rights
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the tab object was not found
            -- set corresponding return value:
            l_retValue := c_OBJECTNOTFOUND;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get containerId';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_name = ' || ai_name ||
            ', ai_validUntil = ' || ai_validUntil ||
            ', ai_description = ' || ai_description ||
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$store', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_Object$store;
/

show errors;


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for changing a business object.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_showInNews       Display the object in the news.
 *
 * @output parameters:
 * @param   ao_oid              Oid of the changed object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$performChange
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_validUntil           ibs_Object.validUntil%TYPE,
    ai_description          ibs_Object.description%TYPE,
    ai_showInNews           ibs_Object.flags%TYPE,
    -- output parameters:
    ao_oid                  OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ST_ACTIVE             CONSTANT ibs_Object.state%TYPE := 2; -- active state
    c_ST_CREATED            CONSTANT ibs_Object.state%TYPE := 4;-- created state
    c_INNEWS                CONSTANT ibs_Object.flags%TYPE := 4;
                                            -- bit value for showInNews
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;
                                            -- bit value for check out state

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_count                 INTEGER := 0;    -- counter
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_coUserId              ibs_Checkout_01.userId%TYPE := 0;
                                            -- user who checked the object out
    l_tVersionId            ibs_Object.tVersionId%TYPE := 0;
                                            -- tVersionId of object
    l_containerId           ibs_Object.containerId%TYPE := c_NOOID;
                                            -- oid of container
    l_containerKind         ibs_Object.containerKind%TYPE; -- kind of
                                            -- object/container relationship
    l_icon                  ibs_Object.icon%TYPE; -- the icon of the object
    l_owner                 ibs_Object.owner%TYPE; -- the owner of the object
    l_oLevel                ibs_Object.oLevel%TYPE; -- level of object
    l_posNoPath             ibs_Object.posNoPath%TYPE; -- posNoPath of object
    l_state                 ibs_Object.state%TYPE; -- the state of the object

    -- exceptions:
    e_EXCEPTION             EXCEPTION;      -- any standard exception

-- body:
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Object$performChange;

    -- conversions: (OBJECTIDSTRING) - all input object ids must be converted
    p_stringToByte (ai_oid_s, ao_oid);

    -- check if the object is checked out:
    BEGIN
        SELECT  userId
        INTO    l_coUserId
        FROM    ibs_Checkout_01
        WHERE   oid = ao_oid;

        -- at this point we know that the object is checked out.
        IF (l_coUserId <> ai_userId)    -- wrong user?
        THEN
            -- set corresponding return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
            RAISE e_EXCEPTION;
        END IF; -- if wrong user
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not checked out
            l_coUserId := 0;
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check check-out state';
            RAISE;                      -- call common exception handler
    END;

    -- get the data of the object:
    BEGIN
        SELECT  containerId, containerKind, tVersionId, icon, owner,
                state, oLevel, posNoPath
        INTO    l_containerId, l_containerKind, l_tVersionId, l_icon, l_owner,
                l_state, l_oLevel, l_posNoPath
        FROM    ibs_Object
        WHERE   oid = ao_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object does not exist
            -- set the return value with the error code:
            l_retValue := c_OBJECTNOTFOUND;
            RAISE e_EXCEPTION;
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get the data of the object';
            RAISE;                      -- call common exception handler
    END;

    -- get rights for this user:
    l_rights := p_Rights$checkRights (
        ao_oid,                         -- given object to be accessed by user
        l_containerId,                  -- container of given object
        ai_userId,                      -- user id
        ai_op,                          -- required rights user must have to
                                        -- update object
        l_rights                        -- returned value
        );

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- check if the object is a tab:
        IF (l_containerKind = 2)        -- the object is a tab?
        THEN
            -- get the state of the container:
            BEGIN
                SELECT  state
                INTO    l_state
                FROM    ibs_Object
                WHERE   oid = l_containerId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'get the state of the container';
                    RAISE;              -- call common exception handler
            END;
        -- end if the object is a tab
        ELSIF (l_state = c_ST_CREATED)  -- object was just created?
        THEN
            -- set object to active:
            l_state := c_ST_ACTIVE;
        END IF; -- if object was just created
--          END IF; -- else object is no tab

        -- store the values of the object:
        BEGIN
            UPDATE  ibs_Object
            SET     name = ai_name,
                    validUntil = ai_validUntil,
                    description = ai_description,
                    lastChanged = SYSDATE,
                    state = c_ST_ACTIVE,
                    changer = ai_userId
            WHERE   oid = ao_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get the data of the object';
                RAISE;                  -- call common exception handler
        END;

        -- set the showInNews flag:
        IF (ai_showInNews = 1)          -- object shall be shown in news?
        THEN
            -- set the showInNews bit:
            BEGIN
                UPDATE  ibs_Object
                SET     flags = B_OR (flags, c_INNEWS)
                WHERE   oid = ao_oid;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'set the showInNews bit';
                    RAISE;              -- call common exception handler
            END;
        -- end if object shall be shown in news
        ELSE                            -- object shall not be shown in news
            -- drop the showInNews bit:
            BEGIN
                UPDATE  ibs_Object
                SET     flags = B_AND (flags, B_XOR (2147483647, c_INNEWS))
                                        -- 0x7FFFFFFF
                WHERE   oid = ao_oid;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'drop the showInNews bit';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- else object shall not be shown in news

        -- call standard update procedure:
        BEGIN
            p_ObjectUpdate (ai_name, ai_description, l_icon, ao_oid,
                l_containerId, l_containerId, l_oLevel, l_posNoPath);
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'error in p_ObjectUpdate';
                RAISE;                  -- call common exception handler
        END;

        -- ensure that the tab objects have the correct state:
        -- set the state for all tab objects, which have incorrect
        -- states
        BEGIN
            UPDATE  ibs_Object
            SET     state = l_state
            WHERE   containerId = ao_oid
                AND containerKind = 2
                AND state <> l_state
                AND (   state = c_ST_ACTIVE
                    OR  state = c_ST_CREATED
                    );
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set states of tab objects';
                RAISE;                  -- call common exception handler
        END;

        -- set the protocol entry:
        l_retValue := p_Object$performInsertProtocol
            (ao_oid, ai_userId, ai_op, l_owner);
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        -- set the return value with the error code:
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Object$performChange;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_name = ' || ai_name ||
            ', ai_validUntil = ' || ai_validUntil ||
            ', ai_description = ' || ai_description ||
            ', ai_showInNews = ' || ai_showInNews ||
            ', ao_oid = ' || ao_oid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performChange',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$performChange;
/

show errors;


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_ai_showInNews    Display object in the news.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Object$change
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_validUntil           ibs_Object.validUntil%TYPE,
    ai_description          ibs_Object.description%TYPE,
    ai_showInNews           ibs_Object.flags%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_oid                   ibs_Object.oid%TYPE; -- the oid of the object

-- body:
BEGIN
    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;
END p_Object$change;
/

show errors;


/******************************************************************************
 * Move an existing object to another container (incl. rights check). <BR>
 * All sub structures are moved with the object.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be moved.
 * @param   ai_userId           ID of the user who is moving the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_containerId_s    ID of the container where object shall be
 *                              moved to.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$move
(
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_containerId_s        VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_CUT_FAIL_ERROR        CONSTANT INTEGER := 11; -- error during moving
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;
                                            -- bit value for check out state
    c_TVAttachment          CONSTANT ibs_TVersion.id%TYPE := 16842833;
                                            -- 0x01010051
                                            -- tVersionId of Attachment

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE; -- the oid of the object
    l_containerId           ibs_Object.containerId%TYPE := c_NOOID;
                                            -- oid of container
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_oldContainerId        ibs_Object.containerId%TYPE := c_NOOID;
                                            -- oid of old container
    l_posNoPath             ibs_Object.posNoPath%TYPE; -- posNoPath of object
    l_posNoPathTarget       ibs_Object.posNoPath%TYPE; -- target posNoPath
    l_name                  ibs_Object.name%TYPE; -- counter
    l_description           ibs_Object.description%TYPE;
                                            -- the description of the object
    l_icon                  ibs_Object.icon%TYPE; -- the icon of the object
    l_oLevel                ibs_Object.oLevel%TYPE; -- level of object
    l_coUserId              ibs_Checkout_01.userId%TYPE := 0;
                                            -- user who checked the object out
    l_tVersionId            ibs_Object.tVersionId%TYPE := 0;
                                            -- tVersionId of object

-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input object ids must be converted
    p_stringToByte (ai_oid_s, l_oid);
    p_stringToByte (ai_containerId_s, l_containerId);

    BEGIN
        -- is the object checked out?
        SELECT  userId
        INTO    l_coUserId
        FROM    ibs_CheckOut_01
        WHERE   oid = l_oid;

        l_rowCount := 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not checked out?
            l_rowCount := 0;
        WHEN TOO_MANY_ROWS THEN         -- more than one checkout user found?
            l_rowCount := 2;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get checkout user';
            RAISE;                      -- call common exception handler
    END;

    -- check if the current user is the checkout user:
    IF (l_rowCount > 0 AND l_coUserId <> ai_userId) -- user has no rights?
    THEN
        -- set return value:
        l_retValue := c_INSUFFICIENT_RIGHTS;
    -- if user has no rights
    ELSE                                -- object not checked out by other user
        BEGIN
            -- get the actual containerId of the object:
            SELECT  containerId
            INTO    l_oldContainerId
            FROM    ibs_Object
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_retValue := c_OBJECTNOTFOUND;
                -- create error entry:
                l_ePos := 'object not found';
                RAISE;                  -- call common exception handler
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'getting containerId';
                RAISE;                  -- call common exception handler
        END;

        -- get rights for this user:
        l_rights := p_Rights$checkRights
            (l_oid, l_oldContainerId, ai_userId, ai_op, l_rights);

        -- check if the user has the necessary rights:
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN
            BEGIN
                -- get the old posNoPath:
                SELECT  posNoPath
                INTO    l_posNoPath
                FROM    ibs_Object
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'getting posNoPath';
                    RAISE;              -- call common exception handler
            END;

            BEGIN
                -- get the new posNoPath:
                SELECT  posNoPath
                INTO    l_posNoPathTarget
                FROM    ibs_Object
                WHERE   oid = l_containerId;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'getting posNoPathTarget';
                    RAISE;              -- call common exception handler
            END;

            -- check if the posNoPath is part of posNoPathTarget:
            IF (INSTR (l_posNoPathTarget, l_posNoPath, 1, 1) <> 1)
            THEN
                /*[SPCONV-ERR(62)]:BEGIN TRAN statement ignored*/
                BEGIN
                    -- set the new containerId:
                    UPDATE  ibs_Object
                    SET     containerId = l_containerId
                    WHERE   oid = l_oid;
                EXCEPTION
                    WHEN OTHERS THEN
                        -- create error entry:
                        l_ePos := 'UPDATE';
                        RAISE;          -- call common exception handler
                END;

                -- update the other properties:
                BEGIN
                    SELECT  name, oLevel, description, icon,
                            tVersionId
                    INTO    l_name, l_oLevel, l_description, l_icon,
                            l_tVersionId
                    FROM    ibs_Object
                    WHERE   oid = l_oid;

                    p_ObjectUpdate (l_name, l_description, l_icon, l_oid,
                        l_oldContainerId, l_containerId, l_oLevel,
                        l_posNoPath);
                EXCEPTION
                    WHEN OTHERS THEN
                        -- create error entry:
                        l_ePos := 'updating other properties';
                        RAISE;          -- call common exception handler
                END;

-- ****************************************************************************
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******

                -- Ist leider notwendig, da auf den Sonderfall eines
                -- Attachments keine gesonderte Âbfrage erfolgt und so die
                -- Icons nicht gelöscht (Infoicon bei altem Dokument) bzw.
                -- doppelt gesetzt (MasterIcon bei neuem Dokument) werden.

                -- ensure that the flags and a master are set:
                IF (l_tVersionId = c_TVAttachment) -- Attachment?
                THEN
                    -- ensure that the old attachment owner has the
                    -- correct flags set:
                    l_retValue := p_Attachment_01$ensureMaster
                                (l_oldContainerId, null);
                    -- ensure that the new attachment owner has the
                    -- correct flags set:
                    l_retValue := p_Attachment_01$ensureMaster
                                (l_containerId, null);
                END IF;

-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ****************************************************************************

                COMMIT WORK;
            ELSE
                -- set return value:
                l_retValue := c_CUT_FAIL_ERROR;
            END IF;
        -- end if the user has the rights
        ELSE                            -- the user does not have the rights
            -- set return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights
    END IF; -- else object not checked out by other user

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_containerId_s = ' || ai_containerId_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$move',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$move;
/
show errors;


/******************************************************************************
 * Change the state of an existing object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_state            The new state of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

CREATE OR REPLACE FUNCTION p_Object$changeState
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_state                ibs_Object.state%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ST_ACTIVE             CONSTANT ibs_Object.state%TYPE := 2; -- active state
    c_ST_CREATED            CONSTANT ibs_Object.state%TYPE := 4; -- created state

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_containerId           ibs_Object.containerId%TYPE := c_NOOID;
    l_oldState              ibs_Object.state%TYPE := 0;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get the actual containerId and state of the object:
        SELECT  containerId, state
        INTO    l_containerId, l_oldState
        FROM    ibs_Object
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting containerId, oldState';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting containerId, oldState';
            RAISE;                      -- call common exception handler
    END;

    -- get rights for the user:
    l_rights := p_Rights$checkRights
        (l_oid, l_containerId, ai_userId, ai_op, l_rights);

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- set the new state for the object and all tabs:
        BEGIN
            UPDATE  ibs_Object
            SET     state = ai_state
            WHERE   oid = l_oid
                OR  (   containerId = l_oid
                    AND containerKind = 2
                    AND state <> ai_state
                    AND (   state = c_ST_ACTIVE
                        OR  state = c_ST_CREATED
                        )
                    );
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'set state';
                RAISE;                  -- call common exception handler
        END;

        COMMIT WORK;
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        -- set the return value:
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_state = ' || ai_state ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$changeState', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$changeState;
/

show errors;


/******************************************************************************
 * Change the processState of an existing object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_processState     The new process state of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$changeProcessState
(
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_processState         ibs_Object.processState%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;
    l_oldProcState          ibs_Object.processState%TYPE := 0;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        SELECT  processState
        INTO    l_oldProcState
        FROM    ibs_Object
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting oldProcState';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting oldProcState';
            RAISE;                      -- call common exception handler
    END;

    /*[SPCONV-ERR(50)]:BEGIN TRAN statement ignored*/
    BEGIN
        -- set the new process state:
        UPDATE  ibs_Object
        SET     processState = ai_processState
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'set process state';
            RAISE;                      -- call common exception handler
    END;

    -- set the protocol entry:
    l_retValue := p_Object$performInsertProtocol
        (l_oid, ai_userId, ai_op, ai_userId);

    -- make changes permanent:
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_processState = ' || ai_processState ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$changeProcessState', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$changeProcessState;
/
show errors;


/******************************************************************************
 * Change the owner of an existing object, including subsequent objects. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_owner            The new owner of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$changeOwnerRec
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_owner                ibs_Object.owner%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_containerId           ibs_Object.oid%TYPE := c_NOOID;
    l_oldOwner              ibs_Object.owner%TYPE := 0;
    l_posNoPath             ibs_Object.posNoPath%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get the containerId and the owner of the object:
        SELECT  containerId, owner, posNoPath
        INTO    l_containerId, l_oldOwner, l_posNoPath
        FROM    ibs_Object
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting containerId and owner';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting containerId and owner';
            RAISE;                      -- call common exception handler
    END;

    -- get rights for the user:
    l_rights := p_Rights$checkRights
        (l_oid, l_containerId, ai_userId, ai_op, l_rights);

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        /*[SPCONV-ERR(59)]:BEGIN TRAN statement ignored*/
        BEGIN
            -- change the owner of the object and the subsequent objects:
            UPDATE  ibs_Object
            SET     owner = ai_owner
            WHERE   posNoPath LIKE l_posNoPath || '%';
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'setting owner';
                RAISE;                  -- call common exception handler
        END;

        -- make changes permanent:
        COMMIT WORK;
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        -- set return value:
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_owner = ' || ai_owner ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$changeOwnerRec',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$changeOwnerRec;
/
show errors;


/******************************************************************************
 * Change the owner of the tabs of an existing object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_owner            The new owner of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$changeTabsOwner
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_owner                ibs_Object.owner%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_containerId           ibs_Object.oid%TYPE := c_NOOID;
    l_oldOwner              ibs_Object.owner%TYPE := 0;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get the containerId and the owner of the object:
        SELECT  containerId, owner
        INTO    l_containerId, l_oldOwner
        FROM    ibs_Object
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting containerId and owner';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting containerId and owner';
            RAISE;                      -- call common exception handler
    END;

    -- get rights for the user:
    l_rights := p_Rights$checkRights
        (l_oid, l_containerId, ai_userId, ai_op, l_rights);

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        /*[SPCONV-ERR(59)]:BEGIN TRAN statement ignored*/
        BEGIN
            -- change the owner of the object tabs:
            UPDATE  ibs_Object
            SET     owner = ai_owner
            WHERE   containerId = l_oid
                AND containerKind = 2;
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'setting owner';
                RAISE;                  -- call common exception handler
        END;

        COMMIT WORK;
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        -- set return value:
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_owner = ' || ai_owner ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$changeTabsOwner',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$changeTabsOwner;
/
show errors;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for retrieving the data of a business object.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be retrieved.
 * @param   ai_userId           ID of the user who is retrieving the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerName    Name of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Display the object in the news container.
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out
 * @param   ao_checkOutUser     id of the user which checked out the object
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   ao_oid              Oid of the retrieved object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$performRetrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    -- output parameters:
    ao_state                OUT ibs_Object.state%TYPE,
    ao_tVersionId           OUT ibs_Object.tVersionId%TYPE,
    ao_typeName             OUT ibs_Object.typeName%TYPE,
    ao_name                 OUT ibs_Object.name%TYPE,
    ao_containerId          OUT ibs_Object.containerId%TYPE,
    ao_containerName        OUT ibs_Object.name%TYPE,
    ao_containerKind        OUT ibs_Object.containerKind%TYPE,
    ao_isLink               OUT ibs_Object.isLink%TYPE,
    ao_linkedObjectId       OUT ibs_Object.linkedObjectId%TYPE,
    ao_owner                OUT ibs_Object.owner%TYPE,
    ao_ownerName            OUT ibs_user.fullname%TYPE,
    ao_creationDate         OUT ibs_Object.creationDate%TYPE,
    ao_creator              OUT ibs_Object.creator%TYPE,
    ao_creatorName          OUT ibs_User.fullname%TYPE,
    ao_lastChanged          OUT ibs_Object.lastChanged%TYPE,
    ao_changer              OUT ibs_Object.changer%TYPE,
    ao_changerName          OUT ibs_User.fullname%TYPE,
    ao_validUntil           OUT ibs_Object.validUntil%TYPE,
    ao_description          OUT ibs_Object.description%TYPE,
    ao_showInNews           OUT ibs_Object.flags%TYPE,
    ao_checkedOut           OUT ibs_Object.flags%TYPE,
    ao_checkOutDate         OUT ibs_CheckOut_01.checkout%TYPE,
    ao_checkOutUser         OUT ibs_CheckOut_01.userId%TYPE,
    ao_checkOutUserOid      OUT ibs_User.oid%TYPE,
    ao_checkOutUserName     OUT ibs_User.name%TYPE,
    ao_oid                  OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_RIGHT_VIEW            CONSTANT ibs_RightsKeys.rights%TYPE := 2;
    c_RIGHT_READ            CONSTANT ibs_RightsKeys.rights%TYPE := 4;
    c_INNEWS                CONSTANT ibs_Object.flags%TYPE := 4;
                                            -- bit value for showInNews
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;
                                            -- bit value for check out state

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_dummy                 INTEGER;
    l_containerId           ibs_Object.oid%TYPE := c_NOOID;
    l_tempName              ibs_User.name%TYPE := NULL;
    l_tempOid               ibs_Object.oid%TYPE := c_NOOID;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, ao_oid);

    BEGIN
        -- get the containerId and the owner of the object:
        SELECT  containerId
        INTO    ao_containerId
        FROM    ibs_Object
        WHERE   oid = ao_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting containerId';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting containerId';
            RAISE;                      -- call common exception handler
    END;

    -- get rights for the user:
    l_rights := p_Rights$checkRights
        (ao_oid, ao_containerId, ai_userId, ai_op, l_rights);

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        BEGIN
            -- get the data of the object:
            SELECT  o.state, o.tVersionId, o.typeName, o.name,
                    o.containerId, c.name, o.containerKind,
                    o.isLink, o.linkedObjectId, o.owner, own.fullname,
                    o.creationDate, o.creator, cr.fullname,
                    o.lastChanged, o.changer, ch.fullname,
                    o.validUntil, o.description,
                    B_AND (o.flags, c_INNEWS),
                    B_AND (o.flags, c_ISCHECKEDOUT)
            INTO    ao_state, ao_tVersionId, ao_typeName, ao_name,
                    ao_containerId, ao_containerName, ao_containerKind,
                    ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
                    ao_creationDate, ao_creator, ao_creatorName,
                    ao_lastChanged, ao_changer, ao_changerName,
                    ao_validUntil, ao_description,
                    ao_showInNews, ao_checkedOut
            FROM    ibs_Object o, ibs_Object c,
                    ibs_User own, ibs_User cr, ibs_User ch
            WHERE   o.containerId = c.oid(+)
                AND o.owner = own.id(+)
                AND o.creator = cr.id(+)
                AND o.changer = ch.id(+)
                AND o.oid = ao_oid;
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'get object data';
                RAISE;                  -- call common exception handler
        END;

        -- set the object as read:
        l_dummy := p_setRead (ao_oid, ai_userId);

        -- is the checkout flag set?
        IF (ao_checkedOut = c_ISCHECKEDOUT) -- the object is checked out?
        THEN
            BEGIN
                -- get the info who checked out the object:
                SELECT  ch.checkout, ch.userid, u.oid, u.name
                INTO    ao_checkOutDate, ao_checkOutUser,
                        l_tempOid, l_tempName
                FROM    ibs_CheckOut_01 ch, ibs_User u
                WHERE   (u.id = ch.userid(+))
                    AND ch.oid = ao_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'get checkout values';
                    RAISE;              -- call common exception handler
            END;

            -- rights set for viewing and/or editing the User?
            l_rights := p_Rights$checkRights (ao_oid, l_containerId,
                ai_userId, B_OR (c_RIGHT_VIEW, c_RIGHT_READ), l_rights);

             -- check if the user has the necessary rights
            IF (B_AND (l_rights, c_RIGHT_READ) = c_RIGHT_READ)
                                        -- user has read rights?
            THEN
                 ao_checkOutUserName := l_tempName;
                 ao_checkOutUserOid := l_tempOid;
            -- end if user has read rights?
            ELSIF (B_AND (l_rights, c_RIGHT_VIEW) = c_RIGHT_VIEW)
                                        -- user has view rights?
            THEN
                 ao_checkOutUserName := l_tempName;
            END IF; -- elsif user has view rights
        END IF; -- if the object is checked out
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        -- set the default data of the object:
        ao_state            := 0;
        ao_tVersionId       := 0;
        ao_typeName         := ' ';
        ao_name             := ' ';
        ao_containerId      := c_NOOID;
        ao_containerKind    := 0;
        ao_isLink           := 0;
        ao_linkedObjectId   := c_NOOID;
        ao_owner            := 0;
        ao_ownerName        := ' ';
        ao_creationDate     := SYSDATE;
        ao_creator          := 0;
        ao_creatorName      := ' ';
        ao_lastChanged      := SYSDATE;
        ao_changer          := 0;
        ao_changerName      := ' ';
        ao_validUntil       := SYSDATE;
        ao_description      := ' ';
        ao_showInNews       := 0;
        ao_checkedOut       := 0;
        ao_checkOutDate     := SYSDATE;
        ao_checkOutUser     := 0;
        ao_checkOutUserOid  := c_NOOID;
        ao_checkOutUserName := ' ';
        -- set return value:
        l_retValue          := c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performRetrieve',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$performRetrieve;
/

show errors;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be retrieved.
 * @param   ai_userId           ID of the user who is retrieving the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerName    Name of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Display the object in the news.
 * @param   ao_showInNews       flag if object should be shown in newscontainer
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out
 * @param   ao_checkOutUser     id of the user which checked out the object
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    -- output parameters:
    ao_state                OUT ibs_Object.state%TYPE,
    ao_tVersionId           OUT ibs_Object.tVersionId%TYPE,
    ao_typeName             OUT ibs_Object.typeName%TYPE,
    ao_name                 OUT ibs_Object.name%TYPE,
    ao_containerId          OUT ibs_Object.containerId%TYPE,
    ao_containerName        OUT ibs_Object.name%TYPE,
    ao_containerKind        OUT ibs_Object.containerKind%TYPE,
    ao_isLink               OUT ibs_Object.isLink%TYPE,
    ao_linkedObjectId       OUT ibs_Object.linkedObjectId%TYPE,
    ao_owner                OUT ibs_Object.owner%TYPE,
    ao_ownerName            OUT ibs_user.fullname%TYPE,
    ao_creationDate         OUT ibs_Object.creationDate%TYPE,
    ao_creator              OUT ibs_Object.creator%TYPE,
    ao_creatorName          OUT ibs_User.fullname%TYPE,
    ao_lastChanged          OUT ibs_Object.lastChanged%TYPE,
    ao_changer              OUT ibs_Object.changer%TYPE,
    ao_changerName          OUT ibs_User.fullname%TYPE,
    ao_validUntil           OUT ibs_Object.validUntil%TYPE,
    ao_description          OUT ibs_Object.description%TYPE,
    ao_showInNews           OUT ibs_Object.flags%TYPE,
    ao_checkedOut           OUT ibs_Object.flags%TYPE,
    ao_checkOutDate         OUT ibs_CheckOut_01.checkout%TYPE,
    ao_checkOutUser         OUT ibs_CheckOut_01.userId%TYPE,
    ao_checkOutUserOid      OUT ibs_User.oid%TYPE,
    ao_checkOutUserName     OUT ibs_User.name%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;


-- body:
BEGIN
    -- retrieve the data:
    l_retValue := p_Object$performRetrieve (
        ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);

    -- return the state value:
    RETURN l_retValue;
END p_Object$retrieve;
/

show errors;


/******************************************************************************
 * Determine the oid of the object which is the next container above a given
 * object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object where the upper object's oid
 *                              shall be determined.
 *
 * @output parameters:
 * @param   ao_upperOid         The upper object's oid.
 *
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_INSUFFICIENT_RIGHTS   User has no right to perform action.
 *  c_OBJECTNOTFOUND        The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$getUpperOid
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    -- output parameters:
    ao_upperOid             OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_TVDiscTopic           CONSTANT ibs_TVersion.id%TYPE := 16843777;
                                            -- 0x01010401
                                            -- tVersionId of Discussion Topic
    c_TVDiscEntry           CONSTANT ibs_TVersion.id%TYPE := 16844033;
                                            -- 0x01010501
                                            -- tVersionId of Discussion Entry
    c_TVDiscXMLViewer       CONSTANT ibs_TVersion.id%TYPE := 16872721;
                                            -- 0x01017511
                                            -- tVersionId of DiscXMLViewer

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;
    l_posNoPath             ibs_Object.posNoPath%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get posNoPath of actual object:
        SELECT  posNoPath
        INTO    l_posNoPath
        FROM    ibs_Object
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'select posnopath';
            RAISE;                      -- call common exception handler
    END;

    BEGIN
        -- get the oid of the object which is the nearest container above the
        -- actual object:
        SELECT  containerId
        INTO    ao_upperOid
        FROM    ibs_Object
        WHERE   posNoPath =
                (   SELECT  MAX (o.posNoPath)
                    FROM    ibs_Object o, ibs_Object c
                    WHERE   o.containerId = c.oid(+)
                        AND l_posNoPath LIKE o.posNoPath || '%'
                        AND o.containerKind = 1
                        -- check for special object types which are no
                        -- containers:
                        AND c.tVersionId NOT IN
                            (c_TVDiscTopic, c_TVDiscEntry, c_TVDiscXMLViewer)
                );
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_retValue := c_OBJECTNOTFOUND;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'select containerId from upperObject';
            RAISE;                      -- call common exception handler
        RAISE;
    END;

    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$getUpperOid',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$getUpperOid;
/

show errors;


/******************************************************************************
 * Determine the oid of the object which represents a tab of a given object
 * determined by the object's name. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object where the tab object's oid
 *                              shall be determined.
 * @param   ai_name             Name of the tab object.
 *
 * @output parameters:
 * @param   ao_tabOid           The tab object's oid.
 * @param   ao_tabContent       The number of elements within the tab (if the
 *                              tab is a container).
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$getTabInfo
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_operation            ibs_Operation.id%TYPE,
    ai_userId               ibs_User.id%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    -- output parameters:
    ao_tabOid               OUT ibs_Object.oid%TYPE,
    ao_tabContent           OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get the containerId and the owner of the object:
        SELECT  oid
        INTO    ao_tabOid
        FROM    ibs_Object
        WHERE   containerId = l_oid
--! HACK HACK HACK HB wegen Umlauten ...
            AND name LIKE ai_name
--! ... HACK HACK HACK wegen Umlauten
            AND containerKind = 2;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- no elements within tab:
            ao_tabContent := 0;
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting tabOid';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting tabOid';
            RAISE;                      -- call common exception handler
    END;

    BEGIN
        SELECT  COUNT (*)
        INTO    ao_tabContent
        FROM    v_Container$content
        WHERE   containerid = ao_tabOid
            AND ai_operation = B_AND (rights, ai_operation)
            AND userId = ai_userId;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting content count';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_name = ' || ai_name ||
            ', ao_tabOid = ' || ao_tabOid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$getTabInfo',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$getTabInfo;
/

show errors;


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also deletes all links showing to this object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for deleting a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_oid              Oid of the deleted object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$performDelete
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ao_oid                  OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_containerId           ibs_Object.containerId%TYPE;
    l_posNoPath             ibs_Object.posNoPath%TYPE;
    l_name                  ibs_Object.name%TYPE;
    l_icon                  ibs_Object.icon%TYPE;
    l_dummy                 ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_description           ibs_Object.description%Type;
    l_oLevel                ibs_Object.oLevel%Type;
    l_coUserId              ibs_Checkout_01.userId%TYPE := 0;
                                            -- user who checked the object out


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, ao_oid);

    BEGIN
        -- is the object checked out?
        SELECT  userId
        INTO    l_coUserId
        FROM    ibs_CheckOut_01
        WHERE   oid = ao_oid;

        l_rowCount := 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not checked out?
            l_rowCount := 0;
        WHEN TOO_MANY_ROWS THEN         -- more than one checkout user found?
            l_rowCount := 2;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get checkout user';
            RAISE;                      -- call common exception handler
    END;

    -- check if the current user is the checkout user:
    IF (l_rowCount > 0 AND l_coUserId <> ai_userId) -- user has no rights?
    THEN
        -- set return value:
        l_retValue := c_INSUFFICIENT_RIGHTS;
    -- if user has no rights
    ELSE                                -- object not checked out by other user
        BEGIN
            -- get the object data:
            SELECT  containerId, posNoPath, oLevel, description
            INTO    l_containerId, l_posNoPath, l_oLevel, l_description
            FROM    ibs_Object
            WHERE   oid = ao_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- object not found?
                -- set return value:
                l_retValue := c_OBJECTNOTFOUND;
                -- create error entry:
                l_ePos := 'getting object data';
                RAISE;                  -- call common exception handler
            WHEN TOO_MANY_ROWS THEN     -- more than one object found?
                NULL;                   -- no error, nothing to do
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'getting object data';
                RAISE;                  -- call common exception handler
        END;

        -- check the rights for the object and all subsequent objects:
        BEGIN
            SELECT  count (*)
            INTO    l_rowCount
            FROM    v_Container$rights
            WHERE   userId = ai_userId
                AND B_AND (rights, ai_op) <> ai_op
                AND posNoPath LIKE l_posNoPath || '%';
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'getting count of objects where user has no rights';
                RAISE;                  -- call common exception handler
        END;

        -- check if the user has the necessary rights on all objects:
        IF (l_rowCount = 0)           -- the user has the rights?
        THEN
-- HP 990830
-- PROBLEM: protocol only for object - no subsequent objects!!!!
            -- set the protocol entry:
            l_retValue := p_Object$performInsertProtocol
                (ao_oid, ai_userId, ai_op, ai_userId);

            -- start deletion of object, subsequent objects AND
            -- references to deleted objects:
            BEGIN
                -- mark object AND subsequent objects as 'deleted'
                -- via posNoPath
                UPDATE  ibs_Object
                SET     state = 1,
                        changer = ai_userId,
                        lastChanged = SYSDATE
                WHERE   posNoPath LIKE l_posNoPath || '%';
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'UPDATE objects';
                    RAISE;              -- call common exception handler
            END;

                BEGIN
                    SELECT  oLevel, description
                    INTO    l_oLevel, l_description
                    FROM    ibs_Object
                    WHERE   oid = ao_oid;

                    p_ObjectUpdate (l_name, l_description, l_icon,
                        ao_oid, l_containerId, l_containerId,
                        l_oLevel, l_posNoPath);
                EXCEPTION
                    WHEN OTHERS THEN
                        -- create error entry:
                        l_ePos := 'get oLevel, description';
                        RAISE;          -- call common exception handler
                END;

            BEGIN
                -- mark references to the object as 'deleted':
                UPDATE  ibs_Object
                SET     state = 1,
                        changer = ai_userId,
                        lastChanged = SYSDATE
                WHERE   linkedObjectId IN
                        (   SELECT  oid
                            FROM    ibs_Object
                            WHERE   posNoPath LIKE l_posNoPath || '%'
                                AND state = 1
                        );

                -- the external keys of the deleted object have to be
                -- archived:
                l_retValue := p_KeyMapper$archiveExtKeys (l_posNoPath);
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'UPDATE links';
                    RAISE;              -- call common exception handler
            END;
        -- end if the user has the rights
        ELSE                            -- the user does not have the rights
            -- set return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights
    END IF; -- else object not checked out by other user

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ao_oid = ' || ao_oid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performDelete',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$performDelete;
/

show errors;


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$delete
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;


-- body:
BEGIN
    /*[SPCONV-ERR(20)]:BEGIN TRAN statement ignored*/
    -- perform the operation:
    l_retValue := p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);

    -- make changes permanent:
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;
END p_Object$delete;
/
show errors;


/******************************************************************************
 * Undeletes an object and all its values (incl. rights check). <BR>
 * This procedure also undeletes all links showing to this object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for undeleting a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be undeleted.
 * @param   ai_userId           ID of the user who is undeleting the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$performUnDelete
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_containerId           ibs_Object.containerId%TYPE;
    l_posNoPath             ibs_Object.posNoPath%TYPE; -- current hierarchy path
    l_oLevel                ibs_Object.oLevel%TYPE;
    l_oid                   ibs_Object.oid%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get container id, posNoPath and level within tree of object:
        SELECT  containerId, posNoPath, oLevel
        INTO    l_containerId, l_posNoPath, l_oLevel
        FROM    ibs_Object
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting object data';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting object data';
            RAISE;                      -- call common exception handler
    END;

    BEGIN
        -- start undeletion of object, subsequent objects AND references to
        -- mark object and subsequent objects as 'deleted' via posnopath
        UPDATE  ibs_Object
        SET     state = 2
        WHERE   posNoPath LIKE l_posNoPath || '%'
            AND state = 1;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'updating objects';
            RAISE;                      -- call common exception handler
    END;

    BEGIN
        -- mark references to the object as 'undelete'
        UPDATE  ibs_Object
        SET     state = 2
        WHERE   linkedObjectId IN
                (   SELECT  oid
                    FROM    ibs_Object
                    WHERE   posNoPath LIKE l_posNoPath || '%'
                    AND state = 2
                );
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'updating links';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN    l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performUnDelete',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$performUnDelete;
/
show errors;
/


/******************************************************************************
 * Uneletes an object and all its values (incl. rights check). <BR>
 * This procedure also undelets all links showing to this object.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be undeleted.
 * @param   ai_userId           ID of the user who is undeleting the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$UnDelete
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows


-- body:
BEGIN
    -- perform the operation:
    l_retValue := p_Object$performUnDelete (ai_oid_s, ai_userId, ai_op);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$UnDelete',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$UnDelete;
/
show errors;


/******************************************************************************
 * Deletes all references to an object - but not the object itself.<BR>
 * Attention: no rights check will be done!
 *
 * @input parameters:
 * @param  ai_oid_s             ID of the object wich refs are to be deleted.
 * @param  ai_userId            ID of the user who is deleting the object.
 * @param  ai_op                Operation to be performed
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
CREATE OR REPLACE FUNCTION p_Object$deleteAllRefs
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- mark references to the object as 'deleted'
        -- ATTENTION: used like in p_Object$performDelete
        --            if delete mechanism changes: change both!!
        UPDATE  ibs_Object
        SET     state = 1,
                changer = ai_userId,
                lastChanged = SYSDATE
        WHERE   linkedObjectId = l_oid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'deleting references';
            RAISE;                      -- call common exception handler
    END;

    -- make changes permanent:
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$deleteAllRefs',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$deleteAllRefs;
/
show errors;



/******************************************************************************
 * All stored procedures used for copyPaste.
 ******************************************************************************
 */


/******************************************************************************
 * Read out the master attachment of a given business object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the rootobject to be copied.
 *
 * @output parameters:
 * @param   ao_masterOid_s      The Oid of the masterattachment.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$getMasterOid
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    -- output parameters:
    ao_masterOid            OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_TVAttachmentContainer CONSTANT ibs_TVersion.id%TYPE := 16842849;
                                            -- 0x01010061
                                            -- tVersionId of AttachmentContainer

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get oid of master attachment:
        SELECT  o.oid
        INTO    ao_masterOid
        FROM    ibs_Object o, ibs_Attachment_01 a
        WHERE   o.containerId =
                (   SELECT  oid
                    FROM    ibs_Object
                    WHERE   containerId = l_oid
                        AND tVersionId = c_TVAttachmentContainer
                )
            AND o.oid = a.oid
            AND a.isMaster = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'getting master oid';
            RAISE;                      -- call common exception handler
        WHEN TOO_MANY_ROWS THEN         -- more than one object found?
            NULL;                       -- no error, nothing to do
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'getting master oid';
            RAISE;                      -- call common exception handler
    END;

    -- set the actual object as read:
    l_retValue := p_setRead (l_oid, ai_userId);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ao_masterOid = ' || ao_masterOid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$getMasterOid',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$getMasterOid;
/

show errors;


/******************************************************************************
 * Checks a business object out (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be retrieved.
 * @param   ai_userId           ID of the user who is retrieving the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @output parameters:
 * @param   ao_creationDate     Date when the object was checked out.
 *
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_INSUFFICIENT_RIGHTS   User has no right to perform action.
 *  c_OBJECTNOTFOUND        The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Object$checkOut
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    -- output parameters:
    ao_checkOutDate         OUT ibs_CheckOut_01.checkout%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ISCHECKEDOUT            CONSTANT INTEGER := 16;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_oid                   ibs_Object.oid%TYPE;
    l_coUserId              ibs_Checkout_01.userId%TYPE := 0;
                                            -- user who checked the object out


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get user who had checked out the object:
        SELECT  userId
        INTO    l_coUserId
        FROM    ibs_CheckOut_01
        WHERE   oid = l_oid;

        l_rowCount := 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not checked out?
            l_rowCount := 0;
        WHEN TOO_MANY_ROWS THEN         -- more than one checkout user found?
            l_rowCount := 2;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get checkout user';
            RAISE;                      -- call common exception handler
    END;

    -- check if the object is checked out:
    IF (l_rowCount = 0)                 -- the actual object is not checked out?
    THEN
        -- get rights for the user:
        l_rights := p_Rights$checkRights
            (l_oid, c_NOOID, ai_userId, ai_op, l_rights);

        -- check if the user has the necessary rights:
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN
            BEGIN
                -- set the flag:
                UPDATE  ibs_Object
                SET     flags = B_OR (flags, c_ISCHECKEDOUT)
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'setting the flag';
                    RAISE;              -- call common exception handler
            END;

            -- get the current checkout date:
            ao_checkOutDate := SYSDATE;

            -- add the new tuple to the ibs_CheckOut table:
            BEGIN
                INSERT INTO ibs_Checkout_01
                        (oid, userId, checkout)
                VALUES  (l_oid, ai_userId, ao_checkOutDate);
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'adding ibs_CheckOut_01 entry';
                    RAISE;              -- call common exception handler
            END;

            -- make changes permanent:
            COMMIT WORK;
        -- end if the user has the rights
        ELSE                            -- the user does not have the rights
            -- set return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights
    -- end if the actual object is not checked out
    ELSIF (l_coUserId = ai_userId)      -- checked out by this user?
    THEN
        -- set return value:
        l_retValue := c_ALL_RIGHT;
    -- end elsif checked out by this user
    ELSE                                -- checked out by another user
        -- set return value:
        l_retValue := c_OBJECTNOTFOUND;
    END IF; -- else checked out by another user

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ao_checkOutDate = ' || ao_checkOutDate ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$checkOut',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$checkOut;
/

show errors;


/******************************************************************************
 * Checks in a business object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be checked in.
 * @param   ai_userId           ID of the user who is checking in the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_INSUFFICIENT_RIGHTS   User has no right to perform action.
 *  c_OBJECTNOTFOUND        The required object was not found within the
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Object$checkIn
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_oid                   ibs_Object.oid%TYPE;
    l_coUserId              ibs_Checkout_01.userId%TYPE := 0;
                                            -- user who checked the object out


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get user who had checked out the object:
        SELECT  userId
        INTO    l_coUserId
        FROM    ibs_CheckOut_01
        WHERE   oid = l_oid;

        l_rowCount := 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not checked out?
            l_rowCount := 0;
        WHEN TOO_MANY_ROWS THEN         -- more than one checkout user found?
            l_rowCount := 2;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get checkout user';
            RAISE;                      -- call common exception handler
    END;

    -- check if the object is checked out by the current user:
    IF (l_coUserId = ai_userId)         -- user has checked out the object?
    THEN
        -- get rights for the user:
        l_rights := p_Rights$checkRights
            (l_oid, c_NOOID, ai_userId, ai_op, l_rights);

        -- check if the user has the necessary rights:
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN
            BEGIN
                -- remove the bit for checked out:
                UPDATE  ibs_Object
--
-- CHANGED because OF internal ORACLE error using BITAND
-- only values up to 2147483647 (= 0x7FFFFFFF) can be used (without highest bit!)
--
--                  SET      flags = B_AND (flags, B_XOR (4294967295, c_ISCHECKEDOUT)) -- 0xFFFFFFFF
--
                SET     flags = B_AND (flags, B_XOR (2147483647, c_ISCHECKEDOUT)) -- 0x7FFFFFFF
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'dropping checkout bit';
                    RAISE;              -- call common exception handler
            END;

            BEGIN
                -- remove the tuple from the ibs_CheckOut table:
                DELETE  ibs_Checkout_01
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'deleting ibs_CheckOut_01 entry';
                    RAISE;              -- call common exception handler
            END;

            -- make changes permanent:
            COMMIT WORK;
        -- end if the user has the rights
        ELSE                            -- the user does not have the rights
            -- set return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights
    -- end if user has checked out the object
    ELSE                                -- another user has checked out the
                                        -- object or object not checked out
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF; -- else another user has checked out the...

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$checkIn',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$checkIn;
/

show errors;


/******************************************************************************
 * Returns the OID of an object, regarding to its name. <BR>
 *
 * @input parameters:
 * @param   ai_name             Name of the object that should be found.
 * @param   ai_userName         Name of the user (only set, if searching for a
 *                              private object).
 * @param   ai_domainId         Id of the domain where the object should be
                                found (only set at the search for the root).
 * @param   ai_actOid_s         Oid of the last object found.
 *
 * @output parameters:
 * @param   ao_oid_s            Oid of the object which was found.
 * @return  A value representing the state of the procedure.
 *  OBJECTNOTFOUND          The object was not found.
 *  TOOMANYROWS             More than 1 object was found.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 ******************************************************************************
 */
-- create the new function:
CREATE OR REPLACE FUNCTION p_Object$resolvePath
(
    -- input parameters:
    ai_name                 ibs_Object.name%TYPE,
    ai_userName             ibs_User.name%TYPE,
    ai_domainId             ibs_Domain_01.id%TYPE,
    ai_actOid_s             VARCHAR2,
    -- output parameter
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_TOOMANYROWS           CONSTANT INTEGER := 5; -- too many rows found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_actOid                ibs_Object.oid%TYPE; -- oid of the actual object
    l_oid                   ibs_Object.oid%TYPE; -- oid of the found object
    l_userName              ibs_User.name%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_actOid_s, l_actOid);

    -- this is a hack!! (HB)
    -- it is not possible to compare an empty username ('') with ''
    l_userName := '*' || ai_userName || '*';

    -- check for domain:
    IF (ai_domainId = -1)               -- all domains?
    THEN
        BEGIN
            -- get the oid of the object:
            SELECT  oid
            INTO    l_oid
            FROM    ibs_Object
            WHERE   name = ai_name
                AND containerId = l_actOid
                AND state = 2;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- object not found?
                -- set return value:
                l_retValue := c_OBJECTNOTFOUND;
                -- create error entry:
                l_ePos := 'getting oid';
                RAISE;                  -- call common exception handler
            WHEN TOO_MANY_ROWS THEN     -- more than one object found?
                -- set return value:
                l_retValue := c_TOOMANYROWS;
                -- create error entry:
                l_ePos := 'getting oid';
                RAISE;                  -- call common exception handler
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'getting oid';
                RAISE;                  -- call common exception handler
        END;
    -- end if all domains
    ELSE                                -- specific domain
        IF ((l_userName = '* *') OR (l_userName = '**')) -- not in private area?
        THEN
            BEGIN
                -- get the oid of the object within the proper domain:
                SELECT  o.oid
                INTO    l_oid
                FROM    ibs_Object o, ibs_Domain_01 d
                WHERE   d.id = ai_domainId
                    AND o.name = ai_name
                    AND o.containerId = d.oid
                    AND o.state = 2;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN -- object not found?
                    -- set return value:
                    l_retValue := c_OBJECTNOTFOUND;
                    -- create error entry:
                    l_ePos := 'getting oid within domain';
                    RAISE;              -- call common exception handler
                WHEN TOO_MANY_ROWS THEN -- more than one object found?
                    -- set return value:
                    l_retValue := c_TOOMANYROWS;
                    -- create error entry:
                    l_ePos := 'getting oid within domain';
                    RAISE;              -- call common exception handler
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'getting oid within domain';
                    RAISE;              -- call common exception handler
            END;
        -- end if not in private area
        ELSE                            -- private area
            BEGIN
                -- get the oid for the workspace of the user with the given
                -- username:
                SELECT  w.workspace
                INTO    l_oid
                FROM    ibs_Workspace w, ibs_User u, ibs_Object o
                WHERE   u.domainId = ai_domainId
                    AND u.name = ai_userName
                    AND w.userId = u.id
                    AND o.oid = w.workspace
                    AND o.state = 2;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN -- object not found?
                    -- set return value:
                    l_retValue := c_OBJECTNOTFOUND;
                    -- create error entry:
                    l_ePos := 'getting private oid';
                    RAISE;              -- call common exception handler
                WHEN TOO_MANY_ROWS THEN -- more than one object found?
                    -- set return value:
                    l_retValue := c_TOOMANYROWS;
                    -- create error entry:
                    l_ePos := 'getting private oid';
                    RAISE;              -- call common exception handler
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'getting private oid';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- else private area
    END IF; -- else specific domain

    -- convert the oid to string representation:
    p_byteToString (l_oid, ao_oid_s);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_name = ' || ai_name ||
            ', ai_userName = ' || ai_userName ||
            ', ai_domainId = ' || ai_domainId ||
            ', ai_actOid_s = ' || ai_actOid_s ||
            ', ao_oid_s = ' || ao_oid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$resolvePath',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$resolvePath;
/

show errors;


/******************************************************************************
 * Returns the OID of a object, regarding to its name. <BR>
 *
 * @input parameters:
 * @param   ai_name             Name of the object, that should be found.
 * @param   ai_userName         Name of the user (only set, if searching for a
                                private object).
 * @param   ai_domainId         Id of the domain, where the object should be
                                found (only set at the search for the root).
 * @param   ai_actOid_s         Oid of the last object found.
 *
 * @output parameters:
 * @param   ao_oid_s            Oid of the object which was found.
 * @param   ao_isContainer      1 if the object is a container otherwise 0.
 *
 * @return  A value representing the state of the procedure.
 *  OBJECTNOTFOUND          The object was not found.
 *  TOOMANYROWS             More than 1 object was found.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 ******************************************************************************
 */
-- create the new function:
CREATE OR REPLACE FUNCTION p_Object$resolveObjectPath
(
    -- input parameters:
    ai_name                 ibs_Object.name%TYPE,
    ai_userName             ibs_User.name%TYPE,
    ai_domainId             ibs_Domain_01.id%TYPE,
    ai_actOid_s             VARCHAR2,
    -- output parameter
    ao_oid_s                OUT VARCHAR2,
    ao_isContainer          OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_TOOMANYROWS           CONSTANT INTEGER := 5; -- too many rows found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_actOid                ibs_Object.oid%TYPE; -- oid of the actual object
    l_oid                   ibs_Object.oid%TYPE; -- oid of the found object
    l_userName              ibs_User.name%TYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_actOid_s, l_actOid);

    -- this is a hack!! (HB)
    -- it is not possible to compare an empty username ('') with ''
    l_userName := '*' || ai_userName || '*';

    -- check for domain:
    IF (ai_domainId = -1)               -- all domains?
    THEN
        BEGIN
            -- get the oid of the object:
            SELECT  oid, isContainer
            INTO    l_oid, ao_isContainer
            FROM    ibs_Object
            WHERE   name = ai_name
                AND containerId = l_actOid
                AND state = 2;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- object not found?
                -- set return value:
                l_retValue := c_OBJECTNOTFOUND;
                -- create error entry:
                l_ePos := 'getting oid';
                RAISE;                  -- call common exception handler
            WHEN TOO_MANY_ROWS THEN     -- more than one object found?
                -- set return value:
                l_retValue := c_TOOMANYROWS;
                -- create error entry:
                l_ePos := 'getting oid';
                RAISE;                  -- call common exception handler
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'getting oid';
                RAISE;                  -- call common exception handler
        END;
    -- end if all domains
    ELSE                                -- specific domain
        IF ((l_userName = '* *') OR (l_userName = '**')) -- not in private area?
        THEN
            BEGIN
                -- get the oid of the object within the proper domain:
                SELECT  o.oid, o.isContainer
                INTO    l_oid, ao_isContainer
                FROM    ibs_Object o, ibs_Domain_01 d
                WHERE   d.id = ai_domainId
                    AND o.name = ai_name
                    AND o.containerId = d.oid
                    AND o.state = 2;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN -- object not found?
                    -- set return value:
                    l_retValue := c_OBJECTNOTFOUND;
                    -- create error entry:
                    l_ePos := 'getting oid within domain';
                    RAISE;              -- call common exception handler
                WHEN TOO_MANY_ROWS THEN -- more than one object found?
                    -- set return value:
                    l_retValue := c_TOOMANYROWS;
                    -- create error entry:
                    l_ePos := 'getting oid within domain';
                    RAISE;              -- call common exception handler
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'getting oid within domain';
                    RAISE;              -- call common exception handler
            END;
        -- end if not in private area
        ELSE                            -- private area
            BEGIN
                -- get the oid for the workspace of the user with the given
                -- username:
                SELECT  w.workspace, o.isContainer
                INTO    l_oid, ao_isContainer
                FROM    ibs_Workspace w, ibs_User u, ibs_Object o
                WHERE   u.domainId = ai_domainId
                    AND u.name = ai_userName
                    AND w.userId = u.id
                    AND o.oid = w.workspace
                    AND o.state = 2;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN -- object not found?
                    -- set return value:
                    l_retValue := c_OBJECTNOTFOUND;
                    -- create error entry:
                    l_ePos := 'getting private oid';
                    RAISE;              -- call common exception handler
                WHEN TOO_MANY_ROWS THEN -- more than one object found?
                    -- set return value:
                    l_retValue := c_TOOMANYROWS;
                    -- create error entry:
                    l_ePos := 'getting private oid';
                    RAISE;              -- call common exception handler
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'getting private oid';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- else private area
    END IF; -- else specific domain

    -- convert the oid to string representation:
    p_byteToString (l_oid, ao_oid_s);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_name = ' || ai_name ||
            ', ai_userName = ' || ai_userName ||
            ', ai_domainId = ' || ai_domainId ||
            ', ai_actOid_s = ' || ai_actOid_s ||
            ', ao_oid_s = ' || ao_oid_s ||
            ', ao_isContainer = ' || ao_isContainer ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$resolveObjectPath',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Object$resolveObjectPath;
/

show errors;

EXIT;
