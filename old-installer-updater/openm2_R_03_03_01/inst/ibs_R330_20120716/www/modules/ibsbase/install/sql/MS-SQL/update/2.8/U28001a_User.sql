/******************************************************************************
 * Task:        TASK/BUG#IBS-154 - Reset user password
 *
 * Description: This file contains and update script for adding the new column
 *              changePwd to the table ibs_user.
 * 
 *              A custom script is used here since the standard table update
 *              script leads to problems with the rights table when copying
 *              users later on. 
 *
 * Repeatable:  yes
 *
 * @version     $Id: U28001a_User.sql,v 1.1 2009/02/13 18:35:21 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 090206
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- check if colum exists and add new column if not
IF NOT EXISTS (
	SELECT * FROM INFORMATION_SCHEMA.COLUMNS
	WHERE TABLE_NAME='ibs_user'
		AND COLUMN_NAME='changePwd')
BEGIN
    ALTER TABLE ibs_User
    ADD changePwd BOOL NOT NULL DEFAULT (0)
END

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO