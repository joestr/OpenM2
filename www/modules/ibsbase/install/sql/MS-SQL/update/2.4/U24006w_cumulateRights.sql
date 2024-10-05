/******************************************************************************
 * Task:        TASK/BUG#xxx - Dummy file for Updatescript.
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes in the
 *              database content.
 *
 * Repeatable:  yes/no
 *
 * @version     $Id: U24006w_cumulateRights.sql,v 1.1 2005/02/15 21:38:48 klaus Exp $
 *
 * @author      Horst Pichler (HP) 000221
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (2000), -- error position description
    @l_msg                  VARCHAR (5000)  -- the actual message

-- assign constants:

-- initialize local variables and return values:
SELECT
    @l_file = 'U26000v',
    @l_error = 0

-- body:

    BEGIN TRANSACTION

EXEC p_showActDateTime 'start cumulation   '
    EXEC p_Rights$updateRightsCum
EXEC p_showActDateTime 'cumulation finished'

    COMMIT TRANSACTION

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos
                                        -- integer
                                        -- ...
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
