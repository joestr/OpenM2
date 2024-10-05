/******************************************************************************
 * The IBS_OPERATION table incl. indexes. <BR>
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



CREATE TABLE /*USER*/IBS_OPERATION
(
    ID NUMBER (10,0) NOT NULL,
    NAME VARCHAR2 (63) NOT NULL,
    DESCRIPTION VARCHAR2 (255)
) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_OPERATION  MODIFY (ID DEFAULT  0);
ALTER TABLE /*USER*/IBS_OPERATION  MODIFY (DESCRIPTION DEFAULT  null);
alter table /*USER*/ibs_operation modify (name default 'undefined');

exit;