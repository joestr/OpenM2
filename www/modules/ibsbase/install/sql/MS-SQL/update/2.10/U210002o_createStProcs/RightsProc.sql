/******************************************************************************
 * All stored procedures regarding the rights table. <BR>
 *
 * @version     $Id: RightsProc.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$updateRightsCum'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$updateRightsCum
AS
DECLARE
    -- constants:

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_msg                  NVARCHAR (255), -- message for printout
    @l_i                    ID,             -- loop counter
    @l_maxId                ID,             -- maximum id value
    @l_step                 ID              -- step for incrementing the id

    -- initialize local variables and return values:
SELECT
    @l_error = 0,
    @l_step = 100

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$updateRightsCum

    -- delete all cumulated rights:
    DELETE ibs_RightsCum
    -- truncate is not possible because there is no rollback mechanism
--    TRUNCATE TABLE ibs_RightsCum

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

--EXEC p_showActDateTime 'start loop'
    SELECT  @l_i = MIN (id), @l_maxId = MAX (id)
    FROM    ibs_RightsKey

    WHILE (@l_i <= @l_maxId)
    BEGIN
        -- recalculate the cumulated rights and update the data:
        INSERT INTO ibs_RightsCum (userId, rKey, rights)
        SELECT  p.userId, rk.id,
                MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
                MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
                MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
                MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
                MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
                MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
                MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
                MAX (r1C) + MAX (r1D) + MAX (r1E) + MAX (r1F)
        FROM    ibs_RightsKeys r, ibs_RightsKey rk,
                (
                    SELECT  id AS id, id AS userId
                    FROM    ibs_User
                    UNION
                    SELECT  DISTINCT gu.groupId AS id, gu.userId AS userId
                    FROM    ibs_GroupUser gu, ibs_User u
                    WHERE   gu.userId = u.id
                ) p
        WHERE   p.id = r.rPersonId
            AND r.id = rk.rKeysId
            AND p.userId <> rk.owner
            AND rk.id >= @l_i
            AND rk.id < (@l_i + @l_step)
        GROUP BY rk.id, p.userId

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- compute index for next step:
        SELECT  @l_i = @l_i + @l_step

/*
        -- print state message:
        SELECT  @l_msg =
                CONVERT (VARCHAR (7), @l_i - 1) + ' / ' +
                CONVERT (VARCHAR (7), @l_maxId) + ' done.'
        EXEC p_showActDateTime @l_msg
*/
    END -- while

--EXEC p_showActDateTime 'insert owner rights1'
    -- insert rights for owners where these rights are not already set:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  owner, id, 0x7FFFFFFF
    FROM    ibs_RightsKey

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler
--EXEC p_showActDateTime 'insert owner rights2'

/* KR tuning: explicit rights check for owner is not longer necessary
    -- insert rights for owner for rKey = 0 if these rights are not
    -- already set:
    -- (This is necessary because rKey = 0 means that there are no
    -- explicit permissions set. This also means that there are no
    -- rights cumulated for that key. And so not even the owner has
    -- access to objects with this key if there are no rights
    -- cumulated.)
    IF NOT EXISTS (
        SELECT  rKey
        FROM    ibs_RightsCum
        WHERE   userId = 0x00900001
            AND rKey = 0
        )                   -- no rights for owner with rKey = 0
        -- insert the owner rights into the cumulation table:
        INSERT INTO ibs_RightsCum (userId, rKey, rights)
        VALUES (0x00900001, 0, 0x7FFFFFFF)

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler
*/

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$updateRightsCum
GO
-- p_Rights$updateRightsCum


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$updateRightsCumGroup'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$updateRightsCumGroup
(
    @ai_groupId             GROUPID
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_error                INT             -- the actual error code

    -- initialize local variables and return values:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$updateRightsCumGroup

    -- delete all cumulated rights for the users within the requested group:
    DELETE  ibs_RightsCum
    WHERE   userId IN
            (
                SELECT  userId
                FROM    ibs_GroupUser
                WHERE   groupId = @ai_groupId
            )

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- recalculate the cumulated rights for all users who are in the
    -- regarded group and update the data:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, rk.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E) + MAX (r1F)
    FROM    ibs_RightsKeys r, ibs_RightsKey rk,
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
                            WHERE   groupId = @ai_groupId
                        )
                UNION
                SELECT  DISTINCT groupId AS id, userId AS userId
                FROM    ibs_GroupUser
                WHERE   userId IN
                        (
                            SELECT  DISTINCT userId
                            FROM    ibs_GroupUser
                            WHERE   groupId = @ai_groupId
                        )
            ) p
    WHERE   p.id = r.rPersonId
        AND r.id = rk.rKeysId
        AND p.userId <> rk.owner
    GROUP BY rk.id, p.userId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- insert rights for owners where these rights are not already set:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  rk.owner, rk.id, 0x7FFFFFFF
    FROM    ibs_RightsKey rk,
            (
                -- get the users who are in the regarded group (recursively):
                SELECT  DISTINCT userId
                FROM    ibs_GroupUser
                WHERE   groupId = @ai_groupId
            ) p
    WHERE   p.userId = rk.owner

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$updateRightsCumGroup
GO
-- p_Rights$updateRightsCumGroup


/******************************************************************************
 * Recalculate the cumulated rights for a specific user. <BR>
 *
 * @input parameters:
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              cumulated.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$updateRightsCumUser'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$updateRightsCumUser
(
    @ai_userId              USERID
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_error                INT             -- the actual error code

    -- initialize local variables and return values:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$updateRightsCumUser

    -- delete all cumulated rights for the requested user:
    DELETE  ibs_RightsCum
    WHERE   userId = @ai_userId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- recalculate the cumulated rights for given user and insert the data
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, rk.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E) + MAX (r1F)
    FROM    ibs_RightsKeys r, ibs_RightsKey rk,
            (
                SELECT  @ai_userId AS id, @ai_userId AS userId
                UNION
                SELECT  DISTINCT groupId AS id, userId AS userId
                FROM    ibs_GroupUser
                WHERE   userId = @ai_userId
            ) p
    WHERE   p.id = r.rPersonId
        AND p.userId = @ai_userId
        AND r.id = rk.rKeysId
        AND rk.owner <> @ai_userId
    GROUP BY rk.id, p.userId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- insert rights for owners where these rights are not already set:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  rk.owner, rk.id, 0x7FFFFFFF
    FROM    ibs_RightsKey rk
    WHERE   rk.owner = @ai_userId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$updateRightsCumUser
GO
-- p_Rights$updateRightsCumUser


/******************************************************************************
 * Recalculate the cumulated rights for a specific key. This procedure is
 * only used in trigger 'TrigRightsKeysInsert' on table 'ibs_RightsKeys'.<BR>
 *
 * @input parameters:
 * @param   ai_rightsKeysId     Id of the key for which the rights shall be
 *                              cumulated.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$updateRightsCumKey'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$updateRightsCumKey
(
    @ai_rightsKeysId        ID
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_error                INT             -- the actual error code

    -- initialize local variables and return values:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$updateRightsCumKey

    -- drop all cumulated rights already derived for the actual key:
    DELETE  ibs_RightsCum
    WHERE   rKey = @ai_rightsKeysId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- recalculate the cumulated rights for given key and insert the data
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  p.userId, rk.id,
            MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
            MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
            MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
            MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
            MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
            MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
            MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
            MAX (r1C) + MAX (r1D) + MAX (r1E) + MAX (r1F)
    FROM    ibs_RightsKeys r
            INNER JOIN
            (
                SELECT  id AS id, id AS userId
                FROM    ibs_User
                UNION
                SELECT  DISTINCT gu.groupId AS id, gu.userId AS userId
                FROM    ibs_GroupUser gu, ibs_User u
                WHERE   gu.userId = u.id
            ) p
            ON p.id = r.rPersonId
            INNER JOIN
            (
                SELECT  *
                FROM    ibs_RightsKey
                WHERE   id = @ai_rightsKeysId
            ) rk
            ON r.id = rk.rKeysId AND p.userId <> rk.owner
    GROUP BY rk.id, p.userId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- insert rights for owners where these rights are not already set:
    INSERT INTO ibs_RightsCum (userId, rKey, rights)
    SELECT  owner, id, 0x7FFFFFFF
    FROM    ibs_RightsKey
    WHERE   id = @ai_rightsKeysId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$updateRightsCumKey
GO
-- p_Rights$updateRightsCumKey


/******************************************************************************
 * Delete the rights for a specific user. <BR>
 *
 * @input parameters:
 * @param   ai_userId           Id of the user for whom the rights shall be
 *                              deleted.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$deleteAllUserRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$deleteAllUserRights
(
    @ai_userId              USERID
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_error                INT             -- the actual error code

    -- initialize local variables and return values:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$deleteAllUserRights

    -- set the number of actual values within the keys where the user shall be
    -- dropped:
    UPDATE  ibs_RightsKey
    SET     cnt = cnt - 1
    WHERE   rKeysId IN
            (
                SELECT  DISTINCT id
                FROM    ibs_RightsKeys
                WHERE   rPersonId = @ai_userId
            )

    -- set the number of actual values within the keys where the user shall be
    -- dropped:
    UPDATE  ibs_RightsKeys
    SET     cnt = cnt - 1
    WHERE   id IN
            (
                SELECT  DISTINCT id
                FROM    ibs_RightsKeys
                WHERE   rPersonId = @ai_userId
            )

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- delete all entries for the requested user within the keys:
    DELETE  ibs_RightsKeys
    WHERE   rPersonId = @ai_userId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- delete all cumulated rights for the requested user:
    DELETE  ibs_RightsCum
    WHERE   userId = @ai_userId

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$deleteAllUserRights
GO
-- p_Rights$deleteAllUserRights


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
 *                              @hasRights == @requiredRights if the user has
 *                              all required rights.
 * @return  A value representing the state of the procedure.
 *  NOT_OK                      Any error occurred.
 *  ALL_RIGHT                   Action may be performed, everything ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$checkRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$checkRights
(
    -- input parameters:
    @ai_oid                 OBJECTID,
    @ai_containerId         OBJECTID,
    @ai_userId              USERID = 0,
    @ai_requiredRights      RIGHTS = 0xFFFFFFFF,
    -- output parameters:
    @ao_hasRights           RIGHTS = 0 OUTPUT
)
AS
DECLARE
    -- constants:
/* KR tuning: old version with separate owner values
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_OWNER                USERID,         -- user id of owner
*/

    -- local variables:
