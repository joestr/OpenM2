/******************************************************************************
 * All stored procedures regarding the objectId table. <BR>
 *
 * @version     $Id: ObjectIdProc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR) 20050302
 ******************************************************************************
 */


/******************************************************************************
 * Get the next object id. <BR>
 *
 * @input parameters:
 *
 * @output parameters:
 * @param   ao_id               The id to be used for the next object.
 *                              -1 .... no id was found and none could be
 *                                      generated.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ObjectId$getNext'
GO

-- create the new procedure:
CREATE PROCEDURE p_ObjectId$getNext
--(
    -- input parameters:
    -- output parameters:
--)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_id                   ID,             -- return value
    @l_error                INT             -- the actual error code

    -- assign constants:

    -- initialize local variables and return values:
SELECT
    @l_id = -1

-- body:
    -- create the new rKey:
    -- insert the corresponding values into ibs_RightsKey table:
    INSERT INTO ibs_ObjectId
    DEFAULT VALUES

    -- check if there occurred an error:
    SELECT @l_error = @@error   -- store the error code
    IF (@l_error <> 0)          -- an error occurred?
        GOTO exception          -- call exception handler

    -- get the id of the newly generated rKey:
    SELECT  @l_id = SCOPE_IDENTITY ()

    -- finish the procedure:
    RETURN @l_id

exception:                              -- an error occurred
    -- set return value:
    SELECT  @l_id = -1
    RETURN @l_id
GO
-- p_ObjectId$getNext
