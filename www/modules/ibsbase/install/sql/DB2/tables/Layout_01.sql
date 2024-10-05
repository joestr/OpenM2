-------------------------------------------------------------------------------
-- The ibs_Layout_01 indexes and triggers. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_LAYOUT_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    NAME                VARCHAR (63) NOT NULL,
    DOMAINID            INTEGER NOT NULL WITH DEFAULT 0
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_LAYOUT_01 ADD UNIQUE (OID);

-- Create index statements:
CREATE INDEX IBSDEV1.INDEXLAYOUTNAME ON IBSDEV1.IBS_LAYOUT_01
    (NAME ASC);
