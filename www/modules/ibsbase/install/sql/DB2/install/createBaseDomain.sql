-------------------------------------------------------------------------------
-- A base domain within the framework. <BR>
-- These data structures are especially designed for one company.
--
-- @version     $Id: createBaseDomain.sql,v 1.7 2003/10/21 22:14:46 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pi_createBaseDomain');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pi_createBaseDomain ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    -- domain offset within tVersionId used to insert the domain into the
    -- tVersionId at the correct position. (TVERSIONID ::= 0xDDSSTTTV)
    -- TV stands for TVERSIONID
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_DOMAINOFFSET  INT DEFAULT 16777216; -- 0x01000000
    DECLARE c_TVDomain      INT DEFAULT 16842993; -- 0x010100F1
    DECLARE c_TVContainer   INT DEFAULT 16842785; -- 0x01010021
    DECLARE c_languageId    INT DEFAULT 0;  -- the current language

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_file          VARCHAR (20) DEFAULT 'createBaseDomain';
                                            -- name of actual file
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_eText         VARCHAR (5000); -- full error text
    DECLARE l_cnt           INT;
    DECLARE l_admin         INT;
    DECLARE l_rightsAdmin   INT;
    DECLARE l_root          CHAR (8) FOR BIT DATA;
    DECLARE l_root_s        VARCHAR (18);
    DECLARE l_domainId      INT;
    DECLARE l_idDomain      INT;
    DECLARE l_domainOid     CHAR (8) FOR BIT DATA;
    DECLARE l_domainOid_s   VARCHAR (18);
    DECLARE l_domainScheme  INT;
    DECLARE l_domainName    VARCHAR (63);
    DECLARE l_domainAdmin   INT;
    DECLARE l_homepagePath  VARCHAR (63);
    DECLARE l_msg           VARCHAR (255);
    DECLARE l_str1          VARCHAR (255);
    DECLARE l_str2          VARCHAR (255);
    DECLARE l_name          VARCHAR (63);   -- name of current object
    DECLARE l_desc          VARCHAR (255);  -- description of current object
    -- declare variables for user:
    DECLARE l_userId        INT;
    DECLARE l_groupContainer CHAR (8) FOR BIT DATA;
    DECLARE l_groupContainer_s VARCHAR (18);
    DECLARE l_grpAll        INT;
    DECLARE l_grpAllOid     CHAR (8) FOR BIT DATA;
    DECLARE l_grpAdmin      INT;
    DECLARE l_grpAdminOid   CHAR (8) FOR BIT DATA;
    -- declare variables for rights
    DECLARE l_rightsStandard INT;
    DECLARE l_rightsRead    INT;
    DECLARE l_rightsWrite   INT;
    DECLARE l_rightsReadWrite INT;
    DECLARE l_rightsRights  INT;
    DECLARE l_rightsReadWriteRights INT;
    DECLARE l_rightsProt    INT;
    DECLARE l_rightsReadWriteRightsProt INT;
    DECLARE l_rightsAll     INT;
    DECLARE l_rightsNone    INT;
--########## changeable code ... ##############################################
/*
    DECLARE l_grpGRP1       INT;
    DECLARE l_grpGRP1Oid    CHAR (8) FOR BIT DATA;
    DECLARE l_grpGRP1Oid_s  VARCHAR (18);
    DECLARE l_grpGRP2       INT;
    DECLARE l_grpGRP2Oid    CHAR (8) FOR BIT DATA;
    DECLARE l_grpGRP2Oid_s  VARCHAR (18);
*/
--########## ... changeable code ##############################################
    -- declare variables for public structures
    DECLARE l_public        CHAR (8) FOR BIT DATA;
    DECLARE l_public_s      VARCHAR (18);
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_oid_s         VARCHAR (18);
    DECLARE l_cid_s         VARCHAR (18);
    DECLARE l_cid2_s        VARCHAR (18);
    -- declare variables for basic users:
    DECLARE l_userOid       CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
      SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_root              = c_NOOID;
    SET l_root_s            = c_NOOID_s;
    SET l_domainOid         = c_NOOID;
    SET l_domainOid_s       = c_NOOID_s;
    SET l_groupContainer    = c_NOOID;
    SET l_groupContainer_s  = c_NOOID_s;
    SET l_grpAllOid         = c_NOOID;
    SET l_grpAdminOid       = c_NOOID;
    SET l_public            = c_NOOID;
    SET l_public_s          = c_NOOID_s;
    SET l_oid               = c_NOOID;
    SET l_oid_s             = c_NOOID_s;
    SET l_cid_s             = c_NOOID_s;
    SET l_cid2_s            = c_NOOID_s;
    SET l_userOid           = c_NOOID;

