/******************************************************************************
 * Creates a ProductGroup_01 Object.
 *
 * @version     $Id: ProductGroup_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Bernhard Walter   (BW)  981224
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 ******************************************************************************
 */

if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$create') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$create
GO

-- create the new procedure:
CREATE PROCEDURE p_ProductGroup_01$create

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
 	        INSERT INTO m2_ProductGroup_01 (oid, productGroupProfileOid)
	        VALUES (@oid, 0x0000000000000000)
        END    

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue

GO

/******************************************************************************
 *
 * ??Changes a ProductGroup_01 Object.
 *
 * @version     1.00.0000, 24.12.1998
 *
 * @author      Bernhard Walter   (BW)  981224
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */
 
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$change') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$change
GO

-- create the new procedure:
CREATE PROCEDURE p_ProductGroup_01$change
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
    ,@productGroupProfileOid_s       OBJECTIDSTRING
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid OBJECTID, @productGroupProfileOid OBJECTID
    EXEC    p_stringToByte @productGroupProfileOid_s, @productGroupProfileOid OUTPUT
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
            -- update other values
            UPDATE  m2_ProductGroup_01
	        SET     productGroupProfileOid = @productGroupProfileOid
	        WHERE	oid=@oid
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO


/******************************************************************************
 *
 * ??Retrieves a ProductGroup_01 Object.
 *
 * @version     1.00.0000, 24.12.1998
 *
 * @author      Bernhard Walter   (BW)  981224
 *
 * @output parameters:
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
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */

if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$retrieve') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$retrieve
GO

-- create the new procedure:
CREATE PROCEDURE p_ProductGroup_01$retrieve
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
    ,@containerKind  INT             OUTPUT
    ,@isLink         BOOL            OUTPUT
    ,@linkedObjectId OBJECTID        OUTPUT
    ,@owner          USERID          OUTPUT
    ,@ownerName      NAME            OUTPUT --name of the Creat
    ,@lastChanged    DATETIME        OUTPUT
    ,@changer        USERID          OUTPUT
    ,@changerName    NAME            OUTPUT --name of te Changer
    ,@creationDate   DATETIME        OUTPUT 
    ,@creator        USERID          OUTPUT
    ,@creatorName    NAME            OUTPUT --name of the Creator
    ,@validUntil     DATETIME        OUTPUT
    ,@description    DESCRIPTION     OUTPUT  
    ,@showInNews     BOOL            OUTPUT
    ,@checkedOut     BOOL            OUTPUT
    ,@checkOutDate   DATETIME        OUTPUT
    ,@checkOutUser   USERID          OUTPUT
    ,@checkOutUserOid OBJECTID       OUTPUT
    ,@checkOutUserName NAME          OUTPUT
    -- object specific attributes
    ,@productGroupProfileOid_s       OBJECTIDSTRING       OUTPUT
    ,@productGroupProfile   NAME           OUTPUT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID, @productGroupProfileOid OBJECTID
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
            @validUntil OUTPUT, @description OUTPUT,  @showInNews OUTPUT,
            @checkedOut OUTPUT, @checkOutDate OUTPUT, 
            @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
            @oid OUTPUT
	    

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -- get object specific values
            -- oid of referring ProductGroup
	        SELECT  @productGroupProfileOid = pgp.oid, @productGroupProfile = pgp.name
    	    FROM	m2_ProductGroup_01 pg
    	    JOIN	ibs_Object pgp
    	    ON      pg.productGroupProfileOid = pgp.oid
    	    WHERE   pg.oid = @oid

            EXEC p_byteToString @productGroupProfileOid, @productGroupProfileOid_s OUTPUT
        
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO

/******************************************************************************
 *
 * Deletes a ProductGroup_01 Object.
 *
 * @version     1.00.0000, 24.12.1998
 *
 * @author      Bernhard Walter   (BW)  980524
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */

if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$delete') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductGroup_01$delete
GO

CREATE PROCEDURE p_ProductGroup_01$delete
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
            -- delete special object
            DELETE  m2_ProductGroup_01 
            WHERE   oid = @oid
        END
    COMMIT TRANSACTION
    -- return the state value
    RETURN  @retValue
GO
