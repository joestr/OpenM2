/******************************************************************************
 * The M2_PRODUCTBRAND_01 table incl. indexes. <BR>
 * 
 * @version     $Id: ProductBrand_01.sql,v 1.5 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTBRAND_01
(
    OID RAW (8) NOT NULL,
    IMAGE VARCHAR2 (255)  
) /*TABLESPACE*/;

alter table /*USER*/M2_PRODUCTBRAND_01 modify ( oid default hextoraw('0000000000000000'));
ALTER TABLE M2_PRODUCTBRAND_01 ADD ( CONSTRAINT PK__M2_PRODUCTB__OID__0467FB9D PRIMARY KEY ( oid ) );


EXIT;
