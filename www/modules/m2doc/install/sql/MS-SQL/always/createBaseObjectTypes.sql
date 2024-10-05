/******************************************************************************
 * Create all business object types within m2. <BR>
 *
 * @version     $Id: createBaseObjectTypes.sql,v 1.70 2010/01/13 16:41:14 rburgermann Exp $
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
EXEC p_Type$newLang 0x01010100, N'BusinessObject', 0, 1, 1, 0, 1, N'Document',
    N'm2.doc.Document_01', @c_languageId, N'TN_Document_01'

-- diary:
-- Termin
EXEC p_Type$newLang 0x01010200, N'BusinessObject', 0, 1, 1, 0, 1, N'Termin',
    N'm2.diary.Termin_01', @c_languageId, N'TN_Termin_01'


-- discussions:
-- Discussion
EXEC p_Type$newLang 0x01010300, N'Container', 1, 1, 1, 0, 0, N'Discussion',
    N'm2.bbd.Discussion_01', @c_languageId, N'TN_Discussion_01'

-- Thread
EXEC p_Type$newLang 0x01010400, N'Container', 1, 1, 1, 0, 1, N'Thread',
    N'm2.bbd.Thread_01', @c_languageId, N'TN_Thread_01'


-- Article
EXEC p_Type$newLang 0x01010500, N'Container', 1, 1, 1, 0, 1, N'Article',
    N'm2.bbd.Article_01', @c_languageId, N'TN_Article_01'

-- XMLDiscussion
EXEC p_Type$newLang 0x01010320, N'Discussion',   1, 1, 1, 0, 1, N'XMLDiscussion',
    N'm2.bbd.XMLDiscussion_01', @c_languageId, N'TN_XMLDiscussion_01'
-- XMLDiscussionTemplate
EXEC p_Type$newLang 0x01010310, N'BusinessObject', 1, 1, 1, 0, 1, N'XMLDiscussionTemplate',
    N'm2.bbd.XMLDiscussionTemplate_01', @c_languageId,
    N'TN_XMLDiscussionTemplate_01'

-- diary:
-- TerminplanContainer
EXEC p_Type$newLang 0x01010600, N'Container', 1, 1, 0, 1, 0, N'TerminplanContainer',
    N'm2.diary.TerminplanContainer_01', @c_languageId,
    N'TN_TerminplanContainer_01'

-- Terminplan
EXEC p_Type$newLang 0x01010700, N'Container', 1, 1, 0, 1, 0, N'Terminplan',
    N'm2.diary.Terminplan_01', @c_languageId, N'TN_Terminplan_01'

-- discussions:
-- DiscussionContainer
EXEC p_Type$newLang 0x01010900, N'Container', 1, 1, 0, 1, 0, N'DiscussionContainer',
    N'm2.bbd.DiscussionContainer_01', @c_languageId, N'TN_DiscussionContainer_01'

-- BlackBoard
EXEC p_Type$newLang 0x01010A00, N'Container', 1, 1, 1, 0, 1, N'BlackBoard',
    N'm2.bbd.BlackBoard_01', @c_languageId, N'TN_BlackBoard_01'

-- store:
-- CatalogContainer
EXEC p_Type$newLang 0x01010B00, N'Container', 1, 1, 0, 1, 0, N'Store',
    N'm2.store.CatalogContainer_01', @c_languageId, N'TN_CatalogContainer_01'

-- Catalog
EXEC p_Type$newLang 0x01010C00, N'Container', 1, 1, 1, 1, 1, N'Catalog',
    N'm2.store.Catalog_01', @c_languageId, N'TN_Catalog_01'

-- ProductGroupContainer  ??? (ProductGroupProfileContainer)
EXEC p_Type$newLang 0x01010D00, N'Container', 1, 1, 0, 1, 0, N'ProductGroupContainer',
    N'm2.store.ProductGroupProfileContainer_01', @c_languageId,
    N'TN_ProductGroupProfileContainer_01'

-- ProductGroupProfile
EXEC p_Type$newLang 0x01010E00, N'Container', 1, 1, 1, 0, 1, N'ProductGroupProfile',
    N'm2.store.ProductGroupProfile_01', @c_languageId,
    N'TN_ProductGroupProfile_01'

-- OrderContainer
EXEC p_Type$newLang 0x01011200, N'Container', 1, 1, 0, 1, 0, N'OrderContainer',
    N'm2.store.OrderContainer_01', @c_languageId, N'TN_OrderContainer_01'

-- Order
EXEC p_Type$newLang 0x01011300, N'BusinessObject', 0, 1, 0, 0, 0, N'Order',
    N'm2.store.Order_01', @c_languageId, N'TN_Order_01'

-- ShoppingCart
EXEC p_Type$newLang 0x01011400, N'Container', 1, 1, 0, 1, 0, N'ShoppingCart',
    N'm2.store.ShoppingCart_01', @c_languageId, N'TN_ShoppingCart_01'

-- Product
EXEC p_Type$newLang 0x01011500, N'BusinessObject', 0, 1, 1, 0, 1, N'Product',
    N'm2.store.Product_01', @c_languageId, N'TN_Product_01'

/* currently not available
-- Size
EXEC p_Type$newLang 0x01011600, N'BusinessObject', 0, 1, 0, 0, 0, N'Size',
    N'm2.store.Size_01', @c_languageId, N'TN_Size_01'
-- Color
EXEC p_Type$newLang 0x01011700, N'BusinessObject', 0, 1, 0, 0, 0, N'Color',
    N'm2.store.Color_01', @c_languageId, N'TN_Color_01'
*/
-- ShoppingCartLine
EXEC p_Type$newLang 0x01011900, N'BusinessObject', 0, 1, 0, 0, 0, N'ShoppingCartLine',
    N'm2.store.ShoppingCartLine_01', @c_languageId, N'TN_ShoppingCartLine_01'

