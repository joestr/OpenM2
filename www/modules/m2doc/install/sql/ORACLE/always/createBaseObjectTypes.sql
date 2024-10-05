/******************************************************************************
 * Create all business object types within m2. <BR>
 *
 * @version     $Id: createBaseObjectTypes.sql,v 1.50 2004/01/06 23:53:40 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */

-- p_Type$newLang (id, superTypeCode, isContainer, isInheritable,
--      isSearchable, showInMenu, showInNews, code, className, languageId,
--      typeNameName);
-- ex.:
-- p_Type$newLang (stringToInt ('0x01010050'), 'BusinessObject', 0, 1, 1, 0, 1,
--    'Attachment', 'ibs.bo.Attachment_01', @c_languageId, 'TN_Attachment_01');

DECLARE
    -- constants:
    c_languageId            CONSTANT INTEGER := 0; -- the current language

BEGIN

-- documents:
-- Document
p_Type$newLang (stringToInt ('0x01010100'), 'BusinessObject', 0, 1, 1, 0, 1, 'Document',
    'm2.doc.Document_01', c_languageId, 'TN_Document_01');

-- diary:
-- Termin
p_Type$newLang (stringToInt ('0x01010200'), 'BusinessObject', 0, 1, 1, 0, 1, 'Termin',
    'm2.diary.Termin_01', c_languageId, 'TN_Termin_01');


-- discussions:
-- Discussion
p_Type$newLang (stringToInt ('0x01010300'), 'Container', 1, 1, 1, 0, 0, 'Discussion',
    'm2.bbd.Discussion_01', c_languageId, 'TN_Discussion_01');

-- Thread
p_Type$newLang (stringToInt ('0x01010400'), 'Container', 1, 1, 1, 0, 1, 'Thread',
    'm2.bbd.Thread_01', c_languageId, 'TN_Thread_01');


-- Article
p_Type$newLang (stringToInt ('0x01010500'), 'Container', 1, 1, 1, 0, 1, 'Article',
    'm2.bbd.Article_01', c_languageId, 'TN_Article_01');

-- XMLDiscussion
p_Type$newLang (stringToInt ('0x01010320'), 'Discussion',   1, 1, 1, 0, 1, 'XMLDiscussion',
    'm2.bbd.XMLDiscussion_01', c_languageId, 'TN_XMLDiscussion_01');
-- XMLDiscussionTemplate
p_Type$newLang (stringToInt ('0x01010310'), 'BusinessObject', 1, 1, 1, 0, 1, 'XMLDiscussionTemplate',
    'm2.bbd.XMLDiscussionTemplate_01', c_languageId,
    'TN_XMLDiscussionTemplate_01');

-- diary:
-- TerminplanContainer
p_Type$newLang (stringToInt ('0x01010600'), 'Container', 1, 1, 0, 1, 0, 'TerminplanContainer',
    'm2.diary.TerminplanContainer_01', c_languageId,
    'TN_TerminplanContainer_01');

-- Terminplan
p_Type$newLang (stringToInt ('0x01010700'), 'Container', 1, 1, 0, 1, 0, 'Terminplan',
    'm2.diary.Terminplan_01', c_languageId, 'TN_Terminplan_01');

-- discussions:
-- DiscussionContainer
p_Type$newLang (stringToInt ('0x01010900'), 'Container', 1, 1, 0, 1, 0, 'DiscussionContainer',
    'm2.bbd.DiscussionContainer_01', c_languageId, 'TN_DiscussionContainer_01');

-- BlackBoard
p_Type$newLang (stringToInt ('0x01010A00'), 'Container', 1, 1, 1, 0, 1, 'BlackBoard',
    'm2.bbd.BlackBoard_01', c_languageId, 'TN_BlackBoard_01');

-- store:
-- CatalogContainer
p_Type$newLang (stringToInt ('0x01010B00'), 'Container', 1, 1, 0, 1, 0, 'Store',
    'm2.store.CatalogContainer_01', c_languageId, 'TN_CatalogContainer_01');

