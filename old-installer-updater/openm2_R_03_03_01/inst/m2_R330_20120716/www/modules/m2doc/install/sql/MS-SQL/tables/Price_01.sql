/******************************************************************************
 * The m2_Price_01 table incl. indexes and triggers. <BR>
 * The m2_Price_01 table contains the prices defined for one product.
 *
 * @version     $Id: Price_01.sql,v 1.3 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter  980908
 ******************************************************************************
 */
CREATE TABLE m2_Price_01
(
    oid                 OBJECTID        NOT NULL PRIMARY KEY -- oid of object in ibs_object
    ,costCurrency 	    NVARCHAR(5)     NULL        -- currency used 
    ,cost               money           NULL        -- cost to  buy product
    ,priceCurrency 	    NVARCHAR(5)     NULL        -- currency used 
    ,price     	        money           NULL        -- price of the product
    ,userValue1         money           NULL        -- user defined price (oldcost)
    ,userValue2         money           NULL        -- user defined price (oldprice)
    ,validFrom          DATETIME NULL   DEFAULT GETDATE()
    ,qty                INT             NULL        -- when price depends on quantity    
)
GO
-- m2_Price_01
-- access indices:
