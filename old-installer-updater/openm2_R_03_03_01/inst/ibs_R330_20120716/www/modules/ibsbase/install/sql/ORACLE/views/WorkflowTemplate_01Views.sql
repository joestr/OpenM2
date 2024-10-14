/******************************************************************************
 * All views regarding the WorkflowTemplate_01. <BR>
 *
 * @version     2.02.0001, 08.03.2001
 *
 * @author      Horst Pichler (HP)
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/*
 * Gets the oid of all workflow templates referenced by
 * active workflow-instances. An active workflow-instance
 * has one of the following states:
 * - STATE_OPEN
 * - STATE_OPEN_RUNNING
 * - STATE_OPEN_RUNNING_LASTSTATE
 *
 */
 
-- create the new view:
CREATE OR REPLACE VIEW  v_WorkflowTemplate_01$ref
AS
    -- select all templates 
    -- * referenced by an active workflow-instance,
    -- * that are still active objects (not deleted),
    -- * referenced by an active XMLViewer,
    -- * rererenced by an active XMLViewerContainer and
    -- * referenced by an active DocumentTemplate
    SELECT  definitionId
    FROM    ibs_Workflow_01
    WHERE   definitionId IN
            (SELECT wf.definitionId
             FROM   ibs_Workflow_01 wf, ibs_Object o
             WHERE  wf.objectId = o.oid
             AND    o.state = 2
             AND    (wf.workflowState = 'open.running'
                     OR  wf.workflowState = 'open'
                     OR wf.workflowState = 'open.running.lastStateReached'))
    OR      definitionId IN 
            (SELECT xv.workflowTemplateOid
             FROM   ibs_XMLViewer_01 xv, ibs_Object o
             WHERE  xv.oid = o.oid
             AND    o.state = 2)
    OR      definitionId IN 
            (SELECT xvc.workflowTemplateOid 
             FROM   ibs_XMLViewerContainer_01 xvc, ibs_Object o
             WHERE  xvc.oid = o.oid
             AND    o.state = 2)
    OR      definitionId IN 
            (SELECT dt.workflowTemplateOid 
             FROM   ibs_documenttemplate_01 dt, ibs_Object o
             WHERE  dt.oid = o.oid
             AND    o.state = 2)
;
-- v_WorkflowTemplate_01$ref

show errors;

EXIT;