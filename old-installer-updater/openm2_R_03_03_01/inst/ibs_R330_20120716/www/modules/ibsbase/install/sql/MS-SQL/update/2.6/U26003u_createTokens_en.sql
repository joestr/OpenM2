/******************************************************************************
 * Tokens additions and updates. <BR>
 *
 * @version     $Id: U26003u_createTokens_en.sql,v 1.1 2008/07/17 12:11:17 bbuchegger Exp $
 *
 * @author      Bernd Buchegger (BB) 20080617
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Token_01$new 0, 'TOK_QUERY', 'query', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_QUERYTYPE', 'use Query for', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_COLUMNHEADERS', 'columnnames', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_COLUMNATTRIBUTES', 'columnattributes', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_COLUMNTYPES', 'columntypes', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_FIELDNAMES', 'searchfields', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_FIELDATTRIBUTES', 'searchfieldattributes', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_FIELDTYPES', 'searchfieldtypes', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_MAX_RESULTS', 'maximal results', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_SHOW_QUERY', 'show Query', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_CATEGORY', 'category', 'ibs.obj.query.QueryTokens'
GO
EXEC p_Token_01$new 0, 'TOK_OPEN_QUERY_DEFINITION', 'open query definition', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_OPEN_QUERY', 'open query', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_EXPORT_QUERY', 'export query', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_SELECTED', 'selected', 'ibs.bo.BOTokens'
EXEC p_Token_01$new 0, 'TOK_TIP_USE_MULTISELECTION', 'multiselect with SHIFT+click or STRG+click', 'ibs.bo.BOTokens'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

