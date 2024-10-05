/******************************************************************************
 * All base data within the framework. <BR>
 *
 * @version     $Id: createBaseData.sql,v 1.26 2011/10/18 14:40:51 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980601
 ******************************************************************************
 */


-- don't show count messages:
SET NOCOUNT ON
BEGIN TRANSACTION
GO

-- declare variables:
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything's all right
    @c_MAXValidUntil        DATETIME,
    @c_TVRoot               INT,            -- 0x01015301
    @c_TVUser               INT,            -- tVersionId of User
    @c_TVUserProfile        INT,            -- 0x01013801
    @c_TVReference          INT,            -- 0x01010031
    @c_TVDI                 INT,            -- tVersionId for Data Interchange
    @c_TVImportContainer    INT,            -- tVersionId of import container
    @c_TVExportContainer    INT,            -- tVersionId of export container
    @c_TVLayoutContainer    INT,            -- tVersionId of layout container
    @c_TVLayout             INT,            -- tVersionId of layout
    @c_TVDomainSchemeContainer INT,         -- tVersionId of
                                            -- domain scheme container
    @c_TVDomainScheme       INT,            -- tVersionId of domain scheme
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_rightsAdmin          RIGHTS,         -- rights of administrator
    @l_rightsProfile        RIGHTS,         -- rights on profile
    @l_rightsRootRef        RIGHTS,         -- rights on reference to root
    @l_retVal               INT,            -- return value of function
    @l_profile              OBJECTID,       -- oid of user profile
    @l_profile_s            OBJECTIDSTRING, -- oid of user profile as string
    @l_cid                  OBJECTID,       -- oid of actual container
    @l_cid_s                OBJECTIDSTRING, -- oid of act. container as string
    @l_oid                  OBJECTID,       -- oid of actual business object
    @l_oid_s                OBJECTIDSTRING, -- oid of actual BO as string
    @l_name                 NAME,           -- name of business object
    @l_desc                 DESCRIPTION,    -- description of business object
    @l_rights               RIGHTS,         -- the current rights
    @l_admin                USERID,         -- id of system administrator
    @l_adminOid             OBJECTID,       -- oid of system administrator
    @l_adminOid_s           OBJECTIDSTRING, -- string representation of oid
    @l_root                 OBJECTID,       -- oid of system root
    @l_root_s               OBJECTIDSTRING, -- oid of system root as string
    @l_rootRef              OBJECTID,       -- oid of root reference
    @l_rootRef_s            OBJECTIDSTRING, -- oid of root reference as string
    @l_posNoPath            POSNOPATH,      -- current hierarchy path
    @l_msg                  NVARCHAR (255),  -- current message
    @l_cnt                  INT,            -- counter
    @l_adminAlreadyExists   BOOL,
    @l_rootAlreadyExists    BOOL

-- assign constants:
SELECT
    @c_ALL_RIGHT = 1,
    @c_MAXValidUntil = '#CONFVAR.ibsbase.validUntilSql#',
    @c_TVRoot = 16864001,               -- 0x01015301
    @c_TVUser = 16842913,               -- 0x010100A1
    @c_TVUserProfile = 16857089,        -- 0x01013801
    @c_TVReference = 16842801,          -- 0x01010031
    @c_TVDI = 16872449,                 -- 0x01017401 (Integrationsverwaltung)
    @c_TVImportContainer = 16873729,    -- 0x1017901
    @c_TVExportContainer = 16873985,    -- 0x1017A01
    @c_TVLayoutContainer = 16871169,    -- 0x1016F01
    @c_TVLayout = 16871425,             -- 0x1017001
    @c_TVDomainSchemeContainer = 16843025, -- 0x01010111
    @c_TVDomainScheme = 16843041,       -- 0x01010121
    @c_NOOID = 0x0000000000000000,
    @c_NOOID_s = '0x0000000000000000',
    @c_languageId = 0

-- initialize local variables:
SELECT
    @l_adminAlreadyExists = 0,
    @l_rootAlreadyExists = 0

