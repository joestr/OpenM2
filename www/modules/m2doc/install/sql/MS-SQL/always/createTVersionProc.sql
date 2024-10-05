/******************************************************************************
 * Create all entries of procedures in m2. <BR>
 *
 * @version     $Id: createTVersionProc.sql,v 1.15 2010/01/13 16:41:14 rburgermann Exp $
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


-- Catalog
EXEC p_TVersionProc$new N'Catalog', @c_PC_create, N'p_Catalog_01$create'
EXEC p_TVersionProc$new N'Catalog', @c_PC_retrieve, N'p_Catalog_01$retrieve'
EXEC p_TVersionProc$new N'Catalog', @c_PC_delete, N'p_Catalog_01$delete'
EXEC p_TVersionProc$new N'Catalog', @c_PC_change, N'p_Catalog_01$change'

-- Order
EXEC p_TVersionProc$new N'Order', @c_PC_create, N'p_Order_01$create'
EXEC p_TVersionProc$new N'Order', @c_PC_retrieve, N'p_Order_01$retrieve'
EXEC p_TVersionProc$new N'Order', @c_PC_delete, N'p_Order_01$delete'

-- Price
EXEC p_TVersionProc$new N'ProductSizeColor', @c_PC_create, N'p_Price_01$create'
EXEC p_TVersionProc$new N'ProductSizeColor', @c_PC_retrieve, N'p_Price_01$retrieve'
EXEC p_TVersionProc$new N'ProductSizeColor', @c_PC_delete, N'p_Price_01$delete'
EXEC p_TVersionProc$new N'ProductSizeColor', @c_PC_change, N'p_Price_01$change'
EXEC p_TVersionProc$new N'ProductSizeColor', @c_PC_copy, N'p_Price_01$BOCopy'

-- Product
EXEC p_TVersionProc$new N'Product', @c_PC_create, N'p_Product_01$create'
EXEC p_TVersionProc$new N'Product', @c_PC_retrieve, N'p_Product_01$retrieve'
EXEC p_TVersionProc$new N'Product', @c_PC_change, N'p_Product_01$change'

-- ProductBrand
EXEC p_TVersionProc$new N'ProductBrand', @c_PC_create, N'p_ProductBrand_01$create'
EXEC p_TVersionProc$new N'ProductBrand', @c_PC_retrieve, N'p_ProductBrand_01$retrieve'
EXEC p_TVersionProc$new N'ProductBrand', @c_PC_delete, N'p_ProductBrand_01$delete'
EXEC p_TVersionProc$new N'ProductBrand', @c_PC_change, N'p_ProductBrand_01$change'

-- ProductCollection
EXEC p_TVersionProc$new N'ProductCollection', @c_PC_create, N'p_ProductCollect_01$create'
EXEC p_TVersionProc$new N'ProductCollection', @c_PC_retrieve, N'p_ProductCollect_01$retrieve'
EXEC p_TVersionProc$new N'ProductCollection', @c_PC_delete, N'p_ProductCollect_01$delete'
EXEC p_TVersionProc$new N'ProductCollection', @c_PC_change, N'p_ProductCollect_01$change'
EXEC p_TVersionProc$new N'ProductCollection', @c_PC_createQty, N'p_ProductCollect_01$createQty'
EXEC p_TVersionProc$new N'ProductCollection', @c_PC_createVal, N'p_ProductCollect_01$createVal'

-- ProductGroup
EXEC p_TVersionProc$new N'ProductGroup', @c_PC_create, N'p_ProductGroup_01$create'
EXEC p_TVersionProc$new N'ProductGroup', @c_PC_retrieve, N'p_ProductGroup_01$retrieve'
EXEC p_TVersionProc$new N'ProductGroup', @c_PC_delete, N'p_ProductGroup_01$delete'
EXEC p_TVersionProc$new N'ProductGroup', @c_PC_change, N'p_ProductGroup_01$change'

-- ProductGrpProfile
EXEC p_TVersionProc$new N'ProductGroupProfile', @c_PC_create, N'p_ProductGrpProfile_01$create'
EXEC p_TVersionProc$new N'ProductGroupProfile', @c_PC_retrieve, N'p_ProductGrpProfile_01$retrieve'
EXEC p_TVersionProc$new N'ProductGroupProfile', @c_PC_delete, N'p_ProductGrpProfile_01$delete'
EXEC p_TVersionProc$new N'ProductGroupProfile', @c_PC_change, N'p_ProductGrpProfile_01$change'

-- ProductProfile
EXEC p_TVersionProc$new N'ProductProfile', @c_PC_create, N'p_ProductProfile_01$create'
EXEC p_TVersionProc$new N'ProductProfile', @c_PC_retrieve, N'p_ProductProfile_01$retrieve'
EXEC p_TVersionProc$new N'ProductProfile', @c_PC_delete, N'p_ProductProfile_01$delete'
EXEC p_TVersionProc$new N'ProductProfile', @c_PC_change, N'p_ProductProfile_01$change'

-- ProdProperties
EXEC p_TVersionProc$new N'Property', @c_PC_create, N'p_ProdProperties_01$create'
EXEC p_TVersionProc$new N'Property', @c_PC_retrieve, N'p_ProdProperties_01$retrieve'
EXEC p_TVersionProc$new N'Property', @c_PC_delete, N'p_ProdProperties_01$delete'
EXEC p_TVersionProc$new N'Property', @c_PC_change, N'p_ProdProperties_01$change'

-- PaymentType
EXEC p_TVersionProc$new N'PaymentType', @c_PC_create, N'p_PaymentType_01$create'
EXEC p_TVersionProc$new N'PaymentType', @c_PC_retrieve, N'p_PaymentType_01$retrieve'
EXEC p_TVersionProc$new N'PaymentType', @c_PC_delete, N'p_PaymentType_01$delete'
EXEC p_TVersionProc$new N'PaymentType', @c_PC_change, N'p_PaymentType_01$change'

-- Participant
EXEC p_TVersionProc$new N'Participant', @c_PC_create, N'p_Participant_01$create'
EXEC p_TVersionProc$new N'Participant', @c_PC_retrieve, N'p_Participant_01$retrieve'
EXEC p_TVersionProc$new N'Participant', @c_PC_delete, N'p_Participant_01$delete'
EXEC p_TVersionProc$new N'Participant', @c_PC_change, N'p_Participant_01$change'

-- Termin
EXEC p_TVersionProc$new N'Termin', @c_PC_create, N'p_Termin_01$create'
EXEC p_TVersionProc$new N'Termin', @c_PC_retrieve, N'p_Termin_01$retrieve'
EXEC p_TVersionProc$new N'Termin', @c_PC_delete, N'p_Termin_01$delete'
EXEC p_TVersionProc$new N'Termin', @c_PC_change, N'p_Termin_01$change'

-- Article
EXEC p_TVersionProc$new N'Article', @c_PC_create, N'p_Article_01$create'
EXEC p_TVersionProc$new N'Article', @c_PC_retrieve, N'p_Article_01$retrieve'
EXEC p_TVersionProc$new N'Article', @c_PC_delete, N'p_Article_01$delete'
EXEC p_TVersionProc$new N'Article', @c_PC_change, N'p_Article_01$change'
EXEC p_TVersionProc$new N'Article', @c_PC_changeState, N'p_Article_01$changeState'

-- BlackBoard
EXEC p_TVersionProc$new N'BlackBoard', @c_PC_create, N'p_BlackBoard_01$create'
EXEC p_TVersionProc$new N'BlackBoard', @c_PC_retrieve, N'p_Discussion_01$retrieve'
EXEC p_TVersionProc$new N'BlackBoard', @c_PC_delete, N'p_Discussion_01$delete'
EXEC p_TVersionProc$new N'BlackBoard', @c_PC_change, N'p_Discussion_01$change'

-- DiscXMLViewer
EXEC p_TVersionProc$new N'DiscXMLViewer', @c_PC_create, N'p_DiscXMLViewer_01$create'
EXEC p_TVersionProc$new N'DiscXMLViewer', @c_PC_retrieve, N'p_DiscXMLViewer_01$retrieve'
EXEC p_TVersionProc$new N'DiscXMLViewer', @c_PC_delete, N'p_DiscXMLViewer_01$delete'
EXEC p_TVersionProc$new N'DiscXMLViewer', @c_PC_change, N'p_DiscXMLViewer_01$change'

-- Discussion
EXEC p_TVersionProc$new N'Discussion', @c_PC_create, N'p_Discussion_01$create'
EXEC p_TVersionProc$new N'Discussion', @c_PC_retrieve, N'p_Discussion_01$retrieve'
EXEC p_TVersionProc$new N'Discussion', @c_PC_delete, N'p_Discussion_01$delete'
EXEC p_TVersionProc$new N'Discussion', @c_PC_change, N'p_Discussion_01$change'

-- DiscContainer
EXEC p_TVersionProc$new N'DiscussionContainer', @c_PC_create, N'p_DiscContainer_01$create'

-- Thread
EXEC p_TVersionProc$new N'Thread', @c_PC_create, N'p_Thread_01$create'
EXEC p_TVersionProc$new N'Thread', @c_PC_retrieve, N'p_Thread_01$retrieve'
EXEC p_TVersionProc$new N'Thread', @c_PC_delete, N'p_Thread_01$delete'
EXEC p_TVersionProc$new N'Thread', @c_PC_change, N'p_Thread_01$change'
EXEC p_TVersionProc$new N'Thread', @c_PC_changeState, N'p_Article_01$changeState'

-- XMLDiscussion
EXEC p_TVersionProc$new N'XMLDiscussion', @c_PC_create, N'p_Discussion_01$create'
EXEC p_TVersionProc$new N'XMLDiscussion', @c_PC_retrieve, N'p_XMLDiscussion_01$retrieve'
EXEC p_TVersionProc$new N'XMLDiscussion', @c_PC_delete, N'p_Discussion_01$delete'
EXEC p_TVersionProc$new N'XMLDiscussion', @c_PC_change, N'p_XMLDiscussion_01$change'

-- XMLDiscTemplate
EXEC p_TVersionProc$new N'XMLDiscussionTemplate', @c_PC_create, N'p_XMLDiscTemplate_01$create'
EXEC p_TVersionProc$new N'XMLDiscussionTemplate', @c_PC_retrieve, N'p_XMLDiscTemplate_01$retrieve'
EXEC p_TVersionProc$new N'XMLDiscussionTemplate', @c_PC_delete, N'p_XMLDiscTemplate_01$delete'
EXEC p_TVersionProc$new N'XMLDiscussionTemplate', @c_PC_change, N'p_XMLDiscTemplate_01$change'
EXEC p_TVersionProc$new N'XMLDiscussionTemplate', @c_PC_deleteRec, N'p_XMLDiscTemplate_01$delete'

-- Document
EXEC p_TVersionProc$new N'Document', @c_PC_create, N'p_Document_01$create'
EXEC p_TVersionProc$new N'Document', @c_PC_retrieve, N'p_Document_01$retrieve'
EXEC p_TVersionProc$new N'Document', @c_PC_change, N'p_Document_01$change'

-- Address
EXEC p_TVersionProc$new N'Address', @c_PC_create, N'p_Address_01$create'
EXEC p_TVersionProc$new N'Address', @c_PC_retrieve, N'p_Address_01$retrieve'
EXEC p_TVersionProc$new N'Address', @c_PC_delete, N'p_Address_01$delete'
EXEC p_TVersionProc$new N'Address', @c_PC_change, N'p_Address_01$change'

-- Company
EXEC p_TVersionProc$new N'Company', @c_PC_create, N'p_Company_01$create'
EXEC p_TVersionProc$new N'Company', @c_PC_retrieve, N'p_Company_01$retrieve'
EXEC p_TVersionProc$new N'Company', @c_PC_delete, N'p_Company_01$delete'
EXEC p_TVersionProc$new N'Company', @c_PC_change, N'p_Company_01$change'

-- Person
EXEC p_TVersionProc$new N'Person', @c_PC_create, N'p_Person_01$create'
EXEC p_TVersionProc$new N'Person', @c_PC_retrieve, N'p_Person_01$retrieve'
EXEC p_TVersionProc$new N'Person', @c_PC_delete, N'p_Person_01$delete'
EXEC p_TVersionProc$new N'Person', @c_PC_change, N'p_Person_01$change'

/*
-- Register
EXEC p_TVersionProc$new N'Register', @c_PC_create, N'p_Register_01$createObjects'
*/

-- Note
EXEC p_TVersionProc$new N'Note', @c_PC_create, N'p_Note_01$create'
EXEC p_TVersionProc$new N'Note', @c_PC_retrieve, N'p_Note_01$retrieve'
EXEC p_TVersionProc$new N'Note', @c_PC_delete, N'p_Note_01$delete'
EXEC p_TVersionProc$new N'Note', @c_PC_change, N'p_Note_01$change'

PRINT 'Created all entries in table ibs_TVersionProc'

GO

-- show count messages again:
SET NOCOUNT OFF
GO
