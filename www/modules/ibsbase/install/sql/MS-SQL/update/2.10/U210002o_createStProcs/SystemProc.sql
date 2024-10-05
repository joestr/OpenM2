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
-- delete existing procedure:
EXEC p_dropProc N'p_System$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_System$new
(
    -- input parameters:
    @ai_name                NAME,
    @ai_type                NAME,
    @ai_value               NVARCHAR (255)
    -- output parameters:
)
AS
    -- constants:
    -- local variables:

-- body:
    -- try to update the already existing tuple:
    UPDATE  ibs_System
    SET     type = @ai_type,
            value = @ai_value
    WHERE   name = @ai_name

    IF (@@ROWCOUNT <= 0)                -- the tuple does not exist?
    BEGIN
        -- insert a new tuple:
        INSERT INTO ibs_System
            (state, name, type, value)
        VALUES
            (2, @ai_name, @ai_type, @ai_value)
    END -- if the tuple does not exist
GO
-- p_System$new


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
-- delete existing procedure:
EXEC p_dropProc N'p_System$get'
GO

-- create the new procedure:
CREATE PROCEDURE p_System$get
(
    -- input parameters:
    @ai_name                NAME,
    -- output parameters:
    @ao_value               NVARCHAR (255) OUTPUT
)
AS
    -- constants:
    -- local variables:

-- body:
    -- initializations:
    SELECT
        @ao_value = null

    -- try to get the tuple out of the table:
    SELECT  @ao_value = value
    FROM    ibs_System
    WHERE   name = @ai_name
GO
-- p_System$get


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
-- delete existing procedure:
EXEC p_dropProc N'p_System$getInt'
GO

-- create the new procedure:
CREATE PROCEDURE p_System$getInt
(
    -- input parameters:
    @ai_name                NAME,
    -- output parameters:
    @ao_value               INTEGER OUTPUT
)
AS
    -- constants:
    -- local variables:

-- body:
    -- initializations:
    SELECT
        @ao_value = null

    -- try to get the tuple out of the table:
    SELECT  @ao_value = CONVERT (INTEGER, value)
    FROM    ibs_System
    WHERE   name = @ai_name
GO
-- p_System$getInt
