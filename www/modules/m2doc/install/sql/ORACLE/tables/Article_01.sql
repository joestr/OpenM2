/******************************************************************************
 * The M2_Article_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Article_01.sql,v 1.4 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

CREATE TABLE /*USER*/M2_Article_01
(
    OID RAW (8) NOT NULL,
    CONTENT CLOB NOT NULL,
    DISCUSSIONID RAW (8) NOT NULL,
    STATE NUMBER (10,0)  
) /*TABLESPACE*/;

alter table /*USER*/m2_Article_01 modify ( oid default hextoraw('0000000000000000'));
alter table /*USER*/m2_Article_01 modify ( discussionId default hextoraw('0000000000000000'));
alter table /*USER*/m2_Article_01 modify ( state default 2);
ALTER TABLE M2_Article_01 ADD ( CONSTRAINT UQ__M2_Article___OID__415A6496 UNIQUE ( oid ) );

EXIT;
