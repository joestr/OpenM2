/******************************************************************************
 * All base data within the framework. <BR>
 *
 * @version     $Id: createBaseData.sql,v 1.24 2005/08/11 14:20:27 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR)  990323
 ******************************************************************************
 */


--*****************************************************************************
--** Declarations                                                            **
--*****************************************************************************
DECLARE
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything's all right
    c_MAXValidUntil         CONSTANT DATE :=
                                TO_DATE ('#CONFVAR.ibsbase.validUntilSql#',
                                         'DD.MM.YYYY');
    c_TVRoot                CONSTANT INTEGER := 16864001; -- 0x01015301
    c_TVUser                CONSTANT INTEGER := 16842913; -- 0x010100A1
                                            -- tVersionId of User
    c_TVUserProfile         CONSTANT INTEGER := 16857089; -- 0x01013801
    c_TVReference           CONSTANT INTEGER := 16842801; -- 0x01010031
    c_TVDI                  CONSTANT INTEGER := 16872449; -- 0x01017401
                                            -- tVersionId for Data Interchange
    c_TVImportContainer     CONSTANT INTEGER := 16873729; -- 0x1017901
                                            -- tVersionId of import container
    c_TVExportContainer     CONSTANT INTEGER := 16873985; -- 0x1017A01
                                            -- tVersionId of export container
    c_TVLayoutContainer     CONSTANT INTEGER := 16871169; -- 0x1016F01
                                            -- tVersionId of layout container
    c_TVLayout              CONSTANT INTEGER := 16871425; -- 0x1017001
                                            -- tVersionId of layout
    c_TVDomainSchemeContainer CONSTANT INTEGER := 16843025; -- 0x01010111
                                            -- tVersionId of
                                            -- domain scheme container
    c_TVDomainScheme        CONSTANT INTEGER := 16843041; -- 0x01010121
                                            -- tVersionId of domain scheme
    c_NOOID                 CONSTANT RAW (8) := hextoraw ('0000000000000000');
                                            -- default value for no defined oid
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
                                            -- no oid as string
    c_languageId            CONSTANT INTEGER := 0; -- the current language

    -- local variables:
    l_file                  VARCHAR2 (20) := 'createBaseData';
                                            -- name of actual file
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rightsAdmin           INTEGER;        -- rights of administrator
    l_rightsProfile         INTEGER;        -- rights on profile
    l_rightsRootRef         INTEGER;        -- rights on reference to root
    l_retVal                INTEGER;        -- return value of function
    l_tVersionId            INTEGER;        -- tVersionId
    l_profile               RAW (8);        -- oid of user profile
    l_profile_s             VARCHAR2 (18);  -- oid of user profile as string
    l_cid                   RAW (8);        -- oid of actual container
    l_cid_s                 VARCHAR2 (18);  -- oid of act. container as string
    l_oid                   RAW (8);        -- oid of actual business object
    l_oid_s                 VARCHAR2 (18);  -- oid of actual BO as string
    l_name                  VARCHAR2 (63);  -- name of business object
    l_desc                  VARCHAR2 (255); -- description of business object
    l_rights                INTEGER;        -- the current rights
    l_admin                 INTEGER;        -- id of system administrator
    l_adminOid              RAW (8);        -- oid of system administrator
    l_adminOid_s            VARCHAR2 (18);  -- string representation of oid
    l_root                  RAW (8);        -- oid of system root
    l_root_s                VARCHAR2 (18);  -- oid of system root as string
    l_rootRef               RAW (8);        -- oid of root reference
    l_rootRef_s             VARCHAR2 (18);  -- oid of root reference as string
    l_posNoPath             RAW (254);      -- current hierarchy path
    l_cnt                   INTEGER;        -- counter
    l_adminAlreadyExists    NUMBER (1) := 0; -- state value
    l_rootAlreadyExists     NUMBER (1) := 0; -- state value
    l_menutabOid_s          VARCHAR2 (18);   -- oid of menutab
    l_menutabOid            RAW (8);

    -- exceptions:
    e_exception             EXCEPTION;      -- common exception

-- body:
BEGIN

