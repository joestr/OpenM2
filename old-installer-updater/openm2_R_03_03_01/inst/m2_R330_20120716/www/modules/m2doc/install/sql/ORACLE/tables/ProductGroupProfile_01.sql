/******************************************************************************
 * The M2_PRODUCTGROUPPROFILE_01 table. <BR>
 * 
 * @version     $Id: ProductGroupProfile_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTGROUPPROFILE_01
(
    OID RAW (8) NOT NULL,
    THUMBASIMAGE NUMBER (1,0) NOT NULL,
    CODE VARCHAR2 (63),
    SEASON VARCHAR2 (63),
    IMAGE VARCHAR2 (63),
    THUMBNAIL VARCHAR2 (63)
) /*TABLESPACE*/;

alter table /*USER*/m2_productgroupprofile_01 modify ( oid default hextoRaw('0000000000000000'));
alter table /*USER*/m2_productgroupprofile_01 modify ( code default 'undefined' );
alter table /*USER*/m2_productgroupprofile_01 modify ( season default 'undefined' );
alter table /*USER*/m2_productgroupprofile_01 modify ( image default 'undefined' );
alter table /*USER*/m2_productgroupprofile_01 modify ( thumbnail default 'undefined' );
ALTER TABLE M2_PRODUCTGROUPPROFILE_01 ADD ( CONSTRAINT PK__M2_PRODUCTG__OID__0432F173 PRIMARY KEY ( oid ) );

EXIT;
