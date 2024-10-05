/******************************************************************************
 * Create all entries of procedures in the framework. <BR>
 *
 * @version     $Id: createTVersionProc.sql,v 1.7 2003/10/21 08:53:12 klaus Exp $
 *
 * @author      Mario Oberdorfer (MO)  010131
 ******************************************************************************
 */

-- l_retValue := p_TVersionProc$add (tVersionId, procCode, procName);
-- ex.:
-- l_retValue := p_TVersionProc$add (0x01010021, c_PC_CREATE, 'p_Object$create');
--
-- l_retValue := p_TVersionProc$new (typeCode, procCode, procName);
-- ex.:
-- l_retValue := p_TVersionProc$new ('Container', c_PC_CREATE, 'p_Object$create');


DECLARE
    -- constants:
    c_languageId            CONSTANT INTEGER := 0; -- the current language
    c_PC_create             CONSTANT VARCHAR2 (63) := 'create'; -- procedure code for create
    c_PC_retrieve           CONSTANT VARCHAR2 (63) := 'retrieve'; -- procedure code for retrieve
    c_PC_change             CONSTANT VARCHAR2 (63) := 'change'; -- procedure code for change
    c_PC_copy               CONSTANT VARCHAR2 (63) := 'copy'; -- procedure code for copy
    c_PC_delete             CONSTANT VARCHAR2 (63) := 'delete'; -- procedure code for delete
    c_PC_deleteRec          CONSTANT VARCHAR2 (63) := 'deleteRec'; -- procedure code for recursive delete
    c_PC_move               CONSTANT VARCHAR2 (63) := 'move'; -- procedure code for move
    c_PC_changeState        CONSTANT VARCHAR2 (63) := 'changeState'; -- procedure code for forchangeState
    c_PC_changeProcessState CONSTANT VARCHAR2 (63) := 'changeProcessState'; -- procedure code for changeProcessState
    c_PC_changeOwner        CONSTANT VARCHAR2 (63) := 'changeOwner'; -- procedure code for changeOwner
    c_PC_checkOut           CONSTANT VARCHAR2 (63) := 'checkOut'; -- procedure code for checkOut
    c_PC_InsertProtocol     CONSTANT VARCHAR2 (63) := 'insertProtocol'; -- procedure code for InsertProtocol
    c_PC_checkIn            CONSTANT VARCHAR2 (63) := 'checkIn'; -- procedure code for checkIn
    c_PC_undelete           CONSTANT VARCHAR2 (63) := 'undelete'; -- procedure code for undelete
    c_PC_undeleteRec        CONSTANT VARCHAR2 (63) := 'undeleteRec'; -- procedure code for recursive undelete
    c_PC_deleteAllRefs      CONSTANT VARCHAR2 (63) := 'deleteAllRefs'; -- procedure code for deleteAllRefs
    c_PC_getUpper           CONSTANT VARCHAR2 (63) := 'getUpper'; -- procedure code for getUpper
    c_PC_getTab             CONSTANT VARCHAR2 (63) := 'getTab'; -- procedure code for getTab
    c_PC_getMaster          CONSTANT VARCHAR2 (63) := 'getMaster'; -- procedure code for getMaster
    c_PC_createQty          CONSTANT VARCHAR2 (63) := 'createQty'; -- procedure code for createQty 
    c_PC_createVal          CONSTANT VARCHAR2 (63) := 'createVal'; -- procedure code for createVal
    c_PC_getNotificationData CONSTANT VARCHAR2 (63) := 'getNotificationData'; -- procedure code for getNotificationData
    
    -- local variables:
    l_retValue              INTEGER := 0;   -- return value of function

 
BEGIN
-- BusinessObject
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_create, 'p_Object$create');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_change, 'p_Object$change');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_move, 'p_Object$move');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_changeState, 'p_Object$changeState');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_changeProcessState, 'p_Object$changeProcessState');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_changeOwner, 'p_Object$changeOwnerRec');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_retrieve, 'p_Object$retrieve');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_checkOut, 'p_Object$checkOut');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_InsertProtocol, 'p_Object$InsertProtocol');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_checkIn, 'p_Object$checkIn');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_delete, 'p_Object$delete');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_deleteRec, 'p_Object$delete');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_undelete, 'p_Object$undelete');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_undeleteRec, 'p_Object$undelete');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_deleteAllRefs, 'p_Object$deleteAllRefs');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_copy, 'p_Object$copy');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_getUpper, 'p_Object$getUpperOid');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_getTab, 'p_Object$getTabInfo');
l_retValue := p_TVersionProc$new ('BusinessObject', c_PC_getMaster, 'p_Object$getMasterOid');

