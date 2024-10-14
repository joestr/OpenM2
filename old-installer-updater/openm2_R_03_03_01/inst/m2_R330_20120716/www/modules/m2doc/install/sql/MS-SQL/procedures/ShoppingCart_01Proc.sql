
/******************************************************************************
 * Puts the product in shopping cart of the user
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
EXEC p_dropProc N'p_ShoppingCart_01$createEntry'
GO

-- create the new procedure:
CREATE PROCEDURE p_ShoppingCart_01$createEntry
(
    -- common input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_name           NAME,
    @ai_tVersionId     TVERSIONID,
    @ai_state          INT,                    -- not used anymore
    -- special input parameters
    @ai_qty            INT,
    @ai_unitOfQty      INT,
    @ai_packingUnit    NAME,
    @ai_productDescription DESCRIPTION,
    @ai_price          MONEY,
    @ai_price2         MONEY,
    @ai_price3         MONEY,
    @ai_price4         MONEY,
    @ai_price5         MONEY,
    @ai_priceCurrency  NAME,
    @ai_orderType      NAME,
    @ai_orderRespOid_s OBJECTIDSTRING,
    @ai_orderText      NAME

)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT,
            @c_INSUFFICIENT_RIGHTS INT,
            @c_ALREADY_EXISTS INT
    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1,
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @l_retValue INT,
            @l_oid OBJECTID,
            @l_priceCont OBJECTID,
            @l_shoppingCartOid OBJECTID,
            @l_shoppingCartOid_s OBJECTIDSTRING,
            @l_newOid_s OBJECTIDSTRING,
            @l_newOid OBJECTID,
            @l_catalogOid OBJECTID,
            @l_orderRespOid OBJECTID,
	    @l_existsProductOid INT,
            @l_oldQty INT
    SELECT  @l_orderRespOid = 0x0000000000000000,
            @l_retValue = @c_NOT_OK,
            @l_oid = 0x0000000000000000, @l_oldQty = 0


    -- convert the oid string to OBJECTID
    EXEC    p_StringToByte @ai_oid_s, @l_oid OUTPUT
    EXEC    p_StringToByte @ai_orderRespOid_s, @l_orderRespOid OUTPUT

    -- get the shopping cart
    SELECT  @l_shoppingCartOid = shoppingCart
    FROM    ibs_Workspace
    WHERE   userId = @ai_userId
    -- convert the oid
    EXEC    p_ByteToString @l_shoppingCartOid, @l_shoppingCartOid_s OUTPUT

    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId,
                            @ai_name, @l_shoppingCartOid_s, 1,
                            0, '0x00', @ai_productDescription,
                            @l_newOid_s OUTPUT, @l_newOid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- set the state to created
            UPDATE  ibs_Object
            SET     state = 2
            WHERE   oid = @l_oid

            -- search for catalog where this product is from


            SELECT  @l_catalogOid = cat.oid
            FROM    ibs_object prod, ibs_object prodgr, ibs_object cat
            WHERE   prod.oid = @l_oid
              AND   prodgr.oid = prod.containerId
              AND   cat.oid = prodgr.containerId

            -- create object type specific data:
            -- check if there already one productOid exist
            SELECT @l_existsProductOid = COUNT (*)
            FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
            WHERE ws.userId = @ai_userId
            AND ws.shoppingCart = osce.containerId
            AND osce.state = 2
            AND sce.oid = osce.oid
            AND sce.productOid = @l_oid


            IF (@l_existsProductOid > 0) -- if there already exists the product oid
            BEGIN
                -- if there exists a productOid update the values - price and unit
                SELECT @l_oldQty = sce.qty, @l_shoppingCartOid = sce.oid
                FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
                WHERE ws.userId = @ai_userId
                AND ws.shoppingCart = osce.containerId
                AND osce.state = 2
                AND sce.oid = osce.oid
                AND sce.productOid = @l_oid

                UPDATE m2_ShoppingCartEntry_01
                SET qty = @l_oldQty + @ai_qty, price = @ai_price
                WHERE productOid = @l_oid
                AND oid = @l_shoppingCartOid
            END
            ELSE -- no product exist
            BEGIN
                INSERT INTO m2_ShoppingCartEntry_01
                        (oid, qty, catalogOid, unitOfQty, packingUnit
                        ,productOid, productDescription
                        ,price, price2, price3, price4, price5, priceCurrency
                        ,orderType, ordResp, orderText)
                VALUES (@l_newOid, @ai_qty, @l_catalogOid, @ai_unitOfQty, @ai_packingUnit
                        , @l_oid, @ai_productDescription
                        , @ai_price, @ai_price2, @ai_price3, @ai_price4, @ai_price5, @ai_priceCurrency
                        , @ai_orderType, @l_orderRespOid, @ai_orderText)
            END
            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT  @l_retValue = @c_NOT_OK -- set return value
            END -- if no row affected
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_ShoppingCart_01$createEntry
