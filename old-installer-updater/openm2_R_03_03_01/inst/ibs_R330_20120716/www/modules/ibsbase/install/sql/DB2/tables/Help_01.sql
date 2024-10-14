-------------------------------------------------------------------------------
-- The ibs_Help_01 indexes and triggers. <BR>
-- The ibs_Help_01 table contains the values for the base object Help_01.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_HELP_01
(
   oid                  CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
   GOAL                 VARCHAR (2000),
   SEARCHCONTENT        VARCHAR (255) NOT NULL,
   HELPURL              VARCHAR (255)
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_HELPOID ON IBSDEV1.IBS_HELP_01
    (OID ASC);
CREATE INDEX IBSDEV1.I_HELPSEARCHCONT ON IBSDEV1.IBS_HELP_01
    (SEARCHCONTENT ASC);