-- Connector
l_retValue := p_TVersionProc$new ('Connector', c_PC_create, 'p_Connector_01$create');
l_retValue := p_TVersionProc$new ('Connector', c_PC_retrieve, 'p_Connector_01$retrieve');
l_retValue := p_TVersionProc$new ('Connector', c_PC_delete, 'p_Connector_01$delete');
l_retValue := p_TVersionProc$new ('Connector', c_PC_change, 'p_Connector_01$change');

-- DocumentTemplate
l_retValue := p_TVersionProc$new ('DocumentTemplate', c_PC_create, 'p_DocumentTemplate_01$create');
l_retValue := p_TVersionProc$new ('DocumentTemplate', c_PC_change, 'p_DocumentTemplate_01$change');
l_retValue := p_TVersionProc$new ('DocumentTemplate', c_PC_retrieve, 'p_DocumentTemplate_01$retrieve');
l_retValue := p_TVersionProc$new ('DocumentTemplate', c_PC_delete, 'p_DocumentTemplate_01$delete');
l_retValue := p_TVersionProc$new ('DocumentTemplate', c_PC_deleteRec, 'p_DocumentTemplate_01$delete');

-- FileConnector
l_retValue := p_TVersionProc$new ('FileConnector', c_PC_create, 'p_Connector_01$create');
l_retValue := p_TVersionProc$new ('FileConnector', c_PC_retrieve, 'p_Connector_01$retrieve');
l_retValue := p_TVersionProc$new ('FileConnector', c_PC_delete, 'p_Connector_01$delete');
l_retValue := p_TVersionProc$new ('FileConnector', c_PC_change, 'p_Connector_01$change');

-- HTTPScriptConnector
l_retValue := p_TVersionProc$new ('HTTPScriptConnector', c_PC_create, 'p_Connector_01$create');
l_retValue := p_TVersionProc$new ('HTTPScriptConnector', c_PC_retrieve, 'p_Connector_01$retrieve');
l_retValue := p_TVersionProc$new ('HTTPScriptConnector', c_PC_delete, 'p_Connector_01$delete');
l_retValue := p_TVersionProc$new ('HTTPScriptConnector', c_PC_change, 'p_Connector_01$change');

-- MailConnector
l_retValue := p_TVersionProc$new ('MailConnector', c_PC_create, 'p_Connector_01$create');
l_retValue := p_TVersionProc$new ('MailConnector', c_PC_retrieve, 'p_Connector_01$retrieve');
l_retValue := p_TVersionProc$new ('MailConnector', c_PC_delete, 'p_Connector_01$delete');
l_retValue := p_TVersionProc$new ('MailConnector', c_PC_change, 'p_Connector_01$change');

-- XMLViewer
l_retValue := p_TVersionProc$new ('XMLViewer', c_PC_create, 'p_XMLViewer_01$create');
l_retValue := p_TVersionProc$new ('XMLViewer', c_PC_retrieve, 'p_XMLViewer_01$retrieve');
l_retValue := p_TVersionProc$new ('XMLViewer', c_PC_delete, 'p_XMLViewer_01$delete');
l_retValue := p_TVersionProc$new ('XMLViewer', c_PC_change, 'p_XMLViewer_01$change');

-- XMLViewerContainer
l_retValue := p_TVersionProc$new ('XMLViewerContainer', c_PC_create, 'p_XMLViewerContainer_01$create');
l_retValue := p_TVersionProc$new ('XMLViewerContainer', c_PC_retrieve, 'p_XMLViewerContainer_01$retrieve');
l_retValue := p_TVersionProc$new ('XMLViewerContainer', c_PC_delete, 'p_XMLViewerContainer_01$delete');
l_retValue := p_TVersionProc$new ('XMLViewerContainer', c_PC_change, 'p_XMLViewerContainer_01$change');

