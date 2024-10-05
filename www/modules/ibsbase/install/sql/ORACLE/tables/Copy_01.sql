/******************************************************************************
 * The ibs_copy table. <BR>
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

CREATE TABLE /*USER*/IBS_COPY
(
    OLDOID RAW (8) NOT NULL,
    NEWOID RAW (8) NOT NULL,
    COPYID INTEGER NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/ibs_copy modify (oldoid default hextoraw('0000000000000000'));
alter table /*USER*/ibs_copy modify (newoid default hextoraw('0000000000000000'));

exit;
