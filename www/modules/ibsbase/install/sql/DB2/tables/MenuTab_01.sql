-------------------------------------------------------------------------------
-- The ibs_MenuTab_01 indexes and triggers. <BR>
-- The ibs_MenuTab_01 table contains the values for the m2 object MenuTab_01.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_MENUTAB_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    DESCRIPTION         VARCHAR (255) NOT NULL,
    ISPRIVATE           SMALLINT,
    PRIORITYKEY         INTEGER NOT NULL,
    DOMAINID            INTEGER NOT NULL,
    CLASSFRONT          VARCHAR (255) NOT NULL,
    CLASSBACK           VARCHAR (255) NOT NULL,
    FILENAME            VARCHAR (255) NOT NULL,
    objectOid           CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_MENUTAB_DOMAINID ON IBSDEV1.IBS_MENUTAB_01
    (DOMAINID ASC);
CREATE UNIQUE INDEX IBSDEV1.I_MENUTAB_OID ON IBSDEV1.IBS_MENUTAB_01
    (OID ASC);
CREATE INDEX IBSDEV1.I_MENUTAB_PRI_KEY ON IBSDEV1.IBS_MENUTAB_01
    (PRIORITYKEY ASC);
