-------------------------------------------------------------------------------
-- The M2_PARTICIPANT_01 table incl. indexes. <BR>
-- The address table contains all currently existing addresses.
--
-- @version     $Id: Participant_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PARTICIPANT_01
(
    OID                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    ANNOUNCERID         INTEGER     NOT NULL,
    ANNOUNCERNAME       VARCHAR (63) NOT NULL
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_PARTICIPANTOID ON
    IBSDEV1.M2_PARTICIPANT_01 (OID ASC);
