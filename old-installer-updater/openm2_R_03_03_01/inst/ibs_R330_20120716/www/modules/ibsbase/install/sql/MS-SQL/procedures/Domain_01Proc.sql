/******************************************************************************
 * All stored procedures regarding the domain table. <BR>
 *
 * @version     $Id: Domain_01Proc.sql,v 1.42 2012/02/13 14:04:39 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980725
 ******************************************************************************
 */

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
 * @param   ai_sslRequired      the flag if SSL must be used or not
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Domain_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Domain_01$create
(
    -- common input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_tVersionId          TVERSIONID,
    @ai_name                NAME,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_containerKind       INT,
    @ai_isLink              BOOL,
    @ai_linkedObjectId_s    OBJECTIDSTRING,
    @ai_description         DESCRIPTION,
    @ai_sslRequired         BOOL,
    -- common output parameters:
    @ao_oid_s               OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_oid                  OBJECTID,       -- the actual oid
    @l_name                 NAME,           -- the actual name
    @l_desc                 DESCRIPTION,    -- the actual description
    @l_domainId             DOMAINID,
    @l_tVersionId           INT,
    @l_admin                USERID,
    @l_admin2               USERID,
    @l_systemUser         	USERID,
    @l_systemUserOid      	OBJECTID,
    @l_systemUserName		NAME,
    @l_rights               RIGHTS,
    @l_allRights            RIGHTS,
    @l_public               OBJECTID,
    @l_public_s             OBJECTIDSTRING,
    @l_workspaces           OBJECTID,
    @l_workspaces_s         OBJECTIDSTRING,
    @l_userMmtOid           OBJECTID,
    @l_userMmtOid_s         OBJECTIDSTRING,
    @l_groupContainer       OBJECTID,
    @l_groupContainer_s     OBJECTIDSTRING,
    @l_userContainer        OBJECTID,
    @l_userContainer_s      OBJECTIDSTRING,
    @l_allUserGroup         GROUPID,
    @l_allUserGroupOid      OBJECTID,
    @l_allUserGroupOid_s    OBJECTIDSTRING,
    @l_adminGroup           GROUPID,
    @l_adminGroupOid        OBJECTID,
    @l_adminGroupOid_s      OBJECTIDSTRING,
    @l_userAdminGroup       GROUPID,
    @l_userAdminGroupOid    OBJECTID,
    @l_userAdminGroupOid_s  OBJECTIDSTRING,
    @l_structAdminGroup     GROUPID,
    @l_structAdminGroupOid  OBJECTID,
    @l_structAdminGroupOid_s OBJECTIDSTRING,
    @c_TVLocaleContainer    INT,            -- tVersionId of locale container
    @c_TVLocale             INT,            -- tVersionId of locale
    @l_localeContainerOid   OBJECTID,
    @l_localeContainerOid_s OBJECTIDSTRING,
    @l_localeOid            OBJECTID,
    @l_localeOid_s          OBJECTIDSTRING,
    @l_layoutContainerOid   OBJECTID,
    @l_layoutContainerOid_s OBJECTIDSTRING,
    @l_layoutOid            OBJECTID,
    @l_layoutOid_s          OBJECTIDSTRING,
    @l_menutabContainerOid  OBJECTID,
    @l_menutabContainerOid_s    OBJECTIDSTRING,
    @l_menutabOid           OBJECTID,
    @l_menutabOid_s         OBJECTIDSTRING,
    @l_queryContainer       OBJECTID,
    @l_queryContainer_s     OBJECTIDSTRING,
    @l_queryPublicContainer OBJECTID,
    @l_queryPublicContainer_s   OBJECTIDSTRING,
    @l_adminOid             OBJECTID,
    @l_adminOid_s           OBJECTIDSTRING,
    @l_workspTemplContainerOid   OBJECTID,
    @l_workspTemplContainerOid_s OBJECTIDSTRING,
    @l_tvWorkspaceTempCont  INTEGER,
    @l_rightsAll            INTEGER,
    @l_localOp              INTEGER     -- operation for local operations

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOOID_s              = '0x0000000000000000',
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_ALREADY_EXISTS       = 21,
    @c_languageId           = 0             -- default language

    -- initialize local variables:
SELECT
    @l_oid                  = @c_NOOID,
    @l_retValue             = @c_NOT_OK,
    @l_localOp              = 0,
    @l_systemUserName		= N'SysAdmin'

-- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId,
                            @ai_name, @ai_containerId_s, @ai_containerKind,
                            @ai_isLink, @ai_linkedObjectId_s, @ai_description,
                            @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- object created successfully?
        BEGIN
            -- create object specific data:
            -- get all rights:
            SELECT  @l_allRights = SUM (id)
            FROM    ibs_Operation

            -- create object type specific data:
            -- set default values:
            INSERT INTO ibs_Domain_01
                    (oid, sslRequired)
            VALUES  (@l_oid, @ai_sslRequired)

            -- get domain id:
            SELECT  @l_domainId = id
            FROM    ibs_Domain_01
            WHERE   oid = @l_oid

            -- create administrator of domain:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domAdmin', @l_name OUTPUT, @l_desc OUTPUT
            EXEC p_User_01$new @l_domainId, 0, @l_name, N'isEnc_K3QjSG5haSBtcGRMLmFBWQ%3D%3D',
                @l_name, null, null, @l_admin OUTPUT
            -- set administrator flag:
            UPDATE  ibs_User
            SET     admin = 1
            WHERE   id = @l_admin

            -- set rights of actual user on domain:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read', N'change', N'delete', N'viewRights',
                        N'viewProtocol')
            EXEC @l_retValue = p_Rights$setRights @l_oid, @ai_userId, @l_rights
            -- set rights of admin on domain:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'new', N'view', N'read', N'addElem', N'delElem', N'viewElems')
            EXEC @l_retValue = p_Rights$setRights @l_oid, @l_admin, @l_rights

            -- create public container of domain:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domPublic', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Object$performCreate @l_admin, @l_localOp, 0x01010021,
                @l_name, @ao_oid_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_public_s OUTPUT, @l_public OUTPUT

            -- create key mapper for public container:
            EXEC p_KeyMapper$new @l_public_s, N'menuPublic', N'ibs_instobj'

            -- delete all rights on public container:
            EXEC p_Rights$deleteObjectRights @l_public
            -- set rights of admin on public container:
            EXEC p_Rights$setRights @l_public, @l_admin, @l_allRights

            -- create user management:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domUserMmt', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_UserAdminContainer_01$create @l_admin, @l_localOp, 0x01013601,
                @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_userMmtOid_s OUTPUT
            EXEC p_stringToByte @l_userMmtOid_s, @l_userMmtOid OUTPUT

            -- create key mapper for user management:
            EXEC p_KeyMapper$new @l_userMmtOid_s, N'userAdmin', N'ibs_instobj'

            -- get group container:
            SELECT  @l_groupContainer = oid
            FROM    ibs_Object
            WHERE   containerId = @l_userMmtOid
                AND tVersionId = 0x01013401
            EXEC p_byteToString @l_groupContainer, @l_groupContainer_s OUTPUT

            -- create key mapper for group container:
            EXEC p_KeyMapper$new @l_groupContainer_s, N'groups', N'ibs_instobj'

            -- set groups oid before creating first group
            UPDATE  ibs_Domain_01
            SET     groupsOid = @l_groupContainer
            WHERE   oid = @l_oid

            -- get user container:
            SELECT  @l_userContainer = oid
            FROM    ibs_Object
            WHERE   containerId = @l_userMmtOid
                AND tVersionId = 0x01013301
            EXEC p_byteToString @l_userContainer, @l_userContainer_s OUTPUT

            -- create key mapper for user container:
            EXEC p_KeyMapper$new @l_userContainer_s, N'users', N'ibs_instobj'

            -- create group for all users:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domGroupAll', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Group_01$create @l_admin, @l_localOp, 0x010100b1,
                @l_name, @l_groupContainer_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_allUserGroupOid_s OUTPUT
            EXEC p_stringToByte @l_allUserGroupOid_s, @l_allUserGroupOid OUTPUT
            -- get id of group with all users:
            SELECT  @l_allUserGroup = id
            FROM    ibs_Group
            WHERE   oid = @l_allUserGroupOid

            -- set rights of group with all users on public container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read', N'createLink', N'distribute', N'viewElems')
            EXEC p_Rights$setRights @l_public, @l_allUserGroup, @l_rights, 0

            -- create domain administrator group:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domGroupAdministrators', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Group_01$create @l_admin, @l_localOp, 0x010100b1,
                @l_name, @l_groupContainer_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_adminGroupOid_s OUTPUT
            EXEC p_stringToByte @l_adminGroupOid_s, @l_adminGroupOid OUTPUT
            -- get id of domain administrator group:
            SELECT  @l_adminGroup = id
            FROM    ibs_Group
            WHERE   oid = @l_adminGroupOid

            -- set rights of administrator group on public container:
            EXEC p_Rights$setRights @l_public, @l_adminGroup, @l_allRights, 0
            -- set rights of administrator group on user management container:

            SELECT  @l_rights = 0

            EXEC p_Rights$setRights @l_userMmtOid, @l_adminGroup, @l_rights, 1
            -- set rights of administrator group on domain:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read', N'viewElems')
            EXEC p_Rights$setRights @l_oid, @l_adminGroup, @l_rights, 0

            -- add domain administrator group to the group of all users:
            EXEC p_Group_01$addGroup @l_admin, @l_allUserGroupOid, @l_adminGroupOid

            -- create user administrator group:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domGroupUserGroupAdmins', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Group_01$create @l_admin, @l_localOp, 0x010100b1,
                @l_name, @l_groupContainer_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_userAdminGroupOid_s OUTPUT
            EXEC p_stringToByte @l_userAdminGroupOid_s, @l_userAdminGroupOid OUTPUT
            -- get id of user administrator group:
            SELECT  @l_userAdminGroup = id
            FROM    ibs_Group
            WHERE   oid = @l_userAdminGroupOid

            -- set rights of user administrator group on user admin container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read', N'createLink', N'viewElems')
            EXEC p_Rights$setRights @l_userMmtOid, @l_userAdminGroup, @l_rights, 0

            -- set rights of user administrator group on subsequent objects of
            -- user and group container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'new', N'view', N'read', N'change', N'delete', N'login',
                        N'viewRights', N'setRights', N'createLink', N'distribute',
                        N'addElem', N'delElem', N'viewElems', N'viewProtocol')
            EXEC p_Rights$setRights @l_userContainer, @l_userAdminGroup, @l_rights, 1
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'new', N'view', N'read', N'change', N'delete', -- no login
                        N'viewRights', N'setRights', N'createLink', N'distribute',
                        N'addElem', N'delElem', N'viewElems', N'viewProtocol')
            EXEC p_Rights$setRights @l_groupContainer, @l_userAdminGroup, @l_rights, 1

