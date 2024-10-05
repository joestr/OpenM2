/******************************************************************************
 * All stored procedures regarding the Person_01 Object. <BR>
 * 
 * @version     $Id: Person_01Proc.sql,v 1.10 2009/12/02 18:35:04 rburgermann Exp $
 *
 * @author      Keim Christine (CK)  980605
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new Person_01 Object (incl. rights check). <BR>
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
 * @param   @userOid            oid of assigned user
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Person_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Person_01$create
(
    -- input parameters:
    @userId             USERID,
    @op                 INT,
    @tVersionId         TVERSIONID,
    @name               NAME,
    @containerId_s      OBJECTIDSTRING,
    @containerKind      INT,
    @isLink             BOOL,
    @linkedObjectId_s   OBJECTIDSTRING,
    @description        DESCRIPTION,
--    @userOid            OBJECTID,
    -- output parameters:
    @oid_s              OBJECTIDSTRING OUTPUT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
	-- initialize local variables:
    SELECT  @oid = 0x0000000000000000
    DECLARE @addressOid_s   OBJECTIDSTRING
    DECLARE @personsOid_s   OBJECTIDSTRING


    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description,  
                @oid_s OUTPUT, @oid OUTPUT

	    IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
	            -- insert the other values
	            INSERT INTO mad_Person_01 (oid, fullname, prefix, title, position, company,
	                                       offemail, offhomepage, userOid)
	               VALUES (@oid, @name, N'', N'', N'', N'', N'', N'',0x0000000000000000)

        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
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
 * @param   @showInNews         show in news flag.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Person_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Person_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL,
    @title          NVARCHAR(31),
    @prefix         NVARCHAR(15),
    @position       NVARCHAR(31),
    @company        NVARCHAR(63),
    @offemail       NVARCHAR(127),
    @offhomepage    NVARCHAR(255),
    @userOid_s      OBJECTIDSTRING
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
    DECLARE @userOid OBJECTID
	-- initialize local variables:
    EXEC p_stringToByte @userOid_s, @userOid OUTPUT


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
		    -- update the other values
			UPDATE mad_Person_01
			SET	fullname = @name,
                title = @title,
                prefix = @prefix,
                position = @position,
                company = @company,
                offemail = @offemail,
                offhomepage = @offhomepage,
                userOid = @userOid
			WHERE oid = @oid

        END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Person_01$change


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
 * @param   @showInNews         show in news flag.
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   @userOid            oid of assigned user
 *
 * @param   @maxlevels          Maximum of the levels allowed in the discussion
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Person_01$retrieve'
GO
-- create the new procedure:
CREATE PROCEDURE p_Person_01$retrieve
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
    @title          NVARCHAR(31)    OUTPUT,
    @prefix         NVARCHAR(15)    OUTPUT,
    @position       NVARCHAR(31)    OUTPUT,
    @company        NVARCHAR(63)    OUTPUT,
    @offemail       NVARCHAR(127)   OUTPUT,
    @offhomepage    NVARCHAR(255)   OUTPUT,
    @adressId       OBJECTID        OUTPUT,
    @userOid        OBJECTID        OUTPUT, -- user linked to person
    @userName       NAME            OUTPUT  -- name of the assigned user
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT    
    DECLARE @ST_ACTIVE INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    SELECT  @ST_ACTIVE = 2    
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
	-- initialize local variables:
--    @fullname               NAME        OUTPUT, -- fullname of the User

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
        	
        SELECT @title = title,
               @position = position,
               @prefix = prefix,
               @company = company,
               @offemail = offemail,
               @offhomepage = offhomepage,
               @userOid=userOid
		FROM mad_Person_01
		WHERE oid=@oid 

        SELECT  @adressId = oid
        FROM    ibs_Object 
        WHERE   containerId = @oid
            AND containerKind = 2
            AND tVersionId = 0x01012f01
            
      
            
        -- get userName linked to the person:
        SELECT  @userName = fullname
            FROM    ibs_user
            WHERE   oid = @userOid
 
        END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Person_01$retrieve


/******************************************************************************
 * Deletes a Person_01 object and all its values (incl. rights check). <BR>
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
EXEC p_dropProc N'p_Person_01$delete'
GO

CREATE PROCEDURE p_Person_01$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT


    ---------------------------------------------------------------------------
    -- DEFINITIONS
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_DELETE INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_DELETE = 16                          -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights INT                 -- return value of called proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @containerId OBJECTID

    ---------------------------------------------------------------------------
    -- START

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC p_Rights$checkRights
             @oid,                          -- given object to be accessed by user
             @containerId,                  -- container of given object
             @userId,                       -- user_id
             @op,                           -- required rights user must have to 
                                            -- delete object (operation to be perf.)
             @rights OUTPUT                 -- returned value

        -- check if the user has the necessary rights
        IF (@rights > 0)                    -- the user has the rights?
        BEGIN
            BEGIN TRANSACTION
			
            -- delete references to the object
            DELETE  ibs_Object 
            WHERE   linkedObjectId = @oid
            -- delete all values of object
            DELETE  mad_Person_01
            WHERE   oid = @oid
            -- delete object itself
            DELETE  ibs_Object 
            WHERE   oid = @oid

            COMMIT TRANSACTION
        END -- if the user has the rights
        ELSE                                -- the user does not have the rights
        BEGIN
            SELECT  @retValue = @INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Person_01$delete



/******************************************************************************
 * Copies a Person_01 object and all its values (incl. rights check). <BR>
 */
EXEC p_dropProc N'p_Person_01$BOCopy'
GO


-- create the new procedure:
CREATE PROCEDURE p_Person_01$BOCopy
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
 	INSERT INTO mad_Person_01 
                (oid, fullname, title, prefix, 
                position, company, offemail, offhomepage, userOid)
	SELECT  @newOid, fullname, title, prefix, 
                position, company, offemail, offhomepage, userOid
    FROM    mad_Person_01 
    WHERE oid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @retValue
GO
-- p_Person_01$BOCopy
