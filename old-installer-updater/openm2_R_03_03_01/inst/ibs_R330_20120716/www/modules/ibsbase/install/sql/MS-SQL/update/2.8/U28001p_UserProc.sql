/******************************************************************************
 * All stored procedures regarding the user table. <BR>
 *
 * @version     2.21.0024, 04.07.2002 KR
 *
 * @author      Klaus Reimüller (KR)  980528
 * @author      Heinz Josef Stampfer (HJ)  980521 ????
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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$performCreate'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$performCreate
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
    @ai_newUserId           USERID = 0x0,
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
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_containerId          OBJECTID,
    @l_usersOid             OBJECTID,
    @l_usersOid_s           OBJECTIDSTRING,
    @l_oid                  OBJECTID,
    @l_domainId             DOMAINID,
    @l_allGroupOid          OBJECTID,
    @l_state                STATE,
    @l_rights               RIGHTS,
    @l_name                 NAME,
    @l_newUserId            USERID,
    @l_localOp              INTEGER     -- operation for local operations

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_ALREADY_EXISTS       = 21,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_error                = 0,
    @l_rowCount             = 0,
    @l_oid                  = @c_NOOID,
    @l_newUserId            = @ai_newUserId,
    @l_name                 = @ai_name,
    @l_localOp              = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT

    -- get the domain data:
    SELECT  @l_usersOid = d.usersOid, @l_domainId = d.id
    FROM    ibs_User u, ibs_Domain_01 d
    WHERE   u.id = @ai_userId
        AND d.id = u.domainId
    -- convert oid to string:
    EXEC p_byteToString @l_usersOid, @l_usersOid_s OUTPUT

    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate
                @ai_userId, @ai_op, @ai_tVersionId, @l_name, @l_usersOid_s,
                @ai_containerKind, @ai_isLink, @ai_linkedObjectId_s,
                @ai_description, 
                @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- object created successfully?
        BEGIN
            -- get the state and name from ibs_Object:
            SELECT  @l_state = state, @l_name = name
            FROM    ibs_Object
            WHERE   oid = @l_oid

            -- try to set data of the user:
            UPDATE  ibs_User
            SET     name = @l_name,
                    oid = @l_oid,
                    state = @l_state,
                    fullname = @l_name,
                    domainId = @l_domainId
            WHERE   id = @l_newUserId

            IF (@@ROWCOUNT <= 0)        -- user not found?
            BEGIN

                -- create new tuple for user:
                INSERT INTO ibs_User
                        (name, oid, state, password, fullname, domainId)
                VALUES  (@l_name, @l_oid, @l_state, '', @l_name, @l_domainId)

                -- get the new id:
                SELECT  @l_newUserId = id
                FROM    ibs_User
                WHERE   oid = @l_oid

            END -- if user not found

            -- set rights of user on his/her own data:
            -- (this is necessary to allow the user to add his/her own person)
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN ('view', 'read', 'new', 'addElem')
            EXEC p_Rights$addRights @l_oid, @l_newUserId, @l_rights, 1
            -- create a new workspace:
            EXEC p_Workspace_01$create @ai_userId, @l_localOp, @l_newUserId

            -- check if container is a group
            IF EXISTS (
                 SELECT  * 
                 FROM    ibs_Group
                 WHERE   oid = @l_containerId) -- container is a group?
            BEGIN
                -- add user to group, roleId not inserted:
                EXEC p_Group_01$addUser
                    @ai_userId, @l_containerId, @l_oid, @c_NOOID
            END -- if

            -- put every created User in the Group Jeder
            -- get group of all users of domain:
            SELECT  @l_allGroupOid = g.oid
            FROM    ibs_Group g, ibs_Domain_01 d
            WHERE   d.id = @l_domainId
                AND g.id = d.allGroupId
            -- add user to group, roleId not inserted:
            EXEC p_Group_01$addUser @ai_userId, @l_allGroupOid, @l_oid, @c_NOOID

/*
ALREADY DONE IN p_Group_01$addUser
            -- update the cumulated rights:
            EXEC p_Rights$updateRightsCumUser @uUserId
*/
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_User_01$performCreate


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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT

    EXEC @retValue = p_User_01$performCreate @userId, @op, @tVersionId, @name, 
                @containerId_s, @containerKind, @isLink, @linkedObjectId_s, 
                @description, NULL, @oid_s OUTPUT

    -- return the state value:
    RETURN  @retValue
GO
-- p_User_01$create


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
 * @param   @delLink            Should linked Person be deleted ? (0 = no, else yes)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$change
