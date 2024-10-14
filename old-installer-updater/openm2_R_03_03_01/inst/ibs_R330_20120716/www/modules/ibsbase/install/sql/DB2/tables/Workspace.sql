-------------------------------------------------------------------------------
-- The IBS_WORKSPACE table incl. indexes. <BR>
-- The workspace table contains all currently existing user workspaces.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:56 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_WORKSPACE
(
    USERID              INTEGER NOT NULL WITH DEFAULT 0,
    DOMAINID            INTEGER NOT NULL WITH DEFAULT 0,
    WORKSPACE           CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    WORKBOX             CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    OUTBOX              CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    INBOX               CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    NEWS                CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    HOTLIST             CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    PROFILE             CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    PUBLICWSP           CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    SHOPPINGCART        CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    ORDERS              CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);

-- Primary key:
ALTER TABLE IBSDEV1.IBS_WORKSPACE ADD PRIMARY KEY (USERID);

-- Create index statements:
CREATE INDEX IBSDEV1.I_WORKSP_DOMAINID ON IBSDEV1.IBS_WORKSPACE
    (DOMAINID ASC);
CREATE INDEX IBSDEV1.I_WORKSP_INBOX ON IBSDEV1.IBS_WORKSPACE
    (INBOX ASC);
CREATE INDEX IBSDEV1.I_WORKSP_OUTBOX ON IBSDEV1.IBS_WORKSPACE
    (OUTBOX ASC);
CREATE INDEX IBSDEV1.I_WORKSP_WORKSP ON IBSDEV1.IBS_WORKSPACE
    (WORKSPACE ASC);