--*****************************************************************************
--** Create system administrator                                             **
--*****************************************************************************

    -- create administrator:
    p_ObjectDesc_01$get (c_languageId, 'OD_sysAdmin', l_name, l_desc);
    l_retVal := p_User_01$new (0, 1, l_name, 'isEnc_bnRpSG1hZCBBcA%3D%3D%0A',
        'System Administrator', null, null, l_admin);

    -- set admin flag:
    UPDATE  ibs_User
    SET     admin = 1
    WHERE   id = l_admin;

    -- store the id of the administrator:
    p_System$new ('ID_sysAdmin', 'INTEGER', '' || l_admin);

    -- get rights for administrator on root:
    SELECT  SUM (id)
    INTO    l_rightsAdmin
    FROM    ibs_Operation
    WHERE   name IN ('new', 'view', 'read', 'viewElems', 'addElem', 'delElem');

    -- get rights for administrator on his/her own profile:
    SELECT  SUM (id)
    INTO    l_rightsProfile
    FROM    ibs_Operation
    WHERE   name IN ('view', 'read', 'viewElems');

    -- get rights for administrator on reference to root:
    SELECT  SUM (id)
    INTO    l_rightsRootRef
    FROM    ibs_Operation
    WHERE   name IN ('view', 'read', 'viewElems');

    -- check if there was an error:
    IF (l_retVal = 1)                   -- no error during creation
    THEN
        debug ('System Administrator created.');
    ELSIF (l_retVal = 21)               -- administrator already exists
    THEN
        debug ('System Administrator already exists.');
        l_adminAlreadyExists := 1;
    ELSE                                -- error during creation
        -- log the error:
        l_ePos := 'Error during creation of System Administrator:' ||
                  ' retVal = ' || l_retVal || '.';
        RAISE e_exception;              -- call common exception handler
    END IF; -- else error during creation


