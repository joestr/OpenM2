-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_Message_01 table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020819
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Creates a new tupel in the table. <BR>
-- This procedure inserts a tuple into ibs_Message_01 or updates it, if it exists
--
-- @input parameters:
-- @param   @languageId ID of the language (0 = default).
-- @param   @name       name of the Message
-- @param   @value      text of the Message
-- @param   @className  which javaclass refers to that Message
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Message_01$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Message_01$new
(
    -- input parameters:
    IN  ai_languageId       INT,
    IN  ai_name             VARCHAR (255),
    IN  ai_value            VARCHAR (2048),
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

    -- check if tupel exists. if exists then update the value, else insert tupel
    IF EXISTS
    (
        SELECT  id 
        FROM    IBSDEV1.ibs_Message_01
        WHERE   languageId = ai_languageId
            AND name = ai_name
            AND classname = ai_classname
    )
    THEN                                -- if Message exists --> UPDATE
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_Message_01
        SET     value = ai_Value
        WHERE   languageId = ai_languageId
            AND name = ai_name
            AND classname = ai_classname;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in Message exists --> UPDATE';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    ELSE                                -- Message does not exist --> INSERT
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_Message_01
                (languageId, name, value, className)
        VALUES  (ai_languageId, ai_name, ai_value, ai_className);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in -- Message does not exist --> INSERT';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- else Message does not exist --> INSERT

    -- finish transaction:
    COMMIT;                             -- make changes permanent
    -- finish the procedure:
    RETURN;
  
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Message_01$new',
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
    
    -- return error value:
END;
-- p_Message_01$new