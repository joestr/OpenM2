-------------------------------------------------------------------------------
-- The IBS_XMLVIEWER_01 table incl. indexes. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:56 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_XMLVIEWER_01
(
    OID                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    TEMPLATEOID         CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
    WORKFLOWTEMPLATEOID CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
);

-- Unique constraints
ALTER TABLE IBSDEV1.IBS_XMLVIEWER_01 ADD UNIQUE (OID);
