/******************************************************************************
 * Create all business object types within the framework. <BR>
 *
 * @version     $Id: createBaseObjectTypes.sql,v 1.74 2010/03/23 12:44:47 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */

-- EXEC p_Type$newLang id, superTypeCode, isContainer, isInheritable,
--      isSearchable, showInMenu, showInNews, code, className, languageId,
--      typeNameName
-- ex.:
-- EXEC p_Type$newLang 0x01010050, N'BusinessObject', 0, 1, 1, 0, 1, N'Attachment',
--    N'ibs.obj.doc.Attachment_01', @c_languageId, N'TN_Attachment_01'

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_languageId           INT             -- the current language

    -- local variables:

-- initializations:
SELECT
    @c_languageId           = 0


-- body:
BEGIN

-- BusinessObject
EXEC p_Type$newLang 0x01010010, N'', 0, 1, 0, 0, 0, N'BusinessObject',
    N'ibs.bo.BusinessObject', @c_languageId, N'TN_BusinessObject'
-- Container
EXEC p_Type$newLang 0x01010020, N'BusinessObject', 1, 1, 1, 1, 0, N'Container',
    N'ibs.bo.Container', @c_languageId, N'TN_Container'

-- references:
-- Referenz
EXEC p_Type$newLang 0x01010030, N'BusinessObject', 0, 0, 0, 0, 0, N'Referenz',
    N'ibs.obj.ref.Referenz_01', @c_languageId, N'TN_Referenz_01'

-- ReferenzContainer
EXEC p_Type$newLang 0x01010040, N'Container', 1, 0, 0, 0, 0, N'ReferenzContainer',
    N'ibs.obj.ref.ReferenzContainer_01', @c_languageId, N'TN_ReferenzContainer_01'

-- attachments:
-- Attachment
EXEC p_Type$newLang 0x01010050, N'BusinessObject', 0, 1, 1, 0, 1, N'Attachment',
    N'ibs.obj.doc.Attachment_01', @c_languageId, N'TN_Attachment_01'
-- AttachmentContainer
EXEC p_Type$newLang 0x01010060, N'Container', 1, 1, 0, 0, 0, N'AttachmentContainer',
    N'ibs.obj.doc.AttachmentContainer_01', @c_languageId, N'TN_AttachmentContainer_01'

-- menu:
-- Menu
EXEC p_Type$newLang 0x01010070, N'Container', 1, 1, 0, 1, 0, N'Menu',
    N'ibs.obj.menu.Menu_01', @c_languageId, N'TN_Menu_01'

/* currently not available
-- MenuElement
EXEC p_Type$newLang 0x01010080, N'BusinessObject', 0, 1, 0, 1, 0, N'MenuElement',
    N'ibs.obj.menu.MenuElement_01', @c_languageId, N'TN_MenuElement_01'
*/

-- search:
-- SearchContainer
EXEC p_Type$newLang 0x01010090, N'Container', 1, 1, 0, 1, 0, N'SearchContainer',
    N'ibs.obj.search.SearchContainer_01', @c_languageId, N'TN_SearchContainer_01'
-- user management:
-- User
EXEC p_Type$newLang 0x010100A0, N'BusinessObject', 0, 1, 1, 0, 0, N'User',
    N'ibs.obj.user.User_01', @c_languageId, N'TN_User_01'

-- Group
EXEC p_Type$newLang 0x010100B0, N'BusinessObject', 0, 1, 1, 0, 0, N'Group',
    N'ibs.obj.user.Group_01', @c_languageId, N'TN_Group_01'

-- rights management:
-- Rights
EXEC p_Type$newLang 0x010100D0, N'BusinessObject', 0, 0, 0, 0, 0, N'Rights',
    N'ibs.obj.user.Rights_01', @c_languageId, N'TN_Rights_01'

-- RightsContainer
EXEC p_Type$newLang 0x010100E0, N'Container', 1, 0, 0, 0, 0, N'RightsContainer',
    N'ibs.obj.user.RightsContainer_01', @c_languageId, N'TN_RightsContainer_01'

-- domains:
-- Domain
EXEC p_Type$newLang 0x010100F0, N'Container', 1, 0, 0, 0, 0, N'Domain',
    N'ibs.obj.dom.Domain_01', @c_languageId, N'TN_Domain_01'

-- DomainSchemeContainer
EXEC p_Type$newLang 0x01010110, N'Container', 1, 1, 0, 1, 0, N'DomainSchemeContainer',
    N'ibs.obj.dom.DomainSchemeContainer_01', @c_languageId,
    N'TN_DomainSchemeContainer_01'
-- DomainScheme
EXEC p_Type$newLang 0x01010120, N'BusinessObject', 0, 1, 0, 0, 0, N'DomainScheme',
    N'ibs.obj.dom.DomainScheme_01', @c_languageId, N'TN_DomainScheme_01'

-- news:
-- NewsContainer
EXEC p_Type$newLang 0x01010800, N'Container', 1, 1, 0, 1, 0, N'NewsContainer',
    N'ibs.obj.wsp.NewsContainer_01', @c_languageId, N'TN_NewsContainer_01'

