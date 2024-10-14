/******************************************************************************
 * Description: This file updates the new column typeCode with the correct
 *              values from the ibs_type table
 * 
 * Repeatable:  yes
 *
 * @version     $Id: U300005u_updateObject.sql,v 1.1 2010/04/20 21:10:06 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB) 20100420
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- Update the typeCode column within ibs_object
-- Only update the columns when the column contains 'UNKNOWN' at the moment
UPDATE ibs_object
SET typeCode = t.code
FROM ibs_TVersion tv,
     ibs_type t
WHERE tversionid = tv.id
  AND tv.typeid = t.id
  AND typeCode = 'UNKNOWN'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO