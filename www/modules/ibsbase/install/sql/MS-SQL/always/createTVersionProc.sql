/******************************************************************************
 * Create all entries of procedures in the framework. <BR>
 *
 * @version     $Id: createTVersionProc.sql,v 1.14 2010/03/23 12:44:47 btatzmann Exp $
 *
 * @author      Mario Oberdorfer (MO)  010131
 ******************************************************************************
 */

-- EXEC p_TVersionProc$add tVersionId, procCode, procName
-- ex.:
-- EXEC p_TVersionProc$add 0x01010021, @c_PC_CREATE, 'p_Object$create'
--
-- EXEC p_TVersionProc$new typeCode, procCode, procName
-- ex.:
-- EXEC p_TVersionProc$new 'Container', @c_PC_CREATE, 'p_Object$create'

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_languageId           INT,            -- the current language
    @c_PC_create            NAME,           -- procedure code for create
    @c_PC_retrieve          NAME,           -- procedure code for retrieve
    @c_PC_change            NAME,           -- procedure code for change
    @c_PC_copy              NAME,           -- procedure code for copy
    @c_PC_delete            NAME,           -- procedure code for delete
    @c_PC_deleteRec         NAME,           -- procedure code for recursive delete
    @c_PC_move              NAME,           -- procedure code for move
    @c_PC_changeState       NAME,           -- procedure code for forchangeState
    @c_PC_changeProcessState NAME,          -- procedure code for changeProcessState
    @c_PC_changeOwner       NAME,           -- procedure code for changeOwner
    @c_PC_checkOut          NAME,           -- procedure code for checkOut
    @c_PC_InsertProtocol    NAME,           -- procedure code for InsertProtocol
    @c_PC_checkIn           NAME,           -- procedure code for checkIn
    @c_PC_undelete          NAME,           -- procedure code for undelete
    @c_PC_undeleteRec       NAME,           -- procedure code for recursive undelete
    @c_PC_deleteAllRefs     NAME,           -- procedure code for deleteAllRefs
    @c_PC_getUpper          NAME,           -- procedure code for getUpper
    @c_PC_getTab            NAME,           -- procedure code for getTab
    @c_PC_getMaster         NAME,           -- procedure code for getMaster
    @c_PC_createQty         NAME,           -- procedure code for createQty
    @c_PC_createVal         NAME,            -- procedure code for createVal
    @c_PC_getNotificationData NAME          -- procedure code for getNotificationData

    -- local variables:

-- initializations:
SELECT
    @c_languageId               = 0,
    @c_PC_create                = 'create',
    @c_PC_retrieve              = 'retrieve',
    @c_PC_change                = 'change',
    @c_PC_copy                  = 'copy',
    @c_PC_delete                = 'delete',
    @c_PC_deleteRec             = 'deleteRec',
    @c_PC_move                  = 'move',
    @c_PC_changeState           = 'changeState',
    @c_PC_changeProcessState    = 'changeProcessState',
    @c_PC_changeOwner           = 'changeOwner',
    @c_PC_checkOut              = 'checkOut',
    @c_PC_InsertProtocol        = 'insertProtocol',
    @c_PC_checkIn               = 'checkIn',
    @c_PC_undelete              = 'undelete',
    @c_PC_undeleteRec           = 'undeleteRec',
    @c_PC_deleteAllRefs         = 'deleteAllRefs',
    @c_PC_getUpper              = 'getUpper',
    @c_PC_getTab                = 'getTab',
    @c_PC_getMaster             = 'getMaster',
    @c_PC_createQty             = 'createQty',
    @c_PC_createVal             = 'createVal',
    @c_PC_getNotificationData   = 'getNotificationData'


-- BusinessObject
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_create, N'p_Object$create'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_change, N'p_Object$change'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_move, N'p_Object$move'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_changeState, N'p_Object$changeState'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_changeProcessState, N'p_Object$changeProcessState'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_changeOwner, N'p_Object$changeOwnerRec'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_retrieve, N'p_Object$retrieve'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_checkOut, N'p_Object$checkOut'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_InsertProtocol, N'p_Object$InsertProtocol'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_checkIn, N'p_Object$checkIn'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_delete, N'p_Object$delete'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_deleteRec, N'p_Object$delete'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_undelete, N'p_Object$undelete'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_undeleteRec, N'p_Object$undelete'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_deleteAllRefs, N'p_Object$deleteAllRefs'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_copy, N'p_Object$copy'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_getUpper, N'p_Object$getUpperOid'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_getTab, N'p_Object$getTabInfo'
EXEC p_TVersionProc$new N'BusinessObject', @c_PC_getMaster, N'p_Object$getMasterOid'

