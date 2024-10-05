--------------------------------------------------------------------------------
-- All stored procedures regarding to the class Counter. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020829
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Get Next count of specific counter if counter does not exist,
-- create counter. <BR>
--
-- @param   ai_counterName      Name of the counter to be incremented
--
-- @param   ao_nextCount        incremented count of specified counter
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Counter$getNext');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Counter$getNext
(
    IN  ai_counterName      VARCHAR (63),
    OUT ao_nextCount        INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.

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

    -- check if counter allready exist, if not create new counter
    IF NOT EXISTS
        (
            SELECT counterName 
            FROM IBSDEV1.ibs_Counter
            WHERE counterName = ai_counterName
        ) -- if counter does not exist allready - create it
    THEN 
        -- set outputparameter to starting count
        SET ao_nextCount = 1;
        -- create new counter
        SEt l_sqlCode = 0;

        INSERT  INTO IBSDEV1.ibs_Counter (counterName, currentCount)
        VALUES (ai_counterName, ao_nextCount);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in create new counter';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    ELSE -- if counter does not exist allready - create it
        -- get next count of required counter
        SEt l_sqlCode = 0;

        SELECT currentCount + 1
        INTO ao_nextCount
        FROM IBSDEV1.ibs_Counter
        WHERE counterName = ai_counterName;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in get next count of required counter';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- increment counter
        SEt l_sqlCode = 0;

        UPDATE IBSDEV1.ibs_Counter
        SET currentCount = ao_nextCount
        WHERE counterName = ai_counterName;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in increment counter';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- else counter does not exist allready - create it

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN c_ALL_RIGHT;
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Counter$getNext',
        l_sqlcode, l_ePos,
        '', 0, 'ai_counterName', ai_counterName,
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
-- p_Counter$getNext

--------------------------------------------------------------------------------
-- Reset a counter to 0 (the next call of getNext returns 1) <BR>
-- If required counter does not exist, nothing happens. <BR>
--
-- @param   ai_counterName      Name of the counter to be reseted
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Counter$reset');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Counter$reset
(
    IN  ai_counterName      VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.

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

    -- reset counter
    SEt l_sqlCode = 0;

    UPDATE  IBSDEV1.ibs_Counter
    SET     currentCount = 0
    WHERE   counterName = ai_counterName;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in increment counter';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN c_ALL_RIGHT;
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Counter$reset',
        l_sqlcode, l_ePos,
        '', 0, 'ai_counterName', ai_counterName,
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
-- p_Counter$reset