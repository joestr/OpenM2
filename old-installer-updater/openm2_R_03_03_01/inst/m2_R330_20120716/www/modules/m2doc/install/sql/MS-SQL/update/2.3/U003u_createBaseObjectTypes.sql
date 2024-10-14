/******************************************************************************
 * Create all business object types within m2. <BR>
 *
 * @version     $Id: U003u_createBaseObjectTypes.sql,v 1.1 2004/01/18 02:08:12 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */

-- EXEC p_Type$newLang id, superTypeCode, isContainer, isInheritable,
--      isSearchable, showInMenu, showInNews, code, className, languageId,
--      typeNameName
-- ex.:
-- EXEC p_Type$newLang 0x01010050, 'BusinessObject', 0, 1, 1, 0, 1, 'Attachment',
--    'ibs.bo.Attachment_01', @c_languageId, 'TN_Attachment_01'

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

-- documents:
-- Document
EXEC p_Type$newLang 0x01010100, 'BusinessObject', 0, 1, 1, 0, 1, 'Document',
    'm2.doc.Document_01', @c_languageId, 'TN_Document_01'

-- diary:
-- Termin
EXEC p_Type$newLang 0x01010200, 'BusinessObject', 0, 1, 1, 0, 1, 'Termin',
    'm2.diary.Termin_01', @c_languageId, 'TN_Termin_01'


-- discussions:
-- Discussion
EXEC p_Type$newLang 0x01010300, 'Container', 1, 1, 1, 0, 0, 'Discussion',
    'm2.bbd.Discussion_01', @c_languageId, 'TN_Discussion_01'

-- Thread
EXEC p_Type$newLang 0x01010400, 'Container', 1, 1, 1, 0, 1, 'Thread',
    'm2.bbd.Thread_01', @c_languageId, 'TN_Thread_01'


-- Article
EXEC p_Type$newLang 0x01010500, 'Container', 1, 1, 1, 0, 1, 'Article',
    'm2.bbd.Article_01', @c_languageId, 'TN_Article_01'

-- XMLDiscussion
EXEC p_Type$newLang 0x01010320, 'Discussion',   1, 1, 1, 0, 1, 'XMLDiscussion',
    'm2.bbd.XMLDiscussion_01', @c_languageId, 'TN_XMLDiscussion_01'
-- XMLDiscussionTemplate
EXEC p_Type$newLang 0x01010310, 'BusinessObject', 1, 1, 1, 0, 1, 'XMLDiscussionTemplate',
    'm2.bbd.XMLDiscussionTemplate_01', @c_languageId,
    'TN_XMLDiscussionTemplate_01'

-- diary:
-- TerminplanContainer
EXEC p_Type$newLang 0x01010600, 'Container', 1, 1, 0, 1, 0, 'TerminplanContainer',
    'm2.diary.TerminplanContainer_01', @c_languageId,
    'TN_TerminplanContainer_01'

-- Terminplan
EXEC p_Type$newLang 0x01010700, 'Container', 1, 1, 0, 1, 0, 'Terminplan',
    'm2.diary.Terminplan_01', @c_languageId, 'TN_Terminplan_01'

-- discussions:
-- DiscussionContainer
EXEC p_Type$newLang 0x01010900, 'Container', 1, 1, 0, 1, 0, 'DiscussionContainer',
    'm2.bbd.DiscussionContainer_01', @c_languageId, 'TN_DiscussionContainer_01'

-- BlackBoard
EXEC p_Type$newLang 0x01010A00, 'Container', 1, 1, 1, 0, 1, 'BlackBoard',
    'm2.bbd.BlackBoard_01', @c_languageId, 'TN_BlackBoard_01'

-- store:
-- CatalogContainer
EXEC p_Type$newLang 0x01010B00, 'Container', 1, 1, 0, 1, 0, 'Store',
    'm2.store.CatalogContainer_01', @c_languageId, 'TN_CatalogContainer_01'

-- Catalog
EXEC p_Type$newLang 0x01010C00, 'Container', 1, 1, 1, 1, 1, 'Catalog',
    'm2.store.Catalog_01', @c_languageId, 'TN_Catalog_01'

