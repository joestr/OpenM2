/******************************************************************************
 * All views regarding a Catalog. <BR>
 *
 * @version     $Id: Catalog_01Views.sql,v 1.10 2003/10/31 00:13:19 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  990507
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given Catalog (incl. rights). <BR>
 */
-- create the new view:
CREATE OR REPLACE VIEW v_Catalog$content
AS
    SELECT  o.*, o2.name AS company
    FROM    v_Container$content o, m2_Catalog_01 c, ibs_Object o2
    WHERE   o.containerId = c.oid(+)
      AND   c.companyOid = o2.oid(+)
;
-- v_Catalog$content


CREATE OR REPLACE VIEW v_Order$content
AS
    SELECT  o.name, o.containerId, v.*, p.productNo
    FROM    ibs_Object o, m2_ShoppingCartEntry_01 v, m2_Product_01 p
    WHERE   o.oid = v.oid
    AND     v.productOid = p.oid
;
-- v_Order$content

CREATE OR REPLACE VIEW v_PredefinedCodes$selList
AS
    SELECT  p.categoryOid, p.oid, o.name, o.state
    FROM    m2_ProductProperties_01 p, ibs_Object o
    WHERE   o.oid = p.oid
;
-- v_PredefinedCodes$selList

CREATE OR REPLACE VIEW v_PriceCodeValues
AS
    SELECT  pr.oid as priceOid, o3.name AS categoryName, 
            prc.categoryOid, pc.codeValues AS productCodeValues, prc.codeValues,
            prc.validForAllValues
    FROM    m2_Price_01 pr, m2_PriceCodeValues_01 prc, ibs_Object o1,
            ibs_Object o2, m2_ProductCodeValues_01 pc, ibs_Object o3
    WHERE   prc.priceOid = pr.oid
      AND   o1.oid = pr.oid
      AND   o2.oid = o1.containerId
      AND   (pc.productOid = o2.containerId AND pc.categoryOid = prc.categoryOid)
      AND   o3.oid = prc.categoryOid
;
-- v_PriceCodeValues

CREATE OR REPLACE VIEW v_PriceContainer$content
AS
    SELECT  crr.*, pr.priceCurrency, pr.price, pr.costCurrency,
            pr.cost, o.name as categoryName, prc.codeValues
            , prc.validForAllValues
            , pr.qty
    FROM v_Container$rightsRead crr, m2_price_01 pr, 
         m2_PriceCodeValues_01 prc, ibs_Object o
    -- join to price
    WHERE crr.oid = pr.oid
      AND prc.priceOid(+) = pr.oid
      AND o.oid(+) = prc.categoryOid
;
-- v_PriceContainer$content

CREATE OR REPLACE VIEW v_ProductCodeValues
AS
    SELECT  pc.productOid, o.name AS categoryName, pc.categoryOid, 
            pc.predefinedCodeOid, pp.values1 as predefinedCodeValues,
            pc.codeValues
    FROM    m2_ProductCodeValues_01 pc, ibs_Object o, m2_ProductProperties_01 pp
    WHERE   o.oid = pc.categoryOid
      AND   o.state = 2
      AND   pp.oid(+) = pc.predefinedCodeOid
;
-- v_ProductCodeValues

CREATE OR REPLACE VIEW v_ProductCollection$content
AS
    SELECT  collectionOid, pcq.id, quantity, o1.name as categoryName, categoryOid, value
    FROM    m2_ProductCollectionQty_01 pcq, m2_ProductCollectionValue_01 pcv,
            ibs_Object o1
    WHERE   pcq.id = pcv.id
      AND   o1.oid = pcv.categoryOid
;
-- v_ProductCollection$content

