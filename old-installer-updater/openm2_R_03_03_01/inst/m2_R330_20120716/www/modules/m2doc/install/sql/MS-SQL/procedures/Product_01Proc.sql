/******************************************************************************
 * All stored procedures regarding the Product_01 table. <BR>
 *
 * @version     $Id: Product_01Proc.sql,v 1.15 2009/12/02 18:35:04 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW)  980915
 ******************************************************************************
 */


/******************************************************************************
 * Create a new object (incl. rights check). <BR>
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
EXEC p_dropProc N'p_Product_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Product_01$create
(
    -- common input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- common output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- define local variables:
    DECLARE @oid OBJECTID
    -- initialize local variables:
    SELECT  @oid = 0x0000000000000000

    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId,
                            @name, @containerId_s, @containerKind,
                            @isLink, @linkedObjectId_s, @description,
                            @oid_s OUTPUT, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- create object type specific data:
            INSERT INTO m2_Product_01
                    (oid, productNo, ean, availableFrom,
                     unitOfQty, packingUnit, thumbAsImage, thumbNail,
                     image, stock, created, productDescription)
            VALUES  (@oid, N'', N'', getDate(),
                     1, N'Stk.', 0, null,
                     null, N'', 0, N'')

            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT  @retValue = @NOT_OK -- set return value
            END -- if no row affected
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Product_01$create


/******************************************************************************
 * Changes the attributes of an existing product (incl. rights check). <BR>
 * Currently all property lists are transferred via this procedure for
 * performance reasons.
 * The maximum is currently 6.
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
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Product_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Product_01$change
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL,
    -- type-specific input parameters:
    @productNo      NAME,
    @ean            NAME,
    @availableFrom  DATETIME,
    @unitOfQty      INT,
    @packingUnit    NAME,
    @thumbAsImage   BOOL,
    @thumbnail        NAME,
    @image          NAME,
    @path           NVARCHAR(20),
    @stock          NAME,
    --
    @productDialogStep INT,
    --1
    @productProfileOid_s OBJECTIDSTRING,
    @brandNameOid_s OBJECTIDSTRING,
    @hasAssortment  INT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- define local variables:
    DECLARE @oid OBJECTID
    -- initialize local variables:
    DECLARE @oldImage NAME, @oldThumb NAME,
            @productProfileOid OBJECTID,
            @brandNameOid OBJECTID,
            @notSupported INT


    SELECT  @notSupported = 0

    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:

        -- don't set the description when coming from the first
        -- dialog
        EXEC    p_StringToByte @oid_s, @oid OUTPUT
        IF (@productDialogStep <> 3)
        BEGIN
            SELECT  @description = description
            FROM    ibs_Object
            WHERE   oid = @oid
        END

        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update object type specific data:
            -- update other values
            IF (@productDialogStep = 1)
            BEGIN
                -- convert the oids of the property lists
                EXEC p_StringToByte @productProfileOid_s, @productProfileOid OUTPUT

                INSERT INTO m2_ProductCodeValues_01 (productOid, categoryOid, predefinedCodeOid)
                SELECT      @oid, categoryOid, 0x0000000000000000
                FROM        m2_ProfileCategory_01
                WHERE       productProfileOid = @productProfileOid

                -- dont't use assortment if there are no codes
                -- or more than two
                IF ((@@ROWCOUNT = 0) OR (@@ROWCOUNT > 2))
                    SELECT  @notSupported = 1

                -- delete not needed tabs:
                IF (@hasAssortment = 1) -- product has assortments?
                BEGIN
                    -- delete tab prices:
                    EXEC p_Object$deleteTab @userId, @op, @oid, N'Prices'
                END -- if product has assortments
                ELSE                    -- product has prices
                BEGIN
                    -- delete tab assortments:
                    EXEC p_Object$deleteTab @userId, @op, @oid, N'Assortments'
                END -- else product has prices

                -- update the product table
                UPDATE  m2_Product_01
                SET     productProfileOid =  @productProfileOid,
                        hasAssortment = @hasAssortment
                WHERE   oid = @oid

                -- set state to st_created for next changeform (productDialogStep 2 or 3)
                EXEC p_Object$changeState @oid_s, @userId, @op, 4
            END
            ELSE IF (@productDialogStep = 2)
            BEGIN
                -- set state to st_created for next changeform (productDialogStep 3)
                EXEC p_Object$changeState @oid_s, @userId, @op, 4
            END
            ELSE IF (@productDialogStep = 3)
            BEGIN
                -- don't change the images if null
                SELECT  @oldImage = image,
                        @oldThumb = thumbnail
                FROM    m2_Product_01
                WHERE   oid = @oid

                IF @image IS NULL
                    SELECT @image = @oldImage
                IF @thumbnail IS NULL
                    SELECT @thumbnail = @oldThumb

                EXEC p_StringToByte @brandNameOid_s, @brandNameOid OUTPUT

                -- change the property lists
                UPDATE  m2_Product_01
                SET     productNo = @productNo
                        ,ean = @ean
                        ,availableFrom = @availableFrom
                        ,unitOfQty = @unitOfQty
                        ,packingUnit = @packingUnit
                        ,thumbAsImage = @thumbAsImage
                        ,thumbnail = @thumbnail
                        ,image = @image
                        ,path = @path
                        ,stock = @stock
                        ,created = 1
                        ,brandNameOid = @brandNameOid
                WHERE   oid = @oid
            END

            -- check if change was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Product_01$change


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
EXEC p_dropProc N'p_Product_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Product_01$retrieve
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- common output parameters:
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
    @checkOutUserOid  OBJECTID      OUTPUT,
    @checkOutUserName NAME          OUTPUT,
    -- type-specific output attributes:
    @productNo      NAME            OUTPUT,
    @ean            NAME            OUTPUT,
    @availableFrom  DATETIME        OUTPUT,
    @unitOfQty      INT             OUTPUT,
    @packingUnit    NAME            OUTPUT,
    @thumbAsImage   BOOL            OUTPUT,
    @thumbnail      NAME            OUTPUT,
    @image          NAME            OUTPUT,
    @path           NVARCHAR(20)    OUTPUT,
    @stock          NAME            OUTPUT,
    @priceCont_s    OBJECTIDSTRING  OUTPUT,
    -- 1
    @productProfileOid_s OBJECTIDSTRING OUTPUT,
    @hasAssortment  INT             OUTPUT,
    @created        INT             OUTPUT,
    @brandName      NAME            OUTPUT,
    @brandNameOid_s OBJECTIDSTRING  OUTPUT,
    @brandImage     NAME            OUTPUT,
    @collectionContainerOid_s OBJECTIDSTRING OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- define local variables:
    DECLARE @oid OBJECTID
    -- initialize local variables:
    DECLARE @priceCont OBJECTID
    DECLARE @pgThumb NAME, @pgImage NAME,
            @pgThumbAsImage BOOL,
            @productProfileOid OBJECTID,
            @brandNameOid OBJECTID

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
            -- get object specific data
            SELECT  @productNo = p.productNo
                    ,@ean = p.ean
                    ,@availableFrom = p.availableFrom
                    ,@unitOfQty = p.unitOfQty
                    ,@packingUnit = p.packingUnit
                    ,@thumbAsImage = p.thumbAsImage
                    ,@thumbnail = p.thumbnail
                    ,@image = p.image
                    ,@stock = p.stock
                    ,@hasAssortment = p.hasAssortment
                    ,@productProfileOid = p.productProfileOid
                    ,@created = p.created
                    ,@brandName = o.name
                    ,@brandNameOid = p.brandNameOid
                    ,@brandImage = bn.image
                    ,@path = p.path
            FROM    m2_Product_01 p
            LEFT OUTER JOIN    m2_ProductBrand_01 bn
            ON      bn.oid = p.brandNameOid
            LEFT OUTER JOIN    ibs_Object o
            ON      bn.oid = o.oid
            WHERE   p.oid =  @oid

            EXEC    p_ByteToString @brandNameOid, @brandNameOid_s OUTPUT

            -- get the images from the product group
            SELECT  @pgThumbAsImage = pg.thumbAsImage, @pgImage = pg.image, @pgThumb = pg.thumbnail
            FROM    m2_ProductGroupProfile_01 pg
            JOIN    m2_ProductGroup_01 cpg
            ON      cpg.productGroupProfileOid = pg.oid
            JOIN    ibs_Object o1
            ON      (o1.containerId = cpg.oid AND o1.oid = @oid)

            -- if there is no image for this product get it from product group
            IF @image IS NULL
                SELECT @image = @pgImage
            -- if there is no thumbnail for this product get it
            -- from product group
            IF ((@thumbnail IS NULL) AND (@thumbAsImage = 0))
            BEGIN
                IF @pgThumbAsImage = 1
                BEGIN
                    SELECT @thumbAsImage = 1
                END
                ELSE
                    SELECT @thumbnail = @pgThumb
            END
             -- get price container id
            SELECT  @priceCont = oid
            FROM    ibs_Object
            WHERE   tversionid  = 0x01012101  -- productSizeColorContainer
             AND    containerId = @oid        -- sub element of container

            EXEC p_ByteToString @priceCont, @priceCont_s OUTPUT

             -- get collection container id
            DECLARE @collectionContainerOid OBJECTID

            SELECT  @collectionContainerOid = oid
            FROM    ibs_Object
            WHERE   tversionid  = 0x01017701  -- productCollectionContainer
             AND    containerId = @oid        -- sub element of container

            EXEC p_ByteToString @collectionContainerOid , @collectionContainerOid_s OUTPUT

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Product_01$retrieve



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
EXEC p_dropProc N'p_Product_01$delete'
GO

/*
-- create the new procedure:
CREATE PROCEDURE p_Product_01$delete
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- define local variables:
    -- initialize local variables:

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op,
                @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  m2_Product_01
            WHERE   oid = @oid
            -- check if deletion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
G O
-- p_Product_01$delete
*/


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
-- delete existing procedure:
EXEC p_dropProc N'p_Product_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Product_01$BOCopy
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

    -- make an insert for all type specific tables:
    INSERT  INTO m2_Product_01
            (oid, productNo, ean, availableFrom, unitOfQty, packingUnit,
            thumbAsImage, thumbnail, image, stock,
            productProfileOid, hasAssortment, created, brandNameOid
            , path, productDescription
            )
    SELECT  @newOid, productNo, ean, availableFrom, unitOfQty, packingUnit,
            thumbAsImage, thumbnail, image, stock,
            productProfileOid, hasAssortment, created, brandNameOid
            , path, productDescription
    FROM    m2_Product_01
    WHERE   oid = @oid

    -- insert the code values of the product
    INSERT INTO m2_ProductCodeValues_01 (productOid, categoryOid, predefinedCodeOid, codeValues)
    SELECT      @newOid, categoryOid, predefinedCodeOid, codeValues
    FROM        m2_ProductCodeValues_01
    WHERE       productOid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @retValue
