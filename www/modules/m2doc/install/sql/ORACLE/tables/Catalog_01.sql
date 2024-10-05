/******************************************************************************
 * The M2_CATALOG_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Catalog_01.sql,v 1.8 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_CATALOG_01
(
    OID RAW (8) NOT NULL,
    COMPANYOID RAW (8),
    ORDRESP RAW (8),
    ORDRESPMED RAW (8),
    CONTRESP RAW (8),
    CONTRESPMED RAW (8),
    LOCKED NUMBER (1,0) NOT NULL,
    DESCRIPTION1 VARCHAR2 (255),
    DESCRIPTION2 VARCHAR2 (255),  
    ISORDEREXPORT NUMBER (1,0) NOT NULL,
    CONNECTOROID RAW (8),
    TRANSLATOROID RAW (8),
    FILTERID NUMBER (3,0),
    NOTIFYBYEMAIL NUMBER (1,0) NOT NULL, 
    SUBJECT VARCHAR2 (255) NULL, 
    CONTENT VARCHAR2 (255) NULL 
) /*TABLESPACE*/;

ALTER TABLE M2_CATALOG_01 ADD ( CONSTRAINT PK__M2_CATALOG___OID__71496EDA PRIMARY KEY ( oid ) );

EXIT;
