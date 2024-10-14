--------------------------------------------------------------------------------
-- Create all business object types within m2. <BR>
--
-- @version     $Id: createBaseObjectTypes.sql,v 1.9 2004/01/06 23:53:38 klaus Exp $
--
-- @author      Marcel Samek (MS)  020921
----------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pim2_createBaseObjTypes ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_languageId INT;
    DECLARE c_id_Article INT;                                               
    DECLARE c_id_BlackBoard INT;                                            
    DECLARE c_id_Catalog INT;                                               
    DECLARE c_id_CatalogContainer INT;                                      
    DECLARE c_id_Company INT;                                               
    DECLARE c_id_Discussion INT;                                            
    DECLARE c_id_DiscussionContainer INT;                                   
    DECLARE c_id_DiscXMLViewer INT;                                         
    DECLARE c_id_DocumentContainer INT;                                     
    DECLARE c_id_Document INT;                                              
    DECLARE c_id_MasterDataContainer INT;                                   
    DECLARE c_id_Order INT;                                                 
    DECLARE c_id_OrderContainer INT;                                        
    DECLARE c_id_OverlapContainer INT;                                      
    DECLARE c_id_Participant INT;                                           
    DECLARE c_id_ParticipantContainer INT;                                  
    DECLARE c_id_PaymentType INT;                                           
    DECLARE c_id_PaymentTypeContainer INT;                                  
    DECLARE c_id_Person INT;                                                
    DECLARE c_id_PersonContainer INT;                                       
    DECLARE c_id_PersonSearchContainer INT;                                 
    DECLARE c_id_PersonUserContainer INT;                                   
    DECLARE c_id_Product INT;                                               
    DECLARE c_id_ProductBrand INT;                                          
    DECLARE c_id_ProductCollection INT;                                     
    DECLARE c_id_ProductCollectionContainer INT;                            
    DECLARE c_id_ProductGroup INT;                                          
    DECLARE c_id_ProductGroupContainer INT;                                 
    DECLARE c_id_ProductGroupProfile INT;                                   
    DECLARE c_id_ProductProfile INT;                                        
    DECLARE c_id_ProductProfileContainerStore INT;                               
    DECLARE c_id_ProductProfileContainer INT;                               
    DECLARE c_id_ProductProperties INT;                                     
    DECLARE c_id_ProductPropertiesContainer INT;                            
    DECLARE c_id_ProductSizeColor INT;                                      
    DECLARE c_id_ProductSizeColorContainer INT;                             
    DECLARE c_id_PropertyCategory INT;                                      
    DECLARE c_id_PropertyCategoryContainer INT;                             
    DECLARE c_id_SelectCompanyContainer INT;                                
    DECLARE c_id_SelectUserContainer INT;                                   
    DECLARE c_id_ShoppingCart INT;                                          
    DECLARE c_id_ShoppingCartLine INT;                                      
    DECLARE c_id_Termin INT;                                                
    DECLARE c_id_Terminplan INT;                                            
    DECLARE c_id_TerminplanContainer INT;                                   
    DECLARE c_id_Thread INT;                                                 
    DECLARE c_id_XMLDiscussion INT;                                         
    DECLARE c_id_XMLDiscussionTemplate INT;                                 
    DECLARE c_id_XMLDiscussionTemplateContainer INT;                        

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
  
    -- the current language
    -- local variables:
    -- initializations:
    SET c_languageId = 0;


    -- documents:
    -- Document
    SET c_id_Document = IBSDEV1.p_hexStringToInt('01010100');
    CALL IBSDEV1.p_Type$newLang (c_id_Document, 'BusinessObject', 0, 1,
                          1, 0, 1, 'Document',
                          'm2.doc.Document_01', c_languageId, 'TN_Document_01');
  
    -- diary:
    -- Termin
    SET c_id_Termin = IBSDEV1.p_hexStringToInt('01010200');
    CALL IBSDEV1.p_Type$newLang (c_id_Termin, 'BusinessObject', 0, 1,
                          1, 0, 1, 'Termin',
                          'm2.diary.Termin_01', c_languageId, 'TN_Termin_01');
  
    -- discussions:
    -- Discussion
    SET c_id_Discussion = IBSDEV1.p_hexStringToInt('01010300');
    CALL IBSDEV1.p_Type$newLang (c_id_Discussion, 'Container', 1, 1,
                          1, 0, 0, 'Discussion',
                          'm2.bbd.Discussion_01', c_languageId, 'TN_Discussion_01');
  
    -- Thread
    SET c_id_Thread = IBSDEV1.p_hexStringToInt('01010400');
    CALL IBSDEV1.p_Type$newLang (c_id_Thread, 'Container', 1, 1,
                          1, 0, 1, 'Thread',
                          'm2.bbd.Thread_01', c_languageId, 'TN_Thread_01');
  
    -- Article
    SET c_id_Article = IBSDEV1.p_hexStringToInt('01010500');
    CALL IBSDEV1.p_Type$newLang (c_id_Article, 'Container', 1, 1,
                          1, 0, 1, 'Article',
                          'm2.bbd.Article_01', c_languageId, 'TN_Article_01');
  
    -- XMLDiscussion
    SET c_id_XMLDiscussion = IBSDEV1.p_hexStringToInt('01010320');
    CALL IBSDEV1.p_Type$newLang (c_id_XMLDiscussion, 'Discussion', 1, 1,
                          1, 0, 1, 'XMLDiscussion',
                          'm2.bbd.XMLDiscussion_01', c_languageId, 'TN_XMLDiscussion_01');
  
    -- XMLDiscussionTemplate
    SET c_id_XMLDiscussionTemplate = IBSDEV1.p_hexStringToInt('01010310');
    CALL IBSDEV1.p_Type$newLang (c_id_XMLDiscussionTemplate, 'BusinessObject', 1, 1,
                          1, 0, 1, 'XMLDiscussionTemplate',
                          'm2.bbd.XMLDiscussionTemplate_01', c_languageId, 'TN_XMLDiscussionTemplate_01');
  
    -- diary:
    -- TerminplanContainer
    SET c_id_TerminplanContainer = IBSDEV1.p_hexStringToInt('01010600');
    CALL IBSDEV1.p_Type$newLang (c_id_TerminplanContainer, 'Container', 1, 1,
                          0, 1, 0, 'TerminplanContainer',
                          'm2.diary.TerminplanContainer_01', c_languageId, 'TN_TerminplanContainer_01');
  
    -- Terminplan
    SET c_id_Terminplan = IBSDEV1.p_hexStringToInt('01010700');
    CALL IBSDEV1.p_Type$newLang (c_id_Terminplan, 'Container', 1, 1,
                          0, 1, 0, 'Terminplan',
                          'm2.diary.Terminplan_01', c_languageId, 'TN_Terminplan_01');
  
    -- discussions:
    -- DiscussionContainer
    SET c_id_DiscussionContainer = IBSDEV1.p_hexStringToInt('01010900');
    CALL IBSDEV1.p_Type$newLang (c_id_DiscussionContainer, 'Container', 1, 1,
                          0, 1, 0, 'DiscussionContainer',
                          'm2.bbd.DiscussionContainer_01', c_languageId, 'TN_DiscussionContainer_01');
  
    -- BlackBoard
    SET c_id_BlackBoard = IBSDEV1.p_hexStringToInt('01010A00');
    CALL IBSDEV1.p_Type$newLang (c_id_BlackBoard, 'Container', 1, 1,
                          1, 0, 1, 'BlackBoard',
                          'm2.bbd.BlackBoard_01', c_languageId, 'TN_BlackBoard_01');

    -- store:
    -- CatalogContainer
    SET c_id_CatalogContainer = IBSDEV1.p_hexStringToInt('01010B00');
    CALL IBSDEV1.p_Type$newLang (c_id_CatalogContainer, 'Container', 1, 1,
                          0, 1, 0, 'Store',
                          'm2.store.CatalogContainer_01', c_languageId, 'TN_CatalogContainer_01');
  
    -- Catalog
    SET c_id_Catalog = IBSDEV1.p_hexStringToInt('01010C00');
    CALL IBSDEV1.p_Type$newLang (c_id_Catalog, 'Container', 1, 1,
                          1, 1, 1, 'Catalog',
                          'm2.store.Catalog_01', c_languageId, 'TN_Catalog_01');
  
    -- ProductGroupContainer  ??? (ProductGroupProfileContainer)
    SET c_id_ProductGroupContainer = IBSDEV1.p_hexStringToInt('01010D00');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductGroupContainer, 'Container', 1, 1,
                          0, 1, 0, 'ProductGroupContainer',
                          'm2.store.ProductGroupProfileContainer_01', c_languageId, 'TN_ProductGroupProfileContainer_01');
  
    -- ProductGroupProfile
    SET c_id_ProductGroupProfile = IBSDEV1.p_hexStringToInt('01010E00');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductGroupProfile, 'Container', 1, 1,
                          1, 0, 1, 'ProductGroupProfile',
                          'm2.store.ProductGroupProfile_01', c_languageId, 'TN_ProductGroupProfile_01');
  
    -- OrderContainer
    SET c_id_OrderContainer = IBSDEV1.p_hexStringToInt('01011200');
    CALL IBSDEV1.p_Type$newLang (c_id_OrderContainer, 'Container', 1, 1,
                          0, 1, 0, 'OrderContainer',
                          'm2.store.OrderContainer_01', c_languageId, 'TN_OrderContainer_01');
  
    -- Order
    SET c_id_Order = IBSDEV1.p_hexStringToInt('01011300');
    CALL IBSDEV1.p_Type$newLang (c_id_Order, 'BusinessObject', 0, 1,
                          0, 0, 0, 'Order',
                          'm2.store.Order_01', c_languageId, 'TN_Order_01');
  
    -- ShoppingCart
    SET c_id_ShoppingCart = IBSDEV1.p_hexStringToInt('01011400');
    CALL IBSDEV1.p_Type$newLang (c_id_ShoppingCart, 'Container', 1, 1,
                          0, 1, 0, 'ShoppingCart',
                          'm2.store.ShoppingCart_01', c_languageId, 'TN_ShoppingCart_01');
  
    -- Product
    SET c_id_Product = IBSDEV1.p_hexStringToInt('01011500');
    CALL IBSDEV1.p_Type$newLang (c_id_Product, 'BusinessObject', 0, 1,
                          1, 0, 1, 'Product',
                          'm2.store.Product_01', c_languageId, 'TN_Product_01');
  
    -- ShoppingCartLine
    SET c_id_ShoppingCartLine = IBSDEV1.p_hexStringToInt('01011900');
    CALL IBSDEV1.p_Type$newLang (c_id_ShoppingCartLine, 'BusinessObject', 0, 1,
                          0, 0, 0, 'ShoppingCartLine',
                          'm2.store.ShoppingCartLine_01', c_languageId, 'TN_ShoppingCartLine_01');
  
    -- Diary:
    -- OverlapContainer
    SET c_id_OverlapContainer = IBSDEV1.p_hexStringToInt('01011A00');
    CALL IBSDEV1.p_Type$newLang (c_id_OverlapContainer, 'Container', 1, 1,
                          0, 0, 0, 'OverlapContainer',
                          'm2.diary.OverlapContainer_01', c_languageId, 'TN_OverlapContainer_01');
