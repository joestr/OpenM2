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
CREATE TABLE IBSDEV1.IBS_SENTOBJECT_01
(
    DISTRIBUTETVERSIONID INTEGER WITH DEFAULT 0,
    DISTRIBUTETYPENAME  VARCHAR (63) WITH DEFAULT 'undefined',
    DISTRIBUTENAME      VARCHAR (63) WITH DEFAULT 'undefined',
    DISTRIBUTEICON      VARCHAR (63) WITH DEFAULT 'undefined',
    ACTIVITIES          VARCHAR (63) WITH DEFAULT 'undefined',
    DELETED             SMALLINT,
    OID                 CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- oid of the sent object
    DISTRIBUTEID        CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
                                            -- version of the sent object
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_SENTOBJECT_01 ADD UNIQUE (OID);

-- Create index statements:
CREATE INDEX IBSDEV1.I_SENTOBJECTID ON IBSDEV1.IBS_SENTOBJECT_01
    (DISTRIBUTEID ASC);
CREATE INDEX IBSDEV1.I_SENTOBJ_TVERS_ID ON IBSDEV1.IBS_SENTOBJECT_01
    (DISTRIBUTETVERSIONID ASC);
