-------------------------------------------------------------------------------
-- All base data within the framework. <BR>
--
-- @version     $Id: createBaseData.sql,v 1.7 2005/08/11 14:20:27 klreimue Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pi_createBaseData');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pi_createBaseData () 
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;
    DECLARE c_MAXValidUntil TIMESTAMP;
    DECLARE c_TVRoot        INT;
    DECLARE c_TVUser        INT;
    DECLARE c_TVUserProfile INT;
    DECLARE c_TVReference   INT;
    DECLARE c_TVDI          INT;
    DECLARE c_TVImportContainer INT;
    DECLARE c_TVExportContainer INT;
    DECLARE c_TVLayoutContainer INT;
    DECLARE c_TVLayout      INT;
    DECLARE c_TVDomainSchemeContainer INT;
    DECLARE c_TVDomainScheme INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_languageId    INT;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_rightsAdmin   INT;
    DECLARE l_rightsProfile INT;
    DECLARE l_rightsRootRef INT;
    DECLARE l_retVal        INT;
    DECLARE l_profile       CHAR (8) FOR BIT DATA; -- oid of user profile
    DECLARE l_profile_s     VARCHAR (18);
    DECLARE l_cid           CHAR (8) FOR BIT DATA; -- oid of actual container
    DECLARE l_cid_s         VARCHAR (18);
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of actual object
    DECLARE l_oid_s         VARCHAR (18);
    DECLARE l_name          VARCHAR (63);
    DECLARE l_desc          VARCHAR (255);
    DECLARE l_rights        INT;
    DECLARE l_admin         INT;
    DECLARE l_adminOid      CHAR (8) FOR BIT DATA; -- oid of administrator obj.
    DECLARE l_adminOid_s    VARCHAR (18);
    DECLARE l_root          CHAR (8) FOR BIT DATA; -- oid of root object
    DECLARE l_root_s        VARCHAR (18);
    DECLARE l_rootRef       CHAR (8) FOR BIT DATA; -- oid of reference to root
    DECLARE l_rootRef_s     VARCHAR (18);
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_msg           VARCHAR (255);
    DECLARE l_msgBitData    CHAR (18);
    DECLARE l_cnt           INT;
    DECLARE l_adminAlreadyExists SMALLINT;
    DECLARE l_rootAlreadyExists SMALLINT;
    DECLARE l_tVersionid    INT;
    DECLARE l_retValue      INT;
    DECLARE l_menuTabOid_s  VARCHAR (18);
    DECLARE l_menuTabOid    CHAR (8) FOR BIT DATA; -- oid of menu tab object
    DECLARE l_sqlcode       INT DEFAULT 0;  -- last sql code
    DECLARE l_sqlmsg        VARCHAR (2000) DEFAULT ''; -- last sql message

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_ALL_RIGHT = 1;
    SET c_MAXValidUntil = '#CONFVAR.ibsbase.validUntilSql#-00.00.00.000000';
    SET c_TVRoot = 16864001;
    SET c_TVUser = 16842913;
    SET c_TVUserProfile = 16857089;
    SET c_TVReference = 16842801;
    SET c_TVDI = 16872449;
    SET c_TVImportContainer = 16873729;
    SET c_TVExportContainer = 16873985;
    SET c_TVLayoutContainer = 16871169;
    SET c_TVLayout = 16871425;
    SET c_TVDomainSchemeContainer = 16843025;
    SET c_TVDomainScheme = 16843041;
    SET c_languageId = 0;

    -- initialize local variables:
    SET l_adminAlreadyExists = 0;
    SET l_rootAlreadyExists = 0;
    SET l_root = c_NOOID;
    SET l_root_s = c_NOOID_s;
    SET l_adminOid = c_NOOID;
    SET l_adminOid_s = c_NOOID_S;
    SET l_cid = c_NOOID;
    SET l_cid_s = c_NOOID_S;

