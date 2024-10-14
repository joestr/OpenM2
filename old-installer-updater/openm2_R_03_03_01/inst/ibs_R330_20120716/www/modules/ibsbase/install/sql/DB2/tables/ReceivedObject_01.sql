-------------------------------------------------------------------------------
-- The ibs object table incl. indexes and triggers. <BR>
-- The object table contains all currently existing system objects.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_RECEIVEDOBJECT_01
(
    DISTRIBUTEDTVERSIONID INTEGER NOT NULL WITH DEFAULT 0,
                                        -- version of the sent object
    DISTRIBUTEDTYPENAME VARCHAR (63) WITH DEFAULT 'undefined',
    DISTRIBUTEDNAME     VARCHAR (63) WITH DEFAULT 'undefined',
    DISTRIBUTEDICON     VARCHAR (63) WITH DEFAULT 'undefined',
    ACTIVITIES          VARCHAR (63) WITH DEFAULT 'undefined',
    SENDERFULLNAME      VARCHAR (63) WITH DEFAULT 'undefined',
    OID                 CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                        -- oid of the sent object
    DISTRIBUTEDID       CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
    SENTOBJECTID        CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_RECEIVEDOBJECT_01 ADD UNIQUE (OID);

-- Create index statements:
CREATE INDEX IBSDEV1.I_REC_DOBJ_DISID ON 
    IBSDEV1.IBS_RECEIVEDOBJECT_01 (DISTRIBUTEDID ASC);
CREATE INDEX IBSDEV1.I_REC_DOBJ_SENTID ON 
    IBSDEV1.IBS_RECEIVEDOBJECT_01 (SENTOBJECTID ASC);
CREATE INDEX IBSDEV1.I_REC_DOBJ_TVER_ID ON 
    IBSDEV1.IBS_RECEIVEDOBJECT_01 (DISTRIBUTEDTVERSIONID ASC);
