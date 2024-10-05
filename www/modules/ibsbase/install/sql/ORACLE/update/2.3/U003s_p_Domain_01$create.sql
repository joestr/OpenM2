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
 * @param   ai_sslRequired      Flag if SSL must be used for this domain or not.
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Domain_01$create
(
    -- common input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    ai_sslRequired          NUMBER,
    -- common output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
                                                -- no defined oid as string
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this operation
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists
    c_languageId            CONSTANT INTEGER := 0; -- the current language (default)

    -- local variables:
    l_retValue              INT := c_NOT_OK;    -- return value of this function
    l_oid                   RAW (8) := c_NOOID; -- the actual oid
    l_name                  VARCHAR2 (63);  -- the actual name
    l_desc                  VARCHAR2 (255); -- the actual description
    l_domainId              INTEGER;
    l_tVersionId            INTEGER;
    l_admin                 INTEGER;
    l_admin2                INTEGER;
    l_systemUser            INTEGER;
    l_systemUserOid         RAW (8);
    l_systemUserName        VARCHAR2 (63) := 'SysAdmin';
    l_rights                INTEGER;
    l_allRights             INTEGER;
    l_public                RAW (8);
    l_public_s              VARCHAR2 (18);
    l_workspaces            RAW (8);
    l_workspaces_s          VARCHAR2 (18);
    l_userMmtOid            RAW (8);
    l_userMmtOid_s          VARCHAR2 (18);
    l_groupContainer        RAW (8);
    l_groupContainer_s      VARCHAR2 (18);
    l_userContainer         RAW (8);
    l_userContainer_s       VARCHAR2 (18);
    l_allUserGroup          INTEGER;
    l_allUserGroupOid       RAW (8);
    l_allUserGroupOid_s     VARCHAR2 (18);
    l_adminGroup            INTEGER;
    l_adminGroupOid         RAW (8);
    l_adminGroupOid_s       VARCHAR2 (18);
    l_userAdminGroup        INTEGER;
    l_userAdminGroupOid     RAW (8);
    l_userAdminGroupOid_s   VARCHAR2 (18);
    l_structAdminGroup      INTEGER;
    l_structAdminGroupOid   RAW (8);
    l_structAdminGroupOid_s VARCHAR2 (18);
    l_layoutContainerOid    RAW (8);
    l_layoutContainerOid_s  VARCHAR2 (18);
    l_layoutOid             RAW (8);
    l_layoutOid_s           VARCHAR2 (18);
    l_menutabContainerOid   RAW (8);
    l_menutabContainerOid_s VARCHAR2 (18);
    l_menutabOid            RAW (8);
    l_menutabOid_s          VARCHAR2 (18);
    l_queryContainer        RAW (8);
    l_queryContainer_s      VARCHAR2 (18);
    l_queryPublicContainer  RAW (8);
    l_queryPublicContainer_s VARCHAR2 (18);
    l_adminOid              RAW (8);
    l_adminOid_s            VARCHAR2 (18);
    l_workspTemplContainerOid ibs_Object.containerId%TYPE;
    l_workspTemplContainerOid_s VARCHAR2 (18);
    l_tvWorkspaceTempCont   INTEGER;
    l_rightsAll             INTEGER;
    l_dummy                 INTEGER;

-- body:
BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                        ai_name, ai_containerId_s, ai_containerKind,
                        ai_isLink, ai_linkedObjectId_s, ai_description,
                        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)   -- object created successfully?
    THEN
        -- create object specific data:
        -- get all rights:
        BEGIN
            SELECT  SUM (id)
            INTO    l_allRights
            FROM    ibs_Operation;
        EXCEPTION
            WHEN OTHERS THEN
                err;
        END;

        -- create object type specific data:
        -- set default values:
        BEGIN
            INSERT INTO ibs_Domain_01 (oid, sslRequired)
            VALUES  (l_oid, ai_sslRequired);
        EXCEPTION
            WHEN OTHERS THEN
                err;
        END;
        -- get domain id:
        BEGIN
            SELECT  id
            INTO    l_domainId
            FROM    ibs_Domain_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                err;
        END;

        p_ObjectDesc_01$get (c_languageId, 'OD_domAdmin', l_name, l_desc);
        l_dummy := p_User_01$new (
            l_domainId, 0, l_name, 'isEnc_K3QjSG5haSBtcGRMLmFBWQ%3D%3D',
            l_name, null, null, l_admin);

        -- set administrator flag:
        UPDATE  ibs_User
        SET     admin = 1
        WHERE   id = l_admin;

        -- set rights of actual user on domain:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'change', 'delete', 'viewRights',
                        'viewProtocol');
        p_Rights$setRights (l_oid, ai_userId, l_rights, 0);

        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'view', 'read', 'addElem', 'delElem', 'viewElems');

        p_Rights$setRights (l_oid, l_admin, l_rights, 0);

        p_ObjectDesc_01$get (c_languageId, 'OD_domPublic', l_name, l_desc);
        l_retValue := p_Object$performCreate (l_admin, 1, 16842785,
            l_name, ao_oid_s, 1, 0, c_NOOID_s, l_desc,
            l_public_s, l_public);

        -- delete all rights on public container:
        UPDATE  ibs_Object
        SET     rKey = 0
        WHERE   oid = l_public;
        -- set rights of admin on public container:
        p_Rights$setRights (l_public, l_admin, l_allRights, 0);

        -- create user management:
        p_ObjectDesc_01$get (c_languageId, 'OD_domUserMmt', l_name, l_desc);
        l_retValue := p_userAdminContainer_01$create (l_admin, ai_op, 16856577,
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc,
            l_userMmtOid_s);
        p_stringToByte (l_userMmtOid_s, l_userMmtOid);

        -- get group container:
        SELECT  oid
        INTO    l_groupContainer
        FROM    ibs_Object
        WHERE   containerId = l_userMmtOid
            AND tVersionId = 16856065;
        p_byteToString (l_groupContainer, l_groupContainer_s);

        -- set groups oid before creating first group
        UPDATE  ibs_Domain_01
        SET     groupsOid = l_groupContainer
        WHERE   oid = l_oid;

        -- get user container:
        SELECT  oid
        INTO    l_userContainer
        FROM    ibs_Object
        WHERE   containerId = l_userMmtOid
            AND tVersionId = 16855809;
        p_byteToString (l_userContainer, l_userContainer_s);

        -- create group for all users:
        p_ObjectDesc_01$get (c_languageId, 'OD_domGroupAll', l_name, l_desc);
        l_retValue := p_Group_01$create (l_admin, ai_op, 16842929,
            l_name, l_groupContainer_s, 1, 0, c_NOOID_s, l_desc,
            l_allUserGroupOid_s);
        p_stringToByte (l_allUserGroupOid_s, l_allUserGroupOid);

        -- get id of group with all users:
        SELECT  id
        INTO    l_allUserGroup
        FROM    ibs_Group
        WHERE   oid = l_allUserGroupOid;

        -- set rights of group with all users on public container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'createLink', 'distribute', 'viewElems');
        p_Rights$setRights (l_public, l_allUserGroup, l_rights, 0);

        -- create domain administrator group:
        p_ObjectDesc_01$get (c_languageId, 'OD_domGroupAdministrators', l_name, l_desc);
        l_retValue := p_Group_01$create (l_admin, ai_op, 16842929,
            l_name, l_groupContainer_s, 1, 0, c_NOOID_s, l_desc,
            l_adminGroupOid_s);
        p_stringToByte (l_adminGroupOid_s, l_adminGroupOid);
        -- get id of domain administrator group:
        SELECT  id
        INTO    l_adminGroup
        FROM    ibs_Group
        WHERE   oid = l_adminGroupOid;
        -- set rights of administrator group on public container:
        p_Rights$setRights (l_public, l_adminGroup, l_allRights, 0);

        -- set rights of administrator group on user management container:
        l_rights := 0;
        p_Rights$setRights (l_userMmtOid, l_adminGroup, l_rights, 1);
        -- set rights of administrator group on domain:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'viewElems');
        p_Rights$setRights (l_oid, l_adminGroup, l_rights, 0);

        -- add domain administrator group to the group of all users:
        l_dummy := p_Group_01$addGroup (l_admin, l_allUserGroupOid, l_adminGroupOid, c_NOOID);

        -- create user administrator group:
        p_ObjectDesc_01$get (c_languageId, 'OD_domGroupUserGroupAdmins', l_name, l_desc);
        l_retValue := p_Group_01$create (l_admin, ai_op, 16842929,
            l_name, l_groupContainer_s, 1, 0, c_NOOID_s, l_desc,
            l_userAdminGroupOid_s);
        p_stringToByte (l_userAdminGroupOid_s, l_userAdminGroupOid);
        -- get id of user administrator group:
        SELECT  id
        INTO    l_userAdminGroup
        FROM    ibs_Group
        WHERE   oid = l_userAdminGroupOid;

        -- set rights of user administrator group on user admin container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'createLink', 'viewElems');
        p_Rights$setRights (l_userMmtOid, l_userAdminGroup, l_rights, 0);

        -- set rights of user administrator group on subsequent objects of
        -- user and group container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'view', 'read', 'change', 'delete', 'login',
                        'viewRights', 'setRights', 'createLink', 'distribute',
                        'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_userContainer, l_userAdminGroup, l_rights, 1);
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'view', 'read', 'change', 'delete', -- no login
                        'viewRights', 'setRights', 'createLink', 'distribute',
                        'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_groupContainer, l_userAdminGroup, l_rights, 1);

        -- add group of user administrators to the domain administrator group:
        l_dummy := p_Group_01$addGroup (l_admin,
            l_adminGroupOid, l_userAdminGroupOid, c_NOOID);
        -- add user administrator group to the group of all users:
        l_dummy := p_Group_01$addGroup (l_admin,
            l_allUserGroupOid, l_userAdminGroupOid, c_NOOID);

        -- create group of common structure administrators:
        p_ObjectDesc_01$get (c_languageId, 'OD_domGroupStructAdmins', l_name, l_desc);
        l_retValue := p_Group_01$create (l_admin, ai_op, 16842929,
            l_name, l_groupContainer_s, 1, 0, c_NOOID_s, l_desc,
            l_structAdminGroupOid_s);
        p_stringToByte (l_structAdminGroupOid_s, l_structAdminGroupOid);
        -- get id of user administrator group:
        SELECT  id
        INTO    l_structAdminGroup
        FROM    ibs_Group
        WHERE   oid = l_structAdminGroupOid;

        -- set rights of structure administrator group on public container
        -- and all subsequent objects:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'view', 'read', 'change', 'delete',
                        'viewRights', 'setRights', 'createLink', 'distribute',
                        'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_public, l_structAdminGroup, l_rights, 1);

        -- set rights of structure administrator group on
        -- user management container:
        l_rights := 0;

        p_Rights$setRights (l_userMmtOid, l_structAdminGroup, l_rights, 1);

        -- add group of structure administrators to the domain administrator
        -- group:
        l_dummy := p_Group_01$addGroup (l_admin,
            l_adminGroupOid, l_structAdminGroupOid, c_NOOID);
        -- add structure administrator group to the group of all users:
        l_dummy := p_Group_01$addGroup (l_admin,
            l_allUserGroupOid, l_structAdminGroupOid, c_NOOID);

        -- create container for the user workspaces:
        p_ObjectDesc_01$get (c_languageId, 'OD_domWorkspaces', l_name, l_desc);
        l_retValue := p_Object$performCreate (l_admin, 1, 16842785,
            l_name, ao_oid_s, 1, 0, c_NOOID_s, l_desc,
            l_workspaces_s, l_workspaces);

        -- delete all rights on workspaces container:
        UPDATE  ibs_Object
        SET     rKey = 0
        WHERE   oid = l_workspaces;
        -- set rights of administrators on workspaces container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'view', 'read', 'change', 'delete',
                        'viewRights', 'setRights', 'createLink', 'distribute',
                        'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_workspaces, l_adminGroup, l_rights, 0);
        p_Rights$setRights (l_workspaces, l_admin, l_rights, 0);

        -- store data in the domain tuple:
        UPDATE  ibs_Domain_01
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

        -- create layout container:
        p_ObjectDesc_01$get (c_languageId, 'OD_domLayouts', l_name, l_desc);
        l_retValue := p_Object$create (l_admin, ai_op, 16871169,
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc,
            l_layoutContainerOid_s);

        -- set rights on layout container:
        p_stringToByte (l_layoutContainerOid_s, l_layoutContainerOid);

        -- set rights:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'viewRights',
                    'new', 'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_layoutContainerOid, l_structAdminGroup, l_rights, 1);
        p_Rights$setRights (l_layoutContainerOid, l_allUserGroup, 0, 1);

        -- create layout "Standard" for the domain within the layout
        -- container:
        p_ObjectDesc_01$get (c_languageId, 'OD_domLayoutStandard', l_name, l_desc);
        -- do not use name of standardlayout out of multilingualtables, to avoid problems with names
        l_retValue := p_Layout_01$create (l_admin, ai_op, 16871425,
            'Standard', l_layoutContainerOid_s, 1, 0, c_NOOID_s, l_desc,
            l_layoutOid_s);
        p_stringToByte (l_layoutOid_s, l_layoutOid);

        -- set rights on layout:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation;
        p_Rights$setRights (l_layoutOid, l_structAdminGroup, l_rights, 1);
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read');
        p_Rights$setRights (l_layoutOid, l_allUserGroup, l_rights, 1);

        -- create the business object for the administrator of the domain:
        p_ObjectDesc_01$get (c_languageId, 'OD_domAdmin', l_name, l_desc);
        l_retValue := p_User_01$performCreate (l_admin, ai_op, 16842913,
            l_name, l_userContainer_s, 1, 0, c_NOOID_s, l_desc, l_admin,
            l_adminOid_s);
        p_stringToByte (l_adminOid_s, l_adminOid);

        -- add domain administrator to the administrator group:
        l_dummy := p_Group_01$addUser (l_admin, l_adminGroupOid, l_adminOid, c_NOOID);

        --*****************************************************************************
        --** create systemcontainer for menutabs                                     **
        --*****************************************************************************

        -- create menutab container:
        p_ObjectDesc_01$get (c_languageId, 'OD_domMenuTabs', l_name, l_desc);

        SELECT  actVersion INTO l_tVersionId
        FROM    ibs_Type WHERE code = 'MenuTabContainer';

        l_retValue := p_Object$create (l_admin, ai_op, l_tVersionId,
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc,
            l_menutabContainerOid_s);

        -- set rights on menutab container:
        p_stringToByte (l_menutabContainerOid_s, l_menutabContainerOid);
        -- delete all rights on menutab container:
        p_Rights$deleteObjectRights (l_menutabContainerOid);
        -- set rights for user Administrator on menutab container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'view', 'read', 'change', 'delete',
                    'viewRights', 'setRights', 'createLink', 'distribute',
                    'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_menutabContainerOid, l_admin, l_rights, 0);

        --**********************************************************************
        --** fills the table ibs_MenuTab_01 with tuples which are necessary to show **
        --** the tabs at the upper left side of the application               **
        --**********************************************************************
        p_ObjectDesc_01$get (c_languageId, 'OD_domPublic', l_name, l_desc);

        SELECT  actVersion INTO l_tVersionId
        FROM    ibs_Type WHERE code = 'MenuTab';

        l_retValue := p_MenuTab_01$create (l_admin, ai_op, l_tVersionId,
            l_name, l_menutabContainerOid_s, 1, 0, c_NOOID_s, l_desc,
            l_menutabOid_s);
        p_stringToByte (l_menutabOid_s, l_menutabOid);
        -- update:
        UPDATE ibs_MenuTab_01
        SET      objectOid = l_public,
                 description = l_name,
                 priorityKey = 1,
                 isPrivate = 0,
                 domainId = l_domainId,
                 classFront = 'groupFront',
                 classBack = 'groupBack',
                 fileName = 'group.htm'
        WHERE oid = l_menutabOid;

        -- get the name of the tab privat, multilinguality
        -- fill the table ibs_MenuTab_01 with tuples for private tab
        p_ObjectDesc_01$get (c_languageId, 'OD_wspPrivate', l_name, l_desc);
        l_retValue := p_MenuTab_01$create (l_admin, ai_op, l_tVersionId,
            l_name, l_menutabContainerOid_s, 1, 0, c_NOOID_s, l_desc,
            l_menutabOid_s);
        p_stringToByte (l_menutabOid_s, l_menutabOid);

        -- update:
        UPDATE ibs_MenuTab_01
        SET      objectOid = l_workspaces,
                 description = l_name,
                 priorityKey = 10,
                 isPrivate = 1,
                 domainId = l_domainId,
                 classFront = 'privateFront',
                 classBack = 'privateBack',
                 fileName = 'private.htm'
        WHERE oid = l_menutabOid;

        --*****************************************************************************
        --** create systemcontainer for queries                                      **
        --*****************************************************************************
        -- create system user used for customizing tasks ...
        l_retValue := p_User_01$createFast (l_admin, l_domainId, l_systemUserName,
        	'isEnc_K3QjSHNhbyB0cG5MLmFpWQ%3D%3D', l_systemUserName,
        	l_systemUserOid);

        -- get id of system user
        SELECT id
        INTO   l_systemUser
        FROM   ibs_User
        WHERE  oid = l_systemUserOid;

        -- create container for systemqueries
        l_retValue := p_Object$performCreate (l_systemUser, 0,
            16875329,                 -- tVersionId = 0x01017F41 = querycreatorcontainer,
            'Systemqueries', l_public_s, 1, 0, c_NOOID_s,
            'This container contains all querycreators used in this domain, it can be only seen by the system user.',
            l_queryContainer_s , l_queryContainer);

        -- set rights of system user on query container:
        -- delete all rights on search container:
        p_Rights$deleteObjectRights (l_queryContainer);

        -- set rights for system user on search container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'view', 'read', 'change', 'delete',
                    'viewRights', 'setRights', 'createLink', 'distribute',
                    'addElem', 'delElem', 'viewElems', 'viewProtocol');

        p_Rights$setRights (l_queryContainer, l_systemUser, l_rights, 0);

        -- create container for querytemplates seen by public users
        l_retValue := p_Object$performCreate (l_systemUser, 1,
            16875329,                 -- tVersionId = 0x01017F41 = querycreatorcontainer,
            'Publicqueries', l_queryContainer_s, 1, 0, c_NOOID_s,
            'This container contains all querycreators for public usage.',
            l_queryPublicContainer_s, l_queryPublicContainer);

        -- delete all rights on search container:
        p_Rights$deleteObjectRights (l_queryPublicContainer);

        -- set all rights for system user:
        p_Rights$setRights (l_queryPublicContainer, l_systemUser, l_rights, 0);

        -- set rights of group with all users on public container:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'viewElems');

        p_Rights$setRights (l_queryPublicContainer, l_allUserGroup, l_rights, 0);

        -- create all standard querytemplates which should be seen by every user
        p_createBaseQueryCreators (l_systemUser, l_queryPublicContainer_s);

        --*********************************************************************
        --** create systemcontainer workspacetemplates                       **
        --*********************************************************************
        BEGIN
            SELECT  actVersion
            INTO    l_tvWorkspaceTempCont
            FROM    ibs_Type
            WHERE   code = 'WorkspaceTemplateContainer';
        EXCEPTION
            WHEN OTHERS THEN
                RAISE;                  -- call common exception handling
        END;

        p_ObjectDesc_01$get (c_languageId, 'OD_domWorkspaceTemplate', l_name, l_desc);
        l_retValue := p_Object$performCreate (l_admin, ai_op, l_tvWorkspaceTempCont,
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc,
            l_workspTemplContainerOid_s, l_workspTemplContainerOid);

        -- delete all rights on workspacetemplate container:
        p_Rights$deleteObjectRights (l_workspTemplContainerOid);

    END IF; -- if object created successfully
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Domain_01$create',
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            ', ai_tVersionId = ' || ai_tVersionId  ||
            ', ai_name = ' || ai_name  ||
            ', ai_containerId_s = ' || ai_containerId_s  ||
            ', ai_containerKind = ' || ai_containerKind  ||
            ', ai_isLink = ' || ai_isLink  ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s  ||
            ', ai_description = ' || ai_description  ||
            ', errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );
        RETURN c_NOT_OK;
END p_Domain_01$create;
/

show errors;

exit;
