--------------------------------------------------------------------------------
-- All stored procedures regarding the KeyMapper Object. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020831
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new KeyMapper Object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid                oid of the business object
-- @param   @id                 external object id
-- @param   @idDomain           id domain of the external id   
--
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  NOT_OK                  An Error occured
--  ALL_RIGHT               Action performed, values returned, everything ok.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_KeyMapper$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_KeyMapper$new
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_id               VARCHAR (255),
    IN  ai_idDomain         VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_foundOid      CHAR (8) FOR BIT DATA;
    DECLARE l_rowCount      INT DEFAULT 0;
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

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in convert ai_oid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- check if this ID/IDDOMAIN combination does already exist
    SET l_sqlcode = 0;
    SELECT  k.oid 
    INTO    l_foundOid
    FROM    IBSDEV1.ibs_KeyMapper k, IBSDEV1.ibs_Object o
    WHERE   k.id = ai_id
        AND k.idDomain = ai_idDomain
        AND o.oid = k.oid
        AND o.state = 2;

    -- check if an oid has been found
    IF (l_sqlcode = 0)                  -- at least one row found?
    THEN 
        -- check the the external key is already attached to another object
        IF (l_foundOid <> l_oid)
        THEN 
            SET l_retValue = c_NOT_OK;  -- set return value
        END IF;
    ELSE                                -- no row found or error
        if (l_sqlcode = 100)            -- no row found
        then
            -- check if an entry for this OID already exists
            SET l_sqlcode = 0;
            SELECT  COUNT(*) 
            INTO    l_rowcount
            FROM    
            (
                SELECT  k.oid 
                FROM    IBSDEV1.ibs_KeyMapper k
                WHERE   k.oid = l_oid
            ) AS temp_table1;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in entry for this OID already exists';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            IF (l_rowCount = 0)         -- no row found?
            THEN 
                -- insert the values:
                SET l_sqlcode = 0;
                INSERT  INTO IBSDEV1.ibs_KeyMapper (oid, id, idDomain)
                VALUES  (l_oid, ai_id, ai_idDomain);

                -- check if there occurred an error:
                IF (l_sqlcode <> 0)     -- any exception?
                THEN
                    -- create error entry:
                    SET l_ePos = 'Error in  insert the values';
                    GOTO exception1;    -- call common exception handler
                END IF; -- if any exception

            ELSE                        -- some row found?
                IF (l_rowCount = 1)     -- one row found?
                THEN 
                    -- overwrite the old values
                    SET l_sqlcode = 0;
                    UPDATE IBSDEV1.ibs_KeyMapper
                    SET     id = ai_id,
                            idDomain = ai_idDomain
                    WHERE   oid = l_oid;

                    -- check if there occurred an error:
                    IF (l_sqlcode <> 0)     -- any exception?
                    THEN
                        -- create error entry:
                        SET l_ePos = 'Error in overwrite the old values';
                        GOTO exception1;    -- call common exception handler
                    END IF; -- if any exception
                ELSE                    -- more than one row found?
                    SET l_retValue = c_NOT_OK;
                END IF; -- else more than one row found?
            END IF; -- else some row found?
        else                            -- error in select ID/IDDOMAIN
            -- create error entry:
            SET l_ePos = 'Error in SELECT ID/IDDOMAIN combination';
            GOTO exception1;            -- call common exception handler
        END IF; -- else error in select ID/IDDOMAIN
    END IF; -- else no row found or error
  
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_KeyMapper$new',
        l_sqlcode, l_ePos,
        '', 0, 'ai_oid_s', ai_oid_s,
        '', 0, 'ai_id', ai_id,
        '', 0, 'ai_idDomain', ai_idDomain,
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
-- p_KeyMapper$new

--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @id                 id of the external Object
-- @param   @idDomain           id domain of the external object
--
-- @output parameters:
-- @param   @oid                oid of the related object.
 
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_KeyMapper$getOid');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_KeyMapper$getOid
(
    -- input parameters:
    IN  ai_id               VARCHAR (255),
    IN  ai_idDomain         VARCHAR (63),
    -- output parameters:
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
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
    SET ao_oid              = c_NOOID;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve oid:
    SET l_sqlcode = 0;
    SELECT  k.oid 
    INTO    ao_oid
    FROM    IBSDEV1.ibs_KeyMapper k, IBSDEV1.ibs_Object o
    WHERE   k.id = ai_id
        AND k.idDomain = ai_idDomain
        AND k.oid = o.oid
        AND o.state = 2;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in retrieve oid';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_KeyMapper$getOid',
        l_sqlcode, l_ePos,
        '', 0, 'ai_id', ai_id,
        '', 0, 'ai_idDomain', ai_idDomain,
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
-- p_KeyMapper$getOid

--------------------------------------------------------------------------------
-- Gets all data from a given object. <BR>
--
-- @input parameters:
-- @param   @oid                oid of the related object.
--
-- @output parameters:
-- @param   @id                 id of the external Object
-- @param   @idDomain           id domain of the external object
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_OK                  The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_KeyMapper$getDomainID');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_KeyMapper$getDomainID
(
    -- input parameters
    IN  ai_oid_s            VARCHAR (18),
    -- output parameters:
    OUT ao_id               VARCHAR (255),
    OUT ao_idDomain         VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
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

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_oid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- retrieve id and domain:
    SET l_sqlcode = 0;
    SELECT  k.id, k.idDomain 
    INTO    ao_id, ao_idDomain
    FROM    IBSDEV1.ibs_KeyMapper k
    WHERE   k.oid = l_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in retrieve id and domain';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception
  
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_KeyMapper$getDomainID',
        l_sqlcode, l_ePos,
        '', 0, 'ai_oid_s', ai_oid_s,
        '', 0, 'ao_id', ao_id,
        '', 0, 'ao_idDomain', ao_idDomain,
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
-- p_KeyMapper$getDomainID

--------------------------------------------------------------------------------
-- Moves all EXTKEYs of the business objects below the given posNoPath
-- from the KeyMapper table to archive table.<BR>
--
-- @input parameters:
-- @param   @posNoPath      the posNoPath of the toplevel object
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_OK                  The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_KeyMapper$archiveExtKeys');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_KeyMapper$archiveExtKeys
(
    IN  ai_posNoPath        VARCHAR (254)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
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

    SET l_sqlcode = 0;
    INSERT  INTO IBSDEV1.ibs_KeyMapperArchive (oid, id, idDomain)
    SELECT  m.oid, m.id, m.idDomain
    FROM    IBSDEV1.ibs_KeyMapper m, IBSDEV1.ibs_Object o
    WHERE   o.oid = m.oid
        AND o.posNoPath LIKE ai_posNoPath || '%';

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in INSERT statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- delete the copied tuples from the ibs_KeyMapper table
    SET l_sqlcode = 0;
    DELETE  FROM IBSDEV1.ibs_KeyMapper
    WHERE   oid IN
            (
                SELECT m.oid 
                FROM IBSDEV1.ibs_Keymapper m, IBSDEV1.ibs_Object o
                WHERE o.oid = m.oid
                    AND o.posNoPath LIKE ai_posNoPath || '%'
            );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in DELETE statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_KeyMapper$archiveExtKeys',
        l_sqlcode, l_ePos,
        '', 0, 'ai_posNoPath', ai_posNoPath,
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
-- p_KeyMapper$archiveExtKeys