/*
            -- set rights of user administrator group on user and group
            -- container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN ('new', 'view', 'read', 'createLink', 'addElem',
                        'delElem', 'viewElems')
            EXEC p_Rights$setRights @l_userContainer, @l_userAdminGroup, @l_rights, 0
            EXEC p_Rights$setRights @l_groupContainer, @l_userAdminGroup, @l_rights, 0
*/

            -- add group of user administrators to the domain administrator group:
            EXEC p_Group_01$addGroup @l_admin, @l_adminGroupOid, @l_userAdminGroupOid
            -- add user administrator group to the group of all users:
            EXEC p_Group_01$addGroup @l_admin, @l_allUserGroupOid, @l_userAdminGroupOid

            -- create group of common structure administrators:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domGroupStructAdmins', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Group_01$create @l_admin, @l_localOp, 0x010100b1,
                @l_name, @l_groupContainer_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_structAdminGroupOid_s OUTPUT
            EXEC p_stringToByte @l_structAdminGroupOid_s, @l_structAdminGroupOid OUTPUT
            -- get id of user administrator group:
            SELECT  @l_structAdminGroup = id
            FROM    ibs_Group
            WHERE   oid = @l_structAdminGroupOid

            -- set rights of structure administrator group on public container
            -- and all subsequent objects:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'new', N'view', N'read', N'change', N'delete',
                        N'viewRights', N'setRights', N'createLink', N'distribute',
                        N'addElem', N'delElem', N'viewElems', N'viewProtocol')
            EXEC p_Rights$setRights @l_public, @l_structAdminGroup, @l_rights, 1

            -- set rights of structure administrator group on
            -- user management container:
            SELECT  @l_rights = 0
            EXEC p_Rights$setRights @l_userMmtOid, @l_structAdminGroup, @l_rights, 1

            -- add group of structure administrators to the domain administrator group:
            EXEC p_Group_01$addGroup @l_admin, @l_adminGroupOid, @l_structAdminGroupOid
            -- add structure administrator group to the group of all users:
            EXEC p_Group_01$addGroup @l_admin, @l_allUserGroupOid, @l_structAdminGroupOid

            -- create container for the user workspaces:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domWorkspaces', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Object$performCreate @l_admin, 0x00000001, 0x01010021,
                @l_name, @ao_oid_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_workspaces_s OUTPUT, @l_workspaces OUTPUT

            -- create key mapper for user workspace container:
            EXEC p_KeyMapper$new @l_workspaces_s, N'userWorkspaces', N'ibs_instobj'

            -- delete all rights on workspaces container:
            EXEC p_Rights$deleteObjectRights @l_workspaces

            -- set rights of administrators on workspaces container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'new', N'view', N'read', N'change', N'delete',
                        N'viewRights', N'setRights', N'createLink', N'distribute',
                        N'addElem', N'delElem', N'viewElems', N'viewProtocol')
            EXEC p_Rights$setRights @l_workspaces, @l_adminGroup, @l_rights
            EXEC p_Rights$setRights @l_workspaces, @l_admin, @l_rights

            -- store data in the domain tuple:
            UPDATE  ibs_Domain_01
            SET     adminGroupId = @l_adminGroup,
                    adminId = @l_admin,
                    allGroupId = @l_allUserGroup,
                    userAdminGroupId = @l_userAdminGroup,
                    structAdminGroupId = @l_structAdminGroup,
                    groupsOid = @l_groupContainer,
                    usersOid = @l_userContainer,
                    publicOid = @l_public,
                    workspacesOid = @l_workspaces
            WHERE   oid = @l_oid

            --*****************************************************************************
            --** Create Locale Management for domain                                     **
            --*****************************************************************************

            -- create locale container:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLocales', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Object$create @l_admin, @l_localOp, 0x10101b1,
                @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_localeContainerOid_s OUTPUT

            -- create key mapper for locale container:
            EXEC p_KeyMapper$new @l_localeContainerOid_s, N'locales', N'ibs_instobj'
            
            -- set rights on locale container:
            EXEC p_stringToByte @l_localeContainerOid_s, @l_localeContainerOid OUTPUT
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read', N'viewRights',
                    N'new', N'addElem', N'delElem', N'viewElems', N'viewProtocol')
            EXEC p_Rights$setRights @l_localeContainerOid, @l_structAdminGroup, @l_rights, 1
            EXEC p_Rights$setRights @l_localeContainerOid, @l_allUserGroup, 0, 1

            -- create locale 'en_US' for the domain within the locale
            -- container:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLocaleEnUS', @l_name OUTPUT, @l_desc OUTPUT

            EXEC @l_retValue = p_Locale_01$create @l_admin, @l_localOp, 0x10101c1,
                @l_name, @l_localeContainerOid_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_localeOid_s OUTPUT
            
            EXEC p_stringToByte @l_localeOid_s, @l_localeOid OUTPUT
            
            -- create key mapper for locale:
            EXEC p_KeyMapper$new @l_localeOid_s, N'locale_en_US', N'ibs_instobj'

            -- set rights on locale:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            EXEC p_Rights$setRights @l_localeOid, @l_structAdminGroup, @l_rights, 1
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read')
            EXEC p_Rights$setRights @l_localeOid, @l_allUserGroup, @l_rights, 1

            -- set locale as default locale:
            EXEC @l_retValue = p_Locale_01$change @l_localeOid_s, @l_admin, 0,
                @l_name, N'#CONFVAR.ibsbase.validUntil#', @l_desc, 1, N'en', N'US', 1

