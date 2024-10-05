/******************************************************************************
 * All stored procedures regarding the operation table. <BR>
 * 
 * @version     2.21.0004, 28.06.2002 KR
 *
 * @author      Mario Stegbauer (MS)  990805
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new operation (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @op                 Operation to be created.
 * @param   @name               Name of the operation.
 * @param   @description        Description of the operation.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
CREATE OR REPLACE FUNCTION p_Operation$new
(
    ai_op           NUMBER ,
    ai_name         VARCHAR2 ,
    ai_description  VARCHAR2 
)
RETURN INTEGER
AS
    c_ALL_RIGHT     NUMBER(10,0) := 1;
    l_retValue      NUMBER(10,0) := c_ALL_RIGHT;
    l_rowCount      NUMBER(10,0) := 0;

BEGIN
    SELECT count (*) INTO l_rowcount FROM ibs_Operation WHERE id = ai_op;

    IF (l_rowcount > 0)
    THEN
        UPDATE ibs_Operation
        SET    name = ai_name, description = ai_description
        WHERE  id = ai_op;
    ELSE
        INSERT INTO ibs_Operation (id, name, description)
        VALUES (ai_op, ai_name, ai_description);
    END IF;

    COMMIT WORK;
    
    RETURN l_retValue;

EXCEPTION
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Operation$new',
                          'op: ' || ai_op ||
                          ', name: ' || ai_name ||
                          ', description: ' || ai_description ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );
    return 0;
END p_Operation$new;
/

show errors;

exit;