-- ProductGroupContainer  ??? (ProductGroupProfileContainer)
EXEC p_Type$newLang 0x01010D00, 'Container', 1, 1, 0, 1, 0, 'ProductGroupContainer',
    'm2.store.ProductGroupProfileContainer_01', @c_languageId,
    'TN_ProductGroupProfileContainer_01'

-- ProductGroupProfile
EXEC p_Type$newLang 0x01010E00, 'Container', 1, 1, 1, 0, 1, 'ProductGroupProfile',
    'm2.store.ProductGroupProfile_01', @c_languageId,
    'TN_ProductGroupProfile_01'

-- OrderContainer
EXEC p_Type$newLang 0x01011200, 'Container', 1, 1, 0, 1, 0, 'OrderContainer',
    'm2.store.OrderContainer_01', @c_languageId, 'TN_OrderContainer_01'

-- Order
EXEC p_Type$newLang 0x01011300, 'BusinessObject', 0, 1, 0, 0, 0, 'Order',
    'm2.store.Order_01', @c_languageId, 'TN_Order_01'

-- ShoppingCart
EXEC p_Type$newLang 0x01011400, 'Container', 1, 1, 0, 1, 0, 'ShoppingCart',
    'm2.store.ShoppingCart_01', @c_languageId, 'TN_ShoppingCart_01'

-- Product
EXEC p_Type$newLang 0x01011500, 'BusinessObject', 0, 1, 1, 0, 1, 'Product',
    'm2.store.Product_01', @c_languageId, 'TN_Product_01'

/* currently not available
-- Size
EXEC p_Type$newLang 0x01011600, 'BusinessObject', 0, 1, 0, 0, 0, 'Size',
    'm2.store.Size_01', @c_languageId, 'TN_Size_01'
-- Color
EXEC p_Type$newLang 0x01011700, 'BusinessObject', 0, 1, 0, 0, 0, 'Color',
    'm2.store.Color_01', @c_languageId, 'TN_Color_01'
*/
-- ShoppingCartLine
EXEC p_Type$newLang 0x01011900, 'BusinessObject', 0, 1, 0, 0, 0, 'ShoppingCartLine',
    'm2.store.ShoppingCartLine_01', @c_languageId, 'TN_ShoppingCartLine_01'

-- Diary:
-- OverlapContainer
EXEC p_Type$newLang 0x01011A00, 'Container', 1, 1, 0, 0, 0, 'OverlapContainer',
    'm2.diary.OverlapContainer_01', @c_languageId, 'TN_OverlapContainer_01'

-- store:
-- ProductGroup
EXEC p_Type$newLang 0x01011F00, 'Container', 1, 1, 1, 1, 1, 'ProductGroup',
    'm2.store.ProductGroup_01', @c_languageId, 'TN_ProductGroup_01'

-- diary:
-- ParticipantContainer
EXEC p_Type$newLang 0x01012000, 'Container', 1, 1, 0, 0, 0, 'ParticipantContainer',
    'm2.diary.ParticipantContainer_01', @c_languageId,
    'TN_ParticipantContainer_01'

-- store:
-- ProductSizeColorContainer (PriceContainer)
EXEC p_Type$newLang 0x01012100, 'Container', 1, 1, 0, 0, 0, 'ProductSizeColorContainer',
    'm2.store.PriceContainer_01', @c_languageId,
    'TN_ProductSizeColorContainer_01'

-- ProductSizeColor (Price)
EXEC p_Type$newLang 0x01012200, 'BusinessObject', 0, 1, 0, 0, 0, 'ProductSizeColor',
    'm2.store.Price_01', @c_languageId, 'TN_ProductSizeColor_01'

-- master data:
-- MasterDataContainer
EXEC p_Type$newLang 0x01012900, 'Container', 1, 1, 0, 1, 0, 'MasterDataContainer',
    'm2.mad.MasterDataContainer_01', @c_languageId, 'TN_MasterDataContainer_01'

-- Person
EXEC p_Type$newLang 0x01012A00, 'BusinessObject', 0, 1, 1, 0, 1, 'Person',
    'm2.mad.Person_01', @c_languageId, 'TN_Person_01'

-- PersonContainer
EXEC p_Type$newLang 0x01012B00, 'Container', 1, 1, 0, 0, 0, 'PersonContainer',
    'm2.mad.PersonContainer_01', @c_languageId, 'TN_PersonContainer_01'
