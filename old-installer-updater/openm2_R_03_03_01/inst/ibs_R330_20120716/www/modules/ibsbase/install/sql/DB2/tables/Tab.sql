-------------------------------------------------------------------------------
-- The ibs Tab table incl. indexes. <BR>
-- This table contains all tabs which are available throughout the system.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_TAB
(
    ID              INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20 ),     
                                            -- unique id of the tab
    DOMAINID        INTEGER NOT NULL WITH DEFAULT 0,
                                            -- valid domain of tab
    CODE            VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
                                            -- definite description of tab
    KIND            INTEGER NOT NULL WITH DEFAULT 0,   
                                            -- kind of tab
    TVERSIONID      INTEGER WITH DEFAULT 0,      
                                            -- id of type version
    FCT             INTEGER NOT NULL WITH DEFAULT 0,    
                                            -- function of tab
    PRIORITY        INTEGER NOT NULL WITH DEFAULT 0,
                                            -- priority of tab
    MULTILANGKEY    VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
                                            -- key for storing the
                                            -- multilang values of this tab
    RIGHTS          INTEGER NOT NULL WITH DEFAULT 0,    
                                            -- necessary rights to show the
                                            -- tab
    CLASS           VARCHAR (255) WITH DEFAULT 'undefined',
                                            -- class to show the view tab
    OID             CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
                                            -- object id (for later use)
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_TABDOMAINIDCODE ON IBSDEV1.IBS_TAB
(DOMAINID ASC, CODE ASC);
CREATE UNIQUE INDEX IBSDEV1.INDEXTABID ON IBSDEV1.IBS_TAB
(ID ASC);
