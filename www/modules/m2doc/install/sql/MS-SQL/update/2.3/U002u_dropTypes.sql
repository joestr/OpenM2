/******************************************************************************
 * Task:        Task Code Cleaning 2.4 - module concept.
 *
 * Description: Drop not needed object types.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U002u_dropTypes.sql,v 1.1 2004/01/06 23:48:31 klaus Exp $
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
    @l_file = 'U002u',
    @l_error = 0

-- body:
    -- drop the types:
    EXEC p_Type$deletePhysical 0x01011800, 'OrderLine'
    EXEC p_Type$deletePhysical 0x01016100, 'ProductSourceContainer'
    EXEC p_Type$deletePhysical 0x01016200, 'Supplier'
    EXEC p_Type$deletePhysical 0x01016300, 'SP_Member'
    EXEC p_Type$deletePhysical 0x01016400, 'SP_BillAddress'
    EXEC p_Type$deletePhysical 0x01016500, 'SP_Branch'
    EXEC p_Type$deletePhysical 0x01016600, 'SP_BranchContainer'
    EXEC p_Type$deletePhysical 0x01016700, 'SP_MasterDataContainer'
    EXEC p_Type$deletePhysical 0x01016A00, 'SP_ProductSource' -- (no type name!)

    -- drop the type names:
    DELETE  ibs_TypeName_01
    WHERE   name IN (
                'TN_OrderLine_01',
                'TN_SP_ProductSourceContainer_01',
                'TN_SP_Supplier_01',
                'TN_SP_Member_01',
                'TN_SP_BillAddress_01',
                'TN_SP_Branch_01',
                'TN_SP_BranchContainer_01',
                'TN_SP_MasterDataContainer_01'
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
