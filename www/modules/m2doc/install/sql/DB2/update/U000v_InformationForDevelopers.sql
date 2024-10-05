/******************************************************************************
 * Task:        TASK/BUG#xxx - Dummy file for Updatescript.
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes in the
 *              database content.
 *
 * Repeatable:  yes/no
 *
 * @version     $Id: U000v_InformationForDevelopers.sql,v 1.3 2003/10/06 22:06:13 klaus Exp $
 *
 * @author      Horst Pichler (HP) 000221
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_CONST1               INT,            -- description

    -- local variables:
    @l_file                 VARCHAR (5),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (2000), -- error position description
    @l_msg                  VARCHAR (5000), -- the actual message
    @l_var1                 INT,            -- description
    @l_var2                 INT,            -- description
    @l_var3                 VARCHAR (255)   -- description

-- assign constants:
SELECT
    @c_CONST1 = 1234567

-- initialize local variables:
SELECT
    @l_file = 'U000',
    @l_error = 0,
    @l_var2 = 7,
    @l_var3 = 'var3Text'

-- body:

/*
    Here comes some code, e.g. an UPDATE statement.
*/

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'UPDATE problems', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

/*
    Some other code...
*/

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos
            'l_var1', @l_var1,          -- integer
            'l_var3', @l_var3,          -- varchar
            'l_var2', @l_var2           -- integer
                                        -- varchar
                                        -- integer
                                        -- ...
    SELECt  @l_msg = @l_file + ': Error message:'
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
