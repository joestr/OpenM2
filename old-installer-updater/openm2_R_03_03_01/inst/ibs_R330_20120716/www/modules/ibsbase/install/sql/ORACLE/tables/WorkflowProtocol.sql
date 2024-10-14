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
CREATE TABLE /*USER*/ibs_WorkflowProtocol
(
    id                  INTEGER         NOT NULL, -- unique ascending number
    entryDate           DATE            NOT NULL, -- date of entry
    instanceId          RAW(8)          NOT NULL, -- oid of the workflow instance
    objectId            RAW(8)          NOT NULL, -- oid of the object (forwarded)
    objectName          VARCHAR2(63),             -- name of the object
    currentState        VARCHAR2(63)    NOT NULL, -- name of the current state
    operationType       INTEGER         NOT NULL, -- 0=undefined,
                                                  -- 1=sentToUser
                                                  -- 2=sentCCToUser
                                                  -- 3=sentToApplication
                                                  -- 10=start,
                                                  -- 11=finish,
                                                  -- 12=abort,
                                                  -- 13=terminate
    fromParticipantId   INTEGER         NOT NULL, -- id of the user/application
                                                  -- who forwarded or performed
                                                  -- operation (like 'terminate')
    toParticipantId     INTEGER,                  -- id of the user/application
                                                  -- to whom was forwarded
    fromParticipantName VARCHAR2(63),              -- full name
    toParticipantName   VARCHAR2(63),              -- full name
    additionalComment   VARCHAR2(255)             -- comments
);

ALTER TABLE /*USER*/ibs_WorkflowProtocol modify ( id         DEFAULT 0 );
ALTER TABLE /*USER*/ibs_WorkflowProtocol modify ( entryDate  DEFAULT SYSDATE );
ALTER TABLE /*USER*/ibs_WorkflowProtocol modify ( instanceId DEFAULT hexToRaw('0000000000000000') );
ALTER TABLE /*USER*/ibs_WorkflowProtocol modify ( objectId   DEFAULT hexToRaw('0000000000000000') );
ALTER TABLE /*USER*/ibs_WorkflowProtocol modify ( currentState DEFAULT 'UNDEFINED' );
ALTER TABLE /*USER*/ibs_WorkflowProtocol modify ( operationType DEFAULT 0 );
ALTER TABLE /*USER*/ibs_WorkflowProtocol modify ( fromParticipantId DEFAULT 0 );

EXIT;