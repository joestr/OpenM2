/******************************************************************************
 * All stored procedures regarding the PaymentType table. <BR>
 * 
 * @version     $Id: PaymentType_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
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
-- delete existing procedure:
EXEC p_dropProc N'p_PaymentType_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_PaymentType_01$create
(
    -- common input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_tVersionId          TVERSIONID,
    @ai_name                NAME,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_containerKind       INT,
    @ai_isLink              BOOL,
    @ai_linkedObjectId_s    OBJECTIDSTRING,
    @ai_description         DESCRIPTION,
    -- common output parameters:
    @ao_oid_s               OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_oid                  OBJECTID        -- the actual oid

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_oid                  = @c_NOOID,
    @l_retValue             = @c_NOT_OK

-- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId, 
                            @ai_name, @ai_containerId_s, @ai_containerKind, 
                            @ai_isLink, @ai_linkedObjectId_s, @ai_description, 
                            @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- insert default values
            INSERT INTO m2_PaymentType_01
                        (oid, paymentTypeId, name)
            VALUES      (@l_oid, -1, '')
        END  -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
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
-- delete existing procedure:
EXEC p_dropProc N'p_PaymentType_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_PaymentType_01$change
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    -- type-specific input parameters:
    @ai_paymentTypeId       ID
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_retValue             INT             -- return value of this function

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
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId,
                @ai_op, @ai_name, @ai_validUntil, @ai_description,
                @ai_showInNews, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- set the new payment type id:
            UPDATE  m2_PaymentType_01
            SET     paymentTypeId = @ai_paymentTypeId,
                    name = @ai_name
            FROM    m2_PaymentType_01
            WHERE   oid = @l_oid
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
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
-- delete existing procedure:
EXEC p_dropProc N'p_PaymentType_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_PaymentType_01$retrieve
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- common output parameters:
    @ao_state               STATE           OUTPUT,
    @ao_tVersionId          TVERSIONID      OUTPUT,
    @ao_typeName            NAME            OUTPUT,
    @ao_name                NAME            OUTPUT,
    @ao_containerId         OBJECTID        OUTPUT,
    @ao_containerName       NAME            OUTPUT,
    @ao_containerKind       INT             OUTPUT,
    @ao_isLink              BOOL            OUTPUT,
    @ao_linkedObjectId      OBJECTID        OUTPUT,
    @ao_owner               USERID          OUTPUT,
    @ao_ownerName           NAME            OUTPUT,
    @ao_creationDate        DATETIME        OUTPUT,
    @ao_creator             USERID          OUTPUT,
    @ao_creatorName         NAME            OUTPUT,
    @ao_lastChanged         DATETIME        OUTPUT,
    @ao_changer             USERID          OUTPUT,
    @ao_changerName         NAME            OUTPUT,
    @ao_validUntil          DATETIME        OUTPUT,
    @ao_description         DESCRIPTION     OUTPUT,
    @ao_showInNews          BOOL            OUTPUT,
    @ao_checkedOut          BOOL            OUTPUT,
    @ao_checkOutDate        DATETIME        OUTPUT,
    @ao_checkOutUser        USERID          OUTPUT,
    @ao_checkOutUserOid     OBJECTID        OUTPUT,
    @ao_checkOutUserName    NAME            OUTPUT,
    -- type-specific output parameters:
    @ao_paymentTypeId       ID              OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @l_retValue = p_Object$performRetrieve @ai_oid_s, @ai_userId,
                @ai_op, @ao_state OUTPUT, @ao_tVersionId OUTPUT,
                @ao_typeName OUTPUT, @ao_name OUTPUT, @ao_containerId OUTPUT,
                @ao_containerName OUTPUT, @ao_containerKind OUTPUT,
                @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT, 
                @ao_owner OUTPUT, @ao_ownerName OUTPUT, @ao_creationDate OUTPUT,
                @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT,
                @ao_changerName OUTPUT, @ao_validUntil OUTPUT,
                @ao_description OUTPUT, @ao_showInNews OUTPUT,
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT,
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT,
                @ao_checkOutUserName OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- retrieve object type specific data:
            SELECT  @ao_paymentTypeId = paymentTypeId
            FROM    m2_PaymentType_01 pt
            WHERE   pt.oid = @l_oid

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT < 0)         -- no row affected?
                SELECT  @l_retValue = @c_NOT_OK -- set return value
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_PaymentType_01$retrieve
