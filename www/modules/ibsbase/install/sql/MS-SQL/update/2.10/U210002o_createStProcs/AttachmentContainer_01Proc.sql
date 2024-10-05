/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 *
 * @version     $Id: AttachmentContainer_01Proc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Heinz Stampfer (KR)  980521
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
EXEC p_dropProc N'p_AC_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_AC_01$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2 -- return values
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
            @containerKind, @isLink, @linkedObjectId_s, @description, 
            @oid_s OUTPUT

    -- return the state value
    RETURN  @retValue
GO
-- p_AC_01$create


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
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_AC_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_AC_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    BEGIN TRANSACTION

		-- perform the change of the object:
		EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name, 
		        @validUntil, @description, @showInNews

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_AC_01$change


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
 * @param   @containerName      Name of the Container
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
 * 
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_AC_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_AC_01$retrieve
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

    @masterId_s    	OBJECTIDSTRING	OUTPUT, 
    @fileName       NVARCHAR(255)   OUTPUT,
    @url            NVARCHAR(255)   OUTPUT,
    @path           NVARCHAR(255)   OUTPUT,
    @attachmentType INT             OUTPUT
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
    DECLARE @dummy INT
    
    DECLARE @masterId  OBJECTID
    SELECT  @masterId  = 0x0000000000000000

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

        IF (@retValue = @ALL_RIGHT) --  all right with the basisobject
        BEGIN      	
            -- check if the container has a element or not ?
            SELECT  *
            FROM    ibs_Object
            WHERE   containerId = @oid 
                    AND tVersionId = 16842833 -- typeName = attachment

            IF (@@ROWCOUNT > 0) -- AttachmentContainer is not empty
            BEGIN

                SELECT  @masterId = a.oid   -- search the master attachement
    	        FROM    ibs_Attachment_01 a, ibs_Object o
	            WHERE   o.containerId = @oid 
                        AND a.isMaster = 1
                        AND o.oid = a.oid
                        AND o.state = 2     -- state = ST_ACTIVE

                SELECT @dummy = @@ROWCOUNT

                IF (@dummy = 1) -- exact one master attachment has been found
                    BEGIN       -- read the data of the master element
                        SELECT @fileName = filename,
                               @url = url,
                               @path = path,
                               @attachmentType = attachmentType
                        FROM   ibs_Attachment_01
                        WHERE oid = @masterId
                    END
                ELSE
                IF (@dummy = 0) -- no master attachment found
                    BEGIN
                        -- define the oldest attachment as the new master attachment
                        SELECT  @masterId = MIN (a.oid)
                        FROM    ibs_Attachment_01 a 
                        JOIN    ibs_Object o 
                                ON a.oid = o.oid
                        WHERE   o.containerId = @oid
                          AND   o.state = 2             -- state is ST_ACTIVE

                        SELECT  @fileName = filename,   -- get the data of the new master attachment
                                @url = url,
                                @path = path,
                                @attachmentType = attachmentType
                        FROM    ibs_Attachment_01
                        WHERE   oid = @masterId
                    END  -- define new master attachment 
                ELSE
                IF (@dummy > 1) -- more then one master attachment found
                BEGIN
                    -- set the oldest attachment to master
                    SELECT  @masterId = MIN (a.oid)
                    FROM    ibs_Attachment_01 a 
                    JOIN    ibs_Object o 
                            ON a.oid = o.oid
                    WHERE   o.containerId = @oid
                      AND   o.state = 2     -- state is ST_ACTIVE
                      
                    -- read out the properties
                    SELECT @fileName = filename,
                           @url = url,
                           @path = path,
                           @attachmentType = attachmentType
                    FROM   ibs_Attachment_01
                    WHERE  oid = @masterId
                END    
            ELSE  -- Container is empty and therefore no master attachment defined
                BEGIN
                    SELECT @fileName = N'kein Masterfile definiert'
                    SELECT @url = N'keine MasterUrl definiert'
                END        
            END  -- container is not empty
        -- convert oid to string:
        EXEC    p_byteToString @masterId, @masterId_s OUTPUT
        END --  all right with the basisobject

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
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
EXEC p_dropProc N'p_AC_01$delete'
GO

CREATE PROCEDURE p_AC_01$delete
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
--    DECLARE @posNoPath POSNOPATH_VC
 

    ---------------------------------------------------------------------------
    -- START


    -- get container id of object
    SELECT  @containerId = containerId
    FROM    ibs_Object
    WHERE   oid = @oid

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
    IF (@rights = @op)                     -- the user has the rights?
        BEGIN
            BEGIN TRANSACTION
/*
            -- delete subsequent objects
            DELETE  ibs_Object
            WHERE   posNoPath LIKE @posNoPath + '%'
                AND oid <> @oid

            -- delete references to the object
            DELETE  ibs_Object 
            WHERE   linkedObjectId = @oid


            -- delete object itself
            DELETE  ibs_Object 
            WHERE   oid = @oid
*/
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


/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- AJ 990425 changed ... //////////////////////////////////////////////////////
-- delete existing procedure:
EXEC p_dropProc N'p_AC_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_AC_01$BOCopy
(
    -- input parameters:
    @oid                OBJECTID,
    @userId             USERID,
    @op                 INT,
    @newOid             OBJECTID OUTPUT
)
AS

    ---------------------------------------------------------------------------
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    ---------------------------------------------------------------------------
    
    DECLARE @tVersionId     TVERSIONID  
    DECLARE @containerId    OBJECTID    -- the oid of the container the object
    DECLARE @l_containerOid2 OBJECTID   -- the oid of the container of the
                                        -- object's container
    DECLARE @retVal         INT
    DECLARE @name           NVARCHAR(255)
    DECLARE @containerKind  INT
    DECLARE @isLink         BOOL
    DECLARE @linkedObjectId OBJECTID
    DECLARE @description    NVARCHAR(255)


    SELECT  @tVersionId = tVersionId, @name = name, @containerId = containerId,
            @containerKind = containerKind, @l_containerOid2 = containerOid2,
            @isLink = isLink, @linkedObjectId = linkedObjectId,
            @description = description
    FROM    ibs_Object
    WHERE   oid = @oid


    INSERT INTO ibs_Object
           (tVersionId, name, containerId, containerKind, containerOid2,
            isLink, linkedObjectId, owner, creator, changer, 
            validUntil, description)
    VALUES (@tVersionId, @name, @containerId, @containerKind, @l_containerOid2,
            @isLink, @linkedObjectId, @userId, @userId, @userId, 
            DATEADD (month, 3, getDate ()), @description)


    SELECT  @newOid = oid
    FROM    ibs_Object
    WHERE   id = (  SELECT  MAX (id)
                    FROM    ibs_Object
                    WHERE   tVersionId = @tVersionId
                        AND name = @name
                        AND containerId = @containerId
                        AND containerKind = @containerKind
                        AND containerOid2 = @l_containerOid2
                        AND isLink = @isLink
                        AND linkedObjectId = @linkedObjectId)

    -- return the state value
    RETURN  @retValue
GO
