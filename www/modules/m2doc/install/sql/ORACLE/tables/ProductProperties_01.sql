/******************************************************************************
 * The M2_PRODUCTPROPERTIES_01 table. <BR>
 * 
 * @version     $Id: ProductProperties_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTPROPERTIES_01
(
    OID RAW (8) NOT NULL,
    CATEGORYOID RAW (8),
    DELIMITER VARCHAR2 (1),
    VALUES1 VARCHAR2 (255),
    VALUES2 VARCHAR2 (255),
    VALUES3 VARCHAR2 (255),
    VALUES4 VARCHAR2 (255)  
) /*TABLESPACE*/;

ALTER TABLE M2_PRODUCTPROPERTIES_01 ADD ( CONSTRAINT PK__M2_PRODUCTP__OID__572B40D2 PRIMARY KEY ( oid ) );

EXIT;
