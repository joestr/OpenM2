/******************************************************************************
 * All stored procedures regarding the Connector_01 Object. <BR>
 *
 * @version     1.11.0001, 09.12.1999
 *
 * @author      Harald Buzzi (HB)  991209
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
EXEC p_dropProc N'p_Connector_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Connector_01$create
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
    DECLARE @NOT_OK INT,@ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @NOT_OK =0,@ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- define local variables:
    DECLARE @oid OBJECTID
    -- initialize local variables:
    SELECT  @oid = 0x0000000000000000
    DECLARE @rights RIGHTS
    DECLARE @actRights RIGHTS

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
            INSERT INTO ibs_Connector_01 (oid, connectorType, isImportConnector, 
                                            isExportConnector, arg1, arg2, arg3,
                                            arg4, arg5, arg6, arg7, arg8, arg9)
            VALUES  (@oid, 0, 0, 0, N'', N'', N'', N'', N'', N'', N'', N'', N'')
        END
        
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Connector_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         flag if object should be shown in NewsContainer
 * @param   @connectorType      determines the type of the connector (file, http, ...)
 * @param   @isImportConnector  flag is set if connector is an importconnector
 * @param   @isExportConnector  flag is set if connector is an exportconnector
 * @param   @arg1               an Argument of the connector
 * @param   @arg2               an Argument of the connector
 * @param   @arg3               an Argument of the connector
 * @param   @arg4               an Argument of the connector
 * @param   @arg5               an Argument of the connector
 * @param   @arg6               an Argument of the connector
 * @param   @arg7               an Argument of the connector
 * @param   @arg8               an Argument of the connector
 * @param   @arg9               an Argument of the connector
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Connector_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Connector_01$change
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL,
    -- type-specific input parameters:
    @connectorType      INT,
    @isImportConnector  BOOL,
    @isExportConnector  BOOL,
    @arg1               NVARCHAR(255),
    @arg2               NVARCHAR(255),
    @arg3               NVARCHAR(255),
    @arg4               NVARCHAR(255),
    @arg5               NVARCHAR(128),
    @arg6               NVARCHAR(128),
    @arg7               NVARCHAR(128),
    @arg8               NVARCHAR(128),
    @arg9               NVARCHAR(128)
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


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update further information
	        UPDATE ibs_Connector_01
	        SET     connectorType       = @connectorType,
                    isImportConnector   = @isImportConnector,
                    isExportConnector   = @isExportConnector,
                    arg1                = @arg1,
                    arg2                = @arg2,
                    arg3                = @arg3,
                    arg4                = @arg4,
                    arg5                = @arg5,
                    arg6                = @arg6,
                    arg7                = @arg7,
                    arg8                = @arg8,
                    arg9                = @arg9

	        WHERE  oid=@oid
        END
    COMMIT TRANSACTION
       
    -- return the state value:
    RETURN  @retValue
GO
-- p_Connector_01$change


/******************************************************************************
 * Creates a new referenz to a connector object. <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @orginOid_s         Id of the orgin object.
 * @param   @targetOid_s        Id of the target object.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created refernece.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Connector_01$createRef'
GO

-- create the new procedure:
CREATE PROCEDURE p_Connector_01$createRef
(
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @validUntil     DATETIME,
    -- type-specific input parameters:
    @orginOid_s     OBJECTIDSTRING,
    @targetOid_s    OBJECTIDSTRING,
    -- common output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
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
    DECLARE 
        @oid            OBJECTID,
        @orginOid       OBJECTID,
        @targetOid      OBJECTID,
        @targetName     NAME,
        @containerId    OBJECTID,
        @containerId_s  OBJECTIDSTRING,
        @TV_ReferenzContainer   TVERSIONID
    -- initialize local variables:

    -- body:
    BEGIN TRANSACTION
        -- convert oids
        EXEC p_StringToByte @orginOid_s, @orginOid OUTPUT
        EXEC p_StringToByte @targetOid_s, @targetOid OUTPUT
        
        -- get tVersionId of the reference container
        SELECT  @TV_ReferenzContainer = actVersion
        FROM    ibs_Type
        WHERE   code like N'ReferenzContainer'
        
        -- get referenz container of the orgin object
        SELECT  @containerId = r.oid
        FROM    ibs_object r, ibs_object o
        WHERE   r.containerId = o.oid 
            AND o.oid = @orginOid
            AND r.tVersionId = @TV_ReferenzContainer
            
        -- get name of target object
        SELECT  @targetName = o.name
        FROM    ibs_object o
        WHERE   o.oid = @targetOid
        
            
        -- convert oid
        EXEC p_ByteToString @containerId, @containerId_s OUTPUT
        
        -- create the referenz:
        EXEC @retValue = p_referenz_01$create @userId, @op, @tVersionId, 
                    @targetName, @containerId_s, 1, 1, @targetOid_s, '', @oid_s OUTPUT

    COMMIT TRANSACTION
       
    -- return the state value:
    RETURN  @retValue
GO
-- p_Connector_01$createRef




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
 * @param   @showInNews         flag if object should be shown in NewsContainer
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
 * @param   @connectorType      determines the type of the connector (file, http, ...)
 * @param   @isImportConnector  flag is set if connector is an importconnector
 * @param   @isExportConnector  flag is set if connector is an exportconnector
 * @param   @arg1               an Argument of the connector
 * @param   @arg2               an Argument of the connector
 * @param   @arg3               an Argument of the connector
 * @param   @arg4               an Argument of the connector
 * @param   @arg5               an Argument of the connector
 * @param   @arg6               an Argument of the connector
 * @param   @arg7               an Argument of the connector
 * @param   @arg8               an Argument of the connector
 * @param   @arg9               an Argument of the connector

 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Connector_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Connector_01$retrieve
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
    @connectorType      INT             OUTPUT,
    @isImportConnector  BOOL            OUTPUT,
    @isExportConnector  BOOL            OUTPUT,
    @arg1               NVARCHAR(255)   OUTPUT,
    @arg2               NVARCHAR(255)   OUTPUT,
    @arg3               NVARCHAR(255)   OUTPUT,
    @arg4               NVARCHAR(255)   OUTPUT,
    @arg5               NVARCHAR(128)   OUTPUT,
    @arg6               NVARCHAR(128)   OUTPUT,
    @arg7               NVARCHAR(128)   OUTPUT,
    @arg8               NVARCHAR(128)   OUTPUT,
    @arg9               NVARCHAR(128)   OUTPUT
        --  DATATYPE  TEXT is not possible
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

        IF (@retValue = @ALL_RIGHT)
        BEGIN      	
            SELECT  @connectorType       = connectorType,
                    @isImportConnector   = isImportConnector,
                    @isExportConnector   = isExportConnector,
                    @arg1                = arg1,
                    @arg2                = arg2,
                    @arg3                = arg3,
                    @arg4                = arg4,
                    @arg5                = arg5,
                    @arg6                = arg6,
                    @arg7                = arg7,
                    @arg8                = arg8,
                    @arg9                = arg9
	        FROM    ibs_Connector_01
	        WHERE   oid=@oid
        END
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Connector_01$retrieve


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
-- delete existing procedure:
EXEC p_dropProc N'p_Connector_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Connector_01$delete
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
            DELETE  ibs_Connector_01
            WHERE   oid NOT IN 
                    (SELECT oid 
                    FROM    ibs_Object)

            -- check if deletion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @retValue = @NOT_OK -- set return value
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Connector_01$delete


/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @oid                ID of the object to be copy.
 * @param   @userId             ID of the user who is copying the object.
 * @param   @newOid             ID of the copy of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Connector_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Connector_01$BOCopy
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

    INSERT INTO ibs_Connector_01
         (oid, connectorType, isImportConnector, 
          isExportConnector, arg1, arg2, arg3,
          arg4, arg5, arg6, arg7, arg8, arg9)

    SELECT @newOid, connectorType, isImportConnector, 
          isExportConnector, arg1, arg2, arg3,
          arg4, arg5, arg6, arg7, arg8, arg9
    FROM ibs_Connector_01
    WHERE oid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @retValue
GO
-- p_Connector_01$BOCopy
