-------------------------------------------------------------------------------
-- Create entries for type dependencies. <BR>
-- This script stores in the database all information regarding which objects
-- may be contained in which other objects.
--
-- @version     $Id: createMayContainEntries.sql,v 1.9 2004/01/16 00:40:44 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pim2_createMayContainEntries');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pim2_createMayContainEntries ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE c_NOT_OK INT;
    DECLARE c_ALL_RIGHT INT;
    DECLARE l_retValue INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
	SET c_NOT_OK = 0;
	SET c_ALL_RIGHT = 1;
    -- initialize local variables:
	SET l_retValue = c_ALL_RIGHT;

    -- BusinessObject:
    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Document');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Discussion');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'TerminplanContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Terminplan');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'DiscussionContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'BlackBoard');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Store');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Catalog');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'MasterDataContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'DocumentContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- Container:
    CALL IBSDEV1.p_MayContain$new('Container', 'Document');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'Discussion');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'TerminplanContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'Terminplan');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'DiscussionContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'BlackBoard');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'Store');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'Catalog');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'MasterDataContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'DocumentContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- ConnectorContainer:
  
    -- DocumentTemplateContainer:

    -- ExportContainer:
    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Document');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DocumentContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Termin');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Discussion');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Thread');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Article');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'TerminplanContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Terminplan');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DiscussionContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'BlackBoard');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Store');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Catalog');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductGroupProfile');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'OrderContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Order');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ShoppingCart');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Product');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ShoppingCartLine');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'OverlapContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductGroup');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ParticipantContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductSizeColorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductSizeColor');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'MasterDataContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Person');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PersonContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Company');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Participant');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Address');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PersonUserContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PersonSearchContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
END;
-- pim2_createMayContainEntries


-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pim2_createMayContainEntries2');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pim2_createMayContainEntries2 ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;
    DECLARE c_NOT_OK INT;
    DECLARE c_ALL_RIGHT INT;
    DECLARE l_retValue INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
	SET c_NOT_OK = 0;
	SET c_ALL_RIGHT = 1;
    -- initialize local variables:
	SET l_retValue = c_ALL_RIGHT;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'SelectUserContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'SelectCompanyContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductGroupContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductSizeColorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductSizeColor');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Property');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PropertyContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductProfile');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductProfileContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PaymentType');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PaymentTypeContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductBrand');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductBrandContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductCollection');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ProductCollectionContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PropertyCategory');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'PropertyCategoryContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DiscXMLViewer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'XMLDiscussion');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- PersonSearchContainer:
    CALL IBSDEV1.p_MayContain$new('PersonSearchContainer', 'Person');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- User:
    CALL IBSDEV1.p_MayContain$new('User', 'Person');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Store:
    CALL IBSDEV1.p_MayContain$new('Store', 'Catalog');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Store', 'Store');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Catalog:
    CALL IBSDEV1.p_MayContain$new('Catalog', 'ProductGroup');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- OrderContainer:
    CALL IBSDEV1.p_MayContain$new('OrderContainer', 'Order');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ProductSizeColorContainer:
    CALL IBSDEV1.p_MayContain$new('ProductSizeColorContainer', 'ProductSizeColor');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ProductBrandContainer:
    CALL IBSDEV1.p_MayContain$new('ProductBrandContainer', 'ProductBrand');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ProductCollectionContainer:
    CALL IBSDEV1.p_MayContain$new('ProductCollectionContainer', 'ProductCollection');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ProductGroupContainer:
    CALL IBSDEV1.p_MayContain$new('ProductGroupContainer', 'ProductGroupProfile');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ProductGroup:
    CALL IBSDEV1.p_MayContain$new('ProductGroup', 'Product');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ProductProfileContainer:
    CALL IBSDEV1.p_MayContain$new('ProductProfileContainer', 'ProductProfile');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- PaymentTypeContainer:
    CALL IBSDEV1.p_MayContain$new('PaymentTypeContainer', 'PaymentType');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- PropertyContainer:
    CALL IBSDEV1.p_MayContain$new('PropertyContainer', 'Property');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- PropertyCategoryContainer:
    CALL IBSDEV1.p_MayContain$new('PropertyCategoryContainer', 'PropertyCategory');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ParticipantContainer:
    CALL IBSDEV1.p_MayContain$new('ParticipantContainer', 'Participant');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- TerminplanContainer:
    CALL IBSDEV1.p_MayContain$new('TerminplanContainer', 'Terminplan');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Terminplan:
    CALL IBSDEV1.p_MayContain$new('Terminplan', 'Termin');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Article:
    CALL IBSDEV1.p_MayContain$new('Article', 'Article');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- BlackBoard:
    CALL IBSDEV1.p_MayContain$new('BlackBoard', 'Article');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- DiscussionContainer:
    CALL IBSDEV1.p_MayContain$new('DiscussionContainer', 'Discussion');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('DiscussionContainer', 'BlackBoard');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('DiscussionContainer', 'XMLDiscussion');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Discussion:
    CALL IBSDEV1.p_MayContain$new('Discussion', 'Thread');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Thread:
    CALL IBSDEV1.p_MayContain$new('Thread', 'Article');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- DocumentContainer:
    CALL IBSDEV1.p_MayContain$new('DocumentContainer', 'Document');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('DocumentContainer', 'File');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('DocumentContainer', 'Url');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('DocumentContainer', 'Note');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- MasterDataContainer:
    CALL IBSDEV1.p_MayContain$new('MasterDataContainer', 'Company');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('MasterDataContainer', 'Person');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- PersonContainer:
    CALL IBSDEV1.p_MayContain$new('PersonContainer', 'Person');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- DiscXMLViewer:
    CALL IBSDEV1.p_MayContain$new('DiscXMLViewer', 'DiscXMLViewer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- XMLDiscussion:
    CALL IBSDEV1.p_MayContain$new('XMLDiscussion', 'DiscXMLViewer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- XMLDiscussionTemplateContainer:
    CALL IBSDEV1.p_MayContain$new('XMLDiscussionTemplateContainer', 'XMLDiscussionTemplate');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- show count messages again:
END;
-- pim2_createMayContainEntries2

-- execute procedures:
CALL IBSDEV1.pim2_createMayContainEntries;
CALL IBSDEV1.pim2_createMayContainEntries2;
-- delete procedures:
CALL IBSDEV1.p_dropProc ('pim2_createMayContainEntries');
CALL IBSDEV1.p_dropProc ('pim2_createMayContainEntries2');
