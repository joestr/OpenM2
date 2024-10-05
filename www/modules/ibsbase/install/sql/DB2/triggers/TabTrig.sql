--------------------------------------------------------------------------------
-- The triggers for the ibs tab table. <BR>
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
-- 2002.11.10 - Completely new code (ZK)
----------------------------------------------------------------------------------
CALL IBSDEV1.p_DropTrig('TrigTabInsert');
--------------------------------------------------------------------------------
-- create the trigger
CREATE TRIGGER IBSDEV1.TrigTabInsert 
AFTER INSERT ON IBSDEV1.ibs_Tab 
REFERENCING NEW ROW AS NRow 
FOR EACH ROW  
MODE DB2ROW  
WHEN( NRow.multilangKey  = '' OR NRow.multilangKey IS NULL )
TRGR : BEGIN ATOMIC  
    UPDATE IBSDEV1.ibs_Tab
    SET multilangKey = 'TAB_'  || CAST(NRow.ID AS CHAR (10))
    WHERE ID = NRow.ID;
    LEAVE TRGR;
END;
-- TrigTabInsert