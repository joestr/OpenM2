-------------------------------------------------------------------------------
-- The ibs address table incl. indexes. <BR>
-- The address table contains all currently existing addresses.
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/31 16:29:03 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.m2_Address_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    STREET              VARCHAR (63) NOT NULL,
    ZIP                 VARCHAR (15) NOT NULL,
    TOWN                VARCHAR (63) NOT NULL,
    MAILBOX             VARCHAR (15) NOT NULL,
    COUNTRY             VARCHAR (31) NOT NULL,
    TEL                 VARCHAR (63) NOT NULL,
    FAX                 VARCHAR (63) NOT NULL,
    EMAIL               VARCHAR (127) NOT NULL,
    HOMEPAGE            VARCHAR (255) NOT NULL
);

-- Unique constraints:
ALTER TABLE IBSDEV1.m2_Address_01 ADD UNIQUE (OID);
