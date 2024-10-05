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
CREATE TABLE /*USER*/ibs_Workflow_01
(
    oid                 RAW(8) NOT NULL, -- Workflow_01 object
    objectId            RAW(8) NOT NULL, -- object to forward
    definitionId        RAW(8) NOT NULL, -- workflow xml-template
    startDate           DATE   NOT NULL, -- instantiation time
    endDate             DATE   NOT NULL, -- instance finalization time
    workflowState       VARCHAR2(32),    -- state (WfMC)
    currentState        VARCHAR2(64),    -- state (m2)
    processManager      INTEGER,         -- the process manager
    processManagerCont  RAW(8),          -- path to process manager
    starter             INTEGER,         -- usert who starts wf
    starterContainer    RAW(8),          -- oid of startercontainer
    currentOwner        INTEGER,         -- current owner of object
    currentContainer    RAW(8),          -- oid of current container
    writeLog            NUMBER(1)        -- shall log be written?
);

ALTER TABLE /*USER*/ibs_Workflow_01 modify ( oid DEFAULT hexToRaw('0000000000000000') );
ALTER TABLE /*USER*/ibs_Workflow_01 modify ( objectId DEFAULT hexToRaw('0000000000000000') );
ALTER TABLE /*USER*/ibs_Workflow_01 modify ( definitionId DEFAULT hexToRaw('0000000000000000') );
ALTER TABLE /*USER*/ibs_Workflow_01 modify ( startDate DEFAULT SYSDATE );
ALTER TABLE /*USER*/ibs_Workflow_01 modify ( endDate DEFAULT SYSDATE );

EXIT;
