/******************************************************************************
 * All stored procedures regarding the EDITranslator_01 Object. <BR>
 *
 * @version     2.21.0001, 27.02.2002 KR
 *
 * @author      Klaus Reimüller (KR) 020227
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
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
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_EDITranslator_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_EDITranslator_01$create
(
    -- common input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_tVersionId          TVERSIONID,
    @ai_name                NAME,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_containerKind       INT,
    @ai_isLink              BOOL,
    @ai_linkedObjectId_s    OBJECTIDSTRING,
    @ai_description         DESCRIPTION,
    -- common output parameters:
    @ao_oid_s               OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_ALREADY_EXISTS       INT,            -- the object exists already

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_containerId          OBJECTID,       -- oid of the container object
    @l_linkedObjectId       OBJECTID,       -- oid of the linked object
    @l_oid                  OBJECTID        -- the oid of the object

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK,
    @l_error = 0,
    @l_oid = @c_NOOID

-- body:
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    EXEC    p_stringToByte @ai_containerId_s, @l_containerId OUTPUT
    EXEC    p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT

/* no transactions allowed because already defined in p_Translator_01$create
    BEGIN TRANSACTION
*/
        -- create base object:
        EXEC @l_retValue = p_Translator_01$create
                            @ai_userId, @ai_op, @ai_tVersionId,
                            @ai_name, @ai_containerId_s, @ai_containerKind,
                            @ai_isLink, @ai_linkedObjectId_s, @ai_description,
                            @ao_oid_s OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- object created successfully?
        BEGIN
            -- convert the oid:
            EXEC p_stringToByte @ao_oid_s, @l_oid OUTPUT
            -- create object type specific data:
            INSERT INTO ibs_EDITranslator_01 (oid, filterFile, formatFile)
            VALUES  (@l_oid, N'',N'')
        END -- if object created successfully

/* no transactions allowed because already defined in p_Translator_01$create
    COMMIT TRANSACTION
*/

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_EDITranslator_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_showInNews       Display object in the news.
 *
 * @param   ai_isMaster         Is true if the attachment is a master.
 * @param   ai_attachmentType   Is the type of the attachment.
 * @param   ai_filename         The filename of the attachment.
 * @param   ai_path             The path of the attachment.
 * @param   ai_url              The Hyperlink of the attachment.
 * @param   ai_filesize         The size of the attachment.
 * @param   ai_isWeblink        Is true if the flag 32 is set (flag 32 is
 *                              set when the attachment is a weblink).
 *
 * @param   ai_extension        The extension of the generated output file.
 * @param   ai_filterFile       The name of the file for filtering the input.
 * @param   ai_formatFile       The name of the file with the format definition.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_EDITranslator_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_EDITranslator_01$change
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    -- attachment-specific input parameters:
    @ai_isMaster            BOOL,
    @ai_attachmentType      INT,
    @ai_filename	        NVARCHAR (255),
    @ai_path	            NVARCHAR (255),
    @ai_url 	            NVARCHAR (255),
    @ai_filesize	        REAL,
    @ai_isWeblink           BOOL,
    -- Translator-specific input parameters:
    @ai_extension           NVARCHAR (15),
    -- EDITranslator-specific input parameters:
    @ai_filterFile          FILENAME,
    @ai_formatFile          FILENAME
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_oid                  OBJECTID        -- the oid of the object

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK,
    @l_error = 0,
    @l_oid = @c_NOOID

-- body:
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

/* no transactions allowed because already defined in p_Translator_01$change
    BEGIN TRANSACTION
*/
        -- perform the change of the object:
        EXEC @l_retValue = p_Translator_01$change @ai_oid_s, @ai_userId, @ai_op,
                @ai_name, @ai_validUntil, @ai_description, @ai_showInNews,
                @ai_isMaster, @ai_attachmentType, @ai_filename, @ai_path,
                @ai_url, @ai_filesize, @ai_isWeblink, @ai_extension

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed successfully?
        BEGIN
            -- update further information:
	        UPDATE  ibs_EDITranslator_01
	        SET     filterFile = @ai_filterFile,
                    formatFile = @ai_formatFile
	        WHERE   oid = @l_oid
        END -- if operation performed successfully
