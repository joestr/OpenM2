-------------------------------------------------------------------------------
-- The ibs_Protocol_01 table incl. indexes. <BR>
-- The ibs_Protocol_01 table contains the values for the base object  
-- Protokoll_01.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_PROTOCOL_01
(
    ID              INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20 ),
    FULLNAME        VARCHAR (63) NOT NULL WITH DEFAULT 'UNKNOWN',
    USERID          INTEGER NOT NULL WITH DEFAULT 0,
    OBJECTNAME      VARCHAR (63) NOT NULL WITH DEFAULT 'UNKNOWN',
    ICON            VARCHAR (63) NOT NULL WITH DEFAULT 'icon.gif',
    TVERSIONID      INTEGER NOT NULL WITH DEFAULT 0,
    CONTAINERKIND   INTEGER NOT NULL WITH DEFAULT 0,
    OWNER           INTEGER NOT NULL WITH DEFAULT 0,
    ACTION          INTEGER NOT NULL WITH DEFAULT 0,
    ACTIONDATE      TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
    OID             CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
    CONTAINERID     CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_PROT_CONTAINERID ON IBSDEV1.IBS_PROTOCOL_01
    (CONTAINERID ASC);
CREATE UNIQUE INDEX IBSDEV1.I_PROTOCOLID ON IBSDEV1.IBS_PROTOCOL_01
    (ID ASC);
CREATE INDEX IBSDEV1.I_PROTOCOLOID ON IBSDEV1.IBS_PROTOCOL_01
    (OID ASC);
CREATE INDEX IBSDEV1.I_PROTOCOLUSERID ON IBSDEV1.IBS_PROTOCOL_01
    (USERID ASC);
