/*******************************************************************************
 * A base domain within an m2 system. <BR>
 * These data structures are especially designed for one company.
 *
 * @version     $Id: createBaseDomain.sql,v 1.19 2004/01/16 00:44:05 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  990323
 ******************************************************************************
 */


--*****************************************************************************
--** Declarations                                                            **
--*****************************************************************************
DECLARE
    -- declare constants:
    -- domain offset within tVersionId used to insert the domain into the
    -- tVersionId at the correct position. (TVERSIONID ::= 0xDDSSTTTV)
    -- TV stands for TVERSIOID
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything's all right
    c_NOOID                 CONSTANT RAW (8) := hextoraw ('0000000000000000');
                                            -- default value for no defined oid
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
                                            -- no oid as string
    c_DOMAINOFFSET          CONSTANT INTEGER := 16777216; -- 0x01000000
    c_TVDomain              CONSTANT INTEGER := 16842993; -- 0x010100F1
    c_TVContainer           CONSTANT INTEGER := 16842785; -- 0x01010021
    c_TVDiscussionContainer CONSTANT INTEGER := 16845057; -- 0x01010901
    c_TVDiscussion          CONSTANT INTEGER := 16843521; -- 0x01010301
    c_TVStore               CONSTANT INTEGER := 16845569; -- 0x01010B01;
    c_TVCatalog             CONSTANT INTEGER := 16845825; -- 0x01010C01;
    c_TVProductSourceContainer CONSTANT INTEGER := 16867585; -- 0x01016101;
    c_TVTerminplanContainer CONSTANT INTEGER := 16844289; -- 0x01010601;
    c_TVTerminplan          CONSTANT INTEGER := 16844545; -- 0x01010701;
    c_TVMasterDataContainer CONSTANT INTEGER := 16853249; -- 0x01012901;
    c_TVPhoneBookContainer  CONSTANT INTEGER := 16858625; -- 0x01013e01;
    c_TVPBPersNumContainer  CONSTANT INTEGER := 16861185; -- 0x01014801;
    c_languageId            CONSTANT INTEGER := 0; -- the current language

    -- declare variables:
    l_file                  VARCHAR2 (20) := 'createBaseDomain';
                                            -- name of actual file
    l_retValue              INTEGER;        -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_cnt                   INTEGER;
    l_admin                 INTEGER;
    l_rightsAdmin           INTEGER;
    l_root                  RAW (8);
    l_root_s                VARCHAR2 (18);
    l_domainId              INTEGER;
    l_idDomain              INTEGER;
    l_domainOid             RAW (8);
    l_domainOid_s           VARCHAR2 (18);
    l_domainScheme          INTEGER;
    l_domainName            VARCHAR2 (63);
    l_domainAdmin           INTEGER;
    l_homepagePath          VARCHAR2 (63);
    l_msg                   VARCHAR2 (255);
    l_str1                  VARCHAR2 (255);
    l_str2                  VARCHAR2 (255);
    l_name                  VARCHAR2 (63);  -- name of current object
    l_desc                  VARCHAR2 (255); -- description of current object
    -- declare variables for user:
    l_userId                INTEGER;
    l_groupContainer        RAW (8);
    l_groupContainer_s      VARCHAR2 (18);
    l_grpAll                INTEGER;
    l_grpAllOid             RAW (8);
    l_grpAdmin              INTEGER;
    l_grpAdminOid           RAW (8);
    -- declare variables for rights
    l_rightsStandard        INTEGER;
    l_rightsRead            INTEGER;
    l_rightsWrite           INTEGER;
    l_rightsReadWrite       INTEGER;
    l_rightsRights          INTEGER;
    l_rightsReadWriteRights INTEGER;
    l_rightsProt            INTEGER;
    l_rightsReadWriteRightsProt INTEGER;
    l_rightsAll             INTEGER;
    l_rightsNone            INTEGER;
--########## changeable code ... ##############################################
--/*
--    l_grpGRP1               INTEGER;
--    l_grpGRP1Oid            RAW (8);
--    l_grpGRP1Oid_s          VARCHAR2 (18);
--    l_grpGRP2               INTEGER;
--    l_grpGRP2Oid            RAW (8);
--    l_grpGRP2Oid_s          VARCHAR2 (18);
--*/
--########## ... changeable code ##############################################
    -- declare variables for public structures
    l_public                RAW (8);
    l_public_s              VARCHAR2 (18);
    l_oid                   RAW (8);
    l_oid_s                 VARCHAR2 (18);
    l_cid_s                 VARCHAR2 (18);
    l_cid2_s                VARCHAR2 (18);
    -- declare variables for basic users:
    l_userOid               RAW (8);

    -- exceptions:
    e_exception             EXCEPTION;      -- common exception