-- distribution:
-- RecipientContainer
EXEC p_Type$newLang 0x01011B00, N'Container', 1, 1, 0, 0, 0, N'RecipientContainer',
    N'ibs.obj.wsp.RecipientContainer_01', @c_languageId, N'TN_RecipientContainer_01'
-- Recipient
EXEC p_Type$newLang 0x01011C00, N'BusinessObject', 0, 1, 0, 0, 0, N'Recipient',
    N'ibs.obj.wsp.Recipient_01', @c_languageId, N'TN_Recipient_01'

-- SentObjectContainer
EXEC p_Type$newLang 0x01011D00, N'Container', 1, 1, 0, 1, 0, N'SentObjectContainer',
    N'ibs.obj.wsp.SentObjectContainer_01', @c_languageId, N'TN_SentObjectContainer_01'

-- SentObject
EXEC p_Type$newLang 0x01011E00, N'BusinessObject', 0, 1, 0, 0, 0, N'SentObject',
    N'ibs.obj.wsp.SentObject_01', @c_languageId, N'TN_SentObject_01'

/* currently not available
-- customizing/types:
-- Type
EXEC p_Type$newLang 0x01012300, N'BusinessObject', 0, 1, 0, 0, 0, N'Type',
    N'ibs.cust.Type_01', @c_languageId, N'TN_Type_01'

-- TypeContainer
EXEC p_Type$newLang 0x01012400, N'Container', 1, 1, 0, 1, 0, N'TypeContainer',
    N'ibs.cust.TypeContainer_01', @c_languageId, N'TN_TypeContainer_01'
-- Method
EXEC p_Type$newLang 0x01012700, N'BusinessObject', 0, 1, 0, 0, 0, N'Method',
    N'ibs.cust.Method_01', @c_languageId, N'TN_Method_01'

-- MethodContainer
EXEC p_Type$newLang 0x01012800, N'Container', 1, 1, 0, 0, 0, N'MethodContainer',
    N'ibs.cust.MethodContainer_01', @c_languageId, N'TN_MethodContainer_01'
*/

-- distribution:
-- Inbox
EXEC p_Type$newLang 0x01012D00, N'Container', 1, 1, 0, 1, 0, N'Inbox',
    N'ibs.obj.wsp.Inbox_01', @c_languageId, N'TN_Inbox_01'

/* currently not available
-- customizing/types:
-- Parameter
EXEC p_Type$newLang 0x01013000, N'BusinessObject', 0, 1, 0, 0, 0, N'Parameter',
    N'ibs.cust.Parameter_01', @c_languageId, N'TN_Parameter_01'
-- ParameterContainer
EXEC p_Type$newLang 0x01013100, N'Container', 1, 1, 0, 0, 0, N'ParameterContainer',
    N'ibs.cust.ParameterContainer_01', @c_languageId, N'TN_ParameterContainer_01'
*/

-- user management:
-- Workspace
EXEC p_Type$newLang 0x01013200, N'Container', 1, 1, 0, 1, 0, N'Workspace',
    N'ibs.obj.wsp.Workspace_01', @c_languageId, N'TN_Workspace_01'

-- UserContainer
EXEC p_Type$newLang 0x01013300, N'Container', 1, 1, 0, 1, 0, N'UserContainer',
    N'ibs.obj.user.UserContainer_01', @c_languageId, N'TN_UserContainer_01'

-- GroupContainer
EXEC p_Type$newLang 0x01013400, N'Container', 1, 1, 0, 1, 0, N'GroupContainer',
    N'ibs.obj.user.GroupContainer_01', @c_languageId, N'TN_GroupContainer_01'

/*
-- RoleContainer
EXEC p_Type$newLang 0x01013500, N'Container', 1, 1, 0, 0, 0, N'RoleContainer',
    N'ibs.obj.user.RoleContainer_01', @c_languageId, N'TN_RoleContainer_01'
*/
-- UserAdminContainer
EXEC p_Type$newLang 0x01013600, N'Container', 1, 1, 0, 1, 0, N'UserAdminContainer',
    N'ibs.obj.user.UserAdminContainer_01', @c_languageId, N'TN_UserAdminContainer_01'

/* currently not available
-- UserGroupsContainer
EXEC p_Type$newLang 0x01013700, N'Container', 1, 1, 0, 0, 0, N'UserGroupsContainer',
    N'ibs.obj.user.UserGroupsContainer_01', @c_languageId,
    N'TN_UserGroupsContainer_01'
*/

-- UserAddress_01
-- inherits from BusinessObject
EXEC p_Type$newLang 0x01012F10, N'BusinessObject', 0, 1, 0, 0, 0, N'UserAddress',
    N'ibs.obj.user.UserAddress_01', @c_languageId, N'TN_UserAddress_01'

-- UserProfile
EXEC p_Type$newLang 0x01013800, N'BusinessObject', 0, 1, 0, 1, 0, N'UserProfile',
    N'ibs.obj.user.UserProfile_01', @c_languageId, N'TN_UserProfile_01'

