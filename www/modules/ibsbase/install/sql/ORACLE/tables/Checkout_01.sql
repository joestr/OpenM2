/******************************************************************************
 * The IBS_Checkout_01 table incl. indexes. <BR>
 * 
 *
 * @version     1.10.0001, 20.02.2000
 *
 * @author      Christine Keim (CK)  000220
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */


CREATE TABLE /*USER*/IBS_CHECKOUT_01
(
    OID             RAW (8) NOT NULL,
    USERID          INTEGER NOT NULL,
    CHECKOUT        DATE    NOT NULL
) /*TABLESPACE*/;

exit;
