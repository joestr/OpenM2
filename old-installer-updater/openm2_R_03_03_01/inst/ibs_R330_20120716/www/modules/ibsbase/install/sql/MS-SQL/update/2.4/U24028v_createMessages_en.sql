/******************************************************************************
 * All messages within the ibs framework. <BR>
 *
 * @version     $Id: U24028v_createMessages_en.sql,v 1.1 2006/10/17 16:48:12 bebucheg Exp $
 *
 * @author      Klaus Reimüller (KR) 20060330
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_Message_01$new 0, 'MSG_CREATE_OBJECT_IN_ALTERNATIVE_PATH', 'Create object using alternative path: <name>', 'ibs.bo.BOMessages'

GO
-- show count messages again:
SET NOCOUNT OFF
GO

