/******************************************************************************
 * Task:        TASK FAC02 - SAP
 *              FAC050717_1 Recreation of already deleted user fails.
 *
 * Description: The problem is, that the workspaces of users where not deleted
 *              during deletion of the users. So the objects with keymapping ids
 *              containing the user name still existed, and it was not possible
 *              to create a new workspace for another user with the same user
 *              name.
 *              This script ensures that all existing workspaces are consistent
 *              and that workspaces of deleted users are also deleted.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24017u_wspCleanup.sql,v 1.1 2005/07/27 15:33:57 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20050717
 ******************************************************************************
 */ 

-- ensure correct entries for all active users:
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- default value for no defined oid

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_msg                  VARCHAR (2000), -- output message
    @l_userOid              OBJECTID,       -- oid of actual user
    @l_userOid_s            OBJECTIDSTRING  -- oid of actual user as string

-- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000

-- initialize local variables:
SELECT
    @l_file                 = 'U00002u',
    @l_retValue             = @c_ALL_RIGHT,
    @l_error                = 0

-- body:

    ---------------------------------------------------------------------------
    -- Assign workspace objects to all workspaces of active users which do not
    -- already have the objects assigned.
    DECLARE objCursor INSENSITIVE CURSOR FOR
        SELECT  u.oid
        FROM    ibs_User u, ibs_Workspace wsp
        WHERE   u.state = 2
            AND u.domainId <> 0
            AND wsp.userId = u.id
            AND wsp.workBox = @c_NOOID

    BEGIN
        -- insert the workspace objects:
        OPEN objCursor
        --
        -- get first user oid:
        FETCH NEXT FROM objCursor INTO @l_userOid
        --
        WHILE (@@FETCH_STATUS = 0)
        BEGIN
            -- convert oid into string representation:
            EXEC p_byteToString @l_userOid, @l_userOid_s OUTPUT

            -- assign the workspace objects:
            EXEC @l_retValue = p_Workspace$assignStdObjects @l_userOid_s

            -- get next user oid:
            FETCH NEXT FROM objCursor INTO @l_userOid
        END -- while

        -- dump cursor structures
        CLOSE objCursor
        DEALLOCATE objCursor
    END

    SELECT  @l_msg = @l_file + ': Objects assigned to workspaces.'
    PRINT @l_msg

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos,
        '', 0,
        '', ''
    SELECT  @l_msg = @l_file + ': Error when assigning objects to workspaces:'
    PRINT @l_msg
    SELECT  @l_msg = 'Error ' + CONVERT (VARCHAR, @l_error) +
            '; position: ' + @l_ePos
    PRINT @l_msg

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': workspace assigning finished.'
    PRINT @l_msg
GO

-- just a check: get all users which do not have correct objects assigned to their workspaces
PRINT 'Get all users which do not have correct objects assigned to their workspaces. These should be none.'
SELECT  '' AS usersWithInconsistentWorkspaces,
        u.state, owsp.state, u.oid AS userOid, u.name AS userName
FROM    ibs_User u, ibs_Workspace wsp, ibs_Object owsp
WHERE   u.state = 2
    AND u.domainId <> 0
    AND wsp.userId = u.id
    AND wsp.workBox = 0x0000000000000000
    AND wsp.workspace = owsp.oid
GO


-- ensure that workspaces of deleted users are also deleted:
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_msg                  VARCHAR (2000), -- output message
    @l_userId               USERID,         -- id of user who deletes the
                                            -- workspaces
    @l_workspaceOid         OBJECTID,       -- oid of actual workspace
    @l_workspaceOid_s       OBJECTIDSTRING  -- oid of actual workspace as string

-- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

-- initialize local variables:
SELECT
    @l_file                 = 'U00002u',
    @l_retValue             = @c_ALL_RIGHT,
    @l_error                = 0

-- body:
    ---------------------------------------------------------------------------
    -- Delete all active workspaces which belong to deleted users.
    DECLARE objCursor INSENSITIVE CURSOR FOR
        SELECT  wsp.workspace, dom.adminId
        FROM    ibs_User u, ibs_Workspace wsp, ibs_Object owsp,
                ibs_Domain_01 dom
        WHERE   u.state IN (1, 4)
            AND u.domainId <> 0
            AND wsp.userId = u.id
            AND owsp.oid = wsp.workspace
            AND owsp.state = 2
            AND dom.id = u.domainId

    BEGIN
        -- insert the target and the container of the copied object
        OPEN objCursor
        --
        -- get first object to be deleted:
        FETCH NEXT FROM objCursor INTO @l_workspaceOid, @l_userId
        --
        WHILE (@@FETCH_STATUS = 0)
        BEGIN
            -- convert oid into string representation:
            EXEC p_byteToString @l_workspaceOid, @l_workspaceOid_s OUTPUT

            -- delete the workspace:
            EXEC @l_retValue = p_Object$delete @l_workspaceOid_s, @l_userId, 0

            -- get next object to be deleted:
            FETCH NEXT FROM objCursor INTO @l_workspaceOid, @l_userId
        END -- while

        -- dump cursor structures
        CLOSE objCursor
        DEALLOCATE objCursor
    END

    SELECT  @l_msg = @l_file + ': Workspaces deleted.'
    PRINT @l_msg

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos,
        '', 0,
        '', ''
    SELECT  @l_msg = @l_file + ': Error when deleting workspaces:'
    PRINT @l_msg
    SELECT  @l_msg = 'Error ' + CONVERT (VARCHAR, @l_error) +
            '; position: ' + @l_ePos
    PRINT @l_msg

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': workspace deletion finished.'
    PRINT @l_msg
GO

-- just a check: get all active workspace objects which belong to deleted users
PRINT 'Get all active workspace objects which belong to deleted users. These should be none.'
SELECT  '' AS workspacesToDelete,
        u.name AS userName, wsp.workspace AS workspaceOid
FROM    ibs_User u, ibs_Workspace wsp, ibs_Object owsp
WHERE   u.state IN (1, 4)
    AND u.domainId <> 0
    AND wsp.userId = u.id
    AND owsp.oid = wsp.workspace
    AND owsp.state = 2
GO
