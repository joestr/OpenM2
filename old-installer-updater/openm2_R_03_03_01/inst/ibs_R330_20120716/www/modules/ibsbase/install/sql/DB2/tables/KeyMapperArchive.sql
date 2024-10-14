-------------------------------------------------------------------------------
-- The ibs_KeyMapperArchive table. <BR>
-- This table is used to archive the external keys (EXTKEY) of deleted objects.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_KEYMAPPERARCHIVE
(
    oid                 CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
    ID                  VARCHAR (255) NOT NULL,
    IDDOMAIN            VARCHAR (63)  NOT NULL
);
