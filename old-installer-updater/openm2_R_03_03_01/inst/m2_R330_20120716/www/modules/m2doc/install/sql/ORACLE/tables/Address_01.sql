/******************************************************************************
 * The m2_Address_01 table incl. indexes. <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  990804
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990804    Code cleaning.
 * <DD>HB 990810    EXIT inserted.
 ******************************************************************************
 */

CREATE TABLE /*USER*/m2_Address_01
(
    OID RAW (8) NOT NULL,
    STREET VARCHAR2 (63),
    ZIP VARCHAR2 (15),
    TOWN VARCHAR2 (63),
    MAILBOX VARCHAR2 (15),
    COUNTRY VARCHAR2 (31),
    TEL VARCHAR2 (63),
    FAX VARCHAR2 (63),
    EMAIL VARCHAR2 (127),
    HOMEPAGE VARCHAR2 (255)  
) /*TABLESPACE*/;

alter table /*USER*/m2_Address_01 modify ( oid default hextoraw('0000000000000000'));
ALTER TABLE m2_Address_01 ADD ( CONSTRAINT UQ__m2_Address__OID__357F68ED UNIQUE ( oid ) );

EXIT;