--*****************************************************************************
--** Create root of system                                                   **
--*****************************************************************************

    -- procedure p_Object$create
    -- userId, op, tVersionId,
    -- name, containerId_s, containerKind, isLink, linkedObjectId_s,
    -- description, oid_s OUTPUT

    l_retVal := 21;
    l_cnt := 0;

    -- Root of the system:
    BEGIN
        SELECT  DECODE (MIN (oid), null, c_NOOID, MIN (oid))
        INTO    l_root
        FROM    ibs_Object
        WHERE   containerId = c_NOOID;

        IF (l_root <> c_NOOID) THEN
            -- convert oid of root to string representation:
            p_byteToString (l_root, l_root_s);
            l_rootAlreadyExists := 1;
        ELSE                                -- root does not exist yet
            -- create the root:
            p_ObjectDesc_01$get (c_languageId, 'OD_sysRoot', l_name, l_desc);

            l_retVal := p_Object$create (l_admin, 0, c_TVRoot,
                    l_name, c_NOOID_s, 1, 0, c_NOOID_s, l_desc, l_root_s);

            -- convert string representation of oid to byte:
            p_stringToByte (l_root_s, l_root);

            -- set valid until date of root and all yet created objects to
            -- nearly infinite:
            SELECT  posNoPath
            INTO    l_posNoPath
            FROM    ibs_Object
            WHERE   oid = l_root;

            UPDATE  ibs_Object
            SET     validUntil = c_MAXValidUntil
            WHERE   INSTR (posNoPath, l_posNoPath, 1, 1) = 1;
        END IF; -- else root does not exist yet

    EXCEPTION
        WHEN OTHERS THEN
            -- log the error:
            l_ePos := 'Error while creating root.';
            RAISE;                      -- call common exception handler
    END;

    -- check if there was an error:
    IF (l_retVal = 1)                   -- no error during creation
    THEN
        debug ('System Root created.');
    ELSIF (l_retVal = 21)               -- root already exists
    THEN
        debug ('System Root already exists.');
        l_rootAlreadyExists := 1;
    ELSE                                -- error during creation
        -- log the error:
        l_ePos := 'Error during creation of System Root:' ||
                  ' retVal = ' || l_retVal || '.';
        RAISE e_exception;              -- call common exception handler
    END IF; -- else error during creation

    -- delete all existing rights on the root:
    p_Rights$deleteObjectRights (l_root);

    -- set administrator rights on root:
    p_Rights$setRights (l_root, l_admin, l_rightsAdmin, 1);

    -- get data of eventually already existing user profile:
    BEGIN
        SELECT  oid
        INTO    l_profile
        FROM    ibs_UserProfile
        WHERE   userId = l_admin;

        p_byteToString (l_profile, l_profile_s);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN             -- user profile does not exist yet?
            -- create user profile of administrator:
            p_ObjectDesc_01$get (c_languageId, 'OD_sysAdminUserprofile',
                l_name, l_desc);
            l_retVal :=
                p_UserProfile_01$create (l_admin, 0, l_admin, c_TVUserProfile,
                    l_name, l_root_s, 1, 0, c_NOOID_s, l_desc, l_profile_s);
            p_stringToByte (l_profile_s, l_profile);

            -- set rights of administrator on his/her own user profile:
            p_Rights$setRights (l_profile, l_admin, l_rightsProfile, 1);

            -- store workspace of administrator:
            INSERT  INTO ibs_Workspace
                    (userId, domainId, workspace, profile, publicWsp)
            VALUES  (l_admin, 0, l_root, l_profile, l_root);
        -- end when user profile does not exist yet
    END;

    -- check if there was an error:
    IF (l_retVal = 1)                       -- no error during creation
    THEN
        debug ('Profile for system administrator created.');
    ELSIF (l_retVal = 21)                   -- root already exists
    THEN
        debug ('Profile for system administrator already exists.');
    ELSE                                    -- error during creation
        debug ('Error during creation of profile for system administrator:' ||
               ' ' || l_retVal || '.');
    END IF; -- else error during creation


    -- check if there is already a reference to the root:
    BEGIN
        SELECT  MIN (oid)
        INTO    l_rootRef
        FROM    ibs_Object
        WHERE   containerId = l_root
            AND tVersionId = c_TVReference
            AND linkedObjectId = l_root;

        IF (l_rootRef IS NULL)          -- object not found?
        THEN
            -- log the error:
            l_ePos := 'Could not find reference to root;' ||
                      ' root = ' || l_root || '.';
            RAISE NO_DATA_FOUND;        -- call common exception handler
        END IF; -- object not found

        p_byteToString (l_rootRef, l_rootRef_s);

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- reference does not exists yet?
            -- create reference to root used for administrative purposes:
            p_ObjectDesc_01$get (c_languageId, 'OD_sysRootRef',
                l_name, l_desc);
            l_retVal := p_Object$create (l_admin, 0, c_TVReference,
                    l_name, l_root_s, 1, 1, l_root_s, l_desc, l_rootRef_s);
            p_stringToByte (l_rootRef_s, l_rootRef);
            -- ensure that the reference is shown in the menu:
            UPDATE  ibs_Object
            SET     showInMenu = 1
            WHERE   oid = l_rootRef;
        -- end when reference does not exists yet
    END;

    -- set rights of administrator on reference to root:
    p_Rights$setRights (l_rootRef, l_admin, l_rightsRootRef, 1);


--*****************************************************************************
--** Create base object for system administrator                             **
--*****************************************************************************

    -- create base object for system administrator:
    p_ObjectDesc_01$get (c_languageId, 'OD_sysAdmin', l_name, l_desc);
    l_retVal := p_Object$performCreate (l_admin, 1, c_TVUser,
        l_name, l_root_s, 1, 0, c_NOOID_s, l_desc,
        l_adminOid_s, l_adminOid);

    IF (l_retVal = c_ALL_RIGHT)         -- object successfully created?
    THEN
        -- set the new oid for the system administrator:
        BEGIN
            UPDATE  ibs_User
            SET     oid = l_adminOid
            WHERE   id = l_admin;
        EXCEPTION
            WHEN OTHERS THEN
                -- log the error:
                l_ePos := 'Error when updating admin user ' || l_admin || '.';
                RAISE;                  -- call common exception handler
        END;
        debug ('cBData: Creation of BusinessObject for system administrator' ||
               ' finished.');
    ELSE                                -- error during object creation?
        -- log the error:
        l_ePos := 'BusinessObject for admin could not be created;' ||
                  ' admin = ' || l_admin || '.';
        RAISE e_exception;              -- call common exception handler
    END IF; -- else error during object creation?


