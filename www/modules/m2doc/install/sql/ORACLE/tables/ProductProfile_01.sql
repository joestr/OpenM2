/******************************************************************************
 * The M2_PRODUCTPROFILE_01 table. <BR>
 * 
 * @version     $Id: ProductProfile_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTPROFILE_01
(
    OID RAW (8) NOT NULL,
    CATEGORIES VARCHAR2 (255)
) /*TABLESPACE*/;

alter table /*USER*/m2_productprofile_01 modify ( oid default hextoraw ( '0000000000000000'));
ALTER TABLE M2_PRODUCTPROFILE_01 ADD ( CONSTRAINT PK__M2_PRODUCTP__OID__0CA83BE3 PRIMARY KEY ( oid ) );

EXIT;
