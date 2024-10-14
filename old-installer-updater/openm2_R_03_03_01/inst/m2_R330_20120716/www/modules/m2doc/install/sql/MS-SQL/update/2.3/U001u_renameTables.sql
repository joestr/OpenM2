/******************************************************************************
 * Task:        TASK Release 2.4: Separate ibs and m2.
 *
 * Description: Rename all changed tables.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U001u_renameTables.sql,v 1.1 2003/10/31 16:27:20 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 031031
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_renameTable '$RCSFile$', 'ibs_Address_01', 'm2_Address_01'
GO

EXEC p_renameTable '$RCSFile$', 'm2_Beitrag_01', 'm2_Article_01'
GO

EXEC p_renameTable '$RCSFile$', 'm2_Diskussion_01', 'm2_Discussion_01'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
