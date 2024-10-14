/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 * 
 * @version     $Id: ReceivedObject_01Proc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ)  980521
 ******************************************************************************
 */


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
 *                              This id is never used. Instead of the object is
 *                              created in the inbox of the user.
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
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ReceivedObject_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_ReceivedObject_01$create
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
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    DECLARE @containerId        OBJECTID
    DECLARE @oid                OBJECTID
    DECLARE @l_senderFullName   NAME

    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2 -- return values
    -- define return values
    DECLARE @retValue   INT                 -- return value of this procedure
    DECLARE @returnValue     INT            -- return value of this procedure
    DECLARE @rights     INT                 -- rights

    -- initialize return values
    SELECT  @retValue = @INSUFFICIENT_RIGHTS

    BEGIN TRANSACTION  -- Begin of Transaction

/* KR this is not necessary and not correct.
 * The user, who is creating the inbox entry, may be another one than the owner
 * of the inbox.
 * So we use the container which is defined through the input parameters.
        -- get the inbox container
        SELECT  @containerId = inBox
        FROM    ibs_Workspace
        WHERE   userId = @userId

        -- convert containerId to containerId_s:
        EXEC    p_byteToString @containerId, @containerId_s OUTPUT
*/

/* KR this is not necessary!
 * instead the operation is always set to 0 in p_Object$performCreate
    -- set the rights of the inboxContainer
        -- start set rights
        -- compute the right you want to add or set
        SELECT  @rights = SUM (id)
        FROM    ibs_Operation
        WHERE   name IN ('new', 'read', 'view','change','delete',
                'viewRights','setRights','createLink','distribute','addElem',
                'delElem','viewElems')

        -- set new rights
        EXEC  @returnValue = p_rights$set @containerId, @userId, @rights,1
        -- end set rights
    -- end of set the rights of the inboxContainer
*/

        -- create the base object entry:
        EXEC    @retValue = p_Object$performCreate @userId, @op, @tVersionId,
                @name, @containerId_s, @containerKind, @isLink,
                @linkedObjectId_s, @description,
                @oid_s OUTPUT, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- no error occurred?
        BEGIN
            -- get the full name of the sender:
            SELECT  @l_senderFullName = fullname
            FROM    ibs_User
            WHERE   id = @userId

            INSERT INTO ibs_ReceivedObject_01 (oid, distributedId,
                    distributedTVersionId, distributedTypeName, distributedName,
                    distributedIcon, activities, sentObjectId, senderFullName)
	        VALUES (@oid, 0,
	                0, null, N'not defined',
	                N'icon.gif', N'no defined', 0, @l_senderFullName)
        END  -- no error occurred?
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_ReceivedObject_01$create


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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ReceivedObject_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_ReceivedObject_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL,

    @distributedId_s         OBJECTIDSTRING,
    @distributedTVersionId   TVERSIONID,
    @distributedTypeName     NAME,
    @distributedName         NAME,
    @distributedIcon         NAME,
    @activities              NAME,
    @sentObjectId_s          OBJECTIDSTRING
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
    DECLARE @senderFullName NAME
    DECLARE @senderUid  USERID 
    DECLARE @recipientContainerId_s  OBJECTIDSTRING 

    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid                        OUTPUT
    DECLARE @sentObjectId   OBJECTID
    EXEC p_stringToByte @sentObjectId_s, @sentObjectId      OUTPUT
    DECLARE @distributedId   OBJECTID 
    EXEC p_stringToByte @distributedId_s, @distributedId    OUTPUT

    BEGIN TRANSACTION

/*
    -- read out the SenderName
    Select  @senderFullName = u.fullname
    FROM    ibs_Object o, ibs_user u
    WHERE   o.oid = @sentObjectId
    AND     u.id = o.owner
*/
    -- perform the change of the object:
    EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name, 
            @validUntil, @description, @showInNews

    IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
    BEGIN
        -- check if we have all necessary data:
        IF (@distributedName = N'' OR @distributedName IS NULL OR
            @distributedIcon = N'' OR @distributedIcon IS NULL)
        BEGIN
            SELECT  @distributedName = name, @distributedIcon = icon
            FROM    ibs_Object
            WHERE   oid = @distributedId
        END -- if

        UPDATE ibs_ReceivedObject_01
        SET
                distributedId  = @distributedId,
                distributedTVersionId = @distributedTVersionId,
                distributedName  =  @distributedName,
                distributedIcon = @distributedIcon,
                activities = @activities,
                sentobjectId = @sentobjectId
        WHERE   oid = @oid
    END -- operation properly performed?

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_ReceivedObject_01$change


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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_ReceivedObject_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_ReceivedObject_01$retrieve
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
    @checkOutUserName NAME          OUTPUT,
    @distributedId_s         OBJECTIDSTRING OUTPUT,
    @distributedTVersionId   TVERSIONID     OUTPUT,
    @distributedTypeName     NAME           OUTPUT,
    @distributedName         NAME           OUTPUT,    
    @distributedIcon         NAME           OUTPUT,
    @activities              NAME           OUTPUT,
    @recipientContainerId_s  OBJECTIDSTRING OUTPUT,
    @senderFullName          NAME           OUTPUT    
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
    DECLARE @recipientContainerId       OBJECTID        -- used for 'Reiter' definition
    SELECT  @recipientContainerId =     0x0000000000000000
    DECLARE @sentObjectId               OBJECTID        -- used for 'Reiter' definition
    SELECT  @sentObjectId =         0x0000000000000000
    DECLARE @distributedId          OBJECTID        -- used for 'Reiter' definition
    SELECT  @distributedId =        0x0000000000000000
    DECLARE @recipientId            OBJECTID        -- used for 'Reiter' definition
    SELECT  @recipientId =          0x0000000000000000

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

        IF (@retValue = @ALL_RIGHT)
        BEGIN      	
            SELECT  
                @distributedId =  distributedId,
                @distributedTVersionId = distributedTVersionId,
                @distributedTypeName = distributedTypeName,
                @distributedName  =  distributedName,
                @distributedIcon = distributedIcon,
                @activities = activities,
                @sentObjectId = sentObjectId,
                @senderFullName = senderFullName
	        FROM ibs_ReceivedObject_01
	        WHERE oid = @oid

            UPDATE ibs_Recipient_01 
            SET readDate = getdate () 
            WHERE sentObjectId = @sentObjectId 
            AND readDate IS NULL
            AND recipientID = (SELECT oid 
                            FROM ibs_user WHERE id = @userId)

            -- read out the recipientcontainerOid
            SELECT @recipientContainerId = oid
            FROM ibs_object
            WHERE containerId = @sentObjectId 
                AND (tVersionId = 16849665 ) -- recipientlist

            -- convert sendedObjectId to output
            EXEC    p_byteToString @distributedId, @distributedId_s OUTPUT
            -- convert sendedObjectId to output

            EXEC    p_byteToString @recipientContainerId, 
                    @recipientContainerId_s OUTPUT

        END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO


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
EXEC p_dropProc N'p_ReceivedObject_01$delete'
GO

