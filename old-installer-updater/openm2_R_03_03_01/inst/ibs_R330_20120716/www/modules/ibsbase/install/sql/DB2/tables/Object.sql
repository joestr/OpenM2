-------------------------------------------------------------------------------
-- The ibs object table incl. indexes. <BR>
-- The object table contains all currently existing system objects.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_OBJECT
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    ID                  INTEGER NOT NULL WITH DEFAULT 0,
    STATE               INTEGER NOT NULL WITH DEFAULT 2,
    TVERSIONID          INTEGER NOT NULL WITH DEFAULT 0,
    TYPENAME            VARCHAR (63) NOT NULL WITH DEFAULT 'UNKNOWN',
    ISCONTAINER         SMALLINT NOT NULL WITH DEFAULT 0,
    NAME                VARCHAR (63) NOT NULL WITH DEFAULT 'UNDEFINED',
    containerId         CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    CONTAINERKIND       INTEGER NOT NULL,
    ISLINK              SMALLINT NOT NULL WITH DEFAULT 0,
    linkedObjectId      CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    SHOWINMENU          SMALLINT NOT NULL WITH DEFAULT 0,
    FLAGS               INTEGER NOT NULL WITH DEFAULT 0,
                                        -- flags for some properties of the obj:
                                        -- Bit 0 ... hasMasterObject
                                        -- Bit 1 ... ...
    OWNER               INTEGER NOT NULL WITH DEFAULT 0,
    OLEVEL              INTEGER NOT NULL WITH DEFAULT 0,
    POSNO               INTEGER NOT NULL WITH DEFAULT 0,
    POSNOPATH           VARCHAR (254) NOT NULL WITH DEFAULT '0000',
    CREATIONDATE        TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
    CREATOR             INTEGER NOT NULL WITH DEFAULT 0,
    LASTCHANGED         TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
    CHANGER             INTEGER NOT NULL WITH DEFAULT 0,
    VALIDUNTIL          TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
    DESCRIPTION         VARCHAR (255),
    ICON                VARCHAR (63),
    PROCESSSTATE        INTEGER WITH DEFAULT 0,
    RKEY                INTEGER WITH DEFAULT 0,
    CONSISTSOFID        INTEGER NOT NULL WITH DEFAULT 0
                                        -- unique tab id if the object
                                        -- represents a tab
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_OBJ_CONTAINERID ON IBSDEV1.IBS_OBJECT
    (CONTAINERID ASC);
CREATE INDEX IBSDEV1.I_OBJ_CONT_KIND ON IBSDEV1.IBS_OBJECT
    (CONTAINERKIND ASC);
CREATE INDEX IBSDEV1.I_OBJ_CONTLINKOID ON IBSDEV1.IBS_OBJECT
    (LINKEDOBJECTID ASC);
CREATE UNIQUE INDEX IBSDEV1.I_OBJECTID ON IBSDEV1.IBS_OBJECT
    (ID ASC);
CREATE INDEX IBSDEV1.I_OBJECTNAME ON IBSDEV1.IBS_OBJECT
    (NAME ASC);
CREATE UNIQUE INDEX IBSDEV1.I_OBJECTOID ON IBSDEV1.IBS_OBJECT
    (OID ASC);
CREATE INDEX IBSDEV1.I_OBJECTOWNER ON IBSDEV1.IBS_OBJECT
    (OWNER ASC);
CREATE INDEX IBSDEV1.I_OBJECTPOSNOPATH ON IBSDEV1.IBS_OBJECT
    (POSNOPATH ASC);
CREATE INDEX IBSDEV1.I_OBJECTRKEY ON IBSDEV1.IBS_OBJECT
    (RKEY ASC);
CREATE INDEX IBSDEV1.I_OBJ_RTVERSIONID ON IBSDEV1.IBS_OBJECT
    (TVERSIONID ASC);
CREATE INDEX IBSDEV1.I_OBJ_VALIDUNTIL ON IBSDEV1.IBS_OBJECT
    (VALIDUNTIL ASC);
