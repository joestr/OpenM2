/******************************************************************************
 * The triggers for the ibs_Protocol_01 table. <BR>
 *
 * @version     $Id: Protocol_01Trig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ)  980901
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigProtocolInsert'
GO

-- create the trigger:
CREATE TRIGGER TrigProtocolInsert ON ibs_Protocol_01
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
            FROM    ibs_Protocol_01
        END -- if id not set

        UPDATE ibs_Protocol_01 
        SET id = @id
        WHERE id = @oldId
GO
-- TrigProtocolInsert

PRINT 'Trigger für Tabelle ibs_Protocol_01 angelegt'
GO
