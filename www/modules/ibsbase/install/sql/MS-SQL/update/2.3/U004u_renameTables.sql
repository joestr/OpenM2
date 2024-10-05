/******************************************************************************
 * Task:        TASK Release 2.4: Separate ibs and m2.
 *
 * Description: Rename all changed tables.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U004u_renameTables.sql,v 1.1 2003/10/31 16:30:27 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 031031
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_renameTable '$RCSFile$', 'm2_Note_01', 'ibs_Note_01'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
