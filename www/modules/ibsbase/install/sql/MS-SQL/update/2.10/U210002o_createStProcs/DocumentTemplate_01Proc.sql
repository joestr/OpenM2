/******************************************************************************
 * All stored procedures regarding the ibs_DocumentTemplate_01 table. <BR>
 *
 * @version     $Id: DocumentTemplate_01Proc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Michael Steiner (MS)
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
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
EXEC p_dropProc N'p_DocumentTemplate_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_DocumentTemplate_01$create
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
    @ao_oid_s               OBJECTIDSTRING OUTPUT
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,
    @c_INSUFFICIENT_RIGHTS  INT,
    @c_OBJECTNOTFOUND       INT,
    @c_NOOID                INT,
    @c_MAS_FILE             INT,
    @c_MAS_HYPERLINK        INT,

    -- local variables:
    @l_retValue             INT,
    @l_oid                  OBJECTID

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_NOOID                = 0x0000000000000000,
    @c_MAS_FILE             = 1,
    @c_MAS_HYPERLINK        = 2

    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID

/* no transactions allowed because already defined in p_Attachment_01$create
    BEGIN TRANSACTION
*/

        EXEC @l_retValue = p_Attachment_01$create @ai_userId, @ai_op, @ai_tVersionId, @ai_name,
                @ai_containerId_s, @ai_containerKind, @ai_isLink, @ai_linkedObjectId_s, @ai_description,
                @ao_oid_s OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)    -- no error occurred?
        BEGIN
            EXEC p_stringToByte @ao_oid_s, @l_oid OUTPUT
            -- additional attributes for db-mapping
 	        INSERT INTO ibs_DocumentTemplate_01
 	                (oid, objectType, typeName, className, iconName, mayExistIn,
 	                mayContain, isContainerType, objectSuperType,
                    isSearchable, isInheritable, showInMenu, showInNews,
 	                typeId, tVersionId, systemDisplayMode, dbMapped,
 	                tableName, procCopy, mappingInfo, workflowTemplateOid,
 	                attachmentCopy, logDirectory, showDOMTree)
	        VALUES  (@l_oid, N'', N'', N'', N'', N'',
	                N'', 0, N'',
	                0, 0, 0, 0,
	                0, 0, 0, 0,
	                N'', N'', N'', @c_NOOID,
	                N'', N'', 0)
        END  -- no error occurred?

/* no transactions allowed because already defined in p_Attachment_01$create
    COMMIT TRANSACTION
*/

    -- return the state value
    RETURN  @l_retValue
GO
-- p_DocumentTemplate_01$create



/******************************************************************************
 * Registers a tab for a document template.<BR>
 *
 * @input parameters:
 * @param   ai_oid              The oid of the document template.
 * @param   ai_typeCode         The type code of the document template.
 * @param   ai_tabPos           The position number (id) of the tab.
 *
 * @return  A value representing the state of the procedure.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_DocumentTemplate_01$regTab'
GO

-- create the new procedure:
CREATE PROCEDURE p_DocumentTemplate_01$regTab
(
    @ai_oid                 OBJECTID,
    @ai_typeCode            NAME,
    @ai_tabPos              INT
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOT_OK               INT,            -- error

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INTEGER,        -- counter
    @l_id                   INT,            -- the position id of the tab
    @l_kind                 INT,            -- the kind of the tab
    @l_tVersionId           TVERSIONID,     -- the tVersionId of the tab object
    @l_fct                  INT,            -- the function of the tab
    @l_priority             INT,            -- the priority of the tab
    @l_name                 NAME,           -- the name of the tab
    @l_desc                 DESCRIPTION,    -- the description of the tab
    @l_tabCode              NAME,           -- the tab code in the ibs_tab
    @l_class                DESCRIPTION,    -- the class to show tab view
    @l_multilangKey         NAME,           -- the multilang key in the ibs_tab
    @l_domainId             INT,            -- id of the current domain
    @l_retValue             INT,
    @l_msg                  DESCRIPTION,
    @l_tabId                ID

    -- initialization:
SELECT
    @c_NOT_OK           = 0,
    @c_ALL_RIGHT        = 1,
    @l_id               = 0,
    @l_kind             = 0,
    @l_tVersionId       = 0,
    @l_fct              = 0,
    @l_priority         = 0,
    @l_name             = N'',
    @l_desc             = N'',
    @l_tabCode          = N'',
    @l_class            = N'',
    @l_retValue         = @c_NOT_OK,
    @l_msg              = N''

-- body:

    -- ATTENTION!!
    -- !! NOT YET SUPPORTED BY THE BASE !!!
    -- The domain id must be set to the domain id of the document template
    -- object.

    -- SELECT @l_domainId = u.domainId
    -- FROM ibs_Object o, ibs_User u
    -- WHERE o.oid = @ai_oid AND u.id = o.owner
    SELECT @l_domainId = 0


    -- select the tab with the given id and document template oid
    SELECT  @l_id = id,
            @l_kind = kind,
            @l_tVersionId = tVersionId,
            @l_fct = fct,
            @l_priority = priority,
            @l_name = name,
            @l_desc = description,
            @l_tabCode = tabCode,
            @l_class = class
    FROM    ibs_TabTemplate_01
    WHERE   oid = @ai_oid
        AND id = @ai_tabPos

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'select', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    IF (@l_count = 1)                   -- the tab template was found?
    BEGIN
        -- remove all leading/trailing spaces from the tab name:
        SELECT  @l_name = LTRIM (RTRIM (@l_name))
        -- if no tab name is given set the type name from the ibs_Type table:
        IF (@l_name IS NULL OR @l_name = '') -- no tab name defined?
        BEGIN
            SELECT  @l_name = name
            FROM    ibs_Type t, ibs_TVersion tv
            WHERE   t.id = tv.typeId
                AND tv.id = @l_tVersionId
        END -- if no tab name defined

        -- if no tabCode is given set tabcode to oid concatenated with typeCode:
        IF (@l_tabCode IS NULL OR @l_tabCode = '') -- no code defined?
        BEGIN
            -- the unique tab code is composed by the oid of the template,
            -- the type code of the form and the id of the tab.
            EXEC    p_byteToString @ai_oid, @l_tabCode OUTPUT
            SELECT  @l_tabCode = @l_tabCode +
                    '_' + SUBSTRING (@ai_typeCode, 1, 40) +
                    '_' + CONVERT (VARCHAR (10), @l_id)
        END -- if no code defined

        -- use the tab code as multilang key:
        SELECT @l_multilangKey = @l_tabCode

        -- check if the tab already exists
        IF NOT EXISTS ( SELECT  id
                        FROM    ibs_Tab
                        WHERE   code = @l_tabCode
                            AND domainId = @l_domainId)
        BEGIN                           -- tab does not exist?
            -- register the new tab:
            EXEC p_Tab$new @l_domainId, @l_tabCode, @l_kind, @l_tVersionId,
                           @l_fct, @l_priority, @l_multilangKey, 0, @l_class,
                           @l_tabId OUTPUT
        END -- if tab does not exist

        -- try to update the tab name and description
        UPDATE  ibs_ObjectDesc_01
        SET     objName = @l_name,
                objDesc = @l_desc,
                className = N'ibs.bo.ObjectDesc'
        WHERE   languageId = 0
            AND name = @l_multilangKey;

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            N'update', @l_ePos OUTPUT, @l_count OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        IF (@l_count = 0)               -- the objectdesc entry was not found?
        BEGIN
            -- set the tab name and description for the new tab type:
            INSERT INTO ibs_ObjectDesc_01
                    (id, languageId, name, objName, objDesc,
                    className)
            VALUES  (0, 0, @l_multilangKey, @l_name, @l_desc,
                    N'ibs.bo.ObjectDesc')

            EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                N'insert', @l_ePos OUTPUT, @l_count OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler
        END -- if the objectdesc entry was not found

        -- add the tab to the consistsof table:
        EXEC p_Type$addTabs @ai_typeCode, '', @l_tabCode
        -- set the return code:
        SELECT @l_retValue = @c_ALL_RIGHT
    END -- if the tab template was found

    -- return the state value:
    RETURN @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_DocumentTemplate_01$regTab', @l_error, @l_ePos,
            N'ai_tabPos', @ai_tabPos,
            N'ai_typeCode', @ai_typeCode,
            N'l_id', @l_id,
            N'l_name', @l_name,
            N'l_kind', @l_kind,
            N'l_desc', @l_desc,
            N'l_tVersionId', @l_tVersionId,
            N'l_tabCode', @l_tabCode,
            N'l_fct', @l_fct,
            N'l_class', @l_class,
            N'l_priority', @l_priority
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_DocumentTemplate_01$regTab


/******************************************************************************
 * Inserts/Updates the type information for the document type
 * in the ibs_Type table.<BR>
 *
 * @input parameters:
 * @param   ai_oid              The oid of the template.
 * @param   ai_typeId           The type id (0 for a new type).
 * @param   ai_typeCode         The type code.
 * @param   ai_typeName         The type name.
 * @param   ai_className        The java class name.
 * @param   ai_iconName         The icon name.
 * @param   ai_mayExistIn       List of container type codes where objects of
 *                              this type are allowed to exist in.
 * @param   ai_isContainer      true if the type is a container.
 * @param   ai_mayContain       List of object type codes whitch this container
 *                              is allowed to contain.
 *
 * @output parameters:
 * @param   ao_typeId           The resulting type id.
 * @param   ao_tVersionId       The resulting tVersion id.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_DocumentTemplate_01$newType'
GO

-- create the new procedure:
CREATE PROCEDURE p_DocumentTemplate_01$newType
(
    -- input parameters:
    @ai_oid                 OBJECTID,
    @ai_typeId              TYPEID,
    @ai_typeCode            NAME,
    @ai_typeName            NAME,
    @ai_className           NAME,
    @ai_iconName            NAME,
    @ai_mayExistIn          NVARCHAR (2000),
    @ai_isContainerType     BOOL,
    @ai_mayContain          NVARCHAR (255),
    @ai_superTypeCode       NAME,
    @ai_isSearchable        BOOL,
    @ai_isInheritable       BOOL,
    @ai_isShowInMenu        BOOL,
    @ai_isShowInNews        BOOL,

    -- output parameters:
    @ao_typeId              TYPEID OUTPUT,
    @ao_tVersionId          TVERSIONID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_languageId           INT,

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INT,            -- counter
    @l_code                 NAME,
    @l_idx                  INT,
    @l_len                  INT,
    @l_tabId                INT,
    @l_oldIcon              NAME,
    @l_oldShowInMenu        BOOL

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_languageId           = 0         -- the current language

    -- initialize local variables:
SELECT
    @l_retValue             = @c_NOT_OK,
    @l_error                = 0,
    @l_count                = 0

-- body:
    BEGIN TRANSACTION

        -- if the typeId is 0 register the new type
        IF (@ai_typeId = 0)            -- register new type?
        BEGIN
            -- check if a type with this code name already exists:
            -- if the type not exists register the type
            IF NOT EXISTS ( SELECT  id
                            FROM    ibs_Type
                            WHERE   code = @ai_typeCode)
                                        -- type does not exist?
            BEGIN
                -- drop surrounding spaces:
                SELECT  @ai_superTypeCode = LTRIM (RTRIM (@ai_superTypeCode))

                -- determinate the type id of the super type:
                -- if no super type code is set use the default type
                IF (@ai_superTypeCode IS NULL OR @ai_superTypeCode = '')
                BEGIN
                    -- set the default type code of the super type:
                    IF (@ai_isContainerType > 0)
                        SELECT @ai_superTypeCode = N'Container' -- Container
                    ELSE
                        SELECT @ai_superTypeCode = N'XMLViewer' -- XMLViewer
                END -- if

                -- register the new type:
                EXEC p_Type$newLang @ai_typeId, @ai_superTypeCode,
                        @ai_isContainerType, @ai_isInheritable,
                        @ai_isSearchable, @ai_isShowInMenu, @ai_isShowInNews,
                        @ai_typeCode, @ai_className, @c_languageId, @ai_typeName
            END -- if type does not exist
        END -- if register new type

        -- get the typeId and tVersionId of the type:
        SELECT  @ao_typeId = id, @ao_tVersionId = actVersion
        FROM    ibs_Type
        WHERE   code = @ai_typeCode

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            N'type not created', @l_ePos OUTPUT, @l_count OUTPUT
        IF (@l_error <> 0 OR @l_count <> 1) -- an error occurred?
            GOTO exception              -- call common exception handler

        -- Update classname in ibs_tversion
        UPDATE  ibs_tVersion
        SET     classname = @ai_className
        WHERE   id = @ao_tVersionId

        -- get the old settings from ibs_Type: icon, showInMenu
        -- for later check if data needs to be changed in ibs_oOject
        SELECT  @l_oldIcon = icon,
                @l_oldShowInMenu = showInMenu
        FROM    ibs_Type
        WHERE   id = @ao_typeId

        -- update settings in ibs_Type:
        UPDATE  ibs_Type
        SET     isSearchable = @ai_isSearchable,
                showInMenu = @ai_isShowInMenu,
                showInNews = @ai_isShowInNews,
                icon = @ai_iconName
        WHERE   id = @ao_typeId

        -- check if ibs_Object needs to be changed:
        IF ((@ai_isShowInMenu != @l_oldShowInMenu) OR (@l_oldIcon != @ai_iconName))
        BEGIN
            UPDATE  ibs_Object
            SET     showInMenu = @ai_isShowInMenu,
                    icon = @ai_iconName
            WHERE   tVersionId = @ao_tVersionId
        END -- if

        -- add the default tabs:
        -- if the new type is a container add the content tab, too
        IF (@ai_isContainerType > 0)
            EXEC p_Type$addTabs @ai_typeCode, N'',
                N'Content', N'Info', N'References', N'Rights'
        ELSE
            EXEC p_Type$addTabs @ai_typeCode, N'',
                N'Info', N'References', N'Rights'


        -- add the additional tabs:
        -- define cursor: get all tabs from the ibs_TabTemplate_01 table
        DECLARE tab_Cursor CURSOR FOR
        SELECT  id
        FROM    ibs_TabTemplate_01
        WHERE   oid = @ai_oid
        ORDER BY id
        -- open the cursor:
        OPEN tab_Cursor
        -- get the first tab:
        FETCH NEXT FROM tab_Cursor INTO @l_tabId
        -- loop through all found tabs:
        WHILE (@@FETCH_STATUS <> -1)    -- another tab found?
        BEGIN
            -- register the tab:
            EXEC @l_retValue =
                p_DocumentTemplate_01$regTab @ai_oid, @ai_typeCode, @l_tabId
            -- get next tab:
            FETCH NEXT FROM tab_Cursor INTO @l_tabId
        END -- while another tab found
        -- deallocate cursor:
        DEALLOCATE tab_Cursor


        -- set the correct type and icon name in ibs_Type:
        UPDATE ibs_Type
        SET    name = @ai_typeName,
               icon = @ai_iconName
        WHERE  code = @ai_typeCode

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'setting the type name and icon', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- delete existing may contain entrys for the type
        -- EXEC p_MayContain$deleteType @ao_typeId

        -- set the may contain entrys for the new type
        -- the parameter '@ai_mayExistIn' holds a comma separated list
        -- of the container type codes.

        -- if the container code list is empty set the default container codes
        -- like the XMLViewer_01.

        -- remove all leading and trailing spaces:
        SELECT  @ai_mayExistIn = LTRIM (RTRIM (@ai_mayExistIn))

        -- if not defined set the default type codes for form objects:
        IF (@ai_mayExistIn = N'')
            SELECT @ai_mayExistIn = N'Container,ExportContainer,MasterDataContainer'

        -- ensure that there are no leading spaces:
        SELECT @ai_mayExistIn = LTRIM (@ai_mayExistIn)

        WHILE (@ai_mayExistIn > N'')
        BEGIN
            -- separate the container type codes:
            SELECT @l_code = N''
            SELECT @l_len = LEN (@ai_mayExistIn)
            SELECT @l_idx = CHARINDEX (N',', @ai_mayExistIn)

            -- check if the comma (',') was found:
            IF (@l_idx > 0)             -- found the comma?
            BEGIN
                -- get the next code:
                SELECT @l_code =
                    RTRIM (SUBSTRING (@ai_mayExistIn, 1, @l_idx - 1))
                -- get the rest of the string:
                SELECT @ai_mayExistIn =
                    LTRIM (SUBSTRING (@ai_mayExistIn, @l_idx + 1, @l_len - @l_idx))
            END -- if found the comma
            ELSE                        -- did not find comma
            BEGIN
                -- get the rest of the string as code:
                SELECT @l_code = RTRIM (@ai_mayExistIn)
                -- no more entries:
                SELECT @ai_mayExistIn = N''
            END -- else did not find comma

            -- check if the code could be found:
            IF (@l_code > N'')           -- code not empty?
            BEGIN
                -- add the maycontain entry:
                EXEC @l_retValue = p_MayContain$new @l_code, @ai_typeCode
            END -- if code not empty
        END -- while

        -- if the new type is a container set the correct maycontain entries:
        IF (@ai_isContainerType > 0)    -- the type is a container?
        BEGIN
            -- the parameter 'ai_mayContain' holds a comma separated list
            -- of the type codes.
            -- ensure that there are no leading spaces:
            SELECT @ai_mayContain = LTRIM (@ai_mayContain)

            WHILE (@ai_mayContain > N'')
            BEGIN
                -- separate the object type codes:
                SELECT @l_code = ''
                SELECT @l_len = LEN (@ai_mayContain)
                SELECT @l_idx = CHARINDEX (N',', @ai_mayContain)

                -- check if the comma (',') was found:
                IF (@l_idx > 0)         -- found the comma?
                BEGIN
                    -- get the next code:
                    SELECT @l_code =
                        RTRIM (SUBSTRING (@ai_mayContain, 1, @l_idx - 1))
                    -- get the rest of the string:
                    SELECT @ai_mayContain =
                        LTRIM (SUBSTRING (@ai_mayContain, @l_idx + 1, @l_len - @l_idx))
                END -- if found the comma
                ELSE                    -- did not find comma
                BEGIN
                    -- get the rest of the string as code:
                    SELECT @l_code = @ai_mayContain
                    -- no more entries:
                    SELECT @ai_mayContain = N''
                END -- else did not find comma

                -- check if the code could be found:
                IF (@l_code > N'')       -- code not empty?
                BEGIN
                    -- add the maycontain entry:
                    EXEC @l_retValue = p_MayContain$new @ai_typeCode, @l_code
                END -- if code not empty
            END -- while
        END -- if the type is a container

        -- set the result value:
        SELECT @l_retValue = @c_ALL_RIGHT

    COMMIT TRANSACTION              -- make changes permanent

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_DocumentTemplate_01$newType', @l_error, @l_ePos,
            N'ai_typeId', @ai_typeId,
            N'ai_typeCode', @ai_typeCode,
            N'ai_isContainerType', @ai_isContainerType,
            N'ai_typeName', @ai_typeName,
            N'ai_isSearchable', @ai_isSearchable,
            N'ai_className', @ai_className,
            N'ai_isInheritable', @ai_isInheritable,
            N'ai_iconName', @ai_iconName,
            N'ai_isShowInMenu', @ai_isShowInMenu,
            N'ai_mayExistIn', @ai_mayExistIn,
            N'ai_isShowInNews', @ai_isShowInNews,
            N'ai_mayContain', @ai_mayContain,
            N'l_tabId', @l_tabId,
            N'l_code', @l_code,
            N'ao_typeId', @ao_typeId,
            N'', N'',
            N'ao_tVersionId', @ao_tVersionId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_DocumentTemplate_01$newType


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
 * @param   @showInNews         Display object in the news.
 *
 * ...
 *
 * @param   @objectType         the object type code
 * @param   @typeName           the object type name
 * @param   @className          the java class name
 * @param   @iconName           the icon name
 * @param   @mayExistIn         list of container type codes
 * @param   @isContainerType    defines the template a container type
 * @param   @mayContain         list of type codes that can appare in this container
 * @param   @superTypeCode      the type code of the super type
 * @param   @isSearchable       is the type searchable
 * @param   @isInheritable      is the type inheritable
 * @param   @isShowInMenu       the 'show in menu' flag for the type
 * @param   @isShowInNews       the 'show in news' flag for the type
 * @param   @systemDisplayMode  display mode for the system section of the object
 * @param   @dbMapped           objects of this type are db-mapped
 * @param   @tableName          the mapping table
 * @param   @procCopy           the name of the copy procedure
 * @param   @wfTemplateOid_s    the workflow template oid
 * @param   @attachmentCopy     the name of the copy procedure for attachments (XMLDATA)
 * @param   @showDOMTree        flag to show the DOM tree to the user
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_DocumentTemplate_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_DocumentTemplate_01$change
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
    @ai_filename	        NVARCHAR (255),
    @ai_path	            NVARCHAR (255),
    @ai_url 	            NVARCHAR (255),
    @ai_filesize	        REAL,
    @ai_isWeblink           BOOL,

    @ai_objectType          NAME,
    @ai_typeName            NAME,
    @ai_className           NAME,
    @ai_iconName            NAME,
    @ai_mayExistIn          NVARCHAR (255),
    @ai_isContainerType     BOOL,
    @ai_mayContain          NVARCHAR (255),
    @ai_superTypeCode       NAME,
    @ai_isSearchable        BOOL,
    @ai_isInheritable       BOOL,
    @ai_isShowInMenu        BOOL,
    @ai_isShowInNews        BOOL,
    @ai_systemDisplayMode   INT,
    @ai_dbMapped            BOOL,
    @ai_tableName           NVARCHAR (30),
    @ai_procCopy            NVARCHAR (30),
    @ai_wfTemplateOid_s     OBJECTIDSTRING,
    @ai_attachmentCopy      NVARCHAR (30),
    @ai_showDOMTree         BOOL
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this procedure
    @l_oid                  OBJECTID,       -- converted input parameter
    @l_wfTemplateOid        OBJECTID,       -- converted input parameter
    @l_typeID               INT,
    @l_tVersionID           INT

    -- assign constants:
SELECT
    @c_ALL_RIGHT = 1

    -- initialize local variables and return values:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_oid      = 0x0000000000000000

-- body:
/* no transactions allowed because already defined in p_Attachment_01$change
    BEGIN TRANSACTION
*/
        -- perform the change of the object:
        EXEC @l_retValue = p_Attachment_01$change @ai_oid_s, @ai_userId, @ai_op,
                @ai_name, @ai_validUntil, @ai_description, @ai_showInNews,
                @ai_isMaster, @ai_attachmentType, @ai_filename, @ai_path,
                @ai_url, @ai_filesize, @ai_isWeblink


        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed?
        BEGIN
            EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT
            EXEC p_stringToByte @ai_wfTemplateOid_s, @l_wfTemplateOid OUTPUT

            -- for a new type the typeId is 0 and we have
            -- to register the new type to get a new typeId.
            -- if the type is not new (typeId != 0) the type
            -- informations are updated
            SELECT @l_typeID = typeID, @l_tVersionID = tVersionId
            FROM ibs_DocumentTemplate_01
            WHERE oid = @l_oid

            -- register the new type with the given type code and class name
            EXEC @l_retValue = p_DocumentTemplate_01$newType
                                    @l_oid, @l_typeID, @ai_objectType,
                                    @ai_typeName, @ai_className,
                                    @ai_iconName, @ai_mayExistIn,
                                    @ai_isContainerType, @ai_mayContain,
                                    @ai_superTypeCode,
                                    @ai_isSearchable,
                                    @ai_isInheritable,
                                    @ai_isShowInMenu,
                                    @ai_isShowInNews,
                                    @l_typeId OUTPUT,
                                    @l_tVersionId OUTPUT

            IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed?
            BEGIN
                -- update further information:
                UPDATE  ibs_DocumentTemplate_01
                SET     objectType = @ai_objectType,
                        typeId = @l_typeId,
                        tVersionId = @l_tVersionId,
                        typeName = @ai_typeName,
                        className = @ai_className,
                        iconName = @ai_iconName,
                        mayExistIn = @ai_mayExistIn,
                        isContainerType = @ai_isContainerType,
                        mayContain = @ai_mayContain,
                        objectSuperType = @ai_superTypeCode,
                        isSearchable = @ai_isSearchable,
                        isInheritable = @ai_isInheritable,
                        showInMenu = @ai_isShowInMenu,
                        showInNews = @ai_isShowInNews,
                        systemDisplayMode = @ai_systemDisplayMode,
                        dbMapped = @ai_dbMapped,
                        tableName = @ai_tableName,
                        procCopy = @ai_procCopy,
                        workflowTemplateOid = @l_wfTemplateOid,
                        attachmentCopy = @ai_attachmentCopy,
                        logDirectory = @ai_objectType,
                        showDOMTree = @ai_showDOMTree
                WHERE   oid = @l_oid
            END -- if retValue is ok
        END -- if retValue is ok

