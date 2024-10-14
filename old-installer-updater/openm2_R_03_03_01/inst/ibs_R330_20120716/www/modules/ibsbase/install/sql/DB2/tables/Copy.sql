-------------------------------------------------------------------------------
-- The ibs_Copy table is used while copy m2 objects. <BR>
-- 
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)  020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_COPY
(
    COPYID              INTEGER,
    oldOid              CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    newOid              CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_COPY ADD UNIQUE (OLDOID);
