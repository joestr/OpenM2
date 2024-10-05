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
 * This procedure inserts a tupel into ibs_TypeName_01 or updates it, if it exists
 *
 * @input parameters:
 * @param   @ai_languageId ID of the language (0 = default).
 * @param   @ai_name       name of the TypeName
 * @param   @ai_value      text of the TypeName
 * @param   @ai_className  which javaclass refers to that TypeName
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_TypeName_01$new'
GO


-- create the new procedure:
CREATE PROCEDURE p_TypeName_01$new
(
    -- input parameters:
    @ai_languageId  ID,
    @ai_name        DESCRIPTION,
    @ai_value       DESCRIPTION,
    @ai_className   FILENAME
    -- output parameters
)
AS
    -- declare local variables
    DECLARE @l_newValue DESCRIPTION       -- new value with '~' instead of trailing blanks
    -- variables needed for the update on ibs_Object and ibs_Type
    DECLARE @l_pos INT
    DECLARE @l_subName NAME
    DECLARE @l_tVersionID  TVERSIONID
    
    -- trailing blanks will be changed into '~', 
    -- because MS-SQL-Server cuts all trailing blanks
    SELECT @l_newValue = RTRIM (@ai_value)
    SELECT @l_newValue = @l_newValue + REPLICATE ('~', DATALENGTH (@ai_value) - DATALENGTH (@l_newValue))

    -- check if tupel exists. if exists then update the value, else insert tupel
    IF EXISTS (
        SELECT id
        FROM   ibs_TypeName_01
        WHERE  languageId   = @ai_languageId
           AND name         = @ai_name
           AND classname    = @ai_className
           )
    BEGIN                               -- TypeName exists --> UPDATE
    
        UPDATE ibs_TypeName_01
        SET    value = @l_newValue
        WHERE  languageId   = @ai_languageId
           AND name         = @ai_name
           AND classname    = @ai_className
           
    END -- TypeName exists
    ELSE
    BEGIN                               -- TypeName does not exist --> INSERT
    
        INSERT INTO ibs_TypeName_01
            (languageId, name, value, className)
        VALUES
            (@ai_languageId, @ai_name, @l_newValue, @ai_className)
    
    END -- TypeName does not exist
GO
-- p_TypeName_01$new



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
-- delete existing procedure:
EXEC p_dropProc N'p_TypeName_01$get'
GO

-- create the new procedure:
CREATE PROCEDURE p_TypeName_01$get
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
    FROM    ibs_TypeName_01
    WHERE   languageId = @ai_languageId
      AND   name = @ai_name
GO
-- p_TypeName_01$get