-- body:
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'start', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- get configuration for installation  (is set in file installConfig.sql)
    CALL IBSDEV1.p_System$get ('DOMAIN_NAME', l_domainName);
    CALL IBSDEV1.p_System$get ('WWW_HOME_PATH', l_homepagePath);
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'get domain config', '', 0, 'l_domainName', l_domainName, '', 0, 'l_homepagePath', l_homepagePath, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- set domainscheme to standardscheme
    SET l_domainScheme = 1;

    -- ensure that the domain scheme exists:
    IF NOT EXISTS
    (
        SELECT  *
        FROM    IBSDEV1.ibs_DomainScheme_01
        WHERE   id = l_domainScheme
    )                                   -- domain scheme not found?
    THEN 
        -- get the first domain scheme to be found:
        SELECT  MIN (id) 
        INTO    l_domainScheme
        FROM    IBSDEV1.ibs_DomainScheme_01;

        -- check if there was a scheme found:
        IF (l_sqlcode = 0 AND l_domainScheme <> 0) -- scheme found?
        THEN
            SET l_msg =
                'Required domain scheme not found, using scheme ' ||
                CAST (rtrim (CHAR (l_domainScheme)) AS VARCHAR (10)) ||
                '.';
CALL IBSDEV1.logError (300, 'cBDomain', l_sqlcode, 'get domain scheme', 'l_domainScheme', l_domainScheme, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            CALL IBSDEV1.p_debug (l_msg);
        -- if scheme found
        ELSE                            -- no scheme found
            SET l_msg = 'No domain scheme was found, domain could not be created.';
CALL IBSDEV1.logError (300, 'cBDomain', l_sqlcode, 'get domain scheme', 'l_domainScheme', l_domainScheme, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            CALL IBSDEV1.p_debug (l_msg);
            GOTO FINISH;
        END IF; -- else no scheme found
    END IF; -- if domain scheme not found

CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'domain scheme', 'l_domainScheme', l_domainScheme, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- show state message:
    SET l_msg = 'Creating domain ' || l_domainName || '...';
    CALL IBSDEV1.p_debug (l_msg);
    CALL IBSDEV1.p_debug ('');

    -- get system administrator:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_System$getInt ('ID_sysAdmin', l_admin);

    IF (l_sqlcode <> 0)                 -- the administrator was not found?
    THEN
        -- log the error:
        SET l_ePos = 'Could not find system administrator with id = ' ||
                  RTRIM (CHAR (l_admin)) || ' for domain with id = ' ||
                  RTRIM (CHAR ( l_domainScheme)) || '.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if the administrator was not found

    -- get root:
    SELECT  MIN (oid)
    INTO    l_root
    FROM    IBSDEV1.ibs_Object
    WHERE   containerId = c_NOOID;

    IF (l_sqlcode <> 0)                 -- the system root was not found?
    THEN
        -- log the error:
        SET l_ePos = 'Could not find system root.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if the system root was not found

    CALL IBSDEV1.p_byteToString (l_root, l_root_s);

    -- create the domain:
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domDomain', l_name, l_desc);
    SET l_str1 = l_name || l_domainName;
    SET l_str2 = l_desc || l_domainName;
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'before create domain', 'l_domainScheme', l_domainScheme, 'l_domainName', l_domainName, 'l_admin', l_admin, 'l_str1', l_str1, 'c_TVDomain', c_TVDomain, 'l_str2', l_str2, '', 0, 'l_root_s', l_root_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- create the domain with the SSL option 0, which means that no SSL is
    -- required:
    CALL IBSDEV1.p_Domain_01$create
        (l_admin, 0, c_TVDomain, l_domainName, l_root_s, 1,
        0, c_NOOID_s, l_str2, 0, l_domainOid_s);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'domain created', 'l_retValue', l_retValue, 'l_domainOid_s', l_domainOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    CALL IBSDEV1.p_stringToByte (l_domainOid_s, l_domainOid);

    IF (l_retValue <> c_ALL_RIGHT)        -- error in domain creation?
    THEN
        -- log the error:
        SET l_ePos = 'Error code when creating domain: ' ||
                     RTRIM (CHAR (l_retValue)) || '.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if error in domain creation

    -- get the domain id:
    SET l_sqlcode = 0;
    SELECT  id, id * c_DOMAINOFFSET
    INTO    l_domainId, l_idDomain
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   oid = l_domainOid;

    IF (l_sqlcode <> 0)                 -- just created domain not found?
    THEN
        -- log the error:
        SET l_ePos = 'Could not find domain id for given domain oid ' ||
                     l_domainOid_s || '.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if just created domain not found

    -- get administrator of domain:
    SELECT  adminId
    INTO    l_domainAdmin
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   id = l_domainId;

    IF (l_sqlcode <> 0)                 -- domain administrator not found?
    THEN
        -- log the error:
        SET l_ePos = 'Could not find domain administrator for domain' ||
                     ' with id ' || RTRIM (CHAR (l_domainId)) || '.';
        GOTO exception1;                -- call common exception handler
    END IF; -- domain administrator not found

    -- set the domain scheme:
    CALL IBSDEV1.p_Domain_01$setScheme
        (l_domainAdmin, l_domainId, l_domainScheme, l_homepagePath);

    -- check for error:
    IF (l_retValue = 1)                   -- no error during creation
    THEN
        CALL IBSDEV1.p_debug ('Domain created.');
    ELSEIF (l_retValue = 2)               -- insufficient rights
    THEN
        CALL IBSDEV1.p_debug ('Insufficient rights to create domain.');
    ELSEIF (l_retValue = 21)              -- domain already exists
    THEN
        CALL IBSDEV1.p_debug ('Domain already exists.');
    ELSE                                -- error during creation
        SET l_msg = 'Error during creation of domain: ' ||
                    RTRIM (CHAR (l_retValue));
        CALL IBSDEV1.p_debug (l_msg);
    END IF; -- else error during creation

    IF (l_retValue <> c_ALL_RIGHT)        -- error in domain creation?
    THEN
        -- log the error:
        SET l_ePos = 'Error code when creating domain: ' ||
                     RTRIM (CHAR (l_retValue)) || '.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if error in domain creation


--*****************************************************************************
--** Create user groups                                                      **
--*****************************************************************************

    SET l_sqlcode = 0;
    SET l_userId = l_domainAdmin;

    -- get group of all users:
    SELECT  id, oid
    INTO    l_grpAll, l_grpAllOid
    FROM    IBSDEV1.ibs_Group
    WHERE   id IN
            (
                SELECT  allGroupId 
                FROM    IBSDEV1.ibs_Domain_01
                WHERE   id = l_domainId
            );

    -- get group of all administrators:
    SELECT  id, oid
    INTO    l_grpAdmin, l_grpAdminOid
    FROM    IBSDEV1.ibs_Group
    WHERE   id IN
            (
                SELECT  adminGroupId 
                FROM    IBSDEV1.ibs_Domain_01
                WHERE   id = l_domainId
            );

    -- get group container:
    SELECT  groupsOid
    INTO    l_groupContainer
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   id = l_domainId;

    CALL IBSDEV1.p_byteToString (l_groupContainer, l_groupContainer_s);

--########## changeable code ... ##############################################
/*
    -- create GRP1 group:
    SELECT l_desc = 'Alle Benutzer der Gruppe GRP1.';
    CALL IBSDEV1.p_Group_01$create (l_userId, 0, 0x010100b1, 'GRP1',
    ;l_groupContainer_s, 1, 0,;c_NOOID,;l_desc,
    ;l_grpGRP1Oid_s OUTPUT
    EXEC p_stringToByte;l_grpGRP1Oid_s,;l_grpGRP1Oid OUTPUT
    -- get id of GRP1 group:
    SELECT;l_grpGRP1 = id
    FROM IBSDEV1.   ibs_Group
    WHERE   oid =;l_grpGRP1Oid

    -- add GRP2 group to GRP1 group:
    CALL IBSDEV1.p_Group_01$addGroup (l_userId, l_grpGRP1Oid, l_grpGRP2Oid, c_NOOID);
*/
--########## ... changeable code ##############################################

    SET l_msg = 'Groups structure created.';
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'user groups', '', 0, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_debug (l_msg);

    IF (l_sqlcode <> 0)                 -- any exception occurred?
    THEN
        -- log the error:
        SET l_ePos = 'Error when creating user groups.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception occurred


--*****************************************************************************
--** Get rights                                                              **
--*****************************************************************************

    SET l_sqlcode = 0;

    -- get read rights:
    SELECT  SUM (id) 
    INTO    l_rightsRead
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('view', 'read', 'viewElems', 'createLink', 'distribute');

    -- get write rights:
    SELECT  SUM (id) 
    INTO    l_rightsWrite
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('new', 'change', 'delete', 'addElem', 'delElem');

    -- get rights for viewing and setting rights:
    SELECT  SUM (id) 
    INTO    l_rightsRights
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('viewRights', 'setRights');

    -- get rights for viewing the protocol:
    SELECT  SUM (id) 
    INTO    l_rightsProt
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('viewProtocol');

    -- get read/write rights:
    SET l_rightsReadWrite = IBSDEV1.b_OR (l_rightsRead, l_rightsWrite);

    -- get read/write/rights rights:
    SET l_rightsReadWriteRights =
        IBSDEV1.b_OR (l_rightsReadWrite, l_rightsRights);

    -- get all rights and no rights:
    SET l_rightsReadWriteRightsProt =
        IBSDEV1.b_OR (l_rightsReadWriteRights, l_rightsProt);
    SET l_rightsNone = 0;

    -- get all rights and no rights:
    SELECT  SUM (id)
    INTO    l_rightsAll
    FROM    IBSDEV1.ibs_Operation;

    -- set common rights for all users:
    SET l_rightsStandard = l_rightsRead;

    IF (l_sqlcode <> 0)                 -- any exception occurred?
    THEN
        -- log the error:
        SET l_ePos = 'Error when getting rights.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception occurred


--*****************************************************************************
--** Create public structures within a domain                                **
--*****************************************************************************

    SET l_sqlcode = 0;

    -- get public container:
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domPublic', l_name, l_desc);

    SELECT  oid
    INTO    l_public
    FROM    IBSDEV1.ibs_Object
    WHERE   containerId = l_domainOid
        AND name = l_name;

    CALL IBSDEV1.p_byteToString (l_public, l_public_s);

--########## changeable code ... ##############################################
/*
    -- delete rights of group of all users on public container:
    DELETE  FROM ibs_Rights
    WHERE   rOid =;l_public
        AND rPersonId =;l_grpAll
    -- set rights of GRP1 group on public container:
    CALL IBSDEV1.p_Rights$setRights (l_public, l_grpGRP1, l_rightsStandard, 0);
*/

/*
    -- disable rights of group of GRP1 users on actual container:
    CALL IBSDEV1.p_Rights$setRights (l_oid, l_grpGRP1, 0, 0);
*/
/*
    -- set rights of GRP2 group on actual container:
    EXEC p_stringToByte;l_cid_s,;l_oid OUTPUT
    CALL IBSDEV1.p_Rights$setRights (l_oid, l_grpGRP2, l_rightsStandard, 0);
*/


/*
    EXEC p_ObjectDesc_01$get;c_languageId, 'OD_<tag name>',;l_name OUTPUT,;l_desc OUTPUT
    EXEC p_<object type>$create;l_userId, 0, <tVersionId>,
        ;l_name,;l_<super container>, 1, 0,;c_NOOID_s,;l_desc,;l_<out variable>_s OUTPUT
*/

    -- Informationen:
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domInformation', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domInfoCommon', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domInfoPress', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domInfoArticles', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domInfoForms', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domInfoSideGlances', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domInfoTrendInfo', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- Projekte:
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domProjects', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_domProjM2', l_name, l_desc);

    CALL IBSDEV1.p_Object$create (l_userId, 0, c_TVContainer, l_name,
        l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- set rights of all group on public container:
    CALL IBSDEV1.p_Rights$setRights (l_public, l_grpAll, l_rightsStandard, 0);

/*
    -- set rights of GRP2 group on public container which shall not be left
    -- to subsequent objects:
    CALL IBSDEV1.p_Rights$setRights (l_public, l_grpGRP2, l_rightsStandard, 0);
*/
--########## ... changeable code ##############################################


    SET l_msg = 'Domain structure created.';
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'public domain structures', '', 0, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_debug (l_msg);

    IF (l_sqlcode <> 0)                 -- any exception occurred?
    THEN
        -- log the error:
        SET l_ePos = 'Error when creating public structures.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception occurred


--*****************************************************************************
--** Create basic users                                                      **
--*****************************************************************************

    SET l_sqlcode = 0;

    -- create users:
    --
    -- CALL IBSDEV1.p_User_01$createFast (uid, domainId, username, password,
    --      fullname, l_userOid);
--########## changeable code ... ##############################################
    CALL IBSDEV1.p_User_01$createFast (l_userId, l_domainId, 'Debug',
        'isEnc_ZHQ%3D%0A', 'Debug', l_userOid);
--########## ... changeable code ##############################################

    SET l_msg = 'Users created.';
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'basic users', '', 0, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_debug (l_msg);

    IF (l_sqlcode <> 0)                 -- any exception occurred?
    THEN
        -- log the error:
        SET l_ePos = 'Error when creating basic users.';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception occurred

    -- cumulate the rights:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Rights$updateRightsCum;

    SET l_msg = 'Rights cumulated.';
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'basic users', '', 0, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_debug (l_msg);


FINISH:
    -- show state message:
    SET l_msg = l_file || ': finished.';
CALL IBSDEV1.logError (100, 'cBDomain', l_sqlcode, 'finish', '', 0, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_debug (l_msg);

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- terminate procedure:
    RETURN 0;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, l_file, l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'l_root_s', l_root_s,
        'l_domainId', l_domainId, 'l_domainOid_s', l_domainOid_s,
        'l_domainScheme', l_domainScheme, 'l_domainName', l_domainName,
        'l_domainAdmin', l_domainAdmin, 'l_homepagePath', l_homepagePath,
        'l_userId', l_userId, 'l_public_s', l_public_s,
        'l_grpAll', l_grpAll, 'l_cid_s', l_cid_s,
        'l_grpAdmin', l_grpAdmin, 'l_oid_s', l_oid_s,
        '', 0, 'l_groupContainer_s', l_groupContainer_s,
        '', 0, '', '',
        '', 0, '', '');

    -- show state message:
    SET l_msg = l_file || ': finished with exception.';
CALL IBSDEV1.logError (300, 'cBDomain', l_sqlcode, 'finish', '', 0, 'l_msg', l_msg, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_debug (l_msg);
END;
-- pi_createBaseDomain

-- execute procedure:
CALL IBSDEV1.pi_createBaseDomain;
-- delete procedure:
CALL IBSDEV1.p_dropProc ('pi_createBaseDomain');
