/******************************************************************************
 * Update the messages. <BR>
 *
 * @version     $Id: U24025u_tokens_de.sql,v 1.1 2006/04/11 11:13:50 bebucheg Exp $
 *
 * @author      Klaus Reimüller (KR)  040425
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

-- delete old tokens:
DELETE  ibs_Token_01
WHERE   languageId = 0
    AND classname = 'ibs.di.DITokens'
    AND name IN ('TOK_DBMAPPED','TOK_FORMTYPE','TOK_UPDATEMAPPING')
GO

EXEC p_Token_01$new 0,'TOK_DBMAPPED', 'Datenbank Mapping aktiviert', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORMTYPE', 'Formular Typname', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_UPDATEMAPPING', 'Datenbank Mapping Tabelle neu erstellen', 'ibs.di.DITokens'
GO
EXEC p_Token_01$new 0,'TOK_VALIDATESTRUCTURE', 'Strukturüberprüfung aktivieren', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_INVALID_IMPORT_FIELD', 'Ungültiges Feld in der Importdatei', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_INVALID_IMPORT_FIELDTYPE', 'Ungültiger Feldtyp in der Importdatei', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORCE_TRANSLATION', 'Translation erzwingen', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORCE_TRANSLATOR', 'Translator generieren erzwingen', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORMTYPECODE', 'Formular Typcode', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_DBMAPPING_TABLE', 'Datenbank Mapping Tabelle', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_UPDATEMAPPING_SYSTEM', 'System Werte beim Datenbank Mapping aktualisieren', 'ibs.di.DITokens'
GO


PRINT 'U24025u: german tokens updated.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
