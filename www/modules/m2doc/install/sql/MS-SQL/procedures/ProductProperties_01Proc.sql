/******************************************************************************
 * All stored procedures regarding the ProductProperties_01 table. <BR>
 *
 * @version     $Id: ProductProperties_01Proc.sql,v 1.8 2009/12/02 18:35:03 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW)  980908
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
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
-- delete existing procedure:
IF EXISTS (
            SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$create')
                AND sysstat & 0xf = 4
          )
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$create
GO

-- create the new procedure:
CREATE PROCEDURE p_ProdProperties_01$create
(
    -- common input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- common output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- define local variables:
    DECLARE @oid OBJECTID
    -- initialize local variables:
    SELECT  @oid = 0x0000000000000000

    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId,
                            @name, @containerId_s, @containerKind,
                            @isLink, @linkedObjectId_s, @description,
                            @oid_s OUTPUT, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- create object type specific data:
            INSERT INTO m2_ProductProperties_01
                    (oid, categoryOid, delimiter, values1, values2, values3, values4)
            VALUES  (@oid, 0x0000000000000000, N'', N'', N'', N'', N'')

            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT  @retValue = @NOT_OK -- set return value
            END -- if no row affected
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO 
-- p_ProdProperties_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
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
-- delete existing procedure:
IF EXISTS ( SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$change')
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$change
GO

-- create the new procedure:
CREATE PROCEDURE p_ProdProperties_01$change
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId            USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL,
    -- type-specific input parameters:
    @categoryOid_s  OBJECTIDSTRING,
    @delimiter      NVARCHAR(1),
    @values1        NVARCHAR(255),
    @values2        NVARCHAR(255),
    @values3        NVARCHAR(255),
    @values4        NVARCHAR(255)
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
    -- initialize local variables:
    DECLARE @categoryOid    OBJECTID


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update object type specific data:
            EXEC	p_StringToByte	@categoryOid_s, @categoryOid OUTPUT
            
            UPDATE  m2_ProductProperties_01
            SET     categoryOid = @categoryOid,
                    delimiter = @delimiter,
                    values1 = @values1,
                    values2 = @values2,
                    values3 = @values3,
                    values4 = @values4
            WHERE   oid = @oid

            -- check if change was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO 
-- p_ProdProperties_01$change


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
 *
 * @param   @categoryName       Name of the category
 * @param   @delimiter          Delimiter character used for property string
 * @param   @properties         Properties string
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
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$retrieve')
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$retrieve
GO

-- create the new procedure:
CREATE PROCEDURE p_ProdProperties_01$retrieve
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
    -- type-specific output attributes:
    @categoryOid    OBJECTID        OUTPUT,
    @categoryName   NAME            OUTPUT,
    @delimiter      NVARCHAR(1)     OUTPUT,
    @values1        NVARCHAR(255)   OUTPUT,
    @values2        NVARCHAR(255)   OUTPUT,
    @values3        NVARCHAR(255)   OUTPUT,
    @values4        NVARCHAR(255)   OUTPUT
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
                @checkedOut OUTPUT, @checkOutDate OUTPUT, 
                @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
                @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- retrieve object type specific data:
            SELECT  @categoryOid = categoryOid,
                    @categoryName = o.name,
                    @delimiter = delimiter,   
                    @values1 = values1,
                    @values2 = values2,
                    @values3 = values3,
                    @values4 = values4
            FROM    m2_ProductProperties_01 pp
            LEFT OUTER JOIN	ibs_Object o
            ON		o.oid = pp.categoryOid
            WHERE   pp.oid = @oid

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO 
-- p_ProdProperties_01$retrieve

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
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$delete')
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_ProdProperties_01$delete
GO

CREATE PROCEDURE p_ProdProperties_01$delete
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
    DECLARE @id ID
    -- initialize local variables:


    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op,
                @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object type specific data:
            DELETE  m2_ProductProperties_01
            WHERE   oid = @oid

             -- check if deletion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO 
-- p_ProdProperties_01$delete


