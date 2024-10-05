/******************************************************************************
 * All stored procedures regarding the user table. <BR>
 *
 * @version     $Revision: 1.29 $, $Date: 2009/02/10 09:31:16 $
 *              $Author: btatzmann $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


 /******************************************************************************
 * DELETE a user from all his groups he is a member of. <BR>
 *
 * input parameters:
 * param   ai_userId            Id of the user who is deleting the user.
 * param   ai_op                Operation to be performed (used for rights 
 *                              check).
 * param   ai_userOid_s         Id of the user to be deleted from all his groups
 *
 * output parameters:
 * return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_User_01$delUserGroups
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_uUserOid_s           VARCHAR2
)
RETURN INTEGER
AS
    -- DEFINITIONS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1;    
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_NOT_ALL               CONSTANT INTEGER := 31;
    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_rights                INTEGER := 0;
    l_groupOid              RAW (8);
    l_uUserId               INTEGER;
    l_uUserOid              RAW (8);    
    -- define cursor:
    CURSOR GroupUser_Cursor IS 
        SELECT  g.oid
        FROM    ibs_GroupUser gu, ibs_Group g
        WHERE   gu.userId = l_uUserId
            AND gu.origGroupId = gu.groupId
            AND gu.groupId = g.id;            
    l_cursorRow             GroupUser_Cursor%ROWTYPE;
    
BEGIN 
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_uUserOid_s, l_uUserOid);

    SELECT  id
    INTO    l_uUserId
    FROM    ibs_User u
    WHERE   oid = l_uUserOid;

    -- loop through the cursor rows:
    FOR l_cursorRow IN GroupUser_Cursor
    LOOP    
        -- get the first object:
        l_groupOid := l_cursorRow.oid;         
        -- get rights for this user
        l_rights := p_Rights$checkRights (
            l_uUserOid,                 -- given object to be accessed by user
            l_groupOid,                 -- container of given object
            ai_userId,                  -- user_id
            ai_op,                      -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
            l_rights);                  -- returned value

        -- check if the user has the necessary rights
        IF (l_rights <> ai_op)     -- the user does not have the rights?
        THEN
            l_retValue := c_NOT_ALL;
        END IF;    -- if the user does not have the rights
        EXIT WHEN l_retValue <> c_ALL_RIGHT;
    END LOOP;   -- while another tuple found

    -- the user can be deleted from all groups?
    IF (l_retValue = c_ALL_RIGHT)         
    THEN
        -- delete user from all groups:
        DELETE  ibs_GroupUser
        WHERE   userId = l_uUserId;
        -- recompute the rights of the user:
        p_Rights$updateRightsCumUser (l_uUserId);
        COMMIT WORK;
    ELSE 
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF;     -- the user does not have the rights

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$delUserGroups',
            'Input: ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_uUserOid_s = ' || ai_uUserOid_s ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
    l_retValue := c_INSUFFICIENT_RIGHTS;    
    RETURN l_retValue;
END p_User_01$delUserGroups;
/


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_tVersionId       Type of the new object.
 * @param   ai_name             Name of the object.
 * @param   ai_containerId_s    ID of the container where object shall be 
 *                              created in.
 * @param   ai_containerKind    Kind of object/container relationship
 * @param   ai_isLink           Defines if the object is a link
 * @param   ai_linkedObjectId_s If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   ai_description      Description of the object.
 * @param   ai_newUserId        User id to be used. If this value is set the
 *                              procedure tries to get the existing tuple to
 *                              this out of the user table instead of 
 *                              creating a new one.
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_User_01$performCreate
(
    -- input parameters:
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_tVersionId           ibs_Object.tVersionId%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        ibs_Object.containerKind%TYPE,
    ai_isLink               ibs_Object.isLink%TYPE,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          ibs_Object.description%TYPE,
    ai_newUserId            ibs_User.id%TYPE DEFAULT 0,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;-- the object already exists
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter

    -- local variables:
    l_containerId           ibs_Object.oid%TYPE := c_NOOID;
    l_usersOid              ibs_Object.oid%TYPE;
    l_usersOid_s            VARCHAR2 (18);
    l_oid                   ibs_Object.oid%TYPE := c_NOOID;
    l_domainId              ibs_Domain_01.id%TYPE;
    l_allGroupOid           ibs_Object.oid%TYPE;
    l_state                 ibs_Object.state%TYPE;
    l_rights                ibs_RightsKeys.rights%TYPE;
    l_name                  ibs_Object.name%TYPE := ai_name;
    l_newUserId             ibs_User.id%TYPE := ai_newUserId;
    l_localOp               INTEGER := 0;  -- operation for local operations

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_containerId_s, l_containerId);

    BEGIN
        -- get the domain data:
        SELECT  d.usersOid, d.id
        INTO    l_usersOid, l_domainId
        FROM    ibs_User u, ibs_Domain_01 d
        WHERE   u.id = ai_userId
            AND d.id = u.domainId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get domain data';
            RAISE;                      -- call common exception handler
    END;

    -- convert oid to string:
    p_byteToString (l_usersOid, l_usersOid_s);

    -- create base object:
    l_retValue := p_Object$performCreate (
        ai_userId, ai_op, ai_tVersionId, l_name, l_usersOid_s,
        ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description,
        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        -- get the state and name from ibs_Object:
        BEGIN
            SELECT  state, name
            INTO    l_state, l_name 
            FROM    ibs_Object 
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get state and name';
                RAISE;                  -- call common exception handler
        END;

        -- initialize counter:
        l_rowCount := 0;

        BEGIN
            -- check if the user exists already:
            SELECT  COUNT (*)
            INTO    l_rowCount
            FROM    ibs_User
            WHERE   id = l_newUserId;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set data';
                RAISE;                  -- call common exception handler
        END;

        IF (l_rowCount = 1)             -- user was found?
        THEN
            BEGIN
                -- try to set data of the user:
                UPDATE  ibs_User
                SET     name = l_name,
                        oid = l_oid,
                        state = l_state,
                        fullname = l_name,
                        domainId = l_domainId
                WHERE   id = l_newUserId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'update ibs_User';
                    RAISE;              -- call common exception handler
            END;
        -- end if user was found
        ELSE                            -- user was not found
            BEGIN
                -- create new tuple for user:
                INSERT INTO ibs_User
                        (name, oid, state, password, fullname, domainId)
                VALUES  (l_name, l_oid, l_state, '', l_name, l_domainId);

                -- get the new id:
                SELECT  id
                INTO    l_newUserId
                FROM    ibs_User
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'create ibs_User';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- else user was not found

        -- set rights of user on his/her own data:
        -- (this is necessary to allow the user to add his/her own person)
        BEGIN
            SELECT  SUM (id)
            INTO    l_rights
            FROM    ibs_Operation
            WHERE   name IN ('view', 'read', 'new', 'addElem');

            p_Rights$addRights (l_oid, l_newUserId, l_rights, 1);
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'create tuple';
                RAISE;                  -- call common exception handler
        END;
                 

        -- create a new workspace:
        l_retValue := p_Workspace_01$create (ai_userId, l_localOp, l_newUserId);

        -- add user to group if the container is a group:
        -- initialize counter:
        l_rowCount := 0;

        BEGIN
            -- check if container is a group:
            SELECT  COUNT (*)
            INTO    l_rowCount
            FROM    ibs_Group
            WHERE   oid = l_containerId;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- container is not a group
                l_rowCount := 0;
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check for group';
                RAISE;                  -- call common exception handler
        END;

        IF (l_rowCount = 1)             -- container is a group?
        THEN
            -- add user to group, roleId not inserted:
            l_retValue :=
                p_Group_01$addUser (ai_userId, l_containerId, l_oid, c_NOOID);
        END IF; -- if container is a group

        
        -- put every created User in the Group Jeder:
        BEGIN
            -- get group of all users of domain:
            SELECT  g.oid
            INTO    l_allGroupOid
            FROM    ibs_Group g, ibs_Domain_01 d
            WHERE   d.id = l_domainId
                AND g.id = d.allGroupId;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get Jeder';
                RAISE;                  -- call common exception handler
        END;

        -- add user to group, roleId not inserted:
        l_retValue :=
            p_Group_01$addUser (ai_userId, l_allGroupOid, l_oid, c_NOOID);
/*
--
-- HP Tuning: cumulation already executed in p_Group_01$addUser
--
        -- update the cumulated rights:
        p_Rights$updateRightsCumUser (l_uUserId);
*/
        
    END IF; -- object created successfully

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_tVersionId = ' || ai_tVersionId ||
            ', ai_name = ' || ai_name ||
            ', ai_containerId_s = ' || ai_containerId_s ||
            ', ai_containerKind = ' || ai_containerKind ||
            ', ai_isLink = ' || ai_isLink ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s ||
            ', ai_description = ' || ai_description ||
            ', ai_newUserId = ' || ai_newUserId ||
            ', ao_oid_s = ' || ao_oid_s ||
            ', l_domainId = ' || l_domainId ||
            ', l_usersOid = ' || l_usersOid ||
            ', l_usersOid_s = ' || l_usersOid_s ||
            ', l_allGroupOid = ' || l_allGroupOid ||
            ', l_newUserId = ' || l_newUserId ||
            ', l_oid = ' || l_oid ||
            ', l_name = ' || l_name ||
            ', l_state = ' || l_state ||
            ', l_rights = ' || l_rights ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_User_01$performCreate', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_User_01$performCreate;
