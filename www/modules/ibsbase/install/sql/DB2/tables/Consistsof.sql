-------------------------------------------------------------------------------
-- The ibs consistsOf table incl. indexes. <BR>
-- The consistsOf table contains the dependencies between types regarding tabs.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-- 2002.11.07 - change ID to GENERATED ALWAYS (ZK)
-------------------------------------------------------------------------------
--/

-- Create table statement
CREATE TABLE IBSDEV1.IBS_CONSISTSOF
(
    ID                  INTEGER GENERATED ALWAYS AS IDENTITY ( 
                            START WITH 1 INCREMENT BY 1 
    	                    NO MINVALUE NO MAXVALUE 
    	                    NO CYCLE NO ORDER 
    	                    CACHE 20 ),
                                            -- unique id of the tab
    oid                 CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- unique object id
                                            -- (for later use)
    TVERSIONID          INTEGER NOT NULL WITH DEFAULT 0,
                                            -- tVersionId of the tab
    TABID               INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of the tab in table
                                            -- ibs_Tab
    PRIORITY            INTEGER,            -- priority of the tab
    RIGHTS              INTEGER NOT NULL WITH DEFAULT 0,
                                            -- necessary rights to show the
                                            -- tab
    INHERITEDFROM       INTEGER NOT NULL WITH DEFAULT 0
                                            -- id of type version from which 
                                            -- this tuple was inherited
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.INDEXCONSISTSOFID ON IBSDEV1.IBS_CONSISTSOF
    (ID ASC);
-- changed name of index, because only 18 char. is possible
CREATE INDEX IBSDEV1.INDEXCONSISTSOFTVE ON IBSDEV1.IBS_CONSISTSOF
    (TVERSIONID ASC, TABID ASC);