/* KR tuning: old version with separate owner values
    @l_count                INT,
*/
    @l_rKey                 ID              -- the actual rights key
/* KR tuning: old version with separate owner values
    @l_actUserId            USERID          -- actual user id
*/

    -- assign constants:
/* KR tuning: old version with separate owner values
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_OWNER                = 0x00900001
*/

    -- initialize local variables and return values:
SELECT
    @l_rKey = -1,
/* KR tuning: old version with separate owner values
    @l_actUserId = @ai_userId,
*/
    @ao_hasRights = 0                   -- no rights allowed thus far

-- body:
    -- check if a rights check shall be done:
    IF (@ai_requiredRights = 0)
    BEGIN
        -- return the original value:
        SELECT  @ao_hasRights = @ai_requiredRights
        RETURN @ao_hasRights
    END -- if

/* KR tuning: old version with separate owner values
    -- check if the actual user is the owner of the required object:
    IF (@ai_oid = @c_NOOID)
        SELECT  @l_count = COUNT (id)
        FROM    ibs_Object
        WHERE   oid = @ai_containerId
        AND     owner = @ai_userId
    ELSE
        SELECT  @l_count = COUNT (id)
        FROM    ibs_Object
        WHERE   oid = @ai_oid
        AND     owner = @ai_userId

    -- check if the actual user is the owner of the required object:
    IF (@l_count > 0)
        SELECT  @l_actUserId = @c_OWNER     -- user id of owner
    ELSE
        SELECT  @l_actUserId = @ai_userId   -- given userId
*/

    -- get the rights key of the actual object:
    SELECT  @l_rKey = rKey
    FROM    ibs_Object
    WHERE   oid = @ai_oid

    IF (@@ROWCOUNT = 0)                 -- no valid rights key found?
        -- get the rights key of the container:
        SELECT  @l_rKey = rKey
        FROM    ibs_Object
        WHERE   oid = @ai_containerId

    -- compute the rights:
    SELECT  @ao_hasRights = (rights & @ai_requiredRights)
    FROM    ibs_RightsCum r
    WHERE   rKey = @l_rKey
        AND userId = @ai_userId
/* KR tuning: old version with separate owner values
    -- compute the rights:
    SELECT  @ao_hasRights = (rights & @ai_requiredRights)
    FROM    ibs_RightsCum r
    WHERE   rKey = @l_rKey
        AND userId = @l_actUserId
*/

    -- return the computed rights:
    RETURN @ao_hasRights
GO
-- p_Rights$checkRights


/******************************************************************************
 * Check if a user has the necessary access rights within a specific rKey. <BR>
 *
 * @input parameters:
 * @param   ai_oid              Oid of the object for which the rights shall be
 *                              checked.
 * @param   ai_rKey             The rKey to be checked.
 * @param   ai_requiredRights   Rights which are required for the user.
 *                              This is a bit pattern where each required right
 *                              is set to 1.
 *
 * @output parameters:
 * @param   ao_hasRights        Contains all of the required rights which are
 *                              allowed.
 *                              @hasRights == @requiredRights if the user has
 *                              all required rights.
 * @return  A value representing the state of the procedure.
 *  NOT_OK                      Any error occurred.
 *  ALL_RIGHT                   Action may be performed, everything ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$checkRKeyRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$checkRKeyRights
(
    -- input parameters:
    @ai_rKey                ID,
    @ai_userId              USERID = 0,
    @ai_requiredRights      RIGHTS = 0xFFFFFFFF,
    -- output parameters:
    @ao_hasRights           RIGHTS = 0 OUTPUT
)
AS
--DECLARE
    -- constants:

    -- local variables:

    -- assign constants:

    -- initialize local variables and return values:
SELECT
    @ao_hasRights = 0                   -- no rights allowed thus far

-- body:
    -- check if a rights check shall be done:
    IF (@ai_requiredRights = 0)
    BEGIN
        -- return the original value:
        SELECT  @ao_hasRights = @ai_requiredRights
        RETURN @ao_hasRights
    END -- if

    -- compute the rights:
    SELECT  @ao_hasRights = (rights & @ai_requiredRights)
    FROM    ibs_RightsCum r
    WHERE   rKey = @ai_rKey
        AND userId = @ai_userId

    -- return the computed rights:
    RETURN @ao_hasRights
GO
-- p_Rights$checkRKeyRights


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
 *                              @hasRights == @requiredRights if the user has
 *                              all required rights.
 * @return  A value representing the state of the procedure.
 *  NOT_OK                      Any error occurred.
 *  ALL_RIGHT                   Action may be performed, everything ok.
 *  NOT_ALL                     The user does not have the rights on all
 *                              objects.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$checkRightsRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$checkRightsRec
(
    -- input parameters:
    @ai_posNoPath           POSNOPATH_VC,
    @ai_userId              USERID = 0,
    @ai_requiredRights      RIGHTS = 0xFFFFFFFF
    -- output parameters:
)
AS
DECLARE
    -- constants:
/* KR tuning: old version with separate owner values
    @c_OWNER                USERID,         -- user id of owner
*/
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_count                INT,            -- counter for regarded objects
    @l_countSuff            INT,            -- counter for objects with
                                            -- sufficient rights
    @l_countSuff1           INT,            -- counter 1
    @l_countSuff2           INT            -- counter 2


    -- assign constants:
SELECT
/* KR tuning: old version with separate owner values
    @c_OWNER                = 0x00900001,
*/
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2

    -- initialize local variables and return values:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_count = 0,
    @l_countSuff  = 0,
    @l_countSuff1 = 0,
    @l_countSuff2 = 0

-- body:
    -- check if a rights check shall be done:
    IF (@ai_requiredRights = 0)
    BEGIN
        -- return the original value:
        RETURN @c_ALL_RIGHT
    END -- if

    -- get the number of regarded objects:
    SELECT  @l_count = COUNT (oid)
    FROM    ibs_Object
    WHERE   posNoPath LIKE @ai_posNoPath + '%'
        AND state <> @c_STATE_DELETED

    -- get number of objects for which the user has sufficient rights:
    SELECT  @l_countSuff = COUNT (o.oid)
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.posNoPath LIKE @ai_posNoPath + '%'
        AND o.state <> @c_STATE_DELETED
        AND r.rKey = o.rKey
        AND r.userId = @ai_userId
        AND (r.rights & @ai_requiredRights) = @ai_requiredRights

/* KR tuning: old version with separate owner values
--------------------------
--------------------------
--------------------------
--
-- HP - Performance Tuning: split query in 2 parts
--
/ *
    -- get number of objects for which the user has sufficient rights:
    SELECT  @l_countSuff = COUNT (o.oid)
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.posNoPath LIKE @ai_posNoPath + '%'
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
* /
    -- query split in 2 part!
    -- userid <> owner
    SELECT  @l_countSuff1 = COUNT (o.oid)
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.posNoPath LIKE @ai_posNoPath + '%'
        AND o.state <> @c_STATE_DELETED
        AND r.rKey = o.rKey
        AND r.userId = @ai_userId
        AND o.owner <> @ai_userId
        AND (r.rights & @ai_requiredRights) = @ai_requiredRights

    SELECT  @l_countSuff2 = COUNT (o.oid)
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.posNoPath LIKE @ai_posNoPath + '%'
        AND o.state <> @c_STATE_DELETED
        AND r.rKey = o.rKey
        AND r.userId = @c_OWNER
        AND o.owner  = @ai_userId
        AND (r.rights & @ai_requiredRights) = @ai_requiredRights

    -- add results
    SELECT @l_countSuff = @l_countSuff1 + @l_countSuff2
--
-- HP - Tuning End
--
--------------------------
--------------------------
--------------------------
... KR tuning */


    -- check if there are any objects for which the user has insufficient
    -- rights:
    IF (@l_count <> @l_countSuff)   -- at least one object for which
                                    -- the user has insufficient rights?
    BEGIN
        IF (@l_countSuff = 0)       -- no object for which the user has
                                    -- sufficient rights?
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        ELSE                        -- the user has sufficient rights for at
                                    -- least one object
            SELECT  @l_retValue = @c_NOT_ALL
    END -- if at least one object ...

    -- return the computed state value:
    RETURN @l_retValue
GO
-- p_Rights$checkRightsRec


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$checkUserRightsRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$checkUserRightsRec
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              INT,
    @ai_requiredRights      INT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INTEGER,        -- counter
    @l_posNoPath            POSNOPATH_VC,   -- pos no path of the object
    @l_oid                  OBJECTID        -- the oid of the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_error                = 0,
    @l_count                = 0,
    @l_retValue             = @c_NOT_OK

-- body:
    -- conversions (OBJECTIDSTRING)
    -- all input objectids must be converted
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

    -- get the posNoPath of the actual object:
    SELECT  @l_posNoPath = posNoPath
    FROM    ibs_Object
    WHERE   oid = @l_oid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get posNoPath', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0 OR @l_count <= 0) -- an error occurred?
        GOTO exception              -- call common exception handler

    -- check if there are any objects for which the rights cannot be set:
    EXEC @l_retValue =
        p_Rights$checkRightsRec @l_posNoPath, @ai_userId, @ai_requiredRights

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Rights$checkUserRightsRec', @l_error,
            @l_ePos,
            N'ai_userId', @ai_userId,
            N'ai_oid_s', @ai_oid_s,
            N'ai_requiredRights', @ai_requiredRights
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Rights$checkUserRightsRec


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
 *                              @hasRights == @requiredRights if the user has
 *                              all required rights.
 * @return  A value representing the state of the procedure.
 *  NOT_OK                      Any error occurred.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$checkObjectRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$checkObjectRights