/* currently not available
-- customizing/types:
-- Tab
EXEC p_Type$newLang 0x01013900, N'BusinessObject', 0, 1, 0, 0, 0, N'Tab',
    N'ibs.cust.Tab_01', @c_languageId, N'TN_Tab_01'

-- TabContainer
EXEC p_Type$newLang 0x01013A00, N'Container', 1, 1, 0, 0, 0, N'TabContainer',
    N'ibs.cust.TabContainer_01', @c_languageId, N'TN_TabContainer_01'
-- Button
EXEC p_Type$newLang 0x01013B00, N'BusinessObject', 0, 1, 0, 0, 0, N'Button',
    N'ibs.cust.Button_01', @c_languageId, N'TN_Button_01'
-- ButtonContainer
EXEC p_Type$newLang 0x01013C00, N'Container', 1, 1, 0, 0, 0, N'ButtonContainer',
    N'ibs.cust.ButtonContainer_01', @c_languageId, N'TN_ButtonContainer_01'
-- customizing/types:
-- TVersion
EXEC p_Type$newLang 0x01014E00, N'BusinessObject', 0, 1, 0, 0, 0, N'TVersion',
    N'ibs.cust.TVersion_01', @c_languageId, N'TN_TVersion_01'

-- TVersionContainer
EXEC p_Type$newLang 0x01014F00, N'Container', 1, 1, 0, 0, 0, N'TVersionContainer',
    N'ibs.cust.TVersionContainer_01', @c_languageId, N'TN_TVersionContainer_01'
-- TabObject
EXEC p_Type$newLang 0x01015000, N'Tab', 0, 1, 0, 0, 0, N'TabObject',
    N'ibs.cust.TabObject_01', @c_languageId, N'TN_TabObject_01'
-- TabView
EXEC p_Type$newLang 0x01015100, N'Tab', 0, 1, 0, 0, 0, N'TabView',
    N'ibs.cust.TabView_01', @c_languageId, N'TN_TabView_01'
*/

-- user management:
-- MembershipContainer
EXEC p_Type$newLang 0x01015200, N'Container', 1, 1, 0, 0, 0, N'MembershipContainer',
    N'ibs.obj.user.MemberShip_01', @c_languageId, N'TN_MemberShip_01'

-- root:
-- Root
EXEC p_Type$newLang 0x01015300, N'Container', 1, 0, 0, 1, 0, N'Root',
    N'ibs.obj.dom.Root_01', @c_languageId, N'TN_Root_01'

/* currently not available
-- customizing/types:
-- TypeReference
EXEC p_Type$newLang 0x01015400, N'Referenz', , 0, 1, 0, 0, 0, N'TypeReference',
    N'ibs.cust.TypeReference_01', @c_languageId, N'TN_TypeReference_01'
-- TypeReferenceContainer
EXEC p_Type$newLang 0x01015500, N'ReferenzContainer', , 1, 1, 0, 0, 0, N'TypeReferenceContainer',
    N'ibs.cust.TypeReferenceContainer_01', @c_languageId,
    N'TN_TypeReferenceContainer_01'
*/

-- distribution:
-- ReceivedObject
EXEC p_Type$newLang 0x01015600, N'BusinessObject', 0, 1, 0, 0, 0, N'ReceivedObject',
    N'ibs.obj.wsp.ReceivedObject_01', @c_languageId, N'TN_ReceivedObject_01'

-- base:
-- CleanContainer
EXEC p_Type$newLang 0x01015700, N'Container', 1, 1, 0, 0, 0, N'CleanContainer',
    N'ibs.obj.wsp.CleanContainer_01', @c_languageId, N'TN_CleanContainer_01'

-- Procotol:
-- LogContainer
EXEC p_Type$newLang 0x01015900, N'Container', 1, 1, 0, 0, 0, N'LogContainer',
    N'ibs.obj.log.LogContainer_01', @c_languageId, N'TN_LogContainer_01'

-- Document Management:
-- File
EXEC p_Type$newLang 0x01016800, N'Attachment', 0, 1, 1, 0, 1, N'File',
    N'ibs.obj.doc.File_01', @c_languageId, N'TN_File_01'

-- Url
EXEC p_Type$newLang 0x01016900, N'Attachment', 0, 1, 1, 0, 1, N'Url',
    N'ibs.obj.doc.Url_01', @c_languageId, N'TN_Url_01'

-- user:
-- PersonSearchContainer
EXEC p_Type$newLang 0x01016E00, N'Container', 1, 1, 0, 0, 0, N'PersonSearchContainer',
    N'ibs.obj.user.PersonSearchContainer_01', @c_languageId,
    N'TN_PersonSearchContainer_01'

-- layout:
-- LayoutContainer
EXEC p_Type$newLang 0x01016F00, N'Container', 1, 1, 0, 1, 0, N'LayoutContainer',
    N'ibs.obj.layout.LayoutContainer_01', @c_languageId, N'TN_LayoutContainer_01'

-- Layout
EXEC p_Type$newLang 0x01017000, N'BusinessObject', 0, 1, 0, 0, 0, N'Layout',
    N'ibs.obj.layout.Layout_01', @c_languageId, N'TN_Layout_01'

