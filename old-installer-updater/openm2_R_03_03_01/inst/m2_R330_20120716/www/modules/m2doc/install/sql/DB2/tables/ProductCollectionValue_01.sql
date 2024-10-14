-------------------------------------------------------------------------------
-- Contents the different values within one collection
--
-- @version     $Id: ProductCollectionValue_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTCOLLECTIONVALUE_01
(
    ID              INTEGER NOT NULL DEFAULT 0,
    VALUE           VARCHAR (255),
    CATEGORYOID     CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
);