(
    -- input
    @ai_oid_s               OBJECTIDSTRING,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_userId              USERID = 0,
    @ai_requiredRights      RIGHTS = 0xFFFFFFFF,
    -- output
    @ao_hasRights           RIGHTS = 0 OUTPUT
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_oid                  OBJECTID,       -- oid of the object
    @l_containerId          OBJECTID        -- oid of the container

-- body:
    -- conversions (OBJECTIDSTRING)
    -- all input objectids must be converted
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT
    EXEC p_StringToByte @ai_containerId_s, @l_containerId OUTPUT

    -- call basic checkRights procedure:
    EXEC @ao_hasRights = p_Rights$checkRights
            @l_oid, @l_containerId, @ai_userid, @ai_requiredRights,
            @ao_hasRights

    -- return the computed rights:
    RETURN @ao_hasRights
GO
-- p_Rights$checkObjectRights


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$getRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$getRights
(
    -- input parameters:
    @ai_rOid                OBJECTID,
    @ai_rPersonId           INT,
    -- output parameters:
    @ao_rights              RIGHTS OUTPUT
)
AS
DECLARE
    -- constants:
    @c_RIGHTS_ALL           INT,            -- all rights together

    -- local variables:
    @l_containerId          OBJECTID        -- oid of the container

    -- assign constants:
SELECT
    @c_RIGHTS_ALL           = 0x7FFFFFFF

    -- initialize local variables and return values:
SELECT
    @ao_rights = 0

-- body:
    -- get container oid:
    SELECT  @l_containerId = containerId
    FROM    ibs_Object
    WHERE   oid = @ai_rOid

    -- get rights for this user:
    EXEC @ao_rights = p_Rights$checkRights
        @ai_rOid, @l_containerId, @ai_rPersonId, @c_RIGHTS_ALL,
        @ao_rights OUTPUT
GO
-- p_Rights$getRights


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$getRightsContainer'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$getRightsContainer
(
    -- input parameters:
    @ai_rOid_s              OBJECTIDSTRING,
    @ai_rPersonId           INT,
    -- output parameters:
    @ao_objectRights        RIGHTS OUTPUT,
    @ao_containerRights     RIGHTS OUTPUT,
    @ao_isContainer         BOOL OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_rOid                 OBJECTID,       -- oid of the rights object
    @l_containerId          OBJECTID        -- oid of the container

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
SELECT
    @ao_objectRights = 0,
    @ao_containerRights = 0,
    @ao_isContainer = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    -- convert string representation to binary representation:
    EXEC p_stringToByte @ai_rOid_s, @l_rOid OUTPUT

    -- get container oid:
    SELECT  @l_containerId = containerId, @ao_isContainer = isContainer
    FROM    ibs_Object
    WHERE   oid = @l_rOid

    -- root object is its own container:
    IF (@l_containerId = @c_NOOID)      -- object has no container?
        SELECT  @l_containerId = @l_rOid -- set container = object

    -- get rights for user on object:
    EXEC p_Rights$getRights @l_rOid, @ai_rPersonId,
            @ao_objectRights OUTPUT
    -- get rights for user on container:
    EXEC p_Rights$getRights @l_containerId, @ai_rPersonId,
            @ao_containerRights OUTPUT
GO
-- p_Rights$getRightsContainer


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$getKey'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$getKey
(
    -- input parameters:
    @ai_actId               ID,
    @ai_rPersonId           INT,
    @ai_rights              RIGHTS = 0,
    -- output parameters:
    @ao_id                  ID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_actRKeysId           ID,             -- id of old entry in ibs_RightsKeys
                                            -- table
    @l_rKeysId              ID,             -- id of entry in ibs_RightsKeys
                                            -- table
    @l_owner                USERID,         -- owner for rKey
    @l_cnt                  INT             -- counter

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
SELECT
    @ao_id = -1,
    @l_rKeysId = -1,
    @l_error = 0,
    @l_cnt = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$getKey

    -- get actual rKeysId and owner:
    SELECT  @l_actRKeysId = rKeysId, @l_owner = owner
    FROM    ibs_RightsKey
    WHERE   id = @ai_actId
    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- get old number of rights except those for the required person:
    SELECT  @l_cnt = COUNT (*)
    FROM    ibs_RightsKeys rks
    WHERE   rks.id = @l_actRKeysId
        AND rks.rPersonId <> @ai_rPersonId
    -- check if there was an error:
    IF (@@error <> 0)                   -- an error occurred?
        SELECT  @l_cnt = 0              -- ensure correct counter value

    -- check if there will be some rights left:
    IF (@l_cnt = 0 AND @ai_rights <= 0) -- there will be no rights left?
    BEGIN
        -- get the new rights key:
        EXEC p_Rights$getKeyForOwner 0, @l_owner, @ao_id OUTPUT
        RETURN                          -- terminate procedure
    END -- if there will be no rights left

    IF (@ai_rights > 0)                 -- there shall be some rights set?
    BEGIN
        SELECT  @l_cnt = @l_cnt + 1     -- the new right must also be counted

        -- check if there exists already a key for the new rights:
        SELECT  @l_rKeysId = MIN (id)
        FROM
        (
            SELECT  COUNT (*) AS cnt, rkc.id
            FROM    ibs_RightsKeys rkc,
                    (
                        SELECT  rPersonId, rights
                        FROM    ibs_RightsKeys
                        WHERE   id = @l_actRKeysId
                            AND rPersonId <> @ai_rPersonId
                        UNION
                        SELECT  @ai_rPersonId AS rPersonId,
                                @ai_rights AS rights
                    ) r
            WHERE   rkc.cnt = @l_cnt
                AND r.rPersonId = rkc.rPersonId
                AND r.rights = rkc.rights
            GROUP BY rkc.id
        ) res
        WHERE   cnt = @l_cnt

        -- check if there was an error:
        IF (@@error <> 0)               -- an error occurred?
            SELECT  @l_rKeysId = -1     -- set corresponding return value
    END -- if there shall be some rights set
    ELSE                                -- don't set rights
    BEGIN
        -- check if there exists already a key for the new rights and set return
        -- value:
        SELECT  @l_rKeysId = MIN (id)
        FROM
        (
            SELECT  COUNT (rkc.id) AS cnt, rkc.id
            FROM    ibs_RightsKeys rkc, ibs_rightsKeys r
            WHERE   rkc.cnt = @l_cnt
                AND r.id = @l_actRKeysId
                AND r.rPersonId <> @ai_rPersonId
                AND r.rPersonId = rkc.rPersonId
                AND r.rights = rkc.rights
            GROUP BY rkc.id
        ) res
        WHERE   cnt = @l_cnt

        -- check if there was an error:
        IF (@@error <> 0)               -- an error occurred?
            SELECT  @l_rKeysId = -1     -- set corresponding return value
    END -- else don't set rights

    -- ensure that the id has an allowed value:
/*
    IF (@l_rKeysId IS NULL)             -- no key found?
        SELECT  @l_rKeysId = -1         -- set value for not found
*/

/* first try
        -- check if there exists already a key for the new rights and set return
        -- value:
        SELECT  @id = id
        FROM
        (
            SELECT COUNT (*) AS cnt, rkc.id
            FROM    (
                        SELECT  *
                        FROM    ibs_RightsKeys
                        WHERE   cnt = @cnt
                    ) rkc
                    INNER JOIN
                    (
                        SELECT  *
                        FROM    (
                                    SELECT  rPersonId, rights
                                    FROM    ibs_RightsKeys
                                    WHERE   id = @actId
                                        AND rPersonId <> @rPersonId
                                    UNION
                                    SELECT  @rPersonId AS rPersonId, @rights AS rights
                                ) rk
                        WHERE   rights > 0
                    ) r
                    ON      r.rPersonId = rkc.rPersonId
                        AND r.rights = rkc.rights
            GROUP BY rkc.id
        ) res
        WHERE   cnt = @cnt
*/

    IF (@l_rKeysId = -1 OR @l_rKeysId IS NULL) -- no corresponding key found?
    BEGIN
        -- create a new rights key:
        IF (@ai_rights > 0)             -- there shall be some rights set?
        BEGIN
            INSERT INTO ibs_RightsKeys (id, rPersonId, rights, cnt,
                    r00, r01, r02, r03, r04, r05, r06, r07,
                    r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                    r10, r11, r12, r13, r14, r15, r16, r17,
                    r18, r19, r1A, r1B, r1C, r1D, r1E, r1F)
            (
                SELECT  -1, rPersonId, rights, @l_cnt,
                        rights & 0x00000001, rights & 0x00000002,
                        rights & 0x00000004, rights & 0x00000008,
                        rights & 0x00000010, rights & 0x00000020,
                        rights & 0x00000040, rights & 0x00000080,
                        rights & 0x00000100, rights & 0x00000200,
                        rights & 0x00000400, rights & 0x00000800,
                        rights & 0x00001000, rights & 0x00002000,
                        rights & 0x00004000, rights & 0x00008000,
                        rights & 0x00010000, rights & 0x00020000,
                        rights & 0x00040000, rights & 0x00080000,
                        rights & 0x00100000, rights & 0x00200000,
                        rights & 0x00400000, rights & 0x00800000,
                        rights & 0x01000000, rights & 0x02000000,
                        rights & 0x04000000, rights & 0x08000000,
                        rights & 0x10000000, rights & 0x20000000,
                        rights & 0x40000000, rights & 0x80000000
                FROM    ibs_RightsKeys
                WHERE   id = @l_actRKeysId
                    AND rPersonId <> @ai_rPersonId
                UNION
                SELECT  -1, @ai_rPersonId, @ai_rights, @l_cnt,
                        @ai_rights & 0x00000001, @ai_rights & 0x00000002,
                        @ai_rights & 0x00000004, @ai_rights & 0x00000008,
                        @ai_rights & 0x00000010, @ai_rights & 0x00000020,
                        @ai_rights & 0x00000040, @ai_rights & 0x00000080,
                        @ai_rights & 0x00000100, @ai_rights & 0x00000200,
                        @ai_rights & 0x00000400, @ai_rights & 0x00000800,
                        @ai_rights & 0x00001000, @ai_rights & 0x00002000,
                        @ai_rights & 0x00004000, @ai_rights & 0x00008000,
                        @ai_rights & 0x00010000, @ai_rights & 0x00020000,
                        @ai_rights & 0x00040000, @ai_rights & 0x00080000,
                        @ai_rights & 0x00100000, @ai_rights & 0x00200000,
                        @ai_rights & 0x00400000, @ai_rights & 0x00800000,
                        @ai_rights & 0x01000000, @ai_rights & 0x02000000,
                        @ai_rights & 0x04000000, @ai_rights & 0x08000000,
                        @ai_rights & 0x10000000, @ai_rights & 0x20000000,
                        @ai_rights & 0x40000000, @ai_rights & 0x80000000
            )

            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler
        END -- if there shall be some rights set
        ELSE                            -- don't set rights for actual person
        BEGIN
            INSERT INTO ibs_RightsKeys (id, rPersonId, rights, cnt,
                    r00, r01, r02, r03, r04, r05, r06, r07,
                    r08, r09, r0A, r0B, r0C, r0D, r0E, r0F,
                    r10, r11, r12, r13, r14, r15, r16, r17,
                    r18, r19, r1A, r1B, r1C, r1D, r1E, r1F)
            SELECT  -1, rPersonId, rights, @l_cnt,
                    rights & 0x00000001, rights & 0x00000002,
                    rights & 0x00000004, rights & 0x00000008,
                    rights & 0x00000010, rights & 0x00000020,
                    rights & 0x00000040, rights & 0x00000080,
                    rights & 0x00000100, rights & 0x00000200,
                    rights & 0x00000400, rights & 0x00000800,
                    rights & 0x00001000, rights & 0x00002000,
                    rights & 0x00004000, rights & 0x00008000,
                    rights & 0x00010000, rights & 0x00020000,
                    rights & 0x00040000, rights & 0x00080000,
                    rights & 0x00100000, rights & 0x00200000,
                    rights & 0x00400000, rights & 0x00800000,
                    rights & 0x01000000, rights & 0x02000000,
                    rights & 0x04000000, rights & 0x08000000,
                    rights & 0x10000000, rights & 0x20000000,
                    rights & 0x40000000, rights & 0x80000000
            FROM    ibs_RightsKeys
            WHERE   id = @l_actRKeysId
                AND rPersonId <> @ai_rPersonId

            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler
        END -- else don't set rights for actual person

        -- get the id of the newly generated key:
        SELECT  @l_rKeysId = MAX (id)
        FROM    ibs_RightsKeys

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- insert the corresponding values into ibs_RightsKey table:
        INSERT INTO ibs_RightsKey
                (rKeysId, owner, cnt)
        VALUES  (@l_rKeysId, @l_owner, @l_cnt)

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- get the id of the newly generated rKey:
        SELECT  @ao_id = SCOPE_IDENTITY ()

        -- call procedure: update cumulated rights for the actual key
        EXEC p_Rights$updateRightsCumKey @ao_id
    END -- if no corresponding key found
    ELSE                                -- corresponding key found
    BEGIN
        -- get the rKey id for the actual owner:
        SELECT  @ao_id = id
        FROM    ibs_RightsKey
        WHERE   rKeysId = @l_rKeysId
            AND owner = @l_owner

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- check if there exists already the necessary value
        -- for the actual owner:
        IF (@ao_id IS NULL OR @ao_id = -1) -- rKey not found?
        BEGIN
            -- create the new rKey:
            -- insert the corresponding values into ibs_RightsKey table:
            INSERT INTO ibs_RightsKey
                    (rKeysId, owner, cnt)
            VALUES  (@l_rKeysId, @l_owner, @l_cnt)

            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- get the id of the newly generated rKey:
            SELECT  @ao_id = SCOPE_IDENTITY ()

            -- call procedure: update cumulated rights for the actual key
            EXEC p_Rights$updateRightsCumKey @ao_id
        END -- if rKey not found

    END -- else corresponding key found

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$getKey
    -- set return value:
    SELECT  @ao_id = -1
GO
-- p_Rights$getKey


/******************************************************************************
 * Get a key being similar to an actual key. <BR>
 * The key returned is equal to the actual key in all parts except the owner
 * who is explicitly defined.
 * If there does not exist a corresponding key it is automatically created.
 *
 * @input parameters:
 * @param   ai_actId            Id of actual key for which to find a similar
 *                              one.
 *                              If the id is 0 we will search for a key having
 *                              rKeysId = 0 assigned.
 * @param   ai_owner            Id of the owner.
 *
 * @output parameters:
 * @param   ao_id               The id of the found key.
 *                              0 ..... there are no owner defined.
 *                              -1 .... no key was found and none could be
 *                                      generated.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                   Action performed, values returned, evrythng ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$getKeyForOwner'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$getKeyForOwner
(
    -- input parameters:
    @ai_actId               ID,
    @ai_owner               USERID,
    -- output parameters:
    @ao_id                  ID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_error                INT             -- the actual error code

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
SELECT
    @ao_id = -1,
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$getKeyForOwner

    -- check if there was an old key defined:
    IF (@ai_actId = 0)                  -- no key defined?
    BEGIN
        -- get the new rights key:
        SELECT  @ao_id = id
        FROM    ibs_RightsKey
        WHERE   rKeysId = 0
            AND owner = @ai_owner

        -- check if there was an error or no key found:
        IF (@@error <> 0 OR @@ROWCOUNT = 0 OR @ao_id IS NULL)
        BEGIN
            INSERT INTO ibs_RightsKey
                    (rKeysId, owner, cnt)
            VALUES  (0, @ai_owner, 0)

            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- get the id of the newly generated rKey:
            SELECT  @ao_id = SCOPE_IDENTITY ()

            -- call procedure: update cumulated rights for the actual key
            EXEC p_Rights$updateRightsCumKey @ao_id
        END -- if an error occurred or no corresponding key found
    END -- if no key defined
    ELSE                                -- key was defined
    BEGIN
        -- get the id of the rKey:
        SELECT  @ao_id = rk.id
        FROM    ibs_RightsKey rk, ibs_RightsKey actrk
        WHERE   actrk.id = @ai_actId
            AND actrk.rKeysId = rk.rKeysId
            AND rk.owner = @ai_owner

        -- check if there was an error or no key found:
        IF (@@error <> 0 OR @@ROWCOUNT = 0 OR @ao_id IS NULL)
        BEGIN
            -- create a new rights key within ibs_RightsKey table:
            INSERT INTO ibs_RightsKey
                    (rKeysId, owner, cnt)
            SELECT  actrk.rKeysId, @ai_owner, actrk.cnt
            FROM    ibs_RightsKey actrk
            WHERE   actrk.id = @ai_actId

            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- get the id of the newly generated rKey:
            SELECT  @ao_id = SCOPE_IDENTITY ()

            -- call procedure: update cumulated rights for the actual key
            EXEC p_Rights$updateRightsCumKey @ao_id
        END -- if an error occurred or no corresponding key found
    END -- else key was defined

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$getKeyForOwner
    -- set return value:
    SELECT  @ao_id = -1
GO
-- p_Rights$getKeyForOwner


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$getRightsKey'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$getRightsKey
(
    -- input parameters:
    @ai_oid                 OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOT_FOUND            INT,            -- key not found

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_rKey                 ID              -- the found key

    -- assign constants:
SELECT
    @c_NOT_FOUND            = -1

    -- initialize local variables and return values:
SELECT
    @l_error = 0,
    @l_rKey = @c_NOT_FOUND

-- body:
    -- get the rights key of the object:
    SELECT  @l_rKey = rKey
    FROM    ibs_Object
    WHERE   oid = @ai_oid

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- return the computed value:
    RETURN  @l_rKey

exception:
    -- return error value:
    RETURN @c_NOT_FOUND
GO
-- p_Rights$getRightsKey


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$reuseKey'
GO

/* KR tuning: this procedure is never used.
-- create the new procedure:
CREATE PROCEDURE p_Rights$reuseKey
(
    -- input parameters:
    @ai_id                  INTEGER,
    @ai_replId              INTEGER,
    -- output parameters:
    @ao_newId               INTEGER OUTPUT
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_error                INTEGER,        -- the actual error code
    @l_count                INTEGER,
    @l_mcount               INTEGER

    -- initialize local variables and return values:
SELECT
    @ao_newId = @ai_replId,
    @l_error = 0,
    @l_count = 0,
    @l_mcount = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$reuseKey

    -- check if the old key is not used by another object and is not a business
    -- object itself:
    IF (@ai_id > 0)                     -- the old id is valid?
    BEGIN
        -- count how many objects use this key:
        SELECT  @l_count = COUNT (*)
        FROM    ibs_Object
        WHERE   rKey = @ai_id

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- check if the rights key is not an object itself:
        -- (a value > 0 says that it is an object)
        SELECT  @l_mcount = COUNT (o.oid)
        FROM    ibs_RightsKeys r, ibs_Object o
        WHERE   rKey = @ai_id
            AND r.oid = o.oid

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        IF (@l_count = 0) AND (@l_mcount <= 0)
                                        -- the key is not longer used and is
                                        -- not an object?
        BEGIN
            -- delete the old key:
            DELETE  ibs_RightsKeys
            WHERE   id = @ai_id
            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- delete all cumulated rights for this key:
            DELETE  ibs_RightsCum
            WHERE   rKey = @ai_id
            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- reuse the old id for the new key:
            UPDATE  ibs_RightsKeys
            SET     id = @ai_id
            WHERE   id = @ai_replId
            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- set the key within the cumulated rights:
            UPDATE  ibs_RightsCum
            SET     rKey = @ai_id
            WHERE   rKey = @ai_replId
            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- store the key id within all objects which use this key:
            UPDATE  ibs_Object
            SET     rKey = @ai_id
            WHERE   rKey = @ai_replId
            -- check if there occurred an error:
            SELECT @l_error = @@error   -- store the error code
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- set the id:
            SELECT  @ao_newId = @ai_id
        END -- if the key is not used and is not an object
    END -- if the old id was valid

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$reuseKey
    -- set return value:
    SELECT  @ao_newId = @ai_replId
... KR tuning */
GO
-- p_Rights$reuseKey


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$setKey'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$setKey
(
    -- input parameters:
    @ai_rOid                OBJECTID = 0,
    @ai_rKey                INT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_ParticipantContainer INT,            -- tVersionId of
                                            -- ParticipantContainer
    @c_Reference            INT,            -- tVersionId of Reference

    -- local variables:
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_ParticipantContainer = 16850945, -- 0x01012001
    @c_Reference            = 16842801  -- 0x01010031

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- set rights key of the actual object and its tabs,
    -- which do not already have this rKey:
    UPDATE  ibs_Object
    SET     rKey = @ai_rKey
    WHERE   oid = @ai_rOid
        OR  (
                containerId = @ai_rOid
            AND containerKind = 2
--! HACK ...
            -- don't propagate rights to some specific object types:
            AND tVersionId <> @c_ParticipantContainer
            AND tVersionId <> @c_Reference
--! ... HACK
            )
        AND rKey <> @ai_rKey
/* KR performance tuning: Already done in Trigger
        OR  (linkedObjectId IN
                (SELECT oid
                FROM    ibs_Object
                WHERE   oid = @ai_rOid
                    OR  (containerId = @ai_rOid
                        AND containerKind = 2
--! HACK ...
                        -- don't propagate rights to some specific object types:
                        AND tVersionId <> @c_ParticipantContainer
                        AND tVersionId <> @c_Reference
--! ... HACK
                        )
                )
            AND state <> @c_STATE_DELETED
            )
... KR performance tuning */

    -- check if there occurred an error:
    IF (@@error <> 0)                   -- an error occurred?
        SELECT  @l_retValue = @c_NOT_OK -- set corresponding return value

    -- return the computed state value:
    RETURN @l_retValue
GO
-- p_Rights$setKey


/******************************************************************************
 * Set the rights key for a specific object and all of its sub objects. <BR>
 * This procedure sets the rights only for those objects that have the same
 * old rKey.
 * The rights keys of the references to the changed objects are changed, too.
 * There is no rights check done.
 *
 * @input parameters:
 * @param   ai_rOid             Oid of the object for which to set the rKey.
 * @param   ai_posNoPath        The pos no path of the object.
 *                              If this is null it is computed out of the
 *                              object.
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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$setKeyRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$setKeyRec
(
    -- input parameters:
    @ai_rOid                OBJECTID = 0,
    @ai_posNoPath           POSNOPATH_VC,
    @ai_oldRKey             INT,
    @ai_rKey                INT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_EMPTYPOSNOPATH       POSNOPATH_VC,   -- default/invalid posNoPath
    @c_ParticipantContainer INT,            -- tVersionId of
                                            -- ParticipantContainer
    @c_Reference            INT,            -- tVersionId of Reference

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_posNoPath            POSNOPATH_VC    -- the pos no path of the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_EMPTYPOSNOPATH       = '0000',
    @c_ParticipantContainer = 16850945, -- 0x01012001
    @c_Reference            = 16842801 -- 0x01010031

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_posNoPath = @l_posNoPath

-- body:
    -- check if the rKey has to be changed:
    IF (@ai_rKey = @ai_oldRKey)         -- nothing to do?
    BEGIN
        RETURN @c_ALL_RIGHT
    END -- if nothing to do

    -- check if we have a posNoPath:
    IF (@l_posNoPath IS NULL)
    BEGIN
        -- get posNoPath of actual object:
        SELECT  @l_posNoPath = posNoPath
        FROM    ibs_Object
        WHERE   oid = @ai_rOid
    END -- if

    -- check if there occurred an error:
    IF (@@error <> 0)                   -- an error occurred?
        SELECT  @l_retValue = @c_NOT_OK -- set corresponding return value
    ELSE                                -- no error
    BEGIN
        -- set rights key of the actual object and its subsequent objects
        -- having the same old rights key:
        UPDATE  ibs_Object
        SET     rKey = @ai_rKey
        WHERE   posNoPath LIKE @l_posNoPath + '%'
            AND state <> @c_STATE_DELETED
            AND posNoPath <> @c_EMPTYPOSNOPATH
            AND rKey = @ai_oldRKey
            -- don't propagate rights to some specific object types:
            AND (
                    oid = @ai_rOid
--! HACK ...
                    OR  (
                                tVersionId <> @c_ParticipantContainer
                            AND tVersionId <> @c_Reference
                        )
--! ... HACK
                )
/* KR performance tuning: Already done in Trigger
        UPDATE  ibs_Object
        SET     rKey = @ai_rKey
        WHERE   (posNoPath LIKE @l_posNoPath + '%'
                AND state <> @c_STATE_DELETED
                AND posNoPath <> @c_EMPTYPOSNOPATH
                AND rKey = @ai_oldRKey
                -- don't propagate rights to some specific object types:
                AND (oid = @ai_rOid
--! HACK ...
                    OR  (
                            tVersionId <> @c_ParticipantContainer
                        AND tVersionId <> @c_Reference
                        )
--! ... HACK
                    )
                )
            OR  (linkedObjectId IN
                    (SELECT oid
                    FROM    ibs_Object
                    WHERE   posNoPath LIKE @l_posNoPath + '%'
                        AND state <> @c_STATE_DELETED
                        AND posNoPath <> @c_EMPTYPOSNOPATH
                        AND rKey = @ai_oldRKey
                        -- don't propagate rights to some specific object
                        -- types:
                        AND (oid = @ai_rOid
--! HACK ...
                            OR  (
                                    tVersionId <> @c_ParticipantContainer
                                AND tVersionId <> @c_Reference
                                )
--! ... HACK
                            )
                    )
                AND state <> @c_STATE_DELETED
                )
... KR performance tuning */

        -- check if there occurred an error:
        IF (@@error <> 0)               -- an error occurred?
            SELECT  @l_retValue = @c_NOT_OK -- set corresponding return value
    END -- else no error

    -- return the computed state value:
    RETURN @l_retValue
GO
-- p_Rights$setKeyRec


/******************************************************************************
 * Set the rights key for a specific object and all of its sub objects. <BR>
 * This procedure ensures that there is not only the current rKey set for the
 * object and all objects below, but it also sets the correct rKey for all these
 * objects. (incl. owner behaviour).
 * There is no rights check done.
 *
 * @input parameters:
 * @param   ai_rOid             Oid of the object for which to set the rKey.
 * @param   ai_rKey             Id of the rights key.
 * @param   ai_owner            The owner of the new rKey.
 *                              If this value is 0 the procedure tries to
 *                              get it out of the rKey itself.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  NOT_OK                  Something went wrong during performing the
 *                          operation.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$setKeyForAllRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$setKeyForAllRec
(
    -- input parameters:
    @ai_rOid                OBJECTID = 0,
    @ai_rKey                INT,
    @ai_owner               USERID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_EMPTYPOSNOPATH       POSNOPATH_VC,   -- default/invalid posNoPath
    @c_ParticipantContainer INT,            -- tVersionId of
                                            -- ParticipantContainer
    @c_Reference            INT,            -- tVersionId of Reference

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the object
    @l_oldRKey              INT,            -- id of the old rights key
    @l_newRKey              INT,            -- id of the new rights key
    @l_owner                USERID,         -- the owner of the object
--    @l_rKey                 ID,             -- actual rKey to be set
    @l_oLevel               INT,            -- oLevel of act object
    @l_rKeysId              ID,             -- rKeysId of rKey to be settt
    @l_rKeyOwner            USERID          -- the owner of the rKey to be set

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_EMPTYPOSNOPATH       = '0000',
    @c_ParticipantContainer = 16850945, -- 0x01012001
    @c_Reference            = 16842801 -- 0x01010031

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rKeyOwner = @ai_owner,
    @l_rKeysId = 0

-- body:
    -- check if we have to get the owner of the rKey
    IF (@l_rKeyOwner = 0 AND @ai_rKey <> 0)
    BEGIN
        -- get the owner out of the rKey:
        SELECT  @l_rKeyOwner = owner, @l_rKeysId = rKeysId
        FROM    ibs_RightsKey
        WHERE   id = @ai_rKey
    END -- if

    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$setKeyForAllRec

    -- get posNoPath of actual object:
    SELECT  @l_posNoPath = posNoPath, @l_oLevel = oLevel
    FROM    ibs_Object
    WHERE   oid = @ai_rOid

    -- check if there occurred an error and the object exists:
    IF (@@error <> 0 OR @@ROWCOUNT = 0) -- an error occurred?
        GOTO exception              -- call exception handler

    -- set rights key of the actual object and its subsequent objects
    -- having the same owner:
/* KR does not bring anything
    UPDATE  ibs_Object
    SET     rKey = @ai_rKey
    WHERE   oid = @ai_rOid
        AND state = @c_STATE_ACTIVE
        AND owner = @l_rKeyOwner

    UPDATE  ibs_Object
    SET     rKey = @ai_rKey
    WHERE   posNoPath LIKE @l_posNoPath + '%'
        AND state = @c_STATE_ACTIVE
        AND posNoPath <> @c_EMPTYPOSNOPATH
        AND owner = @l_rKeyOwner
        -- don't propagate rights to some specific object types:
--! HACK ...
        AND tVersionId <> @c_ParticipantContainer
        AND tVersionId <> @c_Reference
--! ... HACK
*/

    -- define cursor:
    -- get all rkeys and owners of the objects below which have not already the
    -- new rKey set:
-- KR 20050304: Use FAST_FORWARD (FORWARD_ONLY, READ_ONLY) to improve
--              performance
--    DECLARE updateCursor CURSOR FAST_FORWARD FOR
    DECLARE updateCursor CURSOR FOR
        SELECT  DISTINCT o.rkey, o.owner
        FROM    ibs_Object o, ibs_RightsKey rk
        WHERE   rk.rKeysId <> @l_rKeysId
            AND o.rKey = rk.id
            AND o.oLevel >= @l_oLevel
            AND o.state <> @c_STATE_DELETED
            AND o.rKey <> @ai_rKey
            AND o.posNoPath LIKE @l_posNoPath + '%'
--            AND o.posNoPath <> @c_EMPTYPOSNOPATH

    -- open the cursor:
    OPEN    updateCursor

    -- get the first object:
    FETCH NEXT FROM updateCursor INTO @l_oldRKey, @l_owner

    -- loop through all objects:
    WHILE (@l_retValue = @c_ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                    -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
            IF (@l_rKeyOwner <> @l_owner)
            BEGIN
                -- get the key containing the resulting rights:
                EXEC p_Rights$getKeyForOwner @ai_rKey, @l_owner,
                                             @l_newRKey OUTPUT
            END -- if
            ELSE
            BEGIN
                SELECT @l_newRKey = @ai_rKey
            END -- else

            -- check if there was an error:
            IF (@l_newRKey = -1)        -- an error occurred?
            BEGIN
                SELECT  @l_retValue = @c_NOT_OK -- set return value
            END -- if
            ELSE                        -- no error
            BEGIN
                -- check if the key has to be changed:
                IF (@l_newRKey <> @l_oldRKey)
                BEGIN
                    -- set rights key of all subsequent objects of the actual
                    -- object with the same old key:
                    EXEC @l_retValue =
                        p_Rights$setKeyRec
                            @ai_rOid, @l_posNoPath, @l_oldRKey, @l_newRKey
                END -- if
            END -- else no error
        END -- if
        -- get next tuple:
        FETCH NEXT FROM updateCursor INTO @l_oldRKey, @l_owner
    END -- while another tuple found

    -- close the not longer needed cursor:
    CLOSE updateCursor
    DEALLOCATE updateCursor

    -- finish the procedure, return result:
    RETURN @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$setKeyForAllRec

    -- finish the procedure, return result:
    RETURN @c_NOT_OK
GO
-- p_Rights$setKeyForAllRec


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$setRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$setRights
(
    -- input parameters:
    @ai_rOid                OBJECTID = 0,
    @ai_rPersonId           INT,
    @ai_rights              RIGHTS = 0,
    @ai_rec                 BOOL = 0
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_EMPTYPOSNOPATH       POSNOPATH_VC,   -- default/invalid posNoPath
    @c_NOOID                OBJECTID,       -- default value for no defined oid

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the object
    @l_oldId                ID,             -- the old rights key
    @l_id                   ID              -- the new rights key

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_EMPTYPOSNOPATH       = '0000',
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_id = 0

-- body:
    -- check if there was a valid oid defined:
    IF (@ai_rOid = @c_NOOID)            -- no valid oid?
    BEGIN
        -- nothing to do, terminate procedure:
        RETURN
    END -- no valid oid

    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$setRights

    IF (@ai_rec = 1)                    -- set rights for all objects with
                                        -- same path prefix?
    BEGIN
        -- get posNoPath of actual object:
        SELECT  @l_posNoPath = posNoPath
        FROM    ibs_Object
        WHERE   oid = @ai_rOid

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- define cursor:
        DECLARE updateCursor CURSOR FOR
            SELECT  DISTINCT rKey
            FROM    ibs_Object
            WHERE   posNoPath LIKE @l_posNoPath + '%'
                AND state <> @c_STATE_DELETED
                AND posNoPath <> @c_EMPTYPOSNOPATH

        -- open the cursor:
        OPEN    updateCursor

        -- get the first object:
        FETCH NEXT FROM updateCursor INTO @l_oldId

        -- loop through all objects:
        WHILE (@l_retValue = @c_ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                        -- another object found?
        BEGIN
            -- Because @@FETCH_STATUS may have one of the three values
            -- -2, -1, or 0 all of these cases must be checked.
            -- In this case the tuple is skipped if it was deleted during
            -- the execution of this procedure.
            IF (@@FETCH_STATUS <> -2)
            BEGIN
                -- get the key containing the resulting rights:
                EXEC p_Rights$getKey @l_oldId, @ai_rPersonId, @ai_rights,
                        @l_id OUTPUT

                -- check if there was an error:
                IF (@l_id = -1)         -- an error occurred?
                    SELECT  @l_retValue = @c_NOT_OK -- set return value
                ELSE                    -- no error
                    -- set rights key of all subsequent objects of the actual
                    -- object with the same old key:
                    EXEC @l_retValue =
                        p_Rights$setKeyRec
                            @ai_rOid, @l_posNoPath, @l_oldId, @l_id
            END -- if
            -- get next tuple:
            FETCH NEXT FROM updateCursor INTO @l_oldId
        END -- while another tuple found

        -- close the not longer needed cursor:
        CLOSE updateCursor
        DEALLOCATE updateCursor
    END -- if set rights for all objects with  same path prefix
    ELSE                                -- set rights only for given object
    BEGIN
        -- get the old rights key:
        SELECT  @l_oldId = rKey
        FROM    ibs_Object
        WHERE   oid = @ai_rOid

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- get the new rights key:
        EXEC p_Rights$getKey @l_oldId, @ai_rPersonId, @ai_rights, @l_id OUTPUT

        -- check if there was an error:
        IF (@l_id = -1)                 -- an error occurred?
            SELECT  @l_retValue = @c_NOT_OK -- set return value
        ELSE                            -- no error
            -- set rights key of the actual object:
            EXEC p_Rights$setKey @ai_rOid, @l_id
    END -- else set rights only for given object

    -- check if there occurred an error:
    IF (@l_retValue <> @c_ALL_RIGHT)    -- an error occurred?
        GOTO exception                  -- call exception handler

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$setRights
GO
-- p_Rights$setRights


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$addRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$addRights
(
    -- input parameters:
    @ai_rOid                OBJECTID = 0,
    @ai_rPersonId           INT,
    @ai_rights              RIGHTS = 0,
    @ai_rec                 BOOL = 0
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_EMPTYPOSNOPATH       POSNOPATH_VC,   -- default/invalid posNoPath

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the object
    @l_oldId                ID,             -- the old rights key
    @l_id                   ID,             -- the new rights key
    @l_newRights            RIGHTS          -- the new rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_EMPTYPOSNOPATH       = '0000'

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_id = 0,
    @l_newRights = @ai_rights

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$addRights

    IF (@ai_rec = 1)                    -- set rights for all objects with
                                        -- same path prefix?
    BEGIN
        -- get posNoPath of actual object:
        SELECT  @l_posNoPath = posNoPath
        FROM    ibs_Object
        WHERE   oid = @ai_rOid

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- define cursor:
        DECLARE updateCursor CURSOR FOR
            SELECT  DISTINCT rKey
            FROM    ibs_Object
            WHERE   posNoPath LIKE @l_posNoPath + '%'
                AND state <> @c_STATE_DELETED
                AND posNoPath <> @c_EMPTYPOSNOPATH

        -- open the cursor:
        OPEN    updateCursor

        -- get the first object:
        FETCH NEXT FROM updateCursor INTO @l_oldId

        -- loop through all objects:
        WHILE (@l_retValue = @c_ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                        -- another object found?
        BEGIN
            -- Because @@FETCH_STATUS may have one of the three values
            -- -2, -1, or 0 all of these cases must be checked.
            -- In this case the tuple is skipped if it was deleted during
            -- the execution of this procedure.
            IF (@@FETCH_STATUS <> -2)
            BEGIN
                -- initialize the new rights:
                SELECT  @l_newRights = @ai_rights

                -- get resulting rights:
                -- compute the resulting rights as binary or
                -- (= bitwise sum) of the old and the new rights:
                -- (if there are no rights set for the rPerson the value of
                -- l_newRights stays as initialized => l_newRights = ai_rights)
                SELECT  @l_newRights = rks.rights | @ai_rights
                FROM    ibs_RightsKeys rks, ibs_RightsKey rk
                WHERE   rk.id = @l_oldId
                    AND rks.id = rk.rKeysId
                    AND rks.rPersonId = @ai_rPersonId

                -- get the key containing the resulting rights:
                EXEC p_Rights$getKey @l_oldId, @ai_rPersonId, @l_newRights,
                        @l_id OUTPUT

                -- check if there was an error:
                IF (@l_id = -1)         -- an error occurred?
                    SELECT  @l_retValue = @c_NOT_OK -- set return value
                ELSE                    -- no error
                    -- set rights key of all subsequent objects of the actual
                    -- object:
                    EXEC @l_retValue =
                        p_Rights$setKeyRec
                            @ai_rOid, @l_posNoPath, @l_oldId, @l_id
            END -- if
            -- get next tuple:
            FETCH NEXT FROM updateCursor INTO @l_oldId
        END -- while another tuple found

        -- close the not longer needed cursor:
        CLOSE updateCursor
        DEALLOCATE updateCursor
    END -- if set rights for all objects with  same path prefix
    ELSE                                -- set rights only for given object
    BEGIN
        -- get the old rights key:
        SELECT  @l_oldId = rKey
        FROM    ibs_Object
        WHERE   oid = @ai_rOid

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler

        -- get resulting rights:
        -- compute the resulting rights as binary or
        -- (= bitwise sum) of the old and the new rights:
        -- (if there are no rights set for the rPerson the value of
        -- l_newRights stays as initialized => l_newRights = ai_rights)
        SELECT  @l_newRights = rks.rights | @ai_rights
        FROM    ibs_RightsKeys rks, ibs_RightsKey rk
        WHERE   rk.id = @l_oldId
            AND rk.rKeysId = rks.id
            AND rks.rPersonId = @ai_rPersonId

        -- get the key containing the resulting rights:
        EXEC p_Rights$getKey @l_oldId, @ai_rPersonId, @l_newRights, @l_id OUTPUT

        -- check if there was an error:
        IF (@l_id = -1)                 -- an error occurred?
            SELECT  @l_retValue = @c_NOT_OK -- set return value
        ELSE                            -- no error
            -- set rights key of the actual object:
            EXEC p_Rights$setKey @ai_rOid, @l_id
    END -- else set rights only for given object

    -- check if there occurred an error:
    IF (@l_retValue <> @c_ALL_RIGHT)    -- an error occurred?
        GOTO exception                  -- call exception handler

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$addRights
GO
-- p_Rights$addRights


/******************************************************************************
 * Propagate the rights of one object to another object. <BR>
 * After this operation the secondary object has the same rights as the primary
 * one.
 *
 * @input parameters:
 * @param   ai_masterOid    Oid of the object which is the master for this
 *                          operation.
 * @param   ai_rOid         Oid of the object on which rights are defined.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$propagateRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$propagateRights
(
    -- input parameters:
    @ai_masterOid           OBJECTID,
    @ai_rOid                OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_masterRKey           ID,             -- rights key of master object
    @l_rKey                 ID,             -- the actual rights key
    @l_owner                USERID          -- the owner of the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rKey = -1

-- body:
    -- check if the objects are different:
    IF (@ai_masterOid = @ai_rOid)       -- nothing to do?
    BEGIN
        -- finish procedure:
        RETURN
    END -- if nothing to do

    -- get the rights key of the master object:
    SELECT  @l_masterRKey = rKey
    FROm    ibs_Object
    WHERE   oid = @ai_masterOid

    -- get the owner of the object for which to set the rights:
    SELECT  @l_owner = owner, @l_rKey = rKey
    FROm    ibs_Object
    WHERE   oid = @ai_rOid

    -- check if the rKeys are different:
    IF (@l_masterRKey = @l_rKey)        -- nothing to do?
    BEGIN
        -- finish procedure:
        RETURN
    END -- if nothing to do

    -- get the rights key of the master object:
    EXEC p_Rights$getKeyForOwner @l_masterRKey, @l_owner, @l_rKey OUTPUT

    -- check if the rKeys are different:
    IF (@l_masterRKey = @l_rKey)        -- nothing to do?
    BEGIN
        -- finish procedure:
        RETURN
    END -- if nothing to do

    -- check if there was an error:
    IF (@l_rKey = -1)               -- an error occurred?
        SELECT  @l_retValue = @c_NOT_OK -- set return value
    ELSE                            -- no error
        -- set the rights key of the secondary object:
        EXEC p_Rights$setKey @ai_rOid, @l_rKey
GO
-- p_Rights$propagateRights


/******************************************************************************
 * Propagate the rights of one object to another object. <BR>
 * After this operation the secondary object and its sub objects have the same
 * rights as the primary one.
 *
 * @input parameters:
 * @param   ai_masterOid    Oid of the object which is the master for this
 *                          operation.
 * @param   ai_rOid         Oid of the object on which rights are defined.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$propagateRightsRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$propagateRightsRec
(
    -- common input parameters:
    @ai_masterOid           OBJECTID,
    @ai_rOid                OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_rKey                 ID,             -- actual rKey to be set
    @l_masterOwner          USERID          -- the owner of the master object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$propagateRightsRec

    -- get the rkey and owner of the master object:
    SELECT @l_rKey = rKey, @l_masterOwner = owner
    FROM   ibs_Object
    WHERE  oid = @ai_masterOid

    -- check if there occurred an error and the object exists:
    IF (@@error <> 0 OR @@ROWCOUNT = 0) -- an error occurred?
        GOTO exception              -- call exception handler

    EXEC @l_retValue =
        p_Rights$setKeyForAllRec @ai_rOid, @l_rKey, @l_masterOwner

    -- finish the procedure, return result:
    RETURN @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$propagateRightsRec

    -- finish the procedure, return result:
    RETURN @c_NOT_OK
GO
-- p_Rights$propagateRightsRec


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$propagateUserRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$propagateUserRights
(
    -- input parameters:
    @ai_masterOid           OBJECTID,
    @ai_rOid                OBJECTID,
    @ai_rPersonId           PERSONID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_id                   ID,             -- the actual rights key
    @l_oldId                ID,
    @l_rights               RIGHTS

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_id = 0,
    @l_oldId = 0,
    @l_rights = 0

-- body:
    -- get the old rights key:
    SELECT  @l_oldId = rKey
    FROM    ibs_Object
    WHERE   oid = @ai_rOid

    -- check if there was an error:
    IF (@@error <> 0)                   -- an error occurred?
        SELECT  @l_oldId = 0            -- set default value

    -- get the rights to be set:
    SELECT  @l_rights = rights
    FROM    ibs_RightsKeys rks, ibs_RightsKey rk, ibs_Object o
    WHERE   o.oid = @ai_masterOid
        AND o.rKey = rk.id
        AND rk.rKeysId = rks.id
        AND rks.rPersonId = @ai_rPersonId

    -- check if there was an error:
    IF (@@error <> 0)                   -- an error occurred?
        SELECT  @l_rights = 0           -- set default value

    -- get the new rights key:
    EXEC p_Rights$getKey @l_oldId, @ai_rPersonId, @l_rights, @l_id OUTPUT

    -- check if there was an error:
    IF (@l_id = -1)                     -- an error occurred?
        SELECT  @l_retValue = @c_NOT_OK -- set return value
    ELSE                                -- no error
        -- update the rights key of the object:
        EXEC p_Rights$setKey @ai_rOid, @l_id
GO
-- p_Rights$propagateUserRights


/******************************************************************************
 * Delete all rights of a given object. <BR>
 *
 * @input parameters:
 * @param   ai_oid              ID of the object for which to delete the rights.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$deleteObjectRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$deleteObjectRights
(
    -- common input parameters:
    @ai_oid            OBJECTID
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_newRKey              INT,            -- id of the new rights key
    @l_owner                USERID          -- owner of object

    -- assign constants:

    -- initialize local variables:
SELECT
    @l_newRKey = 0

-- body:
    -- get the owner:
    SELECT  @l_owner = owner
    FROM    ibs_Object
    WHERE   oid = @ai_oid

    -- get the new rights key:
    EXEC p_Rights$getKeyForOwner 0, @l_owner,
                                 @l_newRKey OUTPUT

    -- delete all rights defined on the object:
    EXEC p_Rights$setKey @ai_oid, @l_newRKey

    -- finish the procedure:
    RETURN
GO
-- p_Rights$deleteObjectRights


/******************************************************************************
 * Delete all rights of a given object and all sub objects. <BR>
 *
 * @input parameters:
 * @param   ai_oid              ID of the object for which to delete the rights.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$deleteObjectRightsRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$deleteObjectRightsRec
(
    -- common input parameters:
    @ai_oid            OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT             -- the actual error code

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
--    SAVE TRANSACTION s_Rights$deleteObjectRightsRec

    EXEC p_Rights$setKeyForAllRec @ai_oid, 0, 0

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
--    ROLLBACK TRANSACTION s_Rights$deleteObjectRightsRec
GO
-- p_Rights$deleteObjectRightsRec


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$set'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$set
(
    -- input parameters:
    @ai_rOid                OBJECTID,
    @ai_rPersonId           INT,
    @ai_rights              RIGHTS,
    @ai_operation           INT
)
AS
    IF (@ai_operation = 1)              -- new?
    BEGIN
        EXEC p_Rights$setRights @ai_rOid, @ai_rPersonId, @ai_rights, 0
    END -- if new
    ELSE IF (@ai_operation = 2)         -- delete all rights of person on obj.?
    BEGIN
        EXEC p_Rights$setRights @ai_rOid, @ai_rPersonId, 0, 0
    END -- else if delete all rights of person on obj.
    ELSE IF (@ai_operation = 3)         -- delete all rights of object?
    BEGIN
        EXEC p_Rights$deleteObjectRights @ai_rOid
    END -- else if delete all rights of object
    ELSE IF (@ai_operation = 4)         -- add rights to actual ones?
    BEGIN
        EXEC p_Rights$addRights @ai_rOid, @ai_rPersonId, @ai_rights, 0
    END -- else if add rights to actual ones
GO
-- p_Rights$set


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$setRightsRecursive'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$setRightsRecursive
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              INT,
    @ai_op                  INT
)
AS
DECLARE
    -- constants:
    @c_OWNER                USERID,         -- user id of owner
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_ParticipantContainer INT,            -- tVersionId of
                                            -- ParticipantContainer
    @c_Reference            INT,            -- tVersionId of Reference

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_oid                  OBJECTID,       -- the object id of the act. object
    @l_posNoPath            POSNOPATH_VC,   -- pos no path of the object
    @l_rKey                 ID,             -- rKey of the object
    @l_oldRKey              ID,             -- old rKey to be overwritten
    @l_newRKey              ID,             -- new rKey to be set
    @l_owner                USERID,         -- owner for which to get the rKey
    @l_count                INT             -- counter

    -- assign constants:
SELECT
    @c_OWNER                = 0x00900001,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_ParticipantContainer = 16850945, -- 0x01012001
    @c_Reference            = 16842801  -- 0x01010031

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rKey = 0,
    @l_count = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

    BEGIN TRANSACTION
        -- get posNoPath and rights key of actual object:
        SELECT  @l_posNoPath = posNoPath, @l_rKey = rKey
        FROM    ibs_Object
        WHERE   oid = @l_oid

        IF (@@error <> 0 OR @@ROWCOUNT = 0) -- the object was not found?
            GOTO exception              -- call exception handler

        -- check if there are any objects for which the rights cannot be set:
        EXEC @l_retValue =
            p_Rights$checkRightsRec @l_posNoPath, @ai_userId, @ai_op

        IF (@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_NOT_ALL)
                                        -- rights can be set for >= one object?
        BEGIN
            IF (@l_retValue = @c_ALL_RIGHT)
                                        -- rights can be set for all objects?
            BEGIN
                -- define cursor:
                DECLARE updateCursor CURSOR FOR
                    -- get the rKeys to be changed:
                    SELECT  DISTINCT o.rKey, o.owner
                    FROM    ibs_Object o
                    WHERE   o.posNoPath LIKE @l_posNoPath + '%'
                        AND o.rKey <> @l_rKey
                        AND o.state <> @c_STATE_DELETED
            END -- if rights can be set for all objects
            ELSE                        -- rights can be set for >= one object?
            BEGIN
                -- define cursor:
                DECLARE updateCursor CURSOR FOR
                    -- get the rKeys to be changed:
                    SELECT  DISTINCT o.rKey, o.owner
                    FROM    ibs_Object o, ibs_RightsCum r
                    WHERE   o.posNoPath LIKE @l_posNoPath + '%'
                        AND o.rKey <> @l_rKey
                        AND r.rKey = o.rKey
                        AND r.userId = @ai_userId
                        AND (r.rights & @ai_op) = @ai_op
                        AND o.state <> @c_STATE_DELETED
            END -- else rights can be set for >= one object

            -- open the cursor:
            OPEN    updateCursor

            -- get the first object:
            FETCH NEXT FROM updateCursor INTO @l_oldRKey, @l_owner

            -- loop through all objects:
            WHILE (@l_retValue = @c_ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                            -- another object found?
            BEGIN
                -- Because @@FETCH_STATUS may have one of the three values
                -- -2, -1, or 0 all of these cases must be checked.
                -- In this case the tuple is skipped if it was deleted during
                -- the execution of this procedure.
                IF (@@FETCH_STATUS <> -2)
                BEGIN
                    -- get the new rKey:
                    EXEC p_Rights$getKeyForOwner @l_rKey, @l_owner,
                                                 @l_newRKey OUTPUT

                    -- set the rights key for all relevant objects:
                    UPDATE  ibs_Object
                    SET     rKey = @l_newRKey
                    WHERE   posNoPath LIKE @l_posNoPath + '%'
                        AND rKey = @l_oldRKey
                        -- don't propagate rights to some specific object types:
--! HACK ...
                        AND tVersionId <> @c_ParticipantContainer
                        AND tVersionId <> @c_Reference
--! ... HACK

                    -- check if there occurred an error:
                    IF @@error <> 0     -- an error occurred?
                        -- throw exception
                        GOTO exception
                END -- if
                -- get next tuple:
                FETCH NEXT FROM updateCursor INTO @l_oldRKey, @l_owner
            END -- while another tuple found

            -- close the not longer needed cursor:
            CLOSE updateCursor
            DEALLOCATE updateCursor
        END -- if rights can be set for >= one object

    -- finish the transaction:
    IF (@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_NOT_ALL)
                                        -- no severe error occurred?
        COMMIT TRANSACTION              -- make changes permanent
    ELSE                                -- there occurred an error
        ROLLBACK TRANSACTION            -- undo changes

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- undo all changes:
    ROLLBACK TRANSACTION
    -- return error value:
    RETURN @c_NOT_OK
GO
-- p_Rights$setRightsRecursive



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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$setUserRightsRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$setUserRightsRec
(
    -- input parameters:
    @ai_oid                 OBJECTID,
    @ai_userId              INT,
    @ai_op                  INT,
    @ai_rPersonId           INT
)
AS
DECLARE
    -- constants:
    @c_OWNER                USERID,         -- user id of owner
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_ParticipantContainer INT,            -- tVersionId of
                                            -- ParticipantContainer
    @c_Reference            INT,            -- tVersionId of Reference

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_posNoPath            POSNOPATH_VC,   -- pos no path of the object
    @l_rights               RIGHTS,         -- the rights
    @l_actOid               OBJECTID,       -- the actual object id
    @l_rKey                 ID,             -- rKey of actual object
    @l_count                INT             -- counter

    -- assign constants:
SELECT
    @c_OWNER                = 0x00900001,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_ParticipantContainer = 16850945, -- 0x01012001
    @c_Reference            = 16842801  -- 0x01010031

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK,
    @l_rights = 0,
    @l_count = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$setUserRightsRec

        -- get the rights of the specific person for the defined object:
        SELECT  @l_rights = rks.rights,
                @l_posNoPath = o.posNoPath, @l_rKey = o.rKey
        FROM    ibs_Object o, ibs_RightsKey rk, ibs_RightsKeys rks
        WHERE   o.oid = @ai_oid
            AND rks.rPersonId = @ai_rPersonId
            AND o.rKey = rk.id
            AND rk.rKeysId = rks.id

        IF (@@error <> 0 OR @@ROWCOUNT = 0) -- the object or its rights key was
                                        -- not found?
            GOTO exception              -- call exception handler

        -- check if there are any objects for which the rights cannot be set:
        EXEC @l_retValue =
            p_Rights$checkRightsRec @l_posNoPath, @ai_userId, @ai_op

        IF (@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_NOT_ALL)
                                        -- rights can be set for >= one object?
        BEGIN
            IF (@l_retValue = @c_ALL_RIGHT)
                                        -- rights can be set for all objects?
            BEGIN
                -- define cursor:
                DECLARE updateCursor CURSOR FOR
                    SELECT  o.oid
                    FROM    ibs_Object o
                    WHERE   o.posNoPath LIKE @l_posNoPath + '%'
                        AND o.state <> @c_STATE_DELETED
                        AND o.rKey <> @l_rKey
            END -- if rights can be set for all objects
            ELSE                        -- rights can be set for >= one object?
            BEGIN
                -- define cursor:
                DECLARE updateCursor CURSOR FOR
                    SELECT  o.oid
                    FROM    ibs_Object o, ibs_RightsCum r
                    WHERE   o.posNoPath LIKE @l_posNoPath + '%'
                        AND o.state <> @c_STATE_DELETED
                        AND o.rKey <> @l_rKey
                        AND r.rKey = o.rKey
                        AND r.userId = @ai_userId
/* KR tuning: explicit rights check for owner is not longer necessary
                        AND (
                                (o.owner <> @ai_userId
                                AND r.userId = @ai_userId)
                            OR
                                (o.owner = @ai_userId
                                AND r.userId = @c_OWNER)
                            )
*/
                        AND (r.rights & @ai_op) = @ai_op
            END -- else rights can be set for >= one object

            -- open the cursor:
            OPEN updateCursor

            -- get the first object:
            FETCH NEXT FROM updateCursor INTO @l_actOid

            -- loop through all found objects:
            WHILE (@@FETCH_STATUS <> -1) -- another object found?
            BEGIN
                -- Because @@FETCH_STATUS may have one of the three values
                -- -2, -1, or 0 all of these cases must be checked.
                -- In this case the tuple is skipped if it was deleted during
                -- the execution of this procedure.
                IF (@@FETCH_STATUS <> -2)
                BEGIN
                    -- set the rights for the actual object:
                    EXEC p_Rights$setRights @l_actOid, @ai_rPersonId,
                            @l_rights, 0
                END -- if
                -- get next object:
                FETCH NEXT FROM updateCursor INTO @l_actOid
            END -- while another object found

            -- close the cursor:
            CLOSE updateCursor
            DEALLOCATE updateCursor
        END -- if rights can be set for >= one object

    -- finish the transaction:
    IF (@l_retValue <> @c_ALL_RIGHT AND @l_retValue <> @c_NOT_ALL)
                                        -- there occurred an error?
    BEGIN
        -- roll back to the save point:
        ROLLBACK TRANSACTION s_Rights$setUserRightsRec
    END -- if there occurred an error

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$setUserRightsRec
    -- return error value:
    RETURN @c_NOT_OK
GO
-- p_Rights$setUserRightsRec


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
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$deleteUserRightsRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$deleteUserRightsRec
(
    -- input parameters:
    @ai_oid                 OBJECTID,
    @ai_userId              INT,
    @ai_op                  INT,
    @ai_rPersonId           INT
)
AS
DECLARE
    -- constants:
    @c_OWNER                USERID,         -- user id of owner
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects
    @c_STATE_DELETED        INT,            -- the object was deleted
    @c_STATE_ACTIVE         INT,            -- active state of object
    @c_ParticipantContainer INT,            -- tVersionId of
                                            -- ParticipantContainer
    @c_Reference            INT,            -- tVersionId of Reference

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_posNoPath            POSNOPATH_VC,   -- pos no path of the object
    @l_rights               RIGHTS,         -- the rights
    @l_actOid               OBJECTID,       -- the actual object id
    @l_count                INT             -- counter

    -- assign constants:
SELECT
    @c_OWNER                = 0x00900001,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31,
    @c_STATE_DELETED        = 1,
    @c_STATE_ACTIVE         = 2,
    @c_ParticipantContainer = 16850945, -- 0x01012001
    @c_Reference            = 16842801  -- 0x01010031

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK,
    @l_rights = 0,
    @l_count = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Rights$deleteUserRightsRec

        -- get the posNoPath of the root object:
        SELECT  @l_posNoPath = posNoPath
        FROM    ibs_Object
        WHERE   oid = @ai_oid

        IF (@@error <> 0 OR @@ROWCOUNT = 0) -- the object was not found?
            GOTO exception              -- call exception handler

        -- check if there are any objects for which the rights cannot be set:
        EXEC @l_retValue =
            p_Rights$checkRightsRec @l_posNoPath, @ai_userId, @ai_op

        IF (@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_NOT_ALL)
                                        -- rights can be set for >= one object?
        BEGIN
            IF (@l_retValue = @c_ALL_RIGHT)
                                        -- rights can be set for all objects?
            BEGIN
                -- define cursor:
                DECLARE updateCursor CURSOR FOR
                    SELECT  o.oid
                    FROM    ibs_Object o
                    WHERE   o.posNoPath LIKE @l_posNoPath + '%'
                        AND o.state <> @c_STATE_DELETED
/* KR the user rights shall also be deleted for the actual object
                        AND o.oid <> @ai_oid
*/
            END -- if rights can be set for all objects
            ELSE                        -- rights can be set for >= one object?
            BEGIN
                -- define cursor:
                DECLARE updateCursor CURSOR FOR
                    SELECT  o.oid
                    FROM    ibs_Object o, ibs_RightsCum r
                    WHERE   o.posNoPath LIKE @l_posNoPath + '%'
                        AND o.state <> @c_STATE_DELETED
/* KR the user rights shall also be deleted for the actual object
                        AND o.oid <> @ai_oid
*/
                        AND r.rKey = o.rKey
                        AND r.userId = @ai_userId
/* KR tuning: explicit rights check for owner is not longer necessary
                        AND (
                                (o.owner <> @ai_userId
                                AND r.userId = @ai_userId)
                            OR
                                (o.owner = @ai_userId
                                AND r.userId = @c_OWNER)
                            )
*/
                        AND (r.rights & @ai_op) = @ai_op
            END -- else rights can be set for >= one object

            -- open the cursor:
            OPEN updateCursor

            -- get the first object:
            FETCH NEXT FROM updateCursor INTO @l_actOid

            -- loop through all found objects:
            WHILE (@@FETCH_STATUS <> -1) -- another object found?
            BEGIN
                -- Because @@FETCH_STATUS may have one of the three values
                -- -2, -1, or 0 all of these cases must be checked.
                -- In this case the tuple is skipped if it was deleted during
                -- the execution of this procedure.
                IF (@@FETCH_STATUS <> -2)
                BEGIN
                    -- set the rights for the actual object:
                    EXEC p_Rights$setRights @l_actOid, @ai_rPersonId, 0, 0
                END -- if
                -- get next object:
                FETCH NEXT FROM updateCursor INTO @l_actOid
            END -- while another object found

            -- close the cursor:
            CLOSE updateCursor
            DEALLOCATE updateCursor
        END -- if rights can be set for >= one object

    -- finish the transaction:
    IF (@l_retValue <> @c_ALL_RIGHT AND @l_retValue <> @c_NOT_ALL)
                                        -- there occurred an error?
    BEGIN
        -- roll back to the save point:
        ROLLBACK TRANSACTION s_Rights$deleteUserRightsRec
    END -- if there occurred an error

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Rights$deleteUserRightsRec
    -- return error value:
    RETURN @c_NOT_OK
GO
-- p_Rights$deleteUserRightsRec


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
 *                              @hasRights == @requiredRights if the user has
 *                              all required rights.
 * @return  A value representing the rights or the state of the procedure.
 *  = ao_hasRights              No error, the value contains the rights.
 *  NOT_OK                      Any error occurred.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Rights$checkRights1'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights$checkRights1
(
    -- input parameters
    @ai_oid_s               OBJECTIDSTRING,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_userId              USERID = 0,
    @ai_requiredRights      RIGHTS = 0xFFFFFFFF,
    -- output parameters:
    @ao_hasRights           RIGHTS = 0 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong

    -- local variables:
    @l_retValue             INT             -- return value of function

    -- assign constants:
SELECT
    @c_NOT_OK               = 0

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK

-- body:
    -- redirect to basic checkRights procedure:
    EXEC @l_retValue = p_Rights$checkObjectRights
        @ai_oid_s, @ai_containerId_s, @ai_userId, @ai_requiredRights,
        @ao_hasRights OUTPUT

    -- return the computed rights:
    RETURN @l_retValue
GO
-- p_Rights$checkRights1




---------------------------
-- DELETE OLD PROCEDURES --
---------------------------


EXEC p_dropProc N'p_Rights$setRightsOld'
GO

EXEC p_dropProc N'p_Rights$addRightsOld'
GO

EXEC p_dropProc N'p_Rights$setRightsRecursiveOld'
GO

EXEC p_dropProc N'p_Rights$setUserRightsRecNew'
GO

EXEC p_dropProc N'p_Rights$deleteUserRightsRecNw'
GO