CREATE OR REPLACE VIEW v_ProductGroup_01$content
AS
SELECT      prod.*,
            minBuyPrice, maxBuyPrice, costCurrencyPrice,
            minSalesPrice, maxSalesPrice, priceCurrency
    FROM    (   SELECT  cr.*, p.productNo, p.thumbAsImage,
                        p.thumbnail, p.image, p.path, p.hasAssortment
                FROM    v_Container$rightsRead cr, m2_Product_01 p
                WHERE   cr.isLink = 0
                    AND cr.oid = p.oid
                UNION
                SELECT  cr.*, p.productNo, p.thumbAsImage,
                        p.thumbnail, p.image, p.path, p.hasAssortment
                FROM    v_Container$rightsRead cr, m2_Product_01 p
                WHERE   cr.isLink = 1
                  AND   cr.linkedObjectId = p.oid 
             ) prod,
            (   SELECT  o1.containerId, o2.userId, max (o2.rights) AS rights,
                        min (psc.price) as minSalesPrice,
                        max (psc.price) as maxSalesPrice,
                        psc.priceCurrency,
                        min (psc.cost) as minBuyPrice,
                        max (psc.cost) as maxBuyPrice,
                        psc.costCurrency AS costCurrencyPrice
                FROM    ibs_Object o1, v_Container$rightsRead o2,
                            m2_Price_01 psc
                WHERE   o2.containerId = o1.oid
                    AND o2.oid = psc.oid
                    AND o2.state = 2
                      -- check if price is valid for current date
--                    AND o2.validUntil >= SYSDATE 
                      -- do not check valid until date because this is the date
                      -- until the price is 'guaranteed' not 'valid'
                    AND psc.validFrom <= SYSDATE
                GROUP BY o1.containerId,
                        psc.priceCurrency,
                        psc.costCurrency,
                        o2.userId 
            )  minvkprice
    WHERE minvkprice.containerId(+) = prod.oid
        AND minvkprice.userId(+) = prod.userId
        -- check rights for user on price if there is a price
        AND B_AND (DECODE (minvkprice.rights, NULL, 2, minvkprice.rights), 2) = 2;
-- v_ProductGroup_01$content

CREATE OR REPLACE VIEW v_ProductPrices
AS
 SELECT crr.*, p.priceCurrency, p.price, p.cost, p.costCurrency, 
        p.userValue1, p.userValue2, prcv.categoryOid, o.name as categoryName,
        prcv.codeValues, prcv.validForAllValues,
        p.qty, p.validFrom
 FROM   v_Container$rights crr, m2_Price_01 p,
        m2_PriceCodeValues_01 prcv, ibs_Object o
 WHERE  crr.oid = p.oid 
   AND  prcv.priceOid(+) = p.oid
   AND  o.oid(+) = prcv.categoryOid
   AND  p.validFrom <= SYSDATE
   AND  crr.validUntil >= SYSDATE
 ;       
 -- v_ProductPrices


CREATE OR REPLACE VIEW v_ShoppingCart$catalogs
AS
    SELECT  wsp.userId, v.catalogOid as oid, o2.name as name
    FROM    m2_ShoppingCartEntry_01 v, ibs_Workspace wsp, ibs_Object o1, ibs_Object o2
    WHERE   wsp.shoppingCart = o1.containerId
      AND   o1.state = 2
      AND   o1.oid = v.oid
      AND   o2.oid = v.catalogOid
;
--v_ShoppingCart$catalogs

/******************************************************************************
 * Gets the data of the objects within a given VersionContainer (incl. rights). <BR>
 */

-- create the new view:
CREATE OR REPLACE VIEW v_ShoppingCart$content
AS
    SELECT  o.*, v.qty as quantity, unitOfQty, packingUnit, productOid, price, priceCurrency,
            productDescription
    FROM    v_Container$rights o, m2_ShoppingCartEntry_01 v 
    WHERE   o.oid = v.oid
;
-- v_ShoppingCart$content

CREATE OR REPLACE VIEW v_ShoppingCartUser$content
AS
    SELECT  wsp.userId, o1.name, v.qty as quantity, unitOfQty, packingUnit, productOid, price, priceCurrency,
            productDescription, v.catalogOid
    FROM    m2_ShoppingCartEntry_01 v, ibs_Workspace wsp, ibs_Object o1
    WHERE   wsp.shoppingCart = o1.containerId
      AND   o1.state = 2
      AND   o1.oid = v.oid
;
-- v_ShoppingCartUser$content

EXIT;
