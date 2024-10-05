--------------------------------------------------------------------------------
-- The triggers for ibs_Object. <BR>
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
-- 2002.11.10 - Completely new code (ZK)
----------------------------------------------------------------------------------
CALL IBSDEV1.p_DropTrig('TrigObjectUpdate');
--------------------------------------------------------------------------------
-- create the trigger
CREATE TRIGGER IBSDEV1.TrigObjectUpdate 
BEFORE UPDATE ON IBSDEV1.ibs_Object 
REFERENCING NEW ROW AS NRow
            OLD ROW AS ORow  
FOR EACH ROW 
MODE DB2ROW
WHEN( NRow.name <> ORow.name 
      OR NRow.linkedObjectId <> ORow.linkedObjectId 
      OR NRow.owner <> ORow.owner 
      OR NRow.changer <> ORow.changer 
      OR NRow.validUntil <> ORow.validUntil 
      OR NRow.description <> ORow.description 
      OR NRow.icon <> ORow.icon )  
TRGR : BEGIN ATOMIC   
    SET NROW.lastChanged = CURRENT TIMESTAMP;     
    LEAVE TRGR;
END;
-- TrigObjectUpdate
