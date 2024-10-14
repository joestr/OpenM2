/******************************************************************************
 * All views regarding the type 'StateContainer'. <BR>
 *
 * @version     2.10.0001, 23.03.2001
 *
 * @author      Thomas Joahm (TJ)  010323
 ******************************************************************************
 */

-- delete existing view:
EXEC p_dropView 'v_StateContainer_01$content'
GO

-- create the new view:
CREATE VIEW v_StateContainer_01$content
AS
    SELECT  w.currentState AS workflowState,
            w.oid AS workflowOid, wp.entryDate AS stateChangeDate,
            o.*
    FROM    v_Container$content o,
            (SELECT oid, CASE WHEN isLink = 0 
                         THEN oid ELSE linkedObjectId 
                         END AS joinoid 
             FROM ibs_Object) s
      LEFT OUTER JOIN ibs_Workflow_01 w
        ON w.objectid = s.joinOid
      LEFT OUTER JOIN  ibs_workflowprotocol wp
          ON  wp.instanceId = w.oid
          AND wp.currentState = w.currentState
          AND wp.operationType = 1  -- receiver
      WHERE o.oid = s.oid

GO
-- v_StateContain$content 
