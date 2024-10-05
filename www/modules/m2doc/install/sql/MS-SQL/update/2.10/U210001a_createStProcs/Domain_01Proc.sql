/******************************************************************************
 * All stored procedures regarding the domain table. <BR>
 *
 * @version     $Id: Domain_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  980725
 ******************************************************************************
 */

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

    -- check if there is a catalog management to create for the domain:
    IF EXISTS (SELECT *
                FROM    ibs_DomainScheme_01
                WHERE   id = @ai_schemeId
                AND hasCatalogManagement = 1)
                                        -- the scheme specifies a catalog
                                        -- management?
    BEGIN
        -- create catalog management:
        -- Katalogverwaltung:
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domCatalogManagement', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @ai_userId, 0x00000001, 0x01010021, -- Container
                @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT
        EXEC p_stringToByte @l_cid_s, @l_cid OUTPUT
        -- set rights on catalog management:
        EXEC p_Rights$deleteObjectRights @l_cid
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_admin
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_adminGroup
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_userAdminGroup
        EXEC p_Rights$propagateUserRights @l_public, @l_cid, @l_structAdminGroup

        -- create sub structures:
        -- Produktmarken
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domCatProductBrands', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @ai_userId, 0x00000001, 0x01017201, --
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = @l_oid
        -- Produktschlüsselkategorien
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domCatProductKeyCategories', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @ai_userId, 0x00000001, 0x01016001, --
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = @l_oid
        -- Produktschlüssel
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domCatProductKeys', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @ai_userId, 0x00000001, 0x01015B01, --
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = @l_oid
        -- Warengruppenprofile
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domCatProductGroupProfiles', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @ai_userId, 0x00000001, 0x01010D01, -- ProductGroupContainer
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = @l_oid
        -- Warenprofile
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domCatProductProfiles', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @ai_userId, 0x00000001, 0x01016D01, --
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = @l_oid
        -- Zahlungsarten
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_paymentTypeContainer', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @ai_userId, 0, 0x01016D11,
                @l_name , @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = @l_oid
    END -- if the scheme has a catalog management

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

        -- Export
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDIExport', @l_name OUTPUT, @l_desc OUTPUT
        EXEC p_Object$create @l_admin, @l_localOp, 0x01017a01, -- Export Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
    END -- if the scheme has a data interchange component
GO
-- p_Domain_01$setScheme
