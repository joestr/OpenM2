/******************************************************************************
 * Drop old UDDT with VARCHAR fields and create new UDDT with NVARCHAR fields
 * Additional bind the default to the new UDDT <BR>
 *
 * @version     $Id: U210002l_createNewUDDT.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Drop old UDDT for 'Description'
DROP TYPE DESCRIPTION
GO

-- Create new UDDT for 'Description'
CREATE TYPE DESCRIPTION FROM NVARCHAR(255) NULL
GO

-- Bind default to the new UDDT
EXEC sp_bindefault 'DEF_DESCRIPTION', 'DESCRIPTION'
GO

-- Drop old UDDT for 'Name'
DROP TYPE NAME
GO
-- Create new UDDT for 'Name'
CREATE TYPE NAME FROM NVARCHAR(63) NULL
GO

-- Bind default to the new UDDT
-- NO DEFAULT NECESSARY FOR UDDT 'NAME'

-- Drop old UDDT for 'Filename'
DROP TYPE FILENAME
GO

-- Create new UDDT for 'Filename'
CREATE TYPE FILENAME FROM NVARCHAR(255) NULL
GO

-- Bind default to the new UDDT
EXEC sp_bindefault 'DEF_FILENAME', 'FILENAME'
GO

-- Drop old UDDT for 'EMail'
DROP TYPE EMAIL
GO

-- Create new UDDT for 'EMail'
CREATE TYPE EMAIL FROM NVARCHAR(128) NULL
GO

-- Bind default to the new UDDT
EXEC sp_bindefault 'DEF_EMAIL', 'EMAIL'
GO

-- Drop old UDDT for 'StoredProcname'
DROP TYPE STOREDPROCNAME
GO

-- Create new UDDT for 'StoredProcname'
CREATE TYPE STOREDPROCNAME FROM NVARCHAR(128) NULL
GO

-- Bind default to the new UDDT
EXEC sp_bindefault 'DEF_STOREDPROCNAME', 'STOREDPROCNAME'
GO

-- Drop old UDDT for 'Phonenum'
DROP TYPE PHONENUM
GO

-- Create new UDDT for 'Phonenum'
CREATE TYPE PHONENUM FROM NVARCHAR(25) NULL
GO

-- Bind default to the new UDDT
EXEC sp_bindefault 'DEF_PHONENUM', 'PHONENUM'
GO
