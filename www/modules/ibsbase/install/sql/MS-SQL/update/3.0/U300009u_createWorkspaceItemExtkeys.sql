/******************************************************************************
 * Task:        IBS-463 m2ml - MLI Java Script - Migrate current Objects texts
 *
 * Description: This file creates key mapper entries for all existing
 *              user profile workspace and hotlist workspace objects.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U300009u_createWorkspaceItemExtkeys.sql,v 1.1 2010/05/21 10:05:35 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20100521
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
BEGIN TRANSACTION
GO

-- declare variables:
DECLARE
    -- local variables:
    @l_extIdProfile         NAME,           -- name of ext if for profile
	@l_extIdHotlist         NAME,           -- name of ext if for hotlist
    @l_userName             NAME,           -- user name
    @l_workspaceOid         OBJECTID,       -- oid of workspace
	@l_profile		        OBJECTID,       -- oid of profile
	@l_hotlist              OBJECTID,       -- oid of hotlist
    @l_profile_s            OBJECTIDSTRING, -- string representation of profile oid
    @l_hotlist_s            OBJECTIDSTRING  -- string representation of hotlist oid

-- body:
        -- remove all existing key mapper entries first
        DELETE FROM ibs_keymapper where id like 'wsp_userprofile_%'
        DELETE FROM ibs_keymapper where id like 'wsp_hotlist_%'

        -- get all workspaces
        -- define cursor:
        DECLARE Workspace_Cursor CURSOR FOR 
            SELECT  oid
            FROM    ibs_object o
            WHERE   o.typecode = 'Workspace'

        -- open the cursor:
        OPEN    Workspace_Cursor

        -- get the first workspace:
        FETCH NEXT FROM Workspace_Cursor INTO @l_workspaceOid

        -- loop through all found tupels:
        WHILE (@@FETCH_STATUS <> -1)            -- another user found?
        BEGIN
            -- Because @@FETCH_STATUS may have one of the three values
            -- -2, -1, or 0 all of these cases must be checked.
            -- In this case the tuple is skipped if it was deleted during
            -- the execution of this procedure.
            IF (@@FETCH_STATUS <> -2)
            BEGIN
                -- retrieve the user name for the workspace               
                SELECT  @l_userName = u.name
                FROM    ibs_workspace w, ibs_user u
                WHERE   workspace = @l_workspaceOid
                        AND w.userId = u.id
            
                -- get the oids for user profile and hotlist
                SELECT  @l_profile = w.profile, @l_hotlist = w.hotlist
                FROM    ibs_Workspace w
                WHERE   w.workspace = @l_workspaceOid
                    
                -- compute ext ids    
                SELECT  @l_extIdProfile = N'wsp_userprofile_' + @l_userName
                SELECT  @l_extIdHotlist = N'wsp_hotlist_' + @l_userName
                    
				-- create string reprentations of oids   
                SELECT  @l_profile_s = dbo.f_byteToString(@l_profile)
                SELECT  @l_hotlist_s = dbo.f_byteToString(@l_hotlist)
            
                -- create key mapper for workspace:
                EXEC p_KeyMapper$new @l_profile_s, @l_extIdProfile, N'ibs_instobj'
                EXEC p_KeyMapper$new @l_hotlist_s, @l_extIdHotlist, N'ibs_instobj'

            END -- if
            -- get next tupel:
            FETCH NEXT FROM Workspace_Cursor INTO @l_workspaceOid
        END -- while another user found

        CLOSE Workspace_Cursor
        DEALLOCATE Workspace_Cursor
GO

COMMIT TRANSACTION
-- show count messages again:
SET NOCOUNT OFF
GO
PRINT '$RCSFile$: finished.'
GO