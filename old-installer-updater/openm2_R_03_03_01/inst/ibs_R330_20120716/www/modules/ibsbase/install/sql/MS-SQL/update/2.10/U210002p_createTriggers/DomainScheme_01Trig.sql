/******************************************************************************
 * The triggers for the ibs domain scheme table. <BR>
 *
 * @version     $Id: DomainScheme_01Trig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  980923
 ******************************************************************************
 */
-- INSERT trigger for ibs_DomainScheme_01
-- delete existing trigger:
EXEC p_dropTrig N'TrigDomainScheme_01Insert'
GO

-- create the trigger
CREATE TRIGGER TrigDomainScheme_01Insert ON ibs_DomainScheme_01
FOR INSERT
AS 
    DECLARE @id ID, @newId ID, @oid OBJECTID, @newOid OBJECTID, 
            @tVersionId TVERSIONID, @NOOID OBJECTID

    SELECT  @tVersionId = 0x00000001, @NOOID = 0x0000000000000000

    -- ensure that an id and an oid are set:
        -- get actual id and oid:
        SELECT  @id = id, @newId = id, @oid = oid, @newOid = oid
        FROM    inserted

        -- ensure that an id is set:
        IF (@id <= 0x00000000)              -- no id defined?
            -- compute new id:
            SELECT  @newId = COALESCE (MAX (id) + 1, 1)
            FROM    ibs_DomainScheme_01

        -- ensure that an oid is set:
        IF (@oid = @NOOID)                  -- no oid defined?
            -- compute new oid:
            SELECT  @newOid = CONVERT (BINARY (4), @tVersionId) + CONVERT (BINARY (4), @newId)

        -- set the id and oid:
        UPDATE  ibs_DomainScheme_01
        SET     id = @newId,
                oid = @newOid
        WHERE   id = @id
GO
-- TrigDomainScheme_01Insert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigDomainScheme_01Update'
GO

/*
-- create the trigger
CREATE TRIGGER TrigDomainScheme_01Update ON ibs_DomainScheme_01
FOR UPDATE
AS 
*/
GO
-- TrigDomainScheme_01Update


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigDomainScheme_01Delete'
GO

/*
-- create the trigger
CREATE TRIGGER TrigDomainScheme_01Delete ON ibs_DomainScheme_01
FOR DELETE
AS 
*/
GO
-- TrigDomainScheme_01Delete


PRINT 'Trigger für Tabelle ibs_DomainScheme_01 angelegt'
GO
