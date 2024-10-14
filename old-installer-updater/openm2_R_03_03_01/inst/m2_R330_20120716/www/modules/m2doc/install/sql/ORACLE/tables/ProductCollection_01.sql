/******************************************************************************
 * The M2_PRODUCTCOLLECTION_01 table incl. indexes. <BR>
 * 
 * @version     $Id: ProductCollection_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTCOLLECTION_01
(
    OID RAW (8) NOT NULL,
    COST NUMBER (19,4),
    COSTCURRENCY VARCHAR2 (5),
    TOTALQUANTITY NUMBER (10,0),
    VALIDFROM DATE,
    CATEGORYOIDX RAW (8),
    CATEGORYOIDY RAW (8),
    NRCODES NUMBER (10,0)
) /*TABLESPACE*/;

alter table /*USER*/m2_productcollection_01 modify ( oid default hextoraw('0000000000000000') );
alter table /*USER*/m2_productcollection_01 modify ( categoryoidx default hextoraw('0000000000000000') );
alter table /*USER*/m2_productcollection_01 modify ( categoryoidy default hextoraw('0000000000000000') );
ALTER TABLE M2_PRODUCTCOLLECTION_01 ADD ( CONSTRAINT PK__M2_PRODUCTC__OID__07EE73BE PRIMARY KEY ( oid ) );

EXIT;
