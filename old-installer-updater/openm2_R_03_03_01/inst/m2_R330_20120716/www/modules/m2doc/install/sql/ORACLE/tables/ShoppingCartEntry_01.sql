/******************************************************************************
 * The M2_SHOPPINGCARTENTRY_01 table <BR>
 * 
 * @version     $Id: ShoppingCartEntry_01.sql,v 1.5 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_SHOPPINGCARTENTRY_01
(
    OID RAW (8) NOT NULL,
    QTY NUMBER (10,0),
    UNITOFQTY NUMBER (10,0),
    PACKINGUNIT VARCHAR2 (63),
    PRODUCTOID RAW (8),
    PRODUCTDESCRIPTION VARCHAR2 (255),
    CATALOGOID RAW (8),
    PRICE NUMBER (19,4),
    PRICE2 NUMBER (19,4),
    PRICE3 NUMBER (19,4),
    PRICE4 NUMBER (19,4),
    PRICE5 NUMBER (19,4),
    PRICECURRENCY VARCHAR2 (5),
    ORDERTYPE VARCHAR2 (63),
    ORDRESP RAW (8),
    ORDERTEXT VARCHAR2 (63)
) /*TABLESPACE*/;

ALTER TABLE M2_SHOPPINGCARTENTRY_01 ADD ( CONSTRAINT PK__M2_SHOPPING__OID__49D145B4 PRIMARY KEY ( oid ) );

EXIT;
