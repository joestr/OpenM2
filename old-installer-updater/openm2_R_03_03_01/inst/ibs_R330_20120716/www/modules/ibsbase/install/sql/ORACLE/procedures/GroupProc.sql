/******************************************************************************
 * All stored procedures regarding the group table. <BR>
 *
 * @version     2.21.0016, 04.07.2002 KR
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */

/******************************************************************************
 * Add a group to another group determined by their ids. <BR>
 * This function does not use any transactions, so it may be called from any
 * kind of code.
 *
 * @input parameters:
 * @param   ai_majorGroupId     Id of the group where the group shall be added.
 * @param   ai_minorGroupId     Id of the group to be added.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_OK                  An error occurred.
 */
CREATE OR REPLACE FUNCTION p_Group_01$addGroupId
(
    -- input parameters:
    ai_majorGroupId         INTEGER,
    ai_minorGroupId         INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- counter

-- body:
BEGIN
    -- check if the sub group is not already in the super group:
    BEGIN
        SELECT  COUNT (*)
        INTo    l_rowCount
        FROM    ibs_GroupUser
        WHERE   groupId = ai_majorGroupId
            AND userId = ai_minorGroupId
            AND groupId = origGroupId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check if group relationship already exists';
            RAISE;                      -- call common exception handler  
    END;


    IF (l_rowCount = 0)                 -- group relationship does not exist?
    THEN
        -- insert the sub group into the super group and generate all inherited
        -- tuples:
        BEGIN
            INSERT INTO ibs_GroupUser
                    (state, groupId, userId, roleId, origGroupId,
                    idPath)
            SELECT  2, g.groupId, u.userId, 0, u.origGroupId,
                    u.idPath || g.idPath
            FROM    (
                        SELECT  userId, origGroupId, idPath
                        FROM    ibs_GroupUser
                        WHERE   groupId = ai_minorGroupId
                        UNION
                        SELECT  ai_minorGroupId AS userId,
                                ai_majorGroupId AS origGroupId, 
                                INTTORAW (ai_minorGroupId, 4) AS idPath
                        FROM    DUAL
                    ) u,
                    (
                        SELECT  groupId, idPath
                        FROM    ibs_GroupUser
                        WHERE   INSTR (idPath,
                                    INTTORAW (ai_majorGroupId, 4), 1, 1) = 1
                        UNION
                        SELECT  ai_majorGroupId AS groupId,
                                INTTORAW (ai_majorGroupId, 4) AS idPath
                        FROM    DUAL
                    ) g;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'insert';
                RAISE;                  -- call common exception handler  
        END;

        -- actualize all cumulated rights:
        p_Rights$updateRightsCumGroup (ai_minorGroupId);
    END IF; -- if group relationship does not exist

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Group_01$addGroup',
            'Input: ai_majorGroupId: ' || ai_majorGroupId ||
            ', ai_minorGroupId: ' || ai_minorGroupId ||
            ', l_rowCount: ' || l_rowCount ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$addGroupId;
/

show errors;


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
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Group_01$create
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
    l_containerId           ibs_Object.oid%TYPE := c_NOOID;
    l_groupsOid             ibs_Object.oid%TYPE;
    l_groupsOid_s           VARCHAR2 (18);
    l_oid                   ibs_Object.oid%TYPE := c_NOOID;
    l_domainId              ibs_Domain_01.id%TYPE;
    l_state                 ibs_Object.state%TYPE;
    l_rights                ibs_RightsKeys.rights%TYPE;
    l_name                  ibs_Object.name%TYPE := ai_name;
    l_newGroupId            ibs_Group.id%TYPE;
    l_groupId               ibs_Group.id%TYPE;

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_containerId_s, l_containerId);

    BEGIN
        -- get the domain data:
        SELECT  d.groupsOid, d.id
        INTO    l_groupsOid, l_domainId
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
    p_byteToString (l_groupsOid, l_groupsOid_s);

    /*[SPCONV-ERR(46)]:BEGIN TRAN statement ignored*/
    -- create base object:
    l_retValue := p_Object$performCreate (
        ai_userId, ai_op, ai_tVersionId, ai_name, l_groupsOid_s,
        ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description,
        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        BEGIN
            -- get the state and name from ibs_Object:
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
            -- check if the group exists already:
            SELECT  COUNT (*)
            INTO    l_rowCount
            FROM    ibs_Group
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check group existence';
                RAISE;                  -- call common exception handler
        END;

        IF (l_rowCount = 1)             -- group was found?
        THEN
            BEGIN
                -- try to set data of the group:
                UPDATE  ibs_Group
                SET     name = l_name,
                        state = l_state,
                        domainId = l_domainId
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'update ibs_Group';
                    RAISE;              -- call common exception handler
            END;
        -- end if group was found
        ELSE                            -- group was not found
            BEGIN
                -- create new tuple for group:
                INSERT INTO ibs_Group
                        (oid, name, state, domainId)
                VALUES  (l_oid, l_name, l_state, l_domainId);
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'create ibs_User';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- else user was not found

        BEGIN
            -- get the id:
            SELECT  id
            INTO    l_newGroupId
            FROM    ibs_Group
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get group id';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            -- check if container is a group:
            -- get the id of the container group:
            SELECT  id 
            INTO    l_groupId
            FROM    ibs_Group
            WHERE   oid = l_containerId;

            -- at this point we know that the container is a group because 
            -- otherwise the above query would raise an exception NO_DATA_FOUND.
            -- add group:
            l_retValue := p_Group_01$addGroupId (l_groupId, l_newGroupId);

/* already done in p_Group_01$addGroupId
            -- actualize all cumulated rights:
            p_Rights$updateRightsCum ();
*/
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- container is not a group
                l_rowCount := 0;
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check for group';
                RAISE;                  -- call common exception handler
        END;

        -- set rights of group on its own data:
        -- (this is necessary to allow the group to be shown in some
        -- dialogs)
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view', 'read', 'viewElems');
        p_Rights$addRights (l_oid, l_newGroupId, l_rights, 1);
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
            ', ao_oid_s = ' || ao_oid_s ||
            ', l_domainId = ' || l_domainId ||
            ', l_groupsOid = ' || l_groupsOid ||
            ', l_groupsOid_s = ' || l_groupsOid_s ||
            ', l_newGroupId = ' || l_newGroupId ||
            ', l_oid = ' || l_oid ||
            ', l_name = ' || l_name ||
            ', l_state = ' || l_state ||
            ', l_rights = ' || l_rights ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Group_01$create', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Group_01$create;
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
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Group_01$change
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           INTEGER,    
    ai_state                INTEGER 
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_NAME_ALREADY_EXISTS   CONSTANT INTEGER := 51;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');    

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_domainId              INTEGER;
    l_given                 INTEGER := 0;

BEGIN
    p_stringToByte (ai_oid_s, l_oid);

    -- compute domain id:
    -- (divide user id by 0x01000000, i.e. get the first byte)
    l_domainId := (ai_userId - MOD (ai_userId, 16777216)) / 16777216;

    -- is the name already given in this domain?
    SELECT  COUNT (*) 
    INTO    l_given
    FROM    ibs_Group g, ibs_Object o 
    WHERE   g.oid = o.oid
        AND o.name = ai_name
        AND g.domainId = l_domainId        
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
            
        IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
        THEN
            -- update the other values, get the state from the object:
            UPDATE  ibs_Group
            SET     name = ai_name, 
                    state = ( 
                                SELECT state
                                FROM   ibs_Object o
                                WHERE  oid =  l_oid
                            )
            WHERE   oid = l_oid;
        END IF; -- if operation properly performed
    END IF; -- else name not given

    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error (ibs_error.error, 'p_Group$change',        
        'Input: ai_oid_s = ' || ai_oid_s || 
        ', ai_userId = ' || ai_userId ||
        ', ai_op = ' || ai_op ||      
        ', ai_name = ' || ai_name ||
        ', ai_validUntil = ' || ai_validUntil ||
        ', ai_description = ' || ai_description ||
        ', ai_showInNews = ' || ai_showInNews ||        
        ', ai_state = ' || ai_state ||
        '; errorcode = ' || SQLCODE ||
        ', errormessage = ' || SQLERRM);
    -- return error code:
    RETURN c_NOT_OK;
END  p_Group_01$change;
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
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
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
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Group_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- common output parameters:
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
    ao_ownerName            OUT VARCHAR2,
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2,
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2,
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT INTEGER,    
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2
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
    l_oid                   RAW (8);

BEGIN
    l_retValue := p_Object$performRetrieve (
        ai_oid_s, ai_userId, ai_op, 
        ao_state, ao_tVersionId, ao_typeName, ao_name,
        ao_containerId, ao_containerName, ao_containerKind, 
        ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName, 
        ao_creationDate, ao_creator, ao_creatorName, 
        ao_lastChanged, ao_changer, ao_changerName, 
        ao_validUntil, ao_description, ao_showInNews, 
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,        
        l_oid);

/* seems to be not necessary
    IF  (l_retValue = c_ALL_RIGHT)      -- the object was found?
    THEN

    END IF; -- if the object was found
*/

    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Group_01$retrieve',
            'Input: ai_oid_s: ' || ai_oid_s ||
            ', ai_userId: ' || ai_userId ||
            ', ai_op: ' || ai_op ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$retrieve;
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
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Group_01$delete
(
    -- input parameters:
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
    c_ST_DELETED           CONSTANT INTEGER := 1; -- state to indicate deletion
                                            -- of object    

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of procedure
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text    
    l_oid                   RAW (8);        -- the oid of the object to be deleted  
    l_rights                INTEGER := 0;   -- actual rights
    l_id                    INTEGER;        -- the id of the group
    l_dummyOid              RAW(8);         -- dummyOid only for oracle
    l_rowCount              INTEGER;        -- row counter

BEGIN
    -- conversions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    COMMIT WORK; -- finish previous and begin new TRANSACTION

        -- get the group data:
        BEGIN
            SELECT  id
            INTO    l_id
            FROM    ibs_Group
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get group data';
                RAISE;                  -- call common exception handler
        END;

        -- check if the group is a system group:
        BEGIN
            SELECT  COUNT (id)
            INTO    l_rowCount
            FROM    ibs_Domain_01
            WHERE   adminGroupId = l_id
                OR  allGroupId = l_id
                OR  userAdminGroupId = l_id
                OR  structAdminGroupId = l_id;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check for system group';
                RAISE;                  -- call common exception handler
        END;

        IF (l_rowCount > 0)             -- the group is a system group?
        THEN
            -- set corresponding return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        -- the group is a system group?
        ELSE                            -- the group is no system group
            -- group may be deleted

            -- delete base object and references:
            l_retValue :=
                p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_dummyOid);

            -- check if there was an error:
            IF (l_retValue = c_ALL_RIGHT) -- operation properly performed?
            THEN
                -- delete object type specific data:
                -- (delete all type specific tuples which are not within
                -- ibs_Object)

                -- delete all rights for the deleted group:
                p_Rights$deleteAllUserRights (l_id);

                -- actualize all cumulated rights:
                p_Rights$updateRightsCumGroup (l_id);

                -- delete all the entries in ibs_GroupUser:
                BEGIN
                    DELETE  ibs_GroupUser
                    WHERE   (   userid = l_id
                            OR  groupid = l_id
                            OR  origGroupId = l_id)
                        OR (INSTR (idPath, intToRaw (l_id, 8), 1, 1) > 0);
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'delete group/user data';
                        RAISE;          -- call common exception handler
                END;

                -- set object as deleted:
                BEGIN
                    UPDATE  ibs_Group
                    SET     state = c_ST_DELETED
                    WHERE   id = l_id;
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'update group state';
                        RAISE;          -- call common exception handler
                END;
            END IF; -- if operation properly performed
    END IF; -- else the group is no system group

    -- finish the transaction:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
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
        ibs_error.log_error (ibs_error.error, 'p_Group_01$delete', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$delete;
/
show errors;


/******************************************************************************
 * Add a new user to a group and set the rights of the group on the user. <BR>
 * If there are already rights set the new rights are added to the existing
 * rights. <BR>
 * The rights for the user are not cumulated.
 *
 * @input parameters:
 * @param   ai_groupId          Id of the group where the user shall be added.
 * @param   ai_userId           Id of the user to be added.
 * @param   ai_userOid          Oid of the user to be added.
 * @param   ai_rights           Rights to set for the group on the user.
 *                              null ... don't set any rights
 *                              0 ...... don't set any rights
 *                              -1 ..... set default rights
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Group_01$addUserSetRNoCum
(
    -- input parameters:
    ai_groupId              INTEGER,
    ai_userId               INTEGER,
    ai_userOid              RAW,
    ai_rights               INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this function
    l_rights                INTEGER := ai_rights; -- the current rights
    l_superGroupId          INTEGER;        -- id of actual super group
    l_idPath                RAW (254);      -- posNoPath of the group
    -- define cursor to group hierarchie for each group where the group is a
    -- (direct or indirect) member:
    -- these are the groups where the user must be added
    CURSOR groupUserCursor IS
        SELECT  groupId, idPath
        FROM    ibs_GroupUser 
        WHERE   TO_NUMBER (userId) = TO_NUMBER (ai_groupId);
    l_cursorRow             groupUserCursor%ROWTYPE;

BEGIN
-- body:
    -- insert user into group:
    BEGIN
        INSERT INTO ibs_GroupUser
                (state, groupId, userId, roleId, origGroupId,
                idPath)
        VALUES  (2, ai_groupId, ai_userId, 0, ai_groupId,
                inttoraw (ai_groupId, 2));
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_Group_01$addUserSetRNoCum.insert',
                'OTHER error for ai_groupId ' || ai_groupId ||
                ' and ai_userId ' || ai_userId);
        RAISE;
    END;

    -- set the rights of the group on the user:
    IF (l_rights = -1)              -- set default rights?
    THEN
        -- get the rights to be set:
        BEGIN
            SELECT  SUM (id)
            INTO    l_rights
            FROM    ibs_Operation
            WHERE   name IN ('view');
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                    'p_Group_01$addUserSetRNoCum.select',
                    'OTHER error.');
            RAISE;
        END;
    END IF; -- set default rights
    -- set the rights:
    IF (l_rights <> 0)              -- there shall be some rights set?
    THEN
        p_Rights$addRights (ai_userOid, ai_groupId, l_rights, 1);
    END IF; -- there shall be some rights set

    -- store the relationships with all groups which are above the actual 
    -- one:
    -- loop through the cursor rows:
    FOR l_cursorRow IN groupUserCursor
    LOOP
        -- get the actual tuple values:
        l_superGroupId := l_cursorRow.groupId;
        l_idPath := l_cursorRow.idPath;

        -- insert user into all groups where this group is part of:
        BEGIN
            INSERT INTO ibs_GroupUser
                    (state, groupId, userId, roleId, origGroupId,
                    idPath)
            VALUES  (2, l_superGroupId, ai_userId, 0, ai_groupId,
                    l_idPath);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                    'p_Group_01$addUserSetRNoCum.insert2',
                    'OTHER error for l_superGroupId ' || l_superGroupId ||
                    ', ai_groupId ' || ai_groupId ||
                    ', ai_userId ' || ai_userId ||
                    ' and idPath ' || l_idPath);
            RAISE;
        END;

        -- check for an error and exit the loop if there was any:
        EXIT WHEN (l_retValue <> c_ALL_RIGHT);
    END LOOP; -- while another tuple found

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Group_01$addUserSetRNoCum',
            'Input: ai_groupId = ' || ai_groupId ||
            ', ai_userId = ' || ai_userId ||
            ', ai_userOid = ' || ai_userOid ||
            ', ai_rights = ' || ai_rights ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        RETURN c_NOT_OK;
END p_Group_01$addUserSetRNoCum;
/

show errors;


/******************************************************************************
 * Add a new user to a group and set the rights of the group on the user. <BR>
 * If there are already rights set the new rights are added to the existing
 * rights. <BR>
 * The rights for the user are newly cumulated at the end of this procedure.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is adding the user.
 * @param   @groupId            Id of the group where the user shall be added.
 * @param   @userOid            Id of the user to be added.
 * @param   @roleOid            Id of the role to be added. 
 * @param   @rights             Rights to set for the group on the user.
 *                              null ... don't set any rights
 *                              0 ...... don't set any rights
 *                              -1 ..... set default rights
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Group_01$addUserSetRights
(
    -- input parameters:
    ai_userId               INTEGER ,
    ai_groupOid             RAW,
    ai_userOid              RAW,
    ai_roleOid              RAW DEFAULT hexToRaw ('0000000000000000'),
    ai_rights               INTEGER DEFAULT 0
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of this function
    l_groupId               INTEGER;        -- the group id
    l_uUserId               INTEGER;        -- the user id
    l_roleId                INTEGER;        -- the role id
    l_rights                INTEGER := ai_rights; -- the current rights
    l_superGroupId          INTEGER;        -- id of actual super group
    l_idPath                RAW (254);      -- posNoPath of the group

BEGIN
    BEGIN
        -- get the id of the group where the user shall be added:
        SELECT  id
        INTO    l_groupId
        FROM    ibs_Group 
        WHERE   oid = ai_groupOid;

        -- get the id of the user to be added:
        SELECT  id
        INTO    l_uUserId
        FROM    ibs_User u
        WHERE   oid = ai_userOid;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_Group_01$addUserSetRights.select',
                'OTHER error for ai_groupOid ' || ai_groupOid ||
                ' and ai_userOid ' || ai_userOid);
            RAISE;
    END;

    -- at this point we know that both the group and the user exist.
    -- insert user into group:
    l_retValue :=p_Group_01$addUserSetRNoCum
        (l_groupId, l_uUserId, ai_userOid, ai_rights);

    -- check if there was a problem:
    IF (l_retValue = c_ALL_RIGHT)       -- no problem?
    THEN    
        -- actualize all cumulated rights:
        p_Rights$updateRightsCumUser (l_uUserId);
    END IF; -- no problem

    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Group_01$addUserSetRights',
            'Input: ai_userId: ' || ai_userId ||
            ', ai_groupOid: ' || ai_groupOid ||
            ', ai_userOid: ' || ai_userOid ||
            ', ai_roleOid: ' || ai_roleOid ||
            ', ai_rights: ' || ai_rights ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$addUserSetRights;
/

show errors;

/******************************************************************************
 * Add a new user to a group. <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is adding the user.
 * @param   @groupId            Id of the group where the user shall be added.
 * @param   @userOid            Id of the user to be added.
 * @param   @roleOid            Id of the role to be added. 
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Group_01$addUser
(
    -- input parameters:
    ai_userId               INTEGER ,
    ai_groupOid             RAW,
    ai_userOid              RAW,
    ai_roleOid              RAW DEFAULT hexToRaw ('0000000000000000')
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_rights                INTEGER;

BEGIN
    -- get the rights of the group on the user:
    BEGIN
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('view');
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;
    END;

    l_retValue := p_Group_01$addUserSetRights (
        ai_userId, ai_groupOid, ai_userOid, ai_roleOid, l_rights);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Group_01$addUser',
            'Input: ai_userId: ' || ai_userId ||
            ', ai_groupOId: ' || ai_groupOId ||
            ', ai_userOId: ' || ai_userOId ||
            ', ai_roleOId: ' || ai_roleOId ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$addUser;
/

show errors;


/******************************************************************************
 * Add a group to another group. <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is adding the group.
 * @param   ai_majorGroupOid    Oid of the group where the group shall be
 *                              added.
 * @param   ai_minorGroupOid    Oid of the group to be added.
 * @param   ai_roleOid          Oid of the role to be added.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_OK                  An error occurred.
 */
CREATE OR REPLACE FUNCTION p_Group_01$addGroup
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_majorGroupOid        RAW,
    ai_minorGroupOid        RAW,
    ai_roleOid              RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_majorGroupId          INTEGER;        -- id of major group
    l_minorGroupId          INTEGER;        -- id of minor group

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION
        -- get id of major group:
        BEGIN
            SELECT  id
            INTO    l_majorGroupId
            FROM    ibs_Group 
            WHERE   oid = ai_majorGroupOid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get major group id';
                RAISE;                  -- call common exception handler  
        END;
        
        -- get id of minor group:
        BEGIN
            SELECT  id
            INTO    l_minorGroupId
            FROM    ibs_Group u
            WHERE   oid = ai_minorGroupOid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get minor group id';
                RAISE;                  -- call common exception handler  
        END;

        -- at this point we know that both the group and the sub group exist.
        -- add the minor group to the major group:
        l_retValue := p_Group_01$addGroupId (l_majorGroupId, l_minorGroupId);

    -- finish the transaction:
    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
    END IF; -- else an error occurred

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; l_majorGroupId = ' || l_majorGroupId ||
            ', l_minorGroupId = ' || l_minorGroupId ||
            ', ai_userId = ' || ai_userId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Group_01$addGroup', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$addGroup;
/

show errors;


/******************************************************************************
 * Delete a user from a group. <BR>
 * The rights for the user are not cumulated. There is also no rights check
 * done.
 *
 * @input parameters:
 * @param   ai_groupId          Id of the group where the user shall be deleted.
 * @param   ai_userId           Id of the user to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Group_01$delUserNoCum
(
    -- input parameters:
    ai_groupId              INTEGER,
    ai_userId               INTEGER    
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text       
    l_rights                INTEGER := 0;   -- the current rights
    l_rowCount              INTEGER;        -- row counter

BEGIN
-- body:

    -- check if the group is a system group:
    BEGIN
        SELECT  COUNT (id)
        INTO    l_rowCount
        FROM    ibs_Domain_01
        WHERE   allGroupId = ai_groupId;      
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check for system group';
            RAISE;                      -- call common exception handler
    END;
     
    IF (l_rowCount > 0)                 -- the group is a system group?
    THEN
        -- set corresponding return value:
        l_retValue := c_INSUFFICIENT_RIGHTS;
     -- the group is a system group?        
     ELSE                               -- the group is no system group
        -- user may be deleted
        -- delete user from all groups where the origGroupId is the GroupId:
        BEGIN
            DELETE FROM ibs_GroupUser 
            WHERE   origGroupId = ai_groupId 
                AND userId = ai_userId;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'delete group/user data';
                RAISE;                  -- call common exception handler
        END;
     END IF; -- else the group is no system group

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_groupId = ' || ai_groupId ||
            ', ai_userId = ' || ai_userId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Group_01$delUserNoCum', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$delUserNoCum;
/

show errors;


/******************************************************************************
 * Delete a user from a group. <BR>
 * The rights for the user are newly cumulated at the end of this procedure.
 *
 * @input parameters:
 * @param   ai_userId           Id of the user who is adding the user.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_groupOid_s       Oid of the group where the user shall be
 *                              deleted.
 * @param   ai_userOid_s        Oid of the group to be deleted.
 * @param   ai_roleOid_s        Oid of the role to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Group_01$delUser
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_groupOid_s           VARCHAR2,
    ai_userOid_s            VARCHAR2,
    ai_roleOid_s            VARCHAR2
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
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_groupId               INTEGER;        -- id of group to add the user
    l_uUserId               INTEGER;        -- id of user to be added
    l_groupOid              RAW (8);        -- oid of group
    l_userOid               RAW (8);        -- oid of user
    l_rights                INTEGER := 0;   -- the current rights
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text       
    l_rowCount              INTEGER;        -- row counter    

BEGIN
    -- convert string representations to oids:
    p_stringToByte (ai_groupOid_s, l_groupOid);
    p_stringToByte (ai_userOid_s, l_userOid);
    
    COMMIT WORK; -- finish previous and begin new TRANSACTION
    
        BEGIN
            -- get the user id:
            BEGIN
                SELECT  id
                INTO    l_uUserId
                FROM    ibs_User u 
                WHERE   oid = l_userOid;      
             EXCEPTION
                 WHEN OTHERS THEN       -- any error
                     -- create error entry:
                     l_ePos := 'get the user id';
                    RAISE;              -- call common exception handler
            END;        

            -- get the group id:
            BEGIN
                SELECT  id
                INTO    l_groupId
                FROM    ibs_Group 
                WHERE   oid = l_groupOid;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'get the group id';
                    RAISE;              -- call common exception handler
            END;            
            
            -- check if the group is a system group:
            BEGIN
                SELECT  COUNT (*)
                INTO    l_rowCount
                FROM    ibs_Domain_01
                WHERE   allGroupId = l_groupId;      
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'check for system group';
                    RAISE;              -- call common exception handler
            END;
         
            IF (l_rowCount > 0)         -- the group is a system group?
            THEN
               -- set corresponding return value:
               l_retValue := c_INSUFFICIENT_RIGHTS;
               l_rights   := ai_op - 1;
            -- the group is a system group?        
            ELSE                        -- the group is no system group
            -- user may be deleted
            -- get rights for this user:
            l_rights := p_Rights$checkRights (
                l_userOid,              -- given object to be accessed by user
                l_groupOid,             -- container of the given object
                ai_userId,              -- user_id
                ai_op,                  -- required rights user must have to 
                                        -- insert/update object
                l_rights                -- returned value
                );
            END IF; -- else user may be deleted
            -- check if the user has the necessary rights
            IF (l_rights = ai_op)       -- the user has the rights?
            THEN
                -- delete user from the group:
                l_retValue := p_Group_01$delUserNoCum (l_groupId, l_uUserId);

                -- check if everything ws o.k.:
                IF (l_retValue = c_ALL_RIGHT)   -- no error?
                THEN
                    -- actualize all cumulated rights:
                    p_Rights$updateRightsCumUser (l_uUserId);
                END IF; -- no error

                COMMIT WORK;
            ELSE
                l_retValue := c_INSUFFICIENT_RIGHTS;
            END IF; -- else the user does not have the rights

        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- the group or the user does not exist?
                l_retValue := c_OBJECTNOTFOUND;
        END;

    -- finish the transaction:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
    END IF; -- else an error occurred

    -- return the state value:
    RETURN  l_retValue;


EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_op = ' || ai_op ||
            ', ai_groupOid_s = ' || ai_groupOid_s ||
            ', ai_userOid_s = ' || ai_userOid_s ||
            ', ai_roleOid_s = ' || ai_roleOid_s ||            
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Group_01$delUser', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$delUser;
/
show errors;


/******************************************************************************
 * Delete a group from another group determined by their ids. <BR>
 * This function does not use any transactions, so it may be called from any
 * kind of code.
 *
 * @input parameters:
 * @param   ai_majorGroupId     Id of the group where the group shall be
 *                              deleted.
 * @param   ai_minorGroupId     Id of the group to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_OK                  An error occurred.
 */
CREATE OR REPLACE FUNCTION p_Group_01$delGroupId
(
    -- input parameters:
    ai_majorGroupId         INTEGER,
    ai_minorGroupId         INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- counter

-- body:
BEGIN
    -- check if the sub group is within the super group:
    BEGIN
        SELECT  COUNT (*)
        INTo    l_rowCount
        FROM    ibs_GroupUser
        WHERE   groupId = ai_majorGroupId
            AND userId = ai_minorGroupId
            AND groupId = origGroupId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check if group relationship already exists';
            RAISE;                      -- call common exception handler  
    END;

    IF (l_rowCount > 0)                 -- group relationship exists?
    THEN
        -- delete the sub group from the super group and drop all inherited
        -- tuples:
        BEGIN
            DELETE  ibs_GroupUser 
            WHERE   (INSTR (idPath,
                            intToRaw (ai_minorGroupId, 8) ||
                            intToRaw (ai_majorGroupId, 8), 1, 1) > 0);
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'delete';
                RAISE;                  -- call common exception handler  
        END;

        -- actualize all cumulated rights:
        p_Rights$updateRightsCumGroup (ai_minorGroupId);
    END IF; -- if group relationship exists

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Group_01$delGroup',
            'Input: ai_majorGroupId: ' || ai_majorGroupId ||
            ', ai_minorGroupId: ' || ai_minorGroupId ||
            ', l_rowCount: ' || l_rowCount ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$delGroupId;
/

show errors;


/******************************************************************************
 * Delete a group from another group. <BR>
 * There is also a rights check done in this procedure. <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is adding the group.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_majorGroupOid    Oid of the group where the group shall be
 *                              added.
 * @param   ai_minorGroupOid    Oid of the group to be added.
 * @param   ai_roleOid          Oid of the role to be added.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_OK                  An error occurred.
 */
CREATE OR REPLACE FUNCTION p_Group_01$delGroup
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_majorGroupOid_s      VARCHAR2,
    ai_minorGroupOid_s      VARCHAR2,
    ai_roleOid_s            VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_majorGroupOid         RAW (8);        -- oid of major group
    l_minorGroupOid         RAW (8);        -- oid of minor group
    l_majorGroupId          INTEGER;        -- id of major group
    l_minorGroupId          INTEGER;        -- id of minor group
    l_rights                INTEGER;        -- the rights of the user on the
                                            -- current group

-- body:
BEGIN
    -- convert oid strings to oids:
    p_stringToByte (ai_majorGroupOid_s, l_majorGroupOid);
    p_stringToByte (ai_minorGroupOid_s, l_minorGroupOid);

    COMMIT WORK; -- finish previous and begin new TRANSACTION
        -- get id of major group:
        BEGIN
            SELECT  id
            INTO    l_majorGroupId
            FROM    ibs_Group 
            WHERE   oid = l_majorGroupOid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get major group id';
                RAISE;                  -- call common exception handler  
        END;
        
        -- get id of minor group:
        BEGIN
            SELECT  id
            INTO    l_minorGroupId
            FROM    ibs_Group u
            WHERE   oid = l_minorGroupOid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get minor group id';
                RAISE;                  -- call common exception handler  
        END;

        -- at this point we know that both the group and the sub group exist.
        -- get rights for the current user:
        l_rights := p_Rights$checkRights (
            l_minorGroupOid,            -- given object to be accessed by user
            l_majorGroupOid,            -- container of the given object
            ai_userId,                  -- user_id
            ai_op,                      -- required rights user must have to 
                                        -- insert/update object
            l_rights                    -- returned value
            );

        -- check if the user has the necessary rights
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN
            -- delete the minor group from the major group:
            l_retValue :=
                p_Group_01$delGroupId (l_majorGroupId, l_minorGroupId);
        ELSE                            -- the user does not have the rights
            -- set corresponding return value:
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights
        -- add the minor group to the major group:
        l_retValue := p_Group_01$delGroupId (l_majorGroupId, l_minorGroupId);

    -- finish the transaction:
    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
    END IF; -- else an error occurred

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; l_majorGroupId = ' || l_majorGroupId ||
            ', ai_majorGroupOid_s = ' || ai_majorGroupOid_s ||
            ', l_minorGroupId = ' || l_minorGroupId ||
            ', ai_minorGroupOid_s = ' || ai_minorGroupOid_s ||
            ', ai_userId = ' || ai_userId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Group_01$delGroup', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$delGroup;
/

show errors;


/******************************************************************************
 * Copies a Group_01 object and all its values (incl. rights check). <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_oid              Oid of group to be copied.
 * @param   ai_userId           Id of user who is copying the group.
 * @param   ai_newOid           Oid of the new group.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 An error occurred.
 */
CREATE OR REPLACE FUNCTION p_Group_01$BOCopy
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
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;  -- tuple not found
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- active state of object

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- row counter
    l_groupId               INTEGER;        -- id of copied group
    l_newGroupId            INTEGER;        -- new id of the group
    l_origGroupId           INTEGER;        -- id of original group in group
                                            -- hierarchy
    l_idPath                RAW (254);      -- posNoPath of group/user
                                            -- relationship
    l_userId                INTEGER;        -- the actual user within the group

    -- define cursor:
    -- get all users and groups in the old group
    CURSOR GroupCopy_Cursor IS
        SELECT  userId,
                DECODE (origGroupId, l_groupId, l_newGroupId, origGroupId) 
                    AS origGroupId,
                (idPath || INTTORAW (l_newGroupId, 4)) AS idPath
        FROM    ibs_GroupUser
        WHERE   groupId IN (
                    SELECT  userId 
                    FROM    ibs_GroupUser
                    WHERE   groupId = l_groupId
                        AND origGroupId = l_groupId
                       )
        UNION
        SELECT  s.userId, s.origGroupId,
                DECODE (u.id, NULL,
                        INTTORAW (s.userId, 4) || INTTORAW (l_newGroupId, 4),
                        INTTORAW (l_newGroupId, 4)) AS idPath
        FROM    ibs_User u, ibs_Group g2,
                (
                    SELECT  groupId, userId, origGroupId
                    FROM    ibs_GroupUser
                    WHERE   origGroupId = groupId
                        AND groupId = l_groupId
                ) s
       WHERE    s.userId = u.id(+)
            AND s.userId = g2.id(+);
    l_cursorRow         GroupCopy_Cursor%ROWTYPE;

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- get the id of the group:
    BEGIN
        SELECT  id
        INTO    l_groupId
        FROM    ibs_Group  
        WHERE   oid = ai_oid;

        -- at this point we know that the group exists.

        -- make an insert for all type specific tables:
        BEGIN
            INSERT INTO ibs_Group (oid, state, name, domainId)
                SELECT  ai_newOid, c_ST_ACTIVE, name, domainId
                FROM    ibs_Group
                WHERE   oid = ai_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'insert new group data';
                RAISE;                  -- call common exception handler
        END;

        -- get the new id of the group:
        BEGIN
            SELECT  id
            INTO    l_newGroupId
            FROM    ibs_Group
            WHERE   oid = ai_newOid;
            
            -- at this point we know that the new group exists.

            -- get all users and groups in the old group:
            -- loop through the cursor rows:
            FOR l_cursorRow IN GroupCopy_Cursor -- another tuple found
            LOOP
                -- get the actual tuple values:
                l_userId := l_cursorRow.userId;
                l_origGroupId := l_cursorRow.origGroupId;
                l_idPath := l_cursorRow.idPath;

                -- check if user was originally in the copied group:
                IF (l_origGroupId = l_groupId) -- user was in copied group?
                THEN
                    -- use the new id of the group instead:
                    l_origGroupId := l_newGroupId;
                END IF; -- if user was in copied group
                
                -- insert all users of old group into the new group:
                BEGIN
                    INSERT INTO ibs_GroupUser
                            (state, groupId, userId, roleId,
                            origGroupId, idPath)
                    VALUES  (c_ST_ACTIVE, l_newGroupId, l_userId, 0,
                            l_origGroupId, l_idPath);

                    l_rowCount := SQL%ROWCOUNT;

                    -- check if insert was performed correctly:
                    IF (l_rowCount <= 0) -- no row affected?
                    THEN
                        -- set corresponding return value:
                        l_retValue := c_NOT_OK;
                    END IF; -- if no row affected

                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'insert records';
                        RAISE;          -- call common exception handler
                END;

                -- check for an error and exit the loop if there was any:
                EXIT WHEN (l_retValue <> c_ALL_RIGHT);
            END LOOP; -- while another user found

            -- actualize all cumulated rights:
            p_Rights$updateRightsCum ();

        EXCEPTION
            WHEN NO_DATA_FOUND THEN         -- the new group does not exist?
                l_retValue := c_OBJECTNOTFOUND;
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get new group id';
                RAISE;                  -- call common exception handler
        END;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the group does not exist?
            l_retValue := c_OBJECTNOTFOUND;
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get group id';
            RAISE;                      -- call common exception handler
    END;

    -- finish the transaction:
    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
    END IF; -- else an error occurred

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_newOid = ' || ai_newOid ||
            ', l_groupId = ' || l_groupId ||
            ', l_newGroupId = ' || l_newGroupId ||
            ', l_origGroupId = ' || l_origGroupId ||
            ', l_userId = ' || l_userId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Group_01$BOCopy', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Group_01$BOCopy;
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
CREATE OR REPLACE FUNCTION p_Group_01$changeState
(
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_state                INTEGER 
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_RIGHT_UPDATE          CONSTANT INTEGER := 8;
    c_RIGHT_INSERT          CONSTANT INTEGER := 1;
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- active state
    c_ST_CREATED            CONSTANT INTEGER := 4; -- created state

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_rights                INTEGER := 0;
    l_containerId           RAW (8) := c_NOOID;
    l_oldState              INTEGER := 0;

BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    -- get the actual container id and state of object:
    BEGIN
        SELECT  containerId, state
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

        -- check if the user has the necessary rights
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN
            -- check if the state transition from the actual state to the new
            -- state is allowed:
            -- not implemented yet

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
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                        'p_Group_01$changeState.updateObject',
                        'OTHER error for object ' || l_oid);
                    RAISE;
            END;

            BEGIN
                -- update the state of the group tuple:
                UPDATE  ibs_Group
                SET     state = ai_state
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                        'p_Group_01$changeState.updateGroup',
                        'OTHER error for object ' || l_oid);
                    RAISE;
            END;

            COMMIT WORK;
        ELSE                            -- the user does not have the rights
            -- set the return value with the error code:
            l_retValue :=  c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the object does not exist?
            l_retValue :=  c_OBJECTNOTFOUND;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_Group_01$changeState.select',
                'OTHER error for object ' || l_oid);
            RAISE;
    END;

    -- return the state value:
    RETURN l_retValue;
    
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Group_01$changeState',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op ||
            ', ai_state = ' || ai_state ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
    -- return error code:
    RETURN c_NOT_OK;
END p_Group_01$changeState;
/

show errors;

EXIT;
