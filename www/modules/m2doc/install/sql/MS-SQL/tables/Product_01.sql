/******************************************************************************
 * The m2_Product_01 table incl. indexes and triggers. <BR>
 * The m2_Product_01 table contains the values for the base object Product_01.
 * Currenty the property container (propertyOid, values) is for performance
 * reasons in this table.
 * It defines the attributes (colors, sizes) for this product. If more categories
 * are needed the table and the procedures have to be extended.
 * 
 * @version     $Id: Product_01.sql,v 1.5 2010/01/14 14:06:59 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW)  9808908
 ******************************************************************************
 */

CREATE TABLE m2_Product_01
(
    oid                 OBJECTID      NOT NULL PRIMARY KEY -- oid of ibs_object
    ,productNo	        NAME          NULL        -- product number
    ,ean                NAME          NULL        -- ean number
    ,productDescription NTEXT         NOT NULL    -- product description for long descriptions    
    ,availableFrom      DATETIME      NULL        -- product availability
    ,unitOfQty          INT           NULL        -- unit
    ,packingUnit        NAME          NULL        -- packaging unit
    ,thumbAsImage       BOOL          NOT NULL        -- if thumbNail is just a smaller image
    ,thumbnail	        NAME          NULL        -- thumbnail image
    ,image              NAME          NULL        -- product picture
    ,stock              NAME          NULL       -- statement about stock
    ,hasAssortment      INTEGER       NULL       -- if product has an assortment
    ,productProfileOid  OBJECTID      NULL       -- the product profile used
    ,brandNameOid       OBJECTID      NULL        -- the name of the brand
    ,created            INT           NULL        -- the state of the tuple
    ,path               NVARCHAR(20)  NULL        -- additional path info
)
GO
-- m2_Product_01

-- unique key index:
CREATE UNIQUE INDEX IndexProduct_01Oid ON m2_Product_01 (oid)
GO
-- access indices:
CREATE INDEX IndexProduct_01ProductNo ON m2_Product_01 (productNo)
GO


