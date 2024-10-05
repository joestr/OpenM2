/******************************************************************************
 * Task:        BUG#2046 - Wrong rights aliases after finishing of workflow.
 *
 * Description: Changed rights entries for rights mapping.
 *
 * Repeatable:  yes
 *
 * @version     2.30.0001, 05.09.2002 KR
 *
 * @author      Klaus Reimüller (KR) 020905
 ******************************************************************************
 */ 

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:

    -- local variables:
    @l_file                 VARCHAR (5),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_msg                  VARCHAR (255)   -- the actual message

-- assign constants:

-- initialize local variables:
SELECT
    @l_file = 'U001u',
    @l_error = 0

-- body:
    -- delete all entries in ibs_RightsMapping
    DELETE ibs_RightsMapping

    -- create rights entries: rights are kind of hierarchical
    INSERT  ibs_RightsMapping VALUES  ('READ', 'READ')
    INSERT  ibs_RightsMapping VALUES  ('READ', 'VIEW')
    INSERT  ibs_RightsMapping VALUES  ('READ', 'VIEWELEMS')

    INSERT  ibs_RightsMapping VALUES  ('CREATE', 'READ')
    INSERT  ibs_RightsMapping VALUES  ('CREATE', 'VIEW')
    INSERT  ibs_RightsMapping VALUES  ('CREATE', 'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  ('CREATE', 'NEW')
    INSERT  ibs_RightsMapping VALUES  ('CREATE', 'ADDELEM')

    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'READ')
    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'VIEW')
    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'NEW')
    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  ('CHANGE', 'CHANGE')

    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'READ')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'VIEW')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'NEW')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'CHANGE')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DELETE')
    INSERT  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DELELEM')

    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'READ')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEW')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'NEW')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'CHANGE')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DELETE')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DELELEM')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWRIGHTS')
--    no setrights allowed for workflow-users
--    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'SETRIGHTS')
    INSERT  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWPROTOCOL')

    INSERT  ibs_RightsMapping VALUES  ('ALL', 'READ')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'VIEW')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'VIEWELEMS')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'CREATELINK')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'DISTRIBUTE')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'NEW')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'ADDELEM')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'CHANGE')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'DELETE')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'DELELEM')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'VIEWRIGHTS')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'SETRIGHTS')
    INSERT  ibs_RightsMapping VALUES  ('ALL', 'VIEWPROTOCOL')

    -- jump to end of code block:
    GOTO finish

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished'
    PRINT @l_msg
GO

-- show count messages again:
SET NOCOUNT OFF
GO
