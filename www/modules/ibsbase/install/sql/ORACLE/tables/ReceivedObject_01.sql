/******************************************************************************
 * Create the table ibs_RECEIVEOBJECT_01. <BR>
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

-- tables:
CREATE TABLE /*USER*/IBS_RECEIVEDOBJECT_01
(
    OID RAW (8) NOT NULL,
    DISTRIBUTEDID RAW (8) NOT NULL,
    DISTRIBUTEDTVERSIONID NUMBER (10,0),
    DISTRIBUTEDTYPENAME VARCHAR2 (63),
    DISTRIBUTEDNAME VARCHAR2 (63),
    DISTRIBUTEDICON VARCHAR2 (63),
    ACTIVITIES VARCHAR2 (63),
    SENTOBJECTID RAW (8) NOT NULL,
    SENDERFULLNAME VARCHAR2 (63)  
) /*TABLESPACE*/;

alter table /*USER*/ibs_receivedObject_01 modify ( oid default hextoraw('0000000000000000'));
alter table /*USER*/ibs_receivedObject_01 modify ( distributedId default hextoraw('0000000000000000'));
alter table /*USER*/ibs_receivedObject_01 modify ( distributedTVersionId default 0);
alter table /*USER*/ibs_receivedObject_01 modify ( distributedTypeName default 'undefined');
alter table /*USER*/ibs_receivedObject_01 modify ( distributedName default 'undefined');
alter table /*USER*/ibs_receivedObject_01 modify ( distributedIcon default 'undefined');
alter table /*USER*/ibs_receivedObject_01 modify ( activities default 'undefined');
alter table /*USER*/ibs_receivedObject_01 modify ( sentObjectId default hextoraw('0000000000000000'));
alter table /*USER*/ibs_receivedObject_01 modify ( senderFullName default 'undefined');

ALTER TABLE IBS_RECEIVEDOBJECT_01 ADD ( CONSTRAINT UQ__IBS_RECEIVE__OID__77024830 UNIQUE ( oid ) );

exit;
