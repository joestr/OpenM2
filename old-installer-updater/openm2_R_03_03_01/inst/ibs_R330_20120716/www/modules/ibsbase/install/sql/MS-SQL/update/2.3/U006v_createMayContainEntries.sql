/******************************************************************************
 * Create entries for type dependencies. <BR>
 * This script stores in the database all information regarding which objects
 * may be contained in which other objects.
 *
 * @version     $Id: U006v_createMayContainEntries.sql,v 1.1 2004/01/19 11:19:33 klaus Exp $
 *
 * @author      Klaus Reim?ller (KR)  001018
 ******************************************************************************
 */

-- mayContain entries:
-- l_retValue := p_MayContain$new (majorTypeCode, minorTypeCode);

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT             -- return value of a function

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- BusinessObject:
    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'Container'
    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'File'
    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'Url'
--    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'XMLViewer'
    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'XMLViewerContainer'
    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'QueryExecutive'
    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'StateContainer'
    EXEC @l_retValue = p_MayContain$new 'BusinessObject', 'Note'

    -- Container:
    EXEC @l_retValue = p_MayContain$new 'Container', 'Container'
    EXEC @l_retValue = p_MayContain$new 'Container', 'File'
    EXEC @l_retValue = p_MayContain$new 'Container', 'Url'
--    EXEC @l_retValue = p_MayContain$new 'Container', 'XMLViewer'
    EXEC @l_retValue = p_MayContain$new 'Container', 'XMLViewerContainer'
    EXEC @l_retValue = p_MayContain$new 'Container', 'QueryExecutive'
    EXEC @l_retValue = p_MayContain$new 'Container', 'ServicePoint'
    EXEC @l_retValue = p_MayContain$new 'Container', 'Note'

    -- ConnectorContainer:
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'FileConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'FTPConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'MailConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'HTTPConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'EDISwitchConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'HTTPScriptConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'SAPBCXMLRFCConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'SAPBCConnector'
    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'HTTP MultipartConnector'

    -- DocumentTemplateContainer:
    EXEC @l_retValue = p_MayContain$new 'DocumentTemplateContainer', 'DocumentTemplateContainer'
    EXEC @l_retValue = p_MayContain$new 'DocumentTemplateContainer', 'DocumentTemplate'

    -- ExportContainer:
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'BusinessObject'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Container'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Referenz'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ReferenzContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Attachment'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'AttachmentContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Menu'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'MenuElement'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'SearchContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'User'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Group'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Role'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Rights'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'RightsContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Domain'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'DomainSchemeContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'DomainScheme'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'NewsContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'RecipientContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Recipient'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'SentObjectContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'SentObject'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Type'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TypeContainer'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Method'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'MethodContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Inbox'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Parameter'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ParameterContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Workspace'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'UserContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'GroupContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'UserAdminContainer'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'UserGroupsContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'UserProfile'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Tab'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TabContainer'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Button'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ButtonContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Workflow'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'WorkflowTemplate'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'WorkflowTemplateContainer'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TVersion'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TVersionContainer'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'RoleContainer'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TabObject'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TabView'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'MembershipContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Root'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TypeReference'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TypeReferenceContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'CleanContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ReceivedObject'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'LogContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'File'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Url'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'LayoutContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Layout'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Integrator'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ObjectSearchContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'IntegratorContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ImportScript'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ImportScriptContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Connector'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ConnectorContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Translator'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ASCIITranslator'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'TranslatorContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'FileConnector'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'FTPConnector'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'MailConnector'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'HTTPConnector'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'EDISwitchConnector'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'HTTPScriptConnector'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'HTTPMultipartConnector'
--    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'XMLViewer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ImportContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'ExportContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'DocumentTemplate'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'DocumentTemplateContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'XMLViewerContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'HelpContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Help'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'QueryCreatorContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'QueryCreator'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'DBQueryCreator'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'QueryExecutive'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'StateContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'MenuTabContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'MenuTab'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'QuerySelectContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'EDITranslator'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Note'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'WorkspaceTemplate'

    -- ImportScriptContainer:
    EXEC @l_retValue = p_MayContain$new 'ImportScriptContainer', 'ImportScript'

    -- IntegratorContainer:
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'Container'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'IntegratorContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'ImportContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'ExportContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'ImportScriptContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'ConnectorContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'XMLViewerContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'DocumentTemplateContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'WorkflowTemplateContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'TranslatorContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'WorkspaceTemplateContainer'
    EXEC @l_retValue = p_MayContain$new 'IntegratorContainer', 'QueryCreatorContainer'

    -- TranslatorContainer:
    EXEC @l_retValue = p_MayContain$new 'TranslatorContainer', 'Translator'
    EXEC @l_retValue = p_MayContain$new 'TranslatorContainer', 'ASCIITranslator'
    EXEC @l_retValue = p_MayContain$new 'TranslatorContainer', 'EDITranslator'

    -- XMLViewerContainer:
