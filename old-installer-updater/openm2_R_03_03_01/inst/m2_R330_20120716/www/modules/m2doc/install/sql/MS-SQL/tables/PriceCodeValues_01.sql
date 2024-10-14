/******************************************************************************
 * This table contains tuples which define the relationship between
 * a price and the code values of a product (colors, sizes, etc.). <BR>
 *
 * @version     $Id: PriceCodeValues_01.sql,v 1.4 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 981222
 ******************************************************************************
 */

CREATE TABLE m2_PriceCodeValues_01
(
    priceOid            OBJECTID        NOT NULL,
    categoryOid         OBJECTID        NOT NULL,
    validForAllValues   BOOL            NOT NULL,
    codeValues          NVARCHAR(255)   NULL
)
GO