/

show errors;

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be 
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_User_01$create
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               INTEGER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;

BEGIN
    l_retValue := p_User_01$performCreate (
        ai_userId, ai_op, ai_tVersionId, ai_name, 
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, NULL,
        ao_oid_s);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$create',
            'Input: ai_userId = ' || ai_userId || 
            ', ai_op = ' || ai_op || 
            ', ai_tVersionId = ' || ai_tVersionId || 
            ', ai_name = ' || ai_name || 
            ', ai_containerId_s = ' || ai_containerId_s || 
            ', ai_containerKind = ' || ai_containerKind || 
            ', ai_isLink = ' || ai_isLink || 
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s || 
            ', ai_description = ' || ai_description ||
            '; sqlcode = ' || SQLCODE || 
            ', sqlerrm = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         the showInNews flag 
 * @param   @delLink            Should linked Person be deleted ?
 *                              (0 = no, else yes)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_User_01$change
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           INTEGER,    
    ai_fullname             VARCHAR2,
    ai_state                INTEGER,
    ai_password             VARCHAR2,
	ai_changePwd			NUMBER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOOID_S               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_NAME_ALREADY_EXISTS   CONSTANT INTEGER := 51;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8);
    l_domainId              INTEGER;
    l_given                 INTEGER;
    l_linkOid               RAW (8) := c_NOOID;
    l_linkOid_s             VARCHAR2 (18) := c_NOOID_S;
    
BEGIN
    p_stringToByte (ai_oid_s, l_oid);

    -- compute domain id:
    -- (divide user id by 0x01000000, i.e. get the first byte)
    l_domainId := (ai_userId - MOD (ai_userId, 16777216)) / 16777216;

    -- is the name already given in this domain?
    SELECT  COUNT (*) 
    INTO    l_given
    FROM    ibs_User u, ibs_Object o 
    WHERE   u.oid = o.oid
        AND o.name = ai_name
        AND u.domainId = l_domainId        
        AND o.state = 2
        AND o.oid <> l_oid;

    IF (l_given > 0)                    -- name already given?
    THEN
        l_retValue := c_NAME_ALREADY_EXISTS;
    ELSE                                -- name not given
        -- perform the change of the object:
        l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op,
                ai_name, ai_validUntil, ai_description,
                ai_showInNews, l_oid);

        IF (l_retValue = c_ALL_RIGHT)   -- operation properly performed?
        THEN
            -- update the other values, get the state from the object:
            UPDATE  ibs_User
            SET     name = ai_name,
                    fullname = ai_fullname,
                    state = (
                                SELECT  state
                                FROM    ibs_Object
                                WHERE   oid = l_oid
                            ),
                    password = ai_password,
					changePwd = ai_changePwd
            WHERE   oid = l_oid;

        END IF; -- if operation properly performed
    END IF; -- else name not given

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$change',
            'Input: ai_oid_s = ' || ai_oid_s || 
            ', ai_userId = ' || ai_userId || 
            ', ai_op = ' || ai_op || 
            ', ai_name = ' || ai_name || 
            ', ai_validUntil = ' || ai_validUntil || 
            ', ai_description = ' || ai_description || 
            ', ai_showInNews = ' || ai_showInNews || 
            ', ai_fullname = ' || ai_fullname || 
            ', ai_state = ' || ai_state || 
            ', ai_password = ' || ai_password || 
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$change;
/

show errors;



/******************************************************************************
 * Creates a new user. <BR>
 * This procedure also adds the user to a group and sets the rights of members 
 * of this group on the user.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @domainId           Id of the domain where the user shall resist.
 * @param   @username           Name of the user.
 * @param   @password           Password initially set for this user.
 * @param   @fullname           Full name of the user.
 *
 * @output parameters:
 * @param   @oid                Oid of the newly generated user.
 * @return  A value representing the state of the procedure.
 * @ALL_RIGHT               Action performed, values returned, everything ok.
 * @ALREADY_EXISTS          An user with this id already exists.
 */
CREATE OR REPLACE FUNCTION p_User_01$createFast
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_domainId             INTEGER,
    ai_username             VARCHAR2,
    ai_password             VARCHAR2,
    ai_fullname             VARCHAR2,
    -- output parameters:
    ao_oid                  OUT RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_containerId           RAW (8);
    l_containerId_s         VARCHAR2 (18);
    l_oid_s                 VARCHAR2 (18);
    l_groupId               INTEGER;
    l_groupOid              RAW (8); 
    l_validUntil            DATE;
   
BEGIN
    -- initialize return values:
    ao_oid := c_NOOID;

    BEGIN
        -- get user container:
        SELECT  usersOid
        INTO    l_containerId
        FROM    ibs_Domain_01
        WHERE   id = ai_domainId;

        -- at this point we know that the domain exists.

        -- convert container oid to string representation:
        p_byteToString (l_containerId, l_containerId_s);

        -- create the user (tVersionId 0x010100A1):
        -- the current user does not need any rights because this procedure
        -- shall only be called during installation.
        l_retValue := p_User_01$performCreate (
            ai_userId, 0, 16842913, ai_username, l_containerId_s, 1,
            0, c_NOOID_s, ' ', NULL,
            l_oid_s);

        -- convert user oid string to oid representation:
        p_stringToByte (l_oid_s, ao_oid);

        -- check if there was an error during creation:
        IF (l_retValue = c_ALL_RIGHT)   -- user created successfully?
        THEN
            BEGIN
                l_validUntil := ADD_MONTHS (SYSDATE, 12);

                -- store user specific data:
                -- the user has created the object, so don't check rights.
                l_retValue := p_User_01$change (l_oid_s, ai_userId, 0,
                    ai_username, l_validUntil, ' ', 0, ai_fullname, 2,
                    ai_password);
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                        'p_User_01$createFast.get group',
                        'OTHER error for domain ' || ai_domainId);
                    RAISE;
            END;
        END IF; -- if user created successfully

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- domain does not exist
            NULL;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$createFast.get Jeder',
                'OTHER error for domain ' || ai_domainId);
            RAISE;
    END;

    COMMIT WORK;

    -- return the state value:
    RETURN  p_User_01$createFast.l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$createFast',
            'Input: ai_userId = ' || ai_userId || 
            ', ai_domainId = ' || ai_domainId || 
            ', ai_username = ' || ai_username || 
            ', ai_password = ' || ai_password || 
            ', ai_fullname = ' || ai_fullname || 
            ', ao_oid = ' || ao_oid || 
            '; sqlcode = ' || SQLCODE || 
            ', sqlerrm = ' || SQLERRM); 
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$createFast;
/

