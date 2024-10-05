/******************************************************************************
 * Task:        TASK EVN CRE5
 *              EVN_CR157: Create backup of imported files.
 *
 * Description: For all files which are imported shall be backups created.
 *              The same is done for exported files.
 *              There are some new tokens which have to be set in the
 *              multilang table.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24016u_createTokens_german.sql,v 1.2 2005/06/16 00:10:11 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20050615
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- tokens:
EXEC p_Token_01$new 0,'TOK_CREATEIMPORTBACKUP', 'Importdateien sichern', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_CREATEEXPORTBACKUP', 'Exportdateien sichern', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_SELECTBACKUPCONNECTOR', 'Konnektor für Sicherung ausw&auml;hlen', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_SETBACKUPCONNECTOR', 'Backup Konnektor setzen ...', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_BACKUPCONNECTORTYPE', 'Konnektortype für Sicherung', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_BACKUPCONNECTOR', 'Sicherungs-Konnektor', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_BACKUPPATH', 'Sicherungs-Pfad', 'ibs.di.DITokens'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
