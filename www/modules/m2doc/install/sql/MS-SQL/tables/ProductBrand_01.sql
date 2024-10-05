/******************************************************************************
 * The brand of a product. <BR>
 *
 * @version     $Id: ProductBrand_01.sql,v 1.3 2003/10/31 00:13:05 klaus Exp $
 *
 * @author      Bernhard Walter (BW) 981222
 ******************************************************************************
 */

GO

CREATE TABLE m2_ProductBrand_01
(
    oid                 OBJECTID    NOT NULL PRIMARY KEY    -- oid of ibs_object
    ,image	            DESCRIPTION NULL                    -- image of the product
)
GO

