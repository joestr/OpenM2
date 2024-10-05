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
-- delete existing procedure:
EXEC p_dropProc N'p_Workflow_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workflow_01$create
(
    -- common input parameters:
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_tVersionId     TVERSIONID,
    @ai_name           NAME,
    @ai_containerId_s  OBJECTIDSTRING,
    @ai_containerKind  INT,
    @ai_isLink         BOOL,
    @ai_linkedObjectId_s OBJECTIDSTRING,
    @ai_description    DESCRIPTION,
    -- common output parameters:
    @ao_oid_s          OBJECTIDSTRING OUTPUT
)
AS
-- constants:
DECLARE
    @c_NOT_OK           INT,
    @c_ALL_RIGHT        INT, 
    @c_NOOID            OBJECTID
            
-- local variables:
DECLARE 
    @l_retValue         INT,
    @l_oid              OBJECTID    
            
-- initialize constants:
SELECT  
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1, 
    @c_NOOID                = 0x0000000000000000

-- initialize local variables:
SELECT
    @l_retValue             = @c_NOT_OK,
    @l_oid                  = @c_NOOID

-- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, 
                            @ai_tVersionId, @ai_name, @ai_containerId_s,
                            /*@ai_containerKind -> HARDCODED*/ 3,
                            @ai_isLink, @ai_linkedObjectId_s, 
                            @ai_description, @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- create object type specific data:
            INSERT INTO ibs_Workflow_01 
                (oid, objectId, definitionId, startDate, workflowState,
                 currentState, processManager, processManagerCont,
                 starter, starterContainer, currentOwner, currentContainer,
                 writeLog)
            VALUES 
                (@l_oid, @c_NOOID, @c_NOOID, getDate(), N'UNDEFINED',
                 N'UNDEFINED', @ai_userId, @c_NOOID, @ai_userId, @c_NOOID, 
                 @ai_userId, @c_NOOID, 0)
        END
        
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
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
-- delete existing procedure:
EXEC p_dropProc N'p_Workflow_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workflow_01$change
(
    -- common input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_name           NAME,
    @ai_validUntil     DATETIME,
    @ai_description    DESCRIPTION,
    @ai_showInNews     BOOL,
    -- type-specific input parameters:
    @ai_objectId_s     OBJECTIDSTRING,     -- object to forward
    @ai_definitionId_s OBJECTIDSTRING,     -- workflow xml-template
    @ai_startDate      DATETIME,           -- instantiation time
    @ai_endDate        DATETIME,           -- completion time    
    @ai_workflowState  NVARCHAR(32),       -- state (WfMC)
    @ai_currentState   NVARCHAR(64),       -- state (m2)
    @ai_processManager USERID,             -- process manager
    @ai_processManagerCont_s OBJECTIDSTRING,   -- path to container of proc.mgr
    @ai_starter        USERID,             -- user who starts wf
    @ai_starterContainer_s OBJECTIDSTRING, -- oid of startercontainer
    @ai_currentOwner   USERID,             -- current owner of forward-object
    @ai_currentContainer_s OBJECTIDSTRING, -- oid of current container
    @ai_writeLog       BIT                 -- shall log be written?
)
AS
-- constants:
DECLARE
    @c_NOT_OK           INT,
    @c_ALL_RIGHT        INT, 
    @c_NOOID            OBJECTID

-- local variables:
DECLARE 
    @l_retValue         INT,
    @l_oid              OBJECTID,
    @l_objectId         OBJECTID,
    @l_definitionId     OBJECTID,
    @l_processManagerCont OBJECTID,
    @l_starterContainer OBJECTID,
    @l_currentContainer OBJECTID    
    

-- initialize constants:
SELECT  
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1, 
    @c_NOOID                = 0x0000000000000000

