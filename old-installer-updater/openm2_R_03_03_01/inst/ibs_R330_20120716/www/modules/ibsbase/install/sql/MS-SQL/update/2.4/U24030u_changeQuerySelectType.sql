/******************************************************************************
 * Task:        TASK TRI060306_01 - ClassCastException in MenuTab
 *
 * Description: The object types Document and QuerySelectContainer where both
 *              created with the same type id.
 *              We have to ensure uniqueness of these types.
 *              Thus the object type Document (which is created with a fixed
 *              type id of 0x01010100) stays with the original type id. The
 *              data have to be updated.
 *              The object type QuerySelectContainer has to be newly created.
 *              So it getss a new type id.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24030u_changeQuerySelectType.sql,v 1.1 2006/12/22 02:07:14 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR) 20061118
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- change data of Document object type:
UPDATE ibs_Type
SET code = 'Document'
WHERE id = 0x01010100
GO

-- change data of type version of Document:
UPDATE ibs_TVersion
SET code = 'Document_01'
WHERE typeId = 0x01010100
GO

-- re-create QuerySelectContainer:
EXEC p_Type$newLang 0, 'Container', 1, 1, 0, 0, 0, 'QuerySelectContainer',
    'ibs.obj.menu.QuerySelectContainer_01', 0,
    'TN_QuerySelectContainer_01'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
