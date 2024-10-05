/******************************************************************************
 * The triggers for the ibs workspace table. <BR>
 *
 * @version     $Id: WorkspaceTrig.sql,v 1.5 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980617
 ******************************************************************************
 */
/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigWorkspaceInsert'
GO

-- create the trigger:
/*
CREATE TRIGGER TrigWorkspaceInsert ON ibs_Workspace
FOR INSERT
AS 
*/
GO
-- TrigWorkspaceInsert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigWorkspaceUpdate'
GO

-- create the trigger:
/*
CREATE TRIGGER TrigWorkspaceUpdate ON ibs_Workspace
FOR INSERT
AS 
*/
GO
-- TrigWorkspaceUpdate


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigWorkspaceDelete'
GO

-- create the trigger:
/*
CREATE TRIGGER TrigWorkspaceDelete ON ibs_Workspace
FOR INSERT
AS 
*/
GO
-- TrigWorkspaceDelete


PRINT 'Trigger für ibs_Workspace angelegt'
GO