-- ReceivedObject
l_retValue := p_TVersionProc$new ('ReceivedObject', c_PC_create, 'p_ReceivedObject_01$create');
l_retValue := p_TVersionProc$new ('ReceivedObject', c_PC_retrieve, 'p_ReceivedObject_01$retrieve');
l_retValue := p_TVersionProc$new ('ReceivedObject', c_PC_delete, 'p_ReceivedObject_01$delete');
l_retValue := p_TVersionProc$new ('ReceivedObject', c_PC_change, 'p_ReceivedObject_01$change');

-- Recipient
l_retValue := p_TVersionProc$new ('Recipient', c_PC_create, 'p_Recipient_01$create');
l_retValue := p_TVersionProc$new ('Recipient', c_PC_retrieve, 'p_Recipient_01$retrieve');
l_retValue := p_TVersionProc$new ('Recipient', c_PC_delete, 'p_Recipient_01$delete');
l_retValue := p_TVersionProc$new ('Recipient', c_PC_change, 'p_Recipient_01$change');

-- SentObject
l_retValue := p_TVersionProc$new ('SentObject', c_PC_create, 'p_SentObject_01$create');
l_retValue := p_TVersionProc$new ('SentObject', c_PC_retrieve, 'p_SentObject_01$retrieve');
l_retValue := p_TVersionProc$new ('SentObject', c_PC_delete, 'p_SentObject_01$delete');
l_retValue := p_TVersionProc$new ('SentObject', c_PC_change, 'p_SentObject_01$change');

-- Domain
l_retValue := p_TVersionProc$new ('Domain', c_PC_create, 'p_Domain_01$create');
l_retValue := p_TVersionProc$new ('Domain', c_PC_retrieve, 'p_Domain_01$retrieve');
l_retValue := p_TVersionProc$new ('Domain', c_PC_delete, 'p_Domain_01$delete');
l_retValue := p_TVersionProc$new ('Domain', c_PC_change, 'p_Domain_01$change');
l_retValue := p_TVersionProc$new ('Domain', c_PC_deleteRec, 'p_Domain_01$delete');


-- DomainScheme
l_retValue := p_TVersionProc$new ('DomainScheme', c_PC_create, 'p_DomainScheme_01$create');
l_retValue := p_TVersionProc$new ('DomainScheme', c_PC_retrieve, 'p_DomainScheme_01$retrieve');
l_retValue := p_TVersionProc$new ('DomainScheme', c_PC_delete, 'p_DomainScheme_01$delete');
l_retValue := p_TVersionProc$new ('DomainScheme', c_PC_change, 'p_DomainScheme_01$change');
l_retValue := p_TVersionProc$new ('DomainScheme', c_PC_deleteRec, 'p_DomainScheme_01$delete');

-- Attachment
l_retValue := p_TVersionProc$new ('Attachment', c_PC_create, 'p_Attachment_01$create');
l_retValue := p_TVersionProc$new ('Attachment', c_PC_retrieve, 'p_Attachment_01$retrieve');
l_retValue := p_TVersionProc$new ('Attachment', c_PC_delete, 'p_Attachment_01$delete');
l_retValue := p_TVersionProc$new ('Attachment', c_PC_change, 'p_Attachment_01$change');
l_retValue := p_TVersionProc$new ('Attachment', c_PC_deleteRec, 'p_Attachment_01$delete');

-- AttachmentContainer
l_retValue := p_TVersionProc$new ('AttachmentContainer', c_PC_create, 'p_AC_01$create');
l_retValue := p_TVersionProc$new ('AttachmentContainer', c_PC_retrieve, 'p_AC_01$retrieve');
l_retValue := p_TVersionProc$new ('AttachmentContainer', c_PC_delete, 'p_AC_01$delete');
l_retValue := p_TVersionProc$new ('AttachmentContainer', c_PC_change, 'p_AC_01$change');

-- Help
l_retValue := p_TVersionProc$new ('Help', c_PC_create, 'p_Help_01$create');
l_retValue := p_TVersionProc$new ('Help', c_PC_retrieve, 'p_Help_01$retrieve');
l_retValue := p_TVersionProc$new ('Help', c_PC_delete, 'p_Help_01$delete');
l_retValue := p_TVersionProc$new ('Help', c_PC_change, 'p_Help_01$change');