-- Company
EXEC p_Type$newLang 0x01012C00, 'BusinessObject', 0, 1, 1, 0, 1, 'Company',
    'm2.mad.Company_01', @c_languageId, 'TN_Company_01'

-- diary:
-- Participant
EXEC p_Type$newLang 0x01012E00, 'BusinessObject', 0, 1, 1, 0, 0, 'Participant',
    'm2.diary.Participant_01', @c_languageId, 'TN_Participant_01'

-- master data:
-- Address
EXEC p_Type$newLang 0x01012F00, 'BusinessObject', 0, 1, 0, 0, 1, 'Address',
    'm2.mad.Address_01', @c_languageId, 'TN_Address_01'

-- Document Management:
-- DocumentContainer
EXEC p_Type$newLang 0x01014B00, 'Container', 1, 1, 1, 1, 1, 'DocumentContainer',
    'm2.doc.DocumentContainer_01', @c_languageId, 'TN_DocumentContainer_01'

-- Store:
-- Property ??? (ProductProperties)
EXEC p_Type$newLang 0x01015A00, 'BusinessObject', 0, 1, 0, 0, 0, 'Property',
    'm2.store.ProductProperties_01', @c_languageId, 'TN_ProductProperties_01'

-- ProductPropertiesContainer
EXEC p_Type$newLang 0x01015B00, 'Container', 1, 1, 0, 1, 0, 'PropertyContainer',
    'm2.store.ProductPropertiesContainer_01', @c_languageId,
    'TN_ProductPropertiesContainer_01'

-- master data:
-- PersonUserContainer
EXEC p_Type$newLang 0x01015E00, 'Container', 1, 1, 0, 0, 0, 'PersonUserContainer',
    'm2.mad.PersonUserContainer_01', @c_languageId, 'TN_PersonUserContainer_01'

-- store:
-- PropertyCategory
EXEC p_Type$newLang 0x01015F00, 'BusinessObject', 0, 1, 0, 0, 0, 'PropertyCategory',
    'm2.store.PropertyCategory_01', @c_languageId, 'TN_PropertyCategory_01'

-- PropertyCategoryContainer
EXEC p_Type$newLang 0x01016000, 'Container', 1, 1, 0, 1, 0, 'PropertyCategoryContainer',
    'm2.store.PropertyCategoryContainer_01', @c_languageId,
    'TN_PropertyCategoryContainer_01'

-- store:
-- ProductProfile
EXEC p_Type$newLang 0x01016C00, 'BusinessObject', 0, 1, 0, 0, 0, 'ProductProfile',
    'm2.store.ProductProfile_01', @c_languageId, 'TN_ProductProfile_01'

-- ProductProfileContainer
EXEC p_Type$newLang 0x01016D00, 'Container', 1, 1, 0, 1, 0, 'ProductProfileContainer',
    'm2.store.ProductProfileContainer_01', @c_languageId,
    'TN_ProductProfileContainer_01'

-- PaymentType
EXEC p_Type$newLang 0x01016C10, 'BusinessObject', 0, 1, 0, 0, 0, 'PaymentType',
    'm2.store.PaymentType_01', @c_languageId, 'TN_PaymentType_01'

-- PaymentTypeContainer
EXEC p_Type$newLang 0x01016D10, 'Container', 1, 1, 0, 1, 0, 'PaymentTypeContainer',
    'm2.store.PaymentTypeContainer_01', @c_languageId,
    'TN_PaymentTypeContainer_01'

-- ProductBrand
EXEC p_Type$newLang 0x01017100, 'BusinessObject', 0, 1, 0, 0, 0, 'ProductBrand',
    'm2.store.ProductBrand_01', @c_languageId, 'TN_ProductBrand_01'

-- ProductProfileContainer
EXEC p_Type$newLang 0x01017200, 'Container', 1, 1, 0, 1, 0, 'ProductBrandContainer',
    'm2.store.ProductBrandContainer_01', @c_languageId,
    'TN_ProductBrandContainer_01'

-- DiscXMLViewer
EXEC p_Type$newLang 0x01017510, 'XMLViewer', 1, 1, 1, 0, 1, 'DiscXMLViewer',
    'm2.bbd.DiscXMLViewer_01', @c_languageId, 'TN_DiscXMLViewer_01'

