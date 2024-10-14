/******************************************************************************
 * The ibs_groupuser table incl. indexes. <BR>
 * The objet table contains all currently existing system objects.
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

CREATE SEQUENCE groupUserIdSeq
	INCREMENT BY 1
	START WITH 27262977
	NOMAXVALUE
	NOCYCLE
	CACHE 5;

CREATE TABLE /*USER*/IBS_GROUPUSER
(
    ID NUMBER (10,0) NOT NULL,
    STATE NUMBER (10,0) NOT NULL,
    GROUPID NUMBER (10,0) NOT NULL,
    USERID NUMBER (10,0) NOT NULL,
    ROLEID NUMBER (10,0),
    ORIGGROUPID NUMBER (10,0) NOT NULL,
    IDPATH RAW (254) NOT NULL
) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_GROUPUSER  MODIFY (ID DEFAULT  0);
ALTER TABLE /*USER*/IBS_GROUPUSER  MODIFY (STATE DEFAULT  2);
ALTER TABLE /*USER*/IBS_GROUPUSER  MODIFY (GROUPID DEFAULT  0);
ALTER TABLE /*USER*/IBS_GROUPUSER  MODIFY (USERID DEFAULT  0);
ALTER TABLE /*USER*/IBS_GROUPUSER  MODIFY (ROLEID DEFAULT  0);
ALTER TABLE /*USER*/IBS_GROUPUSER  MODIFY (ORIGGROUPID DEFAULT  0);
alter table /*USER*/ibs_groupuser modify (idPath default hextoraw('0000'));

exit;
