-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_ObjectDesc_01 table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Creates a new tuple in the table. <BR>
-- This procedure inserts a tuple into ibs_ObjectDesc_01 or updates it, if it 
-- exists already.
--
-- @input parameters:
-- @param   ai_languageId       ID of the language (0 = default).
-- @param   ai_name             Unique name of the object.
-- @param   ai_objName          Name of the business object.
-- @param   ai_objDesc          Description of the business object.
-- @param   ai_className        Java class which shall contain this object data.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ObjectDesc_01$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ObjectDesc_01$new
(
    -- input parameters:
    IN  ai_languageId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_objName          VARCHAR (63),
    IN  ai_objDesc          VARCHAR (255),
    IN  ai_className        VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_rowcount      INT;
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

    SET l_sqlcode = 0;
    UPDATE  IBSDEV1.ibs_ObjectDesc_01
    SET     objName = ai_objName,
            objDesc = ai_objDesc,
            className = ai_className
    WHERE   languageId = ai_languageId
        AND name = ai_name;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100) -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in UPDATE statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    IF (l_rowcount <= 0)
    THEN                                -- insert a new tuple:
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_ObjectDesc_01
                (languageId, name, objName, objDesc, className)
        VALUES  (ai_languageId, ai_name, ai_objName, ai_objDesc, ai_className);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in insert a new tuple';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
    END IF; -- insert a new tuple:

    -- finish transaction:
    COMMIT;                             -- make changes permanent
    -- finish the procedure:
    RETURN;
  
exception1:

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_ObjectDesc_01$new',
        l_sqlcode, l_ePos,
        'ai_languageId', ai_languageId, 'ai_name', ai_name,
        '', 0, 'ai_objName', ai_objName,
        '', 0, 'ai_objDesc', ai_objDesc,
        '', 0, 'ai_className', ai_className,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
END;
-- p_ObjectDesc_01$new


-------------------------------------------------------------------------------
-- Get a tuple out of the table. <BR>
-- This procedure gets a tuple out of ibs_ObjectDesc_01 by using the 
-- languageId and the name together as unique key.
-- If there is no tuple found the parameter ao_objName is set to ' '.
--
-- @input parameters:
-- @param   ai_languageId       ID of the language (0 = default).
-- @param   ai_name             Unique name of the object.
--
-- @output parameters:
-- @param   ao_objName          Name of the business object.
-- @param   ao_objDesc          Description of the business object.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ObjectDesc_01$get');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ObjectDesc_01$get
(
    -- input parameters:
    IN  ai_languageId       INT,
    IN  ai_name             VARCHAR (63),
    -- output parameters:
    OUT ao_objName          VARCHAR (63),
    OUT ao_objDesc          VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_rowcount      INT;
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
    SET ao_objName          = ' ';
    SET ao_objDesc          = NULL;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- try to get the tuple out of the table:
    SET l_sqlcode = 0;
    SELECT  objName, objDesc
    INTO    ao_objName, ao_objDesc
    FROM    IBSDEV1.ibs_ObjectDesc_01
    WHERE   languageId = ai_languageId
        AND name = ai_name;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in try to get the tuple out of the table';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
    -- finish the procedure:
    RETURN;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_ObjectDesc_01$get',
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
-- p_ObjectDesc_01$get
