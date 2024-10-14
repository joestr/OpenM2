/******************************************************************************
 * All stored procedures regarding the rights table. <BR>
 *
 * @version     $Id: U24006r_Rights_01Proc.sql,v 1.1 2005/02/15 21:38:48 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  980528
 ******************************************************************************
 */


/******************************************************************************
 * Delete all rights of an user on an object and all its sub objects (incl.
 * rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the root object for which to delete the
 *                              rights.
 * @param   ai_rPersonId        Person for which to delete the rights.
 * @param   ai_userId           ID of the user who wants to delete the rights.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Rights_01$deleteRightsRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights_01$deleteRightsRec
(
    -- common input parameters:
    @ai_rOid_s              OBJECTIDSTRING,
    @ai_rPersonId           INT,
    @ai_userId              USERID,
    @ai_op                  INT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_rOid                 OBJECTID,       -- the oid of the object to set the
                                            -- rights on
    @l_rights               RIGHTS          -- the actual rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC p_stringToByte @ai_rOid_s, @l_rOid OUTPUT

    BEGIN TRANSACTION
        -- get rights for this user:
        EXEC p_Rights$checkRights
             @l_rOid,                   -- given object to be accessed by user
             @l_rOid,                   -- container of given object
             @ai_userId,                -- user_id
             @ai_op,                    -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @l_rights OUTPUT           -- returned value

        -- check if the user has the necessary rights:
        IF (@l_rights = @ai_op)          -- the user has the rights?
        BEGIN
            -- delete the rights:
            EXEC p_Rights$setRights @l_rOid, @ai_rPersonid, 0, 0

            -- delete the rights of the person on the sub objects:
            EXEC @l_retValue = p_Rights$deleteUserRightsRec
                @l_rOid, @ai_userId, @ai_op, @ai_rPersonId
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Rights_01$deleteRightsRec


/******************************************************************************
 * Creates a new Rights_01 object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_rOid_s           ID of the object, for which rights shall be set.
 * @param   ai_rPersonId        ID of the person/group/role for which rights
 *                              shall be set.
 * @param   ai_rRights          The rights to be set.
 * @param   ai_recursive        Shall the operation be done recursively?
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Rights_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights_01$create
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_rOid_s              OBJECTIDSTRING,
    @ai_rPersonId           INT,
    @ai_rRights             INT,
    @ai_recursive           BOOL = 0
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_ALREADY_EXISTS       INT,            -- the object exists already
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_rOid                 OBJECTID,       -- the oid of the object to set the
                                            -- rights on
    @l_rights               RIGHTS          -- the actual rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_ALREADY_EXISTS       = 21,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC p_stringToByte @ai_rOid_s, @l_rOid OUTPUT

    BEGIN TRANSACTION
        -- get rights for this user:
        EXEC p_Rights$checkRights
             @l_rOid,                   -- given object to be accessed by user
             @l_rOid,                   -- container of given object
             @ai_userId,                -- user_id
             @ai_op,                    -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @l_rights OUTPUT           -- returned value

        -- check if the user has the necessary rights:
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            -- set the new rights:
            EXEC p_Rights$setRights @l_rOid, @ai_rPersonId, @ai_rRights, 0

            IF (@ai_recursive = 1)      -- set the rights recursive?
            BEGIN
                EXEC p_Rights$setUserRightsRec
                    @l_rOid, @ai_userId, @ai_op, @ai_rPersonId
            END -- if set the rights recursive
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Rights_01$create


/******************************************************************************
 * Gets all data from a given Rights object . <BR>
 *
 * @input parameters:
 * @param   ai_rOid_s           ID of the object for which to get the rights.
 * @param   ai_rPersonId        Person for which to get the data.
 * @param   ai_userId           ID of the user who wants to get the data.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @output parameters:
 * @param   ao_containerId      ID of the rights container.
 * @param   ao_objectName       Name of the object on wich this right counts.
 * @param   ao_pOid             The object id of the user/role/group) for whom
 *                              the rights are valid.
 * @param   ao_pName            The name of the user/role/group for whom the
 *                              rights are valid.
 * @param   ao_rights           The rights.
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Rights_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights_01$retrieve
(
    -- common input parameters:
    @ai_rOid_s              OBJECTIDSTRING,
    @ai_rPersonId           INT,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- common output parameters:
    @ao_containerId         OBJECTID        OUTPUT,
    @ao_objectName          NAME            OUTPUT,
    -- type-specific output parameters:
    @ao_pOid                OBJECTID        OUTPUT,
    @ao_pName               NAME            OUTPUT,
    @ao_rights              INT             OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_rOid                 OBJECTID,       -- the oid of the object to set the
                                            -- rights on
    @l_rights               RIGHTS          -- the actual rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC p_stringToByte @ai_rOid_s, @l_rOid OUTPUT

    BEGIN TRANSACTION
        -- get name of object on which this rights are set:
        SELECT  @ao_objectName = name, @ao_containerId = @l_rOid
        FROM    ibs_Object
        WHERE   oid = @l_rOid

        -- get rights for this user:
        EXEC p_Rights$checkRights
             @l_rOid,                   -- given object to be accessed by user
             @l_rOid,                   -- container of given object
             @ai_userId,                -- user_id
             @ai_op,                    -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @l_rights OUTPUT           -- returned value

        -- check if the user has the necessary rights:
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            SELECT  @ao_pOid = p.oid, @ao_pName = p.name, @ao_rights = r.rights
            FROM    (   SELECT  rk.id, rks.rights, rks.rPersonId
                        FROM    ibs_RightsKeys rks, ibs_RightsKey rk
                        WHERE   rks.rPersonId = @ai_rPersonId
                            AND rks.id = rk.rKeysId
                    ) r
                    INNER JOIN
                    (   SELECT  rKey
                        FROM    ibs_Object
                        WHERE   oid = @l_rOid
                    ) o ON o.rKey = r.id
                    LEFT OUTER JOIN
                    (
                        (   SELECT  oid, name, id
                            FROM    ibs_User
                            WHERE   state = 2
                                AND id = @ai_rPersonId
                        )
                        UNION
                        (   SELECT  oid, name, id
                            FROM    ibs_Group
                            WHERE   state = 2
                                AND id = @ai_rPersonId
                        )
                    ) p ON p.id = r.rPersonId
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Rights_01$retrieve


/******************************************************************************
 * Changes the data of a given Rights object . <BR>
 *
 * @input parameters:
 * @param   ai_rOid_s           ID of the object for which to change the rights.
 * @param   ai_rPersonId        Person for which to change the data.
 * @param   ai_userId           ID of the user who wants to change the data.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_rRights          The value the rights shall be changed to.
 * @param   ai_recursive        Shall the rights be set for the sub objects,
 *                              too? (default 0)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Rights_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights_01$change
(
    -- common input parameters:
    @ai_rOid_s              OBJECTIDSTRING,
    @ai_rPersonId           INT,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- type-specific input parameters:
    @ai_rRights             INT,
    @ai_recursive           BOOL = 0
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_rOid                 OBJECTID,       -- the oid of the object to set the
                                            -- rights on
    @l_rights               RIGHTS          -- the actual rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC p_stringToByte @ai_rOid_s, @l_rOid OUTPUT

    BEGIN TRANSACTION
        -- get rights for this user:
        EXEC p_Rights$checkRights
             @l_rOid,                   -- given object to be accessed by user
             @l_rOid,                   -- container of given object
             @ai_userId,                -- user_id
             @ai_op,                    -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @l_rights OUTPUT           -- returned value

        -- check if the user has the necessary rights:
        IF (@l_rights = @ai_op)          -- the user has the rights?
        BEGIN
            -- update the rights:
            EXEC p_Rights$setRights @l_rOid, @ai_rPersonid, @ai_rRights, 0

            IF (@ai_recursive = 1)     -- set the rights recursive?
            BEGIN
                EXEC @l_retValue = p_Rights$setUserRightsRec
                    @l_rOid, @ai_userId, @ai_op, @ai_rPersonId
            END -- if set the rights recursive
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Rights_01$change


/******************************************************************************
 * Deletes all data from a given Rights object. <BR>
 *
 * @input parameters:
 * @param   ai_rOid_s           ID of the object for which to delete the rights.
 * @param   ai_rPersonId        Person for which to delete the rights.
 * @param   ai_userId           ID of the user who wants to delete the rights.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Rights_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Rights_01$delete
(
    -- common input parameters:
    @ai_rOid_s              OBJECTIDSTRING,
    @ai_rPersonId           INT,
    @ai_userId              USERID,
    @ai_op                  INT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOT_ALL              INT,            -- operation could not be performed
                                            -- for all objects

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_rOid                 OBJECTID,       -- the oid of the object to set the
                                            -- rights on
    @l_rights               RIGHTS          -- the actual rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC p_stringToByte @ai_rOid_s, @l_rOid OUTPUT

    BEGIN TRANSACTION
        -- get rights for this user:
        EXEC p_Rights$checkRights
             @l_rOid,                   -- given object to be accessed by user
             @l_rOid,                   -- container of given object
             @ai_userId,                -- user_id
             @ai_op,                    -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @l_rights OUTPUT           -- returned value

        -- check if the user has the necessary rights:
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            -- delete the rights:
            EXEC p_Rights$setRights @l_rOid, @ai_rPersonId, 0, 0
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Rights_01$delete


-- delete old procedures:
EXEC p_dropProc 'p_Rights_01$getUpperOid'
GO
