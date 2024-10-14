-------------------------------------------------------------------------------
-- All stored procedures regarding the operation table. <BR>
-- 
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------


-------------------------------------------------------------------------------
-- Creates a new operation (incl. rights check). <BR>
-- If operation (op) already exists values will be updated.
--
-- @input parameters:
-- @param   @op                 Operation to be created.
-- @param   @name               Name of the operation.
-- @param   @description        Description of the operation.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Operation$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Operation$new(
    -- input parameters:
    IN    ai_op             INT,
    IN    ai_name           VARCHAR (63),
    IN    ai_description    VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    SET c_ALL_RIGHT         = 1;
    SET l_retValue          = c_ALL_RIGHT;
    IF EXISTS   (
                    SELECT * 
                    FROM IBSDEV1.ibs_Operation 
                    WHERE id = ai_op
                )
    THEN 
        UPDATE IBSDEV1.ibs_Operation
        SET name = ai_name,
            description = ai_description
        WHERE id = ai_op;
        COMMIT;
    ELSE 
        INSERT INTO IBSDEV1.ibs_Operation (id, name, description)
        VALUES (ai_op, ai_name, ai_description);
        COMMIT;
    END IF;

    -- return the state value
    RETURN l_retValue;
END;
-- p_Operation$new
