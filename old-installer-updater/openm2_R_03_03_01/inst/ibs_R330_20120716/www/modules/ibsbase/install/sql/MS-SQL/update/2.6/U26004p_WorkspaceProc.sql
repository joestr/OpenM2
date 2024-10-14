/** ****************************************************************************
 * All stored procedures regarding the ibs_Workspace table. <BR>
 *
 * @version     $Id: U26004p_WorkspaceProc.sql,v 1.1 2008/10/07 15:29:32 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR)  980617
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new workspace (incl. rights check). <BR>
 * The rights are checked against the root of the system.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the workspace.
 * @param   @op                 Operation to be performed (possibly in the
 *                              future used for rights check).
 * @param   @wUserId            ID of the user for whom the workspace is
 *                              created.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The workspace was not created due to an unknown
 *                          error.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Workspace_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workspace_01$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @wUserId        USERID
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
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_rights               RIGHTS,
    @l_name                 NAME,           -- name of the current object
    @l_desc                 DESCRIPTION     -- description of the current object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_ALREADY_EXISTS       = 21,
    @c_NOOID                = 0x0000000000000000,
    @c_languageId           = 0             -- default language

    -- initialize local variables:
SELECT
    @l_retValue             = @c_NOT_OK,
    @l_error                = 0,
    @l_rowCount             = 0


    -- definitions:
    -- define local variables:
    DECLARE @domainOid OBJECTID, @domainId DOMAINID, @workspace OBJECTID,
            @workspacesOid OBJECTID,
            @workBox OBJECTID, @outBox OBJECTID, @inBox OBJECTID,
            @news OBJECTID, @hotList OBJECTID, @profile OBJECTID,
            @shoppingCart OBJECTID,
            @orders OBJECTID,
            @workspaceProc STOREDPROCNAME,
            @execStr VARCHAR (255),
            @publicWsp OBJECTID, @admin USERID, @userAdminGroup GROUPID
    DECLARE @domainOid_s OBJECTIDSTRING, @workspace_s OBJECTIDSTRING,
            @workspacesOid_s OBJECTIDSTRING,
            @workBox_s OBJECTIDSTRING, @outBox_s OBJECTIDSTRING,
            @inBox_s OBJECTIDSTRING,
            @news_s OBJECTIDSTRING, @hotList_s OBJECTIDSTRING,
            @profile_s OBJECTIDSTRING,
            @shoppingCart_s OBJECTIDSTRING,
            @orders_s OBJECTIDSTRING

-- body:
    -- set domain id:
    SELECT  @domainId = @wUserId / 0x01000000

    -- get domain info:
    SELECT  @domainOid = oid, @workspacesOid = workspacesOid,
            @admin = adminId, @userAdminGroup = userAdminGroupId,
            @workspaceProc = workspaceProc, @publicWsp = publicOid
    FROM    ibs_Domain_01
    WHERE   id = @domainId

    -- convert domain oid to string value:
    EXEC p_byteToString @domainOid, @domainOid_s OUTPUT

    -- convert workspaces oid to string value:
    EXEC p_byteToString @workspacesOid, @workspacesOid_s OUTPUT

    -- get rights to be set for this user on her/his own workspace:
    SELECT  @l_rights = SUM (id)
    FROM    ibs_Operation

    -- check if there exists already a workspace for this user:
    IF EXISTS
        (SELECT userId
        FROM    ibs_Workspace
        WHERE   userId = @wUserId)      -- workspace already exists?
    BEGIN
        SELECT @l_retValue = @c_ALREADY_EXISTS
    END -- if workspace already exists
    ELSE                                -- workspace does not exist yet
    BEGIN
        BEGIN TRANSACTION
            -- create workspace of the user:
            EXEC p_ObjectDesc_01$get @c_languageId, 'OD_wspPrivate', @l_name OUTPUT, @l_desc OUTPUT
            EXEC @l_retValue = p_Object$performCreate @userId, @op, 0x01013201,
                    @l_name, @workspacesOid_s, 1, 0, '0x0000000000000000', @l_desc,
                    @workspace_s OUTPUT, @workspace OUTPUT

            -- delete all actual defined rights:
            EXEC p_Rights$deleteObjectRights @workspace

            -- set rights for user and user administrator group:
            SELECT  @l_rights = sum (id)
            FROM    ibs_Operation
            WHERE   name IN ('view', 'read', 'viewElems')
            EXEC    p_Rights$setRights @workspace, @userAdminGroup, @l_rights
            EXEC    p_Rights$setRights @workspace, @wUserId, @l_rights

            -- create the several components of the workspace:
            SELECT  @execStr = 'EXEC ' + @workspaceProc + ' ' +
                    CONVERT (VARCHAR (12), @userId) + ', ' +
                    CONVERT (VARCHAR (12), @op) + ', ' +
                    CONVERT (VARCHAR (12), @wUserId) + ', ' +
                    CONVERT (VARCHAR (12), @domainId) + ', ' +
                    '''' + @workspace_s + ''''
            EXEC (@execStr)


            -- store the objects within the workspace:
            UPDATE  ibs_Workspace
            SET     workspace = @workspace,
                    publicWsp = @publicWsp
            WHERE   userId = @wUserId

            -- check if the workspace was created:
            IF (@@ROWCOUNT <= 0)        -- workspace was not created?
            BEGIN
                -- set the return value with the error code:
                SELECT  @l_retValue = @c_NOT_OK
            END -- if workspace was not created
            ELSE
            BEGIN                       -- workspace was created
                -- get objects of workspace:
                SELECT  @workBox = workBox, @outBox = outBox, @inBox = inBox,
                        @news = news, @hotList = hotList, @profile = profile,
                        @shoppingCart = shoppingCart, @orders = orders
                FROM    ibs_Workspace
                WHERE   userId = @wUserId

                -- set rights on news container
                IF (@news <> @c_NOOID)
                BEGIN
                    -- for administrator group and the user himself:
                    SELECT  @l_rights = sum (id)
                    FROM    ibs_Operation
                    WHERE   name IN ('view', 'read', 'viewElems')
                    EXEC    p_Rights$setRights @news, @userAdminGroup, @l_rights
                    EXEC    p_Rights$setRights @news, @wUserId, @l_rights
                END

                -- set rights on work box, out box, in box, hotList,
                -- shopping cart, and orders for administrator group:
                SELECT  @l_rights = sum (id)
                FROM    ibs_Operation
                WHERE   name IN ('new', 'view', 'read', 'change', 'delete',
                            'viewRights', 'setRights', 'createLink',
                            'distribute', 'addElem', 'delElem', 'viewElems',
                            'viewProtocol')
                IF (@workBox <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @workBox, @userAdminGroup, @l_rights
                END
                IF (@outBox <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @outBox, @userAdminGroup, @l_rights
                END
                IF (@inBox <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @inBox, @userAdminGroup, @l_rights
                END
                IF (@hotList <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @hotList, @userAdminGroup, @l_rights
                END
                IF (@shoppingCart <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @shoppingCart, @userAdminGroup, @l_rights
                END
                IF (@orders <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @orders, @userAdminGroup, @l_rights
                END

                -- set rights on profile for administrator group:
                IF (@profile <> @c_NOOID)
                BEGIN
                    SELECT  @l_rights = sum (id)
                    FROM    ibs_Operation
                    WHERE   name IN ('view', 'read', 'change',
                                'viewRights', 'setRights', 'createLink',
                                'viewElems', 'viewProtocol')
                    EXEC p_Rights$setRights @profile, @userAdminGroup, @l_rights
                END


                -- set rights on work box, out box, in box, hotList,
                -- shopping cart, and orders for the user himself:
                SELECT  @l_rights = sum (id)
                FROM    ibs_Operation
                WHERE   name IN ('new', 'view', 'read', 'change', 'delete',
                            'createLink', 'distribute',
                            'addElem', 'delElem', 'viewElems')
                IF (@workBox <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @workBox, @wUserId, @l_rights
                END
                IF (@outBox <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @outBox, @wUserId, @l_rights
                END
                IF (@inBox <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @inBox, @wUserId, @l_rights
                END
                IF (@hotList <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @hotList, @wUserId, @l_rights
                END
                IF (@shoppingCart <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @shoppingCart, @wUserId, @l_rights
                END
                IF (@orders <> @c_NOOID)
                BEGIN
                    EXEC p_Rights$setRights @orders, @wUserId, @l_rights
                END
                -- set rights on user profile for the user himself:
                IF (@profile <> @c_NOOID)
                BEGIN
                    SELECT  @l_rights = sum (id)
                    FROM    ibs_Operation
                    WHERE   name IN ('view', 'read', 'change',
                                'createLink', 'viewElems')
                    EXEC p_Rights$setRights @profile, @wUserId, @l_rights
                END
            END -- else workspace was created
        COMMIT TRANSACTION
    END -- else workspace does not exist yet

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Workspace_01$create


/******************************************************************************
 * Creates the specific data for a new workspace of this domain
 * (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the workspace.
 * @param   @op                 Operation to be performed (possibly in the
 *                              future used for rights check).
 * @param   @wUserId            ID of the user for whom the workspace is
 *                              created.
 * @param   @domainId           ID of the domain where the user belongs to.
 * @param   @workspace_s        String representation of the oid of the
 *                              workspace.
 *
 * @output parameters:
 * @param   @workBox            Oid of the workbox.
 * @param   @outBox             Oid of the outbox.
 * @param   @inBox              Oid of the inbox.
 * @param   @news               Oid of the news folder.
 * @param   @hotList            Oid of the hotlist.
 * @param   @profile            Oid of the user profile.
 * @param   @shoppingCart       Oid of the shopping cart.
 * @param   @orders             Oid of the order container.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The workspace was not created due to an unknown
 *                          error.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Workspace_01$createObjects'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workspace_01$createObjects
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @wUserId        USERID,
    @domainId       DOMAINID,
    @workspace_s    OBJECTIDSTRING
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @workBox OBJECTID, @outBox OBJECTID, @inBox OBJECTID,
            @news OBJECTID,
            @hotList OBJECTID, @profile OBJECTID,
            @shoppingCart OBJECTID,
            @orders OBJECTID
    DECLARE @workBox_s OBJECTIDSTRING, @outBox_s OBJECTIDSTRING,
            @inBox_s OBJECTIDSTRING,
            @news_s OBJECTIDSTRING, @hotList_s OBJECTIDSTRING,
            @profile_s OBJECTIDSTRING,
            @shoppingCart_s OBJECTIDSTRING,
            @orders_s OBJECTIDSTRING

DECLARE
    -- constants:
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_name                 NAME,           -- name of the current object
    @l_desc                 DESCRIPTION     -- description of the current object

    -- assign constants:
SELECT
    @c_languageId           = 0             -- default language

    -- initialize object ids:
    SELECT  @workBox = 0x0000000000000000,
            @outBox = 0x0000000000000000,
            @inBox = 0x0000000000000000,
            @news = 0x0000000000000000,
            @hotList = 0x0000000000000000,
            @profile = 0x0000000000000000,
            @shoppingCart = 0x0000000000000000,
            @orders = 0x0000000000000000


-- body:
/*
    -- workBox
    EXEC p_ObjectDesc_01$get @c_languageId, 'OD_wspWorkBox', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @retValue = p_Object$performCreate @userId, @op, 0x01010021,
            @l_name, @workspace_s, 1, 0, '0x0000000000000000', @l_desc,
            @workBox_s OUTPUT, @workBox OUTPUT
    -- news
    EXEC p_ObjectDesc_01$get @c_languageId, 'OD_wspNews', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @retValue = p_Object$performCreate @userId, @op, 0x01010801,
            @l_name, @workspace_s, 1, 0, '0x0000000000000000', @l_desc,
            @news_s OUTPUT, @news OUTPUT
*/
    -- hotList
    EXEC p_ObjectDesc_01$get @c_languageId, 'OD_wspHotList', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @retValue = p_Object$performCreate @userId, @op, 0x01010041,
            @l_name, @workspace_s, 1, 0, '0x0000000000000000', @l_desc,
            @hotList_s OUTPUT, @hotList OUTPUT
    -- ensure that the hotlist is displayed in the menu:
    UPDATE  ibs_Object
    SET     showInMenu = 1
    WHERE   oid = @hotList
/*
    -- outBox
    EXEC p_ObjectDesc_01$get @c_languageId, 'OD_wspOutBox', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @retValue = p_Object$performCreate @userId, @op, 0x01011D01,
            @l_name, @workspace_s, 1, 0, '0x0000000000000000', @l_desc,
            @outBox_s OUTPUT, @outBox OUTPUT
    -- inBox
    EXEC p_ObjectDesc_01$get @c_languageId, 'OD_wspInBox', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @retValue = p_Object$performCreate @userId, @op, 0x01012D01,
            @l_name, @workspace_s, 1, 0, '0x0000000000000000', @l_desc,
            @inBox_s OUTPUT, @inBox OUTPUT
*/
    -- profile
    EXEC p_ObjectDesc_01$get @c_languageId, 'OD_wspProfile', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @retValue = p_UserProfile_01$create @userId, @op, @wUserId, 0x01013801,
            @l_name, @workspace_s, 1, 0, '0x0000000000000000', @l_desc,
            @profile_s OUTPUT
    EXEC    p_stringToByte @profile_s, @profile OUTPUT

    -- store the objects within the workspace:
    INSERT INTO ibs_Workspace
            (userId, domainId, workBox, outBox,
             inBox, news, hotList, profile,
             shoppingCart, orders)
    VALUES  (@wUserId, @domainId, @workBox, @outBox,
             @inBox, @news, @hotList, @profile,
             @shoppingCart, @orders)

    -- return the state value
    RETURN  @retValue
GO
-- p_Workspace_01$createObjects


/******************************************************************************
 * Changes the attributes of an existing workspace. <BR>
 * There is no rights check done at this time because it makes no sense to
 * check whether a user has access to his/her own workspace.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is changing the workspace
 *                              and whose workspace is changed.
 * @param   @op                 Operation to be performed (possibly in the
 *                              future used for rights check).
 * @param   @workspace          The workspace of the user itself.
 * @param   @workBox            The workBox of the user.
 * @param   @outBox             The box for outgoing messages/objects.
 * @param   @inBox              The box for incoming messages/objects.
 * @param   @news               Everything which is new for the user.
 * @param   @hotList            The personalized bookmarks of the user.
 * @param   @profile            The user's profile.
 * @param   @shoppingCart       The shopping cart.
 * @param   @orders             The order container.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Workspace_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workspace_01$change
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @workspace      OBJECTID,
    @workBox        OBJECTID,
    @outBox         OBJECTID,
    @inBox          OBJECTID,
    @news           OBJECTID,
    @hotList        OBJECTID,
    @profile        OBJECTID,
    @shoppingCart   OBJECTID = 0x000000000000000,
    @orders         OBJECTID = 0x000000000000000
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- define used variables

    BEGIN TRANSACTION

    -- perform update:
    UPDATE  ibs_Workspace
    SET     workspace = @workspace,
            workBox = @workBox,
            outBox = @outBox,
            inBox = @inBox,
            news = @news,
            hotList = @hotList,
            profile = @profile
    WHERE   userId = @userId
    IF (@shoppingCart <> 0x0000000000000000) -- shoppingCart defined?
        UPDATE  ibs_Workspace
        SET     shoppingCart = @shoppingCart
        WHERE   userId = @userId
    IF (@orders <> 0x0000000000000000)  -- order container defined?
        UPDATE  ibs_Workspace
        SET     orders = @orders
        WHERE   userId = @userId

    -- check if the workspace exists:
    IF (@@ROWCOUNT <= 0)                -- workspace does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- if workspace does not exist

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Workspace_01$change


/******************************************************************************
 * Gets all data from a given workspace (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              Id of the object to be retrieved.
 * @param   @userId             Id of the user who is getting the data.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
 * @param   @containerName      Name of the object's container.
 * @param   @containerKind      Kind of object/container relationship.
 * @param   @isLink             Is the object a link?
 * @param   @linkedObjectId     Link if isLink is true.
 * @param   @owner              ID of the owner of the object.
 * @param   @ownerName          Name of the owner of the object.
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @creatorName        Name of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the
 *                              object.
 * @param   @changerName        Name of person who did the last change to the
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         show in news flag
 * @param   @showInNews         Display object in the news.
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
 * @param   @domainId           The id of the domain where the workspace
 *                              belongs to.
 * @param   @workspace          The workspace of the user itself.
 * @param   @workBox            The workBox of the user.
 * @param   @outBox             The box for outgoing messages/objects.
 * @param   @inBox              The box for incoming messages/objects.
 * @param   @news               Everything which is new for the user.
 * @param   @hotList            The personalized bookmarks of the user.
 * @param   @profile            The user's profile.
 * @param   @publicWsp          The oid of public container being at the same
 *                              place as this workspace.
 * @param   @shoppingCart       The shopping cart.
 * @param   @orders             The order container.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Workspace_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workspace_01$retrieve
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- common output parameters
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
    @checkOutUserName NAME          OUTPUT,
    -- type-specific output parameters:
    @domainId       DOMAINID        OUTPUT,
    @workspace      OBJECTID        OUTPUT,
    @workBox        OBJECTID        OUTPUT,
    @outBox         OBJECTID        OUTPUT,
    @inBox          OBJECTID        OUTPUT,
    @news           OBJECTID        OUTPUT,
    @hotList        OBJECTID        OUTPUT,
    @profile        OBJECTID        OUTPUT,
    @publicWsp      OBJECTID        OUTPUT,
    @shoppingCart   OBJECTID = 0x0000000000000000 OUTPUT,
    @orders         OBJECTID = 0x0000000000000000 OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @OBJECTNOTFOUND INT
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- define local variables:
    DECLARE @oid OBJECTID
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @OBJECTNOTFOUND = 3
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
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
            -- retrieve object type specific data:
            SELECT  @domainId = domainId, @workspace = workspace,
                    @workBox = workBox, @outBox = outBox, @inBox = inBox,
                    @news = news, @hotList = hotList,
                    @profile = profile, @publicWsp = publicWsp,
                    @shoppingCart = shoppingCart,
                    @orders = orders
            FROM    ibs_Workspace
            WHERE   workspace = @oid

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT <= 0)        --  no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Workspace_01$retrieve


/******************************************************************************
 * Gets all data from a given workspace for the actual user. <BR>
 * There is no rights check done at this time because it makes no sense to
 * check whether a user has access to his/her own workspace.
 *
 * @input parameters:
 * @param   @userId             ID of the user who wants to get his/her
 *                              workspace data.
 * @param   @op                 Operation to be performed (possibly in the
 *                              future used for rights check).
 *
 * @output parameters:
 * @param   @domainId           The id of the domain where the workspace
 *                              belongs to.
 * @param   @workspace          The workspace of the user itself.
 * @param   @workBox            The workBox of the user.
 * @param   @outBox             The box for outgoing messages/objects.
 * @param   @inBox              The box for incoming messages/objects.
 * @param   @news               Everything which is new for the user.
 * @param   @hotList            The personalized bookmarks of the user.
 * @param   @profile            The user's profile.
 * @param   @publicWsp          The oid of public container being at the same
 *                              place as this workspace.
 * @param   @shoppingCart       The shopping cart.
 * @param   @orders             The order container.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Workspace_01$retrieveForActU'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workspace_01$retrieveForActU
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    -- output parameters
    @domainId       DOMAINID        OUTPUT,
    @workspace      OBJECTID        OUTPUT,
    @workBox        OBJECTID        OUTPUT,
    @outBox         OBJECTID        OUTPUT,
    @inBox          OBJECTID        OUTPUT,
    @news           OBJECTID        OUTPUT,
    @hotList        OBJECTID        OUTPUT,
    @profile        OBJECTID        OUTPUT,
    @publicWsp      OBJECTID        OUTPUT,
    @shoppingCart   OBJECTID = 0x0000000000000000 OUTPUT,
    @orders         OBJECTID = 0x0000000000000000 OUTPUT,
    @name           NAME            OUTPUT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    -- get the data of the workspace and return them
    SELECT  @name = name, @domainId = domainId, @workspace = workspace,
            @workBox = workBox, @outBox = outBox, @inBox = inBox,
            @news = news, @hotList = hotList,
            @profile = profile, @publicWsp = publicWsp,
            @shoppingCart = shoppingCart,
            @orders = orders
    FROM    ibs_Workspace w, ibs_object o
    WHERE   w.userId = @userId
    AND     w.workspace = o.oid

    -- check if the workspace exists:
    IF (@@ROWCOUNT <= 0)                -- workspace does not exist?
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- if workspace does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Workspace_01$retrieveForActU


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
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
-- delete existing procedure:
EXEC p_dropProc 'p_Workspace_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Workspace_01$delete
(
    @userId         USERID,
    @op             INT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables
    DECLARE @workspace OBJECTID, @workBox OBJECTID, @outBox OBJECTID,
            @inBox OBJECTID, @news OBJECTID, @hotList OBJECTID,
            @profile OBJECTID,
            @shoppingCart OBJECTID,
            @orders OBJECTID
    DECLARE @workspace_s OBJECTIDSTRING, @workBox_s OBJECTIDSTRING, @outBox_s OBJECTIDSTRING,
            @inBox_s OBJECTIDSTRING, @news_s OBJECTIDSTRING, @hotList_s OBJECTIDSTRING,
            @profile_s OBJECTIDSTRING,
            @shoppingCart_s OBJECTIDSTRING,
            @orders_s OBJECTIDSTRING

    -- get the data of the workspace
    SELECT  @workspace = workspace, @workBox = workBox,
            @outBox = outBox, @inBox = inBox,
            @news = news, @hotList = hotList,
            @profile = profile,
            @shoppingCart = shoppingCart,
            @orders = orders
    FROM    ibs_Workspace
    WHERE   userId = @userId

    -- check if the workspace exists:
    IF (@@ROWCOUNT > 0)                 -- workspace exists?
    BEGIN
        BEGIN TRANSACTION

        -- delete workspace itself
        DELETE  ibs_Workspace
        WHERE   userId = @userId

        -- convert OBJECTIDs to OBJECTIDSTRINGs
        EXEC p_byteToString @workspace, @workspace_s OUTPUT
        EXEC p_byteToString @workBox, @workBox_s OUTPUT
        EXEC p_byteToString @outBox, @outBox_s OUTPUT
        EXEC p_byteToString @inBox, @inBox_s OUTPUT
        EXEC p_byteToString @news, @news_s OUTPUT
        EXEC p_byteToString @hotList, @hotList_s OUTPUT
        EXEC p_byteToString @profile, @profile_s OUTPUT
        EXEC p_byteToString @shoppingCart, @shoppingCart_s OUTPUT
        EXEC p_byteToString @orders, @orders_s OUTPUT

        -- delete belonging objects
        EXEC p_Object$delete @orders_s, @userId, @op
        EXEC p_Object$delete @shoppingCart_s, @userId, @op
        EXEC p_Object$delete @hotList_s, @userId, @op
        EXEC p_Object$delete @news_s, @userId, @op
        EXEC p_Object$delete @inBox_s, @userId, @op
        EXEC p_Object$delete @outBox_s, @userId, @op
        EXEC p_Object$delete @workBox_s, @userId, @op
        EXEC p_Object$delete @workspace_s, @userId, @op
        EXEC p_UserProfile_01$delete @profile_s, @userId, @op

        COMMIT TRANSACTION
    END -- if workspace exists

    ELSE                                -- the workspace does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the workspace does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Workspace_01$delete


/******************************************************************************
 * This procedure is used after importing a xml - structure in the
 * workspace of the user to assign standardobjects like Inbox, Outbox,
 * ShoppingCart, NewsContainer, Hotlist etc. to Workspace
 * (Table ibs_workspace).
 * The objects to be assigned are identified via their type and only the
 * objects are assigned where no other object was assigned to Workspace Table
 * in procedure p_Workspace$createObjects. <BR>
 *
 * @input parameters:
 * @param   @oid_s              oid of user.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- delete existing procedure
EXEC p_dropProc 'p_Workspace$assignStdObjects'
GO

CREATE PROCEDURE p_Workspace$assignStdObjects
(
    @ai_oid_s          OBJECTIDSTRING
)
AS
    -- define return constants
    DECLARE @c_ALL_RIGHT INT, @c_INSUFFICIENT_RIGHTS INT,
            @c_OBJECTNOTFOUND INT, @c_NOOID OBJECTID
    -- set constants
    SELECT  @c_ALL_RIGHT = 1, @c_INSUFFICIENT_RIGHTS = 2,   -- return values
            @c_OBJECTNOTFOUND = 3, @c_NOOID = 0x0000000000000000

    -- define return values
    DECLARE @l_retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @l_retValue = @c_ALL_RIGHT

    -- define oid values
    DECLARE @l_workbox OBJECTID, @l_outbox OBJECTID,
            @l_inbox OBJECTID, @l_news OBJECTID,
            @l_profile OBJECTID, @l_workspace OBJECTID,
            @l_shoppingCart OBJECTID, @l_orders OBJECTID,
            @l_posnopath POSNOPATH_VC, @l_oid OBJECTID

    -- convert input oid string to oid
    EXEC p_StringToByte @ai_oid_s, @l_oid OUTPUT

    -- get all oids of all standardobjects in workspace
    SELECT  @l_workbox = w.workbox, @l_outbox = w.outbox, @l_inbox = w.inbox,
            @l_news = w.news, @l_profile = w.profile,
            @l_shoppingCart = w.shoppingCart, @l_orders = w.orders,
            @l_workspace = w.workspace, @l_posnopath = ow.posnopath
    FROM    ibs_Workspace w, ibs_User u, ibs_Object ow
    WHERE   w.userId = u.id
      AND   u.oid = @l_oid
      AND   ow.oid = w.workspace

    -- check workbox
    IF (@l_workbox = @c_NOOID)
    BEGIN
        -- set workbox to first container to be found in workspace
        SELECT @l_workbox = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = 'Container'
          AND  o.posnopath LIKE @l_posnopath + '%'
    END

    -- check outbox
    IF (@l_outbox = @c_NOOID)
    BEGIN
        -- set outbox to first SentObjectContainer to be found in workspace
        SELECT @l_outbox = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = 'SentObjectContainer'
          AND  o.posnopath LIKE @l_posnopath + '%'
    END

    -- check inbox
    IF (@l_inbox = @c_NOOID)
    BEGIN
        -- set inbox to first Inbox to be found in workspace
        SELECT @l_inbox = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = 'Inbox'
          AND  o.posnopath LIKE @l_posnopath + '%'

    END

    -- check news
    IF (@l_news = @c_NOOID)
    BEGIN
        PRINT 'set newscontainer to first newscontainer to be found in workspace'
        SELECT @l_news = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = 'NewsContainer'
          AND  o.posnopath LIKE @l_posnopath + '%'
    END

    -- check profile
    IF (@l_profile = @c_NOOID)
    BEGIN
        -- set profile to first userprofile to be found in workspace
        SELECT @l_profile = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = 'UserProfile'
          AND  o.posnopath LIKE @l_posnopath + '%'
    END

    -- store the new object oids:
    UPDATE  ibs_Workspace
    SET     workbox = @l_workbox,
            outbox = @l_outbox,
            inbox = @l_inbox,
            news = @l_news,
            profile = @l_profile
    WHERE   workspace = @l_workspace

    -- return the state value
    RETURN  @l_retValue
GO
