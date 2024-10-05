/******************************************************************************
 * The IBS_TOKEN_01 table incl. indexes. <BR>
 * 
 *
 * @version     1.10.0001, 14.12.1999
 *
 * @author      Christine Keim (CK)  991214
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

CREATE SEQUENCE tokIdSeq
                INCREMENT BY 1
                START WITH 1
                NOMAXVALUE
                NOCYCLE
                CACHE 20;

CREATE TABLE /*USER*/IBS_TOKEN_01
(
    ID NUMBER (10,0) NOT NULL,
    LANGUAGEID NUMBER (10,0) NOT NULL,
    NAME VARCHAR2 (63) NOT NULL,
    VALUE VARCHAR2 (255) NULL,
    CLASSNAME VARCHAR2 (255)  
) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_TOKEN_01  MODIFY (ID DEFAULT 0);
ALTER TABLE /*USER*/IBS_TOKEN_01  MODIFY (LANGUAGEID DEFAULT 0);

exit;