CREATE PROCEDURE p_ReceivedObject_01$delete
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- the user has no rights to delete
    @c_OBJECTNOTFOUND       INT,            -- the object was not found                                        

    -- local variables:
    @l_retValue             INT,            -- return value of a function
--    @l_error                INT,            -- the actual error code
--    @l_ePos                 NVARCHAR (255), -- error position description
    @l_oid                  OBJECTID,        -- the oid of the object
    @l_rights               INT             -- the rights the user has

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
--    @l_error = 0,
    @l_rights = 0

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

--    BEGIN TRANSACTION
/* KR use faster implementation specific to ReceivedObject
        -- all references and the object itself are deleted (plus rights)
        EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId, @ai_op,
            @l_oid OUTPUT
*/

/* KR The following implementation is much faster than p_Object$performDelete.
 * It takes into regard all things which are specific to ReceivedObject
 */    

    -- remark:
    -- The following checks are not performed:
    -- - is the object checked out?
    -- - is the object marked as not deletable?
    -- - are thre any references to the object?
    -- - does the object have an ext key which shall be archived?

    -- check if a rights check shall be done:
    IF (@ai_op = 0)
    BEGIN
        -- mark object and subsequent objects as 'deleted':
        UPDATE  ibs_Object
        SET     state = 1,
                changer = @ai_userId,
                lastChanged = getDate ()
        WHERE   oid = @l_oid
    END -- if
    ELSE
    BEGIN
        -- get rights for this user
        EXEC p_Rights$checkRights
             @l_oid,                -- given object to be accessed by user
             0x0000000000000000,    -- container of given object
             @ai_userId,            -- user_id
             @ai_op,                -- required rights user must have to
                                    -- delete object (operation to be perf.)
             @l_rights OUTPUT       -- returned value

/*
        SELECT  @l_rights = (rights & @ai_op)
        FROM    ibs_RightsCum r, ibs_Object o
        WHERE   o.oid = @l_oid
            AND o.rKey = r.rKey
            AND r.userId = @ai_userId
*/

        -- check if the user has the necessary rights
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            -- mark object and subsequent objects as 'deleted':
            UPDATE  ibs_Object
            SET     state = 1,
                    changer = @ai_userId,
                    lastChanged = getDate ()
            WHERE   oid = @l_oid
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- else

/* KR orig version
    -- get rights for this user
    EXEC p_Rights$checkRights
         @l_oid,                -- given object to be accessed by user
         0x0000000000000000,    -- container of given object
         @ai_userId,            -- user_id
         @ai_op,                -- required rights user must have to
                                -- delete object (operation to be perf.)
         @l_rights OUTPUT       -- returned value

    -- check if the user has the necessary rights
    IF (@l_rights = @ai_op)     -- the user has the rights?
    BEGIN
        -- mark object and subsequent objects as 'deleted':
        UPDATE  ibs_Object
        SET     state = 1,
                changer = @ai_userId,
                lastChanged = getDate ()
        WHERE   oid = @l_oid
            OR  containerId = @l_oid
--            OR  containerOid2 = @l_oid
    END -- if the user has the rights
    ELSE                                -- the user does not have the rights
    BEGIN
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- else the user does not have the rights
*/

/* KR the following is not necessary because the object is already marked as
 * deleted.
 * Instead of these tupples shall be deleted within a reorg process.
    IF (@l_retValue = @c_ALL_RIGHT)
    BEGIN
        -- delete all values of object
        DELETE  ibs_ReceivedObject_01 
        WHERE   oid = @l_oid
    END	-- if
*/

--    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_ReceivedObject_01$delete
