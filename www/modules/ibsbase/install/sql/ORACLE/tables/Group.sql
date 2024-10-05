/******************************************************************************
 * The ibs_group_01 table incl. indexes. <BR>
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

CREATE SEQUENCE groupIdSeq
	INCREMENT BY 1
	START WITH 2097153
	NOMAXVALUE
	NOCYCLE
    NOCACHE;

CREATE TABLE /*USER*/IBS_GROUP
(
    ID NUMBER (10,0) NOT NULL,
    OID RAW (8) NOT NULL,
    STATE NUMBER (10,0) NOT NULL,
    DOMAINID NUMBER (10,0) NOT NULL,
    NAME VARCHAR2 (63) NOT NULL
) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_GROUP  MODIFY (ID DEFAULT  0);
ALTER TABLE /*USER*/IBS_GROUP  MODIFY (OID DEFAULT  hextoraw('0000000000000000'));
ALTER TABLE /*USER*/IBS_GROUP  MODIFY (state DEFAULT  2);
ALTER TABLE /*USER*/IBS_GROUP  MODIFY (domainId DEFAULT  0);
ALTER TABLE /*USER*/IBS_GROUP  MODIFY (name DEFAULT  'undefined');
exit;
