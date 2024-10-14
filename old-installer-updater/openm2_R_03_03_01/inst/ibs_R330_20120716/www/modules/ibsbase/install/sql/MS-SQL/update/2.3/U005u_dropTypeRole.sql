/******************************************************************************
 * Task:        Task Code Cleaning 2.4 - module concept.
 *
 * Description: Drop not needed object type Role.
 *
 * Repeatable:  yes
 *
 * @version     $id$
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
    @l_file = 'U005u',
    @l_error = 0

-- body:
    -- drop the type:
    EXEC p_Type$deletePhysical 0x010100C0, 'Role'

    -- drop the type name:
    DELETE  ibs_TypeName_01
    WHERE  name = 'TN_Role_01'

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
