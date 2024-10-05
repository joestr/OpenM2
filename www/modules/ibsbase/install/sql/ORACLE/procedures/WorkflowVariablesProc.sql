/******************************************************************************
 * All stored procedures regarding the Workflows variables. <BR>
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
 * @param @ai_variableName        name of the variable
 * @param @ai_variableValue       value of the variable
 *
 * @output parameters:
 *
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_WorkflowVariables$crtEntry
(
    -- common input parameters:
    ai_instanceId_s        VARCHAR2,
    ai_variableName        VARCHAR2,
    ai_variableValue       VARCHAR2
)
AS
-- constants:
-- local variables:
   l_count        INTEGER := 0;
   l_instanceId   RAW(8);
BEGIN
-- initialize constants:
-- initialize local variables:
    p_stringToByte (ai_instanceId_s, l_instanceId);

-- body:
    -- check if entry already exists
    SELECT  COUNT(*)  
    INTO    l_count
    FROM    ibs_WorkflowVariables
    WHERE   instanceId = l_instanceId
    AND     variableName = ai_variableName;
        
    -- check if there were already an entry
    IF (l_count = 0)
    THEN
        INSERT INTO ibs_WorkflowVariables
        VALUES (l_instanceId, ai_variableName, ai_variableValue);
    ELSE
        UPDATE  ibs_WorkflowVariables
        SET     variableValue = ai_variableValue
        WHERE   instanceId = l_instanceId
        AND     variableName = ai_variableName;
    END IF;
            
   COMMIT WORK;


EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_WorkflowVariables$crtEntry',
            'Input: ' ||
            ', ai_instanceId_s = ' || ai_instanceId_s ||
            ', ai_variableName = ' || ai_variableName || 
            ', ai_variableValue = ' || ai_variableValue || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_WorkflowVariables$crtEntry;
/
show errors;
-- p_WorkflowVariables$crtEntry
EXIT;