/******************************************************************************
 * Task:        TRI060414_01 - Message field not existing.
 *
 * Description: Rename message from MSG_TRANSLATION_SUCCESSFULL to
 *              MSG_TRANSLATION_SUCCESSFUL which is the correct name in
 *              ibs.di.DIMessages.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24027u_changeMessages.sql,v 1.1 2006/04/14 12:53:06 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060414
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_CONST1               INT,            -- description

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (2000), -- error position description
    @l_msg                  VARCHAR (5000)  -- the actual message

-- assign constants:
SELECT
    @c_CONST1 = 1234567

-- initialize local variables and return values:
SELECT
    @l_file = 'U24027u',
    @l_error = 0

-- body:

    -- change the database entry:
    UPDATE  ibs_Message_01
    SET     name = 'MSG_TRANSLATION_SUCCESSFUL'
    WHERE   name = 'MSG_TRANSLATION_SUCCESSFULL'
        AND className = 'ibs.di.DIMessages'

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'Could not update message', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos

    SELECT  @l_msg = @l_file + ': Error message:'
    PRINT @l_msg
    SELECT  @l_msg = 'Error ' + CONVERT (VARCHAR, @l_error) +
            '; position: ' + @l_ePos
    PRINT @l_msg

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished'
    PRINT @l_msg
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
