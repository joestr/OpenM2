/******************************************************************************
 * All stored procedures regarding the domain table. <BR>
 *
 * @version     $Id: Domain_01Proc.sql,v 1.38 2003/10/20 02:03:09 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
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
CREATE OR REPLACE PROCEDURE p_Domain_01$setScheme
(
    -- common input parameters:
    ai_userId               INTEGER,
    ai_id                   INTEGER,
    ai_schemeId             INTEGER,
    ai_homepagePath         VARCHAR2
)
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
                                                -- no defined oid as string
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_languageId            CONSTANT INTEGER := 0; -- the current language (default)

    -- local variables:
    l_oid                   RAW (8) := c_NOOID; -- the actual oid
    l_oid_s                 VARCHAR2 (18);      -- actual oid as string
    l_retValue              INT := c_NOT_OK;    -- return value of a function
    l_name                  VARCHAR2 (63);  -- the actual name
    l_desc                  VARCHAR2 (255); -- the actual description
    l_oldDomainScheme       INT;                -- the old domain scheme
    l_id                    INT;                -- the id of the domain
    l_homepagePath          VARCHAR2 (255);     -- the homepage path of the domain
    l_publicr               RAW (8);
    l_public_s              VARCHAR2 (18);
    l_cid                   RAW (8);
    l_cid_s                 VARCHAR2 (18);
    l_allGroupId            INTEGER;
    l_admin                 INTEGER;
    l_adminGroup            INTEGER;
    l_userAdminGroup        INTEGER;
    l_structAdminGroup      INTEGER;
    l_localOp               INTEGER := 0;  -- operation for local operations
    l_dummy                 INTEGER;

-- body:
BEGIN
    -- set the domain scheme:
    UPDATE  ibs_Domain_01
    SET     (scheme, workspaceProc) =
                (
                    SELECT  id, workspaceProc
                    FROM    ibs_DomainScheme_01
                    WHERE   id = ai_schemeId
                ),
            homepagePath = ai_homepagePath
    WHERE   id = ai_id;

    -- get public container and group of all users:
    SELECT  publicOid,
            allGroupId,
            adminId,
            adminGroupId,
            userAdminGroupId,
            structAdminGroupId
    INTO    l_publicr,
            l_allGroupId,
            l_admin,
            l_adminGroup,
            l_userAdminGroup,
            l_structAdminGroup
    FROM    ibs_Domain_01
    WHERE   id = ai_id;
    p_byteToString (l_publicr, l_public_s);

    -- check if there is a catalog management to create for the domain:
    BEGIN
        SELECT  id        -- if it does not exist, exception is thrown
        INTO    l_dummy
        FROM    ibs_DomainScheme_01
        WHERE   id = ai_schemeId
          AND   hasCatalogManagement = 1;  -- the scheme specifies a catalog
                                            -- management?
        -- create catalog management:
        -- Katalogverwaltung:
        p_ObjectDesc_01$get (c_languageId, 'OD_domCatalogManagement', l_name, l_desc);
        l_dummy := p_Object$create (ai_userId, 1,  16842785, -- Container
                l_name, l_public_s, 1, 0, c_NOOID_s, l_desc,
                l_cid_s);
        p_stringToByte (l_cid_s, l_cid);

        -- set rights on catalog management:
        p_Rights$deleteObjectRights (l_cid);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_admin);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_adminGroup);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_userAdminGroup);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_structAdminGroup);

        -- create sub structures:
        -- Produktmarken
        p_ObjectDesc_01$get (c_languageId, 'OD_domCatProductBrands', l_name, l_desc);
        l_dummy := p_Object$create (ai_userId, 1,  16871937, --
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        p_stringToByte (l_oid_s, l_oid);
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Produktschlüsselkategorien
        p_ObjectDesc_01$get (c_languageId, 'OD_domCatProductKeyCategories', l_name, l_desc);
        l_dummy := p_Object$create (ai_userId, 1,  16867329, --
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        p_stringToByte (l_oid_s, l_oid);
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Produktschlüssel
        p_ObjectDesc_01$get (c_languageId, 'OD_domCatProductKeys', l_name, l_desc);
        l_dummy := p_Object$create (ai_userId, 1,  16866049, --
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        p_stringToByte (l_oid_s, l_oid);
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Warengruppenprofile
        p_ObjectDesc_01$get (c_languageId, 'OD_domCatProductGroupProfiles', l_name, l_desc);
        l_dummy := p_Object$create (ai_userId, 1,  16846081, -- ProductGroupContainer
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        p_stringToByte (l_oid_s, l_oid);
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Warenprofile
        p_ObjectDesc_01$get (c_languageId, 'OD_domCatProductProfiles', l_name, l_desc);
        l_dummy := p_Object$create (ai_userId, 1,  16870657, --
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        p_stringToByte (l_oid_s, l_oid);
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Zahlungsarten
        p_ObjectDesc_01$get (c_languageId, 'OD_paymentTypeContainer', l_name, l_desc);
        l_dummy := p_Object$create (ai_userId, 1, 16870673, --
                l_name , l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        p_stringToByte (l_oid_s, l_oid);
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;  -- scheme has no catalog management
        WHEN OTHERS THEN
            RAISE;
    END;-- if the scheme has a catalog management

    -- check if there is a data interchange component to create for the domain:
    BEGIN
        SELECT  id
        INTO    l_dummy
        FROM    ibs_DomainScheme_01
        WHERE   id = ai_schemeId
            AND hasDataInterchange = 1; -- the scheme specifies a
                                        -- data interchange component?
        -- create import/export management:
        -- Data Interchange:
        p_ObjectDesc_01$get (c_languageId, 'OD_domDataInterchange', l_name, l_desc);
        l_dummy := p_Object$create (l_admin, l_localOp,  16872449, -- Interchange Container
                l_name, l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
        p_stringToByte (l_cid_s, l_cid);

        -- set rights on import/export management:
        p_Rights$deleteObjectRights (l_cid);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_admin);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_adminGroup);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_userAdminGroup);
        p_Rights$propagateUserRights (l_publicr, l_cid, l_structAdminGroup);

        -- Import
        p_ObjectDesc_01$get (c_languageId, 'OD_domDIImport', l_name, l_desc);
        l_dummy := p_Object$create (l_admin, l_localOp,  16873729, -- Import Container
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);

        -- Export
        p_ObjectDesc_01$get (c_languageId, 'OD_domDIExport', l_name, l_desc);
        l_dummy := p_Object$create (l_admin, l_localOp,  16873985, -- Export Container
                l_name, l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;  -- scheme has no data interchange component
        WHEN OTHERS THEN
            RAISE;
    END;-- if the scheme has a data interchange component

COMMIT WORK;


EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Domain_01$setScheme',
            ', ai_userId = ' || ai_userId ||
            ', ai_id = ' || ai_id ||
            ', ai_schemeId = ' || ai_schemeId ||
            ', ai_homepagePath = ' || ai_homepagePath ||
            ', errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
END p_Domain_01$setScheme;
/

show errors;

exit;
