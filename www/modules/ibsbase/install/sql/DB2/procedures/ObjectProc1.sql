--------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_Object table. <BR>
--
-- @version     $Revision: 1.7 $, $Date: 2003/10/21 22:37:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020826
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- An empty Dummy because of a cyclic dependency. <BR>
--
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    RETURN 0;
END;
-- p_Object$create


--------------------------------------------------------------------------------
-- Get the oid of a specific tab of an object. <BR>
-- If the tab does not exist for this object or the tab itself is not an object
-- there is no oid available an OBJECTNOTFOUND ist returned.
--
-- @input parameters:
-- @param   ai_oid              Id of the object for which to get the tab oid.
-- @param   ai_tabCode          The code of the tab (as it is in ibs_Tab).
--
-- @output parameters:
-- @param   ao_tabOid           The oid of the tab object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  OBJECTNOTFOUND          The tab object was not found.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$getTabOid');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$getTabOid
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_tabCode          VARCHAR (63),
    -- output parameters:
    OUT ao_tabOid           CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_CONT_PARTOF   INT DEFAULT 2;  -- containerKind part of

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rowCount      INT DEFAULT 0;  -- row counter
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_oid_s        VARCHAR (18);
    DECLARE el_tabOid_s     VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET el_oid_s            = c_NOOID_s;
    SET el_tabOid_s         = c_NOOID_s;
    SET ao_tabOid           = c_NOOID;
  
