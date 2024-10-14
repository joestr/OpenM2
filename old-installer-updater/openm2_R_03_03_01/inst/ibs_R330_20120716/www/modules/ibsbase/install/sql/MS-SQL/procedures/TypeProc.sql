/******************************************************************************
 * All stored procedures regarding the type table. <BR>
 *
 * @version     2.2.1.0016, 19.03.2002 KR
 *
 * @author      Klaus Reimüller (KR)  980608
 ******************************************************************************
 */


/******************************************************************************
 * Create a new type. <BR>
 * This procedure also creates a first version of the type. <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_id               Id of the type to be created.
 *                              null => create new id.
 * @param   ai_name             Name of the type.
 * @param   ai_superTypeCode    Super type of the type. '' => default super type
 *                              If there exists a type called 'BusinessObject'
 *                              this type is generally the super type of all
 *                              other types.
 *                              If this type does not exist, but there exists
 *                              another type having no super type that type is
 *                              used as super type of the new type.
 * @param   ai_isContainer      Is the type a container. 1 => true
 * @param   ai_isInheritable    May the type be inherited, i.e. a sub type be
 *                              created?
 * @param   ai_isSearchable     Is it possible to search for object of the type?
 * @param   ai_showInMenu       May instances of this type be displayed in a
 *                              menu?
 * @param   ai_showInNews       May instances of this type be included in the
 *                              news?
 * @param   ai_code             Code of the type (unique name).
 * @param   ai_className        Name of class which is responsible for the
 *                              actual (= first, if type is new) version of the
 *                              type.
 *
 * @output parameters:
 * @param   ao_newId            New id = @id if @id <> null, a newly generated
 *                              id otherwise.
 * @param   ao_newActVersionId  Id of the actual version for this type.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 *                          (=> The data of the type is changed instead of newly
 *                          created.)
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Type$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_Type$new
(
    -- input parameters:
    @ai_id                  TYPEID  = 0x00000000,
    @ai_name                NAME,
    @ai_superTypeCode       NAME,
    @ai_isContainer         BOOL,
    @ai_isInheritable       BOOL,
    @ai_isSearchable        BOOL,
    @ai_showInMenu          BOOL,
    @ai_showInNews          BOOL,
    @ai_code                NAME,
    @ai_className           NAME,
    -- output parameters:
    @ao_newId               TYPEID  = 0x00000000 OUTPUT,
    @ao_newActVersionId     TVERSIONID = 0x00000000 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_ST_ACTIVE            INT,            -- active state
    @c_EMPTYPOSNOPATH       POSNOPATH,      -- default/invalid posNoPath
    @c_noTypeId             TYPEID,         -- id of no type
    @c_CommonSuperType      NAME,           -- name of common super type

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_id                   TYPEID,         -- the id of the type
    @l_superTypeId          TYPEID,         -- id of super type of the
                                            -- actual type
    @l_count                INTEGER         -- counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21,
    @c_ST_ACTIVE            = 2,
    @c_EMPTYPOSNOPATH       = 0x00,
    @c_noTypeId             = 0x00000000,
    @c_CommonSuperType      = N'BusinessObject'

    -- initialize local variables:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_error                = 0,
    @l_id                   = @ai_id,
    @l_count                = 0

-- body:
    -- initialize return values:
    SELECT  @ao_newId = 0

    BEGIN TRANSACTION -- begin new TRANSACTION

        -- check if a type with this id already exists:
        SELECT  @l_id = id, @ao_newActVersionId = actVersion
        FROM    ibs_Type
        WHERE   (   @ai_id <> @c_noTypeId   -- id set
                AND id = @ai_id
                )
                OR
                (   @ai_id = @c_noTypeId    -- no id set => use code
                AND code = @ai_code
                )

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            N'select', @l_ePos OUTPUT, @l_count OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        IF (@l_count > 0)               -- a type with this id already exists?
        BEGIN
            -- update typecode, showInMenu and IsInheritable which is set in
            -- createBaseObjectTypes
            UPDATE  ibs_Type
            SET     isInheritable = @ai_isInheritable,
                    isSearchable = @ai_isSearchable,
                    showInMenu = @ai_showInMenu,
                    showInNews = @ai_showInNews,
                    name = @ai_name
            WHERE   id = @l_id

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'update type', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

            -- update data within ibs_TVersion:
            UPDATE  ibs_TVersion
            SET     className = @ai_className
            WHERE   id IN
                    (
                        SELECT  actVersion
                        FROM    ibs_Type
                        WHERE   id = @l_id
                    )

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'update tversion', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

            -- set return value:
            SELECT  @l_retValue = @c_ALREADY_EXISTS
        END -- if a type with this id already exists
        ELSE                            -- type id not already there
        BEGIN
            -- get the super type id:
            SELECT  @l_superTypeId = id
            FROM    ibs_Type
            WHERE   code = @ai_superTypeCode

            -- set super type id:
            IF (@@ROWCOUNT <= 0)        -- no correct super type id?
            BEGIN
                -- get default super type:
                SELECT  @l_superTypeId = id
                FROM    ibs_Type
                WHERE   code = @c_CommonSuperType

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                    N'select default super type',
                    @l_ePos OUTPUT, @l_count OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call common exception handler

                IF (@l_count = 0)       -- type not found?
                BEGIN
                    -- check if there exists at least one type and get the
                    -- minimum id of all types having no super types:
                    SELECT  @l_superTypeId = COALESCE (MIN (id), @c_NoTypeId)
                    FROM    ibs_Type
                    WHERE   superTypeId = @c_noTypeId

                    -- check if there occurred an error:
                    EXEC @l_error = ibs_error.prepareErrorCount @@error,
                        @@ROWCOUNT, N'select minimum super type',
                        @l_ePos OUTPUT, @l_count OUTPUT
                    IF (@l_error <> 0)  -- an error occurred?
                        GOTO exception  -- call common exception handler

                    -- check if there was at least one type found:
                    IF (@l_count = 0)   -- no type was found?
                        -- actual type has no super type:
                        SELECT @l_superTypeId = @c_noTypeId
                END -- if type not found
            END -- if no correct super type id

            -- add the new type:
            -- (the id of the type itself is set as actual tVersionId until
            -- a version of the exists)
            INSERT INTO ibs_Type
                    (id, state, name, idProperty,
                    superTypeId, posNo, posNoPath,
                    isContainer, isInheritable, isSearchable,
                    showInMenu, showInNews, code, nextPropertySeq, actVersion,
                    validUntil)
            VALUES (@l_id, @c_ST_ACTIVE, @ai_name, 1,
                    @l_superTypeId, 0, @c_EMPTYPOSNOPATH,
                    @ai_isContainer, @ai_isInheritable, @ai_isSearchable,
                    @ai_showInMenu, @ai_showInNews, @ai_code, 1, @l_id,
                    DATEADD (month, 120, getDate ()))

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                N'insert', @l_ePos OUTPUT, @l_count OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

            -- check if the row was correctly inserted:
            IF (@l_count = 1)           -- type was inserted correctly?
            BEGIN
                -- get the type id:
                SELECT  @l_id = MAX (id)
                FROM    ibs_Type
                WHERE   code = @ai_code

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'get type id', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call common exception handler

                -- create the new type version:
                EXEC @l_retValue = p_TVersion$new @l_id, @ai_code,
                        @ai_className, @ao_newActVersionId OUTPUT

                -- check if there was an error during creating the type version:
                IF (@l_retValue = @c_ALL_RIGHT) -- type version created?
                BEGIN
                    -- set the type version within the type:
                    UPDATE  ibs_Type
                    SET     actVersion = @ao_newActVersionId
                    WHERE   id = @l_id

                    -- check if there occurred an error:
                    EXEC @l_error = ibs_error.prepareError @@error,
                        N'set type version', @l_ePos OUTPUT
                    IF (@l_error <> 0)  -- an error occurred?
                        GOTO exception  -- call common exception handler

                    -- inherit may contain entries from super type:
                    IF (@l_superTypeId <> @c_noTypeId) -- super type exists?
                    BEGIN
                        EXEC @l_retValue =
                                p_MayContain$inherit @l_superTypeId, @l_id
                    END -- if super type exists
                END -- if type version created
            END -- if type was inserted correctly

        END -- else type id not already there

    -- check if there occurred an error:
    IF (@l_retValue = @c_ALL_RIGHT OR @l_retValue = @c_ALREADY_EXISTS)
                                        -- everything all right?
    BEGIN
        COMMIT TRANSACTION              -- make changes permanent
        SELECT  @ao_newId = @l_id       -- set new id
    END -- if evereything all right
    ELSE                                -- an error occured
        ROLLBACK TRANSACTION            -- undo changes

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Type$new', @l_error, @l_ePos,
            N'ai_id', @ai_id,
            N'ai_name', @ai_name,
            N'ai_isContainer', @ai_isContainer,
            N'ai_code', @ai_code,
            N'ai_isInheritable', @ai_isInheritable,
            N'ai_className', @ai_className,
            N'ai_isSearchable', @ai_isSearchable,
            N'ai_superTypeCode', @ai_superTypeCode,
            N'ai_showInMenu', @ai_showInMenu,
            N'', N'',
            N'ai_showInNews', @ai_showInNews,
            N'', N'',
            N'l_id', @l_id,
            N'', N'',
            N'ao_newId', @ao_newId,
            N'', N'',
            N'ao_newActVersionId', @ao_newActVersionId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Type$new


/******************************************************************************
 * Create a language dependent version of a type. <BR>
 * This procedure uses some mechanisms to get the required data and then
 * creates the type:
 * 1. The language dependent name of the type is determined.
 * 2. If there is no one found the type code is used as name of the type.
 * 3. The type is stored, i.e. if it exists it is changed, otherwise it is
 * created.
 *
 * @input parameters:
 * @param   ai_id               The id of the type.
 * @param   ai_superTypeCode    The code of the super type.
 * @param   ai_isContainer      Is an object of this type a container?
 * @param   ai_isInheritable    May there be a sub type of the type?
 * @param   ai_isSearchable     Is it possible to search for object of the type?
 * @param   ai_showInMenu       Is an object of this type be shown in the menu?
 * @param   ai_showInNews       May instances of this type be included in the
 *                              news?
 * @param   ai_code             The code of the type, i.e. its unique name
 * @param   ai_className        The name of the Java class which implements an
 *                              object of this type.
 * @param   ai_languageId       The language for which the type shall be
 *                              generated.
 * @param   ai_typeNameName     The id through which the language dependent name
 *                              of the type can be found.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Type$newLang'
GO

-- create the new procedure:
CREATE PROCEDURE p_Type$newLang
(
    -- input parameters:
    @ai_id                  TYPEID  = 0x00000000,
    @ai_superTypeCode       NAME,
    @ai_isContainer         BOOL,
    @ai_isInheritable       BOOL,
    @ai_isSearchable        BOOL,
    @ai_showInMenu          BOOL,
    @ai_showInNews          BOOL,
    @ai_code                NAME,
    @ai_className           NAME,
    @ai_languageId          INT,
    @ai_typeNameName        NAME
    -- output parameters:
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_id                   INT,            -- the actual id
    @l_actTVId              TVERSIONID,     -- the type version id
    @l_typeName             NAME,           -- the name of the type
    @l_typeClass            FILENAME        -- the java class (deprecated)

    -- assign constants:

    -- initialize local variables:

-- body:
    -- create the type itself:
    EXEC p_TypeName_01$get @ai_languageId, @ai_typeNameName,
        @l_typeName OUTPUT, @l_typeClass OUTPUT
    IF (@l_typeName IS NULL)
        SELECT @l_typeName = @ai_code

    EXEC @l_retValue = p_Type$new @ai_id, @l_typeName, @ai_superTypeCode,
        @ai_isContainer, @ai_isInheritable, @ai_isSearchable, @ai_showInMenu,
        @ai_showInNews, @ai_code, @ai_className,
        @l_id OUTPUT, @l_actTVId OUTPUT
GO
-- p_Type$newLang


/******************************************************************************
 * Add some tabs to a type. <BR>
 * The tabs for the types are defined. There can be up to 10 tabs defined
 * for each type.
 * In the SQL Server version the tab parameters are optional. <BR>
 * This procedure gets the actual version of the type and calls
 * p_TVersion$addTabs for this type version.
 *
 * @input parameters:
 * @param   ai_code             The code of the type, i.e. its unique name
 * @param   ai_defaultTab       The code of the default tab.
 * @param   ai_tabCodeX         The code of the tab, i.e. the unique name.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Type$addTabs'
GO

-- create the new procedure:
CREATE PROCEDURE p_Type$addTabs
(
    -- input parameters:
    @ai_code                NAME,
    @ai_defaultTab          NAME = NULL,
    @ai_tabCode1            NAME = NULL,
    @ai_tabCode2            NAME = NULL,
    @ai_tabCode3            NAME = NULL,
    @ai_tabCode4            NAME = NULL,
    @ai_tabCode5            NAME = NULL,
    @ai_tabCode6            NAME = NULL,
    @ai_tabCode7            NAME = NULL,
    @ai_tabCode8            NAME = NULL,
    @ai_tabCode9            NAME = NULL,
    @ai_tabCode10           NAME = NULL
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_id                   INT,            -- the actual id
    @l_actTVId              TVERSIONID      -- the type version id

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- get the type data:
    SELECT  @l_actTVId = actVersion
    FROM    ibs_Type
    WHERE   code = @ai_code

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get type data', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- create the tabs:
    EXEC p_TVersion$addTabs @l_actTVId, @ai_defaultTab, @ai_tabCode1,
            @ai_tabCode2, @ai_tabCode3, @ai_tabCode4, @ai_tabCode5,
            @ai_tabCode6, @ai_tabCode7, @ai_tabCode8, @ai_tabCode9,
            @ai_tabCode10

    -- terminate the procedure:
    RETURN

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Type$addTabs', @l_error, @l_ePos,
            N'l_actTVId', @l_actTVId,
            N'ai_code', @ai_code,
            N'', 0,
            N'ai_defaultTab', @ai_defaultTab,
            N'', 0,
            N'ai_tabCode1', @ai_tabCode1,
            N'', 0,
            N'ai_tabCode2', @ai_tabCode2,
            N'', 0,
            N'ai_tabCode3', @ai_tabCode3,
            N'', 0,
            N'ai_tabCode4', @ai_tabCode4,
            N'', 0,
            N'ai_tabCode5', @ai_tabCode5,
            N'', 0,
            N'ai_tabCode6', @ai_tabCode6,
            N'', 0,
            N'ai_tabCode7', @ai_tabCode7,
            N'', 0,
            N'ai_tabCode8', @ai_tabCode8,
            N'', 0,
            N'ai_tabCode9', @ai_tabCode9
GO
-- p_Type$addTabs


/******************************************************************************
 * Create a new type (no rights check). <BR>
 * This procedure calls p_Type$new, which contains a TRANSACTION block, so it
 * is not allowed to call this procedure from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_superTypeId      Super type of the type. 0 => no super type
 * @param   ai_isContainer      Is the type a container. 1 => true
 * @param   ai_isInheritable    May the type be inherited, i.e. a sub type be
 *                              created?
 * @param   ai_isSearchable     Is it possible to search for object of the type?
 * @param   ai_showInMenu       May instances of this type be displayed in a
 *                              menu?
 * @param   ai_showInNews       May instances of this type be included in the
 *                              news?
 * @param   ai_code             Code of the type (unique name).
 * @param   ai_className        Name of class which is responsible for the
 *                              actual (= first, if type is new) version of the
 *                              type.
 * @param   ai_description      Description of the type.
 *
 * @output parameters:
 * @param   ao_id               Id of the newly created type.
 * @param   ao_actVersionId     Id of the actual version for the new type.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Type$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Type$create
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_superTypeId         TYPEID,
    @ai_isContainer         BOOL,
    @ai_isInheritable       BOOL,
    @ai_isSearchable        BOOL,
    @ai_showInMenu          BOOL,
    @ai_showInNews          BOOL,
    @ai_code                NAME,
    @ai_className           NAME,
    @ai_description         DESCRIPTION,
    -- output parameters:
    @ao_id                  TYPEID OUTPUT,
    @ao_actVersionId        TVERSIONID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,                -- everything was o.k.

    -- local variables:
    @l_retValue             INT,                -- return value of function
    @l_superTypeCode        NAME                -- the code of the super type

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_superTypeCode        = ''

-- body:
    -- get the type code of the super type:
    SELECT  @l_superTypeCode = code
    FROM    ibs_Type
    WHERE   id = @ai_superTypeId

    -- create the new type and its first version:
    EXEC @l_retValue = p_Type$new 0, @ai_name, @ai_superTypeId,
        @ai_isContainer, @ai_isInheritable, @ai_isSearchable, @ai_showInMenu,
        @ai_showInNews, @ai_code, @ai_className,
        @ao_id OUTPUT, @ao_actVersionId OUTPUT

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Type$create



/******************************************************************************
 * Change the attributes of an existing type (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_id               ID of the type to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_idProperty       Id of the property used to represent the id of
 *                              one object of this type.
 * @param   ai_superTypeId      Id of the superior type.
 * @param   ai_code             Code of the type.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Type$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Type$change
(
    -- input parameters:
    @ai_id                  TYPEID,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    -- type-specific parameters:
    @ai_idProperty          PROPERTYSEQ,
    @ai_superTypeId         TYPEID = 0x0000000000000000,
    @ai_code                NAME
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,                -- something went wrong
    @c_ALL_RIGHT            INT,                -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,                -- not enough rights for this
                                                -- operation
    @c_OBJECTNOTFOUND       INT,                -- the object was not found

    -- local variables:
    @l_retValue             INT,                -- return value of a function
    @l_rights               RIGHTS,             -- rights value
    @l_oid                  OBJECTID            -- oid of the type

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rights = 0

-- body:
    -- get object id of type:
    SELECT  @l_oid = oid
    FROM    ibs_Type
    WHERE   id = @ai_id

    -- check if the type exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC @l_rights = p_Rights$checkRights
            @l_oid,                     -- given object to be accessed by user
            @ai_superTypeId,            -- container of given object
            @ai_userId,                 -- user id
            @ai_op,                     -- required rights user must have to
                                        -- update object
            @l_rights OUTPUT            -- returned value

        -- check if the user has the necessary rights
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            BEGIN TRANSACTION

            -- update the properties of the type:
            UPDATE  ibs_Type
            SET     name = @ai_name,
                    validUntil = @ai_validUntil,
                    description = @ai_description,
                    idProperty = @ai_idProperty,
                    superTypeId = @ai_superTypeId,
                    code = @ai_code
            WHERE   id = @ai_id

            COMMIT TRANSACTION
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- set the return value with the error code:
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
-- p_Type$change



/******************************************************************************
 * Get all data from a given type (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_id               Id of the type to be changed.
 * @param   ai_userId           Id of the user who is creating the type.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_name             Name of the object itself.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_idProperty       Id of the property used to represent the id of
 *                              one object of this type.
 * @param   ao_superTypeId      Id of the superior type.
 * @param   ao_isContainer      Is the type a container?
 * @param   ao_code             Code of the type.
 * @param   ao_nextPropertySeq  Sequence number of the next new property.
 * @param   ao_actVersionId     Id of the actual version.
 * @param   ao_actVersionSeq    Sequence number of the actual version.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Type$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Type$retrieve
(
    -- input parameters:
    @ai_id                  TYPEID,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- common output parameters:
    @ao_state               STATE           OUTPUT,
    @ao_name                NAME            OUTPUT,
    @ao_validUntil          DATETIME        OUTPUT,
    @ao_description         DESCRIPTION     OUTPUT,
    -- type-specific output parameters:
    @ao_idProperty          PROPERTYSEQ     OUTPUT,
    @ao_superTypeId         TYPEID          OUTPUT,
    @ao_isContainer         BOOL            OUTPUT,
    @ao_code                NAME            OUTPUT,
    @ao_nextPropertySeq     PROPERTYSEQ     OUTPUT,
    @ao_actVersionId        TVERSIONID      OUTPUT,
    @ao_actVersionSeq       TVERSIONSEQ     OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,                -- something went wrong
    @c_ALL_RIGHT            INT,                -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,                -- not enough rights for this
                                                -- operation
    @c_OBJECTNOTFOUND       INT,                -- the object was not found

    -- local variables:
    @l_retValue             INT,                -- return value of a function
    @l_rights               RIGHTS,             -- rights value
    @l_oid                  OBJECTID            -- oid of the type

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_rights = 0

-- body:
    -- get id of superior type
    SELECT  @ao_superTypeId = superTypeId
    FROM    ibs_Type
    WHERE   id = @ai_id

    -- check if the type exists:
    IF (@@ROWCOUNT > 0)                 -- type exists?
    BEGIN
        -- get rights for this user
        EXEC p_Rights$checkRights
             @ai_id,                    -- given type to be accessed by user
             @ao_superTypeId,           -- superior type of given type
             @ai_userId,                -- user_id
             @ai_op,                    -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @l_rights OUTPUT           -- returned value

        -- check if the user has the necessary rights
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            -- get the data of the type and return it
            SELECT  @l_oid = t.oid, @ao_state = t.state, @ao_name = t.name,
                    @ao_validUntil = t.validUntil,
                    @ao_description = t.description,
                    @ao_idProperty = t.idProperty,
                    @ao_superTypeId = t.superTypeId,
                    @ao_isContainer = t.isContainer,
                    @ao_code = t.code,
                    @ao_nextPropertySeq = nextPropertySeq,
                    @ao_actVersionId = t.actVersion,
                    @ao_actVersionSeq = tv.tVersionSeq
            FROM    ibs_Type t LEFT JOIN ibs_TVersion tv ON t.actVersion = tv.id
            WHERE   t.id = @ai_id

            -- set object as already read:
            EXEC    p_setRead @l_oid, @ai_userId
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
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
-- p_Type$retrieve


/******************************************************************************
 * Show all types. <BR>
 *
 * @input parameters:
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_showTypes'
GO

-- create the new procedure:
CREATE PROCEDURE p_showTypes
    -- input parameters:
    -- output parameters:
AS
    -- get the types and display them:
    SELECT  CONVERT (BINARY (4), id) AS id,
            CONVERT (BINARY (4), actVersion) AS tVersionId,
            CONVERT (BINARY (4), superTypeId) AS superType,
            name
    FROM    ibs_Type
GO
-- p_showTypes


/******************************************************************************
 * Delete given type (including all its tversions). <BR>
 * 1. First ibs_Type is checked if there is an according id/code entry
 *    If not: deletion will not be performed
 *    (returns TYPE_MISMATCH).
 * 2. Each tversions will be deleted - if there exist activ objects
 *    with given type then no deletion will be performed
 *    (returns ALREADY_EXISTS).
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_typeId           Type for which all type versions shall be
 *                              deleted.
 * @param   ai_code             Code of type (for security reasons)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_TYPE_MISMATCH          Given id/code is not valid (no entry).
 * c_ALREADY_EXISTS         Found objects with this type(version)
 *                          in ibs_Object - deletion not possible.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Type$deletePhysical'
GO

-- create the new procedure:
CREATE PROCEDURE p_Type$deletePhysical
(
    -- input parameters:
    @ai_typeId              TYPEID,
    @ai_code                NAME
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_TYPE_MISMATCH        INT,            -- a type mismatch occurred

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21,
    @c_TYPE_MISMATCH        = 99

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- check if the given id/code pair is an entry in ibs_Type:
    SELECT  @l_rowCount = count (id)
    FROM    ibs_Type
    WHERE   @ai_code = code
        AND @ai_typeId = id

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'check id/code pair', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call exception handler

    -- check if the id/code pair was found:
    IF (@l_rowCount = 0)                -- didn't find the specific id/code
                                        -- pair?
    BEGIN
        -- set error code:
        SELECT  @l_retValue = @c_TYPE_MISMATCH
        SELECT  @l_ePos = @l_ePos + N' Type Mismatch:' +
                N' ai_typeId = ' + CONVERT (VARCHAR, @ai_typeId) +
                N', ai_code = ' + @ai_code
        EXEC ibs_error.logError 500, N'p_Type$deletePhysical',
            @l_error, @l_ePos,
            N'ai_typeId', @ai_typeId,
            N'ai_code', @ai_code
    END -- if didn't find the specific id/code pair
    ELSE                                -- id/code pair found
    BEGIN
        -- type can be deleted
        BEGIN TRANSACTION -- begin new TRANSACTION
            -- delete all type versions of given type
            -- procedure possibly returns c_ALREADY_EXISTS
            EXEC @l_retValue = p_tVersion$deletePhysical @ai_typeId

            -- problems while deleting tversions?
            IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
            BEGIN
                -- delete the may contain entries for the type:
                EXEC @l_retValue = p_MayContain$deleteType @ai_typeId

                IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
                BEGIN
                    -- delete the type:
                    DELETE  ibs_Type
                    WHERE   id = @ai_typeId

                    -- check if there occurred an error:
                    EXEC @l_error = ibs_error.prepareError @@error,
                        N'Error when deleting ibs_Type entry', @l_ePos OUTPUT
                    IF (@l_error <> 0)      -- an error occurred?
                        GOTO exception      -- call common exception handler
                END -- if everything all right
            END -- if everything all right

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything all right?
            COMMIT TRANSACTION          -- make changes permanent
        ELSE                            -- an error occured
            ROLLBACK TRANSACTION        -- undo changes
    END -- else id/code pair found

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Type$deletePhysical', @l_error, @l_ePos,
            N'ai_typeId', @ai_typeId,
            N'ai_code', @ai_code
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Type$deletePhysical
