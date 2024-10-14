/******************************************************************************
 * All stored procedures regarding the m2_Order_01 table. <BR>
 * 
 * @version     $Id: Order_01Proc.sql,v 1.12 2003/10/31 16:27:54 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  990507
 ******************************************************************************
 */


/******************************************************************************
 *
 * Creates an Order Object.
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Order_01$create
(
    -- input parameters:
    ai_userid               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_state                INTEGER,
    ai_description          VARCHAR2,
    ---- specialized attributes of object Order ---------------
    ai_voucherNo            VARCHAR2,
    ai_voucherDate          DATE,
    ai_supplierCompany      VARCHAR2,
    ai_contactSupplier      VARCHAR2,
    ai_customerCompany      VARCHAR2,
    ai_contactCustomer      VARCHAR2,
    ai_deliveryAddress      VARCHAR2,
    ai_paymentAddress       VARCHAR2,
    ai_description1         VARCHAR2,
    ai_description2         VARCHAR2,
    ai_description3         VARCHAR2,
    ai_deliveryDate         DATE,
    ai_catalogOid_s         VARCHAR2,
    ai_paymentOid_s         VARCHAR2,
    ai_cc_owner             VARCHAR2,
    ai_cc_number            VARCHAR2,
    ai_cc_expmonth          VARCHAR2,
    ai_cc_expyear           VARCHAR2,
    -- output parameters:
    ao_oid_s                OUT         VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOOID_S               CONSTANT        VARCHAR2 (18) := '0x0000000000000000';
    c_NOT_OK                CONSTANT        INTEGER := 0;
    c_ALL_RIGHT             CONSTANT        INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT        INTEGER := 2;
    -- local variables
    l_oldState              INTEGER;
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW(8);
    l_catalogOid            RAW(8);
    l_shoppingCartOid       RAW(8);
    l_ordersOid             RAW(8);
    l_ordersOid_s           VARCHAR2(18);
    l_shoppingOid           RAW(8);
    l_paymentOid            RAW(8);
    
    CURSOR l_shoppingCartEntries IS
    (
        SELECT  oid
        FROM    m2_ShoppingCartEntry_01
        WHERE   catalogOid = l_catalogOid
    );

BEGIN
    p_stringToByte (ai_catalogOid_s, l_catalogOid);
    p_stringToByte (ai_paymentOid_s, l_paymentOid);
    -- get the shopping cart
    SELECT  shoppingCart, orders
    INTO    l_shoppingCartOid, l_ordersOid
    FROM    ibs_Workspace
    WHERE   userId = ai_userid;

    p_ByteToString (l_ordersOid, l_ordersOid_s);

    -- create order object in the order container
    l_retValue := p_Object$performCreate (
                        ai_userid, ai_op, ai_tVersionId,
                        ai_name, l_ordersOid_s, 0,
                        0, c_NOOID_S , ai_description,
                        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            -- update other values
            INSERT INTO  m2_Order_01
                    (oid, voucherNo,voucherDate, supplierCompany, contactSupplier,
                    customerCompany, contactCustomer, deliveryAddress, paymentAddress,
                    description1, description2, description3, deliveryDate,
                    paymentOid, cc_number, cc_expmonth, cc_expyear,
                    cc_owner, catalogOid)
            VALUES  (l_oid, ai_voucherNo, ai_voucherDate, ai_supplierCompany, ai_contactSupplier,
                     ai_customerCompany, ai_contactCustomer, ai_deliveryAddress, ai_paymentAddress,
                     ai_description1, ai_description2, ai_description3, ai_deliveryDate,
                     l_paymentOid, ai_cc_number, ai_cc_expmonth, ai_cc_expyear,
                     ai_cc_owner, l_catalogOid);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_Order_01$create','Error INSERT INTO');
                RAISE;
        END;

        -- move the product from the shopping cart to the order
        OPEN l_shoppingCartEntries;
        
        BEGIN
        LOOP    
            FETCH l_shoppingCartEntries INTO l_shoppingOid;
            EXIT WHEN l_shoppingCartEntries%NOTFOUND;
            
                UPDATE  ibs_Object
                SET     containerId = l_oid,        
                        tversionId = 16848897,
                        state = 2
                WHERE   containerId = l_shoppingCartOid
                  AND   state = 2
                  AND   oid = l_shoppingOid;
        END LOOP;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_Order_01$create','Error UPDATE');
                RAISE;
        END;
        
        CLOSE l_shoppingCartEntries;
        
        -- UPDATE state of the order
        l_retValue := p_Object$changeState (ao_oid_s, ai_userid, ai_op, ai_state);
    END IF;

    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Order_01$create',
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', tVersionId: ' || ai_tVersionId ||
                          ', name: ' || ai_name ||
                          ', state: ' || ai_state ||
                          ', description: ' || ai_description ||
                          ', voucherNo: ' || ai_voucherNo ||
                          ', voucherDate: ' || ai_voucherDate ||
                          ', supplierCompany: ' || ai_supplierCompany ||
                          ', contactSupplier: ' || ai_contactSupplier ||
                          ', customerCompany: ' || ai_customerCompany ||
                          ', contactCustomer: ' || ai_contactCustomer ||
                          ', deliveryAddress: ' || ai_deliveryAddress ||
                          ', paymentAddress: ' || ai_paymentAddress ||
                          ', description1: ' || ai_description1 ||
                          ', description2: ' || ai_description2 ||
                          ', description3: ' || ai_description3 ||
                          ', deliveryDate: ' || ai_deliveryDate ||
                          ', catalogOid_s: ' || ai_catalogOid_s ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;
END p_Order_01$create;
/

show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId                Id of the user who is getting the data.
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Order_01$retrieve
(
    -- common input parameters:
    ai_oid_s            VARCHAR2,
    ai_userId           INTEGER,
    ai_op               INTEGER,
    -- common output parameters
    ao_state            OUT INTEGER,
    ao_tVersionId       OUT INTEGER,
    ao_typeName         OUT VARCHAR2,
    ao_name             OUT VARCHAR2,
    ao_containerId      OUT RAW,
    ao_containerName    OUT VARCHAR2,
    ao_containerKind    OUT INTEGER,
    ao_isLink           OUT NUMBER,
    ao_linkedObjectId   OUT RAW,
    ao_owner            OUT INTEGER,
    ao_ownerName        OUT VARCHAR2,
    ao_creationDate     OUT DATE,
    ao_creator          OUT INTEGER,
    ao_creatorName      OUT VARCHAR2,
    ao_lastChanged      OUT DATE,
    ao_changer          OUT INTEGER,
    ao_changerName      OUT VARCHAR2,
    ao_validUntil       OUT DATE,
    ao_description      OUT VARCHAR2,
    ao_showInNews       OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,    
    -- type-specific  attributes:
    ao_voucherNo       OUT VARCHAR2,
    ao_voucherDate     OUT DATE,
    ao_supplierCompany OUT VARCHAR2,
    ao_contactSupplier OUT VARCHAR2,
    ao_customerCompany OUT VARCHAR2,
    ao_contactCustomer OUT VARCHAR2,
    ao_deliveryAddress OUT VARCHAR2,
    ao_paymentAddress  OUT VARCHAR2,
    ao_supplierAddress OUT VARCHAR2,
    ao_description1    OUT VARCHAR2,
    ao_description2    OUT VARCHAR2,
    ao_description3    OUT VARCHAR2,
    ao_deliveryDate    OUT DATE,
    ao_orderRespOid_s  OUT VARCHAR2,
    ao_paymentOid_s    OUT VARCHAR2,
    ao_paymentTypeId   OUT INTEGER,
    ao_paymentName     OUT VARCHAR2,
    ao_cc_owner        OUT VARCHAR2,
    ao_cc_number       OUT VARCHAR2,
    ao_cc_expmonth     OUT VARCHAR2,
    ao_cc_expyear      OUT VARCHAR2,
    ao_eMailOrderResp  OUT VARCHAR2,
    ao_eMailCurrentUser OUT VARCHAR2,
    ao_catalogOid_s    OUT VARCHAR2
)
RETURN INTEGER
AS

    -- define constants
    c_NOOID             CONSTANT RAW (8) := '0000000000000000';
    c_NOOID_S           CONSTANT VARCHAR2 (18) := '0x0000000000000000';
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
    l_catalogOid            RAW (8) := c_NOOID;
    l_orderRespOid          RAW (8) := c_NOOID;
    l_addressOid            RAW (8) := c_NOOID;
    l_companyOid            RAW (8) := c_NOOID;
    l_paymentOid            RAW (8) := c_NOOID;

BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve(
                ai_oid_s, ai_userId, ai_op,
                ao_state, ao_tVersionId, ao_typeName,
                ao_name, ao_containerId, ao_containerName,
                ao_containerKind, ao_isLink, ao_linkedObjectId,
                ao_owner, ao_ownerName,
                ao_creationDate, ao_creator, ao_creatorName,
                ao_lastChanged, ao_changer, ao_changerName,
                ao_validUntil, ao_description, ao_showInNews, 
                ao_checkedOut, ao_checkOutDate, 
                ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                l_oid);

        IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
        THEN
            BEGIN
                -- retrieve object type specific data:
                -- get object specific data
	            SELECT voucherNo, voucherDate, supplierCompany,
                       contactSupplier, customerCompany, contactCustomer,
                       deliveryAddress, paymentAddress, description1,  
                       description2, description3, deliveryDate,
                       paymentOid, cc_number, cc_expmonth,
                       cc_expyear, cc_owner, catalogOid
                INTO    ao_voucherNo, ao_voucherDate, ao_supplierCompany,
                        ao_contactSupplier, ao_customerCompany, ao_contactCustomer,
                        ao_deliveryAddress, ao_paymentAddress, ao_description1,
                        ao_description2, ao_description3, ao_deliveryDate,
                        l_paymentOid, ao_cc_number, ao_cc_expmonth,
                        ao_cc_expyear, ao_cc_owner, l_catalogOid
                FROM    m2_Order_01
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    l_retValue := c_NOT_OK;
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in retrive object type specific data');
                RAISE;
            END;
            
            p_StringToByte (l_catalogOid, ao_catalogOid_s);
            p_StringToByte (l_paymentOid, ao_paymentOid_s);

            BEGIN
                -- get catalogOid
                SELECT DISTINCT catalogOid
                INTO    l_catalogOid
                FROM    ibs_Object o, m2_ShoppingCartEntry_01 s
                WHERE   s.oid = o.oid
                  AND   o.tVersionId = 16848897 -- Bestellposition/Part of Order
                  AND   o.containerId = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in retrive get catalogOid');
                RAISE;
            END;
            
            BEGIN
                -- retrieve the orderResponsibleOid out of the db
	            SELECT  ordResp
	            INTO    l_orderRespOid
                FROM    m2_Catalog_01
	            WHERE   oid =  l_catalogOid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in retrive get orderResponsibleOid');
                RAISE;
            END;


            -- convert the oids to strings
            p_ByteToString (l_orderRespOid, ao_orderRespOid_s);
	
            BEGIN
                -- get addressOid of Company
                SELECT  o.oid, c.companyOid
                INTO    l_addressOid, l_companyOid
                FROM    m2_Catalog_01 c, ibs_Object o
                WHERE   c.oid = l_catalogOid
                  AND   o.containerId = c.companyOid
                  AND   o.tVersionId = 16854785;      -- Tab - address
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;   -- nothing should happen - no company assigned to order
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in get addressOid');
                RAISE;
            END;
        
            BEGIN
                -- get name of company
                SELECT  name
                INTO    ao_supplierCompany
                FROM    ibs_Object
                WHERE   oid = l_companyOid;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    ao_supplierCompany := ' ';   -- nothing should happen - 
                                                -- no company assigned to order
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in get name of company');
                RAISE;
            END;
    
            BEGIN
                -- get the full-address of company
                SELECT  street || ';'  -- add delimiter ; to string
                         || country || ' ' || zip || ' ' || town
                INTO    ao_supplierAddress
                FROM    m2_Address_01
                WHERE   oid = l_addressOid;
             EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    ao_supplierAddress := ' ';
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in get address of supplier');
                RAISE;
            END;
            
            BEGIN
                -- get name of payment type
                SELECT  name
                INTO    ao_paymentName
                FROM    ibs_object
                WHERE   oid = l_paymentOid;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    ao_supplierAddress := ' ';
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in SELECT name');
                RAISE;
            END;

            BEGIN
                -- get the id of the payment type
                SELECT  paymentTypeId
                INTO    ao_paymentTypeId
                FROM    m2_PaymentType_01
                WHERE   oid = l_paymentOid;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    ao_supplierAddress := ' ';
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in SELECT paymentTypeId');
                RAISE;
            END;
            
            BEGIN
                -- get email of orderResponsible
                SELECT  a.email
                INTO    ao_eMailOrderResp
                FROM    ibs_User u,
                        ibs_UserProfile p, 
                        ibs_Object tabAddress, 
                        ibs_UserAddress_01 a 
                WHERE   p.userid = u.id
                AND  u.oid = l_orderRespOid
                AND  p.oid = tabAddress.containerId
                AND  tabAddress.oid = a.oid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in get email of orderResponsible');
                RAISE;
            END;
            
            BEGIN
                -- get user email data
                -- get fullname, personOid linked to the current user and the emailAddress
                -- off current user
                SELECT  a.email
                INTO    ao_eMailCurrentUser
                FROM    ibs_User u,
                        ibs_UserProfile p, 
                        ibs_Object tabAddress, 
                        ibs_UserAddress_01 a
                WHERE   u.id = ai_userId 
                        AND  p.userid = u.id
                        AND  p.oid = tabAddress.containerId
                        AND  tabAddress.oid = a.oid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
                        'Error in get user email data');
                RAISE;
            END;
                
        END IF;-- if operation properly performed
        
        
        
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieve',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_Order_01$retrieve;
/

show errors;

/******************************************************************************
 *
 * Retrieve the default values for an order.
 *
 * @version     1.00.0000, 24.09.1998
 *
 * @author      Bernhard Walter (BW)  980924
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 *
 */
 
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Order_01$retrDefaults
(
    ai_userId               INTEGER,
    ai_catalogOid_s         VARCHAR2,
    -- output parameters
    ao_voucherNo            OUT VARCHAR2,  
    ao_voucherDate          OUT DATE,
    ao_supplierCompany      OUT VARCHAR2,
    ao_contactSupplier      OUT VARCHAR2,
    ao_orderRespOid_s       OUT VARCHAR2,
    ao_orderRespMediaOid_s  OUT VARCHAR2,
    ao_customerCompany      OUT VARCHAR2,
    ao_contactCustomer      OUT VARCHAR2,
    ao_deliveryName         OUT VARCHAR2,
    ao_deliveryAddress      OUT VARCHAR2,
    ao_deliveryZIP          OUT VARCHAR2,
    ao_deliveryTown         OUT VARCHAR2,
    ao_deliveryCountry      OUT VARCHAR2,
    ao_paymentName          OUT VARCHAR2,
    ao_paymentAddress       OUT VARCHAR2,
    ao_paymentZIP           OUT VARCHAR2,
    ao_paymentTown          OUT VARCHAR2,
    ao_paymentCountry       OUT VARCHAR2,
    ao_description1         OUT VARCHAR2,
    ao_description2         OUT VARCHAR2,
    ao_description3         OUT VARCHAR2,
    ao_deliveryDate         OUT DATE,
    ao_eMailOrderResp       OUT VARCHAR2,
    ao_eMailCurrentUser     OUT VARCHAR2
)
RETURN INTEGER
AS
    -- define constants
    c_NOOID             CONSTANT RAW (8) := '0000000000000000';
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

    l_catalogOid            RAW (8) := c_NOOID;
    l_orderRespOid          RAW (8) := c_NOOID;
    l_orderRespMediaOid     RAW (8) := c_NOOID;
    l_personOid             RAW (8) := c_NOOID;
    l_companyOid            RAW (8) := c_NOOID;
