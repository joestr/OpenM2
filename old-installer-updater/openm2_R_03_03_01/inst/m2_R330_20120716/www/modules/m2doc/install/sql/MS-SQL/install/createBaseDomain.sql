/******************************************************************************
 * A base domain within an m2 system. <BR>
 * These data structures are especially designed for one company.
 *
 * @version     $Id: createBaseDomain.sql,v 1.21 2011/10/18 14:40:51 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980810
 ******************************************************************************
 */

-- ä => „, ö => ”, ü => ?, î => á, - => Ž, Ö => ™, _ => š

-- don't show count messages:
SET NOCOUNT ON
BEGIN TRANSACTION
GO


--*****************************************************************************
--** Declarations                                                            **
--*****************************************************************************

DECLARE
    -- declare constants:
    -- domain offset within tVersionId used to insert the domain into the
    -- tVersionId at the correct position. (TVERSIONID ::= 0xDDSSTTTV)
    -- TV stands for TVERSIONID
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_DOMAINOFFSET         INTEGER,
    @c_TVDomain             INTEGER,
    @c_TVContainer          INTEGER,
    @c_TVDiscussionContainer INTEGER,
    @c_TVDiscussion         INTEGER,
    @c_TVStore              INTEGER,
    @c_TVCatalog            INTEGER,
    @c_TVProductSourceContainer INTEGER,
    @c_TVTerminplanContainer INTEGER,
    @c_TVTerminplan         INTEGER,
    @c_TVMasterDataContainer INTEGER,
    @c_TVPhoneBookContainer INTEGER,
    @c_TVPBPersNumContainer INTEGER,
    @c_languageId           INTEGER,        -- the current language

    -- declare variables:
    @l_cnt                  INTEGER,
    @l_admin                INTEGER,
    @l_rightsAdmin          INTEGER,
    @l_root                 OBJECTID,
    @l_root_s               OBJECTIDSTRING,
    @l_domainId             INTEGER,
    @l_idDomain             INTEGER,
    @l_domainOid            OBJECTID,
    @l_domainOid_s          OBJECTIDSTRING,
    @l_domainScheme         INTEGER,
    @l_domainName           NAME,
    @l_domainAdmin          INTEGER,
    @l_homepagePath         NAME,
    @l_retVal               INTEGER,
    @l_msg                  NVARCHAR (255),  -- the current message text
    @l_str1                 NVARCHAR (255),
    @l_str2                 NVARCHAR (255),
    @l_name                 NAME,           -- name of current object
    @l_desc                 DESCRIPTION,    -- description of current object
    -- declare variables for user:
    @l_userId               INTEGER,
    @l_groupContainer       OBJECTID,
    @l_groupContainer_s     OBJECTIDSTRING,
    @l_grpAll               INTEGER,
    @l_grpAllOid            OBJECTID,
    @l_grpAdmin             INTEGER,
    @l_grpAdminOid          OBJECTID,
    -- declare variables for rights
    @l_rightsStandard       INTEGER,
    @l_rightsRead           INTEGER,
    @l_rightsWrite          INTEGER,
    @l_rightsReadWrite      INTEGER,
    @l_rightsRights         INTEGER,
    @l_rightsReadWriteRights INTEGER,
    @l_rightsProt           INTEGER,
    @l_rightsReadWriteRightsProt INTEGER,
    @l_rightsAll            INTEGER,
    @l_rightsNone           INTEGER,
--########## changeable code ... ##############################################
--/*
--    @l_grpGRP1               INTEGER,
--    @l_grpGRP1Oid            OBJECTID,
--    @l_grpGRP1Oid_s          OBJECTIDSTRING,
--    @l_grpGRP2               INTEGER,
--    @l_grpGRP2Oid            OBJECTID,
--    @l_grpGRP2Oid_s          OBJECTIDSTRING,
--*/
--########## ... changeable code ##############################################
    -- declare variables for public structures
    @l_public               OBJECTID,
    @l_public_s             OBJECTIDSTRING,
    @l_oid                  OBJECTID,
    @l_oid_s                OBJECTIDSTRING,
    @l_cid_s                OBJECTIDSTRING,
    @l_cid2_s               OBJECTIDSTRING,
    -- declare variables for basic users:
    @l_userOid              OBJECTID


