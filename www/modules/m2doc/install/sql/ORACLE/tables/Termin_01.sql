/******************************************************************************
 * The M2_TERMIN_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Termin_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

CREATE TABLE /*USER*/M2_TERMIN_01
(
    OID RAW (8) NOT NULL,
    STARTDATE DATE NOT NULL,
    ENDDATE DATE NOT NULL,
    PLACE VARCHAR2 (255) NOT NULL,
    PARTICIPANTS NUMBER (1,0) NOT NULL,
    MAXNUMPARTICIPANTS NUMBER (10,0) NOT NULL,
    SHOWPARTICIPANTS NUMBER (1,0),
    DEADLINE DATE,
    ATTACHMENTS NUMBER (1,0)  
) /*TABLESPACE*/;

alter table /*USER*/m2_termin_01 modify ( oid default hextoraw('0000000000000000'));
ALTER TABLE M2_TERMIN_01 ADD ( CONSTRAINT PK__M2_TERMIN_0__OID__4868202F PRIMARY KEY ( oid ) );

EXIT;