-- Layout
l_retValue := p_TVersionProc$new ('Layout', c_PC_create, 'p_Layout_01$create');
l_retValue := p_TVersionProc$new ('Layout', c_PC_retrieve, 'p_Layout_01$retrieve');
l_retValue := p_TVersionProc$new ('Layout', c_PC_delete, 'p_Layout_01$delete');
l_retValue := p_TVersionProc$new ('Layout', c_PC_change, 'p_Layout_01$change');
l_retValue := p_TVersionProc$new ('Layout', c_PC_copy, 'p_Layout_01$copy');

-- QueryCreator
l_retValue := p_TVersionProc$new ('QueryCreator', c_PC_create, 'p_QueryCreator_01$create');
l_retValue := p_TVersionProc$new ('QueryCreator', c_PC_retrieve, 'p_QueryCreator_01$retrieve');
l_retValue := p_TVersionProc$new ('QueryCreator', c_PC_change, 'p_QueryCreator_01$change');

-- DBQueryCreator
l_retValue := p_TVersionProc$new ('DBQueryCreator', c_PC_create, 'p_DBQueryCreator_01$create');
l_retValue := p_TVersionProc$new ('DBQueryCreator', c_PC_retrieve, 'p_DBQueryCreator_01$retrieve');
l_retValue := p_TVersionProc$new ('DBQueryCreator', c_PC_change, 'p_DBQueryCreator_01$change');
l_retValue := p_TVersionProc$new ('DBQueryCreator', c_PC_copy, 'p_DBQueryCreator_01$BOCopy');

-- QueryExecutive
l_retValue := p_TVersionProc$new ('QueryExecutive', c_PC_create, 'p_QueryExecutive_01$create');
l_retValue := p_TVersionProc$new ('QueryExecutive', c_PC_retrieve, 'p_QueryExecutive_01$retrieve');
l_retValue := p_TVersionProc$new ('QueryExecutive', c_PC_change, 'p_QueryExecutive_01$change');

-- Referenz
l_retValue := p_TVersionProc$new ('Referenz', c_PC_create, 'p_Referenz_01$create');

-- Rights
l_retValue := p_TVersionProc$new ('Rights', c_PC_create, 'p_Rights_01$create');
l_retValue := p_TVersionProc$new ('Rights', c_PC_retrieve, 'p_Rights_01$retrieve');
l_retValue := p_TVersionProc$new ('Rights', c_PC_delete, 'p_Rights_01$delete');
l_retValue := p_TVersionProc$new ('Rights', c_PC_deleteRec, 'p_Rights_01$deleteRightsRec');
l_retValue := p_TVersionProc$new ('Rights', c_PC_change, 'p_Rights_01$change');
l_retValue := p_TVersionProc$new ('Rights', c_PC_getUpper, 'p_Rights_01$getUpperOid');

-- RightsContainer
l_retValue := p_TVersionProc$new ('RightsContainer', c_PC_retrieve, 'p_RightsContainer_01$retrieve');

-- UserProfile
l_retValue := p_TVersionProc$new ('UserProfile', c_PC_create, 'p_UserProfile_01$create');
l_retValue := p_TVersionProc$new ('UserProfile', c_PC_retrieve, 'p_UserProfile_01$retrieve');
l_retValue := p_TVersionProc$new ('UserProfile', c_PC_delete, 'p_UserProfile_01$delete');
l_retValue := p_TVersionProc$new ('UserProfile', c_PC_change, 'p_UserProfile_01$change');

-- UserAddress
l_retValue := p_TVersionProc$new ('UserAddress', c_PC_create, 'p_UserAddress_01$create');
l_retValue := p_TVersionProc$new ('UserAddress', c_PC_retrieve, 'p_UserAddress_01$retrieve');
l_retValue := p_TVersionProc$new ('UserAddress', c_PC_delete, 'p_UserAddress_01$delete');
l_retValue := p_TVersionProc$new ('UserAddress', c_PC_change, 'p_UserAddress_01$change');

-- Workspace
l_retValue := p_TVersionProc$new ('Workspace', c_PC_create, 'p_Workspace_01$create');
l_retValue := p_TVersionProc$new ('Workspace', c_PC_retrieve, 'p_Workspace_01$retrieve');
l_retValue := p_TVersionProc$new ('Workspace', c_PC_change, 'p_Workspace_01$change');

