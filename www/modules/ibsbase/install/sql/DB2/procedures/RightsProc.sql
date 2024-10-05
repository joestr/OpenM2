--------------------------------------------------------------------------------
-- All stored procedures regarding the rights table. <BR>
--
-- @version     $Revision: 1.7 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Recalculate all cumulated rights. <BR>
--
-- @input parameters:
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$updateRightsCum');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$updateRightsCum ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
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

    -- initialize local variables and return values:

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_updateRC ON ROLLBACK RETAIN CURSORS;

    -- delete all cumulated rights:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_RightsCum;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- recalculate the cumulated rights and update the data:
    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, r.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E)
    FROM    IBSDEV1.ibs_RightsKeys r,
            (
                SELECT  id AS id, id AS userId
                FROM    IBSDEV1.ibs_User
                UNION
                SELECT  gu.groupId AS id, gu.userId AS userId
                FROM    IBSDEV1.ibs_GroupUser gu, IBSDEV1.ibs_User u
                WHERE   gu.userId = u.id
            ) p
    WHERE   p.id = r.rPersonId
    GROUP BY r.id, p.userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- insert rights for owner where these rights are not already
    -- set:
    -- 0x00900001 = 9437185   0x7FFFFFFF = 2147483647
    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_RightsCum (userId, rKey, rights)
    SELECT  DISTINCT 9437185, id, 2147483647 -- 0x7FFFFFFF
    FROM    IBSDEV1.ibs_RightsKeys
    WHERE   id NOT IN
            (
                SELECT  rKey
                FROM    ibs_RightsCum
                WHERE   userId = 9437185 -- 0x00900001
            );
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- insert rights for owner for rKey = 0 if these rights are not
    -- already set:
    -- (This is necessary because rKey = 0 means that there are no
    -- explicit permissions set. This also means that there are no
    -- rights cumulated for that key. And so not even the owner has
    -- access to objects with this key if there are no rights
    -- cumulated.)
    SET l_sqlcode = 0;
    IF NOT EXISTS  (
                        SELECT  rKey
                        FROM    IBSDEV1.ibs_RightsCum
                        WHERE   userId = 9437185 -- 0x00900001
                            AND rKey = 0
                    )
    THEN                                -- no rights for owner with rKey = 0
        -- insert the owner rights into the cumulation table:
        INSERT INTO IBSDEV1.ibs_RightsCum (userId, rKey, rights)
        VALUES (9437185, 0, 2147483647);
--        VALUES (0x00900001, 0, 0x7FFFFFFF)
    END IF; -- end if no rights for owner with rKey = 0
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- release the savepoint:
    RELEASE s_Rights_updateRC;
    -- finish the procedure:
    RETURN 0;
  
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_updateRC;
    -- release the savepoint:
    RELEASE s_Rights_updateRC;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$updateRightsCum', l_sqlcode, 'end',
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
END;
-- p_Rights$updateRightsCum


--------------------------------------------------------------------------------
-- Recalculate the cumulated rights for the users within a specific group. <BR>
-- The rights are recalculated for all users which are directly and indirectly
-- (recursively) within the specified group.
--
-- @input parameters:
-- @param   ai_groupId          Id of the group for whose users the rights
--                              shall be cumulated.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$updateRightsCumGroup');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$updateRightsCumGroup
(
    -- input parameters
    IN  ai_groupId          INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
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

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_updateRCG ON ROLLBACK RETAIN CURSORS;

    -- delete all cumulated rights for the users within the requested group:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_RightsCum
    WHERE   userId IN
            (
                SELECT  userId 
                FROM    IBSDEV1.ibs_GroupUser
                WHERE   groupId = ai_groupId
            );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- recalculate the cumulated rights for all users who are in the
    -- regarded group and update the data:
    SET l_sqlcode = 0;
    INSERT INTO IBSDEV1.ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, r.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E)
    FROM    IBSDEV1.ibs_RightsKeys r,
            (
                -- get the users who are in the regarded groups and compute
                -- for each of them all groups where (s)he is contained
                -- (recursively):
                SELECT  id AS id, id AS userId
                FROM    IBSDEV1.ibs_User
                WHERE   id IN
                        (
                            SELECT  userId
                            FROM    IBSDEV1.ibs_GroupUser
                            WHERE   groupId = ai_groupId
                        )
                UNION
                SELECT  groupId AS id, userId AS userId
                FROM    IBSDEV1.ibs_GroupUser
                WHERE   userId IN
                        (
                            SELECT  userId
                            FROM    IBSDEV1.ibs_GroupUser
                            WHERE   groupId = ai_groupId
                        )
            ) p
    WHERE   p.id = r.rPersonId
    GROUP BY r.id, p.userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- release the savepoint:
    RELEASE s_Rights_updateRCG;
    -- finish the procedure:
    RETURN 0;

exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_updateRCG;
    -- release the savepoint:
    RELEASE s_Rights_updateRCG;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$updateRightsCumGroup', l_sqlcode, 'end',
        'ai_groupId', ai_groupId, '', '',
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
-- p_Rights$updateRightsCumGroup


--------------------------------------------------------------------------------
-- Recalculate the cumulated rights for a specific user. <BR>
--
-- @input parameters:
-- @param   ai_userId           Id of the user for whom the rights shall be
--                              cumulated.
--
-- @output parameters:
--------------------------------------------------------------------------------
CALL IBSDEV1.p_dropProc ('p_Rights$updateRightsCumUser');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$updateRightsCumUser
(
    -- input parameters
    IN  ai_userId           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    -- local variables:
    DECLARE SQLCODE     INT;
    DECLARE l_sqlcode   INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_updateRCU ON ROLLBACK RETAIN CURSORS;

    -- delete all cumulated rights for the requested user:
    SET l_sqlcode = 0;

    DELETE FROM IBSDEV1.ibs_RightsCum
    WHERE   userId = ai_userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- recalculate the cumulated rights for given user and insert the data
    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, r.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E)
    FROM    IBSDEV1.ibs_RightsKeys r,
            (
                SELECT  id AS id, id AS userId
                FROM    IBSDEV1.ibs_User
                WHERE   id = ai_userId
                UNION
                SELECT  groupId AS id, userId AS userId
                FROM    IBSDEV1.ibs_GroupUser
                WHERE   userId = ai_userId
            ) p
    WHERE   p.id = r.rPersonId
        AND p.userId = ai_userId
    GROUP BY r.id, p.userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- release the savepoint:
    RELEASE s_Rights_updateRCU;
    -- finish the procedure:
    RETURN 0;

exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_updateRCU;
    -- release the savepoint:
    RELEASE s_Rights_updateRCU;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$updateRightsCumUser', l_sqlcode, 'end',
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
END;
-- p_Rights$updateRightsCumUser


--------------------------------------------------------------------------------
-- Recalculate the cumulated rights for a specific key. This procedure is
-- only used in trigger "TrigRightsKeysInsert" on table "ibs_RightsKeys".<BR>
--
-- @input parameters:
-- @param   ai_rightsKeysId     Id of the key for which the rights shall be
--                              cumulated.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$updateRightsCumKey');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$updateRightsCumKey
(
    IN  ai_rightsKeysId     INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
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

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_updateRCK ON ROLLBACK RETAIN CURSORS;

    -- drop all cumulated rights already derived for the actual key:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_RightsCum
    WHERE   rKey = ai_rightsKeysId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;
    -- recalculate the cumulated rights for given key and insert the data

    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, r.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E)
    FROM    (
                SELECT  *
                FROM    IBSDEV1.ibs_RightsKeys
                WHERE   id = ai_rightsKeysId
            ) r,
            (
                SELECT  id AS id, id AS userId
                FROM    IBSDEV1.ibs_User
                UNION
                SELECT  gu.groupId AS id, gu.userId AS userId
                FROM    IBSDEV1.ibs_GroupUser gu, IBSDEV1.ibs_User u
                WHERE   gu.userId = u.id
            ) p
    WHERE p.id = r.rPersonId
    GROUP BY r.id, p.userId;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- insert rights for owner where these rights are not already set:
    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_RightsCum (userId, rKey, rights)
    SELECT  DISTINCT 9437185, id, 2147483647 -- 0x00900001, id, 0x7FFFFFFF
    FROM    IBSDEV1.ibs_RightsKeys
    WHERE   id = ai_rightsKeysId
        AND id NOT IN
            (
                SELECT  rKey
                FROM    ibs_RightsCum
                WHERE   userId = 9437185 -- 0x00900001
                    AND rkey = ai_rightsKeysId
            );
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        GOTO exception1;                -- call common exception handler
    END IF;

    -- release the savepoint:
    RELEASE s_Rights_updateRCK;
    -- finish the procedure:
    RETURN 0;
  
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_updateRCK;
    -- release the savepoint:
    RELEASE s_Rights_updateRCK;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$updateRightsCumKey', l_sqlcode, 'end',
        'ai_rightsKeysId', ai_rightsKeysId, '', '',
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
-- p_Rights$updateRightsCumKey


