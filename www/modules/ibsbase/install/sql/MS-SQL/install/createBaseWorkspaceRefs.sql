/******************************************************************************
 * Check and update necessary workspace references from of users. <BR>
 *
 * @version     $Id: createBaseWorkspaceRefs.sql,v 1.1 2011/10/18 14:53:08 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB)  20110509
 ******************************************************************************
 */
-- don't show count messages:
SET NOCOUNT ON
GO

-- locale variable
DECLARE
    @l_userOid_Admin            OBJECTID,        -- oid of user 'Administrator'
    @l_userOid_Admin_s          OBJECTIDSTRING,  -- oid of user 'Administrator' as string
    @l_userOid_SysAdmin         OBJECTID,        -- oid of user 'SysAdmin'
    @l_userOid_SysAdmin_s       OBJECTIDSTRING,  -- oid of user 'SysAdmin' as string
    @l_userOid_Debug            OBJECTID,        -- oid of user 'Debug'
    @l_userOid_Debug_s          OBJECTIDSTRING   -- oid of user 'Debug' as string

-- assign the workspace objects 'Administrator':
SELECT @l_userOid_Admin = oid FROM ibs_user WHERE id = 25165825
EXEC p_byteToString @l_userOid_Admin, @l_userOid_Admin_s OUTPUT
EXEC p_Workspace$assignStdObjects @l_userOid_Admin_s

-- assign the workspace objects 'SysAdmin':
SELECT @l_userOid_SysAdmin = oid FROM ibs_user WHERE id = 25165826
EXEC p_byteToString @l_userOid_SysAdmin, @l_userOid_SysAdmin_s OUTPUT
EXEC p_Workspace$assignStdObjects @l_userOid_SysAdmin_s

-- assign the workspace objects 'Debug':
SELECT @l_userOid_Debug = oid FROM ibs_user WHERE id = 25165827
EXEC p_byteToString @l_userOid_Debug, @l_userOid_Debug_s OUTPUT
EXEC p_Workspace$assignStdObjects @l_userOid_Debug_s

-- show count messages again:
SET NOCOUNT OFF
GO