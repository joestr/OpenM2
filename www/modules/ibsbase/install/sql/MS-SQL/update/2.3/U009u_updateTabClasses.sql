/******************************************************************************
 * Task:        Migration from m2 R.23 to m2 R2.4.
 *
 * Description: Ensure that the classes which implement standard tabs are
 *              correct.
 *
 * Repeatable:  yes
 *
 * @version     $id$
 *
 * @author      Klaus Reimüller (KR) 19.05.2004
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
    @l_file = 'U009u',
    @l_error = 0

-- body:
    -- update the tabs:
    -- rights tab:
    UPDATE  ibs_Tab
    SET     class = 'ibs.obj.user.RightsContainer_01'
    WHERE   class = 'ibs.user.RightsContainer_01'

    -- log tab:
    UPDATE  ibs_Tab
    SET     class = 'ibs.obj.log.LogView_01'
    WHERE   class = 'ibs.bo.LogView_01'

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