END;


-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes2');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createBaseObjTypes2 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_languageId INT;
    DECLARE c_id_Article INT;                                               
    DECLARE c_id_BlackBoard INT;                                            
    DECLARE c_id_Catalog INT;                                               
    DECLARE c_id_CatalogContainer INT;                                      
    DECLARE c_id_Company INT;                                               
    DECLARE c_id_Discussion INT;                                            
    DECLARE c_id_DiscussionContainer INT;                                   
    DECLARE c_id_DiscXMLViewer INT;                                         
    DECLARE c_id_DocumentContainer INT;                                     
    DECLARE c_id_Document INT;                                              
    DECLARE c_id_MasterDataContainer INT;                                   
    DECLARE c_id_Order INT;                                                 
    DECLARE c_id_OrderContainer INT;                                        
    DECLARE c_id_OverlapContainer INT;                                      
    DECLARE c_id_Participant INT;                                           
    DECLARE c_id_ParticipantContainer INT;                                  
    DECLARE c_id_PaymentType INT;                                           
    DECLARE c_id_PaymentTypeContainer INT;                                  
    DECLARE c_id_Person INT;                                                
    DECLARE c_id_PersonContainer INT;                                       
    DECLARE c_id_PersonSearchContainer INT;                                 
    DECLARE c_id_PersonUserContainer INT;                                   
    DECLARE c_id_Product INT;                                               
    DECLARE c_id_ProductBrand INT;                                          
    DECLARE c_id_ProductCollection INT;                                     
    DECLARE c_id_ProductCollectionContainer INT;                            
    DECLARE c_id_ProductGroup INT;                                          
    DECLARE c_id_ProductGroupContainer INT;                                 
    DECLARE c_id_ProductGroupProfile INT;                                   
    DECLARE c_id_ProductProfile INT;                                        
    DECLARE c_id_ProductProfileContainerStore INT;                               
    DECLARE c_id_ProductProfileContainer INT;                               
    DECLARE c_id_ProductProperties INT;                                     
    DECLARE c_id_ProductPropertiesContainer INT;                            
    DECLARE c_id_ProductSizeColor INT;                                      
    DECLARE c_id_ProductSizeColorContainer INT;                             
    DECLARE c_id_PropertyCategory INT;                                      
    DECLARE c_id_PropertyCategoryContainer INT;                             
    DECLARE c_id_SelectCompanyContainer INT;                                
    DECLARE c_id_SelectUserContainer INT;                                   
    DECLARE c_id_ShoppingCart INT;                                          
    DECLARE c_id_ShoppingCartLine INT;                                      
    DECLARE c_id_Termin INT;                                                
    DECLARE c_id_Terminplan INT;                                            
    DECLARE c_id_TerminplanContainer INT;                                   
    DECLARE c_id_Thread INT;                                                 
    DECLARE c_id_XMLDiscussion INT;                                         
    DECLARE c_id_XMLDiscussionTemplate INT;                                 
    DECLARE c_id_XMLDiscussionTemplateContainer INT;                        
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
  
    -- the current language
    -- local variables:
    -- initializations:
    SET c_languageId = 0;


    -- store:
    -- ProductGroup
    SET c_id_ProductGroup = IBSDEV1.p_hexStringToInt('01011F00');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductGroup, 'Container', 1, 1,
                          1, 1, 1, 'ProductGroup',
                          'm2.store.ProductGroup_01', c_languageId, 'TN_ProductGroup_01');
  
    -- diary:
    -- ParticipantContainer
    SET c_id_ParticipantContainer = IBSDEV1.p_hexStringToInt('01012000');
    CALL IBSDEV1.p_Type$newLang (c_id_ParticipantContainer, 'Container', 1, 1,
                          0, 0, 0, 'ParticipantContainer',
                          'm2.diary.ParticipantContainer_01', c_languageId, 'TN_ParticipantContainer_01');
  
    -- store:
    -- ProductSizeColorContainer (PriceContainer)
    SET c_id_ProductSizeColorContainer = IBSDEV1.p_hexStringToInt('01012100');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductSizeColorContainer, 'Container', 1, 1,
                          0, 0, 0, 'ProductSizeColorContainer',
                          'm2.store.PriceContainer_01', c_languageId, 'TN_ProductSizeColorContainer_01');
  
    -- ProductSizeColor (Price)
    SET c_id_ProductSizeColor = IBSDEV1.p_hexStringToInt('01012200');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductSizeColor, 'BusinessObject', 0, 1,
                          0, 0, 0, 'ProductSizeColor',
                          'm2.store.Price_01', c_languageId, 'TN_ProductSizeColor_01');
  
    -- master data:
    -- MasterDataContainer
    SET c_id_MasterDataContainer = IBSDEV1.p_hexStringToInt('01012900');
    CALL IBSDEV1.p_Type$newLang (c_id_MasterDataContainer, 'Container', 1, 1,
                          0, 1, 0, 'MasterDataContainer',
                          'm2.mad.MasterDataContainer_01', c_languageId, 'TN_MasterDataContainer_01');
  
    -- Person
    SET c_id_Person = IBSDEV1.p_hexStringToInt('01012A00');
    CALL IBSDEV1.p_Type$newLang (c_id_Person, 'BusinessObject', 0, 1,
                          1, 0, 1, 'Person',
                          'm2.mad.Person_01', c_languageId, 'TN_Person_01');
  
    -- PersonContainer
    SET c_id_PersonContainer = IBSDEV1.p_hexStringToInt('01012B00');
    CALL IBSDEV1.p_Type$newLang (c_id_PersonContainer, 'Container', 1, 1,
                          0, 0, 0, 'PersonContainer',
                          'm2.mad.PersonContainer_01', c_languageId, 'TN_PersonContainer_01');
  
    -- Company
    SET c_id_Company = IBSDEV1.p_hexStringToInt('01012C00');
    CALL IBSDEV1.p_Type$newLang (c_id_Company, 'BusinessObject', 0, 1,
                          1, 0, 1, 'Company',
                          'm2.mad.Company_01', c_languageId, 'TN_Company_01');
  
    -- diary:
    -- Participant
    SET c_id_Participant = IBSDEV1.p_hexStringToInt('01012E00');
    CALL IBSDEV1.p_Type$newLang (c_id_Participant, 'BusinessObject', 0, 1,
                          1, 0, 0, 'Participant',
                          'm2.diary.Participant_01', c_languageId, 'TN_Participant_01');
  
    -- Document Management:
    -- DocumentContainer
    SET c_id_DocumentContainer = IBSDEV1.p_hexStringToInt('01014B00');
    CALL IBSDEV1.p_Type$newLang (c_id_DocumentContainer, 'Container', 1, 1,
                          1, 1, 1, 'DocumentContainer',
                          'm2.doc.DocumentContainer_01', c_languageId, 'TN_DocumentContainer_01');
  
    -- Store:
    -- Property ??? (ProductProperties)
    SET c_id_ProductProperties = IBSDEV1.p_hexStringToInt('01015A00');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductProperties, 'BusinessObject', 0, 1,
                          0, 0, 0, 'Property',
                          'm2.store.ProductProperties_01', c_languageId, 'TN_ProductProperties_01');
  
    -- ProductPropertiesContainer
    SET c_id_ProductPropertiesContainer = IBSDEV1.p_hexStringToInt('01015B00');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductPropertiesContainer, 'Container', 1, 1,
                          0, 1, 0, 'PropertyContainer',
                          'm2.store.ProductPropertiesContainer_01', c_languageId, 'TN_ProductPropertiesContainer_01');

    -- master data:
    -- PersonUserContainer
    SET c_id_PersonUserContainer = IBSDEV1.p_hexStringToInt('01015E00');
    CALL IBSDEV1.p_Type$newLang (c_id_PersonUserContainer, 'Container', 1, 1,
                          0, 0, 0, 'PersonUserContainer',
                          'm2.mad.PersonUserContainer_01', c_languageId, 'TN_PersonUserContainer_01');
  
    -- store:
    -- PropertyCategory
    SET c_id_PropertyCategory = IBSDEV1.p_hexStringToInt('01015F00');
    CALL IBSDEV1.p_Type$newLang (c_id_PropertyCategory, 'BusinessObject', 0, 1,
                          0, 0, 0, 'PropertyCategory',
                          'm2.store.PropertyCategory_01', c_languageId, 'TN_PropertyCategory_01');
  
    -- PropertyCategoryContainer
    SET c_id_PropertyCategoryContainer = IBSDEV1.p_hexStringToInt('01016000');
    CALL IBSDEV1.p_Type$newLang (c_id_PropertyCategoryContainer, 'Container', 1, 1,
                          0, 1, 0, 'PropertyCategoryContainer',
                          'm2.store.PropertyCategoryContainer_01', c_languageId, 'TN_PropertyCategoryContainer_01');
  
    -- store:
    -- ProductProfile
    SET c_id_ProductProfile = IBSDEV1.p_hexStringToInt('01016C00');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductProfile, 'BusinessObject', 0, 1,
                          0, 0, 0, 'ProductProfile',
                          'm2.store.ProductProfile_01', c_languageId, 'TN_ProductProfile_01');
  
    -- ProductProfileContainer
    SET c_id_ProductProfileContainerStore = IBSDEV1.p_hexStringToInt('01016D00');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductProfileContainer, 'Container', 1, 1,
                          0, 1, 0, 'ProductProfileContainer',
                          'm2.store.ProductProfileContainer_01', c_languageId, 'TN_ProductProfileContainer_01');
  
    -- PaymentType
    SET c_id_PaymentType = IBSDEV1.p_hexStringToInt('01016C10');
    CALL IBSDEV1.p_Type$newLang (c_id_PaymentType, 'BusinessObject', 0, 1,
                          0, 0, 0, 'PaymentType',
                          'm2.store.PaymentType_01', c_languageId, 'TN_PaymentType_01');
  
    -- PaymentTypeContainer
    SET c_id_PaymentTypeContainer = IBSDEV1.p_hexStringToInt('01016D10');
    CALL IBSDEV1.p_Type$newLang (c_id_PaymentTypeContainer, 'Container', 1, 1,
                          0, 1, 0, 'PaymentTypeContainer',
                          'm2.store.PaymentTypeContainer_01', c_languageId, 'TN_PaymentTypeContainer_01');
  
    -- ProductBrand
    SET c_id_ProductBrand = IBSDEV1.p_hexStringToInt('01017100');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductBrand, 'BusinessObject', 0, 1,
                          0, 0, 0, 'ProductBrand',
                          'm2.store.ProductBrand_01', c_languageId, 'TN_ProductBrand_01');

    -- ProductProfileContainer
    SET c_id_ProductProfileContainer = IBSDEV1.p_hexStringToInt('01017200');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductProfileContainer, 'Container', 1, 1,
                          0, 1, 0, 'ProductBrandContainer',
                          'm2.store.ProductBrandContainer_01', c_languageId, 'TN_ProductBrandContainer_01');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes3');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createBaseObjTypes3 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_languageId INT;
    DECLARE c_id_Article INT;                                               
    DECLARE c_id_BlackBoard INT;                                            
    DECLARE c_id_Catalog INT;                                               
    DECLARE c_id_CatalogContainer INT;                                      
    DECLARE c_id_Company INT;                                               
    DECLARE c_id_Discussion INT;                                            
    DECLARE c_id_DiscussionContainer INT;                                   
    DECLARE c_id_DiscXMLViewer INT;                                         
    DECLARE c_id_DocumentContainer INT;                                     
    DECLARE c_id_Document INT;                                              
    DECLARE c_id_MasterDataContainer INT;                                   
    DECLARE c_id_Order INT;                                                 
    DECLARE c_id_OrderContainer INT;                                        
    DECLARE c_id_OverlapContainer INT;                                      
    DECLARE c_id_Participant INT;                                           
    DECLARE c_id_ParticipantContainer INT;                                  
    DECLARE c_id_PaymentType INT;                                           
    DECLARE c_id_PaymentTypeContainer INT;                                  
    DECLARE c_id_Person INT;                                                
    DECLARE c_id_PersonContainer INT;                                       
    DECLARE c_id_PersonSearchContainer INT;                                 
    DECLARE c_id_PersonUserContainer INT;                                   
    DECLARE c_id_Product INT;                                               
    DECLARE c_id_ProductBrand INT;                                          
    DECLARE c_id_ProductCollection INT;                                     
    DECLARE c_id_ProductCollectionContainer INT;                            
    DECLARE c_id_ProductGroup INT;                                          
    DECLARE c_id_ProductGroupContainer INT;                                 
    DECLARE c_id_ProductGroupProfile INT;                                   
    DECLARE c_id_ProductProfile INT;                                        
    DECLARE c_id_ProductProfileContainerStore INT;                               
    DECLARE c_id_ProductProfileContainer INT;                               
    DECLARE c_id_ProductProperties INT;                                     
    DECLARE c_id_ProductPropertiesContainer INT;                            
    DECLARE c_id_ProductSizeColor INT;                                      
    DECLARE c_id_ProductSizeColorContainer INT;                             
    DECLARE c_id_PropertyCategory INT;                                      
    DECLARE c_id_PropertyCategoryContainer INT;                             
    DECLARE c_id_SelectCompanyContainer INT;                                
    DECLARE c_id_SelectUserContainer INT;                                   
    DECLARE c_id_ShoppingCart INT;                                          
    DECLARE c_id_ShoppingCartLine INT;                                      
    DECLARE c_id_Termin INT;                                                
    DECLARE c_id_Terminplan INT;                                            
    DECLARE c_id_TerminplanContainer INT;                                   
    DECLARE c_id_Thread INT;                                                 
    DECLARE c_id_XMLDiscussion INT;                                         
    DECLARE c_id_XMLDiscussionTemplate INT;                                 
    DECLARE c_id_XMLDiscussionTemplateContainer INT;                        
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
  
    -- the current language
    -- local variables:
    -- initializations:
    SET c_languageId = 0;  


    -- DiscXMLViewer
    SET c_id_DiscXMLViewer = IBSDEV1.p_hexStringToInt('01017510');
    CALL IBSDEV1.p_Type$newLang (c_id_DiscXMLViewer, 'XMLViewer', 1,
                          1, 1, 0,
                          1, 'DiscXMLViewer', 'm2.bbd.DiscXMLViewer_01',
                          c_languageId, 'TN_DiscXMLViewer_01');
  
    -- Store:
    -- ProductCollection
    SET c_id_ProductCollection = IBSDEV1.p_hexStringToInt('01017600');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductCollection, 'BusinessObject', 0,
                          1, 0, 0,
                          0, 'ProductCollection', 'm2.store.ProductCollection_01',
                          c_languageId, 'TN_ProductCollection_01');
  
    -- ProductCollectionContainer
    SET c_id_ProductCollectionContainer = IBSDEV1.p_hexStringToInt('01017700');
    CALL IBSDEV1.p_Type$newLang (c_id_ProductCollectionContainer, 'Container', 1,
                          1, 0, 1,
                          0, 'ProductCollectionContainer', 'm2.store.ProductCollectionContainer_01',
                          c_languageId, 'TN_ProductCollectionContainer_01');
  
    -- SelectUserContainer
    SET c_id_SelectUserContainer = IBSDEV1.p_hexStringToInt('01017800');
    CALL IBSDEV1.p_Type$newLang (c_id_SelectUserContainer, 'Container', 1,
                          1, 0, 0,
                          0, 'SelectUserContainer', 'm2.store.SelectUserContainer_01',
                          c_languageId, 'TN_SelectUserContainer_01');
  
    -- SelectCompanyContainer
    SET c_id_SelectCompanyContainer = IBSDEV1.p_hexStringToInt('01017B00');
    CALL IBSDEV1.p_Type$newLang (c_id_SelectCompanyContainer, 'Container', 1,
                          1, 0, 0,
                          0, 'SelectCompanyContainer', 'm2.store.SelectCompanyContainer_01',
                          c_languageId, 'TN_SelectCompanyContainer_01');
  
    -- XMLDiscussionTemplateContainer
    SET c_id_XMLDiscussionTemplateContainer = IBSDEV1.p_hexStringToInt('01017d10');
    CALL IBSDEV1.p_Type$newLang (c_id_XMLDiscussionTemplateContainer, 'DocumentTemplateContainer', 1,
                          1, 1, 0,
                          0, 'XMLDiscussionTemplateContainer', 'm2.bbd.XMLDiscussionTemplateContainer_01',
                          c_languageId, 'TN_XMLDiscussionTemplateContainer_01');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes4');
    -- register all predefined tabs:
    -- EXEC @l_retValue = p_Tab$new domainId, code, kind, 
    -- tVersionId, fct, priority,multilangKey, rights, @l_tabId OUTPUT
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createBaseObjTypes4 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;    -- everything was o.k.
    DECLARE c_languageId    INT;    -- the current language
    DECLARE c_OP_READ       INT;    -- operation for reading
    DECLARE c_TK_VIEW       INT;
    DECLARE c_TK_OBJECT     INT;
    DECLARE c_TK_LINK       INT;
    DECLARE c_TK_FUNCTION   INT;
    
    DECLARE c_id_binary1    INT;    
    DECLARE c_id_binary2    INT;
    DECLARE c_id_binary3    INT;    
    DECLARE c_id_binary4    INT;
    DECLARE c_id_binary5    INT;    
    DECLARE c_id_binary6    INT;
    DECLARE c_id_binary7    INT;    
    DECLARE c_id_binary8    INT;
    DECLARE c_id_binary9    INT;    
    DECLARE c_id_binary10   INT;
    DECLARE c_id_binary11   INT;    
    DECLARE c_id_binary12   INT;
    DECLARE c_id_binary13   INT;    
    DECLARE c_id_binary14   INT;
    DECLARE c_id_binary15   INT;    
    DECLARE c_id_binary16   INT;
    DECLARE c_id_binary17   INT;    
    DECLARE c_id_binary18   INT;
    DECLARE c_id_binary19   INT;    
    DECLARE c_id_binary20   INT;
    DECLARE c_id_binary21   INT;    
    DECLARE c_id_binary22   INT;
    DECLARE c_id_binary23   INT;    
    DECLARE c_id_binary24   INT;
    
    -- local variables:
    DECLARE l_retValue INT;-- return value of a function
    DECLARE l_tabId INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- id of actual tab
    -- assign constants:
        SET c_ALL_RIGHT = 1;
        SET c_languageId = 0;
        SET c_OP_READ = 4;
        SET c_TK_VIEW = 1;
        SET c_TK_OBJECT = 2;
        SET c_TK_LINK = 3;
        SET c_TK_FUNCTION = 4;
    
    -- initialize local variables:
        SET l_retValue = c_ALL_RIGHT;
        SET l_tabId = 0;

    CALL IBSDEV1.p_Tab$new(0, 'Month', c_TK_VIEW, 0,
                     3002, 9105, 'OD_tabMonth', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    CALL IBSDEV1.p_Tab$new(0, 'Day', c_TK_VIEW, 0,
                     3001, 9100, 'OD_tabDay', 4194304,
                     'm2.diary.Terminplan_01', l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary1 = IBSDEV1.p_hexStringToInt('01012F01');
    CALL IBSDEV1.p_Tab$new(0, 'Address', c_TK_OBJECT, c_id_binary1,
                     51, 0, 'OD_tabAddress', 4,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary3 = IBSDEV1.p_hexStringToInt('01017701');
    CALL IBSDEV1.p_Tab$new(0, 'Assortments', c_TK_OBJECT, c_id_binary3,
                     51, 0, 'OD_tabAssortments', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary7 = IBSDEV1.p_hexStringToInt('01016601');
    CALL IBSDEV1.p_Tab$new(0, 'Branches', c_TK_OBJECT, c_id_binary7,
                     51, 0, 'OD_tabBranches', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary9 = IBSDEV1.p_hexStringToInt('01012B01');
    CALL IBSDEV1.p_Tab$new(0, 'Contacts', c_TK_OBJECT, c_id_binary9,
                     51, 0, 'OD_tabContacts', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary15 = IBSDEV1.p_hexStringToInt('01015E01');
    CALL IBSDEV1.p_Tab$new(0, 'PersonUsers', c_TK_OBJECT, c_id_binary15,
                     51, 0, 'OD_tabPersonUsers', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary16 = IBSDEV1.p_hexStringToInt('01012101');
    CALL IBSDEV1.p_Tab$new(0, 'Prices', c_TK_OBJECT, c_id_binary16,
                     51, 0, 'OD_tabPrices', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary18 = IBSDEV1.p_hexStringToInt('01015B01');
    CALL IBSDEV1.p_Tab$new(0, 'Properties', c_TK_OBJECT, c_id_binary18,
                     51, 0, 'OD_tabProperties', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary23 = IBSDEV1.p_hexStringToInt('01014F01');
    CALL IBSDEV1.p_Tab$new(0, 'Versions', c_TK_OBJECT, c_id_binary23,
                     51, 0, 'OD_tabVersions', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary24 = IBSDEV1.p_hexStringToInt('01012001');
    CALL IBSDEV1.p_Tab$new(0, 'Participants', c_TK_OBJECT, c_id_binary24,
                     51, 0, 'OD_tabParticipants', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_Tab$new(0, 'ShoppingCart', c_TK_FUNCTION, 0,
                     9001, -3000, 'OD_tabShoppingCart', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes5');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createBaseObjTypes5 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- set the tabs for the object types:
-- body:
    -- documents:
    -- Document
    CALL IBSDEV1.p_Type$addTabs('Document', '', 'Info', 'Attachments', 'References', 'Rights', 'Protocol', '', '', '', '', '');
    
    -- diary:
    -- Termin
    CALL IBSDEV1.p_Type$addTabs('Termin', '', 'Info', 'Contacts',
                            'References', 'Rights', 'Protocol', 'Attachments', 'Participants', '', '', '');
    
    -- discussions:
    -- Discussion
    CALL IBSDEV1.p_Type$addTabs('Discussion', '', 'Info', 'Content',
                                        'References', 'Rights', 'Protocol', '', '', '', '', '');
    
    
    -- Thread
    CALL IBSDEV1.p_Type$addTabs('Thread', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- Article
    CALL IBSDEV1.p_Type$addTabs('Article', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- XMLDiscussionTemplate
    CALL IBSDEV1.p_Type$addTabs('XMLDiscussionTemplate', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- diary:
    -- TerminplanContainer
    CALL IBSDEV1.p_Type$addTabs('TerminplanContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- Terminplan
    CALL IBSDEV1.p_Type$addTabs('Terminplan', 'Month', 'Info', 'Content',
                            'References', 'Rights', 'Day', 'Month', '', '', '', '');
    
    
    -- news:
    -- NewsContainer
    CALL IBSDEV1.p_Type$addTabs('NewsContainer', '', 'Info', 'Content', '', '', '', '', '', '', '', '');
    
    
    -- discussions:
    -- DiscussionContainer
    CALL IBSDEV1.p_Type$addTabs('DiscussionContainer', '', 'Info', 'Content',
                                         'References', 'Rights', 'Templates', '', '', '', '', '');
    

    -- BlackBoard
    CALL IBSDEV1.p_Type$addTabs('BlackBoard', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- store:
    -- CatalogContainer
    CALL IBSDEV1.p_Type$addTabs('Store', '', 'Info', 'Content',
                            'References', 'Rights', 'Protocol', 'ShoppingCart', '', '', '', '');
    
    
    -- Catalog
    CALL IBSDEV1.p_Type$addTabs('Catalog', '', 'Info', 'Content',
                            'References', 'Rights', 'Protocol', 'ShoppingCart',
                                                    'Contacts', '', '', '');
    
    -- ProductGroupContainer ??? (ProductGroupProfileContainer)
    CALL IBSDEV1.p_Type$addTabs('ProductGroupContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- ProductGroupProfile
    CALL IBSDEV1.p_Type$addTabs('ProductGroupProfile', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- OrderContainer
    CALL IBSDEV1.p_Type$addTabs('OrderContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- Order
    CALL IBSDEV1.p_Type$addTabs('Order', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- ShoppingCart
    CALL IBSDEV1.p_Type$addTabs('ShoppingCart', '', 'Content', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- Product
    CALL IBSDEV1.p_Type$addTabs('Product', '', 'Info', 'References',
                            'Rights', 'Prices', 'Assortments', 'Contacts',
                                                    'ShoppingCart', '', '', '');

    -- store:
    -- ProductGroup
    CALL IBSDEV1.p_Type$addTabs('ProductGroup', '', 'Info', 'Content',
                                         'References', 'Rights', 'ShoppingCart', '', '', '', '', '', '');

    -- ProductSizeColor (Price)
    CALL IBSDEV1.p_Type$addTabs('ProductSizeColor', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- master data:
    -- MasterDataContainer
    CALL IBSDEV1.p_Type$addTabs('MasterDataContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- Person
    CALL IBSDEV1.p_Type$addTabs('Person', '', 'Info', 'References',
                                                    'Rights', 'Address', '', '', '', '', '', '');
    
    -- Company
    CALL IBSDEV1.p_Type$addTabs('Company', '', 'Info', 'References',
                            'Rights', 'Address', 'Contacts', '', '', '', '', '');

    -- diary:
    -- Participant
    CALL IBSDEV1.p_Type$addTabs('Participant', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');

    -- Document Management:
    -- DocumentContainer
    CALL IBSDEV1.p_Type$addTabs('DocumentContainer', '', 'Info', 'Content',
                                         'References', 'Rights', 'Protocol', '', '', '', '', '');

    -- Store:
    -- Property ??? (ProductProperties)
    CALL IBSDEV1.p_Type$addTabs('Property', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
    
    -- ProductPropertiesContainer
    CALL IBSDEV1.p_Type$addTabs('PropertyContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');

    -- PropertyCategory
    CALL IBSDEV1.p_Type$addTabs('PropertyCategory', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
    
    -- PropertyCategoryContainer
    CALL IBSDEV1.p_Type$addTabs('PropertyCategoryContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- store:
    -- ProductProfile
    CALL IBSDEV1.p_Type$addTabs('ProductProfile', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
    
    -- ProductProfileContainer
    CALL IBSDEV1.p_Type$addTabs('ProductProfileContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- PaymentType
    CALL IBSDEV1.p_Type$addTabs('PaymentType', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
    
    -- PaymentTypeContainer
    CALL IBSDEV1.p_Type$addTabs('PaymentTypeContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- store:
    -- ProductBrand
    CALL IBSDEV1.p_Type$addTabs('ProductBrand', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
    
    -- ProductCollection
    CALL IBSDEV1.p_Type$addTabs('ProductCollection', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes6');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createBaseObjTypes6 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
  DECLARE SQLCODE INT;
  DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- set default tabs for all types which don't have default tabs:
    UPDATE  IBSDEV1.ibs_TVersion
    SET     defaultTab =
            (
                SELECT  COALESCE (MIN (cId), 0)
                FROM    (
                            SELECT  tVersionId AS cTVersionId, id AS cId,
                                    priority AS cPriority
                            FROM    IBSDEV1.ibs_ConsistsOf
                        ) c
                WHERE   cPriority =
                        (
                            SELECT  MAX (c2Priority)
                            FROM	(
                                        SELECT  tVersionId AS c2TVersionId,
                                                priority AS c2Priority
                                        FROM    IBSDEV1.ibs_ConsistsOf
                                    ) c2
                            WHERE   c2TVersionId = id
                        )
                    AND cTVersionId = id
            )
    WHERE   defaultTab = 0;
END;

--/////////////////////////////////////////////////////////////////////////////
-- ensure that each tVersion has a correct state
--/////////////////////////////////////////////////////////////////////////////
-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes7');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createBaseObjTypes7 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
  DECLARE SQLCODE INT;
  DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    UPDATE IBSDEV1.ibs_TVersion
    SET state = 2
    WHERE state = 4;
END;


-- execute procedures:
CALL IBSDEV1.pim2_createBaseObjTypes;
CALL IBSDEV1.pim2_createBaseObjTypes2;
CALL IBSDEV1.pim2_createBaseObjTypes3;
CALL IBSDEV1.pim2_createBaseObjTypes4;
CALL IBSDEV1.pim2_createBaseObjTypes5;
CALL IBSDEV1.pim2_createBaseObjTypes6;
CALL IBSDEV1.pim2_createBaseObjTypes7;

-- delete procedures:
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes');
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes2');
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes3');
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes4');
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes5');
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes6');
CALL IBSDEV1.p_dropProc ('pim2_createBaseObjTypes7');
