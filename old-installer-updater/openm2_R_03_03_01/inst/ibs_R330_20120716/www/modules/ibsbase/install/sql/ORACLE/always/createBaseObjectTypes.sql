/******************************************************************************
 * Create all business object types within the framework. <BR>
 *
 * @version     $Id: createBaseObjectTypes.sql,v 1.50 2003/12/30 00:09:04 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */

-- p_Type$newLang (id, superTypeCode, isContainer, isInheritable,
--      isSearchable, showInMenu, showInNews, code, className, languageId,
--      typeNameName);
-- ex.:
-- p_Type$newLang (stringToInt ('0x01010050'), 'BusinessObject', 0, 1, 1, 0, 1,
--    'Attachment', 'ibs.obj.doc.Attachment_01', @c_languageId, 'TN_Attachment_01');

DECLARE
    -- constants:
    c_languageId            CONSTANT INTEGER := 0; -- the current language

BEGIN

-- BusinessObject
p_Type$newLang (stringToInt ('0x01010010'), '', 0, 1, 0, 0, 0, 'BusinessObject',
    'ibs.bo.BusinessObject', c_languageId, 'TN_BusinessObject');
-- Container
p_Type$newLang (stringToInt ('0x01010020'), 'BusinessObject', 1, 1, 1, 1, 0, 'Container',
    'ibs.bo.Container', c_languageId, 'TN_Container');

-- references:
-- Referenz
p_Type$newLang (stringToInt ('0x01010030'), 'BusinessObject', 0, 0, 0, 0, 0, 'Referenz',
    'ibs.obj.ref.Referenz_01', c_languageId, 'TN_Referenz_01');

-- ReferenzContainer
p_Type$newLang (stringToInt ('0x01010040'), 'Container', 1, 0, 0, 0, 0, 'ReferenzContainer',
    'ibs.obj.ref.ReferenzContainer_01', c_languageId, 'TN_ReferenzContainer_01');

-- attachments:
-- Attachment
p_Type$newLang (stringToInt ('0x01010050'), 'BusinessObject', 0, 1, 1, 0, 1, 'Attachment',
    'ibs.obj.doc.Attachment_01', c_languageId, 'TN_Attachment_01');

-- AttachmentContainer
p_Type$newLang (stringToInt ('0x01010060'), 'Container', 1, 1, 0, 0, 0, 'AttachmentContainer',
    'ibs.obj.doc.AttachmentContainer_01', c_languageId, 'TN_AttachmentContainer_01');

-- menu:
-- Menu
p_Type$newLang (stringToInt ('0x01010070'), 'Container', 1, 1, 0, 1, 0, 'Menu',
    'ibs.obj.menu.Menu_01', c_languageId, 'TN_Menu_01');

/* currently not available
-- MenuElement
p_Type$newLang (stringToInt ('0x01010080'), 'BusinessObject', 0, 1, 0, 1, 0, 'MenuElement',
    'ibs.obj.menu.MenuElement_01', c_languageId, 'TN_MenuElement_01');
*/

-- search:
-- SearchContainer
p_Type$newLang (stringToInt ('0x01010090'), 'Container', 1, 1, 0, 1, 0, 'SearchContainer',
    'ibs.obj.search.SearchContainer_01', c_languageId, 'TN_SearchContainer_01');
-- user management:
-- User
p_Type$newLang (stringToInt ('0x010100A0'), 'BusinessObject', 0, 1, 1, 0, 0, 'User',
    'ibs.obj.user.User_01', c_languageId, 'TN_User_01');

-- Group
p_Type$newLang (stringToInt ('0x010100B0'), 'BusinessObject', 0, 1, 1, 0, 0, 'Group',
    'ibs.obj.user.Group_01', c_languageId, 'TN_Group_01');

-- rights management:
-- Rights
p_Type$newLang (stringToInt ('0x010100D0'), 'BusinessObject', 0, 0, 0, 0, 0, 'Rights',
    'ibs.obj.user.Rights_01', c_languageId, 'TN_Rights_01');

-- RightsContainer
p_Type$newLang (stringToInt ('0x010100E0'), 'Container', 1, 0, 0, 0, 0, 'RightsContainer',
    'ibs.obj.user.RightsContainer_01', c_languageId, 'TN_RightsContainer_01');

-- domains:
-- Domain
p_Type$newLang (stringToInt ('0x010100F0'), 'Container', 1, 0, 0, 0, 0, 'Domain',
    'ibs.obj.dom.Domain_01', c_languageId, 'TN_Domain_01');

-- DomainSchemeContainer
p_Type$newLang (stringToInt ('0x01010110'), 'Container', 1, 1, 0, 1, 0, 'DomainSchemeContainer',
    'ibs.obj.dom.DomainSchemeContainer_01', c_languageId,
    'TN_DomainSchemeContainer_01');
