/******************************************************************************
 * The triggers for the ibs group table. <BR>
 *
 * @version     $Id: GroupTrig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  980706
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigGroupInsert'
GO


-- create the trigger:
CREATE TRIGGER TrigGroupInsert ON ibs_Group
FOR INSERT
AS 
    -- define local variables:
    DECLARE @id GROUPID, @newId GROUPID, @domainId DOMAINID, @oid OBJECTID, 
            @tVersionId TVERSIONID

        -- get the id and oid:
        SELECT  @id = id, @newId = id, @domainId = domainId, @oid = oid,
                @tVersionId = 0x010100B1
        FROM    inserted

        IF (@id <= 0)                   -- no id defined?
        BEGIN
            -- set the id:
            SELECT  @newId = COALESCE ((MAX (id) + 1), @domainId * 0x01000000 + 0x200001)
            FROM    ibs_Group
            WHERE   domainId = @domainId
                AND id <> @id

            -- set the new id:
            UPDATE  ibs_Group
            SET     id = @newId
            WHERE   id = @id

            -- remember new id:
            SELECT  @id = @newId
        END -- if no id defined

        -- ensure that a correct oid is set:
        IF (@oid = 0x0000000000000000)  -- no oid set?
            -- compute and set oid:
            UPDATE  ibs_Group
            SET     -- oid = domain, server, type + version, id
                    oid = CONVERT (BINARY (4), @tVersionId) + CONVERT (BINARY (4), @id)
            WHERE   id = @id
GO
-- TrigGroupInsert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigGroupUpdate'
GO


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigGroupDelete'
GO


PRINT 'Trigger für Tabelle ibs_Group angelegt'
GO
