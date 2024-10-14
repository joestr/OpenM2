/******************************************************************************
 * The ibs_Note_01 table. <BR>
 * 
 * @version     $Id: Note_01.sql,v 1.5 2003/10/31 16:30:03 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/ibs_Note_01
(
    OID RAW (8) NOT NULL,
    CONTENT CLOB NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/ibs_Note_01 modify ( oid default hextoraw('0000000000000000'));
ALTER TABLE ibs_Note_01 ADD ( CONSTRAINT UQ__ibs_Note_01__OID__6BE66696 UNIQUE ( oid ) );


EXIT;