-- DomainScheme
p_Type$newLang (stringToInt ('0x01010120'), 'BusinessObject', 0, 1, 0, 0, 0, 'DomainScheme',
    'ibs.obj.dom.DomainScheme_01', c_languageId, 'TN_DomainScheme_01');

-- news:
-- NewsContainer
p_Type$newLang (stringToInt ('0x01010800'), 'Container', 1, 1, 0, 1, 0, 'NewsContainer',
    'ibs.obj.wsp.NewsContainer_01', c_languageId, 'TN_NewsContainer_01');

-- distribution:
-- RecipientContainer
p_Type$newLang (stringToInt ('0x01011B00'), 'Container', 1, 1, 0, 0, 0, 'RecipientContainer',
    'ibs.obj.wsp.RecipientContainer_01', c_languageId, 'TN_RecipientContainer_01');
-- Recipient
p_Type$newLang (stringToInt ('0x01011C00'), 'BusinessObject', 0, 1, 0, 0, 0, 'Recipient',
    'ibs.obj.wsp.Recipient_01', c_languageId, 'TN_Recipient_01');

-- SentObjectContainer
p_Type$newLang (stringToInt ('0x01011D00'), 'Container', 1, 1, 0, 1, 0, 'SentObjectContainer',
    'ibs.obj.wsp.SentObjectContainer_01', c_languageId, 'TN_SentObjectContainer_01');

-- SentObject
p_Type$newLang (stringToInt ('0x01011E00'), 'BusinessObject', 0, 1, 0, 0, 0, 'SentObject',
    'ibs.obj.wsp.SentObject_01', c_languageId, 'TN_SentObject_01');

/* currently not available
-- customizing/types:
-- Type
p_Type$newLang (stringToInt ('0x01012300'), 'BusinessObject', 0, 1, 0, 0, 0, 'Type',
    'ibs.cust.Type_01', c_languageId, 'TN_Type_01');

-- TypeContainer
p_Type$newLang (stringToInt ('0x01012400'), 'Container', 1, 1, 0, 1, 0, 'TypeContainer',
    'ibs.cust.TypeContainer_01', c_languageId, 'TN_TypeContainer_01');
-- Method
p_Type$newLang (stringToInt ('0x01012700'), 'BusinessObject', 0, 1, 0, 0, 0, 'Method',
    'ibs.cust.Method_01', c_languageId, 'TN_Method_01');

-- MethodContainer
p_Type$newLang (stringToInt ('0x01012800'), 'Container', 1, 1, 0, 0, 0, 'MethodContainer',
    'ibs.cust.MethodContainer_01', c_languageId, 'TN_MethodContainer_01');
*/

-- distribution:
-- Inbox
p_Type$newLang (stringToInt ('0x01012D00'), 'Container', 1, 1, 0, 1, 0, 'Inbox',
    'ibs.obj.wsp.Inbox_01', c_languageId, 'TN_Inbox_01');

/* currently not available
-- customizing/types:
-- Parameter
p_Type$newLang (stringToInt ('0x01013000'), 'BusinessObject', 0, 1, 0, 0, 0, 'Parameter',
    'ibs.cust.Parameter_01', c_languageId, 'TN_Parameter_01');
-- ParameterContainer
p_Type$newLang (stringToInt ('0x01013100'), 'Container', 1, 1, 0, 0, 0, 'ParameterContainer',
    'ibs.cust.ParameterContainer_01', c_languageId, 'TN_ParameterContainer_01');
*/

-- user management:
-- Workspace
p_Type$newLang (stringToInt ('0x01013200'), 'Container', 1, 1, 0, 1, 0, 'Workspace',
    'ibs.obj.wsp.Workspace_01', c_languageId, 'TN_Workspace_01');

-- UserContainer
p_Type$newLang (stringToInt ('0x01013300'), 'Container', 1, 1, 0, 1, 0, 'UserContainer',
    'ibs.obj.user.UserContainer_01', c_languageId, 'TN_UserContainer_01');

-- GroupContainer
p_Type$newLang (stringToInt ('0x01013400'), 'Container', 1, 1, 0, 1, 0, 'GroupContainer',
    'ibs.obj.user.GroupContainer_01', c_languageId, 'TN_GroupContainer_01');

/*
-- RoleContainer
p_Type$newLang (stringToInt ('0x01013500'), 'Container', 1, 1, 0, 0, 0, 'RoleContainer',
    'ibs.obj.user.RoleContainer_01', c_languageId, 'TN_RoleContainer_01');
*/
-- UserAdminContainer
p_Type$newLang (stringToInt ('0x01013600'), 'Container', 1, 1, 0, 1, 0, 'UserAdminContainer',
    'ibs.obj.user.UserAdminContainer_01', c_languageId, 'TN_UserAdminContainer_01');

/* currently not available
-- UserGroupsContainer
p_Type$newLang (stringToInt ('0x01013700'), 'Container', 1, 1, 0, 0, 0, 'UserGroupsContainer',
    'ibs.obj.user.UserGroupsContainer_01', c_languageId,
    'TN_UserGroupsContainer_01');
*/

