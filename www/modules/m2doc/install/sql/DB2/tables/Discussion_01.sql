-------------------------------------------------------------------------------
-- The M2_Discussion_01 Table. <BR>
-- The address table contains all currently existing addresses.
--
-- @version     $Id: Discussion_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_Discussion_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    MAXLEVELS           INTEGER  NOT NULL,
    DEFAULTVIEW         SMALLINT NOT NULL,
    refOid              CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.M2_Discussion_01 ADD UNIQUE (OID);
