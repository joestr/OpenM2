/******************************************************************************
 * Initializes the application. <BR>
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_Application$initialize
(
    ai_dummy                 INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of this procedure

BEGIN

    -- do nothing
    -- return the state value:
    RETURN l_retValue;

END p_Application$initialize;
/

show errors;
EXIT;