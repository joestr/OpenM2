-------------------------------------------------------------------------------
-- The M2_PRODUCTPROFILE_01 table. <BR>
--
-- @version     $Id: ProductProfile_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTPROFILE_01
(
    CATEGORIES      VARCHAR (255),           
                                            -- category of the properties
    OID             CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000'
                                            -- oid of object in ibs_object
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRODUCTPROFILE_01 ADD PRIMARY KEY (OID);
