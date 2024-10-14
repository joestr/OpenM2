-------------------------------------------------------------------------------
-- The ibs object table incl. indexes and triggers. <BR>
-- The object table contains all currently existing system objects.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_SENTOBJECTCONTAINER_01
(
    OID                 CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- Oid of the sentObject
    NUMBEROFDAYS        INTEGER
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_SENTOBJECTCONTAINER_01 ADD UNIQUE (OID);
