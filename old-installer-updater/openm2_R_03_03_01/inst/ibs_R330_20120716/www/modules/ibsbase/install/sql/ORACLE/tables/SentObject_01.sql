/******************************************************************************
 * Create the table IBS_SENTOBJECT_01. <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  990308
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */
 
CREATE TABLE /*USER*/IBS_SENTOBJECT_01
(
    OID RAW (8) NOT NULL,
    DISTRIBUTEID RAW (8) NOT NULL,
    DISTRIBUTETVERSIONID NUMBER (10,0),
    DISTRIBUTETYPENAME VARCHAR2 (63),
    DISTRIBUTENAME VARCHAR2 (63),
    DISTRIBUTEICON VARCHAR2 (63),
    ACTIVITIES VARCHAR2 (63),
    DELETED NUMBER (1,0) NOT NULL
) /*TABLESPACE*/;
alter table /*USER*/ibs_sentObject_01 modify ( oid default hextoraw('0000000000000000'));
alter table /*USER*/ibs_sentObject_01 modify ( distributeid default hextoraw('0000000000000000'));
alter table /*USER*/ibs_sentObject_01 modify ( DISTRIBUTETVERSIONID default 0);
alter table /*USER*/ibs_sentObject_01 modify ( distributeTypeName default 'undefined');
alter table /*USER*/ibs_sentObject_01 modify ( distributeName default 'undefined');
alter table /*USER*/ibs_sentObject_01 modify ( distributeIcon default 'undefined');
alter table /*USER*/ibs_sentObject_01 modify ( activities default 'undefined');

ALTER TABLE IBS_SENTOBJECT_01 ADD ( CONSTRAINT UQ__IBS_SENTOBJ__OID__4A905FC3 UNIQUE ( oid ) );

exit;