show errors;


/******************************************************************************
 * Creates a new user. <BR>
 * This procedure also adds the user to a group and sets the rights of members 
 * of this group on the user.
 *
 * @input parameters:
 * @param   @domainId           Id of the domain where the user shall resist.
 * @param   @userNo             Predefined number of the user.
 * @param   @name               Name of the user.
 * @param   @password           Password initially set for this user.
 * @param   @fullname           Full name of the user.
 * @param   @group              Group to add the user to 
 *                              (null -> don't add user to a group).
 * @param   @rights             Rights which the members of the group shall 
 *                              have on this user (null -> don't assign rights).
 *
 * @output parameters:
 * @param   @newId              New id = @id if @ <> null, a newly generated
 *                              id otherwise
 * @return  A value representing the state of the procedure.
 * @ALL_RIGHT               Action performed, values returned, everything ok.
 * @ALREADY_EXISTS          An user with this id already exists.
 */
CREATE OR REPLACE FUNCTION p_User_01$new
(
    -- input parameters:
    ai_domainId             INTEGER,
    ai_userNo               INTEGER DEFAULT 0,
    ai_name                 VARCHAR2,
    ai_password             VARCHAR2,
    ai_fullname             VARCHAR2,
    ai_group                INTEGER,
    ai_rights               INTEGER,
    -- output parameters:
    ao_id                   OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_msg                   VARCHAR2 (255);
    l_oid                   RAW (8) := c_NOOID;
    l_cnt                   INTEGER := 0;
    l_groupOid              RAW (8);

BEGIN
    -- initialize return values:
    ao_id := 0;

    -- compute id:
    IF  (ai_userNo <> 0)                -- user number defined?
    THEN
        -- set domain id as highest byte of user id and add the user number:
        ao_id := (ai_domainId * 16777216) + 8388608 + ai_userNo;
    ELSE                                -- no user number defined
        ao_id := 0;
    END IF; -- else no user number defined
    
    -- check if an user with this id already exists:
    BEGIN
        SELECT  id
        INTO    l_cnt
        FROM    ibs_User 
        WHERE   id = ao_id;

        -- at this point we know that an user with this id already exists.
        l_retValue := c_ALREADY_EXISTS;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- user id not already there?
            /*[SPCONV-ERR(52)]:BEGIN TRAN statement ignored*/
            -- add the new user:
            BEGIN
                INSERT INTO ibs_User (id, oid, state, domainId, name, 
                        password, fullname)
                VALUES  (ao_id, c_NOOID, 2, ai_domainId, ai_name, 
                        ai_password, ai_fullname);

                IF (SQL%ROWCOUNT > 0)   -- user was inserted?
                THEN
                    IF  (ao_id = 0)     -- id must have been changed?
                    THEN
                        -- get the id of the newly inserted user:
                        -- (get highest id of user with the same name with 
                        -- the same domain within his id)
                        BEGIN
                            SELECT  MAX (id)
                            INTO    ao_id
                            FROM    ibs_User 
                            WHERE   name = ai_name 
                                AND (id - MOD (id, 16777216)) / 16777216 =
                                    ai_domainId;

                        EXCEPTION
                            WHEN OTHERS THEN
                                ibs_error.log_error (ibs_error.error,
                                    'p_User_01$new.get id',
                                    'OTHER error for user with name ' ||
                                    ai_name ||
                                    ' and domainId ' || ai_domainId);
                                RAISE;
                        END;
                    END IF; -- if id must have been changed

                    -- get oid:
                    BEGIN
                        SELECT  oid
                        INTO    l_oid
                        FROM    ibs_User 
                        WHERE   id = ao_id;

                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            ibs_error.log_error (ibs_error.error,
                                'p_User_01$new.get oid',
                                'NO_DATA_FOUND for user with id ' || ao_id);
                            RAISE;
                        WHEN TOO_MANY_ROWS THEN
                            ibs_error.log_error (ibs_error.error,
                                'p_User_01$new.get oid',
                                'TOO_MANY_ROWS for user with id ' || ao_id);
                            RAISE;
                        WHEN OTHERS THEN
                            ibs_error.log_error (ibs_error.error,
                                'p_User_01$new.get oid',
                                'OTHER error for user with id ' || ao_id);
                            RAISE;
                    END;

                    -- add user to a group:
                    IF  (ai_group <> NULL) -- group set?
                    THEN
                        BEGIN
                            -- get the oid of the group:
                            SELECT  oid
                            INTO    l_groupOid
                            FROM    ibs_Group
                            WHERE   id = ai_group;

                            -- at this point we know that the group exists.

                            -- add user to group:
                            l_retValue := p_Group_01$addUserSetRights (
                                ao_id, l_groupOid, l_oid, c_NOOID, ai_rights);
                        EXCEPTION
                            WHEN NO_DATA_FOUND THEN -- group does not exist?
                                ibs_error.log_error (ibs_error.error,
                                    'p_User_01$new.add user to group',
                                    'NO_DATA_FOUND for group with id ' ||
                                    ai_group);
                                RAISE;
                            WHEN OTHERS THEN
                                ibs_error.log_error (ibs_error.error,
                                    'p_User_01$new.add user to group',
                                    'OTHER error for group with id ' ||
                                    ai_group);
                                RAISE;
                        END;
                    END IF; -- if group set

                    -- cumulate rights for user:
                    p_Rights$updateRightsCumUser (ao_id);
                END IF; -- if user was inserted

            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                        'p_User_01$new.add new user',
                        'OTHER error for user with id ' || ao_id);
                    RAISE;
            END;

            COMMIT WORK;
        -- end when user id not already there        
        WHEN OTHERS THEN
            NULL;
    END;

    -- return the state value
    RETURN p_User_01$new.l_retValue;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$new',
            'Input: ' || ai_domainId || ', ' || ai_userNo || ', ' || 
            ai_name || ', ' || ai_password || ', ' || ai_fullname || ', ' || 
            ai_group || ', ' || ai_rights ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$new;
/

show errors;



/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The user's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the user.
 * @param   @containerId        ID of the object's container.
 * @param   @containerKind      Kind of object/container relationship.
 * @param   @isLink             Is the object a link?
 * @param   @linkedObjectId     Link if isLink is true.
 * @param   @owner              ID of the owner of the object.
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the 
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         the showInNews flag
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   @fullname           Fullname of the user
 * @param   @password           Password of the user
 * @param   @workspaveId        Workspave ot the user
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_User_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- output parameters
    ao_state                OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_typeName             OUT VARCHAR2,
    ao_name                 OUT VARCHAR2,
    ao_containerId          OUT RAW,
    ao_containerName        OUT VARCHAR2,
    ao_containerKind        OUT INTEGER,
    ao_isLink               OUT NUMBER,
    ao_linkedObjectId       OUT RAW,
    ao_owner                OUT INTEGER,
    ao_ownerName            OUT VARCHAR2, -- name of the Owner
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2, -- name of the Changer
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2, -- name of the Creator
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT INTEGER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,
    -- objectspezific attributes
    ao_fullname             OUT VARCHAR2, -- fullname of the User
    ao_password             OUT VARCHAR2, -- password of the User
    ao_workspaceId          OUT RAW,    -- workspave of the User
    ao_memberShipId         OUT RAW,    -- memberShips of the User
    ao_personOid            OUT RAW,
	ao_changePwd			OUT NUMBER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; 
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; 
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this procedure
    l_oid                   RAW (8); 
    l_id                    INTEGER;

BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (
        ai_oid_s, ai_userId, ai_op, 
        ao_state, ao_tVersionId, ao_typeName, ao_name, 
        ao_containerId, ao_containerName, ao_containerKind, 
        ao_isLink, ao_linkedObjectId, 
        ao_owner, ao_ownerName, 
        ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName,
        ao_validUntil, ao_description, ao_showInNews, 
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
        l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- get object type specific data:
        BEGIN
            SELECT  id, fullname, password, changePwd
            INTO    l_id, ao_fullname, ao_password, ao_changePwd
            FROM    ibs_User
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                    'p_User_01$retrieve.get user data',
                    'OTHER error for user with oid ' || l_oid);
                RAISE;
        END;

        -- get workspaceId of the user:
        BEGIN
            SELECT  workspace
            INTO    ao_workspaceId
            FROM    ibs_Workspace
            WHERE   userId = l_id;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                    'p_User_01$retrieve.get workspaceId',
                    'OTHER error for workspace with user id ' || l_id);
                RAISE;
        END;

        -- get memberShipId of the user:
        BEGIN
            SELECT  o.oid
            INTO    ao_memberShipId
            FROM    ibs_Object o
            WHERE   o.containerId = l_oid
                AND o.containerKind = 2
                AND o.tVersionId = 16863745;  -- 0x01015201; memberShip objects
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- membership container not found?
                ao_memberShipId := null; -- this is a valid state
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                    'p_User_01$retrieve.get memberShipId',
                    'OTHER error for object with containerId ' || l_oid);
                RAISE;
        END;

        -- get personOid linked to the user:
        BEGIN
            SELECT  linkedObjectId, name
            INTO    ao_personOid, ao_fullname
            FROM    ibs_Object
            WHERE   containerId = l_oid
                AND tVersionId = 16842801 -- 0x01010031
                AND state = 2;          -- check if state is ST_ACTIVE
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- person not found?
                ao_personOid := null;   -- this is a valid state
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                    'p_User_01$retrieve.get personOid',
                    'OTHER error for object with containerId ' || l_oid);
                RAISE;
        END;


    END IF; -- if operation properly performed

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$retrieve',                   
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$retrieve;    
/

show errors;


/******************************************************************************
 * Makes the login of a new user. (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @domainId           Domain where the user wants to be logged in.
 * @param   @username           Required user name.
 * @param   @password           Password typed by the user.
 *
 * @output parameters:
 * @param   @oid                Object id of the user object.
 * @param   @id                 Id of the user.
 * @param   @fullname           Full name of the user.
 * @param   @sslRequired        flag if SSL must be used for this domain or not
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_User_01$login
(
    -- input parameters:
    ai_domainId             INTEGER,
    ai_username             VARCHAR2,
    ai_password             VARCHAR2,
    -- output parameters
    ao_oid                  OUT RAW,
    ao_id                   OUT INTEGER,
    ao_fullname             OUT VARCHAR2,
    ao_domainName           OUT VARCHAR2,
    ao_sslRequired          OUT NUMBER,
	ao_changePwd			OUT NUMBER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0; 
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; 
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_WRONG_PASSWORD        CONSTANT INTEGER := 11;
    c_NOT_VALID             CONSTANT INTEGER := 41;

    -- local variables:
    l_retValue              INTEGER := c_OBJECTNOTFOUND; -- return value of this procedure
    l_rights                INTEGER;
    l_realPassword          VARCHAR2 (63) := 'unknownPassword';
    l_validUntil            DATE;

BEGIN
    -- initialize return values:
    ao_oid := c_NOOID;
    ao_id := 0;
    ao_fullname := ' ';
    ao_domainName := ' ';
    ao_sslRequired := c_NOT_OK;

    -- get data of required user:
    BEGIN
        SELECT  u.id, u.oid, u.password, u.fullname, u.changePwd, o.validUntil
        INTO    ao_id, ao_oid, l_realPassword, ao_fullname, ao_changePwd, l_validUntil
        FROM    ibs_User u, ibs_Object o
        WHERE   u.name = ai_username
            AND (   u.domainId = ai_domainId
                OR  u.domainId = 0
                )
            AND u.state = 2
            AND o.state = 2
            AND o.oid = u.oid;

        -- at this point we know that the user exists.

        -- check if user is valid:
        IF (l_validUntil >= SYSDATE)    -- the user is valid?
        THEN
            -- check password:
            IF (ai_password = l_realPassword) -- correct password?
            THEN
                BEGIN
                    -- get domain data:
                    SELECT  o.name, d.sslRequired
                    INTO    ao_domainName, ao_sslRequired
                    FROM    ibs_Object o, ibs_Domain_01 d 
                    WHERE   d.id = ai_domainId 
                        AND o.oid = d.oid 
                        AND o.state = 2;

                EXCEPTION
                    WHEN NO_DATA_FOUND THEN -- the domain was not found?
                        -- ensure that ssl dont have to be used if no domain found
                        ao_sslRequired := c_NOT_OK; 
                        ibs_error.log_error (ibs_error.error,
                            'p_User_01$login.getDomainData',
                            'NO_DATA_FOUND for domain ' || ai_domainId);
                        RAISE;
                    WHEN TOO_MANY_ROWS THEN
                        ibs_error.log_error (ibs_error.error,
                            'p_User_01$login.getDomainData',
                            'TOO_MANY_ROWS for domain ' || ai_domainId);
                        RAISE;
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error,
                            'p_User_01$login.getDomainData',
                            'OTHER error for domain ' || ai_domainId);
                        RAISE;
                END;
                l_retValue := c_ALL_RIGHT;

            ELSE                        -- wrong password
                ao_id := 0;
                ao_oid := c_NOOID;
                ao_fullname := ' ';
                l_retValue := c_WRONG_PASSWORD;
            END IF; -- else wrong password
        ELSE                            -- the user is not longer valid
            ao_id := 0;
            ao_oid := c_NOOID;
            ao_fullname := ' ';
            l_retValue := c_NOT_VALID;
        END IF; -- else the user is not longer valid

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the user does not exist?
            l_retValue := c_OBJECTNOTFOUND;
        WHEN TOO_MANY_ROWS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$login.getUserData',
                'TOO_MANY_ROWS for user ' || ai_username ||
                ' within domain ' || ai_domainId);
            RAISE;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$login.getUserData',
                'OTHER error for user ' || ai_username ||
                ' within domain ' || ai_domainId);
            RAISE;
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$login',
            'Input: ai_domainId = ' || ai_domainId ||
            ', ai_username = ' || ai_username ||
            ', ai_password = ' || ai_password ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$login;
/

show errors;

/******************************************************************************
 * Makes the logout of a online user.<BR>
 *
 * @input parameters:
 * @param   @id                 Id of the user.
 * @param   @oid                Object id of the user object.
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_User_01$logout
(
    -- input parameters:
    ai_id                   OUT INTEGER,
    ai_oid                  OUT RAW
    -- output parameters
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0; 
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of this procedure

BEGIN

    -- do nothing
    -- return the state value:
    RETURN l_retValue;

END p_User_01$logout;
/

show errors;


/******************************************************************************
 * Changes the password of the user. (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             Id of the user whose password is to be changed.
 * @param   @oldPassword        The old password of the user.
 * @param   @newPassword        The new password of the user.
 *
 * @output parameters:
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  WRONGPASSWORD           The given password is wrong.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_User_01$changePassword
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_oldPassword          VARCHAR2,
    ai_newPassword          VARCHAR2
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0; 
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; 
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_WRONG_PASSWORD        CONSTANT INTEGER := 11;

    -- local variables:
    l_retValue              INTEGER := c_OBJECTNOTFOUND;
                                            -- return value of this procedure
    l_rights                INTEGER;
    l_realPassword          VARCHAR2 (63) := 'unknownPassword';

BEGIN
    -- get data of required user:
    BEGIN
        SELECT  password
        INTO    l_realPassword
        FROM    ibs_User 
        WHERE   id = ai_userId;

        -- at this point we know that the user exists.

        -- check password:
        IF (ai_oldPassword = l_realPassword) -- correct password?
        THEN
            -- set the new password:
            BEGIN
                UPDATE  ibs_User
                SET     password = ai_newPassword, changePwd = 0
                WHERE   id = ai_userId;

            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                        'p_User_01$changePassword.update',
                        'OTHER error for user ' || ai_userId);
                    RAISE;
            END;

            -- set return value:
            l_retValue := c_ALL_RIGHT;
        ELSE                            -- wrong password
            l_retValue := c_WRONG_PASSWORD;
        END IF; -- else wrong password

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- user not found?
            l_retValue := c_OBJECTNOTFOUND;
        WHEN TOO_MANY_ROWS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$changePassword.getPassword',
                'TOO_MANY_ROWS for user ' || ai_userId);
            RAISE;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$changePassword.getPassword',
                'OTHER error for user ' || ai_userId);
            RAISE;
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$changePassword',
            'Input: ai_userId = ' || ai_userId  ||
            ', ai_oldPassword = ' || ai_oldPassword ||
            ', ai_newPassword = ' || ai_newPassword ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$changePassword;
/

show errors;


/******************************************************************************
 * Delete an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_User_01$delete
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
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
                                            -- delete an object
    c_ST_DELETED            CONSTANT INTEGER := 1; -- state to indicate deletion of
                                            -- object      

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of a function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text       
    l_rowCount              INTEGER := 0;   -- row counter      
    l_oid                   RAW (8);        -- the oid of the object to be
                                            -- deleted
    l_id                    INTEGER;        -- the id of the user
    l_rights                INTEGER := 0;   -- the current rights    
    l_dummyOid              RAW(8);         -- dummyOid only for oracle

BEGIN
    -- conversions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);
                
    COMMIT WORK; -- finish previous and begin new TRANSACTION

        -- get the user data:
        BEGIN
            SELECT  id
            INTO    l_id
            FROM    ibs_User
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get user data';
                RAISE;                  -- call common exception handler
        END;

        -- check if the user is a system user:
        BEGIN
            SELECT  COUNT (id)
            INTO    l_rowCount
            FROM    ibs_Domain_01
            WHERE   adminId = l_id;

        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check for system user';
                RAISE;                  -- call common exception handler
        END;

        IF (l_rowCount > 0)             -- the user is a system user?
        THEN
            -- set corresponding return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        -- the user is a system user?
        ELSE                            -- the user is no system user
            -- user may be deleted

            -- delete base object and references:
            l_retValue :=
                p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_dummyOid);
    
            -- check if there was an error:
            IF (l_retValue = c_ALL_RIGHT) -- operation properly performed?
            THEN
                -- delete object type specific data:
                -- (delete all type specific tuples which are not within
                -- ibs_Object)

                -- delete all rights for the deleted user:
                p_Rights$deleteAllUserRights (l_id);

                -- delete all the entries in ibs_GroupUser:
                BEGIN
                    DELETE  ibs_GroupUser
                    WHERE   userid = l_id;
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'delete group/user data';
                        RAISE;          -- call common exception handler
                END;

                -- set object as deleted:
                BEGIN
                    UPDATE  ibs_User
                    SET     state = c_ST_DELETED
                    WHERE   id = l_id;
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'update user state';
                        RAISE;          -- call common exception handler
                END;
            END IF; -- if operation properly performed
    END IF; -- else the user is no system user

    -- finish the transaction:
    IF (l_retValue = c_ALL_RIGHT)   -- everything all right?
    THEN
        COMMIT WORK;                -- make changes permanent
    ELSE                            -- an error occured
        ROLLBACK;                   -- undo changes
    END IF; -- else an error occurred

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId ||
            ', ai_oid_s = ' || ai_oid_s ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_User_01$delete', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$delete;
/
show errors;


/******************************************************************************
 * Copies an User_01 object and all its values (incl. rights check). <BR>
 */
