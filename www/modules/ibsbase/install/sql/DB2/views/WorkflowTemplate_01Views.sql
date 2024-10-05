------------------------------------------------------------------------------
 -- All views regarding the WorkflowTemplate_01. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:59 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------


    -- Gets the oid of all workflow templates referenced by
    -- active workflow-instances. An active workflow-instance
    -- has one of the following states:
    -- - STATE_OPEN
    -- - STATE_OPEN_RUNNING
    -- - STATE_OPEN_RUNNING_LASTSTATE



    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_WORKFLOWTEMPLATE_01$REF');

    -- create the new view: 
CREATE VIEW  IBSDEV1.v_WorkflowTemplate_01$ref  
AS
    -- select all templates 
    -- * referenced by an active workflow-instance,
    -- * that are still active objects (not deleted),
    -- * referenced by an active XMLViewer,
    -- * rererenced by an active XMLViewerContainer and
    -- * referenced by an active DocumentTemplate                             
    SELECT  definitionId      
    FROM    IBSDEV1.ibs_Workflow_01      
    WHERE   definitionId IN              
            (SELECT wf.definitionId               
             FROM   IBSDEV1.ibs_Workflow_01 wf, IBSDEV1.ibs_Object o               
             WHERE  wf.objectId = o.oid               
             AND    o.state = 2               
             AND    (wf.workflowState = 'open.running'                       
             OR      wf.workflowState = 'open'                       
             OR      wf.workflowState = 'open.running.lastStateReached'))      
    OR      definitionId IN               
            (SELECT xv.workflowTemplateOid               
             FROM   IBSDEV1.ibs_XMLViewer_01 xv, 
                 IBSDEV1.ibs_Object o                               
             WHERE  xv.oid = o.oid               
             AND    o.state = 2)      
    OR      definitionId IN               
            (SELECT xvc.workflowTemplateOid                
             FROM   IBSDEV1.ibs_XMLViewerContainer_01 xvc, 
                    IBSDEV1.ibs_Object o                  
             WHERE  xvc.oid = o.oid               
             AND    o.state = 2)      
    OR      definitionId IN               
            (SELECT dt.workflowTemplateOid                
             FROM   IBSDEV1.ibs_documenttemplate_01 dt, 
                  IBSDEV1.ibs_Object o
             WHERE  dt.oid = o.oid               
             AND    o.state = 2);
    -- v_WorkflowTemplate_01$ref
