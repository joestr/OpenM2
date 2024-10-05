/******************************************************************************
 * All stored procedures regarding the ibs_Exception_01 table. <BR>
 *
 * @version     1.11.0001, 15.12.1999
 *
 * @author      Ralf Werl    (RW)  991215
 *
 * <DT><B>Updates:</B>
 *
 ******************************************************************************/

/******************************************************************************
 * Creates a new tupel in the table. <BR>
 * This procedure inserts a tupel into ibs_Exception_01 or updates it, if it 
 * exists
 *
 * @input parameters:
 * @param   ai_languageId ID of the language (0 = default).
 * @param   ai_name       name of the Exception
 * @param   ai_value      text of the Exception
 * @param   ai_className  which javaclass refers to that Exception
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_Exception_01$new
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
        FROM    ibs_Exception_01
        WHERE   languageId   = ai_languageId
           AND  name         = ai_name
           AND  classname    = ai_className;
           
    EXCEPTION
        WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_Exception_01$new',
                                     'Error SELECT COUNT');
        RAISE;
    END; -- BEGIN EXCEPTION
    
    IF (l_count > 0)                        -- Exception extists --> UPDATE
    THEN
        BEGIN  
            UPDATE ibs_Exception_01
            SET    value = ai_value
            WHERE  languageId    = ai_languageId
                AND name         = ai_name
                AND classname    = ai_className;
           
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_Exception_01$new',
                                     'Error in UPDATE');
        RAISE;
    END; -- Exception exists
    ELSE
        BEGIN                               -- Exception does not exist --> INSERT
    
            INSERT INTO ibs_Exception_01
                (languageId, name, value, className)
            VALUES
                (ai_languageId, ai_name, ai_value, ai_className);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Exception_01$new',
                                      'Error in INSERT');
        RAISE;
        END; -- Exception does not exist
    END IF;
    
END p_Exception_01$new;
/

show errors;


exit;
