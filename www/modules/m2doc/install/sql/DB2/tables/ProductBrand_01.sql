-------------------------------------------------------------------------------
-- The brand of a product. <BR>
--
-- @version     $Id: ProductBrand_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTBRAND_01
(
    OID             CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',
                                            -- oid of ibs_object
    IMAGE           VARCHAR (255)           -- image of the product
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRODUCTBRAND_01 ADD PRIMARY KEY (OID);