-- Data Interchange:
/*
-- Integrator ---> BB: is not used anymore!!!
EXEC p_Type$newLang 0x01017300, N'BusinessObject', 0, 1, 0, 0, 0, N'Integrator',
    N'ibs.di.Integrator_01', @c_languageId, N'TN_Integrator_01'
*/
-- ObjectSearchContainer
EXEC p_Type$newLang 0x01017310, N'Container', 1, 1, 0, 0, 0, N'ObjectSearchContainer',
    N'ibs.obj.search.ObjectSearchContainer_01', @c_languageId,
    N'TN_ObjectSearchContainer_01'
-- IntegratorContainer
EXEC p_Type$newLang 0x01017400, N'Container', 1, 1, 0, 1, 0, N'IntegratorContainer',
    N'ibs.di.IntegratorContainer_01', @c_languageId, N'TN_IntegratorContainer_01'
-- ImportScript
EXEC p_Type$newLang 0x01017410, N'File', 0, 1, 0, 0, 0, N'ImportScript',
    N'ibs.di.imp.ImportScript_01', @c_languageId, N'TN_ImportScript_01'
-- ImportScriptContainer
EXEC p_Type$newLang 0x01017420, N'Container', 1, 1, 0, 1, 0, N'ImportScriptContainer',
    N'ibs.di.imp.ImportScriptContainer_01', @c_languageId,
    N'TN_ImportScriptContainer_01'
-- Connector
EXEC p_Type$newLang 0x01017430, N'BusinessObject', 0, 1, 0, 0, 0, N'Connector',
    N'ibs.di.connect.Connector_01', @c_languageId, N'TN_Connector_01'
-- ConnectorContainer
EXEC p_Type$newLang 0x01017440, N'Container', 1, 1, 0, 1, 0, N'ConnectorContainer',
    N'ibs.di.connect.ConnectorContainer_01', @c_languageId, N'TN_ConnectorContainer_01'
-- Translator
EXEC p_Type$newLang 0x01017450, N'File', 0, 1, 0, 0, 0, N'Translator',
    N'ibs.di.trans.Translator_01', @c_languageId, N'TN_Translator_01'
-- ASCIITranslator_01
EXEC p_Type$newLang 0x01017380, N'Translator', 0, 1, 0, 0, 0, N'ASCIITranslator',
    N'ibs.di.trans.ASCIITranslator_01', @c_languageId, N'TN_ASCIITranslator_01'
-- TranslatorContainer
EXEC p_Type$newLang 0x01017460, N'Container', 1, 1, 0, 1, 0, N'TranslatorContainer',
    N'ibs.di.trans.TranslatorContainer_01', @c_languageId, N'TN_TranslatorContainer_01'
-- FileConnector
EXEC p_Type$newLang 0x01017470, N'Connector', 0, 1, 0, 0, 0, N'FileConnector',
    N'ibs.di.connect.FileConnector_01', @c_languageId, N'TN_FileConnector_01'
-- FTPConnector
EXEC p_Type$newLang 0x01017480, N'Connector', 0, 1, 0, 0, 0, N'FTPConnector',
    N'ibs.di.connect.FTPConnector_01', @c_languageId, N'TN_FTPConnector_01'
-- MailConnector
EXEC p_Type$newLang 0x01017490, N'Connector', 0, 1, 0, 0, 0, N'MailConnector',
    N'ibs.di.connect.MailConnector_01', @c_languageId, N'TN_MailConnector_01'
-- HTTPConnector
EXEC p_Type$newLang 0x010174A0, N'Connector', 0, 1, 0, 0, 0, N'HTTPConnector',
    N'ibs.di.connect.HTTPConnector_01', @c_languageId, N'TN_HTTPConnector_01'
-- EDISwitchConnector
EXEC p_Type$newLang 0x010174B0, N'Connector', 0, 1, 0, 0, 0, N'EDISwitchConnector',
    N'ibs.di.connect.EDISwitchConnector_01', @c_languageId, N'TN_EDISwitchConnector_01'
-- HTTPScriptConnector
EXEC p_Type$newLang 0x010174C0, N'Connector', 0, 1, 0, 0, 0, N'HTTPScriptConnector',
    N'ibs.di.connect.HTTPScriptConnector_01', @c_languageId, N'TN_HTTPScriptConnector_01'
-- SAPBCXMLRFCConnector
EXEC p_Type$newLang 0x010174D0, N'Connector', 0, 1, 0, 0, 0, N'SAPBCXMLRFCConnector',
    N'ibs.di.connect.SAPBCXMLRFCConnector_01', @c_languageId, N'TN_SAPBCXMLRFCConnector_01'
-- HTTPMultipartConnector
EXEC p_Type$newLang 0x010174E0, N'Connector', 0, 1, 0, 0, 0, N'HTTPMultipartConnector',
    N'ibs.di.connect.HTTPMultipartConnector_01', @c_languageId,
    N'TN_HTTPMultipartConnector_01'

-- XMLViewer
EXEC p_Type$newLang 0x01017500, N'BusinessObject', 0, 1, 1, 0, 1, N'XMLViewer',
    N'ibs.di.XMLViewer_01', @c_languageId, N'TN_XMLViewer_01'

