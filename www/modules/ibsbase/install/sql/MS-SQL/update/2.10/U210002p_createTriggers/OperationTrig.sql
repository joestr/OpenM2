/******************************************************************************
 * The triggers for the ibs operations table. <BR>
 *
 * @version     $Id: OperationTrig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Klaus Reim�ller (KR)    980528
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigOperationInsert'
GO


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigOperationUpdate'
GO


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigOperationDelete'
GO


PRINT 'Trigger f�r Tabelle ibs_Operation angelegt'
GO