-- body:
    ---------------------------------------------------------------------------
    -- Create system administrator                                             
    ---------------------------------------------------------------------------
    -- create administrator:
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'start', '', 0, 'l_root_s', l_root_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_sysAdmin', l_name, l_desc);
  
    CALL IBSDEV1.p_User_01$new
        (0, 1, l_name, 'isEnc_bnRpSG1hZCBBcA%3D%3D%0A', 'System Administrator',
        NULL, NULL, l_admin);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'admin created', '', 0, 'l_root_s', l_root_s, 'l_admin', l_admin, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    -- set admin flag:
    UPDATE  IBSDEV1.ibs_User
    SET     admin = 1
    WHERE   id = l_admin;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'after user update', '', 0, 'l_root_s', l_root_s, 'l_admin', l_admin, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- store the id of the administrator:
    SET l_desc = CAST (l_admin AS VARCHAR (255));
    CALL IBSDEV1.p_System$new ('ID_sysAdmin', 'INTEGER', l_desc);
  
    -- get rights for administrator on root:
    SELECT  SUM (id) 
    INTO    l_rightsAdmin
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('new', 'view', 'read', 'viewElems', 'addElem', 'delElem');
  
    -- get rights for administrator on his/her own profile:
    SELECT  SUM (id) 
    INTO    l_rightsProfile
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('view', 'read', 'viewElems');
  
    -- get rights for administrator on reference to root:
    SELECT  SUM (id) 
    INTO    l_rightsRootRef
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('view', 'read', 'viewElems');
  
    -- check if there was an error:
    IF l_retVal = 1 THEN 
        -- no error during creation
        SET l_msg = 'System Administrator created.';
        CALL IBSDEV1.p_Debug (l_msg);
    ELSE 
        IF l_retVal = 21 THEN 
            -- administrator already exists
            SET l_msg = 'System Administrator already exists.';
            CALL IBSDEV1.p_Debug (l_msg);
        ELSE 
            SET l_msg = 'Error during creation of System Administrator: ' ||
                CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10));
            CALL IBSDEV1.p_Debug (l_msg);
        END IF;
    END IF;
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'system administrator created', '', 0, 'l_root_s', l_root_s, 'l_admin', l_admin, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    ---------------------------------------------------------------------------
    -- Create root of system                                                   
    ---------------------------------------------------------------------------

    -- PROCEDURE IBSDEV1.p_Object$create
    -- uid, op, tVersionId,
    -- name, containerId_s, containerKind, isLink, linkedObjectId_s,
    -- description, oid_s OUTPUT
    SET l_retVal = 21;
    -- Root of the system:
    IF NOT EXISTS
    (
        SELECT  id 
        FROM    IBSDEV1.ibs_Object
        WHERE   containerId = c_NOOID
    ) 
    THEN
        -- create the root:
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysRoot', l_name, l_desc);

        CALL IBSDEV1.p_Object$performCreate (l_admin, 0, 16864001, l_name, -- X'01015301'
            c_NOOID_s, 1, 0, c_NOOID_s, l_desc, l_root_s, l_root);
        GET DIAGNOSTICS l_retVal = RETURN_STATUS;

