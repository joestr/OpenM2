/******************************************************************************
 * The content of shopping carts. <BR>
 *
 * @version     $Id: ShoppingCartEntry_01.sql,v 1.6 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 981224
 ******************************************************************************
 */

CREATE TABLE m2_ShoppingCartEntry_01
(
    oid                 OBJECTID     NOT NULL PRIMARY KEY
    ,qty                INT          NULL
    ,unitOfQty          INT          NULL
    ,packingUnit        NAME         NULL
    ,productOid         OBJECTID     NULL
    ,productDescription DESCRIPTION  NULL
    -- redundancy for performance 
    ,catalogOid         OBJECTID     NULL
    ,price              MONEY        NULL -- price for calculation
    -- other prices like netto-price, price per unit
    ,price2             MONEY        NULL 
    ,price3             MONEY        NULL
    ,price4             MONEY        NULL
    ,price5             MONEY        NULL
    ,priceCurrency      NVARCHAR (5) NULL
    ,orderType          NAME         NULL
    ,ordResp            OBJECTID     NULL
    -- text to be shown when select the ordertype
    ,orderText          NAME         NULL
)
GO
