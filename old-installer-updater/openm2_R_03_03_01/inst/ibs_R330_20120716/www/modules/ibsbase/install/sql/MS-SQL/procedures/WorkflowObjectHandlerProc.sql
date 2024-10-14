/******************************************************************************
 * All stored procedures regarding the workflow service. <BR>
 *
 * @version     2.05.0001, 5.10.2000
 *
 * @author      Horst Pichler, 5.10.2000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */


/******************************************************************************
 * Set the workflow-flag in the BusinessObject. <BR>
 * 
 * @input parameters:
 * @param   @ai_oid_s           ID of the object where the flag shall be set.
 * @param   @ai_setWorkflowFlag Sets or unsets the workflow flag.
 *
 * @output parameters:
 */
-- delete existing procedure: 
EXEC p_dropProc N'p_Workflow$setWorkflowFlag'
GO

CREATE PROCEDURE p_Workflow$setWorkflowFlag
(
    -- common input parameters:
    @ai_oid_s           OBJECTIDSTRING,
    @ai_setWorkflowFlag   BOOL
)
AS
-- constants:
DECLARE
    @c_NOT_OK           INT,
    @c_ALL_RIGHT        INT,    
    @c_FLAG_INWORKFLOW  INT,    
    @c_TRUE             BOOL,
    @c_FALSE            BOOL

            
-- local variables:
DECLARE 
    @l_oid              OBJECTID,
    @l_currentFlags     INT,
    @l_isSet            BOOL
            
-- initialize constants:
SELECT  
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_FLAG_INWORKFLOW      = 64,
    @c_TRUE                 = 1,
    @c_FALSE                = 0

-- initialize local variables:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT
-- body:
    
    -- retrieve current settings
    SELECT  @l_currentFlags = flags
    FROM    ibs_Object
    WHERE   oid = @l_oid
    
    -- check if flag is already set
    IF ((@l_currentFlags & @c_FLAG_INWORKFLOW) = @c_FLAG_INWORKFLOW)
      SELECT @l_isSet = @c_TRUE
    ELSE
      SELECT @l_isSet = @c_FALSE    
    
    -- change setting only if it is necessary
    IF ((@l_isSet = @c_TRUE AND @ai_setWorkflowFlag = @c_FALSE) OR
        (@l_isSet = @c_FALSE AND @ai_setWorkflowFlag = @c_TRUE))
    BEGIN TRANSACTION
      UPDATE  ibs_Object
      SET     flags = flags ^ @c_FLAG_INWORKFLOW
      WHERE   oid = @l_oid
    COMMIT TRANSACTION
    
GO
-- p_Workflow$setWorkflowFlag


/******************************************************************************
 * Get the workflow-flag of the given BusinessObject. <BR>
 * 
 * @input parameters:
 * @param   @ai_oid_s           ID of the object where the flag shall be set.
 *
 * @output parameters:
 * @param   @ao_workflowFlag Sets or unsets the workflow flag. 
 */
-- delete existing procedure: 
EXEC p_dropProc N'p_Workflow$getWorkflowFlag'
GO

CREATE PROCEDURE p_Workflow$getWorkflowFlag
(
    -- common input parameters:
    @ai_oid_s           OBJECTIDSTRING,
    -- common input parameters:    
    @ao_workflowFlag   BOOL OUTPUT
)
AS
-- constants:
DECLARE
    @c_NOT_OK           INT,
    @c_ALL_RIGHT        INT,    
    @c_FLAG_INWORKFLOW  INT,    
    @c_TRUE             BOOL,
    @c_FALSE            BOOL

            
-- local variables:
DECLARE 
    @l_oid              OBJECTID,
    @l_currentFlags     INT
            
-- initialize constants:
SELECT  
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_FLAG_INWORKFLOW      = 64,
    @c_TRUE                 = 1,
    @c_FALSE                = 0

-- initialize local variables:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT
-- body:

    -- retrieve current settings
    SELECT  @l_currentFlags = flags
    FROM    ibs_Object
    WHERE   oid = @l_oid
    
    -- set: check if flag is already set
    IF ((@l_currentFlags & @c_FLAG_INWORKFLOW) = @c_FLAG_INWORKFLOW)
      SELECT @ao_workflowFlag = @c_TRUE
    ELSE
      SELECT @ao_workflowFlag = @c_FALSE    
    
GO
-- p_Workflow$getWorkflowFlag


/******************************************************************************
 * Gets the system user of this domain. <BR>
 * 
 * @input parameters:
 * @param   @ai_domainId    id of the domain, for which the system-user
 *                          shall be retrieved
 *
 * @output parameters:
 * @param   @ao_userId      the system-users OID
 *                          -1 if no system user set for domain
 *                          -2 if  multiple system users set for domain
 */
-- delete existing procedure: 
EXEC p_dropProc N'p_Workflow$getSystemUserId'
GO

CREATE PROCEDURE p_Workflow$getSystemUserId
(
    -- input parameters:
    @ai_domainId  INTEGER,
    -- output parameters:    
    @ao_userId    INTEGER OUTPUT
)
AS
DECLARE
-- constants:
-- local variables:
    @rowcount INTEGER
            
-- initialize constants:

-- initialize local variables:

-- body:
    -- init out parameter
    SELECT @ao_userId = -1

    -- retrieve system user of domain
    SELECT  @ao_userId = adminId
    FROM    ibs_Domain_01
    WHERE   id = @ai_domainId
/*    
    SELECT  @ao_userId = u.id
    FROM    ibs_User u, ibs_Object o
    WHERE   UPPER(u.name) = UPPER('SysAdmin')
    AND     u.domainId = @ai_domainId
    AND     u.oid = o.oid
    AND     o.state = 2
*/    

    -- get rowcount
    SELECT @rowcount = @@ROWCOUNT
    
    -- check number of entries
    IF (@rowcount = 0)
      SELECT @ao_userId = -1
    IF (@rowcount <> 1)
      SELECT @ao_userId = -2
GO
-- p_Workflow$getSystemUserId


/******************************************************************************
 * Deletes read-flags for given object and its links. <BR>
 * 
 * @input parameters:
 * @param   @ai_oid_s    oid of the object; string-representation
 *
 * @output parameters:
 */
-- delete existing procedure: 
EXEC p_dropProc N'p_Workflow$delObjReadEntries'
GO

CREATE PROCEDURE p_Workflow$delObjReadEntries
(
    -- input parameters:
    @ai_oid_s           OBJECTIDSTRING
)
AS
DECLARE
-- constants:
-- local variables:
    @l_oid              OBJECTID
    
-- initialize constants:

-- initialize local variables:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

-- body:
    -- delete object-read-entries in table ibs_object
    -- read: for object itself and references on object
/*
    DELETE ibs_ObjectRead
    WHERE oid IN
        ((SELECT oid 
         FROM   ibs_Object
         WHERE  oid = @l_oid)
        UNION
         (SELECT oid 
          FROM   ibs_Object
          WHERE  linkedObjectId = @l_oid))
*/

    -- tuned version
    --
    -- 1. delete read-entries of object itself
    DELETE ibs_ObjectRead
    WHERE  oid = @l_oid
    --
    -- 2. delete read-entries of linked-objects
    DELETE ibs_ObjectRead
    WHERE  oid IN
         (SELECT oid 
          FROM   ibs_Object
          WHERE  linkedObjectId = @l_oid)

GO
-- p_Workflow$delObjReadEntries