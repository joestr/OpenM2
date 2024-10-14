CREATE OR REPLACE FUNCTION p_ShoppingCart_01$createEntry
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    ai_name           VARCHAR2,
    ai_tVersionId     INTEGER,
    ai_state          INTEGER,                    -- not used anymore
    -- special input parameters
    ai_qty            INTEGER,
    ai_unitOfQty      INTEGER,
    ai_packingUnit    VARCHAR2,
    ai_productDescription VARCHAR2,
    ai_price          NUMBER,
    ai_price2         NUMBER,
    ai_price3         NUMBER,
    ai_price4         NUMBER,
    ai_price5         NUMBER,
    ai_priceCurrency  VARCHAR2,
    ai_orderType      VARCHAR2,
    ai_orderRespOid_s VARCHAR2,
    ai_orderText      VARCHAR2
)
RETURN INTEGER
AS

    -- define constants
    c_NOOID             CONSTANT RAW (8) := '0000000000000000';
    c_NOOID_s           CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    -- define return constants
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    -- define return values
    l_retValue              INTEGER := c_NOT_OK;       -- return value of this procedure
    -- define local variables
    l_oid                   RAW (8) := c_NOOID;
    l_priceCont             RAW (8);
    l_shoppingCartOid       RAW (8);
    l_shoppingCartOid_s     VARCHAR2 (18);
    l_newOid_s              VARCHAR2 (18);
    l_newOid                RAW (8);
    l_catalogOid            RAW (8);
    l_orderRespOid          RAW (8);
    l_existsProductOid      INTEGER := 0;
    l_oldQty                INTEGER := 0;


BEGIN
    -- convert the oid string to OBJECTID
    p_StringToByte (ai_oid_s, l_oid);
    p_StringToByte (ai_orderRespOid_s, l_orderRespOid);


    BEGIN
        -- get the shopping cart
        SELECT  shoppingCart
        INTO    l_shoppingCartOid
        FROM    ibs_Workspace
        WHERE   userId = ai_userId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Product_01$createCartEntry',
            'Error in get shoppingCart');
        RAISE;
    END;

    -- convert the oid
    p_ByteToString (l_shoppingCartOid, l_shoppingCartOid_s);

    -- body:
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                        ai_name, l_shoppingCartOid_s, 1,
                        0, c_NOOID_s, ai_productDescription,
                        l_newOid_s, l_newOid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
        BEGIN
            -- set the state to created
            UPDATE  ibs_Object
            SET     state = 2
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Product_01$createCartEntry',
                'Error in set state to created');
            RAISE;
        END;

        -- search for catalog where this product is from
        BEGIN
            SELECT  cat.oid
            INTO    l_catalogOid
            FROM    ibs_object prod, ibs_object prodgr, ibs_object cat
            WHERE   prod.oid = l_oid
              AND   prodgr.oid = prod.containerId
              AND   cat.oid = prodgr.containerId;
            EXCEPTION
                WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_ShoppingCart_01$createCartEntry',
                                     'Error SELECT COUNT');
            RAISE;
        END;

        -- check if this product does exists already in shopping cart of current user
        SELECT count (*)
        INTO l_existsProductOid
        FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
        WHERE ws.userId = ai_userId
        AND ws.shoppingCart = osce.containerId
        AND osce.state = 2
        AND sce.oid = osce.oid
        AND sce.productOid = l_oid;

        IF (l_existsProductOid > 0) -- if there already exists the product
        THEN
            -- if there exists a productOid update the values - price and unit
            SELECT sce.qty, sce.oid
            INTO l_oldQty,l_shoppingCartOid
            FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
            WHERE ws.userId = ai_userId
            AND ws.shoppingCart = osce.containerId
            AND osce.state = 2
            AND sce.oid = osce.oid
            AND sce.productOid = l_oid;

            UPDATE m2_ShoppingCartEntry_01
            SET qty = l_oldQty + ai_qty, price = ai_price
            WHERE productOid = l_oid
            AND oid = l_shoppingCartOid;

        ELSE
            -- create object type specific data:
            INSERT INTO m2_ShoppingCartEntry_01
                    (oid, qty, catalogOid, unitOfQty, packingUnit, productOid, productDescription
                    ,price, price2, price3, price4, price5, priceCurrency
                    ,orderType, ordResp, orderText)
            VALUES (l_newOid, ai_qty, l_catalogOid, ai_unitOfQty, ai_packingUnit, l_oid, ai_productDescription
                    ,ai_price, ai_price2, ai_price3, ai_price4, ai_price5, ai_priceCurrency
                    ,ai_orderType, l_orderRespOid, ai_orderText);
        END IF;
        -- check if insertion was performed properly:
        IF (SQL%ROWCOUNT <= 0)        -- no row affected?
        THEN
            l_retValue := c_NOT_OK; -- set return value
        END IF; -- if no row affected
    END IF;-- if object created successfully
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Product_01$createCartEntry',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_ShoppingCart_01$createEntry;
/

show errors;

EXIT;