-- assign constants:
SELECT
    @c_NOOID = 0x0000000000000000,
    @c_NOOID_s = '0x0000000000000000',
    @c_DOMAINOFFSET         = 16777216,     -- 0x01000000
    @c_TVDomain             = 16842993,     -- 0x010100F1
    @c_TVContainer          = 16842785,     -- 0x01010021
    @c_TVDiscussionContainer = 16845057,    -- 0x01010901
    @c_TVDiscussion         = 16843521,     -- 0x01010301
    @c_TVStore              = 16845569,     -- 0x01010B01
    @c_TVCatalog            = 16845825,     -- 0x01010C01
    @c_TVProductSourceContainer = 16867585, -- 0x01016101
    @c_TVTerminplanContainer = 16844289,    -- 0x01010601
    @c_TVTerminplan         = 16844545,     -- 0x01010701
    @c_TVMasterDataContainer = 16853249,    -- 0x01012901
    @c_TVPhoneBookContainer = 16858625,     -- 0x01013e01
    @c_TVPBPersNumContainer = 16861185,     -- 0x01014801
    @c_languageId = 0

-- initialize local variables:

-- body:
    -- get configuration for installation  (is set in file installConfig.sql)
    EXEC p_System$get N'DOMAIN_NAME', @l_domainName OUTPUT
    EXEC p_System$get N'WWW_HOME_PATH', @l_homepagePath OUTPUT

    -- set domainscheme to standardscheme
    SELECT @l_domainScheme = 0x1

    -- show state message
    SELECT  @l_msg = N'Extending domain ' + @l_domainName + N'...'
    PRINT   @l_msg

    -- get system administrator:
    EXEC p_System$getInt N'ID_sysAdmin', @l_admin OUTPUT

    -- get root:
    SELECT  @l_root = oid
    FROM    ibs_Object
    WHERE   containerId = @c_NOOID
       AND  tVersionId = 0x01015301
    EXEC p_byteToString @l_root, @l_root_s OUTPUT

    -- get the domain data:
    SELECT  @l_domainId = id, @l_idDomain = id * 0x01000000,
            @l_domainAdmin = adminId,
            @l_grpAll = allGroupId, @l_grpAdmin = adminGroupId,
            @l_groupContainer = groupsOid,
            @l_public = publicOid
    FROM    ibs_Domain_01
    WHERE   id =
            (
                SELECT  MIN (d.id)
                FROM    ibs_Domain_01 d, ibs_Object o
                WHERE   d.oid = o.oid
                    AND o.name = @l_domainName
            )


--*****************************************************************************
--** Get rights                                                              **
--*****************************************************************************

    -- get read rights:
    SELECT  @l_rightsRead = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'view', N'read', N'viewElems', N'createLink', N'distribute')
    -- get write rights:
    SELECT  @l_rightsWrite = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'new', N'change', N'delete', N'addElem', N'delElem')
    -- get rights for viewing and setting rights:
    SELECT  @l_rightsRights = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'viewRights', N'setRights')
    -- get rights for viewing the protocol:
    SELECT  @l_rightsProt = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'viewProtocol')
    -- get read/write rights:
    SELECT  @l_rightsReadWrite = @l_rightsRead | @l_rightsWrite
    -- get read/write/rights rights:
    SELECT  @l_rightsReadWriteRights = @l_rightsReadWrite | @l_rightsRights
    -- get all rights and no rights:
    SELECT  @l_rightsReadWriteRightsProt = @l_rightsReadWriteRights | @l_rightsProt
    -- get all rights and no rights:
    SELECT  @l_rightsAll = SUM (id), @l_rightsNone = 0
    FROM    ibs_Operation
    -- set common rights for all users:
    SELECT  @l_rightsStandard = @l_rightsRead



--*****************************************************************************
--** Create public structures within a domain                                **
--*****************************************************************************

    -- get public container:
    EXEC p_byteToString @l_public, @l_public_s OUTPUT

    -- set executing user:
    SELECT  @l_userId = @l_domainAdmin


