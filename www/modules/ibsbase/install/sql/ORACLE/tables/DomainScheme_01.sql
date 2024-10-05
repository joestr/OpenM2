/******************************************************************************
 * Create table IBS_DOMAINSCHEME_01.
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

CREATE SEQUENCE domainSchemeIdSeq
	INCREMENT BY 1
	START WITH 1
	NOMAXVALUE
	NOCYCLE
        NOCACHE;

CREATE TABLE /*USER*/IBS_DOMAINSCHEME_01(ID NUMBER (10,0) NOT NULL,OID RAW (8)  ,WORKSPACEPROC VARCHAR2 (63)  ,HASCATALOGMANAGEMENT NUMBER (1,0) NOT NULL,HASDATAINTERCHANGE NUMBER (1,0)  ) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_DOMAINSCHEME_01  MODIFY (ID DEFAULT 0);
ALTER TABLE /*USER*/IBS_DOMAINSCHEME_01  MODIFY (oid DEFAULT hextoraw('0000000000000000'));

exit;
