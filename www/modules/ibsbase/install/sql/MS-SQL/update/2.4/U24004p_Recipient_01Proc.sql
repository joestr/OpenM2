/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 * 
 *
 * @version     $Id: U24004p_Recipient_01Proc.sql,v 1.1 2005/01/26 13:10:31 klaus Exp $
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
EXEC p_dropProc 'p_Recipient_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Recipient_01$create
(
    -- input parameters:
    @userId             USERID,
    @op                 INT,
    @tVersionId         TVERSIONID,
    @name               NAME,
    @containerId_s      OBJECTIDSTRING,
    @containerKind      INT,
    @isLink             BOOL,
    @linkedObjectId_s   OBJECTIDSTRING,
    @description        DESCRIPTION,
    @recipientOid_s     OBJECTIDSTRING,
    @sentObjectOid_s    OBJECTIDSTRING,
    @recipientRights       INT,  --  the rights the sender want to 
                                 --  set by the recipient
    @frooze              BOOL,    

    -- output parameters:
    @oid_s               OBJECTIDSTRING OUTPUT
)
AS
    -- declare properties
    DECLARE @oid                OBJECTID
    DECLARE @containerId        OBJECTID
    DECLARE @linkedObjectId     OBJECTID
    DECLARE @sentObjectOid      OBJECTID
    DECLARE @recipientOid       OBJECTID
    DECLARE @inboxOid           OBJECTID
    SELECT  @inboxOid       =   0x0000000000000000
    DECLARE @inboxOid_s         OBJECTIDSTRING
    DECLARE @recipientName      NAME
    DECLARE @returnValue        INT
    DECLARE @returnFlag         INT
    DECLARE @rId                ID
    DECLARE @ReceivedObjecttVersionId         TVERSIONID
    SELECT  @ReceivedObjecttVersionId = 0x01015601
    --init properties
    SELECT  @oid        =   0x0000000000000000

    SELECT  @inboxOid               =   0x0000000000000000

    DECLARE @distributedId           OBJECTID
    SELECT  @distributedId           =   0x0000000000000000
    DECLARE @distributedTVersionId   TVERSIONID    
    DECLARE @distributedTypeName     NAME          
    DECLARE @distributedName         NAME          
    DECLARE @distributedIcon         NAME          
    DECLARE @activities              NAME               
    DECLARE @sentObjectId            OBJECTID
    DECLARE @senderFullName          NAME 
 

    -- CONVERTIONS (OBJECTIDSTRING) 
    EXEC p_stringToByte @linkedObjectId_s,   @linkedObjectId     OUTPUT
    EXEC p_stringToByte @containerId_s,      @containerId        OUTPUT
    EXEC p_stringToByte @recipientOid_s,     @recipientOid       OUTPUT
    EXEC p_stringToByte @sentObjectOid_s,    @sentObjectOid      OUTPUT

    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2 -- return values
    -- define return values
    DECLARE @retValue INT           
    DECLARE @rights   INT         
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    SELECT  @rights = 0

    BEGIN TRANSACTION

        --  find the containerId_s of the recipients
        --  the recipientContainer is Part_Of the Sentobject
        SELECT  @containerId = oid
        FROM    ibs_Object
        WHERE   containerId = @sentObjectOid
                AND tVersionId = 0x01011b01 -- Empf�nger
        -- create recipient
        -- read out the name of the recipient
        SELECT  @recipientName = fullname, @rId = id
        FROM    ibs_User
        WHERE   oid = @recipientOid

   --     Select @name = @recipientName

        -- convert containerId to containerId_s:
        EXEC    p_byteToString @containerId, @containerId_s OUTPUT

        -- create the recipientsObject in the Database
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT


        IF (@retValue = @ALL_RIGHT)     -- p_Object$performCreate operation properly performed?
        BEGIN 

            -- read out data of the distributed object  and the description of
            -- sentobject_01 for the ReceivedObject_01
            SELECT
                    @distributedId =  distributeId,
                    @distributedTVersionId = distributeTVersionId,
                    @distributedTypeName = distributeTypeName,
                    @distributedName  =  distributeName,
                    @distributedIcon = distributeIcon,
                    @activities = activities

            FROM    ibs_SentObject_01
            WHERE   oid = @sentObjectOid

            -- create recipient
            -- read out the name and teh rId of the recipient
            SELECT  @recipientName = fullname, @rId = id
            FROM    ibs_User
            WHERE   oid = @recipientOid

            -- Insert the other values
            INSERT INTO ibs_Recipient_01 
                    (oid, recipientId, recipientName, readDate, sentObjectId, deleted)
            VALUES  (@oid, @recipientOid , @recipientName, null, @sentObjectOid, 0)

              -- add the rights to the distributed Object for the recipients
            EXEC  @returnValue = p_Rights$addRights @distributedId, @rId, @recipientRights, 1        
        --  EXEC  @returnValue = p_Rights$setRights @distributedId, @rId, @recipientRights, 1        

            -- create and update the receivedObject_01 for the inbox
            SELECT  @inboxOid = inbox
            FROM    ibs_Workspace
            WHERE   userId = @rId
      
            -- convert containerId to containerId_s:
            EXEC    p_byteToString @inboxOid, @inboxOid_s OUTPUT

            -- compute the right you want to add to the inboxContainer
            SELECT  @rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN ('new', 'read', 'view','change','delete',
                'viewRights','setRights','createLink','distribute','addElem',
                'delElem','viewElems')

            -- set new rights
            EXEC  @returnValue = p_rights$set @inboxOid, @rId, @rights, 1 

        END -- create recipient

        -- create a ReceivedObject_01 in the InboxContainer
        EXEC @retValue = p_Object$performCreate @rId, @op, @ReceivedObjecttVersionId, 
                @name, @inboxOid_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- end of create and update the receivedObject_01 for the inbox 	        
            -- read out the SenderName

            SELECT  @senderFullName = u.fullname
            FROM    ibs_Object o, ibs_user u
            WHERE   o.oid = @sentObjectOid
            AND     u.id = o.owner

            INSERT INTO ibs_ReceivedObject_01 (oid, distributedId,
                    distributedTVersionId, distributedTypeName, distributedName, 
                    distributedIcon, activities, sentObjectId, senderFullName )
	        VALUES (@oid, @distributedId, @distributedTVersionId, @distributedTypeName, 
                    @distributedName, @distributedIcon, 
                    @activities, @sentObjectOid, @senderFullName)

        END -- end of create ibs_ReceivedObject_01 table
       
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO


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
EXEC p_dropProc 'p_Recipient_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Recipient_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL
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
    
    /*
    -- body:
    BEGIN TRANSACTION

        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

      
    COMMIT TRANSACTION
    */
    -- return the state value:
    RETURN  @retValue

