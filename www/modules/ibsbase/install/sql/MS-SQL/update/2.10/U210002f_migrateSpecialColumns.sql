/******************************************************************************
 * Migrate columns which are not handled by the migration procedure. Migrate
 * those columns per hand to get them up-to-date.<BR>
 *
 * @version     $Id: U210002f_migrateSpecialColumns.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Migrate 'dbo.ibs_System.name'
DROP INDEX IndexSystemName ON dbo.ibs_System
GO
ALTER TABLE dbo.ibs_System ALTER COLUMN name NNAME NOT NULL
GO
CREATE UNIQUE INDEX IndexSystemName ON dbo.ibs_System (name)
GO

-- Migrate 'dbo.ibs_TVersionProc.code'
DROP INDEX IndexTVersionProcIdCode ON dbo.ibs_TVersionProc
GO
ALTER TABLE dbo.ibs_TVersionProc ALTER COLUMN code NNAME NOT NULL
GO
CREATE UNIQUE INDEX IndexTVersionProcIdCode ON dbo.ibs_TVersionProc (tVersionId, code)
GO

-- Migrate 'dbo.ibs_WorkflowVariables.variableName'
DROP INDEX IndexWFVariablesIdName ON dbo.ibs_WorkflowVariables
GO
ALTER TABLE dbo.ibs_WorkflowVariables ALTER COLUMN variableName NVARCHAR(64) NOT NULL
GO
CREATE UNIQUE INDEX IndexWFVariablesIdName ON dbo.ibs_WorkflowVariables (instanceId, variableName)
GO