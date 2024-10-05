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
    instanceId      RAW(8)          NOT NULL,   -- oid of workflow-instance
    variableName    VARCHAR2(64)    NOT NULL,   -- the name of the variable
    variableValue   VARCHAR2(255)   NOT NULL    -- the value of the variable
);

EXIT;