-------------------------------------------------------------------------------
-- All stored procedures regarding the rights table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------


-------------------------------------------------------------------------------
-- Delete all rights of an user on an object and all its sub objects (incl.
-- rights check). <BR>
--
-- @input parameters:
-- @param   ai_oid_s            ID of the root object for which to delete the
--                              rights.
-- @param   ai_rPersonId        Person for which to delete the rights.
-- @param   ai_userId           ID of the user who wants to delete the rights.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights_01$deleteRightsRec');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights_01$deleteRightsRec
(
    -- common input parameters:
    IN  ai_rOid_s           VARCHAR (18),
    IN  ai_rPersonId        INT,
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rOid          CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_rights        INT;            -- the actual rights
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

    -- conversions (objectidstring) - all input objectids must be converted:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_rOid_s, l_rOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_rOid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get rights for this user:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Rights$checkRights(l_rOid, l_rOid, ai_userId, ai_op,
        l_rights);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in get rights for this user';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    IF (l_rights = ai_op)
    THEN 
        -- delete the rights:
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Rights$setRights(l_rOid, ai_rPersonid, 0, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in p_Rights$setRights';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- delete the rights of the person on the sub objects:
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Rights$deleteUserRightsRec(l_rOid, ai_userId, ai_op,
            ai_rPersonId);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in p_Rights$deleteUserRightsRec';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSE 
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights_01$deleteRightsRec',
        l_sqlcode, l_ePos,
        'ai_rPersonId', ai_rPersonId, 'ai_rOid_s', ai_rOid_s,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
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
-- p_Rights_01$deleteRightsRec


-------------------------------------------------------------------------------
-- Creates a new Rights_01 object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is creating the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_rOid_s           ID of the object, for which rights shall be set.
-- @param   ai_rPersonId        ID of the person/group/role for which rights
--                              shall be set.
-- @param   ai_rRights          The rights to be set.
-- @param   ai_recursive        Shall the operation be done recursively?
--
-- @output parameters:
-- @param   ao_oid_s            OID of the newly created object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights_01$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_rOid_s           VARCHAR (18),
    IN  ai_rPersonId        INT,
    IN  ai_rRights          INT,
    IN  ai_recursive        SMALLINT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object exists already
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rOid          CHAR (8) FOR BIT DATA; -- the oid of the object
                                        -- to set the rights on
    DECLARE l_rights        INT;        -- the actual rights
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

    -- conversions (objectidstring) - all input objectids must be converted:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_rOid_s, l_rOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_rOid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get rights for this user:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Rights$checkRights(l_rOid, l_rOid, ai_userId, ai_op,
        l_rights);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Rights$checkRights';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN 
        -- set the new rights:
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Rights$setRights(l_rOid, ai_rPersonId, ai_rRights, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in p_Rights$checkRights';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception

        IF (ai_recursive = 1)           -- set the rights recursive?
        THEN 
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_Rights$setUserRightsRec(l_rOid, ai_userId, ai_op,
                ai_rPersonId);

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)                 -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in p_Rights$setUserRightsRec';
                GOTO exception1;                -- call common exception handler
            END IF; -- if any exception

        END IF; -- if set the rights recursive?
    ELSE                                -- the user does not have the rights
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights_01$create',
        l_sqlcode, l_ePos,
        'ai_rPersonId', ai_rPersonId, 'ai_rOid_s', ai_rOid_s,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
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
-- p_Rights_01$create


-------------------------------------------------------------------------------
-- Gets all data from a given Rights object . <BR>
--
-- @input parameters:
-- @param   ai_rOid_s           ID of the object for which to get the rights.
-- @param   ai_rPersonId        Person for which to get the data.
-- @param   ai_userId           ID of the user who wants to get the data.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @output parameters:
-- @param   ao_containerId      ID of the rights container.
-- @param   ao_objectName       Name of the object on wich this right counts.
-- @param   ao_pOid             The object id of the user/role/group) for whom
--                              the rights are valid.
-- @param   ao_pName            The name of the user/role/group for whom the
--                              rights are valid.
-- @param   ao_rights           The rights.
--
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights_01$retrieve
(
    -- common input parameters:
    IN  ai_rOid_s           VARCHAR (18),
    IN  ai_rPersonId        INT,
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- common output parameters:
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_objectName       VARCHAR (63),
    -- type-specific output parameters:
    OUT ao_pOid             CHAR (8) FOR BIT DATA,
    OUT ao_pName            VARCHAR (63),
    OUT ao_rights           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object exists already
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rOid          CHAR (8) FOR BIT DATA;        -- the oid of the object to set the
                                            -- rights on
    DECLARE l_rights        INT;            -- the actual rights
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

    -- conversions (objectidstring) - all input objectids must be converted:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_rOid_s, l_rOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_rOid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get name of object on which this rights are set:
    SET l_sqlcode = 0;
    SELECT  name, l_rOid
    INTO    ao_objectName, ao_containerId
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_rOid;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in get name of object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get rights for this user:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Rights$checkRights(l_rOid, l_rOid, ai_userId, ai_op,
        l_rights);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Rights$checkRights';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    IF (l_rights = ai_op)               -- the user has the rights?
    THEN 
        SET l_sqlcode = 0;
        SELECT   p.oid, p.name, r.rights
        INTO ao_pOid, ao_pName, ao_rights
        FROM    (
                    SELECT  id, rights, rPersonId
                    FROM IBSDEV1.   ibs_RightsKeys
                    WHERE   rPersonId = ai_rPersonId
                ) r
                INNER JOIN
                (   SELECT  rKey
                    FROM IBSDEV1.   ibs_Object
                    WHERE   oid = l_rOid
                ) o ON o.rKey = r.id
                LEFT OUTER JOIN
                (
                    (
                        SELECT  oid, name, id
                        FROM IBSDEV1.   ibs_User
                        WHERE   state = 2
                        AND id = ai_rPersonId
                    )
                    UNION
                    (
                        SELECT  oid, name, id
                        FROM IBSDEV1.   ibs_Group
                        WHERE   state = 2
                            AND id = ai_rPersonId
                    )
                ) p ON p.id = r.rPersonId
        WHERE   o.rKey = r.id;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT statement';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
    ELSE                                -- the user does not have the rights
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_rPersonId', ai_rPersonId, 'ai_rOid_s', ai_rOid_s,
        'ai_userId', ai_userId, 'ao_objectName', ao_objectName,
        'ai_op', ai_op, 'ao_pName', ao_pName,
        'ao_rights', ao_rights, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Rights_01$retrieve

-------------------------------------------------------------------------------
-- Changes the data of a given Rights object . <BR>
--
-- @input parameters:
-- @param   ai_rOid_s           ID of the object for which to change the rights.
-- @param   ai_rPersonId        Person for which to change the data.
-- @param   ai_userId           ID of the user who wants to change the data.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_rRights          The value the rights shall be changed to.
-- @param   ai_recursive        Shall the rights be set for the sub objects,
--                              too? (default 0)
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights_01$change
(
    -- common input parameters:
    IN  ai_rOid_s           VARCHAR (18),
    IN  ai_rPersonId        INT,
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- type-specific input parameters:
    IN  ai_rRights          INT,
    IN  ai_recursive        SMALLINT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object exists already
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rOid          CHAR (8) FOR BIT DATA;        -- the oid of the object to set the
                                            -- rights on
    DECLARE l_rights        INT;            -- the actual rights
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

    -- conversions (objectidstring) - all input objectids must be converted:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_rOid_s, l_rOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_rOid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get rights for this user:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Rights$checkRights(l_rOid, l_rOid, ai_userId, ai_op,
        l_rights);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Rights$checkRights';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN 
        -- update the rights:
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Rights$setRights(l_rOid, ai_rPersonid, ai_rRights, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in p_Rights$setRights';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        IF (ai_recursive = 1)
        THEN 
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_Rights$setUserRightsRec(l_rOid, ai_userId, ai_op,
                ai_rPersonId);

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in p_Rights$setRights';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;
    ELSE                                -- the user does not have the rights
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_rPersonId', ai_rPersonId, 'ai_rOid_s', ai_rOid_s,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
        'ao_rights', ai_rRights, '', '',
        'ai_recursive', ai_recursive, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Rights_01$change

-------------------------------------------------------------------------------
-- Deletes all data from a given Rights object. <BR>
--
-- @input parameters:
-- @param   ai_rOid_s           ID of the object for which to delete the rights.
-- @param   ai_rPersonId        Person for which to delete the rights.
-- @param   ai_userId           ID of the user who wants to delete the rights.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights_01$delete
(
    -- common input parameters:
    IN  ai_rOid_s           VARCHAR (18),
    IN  ai_rPersonId        INT,
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object exists already
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rOid          CHAR (8) FOR BIT DATA;        -- the oid of the object to set the
                                            -- rights on
    DECLARE l_rights        INT;            -- the actual rights
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

    -- conversions (objectidstring) - all input objectids must be converted:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_rOid_s, l_rOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_rOid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get rights for this user:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Rights$checkRights(l_rOid, l_rOid, ai_userId, ai_op,
        l_rights);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Rights$checkRights';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)
    THEN 
        -- delete the rights:
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Rights$setRights(l_rOid, ai_rPersonId, 0, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in p_Rights$setRights';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    ELSE 
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_rPersonId', ai_rPersonId, 'ai_rOid_s', ai_rOid_s,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
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
-- p_Rights_01$delete


-- delete old procedures:
CALL IBSDEV1.p_dropProc ('p_Rights_01$getUpperOid');
