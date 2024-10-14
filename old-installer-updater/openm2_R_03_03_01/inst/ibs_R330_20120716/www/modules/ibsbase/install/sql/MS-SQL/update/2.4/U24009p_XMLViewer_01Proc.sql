/******************************************************************************
 * All stored procedures regarding the XMLViewer_01 Object. <BR>
 *
 *
 * @version     1.10.0001, 17.05.2000
 *
 * @author      Christine Keim (CK)  000517
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new XMLViewer_01 Object (incl. rights check). <BR>
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
EXEC p_dropProc 'p_XMLViewer_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewer_01$create
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
    DECLARE @l_path                 VARCHAR (255)
    DECLARE @l_containerTVersionId  INT
    DECLARE @l_oid                  OBJECTID
    DECLARE @l_templateOid          OBJECTID
    DECLARE @l_wfTemplateOid        OBJECTID

	-- initialize local variables:
    SELECT  @l_oid = 0x0000000000000000
    SELECT  @l_templateOid = 0x0000000000000000
    SELECT  @l_wfTemplateOid = 0x0000000000000000

    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description,
                @oid_s OUTPUT, @l_oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
            SELECT @l_path = value + 'upload/files/'
	        FROM ibs_System
	        WHERE name = 'WWW_BASE_PATH'

            -- insert the other values
            INSERT INTO ibs_Attachment_01 (oid, filename, url, fileSize, path, attachmentType, isMaster)
            VALUES (@l_oid, 'xmldata.xml', '', 1, @l_path + @oid_s + RIGHT (@l_path, 1), 3, 0)

            -- get the template and workflow oid associated with the form.
            -- for old forms (tVersionId == XMLViewer_01) this values are set in Java.
            -- for new forms we can do this now.
            IF (@tVersionId != 0x01017501)  -- is a new form with its own tVersionId
            BEGIN
                SELECT @l_templateOid = oid, @l_wfTemplateOid = workflowTemplateOid
                FROM ibs_DocumentTemplate_01
                WHERE tVersionId = @tVersionId
            END

            -- if no workflow template is defined in the document template
            -- and the container is a XMLViewerContainer_01
            -- get the workflow template from the container.
            IF (@l_wfTemplateOid = 0x0000000000000000)
            BEGIN
                -- get the tVersionId of the container object
                SELECT @l_containerTVersionId = tVersionId
                FROM ibs_object
                WHERE oid = @containerId

                -- if the container is a XMLViewerContainer_01 or a ServicePoint_01
                -- get the workflow oid defined by the container.
                IF (@l_containerTVersionId = 0x01017e01 OR
                    @l_containerTVersionId = 0x01010191)
                BEGIN
                    SELECT @l_wfTemplateOid = workflowTemplateOid
                    FROM ibs_XMLViewerContainer_01
                    WHERE oid = @containerId
                END
            END

            -- insert the template and workflow oid in the ibs_xmlviewer_01 table
            INSERT INTO ibs_XMLViewer_01 (oid, templateOid, workflowTemplateOid)
            VALUES (@l_oid, @l_templateOid, @l_wfTemplateOid)

        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_XMLViewer_01$create

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
 * @param   @showInNews         Should the currrent object displayed in the news.
 * @param   @templateOid        oid of the document template object
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- delete existing procedure:
EXEC p_dropProc 'p_XMLViewer_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewer_01$change
(
    -- input parameters:
    @oid_s              OBJECTIDSTRING,
    @userId             USERID,
    @op                 INT,
    @name               NAME,
    @validUntil         DATETIME,
    @description        DESCRIPTION,
    @showInNews         BOOL,
    -- xml viewer specific input parameters:
    @templateOid_s      OBJECTIDSTRING,
    @wfTemplateOid_s    OBJECTIDSTRING
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @oid            OBJECTID
    DECLARE @templateOid    OBJECTID
    DECLARE @wfTemplateOid  OBJECTID
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews

        IF (@retValue = @ALL_RIGHT)
        BEGIN
            EXEC p_stringToByte @oid_s, @oid OUTPUT
            EXEC p_stringToByte @templateOid_s, @templateOid OUTPUT
            EXEC p_stringToByte @wfTemplateOid_s, @wfTemplateOid OUTPUT

            -- insert the template oid in the ibs_xmlviewer_01 table
            UPDATE ibs_XMLViewer_01
            SET templateOid = @templateOid,
                workflowTemplateOid = @wfTemplateOid
            WHERE oid = @oid

            IF (@@ROWCOUNT < 1)
                SELECT @retValue = @OBJECTNOTFOUND
        END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_XMLViewer_01$change

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
 * @param   @showInNews         Display the object in the news.
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
 * @param   @ao_templateOid     the oid of the template object
 * @param   @ao_systemDisplayMode  the display mode for the system section
 * @param   @ao_dbMapped        the dbMapped attribute from the template object
 * @param   @ao_mappingTable    the name of the mapping table from the template object
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- delete existing procedure:
EXEC p_dropProc 'p_XMLViewer_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewer_01$retrieve
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- output parameters
    @ao_state               STATE OUTPUT,
    @ao_tVersionId          TVERSIONID OUTPUT,
    @ao_typeName            NAME OUTPUT,
    @ao_name                NAME OUTPUT,
    @ao_containerId         OBJECTID OUTPUT,
    @ao_containerName       NAME OUTPUT,
    @ao_containerKind       INT OUTPUT,
    @ao_isLink              BOOL OUTPUT,
    @ao_linkedObjectId      OBJECTID OUTPUT,
    @ao_owner               USERID OUTPUT,
    @ao_ownerName           NAME OUTPUT,
    @ao_creationDate        DATETIME OUTPUT,
    @ao_creator             USERID OUTPUT,
    @ao_creatorName         NAME OUTPUT,
    @ao_lastChanged         DATETIME OUTPUT,
    @ao_changer             USERID OUTPUT,
    @ao_changerName         NAME OUTPUT,
    @ao_validUntil          DATETIME OUTPUT,
    @ao_description         DESCRIPTION OUTPUT,
    @ao_showInNews          BOOL OUTPUT,
    @ao_checkedOut          BOOL OUTPUT,
    @ao_checkOutDate        DATETIME OUTPUT,
    @ao_checkOutUser        USERID OUTPUT,
    @ao_checkOutUserOid     OBJECTID OUTPUT,
    @ao_checkOutUserName    NAME OUTPUT,

    @ao_templateOid         OBJECTID OUTPUT,
    @ao_wfTemplateOid       OBJECTID OUTPUT,
    @ao_systemDisplayMode   INT OUTPUT,
    @ao_dbMapped            BOOL OUTPUT,
    @ao_showDOMTree         BOOL OUTPUT
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_retValue             INT,            -- return value of this procedure
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
            --- get the template oid and the mapping attributes from the template
            EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT
            SELECT
                @ao_templateOid = v.templateOid,
                @ao_wfTemplateOid = v.workflowTemplateOid,
                @ao_systemDisplayMode = t.systemDisplayMode,
                @ao_dbMapped = t.dbMapped,
                @ao_showDOMTree = t.showDOMTree
            FROM ibs_XMLViewer_01 v, ibs_DocumentTemplate_01 t
            WHERE v.oid = @l_oid
            AND t.oid = v.templateOid
        END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_XMLViewer_01$retrieve

/******************************************************************************
 * Deletes a XMLViewer_01 object and all its values (incl. rights check). <BR>
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
EXEC p_dropProc 'p_XMLViewer_01$delete'
GO

CREATE PROCEDURE p_XMLViewer_01$delete
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
            DELETE  ibs_Attachment_01
            WHERE   oid = @oid
        END

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_XMLViewer_01$delete


-- delete existing procedure:
EXEC p_dropProc 'p_XMLViewer_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewer_01$BOCopy
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
    -- declare local variables
    DECLARE @path VARCHAR (255)
    DECLARE @oid_s OBJECTIDSTRING
    DECLARE @l_templateOid OBJECTID
    DECLARE @l_dbMapped BOOL
    DECLARE @l_showDOMTree BOOL
    DECLARE @l_procCopy VARCHAR (30)

    -- CONVERTIONS (OBJECTID)
    EXEC    p_byteToString @oid, @oid_s OUTPUT

    SELECT @path = value + 'upload/files/'
    FROM ibs_System
    WHERE name = 'WWW_BASE_PATH'

    -- make an insert for all type specific tables:
    INSERT INTO ibs_Attachment_01
            (oid, filename, url, fileSize, path, attachmentType, isMaster)
    SELECT  @newOid, b.filename, b.url, b.fileSize,
            @path + @oid_s + RIGHT (@path, 1),
            b.attachmentType, b.isMaster
    FROM    ibs_Attachment_01 b
    WHERE   b.oid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
    BEGIN
        -- make an insert in the ibs_xmlviewer_01 table
        INSERT INTO ibs_XMLViewer_01
                (oid, templateOid)
        SELECT  @newOid, templateOid
        FROM    ibs_XMLViewer_01
        WHERE   oid = @oid

        -- check if insert was performed correctly:
        IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        BEGIN
            -- for db-mapped objects we have to perform a insert in the
            -- mapping table too.

            SELECT @l_templateOid = templateOid
            FROM   ibs_XMLViewer_01
            WHERE oid = @newOid

            SELECT @l_dbMapped = 0
            SELECT @l_showDOMTree = 0
            SELECT @l_procCopy = ''

            SELECT @l_dbMapped = dbMapped,
                   @l_showDOMTree = showDOMTree,
                   @l_procCopy = procCopy
            FROM   ibs_DocumentTemplate_01
            WHERE  oid = @l_templateOid

            -- if the object is db-mapped perform the copy in the mapping table
            -- by calling the type specific copy procedure.
            IF (@l_dbMapped = 1)
            BEGIN
                EXEC @retValue = @l_procCopy @oid, @newOid
                if (@retValue = 1)
                    SELECT  @retValue = @ALL_RIGHT  -- set return value
            ELSE
                SELECT  @retValue = @ALL_RIGHT  -- set return value
            END
        END
    END

    -- return the state value:
    RETURN  @retValue
GO
-- p_XMLViewer_01$BOCopy