-- body:
BEGIN
    -- get configuration for installation  (is set in file installConfig.sql)
    p_System$get ('DOMAIN_NAME', l_domainName);
    p_System$get ('WWW_HOME_PATH', l_homepagePath);

    -- set domainscheme to standardscheme
    l_domainScheme := 9;

    -- ensure that the domain scheme exists:
    l_cnt := 0;
    SELECT  COUNT (id)
    INTO    l_cnt
    FROM    ibs_DomainScheme_01
    WHERE   id = l_domainScheme;

    IF (l_cnt <= 0)                         -- domain scheme not found?
    THEN
        -- get the first domain scheme to be found:
        SELECT  MIN (id), COUNT (id)
        INTO    l_domainScheme, l_cnt
        FROM    ibs_DomainScheme_01;

        -- check if there was a scheme found:
        IF (l_cnt > 0)                  -- scheme found?
        THEN
            debug ('Required domain scheme not found, using scheme ' ||
                     l_domainScheme || '.');
        -- if scheme found
        ELSE                            -- no scheme found
            -- log the error:
            l_ePos := 'Could not find domain scheme for id = ' ||
                      l_domainScheme || '.';
            RAISE e_exception;          -- call common exception handler
        END IF; -- else not scheme found
    END IF;

    -- show state message
    debug ('Creating domain ' || l_domainName || '...');
    debug ('');

    -- get system administrator:
    p_System$getInt ('ID_sysAdmin', l_admin);
    IF (l_admin IS NULL)                -- the administrator was not found?
    THEN
        -- log the error:
        l_ePos := 'Could not find system administrator with id = ' ||
                  l_admin || ' for domain with id = ' || l_domainScheme || '.';
        RAISE e_exception;              -- call common exception handler
    END IF;

    -- get root:
    BEGIN
        SELECT  MIN (oid)
        INTO    l_root
        FROM    ibs_Object
        WHERE   containerId = createOid (0, 0);
        p_byteToString (l_root, l_root_s);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            -- log the error:
            l_ePos := 'Could not find system root.';
            RAISE;                      -- call common exception handler
    END;

    -- create the domain:
    p_ObjectDesc_01$get (c_languageId, 'OD_domDomain', l_name, l_desc);
    l_str1 := l_name || l_domainName;
    l_str2 := l_desc || l_domainName;

    -- create the domain with the SSL option 0, what means that no SSL is
    -- required:
    l_retValue := p_Domain_01$create (
        l_admin, 1, c_TVDomain, l_domainName, l_root_s, 1,
        0, c_NOOID_s, l_str2, 0, l_domainOid_s);
    p_stringToByte (l_domainOid_s, l_domainOid);

    IF (l_retValue <> c_ALL_RIGHT)        -- error in domain creation?
    THEN
        -- log the error:
        l_ePos := 'Error code when creating domain: ' ||
                  l_retValue || '.';
        RAISE e_exception;              -- call common exception handler
    END IF; -- if error in domain creation

    -- get the domain id:
    BEGIN
        SELECT  id, id * c_DOMAINOFFSET
        INTO    l_domainId, l_idDomain
        FROM    ibs_Domain_01
        WHERE   oid = l_domainOid;
    EXCEPTION
        WHEN OTHERS THEN
            -- log the error:
            l_ePos := 'Could not find domain id for given domain oid ' ||
                      l_domainOid || '.';
            RAISE;                      -- call common exception handler
    END;

    -- get administrator of domain:
    BEGIN
        SELECT  adminId
        INTO    l_domainAdmin
        FROM    ibs_Domain_01
        WHERE   id = l_domainId;
    EXCEPTION
        WHEN OTHERS THEN
            -- log the error:
            l_ePos := 'Could not find domain administrator for domain' ||
                     ' with id ' || l_domainId || '.';
            RAISE;                      -- call common exception handler
    END;

    -- set the domain scheme:
    p_Domain_01$setScheme
        (l_domainAdmin, l_domainId, l_domainScheme, l_homepagePath);

    -- check for error:
    IF (l_retValue = 1)                   -- no error during creation
    THEN
        debug ('Domain created.');
    ELSIF (l_retValue = 2)                -- insufficient rights
    THEN
        debug ('Insufficient rights to create domain.');
    ELSIF (l_retValue = 21)               -- domain already exists
    THEN
        debug ('Domain already exists.');
    ELSE                                -- error during creation
        debug ('Error during creation of domain: ' || TO_CHAR (l_retValue));
    END IF; -- else error during creation

    IF (l_retValue <> c_ALL_RIGHT)
    THEN
        -- log the error:
        l_ePos := 'Could not create domain; retVal = ' || l_retValue || '.';
        RAISE e_exception;              -- call common exception handler
    END IF;