-- Diary:
-- OverlapContainer
EXEC p_Type$newLang 0x01011A00, N'Container', 1, 1, 0, 0, 0, N'OverlapContainer',
    N'm2.diary.OverlapContainer_01', @c_languageId, N'TN_OverlapContainer_01'

-- store:
-- ProductGroup
EXEC p_Type$newLang 0x01011F00, N'Container', 1, 1, 1, 1, 1, N'ProductGroup',
    N'm2.store.ProductGroup_01', @c_languageId, N'TN_ProductGroup_01'

-- diary:
-- ParticipantContainer
EXEC p_Type$newLang 0x01012000, N'Container', 1, 1, 0, 0, 0, N'ParticipantContainer',
    N'm2.diary.ParticipantContainer_01', @c_languageId,
    N'TN_ParticipantContainer_01'

-- store:
-- ProductSizeColorContainer (PriceContainer)
EXEC p_Type$newLang 0x01012100, N'Container', 1, 1, 0, 0, 0, N'ProductSizeColorContainer',
    N'm2.store.PriceContainer_01', @c_languageId,
    N'TN_ProductSizeColorContainer_01'

-- ProductSizeColor (Price)
EXEC p_Type$newLang 0x01012200, N'BusinessObject', 0, 1, 0, 0, 0, N'ProductSizeColor',
    N'm2.store.Price_01', @c_languageId, N'TN_ProductSizeColor_01'

-- master data:
-- MasterDataContainer
EXEC p_Type$newLang 0x01012900, N'Container', 1, 1, 0, 1, 0, N'MasterDataContainer',
    N'm2.mad.MasterDataContainer_01', @c_languageId, N'TN_MasterDataContainer_01'

-- Person
EXEC p_Type$newLang 0x01012A00, N'BusinessObject', 0, 1, 1, 0, 1, N'Person',
    N'm2.mad.Person_01', @c_languageId, N'TN_Person_01'

-- PersonContainer
EXEC p_Type$newLang 0x01012B00, N'Container', 1, 1, 0, 0, 0, N'PersonContainer',
    N'm2.mad.PersonContainer_01', @c_languageId, N'TN_PersonContainer_01'
-- Company
EXEC p_Type$newLang 0x01012C00, N'BusinessObject', 0, 1, 1, 0, 1, N'Company',
    N'm2.mad.Company_01', @c_languageId, N'TN_Company_01'

