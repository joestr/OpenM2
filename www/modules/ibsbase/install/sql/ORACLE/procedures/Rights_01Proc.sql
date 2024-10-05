/******************************************************************************
 * All stored procedures regarding the rights table. <BR>
 *
 * @version     2.21.0001, 11.06.2002 KR
 *
 * @author      Mario Stegbauer (MS)  980806
 ******************************************************************************
 */


/******************************************************************************
 * Delete all rights of an user on an object and all its sub objects (incl.
 * rights check). <BR>
 *
 * @input parameters:
 * @param   ai_rOid_s           ID of the root object for which to delete the
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
CREATE OR REPLACE FUNCTION p_Rights_01$deleteRightsRec
(
    -- common input parameters:
    ai_rOid_s               VARCHAR2,
    ai_rPersonId            ibs_RightsKeys.rPersonId%TYPE,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_rOid                  ibs_Object.oid%TYPE; -- the oid of the object to set
                                            -- the rights on
    l_rights                INTEGER := 0;   -- the actual rights

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_rOid_s, l_rOid);

    -- get rights for the actual user:
    l_rights := p_Rights$checkRights (
        l_rOid,                         -- given object to be accessed by user
        l_rOid,                         -- container of given object
        ai_userId,                      -- user_id
        ai_op,                          -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
        l_rights                        -- returned value
        );

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- delete the rights:
        p_Rights$setRights (l_rOid, ai_rPersonid, 0, 0);

        -- delete the rights of the person on the sub objects:
        l_retValue := p_Rights$deleteUserRightsRec (
            l_rOid, ai_userId, ai_op, ai_rPersonId);
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights$deleteRightsRec',
            'Input: ai_rOid_s = ' || ai_rOid_s ||
            ', ai_rPersonId = ' || ai_rPersonId || 
            ', ai_userId = ' || ai_userId || 
            ', ai_op = ' || ai_op || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights_01$deleteRightsRec;
/

show errors;


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
CREATE OR REPLACE FUNCTION p_Rights_01$create
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_rOid_s               VARCHAR2,
    ai_rPersonId            INTEGER,
    ai_rRights              INTEGER,
    ai_recursive            NUMBER
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;-- the object already exists

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_rOid                  RAW (8);        -- the oid of the object to set the
                                            -- rights on
    l_rights                INTEGER;        -- the actual rights
    l_dummy                 INTEGER;

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_rOid_s, l_rOid);

    -- get rights for this user:
    l_rights := p_Rights$checkRights (
        l_rOid,                         -- given object to be accessed by user
        l_rOid,                         -- container of given object
        ai_userId,                      -- user_id
        ai_op,                          -- required rights user must have to
                                        -- insert/update object
        l_rights                        -- returned value
        );

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- set the new rights:
        p_Rights$setRights (l_rOid, ai_rPersonId, ai_rRights, 0);

        IF (ai_recursive = 1)           -- set the rights recursive?
        THEN
            l_dummy := p_Rights$setUserRightsRec (
                l_rOid, ai_userId, ai_op, ai_rPersonId);
        END IF; -- if set the rights recursive
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF;-- else the user does not have the rights
    
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights_01$create',
            'Input: ai_userId = ' || ai_userId || 
            ', ai_op = ' || ai_op || 
            ', ai_rOid_s = ' || ai_rOid_s || 
            ', ai_rPersonId = ' || ai_rPersonId || 
            ', ai_rRights = ' || ai_rRights || 
            ', ai_recursive = ' || ai_recursive || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights_01$create;
/

show errors;


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
CREATE OR REPLACE FUNCTION p_Rights_01$retrieve
(
    -- common input parameters:
    ai_rOid_s               VARCHAR2,
    ai_rPersonId            INTEGER,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- common output parameters:
    ao_containerId          OUT RAW,
    ao_objectName           OUT VARCHAR2,
    ao_pOid                 OUT RAW,
    ao_pName                OUT VARCHAR2,
    ao_rights               OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
                                            -- default value for no defined oid
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_rOid                  RAW (8);        -- the oid of the object to set the
                                            -- rights on
    l_rights                INTEGER;        -- the actual rights

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_rOid_s, l_rOid);

    BEGIN
        -- get name of object on which this rights are set:
        SELECT  name
        INTO    ao_objectName
        FROM    ibs_Object
        WHERE   oid = l_rOid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the rights object does not exist?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'get object name';
            RAISE;                      -- call common exception handler
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get object name';
            RAISE;                      -- call common exception handler
    END;

    -- set container id:
    ao_containerId := l_rOid;

    -- get rights for the actual user:
    ao_rights := p_Rights$checkRights (
        l_rOid,                         -- given object to be accessed by user
        l_rOid,                         -- container of given object
        ai_userId,                      -- user_id
        ai_op,                          -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
        ao_rights                       -- returned value
        );

    -- check if the user has the necessary rights:
    IF (ao_rights = ai_op)              -- the user has the rights?
    THEN
        BEGIN
            SELECT  p.oid, p.name, r.rights
            INTO    ao_pOid, ao_pName, ao_rights
            FROM    (   SELECT  id, rights, rPersonId
                        FROM    ibs_RightsKeys
                        WHERE   rPersonId = ai_rPersonId
                    ) r,
                    (   SELECT  rKey
                        FROM    ibs_Object
                        WHERE   oid = l_rOid
                    ) o,
                    (
                        (   SELECT  oid, name, id
                            FROM    ibs_User
                            WHERE   state = 2
                                AND id = ai_rPersonId
                        ) 
                        UNION
                        (   SELECT  oid, name, id
                            FROM    ibs_Group
                            WHERE   state = 2
                                AND id = ai_rPersonId
                        )
                    ) p 
            WHERE   o.rKey = r.id
                AND p.id(+) = r.rPersonId;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                ao_pOid := c_NOOID;
                ao_pName := '';
                ao_rights := 0;
        END;
    -- end if the user has the rights
    ELSE                            -- the user does not have the rights
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_rOid_s = ' || ai_rOid_s ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ao_containerId = ' || ao_containerId ||
            ', ao_objectName = ' || ao_objectName ||
            ', ao_pOid = ' || ao_pOid ||
            ', ao_pName = ' || ao_pName ||
            ', ao_rights = ' || ao_rights ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Rights_01$retrieve', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Rights_01$retrieve;
/

show errors;


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
CREATE OR REPLACE FUNCTION p_Rights_01$change
(
    -- common input parameters:
    ai_rOid_s               VARCHAR2,
    ai_rPersonId            INTEGER,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- type-specific input parameters:
    ai_rRights              INTEGER,
    ai_recursive            NUMBER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_rOid                  RAW (8);        -- the oid of the object to set the
                                            -- rights on
    l_rights                INTEGER;        -- the actual rights

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_rOid_s, l_rOid);

    -- get rights for the actual user:
    l_rights := p_Rights$checkRights (
        l_rOid,                         -- given object to be accessed by user
        l_rOid,                         -- container of given object
        ai_userId,                      -- user_id
        ai_op,                          -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
        l_rights                        -- returned value
        );

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- update the rights:
        p_Rights$setRights (l_rOid, ai_rPersonid, ai_rRights, 0);

        IF (ai_recursive = 1)           -- set the rights recursive?
        THEN
            l_retValue := p_Rights$setUserRightsRec (
                l_rOid, ai_userId, ai_op, ai_rPersonId);
        END IF; -- if set the rights recursive
    -- end if the user has the rights
    ELSE                        -- the user does not have the rights
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF;-- else the user does not have the rights

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights_01$change',
            'Input: ai_rOid_s = ' || ai_rOid_s ||
            ', ai_rPersonId = ' || ai_rPersonId || 
            ', ai_userId = ' || ai_userId || 
            ', ai_op = ' || ai_op || 
            ', ai_rRights = ' || ai_rRights || 
            ', ai_recursive = ' || ai_recursive || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights_01$change;
/

show errors;

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
CREATE OR REPLACE FUNCTION p_Rights_01$delete
(
    -- common input parameters:
    ai_rOid_s               VARCHAR2,
    ai_rPersonId            INTEGER,
    ai_userId               INTEGER,
    ai_op                   INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_rOid                  RAW (8);        -- the oid of the object to set the
                                            -- rights on
    l_rights                INTEGER;        -- the actual rights

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_rOid_s, l_rOid);

    -- get rights for the actual user:
    l_rights := p_Rights$checkRights (
        l_rOid,                         -- given object to be accessed by user
        l_rOid,                         -- container of given object
        ai_userId,                      -- user_id
        ai_op,                          -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
        l_rights                        -- returned value
        );

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- delete the rights:
        p_Rights$setRights (l_rOid, ai_rPersonid, 0, 0);
    -- end if the user has the rights
    ELSE                                -- the user does not have the rights
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF;-- else the user does not have the rights
    
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Rights_01$delete',
            'Input: ai_rOid_s = ' || ai_rOid_s ||
            ', ai_rPersonId = ' || ai_rPersonId || 
            ', ai_userId = ' || ai_userId || 
            ', ai_op = ' || ai_op || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Rights_01$delete;
/

show errors;

EXIT;
