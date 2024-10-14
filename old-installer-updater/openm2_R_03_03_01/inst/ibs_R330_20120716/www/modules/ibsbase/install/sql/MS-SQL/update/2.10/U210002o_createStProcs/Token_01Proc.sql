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
 * This procedure inserts a tupel into ibs_Token_01 or updates it, if it exists
 *
 * @input parameters:
 * @param   @languageId ID of the language (0 = default).
 * @param   @name       name of the token
 * @param   @value      text of the token
 * @param   @className  which javaclass refers to that token
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Token_01$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_Token_01$new
(
    -- input parameters:
    @languageId  ID,
    @name        DESCRIPTION,
    @value       DESCRIPTION,
    @className   FILENAME
    -- output parameters
)
AS
    -- declare local variables
    DECLARE @newValue DESCRIPTION       -- new value with '~' instead of trailing blanks
    
    -- trailing blanks will be changed into '~', 
    -- because MS-SQL-Server cuts all trailing blanks
    SELECT @newValue = RTRIM (@value)
    SELECT @newValue = @newValue + REPLICATE ('~', DATALENGTH (@value) - DATALENGTH (@newValue))

    -- check if tupel exists. if exists then update the value, else insert tupel
    IF EXISTS (
        SELECT id
        FROM   ibs_Token_01
        WHERE  languageId   = @languageId
           AND name         = @name
           AND classname    = @className
           )
    BEGIN                               -- token exists --> UPDATE
    
        UPDATE ibs_Token_01
        SET    value = @newValue
        WHERE  languageId   = @languageId
           AND name         = @name
           AND classname    = @className
           
    END -- token exists
    ELSE
    BEGIN                               -- token does not exist --> INSERT
    
        INSERT INTO ibs_Token_01
            (languageId, name, value, className)
        VALUES
            (@languageId, @name, @newValue, @className)
    
    END -- token does not exist

GO

-- p_Token_01$new


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
-- delete existing procedure:
EXEC p_dropProc N'p_Token_01$get'
GO

-- create the new procedure:
CREATE PROCEDURE p_Token_01$get
(
    -- input parameters:
    @ai_languageId          ID,
    @ai_name                DESCRIPTION,
    -- output parameters:
    @ao_value               DESCRIPTION OUTPUT,
    @ao_className           FILENAME    OUTPUT
)
AS
    -- constants:
    -- local variables:

-- body:
    -- initializations:
    SELECT
        @ao_value = null,
        @ao_className = null

    -- try to get the tuple out of the table:
    SELECT  @ao_value = value, @ao_className = classname
    FROM    ibs_Token_01
    WHERE   languageId = @ai_languageId
      AND   name = @ai_name
GO
-- p_Token_01$get