(
    -- input parameters:
    @oid_s             OBJECTIDSTRING,
    @userId            USERID,
    @op                INT,
    @name              NAME,
    @validUntil        DATETIME,
    @description       DESCRIPTION,
    @showInNews        BOOL,
    @fullname          NAME,
    @state             INT,
    @password          NAME,
	@changePwd		   BOOL
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT,
            @NAME_ALREADY_EXISTS INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3,
            @NAME_ALREADY_EXISTS = 51
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
    DECLARE @domainId ID
    DECLARE @given INT
    DECLARE @linkOid    OBJECTID
    DECLARE @linkOid_s  OBJECTIDSTRING

    -- initialize local variables:
    SELECT @linkOid   = 0x0000000000000000
    SELECT @linkOid_s = '0x0000000000000000'

    -- body:
    BEGIN TRANSACTION
        EXEC p_stringToByte @oid_s, @oid OUTPUT

        -- compute domain id:
        -- (divide user id by 0x01000000, i.e. get the first byte)
        SELECT @domainId = @userId / 0x01000000

        -- is the name already given in this domain?
        SELECT  @given = COUNT (*) 
        FROM    ibs_User u JOIN ibs_Object o ON u.oid = o.oid
        WHERE   o.name = @name
        AND     u.domainId = @domainId        
        AND     o.state = 2
        AND     o.oid <> @oid

        
        IF (@given > 0)
            SELECT @retValue = @NAME_ALREADY_EXISTS
        ELSE  -- name not given
        BEGIN
                -- perform the change of the object:
                EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                        @validUntil, @description, @showInNews, @oid OUTPUT

                IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
                BEGIN
                    -- update the other values, get the state from the object:
                    UPDATE  ibs_User
                    SET     name = @name,
                            fullname = @fullname,
                            state = o.state,
                            password = @password,
							changePwd = @changePwd
                    FROM    ibs_user u, ibs_Object o
                    WHERE   u.oid = @oid
                        AND u.oid = o.oid
                END -- if operation properly performed
        END -- if name already given
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_User_01$change



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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$createFast'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$createFast
(
    -- input parameters:
    @userId         USERID,
    @domainId       DOMAINID,
    @username       NAME,
    @password       NAME,
    @fullname       NAME,
    -- output parameters:
    @oid            OBJECTID = 0x0000000000000000 OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @containerId OBJECTID, @containerId_s OBJECTIDSTRING,
            @oid_s OBJECTIDSTRING,
            @groupId GROUPID, @groupOid OBJECTID, 
            @validUntil DATETIME
    -- initialize local variables:


    -- body:
    BEGIN TRANSACTION
        -- get user container:
        SELECT  @containerId = usersOid
        FROM    ibs_Domain_01
        WHERE   id = @domainId

        -- check if the domain was found:
        IF (@@ROWCOUNT > 0)             -- the domain was found?
        BEGIN
            -- convert container oid to string representation:
            EXEC p_byteToString @containerId, @containerId_s OUTPUT


            -- create the user:
            EXEC @retValue = p_User_01$performCreate @userId, 0x00000001, 0x010100A1, 
                    @username, @containerId_s, 1,
                    0, '0x0000000000000000', '', NULL,
                    @oid_s OUTPUT

            -- convert user oid string to oid representation:
            EXEC p_stringToByte @oid_s, @oid OUTPUT

            -- check if there was an error during creation:
            IF (@retValue = @ALL_RIGHT) -- user created successfully?
            BEGIN
                -- set valid Time to one year
                SELECT  @validUntil = DATEADD (month, 12, getDate ())
                -- store user specific data:
                EXEC p_User_01$change @oid_s, @userId, 0x00000001, @username, 
                        @validUntil, '', 0, @fullname, 2, @password
            END -- if user created successfully
        END -- if the domain was found
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_User_01$createFast


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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$new
(
    -- input parameters:
    @domainId               DOMAINID,
    @userNo                 USERID = 0x00000000,
    @name                   NAME,
    @password               NAME,
    @fullname               NAME,
    @group                  GROUPID,
    @rights                 RIGHTS,
    -- output parameters:
    @id                     USERID = 0x00000000 OUTPUT
)
AS
    -- definitions:
DECLARE
    -- constants:
    @ALL_RIGHT              INT, 
    @ALREADY_EXISTS         INT,
    -- local variables:
    @retValue               INT,        -- return value of this procedure
    @msg                    VARCHAR (255), 
    @oid                    OBJECTID,
    @groupOid               OBJECTID
/*
    @groupId                GROUPID, 
    @uUserId                USERID,
    @@groupId               GROUPID, 
    @@idPath                POSNOPATH
*/
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @ALREADY_EXISTS = 21
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- initialize local variables:
    SELECT  @oid = 0x0000000000000000


    -- body:
    -- compute id:
    IF (@userNo <> 0)                   -- user number defined?
        SELECT  @id = @domainId * 0x01000000 + 0x00800000 + @userNo
    ELSE                                -- no user number defined
        SELECT  @id = 0x00000000

    -- check if an user with this id already exists:
    IF EXISTS (
        SELECT  id
        FROM    ibs_User
        WHERE   id = @id
        )                               -- a user with this id already exists?
    BEGIN
        SELECT  @retValue = @ALREADY_EXISTS
    END -- if a user with this id already exists
    ELSE                                -- user id not already there
    BEGIN
        BEGIN TRANSACTION
            -- add the new user:
            INSERT INTO ibs_User (id, oid, state, domainId, name, password, fullname)
            VALUES (@id, 0x0000000000000000, 2, @domainId, @name, @password, @fullname)

            IF (@@ROWCOUNT > 0)         -- user was inserted?
            BEGIN
                IF (@id = 0)            -- id must have been changed?
                    -- get the id of the newly inserted user:
                    SELECT  @id = MAX (id)
                    FROM    ibs_User
                    WHERE   name = @name
                        AND (id / 0x01000000) = @domainId

                -- get oid:
                SELECT  @oid = oid
                FROM    ibs_User
                WHERE   id = @id

                -- add user to a group:
                IF (@group <> null)         -- group set?
                BEGIN
                    -- get the oid of the group:
                    SELECT  @groupOid = oid
                    FROM    ibs_Group
                    WHERE   id = @group

                    -- add user to group:
                    EXEC p_Group_01$addUserSetRights 
                        @id, @groupOid, @oid, 0x0000000000000000, @rights
                END -- if group set

                -- cumulate rights for user:
                EXEC p_Rights$updateRightsCumUser @id
            END -- if user was inserted
        COMMIT TRANSACTION
    END -- else user id not already there
    -- return the state value
    RETURN  @retValue
GO
-- p_User_01$new



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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$retrieve
(
    -- input parameters:
    @oid_s                  OBJECTIDSTRING,
    @userId                 USERID,
    @op                     INT,
    -- output parameters
    @state                  STATE       OUTPUT,
    @tVersionId             TVERSIONID  OUTPUT,
    @typeName               NAME        OUTPUT,
    @name                   NAME        OUTPUT,
    @containerId            OBJECTID    OUTPUT,
    @containerName          NAME        OUTPUT,
    @containerKind          INT         OUTPUT,
    @isLink                 BOOL        OUTPUT,
    @linkedObjectId         OBJECTID    OUTPUT,
    @owner                  USERID      OUTPUT,
    @ownerName              NAME        OUTPUT, -- name of the Owner
    @creationDate           DATETIME    OUTPUT,
    @creator                USERID      OUTPUT,
    @creatorName            NAME        OUTPUT, -- name of the Changer
    @lastChanged            DATETIME    OUTPUT,
    @changer                USERID      OUTPUT,
    @changerName            NAME        OUTPUT, -- name of the Creator
    @validUntil             DATETIME    OUTPUT,
    @description            DESCRIPTION OUTPUT,
    @showInNews             BOOL        OUTPUT,
    @checkedOut             BOOL        OUTPUT,
    @checkOutDate           DATETIME    OUTPUT,
    @checkOutUser           USERID      OUTPUT,
    @checkOutUserOid        OBJECTID    OUTPUT,
    @checkOutUserName       NAME        OUTPUT,
    -- objectspezific attributes
    @fullname               NAME        OUTPUT, -- fullname of the User
    @password               NAME        OUTPUT, -- password of the User
    @workspaceId            OBJECTID    OUTPUT, -- workspave of the User
    @memberShipId           OBJECTID    OUTPUT, -- memberShips of the User
    @personOid              OBJECTID    OUTPUT,
    @changePwd				BOOL		OUTPUT
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
    DECLARE @oid OBJECTID, @id ID
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
                @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
                @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- get object type specific data:
            SELECT  @id = id,
                    @fullname = fullname, 
--                    @state = state,
                    @password = password,
					@changePwd = changePwd
            FROM    ibs_User
            WHERE   oid = @oid

            -- get workspaceId of the user:
            SELECT  @workspaceId = workspace
            FROM    ibs_Workspace
            WHERE   userId = @id

            -- get memberShipId of the user:
            SELECT  @memberShipId = o.oid
            FROM    ibs_Object o
            WHERE   o.containerId = @oid
                AND o.containerKind = 2
                AND o.tVersionId = 0x01015201  -- tVersionId of memberShip objects

           -- get personOid linked to the user:
            SELECT  @personOid = linkedObjectId, @fullname = name
            FROM    ibs_Object
            WHERE   containerId = @oid
                AND tVersionId = 0x01010031
                AND state = 2     -- check if state is ST_ACTIVE

        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_User_01$retrieve


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
 * @param   @ao_sslRequired     flag if SSL must be used for the domain or not
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$login'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$login
(
    -- input parameters:
    @domainId               DOMAINID,
    @username               NAME,
    @password               NAME,
    -- output parameters
    @oid                    OBJECTID OUTPUT,
    @id                     USERID OUTPUT,
    @fullname               NAME OUTPUT,
    @domainName             NAME = '' OUTPUT,
    @ao_sslRequired         BOOL = 0 OUTPUT,
	@changePwd         		BOOL = 0 OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT,
            @WRONG_PASSWORD INT, 
            @NOT_VALID INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3,
            @WRONG_PASSWORD = 11, 
            @NOT_VALID = 41
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @OBJECTNOTFOUND
    -- define local variables:
    DECLARE @rights RIGHTS              -- return value of called procedure
    DECLARE @realPassword NAME,
            @validUntil DATETIME
    -- initialize local variables:
    SELECT  @realPassword = 'unknownPassword',
            @oid = 0x0000000000000000, @id = 0x00000000, @fullname = ''

    -- body:
    -- get data of required user:
    SELECT  @id = u.id, @oid = u.oid, @realPassword = u.password, 
            @fullname = u.fullname, @validUntil = o.validUntil,
			@changePwd = u.changePwd
    FROM    ibs_User u, ibs_Object o
    WHERE   u.name = @username
        AND (   u.domainId = @domainId
            OR  u.domainId = 0
            )
        AND u.state = 2
        AND o.state = 2
        AND o.oid = u.oid

    -- check if the user exists:
    IF (@@ROWCOUNT > 0)                 -- user found?
    BEGIN
        -- check if user is valid:
        IF (@validUntil >= getDate ())  -- the user is valid?
        BEGIN
            -- check password:
            IF (@password = @realPassword) -- correct password?
            BEGIN
                -- get domain data:
                SELECT  @domainName = o.name,
                        @ao_sslRequired = d.sslRequired
                FROM    ibs_Object o, ibs_Domain_01 d
                WHERE   d.id = @domainId
                    AND o.oid = d.oid
                    AND o.state = 2

                IF (@@ROWCOUNT = 0)        -- no domain found, not using SSL 
                BEGIN
                    SELECT @ao_sslRequired = 0
                END

                SELECT  @retValue = @ALL_RIGHT
            END -- if correct password

            ELSE                        -- wrong password
            BEGIN
                SELECT  @id = 0x00000000, @oid = 0x0000000000000000, 
                        @fullname = '', @retValue = @WRONG_PASSWORD
            END -- else wrong password
        END -- if the user is valid
        ELSE                            -- the user is not longer valid
        BEGIN
            SELECT  @id = 0x00000000, @oid = 0x0000000000000000, 
                    @fullname = '', @retValue = @NOT_VALID
        END -- else the user is not longer valid
    END -- if user found

    ELSE                                -- user not found
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else user not found

    -- return the state value:
    RETURN  @retValue
GO
-- p_User_01$login


/******************************************************************************
 * Makes the logout of a online user. <BR>
 *
 * @input parameters:
 * @param   @id                 Id of the user.
 * @param   @oid                Object id of the user object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The required object was not found within the 
 */
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$logout'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$logout
(
    -- input parameters:
    @id                     USERID,
    @oid                    OBJECTID
    -- output parameters
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT

--
-- NOT IMPLEMENTED YET!
--
    
    -- return the state value:
    RETURN  -1
GO
-- p_User_01$logout


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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$changePassword'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$changePassword
(
    -- input parameters:
    @userId                 USERID,
    @oldPassword            NAME,
    @newPassword            NAME
    -- output parameters:
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT,
            @WRONG_PASSWORD INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3,
            @WRONG_PASSWORD = 11
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @OBJECTNOTFOUND
    -- define local variables:
    DECLARE @rights RIGHTS              -- return value of called procedure
    DECLARE @realPassword NAME
    -- initialize local variables:
    SELECT  @realPassword = 'unknownPassword'
    -- body:

    -- get data of required user:
    SELECT  @realPassword = password
    FROM    ibs_User
    WHERE   id = @userId

    -- check if the user exists:
    IF (@@ROWCOUNT > 0)                 -- user found?
    BEGIN
        -- check password:
        IF (@oldPassword = @realPassword)  -- correct password?
        BEGIN
            -- set the new password:
            UPDATE  ibs_User
            SET     password = @newPassword, changePwd = 0
            WHERE   id = @userId

            -- set return value:
            SELECT  @retValue = @ALL_RIGHT
        END -- if correct password

        ELSE                            -- wrong password
        BEGIN
            SELECT  @retValue = @WRONG_PASSWORD
        END -- else wrong password
    END -- if user found
    ELSE                                -- user not found
    BEGIN
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else user not found

    -- return the state value:
    RETURN  @retValue
GO
-- p_User_01$changePassword


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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$delete'
GO

CREATE PROCEDURE p_User_01$delete
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
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_id                   USERID,         -- the id of the user
    @l_oid                  OBJECTID,       -- the oid of the object to be
                                            -- deleted
    @l_rights               RIGHTS,         -- actual rights
    @l_workspaceOid         OBJECTID,       -- oid of the workspace
    @l_workspaceOid_s       OBJECTIDSTRING  -- oid of workspace as string

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

    -- get the user data:
    SELECT  @l_id = id
    FROM    ibs_User
    WHERE   oid = @l_oid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'get user data', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call exception handler

    -- check if the user is a system user:
    IF EXISTS
        (SELECT id
        FROM    ibs_Domain_01
        WHERE   adminId = @l_id)
                                        -- the user is a system user?
    BEGIN
        -- set corresponding return value:
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- if the user is a system user
    ELSE                                -- the user is no system user
    BEGIN
        -- user may be deleted

        -- get the workspace data:
        SELECT  @l_workspaceOid = workspace
        FROM    ibs_Workspace
        WHERE   userid = @l_id

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            'get workspace data', @l_ePos OUTPUT
        IF (@l_error <> 0)                  -- an error occurred?
            GOTO NonTransactionException    -- call exception handler

        EXEC p_byteToString @l_workspaceOid, @l_workspaceOid_s OUTPUT

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

                -- delete all rights for the deleted user:
                EXEC p_Rights$deleteAllUserRights @l_id

                -- delete all the entries in ibs_GroupUser:
                DELETE  ibs_GroupUser
                WHERE   userId = @l_id
                        
                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    'delete group/user data', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call exception handler

                -- set object as deleted:
                UPDATE  ibs_User
                SET     state = @c_ST_DELETED
                WHERE   id = @l_id

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    'update user state', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call exception handler

                -- delete the workspace object:
                EXEC @l_retValue =
                    p_Object$performDelete @l_workspaceOid_s, @ai_userId, @ai_op
            END -- if operation properly performed

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
            COMMIT TRANSACTION          -- make changes permanent
        ELSE                            -- an error occured
            ROLLBACK TRANSACTION        -- undo changes
    END -- else the user is no system user

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, 'p_User_01$delete', @l_error, @l_ePos,
            'ai_userId', @ai_userId,
            'ai_oid_s', @ai_oid_s,
            'ai_op', @ai_op
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_User_01$delete


/******************************************************************************
 * Copies an User_01 object and all its values (incl. rights check). <BR>
 */
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$BOCopy
(
    -- common input parameters:
    @ai_oid                 OBJECTID,
    @ai_userId              USERID,
    @ai_newOid              OBJECTID
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOT_OK               INT,            -- error occured
    @c_ST_ACTIVE            INT,            -- state value of active object

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_name                 NAME,           -- name of user
    @l_baseName             NAME,           -- base part of user name
    @l_count                INT,            -- counter
    @l_groupOid             OBJECTID,       -- oid of actual group
    @l_userId               USERID,         -- id of actual user
    @l_oldUserId            USERID          -- old user id

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOT_OK               = 0,
    @c_ST_ACTIVE            = 2

    -- initialize local variables:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_count                = 0

-- body:
    BEGIN TRANSACTION
        -- get id of user to be copied:
        SELECT  @l_oldUserId = id, @l_baseName = name + '#copy'
        FROM    ibs_User
        WHERE   oid = @ai_oid

        -- get unique user name:
        SELECT  @l_name = @l_baseName

        -- try the temp names until an unused name is found:
        WHILE EXISTS
        (   SELECT id
            FROM    ibs_User
            WHERE   name = @l_name
                AND state = @c_ST_ACTIVE
        )
        BEGIN
            -- compute new user name:
            SELECT  @l_count = @l_count + 1
            SELECT  @l_name = @l_baseName + CONVERT (VARCHAR, @l_count)
        END -- while


        -- ensure that the name in ibs_Object is correct:
        UPDATE  ibs_Object
        SET     name = @l_name
        WHERE   oid = @ai_newOid

        -- make an insert for all type specific tables:
        INSERT INTO ibs_User
                (oid, name, state, password, fullname, domainId, changePwd)
        SELECT  @ai_newOid, @l_name, @c_ST_ACTIVE, password, fullname, domainId, changePwd
        FROM    ibs_User
        WHERE   oid = @ai_oid

        -- get the id of the new user:
        SELECT  @l_userId = id
        FROM    ibs_User
        WHERE   oid = @ai_newOid

        -- create a new worksapace and a workspace container:
        EXEC p_Workspace_01$create @ai_userId, 4, @l_userId

        -- get all users and groups in the old group
        -- define cursor:
        DECLARE UserBOCopy_Cursor CURSOR FOR 
            SELECT  g.oid
            FROM    ibs_GroupUser gu, ibs_Group g
            WHERE   gu.userId = @l_oldUserId
                AND gu.origGroupId = gu.groupId
                AND gu.groupId = g.id

        -- open the cursor:
        OPEN    UserBOCopy_Cursor

        -- get the first user:
        FETCH NEXT FROM UserBOCopy_Cursor INTO @l_groupOid

        -- loop through all found tupels:
        WHILE (@@FETCH_STATUS <> -1)            -- another user found?
        BEGIN
            -- Because @@FETCH_STATUS may have one of the three values
            -- -2, -1, or 0 all of these cases must be checked.
            -- In this case the tuple is skipped if it was deleted during
            -- the execution of this procedure.
            IF (@@FETCH_STATUS <> -2)
            BEGIN
                -- add user to the current group:
                EXEC p_Group_01$addUser @ai_userId, @l_groupOid, @ai_newOid, 0
            END -- if
            -- get next tupel:
            FETCH NEXT FROM UserBOCopy_Cursor INTO @l_groupOid
        END -- while another user found

        CLOSE UserBOCopy_Cursor
        DEALLOCATE UserBOCopy_Cursor

        SELECT  @l_retValue = @c_ALL_RIGHT -- set return value

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_User_01$BOCopy


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
EXEC p_dropProc 'p_User_01$changeState'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$changeState
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
    @l_ePos                 VARCHAR (255)   -- error position description

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
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
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
        -- get rights for this user:
        EXEC @rights = p_Rights$checkRights
            @oid,                       -- given object to be accessed by user
            @containerId,               -- container of given object
            @userId,                    -- user id
            @op,                        -- required rights user must have to
                                        -- update object
            @rights OUTPUT              -- returned value

        -- check if the user has the necessary rights:
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

                -- update the state of the user tuple:
                UPDATE  ibs_User
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

    -- return the state value:
    RETURN  @retValue
GO
-- p_User_01$changeState


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
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$delUnneededGrNoCum'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$delUnneededGrNoCum
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_userOid             OBJECTID,
    @ai_groupOid01          OBJECTID,
    @ai_groupOid02          OBJECTID,
    @ai_groupOid03          OBJECTID,
    @ai_groupOid04          OBJECTID,
    @ai_groupOid05          OBJECTID,
    @ai_groupOid06          OBJECTID,
    @ai_groupOid07          OBJECTID,
    @ai_groupOid08          OBJECTID,
    @ai_groupOid09          OBJECTID,
    @ai_groupOid10          OBJECTID,
    @ai_groupOid11          OBJECTID,
    @ai_groupOid12          OBJECTID,
    @ai_groupOid13          OBJECTID,
    @ai_groupOid14          OBJECTID,
    @ai_groupOid15          OBJECTID
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
    @l_groupId              GROUPID         -- the actual group

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- define cursor which gets all groups where user is currently in but
    -- should not:
    DECLARE delGroupCursor CURSOR FOR 
        SELECT  groupId
        FROM    ibs_GroupUser
        WHERE   userId = @ai_userId
            AND groupId = origGroupId
            AND groupId NOT IN
                (
                    SELECT  id
                    FROM    ibs_Group
                    WHERE   oid IN
                            (
                                @ai_groupOid01, @ai_groupOid02, @ai_groupOid03,
                                @ai_groupOid04, @ai_groupOid05, @ai_groupOid06,
                                @ai_groupOid07, @ai_groupOid08, @ai_groupOid09,
                                @ai_groupOid10, @ai_groupOid11, @ai_groupOid12,
                                @ai_groupOid13, @ai_groupOid14, @ai_groupOid15
                            )
                )

    -- open the cursor:
    OPEN    delGroupCursor

    -- get the first object:
    FETCH NEXT FROM delGroupCursor INTO @l_groupId

    -- loop through all found objects:
    WHILE (@@FETCH_STATUS <> -1 AND @l_retValue = @c_ALL_RIGHT)
                                        -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
            -- delete the user from the group:
            EXEC @l_retValue = p_Group_01$delUserNoCum @l_groupId, @ai_userId
        END -- if
        -- get next object:
        FETCH NEXT FROM delGroupCursor INTO @l_groupId
    END -- while another object found

    -- close and deallocate cursor to allow another cursor with the same 
    -- name:
    CLOSE delGroupCursor
    DEALLOCATE delGroupCursor

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_User_01$delUnneededGrNoCum


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
 * If the user is already a member of the group, nothing is changed.
 * Otherwise he is added to the group.
 * A groupId of 0 means not to change any membership of the user.
 * The rights of the user are not recumulated.
 *
 * @input parameters:
 * @param   ai_userId           Id of the user, whose group memberships are set.
 * @param   ai_groupId          Id of the group where the user shall be a
 *                              member.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$addNeededGrNoCum'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$addNeededGrNoCum
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_userOid             OBJECTID,
    @ai_groupOid01          OBJECTID,
    @ai_groupOid02          OBJECTID,
    @ai_groupOid03          OBJECTID,
    @ai_groupOid04          OBJECTID,
    @ai_groupOid05          OBJECTID,
    @ai_groupOid06          OBJECTID,
    @ai_groupOid07          OBJECTID,
    @ai_groupOid08          OBJECTID,
    @ai_groupOid09          OBJECTID,
    @ai_groupOid10          OBJECTID,
    @ai_groupOid11          OBJECTID,
    @ai_groupOid12          OBJECTID,
    @ai_groupOid13          OBJECTID,
    @ai_groupOid14          OBJECTID,
    @ai_groupOid15          OBJECTID
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
    @l_groupId              GROUPID         -- the actual group

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- define cursor which gets all groups where user is currently in but
    -- should not:
    DECLARE addGroupCursor CURSOR FOR 
        SELECT  id
        FROM    ibs_Group
        WHERE   oid IN
                (
                    @ai_groupOid01, @ai_groupOid02, @ai_groupOid03,
                    @ai_groupOid04, @ai_groupOid05, @ai_groupOid06,
                    @ai_groupOid07, @ai_groupOid08, @ai_groupOid09,
                    @ai_groupOid10, @ai_groupOid11, @ai_groupOid12,
                    @ai_groupOid13, @ai_groupOid14, @ai_groupOid15
                )
            AND id NOT IN
                (
                    SELECT  groupId
                    FROM    ibs_GroupUser
                    WHERE   userId = @ai_userId
                        AND groupId = origGroupId
                )

    -- open the cursor:
    OPEN    addGroupCursor

    -- get the first object:
    FETCH NEXT FROM addGroupCursor INTO @l_groupId

    -- loop through all found objects:
    WHILE (@@FETCH_STATUS <> -1 AND @l_retValue = @c_ALL_RIGHT)
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
            -- add the user to the group:
            EXEC @l_retValue = p_Group_01$addUserSetRNoCum
                @l_groupId, @ai_userId, @ai_userOid, 0
        END -- if
        -- get next object:
        FETCH NEXT FROM addGroupCursor INTO @l_groupId
    END -- while another object found

    -- close and deallocate cursor to allow another cursor with the same 
    -- name:
    CLOSE addGroupCursor
    DEALLOCATE addGroupCursor

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_User_01$addNeededGrNoCum


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
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$setGroups'
GO


-- create the new procedure:
CREATE PROCEDURE p_User_01$setGroups
(
    -- input parameters:
    @ai_userOid_s           OBJECTIDSTRING,
    @ai_groupOid01_s        OBJECTIDSTRING,
    @ai_groupOid02_s        OBJECTIDSTRING,
    @ai_groupOid03_s        OBJECTIDSTRING,
    @ai_groupOid04_s        OBJECTIDSTRING,
    @ai_groupOid05_s        OBJECTIDSTRING,
    @ai_groupOid06_s        OBJECTIDSTRING,
    @ai_groupOid07_s        OBJECTIDSTRING,
    @ai_groupOid08_s        OBJECTIDSTRING,
    @ai_groupOid09_s        OBJECTIDSTRING,
    @ai_groupOid10_s        OBJECTIDSTRING,
    @ai_groupOid11_s        OBJECTIDSTRING,
    @ai_groupOid12_s        OBJECTIDSTRING,
    @ai_groupOid13_s        OBJECTIDSTRING,
    @ai_groupOid14_s        OBJECTIDSTRING,
    @ai_groupOid15_s        OBJECTIDSTRING
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
    @l_userId               USERID,         -- id of the user
    @l_userOid              OBJECTID,       -- oid of the user
    @l_groupOid01           OBJECTID,       -- oid of a group
    @l_groupOid02           OBJECTID,       -- oid of a group
    @l_groupOid03           OBJECTID,       -- oid of a group
    @l_groupOid04           OBJECTID,       -- oid of a group
    @l_groupOid05           OBJECTID,       -- oid of a group
    @l_groupOid06           OBJECTID,       -- oid of a group
    @l_groupOid07           OBJECTID,       -- oid of a group
    @l_groupOid08           OBJECTID,       -- oid of a group
    @l_groupOid09           OBJECTID,       -- oid of a group
    @l_groupOid10           OBJECTID,       -- oid of a group
    @l_groupOid11           OBJECTID,       -- oid of a group
    @l_groupOid12           OBJECTID,       -- oid of a group
    @l_groupOid13           OBJECTID,       -- oid of a group
    @l_groupOid14           OBJECTID,        -- oid of a group
    @l_groupOid15           OBJECTID        -- oid of a group                                            

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOT_ALL              = 31

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK    


-- body:
    EXEC p_stringToByte @ai_userOid_s, @l_userOid OUTPUT
    EXEC p_stringToByte @ai_groupOid01_s, @l_groupOid01 OUTPUT
    EXEC p_stringToByte @ai_groupOid02_s, @l_groupOid02 OUTPUT
    EXEC p_stringToByte @ai_groupOid03_s, @l_groupOid03 OUTPUT
    EXEC p_stringToByte @ai_groupOid04_s, @l_groupOid04 OUTPUT
    EXEC p_stringToByte @ai_groupOid05_s, @l_groupOid05 OUTPUT
    EXEC p_stringToByte @ai_groupOid06_s, @l_groupOid06 OUTPUT
    EXEC p_stringToByte @ai_groupOid07_s, @l_groupOid07 OUTPUT
    EXEC p_stringToByte @ai_groupOid08_s, @l_groupOid08 OUTPUT
    EXEC p_stringToByte @ai_groupOid09_s, @l_groupOid09 OUTPUT
    EXEC p_stringToByte @ai_groupOid10_s, @l_groupOid10 OUTPUT
    EXEC p_stringToByte @ai_groupOid11_s, @l_groupOid11 OUTPUT
    EXEC p_stringToByte @ai_groupOid12_s, @l_groupOid12 OUTPUT
    EXEC p_stringToByte @ai_groupOid13_s, @l_groupOid13 OUTPUT
    EXEC p_stringToByte @ai_groupOid14_s, @l_groupOid14 OUTPUT
    EXEC p_stringToByte @ai_groupOid15_s, @l_groupOid15 OUTPUT


    BEGIN TRANSACTION
        -- get the user id:
        SELECT  @l_userId = id
        FROM    ibs_User
        WHERE   oid = @l_userOid

        -- delete all groups which are not needed for this user:
        EXEC @l_retValue = p_User_01$delUnneededGrNoCum @l_userId, @l_userOid,
            @l_groupOid01, @l_groupOid02, @l_groupOid03,
            @l_groupOid04, @l_groupOid05, @l_groupOid06,
            @l_groupOid07, @l_groupOid08, @l_groupOid09,
            @l_groupOid10, @l_groupOid11, @l_groupOid12,
            @l_groupOid13, @l_groupOid14, @l_groupOid15

        -- add the groups which are needed for this user:
        EXEC @l_retValue = p_User_01$addNeededGrNoCum @l_userId, @l_userOid,
            @l_groupOid01, @l_groupOid02, @l_groupOid03,
            @l_groupOid04, @l_groupOid05, @l_groupOid06,
            @l_groupOid07, @l_groupOid08, @l_groupOid09,
            @l_groupOid10, @l_groupOid11, @l_groupOid12,
            @l_groupOid13, @l_groupOid14, @l_groupOid15

        -- actualize all cumulated rights for this user:
        EXEC    p_Rights$updateRightsCumUser @l_userId

    -- finish the transaction:
    IF (@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_NOT_ALL)
                                        -- no severe error occurred?
        COMMIT TRANSACTION              -- make changes permanent
    ELSE                                -- there occurred an error
        ROLLBACK TRANSACTION            -- undo changes

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_User_01$setGroups


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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$delUserGroups'
GO


-- create the new procedure:
CREATE PROCEDURE p_User_01$delUserGroups
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @uUserOid_s     OBJECTIDSTRING
)
AS
    -- DEFINITIONS
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT        -- constant
    DECLARE @ALL_RIGHT INT                  -- constant
    DECLARE @OBJECTNOTFOUND INT             -- constant
    DECLARE @NOT_ALL INT
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    DECLARE @rights RIGHTS                  -- return value of rights proc.

    DECLARE @groupOid OBJECTID
    DECLARE @uUserId GROUPID, @uUserOid OBJECTID

    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @OBJECTNOTFOUND = 3, @NOT_ALL = 31
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0

    EXEC p_stringToByte @uUserOid_s, @uUserOid OUTPUT

    SELECT  @uUserId = id
    FROM    ibs_User u
    WHERE   oid = @uUserOid

    -- define cursor:
    DECLARE GroupUser_Cursor CURSOR FOR 
        SELECT  g.oid
        FROM    ibs_GroupUser gu, ibs_Group g
        WHERE   gu.userId = @uUserId
            AND gu.origGroupId = gu.groupId
            AND gu.groupId = g.id

    -- open the cursor:
    OPEN    GroupUser_Cursor

    -- get the first group:
    FETCH NEXT FROM GroupUser_Cursor INTO @groupOid

    -- loop through all found groups:
    WHILE (@retValue = @ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                        -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
            -- get rights for this user
            EXEC @rights = p_Rights$checkRights
                @uUserOid,              -- given object to be accessed by user
                @groupOid,              -- container of given object
                @userId,                -- user_id
                @op,                    -- required rights user must have to
                                        -- insert/update object
                @rights OUTPUT          -- returned value

            -- check if the user has the necessary rights
            IF (@rights <> @op)         -- the user does not have the rights?
            BEGIN
                SELECT @retValue = @NOT_ALL
            END -- if the user does not have the rights
        END -- if

        -- get next group:
        FETCH NEXT FROM GroupUser_Cursor INTO @groupOid
    END -- while another object found

    -- close and deallocate cursor to allow another cursor with the same 
    -- name:
    CLOSE GroupUser_Cursor
    DEALLOCATE GroupUser_Cursor

    IF (@retValue = @ALL_RIGHT)         -- the user may be deleted from all
                                        -- groups?
    BEGIN
        BEGIN TRANSACTION
            -- delete user from all groups:
            DELETE  ibs_GroupUser
            WHERE   userId = @uUserId

            -- recompute the rights of the user:
            EXEC p_Rights$updateRightsCumUser @uUserId
        COMMIT TRANSACTION
    END -- if the user may be deleted from all groups
    ELSE 
    BEGIN
        SELECT @retValue = @INSUFFICIENT_RIGHTS
    END

    -- return the state value
    RETURN  @retValue
GO
-- p_User_01$delUserGroups


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
-- delete existing procedure:
EXEC p_dropProc 'p_User_01$getInfo'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$getInfo
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_domainId            INT,
    @ao_userName            NAME OUTPUT,
    @ao_password            NAME OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT             -- return value of this procedure

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables and return values:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @ao_userName = '',
    @ao_password = ''

-- body:
    SELECT  @ao_userName = name, @ao_password = password
    FROM    ibs_User
    WHERE   id = @ai_userId
        AND domainId = @ai_domainId
    
    IF (@@ROWCOUNT = 0)
        SELECT @l_retValue = @c_NOT_OK
        
    -- return the state value:
    RETURN  @l_retValue
GO
-- p_User_01$getInfo 