-- body:
--*****************************************************************************
--** Create system administrator                                             **
--*****************************************************************************

    -- create administrator:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysAdmin',
        @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal =
        p_User_01$new 0, 1, @l_name, N'isEnc_bnRpSG1hZCBBcA%3D%3D',
            N'System Administrator', null, null, @l_admin OUTPUT

    -- set admin flag:
    UPDATE  ibs_User
    SET     admin = 1
    WHERE   id = @l_admin

    -- store the id of the administrator:
    SELECT  @l_desc = CONVERT (NVARCHAR (255), @l_admin)
    EXEC p_System$new N'ID_sysAdmin', N'INTEGER', @l_desc

    -- get rights for administrator on root:
    SELECT  @l_rightsAdmin = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'new', N'view', N'read', N'viewElems', N'addElem', N'delElem')

    -- get rights for administrator on his/her own profile:
    SELECT  @l_rightsProfile = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'view', N'read', N'viewElems')

    -- get rights for administrator on reference to root:
    SELECT  @l_rightsRootRef = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'view', N'read', N'viewElems')

    -- check if there was an error:
    IF (@l_retVal = 1)                  -- no error during creation
        PRINT 'System Administrator created.'
    ELSE IF (@l_retVal = 21)            -- administrator already exists
        PRINT 'System Administrator already exists.'
    ELSE                                -- error during creation
    BEGIN
        SELECT  @l_msg = 'Error during creation of System Administrator: ' +
                CONVERT (VARCHAR (10), @l_retVal)
        PRINT @l_msg
    END -- else error during creation


--*****************************************************************************
--** Create root of system                                                   **
--*****************************************************************************

    -- procedure p_Object$create
    -- uid, op, tVersionId,
    -- name, containerId_s, containerKind, isLink, linkedObjectId_s,
    -- description, oid_s OUTPUT

    SELECT  @l_retVal = 21

    -- Root of the system:
    IF NOT EXISTS (
            SELECT  id
            FROM    ibs_Object
            WHERE   containerId = @c_NOOID)
                                        -- root does not exist yet?
    BEGIN
        -- create the root:
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysRoot',
            @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_admin, 0x00000000, 0x01015301,
                @l_name, @c_NOOID_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_root_s OUTPUT

        -- convert string representation of oid to byte:
        EXEC p_stringToByte @l_root_s, @l_root OUTPUT

        -- set valid until date of root and all yet created objects to nearly
        -- infinite:
        UPDATE  ibs_Object
        SET     validUntil = @c_MAXValidUntil
        WHERE   CHARINDEX (
                    (SELECT posNoPath
                    FROM    ibs_Object
                    WHERE   oid = @l_root
                    ), posNoPath
                ) = 1
    END -- if root does not exist yet
    ELSE                                -- root already exists
    BEGIN
        -- get the root oid:
        SELECT  @l_root = oid
        FROM    ibs_Object
        WHERE   containerId = @c_NOOID

        -- convert oid of root to string representation:
        EXEC p_byteToString @l_root, @l_root_s OUTPUT
    END -- else root already exists


    -- delete all existing rights on the root:
    EXEC p_Rights$deleteObjectRights @l_root

    -- set administrator rights on root:
    EXEC p_Rights$setRights @l_root, @l_admin, @l_rightsAdmin, 1

    -- create user profile of administrator:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysAdminUserprofile',
        @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal =
        p_UserProfile_01$create @l_admin, 0x00000000, @l_admin,
            @c_TVUserProfile,
            @l_name, @l_root_s, 1, 0, @c_NOOID_s, @l_desc,
            @l_profile_s OUTPUT
    EXEC    p_stringToByte @l_profile_s, @l_profile OUTPUT

    -- set rights of administrator on his/her own user profile:
    EXEC p_Rights$setRights @l_profile, @l_admin, @l_rightsProfile, 1


    -- store workspace of administrator:
    INSERT  INTO ibs_Workspace (userId, domainId, workspace, profile, publicWsp)
    VALUES  (@l_admin, 0, @l_root, @l_profile, @l_root)

    -- check if there was an error:
    IF (@l_retVal = 1)                  -- no error during creation
        PRINT 'System Root created.'
    ELSE IF (@l_retVal = 21)            -- root already exists
        PRINT 'System Root already exists.'
    ELSE                                -- error during creation
    BEGIN
        SELECT  @l_msg = 'Error during creation of System Root: ' +
                CONVERT (VARCHAR (10), @l_retVal) + '.'
        PRINT @l_msg
    END -- else error during creation


    -- create reference to root used for administrative purposes:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysRootRef',
        @l_name OUTPUT, @l_desc OUTPUT
    EXEC p_Object$create @l_admin, 0x00000000, 0x01010031,
                @l_name, @l_root_s, 1, 1, @l_root_s, @l_desc,
                @l_rootRef_s OUTPUT
    EXEC p_stringToByte @l_rootRef_s, @l_rootRef OUTPUT
    -- ensure that the reference is shown in the menu:
    UPDATE  ibs_Object
    SET     showInMenu = 1
    WHERE   oid = @l_rootRef

    -- set rights of administrator on reference to root:
    EXEC p_Rights$setRights @l_rootRef, @l_admin, @l_rightsRootRef, 1


