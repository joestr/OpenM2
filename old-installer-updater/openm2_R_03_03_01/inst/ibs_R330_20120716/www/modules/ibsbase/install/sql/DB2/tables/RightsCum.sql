-------------------------------------------------------------------------------
-- The ibs rights cum table incl. indexes. <BR>
-- The rights cum table contains the cumulated rights for each user within all
-- rights keys.
-- This table contains only tuples for rights keys where the user has already 
-- rights.
-- Rights keys, which does not contain the user, are not necessary within
-- this table.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_RIGHTSCUM
(
    USERID          INTEGER NOT NULL WITH DEFAULT 0,
                                            -- the user who has the rights
    RKEY            INTEGER NOT NULL WITH DEFAULT 0,
                                            -- the rights key for which the 
                                            -- rights are cumulated
    RIGHTS          INTEGER NOT NULL WITH DEFAULT 0
                                            -- the cumulated rights
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_RIGHTSCUMRKEY ON IBSDEV1.IBS_RIGHTSCUM
    (RKEY ASC);
CREATE UNIQUE INDEX IBSDEV1.I_RIGHTSCUMUIDKEY ON IBSDEV1.IBS_RIGHTSCUM
    (USERID ASC, RKEY ASC);