-- ImportContainer
EXEC p_Type$newLang 0x01017900, N'Container', 1, 1, 0, 1, 0, N'ImportContainer',
    N'ibs.di.imp.ImportContainer_01', @c_languageId, N'TN_ImportContainer_01'

-- ExportContainer
EXEC p_Type$newLang 0x01017A00, N'Container', 1, 1, 0, 1, 0, N'ExportContainer',
    N'ibs.di.exp.ExportContainer_01', @c_languageId, N'TN_ExportContainer_01'

-- DocumentTemplate
EXEC p_Type$newLang 0x01017C00, N'File', 0, 1, 0, 0, 0, N'DocumentTemplate',
    N'ibs.di.DocumentTemplate_01', @c_languageId, N'TN_DocumentTemplate_01'

-- DocumentTemplateContainer
EXEC p_Type$newLang 0x01017D00, N'Container', 1, 1, 0, 1, 0, N'DocumentTemplateContainer',
    N'ibs.di.DocumentTemplateContainer_01', @c_languageId,
    N'TN_DocumentTemplateContainer_01'

-- XMLViewerContainer_01
EXEC p_Type$newLang 0x01017E00, N'Container', 1, 1, 0, 1, 0, N'XMLViewerContainer',
    N'ibs.di.XMLViewerContainer_01', @c_languageId, N'TN_XMLViewerContainer_01'

-- Help
-- HelpContainer_01
EXEC p_Type$newLang 0x01017F00, N'Container', 1, 1, 1, 1, 0, N'HelpContainer',
    N'ibs.obj.help.HelpContainer_01', @c_languageId, N'TN_HelpContainer_01'
-- Help_01
EXEC p_Type$newLang 0x01017F10, N'BusinessObject', 0, 1, 1, 0, 0, N'Help',
    N'ibs.obj.help.Help_01', @c_languageId, N'TN_Help_01'

-- Search
-- SimpleSearchContainer_01
EXEC p_Type$newLang 0x01017F30, N'Container', 1, 1, 0, 1, 0, N'SimpleSearchContainer',
    N'ibs.obj.search.SimpleSearchContainer_01', @c_languageId,
    N'TN_SimpleSearchContainer_01'

-- QueryCreator_01
EXEC p_Type$newLang 0x01017F20, N'BusinessObject', 0, 1, 0, 1, 0, N'QueryCreator',
    N'ibs.obj.query.QueryCreator_01', @c_languageId,
    N'TN_QueryCreator_01'

-- QueryCreatorContainer_01
EXEC p_Type$newLang 0x01017F40, N'Container', 1, 1, 0, 1, 0, N'QueryCreatorContainer',
    N'ibs.obj.query.QueryCreatorContainer_01', @c_languageId,
    N'TN_QueryCreatorContainer_01'

-- QueryExecutive_01
-- this type does not inherit from container, because it doesn't physicaly
-- contents m2-objects, but it's java-class extends container.
EXEC p_Type$newLang 0x01017F50, N'BusinessObject', 0, 0, 0, 1, 0, N'QueryExecutive',
    N'ibs.obj.query.QueryExecutive_01', @c_languageId,
    N'TN_QueryExecutive_01'


-- Workflow Management:
-- Workflow
EXEC p_Type$newLang 0x01014800, N'BusinessObject', 0, 1, 0, 0, 0, N'Workflow',
    N'ibs.obj.workflow.Workflow_01', @c_languageId, N'TN_Workflow_01'

-- WorkflowTemplate
EXEC p_Type$newLang 0x01014C00, N'Container', 0, 1, 0, 0, 0, N'WorkflowTemplate',
    N'ibs.obj.workflow.WorkflowTemplate_01', @c_languageId, N'TN_WorkflowTemplate_01'

-- WorkflowTemplateContainer
EXEC p_Type$newLang 0x01014D00, N'Container', 1, 1, 0, 1, 0, N'WorkflowTemplateContainer',
    N'ibs.obj.workflow.WorkflowTemplateContainer_01', @c_languageId,
    N'TN_WorkflowTemplateContainer_01'

-- ServicePoint_01
-- inherits from XMLViewerContainer_01
EXEC p_Type$newLang 0x01010190, N'XMLViewerContainer', 1, 1, 1, 0, 0, N'ServicePoint',
    N'ibs.service.servicepoint.ServicePoint_01', @c_languageId,
    N'TN_ServicePoint_01'

-- WasteBasket_01
-- inherits from Container_01
EXEC p_Type$newLang 0x010101A0, N'Container', 1, 0, 0, 1, 0, N'WasteBasket',
    N'ibs.obj.wsp.WasteBasket_01', 0,
    N'TN_WasteBasket_01'

-- documents:
-- Note
EXEC p_Type$newLang 0x01016B00, N'BusinessObject', 0, 1, 1, 0, 0, N'Note',
    N'ibs.obj.doc.Note_01', @c_languageId, N'TN_Note_01'


-------------------------------------------------------------------------------
-- The following types do not have predefined type ids.
-- This is necessary due to the fact that type ids for other object types can
-- be set dynamically and to avoid that different types have the same id.