-- UserAddress_01
-- inherits from BusinessObject
p_Type$newLang (stringToInt ('0x01012F10'), 'BusinessObject', 0, 1, 0, 0, 0, 'UserAddress',
    'ibs.obj.user.UserAddress_01', c_languageId, 'TN_UserAddress_01');

-- UserProfile
p_Type$newLang (stringToInt ('0x01013800'), 'BusinessObject', 0, 1, 0, 1, 0, 'UserProfile',
    'ibs.obj.user.UserProfile_01', c_languageId, 'TN_UserProfile_01');

/* currently not available
-- customizing/types:
-- Tab
p_Type$newLang (stringToInt ('0x01013900'), 'BusinessObject', 0, 1, 0, 0, 0, 'Tab',
    'ibs.cust.Tab_01', c_languageId, 'TN_Tab_01');

-- TabContainer
p_Type$newLang (stringToInt ('0x01013A00'), 'Container', 1, 1, 0, 0, 0, 'TabContainer',
    'ibs.cust.TabContainer_01', c_languageId, 'TN_TabContainer_01');
-- Button
p_Type$newLang (stringToInt ('0x01013B00'), 'BusinessObject', 0, 1, 0, 0, 0, 'Button',
    'ibs.cust.Button_01', c_languageId, 'TN_Button_01');
-- ButtonContainer
p_Type$newLang (stringToInt ('0x01013C00'), 'Container', 1, 1, 0, 0, 0, 'ButtonContainer',
    'ibs.cust.ButtonContainer_01', c_languageId, 'TN_ButtonContainer_01');
-- TVersion
p_Type$newLang (stringToInt ('0x01014E00'), 'BusinessObject', 0, 1, 0, 0, 0, 'TVersion',
    'ibs.cust.TVersion_01', c_languageId, 'TN_TVersion_01');
-- TVersionContainer
p_Type$newLang (stringToInt ('0x01014F00'), 'Container', 1, 1, 0, 0, 0, 'TVersionContainer',
    'ibs.cust.TVersionContainer_01', c_languageId, 'TN_TVersionContainer_01');
-- TabObject
p_Type$newLang (stringToInt ('0x01015000'), 'Tab', 0, 1, 0, 0, 0, 'TabObject',
    'ibs.cust.TabObject_01', c_languageId, 'TN_TabObject_01');
-- TabView
p_Type$newLang (stringToInt ('0x01015100'), 'Tab', 0, 1, 0, 0, 0, 'TabView',
    'ibs.cust.TabView_01', c_languageId, 'TN_TabView_01');
*/

-- user management:
-- MembershipContainer
p_Type$newLang (stringToInt ('0x01015200'), 'Container', 1, 1, 0, 0, 0, 'MembershipContainer',
    'ibs.obj.user.MemberShip_01', c_languageId, 'TN_MemberShip_01');

-- root:
-- Root
p_Type$newLang (stringToInt ('0x01015300'), 'Container', 1, 0, 0, 1, 0, 'Root',
    'ibs.obj.dom.Root_01', c_languageId, 'TN_Root_01');

/* currently not available
-- customizing/types:
-- TypeReference
p_Type$newLang (stringToInt ('0x01015400'), 'Referenz', 0, 1, 0, 0, 0, 'TypeReference',
    'ibs.cust.TypeReference_01', c_languageId, 'TN_TypeReference_01');
-- TypeReferenceContainer
p_Type$newLang (stringToInt ('0x01015500'), 'ReferenzContainer', 1, 1, 0, 0, 0, 'TypeReferenceContainer',
    'ibs.cust.TypeReferenceContainer_01', c_languageId,);
    'TN_TypeReferenceContainer_01'
*/

-- distribution:
-- ReceivedObject
p_Type$newLang (stringToInt ('0x01015600'), 'BusinessObject', 0, 1, 0, 0, 0, 'ReceivedObject',
    'ibs.obj.wsp.ReceivedObject_01', c_languageId, 'TN_ReceivedObject_01');

-- base:
-- CleanContainer
p_Type$newLang (stringToInt ('0x01015700'), 'Container', 1, 1, 0, 0, 0, 'CleanContainer',
    'ibs.obj.wsp.CleanContainer_01', c_languageId, 'TN_CleanContainer_01');

-- Procotol:
-- LogContainer
p_Type$newLang (stringToInt ('0x01015900'), 'Container', 1, 1, 0, 0, 0, 'LogContainer',
    'ibs.obj.log.LogContainer_01', c_languageId, 'TN_LogContainer_01');

-- Document Management:
-- File
p_Type$newLang (stringToInt ('0x01016800'), 'Attachment', 0, 1, 1, 0, 1, 'File',
    'ibs.obj.doc.File_01', c_languageId, 'TN_File_01');