-- initialize local variables:
SELECT
    @l_retValue             = @c_NOT_OK,
    @l_oid                  = @c_NOOID,
    @l_objectId             = @c_NOOID,
    @l_definitionId         = @c_NOOID

    EXEC p_stringToByte @ai_objectId_s, @l_objectId OUTPUT
    EXEC p_stringToByte @ai_definitionId_s, @l_definitionId OUTPUT
    EXEC p_stringToByte @ai_processManagerCont_s, @l_processManagerCont OUTPUT
    EXEC p_stringToByte @ai_starterContainer_s, @l_starterContainer OUTPUT
    EXEC p_stringToByte @ai_currentContainer_s, @l_currentContainer OUTPUT            

-- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, 
                @ai_op, @ai_name, @ai_validUntil, @ai_description, 
                @ai_showInNews, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update further information
            UPDATE  ibs_Workflow_01
            SET     objectId = @l_objectId,
                    definitionId = @l_definitionId,
                    startDate = @ai_startDate,
                    endDate = @ai_endDate,
                    workflowState = @ai_workflowState,
                    currentState = @ai_currentState,
                    processManager = @ai_processManager,
                    processManagerCont = @l_processManagerCont,
                    starter = @ai_starter,
                    starterContainer = @l_starterContainer,
                    currentOwner = @ai_currentOwner,
                    currentContainer = @l_currentContainer,                    
                    writeLog = @ai_writeLog
            WHERE   oid = @l_oid
        END
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
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
-- delete existing procedure:
EXEC p_dropProc N'p_Workflow_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workflow_01$retrieve
(
    -- common input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT,
    -- common output parameters:
    @ao_state          STATE           OUTPUT,
    @ao_tVersionId     TVERSIONID      OUTPUT,
    @ao_typeName       NAME            OUTPUT,
    @ao_name           NAME            OUTPUT,
    @ao_containerId    OBJECTID        OUTPUT,
    @ao_containerName  NAME            OUTPUT,
    @ao_containerKind  INT             OUTPUT,
    @ao_isLink         BOOL            OUTPUT,
    @ao_linkedObjectId OBJECTID        OUTPUT,
    @ao_owner          USERID          OUTPUT,
    @ao_ownerName      NAME            OUTPUT,
    @ao_creationDate   DATETIME        OUTPUT,
    @ao_creator        USERID          OUTPUT,
    @ao_creatorName    NAME            OUTPUT,
    @ao_lastChanged    DATETIME        OUTPUT,
    @ao_changer        USERID          OUTPUT,
    @ao_changerName    NAME            OUTPUT,
    @ao_validUntil     DATETIME        OUTPUT,
    @ao_description    DESCRIPTION     OUTPUT,
    @ao_showInNews     BOOL            OUTPUT,
    @ao_checkedOut     BOOL            OUTPUT,
    @ao_checkOutDate   DATETIME        OUTPUT,
    @ao_checkOutUser   USERID          OUTPUT,
    @ao_checkOutUserOid OBJECTID       OUTPUT,
    @ao_checkOutUserName NAME          OUTPUT,
    -- type-specific output attributes:
    @ao_objectId       OBJECTID        OUTPUT,     -- object to forward
    @ao_definitionId   OBJECTID        OUTPUT,     -- workflow xml-template
    @ao_startDate      DATETIME        OUTPUT,     -- instantiation time
    @ao_endDate        DATETIME        OUTPUT,     -- completion time    
    @ao_workflowState  NVARCHAR(255)   OUTPUT,     -- state (WfMC)
    @ao_currentState   NVARCHAR(255)   OUTPUT,     -- state (m2)
    @ao_processManager USERID          OUTPUT,     -- process manager
    @ao_processManagerCont OBJECTID    OUTPUT,      -- path to cont. of proc-manager
    @ao_starter        USERID          OUTPUT,     -- user who starts wf
    @ao_starterContainer OBJECTID      OUTPUT,     -- oid of startercontainer
    @ao_currentOwner   USERID          OUTPUT,     -- current owner of forward-object
    @ao_currentContainer OBJECTID      OUTPUT,     -- oid of startercontainer
    @ao_writeLog       BIT             OUTPUT,     -- shall log be written?
    
    -- additional attributes: used for viewing only
    @ao_objectName     NAME            OUTPUT,     -- object-name 
    @ao_definitionName NAME            OUTPUT,     -- workflow-template name
    @ao_processManagerName NAME        OUTPUT,     -- process managers name
    @ao_starterName    NAME            OUTPUT,     -- user who starts wf
    @ao_currentOwnerName NAME          OUTPUT      -- name of current owner
)
AS
-- constants:
DECLARE
    @c_NOT_OK           INT,
    @c_ALL_RIGHT        INT, 
    @c_NOOID            OBJECTID
            
