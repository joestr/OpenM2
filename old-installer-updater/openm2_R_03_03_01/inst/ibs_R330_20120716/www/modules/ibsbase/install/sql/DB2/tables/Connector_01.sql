-------------------------------------------------------------------------------
-- The IBS_CONNECTOR_01 table incl. indexes. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_CONNECTOR_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    CONNECTORTYPE       INTEGER NOT NULL,
    ISIMPORTCONNECTOR   INTEGER,
    ISEXPORTCONNECTOR   INTEGER,
    ARG1                VARCHAR (255),
    ARG2                VARCHAR (255),
    ARG3                VARCHAR (255),
    ARG4                VARCHAR (255),
    ARG5                VARCHAR (128),
    ARG6                VARCHAR (128),
    ARG7                VARCHAR (128),
    ARG8                VARCHAR (128),
    ARG9                VARCHAR (128)
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.INDEXCONNECTOROID ON IBSDEV1.IBS_CONNECTOR_01
    (OID ASC);