/*
        -- convert string representation of oid to byte:
        CALL IBSDEV1.p_stringToByte (l_root_s, l_root);
*/

        -- set valid until date of root and all yet created objects to nearly
        -- infinite:
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_Object
        SET     validUntil = c_MAXValidUntil
        WHERE   LOCATE
                (
                    (
                        SELECT posNoPath 
                        FROM IBSDEV1.ibs_Object
                        WHERE oid = l_root
                    ) ,
                    posNoPath
                ) = 1;

        IF (l_sqlcode <> 0)
        THEN
            CALL IBSDEV1.logError (500, 'cBData', l_sqlcode, 'update validUntil', '', 0, 'l_root_s', l_root_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        END IF;
    ELSE 
        -- get the root oid:
        SELECT  oid
        INTO    l_root
        FROM    IBSDEV1.ibs_Object
        WHERE   containerId = c_NOOID;
  
        -- convert oid of root to string representation:
        CALL IBSDEV1.p_byteToString (l_root, l_root_s);
    END IF;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'l_root_s', '', 0, 'l_root_s', l_root_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- delete all existing rights on the root:
    CALL IBSDEV1.p_Rights$deleteObjectRights (l_root);

    -- set administrator rights on root:
    CALL IBSDEV1.p_Rights$setRights (l_root, l_admin, l_rightsAdmin, 1);
--    -- cumulate the rights:
--    CALL p_Rights$updateRightsCum;

    -- create user profile of administrator:
    CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysAdminUserprofile', l_name, l_desc);
  
    CALL IBSDEV1.p_UserProfile_01$create (l_admin, 0, l_admin,
        c_TVUserProfile, l_name, l_root_s, 1, 0,
        c_NOOID_s, l_desc, l_profile_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;

--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'l_profile_s', '', 0, 'l_profile_s', l_profile_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    CALL IBSDEV1.p_stringToByte (l_profile_s, l_profile);
  
    -- set rights of administrator on his/her own user profile:
    CALL IBSDEV1.p_Rights$setRights (l_profile, l_admin, l_rightsProfile, 1);
--    -- cumulate the rights:
--    CALL p_Rights$updateRightsCum;

    -- store workspace of administrator:
    SET sqlcode = 0;
    INSERT  INTO IBSDEV1.ibs_Workspace
            (userId, domainId, workspace, profile, publicWsp) 
    VALUES  (l_admin, 0, l_root, l_profile, l_root);

    -- check if there was an error:
    IF (l_retVal = 1)
    THEN 
        SET l_msg = 'System Root created.';
        CALL IBSDEV1.p_Debug (l_msg);
    ELSE 
        IF (l_retVal = 21)
        THEN 
            -- root already exists
            SET l_msg = 'System Root already exists.';
            CALL IBSDEV1.p_Debug (l_msg);
        ELSE 
            SET l_msg = 'Error during creation of System Root: ' ||
                CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10)) || '.';
            CALL IBSDEV1.p_Debug (l_msg);
        END IF;
    END IF;
  
    -- create reference to root used for administrative purposes:
    CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysRootRef', l_name, l_desc);
  
    CALL IBSDEV1.p_Object$performCreate (l_admin, 0, 16842801, l_name, -- X'01010031'
        l_root_s, 1, 1, l_root_s, l_desc, l_rootRef_s, l_rootRef);
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'after reference',
'', 0, 'l_rootRef_s', l_rootRef_s, '', 0, '', '',
'', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0,
'', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
  
/*
    CALL IBSDEV1.p_stringToByte (l_rootRef_s, l_rootRef);
*/
  
    -- ensure that the reference is shown in the menu:
--    UPDATE IBSDEV1.ibs_Object
--    SET showInMenu = 1
--    WHERE oid = l_rootRef;

    -- set rights of administrator on reference to root:
    CALL IBSDEV1.p_Rights$setRights (l_rootRef, l_admin, l_rightsRootRef, 1);
--    -- cumulate the rights:
--    CALL p_Rights$updateRightsCum;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'after setting rights', '', 0, 'l_rootRef_s', l_rootRef_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'system root created', '', 0, 'l_root_s', l_root_s, 'l_admin', l_admin, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    ---------------------------------------------------------------------------
    -- Create base object for system administrator                             
    ---------------------------------------------------------------------------
    -- create base object for system administrator:
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'getting ObjectDesc', 'c_languageId', c_languageId, 'l_name', l_name, '', 0, 'l_desc', l_desc, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
    CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysAdmin', l_name, l_desc);
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'after getting ObjectDesc', '', 0, 'l_name', l_name, '', 0, 'l_desc', l_desc, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'before create user object', 'l_retVal', l_retVal, 'l_rootRef_s', l_rootRef_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performCreate (l_admin, 0, c_TVUser, l_name,
        l_root_s, 1, 0, c_NOOID_s, l_desc, l_adminOid_s, l_rootRef);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;

--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'user object created', 'l_retVal', l_retVal, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;

    IF (l_retVal = c_ALL_RIGHT)
    THEN 
        -- set the new oid for the system administrator:
        SET l_sqlcode = 0;
SET l_sqlmsg = 'new msg';
        UPDATE  IBSDEV1.ibs_User
        SET     oid = l_adminOid
        WHERE   id = l_admin;
        GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
