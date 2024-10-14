/******************************************************************************
 * Creates a ProductGroupProfile Object.
 *
 * @version     $Id: ProductGroupProfile_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Bernhard Walter   (BW)  981221
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
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
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$create') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$create

GO

-- create the new procedure:
CREATE PROCEDURE p_ProductGrpProfile_01$create
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

    BEGIN TRANSACTION

        DECLARE @oid    OBJECTID
        
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT

	    IF @retValue = @ALL_RIGHT
        BEGIN
            -- Insert the other values
 	        INSERT INTO m2_ProductGroupProfile_01 (oid, thumbAsImage)
	        VALUES (@oid, 0)
        END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue

GO

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         flag if object should be shown in newscontainer
 *
 * @param   @prop1              Description of the first type specific property.
 * @param   @prop2              Description of the second type specific
 *                              property.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$change') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$change
GO
-- create the new procedure:
CREATE PROCEDURE p_ProductGrpProfile_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING
    ,@userId         USERID
    ,@op             INT
    ,@name           NAME
    ,@validUntil     DATETIME
    ,@description    DESCRIPTION
    ,@showInNews    BOOL
    ---- attributes of object attachment ---------------
    ,@code    NAME
    ,@season         NAME
    ,@thumbnail      NAME
    ,@thumbAsImage   BOOL   
    ,@image          NAME
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID,
            @oldThumb       NAME,
            @oldImage       NAME

    EXEC p_stringToByte @oid_s, @oid OUTPUT
 
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

        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            SELECT  @oldThumb = thumbnail,
                    @oldImage = image
            FROM    m2_ProductGroupProfile_01
            WHERE   oid = @oid

            IF @thumbNail IS NULL
                SELECT @thumbnail = @oldThumb
            IF @image IS NULL
                SELECT @image = @oldImage
             
            -- update other values
            UPDATE  m2_ProductGroupProfile_01
	        SET     code = @code
                    ,season = @season
                    ,thumbnail = @thumbnail
                    ,thumbAsImage = @thumbAsImage
                    ,image = @image
	        WHERE   oid=@oid
        END -- if operation properly performed

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
 * @param   @showInNews         flag if object should be shown in newscontainer
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
 * @param   @prop1              Description of the first property.
 * @param   @prop2              Description of the second property.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$retrv') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$retrv
GO

-- create the new procedure:
CREATE PROCEDURE p_ProductGrpProfile_01$retrv
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING
    ,@userId         USERID
    ,@op             INT

    -- output parameters
    ,@state          STATE           OUTPUT
    ,@tVersionId     TVERSIONID      OUTPUT
    ,@typeName       NAME            OUTPUT
    ,@name           NAME            OUTPUT
    ,@containerId    OBJECTID        OUTPUT
    ,@containerName  NAME            OUTPUT
    ,@containerKind  INT            OUTPUT
    ,@isLink         BOOL            OUTPUT
    ,@linkedObjectId OBJECTID        OUTPUT
    ,@owner          USERID          OUTPUT
    ,@ownerName      NAME            OUTPUT --name of the Creat
    ,@creationDate   DATETIME        OUTPUT 
    ,@creator        USERID          OUTPUT
    ,@creatorName    NAME            OUTPUT --name of the Creator
    ,@lastChanged    DATETIME        OUTPUT
    ,@changer        USERID          OUTPUT
    ,@changerName    NAME            OUTPUT --name of te Changer
    ,@validUntil     DATETIME        OUTPUT
    ,@description    DESCRIPTION     OUTPUT  
    ,@showInNews     BOOL            OUTPUT
    ,@checkedOut     BOOL            OUTPUT
    ,@checkOutDate   DATETIME        OUTPUT
    ,@checkOutUser   USERID          OUTPUT
    ,@checkOutUserOid OBJECTID       OUTPUT
    ,@checkOutUserName NAME          OUTPUT
    -----specific outputdata of ProductGroup--------------
    ,@code    NAME           OUTPUT
    ,@season         NAME           OUTPUT
    ,@thumbnail      NAME           OUTPUT
    ,@thumbAsImage   BOOL           OUTPUT
    ,@image          NAME           OUTPUT
-------------------------------------------------------------------------------

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
            -----------------------specific table outread---------------------
	        SELECT
                @code = code
                ,@season = season
                ,@thumbnail = thumbnail
                ,@thumbAsImage = thumbAsImage
                ,@image = image
    	    FROM m2_ProductGroupProfile_01
	        WHERE oid =  @oid
        END -- if operation properly performed

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
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$delete') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGrpProfile_01$delete
GO

CREATE PROCEDURE p_ProductGrpProfile_01$delete
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

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -- delete object itself
            DELETE  m2_ProductGroupProfile_01 
            WHERE   oid = @oid
        END

    COMMIT TRANSACTION
    -- return the state value
    RETURN  @retValue

GO
