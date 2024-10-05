/******************************************************************************
 * The ibs_attachment_01 table incl. indexes and triggers. <BR>
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

CREATE TABLE /*USER*/IBS_ATTACHMENT_01
(
    OID RAW (8) NOT NULL,
    FILENAME VARCHAR2 (255) NOT NULL,
    PATH VARCHAR2 (255) NOT NULL,
    FILESIZE FLOAT (24) NOT NULL,
    URL VARCHAR2 (255) NOT NULL,
    ATTACHMENTTYPE NUMBER (10,0) NOT NULL,
    ISMASTER NUMBER (1,0) NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/ibs_attachment_01 modify ( oid default hextoraw('0000000000000000'));
ALTER TABLE IBS_ATTACHMENT_01 ADD ( CONSTRAINT UQ__IBS_ATTACHM__OID__0AFE53E5 UNIQUE ( oid ) );

exit;
