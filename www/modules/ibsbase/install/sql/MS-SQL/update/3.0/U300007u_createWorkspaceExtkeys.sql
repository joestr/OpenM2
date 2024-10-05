/******************************************************************************
 * Task:        IBS-467 The application header is not internationalized
 *              correctly
 *
 * Description: This file creates key mapper entries for all existing workspace
 *              instances.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U300007u_createWorkspaceExtkeys.sql,v 1.3 2010/05/21 10:05:49 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20100510
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
    @l_extid                NAME,           -- name of ext key
    @l_workspaceOid         OBJECTID,       -- oid of workspace
    @l_workspaceOid_s       OBJECTIDSTRING  -- string representation of workspace oid

-- body:
        -- remove all existing key mapper entries first
        DELETE FROM ibs_keymapper where id like 'wsp_root_%'

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
                -- retrieve the ext id for the workspace based on the username               
                SELECT  @l_extid = N'wsp_root_' + u.name
                FROM    ibs_workspace w, ibs_user u
                WHERE   workspace = @l_workspaceOid
                        AND w.userId = u.id
            
                SELECT  @l_workspaceOid_s = dbo.f_byteToString(@l_workspaceOid)
            
                -- create key mapper for workspace:
                EXEC p_KeyMapper$new @l_workspaceOid_s, @l_extid, N'ibs_instobj'

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