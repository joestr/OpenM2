/******************************************************************************
 *
 * The ibs_Workflow_01 table. <BR>
 *
 * @version         2.50.0001, 1.11.2000
 *
 * @author      Horst Pichler (HP)  01112000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_Workflow_01
(
    oid                 OBJECTID        NOT NULL, -- Workflow_01 object
    objectId            OBJECTID        NOT NULL, -- object to forward
    definitionId        OBJECTID        NOT NULL, -- workflow xml-template
    startDate           DATETIME        NOT NULL, -- instantiation time
    endDate             DATETIME        NOT NULL DEFAULT getDate(),
                                                  -- instance finalization time
    workflowState       NVARCHAR(32),             -- state (WfMC)
    currentState        NVARCHAR(64),             -- state (m2)
    processManager      USERID,                   -- the process manager
    processManagerCont  OBJECTID,                 -- path to process manager
    starter             USERID,                   -- usert who starts wf
    starterContainer    OBJECTID,                 -- oid of startercontainer
    currentOwner        USERID,                   -- current owner of object
    currentContainer    OBJECTID,                 -- oid of current container
    writeLog            BIT                       -- shall log be written?
)
GO