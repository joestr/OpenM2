-------------------------------------------------------------------------------
-- Jointable between m2_ProductCollection_01 and ProductCollectionValue_01
--
-- @version     $Id: ProductCollectionQty_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTCOLLECTIONQTY_01
(
    ID              INTEGER NOT NULL DEFAULT 0,
    QUANTITY        INTEGER,
    COLLECTIONOID   CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
);