CREATE OR REPLACE FUNCTION p_User_01$BOCopy
(
    -- common input parameters:
    ai_oid                  RAW,
    ai_userId               INTEGER,
    ai_newOid               RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ST_ACTIVE             CONSTANT ibs_Object.state%TYPE := 2; -- active state

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_userId                ibs_GroupUser.userId%TYPE; -- id of actual user
    l_oldUserId             ibs_GroupUser.userId%TYPE; -- old user id
    l_dummy                 INTEGER;        -- ignored return value of function
    l_name                  ibs_User.name%TYPE; -- name of user
    l_baseName              ibs_User.name%TYPE; -- base part of user name
    l_count                 INTEGER := 0;   -- counter
    l_id                    ibs_User.id%TYPE; -- id of actual user

    CURSOR UserBOCopy_Cursor IS
        -- get all users and groups in the old group:
        SELECT  g.oid AS groupOid
        FROM    ibs_GroupUser gu, ibs_Group g
        WHERE   gu.userId = l_oldUserId
            AND gu.origGroupId = gu.groupId
            AND gu.groupId = g.id;
    l_cursorRow             UserBOCopy_Cursor%ROWTYPE;

BEGIN
    /*[SPCONV-ERR(24)]:BEGIN TRAN statement ignored*/
    BEGIN
        -- get id of user to be copied:
        SELECT  id, name || '#copy'
        INTO    l_oldUserId, l_baseName
        FROM    ibs_User
        WHERE   oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get user id';
            RAISE;                      -- call common exception handler
    END;

    -- get unique user name:
    l_name := l_baseName;

    BEGIN
        -- check if the name is already used:
        SELECT  id
        INTO    l_id
        FROM    ibs_User
        WHERE   name = l_name
            AND state = c_ST_ACTIVE;

        l_rowCount := SQL%ROWCOUNT;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_rowCount := 0;
        WHEN TOO_MANY_ROWS THEN
            l_rowCount := SQL%ROWCOUNT;
        WHEN OTHERS THEN                    -- any error
            -- create error entry:
            l_ePos := 'check user name 1';
            RAISE;                          -- call common exception handler
    END;

    -- try the temp names until an unused name is found:
    WHILE (l_rowCount > 0)
    LOOP
        -- compute new user name:
        l_count := l_count + 1;
        l_name := l_baseName || l_count;

        BEGIN
            -- check if the name is already used:
            SELECT  id
            INTO    l_id
            FROM    ibs_User
            WHERE   name = l_name
                AND state = c_ST_ACTIVE;

            l_rowCount := SQL%ROWCOUNT;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_rowCount := 0;
            WHEN TOO_MANY_ROWS THEN
                l_rowCount := SQL%ROWCOUNT;
            WHEN OTHERS THEN                -- any error
                -- create error entry:
                l_ePos := 'check user name 2';
                RAISE;                      -- call common exception handler
        END;
    END LOOP; -- while

    BEGIN
        -- ensure that the name in ibs_Object is correct:
        UPDATE  ibs_Object
        SET     name = l_name
        WHERE   oid = ai_newOid;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'set object name';
            RAISE;                      -- call common exception handler
    END;


    -- make an insert for all type specific tables:
    BEGIN
        INSERT INTO ibs_User
                (oid, name, state, password, fullname, domainId, changePwd)
        SELECT  ai_newOid, l_name, c_ST_ACTIVE, password, fullname, domainId, changePwd
        FROM    ibs_User 
        WHERE   oid = ai_oid;

        -- get the id of the new user:
        SELECT  id
        INTO    l_userId
        FROM    ibs_User
        WHERE   oid = ai_newOid;

        -- at this point we know that the user was inserted.
        -- create a new workspace and a workspace container:
        l_dummy := p_Workspace_01$create (ai_userId, 4, l_userId);

        -- get all users and groups in the old group
        -- loop through all found users:
        FOR l_cursorRow IN UserBOCopy_Cursor
        LOOP
            -- add user to the current group:
            l_dummy := p_Group_01$addUser (ai_userId, l_cursorRow.groupOid,
                    ai_newOid, c_NOOID);
        END LOOP;

        l_retValue := c_ALL_RIGHT;      -- set return value

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the user could not be isnerted?
            l_retValue := c_NOT_OK;
        WHEN TOO_MANY_ROWS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$BOCopy.insert',
                'TOO_MANY_ROWS for user with oid ' || ai_oid);
            RAISE;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$BOCopy.insert',
                'OTHER error for user with oid ' || ai_oid);
            RAISE;
    END;

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$BOCopy',
            'Input: ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_newOid = ' || ai_newOid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$BOCopy;