/*
            -- create locale 'de_AT' for the domain within the locale
            -- container:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLocaleDeAT', @l_name OUTPUT, @l_desc OUTPUT

            EXEC @l_retVal = p_Locale_01$create @l_admin, @l_localOp, @c_TVLocale,
                @l_name, @l_localeContainerOid_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_localeOid_s OUTPUT
            
            EXEC p_stringToByte @l_localeOid_s, @l_localeOid OUTPUT
            
            -- create key mapper for locale:
            EXEC p_KeyMapper$new @l_localeOid_s, locale_en_US', N'ibs_instobj'

            -- set rights on locale:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            EXEC p_Rights$setRights @l_localeOid, @l_structAdminGroup, @l_rights, 1
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read')
            EXEC p_Rights$setRights @l_localeOid, @l_allUserGroup, @l_rights, 1
*/

            --*****************************************************************************
            --** Create Locale Management for domain - END                               **
            --*****************************************************************************

            --*****************************************************************************
            --** Create Layout Management for domain                                     **
            --*****************************************************************************

            -- create layout container:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLayouts', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Object$create @l_admin, @l_localOp, 0x01016F01,
                @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_layoutContainerOid_s OUTPUT

            -- create key mapper for layout container:
            EXEC p_KeyMapper$new @l_layoutContainerOid_s, N'layouts', N'ibs_instobj'

            -- set rights on layout container:
            EXEC p_stringToByte @l_layoutContainerOid_s, @l_layoutContainerOid OUTPUT
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read', N'viewRights',
                    N'new', N'addElem', N'delElem', N'viewElems', N'viewProtocol')
            EXEC p_Rights$setRights @l_layoutContainerOid, @l_structAdminGroup, @l_rights, 1
            EXEC p_Rights$setRights @l_layoutContainerOid, @l_allUserGroup, 0, 1

            -- create layout 'Standard' for the domain within the layout
            -- container:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLayoutStandard', @l_name OUTPUT, @l_desc OUTPUT

            -- do not use name of standardlayout out of multilingualtables, to avoid problems with names
            EXEC @l_retValue = p_Layout_01$create @l_admin, @l_localOp, 0x01017001,
                N'Standard', @l_layoutContainerOid_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_layoutOid_s OUTPUT
            EXEC p_stringToByte @l_layoutOid_s, @l_layoutOid OUTPUT

            -- set rights on layout:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            EXEC p_Rights$setRights @l_layoutOid, @l_structAdminGroup, @l_rights, 1
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read')
            EXEC p_Rights$setRights @l_layoutOid, @l_allUserGroup, @l_rights, 1

            -- set layout as default layout:
            EXEC @l_retValue = p_Layout_01$change @l_layoutOid_s, @l_admin, 0,
                N'Standard', N'#CONFVAR.ibsbase.validUntil#', @l_desc, 1, 1

            --*****************************************************************************
            --** Create Layout Management for domain - END                               **
            --*****************************************************************************

            -- create the business object for the administrator of the domain:
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domAdmin', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_User_01$performCreate @l_admin, @l_localOp, 0x010100a1,
                @l_name, @l_userContainer_s, 1, 0, @c_NOOID_s, @l_desc, @l_admin,
                @l_adminOid_s OUTPUT
            EXEC p_stringToByte @l_adminOid_s, @l_adminOid OUTPUT
            -- add domain administrator to the administrator group:
            EXEC p_Group_01$addUser @l_admin, @l_adminGroupOid, @l_adminOid

            --*****************************************************************************
            --** create systemcontainer for menutabs                                     **
            --*****************************************************************************

            -- create menutab container:
            EXEC p_ObjectDesc_01$get    @c_languageId, N'OD_domMenuTabs',
                                        @l_name OUTPUT, @l_desc OUTPUT

            SELECT  @l_tVersionId = actVersion
            FROM    ibs_Type
            WHERE   code = N'MenuTabContainer'

            EXEC @l_retValue = p_Object$create @l_admin, @l_localOp, @l_tVersionId,
                @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_menutabContainerOid_s OUTPUT

            -- create key mapper for menutab container:
            EXEC p_KeyMapper$new @l_menutabContainerOid_s, N'menutabs', N'ibs_instobj'

            -- set rights on menutab container:
            EXEC p_stringToByte @l_menutabContainerOid_s,
                                @l_menutabContainerOid OUTPUT

            -- delete all rights on menutab container:
            EXEC p_Rights$deleteObjectRights @l_menutabContainerOid

            -- set rights for user Administrator on menutab container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'new', N'view', N'read', N'change', N'delete',
                        N'viewRights', N'setRights', N'createLink', N'distribute',
                        N'addElem', N'delElem', N'viewElems', N'viewProtocol')

            EXEC p_Rights$setRights @l_menutabContainerOid, @l_admin, @l_rights, 0


            --**********************************************************************
            --** fills the table ibs_MenuTab_01 with tuples which are necessary   **
            --** to show the tabs at the upper left side of the application       **
            --**********************************************************************
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domPublic',
                                @l_name OUTPUT, @l_desc OUTPUT

            SELECT  @l_tVersionId = actVersion
            FROM    ibs_Type
            WHERE   code = N'MenuTab'

            EXEC @l_retValue = p_MenuTab_01$create @l_admin, @l_localOp, @l_tVersionId,
                    @l_name,     @l_menutabContainerOid_s, 1, 0, @c_NOOID_s, @l_desc,
                    @l_menutabOid_s OUTPUT

            EXEC p_stringToByte @l_menutabOid_s, @l_menutabOid OUTPUT

            -- create key mapper for public menutab:
            EXEC p_KeyMapper$new @l_menutabOid_s, N'menutabPublic', N'ibs_instobj'

            -- update:
            UPDATE  ibs_MenuTab_01
            SET     objectOid = @l_public,
                    description = @l_name,
                    priorityKey = 1,
                    isPrivate = 0,
                    domainId = @l_domainId,
                    classFront = N'groupFront',
                    classBack = N'groupBack',
                    fileName = N'group.htm'
            WHERE oid = @l_menutabOid


            -- get the name of the tab privat, multilinguality
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_wspPrivate',
                    @l_name OUTPUT, @l_desc OUTPUT

            -- fill the table ibs_MenuTab_01 with tuples for private tab
            EXEC @l_retValue = p_MenuTab_01$create @l_admin, @l_localOp, @l_tVersionId,
                @l_name, @l_menutabContainerOid_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_menutabOid_s OUTPUT

            EXEC p_stringToByte @l_menutabOid_s, @l_menutabOid OUTPUT

            -- update:
            UPDATE ibs_MenuTab_01
            SET      objectOid = @l_workspaces,
                     description = @l_name,
                     priorityKey = 10,
                     isPrivate = 1,
                     domainId = @l_domainId,
                     classFront = N'privateFront',
                     classBack = N'privateBack',
                     fileName = N'private.htm'
            WHERE oid = @l_menutabOid

            --*****************************************************************************
            --** create systemcontainer for queries                                      **
            --*****************************************************************************
            -- create system user used for customizing tasks ...
            EXEC p_User_01$createFast @l_admin, @l_domainId, @l_systemUserName,
                N'isEnc_K3QjSHNhbyB0cG5MLmFpWQ%3D%3D', @l_systemUserName, @l_systemUserOid OUTPUT

            -- get id of system user
            SELECT @l_systemUser = id
            FROM   ibs_User
            WHERE  oid = @l_systemUserOid

            -- create container for systemqueries
            EXEC @l_retValue = p_Object$performCreate @l_systemUser, 0x00000000,
                16875329,                 -- tVersionId = 0x01017F41 = querycreatorcontainer,
                N'Systemqueries', @l_public_s, 1, 0, @c_NOOID_s,
                N'This container contains all querycreators used in this domain, it can be only seen by the system user.',
                @l_queryContainer_s OUTPUT, @l_queryContainer OUTPUT

            -- set rights of system user on query container
            -- delete all rights on search container:
            EXEC p_Rights$deleteObjectRights @l_queryContainer

            -- set rights for system user on search container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'new', N'view', N'read', N'change', N'delete',
                        N'viewRights', N'setRights', N'createLink', N'distribute',
                        N'addElem', N'delElem', N'viewElems', N'viewProtocol')

            EXEC p_Rights$setRights @l_queryContainer, @l_systemUser, @l_rights, 0

            -- create container for querytemplates seen by public users
            EXEC @l_retValue = p_Object$performCreate @l_systemUser, 0x00000001,
                16875329,                 -- tVersionId = 0x01017F41 = querycreatorcontainer,
                N'Publicqueries', @l_queryContainer_s, 1, 0, @c_NOOID_s,
                N'This container contains all querycreators for public usage.',
                @l_queryPublicContainer_s OUTPUT, @l_queryPublicContainer OUTPUT

            -- delete all rights on search container:
            EXEC p_Rights$deleteObjectRights @l_queryPublicContainer

            -- set all rights for system user
            EXEC p_Rights$setRights @l_queryPublicContainer, @l_systemUser, @l_rights, 0

            -- set rights of group with all users on public container:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'viewElems')
            EXEC p_Rights$setRights @l_queryPublicContainer, @l_allUserGroup, @l_rights, 0

            -- create all standard querytemplates which should be seen by every user
            EXEC p_createBaseQueryCreators @l_systemUser, @l_queryPublicContainer_s

            --*****************************************************************************
            --** create systemcontainer workspacetemplates                               **
            --*****************************************************************************
            SELECT @l_tvWorkspaceTempCont = actVersion
            FROM   ibs_Type
            WHERE  code = N'WorkspaceTemplateContainer'

            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domWorkspaceTemplate', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Object$performCreate @l_admin, @l_localOp, @l_tvWorkspaceTempCont,
                @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_workspTemplContainerOid_s OUTPUT, @l_workspTemplContainerOid OUTPUT
            -- delete rights on workspacetemplate container
            EXEC p_Rights$deleteObjectRights @l_workspTemplContainerOid

        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Domain_01$create


