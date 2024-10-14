--------------------------------------------------------------------------------
 -- The triggers for the ibs domain scheme table. <BR>
 --
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020910
 ------------------------------------------------------------------------------
CALL IBSDEV1.p_DropTrig('TrigDomainScheme');
-- create the trigger
CREATE TRIGGER IBSDEV1.TrigDomainScheme
AFTER INSERT ON IBSDEV1.ibs_DomainScheme_01
REFERENCING NEW ROW AS NRow
FOR EACH ROW 
MODE DB2ROW
WHEN ( NRow.OID = X'0000000000000000' )
TRGR : BEGIN ATOMIC 
    UPDATE IBSDEV1.ibs_DomainScheme_01
    SET OID = IBSDEV1.p_intToBinary( 1 ) ||
        IBSDEV1.p_intToBinary( NRow.id )
    WHERE ID = NRow.ID;
    LEAVE TRGR;
END;
-- TrigDomainScheme_01Insert