-- Url
p_Type$newLang (stringToInt ('0x01016900'), 'Attachment', 0, 1, 1, 0, 1, 'Url',
    'ibs.obj.doc.Url_01', c_languageId, 'TN_Url_01');

-- user:
-- PersonSearchContainer
p_Type$newLang (stringToInt ('0x01016E00'), 'Container', 1, 1, 0, 0, 0, 'PersonSearchContainer',
    'ibs.obj.user.PersonSearchContainer_01', c_languageId,
    'TN_PersonSearchContainer_01');

-- layout:
-- LayoutContainer
p_Type$newLang (stringToInt ('0x01016F00'), 'Container', 1, 1, 0, 1, 0, 'LayoutContainer',
    'ibs.obj.layout.LayoutContainer_01', c_languageId, 'TN_LayoutContainer_01');

-- Layout
p_Type$newLang (stringToInt ('0x01017000'), 'BusinessObject', 0, 1, 0, 0, 0, 'Layout',
    'ibs.obj.layout.Layout_01', c_languageId, 'TN_Layout_01');

-- Data Interchange:
/*
-- Integrator ---> BB: is not used anymore!!!
p_Type$newLang (stringToInt ('0x01017300'), 'BusinessObject', 0, 1, 0, 0, 0, 'Integrator',
    'ibs.di.Integrator_01', c_languageId, 'TN_Integrator_01');
*/
-- ObjectSearchContainer
p_Type$newLang (stringToInt ('0x01017310'), 'Container', 1, 1, 0, 0, 0, 'ObjectSearchContainer',
    'ibs.obj.search.ObjectSearchContainer_01', c_languageId,
    'TN_ObjectSearchContainer_01');
-- IntegratorContainer
p_Type$newLang (stringToInt ('0x01017400'), 'Container', 1, 1, 0, 1, 0, 'IntegratorContainer',
    'ibs.di.IntegratorContainer_01', c_languageId, 'TN_IntegratorContainer_01');
-- ImportScript
p_Type$newLang (stringToInt ('0x01017410'), 'File', 0, 1, 0, 0, 0, 'ImportScript',
    'ibs.di.imp.ImportScript_01', c_languageId, 'TN_ImportScript_01');
-- ImportScriptContainer
p_Type$newLang (stringToInt ('0x01017420'), 'Container', 1, 1, 0, 1, 0, 'ImportScriptContainer',
    'ibs.di.imp.ImportScriptContainer_01', c_languageId,
    'TN_ImportScriptContainer_01');
-- Connector
p_Type$newLang (stringToInt ('0x01017430'), 'BusinessObject', 0, 1, 0, 0, 0, 'Connector',
    'ibs.di.connect.Connector_01', c_languageId, 'TN_Connector_01');
-- ConnectorContainer
p_Type$newLang (stringToInt ('0x01017440'), 'Container', 1, 1, 0, 1, 0, 'ConnectorContainer',
    'ibs.di.connect.ConnectorContainer_01', c_languageId, 'TN_ConnectorContainer_01');
-- Translator
p_Type$newLang (stringToInt ('0x01017450'), 'File', 0, 1, 0, 0, 0, 'Translator',
    'ibs.di.trans.Translator_01', c_languageId, 'TN_Translator_01');
-- ASCIITranslator_01
p_Type$newLang (stringToInt ('0x01017380'), 'Translator', 0, 1, 0, 0, 0, 'ASCIITranslator',
    'ibs.di.trans.ASCIITranslator_01', c_languageId, 'TN_ASCIITranslator_01');
-- TranslatorContainer
p_Type$newLang (stringToInt ('0x01017460'), 'Container', 1, 1, 0, 1, 0, 'TranslatorContainer',
    'ibs.di.trans.TranslatorContainer_01', c_languageId, 'TN_TranslatorContainer_01');
-- FileConnector
p_Type$newLang (stringToInt ('0x01017470'), 'Connector', 0, 1, 0, 0, 0, 'FileConnector',
    'ibs.di.connect.FileConnector_01', c_languageId, 'TN_FileConnector_01');
-- FTPConnector
p_Type$newLang (stringToInt ('0x01017480'), 'Connector', 0, 1, 0, 0, 0, 'FTPConnector',
    'ibs.di.connect.FTPConnector_01', c_languageId, 'TN_FTPConnector_01');
-- MailConnector
p_Type$newLang (stringToInt ('0x01017490'), 'Connector', 0, 1, 0, 0, 0, 'MailConnector',
    'ibs.di.connect.MailConnector_01', c_languageId, 'TN_MailConnector_01');
-- HTTPConnector
p_Type$newLang (stringToInt ('0x010174A0'), 'Connector', 0, 1, 0, 0, 0, 'HTTPConnector',
    'ibs.di.connect.HTTPConnector_01', c_languageId, 'TN_HTTPConnector_01');

