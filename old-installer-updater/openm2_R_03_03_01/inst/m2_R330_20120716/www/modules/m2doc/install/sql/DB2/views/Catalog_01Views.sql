------------------------------------------------------------------------------
-- All views regarding a Catalog. <BR>
-- 
-- @version     $Id: Catalog_01Views.sql,v 1.4 2003/10/31 00:12:56 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
------------------------------------------------------------------------------


    -- Gets the data of the objects within a given Catalog (incl. rights). <BR>
    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_Catalog$content');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Catalog$content
AS
    SELECT  o.*, o2.name AS company
    FROM    IBSDEV1.v_Container$content o
    LEFT OUTER JOIN IBSDEV1.m2_Catalog_01 c ON o.containerId = c.oid
    LEFT OUTER JOIN IBSDEV1.ibs_Object o2 ON o2.oid = c.companyOid;
    -- v_Catalog$content

GRANT SELECT ON IBSDEV1.v_Catalog$content TO PUBLIC;

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_Order$content');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Order$content
AS
    SELECT  o.name, o.containerId, v.*, p.productNo
    FROM    IBSDEV1.ibs_Object o, IBSDEV1.m2_ShoppingCartEntry_01 v, IBSDEV1.m2_Product_01 p
    WHERE   o.oid = v.oid
    AND     v.productOid = p.oid;

GRANT SELECT ON IBSDEV1.v_Order$content TO PUBLIC;

    -- v_Order$content

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_PredefinedCodes$selList');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_PredefinedCodes$selList
AS
    SELECT  p.categoryOid, p.oid, o.name, o.state
    FROM    IBSDEV1.m2_ProductProperties_01 p
    JOIN    IBSDEV1.ibs_Object o
    ON      o.oid = p.oid;

GRANT SELECT ON IBSDEV1.v_PredefinedCodes$selList TO PUBLIC;

    -- v_PredefinedCodes$selList


    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_PriceCodeValues');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_PriceCodeValues
AS
    SELECT  pr.oid as priceOid, o3.name AS categoryName, prc.categoryOid, 
            pc.codeValues AS productCodeValues, prc.codeValues, 
            prc.validForAllValues 
    FROM    IBSDEV1.m2_Price_01 pr
    JOIN    IBSDEV1.m2_PriceCodeValues_01 prc 
    ON      prc.priceOid = pr.oid 
    JOIN    IBSDEV1.ibs_Object o1 
    ON      o1.oid = pr.oid 
    JOIN    IBSDEV1.ibs_Object o2 
    ON      o2.oid = o1.containerId 
    JOIN    IBSDEV1.m2_ProductCodeValues_01 pc 
    ON     (pc.productOid = o2.containerId AND pc.categoryOid = prc.categoryOid)
    JOIN    IBSDEV1.ibs_Object o3 
    ON      o3.oid = prc.categoryOid;

GRANT SELECT ON IBSDEV1.v_PriceCodeValues TO PUBLIC;

    -- v_PriceCodeValues


    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_PriceContainer$content');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_PriceContainer$content
AS
    SELECT  crr.*, pr.priceCurrency, pr.price, pr.costCurrency, pr.cost, 
            o.name as categoryName, prc.codeValues, prc.validForAllValues, 
            pr.qty
    FROM    IBSDEV1.v_Container$rightsRead crr 
    JOIN    IBSDEV1.m2_price_01 pr
    ON      crr.oid = pr.oid
    LEFT OUTER JOIN IBSDEV1.m2_PriceCodeValues_01 prc ON prc.priceOid = pr.oid 
    LEFT OUTER JOIN IBSDEV1.ibs_Object o ON  o.oid = prc.categoryOid;
    -- v_PriceContainer$content


GRANT SELECT ON IBSDEV1.v_PriceContainer$content TO PUBLIC;

    -- delete existing view:
CALL IBSDEV1.p_dropView ('v_ProductCodeValues');

    -- create the new view:
CREATE VIEW IBSDEV1.v_ProductCodeValues
AS
    SELECT  pc.productOid, o.name AS categoryName, pc.categoryOid,
            pc.predefinedCodeOid, pp.values1 as predefinedCodeValues,
            pc.codeValues
    FROM    IBSDEV1.m2_ProductCodeValues_01 pc
    JOIN    IBSDEV1.ibs_Object o 
    ON      o.oid = pc.categoryOid 
    AND     o.state = 2
    LEFT OUTER JOIN IBSDEV1.m2_ProductProperties_01 pp 
    ON      pp.oid = pc.predefinedCodeOid;

GRANT SELECT ON IBSDEV1.v_ProductCodeValues TO PUBLIC;

    -- v_ProductCodeValues


    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_ProductCollection$content');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_ProductCollection$content
AS
    SELECT  collectionOid, pcq.id, quantity, o1.name as categoryName, categoryOid, value
    FROM    IBSDEV1.m2_ProductCollectionQty_01 pcq
        JOIN    IBSDEV1.m2_ProductCollectionValue_01 pcv 
        ON      pcq.id = pcv.id
        JOIN    IBSDEV1.ibs_Object o1
        ON      o1.oid = pcv.categoryOid;

GRANT SELECT ON IBSDEV1.v_ProductCollection$content TO PUBLIC;

    -- v_ProductCollection$content


    -- delete existing view:
