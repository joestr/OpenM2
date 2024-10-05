--------------------------------------------------------------------------------
-- The triggers for the ibs consistsOf table. <BR>
--
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
-- 2002.11.10 - Completely new code  (ZK)
----------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- INSERT trigger for ibs_ConsistsOf
-- delete old trigger
CALL IBSDEV1.p_DropTrig('TrigConsistsOfInse');
-- create the trigger:
CREATE TRIGGER IBSDEV1.TrigConsistsOfInse
AFTER INSERT ON IBSDEV1.ibs_ConsistsOf
REFERENCING NEW ROW AS NRow
FOR EACH ROW
MODE DB2ROW
WHEN ( NRow.OID = X'0000000000000000' )
TRGR : BEGIN ATOMIC
    UPDATE IBSDEV1.ibs_ConsistsOf
    SET OID = IBSDEV1.p_intToBinary( 16851729 )  ||
        IBSDEV1.p_intToBinary( NRow.id )
    WHERE id = NRow.id;
    LEAVE TRGR;
END;