-- p_Group
l_retValue := p_TVersionProc$new ('Group', c_PC_create, 'p_Group_01$create');
l_retValue := p_TVersionProc$new ('Group', c_PC_retrieve, 'p_Group_01$retrieve');
l_retValue := p_TVersionProc$new ('Group', c_PC_delete, 'p_Group_01$delete');
l_retValue := p_TVersionProc$new ('Group', c_PC_deleteRec, 'p_Group_01$delete');
l_retValue := p_TVersionProc$new ('Group', c_PC_change, 'p_Group_01$change');
l_retValue := p_TVersionProc$new ('Group', c_PC_changeState, 'p_Group_01$changeState');

-- User
l_retValue := p_TVersionProc$new ('User', c_PC_create, 'p_User_01$create');
l_retValue := p_TVersionProc$new ('User', c_PC_retrieve, 'p_User_01$retrieve');
l_retValue := p_TVersionProc$new ('User', c_PC_delete, 'p_User_01$delete');
l_retValue := p_TVersionProc$new ('User', c_PC_deleteRec, 'p_User_01$delete');
l_retValue := p_TVersionProc$new ('User', c_PC_change, 'p_User_01$change');
l_retValue := p_TVersionProc$new ('User', c_PC_changeState, 'p_User_01$changeState');
l_retValue := p_TVersionProc$new ('User', c_PC_getNotificationData, 'p_User_01$getNotificationData');

-- UserAdminContainer
l_retValue := p_TVersionProc$new ('UserAdminContainer', c_PC_create, 'p_UserAdminContainer_01$create');

-- Workflow
l_retValue := p_TVersionProc$new ('Workflow', c_PC_create, 'p_Workflow_01$create');
l_retValue := p_TVersionProc$new ('Workflow', c_PC_retrieve, 'p_Workflow_01$retrieve');
l_retValue := p_TVersionProc$new ('Workflow', c_PC_delete, 'p_Workflow_01$delete');
l_retValue := p_TVersionProc$new ('Workflow', c_PC_change, 'p_Workflow_01$change');

-- Translator
l_retValue := p_TVersionProc$new ('Translator', c_PC_create, 'p_Translator_01$create');
l_retValue := p_TVersionProc$new ('Translator', c_PC_retrieve, 'p_Translator_01$retrieve');
--l_retValue := p_TVersionProc$new ('Translator', c_PC_delete, 'p_Translator_01$delete');
--l_retValue := p_TVersionProc$new ('Translator', c_PC_deleteRec, 'p_Translator_01$delete');
l_retValue := p_TVersionProc$new ('Translator', c_PC_change, 'p_Translator_01$change');
l_retValue := p_TVersionProc$new ('Translator', c_PC_copy, 'p_Translator_01$BOcopy');

-- ASCIITranslator
l_retValue := p_TVersionProc$new ('ASCIITranslator', c_PC_create, 'p_ASCIITranslator_01$create');
l_retValue := p_TVersionProc$new ('ASCIITranslator', c_PC_retrieve, 'p_ASCIITranslator_01$retrieve');
--l_retValue := p_TVersionProc$new ('ASCIITranslator', c_PC_delete, 'p_ASCIITranslator_01$delete');
--l_retValue := p_TVersionProc$new ('ASCIITranslator', c_PC_deleteRec, 'p_ASCIITranslator_01$delete');
l_retValue := p_TVersionProc$new ('ASCIITranslator', c_PC_change, 'p_ASCIITranslator_01$change');
l_retValue := p_TVersionProc$new ('ASCIITranslator', c_PC_copy, 'p_ASCIITranslator_01$BOcopy');

-- EDITranslator
l_retValue := p_TVersionProc$new ('EDITranslator', c_PC_create, 'p_EDITranslator_01$create');
l_retValue := p_TVersionProc$new ('EDITranslator', c_PC_retrieve, 'p_EDITranslator_01$retrieve');
l_retValue := p_TVersionProc$new ('EDITranslator', c_PC_change, 'p_EDITranslator_01$change');
l_retValue := p_TVersionProc$new ('EDITranslator', c_PC_copy, 'p_EDITranslator_01$BOcopy');


debug ('Created all ibs entries in table ibs_TVersionProc');

END;
/

COMMIT WORK;
/

EXIT;
