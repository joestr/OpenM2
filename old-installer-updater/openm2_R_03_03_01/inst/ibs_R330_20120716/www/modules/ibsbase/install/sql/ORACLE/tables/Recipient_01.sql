/******************************************************************************
 * Create the table IBS_RECIPIENT_01. <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Bernd Buchegger (BB)  990519
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */

CREATE TABLE /*USER*/IBS_RECIPIENT_01
(
    OID RAW (8) NOT NULL,
    RECIPIENTID RAW (8) NOT NULL,
    RECIPIENTNAME VARCHAR2 (63),
    READDATE DATE,
    SENTOBJECTID RAW (8),
    DELETED NUMBER (1,0) NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/ibs_recipient_01 modify ( oid default hextoraw ( '0000000000000000') );
alter table /*USER*/ibs_recipient_01 modify ( recipientid default hextoraw ( '0000000000000000') );
alter table /*USER*/ibs_recipient_01 modify ( recipientName default 'undefined' );
alter table /*USER*/ibs_recipient_01 modify ( sentObjectId default hextoraw ( '0000000000000000') );
ALTER TABLE IBS_RECIPIENT_01 ADD ( CONSTRAINT UQ__IBS_RECIPIE__OID__0DDAC090 UNIQUE ( oid ) );

exit;
