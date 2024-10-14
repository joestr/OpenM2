/******************************************************************************
 * The triggers for the ibs domain table. <BR>
 * 
 * @version     $Id: Domain_01Trig.sql,v 1.5 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980725
 ******************************************************************************
 */

-- INSERT trigger for ibs_Domain_01
-- delete existing trigger:
EXEC p_dropTrig N'TrigDomain_01Insert'
GO

-- create the trigger
CREATE TRIGGER TrigDomain_01Insert ON ibs_Domain_01
FOR INSERT
AS 
    DECLARE @id DOMAINID, @newId DOMAINID, @oid OBJECTID, @newOid OBJECTID, 
            @tVersionId TVERSIONID, @NOOID OBJECTID

    SELECT  @tVersionId = 0x010100F1, @NOOID = 0x0000000000000000

    -- ensure that an id and an oid are set:
    -- get actual id and oid:
    SELECT  @id = id, @newId = id, @oid = oid, @newOid = oid
    FROM    inserted

    -- ensure that an id ist set:
    IF (@id <= 0x00000000)              -- no id defined?
        -- compute new id:
        SELECT  @newId = COALESCE (MAX (id) + 1, 1)
        FROM    ibs_Domain_01

    -- ensure that an oid is set:
    IF (@oid = @NOOID)                  -- no oid defined?
        -- compute new oid:
        SELECT  @newOid = CONVERT (BINARY (4), @tVersionId) + CONVERT (BINARY (4), @newId)

    -- set the id and oid:
    UPDATE  ibs_Domain_01
    SET     id = @newId,
            oid = @newOid
    WHERE   id = @id

/* the following code is not longer necessary:
    -- ensure that there is a valid scheme:
    IF NOT EXISTS 
        (SELECT d.id
        FROM    ibs_Domain_01 d, ibs_DomainScheme_01 s
        WHERE   d.id = @newId
            AND d.scheme = s.id)
                                        -- no valid scheme?
        -- set first foundable scheme:
        UPDATE  ibs_Domain_01
        SET     scheme = s.id
        FROM    ibs_Domain_01 d, ibs_DomainScheme_01 s
        WHERE   d.id = @newId
            AND s.id = 
                (
                    SELECT  MIN (id)
                    FROM    ibs_DomainScheme_01
                )

    -- get workspaceProc from the scheme:
    UPDATE  ibs_Domain_01
    SET     workspaceProc = s.workspaceProc
    FROM    ibs_Domain_01 d, ibs_DomainScheme_01 s
    WHERE   d.id = @newId
        AND d.scheme = s.id
*/
    -- ensure that there is a valid workspace procedure set:
    UPDATE  ibs_Domain_01
    SET     workspaceProc = N'p_Workspace_01$createObjects'
    WHERE   id = @newId
        AND workspaceProc IS NULL
GO
-- TrigDomain_01Insert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigDomain_01Update'
GO

/*
-- create the trigger
CREATE TRIGGER TrigDomain_01Update ON ibs_Domain_01
FOR UPDATE
AS 
*/
GO
-- TrigDomain_01Update


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigDomain_01Delete'
GO

/*
-- create the trigger
CREATE TRIGGER TrigDomain_01Delete ON ibs_Domain_01
FOR DELETE
AS 
*/
GO
-- TrigDomain_01Delete


PRINT 'Trigger für Tabelle ibs_Domain_01 angelegt'
GO
