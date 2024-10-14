--------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_Exception_01 table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020818
-------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Creates a new tupel in the table. <BR>
-- This procedure inserts a tupel into ibs_Exception_01 or updates it, if it
-- exists
--
-- @input parameters:
-- @param   @languageId ID of the language (0 = default).
-- @param   @name       name of the Exception
-- @param   @value      text of the Exception
-- @param   @className  which javaclass refers to that Exception
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Exception_01$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Exception_01$new
(
    -- input parameters:
    IN  ai_languageId       INT,
    IN  ai_name             VARCHAR (255),
    IN  ai_value            VARCHAR (255),
    IN  ai_className        VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    IF EXISTS
    (
        SELECT  id 
        FROM    IBSDEV1.ibs_Exception_01
        WHERE   languageId = ai_languageId
            AND name = ai_name
            AND classname = ai_className
    )
    THEN                                -- if Exception exists
        -- Exception exists --> UPDATE
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_Exception_01
        SET     value = ai_Value
        WHERE   languageId = ai_languageId
            AND name = ai_name
            AND classname = ai_className;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in p_Object$performCreate';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    ELSE                                -- else Exception exists
        -- Exception does not exist --> INSERT
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_Exception_01
                (languageId, name, value, className)
        VALUES  (ai_languageId, ai_name, ai_Value, ai_className);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in p_Object$performCreate';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- else Exception exists

    -- finish transaction:
    COMMIT;                             -- make changes permanent
    -- finish the procedure:
    RETURN;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Exception_01$new',
        l_sqlcode, l_ePos,
        'ai_languageId', ai_languageId, 'ai_name', ai_name,
        '', 0, 'ai_value', ai_value,
        '', 0, 'ai_className', ai_className,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
END;
-- p_Exception_01$new
