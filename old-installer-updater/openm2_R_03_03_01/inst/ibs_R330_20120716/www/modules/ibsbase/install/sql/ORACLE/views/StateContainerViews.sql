/******************************************************************************
 * All views regarding the type 'StateContainer'. <BR>
 *
 * @version     2.10.0001, 23.03.2001
 *
 * @author      Thomas Joahm (TJ)  010323
 ******************************************************************************
 */
 
-- create the new view:
CREATE OR REPLACE VIEW v_StateContainer_01$content
AS
    SELECT  w.currentState AS workflowState,
            w.oid AS workflowOid, wp.entryDate AS stateChangeDate,
            o.*
    FROM    v_Container$content o,
            (SELECT oid, DECODE (isLink, 0, oid, linkedObjectId) AS joinoid 
             FROM ibs_Object) s,
            ibs_Workflow_01 w,
            ibs_workflowprotocol wp            
    WHERE   o.oid = s.oid
        AND w.objectid(+) = s.joinOid
        AND wp.instanceId(+) = w.oid
        AND wp.currentState(+) = w.currentState;

-- v_StateContain$content

EXIT;