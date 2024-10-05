/******************************************************************************
 * All stored procedures regarding the group table. <BR>
 *
 * @version     2.21.0021, 04.07.2002 KR
 *
 * @author      Centner Martin (CM)  980702
 ******************************************************************************
 */


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$create
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_tVersionId          TVERSIONID,
    @ai_name                NAME,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_containerKind       INT,
    @ai_isLink              BOOL,
    @ai_linkedObjectId_s    OBJECTIDSTRING,
    @ai_description         DESCRIPTION,
    -- output parameters:
    @ao_oid_s               OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_NOOID                OBJECTID,       -- default value for no defined oid

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_containerId          OBJECTID,
    @l_groupsOid            OBJECTID,
    @l_groupsOid_s          OBJECTIDSTRING,
    @l_contIsGroup          BOOL,
    @l_contOid              OBJECTID,
    @l_contOid_s            OBJECTIDSTRING,
    @l_refOid               OBJECTID,
    @l_refOid_s             OBJECTIDSTRING,
    @l_oid                  OBJECTID,
    @l_domainId             DOMAINID,
    @l_state                STATE,
    @l_rights               RIGHTS,
    @l_name                 NAME,
    @l_newGroupId           GROUPID,
    @l_groupId              GROUPID

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_ALREADY_EXISTS       = 21,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0,
    @l_oid = @c_NOOID,
    @l_name = @ai_name,
    @l_contIsGroup = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT

    -- get the domain data:
    SELECT  @l_groupsOid = d.groupsOid, @l_domainId = d.id
    FROM    ibs_User u, ibs_Domain_01 d
    WHERE   u.id = @ai_userId
        AND d.id = u.domainId

    -- convert oid to string:
    EXEC p_byteToString @l_groupsOid, @l_groupsOid_s OUTPUT

    -- check if container is a group:
    IF EXISTS (
         SELECT  * 
         FROM    ibs_Group
         WHERE   oid = @l_containerId) -- container is a group?
    BEGIN
        -- set the flag:
        SELECT  @l_contIsGroup = 1

        -- the group shall be created in the major groups container:
        SELECT  @l_contOid = @l_groupsOid
        SELECT  @l_contOid_s = @l_groupsOid_s
    END -- if container is a group
    ELSE                                -- container is no group
    BEGIN
        -- set the flag:
        SELECT  @l_contIsGroup = 0

        -- the group shall be created in the container:
        SELECT  @l_contOid = @l_containerId
        SELECT  @l_contOid_s = @ai_containerId_s
    END -- else container is no group

    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate
            @ai_userId, @ai_op, @ai_tVersionId, @ai_name, @l_contOid_s,
            @ai_containerKind, @ai_isLink, @ai_linkedObjectId_s,
            @ai_description, 
            @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- object created successfully?
        BEGIN
            -- get the state and name from ibs_Object:
            SELECT  @l_state = state, @l_name = name
            FROM    ibs_Object
            WHERE   oid = @l_oid

            -- try to set data of the group:
            UPDATE  ibs_Group
            SET     name = @l_name,
                    state = @l_state,
                    domainId = @l_domainId
            WHERE   oid = @l_oid

            IF (@@ROWCOUNT <= 0)        -- group not found?
            BEGIN
                -- create new tuple for group:
                INSERT INTO ibs_Group
                        (oid, name, state, domainId)
                VALUES  (@l_oid, @l_name, @l_state, @l_domainId)
            END -- if group not found

            -- get the id:
            SELECT  @l_newGroupId = id
            FROM    ibs_Group
            WHERE   oid = @l_oid

            -- check if container is a group:
            IF (@l_contIsGroup = 1)     -- container is a group?

            BEGIN
                -- get the id of the container group:
                SELECT  @l_groupId = id 
                FROM    ibs_Group
                WHERE   oid = @l_containerId

                -- add group:
                EXEC @l_retValue = p_Group_01$addGroupId @l_groupId, @l_newGroupId

/* already done in p_Group_01$addGroupId
                -- actualize all cumulated rights:
                EXEC    p_Rights$updateRightsCum
*/
            END -- if container is a group
