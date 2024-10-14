/******************************************************************************
 * Tokens additions and updates. <BR>
 *
 * @version     $Id: U26003u_createTokens_de.sql,v 1.1 2008/07/17 12:11:17 bbuchegger Exp $
 *
 * @author      Bernd Buchegger (BB) 20080617
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Token_01$new 0, 'TOK_QUERY', 'Query', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_QUERYTYPE', 'Verwendung für', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_COLUMNHEADERS', 'Ergebnisfeldnamen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_COLUMNATTRIBUTES', 'Ergebnisfeldattribute', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_COLUMNTYPES', 'Ergebnisfeldtypen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_FIELDNAMES', 'Suchfeldnamen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_FIELDATTRIBUTES', 'Suchfeldattribute', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_FIELDTYPES', 'Suchfeldtypen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_MAX_RESULTS', 'Obergrenze Suchergebnis', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_SHOW_QUERY', 'Query anzeigen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_CATEGORY', 'Kategorie', 'ibs.obj.query.QueryTokens'
GO
EXEC p_Token_01$new 0, 'TOK_OPEN_QUERY_DEFINITION', 'Query-Definition öffnen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_OPEN_QUERY', 'Query öffnen', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_EXPORT_QUERY', 'Query exportieren', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_SELECTED', 'Ausgewählt', 'ibs.bo.BOTokens'
EXEC p_Token_01$new 0, 'TOK_TIP_USE_MULTISELECTION', 'Mehrfachauswahl durch SHIFT+Klick bzw. STRG+Klick', 'ibs.bo.BOTokens'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

