-------------------------------------------------------------------------------
-- Create some known schemes. <BR>
--
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:46 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pi_createSchemes');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pi_createSchemes ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the current scheme
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
CALL IBSDEV1.logError (100, 'cSchemes', l_sqlcode, 'start', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_Debug ('Creating domain schemes...'); 
  
    -- base scheme:
    CALL IBSDEV1.p_DomainScheme_01$new ('Standard', 'The standard scheme.', 
        'p_Workspace_01$createObjects', '', 1, 1, l_oid);
  
    -- scheme for Sport2000:
    CALL IBSDEV1.p_DomainScheme_01$new ('SP2000', 
        'Scheme specially created for Sport2000.', 
        'p_Workspace_01$createSport2000', 'Sport2000%', 1, 1, l_oid);
  
    -- scheme for Verbund:
    CALL IBSDEV1.p_DomainScheme_01$new ('Verbund', 
        'Scheme specially created for Verbund.', 
        'p_Workspace_01$createVerbund', 'TeleRing%', 0, 1, l_oid);
  
    -- scheme for NWR:
    CALL IBSDEV1.p_DomainScheme_01$new ('Central Network', 
        'Scheme specially created for Nord-West-Ring.', 
        'p_Workspace_01$createNWR', 'Central Network%', 0, 1, l_oid);
  
    -- scheme for Garant:
    CALL IBSDEV1.p_DomainScheme_01$new ('Easy',
        'Scheme specially created for Garant.', 
        'p_Workspace_01$createGarant', 'Garant%', 0, 1, l_oid);
  
    -- scheme for MCC:
    CALL IBSDEV1.p_DomainScheme_01$new ('MCC',
        'Scheme specially created for MCC.', 
        'p_Workspace_01$createMCC', 'MCC%', 0, 1, l_oid);
  
    -- scheme for K„rntenwerbung:
    CALL IBSDEV1.p_DomainScheme_01$new ('KW',
        'Scheme specially created for K„rntenwerbung.', 
        'p_Workspace_01$createObjects', 'KW%', 1, 1, l_oid);
  
    -- scheme for MRI2:
    CALL IBSDEV1.p_DomainScheme_01$new ('MRI2',
        'Scheme specially created for Modering.', 
    	'p_Workspace_01$createMRI2', 'MRI%', 1, 1, l_oid);
  
CALL IBSDEV1.logError (100, 'cSchemes', l_sqlcode, 'base scheme data created', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- ensure that each domain has a defined scheme:
    UPDATE  IBSDEV1.ibs_Domain_01
    SET     scheme =
            (
                SELECT  MIN (id) 
                FROM    IBSDEV1.ibs_DomainScheme_01
            )
    WHERE   scheme NOT IN
            (
                SELECT  id 
                FROM    IBSDEV1.ibs_DomainScheme_01
            );

    -- set correct workspace procedures of the domains:
    UPDATE  IBSDEV1.ibs_Domain_01
    SET     workspaceProc =
            (
                SELECT  DISTINCT s.workspaceProc 
                FROM    IBSDEV1.ibs_Domain_01 d, 
                        IBSDEV1.ibs_DomainScheme_01 s
                WHERE   d.scheme = s.id
            )
    WHERE   EXISTS
            (  
                SELECT  *  
                FROM    IBSDEV1.ibs_Domain_01 d, 
                        IBSDEV1.ibs_DomainScheme_01 s
                WHERE   d.scheme = s.id
            );

CALL IBSDEV1.logError (100, 'cSchemes', l_sqlcode, 'schemes created', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    CALL IBSDEV1.p_debug ('Schemes created.');
END;           
-- pi_createSchemes

-- execute procedure:
CALL IBSDEV1.pi_createSchemes;
-- delete procedure:
CALL IBSDEV1.p_dropProc ('pi_createSchemes');