/* KR not necessary because the group is created physically in the container
            ELSE                        -- container is no group
            BEGIN
                -- create a reference to the group within the container:
                EXEC @l_retValue = p_Object$performCreate
                    @ai_userId, @ai_op, 0x01010031, @ai_name, @ai_containerId_s,
                    1, 1, @ao_oid_s, @ai_description, 
                    @l_refOid_s OUTPUT, @l_refOid OUTPUT
            END -- else container is no group
*/

            -- set rights of group on its own data:
            -- (this is necessary to allow the group to be shown in some
            -- dialogs)
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read', N'viewElems')
            EXEC p_Rights$addRights @l_oid, @l_newGroupId, @l_rights, 1
        END -- if object created successfully

    COMMIT TRANSACTION                  -- make changes permanent

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Group_01$create


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$change
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING, -- 
    @ai_userId              USERID,         -- 
    @ai_op                  INT,            -- 
    @ai_name                NAME,           -- 
    @ai_validUntil          DATETIME,       -- 
    @ai_description         DESCRIPTION,    -- 
    @ai_showInNews          BOOL,           -- 
    @ai_state               INT             -- 
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NAME_ALREADY_EXISTS  INT,            -- name of group exists

    -- local variables:
    @l_retValue             INT,            -- return value of this procedure
    @l_domainId             DOMAINID,       -- the id of the domain
    @l_oid                  OBJECTID,       -- the converted oid_s
    @l_given                INT             -- counter

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_ALL_RIGHT            = 1,
    @c_NAME_ALREADY_EXISTS  = 51

    -- initialize local variables and return values:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_domainId = @c_NOOID,
    @l_oid = @c_NOOID,
    @l_given = 0

-- body:
    BEGIN TRANSACTION
        EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT
            
        -- compute domain id:
        -- (divide user id by 0x01000000, i.e. get the first byte)
        SELECT @l_domainId = @ai_userId / 0x01000000

        -- is the name already given in this domain?
        SELECT  @l_given = COUNT (*) 
        FROM    ibs_Group g JOIN ibs_Object o ON g.oid = o.oid
        WHERE   o.name = @ai_name
            AND g.domainId = @l_domainId        
            AND o.state = 2
            AND o.oid <> @l_oid

        
        IF (@l_given > 0)
        BEGIN
            SELECT @l_retValue = @c_NAME_ALREADY_EXISTS
        END -- if
        ELSE  -- name not given
        BEGIN    
            -- perform the change of the object:
            EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId,
                    @ai_op, @ai_name,
                    @ai_validUntil, @ai_description, @ai_showInNews,
                    @l_oid OUTPUT

            IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed?
            BEGIN
                -- update the other values, get the state from the object:
                UPDATE  ibs_Group
                SET     name = @ai_name,
                        state = o.state
                FROM    ibs_Group g, ibs_Object o
                WHERE   g.oid = @l_oid
                    AND g.oid = o.oid
            END -- if operation properly performed
        END -- else
    COMMIT TRANSACTION

    -- return the state value:
    RETURN @l_retValue
GO
-- p_Group_01$change


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
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$retrieve
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
    @state          STATE           OUTPUT,
    @tVersionId     TVERSIONID      OUTPUT,
    @typeName       NAME            OUTPUT,
    @name           NAME            OUTPUT,
    @containerId    OBJECTID        OUTPUT,
    @containerName  NAME            OUTPUT,
    @containerKind  INT             OUTPUT,    
    @isLink         BOOL            OUTPUT,
    @linkedObjectId OBJECTID        OUTPUT,
    @owner          USERID          OUTPUT,
    @ownerName      NAME            OUTPUT,
    @creationDate   DATETIME        OUTPUT,
    @creator        USERID          OUTPUT,
    @creatorName    NAME            OUTPUT,
    @lastChanged    DATETIME        OUTPUT,
    @changer        USERID          OUTPUT,
    @changerName    NAME            OUTPUT,
    @validUntil     DATETIME        OUTPUT,
    @description    DESCRIPTION     OUTPUT,
    @showInNews     BOOL            OUTPUT,
    @checkedOut     BOOL            OUTPUT,
    @checkOutDate   DATETIME        OUTPUT,
    @checkOutUser   USERID          OUTPUT,
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
    -- initialize local variables:

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @retValue = p_Object$performRetrieve
                @oid_s, @userId, @op,
                @state OUTPUT, @tVersionId OUTPUT, @typeName OUTPUT, 
                @name OUTPUT, @containerId OUTPUT, @containerName OUTPUT, 
                @containerKind OUTPUT, @isLink OUTPUT, @linkedObjectId OUTPUT, 
                @owner OUTPUT, @ownerName OUTPUT, 
                @creationDate OUTPUT, @creator OUTPUT, @creatorName OUTPUT,
                @lastChanged OUTPUT, @changer OUTPUT, @changerName OUTPUT,
                @validUntil OUTPUT, @description OUTPUT, @showInNews OUTPUT, 
                @checkedOut OUTPUT, @checkOutDate OUTPUT, 
                @checkOutUser OUTPUT, @checkOutUserOid OUTPUT,
                @checkOutUserName OUTPUT, 
                @oid OUTPUT

