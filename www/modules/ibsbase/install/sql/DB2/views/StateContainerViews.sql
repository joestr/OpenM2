 ------------------------------------------------------------------------------
 -- All views regarding a xmldiscussion. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_STATECONTAINER_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_StateContainer_01$content  
AS      
    SELECT  w.currentState AS workflowState, w.oid AS workflowOid, 
            wp.entryDate AS stateChangeDate, o.*
    FROM    IBSDEV1.v_Container$content o,
            (SELECT oid, 
             CASE WHEN isLink = 0 THEN oid ELSE linkedObjectId END AS joinoid
             FROM IBSDEV1.ibs_Object) s        
    LEFT OUTER JOIN IBSDEV1.ibs_Workflow_01 w ON w.objectid = s.joinOid        
    LEFT OUTER JOIN  IBSDEV1.ibs_workflowprotocol wp ON  wp.instanceId = w.oid                
    AND wp.currentState = w.currentState            
    AND wp.operationType = 1        
    WHERE o.oid = s.oid;