-- Catalog
p_Type$newLang (stringToInt ('0x01010C00'), 'Container', 1, 1, 1, 1, 1, 'Catalog',
    'm2.store.Catalog_01', c_languageId, 'TN_Catalog_01');

-- ProductGroupContainer  ??? (ProductGroupProfileContainer)
p_Type$newLang (stringToInt ('0x01010D00'), 'Container', 1, 1, 0, 1, 0, 'ProductGroupContainer',
    'm2.store.ProductGroupProfileContainer_01', c_languageId,
    'TN_ProductGroupProfileContainer_01');

-- ProductGroupProfile
p_Type$newLang (stringToInt ('0x01010E00'), 'Container', 1, 1, 1, 0, 1, 'ProductGroupProfile',
    'm2.store.ProductGroupProfile_01', c_languageId,
    'TN_ProductGroupProfile_01');

-- OrderContainer
p_Type$newLang (stringToInt ('0x01011200'), 'Container', 1, 1, 0, 1, 0, 'OrderContainer',
    'm2.store.OrderContainer_01', c_languageId, 'TN_OrderContainer_01');

-- Order
p_Type$newLang (stringToInt ('0x01011300'), 'BusinessObject', 0, 1, 0, 0, 0, 'Order',
    'm2.store.Order_01', c_languageId, 'TN_Order_01');

-- ShoppingCart
p_Type$newLang (stringToInt ('0x01011400'), 'Container', 1, 1, 0, 1, 0, 'ShoppingCart',
    'm2.store.ShoppingCart_01', c_languageId, 'TN_ShoppingCart_01');

-- Product
p_Type$newLang (stringToInt ('0x01011500'), 'BusinessObject', 0, 1, 1, 0, 1, 'Product',
    'm2.store.Product_01', c_languageId, 'TN_Product_01');

/* currently not available
-- Size
p_Type$newLang (stringToInt ('0x01011600'), 'BusinessObject', 0, 1, 0, 0, 0, 'Size',
    'm2.store.Size_01', c_languageId, 'TN_Size_01');
-- Color
p_Type$newLang (stringToInt ('0x01011700'), 'BusinessObject', 0, 1, 0, 0, 0, 'Color',
    'm2.store.Color_01', c_languageId, 'TN_Color_01');
*/
-- ShoppingCartLine
p_Type$newLang (stringToInt ('0x01011900'), 'BusinessObject', 0, 1, 0, 0, 0, 'ShoppingCartLine',
    'm2.store.ShoppingCartLine_01', c_languageId, 'TN_ShoppingCartLine_01');

-- Diary:
-- OverlapContainer
p_Type$newLang (stringToInt ('0x01011A00'), 'Container', 1, 1, 0, 0, 0, 'OverlapContainer',
    'm2.diary.OverlapContainer_01', c_languageId, 'TN_OverlapContainer_01');

-- store:
-- ProductGroup
p_Type$newLang (stringToInt ('0x01011F00'), 'Container', 1, 1, 1, 1, 1, 'ProductGroup',
    'm2.store.ProductGroup_01', c_languageId, 'TN_ProductGroup_01');

-- diary:
-- ParticipantContainer
p_Type$newLang (stringToInt ('0x01012000'), 'Container', 1, 1, 0, 0, 0, 'ParticipantContainer',
    'm2.diary.ParticipantContainer_01', c_languageId,
    'TN_ParticipantContainer_01');

-- store:
-- ProductSizeColorContainer (PriceContainer)
p_Type$newLang (stringToInt ('0x01012100'), 'Container', 1, 1, 0, 0, 0, 'ProductSizeColorContainer',
    'm2.store.PriceContainer_01', c_languageId,
    'TN_ProductSizeColorContainer_01');

-- ProductSizeColor (Price)
p_Type$newLang (stringToInt ('0x01012200'), 'BusinessObject', 0, 1, 0, 0, 0, 'ProductSizeColor',
    'm2.store.Price_01', c_languageId, 'TN_ProductSizeColor_01');

-- master data:
-- MasterDataContainer
p_Type$newLang (stringToInt ('0x01012900'), 'Container', 1, 1, 0, 1, 0, 'MasterDataContainer',
    'm2.mad.MasterDataContainer_01', c_languageId, 'TN_MasterDataContainer_01');