--------------------------------------------------------------------------------
-- Delete the rights for a specific user. <BR>
--
-- @input parameters:
-- @param   ai_userId           Id of the user for whom the rights shall be
--                              deleted.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$deleteAllUserRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$deleteAllUserRights
(
    IN  ai_userId           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
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

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_deleteAUR ON ROLLBACK RETAIN CURSORS;

    -- set the number of actual values within the keys where :
    SET l_sqlcode = 0;
    UPDATE  IBSDEV1.ibs_RightsKeys
    SET     cnt = cnt - 1
    WHERE   id IN
            (
                SELECT  DISTINCT id
                FROM    IBSDEV1.ibs_RightsKeys
                WHERE   rPersonId = ai_userId
            );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        GOTO exception1;                -- call common exception handler
    END IF;

    -- delete all entries for the requested user within the keys:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_RightsKeys
    WHERE   rPersonId = ai_userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        GOTO exception1;                -- call common exception handler
    END IF;

    -- delete all cumulated rights for the requested user:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_RightsCum
    WHERE   userId = ai_userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        GOTO exception1;                -- call common exception handler
    END IF;

    -- release the savepoint:
    RELEASE s_Rights_deleteAUR;
    -- finish the procedure:
    RETURN 0;

exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_deleteAUR;
    -- release the savepoint:
    RELEASE s_Rights_deleteAUR;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$deleteAllUserRights', l_sqlcode, 'e nd',
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
END;
-- p_Rights$deleteAllUserRights


--------------------------------------------------------------------------------
-- Check if a user has the necessary access rights on an object. <BR>
--
-- @input parameters:
-- @param   ai_oid              Oid of the object for which the rights shall be
--                              checked.
-- @param   ai_containerId      Container in which the object resides.
--                              This container is used if there are no rights
--                              defined on the object itself.
-- @param   ai_userId           Id of the user for whom the rights shall be
--                              checked.
-- @param   ai_requiredRights   Rights which are required for the user.
--                              This is a bit pattern where each required right
--                              is set to 1.
--
-- @output parameters:
-- @param   ao_hasRights        Contains all of the required rights which are
--                              allowed.
--                              @hasRights == @requiredRights if the user has
--                              all required rights.
-- @return  A value representing the state of the procedure.
--  NOT_OK                      Any error occurred.
--  ALL_RIGHT                   Action may be performed, everything ok.
--  INSUFFICIENT_RIGHTS         User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$checkRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$checkRights
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_containerId      CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_requiredRights   INT,
    -- output parameters:
    OUT ao_hasRights        INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_OWNER         INT DEFAULT 9437185; -- 0x00900001;
                                            -- user id of owner

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_count         INT DEFAULT 0;
    DECLARE l_rKey          INT DEFAULT -1; -- the actual rights key
    DECLARE l_actUserId     INT;            -- actual user id
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_oid_s        VARCHAR (18);
    DECLARE el_containerId_s VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_actUserId         = ai_userId;
    SET el_oid_s            = c_NOOID_s;
    SET el_containerId_s    = c_NOOID_s;
    SET ao_hasRights        = 0;            -- no rights allowed thus far

-- body:
    -- check if the actual user is the owner of the required object:
    IF (ai_oid = c_NOOID)
    THEN 
        SELECT  COUNT (id) 
        INTO    l_count
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = ai_containerId
            AND owner = ai_userId;
    ELSE 
        SELECT  COUNT (id)
        INTO    l_count
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = ai_oid
        AND     owner = ai_userId;
    END IF;

    IF (l_count > 0)                    -- the actual user is the owner?
    THEN
        -- set the user id:
        SET l_actUserId = c_OWNER;      -- user id of owner
    END IF; -- the actual user is the owner

    -- get the rights key of the actual object:
    SET l_sqlcode = 0;

    SELECT  rKey
    INTO    l_rKey
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = ai_oid;

    IF (l_sqlcode = 100)                -- object not found => use container
    THEN
        -- get rights key of the container:
        -- (if there is no key defined for the container return with the
        -- actual rights result)
        SET l_sqlcode = 0;
        SELECT  rKey
        INTO    l_rKey
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = ai_containerId;

        IF (l_sqlcode = 100)            -- container not found?
        THEN
            -- return the actual value:
            RETURN ao_hasRights;
        END IF; -- if container not found
    END IF; -- if object not found => use container

    -- compute the rights:
    -- (if the key is not found return with the actual rights result)
    SELECT  IBSDEV1.B_AND (rights, ai_requiredRights)
    INTO    ao_hasRights
    FROM    IBSDEV1.ibs_RightsCum
    WHERE   rKey = l_rKey
        AND userId = l_actUserId;

/*
CALL p_byteToString (ai_oid, el_oid_s);
CALL p_byteToString (ai_containerId, el_containerId_s);
CALL IBSDEV1.logError (100, 'p_Rights$checkRights', l_sqlcode, 'end', 'ai_userId', ai_userId, 'ai_oid', el_oid_s, 'ai_requiredRights', ai_requiredRights, 'ai_containerId', el_containerId_s, 'l_rKey', l_rKey, '', '', 'l_count', l_count, '', '', 'l_actUserId', l_actUserId, '', '', 'ao_hasRights', ao_hasRights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
*/
    -- return the computed rights:
    RETURN ao_hasRights;

exception1:
    CALL p_byteToString (ai_oid, el_oid_s);
    CALL p_byteToString (ai_containerId, el_containerId_s);
    CALL IBSDEV1.logError (500, 'p_Rights$checkRights', l_sqlcode, 'exception',
        'ai_userId', ai_userId, 'ai_oid', el_oid_s,
        'ai_requiredRights', ai_requiredRights, 'ai_containerId', el_containerId_s,
        'ao_hasRights', ao_hasRights, '', '',
        'l_count', l_count, '', '',
        'l_actUserId', l_actUserId, '', '',
        'l_rKey', l_rKey, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Rights$checkRights


--------------------------------------------------------------------------------
-- Check if a user has the necessary access rights on an object and its sub
-- objects. <BR>
--
-- @input parameters:
-- @param   ai_oid              Oid of the object for which the rights shall be
--                              checked.
-- @param   ai_containerId      Container in which the object resides.
--                              This container is used if there are no rights
--                              defined on the object itself.
-- @param   ai_userId           Id of the user for whom the rights shall be
--                              checked.
-- @param   ai_requiredRights   Rights which are required for the user.
--                              This is a bit pattern where each required right
--                              is set to 1.
--
-- @output parameters:
-- @param   ao_hasRights        Contains all of the required rights which are
--                              allowed.
--                              @hasRights == @requiredRights if the user has
--                              all required rights.
-- @return  A value representing the state of the procedure.
--  NOT_OK                      Any error occurred.
--  ALL_RIGHT                   Action may be performed, everything ok.
--  NOT_ALL                     The user does not have the rights on all
--                              objects.
--  INSUFFICIENT_RIGHTS         User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$checkRightsRec');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$checkRightsRec
(
    -- input parameters:
    IN  ai_posNoPath        VARCHAR (254),
    IN  ai_userId           INT,
    IN  ai_requiredRights   INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    -- constants:
    DECLARE c_OWNER         INT DEFAULT 9437185; -- user id of owner
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
                                            -- for all objects
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_count         INT DEFAULT 0;  -- counter for regarded objects
    DECLARE l_countSuff     INT DEFAULT 0;  -- counter for objects with
                                            -- sufficient rights
    DECLARE l_countSuff1    INT DEFAULT 0;  -- counter 1
    DECLARE l_countSuff2    INT DEFAULT 0;  -- counter 2
    DECLARE l_sqlcode       INT DEFAULT 0;

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
    -- get the number of regarded objects:
    SET l_sqlcode = 0;
    SELECT  COUNT (oid) 
    INTO    l_count
    FROM    IBSDEV1.ibs_Object
    WHERE   posNoPath LIKE (ai_posNoPath || '%')
        AND state <> c_STATE_DELETED;

    -- check if there occurred an error:
    IF (l_sqlcode = 100)                -- nothing found?
    THEN
        -- reinitialize the variable:
        SET l_count = 0;
    -- end if nothing found
    ELSEIF (l_sqlcode <> 0)
    THEN
        SET l_ePos = 'get l_count';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- get number of objects for which the user has sufficient rights:
--------------------------
--------------------------
--------------------------
--
-- HP - Performance Tuning: split query in 2 parts
--
/*
    -- get number of objects for which the user has sufficient rights:
    SELECT  @l_countSuff = COUNT (o.oid)
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.posNoPath LIKE @ai_posNoPath || '%'
        AND o.state <> @c_STATE_DELETED
        AND r.rKey = o.rKey
        AND (
                (o.owner <> @ai_userId
                AND r.userId = @ai_userId)
            OR
                (o.owner = @ai_userId
                AND r.userId = @c_OWNER)
            )
         AND (r.rights & @ai_requiredRights) = @ai_requiredRights
*/
    SET l_sqlcode = 0;
    -- query split in 2 part!
    -- userid <> owner
    SELECT  COUNT (o.oid) 
    INTO    l_countSuff1
    FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
    WHERE   o.posNoPath LIKE (ai_posNoPath || '%')
        AND o.state <> c_STATE_DELETED
        AND r.rKey = o.rKey
        AND r.userId = ai_userId
        AND o.owner <> ai_userId
        AND b_AND(r.rights, ai_requiredRights) = ai_requiredRights;
  
    -- check if there occurred an error:
    IF (l_sqlcode = 100)                -- nothing found?
    THEN
        -- reinitialize the variable:
        SET l_countSuff1 = 0;
        SET l_sqlcode = 0;
    -- end if nothing found
    ELSEIF (l_sqlcode <> 0)
    THEN
        SET l_ePos = 'get l_countSuff1';
        GOTO exception1;                -- call common exception handler
    END IF;

    SELECT  COUNT (o.oid) 
    INTO    l_countSuff2
    FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
    WHERE   o.posNoPath LIKE (ai_posNoPath || '%')
        AND o.state <> c_STATE_DELETED
        AND r.rKey = o.rKey
        AND r.userId = c_OWNER
        AND o.owner = ai_userId
        AND b_AND(r.rights, ai_requiredRights) = ai_requiredRights;

    -- check if there occurred an error:
    IF (l_sqlcode = 100)                -- nothing found?
    THEN
        -- reinitialize the variable:
        SET l_countSuff2 = 0;
        SET l_sqlcode = 0;
    -- end if nothing found
    ELSEIF (l_sqlcode <> 0)
    THEN
        SET l_ePos = 'get l_countSuff2';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- add results
    SET l_countSuff = l_countSuff1 + l_countSuff2;
--
-- HP - Tuning End
--
--------------------------
--------------------------
--------------------------

    -- check if there are any objects for which the user has insufficient
    -- rights:
    IF (l_count <> l_countSuff)         -- at least one object for which
                                        -- the user has insufficient rights?
    THEN
        IF (l_countSuff = 0)            -- no object for which the user has
                                        -- sufficient rights?
        THEN
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        ELSE                            -- the user has sufficient rights for at
                                        -- least one object
            SET l_retValue = c_NOT_ALL;
        END IF; -- if no object ...
    END IF; -- at least one object ...
  
    -- return the computed state value:
    RETURN l_retValue;

exception1:
    CALL IBSDEV1.logError (500, 'p_Rights$checkRightsRec', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_posNoPath', ai_posNoPath,
        'ai_requiredRights', ai_requiredRights, '', '',
        'l_count', l_count, '', '',
        'l_countSuff1', l_countSuff1, '', '',
        'l_countSuff2', l_countSuff2, '', '',
        'l_countSuff', l_countSuff, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Rights$checkRightsRec


--------------------------------------------------------------------------------
-- Check if a user has the necessary access rights on an object and its sub
-- objects. <BR>
--
-- @input parameters:
-- @param   ai_oid_s            Oid of the object for which the rights shall be
--                              checked.
-- @param   ai_userId           Id of the user for whom the rights shall be
--                              checked.
-- @param   ai_requiredRights   Rights which are required for the user.
--                              This is a bit pattern where each required right
--                              is set to 1.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_NOT_OK                     Any error occurred.
-- c_ALL_RIGHT                  Check performed, values returned, evrythng ok.
-- c_NOT_ALL                    The user does not have the rights on all
--                              objects.
-- c_INSUFFICIENT_RIGHTS        User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$checkUserRightsRec');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$checkUserRightsRec
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (63),
    IN  ai_userId           INT,
    IN  ai_requiredRights   INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
                                            -- for all objects

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_count         INT DEFAULT 0;  -- counter
    DECLARE l_posNoPath     VARCHAR (254);  -- pos no path of the object
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- the oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;

-- body:
    -- conversions (OBJECTIDSTRING)
    -- all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- get the posNoPath of the actual object:
    SET l_sqlcode = 0;

    SELECT  posNoPath
    INTO    l_posNoPath
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)
    THEN
        SET l_ePos = 'get posNoPath';
        GOTO exception1;                --- call common exception handler
    END IF;

    -- check if there are any objects for which the rights cannot be set:
    CALL IBSDEV1.p_Rights$checkRightsRec
            (l_posNoPath, ai_userId, ai_requiredRights);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$checkUserRightsRec', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_requiredRights', ai_requiredRights, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Rights$checkUserRightsRec


--------------------------------------------------------------------------------
-- Check if a user has the necessary access rights on an object. <BR>
--
-- @input parameters:
-- @param   ai_oid_s            Oid of the object for which the rights shall be
--                              checked.
-- @param   ai_containerId_s    Container in which the object resides.
--                              This container is used if there are no rights
--                              defined on the object itself.
-- @param   ai_userId           Id of the user for whom the rights shall be
--                              checked.
-- @param   ai_requiredRights   Rights which are required for the user.
--                              This is a bit pattern where each required right
--                              is set to 1.
--
-- @output parameters:
-- @param   ao_hasRights        Contains all of the required rights which are
--                              allowed.
--                              @hasRights == @requiredRights if the user has
--                              all required rights.
-- @return  A value representing the state of the procedure.
--  NOT_OK                      Any error occurred.
--  INSUFFICIENT_RIGHTS         User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$checkObjectRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$checkObjectRights(
    -- input
    IN  ai_oid_s            VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (63),
    IN  ai_userId           INT,
    IN  ai_requiredRights   INT,
    -- output
    OUT ao_hasRights        INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong

    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE SQLCODE         INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
-- body:
    -- conversions (OBJECTIDSTRING)
    -- all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_StringToByte (ai_containerId_s, l_containerId);

    -- call basic checkRights procedure:
    CALL IBSDEV1.p_Rights$checkRights
            (l_oid, l_containerId, ai_userid, ai_requiredRights, ao_hasRights);
  
    -- return the computed rights:
    RETURN ao_hasRights;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$checkObjectRights', l_sqlcode, 'end',
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_requiredRights', ai_requiredRights, 'ai_containerId_s', ai_containerId_s,
        'ao_hasRights', ao_hasRights, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Rights$checkObjectRights


--------------------------------------------------------------------------------
-- Get the rights of a rights person (user, group, ...) on a specific object.
-- <BR>
--
-- @input parameters:
-- @param   ai_rOid             Oid of the object on which rights shall be
--                              defined.
-- @param   ai_rPersonId        Id of the rights person (user, group, ...)
--
-- @output parameters:
-- @param   ao_rights           Rights of the person on the object.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$getRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$getRights
(
    -- input parameters:
    IN  ai_rOid             CHAR (8) FOR BIT DATA,
    IN  ai_rPersonId        INT,
    -- output parameters:
    OUT ao_rights           INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_RIGHTS_ALL    INT DEFAULT 2147483647; -- 0x7FFFFFFF
                                            -- all rights together

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- oid of the container
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_rOid_s       VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET ao_rights           = 0;

-- body:
    -- get container oid:
    SET l_sqlcode = 0;

    SELECT  containerId
    INTO    l_containerId
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = ai_rOid;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)
    THEN
        SET l_ePos = 'get containerId';
        GOTO exception1;                --- call common exception handler
    END IF;

    -- get rights for this user:
    CALL IBSDEV1.p_Rights$checkRights
            (ai_rOid, l_containerId, ai_rPersonId, c_RIGHTS_ALL, ao_rights);

    RETURN 0;                           -- terminate the procedure

exception1:
    -- log the error:
    CALL p_binaryToHexString (ai_rOid, el_rOid_s);
    CALL IBSDEV1.logError (500, 'p_Rights$getRights', l_sqlcode, l_ePos,
        'ai_rPersonId', ai_rPersonId, 'ai_rOid', el_rOid_s,
        'ao_rights', ao_rights, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
END;
-- p_Rights$getRights


--------------------------------------------------------------------------------
-- Get the rights of a rights person (user, group, ...) on a specific object
-- and its container. <BR>
--
-- @input parameters:
-- @param   ai_rOid_s           Oid of the object on which rights shall be
--                              defined.
-- @param   ai_rPersonId        Id of the rights person (user, group, ...)
--
-- @output parameters:
-- @param   ao_objectRights     Rights of the person on the object.
-- @param   ao_containerRights  Rights of the person on the container of the
--                              object.
-- @param   ao_isContainer      Tells if the object with rOid_s is a container.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$getRightsContainer');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$getRightsContainer
(
    -- input parameters:
    IN  ai_rOid_s           VARCHAR (63),
    IN  ai_rPersonId        INT,
    -- output parameters:
    OUT ao_objectRights     INT,
    OUT ao_containerRights  INT,
    OUT ao_isContainer      SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rOid          CHAR (8) FOR BIT DATA; -- oid of the rights object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- oid of the container
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_containerId       = c_NOOID;
    SET ao_objectRights     = 0;
    SET ao_containerRights  = 0;
    SET ao_isContainer      = 0;

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    -- convert string representation to binary representation:
    CALL IBSDEV1.p_stringToByte (ai_rOid_s, l_rOid);

    -- get container oid:
    SELECT  containerId, isContainer
    INTO    l_containerId, ao_isContainer
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_rOid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        SET l_ePos = 'get container oid';
        GOTO exception1;                --- call common exception handler
    END IF;

    -- root object is its own container:
    IF (l_containerId = c_NOOID)        -- object has no container?
    THEN
        SET l_containerId = l_rOid;     -- set container = object
    END IF;
    -- get rights for user on object:
    CALL IBSDEV1.p_Rights$getRights (l_rOid, ai_rPersonId, ao_objectRights);
    -- get rights for user on container:
    CALL IBSDEV1.p_Rights$getRights
            (l_containerId, ai_rPersonId, ao_containerRights);

    RETURN 0;                           -- terminate the procedure

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$getRightsContainer', l_sqlcode, l_ePos,
        'ai_rPersonId', ai_rPersonId, 'ai_rOid_s', ai_rOid_s,
        'ao_objectRights', ao_objectRights, '', '',
        'ao_containerRights', ao_containerRights, '', '',
        'ao_isContainer', ao_isContainer, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
END;
-- p_Rights$getRightsContainer


--------------------------------------------------------------------------------
-- Get a key being similar to an actual key. <BR>
-- The key returned is equal to the actual key in all parts except one which
-- is explicitly given.
-- If there does not exist a corresponding key it is automatically created.
--
-- @input parameters:
-- @param   ai_actId            Id of actual key for which to find a similar
--                              one.
-- @param   ai_rPersonId        Id of the rights person (user, group, ...)
-- @param   ai_rights           New rights.
--
-- @output parameters:
-- @param   ao_id               The id of the found key.
--                              0 ..... there are no rights left, i.e. the new
--                                      key is empty.
--                              -1 .... no key was found and none could be
--                                      generated.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT                   Action performed, values returned, evrythng ok.
--  INSUFFICIENT_RIGHTS         User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$getKey');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$getKey
(
    -- input parameters:
    IN  ai_actId            INT,
    IN  ai_rPersonId        INT,
    IN  ai_rights           INT,
    -- output parameters:
    OUT ao_id               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_cnt           INT DEFAULT 0;  -- counter
    DECLARE l_id            INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT DEFAULT 0;
    DECLARE l_rights        INT;
    DECLARE l_rPersonId     INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET ao_id               = -1;

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_getKey ON ROLLBACK RETAIN CURSORS;

--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'aa', 'ao_id', ao_id, '', '', 'ai_actId', ai_actId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- get old number of rights except those for the required person:
    SET l_sqlcode = 0;
    SELECT  COUNT (*)
    INTO    l_cnt
    FROM    IBSDEV1.ibs_RightsKeys
    WHERE   id = ai_actId
        AND rPersonId <> ai_rPersonId;
--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'b', 'ao_id', ao_id, '', '', 'l_cnt', l_cnt, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- check if there was an error:
    IF (l_sqlcode <> 0)                 -- error occurred?
    THEN 
        IF (l_sqlcode = 100)            -- no data found?
        THEN
            SET l_cnt = 0;
        -- end if no data found
        ELSE                            -- any other error
            GOTO exception1;            -- call common exception handler
        END IF; -- else any other error
    END IF; -- if error occurred

    -- check if there will be some rights left:
    IF (l_cnt = 0 AND ai_rights <= 0)   -- there will be no rights left?
    THEN
        SET ao_id = 0;                  -- set the new rights key
        RETURN 0;                       -- terminate procedure
    END IF; -- if there will be no rights left

    IF (ai_rights > 0)                  -- there shall be some rights set?
    THEN
        SET l_cnt = l_cnt + 1;          -- the new right must also be counted
        -- check if there exists already a key for the new rights and set
        -- return value:
        SET l_sqlcode = 0;
        SELECT  MIN (id)
        INTO    ao_id
        FROM
        (
            SELECT COUNT (*) AS cnt, rkc.id
            FROM    IBSDEV1.ibs_RightsKeys rkc,
                    (
                        SELECT  rPersonId, rights
                        FROM    IBSDEV1.ibs_RightsKeys
                        WHERE   id = ai_actId
                            AND rPersonId <> ai_rPersonId
                        UNION
                        SELECT  ai_rPersonId AS rPersonId,
                                ai_rights AS rights
                        FROM    SYSIBM.SYSDUMMY1
                    ) r
            WHERE   rkc.cnt = l_cnt
                AND r.rPersonId = rkc.rPersonId
                AND r.rights = rkc.rights
            GROUP BY rkc.id
        ) res
        WHERE   cnt = l_cnt;

        -- check if there was an error:
        IF (l_sqlcode <> 0)             -- error occurred?
        THEN 
            IF (l_sqlcode = 100)        -- no id found?
            THEN
                SET ao_id = -1;
            -- end if no data found
            ELSE                        -- any other error
                GOTO exception1;        -- call common exception handler
            END IF; -- else any other error
        END IF; -- if error occurred
    -- end if there shall be some rights set
    ELSE                                -- don't set rights
        -- check if there exists already a key for the new rights and set return
        -- value:
        SET l_sqlcode = 0;
        SELECT  MIN (id)
        INTO    ao_id
        FROM
        (
            SELECT  COUNT (rkc.id) AS cnt, rkc.id
            FROM    IBSDEV1.ibs_RightsKeys rkc, IBSDEV1.ibs_rightsKeys r
            WHERE   rkc.cnt = l_cnt
                AND r.id = ai_actId
                AND r.rPersonId <> ai_rPersonId
                AND r.rPersonId = rkc.rPersonId
                AND r.rights = rkc.rights
            GROUP BY rkc.id
        ) res
        WHERE   cnt = l_cnt;

        -- check if there was an error:
        IF (l_sqlcode <> 0)             -- error occurred?
        THEN 
            IF (l_sqlcode = 100)        -- no id found?
            THEN
                SET ao_id = -1;
            -- end if no data found
            ELSE                        -- any other error
                GOTO exception1;        -- call common exception handler
            END IF; -- else any other error
        END IF; -- if error occurred
    END IF; -- else don't set rights

    -- ensure that the id has an allowed value:
    IF (ao_id IS NULL)                  -- no id found?
    THEN
        SET ao_id = -1;                 -- set corresponding return value
    END IF; -- no id found

    IF (ao_id = -1)                     -- no corresponding key found?
    THEN
        -- create a new rights key:
        IF (ai_rights > 0)              -- there shall be some rights set?
        THEN
            SET l_sqlcode = 0;

            INSERT INTO IBSDEV1.ibs_RightsKeys (id, rPersonId, rights, cnt,
                    r00, r01, r02, r03, r04, r05, r06, r07,
                    r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                    r10, r11, r12, r13, r14, r15, r16, r17,
                    r18, r19, r1A, r1B, r1C, r1D, r1E)
            (
                SELECT  -1, rPersonId, rights, l_cnt,
                        IBSDEV1.b_AND (rights,          1),
                        IBSDEV1.b_AND (rights,          2),
                        IBSDEV1.b_AND (rights,          4),
                        IBSDEV1.b_AND (rights,          8),
                        IBSDEV1.b_AND (rights,         16),
                        IBSDEV1.b_AND (rights,         32),
                        IBSDEV1.b_AND (rights,         64),
                        IBSDEV1.b_AND (rights,        128),
                        IBSDEV1.b_AND (rights,        256),
                        IBSDEV1.b_AND (rights,        512),
                        IBSDEV1.b_AND (rights,       1024),
                        IBSDEV1.b_AND (rights,       2048),
                        IBSDEV1.b_AND (rights,       4096),
                        IBSDEV1.b_AND (rights,       8192),
                        IBSDEV1.b_AND (rights,      16384),
                        IBSDEV1.b_AND (rights,      32768),
                        IBSDEV1.b_AND (rights,      65536),
                        IBSDEV1.b_AND (rights,     131072),
                        IBSDEV1.b_AND (rights,     262144),
                        IBSDEV1.b_AND (rights,     524288),
                        IBSDEV1.b_AND (rights,    1048576),
                        IBSDEV1.b_AND (rights,    2097152),
                        IBSDEV1.b_AND (rights,    4194304),
                        IBSDEV1.b_AND (rights,    8388608),
                        IBSDEV1.b_AND (rights,   16777216),
                        IBSDEV1.b_AND (rights,   33554432),
                        IBSDEV1.b_AND (rights,   67108864),
                        IBSDEV1.b_AND (rights,  134217728),
                        IBSDEV1.b_AND (rights,  268435456),
                        IBSDEV1.b_AND (rights,  536870912),
                        IBSDEV1.b_AND (rights, 1073741824)
                FROM    IBSDEV1.ibs_RightsKeys
                WHERE   id = ai_actId
                    AND rPersonId <> ai_rPersonId
                UNION
                SELECT  -1, ai_rPersonId, ai_rights, l_cnt,
                        IBSDEV1.b_AND (ai_rights,          1),
                        IBSDEV1.b_AND (ai_rights,          2),
                        IBSDEV1.b_AND (ai_rights,          4),
                        IBSDEV1.b_AND (ai_rights,          8),
                        IBSDEV1.b_AND (ai_rights,         16),
                        IBSDEV1.b_AND (ai_rights,         32),
                        IBSDEV1.b_AND (ai_rights,         64),
                        IBSDEV1.b_AND (ai_rights,        128),
                        IBSDEV1.b_AND (ai_rights,        256),
                        IBSDEV1.b_AND (ai_rights,        512),
                        IBSDEV1.b_AND (ai_rights,       1024),
                        IBSDEV1.b_AND (ai_rights,       2048),
                        IBSDEV1.b_AND (ai_rights,       4096),
                        IBSDEV1.b_AND (ai_rights,       8192),
                        IBSDEV1.b_AND (ai_rights,      16384),
                        IBSDEV1.b_AND (ai_rights,      32768),
                        IBSDEV1.b_AND (ai_rights,      65536),
                        IBSDEV1.b_AND (ai_rights,     131072),
                        IBSDEV1.b_AND (ai_rights,     262144),
                        IBSDEV1.b_AND (ai_rights,     524288),
                        IBSDEV1.b_AND (ai_rights,    1048576),
                        IBSDEV1.b_AND (ai_rights,    2097152),
                        IBSDEV1.b_AND (ai_rights,    4194304),
                        IBSDEV1.b_AND (ai_rights,    8388608),
                        IBSDEV1.b_AND (ai_rights,   16777216),
                        IBSDEV1.b_AND (ai_rights,   33554432),
                        IBSDEV1.b_AND (ai_rights,   67108864),
                        IBSDEV1.b_AND (ai_rights,  134217728),
                        IBSDEV1.b_AND (ai_rights,  268435456),
                        IBSDEV1.b_AND (ai_rights,  536870912),
                        IBSDEV1.b_AND (ai_rights, 1073741824)
                FROM    SYSIBM.SYSDUMMY1
            );

            -- check if there was an error:
            IF (l_sqlcode <> 0)         -- an error occurred?
            THEN
                SET l_ePos = 'insert1';
                GOTO exception1;        -- call common exception handler
            END IF; -- if an error occurred
        -- end if there shall be some rights set
        ELSE                            -- don't set rights for actual person
            SET l_sqlcode = 0;

            INSERT INTO IBSDEV1.ibs_RightsKeys (id, rPersonId, rights, cnt,
                    r00, r01, r02, r03, r04, r05, r06, r07,
                    r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                    r10, r11, r12, r13, r14, r15, r16, r17,
                    r18, r19, r1A, r1B, r1C, r1D, r1E)
            SELECT  -1, rPersonId, rights, l_cnt,
                    IBSDEV1.b_AND (rights,          1),
                    IBSDEV1.b_AND (rights,          2),
                    IBSDEV1.b_AND (rights,          4),
                    IBSDEV1.b_AND (rights,          8),
                    IBSDEV1.b_AND (rights,         16),
                    IBSDEV1.b_AND (rights,         32),
                    IBSDEV1.b_AND (rights,         64),
                    IBSDEV1.b_AND (rights,        128),
                    IBSDEV1.b_AND (rights,        256),
                    IBSDEV1.b_AND (rights,        512),
                    IBSDEV1.b_AND (rights,       1024),
                    IBSDEV1.b_AND (rights,       2048),
                    IBSDEV1.b_AND (rights,       4096),
                    IBSDEV1.b_AND (rights,       8192),
                    IBSDEV1.b_AND (rights,      16384),
                    IBSDEV1.b_AND (rights,      32768),
                    IBSDEV1.b_AND (rights,      65536),
                    IBSDEV1.b_AND (rights,     131072),
                    IBSDEV1.b_AND (rights,     262144),
                    IBSDEV1.b_AND (rights,     524288),
                    IBSDEV1.b_AND (rights,    1048576),
                    IBSDEV1.b_AND (rights,    2097152),
                    IBSDEV1.b_AND (rights,    4194304),
                    IBSDEV1.b_AND (rights,    8388608),
                    IBSDEV1.b_AND (rights,   16777216),
                    IBSDEV1.b_AND (rights,   33554432),
                    IBSDEV1.b_AND (rights,   67108864),
                    IBSDEV1.b_AND (rights,  134217728),
                    IBSDEV1.b_AND (rights,  268435456),
                    IBSDEV1.b_AND (rights,  536870912),
                    IBSDEV1.b_AND (rights, 1073741824)
            FROM    IBSDEV1.ibs_RightsKeys
            WHERE   id = ai_actId
                AND rPersonId <> ai_rPersonId;

            -- check if there was an error:
            IF (l_sqlcode <> 0)         -- an error occurred?
            THEN
                SET l_ePos = 'insert1';
                GOTO exception1;        -- call common exception handler
            END IF; -- if an error occurred
        END IF;-- else don't set rights for actual person

        -- get the id of the newly generated key:
        SET l_sqlcode = 0;

        SELECT  MAX (id)
        INTO    ao_id
        FROM    ibs_RightsKeys;

        -- check if there was an error:
        IF (l_sqlcode <> 0)             -- an error occurred?
        THEN
            SET l_ePos = 'get the id of the newly generated key';
            GOTO exception1;            -- call common exception handler
        END IF; -- if an error occurred

        -- cumulate the rights:
        CALL p_Rights$updateRightsCumKey (ao_id);
    END IF; -- if no corresponding key found

    -- release the savepoint:
    RELEASE s_Rights_getKey;
    -- finish the procedure:
    RETURN 0;
  
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_getKey;
    -- release the savepoint:
    RELEASE s_Rights_getKey;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$getKey', l_sqlcode, l_ePos,
        'ai_actId', ai_actId, '', '',
        'ai_rPersonId', ai_rPersonId, '', '',
        'ai_rights', ai_rights, '', '',
        'ao_id', ao_id, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set return value:
    SET ao_id = -1;
END;
-- p_Rights$getKey


--------------------------------------------------------------------------------
-- Get the actual rights key of an object. <BR>
--
-- @input parameters:
-- @param   ai_oid              The oid of the object for which the rights key
--                              shall be computed.
--
-- @output parameters:
-- @return  The id of the rights key which is set for the object or -1 if there
--          was no key found.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$getKey');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$getKey
(
    -- input parameters:
    IN  ai_actId            INT,
    IN  ai_rPersonId        INT,
    IN  ai_rights           INT,
    -- output parameters:
    OUT ao_id               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_cnt           INT DEFAULT 0;  -- counter
    DECLARE l_id            INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT DEFAULT 0;
    DECLARE l_rights        INT;
    DECLARE l_rPersonId     INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET ao_id               = -1;

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_getKey ON ROLLBACK RETAIN CURSORS;

--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'begin', 'ao_id', ao_id, '', '', 'ai_actId', ai_actId, '', '', 'ai_rPersonId', ai_rPersonId, '', '', 'ai_rights', ai_rights, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- get old number of rights except those for the required person:
    SET l_sqlcode = 0;
    SELECT  COUNT (*)
    INTO    l_cnt
    FROM    IBSDEV1.ibs_RightsKeys
    WHERE   id = ai_actId
        AND rPersonId <> ai_rPersonId;
--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'b', 'ao_id', ao_id, '', '', 'l_cnt', l_cnt, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- check if there was an error:
    IF (l_sqlcode <> 0)                 -- error occurred?
    THEN 
        IF (l_sqlcode = 100)            -- no data found?
        THEN
            SET l_cnt = 0;
        -- end if no data found
        ELSE                            -- any other error
            GOTO exception1;            -- call common exception handler
        END IF; -- else any other error
    END IF; -- if error occurred

    -- check if there will be some rights left:
    IF (l_cnt = 0 AND ai_rights <= 0)   -- there will be no rights left?
    THEN
        SET ao_id = 0;                  -- set the new rights key
        RETURN 0;                       -- terminate procedure
    END IF; -- if there will be no rights left

    IF (ai_rights > 0)                  -- there shall be some rights set?
    THEN
        SET l_cnt = l_cnt + 1;          -- the new right must also be counted
        -- check if there exists already a key for the new rights and set
        -- return value:
        SET l_sqlcode = 0;
        SELECT  MIN (id)
        INTO    ao_id
        FROM
        (
            SELECT  COUNT (*) AS cnt, rkc.id
            FROM    IBSDEV1.ibs_RightsKeys rkc,
                    (
                        SELECT  rPersonId, rights
                        FROM    IBSDEV1.ibs_RightsKeys
                        WHERE   id = ai_actId
                            AND rPersonId <> ai_rPersonId
                        UNION
                        SELECT  ai_rPersonId AS rPersonId,
                                ai_rights AS rights
                        FROM    SYSIBM.SYSDUMMY1
                    ) r
            WHERE   rkc.cnt = l_cnt
                AND r.rPersonId = rkc.rPersonId
                AND r.rights = rkc.rights
            GROUP BY rkc.id
        ) res
        WHERE   cnt = l_cnt;
--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'after if select', 'ao_id', ao_id, '', '', 'l_cnt', l_cnt, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        -- check if there was an error:
        IF (l_sqlcode <> 0)             -- error occurred?
        THEN 
            IF (l_sqlcode = 100)        -- no id found?
            THEN
                SET ao_id = -1;
            -- end if no data found
            ELSE                        -- any other error
                GOTO exception1;        -- call common exception handler
            END IF; -- else any other error
        END IF; -- if error occurred
    -- end if there shall be some rights set
    ELSE                                -- don't set rights
        -- check if there exists already a key for the new rights and set return
        -- value:
        SET l_sqlcode = 0;
        SELECT  MIN (id)
        INTO    ao_id
        FROM
        (
            SELECT  COUNT (rkc.id) AS cnt, rkc.id
            FROM    IBSDEV1.ibs_RightsKeys rkc, IBSDEV1.ibs_rightsKeys r
            WHERE   rkc.cnt = l_cnt
                AND r.id = ai_actId
                AND r.rPersonId <> ai_rPersonId
                AND r.rPersonId = rkc.rPersonId
                AND r.rights = rkc.rights
            GROUP BY rkc.id
        ) res
        WHERE   cnt = l_cnt;
--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'after else select', 'ao_id', ao_id, '', '', 'l_cnt', l_cnt, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        -- check if there was an error:
        IF (l_sqlcode <> 0)             -- error occurred?
        THEN 
            IF (l_sqlcode = 100)        -- no id found?
            THEN
                SET ao_id = -1;
            -- end if no data found
            ELSE                        -- any other error
                GOTO exception1;        -- call common exception handler
            END IF; -- else any other error
        END IF; -- if error occurred
    END IF; -- else don't set rights

    -- ensure that the id has an allowed value:
    IF (ao_id IS NULL)                  -- no id found?
    THEN
        SET ao_id = -1;                 -- set corresponding return value
    END IF; -- no id found

    IF (ao_id = -1)                     -- no corresponding key found?
    THEN
        -- create a new rights key:
        IF (ai_rights > 0)              -- there shall be some rights set?
        THEN
            SET l_sqlcode = 0;

            INSERT INTO IBSDEV1.ibs_RightsKeys (id, rPersonId, rights, cnt,
                    r00, r01, r02, r03, r04, r05, r06, r07,
                    r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                    r10, r11, r12, r13, r14, r15, r16, r17,
                    r18, r19, r1A, r1B, r1C, r1D, r1E)
            (
                SELECT  -1, rPersonId, rights, l_cnt,
                        IBSDEV1.b_AND (rights,          1),
                        IBSDEV1.b_AND (rights,          2),
                        IBSDEV1.b_AND (rights,          4),
                        IBSDEV1.b_AND (rights,          8),
                        IBSDEV1.b_AND (rights,         16),
                        IBSDEV1.b_AND (rights,         32),
                        IBSDEV1.b_AND (rights,         64),
                        IBSDEV1.b_AND (rights,        128),
                        IBSDEV1.b_AND (rights,        256),
                        IBSDEV1.b_AND (rights,        512),
                        IBSDEV1.b_AND (rights,       1024),
                        IBSDEV1.b_AND (rights,       2048),
                        IBSDEV1.b_AND (rights,       4096),
                        IBSDEV1.b_AND (rights,       8192),
                        IBSDEV1.b_AND (rights,      16384),
                        IBSDEV1.b_AND (rights,      32768),
                        IBSDEV1.b_AND (rights,      65536),
                        IBSDEV1.b_AND (rights,     131072),
                        IBSDEV1.b_AND (rights,     262144),
                        IBSDEV1.b_AND (rights,     524288),
                        IBSDEV1.b_AND (rights,    1048576),
                        IBSDEV1.b_AND (rights,    2097152),
                        IBSDEV1.b_AND (rights,    4194304),
                        IBSDEV1.b_AND (rights,    8388608),
                        IBSDEV1.b_AND (rights,   16777216),
                        IBSDEV1.b_AND (rights,   33554432),
                        IBSDEV1.b_AND (rights,   67108864),
                        IBSDEV1.b_AND (rights,  134217728),
                        IBSDEV1.b_AND (rights,  268435456),
                        IBSDEV1.b_AND (rights,  536870912),
                        IBSDEV1.b_AND (rights, 1073741824)
                FROM    IBSDEV1.ibs_RightsKeys
                WHERE   id = ai_actId
                    AND rPersonId <> ai_rPersonId
                UNION
                SELECT  -1, ai_rPersonId, ai_rights, l_cnt,
                        IBSDEV1.b_AND (ai_rights,          1),
                        IBSDEV1.b_AND (ai_rights,          2),
                        IBSDEV1.b_AND (ai_rights,          4),
                        IBSDEV1.b_AND (ai_rights,          8),
                        IBSDEV1.b_AND (ai_rights,         16),
                        IBSDEV1.b_AND (ai_rights,         32),
                        IBSDEV1.b_AND (ai_rights,         64),
                        IBSDEV1.b_AND (ai_rights,        128),
                        IBSDEV1.b_AND (ai_rights,        256),
                        IBSDEV1.b_AND (ai_rights,        512),
                        IBSDEV1.b_AND (ai_rights,       1024),
                        IBSDEV1.b_AND (ai_rights,       2048),
                        IBSDEV1.b_AND (ai_rights,       4096),
                        IBSDEV1.b_AND (ai_rights,       8192),
                        IBSDEV1.b_AND (ai_rights,      16384),
                        IBSDEV1.b_AND (ai_rights,      32768),
                        IBSDEV1.b_AND (ai_rights,      65536),
                        IBSDEV1.b_AND (ai_rights,     131072),
                        IBSDEV1.b_AND (ai_rights,     262144),
                        IBSDEV1.b_AND (ai_rights,     524288),
                        IBSDEV1.b_AND (ai_rights,    1048576),
                        IBSDEV1.b_AND (ai_rights,    2097152),
                        IBSDEV1.b_AND (ai_rights,    4194304),
                        IBSDEV1.b_AND (ai_rights,    8388608),
                        IBSDEV1.b_AND (ai_rights,   16777216),
                        IBSDEV1.b_AND (ai_rights,   33554432),
                        IBSDEV1.b_AND (ai_rights,   67108864),
                        IBSDEV1.b_AND (ai_rights,  134217728),
                        IBSDEV1.b_AND (ai_rights,  268435456),
                        IBSDEV1.b_AND (ai_rights,  536870912),
                        IBSDEV1.b_AND (ai_rights, 1073741824)
                FROM    SYSIBM.SYSDUMMY1
            );
--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'after if insert', 'ao_id', ao_id, '', '', 'l_cnt', l_cnt, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

            -- check if there was an error:
            IF (l_sqlcode <> 0)         -- an error occurred?
            THEN
                SET l_ePos = 'insert1';
                GOTO exception1;        -- call common exception handler
            END IF; -- if an error occurred
        -- end if there shall be some rights set
        ELSE                            -- don't set rights for actual person
            SET l_sqlcode = 0;

            INSERT INTO IBSDEV1.ibs_RightsKeys (id, rPersonId, rights, cnt,
                    r00, r01, r02, r03, r04, r05, r06, r07,
                    r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                    r10, r11, r12, r13, r14, r15, r16, r17,
                    r18, r19, r1A, r1B, r1C, r1D, r1E)
            SELECT  -1, rPersonId, rights, l_cnt,
                    IBSDEV1.b_AND (rights,          1),
                    IBSDEV1.b_AND (rights,          2),
                    IBSDEV1.b_AND (rights,          4),
                    IBSDEV1.b_AND (rights,          8),
                    IBSDEV1.b_AND (rights,         16),
                    IBSDEV1.b_AND (rights,         32),
                    IBSDEV1.b_AND (rights,         64),
                    IBSDEV1.b_AND (rights,        128),
                    IBSDEV1.b_AND (rights,        256),
                    IBSDEV1.b_AND (rights,        512),
                    IBSDEV1.b_AND (rights,       1024),
                    IBSDEV1.b_AND (rights,       2048),
                    IBSDEV1.b_AND (rights,       4096),
                    IBSDEV1.b_AND (rights,       8192),
                    IBSDEV1.b_AND (rights,      16384),
                    IBSDEV1.b_AND (rights,      32768),
                    IBSDEV1.b_AND (rights,      65536),
                    IBSDEV1.b_AND (rights,     131072),
                    IBSDEV1.b_AND (rights,     262144),
                    IBSDEV1.b_AND (rights,     524288),
                    IBSDEV1.b_AND (rights,    1048576),
                    IBSDEV1.b_AND (rights,    2097152),
                    IBSDEV1.b_AND (rights,    4194304),
                    IBSDEV1.b_AND (rights,    8388608),
                    IBSDEV1.b_AND (rights,   16777216),
                    IBSDEV1.b_AND (rights,   33554432),
                    IBSDEV1.b_AND (rights,   67108864),
                    IBSDEV1.b_AND (rights,  134217728),
                    IBSDEV1.b_AND (rights,  268435456),
                    IBSDEV1.b_AND (rights,  536870912),
                    IBSDEV1.b_AND (rights, 1073741824)
            FROM    IBSDEV1.ibs_RightsKeys
            WHERE   id = ai_actId
                AND rPersonId <> ai_rPersonId;
--CALL IBSDEV1.logError (100, 'p_Rights$getKey', l_sqlcode, 'after else insert', 'ao_id', ao_id, '', '', 'l_cnt', l_cnt, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

            -- check if there was an error:
            IF (l_sqlcode <> 0)         -- an error occurred?
            THEN
                SET l_ePos = 'insert1';
                GOTO exception1;        -- call common exception handler
            END IF; -- if an error occurred
        END IF;-- else don't set rights for actual person

        -- get the id of the newly generated key:
        SET l_sqlcode = 0;

        SELECT  MAX (id)
        INTO    ao_id
        FROM    ibs_RightsKeys;

        -- check if there was an error:
        IF (l_sqlcode <> 0)             -- an error occurred?
        THEN
            SET l_ePos = 'get the id of the newly generated key';
            GOTO exception1;            -- call common exception handler
        END IF; -- if an error occurred
    END IF; -- if no corresponding key found

    -- release the savepoint:
    RELEASE s_Rights_getKey;
    -- finish the procedure:
    RETURN 0;
  
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_getKey;
    -- release the savepoint:
    RELEASE s_Rights_getKey;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$getKey', l_sqlcode, l_ePos,
        'ai_actId', ai_actId, '', '',
        'ai_rPersonId', ai_rPersonId, '', '',
        'ai_rights', ai_rights, '', '',
        'ao_id', ao_id, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set return value:
    SET ao_id = -1;
END;
-- p_Rights$getKey


--------------------------------------------------------------------------------
-- Check if a specific rights key is used by at least one object. <BR>
-- If it is not used by any object, the key is deleted.
-- The replacement key reuses the old key. It is converted to the old id and
-- all business objects' rKey attributes having the value of the replacement
-- id are changed to the deleted id accordingly.
--
-- @input parameters:
-- @param   ai_id               The id of the key which shall be checked for
--                              being used.
-- @param   ai_replId           The id of the key which shall replace the first
--                              one.
--
-- @output parameters:
-- @param   ao_newId            The new id by which ai_replId has been replaced.
--                              This value can be ai_replId itself if unchanged
--                              or ai_id otherwise.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$reuseKey');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$reuseKey
(
    -- input parameters:
    IN  ai_id               INT,
    IN  ai_replId           INT,
    -- output parameters:
    OUT ao_newId            INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_count         INT DEFAULT 0;
    DECLARE l_mcount        INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET ao_newId            = ai_replId;

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_reuseKey ON ROLLBACK RETAIN CURSORS;

    -- check if the old key is not used by another object and is not a business
    -- object itself:
    IF (ai_id > 0)                      -- the old id is valid?
    THEN
        -- count how many objects use this key:
        SET l_sqlcode = 0;

        SELECT  COUNT (*)
        INTO    l_count
        FROM    IBSDEV1.ibs_Object
        WHERE   rKey = ai_id;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
        THEN
            GOTO exception1;            -- call common exception handler
        END IF;

        -- check if the rights key is not an object itself:
        -- (a value > 0 says that it is an object)
        SET l_sqlcode = 0;

        SELECT  COUNT (o.oid) 
        INTO    l_mcount
        FROM    IBSDEV1.ibs_RightsKeys r, IBSDEV1.ibs_Object o
        WHERE   rKey = ai_id
            AND r.oid = o.oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
        THEN
            GOTO exception1;            -- call common exception handler
        END IF;

        IF (l_count = 0) AND (l_mcount <= 0)
                                        -- the key is not longer used and is
                                        -- not an object?
        THEN
            -- delete the old key:
            SET l_sqlcode = 0;

            DELETE FROM IBSDEV1.ibs_RightsKeys
            WHERE   id = ai_id;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                GOTO exception1;        -- call common exception handler
            END IF;

            -- delete all cumulated rights for this key:
            SET l_sqlcode = 0;

            DELETE FROM IBSDEV1.ibs_RightsCum
            WHERE   rKey = ai_id;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                GOTO exception1;        -- call common exception handler
            END IF;

            -- reuse the old id for the new key:
            SET l_sqlcode = 0;

            UPDATE  IBSDEV1.ibs_RightsKeys
            SET     id = ai_id
            WHERE   id = ai_replId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                GOTO exception1;        -- call common exception handler
            END IF;

            -- set the key within the cumulated rights:
            SET l_sqlcode = 0;

            UPDATE  IBSDEV1.ibs_RightsCum
            SET     rKey = ai_id
            WHERE   rKey = ai_replId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                GOTO exception1;        -- call common exception handler
            END IF;

            -- store the key id within all objects which use this key:
            SET l_sqlcode = 0;

            UPDATE  IBSDEV1.ibs_Object
            SET     rKey = ai_id
            WHERE   rKey = ai_replId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN
                GOTO exception1;        -- call common exception handler
            END IF;

            -- set the id:
            SET ao_newId = ai_id;
        END IF; -- if the key is not used and is not an object
    END IF; -- if the old id was valid

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- release the savepoint:
    RELEASE s_Rights_reuseKey;
    -- finish the procedure:
    RETURN 0;
  
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_reuseKey;
    -- release the savepoint:
    RELEASE s_Rights_reuseKey;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Rights$reuseKey', l_sqlcode, 'end',
        'ai_id', ai_id, '', '',
        'ai_replId', ai_replId, '', '',
        'ao_newId', ao_newId, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set return value:
    SET ao_newId = ai_replId;
END;
-- p_Rights$reuseKey


--------------------------------------------------------------------------------
-- Set the rights key for a specific object and all of its tabs. <BR>
-- The rights keys of the references to the changed object are changed, too.
--
-- @input parameters:
-- @param   ai_rOid             Oid of the object for which to set the rKey.
-- @param   ai_rKey             Id of the rights key.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  NOT_OK                  Something went wrong during performing the
--                          operation.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$setKey');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$setKey
(
    -- input parameters:
    IN    ai_rOid           CHAR (8) FOR BIT DATA,
    IN    ai_rKey           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object
    DECLARE c_ParticipantContainer INT DEFAULT 16850945; -- 0x01012001
                                            -- tVersionId of
                                            -- ParticipantContainer
    DECLARE c_Reference     INT DEFAULT 16842801; -- 0x01010031
                                            -- tVersionId of Reference

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_rOid_s       VARCHAR (18);

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
    -- set rights key of the actual object and its tabs:
    SET l_sqlcode = 0;
    UPDATE  IBSDEV1.ibs_Object
    SET     rKey = ai_rKey
    WHERE   oid = ai_rOid
        OR  (   containerId = ai_rOid
            AND containerKind = 2
--! HACK ...
            -- don't propagate rights to some specific object types:
            AND tVersionId <> c_ParticipantContainer
            AND tVersionId <> c_Reference
--! ... HACK
            )
        OR  (   linkedObjectId IN
                (
                    SELECT  oid 
                    FROM    IBSDEV1.ibs_Object
                    WHERE   oid = ai_rOid
                        OR  (   containerId = ai_rOid
                            AND containerKind = 2
--! HACK ...
                            -- don't propagate rights to some specific object
                            -- types:
                            AND tVersionId <> c_ParticipantContainer
                            AND tVersionId <> c_Reference
--! ... HACK
                            )
                )
            AND state <> c_STATE_DELETED
            );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        SET l_ePos = 'update rKey';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- return the computed state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL p_binaryToHexString (ai_rOid, el_rOid_s);
    CALL IBSDEV1.logError (500, 'p_Rights$setKey', l_sqlcode, l_ePos,
        'ai_rKey', ai_rKey, 'ai_rOid', el_rOid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Rights$setKey


--------------------------------------------------------------------------------
-- Set the rights key for a specific object and all of its sub objects. <BR>
-- The rights keys of the references to the changed object are changed, too.
--
-- @input parameters:
-- @param   ai_rOid             Oid of the object for which to set the rKey.
-- @param   ai_oldRKey          Old id of the rights key.
-- @param   ai_rKey             Id of the rights key.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  NOT_OK                  Something went wrong during performing the
--                          operation.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$setKeyRec');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$setKeyRec
(
    -- input parameters:
    IN  ai_rOid             CHAR (8) FOR BIT DATA,
    IN  ai_oldRKey          INT,
    IN  ai_rKey             INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object
    DECLARE c_EMPTYPOSNOPATH VARCHAR (254) DEFAULT '0000';
                                            -- default/invalid posNoPath
    DECLARE c_ParticipantContainer INT DEFAULT 16850945; -- 0x01012001
                                            -- tVersionId of
                                            -- ParticipantContainer
    DECLARE c_Reference     INT DEFAULT 16842801; -- 0x01010031
                                            -- tVersionId of Reference

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_posNoPath     VARCHAR (254);   -- the pos no path of the object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_rOid_s       VARCHAR (18);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_posNoPath         = c_EMPTYPOSNOPATH;

-- body:
    -- get posNoPath of actual object:
    SET l_sqlcode = 0;

    SELECT  posNoPath
    INTO    l_posNoPath
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = ai_rOid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        SET l_ePos = 'get posNoPath';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- set rights key of the actual object and its subsequent objects
    -- having the same old rights key:
    SET l_sqlcode = 0;

    UPDATE  IBSDEV1.ibs_Object
    SET     rKey = ai_rKey
    WHERE   (   posNoPath LIKE (l_posNoPath || '%')
            AND state <> c_STATE_DELETED
            AND posNoPath <> c_EMPTYPOSNOPATH
            AND rKey = ai_oldRKey
            -- don't propagate rights to some specific object types:
            AND (   oid = ai_rOid
--! HACK ...
                OR  (
                        tVersionId <> c_ParticipantContainer
                    AND tVersionId <> c_Reference
                    )
--! ... HACK
                )
            )
        OR  (   linkedObjectId IN
                (   SELECT  oid
                    FROM    IBSDEV1.ibs_Object
                    WHERE   posNoPath LIKE (l_posNoPath || '%')
                        AND state <> c_STATE_DELETED
                        AND posNoPath <> c_EMPTYPOSNOPATH
                        AND rKey = ai_oldRKey
                        -- don't propagate rights to some specific object
                        -- types:
                        AND (   oid = ai_rOid
--! HACK ...
                            OR  (
                                    tVersionId <> c_ParticipantContainer
                                AND tVersionId <> c_Reference
                                )
--! ... HACK
                            )
                )
            AND state <> c_STATE_DELETED
            );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN 
        SET l_ePos = 'update';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- return the computed state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL p_binaryToHexString (ai_rOid, el_rOid_s);
    CALL IBSDEV1.logError (500, 'p_Rights$setKeyRec', l_sqlcode, l_ePos,
        'ai_rKey', ai_rKey, 'ai_rOid', el_rOid_s,
        'ai_oldRKey', ai_oldRKey, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Rights$setKeyRec


--------------------------------------------------------------------------------
-- Set the rights for a specific person on an object. <BR>
-- There is no rights check done!
--
-- @input parameters:
-- @param   ai_rOid             Oid of the object on which rights are defined.
-- @param   ai_rPersonId        Id of the rights person (user, group, ...)
-- @param   ai_rights           New rights.
-- @param   ai_rec              Set rights for sub containers, too.
--                              (1 true, 0 false).
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$setRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$setRights
(
    -- input parameters:
    IN  ai_rOid             CHAR (8) FOR BIT DATA,
    IN  ai_rPersonId        INT,
    IN  ai_rights           INT,
    IN  ai_rec              SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object
    DECLARE c_EMPTYPOSNOPATH VARCHAR (254) DEFAULT '0000';
                                            -- default/invalid posNoPath

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_posNoPath     VARCHAR (254);  -- the pos no path of the object
    DECLARE l_oldId         INT;            -- the old rights key
    DECLARE l_id            INT DEFAULT 0;  -- the new rights key
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    DECLARE el_rOid_s       VARCHAR (18);
 
    -- define cursor:
    DECLARE updateCursor INSENSITIVE CURSOR FOR
        SELECT  DISTINCT rKey
        FROM    IBSDEV1.ibs_Object
        WHERE   posNoPath LIKE (l_posNoPath || '%')
            AND state <> c_STATE_DELETED
            AND posNoPath <> c_EMPTYPOSNOPATH;

    -- exception handlers:  
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights_setRights ON ROLLBACK RETAIN CURSORS;

--CALL IBSDEV1.logError (100, 'p_Rights$setRights', l_sqlcode, '1', 'ai_rPersonId', ai_rPersonId, '', '', 'ai_rights', ai_rights, '', '', 'ai_rec', ai_rec, '', '', 'l_sqlcode', l_sqlcode, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    IF (ai_rec = 1)                     -- set rights for all objects with
                                        -- same path prefix?
    THEN
        -- get posNoPath of actual object:
        SET l_sqlcode = 0;
        SELECT  posNoPath
        INTO    l_posNoPath
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = ai_rOid;

--CALL IBSDEV1.logError (100, 'p_Rights$setRights', l_sqlcode, '2', 'ai_rPersonId', ai_rPersonId, '', '', 'ai_rights', ai_rights, '', '', 'ai_rec', ai_rec, '', '', 'l_sqlcode', l_sqlcode, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
        THEN
            GOTO exception1;            -- call common exception handler
        ELSEIF (l_sqlcode = 100)        -- the referenced object was not found?
        THEN
            -- set corresponding return value:
            SET l_retValue = c_NOT_OK;
        END IF; -- else if the referenced object was not found

        -- open the cursor:
        OPEN updateCursor;
        -- get the first object:
--CALL IBSDEV1.logError (100, 'p_Rights$setRights', l_sqlcode, '3', 'ai_rPersonId', ai_rPersonId, '', '', 'ai_rights', ai_rights, '', '', 'ai_rec', ai_rec, '', '', 'l_sqlcode', l_sqlcode, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        SET l_sqlcode = 0;
        FETCH FROM updateCursor INTO l_oldId;
        SET l_sqlstatus = l_sqlcode;

        -- loop through all objects:
        WHILE (l_retValue = c_ALL_RIGHT AND l_sqlstatus = 0)
                                        -- another tuple found
        DO
            -- get the key containing the resulting rights:
            CALL IBSDEV1.p_Rights$getKey
                    (l_oldId, ai_rPersonId, ai_rights, l_id);
            -- check if there was an error:
            IF (l_id = -1)          -- an error occurred?
            THEN 
                SET l_retValue = c_NOT_OK; -- set return value
            -- end if an error occurred
            ELSE                    -- no error
                -- set rights key of all subsequent objects of the actual
                -- object with the same old key:
                CALL IBSDEV1.p_Rights$setKeyRec (ai_rOid, l_oldId, l_id);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            END IF; -- else no error

            -- get next tuple:
            SET l_sqlcode = 0;
            FETCH FROM updateCursor INTO l_oldId;
            SET l_sqlstatus = l_sqlcode;
        END WHILE; -- while antoher tuple found

        -- close the not longer needed cursor:
        CLOSE updateCursor;
    -- end if set rights for all objects with same path prefix?

    ELSE                                -- set rights only for given object
        -- get the old rights key:
        SET l_sqlcode = 0;
        SELECT  rKey
        INTO    l_oldId
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = ai_rOid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
        THEN 
            GOTO exception1;
        END IF;

        -- get the new rights key:
        CALL IBSDEV1.p_Rights$getKey (l_oldId, ai_rPersonId, ai_rights, l_id);

        -- check if there was an error:
        IF (l_id = -1)                  -- an error occurred?
        THEN
            SET l_retValue = c_NOT_OK;  -- set return value
        ELSE                            -- no error
            -- set rights key of the actual object:
            CALL IBSDEV1.p_Rights$setKey (ai_rOid, l_id);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF; -- else no error
    END IF; -- else set rights only for given object

    -- check if there occurred an error:
    IF (l_retValue <> c_ALL_RIGHT)      -- an error occurred?
    THEN
        GOTO exception1;                -- call exception handler
    END IF; -- an error occurred

    -- release the savepoint:
    RELEASE s_Rights_setRights;
    -- finish the procedure:
    RETURN 0;
  
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Rights_setRights;
    -- release the savepoint:
    RELEASE s_Rights_setRights;

    CALL p_binaryToHexString (ai_rOid, el_rOid_s);
    CALL IBSDEV1.logError (500, 'p_Rights$setRights', l_sqlcode, 'error',
        'ai_rPersonId', ai_rPersonId, 'ai_rOid', el_rOid_s,
        'ai_rights', ai_rights, '', '',
        'ai_rec', ai_rec, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
END;
-- p_Rights$setRights


--------------------------------------------------------------------------------
-- Add rights of a user/group to the actual rights the user has. <BR>
-- If there are no rights defined yet, the added rights are stored as actual
-- rights. If there are already some rights defined the new rights are added
-- to this rights (bitwise OR).
--
-- @input parameters:
-- @param   ai_rOid             Oid of the object on which rights are defined.
-- @param   ai_rPersonId        Id of the rights person (user, group, ...)
-- @param   ai_rights           New rights.
-- @param   ai_rec              Set rights for sub containers, too.
--                              (1 true, 0 false).
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$addRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$addRights
(
    -- input parameters:
    IN  ai_rOid             CHAR (8) FOR BIT DATA,
    IN  ai_rPersonId        INT,
    IN  ai_rights           INT,
    IN  ai_rec              SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object
    DECLARE c_EMPTYPOSNOPATH VARCHAR (254) DEFAULT '0000';
                                            -- default/invalid posNoPath

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_posNoPath     VARCHAR (254);  -- the pos no path of the object
    DECLARE l_oldId         INT;            -- the old rights key
    DECLARE l_id            INT DEFAULT 0;  -- the new rights key
    DECLARE l_newRights     INT;            -- the new rights
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;

    -- define cursor:
    DECLARE updateCursor INSENSITIVE CURSOR FOR
        SELECT  DISTINCT rKey 
        FROM    IBSDEV1.ibs_Object
        WHERE   posNoPath LIKE (l_posNoPath || '%')
            AND state <> c_STATE_DELETED
            AND posNoPath <> c_EMPTYPOSNOPATH;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_newRights         = ai_rights;

-- body:
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    IF ai_rec = 1 THEN 
    -- get posNoPath of actual object:
        SET l_sqlcode = 0;
        SELECT posNoPath
        INTO l_posNoPath
        FROM IBSDEV1.ibs_Object
        WHERE oid = ai_rOid;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            GOTO exception1;
        END IF;
        -- open the cursor:
        OPEN updateCursor;
        -- get the first object:
        SET l_sqlcode = 0;
        FETCH FROM updateCursor INTO l_oldId;
        SET l_sqlstatus = l_sqlcode;
        -- loop through all objects:
        WHILE (l_retValue = c_ALL_RIGHT AND l_sqlstatus = 0)
                                        -- another tuple found
        DO
            -- initialize the new rights:
            SET l_newRights = ai_rights;
            -- get resulting rights:
            -- compute the resulting rights as binary or
            -- (= bitwise sum) of the old and the new rights:
            -- (if there are no rights set for the rPerson the value of
            -- l_newRights stays as initialized => l_newRights = ai_rights)
            SELECT IBSDEV1.b_or(rights, ai_rights) AS newRights
            INTO l_newRights
            FROM IBSDEV1.ibs_RightsKeys
            WHERE id = l_oldId
                AND rPersonId = ai_rPersonId;
            -- get the key containing the resulting rights:
            CALL IBSDEV1.p_Rights$getKey(l_oldId, ai_rPersonId, l_newRights, l_id);
            -- check if there was an error:
            IF l_id = -1 THEN 
                SET l_retValue = c_NOT_OK;
            ELSE 
                -- set rights key of all subsequent objects of the actual
                -- object:
                CALL IBSDEV1.p_Rights$setKeyRec(ai_rOid, l_oldId, l_id);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
                END IF;

            -- get next tuple:
            SET l_sqlcode = 0;
            FETCH FROM updateCursor INTO l_oldId;
            SET l_sqlstatus = l_sqlcode;
        END WHILE; -- another tuple found

        -- close the not longer needed cursor:
        CLOSE updateCursor;
    ELSE 
        -- get the old rights key:
        SET l_sqlcode = 0;
        SELECT rKey
        INTO l_oldId
        FROM IBSDEV1.ibs_Object
        WHERE oid = ai_rOid;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            GOTO exception1;
        END IF;
        -- get resulting rights:
        -- compute the resulting rights as binary or
        -- (= bitwise sum) of the old and the new rights:
        -- (if there are no rights set for the rPerson the value of
        -- l_newRights stays as initialized => l_newRights = ai_rights)
        SELECT IBSDEV1.b_or(rights, ai_rights) AS newRights
        INTO l_newRights
        FROM IBSDEV1.ibs_RightsKeys
        WHERE id = l_oldId
            AND rPersonId = ai_rPersonId;
        -- get the key containing the resulting rights:
        CALL IBSDEV1.p_Rights$getKey (l_oldId, ai_rPersonId, l_newRights, l_id);
        -- check if there was an error:
        IF l_id = -1 THEN 
            SET l_retValue = c_NOT_OK;
        ELSE 
            -- set rights key of the actual object:
            CALL IBSDEV1.p_Rights$setKey(ai_rOid, l_id);
        END IF;
    END IF;
    -- check if there occurred an error:
    IF l_retValue <> c_ALL_RIGHT THEN 
        GOTO exception1;
    END IF;
    -- finish the procedure:
    RETURN 0;
exception1:
    -- roll back to the save point:
    ROLLBACK;
END;

--------------------------------------------------------------------------------
-- Propagate the rights of one object to another object. <BR>
-- After this operation the secondary object has the same rights as the primary
-- one.
--
-- @input parameters:
-- @param   ai_masterObjectId   Oid of the object which is the master for this
--                              operation.
-- @param   ai_rOid             Oid of the object on which rights are defined.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$propagateRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$propagateRights(
    -- input parameters:
    IN    ai_masterObjectId CHAR (8) FOR BIT DATA,
    IN    ai_rOid           CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_rKey          INT;            -- the actual rights key
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rKey              = -1;

-- body:
    -- get the rights key of the master object:
    CALL IBSDEV1.p_Rights$getRightsKey(ai_masterObjectId);
    GET DIAGNOSTICS l_rKey = RETURN_STATUS;
    -- check if there was an error:
    IF l_rKey = -1 THEN 
        SET l_retValue = c_NOT_OK;
    ELSE 
        -- set the rights key of the secondary object:
        CALL IBSDEV1.p_Rights$setKey(ai_rOid, l_rKey);
    END IF;
END;
-- p_Rights$propagateRights

--------------------------------------------------------------------------------
-- Propagate the rights of one person on an object to another object. <BR>
-- After this operation the person has on the secondary object the same rights
-- as on the primary one.
--
-- @input parameters:
-- @param   ai_masterOid        Object from which the rights shall be
--                              propagated.
-- @param   ai_rOid             Object to which the rights shall be propagated.
-- @param   ai_rPersonId        ID of the person for which the rights shall be
--                              propagated.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- Delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$propagateUserRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$propagateUserRights(
    IN    ai_masterOid      CHAR (8) FOR BIT DATA,
    IN    ai_rOid           CHAR (8) FOR BIT DATA,
    IN    ai_rPersonId      INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_id            INT;            -- the actual rights key
    DECLARE l_oldId         INT;
    DECLARE l_rights        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_id                = 0;
    SET l_oldId             = 0;
    SET l_rights            = 0;

-- body:
    -- get the old rights key:
    SET l_sqlcode = 0;

    SELECT rKey
    INTO l_oldId
    FROM IBSDEV1.ibs_Object
    WHERE oid = ai_rOid;

    -- check if there was an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_oldId = 0;
    END IF;
    -- get the rights to be set:
    SET l_sqlcode = 0;

    SELECT rights
    INTO l_rights
    FROM IBSDEV1.ibs_RightsKeys r, IBSDEV1.ibs_Object o
    WHERE r.rPersonId = ai_rPersonId
        AND o.oid = ai_masterOid
        AND o.rKey = r.id;
    -- check if there was an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_rights = 0;
    END IF;
    -- get the new rights key:
--    CALL IBSDEV1.p_Rights$getKey (l_oldId, CAST(ai_rPersonId AS INT), l_rights, l_id);
    CALL IBSDEV1.p_Rights$getKey (l_oldId, ai_rPersonId, l_rights, l_id);
    -- check if there was an error:
    IF l_id = -1 THEN 
        SET l_retValue = c_NOT_OK;
    ELSE 
        -- update the rights key of the object:
        CALL IBSDEV1.p_Rights$setKey(ai_rOid, l_id);
    END IF;
END;
-- p_Rights$propagateUserRights

--------------------------------------------------------------------------------
-- Delete all rights of a given object. <BR>
--
-- @input parameters:
-- @param   ai_oid              ID of the object for which to delete the rights.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$deleteObjectRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$deleteObjectRights
(
    IN    ai_oid            CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
-- body:
    -- delete all rights defined on the object:
    CALL IBSDEV1.p_Rights$setKey (ai_oid, 0);
END;
-- p_Rights$deleteObjectRights


--------------------------------------------------------------------------------
-- Set the rights of a specific user and Businessobject. <BR>
-- With the operation parameter you can define the specific operation.
--
-- @input parameters:
-- @param   ai_rOid             ID of the object, for which rights shall be set.
-- @param   ai_rPersonId        ID of the person/group/role for which rights
--                              shall be set.
-- @param   ai_rights           The rights to be set.
-- @param   ai_operation        The operation to be performed with the rights:
--                              1 ... overwrite the actual rights.
--                              2 ... delete all rights of person on object.
--                              3 ... delete all rights of object.
--                              4 ... add the new rights to the actual ones,
--                                    if no rights exist insert new.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$set');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$set(
    -- input parameters:
    IN    ai_rOid           CHAR (8) FOR BIT DATA,
    IN    ai_rPersonId      INT,
    IN    ai_rights         INT,
    IN    ai_operation      INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    IF ai_operation = 1 THEN 
        CALL IBSDEV1.p_Rights$setRights(ai_rOid, ai_rPersonId, ai_rights, 0);
    ELSE 
        IF ai_operation = 2 THEN 
            CALL IBSDEV1.p_Rights$setRights(ai_rOid, ai_rPersonId, 0, 0);
        ELSE 
            IF ai_operation = 3 THEN 
                CALL IBSDEV1.p_Rights$deleteObjectRights(ai_rOid);
            ELSE 
                IF ai_operation = 4 THEN 
                    CALL IBSDEV1.p_Rights$addRights(ai_rOid, ai_rPersonId, ai_rights,
                        0);
                END IF;
            END IF;
        END IF;
    END IF;
END;
-- p_Rights$set

--------------------------------------------------------------------------------
-- Propagate the rights of an object to all its subobjects.
--
-- @input parameters:
-- @param   ai_oid_s            The oid of the object which rights should be
--                              propagated.
-- @param   ai_userId           The user which is doing the operation.
-- @param   ai_op               The operation that is being done.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  NOT_OK                  Something went wrong during performing the
--                          operation.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_ALL                 The operation could not be performed on all objects
--                          (because the user did not have enough rights to
--                          set the rights on all these objects).
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$setRightsRecursive');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$setRightsRecursive(
    -- input parameters:
    IN    ai_oid_s          VARCHAR (63),
    IN    ai_userId         INT,
    IN    ai_op             INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_OWNER         INT;            -- user id of owner
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT;            -- operation could not be performed
                                            -- for all objects
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object
    DECLARE c_ParticipantContainer INT;     -- tVersionId of
                                            -- ParticipantContainer
    DECLARE c_Reference     INT;            -- tVersionId of Reference

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the object id of the act. object
    DECLARE l_posNoPath     VARCHAR (254);   -- pos no path of the object
    DECLARE l_rKey          INT;            -- rKey of the object
    DECLARE l_count         INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_OWNER             = 9437185;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_NOT_ALL           = 31;
    SET c_ParticipantContainer = 16850945;
    SET c_Reference         = 16842801;
  
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rKey              = 0;
    SET l_count             = 0;
  
-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- get posNoPath and rights key of actual object:
    SET l_sqlcode = 0;
    SELECT posNoPath, rKey
    INTO l_posNoPath, l_rKey
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_oid;
    IF l_sqlcode = 100 THEN 
        SET l_rowcount = 0;
    ELSE 
        SET l_rowcount = 1;
    END IF;

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 OR l_rowcount = 0 THEN 
        GOTO exception1;
    END IF;
    -- check if there are any objects for which the rights cannot be set:
    CALL IBSDEV1.p_Rights$checkRightsRec(l_posNoPath, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL THEN 
------------------------------------------------------------------------------
------------------------------------------------------------------------------
------------------------------------------------------------------------------
--
-- HP - Performance Tuning: split update in 2 parts
--
/*
            -- set rights for subsequent objects:
            UPDATE  ibs_Object
            SET     rKey = @l_rKey
            WHERE   oid IN
                    (SELECT o.oid
                    FROM    ibs_Object o, ibs_RightsCum r
                    WHERE   o.posNoPath LIKE @l_posNoPath || '%'
                        AND o.oid <> @l_oid
                        AND r.rKey = o.rKey
                        AND (
                                (o.owner <> @ai_userId
                                AND r.userId = @ai_userId)
                            OR
                                (o.owner = @ai_userId
                                AND r.userId = @c_OWNER)
                            )
                        AND (r.rights & @ai_op) = @ai_op
                        AND state <> @c_STATE_DELETED
                    )
                    -- don't propagate rights to some specific object types:
--! HACK ...
                    AND tVersionId <> @c_ParticipantContainer
                    AND tVersionId <> @c_Reference
... HACK
*/
        --
        -- SPLIT update statement in 2 faster parts - no OR needed
        --       (sets rights for subsequent objects)
        --

        -- 1. part of update statment
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_Object
        SET rKey = l_rKey
        WHERE oid IN    (
                            SELECT o.oid 
                            FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
                            WHERE o.posNoPath LIKE l_posNoPath || '%'
                                AND r.userId = ai_userId
                                AND o.owner <> ai_userId
                                AND o.oid <> l_oid
                                AND r.rKey = o.rKey
                                AND b_and(r.rights, ai_op) = ai_op
                                AND state <> c_STATE_DELETED
--! HACK ...
                                -- don't propagate rights to some
                                -- specific object types:
                                AND tVersionId <> c_ParticipantContainer
                                AND tVersionId <> c_Reference
--! ... HACK
                        );
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            -- throw exception1
            GOTO exception1;
        END IF;
        -- 2. part of update statment
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_Object
        SET rKey = l_rKey
        WHERE oid IN    (
                            SELECT o.oid 
                            FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
                            WHERE o.posNoPath LIKE (l_posNoPath || '%')
                                AND o.owner = ai_userId
                                AND r.userId = c_OWNER
                                AND o.oid <> l_oid
                                AND r.rKey = o.rKey
                                AND b_and(r.rights, ai_op) = ai_op
                                AND state <> c_STATE_DELETED
--! HACK ...
                            -- don't propagate rights to some specific
                            -- object types:
                                AND tVersionId <> c_ParticipantContainer
                                AND tVersionId <> c_Reference
--! ... HACK
                         );
    
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            -- throw exception1
            GOTO exception1;
        END IF;
    END IF;
    -- finish the transaction:
    IF l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL THEN 
        COMMIT;
    ELSE 
        ROLLBACK;
    END IF;
    -- return the state value:
    RETURN l_retValue;
exception1:
    -- undo all changes:
    ROLLBACK;
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Rights$setRightsRecursive

--------------------------------------------------------------------------------
-- Propagate the rights an user has on an object to all its sub objects.
-- (incl. rights check)
--
-- @input parameters:
-- @param   ai_oid              The oid of the root object from which rights
--                              should be propagated.
-- @param   ai_userId           The user which is doing the operation.
-- @param   ai_op               The operation that is being done.
-- @param   ai_rPersonId        The person for whom the rights shall be
--                              propagated.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  NOT_OK                  Something went wrong during performing the
--                          operation.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_ALL                 The operation could not be performed on all objects
--                          (because the user did not have enough rights to
--                          set the rights on all these objects).
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$setUserRightsRec');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$setUserRightsRec(
    -- input parameters:
    IN    ai_oid            CHAR (8) FOR BIT DATA,
    IN    ai_userId         INT,
    IN    ai_op             INT,
    IN    ai_rPersonId      INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_OWNER         INT;            -- user id of owner
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT;            -- operation could not be performed
                                            -- for all objects
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object
    DECLARE c_ParticipantContainer INT;     -- tVersionId of
                                            -- ParticipantContainer
    DECLARE c_Reference     INT;            -- tVersionId of Reference

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_posNoPath     VARCHAR (254);   -- pos no path of the object
    DECLARE l_rights        INT;            -- the rights
    DECLARE l_actOid        CHAR (8) FOR BIT DATA;        -- the actual object id
    DECLARE l_count         INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    DECLARE l_rowcount      INT;
  
    -- define cursor:
    DECLARE updateCursor CURSOR WITH HOLD FOR 
        SELECT o.oid 
        FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
        WHERE o.posNoPath LIKE (l_posNoPath || '%')
            AND o.state <> c_STATE_DELETED
            AND o.oid <> ai_oid
            AND r.rKey = o.rKey
            AND (
                    (
                        o.owner <> ai_userId
                        AND  r.userId = ai_userId
                    )
                    OR
                    (
                        o.owner = ai_userId
                        AND r.userId = c_OWNER
                    )
                )
            AND b_and(r.rights, ai_op) = ai_op;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_OWNER             = 9437185;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_NOT_ALL           = 31;
    SET c_ParticipantContainer = 16850945;
    SET c_Reference         = 16842801;
  
    -- initialize local variables:
    SET l_retValue          = c_NOT_OK;
    SET l_rights            = 0;
    SET l_count             = 0;
  
-- body:
    -- get the rights of the specific person for the defined object:
    SET l_sqlcode = 0;
    SELECT r.rights, o.posNoPath 
    INTO l_rights, l_posNoPath
    FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsKeys r
    WHERE o.oid = ai_oid
        AND r.rPersonId = ai_rPersonId
        AND o.rKey = r.id;
    IF l_sqlcode = 100 THEN 
        SET l_rowcount = 0;
    ELSE 
        SET l_rowcount = 1;
    END IF;
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 OR l_rowcount = 0 THEN 
        GOTO exception1;
    END IF;
    -- check if there are any objects for which the rights cannot be set:
    CALL IBSDEV1.p_Rights$checkRightsRec(l_posNoPath, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL THEN 
        -- open the cursor:
        OPEN updateCursor;
        -- get the first object:
        SET l_sqlcode = 0;
        FETCH FROM updateCursor INTO l_actOid;
        SET l_sqlstatus = l_sqlcode;
        -- loop through all found objects:
        WHILE (l_sqlstatus = 0)         -- another tuple found
        DO
            -- set the rights for the actual object:
            CALL IBSDEV1.p_Rights$setRights
                    (l_actOid, ai_rPersonId, l_rights, 0);

            -- get next object:
            SET l_sqlcode = 0;
            FETCH FROM updateCursor INTO l_actOid;
            SET l_sqlstatus = l_sqlcode;
        END WHILE; -- another tuple found
    
        -- close the cursor:
        CLOSE updateCursor;
    END IF;

    -- finish the transaction:
    IF l_retValue <> c_ALL_RIGHT AND l_retValue <> c_NOT_ALL THEN 
        -- roll back to the save point:
        ROLLBACK;
    END IF;
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- roll back to the save point:
    ROLLBACK;
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Rights$setUserRightsRec


--------------------------------------------------------------------------------
-- Delete all rights of an user on an object and all its sub objects (incl.
-- rights check). <BR>
--
-- @input parameters:
-- @param   ai_oid              The oid of the root object from which rights
--                              should be deleted.
-- @param   ai_userId           The user who is doing the operation.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_rPersonId        The person for whom the rights shall be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT                   Action performed, values returned, evrythng ok.
--  INSUFFICIENT_RIGHTS         User has no right to perform action.
--  OBJECTNOTFOUND              The required object was not found within the
--                              database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$deleteUserRightsRec');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$deleteUserRightsRec(
    -- input parameters:
    IN    ai_oid            CHAR (8) FOR BIT DATA,
    IN    ai_userId         INT,
    IN    ai_op             INT,
    IN    ai_rPersonId      INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_OWNER         INT;            -- user id of owner
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT;            -- operation could not be performed
                                            -- for all objects
    DECLARE c_STATE_DELETED INT DEFAULT 1;  -- the object was deleted
    DECLARE c_STATE_ACTIVE  INT DEFAULT 2;  -- active state of object
    DECLARE c_ParticipantContainer INT;     -- tVersionId of
                                            -- ParticipantContainer
    DECLARE c_Reference     INT;            -- tVersionId of Reference

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_posNoPath     VARCHAR (254);   -- pos no path of the object
    DECLARE l_rights        INT;            -- the rights
    DECLARE l_actOid        CHAR (8) FOR BIT DATA;        -- the actual object id
    DECLARE l_count         INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    DECLARE l_rowcount      INT;
  
    -- define cursor:
    DECLARE updateCursor CURSOR WITH HOLD FOR 
    SELECT o.oid 
    FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
    WHERE o.posNoPath LIKE (l_posNoPath || '%')
        AND o.state <> c_STATE_DELETED
        AND o.oid <> ai_oid
        AND r.rKey = o.rKey
        AND (
                (
                    o.owner <> ai_userId
                    AND r.userId = ai_userId
                )
                OR
                (
                    o.owner = ai_userId
                    AND r.userId = c_OWNER
                )
            )
        AND IBSDEV1.b_and(r.rights, ai_op) = ai_op;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_OWNER             = 9437185;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_NOT_ALL           = 31;
    SET c_ParticipantContainer = 16850945;
    SET c_Reference         = 16842801;

    -- initialize local variables:
    SET l_retValue          = c_NOT_OK;
    SET l_rights            = 0;
    SET l_count             = 0;
  
-- body:
    -- get the posNoPath of the root object:
    SET l_sqlcode = 0;
    SELECT posNoPath
    INTO l_posNoPath
    FROM IBSDEV1.ibs_Object
    WHERE oid = ai_oid;

    IF l_sqlcode = 100 THEN 
        SET l_rowcount = 0;
    ELSE 
        SET l_rowcount = 1;
    END IF;

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 OR l_rowcount = 0 THEN 
        GOTO exception1;
    END IF;
    -- check if there are any objects for which the rights cannot be set:
    CALL IBSDEV1.p_Rights$checkRightsRec(l_posNoPath, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL THEN 
        -- open the cursor:
        OPEN updateCursor;
        -- get the first object:
        SET l_sqlcode = 0;
        FETCH FROM updateCursor INTO l_actOid;
        SET l_sqlstatus = l_sqlcode;

        -- loop through all found objects:
        WHILE (l_sqlstatus = 0)         -- another tuple found
        DO
            -- set the rights for the actual object:
            CALL IBSDEV1.p_Rights$setRights(l_actOid, ai_rPersonId, 0, 0);

            -- get next object:
            SET l_sqlcode = 0;
            FETCH FROM updateCursor INTO l_actOid;
            SET l_sqlstatus = l_sqlcode;
        END WHILE; -- another tuple found

        -- close the cursor:
        CLOSE updateCursor;
    END IF;

    -- finish the transaction:
    IF l_retValue <> c_ALL_RIGHT AND l_retValue <> c_NOT_ALL THEN 
        -- roll back to the save point:
        ROLLBACK;
    END IF;

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- roll back to the save point:
    ROLLBACK;
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Rights$deleteUserRightsRec

--------------------------------------------------------------------------------
-- Check if a user has the necessary access rights on an object. <BR>
--
-- @input parameters:
-- @param   ai_oid_s            Oid of the object for which the rights shall be
--                              checked.
-- @param   ai_containerId_s    Container in which the object resides.
--                              This container is used if there are no rights
--                              defined on the object itself.
-- @param   ai_userId           Id of the user for whom the rights shall be
--                              checked.
-- @param   ai_requiredRights   Rights which are required for the user.
--                              This is a bit pattern where each required right
--                              is set to 1.
--
-- @output parameters:
-- @param   ao_hasRights        Contains all of the required rights which are
--                              allowed.
--                              @hasRights == @requiredRights if the user has
--                              all required rights.
-- @return  A value representing the rights or the state of the procedure.
--  = ao_hasRights              No error, the value contains the rights.
--  NOT_OK                      Any error occurred.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Rights$checkRights1');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Rights$checkRights1(
    -- input parameters
    IN    ai_oid_s          VARCHAR (63),
    IN    ai_containerId_s  VARCHAR (63),
    IN    ai_userId         INT,
    IN    ai_requiredRights INT,
    OUT ao_hasRights      INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue          = c_NOT_OK;

-- body:
    -- redirect to basic checkRights procedure:
    CALL IBSDEV1.p_Rights$checkObjectRights(ai_oid_s, ai_containerId_s,
        ai_userId, ai_requiredRights, ao_hasRights);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- return the computed rights:
    RETURN l_retValue;
END;
-- p_Rights$checkRights1