BEGIN

    -- initialize customerCompany
    ao_customerCompany := '';

    -- convert to byte 
    p_stringToByte (ai_catalogOid_s, l_catalogOid);

    BEGIN
        -- retrieve the supplier data out of the catalog
	    SELECT  co.name || ' ' || co.legal_form,
                u.fullname, ca.ordResp, ca.ordRespMed,
                ca.description1, ca.description2, ' '
        INTO    ao_supplierCompany, ao_contactSupplier,
                l_orderRespOid, l_orderRespMediaOid,
                ao_description1, ao_description2, ao_description3
        FROM    m2_Catalog_01 ca,
                (
                    SELECT c.legal_form, c.oid, o.name
                    FROM    mad_Company_01 c, ibs_Object o
                    WHERE   c.oid = o.oid
                      AND   o.state = 2
                ) co,
                (
                    SELECT oid, fullname
                    FROM    ibs_User
                    WHERE   state = 2
                ) u
	    WHERE   ca.oid =  l_catalogOid
          AND   co.oid(+) = ca.companyOid
          AND   u.oid(+) = ca.ordResp;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;       -- valid exception
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrDefaults',
                'Error in get address of company');
        RAISE;
    END;           
      
    -- convert the oids to strings
    p_ByteToString (l_orderRespOid, ao_orderRespOid_s);
    p_ByteToString (l_orderRespMediaOid, ao_orderRespMediaOid_s);

    BEGIN
        -- get email of orderResponsible
        SELECT  a.email
        INTO    ao_eMailOrderResp
        FROM    ibs_User u,
                ibs_UserProfile p, 
                ibs_Object tabAddress, 
                ibs_UserAddress_01 a 
        WHERE   p.userid = u.id
                AND  u.oid = l_orderRespOid
                AND  p.oid = tabAddress.containerId
                AND  tabAddress.oid = a.oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;       -- valid exception
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrDefaults',
                'Error in get email of orderResponsible');
        RAISE;
    END;           

    BEGIN

        -- get oid of person linked to user
        -- it is possible that more than one person is linked to the user, in
        -- this case, the first one is taken  (to provide old m2-systems)
        SELECT  min (per.oid)
        INTO    l_personOid
        FROM    ibs_User u,
                ibs_Object oper,
                mad_Person_01 per 
        WHERE   u.id = ai_userId 
           AND  u.oid = per.useroid
           AND  oper.oid = per.oid
           AND  oper.state = 2;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrDefaults',
                'Error in get oid of assigned person of current user');
        RAISE;
    END;
    

    BEGIN
        -- get user data
        -- get fullname, personOid linked to the current user and the emailAddress
        -- off current user
        SELECT  u.fullName, a.email          --useroid only for testing	 
        INTO    ao_contactCustomer, ao_eMailCurrentUser
        FROM    ibs_User u,
                ibs_UserProfile p, 
                ibs_Object tabAddress, 
                ibs_UserAddress_01 a
        WHERE   u.id = ai_userId 
                AND  p.userid = u.id
                AND  p.oid = tabAddress.containerId
                AND  tabAddress.oid = a.oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ao_contactCustomer := ' ';
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrDefaults',
                'Error in get user data');
        RAISE;
    END;        
  
    BEGIN
        -- get the users company
        SELECT DISTINCT o1.oid, o1.name || ' ' || c.legal_form
        INTO    l_companyOid, ao_customerCompany 
        FROM    ibs_Object o1, mad_Company_01 c, ibs_Object o2,
                ibs_Object o3
        WHERE   c.oid = o1.oid              -- Company
          AND   o1.state = 2
          AND   o2.containerId = o1.oid     -- Tab Contacts - Company
          AND   o2.state = 2
          AND   o3.containerId = o2.oid     -- Person - Tab Contacts
          AND   o3.state = 2
          AND	o3.oid = l_personOid;         -- Person
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;       -- valid exception
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrDefaults',
                'Error in get address of company');
        RAISE;
    END;           

    p_Order_01$retrieveAddresses (
             l_companyOid, ao_customerCompany, 
             ao_deliveryName, ao_paymentName, ao_deliveryAddress,
             ao_paymentAddress, ao_deliveryZip, ao_paymentZip, ao_deliveryTown,
             ao_paymentTown, ao_deliveryCountry, ao_paymentCountry);

    -- set the dates to today
    ao_voucherDate := SYSDATE;
    ao_deliveryDate := SYSDATE;
