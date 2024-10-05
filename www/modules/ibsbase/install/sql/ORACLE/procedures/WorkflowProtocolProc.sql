/******************************************************************************
 * All stored procedures regarding the WorkflowProtocol. <BR>
 *
 * @version     2.05.0001, 5.10.2000
 *
 * @author      Horst Pichler, 5.10.2000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * Creates a workflow protocol entry. <BR>
 *
 * @input parameters:
 * @param @ai_instanceId          oid of the instance
 * @param @ai_objectId            oid of the object (forwarded)
 * @param @ai_currentState        name of the current state
 * @param @ai_operationType       
 * @param @ai_fromParticipantId   
 * @param @ai_toParticipantId     
 * @param @ai_fromParticipantName 
 * @param @ai_toParticipantName   
 * @param @ai_additionalComment   
 *
 * @output parameters:
 *
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_WorkflowProtocol$createEntry
(
    -- common input parameters:
    ai_instanceId_s        VARCHAR2,
    ai_objectId_s          VARCHAR2,
    ai_objectName          VARCHAR2,
    ai_currentState        VARCHAR2,
    ai_operationType       INTEGER,
    ai_fromParticipantId   INTEGER,
    ai_toParticipantId     INTEGER,
    ai_fromParticipantName VARCHAR2,
    ai_toParticipantName   VARCHAR2,
    ai_additionalComment   VARCHAR2
)
AS
    -- local variables:
    l_id            INTEGER := 0;
    l_objectId      RAW(8);
    l_instanceId    RAW(8);


BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_objectId_s, l_objectId);
    p_stringToByte (ai_instanceId_s, l_instanceId);

-- body:
    -- select and increment max id
    BEGIN
        -- get highest id in table
        SELECT  MAX(id)
        INTO    l_id
        FROM    ibs_WorkflowProtocol;
        
        IF (l_id IS NULL)
        THEN 
            l_id := 1;
        END IF;
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_id := 1;
    END;
       
    -- create protocol entry with given parameters
    INSERT INTO ibs_WorkflowProtocol 
        (id, entryDate, instanceId, objectId, objectName, currentState,
         operationType, fromParticipantId, toParticipantId,
         fromParticipantName, toParticipantName, additionalComment)
    VALUES
        (l_id, SYSDATE, l_instanceId, l_objectId, ai_objectName,
         ai_currentState, ai_operationType, ai_fromParticipantId, 
         ai_toParticipantId, ai_fromParticipantName, ai_toParticipantName, 
         ai_additionalComment);
         
    COMMIT WORK;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_WorkflowProtocol$createEntry',
            'Input: ' ||
            ', ai_instanceId_s = ' || ai_instanceId_s ||
            ', ai_objectId_s = ' || ai_objectId_s ||
            ', ai_objectName = ' || ai_objectName || 
            ', ai_currentState = ' || ai_currentState || 
            ', ai_operationType = ' || ai_operationType || 
            ', ai_fromParticipantId = ' || ai_fromParticipantId || 
            ', ai_toParticipantId = ' || ai_toParticipantId || 
            ', ai_fromParticipantName = ' || ai_fromParticipantName ||
            ', ai_toParticipantName = ' || ai_toParticipantName ||
            ', ai_additionalComment = ' || ai_additionalComment ||                                                                                             
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_WorkflowProtocol$createEntry;
/

show errors;
-- p_WorkflowProtocol$createEntry

commit work;

exit;