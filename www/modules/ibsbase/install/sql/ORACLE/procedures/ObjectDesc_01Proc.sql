/******************************************************************************
 * All stored procedures regarding the ibs_ObjectDesc_01 table. <BR>
 *
 * @version     2.00.0001, 29.02.2000
 *
 * @author      Klaus Reimüller (KR)  000229
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new tuple in the table. <BR>
 * This procedure inserts a tuple into ibs_ObjectDesc_01 or updates it, if it 
 * exists already.
 *
 * @input parameters:
 * @param   ai_languageId       ID of the language (0 = default).
 * @param   ai_name             Unique name of the object.
 * @param   ai_objName          Name of the business object.
 * @param   ai_objDesc          Description of the business object.
 * @param   ai_className        Java class which shall contain this object data.
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_ObjectDesc_01$new
(
    -- input parameters:
    ai_languageId           INTEGER,
    ai_name                 VARCHAR2,
    ai_objName              VARCHAR2,
    ai_objDesc              VARCHAR2,
    ai_className            VARCHAR2
    -- output parameters:
)
AS
    -- constants:
    -- local variables:
    l_count                 INTEGER := 0;

BEGIN
-- body:

    -- check if tupel exists. if exists then update the value, else insert tupel
    BEGIN
        SELECT  COUNT(id)
        INTO    l_count
        FROM    ibs_ObjectDesc_01
        WHERE   languageId   = ai_languageId
           AND  name         = ai_name
           AND  classname    = ai_className;
           
    EXCEPTION
        WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_ObjectDesc_01$new',
                                     'Error SELECT COUNT');
        RAISE;
    END; -- BEGIN Message
    
    IF (l_count > 0)                        -- Message extists --> UPDATE
    THEN
        BEGIN  
            UPDATE ibs_ObjectDesc_01
            SET    objName = ai_objName, objDesc = ai_objDesc
            WHERE  languageId    = ai_languageId
                AND name         = ai_name
                AND classname    = ai_className;
           
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_ObjectDesc_01$new',
                                     'Error in UPDATE');
            RAISE;
        END; -- Message exists
    ELSE
        BEGIN                               -- Message does not exist --> INSERT
            INSERT INTO ibs_ObjectDesc_01
                (languageId, name, objName, objDesc, className)
            VALUES
                (ai_languageId, ai_name, ai_objName, ai_objDesc, ai_className);
        EXCEPTION

            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_ObjectDesc_01$new',
                                      'Error in INSERT');
            RAISE;
        END; -- Message does not exist
    END IF;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ObjectDesc_01$new',
            'ai_languageId = ' || ai_languageId ||
            ', ai_name = ' || ai_name ||
            ', ai_objName = ' || ai_objName ||
            ', ai_objDesc = ' || ai_objDesc ||
            ', ai_className = ' || ai_className ||
            ', errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);

END p_ObjectDesc_01$new;
/

show errors;


/******************************************************************************
 * Get a tuple out of the table. <BR>
 * This procedure gets a tuple out of ibs_ObjectDesc_01 by using the 
 * languageId and the name together as unique key.
 * If there is no tuple found the parameter ao_objName is set to ' '.
 *
 * @input parameters:
 * @param   ai_languageId       ID of the language (0 = default).
 * @param   ai_name             Unique name of the object.
 *
 * @output parameters:
 * @param   ao_objName          Name of the business object.
 * @param   ao_objDesc          Description of the business object.
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_ObjectDesc_01$get
(
    -- input parameters:
    ai_languageId           INTEGER,
    ai_name                 VARCHAR2,
    -- output parameters:
    ao_objName              OUT VARCHAR2,
    ao_objDesc              OUT VARCHAR2
)
AS
    -- constants:
    -- local variables:

BEGIN
-- body:
    -- initializations:
    ao_objName := ' ';
    ao_objDesc := null;

    -- try to get the tuple out of the table:
    BEGIN
        SELECT  objName, objDesc
        INTO    ao_objName, ao_objDesc
        FROM    ibs_ObjectDesc_01
        WHERE   languageId = ai_languageId
            AND name = ai_name;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_ObjectDesc_01$get',
                                  'Error in SELECT');
            RAISE;
    END;

EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ObjectDesc_01$get',
            'ai_languageId = ' || ai_languageId ||
            ', ai_name = ' || ai_name ||
            ', errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
    
END p_ObjectDesc_01$get;
/

show errors;

EXIT;