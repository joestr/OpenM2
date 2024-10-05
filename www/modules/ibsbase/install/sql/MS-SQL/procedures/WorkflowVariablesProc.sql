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
-- delete existing procedure:
EXEC p_dropProc N'p_WorkflowVariables$crtEntry'
GO

-- create the new procedure:
CREATE PROCEDURE p_WorkflowVariables$crtEntry
(
    -- common input parameters:
    @ai_instanceId_s        OBJECTIDSTRING,
    @ai_variableName        NVARCHAR(64),
    @ai_variableValue       NVARCHAR(255)
)
AS
-- constants:
            
-- local variables:
DECLARE 
    @l_count        INT,
    @l_instanceId   OBJECTID
                
-- initialize constants:

-- initialize local variables:
    EXEC p_stringToByte @ai_instanceId_s, @l_instanceId OUTPUT

-- body:
    BEGIN TRANSACTION
        -- check if entry already exists
        SELECT  @l_count = COUNT(*)  
        FROM    ibs_WorkflowVariables
        WHERE   instanceId = @l_instanceId
        AND     variableName = @ai_variableName
        
        -- check if there were already an entry
        IF (@l_count = 0)
        BEGIN
            INSERT INTO ibs_WorkflowVariables
            VALUES (@l_instanceId, @ai_variableName, @ai_variableValue)
        END
        ELSE
        BEGIN
            UPDATE  ibs_WorkflowVariables
            SET     variableValue = @ai_variableValue
            WHERE   instanceId = @l_instanceId
            AND     variableName = @ai_variableName
        END
            
   COMMIT TRANSACTION
GO
-- p_WorkflowVariables$crtEntry