-- Person
p_Type$newLang (stringToInt ('0x01012A00'), 'BusinessObject', 0, 1, 1, 0, 1, 'Person',
    'm2.mad.Person_01', c_languageId, 'TN_Person_01');

-- PersonContainer
p_Type$newLang (stringToInt ('0x01012B00'), 'Container', 1, 1, 0, 0, 0, 'PersonContainer',
    'm2.mad.PersonContainer_01', c_languageId, 'TN_PersonContainer_01');
-- Company
p_Type$newLang (stringToInt ('0x01012C00'), 'BusinessObject', 0, 1, 1, 0, 1, 'Company',
    'm2.mad.Company_01', c_languageId, 'TN_Company_01');

-- diary:
-- Participant
p_Type$newLang (stringToInt ('0x01012E00'), 'BusinessObject', 0, 1, 1, 0, 0, 'Participant',
    'm2.diary.Participant_01', c_languageId, 'TN_Participant_01');

-- master data:
-- Address
p_Type$newLang (stringToInt ('0x01012F00'), 'BusinessObject', 0, 1, 0, 0, 1, 'Address',
    'm2.mad.Address_01', c_languageId, 'TN_Address_01');

-- Document Management:
-- DocumentContainer
p_Type$newLang (stringToInt ('0x01014B00'), 'Container', 1, 1, 1, 1, 1, 'DocumentContainer',
    'm2.doc.DocumentContainer_01', c_languageId, 'TN_DocumentContainer_01');

-- Store:
-- Property ??? (ProductProperties)
p_Type$newLang (stringToInt ('0x01015A00'), 'BusinessObject', 0, 1, 0, 0, 0, 'Property',
    'm2.store.ProductProperties_01', c_languageId, 'TN_ProductProperties_01');

-- ProductPropertiesContainer
p_Type$newLang (stringToInt ('0x01015B00'), 'Container', 1, 1, 0, 1, 0, 'PropertyContainer',
    'm2.store.ProductPropertiesContainer_01', c_languageId,
    'TN_ProductPropertiesContainer_01');

-- master data:
-- PersonUserContainer
p_Type$newLang (stringToInt ('0x01015E00'), 'Container', 1, 1, 0, 0, 0, 'PersonUserContainer',
    'm2.mad.PersonUserContainer_01', c_languageId, 'TN_PersonUserContainer_01');

-- store:
-- PropertyCategory
p_Type$newLang (stringToInt ('0x01015F00'), 'BusinessObject', 0, 1, 0, 0, 0, 'PropertyCategory',
    'm2.store.PropertyCategory_01', c_languageId, 'TN_PropertyCategory_01');

-- PropertyCategoryContainer
p_Type$newLang (stringToInt ('0x01016000'), 'Container', 1, 1, 0, 1, 0, 'PropertyCategoryContainer',
    'm2.store.PropertyCategoryContainer_01', c_languageId,
    'TN_PropertyCategoryContainer_01');

-- store:
-- ProductProfile
p_Type$newLang (stringToInt ('0x01016C00'), 'BusinessObject', 0, 1, 0, 0, 0, 'ProductProfile',
    'm2.store.ProductProfile_01', c_languageId, 'TN_ProductProfile_01');

-- ProductProfileContainer
p_Type$newLang (stringToInt ('0x01016D00'), 'Container', 1, 1, 0, 1, 0, 'ProductProfileContainer',
    'm2.store.ProductProfileContainer_01', c_languageId,
    'TN_ProductProfileContainer_01');

-- PaymentType
p_Type$newLang (stringToInt ('0x01016C10'), 'BusinessObject', 0, 1, 0, 0, 0, 'PaymentType',
    'm2.store.PaymentType_01', c_languageId, 'TN_PaymentType_01');

-- PaymentTypeContainer
p_Type$newLang (stringToInt ('0x01016D10'), 'Container', 1, 1, 0, 1, 0, 'PaymentTypeContainer',
    'm2.store.PaymentTypeContainer_01', c_languageId,
    'TN_PaymentTypeContainer_01');

