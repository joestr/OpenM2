/******************************************************************************
 * The triggers for the ibs rights keys table. <BR>
 *
 * @version     $Id: U24006q_RightsKeysTrig.sql,v 1.1 2005/02/15 21:38:48 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  990304
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig 'TrigRightsKeysInsert'
GO
-- TrigRightsKeysInsert


-- create the trigger:
CREATE TRIGGER TrigRightsKeysInsert ON ibs_RightsKeys
FOR INSERT
AS 
DECLARE
    -- local variables:
    @id         ID, 
    @seq        INT,
    @oldId      ID, 
    @rPersonId  PERSONID, 
    @rights     RIGHTS, 
    @cnt        INT

    -- count the number of inserted tuples:
    SELECT  @cnt = COUNT (*)
    FROM    inserted

    -- check if there were some tuples inserted:
    IF (@cnt > 0)                      -- do something if tuples are found
	BEGIN
        
        SELECT  @id = MIN (id)
        FROM    inserted

        IF (@id <= 0)
        BEGIN
            -- remember the old id:
            SELECT  @oldId = @id
            -- get id for the inserted tuples:
            SELECT  @id = COALESCE (MAX (id) + 1, 1)
            FROM    ibs_RightsKeys

            IF (@id <= 0)                   -- value to low?
                -- set the lowest possible value:
                SELECT  @id = 1

            -- set id for the inserted tuples:    
            UPDATE  ibs_RightsKeys
            SET     id = @id
            WHERE   id <= 0
                AND id IN ( SELECT  id 
                            FROM    inserted)
        END -- if

        -- set the number of actual values within the key:
        UPDATE  ibs_RightsKeys
        SET     cnt =
                (   SELECT  COUNT (id)
                    FROM    ibs_RightsKeys
                    WHERE   id = @id
                )
        WHERE   id = @id
	END --if (@cnt > 0 )

GO
-- TrigRightsKeysInsert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig 'TrigRightsKeysUpdate'
GO
-- TrigRightsKeysUpdate


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig 'TrigRightsKeysDelete'
GO
-- TrigRightsKeysDelete


PRINT 'Trigger für Tabelle ibs_RightsKeys angelegt'
GO
 