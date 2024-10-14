--------------------------------------------------------------------------------
-- All stored procedures regarding the domain table. <BR>
--
-- @version     $Id: Domain_01Proc.sql,v 1.6 2003/10/21 22:14:48 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020818
-------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is creating the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_tVersionId       Type of the new object.
-- @param   ai_name             Name of the object.
-- @param   ai_containerId_s    ID of the container where object shall be
--                              created in.
-- @param   ai_containerKind    Kind of object/container relationship
-- @param   ai_isLink           Defines if the object is a link
-- @param   ai_linkedObjectId_s If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   ai_description      Description of the object.
-- @param   ai_sslRequired      the flag if SSL must be used or not
--
-- @output parameters:
-- @param   ao_oid_s            OID of the newly created object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Domain_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Domain_01$create
(
    -- common input parameters:
    IN  ai_userId         INT,
    IN  ai_op             INT,
    IN  ai_tVersionId     INT,
    IN  ai_name           VARCHAR (63),
    IN  ai_containerId_s  VARCHAR (18),
    IN  ai_containerKind  INT,
    IN  ai_isLink         SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description    VARCHAR (255),
    IN  ai_sslRequired    SMALLINT,
    -- common output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_languageId    INT DEFAULT 0;  -- the current language

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT DEFAULT 0;  -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- the actual oid
    DECLARE l_name          VARCHAR (63) DEFAULT ''; -- the actual name
    DECLARE l_desc          VARCHAR (255) DEFAULT ''; -- the actual description
    DECLARE l_domainId      INT DEFAULT 0;
    DECLARE l_tVersionId    INT DEFAULT 0;
    DECLARE l_admin         INT DEFAULT 0;
    DECLARE l_admin2        INT DEFAULT 0;
    DECLARE l_adminOid      CHAR (8) FOR BIT DATA;
    DECLARE l_adminOid_s    VARCHAR (18);
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_allRights     INT DEFAULT 0;
    DECLARE l_public        CHAR (8) FOR BIT DATA;
    DECLARE l_public_s      VARCHAR (18);
    DECLARE l_workspaces    CHAR (8) FOR BIT DATA;
    DECLARE l_workspaces_s  VARCHAR (18);
    DECLARE l_userMmtOid    CHAR (8) FOR BIT DATA;
    DECLARE l_userMmtOid_s  VARCHAR (18);
    DECLARE l_groupContainer CHAR (8) FOR BIT DATA;
    DECLARE l_groupContainer_s VARCHAR (18);
    DECLARE l_userContainer CHAR (8) FOR BIT DATA;
    DECLARE l_userContainer_s VARCHAR (18);
    DECLARE l_allUserGroup  INT DEFAULT 0;
    DECLARE l_allUserGroupOid CHAR (8) FOR BIT DATA;
    DECLARE l_allUserGroupOid_s VARCHAR (18);
    DECLARE l_adminGroup    INT DEFAULT 0;
    DECLARE l_adminGroupOid CHAR (8) FOR BIT DATA;
    DECLARE l_adminGroupOid_s VARCHAR (18);
    DECLARE l_userAdminGroup INT DEFAULT 0;
    DECLARE l_userAdminGroupOid CHAR (8) FOR BIT DATA;
    DECLARE l_userAdminGroupOid_s VARCHAR (18);
    DECLARE l_structAdminGroup INT DEFAULT 0;
    DECLARE l_structAdminGroupOid CHAR (8) FOR BIT DATA;
    DECLARE l_structAdminGroupOid_s VARCHAR (18);
    DECLARE l_systemUser    INT DEFAULT 0;
    DECLARE l_systemUserOid CHAR (8) FOR BIT DATA;
    DECLARE l_systemUserOid_s VARCHAR (18);
    DECLARE l_systemUserName VARCHAR (63);
    DECLARE l_layoutContainerOid CHAR (8) FOR BIT DATA;
    DECLARE l_layoutContainerOid_s VARCHAR (18);
    DECLARE l_layoutOid     CHAR (8) FOR BIT DATA;
    DECLARE l_layoutOid_s   VARCHAR (18);
    DECLARE l_menutabContainerOid CHAR (8) FOR BIT DATA;
    DECLARE l_menutabContainerOid_s VARCHAR (18);
    DECLARE l_menutabOid    CHAR (8) FOR BIT DATA;
    DECLARE l_menutabOid_s  VARCHAR (18);
    DECLARE l_queryContainer CHAR (8) FOR BIT DATA;
    DECLARE l_queryContainer_s VARCHAR (18);
    DECLARE l_queryPublicContainer CHAR (8) FOR BIT DATA;
    DECLARE l_queryPublicContainer_s VARCHAR (18);
    DECLARE l_workspTemplContainerOid CHAR (8) FOR BIT DATA;
    DECLARE l_workspTemplContainerOid_s VARCHAR (18);
    DECLARE l_tvWorkspaceTempCont INT DEFAULT 0;
    DECLARE l_rightsAll     INT DEFAULT 0;
    DECLARE l_localOp       INT DEFAULT 0;  -- operation for local operations
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
    SET l_oid               = c_NOOID;
    SET l_public            = c_NOOID;
    SET l_public_s          = c_NOOID_s;
    SET l_workspaces        = c_NOOID;
    SET l_workspaces_s      = c_NOOID_s;
    SET l_userMmtOid        = c_NOOID;
    SET l_userMmtOid_s      = c_NOOID_s;
    SET l_groupContainer    = c_NOOID;
    SET l_groupContainer_s  = c_NOOID_s;
    SET l_userContainer     = c_NOOID;
    SET l_userContainer_s   = c_NOOID_s;
    SET l_allUserGroupOid   = c_NOOID;
    SET l_allUserGroupOid_s = c_NOOID_s;
    SET l_adminGroupOid     = c_NOOID;
    SET l_adminGroupOid_s   = c_NOOID_s;
    SET l_userAdminGroupOid = c_NOOID;
    SET l_userAdminGroupOid_s = c_NOOID_s;
    SET l_structAdminGroupOid = c_NOOID;
    SET l_structAdminGroupOid_s = c_NOOID_s;
    SET l_systemUser        = 0;
    SET l_systemUserOid     = c_NOOID;
    SET l_systemUserOid_s   = c_NOOID_s;
    SET l_systemUserName    = 'SysAdmin';
    SET l_layoutContainerOid = c_NOOID;
    SET l_layoutContainerOid_s = c_NOOID_s;
    SET l_layoutOid         = c_NOOID;
    SET l_layoutOid_s       = c_NOOID_s;
    SET l_menutabContainerOid = c_NOOID;
    SET l_menutabContainerOid_s = c_NOOID_s;
    SET l_menutabOid        = c_NOOID;
    SET l_menutabOid_s      = c_NOOID_s;
    SET l_queryContainer    = c_NOOID;
    SET l_queryContainer_s  = c_NOOID_s;
    SET l_queryPublicContainer = c_NOOID;
    SET l_queryPublicContainer_s = c_NOOID_s;
    SET l_adminOid          = c_NOOID;
    SET l_adminOid_s        = c_NOOID_s;
    SET l_workspTemplContainerOid = c_NOOID;
    SET l_workspTemplContainerOid_s = c_NOOID_s;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- create base object:
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind,
        ai_isLink, ai_linkedObjectId_s, ai_description,
        ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'perform create object';
        GOTO exception1;                -- call common exception handler
    END IF;

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        -- create object specific data:
        -- get all rights:
        SET l_sqlcode = 0;
        SELECT  SUM (id) 
        INTO    l_allRights
        FROM    IBSDEV1.ibs_Operation;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get all rights';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create object type specific data:
        -- set default values:
        INSERT  INTO IBSDEV1.ibs_Domain_01
                (oid, sslRequired)
        VALUES  (l_oid, ai_sslRequired);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'INSERT INTO ibs_Domain_01';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get domain id:
        SELECT  id
        INTO    l_domainId
        FROM    IBSDEV1.ibs_Domain_01
        WHERE   oid = l_oid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get domain id';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create administrator of domain:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domAdmin', l_name, l_desc);

        CALL IBSDEV1.p_User_01$new
            (l_domainId, 0, l_name, 'isEnc_K3QjSG5haSBtcGRMLmFBWQ%3D%3D%0A',
            l_name, NULL, NULL, l_admin);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'p_User_01$new';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set administrator flag:
        UPDATE  IBSDEV1.ibs_User
        SET     admin = 1
        WHERE   id = l_admin;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'UPDATE administrator user';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of actual user on domain:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('view', 'read', 'change', 'delete', 'viewRights',
                'viewProtocol');

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get admin rights on domain';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_Rights$setRights (l_oid, ai_userId, l_rights, 0);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'set admin rights';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of admin on domain:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('new', 'view', 'read', 'addElem', 'delElem', 'viewElems');

        IF (l_sqlcode <> 0)
        THEN
            -- create error entry:
            SET l_ePos = 'get administrator rights on domain';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_Rights$setRights (l_oid, l_admin, l_rights, 0);
    
        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'set administrator rights';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create public container of domain:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domPublic', l_name, l_desc);

        CALL IBSDEV1.p_Object$performCreate (l_admin, l_localOp, 16842785, l_name,
            ao_oid_s, 1, 0, c_NOOID_s, l_desc, l_public_s, l_public);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create public container';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- delete all rights on public container:
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_public);
        -- set rights of admin on public container:
        CALL IBSDEV1.p_Rights$setRights (l_public, l_admin, l_allRights, 0);

        -- create user management:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domUserMmt', l_name, l_desc);
        CALL IBSDEV1.p_UserAdminContainer_01$create (l_admin, l_localOp, 16856577,
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_userMmtOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        CALL IBSDEV1.p_stringToByte (l_userMmtOid_s, l_userMmtOid);
    
        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create usermmt';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get group container:
        SELECT  oid
        INTO    l_groupContainer
        FROM    IBSDEV1.ibs_Object
        WHERE   containerId = l_userMmtOid
            AND tVersionId = 16856065;

        IF (l_sqlcode <> 0)
        THEN
            -- create error entry:
            SET l_ePos = 'get group container oid';
            GOTO exception1;            -- call common exception handler
        END IF;
 
        CALL IBSDEV1.p_byteToString (l_groupContainer, l_groupContainer_s);
    
        -- set groups oid before creating first group
        UPDATE  IBSDEV1.ibs_Domain_01
        SET     groupsOid = l_groupContainer
        WHERE   oid = l_oid;

        IF (l_sqlcode <> 0)
        THEN
            -- create error entry:
            SET l_ePos = 'set groups oid';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get user container:
        SELECT  oid
        INTO    l_userContainer
        FROM    IBSDEV1.ibs_Object
        WHERE   containerId = l_userMmtOid
            AND tVersionId = 16855809;

        IF (l_sqlcode <> 0)
        THEN
            -- create error entry:
            SET l_ePos = 'get user container';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_byteToString (l_userContainer, l_userContainer_s);

        -- create group for all users:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domGroupAll', l_name, l_desc);

        CALL IBSDEV1.p_Group_01$create (l_admin, l_localOp, 16842929, l_name,
            l_groupContainer_s, 1, 0, c_NOOID_s, l_desc, l_allUserGroupOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        CALL IBSDEV1.p_stringToByte (l_allUserGroupOid_s, l_allUserGroupOid);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create group for all users';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get id of group with all users:
        SELECT  id
        INTO    l_allUserGroup
        FROM    IBSDEV1.ibs_Group
        WHERE   oid = l_allUserGroupOid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get id of group with all users';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of group with all users on public container:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('view', 'read', 'createLink', 'distribute', 'viewElems');

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get rights of group with all users on public cont.';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_Rights$setRights
                (l_public, l_allUserGroup, l_rights, 0);
 
        -- create domain administrator group:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domGroupAdministrators', l_name, l_desc);
        CALL IBSDEV1.p_Group_01$create (l_admin, l_localOp, 16842929, l_name,
            l_groupContainer_s, 1, 0, c_NOOID_s, l_desc, l_adminGroupOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        CALL IBSDEV1.p_stringToByte (l_adminGroupOid_s, l_adminGroupOid);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create domain administrator group';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get id of domain administrator group:
        SELECT  id
        INTO    l_adminGroup
        FROM    IBSDEV1.ibs_Group
        WHERE   oid = l_adminGroupOid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get id of domain administrator group';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of administrator group on public container:
        CALL IBSDEV1.p_Rights$setRights
            (l_public, l_adminGroup, l_allRights, 0);
        -- set rights of administrator group on user management container:
        SET l_rights = 0;
        CALL IBSDEV1.p_Rights$setRights
            (l_userMmtOid, l_adminGroup, l_rights, 1);

        -- set rights of administrator group on domain:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'read', 'viewElems');

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get rights of administrator group on domain';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_Rights$setRights (l_oid, l_adminGroup, l_rights, 0);

        -- add domain administrator group to the group of all users:
        CALL IBSDEV1.p_Group_01$addGroup
            (l_admin, l_allUserGroupOid, l_adminGroupOid, c_NOOID);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'add domain administrator group to all group';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create user administrator group:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domGroupUserGroupAdmins', l_name, l_desc);
        CALL IBSDEV1.p_Group_01$create (l_admin, l_localOp, 16842929, l_name,
            l_groupContainer_s, 1, 0, c_NOOID_s,
            l_desc, l_userAdminGroupOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        CALL IBSDEV1.p_stringToByte
            (l_userAdminGroupOid_s, l_userAdminGroupOid);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create user administrator group';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get id of user administrator group:
        SELECT  id
        INTO    l_userAdminGroup
        FROM    IBSDEV1.ibs_Group
        WHERE   oid = l_userAdminGroupOid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get id of user administrator group';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of user administrator group on user admin container:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'read', 'createLink', 'viewElems');

        CALL IBSDEV1.p_Rights$setRights
            (l_userMmtOid, l_userAdminGroup, l_rights, 0);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'set rights for user admins1';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of user administrator group on subsequent objects of
        -- user and group container:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('new', 'view', 'read', 'change', 'delete', 'login',
                'viewRights', 'setRights', 'createLink', 'distribute',
                'addElem', 'delElem', 'viewElems', 'viewProtocol');

        CALL IBSDEV1.p_Rights$setRights
            (l_userContainer, l_userAdminGroup, l_rights, 1);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'set rights for user admins2';
            GOTO exception1;            -- call common exception handler
        END IF;

        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('new', 'view', 'read', 'change', 'delete', 'viewRights',
                'setRights', 'createLink', 'distribute', 'addElem',
                'delElem', 'viewElems', 'viewProtocol');

        CALL IBSDEV1.p_Rights$setRights
            (l_groupContainer, l_userAdminGroup, l_rights, 1);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'set rights for user admins3';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- add group of user administrators to the domain administrator group:
        CALL IBSDEV1.p_Group_01$addGroup
            (l_admin, l_adminGroupOid, l_userAdminGroupOid, c_NOOID);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'add user administrators to domain admins';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- add user administrator group to the group of all users:
        CALL IBSDEV1.p_Group_01$addGroup
            (l_admin, l_allUserGroupOid, l_userAdminGroupOid, c_NOOID);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'add user administrators to all group';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create group of common structure administrators:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domGroupStructAdmins', l_name, l_desc);
        CALL IBSDEV1.p_Group_01$create (l_admin, l_localOp, 16842929, l_name,
            l_groupContainer_s, 1, 0, c_NOOID_s, l_desc,
            l_structAdminGroupOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        CALL IBSDEV1.p_stringToByte
            (l_structAdminGroupOid_s, l_structAdminGroupOid);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create common structure admins';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get id of structure administrator group:
        SELECT  id
        INTO    l_structAdminGroup
        FROM    IBSDEV1.ibs_Group
        WHERE   oid = l_structAdminGroupOid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get id of structure admins';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of structure administrator group on public container
        -- and all subsequent objects:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('new', 'view', 'read', 'change', 'delete', 'viewRights',
                'setRights', 'createLink', 'distribute', 'addElem', 'delElem',
                'viewElems', 'viewProtocol');
        CALL IBSDEV1.p_Rights$setRights
            (l_public, l_structAdminGroup, l_rights,1);

        -- set rights of structure administrator group on
        -- user management container:
        SET l_rights = 0;
        CALL IBSDEV1.p_Rights$setRights
            (l_userMmtOid, l_structAdminGroup, l_rights, 1);

        -- add group of structure administrators to the domain administrator
        -- group:
        CALL IBSDEV1.p_Group_01$addGroup
            (l_admin, l_adminGroupOid, l_structAdminGroupOid, c_NOOID);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'add struct admins to domain admins';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- add structure administrator group to the group of all users:
        CALL IBSDEV1.p_Group_01$addGroup
            (l_admin, l_allUserGroupOid, l_structAdminGroupOid, c_NOOID);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'add struct admins to all group';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create container for the user workspaces:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domWorkspaces', l_name, l_desc);
        CALL IBSDEV1.p_Object$performCreate (l_admin, l_localOp, 16842785, l_name,
            ao_oid_s, 1, 0, c_NOOID_s, l_desc, l_workspaces_s, l_workspaces);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create workspaces container';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- delete all rights on workspaces container:
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_workspaces);

        -- set rights of administrators on workspaces container:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('new', 'view', 'read', 'change', 'delete', 'viewRights',
                'setRights', 'createLink', 'distribute', 'addElem', 'delElem',
                'viewElems', 'viewProtocol');
        CALL IBSDEV1.p_Rights$setRights
            (l_workspaces, l_adminGroup, l_rights, 0);
        CALL IBSDEV1.p_Rights$setRights
            (l_workspaces, l_admin, l_rights, 0);

        -- store data in the domain tuple:
        UPDATE  IBSDEV1.ibs_Domain_01
        SET     adminGroupId = l_adminGroup,
                adminId = l_admin,
                allGroupId = l_allUserGroup,
                userAdminGroupId = l_userAdminGroup,
                structAdminGroupId = l_structAdminGroup,
                groupsOid = l_groupContainer,
                usersOid = l_userContainer,
                publicOid = l_public,
                workspacesOid = l_workspaces
        WHERE   oid = l_oid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'store data in the domain tuple';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create layout container:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domLayouts', l_name, l_desc);
        CALL IBSDEV1.p_Object$create
            (l_admin, l_localOp, 16871169, l_name, l_public_s,
            1, 0, c_NOOID_s, l_desc, l_layoutContainerOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create layout container';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights on layout container:
        CALL IBSDEV1.p_stringToByte
            (l_layoutContainerOid_s, l_layoutContainerOid);
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('view', 'read', 'viewRights', 'new', 'addElem', 'delElem',
                'viewElems', 'viewProtocol');
        CALL IBSDEV1.p_Rights$setRights
            (l_layoutContainerOid, l_structAdminGroup, l_rights, 1);
        CALL IBSDEV1.p_Rights$setRights
            (l_layoutContainerOid, l_allUserGroup, 0, 1);

        -- create layout "Standard" for the domain within the layout
        -- container:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domLayoutStandard', l_name, l_desc);
        -- do not use name of standardlayout out of multilingualtables, to avoid
        -- problems with names
        CALL IBSDEV1.p_Layout_01$create (l_admin, l_localOp, 16871425, 'Standard',
            l_layoutContainerOid_s, 1, 0, c_NOOID_s, l_desc, l_layoutOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        CALL IBSDEV1.p_stringToByte (l_layoutOid_s, l_layoutOid);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create Standard layout';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights on layout:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation;

        CALL IBSDEV1.p_Rights$setRights
            (l_layoutOid, l_structAdminGroup, l_rights, 1);

        SELECT  SUM (id)
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'read');

        CALL IBSDEV1.p_Rights$setRights
            (l_layoutOid, l_allUserGroup, l_rights, 1);

        -- create the business object for the administrator of the domain:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domAdmin', l_name, l_desc);
        CALL IBSDEV1.p_User_01$performCreate
            (l_admin, l_localOp, 16842913, l_name,
            l_userContainer_s, 1, 0, c_NOOID_s, l_desc,
            l_admin, l_adminOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        CALL IBSDEV1.p_stringToByte (l_adminOid_s, l_adminOid);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create administrator object';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- add domain administrator to the administrator group:
        CALL IBSDEV1.p_Group_01$addUser
            (l_admin, l_adminGroupOid, l_adminOid, c_NOOID);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'add domain admin to admin group';
            GOTO exception1;            -- call common exception handler
        END IF;

        --*********************************************************************
        --** create systemcontainer for menutabs                            --*
        --*********************************************************************
        -- create menutab container:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domMenuTabs', l_name, l_desc);

        SELECT  actVersion
        INTO    l_tVersionId
        FROM    IBSDEV1.ibs_Type
        WHERE   code = 'MenuTabContainer';

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get tVersionId of MenuTabContainer';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_Object$create
            (l_admin, l_localOp, l_tVersionId, l_name, l_public_s,
            1, 0, c_NOOID_s, l_desc, l_menutabContainerOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create menu tab container';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights on menutab container:
        CALL IBSDEV1.p_stringToByte
            (l_menutabContainerOid_s, l_menutabContainerOid);

        -- delete all rights on menutab container:
        CALL IBSDEV1.p_Rights$deleteObjectRights(l_menutabContainerOid);

        -- set rights for user Administrator on menutab container:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('new', 'view', 'read', 'change', 'delete', 'viewRights',
                'setRights', 'createLink', 'distribute', 'addElem', 'delElem',
                'viewElems', 'viewProtocol');
        CALL IBSDEV1.p_Rights$setRights
            (l_menutabContainerOid, l_admin, l_rights, 0);

        --**********************************************************************
        --** fills the table ibs_MenuTab_01 with tuples which are necessary   **
        --** to show the tabs at the upper left side of the application       **
        --**********************************************************************
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domPublic', l_name, l_desc);

        SELECT  actVersion
        INTO    l_tVersionId
        FROM    IBSDEV1.ibs_Type
        WHERE   code = 'MenuTab';

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get tVersionId of MenuTab';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_MenuTab_01$create
            (l_admin, l_localOp, l_tVersionId, l_name,
            l_menutabContainerOid_s, 1, 0, c_NOOID_s,
            l_desc, l_menutabOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create group menu tab';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_stringToByte (l_menutabOid_s, l_menutabOid);

        -- update:
        UPDATE  IBSDEV1.ibs_MenuTab_01
        SET     objectOid = l_public,
                description = l_name,
                priorityKey = 1,
                isPrivate = 0,
                domainId = l_domainId,
                classFront = 'groupFront',
                classBack = 'groupBack',
                fileName = 'group.htm'
        WHERE   oid = l_menutabOid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'update group menu tab';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- get the name of the tab privat, multilinguality
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_wspPrivate', l_name, l_desc);

        -- fill the table ibs_MenuTab_01 with tuples for private tab
        CALL IBSDEV1.p_MenuTab_01$create
            (l_admin, l_localOp, l_tVersionId, l_name,
            l_menutabContainerOid_s, 1, 0, c_NOOID_s, l_desc, l_menutabOid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create private menu tab';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_stringToByte (l_menutabOid_s, l_menutabOid);
    
        -- update:
        UPDATE  IBSDEV1.ibs_MenuTab_01
        SET     objectOid = l_workspaces,
                description = l_name,
                priorityKey = 10,
                isPrivate = 1,
                domainId = l_domainId,
                classFront = 'privateFront',
                classBack = 'privateBack',
                fileName = 'private.htm'
        WHERE   oid = l_menutabOid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'update private menu tab';
            GOTO exception1;            -- call common exception handler
        END IF;

        --*********************************************************************
        --** create systemcontainer for queries                              **
        --*********************************************************************
        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'cumulate administrator rights';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create system user used for customizing tasks ...
        CALL IBSDEV1.p_User_01$createFast (l_admin, l_domainId, l_systemUserName,
            'isEnc_K3QjSHNhbyB0cG5MLmFpWQ%3D%3D%0A', l_systemUserName,
            l_systemUserOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create system user';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_byteToString (l_systemUserOid, l_systemUserOid_s);

        -- get id of system user:
        SELECT  id
        INTO    l_systemUser
        FROM    IBSDEV1.ibs_User
        WHERE   oid = l_systemUserOid;

--CALL IBSDEV1.logError (100, 'p_Domain_01$create', l_sqlcode, 'system user', 'l_systemUser', l_systemUser, 'l_systemUserOid_s', l_systemUserOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get id of system user';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- create container for systemqueries:
        CALL IBSDEV1.p_Object$performCreate (l_systemUser, l_localOp, 16875329,
            'Systemqueries', l_public_s, 1, 0, c_NOOID_s,
            'This container contains all querycreators used in this domain, it can be only seen by the system user.',
            l_queryContainer_s, l_queryContainer);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create systemqueries container';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- set rights of system user on query container
        -- delete all rights on search container:
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_queryContainer);

        -- set rights for system user on search container:
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN
                ('new', 'view', 'read', 'change', 'delete', 'viewRights',
                'setRights', 'createLink', 'distribute', 'addElem', 'delElem',
                'viewElems', 'viewProtocol');
    
        CALL IBSDEV1.p_Rights$setRights
            (l_queryContainer, l_systemUser, l_rights, 0);

        -- create container for querytemplates seen by public users:
        CALL IBSDEV1.p_Object$performCreate (l_systemUser, l_localOp, 16875329,
            'Publicqueries', l_queryContainer_s, 1, 0, c_NOOID_s,
            'This container contains all querycreators for public usage.',
            l_queryPublicContainer_s, l_queryPublicContainer);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    
        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create public query templates container';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- delete all rights on search container:
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_queryPublicContainer);

        -- set all rights for system user:
        CALL IBSDEV1.p_Rights$setRights
            (l_queryPublicContainer, l_systemUser, l_rights, 0);

        -- set rights of group with all users on public container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'viewElems');

        CALL IBSDEV1.p_Rights$setRights
            (l_queryPublicContainer, l_allUserGroup, l_rights, 0);

        -- create all standard querytemplates which should be seen by every user
        CALL IBSDEV1.p_createBaseQueryCreators
            (l_systemUser, l_queryPublicContainer_s);
        -- there exists no return value
    
        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create base query creators';
            GOTO exception1;            -- call common exception handler
        END IF;

        --*********************************************************************
        --** create systemcontainer workspacetemplates                       **
        --*********************************************************************

        SELECT  actVersion
        INTO    l_tvWorkspaceTempCont
        FROM    IBSDEV1.ibs_Type
        WHERE   code = 'WorkspaceTemplateContainer';
    
        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get tVersionId of WorkspaceTemplateContainer';
            GOTO exception1;            -- call common exception handler
        END IF;

        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_domWorkspaceTemplate', l_name, l_desc);

        CALL IBSDEV1.p_Object$performCreate
            (l_admin, l_localOp, l_tvWorkspaceTempCont,
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc,
            l_workspTemplContainerOid_s, l_workspTemplContainerOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create workspace template container';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- delete rights on workspacetemplate container
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_workspTemplContainerOid);
    END IF; -- if object created successfully?

    -- finish the transaction:
    COMMIT;                             -- make changes permanent

CALL IBSDEV1.logError (100, 'p_Domain_01$create', l_sqlcode, 'end', 'l_retValue', l_retValue, 'ao_oid_s', ao_oid_s, '', 0, 'ai_name', ai_name, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Domain_01$create1', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'ao_oid_s', ao_oid_s,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_description', ai_description,
        'ai_tVersionId', ai_tVersionId, 'ai_containerId_s', ai_containerId_s,
        'ai_containerKind', ai_containerKind, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_isLink', ai_isLink, '', '',
        'ai_sslRequired', ai_sslRequired, '', '',
        '', 0, 'l_userMmtOid_s', l_userMmtOid_s,
        '', 0, '', '',
        '', 0, '', '');
    CALL IBSDEV1.logError (500, 'p_Domain_01$create2', l_sqlcode, l_ePos,
        'l_domainId', l_domainId, 'l_name', l_name,
        'l_tVersionId', l_tVersionId, 'l_desc', l_desc,
        'l_admin', l_admin, 'l_adminOid_s', l_adminOid_s,
        'l_systemUser', l_systemUser, 'l_systemUserOid_s', l_systemUserOid_s,
        'l_allUserGroup', l_allUserGroup, 'l_allUserGroupOid_s', l_allUserGroupOid_s,
        'l_adminGroup', l_adminGroup, 'l_userAdminGroupOid_s', l_userAdminGroupOid_s,
        'l_userAdminGroup', l_userAdminGroup, 'l_userAdminGroupOid_s', l_userAdminGroupOid_s,
        'l_structAdminGroup', l_structAdminGroup, 'l_structAdminGroupOid_s', l_structAdminGroupOid_s,
        'l_rights', l_rights, 'l_systemUserName', l_systemUserName,
        'l_tvWorkspaceTempCont', l_tvWorkspaceTempCont, 'l_workspTemplContainerOid_s', l_workspTemplContainerOid_s);
    CALL IBSDEV1.logError (500, 'p_Domain_01$create3', l_sqlcode, l_ePos,
        '', 0, 'l_public_s', l_public_s,
        '', 0, 'l_workspaces_s', l_workspaces_s,
        '', 0, 'l_groupContainer_s', l_groupContainer_s,
        '', 0, 'l_userContainer_s', l_userContainer_s,
        '', 0, 'l_layoutContainerOid_s', l_layoutContainerOid_s,
        '', 0, 'l_layoutOid_s', l_layoutOid_s,
        '', 0, 'l_menutabContainerOid_s', l_menutabContainerOid_s,
        '', 0, 'l_menutabOid_s', l_menutabOid_s,
        '', 0, 'l_queryContainer_s', l_queryContainer_s,
        '', 0, 'l_queryPublicContainer_s', l_queryPublicContainer_s);
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Domain_01$create


--------------------------------------------------------------------------------
-- Set the scheme of a domain. <BR>
-- This procedure also performs some operations which are corresponding to
-- the selected scheme, i.e. creating a catalog management.
--
-- @input parameters:
-- @param   ai_userId           Id of the user who is setting the scheme.
-- @param   ai_id               Id of the domain.
-- @param   ai_schemeId         Id of the domain scheme.
-- @param   ai_homepagePath     Homepage path of the domain, i.e. the path
--                              where it resides, e.g. '/m2/'.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Domain_01$setScheme');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Domain_01$setScheme
(
    -- common input parameters:
    IN  ai_userId           INT,
    IN  ai_id               INT,
    IN  ai_schemeId         INT,
    IN  ai_homepagePath     VARCHAR (63)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_languageId    INT DEFAULT 0;  -- the current language
    
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
    DECLARE l_oid_s         VARCHAR (18);    -- the actual oid as string
    DECLARE l_cid           CHAR (8) FOR BIT DATA; -- the actual containerId
    DECLARE l_cid_s         VARCHAR (18);    -- the actual containerId as string
    DECLARE l_name          VARCHAR (63);    -- the actual name
    DECLARE l_desc          VARCHAR (255);   -- the actual description
    DECLARE l_public        CHAR (8) FOR BIT DATA;
    DECLARE l_public_s      VARCHAR (18);
    DECLARE l_allGroupId    INT;
    DECLARE l_admin         INT;
    DECLARE l_adminGroup    INT;
    DECLARE l_userAdminGroup INT;
    DECLARE l_structAdminGroup INT;
    DECLARE l_localOp       INT DEFAULT 0;  -- operation for local operations
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
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;
    SET c_languageId        = 0;
  
    -- initialize local variables:
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;
    SET l_oid_s             = c_NOOID_s;
    SET l_cid               = c_NOOID;
    SET l_cid_s             = c_NOOID_s;
    SET l_public            = c_NOOID;
    SET l_public_s          = c_NOOID_s;

-- body:
CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'start', 'l_retValue', l_retValue, '', '', 'ai_userId', ai_userId, '', '', 'ai_id', ai_id, '', '', 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- set the domain scheme:
    UPDATE  IBSDEV1.ibs_Domain_01
    SET     (scheme, workspaceProc, homepagePath) =
            (
                SELECT  DISTINCT s.id, s.workspaceProc, ai_homepagePath
                FROM    IBSDEV1.ibs_Domain_01 d, IBSDEV1.ibs_DomainScheme_01 s
                WHERE   s.id = ai_schemeId
                    AND d.id = ai_id
            );

-- WHERE EXISTS
--       (SELECT  *  
--        FROM IBSDEV1.ibs_Domain_01, IBSDEV1.ibs_DomainScheme_01
--        WHERE s.id = (Untranslated expression) AND
--              d.id = (Untranslated expression));
  
    -- get public container and group of all users:
    SELECT  publicOid, allGroupId, adminId, adminGroupId,
            userAdminGroupId, structAdminGroupId
    INTO    l_public, l_allGroupId, l_admin, l_adminGroup,
            l_userAdminGroup, l_structAdminGroup
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   id = ai_id;

    CALL IBSDEV1.p_byteToString (l_public, l_public_s);
  
--CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'middle', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- check if there is a data interchange component to create for the domain:
    IF EXISTS   (   SELECT  *
                    FROM    IBSDEV1.ibs_DomainScheme_01
                    WHERE   id = ai_schemeId
                        AND hasDataInterchange = 1
                )
    THEN 
--CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'create data interchange', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        -- create import/export management:
        -- Data Interchange:
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_domDataInterchange', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (l_admin, l_localOp, 16872449, l_name,
            l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        CALL IBSDEV1.p_stringToByte (l_cid_s, l_cid);

        -- set rights on import/export management:
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_cid);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_admin);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_adminGroup);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_userAdminGroup);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_structAdminGroup);
        -- Import
        CALL IBSDEV1.p_ObjectDesc_01$get(c_languageId, 'OD_domDIImport', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (l_admin, l_localOp, 16873729, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- Export
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_domDIExport', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (l_admin, l_localOp, 16873985, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
/*
    ELSE
CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'no data interchange', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
*/
    END IF;
CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'end', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
END;
-- p_Domain_01$setScheme


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_id               ID of the object to be changed.
-- @param   ai_userId           ID of the user who is changing the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_name             Name of the object.
-- @param   ai_validUntil       Date until which the object is valid.
-- @param   ai_description      Description of the object.
-- @param   ai_showInNews       Shall the currrent object be displayed in the
--                              news?
-- @param   ai_domainScheme     The id of the domain scheme of the domain.
--                              If this is different from the actual one the
--                              procedure p_Domain_01$setScheme is called to
--                              set the new scheme.
-- @param   ai_homepagePath     Contains the homepagepath of the domain.
-- @param   ai_sslRequired      The flag if SSL must be used or not.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Domain_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Domain_01$change(
    -- common input parameters:
    IN    ai_oid_s          VARCHAR (18),
    IN    ai_userId         INT,
    IN    ai_op             INT,
    IN    ai_name           VARCHAR (63),
    IN    ai_validUntil     TIMESTAMP,
    IN    ai_description    VARCHAR (255),
    IN    ai_showInNews     SMALLINT,
    -- type-specific input parameters:
    IN    ai_domainScheme   INT,
    IN    ai_homepagePath   VARCHAR (63),
    IN    ai_sslRequired    SMALLINT
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
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_oldDomainScheme INT;          -- the old domain scheme
    DECLARE l_id            INT;            -- the id of the domain
    DECLARE l_homepagePath  VARCHAR (63);    -- local copy of ai_homepagePath
    DECLARE l_oldHomepagePath VARCHAR (63);  -- the old homepage path of the domain
    DECLARE l_adminId       INT;            -- id of domain administrator
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables:
    SET l_oid               = c_NOOID;
    SET l_retValue          = c_NOT_OK;

-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN        -- operation performed properly?
        IF (ai_homepagePath IS NULL) OR (ltrim (ai_homepagePath) || 'x' = 'x') THEN 
                                            -- no homepagePath shall be set?
            SET l_homepagePath = NULL;
        ELSE 
                                            -- there is a homepagePath to be set
            SET l_homepagePath = ai_homepagePath;
        END IF;
        -- get the current domain scheme:
        SET l_oldDomainScheme = ai_domainScheme;

        SELECT id, scheme,homepagePath , adminId
        INTO l_id, l_oldDomainScheme, l_oldHomepagePath, l_adminId
        FROM IBSDEV1.ibs_Domain_01
        WHERE oid = l_oid;

        IF l_oldDomainScheme <> ai_domainScheme THEN  -- the scheme shall be changed?
            -- update object type specific data:
            CALL IBSDEV1.p_Domain_01$setScheme(l_adminId, l_id, ai_domainScheme,
                l_homepagePath);
        END IF;
        -- set the new homepagePath:
        UPDATE IBSDEV1.ibs_Domain_01
        SET homepagePath = l_homepagePath,
            sslRequired = ai_sslRequired
        WHERE oid = l_oid;
    END IF;
    -- if operation performed properly
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Domain_01$change


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @param   ai_oid_s            ID of the object to be retrieved.
-- @param   ai_userId           Id of the user who is getting the data.
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
-- @param   ao_ownerName        Name of the owner of the object.
-- @param   ao_creationDate     Date when the object was created.
-- @param   ao_creator          ID of person who created the object.
-- @param   ao_creatorName      Name of person who created the object.
-- @param   ao_lastChanged      Date of the last change of the object.
-- @param   ao_changer          ID of person who did the last change to the
--                              object.
-- @param   ao_changerName      Name of person who did the last change to the
--                              object.
-- @param   ao_validUntil       Date until which the object is valid.
-- @param   ao_description      Description of the object.
-- @param   ao_showInNews       The showInNews flag.
-- @param   ao_checkedOut       Is the object checked out?
-- @param   ao_checkOutDate     Date when the object was checked out
-- @param   ao_checkOutUser     Oid of the user which checked out the object
-- @param   ai_checkOutUserOid  Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   ao_checkOutUserName name of the user which checked out the object,
--                              is only set if this user has the right to read
--                              the checkOut-User
-- @param   ao_domainScheme     The id of the domain scheme.
-- @param   ao_domainSchemeName The name of the domain scheme.
-- @param   ao_homepagePath     The homepagepaht of the domain.
-- @param   ao_sslRequired      The flag if SSL must be used or not.
--
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Domain_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Domain_01$retrieve(
    -- common input parameters:
    IN    ai_oid_s          VARCHAR (18),
    IN    ai_userId         INT,
    IN    ai_op             INT,
    -- common output parameters:
    OUT ao_state          INT,
    OUT ao_tVersionId     INT,
    OUT ao_typeName       VARCHAR (63),
    OUT ao_name           VARCHAR (63),
    OUT ao_containerId    CHAR (8) FOR BIT DATA,
    OUT ao_containerName  VARCHAR (63),
    OUT ao_containerKind  INT,
    OUT ao_isLink         SMALLINT,
    OUT ao_linkedObjectId CHAR (8) FOR BIT DATA,
    OUT ao_owner          INT,
    OUT ao_ownerName      VARCHAR (63),
    OUT ao_creationDate   TIMESTAMP,
    OUT ao_creator        INT,
    OUT ao_creatorName    VARCHAR (63),
    OUT ao_lastChanged    TIMESTAMP,
    OUT ao_changer        INT,
    OUT ao_changerName    VARCHAR (63),
    OUT ao_validUntil     TIMESTAMP,
    OUT ao_description    VARCHAR (255),
    OUT ao_showInNews     SMALLINT,
    OUT ao_checkedOut     SMALLINT,
    OUT ao_checkOutDate   TIMESTAMP,
    OUT ao_checkOutUser   INT,
    OUT ao_checkOutUserOid CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    -- type-specific output parameters:
    OUT ao_domainScheme   INT,
    OUT ao_domainSchemeName VARCHAR (63),
    OUT ao_homepagePath   VARCHAR (63),
    OUT ao_sslRequired    SMALLINT
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
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
  
    -- initialize local variables:
    SET l_oid               = c_NOOID;
    SET l_retValue          = c_NOT_OK;

-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve
        (ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
        ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN -- operation performed properly?
        SET l_sqlcode = 0;
        -- retrieve object type specific data:
        SELECT homepagePath, d.sslRequired
        INTO ao_homepagePath, ao_sslRequired
        FROM IBSDEV1.ibs_Domain_01 d
        WHERE d.oid = l_oid;

        -- retrieve domain scheme data:
        SELECT d.scheme, o.name 
        INTO ao_domainScheme, ao_domainSchemeName
        FROM IBSDEV1.ibs_Domain_01 d, IBSDEV1.ibs_DomainScheme_01 ds, IBSDEV1.ibs_Object o
        WHERE d.oid = l_oid
            AND d.scheme = ds.id
            AND o.oid = ds.oid;

        -- check if retrieve was performed properly:
        IF l_sqlcode <> 0 THEN             -- no row affected?
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;
    -- if operation performed properly
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Domain_01$retrieve


--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be deleted.
-- @param   ai_userId           ID of the user who is deleting the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Domain_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Domain_01$delete(
    -- common input parameters:
    IN    ai_oid_s          VARCHAR (18),
    IN    ai_userId         INT,
    IN    ai_op             INT
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
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
  
    -- initialize local variables:
    SET l_oid               = c_NOOID;
    SET l_retValue          = c_NOT_OK;
  
-- body:
    -- delete base object:
    CALL IBSDEV1.p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN 
        -- delete object type specific data:
        -- (deletes all type specific tuples which are not within ibs_Object)
        DELETE FROM IBSDEV1.ibs_Domain_01
        WHERE oid NOT IN    (
                                SELECT oid 
                                FROM IBSDEV1.ibs_Object
                            );
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    
        -- check if deletion was performed properly:
        IF l_rowcount <= 0 THEN             -- no row affected?
            SET l_retValue = c_NOT_OK;      -- set return value
        END IF;
        -- deletes all tuples in ibs_Menu wich are not in ibs_Domain_01
        -- excepted the tuple of the system root because it is in no domain
        DELETE FROM IBSDEV1.ibs_MenuTab_01
        WHERE domainId NOT IN   (   
                                    SELECT id 
                                    FROM IBSDEV1.ibs_Domain_01
                                ) AND domainId > 0;
    END IF;
  
    -- if operation performed properly
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Domain_01$delete


--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
--
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be deleted.
-- @param   ai_userId           ID of the user who is deleting the object.
-- @param   ai_newOid           The oid of the copy.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Domain_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Domain_01$BOCopy(
    -- common input parameters:
    IN    ai_oid            CHAR (8) FOR BIT DATA,
    IN    ai_userId         INT,
    IN    ai_newOid         CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this function
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
    SET l_retValue          = c_NOT_OK;

-- body:
    -- make an insert for all type specific tables:
    COMMIT;
    -- check if insert was performed correctly:
    INSERT  INTO IBSDEV1.ibs_Domain_01
            (oid, adminGroupId, adminId,
            allGroupId, userAdminGroupId, structAdminGroupId,
            groupsOid, usersOid, homepagePath, logo, scheme,
            workspaceProc, sslRequired)
    SELECT  ai_newOid, adminGroupId, adminId,
            allGroupId, userAdminGroupId, structAdminGroupId,
            groupsOid, usersOid, homepagePath, logo, scheme,
            workspaceProc, sslRequired
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   oid = ai_oid;

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    IF l_rowcount >= 1 THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Domain_01$BOCopy
