-------------------------------------------------------------------------------
-- The ibs rights keys table incl. indexes. <BR>
-- The rights keys table contains all rights keys defined within the system.
-- A rights key is a set of relationships person/rights where
-- - person is the person (group or user) who has the rights,
-- - rights are the rights the person has.
-- The objects' rights are defined through the rights key of the object.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_RIGHTSKEYS
(
/*    ID              INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20 ),*/
    ID              INTEGER NOT NULL ,
                                            -- the key id
    RPERSONID       INTEGER NOT NULL,
                                            -- the person who has the rights
    RIGHTS          INTEGER NOT NULL WITH DEFAULT 0, 
                                            -- the rights the person has
    CNT             INTEGER NOT NULL WITH DEFAULT 0,
                                            -- number of tuples for the 
                                            -- actual key
    -- Single rights: Introduced to speed up rights cumulation
    R00             INTEGER,
    R01             INTEGER,
    R02             INTEGER,
    R03             INTEGER,
    R04             INTEGER,
    R05             INTEGER,
    R06             INTEGER,
    R07             INTEGER,
    R08             INTEGER,
    R09             INTEGER,
    R0A             INTEGER,
    R0B             INTEGER,
    R0C             INTEGER,
    R0D             INTEGER,
    R0E             INTEGER,
    R0F             INTEGER,
    R10             INTEGER,
    R11             INTEGER,
    R12             INTEGER,
    R13             INTEGER,
    R14             INTEGER,
    R15             INTEGER,
    R16             INTEGER,
    R17             INTEGER,
    R18             INTEGER,
    R19             INTEGER,
    R1A             INTEGER,
    R1B             INTEGER,
    R1C             INTEGER,
    R1D             INTEGER,
    R1E             INTEGER,
    R1F             INTEGER,
    OID             CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
                                            -- object id of the rights key
                                            -- (used if the key itself is a 
                                            -- business object)
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_RIGHTSKEYSIDPER ON IBSDEV1.IBS_RIGHTSKEYS
    (ID ASC, RPERSONID ASC);
CREATE INDEX IBSDEV1.I_RIGHKEYSRPERSID ON IBSDEV1.IBS_RIGHTSKEYS
    (RPERSONID ASC);
