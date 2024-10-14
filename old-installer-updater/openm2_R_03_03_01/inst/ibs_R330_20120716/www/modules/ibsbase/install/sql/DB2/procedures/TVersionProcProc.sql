--------------------------------------------------------------------------------
-- All stored procedures regarding the TVersionProc table. <BR>
-- 
-- @version     $Revision: 1.7 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Inherit the tuples from one tVersion to another tVersion. <BR>
-- If there are any tVersions currently inheriting their tuples from the second
-- tVersion they will also inherit their tuples from the first tVersion. <BR>
-- This function must be called from within a transaction handled code block
-- because it uses savepoints.
--
-- @input parameters:
-- @param   ai_majorTVersionId  Id of the major tVersion from which the tuples
--                              shall be inherited.
-- @param   ai_minorTVersionId  Id of minor tVersion to which the tuples shall
--                              be inherited.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersionProc$inherit');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersionProc$inherit(
    -- input parameters:
    IN    ai_majorTVersionId INT,
    IN    ai_minorTVersionId INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN 
    DECLARE SQLCODE         INT;
    -- input parameters:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE l_ePos          VARCHAR (255);
    DECLARE l_count         INT;
    DECLARE l_retValue      INT;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_code          VARCHAR (63);
    DECLARE l_inheritedFrom INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    DECLARE l_rowcount      INT;
    -- define cursor:
    -- get all codes of the major tVersion.
    DECLARE updateCursor CURSOR WITH HOLD FOR 
        SELECT code, inheritedFrom 
        FROM IBSDEV1.ibs_TVersionProc
        WHERE tVersionId = ai_majorTVersionId;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- id of tVersion from which the
    -- actual tVersion/code pair is
    -- inherited
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    SAVEPOINT s_TVerPP_inherit ON ROLLBACK RETAIN CURSORS;
    -- get the data of the tVersion to which to inherit the tuples:
    SET l_sqlcode = 0;

    SELECT  posNoPath
    INTO    l_posNoPath
    FROM    IBSDEV1.ibs_TVersion
    WHERE   id = ai_minorTVersionId;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 THEN 
        SET l_ePos = 'get minor tVersion data';
        GOTO exception1;
    END IF;
    -- open the cursor:

    OPEN updateCursor;

    -- get the first object:
    SET l_sqlcode = 0;
    FETCH FROM updateCursor INTO l_code, l_inheritedFrom;
    SET l_sqlstatus = l_sqlcode;
    -- loop through all objects:
    WHILE l_sqlcode <> 100 DO
        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
            -- delete the values for the minor tVersion and all
            -- tVersions below which inherit their values from the same
            -- TVersion as that tVersion:
            SET l_sqlcode = 0;
            DELETE FROM IBSDEV1.ibs_TVersionProc
            WHERE tVersionId IN (   
                                    SELECT id 
                                    FROM IBSDEV1.ibs_TVersion
                                    WHERE posNoPath LIKE l_posNoPath || '%'
                                )
                AND code = l_code
                AND inheritedFrom = l_inheritedFrom;
            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
                SET l_ePos = 'delete for act tVersion and tVersions below';
                GOTO cursorException;
            END IF;
        END IF;
        -- get next object:
        SET l_sqlcode = 0;
        FETCH FROM updateCursor INTO l_code, l_inheritedFrom;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;
    -- close the not longer needed cursor:
--    CLOSE updateCursor;
    -- add the records to the minor tVersion and all tVersions
    -- below which before inherited from the same tVersion as
    -- the minor tVersion:

    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_TVersionProc
        (tVersionId, code, name, inheritedFrom)
    SELECT  tv.id, p.code, p.name, p.inheritedFrom
    FROM    IBSDEV1.ibs_TVersionProc p, IBSDEV1.ibs_TVersion tv
    WHERE   tv.posNoPath LIKE l_posNoPath || '%'
        AND tv.id NOT IN (
                            SELECT  tVersionId
                            FROM IBSDEV1.ibs_TVersionProc
                            WHERE   code = p.code
                        )
        AND p.tVersionId = ai_majorTVersionId;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'delete for act tVersion and tVersions below';
        GOTO exception1;
    END IF;

    -- release the savepoint:
    RELEASE s_TVerPP_inherit;
  
    -- return the state value:
    RETURN l_retValue;

cursorException:
    -- close the not longer needed cursor:
--    CLOSE updateCursor;
    RETURN c_NOT_OK;
  
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_TVerPP_inherit;
    -- release the savepoint:
    RELEASE s_TVerPP_inherit;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersionProc$inherit', l_sqlcode, l_ePos,
        'ai_majorTVersionId', ai_majorTVersionId, '', '', 'ai_minorTVersionId',
        ai_minorTVersionId, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
    -- return the error code:
    RETURN c_NOT_OK;
END;
-- p_TVersionProc$inherit


--------------------------------------------------------------------------------
-- This procedure creates tuples into the TVersionProc table. <BR>
-- If there exists already an entry for the specified procedure within the
-- tVersion it is overwritten with the new value. <BR>
-- It contains a TRANSACTION block, so it is not allowed to CALL IBSDEV1.this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_tVersionId       Id of the tVersion for which the procedures
--                              shall be defined.
-- @param   ai_code             Unique code of the procedure.
-- @param   ai_name             Name of the procedure.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersionProc$add');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersionProc$add(
    -- input parameters:
    IN    ai_tVersionId     INT,
    IN    ai_code           VARCHAR (63),
    IN    ai_name           VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_count         INT;            -- counter
    DECLARE l_posNoPath     VARCHAR (254);   -- the pos no path of the tVersion
    DECLARE l_inheritedFrom INT;            -- tVersion from which the tVersion
                                            -- inherited the entry before
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- get the data of the actual tVersion:
    SET l_sqlcode = 0;

    SELECT  posNoPath
    INTO    l_posNoPath
    FROM    IBSDEV1.ibs_TVersion
    WHERE   id = ai_tVersionId;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'get tVersion data';
        GOTO NonTransactionException;
    END IF;
    -- get the procedure data of the actual tVersion:
    SET l_sqlcode = 0;

    SELECT inheritedFrom
    INTO l_inheritedFrom
    FROM IBSDEV1.ibs_TVersionProc
    WHERE tVersionId = ai_tVersionId
        AND code = ai_code;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'get tVersion procedure data';
        GOTO NonTransactionException;
    END IF;
  
    -- at this point we know that the operation may be done
    COMMIT; -- finish previous and begin new TRANSACTION
    
    -- update the value for the actual tVersion and all tVersions below
    -- which inherit their values from the same TVersion as this tVersion:
    -- all these tVersions inherit now from the actual tVersion
    SET l_sqlcode = 0;
    UPDATE  IBSDEV1.ibs_TVersionProc
    SET     name = ai_name,
            inheritedFrom = ai_tVersionId
    WHERE   tVersionId IN
            (
                SELECT  id 
                FROM    IBSDEV1.ibs_TVersion
                WHERE   posNoPath LIKE l_posNoPath || '%'
            )
        AND code = ai_code
        AND inheritedFrom = l_inheritedFrom;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        SET l_ePos = 'update for act tVersion and tVersions below';
        GOTO exception1;
    END IF;
    -- add the record to all tVersions below which currently do not have
    -- this record:

    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_TVersionProc
            (tVersionId, code, name, inheritedFrom)
    SELECT  id, ai_code, ai_name, ai_tVersionId
    FROM    IBSDEV1.ibs_TVersion
    WHERE   id NOT IN
            (
                SELECT  DISTINCT tVersionId
                FROM    IBSDEV1.ibs_TVersionProc
                WHERE   code = ai_code
            )
        AND posNoPath LIKE l_posNoPath || '%';

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        SET l_ePos = 'insert for act tVersion and tVersions below';
        GOTO exception1;
    END IF;

    -- finish the transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;
 
exception1:                             -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;                           -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersionProc$add', l_sqlcode, l_ePos,
        'ai_tVersionId', ai_tVersionId, 'ai_code', ai_code, '', 0,
        'ai_name', ai_name, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- set new transaction starting point:
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_TVersionProc$add