GO
-- p_Product_01$BOCopy



/******************************************************************************
 * Retrieve the price information
 *
 * @input parameters:
 * @param   @oid_s              Object id string
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns     A result set with the price information
 *              priceCurrency, price, cost, costCurrency, colors, sizes
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Product_01$retrievePrices'
GO

-- AJ 990425 deleted ... //////////////////////////////////////////////////////
/*
-- create the new procedure:
CREATE PROCEDURE p_Product_01$retrievePrices
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    DECLARE @priceCont OBJECTID, @oid OBJECTID

    -- convert the oid string to OBJECTID
    EXEC    p_StringToByte @oid_s, @oid OUTPUT

    -- get price-container id
    SELECT  @priceCont = oid
    FROM    ibs_Object
    WHERE   tversionid  = 0x01012101        -- PriceContainer
    AND     containerId = @oid              -- sub element of container

    SELECT  priceCurrency, price, cost, costCurrency, userValue1, userValue2, values1, values2
    FROM    v_Container$rights crr
    JOIN    m2_Price_01 p
    ON      crr.oid = p.oid
    WHERE   crr.containerId = @priceCont
    AND     (crr.rights & @op) > 0
-- AJ 990416 changed ... //////////////////////////////////////////////////////
    AND     crr.userId = @userId
-- ... AJ 990416 changed ... //////////////////////////////////////////////////
/ *
    AND     crr.uid = @userId
* /
-- ... AJ 990416 changed //////////////////////////////////////////////////////
G O

*/
-- ... AJ 990425 deleted //////////////////////////////////////////////////////

