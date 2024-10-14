/******************************************************************************
 * All stored procedures regarding the WorkflowProtocol. <BR>
 *
 * @version     $Id: WorkflowProtocolProc.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Horst Pichler, 5.10.2000
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
-- delete existing procedure:
EXEC p_dropProc N'p_WorkflowProtocol$createEntry'
GO

-- create the new procedure:
CREATE PROCEDURE p_WorkflowProtocol$createEntry
(
    -- common input parameters:
    @ai_instanceId_s        OBJECTIDSTRING,
    @ai_objectId_s          OBJECTIDSTRING,
    @ai_objectName          NVARCHAR(64),
    @ai_currentState        NVARCHAR(64),
    @ai_operationType       INTEGER,
    @ai_fromParticipantId   USERID,
    @ai_toParticipantId     USERID,
    @ai_fromParticipantName NAME,
    @ai_toParticipantName   NAME,
    @ai_additionalComment   NVARCHAR(255)
)
AS
-- constants:
            
-- local variables:
DECLARE 
    @l_id           INT,
    @l_objectId     OBJECTID,
    @l_instanceId   OBJECTID
                
-- initialize constants:

-- initialize local variables:
    SELECT @l_id = 0
    EXEC p_stringToByte @ai_objectId_s, @l_objectId OUTPUT
    EXEC p_stringToByte @ai_instanceId_s, @l_instanceId OUTPUT            

-- body:
    BEGIN TRANSACTION
        -- select and increment max id
        SELECT  @l_id = MAX(id)
        FROM    ibs_WorkflowProtocol
        
        -- check if there were already an entry
        IF (@l_id IS NULL)
            SELECT @l_id = 1
        ELSE
            SELECT @l_id = @l_id + 1
    
        -- create protocol entry with given parameters
        INSERT INTO ibs_WorkflowProtocol 
            (id, entryDate, instanceId, objectId, objectName, currentState,
             operationType, fromParticipantId, toParticipantId,
             fromParticipantName, toParticipantName, additionalComment)
        VALUES
            (@l_id, getDate(), @l_instanceId, @l_objectId, @ai_objectName,
             @ai_currentState, @ai_operationType, @ai_fromParticipantId, 
             @ai_toParticipantId, @ai_fromParticipantName, @ai_toParticipantName, 
             @ai_additionalComment)
    COMMIT TRANSACTION
GO
-- p_WorkflowProtocol$createEntry