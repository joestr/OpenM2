/******************************************************************************
 * All stored procedures regarding the ibs_Token_01 table. <BR>
 *
 * @version     1.11.0001, 14.12.1999
 *
 * @author      Harald Buzzi    (HB)  991214
 *
 * <DT><B>Updates:</B>
 *
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new tupel in the table. <BR>
 * This procedure inserts a tupel into ibs_Message_01 or updates it, if it 
 * exists
 *
 * @input parameters:
 * @param   ai_languageId ID of the language (0 = default).
 * @param   ai_name       name of the Message
 * @param   ai_value      text of the Message
 * @param   ai_className  which javaclass refers to that Message
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_Message_01$new
(
    -- input parameters:
    ai_languageId  INTEGER,
    ai_name        VARCHAR2,
    ai_value       VARCHAR2,
    ai_className   VARCHAR2
    -- output parameters
)
AS
    -- local valriables
    l_count     INTEGER := 0;

BEGIN

    -- check if tupel exists. if exists then update the value, else insert tupel
    BEGIN
        SELECT  COUNT(id)
        INTO    l_count
        FROM    ibs_Message_01
        WHERE   languageId   = ai_languageId
           AND  name         = ai_name
           AND  classname    = ai_className;
           
    EXCEPTION
        WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_Message_01$new',
                                     'Error SELECT COUNT');
        RAISE;
    END; -- BEGIN Message
    
    IF (l_count > 0)                        -- Message extists --> UPDATE
    THEN
        BEGIN  
            UPDATE ibs_Message_01
            SET    value = ai_value
            WHERE  languageId    = ai_languageId
                AND name         = ai_name
                AND classname    = ai_className;
           
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_Message_01$new',
                                     'Error in UPDATE');
            RAISE;
        END; -- Message exists
    ELSE
        BEGIN                               -- Message does not exist --> INSERT
    
            INSERT INTO ibs_Message_01
                (languageId, name, value, className)
            VALUES
                (ai_languageId, ai_name, ai_value, ai_className);

        EXCEPTION

            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Message_01$new',
                                      'Error in INSERT');
            RAISE;
        END; -- Message does not exist
    END IF;
    
END p_Message_01$new;
/

show errors;

exit;