/* no transactions allowed because already defined in p_Translator_01$change
    COMMIT TRANSACTION
*/

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_EDITranslator_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerName    Name of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_ownerName        Name of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_creatorName      Name of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_changerName      Nameof person who did the last change to
 *                              the object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Display the object in the news.
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out.
 * @param   ao_checkOutUser     ID of the user which checked out the object
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to
 *                              READ the checkOut user.
 * @param   ao_checkOutUserName Name of the user which checked out the
 *                              object, is only set if this user has the
 *                              right to view the checkOut-User.
 *
 * @param   ao_isMaster         Is true if the attachment is a master.
 * @param   ao_attachmentType   Is the type of the attachment.
 * @param   ao_filename         The filename of the attachment.
 * @param   ao_path             The path of the attachment.
 * @param   ao_url              The Hyperlink of the attachment.
 * @param   ao_filesize         The size of the attachment.
 * @param   ao_isWeblink        Is true if the flag 32 is set (flag 32 is
 *                              set when the attachment is a weblink).
 *
 * @param   ao_extension        The extension of the generated output file.
 * @param   ao_filterFile       The name of the file for filtering the input.
 * @param   ao_formatFile       The name of the file with the format definition.
 *
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_EDITranslator_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_EDITranslator_01$retrieve
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- output parameters:
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
    -- attachment-specific output parameters:
    @ao_isMaster            BOOL            OUTPUT,
    @ao_attachmentType      INT             OUTPUT,
    @ao_filename            NVARCHAR(255)   OUTPUT,
    @ao_path                NVARCHAR(255)   OUTPUT,
    @ao_url                 NVARCHAR(255)   OUTPUT,
    @ao_filesize            REAL            OUTPUT,
    @ao_isWeblink           BOOL            OUTPUT,
    -- Translator-specific output parameters:
    @ao_extension           NVARCHAR (15)   OUTPUT,
    -- EDITranslator-specific output parameters:
    @ao_filterFile          FILENAME        OUTPUT,
    @ao_formatFile          FILENAME        OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_oid                  OBJECTID        -- the oid of the object

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK,
    @l_error = 0,
    @l_oid = @c_NOOID

-- body:
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

/* no transactions allowed because already defined in p_Translator_01$retrieve
    BEGIN TRANSACTION
*/
        -- retrieve the attachment data:
        EXEC @l_retValue = p_Translator_01$retrieve
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
                @ao_checkOutUserName OUTPUT,
                @ao_isMaster OUTPUT, @ao_attachmentType OUTPUT,
                @ao_filename OUTPUT, @ao_path OUTPUT,
                @ao_url OUTPUT, @ao_filesize OUTPUT,
                @ao_isWeblink OUTPUT, @ao_extension OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed successfully?
        BEGIN
            -- retrieve the type specific data:
            SELECT  @ao_filterFile = filterFile,
                    @ao_formatFile = formatFile
	        FROM    ibs_EDITranslator_01
	        WHERE   oid = @l_oid
        END -- if operation performed successfully
/* no transactions allowed because already defined in p_Translator_01$retrieve
    COMMIT TRANSACTION
*/

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_EDITranslator_01$retrieve


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   ai_oid              Oid of group to be copied.
 * @param   ai_userId           Id of user who is copying the group.
 * @param   ai_newOid           Oid of the new group.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 An error occurred.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_EDITranslator_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_EDITranslator_01$BOCopy
(
    -- common input parameters:
    @ai_oid                 OBJECTID,
    @ai_userId              USERID,
    @ai_newOid              OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INTEGER         -- counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_error                = 0,
    @l_count                = 0,
    @l_retValue             = @c_NOT_OK

-- body:
/* no transactions allowed because already defined in p_Translator_01$BOCopy
    BEGIN TRANSACTION -- begin new TRANSACTION
*/

        -- copy base object:
        EXEC @l_retValue =
            p_Translator_01$BOCopy @ai_oid, @ai_userId, @ai_newOid

        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- make an insert for all type specific tables:
            -- (it's currently not possible to copy files!)
            INSERT INTO ibs_EDITranslator_01
                    (oid, filterFile, formatFile)
            SELECT  @ai_newOid, '', ''
            FROM    ibs_EDITranslator_01
            WHERE   oid = @ai_oid

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                N'insert new edi translator data', @l_ePos OUTPUT, @l_count OUTPUT
            IF (@l_error <> 0 OR @l_count <> 1) -- an error occurred?
                GOTO exception              -- call common exception handler
	    END -- if operation properly performed

/* no transactions allowed because already defined in p_Translator_01$BOCopy
    -- finish the transaction:
    -- check if there occurred an error:
    IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
        COMMIT TRANSACTION          -- make changes permanent
    ELSE                            -- an error occured
        ROLLBACK TRANSACTION        -- undo changes
*/

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
/* no transactions allowed because already defined in p_Translator_01$BOCopy
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
*/
    -- log the error:
    EXEC ibs_error.logError 500, N'p_EDITranslator_01$BOCopy', @l_error,
            @l_ePos,
            N'ai_userId', @ai_userId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_EDITranslator_01$BOCopy
