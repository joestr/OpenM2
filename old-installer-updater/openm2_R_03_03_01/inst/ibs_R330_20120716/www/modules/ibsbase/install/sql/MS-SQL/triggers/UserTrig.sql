/******************************************************************************
 * The triggers for the ibs user table. <BR>
 *
 * @version     $Id: UserTrig.sql,v 1.5 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980528
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigUserInsert'
GO

-- create the trigger:
CREATE TRIGGER TrigUserInsert ON ibs_User
FOR INSERT
AS 
    -- define local variables:
    DECLARE @id USERID, @newId USERID, @domainId DOMAINID, @oid OBJECTID, 
            @tVersionId TVERSIONID

        -- get the id and oid:
        SELECT  @id = id, @newId = id, @domainId = domainId, @oid = oid,
                @tVersionId = 0x010100A1
        FROM    inserted

        IF (@id <= 0)                   -- no id defined?
        BEGIN
            -- set the id:
            SELECT  @newId = COALESCE ((MAX (id) + 1), @domainId * 0x01000000 + 0x800001)
            FROM    ibs_User
            WHERE   domainId = @domainId
                AND id <> @id

            -- set the new id:
            UPDATE  ibs_User
            SET     id = @newId
            WHERE   id = @id

            -- remember new id:
            SELECT  @id = @newId
        END -- if no id defined

        -- ensure that a correct oid is set:
        IF (@oid = 0x0000000000000000)  -- no oid set?
            -- compute and set oid:
            UPDATE  ibs_User
            SET     -- oid = domain, server, type + version, id
                    oid = CONVERT (BINARY (4), @tVersionId) + CONVERT (BINARY (4), @id)
            WHERE   id = @id
GO
-- TrigUserInsert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigUserUpdate'
GO


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigUserDelete'
GO


PRINT 'Trigger für Tabelle ibs_User angelegt'
GO