-- Store:
-- ProductCollection
EXEC p_Type$newLang 0x01017600, 'BusinessObject', 0, 1, 0, 0, 0, 'ProductCollection',
    'm2.store.ProductCollection_01', @c_languageId, 'TN_ProductCollection_01'

-- ProductCollectionContainer
EXEC p_Type$newLang 0x01017700, 'Container', 1, 1, 0, 1, 0,
    'ProductCollectionContainer',
    'm2.store.ProductCollectionContainer_01', @c_languageId,
    'TN_ProductCollectionContainer_01'
-- SelectUserContainer
EXEC p_Type$newLang 0x01017800, 'Container', 1, 1, 0, 0, 0, 'SelectUserContainer',
    'm2.store.SelectUserContainer_01', @c_languageId,
    'TN_SelectUserContainer_01'

-- SelectCompanyContainer
EXEC p_Type$newLang 0x01017B00, 'Container', 1, 1, 0, 0, 0, 'SelectCompanyContainer',
    'm2.store.SelectCompanyContainer_01', @c_languageId,
    'TN_SelectCompanyContainer_01'

-- XMLDiscussionTemplateContainer
EXEC p_Type$newLang 0x01017d10, 'DocumentTemplateContainer', 1, 1, 1, 0, 0,
    'XMLDiscussionTemplateContainer',
    'm2.bbd.XMLDiscussionTemplateContainer_01', @c_languageId,
    'TN_XMLDiscussionTemplateContainer_01'


-------------------------------------------------------------------------------
-- The following types do not have predefined type ids.
-- This is necessary due to the fact that type ids for other object types can
-- be set dynamically and to avoid that different types have the same id.

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
    EXEC @l_retValue = p_Tab$new 0, 'Month', @c_TK_VIEW, 0, 3002, 9105, 'OD_tabMonth', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Day', @c_TK_VIEW, 0, 3001, 9100, 'OD_tabDay', 4194304, 'm2.diary.Terminplan_01', @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Address', @c_TK_OBJECT, 0x01012F01, 51, 0, 'OD_tabAddress', 4, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Assortments', @c_TK_OBJECT, 0x01017701, 51, 0, 'OD_tabAssortments', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Branches', @c_TK_OBJECT, 0x01016601, 51, 0, 'OD_tabBranches', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Contacts', @c_TK_OBJECT, 0x01012B01, 51, 0, 'OD_tabContacts', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'PersonUsers', @c_TK_OBJECT, 0x01015E01, 51, 0, 'OD_tabPersonUsers', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Prices', @c_TK_OBJECT, 0x01012101, 51, 0, 'OD_tabPrices', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Properties', @c_TK_OBJECT, 0x01015B01, 51, 0, 'OD_tabProperties', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Versions', @c_TK_OBJECT, 0x01014F01, 51, 0, 'OD_tabVersions', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'Participants', @c_TK_OBJECT, 0x01012001, 51, 0, 'OD_tabParticipants', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, 'ShoppingCart', @c_TK_FUNCTION, 0, 9001, -3000, 'OD_tabShoppingCart', 4194304, NULL, @l_tabId OUTPUT
END
GO
PRINT 'Tabs created.'
GO

-- set the tabs for the object types:
-- body:
BEGIN
-- documents:
-- Document
EXEC p_Type$addTabs 'Document', ''
    , 'Info', 'Attachments', 'References', 'Rights', 'Protocol'

-- diary:
-- Termin
EXEC p_Type$addTabs 'Termin', ''
    , 'Info', 'Contacts', 'References', 'Rights', 'Protocol', 'Attachments'
    , 'Participants'

-- discussions:
-- Discussion
EXEC p_Type$addTabs 'Discussion', ''
    , 'Info', 'Content', 'References', 'Rights', 'Protocol'

-- Thread
EXEC p_Type$addTabs 'Thread', ''
    , 'Info', 'References', 'Rights'

-- Article
EXEC p_Type$addTabs 'Article', ''
    , 'Info', 'References', 'Rights'

-- XMLDiscussionTemplate
EXEC p_Type$addTabs 'XMLDiscussionTemplate', ''
    , 'Info', 'References', 'Rights'

-- diary:
-- TerminplanContainer
EXEC p_Type$addTabs 'TerminplanContainer', ''
    , 'Info', 'Content', 'References', 'Rights'

