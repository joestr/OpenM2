/******************************************************************************
 * The M2_ORDER_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Order_01.sql,v 1.6 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_ORDER_01
(
    OID RAW (8) NOT NULL,
    VOUCHERNO VARCHAR2 (63),
    VOUCHERDATE DATE,
    SUPPLIERCOMPANY VARCHAR2 (63),
    CONTACTSUPPLIER VARCHAR2 (63),
    CUSTOMERCOMPANY VARCHAR2 (63),
    CONTACTCUSTOMER VARCHAR2 (63),
    DELIVERYADDRESS VARCHAR2 (255),
    PAYMENTADDRESS VARCHAR2 (255),
    DESCRIPTION1 VARCHAR2 (255),
    DESCRIPTION2 VARCHAR2 (255),
    DESCRIPTION3 VARCHAR2 (255),
    DELIVERYDATE DATE,
    ORIGINATOR RAW (8),
    RECIPIENT RAW (8),
    PAYMENTOID RAW (8) NOT NULL,
    CC_NUMBER VARCHAR2 (16) NULL,
    CC_EXPMONTH VARCHAR2 (63) NULL,
    CC_EXPYEAR VARCHAR2 (4) NULL,
    CC_OWNER VARCHAR2 (255) NULL,
    CATALOGOID RAW (8) NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/m2_order_01 modify ( oid default hextoraw('0000000000000000'));
alter table /*USER*/m2_order_01 modify ( voucherno default 'undefined' );
alter table /*USER*/m2_order_01 modify ( supplierCompany default 'undefined' );
alter table /*USER*/m2_order_01 modify ( contactSupplier default 'undefined' );
alter table /*USER*/m2_order_01 modify ( customerCompany default 'undefined' );
alter table /*USER*/m2_order_01 modify ( contactCustomer default 'undefined' );
alter table /*USER*/m2_order_01 modify ( originator default hextoraw ('0000000000000000'));
alter table /*USER*/m2_order_01 modify ( recipient default hextoraw ('0000000000000000'));
alter table /*USER*/m2_order_01 modify ( CATALOGOID default hextoraw ('0000000000000000'));
ALTER TABLE M2_ORDER_01 ADD ( CONSTRAINT PK__M2_ORDER_01__OID__02BEDEF6 PRIMARY KEY ( oid ) );

EXIT;