--*****************************************************************************
--** Create user groups                                                      **
--*****************************************************************************

    l_userId := l_domainAdmin;

    -- get group of all users:
    SELECT  id, oid
    INTO    l_grpAll, l_grpAllOid
    FROM    ibs_Group
    WHERE   id IN
            (
                SELECT  allGroupId
                FROM    ibs_Domain_01
                WHERE   id = l_domainId
            );

    -- get group of all administrators:
    SELECT  id, oid
    INTO    l_grpAdmin, l_grpAdminOid
    FROM    ibs_Group
    WHERE   id IN
            (
                SELECT  adminGroupId
                FROM    ibs_Domain_01
                WHERE   id = l_domainId
            );

    -- get group container:
    SELECT  groupsOid
    INTO    l_groupContainer
    FROM    ibs_Domain_01
    WHERE   id = l_domainId;
    p_byteToString (l_groupContainer, l_groupContainer_s);

--########## changeable code ... ##############################################
--/*
--    -- create GRP1 group:
--    l_desc := 'Alle Benutzer der Gruppe GRP1.';
--    l_retValue := p_Group_01$create (l_userId, 0x00000001, 0x010100b1, 'GRP1',
--        l_groupContainer_s, 1, 0, 0x0000000000000000, l_desc,
--        l_grpGRP1Oid_s);
--    p_stringToByte (l_grpGRP1Oid_s, l_grpGRP1Oid);
--    -- get id of GRP1 group:
--    SELECT  id
--    INTO    l_grpGRP1
--    FROM    ibs_Group
--    WHERE   oid = l_grpGRP1Oid;

--    -- add GRP2 group to GRP1 group:
--    p_Group_01$addGroup (l_userId, l_grpGRP1Oid, l_grpGRP2Oid);
--*/
--########## ... changeable code ##############################################

    debug ('Groups structure created.');



--*****************************************************************************
--** Get rights                                                              **
--*****************************************************************************

    -- get read rights:
    SELECT  SUM (id)
    INTO    l_rightsRead
    FROM    ibs_Operation
    WHERE   name IN ('view', 'read', 'viewElems', 'createLink', 'distribute');
    -- get write rights:
    SELECT  SUM (id)
    INTO    l_rightsWrite
    FROM    ibs_Operation
    WHERE   name IN ('new', 'change', 'delete', 'addElem', 'delElem');
    -- get rights for viewing and setting rights:
    SELECT  SUM (id)
    INTO    l_rightsRights
    FROM    ibs_Operation
    WHERE   name IN ('viewRights', 'setRights');
    -- get rights for viewing the protocol:
    SELECT  SUM (id)
    INTO    l_rightsProt
    FROM    ibs_Operation
    WHERE   name IN ('viewProtocol');
    -- get read/write rights:
    l_rightsReadWrite := B_OR (l_rightsRead, l_rightsWrite);
    -- get read/write/rights rights:
    l_rightsReadWriteRights := B_OR (l_rightsReadWrite, l_rightsRights);
    -- get all rights and no rights:
    l_rightsReadWriteRightsProt := B_OR (l_rightsReadWriteRights, l_rightsProt);
    -- get all rights and no rights:
    l_rightsNone := 0;
    SELECT  SUM (id)
    INTO    l_rightsAll
    FROM    ibs_Operation;
    -- set common rights for all users:
    l_rightsStandard := l_rightsRead;


--*****************************************************************************
--** Create public structures within a domain                                **
--*****************************************************************************

    -- get public container:
    p_ObjectDesc_01$get (c_languageId, 'OD_domPublic', l_name, l_desc);
    SELECT  oid
    INTO    l_public
    FROM    ibs_Object
    WHERE   containerId = l_domainOid
    AND     name = l_name;
    p_byteToString (l_public, l_public_s);


--########## changeable code ... ##############################################
--/*
--    -- delete rights of group of all users on public container:
--    DELETE  ibs_Rights
--    WHERE   rOid = l_public
--        AND rPersonId = l_grpAll;
--    -- set rights of GRP1 group on public container:
--    p_Rights$setRights (l_public, l_grpGRP1, l_rightsStandard, 0);
--*/


--/*
--    -- disable rights of group of GRP1 users on actual container:
--    p_Rights$setRights (l_oid, l_grpGRP1, 0, 0);
--*/
--/*
--    -- set rights of GRP2 group on actual container:
--    p_stringToByte (l_cid_s, l_oid);
--    p_Rights$setRights (l_oid, l_grpGRP2, l_rightsStandard, 0);
--*/


