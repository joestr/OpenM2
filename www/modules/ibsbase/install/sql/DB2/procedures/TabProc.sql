-------------------------------------------------------------------------------
-- All stored procedures regarding the tab table. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------


-------------------------------------------------------------------------------
-- Create a new tab. <BR>
-- If a tab with the same domainId (or in domain 0) and the specified code
-- exists already nothing is done and c_ALREADY_EXISTS is returned.
--
-- @input parameters:
-- @param   ai_domainId         Id of domain in which the tab is valid.
--                              0 ... tab is valid in all domains
-- @param   ai_code             The tab code. This code is unique within each
--                              domain.
-- @param   ai_kind             Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
-- @param   ai_tVersionId       tVersionId of object which is representing the
--                              tab (just for tabs of type OBJECT).
-- @param   ai_fct              Function to be performed when tab is selected.
-- @param   ai_priority         Priority of the tab (+oo ... -oo).
-- @param   ai_multilangKey     The code of the tab in the multilang table.
-- @param   ai_rights           Necessary permissions for the tab to be
--                              displayed.
--                              0 ... no rights necessary
-- @param   ai_class            the class to show viewtab
--
-- @output parameters:
-- @param   ao_id               Id of the newly generated tab.
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A tab with this code already exists.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Tab$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Tab$new(
    -- input parameters:
    IN  ai_domainId         INT,
    IN  ai_code             VARCHAR (63),
    IN  ai_kind             INT,
    IN  ai_tVersionId       INT,
    IN  ai_fct              INT,
    IN  ai_priority         INT,
    IN  ai_multilangKey     VARCHAR (63),
    IN  ai_rights           INT,
    IN  ai_class            VARCHAR (255),
    -- output parameters:
    OUT ao_id               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_kind          INT;            -- kind of the tab
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_ALREADY_EXISTS    = 21;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_kind              = ai_kind;
-- body:
    -- check if the tab exists already:
    SET l_sqlcode = 0;
    SELECT MAX (id) 
    INTO ao_id
    FROM IBSDEV1.ibs_Tab
    WHERE (ai_domainId = 0
        AND code = ai_code)
        OR  (ai_domainId <> 0
        AND domainId = ai_domainId
        AND code = ai_code);

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'check if tab exists already';
        GOTO NonTransactionException;
    END IF;
    -- check if the tab was found:
    IF l_sqlcode = 100 OR (ao_id IS NULL) THEN 
        -- the tab may be inserted
        -- add the tab to the table:
        SET l_sqlcode = 0;

        INSERT INTO IBSDEV1.ibs_Tab
            (domainId, code, kind, tVersionId, fct,
            priority, multilangKey, rights, class)
        VALUES (ai_domainId, ai_code, l_kind, ai_tVersionId, ai_fct,
            ai_priority, ai_multilangKey, ai_rights, ai_class);
    
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'insert tab into ibs_Tab';
            GOTO exception1;
        END IF;
        -- get the new tab id:
        SET l_sqlcode = 0;

        SELECT MAX (id) 
        INTO ao_id
        FROM IBSDEV1.ibs_Tab
        WHERE domainId = ai_domainId
            AND code = ai_code;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'get new tab id';
            GOTO exception1;
        END IF;
        -- check if there occurred an error:
        IF l_sqlcode = 0 THEN 
            COMMIT;
        ELSE 
            ROLLBACK;
        END IF;
    ELSE 
        -- set return value:
        SET l_retValue = c_ALREADY_EXISTS;
    END IF;
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
NonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Tab$new', l_sqlcode, l_ePos,
        'ai_domainId', ai_domainId, 'ai_code', ai_code, 'ai_kind', ai_kind,
        'ai_multilangKey', ai_multilangKey, 'ai_tVersionId', ai_tVersionId,
        '', '', 'ai_fct', ai_fct, '', '', 'ai_priority', ai_priority,
        '', '', 'ai_rights', ai_rights, '', '', 'ao_id', ao_id, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Tab$new

-------------------------------------------------------------------------------
-- Set the attributes of an existing tab. <BR>
--
-- @input parameters:
-- @param   ai_domainId         Id of domain in which the tab shall be changed.
--                              0 ... tab is valid in all domains
-- @param   ai_code             The tab code. This code is unique within each
--                              domain.
-- @param   ai_kind             New Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
-- @param   ai_tVersionId       New tVersionId of object which is representing
--                              the tab (just for tabs of type OBJECT).
-- @param   ai_fct              New Function to be performed when tab is
--                              selected.
-- @param   ai_priority         New Priority of the tab (+oo ... -oo).
-- @param   ai_multilangKey     New code of the tab in the multilang table.
-- @param   ai_rights           New permissions for the tab to be
--                              displayed.
--                              0 ... no rights necessary
-- @param   ai_class            class to show view tab
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  c_ALL_RIGHT             Action performed, values returned, everything ok.
--  c_OBJECTNOTFOUND        The required object was not found within the 
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Tab$set');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Tab$set(
    -- input parameters:
    IN  ai_domainId         INT,
    IN  ai_code             VARCHAR (63),
    IN  ai_kind             INT,
    IN  ai_tVersionId       INT,
    IN  ai_fct              INT,
    IN  ai_priority         INT,
    IN  ai_multilangKey     VARCHAR (63),
    IN  ai_rights           INT,
    IN  ai_class            VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE l_retValue      INT;
    DECLARE l_ePos          VARCHAR (255);
    DECLARE l_rowCount      INT;
    DECLARE l_id            INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- check if the tab exists already:
    SET l_sqlcode = 0;
    SELECT MAX (id) 
    INTO l_id
    FROM IBSDEV1.ibs_Tab
    WHERE domainId = ai_domainId
        AND code = ai_code;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'check if tab exists already';
        GOTO NonTransactionException;
    END IF;
    -- check if the tab was found:
    IF l_sqlcode = 100 OR (l_id IS NULL) THEN 
        -- set return value:
        SET l_retValue = c_OBJECTNOTFOUND;
    ELSE 
        -- the tab may be inserted
        -- change the tab data:
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_Tab
        SET kind = ai_kind,
            tVersionId = ai_tVersionId,
            fct = ai_fct,
            priority = ai_priority,
            multilangKey = ai_multilangKey,
            rights = ai_rights,
            class = ai_class
        WHERE id = l_id;
    
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'change tab data';
            GOTO exception1;
        END IF;
        -- check if there occurred an error:
        IF l_sqlcode = 0 THEN 
            COMMIT;
        ELSE 
            ROLLBACK;
        END IF;
    END IF;
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
NonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Tab$set', l_sqlcode, l_ePos,
        'ai_domainId', ai_domainId, 'ai_code', ai_code, 'ai_kind', ai_kind,
        'ai_multilangKey', ai_multilangKey, 'ai_tVersionId', ai_tVersionId,
        '', '', 'ai_fct', ai_fct, '', '', 'ai_priority', ai_priority, '', '',
        'ai_rights', ai_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Tab$set

-------------------------------------------------------------------------------
-- Get the data from a given tab. <BR>
-- This procedure gets a tuple out of ibs_Tab by using the domainId and the
-- code together as unique key.
-- If there is no tuple found the return value is c_OBJECTNOTFOUND.
--
-- @input parameters:
-- @param   ai_domainId         Id of domain in which the tab shall be changed.
--                              0 ... tab is valid in all domains
-- @param   ai_code             The tab code. This code is unique within each
--                              domain.
--
-- @output parameters:
-- @param   ao_id               The id of the tab
-- @param   ao_kind             Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
-- @param   ao_tVersionId       tVersionId of object which is representing
--                              the tab (just for tabs of type OBJECT).
-- @param   ao_fct              Function to be performed when tab is
--                              selected.
-- @param   ao_priority         Priority of the tab (+oo ... -oo).
-- @param   ao_multilangKey     Code of the tab in the multilang table.
-- @param   ao_rights           Permissions for the tab to be displayed.
--                              0 ... no rights necessary
-- @return  A value representing the state of the procedure.
--  c_ALL_RIGHT             Action performed, values returned, everything ok.
--  c_OBJECTNOTFOUND        The required object was not found within the 
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Tab$get');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Tab$get(
    -- input parameters:
    IN  ai_domainId         INT,
    IN  ai_code             VARCHAR (63),
    -- output parameters:
    OUT ao_id               INT,
    OUT ao_kind             INT,
    OUT ao_tVersionId       INT,
    OUT ao_fct              INT,
    OUT ao_priority         INT,
    OUT ao_multilangKey     VARCHAR (63),
    OUT ao_rights           INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- input parameters:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- output parameters:
    DECLARE l_retValue      INT;
    DECLARE l_ePos          VARCHAR (255);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize local variables:
    SET l_retValue           = c_ALL_RIGHT;
-- body:
    -- check if the tab exists:
    -- get the tab data:
    SET l_sqlcode = 0;

    SELECT id, kind, tVersionId, fct, priority, multilangKey, rights 
    INTO ao_id, ao_kind, ao_tVersionId, ao_fct, ao_priority,
        ao_multilangKey, ao_rights
    FROM IBSDEV1.ibs_Tab
    WHERE domainId = ai_domainId
        AND code = ai_code;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'check if tab exists';
        GOTO exception1;
    END IF;
    -- check if the tab was found:
    IF l_sqlcode = 100 OR (ao_id IS NULL) THEN 
        -- set return value:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value:

    RETURN l_retValue;
  
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Tab$get', l_sqlcode, l_ePos,
        'ai_domainId', ai_domainId, 'ai_code', ai_code, 'ao_id', ao_id,
        'ao_multilangKey', ao_multilangKey, 'ao_kind', ao_kind, '', '',
        'ao_tVersionId', ao_tVersionId, '', '', 'ao_fct', ao_fct, '', '',
        'ao_priority', ao_priority, '', '', 'ao_rights', ao_rights
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Tab$get

-------------------------------------------------------------------------------
-- Get the code from a given taboid. <BR>
-- This procedure gets a tuple out of ibs_Tab by using the taboid at ibs_object.
--
-- If there is no tuple found the return value is c_OBJECTNOTFOUND.
--
-- @input parameters:
-- @param   ai_tabOid           Oid of the tab to get the typecode
-- 
--
-- @output parameters:
-- @param   ao_tabCode          code of the tab
-- 
-- 
-- @return  A value representing the state of the procedure.
--  c_ALL_RIGHT             Action performed, values returned, everything ok.
--  c_OBJECTNOTFOUND        The required object was not found within the
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Tab$getCodeFromOid');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Tab$getCodeFromOid(
    -- input parameters:
    IN  ai_tabOid           VARCHAR (18),
    -- output parameters:
    OUT ao_tabCode          VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_tabOid        CHAR (8) FOR BIT DATA;        -- tabOid to hold the oid 
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_tabOid, l_tabOid);
    -- get the code
    SET l_sqlcode = 0;

    SELECT code
    INTO ao_tabCode
    FROM IBSDEV1.ibs_Tab t, IBSDEV1.ibs_ConsistsOf c, IBSDEV1.ibs_Object o
    WHERE t.id = c.tabId
        AND c.id = o.consistsOfId
        AND o.oid = l_tabOid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'check if tab exists';
        GOTO exception1;
    END IF;
    -- check if the tab was found:
    IF l_sqlcode = 100 OR (rtrim(ao_tabCode) LIKE '') THEN 
        -- set return value:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Tab$getCodeFromOid', l_sqlcode, l_ePos,
        '',0,'ai_tabOid', ai_tabOid,'',0, 'ao_tabCode', ao_tabCode, '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
  
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Tab$getCodeFromOid
