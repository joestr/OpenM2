-------------------------------------------------------------------------------
-- The ibs system table incl. indexes. <BR>
-- The system table contains system variables used for configuring the system.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_SYSTEM
(
    ID              INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20 ),
    STATE           INTEGER NOT NULL WITH DEFAULT 2,
    NAME            VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
    TYPE            VARCHAR (63) WITH DEFAULT 'undefined',
    VALUE           VARCHAR (255) WITH DEFAULT NULL
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.INDEXSYSTEMNAME ON IBSDEV1.IBS_SYSTEM
    (NAME ASC);
