-------------------------------------------------------------------------------
-- This table contains information about a product group
-- in a catalog. <BR>
--
-- @version     $Id: ProductGroup_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTGROUP_01
(
    OID                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    PRODUCTGROUPPROFILEOID CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRODUCTGROUP_01 ADD PRIMARY KEY (OID);