--*****************************************************************************
--** Create MenuTab for System Adminstrator                                  **
--*****************************************************************************

    -- create MenuTab for System Adminstrator because the System Administrator
    -- is in domain 0, which not realy exists
    -- sets the tab for the System Administrator

    SELECT  actVersion INTO l_tVersionId
    FROM    ibs_Type WHERE code = 'MenuTab';

    p_ObjectDesc_01$get (c_languageId, 'OD_sysRoot', l_name, l_desc);
    l_retVal := p_MenuTab_01$create (l_admin, 1, l_tVersionId,
        l_name, l_root_s, 1, 0, c_NOOID_s, l_desc,
        l_menutabOid_s);
    p_stringToByte (l_menutabOid_s, l_menutabOid);

    -- update:
    UPDATE ibs_MenuTab_01
    SET      objectOid = l_root,
             description = l_name,
             priorityKey = 1,
             isPrivate = 0,
             domainId = 0,
             classFront = 'systemFront',
             classBack = 'systemBack',
             fileName = 'system.htm'
    WHERE oid = l_menutabOid;

--*****************************************************************************
--** Create Data Interchange on system root                                  **
--*****************************************************************************

    -- create import/export management:
    -- Data Interchange:
    p_ObjectDesc_01$get (c_languageId, 'OD_sysDataInterchange',
        l_name, l_desc);
    l_retVal := p_Object$create (l_admin, 1, c_TVDI,
            l_name, l_root_s, 1, 0, c_NOOID_s, l_desc,
            l_cid_s);

    IF (l_retVal = c_ALL_RIGHT)     -- data interchange created correctly?
    THEN
        -- convert string to oid representation:
        p_stringToByte (l_cid_s, l_cid);
        -- set rights on import/export management:
        p_Rights$propagateUserRights (l_root, l_cid, l_admin);

        -- Import
        p_ObjectDesc_01$get (c_languageId, 'OD_sysDIImport',
            l_name, l_desc);
        l_retVal := p_Object$create (l_admin, 1, c_TVImportContainer,
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

        IF (l_retVal = c_ALL_RIGHT) -- import container created?
        THEN
            -- Export
            p_ObjectDesc_01$get (c_languageId, 'OD_sysDIExport',
                l_name, l_desc);
            l_retVal := p_Object$create (l_admin, 1, c_TVExportContainer,
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

            IF (l_retVal = c_ALL_RIGHT) -- export container created?
            THEN
                debug ('cBData: Data Interchange on system level correctly' ||
                       ' defined.');
            -- if export container created
            ELSE                        -- export container not created
                debug ('cBData: Error when creating export container:' ||
                       ' retVal = ' || l_retVal || '.');
            END IF; -- else export container not created
        ELSE                            -- import container not created
            debug ('cBData: Error when creating import container:' ||
                   ' retVal = ' || l_retVal || '.');
        END IF; -- else import container not created
    ELSE
        -- log the error:
        l_ePos := 'Error when creating data interchange:' ||
            ' retVal = ' || l_retVal || '.';
        RAISE e_exception;              -- call common exception handler
    END IF; -- if data interchange created correctly


--*****************************************************************************
--** Create Layout Management on system root                                 **
--*****************************************************************************

    -- create layout container:
    p_ObjectDesc_01$get (c_languageId, 'OD_sysLayouts', l_name, l_desc);
    l_retVal := p_Object$create (l_admin, 1, c_TVLayoutContainer,
        l_name, l_root_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);

    IF (l_retVal = c_ALL_RIGHT)         -- layout management created correctly?
    THEN
        -- set rights on layout container:
        p_stringToByte (l_cid_s, l_cid);

        -- set rights:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'viewRights',
                    'new', 'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_cid, l_admin, l_rights, 1);

        -- create layout "Standard" for the root within the layout container:
        p_ObjectDesc_01$get (c_languageId, 'OD_sysLayoutStandard',
            l_name, l_desc);
        l_retVal := p_Layout_01$create (l_admin, 1, c_TVLayout,
            'Standard', l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

        IF (l_retVal = c_ALL_RIGHT)     -- layout created?
        THEN
            p_stringToByte (l_oid_s, l_oid);

            -- set rights on layout:
            SELECT  SUM (id)
            INTO    l_rights
            FROM    ibs_Operation;
            p_Rights$setRights (l_oid, l_admin, l_rights, 1);

            -- set layout for system administrator:
            BEGIN
                UPDATE  ibs_UserProfile
                SET     layoutId = l_oid
                WHERE   userId = l_admin;
            EXCEPTION
                WHEN OTHERS THEN
                    -- log the error:
                    l_ePos := 'Error when updating user ' || l_admin ||
                        ' with layout ' || l_oid || '.';
                    RAISE;              -- call common exception handler
            END;

            debug ('cBData: Layout Management on system level correctly' ||
                   ' defined.');
        -- if layout created
        ELSE                            -- layout not created
            -- log the error:
            l_ePos := 'Error when creating Layout Standard:' ||
                ' retVal = ' || l_retVal || '.';
            RAISE e_exception;          -- call common exception handler
        END IF; -- else layout not created
    -- if layout management created correctly
    ELSE                            -- layout management not created correctly
        -- log the error:
        l_ePos := 'Error when creating Layout Management:' ||
           ' retVal = ' || l_retVal || '.';
        RAISE e_exception;          -- call common exception handler
    END IF; -- else layout management not created correctly


--*****************************************************************************
--** Create Domain Scheme Management on system root                          **
--*****************************************************************************

    -- create domain scheme container:
    p_ObjectDesc_01$get (c_languageId, 'OD_sysDomainSchemes',
        l_name, l_desc);
    l_retVal := p_Object$create (l_admin, 1, c_TVDomainSchemeContainer,
            l_name, l_root_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);

    IF (l_retVal = c_ALL_RIGHT)         -- domain scheme management created
                                        -- correctly?
    THEN
        -- set rights on domain scheme container:
        p_stringToByte (l_cid_s, l_cid);

        -- set rights:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'viewRights',
                    'new', 'addElem', 'delElem', 'viewElems', 'viewProtocol');
        p_Rights$setRights (l_cid, l_admin, l_rights, 1);

        debug ('cBData: Domain Scheme Management correctly defined.');
    -- if domain scheme management created correctly
    ELSE                                -- domain scheme management not created
                                        -- correctly
        -- log the error:
        l_ePos := 'Error when creating Domain Scheme Management:' ||
            ' retVal = ' || l_retVal || '.';
        RAISE e_exception;              -- call common exception handler
    END IF; -- else domain scheme management not created correctly


--*****************************************************************************
--** Cumulate the rights                                                     **
--*****************************************************************************

    -- ensure that the rights are correctly cumulated:
    p_Rights$updateRightsCum ();
    debug ('cBData: Rights cumulation finished.');

--*****************************************************************************
--** Cumulate the rights                                                     **
--*****************************************************************************

    -- create rights entries: rights are kind of hierarchical
    INSERT INTO  ibs_RightsMapping VALUES  ('READ', 'READ');
    INSERT INTO  ibs_RightsMapping VALUES  ('READ', 'VIEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('READ', 'VIEWELEMS');

    INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'READ');
    INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'VIEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'VIEWELEMS');
    INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'NEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'ADDELEM');

    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'READ');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'VIEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'VIEWELEMS');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'CREATELINK');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'DISTRIBUTE');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'NEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'ADDELEM');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'CHANGE');

    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'READ');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'VIEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'VIEWELEMS');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'CREATELINK');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DISTRIBUTE');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'NEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'ADDELEM');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'CHANGE');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DELETE');
    INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DELELEM');

    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'READ');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWELEMS');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'CREATELINK');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DISTRIBUTE');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'NEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'ADDELEM');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'CHANGE');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DELETE');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DELELEM');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWRIGHTS');
--    no setrights allowed for workflow-users
--    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'SETRIGHTS');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWPROTOCOL');

    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'READ');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEWELEMS');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'CREATELINK');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'DISTRIBUTE');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'NEW');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'ADDELEM');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'CHANGE');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'DELETE');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'DELELEM');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEWRIGHTS');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'SETRIGHTS');
    INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEWPROTOCOL');


    -- show state message:
    debug (l_file || ': finished');

EXCEPTION
    WHEN OTHERS THEN
        err;
        -- create error entry:
        l_eText := l_file || ': ' || l_ePos ||
            '; l_retVal = ' || l_retVal ||
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
