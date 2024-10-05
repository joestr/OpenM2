/******************************************************************************
 * All messages within the ibs framework. <BR>
 *
 * @version     $Id: U24026v_createMessages_en.sql,v 1.1 2006/04/11 15:52:20 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060330
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Message_01$new 0, 'MSG_LEVELSTEP_DESCRIPTION', 'The levelStep defines how many levels of the menu tree shall be loaded at once from server to client. It is an integer 0, 1, 2, 3, ... The value 0 means always to get all elements of the menu tree.', 'ibs.obj.menu.MenuMessages'
EXEC p_Message_01$new 0, 'MSG_LEVELSTEPMAX_DESCRIPTION', 'The levelStepMax defines upto which level the levelStep is used for getting the elements of the menu tree. Starting at this level the complete partial tree below is fetched at once from the server. levelStepMax is an integer 0, 1, 2, 3, ... The value 0 means always to get all elements of the menu tree.', 'ibs.obj.menu.MenuMessages'

GO
-- show count messages again:
SET NOCOUNT OFF
GO

