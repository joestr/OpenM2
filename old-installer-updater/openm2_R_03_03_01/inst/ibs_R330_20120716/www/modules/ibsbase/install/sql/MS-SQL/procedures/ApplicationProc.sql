/******************************************************************************
 * Initializes the application. <BR>
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Application$initialize'
GO

-- create the new procedure:
CREATE PROCEDURE p_Application$initialize
(
    @dummy             INT
)
AS
DECLARE
    -- definitions:
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @l_retValue             INT

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_ALL_RIGHT

    -- body:

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Application$initialize
