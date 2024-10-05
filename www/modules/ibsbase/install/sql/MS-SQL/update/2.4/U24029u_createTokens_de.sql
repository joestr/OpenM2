/******************************************************************************
 * Tokens additions and updates. <BR>
 *
 * @version     $Id: U24029u_createTokens_de.sql,v 1.1 2006/10/17 16:45:58 bebucheg Exp $
 *
 * @author      Bernd Buchegger (BB) 20060817
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Token_01$new 0, 'TOK_SEARCH', 'Suchen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_NEW_SEARCH', 'Neue Suche', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_SAVE', 'Speichern', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_OPEN_REPORT', 'Report öffnen...', 'ibs.obj.query.QueryTokens'

EXEC p_Token_01$new 0, 'TOK_SAVE_TITLE', 'Suchergebnis als TXT Datei speichern', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_OPEN_REPORT_TITLE', 'Suchergebnis in Reporting Engine öffnen', 'ibs.obj.query.QueryTokens'

GO

-- show count messages again:
SET NOCOUNT OFF
GO

