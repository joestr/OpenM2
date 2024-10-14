--------------------------------------------------------------------------------
-- Create all entries of procedures in m2. <BR>
--
-- @version     $Id: createTVersionProc.sql,v 1.8 2004/01/16 00:45:29 klaus Exp $
--
-- @author      Marcel Samek (MS)  020921
--------------------------------------------------------------------------------

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createTVersionProc ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';  
    
    -- Catalog
    CALL IBSDEV1.p_TVersionProc$new('Catalog', c_PC_create, 'p_Catalog_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Catalog', c_PC_retrieve, 'p_Catalog_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Catalog', c_PC_delete, 'p_Catalog_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Catalog', c_PC_change, 'p_Catalog_01$change');
  
    
    -- Order
    CALL IBSDEV1.p_TVersionProc$new('Order', c_PC_create, 'p_Order_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Order', c_PC_retrieve, 'p_Order_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Order', c_PC_delete, 'p_Order_01$delete');
  
    
    -- Price
    CALL IBSDEV1.p_TVersionProc$new('ProductSizeColor', c_PC_create, 'p_Price_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ProductSizeColor', c_PC_retrieve, 'p_Price_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ProductSizeColor', c_PC_delete, 'p_Price_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('ProductSizeColor', c_PC_change, 'p_Price_01$change');
    CALL IBSDEV1.p_TVersionProc$new('ProductSizeColor', c_PC_copy, 'p_Price_01$BOCopy');
  
    
    -- Product
    CALL IBSDEV1.p_TVersionProc$new('Product', c_PC_create, 'p_Product_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Product', c_PC_retrieve, 'p_Product_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Product', c_PC_change, 'p_Product_01$change');
  
    
    -- ProductBrand
    CALL IBSDEV1.p_TVersionProc$new('ProductBrand', c_PC_create, 'p_ProductBrand_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ProductBrand', c_PC_retrieve, 'p_ProductBrand_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ProductBrand', c_PC_delete, 'p_ProductBrand_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('ProductBrand', c_PC_change, 'p_ProductBrand_01$change');
  
    -- ProductCollection
    CALL IBSDEV1.p_TVersionProc$new('ProductCollection', c_PC_create, 'p_ProductCollect_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ProductCollection', c_PC_retrieve, 'p_ProductCollect_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ProductCollection', c_PC_delete, 'p_ProductCollect_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('ProductCollection', c_PC_change, 'p_ProductCollect_01$change');
    CALL IBSDEV1.p_TVersionProc$new('ProductCollection', c_PC_createQty, 'p_ProductCollect_01$createQty');
    CALL IBSDEV1.p_TVersionProc$new('ProductCollection', c_PC_createVal, 'p_ProductCollect_01$createVal');

    -- ProductGroup
    CALL IBSDEV1.p_TVersionProc$new('ProductGroup', c_PC_create, 'p_ProductGroup_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ProductGroup', c_PC_retrieve, 'p_ProductGroup_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ProductGroup', c_PC_delete, 'p_ProductGroup_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('ProductGroup', c_PC_change, 'p_ProductGroup_01$change');
  
    
    -- ProductGrpProfile
    CALL IBSDEV1.p_TVersionProc$new('ProductGroupProfile', c_PC_create, 'p_ProductGrpProfile_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ProductGroupProfile', c_PC_retrieve, 'p_ProductGrpProfile_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ProductGroupProfile', c_PC_delete, 'p_ProductGrpProfile_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('ProductGroupProfile', c_PC_change, 'p_ProductGrpProfile_01$change');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc2');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createTVersionProc2 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';  
    
    -- ProductProfile
    CALL IBSDEV1.p_TVersionProc$new('ProductProfile', c_PC_create, 'p_ProductProfile_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ProductProfile', c_PC_retrieve, 'p_ProductProfile_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ProductProfile', c_PC_delete, 'p_ProductProfile_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('ProductProfile', c_PC_change, 'p_ProductProfile_01$change');
  
    
    -- ProdProperties
    CALL IBSDEV1.p_TVersionProc$new('Property', c_PC_create, 'p_ProdProperties_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Property', c_PC_retrieve, 'p_ProdProperties_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Property', c_PC_delete, 'p_ProdProperties_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Property', c_PC_change, 'p_ProdProperties_01$change');
  
    -- PaymentType
    CALL IBSDEV1.p_TVersionProc$new('PaymentType', c_PC_create, 'p_PaymentType_01$create');
    CALL IBSDEV1.p_TVersionProc$new('PaymentType', c_PC_retrieve, 'p_PaymentType_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('PaymentType', c_PC_delete, 'p_PaymentType_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('PaymentType', c_PC_change, 'p_PaymentType_01$change');
  
    -- Participant
    CALL IBSDEV1.p_TVersionProc$new('Participant', c_PC_create, 'p_Participant_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Participant', c_PC_retrieve, 'p_Participant_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Participant', c_PC_delete, 'p_Participant_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Participant', c_PC_change, 'p_Participant_01$change');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc3');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createTVersionProc3 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';
  
    -- Termin
    CALL IBSDEV1.p_TVersionProc$new('Termin', c_PC_create, 'p_Termin_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Termin', c_PC_retrieve, 'p_Termin_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Termin', c_PC_delete, 'p_Termin_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Termin', c_PC_change, 'p_Termin_01$change');
  
    -- Article
    CALL IBSDEV1.p_TVersionProc$new('Article', c_PC_create, 'p_Article_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Article', c_PC_retrieve, 'p_Article_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Article', c_PC_delete, 'p_Article_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Article', c_PC_change, 'p_Article_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Article', c_PC_changeState, 'p_Article_01$changeState');
  
    
    -- BlackBoard
    CALL IBSDEV1.p_TVersionProc$new('BlackBoard', c_PC_create, 'p_BlackBoard_01$create');
    CALL IBSDEV1.p_TVersionProc$new('BlackBoard', c_PC_retrieve, 'p_Discussion_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('BlackBoard', c_PC_delete, 'p_Discussion_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('BlackBoard', c_PC_change, 'p_Discussion_01$change');
  
    -- DiscXMLViewer
    CALL IBSDEV1.p_TVersionProc$new('DiscXMLViewer', c_PC_create, 'p_DiscXMLViewer_01$create');
    CALL IBSDEV1.p_TVersionProc$new('DiscXMLViewer', c_PC_retrieve, 'p_DiscXMLViewer_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('DiscXMLViewer', c_PC_delete, 'p_DiscXMLViewer_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('DiscXMLViewer', c_PC_change, 'p_DiscXMLViewer_01$change');
  
    
    -- Discussion
    CALL IBSDEV1.p_TVersionProc$new('Discussion', c_PC_create, 'p_Discussion_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Discussion', c_PC_retrieve, 'p_Discussion_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Discussion', c_PC_delete, 'p_Discussion_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Discussion', c_PC_change, 'p_Discussion_01$change');
  
    
    -- DiscContainer
    CALL IBSDEV1.p_TVersionProc$new('DiscussionContainer', c_PC_create, 'p_DiscContainer_01$create');
  
    -- Thread
    CALL IBSDEV1.p_TVersionProc$new('Thread', c_PC_create, 'p_Thread_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Thread', c_PC_retrieve, 'p_Thread_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Thread', c_PC_delete, 'p_Thread_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Thread', c_PC_change, 'p_Thread_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Thread', c_PC_changeState, 'p_Article_01$changeState');

    -- XMLDiscussion
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussion', c_PC_create, 'p_Discussion_01$create');
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussion', c_PC_retrieve, 'p_XMLDiscussion_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussion', c_PC_delete, 'p_Discussion_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussion', c_PC_change, 'p_XMLDiscussion_01$change');
  
    
    -- XMLDiscTemplate
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussionTemplate', c_PC_create, 'p_XMLDiscTemplate_01$create');
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussionTemplate', c_PC_retrieve, 'p_XMLDiscTemplate_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussionTemplate', c_PC_delete, 'p_XMLDiscTemplate_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussionTemplate', c_PC_change, 'p_XMLDiscTemplate_01$change');
    CALL IBSDEV1.p_TVersionProc$new('XMLDiscussionTemplate', c_PC_deleteRec, 'p_XMLDiscTemplate_01$delete');
  
    -- Document
    CALL IBSDEV1.p_TVersionProc$new('Document', c_PC_create, 'p_Document_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Document', c_PC_retrieve, 'p_Document_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Document', c_PC_change, 'p_Document_01$change');
  
    -- Address
    CALL IBSDEV1.p_TVersionProc$new('Address', c_PC_create, 'p_Address_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Address', c_PC_retrieve, 'p_Address_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Address', c_PC_delete, 'p_Address_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Address', c_PC_change, 'p_Address_01$change');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc4');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createTVersionProc4 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';

    -- Company
    CALL IBSDEV1.p_TVersionProc$new('Company', c_PC_create, 'p_Company_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Company', c_PC_retrieve, 'p_Company_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Company', c_PC_delete, 'p_Company_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Company', c_PC_change, 'p_Company_01$change');

    -- Person
    CALL IBSDEV1.p_TVersionProc$new('Person', c_PC_create, 'p_Person_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Person', c_PC_retrieve, 'p_Person_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Person', c_PC_delete, 'p_Person_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Person', c_PC_change, 'p_Person_01$change');
  
    -- Note
    CALL IBSDEV1.p_TVersionProc$new('Note', c_PC_create, 'p_Note_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Note', c_PC_retrieve, 'p_Note_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Note', c_PC_delete, 'p_Note_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Note', c_PC_change, 'p_Note_01$change');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc5');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createTVersionProc5 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_delete = 'delete';

END;

-- execute procedures:
CALL IBSDEV1.pim2_createTVersionProc;
CALL IBSDEV1.pim2_createTVersionProc2;
CALL IBSDEV1.pim2_createTVersionProc3;
CALL IBSDEV1.pim2_createTVersionProc4;
CALL IBSDEV1.pim2_createTVersionProc5;

-- delete procedures:
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc');
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc2');
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc3');
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc4');
CALL IBSDEV1.p_dropProc ('pim2_createTVersionProc5');