/

show errors;

/******************************************************************************
 * Change the state of an existing object. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @state              The new state of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_User_01$changeState
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_state                INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_RIGHT_INSERT          CONSTANT INTEGER := 1;
    c_RIGHT_UPDATE          CONSTANT INTEGER := 8;
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- active state
    c_ST_CREATED            CONSTANT INTEGER := 4; -- created state

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8);
    l_rights                INTEGER := 0;
    l_containerId           RAW (8) := c_NOOID;
    l_oldState              INTEGER := 0;

BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    -- get the actual container id and state of object:
    BEGIN
        SELECT  containerId,  state
        INTO    l_containerId, l_oldState
        FROM    ibs_Object 
        WHERE   oid = l_oid;

        -- at this point we know that the object exists.

        -- get rights for the actual user:
        l_rights := p_Rights$checkRights (
            l_oid,                      -- given object to be accessed by user
            l_containerId,              -- container of given object
            ai_userId,                  -- user id
            ai_op,                      -- required rights user must have to
                                        -- update object
            l_rights                    -- returned value
            );

        -- check if the user has the necessary rights:
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN
            -- check if the state transition from the actual state to the new
            -- state is allowed:
            -- not implemented yet

            /*[SPCONV-ERR(59)]:BEGIN TRAN statement ignored*/
            BEGIN
                -- set the new state for the object and all tabs:
                UPDATE  ibs_Object
                SET     state = ai_state
                WHERE   oid = l_oid
                    OR  (   containerId = l_oid
                        AND containerKind = 2
                        AND state <> ai_state
                        AND (   state = c_ST_ACTIVE
                            OR  state = c_ST_CREATED
                            )
                        );

                -- update the state of the user tuple:
                UPDATE  ibs_User
                SET     state = ai_state
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                        'p_User_01$changeState.update',
                        'OTHER error for user with oid ' || l_oid);
                    RAISE;
            END;

            COMMIT WORK;
        ELSE                            -- the user does not have the rights
            -- set the return value with the error code:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object not found?
            l_retValue := c_OBJECTNOTFOUND;
        WHEN TOO_MANY_ROWS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$changeState.select',
                'TOO_MANY_ROWS for user with oid ' || l_oid);
            RAISE;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$changeState.select',
                'OTHER error for user with oid ' || l_oid);
            RAISE;
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$changeState',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op ||
            ', ai_state = ' || ai_state ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$changeState;
/

show errors;


