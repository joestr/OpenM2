-------------------------------------------------------------------------------
-- The m2_Catalog_01 table incl. indexes and triggers. <BR>
-- The m2_Catalog_01 table contains the values for the base object Catalog_01.
--
-- @version     $Id: Catalog_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_CATALOG_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    LOCKED              SMALLINT NOT NULL WITH DEFAULT 0,
                                            -- default not locked
    DESCRIPTION1        VARCHAR (255),
    DESCRIPTION2        VARCHAR (255),
    ISORDEREXPORT       SMALLINT NOT NULL WITH DEFAULT 0,
                                            -- default do not use export order
    FILTERID            SMALLINT,
    -- notify orderresponsible by email  if he gets a new order
    NOTIFYBYEMAIL       SMALLINT NOT NULL,
    SUBJECT             VARCHAR (255),           
                                            -- subject of email
    CONTENT             VARCHAR (255),           
                                            -- content of email
    companyOid          CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
    ordResp             CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- The responsible User, Group or
                                            -- Person for orderings
    ordRespMed          CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- The medium the ordering is send 
                                            -- through
    contResp            CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- The responsible User, Group or
                                            -- Person for the contents of this
                                            -- cataolog
    contRespMed         CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- The medium the content
                                            -- responsible is reached
    connectorOid        CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
    translatorOid       CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_CATALOG_01 ADD PRIMARY KEY (OID);
