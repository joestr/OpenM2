/******************************************************************************
 * Create table IBS_SENTOBJECTCONTAINER_01. <BR>
 * The object table contains all currently existing system objects.
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  980803
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803   Code cleaning.
 ******************************************************************************
 */

CREATE TABLE /*USER*/IBS_SENTOBJECTCONTAINER_01
(
    OID RAW (8) NOT NULL,
    NUMBEROFDAYS NUMBER (10,0) NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/ibs_sentobjectcontainer_01 modify ( oid default hextoraw ( '0000000000000000' ));
ALTER TABLE IBS_SENTOBJECTCONTAINER_01 ADD ( CONSTRAINT UQ__IBS_SENTOBJ__OID__139399E6 UNIQUE ( oid ) );

exit;