-- diary:
-- Participant
EXEC p_Type$newLang 0x01012E00, N'BusinessObject', 0, 1, 1, 0, 0, N'Participant',
    N'm2.diary.Participant_01', @c_languageId, N'TN_Participant_01'

-- master data:
-- Address
EXEC p_Type$newLang 0x01012F00, N'BusinessObject', 0, 1, 0, 0, 1, N'Address',
    N'm2.mad.Address_01', @c_languageId, N'TN_Address_01'

-- Document Management:
-- DocumentContainer
EXEC p_Type$newLang 0x01014B00, N'Container', 1, 1, 1, 1, 1, N'DocumentContainer',
    N'm2.doc.DocumentContainer_01', @c_languageId, N'TN_DocumentContainer_01'

-- Store:
-- Property ??? (ProductProperties)
EXEC p_Type$newLang 0x01015A00, N'BusinessObject', 0, 1, 0, 0, 0, N'Property',
    N'm2.store.ProductProperties_01', @c_languageId, N'TN_ProductProperties_01'

-- ProductPropertiesContainer
EXEC p_Type$newLang 0x01015B00, N'Container', 1, 1, 0, 1, 0, N'PropertyContainer',
    N'm2.store.ProductPropertiesContainer_01', @c_languageId,
    N'TN_ProductPropertiesContainer_01'

-- master data:
-- PersonUserContainer
EXEC p_Type$newLang 0x01015E00, N'Container', 1, 1, 0, 0, 0, N'PersonUserContainer',
    N'm2.mad.PersonUserContainer_01', @c_languageId, N'TN_PersonUserContainer_01'

-- store:
-- PropertyCategory
EXEC p_Type$newLang 0x01015F00, N'BusinessObject', 0, 1, 0, 0, 0, N'PropertyCategory',
    N'm2.store.PropertyCategory_01', @c_languageId, N'TN_PropertyCategory_01'

-- PropertyCategoryContainer
EXEC p_Type$newLang 0x01016000, N'Container', 1, 1, 0, 1, 0, N'PropertyCategoryContainer',
    N'm2.store.PropertyCategoryContainer_01', @c_languageId,
    N'TN_PropertyCategoryContainer_01'

-- store:
-- ProductProfile
EXEC p_Type$newLang 0x01016C00, N'BusinessObject', 0, 1, 0, 0, 0, N'ProductProfile',
    N'm2.store.ProductProfile_01', @c_languageId, N'TN_ProductProfile_01'

-- ProductProfileContainer
EXEC p_Type$newLang 0x01016D00, N'Container', 1, 1, 0, 1, 0, N'ProductProfileContainer',
    N'm2.store.ProductProfileContainer_01', @c_languageId,
    N'TN_ProductProfileContainer_01'

-- PaymentType
EXEC p_Type$newLang 0x01016C10, N'BusinessObject', 0, 1, 0, 0, 0, N'PaymentType',
    N'm2.store.PaymentType_01', @c_languageId, N'TN_PaymentType_01'

-- PaymentTypeContainer
EXEC p_Type$newLang 0x01016D10, N'Container', 1, 1, 0, 1, 0, N'PaymentTypeContainer',
    N'm2.store.PaymentTypeContainer_01', @c_languageId,
    N'TN_PaymentTypeContainer_01'

-- ProductBrand
EXEC p_Type$newLang 0x01017100, N'BusinessObject', 0, 1, 0, 0, 0, N'ProductBrand',
    N'm2.store.ProductBrand_01', @c_languageId, N'TN_ProductBrand_01'

-- ProductProfileContainer
EXEC p_Type$newLang 0x01017200, N'Container', 1, 1, 0, 1, 0, N'ProductBrandContainer',
    N'm2.store.ProductBrandContainer_01', @c_languageId,
    N'TN_ProductBrandContainer_01'

-- DiscXMLViewer
EXEC p_Type$newLang 0x01017510, N'XMLViewer', 1, 1, 1, 0, 1, N'DiscXMLViewer',
    N'm2.bbd.DiscXMLViewer_01', @c_languageId, N'TN_DiscXMLViewer_01'

