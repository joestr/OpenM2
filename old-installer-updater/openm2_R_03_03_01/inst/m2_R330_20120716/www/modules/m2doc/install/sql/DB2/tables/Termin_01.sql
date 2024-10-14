-------------------------------------------------------------------------------
-- The m2_Termin_01 table incl. indexes. <BR>
-- The m2_Termin_01 table contains the values for the base object Termin_01.
--
-- @version     $Id: Termin_01.sql,v 1.3 2003/10/31 00:12:55 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_TERMIN_01
(
    STARTDATE       TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,              
    					                    -- begin of term, date and time
    ENDDATE         TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,              
    					                    -- end of term, date and time
    PLACE           VARCHAR (255),           
                                            -- place where term happens
    PARTICIPANTS    SMALLINT,               
                                            -- does term have participants?
    MAXNUMPARTICIPANTS INTEGER,             
                                            -- maximum number of participants
    SHOWPARTICIPANTS SMALLINT,              
                                            -- viewing of participants 
                                            -- list allowed?
    DEADLINE        TIMESTAMP WITH DEFAULT CURRENT TIMESTAMP,   
                                            -- deadline (time) to cancel 
                                            -- the announcement
    ATTACHMENTS     SMALLINT,               
                                            -- may term have attachments?
    OID             CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000'
                                            -- unique object id, reference to
                                            -- table ibs_object
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_TERMIN_01 ADD PRIMARY KEY (OID);

-- Create index statements:
CREATE INDEX IBSDEV1.I_OB_TERMINENDDATE ON IBSDEV1.M2_TERMIN_01
    (ENDDATE ASC);
CREATE INDEX IBSDEV1.I_OB_TERMINST_DATE ON IBSDEV1.M2_TERMIN_01
    (STARTDATE ASC);
