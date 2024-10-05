/******************************************************************************
 * All stored procedures regarding the ibs_System table. <BR>
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
 * The state of a newly inserted tuple is automatically set to 2 (active).
 *
 * @input parameters:
 * @param   ai_name             Unique name of the value.
 * @param   ai_type             Type of the value.
 * @param   ai_value            The value itself.
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_System$new
(
    -- input parameters:
    ai_name                 VARCHAR2,
    ai_type                 VARCHAR2,
    ai_value                VARCHAR2
    -- output parameters:
)
AS
    -- constants:
    -- local variables:
    l_count                 INTEGER;

BEGIN
-- body:

    BEGIN
        SELECT  COUNT(id)
        INTO    l_count
        FROM    ibs_System
        WHERE   name = ai_name;           
    EXCEPTION
        WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_System_01$new',
                                     'Error SELECT COUNT');
        RAISE;
    END; -- BEGIN Message
    
    IF (l_count > 0)                        -- Message extists --> UPDATE
    THEN
        BEGIN  
            UPDATE  ibs_System
            SET     type = ai_type,
                    value = ai_value
            WHERE   name = ai_name;           
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 'p_System_01$new',
                                     'Error in UPDATE');
            RAISE;
        END; -- Message exists
    ELSE
        BEGIN                               -- Message does not exist --> INSERT    
            INSERT INTO ibs_System
                (state, name, type, value)
            VALUES
                (2, ai_name, ai_type, ai_value);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_System_01$new',
                                      'Error in INSERT');
            RAISE;
        END; -- Message does not exist
    END IF;
END p_System$new;
/

show errors;


/******************************************************************************
 * Get a value out of the table. <BR>
 * This procedure gets a value out of ibs_System by using the name as
 * unique key.
 * If there is no tuple found the parameter ao_value is set to null.
 *
 * @input parameters:
 * @param   ai_name             Unique name of the object.
 *
 * @output parameters:
 * @param   ao_value            The value out of the table.
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_System$get
(
    -- input parameters:
    ai_name                 VARCHAR2,
    -- output parameters:
    ao_value                OUT VARCHAR2
)
AS
    -- constants:
    -- local variables:

BEGIN
-- body:
    -- initializations:
    ao_value := null;

    -- try to get the tuple out of the table:
    BEGIN
        SELECT  value
        INTO    ao_value
        FROM    ibs_System
        WHERE   name = ai_name;
    EXCEPTION
        WHEN OTHERs THEN
            ibs_error.log_error ( ibs_error.error, 'p_System$get',
                                  'Error in SELECT');
            RAISE;
    END;
END p_System$get;
/

show errors;


/******************************************************************************
 * Get an integer value out of the table. <BR>
 * This procedure gets a value out of ibs_System by using the name as
 * unique key.
 * The value is converted to INTEGER.
 * If there is no tuple found or the value is no valid INTEGER the parameter 
 * ao_value is set to null.
 *
 * @input parameters:
 * @param   ai_name             Unique name of the object.
 *
 * @output parameters:
 * @param   ao_value            The value out of the table.
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_System$getInt
(
    -- input parameters:
    ai_name                 VARCHAR2,
    -- output parameters:
    ao_value                OUT INTEGER
)
AS
    -- constants:
    -- local variables:

BEGIN
-- body:
    -- initializations:
    ao_value := null;

    -- try to get the tuple out of the table:
    BEGIN
        SELECT  TO_NUMBER (value)
        INTO    ao_value
        FROM    ibs_System
        WHERE   name = ai_name;
    EXCEPTION
        WHEN OTHERs THEN
            ibs_error.log_error ( ibs_error.error, 'p_System$getInt',
                                  'Error in SELECT');
            RAISE;
    END;
END p_System$getInt;
/

show errors;

EXIT;