-- Connector
EXEC p_TVersionProc$new N'Connector', @c_PC_create, N'p_Connector_01$create'
EXEC p_TVersionProc$new N'Connector', @c_PC_retrieve, N'p_Connector_01$retrieve'
EXEC p_TVersionProc$new N'Connector', @c_PC_delete, N'p_Connector_01$delete'
EXEC p_TVersionProc$new N'Connector', @c_PC_change, N'p_Connector_01$change'

-- DocumentTemplate
EXEC p_TVersionProc$new N'DocumentTemplate', @c_PC_create, N'p_DocumentTemplate_01$create'
EXEC p_TVersionProc$new N'DocumentTemplate', @c_PC_change, N'p_DocumentTemplate_01$change'
EXEC p_TVersionProc$new N'DocumentTemplate', @c_PC_retrieve, N'p_DocumentTemplate_01$retrieve'
EXEC p_TVersionProc$new N'DocumentTemplate', @c_PC_delete, N'p_DocumentTemplate_01$delete'
EXEC p_TVersionProc$new N'DocumentTemplate', @c_PC_deleteRec, N'p_DocumentTemplate_01$delete'

-- FileConnector
EXEC p_TVersionProc$new N'FileConnector', @c_PC_create, N'p_Connector_01$create'
EXEC p_TVersionProc$new N'FileConnector', @c_PC_retrieve, N'p_Connector_01$retrieve'
EXEC p_TVersionProc$new N'FileConnector', @c_PC_delete, N'p_Connector_01$delete'
EXEC p_TVersionProc$new N'FileConnector', @c_PC_change, N'p_Connector_01$change'

-- HTTPScriptConnector
EXEC p_TVersionProc$new N'HTTPScriptConnector', @c_PC_create, N'p_Connector_01$create'
EXEC p_TVersionProc$new N'HTTPScriptConnector', @c_PC_retrieve, N'p_Connector_01$retrieve'
EXEC p_TVersionProc$new N'HTTPScriptConnector', @c_PC_delete, N'p_Connector_01$delete'
EXEC p_TVersionProc$new N'HTTPScriptConnector', @c_PC_change, N'p_Connector_01$change'

-- MailConnector
EXEC p_TVersionProc$new N'MailConnector', @c_PC_create, N'p_Connector_01$create'
EXEC p_TVersionProc$new N'MailConnector', @c_PC_retrieve, N'p_Connector_01$retrieve'
EXEC p_TVersionProc$new N'MailConnector', @c_PC_delete, N'p_Connector_01$delete'
EXEC p_TVersionProc$new N'MailConnector', @c_PC_change, N'p_Connector_01$change'

-- XMLViewer
EXEC p_TVersionProc$new N'XMLViewer', @c_PC_create, N'p_XMLViewer_01$create'
EXEC p_TVersionProc$new N'XMLViewer', @c_PC_retrieve, N'p_XMLViewer_01$retrieve'
EXEC p_TVersionProc$new N'XMLViewer', @c_PC_delete, N'p_XMLViewer_01$delete'
EXEC p_TVersionProc$new N'XMLViewer', @c_PC_change, N'p_XMLViewer_01$change'

-- XMLViewerContainer
EXEC p_TVersionProc$new N'XMLViewerContainer', @c_PC_create, N'p_XMLViewerContainer_01$create'
EXEC p_TVersionProc$new N'XMLViewerContainer', @c_PC_retrieve, N'p_XMLViewerContainer_01$retrieve'
EXEC p_TVersionProc$new N'XMLViewerContainer', @c_PC_delete, N'p_XMLViewerContainer_01$delete'
EXEC p_TVersionProc$new N'XMLViewerContainer', @c_PC_change, N'p_XMLViewerContainer_01$change'

