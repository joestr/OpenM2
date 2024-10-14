-------------------------------------------------------------------------------
-- The m2_Price_01 table incl. indexes and triggers. <BR>
-- The m2_Price_01 table contains the prices defined for one product.
--
-- @version     $Id: Price_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRICE_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- oid of object in ibs_object
    COSTCURRENCY    VARCHAR (5),            -- currency used 
    COST            INTEGER,                -- cost to  buy product
    PRICECURRENCY   VARCHAR (5),            -- currency used
    PRICE           INTEGER,                -- price of the product
    USERVALUE1      INTEGER,                -- user defined price (oldcost)
    USERVALUE2      INTEGER,                -- user defined price (oldprice)
    VALIDFROM       TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
    QTY             INTEGER                 -- when price depends on quantity
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRICE_01 ADD PRIMARY KEY (OID);
