-------------------------------------------------------------------------------
-- The IBS_DBQUERYCREATOR_01 table incl. indexes. <BR>
-- Table for dynamic search queries on databases.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_DBQUERYCREATOR_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- oid of the object
    connectorOid        CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
                                            -- oid of database connector
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_DBQUERYCREATOR_01 ADD UNIQUE (OID);
