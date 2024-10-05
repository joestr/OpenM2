-------------------------------------------------------------------------------
-- The MAD_COMPANY_01 table. <BR>
--
-- @version     $Id: Company_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--

-- Create table statement:
CREATE TABLE IBSDEV1.MAD_COMPANY_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    OWNER               VARCHAR (63) DEFAULT 'undefined',
    MANAGER             VARCHAR (63) DEFAULT 'undefined',
    LEGAL_FORM          VARCHAR (63),
    MWST                INTEGER
);

-- Unique constraints:
ALTER TABLE IBSDEV1.MAD_COMPANY_01 ADD UNIQUE (OID);
