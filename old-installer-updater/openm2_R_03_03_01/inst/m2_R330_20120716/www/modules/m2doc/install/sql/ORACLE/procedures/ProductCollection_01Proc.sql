/******************************************************************************
 * All stored procedures regarding the m2_Catalog_01 table. <BR>
 * 
 * @version     $Id: ProductCollection_01Proc.sql,v 1.6 2003/10/31 00:13:15 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  990507
 ******************************************************************************
 */

/******************************************************************************
 *
 * Creates a ProductCollection Object.
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductCollect_01$create
(
    -- input parameters
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS

    -- constants
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    c_NOT_OK                CONSTANT    INTEGER := 0;
    -- local variables
    l_oid                   RAW(8);
    l_retValue              INTEGER := c_NOT_OK;
    l_containerId           RAW (8);
    l_linkedObjectId        RAW (8);

BEGIN 
        p_stringToByte (ai_containerId_s, l_containerId);
        p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

        l_retValue := p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, 
                ai_name, ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s, 
                ai_description, ao_oid_s, l_oid);

        IF (l_retValue = c_ALL_RIGHT)
        THEN
            -- Insert the other values
             INSERT INTO m2_ProductCollection_01 (oid, validFrom)
             VALUES (l_oid, SYSDATE);
        END IF;

COMMIT WORK;
    -- return the state value
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductCollect_01$create',
    ', userId = ' || ai_userId  ||
    ', tVersionId = ' || ai_tVersionId  ||
    ', name = ' || ai_name  ||
    ', containerId = ' || ai_containerId_s  ||
    ', containerKind = ' || ai_containerKind  ||
    ', isLink = ' || ai_isLink  ||
    ', linkedObjectIds = ' || ai_linkedObjectId_s  ||
    ', description = ' || ai_description  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_ProductCollect_01$create;
/

show errors;

/******************************************************************************
 *
 * Changes a ProductCollection Object.
 *
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */


-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductCollect_01$change
(
    -- input parameters:
    ai_oid_s            VARCHAR2,
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_name             VARCHAR2,
    ai_validUntil       DATE,
    ai_description      VARCHAR2,
    ai_showInNews       INTEGER,
    ---- attributes of object attachment ---------------
    ai_cost             NUMBER,       
    ai_costCurrency     VARCHAR2,
    ai_totalQuantity    INTEGER,
    ai_validFrom        DATE,
    ai_categoryOidX_s   VARCHAR2,
    ai_categoryOidY_s   VARCHAR2,
    ai_nrCodes          INTEGER
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    c_NOT_OK                CONSTANT    INTEGER := 0;
    -- local variables
    l_oid                   RAW(8);
    l_retValue              INTEGER := c_NOT_OK;

    l_oldImage              VARCHAR2 (63);
    l_categoryOidX          RAW (8);
    l_categoryOidY          RAW (8);

BEGIN

    p_StringToByte (ai_categoryOidX_s, l_categoryOidX);
    p_StringToByte (ai_categoryOidY_s, l_categoryOidY);

    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
                ai_validUntil, ai_description, ai_showInNews, l_oid);

        -- operation properly performed?
        IF (l_retValue = c_ALL_RIGHT)
        THEN
            -- update other values
            UPDATE  m2_ProductCollection_01
            SET     cost = ai_cost,
                    costCurrency = ai_costCurrency,
                    validFrom = ai_validFrom,
                    totalQuantity = ai_totalQuantity,
                    categoryOidX = l_categoryOidX,
                    categoryOidY = l_categoryOidY,
                    nrCodes = ai_nrCodes
            WHERE   oid = l_oid;
        END IF;-- if operation properly performed

COMMIT WORK;
    -- return the state value
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductCollect_01$change',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;

END p_ProductCollect_01$change;
/

show errors;

