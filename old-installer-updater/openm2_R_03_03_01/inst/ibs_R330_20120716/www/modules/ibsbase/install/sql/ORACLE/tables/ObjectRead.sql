/******************************************************************************
 * The IBS_OBJECTREAD table incl. indexes. <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  990804
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990804    Code cleaning.
 ******************************************************************************
 */



CREATE TABLE /*USER*/IBS_OBJECTREAD
(
OID RAW (8) NOT NULL,
userId NUMBER (10,0) NOT NULL,
HASREAD NUMBER (1,0) NOT NULL,
LASTREAD DATE NOT NULL
) /*TABLESPACE*/;
alter table /*USER*/ibs_objectread modify ( oid default hextoraw('0000000000000000'));
alter table /*USER*/ibs_objectread modify ( userId default 0);

exit;
