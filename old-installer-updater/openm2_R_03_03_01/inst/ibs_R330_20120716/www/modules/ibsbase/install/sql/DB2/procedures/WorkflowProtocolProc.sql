--------------------------------------------------------------------------------
 -- All stored procedures regarding the WorkflowProtocol. <BR>
 --
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:52 $
--              $Author: klaus $
--
-- author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
 -- Creates a workflow protocol entry. <BR>
 --
 -- @input parameters:
 -- @param @ai_instanceId          oid of the instance
 -- @param @ai_objectId            oid of the object (forwarded)
 -- @param @ai_currentState        name of the current state
 -- @param @ai_operationType       
 -- @param @ai_fromParticipantId   
 -- @param @ai_toParticipantId     
 -- @param @ai_fromParticipantName 
 -- @param @ai_toParticipantName   
 -- @param @ai_additionalComment   
 --
 -- @output parameters:
 --
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_WorkflowProtocol$createEntry');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_WorkflowProtocol$createEntry
(
    -- common input parameters:
    IN ai_instanceId_s      VARCHAR (18),
    IN ai_objectId_s        VARCHAR (18),
    IN ai_objectName        VARCHAR (64),
    IN ai_currentState      VARCHAR (64),
    IN ai_operationType     INT,
    IN ai_fromParticipantId INT,
    IN ai_toParticipantId   INT,
    IN ai_fromParticipantName VARCHAR (63),
    IN ai_toParticipantName VARCHAR (63),
    IN ai_additionalComment VARCHAR (255)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;

    -- constants:
    -- local variables:
    DECLARE l_id            INT;
    DECLARE l_objectId      CHAR (8) FOR BIT DATA;
    DECLARE l_instanceId    CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize constants:
    -- initialize local variables:
    SET l_id = 0;
    CALL IBSDEV1.p_stringToByte (ai_objectId_s, l_objectId);
    CALL IBSDEV1.p_stringToByte (ai_instanceId_s, l_instanceId);
-- body:

    -- select and increment max id
    SELECT MAX (id)
    INTO l_id
    FROM IBSDEV1.ibs_WorkflowProtocol;

    -- check if there were already an entry

    IF l_id IS NULL THEN
        SET l_id = 1;
    ELSE
        SET l_id = l_id + 1;
    END IF;

    -- create protocol entry with given parameters
    INSERT INTO IBSDEV1.ibs_WorkflowProtocol(id, entryDate, instanceId,
        objectId, objectName, currentState, operationType,
        fromParticipantId, toParticipantId, fromParticipantName,
        toParticipantName,  additionalComment)
    VALUES (l_id, CURRENT TIMESTAMP, l_instanceId, l_objectId,
           ai_objectName, ai_currentState, ai_operationType,
           ai_fromParticipantId, ai_toParticipantId,
           ai_fromParticipantName, ai_toParticipantName,
           ai_additionalComment);

    COMMIT;
END;
-- p_WorkflowProtocol$createEntry