-- Terminplan
EXEC p_Type$addTabs 'Terminplan', 'Month'
    , 'Info', 'Content', 'References', 'Rights', 'Day', 'Month'

-- discussions:
-- DiscussionContainer
EXEC p_Type$addTabs 'DiscussionContainer', ''
    , 'Info', 'Content', 'References', 'Rights', 'Templates'

-- BlackBoard
EXEC p_Type$addTabs 'BlackBoard', ''
   , 'Info', 'Content', 'References', 'Rights'

-- store:
-- CatalogContainer
EXEC p_Type$addTabs 'Store', ''
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', 'ShoppingCart'

-- Catalog
EXEC p_Type$addTabs 'Catalog', ''
    , 'Info', 'Content', 'References', 'Rights', 'Protocol', 'ShoppingCart', 'Contacts'

-- ProductGroupContainer ??? (ProductGroupProfileContainer)
EXEC p_Type$addTabs 'ProductGroupContainer', ''
    , 'Info', 'Content', 'References', 'Rights'

-- ProductGroupProfile
EXEC p_Type$addTabs 'ProductGroupProfile', ''
    , 'Info', 'References', 'Rights'

-- OrderContainer
EXEC p_Type$addTabs 'OrderContainer', ''
    , 'Info', 'Content', 'References', 'Rights'

-- Order
EXEC p_Type$addTabs 'Order', ''
    , 'Info', 'References', 'Rights'

-- ShoppingCart
EXEC p_Type$addTabs 'ShoppingCart', ''
    , 'Content', 'References', 'Rights'

-- Product
EXEC p_Type$addTabs 'Product', ''
    , 'Info', 'References', 'Rights', 'Prices'
    , 'Assortments', 'Contacts', 'ShoppingCart'

-- ProductGroup
EXEC p_Type$addTabs 'ProductGroup', ''
    , 'Info', 'Content', 'References', 'Rights', 'ShoppingCart'

-- ProductSizeColor (Price)
EXEC p_Type$addTabs 'ProductSizeColor', ''
    , 'Info', 'References', 'Rights'

-- master data:
-- MasterDataContainer
EXEC p_Type$addTabs 'MasterDataContainer', ''
    , 'Info', 'Content', 'References', 'Rights'

-- Person
EXEC p_Type$addTabs 'Person', ''
    , 'Info', 'References', 'Rights', 'Address'

-- Company
EXEC p_Type$addTabs 'Company', ''
    , 'Info', 'References', 'Rights', 'Address', 'Contacts'

-- diary:
-- Participant
EXEC p_Type$addTabs 'Participant', ''
    , 'Info', 'References', 'Rights'

-- Document Management:
-- DocumentContainer
EXEC p_Type$addTabs 'DocumentContainer', ''
    , 'Info', 'Content', 'References', 'Rights', 'Protocol'

-- Store:
-- Property ??? (ProductProperties)
EXEC p_Type$addTabs 'Property', ''
    , 'Info', 'Rights'

-- ProductPropertiesContainer
EXEC p_Type$addTabs 'PropertyContainer', ''
    , 'Info', 'Content', 'Rights'

-- PropertyCategory
EXEC p_Type$addTabs 'PropertyCategory', ''
    , 'Info', 'Rights'

-- PropertyCategoryContainer
EXEC p_Type$addTabs 'PropertyCategoryContainer', ''
    , 'Info', 'Content', 'Rights'

-- store:
-- ProductProfile
EXEC p_Type$addTabs 'ProductProfile', ''
    , 'Info', 'Rights'

-- ProductProfileContainer
EXEC p_Type$addTabs 'ProductProfileContainer', ''
    , 'Info', 'Content', 'Rights'

-- PaymentType
EXEC p_Type$addTabs 'PaymentType', ''
    , 'Info', 'Rights'

-- PaymentTypeContainer
EXEC p_Type$addTabs 'PaymentTypeContainer', ''
    , 'Info', 'Content', 'Rights'

-- ProductBrand
EXEC p_Type$addTabs 'ProductBrand', ''
    , 'Info', 'Rights'

-- ProductCollection
EXEC p_Type$addTabs 'ProductCollection', ''
    , 'Info', 'Rights'
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