-- Store:
-- ProductCollection
EXEC p_Type$newLang 0x01017600, N'BusinessObject', 0, 1, 0, 0, 0, N'ProductCollection',
    N'm2.store.ProductCollection_01', @c_languageId, N'TN_ProductCollection_01'

-- ProductCollectionContainer
EXEC p_Type$newLang 0x01017700, N'Container', 1, 1, 0, 1, 0,
    N'ProductCollectionContainer',
    N'm2.store.ProductCollectionContainer_01', @c_languageId,
    N'TN_ProductCollectionContainer_01'
-- SelectUserContainer
EXEC p_Type$newLang 0x01017800, N'Container', 1, 1, 0, 0, 0, N'SelectUserContainer',
    N'm2.store.SelectUserContainer_01', @c_languageId,
    N'TN_SelectUserContainer_01'

-- SelectCompanyContainer
EXEC p_Type$newLang 0x01017B00, N'Container', 1, 1, 0, 0, 0, N'SelectCompanyContainer',
    N'm2.store.SelectCompanyContainer_01', @c_languageId,
    N'TN_SelectCompanyContainer_01'

-- XMLDiscussionTemplateContainer
EXEC p_Type$newLang 0x01017d10, N'DocumentTemplateContainer', 1, 1, 1, 0, 0,
    N'XMLDiscussionTemplateContainer',
    N'm2.bbd.XMLDiscussionTemplateContainer_01', @c_languageId,
    N'TN_XMLDiscussionTemplateContainer_01'


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
    EXEC @l_retValue = p_Tab$new 0, N'Month', @c_TK_VIEW, 0, 3002, 9105, N'OD_tabMonth', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Day', @c_TK_VIEW, 0, 3001, 9100, N'OD_tabDay', 4194304, 'm2.diary.Terminplan_01', @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Address', @c_TK_OBJECT, 0x01012F01, 51, 0, N'OD_tabAddress', 4, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Assortments', @c_TK_OBJECT, 0x01017701, 51, 0, N'OD_tabAssortments', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Branches', @c_TK_OBJECT, 0x01016601, 51, 0, N'OD_tabBranches', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Contacts', @c_TK_OBJECT, 0x01012B01, 51, 0, N'OD_tabContacts', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'PersonUsers', @c_TK_OBJECT, 0x01015E01, 51, 0, N'OD_tabPersonUsers', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Prices', @c_TK_OBJECT, 0x01012101, 51, 0, N'OD_tabPrices', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Properties', @c_TK_OBJECT, 0x01015B01, 51, 0, N'OD_tabProperties', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Versions', @c_TK_OBJECT, 0x01014F01, 51, 0, N'OD_tabVersions', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'Participants', @c_TK_OBJECT, 0x01012001, 51, 0, N'OD_tabParticipants', 4194304, NULL, @l_tabId OUTPUT
    EXEC @l_retValue = p_Tab$new 0, N'ShoppingCart', @c_TK_FUNCTION, 0, 9001, -3000, N'OD_tabShoppingCart', 4194304, NULL, @l_tabId OUTPUT
END
GO
PRINT 'Tabs created.'
GO

-- set the tabs for the object types:
-- body:
BEGIN
-- documents:
-- Document
EXEC p_Type$addTabs N'Document', N''
    , N'Info', N'Attachments', N'References', N'Rights', N'Protocol'

-- diary:
-- Termin
EXEC p_Type$addTabs N'Termin', N''
    , N'Info', N'Contacts', N'References', N'Rights', N'Protocol', N'Attachments'
    , N'Participants'

-- discussions:
-- Discussion
EXEC p_Type$addTabs N'Discussion', N''
    , N'Info', N'Content', N'References', N'Rights', N'Protocol'

-- Thread
EXEC p_Type$addTabs N'Thread', N''
    , N'Info', N'References', N'Rights'

-- Article
EXEC p_Type$addTabs N'Article', N''
    , N'Info', N'References', N'Rights'

-- XMLDiscussionTemplate
EXEC p_Type$addTabs N'XMLDiscussionTemplate', N''
    , N'Info', N'References', N'Rights'

