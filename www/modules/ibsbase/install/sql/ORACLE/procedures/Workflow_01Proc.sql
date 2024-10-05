/******************************************************************************
 * All stored procedures regarding the Workflow_01 Object. <BR>
 *
 * @version     2.05.0001, 5.10.2000
 *
 * @author      Horst Pichler, 5.10.2000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_userId          ID of the user who is creating the object.
 * @param   @ai_op              Operation to be performed (used for rights 
 *                              check).
 * @param   @ai_tVersionId      Type of the new object.
 * @param   @ai_name            Name of the object.
 * @param   @ai_containerId_s   ID of the container where object shall be 
 *                              created in.
 * @param   @ai_containerKind   Kind of object/container relationship
 * @param   @ai_isLink          Defines if the object is a link
 * @param   @ai_linkedObjectId_s If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @ai_description     Description of the object.
 *
 * @output parameters:
 * @param   @ao_oid_s           OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Workflow_01$create
(
    -- common input parameters:
    ai_userId         INTEGER,
    ai_op             INTEGER,
    ai_tVersionId     INTEGER,
    ai_name           VARCHAR2,
    ai_containerId_s  VARCHAR2,
    ai_containerKind  INTEGER,
    ai_isLink         NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description    VARCHAR2,
    -- common output parameters:
    ao_oid_s          OUT VARCHAR2
) RETURN INTEGER 
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
            
    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;

    -- body            
BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, 
                        ai_tVersionId, ai_name, ai_containerId_s, 
                        /* ai_containerKind -> HARDCODED*/ 3,
                        ai_isLink, ai_linkedObjectId_s, 
                        ai_description, ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
        -- create object type specific data:
        INSERT INTO ibs_Workflow_01 
            (oid, objectId, definitionId, startDate, workflowState,
             currentState, processManager, processManagerCont,
             starter, starterContainer, currentOwner, currentContainer,
             writeLog)
        VALUES 
            (l_oid, c_NOOID, c_NOOID, SYSDATE, 'UNDEFINED',
             'UNDEFINED', ai_userId, c_NOOID, ai_userId, c_NOOID, 
             ai_userId, c_NOOID, 0);
    END IF;
        
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workflow_01$create',
            'Input: ' ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op || 
            ', ai_tVersionId = ' || ai_tVersionId || 
            ', ai_name = ' || ai_name || 
            ', ai_containerId_s = ' || ai_containerId_s || 
            ', ai_containerKind = ' || ai_containerKind || 
            ', ai_isLink = ' || ai_isLink || 
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s ||
            ', ai_description = ' || ai_description ||             
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Workflow_01$create;
/
show errors;
-- p_Workflow_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_id              ID of the object to be changed.
 * @param   @ai_userId          ID of the user who is changing the object.
 * @param   @ai_op              Operation to be performed (used for rights 
 *                              check).
 * @param   @ai_name            Name of the object.
 * @param   @ai_validUntil      Date until which the object is valid.
 * @param   @ai_description     Description of the object.
 * @param   @ai_showInNews      the showInNews flag    
 *
 * @param   @ai_objectId_s      object id of the related business object
 * @param   @ai_definitionId_s  workflow xml-template
 * @param   @ai_startDate       instantiation time
 * @param   @ai_endDate         completion time
 * @param   @ai_workflowState   state (WfMC)
 * @param   @ai_currentState    state (m2)
 * @param   @ai_starter         user who starts wf
 * @param   @ai_starterContainer path to startercontainer
 * @param   @ai_currentOwner    id of current user (= receiver of current state)
 * @param   @ai_logFilePath     path to log file
 * @param   @ai_logFileName     log name
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Workflow_01$change
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    ai_name           VARCHAR2,
    ai_validUntil     DATE,
    ai_description    VARCHAR2,
    ai_showInNews     NUMBER,
    -- type-specific input parameters:
    ai_objectId_s     VARCHAR2,     -- object to forward
    ai_definitionId_s VARCHAR2,     -- workflow xml-template
    ai_startDate      DATE,         -- instantiation time
    ai_endDate        DATE,         -- completion time    
    ai_workflowState  VARCHAR2,     -- state (WfMC)
    ai_currentState   VARCHAR2,     -- state (m2)
    ai_processManager INTEGER,      -- process manager
    ai_processManagerCont_s VARCHAR2, -- path to container of proc.mgr
    ai_starter        INTEGER,      -- user who starts wf
    ai_starterContainer_s VARCHAR2, -- oid of startercontainer
    ai_currentOwner   INTEGER,      -- current owner of forward-object
    ai_currentContainer_s VARCHAR2, -- oid of current container
    ai_writeLog       NUMBER        -- shall log be written?
) RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue         INTEGER := c_ALL_RIGHT;
    l_oid              RAW (8) := c_NOOID;
    l_objectId         RAW (8) := c_NOOID;
    l_definitionId     RAW (8) := c_NOOID;
    l_processManagerCont RAW (8);
    l_starterContainer RAW (8);
    l_currentContainer RAW (8);    

BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_objectId_s, l_objectId);
    p_stringToByte (ai_definitionId_s, l_definitionId);
    p_stringToByte (ai_processManagerCont_s, l_processManagerCont);
    p_stringToByte (ai_starterContainer_s, l_starterContainer);
    p_stringToByte (ai_currentContainer_s, l_currentContainer);

-- body:
    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, 
            ai_op, ai_name, ai_validUntil, ai_description, 
            ai_showInNews, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        -- update further information
        UPDATE  ibs_Workflow_01
        SET     objectId = l_objectId,
                definitionId = l_definitionId,
                startDate = ai_startDate,
                endDate = ai_endDate,
                workflowState = ai_workflowState,
                currentState = ai_currentState,
                processManager = ai_processManager,
                processManagerCont = l_processManagerCont,
                starter = ai_starter,
                starterContainer = l_starterContainer,
                currentOwner = ai_currentOwner,
                currentContainer = l_currentContainer,                    
                writeLog = ai_writeLog
        WHERE   oid = l_oid;
    END IF;
    
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workflow_01$change',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op || 
            ', ai_name = ' || ai_name || 
            ', ai_validUntil = ' || ai_validUntil || 
            ', ai_description = ' || ai_description || 
            ', ai_showInNews = ' || ai_showInNews || 
            ', ai_objectId_s = ' || ai_objectId_s ||
            ', ai_definitionId_s = ' || ai_definitionId_s ||
            ', ai_startDate = ' || ai_startDate ||             
            ', ai_endDate = ' || ai_endDate ||
            ', ai_workflowState = ' || ai_workflowState ||
            ', ai_currentState = ' || ai_currentState ||
            ', ai_processManager = ' || ai_processManager ||
            ', ai_processManagerCont_s = ' || ai_processManagerCont_s ||
            ', ai_starter = ' || ai_starter ||
            ', ai_starterContainer_s = ' || ai_starterContainer_s ||                                                                        
            ', ai_currentOwner = ' || ai_currentOwner ||
            ', ai_currentContainer_s = ' || ai_currentContainer_s ||
            ', ai_writeLog = ' || ai_writeLog ||                                    
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Workflow_01$change;
/

show errors;
-- p_Workflow_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_oid_s           ID of the object to be retrieved.
 * @param   @ai_userId          Id of the user who is getting the data.
 * @param   @ai_op              Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @ao_state           The object's state.
 * @param   @ao_tVersionId      ID of the object's type (correct version).
 * @param   @ao_typeName        Name of the object's type.
 * @param   @ao_name            Name of the object itself.
 * @param   @ao_containerId     ID of the object's container.
 * @param   @ao_containerName   Name of the object's container.
 * @param   @ao_containerKind   Kind of object/container relationship.
 * @param   @ao_isLink          Is the object a link?
 * @param   @ao_linkedObjectId  Link if isLink is true.
 * @param   @ao_owner           ID of the owner of the object.
 * @param   @ao_creationDate    Date when the object was created.
 * @param   @ao_creator         ID of person who created the object.
 * @param   @ao_lastChanged     Date of the last change of the object.
 * @param   @ao_changer         ID of person who did the last change to the
 *                              object.
 * @param   @ao_validUntil      Date until which the object is valid.
 * @param   @ao_description     Description of the object.
 * @param   @ao_showInNews      the showInNews flag    
 * @param   @ao_checkedOut      Is the object checked out?
 * @param   @ao_checkOutDate    Date when the object was checked out
 * @param   @ao_checkOutUser    id of the user which checked out the object
 * @param   @ao_checkOutUserOid Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @ao_checkOutUserName name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   @ao_objectId_s      object id of the related business object
 * @param   @ao_definitionId_s  workflow xml-template
 * @param   @ao_startDate       instantiation time
 * @param   @ao_endDate         completion time 
 * @param   @ao_workflowState   state (WfMC)
 * @param   @ao_currentState    state (m2)
 * @param   @ao_starter         user who starts wf
 * @param   @ao_starterContainer path to startercontainer
 * @param   @ao_currentOwner    id of current owner (= receiver of current state)
 * @param   @ao_logFilePath     path to log file
 * @param   @ao_logFileName     log name
 *
 * @param   @ao_objectName      name of forward-object
 * @param   @ao_definitionName  name of workflow-template
 * @param   @ao_processManagerName process managers name
 * @param   @ao_starterName     user who starts wf
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Workflow_01$retrieve
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    -- common output parameters:
    ao_state          OUT INTEGER,
    ao_tVersionId     OUT INTEGER,
    ao_typeName       OUT VARCHAR2,
    ao_name           OUT VARCHAR2,
    ao_containerId    OUT RAW,
    ao_containerName  OUT VARCHAR2,
    ao_containerKind  OUT INTEGER,
    ao_isLink         OUT NUMBER,
    ao_linkedObjectId OUT RAW,
    ao_owner          OUT INTEGER,
    ao_ownerName      OUT VARCHAR2,
    ao_creationDate   OUT DATE,
    ao_creator        OUT INTEGER,
    ao_creatorName    OUT VARCHAR2,
    ao_lastChanged    OUT DATE,
    ao_changer        OUT INTEGER,
    ao_changerName    OUT VARCHAR2,
    ao_validUntil     OUT DATE,
    ao_description    OUT VARCHAR2,
    ao_showInNews     OUT NUMBER,
    ao_checkedOut     OUT NUMBER,
    ao_checkOutDate   OUT DATE,
    ao_checkOutUser   OUT INTEGER,
    ao_checkOutUserOid OUT RAW,
    ao_checkOutUserName OUT VARCHAR2,
    -- type-specific output attributes:
    ao_objectId       OUT RAW,
    ao_definitionId   OUT RAW,
    ao_startDate      OUT DATE,
    ao_endDate        OUT DATE,
    ao_workflowState  OUT VARCHAR2,
    ao_currentState   OUT VARCHAR2,
    ao_processManager OUT INTEGER,
    ao_processManagerCont OUT RAW,
    ao_starter        OUT INTEGER,
    ao_starterContainer OUT RAW,
    ao_currentOwner   OUT INTEGER, 
    ao_currentContainer OUT RAW,        -- oid of startercontainer
    ao_writeLog       OUT NUMBER,       -- shall log be written?
    -- additional attributes: used for viewing only
    ao_objectName     OUT VARCHAR2,     -- object-name 
    ao_definitionName OUT VARCHAR2,     -- workflow-template name
    ao_processManagerName OUT VARCHAR2, -- process managers name
    ao_starterName    OUT VARCHAR2,     -- user who starts wf
    ao_currentOwnerName OUT VARCHAR2    -- name of current owner
) RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;    
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
            
    -- local variables:
    l_retValue         INTEGER := c_ALL_RIGHT;
    l_oid              RAW (8) := c_NOOID;
    
    -- body:
