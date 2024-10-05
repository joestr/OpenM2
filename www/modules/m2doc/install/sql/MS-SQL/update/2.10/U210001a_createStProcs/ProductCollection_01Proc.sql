/******************************************************************************
 * Creates a ProductCollection Object.
 *
 * @version     $Id: ProductCollection_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Bernhard Walter   (BW)  981226
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 ******************************************************************************
 */
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$create') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$create

GO

-- create the new procedure:
CREATE PROCEDURE p_ProductCollect_01$create
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
 	        INSERT INTO m2_ProductCollection_01 (oid, validFrom)
	        VALUES (@oid, GETDATE())
        END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue

GO

/******************************************************************************
 *
 * Changes a ProductCollection Object.
 *
 * @version     1.00.0000, 26.12.1998
 *
 * @author      Bernhard Walter   (BW)  981226
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$change') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$change
GO

-- create the new procedure:
CREATE PROCEDURE p_ProductCollect_01$change
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
    ,@cost           MONEY          
    ,@costCurrency   NVARCHAR(5)     
    ,@totalQuantity  INT          
    ,@validFrom      DATETIME   
    ,@categoryOidX_s OBJECTIDSTRING
    ,@categoryOidY_s OBJECTIDSTRING
    ,@nrCodes        INT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID,
            @oldImage       NAME,
            @categoryOidX   OBJECTID,
            @categoryOidY   OBJECTID

    EXEC p_stringToByte @oid_s, @oid OUTPUT
    EXEC p_stringToByte @categoryOidX_s, @categoryOidX OUTPUT
    EXEC p_stringToByte @categoryOidY_s, @categoryOidY OUTPUT
 
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
            UPDATE  m2_ProductCollection_01
	        SET     cost = @cost,
	                costCurrency = @costCurrency,
	                validFrom = @validFrom,
	                totalQuantity = @totalQuantity,
	                categoryOidX = @categoryOidX,
	                categoryOidY = @categoryOidY,
	                nrCodes = @nrCodes
	        WHERE   oid=@oid
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO

/******************************************************************************
 *
 * Retrieves a ProductCollection Object.
 *
 * @version     1.00.0000, 26.12.1998
 *
 * @author      Bernhard Walter   (BW)  981226
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

if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$retrieve') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$retrieve
GO

-- create the new procedure:
CREATE PROCEDURE p_ProductCollect_01$retrieve
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
    -----specific outputdata of ProductBrand--------------
    ,@productOid     OBJECTID        OUTPUT
    ,@nrCodes        INT             OUTPUT
    ,@cost           MONEY           OUTPUT 
    ,@costCurrency   NVARCHAR(5)     OUTPUT   
    ,@totalQuantity  INT             OUTPUT
    ,@validFrom      DATETIME        OUTPUT
    ,@categoryOidX   OBJECTID        OUTPUT
    ,@categoryOidY   OBJECTID        OUTPUT
    ,@nrCodes2       INT             OUTPUT
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
            @validUntil OUTPUT, @description OUTPUT,  @showInNews OUTPUT,
            @checkedOut OUTPUT, @checkOutDate OUTPUT, 
            @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
            @oid OUTPUT

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -----------------------specific table outread---------------------
	        SELECT  @productOid = containerId
    	    FROM    ibs_Object
	        WHERE   oid =  @containerId
	        
	        -- select the number of codes
	        SELECT      @nrCodes = count(*)
	        FROM        m2_ProfileCategory_01 pc
	        JOIN        m2_Product_01 p
	        ON          pc.productProfileOid = p.productProfileOid
	        WHERE       p.oid = @productOid
	        
	        -- select values from collection table
	        SELECT      @cost = cost,
	                    @costCurrency = costCurrency,
                        @totalQuantity = totalQuantity,
	                    @validFrom = validFrom,
	                    @categoryOidX = categoryOidX,
	                    @categoryOidY = categoryOidY,
	                    @nrCodes2 = nrCodes
	        FROM        m2_ProductCollection_01
	        WHERE       oid = @oid

            SELECT DISTINCT @totalQuantity = sum(quantity)
            FROM    v_ProductCollection$content
	        WHERE   collectionOid = @oid
            GROUP BY categoryname
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO

/******************************************************************************
 *
 * Creates a new quantity entry in a product collection.
 *
 * @version     1.00.0000, 15.01.1999
 *
 * @author      Bernhard Walter   (BW)  990115
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

IF EXISTS (SELECT * FROM sysobjects WHERE type = N'P' AND name = N'p_ProductCollect_01$createQty')
	BEGIN
		PRINT 'Dropping Procedure p_ProductCollect_01$createQty'
		DROP  Procedure  p_ProductCollect_01$createQty
	END

GO
PRINT 'Creating Procedure p_ProductCollect_01$createQty'
GO
CREATE Procedure p_ProductCollect_01$createQty
(
    -- input parameters:
    @collectionOid_s		OBJECTIDSTRING,
    @quantity			INT,
    -- output parameters:
    @id					ID OUTPUT
)
AS
    DECLARE @collectionOid OBJECTID

	-- select a new Id
	SELECT  @id = COALESCE (MAX (id) + 1, 1)
    FROM    m2_ProductCollectionQty_01
    
    -- insert a new tuple
    EXEC p_StringToByte @collectionOid_s, @collectionOid OUTPUT
    INSERT INTO m2_ProductCollectionQty_01 (id, collectionOid, quantity)
    VALUES	(@id, @collectionOid, @quantity)
GO

/******************************************************************************
 *
 * Creates a new value entry in a product collection.
 *
 * @version     1.00.0000, 15.01.1999
 *
 * @author      Bernhard Walter   (BW)  990115
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

IF EXISTS (SELECT * FROM sysobjects WHERE type = N'P' AND name = N'p_ProductCollect_01$createVal')
	BEGIN
		PRINT 'Dropping Procedure p_ProductCollect_01$createVal'
		DROP  Procedure  p_ProductCollect_01$createVal
	END

GO
PRINT 'Creating Procedure p_ProductCollect_01$createVal'
GO
CREATE Procedure p_ProductCollect_01$createVal
(
    -- input parameters:
    @id					ID,   
    @categoryOid_s	    OBJECTIDSTRING,
    @value  			NVARCHAR(255)
    -- output parameters:
)
AS
    DECLARE @categoryOid OBJECTID

    -- insert a new tuple
    EXEC p_StringToByte @categoryOid_s, @categoryOid OUTPUT
    INSERT INTO m2_ProductCollectionValue_01 (id, categoryOid, value)
    VALUES	(@id, @categoryOid, @value)
GO

/******************************************************************************
 *
 * Deletes a ProductCollection Object.
 *
 * @version     1.00.0000, 15.01.1999
 *
 * @author      Bernhard Walter   (BW)  980115
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */
 
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$delete') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$delete
GO