-- diary:
-- TerminplanContainer
EXEC p_Type$addTabs N'TerminplanContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- Terminplan
EXEC p_Type$addTabs N'Terminplan', N'Month'
    , N'Info', N'Content', N'References', N'Rights', N'Day', N'Month'

-- discussions:
-- DiscussionContainer
EXEC p_Type$addTabs N'DiscussionContainer', N''
    , N'Info', N'Content', N'References', N'Rights', N'Templates'

-- BlackBoard
EXEC p_Type$addTabs N'BlackBoard', N''
   , N'Info', N'Content', N'References', N'Rights'

-- store:
-- CatalogContainer
EXEC p_Type$addTabs N'Store', N''
    , N'Info', N'Content', N'References', N'Rights', N'Protocol', N'ShoppingCart'

-- Catalog
EXEC p_Type$addTabs N'Catalog', N''
    , N'Info', N'Content', N'References', N'Rights', N'Protocol', N'ShoppingCart', N'Contacts'

-- ProductGroupContainer ??? (ProductGroupProfileContainer)
EXEC p_Type$addTabs N'ProductGroupContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- ProductGroupProfile
EXEC p_Type$addTabs N'ProductGroupProfile', N''
    , N'Info', N'References', N'Rights'

-- OrderContainer
EXEC p_Type$addTabs N'OrderContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- Order
EXEC p_Type$addTabs N'Order', N''
    , N'Info', N'References', N'Rights'

-- ShoppingCart
EXEC p_Type$addTabs N'ShoppingCart', N''
    , N'Content', N'References', N'Rights'

-- Product
EXEC p_Type$addTabs N'Product', N''
    , N'Info', N'References', N'Rights', N'Prices'
    , N'Assortments', N'Contacts', N'ShoppingCart'

-- ProductGroup
EXEC p_Type$addTabs N'ProductGroup', N''
    , N'Info', N'Content', N'References', N'Rights', N'ShoppingCart'

-- ProductSizeColor (Price)
EXEC p_Type$addTabs N'ProductSizeColor', N''
    , N'Info', N'References', N'Rights'

-- master data:
-- MasterDataContainer
EXEC p_Type$addTabs N'MasterDataContainer', N''
    , N'Info', N'Content', N'References', N'Rights'

-- Person
EXEC p_Type$addTabs N'Person', N''
    , N'Info', N'References', N'Rights', N'Address'

-- Company
EXEC p_Type$addTabs N'Company', N''
    , N'Info', N'References', N'Rights', N'Address', N'Contacts'

-- diary:
-- Participant
EXEC p_Type$addTabs N'Participant', N''
    , N'Info', N'References', N'Rights'

-- Document Management:
-- DocumentContainer
EXEC p_Type$addTabs N'DocumentContainer', N''
    , N'Info', N'Content', N'References', N'Rights', N'Protocol'

-- Store:
-- Property ??? (ProductProperties)
EXEC p_Type$addTabs N'Property', N''
    , N'Info', N'Rights'

-- ProductPropertiesContainer
EXEC p_Type$addTabs N'PropertyContainer', N''
    , N'Info', N'Content', N'Rights'

-- PropertyCategory
EXEC p_Type$addTabs N'PropertyCategory', N''
    , N'Info', N'Rights'

-- PropertyCategoryContainer
EXEC p_Type$addTabs N'PropertyCategoryContainer', N''
    , N'Info', N'Content', N'Rights'

-- store:
-- ProductProfile
EXEC p_Type$addTabs N'ProductProfile', N''
    , N'Info', N'Rights'

-- ProductProfileContainer
EXEC p_Type$addTabs N'ProductProfileContainer', N''
    , N'Info', N'Content', N'Rights'

-- PaymentType
EXEC p_Type$addTabs N'PaymentType', N''
    , N'Info', N'Rights'

-- PaymentTypeContainer
EXEC p_Type$addTabs N'PaymentTypeContainer', N''
    , N'Info', N'Content', N'Rights'

-- ProductBrand
EXEC p_Type$addTabs N'ProductBrand', N''
    , N'Info', N'Rights'

-- ProductCollection
EXEC p_Type$addTabs N'ProductCollection', N''
    , N'Info', N'Rights'
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