-- menutabs:
-- MenuTabContainer
EXEC p_Type$newLang 0, N'Container', 1, 1, 0, 1, 0, N'MenuTabContainer',
    N'ibs.obj.menu.MenuTabContainer_01', @c_languageId, N'TN_MenuTabContainer_01'

-- MenuTab
EXEC p_Type$newLang 0, N'BusinessObject', 0, 1, 0, 0, 0, N'MenuTab',
    N'ibs.obj.menu.MenuTab_01', @c_languageId, N'TN_MenuTab_01'

-- QuerySelectContainer
EXEC p_Type$newLang 0, N'Container', 1, 1, 0, 0, 0, N'QuerySelectContainer',
    N'ibs.obj.menu.QuerySelectContainer_01', @c_languageId,
    N'TN_QuerySelectContainer_01'

-- StateContainer_01
EXEC p_Type$newLang 0, N'Container', 1, 1, 0, 1, 0, N'StateContainer',
    N'ibs.bo.StateContainer_01', @c_languageId, N'TN_StateContainer_01'
-- EDITranslator_01
EXEC p_Type$newLang 0, N'Translator', 0, 1, 0, 0, 0, N'EDITranslator',
    N'ibs.di.edi.EDITranslator_01', @c_languageId, N'TN_EDITranslator_01'

-- DBQueryCreator_01
EXEC p_Type$newLang 0, N'QueryCreator', 0, 1, 0, 1, 0, N'DBQueryCreator',
    N'ibs.obj.query.DBQueryCreator_01', @c_languageId, N'TN_DBQueryCreator_01'

-- WorkspaceTemplateContainer
EXEC p_Type$newLang 0, N'Container', 1, 1, 0, 1, 0, N'WorkspaceTemplateContainer',
    N'ibs.bo.Container', @c_languageId, N'TN_WorkspaceTemplateContainer_01'

-- WorkspaceTemplate
EXEC p_Type$newLang 0, N'File', 0, 1, 0, 1, 0, N'WorkspaceTemplate',
    N'ibs.obj.user.WorkspaceTemplate_01', @c_languageId, N'TN_WorkspaceTemplate_01'

-- SAPBCConnector
EXEC p_Type$newLang 0, N'Connector', 0, 1, 0, 0, 0, N'SAPBCConnector',
    N'ibs.di.connect.SAPBCConnector_01', @c_languageId,
    N'TN_SAPBCConnector_01'

-- LocaleContainer
EXEC p_Type$newLang 0, N'Container', 1, 1, 0, 1, 0, N'LocaleContainer',
    N'ibs.obj.ml.LocaleContainer_01', @c_languageId, N'TN_LocaleContainer_01'

-- Locale
EXEC p_Type$newLang 0, N'BusinessObject', 0, 1, 0, 0, 0, N'Locale',
    N'ibs.obj.ml.Locale_01', @c_languageId, N'TN_Locale_01'
    
END
GO
PRINT 'Types created.'
GO

-- register all predefined tabs:
-- EXEC @l_retValue = p_Tab$new domainId, code, kind, tVersionId, fct, priority,
--             multilangKey, rights, @l_tabId OUTPUT
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_languageId           INT,            -- the current language
    @c_OP_READ              INT,            -- operation for reading
    @c_TK_VIEW              INT,            --
    @c_TK_OBJECT            INT,            --
    @c_TK_LINK              INT,            --
    @c_TK_FUNCTION          INT,            --

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_tabId                INT             -- id of actual tab

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_languageId           = 0,
    @c_OP_READ              = 4,
    @c_TK_VIEW              = 1,
    @c_TK_OBJECT            = 2,
    @c_TK_LINK              = 3,
    @c_TK_FUNCTION          = 4

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_tabId = 0

-- body:
BEGIN
    EXEC @l_retValue = p_Tab$new 0, N'Content', @c_TK_VIEW, 0, 41, 10000, N'OD_tabContent', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'ContentFrameset', @c_TK_VIEW, 0, 41, 9900, N'OD_tabContentFrameset', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Info', @c_TK_VIEW, 0, 56, 9000, N'OD_tabInfo', 4, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'AddressValues', @c_TK_OBJECT, 0x01012F11, 51, 0, N'OD_tabAddressValues', 4, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Attachments', @c_TK_OBJECT, 0x01010061, 51, 0, N'OD_tabAttachments', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Attributes', @c_TK_OBJECT, 0x01012601, 51, 0, N'OD_tabAttributes', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Buttons', @c_TK_OBJECT, 0x01014A01, 51, 0, N'OD_tabButtons', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Contacts', @c_TK_OBJECT, 0x01012B01, 51, 0, N'OD_tabContacts', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Containership', @c_TK_OBJECT, 0x01015501, 51, 0, N'OD_tabContainership', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Filter', @c_TK_VIEW, 0, 93, 0, N'OD_tabFilter', 4, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Groups', @c_TK_OBJECT, 0x01013701, 51, 0, N'OD_tabGroups', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Membership', @c_TK_OBJECT, 0x01015201, 51, 0, N'OD_tabMembership', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Methods', @c_TK_OBJECT, 0x01012801, 51, 0, N'OD_tabMethods', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Parameters', @c_TK_OBJECT, 0x01013101, 51, 0, N'OD_tabParameters', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Private', @c_TK_FUNCTION, 0, 271, 0, N'OD_tabPrivate', 4, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Recipients', @c_TK_OBJECT, 0x01011B01, 51, 0, N'OD_tabRecipients', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'References', @c_TK_OBJECT, 0x01010041, 51, 0, N'OD_tabReferences', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Tabs', @c_TK_OBJECT, 0x01013A01, 51, 0, N'OD_tabTabs', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Templates', @c_TK_OBJECT, 0x01017D11, 51, 0, N'OD_tabTemplates', 1048576, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Rights', @c_TK_VIEW, 0, 253, -9000, N'OD_tabRights', 256, N'ibs.obj.user.RightsContainer_01', @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Protocol', @c_TK_VIEW, 0, 253, -10000, N'OD_tabProtocol', 16777216, N'ibs.obj.log.LogView_01', @l_tabId OUTPUT