CREATE PROCEDURE p_ProductCollect_01$delete
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
    DECLARE @@id		ID


    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION

        -- perform deletion of object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
                -- define cursor:
                DECLARE Id_Cursor CURSOR FOR 
                    SELECT	id 
                    FROM	m2_ProductCollectionQty_01	 
                    WHERE	collectionOid = @oid

                -- open the cursor:
                OPEN    Id_Cursor

                -- get the first user:
                FETCH NEXT FROM Id_Cursor INTO @@id

                -- loop through all found users:

                WHILE (@@FETCH_STATUS <> -1)            -- another user found?
                BEGIN
                    -- Da @@FETCH_STATUS einen der drei Werte -2, -1 oder 0
                    -- besitzen kann, müssen alle drei Fälle geprüft werden.
                    -- In diesem Fall wird eine Tabelle, wenn sie während der
                    -- Ausführung der Prozedur gelöscht wurde, übersprungen.
                    -- Ein erfolgreicher Abruf (0) veranlaßt die Ausführung
                    -- von DBCC innerhalb der BEGIN..END-Schleife.
                    IF (@@FETCH_STATUS <> -2)
                    BEGIN
						-- delete entries in the data tables
						DELETE  m2_ProductCollectionQty_01
						WHERE   id = @@id
						
						DELETE	m2_ProductCollectionValue_01
						WHERE   id = @@id
                    END -- if
                    -- get next user:
                    FETCH NEXT FROM Id_Cursor INTO @@id
                END -- while another user found

                DEALLOCATE Id_Cursor
        END

    COMMIT TRANSACTION
    -- return the state value
    RETURN  @retValue

GO

/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
IF EXISTS ( SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$BOCopy')
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_ProductCollect_01$BOCopy
GO

-- create the new procedure:
CREATE PROCEDURE p_ProductCollect_01$BOCopy
(
    -- common input parameters:
    @oid            OBJECTID,
    @userId         USERID,
    @newOid         OBJECTID
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK

    -- not implemented yet
    -- make an insert for all type specific tables:
    INSERT  INTO m2_ProductCollection_01
            (oid, cost, costCurrency, totalQuantity, validFrom, categoryOidX, categoryOidY, nrCodes
            )
    SELECT  @newOid,  cost, costCurrency, totalQuantity, 
            validFrom, categoryOidX, categoryOidY, nrCodes
    FROM    m2_ProductCollection_01
    WHERE   oid = @oid
    

    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @retValue
GO 
-- p_ProductCollect_01$BOCopy