GO


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
EXEC p_dropProc 'p_Recipient_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Recipient_01$retrieve
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
    @recipientOid_s     OBJECTIDSTRING      OUTPUT,
    @recipientName      NAME                OUTPUT,
    @recipientPosition  NAME                OUTPUT,
    @recipientEmail     NAME                OUTPUT,
    @recipientTitle     NAME                OUTPUT,
    @recipientCompany   NAME                OUTPUT
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
    DECLARE @recipientOid   OBJECTID

    SELECT @recipientPosition  = 'undefined'
    SELECT @recipientEmail     = 'undefined'
    SELECT @recipientTitle     = 'undefined'
    SELECT @recipientCompany   = 'undefined'

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
            SELECT  @recipientOid = recipientId
	        FROM    ibs_Recipient_01
	        WHERE   oid = @oid

            SELECT  @recipientName = fullname
            FROM    ibs_User
            WHERE   oid = @recipientOid

            -- convert sentObject to output
            EXEC    p_byteToString @recipientOid,@recipientOid_s OUTPUT
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
EXEC p_dropProc 'p_Recipient_01$delete'
GO

CREATE PROCEDURE p_Recipient_01$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT


    ---------------------------------------------------------------------------
    -- DEFINITIONS
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_DELETE INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_DELETE = 16                          -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights INT                 -- return value of called proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0

    ---------------------------------------------------------------------------
    -- START
   BEGIN TRANSACTION
        -- all references and the object itself are deleted (plus rights)
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

        IF (@retValue = @ALL_RIGHT)
        BEGIN
            
            -- delete all values of object
            DELETE  ibs_Recipient_01 
            WHERE   oid = @oid

        END	

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue

GO
