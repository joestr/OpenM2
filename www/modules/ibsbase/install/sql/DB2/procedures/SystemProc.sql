-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_System table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Creates a new tuple in the table. <BR>
-- This procedure inserts a tuple into ibs_ObjectDesc_01 or updates it, if it 
-- exists already.
-- The state of a newly inserted tuple is automatically set to 2 (active).
--
-- @input parameters:
-- @param   ai_name             Unique name of the value.
-- @param   ai_type             Type of the value.
-- @param   ai_value            The value itself.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_System$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_System$new
(
    -- input parameters:
    IN  ai_name             VARCHAR (63),
    IN  ai_type             VARCHAR (63),
    IN  ai_value            VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- try to update the already existing tuple:
    UPDATE  IBSDEV1.ibs_System
    SET     type = ai_type,
            value = ai_value
    WHERE   name = ai_name;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    IF (l_rowcount <= 0)                -- Message does not exist --> INSERT
    THEN 
        -- insert a new tuple:
        INSERT INTO IBSDEV1.ibs_System
            (state, name, type, value)
        VALUES
            (2, ai_name, ai_type, ai_value);
    END IF; -- Message does not exist
END;
-- p_System$new


-------------------------------------------------------------------------------
-- Get a value out of the table. <BR>
-- This procedure gets a value out of ibs_System by using the name as
-- unique key.
-- If there is no tuple found the parameter ao_value is set to null.
--
-- @input parameters:
-- @param   ai_name             Unique name of the object.
--
-- @output parameters:
-- @param   ao_value            The value out of the table.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_System$get');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_System$get
(
    IN  ai_name             VARCHAR (63),
    OUT ao_value            VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initializations:
    SET ao_value            = NULL;

-- body:
    -- try to get the tuple out of the table:
    SELECT  value
    INTO    ao_value
    FROM    IBSDEV1.ibs_System
    WHERE   name = ai_name;
END;
-- p_System$get


-------------------------------------------------------------------------------
-- Get an integer value out of the table. <BR>
-- This procedure gets a value out of ibs_System by using the name as
-- unique key.
-- The value is converted to INTEGER.
-- If there is no tuple found or the value is no valid INTEGER the parameter 
-- ao_value is set to null.
--
-- @input parameters:
-- @param   ai_name             Unique name of the object.
--
-- @output parameters:
-- @param   ao_value            The value out of the table.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_System$getInt');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_System$getInt
(
    -- input parameters:
    IN  ai_name             VARCHAR (63),
    -- output parameters:
    OUT ao_value            INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initializations:
    SET ao_value            = 0;

-- body:
    -- try to get the tuple out of the table:
    SELECT  CAST (value AS INT) AS value
    INTO    ao_value
    FROM    IBSDEV1.ibs_System
    WHERE   name = ai_name;
END;
-- p_System$getInt
