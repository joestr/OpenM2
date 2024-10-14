-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_ObjectRead table. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------



-------------------------------------------------------------------------------
-- Sets a business object as already read by the user. <BR>
--
-- @input parameters:
-- @param   @oid                ID of the object to be changed.
-- @param   @userId             ID of the user who has read the object.
--
-- @output parameters:
-- @param   @state              The object's state.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_setRead2');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_setRead2
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
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
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- set object as already read:
    UPDATE  IBSDEV1.ibs_ObjectRead
    SET     hasRead = 1,
            lastRead = CURRENT TIMESTAMP
    WHERE   oid = ai_oid
        AND userId = ai_userId;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    IF (l_rowcount <= 0)                -- if there is no object
    THEN 
        -- create new entry:
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_ObjectRead (oid, userId, hasRead, lastRead)
        VALUES  (ai_oid, ai_userId, 1, CURRENT TIMESTAMP);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in create new entry';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
    END IF; -- if there is no object

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
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_setRead2',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '',
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
-- p_setRead2

-------------------------------------------------------------------------------
-- Sets a business object as already read by the user. <BR>
--
-- @input parameters:
-- @param   @oid                ID of the object to be changed.
-- @param   @userId             ID of the user who has read the object.
--
-- @output parameters:
-- @param   @state              The object's state.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_setRead');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_setRead
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
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
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    CALL IBSDEV1.p_setRead2(ai_oid, ai_userId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- update existing entries for references to the object:
    SET l_sqlcode = 0;
    UPDATE  IBSDEV1.ibs_ObjectRead
    SET     hasRead = 1,
            lastRead = CURRENT TIMESTAMP
    WHERE   oid IN
            (
                SELECT  oid 
                FROM    IBSDEV1.ibs_Object
                WHERE   linkedObjectId = ai_oid
            )
        AND userId = ai_userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100) -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in update existing entries';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- create new entries for references to the object:
    SET l_sqlcode = 0;
    INSERT  INTO IBSDEV1.ibs_ObjectRead (oid, userId, hasRead, lastRead)
    SELECT  oid, ai_userId, 1, CURRENT TIMESTAMP
    FROM    IBSDEV1.ibs_Object
    WHERE   linkedObjectId = ai_oid
        AND oid NOT IN  
            (
                SELECT  oid
                FROM IBSDEV1.ibs_ObjectRead
                WHERE   userId = ai_userId
            );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100) -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in create new entries';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_setRead',
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
-- p_setRead
