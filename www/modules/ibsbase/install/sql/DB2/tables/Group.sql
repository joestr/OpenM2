-------------------------------------------------------------------------------
-- The ibs group table incl. indexes. <BR>
-- The group table contains all currently existing groups.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_GROUP
(
    oid                 CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- object id of group
    ID                  INTEGER GENERATED ALWAYS AS IDENTITY ( 
                            START WITH 1 INCREMENT BY 1 
    	                    NO MINVALUE NO MAXVALUE 
    	                    NO CYCLE NO ORDER 
    	                    CACHE 20 ),
                                            -- <domainid>001{21--0|1}
    STATE               INTEGER NOT NULL WITH DEFAULT 2,
                                            -- state of object
    DOMAINID            INTEGER NOT NULL WITH DEFAULT 0,
                                            -- domain where the group resides
    NAME                VARCHAR (63) NOT NULL WITH DEFAULT 'undefined'
                                            -- group name
);

-- Create index statements:
CREATE INDEX IBSDEV1.INDEXGROUPDOMAINID ON IBSDEV1.IBS_GROUP
   (DOMAINID ASC);
CREATE UNIQUE INDEX IBSDEV1.INDEXGROUPID ON IBSDEV1.IBS_GROUP
   (ID ASC);
CREATE INDEX IBSDEV1.INDEXGROUPNAME ON IBSDEV1.IBS_GROUP
   (NAME ASC);
CREATE UNIQUE INDEX IBSDEV1.INDEXGROUPOID ON IBSDEV1.IBS_GROUP
   (OID ASC);
CREATE INDEX IBSDEV1.INDEXGROUPSTATE ON IBSDEV1.IBS_GROUP
   (STATE ASC);
