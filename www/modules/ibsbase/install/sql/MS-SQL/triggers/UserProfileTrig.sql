/******************************************************************************
 * The triggers for the ibs UserProfile table. <BR>
 *
 * @version     $Id: UserProfileTrig.sql,v 1.5 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB)  980709
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigUserProfileInsert'
GO

-- create the trigger:
/*
CREATE TRIGGER TrigUserProfileInsert ON ibs_UserProfile
FOR INSERT
AS
*/
GO
-- TrigUserProfileInsert


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigUserProfileUpdate'
GO

-- create the trigger:
/*
CREATE TRIGGER TrigUserProfileUpdate ON ibs_UserProfile
FOR INSERT
AS
*/
GO
-- TrigUserProfileUpdate


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigUserProfileDelete'
GO

-- create the trigger:
/*
CREATE TRIGGER TrigUserProfileDelete ON ibs_UserProfile
FOR INSERT
AS
*/
GO
-- TrigUserProfileDelete


PRINT 'Trigger für Tabelle ibs_UserProfile angelegt'
GO
