/******************************************************************************
 * All views regarding a Catalog. <BR>
 *
 * @version     $Id: Catalog_01Views.sql,v 1.12 2006/01/19 15:56:47 klreimue Exp $
 *
 * @author      Bernhard Walter (BW)  980809
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given Catalog (incl. rights). <BR>
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_Catalog$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_Catalog$content
GO

-- create the new view:
CREATE VIEW v_Catalog$content
AS
    SELECT  o.*, o2.name AS company
    FROM    v_Container$content o 
            LEFT OUTER JOIN m2_Catalog_01 c ON o.containerId = c.oid
            LEFT OUTER JOIN ibs_Object o2 ON o2.oid = c.companyOid
GO 
-- v_Catalog$content



IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_Order$content')
	BEGIN
		DROP  View v_Order$content
	END
GO

CREATE View v_Order$content
as
    SELECT  o.name, o.containerId, v.*, p.productNo
    FROM    ibs_Object o, m2_ShoppingCartEntry_01 v, m2_Product_01 p
    WHERE   o.oid = v.oid
    AND     v.productOid = p.oid
GO

GRANT SELECT ON v_Order$content TO PUBLIC

GO 
-- v_Order$content

IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_PredefinedCodes$selList')
	BEGIN
		DROP  View v_PredefinedCodes$selList
	END
GO

CREATE View v_PredefinedCodes$selList
as
    SELECT  p.categoryOid, p.oid, o.name, o.state
    FROM    m2_ProductProperties_01 p
    JOIN    ibs_Object o
    ON      o.oid = p.oid
GO

GRANT SELECT ON v_PredefinedCodes$selList TO PUBLIC
GO
-- v_PredefinedCodes$selList

IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_PriceCodeValues')
	BEGIN
		DROP  View v_PriceCodeValues
	END
GO

CREATE View v_PriceCodeValues
AS
    SELECT  pr.oid as priceOid, o3.name AS categoryName, 
            prc.categoryOid, pc.codeValues AS productCodeValues, prc.codeValues,
            prc.validForAllValues
    FROM    m2_Price_01 pr
    JOIN    m2_PriceCodeValues_01 prc
    ON      prc.priceOid = pr.oid
    JOIN    ibs_Object o1
    ON      o1.oid = pr.oid
    JOIN    ibs_Object o2
    ON      o2.oid = o1.containerId
    JOIN    m2_ProductCodeValues_01 pc
    ON      (pc.productOid = o2.containerId AND pc.categoryOid = prc.categoryOid)
    JOIN    ibs_Object o3
    ON      o3.oid = prc.categoryOid
GO

GRANT SELECT ON v_PriceCodeValues TO PUBLIC
GO
-- v_PriceCodeValues



if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.v_PriceContainer$content') and sysstat & 0xf = 2)
	drop view #CONFVAR.ibsbase.dbOwner#.v_PriceContainer$content
GO

CREATE VIEW v_PriceContainer$content
AS
    SELECT  crr.*, pr.priceCurrency, pr.price, pr.costCurrency,
            pr.cost, o.name as categoryName, prc.codeValues,
            prc.validForAllValues, pr.qty
    FROM v_Container$rightsRead crr
    -- join to price
    JOIN m2_price_01 pr 
    ON crr.oid = pr.oid
    LEFT OUTER JOIN m2_PriceCodeValues_01 prc
    ON	prc.priceOid = pr.oid
    LEFT OUTER JOIN ibs_Object o
    ON  o.oid = prc.categoryOid
GO
-- v_PriceContainer$content

IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_ProductCodeValues')
	BEGIN
		DROP  View v_ProductCodeValues
	END
GO


CREATE View v_ProductCodeValues
AS
    SELECT  pc.productOid, o.name AS categoryName, pc.categoryOid, 
            pc.predefinedCodeOid, pp.values1 as predefinedCodeValues,
            pc.codeValues
    FROM    m2_ProductCodeValues_01 pc
    JOIN    ibs_Object o
    ON      o.oid = pc.categoryOid
    AND     o.state = 2
    LEFT OUTER JOIN    m2_ProductProperties_01 pp
    ON      pp.oid = pc.predefinedCodeOid
GO

GRANT SELECT ON v_ProductCodeValues TO PUBLIC
GO
-- v_ProductCodeValues


IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_ProductCollection$content')
	BEGIN
		DROP  View v_ProductCollection$content
	END
GO

CREATE View v_ProductCollection$content
as
    SELECT  collectionOid, pcq.id, quantity, o1.name as categoryName, categoryOid, value
    FROM    m2_ProductCollectionQty_01 pcq
    JOIN    m2_ProductCollectionValue_01 pcv
    ON      pcq.id = pcv.id
    JOIN    ibs_Object o1
    ON      o1.oid = pcv.categoryOid
GO

GRANT SELECT ON v_ProductCollection$content TO PUBLIC
GO
-- v_ProductCollection$content

IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_ProductGroup_01$content')
	BEGIN
		DROP  View v_ProductGroup_01$content
	END
GO

CREATE VIEW v_ProductGroup_01$content
AS
    SELECT  cr.oid, cr.name, cr.description, cr.icon, cr.isNew, cr.isLink,
            cr.linkedObjectId, cr.rights, cr.userId, cr.containerId, cr.flags,
            prod.productNo, prod.thumbAsImage, prod.thumbnail, prod.image,
            minvkprice.minBuyPrice, minvkprice.maxBuyPrice, minvkprice.costCurrencyPrice,
            minvkprice.minSalesPrice, minvkprice.maxSalesPrice, minvkprice.priceCurrency, 
            prod.path, prod.hasAssortment
    FROM    (
                    SELECT oid, name, description, icon, isNew, isLink,
                           linkedObjectId, rights, userId, containerId, flags,
                           linkedObjectId AS productOid
                    FROM   v_Container$rightsRead
                    WHERE  isLink = 1
                    UNION ALL
                    SELECT oid, name, description, icon, isNew, isLink,
                           linkedObjectId, rights, userId, containerId, flags,
                           oid AS productOid
                    FROM   v_Container$rightsRead
                    WHERE  isLink = 0
             ) cr JOIN m2_Product_01 prod ON cr.productOid = prod.oid
             LEFT OUTER JOIN (
                        SELECT  o1.containerId, 
                                o2.userId,
                                psc.priceCurrency, 
                                psc.costCurrency AS costCurrencyPrice, 
                                min (psc.price) AS minSalesPrice,
                                max (psc.price) AS maxSalesPrice, 
                                min (psc.cost)  AS minBuyPrice,
                                max (psc.cost)  AS maxBuyPrice
                	FROM    v_Container$rights o2, m2_Price_01 psc, ibs_Object o1
                        WHERE   psc.validFrom < getDate ()
                          AND   psc.oid = o2.oid
                          AND   o2.state = 2
                          AND   o2.rights > 1
                          AND   o2.containerId = o1.oid

                        GROUP BY o1.containerId, 
                                 psc.priceCurrency,
                                 psc.costCurrency,
                                 o2.userId
    ) minvkprice ON  minvkprice.containerId = prod.oid 
                 AND minvkprice.userId = cr.userId
GO
GRANT SELECT ON v_ProductGroup_01$content TO PUBLIC
GO
-- v_ProductGroup_01$content

IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_ProductPrices')
	BEGIN
		DROP  View v_ProductPrices
	END
GO

CREATE View v_ProductPrices
AS
 SELECT  crr.*, p.priceCurrency, p.price, p.cost, p.costCurrency, 
         p.userValue1, p.userValue2, prcv.categoryOid, o.name as categoryName,
         prcv.codeValues, prcv.validForAllValues,
         p.qty, p.validFrom
 FROM    v_Container$rights crr
 JOIN    m2_Price_01 p
   ON    crr.oid = p.oid
 LEFT OUTER JOIN  m2_PriceCodeValues_01 prcv
   ON    prcv.priceOid = p.oid
 LEFT OUTER JOIN ibs_Object o
   ON    o.oid = prcv.categoryOid
 WHERE   crr.validUntil >= getDate ()
   AND   p.validFrom <= getDate ()

GO

GRANT SELECT ON v_ProductPrices TO PUBLIC
GO
-- v_ProductPrices


IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_ShoppingCart$catalogs')
	BEGIN
		DROP  View v_ShoppingCart$catalogs
	END
GO

CREATE View v_ShoppingCart$catalogs
as
    SELECT  wsp.userId, v.catalogOid as oid, o2.name as name
    FROM    m2_ShoppingCartEntry_01 v, ibs_Workspace wsp, ibs_Object o1, ibs_Object o2
    WHERE   wsp.shoppingCart = o1.containerId
    AND     o1.state = 2
    AND     o1.oid = v.oid
    AND     o2.oid = v.catalogOid
GO

GRANT SELECT ON v_ShoppingCart$catalogs TO PUBLIC
GO
--v_ShoppingCart$catalogs

/******************************************************************************
 * Gets the data of the objects within a given VersionContainer (incl. rights). <BR>
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_ShoppingCart$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_ShoppingCart$content
GO
 
-- create the new view:
CREATE VIEW v_ShoppingCart$content
AS
    SELECT  o.*, v.qty as quantity, unitOfQty, packingUnit, productOid, price, priceCurrency,
            productDescription
    FROM    v_Container$rights o 
            JOIN m2_ShoppingCartEntry_01 v 
            ON o.oid = v.oid 
GO 
-- v_ShoppingCart$content


IF EXISTS (SELECT * FROM sysobjects WHERE type = 'V' AND name = 'v_ShoppingCartUser$content')
	BEGIN
		DROP  View v_ShoppingCartUser$content
	END
GO

CREATE View v_ShoppingCartUser$content
as
    SELECT  wsp.userId, o1.name, v.qty as quantity, unitOfQty, packingUnit, productOid, price, priceCurrency,
            productDescription, v.catalogOid
    FROM    m2_ShoppingCartEntry_01 v, ibs_Workspace wsp, ibs_Object o1
    WHERE   wsp.shoppingCart = o1.containerId
    AND     o1.state = 2
    AND     o1.oid = v.oid
GO

GRANT SELECT ON v_ShoppingCartUser$content TO PUBLIC
GO
-- v_ShoppingCartUser$content