--    EXEC @l_retValue = p_MayContain$new 'XMLViewerContainer', 'XMLViewer'

    -- Inbox:
    EXEC @l_retValue = p_MayContain$new 'Inbox', 'ReceivedObject'

    -- RecipientContainer:
    EXEC @l_retValue = p_MayContain$new 'RecipientContainer', 'Recipient'

    -- SentObjectContainer:
    EXEC @l_retValue = p_MayContain$new 'SentObjectContainer', 'SentObject'

    -- DomainSchemeContainer:
    EXEC @l_retValue = p_MayContain$new 'DomainSchemeContainer', 'DomainScheme'

    -- Domain:
    EXEC @l_retValue = p_MayContain$new 'Domain', 'Container'
    EXEC @l_retValue = p_MayContain$new 'Domain', 'Workspace'

    -- AttachmentContainer:
    EXEC @l_retValue = p_MayContain$new 'AttachmentContainer', 'Attachment'

    -- HelpContainer:
    EXEC @l_retValue = p_MayContain$new 'HelpContainer', 'Help'
    EXEC @l_retValue = p_MayContain$new 'HelpContainer', 'HelpContainer'

    -- LayoutContainer:
    EXEC @l_retValue = p_MayContain$new 'LayoutContainer', 'Layout'

    -- MenuTabContainer
    EXEC @l_retValue = p_MayContain$new 'MenuTabContainer', 'MenuTab'

    -- ReferenzContainer:
    EXEC @l_retValue = p_MayContain$new 'ReferenzContainer', 'Referenz'

    -- Workspace:
    EXEC @l_retValue = p_MayContain$new 'Workspace', 'Inbox'
    EXEC @l_retValue = p_MayContain$new 'Workspace', 'SentObjectContainer'
    EXEC @l_retValue = p_MayContain$new 'Workspace', 'Container'
    EXEC @l_retValue = p_MayContain$new 'Workspace', 'WasteBasket'

    -- Group:
    EXEC @l_retValue = p_MayContain$new 'Group', 'Group'
    EXEC @l_retValue = p_MayContain$new 'Group', 'User'

    -- WorkflowTemplateContainer:
    EXEC @l_retValue = p_MayContain$new 'WorkflowTemplateContainer', 'WorkflowTemplate'

    -- Root:
    EXEC @l_retValue = p_MayContain$new 'Root', 'Domain'

    -- GroupContainer:
    EXEC @l_retValue = p_MayContain$new 'GroupContainer', 'Group'

    -- MembershipContainer:
    EXEC @l_retValue = p_MayContain$new 'MembershipContainer', 'Group'

    -- UserAdminContainer:
    EXEC @l_retValue = p_MayContain$new 'UserAdminContainer', 'GroupContainer'
    EXEC @l_retValue = p_MayContain$new 'UserAdminContainer', 'UserContainer'

    -- UserContainer:
    EXEC @l_retValue = p_MayContain$new 'UserContainer', 'User'

    -- QueryCreatorContainer:
    EXEC @l_retValue = p_MayContain$new 'QueryCreatorContainer', 'QueryCreator'
    EXEC @l_retValue = p_MayContain$new 'QueryCreatorContainer', 'DBQueryCreator'
    EXEC @l_retValue = p_MayContain$new 'QueryCreatorContainer', 'QueryCreatorContainer'

    -- WorkspaceTemplateContainer:
    EXEC @l_retValue = p_MayContain$new 'WorkspaceTemplateContainer', 'WorkspaceTemplate'
GO


-- show count messages again:
SET NOCOUNT OFF
GO
