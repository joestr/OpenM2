-------------------------------------------------------------------------------
-- The M2_Article_01 table incl. indexes. <BR>
-- The address table contains all currently existing addresses.
--
-- @version     $Id: Article_01.sql,v 1.3 2003/10/31 00:12:53 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_Article_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    STATE               INTEGER NOT NULL WITH DEFAULT 2,
    CONTENT             CLOB(2047M) NOT NULL,
    discussionId        CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.M2_Article_01 ADD UNIQUE (OID);

-- Create index statements:
CREATE INDEX IBSDEV1.I_BEIT_DISCUS_ID ON IBSDEV1.M2_Article_01
    (DISCUSSIONID ASC);
