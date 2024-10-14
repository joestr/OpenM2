/******************************************************************************
 * All stored procedures regarding the ibs_attachment_01 table. <BR>
 * 
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Heinz Stampfer (KR)  980521
 *
 * <DT><B>Updates:</B>
 * 
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */

/******************************************************************************
 * Attachment_01Proc is splited into Attachment_01Proc1 and Attachment_01Proc2
 * because of a cyclic-dependency. <BR>
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_userId              ID of the user who is creating the object.
 * @param   @ai_op                  Operation to be performed (used for rights 
 *                                  check).
 * @param   @ai_tVersionId          Type of the new object.
 * @param   @ai_name                Name of the object.
 * @param   @ai_containerId_s       ID of the container where object shall be 
 *                                  created in.
 * @param   @ai_containerKind       Kind of object/container relationship
 * @param   @ai_isLink              Defines if the object is a link
 * @param   @ai_linkedObjectId_s    If the object is a link this is the ID of
 *                                  the where the link shows to.
 * @param   @ai_description         Description of the object.
 *
 * @output parameters:
 * @param   @ao_oid_s               OID of the newly created object.
 *
 * @returns A value representing the state of the procedure.
 *  @c_ALL_RIGHT                    Action performed, values returned,
 *                                  everything ok.
 *  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
 *  @c_NOT_OK                       Something went wrong.
 */
 
-- delete existing procedure: 
EXEC p_dropProc N'p_Attachment_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Attachment_01$create
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
    @ao_oid_s               OBJECTIDSTRING  OUTPUT
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                INT,            -- oid of no valid object
    @c_MAS_FILE             INT,            -- constant for file Attachment
    @c_MAS_HYPERLINK        INT,            -- constant for hyperlink Attachment
    @c_TVAttachment         TVERSIONID,     -- tVersionId of Attachment

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_oid                  OBJECTID,       -- oid of the object
    @l_masterId             OBJECTID,       -- masterid of the object
    @l_containerId          OBJECTID,       -- id of the container object
    @l_linkedObjectId       OBJECTID,       -- id to where the link shows to
    @l_documentId           OBJECTID,       -- id of the owner document
    @l_attachmentFor        NVARCHAR(255)   -- text which contains the name of
                                            -- the owner object

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1, 
    @c_NOOID                = 0x0000000000000000,
    @c_MAS_FILE             = 1, 
    @c_MAS_HYPERLINK        = 2,
    @c_TVAttachment         = 0x01010051
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_masterId             = @c_NOOID,
    @l_oid                  = @c_NOOID,
    @l_documentId           = @c_NOOID,
    @l_attachmentFor        = ''


    --body:
    BEGIN TRANSACTION
        -- CONVERSIONS (OBJECTIDSTRING) 
        EXEC p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT
        EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT

        -- set the value for default language (english)
        SELECT  @l_attachmentFor = 'attachment for'

	    --  The defaultname is set only when it is no File or Url Type 
        IF (@ai_tVersionId = @c_TVAttachment) -- is abject an attachment ?
        BEGIN
            SELECT  @ai_name = @l_attachmentFor + ' ' + name 
            FROM    ibs_object
            WHERE   oid IN (SELECT  containerId
                            FROM    ibs_Object
                            WHERE   oid = @l_containerId
                           )
        END -- if is abject an attachment

        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_NOT_OK
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op,
                @ai_tVersionId, @ai_name, @ai_containerId_s, @ai_containerKind,
                @ai_isLink, @ai_linkedObjectId_s, @ai_description,
                @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- no error occurred?
        BEGIN
 	        INSERT INTO ibs_Attachment_01
 	                    (oid, filename, path, filesize, url, attachmentType,
 	                     isMaster)
	        VALUES      (@l_oid, '', '', 0.0,'', 1, 0) 
        END  -- if no error occurred

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Attachment_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_oid_s               ID of the object to be changed.
 * @param   @ai_userId              ID of the user who is creating the object.
 * @param   @ai_op                  Operation to be performed (used for rights
 *                                  check).
 * @param   @ai_name                Name of the object.
 * @param   @ai_validUntil          Date until which the object is valid.
 * @param   @ai_description         Description of the object.
 * @param   @ai_showInNews          Display object in the news.
 *
 * @param   @ai_isMaster            Is true if the attachment is a master.
 * @param   @ai_attachmentType      Is the type of the attachment.
 * @param   @ai_filename            The filename of the attachment.
 * @param   @ai_path                The path of the attachment.
 * @param   @ai_url                 The Hyperlink of the attachment.
 * @param   @ai_filesize            The size of the attachment.
 * @param   @ai_isWeblink           Is true if the flag 32 is set (flag 32 is
 *                                  set when the attachment is a weblink).
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  @c_ALL_RIGHT                    Action performed, values returned,
 *                                  everything ok.
 *  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
 *  @c_OBJECTNOTFOUND               The required object was not found within
 *                                  the database.
 */
 