-- ProductBrand
p_Type$newLang (stringToInt ('0x01017100'), 'BusinessObject', 0, 1, 0, 0, 0, 'ProductBrand',
    'm2.store.ProductBrand_01', c_languageId, 'TN_ProductBrand_01');

-- ProductProfileContainer
p_Type$newLang (stringToInt ('0x01017200'), 'Container', 1, 1, 0, 1, 0, 'ProductBrandContainer',
    'm2.store.ProductBrandContainer_01', c_languageId,
    'TN_ProductBrandContainer_01');

-- DiscXMLViewer
p_Type$newLang (stringToInt ('0x01017510'), 'XMLViewer', 1, 1, 1, 0, 1, 'DiscXMLViewer',
    'm2.bbd.DiscXMLViewer_01', c_languageId, 'TN_DiscXMLViewer_01');

-- Store:
-- ProductCollection
p_Type$newLang (stringToInt ('0x01017600'), 'BusinessObject', 0, 1, 0, 0, 0, 'ProductCollection',
    'm2.store.ProductCollection_01', c_languageId, 'TN_ProductCollection_01');

-- ProductCollectionContainer
p_Type$newLang (stringToInt ('0x01017700'), 'Container', 1, 1, 1, 1, 0,
    'ProductCollectionContainer',
    'm2.store.ProductCollectionContainer_01', c_languageId,
    'TN_ProductCollectionContainer_01');
-- SelectUserContainer
p_Type$newLang (stringToInt ('0x01017800'), 'Container', 1, 1, 0, 0, 0, 'SelectUserContainer',
    'm2.store.SelectUserContainer_01', c_languageId,
    'TN_SelectUserContainer_01');

-- SelectCompanyContainer
p_Type$newLang (stringToInt ('0x01017B00'), 'Container', 1, 1, 0, 0, 0, 'SelectCompanyContainer',
    'm2.store.SelectCompanyContainer_01', c_languageId,
    'TN_SelectCompanyContainer_01');

-- XMLDiscussionTemplateContainer
p_Type$newLang (stringToInt ('0x01017d10'), 'DocumentTemplateContainer', 1, 1, 1, 0, 0,
    'XMLDiscussionTemplateContainer',
    'm2.bbd.XMLDiscussionTemplateContainer_01', c_languageId,
    'TN_XMLDiscussionTemplateContainer_01');

