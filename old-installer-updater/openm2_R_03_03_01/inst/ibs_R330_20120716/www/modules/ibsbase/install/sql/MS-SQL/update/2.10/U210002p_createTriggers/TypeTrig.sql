/******************************************************************************
 * The triggers for the ibs type table. <BR>
 *
 * @version     $Id: TypeTrig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */

/******************************************************************************
 * INSERT trigger for ibs_Type
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigTypeInsert'
GO

-- create the trigger
CREATE TRIGGER TrigTypeInsert ON ibs_Type
FOR INSERT
AS 
    DECLARE @thisSeq INT, @domainId INT, @serverId INT, @id TYPEID,
            @superTypeId TYPEID, @posNo POSNO, @posNoPath POSNOPATH,
            @tVersionId TVERSIONID

    SELECT  @tVersionId = 0x01012301

/*
    SELECT  @domainId = CONVERT (BINARY (1), CONVERT (INT, value))
    FROM    ibs_System
    WHERE   name = 'domainId'

    SELECT  @serverId = CONVERT (BINARY (1), CONVERT (INT, value))
    FROM    ibs_System
    WHERE   name = 'serverId'
*/

    -- get next sequence number:
    -- get actual id
    SELECT  @id = id
    FROM    inserted

    IF (@id = 0x00000000)           -- no id defined?
    BEGIN
        SELECT  @id = COALESCE (MIN (id) + 0x00000010, 0x01010010)
        FROM    ibs_Type
        WHERE   (id + 0x00000010) NOT IN 
                (
                    SELECT  id 
                    FROM    ibs_Type
                )
            AND id > 0

        -- set the id (if <= 0):
        UPDATE  ibs_Type
        SET     id = @id
--        SET     id = @domainId * 0x1000000 + @serverId * 0x10000 + @thisSeq * 0x10
        WHERE   id IN ( SELECT  id
                        FROM    inserted
                        WHERE   id <= 0)
    END -- if no id defined

    -- get super type id:
    SELECT  @superTypeId = superTypeId
    FROM    inserted

    -- get position number:
    SELECT  @posNo = COALESCE (MAX (t.posNo) + 1, 1)
    FROM    ibs_Type t, inserted i
    WHERE   t.superTypeId = i.superTypeId
        AND t.id <> i.id

    -- get position path:
    IF (@superTypeId <> 0)              -- object is a subtype?
    BEGIN
        -- compute the posNoPath as posNoPath of super type concatenated by
        -- the posNo of this type:
        SELECT  DISTINCT @posNoPath = t.posNoPath + CONVERT (BINARY (2), @posNo)
        FROM    ibs_Type t, inserted i
        WHERE   t.id = i.superTypeId
    END -- if type is a subtype
    ELSE                                -- type is not a subtype
                                        -- i.e. it is on top level
    BEGIN
        -- compute the posNoPath as posNo of this object:
        SELECT  @posNoPath = CONVERT (BINARY (2), @posNo)
    END -- else type is not subtype

    -- set other attributes:
    UPDATE  ibs_Type
    SET     posNo = @posNo,
            posNoPath = @posNoPath,
            oid = CONVERT (BINARY (4), @tVersionId) + CONVERT (BINARY (4), @id),
            icon = code + N'.gif'
    WHERE   id = @id
GO
-- TrigTypeInsert



/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigTypeUpdate'
GO


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigTypeDelete'
GO


PRINT 'Trigger für Tabelle ibs_Type angelegt'
GO
