/******************************************************************************
 * Create all entries of procedures in m2. <BR>
 *
 * @version     $Id: createTVersionProc.sql,v 1.10 2004/01/16 00:45:31 klaus Exp $
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
-- Catalog
l_retValue := p_TVersionProc$new ('Catalog', c_PC_create, 'p_Catalog_01$create');
l_retValue := p_TVersionProc$new ('Catalog', c_PC_retrieve, 'p_Catalog_01$retrieve');
l_retValue := p_TVersionProc$new ('Catalog', c_PC_delete, 'p_Catalog_01$delete');
l_retValue := p_TVersionProc$new ('Catalog', c_PC_change, 'p_Catalog_01$change');

-- Order
l_retValue := p_TVersionProc$new ('Order', c_PC_create, 'p_Order_01$create');
l_retValue := p_TVersionProc$new ('Order', c_PC_retrieve, 'p_Order_01$retrieve');
l_retValue := p_TVersionProc$new ('Order', c_PC_delete, 'p_Order_01$delete');

-- Price
l_retValue := p_TVersionProc$new ('ProductSizeColor', c_PC_create, 'p_Price_01$create');
l_retValue := p_TVersionProc$new ('ProductSizeColor', c_PC_retrieve, 'p_Price_01$retrieve');
l_retValue := p_TVersionProc$new ('ProductSizeColor', c_PC_delete, 'p_Price_01$delete');
l_retValue := p_TVersionProc$new ('ProductSizeColor', c_PC_change, 'p_Price_01$change');
l_retValue := p_TVersionProc$new ('ProductSizeColor', c_PC_copy, 'p_Price_01$BOCopy');

-- Product
l_retValue := p_TVersionProc$new ('Product', c_PC_create, 'p_Product_01$create');
l_retValue := p_TVersionProc$new ('Product', c_PC_retrieve, 'p_Product_01$retrieve');
l_retValue := p_TVersionProc$new ('Product', c_PC_change, 'p_Product_01$change');

-- ProductBrand
l_retValue := p_TVersionProc$new ('ProductBrand', c_PC_create, 'p_ProductBrand_01$create');
l_retValue := p_TVersionProc$new ('ProductBrand', c_PC_retrieve, 'p_ProductBrand_01$retrieve');
l_retValue := p_TVersionProc$new ('ProductBrand', c_PC_delete, 'p_ProductBrand_01$delete');
l_retValue := p_TVersionProc$new ('ProductBrand', c_PC_change, 'p_ProductBrand_01$change');

-- ProductCollection
l_retValue := p_TVersionProc$new ('ProductCollection', c_PC_create, 'p_ProductCollect_01$create');
l_retValue := p_TVersionProc$new ('ProductCollection', c_PC_retrieve, 'p_ProductCollect_01$retrieve');
l_retValue := p_TVersionProc$new ('ProductCollection', c_PC_delete, 'p_ProductCollect_01$delete');
l_retValue := p_TVersionProc$new ('ProductCollection', c_PC_change, 'p_ProductCollect_01$change');
l_retValue := p_TVersionProc$new ('ProductCollection', c_PC_createQty, 'p_ProductCollect_01$createQty');
l_retValue := p_TVersionProc$new ('ProductCollection', c_PC_createVal, 'p_ProductCollect_01$createVal');

-- ProductGroup
l_retValue := p_TVersionProc$new ('ProductGroup', c_PC_create, 'p_ProductGroup_01$create');
l_retValue := p_TVersionProc$new ('ProductGroup', c_PC_retrieve, 'p_ProductGroup_01$retrieve');
l_retValue := p_TVersionProc$new ('ProductGroup', c_PC_delete, 'p_ProductGroup_01$delete');
l_retValue := p_TVersionProc$new ('ProductGroup', c_PC_change, 'p_ProductGroup_01$change');

-- ProductGrpProfile
l_retValue := p_TVersionProc$new ('ProductGroupProfile', c_PC_create, 'p_ProductGrpProfile_01$create');
l_retValue := p_TVersionProc$new ('ProductGroupProfile', c_PC_retrieve, 'p_ProductGrpProfile_01$retrieve');
l_retValue := p_TVersionProc$new ('ProductGroupProfile', c_PC_delete, 'p_ProductGrpProfile_01$delete');
l_retValue := p_TVersionProc$new ('ProductGroupProfile', c_PC_change, 'p_ProductGrpProfile_01$change');

-- ProductProfile
l_retValue := p_TVersionProc$new ('ProductProfile', c_PC_create, 'p_ProductProfile_01$create');
l_retValue := p_TVersionProc$new ('ProductProfile', c_PC_retrieve, 'p_ProductProfile_01$retrieve');
l_retValue := p_TVersionProc$new ('ProductProfile', c_PC_delete, 'p_ProductProfile_01$delete');
l_retValue := p_TVersionProc$new ('ProductProfile', c_PC_change, 'p_ProductProfile_01$change');
 
-- PaymentType
l_retValue := p_TVersionProc$new ('PaymentType', c_PC_create, 'p_PaymentType_01$create');
l_retValue := p_TVersionProc$new ('PaymentType', c_PC_retrieve, 'p_PaymentType_01$retrieve');
l_retValue := p_TVersionProc$new ('PaymentType', c_PC_delete, 'p_PaymentType_01$delete');
l_retValue := p_TVersionProc$new ('PaymentType', c_PC_change, 'p_PaymentType_01$change');


-- ProductProperties
l_retValue := p_TVersionProc$new ('Property', c_PC_create, 'p_ProdProperties_01$create');
l_retValue := p_TVersionProc$new ('Property', c_PC_retrieve, 'p_ProdProperties_01$retrieve');
l_retValue := p_TVersionProc$new ('Property', c_PC_delete, 'p_ProdProperties_01$delete');
l_retValue := p_TVersionProc$new ('Property', c_PC_change, 'p_ProdProperties_01$change');

-- Participant
l_retValue := p_TVersionProc$new ('Participant', c_PC_create, 'p_Participant_01$create');
l_retValue := p_TVersionProc$new ('Participant', c_PC_retrieve, 'p_Participant_01$retrieve');
l_retValue := p_TVersionProc$new ('Participant', c_PC_delete, 'p_Participant_01$delete');
l_retValue := p_TVersionProc$new ('Participant', c_PC_change, 'p_Participant_01$change');

-- Termin
l_retValue := p_TVersionProc$new ('Termin', c_PC_create, 'p_Termin_01$create');
l_retValue := p_TVersionProc$new ('Termin', c_PC_retrieve, 'p_Termin_01$retrieve');
l_retValue := p_TVersionProc$new ('Termin', c_PC_delete, 'p_Termin_01$delete');
l_retValue := p_TVersionProc$new ('Termin', c_PC_change, 'p_Termin_01$change');

-- Article
l_retValue := p_TVersionProc$new ('Article', c_PC_create, 'p_Article_01$create');
l_retValue := p_TVersionProc$new ('Article', c_PC_retrieve, 'p_Article_01$retrieve');
l_retValue := p_TVersionProc$new ('Article', c_PC_delete, 'p_Article_01$delete');
l_retValue := p_TVersionProc$new ('Article', c_PC_change, 'p_Article_01$change');
l_retValue := p_TVersionProc$new ('Article', c_PC_changeState, 'p_Article_01$changeState');

-- BlackBoard
l_retValue := p_TVersionProc$new ('BlackBoard', c_PC_create, 'p_BlackBoard_01$create');
l_retValue := p_TVersionProc$new ('BlackBoard', c_PC_retrieve, 'p_Discussion_01$retrieve');
l_retValue := p_TVersionProc$new ('BlackBoard', c_PC_delete, 'p_Discussion_01$delete');
l_retValue := p_TVersionProc$new ('BlackBoard', c_PC_change, 'p_Discussion_01$change');

-- DiscXMLViewer
l_retValue := p_TVersionProc$new ('DiscXMLViewer', c_PC_create, 'p_DiscXMLViewer_01$create');
l_retValue := p_TVersionProc$new ('DiscXMLViewer', c_PC_retrieve, 'p_DiscXMLViewer_01$retrieve');
l_retValue := p_TVersionProc$new ('DiscXMLViewer', c_PC_delete, 'p_DiscXMLViewer_01$delete');
l_retValue := p_TVersionProc$new ('DiscXMLViewer', c_PC_change, 'p_DiscXMLViewer_01$change');

-- Discussion
l_retValue := p_TVersionProc$new ('Discussion', c_PC_create, 'p_Discussion_01$create');
l_retValue := p_TVersionProc$new ('Discussion', c_PC_retrieve, 'p_Discussion_01$retrieve');
l_retValue := p_TVersionProc$new ('Discussion', c_PC_delete, 'p_Discussion_01$delete');
l_retValue := p_TVersionProc$new ('Discussion', c_PC_change, 'p_Discussion_01$change');

-- DiscContainer
l_retValue := p_TVersionProc$new ('DiscussionContainer', c_PC_create, 'p_DiscContainer_01$create');

-- Thread
l_retValue := p_TVersionProc$new ('Thread', c_PC_create, 'p_Thread_01$create');
l_retValue := p_TVersionProc$new ('Thread', c_PC_retrieve, 'p_Thread_01$retrieve');
l_retValue := p_TVersionProc$new ('Thread', c_PC_delete, 'p_Thread_01$delete');
l_retValue := p_TVersionProc$new ('Thread', c_PC_change, 'p_Thread_01$change');
l_retValue := p_TVersionProc$new ('Thread', c_PC_changeState, 'p_Article_01$changeState');

-- XMLDiscussion
l_retValue := p_TVersionProc$new ('XMLDiscussion', c_PC_create, 'p_Discussion_01$create');
l_retValue := p_TVersionProc$new ('XMLDiscussion', c_PC_retrieve, 'p_XMLDiscussion_01$retrieve');
l_retValue := p_TVersionProc$new ('XMLDiscussion', c_PC_delete, 'p_Discussion_01$delete');
l_retValue := p_TVersionProc$new ('XMLDiscussion', c_PC_change, 'p_XMLDiscussion_01$change');

-- XMLDiscTemplate
l_retValue := p_TVersionProc$new ('XMLDiscussionTemplate', c_PC_create, 'p_XMLDiscTemplate_01$create');
l_retValue := p_TVersionProc$new ('XMLDiscussionTemplate', c_PC_retrieve, 'p_XMLDiscTemplate_01$retrieve');
l_retValue := p_TVersionProc$new ('XMLDiscussionTemplate', c_PC_delete, 'p_XMLDiscTemplate_01$delete');
l_retValue := p_TVersionProc$new ('XMLDiscussionTemplate', c_PC_change, 'p_XMLDiscTemplate_01$change');
l_retValue := p_TVersionProc$new ('XMLDiscussionTemplate', c_PC_deleteRec, 'p_XMLDiscTemplate_01$delete');

-- Document
l_retValue := p_TVersionProc$new ('Document', c_PC_create, 'p_Document_01$create');
l_retValue := p_TVersionProc$new ('Document', c_PC_retrieve, 'p_Document_01$retrieve');
l_retValue := p_TVersionProc$new ('Document', c_PC_change, 'p_Document_01$change');

-- Address
l_retValue := p_TVersionProc$new ('Address', c_PC_create, 'p_Address_01$create');
l_retValue := p_TVersionProc$new ('Address', c_PC_retrieve, 'p_Address_01$retrieve');
l_retValue := p_TVersionProc$new ('Address', c_PC_delete, 'p_Address_01$delete');
l_retValue := p_TVersionProc$new ('Address', c_PC_change, 'p_Address_01$change');

-- Company
l_retValue := p_TVersionProc$new ('Company', c_PC_create, 'p_Company_01$create');
l_retValue := p_TVersionProc$new ('Company', c_PC_retrieve, 'p_Company_01$retrieve');
l_retValue := p_TVersionProc$new ('Company', c_PC_delete, 'p_Company_01$delete');
l_retValue := p_TVersionProc$new ('Company', c_PC_change, 'p_Company_01$change');

-- Person
l_retValue := p_TVersionProc$new ('Person', c_PC_create, 'p_Person_01$create');
l_retValue := p_TVersionProc$new ('Person', c_PC_retrieve, 'p_Person_01$retrieve');
l_retValue := p_TVersionProc$new ('Person', c_PC_delete, 'p_Person_01$delete');
l_retValue := p_TVersionProc$new ('Person', c_PC_change, 'p_Person_01$change');

/*
-- Register
l_retValue := p_TVersionProc$new ('Register', c_PC_create, 'p_Register_01$createObjects');
*/

-- Note
l_retValue := p_TVersionProc$new ('Note', c_PC_create, 'p_Note_01$create');
l_retValue := p_TVersionProc$new ('Note', c_PC_retrieve, 'p_Note_01$retrieve');
l_retValue := p_TVersionProc$new ('Note', c_PC_delete, 'p_Note_01$delete');
l_retValue := p_TVersionProc$new ('Note', c_PC_change, 'p_Note_01$change');

debug ('Created all m2 entries in table ibs_TVersionProc');

END;
/

COMMIT WORK;
/

EXIT;
