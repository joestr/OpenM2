/******************************************************************************
 * The IBS_SYSTEM table incl. indexes. <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  990804
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990804    Code cleaning.
 ******************************************************************************
 */

CREATE SEQUENCE systemIdSeq
    INCREMENT BY 1
    START WITH 1
    NOMAXVALUE
    NOCYCLE
    NOCACHE;

CREATE TABLE /*USER*/IBS_SYSTEM
(
    ID NUMBER (10,0) NOT NULL,
    STATE NUMBER (10,0) NOT NULL,
    NAME VARCHAR2 (63) NOT NULL,
    TYPE VARCHAR2 (63),
    VALUE VARCHAR2 (255) NULL
) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_SYSTEM  MODIFY (ID DEFAULT  0);
ALTER TABLE /*USER*/IBS_SYSTEM  MODIFY (STATE DEFAULT  2);
ALTER TABLE /*USER*/IBS_SYSTEM  MODIFY (NAME DEFAULT 'undefined');
ALTER TABLE /*USER*/IBS_SYSTEM  MODIFY (TYPE DEFAULT 'undefined');
ALTER TABLE /*USER*/IBS_SYSTEM  MODIFY (VALUE DEFAULT NULL);

exit;