BEGIN    
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (
            ai_oid_s, ai_userId, ai_op,
            ao_state, ao_tVersionId, ao_typeName, 
            ao_name, ao_containerId, ao_containerName, 
            ao_containerKind, ao_isLink, ao_linkedObjectId, 
            ao_owner, ao_ownerName, 
            ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName,
            ao_validUntil, ao_description, ao_showInNews, 
            ao_checkedOut, ao_checkOutDate, 
            ao_checkOutUser, ao_checkOutUserOid, 
            ao_checkOutUserName, l_oid);

    -- get type-specific data
    IF (l_retValue = c_ALL_RIGHT)
    THEN      
        -- get base data
        BEGIN
            SELECT  objectId, 
                    definitionId,
                    startDate,
                    endDate,
                    workflowState,
                    currentState,
                    processManager,
                    processManagerCont,
                    starter,
                    starterContainer,
                    currentOwner,
                    currentContainer,                    
                    writeLog
            INTO    ao_objectId, 
                    ao_definitionId,
                    ao_startDate,
                    ao_endDate,
                    ao_workflowState,
                    ao_currentState,
                    ao_processManager,
                    ao_processManagerCont,
                    ao_starter,
                    ao_starterContainer,
                    ao_currentOwner,
                    ao_currentContainer, 
                    ao_writeLog
            FROM    ibs_Workflow_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_retValue := c_OBJECTNOTFOUND;
        END;

        -- get additional data: name of forward-object
        BEGIN
            SELECT  name
            INTO    ao_objectName
            FROM    ibs_Object
            WHERE   oid = ao_objectId;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                ao_objectName := '';
        END;
                        
        -- get additional data: name of template-object
        BEGIN
            SELECT  name
            INTO    ao_definitionName
            FROM    ibs_Object
            WHERE   oid = ao_definitionId;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                ao_definitionName := '';
        END;
            
        -- get additional data: name of process manager
        BEGIN
            SELECT  fullName
            INTO    ao_processManagerName
            FROM    ibs_User
            WHERE   id = ao_processManager;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                ao_processManagerName := '';
        END;

        -- get additional data: name of process manager
        BEGIN
            SELECT  fullName
            INTO    ao_starterName
            FROM    ibs_User
            WHERE   id = ao_starter;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                ao_starterName := '';
        END;
            
        -- get additional data: name of process manager
        BEGIN        
            SELECT  fullName
            INTO    ao_currentOwnerName
            FROM    ibs_User
            WHERE   id = ao_currentOwner;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                ao_currentOwnerName := '';
        END;
            
    END IF;
    
    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workflow_01$retrieve',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
            
    -- return error value:
    RETURN c_NOT_OK;
END p_Workflow_01$retrieve;
/

show errors;
-- p_Workflow_01$retrieve



/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @ai_oid_s           ID of the object to be deleted.
 * @param   @ai_userId          ID of the user who is deleting the object.
 * @param   @ai_op              Operation to be performed (used for rights 
 *                              check).
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure: 
CREATE OR REPLACE FUNCTION p_Workflow_01$delete
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER
) RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
            
    -- local variables:
    l_retValue         INTEGER := c_ALL_RIGHT;
    l_oid              RAW (8) := c_NOOID;
            
-- body:
BEGIN
    -- delete base object:
    l_retValue := p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workflow_01$delete',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
            
    -- return error value:
    RETURN c_NOT_OK;
END p_Workflow_01$delete;
/

show errors;
-- p_Workflow_01$delete

exit;