/* no transactions allowed because already defined in p_Attachment_01$change
    COMMIT TRANSACTION
*/

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_DocumentTemplate_01$change


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
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Display the object in the news.
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out.
 * @param   ao_checkOutUser     Id of the user which checked out the object.
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user.
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut user.
 *
 * ...                          attachment parameters
 *
 * @param   ao_objectType       The object type name.
 * @param   ao_typeName         The object type name.
 * @param   ao_className        The java class name.
 * @param   ao_iconName         The icon name.
 * @param   ao_mayExistIn       The info for may contain.
 * @param   ao_isContainerType  true if the type is a container.
 * @param   ao_mayContain       The info for may contain.
 * @param   ao_superTypeCode    The type code of the super type.
 * @param   ao_isSearchable     Is the type searchable.
 * @param   ao_isInheritable    Is the type inheritable.
 * @param   ao_isShowInMenu     The 'show in menu' flag for the type.
 * @param   ao_isShowInNews     The 'show in news' flag for the type.
 * @param   ao_typeId           The type id.
 * @param   ao_tVersionId       The tVersion id.
 * @param   ao_systemDisplayMode Display mode for the system section of the
 *                              object.
 * @param   ao_dbMapped         Object of this type are db-mapped.
 * @param   ao_tableName        The mapping table name.
 * @param   ao_procCopy         The copy procedure for the mapping.
 * @param   ao_wfTemplateOid    The oid of the workflow template.
 * @param   ao_wfTemplateName   The name of the workflow template.
 * @param   ao_attachmentCopy   The copy procedure for attachments (XMLDATA).
 * @param   ao_logDirectory     The name of the directory where the log of
 *                              translation should be stored.
 * @param   ao_showDOMTree      Flag to show the DOM tree to the user.
 *
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the
 *                          database.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_DocumentTemplate_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_DocumentTemplate_01$retrieve
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

    @ao_isMaster            BOOL OUTPUT,
    @ao_attachmentType      INT OUTPUT,
    @ao_filename            NVARCHAR(255) OUTPUT,
    @ao_path                NVARCHAR(255) OUTPUT,
    @ao_url                 NVARCHAR(255) OUTPUT,
    @ao_filesize            REAL OUTPUT,
    @ao_isWeblink           BOOL OUTPUT,

    @ao_objectType          NAME OUTPUT,
    @ao_objectTypeName      NAME OUTPUT,
    @ao_className           NAME OUTPUT,
    @ao_iconName            NAME OUTPUT,
    @ao_mayExistIn          NVARCHAR (255) OUTPUT,
    @ao_isContainerType     BOOL OUTPUT,
    @ao_mayContain          NVARCHAR (255) OUTPUT,
    @ao_superTypeCode       NAME OUTPUT,
    @ao_isSearchable        BOOL OUTPUT,
    @ao_isInheritable       BOOL OUTPUT,
    @ao_isShowInMenu        BOOL OUTPUT,
    @ao_isShowInNews        BOOL OUTPUT,
    @ao_objectTypeId        TYPEID OUTPUT,
    @ao_objectTVersionId    TVERSIONID OUTPUT,
    @ao_systemDisplayMode   INT OUTPUT,
    @ao_dbMapped            BOOL OUTPUT,
    @ao_tableName           NVARCHAR (30) OUTPUT,
    @ao_procCopy            NVARCHAR (30) OUTPUT,
    @ao_wfTemplateOid       OBJECTID OUTPUT,
    @ao_wfTemplateName      NAME OUTPUT,
    @ao_attachmentCopy      NVARCHAR (30) OUTPUT,
    @ao_logDirectory        FILENAME OUTPUT,
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
/* no transactions allowed because already defined in p_Attachment_01$retrieve
    BEGIN TRANSACTION
*/
        -- retrieve the base object data:
        EXEC @l_retValue = p_Attachment_01$retrieve
                @ai_oid_s, @ai_userId, @ai_op,
                @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT,
                @ao_name OUTPUT, @ao_containerId OUTPUT,
                @ao_containerName OUTPUT, @ao_containerKind OUTPUT,
                @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT,
                @ao_owner OUTPUT, @ao_ownerName OUTPUT,
                @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
                @ao_validUntil OUTPUT, @ao_description OUTPUT, @ao_showInNews OUTPUT,
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT,
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT,
                @ao_checkOutUserName OUTPUT, @ao_isMaster OUTPUT,
                @ao_attachmentType OUTPUT, @ao_filename OUTPUT,
                @ao_path OUTPUT, @ao_url  OUTPUT, @ao_filesize OUTPUT,
                @ao_isWeblink OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)
        BEGIN
            EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT
            SELECT
                @ao_objectType = objectType,
                @ao_objectTypeName = typeName,
                @ao_className = className,
                @ao_iconName = iconName,
                @ao_mayExistIn = mayExistIn,
                @ao_isContainerType = isContainerType,
                @ao_mayContain = mayContain,
                @ao_superTypeCode = objectSuperType,
                @ao_isSearchable = isSearchable,
                @ao_isInheritable = isInheritable,
                @ao_isShowInMenu = showInMenu,
                @ao_isShowInNews = showInNews,
                @ao_objectTypeId = typeId,
                @ao_objectTVersionId = tVersionId,
                @ao_systemDisplayMode = systemDisplayMode,
                @ao_dbMapped = dbMapped,
                @ao_tableName = tableName,
                @ao_procCopy = procCopy,
                @ao_wfTemplateOid = workflowTemplateOid,
                @ao_attachmentCopy = attachmentCopy,
                @ao_logDirectory = logDirectory,
                @ao_showDOMTree = showDOMTree
	        FROM ibs_DocumentTemplate_01
	        WHERE oid = @l_oid

            -- if a workflow is defined get the name of the workflow object
	        IF (@ao_wfTemplateOid != @c_NOOID)
	        BEGIN
	            SELECT @ao_wfTemplateName = name
	            FROM ibs_Object
	            WHERE oid = @ao_wfTemplateOid
	        END
        END

