/******************************************************************************
 * The ibs_rightskeys table incl. indexes. <BR>
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

CREATE SEQUENCE rightsKeysSeq
	INCREMENT BY 1
	START WITH 1
	NOMAXVALUE
	NOCYCLE
	CACHE 20;

CREATE TABLE /*USER*/ibs_RightsKeys
(
    id NUMBER(10,0) NOT NULL,
    oid RAW(8) NULL, 
    rPersonId NUMBER(10,0) NOT NULL,
    rights NUMBER(10,0) NOT NULL, 
    cnt NUMBER(10,0) NOT NULL,
    r00  NUMBER(10,0) NULL,
    r01  NUMBER(10,0) NULL,
    r02  NUMBER(10,0) NULL,
    r03  NUMBER(10,0) NULL,
    r04  NUMBER(10,0) NULL,
    r05  NUMBER(10,0) NULL,
    r06  NUMBER(10,0) NULL,
    r07  NUMBER(10,0) NULL,
    r08  NUMBER(10,0) NULL,
    r09  NUMBER(10,0) NULL,
    r0A  NUMBER(10,0) NULL,
    r0B  NUMBER(10,0) NULL,
    r0C  NUMBER(10,0) NULL,
    r0D  NUMBER(10,0) NULL,
    r0E  NUMBER(10,0) NULL,
    r0F  NUMBER(10,0) NULL,
    r10  NUMBER(10,0) NULL,
    r11  NUMBER(10,0) NULL,
    r12  NUMBER(10,0) NULL,
    r13  NUMBER(10,0) NULL,
    r14  NUMBER(10,0) NULL,
    r15  NUMBER(10,0) NULL,
    r16  NUMBER(10,0) NULL,
    r17  NUMBER(10,0) NULL,
    r18  NUMBER(10,0) NULL,
    r19  NUMBER(10,0) NULL,
    r1A  NUMBER(10,0) NULL,
    r1B  NUMBER(10,0) NULL,
    r1C  NUMBER(10,0) NULL,
    r1D  NUMBER(10,0) NULL,
    r1E  NUMBER(10,0) NULL,
    r1F  NUMBER(10,0) NULL    
) /*TABLESPACE*/;
alter table /*USER*/ibs_rightsKeys modify ( id default 0 );
alter table /*USER*/ibs_rightsKeys modify ( oid default hextoraw('0000000000000000') );
alter table /*USER*/ibs_rightsKeys modify ( rights default 0 );
alter table /*USER*/ibs_rightsKeys modify ( cnt default 0 );

exit;