/******************************************************************************
 * Delete the user from all groups where he should not be a member. <BR>
 * The parameters represent all groups where the user may be in. If he is in
 * one group which is not mentioned, he is dropped from that group.
 * If one of the groupIds is 0 this means not take this parameter into account.
 * There is no cumulation done within this procedure.
 *
 * @input parameters:
 * @param   ai_userId           Id of the user, whose group memberships are set.
 * @param   ai_userOid          Oid of the user, whose group memberships are
 *                              set.
 * @param   ai_groupOid01       Oid of first group where the user may be a
 *                              member.
 * @param   ai_groupOid02       Oid of 2nd group.
 * @param   ai_groupOid03       Oid of 3rd group.
 * @param   ai_groupOid04       Oid of 4th group.
 * @param   ai_groupOid05       Oid of 5th group.
 * @param   ai_groupOid06       Oid of 6th group.
 * @param   ai_groupOid07       Oid of 7th group.
 * @param   ai_groupOid08       Oid of 8th group.
 * @param   ai_groupOid09       Oid of 9th group.
 * @param   ai_groupOid10       Oid of 10th group.
 * @param   ai_groupOid11       Oid of 11th group.
 * @param   ai_groupOid12       Oid of 12th group.
 * @param   ai_groupOid13       Oid of 13th group.
 * @param   ai_groupOid14       Oid of 14th group.
 * @param   ai_groupOid15       Oid of 15th group.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_User_01$delUnneededGrNoCum
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_userOid              RAW,
    ai_groupOid01           RAW,
    ai_groupOid02           RAW,
    ai_groupOid03           RAW,
    ai_groupOid04           RAW,
    ai_groupOid05           RAW,
    ai_groupOid06           RAW,
    ai_groupOid07           RAW,
    ai_groupOid08           RAW,
    ai_groupOid09           RAW,
    ai_groupOid10           RAW,
    ai_groupOid11           RAW,
    ai_groupOid12           RAW,
    ai_groupOid13           RAW,
    ai_groupOid14           RAW,
    ai_groupOid15           RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this function
    l_groupId               INTEGER;        -- the actual group
    -- define cursor which gets all groups where user is currently in but
    -- should not:
    CURSOR delGroupCursor IS
        SELECT  groupId
        FROM    ibs_GroupUser
        WHERE   userId = ai_userId
            AND groupId = origGroupId
            AND groupId NOT IN
                (
                    SELECT  id
                    FROM    ibs_Group
                    WHERE   oid IN
                            (
                                ai_groupOid01, ai_groupOid02, ai_groupOid03,
                                ai_groupOid04, ai_groupOid05, ai_groupOid06,
                                ai_groupOid07, ai_groupOid08, ai_groupOid09,
                                ai_groupOid10, ai_groupOid11, ai_groupOid12,
                                ai_groupOid13, ai_groupOid14, ai_groupOid15
                            )
                );
    l_cursorRow             delGroupCursor%ROWTYPE;

BEGIN
-- body:
    -- loop through the cursor rows:
    FOR l_cursorRow IN delGroupCursor
    LOOP
        -- get the actual tuple values:
        l_groupId := l_cursorRow.groupId;

        -- delete the user from the group:
        l_retValue := p_Group_01$delUserNoCum (l_groupId, ai_userId);

        -- check for an error and exit the loop if there was any:
        EXIT WHEN (l_retValue <> c_ALL_RIGHT);
    END LOOP; -- while another tuple found

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$delUnneededGrNoCum',
            'Input: ai_userId = ' || ai_userId ||
            ', ai_userOid = ' || ai_userOid ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        RETURN c_NOT_OK;
END p_User_01$delUnneededGrNoCum;
/

show errors;



/******************************************************************************
 * Add the user from all groups where he is not already a member. <BR>
 * The parameters represent all groups where the user shall be in. If he is not
 * in one of the mentioned groups, he is added to that group.
 * If one of the groupIds is 0 this means not take this parameter into account.
 * There is no cumulation done within this procedure.
 *
 * @input parameters:
 * @param   ai_userId           Id of the user, whose group memberships are set.
 * @param   ai_userOid          Oid of the user, whose group memberships are
 *                              set.
 * @param   ai_groupOid01       Oid of first group where the user may be a
 *                              member.
 * @param   ai_groupOid02       Oid of 2nd group.
 * @param   ai_groupOid03       Oid of 3rd group.
 * @param   ai_groupOid04       Oid of 4th group.
 * @param   ai_groupOid05       Oid of 5th group.
 * @param   ai_groupOid06       Oid of 6th group.
 * @param   ai_groupOid07       Oid of 7th group.
 * @param   ai_groupOid08       Oid of 8th group.
 * @param   ai_groupOid09       Oid of 9th group.
 * @param   ai_groupOid10       Oid of 10th group.
 * @param   ai_groupOid11       Oid of 11th group.
 * @param   ai_groupOid12       Oid of 12th group.
 * @param   ai_groupOid13       Oid of 13th group.
 * @param   ai_groupOid14       Oid of 14th group.
 * @param   ai_groupOid15       Oid of 15th group.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_User_01$addNeededGrNoCum
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_userOid              RAW,
    ai_groupOid01           RAW,
    ai_groupOid02           RAW,
    ai_groupOid03           RAW,
    ai_groupOid04           RAW,
    ai_groupOid05           RAW,
    ai_groupOid06           RAW,
    ai_groupOid07           RAW,
    ai_groupOid08           RAW,
    ai_groupOid09           RAW,
    ai_groupOid10           RAW,
    ai_groupOid11           RAW,
    ai_groupOid12           RAW,
    ai_groupOid13           RAW,
    ai_groupOid14           RAW,
    ai_groupOid15           RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this function
    l_groupId               INTEGER;        -- the actual group
    -- define cursor which gets all groups where user is currently not in but
    -- he should be in:
    CURSOR addGroupCursor IS
        SELECT  id
        FROM    ibs_Group
        WHERE   oid IN
                (
                    ai_groupOid01, ai_groupOid02, ai_groupOid03,
                    ai_groupOid04, ai_groupOid05, ai_groupOid06,
                    ai_groupOid07, ai_groupOid08, ai_groupOid09,
                    ai_groupOid10, ai_groupOid11, ai_groupOid12,
                    ai_groupOid13, ai_groupOid14, ai_groupOid15
                )
            AND id NOT IN
                (
                    SELECT  groupId
                    FROM    ibs_GroupUser
                    WHERE   userId = ai_userId
                        AND groupId = origGroupId
                );
    l_cursorRow             addGroupCursor%ROWTYPE;

BEGIN
-- body:
    -- loop through the cursor rows:
    FOR l_cursorRow IN addGroupCursor
    LOOP
        -- get the actual tuple values:
        l_groupId := l_cursorRow.id;

        -- add the user to the group:
        l_retValue := p_Group_01$addUserSetRNoCum
            (l_groupId, ai_userId, ai_userOid, 0);

        -- check for an error and exit the loop if there was any:
        EXIT WHEN (l_retValue <> c_ALL_RIGHT);
    END LOOP; -- while another tuple found

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$addNeededGrNoCum',
            'Input: ai_userId = ' || ai_userId ||
            ', ai_userOid = ' || ai_userOid ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        RETURN c_NOT_OK;
END p_User_01$addNeededGrNoCum;
/

show errors;



/******************************************************************************
 * Set the groups for a specific user. <BR>
 * If the user is in all of these groups and no one else nothing is changed.
 * If the user is currently not in one of the groups he is added to this group.
 * If the user is a member of a group, which is not mentioned here, he is
 * removed from that group.
 * If one of the groupIds is 0 this means not to add the user to another group.
 * This procedure makes use of procedures having no cumulation. The rights
 * cumulation for the user is done after the user is assigned to the correct
 * groups. In this way it should work most performance effective.
 *
 * @input parameters:
 * @param   ai_userOid_s        Oidstring of the user, whose group memberships 
 *                              are set.
 * @param   ai_groupOid01_s     Oidstring  of first group where the user may 
 *                              be a member.
 * @param   ai_groupOid02_s     Oidstring of 2nd group.
 * @param   ai_groupOid03_s     Oidstring of 3rd group.
 * @param   ai_groupOid04_s     Oidstring of 4th group.
 * @param   ai_groupOid05_s     Oidstring of 5th group.
 * @param   ai_groupOid06_s     Oidstring of 6th group.
 * @param   ai_groupOid07_s     Oidstring of 7th group.
 * @param   ai_groupOid08_s     Oidstring of 8th group.
 * @param   ai_groupOid09_s     Oidstring of 9th group.
 * @param   ai_groupOid10_s     Oidstring of 10th group.
 * @param   ai_groupOid11_s     Oidstring of 11th group.
 * @param   ai_groupOid12_s     Oidstring of 12th group.
 * @param   ai_groupOid13_s     Oidstring of 13th group.
 * @param   ai_groupOid14_s     Oidstring of 14th group.
 * @param   ai_groupOid15_s     Oidstring of 15th group.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_User_01$setGroups
(
    -- input parameters:
    ai_userOid_s            VARCHAR2,
    ai_groupOid01_s         VARCHAR2,
    ai_groupOid02_s         VARCHAR2,
    ai_groupOid03_s         VARCHAR2,
    ai_groupOid04_s         VARCHAR2,
    ai_groupOid05_s         VARCHAR2,
    ai_groupOid06_s         VARCHAR2,
    ai_groupOid07_s         VARCHAR2,
    ai_groupOid08_s         VARCHAR2,
    ai_groupOid09_s         VARCHAR2,
    ai_groupOid10_s         VARCHAR2,
    ai_groupOid11_s         VARCHAR2,
    ai_groupOid12_s         VARCHAR2,
    ai_groupOid13_s         VARCHAR2,
    ai_groupOid14_s         VARCHAR2,
    ai_groupOid15_s         VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights
    c_NOT_ALL               CONSTANT INTEGER := 31; -- not for all objects done

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK;
                                            -- return value of this function
    l_userId                INTEGER;        -- id of the user
    l_userOid               RAW(8);         -- oid of the user
    l_groupOid01            RAW(8);         -- oid of a group
    l_groupOid02            RAW(8);         -- oid of a group
    l_groupOid03            RAW(8);         -- oid of a group
    l_groupOid04            RAW(8);         -- oid of a group
    l_groupOid05            RAW(8);         -- oid of a group
    l_groupOid06            RAW(8);         -- oid of a group
    l_groupOid07            RAW(8);         -- oid of a group
    l_groupOid08            RAW(8);         -- oid of a group
    l_groupOid09            RAW(8);         -- oid of a group
    l_groupOid10            RAW(8);         -- oid of a group
    l_groupOid11            RAW(8);         -- oid of a group
    l_groupOid12            RAW(8);         -- oid of a group
    l_groupOid13            RAW(8);         -- oid of a group
    l_groupOid14            RAW(8);         -- oid of a group
    l_groupOid15            RAW(8);         -- oid of a group

BEGIN
-- body:

    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_userOid_s, l_userOid);
    p_stringToByte (ai_groupOid01_s, l_groupOid01);
    p_stringToByte (ai_groupOid02_s, l_groupOid02);
    p_stringToByte (ai_groupOid03_s, l_groupOid03);
    p_stringToByte (ai_groupOid04_s, l_groupOid04);
    p_stringToByte (ai_groupOid05_s, l_groupOid05);
    p_stringToByte (ai_groupOid06_s, l_groupOid06);
    p_stringToByte (ai_groupOid07_s, l_groupOid07);
    p_stringToByte (ai_groupOid08_s, l_groupOid08);
    p_stringToByte (ai_groupOid09_s, l_groupOid09);
    p_stringToByte (ai_groupOid10_s, l_groupOid10);
    p_stringToByte (ai_groupOid11_s, l_groupOid11);
    p_stringToByte (ai_groupOid12_s, l_groupOid12);
    p_stringToByte (ai_groupOid13_s, l_groupOid13);        
    p_stringToByte (ai_groupOid14_s, l_groupOid14);
    p_stringToByte (ai_groupOid15_s, l_groupOid15);
    
    -- set a save point for the current transaction:
    SAVEPOINT s_User_01$setGroups;

    -- get the user id:
    BEGIN
        SELECT  id
        INTO    l_userId
        FROM    ibs_User
        WHERE   oid = l_userOid;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$setGroups.select',
                'OTHER error for ai_userOid_s ' || ai_userOid_s);
        RAISE;
    END;

    -- delete all groups which are not needed for this user:
    l_retValue := p_User_01$delUnneededGrNoCum (l_userId, l_userOid,
        l_groupOid01, l_groupOid02, l_groupOid03,
        l_groupOid04, l_groupOid05, l_groupOid06,
        l_groupOid07, l_groupOid08, l_groupOid09,
        l_groupOid10, l_groupOid11, l_groupOid12,
        l_groupOid13, l_groupOid14, l_groupOid15);

    -- add the groups which are needed for this user:
    l_retValue := p_User_01$addNeededGrNoCum (l_userId, l_userOid,
        l_groupOid01, l_groupOid02, l_groupOid03,
        l_groupOid04, l_groupOid05, l_groupOid06,
        l_groupOid07, l_groupOid08, l_groupOid09,
        l_groupOid10, l_groupOid11, l_groupOid12,
        l_groupOid13, l_groupOid14, l_groupOid15);

    -- actualize all cumulated rights for this user:
    p_Rights$updateRightsCumUser (l_userId);

    -- finish the transaction:
    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL)
                                        -- no severe error occurred?
    THEN
        commit work;                    -- make changes permanent
    ELSE                                -- there occurred an error
        ROLLBACK TO s_User_01$setGroups; -- undo changes
    END IF; -- no severe error occurred

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_User_01$setGroups;
        ibs_error.log_error (ibs_error.error, 'p_User_01$setGroups',
            'Input: ai_userOid_s = ' || ai_userOid_s ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        RETURN c_NOT_OK;
END p_User_01$setGroups;
/

show errors;



/******************************************************************************
 * DELETE a user from all his groups he is a member of. <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is deleting the user.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @userOid_s          Id of the user to be deleted from all his groups
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_User_01$delUserGroups
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_uUserOid_s           VARCHAR2
)
RETURN INTEGER
AS
    -- DEFINITIONS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1;    
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_NOT_ALL               CONSTANT INTEGER := 31;
    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_rights                INTEGER := 0;
    l_groupOid              RAW (8);
    l_uUserId               INTEGER;
    l_uUserOid              RAW (8);    
    -- define cursor:
    CURSOR GroupUser_Cursor IS 
        SELECT  g.oid
        FROM    ibs_GroupUser gu, ibs_Group g
        WHERE   gu.userId = l_uUserId
            AND gu.origGroupId = gu.groupId
            AND gu.groupId = g.id;            
    l_cursorRow             GroupUser_Cursor%ROWTYPE;
    
BEGIN 
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_uUserOid_s, l_uUserOid);

    SELECT  id
    INTO    l_uUserId
    FROM    ibs_User u
    WHERE   oid = l_uUserOid;

    -- loop through the cursor rows:
    FOR l_cursorRow IN GroupUser_Cursor
    LOOP    
        -- get the first object:
        l_groupOid := l_cursorRow.oid;         
        -- get rights for this user
        l_rights := p_Rights$checkRights (
            l_uUserOid,                 -- given object to be accessed by user
            l_groupOid,                 -- container of given object
            ai_userId,                  -- user_id
            ai_op,                      -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
            l_rights);                  -- returned value

        -- check if the user has the necessary rights
        IF (l_rights <> ai_op)     -- the user does not have the rights?
        THEN
            l_retValue := c_NOT_ALL;
        END IF;    -- if the user does not have the rights
        EXIT WHEN l_retValue <> c_ALL_RIGHT;
    END LOOP;   -- while another tuple found

    -- the user can be deleted from all groups?
    IF (l_retValue = c_ALL_RIGHT)         
    THEN
        -- delete user from all groups:
        DELETE  ibs_GroupUser
        WHERE   userId = l_uUserId;
        -- recompute the rights of the user:
        p_Rights$updateRightsCumUser (l_uUserId);
        COMMIT WORK;
    ELSE 
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END IF;     -- the user does not have the rights

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$delUserGroups',
            'Input: ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_uUserOid_s = ' || ai_uUserOid_s ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
    l_retValue := c_INSUFFICIENT_RIGHTS;    
    RETURN l_retValue;
END p_User_01$delUserGroups;
/

show errors;


/******************************************************************************
 * Get the basic information of an user. <BR>
 *
 * @input parameters:
 * @param   ai_userId           ID of the user for whom to get the info.
 * @param   ai_domainId         Domain where the user resides.
 *
 * @output parameters:
 * @param   ao_userName         The name of the user.
 * @param   ao_password         The user's password.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new function:
CREATE OR REPLACE FUNCTION p_User_01$getInfo
(
    -- input parameters:
    ai_userId              INTEGER,
    ai_domainId            INTEGER,
    ao_userName            OUT VARCHAR2,
    ao_password            OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK               CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT            CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue             INTEGER := c_ALL_RIGHT;
                                            -- return value of this procedure

-- body:
BEGIN
    -- initialize the output parameters with null
    ao_userName := '';
    ao_password := '';
    BEGIN
        SELECT  name, password
        INTO    ao_userName, ao_password
        FROM    ibs_User
        WHERE   id = ai_userId
        AND     domainId = ai_domainId;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
          l_retValue := c_NOT_OK;
        WHEN OTHERS THEN
          ibs_error.log_error ( ibs_error.error, 'p_User_01$getInfo',
              'default case : ' ||
              ', errorcode = ' || SQLCODE ||
              ', errormessage = ' || SQLERRM);
        RAISE;    
    END;
        
    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_User_01$getInfo',
                              ' ai_userId: ' || ai_userId || ', ' ||
                              ' ai_domainId: ' || ai_domainId || ', ' ||
                              ' sqlcode: ' || SQLCODE ||
                              ' sqlerrm: ' || SQLERRM );
        RETURN c_NOT_OK;
END p_User_01$getInfo;
/

show errors;

/******************************************************************************
 * Returns the settings for notification and all addresses. <BR>
 *
 * 
 * @input parameters:
 * .
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_User_01$getNotificationData
(
    -- common input parameters:
    ai_useroid_s                 VARCHAR2,
    -- output parameters
    ao_username                 OUT VARCHAR2,
    ao_notificationKind         OUT INTEGER,     
    ao_sendSms                  OUT NUMBER,   
    ao_addWeblink               OUT NUMBER,
    ao_email                    OUT VARCHAR2,
    ao_smsemail                 OUT VARCHAR2
) 
RETURN INTEGER

AS
    
    -- definitions:
    -- define return constants :
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1;     
    -- return value of this procedure
    l_retValue                  INTEGER := c_ALL_RIGHT;                 
    l_oid                       RAW (8) := hexToRaw ('0000000000000000');
    l_count                     INTEGER := 0;
    l_userId                    INTEGER := 0;
-- body:
BEGIN 
    -- convert oidstring to binaryOID
    p_stringToByte (ai_useroid_s, l_oid); 
    
    SELECT COUNT(*)    
    INTO   l_count
    FROM   ibs_User u,
          ibs_UserProfile p, 
          ibs_Object tabAddress, 
          ibs_UserAddress_01 a 
    WHERE  p.userid = u.id
    AND    u.oid = l_oid
    AND    p.oid = tabAddress.containerId
    AND    tabAddress.oid = a.oid;
    
    
    IF (l_count = 1) 
    THEN
        l_retValue := c_ALL_RIGHT;
    END IF;
       


    -- make an select for all type specific tables:
   IF (l_retValue = c_ALL_RIGHT) THEN
   BEGIN
    
        SELECT  a.email, a.smsemail, u.id, u.name,
                p.notificationKind,
                p.sendSms,
                p.addWeblink 
        INTO    ao_email, ao_smsemail, l_userId, ao_username, ao_notificationKind,
                ao_sendSms, ao_addWeblink
        FROM    ibs_User u,
                ibs_UserProfile p, 
                ibs_Object tabAddress, 
                ibs_UserAddress_01 a 
        WHERE   p.userid = u.id
            AND u.oid = l_oid
            AND p.oid = tabAddress.containerId
            AND tabAddress.oid = a.oid;
    EXCEPTION
        WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$getNotificationData',
        'Error in retrieve the data');
        RAISE;
    END;
    END IF;
        
COMMIT WORK;
          
-- return the state value:
RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$getNotificationData',
            'userOId: '|| ai_useroid_s ||
             '; sqlcode: '      || SQLCODE ||
            ', sqlerrm: '       || SQLERRM );
    RETURN c_NOT_OK;

END p_User_01$getNotificationData;
/

show errors;

EXIT;