CALL IBSDEV1.p_dropView ('v_ProductGroup_01$content');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_ProductGroup_01$content
AS
    SELECT  cr.oid, cr.name, cr.description, cr.icon, cr.isNew, cr.isLink,
            cr.linkedObjectId, cr.rights, cr.userId, cr.containerId, cr.flags,
            prod.productNo, prod.thumbAsImage, prod.thumbnail, prod.image,
            minvkprice.minBuyPrice, minvkprice.maxBuyPrice, 
            minvkprice.costCurrencyPrice, minvkprice.minSalesPrice, 
            minvkprice.maxSalesPrice, minvkprice.priceCurrency, prod.path, 
            prod.hasAssortment
    FROM   (
            SELECT oid, name, description, icon, isNew, isLink,linkedObjectId,
                   rights, userId, containerId, flags, 
                   linkedObjectId AS productOid
            FROM   IBSDEV1.v_Container$rightsRead
            WHERE  isLink = 1
            UNION ALL
            SELECT oid, name, description, icon, isNew, isLink,
                   linkedObjectId, rights, userId, containerId, flags,
                   oid AS productOid
            FROM   IBSDEV1.v_Container$rightsRead
            WHERE  isLink = 0
           ) cr JOIN IBSDEV1.m2_Product_01 prod ON cr.productOid = prod.oid
                LEFT OUTER JOIN (
                                 SELECT  o1.containerId, o2.userId, 
                                         psc.priceCurrency, 
                                         psc.costCurrency AS costCurrencyPrice,
                                         min (psc.price) AS minSalesPrice,
                                         max (psc.price) AS maxSalesPrice,
                                         min (psc.cost)  AS minBuyPrice,
                                         max (psc.cost)  AS maxBuyPrice
                                 FROM    IBSDEV1.v_Container$rights o2, IBSDEV1.m2_Price_01 psc,
                                         IBSDEV1.ibs_Object o1 
                                 WHERE   psc.validFrom < CURRENT TIMESTAMP 
                                 AND     psc.oid = o2.oid
                                 AND     o2.state = 2
                                 AND     o2.rights > 1
                                 AND     o2.containerId = o1.oid
                                 GROUP BY o1.containerId, psc.priceCurrency,
                                          psc.costCurrency, o2.userId
                                ) minvkprice 
                ON  minvkprice.containerId = prod.oid
                AND minvkprice.userId = cr.userId;

GRANT SELECT ON IBSDEV1.v_ProductGroup_01$content TO PUBLIC;

    -- v_ProductGroup_01$content

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_ProductPrices');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_ProductPrices
AS
    SELECT  crr.*, p.priceCurrency, p.price, p.cost, p.costCurrency,
            p.userValue1, p.userValue2, prcv.categoryOid, 
            o.name as categoryName, prcv.codeValues, prcv.validForAllValues,
            p.qty, p.validFrom
    FROM    IBSDEV1.v_Container$rights crr
    JOIN    IBSDEV1.m2_Price_01 p
    ON      crr.oid = p.oid
    LEFT OUTER JOIN  IBSDEV1.m2_PriceCodeValues_01 prcv
    ON      prcv.priceOid = p.oid
    LEFT OUTER JOIN IBSDEV1.ibs_Object o
    ON      o.oid = prcv.categoryOid
    WHERE   crr.validUntil >= CURRENT TIMESTAMP
    AND     p.validFrom <= CURRENT TIMESTAMP;

GRANT SELECT ON IBSDEV1.v_ProductPrices TO PUBLIC;

    -- v_ProductPrices

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_ShoppingCart$catalogs');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_ShoppingCart$catalogs
AS
    SELECT  wsp.userId, v.catalogOid as oid, o2.name as name
    FROM    IBSDEV1.m2_ShoppingCartEntry_01 v,
        IBSDEV1.ibs_Workspace wsp, IBSDEV1.ibs_Object o1,
        IBSDEV1.ibs_Object o2
    WHERE   wsp.shoppingCart = o1.containerId
    AND     o1.state = 2
    AND     o1.oid = v.oid
    AND     o2.oid = v.catalogOid;

GRANT SELECT ON IBSDEV1.v_ShoppingCart$catalogs TO PUBLIC;
    --v_ShoppingCart$catalogs


    -- Gets the data of the objects within a given 
    -- VersionContainer (incl. rights). <BR>
    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_ShoppingCart$content');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_ShoppingCart$content
AS
    SELECT  o.*, v.qty as quantity, unitOfQty, packingUnit, productOid, price,
            priceCurrency, productDescription
    FROM    IBSDEV1.v_Container$rights o
    JOIN    IBSDEV1.m2_ShoppingCartEntry_01 v
    ON      o.oid = v.oid;
    -- v_ShoppingCart$content

GRANT SELECT ON IBSDEV1.v_ShoppingCart$content TO PUBLIC;

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('v_ShoppingCartUser$content');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_ShoppingCartUser$content
AS
    SELECT  wsp.userId, o1.name, v.qty as quantity, unitOfQty, packingUnit,
            productOid, price, priceCurrency, productDescription, v.catalogOid
    FROM    IBSDEV1.m2_ShoppingCartEntry_01 v,
        IBSDEV1.ibs_Workspace wsp, IBSDEV1.ibs_Object o1
    WHERE   wsp.shoppingCart = o1.containerId
    AND     o1.state = 2
    AND     o1.oid = v.oid;
    -- v_ShoppingCartUser$content

GRANT SELECT ON IBSDEV1.v_ShoppingCartUser$content TO PUBLIC;
