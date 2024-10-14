--------------------------------------------------------------------------------
-- The triggers for the ibs group table. <BR>
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------
CALL IBSDEV1.p_DropTrig('TrigGroupInsert');
--------------------------------------------------------------------------------
-- create the trigger:
CREATE TRIGGER IBSDEV1.TrigGroupInsert
AFTER INSERT ON IBSDEV1.ibs_Group
REFERENCING NEW ROW AS NRow
FOR EACH ROW
MODE DB2ROW
WHEN( NRow.OID = x'0000000000000000' )
TRGR : BEGIN ATOMIC
    UPDATE IBSDEV1.ibs_Group
    SET OID = IBSDEV1.p_intToBinary( 16842929 ) ||
        IBSDEV1.p_intToBinary( NRow.ID )
    WHERE ID = NRow.ID;
LEAVE TRGR;
END;