--*****************************************************************************
--** Create base object for system administrator                             **
--*****************************************************************************

    -- create base object for system administrator:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysAdmin',
        @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$performCreate @l_admin, 1, @c_TVUser,
            @l_name, @l_root_s, 1, 0, @c_NOOID_s, @l_desc,
            @l_adminOid_s OUTPUT, @l_adminOid OUTPUT

    IF (@l_retVal = @c_ALL_RIGHT)       -- object successfully created?
    BEGIN
        -- set the new oid for the system administrator:
        UPDATE  ibs_User
        SET     oid = @l_adminOid
        WHERE   id = @l_admin
        SELECT  @l_msg = 'cBData: Creation of BusinessObject for system' +
                ' administrator finished.'
        PRINT @l_msg
    END -- if object successfully created
    ELSE                    -- error during object creation?
        PRINT 'cBData: BusinessObject could not be created.'


--*****************************************************************************
--** Create MenuTab for System Adminstrator                                  **
--*****************************************************************************

    -- create MenuTab for System Adminstrator because the System Administrator
    -- is in domain 0, which not realy exists
    -- sets the tab for the System Administrator
    DECLARE
        @l_tVersionid           INT,
        @l_retValue             INT,
        @l_menuTabOid_s         OBJECTIDSTRING,
        @l_menuTabOid           OBJECTID

    SELECT  @l_tVersionId = actVersion
    FROM    ibs_Type
    WHERE code = 'MenuTab'

    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysRoot',
        @l_name OUTPUT, @l_desc OUTPUT

    EXEC @l_retValue = p_MenuTab_01$create @l_admin, 1, @l_tVersionId,
        @l_name, @l_root_s, 1, 0, @c_NOOID_s, @l_desc,
        @l_menutabOid_s OUTPUT

    EXEC p_stringToByte @l_menutabOid_s, @l_menutabOid OUTPUT

    -- update:
    UPDATE ibs_MenuTab_01
    SET      objectOid = @l_root,
             description = @l_name,
             priorityKey = 1,
             isPrivate = 0,
             domainId = 0,
             classFront = 'systemFront',
             classBack = 'systemBack',
             fileName = 'system.htm'
    WHERE oid = @l_menutabOid


--*****************************************************************************
--** Create Data Interchange on system root                                  **
--*****************************************************************************

    -- create import/export management:
    -- Data Interchange:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysDataInterchange', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_admin, 1, @c_TVDI,
            @l_name, @l_root_s, 1, 0, @c_NOOID_s, @l_desc,
            @l_cid_s OUTPUT

    IF (@l_retVal = @c_ALL_RIGHT)       -- data interchange created correctly?
    BEGIN
        -- convert string to oid representation:
        EXEC p_stringToByte @l_cid_s, @l_cid OUTPUT
        -- set rights on import/export management:
        EXEC p_Rights$propagateUserRights @l_root, @l_cid, @l_admin

        -- Import
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysDIImport',
            @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_admin, 1, @c_TVImportContainer,
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc,
                @l_oid_s OUTPUT

        IF (@l_retVal = @c_ALL_RIGHT)   -- import container created?
        BEGIN
            -- Export
            EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysDIExport',
                @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retVal = p_Object$create @l_admin, 1, @c_TVExportContainer,
                    @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc,
                    @l_oid_s OUTPUT

            IF (@l_retVal = @c_ALL_RIGHT) -- export container created?
            BEGIN
                SELECT  @l_msg = 'cBData: Data Interchange on system level' +
                        ' correctly defined.'
                PRINT @l_msg
            END -- if export container created
            ELSE                        -- export container not created
            BEGIN
                SELECT  @l_msg = 'cBData: Error when creating export container:' +
                        ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
                PRINT @l_msg
            END -- else export container not created
        END -- if import container created
        ELSE                            -- import container not created
        BEGIN
            SELECT  @l_msg = 'cBData: Error when creating import container:' +
                    ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
            PRINT @l_msg
        END -- else import container not created
    END -- if data interchange created correctly
    ELSE
    BEGIN
        SELECT  @l_msg = 'cBData: Error when creating data interchange:' +
                ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
        PRINT @l_msg
    END -- if data interchange created correctly


