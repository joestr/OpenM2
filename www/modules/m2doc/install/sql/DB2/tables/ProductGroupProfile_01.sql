-------------------------------------------------------------------------------
-- This table holds the profile of a product group. <BR>
--
-- @version     $Id: ProductGroupProfile_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTGROUPPROFILE_01
(
    OID             CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',
    THUMBASIMAGE    SMALLINT NOT NULL,
    CODE            VARCHAR (63) DEFAULT 'undefined',
    SEASON          VARCHAR (63) DEFAULT 'undefined',
    IMAGE           VARCHAR (63) DEFAULT 'undefined',
    THUMBNAIL       VARCHAR (63) DEFAULT 'undefined'
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRODUCTGROUPPROFILE_01 ADD PRIMARY KEY (OID);
