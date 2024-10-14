/******************************************************************************
 * The MAD_PERSON_01 table . <BR>
 * 
 * @version     $Id: Person_01.sql,v 1.5 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/MAD_PERSON_01
(
    OID RAW (8) NOT NULL,
    FULLNAME VARCHAR2 (63) NOT NULL,
    PREFIX VARCHAR2 (15) NOT NULL,
    TITLE VARCHAR2 (31) NOT NULL,
    POSITION VARCHAR2 (31) NOT NULL,
    COMPANY VARCHAR2 (63) NOT NULL,
    OFFEMAIL VARCHAR2 (127) NOT NULL,
    OFFHOMEPAGE VARCHAR2 (255) NOT NULL,
    USEROID RAW (8) NOT NULL
);

alter table /*USER*/mad_person_01 modify (oid default hextoraw('0000000000000000'));
alter table /*USER*/mad_person_01 modify (fullname default 'undefined');
ALTER TABLE MAD_PERSON_01 ADD ( CONSTRAINT UQ__MAD_PERSON___OID__4AE3CED0 UNIQUE ( oid ) );

EXIT;
