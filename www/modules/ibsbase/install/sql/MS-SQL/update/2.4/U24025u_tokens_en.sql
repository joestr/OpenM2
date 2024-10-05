/******************************************************************************
 * Update the messages. <BR>
 *
 * @version     $Id: U24025u_tokens_en.sql,v 1.1 2006/04/11 11:13:50 bebucheg Exp $
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

EXEC p_Token_01$new 0,'TOK_DBMAPPED', 'database mapping activated', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORMTYPE', 'form typename', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_UPDATEMAPPING', 'regenerate database mapping table', 'ibs.di.DITokens'
GO
EXEC p_Token_01$new 0,'TOK_VALIDATESTRUCTURE', 'Activate structure validation', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_INVALID_IMPORT_FIELD', 'Invalid field in importfile', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_INVALID_IMPORT_FIELDTYPE', 'Invalid fieldtype in importfile', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORCE_TRANSLATION', 'force translation', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORCE_TRANSLATOR', 'force translator generation', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_FORMTYPECODE', 'form typecode', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_DBMAPPING_TABLE', 'database mapping table', 'ibs.di.DITokens'
EXEC p_Token_01$new 0,'TOK_UPDATEMAPPING_SYSTEM', 'update system values when refreshing database mapping', 'ibs.di.DITokens'
GO


PRINT 'U24026u: english messages updated.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
