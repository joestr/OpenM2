/******************************************************************************
 * Update the exception messages. <BR>
 *
 * @version     $Id: U008v_exceptions_german.sql,v 1.2 2004/05/21 13:36:17 klaus Exp $
 *
 * @author      Klaus Reim�ller (KR)  040425
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

EXEC p_Exception_01$new 0,'E_OBJECTNOTFOUNDEXCEPTION', 'Objekt nicht gefunden: "<name>".', 'ibs.util.UtilExceptions'
EXEC p_Exception_01$new 0,'E_NAMEALREADYGIVENEXCEPTION', 'Es gibt schon ein Objekt mit diesem Namen: "<name>".', 'ibs.util.UtilExceptions'
EXEC p_Exception_01$new 0,'EXC_WRONGFIELDTYPE', 'Der Suchfeldtyp f�r dieses Feld ist nicht g�ltig.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_WRONGCOLUMNTYPE', 'Ausnahme bei Spaltentypsyntax.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_INCORRECTOIDEXCEPTION', 'Ung�ltige Oid im Suchfeld', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_COLUMNTYPENOTEXIST', 'Unbekannter Spaltentyp.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_NOOID', 'Oid f�r button nicht gesetzt.', 'ibs.obj.query.QueryConstants'
EXEC p_Exception_01$new 0,'EXC_NOJAVASCRIPT', 'Javascript f�r Button nicht gesetzt.', 'ibs.obj.query.QueryConstants'
GO

PRINT 'U008v: german exception messages updated.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
