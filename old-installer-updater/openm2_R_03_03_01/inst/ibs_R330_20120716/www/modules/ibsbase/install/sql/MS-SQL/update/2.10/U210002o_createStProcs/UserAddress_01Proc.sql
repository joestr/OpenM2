/******************************************************************************
 * All stored procedures regarding the UserAddress_01 Object. <BR>
 * 
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Koban Ferdinand (FF)  010122
 *
 * <DT><B>Updates:</B>
 * <DD>
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new UserAddress_01 Object (incl. rights check). <BR>
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
-- delete existing procedure:
EXEC p_dropProc N'p_UserAddress_01$create'
GO



-- create the new procedure:
CREATE PROCEDURE p_UserAddress_01$create
(
    -- input parameters:
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_tVersionId     TVERSIONID,
    @ai_name           NAME,
    @ai_containerId_s  OBJECTIDSTRING,
    @ai_containerKind  INT,
    @ai_isLink         BOOL,
    @ai_linkedObjectId_s OBJECTIDSTRING,
    @ai_description    DESCRIPTION,
    -- output parameters:
    @ao_oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @l_containerId    OBJECTID
    DECLARE @l_linkedObjectId OBJECTID

    EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT
    EXEC p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @c_ALL_RIGHT INT, @c_INSUFFICIENT_RIGHTS INT, @c_ALREADY_EXISTS INT
    -- set constants:
    SELECT  @c_ALL_RIGHT = 1, @c_INSUFFICIENT_RIGHTS = 2, @c_ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_ALL_RIGHT
    -- define local variables:
    DECLARE @l_oid OBJECTID
        -- initialize local variables:
    SELECT  @l_oid = 0x0000000000000000
    DECLARE @l_addressOid_s   OBJECTIDSTRING
    DECLARE @l_personsOid_s   OBJECTIDSTRING

    -- body:
    BEGIN TRANSACTION
    
    
    
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId, @ai_name, @ai_containerId_s,
                @ai_containerKind, @ai_isLink, @ai_linkedObjectId_s, @ai_description, 
                @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- insert the other values
            INSERT INTO Ibs_UserAddress_01 (oid, email, smsemail)
            VALUES (@l_oid, '', '')
            
        END -- if object created successfully
    COMMIT TRANSACTION

    -- END -- if tab useraddress does not exist allready

    -- return the state value
    RETURN  @l_retValue
GO
--- p_UserAddress_01$create


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
EXEC p_dropProc N'p_UserAddress_01$change'
GO


-- create the new procedure:
CREATE PROCEDURE p_UserAddress_01$change
(
    -- input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_name           NAME,
    @ai_validUntil     DATETIME,
    @ai_description    DESCRIPTION,
    @ai_showInNews     BOOL,
    @ai_email          EMAIL,
    @ai_smsemail       EMAIL

)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_ALL_RIGHT INT, @c_INSUFFICIENT_RIGHTS INT, @c_OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @c_ALL_RIGHT = 1, @c_INSUFFICIENT_RIGHTS = 2, @c_OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @l_retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_ALL_RIGHT
    -- define local variables:
    DECLARE @l_oid OBJECTID
        -- initialize local variables:


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, @ai_op, @ai_name,
                @ai_validUntil, @ai_description, @ai_showInNews, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update the other values
            UPDATE  ibs_UserAddress_01
            SET     email = @ai_email,
                    smsemail = @ai_smsemail
            WHERE   oid = @l_oid

        END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_UserAddress_01$change


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
 *
 * @param   @maxlevels          Maximum of the levels allowed in the discussion
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_UserAddress_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_UserAddress_01$retrieve
(
    -- input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT,
    -- output parameters
    @ao_state          STATE           OUTPUT,
    @ao_tVersionId     TVERSIONID      OUTPUT,
    @ao_typeName       NAME            OUTPUT,
    @ao_name           NAME            OUTPUT,
    @ao_containerId    OBJECTID        OUTPUT,
    @ao_containerName  NAME            OUTPUT,
    @ao_containerKind  INT             OUTPUT,
    @ao_isLink         BOOL            OUTPUT,
    @ao_linkedObjectId OBJECTID        OUTPUT,
    @ao_owner          USERID          OUTPUT,
    @ao_ownerName      NAME            OUTPUT,
    @ao_creationDate   DATETIME        OUTPUT,
    @ao_creator        USERID          OUTPUT,
    @ao_creatorName    NAME            OUTPUT,
    @ao_lastChanged    DATETIME        OUTPUT,
    @ao_changer        USERID          OUTPUT,
    @ao_changerName    NAME            OUTPUT,
    @ao_validUntil     DATETIME        OUTPUT,
    @ao_description    DESCRIPTION     OUTPUT,
    @ao_showInNews     BOOL            OUTPUT, 
    @ao_checkedOut     BOOL            OUTPUT,
    @ao_checkOutDate   DATETIME        OUTPUT,
    @ao_checkOutUser   USERID          OUTPUT,
    @ao_checkOutUserOid OBJECTID       OUTPUT,
    @ao_checkOutUserName NAME          OUTPUT,
    @ao_email          EMAIL           OUTPUT,
    @ao_smsemail       EMAIL           OUTPUT

)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT, @c_ALL_RIGHT INT, @c_INSUFFICIENT_RIGHTS INT, @c_OBJECTNOTFOUND INT    
    DECLARE @c_ST_ACTIVE INT
    -- set constants:
    SELECT  @c_NOT_OK = 0, @c_ALL_RIGHT = 1, @c_INSUFFICIENT_RIGHTS = 2, @c_OBJECTNOTFOUND = 3
    SELECT  @c_ST_ACTIVE = 2    
    -- define return values:
    DECLARE @l_retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_ALL_RIGHT
    -- define local variables:
    DECLARE @l_oid OBJECTID
        -- initialize local variables:

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @l_retValue = p_Object$performRetrieve
                @ai_oid_s, @ai_userId, @ai_op,
                @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT, 
                @ao_name OUTPUT, @ao_containerId OUTPUT, @ao_containerName OUTPUT, 
                @ao_containerKind OUTPUT, @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT, 
                @ao_owner OUTPUT, @ao_ownerName OUTPUT, 
                @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
                @ao_validUntil OUTPUT, @ao_description OUTPUT, @ao_showInNews OUTPUT, 
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT, 
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT, @ao_checkOutUserName OUTPUT, 
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)
        BEGIN                
            SELECT @ao_email = email,
                   @ao_smsemail = smsemail
            FROM Ibs_UserAddress_01
            WHERE oid=@l_oid 

        END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_UserAddress_01$retrieve


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
-- delete existing procedure:
EXEC p_dropProc N'p_UserAddress_01$delete'
GO

CREATE PROCEDURE p_UserAddress_01$delete
(
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @l_oid OBJECTID
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT


    ---------------------------------------------------------------------------
    -- DEFINITIONS
    -- define return constants
    DECLARE @c_INSUFFICIENT_RIGHTS INT, @c_ALL_RIGHT INT, @c_OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @c_RIGHT_DELETE INT
    -- set constants
    SELECT  @c_ALL_RIGHT = 1, @c_INSUFFICIENT_RIGHTS = 2,   -- return values
            @c_OBJECTNOTFOUND = 3,
            @c_RIGHT_DELETE = 16                          -- access rights
    -- define return values
    DECLARE @l_retValue INT               -- return value of this procedure
    DECLARE @l_rights INT                 -- return value of called proc.
    -- initialize return values
    SELECT  @l_retValue = @c_ALL_RIGHT, @l_rights = 0
    -- define used variables
    DECLARE @l_containerId OBJECTID

    ---------------------------------------------------------------------------
    -- START

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC p_Rights$checkRights
             @l_oid,                          -- given object to be accessed by user
             @l_containerId,                  -- container of given object
             @ai_userId,                       -- user_id
             @ai_op,                           -- required rights user must have to 
                                            -- delete object (operation to be perf.)
             @l_rights OUTPUT                 -- returned value

        -- check if the user has the necessary rights
        IF (@l_rights > 0)                    -- the user has the rights?
        BEGIN
            BEGIN TRANSACTION
                        
            -- delete references to the object
            DELETE  ibs_Object 
            WHERE   linkedObjectId = @l_oid
            -- delete all values of object
            DELETE  Ibs_UserAddress_01
            WHERE   oid = @l_oid
            -- delete object itself
            DELETE  ibs_Object 
            WHERE   oid = @l_oid

            COMMIT TRANSACTION
        END -- if the user has the rights
        ELSE                                -- the user does not have the rights
        BEGIN
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @l_retValue
GO
-- p_UserAddress_01$delete



/******************************************************************************
 * Returns the settings for notification and all addresses. <BR>
 *
 * 
 * @input parameters:
 * .
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
-- delete existing procedure
EXEC p_dropProc N'p_User_01$getNotificationData'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$getNotificationData
(
    -- common input parameters:
    @ai_useroid_s               OBJECTIDSTRING,
    -- output parameters
    @ao_username                NAME        OUTPUT,   
    @ao_notificationKind        INT         OUTPUT,
    @ao_sendSms                 BOOL        OUTPUT,   
    @ao_addWeblink              BOOL        OUTPUT,
    @ao_email                   EMAIL       OUTPUT,
    @ao_smsemail                EMAIL       OUTPUT
) 
AS
    -- definitions:
    -- define locale Variables:
    DECLARE @l_oid              OBJECTID
    DECLARE @l_userId           USERID
    -- define return constants:
    DECLARE @c_NOT_OK INT, @ALL_RIGHT INT
    -- set constants:
    SELECT  @c_NOT_OK = 0, @ALL_RIGHT = 1
    -- define return values:
    DECLARE @l_retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    
    -- convert oidstring to binaryOID
    EXEC    p_stringToByte @ai_useroid_s, @l_oid OUTPUT
   
    
    
    
    
    -- make an select for all type specific tables:
    
   SELECT   @ao_email = a.email, @ao_smsemail = a.smsemail, @l_userId = u.id, 
            @ao_username = u.name,
            @ao_notificationKind = p.notificationKind,
            @ao_sendSms = p.sendSms,
            @ao_addWeblink = p.addWeblink 
   FROM     ibs_User u,
            ibs_UserProfile p, 
            ibs_Object tabAddress, 
            ibs_UserAddress_01 a 
   WHERE    p.userid = u.id
       AND  u.oid = @l_oid
       AND  p.oid = tabAddress.containerId
       AND  tabAddress.oid = a.oid
          
    -- check if select was performed correctly:
    IF (@@ROWCOUNT = 1)                -- at least one row affected?
        SELECT  @l_retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_User_01$getNotificationData 

