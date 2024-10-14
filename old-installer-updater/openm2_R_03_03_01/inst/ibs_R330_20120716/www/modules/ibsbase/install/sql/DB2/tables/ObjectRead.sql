-------------------------------------------------------------------------------
-- The ibs objectRead table incl. indexes. <BR>
-- The value table tells which object was already read by which user and when.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_OBJECTREAD
(
    OID                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    USERID              INTEGER NOT NULL WITH DEFAULT 0,
    HASREAD             SMALLINT NOT NULL,
    LASTREAD            TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_OBJECTREADOID ON IBSDEV1.IBS_OBJECTREAD
    (OID ASC);
CREATE UNIQUE INDEX IBSDEV1.I_OBJ_READOIDUSER ON IBSDEV1.IBS_OBJECTREAD
    (OID ASC, USERID ASC);
CREATE INDEX IBSDEV1.I_OBJECTREADUSER ON IBSDEV1.IBS_OBJECTREAD
    (USERID ASC);
