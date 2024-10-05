/******************************************************************************
 * All stored procedures regarding the PaymentType table. <BR>
 * 
 * @version     $Id: PaymentType_01Proc.sql,v 1.2 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Daniel Janesch (DJ)  001123
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId               ID of the user who is creating the object.
 * @param   ai_op                   Operation to be performed (used for rights 
 *                                  check).
 * @param   ai_tVersionId           Type of the new object.
 * @param   ai_name                 Name of the object.
 * @param   ai_containerId_s        ID of the container where object shall be 
 *                                  created in.
 * @param   ai_containerKind        Kind of object/container relationship
 * @param   ai_isLink               Defines if the object is a link
 * @param   ai_linkedObjectId_s     If the object is a link this is the ID of
 *                                  the where the link shows to.
 * @param   ai_description          Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s                OID of the newly created object.
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                       Action performed, values returned,
 *                                  everything ok.
 *  INSUFFICIENT_RIGHTS             User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_PaymentType_01$create
(
    -- common input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- common output parameters:
    ao_oid_s                OUT VARCHAR2
) RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
                                            -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; 
                                            -- return value of this function
    l_oid                   RAW (8) := c_NOOID; -- the actual oid

-- body:
BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                        ai_name, ai_containerId_s, ai_containerKind,
                        ai_isLink, ai_linkedObjectId_s, ai_description,
                        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- operation performed properly?
    THEN
        -- insert default values
        INSERT INTO m2_PaymentType_01
                    (oid, paymentTypeId, name)
        VALUES      (l_oid, -1, '');
    END IF;  -- if operation performed properly
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error (ibs_error.error, 'p_PaymentType_01$create',
    ', userId = ' || ai_userId  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_PaymentType_01$create;
/

show errors;
-- p_PaymentType_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_id               ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_showInNews       Shall the currrent object be displayed in the 
 *                              news?
 * @param   ai_paymentTypeId    Id of the payment type.
 *
 * @output parameters:
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                   Action performed, values returned,
 *                              everything ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 *  OBJECTNOTFOUND              The required object was not found within the 
 *                              database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_PaymentType_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           NUMBER,
    -- type-specific input parameters:
    ai_paymentTypeId        INTEGER
) RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
                                            -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; 
                                            -- return value of this function
    l_oid                   RAW (8) := c_NOOID; -- the actual oid

    -- body:
BEGIN
    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
            ai_validUntil, ai_description, ai_showInNews, l_oid);

    IF (l_retValue = c_ALL_RIGHT)           -- operation performed properly?
    THEN
        -- set the new payment type id:
        UPDATE  m2_PaymentType_01
        SET     paymentTypeId = ai_paymentTypeId,
                name = ai_name
        WHERE   oid = l_oid;
    END IF; -- if operation performed properly
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error (ibs_error.error, 'p_PaymentType_01$change',
    ', userId = ' || ai_userId  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_PaymentType_01$change;
/

show errors;
-- p_PaymentType_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @param   ai_oid_s            ID of the object to be retrieved.
 * @param   ai_userId           Id of the user who is getting the data.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerName    Name of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_ownerName        Name of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_creatorName      Name of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_changerName      Name of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       The showInNews flag.
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out.
 * @param   ao_checkOutUser     Oid of the user which checked out the object.
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object.
 *                              is only set if this user has the right to READ
 *                              the checkOut user.
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to read
 *                              the checkOut-User.
 * @param   ao_paymentTypeId    Id of the payment type.
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                   Action performed, values returned,
 *                              everything ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 *  OBJECTNOTFOUND              The required object was not found within the 
 *                              database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_PaymentType_01$retrieve
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- common output parameters:
    ao_state                OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_typeName             OUT VARCHAR2,
    ao_name                 OUT VARCHAR2,
    ao_containerId          OUT RAW,
    ao_containerName        OUT VARCHAR2,
    ao_containerKind        OUT INTEGER,
    ao_isLink               OUT NUMBER,
    ao_linkedObjectId       OUT RAW,
    ao_owner                OUT INTEGER,
    ao_ownerName            OUT VARCHAR2,
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2,
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2,
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT NUMBER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,
    -- type-specific output parameters:
    ao_paymentTypeId        OUT INTEGER
) RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
                                            -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; 
                                            -- return value of this function
    l_oid                   RAW (8):= c_NOOID; -- the actual oid

    -- body:
BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op,
            ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
            ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId, 
            ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
            ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);

    IF (l_retValue = c_ALL_RIGHT)           -- operation performed properly?
    THEN
        -- retrieve object type specific data:
        SELECT  paymentTypeId
        INTO    ao_paymentTypeId
        FROM    m2_PaymentType_01 pt
        WHERE   pt.oid = l_oid;
    END IF; -- if operation performed properly
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error (ibs_error.error, 'p_PaymentType_01$retrieve',
    ', userId = ' || ai_userId  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_PaymentType_01$retrieve;
/

show errors;
-- p_PaymentType_01$retrieve


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT                   Action performed, values returned,
 *                              everything ok.
 *  INSUFFICIENT_RIGHTS         User has no right to perform action.
 *  OBJECTNOTFOUND              The required object was not found within the 
 *                              database.
 */
/*
-- delete existing procedure:
EXEC p_dropProc 'p_PaymentType_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_PaymentType_01$delete
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_oid                  OBJECTID,   -- the actual oid
    @l_retValue             INT         -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId, @ai_op, 
                @l_oid OUTPUT


        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
*/
-- p_PaymentType_01$delete

EXIT;