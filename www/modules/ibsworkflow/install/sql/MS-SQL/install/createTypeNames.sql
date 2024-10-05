/******************************************************************************
 * All typenames within the framework. <BR>
 *
 * @version     $Id: createTypeNames.sql,v 1.1 2010/04/15 10:40:54 rburgermann Exp $
 *
 * @author      autogenerated by m2 MultiLang
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_TypeName_01$new 0,'TN_WorkflowTemplate_01', 'workflow template', 'ibs.bo.type.TypeConstants'
EXEC p_TypeName_01$new 0,'TN_WorkflowTemplateContainer_01', 'workflow template container', 'ibs.bo.type.TypeConstants'
EXEC p_TypeName_01$new 0,'TN_Workflow_01', 'workflow', 'ibs.bo.type.TypeConstants'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