--------------------------------------------------------------------------------
-- This procedure creates tuples into the TVersionProc table. <BR>
-- It calls p_TVersionProc$add for the actual version of the type which is
-- specified by its code.
-- The tuples are stored for the actual version of that type which is identified
-- by its code.
--
-- @input parameters:
-- @param   ai_typeCode         Code value of the type.
-- @param   ai_code             Unique code of the procedure.
-- @param   ai_name             Name of the procedure.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersionProc$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersionProc$new(
    -- input parameters:
    IN    ai_typeCode       VARCHAR (63),
    IN    ai_code           VARCHAR (63),
    IN    ai_name           VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE l_retValue      INT;
    DECLARE l_tVersionId    INT;
    DECLARE l_ePos          VARCHAR (255);
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
  
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_tVersionId        = 0;
-- body:
    -- get the actual tVersion id for the type code:
    SET l_sqlcode = 0;

    SELECT actVersion
    INTO l_tVersionId
    FROM IBSDEV1.ibs_Type
    WHERE code = ai_typeCode;

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get the actual tVersion id for the type code';
        GOTO exception1;
    END IF;
  
    -- add the new procedure entry to the table:
    CALL IBSDEV1.p_TVersionProc$add(l_tVersionId, ai_code, ai_name);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- return the state value:
    RETURN l_retValue;

exception1:
  
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersionProc$new', l_sqlcode, l_ePos,
        'l_tVersionId', l_tVersionId, 'ai_typeCode', ai_typeCode, '', 0,
        'ai_code', ai_code, '', 0, 'ai_name', ai_name, '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_TVersionProc$new

--------------------------------------------------------------------------------
-- Delete a tVersion specific entry for a procedure. <BR>
-- The code entry of the super tVersion is inherited to the actual tVersion and
-- all tVersions below which inherited that entry from the actual tVersion.
-- If there is no entry in the super tVersion the entries in the actual
-- tVersion and all tVersions below which inherit from the actual tVersion are
-- deleted. <BR>
-- It contains a TRANSACTION block, so it is not allowed to CALL IBSDEV1.this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_tVersionId       Id of the tVersion for which a procedure
--                              shall be deleted.
-- @param   ai_code             Unique code of the procedure to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
-- c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersionProc$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersionProc$delete(
    -- input parameters:
    IN    ai_tVersionId     INT,
    IN    ai_code           VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT;           -- tuple not found

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_count         INT;            -- counter
    DECLARE l_posNoPath     VARCHAR (254);   -- the pos no path of the tVersion
    DECLARE l_name          VARCHAR (63);    -- name of procedure in super
                                            -- tVersion
    DECLARE l_inheritedFrom INT;            -- tVersion from which the super
                                            -- tVersion inherits the entry
    DECLARE l_superTVersionId INT;          -- Id of super tVersion of the
                                            -- actual tVersion
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- get the data of the actual tVersion:
    SET l_sqlcode = 0;

    SELECT  posNoPath, superTVersionId
    INTO    l_posNoPath, l_superTVersionId
    FROM    IBSDEV1.ibs_TVersion
    WHERE   id = ai_TVersionId;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'get data of actual tVersion';
        GOTO NonTransactionException;
    END IF;
    -- get the procedure name from the super tVersion:
    SET l_sqlcode = 0;
    SELECT name, inheritedFrom
    INTO l_name, l_inheritedFrom
    FROM IBSDEV1.ibs_TVersionProc
    WHERE tVersionId = l_superTVersionId
        AND code = ai_code;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'get procedure name';
        GOTO NonTransactionException;
    END IF;
    -- at this point we know that the operation may be done
    -- check if there exists an entry in the super tVersion:
    IF l_sqlcode <> 100 THEN 
        -- inherit the entry from the super tVersion to the actual tVersion
        -- and all tVersions which inherited from the actual tVersion:
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_TVersionProc
        SET name = l_name,
            inheritedFrom = l_inheritedFrom
        WHERE tVersionId = l_inheritedFrom
            OR inheritedFrom = ai_tVersionId;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'inherit entry from super tVersion';
            GOTO exception1;
        END IF;
    ELSE 
        -- delete the entry from the actual tVersion and all tVersions
        -- which inherit from the actual tVersion:
        SET l_sqlcode = 0;
        DELETE FROM IBSDEV1.ibs_TVersionProc
        WHERE   (
                    tVersionId = ai_tVersionId
                    OR inheritedFrom = ai_tVersionId
                ) AND
            code = ai_code;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'delete entry';
            GOTO exception1;
        END IF;
    END IF;
    -- finish the transaction:
    COMMIT;
    -- return the state value:
    RETURN  l_retValue;
  
exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
NonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersionProc$delete', l_sqlcode, l_ePos,
        'ai_tVersionId', ai_tVersionId, 'ai_code', ai_code, '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_TVersionProc$delete

--------------------------------------------------------------------------------
-- Delete all occurrences of a code out of the TVersionProc table. <BR>
-- It contains a TRANSACTION block, so it is not allowed to CALL IBSDEV1.this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_code             The code to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersionProc$deleteCode');

CREATE PROCEDURE IBSDEV1.p_TVersionProc$deleteCode(
    -- input parameters:
    IN    ai_code           VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    COMMIT;
    -- delete the entries of the code from the TVersionProc table:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_TVersionProc
    WHERE code = ai_code;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'delete';
        GOTO exception1;
    END IF;
  
    -- finish the transaction:
    COMMIT;
  
    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
  
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersionProc$deleteCode', l_sqlcode, l_ePos,
        '', 0, 'ai_code', ai_code, '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_TVersionProc$deleteCode

--------------------------------------------------------------------------------
-- Delete all occurrences of a specific tVersion out of the TVersionProc table.
-- <BR>
-- If the tVersion is used to inherit entries to sub tVersions the sub tVersions
-- will inherit their entries from the super tVersions of the tVersion. <BR>
-- This function must be called from within a transaction handled code block
-- because it uses savepoints.
--
-- @input parameters:
-- @param   ai_tVersionId       Id of the tVersion to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersionProc$deleteTVersion');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersionProc$deleteTVersion(
    -- input parameters:
    IN    ai_tVersionId     INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_count         INT;            -- counter
    DECLARE l_superTVersionId INT;          -- Id of super tVersion of the
                                            -- actual tVersion
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
  
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_TVerPP_delTV ON ROLLBACK RETAIN CURSORS;
    -- get the data of the tVersion:
    SET l_sqlcode = 0;

    SELECT superTVersionId
    INTO l_superTVersionId
    FROM IBSDEV1.ibs_TVersion
    WHERE id = ai_tVersionId;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'get data of tVersion';
        GOTO exception1;
    END IF;
    -- check if the super tVersion id was found:
    IF l_sqlcode = 0 THEN 
        -- inherit all entries from the super tVersion:
        -- the consequence of this action is, that no sub tVersion will have
        -- inherited values from this tVersion
        CALL IBSDEV1.p_TVersionProc_inherit(l_superTVersionId, ai_tVersionId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
    -- check if there was an error:
    IF l_retValue = c_ALL_RIGHT THEN 
        -- delete the entries of the actual tVersion and all entries which were
        -- inherited from this tVersion from the tVersionProc table:
        SET l_sqlcode = 0;
        DELETE FROM IBSDEV1.ibs_TVersionProc
        WHERE tVersionId = ai_tVersionId
            OR inheritedFrom = ai_tVersionId;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'delete';
            GOTO exception1;
        END IF;
    END IF;

    -- release the savepoint:
    RELEASE s_TVerPP_delTV;

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_TVerPP_delTV;
    -- release the savepoint:
    RELEASE s_TVerPP_delTV;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersionProc$deleteTVersion',
        l_sqlcode, l_ePos, 'ai_tVersionId', ai_tVersionId, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_TVersionProc$deleteTVersion

--------------------------------------------------------------------------------
-- Delete all occurrences of tVersions belonging to a specific type out of the
-- TVersionProc table. <BR>
-- If any tVersion of the type is used to inherit entries to sub tVersions the
-- sub tVersions will inherit their entries from the super tVersions of the
-- specific tVersion. <BR>
-- It contains a TRANSACTION block, so it is not allowed to CALL IBSDEV1.this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_typeId           Id of the type to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersionProc$deleteType');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersionProc$deleteType(
    -- input parameters:
    IN    ai_typeId         INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_tVersionId    INT;            -- id of actual tVersion
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
  
   -- define cursor:
   -- get all tVersions of the type which shall be deleted.
    DECLARE DeleteCursor CURSOR WITH HOLD FOR 
        SELECT  id
        FROM    IBSDEV1.ibs_TVersion
        WHERE   typeId = ai_typeId;
  
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    COMMIT;
    -- open the cursor:
    OPEN DeleteCursor;
    -- get the first object:
    SET l_sqlcode = 0;
    FETCH FROM DeleteCursor INTO l_tVersionId;
    SET l_sqlstatus = l_sqlcode;
  
    -- loop through all objects:
    WHILE l_sqlcode <> 100 AND l_retValue = c_ALL_RIGHT DO
        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
            -- delete the entries for the actual tVersion:
            CALL IBSDEV1.p_TVersionProc_deleteTVersion(l_tVersionId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;
    
        -- get next object:
        SET l_sqlcode = 0;
        FETCH FROM DeleteCursor INTO l_tVersionId;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;
  
    -- close the not longer needed cursor:
    CLOSE DeleteCursor;
    -- finish the transaction:
    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT THEN 
        COMMIT;
    ELSE 
        ROLLBACK;
    END IF;
  
    -- return the state value:
    RETURN l_retValue;
  
exception1:
  
    -- roll back to the beginning of the transaction:
    ROLLBACK;
  
NonTransactionException:
  
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersionProc$deleteType', l_sqlcode,
        l_ePos, 'ai_typeId', ai_typeId, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
  
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_TVersionProc$deleteType