/* no transactions allowed because already defined in p_Attachment_01$retrieve
    COMMIT TRANSACTION
*/

    -- return the state value
    RETURN  @l_retValue
GO
-- p_DocumentTemplate_01$retrieve



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
EXEC p_dropProc N'p_DocumentTemplate_01$delete'
GO

CREATE PROCEDURE p_DocumentTemplate_01$delete
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
    DECLARE
        @ALL_RIGHT              INT,
        @INSUFFICIENT_RIGHTS    INT,
        @OBJECTNOTFOUND         INT,
        @RIGHT_DELETE           INT,
        @OBJECTISREFERENCED     INT

    -- set constants
    SELECT
        @ALL_RIGHT              = 1,
        @INSUFFICIENT_RIGHTS    = 2,        -- return values
        @OBJECTNOTFOUND         = 3,
        @RIGHT_DELETE           = 16,       -- access rights
        @OBJECTISREFERENCED     = 61        -- object is referenced by another

    -- define local variables
    DECLARE
        @retValue               INT,        -- return value of this procedure
        @rights                 INT,        -- return value of called proc.
        @containerId            OBJECTID,
        @l_typeId               INT,
        @l_tVersionId           INT,
        @l_domainId             INT,
        @l_typeCode             NAME,
        @l_tabCode              NAME

    -- initialize local variables
    SELECT
        @retValue               = @ALL_RIGHT,
        @rights                 = 0,
        @containerId            = 0,
        @l_typeId               = 0,
        @l_tVersionId           = 0,
        @l_domainId             = 0,
        @l_typeCode             = N'',
        @l_tabCode              = N''

    ---------------------------------------------------------------------------
    -- START
