/******************************************************************************
 * All stored procedures regarding the rights table. <BR>
 *
 * @version     $Revision: 1.1 $, $Date: 2002/12/09 14:01:12 $
 *              $Author: kreimueller $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


/******************************************************************************
 * Recalculate all cumulated rights. <BR>
 *
 * @input parameters:
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$updateRightsCum
AS
    -- constants:

    -- local variables:
    l_count                 INTEGER := 0;   -- counter

BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$updateRightsCum;

    -- delete all cumulated rights:
    DELETE ibs_RightsCum;
    -- truncate is not possible because there is no rollback mechanism
--    EXEC_SQL ('TRUNCATE TABLE ibs_RightsCum');

    -- recalculate the cumulated rights and update the data:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, r.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E)
    FROM    ibs_RightsKeys r,
            (
                SELECT  id AS id, id AS userId
                FROM    ibs_User
                UNION
                SELECT  gu.groupId AS id, gu.userId AS userId
                FROM    ibs_GroupUser gu, ibs_User u
                WHERE   gu.userId = u.id
            ) p
    WHERE   p.id = r.rPersonId
    GROUP BY r.id, p.userId;

    -- insert rights for owner where these rights are not already
    -- set:
    -- 0x00900001 = 9437185   0x7FFFFFFF = 2147483647
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  DISTINCT 9437185, id, 2147483647 -- 0x7FFFFFFF
    FROM    ibs_RightsKeys
    WHERE   id NOT IN
            (SELECT rKey
            FROM    ibs_RightsCum
            WHERE   userId = 9437185    -- 0x00900001
            );

    -- insert rights for owner for rKey = 0 if these rights are not
    -- already set:
    -- (This is necessary because rKey = 0 means that there are no
    -- explicit permissions set. This also means that there are no
    -- rights cumulated for that key. And so not even the owner has
    -- access to objects with this key if there are no rights
    -- cumulated.)
    BEGIN
        SELECT  COUNT (rKey)
        INTO    l_count
        FROM    ibs_RightsCum
        WHERE   userId = 9437185        -- 0x00900001
            AND rKey = 0;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- set default counter value:
            l_count := 0;
    END;

    if (l_count = 0)                    -- no rights for owner with rKey = 0
    THEN
        -- insert the owner rights into the cumulation table:
        INSERT INTO ibs_RightsCum (userId, rKey, rights)
        VALUES (9437185, 0, 2147483647);
--        VALUES (0x00900001, 0, 0x7FFFFFFF)
    END IF; -- no rights for owner with rKey = 0

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$updateRightsCum;
        ibs_error.log_error (ibs_error.error, 'p_Rights$updateRightsCum',
            'sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$updateRightsCum;
/

show errors;

/******************************************************************************
 * Recalculate the cumulated rights for the users within a specific group. <BR>
 * The rights are recalculated for all users which are directly and indirectly
 * (recursively) within the specified group.
 *
 * @input parameters:
 * @param   ai_groupId          Id of the group for whose users the rights
 *                              shall be cumulated.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$updateRightsCumGroup
(
    ai_groupId              INTEGER
)
AS
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$updateRightsCumGroup;

    -- delete all cumulated rights for the users within the requested group:
    DELETE  ibs_RightsCum
    WHERE   userId IN
            (SELECT userId
            FROM    ibs_GroupUser
            WHERE   groupId = ai_groupId);

    -- recalculate the cumulated rights for all users who are in the regarded
    -- group and update the data:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, r.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E)
    FROM    ibs_RightsKeys r,
            (
                -- get the users who are in the regarded groups and compute
                -- for each of them all groups where (s)he is contained
                -- (recursively):
                SELECT  id AS id, id AS userId
                FROM    ibs_User
                WHERE   id IN
                        (
                            SELECT  userId
                            FROM    ibs_GroupUser
                            WHERE   groupId = ai_groupId
                        )
                UNION
                SELECT  groupId AS id, userId AS userId
                FROM    ibs_GroupUser
                WHERE   userId IN
                        (
                            SELECT  userId
                            FROM    ibs_GroupUser
                            WHERE   groupId = ai_groupId
                        )
            ) p
    WHERE   p.id = r.rPersonId
    GROUP BY r.id, p.userId;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$updateRightsCumGroup;
        ibs_error.log_error (ibs_error.error, 'p_Rights$updateRightsCumGroup',
            'Input: ai_groupId = ' || ai_groupId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$updateRightsCumGroup;
/

show errors;

/******************************************************************************
 * Recalculate the cumulated rights for a specific user. <BR>
 *
 * @input parameters:
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              cumulated.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$updateRightsCumUser
(
    ai_userId               INTEGER
)
AS
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$updateRightsCumUser;

    -- delete all cumulated rights for the requested user:
    DELETE  ibs_RightsCum
    WHERE   userId = ai_userId;

    -- recalculate the cumulated rights and update the data:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, r.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E)
    FROM    ibs_RightsKeys r,
            (
                SELECT  id AS id, id AS userId
                FROM    ibs_User
                WHERE   id = ai_userId
                UNION
                SELECT  groupId AS id, userId AS userId
                FROM    ibs_GroupUser
                WHERE   userId = ai_userId
            ) p
    WHERE   p.id = r.rPersonId
        AND p.userId = ai_userId
    GROUP BY r.id, p.userId;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$updateRightsCumUser;
        ibs_error.log_error (ibs_error.error, 'p_Rights$updateRightsCumUser',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$updateRightsCumUser;
/

show errors;

/******************************************************************************
 * Recalculate the cumulated rights for a specific key. This procedure is
 * only used in trigger "TrigRightsKeysInsert" on table "ibs_RightsKeys".<BR>
 *
 * @input parameters:
 * @param   ai_rightsKeysId     Id of the key for which the rights shall be
 *                              cumulated.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$updateRightsCumKey
(
    ai_rightsKeysId        INTEGER
)
AS
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$updateRightsCumKey;

    -- drop all cumulated rights already derived for the actual key:
    DELETE  ibs_RightsCum
    WHERE   rKey = ai_rightsKeysId;

    -- recalculate the cumulated rights for given key and insert the data
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
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
                FROM    ibs_RightsKeys
                WHERE   id = ai_rightsKeysId
            ) r,
            (
                SELECT  id AS id, id AS userId
                FROM    ibs_User
                UNION
                SELECT  gu.groupId AS id, gu.userId AS userId
                FROM    ibs_GroupUser gu, ibs_User u
                WHERE   gu.userId = u.id
            ) p
    WHERE p.id = r.rPersonId
    GROUP BY r.id, p.userId;

    -- insert rights for owner where these rights are not already set:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  DISTINCT 9437185, id, 2147483647 -- 0x00900001, id, 0x7FFFFFFF
    FROM    ibs_RightsKeys
    WHERE   id = ai_rightsKeysId
        AND id NOT IN
            (SELECT rKey
            FROM    ibs_RightsCum
            WHERE   userId = 9437185    -- 0x00900001
                AND rkey = ai_rightsKeysId);

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$updateRightsCumKey;
        ibs_error.log_error (ibs_error.error, 'p_Rights$updateRightsCumKey',
            'Input: ai_rightsKeysId = ' || ai_rightsKeysId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$updateRightsCumKey;
/

show errors;


/******************************************************************************
 * Delete the rights for a specific user. <BR>
 *
 * @input parameters:
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              deleted.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$deleteAllUserRights
(
    ai_userId               INTEGER
)
AS
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$deleteAllUserRights;

    -- set the number of actual values within the keys where :
    UPDATE  ibs_RightsKeys
    SET     cnt = cnt - 1
    WHERE   id IN
            (SELECT DISTINCT id
            FROM    ibs_RightsKeys
            WHERE   rPersonId = ai_userId);

    -- delete all entries for the requested user within the keys:
    DELETE  ibs_RightsKeys
    WHERE   rPersonId = ai_userId;

    -- delete all cumulated rights for the requested user:
    DELETE  ibs_RightsCum
    WHERE   userId = ai_userId;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$deleteAllUserRights;
        ibs_error.log_error (ibs_error.error, 'p_Rights$deleteAllUserRights',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$deleteAllUserRights;
/

show errors;

/******************************************************************************
 * Check if a user has the necessary access rights on an object. <BR>
 *
 * @input parameters:
 * @param   ai_oid              Oid of the object for which the rights shall be
 *                              checked.
 * @param   ai_containerId      Container in which the object resides.
 *                              This container is used if there are no rights
 *                              defined on the object itself.
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              checked.
 * @param   ai_requiredRights   Rights which are required for the user.
 *                              This is a bit pattern where each required right
 *                              is set to 1.
 *
 * @output parameters:
 * @param   ao_hasRights        Contains all of the required rights which are
 *                              allowed.
 *                              ao_asRights == ai_requiredRights if the user has
 *                              all required rights.
 * @return  A value representing the rights or the state of the procedure.
 *  = ao_hasRights              No error, the value contains the rights.
 *  NOT_OK                      Any error occurred.
 */