-- local variables:
DECLARE 
    @l_retValue         INT,
    @l_oid              OBJECTID       
            
-- initialize constants:
SELECT  
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1, 
    @c_NOOID                = 0x0000000000000000

-- initialize local variables:
SELECT
    @l_retValue             = @c_NOT_OK,
    @l_oid                  = @c_NOOID

-- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @l_retValue = p_Object$performRetrieve
                @ai_oid_s, @ai_userId, @ai_op,
                @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT, 
                @ao_name OUTPUT, @ao_containerId OUTPUT, @ao_containerName OUTPUT, 
                @ao_containerKind OUTPUT, @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT, 
                @ao_owner OUTPUT, @ao_ownerName OUTPUT, 
                @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
                @ao_validUntil OUTPUT, @ao_description OUTPUT, @ao_showInNews OUTPUT, 
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT, 
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT, 
                @ao_checkOutUserName OUTPUT, @l_oid OUTPUT

        -- get type-specific data
        IF (@l_retValue = @c_ALL_RIGHT)
        BEGIN      
            -- get base data
            SELECT  @ao_objectId = objectId, 
                    @ao_definitionId = definitionId,
                    @ao_startDate = startDate,
                    @ao_endDate = endDate,
                    @ao_workflowState = workflowState,
                    @ao_currentState = currentState,
                    @ao_processManager = processManager,
                    @ao_processManagerCont = processManagerCont,
                    @ao_starter = starter,
                    @ao_starterContainer = starterContainer,
                    @ao_currentOwner = currentOwner,
                    @ao_currentContainer = currentContainer,                    
                    @ao_writeLog = writeLog
            FROM    ibs_Workflow_01
            WHERE   oid = @l_oid

            -- get additional data: name of forward-object
            SELECT  @ao_objectName = name
            FROM    ibs_Object
            WHERE   oid = @ao_objectId
                        
            -- get additional data: name of template-object
            SELECT  @ao_definitionName = name
            FROM    ibs_Object
            WHERE   oid = @ao_definitionId
            
            -- get additional data: name of process manager
            SELECT  @ao_processManagerName = fullName
            FROM    ibs_User
            WHERE   id = @ao_processManager

            -- get additional data: name of process manager
            SELECT  @ao_starterName = fullName
            FROM    ibs_User
            WHERE   id = @ao_starter
            
            -- get additional data: name of process manager
            SELECT  @ao_currentOwnerName = fullName
            FROM    ibs_User
            WHERE   id = @ao_currentOwner
            
        END
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
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
EXEC p_dropProc N'p_Workflow_01$delete'
GO

CREATE PROCEDURE p_Workflow_01$delete
(
    -- common input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT
)
AS
-- constants:
DECLARE
    @c_ALL_RIGHT        INT,
    @c_NOT_OK           INT,     
    @c_NOOID            OBJECTID
            
-- local variables:
DECLARE 
    @l_retValue         INT,
    @l_oid              OBJECTID
            
-- initialize constants:
SELECT  
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1, 
    @c_NOOID                = 0x0000000000000000
    
-- initialize local variables:
SELECT
    @l_retValue             = @c_NOT_OK,
    @l_oid                  = @c_NOOID
    
-- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId, @ai_op, 
                @l_oid OUTPUT

/*
        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update entry in ibs_Workflow
            UPDATE  ibs_Workflow_01
            SET     workflowState = ''
            WHERE   oid = @l_oid
        END -- if operation properly performed
*/        
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Workflow_01$delete