-- delete existing procedure: 
EXEC p_dropProc N'p_Attachment_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Attachment_01$change
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,

    @ai_isMaster            BOOL,
    @ai_attachmentType      INT,
    @ai_filename	        NVARCHAR(255),
    @ai_path	            NVARCHAR(255),
    @ai_url 	            NVARCHAR(255),
    @ai_filesize	        REAL,
    @ai_isWeblink           BOOL
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object    
    @c_TVAttachment         TVERSIONID,     -- tVersionId of Attachment
    @c_MAS_FILE             INT,            -- constant for file Attachment
    @c_MAS_HYPERLINK        INT,            -- constant for hyperlink Attachment
    @c_WEBLINK              INT,            -- constant for weblink

    -- local variables:
    @l_retValue             INT,            -- return value of this procedure    
    @l_containerId          OBJECTID,       -- container Id of the object
    @l_tVersionId           TVERSIONID,     -- tVersion Id of the object
    @l_masterId             OBJECTID,       -- id of the new master
    @l_isMaster             BOOL,           -- converted input parameter
                                            -- @ai_isMaster
    @l_oid                  OBJECTID        -- converted input parameter
                                            -- @ai_oid_s

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000,
    @c_TVAttachment         = 0x01010051,
    @c_MAS_FILE             = 1,
    @c_MAS_HYPERLINK        = 2,
    @c_WEBLINK              = 32
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID,
    @l_isMaster             = 0,
    @l_masterId             = null


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, @ai_op,
                @ai_name, @ai_validUntil, @ai_description, @ai_showInNews,
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed ?
        BEGIN
            SELECT  @l_containerId = containerId,
                    @l_tVersionId = tVersionId
            FROM    ibs_Object
            WHERE   oid = @l_oid

            -- update further information:
            UPDATE  ibs_Attachment_01
            SET     attachmentType = @ai_attachmentType,
                    filename = @ai_filename,
                    path = @ai_path,
                    url = @ai_url,
                    filesize = @ai_filesize
            WHERE   oid=@l_oid

            -- updates the flags of the object if it is a weblink
            IF (@ai_isWeblink = 1)          -- is the object a weblink ?
            BEGIN
                -- updates the flags of the object
                UPDATE  ibs_object          -- set the selected property
                                            -- isWeblink
                SET     flags = (flags  | @c_WEBLINK)
                WHERE   oid = @l_oid
            END -- if is the object a weblink
            ELSE -- the object is not a weblink
            BEGIN
                UPDATE  ibs_object          -- delete the selected property
                                            -- isWeblink
                SET     flags = (flags & (0xFFFFFFFF ^ @c_WEBLINK))
                WHERE   oid = @l_oid
            END -- else the object is not a weblink

            IF (@l_tVersionId = @c_TVAttachment) -- is it an attachment ?
	        BEGIN
	            IF (@ai_isMaster = 1)       -- the object is a master
	                SELECT @l_masterId = @l_oid
	                
	            -- ensures that in the attachment container is a master set
	            EXEC p_Attachment_01$ensureMaster @l_containerId, @l_masterId
	
            END -- if is it an attachment
	        ELSE IF (@l_tVersionId != @c_TVAttachment) -- is it no attachment ?
            BEGIN
                -- set the flags to sero
                UPDATE  ibs_object
                SET     flags = (flags & (0xFFFFFFFF ^ (@c_MAS_FILE | @c_MAS_HYPERLINK)))
                WHERE   oid = @l_oid

                IF (@ai_filename != '')     -- is a filename set ?
                BEGIN
                    IF (@ai_attachmentType = @c_MAS_FILE) 
                    BEGIN
                        UPDATE  ibs_object -- set the selected property isMaster
                        SET     flags = (flags  | @c_MAS_FILE)
                        WHERE   oid = @l_oid
                    END
                END -- if is a filename set
                
                IF (@ai_url != '') -- is a url set ?
                BEGIN
                    IF (@ai_attachmentType = @c_MAS_HYPERLINK)
                    BEGIN
                        UPDATE  ibs_object -- set the selected property isMaster
                        SET     flags = (flags  | @c_MAS_HYPERLINK)
                        WHERE   oid = @l_oid
                    END
                END -- if is a url set
            END -- if is it no attachment
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue

GO
-- p_Attachment_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_oid_s               ID of the object to be changed.
 * @param   @ai_userId              ID of the user who is creating the object.
 * @param   @ai_op                  Operation to be performed (used for rights 
 *                                  check).
 *
 * @output parameters:
 * @param   @ao_state               The object's state.
 * @param   @ao_tVersionId          ID of the object's type (correct version).
 * @param   @ao_typeName            Name of the object's type.
 * @param   @ao_name                Name of the object itself.
 * @param   @ao_containerId         ID of the object's container.
 * @param   @ao_containerName       Name of the object's container.
 * @param   @ao_containerKind       Kind of object/container relationship.
 * @param   @ao_isLink              Is the object a link?
 * @param   @ao_linkedObjectId      Link if isLink is true.
 * @param   @ao_owner               ID of the owner of the object.
 * @param   @ao_ownerName           Name of the owner of the object.
 * @param   @ao_creationDate        Date when the object was created.
 * @param   @ao_creator             ID of person who created the object.
 * @param   @ao_creatorName         Name of person who created the object.
 * @param   @ao_lastChanged         Date of the last change of the object.
 * @param   @ao_changer             ID of person who did the last change to the
 *                                  object.
 * @param   @ao_changerName         Nameof person who did the last change to
 *                                  the object.
 * @param   @ao_validUntil          Date until which the object is valid.
 * @param   @ao_description         Description of the object.
 * @param   @ao_showInNews          Display the object in the news.
 * @param   @ao_checkedOut          Is the object checked out?
 * @param   @ao_checkOutDate        Date when the object was checked out.
 * @param   @ao_checkOutUser        ID of the user which checked out the object
 * @param   @ao_checkOutUserOid     Oid of the user which checked out the object
 *                                  is only set if this user has the right to
 *                                  READ the checkOut user.
 * @param   @ao_checkOutUserName    Name of the user which checked out the
 *                                  object, is only set if this user has the
 *                                  right to view the checkOut-User.
 *
 * @param   @ao_isMaster            Is true if the attachment is a master.
 * @param   @ao_attachmentType      Is the type of the attachment.
 * @param   @ao_filename            The filename of the attachment.
 * @param   @ao_path                The path of the attachment.
 * @param   @ao_url                 The Hyperlink of the attachment.
 * @param   @ao_filesize            The size of the attachment.
 * @param   @ao_isWeblink           Is true if the flag 32 is set (flag 32 is
 *                                  set when the attachment is a weblink).
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  @c_ALL_RIGHT                    Action performed, values returned,
 *                                  everything ok.
 *  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
 *  @c_OBJECTNOTFOUND               The required object was not found within
 *                                  the database.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_Attachment_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Attachment_01$retrieve
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- output parameters
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

    @ao_isMaster            BOOL            OUTPUT, 
    @ao_attachmentType      INT             OUTPUT, 
    @ao_filename            NVARCHAR(255)   OUTPUT, 
    @ao_path                NVARCHAR(255)   OUTPUT, 
    @ao_url                 NVARCHAR(255)   OUTPUT, 
    @ao_filesize            REAL            OUTPUT,
    @ao_isWeblink           BOOL            OUTPUT
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_WEBLINK              INT,            -- constant for weblink

    -- local variables:
    @l_retValue             INT,            -- return value of this procedure    
    @l_oid                  OBJECTID        -- converted input parameter
                                            -- oid_s

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000,
    @c_WEBLINK              = 32
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID


    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
        EXEC @l_retValue = p_Object$performRetrieve
                @ai_oid_s, @ai_userId, @ai_op, @ao_state OUTPUT,
                @ao_tVersionId OUTPUT, @ao_typeName OUTPUT, @ao_name OUTPUT,
                @ao_containerId OUTPUT, @ao_containerName OUTPUT,
                @ao_containerKind OUTPUT, @ao_isLink OUTPUT,
                @ao_linkedObjectId OUTPUT, @ao_owner OUTPUT,
                @ao_ownerName OUTPUT, @ao_creationDate OUTPUT,
                @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT,
                @ao_changerName OUTPUT, @ao_validUntil OUTPUT,
                @ao_description OUTPUT, @ao_showInNews OUTPUT, 
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT, 
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT, 
                @ao_checkOutUserName OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed ?
        BEGIN
            SELECT
                @ao_isMaster = isMaster, 
                @ao_attachmentType = attachmentType, 
                @ao_filename = filename, 
                @ao_path = path, 
                @ao_url  = url, 
                @ao_filesize = filesize
	        FROM ibs_Attachment_01
	        WHERE oid=@l_oid
	        
	        -- get the 6th bit out of the flags
	        -- it is set, when the object is a weblink
	        SELECT  @ao_isWeblink = (flags & @c_WEBLINK)
	        FROM    ibs_Object
	        WHERE   oid = @l_oid
        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Attachment_01$retrieve


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @ai_oid_s               ID of the object to be deleted.
 * @param   @ai_userId              ID of the user who is deleting the object.
 * @param   @ai_op                  Operation to be performed (used for rights
 *                                  check).
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  @c_ALL_RIGHT                    Action performed, values returned,
 *                                  everything ok.
 *  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
 *  @c_OBJECTNOTFOUND               The required object was not found within
 *                                  the database.
 */
 
