-------------------------------------------------------------------------------
-- The ibs_Attachment_01 indexes and triggers. <BR>
-- The ibs_Attachment_01 table contains the values for the base object
-- Attachment_01.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_ATTACHMENT_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    FILENAME            VARCHAR (255),
    PATH                VARCHAR (255),
    FILESIZE            INTEGER,
    URL                 VARCHAR (255),
    ATTACHMENTTYPE      INTEGER NOT NULL,
    ISMASTER            SMALLINT
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_ATTACHMENT_01 ADD UNIQUE (OID);

-- Create index statements
-- oracle name of index - INDEXATTACHMENTTYPE
CREATE INDEX IBSDEV1.I_ATTACHTYPE ON IBSDEV1.IBS_ATTACHMENT_01
    (ATTACHMENTTYPE ASC);
