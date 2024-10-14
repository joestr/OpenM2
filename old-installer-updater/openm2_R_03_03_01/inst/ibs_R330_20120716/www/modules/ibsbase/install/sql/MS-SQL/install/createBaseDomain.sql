/******************************************************************************
 * A base domain within an the framework. <BR>
 * These data structures are especially designed for one company.
 *
 * @version     $Id: createBaseDomain.sql,v 1.22 2011/10/18 14:40:51 rburgermann Exp $
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
    @c_languageId = 0

-- initialize local variables:

-- body:
    -- get configuration for installation  (is set in file installConfig.sql)
    EXEC p_System$get N'DOMAIN_NAME', @l_domainName OUTPUT
    EXEC p_System$get N'WWW_HOME_PATH', @l_homepagePath OUTPUT

    -- set domainscheme to standardscheme
    SELECT @l_domainScheme = 0x1


    -- ensure that the domain scheme exists:
    IF NOT EXISTS (
        SELECT  *
        FROM    ibs_DomainScheme_01
        WHERE   id = @l_domainScheme
    )                                   -- domain scheme not found?
    BEGIN
        -- get the first domain scheme to be found:
        SELECT  @l_domainScheme = MIN (id)
        FROM    ibs_DomainScheme_01

        -- check if there was a scheme found:
        IF (@l_domainScheme <> null)    -- scheme found?
        BEGIN
            SELECT  @l_msg = 'Required domain scheme not found, using scheme ' +
                    CONVERT (VARCHAR (10), @l_domainScheme) + '.'
            PRINT   @l_msg
        END -- if scheme found
        ELSE                            -- no scheme found
        BEGIN
            PRINT   'No domain scheme was found, domain could not be created.'
            GOTO FINISH
        END -- else not scheme found
    END -- if domain scheme not found


    -- show state message
    SELECT  @l_msg = 'Creating domain ' + @l_domainName + '...'
    PRINT   @l_msg

    -- get system administrator:
    EXEC p_System$getInt N'ID_sysAdmin', @l_admin OUTPUT

    -- get root:
    SELECT  @l_root = oid
    FROM    ibs_Object
    WHERE   containerId = @c_NOOID
       AND  tVersionId = 0x01015301
    EXEC p_byteToString @l_root, @l_root_s OUTPUT

    -- create the domain:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domDomain', @l_name OUTPUT, @l_desc OUTPUT
    SELECT  @l_str1 = @l_name + @l_domainName,
            @l_str2 = @l_desc + @l_domainName
    -- create the domain with the SSL option 0, what means that no SSL is required
    EXEC @l_retVal = p_Domain_01$create @l_admin, 0x00000001, 0x010100F1,
            @l_domainName, @l_root_s, 1,
            0, @c_NOOID, @l_str2, 0, @l_domainOid_s OUTPUT
    EXEC p_stringToByte @l_domainOid_s, @l_domainOid OUTPUT

    -- get the domain id:
    SELECT  @l_domainId = id, @l_idDomain = id * 0x01000000
    FROM    ibs_Domain_01
    WHERE   oid = @l_domainOid

    -- get administrator of domain:
    SELECT  @l_domainAdmin = adminId
    FROM    ibs_Domain_01
    WHERE   id = @l_domainId

    -- set the domain scheme:
    EXEC p_Domain_01$setScheme @l_domainAdmin, @l_domainId, @l_domainScheme, @l_homepagePath

    -- check for error:
    IF (@l_retVal = 1)                  -- no error during creation
        PRINT 'Domain created.'
    ELSE IF (@l_retVal = 2)             -- insufficient rights
        PRINT 'Insufficient rights to create domain.'
    ELSE IF (@l_retVal = 21)            -- domain already exists
        PRINT 'Domain already exists.'
    ELSE                                -- error during creation
    BEGIN
        SELECT  @l_msg = 'Error during creation of domain: ' +
                CONVERT (VARCHAR (10), @l_retVal)
        PRINT @l_msg
    END -- else error during creation

    IF (@l_retVal <> 1)
        GOTO FINISH


--*****************************************************************************
--** Create user groups                                                      **
--*****************************************************************************

    SELECT  @l_userId = @l_domainAdmin

    -- get group of all users:
    SELECT  @l_grpAll = id, @l_grpAllOid = oid
    FROM    ibs_Group
    WHERE   id IN
            (
                SELECT  allGroupId
                FROM    ibs_Domain_01
                WHERE   id = @l_domainId
            )

    -- get group of all administrators:
    SELECT  @l_grpAdmin = id, @l_grpAdminOid = oid
    FROM    ibs_Group
    WHERE   id IN
            (
                SELECT  adminGroupId
                FROM    ibs_Domain_01
                WHERE   id = @l_domainId
            )

    -- get group container:
    SELECT  @l_groupContainer = groupsOid
    FROM    ibs_Domain_01
    WHERE   id = @l_domainId
    EXEC p_byteToString @l_groupContainer, @l_groupContainer_s OUTPUT


--########## changeable code ... ##############################################
/*
    -- create GRP1 group:
    SELECT  @l_desc = 'Alle Benutzer der Gruppe GRP1.'
    EXEC @l_retVal = p_Group_01$create @l_userId, 0x00000001, 0x010100b1, N'GRP1',
        @l_groupContainer_s, 1, 0, @c_NOOID, @l_desc,
        @l_grpGRP1Oid_s OUTPUT
    EXEC p_stringToByte @l_grpGRP1Oid_s, @l_grpGRP1Oid OUTPUT
    -- get id of GRP1 group:
    SELECT  @l_grpGRP1 = id
    FROM    ibs_Group
    WHERE   oid = @l_grpGRP1Oid

    -- add GRP2 group to GRP1 group:
    EXEC p_Group_01$addGroup @l_userId, @l_grpGRP1Oid, @l_grpGRP2Oid
*/
--########## ... changeable code ##############################################

