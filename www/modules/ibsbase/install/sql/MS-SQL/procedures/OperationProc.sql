/******************************************************************************
 * All stored procedures regarding the operation table. <BR>
 * 
 *
 * @version     1.10.0001, 02.08.1999
 *
 * @author      Klaus Reimüller (KR)  980528
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new operation (incl. rights check). <BR>
 * If operation (op) already exists values will be updated.
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
-- delete existing procedure:
EXEC p_dropProc N'p_Operation$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_Operation$new
(
    -- input parameters:
    @ai_op             INT,
    @ai_name           NAME,
    @ai_description    DESCRIPTION
)
AS
    DECLARE @c_ALL_RIGHT INT                  -- constant
    SELECT  @c_ALL_RIGHT = 1              -- return values

    DECLARE @l_retValue INT                   -- return value of this procedure
    SELECT  @l_retValue = @c_ALL_RIGHT

    IF EXISTS ( SELECT * FROM ibs_Operation WHERE id = @ai_op)
        UPDATE ibs_Operation
        SET name = @ai_name, description = @ai_description
        WHERE  id = @ai_op
    ELSE
        INSERT INTO ibs_Operation (id, name, description)
        VALUES (@ai_op, @ai_name, @ai_description)

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Operation$new
