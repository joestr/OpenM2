--------------------------------------------------------------------------------
-- Product in shopping cart of the user. <BR>
--
-- @version     $Id: ShoppingCart_01Proc.sql,v 1.4 2003/10/31 00:12:52 klaus Exp $
--
-- author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
 -- Puts the product in shopping cart of the user
 --
 -- @input parameters:
 -- @param   @oid_s              Object id string
 -- @param   @userId             ID of the user who is deleting the object.
 -- @param   @op                 Operation to be performed (used for rights
 --                              check).
 --
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_ShoppingCart_01$createEntry');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ShoppingCart_01$createEntry
(
    -- common input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT,
    IN ai_name              VARCHAR (63),
    IN ai_tVersionId        INT,
    IN ai_state             INT,            -- not used anymore
    -- special input parameters
    IN ai_qty               INT,
    IN ai_unitOfQty         INT,
    IN ai_packingUnit       VARCHAR (63),
    IN ai_productDescription VARCHAR (255),
    IN ai_price             DECIMAL(19,4),
    IN ai_price2            DECIMAL(19,4),
    IN ai_price3            DECIMAL(19,4),
    IN ai_price4            DECIMAL(19,4),
    IN ai_price5            DECIMAL(19,4),
    IN ai_priceCurrency     VARCHAR (63),
    IN ai_orderType         VARCHAR (63),
    IN ai_orderRespOid_s    VARCHAR (18),
    IN ai_orderText         VARCHAR (63)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALREADY_EXISTS INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_priceCont     CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCartOid CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCartOid_s VARCHAR (18);
    DECLARE l_newOid_s      VARCHAR (18);
    DECLARE l_newOid        CHAR (8) FOR BIT DATA;
    DECLARE l_catalogOid    CHAR (8) FOR BIT DATA;
    DECLARE l_orderRespOid  CHAR (8) FOR BIT DATA;
    DECLARE l_existsProductOid INT;
    DECLARE l_oldQty        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS = 21;

    -- initialize local variables and return values:
    SET l_newOid            = c_NOOID;
    SET l_newOid_s          = c_NOOID_s;
    SET l_orderRespOid = c_NOOID;
    SET l_retValue = c_NOT_OK;
    SET l_oid = c_NOOID;
    SET l_oldQty = 0;

-- body:
    -- convert the oid string to OBJECTID
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_stringToByte (ai_orderRespOid_s, l_orderRespOid);

    -- get the shopping cart
    SELECT shoppingCart
    INTO l_shoppingCartOid
    FROM IBSDEV1.ibs_Workspace
    WHERE userId = ai_userId;

    -- convert the oid
    CALL IBSDEV1.p_byteToString (l_shoppingCartOid, l_shoppingCartOid_s);

    -- create base object:
    CALL IBSDEV1.p_Object_performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, l_shoppingCartOid_s, 1, 0, '0x00', ai_productDescription,
        l_newOid_s, l_newOid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN

        -- set the state to created
        UPDATE IBSDEV1.ibs_Object
        SET state = 2
        WHERE oid = l_oid;

        -- search for catalog where this product is from
        SELECT cat.oid
        INTO l_catalogOid
        FROM IBSDEV1.ibs_Object AS prod, IBSDEV1.ibs_Object AS prodgr,
            IBSDEV1.ibs_Object AS cat
        WHERE prod.oid = l_oid AND
                    prodgr.oid = prod.containerId AND
                    cat.oid = prodgr.containerId;

        -- create object type specific data:
        -- check if there already one productOid exist
        SELECT COUNT(*)
        INTO l_existsProductOid
        FROM IBSDEV1.ibs_Workspace AS ws, IBSDEV1.ibs_Object AS osce,
            IBSDEV1.m2_ShoppingCartEntry_01 AS sce
        WHERE CAST(ws.userId AS INT) = ai_userId AND
            ws.shoppingCart = osce.containerId AND CAST(osce.state AS INT) = 2
            AND sce.oid = osce.oid AND
            sce.productOid = l_oid;

        IF l_existsProductOid > 0 THEN

            -- if there exists a productOid update the values - price and unit
            SELECT sce1.qty, sce1.oid
            INTO l_oldQty, l_shoppingCartOid
            FROM IBSDEV1.ibs_Workspace AS ws1, IBSDEV1.ibs_Object AS osce1,
                 IBSDEV1.m2_ShoppingCartEntry_01 AS sce1
            WHERE ws1.userId = ai_userId AND
                  ws1.shoppingCart = osce1.containerId AND
                    osce1.state = 2 AND sce1.oid = osce1.oid
                     AND sce1.productOid = l_oid;

            UPDATE IBSDEV1.m2_ShoppingCartEntry_01
            SET qty = l_oldQty + ai_qty,
                price = ai_price
            WHERE productOid = l_oid AND
                oid = l_shoppingCartOid;
            GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        ELSE
            INSERT INTO IBSDEV1.m2_ShoppingCartEntry_01(oid, qty, catalogOid,
                unitOfQty,packingUnit, productOid, productDescription, price,
                price2, price3, price4, price5, priceCurrency, orderType,
                ordResp, orderText)
            VALUES (l_newOid, ai_qty, l_catalogOid, ai_unitOfQty,
                ai_packingUnit, l_oid, ai_productDescription, ai_price,
                ai_price2, ai_price3, ai_price4, ai_price5,
                ai_priceCurrency, ai_orderType, l_orderRespOid,
                ai_orderText);

            GET DIAGNOSTICS l_rowcount = ROW_COUNT;


        END IF;

        -- check if insertion was performed properly:
        IF l_rowcount <= 0 THEN
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;

    -- if object created successfully

    COMMIT;

    -- return the state value:

    RETURN l_retValue;
END;
