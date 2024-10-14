-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_TypeName_01 table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Creates a new tupel in the table. <BR>
-- This procedure inserts a tupel into ibs_TypeName_01 or updates it, if it exists
--
-- @input parameters:
-- @param   @ai_languageId ID of the language (0 = default).
-- @param   @ai_name       name of the TypeName
-- @param   @ai_value      text of the TypeName
-- @param   @ai_className  which javaclass refers to that TypeName
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
-------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TypeName_01$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TypeName_01$new
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
        FROM    IBSDEV1.ibs_TypeName_01
        WHERE   languageId = ai_languageId
            AND name = ai_name
            AND classname = ai_className
    )
    THEN                                -- if TypeName extists --> UPDATE
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_TypeName_01
        SET     value = ai_Value
        WHERE   languageId = ai_languageId
            AND name = ai_name
            AND classname = ai_className;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in TypeName extists --> UPDATE';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    ELSE                                -- else TypeName not exist --> INSERT
        SET l_sqlcode = 0;

        INSERT  INTO IBSDEV1.ibs_TypeName_01
                (languageId, name, value, className)
        VALUES  (ai_languageId, ai_name, ai_Value, ai_className);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in else TypeName not exist --> INSERT';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- else TypeName not exist --> INSERT

    -- finish transaction:
    COMMIT;                             -- make changes permanent
    -- finish the procedure:
    RETURN;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TypeName_01$new',
        l_sqlcode, l_ePos,
        'ai_languageId', ai_languageId, 'ai_name', ai_name,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
END;
-- p_TypeName_01$new


-------------------------------------------------------------------------------
-- Get a tuple out of the table. <BR>
-- This procedure gets a tuple out of ibs_TypeName_01 by using the 
-- languageId and the name together as unique key.
-- If there is no tuple found the parameter ao_value is set to null.
--
-- @input parameters:
-- @param   ai_languageId       ID of the language (0 = default).
-- @param   ai_name             Unique name of the typeName.
--
-- @output parameters:
-- @param   ao_value            text for the typeName.
-- @param   ao_className        Java-constantclass in wich typeName is defined as Constant
--/
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TypeName_01$get');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TypeName_01$get
(
    -- input parameters:
    IN  ai_languageId       INT,
    IN  ai_name             VARCHAR (255),
    -- output parameters:
    OUT ao_value            VARCHAR (255),
    OUT ao_className        VARCHAR (255)
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

    -- initialize local variables and return values:
    SET ao_value            = NULL;
    SET ao_className        = NULL;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;
  
    -- try to get the tuple out of the table:
    SET l_sqlcode = 0;
    SELECT  value, classname
    INTO    ao_value, ao_className
    FROM    IBSDEV1.ibs_TypeName_01
    WHERE   languageId = ai_languageId
        AND name = ai_name;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in get the tuple out of the table';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
    -- finish the procedure:
    RETURN;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TypeName_01$get',
        l_sqlcode, l_ePos,
        'ai_languageId', ai_languageId, 'ai_name', ai_name,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');

END;
-- p_TypeName_01$get
