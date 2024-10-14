/******************************************************************************
 * Unbind of defaults from the user defined datatypes (UDDT). <BR>
 * Necessary to be able to migrate a column which uses one of those datatypes
 *
 * @version     $Id: U210002a_unbindUDDT.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Unbind default from 'Description'
EXEC sp_unbindefault 'DESCRIPTION'
GO

-- Unbind default from 'Filename'
EXEC sp_unbindefault 'FILENAME'
GO

-- Unbind default from 'EMail'
EXEC sp_unbindefault 'EMAIL'
GO

-- Unbind default from 'StoredProcName'
EXEC sp_unbindefault 'STOREDPROCNAME'
GO

-- Unbind default from 'Phonenum'
EXEC sp_unbindefault 'PHONENUM'
GO
