 /******************************************************************************
 * All stored procedures regarding the object table. <BR>
 *
 * @version     $Id: SentObject_01Proc.sql,v 1.9 2009/12/02 18:35:01 rburgermann Exp $
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
-- delete existing procedure:
EXEC p_dropProc N'p_SentObject_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_SentObject_01$create
(
    -- common input parameters:
    @userId             USERID,
    @op                 INT,
    @tVersionId         TVERSIONID,
    @name               NAME,
    @containerId_s      OBJECTIDSTRING,
    @containerKind      INT,
    @isLink             BOOL,
    @linkedObjectId_s   OBJECTIDSTRING,
    @description        DESCRIPTION,
    -- specific input parameters:
    @deleted            BOOL,   
    @distributeId_s     OBJECTIDSTRING,
    @opDistribute       INT,
    @senderRights       INT,
    @freeze             BOOL,
    -- commmon output parameters:
    @oid_s              OBJECTIDSTRING OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, 
            @ALREADY_EXISTS INT
    -- define return values:
    DECLARE @retValue   INT             -- return value of this procedure
    -- define variables used for mapping oids given as parameter strings:
    DECLARE @containerId OBJECTID, @linkedObjectId OBJECTID,
            @distributeId OBJECTID
    -- define local variables:
    DECLARE @oid                    OBJECTID
    DECLARE @rights                 INT
    DECLARE @activities             NAME
    
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @ALREADY_EXISTS = 21
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- convertions (object id string) - all input object dis must be converted:
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT
    EXEC p_stringToByte @distributeId_s, @distributeId   OUTPUT
    -- initialize local variables:
    SELECT  @oid = 0x0000000000000000, @containerId = 0x0000000000000000


    -- body:
    BEGIN TRANSACTION
        -- set the rights of the outboxcontainer:
        SELECT  @containerId = outBox
        FROM    ibs_Workspace
        WHERE   userId = @userId

        -- compute the right to be added or set:
        SELECT  @rights = SUM (id)
        FROM    ibs_Operation
        WHERE   name IN (N'new', N'read', N'view', N'change', N'delete',
                N'viewRights', N'setRights', N'createLink', N'distribute', 
                N'addElem', N'delElem', N'viewElems')
        -- set new rights:
        EXEC @retValue = p_rights$set @containerId, @userId, @rights,1

        -- convert containerId to containerId_s:
        EXEC p_byteToString @containerId, @containerId_s OUTPUT

        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, 
                            @name, @containerId_s, @containerKind, 
                            @isLink, @linkedObjectId_s, @description, 
                            @oid_s OUTPUT, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- create object type specific data:
 	        INSERT INTO ibs_SentObject_01 
 	                (oid, distributeId, distributeTVersionId, 
 	                distributeTypeName, distributeName, distributeIcon,
 	                activities, deleted)
	        SELECT  @oid, @distributeId, tVersionId, typeName, name, icon,
	                @activities, @deleted
	        FROM    ibs_Object
	        WHERE   oid = @distributeId

            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT @retValue = @NOT_OK -- set return value
            END -- if no row affected
            ELSE                        -- insertion performed properly
            BEGIN
                IF (@freeze  = 1)       -- freeze the distributed object?
                BEGIN
                    -- start set rights of the distributed object
                    -- compute the right you want to add:
                    SELECT  @rights = @senderRights
                    -- set the rights of the distributed Object:
                    EXEC @retValue =
                        p_Rights$setRights @distributeId, @userId, @rights, 1        
                END -- if freeze the distributed object
            END -- else insertion performed properly
        END -- if object created successfully
    COMMIT TRANSACTION
    
    -- return the state value:
    RETURN  @retValue
GO
-- p_SentObject_01$create


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
EXEC p_dropProc N'p_SentObject_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_SentObject_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL,

    @distributeId_s OBJECTIDSTRING,
    @activities     NAME,
    @deleted        BOOL
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

    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID
    DECLARE @distributeId   OBJECTID
    EXEC p_stringToByte @oid_s, @oid                    OUTPUT
    EXEC p_stringToByte @distributeId_s, @distributeId  OUTPUT

    DECLARE @distributeTVersionId       TVERSIONID
    DECLARE @distributeTypeName         NAME
    DECLARE @distributeName             NAME
    DECLARE @distributeIcon             NAME


    BEGIN TRANSACTION

        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name, 
                @validUntil, @description, @showInNews

        -- read out from distributed Object in SentObjectTable
    	SELECT  @distributeTVersionId = tVersionId,
                @distributeName = name, @distributeIcon = icon
        FROM    ibs_Object
        WHERE   oid = @distributeId

	    UPDATE ibs_SentObject_01
	    SET     distributeId  = @distributeId,
                distributeTVersionId = @distributeTVersionId,
                distributeName  =  @distributeName,
                distributeIcon = @distributeIcon, 
                activities = @activities,
                deleted =  @deleted
	    WHERE oid=@oid

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_SentObject_01$change


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
EXEC p_dropProc N'p_SentObject_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_SentObject_01$retrieve
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

    @distributeId_s         OBJECTIDSTRING  OUTPUT,
    @distributeTVersionId   TVERSIONID      OUTPUT,
    @distributeTypeName     NAME            OUTPUT,
    @distributeName         NAME            OUTPUT,
    @distributeIcon         NAME            OUTPUT,
    @activities             NAME            OUTPUT,
    @deleted                BOOL            OUTPUT,
    @recipientContainerId_s OBJECTIDSTRING  OUTPUT
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

    DECLARE @recipientContainerId       OBJECTID  -- used for 'Reiter' definition
    DECLARE @distributeId               OBJECTID  -- used for 'Reiter' definition
    SELECT  @recipientContainerId = 0x0000000000000000
    SELECT  @distributeId = 0x0000000000000000

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
                @distributeId =  distributeId,
                @distributeTVersionId = distributeTVersionId,
                @distributeTypeName = distributeTypeName,
                @distributeName  =  distributeName,
                @distributeIcon = distributeIcon,
                @activities = activities,
                @deleted = deleted

	        FROM ibs_SentObject_01
	        WHERE oid = @oid

            SELECT @recipientContainerId = oid
            FROM ibs_object
            WHERE containerId = @oid AND (tVersionId = 16849665 ) -- recipientlist

 
	    ---------------end of conversion--------------------------------------
        -- Fuer die Anzeige der gelesenen und ungelesenen Objekte ist es notwendig, dass 
        -- der p_xxx$retrieve Methoden das folgende Statement einbaut:
        -- set the readdate in the related recipient_01 object of the actual user

            UPDATE ibs_recipient_01 
            SET readDate = getdate () 
            WHERE sentObjectId = @oid 
            AND readDate IS NULL
            AND recipientID = (SELECT oid 
                            FROM ibs_user WHERE id = @userId)


            -- convert sendedObjectId to output
            EXEC    p_byteToString @distributeId, @distributeId_s OUTPUT

            -- convert recipientContainerId to output
            EXEC    p_byteToString @recipientContainerId, 
                    @recipientContainerId_s OUTPUT

        END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_SentObject_01$retrieve


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
EXEC p_dropProc N'p_Sentobject_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Sentobject_01$delete
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
    -- define used variables
    DECLARE @containerId OBJECTID

    -- START
    BEGIN TRANSACTION
        -- all references and the object itself are deleted (plus rights)
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -- delete all values of object
            DELETE  ibs_sentobject_01 
            WHERE   oid = @oid
        END	

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Sentobject_01$delete
