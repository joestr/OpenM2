-------------------------------------------------------------------------------
-- The M2_TERMIN_01 table incl. indexes. <BR>
-- The UserProfile table contains all currently existing user UserProfiles.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_USERADDRESS_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- the Object Identifier of the
                                            -- UserAddress Tab
    EMAIL               VARCHAR (127),      -- the SmSEmail Address of the User
    SMSEMAIL            VARCHAR (127)       -- the Email Address of the User
);

-- Primary key:
ALTER TABLE IBSDEV1.IBS_USERADDRESS_01 ADD PRIMARY KEY (OID);
