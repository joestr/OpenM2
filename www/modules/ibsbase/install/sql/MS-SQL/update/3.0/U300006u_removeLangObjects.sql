/******************************************************************************
 * Description: This file contains all database objects which can be 
 *              removed from the database when the MLI support ist enabled.
 *              This objects are not used anymore after the migration
 *              ibs_token_01, ibs_message_01, ibs_exception_01
 * 
 * Repeatable:  yes
 *
 * @version     $Id: U300006u_removeLangObjects.sql,v 1.1 2010/04/27 15:58:50 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB) 20100427
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- remove objects from the ibs_token_01 table
-- remove procedures from ibs_token_01 table
EXEC p_dropProc N'p_Token_01$new'
GO

EXEC p_dropProc N'p_Token_01$get'
GO

-- remove triggers from the ibs_token_01 table
EXEC p_dropTrig N'TrigTokenInsert'
GO

-- remove ibs_token_01 table
DROP TABLE ibs_Token_01


-- remove objects from the ibs_message_01 table
-- remove procedures from ibs_message_01 table
EXEC p_dropProc N'p_Message_01$new'
GO

-- remove triggers from the ibs_message_01 table
EXEC p_dropTrig N'TrigMessageInsert'
GO

-- remove ibs_message_01 table
DROP TABLE ibs_Message_01


-- remove objects from the ibs_exception_01 table
-- remove procedures from ibs_exception_01 table
EXEC p_dropProc N'p_Exception_01$new'
GO

-- remove triggers from the ibs_exception_01 table
EXEC p_dropTrig N'TrigExceptionInsert'
GO

-- remove ibs_exception_01 table
DROP TABLE ibs_Exception_01


-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO