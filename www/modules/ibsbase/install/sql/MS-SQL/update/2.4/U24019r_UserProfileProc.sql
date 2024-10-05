/******************************************************************************
 * All stored procedures regarding the ibs_UserProfile table. <BR>
 *
 * @version     $Id: U24019r_UserProfileProc.sql,v 1.1 2005/08/22 15:24:50 klaus Exp $
 *
 * @author      Bernd Buchegger (BB)  980709
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new UserProfile (incl. rights check). <BR>
 * The rights are checked against the root of the system.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the UserProfile.
 * @param   @op                 Operation to be performed (possibly in the
 *                              future used for rights check).
 * @param   @upUserId           ID of the user for whom the UserProfile is
 *                              created.
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The UserProfile was not created due to an unknown
 *                          error.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_UserProfile_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_UserProfile_01$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @upUserId       USERID,
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

    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

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
    DECLARE @oid OBJECTID
        -- initialize local variables:
    SELECT  @oid = 0x0000000000000000

    DECLARE @newsTimeLimit              INT
    DECLARE @newsShowOnlyUnread         BOOL
    DECLARE @outboxUseTimeLimit         BOOL
    DECLARE @outboxTimeLimit            INT
    DECLARE @outboxUseTimeFrame         BOOL
    DECLARE @outboxTimeFrameFrom        DATETIME
    DECLARE @outboxTimeFrameTo          DATETIME
    DECLARE @showExtendedAttributes     BOOL
    DECLARE @showRef                    BOOL
    DECLARE @showExtendedRights         BOOL
    DECLARE @saveProfile                BOOL

    DECLARE @showFilesInWindows         BOOL
    DECLARE @lastLogin                  DATETIME
    DECLARE @notificationKind           INT
    DECLARE @sendSms                    BOOL
    DECLARE @addWeblink                 BOOL

    -- set default values
    SELECT @newsTimeLimit = 5
    SELECT @newsShowOnlyUnread = 0
    SELECT @outboxUseTimeLimit = 0
    SELECT @outboxUseTimeLimit = 0
    SELECT @outboxUseTimeFrame = 0
    -- SELECT @outboxTimeFrameFrom DATETIME
    -- SELECT @outboxTimeFrameTo DATETIME
    SELECT @showExtendedAttributes = 0
    SELECT @saveProfile = 0
    SELECT @showRef = 1
    SELECT @showExtendedRights = 0
    SELECT @showFilesInWindows = 0
    -- define local variables:
    DECLARE @domainId DOMAINID
    DECLARE @layoutId OBJECTID
        -- initialize local variables:
    SELECT  @domainId = @userId / 0x01000000
    SELECT  @layoutId = MIN (oid)
    FROM    ibs_Layout_01
    WHERE   isDefault = 1
        AND domainId = @domainId
    SELECT @notificationKind = 1
    SELECT @sendSms = 0
    SELECT @addWeblink = 0

    -- body:
    BEGIN TRANSACTION
    -- create base object:
    EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId,
                        @name, @containerId_s, @containerKind,
                        @isLink, @linkedObjectId_s, @description,
                        @oid_s OUTPUT, @oid OUTPUT

    IF (@retValue = @ALL_RIGHT)         -- object created successfully?
    BEGIN
        -- create object specific data:
        INSERT INTO ibs_UserProfile
                (oid, userId, newsTimeLimit, newsShowOnlyUnread, outboxUseTimeLimit,
                 outboxTimeLimit, outboxUseTimeFrame, outboxTimeFrameFrom,
                 outboxTimeFrameTo, showExtendedAttributes , showFilesInWindows,
                 lastLogin, layoutId, showRef, showExtendedRights, saveProfile, 
                 notificationKind, sendSms, addWeblink
                 )
        VALUES  (@oid, @upUserId, @newsTimeLimit, @newsShowOnlyUnread, @outboxUseTimeLimit,
                 @outboxTimeLimit, @outboxUseTimeFrame, @outboxTimeFrameFrom,
                 @outboxTimeFrameTo, @showExtendedAttributes , @showFilesInWindows,
                 @lastLogin, @layoutId, @showRef, @showExtendedRights, @saveProfile,
                 @notificationKind, @sendSms, @addWeblink
                 )
                 
        -- check if the UserProfile was created:
        IF (@@ROWCOUNT <= 0)        -- UserProfile was not created
        BEGIN
            -- set the return value with the error code:
            SELECT  @retValue = @OBJECTNOTFOUND -- set return value
        END -- if UserProfile was not created
    END -- if object created successfully

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_UserProfile_01$create


/******************************************************************************
 * Changes the attributes of an existing UserProfile. (incl. rights check).<BR>
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         the showInNews flag      
 *
 * @param   @newsTimeLimit      Time limit for the newslist
 * @param   @newsShowOnlyUnread Flag to show only unread messages in newslist
 * @param   @outboxUseTimeLimit Flag to use time limit filter in outbox
 * @param   @outboxTimeLimit    Time limit filter for outbox (in days)
 * @param   @outboxUseTimeFrame Flag to use time frame filter in outbox
 * @param   @outboxTimeFrameFrom Begin date of time frame filter in outbox
 * @param   @outboxTimeFrameTo  End date of time frame filter in outbox
 * @param   @showExtendedAttributes Flag to show complete object attributes
 * @param   @showFilesInWindows Flag to show files in a separate window
 * @param   @lastLogin          Date of last login
 * @param   @layoutId_s              
 * @param   @showExtendedRights    
 * @param   @saveProfile           
 * @param   @notificationKind      bit-pattern for kind of notification
 * @param   @sendSms               should sms be send when notify? 
 * @param   @addWeblink            should weblink be added to email ?
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_UserProfile_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_UserProfile_01$change
(
    -- input parameters:
    @oid_s                  OBJECTIDSTRING,
    @userId                 USERID,
    @op                     INT,
    @name                   NAME,
    @validUntil             DATETIME,
    @description            DESCRIPTION,
    @showInNews             BOOL,
    -- type-specific parameters:
    @newsTimeLimit          INT,
    @newsShowOnlyUnread     BOOL,
    @outboxUseTimeLimit     BOOL,
    @outboxTimeLimit        INT,
    @outboxUseTimeFrame     BOOL,
    @outboxTimeFrameFrom    DATETIME,
    @outboxTimeFrameTo      DATETIME,
    @showExtendedAttributes BOOL,
    @showFilesInWindows     BOOL,
    @lastLogin              DATETIME,
    @layoutId_s             OBJECTIDSTRING,
    @showExtendedRights     BOOL,
    @saveProfile            BOOL,
    @notificationKind       INT,
    @sendSms                BOOL,
    @addWeblink             BOOL

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
    DECLARE @oid OBJECTID
    DECLARE @layoutId OBJECTID
    DECLARE @showRef BOOL
    SELECT @showRef = 1 - @showExtendedAttributes

        -- initialize local variables:
    -- convertions: (OBJECTIDSTRING) - all input object ids must be converted
    EXEC p_stringToByte @layoutId_s, @layoutId OUTPUT
        

    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update object type specific data:
            UPDATE  ibs_UserProfile
            SET     newsTimeLimit = @newsTimeLimit,
                    newsShowOnlyUnread = @newsShowOnlyUnread,
                    outboxUseTimeLimit = @outboxUseTimeLimit,
                    outboxTimeLimit = @outboxTimeLimit,
                    outboxUseTimeFrame = @outboxUseTimeFrame,
                    outboxTimeFrameFrom = @outboxTimeFrameFrom,
                    outboxTimeFrameTo = @outboxTimeFrameTo,
                    showExtendedAttributes = @showExtendedAttributes,
                    showFilesInWindows = @showFilesInWindows,
                    lastLogin = @lastLogin,
                    layoutId = @layoutId,
                    showExtendedRights = @showExtendedRights,
                    showRef = @showRef,
                    saveProfile = @saveProfile,
                    notificationKind = @notificationKind,
                    sendSms = @sendSms, 
                    addWeblink = @addWeblink 
             WHERE  oid = @oid
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_UserProfile_01$change


/******************************************************************************
 * Gets all data from a given UserProfile.(incl. rights check) <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             Id of the user who is getting the data.
 * @param   @op                 Operation to be performed (used for rights
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
 * @param   @newsTimeLimit       time limit for the newslist
 * @param   @newsShowOnlyUnread  flag to show only unread messages in newslist
 * @param   @outboxUseTimeLimit  flag to use time limit filter in outbox
 * @param   @outboxTimeLimit     time limit filter for outbox (in days)
 * @param   @outboxUseTimeFrame  flag to use time frame filter in outbox
 * @param   @outboxTimeFrameFrom begin date of time frame filter in outbox
 * @param   @outboxTimeFrameTo   end date of time frame filter in outbox
 * @param   @showExtendedAttributes  flag to show complete object attributes
 * @param   @showFilesInWindows      flag to show files in a separate window
 * @param   @lastLogin               date of last login
 * @param   @m2AbsBasePath           absolute m2 base path - workaround!
 * @param   @home                home path of the web
 * @param   @layoutId              
 * @param   @layoutName            
 * @param   @showRef               
 * @param   @showExtendedRights    
 * @param   @saveProfile           
 * @param   @notificationKind      bit-pattern for kind of notification
 * @param   @sendSms               should sms be send when notify? 
 * @param   @addWeblink            should weblink be added to email ?

 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_UserProfile_01$retrieve'
GO


-- create the new procedure:
CREATE PROCEDURE p_UserProfile_01$retrieve
(
    -- input parameters:
    @oid_s                  OBJECTIDSTRING,
    @userId                 USERID,
    @op                     INT,
    -- output parameters
    @state                  STATE           OUTPUT,
    @tVersionId             TVERSIONID      OUTPUT,
    @typeName               NAME            OUTPUT,
    @name                   NAME            OUTPUT,
    @containerId            OBJECTID        OUTPUT,
    @containerName          NAME            OUTPUT,
    @containerKind          INT             OUTPUT,
    @isLink                 BOOL            OUTPUT,
    @linkedObjectId         OBJECTID        OUTPUT,
    @owner                  USERID          OUTPUT,
    @ownerName              NAME            OUTPUT,
    @creationDate           DATETIME        OUTPUT,
    @creator                USERID          OUTPUT,
    @creatorName            NAME            OUTPUT,
    @lastChanged            DATETIME        OUTPUT,
    @changer                USERID          OUTPUT,
    @changerName            NAME            OUTPUT,
    @validUntil             DATETIME        OUTPUT,
    @description            DESCRIPTION     OUTPUT,
    @showInNews             BOOL            OUTPUT,
    @checkedOut             BOOL            OUTPUT,
    @checkOutDate           DATETIME        OUTPUT,
    @checkOutUser           USERID          OUTPUT,
    @checkOutUserOid        OBJECTID        OUTPUT,
    @checkOutUserName       NAME            OUTPUT,
    -- type-specific attributes:
    @newsTimeLimit          INT             OUTPUT,
    @newsShowOnlyUnread     BOOL            OUTPUT,
    @outboxUseTimeLimit     BOOL            OUTPUT,
    @outboxTimeLimit        INT             OUTPUT,
    @outboxUseTimeFrame     BOOL            OUTPUT,
    @outboxTimeFrameFrom    DATETIME        OUTPUT,
    @outboxTimeFrameTo      DATETIME        OUTPUT,
    @showExtendedAttributes BOOL            OUTPUT,
    @showFilesInWindows     BOOL            OUTPUT,
    @lastLogin              DATETIME        OUTPUT,
    @m2AbsBasePath          VARCHAR (255)   OUTPUT,
    @home                   VARCHAR (255)   OUTPUT,
    @LayoutId               OBJECTID        OUTPUT,
    @LayoutName             NAME            OUTPUT,
    @showRef                BOOL            OUTPUT,
    @showExtendedRights     BOOL            OUTPUT,
    @saveProfile            BOOL            OUTPUT,
    @notificationKind       INT             OUTPUT,
    @sendSms                BOOL            OUTPUT,
    @addWeblink             BOOL            OUTPUT

)
AS
    -- DEFINITIONS
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
        -- initialize local variables:

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
        BEGIN TRANSACTION
        -- get the data of the UserProfile and return them
        SELECT
            @newsTimeLimit = newsTimeLimit,
            @newsShowOnlyUnread = newsShowOnlyUnread,
            @outboxUseTimeLimit = outboxUseTimeLimit,
            @outboxTimeLimit = outboxTimeLimit,
            @outboxUseTimeFrame = outboxUseTimeFrame,
            @outboxTimeFrameFrom = outboxTimeFrameFrom,
            @outboxTimeFrameTo = outboxTimeFrameTo,
            @showExtendedAttributes = showExtendedAttributes,
            @showFilesInWindows = showFilesInWindows,
            @lastLogin = lastLogin,
            @layoutId = layoutId,
            @showRef = showRef,
            @showExtendedRights = showExtendedRights,
            @saveProfile = saveProfile,
            @notificationKind = notificationKind,
            @sendSms = sendSms, 
            @addWeblink = addWeblink 

        FROM  ibs_UserProfile
        WHERE oid = @oid

        SELECT @layoutName = name
        FROM ibs_Layout_01
        WHERE oid = @layoutId
        
        -- WORKAROUND
        -- get m2 absolute base path
        SELECT @m2AbsBasePath = value
        FROM   ibs_System
        WHERE  name = 'ABS_BASE_PATH'
        COMMIT TRANSACTION

        -- get web absolute base path
        SELECT @home = value
        FROM   ibs_System
        WHERE  name = 'WWW_BASE_PATH'
    END -- if operation properly performed

    -- return the state value:
    RETURN  @retValue
GO
-- p_UserProfile_01$retrieve


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
EXEC p_dropProc 'p_UserProfile_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_UserProfile_01$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op,
                @oid OUTPUT

/* KR should not be done because of undo functionality!
        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  ibs_UserProfile
            WHERE   oid = @oid
        END -- if operation properly performed
*/
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_UserProfile_01$delete
