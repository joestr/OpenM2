/******************************************************************************
 * The M2_PRICE_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Price_01.sql,v 1.4 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRICE_01
(
    OID             RAW (8) NOT NULL,
    COSTCURRENCY    VARCHAR2 (5),
    COST            NUMBER (19,4),
    PRICECURRENCY   VARCHAR2 (5),
    PRICE           NUMBER (19,4),
    USERVALUE1      NUMBER (19,4),
    USERVALUE2      NUMBER (19,4),
    VALIDFROM       DATE,
    QTY             NUMBER (10,0)
) /*TABLESPACE*/;

ALTER TABLE M2_PRICE_01 ADD (CONSTRAINT PK__M2_PRICE_01__OID__7CC6D9D5 PRIMARY KEY (oid));

EXIT;
