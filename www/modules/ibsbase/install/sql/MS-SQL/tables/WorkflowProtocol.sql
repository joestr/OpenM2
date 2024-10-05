/******************************************************************************
 *
 * The ibs_WorkflowProtocol table table. <BR>
 *
 * @version         2.50.0001, 1.11.2000
 *
 * @author      Horst Pichler (HP)  01112000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_WorkflowProtocol
(
    id                  INTEGER         NOT NULL, -- unique ascending number
    entryDate           DATETIME        NOT NULL DEFAULT getDate(), -- date of entry
    instanceId          OBJECTID        NOT NULL, -- oid of the workflow instance
    objectId            OBJECTID        NOT NULL, -- oid of the object (forwarded)
    objectName          NAME,                     -- name of the object
    currentState        NVARCHAR(64)    NOT NULL, -- name of the current state
    operationType       INTEGER         NOT NULL, -- 0=undefined,
                                                  -- 1=sentToUser
                                                  -- 2=sentCCToUser
                                                  -- 3=sentToApplication
                                                  -- 10=start,
                                                  -- 11=finish,
                                                  -- 12=abort,
                                                  -- 13=terminate
    fromParticipantId   USERID          NOT NULL, -- id of the user/application
                                                  -- who forwarded or performed
                                                  -- operation (like 'terminate')
    toParticipantId     USERID,                   -- id of the user/application
                                                  -- to whom was forwarded
    fromParticipantName NAME,                     -- full name
    toParticipantName   NAME,                     -- full name
    additionalComment   NVARCHAR(255)             -- comments
)
GO