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
-- delete existing procedure:
EXEC p_dropProc N'p_ObjectDesc_01$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_ObjectDesc_01$new
(
    -- input parameters:
    @ai_languageId          ID,
    @ai_name                NAME,
    @ai_objName             NAME,
    @ai_objDesc             DESCRIPTION,
    @ai_className           FILENAME
    -- output parameters:
)
AS
    -- constants:
    -- local variables:

-- body:
    -- try to update the already existing tuple:
    UPDATE  ibs_ObjectDesc_01
    SET     objName = @ai_objName,
            objDesc = @ai_objDesc,
            className = @ai_className
    WHERE   languageId = @ai_languageId
        AND name = @ai_name

    IF (@@ROWCOUNT <= 0)                -- the tuple does not exist?
    BEGIN
        -- insert a new tuple:
        INSERT INTO ibs_ObjectDesc_01
            (languageId, name, objName, objDesc, className)
        VALUES
            (@ai_languageId, @ai_name, @ai_objName, @ai_objDesc, @ai_className)
    END -- if the tuple does not exist
GO
-- p_ObjectDesc_01$new


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
-- delete existing procedure:
EXEC p_dropProc N'p_ObjectDesc_01$get'
GO

-- create the new procedure:
CREATE PROCEDURE p_ObjectDesc_01$get
(
    -- input parameters:
    @ai_languageId          ID,
    @ai_name                NAME,
    -- output parameters:
    @ao_objName             NAME OUTPUT,
    @ao_objDesc             DESCRIPTION OUTPUT
)
AS
    -- constants:
    -- local variables:

-- body:
    -- initializations:
    SELECT
        @ao_objName = N' ',
        @ao_objDesc = null

    -- try to get the tuple out of the table:
    SELECT  @ao_objName = objName, @ao_objDesc = objDesc
    FROM    ibs_ObjectDesc_01
    WHERE   languageId = @ai_languageId
        AND name = @ai_name
GO
-- p_ObjectDesc_01$get
