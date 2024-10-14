/******************************************************************************
 * All tokens within the framework. <BR>
 *
 * @version     $Id: U24026u_createTokens_de.sql,v 1.1 2006/04/11 15:52:20 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060330
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Token_01$new 0, 'TOK_MENU_LEVELSTEP', 'Anzahl Ebenen pro Ladevorgang', 'ibs.obj.menu.MenuTabTokens'
EXEC p_Token_01$new 0, 'TOK_MENU_LEVELSTEPMAX', 'Teilbaum komplett laden ab Ebene', 'ibs.obj.menu.MenuTabTokens'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

