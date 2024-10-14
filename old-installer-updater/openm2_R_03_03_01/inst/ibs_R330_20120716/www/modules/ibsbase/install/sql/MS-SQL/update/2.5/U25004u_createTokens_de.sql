/******************************************************************************
 * All tokens within the framework. <BR>
 *
 * @version     $Id: U25004u_createTokens_de.sql,v 1.1 2008/06/02 14:00:13 ctran Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20080416
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Token_01$new 0,'TOK_FIELDNAME', 'Name des Feldes', 'ibs.bo.BOTokens'
EXEC p_Token_01$new 0,'TOK_OLD_VALUE', 'Alter Wert des Feldes', 'ibs.bo.BOTokens'
EXEC p_Token_01$new 0,'TOK_VALUE', 'Neuer Wert des Feldes', 'ibs.bo.BOTokens'

GO

-- show count messages again:
SET NOCOUNT OFF
GO