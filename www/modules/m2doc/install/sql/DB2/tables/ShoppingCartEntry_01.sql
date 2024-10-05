-------------------------------------------------------------------------------
-- The content of shopping carts. <BR>
--
-- @version     $Id: ShoppingCartEntry_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_SHOPPINGCARTENTRY_01
(
    QTY             INTEGER,
    UNITOFQTY       INTEGER,
    PACKINGUNIT     VARCHAR (63),
    PRODUCTDESCRIPTION VARCHAR (255),
    PRICE           INTEGER,                
                                            -- price for calculation
    -- other prices like netto-price, price per unit
    PRICE2          INTEGER,
    PRICE3          INTEGER,
    PRICE4          INTEGER,
    PRICE5          INTEGER,
    PRICECURRENCY   VARCHAR (5),
    ORDERTYPE       VARCHAR (63),
    -- text to be shown when select the ordertype
    ORDERTEXT       VARCHAR (63),
    OID             CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    PRODUCTOID      CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    CATALOGOID      CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    ORDRESP         CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_SHOPPINGCARTENTRY_01 ADD PRIMARY KEY (OID);
