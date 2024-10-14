/******************************************************************************
 * All Procedures regarding to the m2_Order_01 table.
 *
 * @version     $Id: Order_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW)  980924
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

-- delete existing procedure:
IF EXISTS (
            SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id('#CONFVAR.ibsbase.dbOwner#.p_Order_01$create') 
                AND sysstat & 0xf = 4
            )
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Order_01$create
GO
-- create the new procedure:
CREATE PROCEDURE p_Order_01$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @state          INT,
    @description    DESCRIPTION
    ---- specialized attributes of object Order ---------------
    ,@voucherNo         NAME
    ,@voucherDate       DATETIME
    ,@supplierCompany   NAME
    ,@contactSupplier   NAME
    ,@customerCompany   NAME
    ,@contactCustomer   NAME
    ,@deliveryAddress   DESCRIPTION
    ,@paymentAddress    DESCRIPTION
-- AJ 990518 changed ... //////////////////////////////////////////////////////
    ,@description1      DESCRIPTION
    ,@description2      DESCRIPTION
    ,@description3      DESCRIPTION
    ,@deliveryDate      DATETIME
    ,@catalogOid_s      OBJECTIDSTRING
    ,@ai_paymentOid_s   OBJECTIDSTRING
    ,@ai_cc_owner       NVARCHAR(200)
    ,@ai_cc_number      NAME
    ,@ai_cc_expmonth    NAME
    ,@ai_cc_expyear     NAME
    -- output parameters:
    ,@oid_s             OBJECTIDSTRING OUTPUT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    -- declare additional variables
    DECLARE
        @oldState INT

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
    -- declare local variables
    DECLARE @oid            OBJECTID
        ,@catalogOid        OBJECTID
        ,@shoppingCartOid   OBJECTID
        ,@ordersOid         OBJECTID
        ,@ordersOid_s       OBJECTIDSTRING
        ,@l_paymentOid      OBJECTID
        
    -- convert to byte 
    EXEC p_stringToByte @catalogOid_s, @catalogOid OUTPUT
    EXEC p_stringToByte @ai_paymentOid_s, @l_paymentOid OUTPUT
    -- get the shopping cart
    SELECT  @shoppingCartOid = shoppingCart, @ordersOid = orders
    FROM    ibs_Workspace
-- AJ 990305 changed ... //////////////////////////////////////////////////////
    WHERE   userId = @userId
-- ... AJ 990305 changed ... //////////////////////////////////////////////////
/*
    WHERE   uid = @userId
*/
-- ... AJ 990305 changed //////////////////////////////////////////////////////
    EXEC p_ByteToString @ordersOid, @ordersOid_s OUTPUT


    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION
        -- create order object in the order container
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId,
                            @name, @ordersOid_s, 1,
                            0, '0x00' , @description,
                            @oid_s OUTPUT, @oid OUTPUT


        IF @retValue = @ALL_RIGHT
        BEGIN
            -- update other values
            INSERT INTO  m2_Order_01
                    (oid, voucherNo,voucherDate, supplierCompany, contactSupplier,
                    customerCompany, contactCustomer, deliveryAddress, paymentAddress,
                    description1, description2, description3, deliveryDate,
                    paymentOid, cc_number, cc_expmonth, cc_expyear,
                    cc_owner, catalogOid)
                   
            VALUES  (@oid, @voucherNo, @voucherDate, @supplierCompany, @contactSupplier,
                    @customerCompany, @contactCustomer, @deliveryAddress, @paymentAddress,
                    @description1, @description2, @description3, @deliveryDate,
                    @l_paymentOid, @ai_cc_number, @ai_cc_expmonth, @ai_cc_expyear,
                    @ai_cc_owner, @catalogOid)
                   
            -- move the product from the shopping cart to the order
            UPDATE  ibs_Object
            SET     containerId = @oid        
                    ,tversionId = 0x01011801
                    ,state = 2
            FROM    ibs_Object o, m2_ShoppingCartEntry_01 v
            WHERE   v.oid = o.oid
              AND   o.containerId = @shoppingCartOid
              AND   o.state = 2
              AND   v.catalogOid = @catalogOid

            -- UPDATE state of the order
            EXEC @retValue = p_Object$changeState @oid_s, @userId, @op, @state

        END

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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Order_01$retrieve')
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Order_01$retrieve
GO
-- create the new procedure:
CREATE PROCEDURE p_Order_01$retrieve
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
    @ownerName      NVARCHAR(200)   OUTPUT,
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
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT
    -- type-specific output attributes:
    ,@voucherNo         NAME        OUTPUT
    ,@voucherDate       DATETIME    OUTPUT
    ,@supplierCompany   NAME        OUTPUT
    ,@contactSupplier   NAME        OUTPUT
    ,@customerCompany   NAME        OUTPUT
    ,@contactCustomer   NAME        OUTPUT
    ,@deliveryAddress   DESCRIPTION OUTPUT
    ,@paymentAddress    DESCRIPTION OUTPUT
    ,@supplierAddress   DESCRIPTION OUTPUT
    ,@description1      DESCRIPTION OUTPUT
    ,@description2      DESCRIPTION OUTPUT
    ,@description3      DESCRIPTION OUTPUT
    ,@deliveryDate      DATETIME    OUTPUT
    ,@orderRespOid_s    OBJECTIDSTRING  OUTPUT
    ,@ao_paymentOid_s   OBJECTIDSTRING  OUTPUT
    ,@ao_paymentTypeId  INTEGER         OUTPUT
    ,@ao_paymentName    NAME            OUTPUT
    ,@ao_cc_owner       NVARCHAR(200)   OUTPUT
    ,@ao_cc_number      NAME            OUTPUT
    ,@ao_cc_expmonth    NAME            OUTPUT
    ,@ao_cc_expyear     NAME            OUTPUT
    ,@eMailOrderResp    NAME            OUTPUT
    ,@eMailCurrentUser  NAME            OUTPUT
    ,@ao_catalogOid_s   OBJECTIDSTRING  OUTPUT
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
    DECLARE @l_catalogOid   OBJECTID
    DECLARE @orderRespOid   OBJECTID
    DECLARE @l_paymentOid   OBJECTID
   
    -- initialize local variables:
    EXEC p_stringToByte @oid_s, @oid OUTPUT

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
                @checkedOut OUTPUT, @checkOutDate OUTPUT,                 @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
                @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN            -- retrieve object type specific data:
            -- get object specific data
	        SELECT  
                    @voucherNo      = voucherNo,     
                    @voucherDate    = voucherDate,    
                    @supplierCompany= supplierCompany,
                    @contactSupplier= contactSupplier,
                    @customerCompany= customerCompany,
                    @contactCustomer= contactCustomer,
                    @deliveryAddress= deliveryAddress,
                    @paymentAddress = paymentAddress, 
                    @description1   = description1,   
                    @description2   = description2,   
                    @description3   = description3,   
                    @deliveryDate   = deliveryDate,
                    @l_paymentOid   = paymentOid,
                    @ao_cc_number   = cc_number,
                    @ao_cc_expmonth = cc_expmonth,
                    @ao_cc_expyear  = cc_expyear,
                    @ao_cc_owner    = cc_owner,
                    @l_catalogOid   = catalogOid
    
            FROM    m2_Order_01
            WHERE   oid = @oid

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
            EXEC p_ByteToString @l_catalogOid, @ao_catalogOid_s OUTPUT
            EXEC p_ByteToString @l_paymentOid, @ao_paymentOid_s OUTPUT          
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- get catalogOid
    SELECT DISTINCT @l_catalogOid = catalogOid
    FROM ibs_Object o, m2_ShoppingCartEntry_01 s
    WHERE s.oid = o.oid
    AND o.tVersionId = 16848897 -- Bestellposition/Part of Order
    AND o.containerId = @oid

    EXEC p_ByteToString @l_catalogOid, @ao_catalogOid_s OUTPUT

    -- retrieve the orderResponsibleOid out of the db
	SELECT  @orderRespOid = ordResp
    FROM    m2_Catalog_01
	WHERE   oid =  @l_catalogOid
    -- convert the oids to strings
    EXEC p_ByteToString @orderRespOid, @orderRespOid_s OUTPUT
	

    DECLARE @addressOid OBJECTID, @companyOid OBJECTID

    -- get addressOid of Company
    SELECT  @addressOid = o.oid, 
            @companyOid = c.companyOid
    FROM    m2_Catalog_01 c, ibs_Object o
    WHERE   c.oid = @l_catalogOid
      AND   o.containerId = c.companyOid
    AND     o.tVersionId = 16854785      -- Tab - address

    -- get name of company
    SELECT  @supplierCompany = name
    FROM    ibs_Object
    WHERE   oid = @companyOid
    
    -- get the full-address of company
    SELECT  @supplierAddress = street + N';'  -- add delimiter ; to string
             + country + N' ' + zip + N' ' + town
    FROM    m2_Address_01
    WHERE   oid = @addressOid
    
    IF (@@ROWCOUNT = 0)
        SELECT @supplierAddress = N''
        
    -- get name of payment type
    
    SELECT  @ao_paymentName = name
    FROM    ibs_object
    WHERE   oid = @l_paymentOid    
    
    -- get the id of the payment type
    
    SELECT  @ao_paymentTypeId = paymentTypeId
    FROM    m2_PaymentType_01
    WHERE   oid = @l_paymentOid 

    -- get email of orderResponsible
    SELECT   @eMailOrderResp = a.email 
    FROM     ibs_User u,
             ibs_UserProfile p, 
             ibs_Object tabAddress, 
             ibs_UserAddress_01 a 
    WHERE    p.userid = u.id
        AND  u.oid = @orderRespOid
        AND  p.oid = tabAddress.containerId
        AND  tabAddress.oid = a.oid



    -- get fullname, personOid linked to the current user and the emailAddress
    -- off current user
    SELECT   @eMailCurrentUser = a.email
    FROM     ibs_User u,
             ibs_UserProfile p, 
             ibs_Object tabAddress, 
             ibs_UserAddress_01 a
    WHERE   u.id = @userId 
       AND  p.userid = u.id
       AND  p.oid = tabAddress.containerId
       AND  tabAddress.oid = a.oid
   
   
 -- return the state value:
    RETURN  @retValue
