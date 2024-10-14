--------------------------------------------------------------------------------
 -- All stored procedures regarding the workflow service. <BR>
 --
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:52 $
--              $Author: klaus $
--
-- author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
 -- Set the workflow-flag in the BusinessObject. <BR>
 -- 
 -- @input parameters:
 -- @param   @ai_oid_s           ID of the object where the flag shall be set.
 -- @param   @ai_setWorkflowFlag Sets or unsets the workflow flag.
 --
 -- @output parameters:
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Workflow$getWorkflowFlag');
    -- create the new procedure
CREATE PROCEDURE IBSDEV1.p_Workflow$getWorkflowFlag
(
    -- common input parameters:
    IN ai_oid_s             VARCHAR (18),
    -- common output parameters:
    OUT ao_workflowFlag     SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_FLAG_INWORKFLOW INT;
    DECLARE c_TRUE          SMALLINT;
    DECLARE c_FALSE         SMALLINT;
    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_currentFlags  INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
    -- initialize constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_FLAG_INWORKFLOW = 64;
    SET c_TRUE = 1;
    SET c_FALSE = 0;

    -- initialize local variables:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

-- body:
    -- retrieve current settings
    SELECT flags
    INTO l_currentFlags
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_oid;

    -- set: check if flag is already set
    IF B_AND (l_currentFlags, c_FLAG_INWORKFLOW) = c_FLAG_INWORKFLOW THEN
        SET ao_workflowFlag = c_TRUE;
    ELSE
        SET ao_workflowFlag = c_FALSE;
    END IF;
END;
-- p_Workflow$getWorkflowFlag



--------------------------------------------------------------------------------
 -- Get the workflow-flag of the given BusinessObject. <BR>
 -- 
 -- @input parameters:
 -- @param   @ai_oid_s           ID of the object where the flag shall be set.
 --
 -- @output parameters:
 -- @param   @ao_workflowFlag Sets or unsets the workflow flag. 
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Workflow$setWorkflowFlag');
    -- create the new procedure
CREATE PROCEDURE IBSDEV1.p_Workflow$setWorkflowFlag
(
    -- common input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_setWorkflowFlag   SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_FLAG_INWORKFLOW INT;
    DECLARE c_TRUE          SMALLINT;
    DECLARE c_FALSE         SMALLINT;

    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_currentFlags  INT;
    DECLARE l_isSet         SMALLINT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_FLAG_INWORKFLOW = 64;
    SET c_TRUE = 1;
    SET c_FALSE = 0;

    -- initialize local variables:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

-- body:
    -- retrieve current settings
    SELECT flags
    INTO l_currentFlags
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_oid;

    -- check if flag is already set
    IF B_AND (l_currentFlags, c_FLAG_INWORKFLOW) = c_FLAG_INWORKFLOW THEN
        SET l_isSet = c_TRUE;
    ELSE
        SET l_isSet = c_FALSE;
    END IF;

    -- change setting only if it is necessary
    IF l_isSet = c_TRUE AND ai_setWorkflowFlag = c_FALSE OR
        l_isSet = c_FALSE AND ai_setWorkflowFlag = c_TRUE THEN
        CALL IBSDEV1.p_Debug('p_Workflow$setWorkflowFlag');
    END IF;

    UPDATE IBSDEV1.ibs_Object
    SET flags = B_XOR(flags, c_FLAG_INWORKFLOW)
    WHERE oid = l_oid;
END;
-- p_Workflow$setWorkflowFlag

--------------------------------------------------------------------------------
 -- Gets the system user of this domain. <BR>
 -- 
 -- @input parameters:
 -- @param   @ai_domainId    id of the domain, for which the system-user
 --                          shall be retrieved
 --
 -- @output parameters:
 -- @param   @ao_userId      the system-users OID
 --                          -1 if no system user set for domain
 --                          -2 if  multiple system users set for domain
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Workflow$getSystemUserId');
    -- create the new procedure
CREATE PROCEDURE IBSDEV1.p_Workflow$getSystemUserId
(
    -- input parameters:
    IN ai_domainId          INT,
    -- output parameters:
    OUT ao_userId           INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- constants:
    -- local variables:
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- initialize constants:
    -- initialize local variables:
-- body:
    -- init out parameter
    SET ao_userId = -1;

    -- retrieve system user of domain
    SELECT adminId
    INTO ao_userId
    FROM IBSDEV1.ibs_Domain_01
    WHERE id = ai_domainId;
    -- get rowcount
    SELECT COUNT(*)
    INTO l_rowcount
    FROM IBSDEV1.ibs_Domain_01
    WHERE id = ai_domainId;

    -- check number of entries
    IF l_rowcount = 0 THEN
        SET ao_userId = -1;
    END IF;
    IF l_rowcount <> 1 THEN
        SET ao_userId = -2;
    END IF;
END;
-- p_Workflow$getSystemUserId



--------------------------------------------------------------------------------
 -- Deletes read-flags for given object and its links. <BR>
 -- 
 -- @input parameters:
 -- @param   @ai_oid_s    oid of the object; string-representation
 --
 -- @output parameters:
 --
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_Workflow$delObjReadEntries');
-- create the new procedure
CREATE PROCEDURE IBSDEV1.p_Workflow$delObjReadEntries
(
-- input parameters:
  IN ai_oid_s               VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;
    -- constants:
    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- initialize constants:
-- initialize local variables:

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

-- body:
    -- delete object-read-entries in table ibs_object
    -- read: for object itself and references on object

    -- tuned version
    --
    -- 1. delete read-entries of object itself
    DELETE FROM IBSDEV1.ibs_ObjectRead
    WHERE OID = l_oid;
    COMMIT;

    --
    -- 2. delete read-entries of linked-objects
    DELETE FROM IBSDEV1.ibs_ObjectRead
    WHERE oid IN    (
                        SELECT oid
                        FROM IBSDEV1.ibs_Object
                        WHERE linkedObjectId = l_oid
                    );
    COMMIT;
END;
-- p_Workflow$delObjReadEntries