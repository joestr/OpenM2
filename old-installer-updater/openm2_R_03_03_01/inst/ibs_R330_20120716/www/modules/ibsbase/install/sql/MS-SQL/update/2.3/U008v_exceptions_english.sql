/******************************************************************************
 * Update the exception messages. <BR>
 *
 * @version     $Id: U008v_exceptions_english.sql,v 1.2 2004/05/21 13:36:17 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  040425
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

-- delete old exceptions:
DELETE  ibs_Exception_01
WHERE   languageId = 0
    AND name = 'EXC_INCORRECTOIDEXCEPTION'
    AND className = 'ibs.query.QueryConstants'
GO

EXEC p_Exception_01$new 0,'E_OBJECTNOTFOUNDEXCEPTION', 'Object not found: "<name>".', 'ibs.util.UtilExceptions'
EXEC p_Exception_01$new 0,'E_NAMEALREADYGIVENEXCEPTION', 'An Object with this name already exists: "<name>".', 'ibs.util.UtilExceptions'
EXEC p_Exception_01$new 0,'EXC_WRONGFIELDTYPE', 'The searchfieldtype for this field is not valid.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_WRONGCOLUMNTYPE', 'Exception at columntype-syntax.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_INCORRECTOIDEXCEPTION', 'Invalid oid in searchfield.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_COLUMNTYPENOTEXIST', 'Unknown columntype.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_NOOID', 'No oid set for button.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_NOJAVASCRIPT', 'No javascript set for button.', 'ibs.obj.query.QueryConstants'
GO

PRINT 'U008v: english exception messages updated.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
