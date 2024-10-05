-------------------------------------------------------------------------------
-- The m2_ProductProperties_01 table incl. indexes and triggers. <BR>
-- The m2_ProductProperties_01 table contains properties.
-- Products in a catalog may refer to this properties. Usually this table
-- contains information about colors and sizes.
--
-- @version     $Id: ProductProperties_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTPROPERTIES_01
(
    DELIMITER       VARCHAR (1),             
                                            -- delimiter used for properties
    VALUES1         VARCHAR (255),           
                                            -- properties separated by delimiter
    VALUES2         VARCHAR (255),           
                                            -- properties separated by delimiter
    VALUES3         VARCHAR (255),           
                                            -- properties separated by delimiter
    VALUES4         VARCHAR (255),           
                                            -- properties separated by delimiter
    OID             CHAR (8) FOR BIT DATA NOT NULL,    
                                            -- oid of object in ibs_object
    CATEGORYOID     CHAR (8) FOR BIT DATA NOT NULL               
                                            -- category of the properties
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRODUCTPROPERTIES_01 ADD PRIMARY KEY (OID);
