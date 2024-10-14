-------------------------------------------------------------------------------
-- The rights mapping data within the framework. <BR>
--
-- @version     $Id: createRightsMapping.sql,v 1.4 2003/10/21 22:14:45 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createRightsMapping');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createRightsMapping () 
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- drop old rights mapping:
    DELETE  FROM IBSDEV1.ibs_RightsMapping;

    -- create rights entries: rights are kind of hierarchical
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('READ', 'READ');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('READ', 'VIEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('READ', 'VIEWELEMS');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CREATE', 'READ');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CREATE', 'VIEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CREATE', 'VIEWELEMS');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CREATE', 'NEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CREATE', 'ADDELEM');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGE', 'READ');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGE', 'VIEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGE', 'VIEWELEMS');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGE', 'NEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGE', 'ADDELEM');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGE', 'CHANGE');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'READ');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'VIEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'VIEWELEMS');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'NEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'ADDELEM');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'CHANGE');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'DELETE');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('CHANGEDELETE', 'DELELEM');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'READ');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'VIEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'VIEWELEMS');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'NEW');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'ADDELEM');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'CHANGE');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'DELETE');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'DELELEM');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'VIEWRIGHTS');
--    no setrights allowed for workflow-users
--    INSERT  ibs_RightsMapping VALUES ('ALL', 'SETRIGHTS') 
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'CREATELINK');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'DISTRIBUTE');
    INSERT  INTO IBSDEV1.ibs_RightsMapping VALUES ('ALL', 'VIEWPROTOCOL');
END; -- pi_createRightsMapping

-- execute procedure:
CALL IBSDEV1.pi_createRightsMapping;
-- delete procedure:
CALL IBSDEV1.p_dropProc ('pi_createRightsMapping');
