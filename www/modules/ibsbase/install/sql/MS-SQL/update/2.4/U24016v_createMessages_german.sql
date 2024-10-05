/******************************************************************************
 * Task:        TASK EVN CRE5
 *              EVN_CR157: Create backup of imported files.
 *
 * Description: For all files which are imported shall be backups created.
 *              The same is done for exported files.
 *              There are some new messages which have to be set in the
 *              multilang table.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24016v_createMessages_german.sql,v 1.1 2005/06/15 14:33:15 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20050615
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- messages:
EXEC p_Message_01$new 0,'MSG_COULDNOTWRITETOBACKUPCONNECTOR', 'Die Daten konnten nicht auf den Sicherungs-Konnektorpfad geschrieben werden.', 'ibs.di.DIMessages'
EXEC p_Message_01$new 0,'MSG_BACKUP_CONNECTOR_NOT_ALLOWED', 'Dieser Konnektor kann nicht als Backup-Konnektor verwendet werden.', 'ibs.di.DIMessages'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
