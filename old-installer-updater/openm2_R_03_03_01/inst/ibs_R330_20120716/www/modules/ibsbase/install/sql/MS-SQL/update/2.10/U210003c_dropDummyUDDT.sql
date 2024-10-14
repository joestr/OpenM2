/******************************************************************************
 * Drop dummy user defined datatypes for the migration process. <BR>
 *
 * @version     $Id: U210003c_dropDummyUDDT.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Drop dummy UDDT for 'Description'
DROP TYPE NDESCRIPTION
GO

-- Drop dummy UDDT for 'Name'
DROP TYPE NNAME
GO

-- Drop dummy UDDT for 'Filename'
DROP TYPE NFILENAME
GO

-- Drop dummy UDDT for 'EMail'
DROP TYPE NEMAIL
GO

-- Drop dummy UDDT for 'StoredProcname'
DROP TYPE NSTOREDPROCNAME
GO

-- Drop dummy UDDT for 'Phonenum'
DROP TYPE NPHONENUM
GO