-- body:
    -- get the oid of the tab object:
    SET l_sqlcode = 0;
    SELECT  o.oid 
    INTO    ao_tabOid
    FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_ConsistsOf c,
            IBSDEV1.ibs_Tab t
    WHERE   o.containerId = ai_oid
        AND o.containerKind = c_CONT_PARTOF
        AND o.consistsOfId = c.id
        AND c.tabId = t.id
        AND t.code = ai_tabCode;

    -- check if there occurred an error:
    IF (l_sqlcode = 100)                -- the tab object was not found?
    THEN
        -- set corresponding return value:
        SET l_retValue = c_OBJECTNOTFOUND;
    -- end if the tab object was not found
    ELSEIF (l_sqlcode <> 0)             -- any other error?
    THEN
        -- create error entry:
        SET l_ePos = 'get tab oid';
        GOTO exception1;                -- call common exception handler
    END IF; -- else if any other error

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL p_binaryToHexString (ai_oid, el_oid_s);
    CALL p_binaryToHexString (ao_tabOid, el_tabOid_s);
    CALL IBSDEV1.logError (500, 'p_Object$getTabOid', l_sqlcode, l_ePos,
        '', 0, 'ai_tabCode', ai_tabCode,
        '', 0, 'ai_oid', el_oid_s,
        '', 0, 'ao_tabOid', el_tabOid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Object$getTabOid


--------------------------------------------------------------------------------
-- Create a tab for an object. <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure ensures that a specific tab for an object exists. If the tab
-- is already there nothing is done.
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is creating the tab.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_oid              Id of the object for which the tab shall be 
--                              generated.
-- @param   ai_tVersionId       Type of the object.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          There are no tabs to generate.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$createTab');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$createTab
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_tabCode          VARCHAR (63),
    -- output parameters:
    OUT ao_tabOid           CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_TK_OBJECT     INT DEFAULT 2;  -- tab kind Object
    DECLARE c_TK_LINK       INT DEFAULT 3;  -- tab kind Link
    DECLARE c_PROC_CREATE   VARCHAR (63) DEFAULT 'create';
                                            -- code for stored procedure which
                                            -- creates an object

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rowCount      INT DEFAULT 0;  -- row counter
    DECLARE l_oid_s         VARCHAR (18);   -- string representation of oid
    DECLARE l_tabOid_s      VARCHAR (18) DEFAULT ''; -- string representation of tab oid
    DECLARE l_consistsOfId  INT;            -- id of tab in ibs_ConsistsOf
    DECLARE l_tabTVersionId INT;            -- tVersionId of the actual tab
    DECLARE l_tabName       VARCHAR (63);   -- the tab's name
    DECLARE l_tabDescription VARCHAR (255); -- the tab's description
    DECLARE l_tabProc       VARCHAR (63);   -- name of stored procedure for
                                            -- creating the tab object
    DECLARE l_state         INT;            -- the state of the object where
                                            -- the tab belongs to and thus the
                                            -- state of the tab itself
    DECLARE l_cmdString     VARCHAR (2000); -- the actual command for execution
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_tabOid_s     VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid_s             = c_NOOID_s;
    SET l_tabOid_s          = c_NOOID_s;
    SET el_tabOid_s         = c_NOOID_s;
    SET ao_tabOid           = c_NOOID;

-- body:
    -- convert oid to string:
    CALL IBSDEV1.p_byteToString (ai_oid, l_oid_s);

    -- get the tab data:
    CALL IBSDEV1.p_Object$getTabOid (ai_oid, ai_tabCode, ao_tabOid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- check if the tab already exists:
    IF (l_retValue = c_OBJECTNOTFOUND)  -- tab does not exist yet?
    THEN
        -- re-initialize the return value:
        SET l_retValue = c_ALL_RIGHT;

        -- get the data of the tab:
        -- (recognize only known names of tabs which can be constructed by 
        -- p_Object$create)
        SET l_sqlcode = 0;
        SELECT  t.tVersionId, d.objName, d.objDesc, 
                COALESCE (p.name, 'p_Object$create'), c.id, o.state
        INTO    l_tabTVersionId, l_tabName, l_tabDescription, l_tabProc,
                l_consistsOfId, l_state
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_ConsistsOf c,
                IBSDEV1.ibs_ObjectDesc_01 d,
                IBSDEV1.ibs_Tab t
                LEFT OUTER JOIN IBSDEV1.ibs_TVersionProc p
                ON t.tVersionId = p.tVersionId 
        WHERE   o.oid = ai_oid
            AND c.tVersionId = o.tVersionId
            AND c.tabId = t.id
            AND t.kind IN (c_TK_OBJECT, c_TK_LINK)
            AND t.multilangKey = d.name
            AND p.code = c_PROC_CREATE
            AND t.code = ai_tabCode;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any error?
        THEN
            -- create error entry:
            SET l_ePos = 'get tab data';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any error

        -- create the tab:
        -- create statement:
        SET l_cmdString =
            'CALL IBSDEV1.' || l_tabProc || ' (' ||
                CHAR (ai_userId) || ', ' ||
                CHAR (ai_op) || ', ' ||
                CHAR (l_tabTVersionId) || ', ' ||
                '''' || l_tabName || ''', ' ||
                '''' || l_oid_s || ''', ' ||
                '2, 0, ' ||
                '''' || c_NOOID_s || ''', ' ||
                '''' || l_tabDescription || ''', ' ||
                '?)';
--CALL IBSDEV1.logError (100, 'p_Object$createTab', l_sqlcode, 'vor exec1', 'l_retValue', l_retValue, 'l_tabOid_s', l_tabOid_s, 'l_sqlcode', l_sqlcode, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        -- prepare statement for execution:
        PREPARE myStmt FROM l_cmdString;
--CALL IBSDEV1.logError (100, 'p_Object$createTab', l_sqlcode, 'vor exec2', 'l_retValue', l_retValue, 'l_tabOid_s', l_tabOid_s, 'l_sqlcode', l_sqlcode, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        -- execute the statement and get return value:
        EXECUTE myStmt USING l_tabOid_s;
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'p_Object$createTab', l_sqlcode, 'vor exec3', 'l_retValue', l_retValue, 'l_tabOid_s', l_tabOid_s, 'l_sqlcode', l_sqlcode, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
/*
        EXECUTE IMMEDIATE l_cmdString;
CALL IBSDEV1.logError (100, 'p_Object$createTab', l_sqlcode, 'nach exec', 'l_retValue', l_retValue, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
CALL IBSDEV1.logError (100, 'p_Object$createTab', l_sqlcode, 'nach exec + diag', 'l_retValue', l_retValue, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        CALL l_tabProc
            (ai_userId, ai_op, l_tabTVersionId, l_tabName, l_oid_s,
            2, 0, c_NOOID_s, l_tabDescription, l_tabOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
*/

        -- check if there occurred an error:
        IF (l_retValue <> c_ALL_RIGHT)
        THEN 
--CALL IBSDEV1.logError (100, 'p_Object$createTab', l_sqlcode, 'error', 'l_retValue', l_retValue, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            -- create error entry:
            SET l_ePos = 'create tab error';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- convert oid_s to oid:
        CALL IBSDEV1.p_stringToByte (l_tabOid_s, ao_tabOid);

        -- set the tab id and state:
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_Object
        SET     consistsOfId = l_consistsOfId,
                state = l_state
        WHERE   oid = ao_tabOid;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
  
        -- check if there occurred an error:
        IF (l_sqlcode = 100 OR l_rowCount <= 0) -- tab object not found?
        THEN
            -- create error entry:
            SET l_ePos = 'tab object not found';
            GOTO exception1;            -- call common exception handler
        -- end if tab object not found
        ELSEIF (l_sqlcode <> 0)         -- any other error?
        THEN
            -- create error entry:
            SET l_ePos = 'update consistsOfId';
            GOTO exception1;            -- call common exception handler
        END IF;-- if any other error
    END IF; -- if tab does not exist yet

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL p_binaryToHexString (ao_tabOid, el_tabOid_s);
    CALL IBSDEV1.logError (500, 'p_Object$createTab', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'l_oid_s', l_oid_s,
        'ai_userId', ai_userId, 'ai_tabCode', ai_tabCode,
        'ai_op', ai_op, 'l_tabOid_s', l_tabOid_s,
        '', 0, 'ao_tabOid', el_tabOid_s,
        '', 0, 'l_tabProc', l_tabProc,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Object$createTab


--------------------------------------------------------------------------------
-- Create the tabs for an object. <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for creating the tabs regarding a business object.
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is creating the tabs.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_oid              Id of the object for which the tabs shall be 
--                              generated.
-- @param   ai_tVersionId       Type of the object.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          There are no tabs to generate.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$createTabs');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$createTabs
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_tVersionId       INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_TK_OBJECT     INT DEFAULT 2;  -- tab kind Object
    DECLARE c_TK_LINK       INT DEFAULT 3;  -- tab kind Link

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rowCount      INT DEFAULT 0;  -- row counter
    DECLARE l_tabOid        CHAR (8) FOR BIT DATA; -- the oid of the tab object
    DECLARE l_tabCode       VARCHAR (63);   -- the code for the actual tab
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    DECLARE el_oid_s        VARCHAR (18);
    DECLARE el_tabOid_s     VARCHAR (18);

    -- define cursor for running through all tabs:
    DECLARE tabCursor INSENSITIVE CURSOR FOR 
        SELECT  t.code AS tabCode 
        FROM    IBSDEV1.ibs_ConsistsOf c, IBSDEV1.ibs_Tab t
        WHERE   c.tVersionId = ai_tVersionId
            AND c.tabId = t.id
            AND t.kind IN (c_TK_OBJECT, c_TK_LINK)
    FOR READ ONLY;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_tabOid            = c_NOOID;
    SET el_oid_s            = c_NOOID_s;
    SET el_tabOid_s         = c_NOOID_s;

-- body:
    -- open the cursor:
    OPEN tabCursor;

    -- get the first tab:
    SET l_sqlcode = 0;
    FETCH FROM tabCursor INTO l_tabCode;
    SET l_sqlstatus = l_sqlcode;

    -- check if there occurred and error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        SET l_ePos = 'error in fetch first';
        GOTO cursorException;           -- call common exception handler
    END IF;

    -- loop through all found tabs:
    WHILE (l_sqlstatus = 0)
    DO
--CALL IBSDEV1.logError (100, 'p_Object$createTabs', l_sqlcode, 'in while', '', 0, 'l_tabCode', l_tabCode, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        -- create the tab:
        CALL IBSDEV1.p_Object$createTab
                (ai_userId, ai_op, ai_oid, l_tabCode, l_tabOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- get the next tab:
        SET l_sqlcode = 0;
        FETCH FROM tabCursor INTO l_tabCode;
        SET l_sqlstatus = l_sqlcode;

        -- check if there occurred and error:
        IF (l_sqlcode NOT IN (0, 100, -501))
        THEN
            SET l_ePos = 'error in fetch';
            GOTO cursorException;       -- call common exception handler
        END IF;
    END WHILE;
CALL IBSDEV1.logError (100, 'p_Object$createTabs', l_sqlcode, 'tabs created', 'l_retValue', l_retValue, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    -- close the not longer needed cursor:
    CLOSE tabCursor;
    -- return the state value:
    RETURN l_retValue;

cursorException:
    -- close the not longer needed cursor:
    CLOSE tabCursor;
  
exception1:
    -- log the error:
    CALL p_binaryToHexString (ai_oid, el_oid_s);
    CALL p_binaryToHexString (l_tabOid, el_tabOid_s);
    CALL IBSDEV1.logError (500, 'p_Object$createTabs', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid', el_oid_s,
        'ai_op', ai_op, 'l_tabOid', el_tabOid_s,
        'ai_tVersionId', ai_tVersionId, 'l_tabCode', l_tabCode,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
  
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Object$createTabs


--------------------------------------------------------------------------------
-- Delete a tab of an object. <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for deleting a tab from an existing object.
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is deleting the tab.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_oid              Id of the object for which the tab shall be 
--                              deleted.
-- @param   ai_tabCode          The (unique) code of the tab.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          There are no tabs to generate.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$deleteTab');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$deleteTab
(
    -- input parameters:
    IN    ai_userId         INT,
    IN    ai_op             INT,
    IN    ai_oid            CHAR (8) FOR BIT DATA,
    IN    ai_tabCode        VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_PROC_DELETE   VARCHAR (63);

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rowCount      INT;
    DECLARE l_tabOid        CHAR (8) FOR BIT DATA;
    DECLARE l_tabOid_s      VARCHAR (18);
    DECLARE l_tabProc       VARCHAR (63);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_oid_s        VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
   -- assign constants:
    SET c_PROC_DELETE       = 'delete';

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rowCount          = 0;
    SET l_tabOid            = c_NOOID;
    SET l_tabOid_s          = c_NOOID_s;
    SET el_oid_s            = c_NOOID_s;

-- body:
    -- get the tab data:
    SET l_sqlcode = 0;
    SELECT  COALESCE (p.name, 'p_Object$delete'), o.oid
    INTO    l_tabProc, l_tabOid
    FROM    IBSDEV1.ibs_ConsistsOf c, IBSDEV1.ibs_Tab t, 
            IBSDEV1.ibs_Object o, INTOWDEV2.ibs_TVersionProc p
    WHERE   o.tVersionId = p.tVersionId
        AND o.containerId = ai_oid
        AND t.code = ai_tabCode
        AND o.consistsOfId = c.id
        AND c.tabId = t.id
        AND p.code = c_PROC_DELETE;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        SET l_ePos = 'update consistsOfId';
        GOTO exception1;
    END IF;

    -- check if the tab object exists:
    IF (l_sqlcode <> 100)               -- the tab object was found?
    THEN
        -- convert oid to oid_s:
        CALL IBSDEV1.p_byteToString (l_tabOid, l_tabOid_s);
        -- delete the tab object:
        CALL IBSDEV1.p_tabProc (l_tabOid_s, ai_userId, ai_op);
        GET DIAGNOSTICS l_sqlcode = RETURN_STATUS;
    ELSE 
        -- set return value:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- log the error:
    CALL p_binaryToHexString (ai_oid, el_oid_s);
    CALL IBSDEV1.logError (500, 'p_Object$deleteTab', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_tabCode', ai_tabCode,
        'ai_op', ai_op, 'l_tabOid_s', l_tabOid_s,
        '', 0, 'l_tabProc', l_tabProc,
        '', 0, 'ai_oid', el_oid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Object$deleteTab


-------------------------------------------------------------------------------
-- Insert protocol entry for object. <BR>
--
-- @input parameters:
-- @param   ai_oid              ID of the object for that a protocol entry must
--                              be inserted.
-- @param   ai_userId           ID of the user who is inserting.
-- @param   ai_op               Operation to be performed.
-- @param   ai_owner            The (new) owner of the object.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--/
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performInsertProtocol');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performInsertProtocol
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_owner            INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_eText         VARCHAR (5000); -- full error text
    DECLARE l_rowCount      INT DEFAULT 0;  -- number of rows
    DECLARE l_rights        INT DEFAULT 0;  -- the current rights
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_fullName      VARCHAR (255);
    DECLARE l_icon          VARCHAR (255);
    DECLARE l_name          VARCHAR (63);
    DECLARE l_tVersionId    INT;
    DECLARE l_containerKind INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_oid_s        VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_containerId       = c_NOOID;
    SET el_oid_s            = c_NOOID_s;

-- body:
    -- gather information for protocol entry:
    SET l_sqlcode = 0;
    SELECT  u.fullname, o.icon, o.name, o.tVersionId, o.containerId,
            o.containerKind
    INTO    l_fullName, l_icon, l_name, l_tVersionId, l_containerId,
            l_containerKind
    FROM    IBSDEV1.ibs_User u, IBSDEV1.ibs_Object o
    WHERE   u.id = ai_userId
        AND o.oid = ai_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)
    THEN
        IF (l_sqlcode = 100)
        THEN
            -- set return value:
            SET l_retValue = c_OBJECTNOTFOUND;
        END IF;

        -- create error entry:
        SET l_ePos = 'getting data';
        GOTO exception1;                -- call common exception handler
    END IF;
  
    -- add the new tuple to the ibs_Protocol_01 table:
    SET l_sqlcode = 0;
    INSERT INTO IBSDEV1.ibs_Protocol_01
            (fullName, userId, oid, objectName, icon, tVersionId,
            containerId, containerKind, owner, action, actionDate)
    VALUES  (l_fullName, ai_userId, ai_oid, l_name, l_icon, l_tVersionId,
            l_containerId, l_containerKind, ai_owner, ai_op, CURRENT TIMESTAMP);

    IF (l_sqlcode <> 0)
    THEN
        -- create error entry:
        SET l_ePos = 'create protocol entry';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- return the state value:
    RETURN    l_retValue;

exception1:
    -- log the error:
    CALL p_binaryToHexString (ai_oid, el_oid_s);
    CALL IBSDEV1.logError (500, 'p_Object$performInsertProtocol', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid', el_oid_s,
        'ai_op', ai_op, '', '',
        'ai_owner', ai_owner, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Object$performInsertProtocol


--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for creating a business object which contains
-- several other objects.
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              String representation of OID of the newly 
--                              created object.
-- [@param   @oid]              Oid of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performCreate');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performCreate
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18),
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission
    DECLARE c_ISCHECKEDOUT  INT DEFAULT 16; -- 5th bit of attribute 'flags'
    DECLARE c_EMPTYPOSNOPATH VARCHAR (4) DEFAULT '0000';

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rights        INT;            -- return value of rights proc.
    DECLARE l_co_userId     INT;
    DECLARE l_check         INT DEFAULT 0;  -- check out value
    DECLARE l_fullName      VARCHAR (63);
    DECLARE l_icon          VARCHAR (63);
    DECLARE l_name          VARCHAR (63);
    DECLARE l_description   VARCHAR (255);
    DECLARE l_op            INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    --  TRIGGER variables: used by trigger reimplementation:
    DECLARE l_id            INT;            -- the id of the inserted object
    DECLARE l_origId        INT;            -- originally set id
    DECLARE l_typeName      VARCHAR (63);    -- the name of the type
    DECLARE l_isContainer   SMALLINT;       -- is the object a container?
    DECLARE l_showInMenu    SMALLINT;       -- shall the object be shown in
                                            -- the menu?
    DECLARE l_showInNews    INT;            -- shall the object be shown in
                                            -- the news container?
    DECLARE l_oLevel        INT;            -- level of object within
                                            -- hierarchy
    DECLARE l_posNo         INT;            -- position of object within
                                            -- container
    DECLARE l_posNoHex      VARCHAR (4);     -- hex representation of posNo
    DECLARE l_posNoPath     VARCHAR (254);   -- the posNoPath
    DECLARE l_flags         INT;            -- the flag which are set
    DECLARE l_validUntil    TIMESTAMP;      -- date until which the object
                                            -- is valid
    DECLARE l_rKey          INT;            -- rights key
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_return        INT;
    DECLARE l_rowcount      INT;
    DECLARE el_oid_s        VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_rights            = 0;
    SET l_co_userId         = 0;
    SET l_check             = 0;
    SET l_fullName          = '';
    SET l_icon              = 'icon.gif';
    SET l_name              = ai_name;
    SET l_description       = ai_description;
    SET l_op                = ai_op;
    SET l_containerId       = c_NOOID;
    SET l_linkedObjectId    = c_NOOID;
    SET l_id                = 0;
    SET l_origId            = 0;
    SET l_typeName          = 'UNKNOWN';
    SET l_showInMenu        = 0;
    SET l_showInNews        = 0;
    SET l_oLevel            = 1;
    SET l_posNo             = 0;
    SET l_posNoHex          = '0000';
    SET l_posNoPath         = c_EMPTYPOSNOPATH;
    SET l_flags             = 0;
    SET l_validUntil        = CURRENT TIMESTAMP;
    SET l_rKey              = 0;
    SET el_oid_s            = c_NOOID_s;
    SET ao_oid_s            = c_NOOID_s;
    SET ao_oid              = c_NOOID;

-- body:
CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'begin', 'ai_userId', ai_userId, 'ai_name', ai_name, 'ai_op', ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_containerKind', ai_containerKind, 'ai_description', ai_description, 'ai_tVersionId', ai_tVersionId, 'ai_linkedObjectId_s', ai_linkedObjectId_s, 'ai_isLink', ai_isLink, 'ao_oid_s', ao_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'p_stringToByte', '',0, 'ai_containerId_s', ai_containerId_s, '',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- retrieve check-out-info for new objects container?
    SET l_sqlcode = 0;
    SELECT  co.userId, IBSDEV1.b_AND (o.flags, c_ISCHECKEDOUT)
    INTO    l_co_userId, l_check
    FROM    IBSDEV1.ibs_CheckOut_01 co, IBSDEV1.ibs_Object o
    WHERE   o.oid = l_containerId
        AND co.oid = o.oid;

    -- check if there occurred an error:
    IF (l_sqlcode = 100)                -- no data found?
    THEN 
        SET l_check = 0;
    -- end if no data found
    ELSEIF (l_sqlcode <> 0)
    THEN
        -- create error entry:
        SET l_ePos = 'get checkout user data';
        GOTO exception1;                -- call common exception handler
    END IF;

--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'is the object checked out?', 'ai_userId',ai_userId, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- is the object checked out?
    IF (l_check = c_ISCHECKEDOUT AND l_co_userId <> ai_userId)
    THEN 
        -- current user is not check-out user
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'object is checked out', 'ai_userId',ai_userId, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    ELSE                                -- object not checked out or
                                        -- user is check-out user?
        -- now check if user has permission to create object

        -- check the container rights:
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Rights$checkRights
                (ao_oid, l_containerId, ai_userId, l_op, l_rights);
        GET DIAGNOSTICS l_return = RETURN_STATUS;
--        SET l_rights = l_return;

--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'rights', 'ai_userId',ai_userId, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', 'l_rights', l_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        -- check if the user has the necessary rights:
        IF (l_rights = l_op)            -- the user has the rights?
        THEN
            -- add the new tuple to the ibs_Object table:
---------
--
-- START get and calculate base-data
--         (old trigger functionality!)
--
            --
            -- 1. compute id and oid for new object
            --
            SET l_sqlcode = 0;
            SELECT  COALESCE (MAX (id), 0) + 1
            INTO    l_id
            FROM    IBSDEV1.ibs_Object;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)
            THEN
                -- create error entry:
                SET l_ePos = 'computing id';
                GOTO exception1;        -- call common exception handler
            END IF;

            CALL IBSDEV1.p_createOid (ai_tVersionId, l_id, ao_oid);
            CALL IBSDEV1.p_byteToString (ao_oid, ao_oid_s);

            --
            -- 2. compute olevel, posno and posnopath
            --
            -- derive position number from other objects within container:
            -- The posNo is one more than the actual highest posNo within the 
            -- container or 1 if there is no object within the container yet.
            SET l_sqlcode = 0;
            SELECT  COALESCE (MAX (posNo), 0) + 1
            INTO    l_posNo
            FROM    IBSDEV1.ibs_Object
            WHERE   containerId = l_containerId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)
            THEN
                -- create error entry:
                SET l_ePos = 'computing posNo-Information';
                GOTO exception1;        -- call common exception handler
            END IF;

            -- convert the position number into hex representation:
            CALL IBSDEV1.p_IntToHexString(l_posNo, l_posNoHex);

            -- derive position level and rkey from container:
            -- The level of an object is the level of the container plus 1
            -- or 0, if there is no container.
            SET l_sqlcode = 0;
            SELECT  COALESCE (oLevel, 0) + 1, rKey, validUntil
            INTO    l_oLevel, l_rKey, l_validUntil
            FROM    IBSDEV1.ibs_Object
            WHERE   oid = l_containerId;
            -- check if there were some data found:

            -- check if there occurred an error:
            IF (l_sqlcode = 100)        -- no data found?
            THEN 
                -- no container found for given object; 
                -- must be root-object
                SET l_oLevel = 1;
                SET l_rKey = 0;
            -- end if no data found
            ELSEIF (l_sqlcode <> 0)
            THEN
                -- create error entry:
                SET l_ePos = 'computing oLevel information';
                GOTO exception1;        -- call common exception handler
            END IF;

            -- calculate new position path:
            IF (l_containerId <> c_NOOID) -- object is within a container?
            THEN 
                -- compute the posNoPath as posNoPath of container
                -- concatenated by the posNo of this object:
                SET l_sqlcode = 0;
                SELECT  DISTINCT posNoPath || l_posNoHex
                INTO    l_posNoPath
                FROM    IBSDEV1.ibs_Object
                WHERE   oid = l_containerId;

                -- check if there occurred an error:
                IF (l_sqlcode = 100)    -- no data found?
                THEN 
                    -- compute the posNoPath as posNo of this object:
                    SET l_posNoPath = l_posNoHex;
                -- end if no data found
                ELSEIF (l_sqlcode <> 0)
                THEN
                    -- create error entry:
                    SET l_ePos = 'compute posNoPath';
                    GOTO exception1;    -- call common exception handler
                END IF;
            -- end if object is within a container
            ELSE                        -- object is not within a container
                                        -- i.e. it is on top level
                -- compute the posNoPath as posNo of this object:
                SET l_posNoPath = l_posNoHex;
            END IF;-- else object is not within a container


            --
            -- 3. get type-info: type name, icon and containerId, showInMenus
            --                   showInNews       
            --
            SET l_sqlcode = 0;
            SELECT  t.name, t.isContainer, t.showInMenu,
                    t.showInNews * 4, t.icon 
            INTO    l_typeName, l_isContainer, l_showInMenu,
                    l_showInNews, l_icon
            FROM    IBSDEV1.ibs_Type t, IBSDEV1.ibs_TVersion tv
            WHERE   tv.id = ai_tVersionId
                AND t.id = tv.typeId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)
            THEN
                -- create error entry:
                SET l_ePos = 'selecting type-information';
                GOTO exception1;        -- call common exception handler
            END IF;

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
                SET l_sqlcode = 0;
                SELECT  name, typeName, description, flags, icon, rKey
                INTO    l_name, l_typeName, l_description, l_flags, l_icon,
                        l_rKey
                FROM    IBSDEV1.ibs_Object
                WHERE   oid = l_linkedObjectId;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0)
                THEN
                    -- create error entry:
                    SET l_ePos = 'get link data';
                    GOTO exception1;    -- call common exception handler
                END IF;
            ELSE
                IF ((l_name IS NULL) OR l_name = '' OR l_name = ' ')
                THEN 
                    SET l_name = l_typeName;
                END IF;
            END IF; -- if link object

            --
            -- 5. calculate new flags value: add showInNews
            --
            SET l_flags = b_AND (l_flags, 2147483643) + l_showInNews;
                                        -- 7FFFFFFB

--
-- END get and calculate base-data
--
---------     
--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'insert new information', 'ai_userId',ai_userId, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', 'l_rights', l_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

            --
            -- last but not least: insert new information
            --
            SET l_sqlcode = 0;
            INSERT INTO IBSDEV1.ibs_Object 
                    (id, oid, /*state,*/ tVersionId, typeName,
                    isContainer, name, containerId, containerKind,
                    isLink, linkedObjectId, showInMenu, flags,
                    owner, oLevel, posNo, posNoPath, creationDate,
                    creator, lastChanged, changer, validUntil,
                    description, icon, /*processState,*/ rKey)
            VALUES  (l_id, ao_oid, /*???,*/ ai_tVersionId, l_typeName,
                    l_isContainer, l_name, l_containerId, ai_containerKind,
                    ai_isLink, l_linkedObjectId, l_showInMenu, l_flags,
                    ai_userid, l_oLevel, l_posNo, l_posNoPath, CURRENT TIMESTAMP,
                    ai_userId, CURRENT TIMESTAMP, ai_userId, l_validUntil,
                    ai_description, l_icon, /*???,*/ l_rKey);

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)
            THEN
                -- create error entry:
                SET l_ePos = 'INSERT INTO ibs_Object';
                GOTO exception1;        -- call common exception handler
            END IF;

--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'after insert new information', 'ai_userId',ai_userId, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', 'l_rights', l_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

            --
            -- create tabs (if necessary)
            --
            -- check if creation of tabs is necessary:
            IF (ai_containerKind <> 2)  -- object is independent?
            THEN
                 -- create tabs for the object:
                CALL IBSDEV1.p_Object$createTabs
                        (ai_userId, ai_op, ao_oid, ai_tVersionId);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'after createTabs', 'l_retValue', l_retValue, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', 'l_rights', l_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

                -- check success:
                IF (l_retValue <> c_ALL_RIGHT)
                THEN
                    GOTO exception1; -- e_TAB_CREATION_ERROR;
                END IF;

                -- set the protocol entry:
                CALL p_Object$performInsertProtocol
                    (ao_oid, ai_userId, ai_op, ai_userId);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'after inserting in protocol', 'l_retValue', l_retValue, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', 'l_rights', l_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            -- end if object is independent
            ELSE                        -- object is a tab
                -- set return value: ok
                SET l_retValue = c_ALL_RIGHT;
            END IF; -- else object is a tab
        -- if the user has the rights
        ELSE                            -- the user does not have the rights
            -- user has not enough rights!
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
--CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'user has no rights', 'l_retValue', l_retValue, 'ai_name', ai_name, 'ai_op',ai_op, 'ai_containerId_s', ai_containerId_s, 'ai_tVersionId', ai_tVersionId, '', '', 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', 'l_rights', l_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        END IF; -- else the user does not have the rights
    END IF; -- else object not checked out or user is check-out user

CALL IBSDEV1.logError (100, 'p_Object$performCreate', l_sqlcode, 'end', 'l_retValue', l_retValue, 'ai_name', ai_name, 'ai_userId', ai_userId, 'ai_containerId_s', ai_containerId_s, 'ai_containerKind', ai_containerKind, 'ai_description', ai_description, 'ai_tVersionId', ai_tVersionId, 'ao_oid_s', ao_oid_s, 'l_check', l_check, '', '', 'l_co_userId', l_co_userId, '', '', 'l_rights', l_rights, '', '', 'ai_op', ai_op, '', '', 'ai_isLink', ai_isLink, '', '', '', 0, '', '');
    -- return the state value:
    RETURN l_retValue;

exception1:
-- e_TAB_CREATION_ERROR:
    -- log the error:
    CALL IBSDEV1.p_byteToString (ao_oid, el_oid_s);
    CALL IBSDEV1.logError (500, 'p_Object$performCreate', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_description', ai_description,
        'ai_containerKind', ai_containerKind, 'ai_containerId_s', ai_containerId_s,
        'ai_isLink', ai_isLink, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_tVersionId', ai_tVersionId, 'ao_oid', el_oid_s,
        'l_retValue', l_retValue, 'ao_oid_s', ao_oid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Object$performCreate


--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for creating a business object which contains
-- several other objects.
--
-- @input parameters:
-- @param   @oid                OID of the object to be created.
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              String representation of OID of the newly 
--                              created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performCreateWithId');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performCreateWithId
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- locla variables:
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_id            INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- get rights for this user
    CALL IBSDEV1.p_Rights$checkRights(ai_oid, l_containerId, ai_userId, ai_op, l_rights);
    GET DIAGNOSTICS l_rights = RETURN_STATUS;
    -- check if the user has the necessary rights
    IF l_rights = ai_op THEN 
        -- Dont't set the id because this can lead to inconsistencies!
        SET l_id = 0;
        INSERT INTO IBSDEV1.ibs_Object
            (id, oid, tVersionId, name, containerId, containerKind,
            isLink, linkedObjectId, owner, creator, changer,
            validUntil, description)
        VALUES (l_id, ai_oid, ai_tVersionId, ai_name, l_containerId, ai_containerKind,
            ai_isLink, l_linkedObjectId, ai_userId, ai_userId, ai_userId,
            CURRENT TIMESTAMP + 3 MONTH, ai_description);
    
        COMMIT;
        -- convert oid to oid_s:
        CALL IBSDEV1.p_byteToString (ai_oid, ao_oid_s);
    ELSE 
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$performCreateWithId


--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_retValue      INTEGER;        -- return value of function

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform the operation:
    CALL IBSDEV1.p_Object$performCreate (
        ai_userId, ai_op, ai_tVersionId, ai_name, ai_containerId_s,
        ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description,
        ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- make changes permanent:
    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Object$create


--------------------------------------------------------------------------------
-- Stores the attributes of an existing object (incl. rights check). <BR>
-- Creates the object if not yet existing.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$store');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$store
(
    IN    ai_oid_s          VARCHAR (18),
    IN    ai_userId         INT,
    IN    ai_op             INT,
    IN    ai_name           VARCHAR (63),
    IN    ai_validUntil     TIMESTAMP,
    IN    ai_description    VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_containerId       = c_NOOID;
    SET l_oid               = c_NOOID;

-- body:
    -- conversions: (VARCHAR (18)) - all input object ids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    SET l_sqlcode = 0;
    -- get container id of object:
    SELECT  l_containerId
    INTO    l_containerId
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid;

    -- check if the object exists:
    IF (l_sqlcode = 0)
    THEN
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights (l_oid, l_containerId, ai_userId,
            ai_op, l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;
        -- check if the user has the necessary rights
        IF (l_rights = ai_op)
        THEN
            -- update
            UPDATE  IBSDEV1.ibs_Object
            SET     name = ai_name,
                    lastChanged = CURRENT TIMESTAMP,
                    validUntil = ai_validUntil,
                    description = ai_description
            WHERE   oid = l_oid;
            COMMIT;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$store


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for changing a business object.
--
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be changed.
-- @param   ai_userId           ID of the user who is changing the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_name             Name of the object.
-- @param   ai_validUntil       Date until which the object is valid.
-- @param   ai_description      Description of the object.
-- @param   ai_showInNews       Display the object in the news.
--
-- @output parameters:
-- [@param   @oid]              Oid of the changed object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performChange');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performChange
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    -- output parameters:
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_ST_ACTIVE     INT;            -- active state
    DECLARE c_ST_CREATED    INT;            -- created state
    DECLARE c_INNEWS        INT;            -- bit value for showInNews

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_count         INT;            -- counter
    DECLARE l_rights        INT;            -- the current rights
    DECLARE l_coUserId      INT;            -- user who checked the object out
    DECLARE l_fullName      VARCHAR (63);    -- full name of user who changed the
                                            -- object
    DECLARE l_tVersionId    INT;            -- tVersionId of object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;        -- oid of container
    DECLARE l_containerKind INT;            -- kind of object/container
                                            -- relationship
    DECLARE l_icon          VARCHAR (63);    -- the icon of the object
    DECLARE l_owner         INT;            -- the owner of the object
    DECLARE l_state         INT;            -- the state of the object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE el_oid_s        VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_ST_ACTIVE         = 2;
    SET c_ST_CREATED        = 4;
    SET c_INNEWS            = 4;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_count             = 0;
    SET l_rights            = 0;
    SET l_coUserId          = 0;
    SET l_containerId       = c_NOOID;
    SET el_oid_s            = c_NOOID_s;
    SET ao_oid              = c_NOOID;

-- body:
    -- set a save point for the current transaction:
    -- conversions: (VARCHAR (18)) - all input object ids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, ao_oid);
    -- check if the object is checked out:
    SET l_sqlcode = 0;
    SELECT  userId
    INTO    l_coUserId
    FROM    IBSDEV1.ibs_Checkout_01
    WHERE   oid = ao_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        SET l_ePos = 'is the object checked out';
        GOTO exception1;
    END IF;

    -- check if the current user is allowed to access the object:
    IF (l_sqlcode <> 100 AND l_coUserId <> ai_userId)
    THEN
        -- set corresponding return value:
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
        GOTO exception1;
    END IF;

    -- get the data of the object:
    SET l_sqlcode = 0;
    SELECT  containerId, containerKind, tVersionId, icon, owner,
            state
    INTO    l_containerId, l_containerKind, l_tVersionId, l_icon, l_owner,
            l_state
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = ao_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        SET l_ePos = 'get the data of the object';
        GOTO exception1;
    END IF;

    -- check if the object exists:
    IF (l_sqlcode <> 100)
    THEN
        -- get rights for this user:
        CALL IBSDEV1.p_Rights$checkRights(ao_oid, l_containerId, ai_userId,
            ai_op, l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;

        -- check if the user has the necessary rights:
        IF (l_rights = ai_op)
        THEN
            -- check if the object is a tab:
            IF (l_containerKind = 2)
            THEN
                -- get the state of the container:
                SET l_sqlcode = 0;
                SELECT  state
                INTO    l_state
                FROM    IBSDEV1.ibs_Object
                WHERE   oid = ao_oid;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
                THEN
                    SET l_ePos = 'get the state of the container';
                    GOTO exception1;
                END IF;
            ELSE 
                IF (l_state = c_ST_CREATED)
                THEN
                    -- set object to active:
                    SET l_state = c_ST_ACTIVE;
                END IF;
            END IF;

            -- store the values of the object:
            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.ibs_Object
            SET     name = ai_name,
                    validUntil = ai_validUntil,
                    description = ai_description,
                    lastChanged = CURRENT TIMESTAMP,
                    state = l_state,
                    changer = ai_userId
            WHERE   oid = ao_oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                SET l_ePos = 'store the values';
                GOTO exception1;
            END IF;

            -- set the showInNews flag:
            IF (ai_showInNews = 1)
            THEN
                -- set the showInNews bit:
                SET l_sqlcode = 0;
                UPDATE  IBSDEV1.ibs_Object
                SET     flags = b_AND( flags, c_INNEWS )
                WHERE   oid = ao_oid;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
                THEN
                    SET l_ePos = 'set the showInNews bit';
                    GOTO exception1;
                END IF;
            ELSE 
                -- drop the showInNews bit:
                SET l_sqlcode = 0;
                UPDATE  IBSDEV1.ibs_Object
                SET     flags =
                            b_AND (flags,
                                b_XOR (IBSDEV1.p_hexStringToInt ('7FFFFFFF'),
                                       c_INNEWS)
                                  )
                WHERE   oid = ao_oid;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
                THEN
                    SET l_ePos = 'drop the showInNews bit';
                    GOTO exception1;
                END IF;
            END IF;

            -- ensure that the tab objects have the correct state:
            -- set the state for all tab objects, which have incorrect
            -- states
            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.ibs_Object
            SET     state = l_state
            WHERE   containerId = ao_oid
                AND containerKind = 2
                AND state <> l_state
                AND (   state = c_ST_ACTIVE
                    OR  state = c_ST_CREATED
                    );

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                SET l_ePos = 'set states of tab objects';
                GOTO exception1;
            END IF;
      
            -- get the full name of the user:
            SET l_sqlcode = 0;
            SELECT  fullname
            INTO    l_fullName
            FROM    IBSDEV1.ibs_User
            WHERE   id = ai_userId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                SET l_ePos = 'get the full name of the user';
                GOTO exception1;
            END IF;

            -- create protocol entry:
            INSERT INTO IBSDEV1.ibs_Protocol_01
                    (fullName, userId, oid, objectName, icon,
                    tVersionId, containerId, containerKind,
                    owner, action, actionDate)
            VALUES  (l_fullName, ai_userId, ao_oid, ai_name, l_icon,
                    l_tVersionId, l_containerId, l_containerKind,
                    l_owner, ai_op, CURRENT TIMESTAMP);
      
            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                SET l_ePos = 'create protocol entry';
                GOTO exception1;
            END IF;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- roll back to the save point:
    ROLLBACK; 

    -- log the error:
    CALL p_binaryToHexString (ao_oid, el_oid_s);
    CALL IBSDEV1.logError (500, 'p_Object$performChange', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ai_name', ai_name,
        'ai_showInNews', ai_showInNews, 'ai_description', ai_description,
        '', 0, 'ao_oid', el_oid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
  
    -- set error code:
    IF l_retValue = c_ALL_RIGHT THEN 
        SET l_retValue = c_NOT_OK;
    END IF;
    -- return error code:
    RETURN l_retValue;
END;
-- p_Object$performChange


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         Should the currrent object displayed in the news.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- finish the transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$change


--------------------------------------------------------------------------------
-- Move an existing object to another container (incl. rights check). <BR>
-- All sub structures are moved with the object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be moved.
-- @param   @userId             ID of the user who is moving the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @containerId_s      ID of the container where object shall be
--                              moved to.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$move');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$move
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_containerId_s    VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_CUT_FAIL_ERROR INT DEFAULT 11;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission
    DECLARE c_CHECKEDOUT    INT DEFAULT 16;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_oldContainerId CHAR (8) FOR BIT DATA;
    -- id of container where the object
    -- resides
    DECLARE l_flag          INT DEFAULT 0;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_posNoPathTarget VARCHAR (254);
    DECLARE l_co_userId     INT DEFAULT 0;
    DECLARE l_check         INT DEFAULT 0;
    DECLARE l_tVersionId    INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oldContainerId    = c_NOOID;
    SET l_oid               = c_NOOID;
    SET l_containerId       = c_NOOID;

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);

    -- is the object checked out?
    SELECT  co.userId, b_AND (o.flags, c_CHECKEDOUT)
    INTO    l_co_userId, l_check
    FROM    IBSDEV1.ibs_Object o
            INNER JOIN IBSDEV1.ibs_Checkout_01 co ON o.oid = co.oid
    WHERE   o.oid = l_oid;
  
    IF (l_check = c_CHECKEDOUT AND l_co_userId <> ai_userId)
    THEN
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    ELSE 
        -- get the actual container id of object:
        SET l_sqlcode = 0;

        SELECT  containerId
        INTO    l_oldContainerId
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_oid;

        -- check if the object exists:
        IF (l_sqlcode = 0)
        THEN
            -- get rights for this user
            CALL IBSDEV1.p_Rights$checkRights (l_oid, l_oldContainerId,
                ai_userId, ai_op, l_rights);
            GET DIAGNOSTICS l_rights = RETURN_STATUS;

            -- check if the user has the necessary rights
            IF (l_rights = ai_op)
            THEN
                SELECT  posNoPath
                INTO    l_posNoPath
                FROM    IBSDEV1.ibs_Object
                WHERE   oid = l_oid;

                SELECT  posNoPath
                INTO    l_posNoPathTarget
                FROM    IBSDEV1.ibs_Object
                WHERE   oid = l_containerId;
        
                IF (POSSTR (l_posNoPathTarget, l_posNoPath) <> 1)
                THEN
                    -- update
                    UPDATE  ibs_Object
                    SET     containerId = l_containerId
                    WHERE   oid = l_oid;
-- ****************************************************************************
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******

                    -- Ist leider notwendig, da auf den Sonderfall eines
                    -- Attachments keine gesonderte abfrage erfollgt und
                    -- so die Icons nicht gelöscht (Infoicon bei altem
                    -- Dokument) bzw. doppelt gesetz (MasterIcon bei neuem
                    -- Dokument) werden.
 
                    -- get the tVerionId of the object
                    SELECT  tVersionId
                    INTO    l_tVersionId
                    FROM    IBSDEV1.ibs_Object
                    WHERE   oid = l_oid;
          
                    -- ensures that the flags and a master are set
                    IF (l_tVersionId = 16842833)
                    THEN
                        -- ensures that the old attachment owner has the
                        -- right flags set
                        CALL IBSDEV1.p_Attachment_01$ensureMaster
                            (l_oldContainerId, NULL);
                        -- ensures that the new attachment owner has the
                        -- right flags set
                        CALL IBSDEV1.p_Attachment_01$ensureMaster
                            (l_containerId, NULL);
                    END IF;
                ELSE
                    SET l_flag = 1;
                END IF;

                COMMIT;
            ELSE
                -- set the return value with the error code:
                SET l_retValue = c_INSUFFICIENT_RIGHTS;
            END IF;
        ELSE
            -- set the return value with the error code:
            SET l_retValue = c_OBJECTNOTFOUND;
        END IF;
    END IF;

    -- return the state value:
    IF (l_flag <> 1)
    THEN
        RETURN l_retValue;
    ELSE
        RETURN c_CUT_FAIL_ERROR;
    END IF;
END;
-- p_Object$move


--------------------------------------------------------------------------------
-- Move an existing object to another container. <BR>
-- All sub structures are moved with the object.
--
-- @input parameters:
-- @param   @oid                ID of the object to be moved.
-- @param   @targetId           ID of the container where object shall be
--                              moved to.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performMove');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performMove
(
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_targetId         CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE     INT;
    DECLARE l_sqlcode   INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- set the new containerId of the object:
    -- (the rest does the trigger)
    UPDATE  IBSDEV1.ibs_Object
    SET     containerId = ai_targetId
    WHERE   oid = ai_oid;

    COMMIT;
END;
-- p_Object$performMove


--------------------------------------------------------------------------------
-- Change the state of an existing object. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @state              The new state of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$changeState');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$changeState
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_state            INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission
    DECLARE c_ST_ACTIVE     INT DEFAULT 2;
    DECLARE c_ST_CREATED    INT DEFAULT 4;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_oldState      INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET l_containerId       = c_NOOID;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- get the actual container id and state of object:
    SET l_sqlcode = 0;

    SELECT  containerId, state
    INTO    l_containerId, l_oldState
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid;

    -- check if the object exists:
    IF (l_sqlcode = 0)
    THEN
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights (l_oid, l_containerId,
            ai_userId, ai_op, l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;

        -- check if the user has the necessary rights
        IF (l_rights = ai_op)
        THEN
            -- check if the state transition from the actual state to the new
            -- state is allowed:
            -- not implemented yet
            -- set the new state for the object and all tabs:
            UPDATE  IBSDEV1.ibs_Object
            SET     state = ai_state
            WHERE   oid = l_oid
                OR  (   containerId = l_oid
                    AND containerKind = 2
                    AND state <> ai_state
                    AND (   state = c_ST_ACTIVE
                        OR  state = c_ST_CREATED
                        )
                    );
            COMMIT;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$changeState


--------------------------------------------------------------------------------
-- Change the processState of an existing object. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @processState       The new process state of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$changeProcessState');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$changeProcessState
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),    -- Objectid - String
    IN  ai_userId           INT,            -- UserId
    IN  ai_op               INT,            -- Operation
    IN  ai_processState     INT             -- new value for processState
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions: (VARCHAR (18)) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_oldProcState  INT;
    -- insert for protocolcontainer
    DECLARE l_fullName      VARCHAR (63);
    DECLARE l_icon          VARCHAR (63);
    DECLARE l_name          VARCHAR (63);
    DECLARE l_tVersionId    INT;
    DECLARE l_containerKind INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_containerId       = c_NOOID;
    SET l_oldProcState      = 0;

-- body:
    -- get the actual container id and state of object:
    SET l_sqlcode = 0;

    SELECT containerId, processState
    INTO l_containerId, l_oldProcState
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_oid;

    -- check if the object exists:
    IF l_sqlcode = 0 THEN 
        -- check if the state transition from the actual state to the new
        -- state is allowed:
        -- not implemented yet
        UPDATE IBSDEV1.ibs_Object
        SET processState = ai_processState
        WHERE oid = l_oid;
        COMMIT;
        -- read attributes needed for protocol out of table ibs_Object
        SELECT name, tVersionId, containerId, containerKind,  icon
        INTO l_name, l_tVersionId, l_containerId, l_containerKind, l_icon
        FROM IBSDEV1.ibs_Object
        WHERE oid = l_oid;
        -- read out the  fullName of the User
        SELECT u.fullname 
        INTO l_fullName
        FROM IBSDEV1.ibs_user u
        WHERE u.id = ai_userId;
    
        -- add the new tuple to the ibs_Object table:
        INSERT INTO IBSDEV1.ibs_Protocol_01
            ( fullName, userId, oid, objectName, icon, tVersionId,  
            containerId, containerKind, owner, action, actionDate)
        VALUES(l_fullName, ai_userId, l_oid, l_name, l_icon, l_tVersionId, 
            l_containerId, l_containerKind, ai_userId, ai_op,
            CURRENT TIMESTAMP);
        COMMIT;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$changeProcessState


--------------------------------------------------------------------------------
-- Change the owner of an existing object, including owner-information of
-- subsequent objects. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @owner              The new owner of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$changeOwnerRec');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$changeOwnerRec
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_owner            INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_oldOwner      INT;
    -- posNoPath of given object
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    -- actual owner of the object
    SET l_containerId       = c_NOOID;
    SET l_oldOwner          = 0;

-- body:
    -- get the actual container id and owner of object:
    SET l_sqlcode = 0;

    SELECT containerId, owner
    INTO l_containerId, l_oldOwner
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_oid;

    -- check if the object exists:
    IF l_sqlcode = 0 THEN 
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(l_oid, l_containerId, ai_userId,
            ai_op, l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;
        -- check if the user has the necessary rights
        IF l_rights = ai_op THEN 
            -- change the owner of the object
            UPDATE IBSDEV1.ibs_Object
            SET owner = ai_owner
            WHERE oid = l_oid;
            -- change owner of subsequent objects
            -- first get the posnopath information of the object
            SELECT posNoPath
            INTO l_posNoPath
            FROM IBSDEV1.ibs_object
            WHERE oid = l_oid;
            -- now change every object with coinciding posnopath-prefix
            UPDATE IBSDEV1.ibs_Object
            SET owner = ai_owner
            WHERE posNoPath LIKE l_posNoPath || '%';
      
            COMMIT;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$changeOwnerRec


--------------------------------------------------------------------------------
-- Change the owner of the tabs of an existing object. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @owner              The new owner of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$changeTabsOwner');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$changeTabsOwner
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_owner            INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_oldOwner      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_containerId       = c_NOOID;
    SET l_oldOwner          = 0;

-- body:
    -- get the actual container id and owner of object:
    SET l_sqlcode = 0;

    SELECT containerId, owner
    INTO l_containerId, l_oldOwner
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_oid;

    -- check if the object exists:
    IF l_sqlcode = 0 THEN 
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(l_oid, l_containerId, ai_userId,
            ai_op, l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;
        -- check if the user has the necessary rights
        IF l_rights = ai_op THEN 
            -- update
            UPDATE IBSDEV1.ibs_Object
            SET owner = ai_owner
            WHERE containerId = l_oid
                AND containerKind = 2;
            COMMIT;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$changeTabsOwner


--------------------------------------------------------------------------------
-- Insert protocol entry for object. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object for that a protocol entry must 
--                              be inserted.
-- @param   @userId             ID of the user who is inserting.
-- @param   @op                 Operation to be performed.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$insertProtocol');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$insertProtocol
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;  -- access permission
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;  -- access permission

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    -- insert for protocolcontainer
    DECLARE l_fullName      VARCHAR (63);
    DECLARE l_icon          VARCHAR (63);
    DECLARE l_name          VARCHAR (63);
    DECLARE l_tVersionId    INT;
    DECLARE l_containerKind INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    -- id of container where the object resides:
    SET l_containerId       = c_NOOID;

-- body:
    -- read out the  fullName of the User
    SELECT u.fullname, o.icon, o.name, o.tVersionId, o.containerId,
        o.containerKind 
    INTO l_fullName, l_icon, l_name, l_tVersionId, l_containerId,
        l_containerKind
    FROM IBSDEV1.ibs_user u, IBSDEV1.ibs_object o
    WHERE u.id = ai_userId
        AND o.oid = l_oid;
    -- add the new tuple to the ibs_Protocol_01 table:
    INSERT INTO IBSDEV1.ibs_Protocol_01
        ( fullName, userId, oid, objectName, icon, tVersionId,  
        containerId, containerKind, owner, action, actionDate)
    VALUES (l_fullName, ai_userId, l_oid, l_name, l_icon, l_tVersionId, 
        l_containerId, l_containerKind, ai_userId, ai_op, CURRENT TIMESTAMP);
  
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$insertProtocol


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for retrieving the data of a business object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             ID of the user who is retrieving the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerName      Name of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         Display object in the news?
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
-- [@param   @oid]              Oid of the retrieved object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--
-- @deprecated  This procedure is never used except p_Object$retrieve2.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performRetrieve2');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performRetrieve2
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_icon             VARCHAR (63),
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_VIEW    INT DEFAULT 2;  -- view permission
    DECLARE c_RIGHT_READ    INT DEFAULT 4;  -- read permission

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_INNEWS        INT;
    DECLARE l_ISCHECKEDOUT  INT;
    DECLARE tempName        VARCHAR (63);
    DECLARE tempOid         CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue      = c_ALL_RIGHT;
    SET l_rights        = 0;
    SET l_INNEWS        = 4;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, ao_oid);

    -- get container id of object
    SET l_sqlcode = 0;

    SELECT  containerId
    INTO    ao_containerId
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = ao_oid;

    IF (l_sqlcode = 0)
    THEN 
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(ao_oid, ao_containerId,
            ai_userId, ai_op, l_rights);
        -- check if the user has the necessary rights
        IF (l_rights = ai_op)
        THEN 
            -- get the data of the object and return it
            SELECT o.state, o.tVersionId, o.typeName, o.name, o.containerId,
                c.name, o.containerKind, o.isLink, o.linkedObjectId, o.owner,
                own.fullname, o.creationDate, o.creator, cr.fullname,
                o.lastChanged, o.changer, ch.fullname, o.validUntil,
                o.description, b_AND(o.flags, l_INNEWS),
                b_AND(o.flags, l_ISCHECKEDOUT), o.icon 
            INTO ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
                ao_containerName, ao_containerKind, ao_isLink,
                ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate,
                ao_creator, ao_creatorName, ao_lastChanged, ao_changer,
                ao_changerName, ao_validUntil, ao_description, ao_showInNews,
                ao_checkedOut, ao_icon
            FROM IBSDEV1.ibs_Object o LEFT OUTER JOIN
                IBSDEV1.ibs_Object c ON o.containerId = c.oid
                LEFT OUTER JOIN IBSDEV1.ibs_User own ON o.owner = own.id
                LEFT OUTER JOIN IBSDEV1.ibs_User cr ON o.creator = cr.id
                LEFT OUTER JOIN IBSDEV1.ibs_User ch ON o.changer = ch.id
            WHERE o.oid = ao_oid;

           IF ao_checkedOut = 1 THEN 
                -- get the info who checked out the object
                SELECT ch.checkout, ch.userId, u.oid, u.fullname 
                INTO ao_checkOutDate, ao_checkOutUser, tempOid, tempName
                FROM IBSDEV1.ibs_CheckOut_01 ch LEFT OUTER JOIN
                    IBSDEV1.ibs_User u ON u.id = ch.userid
                WHERE ch.oid = ao_oid;
                -- rights set for viewing the User?
                CALL IBSDEV1.p_Rights$checkRights (tempOid,
                    c_NOOID, ai_userId, c_RIGHT_VIEW, l_rights);
        
                -- check if the user has the necessary rights
                IF (l_rights = c_RIGHT_VIEW)
                THEN 
                    SET ao_checkOutUserName = tempName;
                END IF;

                -- rights set for reading the User?
                CALL IBSDEV1.p_Rights$checkRights (tempOid,
                    c_NOOID, ai_userId, c_RIGHT_READ, l_rights);

                -- check if the user has the necessary rights
                IF (l_rights = c_RIGHT_READ)
                THEN 
                    SET ao_checkOutUserName = tempName;
                    SET ao_checkOutUserOid = tempOid;
                END IF;
            END IF;

            -- set object as already read:
            CALL IBSDEV1.p_setRead(ao_oid, ai_userId);
        ELSE 
            -- get the default data of the object and return it
            SET ao_state = 0;
            SET ao_tVersionId = 0;
            SET ao_typeName = '';
            SET ao_name = '';
            SET ao_containerId = c_NOOID;
            SET ao_containerKind = 0;
            SET ao_isLink = 0;
            SET ao_linkedObjectId = c_NOOID;
            SET ao_owner = 0;
            SET ao_ownerName = '';
            SET ao_creationDate = CURRENT TIMESTAMP;
            SET ao_creator = 0;
            SET ao_creatorName = '';
            SET ao_lastChanged = CURRENT TIMESTAMP;
            SET ao_changer = 0;
            SET ao_changerName = '';
            SET ao_validUntil = CURRENT TIMESTAMP;
            SET ao_description = '';
            SET ao_showInNews = 0;
            SET ao_icon = NULL;
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$performRetrieve2


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for retrieving the data of a business object.
--
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be retrieved.
-- @param   ai_userId           ID of the user who is retrieving the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   ao_state            The object's state.
-- @param   ao_tVersionId       ID of the object's type (correct version).
-- @param   ao_typeName         Name of the object's type.
-- @param   ao_name             Name of the object itself.
-- @param   ao_containerId      ID of the object's container.
-- @param   ao_containerName    Name of the object's container.
-- @param   ao_containerKind    Kind of object/container relationship.
-- @param   ao_isLink           Is the object a link?
-- @param   ao_linkedObjectId   Link if isLink is true.
-- @param   ao_owner            ID of the owner of the object.
-- @param   ao_creationDate     Date when the object was created.
-- @param   ao_creator          ID of person who created the object.
-- @param   ao_lastChanged      Date of the last change of the object.
-- @param   ao_changer          ID of person who did the last change to the
--                              object.
-- @param   ao_validUntil       Date until which the object is valid.
-- @param   ao_description      Description of the object.
-- @param   ao_showInNews       Display object in the news?
-- @param   ao_checkedOut       Is the object checked out?
-- @param   ao_checkOutDate     Date when the object was checked out
-- @param   ao_checkOutUser     id of the user which checked out the object
-- @param   ao_checkOutUserOid  Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   ao_checkOutUserName Name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
-- [@param   ao_oid]            Oid of the retrieved object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performRetrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performRetrieve
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters:
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_RIGHT_VIEW    INT DEFAULT 2;  -- view permission
    DECLARE c_RIGHT_READ    INT DEFAULT 4;  -- read permission
    DECLARE c_INNEWS        INT;            -- bit value for showInNews
    DECLARE c_ISCHECKEDOUT  INT;            -- bit value for check out state

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_rights        INT;            -- the current rights
    DECLARE l_icon          VARCHAR (63);    -- the name of the icon
                                            -- (must be an output parameter!)
    DECLARE l_tempName      VARCHAR (63);    -- temporary name
    DECLARE l_tempOid       CHAR (8) FOR BIT DATA;        -- temporary oid
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_oid_s        VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_INNEWS            = 4;
    SET c_ISCHECKEDOUT      = 16;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rowCount          = 0;
    SET l_rights            = 0;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, ao_oid);
    -- get container id of object:
    SET l_sqlcode = 0;

    SELECT containerId
    INTO ao_containerId
    FROM IBSDEV1.ibs_Object
    WHERE oid = ao_oid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get the full name of the user';
        GOTO exception1;
    END IF;
    -- check if the object exists:
    IF l_sqlcode <> 100 THEN 
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(ao_oid, ao_containerId, ai_userId,
            ai_op, l_rights);
        -- check if the user has the necessary rights:
        IF l_rights = ai_op THEN 
            -- get the data of the object:
            SET l_sqlcode = 0;
            SELECT o.state, o.tVersionId, o.typeName, o.name, o.containerId,
                c.name, o.containerKind, o.isLink, o.linkedObjectId, o.owner,
                own.fullname, o.creationDate, o.creator, cr.fullname,
                o.lastChanged, o.changer, ch.fullname, o.validUntil,
                o.description, o.icon, b_AND(o.flags, c_INNEWS),
                b_AND(o.flags, c_ISCHECKEDOUT)
            INTO ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
                ao_containerName, ao_containerKind, ao_isLink,
                ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate,
                ao_creator, ao_creatorName, ao_lastChanged, ao_changer,
                ao_changerName, ao_validUntil, ao_description, l_icon,
                ao_showInNews, ao_checkedOut
            FROM IBSDEV1.ibs_Object o LEFT OUTER JOIN
                IBSDEV1.ibs_Object c ON o.containerId = c.oid 
                LEFT OUTER JOIN IBSDEV1.ibs_User own ON o.owner = own.id
                LEFT OUTER JOIN IBSDEV1.ibs_User cr ON o.creator = cr.id
                LEFT OUTER JOIN IBSDEV1.ibs_User ch ON o.changer = ch.id
            WHERE o.oid = ao_oid;
            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'get the data of the object';
                GOTO exception1;
            END IF;
            IF ao_checkedOut = 1 THEN 
                -- get the info who checked out the object
                SET l_sqlcode = 0;

                SELECT ch.checkout, ch.userId, u.oid, u.fullname 
                INTO ao_checkOutDate, ao_checkOutUser, l_tempOid, l_tempName
                FROM IBSDEV1.ibs_CheckOut_01 ch LEFT OUTER JOIN
                    IBSDEV1.ibs_User u ON u.id = ch.userid
                WHERE ch.oid = ao_oid;

                -- check if there occurred an error:
                IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                    SET l_ePos = 'get the check out info';
                    GOTO exception1;
                END IF;
        
                -- rights set for viewing the User?
                CALL IBSDEV1.p_Rights$checkRights(l_tempOid, c_NOOID, ai_userId,
                    c_RIGHT_VIEW, l_rights);
                -- check if the user has the necessary rights
                -- to see the user who checked out the object:
                IF l_rights = c_RIGHT_VIEW THEN 
                    SET ao_checkOutUserName = l_tempName;
                END IF;
                -- rights set for reading the User?
                CALL IBSDEV1.p_Rights$checkRights(l_tempOid, c_NOOID, ai_userId,
                    c_RIGHT_READ, l_rights);
                -- check if the user has the necessary rights
                -- to see the user who checked out the object:
                IF l_rights = c_RIGHT_READ THEN 
                    -- set the values:
                    SET ao_checkOutUserName = l_tempName;
                    SET ao_checkOutUserOid = l_tempOid;
                END IF;
            END IF;
            -- set object as already read:
            CALL IBSDEV1.p_setRead(ao_oid, ai_userId);
        ELSE 
            -- get the default data of the object and return it
            SET l_sqlcode = 0;
            SET ao_state = 0;
            SET ao_tVersionId = IBSDEV1.p_hexStringToInt('00');
            SET ao_typeName = '';
            SET ao_name = '';
            SET ao_containerId = c_NOOID;
            SET ao_containerKind = 0;
            SET ao_isLink = 0;
            SET ao_linkedObjectId = c_NOOID;
            SET ao_owner = IBSDEV1.p_hexStringToInt('00');
            SET ao_ownerName = '';
            SET ao_creationDate = CURRENT TIMESTAMP;
            SET ao_creator = IBSDEV1.p_hexStringToInt('00');
            SET ao_creatorName = '';
            SET ao_lastChanged = CURRENT TIMESTAMP;
            SET ao_changer = IBSDEV1.p_hexStringToInt('00');
            SET ao_changerName = '';
            SET ao_validUntil = CURRENT TIMESTAMP;
            SET ao_description = '';
            SET ao_showInNews = 0;
            SET ao_checkedOut = 0;
            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'set default return values';
                GOTO exception1;
            END IF;
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL p_binaryToHexString (ao_oid, el_oid_s);
    CALL IBSDEV1.logError (500, 'p_Object$performRetrieve', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ao_oid', el_oid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
  
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Object$performRetrieve


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             ID of the user who is retrieving the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerName      Name of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         Display the object in the news.
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
-- @param   @icon               Icon of the object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--
-- @deprecated  This procedure is never used.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$retrieve2');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$retrieve2
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_icon             VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    CALL IBSDEV1.p_Object$performRetrieve2 (ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
        ao_icon, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$retrieve2


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             ID of the user who is retrieving the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerName      Name of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         Display object in the news.
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$retrieve
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    CALL IBSDEV1.p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name,
        ao_containerId, ao_containerName, ao_containerKind, ao_isLink,
        ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate,
        ao_creator, ao_creatorName, ao_lastChanged, ao_changer, 
        ao_changerName, ao_validUntil, ao_description, ao_showInNews,
        ao_checkedOut, ao_checkOutDate, ao_checkOutUser, ao_checkOutUserOid,
        ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$retrieve


--------------------------------------------------------------------------------
-- Determine the oid of the object which is the next container above a given 
-- object. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object where the upper object's oid 
--                              shall be determined.
--
-- @output parameters:
-- @param   @upperOid           The upper object's oid.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$getUpperOid');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$getUpperOid
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    -- output parameters
    OUT ao_upperOid         CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_posNoPath     VARCHAR (254) DEFAULT '';
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;

-- body:
--CALL IBSDEV1.logError (100, 'p_Object$getUpperOid', l_sqlcode, '1', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- get posNoPath of actual object:
    SELECT  posNoPath
    INTO    l_posNoPath
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid;
  
    -- get the oid of the object which is the nearest container above the 
    -- actual object:
    SET l_sqlcode = 0;

    SELECT  containerId
    INTO    ao_upperOid
    FROM    IBSDEV1.ibs_Object
    WHERE   posNoPath =
            (
                SELECT  MAX (o.posNoPath) 
                FROM    IBSDEV1.ibs_Object o
                        LEFT OUTER JOIN
                        IBSDEV1.ibs_Object c ON o.containerId = c.oid
                WHERE   l_posNoPath LIKE (o.posNoPath || '%') 
                    AND o.containerKind = 1
                    AND NOT ( 
                                c.tVersionId = 16843777
                            OR  c.tVersionId = 16844033
                            OR  c.tVersionId = 16872721
                            )
            );

    -- check if the object exists:
    IF (l_sqlcode = 100)
    THEN 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$getUpperOid


--------------------------------------------------------------------------------
-- Determine the oid of the object which represents a tab of a given object
-- determined by the object's name. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object where the tab object's oid 
--                              shall be determined.
-- @param   @name               Name of the tab object.
--
-- @output parameters:
-- @param   @tabOid             The tab object's oid.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$getTabInfo');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$getTabInfo
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_operation        INT,
    IN  ai_userId           INT,
    IN  ai_name             VARCHAR (63),
    -- output parameters
    OUT ao_tabOid           CHAR (8) FOR BIT DATA,
    OUT ao_tabContent       INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- get the oid of the object which represents a tab of the actual object
    -- determined by its name:
    SET l_sqlcode = 0;
    SELECT oid
    INTO ao_tabOid
    FROM IBSDEV1.ibs_Object
    WHERE containerId = oid
         -- HACK HACK HACK HB wegen Umlauten ...
         AND name LIKE ai_name
        -- ... HACK HACK HACK HB wegen Umlauten
        AND containerKind = 2;
 
    -- check if the object exists:
    IF l_sqlcode = 100 THEN 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    ELSE 
        SELECT COUNT(*) 
        INTO ao_tabContent
        FROM IBSDEV1.v_Container$content
        WHERE containerid = ao_tabOid
            AND b_AND(rights, ai_operation) = ai_operation
            AND userid = ai_userid;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$getTabInfo


--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also deletes all links showing to this object. <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for deleting a business object which contains
-- several other objects.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- [@param   @oid]              Oid of the deleted object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performDelete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performDelete
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16; -- delete permission

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_oLevel        INT;
    -- value for checked out for the flags-bitarray
    DECLARE l_CHECKEDOUT    INT;
    -- declare local variables
    DECLARE l_co_userId     INT;
    DECLARE l_check         INT;
    -- HP 990830
    -- PROBLEM: protocol only for object - no subsequent objects!!!!
    DECLARE l_fullName      VARCHAR (63);
    DECLARE l_tVersionId    INT;
    DECLARE l_containerKind INT;
    DECLARE l_name          VARCHAR (63);
    DECLARE l_owner         INT;
    DECLARE l_icon          VARCHAR (63);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, ao_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_CHECKEDOUT        = 16;
    SET l_check             = 0;

-- body:
    -- is the object checked out?
    SELECT co.userId, b_AND(o.flags, l_CHECKEDOUT)
    INTO l_co_userId, l_check
    FROM IBSDEV1.ibs_Object o INNER JOIN
        IBSDEV1.ibs_Checkout_01 co ON o.oid = co.oid
    WHERE o.oid = ao_oid;

    IF l_check = l_CHECKEDOUT AND l_co_userId <> ai_userId THEN 
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    ELSE 
        -- get container id, posNoPath and level within tree of object
        SELECT containerId, posNoPath, oLevel
        INTO l_containerId, l_posNoPath, l_oLevel
        FROM IBSDEV1.ibs_Object
        WHERE oid = ao_oid;

        SELECT COUNT(*) 
        INTO l_rowcount
                        FROM IBSDEV1.ibs_Object
                        WHERE oid = ao_oid;
    
        -- check if the object exists:
        IF l_rowcount > 0 THEN 
        -- HP 990830    
        -- PROBLEM: only one object right checked here - no subsequent objects!
            -- get rights for this user
            CALL IBSDEV1.p_Rights$checkRights(ao_oid, l_containerId,
                ai_userId, ai_op, l_rights);
            -- check if the user has the necessary rights
            IF l_rights = ai_op THEN 
                -- read out the  fullName of the User
                SELECT u.fullname, icon, o.tVersionId, o.name,
                     o.owner, containerKind
                INTO l_fullName, l_icon, l_tVersionId, l_name, l_owner,
                    l_containerKind
                FROM IBSDEV1.ibs_user u, IBSDEV1.ibs_object o
                WHERE u.id = ai_userId AND o.oid = ao_oid;
        
                -- add the new tuple to the ibs_Object table:
                INSERT INTO IBSDEV1.ibs_Protocol_01
                    ( fullName, userId, oid, objectName, icon, tVersionId,  
                    containerId, containerKind, owner, action, actionDate)
                VALUES (l_fullName, ai_userId, ao_oid, l_name, l_icon,
                    l_tVersionId, l_containerId, l_containerKind, ai_userId,
                    ai_op, CURRENT TIMESTAMP );
        
        
                -- start deletion of object, subsequent objects AND references
                -- to deleted objects
                -- mark object and subsequent objects as 'deleted' via
                -- posnopath
                UPDATE IBSDEV1.ibs_Object
                SET state = 1,
                    changer = ai_userId,
                    lastChanged = CURRENT TIMESTAMP
                WHERE posNoPath LIKE ( l_posNoPath || '%');
                -- mark references to the object as 'deleted'
                -- ATTENTION: If you change this part you must change 
                --            p_Object$deleteAllRefs as well!!
                UPDATE IBSDEV1.ibs_Object
                SET state = 1,
                    changer = ai_userId,
                    lastChanged = CURRENT TIMESTAMP
                WHERE linkedObjectId IN (
                                            SELECT oid 
                                            FROM IBSDEV1.ibs_Object
                                            WHERE posNoPath LIKE
                                                ( l_posNoPath || '%')
                                            AND state = 1
                                        );
        
                -- the external keys of the deleted objects have to be archived
                CALL IBSDEV1.p_KeyMapper$archiveExtKeys(l_posNoPath);
            ELSE 
                SET l_retValue = c_INSUFFICIENT_RIGHTS;
            END IF;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_OBJECTNOTFOUND;
        END IF;
    END IF;
  
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$performDelete


--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$delete
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform deletion of object:
    CALL IBSDEV1.p_Object$performDelete
            (ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

/*
    IF (l_retValue = c_ALL_RIGHT) THEN      -- operation properly performed?
--       ...
    END IF;                                 -- if operation properly performed
*/

    -- finish transation:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Object$delete


--------------------------------------------------------------------------------
-- Undeletes an object and all its values (incl. rights check). <BR>
-- This procedure also undeletes all links showing to this object. <BR>
-- This procedure does not make a transaction, so that it can be used from
-- within a transaction of another procedure. <BR>
-- This procedure is needed for undeleting a business object which contains
-- several other objects.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be undeleted.
-- @param   @userId             ID of the user who is undeleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- [@param   @oid]              Oid of the deleted object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$performUnDelete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$performUnDelete
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_oLevel        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- conversions: (OBJECTIDSTRING) -- all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, ao_oid);

    -- initialize local variables and return value:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- get container id, posNoPath and level within tree of object
    SET l_sqlcode = 0;

    SELECT containerId, posNoPath, oLevel
    INTO l_containerId, l_posNoPath, l_oLevel
    FROM IBSDEV1.ibs_Object
    WHERE oid = ao_oid;

    IF l_sqlcode <> 0 THEN 
        -- set the return value wirh the error code
        SET l_retValue = c_OBJECTNOTFOUND;
    ELSE 
        -- start undeletion of object, subsequent objects AND references to 
        -- mark object and subsequent objects as 'deleted' via posnopath
        UPDATE IBSDEV1.ibs_Object
        SET state = 2
        WHERE posNoPath LIKE ( l_posNoPath || '%');
        -- mark references to the object as 'undeleted'
        UPDATE IBSDEV1.ibs_Object
        SET state = 2
        WHERE linkedObjectId IN     (
                                    SELECT oid 
                                    FROM IBSDEV1.ibs_Object
                                    WHERE posNoPath LIKE ( l_posNoPath || '%')
                                        AND state = 2
                                    );
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$performDelete


--------------------------------------------------------------------------------
-- Undeletes an object and all its values (incl. rights check). <BR>
-- This procedure also undelets all links showing to this object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be undeleted.
-- @param   @userId             ID of the user who is undeleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$undelete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$undelete
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform undeletion of object:
    CALL IBSDEV1.p_Object$performUnDelete
            (ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
/*
    IF (l_retValue = c_ALL_RIGHT) THEN      -- operation properly performed?
--       ...
    END IF;                                 -- if operation properly performed
*/

    -- finish transation:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Object$undelete


--------------------------------------------------------------------------------
-- Deletes all references to an object - but not the object itself.<BR>
-- Attention: no rights check will be done!
--
-- @input parameters:
-- @param   @oid_s              ID of the object wich refs are to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed 
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$deleteAllRefs');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$deleteAllRefs
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- mark references to the object as 'deleted'
    -- ATTENTION: used like in p_Object$performDelete 
    --            if delete mechanism changes: change both!!
    UPDATE IBSDEV1.ibs_Object
    SET state = 1,
        changer = ai_userId,
        lastChanged = CURRENT TIMESTAMP
    WHERE linkedObjectId = l_oid;
  
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$deleteAllRefs


--------------------------------------------------------------------------------
-- All stored procedures used for copyPaste.
--*****************************************************************************
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Read out the masterattachment of a given businessobject. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the rootobject to be copied.
--
-- @output parameters:
-- @param   @masterOid_s        The Oid of the masterattachment.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$getMasterOid');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$getMasterOid
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    OUT ao_masterOid        CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
  
    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- get the oid of the object which represents a tab of the actual object
    -- determined by its name:
    SET l_sqlcode = 0;

    SELECT o.oid 
    INTO ao_masterOid
    FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_Attachment_01 a
    WHERE o.containerId =   (
                                SELECT oid 
                                FROM IBSDEV1.ibs_Object
                                WHERE containerId = l_oid
                                    AND tVersionId = 16842849
                            )
        AND o.oid = a.oid
        AND a.isMaster = 1;
  
    -- check if the object exists:
    IF l_sqlcode = 100 THEN 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- set object as already read:
    CALL IBSDEV1.p_setRead(l_oid, ai_userId);

    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$getMasterOid


--------------------------------------------------------------------------------
-- Copies a selected BusinessObject and its childs.(incl. rights check). <BR>
-- The rightcheck is done before we start to copy. When one BO of the tree is
-- not able to be copied because of a negativ rightcheckresult, the action is stopped.
--
-- @input parameters:
-- @param   @oid_s              ID of the rootobject to be copied.
-- @param   @userId             ID of the user who is copying the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @targetId_s         Oid of the target BusinessObjectConatiner the root
--                              object is copied to.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$test');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$test
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_containerId_s    VARCHAR (18),
    -- output parameters:
    OUT ao_accObjId         CHAR (8) FOR BIT DATA,
    OUT ao_accObjName       VARCHAR (63),
    OUT ao_accObjContainerId CHAR (8) FOR BIT DATA,
    OUT ao_accObjContainerKind INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_containerTVersionId INT;
    DECLARE l_type          INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- declare cursor:
    DECLARE ibsObjectCursor CURSOR WITH HOLD FOR 
        SELECT  1 AS type, oid, name, containerId, containerKind
        FROM IBSDEV1.   ibs_Object
        WHERE oid IN  (
                          SELECT  containerId
                          FROM IBSDEV1.   ibs_Object
                          WHERE   oid = l_oid
                      )
            UNION
            -- link:
            SELECT  2 AS type, oid, name, containerId, containerKind
            FROM IBSDEV1.   ibs_Object
            WHERE   linkedObjectId = l_oid
            UNION
            -- sentObject
            SELECT  3 AS type, oid, name, containerId, containerKind
            FROM IBSDEV1.   ibs_Object
            WHERE oid IN  (
                               SELECT  oid 
                               FROM IBSDEV1.   ibs_SentObject_01
                               WHERE   distributeId = l_oid
                           );

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
  
    SELECT tVersionId
    INTO l_containerTVersionId
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_containerId;
  
    IF l_containerTVersionId = 16844801 -- news
        OR l_containerTVersionId = 16854273 -- inbox
    THEN 
        SELECT oid, name, containerId, containerKind
        INTO ao_accObjId, ao_accObjName, ao_accObjContainerId,
            ao_accObjContainerKind
        FROM IBSDEV1.ibs_Object
        WHERE oid = l_containerId;
    ELSE 
        OPEN ibsObjectCursor;

        SELECT  type, oid, name, containerId, containerKind
        INTO  l_type, ao_accObjId, ao_accObjName, ao_accObjContainerId,
            ao_accObjContainerKind
        FROM IBSDEV1.ibsObjectCursor 
        WHERE oid <> c_NOOID;

        CLOSE ibsObjectCursor;
    END IF;
END;
-- p_Object$test


--------------------------------------------------------------------------------
-- Returns the OID of a container, regarding to its name. <BR>
--
-- @input parameters:
-- @param   @containerName      name of the container, that should be found
-- @param   @userName           name of the user (only set, if searching for a 
--                                privat container
-- @param   @domainId           id of the domain, where the container should be
--                              found (only set at the search for the root)
-- @param   @actContainer_s     oid of the last container found
--
-- @output parameters:
-- @param   @containerId_s      oid of the container with @containerName
--
-- @returns A value representing the state of the procedure.
--  OBJECTNOTFOUND          The container was not found
--  TOOMANYROWS             More than 1 container was found
--  ALL_RIGHT               Action performed, values returned, everything ok.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$resolvePath');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$resolvePath
(
    -- input parameters:
    IN  ai_containerName    VARCHAR (63),
    IN  ai_userName         VARCHAR (63),
    IN  ai_domainId         INT,
    IN  ai_actContainer_s   VARCHAR (18),
    -- output parameters:
    OUT ao_containerId_s    VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_actContainer  CHAR (8) FOR BIT DATA;        -- oid of the actual found container
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;        -- oid of the new actual container
    DECLARE l_domainOid     CHAR (8) FOR BIT DATA;        -- oid of the domain, where the container should be
    DECLARE l_userId        INT;            -- id of the user in whose privat area the container
                                            -- should be

    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_TOOMANYROWS   INT DEFAULT 5; -- more than one result row found

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_actContainer_s, l_actContainer);
  
    IF ai_domainId = -1 THEN 
        -- get the oid of the container
        SET l_sqlcode = 0;
        SELECT oid 
        INTO l_containerId
        FROM IBSDEV1.ibs_Object
        WHERE name = ai_containerName
            AND containerId = l_actContainer
            AND state = 2;

        SELECT COUNT(*) 
        INTO l_rowcount
        FROM IBSDEV1.ibs_Object
        WHERE name = ai_containerName
            AND containerId = l_actContainer
            AND state = 2;
    ELSE 
        IF ai_userName = '' THEN 
            -- get the domainOid
            SELECT oid
            INTO l_domainOid
            FROM IBSDEV1.ibs_domain_01
            WHERE id = ai_domainId;
            -- get the oid of the container within the proper domain
            SET l_sqlcode = 0;
            SELECT oid
            INTO l_containerId
            FROM IBSDEV1.ibs_Object
            WHERE name = ai_containername
                AND containerId = l_domainOid
                AND state = 2;
            SELECT COUNT(*)
            INTO l_rowcount
            FROM IBSDEV1.ibs_Object
            WHERE name = ai_containername
                AND containerId = l_domainOid
                AND state = 2;
        ELSE 
            -- something in a 'Privat'-container
            -- get the userId from a given username
            SELECT u.id 
            INTO l_userId
            FROM IBSDEV1.ibs_user u, IBSDEV1.ibs_Object o
            WHERE u.domainId = ai_domainId
                AND u.name = ai_userName
                AND u.oid = o.oid
                AND o.state = 2;
            -- get the oid of the workspace for a given user
            SET l_sqlcode = 0;

            SELECT workspace
            INTO l_containerId
            FROM IBSDEV1.ibs_workspace
            WHERE userid = l_userId;

            SELECT COUNT(*)
            INTO l_rowcount
            FROM IBSDEV1.ibs_workspace
            WHERE userid = l_userId;
        END IF;
    END IF;
    IF l_rowcount = 0 THEN 
        SET l_retValue = c_OBJECTNOTFOUND;
    ELSE 
        IF l_rowcount > 1 THEN 
            SET l_retValue = c_TOOMANYROWS;
        ELSE 
            CALL IBSDEV1.p_byteToString (l_containerId, ao_containerId_s);
        END IF;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$resolvePath


--------------------------------------------------------------------------------
-- Checks a business object out (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be checked out.
-- @param   @userId             ID of the user who is checking out the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @output parameters:
-- @param   @creationDate       Date when the object was checked out.
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$checkOut');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$checkOut
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_checkOutDate     TIMESTAMP
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    -- value for checked out for the flags-bitarray
    DECLARE c_ISCHECKEDOUT  INT DEFAULT 16;

    -- local variables:
    DECLARE l_rights        INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- retrieve the information if this object is already checked out

    SELECT  COUNT(*)
    INTO l_rowcount
    FROM IBSDEV1.ibs_Checkout_01
    WHERE   oid = l_oid; 

    IF l_rowcount <= 0 THEN 
        -- rights set for this operation?
        CALL IBSDEV1.p_Rights$checkRights(l_oid, c_NOOID,
            ai_userId,ai_op, l_rights);
        -- check if the user has the necessary rights
        IF l_rights = ai_op THEN 
            -- checkOut the object
            -- set the flag for the checkout in the ibs_object table
            UPDATE IBSDEV1.ibs_Object
            SET flags = b_OR(flags, c_ISCHECKEDOUT)
            WHERE oid = l_oid;

            COMMIT;
            -- get the current checkout date
            SET ao_checkOutDate = CURRENT TIMESTAMP;
            -- add the new tuple to the ibs_CheckOut table:
            INSERT INTO IBSDEV1.ibs_Checkout_01
                ( oid, userId, checkout)
            VALUES ( l_oid, ai_userId, ao_checkOutDate);

            COMMIT;
        ELSE 
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$checkOut


--------------------------------------------------------------------------------
-- Checks in a business object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             ID of the user who is retrieving the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$checkIn');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$checkIn
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    -- value for checked out for the flags-bitarray
    DECLARE c_ISCHECKEDOUT  INT DEFAULT 16;

    -- local variables:
    DECLARE l_rights        INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_checkedUser   INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- retrieving the userId of the user who has checked out this object
    SELECT  userId
    INTO    l_checkedUser
    FROM    IBSDEV1.ibs_CheckOut_01
    WHERE   oid = l_oid;

    -- is the user who wants to perform the checkin the same with the one
    -- who has checked out the object?
    IF l_checkedUser = ai_userId THEN 
        -- rights set for this operation?
        CALL IBSDEV1.p_Rights$checkRights
                (l_oid, c_NOOID, ai_userId, ai_op, l_rights);
        -- check if the user has the necessary rights
        IF l_rights = ai_op THEN 
            -- remove the flag for the checkout in the ibs_object table
            UPDATE  IBSDEV1.ibs_Object
            SET     flags = b_AND (flags, '2147483631') -- 7FFFFFEF
            WHERE   oid = l_oid;
            COMMIT;
            -- delete the tuple regarding this object from the ibs_CheckOut table:
            DELETE FROM IBSDEV1.ibs_Checkout_01
            WHERE   oid = l_oid;
            COMMIT;
        ELSE 
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$checkIn


--------------------------------------------------------------------------------
-- Returns the OID of a object, regarding to its name. <BR>
--
-- @input parameters:
-- @param   @objectName         name of the object, that should be found
-- @param   @userName           name of the user (only set, if searching for a 
--                                privat container
-- @param   @domainId           id of the domain, where the object should be
--                                found (only set at the search for the root)
-- @param   @actContainer_s     oid of the last container found
--
-- @output parameters:
-- @param   @containerId_s      oid of the object with @objectName
-- @param   @isContainer        1 if the object with @objectName is a container
--
-- @returns A value representing the state of the procedure.
--  OBJECTNOTFOUND          The object was not found
--  TOOMANYROWS             More than 1 object was found
--  ALL_RIGHT               Action performed, values returned, everything ok.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$resolveObjectPath');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$resolveObjectPath
(
    -- input parameters:
    IN  ai_objectName       VARCHAR (63),
    IN  ai_userName         VARCHAR (63),
    IN  ai_domainId         INT,
    IN  ai_actContainer_s   VARCHAR (18),
    -- output parameters:
    OUT ao_objectId_s       VARCHAR (18),
    OUT ao_isContainer      INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_actContainer  CHAR (8) FOR BIT DATA;        -- oid of the actual found container
    DECLARE l_objectId      CHAR (8) FOR BIT DATA;        -- oid of the new actual object
    DECLARE l_domainOid     CHAR (8) FOR BIT DATA;        -- oid of the domain, where the object should be
    DECLARE l_userId        INT;            -- id of the user in whose privat area the object
                                            -- should be

    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_TOOMANYROWS   INT DEFAULT 5; -- more than one result row found
  
    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    CALL p_stringToByte (ai_actContainer_s, l_actContainer);
  
    IF ai_domainId = -1 THEN 
        -- get the oid of the container
        SET l_sqlcode = 0;

        SELECT oid, iscontainer
        INTO l_objectId, ao_isContainer
        FROM IBSDEV1.ibs_Object
        WHERE name = ai_objectName
           AND containerId = l_actContainer
           AND state = 2;
        SELECT COUNT(*)
        INTO l_rowcount
        FROM IBSDEV1.ibs_Object
        WHERE name = ai_objectName
           AND containerId = l_actContainer
           AND state = 2;
    ELSE 
        IF ai_userName = '' THEN 
            -- get the domainOid
            SELECT oid
            INTO l_domainOid
            FROM IBSDEV1.ibs_domain_01
            WHERE id = ai_domainId;
            -- get the oid of the container within the proper domain
            SET l_sqlcode = 0;

            SELECT oid, iscontainer
            INTO l_objectId, ao_isContainer
            FROM IBSDEV1.ibs_Object
            WHERE name = ai_objectName
                AND containerId = l_domainOid
                AND state = 2;
            SELECT COUNT(*)
            INTO l_rowcount
            FROM IBSDEV1.ibs_Object
            WHERE name = ai_objectName
                AND containerId = l_domainOid
                AND state = 2;
        ELSE 
            -- get the userId from a given username
            SELECT id
            INTO l_userId
            FROM IBSDEV1.ibs_user
            WHERE domainId = ai_domainId
                AND  name = ai_userName;
            -- get the oid of the workspace for a given user
            SET l_sqlcode = 0;
            SELECT workspace
            INTO l_objectId
            FROM IBSDEV1.ibs_workspace
            WHERE userid = l_userId;
            SELECT COUNT(*)
            INTO l_rowcount
            FROM IBSDEV1.ibs_workspace
            WHERE userid = l_userId;
            -- the workspace is a container
            SET ao_isContainer = 1;
        END IF;
    END IF;

    IF l_rowcount = 0 THEN 
        SET l_retValue = c_OBJECTNOTFOUND;
    ELSE 
        IF l_rowcount > 1 THEN 
            SET l_retValue = c_TOOMANYROWS;
        ELSE 
            CALL p_byteToString (l_objectId, ao_objectId_s);
        END IF;
    END IF;
  
    -- return the state value
    RETURN l_retValue;
END;
-- p_Object$resolveObjectPath
