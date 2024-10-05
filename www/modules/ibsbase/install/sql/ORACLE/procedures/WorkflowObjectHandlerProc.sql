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
CREATE OR REPLACE PROCEDURE p_Workflow$setWorkflowFlag
(
    -- common input parameters:
    ai_oid_s            VARCHAR2,
    ai_setWorkflowFlag  NUMBER
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_FLAG_INWORKFLOW       CONSTANT INTEGER := 64;
    c_TRUE                  CONSTANT NUMBER(1) := 1;
    c_FALSE                 CONSTANT NUMBER(1) := 0;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');    
    
    -- local variables:
    l_oid              RAW(8)   := c_NOOID;
    l_currentFlags     INTEGER  := 0;
    l_isSet            NUMBER(1):= c_FALSE;
    
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    -- body
        
    -- retrieve current settings
    SELECT  flags
    INTO    l_currentFlags
    FROM    ibs_Object
    WHERE   oid = l_oid;
    
    -- check if flag is already set
    IF (B_AND(l_currentFlags, c_FLAG_INWORKFLOW) = c_FLAG_INWORKFLOW)
    THEN
      l_isSet := c_TRUE;
    ELSE
      l_isSet := c_FALSE;
    END IF;
    
    -- change setting only if it is necessary
    IF ((l_isSet = c_TRUE AND ai_setWorkflowFlag = c_FALSE) OR
        (l_isSet = c_FALSE AND ai_setWorkflowFlag = c_TRUE))
    THEN
      UPDATE  ibs_Object
      SET     flags = B_XOR(flags, c_FLAG_INWORKFLOW)
      WHERE   oid = l_oid;
      
      COMMIT WORK;
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workflow$setWorkflowFlag',
            'Input: ' ||
            ', ai_oid_s = ' || ai_oid_s ||
            ', ai_setWorkflowFlag = ' || ai_setWorkflowFlag || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Workflow$setWorkflowFlag;
/
show errors;
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
CREATE OR REPLACE PROCEDURE p_Workflow$getWorkflowFlag
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    -- common input parameters:    
    ao_workflowFlag   OUT NUMBER
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_FLAG_INWORKFLOW       CONSTANT INTEGER := 64;
    c_TRUE                  CONSTANT NUMBER(1) := 1;
    c_FALSE                 CONSTANT NUMBER(1) := 0;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');    
    
    -- local variables:
    l_oid              RAW(8)   := c_NOOID;
    l_currentFlags     INTEGER  := 0;

BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

-- body:

    -- retrieve current settings
    SELECT  flags
    INTO    l_currentFlags
    FROM    ibs_Object
    WHERE   oid = l_oid;
    
    -- set: check if flag is already set
    IF (B_AND(l_currentFlags, c_FLAG_INWORKFLOW) = c_FLAG_INWORKFLOW)
    THEN
      ao_workflowFlag := c_TRUE;
    ELSE
      ao_workflowFlag := c_FALSE;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workflow$getWorkflowFlag',
            'Input: ' ||
            ', ai_oid_s = ' || ai_oid_s ||
            ', ao_workflowFlag = ' || ao_workflowFlag || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Workflow$getWorkflowFlag;
/
show errors;
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
 *                          -3 other error
 */
CREATE OR REPLACE PROCEDURE p_Workflow$getSystemUserId
(
    -- input parameters:
    ai_domainId  INTEGER,
    -- output parameters:
    ao_userId    OUT INTEGER
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    -- local variables:
    l_rowcount              INTEGER := 0;
-- body:
BEGIN
    -- init out parameter
    ao_userId := -1;

    BEGIN
        -- retrieve system user of domain
        SELECT  adminId
        INTO    ao_userId
        FROM    ibs_Domain_01
        WHERE   id = ai_domainId;
        
        -- get rowcount
        l_rowcount := SQL%ROWCOUNT;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        ao_userId := -1;
        RETURN;
    END;

    -- check number of entries: must be 1!
    IF (l_rowcount <> 1) 
    THEN 
        ao_userId := -2;
    END IF;

EXCEPTION WHEN OTHERS THEN
    ao_userId := -3;
    ibs_error.log_error ( ibs_error.error, 'p_Workflow$getSystemUserId',
                          'ai_domainId: ' || ai_domainId ||
                          ', ao_userId: ' || ao_userId);
END p_Workflow$getSystemUserId;
/
show errors;
-- p_Workflow$getSystemUserId


/******************************************************************************
 * Deletes read-flags for given object and its links. <BR>
 * 
 * @input parameters:
 * @param   @ai_oid_s    oid of the object; string-representation
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Workflow$delObjReadEntries
(
    -- input parameters:
    ai_oid_s           VARCHAR2
)
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');    
    -- local variables:
    l_oid              RAW(8)   := c_NOOID;
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

-- body:
    -- delete object-read-entries in table ibs_object
    -- read: for object itself and references on object
/*
    DELETE ibs_ObjectRead
    WHERE oid IN
        (SELECT oid 
         FROM   ibs_Object
         WHERE  oid = l_oid
         OR     linkedObjectId = l_oid);
*/

    -- tuned version
    --
    -- 1. delete read entries of object itself:
    DELETE  ibs_ObjectRead
    WHERE   oid = l_oid;

    --
    -- 2. delete read entries of linked objects:
    DELETE  ibs_ObjectRead
    WHERE   oid IN
            (   SELECT  oid
                FROM    ibs_Object
                WHERE   linkedObjectId = l_oid
            );
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Workflow$delObjReadEntries',
                          'ai_oid_s: ' || ai_oid_s);
END p_Workflow$delObjReadEntries;
/
show errors;
-- p_Workflow$delObjReadEntries


COMMIT WORK;

EXIT;
