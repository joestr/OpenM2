/******************************************************************************
 * All tokens within the framework. <BR>
 *
 * @version     $Id: U25004v_createTokens_en.sql,v 1.1 2008/06/02 14:00:13 ctran Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20080416
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Token_01$new 0,'TOK_FIELDNAME', 'field name', 'ibs.bo.BOTokens'
EXEC p_Token_01$new 0,'TOK_OLD_VALUE', 'old field value', 'ibs.bo.BOTokens'
EXEC p_Token_01$new 0,'TOK_VALUE', 'new field value', 'ibs.bo.BOTokens'

GO

-- show count messages again:
SET NOCOUNT OFF
GO