/******************************************************************************
 * All messages within the framework. <BR>
 *
 * @version     $Id: U24026v_createMessages_de.sql,v 1.1 2006/04/11 15:52:20 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060330
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Message_01$new 0, 'MSG_LEVELSTEP_DESCRIPTION', 'Der levelStep gibt an, wieviele Ebenen des Menübaumes auf einmal vom Server zum Client geholt werden sollen. Er ist also eine ganze Zahl 0, 1, 2, 3, ... Der Wert 0 bedeutet, dass immer alle Elemente des Menübaumes geholt werden sollen.', 'ibs.obj.menu.MenuMessages'
EXEC p_Message_01$new 0, 'MSG_LEVELSTEPMAX_DESCRIPTION', 'Der levelStepMax gibt an, bis zu welcher Ebene der Baum entsprechend dem levelStep geholt werden soll. Ab dieser Ebene wird dann der gesamte darunterliegende Teilbaum auf einmal vom Server geholt. Der levelStepMax ist also eine ganze Zahl 0, 1, 2, 3, ... Der Wert 0 bedeutet, dass immer alle Elemente des Menübaumes geholt werden sollen.', 'ibs.obj.menu.MenuMessages'

GO
-- show count messages again:
SET NOCOUNT OFF
GO

