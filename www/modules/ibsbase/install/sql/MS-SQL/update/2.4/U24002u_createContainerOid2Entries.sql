/******************************************************************************
 * Task:        EVN CRE4 Performance tuning.
 *
 * Description: Create all containerOid2 entries in ibs_Object.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24002u_createContainerOid2Entries.sql,v 1.2 2005/01/27 01:20:30 klaus Exp $
 *
 * @author      Klaus Reimueller (KR) 20050112
 ******************************************************************************
 */ 


-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_msg                  VARCHAR (255),  -- the actual message
    @l_oLevel               INT,            -- the actual oLevel
    @l_maxOLevel            INT             -- the maximum oLevel

-- assign constants:

-- initialize local variables:
SELECT
    @l_file = 'U24002u',
    @l_error = 0,
    @l_oLevel = 1,
    @l_maxOLevel = 0

-- body:
    -- display a message:
    SELECT  @l_msg = @l_file +
                     ' - Creating containerOid2 entries for ibs_Object:'
    RAISERROR (@l_msg, 0, 1) WITH NOWAIT

    -- create all entries:
    UPDATE  ibs_Object
    SET     containerOid2 = c.containerId
    FROM    ibs_Object, ibs_Object c
    WHERE   ibs_Object.containerId = c.oid
/*
    INSERT INTO ibs_Object2
        (id, oid, state, tVersionId, typeName, isContainer,
         name, containerId, containerOid2,
         containerKind, isLink, linkedObjectId, showInMenu, flags,
         owner, oLevel, posNo, posNoPath, creationDate, creator,
         lastChanged, changer, validUntil, description, icon,
         processState, rKey, consistsOfId)
    SELECT  o.id, o.oid, o.state, o.tVersionId, o.typeName, o.isContainer,
            o.name, o.containerId, COALESCE (c.containerId, 0x0000000000000000),
            o.containerKind, o.isLink, o.linkedObjectId, o.showInMenu, o.flags,
            o.owner, o.oLevel, o.posNo, o.posNoPath, o.creationDate, o.creator,
            o.lastChanged, o.changer, o.validUntil, o.description, o.icon,
            o.processState, o.rKey, o.consistsOfId
    FROM    ibs_Object o LEFT OUTER JOIN ibs_Object c ON o.containerId = c.oid
*/

    -- jump to end of code block:
    GOTO finish

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished'
    PRINT @l_msg
GO

-- show count messages again:
SET NOCOUNT OFF
GO
