/******************************************************************************
 * The ibs_help_01 table incl. indexes and triggers. <BR>
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

CREATE TABLE /*USER*/ibs_Help_01
(
    oid              RAW(8)        NOT NULL,
    goal             VARCHAR (2000) NULL,
    searchContent    VARCHAR (255)  NULL,
    helpUrl          VARCHAR (255)
) /*TABLESPACE*/;

exit;