--########## changeable code ... ##############################################
/*
    -- delete rights of group of all users on public container:
    DELETE  ibs_Rights
    WHERE   rOid = @l_public
        AND rPersonId = @l_grpAll
    -- set rights of GRP1 group on public container:
    EXEC p_Rights$setRights @l_public, @l_grpGRP1, @l_rightsStandard, 0
*/


/*
    -- disable rights of group of GRP1 users on actual container:
    EXEC p_Rights$setRights @l_oid, @l_grpGRP1, 0, 0
*/
/*
    -- set rights of GRP2 group on actual container:
    EXEC p_stringToByte @l_cid_s, @l_oid OUTPUT
    EXEC p_Rights$setRights @l_oid, @l_grpGRP2, @l_rightsStandard, 0
*/


/*
    EXEC p_ObjectDesc_01$get @c_languageId, 'OD_<tag name>', @l_name OUTPUT, @l_desc OUTPUT
    EXEC p_<object type>$create @l_userId, 0x00000001, <tVersionId>,
            @l_name, @l_<super container>, 1, 0, @c_NOOID_s, @l_desc, @l_<out variable>_s OUTPUT
*/

    -- Discussions:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDiscussions', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_DiscContainer_01$create @l_userId, 0x00000001, @c_TVDiscussionContainer, -- DiscussionContainer
            @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDiscM2', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Discussion_01$create @l_userId, 0x00000001, @c_TVDiscussion, -- Discussion
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDiscProjects', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Discussion_01$create @l_userId, 0x00000001, @c_TVDiscussion, -- Discussion
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDiscInfoTips', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Discussion_01$create @l_userId, 0x00000001, @c_TVDiscussion, -- Discussion
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDiscOffers', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Discussion_01$create @l_userId, 0x00000001, @c_TVDiscussion, -- Discussion
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT

    -- Warenangebote:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domProducts', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVStore, -- Store
            @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT
--/*
--        EXEC p_ObjectDesc_01$get @c_languageId, 'OD_domProdLeaflets', @l_name OUTPUT, @l_desc OUTPUT
--        EXEC @l_retVal = p_Catalog_01$create @l_userId, 0x00000001, @c_TVCatalog, -- Catalog
--                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
--        EXEC p_ObjectDesc_01$get @c_languageId, 'OD_domProdSupplier1', @l_name OUTPUT, @l_desc OUTPUT
--        EXEC @l_retVal = p_Catalog_01$create @l_userId, 0x00000001, @c_TVCatalog, -- Catalog
--                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
--
--        EXEC p_ObjectDesc_01$get @c_languageId, 'OD_domProdSourceEvidence', @l_name OUTPUT, @l_desc OUTPUT
--        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVProductSourceContainer, -- ProductSourceContainer
--                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
--*/

    -- Termine:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDates', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVTerminplanContainer, -- TerminplanContainer
            @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDatCommon', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVTerminplan, -- Terminplan
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDatAdvertising', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVTerminplan, -- Terminplan
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDatEvents', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVTerminplan, -- Terminplan
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDatTraining', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVTerminplan, -- Terminplan
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT

    -- Stammdaten:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domMasterData', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
            @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domMadSuppliers', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVMasterDataContainer, -- MasterDataContainer
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domMadMembers', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVMasterDataContainer, -- MasterDataContainer
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domMadPartners', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVMasterDataContainer, -- MasterDataContainer
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT

/*
    -- set rights of GRP2 group on public container which shall not be left
    -- to subsequent objects:
    EXEC p_Rights$setRights @l_public, @l_grpGRP2, @l_rightsStandard, 0
*/
--########## ... changeable code ##############################################


PRINT 'Domain structure extended.'


--*****************************************************************************
--** Create basic users                                                      **
--*****************************************************************************

    -- create users:
    --
    -- EXEC p_User_01$createFast uid, domainId, username, password, fullname
    --########## changeable code ... ##########################################
/*
    EXEC p_User_01$createFast @l_userId, @l_domainId, 'Debug', 'isEnc_ZHQ%3D', 'Debug', @l_userOid OUTPUT
*/
    --########## ... changeable code ##########################################

PRINT 'Users created.'

FINISH:
GO
COMMIT TRANSACTION
-- show count messages again:
SET NOCOUNT OFF
GO
