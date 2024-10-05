/******************************************************************************
 * The sql data types necessary for the application. <BR>
 *
 * @version     $Id: Datatypes.sql,v 1.7 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980307
 ******************************************************************************
 */

-- set super user
setuser '#CONFVAR.ibsbase.dbOwner#'
GO



PRINT 'Common data types'

-- data type BOOL
EXEC sp_addtype N'BOOL', N'BIT', N'NOT NULL'
GO

-- data type DESCRIPTION
EXEC sp_addtype N'DESCRIPTION', N'NVARCHAR (255)', N'NULL'
GO
CREATE DEFAULT DEF_DESCRIPTION AS null
GO
EXEC sp_bindefault N'DEF_DESCRIPTION', N'DESCRIPTION'
GO

-- data type ID
EXEC sp_addtype N'ID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_ID AS 0
GO
EXEC sp_bindefault N'DEF_ID', N'ID'
GO


PRINT 'Database management - tables'

-- data type TABLEID
EXEC sp_addtype N'TABLEID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_TABLEID AS 0
GO
EXEC sp_bindefault N'DEF_TABLEID', N'TABLEID'
GO

-- data type TABLESEQ
EXEC sp_addtype N'TABLESEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_TABLESEQ AS 0
GO
EXEC sp_bindefault N'DEF_TABLESEQ', N'TABLESEQ'
GO

-- data type COLUMNID
EXEC sp_addtype N'COLUMNID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_COLUMNID AS 0
GO
EXEC sp_bindefault N'DEF_COLUMNID', N'COLUMNID'
GO

-- data type COLUMNSEQ
EXEC sp_addtype N'COLUMNSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_COLUMNSEQ AS 0
GO
EXEC sp_bindefault N'DEF_COLUMNSEQ', N'COLUMNSEQ'
GO

-- data type SQLDATATYPE
EXEC sp_addtype N'SQLDATATYPE', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_SQLDATATYPE AS 0
GO
EXEC sp_bindefault N'DEF_SQLDATATYPE', N'SQLDATATYPE'
GO


PRINT 'ibs - base elements'

-- data type DOMAINID
EXEC sp_addtype N'DOMAINID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_DOMAINID AS 0
GO
EXEC sp_bindefault N'DEF_DOMAINID', N'DOMAINID'
GO

-- data type SERVERID
EXEC sp_addtype N'SERVERID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_SERVERID AS 0
GO
EXEC sp_bindefault N'DEF_SERVERID', N'SERVERID'
GO


PRINT 'ibs - type model'

-- data type TYPEID
EXEC sp_addtype N'TYPEID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_TYPEID AS 0
GO
EXEC sp_bindefault N'DEF_TYPEID', N'TYPEID'
GO

-- data type TVERSIONSEQ
EXEC sp_addtype N'TVERSIONSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_TVERSIONSEQ AS 0
GO
EXEC sp_bindefault N'DEF_TVERSIONSEQ', N'TVERSIONSEQ'
GO

-- data type TVERSIONID
EXEC sp_addtype N'TVERSIONID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_TVERSIONID AS 0
GO
EXEC sp_bindefault N'DEF_TVERSIONID', N'TVERSIONID'
GO

-- data type GENERICMETHODID
EXEC sp_addtype N'GENERICMETHODID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_GENERICMETHODID AS 0
GO
EXEC sp_bindefault N'DEF_GENERICMETHODID', N'GENERICMETHODID'
GO

-- data type METHODSEQ
EXEC sp_addtype N'METHODSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_METHODSEQ AS 0
GO
EXEC sp_bindefault N'DEF_METHODSEQ', N'METHODSEQ'
GO

-- data type METHODID
EXEC sp_addtype N'METHODID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_METHODID AS 0
GO
EXEC sp_bindefault N'DEF_METHODID', N'METHODID'
GO


-- data type OPERATIONID
EXEC sp_addtype N'OPERATIONID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_OPERATIONID AS 0
GO
EXEC sp_bindefault N'DEF_OPERATIONID', N'OPERATIONID'
GO

-- data type PARAMETERSEQ
EXEC sp_addtype N'PARAMETERSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_PARAMETERSEQ AS 0
GO
EXEC sp_bindefault N'DEF_PARAMETERSEQ', N'PARAMETERSEQ'
GO

-- data type PARAMETERID
EXEC sp_addtype N'PARAMETERID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_PARAMETERID AS 0
GO
EXEC sp_bindefault N'DEF_PARAMETERID', N'PARAMETERID'
GO

-- data type DATATYPE
EXEC sp_addtype N'DATATYPE', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_DATATYPE AS 0
GO
EXEC sp_bindefault N'DEF_DATATYPE', N'DATATYPE'
GO

-- data type PROPERTYID
EXEC sp_addtype N'PROPERTYID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_PROPERTYID AS 0x00000000
GO
EXEC sp_bindefault N'DEF_PROPERTYID', N'PROPERTYID'
GO


-- data type PROPERTYSEQ
EXEC sp_addtype N'PROPERTYSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_PROPERTYSEQ AS 0
GO
EXEC sp_bindefault N'DEF_PROPERTYSEQ', N'PROPERTYSEQ'
GO


PRINT 'ibs - Runtime objects'

-- data type OBJECTSEQ
EXEC sp_addtype N'OBJECTSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_OBJECTSEQ AS 0
GO
EXEC sp_bindefault N'DEF_OBJECTSEQ', N'OBJECTSEQ'
GO

