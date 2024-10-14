-------------------------------------------------------------------------------
-- The M2_XMLDISCUSSIONTEMPLATE_01 table. 
--
-- @version     $Id: XMLDiscussionTemplate_01.sql,v 1.3 2003/10/31 00:12:55 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_XMLDISCUSSIONTEMPLATE_01
(
    OID                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    LEVEL1              CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    LEVEL2              CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    LEVEL3              CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.M2_XMLDISCUSSIONTEMPLATE_01 ADD UNIQUE (OID);