/******************************************************************************
 * Puts the product with the price and color information in shopping cart of
 * the user
 *
 * @input parameters:
 * @param   @oid_s              Object id string
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Product_01$createCartEntry'
GO

-- create the new procedure:
CREATE PROCEDURE p_Product_01$createCartEntry
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @tVersionId     TVERSIONID,
    @state          INT,                    -- not used anymore
    -- special input parameters
    @qty            INT,
    @unitOfQty      INT,
    @packingUnit    NAME,
    @productDescription DESCRIPTION,
    @price          MONEY,
    @priceCurrency  NAME
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    DECLARE @oid OBJECTID
    -- initialize local variables:
    SELECT  @oid = 0x0000000000000000
    DECLARE @priceCont OBJECTID
    DECLARE @shoppingCartOid OBJECTID
    DECLARE @shoppingCartOid_s OBJECTIDSTRING
    DECLARE @newOid_s OBJECTIDSTRING, @newOid OBJECTID, @catalogOid OBJECTID
    DECLARE @l_catalogName NAME, @l_orderRespOid  OBJECTID, @l_existsProductOid INT, @l_oldQty INT
    SELECT  @l_orderRespOid = 0x0000000000000000, @l_existsProductOid = 0, @l_oldQty = 0


    -- convert the oid string to OBJECTID
    EXEC    p_StringToByte @oid_s, @oid OUTPUT

    -- get the shopping cart
    SELECT  @shoppingCartOid = shoppingCart
    FROM    ibs_Workspace
    WHERE userId = @userId
    -- convert the oid
    EXEC    p_ByteToString @shoppingCartOid, @shoppingCartOid_s OUTPUT

    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId,
                            @name, @shoppingCartOid_s, 1,
                            0, '0x00', @productDescription,
                            @newOid_s OUTPUT, @newOid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- set the state to active:
            EXEC p_Object$changeState @oid_s, @userId, @op, 2

            -- search for catalog where this product is from
            SELECT  @catalogOid = ocat.oid, @l_catalogName = ocat.name,
                    @l_orderRespOid = cat.ordresp
            FROM    ibs_object prod, ibs_object prodgr,
                    ibs_object ocat, m2_Catalog_01 cat
            WHERE   prod.oid = @oid
            AND     prodgr.oid = prod.containerId
            AND     ocat.oid = prodgr.containerId
            AND     ocat.oid = cat.oid


            SELECT @l_existsProductOid = COUNT (*)
            FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
            WHERE ws.userId = @userId
            AND ws.shoppingCart = osce.containerId
            AND osce.state = 2
            AND sce.oid = osce.oid
            AND sce.productOid = @oid

            IF (@l_existsProductOid > 0) -- if there already exists the product oid
            BEGIN
                -- if there exists a productOid update the values - price and unit
                SELECT @l_oldQty = sce.qty, @shoppingCartOid = sce.oid
                FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
                WHERE ws.userId = @userId
                AND ws.shoppingCart = osce.containerId
                AND osce.state = 2
                AND sce.oid = osce.oid
                AND sce.productOid = @oid

                -- update the quantity
                UPDATE m2_ShoppingCartEntry_01
                SET qty = @l_oldQty + @qty, price = @price
                WHERE productOid = @oid
                AND oid = @shoppingCartOid

            END
            ELSE
            BEGIN
            -- create object type specific data:
            INSERT INTO m2_ShoppingCartEntry_01
                    (oid, qty, catalogOid, unitOfQty, packingUnit, productOid, productDescription
                    ,price, price2, price3, price4, price5, priceCurrency,
                    orderType, ordResp, orderText)
            VALUES (@newOid, @qty, @catalogOid, @unitOfQty, @packingUnit, @oid, @productDescription
                    ,@price, 0, 0, 0, 0, @priceCurrency,
                    N'Order', @l_orderRespOid, @l_catalogName)
            END

            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT  @retValue = @NOT_OK -- set return value
            END -- if no row affected
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Product_01$createCartEntry
