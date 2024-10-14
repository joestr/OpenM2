--------------------------------------------------------------------------------
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:47 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Initializes the application. <BR>
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Application$initialize');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Application$initialize
(
    IN  ai_dummy            INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100) -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, '',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Application$initialize