--CALL IBSDEV1.logError (100, 'cBData UPDATE user', l_sqlcode, l_sqlmsg, '', 0, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--SET l_sqlmsg = 'new msg';
--CALL IBSDEV1.logError (100, 'cBData UPDATE user', l_sqlcode, l_sqlmsg, '', 0, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        SET l_msg = 'cBData: Creation of BusinessObject for system' ||
            ' administrator finished.';
--CALL IBSDEV1.logError (100, 'cBData set msg', l_sqlcode, l_sqlmsg, '', 0, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Debug (l_msg);
        GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
--CALL IBSDEV1.logError (100, 'cBData debug', l_sqlcode, l_sqlmsg, '', 0, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    ELSE 
        -- error during object creation?
        SET l_msg = 'cBData: BusinessObject could not be created.';
        CALL IBSDEV1.p_Debug (l_msg);
        GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
    END IF;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, l_sqlmsg, '', 0, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'after update for user', '', 0, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'user object created', '', 0, 'l_root_s', l_root_s, 'l_admin', l_admin, 'l_adminOid_s', l_adminOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    ---------------------------------------------------------------------------
    -- Create MenuTab for System Adminstrator                                  
    ---------------------------------------------------------------------------
    -- create MenuTab for System Adminstrator because the System Administrator
    -- is in domain 0, which not realy exists
    -- sets the tab for the System Administrator
    SET sqlcode = 0;
    SELECT  actVersion
    INTO    l_tVersionId
    FROM    IBSDEV1.ibs_Type
    WHERE   code = 'MenuTab';
    GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'get actVersion', 'l_tVersionId', l_tVersionId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
  
    CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysRoot', l_name, l_desc);
  
    CALL IBSDEV1.p_MenuTab_01$create (l_admin, 0, l_tVersionId, l_name,
        l_root_s, 1, 0, c_NOOID_s, l_desc, l_menutabOid_s);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
  
    CALL IBSDEV1.p_stringToByte (l_menutabOid_s, l_menutabOid);
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'after creating menu tab', '', 0, 'l_menutabOid_s', l_menutabOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
  
    -- update:
    SET sqlcode = 0;
    UPDATE  IBSDEV1.ibs_MenuTab_01
    SET     objectOid = l_root,
            description = l_name,
            priorityKey = 1,
            isPrivate = 0,
            domainId = 0,
            classFront = 'systemFront',
            classBack = 'systemBack',
            fileName = 'system.htm'
    WHERE   oid = l_menutabOid;
    GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'after updating menu tab', '', 0, 'l_menutabOid_s', l_menutabOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'menu tab created', '', 0, 'l_root_s', l_root_s, 'l_admin', l_admin, 'l_menutabOid_s', l_menutabOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    ---------------------------------------------------------------------------
    -- Create Data Interchange on system root                                  
    ---------------------------------------------------------------------------
    -- create import/export management:
    -- Data Interchange:
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '1 Create Data Interchange on system root', 'c_languageId',c_languageId, 'l_name', l_name, '', 0, 'l_desc', l_desc, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysDataInterchange', l_name, l_desc);
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '2 Create Data Interchange on system root', 'c_languageId',c_languageId, 'l_name', l_name, '', 0, 'l_desc', l_desc, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performCreate (l_admin, 0, c_TVDI, l_name,
        l_root_s, 1, 0, c_NOOID_s, l_desc, l_cid_s, l_cid);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3a Create Data Interchange on system root', 'l_retVal', l_retVal, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3aa Create Data Interchange on system root', 'l_retVal', l_retVal, 'l_cid_s', l_cid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3 Create Data Interchange on system root', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', l_cid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3c Create Data Interchange on system root', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', 'nix da', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3d Create Data Interchange on system root', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', 'nix da', '', 0, 'l_cid_s', 'nix da', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3e Create Data Interchange on system root', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', '', 'l_retVal', l_retVal, 'l_root_s', 'nix da', '', 0, 'l_cid_s', 'nix da', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3f Create Data Interchange on system root', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', 0, 'l_desc', '', 'l_retVal', l_retVal, 'l_root_s', 'nix da', '', 0, 'l_cid_s', 'nix da', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3g Create Data Interchange on system root', 'c_languageId', c_languageId, 'l_name', '', 'l_admin', 0, 'l_desc', '', 'l_retVal', l_retVal, 'l_root_s', 'nix da', '', 0, 'l_cid_s', 'nix da', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, '3g Create Data Interchange on system root', 'c_languageId', 0, 'l_name', '', 'l_admin', 0, 'l_desc', '', 'l_retVal', l_retVal, 'l_root_s', 'nix da', '', 0, 'l_cid_s', 'nix da', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--COMMIT;
    IF (l_retVal = c_ALL_RIGHT)
    THEN 
        -- convert string to oid representation:
        CALL IBSDEV1.p_stringToByte (l_cid_s, l_cid);
        -- set rights on import/export management:
        CALL IBSDEV1.p_Rights$propagateUserRights (l_root, l_cid, l_admin);
    
        -- Import
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysDIImport', l_name, l_desc);
    
        CALL IBSDEV1.p_Object$create (l_admin, 0, c_TVImportContainer,
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    
        IF (l_retVal = c_ALL_RIGHT)
        THEN 
            -- Export
            CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysDIExport', l_name, l_desc);
      
            CALL IBSDEV1.p_Object$create (l_admin, 0, c_TVExportContainer,
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
            GET DIAGNOSTICS l_retVal = RETURN_STATUS;
      
            IF (l_retVal = c_ALL_RIGHT)
            THEN
                SET l_msg = 'cBData: Data Interchange on system level' ||
                    ' correctly defined.';
        
                CALL IBSDEV1.p_Debug (l_msg);
            ELSE 
                SET l_msg = 'cBData: Error when creating export container:' ||
                    ' retVal = ' ||
                    CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10)) ||
                    '.';
        
                CALL IBSDEV1.p_Debug (l_msg);
            END IF;
        ELSE 
            SET l_msg = 'cBData: Error when creating import container:' ||
                ' retVal = ' ||
                CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10)) ||
                '.';
            CALL IBSDEV1.p_Debug (l_msg);
        END IF;
    ELSE 
        SET l_msg = 'cBData: Error when creating data interchange:' ||
            ' retVal = ' ||
            CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10)) ||
            '.';
        CALL IBSDEV1.p_Debug (l_msg);
    END IF;
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'Data Interchange on system root created', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', l_cid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    ---------------------------------------------------------------------------
    -- Create Layout Management on system root                                 
    ---------------------------------------------------------------------------
    -- create layout container:
    CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysLayouts', l_name, l_desc);
  
    CALL IBSDEV1.p_Object$create (l_admin, 0, c_TVLayoutContainer,
        l_name, l_root_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
--CALL IBSDEV1.logError (100, 'cBData layout cont. created', l_sqlcode, l_sqlmsg, 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', l_cid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    IF (l_retVal = c_ALL_RIGHT)
    THEN
        -- set rights on layout container:
        CALL IBSDEV1.p_stringToByte (l_cid_s, l_cid);
    
        -- set rights:
        SET sqlcode = 0;
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                (   'view', 'read', 'viewRights', 'new', 'addElem', 'delElem',
                    'viewElems', 'viewProtocol'
                );
    
        CALL IBSDEV1.p_Rights$setRights (l_cid, l_admin, l_rights, 1);
--        -- cumulate the rights:
--        CALL p_Rights$updateRightsCum;
    
        -- create layout "Standard" for the root within the layout container:
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysLayoutStandard', l_name, l_desc);
    
        CALL IBSDEV1.p_Layout_01$create (l_admin, 0, c_TVLayout, 'Standard',
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        GET DIAGNOSTICS l_retVal = RETURN_STATUS;
        GET DIAGNOSTICS EXCEPTION 1 l_sqlmsg = MESSAGE_TEXT;
--CALL IBSDEV1.logError (100, 'cBData layout created', l_sqlcode, l_sqlmsg, 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', l_cid_s, '', 0, '', 'l_oid_s', l_oid_s, 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    
        IF (l_retVal = c_ALL_RIGHT)
        THEN
            CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);

            -- set rights on layout:
            SET sqlcode = 0;
            SELECT  SUM (id) 
            INTO    l_rights
            FROM    IBSDEV1.ibs_Operation;

            CALL IBSDEV1.p_Rights$setRights (l_oid, l_admin, l_rights, 1);
--            -- cumulate the rights:
--            CALL p_Rights$updateRightsCum;

            -- set layout for system administrator:
            SET sqlcode = 0;
            UPDATE  IBSDEV1.ibs_UserProfile
            SET     layoutId = l_oid
            WHERE   userId = l_admin;
--CALL IBSDEV1.logError (100, 'cBData layout for admin set', l_sqlcode, l_sqlmsg, 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', l_cid_s, '', 0, 'l_oid_s', l_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

            SET l_msg = 'cBData: Layout Management on system level correctly defined.';
            CALL IBSDEV1.p_Debug (l_msg);
        ELSE 
            SET l_msg =
                'cBData: Error when creating Layout Standard:' ||
                ' retVal = ' ||
                CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10)) ||
                '.';
      
            CALL IBSDEV1.p_Debug (l_msg);
        END IF;
    ELSE 
        SET l_msg = 'cBData: Error when creating Layout Management:' ||
            ' retVal = ' ||
            CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10)) ||
            '.';
        CALL IBSDEV1.p_Debug (l_msg);
    END IF;
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'layout mmt created', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', l_cid_s, '', 0, 'l_oid_s', l_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');


    ---------------------------------------------------------------------------
    -- Create Domain Scheme Management on system root                          
    ---------------------------------------------------------------------------
    -- create domain scheme container:
    CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_sysDomainSchemes', l_name, l_desc);
  
    CALL IBSDEV1.p_Object$create (l_admin, 0, c_TVDomainSchemeContainer,
        l_name, l_root_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    IF (l_retVal = c_ALL_RIGHT)
    THEN
        -- set rights on domain scheme container:
        CALL IBSDEV1.p_stringToByte (l_cid_s, l_cid);
        -- set rights:
        SET sqlcode = 0;
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                (   'view', 'read', 'viewRights', 'new', 'addElem', 'delElem',
                    'viewElems', 'viewProtocol');

        CALL IBSDEV1.p_Rights$setRights (l_cid, l_admin, l_rights, 1);
--        -- cumulate the rights:
--        CALL p_Rights$updateRightsCum;

        SET l_msg = 'cBData: Domain Scheme Management correctly defined.';
        CALL IBSDEV1.p_Debug (l_msg);
    ELSE 
        SET l_msg = 'cBData: Error when creating Domain Scheme Management:' ||
            ' retVal = ' ||
            CAST (rtrim (CHAR (l_retVal)) AS VARCHAR (10)) ||
            '.';
        CALL IBSDEV1.p_Debug (l_msg);
    END IF;
CALL IBSDEV1.logError (100, 'cBData', l_sqlcode, 'domain scheme mmt created', 'c_languageId', c_languageId, 'l_name', l_name, 'l_admin', l_admin, 'l_desc', l_desc, 'l_retVal', l_retVal, 'l_root_s', l_root_s, '', 0, 'l_cid_s', l_cid_s, '', 0, 'l_oid_s', l_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');


    ---------------------------------------------------------------------------
    -- Cumulate the rights                                                     
    ---------------------------------------------------------------------------
    -- ensure that the rights are correctly cumulated:

    -- cumulate the rights:
    CALL IBSDEV1.p_Rights$updateRightsCum ();
  
    SET l_msg = 'cBData: Rights cumulation finished.';
    CALL IBSDEV1.p_Debug (l_msg);
  
    ---------------------------------------------------------------------------
    -- create workflow rights mappings                                         
    ---------------------------------------------------------------------------
endtag:
COMMIT;
END; -- pi_createBaseData

-- execute procedures:
CALL IBSDEV1.pi_createBaseData;
-- delete procedure:
CALL IBSDEV1.p_dropProc ('pi_createBaseData');