/*
        IF (@retValue = @ALL_RIGHT)
        BEGIN

        END
*/
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Group_01$retrieve


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$delete'
GO


-- create the new procedure:
CREATE PROCEDURE p_Group_01$delete
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
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
                                            -- delete an object
    @c_ST_DELETED           INT,            -- state to indicate deletion of
                                            -- object

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_id                   GROUPID,        -- the id of the group
    @l_oid                  OBJECTID,       -- the oid of the object to be
                                            -- deleted
    @l_rights               RIGHTS          -- actual rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_ST_DELETED           = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rights = 0,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

    -- get the group data:
    SELECT  @l_id = id 
    FROM    ibs_Group
    WHERE   oid = @l_oid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get group data', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call exception handler

    -- check if the group is a system group:
    IF EXISTS
        (SELECT id
        FROM    ibs_Domain_01
        WHERE   adminGroupId = @l_id
            OR  allGroupId = @l_id
            OR  userAdminGroupId = @l_id
            OR  structAdminGroupId = @l_id)
                                        -- the group is a system group?
    BEGIN
        -- set corresponding return value:
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- if the group is a system group
    ELSE                                -- the group is no system group
    BEGIN
        -- group may be deleted
        BEGIN TRANSACTION -- begin new TRANSACTION

            -- delete base object and references:
            EXEC @l_retValue =
                p_Object$performDelete @ai_oid_s, @ai_userId, @ai_op

            -- check if there was an error:
            IF (@l_retValue = @c_ALL_RIGHT) -- operation properly performed?
            BEGIN
                -- delete object type specific data:
                -- (delete all type specific tuples which are not within
                -- ibs_Object)

                -- delete all rights for the deleted group:
                EXEC p_Rights$deleteAllUserRights @l_id

                -- actualize all cumulated rights:
                EXEC    p_Rights$updateRightsCumGroup @l_id

                -- delete all the entries in ibs_GroupUser:
                DELETE  ibs_GroupUser
                WHERE   (   userid = @l_id
                        OR  groupid = @l_id
                        OR  origGroupId = @l_id)
                    OR (CHARINDEX (CONVERT (BINARY(4), @l_id), idPath) > 0)

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'delete group/user data', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call exception handler

                -- set object as deleted:
                UPDATE  ibs_Group
                SET     state = @c_ST_DELETED
                WHERE   id = @l_id

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'update group state', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call exception handler
            END -- if operation properly performed

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
            COMMIT TRANSACTION          -- make changes permanent
        ELSE                            -- an error occured
            ROLLBACK TRANSACTION        -- undo changes
    END -- else the group is no system group

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Group_01$delete', @l_error, @l_ePos,
            N'ai_userId', @ai_userId,
            N'ai_oid_s', @ai_oid_s,
            N'ai_op', @ai_op
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Group_01$delete


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
EXEC p_dropProc N'p_Group_01$addUserSetRNoCum'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$addUserSetRNoCum
(
    -- input parameters:
    @ai_groupId             GROUPID,
    @ai_userId              USERID,
    @ai_userOid             OBJECTID,
    @ai_rights              RIGHTS = 0
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_rights               RIGHTS,         -- the current rights
    @l_superGroupId         GROUPID,        -- id of actual super group
    @l_idPath               POSNOPATH       -- posNoPath of the group

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rights = @ai_rights

-- body:
    -- insert user into group:
    INSERT INTO ibs_GroupUser
            (state, groupId, userId, roleId, origGroupId, idPath)
    VALUES  (2, @ai_groupId, @ai_userId, 0, @ai_groupId, @ai_groupId)

    -- set the rights of the group on the user:
    IF (@l_rights = -1)             -- set default rights?
        -- get the rights to be set:
        SELECT  @l_rights = SUM (id)
        FROM    ibs_Operation
        WHERE   name IN (N'view')
    -- set the rights:
    IF (@l_rights <> 0)             -- there shall be some rights set?
    BEGIN
        EXEC p_Rights$addRights @ai_userOid, @ai_groupId, @l_rights, 1
    END -- if there shall be some rights set

    -- store the relationships with all groups which are above the actual 
    -- one:
    -- define cursor:
    DECLARE groupUserCursor CURSOR FOR 
        SELECT  groupId, idPath
        FROM    ibs_GroupUser 
        WHERE   CONVERT (int, userId) = CONVERT (int, @ai_groupId)

    -- open the cursor:
    OPEN    groupUserCursor

    -- get the first user:
    FETCH NEXT FROM groupUserCursor INTO @l_superGroupId, @l_idPath

    -- loop through all found users:
    WHILE (@@FETCH_STATUS <> -1)            -- another user found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
            -- insert user into all groups where this group is part of:
            INSERT INTO ibs_GroupUser
                    (state, groupId, userId, roleId, origGroupId,
                    idPath)
            VALUES  (2, @l_superGroupId, @ai_userId, 0, @ai_groupId,
                    @l_idPath)
        END -- if
        -- get next user:
        FETCH NEXT FROM groupUserCursor INTO @l_superGroupId, @l_idPath
    END -- while another user found

    DEALLOCATE groupUserCursor

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Group_01$addUserSetRNoCum


/******************************************************************************
 * Add a new user to a group and set the rights of the group on the user. <BR>
 * If there are already rights set the new rights are added to the existing
 * rights. <BR>
 * The rights for the user are newly cumulated at the end of this procedure.
 *
 * @input parameters:
 * @param   ai_userId           Id of the user who is adding the user.
 * @param   ai_groupOid         Oid of the group where the user shall be added.
 * @param   ai_userOid          Oid of the user to be added.
 * @param   ai_roleOid          Oid of the role to be added. 
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
EXEC p_dropProc N'p_Group_01$addUserSetRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$addUserSetRights
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_groupOid            OBJECTID,
    @ai_userOid             OBJECTID,
    @ai_roleOid             OBJECTID = 0,
    @ai_rights              RIGHTS = 0
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_groupId              GROUPID,        -- id of group to add the user
    @l_uUserId              USERID,         -- id of user to be added
    @l_roleId               ROLEID,         -- id of the role of the user
                                            -- within the group
    @l_rights               RIGHTS,         -- the current rights
    @l_superGroupId         GROUPID,        -- id of actual super group
    @l_idPath               POSNOPATH       -- posNoPath of the group

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rights = @ai_rights

-- body:
    -- get the id of the group where the user shall be added:
    SELECT  @l_groupId = id
    FROM    ibs_Group
    WHERE   oid = @ai_groupOid

    -- get the id of the user to be added:
    SELECT  @l_uUserId = id
    FROM    ibs_User u
    WHERE   oid = @ai_userOid

    BEGIN TRANSACTION
        -- insert user into group:
        EXEC @l_retValue = p_Group_01$addUserSetRNoCum
            @l_groupId, @l_uUserId, @ai_userOid, @ai_rights

        -- check if there was a problem:
        IF (@l_retValue = @c_ALL_RIGHT) -- no problem?
            -- actualize all cumulated rights:
            EXEC    p_Rights$updateRightsCumUser @l_uUserId
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Group_01$addUserSetRights


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
EXEC p_dropProc N'p_Group_01$addUser'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$addUser
(
    -- input parameters:
    @userId                 USERID,
    @groupOid               OBJECTID,
    @userOid                OBJECTID,
    @roleOid                OBJECTID = 0
)
AS
    -- declarations:
DECLARE
    -- constants:
    @ALL_RIGHT              INT,
    -- local variables:
    @retValue               INT,
    @rights                 RIGHTS

    -- set constants:
    SELECT  @ALL_RIGHT = 1

    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT

    -- get the rights of the group on the user:
    SELECT  @rights = SUM (id)
    FROM    ibs_Operation
    WHERE   name IN (N'view')

    EXEC @retValue = p_Group_01$addUserSetRights @userId, @groupOid, @userOid,
            @roleOid, @rights

    -- return the state value
    RETURN  @retValue
GO
-- p_Group_01$addUser


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$addGroupId'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$addGroupId
(
    -- input parameters:
    @ai_majorGroupId        GROUPID,
    @ai_minorGroupId        GROUPID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT             -- counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT,
    @l_rowCount = 0

-- body:
    -- check if the sub group is not already in the super group:
    SELECT  @l_rowCount = COUNT (*)
    FROM    ibs_GroupUser
    WHERE   groupId = @ai_majorGroupId
        AND userId = @ai_minorGroupId
        AND groupId = origGroupId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'check if group relationship already exists', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    IF (@l_rowCount = 0)                -- group relationship does not exist?
    BEGIN
        -- insert the sub group into the super group and generate all inherited
        -- tuples:
        INSERT INTO ibs_GroupUser
                (id, state,
                groupId, userId, roleId, origGroupId, idPath)
        SELECT  -(u.id & 0x0000FFFF + 0x10000 * (g.id & 0x0000FFFF)), 2,
                g.groupId, u.userId, 0, u.origGroupId, u.idPath + g.idPath
        FROM    (
                    SELECT  id, userId, origGroupId, idPath
                    FROM    ibs_GroupUser
                    WHERE   groupId = @ai_minorGroupId
                    UNION
                    SELECT  0, @ai_minorGroupId AS userId,
                            @ai_majorGroupId AS origGroupId, 
                            CONVERT (VARBINARY (4), @ai_minorGroupId) AS idPath
                ) u
                CROSS JOIN
                (
                    SELECT  id, groupId, idPath
                    FROM    ibs_GroupUser
                    WHERE   SUBSTRING (idPath, 1, 4) =
                            CONVERT (BINARY (4), @ai_majorGroupId)
                    UNION
                    SELECT  0, @ai_majorGroupId AS groupId,
                            CONVERT (VARBINARY (4), @ai_majorGroupId) AS idPath                    
                ) g

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'insert', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- actualize all cumulated rights:
        EXEC    p_Rights$updateRightsCumGroup @ai_minorGroupId
    END -- if group relationship does not exist

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Group_01$addGroupId', @l_error, @l_ePos,
            N'ai_majorGroupId', @ai_majorGroupId,
            N'', N'',
            N'ai_minorGroupId', @ai_minorGroupId,
            N'', N'',
            N'l_rowCount', @l_rowCount
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Group_01$addGroupId


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$addGroup'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$addGroup
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_majorGroupOid       OBJECTID,
    @ai_minorGroupOid       OBJECTID,
    @ai_roleOid             OBJECTID = 0
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- counter
    @l_majorGroupId         GROUPID,        -- id of major group
    @l_minorGroupId         GROUPID         -- id of minor group

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- get id of major group:
    SELECT  @l_majorGroupId = id
    FROM    ibs_Group 
    WHERE   oid = @ai_majorGroupOid
    
    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get major group id', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO nonTransactionException    -- call exception handler

    -- get id of minor group:
    SELECT  @l_minorGroupId = id
    FROM    ibs_Group u
    WHERE   oid = @ai_minorGroupOid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get minor group id', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO nonTransactionException    -- call exception handler

    BEGIN TRANSACTION                   -- start transaction

        -- add the minor group to the major group:
        EXEC @l_retValue =
            p_Group_01$addGroupId @l_majorGroupId, @l_minorGroupId

    -- finish the transaction:
    -- check if there occurred an error:
    IF (@l_retValue = @c_ALL_RIGHT)     -- everything all right?
        COMMIT TRANSACTION              -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK TRANSACTION            -- undo changes

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
nonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Group_01$addGroup', @l_error, @l_ePos,
            N'l_majorGroupId', @l_majorGroupId,
            N'', N'',
            N'l_minorGroupId', @l_minorGroupId,
            N'', N'',
            N'ai_userId', @ai_userId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Group_01$addGroup


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$delUserNoCum'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$delUserNoCum
(
    -- input parameters:
    @ai_groupId             GROUPID,
    @ai_userId              USERID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_rights               RIGHTS          -- the current rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rights = 0

-- body:
    -- check if the group is a system group:
    IF EXISTS
        (SELECT id
        FROM    ibs_Domain_01
        WHERE   allGroupId = @ai_groupId)
                                        -- the group is a system group?
    BEGIN
        -- set corresponding return value:
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- if the group is a system group
    ELSE                                -- the group is no system group
    BEGIN
        -- user may be deleted
        -- delete user from all groups where the origGroupId is the GroupId:
        DELETE  ibs_GroupUser
        WHERE   origGroupId = @ai_groupId
            AND userId = @ai_userId
    END -- else the group is no system group

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Group_01$delUserNoCum


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
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$delUser'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$delUser
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_groupOid_s          OBJECTIDSTRING,
    @ai_userOid_s           OBJECTIDSTRING,
    @ai_roleOid_s           OBJECTIDSTRING
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_groupId              GROUPID,        -- id of group to add the user
    @l_uUserId              USERID,         -- id of user to be added
    @l_groupOid             OBJECTID,       -- oid of group
    @l_userOid              OBJECTID,       -- oid of user
    @l_rights               RIGHTS          -- the current rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rights = 0

-- body:
    -- convert string representations to oids:
    EXEC p_stringToByte @ai_groupOid_s, @l_groupOid OUTPUT
    EXEC p_stringToByte @ai_userOid_s, @l_userOid OUTPUT

    -- get the user id:
    SELECT  @l_uUserId = id
    FROM    ibs_User u
    WHERE   oid = @l_userOid

    -- get the group id:
    SELECT  @l_groupId = id
    FROM    ibs_Group 
    WHERE   oid = @l_groupOid

    -- check if the group is a system group:
    IF EXISTS
        (SELECT *
        FROM    ibs_Domain_01
        WHERE   allGroupId = @l_groupId)
                                        -- the group is a system group?
    BEGIN
        -- set corresponding return value:
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS,
                @l_rights = @ai_op - 1
    END -- if the group is a system group
    ELSE                                -- the group is no system group
    BEGIN
        -- user may be deleted
        -- get rights for this user:
        EXEC @l_rights = p_Rights$checkRights
            @l_userOid,                 -- given object to be accessed by user
            @l_groupOid,                -- container of the given object
            @ai_userId,                 -- user_id
            @ai_op,                     -- required rights user must have to 
                                        -- insert/update object
            @l_rights OUTPUT            -- returned value
    END -- else user may be deleted

    -- check if the user has the necessary rights
    IF (@l_rights = @ai_op)              -- the user has the rights?
    BEGIN
        BEGIN TRANSACTION
            -- delete user from the group:
            EXEC @l_retValue = p_Group_01$delUserNoCum @l_groupId, @l_uUserId

            -- check if everything was o.k.:
            IF (@l_retValue = @c_ALL_RIGHT) -- no error?
            BEGIN
                -- actualize all cumulated rights:
                EXEC    p_Rights$updateRightsCumUser @l_uUserId
            END -- if no error
        COMMIT TRANSACTION
    END
    ELSE
    BEGIN
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- else the user does not have the rights

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Group_01$delUser



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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$delGroupId'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$delGroupId
(
    -- input parameters:
    @ai_majorGroupId        GROUPID,
    @ai_minorGroupId        GROUPID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT             -- counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT,
    @l_rowCount = 0

-- body:
    -- check if the sub group is within the super group:
    SELECT  @l_rowCount = COUNT (*)
    FROM    ibs_GroupUser
    WHERE   groupId = @ai_majorGroupId
        AND userId = @ai_minorGroupId
        AND groupId = origGroupId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'check if group relationship exists', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    IF (@l_rowCount > 0)                -- group relationship exists?
    BEGIN
        -- delete the sub group from the super group and drop all inherited
        -- tuples:
        DELETE  ibs_GroupUser
        WHERE   (CHARINDEX (CONVERT (VARBINARY(4), @ai_minorGroupId) + 
                CONVERT (VARBINARY(4), @ai_majorGroupId ), idPath) > 0)

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'delete', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- actualize all cumulated rights:
        EXEC    p_Rights$updateRightsCumGroup @ai_minorGroupId
    END -- if group relationship exists

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Group_01$delGroupId', @l_error, @l_ePos,
            N'ai_majorGroupId', @ai_majorGroupId,
            N'', N'',
            N'ai_minorGroupId', @ai_minorGroupId,
            N'', N'',
            N'l_rowCount', @l_rowCount
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Group_01$delGroupId


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$delGroup'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$delGroup
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  OPERATIONID,
    @ai_majorGroupOid_s     OBJECTIDSTRING,
    @ai_minorGroupOid_s     OBJECTIDSTRING,
    @ai_roleOid_s           OBJECTIDSTRING
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- counter
    @l_majorGroupOid        OBJECTID,       -- oid of major group
    @l_minorGroupOid        OBJECTID,       -- oid of minor group
    @l_majorGroupId         GROUPID,        -- id of major group
    @l_minorGroupId         GROUPID,        -- id of minor group
    @l_rights               RIGHTS          -- the rights of the user on the
                                            -- current group

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- convert oid strings to oids:
    EXEC p_stringToByte @ai_majorGroupOid_s, @l_majorGroupOid OUTPUT
    EXEC p_stringToByte @ai_minorGroupOid_s, @l_minorGroupOid OUTPUT

    -- get id of major group:
    SELECT  @l_majorGroupId = id
    FROM    ibs_Group 
    WHERE   oid = @l_majorGroupOid
    
    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get major group id', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO nonTransactionException    -- call exception handler

    -- get id of minor group:
    SELECT  @l_minorGroupId = id
    FROM    ibs_Group u
    WHERE   oid = @l_minorGroupOid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get minor group id', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO nonTransactionException    -- call exception handler

    -- at this point we know that both the group and the sub group exist.
    -- get rights for the current user:
    EXEC @l_rights = p_Rights$checkRights
        @l_minorGroupOid,               -- given object to be accessed by user
        @l_majorGroupOid,               -- container of the given object
        @ai_userId,                     -- user_id
        @ai_op,                         -- required rights user must have to 
                                        -- delete object
        @l_rights OUTPUT                -- returned value

    -- check if the user has the necessary rights
    IF (@l_rights = @ai_op)             -- the user has the rights?
    BEGIN
        BEGIN TRANSACTION               -- start transaction
            -- delete the minor group from the major group:
            EXEC @l_retValue =
                p_Group_01$delGroupId @l_majorGroupId, @l_minorGroupId

        -- finish the transaction:
        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
            COMMIT TRANSACTION          -- make changes permanent
        ELSE                            -- an error occured
            ROLLBACK TRANSACTION        -- undo changes
    END -- if the user has the rights
    ELSE                                -- the user does not have the rights
    BEGIN
        -- set corresponding return value:
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- else the user does not have the rights

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
nonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Group_01$delGroup', @l_error, @l_ePos,
            N'l_majorGroupId', @l_majorGroupId,
            N'ai_majorGroupOid_s', ai_majorGroupOid_s,
            N'l_minorGroupId', @l_minorGroupId,
            N'ai_minorGroupOid_s', ai_minorGroupOid_s,
            N'ai_userId', @ai_userId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Group_01$delGroup


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
EXEC p_dropProc N'p_Group_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Group_01$BOCopy
(
    -- common input parameters:
    @ai_oid                 OBJECTID,
    @ai_userId              USERID,
    @ai_newOid              OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- tuple not found
    @c_ST_ACTIVE            INT,            -- active state of object

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_groupId              GROUPID,        -- id of copied group
    @l_newGroupId           GROUPID,        -- new id of the group
    @l_origGroupId          GROUPID,        -- id of original group in group
                                            -- hierarchy
    @l_idPath               POSNOPATH,      -- posNoPath of group/user
                                            -- relationship
    @l_userId               USERID          -- the actual user within the group

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3,
    @c_ST_ACTIVE            = 2

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT

-- body:
    BEGIN TRANSACTION -- begin new TRANSACTION

        -- get the id of the group:
        SELECT  @l_groupId = id
        FROM    ibs_Group
        WHERE   oid = @ai_oid

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'get group id', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- make an insert for all type specific tables:
        INSERT INTO ibs_Group (oid, state, name, domainId)
        SELECT  @ai_newOid, @c_ST_ACTIVE, name, domainId
        FROM    ibs_Group
        WHERE   oid = @ai_oid

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'insert new group data', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- get the new id of the group:
        SELECT  @l_newGroupId = id
        FROM    ibs_Group
        WHERE   oid = @ai_newOid

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'get new group id', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- define cursor:
        -- get all users and groups in the old group
        DECLARE GroupUser_Cursor CURSOR FOR 
            SELECT  userId, 
                    CASE 
                        WHEN (CONVERT (INT, origGroupId) = @l_groupId) 
                            THEN @l_newGroupId
                        ELSE origGroupId
                    END AS origGroupId,  
                    CONVERT (VARBINARY, idPath +
                                        CONVERT (VARBINARY (4), @l_newGroupId))
                        AS idPath
            FROM    ibs_GroupUser
            WHERE   CONVERT (INT, groupId) IN 
                    (
                        SELECT  CONVERT (INT, userId)
                        FROM    ibs_GroupUser
                        WHERE   groupId = @l_groupId
                            AND origGroupId = @l_groupId
                    )
            UNION
            SELECT  s.userId, s.origGroupId,
                    CASE
                        WHEN u.id IS NULL 
                            THEN CONVERT (VARBINARY (4), s.userId) +
                                 CONVERT (VARBINARY (4), @l_newGroupId)
                        ELSE CONVERT (VARBINARY (4), @l_newGroupId)
                        END AS idPath
            FROM    (
                        SELECT groupId, userId, origGroupId
                        FROM    ibs_GroupUser
                        WHERE   origGroupId = groupId
                            AND groupId = @l_groupId
                    ) s 
                    LEFT JOIN ibs_User u ON userId = u.id 
                    LEFT JOIN ibs_Group g2 ON
                        CONVERT (INT, userId) = CONVERT (INT, g2.id)


        -- open the cursor:
        OPEN    GroupUser_Cursor

        -- get the first user:
        FETCH NEXT FROM GroupUser_Cursor
            INTO @l_userId, @l_origGroupId, @l_idPath

        -- loop through all found tuples:
        WHILE (@@FETCH_STATUS <> -1 AND @l_retValue = @c_ALL_RIGHT)
                                                -- another user found?
        BEGIN
            -- Because @@FETCH_STATUS may have one of the three values
            -- -2, -1, or 0 all of these cases must be checked.
            -- In this case the tuple is skipped if it was deleted during
            -- the execution of this procedure.
            IF (@@FETCH_STATUS <> -2)
            BEGIN
                -- check if user was originally in the copied group:
                IF (@l_origGroupId = @l_groupId) -- user was in copied group?
                BEGIN
                    -- use the new id of the group instead:
                    SELECT @l_origGroupId = @l_newGroupId
                END -- if user was in copied group

                -- insert all users of old group into the new group:
                INSERT INTO ibs_GroupUser
                        (state, groupId, userId, roleId,
                        origGroupId, idPath)
                VALUES  (@c_ST_ACTIVE, @l_newGroupId, @l_userId, 0,
                        @l_origGroupId, @l_idPath)

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                    N'insert records', @l_ePos OUTPUT, @l_rowCount OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO cursorException -- call exception handler

                -- check if insert was performed correctly:
                IF (@l_rowCount <= 0)   -- no row affected?
                BEGIN
                    -- set corresponding return value:
                    SELECT  @l_retValue = @c_NOT_OK
                END -- if no row affected
            END -- if

            -- get next tuple:
            FETCH NEXT FROM GroupUser_Cursor
                INTO @l_userId, @l_origGroupId, @l_idPath
        END -- while another user found

        -- close the not longer needed cursor:
        CLOSE GroupUser_Cursor
        DEALLOCATE GroupUser_Cursor

        -- actualize all cumulated rights:
        EXEC    p_Rights$updateRightsCum

    -- finish the transaction:
    -- check if there occurred an error:
    IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
        COMMIT TRANSACTION          -- make changes permanent
    ELSE                            -- an error occured
        ROLLBACK TRANSACTION        -- undo changes

    -- return the state value:
    RETURN  @l_retValue

cursorException:                        -- an error occurred within cursor
    -- close the not longer needed cursor:
    CLOSE GroupUser_Cursor
    DEALLOCATE GroupUser_Cursor
exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Group_01$BOCopy', @l_error,
            @l_ePos,
            N'ai_userId', @ai_userId,
            N'', N'',
            N'l_groupId', @l_groupId,
            N'', N'',
            N'l_newGroupId', @l_newGroupId,
            N'', N'',
            N'l_origGroupId', @l_origGroupId,
            N'', N'',
            N'l_userId', @l_userId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Group_01$BOCopy


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
-- delete existing procedure:
EXEC p_dropProc N'p_Group_01$changeState'
GO


-- create the new procedure:
CREATE PROCEDURE p_Group_01$changeState
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @state          STATE
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_ST_ACTIVE            INT,            -- active state
    @c_ST_CREATED           INT,            -- created state

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255)  -- error position description

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOOID                = 0x0000000000000000,
    @c_ST_ACTIVE            = 2,
    @c_ST_CREATED           = 4

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @containerId OBJECTID,      -- id of container where the object
                                        -- resides
            @oldState STATE             -- actual state of the object
    SELECT  @containerId = 0x0000000000000000, @oldState = 0


    -- get the actual container id and state of object:
    SELECT  @containerId = containerId, @oldState = state
    FROM    ibs_Object
    WHERE   oid = @oid


    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC @rights = p_Rights$checkRights
            @oid,                       -- given object to be accessed by user
            @containerId,               -- container of given object
            @userId,                    -- user id
            @op,                        -- required rights user must have to
                                        -- update object
            @rights OUTPUT              -- returned value

        -- check if the user has the necessary rights
        IF (@rights = @op)              -- the user has the rights?
        BEGIN
            -- check if the state transition from the actual state to the new
            -- state is allowed:
            -- not implemented yet

            BEGIN TRANSACTION

                -- set the new state for the object and all tabs:
                UPDATE  ibs_Object
                SET     state = @state
                WHERE   oid = @oid
                    OR  (   containerId = @oid
                        AND containerKind = 2
                        AND state <> @state
                        AND (   state = @c_ST_ACTIVE
                            OR  state = @c_ST_CREATED
                            )
                        )

                -- update the state of the group tuple:
                UPDATE  ibs_Group
                SET     state = @state
                WHERE   oid = @oid

            COMMIT TRANSACTION
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- set the return value with the error code:
            SELECT  @retValue = @INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Group_01$changeState