-- delete existing procedure: 
EXEC p_dropProc N'p_Attachment_01$delete'
GO

CREATE PROCEDURE p_Attachment_01$delete
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object    
    @c_TVAttachment         TVERSIONID,     -- tVersionId of Attachment

    -- local variables:
    @l_retValue             INT,            -- return value of this procedure    
    @l_containerId          OBJECTID,       -- container Id of the object
    @l_tVersionId           TVERSIONID,     -- tVersion Id of the object
    @l_isMaster             BOOL,           -- converted input parameter
                                            -- @ai_isMaster
    @l_oid                  OBJECTID        -- converted input parameter
                                            -- @ai_oid_s

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000,
    @c_TVAttachment         = 0x01010051
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID,
    @l_isMaster             = 0


    -- body:
    BEGIN TRANSACTION
        -- conversions (objectidstring) - all input objectids must be converted
        EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

        -- read out the tVersionID for File and URL
        SELECT  @l_tVersionId = tVersionId, 
                @l_containerId = containerId
        FROM    ibs_object 
        WHERE   oid = @l_oid

        -- all references and the object itself are deleted (plus rights)
        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
        EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId, @ai_op

        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed ?
        BEGIN
            SELECT @l_isMaster = isMaster
            FROM   ibs_Attachment_01
            WHERE  oid = @l_oid

            -- if we delete a master attachment, we must find a other one
	        IF ((@l_isMaster = 1) AND (@l_tVersionId = @c_TVAttachment))
                EXEC p_Attachment_01$ensureMaster @l_containerId, null
	    END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue

GO
-- p_Attachment_01$delete


/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @ai_oid                 ID of the object to be copied.
 * @param   @ai_userId              ID of the user who copy the object.
 * @param   @ai_newOid              The new Oid of the new created Business
 *                                  Object.
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  @c_ALL_RIGHT                    Action performed, values returned,
 *                                  everything ok.
 *  @c_NOT_OK                       Something went wrong.
 */
 
-- delete existing procedure: 
EXEC p_dropProc N'p_Attachment_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Attachment_01$BOCopy
(
    -- input parameters:
    @ai_oid                 OBJECTID,       -- the oid of Object we want to
                                            -- copy
    @ai_userId              USERID,         -- the userId of the User who wants
                                            -- to copy
    @ai_newOid              OBJECTID        -- the new OID of the copied Object
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.
    @c_NOT_OK               INT,            -- something went wrong

    -- local variables:
    @l_retValue             INT             -- return value of this procedure    


    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOT_OK               = 0
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_NOT_OK
  
    -- body:

-- ****************************************************************************
-- Hier sollte normalerweise ein Überprüfung erfollgen, ob die Beilage sich in
-- einem Container befindet, wo es schon einen Master gibt, wenn nicht, dann
-- sollte dieser natürlich gesetzt werden, und ebenfalls sollten die flags in
-- der Tabelle ibs_Object richtig gesetzt werden.
-- Ist zurzeit als HACK in p_Object$copy mit der StoredProzedure 
-- p_Attachment_01$ensureMaster gelöst.  DJ 24. August 2000
-- ****************************************************************************

    -- make a insert for all your typespecific tables:
    INSERT  INTO ibs_Attachment_01
            (oid, filename, path, filesize,
             url, attachmentType, isMaster)
    SELECT  @ai_newOid, filename, path, filesize, 
            url, attachmentType, isMaster
    FROM    ibs_Attachment_01
    WHERE   oid = @ai_oid
    
    -- check if the insert has processed correctly:
    IF (@@ROWCOUNT >= 1)
        SELECT @l_retvalue = @c_All_RIGHT

    -- return the state value:
    RETURN @l_retValue
GO
-- p_Attachment_01$BOCopy