PRINT 'Groups structure created.'



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
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domPublic', @l_name OUTPUT, @l_desc OUTPUT
    SELECT  @l_public = oid
    FROM    ibs_Object
    WHERE   containerId = @l_domainOid
        AND name = @l_name
    EXEC p_byteToString @l_public, @l_public_s OUTPUT


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
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_<tag name>', @l_name OUTPUT, @l_desc OUTPUT
    EXEC p_<object type>$create @l_userId, 0x00000001, <tVersionId>,
            @l_name, @l_<super container>, 1, 0, @c_NOOID_s, @l_desc, @l_<out variable>_s OUTPUT
*/

    -- MenuObjects:
    EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
            N'MenuObjects', @l_public_s, 1, 0, @c_NOOID_s,
            N'This container is intended to contain all menu object containers.',
            @l_cid_s OUTPUT

            -- create key mapper for menuobjects container:
            EXEC p_KeyMapper$new @l_cid_s, N'menuobjects', N'ibs_instobj'

    -- Informationen:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domInformation', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
            @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domInfoCommon', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domInfoPress', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domInfoArticles', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domInfoForms', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domInfoSideGlances', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domInfoTrendInfo', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT

    -- Projekte:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domProjects', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
            @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc, @l_cid_s OUTPUT
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domProjM2', @l_name OUTPUT, @l_desc OUTPUT
        EXEC @l_retVal = p_Object$create @l_userId, 0x00000001, @c_TVContainer, -- Container
                @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc, @l_oid_s OUTPUT

    -- set rights of all group on public container:
    EXEC p_Rights$setRights @l_public, @l_grpAll, @l_rightsStandard, 0

/*
    -- set rights of GRP2 group on public container which shall not be left
    -- to subsequent objects:
    EXEC p_Rights$setRights @l_public, @l_grpGRP2, @l_rightsStandard, 0
*/
--########## ... changeable code ##############################################


PRINT 'Domain structure created.'


--*****************************************************************************
--** Create basic users                                                      **
--*****************************************************************************

    -- create users:
    --
    -- EXEC p_User_01$createFast uid, domainId, username, password, fullname
    --########## changeable code ... ##############################################
    EXEC p_User_01$createFast @l_userId, @l_domainId, N'Debug', N'isEnc_ZHQ%3D', N'Debug', @l_userOid OUTPUT
    --########## ... changeable code ##############################################

PRINT 'Users created.'

FINISH:
GO
COMMIT TRANSACTION
-- show count messages again:
SET NOCOUNT OFF
GO
