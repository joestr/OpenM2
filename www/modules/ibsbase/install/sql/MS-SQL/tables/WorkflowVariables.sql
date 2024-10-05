/******************************************************************************
 *
 * The ibs_Variable table. <BR>
 *
 * @version         2.50.0001, 1.11.2000
 *
 * @author      Horst Pichler (HP)  01112000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_WorkflowVariables
(
    instanceId      OBJECTID       NOT NULL,   -- oid of workflow-instance
    variableName    NVARCHAR(64)   NOT NULL,   -- the name of the variable
    variableValue   NVARCHAR(255)  NOT NULL    -- the value of the variable
)
GO