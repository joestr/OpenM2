/******************************************************************************
 * Task:        IBS-50 - Multiselectionbox
 *
 * Description: For multi selection fields it is necessary to store more than
 *				one referecedOid to a referencingOid fieldName pair. Since that
 *				it is necessary to drop the index ibs_Reference.IndexReferenceOidName.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U25002i_deleteIndexReferenceOidName.sql,v 1.2 2008/06/03 13:33:36 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20080225
 ******************************************************************************
 */

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO


-- drop the index ibs_Reference.IndexReferenceOidName
IF EXISTS (SELECT name FROM sysindexes
            WHERE name = 'IndexReferenceOidName')
    DROP INDEX ibs_Reference.IndexReferenceOidName
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
