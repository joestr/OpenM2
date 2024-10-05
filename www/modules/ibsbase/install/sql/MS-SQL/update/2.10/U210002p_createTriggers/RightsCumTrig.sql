/******************************************************************************
 * The triggers for the ibs rights cum table. <BR>
 *
 * @version     $Id: RightsCumTrig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  990304
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigRightsCumInsert'
GO
-- TrigRightsCumInsert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigRightsCumUpdate'
GO
-- TrigRightsCumUpdate


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigRightsCumDelete'
GO
-- TrigRightsCumDelete


PRINT 'Trigger für Tabelle ibs_RightsCum angelegt'
GO
