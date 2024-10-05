-------------------------------------------------------------------------------
-- The m2_ProfileCategory_01 table incl. indexes and triggers. <BR>
-- The m2_ProfileCategory_01 is the connection between the productprofile
-- and the code categories.
--
-- @version     $Id: ProfileCategory_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PROFILECATEGORY_01
(
    PRODUCTPROFILEOID   CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- oid of object in ibs_object
    CATEGORYOID         CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
                                            -- the categories of codes
);