GO

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
EXEC p_dropProc N'p_Order_01$retrDefaults'
GO

-- create the new procedure:
CREATE PROCEDURE p_Order_01$retrDefaults
(
    @userId             USERID,
    @catalogOid_s       OBJECTIDSTRING,
    -- output parameters
    @voucherNo          NAME            OUTPUT,  
    @voucherDate        DATETIME        OUTPUT,
    @supplierCompany    NAME            OUTPUT,
    @contactSupplier    NAME            OUTPUT,
    @orderRespOid_s     OBJECTIDSTRING  OUTPUT,
    @orderRespMediaOid_s OBJECTIDSTRING OUTPUT,
    @customerCompany    NAME            OUTPUT,
    @contactCustomer    NAME            OUTPUT,
    @deliveryName       NAME            OUTPUT,
    @deliveryAddress    NAME            OUTPUT,
    @deliveryZIP        NAME            OUTPUT,
    @deliveryTown       NAME            OUTPUT,
    @deliveryCountry    NAME            OUTPUT,
    @paymentName        NAME            OUTPUT,
    @paymentAddress     NAME            OUTPUT,
    @paymentZIP         NAME            OUTPUT,
    @paymentTown        NAME            OUTPUT,
    @paymentCountry     NAME            OUTPUT,
    @description1       DESCRIPTION     OUTPUT,
    @description2       DESCRIPTION     OUTPUT,
    @description3       DESCRIPTION     OUTPUT,
    @deliveryDate       DATETIME        OUTPUT,
    @eMailOrderResp     NAME            OUTPUT,
    @eMailCurrentUser   NAME            OUTPUT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID
        ,@catalogOid        OBJECTID
        ,@orderRespOid      OBJECTID
        ,@orderRespMediaOid OBJECTID
        ,@personOid         OBJECTID
        ,@companyOid        OBJECTID
--        ,@help_oid          OBJECTID
    -- convert to byte 
--    EXEC p_stringToByte @oid_s, @help_oid OUTPUT
    EXEC p_stringToByte @catalogOid_s, @catalogOid OUTPUT


    -- initialize output paramters
    SELECT @customerCompany = N''

    -- retrieve the supplier data out of the catalog
	SELECT  @supplierCompany = co.name + N' ' + co.legal_form,
            @contactSupplier = u.fullname,
            @orderRespOid = ca.ordResp,
            @orderRespMediaOid = ca.ordRespMed,
            @description1 = ca.description1, 
            @description2 = ca.description2,
            @description3 = N'' 
    FROM    m2_Catalog_01 ca
            LEFT OUTER JOIN    
                (
                    SELECT c.legal_form, c.oid, o.name
                    FROM    mad_Company_01 c, ibs_Object o
                    WHERE   c.oid = o.oid
                        AND o.state = 2
                ) co
            ON  co.oid = ca.companyOid
            LEFT OUTER JOIN
                (
                    SELECT oid, fullname
                    FROM    ibs_User
                    WHERE   state = 2
                ) u
            ON      u.oid = ca.ordResp

	WHERE   ca.oid =  @catalogOid

    -- convert the oids to strings
    EXEC p_ByteToString @orderRespOid, @orderRespOid_s OUTPUT
    EXEC p_ByteToString @orderRespMediaOid, @orderRespMediaOid_s OUTPUT

    -- get email of orderResponsible
    SELECT   @eMailOrderResp = a.email 
    FROM     ibs_User u,
             ibs_UserProfile p, 
             ibs_Object tabAddress, 
             ibs_UserAddress_01 a 
    WHERE    p.userid = u.id
        AND  u.oid = @orderRespOid
        AND  p.oid = tabAddress.containerId
        AND  tabAddress.oid = a.oid


    -- get oid of person linked to user
    -- it is possible that more than one person is linked to the user, in
    -- this case, the first one is taken  (to provide old m2-systems)
    SELECT  @personOid = min (per.oid)
    FROM    ibs_User u,
            ibs_Object oper,
            mad_Person_01 per 
    WHERE   u.id = @userId 
       AND  u.oid = per.useroid
       AND  oper.oid = per.oid
       AND  oper.state = 2
    
    -- get fullname and the emailAddress
    -- off current user
    SELECT  @contactCustomer = u.fullname, 
            @eMailCurrentUser = a.email
    FROM     ibs_User u,
             ibs_UserProfile p, 
             ibs_Object tabAddress, 
             ibs_UserAddress_01 a
    WHERE   u.id = @userId 
       AND  p.userid = u.id
       AND  p.oid = tabAddress.containerId
       AND  tabAddress.oid = a.oid
  
    -- get the users company
    SELECT DISTINCT @companyOid = o1.oid, @customerCompany = o1.name + N' ' + c.legal_form
    FROM    ibs_Object o1
            JOIN    mad_Company_01 c
            ON      c.oid = o1.oid              -- Company
            AND     o1.state = 2
            JOIN    ibs_Object o2
            ON      o2.containerId = o1.oid     -- Tab Contacts - Company
            AND     o2.state = 2
            JOIN    ibs_Object o3
            ON      o3.containerId = o2.oid     -- Person - Tab Contacts
            AND	    o3.state = 2
            AND	    o3.oid = @personOid         -- Person

    EXEC p_Order_01$retrieveAddresses
             @companyOid, @customerCompany, 
             @deliveryName OUTPUT, @paymentName OUTPUT, @deliveryAddress OUTPUT,
             @paymentAddress OUTPUT, @deliveryZip OUTPUT, @paymentZip OUTPUT, @deliveryTown OUTPUT,
             @paymentTown OUTPUT, @deliveryCountry OUTPUT, @paymentCountry OUTPUT

    -- set the dates to today
    SELECT  @voucherDate = getDate(), @deliveryDate = getDate()
    -- return the state value
    RETURN  1
GO

-- p_Order_01$retrDefaults

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
IF EXISTS ( SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Order_01$delete')
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Order_01$delete
GO

CREATE PROCEDURE p_Order_01$delete
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
            DELETE  m2_Order_01
            WHERE   oid = @oid
            -- check if deletion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue

GO

-- p_Order_01$delete

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
-- delete existing procedure:
IF EXISTS ( SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Order_01$actOrderDate')
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Order_01$actOrderDate
GO

-- create the new procedure:
CREATE PROCEDURE p_Order_01$actOrderDate
(
    -- common input parameters:
    @ai_oid_s           OBJECTIDSTRING,
    @ai_userId          USERID,
    @ai_op              INT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @l_oid OBJECTID
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

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

    UPDATE  m2_Order_01
    SET     voucherDate = getDate ()
    WHERE   oid = @l_oid

    -- check if update was performed properly
    IF (@@ROWCOUNT <= 0) -- no row affected?
        SELECT @retValue = @NOT_OK  -- set return value
    
    -- return the state value:
    RETURN  @retValue

GO
-- p_Order_01$actOrderDate