/* no transactions allowed because already defined in p_Attachment_01$delete
    BEGIN TRANSACTION
*/

        SELECT @retValue = @OBJECTISREFERENCED

        -- check if the document template is referenced by other objects
        SELECT oid
        FROM v_DocumentTemplate_01$ref
        WHERE oid = @oid

        -- no references found ?
        IF (@@ROWCOUNT = 0)
        BEGIN
            -- all references and the object itself are deleted (plus rights)
            EXEC @retValue = p_Attachment_01$delete @oid_s, @userId, @op

            -- drop all type informations for this template
            IF (@retValue = @ALL_RIGHT)
            BEGIN
                BEGIN TRANSACTION

                -- first get the tVersionId of the type defined by
                -- the document template
                SELECT
                    @l_tVersionId = tVersionId,
                    @l_typeId = typeId,
                    @l_typeCode = objectType
                FROM ibs_DocumentTemplate_01
                WHERE oid = @oid

                -- if the template defines a valid m2 type
                IF (@l_tVersionId != 0 AND @l_typeId != 0)
                BEGIN
                    -- ATTENTION!!
                    -- !! NOT YET SUPPORTED BY THE BASE !!!
                    -- The domain id must be set to the domain id of the document template object.

                    SELECT @l_domainId = 0

                    -- SELECT @l_domainId = u.domainId
                    -- FROM ibs_Object o, ibs_User u
                    -- WHERE o.oid = @oid AND u.id = o.owner


                    -- delete all rows in the ibs_Attachments_01 table who are part
                    -- of a xml viewer object with this template oid
                    DELETE ibs_Attachment_01
                    WHERE oid IN
                            (
                                SELECT  oid
                                FROM    ibs_XMLViewer_01
                                WHERE   templateOid = @oid
                            )

                    -- delete all rows in the ibs_KeyMapper table who are referenced
                    -- by a xml viewer object with this template oid
                    DELETE ibs_KeyMapper
                    WHERE oid IN
                            (
                                SELECT  oid
                                FROM    ibs_XMLViewer_01
                                WHERE   templateOid = @oid
                            )


                    -- delete all form objects (and tabs) with this tVersionId
                    DELETE  ibs_Object
                    WHERE oid IN
                            (
                                SELECT  o1.oid
                                FROM    ibs_Object o1, ibs_Object o2
                                WHERE   o1.posNoPath LIKE o2.posNoPath + '%'
                                    AND o2.tVersionId = @l_tVersionId
                            )

                    -- delete all rows in the XMLViewer_01 table with this template oid
                    DELETE ibs_XMLViewer_01
                    WHERE templateOid = @oid

                    -- delete the m2 type
                    EXEC @retValue = p_Type$deletePhysical @l_typeId, @l_typeCode

                    -- delete the tabs and object descriptions
                    -- ATTENTION!! this should be done by a specific delete procedure.
                    -- and with other tabcodes - because tabcodes are not
                    -- only generated, but set.
                    EXEC p_byteToString @oid, @l_tabCode OUTPUT
                    DELETE ibs_Tab
                    WHERE domainId = @l_domainId AND code LIKE @l_tabCode + N'_%'

                    -- delete consist of entries
                    DELETE ibs_ConsistsOf WHERE tVersionId = @l_tVersionId

                    -- delete the tab description too
                    -- ATTENTION!! this should be done by a specific delete procedure.
                    DELETE ibs_ObjectDesc_01
                    WHERE name LIKE @l_tabCode + N'_%'

                    -- delete all references from and to objects of this type:
                    EXEC    p_Reference$deleteTVersion @l_tVersionId
                END -- IF (@l_tVersionId != 0)

                -- delete the document template object and its tabs from ibs_object
                DELETE  ibs_Object
                WHERE   oid IN
                        (
                            SELECT  o1.oid
                            FROM    ibs_Object o1, ibs_Object o2
                            WHERE   o1.posNoPath LIKE o2.posNoPath + '%'
                                AND o2.oid = @oid
                        )

                -- delete the document template object itself
                DELETE ibs_DocumentTemplate_01
                WHERE oid = @oid

                -- delete the row in the ibs_Attachment_01 table
                DELETE ibs_Attachment_01
                WHERE oid = @oid

                -- delete the reference in the key mapper table
                DELETE ibs_KeyMapper
                WHERE oid = @oid

                -- delete the tab templates
                DELETE ibs_TabTemplate_01
                WHERE oid = @oid

                COMMIT TRANSACTION
            END -- IF (@retValue = @ALL_RIGHT)
        END -- IF (@@ROWCOUNT = 0)