-- EDISwitchConnector
p_Type$newLang (stringToInt ('0x010174B0'), 'Connector', 0, 1, 0, 0, 0, 'EDISwitchConnector',
    'ibs.di.connect.EDISwitchConnector_01', c_languageId, 'TN_EDISwitchConnector_01');

-- HTTPScriptConnector
p_Type$newLang (stringToInt ('0x010174C0'), 'Connector', 0, 1, 0, 0, 0, 'HTTPScriptConnector',
    'ibs.di.connect.HTTPScriptConnector_01', c_languageId, 'TN_HTTPScriptConnector_01');

-- SAPBCXMLRFCConnector
p_Type$newLang (stringToInt ('0x010174D0'), 'Connector', 0, 1, 0, 0, 0, 'SAPBCXMLRFCConnector',
    'ibs.di.connect.SAPBCXMLRFCConnector_01', c_languageId, 'TN_SAPBCXMLRFCConnector_01');

-- HTTPMultipartConnector
p_Type$newLang (stringToInt ('0x010174E0'), 'Connector', 0, 1, 0, 0, 0, 'HTTPMultipartConnector',
    'ibs.di.connect.HTTPMultipartConnector_01', c_languageId, 'TN_HTTPMultipartConnector_01');

-- XMLViewer
p_Type$newLang (stringToInt ('0x01017500'), 'BusinessObject', 0, 1, 1, 0, 1, 'XMLViewer',
    'ibs.di.XMLViewer_01', c_languageId, 'TN_XMLViewer_01');

-- Data Interchange
-- ImportContainer
p_Type$newLang (stringToInt ('0x01017900'), 'Container', 1, 1, 0, 1, 0, 'ImportContainer',
    'ibs.di.imp.ImportContainer_01', c_languageId, 'TN_ImportContainer_01');

-- ExportContainer
p_Type$newLang (stringToInt ('0x01017A00'), 'Container', 1, 1, 0, 1, 0, 'ExportContainer',
    'ibs.di.exp.ExportContainer_01', c_languageId, 'TN_ExportContainer_01');

-- DocumentTemplate
p_Type$newLang (stringToInt ('0x01017C00'), 'File', 0, 1, 0, 0, 0, 'DocumentTemplate',
    'ibs.di.DocumentTemplate_01', c_languageId, 'TN_DocumentTemplate_01');

-- DocumentTemplateContainer
p_Type$newLang (stringToInt ('0x01017D00'), 'Container', 1, 1, 0, 1, 0, 'DocumentTemplateContainer',
    'ibs.di.DocumentTemplateContainer_01', c_languageId,
    'TN_DocumentTemplateContainer_01');

-- XMLViewerContainer_01
p_Type$newLang (stringToInt ('0x01017E00'), 'Container', 1, 1, 0, 1, 0, 'XMLViewerContainer',
    'ibs.di.XMLViewerContainer_01', c_languageId, 'TN_XMLViewerContainer_01');

-- Help
-- HelpContainer_01
p_Type$newLang (stringToInt ('0x01017F00'), 'Container', 1, 1, 1, 1, 0, 'HelpContainer',
    'ibs.obj.help.HelpContainer_01', c_languageId, 'TN_HelpContainer_01');
-- Help_01
p_Type$newLang (stringToInt ('0x01017F10'), 'BusinessObject', 0, 1, 1, 0, 0, 'Help',
    'ibs.obj.help.Help_01', c_languageId, 'TN_Help_01');

-- Search
-- SimpleSearchContainer_01
p_Type$newLang (stringToInt ('0x01017F30'), 'Container', 1, 1, 0, 1, 0, 'SimpleSearchContainer',
    'ibs.obj.search.SimpleSearchContainer_01', c_languageId,
    'TN_SimpleSearchContainer_01');

-- QueryCreator_01
p_Type$newLang (stringToInt ('0x01017F20'), 'BusinessObject', 0, 1, 0, 1, 0, 'QueryCreator',
    'ibs.obj.query.QueryCreator_01', c_languageId,
    'TN_QueryCreator_01');

-- QueryCreatorContainer_01
p_Type$newLang (stringToInt ('0x01017F40'), 'Container', 1, 1, 0, 1, 0, 'QueryCreatorContainer',
    'ibs.obj.query.QueryCreatorContainer_01', c_languageId,
    'TN_QueryCreatorContainer_01');

-- QueryExecutive_01
-- this type does not inherit from container, because it doesn't physicaly
-- contents m2-objects, but it's java-class extends container.
p_Type$newLang (stringToInt ('0x01017F50'), 'BusinessObject', 0, 0, 0, 1, 0, 'QueryExecutive',
    'ibs.obj.query.QueryExecutive_01', c_languageId,
    'TN_QueryExecutive_01');


-- Workflow Management:
-- Workflow
p_Type$newLang (stringToInt ('0x01014800'), 'BusinessObject', 0, 1, 0, 0, 0, 'Workflow',
    'ibs.obj.workflow.Workflow_01', c_languageId, 'TN_Workflow_01');