-------------------------------------------------------------------------------
-- The following types do not have predefined type ids.
-- This is necessary due to the fact that type ids for other object types can
-- be set dynamically and to avoid that different types have the same id.

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
    l_retValue := p_Tab$new (0, 'Month', c_TK_VIEW, 0, 3002, 9105, 'OD_tabMonth', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Day', c_TK_VIEW, 0, 3001, 9100, 'OD_tabDay', 4194304, 'm2.diary.Terminplan_01', l_tabId);
    l_retValue := p_Tab$new (0, 'Address', c_TK_OBJECT, stringToInt ('0x01012F01'), 51, 0, 'OD_tabAddress', 4, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Assortments', c_TK_OBJECT, stringToInt ('0x01017701'), 51, 0, 'OD_tabAssortments', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Branches', c_TK_OBJECT, stringToInt ('0x01016601'), 51, 0, 'OD_tabBranches', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Contacts', c_TK_OBJECT, stringToInt ('0x01012B01'), 51, 0, 'OD_tabContacts', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'PersonUsers', c_TK_OBJECT, stringToInt ('0x01015E01'), 51, 0, 'OD_tabPersonUsers', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Prices', c_TK_OBJECT, stringToInt ('0x01012101'), 51, 0, 'OD_tabPrices', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Properties', c_TK_OBJECT, stringToInt ('0x01015B01'), 51, 0, 'OD_tabProperties', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Versions', c_TK_OBJECT, stringToInt ('0x01014F01'), 51, 0, 'OD_tabVersions', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'Participants', c_TK_OBJECT, stringToInt ('0x01012001'), 51, 0, 'OD_tabParticipants', 4194304, NULL, l_tabId);
    l_retValue := p_Tab$new (0, 'ShoppingCart', c_TK_FUNCTION, 0, 9001, -3000, 'OD_tabShoppingCart', 4194304, NULL, l_tabId);
END;
/

BEGIN
-- documents:
-- Document
p_Type$addTabs ('Document', NULL
    , 'Info', 'Attachments', 'References', 'Rights', 'Protocol', '', '', '', '', '');

-- diary:
-- Termin
p_Type$addTabs ('Termin', NULL
    , 'Info', 'Contacts', 'References', 'Rights', 'Protocol', 'Attachments', 'Participants', '', '', '');

-- discussions:
-- Discussion
p_Type$addTabs ('Discussion', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', '', '', '', '', '');

-- Thread
p_Type$addTabs ('Thread', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- Article
p_Type$addTabs ('Article', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- XMLDiscussionTemplate
p_Type$addTabs ('XMLDiscussionTemplate', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- diary:
-- TerminplanContainer
p_Type$addTabs ('TerminplanContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- Terminplan
p_Type$addTabs ('Terminplan', 'Month'
    , 'Info', 'Content', 'References', 'Rights', 'Day', 'Month', '', '', '', '');

-- discussions:
-- DiscussionContainer
p_Type$addTabs ('DiscussionContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Templates', '', '', '', '', '');

-- BlackBoard
p_Type$addTabs ('BlackBoard', NULL
   , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- store:
-- CatalogContainer
p_Type$addTabs ('Store', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', 'ShoppingCart', '', '', '', '');

-- Catalog
p_Type$addTabs ('Catalog', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', 'ShoppingCart', 'Contacts', '', '', '');

-- ProductGroupContainer ??? (ProductGroupProfileContainer)
p_Type$addTabs ('ProductGroupContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- ProductGroupProfile
p_Type$addTabs ('ProductGroupProfile', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- OrderContainer
p_Type$addTabs ('OrderContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- Order
p_Type$addTabs ('Order', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- ShoppingCart
p_Type$addTabs ('ShoppingCart', NULL
    , 'Content', 'References', 'Rights', '', '', '', '', '', '', '');

-- Product
p_Type$addTabs ('Product', NULL
    , 'Info', 'References', 'Rights', 'Prices'
    , 'Assortments', 'Contacts', 'ShoppingCart', '', '', '');

-- ProductGroup
p_Type$addTabs ('ProductGroup', NULL
    , 'Info', 'Content', 'References', 'Rights', 'ShoppingCart', '', '', '', '', '');

-- ProductSizeColor (Price)
p_Type$addTabs ('ProductSizeColor', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- master data:
-- MasterDataContainer
p_Type$addTabs ('MasterDataContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');

-- Person
p_Type$addTabs ('Person', NULL
    , 'Info', 'References', 'Rights', 'Address', '', '', '', '', '', '');

-- Company
p_Type$addTabs ('Company', NULL
    , 'Info', 'References', 'Rights', 'Address', 'Contacts', '', '', '', '', '');

-- diary:
-- Participant
p_Type$addTabs ('Participant', NULL
    , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

-- Document Management:
-- DocumentContainer
p_Type$addTabs ('DocumentContainer', NULL
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', '', '', '', '', '');

-- Store:
-- Property ??? (ProductProperties)
p_Type$addTabs ('Property', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- ProductPropertiesContainer
p_Type$addTabs ('PropertyContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- store:
-- PropertyCategory
p_Type$addTabs ('PropertyCategory', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- PropertyCategoryContainer
p_Type$addTabs ('PropertyCategoryContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- store:
-- ProductProfile
p_Type$addTabs ('ProductProfile', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- ProductProfileContainer
p_Type$addTabs ('ProductProfileContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- PaymentType
p_Type$addTabs ('PaymentType', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- PaymentTypeContainer
p_Type$addTabs ('PaymentTypeContainer', NULL
    , 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

-- store:
-- ProductBrand
p_Type$addTabs ('ProductBrand', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');

-- Store:
-- ProductCollection
p_Type$addTabs ('ProductCollection', NULL
    , 'Info', 'Rights', '', '', '', '', '', '', '', '');
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
