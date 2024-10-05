/******************************************************************************
 * Create dummy user defined datatypes for the migration process. <BR>
 *
 * @version     $Id: U210002b_createDummyUDDT.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Create dummy UDDT for 'Description'
CREATE TYPE NDESCRIPTION FROM NVARCHAR(255) NULL
GO

-- Create dummy UDDT for 'Name'
CREATE TYPE NNAME FROM NVARCHAR(63) NULL
GO

-- Create dummy UDDT for 'Filename'
CREATE TYPE NFILENAME FROM NVARCHAR(255) NULL
GO

-- Create dummy UDDT for 'EMail'
CREATE TYPE NEMAIL FROM NVARCHAR(128) NULL
GO

-- Create dummy UDDT for 'StoredProcname'
CREATE TYPE NSTOREDPROCNAME FROM NVARCHAR(128) NULL
GO

-- Create dummy UDDT for 'Phonenum'
CREATE TYPE NPHONENUM FROM NVARCHAR(25) NULL
GO