-- WorkflowTemplate
p_Type$newLang (stringToInt ('0x01014C00'), 'Container', 0, 1, 0, 0, 0, 'WorkflowTemplate',
    'ibs.obj.workflow.WorkflowTemplate_01', c_languageId, 'TN_WorkflowTemplate_01');

-- WorkflowTemplateContainer
p_Type$newLang (stringToInt ('0x01014D00'), 'Container', 1, 1, 0, 1, 0, 'WorkflowTemplateContainer',
    'ibs.obj.workflow.WorkflowTemplateContainer_01', c_languageId,
    'TN_WorkflowTemplateContainer_01');

-- ServicePoint_01
-- inherits from XMLViewerContainer_01
p_Type$newLang (stringToInt ('0x01010190'), 'XMLViewerContainer', 1, 1, 0, 0, 0, 'ServicePoint',
    'ibs.service.servicepoint.ServicePoint_01', c_languageId,
    'TN_ServicePoint_01');

-- WasteBasket_01
-- inherits from Container_01
p_Type$newLang (stringToInt ('0x010101A0'), 'Container', 1, 0, 0, 1, 0, 'WasteBasket',
    'ibs.obj.wsp.WasteBasket_01', c_languageId,
    'TN_WasteBasket_01');

-- documents:
-- Note
p_Type$newLang (stringToInt ('0x01016B00'), 'BusinessObject', 0, 1, 1, 0, 0, 'Note',
    'ibs.obj.doc.Note_01', c_languageId, 'TN_Note_01');

-------------------------------------------------------------------------------
-- The following types do not have predefined type ids.
-- This is necessary due to the fact that type ids for other object types can
-- be set dynamically and to avoid that different types have the same id.

-- menutabs:
-- MenuTabContainer
p_Type$newLang (0, 'Container', 1, 1, 0, 1, 0, 'MenuTabContainer',
    'ibs.obj.menu.MenuTabContainer_01', c_languageId, 'TN_MenuTabContainer_01');

-- MenuTab
p_Type$newLang (0, 'BusinessObject', 0, 1, 0, 0, 0, 'MenuTab',
    'ibs.obj.menu.MenuTab_01', c_languageId, 'TN_MenuTab_01');

-- QuerySelectContainer
p_Type$newLang (0, 'Container', 1, 1, 0, 0, 0, 'QuerySelectContainer',
    'ibs.obj.menu.QuerySelectContainer_01', 0,
    'TN_QuerySelectContainer_01');

-- StateContainer_01
p_Type$newLang (0, 'Container', 1, 1, 0, 1, 0, 'StateContainer',
    'ibs.bo.StateContainer_01', c_languageId,
    'TN_StateContainer_01');
-- EDITranslator_01
p_Type$newLang (0, 'Translator', 0, 1, 0, 0, 0, 'EDITranslator',
    'ibs.di.edi.EDITranslator_01', c_languageId,
    'TN_EDITranslator_01');

-- DBQueryCreator_01
p_Type$newLang (0, 'QueryCreator', 0, 1, 0, 1, 0, 'DBQueryCreator',
    'ibs.obj.query.DBQueryCreator_01', c_languageId, 'TN_DBQueryCreator_01');

-- WorkspaceTemplateContainer
p_Type$newLang (0, 'Container', 1, 1, 0, 1, 0, 'WorkspaceTemplateContainer',
    'ibs.bo.Container', c_languageId, 'TN_WorkspaceTemplateContainer_01');

-- WorkspaceTemplate
p_Type$newLang (0, 'File', 0, 1, 0, 1, 0, 'WorkspaceTemplate',
    'ibs.obj.user.WorkspaceTemplate_01', c_languageId, 'TN_WorkspaceTemplate_01');

-- SAPBCConnector
p_Type$newLang (0, 'Connector', 0, 1, 0, 0, 0, 'SAPBCConnector',
    'ibs.di.connect.SAPBCConnector_01', c_languageId, 'TN_SAPBCConnector_01');
END;
/


-- register all predefined tabs:
--    l_retValue := p_Tab$new (domainId, code, kind, tVersionId, fct, priority,
--             multilangKey, rights, l_tabId);
DECLARE
    -- constants:
    c_ALL_RIGHT             INTEGER := 1;       -- everything was o.k.
    c_languageId            INTEGER := 0;       -- the current language
    c_OP_READ               INTEGER := 4;       -- operation for reading
    c_TK_VIEW               INTEGER := 1;       --
    c_TK_OBJECT             INTEGER := 2;       --
    c_TK_LINK               INTEGER := 3;       --
    c_TK_FUNCTION           INTEGER := 4;       --

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of a function
    l_tabId                 INTEGER := 0;       -- id of actual tab


