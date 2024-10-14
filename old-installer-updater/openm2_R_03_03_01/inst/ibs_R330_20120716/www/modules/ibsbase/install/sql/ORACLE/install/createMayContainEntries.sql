/******************************************************************************
 * Create entries for type dependencies. <BR>
 * This script stores in the database all information regarding which objects
 * may be contained in which other objects.
 *
 * @version     $Id: createMayContainEntries.sql,v 1.28 2004/01/16 00:40:23 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  001018
 ******************************************************************************
 */

-- mayContain entries:
-- l_retValue := p_MayContain$new (majorTypeCode, minorTypeCode);

DECLARE
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function

BEGIN
-- body:
    -- BusinessObject:
    l_retValue := p_MayContain$new ('BusinessObject', 'Container');
    l_retValue := p_MayContain$new ('BusinessObject', 'File');
    l_retValue := p_MayContain$new ('BusinessObject', 'Url');
    --l_retValue := p_MayContain$new ('BusinessObject', 'XMLViewer');
    l_retValue := p_MayContain$new ('BusinessObject', 'XMLViewerContainer');
    l_retValue := p_MayContain$new ('BusinessObject', 'QueryExecutive');
    l_retValue := p_MayContain$new ('BusinessObject', 'StateContainer');
    l_retValue := p_MayContain$new ('BusinessObject', 'Note');

    -- Container:
    l_retValue := p_MayContain$new ('Container', 'Container');
    l_retValue := p_MayContain$new ('Container', 'File');
    l_retValue := p_MayContain$new ('Container', 'Url');
    --l_retValue := p_MayContain$new ('Container', 'XMLViewer');
    l_retValue := p_MayContain$new ('Container', 'XMLViewerContainer');
    l_retValue := p_MayContain$new ('Container', 'QueryExecutive');
    l_retValue := p_MayContain$new ('Container', 'ServicePoint');
    l_retValue := p_MayContain$new ('Container', 'Note');

    -- ConnectorContainer:
    l_retValue := p_MayContain$new ('ConnectorContainer', 'FileConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'FTPConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'MailConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'HTTPConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'EDISwitchConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'HTTPScriptConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'SAPBCXMLRFCConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'HTTPMultipartConnector');
    l_retValue := p_MayContain$new ('ConnectorContainer', 'SAPBCConnector');


    -- DocumentTemplateContainer:
    l_retValue := p_MayContain$new ('DocumentTemplateContainer', 'DocumentTemplate');

    -- ExportContainer:
    l_retValue := p_MayContain$new ('ExportContainer', 'BusinessObject');
    l_retValue := p_MayContain$new ('ExportContainer', 'Container');
    l_retValue := p_MayContain$new ('ExportContainer', 'Referenz');
    l_retValue := p_MayContain$new ('ExportContainer', 'ReferenzContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Attachment');
    l_retValue := p_MayContain$new ('ExportContainer', 'AttachmentContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Menu');
--     l_retValue := p_MayContain$new ('ExportContainer', 'MenuElement');
    l_retValue := p_MayContain$new ('ExportContainer', 'SearchContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'User');
    l_retValue := p_MayContain$new ('ExportContainer', 'Group');
    l_retValue := p_MayContain$new ('ExportContainer', 'Role');
    l_retValue := p_MayContain$new ('ExportContainer', 'Rights');
    l_retValue := p_MayContain$new ('ExportContainer', 'RightsContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Domain');
    l_retValue := p_MayContain$new ('ExportContainer', 'DomainSchemeContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'DomainScheme');
    l_retValue := p_MayContain$new ('ExportContainer', 'NewsContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'RecipientContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Recipient');
    l_retValue := p_MayContain$new ('ExportContainer', 'SentObjectContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'SentObject');
--     l_retValue := p_MayContain$new ('ExportContainer', 'Type');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TypeContainer');
--     l_retValue := p_MayContain$new ('ExportContainer', 'Method');
--     l_retValue := p_MayContain$new ('ExportContainer', 'MethodContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Inbox');
--     l_retValue := p_MayContain$new ('ExportContainer', 'Parameter');
--     l_retValue := p_MayContain$new ('ExportContainer', 'ParameterContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Workspace');
    l_retValue := p_MayContain$new ('ExportContainer', 'UserContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'GroupContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'UserAdminContainer');
--     l_retValue := p_MayContain$new ('ExportContainer', 'UserGroupsContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'UserProfile');
--     l_retValue := p_MayContain$new ('ExportContainer', 'Tab');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TabContainer');
--     l_retValue := p_MayContain$new ('ExportContainer', 'Button');
--     l_retValue := p_MayContain$new ('ExportContainer', 'ButtonContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Workflow');
    l_retValue := p_MayContain$new ('ExportContainer', 'WorkflowTemplate');
    l_retValue := p_MayContain$new ('ExportContainer', 'WorkflowTemplateContainer');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TVersion');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TVersionContainer');
--     l_retValue := p_MayContain$new ('ExportContainer', 'RoleContainer');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TabObject');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TabView');
    l_retValue := p_MayContain$new ('ExportContainer', 'MembershipContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Root');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TypeReference');
--     l_retValue := p_MayContain$new ('ExportContainer', 'TypeReferenceContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'CleanContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ReceivedObject');
    l_retValue := p_MayContain$new ('ExportContainer', 'LogContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'File');
    l_retValue := p_MayContain$new ('ExportContainer', 'Url');
    l_retValue := p_MayContain$new ('ExportContainer', 'LayoutContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Layout');
--    l_retValue := p_MayContain$new ('ExportContainer', 'Integrator');
    l_retValue := p_MayContain$new ('ExportContainer', 'ObjectSearchContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'IntegratorContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ImportScript');
    l_retValue := p_MayContain$new ('ExportContainer', 'ImportScriptContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Connector');
    l_retValue := p_MayContain$new ('ExportContainer', 'ConnectorContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Translator');
    l_retValue := p_MayContain$new ('ExportContainer', 'ASCIITranslator');
    l_retValue := p_MayContain$new ('ExportContainer', 'TranslatorContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'FileConnector');
    l_retValue := p_MayContain$new ('ExportContainer', 'FTPConnector');
    l_retValue := p_MayContain$new ('ExportContainer', 'MailConnector');
    l_retValue := p_MayContain$new ('ExportContainer', 'HTTPConnector');
    l_retValue := p_MayContain$new ('ExportContainer', 'EDISwitchConnector');
    l_retValue := p_MayContain$new ('ExportContainer', 'HTTPScriptConnector');
    l_retValue := p_MayContain$new ('ExportContainer', 'HTTPMultipartConnector');
    --l_retValue := p_MayContain$new ('ExportContainer', 'XMLViewer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ImportContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ExportContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'DocumentTemplate');
    l_retValue := p_MayContain$new ('ExportContainer', 'DocumentTemplateContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'XMLViewerContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'HelpContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Help');
    l_retValue := p_MayContain$new ('ExportContainer', 'QueryCreator');
    l_retValue := p_MayContain$new ('ExportContainer', 'DBQueryCreator');
    l_retValue := p_MayContain$new ('ExportContainer', 'QueryExecutive');
    l_retValue := p_MayContain$new ('ExportContainer', 'StateContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'MenuTabContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'MenuTab');
    l_retValue := p_MayContain$new ('ExportContainer', 'QuerySelectContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'EDITranslator');
    l_retValue := p_MayContain$new ('ExportContainer', 'Note');

    -- ImportScriptContainer:
    l_retValue := p_MayContain$new ('ImportScriptContainer', 'ImportScript');

    -- IntegratorContainer:
    l_retValue := p_MayContain$new ('IntegratorContainer', 'Container');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'IntegratorContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'ImportContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'ExportContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'ImportScriptContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'ConnectorContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'XMLViewerContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'DocumentTemplateContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'WorkflowTemplateContainer');
    l_retValue := p_MayContain$new ('IntegratorContainer', 'TranslatorContainer');

    -- TranslatorContainer:
    l_retValue := p_MayContain$new ('TranslatorContainer', 'Translator');
    l_retValue := p_MayContain$new ('TranslatorContainer', 'ASCIITranslator');
    l_retValue := p_MayContain$new ('TranslatorContainer', 'EDITranslator');

    -- XMLViewerContainer:
    --l_retValue := p_MayContain$new ('XMLViewerContainer', 'XMLViewer');

    -- Inbox:
    l_retValue := p_MayContain$new ('Inbox', 'ReceivedObject');

    -- RecipientContainer:
    l_retValue := p_MayContain$new ('RecipientContainer', 'Recipient');

    -- SentObjectContainer:
    l_retValue := p_MayContain$new ('SentObjectContainer', 'SentObject');

    -- DomainSchemeContainer:
    l_retValue := p_MayContain$new ('DomainSchemeContainer', 'DomainScheme');

    -- Domain:
    l_retValue := p_MayContain$new ('Domain', 'Container');
    l_retValue := p_MayContain$new ('Domain', 'Workspace');

    -- AttachmentContainer:
    l_retValue := p_MayContain$new ('AttachmentContainer', 'Attachment');

    -- HelpContainer:
    l_retValue := p_MayContain$new ('HelpContainer', 'Help');
    l_retValue := p_MayContain$new ('HelpContainer', 'HelpContainer');

    -- LayoutContainer:
    l_retValue := p_MayContain$new ('LayoutContainer', 'Layout');

    -- MenuTabContainer
    l_retValue := p_MayContain$new ('MenuTabContainer', 'MenuTab');

    -- ReferenzContainer:
    l_retValue := p_MayContain$new ('ReferenzContainer', 'Referenz');

    -- Workspace:
    l_retValue := p_MayContain$new ('Workspace', 'Inbox');
    l_retValue := p_MayContain$new ('Workspace', 'SentObjectContainer');
    l_retValue := p_MayContain$new ('Workspace', 'Container');
    l_retValue := p_MayContain$new ('Workspace', 'WasteBasket');

    -- Group:
    l_retValue := p_MayContain$new ('Group', 'Group');
    l_retValue := p_MayContain$new ('Group', 'User');

    -- WorkflowTemplateContainer:
    l_retValue := p_MayContain$new ('WorkflowTemplateContainer', 'WorkflowTemplate');

    -- Root:
    l_retValue := p_MayContain$new ('Root', 'Domain');

    -- GroupContainer:
    l_retValue := p_MayContain$new ('GroupContainer', 'Group');

    -- MembershipContainer:
    l_retValue := p_MayContain$new ('MembershipContainer', 'Group');

    -- UserAdminContainer:
    l_retValue := p_MayContain$new ('UserAdminContainer', 'GroupContainer');
    l_retValue := p_MayContain$new ('UserAdminContainer', 'UserContainer');

    -- UserContainer:
    l_retValue := p_MayContain$new ('UserContainer', 'User');

    -- QueryCreatorContainer:
    l_retValue := p_MayContain$new ('QueryCreatorContainer', 'QueryCreator');
    l_retValue := p_MayContain$new ('QueryCreatorContainer', 'DBQueryCreator');
    l_retValue := p_MayContain$new ('QueryCreatorContainer', 'QueryCreatorContainer');
    
    -- WorkspaceTemplateContainer:
    l_retValue := p_MayContain$new ('WorkspaceTemplateContainer', 'WorkspaceTemplate');
END;
/

EXIT;