-- data type OBJECTID
EXEC sp_addtype N'OBJECTID', N'BINARY (8)', N'NOT NULL'
GO
CREATE DEFAULT DEF_OBJECTID AS 0x0000000000000000
GO
EXEC sp_bindefault N'DEF_OBJECTID', N'OBJECTID'
GO

-- data type OBJECTIDSTRING
EXEC sp_addtype N'OBJECTIDSTRING', 'NVARCHAR (18)', N'NULL'
GO

-- data type STATE
EXEC sp_addtype N'STATE', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_STATE AS 2
GO
EXEC sp_bindefault N'DEF_STATE', N'STATE'
GO

-- data type NAME
EXEC sp_addtype N'NAME', N'NVARCHAR (63)', N'NOT NULL'
GO
CREATE DEFAULT DEF_NAME AS N'undefined'
GO
--! Don't use default value for name.
-- EXEC sp_bindefault 'DEF_NAME', 'NAME'
GO

-- data type POSNO
EXEC sp_addtype N'POSNO', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_POSNO AS 0
GO
EXEC sp_bindefault N'DEF_POSNO', N'POSNO'
GO

-- data type POSNOPATH
EXEC sp_addtype N'POSNOPATH', N'VARBINARY (254)', N'NOT NULL'
GO
CREATE DEFAULT DEF_POSNOPATH AS 0x0000
GO
EXEC sp_bindefault N'DEF_POSNOPATH', N'POSNOPATH'
GO


-- data type PosNoPath_VC
EXEC sp_addtype N'POSNOPATH_VC', N'VARCHAR (254)', N'NOT NULL'
GO
CREATE DEFAULT DEF_POSNOPATH_VC AS '0000'
GO
EXEC sp_bindefault N'DEF_POSNOPATH_VC', N'POSNOPATH_VC'
GO


PRINT 'ibs - user and group management'

-- data type GROUPID
EXEC sp_addtype N'GROUPID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_GROUPID AS 0
GO
EXEC sp_bindefault N'DEF_GROUPID', N'GROUPID'
GO

-- data type GROUPSEQ
EXEC sp_addtype N'GROUPSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_GROUPSEQ AS 0
GO
EXEC sp_bindefault N'DEF_GROUPSEQ', N'GROUPSEQ'
GO

-- data type ROLEID
EXEC sp_addtype N'ROLEID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_ROLEID AS 0
GO
EXEC sp_bindefault N'DEF_ROLEID', N'ROLEID'
GO

-- data type ROLESEQ
EXEC sp_addtype N'ROLESEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_ROLESEQ AS 0
GO
EXEC sp_bindefault N'DEF_ROLESEQ', N'ROLESEQ'
GO

-- data type USERID
EXEC sp_addtype N'USERID', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_USERID AS 0
GO
EXEC sp_bindefault N'DEF_USERID', N'USERID'
GO

-- data type USERSEQ
EXEC sp_addtype N'USERSEQ', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_USERSEQ AS 0
GO
EXEC sp_bindefault N'DEF_USERSEQ', N'USERSEQ'
GO


PRINT 'ibs - rights management'

-- data type RIGHTS
EXEC sp_addtype N'RIGHTS', N'INT', N'NOT NULL'
GO
CREATE DEFAULT DEF_RIGHTS AS 0xffffffff
GO
EXEC sp_bindefault N'DEF_RIGHTS', N'RIGHTS'
GO


PRINT 'ibs - master data management'

-- data type MASTERDATAID
EXEC sp_addtype N'MASTERDATAID', N'INT', N'NULL'
GO
CREATE DEFAULT DEF_MASTERDATAID AS 0
GO
EXEC sp_bindefault N'DEF_MASTERDATAID', N'MASTERDATAID'
GO

-- data type PERSONID
EXEC sp_addtype N'PERSONID', N'INT', N'NULL'
GO
CREATE DEFAULT DEF_PERSONID AS 0
GO
EXEC sp_bindefault N'DEF_PERSONID', N'PERSONID'
GO

-- data type EMAIL
EXEC sp_addtype N'EMAIL', N'NVARCHAR (127)', N'NULL'
GO
CREATE DEFAULT DEF_EMAIL AS null
GO
EXEC sp_bindefault N'DEF_EMAIL', N'EMAIL'
GO

-- data type PHONENUM
EXEC sp_addtype N'PHONENUM', N'NVARCHAR (25)', N'NULL'
GO
CREATE DEFAULT DEF_PHONENUM AS null
GO
EXEC sp_bindefault N'DEF_PHONENUM', N'PHONENUM'
GO

-- data type IPADDRESS
EXEC sp_addtype N'IPADDRESS', N'INTEGER', N'NULL'
GO
CREATE DEFAULT DEF_IPADDRESS AS null
GO
EXEC sp_bindefault N'DEF_IPADDRESS', N'IPADDRESS'
GO

-- data type STOREDPROCNAME
EXEC sp_addtype N'STOREDPROCNAME', N'NVARCHAR (128)', N'NULL'
GO
CREATE DEFAULT DEF_STOREDPROCNAME AS null
GO
EXEC sp_bindefault N'DEF_STOREDPROCNAME', N'STOREDPROCNAME'
GO


-- data type FILENAME
EXEC sp_addtype N'FILENAME', N'NVARCHAR (255)', N'NULL'
GO
CREATE DEFAULT DEF_FILENAME AS null
GO
EXEC sp_bindefault N'DEF_FILENAME', N'FILENAME'
GO

-- set user back
setuser
GO
