/******************************************************************************
 * Tokens additions and updates. <BR>
 *
 * @version     $Id: U24029u_createTokens_en.sql,v 1.1 2006/10/17 16:45:58 bebucheg Exp $
 *
 * @author      Bernd Buchegger (BB) 20060817
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Token_01$new 0, 'TOK_SEARCH', 'search', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_NEW_SEARCH', 'new sarch', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_SAVE', 'save', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_OPEN_REPORT', 'open report...', 'ibs.obj.query.QueryTokens'

EXEC p_Token_01$new 0, 'TOK_SAVE_TITLE', 'save search result as TXT file', 'ibs.obj.query.QueryTokens'
EXEC p_Token_01$new 0, 'TOK_OPEN_REPORT_TITLE', 'open search result in reporting engine', 'ibs.obj.query.QueryTokens'

GO

-- show count messages again:
SET NOCOUNT OFF
GO

