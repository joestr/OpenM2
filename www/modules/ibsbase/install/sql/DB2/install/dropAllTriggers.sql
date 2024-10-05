-------------------------------------------------------------------------------
-- Drop all triggers within the framework. <BR>
--
-- @version     $Id: dropAllTriggers.sql,v 1.5 2003/10/21 22:37:48 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure
DROP PROCEDURE IBSDEV1.pi_dropAllTriggers;
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.pi_dropAllTriggers ()
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
        SELECT  s.trigger_name 
        FROM    IBSDEV1.SYSTRIGGERS s
        WHERE   s.EVENT_OBJECT_SCHEMA = 'IBSDEV1'
            AND (
                    UPPER (s.TRIGGER_NAME) LIKE UPPER ('TRIG%')
                OR  UPPER (s.TRIGGER_NAME) LIKE UPPER ('T_%')
                )
        ORDER BY s.trigger_name;

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
        SET l_Exec = 'DROP TRIGGER IBSDEV1.' || l_name;
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
-- pi_dropAllTriggers

-- execute procedure:
CALL IBSDEV1.pi_dropAllTriggers;
-- delete procedure:
DROP PROCEDURE IBSDEV1.pi_dropAllTriggers;