-- ReceivedObject
EXEC p_TVersionProc$new N'ReceivedObject', @c_PC_create, N'p_ReceivedObject_01$create'
EXEC p_TVersionProc$new N'ReceivedObject', @c_PC_retrieve, N'p_ReceivedObject_01$retrieve'
EXEC p_TVersionProc$new N'ReceivedObject', @c_PC_delete, N'p_ReceivedObject_01$delete'
EXEC p_TVersionProc$new N'ReceivedObject', @c_PC_change, N'p_ReceivedObject_01$change'

-- Recipient
EXEC p_TVersionProc$new N'Recipient', @c_PC_create, N'p_Recipient_01$create'
EXEC p_TVersionProc$new N'Recipient', @c_PC_retrieve, N'p_Recipient_01$retrieve'
EXEC p_TVersionProc$new N'Recipient', @c_PC_delete, N'p_Recipient_01$delete'
EXEC p_TVersionProc$new N'Recipient', @c_PC_change, N'p_Recipient_01$change'

-- SentObject
EXEC p_TVersionProc$new N'SentObject', @c_PC_create, N'p_SentObject_01$create'
EXEC p_TVersionProc$new N'SentObject', @c_PC_retrieve, N'p_SentObject_01$retrieve'
EXEC p_TVersionProc$new N'SentObject', @c_PC_delete, N'p_SentObject_01$delete'
EXEC p_TVersionProc$new N'SentObject', @c_PC_change, N'p_SentObject_01$change'

-- Domain
EXEC p_TVersionProc$new N'Domain', @c_PC_create, N'p_Domain_01$create'
EXEC p_TVersionProc$new N'Domain', @c_PC_retrieve, N'p_Domain_01$retrieve'
EXEC p_TVersionProc$new N'Domain', @c_PC_delete, N'p_Domain_01$delete'
EXEC p_TVersionProc$new N'Domain', @c_PC_change, N'p_Domain_01$change'
EXEC p_TVersionProc$new N'Domain', @c_PC_deleteRec, N'p_Domain_01$delete'


-- DomainScheme
EXEC p_TVersionProc$new N'DomainScheme', @c_PC_create, N'p_DomainScheme_01$create'
EXEC p_TVersionProc$new N'DomainScheme', @c_PC_retrieve, N'p_DomainScheme_01$retrieve'
EXEC p_TVersionProc$new N'DomainScheme', @c_PC_delete, N'p_DomainScheme_01$delete'
EXEC p_TVersionProc$new N'DomainScheme', @c_PC_change, N'p_DomainScheme_01$change'
EXEC p_TVersionProc$new N'DomainScheme', @c_PC_deleteRec, N'p_DomainScheme_01$delete'

-- Attachment
EXEC p_TVersionProc$new N'Attachment', @c_PC_create, N'p_Attachment_01$create'
EXEC p_TVersionProc$new N'Attachment', @c_PC_retrieve, N'p_Attachment_01$retrieve'
EXEC p_TVersionProc$new N'Attachment', @c_PC_delete, N'p_Attachment_01$delete'
EXEC p_TVersionProc$new N'Attachment', @c_PC_change, N'p_Attachment_01$change'
EXEC p_TVersionProc$new N'Attachment', @c_PC_deleteRec, N'p_Attachment_01$delete'

-- AttachmentContainer
EXEC p_TVersionProc$new N'AttachmentContainer', @c_PC_create, N'p_AC_01$create'
EXEC p_TVersionProc$new N'AttachmentContainer', @c_PC_retrieve, N'p_AC_01$retrieve'
EXEC p_TVersionProc$new N'AttachmentContainer', @c_PC_delete, N'p_AC_01$delete'
EXEC p_TVersionProc$new N'AttachmentContainer', @c_PC_change, N'p_AC_01$change'

-- Help
EXEC p_TVersionProc$new N'Help', @c_PC_create, N'p_Help_01$create'
EXEC p_TVersionProc$new N'Help', @c_PC_retrieve, N'p_Help_01$retrieve'
EXEC p_TVersionProc$new N'Help', @c_PC_delete, N'p_Help_01$delete'
EXEC p_TVersionProc$new N'Help', @c_PC_change, N'p_Help_01$change'

-- Layout
EXEC p_TVersionProc$new N'Layout', @c_PC_create, N'p_Layout_01$create'
EXEC p_TVersionProc$new N'Layout', @c_PC_retrieve, N'p_Layout_01$retrieve'
EXEC p_TVersionProc$new N'Layout', @c_PC_delete, N'p_Layout_01$delete'
EXEC p_TVersionProc$new N'Layout', @c_PC_change, N'p_Layout_01$change'
EXEC p_TVersionProc$new N'Layout', @c_PC_copy, N'p_Layout_01$copy'

