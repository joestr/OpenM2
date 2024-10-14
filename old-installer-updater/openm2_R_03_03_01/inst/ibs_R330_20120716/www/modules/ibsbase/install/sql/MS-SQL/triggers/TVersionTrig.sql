/******************************************************************************
 * The triggers for the ibs type version table. <BR>
 * 
 * @version     $Id: TVersionTrig.sql,v 1.4 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980416
 ******************************************************************************
 */

/******************************************************************************
 * INSERT trigger for ibs_TVersion.
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigTVersionInsert'
GO

CREATE TRIGGER TrigTVersionInsert ON ibs_TVersion
FOR INSERT
AS 
DECLARE
    -- constants:

    -- local variables:
    @l_thisSeq              INT,            -- return value of a function
    @l_typeId               TYPEID,         -- the actual error code
    @l_id                   TVERSIONID,     -- id of actual tVersion
    @l_posNoPath            POSNOPATH_VC,   -- posNoPath of actual tVersion
    @l_posNo                POSNO,          -- position of actual tVersion
    @l_posNoHex             VARCHAR (4),    -- hex representation of posNo
    @l_superTVersionId      TVERSIONID      -- id of super tVersion

    -- assign constants:

    -- initialize local variables:

-- body:
    SELECT  @l_id = id, @l_typeId = typeId
    FROM    inserted

    -- check if an id was defined:
    IF (@l_id IS NULL OR @l_id <= 0)        -- no id defined?
    BEGIN
        -- create a new sequence number:
        SELECT  @l_thisSeq = COALESCE (MAX (tVersionSeq) + 1, 1)
        FROM    ibs_TVersion
        WHERE   typeId = @l_typeId

        -- compute the id as sum of typeId and sequnce number:
        SELECT  @l_id = @l_typeId | @l_thisSeq
    END -- if no id defined
    ELSE                                    -- there was an id defined
    BEGIN
        -- compute the sequence number out of the id:
        SELECT  @l_thisSeq = @l_id & 0xf
    END -- else there was an id defined

    -- get super tVersion id:
    SELECT  @l_superTVersionId = superTVersionId
    FROM    inserted

    -- get position number:
    SELECT  @l_posNo = COALESCE (MAX (t.posNo) + 1, 1)
    FROM    ibs_TVersion t, inserted i
    WHERE   t.superTVersionId = i.superTVersionId
        AND t.id <> i.id

    -- convert the position number into hex representation:
    EXEC p_IntToHexString @l_posNo, @l_posNoHex OUTPUT

    -- get position path:
    IF (@l_superTVersionId <> 0)        -- tVersion is a subtversion?
    BEGIN
        -- compute the posNoPath as posNoPath of super tVersion concatenated by
        -- the posNo of this tVersion:
        SELECT  DISTINCT @l_posNoPath = posNoPath + @l_posNoHex
        FROM    ibs_TVersion
        WHERE   id = @l_superTVersionId
    END -- if tVersion is a subtversion
    ELSE                                -- type is not a subtype
                                        -- i.e. it is on top level
    BEGIN
        -- compute the posNoPath as posNo of this object:
        SELECT  @l_posNoPath = @l_posNoHex
    END -- else type is not subtype

    -- set the values:
    UPDATE  ibs_TVersion
    SET     id = @l_id,
            tVersionSeq = @l_thisSeq,
            posNo = @l_posNo,
            posNoPath = @l_posNoPath,
            idProperty = CONVERT (BINARY (4), @l_id) + CONVERT (BINARY (2), 0000),
            orderProperty = CONVERT (BINARY (4), @l_id) + CONVERT (BINARY (2), 0000)
    WHERE   id IN ( SELECT  id
                    FROM    inserted
                    WHERE   id <= 0)
GO
-- TrigTVersionInsert


PRINT 'Created trigger for the table ibs_TVersion'
GO
