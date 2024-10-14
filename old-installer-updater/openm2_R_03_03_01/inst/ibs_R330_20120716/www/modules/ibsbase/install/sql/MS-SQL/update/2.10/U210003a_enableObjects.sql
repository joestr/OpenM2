/******************************************************************************
 * Activation of several objects which where disabled before the migration. <BR>
 *
 * @version     $Id: U210003a_enableObjects.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Activation of ALL constraints on ALL tables
EXEC sp_MSforeachtable 'ALTER TABLE ? CHECK CONSTRAINT ALL'
GO

-- Activation of ALL index on ALL tables
EXEC sp_MSforeachtable 'ALTER INDEX ALL ON ? REBUILD'
GO