-- Locale
EXEC p_TVersionProc$new N'Locale', @c_PC_create, N'p_Locale_01$create'
EXEC p_TVersionProc$new N'Locale', @c_PC_retrieve, N'p_Locale_01$retrieve'
EXEC p_TVersionProc$new N'Locale', @c_PC_delete, N'p_Locale_01$delete'
EXEC p_TVersionProc$new N'Locale', @c_PC_change, N'p_Locale_01$change'
EXEC p_TVersionProc$new N'Locale', @c_PC_copy, N'p_Locale_01$copy'

-- QueryCreator
EXEC p_TVersionProc$new N'QueryCreator', @c_PC_create, N'p_QueryCreator_01$create'
EXEC p_TVersionProc$new N'QueryCreator', @c_PC_retrieve, N'p_QueryCreator_01$retrieve'
EXEC p_TVersionProc$new N'QueryCreator', @c_PC_change, N'p_QueryCreator_01$change'

-- DBQueryCreator
EXEC p_TVersionProc$new N'DBQueryCreator', @c_PC_create, N'p_DBQueryCreator_01$create'
EXEC p_TVersionProc$new N'DBQueryCreator', @c_PC_retrieve, N'p_DBQueryCreator_01$retrieve'
EXEC p_TVersionProc$new N'DBQueryCreator', @c_PC_change, N'p_DBQueryCreator_01$change'
EXEC p_TVersionProc$new N'DBQueryCreator', @c_PC_copy, N'p_DBQueryCreator_01$BOCopy'

-- QueryExecutive
EXEC p_TVersionProc$new N'QueryExecutive', @c_PC_create, N'p_QueryExecutive_01$create'
EXEC p_TVersionProc$new N'QueryExecutive', @c_PC_retrieve, N'p_QueryExecutive_01$retrieve'
EXEC p_TVersionProc$new N'QueryExecutive', @c_PC_change, N'p_QueryExecutive_01$change'

-- Referenz
EXEC p_TVersionProc$new N'Referenz', @c_PC_create, N'p_Referenz_01$create'

-- Rights
EXEC p_TVersionProc$new N'Rights', @c_PC_create, N'p_Rights_01$create'
EXEC p_TVersionProc$new N'Rights', @c_PC_retrieve, N'p_Rights_01$retrieve'
EXEC p_TVersionProc$new N'Rights', @c_PC_delete, N'p_Rights_01$delete'
EXEC p_TVersionProc$new N'Rights', @c_PC_deleteRec, N'p_Rights_01$deleteRightsRec'
EXEC p_TVersionProc$new N'Rights', @c_PC_change, N'p_Rights_01$change'
EXEC p_TVersionProc$new N'Rights', @c_PC_getUpper, N'p_Rights_01$getUpperOid'

-- RightsContainer
EXEC p_TVersionProc$new N'RightsContainer', @c_PC_retrieve, N'p_RightsContainer_01$retrieve'

-- UserProfile
EXEC p_TVersionProc$new N'UserProfile', @c_PC_create, N'p_UserProfile_01$create'
EXEC p_TVersionProc$new N'UserProfile', @c_PC_retrieve, N'p_UserProfile_01$retrieve'
EXEC p_TVersionProc$new N'UserProfile', @c_PC_delete, N'p_UserProfile_01$delete'
EXEC p_TVersionProc$new N'UserProfile', @c_PC_change, N'p_UserProfile_01$change'

-- UserAddress
EXEC p_TVersionProc$new N'UserAddress', @c_PC_create, N'p_UserAddress_01$create'
EXEC p_TVersionProc$new N'UserAddress', @c_PC_retrieve, N'p_UserAddress_01$retrieve'
EXEC p_TVersionProc$new N'UserAddress', @c_PC_delete, N'p_UserAddress_01$delete'
EXEC p_TVersionProc$new N'UserAddress', @c_PC_change, N'p_UserAddress_01$change'

-- Workspace
EXEC p_TVersionProc$new N'Workspace', @c_PC_create, N'p_Workspace_01$create'
EXEC p_TVersionProc$new N'Workspace', @c_PC_retrieve, N'p_Workspace_01$retrieve'
EXEC p_TVersionProc$new N'Workspace', @c_PC_change, N'p_Workspace_01$change'

