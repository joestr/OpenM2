/******************************************************************************
 * Task:        EVN CRE4 Performance tuning.
 *
 * Description: Add attribute containerOid2 to table ibs_Object.
 *              Also create the index for this attribute.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24002a_addContainerOid2.sql,v 1.2 2005/01/27 01:20:30 klaus Exp $
 *
 * @author      Klaus Reimueller (KR) 20050112
 ******************************************************************************
 */ 


-- don't show count messages:
SET NOCOUNT ON
GO

ALTER TABLE ibs_Object
ADD containerOid2   OBJECTID        NULL        -- oid of container being two
                                                -- levels above
                                                -- (container of the container)
GO

CREATE INDEX INDEXOBJECTCONTAINEROID2 ON IBS_OBJECT (containerOid2)
GO

-- show state message:
PRINT 'U24002a: finished.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
