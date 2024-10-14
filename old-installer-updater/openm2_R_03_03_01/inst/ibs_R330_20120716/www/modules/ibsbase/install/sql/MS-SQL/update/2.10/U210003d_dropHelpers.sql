/******************************************************************************
 * Create several help objects which are used during the migration. <BR>
 *
 * @version     $Id: U210003d_dropHelpers.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Drop procedure for migration
EXEC p_dropProc 'uc_migrateOneColumn'
GO

-- Drop view for migration
DROP VIEW dbo.vAllIndexes
GO

-- Delete function for migration:
EXEC p_dropFunc 'GetIndexColumns'
GO

-- Delete function for migration
EXEC p_dropFunc 'GetIndexColumnOrder'
GO

-- Drop the table
-- DROP TABLE uc_stproclist
GO 