CREATE OR REPLACE FUNCTION p_Rights$checkRights
(
    -- input parameters:
    ai_oid                  RAW,
    ai_containerId          RAW,
    ai_userId               INTEGER,
    ai_requiredRights       INTEGER,
    -- output parameters:
    ao_hasRights            OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_OWNER                 CONSTANT INTEGER := 9437185; -- 0x00900001
                                            -- user id of owner
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.

    -- local variables:
--    l_retVal                INTEGER := 0;
    l_rKey                  INTEGER := -1;
    l_count                 INTEGER := 0;
    l_actUserId             INTEGER := ai_userId; -- actual user id

BEGIN
    -- no rights allowed thus far:
    ao_hasRights := 0;

    -- check if the actual user is the owner of the required object:
    IF (ai_oid = c_NOOID)
    THEN
        SELECT  COUNT (id)
        INTO    l_count
        FROM    ibs_Object
        WHERE   oid = ai_containerId
        AND     owner = ai_userId;
    ELSE
        SELECT  COUNT (id)
        INTO    l_count
        FROM    ibs_Object
        WHERE   oid = ai_oid
        AND     owner = ai_userId;
    END IF;

    IF (l_count > 0)                    -- the actual user is the owner?
    THEN
        -- set the user id:
        l_actUserId := c_OWNER;         -- user id of owner
    END IF; -- the actual user is the owner

    -- get the rights key of the actual object:
    BEGIN
        SELECT  rKey
        INTO    l_rKey
        FROM    ibs_Object
        WHERE   oid = ai_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN     -- object not found => use container
            -- get rights key of the container:
            -- (if there is no key defined for the container return with the
            -- actual rights result)
            BEGIN
                SELECT  rKey
                INTO    l_rKey
                FROM    ibs_Object
                WHERE   oid = ai_containerId;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    RETURN ao_hasRights;
            END;
        -- end when object not found => use container
    END;

    -- compute the rights:
    -- (if the key is not found return with the actual rights result)
    BEGIN
        SELECT  B_AND (rights, ai_requiredRights)
        INTO    ao_hasRights
        FROM    ibs_RightsCum r
        WHERE   rKey = l_rKey
            AND userId = l_actUserId;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN ao_hasRights;
    END;

    -- return the computed rights:
    RETURN ao_hasRights;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$checkRights',
            'Input: ai_oid = ' || ai_oid ||
            ', ai_containerId = ' || ai_containerId ||
            ', ai_userId = ' || ai_userId ||
            ', ai_requiredRights = ' || ai_requiredRights ||
            ', ao_hasRights = ' || ao_hasRights ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$checkRights;
/

show errors;


/******************************************************************************
 * Check if a user has the necessary access rights on an object and its sub
 * objects. <BR>
 *
 * @input parameters:
 * @param   ai_oid              Oid of the object for which the rights shall be
 *                              checked.
 * @param   ai_containerId      Container in which the object resides.
 *                              This container is used if there are no rights
 *                              defined on the object itself.
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              checked.
 * @param   ai_requiredRights   Rights which are required for the user.
 *                              This is a bit pattern where each required right
 *                              is set to 1.
 *
 * @output parameters:
 * @param   ao_hasRights        Contains all of the required rights which are
 *                              allowed.
 *                              ao_hasRights == ai_requiredRights if the user
 *                              has all required rights.
 * @return  A value representing the state of the procedure.
 *  NOT_OK                      Any error occurred.
 *  ALL_RIGHT                   Action may be performed, everything ok.
 *  NOT_ALL                     The user does not have the rights on all
 *                              objects.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Rights$checkRightsRec
(
    -- input parameters:
    ai_posNoPath            VARCHAR2,
    ai_userId               INTEGER,
    ai_requiredRights       INTEGER
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_OWNER                 CONSTANT INTEGER := 9437185; -- 0x00900001
                                            -- user id of owner
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_NOT_ALL               CONSTANT INTEGER := 31; -- operation could not be
                                            -- performed for all objects
    c_STATE_DELETED         CONSTANT INTEGER := 1; -- the object was deleted
    c_STATE_ACTIVE          CONSTANT INTEGER := 2; -- active state of object

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this function
    l_count                 INTEGER := 0;   -- counter for regarded objects
    l_countSuff             INTEGER := 0;   -- counter for objects with
                                            -- sufficient rights
    l_countSuff1            INTEGER := 0;   -- help counter 1
    l_countSuff2            INTEGER := 0;   -- help counter 1
BEGIN
    -- get the number of regarded objects:
    BEGIN
        SELECT  COUNT (oid)
        INTO    l_count
        FROM    ibs_Object
        WHERE   posNoPath LIKE ai_posNoPath || '%'
            AND state <> c_STATE_DELETED;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN     -- nothing found
            -- reinitialize the variable:
            l_count := 0;
        WHEN OTHERS THEN            -- another error
            ibs_error.log_error
                (ibs_error.error, 'p_Rights$checkRightsRec.get l_count',
                'OTHER error for posNoPath ' || ai_posNoPath);
            RAISE;
    END;

    -- get number of objects for which the user has sufficient rights:
    BEGIN
--------------------------
--------------------------
--------------------------
--
-- HP - Performance Tuning: split query in 2 parts
--
/*
        SELECT  COUNT (o.oid)
        INTO    l_countSuff
        FROM    ibs_Object o, ibs_RightsCum r
        WHERE   o.posNoPath LIKE ai_posNoPath || '%'
            AND o.state <> c_STATE_DELETED
            AND r.rKey = o.rKey
            AND (
                    (o.owner <> ai_userId
                    AND r.userId = ai_userId)
                OR
                    (o.owner = ai_userId
                    AND r.userId = c_OWNER)
                )
            AND B_AND (r.rights, ai_requiredRights) = ai_requiredRights;
*/
        -- query split in 2 part!
        -- userid <> owner
        SELECT  COUNT (o.oid)
        INTO    l_countSuff1
        FROM    ibs_Object o, ibs_RightsCum r
        WHERE   o.posNoPath LIKE ai_posNoPath || '%'
            AND o.state <> c_STATE_DELETED
            AND r.rKey = o.rKey
            AND r.userId = ai_userId
            AND o.owner <> ai_userId
            AND B_AND (r.rights, ai_requiredRights) = ai_requiredRights;

        SELECT  COUNT (o.oid)
        INTO    l_countSuff2
        FROM    ibs_Object o, ibs_RightsCum r
        WHERE   o.posNoPath LIKE ai_posNoPath || '%'
            AND o.state <> c_STATE_DELETED
            AND r.rKey = o.rKey
            AND r.userId = c_OWNER
            AND o.owner  = ai_userId
            AND B_AND (r.rights, ai_requiredRights) = ai_requiredRights;

        -- add results
        l_countSuff := l_countSuff1 + l_countSuff2;
--
-- HP - Tuning End
--
--------------------------
--------------------------
--------------------------
    EXCEPTION
        WHEN NO_DATA_FOUND THEN     -- nothing found
            -- reinitialize the variable:
            l_countSuff := 0;
        WHEN OTHERS THEN            -- another error
            ibs_error.log_error
                (ibs_error.error, 'p_Rights$checkRightsRec.get l_countSuff',
                'OTHER error for posNoPath ' || ai_posNoPath);
            RAISE;
    END;

    -- check if there are any objects for which the user has insufficient
    -- rights:
    IF (l_count <> l_countSuff)     -- at least one object for which
                                    -- the user has insufficient rights?
    THEN
        IF (l_countSuff = 0)        -- no object for which the user has
                                    -- sufficient rights?
        THEN
            l_retValue := c_INSUFFICIENT_RIGHTS;
        ELSE                        -- the user has sufficient rights for at
                                    -- least one object
            l_retValue := c_NOT_ALL;
        END IF; -- if no object ...
    END IF; -- at least one object ...

    -- return the computed state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$checkRightsRec',
            'Input: ai_posNoPath = ' || ai_posNoPath ||
            ', ai_userId = ' || ai_userId ||
            ', ai_requiredRights = ' || ai_requiredRights ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$checkRightsRec;
/

show errors;


/******************************************************************************
 * Check if a user has the necessary access rights on an object and its sub
 * objects. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            Oid of the object for which the rights shall be
 *                              checked.
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              checked.
 * @param   ai_requiredRights   Rights which are required for the user.
 *                              This is a bit pattern where each required right
 *                              is set to 1.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_NOT_OK                     Any error occurred.
 * c_ALL_RIGHT                  Check performed, values returned, evrythng ok.
 * c_NOT_ALL                    The user does not have the rights on all
 *                              objects.
 * c_INSUFFICIENT_RIGHTS        User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Rights$checkUserRightsRec
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_requiredRights       ibs_RightsKeys.rights%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOT_ALL               CONSTANT INTEGER := 31; -- operation could not be
                                            -- performed for all objects

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_oid                   ibs_Object.oid%TYPE; -- the oid of the object
    l_posNoPath             ibs_Object.posNoPath%TYPE;
                                            -- pos no path of the object

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_oid_s, l_oid);

    -- get the posNoPath of the actual object:
    BEGIN
        SELECT  posNoPath
        INTO    l_posNoPath
        FROM    ibs_Object
        WHERE   oid = l_oid;
    EXCEPTION
        -- end when the tab object was not found
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get posNoPath';
            RAISE;                      -- call common exception handler
    END;

    -- check if there are any objects for which the rights cannot be set:
    l_retValue :=
        p_Rights$checkRightsRec (l_posNoPath, ai_userId, ai_requiredRights);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_requiredRights = ' || ai_requiredRights ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Rights$checkUserRightsRec', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Rights$checkUserRightsRec;
/

show errors;


/******************************************************************************
 * Check if a user has the necessary access rights on an object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            Oid of the object for which the rights shall be
 *                              checked.
 * @param   ai_containerId_s    Container in which the object resides.
 *                              This container is used if there are no rights
 *                              defined on the object itself.
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              checked.
 * @param   ai_requiredRights   Rights which are required for the user.
 *                              This is a bit pattern where each required right
 *                              is set to 1.
 *
 * @output parameters:
 * @param   ao_hasRights        Contains all of the required rights which are
 *                              allowed.
 *                              ao_hasRights == ai_requiredRights if the user
 *                              all required rights.
 * @return  A value representing the state of the procedure.
 *  NOT_OK                      Any error occurred.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Rights$checkObjectRights
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_userId               INTEGER,
    ai_requiredRights       INTEGER,
    -- output parameters:
    ao_hasRights            OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.

    -- local variables:
    l_oid                   RAW (8);        -- oid of the object
    l_containerId           RAW (8);        -- oid of the container

BEGIN
    -- conversions (OBJECTIDSTRING)
    -- all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);
    p_stringToByte (ai_containerId_s, l_containerId);

    -- call basic checkRights procedure:
    ao_hasRights := p_Rights$checkRights
        (l_oid, l_containerId, ai_userId, ai_requiredRights,
        ao_hasRights);

    -- return the computed rights:
    RETURN ao_hasRights;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$CheckObjectRights',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_containerId_s = ' || ai_containerId_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_requiredRights = ' || ai_requiredRights ||
            ', ao_hasRights = ' || ao_hasRights ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$checkObjectRights;
/

show errors;


/******************************************************************************
 * Get the rights of a rights person (user, group, ...) on a specific object.
 * <BR>
 *
 * @input parameters:
 * @param   ai_rOid             Oid of the object on which rights shall be
 *                              defined.
 * @param   ai_rPersonId        Id of the rights person (user, group, ...)
 *
 * @output parameters:
 * @param   ao_rights           Rights of the person on the object.
 */
CREATE OR REPLACE PROCEDURE p_Rights$getRights
(
    -- input parameters:
    ai_rOid                 RAW,
    ai_rPersonId            INTEGER,
    -- output parameters:
    ao_rights               OUT INTEGER
)
AS
    -- constants:
    c_RIGHTS_ALL            CONSTANT INTEGER := 2147483647; -- 0x7FFFFFFF
                                            -- all rights together

    -- local variables:
    l_containerId           RAW (8);        -- oid of the container

BEGIN
    -- initialize return values:
    ao_rights := 0;

    -- get container oid:
    SELECT  containerId
    INTO    l_containerId
    FROM    ibs_Object
    WHERE   oid = ai_rOid;

    -- get rights for this user:
    ao_rights := p_Rights$checkRights (
        ai_rOid, l_containerId, ai_rPersonId, c_RIGHTS_ALL,
        ao_rights);

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$getRights',
            'Input: ai_rOid = ' || ai_rOid ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ao_rights = ' || ao_rights ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$getRights;
/

show errors;


/******************************************************************************
 * Get the rights of a rights person (user, group, ...) on a specific object
 * and its container. <BR>
 *
 * @input parameters:
 * @param   ai_rOid_s           Oid of the object on which rights shall be
 *                              defined.
 * @param   ai_rPersonId        Id of the rights person (user, group, ...)
 *
 * @output parameters:
 * @param   ao_objectRights     Rights of the person on the object.
 * @param   ao_containerRights  Rights of the person on the container of the
 *                              object.
 * @param   ao_isContainer      Tells if the object with rOid_s is a container.
 */
CREATE OR REPLACE PROCEDURE p_Rights$getRightsContainer
(
    -- input parameters:
    ai_rOid_s               VARCHAR2,
    ai_rPersonId            INTEGER,
    -- output parameters:
    ao_objectRights         OUT INTEGER,
    ao_containerRights      OUT INTEGER,
    ao_isContainer          OUT NUMBER
)
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_rOid                  RAW (8);
    l_containerId           RAW (8);

BEGIN
    -- initialize return values:
    ao_objectRights := 0;
    ao_containerRights := 0;
    ao_isContainer := 0;

    -- conversions (objectidstring) - all input objectids must be converted
    -- convert string representation to binary representation:
    p_stringToByte (ai_rOid_s, l_rOid);

    -- get container oid:
    SELECT  containerId, isContainer
    INTO    l_containerId, ao_isContainer
    FROM    ibs_Object
    WHERE   oid = l_rOid;

    -- root object is its own container:
    IF (l_containerId = c_NOOID)        -- object has no container?
    THEN
        l_containerId := l_rOid;        -- set container = object
    END IF;
    -- get rights for user on object:
    p_Rights$getRights (l_rOid, ai_rPersonId, ao_objectRights);
    -- get rights for user on container:
    p_Rights$getRights (l_containerId, ai_rPersonId, ao_containerRights);

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$getRightsContainer',
            'Input: ai_rOid_s = ' || ai_rOid_s ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ao_objectRights = ' || ao_objectRights ||
            ', ao_containerRights = ' || ao_containerRights ||
            ', ao_isContainer = ' || ao_isContainer ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$getRightsContainer;
/

show errors;


/******************************************************************************
 * Get a key being similar to an actual key. <BR>
 * The key returned is equal to the actual key in all parts except one which
 * is explicitly given.
 * If there does not exist a corresponding key it is automatically created.
 *
 * @input parameters:
 * @param   ai_actId            Id of actual key for which to find a similar
 *                              one.
 * @param   ai_rPersonId        Id of the rights person (user, group, ...)
 * @param   ai_rights           New rights.
 *
 * @output parameters:
 * @param   ao_id               The id of the found key.
 *                              0 ..... there are no rights left, i.e. the new
 *                                      key is empty.
 *                              -1 .... no key was found and none could be
 *                                      generated.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                   Action performed, values returned, evrythng ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
CREATE OR REPLACE PROCEDURE p_Rights$getKey
(
    -- input parameters:
    ai_actId                INTEGER,
    ai_rPersonId            INTEGER,
    ai_rights               INTEGER,
    -- output parameters:
    ao_id                   OUT INTEGER
)
AS
    -- local variables:
    l_cnt                   INTEGER := 0;   -- counter

BEGIN
    -- initialize return values:
    ao_id := -1;

    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$getKey;

    BEGIN
        -- get old number of rights except those for the required person:
        SELECT  COUNT (*)
        INTO    l_cnt
        FROM    ibs_RightsKeys
        WHERE   id = ai_actId
            AND rPersonId <> ai_rPersonId;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- no data found?
            l_cnt := 0;                 -- ensure correct counter value
        WHEN OTHERS THEN                -- any other error?
            RAISE;                      -- raise to next error handler
    END;

    -- check if there will be some rights left:
    IF (l_cnt = 0 AND ai_rights <= 0)   -- there will be no rights left?
    THEN
        ao_id := 0;                     -- set the new rights key
        RETURN;                         -- terminate procedure
    END IF; -- if there will be no rights left

    IF (ai_rights > 0)                  -- there shall be some rights set?
    THEN
        l_cnt := l_cnt + 1;             -- the new right must also be counted
        BEGIN
            -- check if there exists already a key for the new rights and set
            -- return value:
            SELECT  MIN (id)
            INTO    ao_id
            FROM
            (
                SELECT COUNT (*) AS cnt, rkc.id
                FROM    ibs_RightsKeys rkc,
                        (
                            SELECT  rPersonId, rights
                            FROM    ibs_RightsKeys
                            WHERE   id = ai_actId
                                AND rPersonId <> ai_rPersonId
                            UNION
                            SELECT  ai_rPersonId AS rPersonId,
                                    ai_rights AS rights
                            FROM    sys.dual
                        ) r
                WHERE   rkc.cnt = l_cnt
                    AND r.rPersonId = rkc.rPersonId
                    AND r.rights = rkc.rights
                GROUP BY rkc.id
            ) res
            WHERE   cnt = l_cnt;

            IF (ao_id IS NULL)          -- no id found?
            THEN
                ao_id := -1;            -- set corresponding return value
            END IF; -- no id found
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- no id found?
                ao_id := -1;            -- set corresponding return value
            WHEN OTHERS THEN            -- any other error?
                RAISE;                  -- raise to next error handler
        END;
    ELSE                                -- don't set rights
        -- check if there exists already a key for the new rights and set
        -- return value:
        BEGIN
            SELECT  MIN (id)
            INTO    ao_id
            FROM
            (
                SELECT  COUNT (rkc.id) AS cnt, rkc.id
                FROM    ibs_RightsKeys rkc, ibs_rightsKeys r
                WHERE   rkc.cnt = l_cnt
                    AND r.id = ai_actId
                    AND r.rPersonId <> ai_rPersonId
                    AND r.rPersonId = rkc.rPersonId
                    AND r.rights = rkc.rights
                GROUP BY rkc.id
            ) res
            WHERE   cnt = l_cnt;

            IF (ao_id IS NULL)          -- no id found?
            THEN
                ao_id := -1;            -- set corresponding return value
            END IF; -- no id found
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- no id found?
                ao_id := -1;            -- set corresponding return value
            WHEN OTHERS THEN            -- any other error?
                RAISE;                  -- raise to next error handler
        END;
    END IF; -- else don't set rights

    IF (ao_id = -1)                     -- no corresponding key found?
    THEN
        -- create a new rights key:
        IF (ai_rights > 0)              -- there shall be some rights set?
        THEN
            BEGIN
                INSERT INTO ibs_RightsKeys (id, rPersonId, rights, cnt,
                        r00, r01, r02, r03, r04, r05, r06, r07,
                        r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                        r10, r11, r12, r13, r14, r15, r16, r17,
                        r18, r19, r1A, r1B, r1C, r1D, r1E)
                (
                    SELECT  -1, rPersonId, rights, l_cnt,
                            B_AND (rights,          1),
                            B_AND (rights,          2),
                            B_AND (rights,          4),
                            B_AND (rights,          8),
                            B_AND (rights,         16),
                            B_AND (rights,         32),
                            B_AND (rights,         64),
                            B_AND (rights,        128),
                            B_AND (rights,        256),
                            B_AND (rights,        512),
                            B_AND (rights,       1024),
                            B_AND (rights,       2048),
                            B_AND (rights,       4096),
                            B_AND (rights,       8192),
                            B_AND (rights,      16384),
                            B_AND (rights,      32768),
                            B_AND (rights,      65536),
                            B_AND (rights,     131072),
                            B_AND (rights,     262144),
                            B_AND (rights,     524288),
                            B_AND (rights,    1048576),
                            B_AND (rights,    2097152),
                            B_AND (rights,    4194304),
                            B_AND (rights,    8388608),
                            B_AND (rights,   16777216),
                            B_AND (rights,   33554432),
                            B_AND (rights,   67108864),
                            B_AND (rights,  134217728),
                            B_AND (rights,  268435456),
                            B_AND (rights,  536870912),
                            B_AND (rights, 1073741824)
                    FROM    ibs_RightsKeys
                    WHERE   id = ai_actId
                        AND rPersonId <> ai_rPersonId
                    UNION
                    SELECT  -1, ai_rPersonId, ai_rights, l_cnt,
                            B_AND (ai_rights,          1),
                            B_AND (ai_rights,          2),
                            B_AND (ai_rights,          4),
                            B_AND (ai_rights,          8),
                            B_AND (ai_rights,         16),
                            B_AND (ai_rights,         32),
                            B_AND (ai_rights,         64),
                            B_AND (ai_rights,        128),
                            B_AND (ai_rights,        256),
                            B_AND (ai_rights,        512),
                            B_AND (ai_rights,       1024),
                            B_AND (ai_rights,       2048),
                            B_AND (ai_rights,       4096),
                            B_AND (ai_rights,       8192),
                            B_AND (ai_rights,      16384),
                            B_AND (ai_rights,      32768),
                            B_AND (ai_rights,      65536),
                            B_AND (ai_rights,     131072),
                            B_AND (ai_rights,     262144),
                            B_AND (ai_rights,     524288),
                            B_AND (ai_rights,    1048576),
                            B_AND (ai_rights,    2097152),
                            B_AND (ai_rights,    4194304),
                            B_AND (ai_rights,    8388608),
                            B_AND (ai_rights,   16777216),
                            B_AND (ai_rights,   33554432),
                            B_AND (ai_rights,   67108864),
                            B_AND (ai_rights,  134217728),
                            B_AND (ai_rights,  268435456),
                            B_AND (ai_rights,  536870912),
                            B_AND (ai_rights, 1073741824)
                   FROM sys.dual
                );
            EXCEPTION
                WHEN OTHERS THEN        -- any other error?
                    ibs_error.log_error
                        (ibs_error.error, 'p_Rights$getKey.insert1',
                        'OTHER error for ai_actId ' || ai_actId ||
                        ' and ai_rPersonId ' || ai_rPersonId);
                    RAISE;              -- raise to next error handler
            END;
        ELSE                            -- don't set rights for actual person
            BEGIN
                INSERT INTO ibs_RightsKeys (id, rPersonId, rights, cnt,
                        r00, r01, r02, r03, r04, r05, r06, r07,
                        r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                        r10, r11, r12, r13, r14, r15, r16, r17,
                        r18, r19, r1A, r1B, r1C, r1D, r1E)
                SELECT  -1, rPersonId, rights, l_cnt,
                        B_AND (rights,          1),
                        B_AND (rights,          2),
                        B_AND (rights,          4),
                        B_AND (rights,          8),
                        B_AND (rights,         16),
                        B_AND (rights,         32),
                        B_AND (rights,         64),
                        B_AND (rights,        128),
                        B_AND (rights,        256),
                        B_AND (rights,        512),
                        B_AND (rights,       1024),
                        B_AND (rights,       2048),
                        B_AND (rights,       4096),
                        B_AND (rights,       8192),
                        B_AND (rights,      16384),
                        B_AND (rights,      32768),
                        B_AND (rights,      65536),
                        B_AND (rights,     131072),
                        B_AND (rights,     262144),
                        B_AND (rights,     524288),
                        B_AND (rights,    1048576),
                        B_AND (rights,    2097152),
                        B_AND (rights,    4194304),
                        B_AND (rights,    8388608),
                        B_AND (rights,   16777216),
                        B_AND (rights,   33554432),
                        B_AND (rights,   67108864),
                        B_AND (rights,  134217728),
                        B_AND (rights,  268435456),
                        B_AND (rights,  536870912),
                        B_AND (rights, 1073741824)
                FROM    ibs_RightsKeys
                WHERE   id = ai_actId
                    AND rPersonId <> ai_rPersonId;
            EXCEPTION
                WHEN OTHERS THEN        -- any other error?
                    ibs_error.log_error
                        (ibs_error.error, 'p_Rights$getKey.insert2',
                        'OTHER error for ai_actId ' || ai_actId ||
                        ' and ai_rPersonId ' || ai_rPersonId);
                    RAISE;              -- raise to next error handler
            END;
        END IF;-- else don't set rights for actual person

        -- get the id of the newly generated key:
        SELECT  MAX (id)
        INTO    ao_id
        FROM    ibs_RightsKeys;
    END IF; -- if no corresponding key found

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$getKey;
        ibs_error.log_error (ibs_error.error, 'p_Rights$getKey',
            'Input: ai_actId = ' || ai_actId ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ai_rights = ' || ai_rights ||
            ', ao_id = ' || ao_id ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- set return value:
        ao_id := -1;
END p_Rights$getKey;
/

show errors;


/******************************************************************************
 * Get the actual rights key of an object. <BR>
 *
 * @input parameters:
 * @param   ai_oid              The oid of the object for which the rights key
 *                              shall be computed.
 *
 * @output parameters:
 * @return  The id of the rights key which is set for the object or -1 if there
 *          was no key found.
 */
CREATE OR REPLACE FUNCTION p_Rights$getRightsKey
(
    -- input parameters:
    ai_oid                  RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_FOUND             CONSTANT INTEGER := -1; -- key not found

    -- local variables:
    l_rKey                  INTEGER := c_NOT_FOUND; -- the found key

BEGIN
    -- get the rights key of the object:
    BEGIN
        SELECT  rKey
        INTO    l_rKey
        FROM    ibs_Object
        WHERE   oid = ai_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- no rights key found?
            l_rKey := c_NOT_FOUND;      -- set corresponding return value
        WHEN OTHERS THEN                -- any other error?
            RAISE;                      -- raise to next error handler
    END;

    -- return the computed value:
    RETURN  l_rKey;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$getRightsKey',
            'Input: ai_oid = ' || ai_oid ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
--        err;
        -- return error value:
        RETURN c_NOT_FOUND;
END p_Rights$getRightsKey;
/

show errors;


/******************************************************************************
 * Check if a specific rights key is used by at least one object. <BR>
 * If it is not used by any object, the key is deleted.
 * The replacement key reuses the old key. It is converted to the old id and
 * all business objects' rKey attributes having the value of the replacement
 * id are changed to the deleted id accordingly.
 *
 * @input parameters:
 * @param   ai_id               The id of the key which shall be checked for
 *                              being used.
 * @param   ai_replId           The id of the key which shall replace the first
 *                              one.
 *
 * @output parameters:
 * @param   ao_newId            The new id by which ai_replId has been replaced.
 *                              This value can be ai_replId itself if unchanged
 *                              or ai_id otherwise.
 */
CREATE OR REPLACE PROCEDURE p_Rights$reuseKey
(
    -- input parameters:
    ai_id                   INTEGER,
    ai_replId               INTEGER,
    -- output parameters:
    ao_newId                OUT INTEGER
)
AS
    -- constants:

    -- local variables:
    l_count                 INTEGER := 0;
    l_mcount                INTEGER := 0;

BEGIN
    -- initialize return values:
    ao_newId := ai_replId;

    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$reuseKey;

    -- check if the old key is not used by another object and is not a business
    -- object itself:
    IF (ai_id > 0)                      -- the old id is valid?
    THEN
        -- count how many objects use this key:
        SELECT  COUNT (*)
        INTO    l_count
        FROM    ibs_Object
        WHERE   rKey = ai_id;

        -- check if the rights key is not an object itself:
        -- (a value > 0 says that it is an object)
        SELECT  COUNT (o.oid)
        INTO    l_mcount
        FROM    ibs_RightsKeys r, ibs_Object o
        WHERE   rKey = ai_id
            AND r.oid = o.oid;

        IF (l_count = 0) AND (l_mcount <= 0)
                                        -- the key is not longer used and is
                                        -- not an object?
        THEN
            -- delete the old key:
            DELETE  ibs_RightsKeys
            WHERE   id = ai_id;
            -- delete all cumulated rights for this key:
            DELETE  ibs_RightsCum
            WHERE   rKey = ai_id;

            -- reuse the old id for the new key:
            UPDATE  ibs_RightsKeys
            SET     id = ai_id
            WHERE   id = ai_replId;
            -- set the key within the cumulated rights:
            UPDATE  ibs_RightsCum
            SET     rKey = ai_id
            WHERE   rKey = ai_replId;
            -- store the key id within all objects which use this key:
            UPDATE  ibs_Object
            SET     rKey = ai_id
            WHERE   rKey = ai_replId;
            -- set the id:
            ao_newId := ai_id;
        END IF; -- if the key is not used and is not an object
    END IF; -- if the old id was valid

    COMMIT WORK;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$reuseKey;
        -- set return value:
        ibs_error.log_error (ibs_error.error, 'p_Rights$reuseKey',
            'Input: ai_id = ' || ai_id ||
            ', ai_replId = ' || ai_replId ||
            ', ao_newId = ' || ao_newId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- set output value:
        ao_newId := ai_replId;
END p_Rights$reuseKey;
/

show errors;


/******************************************************************************
 * Set the rights key for a specific object and all of its tabs. <BR>
 * The rights keys of the references to the changed object are changed, too.
 *
 * @input parameters:
 * @param   ai_rOid             Oid of the object for which to set the rKey.
 * @param   ai_rKey             Id of the rights key.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  NOT_OK                  Something went wrong during performing the
 *                          operation.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
CREATE OR REPLACE FUNCTION p_Rights$setKey
(
    -- input parameters:
    ai_rOid                 RAW,
    ai_rKey                 INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_STATE_DELETED         CONSTANT INTEGER := 1; -- the object was deleted
    c_STATE_ACTIVE          CONSTANT INTEGER := 2; -- active state of object
    c_ParticipantContainer  CONSTANT INTEGER := 16850945; -- 0x01012001
                                            -- tVersionId of
                                            -- ParticipantContainer
    c_Reference             CONSTANT INTEGER := 16842801; -- 0x01010031
                                            -- tVersionId of Reference

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this function

BEGIN
-- body:
    -- set rights key of the actual object and its tabs:
    BEGIN
        UPDATE  ibs_Object
        SET     rKey = ai_rKey
        WHERE   oid = ai_rOid
            OR  (containerId = ai_rOid
                AND containerKind = 2
--! HACK ...
                -- don't propagate rights to some specific object types:
                AND tVersionId <> c_ParticipantContainer
                AND tVersionId <> c_Reference
--! ... HACK
                )
            OR  (linkedObjectId IN
                    (SELECT oid
                    FROM    ibs_Object
                    WHERE   oid = ai_rOid
                        OR  (containerId = ai_rOid
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
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            ibs_error.log_error (ibs_error.error, 'p_Rights$setKey.update',
            'OTHER error for ai_rOid ' || ai_rOid ||
            ' and ai_rKey ' || ai_rKey);
            l_retValue := c_NOT_OK;     -- set corresponding return value
    END;

    -- return the computed state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$setKey',
            'Input: ai_rOid = ' || ai_rOid ||
            ', ai_rKey = ' || ai_rKey ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$setKey;
/

show errors;


/******************************************************************************
 * Set the rights key for a specific object and all of its sub objects. <BR>
 * The rights keys of the references to the changed object are changed, too.
 *
 * @input parameters:
 * @param   ai_rOid             Oid of the object for which to set the rKey.
 * @param   ai_oldRKey          Old id of the rights key.
 * @param   ai_rKey             Id of the rights key.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  NOT_OK                  Something went wrong during performing the
 *                          operation.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Rights$setKeyRec
(
    -- input parameters:
    ai_rOid                 RAW,
    ai_oldRKey              INTEGER,
    ai_rKey                 INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_STATE_DELETED         CONSTANT INTEGER := 1; -- the object was deleted
    c_STATE_ACTIVE          CONSTANT INTEGER := 2; -- active state of object
    c_EMPTYPOSNOPATH        CONSTANT VARCHAR2 (254) := '0000';
                                            -- default/invalid posNoPath
    c_ParticipantContainer  CONSTANT INTEGER := 16850945; -- 0x01012001
                                            -- tVersionId of
                                            -- ParticipantContainer
    c_Reference             CONSTANT INTEGER := 16842801; -- 0x01010031
                                            -- tVersionId of Reference

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this function
    l_posNoPath             VARCHAR2 (254) := c_EMPTYPOSNOPATH;
                                            -- the pos no path of the object

BEGIN
-- body:
    -- get posNoPath of actual object:
    BEGIN
        SELECT  posNoPath
        INTO    l_posNoPath
        FROM    ibs_Object
        WHERE   oid = ai_rOid;

        BEGIN
            -- set rights key of the actual object and its subsequent objects
            -- having the same old rights key:
            UPDATE  ibs_Object
            SET     rKey = ai_rKey
            WHERE   (posNoPath LIKE l_posNoPath || '%'
                    AND state <> c_STATE_DELETED
                    AND posNoPath <> c_EMPTYPOSNOPATH
                    AND rKey = ai_oldRKey
                    -- don't propagate rights to some specific object types:
                    AND (oid = ai_rOid
--! HACK ...
                        OR  (
                                tVersionId <> c_ParticipantContainer
                            AND tVersionId <> c_Reference
                            )
--! ... HACK
                        )
                    )
                OR  (linkedObjectId IN
                        (SELECT oid
                        FROM    ibs_Object
                        WHERE   posNoPath LIKE l_posNoPath || '%'
                            AND state <> c_STATE_DELETED
                            AND posNoPath <> c_EMPTYPOSNOPATH
                            AND rKey = ai_oldRKey
                            -- don't propagate rights to some specific object
                            -- types:
                            AND (oid = ai_rOid
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
        EXCEPTION
            WHEN OTHERS THEN            -- another error
                ibs_error.log_error (ibs_error.error,
                    'p_Rights$setKeyRec.update',
                    'OTHER error for ai_rOid ' || ai_rOid ||
                    ', ai_rKey ' || ai_rKey ||
                    ', and ai_oldRKey ' || ai_oldRKey);
                l_retValue := c_NOT_OK; -- set corresponding return value
        END;
    EXCEPTION
        WHEN OTHERS THEN                -- another error
            ibs_error.log_error (ibs_error.error,
                'p_Rights$setKeyRec.posNoPath',
                'OTHER error for ai_rOid ' || ai_rOid);
            l_retValue := c_NOT_OK;     -- set corresponding return value
    END;

    -- return the computed state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$setKeyRec',
            'Input: ai_rOid = ' || ai_rOid ||
            ', ai_oldRKey = ' || ai_oldRKey ||
            ', ai_rKey = ' || ai_rKey ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$setKeyRec;
/

show errors;


/******************************************************************************
 * Set the rights for a specific person on an object. <BR>
 * There is no rights check done!
 *
 * @input parameters:
 * @param   ai_rOid             Oid of the object on which rights are defined.
 * @param   ai_rPersonId        Id of the rights person (user, group, ...)
 * @param   ai_rights           New rights.
 * @param   ai_rec              Set rights for sub containers, too.
 *                              (1 true, 0 false).
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$setRights
(
    -- input parameters:
    ai_rOid                 RAW,
    ai_rPersonId            INTEGER,
    ai_rights               INTEGER,
    ai_rec                  NUMBER
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_STATE_DELETED         CONSTANT INTEGER := 1; -- the object was deleted
    c_STATE_ACTIVE          CONSTANT INTEGER := 2; -- active state of object
    c_EMPTYPOSNOPATH        CONSTANT VARCHAR2 (254) := '0000';
                                            -- default/invalid posNoPath

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_posNoPath             VARCHAR2 (254);
    l_oldId                 INTEGER;
    l_id                    INTEGER := 0;
    -- define cursor:
    CURSOR updateCursor IS
        SELECT DISTINCT rKey
        FROM    ibs_Object
        WHERE   posNoPath LIKE l_posNoPath || '%'
--            AND rKey <> l_id
            AND state <> c_STATE_DELETED
            AND posNoPath <> c_EMPTYPOSNOPATH;
    l_cursorRow             updateCursor%ROWTYPE;

    -- exceptions:
    e_anyException          EXCEPTION;      -- used for any exception

BEGIN
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$setRights;

    IF (ai_rec = 1)                     -- set rights for all objects with
                                        -- same path prefix?
    THEN
        BEGIN
            -- get posNoPath of actual object:
            SELECT  posNoPath
            INTO    l_posNoPath
            FROM    ibs_Object
            WHERE   oid = ai_rOid;

            -- loop through the cursor rows:
            FOR l_cursorRow IN updateCursor
            LOOP
                -- get the actual tuple values:
                l_oldId := l_cursorRow.rKey;

                -- get the key containing the resulting rights:
                p_Rights$getKey (l_oldId, ai_rPersonId, ai_rights, l_id);

                -- check if there was an error:
                IF (l_id = -1)          -- an error occurred?
                THEN
                    l_retValue := c_NOT_OK; -- set return value
                ELSE                    -- no error
                    -- set rights key of all subsequent objects of the actual
                    -- object with the same old key:
                    l_retValue := p_Rights$setKeyRec (ai_rOid, l_oldId, l_id);
                END IF; -- else no error

                -- check for an error and exit the loop if there was any:
                EXIT WHEN (l_retValue <> c_ALL_RIGHT);
            END LOOP; -- while another tuple found
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- the referenced object was not found?
                -- set corresponding return value:
                l_retValue := c_NOT_OK;
        END;

    ELSE                                -- set rights only for given object
        -- get the old rights key:
        SELECT  rKey
        INTO    l_oldId
        FROM    ibs_Object
        WHERE   oid = ai_rOid;

        -- get the new rights key:
        p_Rights$getKey (l_oldId, ai_rPersonId, ai_rights, l_id);

        -- check if there was an error:
        IF (l_id = -1)                  -- an error occurred?
        THEN
            l_retValue := c_NOT_OK;     -- set return value
        ELSE                            -- no error
            -- set rights key of the actual object:
            l_retValue := p_Rights$setKey (ai_rOid, l_id);
        END IF; -- else no error
    END IF; -- else set rights only for given object

    -- check if there occurred an error:
    IF (l_retValue <> c_ALL_RIGHT)      -- an error occurred?
    THEN
        RAISE e_anyException;           -- call exception handler
    END IF; -- an error occurred

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$setRights;
        ibs_error.log_error (ibs_error.error, 'p_Rights$setRights',
            'Input: ai_rOid = ' || ai_rOid ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ai_rights = ' || ai_rights ||
            ', ai_rec = ' || ai_rec ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$setRights;
/

show errors;


/******************************************************************************
 * Add rights of a user/group to the actual rights the user has. <BR>
 * If there are no rights defined yet, the added rights are stored as actual
 * rights. If there are already some rights defined the new rights are added
 * to this rights (bitwise OR).
 *
 * @input parameters:
 * @param   ai_rOid             Oid of the object on which rights are defined.
 * @param   ai_rPersonId        Id of the rights person (user, group, ...)
 * @param   ai_rights           New rights.
 * @param   ai_rec              Set rights for sub containers, too.
 *                              (1 true, 0 false).
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$addRights
(
    -- input parameters:
    ai_rOid                 RAW,
    ai_rPersonId            INTEGER,
    ai_rights               INTEGER,
    ai_rec                  NUMBER
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_STATE_DELETED         CONSTANT INTEGER := 1; -- the object was deleted
    c_STATE_ACTIVE          CONSTANT INTEGER := 2; -- active state of object
    c_EMPTYPOSNOPATH        CONSTANT VARCHAR2 (254) := '0000';
                                            -- default/invalid posNoPath

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_posNoPath             VARCHAR2 (254);
    l_oldId                 INTEGER;
    l_id                    INTEGER := 0;
    l_newRights             INTEGER := ai_rights;
    -- define cursor:
    CURSOR updateCursor IS
        SELECT DISTINCT rKey
        FROM    ibs_Object
        WHERE   posNoPath LIKE l_posNoPath || '%'
--            AND rKey <> l_id
            AND state <> c_STATE_DELETED
            AND posNoPath <> c_EMPTYPOSNOPATH;
    l_cursorRow             updateCursor%ROWTYPE;

    -- exceptions:
    e_anyException          EXCEPTION;      -- used for any exception

BEGIN
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$addRights;

    IF (ai_rec = 1)                     -- set rights for all objects with
                                        -- same path prefix?
    THEN
        BEGIN
            -- get posNoPath of actual object:
            SELECT  posNoPath
            INTO    l_posNoPath
            FROM    ibs_Object
            WHERE   oid = ai_rOid;

            -- loop through the cursor rows:
            FOR l_cursorRow IN updateCursor
            LOOP
                -- get the actual tuple values:
                l_oldId := l_cursorRow.rKey;

                -- initialize the new rights:
                l_newRights := ai_rights;

                -- get resulting rights:
                -- compute the resulting rights as binary or
                -- (= bitwise sum) of the old and the new rights:
                -- (if there are no rights set for the rPerson the value of
                -- l_newRights stays as initialized => l_newRights = ai_rights)
                BEGIN
                    -- compute the resulting rights as binary or
                    -- (= bitwise sum) of the old and the new rights:
                    SELECT  B_OR (rights, ai_rights)
                    INTO    l_newRights
                    FROM    ibs_RightsKeys
                    WHERE   id = l_oldId
                        AND rPersonId = ai_rPersonId;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN -- there were no old rights defined?
                        -- set the new rights as resulting rights:
                        l_newRights := ai_rights;
                END;

                -- get the key containing the resulting rights:
                p_Rights$getKey (l_oldId, ai_rPersonId, l_newRights, l_id);

                -- check if there was an error:
                IF (l_id = -1)          -- an error occurred?
                THEN
                    l_retValue := c_NOT_OK; -- set return value
                ELSE                    -- no error
                    -- set rights key of all subsequent objects of the actual
                    -- object with the same old key:
                    l_retValue := p_Rights$setKeyRec (ai_rOid, l_oldId, l_id);
                END IF; -- else no error

                -- check for an error and exit the loop if there was any:
                EXIT WHEN (l_retValue <> c_ALL_RIGHT);
            END LOOP; -- while another tuple found
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- the referenced object was not found?
                -- set corresponding return value:
                l_retValue := c_NOT_OK;
        END;

    ELSE                                -- set rights only for given object
        -- get the old rights key:
        SELECT  rKey
        INTO    l_oldId
        FROM    ibs_Object
        WHERE   oid = ai_rOid;

        -- get resulting rights:
        -- compute the resulting rights as binary or
        -- (= bitwise sum) of the old and the new rights:
        -- (if there are no rights set for the rPerson the value of
        -- l_newRights stays as initialized => l_newRights = ai_rights)
        BEGIN
            -- compute the resulting rights as binary or
            -- (= bitwise sum) of the old and the new rights:
            SELECT  B_OR (rights, ai_rights)
            INTO    l_newRights
            FROM    ibs_RightsKeys
            WHERE   id = l_oldId
                AND rPersonId = ai_rPersonId;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- there were no old rights defined?
                -- set the new rights as resulting rights:
                l_newRights := ai_rights;
        END;

        -- get the new rights key:
        p_Rights$getKey (l_oldId, ai_rPersonId, l_newRights, l_id);

        -- check if there was an error:
        IF (l_id = -1)                  -- an error occurred?
        THEN
            l_retValue := c_NOT_OK;     -- set return value
        ELSE                            -- no error
            -- set rights key of the actual object:
            l_retValue := p_Rights$setKey (ai_rOid, l_id);
        END IF; -- else no error
    END IF; -- else set rights only for given object

    -- check if there occurred an error:
    IF (l_retValue <> c_ALL_RIGHT)      -- an error occurred?
    THEN
        RAISE e_anyException;           -- call exception handler
    END IF; -- an error occurred

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$addRights;
        ibs_error.log_error (ibs_error.error, 'p_Rights$addRights',
            'Input: ai_rOid = ' || ai_rOid ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ai_rights = ' || ai_rights ||
            ', ai_rec = ' || ai_rec ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$addRights;
/

show errors;


/******************************************************************************
 * Propagate the rights of one object to another object. <BR>
 * After this operation the secondary object has the same rights as the primary
 * one.
 *
 * @input parameters:
 * @param   ai_masterObjectId   Oid of the object which is the master for this
 *                              operation.
 * @param   ai_rOid             Oid of the object on which rights are defined.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$propagateRights
(
    -- input parameters:
    ai_masterObjectId       RAW,
    ai_rOid                 RAW
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_rKey                  INTEGER := -1;

BEGIN
-- body:
--ibs_error.log_error (ibs_error.error, 'p_Rights$propagateRights', 'DIESE PROZEDUR SOLLTE EIGENTLICH GAR NICHT AUFGERUFEN WERDEN. Diese Prozedur wurde aufgerufen mit ' || ai_masterObjectId || ' und ' || ai_rOid || '.');
    -- get the rights key of the master object:
    l_rKey := p_Rights$getRightsKey (ai_masterObjectId);

    -- check if there was an error:
    IF (l_rKey = -1)                    -- an error occurred?
    THEN
        l_retValue := c_NOT_OK;         -- set return value
    ELSE                                -- no error
        -- set the rights key of the secondary object:
        l_retValue := p_Rights$setKey (ai_rOid, l_rKey);
    END IF; -- else no error

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$propagateRights',
            'Input: ai_masterObjectId = ' || ai_masterObjectId ||
            ', ai_rOid = ' || ai_rOid ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$propagateRights;
/

show errors;


/******************************************************************************
 * Propagate the rights of one person on an object to another object. <BR>
 * After this operation the person has on the secondary object the same rights
 * as on the primary one.
 *
 * @input parameters:
 * @param   ai_masterOid        Object from which the rights shall be
 *                              propagated.
 * @param   ai_rOid             Object to which the rights shall be propagated.
 * @param   ai_rPersonId        ID of the person for which the rights shall be
 *                              propagated.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$propagateUserRights
(
    -- input parameters:
    ai_masterOid            RAW,
    ai_rOid                 RAW,
    ai_rPersonId            INTEGER
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_id                    INTEGER := 0;
    l_oldId                 INTEGER := 0;
    l_rights                INTEGER := 0;

BEGIN
-- body:
    -- get the old rights key:
    BEGIN
        SELECT  rKey
        INTO    l_oldId
        FROM    ibs_Object
        WHERE   oid = ai_rOid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN     -- no key found?
            l_oldId := 0;           -- set default value
    END;

    -- get the rights to be set:
    BEGIN
        SELECT  rights
        INTO    l_rights
        FROM    ibs_RightsKeys r, ibs_Object o
        WHERE   r.rPersonId = ai_rPersonId
            AND o.oid = ai_masterOid
            AND o.rKey = r.id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN     -- no key found?
            l_rights := 0;          -- set default value
    END;

    -- get the new rights key:
    p_Rights$getKey (l_oldId, ai_rPersonId, l_rights, l_id);

    -- check if there was an error:
    IF (l_id = -1)                      -- an error occurred?
    THEN
        l_retValue := c_NOT_OK;         -- set return value
    ELSE                                -- no error
        -- update the rights key of the object:
        l_retValue := p_Rights$setKey (ai_rOid, l_id);
    END IF; -- else no error

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$propagateUserRights',
            'Input: ai_masterOid = ' || ai_masterOid ||
            ', ai_rOid = ' || ai_rOid ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Rights$propagateUserRights;
/

show errors;


/******************************************************************************
 * Delete all rights of a given object. <BR>
 *
 * @input parameters:
 * @param   ai_oid              ID of the object for which to delete the rights.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$deleteObjectRights
(
    -- common input parameters:
    ai_oid                  RAW
)
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function

BEGIN
-- body:
    -- delete all rights defined on the object:
    l_retValue := p_Rights$setKey (ai_oid, 0);
END p_Rights$deleteObjectRights;
/

show errors;


/******************************************************************************
 * Delete all rights of a given object and all sub objects. <BR>
 *
 * @input parameters:
 * @param   ai_oid              ID of the object for which to delete the rights.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$deleteObjectRightsRec
(
    -- common input parameters:
    ai_oid                  RAW
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_oldRKey               INTEGER;        -- id of the old rights key

-- body:
BEGIN
    -- get the old rights key:
    BEGIN
        SELECT  rKey
        INTO    l_oldRKey
        FROM    ibs_Object
        WHERE   oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
            l_retValue = c_NOT_OK;
    END;

    -- delete all rights defined on the object:
    p_Rights$setKeyRec (ai_oid, l_oldRKey, 0);
END p_Rights$deleteObjectRightsRec;
/

show errors;


/******************************************************************************
 * Set the rights of a specific user and Businessobject. <BR>
 * With the operation parameter you can define the specific operation.
 *
 * @input parameters:
 * @param   ai_rOid             ID of the object, for which rights shall be set.
 * @param   ai_rPersonId        ID of the person/group/role for which rights
 *                              shall be set.
 * @param   ai_rights           The rights to be set.
 * @param   ai_operation        The operation to be performed with the rights:
 *                              1 ... overwrite the actual rights.
 *                              2 ... delete all rights of person on object.
 *                              3 ... delete all rights of object.
 *                              4 ... add the new rights to the actual ones,
 *                                    if no rights exist insert new.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Rights$set
(
    -- input parameters:
    ai_rOid                 RAW,
    ai_rPersonId            INTEGER,
    ai_rights               INTEGER,
    ai_operation            INTEGER
)
AS
BEGIN
    IF (ai_operation = 1)               -- new?
    THEN
        p_Rights$setRights (ai_rOid, ai_rPersonId, ai_rights, 0);
    ELSIF (ai_operation = 2)            -- delete all rights of person on obj.?
    THEN
        p_Rights$setRights (ai_rOid, ai_rPersonId, 0, 0);
    ELSIF (ai_operation = 3)            -- delete all rights of object?
    THEN
        p_Rights$deleteObjectRights (ai_rOid);
    ELSIF (ai_operation = 4)            -- add rights to actual ones?
    THEN
        p_Rights$addRights (ai_rOid, ai_rPersonId, ai_rights, 0);
    END IF; -- else if add rights to actual ones

    COMMIT WORK;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$set',
            'Input: ai_rOid = ' || ai_rOid ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ai_rights = ' || ai_rights ||
            ', ai_operation = ' || ai_operation ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);

END p_Rights$set;
/

show errors;


/******************************************************************************
 * Propagate the rights of an object to all its subobjects.
 *
 * @input parameters:
 * @param   ai_oid_s            The oid of the object which rights should be
 *                              propagated.
 * @param   ai_userId           The user which is doing the operation.
 * @param   ai_op               The operation that is being done.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  NOT_OK                  Something went wrong during performing the
 *                          operation.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_ALL                 The operation could not be performed on all objects
 *                          (because the user did not have enough rights to
 *                          set the rights on all these objects).
 */
CREATE OR REPLACE FUNCTION p_Rights$setRightsRecursive
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_OWNER                 CONSTANT INTEGER := 9437185; -- 0x00900001
                                            -- user id of owner
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_NOT_ALL               CONSTANT INTEGER := 31; -- operation could not be
                                            -- performed for all objects
    c_STATE_DELETED         CONSTANT INTEGER := 1; -- the object was deleted
    c_STATE_ACTIVE          CONSTANT INTEGER := 2; -- active state of object
    c_ParticipantContainer  CONSTANT INTEGER := 16850945; -- 0x01012001
                                            -- tVersionId of
                                            -- ParticipantContainer
    c_Reference             CONSTANT INTEGER := 16842801; -- 0x01010031
                                            -- tVersionId of Reference

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_oid                   RAW (8);        -- the object id of the act. object
    l_posNoPath             VARCHAR2 (254); -- pos no path of the object
    l_rKey                  INTEGER := 0;   -- rKey of the object
    l_count                 INTEGER := 0;   -- counter

BEGIN
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_oid_s, l_oid);

    -- get posNoPath and rights key of actual object:
    SELECT  posNoPath, rKey
    INTO    l_posNoPath, l_rKey
    FROM    ibs_Object
    WHERE   oid = l_oid;

    -- check if there are any objects for which the rights cannot be set:
    l_retValue := p_Rights$checkRightsRec (l_posNoPath, ai_userId, ai_op);

    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL)
                                    -- rights can be set for >= one object?
    THEN
--
-- MW - Performance Tuning: split update in 2 parts
--
        -- set rights for subsequent objects:
        BEGIN
/*
            UPDATE  ibs_Object
            SET     rKey = l_rKey
            WHERE   oid IN
                    (SELECT o.oid
                    FROM    ibs_Object o, ibs_RightsCum r
                    WHERE   o.posNoPath LIKE l_posNoPath || '%'
                        AND o.oid <> l_oid
                        AND r.rKey = o.rKey
                        AND (
                                (o.owner <> ai_userId
                                AND r.userId = ai_userId)
                            OR
                                (o.owner = ai_userId
                                AND r.userId = c_OWNER)
                            )
                        AND B_AND (r.rights, ai_op) = ai_op
                        AND state <> c_STATE_DELETED
                    )
                    -- don't propagate rights to some specific object types:
--! HACK ...
                    AND tVersionId <> c_ParticipantContainer
                    AND tVersionId <> c_Reference
--! ... HACK
                    ;
*/
            --
            -- SPLIT update statement in 2 faster parts - no OR needed
            --       (sets rights for subsequent objects)
            --

            -- 1. part of update statment
            UPDATE  ibs_Object
            SET     rKey = l_rKey
            WHERE   oid IN
                    (SELECT o.oid
                     FROM   ibs_Object o, ibs_RightsCum r
                     WHERE  o.posNoPath LIKE l_posNoPath || '%'
                        AND r.userId = ai_userId
                        AND o.owner <> ai_userId
                        AND o.oid <> l_oid
                        AND r.rKey = o.rKey
                        AND B_AND (r.rights, ai_op) = ai_op
                        AND o.state <> c_STATE_DELETED
--! HACK ...
                        -- don't propagate rights to some specific object types:
                        AND o.tVersionId <> c_ParticipantContainer
                        AND o.tVersionId <> c_Reference
--! ... HACK
                    );

            -- 2. part of update statment
            UPDATE  ibs_Object
            SET     rKey = l_rKey
            WHERE   oid IN
                    (SELECT o.oid
                     FROM   ibs_Object o, ibs_RightsCum r
                     WHERE  o.posNoPath LIKE l_posNoPath || '%'
                        AND o.owner = ai_userId
                        AND r.userId = c_OWNER
                        AND o.oid <> l_oid
                        AND r.rKey = o.rKey
                        AND B_AND (r.rights, ai_op) = ai_op
                        AND o.state <> c_STATE_DELETED
--! HACK ...
                        -- don't propagate rights to some specific object types:
                        AND o.tVersionId <> c_ParticipantContainer
                        AND o.tVersionId <> c_Reference
--! ... HACK
                    );

        EXCEPTION
            WHEN OTHERS THEN
                -- set corresponding return value:
                l_retValue := c_NOT_OK;
                ibs_error.log_error (ibs_error.error,
                    'p_Rights$setRightsRecursive.set subsequent rights',
                    'OTHER error for oid ' || l_oid ||
                    ' and posNoPath ' || l_posNoPath);
                RAISE;
        END;
    END IF; -- rights can be set for >= one object

    -- finish the transaction:
    IF (l_retValue <> c_ALL_RIGHT AND l_retValue <> c_NOT_ALL)
                                        -- there occurred a severe error?
    THEN
        ROLLBACK;                       -- undo changes
    END IF; -- there occurred a severe error

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$setRightsRecursive',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- undo all changes and set new transaction starting point:
        ROLLBACK;
        COMMIT WORK;
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$setRightsRecursive;
/

show errors;


/******************************************************************************
 * Set rights of an user on an object and all its sub objects (incl.
 * rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid              The oid of the root object from which rights
 *                              should be deleted.
 * @param   ai_userId           The user who is doing the operation.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_rPersonId        The person for whom the rights shall be set.
 * @param   ai_rights           The rights to be set.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                   Action performed, values returned, evrythng ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 *  OBJECTNOTFOUND              The required object was not found within the
 *                              database.
 */
CREATE OR REPLACE FUNCTION p_Rights$setUserRightsRec1
(
    -- input parameters:
    ai_oid                  RAW,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_rPersonId            INTEGER,
    ai_rights               INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_OWNER                 CONSTANT INTEGER := 9437185; -- 0x00900001
                                            -- user id of owner
    c_EMPTYPOSNOPATH        CONSTANT VARCHAR2 (254) := '0000';
                                            -- default/invalid posNoPath
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_NOT_ALL               CONSTANT INTEGER := 31; -- operation could not be
                                            -- performed for all objects
    c_STATE_DELETED         CONSTANT INTEGER := 1; -- the object was deleted
    c_STATE_ACTIVE          CONSTANT INTEGER := 2; -- active state of object

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_posNoPath             VARCHAR2 (254);
    l_actOid                RAW (8);
    -- define cursor:
/*
    CURSOR updateCursor IS
        SELECT DISTINCT rKey
        FROM    ibs_Object
        WHERE   posNoPath LIKE l_posNoPath || '%'
--            AND rKey <> l_id
            AND state <> c_STATE_DELETED
            AND posNoPath <> c_EMPTYPOSNOPATH;
*/
    CURSOR updateCursor IS
        SELECT  o.oid
        FROM    ibs_Object o, ibs_RightsCum r
        WHERE   o.posNoPath LIKE l_posNoPath || '%'
            AND o.state <> c_STATE_DELETED
            AND o.oid <> ai_oid
            AND o.rKey = r.rKey
            AND (
                    (o.owner <> ai_userId
                    AND r.userId = ai_userId)
                OR
                    (o.owner = ai_userId
                    AND r.userId = c_OWNER)
                )
            AND B_AND (r.rights, ai_op) = ai_op;
    l_cursorRow             updateCursor%ROWTYPE;

BEGIN
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Rights$setUserRightsRec1;

    BEGIN
        -- get the posNoPath of the root object:
        SELECT  posNoPath
        INTO    l_posNoPath
        FROM    ibs_Object
        WHERE   oid = ai_oid;

        -- check if there are any objects for which the rights cannot be set:
        l_retValue := p_Rights$checkRightsRec (l_posNoPath, ai_userId, ai_op);

        IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL)
                                        -- rights can be set for >= one object?
        THEN
            -- run through all objects:
            FOR l_cursorRow IN updateCursor
            LOOP
                l_actOid := l_cursorRow.oid;
                -- set the rights for the actual object:
                p_Rights$setRights (l_actOid, ai_rPersonId, ai_rights, 0);
            END LOOP;
        END IF; -- rights can be set for >= one object

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the object or its rights key was
                                        -- not found?
            l_retValue := c_NOT_OK;     -- set corresponding return value
    END;

    -- finish the transaction:
    IF (l_retValue <> c_ALL_RIGHT AND l_retValue <> c_NOT_ALL)
                                        -- there occurred a severe error?
    THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$setUserRightsRec1;
    END IF; -- there occurred a severe error

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Rights$setUserRightsRec1;
        ibs_error.log_error (ibs_error.error, 'p_Rights$deleteUserRightsRec1',
            'Input: ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$setUserRightsRec1;
/

show errors;


/******************************************************************************
 * Propagate the rights an user has on an object to all its sub objects.
 * (incl. rights check)
 *
 * @input parameters:
 * @param   ai_oid              The oid of the root object from which rights
 *                              should be propagated.
 * @param   ai_userId           The user which is doing the operation.
 * @param   ai_op               The operation that is being done.
 * @param   ai_rPersonId        The person for whom the rights shall be
 *                              propagated.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  NOT_OK                  Something went wrong during performing the
 *                          operation.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_ALL                 The operation could not be performed on all objects
 *                          (because the user did not have enough rights to
 *                          set the rights on all these objects).
 */
CREATE OR REPLACE FUNCTION p_Rights$setUserRightsRec
(
    -- input parameters:
    ai_oid                  RAW,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_rPersonId            INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_OWNER                 CONSTANT INTEGER := 9437185; -- 0x00900001
                                            -- user id of owner
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_NOT_ALL               CONSTANT INTEGER := 31; -- operation could not be
                                            -- performed for all objects

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_rights                INTEGER := 0;   -- the rights

BEGIN
-- body:
    BEGIN
        -- get the rights of the specific person for the defined object:
        SELECT  r.rights
        INTO    l_rights
        FROM    ibs_Object o, ibs_RightsKeys r
        WHERE   o.oid = ai_oid
            AND r.rPersonId = ai_rPersonId
            AND o.rKey = r.id;

        -- set the rights for the user on all subsequent objects:
        l_retValue := p_Rights$setUserRightsRec1
                (ai_oid, ai_userId, ai_op, ai_rPersonId, l_rights);

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the object or its rights key was
                                        -- not found?
            l_retValue := c_NOT_OK;     -- set corresponding return value
    END;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$setUserRightsRec',
            'Input: ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$setUserRightsRec;
/

show errors;


/******************************************************************************
 * Delete all rights of an user on an object and all its sub objects (incl.
 * rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid              The oid of the root object from which rights
 *                              should be deleted.
 * @param   ai_userId           The user who is doing the operation.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_rPersonId        The person for whom the rights shall be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                   Action performed, values returned, evrythng ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 *  OBJECTNOTFOUND              The required object was not found within the
 *                              database.
 */
CREATE OR REPLACE FUNCTION p_Rights$deleteUserRightsRec
(
    -- input parameters:
    ai_oid                  RAW,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_rPersonId            INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_NOT_ALL               CONSTANT INTEGER := 31;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;

BEGIN
-- body:
    -- set the rights for the user on all subsequent objects:
    l_retValue := p_Rights$setUserRightsRec1
            (ai_oid, ai_userId, ai_op, ai_rPersonId, 0);

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$deleteUserRightsRec',
            'Input: ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$deleteUserRightsRec;
/

show errors;


/******************************************************************************
 * Check if a user has the necessary access rights on an object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            Oid of the object for which the rights shall be
 *                              checked.
 * @param   ai_containerId_s    Container in which the object resides.
 *                              This container is used if there are no rights
 *                              defined on the object itself.
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              checked.
 * @param   ai_requiredRights   Rights which are required for the user.
 *                              This is a bit pattern where each required right
 *                              is set to 1.
 *
 * @output parameters:
 * @param   ao_hasRights        Contains all of the required rights which are
 *                              allowed.
 *                              ao_hasRights == ai_requiredRights if the user
 *                              has all required rights.
 * @return  A value representing the rights or the state of the procedure.
 *  = ao_hasRights              No error, the value contains the rights.
 *  NOT_OK                      Any error occurred.
 */
CREATE OR REPLACE FUNCTION p_Rights$checkRights1
(
    -- input parameters
    ai_oid_s                VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_userId               INTEGER,
    ai_requiredRights       INTEGER,
    -- output parameters:
    ao_hasRights            OUT      INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function

BEGIN
-- body:
    -- redirect to basic checkRights procedure:
    l_retValue := p_Rights$checkObjectRights
        (ai_oid_s, ai_containerId_s, ai_userId, ai_requiredRights,
        ao_hasRights);

    -- return the computed rights:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$checkRights1',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_containerId_s = ' || ai_containerId_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_requiredRights = ' || ai_requiredRights ||
            ', ao_hasRights = ' || ao_hasRights ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights$checkRights1;
/

show errors;


exit;
