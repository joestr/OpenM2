-------------------------------------------------------------------------------
-- The IBS_USERPROFILE table incl. indexes. <BR>
-- The object table contains all currently existing system objects.
-- 
-- @version     $Revision: 1.3 $, $Date: 2010/03/12 15:01:03 $
--              $Author: btatzmann $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--

-- Create table statement
CREATE TABLE IBSDEV1.IBS_USERPROFILE
(
    oid                     CHAR (8) FOR BIT DATA NOT NULL
                            WITH DEFAULT X'0000000000000000',
    USERID                  INTEGER NOT NULL WITH DEFAULT 0, -- Primary key
    NEWSTIMELIMIT           INTEGER NOT NULL,
    NEWSSHOWONLYUNREAD      SMALLINT NOT NULL,
    OUTBOXUSETIMELIMIT      SMALLINT NOT NULL,
    OUTBOXTIMELIMIT         INTEGER,
    OUTBOXUSETIMEFRAME      SMALLINT NOT NULL,
    OUTBOXTIMEFRAMEFROM     TIMESTAMP,
    OUTBOXTIMEFRAMETO       TIMESTAMP,
    SHOWEXTENDEDATTRIBUTES  SMALLINT NOT NULL,
    SHOWFILESINWINDOWS      SMALLINT NOT NULL,
    LASTLOGIN               TIMESTAMP,
    LAYOUTID                CHAR (8) FOR BIT DATA
                            WITH DEFAULT X'0000000000000000',
    SHOWREF                 SMALLINT NOT NULL,
    SHOWEXTENDEDRIGHTS      SMALLINT NOT NULL,
    SAVEPROFILE             SMALLINT NOT NULL,
    NOTIFICATIONKIND        INTEGER,
    SENDSMS                 SMALLINT NOT NULL,
    ADDWEBLINK              SMALLINT NOT NULL,
    LOCALEID                CHAR (8) FOR BIT DATA
                            WITH DEFAULT X'0000000000000000'
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_USERPROFILEUID ON IBSDEV1.IBS_USERPROFILE
    (userId ASC);
CREATE UNIQUE INDEX IBSDEV1.I_USERPROFILEOID ON IBSDEV1.IBS_USERPROFILE
    (OID ASC);
