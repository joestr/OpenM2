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
 * This procedure inserts a tupel into ibs_Token_01 or updates it, if it 
 * exists
 *
 * @input parameters:
 * @param   ai_languageId ID of the language (0 = default).
 * @param   ai_name       name of the Token
 * @param   ai_value      text of the Token
 * @param   ai_className  which javaclass refers to that Token
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_Token_01$new
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
        FROM    ibs_Token_01
        WHERE   languageId   = ai_languageId
           AND  name         = ai_name
           AND  classname    = ai_className;
           
    EXCEPTION
        WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_Token_01$new',
                                     'Error SELECT COUNT');
        RAISE;
    END; -- BEGIN Token
    
    IF (l_count > 0)                        -- Token extists --> UPDATE
    THEN
        BEGIN  
            UPDATE ibs_Token_01
            SET    value = ai_value
            WHERE  languageId    = ai_languageId
                AND name         = ai_name
                AND classname    = ai_className;
           
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_Token_01$new',
                                     'Error in UPDATE');
            RAISE;
        END; -- Token exists
    ELSE
        BEGIN                               -- Token does not exist --> INSERT
    
            INSERT INTO ibs_Token_01
                (languageId, name, value, className)
            VALUES
                (ai_languageId, ai_name, ai_value, ai_className);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Token_01$new',
                                      'Error in INSERT');
            RAISE;
        END; -- Token does not exist
    END IF;
    
END p_Token_01$new;
/

show errors;


/******************************************************************************
 * Get a tuple out of the table. <BR>
 * This procedure gets a tuple out of ibs_Token_01 by using the 
 * languageId and the name together as unique key.
 * If there is no tuple found the parameter ao_value is set to null.
 *
 * @input parameters:
 * @param   ai_languageId       ID of the language (0 = default).
 * @param   ai_name             Unique name of the typeName.
 *
 * @output parameters:
 * @param   ao_value            text for the typeName.
 * @param   ao_className        Java-constantclass in wich typeName is defined as Constant
 */

-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_Token_01$get
(
    -- input parameters:
    ai_languageId          INTEGER,
    ai_name                VARCHAR2,
    -- output parameters:
    ao_value               OUT VARCHAR2,
    ao_className           OUT VARCHAR2
)
AS
    -- constants:
    -- local variables:
BEGIN
-- body:
    -- initializations:
    ao_value := null;
    ao_className := null;

    -- try to get the tuple out of the table:
    SELECT  value, classname
    INTO    ao_value, ao_className
    FROM    ibs_Token_01
    WHERE   languageId = ai_languageId
      AND   name = ai_name;

    COMMIT WORK;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Token_01$get',
                          'langugeId: ' || ai_languageId ||
                          ', name: ' || ai_name ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );
END p_Token_01$get; 
-- p_Token_01$get
/


show errors;



exit;
