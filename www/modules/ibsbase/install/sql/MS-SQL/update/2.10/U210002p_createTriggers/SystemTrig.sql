/******************************************************************************
 * The triggers for the ibs system table. <BR>
 *
 * @version     $Id: SystemTrig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW)  980702
 ******************************************************************************
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigSystemInsert'
GO

-- create trigger:
CREATE TRIGGER TrigSystemInsert ON ibs_System
FOR INSERT
AS 
    -- compute and set new id:
    UPDATE  ibs_System
    SET     id =  ( SELECT COALESCE (MAX (id) + 1, 1)
                    FROM    ibs_System)
    WHERE   id IN ( SELECT  id
                    FROM    inserted
                    WHERE   id <= 0)
GO
-- TrigSystemInsert


PRINT 'Trigger für Tabelle ibs_System angelegt'
GO
