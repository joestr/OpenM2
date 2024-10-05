--------------------------------------------------------------------------------
 -- All stored procedures regarding the Workflows variables. <BR>
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
 -- @param @ai_variableName        name of the variable
 -- @param @ai_variableValue       value of the variable
 --
 -- @output parameters:
 
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_WorkflowVariables$crtEntry');
    -- create the new procedure
CREATE PROCEDURE IBSDEV1.p_WorkflowVariables$crtEntry
(
    -- common input parameters:
    IN ai_instanceId_s    VARCHAR (18),
    IN ai_variableName    VARCHAR (64),
    IN ai_variableValue   VARCHAR (255)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- constants:
    -- local variables:
    DECLARE l_count         INT;
    DECLARE l_instanceId  CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize constants:
    -- initialize local variables:
    CALL IBSDEV1.p_stringToByte (ai_instanceId_s, l_instanceId);

-- body:

    -- check if entry already exists
    SELECT COUNT(*)
    INTO l_count
    FROM IBSDEV1.ibs_WorkflowVariables
    WHERE instanceId = l_instanceId
        AND variableName = ai_variableName;

    -- check if there were already an entry
    IF l_count = 0 THEN
        INSERT INTO IBSDEV1.ibs_WorkflowVariables
        VALUES (l_instanceId, ai_variableName, ai_variableValue);
    ELSE
        UPDATE IBSDEV1.ibs_WorkflowVariables
        SET variableValue = ai_variableValue
        WHERE instanceId = l_instanceId
            AND variableName = ai_variableName;
    END IF;
    COMMIT;
END;
-- p_WorkflowVariables$crtEntry