/******************************************************************************
 * Set the scheme of a domain. <BR>
 * This procedure also performs some operations which are corresponding to
 * the selected scheme, i.e. creating a catalog management.
 *
 * @input parameters:
 * @param   ai_userId           Id of the user who is setting the scheme.
 * @param   ai_id               Id of the domain.
 * @param   ai_schemeId         Id of the domain scheme.
 * @param   ai_homepagePath     Homepage path of the domain, i.e. the path
 *                              where it resides, e.g. '/m2/'.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Domain_01$setScheme'
GO

-- create the new procedure:
CREATE PROCEDURE p_Domain_01$setScheme
(
    -- common input parameters:
    @ai_userId              USERID,
    @ai_id                  DOMAINID,
    @ai_schemeId            ID,
    @ai_homepagePath        NAME
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_oid_s                OBJECTIDSTRING, -- the actual oid as string
    @l_cid                  OBJECTID,       -- the actual containerId
    @l_cid_s                OBJECTIDSTRING, -- the actual containerId as string
    @l_retValue             INT,            -- return value of a function
    @l_name                 NAME,           -- the actual name
    @l_desc                 DESCRIPTION,    -- the actual description
    @l_public               OBJECTID,
    @l_public_s             OBJECTIDSTRING,
    @l_allGroupId           GROUPID,
    @l_admin                USERID,
    @l_adminGroup           GROUPID,
    @l_userAdminGroup       GROUPID,
    @l_structAdminGroup     GROUPID,
    @l_localOp              INTEGER     -- operation for local operations

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOOID_s              = '0x0000000000000000',
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_ALREADY_EXISTS       = 21,
    @c_languageId           = 0             -- default language

    -- initialize local variables:
SELECT
    @l_oid                  = @c_NOOID,
    @l_retValue             = @c_NOT_OK,
    @l_localOP              = 0

-- body:
    -- set the domain scheme:
    UPDATE  ibs_Domain_01
    SET     scheme = s.id,
            workspaceProc = s.workspaceProc,
            homepagePath = @ai_homepagePath
    FROM    ibs_Domain_01 d, ibs_DomainScheme_01 s
    WHERE   s.id = @ai_schemeId
        AND d.id = @ai_id

    -- get public container and group of all users:
    SELECT  @l_public = publicOid, @l_allGroupId = allGroupId,
            @l_admin = adminId, @l_adminGroup = adminGroupId,
            @l_userAdminGroup = userAdminGroupId,
            @l_structAdminGroup = structAdminGroupId
    FROM    ibs_Domain_01
    WHERE   id = @ai_id
    EXEC p_byteToString @l_public, @l_public_s OUTPUT

    -- check if there is a data interchange component to create for the domain:
    IF EXISTS (SELECT *
                FROM    ibs_DomainScheme_01
                WHERE   id = @ai_schemeId
                AND hasDataInterchange = 1)
                                        -- the scheme specifies a
                                        -- data interchange component?
    BEGIN
        -- create import/export management:
        -- Data Interchange:
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDataInterchange', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @l_admin, @l_localOp, 0x01017401, -- IntegratorContainer
                @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT

        EXEC p_stringToByte @l_cid_s, @l_cid OUTPUT

        -- create key mapper for data interchange container:
        EXEC p_KeyMapper$new @l_cid_s, N'di', N'ibs_instobj'

        -- set rights on import/export management:
        EXEC p_Rights$deleteObjectRights @l_cid
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_admin
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_adminGroup
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_userAdminGroup
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_structAdminGroup

        -- Import
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDIImport', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @l_admin, @l_localOp, 0x01017901, -- Import Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT

        -- create key mapper for import container:
        EXEC p_KeyMapper$new @l_oid_s, N'diimport', N'ibs_instobj'

        -- Export
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDIExport', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @l_admin, @l_localOp, 0x01017a01, -- Export Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT

        -- create key mapper for export container:
        EXEC p_KeyMapper$new @l_oid_s, N'diexport', N'ibs_instobj'
    END -- if the scheme has a data interchange component
GO
-- p_Domain_01$setScheme


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_id               ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_showInNews       Shall the currrent object be displayed in the
 *                              news?
 * @param   ai_domainScheme     The id of the domain scheme of the domain.
 *                              If this is different from the actual one the
 *                              procedure p_Domain_01$setScheme is called to
 *                              set the new scheme.
 * @param   ai_homepagePath     Contains the homepagepath of the domain.
 * @param   ai_sslRequired      The flag if SSL must be used or not.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Domain_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Domain_01$change
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    -- type-specific input parameters:
    @ai_domainScheme        ID,
    @ai_homepagePath        NAME,
    @ai_sslRequired         BOOL
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_retValue             INT,            -- return value of this function
    @l_oldDomainScheme      INT,            -- the old domain scheme
    @l_id                   INT,            -- the id of the domain
    @l_homepagePath         NAME,           -- local copy of ai_homepagePath
    @l_oldHomepagePath      NAME,           -- the old homepage path of the domain
    @l_adminId              USERID          -- id of domain administrator

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId,
                @ai_op, @ai_name, @ai_validUntil, @ai_description, @ai_showInNews,
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            IF (@ai_homepagePath IS null OR LTRIM (@ai_homepagePath) + 'x' = 'x')
                                        -- no homepagePath shall be set?
                SELECT  @l_homepagePath = null
            ELSE                        -- there is a homepagePath to be set
                SELECT  @l_homepagePath = @ai_homepagePath

            -- get the current domain scheme:
            SELECT  @l_oldDomainScheme = @ai_domainScheme
            SELECT  @l_id = id, @l_oldDomainScheme = scheme,
                    @l_oldHomepagePath = homepagePath, @l_adminId = adminId
            FROM    ibs_Domain_01
            WHERE   oid = @l_oid

            IF (@l_oldDomainScheme <> @ai_domainScheme) -- the scheme shall be changed?
            BEGIN
                -- update object type specific data:
                EXEC p_Domain_01$setScheme @l_adminId, @l_id, @ai_domainScheme, @l_homepagePath
            END -- if the scheme shall be changed

            -- set the new homepagePath:
            UPDATE  ibs_Domain_01
            SET     homepagePath = @l_homepagePath,
                    sslRequired = @ai_sslRequired
            WHERE   oid = @l_oid
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Domain_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @param   ai_oid_s            ID of the object to be retrieved.
 * @param   ai_userId           Id of the user who is getting the data.
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
 * @param   ao_ownerName        Name of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_creatorName      Name of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_changerName      Name of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       The showInNews flag.
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out
 * @param   ao_checkOutUser     Oid of the user which checked out the object
 * @param   ai_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName name of the user which checked out the object,
 *                              is only set if this user has the right to read
 *                              the checkOut-User
 * @param   ao_domainScheme     The id of the domain scheme.
 * @param   ao_domainSchemeName The name of the domain scheme.
 * @param   ao_homepagePath     The homepagepaht of the domain.
 * @param   ao_sslRequired      The flag if SSL must be used or not.
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Domain_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Domain_01$retrieve
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- common output parameters:
    @ao_state               STATE           OUTPUT,
    @ao_tVersionId          TVERSIONID      OUTPUT,
    @ao_typeName            NAME            OUTPUT,
    @ao_name                NAME            OUTPUT,
    @ao_containerId         OBJECTID        OUTPUT,
    @ao_containerName       NAME            OUTPUT,
    @ao_containerKind       INT             OUTPUT,
    @ao_isLink              BOOL            OUTPUT,
    @ao_linkedObjectId      OBJECTID        OUTPUT,
    @ao_owner               USERID          OUTPUT,
    @ao_ownerName           NAME            OUTPUT,
    @ao_creationDate        DATETIME        OUTPUT,
    @ao_creator             USERID          OUTPUT,
    @ao_creatorName         NAME            OUTPUT,
    @ao_lastChanged         DATETIME        OUTPUT,
    @ao_changer             USERID          OUTPUT,
    @ao_changerName         NAME            OUTPUT,
    @ao_validUntil          DATETIME        OUTPUT,
    @ao_description         DESCRIPTION     OUTPUT,
    @ao_showInNews          BOOL            OUTPUT,
    @ao_checkedOut          BOOL            OUTPUT,
    @ao_checkOutDate        DATETIME        OUTPUT,
    @ao_checkOutUser        USERID          OUTPUT,
    @ao_checkOutUserOid     OBJECTID        OUTPUT,
    @ao_checkOutUserName    NAME            OUTPUT,
    -- type-specific output parameters:
    @ao_domainScheme        ID              OUTPUT,
    @ao_domainSchemeName    NAME            OUTPUT,
    @ao_homepagePath        NAME            OUTPUT,
    @ao_sslRequired         BOOL            OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @l_retValue = p_Object$performRetrieve
                @ai_oid_s, @ai_userId, @ai_op,
                @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT,
                @ao_name OUTPUT, @ao_containerId OUTPUT, @ao_containerName OUTPUT,
                @ao_containerKind OUTPUT, @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT,
                @ao_owner OUTPUT, @ao_ownerName OUTPUT,
                @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
                @ao_validUntil OUTPUT, @ao_description OUTPUT, @ao_showInNews OUTPUT,
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT,
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT, @ao_checkOutUserName OUTPUT,
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- retrieve object type specific data:
            SELECT  @ao_homepagePath = homepagePath,
                    @ao_sslRequired = d.sslRequired
            FROM    ibs_Domain_01 d
            WHERE   d.oid = @l_oid

            -- retrieve domain scheme data:
            SELECT  @ao_domainScheme = d.scheme, @ao_domainSchemeName = o.name
            FROM    ibs_Domain_01 d, ibs_DomainScheme_01 ds, ibs_Object o
            WHERE   d.oid = @l_oid
                AND d.scheme = ds.id
                AND o.oid = ds.oid

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @l_retValue = @c_NOT_OK -- set return value
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Domain_01$retrieve


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
-- delete existing procedure:
EXEC p_dropProc N'p_Domain_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Domain_01$delete
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_oid                  OBJECTID,   -- the actual oid
    @l_retValue             INT         -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId, @ai_op,
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  ibs_Domain_01
            WHERE   oid NOT IN
                    (SELECT oid
                    FROM    ibs_Object)

            -- check if deletion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @l_retValue = @c_NOT_OK -- set return value

            -- deletes all tuples in ibs_Menu wich are not in ibs_Domain_01
            -- excepted the tuple of the system root because it is in no domain
            DELETE  ibs_MenuTab_01
            WHERE   domainId NOT IN
                    (SELECT id
                    FROM    ibs_Domain_01
                    )
              AND   domainId > 0
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Domain_01$delete


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_newOid           The oid of the copy.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Domain_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Domain_01$BOCopy
(
    -- common input parameters:
    @ai_oid                 OBJECTID,
    @ai_userId              USERID,
    @ai_newOid              OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK

    -- body:
    -- make an insert for all type specific tables:
    INSERT  INTO ibs_Domain_01
            (oid, id, adminGroupId, adminId,
            allGroupId, userAdminGroupId, structAdminGroupId,
            groupsOid, usersOid, homepagePath, logo, scheme,
            workspaceProc, sslRequired)
    SELECT  @ai_newOid, 0, adminGroupId, adminId,
            allGroupId, userAdminGroupId, structAdminGroupId,
            groupsOid, usersOid, homepagePath, logo, scheme,
            workspaceProc, sslRequired
    FROM    ibs_Domain_01
    WHERE   oid = @ai_oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @l_retValue = @c_ALL_RIGHT -- set return value

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Domain_01$BOCopy
