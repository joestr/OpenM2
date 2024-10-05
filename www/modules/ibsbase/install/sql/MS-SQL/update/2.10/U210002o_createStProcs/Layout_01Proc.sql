/******************************************************************************
 * All stored procedures regarding the Layout_01 Object. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Keim Christine (CK)  981221
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new Layout_01 Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_tVersionId       Type of the new object.
 * @param   ai_name             Name of the object.
 * @param   ai_containerId_s    ID of the container where object shall be 
 *                              created in.
 * @param   ai_containerKind    Kind of object/container relationship
 * @param   ai_isLink           Defines if the object is a link
 * @param   ai_linkedObjectId_s If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   ai_description      Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Layout_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Layout_01$create
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_tVersionId          TVERSIONID,
    @ai_name                NAME,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_containerKind       INT,
    @ai_isLink              BOOL,
    @ai_linkedObjectId_s    OBJECTIDSTRING,
    @ai_description         DESCRIPTION,
    -- output parameters:
    @ao_oid_s               OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_ALREADY_EXISTS       INT,            -- the object already exists

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_oid                  OBJECTID,       -- the actual oid
    @l_containerId          OBJECTID,       -- oid of container
    @l_linkedObjectId       OBJECTID,       -- oid of linked object
    @l_allGroupId           GROUPID,        -- group of all users
    @l_structAdminGroupId   GROUPID,        -- group of structure administrators
    @l_rights               RIGHTS,         -- the actual permissions
    @l_domainId             DOMAINID,       -- id of the actual domain
    @l_sysAdmin             USERID          -- user id of system administrator

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_ALL_RIGHT,
    @l_domainId = @ai_userId / 0x01000000

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT
    EXEC p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT

    BEGIN TRANSACTION
        -- get user id of systemadministrator
        SELECT  @l_sysAdmin = CONVERT (INT, value)
        FROM    ibs_System 
        WHERE   name = N'ID_sysAdmin'

        -- create base object:
        EXEC @l_retValue =
            p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId, @ai_name,
                @ai_containerId_s, @ai_containerKind,
                @ai_isLink, @ai_linkedObjectId_s, @ai_description, 
                @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- object created successfully?
        BEGIN
            -- insert the other values:
            INSERT INTO ibs_Layout_01 (oid, name, domainId, isDefault)
            SELECT  @l_oid, @ai_name, @l_domainId, 0


            -- if the layout is for other users than system administrator, set
            -- permissions for the layout
            -- if the layout is for the system administrator there are no
            -- domains available so it is not possible and not necessary to set
            -- permissions for the layout
            IF (NOT (@ai_userId = @l_sysAdmin)) -- not system administrator?
            BEGIN
                -- set permissions on layout:
                -- get the groups for which to set the permissions:
                SELECT  @l_allGroupId = allGroupId,
                        @l_structAdminGroupId = structAdminGroupId
                FROM    ibs_Domain_01
                WHERE   id = @l_domainId

                -- set permissions for structure administrators:
                IF (NOT (@l_structAdminGroupId IS NULL)) -- group found?
                BEGIN
                    SELECT  @l_rights = SUM (id)
                    FROM    ibs_Operation
                    EXEC p_Rights$setRights
                        @l_oid, @l_structAdminGroupId, @l_rights, 1
                END -- if group found
                -- set permissions for group of all users:
                IF (NOT (@l_allGroupId IS NULL)) -- group found?
                BEGIN
                    SELECT  @l_rights = SUM (id)
                    FROM    ibs_Operation
                    WHERE   name IN (N'view', N'read')
                    EXEC p_Rights$setRights @l_oid, @l_allGroupId, @l_rights, 1
                END -- if group found
            END -- if not system administrator
        END -- if object created successfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         the showInNews flag     
 * @param   ai_isDefault        Is this the default layout for the domain?
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Layout_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Layout_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL,
    @ai_isDefault   BOOL
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
    -- initialize local variables:

    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

        UPDATE  ibs_Layout_01
        SET     name = @name,
                isDefault = @ai_isDefault
        WHERE   oid = @oid

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Layout_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
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
 * @param   @description        Description of the object
 * @param   @showInNews         the showInNews flag
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   ao_isDefault        Is this the default layout for the domain?
 *
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Layout_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Layout_01$retrieve
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
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
    @ownerName      NAME            OUTPUT,
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
    @checkOutUserName NAME          OUTPUT,
    -- specific output parameters:
    @ao_isDefault   BOOL            OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID

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
                @validUntil OUTPUT, @description OUTPUT, @showInNews, 
                @checkedOut OUTPUT, @checkOutDate OUTPUT, 
                @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
                @oid OUTPUT

        -- retrieve name of the Layout
        SELECT  @name = name, @ao_isDefault = isDefault
        FROM    ibs_Layout_01 
        WHERE   oid = @oid

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Layout_01$retrieve


/******************************************************************************
 * Deletes a Layout_01 object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Layout_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Layout_01$delete
(
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
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op, 
                @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  ibs_Layout_01
            WHERE   oid = @oid

        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Layout_01$delete


-- delete existing procedure:
EXEC p_dropProc N'p_Layout_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Layout_01$BOCopy
(
    -- common input parameters:
    @oid            OBJECTID,
    @userId         USERID,
    @newOid         OBJECTID
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK

    -- make an insert for all type specific tables:
    INSERT INTO ibs_Layout_01 
            (oid, name, domainId, isDefault)
    SELECT  @newOid, b.name, b.domainId, b.isDefault
    FROM    ibs_Layout_01 b
    WHERE   b.oid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @retValue
GO
-- p_Layout_01$BOCopy