END
GO
PRINT 'Tabs created.'
GO

-- set the tabs for the object types:
-- body:
BEGIN
-- BusinessObject
EXEC p_Type$addTabs N'BusinessObject', N''
    , N'Info', N'References', N'Rights', N'Protocol'
-- Container
EXEC p_Type$addTabs N'Container', ''
    , N'Info', N'Content', N'References', N'Rights'

-- references:
-- Referenz
EXEC p_Type$addTabs N'Referenz', N''
    , N'Info', N'Rights'

-- ReferenzContainer
EXEC p_Type$addTabs N'ReferenzContainer', N''
    , N'Info', N'Content', N'Rights'

-- menu:
-- Menu
EXEC p_Type$addTabs N'Menu', N''
    , N'Info', N'Content', N'References', N'Rights'

-- search:
-- SearchContainer
EXEC p_Type$addTabs N'SearchContainer', N''
    , N'Content'
-- user management:
-- User
EXEC p_Type$addTabs N'User', N''
    , N'Info', N'References', N'Rights', N'Membership', N'Private'

-- Group
EXEC p_Type$addTabs N'Group', N''
    , N'Info', N'Content', N'References', N'Rights', N'Protocol'

-- rights management:
-- Rights
EXEC p_Type$addTabs N'Rights', N''
    , N'Info'

-- RightsContainer
EXEC p_Type$addTabs N'RightsContainer', N''
    , N'Info', N'Content'

-- domains:
-- Domain
EXEC p_Type$addTabs N'Domain', N''
    , N'Info', N'Content', N'Rights'

-- news:
-- NewsContainer
EXEC p_Type$addTabs N'NewsContainer', N''
    , N'Info', N'Content', N'Rights'

-- distribution:
-- Recipient
EXEC p_Type$addTabs N'Recipient', N''
    , N'Info', N'Rights'

-- SentObjectContainer
EXEC p_Type$addTabs N'SentObjectContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- SentObject
EXEC p_Type$addTabs N'SentObject', N''
    , N'Info', N'Rights', N'Recipients'

/* currently not available
-- customizing/types:
-- Type
EXEC p_Type$addTabs N'Type', N''
    , N'Info', N'References', N'Rights', N'Containership', N'Versions'

-- Method
EXEC p_Type$addTabs N'Method', N''
    , N'Info', N'Rights', N'Parameters'
*/

-- distribution:
-- Inbox
EXEC p_Type$addTabs N'Inbox', N''
    , N'Info', N'Content', N'Rights'

-- user management:
-- Workspace
EXEC p_Type$addTabs N'Workspace', N''
    , N'Content', N'References', N'Rights'

-- UserContainer
EXEC p_Type$addTabs N'UserContainer', N''
    , N'Info', N'Content', N'Rights' -- , 'Groups' -- currently not available

-- GroupContainer
EXEC p_Type$addTabs N'GroupContainer', N''
    , N'Info', N'Content', N'Rights'

-- UserAdminContainer
EXEC p_Type$addTabs N'UserAdminContainer', N''
    , N'Info', N'Content', N'Rights'

-- UserProfile
EXEC p_Type$addTabs N'UserProfile', N''
    , N'Info', N'Rights', N'Protocol', N'AddressValues'

/* currently not available
-- customizing/types:
-- Tab
EXEC p_Type$addTabs N'Tab', N''
    , N'Info', N'Buttons', N'Rights'

-- TVersion
EXEC p_Type$addTabs N'TVersion', N''
    , N'Info', N'Rights', N'Attributes', N'Methods', N'Tabs'

-- TabView
EXEC p_Type$addTabs N'TabView', N''
    , N'Info', N'Buttons', N'Rights'
*/

-- Root
EXEC p_Type$addTabs N'Root', N''
    , N'Info', N'Content', N'Rights'

-- distribution:
-- ReceivedObject
EXEC p_Type$addTabs N'ReceivedObject', N''
    , N'Info', N'Rights'

-- Procotol:
-- LogContainer
EXEC p_Type$addTabs N'LogContainer', N''
    , N'Info', N'Content', N'References', N'Rights', N'Filter'