/******************************************************************************
 * Retrieves a ProductCollection Object.
 * @param   ao_showInNews         flag if object should be shown in newscontainer
 * @param   ao_checkedOut         Is the object checked out?
 * @param   ao_checkOutDate       Date when the object was checked out
 * @param   ao_checkOutUser       id of the user which checked out the object
 * @param   ao_checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductCollect_01$retrieve
(
    -- input parameters:
    ai_oid_s            VARCHAR2,
    ai_userId           INTEGER,
    ai_op               INTEGER,

    -- output parameters
    ao_state            OUT              INTEGER,
    ao_tVersionId       OUT              INTEGER,
    ao_typeName         OUT              VARCHAR2,
    ao_name             OUT              VARCHAR2,
    ao_containerId      OUT              RAW,
    ao_containerName    OUT              VARCHAR2,
    ao_containerKind    OUT              INTEGER,
    ao_isLink           OUT              NUMBER,
    ao_linkedObjectId   OUT              RAW,
    ao_owner            OUT              INTEGER,
    ao_ownerName        OUT              VARCHAR2, --name of the Creat
    ao_creationDate     OUT              DATE,
    ao_creator          OUT              INTEGER,
    ao_creatorName      OUT              VARCHAR2, --name of the Creator
    ao_lastChanged      OUT              DATE,
    ao_changer          OUT              INTEGER,
    ao_changerName      OUT              VARCHAR2, --name of te Changer
    ao_validUntil       OUT              DATE,
    ao_description      OUT              VARCHAR,
    ao_showInNews       OUT              INTEGER,
    ao_checkedOut       OUT              NUMBER,
    ao_checkOutDate     OUT              DATE,
    ao_checkOutUser     OUT              INTEGER,
    ao_checkOutUserOid  OUT              RAW,
    ao_checkOutUserName OUT              VARCHAR2,
    -----specific outputdata of ProductBrand--------------
    ao_productOid       OUT              RAW,
    ao_nrCodes          OUT              INTEGER,
    ao_cost             OUT              Number,
    ao_costCurrency     OUT              VARCHAR2,
    ao_totalQuantity    OUT              INTEGER,
    ao_validFrom        OUT              DATE,
    ao_categoryOidX     OUT              RAW,
    ao_categoryOidY     OUT              RAW,
    ao_nrCodes2         OUT              INTEGER
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    c_NOT_OK                CONSTANT    INTEGER := 0;
    -- local variables
    l_oid         RAW(8);
    l_retValue    INTEGER;
BEGIN

    l_retValue := p_Object$performRetrieve(
            ai_oid_s, ai_userId, ai_op,
            ao_state, ao_tVersionId, ao_typeName, ao_name,
            ao_containerId, ao_containerName, ao_containerKind,
            ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
            ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName,
            ao_validUntil, ao_description, ao_showInNews, 
            ao_checkedOut, ao_checkOutDate, 
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
            l_oid);
    

    -- operation properly performed?
    IF (l_retValue = c_ALL_RIGHT)
    THEN
       -----------------------specific table outread---------------------
        SELECT      containerId
        INTO        ao_productOid
        FROM        ibs_Object
        WHERE       oid =  ao_containerId;
            
        -- select the number of codes
        SELECT      COUNT (*)
        INTO        ao_nrCodes
        FROM        m2_ProfileCategory_01 pc, m2_Product_01 p
        WHERE       p.oid = ao_productOid
          AND       pc.productProfileOid = p.productProfileOid;
                    
        -- select values from collection table
        SELECT      cost, costCurrency, totalQuantity, validFrom,
                    categoryOidX, categoryOidY, nrCodes
        INTO        ao_cost, ao_costCurrency, ao_totalQuantity,
                    ao_validFrom, ao_categoryOidX, ao_categoryOidY,
                    ao_nrCodes2
        FROM        m2_ProductCollection_01
        WHERE       oid = l_oid;

        SELECT DISTINCT     SUM (quantity)
        INTO                ao_totalQuantity
        FROM                v_ProductCollection$content
        WHERE               collectionOid = l_oid
        GROUP BY            categoryname;
    END IF;-- if operation properly performed

COMMIT WORK;
    -- return the state value
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductCollect_01$retrieve',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_ProductCollect_01$retrieve;
/

show errors;

/******************************************************************************
 *
 * Creates a new quantity entry in a product collection.
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 *
 */
CREATE OR REPLACE FUNCTION p_ProductCollect_01$createQty
(
    -- input parameters:
    ai_collectionOid_s      VARCHAR2,
    ai_quantity             INTEGER,
    -- output parameters:
    ao_id                   OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    c_NOT_OK                CONSTANT    INTEGER := 0;
    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_collectionOid         RAW(8);

BEGIN
    p_StringToByte (ai_collectionOid_s, l_collectionOid);

    -- select a new Id
    SELECT  DECODE (MAX(id),NULL, 1, MAX(id)+1)
    INTO    ao_id
    FROM    m2_ProductCollectionQty_01;
    
    -- insert a new tuple
    INSERT INTO m2_ProductCollectionQty_01 (id, collectionOid, quantity)
    VALUES    (ao_id, l_collectionOid, ai_quantity);

COMMIT WORK;
    RETURN l_retValue;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, '_ProductCollect_01$createQty',
    ', collectionOid_s = ' || ai_collectionOid_s  ||
    ', quantity = ' || ai_quantity  ||
    ', id = ' || ao_id  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_ProductCollect_01$createQty;
/

show errors;

/******************************************************************************
 *
 * Creates a new value entry in a product collection.
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */
CREATE OR REPLACE FUNCTION p_ProductCollect_01$createVal
(
    -- input parameters:
    ai_id               INTEGER,   
    ai_categoryOid_s    VARCHAR2,
    ai_value            VARCHAR2
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    c_NOT_OK                CONSTANT    INTEGER := 0;
    -- local variables:
    l_retValue          INTEGER := c_ALL_RIGHT;
    l_categoryOid       RAW (8);
    
BEGIN
    p_StringToByte (ai_categoryOid_s, l_categoryOid);
    -- insert a new tuple
    INSERT INTO m2_ProductCollectionValue_01 (id, categoryOid, value)
    VALUES    (ai_id, l_categoryOid, ai_value);
    
COMMIT WORK;
    RETURN l_retValue;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductCollect_01$createVal',
    ', oid = ' || ai_id  ||
    ', categoryOid_s = ' || ai_categoryOid_s  ||
    ', value = ' || ai_value  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_ProductCollect_01$createVal;
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
CREATE OR REPLACE FUNCTION p_ProductCollect_01$BOCopy
(
    -- common input parameters:
    ai_oid            RAW,
    ai_userId         RAW,
    ai_newOid         RAW
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    c_NOT_OK                CONSTANT    INTEGER := 0;
    -- local variables:
    l_retValue          INTEGER := c_NOT_OK;

BEGIN
    -- make an insert for all type specific tables:
    INSERT  INTO m2_ProductCollection_01
            (oid, cost, costCurrency, totalQuantity, validFrom, categoryOidX, categoryOidY, nrCodes
            )
    SELECT  ai_newOid,  cost, costCurrency, totalQuantity, 
            validFrom, categoryOidX, categoryOidY, nrCodes
    FROM    m2_ProductCollection_01
    WHERE   oid = ai_oid;
    
    IF (SQL%ROWCOUNT >= 1)          -- at least one row affected?
    THEN
        l_retValue := c_ALL_RIGHT;  -- set return value
    END IF;

    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductCollect_01$BOCopy',
    ', oid = ' || ai_oid  ||
    ', userId = ' || ai_userId  ||
    ', newOid = ' || ai_newOid  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_ProductCollect_01$BOCopy;
/

show errors;

EXIT;
