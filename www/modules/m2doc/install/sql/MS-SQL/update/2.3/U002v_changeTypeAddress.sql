/******************************************************************************
 * Task:        Task Code Cleaning 2.4 - module concept.
 *
 * Description: Change implementation class of type Address.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U002v_changeTypeAddress.sql,v 1.1 2004/01/06 23:48:46 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 27.12.2003
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
    @l_file = 'U002v',
    @l_error = 0

-- body:
    -- change the type class
    UPDATE  ibs_TVersion
    SET     className = 'm2.mad.Address_01'
    WHERE   typeId = 
            (
                SELECT  id
                FROM    ibs_Type
                WHERE   code = 'Address'
            )

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