/* no transactions allowed because already defined in p_Attachment_01$delete
    COMMIT TRANSACTION
*/
    -- return the state value
    RETURN  @retValue
GO
-- p_DocumentTemplate_01$delete


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
EXEC p_dropProc N'p_DocumentTemplate_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_DocumentTemplate_01$BOCopy
(
    -- input parameters:
    @ai_oid                 OBJECTID,       -- the oid of Object we want to copy
    @ai_userId              USERID,         -- the userId of the User who wants to copy
    @ai_newOid              OBJECTID        -- the new OID of the copied Object
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.

    -- local variables:
    @l_retValue             INT             -- return value of this procedure

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1

    BEGIN TRANSACTION

        EXEC @l_retValue = p_Attachment_01$BOCopy @ai_oid, @ai_userId, @ai_newOid

        IF (@l_retValue = @c_ALL_RIGHT)
        BEGIN
            -- make a insert for all your typespecific tables:
            INSERT  INTO ibs_DocumentTemplate_01
                    (oid, objectType, systemDisplayMode, dbMapped, tableName,
                    procCopy, mappingInfo, workflowTemplateOid)
            SELECT  @ai_newOid, objectType, systemDisplayMode, dbMapped, tableName,
                    procCopy, mappingInfo, workflowTemplateOid
            FROM    ibs_DocumentTemplate_01
            WHERE   oid = @ai_oid

            -- check if the insert has processed correctly:
            IF (@@ROWCOUNT >=1)
                SELECT @l_retvalue = @c_All_RIGHT
        END

    COMMIT TRANSACTION

    -- return the state value:
    RETURN @l_retValue
GO
-- p_DocumentTemplate_01$BOCopy


/******************************************************************************
 * Insert/Update the informations for a document template tab in the table
 * ibs_TabTemplate_01.<BR>
 *
 * @input parameters:
 * @param   ai_oid_s            Oid of the associated document template.
 * @param   ai_id               The number of the tab.
 * @param   ai_kind             The tab kind.
 * @param   ai_code             The type code of the object.
 * @param   ai_fct              The function of the tab.
 * @param   ai_name             The name of the tab.
 * @param   ai_desc             The description of the tab.
 * @param   ai_code             The code of the tab.
 * @param   ai_class            The class to show the tab view.
 *
 * @return  A value representing the state of the procedure.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_TabTemplate_01$addTab'
GO

-- create the new procedure:
CREATE PROCEDURE p_TabTemplate_01$addTab
(
    -- input parameters:
    @ai_oid_s           OBJECTIDSTRING,
    @ai_id              INT,
    @ai_kind            INT,
    @ai_tVersionId      TVERSIONID,
    @ai_fct             INT,
    @ai_priority        INT,
    @ai_name            NAME,
    @ai_desc            DESCRIPTION,
    @ai_code            NAME,
    @ai_class           DESCRIPTION
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT        INT,            -- everything was o.k.
    @c_NOT_OK           INT,            -- error

    -- local variables:
    @l_oid              OBJECTID,
    @l_code             NAME,
    @l_id               INT

    -- initialization:
SELECT
    @c_NOT_OK           = 0,
    @c_ALL_RIGHT        = 1

SELECT
    @l_code             = @ai_code

    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

-- body:
    -- drop leading and trailing spaces from the code:
    SELECT  @l_code = LTRIM (RTRIM (@ai_code))

    BEGIN TRANSACTION
        -- check if the tab already exists:
        IF EXISTS ( SELECT  oid
                    FROM    ibs_TabTemplate_01
                    WHERE   oid = @l_oid
                        AND id = @ai_id)
                                        -- the tab exists?
        BEGIN
            -- if the tab exists update the information

            -- get corresponding id from ibs_tab
            SELECT  @l_id = id
            FROM    ibs_Tab
            WHERE   code = @l_code

            -- update the tabTemplate
            UPDATE  ibs_TabTemplate_01
            SET     kind = @ai_kind,
                    tVersionId = @ai_tVersionId,
                    fct = @ai_fct,
                    priority = @ai_priority,
                    name = @ai_name,
                    description = @ai_desc,
                    tabCode = @l_code,
                    class = @ai_class
            WHERE   oid = @l_oid
                AND id = @ai_id

            -- update ibs_tab
            UPDATE  ibs_Tab
            SET     kind = @ai_kind,
                    tVersionId = @ai_tVersionId,
                    fct = @ai_fct,
                    priority = @ai_priority,
                    code = @l_code,
                    class = @ai_class
            WHERE   id = @l_id

            -- update priority in ibs_ConsistsOf
            UPDATE  ibs_ConsistsOf
            SET     priority = @ai_priority
            WHERE   tabId = @l_id

        END -- if the tab exists
        ELSE                            -- the tab does not exist
        BEGIN
            -- create the tab:
            INSERT INTO ibs_TabTemplate_01
                    (oid, id, kind, tVersionId, fct,
                    priority, name, description, tabCode, class)
            VALUES  (@l_oid, @ai_id, @ai_kind, @ai_tVersionId, @ai_fct,
                    @ai_priority, @ai_name, @ai_desc, @l_code, @ai_class)
        END -- else the tab does not exist

    COMMIT TRANSACTION

    RETURN @c_ALL_RIGHT
GO
-- p_TabTemplate_01$addTab


/******************************************************************************
 * Get the oid of a specific tab of an object. <BR>
 * If the tab does not exist for this object or the tab itself is not an object
 * there is no oid available an OBJECTNOTFOUND ist returned.
 *
 * @input parameters:
 * @param   ai_tabCode          The code of the tab (as it is in ibs_Tab).
 * @param   ai_oid_s            Id of the object for which to get the tab oid.
 *
 * @output parameters:
 * @param   ao_tabOid           The oid of the tab object.
 *
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_OBJECTNOTFOUND         The tab object was not found.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_DocumentTemplate_01$getTab'
GO

-- create the new procedure:
CREATE PROCEDURE p_DocumentTemplate_01$getTab
(
    -- input parameters:
    @ai_tabCode             NVARCHAR (255),
    @ai_oid_s               OBJECTIDSTRING,
    -- output parameters:
    @ao_tabOid              OBJECTID OUTPUT
)
AS
DECLARE
    -- local variables:
    @l_oid                  OBJECTID,
    @l_retValue             INT

    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

    EXEC @l_retValue = p_Object$getTabOid @l_oid, @ai_tabCode, @ao_tabOid OUTPUT

    RETURN  @l_retValue
GO
-- p_DocumentTemplate_01$getTab
