--------------------------------------------------------------------------------
-- All stored procedures regarding the domain table. <BR>
--
-- @version     $Id: Domain_01Proc.sql,v 1.6 2003/10/21 22:14:21 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020818
-------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Set the scheme of a domain. <BR>
-- This procedure also performs some operations which are corresponding to
-- the selected scheme, i.e. creating a catalog management.
--
-- @input parameters:
-- @param   ai_userId           Id of the user who is setting the scheme.
-- @param   ai_id               Id of the domain.
-- @param   ai_schemeId         Id of the domain scheme.
-- @param   ai_homepagePath     Homepage path of the domain, i.e. the path
--                              where it resides, e.g. '/m2/'.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Domain_01$setScheme');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Domain_01$setScheme
(
    -- common input parameters:
    IN  ai_userId           INT,
    IN  ai_id               INT,
    IN  ai_schemeId         INT,
    IN  ai_homepagePath     VARCHAR (63)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_languageId    INT DEFAULT 0;  -- the current language
    
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
    DECLARE l_oid_s         VARCHAR (18);    -- the actual oid as string
    DECLARE l_cid           CHAR (8) FOR BIT DATA; -- the actual containerId
    DECLARE l_cid_s         VARCHAR (18);    -- the actual containerId as string
    DECLARE l_name          VARCHAR (63);    -- the actual name
    DECLARE l_desc          VARCHAR (255);   -- the actual description
    DECLARE l_public        CHAR (8) FOR BIT DATA;
    DECLARE l_public_s      VARCHAR (18);
    DECLARE l_allGroupId    INT;
    DECLARE l_admin         INT;
    DECLARE l_adminGroup    INT;
    DECLARE l_userAdminGroup INT;
    DECLARE l_structAdminGroup INT;
    DECLARE l_localOp       INT DEFAULT 0;  -- operation for local operations
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;
    SET c_languageId        = 0;
  
    -- initialize local variables:
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;
    SET l_oid_s             = c_NOOID_s;
    SET l_cid               = c_NOOID;
    SET l_cid_s             = c_NOOID_s;
    SET l_public            = c_NOOID;
    SET l_public_s          = c_NOOID_s;

-- body:
CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'start', 'l_retValue', l_retValue, '', '', 'ai_userId', ai_userId, '', '', 'ai_id', ai_id, '', '', 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- set the domain scheme:
    UPDATE  IBSDEV1.ibs_Domain_01
    SET     (scheme, workspaceProc, homepagePath) =
            (
                SELECT  DISTINCT s.id, s.workspaceProc, ai_homepagePath
                FROM    IBSDEV1.ibs_Domain_01 d, IBSDEV1.ibs_DomainScheme_01 s
                WHERE   s.id = ai_schemeId
                    AND d.id = ai_id
            );

-- WHERE EXISTS
--       (SELECT  *  
--        FROM IBSDEV1.ibs_Domain_01, IBSDEV1.ibs_DomainScheme_01
--        WHERE s.id = (Untranslated expression) AND
--              d.id = (Untranslated expression));
  
    -- get public container and group of all users:
    SELECT  publicOid, allGroupId, adminId, adminGroupId,
            userAdminGroupId, structAdminGroupId
    INTO    l_public, l_allGroupId, l_admin, l_adminGroup,
            l_userAdminGroup, l_structAdminGroup
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   id = ai_id;

    CALL IBSDEV1.p_byteToString (l_public, l_public_s);
  
    -- check if there is a catalog management to create for the domain:
    IF EXISTS   (
                    SELECT *
                    FROM IBSDEV1.ibs_DomainScheme_01
                    WHERE   id = ai_schemeId
                    AND hasCatalogManagement = 1
                )
    THEN 
        -- create catalog management:
        -- Katalogverwaltung:
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_domCatalogManagement', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (ai_userId, 1, 16842785, l_name,
            l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);

        CALL IBSDEV1.p_stringToByte (l_cid_s, l_cid);

        -- set rights on catalog management:
        CALL IBSDEV1.p_Rights$deleteObjectRights(l_cid);
        CALL IBSDEV1.p_Rights$propagateUserRights(l_public, l_cid, l_admin);
        CALL IBSDEV1.p_Rights$propagateUserRights(l_public, l_cid, l_adminGroup);
        CALL IBSDEV1.p_Rights$propagateUserRights(l_public, l_cid, l_userAdminGroup);
        CALL IBSDEV1.p_Rights$propagateUserRights(l_public, l_cid, l_structAdminGroup);

        -- create sub structures:
        -- Produktmarken
        CALL IBSDEV1.p_ObjectDesc_01$get(c_languageId, 'OD_domCatProductBrands', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (ai_userId, 1, 16871937, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Produktschlüsselkategorien
        CALL IBSDEV1.p_ObjectDesc_01$get(c_languageId, 'OD_domCatProductKeyCategories', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (ai_userId, 1, 16867329, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Produktschlüssel
        CALL IBSDEV1.p_ObjectDesc_01$get(c_languageId, 'OD_domCatProductKeys', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (ai_userId, 1, 16866049, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Warengruppenprofile
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_domCatProductGroupProfiles', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (ai_userId, 1, 16846081, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Warenprofile
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_domCatProductProfiles', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (ai_userId, 1, 16870657, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;

        -- Zahlungsarten
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_paymentTypeContainer', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (ai_userId, 0, 16870673, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oid;
    END IF;

--CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'middle', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- check if there is a data interchange component to create for the domain:
    IF EXISTS   (   SELECT  *
                    FROM    IBSDEV1.ibs_DomainScheme_01
                    WHERE   id = ai_schemeId
                        AND hasDataInterchange = 1
                )
    THEN 
--CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'create data interchange', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        -- create import/export management:
        -- Data Interchange:
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_domDataInterchange', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (l_admin, l_localOp, 16872449, l_name,
            l_public_s, 1, 0, c_NOOID_s, l_desc, l_cid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        CALL IBSDEV1.p_stringToByte (l_cid_s, l_cid);

        -- set rights on import/export management:
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_cid);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_admin);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_adminGroup);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_userAdminGroup);
        CALL IBSDEV1.p_Rights$propagateUserRights (l_public, l_cid, l_structAdminGroup);
        -- Import
        CALL IBSDEV1.p_ObjectDesc_01$get(c_languageId, 'OD_domDIImport', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (l_admin, l_localOp, 16873729, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- Export
        CALL IBSDEV1.p_ObjectDesc_01$get (c_languageId, 'OD_domDIExport', l_name, l_desc);
        CALL IBSDEV1.p_Object$create (l_admin, l_localOp, 16873985, l_name,
            l_cid_s, 1, 0, c_NOOID_s, l_desc, l_oid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
/*
    ELSE
CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'no data interchange', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
*/
    END IF;
CALL IBSDEV1.logError (100, 'p_Domain_01$setScheme', l_sqlcode, 'end', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'ai_userId', ai_userId, 'l_cid_s', l_cid_s, 'ai_id', ai_id, 'l_public_s', l_public_s, 'ai_schemeId', ai_schemeId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
END;
-- p_Domain_01$setScheme
