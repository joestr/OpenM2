/******************************************************************************
 * The ibs_protocol_01 table incl. indexes and triggers. <BR>
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

CREATE SEQUENCE protocolIdSeq
	INCREMENT BY 1
	START WITH 1
	NOMAXVALUE
	NOCYCLE
	CACHE 20;

CREATE TABLE /*USER*/IBS_PROTOCOL_01
(
    ID NUMBER (10,0) NOT NULL,
    OID RAW (8) NOT NULL,
    FULLNAME VARCHAR2 (63) NOT NULL,
    USERID NUMBER (10,0) NOT NULL,
    OBJECTNAME VARCHAR2 (63) NOT NULL,
    ICON VARCHAR2 (63) NOT NULL,
    TVERSIONID NUMBER (10,0) NOT NULL,
    CONTAINERID RAW (8) NOT NULL,
    CONTAINERKIND NUMBER (10,0) NOT NULL,
    OWNER NUMBER (10,0) NOT NULL,
    ACTION NUMBER (10,0) NOT NULL,
    ACTIONDATE DATE NOT NULL
) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (id DEFAULT 0);
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (oid DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (fullName DEFAULT 'UNKNOWN');
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (userId DEFAULT 0);
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (objectName DEFAULT 'UNKNOWN');
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (icon DEFAULT 'icon.gif');
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (tVersionId DEFAULT 0);
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (containerId DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (containerKind DEFAULT 0);
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (owner DEFAULT 0);
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (action DEFAULT 0);
ALTER TABLE /*USER*/IBS_PROTOCOL_01  MODIFY (actionDate DEFAULT SYSDATE);

exit;
