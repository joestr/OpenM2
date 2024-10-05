--------------------------------------------------------------------------------
-- All Procedures regarding to the m2_Order_01 table.
--
-- @version     $Id: Order_01Proc.sql,v 1.5 2003/10/31 16:29:02 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020901
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
--
-- Creates an Order Object.
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
-- 
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Order_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Order_01$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_state            INT,
    IN  ai_description      VARCHAR (255),
    ---- specialized attributes of object Order ---------------
    IN  ai_voucherNo        VARCHAR (63),
    IN  ai_voucherDate      TIMESTAMP,
    IN  ai_supplierCompany  VARCHAR (63),
    IN  ai_contactSupplier  VARCHAR (63),
    IN  ai_customerCompany  VARCHAR (63),
    IN  ai_contactCustomer  VARCHAR (63),
    IN  ai_deliveryAddress  VARCHAR (255),
    IN  ai_paymentAddress   VARCHAR (255),
-- AJ 990518 changed ... //////////////////////////////////////////////////////
    IN  ai_description1     VARCHAR (255),
    IN  ai_description2     VARCHAR (255),
    IN  ai_description3     VARCHAR (255),
    IN  ai_deliveryDate     TIMESTAMP,
    IN  ai_catalogOid_s     VARCHAR (18),
    IN  ai_paymentOid_s     VARCHAR (18),
    IN  ai_cc_owner         VARCHAR (200),
    IN  ai_cc_number        VARCHAR (63),
    IN  ai_cc_expmonth      VARCHAR (63),
    IN  ai_cc_expyear       VARCHAR (63),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_catalogOid    CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCartOid CHAR (8) FOR BIT DATA;
    DECLARE l_ordersOid     CHAR (8) FOR BIT DATA;
    DECLARE l_ordersOid_s   VARCHAR (18);
    DECLARE l_paymentOid    CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oldState      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input object ids must be converted
    ---------------------------------------------------------------------------
    CALL IBSDEV1.p_stringToByte (ai_catalogOid_s, l_catalogOid);
    CALL IBSDEV1.p_stringToByte (ai_paymentOid_s, l_paymentOid);

    -- get the shopping cart:
    SELECT  shoppingCart, orders
    INTO    l_shoppingCartOid, l_ordersOid
    FROM    IBSDEV1.ibs_Workspace
    WHERE   userId = ai_userId;
  
    CALL IBSDEV1.p_byteToString (l_ordersOid, l_ordersOid_s);
  
    -- create order object in the order container
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        l_ordersOid_s, 1, 0, c_NOOID_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- update other values
        INSERT INTO IBSDEV1. m2_Order_01
            (oid, voucherNo,voucherDate, supplierCompany, contactSupplier,
            customerCompany, contactCustomer, deliveryAddress, paymentAddress,
            description1, description2, description3, deliveryDate,
            paymentOid, cc_number, cc_expmonth, cc_expyear,
            cc_owner, catalogOid)
                   
        VALUES  (l_oid, ai_voucherNo, ai_voucherDate, ai_supplierCompany,
            ai_contactSupplier, ai_customerCompany, ai_contactCustomer,
            ai_deliveryAddress, ai_paymentAddress, ai_description1,
            ai_description2, ai_description3, ai_deliveryDate,
            l_paymentOid, ai_cc_number, ai_cc_expmonth, ai_cc_expyear,
            ai_cc_owner, l_catalogOid);
    
        -- move the product from the shopping cart to the order
        UPDATE IBSDEV1.ibs_Object
        SET     containerId = l_oid,
                tversionId = 16848897,
                state = 2
        WHERE   EXISTS  (
                            SELECT  *  
                            FROM    IBSDEV1.ibs_Object o,
                                    IBSDEV1.m2_ShoppingCartEntry_01 v
                            WHERE   v.oid = o.oid 
                                AND o.containerId = l_shoppingCartOid
                                AND o.state = 2 
                                AND v.catalogOid = l_catalogOid
                        );
    
        -- UPDATE state of the order:
        CALL IBSDEV1.p_Object$changeState (ao_oid_s, ai_userId, ai_op, ai_state);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
  
    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;