COMMIT WORK;
    -- return the state value
    RETURN  c_ALL_RIGHT;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrDefaults',
    ', userId = ' || ai_userId  ||
    ', catalogOid_s = ' || ai_catalogOid_s  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_ALL_RIGHT;     -- returns ALL_RIGHT in every case !!
END p_Order_01$retrDefaults;
/

show errors;


/******************************************************************************
 * set voucherdate to actual date. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             Id of the user who is getting the data.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Order_01$actOrderDate
(
    -- common input parameters:
    ai_oid_s            VARCHAR2,
    ai_userId           INTEGER,
    ai_op               INTEGER
)
RETURN INTEGER
AS

    -- define constants
    c_NOOID             CONSTANT RAW (8) := '0000000000000000';
    c_NOOID_S           CONSTANT VARCHAR2 (18)  := '0x0000000000000000';
    -- define return constants
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    -- define return values
    l_retValue              INTEGER := c_NOT_OK;       -- return value of this procedure
    -- define local variables
    l_oid                   RAW (8) := c_NOOID;

BEGIN

    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        UPDATE  m2_Order_01
        SET     voucherDate = SYSDATE 
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_retValue := c_NOT_OK;
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Order_01$actOrderDate',
                'Error in updating voucher date');
        RAISE;
    END;

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Order_01$actOrderDate',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_Order_01$actOrderDate;
/


EXIT;