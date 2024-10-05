/******************************************************************************
 * Create entries for type dependencies. <BR>
 * This script stores in the database all information regarding which objects
 * may be contained in which other objects.
 *
 * @version     $Id: createMayContainEntries.sql,v 1.31 2004/01/16 00:40:46 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  001018
 ******************************************************************************
 */

-- mayContain entries:
-- l_retValue := p_MayContain$new (majorTypeCode, minorTypeCode);

DECLARE
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function

BEGIN
-- body:
    -- BusinessObject:
    l_retValue := p_MayContain$new ('BusinessObject', 'Document');
    l_retValue := p_MayContain$new ('BusinessObject', 'Discussion');
    l_retValue := p_MayContain$new ('BusinessObject', 'TerminplanContainer');
    l_retValue := p_MayContain$new ('BusinessObject', 'Terminplan');
    l_retValue := p_MayContain$new ('BusinessObject', 'DiscussionContainer');
    l_retValue := p_MayContain$new ('BusinessObject', 'BlackBoard');
    l_retValue := p_MayContain$new ('BusinessObject', 'Store');
    l_retValue := p_MayContain$new ('BusinessObject', 'Catalog');
    l_retValue := p_MayContain$new ('BusinessObject', 'MasterDataContainer');
    l_retValue := p_MayContain$new ('BusinessObject', 'DocumentContainer');

    -- Container:
    l_retValue := p_MayContain$new ('Container', 'Document');
    l_retValue := p_MayContain$new ('Container', 'Discussion');
    l_retValue := p_MayContain$new ('Container', 'TerminplanContainer');
    l_retValue := p_MayContain$new ('Container', 'Terminplan');
    l_retValue := p_MayContain$new ('Container', 'DiscussionContainer');
    l_retValue := p_MayContain$new ('Container', 'BlackBoard');
    l_retValue := p_MayContain$new ('Container', 'Store');
    l_retValue := p_MayContain$new ('Container', 'Catalog');
    l_retValue := p_MayContain$new ('Container', 'MasterDataContainer');
    l_retValue := p_MayContain$new ('Container', 'DocumentContainer');

    -- ExportContainer:
    l_retValue := p_MayContain$new ('ExportContainer', 'Document');
    l_retValue := p_MayContain$new ('ExportContainer', 'DocumentContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Termin');
    l_retValue := p_MayContain$new ('ExportContainer', 'Discussion');
    l_retValue := p_MayContain$new ('ExportContainer', 'Thread');
    l_retValue := p_MayContain$new ('ExportContainer', 'Article');
    l_retValue := p_MayContain$new ('ExportContainer', 'TerminplanContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Terminplan');
    l_retValue := p_MayContain$new ('ExportContainer', 'DiscussionContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'BlackBoard');
    l_retValue := p_MayContain$new ('ExportContainer', 'Store');
    l_retValue := p_MayContain$new ('ExportContainer', 'Catalog');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductGroupProfile');
    l_retValue := p_MayContain$new ('ExportContainer', 'OrderContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Order');
    l_retValue := p_MayContain$new ('ExportContainer', 'ShoppingCart');
    l_retValue := p_MayContain$new ('ExportContainer', 'Product');
--     l_retValue := p_MayContain$new ('ExportContainer', 'Size');
--     l_retValue := p_MayContain$new ('ExportContainer', 'Color');
    l_retValue := p_MayContain$new ('ExportContainer', 'ShoppingCartLine');
    l_retValue := p_MayContain$new ('ExportContainer', 'OverlapContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductGroup');
    l_retValue := p_MayContain$new ('ExportContainer', 'ParticipantContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductSizeColorContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductSizeColor');
    l_retValue := p_MayContain$new ('ExportContainer', 'MasterDataContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Person');
    l_retValue := p_MayContain$new ('ExportContainer', 'PersonContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'Company');
    l_retValue := p_MayContain$new ('ExportContainer', 'Participant');
    l_retValue := p_MayContain$new ('ExportContainer', 'Address');
    l_retValue := p_MayContain$new ('ExportContainer', 'PersonUserContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'PersonSearchContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'SelectUserContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'SelectCompanyContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductGroupContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductSizeColorContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductSizeColor');
    l_retValue := p_MayContain$new ('ExportContainer', 'Property');
    l_retValue := p_MayContain$new ('ExportContainer', 'PropertyContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductProfile');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductProfileContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'PaymentType');
    l_retValue := p_MayContain$new ('ExportContainer', 'PaymentTypeContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductBrand');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductBrandContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductCollection');
    l_retValue := p_MayContain$new ('ExportContainer', 'ProductCollectionContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'PropertyCategory');
    l_retValue := p_MayContain$new ('ExportContainer', 'PropertyCategoryContainer');
    l_retValue := p_MayContain$new ('ExportContainer', 'DiscXMLViewer');
    l_retValue := p_MayContain$new ('ExportContainer', 'XMLDiscussion');

    -- PersonSearchContainer:
    l_retValue := p_MayContain$new ('PersonSearchContainer', 'Person');

    -- User:
    l_retValue := p_MayContain$new ('User', 'Person');

    -- Store:
    l_retValue := p_MayContain$new ('Store', 'Catalog');
    l_retValue := p_MayContain$new ('Store', 'Store');

    -- Catalog:
--    l_retValue := p_MayContain$new ('Catalog', 'ProductGroup');
--  fixed: bug due to wrong type
    l_retValue :=  p_MayContain$new ('Catalog', 'CatalogProductGroup');

    -- OrderContainer:
    l_retValue := p_MayContain$new ('OrderContainer', 'Order');

    -- ProductSizeColorContainer:
    l_retValue := p_MayContain$new ('ProductSizeColorContainer', 'ProductSizeColor');

    -- ProductBrandContainer:
    l_retValue := p_MayContain$new ('ProductBrandContainer', 'ProductBrand');

    -- ProductCollectionContainer:
    l_retValue := p_MayContain$new ('ProductCollectionContainer', 'ProductCollection');

    -- ProductGroupContainer:
--    l_retValue := p_MayContain$new ('ProductGroupContainer', 'ProductGroupProfile');
--  fixed: bug due to wrong type
    l_retValue := p_MayContain$new ('ProductGroupContainer', 'ProductGroup');

    -- ProductGroup:
--    l_retValue := p_MayContain$new ('ProductGroup', 'Product');
--  fixed: bug due to wrong type
    l_retValue := p_MayContain$new ('CatalogProductGroup', 'Product');

    -- ProductProfileContainer:
    l_retValue := p_MayContain$new ('ProductProfileContainer', 'ProductProfile');

    -- PaymentTypeContainer:
    l_retValue := p_MayContain$new ('PaymentTypeContainer', 'PaymentType');

    -- PropertyContainer:
    l_retValue := p_MayContain$new ('PropertyContainer', 'Property');

    -- PropertyCategoryContainer:
    l_retValue := p_MayContain$new ('PropertyCategoryContainer', 'PropertyCategory');

    -- ParticipantContainer:
    l_retValue := p_MayContain$new ('ParticipantContainer', 'Participant');

    -- TerminplanContainer:
    l_retValue := p_MayContain$new ('TerminplanContainer', 'Terminplan');

    -- Terminplan:
    l_retValue := p_MayContain$new ('Terminplan', 'Termin');

    -- Article:
    l_retValue := p_MayContain$new ('Article', 'Article');

    -- BlackBoard:
    l_retValue := p_MayContain$new ('BlackBoard', 'Article');

    -- DiscussionContainer:
    l_retValue := p_MayContain$new ('DiscussionContainer', 'Discussion');
    l_retValue := p_MayContain$new ('DiscussionContainer', 'BlackBoard');
    l_retValue := p_MayContain$new ('DiscussionContainer', 'XMLDiscussion');

    -- Discussion:
    l_retValue := p_MayContain$new ('Discussion', 'Thread');

    -- Thread:
    l_retValue := p_MayContain$new ('Thread', 'Article');

    -- DocumentContainer:
    l_retValue := p_MayContain$new ('DocumentContainer', 'Document');
    l_retValue := p_MayContain$new ('DocumentContainer', 'File');
    l_retValue := p_MayContain$new ('DocumentContainer', 'Url');
    l_retValue := p_MayContain$new ('DocumentContainer', 'Note');

    -- MasterDataContainer:
    l_retValue := p_MayContain$new ('MasterDataContainer', 'Company');
    l_retValue := p_MayContain$new ('MasterDataContainer', 'Person');
    --l_retValue := p_MayContain$new ('MasterDataContainer', 'XMLViewer');

    -- PersonContainer:
    l_retValue := p_MayContain$new ('PersonContainer', 'Person');

    -- DiscXMLViewer:
    l_retValue := p_MayContain$new ('DiscXMLViewer', 'DiscXMLViewer');

    -- XMLDiscussion:
    l_retValue := p_MayContain$new ('XMLDiscussion', 'DiscXMLViewer');

    -- XMLDiscussionTemplateContainer:
    l_retValue := p_MayContain$new ('XMLDiscussionTemplateContainer', 'XMLDiscussionTemplate');
END;
/

EXIT;
