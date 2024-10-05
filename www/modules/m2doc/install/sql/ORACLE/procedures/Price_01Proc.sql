/******************************************************************************
 * All stored procedures regarding the Price_01 table. <BR>
 *
 * @version     $Id: Price_01Proc.sql,v 1.5 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  990505
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId                ID of the user who is creating the object.
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

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Price_01$create
(
    -- common input parameters:
    ai_userid               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- common output parameters:
    ao_oid_s                OUT     VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                CONSTANT    INTEGER := 0;
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT    INTEGER := 21;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- local variables
    l_containerId           RAW(8);
    l_linkedObjectId        RAW(8);
    l_retValue              INTEGER := c_NOT_OK;
    l_oid                   RAW(8) := c_NOOID;
    l_categoryOid           RAW(8);

BEGIN
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    l_retValue := p_Object$performCreate (
                    ai_userid, ai_op, ai_tVersionId,
                    ai_name, ai_containerId_s, ai_containerKind,
                    ai_isLink, ai_linkedObjectId_s, ai_description,
                    ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
        -- create object type specific data:
        INSERT INTO m2_Price_01
                   (oid ,costCurrency ,cost,priceCurrency,price ,userValue1   
                    ,userValue2 ,validFrom,qty)
        VALUES  (l_oid, '', 0, '', 0, 0, 0, SYSDATE, 0);

        -- insert a tuple for every code category in the product
        BEGIN
            INSERT INTO m2_PriceCodeValues_01 (priceOid, categoryOid, validForAllValues)
            SELECT      l_oid, categoryOid, 0
            FROM        m2_ProductCodeValues_01 prcv, ibs_Object o, ibs_Object o2
            WHERE       o2.oid = l_containerId
                    AND o.oid = prcv.productOid
                    AND o2.containerId = o.oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
            WHEN OTHERS THEN
                l_retValue := c_NOT_OK; -- set return value
                ibs_error.log_error(ibs_error.error, 'p_Price_01$create','Error INSERT INTO');
                RAISE;
        END;

    END IF; -- if object created successfully
    
COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$change',
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', tVersionId: ' || ai_tVersionId ||
                          ', name: ' || ai_name ||
                          ', containerId_s: ' || ai_containerId_s ||
                          ', containerKind: ' || ai_containerKind ||
                          ', isLink: ' || ai_isLink ||
                          ', linkedObjectId_s: ' || ai_linkedObjectId_s ||
                          ', description: ' || ai_description ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;
END p_Price_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId                ID of the user who is changing the object.
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
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Price_01$change
(
    -- common input parameters:
    ai_oid_s            VARCHAR2,
    ai_userid           INTEGER,
    ai_op               INTEGER,
    ai_name             VARCHAR2,
    ai_validUntil       DATE,
    ai_description      VARCHAR2,
    ai_showInNews       INTEGER,
    -- type-specific input parameters:
    ai_costCurrency     VARCHAR2,
    ai_cost             NUMBER,
    ai_priceCurrency    VARCHAR2,
    ai_price            NUMBER,
    ai_userValue1       NUMBER,
    ai_userValue2       NUMBER,
    ai_validFrom        DATE,
    ai_qty              INTEGER
)   
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                CONSTANT        INTEGER := 0; 
    c_ALL_RIGHT             CONSTANT        INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT        INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT        INTEGER := 3;
    -- local variables
    l_retValue      INTEGER := c_NOT_OK;
    l_oid           RAW(8);

BEGIN
        -- perform the change of the object:
    l_retValue := p_Object$performChange (
                     ai_oid_s, ai_userid, ai_op, ai_name,
                     ai_validUntil, ai_description, ai_showInNews, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        -- update object type specific data:
        BEGIN
            UPDATE  m2_Price_01
            SET     costCurrency  = ai_costCurrency,
                    cost          = ai_cost,           
                    priceCurrency = ai_priceCurrency,
                    price         = ai_price, 
                    userValue1    = ai_userValue1,
                    userValue2    = ai_userValue2,    
                    validFrom     = ai_validFrom,    
                    qty           = ai_qty            
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_retValue := c_NOT_OK;
                ibs_error.log_error(ibs_error.error, 'p_Price_01$change','Error UPDATE');
                RAISE;                
        END;
    END IF;
    
    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$change',
                          ', oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', name: ' || ai_name ||
                          ', validUntil: ' || ai_validUntil ||
                          ', description: ' || ai_description ||
                          ', costCurrency: ' || ai_costCurrency ||
                          ', cost: ' || ai_cost ||
                          ', priceCurrency: ' || ai_priceCurrency ||
                          ', price: ' || ai_price ||
                          ', userValue1: ' || ai_userValue1 ||
                          ', userValue2: ' || ai_userValue2 ||
                          ', validFrom: ' || ai_validFrom ||
                          ', qty: ' || ai_qty ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;

END p_Price_01$change;
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
 * @param   @prop1              Description of the first property.
 * @param   @prop2              Description of the second property.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Price_01$retrieve
(
    -- common input parameters:
    ai_oid_s            VARCHAR2,
    ai_userid           INTEGER,
    ai_op               INTEGER,
    -- common output parameters:
    ao_state            OUT     INTEGER,
    ao_tVersionId       OUT     INTEGER,
    ao_typeName         OUT     VARCHAR2,
    ao_name             OUT     VARCHAR2,
    ao_containerId      OUT     RAW,
    ao_containerName    OUT     VARCHAR2,
    ao_containerKind    OUT     INTEGER,
    ao_isLink           OUT     NUMBER,
    ao_linkedObjectId   OUT     RAW,
    ao_owner            OUT     INTEGER,
    ao_ownerName        OUT     VARCHAR2,
    ao_creationDate     OUT     DATE,
    ao_creator          OUT     INTEGER,
    ao_creatorName      OUT     VARCHAR2,
    ao_lastChanged      OUT     DATE,
    ao_changer          OUT     INTEGER,
    ao_changerName      OUT     VARCHAR2,
    ao_validUntil       OUT     DATE,
    ao_description      OUT     VARCHAR2,
    ao_showInNews       OUT     INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    -- type-specific output attributes:
    ao_costCurrency     OUT     VARCHAR2,
    ao_cost             OUT     NUMBER,
    ao_priceCurrency    OUT     VARCHAR2,
    ao_price            OUT     NUMBER,
    ao_userValue1       OUT     NUMBER,
    ao_userValue2       OUT     NUMBER,
    ao_validFrom        OUT     DATE,
    ao_qty              OUT     INTEGER,
    ao_productOid_s     OUT     RAW
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                CONSTANT    INTEGER := 0; 
    c_ALL_RIGHT             CONSTANT    INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    -- local variables
    l_retValue          INTEGER := c_NOT_OK;               -- return value of this procedure
    l_oid               RAW(8);
    l_productOid        RAW(8);

BEGIN
        -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (
                ai_oid_s, ai_userid, ai_op,
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
        -- retrieve object type specific data:
        SELECT  costCurrency, cost, priceCurrency,
                price, userValue1, userValue2,     
                validFrom, qty  
        INTO    ao_costCurrency, ao_cost, ao_priceCurrency,
                ao_price, ao_userValue1, ao_userValue2,
                ao_validFrom, ao_qty
        FROM    m2_Price_01 pr
        WHERE   pr.oid = l_oid;

		-- select the product which is the container of the
		-- container            
        BEGIN
            SELECT  o.containerId
		    INTO    l_productOid
		    FROM    ibs_Object o
            WHERE   o.oid = ao_containerId;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_retValue := c_NOT_OK;
                ibs_error.log_error(ibs_error.error, 'p_Price_01$retrieve','Error SELECT o.container');
                RAISE;                
        END;
        p_ByteToString (l_productOid, ao_productOid_s);
    END IF;

    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$change',
                          ', oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );

    RETURN c_NOT_OK;
END p_Price_01$retrieve;
/

show errors;


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId                ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Price_01$BOCopy
(
    ai_oid              RAW,
    ai_userid           INTEGER,
    ai_newOid           RAW
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK            CONSTANT        INTEGER := 0; 
    c_ALL_RIGHT         CONSTANT        INTEGER := 1;
    -- local variables
    l_retValue          INTEGER := c_ALL_RIGHT;
    l_costCurrency      VARCHAR2(5);
    l_cost              NUMBER;
    l_priceCurrency     VARCHAR2(5);
    l_price             NUMBER;
    l_userValue1        NUMBER;
    l_userValue2        NUMBER;
    l_validFrom         DATE;
    l_qty               INTEGER;
    l_categoryOid       RAW(8); 
    l_validForAllValues NUMBER; 
    l_codeValues        VARCHAR2(255);

BEGIN

    -- not implemented yet
    -- make an insert for all type specific tables:
    BEGIN
        SELECT  costCurrency, cost, priceCurrency, price, 
                userValue1, userValue2, validFrom, qty
        INTO    l_costCurrency, l_cost, l_priceCurrency, 
                l_price, l_userValue1, l_userValue2, 
                l_validFrom, l_qty
        FROM    m2_Price_01
        WHERE   oid = ai_oid;    
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_retValue := c_NOT_OK; -- set return value
            ibs_error.log_error(ibs_error.error, 'p_Price_01$BOCopy','Error SELECT costCurreny ...');
            RAISE;
    END;
    INSERT  INTO m2_Price_01
            (oid, costCurrency, cost, priceCurrency, price, 
             userValue1, userValue2, validFrom, qty    
            )
    VALUES  (ai_newOid, l_costCurrency, l_cost, l_priceCurrency, l_price, 
            l_userValue1, l_userValue2, l_validFrom, l_qty);
    
    -- insert the code values of the price
    BEGIN
        SELECT      categoryOid, validForAllValues, codeValues
        INTO        l_categoryOid, l_validForAllValues, l_codeValues
        FROM        m2_PriceCodeValues_01
        WHERE       priceOid = ai_oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_retValue := c_NOT_OK; -- set return value
            ibs_error.log_error(ibs_error.error, 'p_Price_01$BOCopy','Error SELECT categoryOid ...');
            RAISE;
    END;    
    INSERT INTO m2_PriceCodeValues_01 (priceOid, categoryOid, validForAllValues, codeValues)
    VALUES      (ai_newOid, l_categoryOid, l_validForAllValues, l_codeValues);

    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$change',
                          ', oid: ' || ai_oid ||
                          ', userid: ' || ai_userid ||
                          ', newOid: ' || ai_newOid ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;
END p_Price_01$BOCopy;
/

show errors;

EXIT;