-- p_Order_01$create


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             Id of the user who is getting the data.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerName      Name of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         flag if object should be shown in newscontainer
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Order_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Order_01$retrieve(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- common output parameters:
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (200),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    -- type-specific output attributes:
    OUT ao_voucherNo        VARCHAR (63),
    OUT ao_voucherDate      TIMESTAMP,
    OUT ao_supplierCompany  VARCHAR (63),
    OUT ao_contactSupplier  VARCHAR (63),
    OUT ao_customerCompany  VARCHAR (63),
    OUT ao_contactCustomer  VARCHAR (63),
    OUT ao_deliveryAddress  VARCHAR (255),
    OUT ao_paymentAddress   VARCHAR (255),
    OUT ao_supplierAddress  VARCHAR (255),
    OUT ao_description1     VARCHAR (255),
    OUT ao_description2     VARCHAR (255),
    OUT ao_description3     VARCHAR (255),
    OUT ao_deliveryDate     TIMESTAMP,
    OUT ao_orderRespOid_s   VARCHAR (18),
    OUT ao_paymentOid_s     VARCHAR (18),
    OUT ao_paymentTypeId    INT,
    OUT ao_paymentName      VARCHAR (63),
    OUT ao_cc_owner         VARCHAR (200),
    OUT ao_cc_number        VARCHAR (63),
    OUT ao_cc_expmonth      VARCHAR (63),
    OUT ao_cc_expyear       VARCHAR (63),
    OUT ao_eMailOrderResp   VARCHAR (63),
    OUT ao_eMailCurrentUser VARCHAR (63),
    OUT ao_catalogOid_s     VARCHAR (18)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- define local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_catalogOid    CHAR (8) FOR BIT DATA;
    DECLARE l_orderRespOid  CHAR (8) FOR BIT DATA;
    DECLARE l_paymentOid    CHAR (8) FOR BIT DATA;
    DECLARE l_addressOid    CHAR (8) FOR BIT DATA;
    DECLARE l_companyOid    CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
  
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
    -- initialize local variables:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
         ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
         ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
         ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
         ao_lastChanged, ao_changer, ao_changerName, ao_validUntil, 
         ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
         ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN 
        -- get object specific data
        SELECT voucherNo, voucherDate, supplierCompany, contactSupplier,
              customerCompany, contactCustomer, deliveryAddress,
              paymentAddress, description1, description2, description3,
              deliveryDate, paymentOid, cc_number, cc_expmonth,
              cc_expyear, cc_owner, catalogOid
        INTO ao_voucherNo, ao_voucherDate, ao_supplierCompany,
            ao_contactSupplier, ao_customerCompany, ao_contactCustomer,
            ao_deliveryAddress, ao_paymentAddress, ao_description1,
            ao_description2, ao_description3, ao_deliveryDate,
            l_paymentOid, ao_cc_number, ao_cc_expmonth, ao_cc_expyear,
            ao_cc_owner, l_catalogOid
        FROM IBSDEV1.m2_Order_01
        WHERE oid = l_oid;

        SELECT COUNT(*) 
        INTO l_rowcount
                        FROM IBSDEV1.m2_Order_01
                        WHERE oid = l_oid;
        -- check if retrieve was performed properly:
        IF l_rowcount <= 0 THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    
        CALL IBSDEV1.p_byteToString (l_catalogOid, ao_catalogOid_s);
        CALL IBSDEV1.p_byteToString (l_paymentOid, ao_paymentOid_s);
    END IF;
    COMMIT;
    -- get catalogOid
    SELECT  DISTINCT catalogOid
    INTO l_catalogOid
    FROM IBSDEV1.ibs_Object o, IBSDEV1.m2_ShoppingCartEntry_01 s
    WHERE s.oid = o.oid
        AND o.tVersionId = 16848897
        AND o.containerId = l_oid;
    CALL IBSDEV1.p_byteToString (l_catalogOid, ao_catalogOid_s);
    -- retrieve the orderResponsibleOid out of the db
    SELECT ordResp
    INTO l_orderRespOid
    FROM IBSDEV1.m2_Catalog_01
    WHERE oid = l_catalogOid;
    -- convert the oids to strings
    CALL IBSDEV1.p_byteToString (l_orderRespOid, ao_orderRespOid_s);
    -- get addressOid of Company
    SELECT o.oid, c.companyOid 
    INTO l_addressOid, l_companyOid
    FROM IBSDEV1.m2_Catalog_01 c, IBSDEV1.ibs_Object o
    WHERE c.oid = l_catalogOid
        AND o.containerId = c.companyOid
        AND o.tVersionId = 16854785;
    -- get name of company
    SELECT name
    INTO ao_supplierCompany
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_companyOid;
  
    -- get the full-address of company
    SET l_sqlcode = 0;
    SELECT street || ';' || country || ' ' || zip ||
        ' ' || town as supplierAddress
    INTO ao_supplierAddress
    FROM IBSDEV1.m2_Address_01
    WHERE oid = l_addressOid;
    IF l_sqlcode = 100 THEN 
        SET l_rowcount = 0;
    ELSE 
        SET l_rowcount = 1;
    END IF;

    IF l_rowcount = 0 THEN 
        SET ao_supplierAddress = '';
    END IF;
    -- get name of payment type
    SELECT name
    INTO ao_paymentName
    FROM IBSDEV1.ibs_object
    WHERE oid = l_paymentOid;
  
    SELECT paymentTypeId
    INTO ao_paymentTypeId
    FROM IBSDEV1.m2_PaymentType_01
    WHERE oid = l_paymentOid;
  
    -- get email of orderResponsible
    SELECT a.email 
    INTO ao_eMailOrderResp
    FROM IBSDEV1.ibs_User u, IBSDEV1.ibs_UserProfile p, IBSDEV1.ibs_Object tabAddress,
        IBSDEV1.ibs_UserAddress_01 a
    WHERE p.userid = u.id
        AND u.oid = l_orderRespOid
        AND p.oid = tabAddress.containerId
        AND tabAddress.oid = a.oid;
  
    -- get fullname, personOid linked to the current user and the emailAddress
    -- off current user
    SELECT a.email 
    INTO ao_eMailCurrentUser
    FROM IBSDEV1.ibs_User u, IBSDEV1.ibs_UserProfile p, IBSDEV1.ibs_Object tabAddress,
        IBSDEV1.ibs_UserAddress_01 a
    WHERE u.id = ai_userId
        AND p.userid = u.id
        AND p.oid = tabAddress.containerId
        AND tabAddress.oid = a.oid;
  
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Order_01$retrieve

--------------------------------------------------------------------------------
--
-- Retrieve the default values for an order.
--
-- @version     1.00.0000, 24.09.1998
--
-- @author      Bernhard Walter (BW)  980924
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
--
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Order_01$retrDefaults');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Order_01$retrDefaults(
    -- intput parameters
    IN  ai_userId           INT,
    IN  ai_catalogOid_s     VARCHAR (18),
    -- output parameters
    OUT ao_voucherNo        VARCHAR (63),
    OUT ao_voucherDate      TIMESTAMP,
    OUT ao_supplierCompany  VARCHAR (63),
    OUT ao_contactSupplier  VARCHAR (63),
    OUT ao_orderRespOid_s   VARCHAR (18),
    OUT ao_orderRespMediaOid_s  VARCHAR (18),
    OUT ao_customerCompany  VARCHAR (63),
    OUT ao_contactCustomer  VARCHAR (63),
    OUT ao_deliveryName     VARCHAR (63),
    OUT ao_deliveryAddress  VARCHAR (63),
    OUT ao_deliveryZIP      VARCHAR (63),
    OUT ao_deliveryTown     VARCHAR (63),
    OUT ao_deliveryCountry  VARCHAR (63),
    OUT ao_paymentName      VARCHAR (63),
    OUT ao_paymentAddress   VARCHAR (63),
    OUT ao_paymentZIP       VARCHAR (63),
    OUT ao_paymentTown      VARCHAR (63),
    OUT ao_paymentCountry   VARCHAR (63),
    OUT ao_description1     VARCHAR (255),
    OUT ao_description2     VARCHAR (255),
    OUT ao_description3     VARCHAR (255),
    OUT ao_deliveryDate     TIMESTAMP,
    OUT ao_eMailOrderResp   VARCHAR (63),
    OUT ao_eMailCurrentUser VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_catalogOid    CHAR (8) FOR BIT DATA;
    DECLARE l_orderRespOid  CHAR (8) FOR BIT DATA;
    DECLARE l_orderRespMediaOid CHAR (8) FOR BIT DATA;
    DECLARE l_personOid     CHAR (8) FOR BIT DATA;
    DECLARE l_companyOid    CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_catalogOid_s, l_catalogOid);
    -- initialize output paramters
    SET ao_customerCompany = '';
    -- retrieve the supplier data out of the catalog
    SELECT  co.name || ' ' || co.legal_form,
        u.fullname, ca.ordResp, ca.ordRespMed,
        ca.description1, ca.description2, '' 
    INTO  ao_supplierCompany, ao_contactSupplier, l_OrderRespOid,
        l_orderRespMediaOid, ao_description1, ao_description2,
        ao_description3 
    FROM IBSDEV1.m2_Catalog_01 ca
        LEFT OUTER JOIN    
        (
            SELECT c.legal_form, c.oid, o.name
            FROM    IBSDEV1.mad_Company_01 c, IBSDEV1.ibs_Object o
            WHERE   c.oid = o.oid
            AND o.state = 2
        ) co
        ON  co.oid = ca.companyOid
        LEFT OUTER JOIN
        (
            SELECT oid, fullname
            FROM IBSDEV1.   ibs_User
            WHERE   state = 2
        ) u
        ON      u.oid = ca.ordResp
    WHERE   ca.oid =  l_catalogOid;
    -- convert the oids to strings
    CALL IBSDEV1.p_byteToString (l_orderRespOid, ao_orderRespOid_s);
    CALL IBSDEV1.p_byteToString (l_orderRespMediaOid, ao_orderRespMediaOid_s);
  
    -- get email of orderResponsible
    SELECT a.email 
    INTO ao_eMailOrderResp
    FROM IBSDEV1.ibs_User u, IBSDEV1.ibs_UserProfile p, IBSDEV1.ibs_Object tabAddress,
        IBSDEV1.ibs_UserAddress_01 a
    WHERE p.userid = u.id
        AND u.oid = l_orderRespOid
        AND p.oid = tabAddress.containerId
        AND tabAddress.oid = a.oid;
  
    -- get oid of person linked to user
    -- it is possible that more than one person is linked to the user, in
    -- this case, the first one is taken  (to provide old m2-systems)
    SELECT MIN (per.oid) 
    INTO l_personOid
    FROM IBSDEV1.ibs_User u, IBSDEV1.ibs_Object oper, IBSDEV1.mad_Person_01 per
    WHERE u.id = ai_userId
        AND u.oid = per.useroid
        AND oper.oid = per.oid
        AND oper.state = 2;
  
    -- get fullname and the emailAddress
    -- off current user
    SELECT u.fullname, a.email 
    INTO ao_contactCustomer, ao_eMailCurrentUser
    FROM IBSDEV1.ibs_User u, IBSDEV1.ibs_UserProfile p, IBSDEV1.ibs_Object tabAddress,
        ibs_UserAddress_01 a
    WHERE u.id = userId
        AND p.userid = u.id
        AND p.oid = tabAddress.containerId
        AND tabAddress.oid = a.oid;
  
    -- get the users company
    SELECT  DISTINCT o1.oid, o1.name || ' ' || c.legal_form
    INTO l_companyOid, ao_customerCompany
    FROM IBSDEV1.ibs_Object o1 INNER JOIN IBSDEV1.mad_Company_01 c ON c.oid = o1.oid
        AND o1.state = 2 INNER JOIN IBSDEV1.ibs_Object o2 ON o2.containerId = o1.oid
        AND o2.state = 2 INNER JOIN IBSDEV1.ibs_Object o3 ON o3.containerId = o2.oid
        AND o3.state = 2
        AND o3.oid = l_personOid;
  
    CALL IBSDEV1.p_Order_01$retrieveAddresses(l_companyOid, ao_customerCompany,
        ao_deliveryName, ao_paymentName, ao_deliveryAddress, ao_paymentAddress,
        ao_deliveryZip, ao_paymentZip, ao_deliveryTown, ao_paymentTown,
        ao_deliveryCountry, ao_paymentCountry);
    -- set the dates to today
    SET ao_voucherDate = CURRENT TIMESTAMP;
    SET ao_deliveryDate = CURRENT TIMESTAMP;
    -- return the state value
    RETURN 1;
END;
-- p_Order_01$retrDefaults

--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Order_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Order_01$delete(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE l_rowcount INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
    -- define local variables:
    -- initialize local variables:
-- body:
    -- delete base object:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN 
        -- delete object type specific data:
        -- (deletes all type specific tuples which are not within ibs_Object)
        DELETE FROM IBSDEV1.m2_Order_01
        WHERE oid = l_oid;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    
        -- check if deletion was performed properly:
        IF l_rowcount <= 0 THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Order_01$delete

--------------------------------------------------------------------------------
-- set voucherdate to actual date. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             Id of the user who is getting the data.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Order_01$actOrderDate');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Order_01$actOrderDate(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
    -- define local variables:
    -- initialize local variables:
    UPDATE IBSDEV1.m2_Order_01
    SET voucherDate = CURRENT TIMESTAMP
    WHERE oid = l_oid;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    COMMIT;
    IF l_rowcount <= 0 THEN 
        SET l_retValue = c_NOT_OK;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Order_01$actOrderDate