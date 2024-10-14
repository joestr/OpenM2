-------------------------------------------------------------------------------
-- The ibs operations table incl. indexes. <BR>
-- The operations table contains the operations which may be performed within
-- the system.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)  020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_OPERATION
(
    ID                  INTEGER NOT NULL DEFAULT 0,
    NAME                VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
    DESCRIPTION         VARCHAR (255) WITH DEFAULT 
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_OPERATIONID ON IBSDEV1.IBS_OPERATION
    (ID ASC);
CREATE INDEX IBSDEV1.I_OPERATIONNAME ON IBSDEV1.IBS_OPERATION
    (NAME ASC);
