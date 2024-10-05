/******************************************************************************
 * All stored procedures regarding the Participant_01 object. <BR>
 *
 * @version     $Id: Participant_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Horst Pichler   (HP)  98xxxx
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
IF EXISTS (SELECT * FROM sysobjects WHERE id = object_id('#CONFVAR.ibsbase.dbOwner#.p_Participant_01$create') AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Participant_01$create
GO

-- create the new procedure:
CREATE PROCEDURE p_Participant_01$create
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
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID
    DECLARE @addressoid OBJECTIDSTRING

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2 -- return values
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    ---------------------------------------------------------------------------
    -- START
    
    -- check if number of max. allowed participants exceeded
    -- declare stored proc parameters
    DECLARE @announced BOOL, @free INT, @deadline DATETIME, @startDate DATETIME
    -- execute stored procedure 
    EXEC @retValue 
        = p_ParticipantCont_01$chkPart @containerId_s, @userId, 
            @announced OUTPUT, @free OUTPUT, @deadline OUTPUT, @startDate OUTPUT
    -- if no more free places, return insuff. rights            
    IF @free <= 0
    BEGIN
        RETURN @INSUFFICIENT_RIGHTS
    END

    BEGIN TRANSACTION

        DECLARE @oid    OBJECTID
        DECLARE @fullname NAME

        -- get full user name out of db
        SELECT  @fullname = fullname
        FROM ibs_User
        WHERE id = @userId

        -- store participant object
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT

	    IF @retValue = @ALL_RIGHT
           BEGIN
                -- insert current user as participant into participant table
                INSERT INTO m2_Participant_01 (oid, announcerId, announcerName)
                VALUES (@oid, @userId, @fullname)
           END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue

GO

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 * This procedure also creates or deletes a participant container (is sub-
 * object which holds all participating users).
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 *
 * @param   @announcerId        Announcers user id
 * @param   @announcerName      Announcers full name
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
IF EXISTS (SELECT * FROM sysobjects WHERE id = object_id('#CONFVAR.ibsbase.dbOwner#.p_Participant_01$change') and sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Participant_01$change
GO

-- create the new procedure:
CREATE PROCEDURE p_Participant_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,     -- objects id as a string
    @userId         USERID,             -- users id
    @op             INT,                -- operation
    @name           NAME,               -- name of object
    @validUntil     DATETIME,           -- valid in system
    @description    DESCRIPTION,        -- textual description
    @showInNews     BOOL   ,            -- flag if show in news
	---- object specific attributes:
    @announcerId    int,                -- announcers id
    @announcerName  NVARCHAR (255)      -- announcers full name
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2 -- return values
    -- define return values
    DECLARE @retValue INT
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    -- get participant container id; convert to string
    DECLARE @partContId OBJECTID, @partContId_s OBJECTIDSTRING
    
    SELECT  @partContId = containerId
    FROM    ibs_Object
    WHERE   oid = @oid
    
    EXEC    p_byteToString @partContId, @partContId_s OUTPUT    

    -- check if number of max. allowed participants exceeded
    -- declare stored proc parameters
    -- DECLARE @announced BOOL, @free INT, @deadline DATETIME, @startDate DATETIME
    -- execute stored procedure 
    -- EXEC @retValue 
    --  = p_ParticipantCont_01$chkPart @partContId_s, @userId, 
    --      @announced OUTPUT, @free OUTPUT, @deadline OUTPUT, @startDate OUTPUT
    -- if no more free places, return insuff. rights            
    -- IF @free <= 0
    -- BEGIN
    --  RETURN @INSUFFICIENT_RIGHTS
    -- END

    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews

        -- no further changing necessary
        -- informations in m2_Participant_01 are only about announcer
        -- announcer was already set in create-procedure and
        -- is not changeable
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
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
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
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
 * @param   @announcerId        Announcers id
 * @param   @announcerName      Announcers full name
 *
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
IF EXISTS (SELECT * FROM sysobjects WHERE id = object_id('#CONFVAR.ibsbase.dbOwner#.p_Participant_01$retrieve') AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Participant_01$retrieve
GO

-- create the new procedure:
CREATE PROCEDURE p_Participant_01$retrieve
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,             -- objects id as string
    @userId         USERID,                     -- users id
    @op             INT,                        -- operation
    -- output parameters
    @state          STATE           OUTPUT,     -- state of object in db
    @tVersionId     TVERSIONID      OUTPUT,     -- type version number
    @typeName       NAME            OUTPUT,     -- type name (Participant)
    @name           NAME            OUTPUT,     -- name of object
    @containerId    OBJECTID        OUTPUT,     -- oid of objects container
    @containerName  NAME            OUTPUT,     -- name of the container
    @containerKind  INT             OUTPUT,     -- 'part-of' or 'normal'    
    @isLink         BOOL            OUTPUT,     -- is object a link
    @linkedObjectId OBJECTID        OUTPUT,     -- oid of linked object
    @owner          USERID          OUTPUT,     -- owners userid
    @ownerName      NAME            OUTPUT,     -- owners name
    @creationDate   DATETIME        OUTPUT,     -- date of object creation
    @creator        USERID          OUTPUT,     -- creators userid
    @creatorName    NAME            OUTPUT,     -- creators name
    @lastChanged    DATETIME        OUTPUT,     -- date&time of last changing
    @changer        USERID          OUTPUT,     -- changers userid
    @changerName    NAME            OUTPUT,     -- changers name
    @validUntil     DATETIME        OUTPUT,     -- objects validity in system
    @description    DESCRIPTION     OUTPUT,     -- textual description
    @showInNews     BOOL            OUTPUT,     -- flag if show in news
    @checkedOut     BOOL            OUTPUT,
    @checkOutDate   DATETIME        OUTPUT,
    @checkOutUser   USERID          OUTPUT,
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT,
    -- object specific parameters
    @announcerId    int             OUTPUT,     -- announcers user id
    @announcerName  NVARCHAR (255)  OUTPUT      -- announcers full name
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT
    -- set constants
    SELECT  @ALL_RIGHT = 1
    -- define return values
    DECLARE @retValue INT
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION

        EXEC @retValue = p_Object$performRetrieve
            @oid_s, @userId, @op,
            @state OUTPUT, @tVersionId OUTPUT, @typeName OUTPUT, @name OUTPUT,
            @containerId OUTPUT, @containerName OUTPUT, @containerKind OUTPUT,
            @isLink OUTPUT, @linkedObjectId OUTPUT, @owner OUTPUT, @ownerName
            OUTPUT, @creationDate OUTPUT, @creator OUTPUT, @creatorName OUTPUT,
            @lastChanged OUTPUT, @changer OUTPUT, @changerName OUTPUT,
            @validUntil OUTPUT, @description OUTPUT, @showInNews OUTPUT,
            @checkedOut OUTPUT, @checkOutDate OUTPUT, 
            @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
            @oid OUTPUT

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN

            -- get object specific values of table Participant_01
            SELECT @announcerId = announcerId,
                   @announcerName = announcerName
            FROM m2_Participant_01
            WHERE oid = @oid
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
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
IF EXISTS (SELECT * FROM sysobjects WHERE id = object_id('#CONFVAR.ibsbase.dbOwner#.p_Participant_01$delete') and sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Participant_01$delete
GO

-- create the new procedure:
CREATE PROCEDURE p_Participant_01$delete
(
    @oid_s          OBJECTIDSTRING,     -- objects id as string
    @userId         USERID,             -- users id
    @op             INT                 -- operation
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT
    -- set constants
    SELECT  @ALL_RIGHT = 1
    -- define return values
    DECLARE @retValue INT
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- participants container
    DECLARE @partContId OBJECTID


    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION

        -- perform deletion of object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

    COMMIT TRANSACTION
    -- return the state value
    RETURN  @retValue

GO