--/*
--    p_ObjectDesc_01$get (c_languageId, 'OD_<tag name>', l_name, l_desc);
--    p_<object type>$create (l_userId, 1, <tVersionId>,
--            l_name, l_<super container>, 1, 0, c_NOOID_s, l_desc, l_<out variable>_s);
--*/

    -- Informationen:
    p_ObjectDesc_01$get (c_languageId, 'OD_domInformation', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domInfoCommon', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domInfoPress', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domInfoArticles', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domInfoForms', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domInfoSideGlances', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domInfoTrendInfo', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

    -- Discussionen:
    p_ObjectDesc_01$get (c_languageId, 'OD_domDiscussions', l_name, l_desc);
    l_retValue := p_DiscContainer_01$create (l_userId, 1, c_TVDiscussionContainer, -- DiscussionContainer
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDiscM2', l_name, l_desc);
    l_retValue := p_Discussion_01$create (l_userId, 1, c_TVDiscussion, -- Discussion
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDiscProjects', l_name, l_desc);
    l_retValue := p_Discussion_01$create (l_userId, 1, c_TVDiscussion, -- Discussion
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDiscInfoTips', l_name, l_desc);
    l_retValue := p_Discussion_01$create (l_userId, 1, c_TVDiscussion, -- Discussion
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDiscOffers', l_name, l_desc);
    l_retValue := p_Discussion_01$create (l_userId, 1, c_TVDiscussion, -- Discussion
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

    -- Warenangebote:
    p_ObjectDesc_01$get (c_languageId, 'OD_domProducts', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVStore, -- Store
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
--/*
--        p_ObjectDesc_01$get (c_languageId, 'OD_domProdLeaflets', l_name, l_desc);
--        l_retValue := p_Catalog_01$create (l_userId, 1, c_TVCatalog, -- Catalog
--                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--        p_ObjectDesc_01$get (c_languageId, 'OD_domProdSupplier1', l_name, l_desc);
--        l_retValue := p_Catalog_01$create (l_userId, 1, c_TVCatalog, -- Catalog
--                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--
--        p_ObjectDesc_01$get (c_languageId, 'OD_domProdSourceEvidence', l_name, l_desc);
--        l_retValue := p_Object$create (l_userId, 1, c_TVProductSourceContainer, -- ProductSourceContainer
--                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--*/
    -- Termine:
    p_ObjectDesc_01$get (c_languageId, 'OD_domDates', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVTerminplanContainer, -- TerminplanContainer
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDatCommon', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVTerminplan, -- Terminplan
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDatAdvertising', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVTerminplan, -- Terminplan
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDatEvents', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVTerminplan, -- Terminplan
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domDatTraining', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVTerminplan, -- Terminplan
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

    -- Projekte:
    p_ObjectDesc_01$get (c_languageId, 'OD_domProjects', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domProjM2', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

    -- Stammdaten:
    p_ObjectDesc_01$get (c_languageId, 'OD_domMasterData', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVContainer, -- Container
            l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domMadSuppliers', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVMasterDataContainer, -- MasterDataContainer
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domMadMembers', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVMasterDataContainer, -- MasterDataContainer
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    p_ObjectDesc_01$get (c_languageId, 'OD_domMadPartners', l_name, l_desc);
    l_retValue := p_Object$create (l_userId, 1, c_TVMasterDataContainer, -- MasterDataContainer
            l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);


    -- set rights of all group on public container:
    p_Rights$setRights (l_public, l_grpAll, l_rightsStandard, 0);

--/*
--    -- set rights of GRP2 group on public container which shall not be left
--    -- to subsequent objects:
--    EXEC p_Rights$setRights l_public, l_grpGRP2, l_rightsStandard, 0
--*/
--########## ... changeable code ##############################################


    debug ('Domain structure created.');


--*****************************************************************************
--** Create basic users                                                      **
--*****************************************************************************


    -- create users:
    --
    -- l_retValue := p_User_01$createFast (userId, domainId, username, password, fullname, l_userOid);
--########## changeable code ... ##############################################
    l_retValue := p_User_01$createFast (l_userId, l_domainId, 'Debug', 'isEnc_ZHQ%3D%0A', 'Debug', l_userOid);
--########## ... changeable code ##############################################

    debug ('Users created.');

    -- cumulate the rights:
    CALL TECTUMDEV1.p_Rights$updateRightsCum;

    debug ('Rights cumulated.');
    debug (l_file || ': finished');

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_file || ': ' || l_ePos ||
            '; l_retValue = ' || l_retValue ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        debug (l_eText);
        ibs_error.log_error (ibs_error.error, l_file, l_eText);
        raise_application_error (-20000, l_eText);
        -- show state message:
        debug (l_file || ': finished');
        RAISE;
END;
/


EXIT;