-- body:
BEGIN
    l_retValue := p_Tab$new (0, 'Content', c_TK_VIEW, 0, 41, 10000, 'OD_tabContent', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'ContentFrameset', c_TK_VIEW, 0, 41, 9900, 'OD_tabContentFrameset', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Info', c_TK_VIEW, 0, 56, 9000, 'OD_tabInfo', 4, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'AddressValues', c_TK_OBJECT, stringToInt ('0x01012F11'), 51, 0, 'OD_tabAddressValues', 4, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Attachments', c_TK_OBJECT, stringToInt ('0x01010061'), 51, 0, 'OD_tabAttachments', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Attributes', c_TK_OBJECT, stringToInt ('0x01012601'), 51, 0, 'OD_tabAttributes', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Buttons', c_TK_OBJECT, stringToInt ('0x01014A01'), 51, 0, 'OD_tabButtons', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Containership', c_TK_OBJECT, stringToInt ('0x01015501'), 51, 0, 'OD_tabContainership', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Filter', c_TK_VIEW, 0, 93, 0, 'OD_tabFilter', 4, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Groups', c_TK_OBJECT, stringToInt ('0x01013701'), 51, 0, 'OD_tabGroups', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Membership', c_TK_OBJECT, stringToInt ('0x01015201'), 51, 0, 'OD_tabMembership', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Methods', c_TK_OBJECT, stringToInt ('0x01012801'), 51, 0, 'OD_tabMethods', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Parameters', c_TK_OBJECT, stringToInt ('0x01013101'), 51, 0, 'OD_tabParameters', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Private', c_TK_FUNCTION, 0, 271, 0, 'OD_tabPrivate', 4, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Recipients', c_TK_OBJECT, stringToInt ('0x01011B01'), 51, 0, 'OD_tabRecipients', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'References', c_TK_OBJECT, stringToInt ('0x01010041'), 51, 0, 'OD_tabReferences', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Tabs', c_TK_OBJECT, stringToInt ('0x01013A01'), 51, 0, 'OD_tabTabs', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Templates', c_TK_OBJECT, stringToInt ('0x01017D11'), 51, 0, 'OD_tabTemplates', 1048576, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Rights', c_TK_VIEW, stringToInt ('0x010100E1'), 51, -9000, 'OD_tabRights', 256, 'ibs.obj.user.RightsContainer_01', l_tabId);
    l_retValue := p_Tab$new (0, 'Protocol', c_TK_OBJECT, stringToInt ('0x01015901'), 51, -10000, 'OD_tabProtocol', 16777216, 'ibs.obj.log.LogView_01', l_tabId);
END;
/

BEGIN
-- set the tabs for the object types:
-- BusinessObject
p_Type$addTabs ('BusinessObject', NULL
    , 'Info', 'References', 'Rights', 'Protocol', '', '', '', '', '', '');
-- Container
p_Type$addTabs ('Container', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- references:
-- Referenz
p_Type$addTabs ('Referenz', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- ReferenzContainer
p_Type$addTabs ('ReferenzContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- menu:
-- Menu
p_Type$addTabs ('Menu', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- search:
-- SearchContainer
p_Type$addTabs ('SearchContainer', NULL
    , 'Content', '', '', '', '', '', '', '', '', '');
-- user management:
-- User
p_Type$addTabs ('User', NULL
    , 'Info', 'References', 'Rights', 'Membership', 'Private', '', '', '', '', '');

-- Group
p_Type$addTabs ('Group', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', '', '', '', '', '');

-- rights management:
-- Rights
p_Type$addTabs ('Rights', NULL
    , 'Info', '', '', '', '', '', '', '', '', '');

-- RightsContainer
p_Type$addTabs ('RightsContainer', NULL
    , 'Info', 'Content', '', '', '', '', '', '', '', '');

-- domains:
-- Domain
p_Type$addTabs ('Domain', NULL
    , 'Info', 'Content', 'Rights' , '', '', '', '', '', '', '');

-- news:
-- NewsContainer
p_Type$addTabs ('NewsContainer', NULL
    , 'Info', 'Content', '', '', '', '', '', '', '', '');

-- distribution:
-- Recipient
p_Type$addTabs ('Recipient', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- SentObjectContainer
p_Type$addTabs ('SentObjectContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- SentObject
p_Type$addTabs ('SentObject', NULL
    , 'Info', 'References', 'Rights', 'Recipients' , '', '', '', '', '', '');

/* currently not available
-- customizing/types:
-- Type
p_Type$addTabs ('Type', NULL
    , 'Info', 'References', 'Rights', 'Containership', 'Versions', '', '', '', '', '');

-- Method
p_Type$addTabs ('Method', NULL
    , 'Info', 'Rights', 'Parameters', '', '', '', '', '', '', '');

*/

-- distribution:
-- Inbox
p_Type$addTabs ('Inbox', NULL
    , 'Info', 'Content', '', '', '', '', '', '', '', '');

-- user management:
-- Workspace
p_Type$addTabs ('Workspace', NULL
    ,'Content', 'References', 'Rights', '', '', '', '', '', '', '');

-- UserContainer
p_Type$addTabs ('UserContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');
    -- tab 'Groups' currently not available

-- GroupContainer
p_Type$addTabs ('GroupContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- UserAdminContainer
p_Type$addTabs ('UserAdminContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- UserProfile
p_Type$addTabs ('UserProfile', NULL
    , 'Info', 'Rights', 'Protocol', 'AddressValues', '', '', '', '', '', '');

/* currently not available
-- customizing/types:
-- Tab
p_Type$addTabs ('Tab', NULL
    , 'Info', 'Buttons', 'Rights', '', '', '', '', '', '', '');

-- TVersion
p_Type$addTabs ('TVersion', NULL
    , 'Info', 'Rights', 'Attributes', 'Methods', 'Tabs', '', '', '', '', '');

-- TabView
p_Type$addTabs ('TabView', NULL
    , 'Info', 'Buttons', 'Rights', '', '', '', '', '', '', '');
*/

-- Root
p_Type$addTabs ('Root', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- distribution:
-- ReceivedObject
p_Type$addTabs ('ReceivedObject', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- Procotol:
-- LogContainer
p_Type$addTabs ('LogContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Filter', '', '', '', '', '');

-- Document Management:
-- File
p_Type$addTabs ('File', NULL
    , 'Info', 'References', 'Rights', 'Protocol', '', '', '', '', '', '');

-- Url
p_Type$addTabs ('Url', NULL
    , 'Info', 'References', 'Rights', 'Protocol', '', '', '', '', '', '');

-- user:
-- PersonSearchContainer
 p_Type$addTabs ('PersonSearchContainer', NULL
    ,'', '', '', '', '', '', '', '', '', '');

-- layout:
-- LayoutContainer
p_Type$addTabs ('LayoutContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- Layout
p_Type$addTabs ('Layout', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- menu tabs:
-- MenuTabContainer
p_Type$addTabs ('MenuTabContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- MenuTab
p_Type$addTabs ('MenuTab', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- QuerySelectContainer
p_Type$addTabs ('QuerySelectContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');


-- Data Interchange
-- ImportScript
p_Type$addTabs ('ImportScript', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- Connector
p_Type$addTabs ('Connector', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');
-- Translator
p_Type$addTabs ('Translator', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');
-- EDITranslator
p_Type$addTabs ('EDITranslator', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');
-- XMLViewer
p_Type$addTabs ('XMLViewer', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');
-- SAPBCXMLRFCConnector
p_Type$addTabs ('SAPBCXMLRFCConnector', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- ImportContainer
p_Type$addTabs ('ImportContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', '', '', '', '', '');

-- ExportContainer
p_Type$addTabs ('ExportContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- DocumentTemplate
p_Type$addTabs ('DocumentTemplate', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- DocumentTemplateContainer
p_Type$addTabs ('DocumentTemplateContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- XMLViewerContainer_01
p_Type$addTabs ('XMLViewerContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- Help_01
p_Type$addTabs ('Help', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- Search
-- SimpleSearchContainer_01
p_Type$addTabs ('SimpleSearchContainer', NULL
    ,'', '', '', '', '', '', '', '', '', '');

-- QueryExecutive_01
-- this type does not inherit from container, because it doesn't physicaly
-- contents m2-objects, but it's java-class extends container.
p_Type$addTabs ('QueryExecutive', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');


-- Workflow Management:
-- Workflow
p_Type$addTabs ('Workflow', NULL
    , 'Info', '', '', '', '', '', '', '', '', '');

-- WorkflowTemplate
p_Type$addTabs ('WorkflowTemplate', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- WorkflowTemplateContainer
p_Type$addTabs ('WorkflowTemplateContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- ServicePoint_01
-- inherits from XMLViewerContainer_01
p_Type$addTabs ('ServicePoint', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- StateContainer_01
p_Type$addTabs ('StateContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- documents:
-- Note
p_Type$addTabs ('Note', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

END;
/

-- set default tabs for all types which don't have default tabs:
BEGIN
    UPDATE  ibs_TVersion
    SET     defaultTab =
            (   SELECT DECODE (MIN (c2.cId), NULL, 0, MIN (c2.cId))
                FROM    (SELECT MAX (priority) AS cPriority,
                                tVersionId AS cTVersionId
                        FROM    ibs_ConsistsOf
                        GROUP BY tVersionId) c1,
                        (SELECT id AS cId, priority AS cPriority,
                                tVersionId AS cTVersionId
                        FROM    ibs_ConsistsOf) c2
                WHERE   c1.cTVersionId = id
                    AND c2.cTVersionId = id
                    AND c2.cPriority = c1.cPriority
            )
    WHERE   defaultTab = 0;
EXCEPTION
    WHEN OTHERS THEN                    -- any error
        -- display the error:
        debug ('cBOT: set default tab error' ||
            ': errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM
        );
END;
/

COMMIT WORK;
/

EXIT;
