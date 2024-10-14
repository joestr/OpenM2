-------------------------------------------------------------------------------
-- The triggers for the ibs user table. <BR>
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
-------------------------------------------------------------------------------

CALL IBSDEV1.p_dropTrig ('TrigRightsKeysInsert');
--------------------------------------------------------------------------------
-- create the trigger:
CREATE TRIGGER IBSDEV1.TrigRightsKeysInsert
AFTER INSERT ON IBSDEV1.ibs_RightsKeys
REFERENCING NEW_TABLE AS inserted
FOR EACH STATEMENT
MODE DB2SQL
BEGIN ATOMIC
    UPDATE  IBSDEV1.ibs_RightsKeys
    SET     id =
            (
                SELECT  COALESCE (MAX (id), 0) + 1
                FROM    IBSDEV1.ibs_RightsKeys
                WHERE   id NOT IN
                        (
                            SELECT  id
                            FROM    inserted
                            WHERE   id <= 0
                        )
            )
    WHERE   id IN
            (
                SELECT  id
                FROM    inserted
                WHERE   id <= 0
            );
END;
-- TrigRightsKeysInsert
