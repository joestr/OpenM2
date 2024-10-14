/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 *
 * @version     $Id: Document_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ)  980729
 *****************************************************************************/


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

EXEC p_dropProc N'p_Document_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Document_01$create
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
    -- output parameters:
    @oid_s              OBJECTIDSTRING OUTPUT
)
AS
    -- declare properties
    DECLARE @oid            OBJECTID
    DECLARE @masterId       OBJECTID
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID
    DECLARE @partofId_s     OBJECTIDSTRING 

    --init properties
    SELECT  @masterId   = 0x0000000000000000
    SELECT  @oid        = 0x0000000000000000

    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2 -- return values
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    DECLARE @partofTVersionId    TVERSIONID
    SELECT  @partofTVersionId =  0x01010061 -- tVersionId of the AttachmentContainer

    BEGIN TRANSACTION
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Document_01$create




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
 * @param   @description        Description of the object.
 * @param   @showInNews         Show in news flag.
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
 * ALL_RIGHT                    Action performed, values returned, everything ok.
 * INSUFFICIENT_RIGHTS          User has no right to perform action.
 * OBJECTNOTFOUND               The required object was not found within the 
 *                              database.
 */

EXEC p_dropProc N'p_Document_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Document_01$retrieve
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

    @attachmentContainerId_s OBJECTIDSTRING     OUTPUT, 
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
    DECLARE @oid                OBJECTID
    DECLARE @masterRights       RIGHTS
    DECLARE @necessaryRights    RIGHTS    


	-- initialize local variables:
    DECLARE @masterId OBJECTID              -- id of the new master
    DECLARE @partofTVersionId   TVERSIONID
    DECLARE @attachmentContainerId OBJECTID 
    DECLARE @Dummy INT
    SELECT  @partofTVersionId =  0x01010061 -- tVersionId of the AttachmentContainer
    SELECT  @attachmentType = -1
    SELECT  @fileName = N''
    SELECT  @url = N''
    SELECT  @path = N''
    SELECT  @masterId = 0x0000000000000000
    SELECT  @attachmentContainerId = 0x0000000000000000
    SELECT  @oid = 0x0000000000000000

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

        IF (@retValue = @ALL_RIGHT) -- object is processed properly
        BEGIN      	
            -- find the attachmentcontainer of the document
            SELECT @attachmentContainerId = oid
            FROM   ibs_object
            WHERE  ((containerId = @oid) AND (tVersionId = @partofTVersionId))

            -- ensures that in the attachment container is a master set
            EXEC p_Attachment_01$ensureMaster @attachmentContainerId, null 

            --search the actual master
            SELECT  @masterId = a.oid
    	    FROM    ibs_Attachment_01 a, ibs_Object o
	        WHERE   o.containerId = @attachmentContainerId 
            AND     a.isMaster = 1
            AND     o.oid = a.oid
			AND     o.state = 2

            -- get the necessary rights:
            SELECT  @necessaryRights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read')

            -- get rights of the user for the master attachment
            EXEC @masterRights = p_Rights$checkRights
                @masterId,                      -- given object to be accessed by user
                @attachmentContainerId,         -- container of given object
                @userId,                        -- user_id
                @necessaryRights,               -- required rights user must have
                @masterRights OUTPUT            -- returned value
     
            -- check if the user has the necessary rights
            IF (@masterRights = @necessaryRights)   -- the user has the rights?                        
            BEGIN            
                -- read out the properties
                SELECT  @fileName = filename,
                        @url = url,
                        @path = path,
                        @attachmentType = attachmentType
                FROM    ibs_Attachment_01
                WHERE   oid = @masterId
            END
            
            -- convert to output
            EXEC    p_byteToString @attachmentContainerId, @attachmentContainerId_s OUTPUT
            EXEC    p_byteToString @masterId, @masterId_s OUTPUT

        END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Document_01$retrieve


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
 * @param   @showInNews         Display object in News or not.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Document_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Document_01$change
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_retValue             INT,            -- return value of this procedure    
    @l_containerId          OBJECTID,       -- container Id of the object
    @l_tVersionId           TVERSIONID,     -- tVersion Id of the object
    @l_isMaster             BOOL,
    @l_oid                  OBJECTID        -- converted input parameter
                                            -- oid_s

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID


    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, 
                @ai_op, @ai_name, @ai_validUntil, @ai_description, 
                @ai_showInNews, @l_oid OUTPUT
    
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Document_01$change

