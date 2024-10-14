/******************************************************************************
 * The triggers for the ibs_ProtocolEntry_01 table. <BR>
 *
 * @version     $Id: U25004z_ProtocolEntry_01Trig.sql,v 1.1 2008/06/02 14:00:13 ctran Exp $
 *
 * @author      Bernhard Tatzmann (BT)  080411
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig 'TrigProtocolEntryInsert'
GO

-- create the trigger:
CREATE TRIGGER TrigProtocolEntryInsert ON ibs_ProtocolEntry_01
FOR INSERT
AS
    --
    DECLARE @id     ID
    DECLARE @oldId  ID

        -- get the inserted id:
        SELECT  @oldId = id, @id = id
        FROM    inserted

        IF (@oldId <= 0)                    -- id not set?
        BEGIN
            -- compute new id:
            SELECT  @id = COALESCE (MAX (id) + 1, 1)
            FROM    ibs_ProtocolEntry_01
        END -- if id not set

        UPDATE ibs_ProtocolEntry_01 
        SET id = @id
        WHERE id = @oldId
GO
-- TrigProtocolEntryInsert

PRINT 'Trigger für Tabelle ibs_ProtocolEntry_01 angelegt'
GO