-- Document Management:
-- File
EXEC p_Type$addTabs N'File', N''
    , N'Info', N'References', N'Rights', N'Protocol'

-- Url
EXEC p_Type$addTabs N'Url', N''
    , N'Info', N'References', N'Rights', N'Protocol'

-- user:
-- PersonSearchContainer
EXEC p_Type$addTabs N'PersonSearchContainer', N''

-- layout:
-- LayoutContainer
EXEC p_Type$addTabs N'LayoutContainer', N''
    , N'Info', N'Content', N'Rights'

-- Layout
EXEC p_Type$addTabs N'Layout', N''
    , N'Info', N'Rights'

-- locale:
-- LocaleContainer
EXEC p_Type$addTabs N'LocaleContainer', N''
    , N'Info', N'Content', N'Rights'

-- Layout
EXEC p_Type$addTabs N'Locale', N''
    , N'Info', N'Rights'

-- menutabs
-- MenuTabContainer
EXEC p_Type$addTabs N'MenuTabContainer', N''
    , N'Info', N'Content', N'Rights', N'', N'', N'', N'', N'', N'', N''

-- MenuTab
EXEC p_Type$addTabs N'MenuTab', N''
    , N'Info', N'Rights', N'', N'', N'', N'', N'', N'', N'', N''

EXEC p_Type$addTabs N'QuerySelectContainer', N''
    , N'Info', N'Content', N'Rights', N'', N'', N'', N'', N'', N'', N''

-- Data Interchange
-- ImportScript
EXEC p_Type$addTabs N'ImportScript', N''
    , N'Info', N'References', N'Rights'

-- Connector
EXEC p_Type$addTabs N'Connector', N''
    , N'Info', N'References', N'Rights'
-- Translator
EXEC p_Type$addTabs N'Translator', N''
    , N'Info', N'References', N'Rights'
-- EDITranslator
EXEC p_Type$addTabs N'EDITranslator', N''
    , N'Info', N'References', N'Rights'
-- XMLViewer
EXEC p_Type$addTabs N'XMLViewer', N''
    , N'Info', N'References', N'Rights'

-- ImportContainer
EXEC p_Type$addTabs N'ImportContainer', N''
    , N'Info', N'Content', N'References', N'Rights', N'Protocol'

-- ExportContainer
EXEC p_Type$addTabs N'ExportContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- DocumentTemplate
EXEC p_Type$addTabs N'DocumentTemplate', N''
    , N'Info', N'References', N'Rights'

-- DocumentTemplateContainer
EXEC p_Type$addTabs N'DocumentTemplateContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- XMLViewerContainer_01
EXEC p_Type$addTabs N'XMLViewerContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- Help_01
EXEC p_Type$addTabs N'Help', N''
    , N'Info', N'References', N'Rights'

-- Search
-- SimpleSearchContainer_01
EXEC p_Type$addTabs N'SimpleSearchContainer', N''


-- QueryExecutive_01
-- This type does not inherit from container, because it doesn't physically
-- contain m2 objects, but its java class extends container.
EXEC p_Type$addTabs N'QueryExecutive', N''
    , N'Info', N'Content', N'References', N'Rights'


-- Workflow Management:
-- Workflow
EXEC p_Type$addTabs N'Workflow', N''
    , N'Info'

-- WorkflowTemplate
EXEC p_Type$addTabs N'WorkflowTemplate', N''
    , N'Info', N'References', N'Rights'

-- WorkflowTemplateContainer
EXEC p_Type$addTabs N'WorkflowTemplateContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- ServicePoint_01
-- inherits from XMLViewerContainer_01
EXEC p_Type$addTabs N'ServicePoint', N''
    , N'Info', N'Content', N'References', N'Rights'

-- StateContainer_01
EXEC p_Type$addTabs N'StateContainer', N''
    , N'Info', N'Content', N'Rights'

-- documents:
-- Note
EXEC p_Type$addTabs N'Note', N''
    , N'Info', N'References', N'Rights'

END
GO

-- set default tabs for all types which don't have default tabs:
UPDATE  ibs_TVersion
SET     defaultTab =
        (
            SELECT  COALESCE (MIN (cId), 0)
            FROM    (
                        SELECT  tVersionId AS cTVersionId, id AS cId,
                                priority AS cPriority
                        FROM    ibs_ConsistsOf
                    ) c
            WHERE   cPriority =
                    (
                        SELECT  MAX (c2Priority)
                        FROM	(
                                    SELECT  tVersionId AS c2TVersionId,
                                            priority AS c2Priority
                                    FROM    ibs_ConsistsOf
                                ) c2
                        WHERE   c2TVersionId = id
                    )
                AND cTVersionId = id
        )
WHERE   defaultTab = 0
GO
PRINT 'Tabs assigned to types.'
GO


--/////////////////////////////////////////////////////////////////////////////
-- ensure that each tVersion has a correct state
--/////////////////////////////////////////////////////////////////////////////
UPDATE  ibs_TVersion
SET     state = 2
WHERE   state = 4
GO

-- show count messages again:
SET NOCOUNT OFF
GO
