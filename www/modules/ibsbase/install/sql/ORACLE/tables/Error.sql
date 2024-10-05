/******************************************************************************
 * The IBS_DB_ERRORS table incl. indexes. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

-- create the table:
CREATE TABLE /*USER*/ibs_db_errors
(
    errorType       NUMBER (10,0)   NOT NULL,   -- type of error
    errorDate       DATE            NOT NULL,   -- date when the error occurred
    errorProc       VARCHAR2 (254)  NOT NULL,   -- procedure where the error
                                                -- occurred
    errorDesc       VARCHAR2 (2500)             -- description of error
) /*TABLESPACE*/;

exit;