/******************************************************************************
 * The ibs_layout_01 table incl. indexes and triggers. <BR>
 * The object table contains all currently existing system objects.
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  980803
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804   Code cleaning.
 ******************************************************************************
 */

CREATE TABLE /*USER*/IBS_LAYOUT_01
(
    OID         RAW (8)         NOT NULL,
    NAME        VARCHAR2 (63)   NOT NULL,
    DOMAINID    NUMBER (10,0)   NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/ibs_layout_01 modify ( oid default hextoraw('0000000000000000'));
alter table /*USER*/ibs_layout_01 modify ( domainId default 0);
ALTER TABLE IBS_LAYOUT_01 ADD ( CONSTRAINT UQ__IBS_LAYOUT___OID__611EBF60 UNIQUE ( oid ) );

exit;
