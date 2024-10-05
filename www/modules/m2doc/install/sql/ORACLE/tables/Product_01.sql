/******************************************************************************
 * The M2_PRODUCT_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Product_01.sql,v 1.3 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCT_01
(
    OID             RAW (8) NOT NULL,
    PRODUCTNO       VARCHAR2 (63),
    EAN             VARCHAR2 (63),
    PRODUCTDESCRIPTION CLOB NOT NULL,
    AVAILABLEFROM   DATE,
    UNITOFQTY       NUMBER (10,0),
    PACKINGUNIT     VARCHAR2 (63),
    THUMBASIMAGE    NUMBER (1,0) NOT NULL,
    THUMBNAIL       VARCHAR2 (63),
    IMAGE           VARCHAR2 (63),
    STOCK           VARCHAR2 (63),
    HASASSORTMENT   NUMBER (10,0),
    PRODUCTPROFILEOID RAW (8),
    BRANDNAMEOID    RAW (8),
    CREATED         NUMBER (10,0),
    PATH            VARCHAR2 (20)
) /*TABLESPACE*/;

ALTER TABLE M2_PRODUCT_01 ADD ( CONSTRAINT PK__M2_PRODUCT___OID__00976AB9 PRIMARY KEY ( oid ) );

EXIT;
