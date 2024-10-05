--------------------------------------------------------------------------------
-- The triggers for the ibs domain table. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------
CALL IBSDEV1.p_DropTrig('TrigDomain_01Ins');
 -- create the trigger
CREATE TRIGGER IBSDEV1.TrigDomain_01Ins
AFTER INSERT ON IBSDEV1.ibs_Domain_01
REFERENCING NEW ROW AS NRow
FOR EACH ROW 
MODE DB2ROW 
WHEN ( NRow.OID = x'0000000000000000' ) 
TRGR : BEGIN ATOMIC 
    UPDATE IBSDEV1.ibs_Domain_01
    SET OID = IBSDEV1.p_intToBinary( 16842993 ) ||
        IBSDEV1.p_intToBinary( NRow.id )
    WHERE id = NRow.id;
    LEAVE TRGR;
END;
----------------------------------------------------------------------------------
CALL IBSDEV1.p_DropTrig('TrigDomain_01Ins1');
 -- create the trigger
CREATE TRIGGER IBSDEV1.TrigDomain_01Ins1
BEFORE INSERT ON IBSDEV1.ibs_Domain_01
REFERENCING NEW ROW AS NRow
FOR EACH ROW 
MODE DB2ROW 
WHEN ( NRow.workspaceProc IS NULL ) 
TRGR : BEGIN ATOMIC 
    SET NRow.workspaceProc = 'p_Workspace_01$createObjects';
    LEAVE TRGR;
END;
