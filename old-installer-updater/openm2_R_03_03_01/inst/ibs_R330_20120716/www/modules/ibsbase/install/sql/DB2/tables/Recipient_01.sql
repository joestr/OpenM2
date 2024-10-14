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
CREATE TABLE IBSDEV1.IBS_RECIPIENT_01
(
    RECIPIENTNAME   VARCHAR (63) WITH DEFAULT 'undefined',
    READDATE        TIMESTAMP WITH DEFAULT CURRENT TIMESTAMP,
    DELETED         SMALLINT,
    OID             CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
                                        -- oid of the business object
    RECIPIENTID     CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
    SENTOBJECTID    CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_RECIPIENT_01 ADD UNIQUE (OID);

-- Create index statements:
CREATE INDEX IBSDEV1.I_REC_READDATE ON IBSDEV1.IBS_RECIPIENT_01
    (READDATE ASC);
CREATE INDEX IBSDEV1.I_REC_RECIPIENTID ON IBSDEV1.IBS_RECIPIENT_01
    (RECIPIENTID ASC);
CREATE INDEX IBSDEV1.I_REC_SENTOBJ_OID ON IBSDEV1.IBS_RECIPIENT_01
    (SENTOBJECTID ASC);