-- p_Group
EXEC p_TVersionProc$new N'Group', @c_PC_create, N'p_Group_01$create'
EXEC p_TVersionProc$new N'Group', @c_PC_retrieve, N'p_Group_01$retrieve'
EXEC p_TVersionProc$new N'Group', @c_PC_delete, N'p_Group_01$delete'
EXEC p_TVersionProc$new N'Group', @c_PC_deleteRec, N'p_Group_01$delete'
EXEC p_TVersionProc$new N'Group', @c_PC_change, N'p_Group_01$change'
EXEC p_TVersionProc$new N'Group', @c_PC_changeState, N'p_Group_01$changeState'

-- User
EXEC p_TVersionProc$new N'User', @c_PC_create, N'p_User_01$create'
EXEC p_TVersionProc$new N'User', @c_PC_retrieve, N'p_User_01$retrieve'
EXEC p_TVersionProc$new N'User', @c_PC_delete, N'p_User_01$delete'
EXEC p_TVersionProc$new N'User', @c_PC_deleteRec, N'p_User_01$delete'
EXEC p_TVersionProc$new N'User', @c_PC_change, N'p_User_01$change'
EXEC p_TVersionProc$new N'User', @c_PC_changeState, N'p_User_01$changeState'
EXEC p_TVersionProc$new N'User', @c_PC_getNotificationData, N'p_User_01$getNotificationData'

-- UserAdminContainer
EXEC p_TVersionProc$new N'UserAdminContainer', @c_PC_create, N'p_UserAdminContainer_01$create'

-- Workflow
EXEC p_TVersionProc$new N'Workflow', @c_PC_create, N'p_Workflow_01$create'
EXEC p_TVersionProc$new N'Workflow', @c_PC_retrieve, N'p_Workflow_01$retrieve'
EXEC p_TVersionProc$new N'Workflow', @c_PC_delete, N'p_Workflow_01$delete'
EXEC p_TVersionProc$new N'Workflow', @c_PC_change, N'p_Workflow_01$change'

-- Translator
EXEC p_TVersionProc$new N'Translator', @c_PC_create, N'p_Translator_01$create'
EXEC p_TVersionProc$new N'Translator', @c_PC_retrieve, N'p_Translator_01$retrieve'
--EXEC p_TVersionProc$new N'Translator', @c_PC_delete, N'p_Translator_01$delete'
--EXEC p_TVersionProc$new N'Translator', @c_PC_deleteRec, N'p_Translator_01$delete'
EXEC p_TVersionProc$new N'Translator', @c_PC_change, N'p_Translator_01$change'
EXEC p_TVersionProc$new N'Translator', @c_PC_copy, N'p_Translator_01$BOCopy'

-- ASCIITranslator
EXEC p_TVersionProc$new N'ASCIITranslator', @c_PC_create, N'p_ASCIITranslator_01$create'
EXEC p_TVersionProc$new N'ASCIITranslator', @c_PC_retrieve, N'p_ASCIITranslator_01$retrieve'
--EXEC p_TVersionProc$new N'ASCIITranslator', @c_PC_delete, N'p_ASCIITranslator_01$delete'
--EXEC p_TVersionProc$new N'ASCIITranslator', @c_PC_deleteRec, N'p_ASCIITranslator_01$delete'
EXEC p_TVersionProc$new N'ASCIITranslator', @c_PC_change, N'p_ASCIITranslator_01$change'
EXEC p_TVersionProc$new N'ASCIITranslator', @c_PC_copy, N'p_ASCIITranslator_01$BOCopy'

-- EDITranslator
EXEC p_TVersionProc$new N'EDITranslator', @c_PC_create, N'p_EDITranslator_01$create'
EXEC p_TVersionProc$new N'EDITranslator', @c_PC_retrieve, N'p_EDITranslator_01$retrieve'
EXEC p_TVersionProc$new N'EDITranslator', @c_PC_change, N'p_EDITranslator_01$change'
EXEC p_TVersionProc$new N'EDITranslator', @c_PC_copy, N'p_EDITranslator_01$BOCopy'

PRINT 'Created all entries in table ibs_TVersionProc'

GO

-- show count messages again:
SET NOCOUNT OFF
GO
