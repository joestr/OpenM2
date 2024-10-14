/******************************************************************************
 * Task:        IBS-732 ibsbase stored procedure 'p_Workspace$assignStdObjects' 
 *              is overwritten within m2doc module
 *
 * Description: This file updates the ibs_keymapper table and add missing
 *              entries for 'user profile' and 'hotlist' of users.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U310001u_updateWspKeyMapperEntries.sql,v 1.2 2011/12/07 16:21:47 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB) 20111207
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
BEGIN TRANSACTION
GO

-- define local variables
DECLARE 
	@l_profile_usrname NAME,
	@l_profile_o OBJECTID,
	@l_profile_s OBJECTIDSTRING,
	@l_extIdProfile NVARCHAR(255), 
	@l_hotlist_usrname NAME,                
	@l_hotlist_o OBJECTID,              
	@l_hotlist_s OBJECTIDSTRING,
	@l_extIdHotlist NVARCHAR(255),
	@l_printMessage NVARCHAR(255)
BEGIN           
    PRINT N'Start updating user profile ext keys'
    DECLARE profileCursor CURSOR FOR
	    SELECT wrk.profile as profileOid, 
	           usr.name as usrName
	    FROM ibs_user usr, 
	         ibs_Workspace wrk
	    WHERE usr.id = wrk.userid
	      AND wrk.profile not in (SELECT keymap.oid FROM ibs_keymapper keymap)
        
    -- open the cursor:
    OPEN    profileCursor

    -- get the first object:
    FETCH NEXT FROM profileCursor INTO @l_profile_o, @l_profile_usrname

    -- loop through all objects:
    WHILE (@@FETCH_STATUS <> -1)        -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted
        -- during the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
			-- create key mapper entries for user profile:
			SELECT  @l_profile_s = dbo.f_byteToString (@l_profile_o)
			SELECT  @l_extIdProfile = N'wsp_userprofile_' + @l_profile_usrname
			SET @l_printMessage = N'Update user profile ext key for user: ' + @l_profile_usrname + N'(UserProfile: ' + @l_profile_s + ')'
			PRINT @l_printMessage
			EXEC p_KeyMapper$new @l_profile_s, @l_extIdProfile, N'ibs_instobj'
        END -- if
        -- get next tuple:
        FETCH NEXT FROM profileCursor INTO @l_profile_o, @l_profile_usrname
    END -- while another tuple found

    -- close the not longer needed cursor:
    CLOSE profileCursor
    DEALLOCATE profileCursor
    PRINT N'Finished updating user profile ext keys'

    PRINT N'Start updating hotlist ext keys'
    DECLARE hotListCursor CURSOR FOR
	    SELECT wrk.hotList as hotListOid,
	           usr.name as usrName
	    FROM ibs_user usr, 
	         ibs_Workspace wrk
	    WHERE usr.id = wrk.userid
	      AND wrk.hotList not in (SELECT keymap.oid FROM ibs_keymapper keymap)

    -- open the cursor:
    OPEN    hotListCursor

    -- get the first object:
    FETCH NEXT FROM hotListCursor INTO @l_hotlist_o, @l_hotlist_usrname

    -- loop through all objects:
    WHILE (@@FETCH_STATUS <> -1)        -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted
        -- during the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
			-- create key mapper entries for hotlist:
			SELECT  @l_hotlist_s = dbo.f_byteToString (@l_hotlist_o)
			SELECT  @l_extIdHotlist = N'wsp_hotlist_' + @l_hotlist_usrname
			SET @l_printMessage = N'Update hotlist ext key for user:' + @l_hotlist_usrname + N'(HotList: ' + @l_hotlist_s + ')'
			PRINT @l_printMessage
			EXEC p_KeyMapper$new @l_hotlist_s, @l_extIdHotlist, N'ibs_instobj'
        END -- if
        -- get next tuple:
        FETCH NEXT FROM hotListCursor INTO @l_hotlist_o, @l_hotlist_usrname
    END -- while another tuple found

    -- close the not longer needed cursor:
    CLOSE hotListCursor
    DEALLOCATE hotListCursor
        
    PRINT N'Finished updating user hotlist ext keys'     
END
GO

COMMIT TRANSACTION
-- show count messages again:
SET NOCOUNT OFF
GO
PRINT '$RCSFile$: finished.'
GO