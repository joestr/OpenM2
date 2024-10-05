/******************************************************************************
 * Task:        IBS-85 - Multiselectionbox
 *
 * Description: For the new table ibs_ProtocolEntry_01 additional indexes are
 *				necessary.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U25004i_createProtocolEntryIndex.sql,v 1.2 2008/06/03 13:33:36 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20080416
 ******************************************************************************
 */

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

IF EXISTS (SELECT name FROM sysindexes
            WHERE name = 'IndexProtocolEntryId')
    DROP INDEX ibs_ProtocolEntry_01.IndexProtocolEntryId
GO
IF EXISTS (SELECT name FROM sysindexes
            WHERE name = 'IndexProtocolEntry_protocolId_01')
    DROP INDEX ibs_ProtocolEntry_01.IndexProtocolEntry_protocolId_01
GO

CREATE UNIQUE INDEX IndexProtocolEntryId         ON ibs_ProtocolEntry_01 (id)
GO
CREATE INDEX IndexProtocolEntry_protocolId_01    ON ibs_ProtocolEntry_01 (protocolId)
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
