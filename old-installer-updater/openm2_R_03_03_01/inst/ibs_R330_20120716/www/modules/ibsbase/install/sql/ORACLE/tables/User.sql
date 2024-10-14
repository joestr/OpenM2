/******************************************************************************
 * The ibs_user table incl. indexes. <BR>
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

CREATE SEQUENCE userIdSeq
	INCREMENT BY 1
	START WITH 8388609
	NOMAXVALUE
	NOCYCLE
	CACHE 5;

CREATE TABLE /*USER*/IBS_USER(ID NUMBER (10,0) NOT NULL,OID RAW (8) NOT NULL,STATE NUMBER (10,0) NOT NULL,DOMAINID NUMBER (10,0) NOT NULL,NAME VARCHAR2 (63) NOT NULL,PASSWORD VARCHAR2 (63)  ,FULLNAME VARCHAR2 (63)  ,ADMIN NUMBER (1,0) NOT NULL), CHANGEPWD NUMBER (1, 0) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_USER  MODIFY (ID DEFAULT  0);
ALTER TABLE /*USER*/IBS_USER  MODIFY (admin DEFAULT  0);
alter table /*USER*/ibs_user modify ( oid default hextoraw('0000000000000000'));
alter table /*USER*/ibs_user modify ( state default 2 );
alter table /*USER*/ibs_user modify ( domainid default 0);
alter table /*USER*/ibs_user modify ( name default 'undefined');
alter table /*USER*/ibs_user modify ( password default 'undefined');
alter table /*USER*/ibs_user modify ( fullname default 'undefined');
alter table /*USER*/ibs_user modify ( changePwd DEFAULT 0);

exit;
