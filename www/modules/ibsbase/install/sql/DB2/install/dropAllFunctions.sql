-------------------------------------------------------------------------------
-- Drop all functions within the framework. <BR>
--
-- @version     $Id: dropAllFunctions.sql,v 1.5 2003/10/21 22:37:48 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure:
DROP PROCEDURE IBSDEV1.pi_dropAllFunctions;
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pi_dropAllFunctions ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    DECLARE l_count         INT DEFAULT 0;
    DECLARE l_name          VARCHAR (32);
    DECLARE l_exec          VARCHAR (255);

    -- define cursor:
    DECLARE object_Cursor INSENSITIVE CURSOR FOR 
        SELECT  s.routine_name 
        FROM    QSYS2.SYSFUNCS s
        WHERE   s.routine_schema = 'IBSDEV1'
            AND (   UPPER (s.routine_name) LIKE UPPER ('P_%')
                OR  UPPER (s.routine_name) IN
                        ('B_AND', 'B_OR', 'B_XOR', 'CREATEOID')
                )
        ORDER BY s.routine_name ASC;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- open the cursor:
    OPEN object_Cursor;

    -- get the first table:
    SET l_sqlcode = 0;
    FETCH FROM object_Cursor INTO l_name;
    SET l_sqlstatus = l_sqlcode;
  
    -- loop through all found objects:
    WHILE (l_sqlstatus = 0)             -- another tuple found
    DO
		-- define and perform operation:
        SET l_exec = 'DROP FUNCTION IBSDEV1.' || l_name;
        EXECUTE IMMEDIATE l_exec;

        -- increment counter:
        SET l_count = l_count + 1;

        -- get the next tuple:
        SET l_sqlcode = 0;
        FETCH FROM object_Cursor INTO l_name;
        SET l_sqlstatus = l_sqlcode;
    END WHILE; -- another tuple found

    -- close the cursor:
    CLOSE object_Cursor;

    -- make changes permanent:
    COMMIT;

    -- return the number of tuples:
    RETURN l_count;
END;
-- pi_dropAllFunctions

-- execute procedure:
CALL IBSDEV1.pi_dropAllFunctions;
-- delete procedure:
DROP PROCEDURE IBSDEV1.pi_dropAllFunctions;
