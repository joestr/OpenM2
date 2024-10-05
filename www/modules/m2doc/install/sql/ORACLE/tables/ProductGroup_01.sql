/******************************************************************************
 * The M2_PRODUCTGROUP_01 table. <BR>
 * 
 * @version     $Id: ProductGroup_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTGROUP_01
(
    OID RAW (8) NOT NULL,
    PRODUCTGROUPPROFILEOID RAW (8)
) /*TABLESPACE*/;

ALTER TABLE M2_PRODUCTGROUP_01 ADD ( CONSTRAINT PK__M2_PRODUCTG__OID__6F02CA63 PRIMARY KEY ( oid ) );

EXIT;
