-------------------------------------------------------------------------------
-- The MAD_PERSON_01 table . <BR>
--
-- @version     $Id: Person_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.MAD_PERSON_01
(
    OID             CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',
    FULLNAME        VARCHAR (63) WITH DEFAULT 'undefined',
    PREFIX          VARCHAR (15),
    TITLE           VARCHAR (31),
    POSITION        VARCHAR (31),
    COMPANY         VARCHAR (63),
    OFFEMAIL        VARCHAR (127),
    OFFHOMEPAGE     VARCHAR (255),
    USEROID         CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.MAD_PERSON_01 ADD UNIQUE (OID);
