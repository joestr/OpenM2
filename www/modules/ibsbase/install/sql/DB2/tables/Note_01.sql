-------------------------------------------------------------------------------
-- The ibs_Note_01 table . <BR>
-- The address table contains all currently existing addresses.
--
-- @version     $Id: Note_01.sql,v 1.4 2003/10/31 16:30:38 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.ibs_Note_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- unique object id, reference 
                                            -- to table ibs_object
    CONTENT             CLOB(2047M) NOT NULL
                                            -- content of note - normal Text
                                            -- or HTML-code
);

-- Unique constraints:
ALTER TABLE IBSDEV1.ibs_Note_01 ADD UNIQUE (OID);
