/******************************************************************************
 * Description: This file adds the column typeCode to the table ibs_object if the
 *              column is not already available within the table.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U300005a_ChangeObject.sql,v 1.1 2010/04/20 21:10:08 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB) 20100420
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- Check if the column typeCode already exists within the table:
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE table_name = 'ibs_object'
    AND   column_name = 'typeCode'
)
-- If column not exist, create the column typeCode:
BEGIN
    ALTER TABLE ibs_object
    ADD typeCode NAME NOT NULL DEFAULT ('UNKNOWN');
END
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO