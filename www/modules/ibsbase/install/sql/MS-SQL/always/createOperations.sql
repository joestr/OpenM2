/******************************************************************************
 * All operations within the framework. <BR>
 *
 * @version     $Id: createOperations.sql,v 1.5 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  990419
 ******************************************************************************
 */

-- ä => „, ö => ”, ü => ?, ß => á, Ä => Ž, Ö => ™, Ü => š

-- don't show count messages:
SET NOCOUNT ON
GO

--*****************************************************************************
--** Initialize user independent data                                        **
--*****************************************************************************

-- create Operations:
EXEC p_Operation$new 0x00000001, N'new', N''
EXEC p_Operation$new 0x00000002, N'view', N''
EXEC p_Operation$new 0x00000004, N'read', N''
EXEC p_Operation$new 0x00000008, N'change', N''
EXEC p_Operation$new 0x00000010, N'delete', N''
EXEC p_Operation$new 0x00000020, N'login', N''
EXEC p_Operation$new 0x00000040, N'UNKNOWN', N''
EXEC p_Operation$new 0x00000080, N'UNKNOWN', N''
EXEC p_Operation$new 0x00000100, N'viewRights', N''
EXEC p_Operation$new 0x00000200, N'setRights', N''
EXEC p_Operation$new 0x00000400, N'UNKNOWN', N''
EXEC p_Operation$new 0x00000800, N'UNKNOWN', N''
EXEC p_Operation$new 0x00001000, N'createLink', N''
EXEC p_Operation$new 0x00002000, N'distribute', N''
EXEC p_Operation$new 0x00004000, N'UNKNOWN', N''
EXEC p_Operation$new 0x00008000, N'UNKNOWN', N''
EXEC p_Operation$new 0x00010000, N'UNKNOWN', N''
EXEC p_Operation$new 0x00020000, N'UNKNOWN', N''
EXEC p_Operation$new 0x00040000, N'UNKNOWN', N''
EXEC p_Operation$new 0x00080000, N'UNKNOWN', N''
EXEC p_Operation$new 0x00100000, N'addElem', N''
EXEC p_Operation$new 0x00200000, N'delElem', N''
EXEC p_Operation$new 0x00400000, N'viewElems', N''
EXEC p_Operation$new 0x00800000, N'UNKNOWN', N''
EXEC p_Operation$new 0x01000000, N'viewProtocol', N''
EXEC p_Operation$new 0x02000000, N'UNKNOWN', N''
EXEC p_Operation$new 0x04000000, N'UNKNOWN', N''
EXEC p_Operation$new 0x08000000, N'UNKNOWN', N''
EXEC p_Operation$new 0x10000000, N'UNKNOWN', N''
EXEC p_Operation$new 0x20000000, N'UNKNOWN', N''
EXEC p_Operation$new 0x40000000, N'UNKNOWN', N''

PRINT 'Operations inserted.'

-- show count messages again:
SET NOCOUNT OFF
GO
 