--*****************************************************************************
--** Create Layout Management on system root                                 **
--*****************************************************************************

    -- create layout container:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysLayouts',
        @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_admin, 1, @c_TVLayoutContainer,
        @l_name, @l_root_s, 1, 0, @c_NOOID_s, @l_desc,
        @l_cid_s OUTPUT

    IF (@l_retVal = @c_ALL_RIGHT)       -- layout management created correctly?
    BEGIN
        -- set rights on layout container:
        EXEC p_stringToByte @l_cid_s, @l_cid OUTPUT

        -- set rights:
        SELECT  @l_rights = SUM (id)
        FROM    ibs_Operation
        WHERE   name IN (N'view', N'read', N'viewRights',
                    N'new', N'addElem', N'delElem', N'viewElems', N'viewProtocol')
        EXEC p_Rights$setRights @l_cid, @l_admin, @l_rights, 1

        -- create layout 'Standard' for the root within the layout container:
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysLayoutStandard',
            @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Layout_01$create @l_admin, 1, @c_TVLayout,
            N'Standard', @l_cid_s, 1, 0, @c_NOOID_s, @l_desc,
            @l_oid_s OUTPUT

        IF (@l_retVal = @c_ALL_RIGHT)   -- layout created?
        BEGIN
            EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT

            -- set rights on layout:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            EXEC p_Rights$setRights @l_oid, @l_admin, @l_rights, 1

            -- set layout for system administrator:
            UPDATE  ibs_UserProfile
            SET     layoutId = @l_oid
            WHERE   userId = @l_admin

            PRINT 'cBData: Layout Management on system level correctly defined.'
        END -- if layout created
        ELSE                            -- layout not created
        BEGIN
            SELECT  @l_msg = 'cBData: Error when creating Layout Standard:' +
                    ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
            PRINT @l_msg
        END -- else layout not created
    END -- if layout management created correctly
    ELSE                                -- layout management not created
                                        -- correctly
    BEGIN
        SELECT  @l_msg = 'cBData: Error when creating Layout Management:' +
                ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
        PRINT @l_msg
    END -- else layout management not created correctly


--*****************************************************************************
--** Create Domain Scheme Management on system root                          **
--*****************************************************************************

    -- create domain scheme container:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_sysDomainSchemes',
        @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_admin, 1, @c_TVDomainSchemeContainer,
        @l_name, @l_root_s, 1, 0, @c_NOOID_s, @l_desc,
        @l_cid_s OUTPUT

    IF (@l_retVal = @c_ALL_RIGHT) -- domain scheme management created correctly?
    BEGIN
        -- set rights on domain scheme container:
        EXEC p_stringToByte @l_cid_s, @l_cid OUTPUT

        -- set rights:
        SELECT  @l_rights = SUM (id)
        FROM    ibs_Operation
        WHERE   name IN (N'view', N'read', N'viewRights',
                    N'new', N'addElem', N'delElem', N'viewElems', N'viewProtocol')
        EXEC p_Rights$setRights @l_cid, @l_admin, @l_rights, 1

        PRINT 'cBData: Domain Scheme Management correctly defined.'
    END -- if domain scheme management created correctly
    ELSE                    -- domain scheme management not created correctly
    BEGIN
        SELECT  @l_msg = 'cBData: Error when creating Domain Scheme Management:' +
                ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
        PRINT @l_msg
    END -- else domain scheme management not created correctly


--*****************************************************************************
--** Cumulate the rights                                                     **
--*****************************************************************************

    -- ensure that the rights are correctly cumulated:
    EXEC p_Rights$updateRightsCum
    PRINT 'cBData: Rights cumulation finished.'
    GO


--*****************************************************************************
--** create workflow rights mappings                                         **
--*****************************************************************************
    -- create rights entries: rights are kind of hierarchical
    INSERT  ibs_RightsMapping VALUES  (N'READ', N'READ')
    INSERT  ibs_RightsMapping VALUES  (N'READ', N'VIEW')
    INSERT  ibs_RightsMapping VALUES  (N'READ', N'VIEWELEMS')
    GO

    INSERT  ibs_RightsMapping VALUES  (N'CREATE', N'READ')
    INSERT  ibs_RightsMapping VALUES  (N'CREATE', N'VIEW')
    INSERT  ibs_RightsMapping VALUES  (N'CREATE', N'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  (N'CREATE', N'NEW')
    INSERT  ibs_RightsMapping VALUES  (N'CREATE', N'ADDELEM')
    GO

    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'READ')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'VIEW')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'NEW')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGE', N'CHANGE')
    GO

    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'READ')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'VIEW')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'NEW')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'CHANGE')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'DELETE')
    INSERT  ibs_RightsMapping VALUES  (N'CHANGEDELETE', N'DELELEM')
    GO

    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'READ')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'VIEW')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'NEW')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'CHANGE')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'DELETE')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'DELELEM')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'VIEWRIGHTS')
--    no setrights ALLNOSETRIGHTSowed for workflow-users
--    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'SETRIGHTS')
    INSERT  ibs_RightsMapping VALUES  (N'ALLNOSETRIGHTS', N'VIEWPROTOCOL')
    GO

    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'READ')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'VIEW')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'NEW')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'CHANGE')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'DELETE')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'DELELEM')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'VIEWRIGHTS')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'SETRIGHTS')
    INSERT  ibs_RightsMapping VALUES  (N'ALL', N'VIEWPROTOCOL')
    GO


COMMIT TRANSACTION
-- show count messages again:
SET NOCOUNT OFF
GO
