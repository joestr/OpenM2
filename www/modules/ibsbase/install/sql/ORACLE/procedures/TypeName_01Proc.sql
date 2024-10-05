/******************************************************************************
 * All stored procedures regarding the ibs_TypeName_01 table. <BR>
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
 * This procedure inserts a tupel into ibs_TypeName_01 or updates it, if it
 * exists
 *
 * @input parameters:
 * @param   ai_languageId ID of the language (0 = default).
 * @param   ai_name       name of the TypeName
 * @param   ai_value      text of the TypeName
 * @param   ai_className  which javaclass refers to that TypeName
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_TypeName_01$new
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
    -- variables needed for the updates on ibs_Object and ibs_Type
    l_name      VARCHAR2 (63);
    l_pos       INTEGER := -1;
    l_tVersionId NUMBER (10);


BEGIN

    -- check if tupel exists. if exists then update the value, else insert tupel
    BEGIN
        SELECT  COUNT(id)
        INTO    l_count
        FROM    ibs_TypeName_01
        WHERE   languageId   = ai_languageId
           AND  name         = ai_name
           AND  classname    = ai_className;

    EXCEPTION
        WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_TypeName_01$new',
                                     'Error SELECT COUNT');
        RAISE;
    END; -- BEGIN TypeName

    IF (l_count > 0)                        -- TypeName extists --> UPDATE
    THEN
        BEGIN
            UPDATE ibs_TypeName_01
            SET    value = ai_value
            WHERE  languageId    = ai_languageId
                AND name         = ai_name
                AND classname    = ai_className;

        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_TypeName_01$new',
                                     'Error in UPDATE');
            RAISE;
        END; -- TypeName exists
    ELSE
        BEGIN                               -- TypeName does not exist --> INSERT

            INSERT INTO ibs_TypeName_01
                (languageId, name, value, className)
            VALUES
                (ai_languageId, ai_name, ai_value, ai_className);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_TypeName_01$new',
                                      'Error in INSERT');
            RAISE;
        END; -- TypeName does not exist
    END IF;
    COMMIT WORK;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_TypeName_01$new',
                          'langugeId: ' || ai_languageId ||
                          ', name: ' || ai_name ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );

END p_TypeName_01$new;
/

show errors;


/******************************************************************************
 * Get a tuple out of the table. <BR>
 * This procedure gets a tuple out of ibs_TypeName_01 by using the
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
CREATE OR REPLACE PROCEDURE p_TypeName_01$get
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
    FROM    ibs_TypeName_01
    WHERE   languageId = ai_languageId
      AND   name = ai_name;

    COMMIT WORK;

EXCEPTION
    WHEN NO_DATA_FOUND THEN         -- object not found?
        -- set return value:
        ao_value := NULL;
        ao_className := NULL;
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_TypeName_01$get',
                          'langugeId: ' || ai_languageId ||
                          ', name: ' || ai_name ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );
END p_TypeName_01$get;
-- p_TypeName_01$get
/


show errors;

exit;
