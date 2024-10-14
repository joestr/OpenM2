/******************************************************************************
 * All stored procedures regarding the error handling. <BR>
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stebauer (MS)  990805
 ******************************************************************************
 */
set define off;

CREATE OR REPLACE PACKAGE ibs_error
AS
    -- some important error types:
    fatal_error         constant number (10,0) := 100;
    error               constant number (10,0) := 500;
    minor_error         constant number (10,0) := 1000;
    warning             constant number (10,0) := 10000;
    MESSAGE             constant number (10,0) := 100000;

    -- Log a message (or error) of the given type
    PROCEDURE log_error
    (
        ai_errorType        ibs_db_errors.errorType%Type,
        ai_errorProc        ibs_db_errors.errorProc%Type,
        ai_errorDesc        ibs_db_errors.errorDesc%Type
    );

    -- Clean the ibs_db_errors table
    PROCEDURE cleanErrorLog;
END;
/


CREATE OR REPLACE PACKAGE BODY ibs_error
AS
    error_level     constant number (10,0) := 9999;
    log_level       Number (10,0) := MESSAGE;


    /**************************************************************************
     * Log an error. <BR>
     * There is NO COMMIT done within this procedure. To ensure that the data is
     * stored permanently in the error log there must be a commit afterwards.
     *
     * @input parameters:
     * @param   ai_errorType        The type of error.
     * @param   ai_errorProc        The procedure where the error occurred.
     * @param   ai_errorDesc        The description of the error.
     *
     * @output parameters:
     */
    PROCEDURE log_error 
    (
        ai_errorType        ibs_db_errors.errorType%Type,
        ai_errorProc        ibs_db_errors.errorProc%Type,
        ai_errorDesc        ibs_db_errors.errorDesc%Type
    )
    IS
    BEGIN
        -- check if the error shall be logged:
        IF (log_level > ai_errorType)   -- log the error?
        THEN
            -- save the error and the message:
            INSERT INTO ibs_db_errors 
                (errorType, errorDate, errorProc, errorDesc)
            VALUES (ai_errorType, sysDate, ai_errorProc, ai_errorDesc);
        END IF; -- log the error

    EXCEPTION
        WHEN OTHERS THEN                -- no logging possible
            dbms_output.put_line (
                '-- ibs_error.log_error reported:');
            dbms_output.put_line (
                '   Errortype ' || ai_errorType ||
                ' in ' || ai_errorProc);
            dbms_output.put_line (
                '   Description: ' || ai_errorDesc);
    END; -- log_error


    PROCEDURE cleanErrorLog
    IS
    BEGIN
        DELETE  ibs_db_errors;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            log_error (error, 'ibs_error.clean_error_log', SQLCODE || SQLERRM);
    END; -- cleanErrorLog

END;
/

exit;