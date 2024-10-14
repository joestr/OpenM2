/******************************************************************************
 * Create entries for type dependencies. <BR>
 * This script stores in the database all information regarding which objects
 * may be contained in which other objects.
 *
 * @version     $Id: createMayContainEntries.sql,v 1.37 2010/01/13 16:41:13 rburgermann Exp $
 *
 * @author      Klaus Reim?ller (KR)  001018
 ******************************************************************************
 */

-- mayContain entries:
-- l_retValue := p_MayContain$new (majorTypeCode, minorTypeCode);

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT             -- return value of a function

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- BusinessObject:
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'Document'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'Discussion'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'TerminplanContainer'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'Terminplan'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'DiscussionContainer'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'BlackBoard'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'Store'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'Catalog'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'MasterDataContainer'
    EXEC @l_retValue = p_MayContain$new N'BusinessObject', N'DocumentContainer'

    -- Container:
    EXEC @l_retValue = p_MayContain$new N'Container', N'Document'
    EXEC @l_retValue = p_MayContain$new N'Container', N'Discussion'
    EXEC @l_retValue = p_MayContain$new N'Container', N'TerminplanContainer'
    EXEC @l_retValue = p_MayContain$new N'Container', N'Terminplan'
    EXEC @l_retValue = p_MayContain$new N'Container', N'DiscussionContainer'
    EXEC @l_retValue = p_MayContain$new N'Container', N'BlackBoard'
    EXEC @l_retValue = p_MayContain$new N'Container', N'Store'
    EXEC @l_retValue = p_MayContain$new N'Container', N'Catalog'
    EXEC @l_retValue = p_MayContain$new N'Container', N'MasterDataContainer'
    EXEC @l_retValue = p_MayContain$new N'Container', N'DocumentContainer'

    -- ExportContainer:
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Document'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'DocumentContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Termin'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Discussion'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Thread'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Article'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'TerminplanContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Terminplan'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'DiscussionContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'BlackBoard'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Store'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Catalog'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductGroupProfile'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'OrderContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Order'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ShoppingCart'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Product'
--    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Size'
--    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Color'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ShoppingCartLine'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'OverlapContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductGroup'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ParticipantContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductSizeColorContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductSizeColor'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'MasterDataContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Person'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PersonContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Company'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Participant'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Address'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PersonUserContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PersonSearchContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'SelectUserContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'SelectCompanyContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductGroupContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductSizeColorContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductSizeColor'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'Property'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PropertyContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductProfile'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductProfileContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PaymentType'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PaymentTypeContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductBrand'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductBrandContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductCollection'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'ProductCollectionContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PropertyCategory'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'PropertyCategoryContainer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'DiscXMLViewer'
    EXEC @l_retValue = p_MayContain$new N'ExportContainer', N'XMLDiscussion'

    -- PersonSearchContainer:
    EXEC @l_retValue = p_MayContain$new N'PersonSearchContainer', N'Person'

    -- User:
    EXEC @l_retValue = p_MayContain$new N'User', N'Person'

    -- Store:
    EXEC @l_retValue = p_MayContain$new N'Store', N'Catalog'
    EXEC @l_retValue = p_MayContain$new N'Store', N'Store'

    -- Catalog:
    EXEC @l_retValue = p_MayContain$new N'Catalog', N'ProductGroup'

    -- OrderContainer:
    EXEC @l_retValue = p_MayContain$new N'OrderContainer', N'Order'

    -- ProductSizeColorContainer:
    EXEC @l_retValue = p_MayContain$new N'ProductSizeColorContainer', N'ProductSizeColor'

    -- ProductBrandContainer:
    EXEC @l_retValue = p_MayContain$new N'ProductBrandContainer', N'ProductBrand'

    -- ProductCollectionContainer:
    EXEC @l_retValue = p_MayContain$new N'ProductCollectionContainer', N'ProductCollection'

    -- ProductGroupContainer:
    EXEC @l_retValue = p_MayContain$new N'ProductGroupContainer', N'ProductGroupProfile'

    -- ProductGroup:
    EXEC @l_retValue = p_MayContain$new N'ProductGroup', N'Product'

    -- ProductProfileContainer:
    EXEC @l_retValue = p_MayContain$new N'ProductProfileContainer', N'ProductProfile'

    -- PaymentTypeContainer:
    EXEC @l_retValue = p_MayContain$new N'PaymentTypeContainer', N'PaymentType'

    -- PropertyContainer:
    EXEC @l_retValue = p_MayContain$new N'PropertyContainer', N'Property'

    -- PropertyCategoryContainer:
    EXEC @l_retValue = p_MayContain$new N'PropertyCategoryContainer', N'PropertyCategory'

    -- ParticipantContainer:
    EXEC @l_retValue = p_MayContain$new N'ParticipantContainer', N'Participant'

    -- TerminplanContainer:
    EXEC @l_retValue = p_MayContain$new N'TerminplanContainer', N'Terminplan'

    -- Terminplan:
    EXEC @l_retValue = p_MayContain$new N'Terminplan', N'Termin'

    -- Article:
    EXEC @l_retValue = p_MayContain$new N'Article', N'Article'

    -- BlackBoard:
    EXEC @l_retValue = p_MayContain$new N'BlackBoard', N'Article'

    -- DiscussionContainer:
    EXEC @l_retValue = p_MayContain$new N'DiscussionContainer', N'Discussion'
    EXEC @l_retValue = p_MayContain$new N'DiscussionContainer', N'BlackBoard'
    EXEC @l_retValue = p_MayContain$new N'DiscussionContainer', N'XMLDiscussion'

    -- Discussion:
    EXEC @l_retValue = p_MayContain$new N'Discussion', N'Thread'

    -- Thread:
    EXEC @l_retValue = p_MayContain$new N'Thread', N'Article'

    -- DocumentContainer:
    EXEC @l_retValue = p_MayContain$new N'DocumentContainer', N'Document'
    EXEC @l_retValue = p_MayContain$new N'DocumentContainer', N'File'
    EXEC @l_retValue = p_MayContain$new N'DocumentContainer', N'Url'
    EXEC @l_retValue = p_MayContain$new N'DocumentContainer', N'Note'

    -- MasterDataContainer:
    EXEC @l_retValue = p_MayContain$new N'MasterDataContainer', N'Company'
    EXEC @l_retValue = p_MayContain$new N'MasterDataContainer', N'Person'
--    EXEC @l_retValue = p_MayContain$new N'MasterDataContainer', N'XMLViewer'

    -- PersonContainer:
    EXEC @l_retValue = p_MayContain$new N'PersonContainer', N'Person'

    -- DiscXMLViewer:
    EXEC @l_retValue = p_MayContain$new N'DiscXMLViewer', N'DiscXMLViewer'

    -- XMLDiscussion:
    EXEC @l_retValue = p_MayContain$new N'XMLDiscussion', N'DiscXMLViewer'

    -- XMLDiscussionTemplateContainer:
    EXEC @l_retValue = p_MayContain$new N'XMLDiscussionTemplateContainer', N'XMLDiscussionTemplate'
GO


-- show count messages again:
SET NOCOUNT OFF
GO
