/******************************************************************************
 * Create table IBS_KEYMAPPER and indices . <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Bernd Buchegger (BB)  990519
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */

CREATE TABLE /*USER*/ibs_KeyMapper
(
    oid         RAW(8)          NOT NULL,
    id          VARCHAR2(255)   NOT NULL,
    idDomain    VARCHAR2(63)    NOT NULL
) /*TABLESPACE*/;

exit;
