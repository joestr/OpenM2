-------------------------------------------------------------------------------
-- The m2_Product_01 table incl. indexes and triggers. <BR>
-- The m2_Product_01 table contains the values for the base object Product_01.
-- Currenty the property container (propertyOid, values) is for performance
-- reasons in this table.
-- It defines the attributes (colors, sizes) for this product. If more
-- categories are needed the table and the procedures have to be extended.
--
-- @version     $Id: Product_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCT_01
(
    PRODUCTNO       VARCHAR (63),           -- product number
    EAN             VARCHAR (63),           -- ean number
    AVAILABLEFROM   TIMESTAMP,              -- product availability
    UNITOFQTY       INTEGER,                -- unit
    PACKINGUNIT     VARCHAR (63),           -- packaging unit
    THUMBASIMAGE    SMALLINT NOT NULL,      -- if thumbNail is just a 
                                            -- smaller image
    THUMBNAIL       VARCHAR (63),           -- thumbnail image
    IMAGE           VARCHAR (63),           -- product picture
    STOCK           VARCHAR (63),           -- statement about stock
    HASASSORTMENT   INTEGER,                -- if product has an assortment
    CREATED         INTEGER,                -- the state of the tuple
    PATH            VARCHAR (20),           -- additional path info
    OID             CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- oid of ibs_object
    PRODUCTDESCRIPTION CLOB(2047M) NOT NULL, -- product description for 
                                            -- long descriptions
    PRODUCTPROFILEOID CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
                                            -- the product profile used
    BRANDNAMEOID    CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
                                            -- the name of the brand
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRODUCT_01 ADD PRIMARY KEY (OID);

-- Create index statements:
CREATE INDEX IBSDEV1.I_PROD_01PRODUCTNO ON IBSDEV1.M2_PRODUCT_01
    (PRODUCTNO ASC);
