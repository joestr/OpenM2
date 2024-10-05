/******************************************************************************
 * All stored procedures regarding the XMLViewerContainer_01 Object. <BR>
 * 
 * @version     $Id: XMLViewerContainer_01Proc.sql,v 1.13 2009/12/02 18:35:02 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB)  990409
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new XMLViewerContainer_01 Object (incl. rights check). <BR>
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
EXEC p_dropProc N'p_XMLViewerContainer_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewerContainer_01$create
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
    DECLARE @templateOid OBJECTID    
	-- initialize local variables:
    SELECT  @oid = 0x0000000000000000
    SELECT  @templateOid = 0x0000000000000000

    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT

	    IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- insert the other values
            INSERT INTO ibs_XMLViewerContainer_01 (oid, useStandardHeader, templateOid, 
                                                   headerFields, workflowTemplateOid, workflowAllowed)
            VALUES (@oid, 1, @templateOid , N'', @templateOid, 1)
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO 
-- p_XMLViewerContainer_01$create


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
 * @param   @useStandardHeader  Flag to use standard list headers
 * @param   @templateOid        oid of the documentTemplate to attach
 * @param   @headerFields       alternative fields to use as header in the list  
 *  
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- delete existing procedure: 
EXEC p_dropProc N'p_XMLViewerContainer_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewerContainer_01$change
(
    -- input parameters:
    @oid_s              OBJECTIDSTRING,
    @userId             USERID,
    @op                 INT,
    @name               NAME,
    @validUntil         DATETIME,
    @description        DESCRIPTION,    
    @showInNews         BOOL,
    -- type-specific attributes:    
    @useStandardHeader  BIT,
    @templateOid_s      OBJECTIDSTRING,
    @headerFields       NVARCHAR(255),
    @wftemplateOid_s    OBJECTIDSTRING,
    @workflowAllowed    BOOL
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
	DECLARE @templateOid OBJECTID     
	DECLARE @wftemplateOid OBJECTID     
	-- initialize local variables:

    -- convertions: (OBJECTIDSTRING) - all input object ids must be converted
    EXEC p_stringToByte @templateOid_s, @templateOid OUTPUT
    EXEC p_stringToByte @wftemplateOid_s, @wftemplateOid OUTPUT

    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update the other values
            UPDATE ibs_XMLViewerContainer_01
            SET     useStandardHeader = @useStandardHeader,
                    templateOid = @templateOid,
                    headerFields = @headerFields,
                    workflowTemplateOid = @wftemplateOid,
                    workflowAllowed = @workflowAllowed
            WHERE oid = @oid

        END
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO 
-- p_XMLViewerContainer_01$change


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
 * @param   @useStandardHeader  Flag to use standard list headers
 * @param   @templateOid        oid of the documentTemplate to attach
 * @param   @templateTVersionId tVersionId from the documentTemplate attached
 * @param   @headerFields       alternative fields to use as header in the list  
 * @param   @templateName       name of the template
 * @param   @templateFileName   filename of template
 * @param   @templatePath       path of template
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- delete existing procedure: 
EXEC p_dropProc N'p_XMLViewerContainer_01$retr'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewerContainer_01$retr
(
    -- input parameters:
    @oid_s              OBJECTIDSTRING,
    @userId             USERID,
    @op                 INT,
    -- output parameters
    @state              STATE           OUTPUT,
    @tVersionId         TVERSIONID      OUTPUT,
    @typeName           NAME            OUTPUT,
    @name               NAME            OUTPUT,
    @containerId        OBJECTID        OUTPUT,
    @containerName      NAME            OUTPUT,
    @containerKind      INT             OUTPUT,
    @isLink             BOOL            OUTPUT,
    @linkedObjectId     OBJECTID        OUTPUT,
    @owner              USERID          OUTPUT,
    @ownerName          NAME            OUTPUT,
    @creationDate       DATETIME        OUTPUT,
    @creator            USERID          OUTPUT,
    @creatorName        NAME            OUTPUT,
    @lastChanged        DATETIME        OUTPUT,
    @changer            USERID          OUTPUT,
    @changerName        NAME            OUTPUT,
    @validUntil         DATETIME        OUTPUT,
    @description        DESCRIPTION     OUTPUT,
    @showInNews         BOOL            OUTPUT,
    @checkedOut         BOOL            OUTPUT,
    @checkOutDate       DATETIME        OUTPUT,
    @checkOutUser       USERID          OUTPUT,
    @checkOutUserOid    OBJECTID        OUTPUT,
    @checkOutUserName   NAME            OUTPUT,
    -- type-specific attributes:    
    @useStandardHeader  BIT             OUTPUT,
    @templateOid        OBJECTID        OUTPUT,
    @templateTVersionId TVERSIONID      OUTPUT,
    @headerFields       NVARCHAR(255)   OUTPUT,
    @templateName       NVARCHAR(255)   OUTPUT,
    @templateFileName   NVARCHAR(255)   OUTPUT,
    @templatePath       NVARCHAR(255)   OUTPUT,
    @wftemplateOid      OBJECTID        OUTPUT,
    @workflowAllowed    BOOL            OUTPUT,
    @wftemplateName     NVARCHAR(255)   OUTPUT,
    @wftemplateFileName NVARCHAR(255)   OUTPUT,
    @wftemplatePath     NVARCHAR(255)   OUTPUT
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
    SELECT @templateName = N''
    SELECT @templateFileName = N''
    SELECT @templatePath = N''            
    SELECT @wftemplateName = N''
    SELECT @wftemplateFileName = N''
    SELECT @wftemplatePath = N''            

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
            
            SELECT @useStandardHeader = useStandardHeader,
                   @templateOid = templateOid,
                   @headerFields = headerFields,
                   @wftemplateOid = workflowTemplateOid,
                   @workflowAllowed = workflowAllowed
            FROM ibs_XMLViewerContainer_01
            WHERE oid = @oid

            -- check if we have a template 
            IF (@templateOid != 0x0000000000000000)
            BEGIN
                -- get the tVersionId from the template
                SELECT @templateTVersionId = tVersionId
                FROM ibs_DocumentTemplate_01
                WHERE oid = @templateOid
                
                -- get the template name and filename
                SELECT @templateName = o.name, @templateFileName = a.filename,
                       @templatePath = a.path
                FROM ibs_object o, ibs_attachment_01 a
                WHERE o.oid = @templateOid
                AND a.oid = o.oid
            END
            -- check if we have a workflow template 
            IF (@wftemplateOid != 0x0000000000000000)
            BEGIN
                -- get the template name and filename
                SELECT @wftemplateName = o.name, @wftemplateFileName = a.filename,
                       @wftemplatePath = a.path
                FROM ibs_object o, ibs_attachment_01 a
                WHERE o.oid = @wftemplateOid
                AND a.oid = o.oid
            END
        END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO 
-- p_XMLViewerContainer_01$retr


/******************************************************************************
 * Deletes a XMLViewerContainer_01 object and all its values (incl. rights check). <BR>
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
EXEC p_dropProc N'p_XMLViewerContainer_01$delete'
GO

CREATE PROCEDURE p_XMLViewerContainer_01$delete
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
   BEGIN TRANSACTION
        -- all references and the object itself are deleted (plus rights)
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

        IF (@retValue = @ALL_RIGHT)
        BEGIN
            
            -- delete all values of object
            DELETE  ibs_XMLViewerContainer_01
            WHERE   oid = @oid
                    
        END	

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO 
-- p_XMLViewerContainer_01$delete



/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @oid              ID of the object to be copied.
 * @param   @userId           ID of the user who copy the object.
 * @param   @newOid           The new Oid of the new created BusinessObject.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_XMLViewerContainer_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewerContainer_01$BOCopy
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
    INSERT INTO ibs_XMLViewerContainer_01 
            (oid, useStandardHeader, templateOid, headerFields, 
             workflowTemplateOid, workflowAllowed)
    SELECT  @newOid, b.useStandardHeader, b.templateOid, b.headerFields, 
            b.workflowTemplateOid, b.workflowAllowed
    FROM    ibs_XMLViewerContainer_01 b
    WHERE   b.oid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @retValue
GO 
-- p_XMLViewerContainer_01$BOCopy
