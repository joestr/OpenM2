/******************************************************************************
 * All stored procedures regarding the domain scheme table. <BR>
 * 
 * @version     1.11.0001, 20.02.2000
 *
 * @author      Klaus Reimüller (KR)  000220
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_DomainScheme_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_DomainScheme_01$create
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
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_ALREADY_EXISTS       INT,            -- the object already exists

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId, 
                            @ai_name, @ai_containerId_s, @ai_containerKind, 
                            @ai_isLink, @ai_linkedObjectId_s, @ai_description, 
                            @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- object created successfully?
        BEGIN
            -- create object specific data:
            -- insert just a tuple with the oid into the table and use the
            -- default values:
            INSERT INTO ibs_DomainScheme_01 
                (id, oid, hasCatalogManagement, hasDataInterchange, workspaceProc)
            VALUES  (0, @l_oid, 0, 0, N'p_Workspace_01$createObjects')

            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT  @l_retValue = @c_NOT_OK -- set return value
            END -- if no row affected
/*
            ELSE                        -- insertion performed properly
            BEGIN
                -- create type specific tab objects:
                -- there are no type specific tab objects.
            END -- else insertion performed properly
*/
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_DomainScheme_01$create


/******************************************************************************
 * Change the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_id               ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_showInNews       Shall the currrent object be displayed in the 
 *                              news?
 * @param   ai_hasCatalogManagement Does a domain with this scheme have a 
 *                              catalog management?
 * @param   ai_hasDataInterchange Does a domain with this scheme have a 
 *                              data interchange component?
 * @param   ai_workspaceProc    The name of the procedure for creating a
 *                              user's workspace within a domain having this
 *                              scheme?
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_DomainScheme_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_DomainScheme_01$change
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    -- type-specific input parameters:
    @ai_hasCatalogManagement BOOL,
    @ai_hasDataInterchange  BOOL,
    @ai_workspaceProc       STOREDPROCNAME
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, 
                @ai_op, @ai_name, @ai_validUntil, @ai_description, @ai_showInNews,
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- update object type specific data:
            UPDATE  ibs_DomainScheme_01
            SET     hasCatalogManagement = @ai_hasCatalogManagement,
                    hasDataInterChange = @ai_hasDataInterChange,
                    workspaceProc = @ai_workspaceProc
            WHERE   oid = @l_oid

            -- check if change was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @l_retValue = @c_NOT_OK -- set return value
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_DomainScheme_01$change


/******************************************************************************
 * Creates a new domain scheme. <BR>
 * This is a shortcut procedure which can be used for batch or installation
 * scripts.
 *
 * @input parameters:
 * @param   ai_name             Name of the object.
 * @param   ai_description      Description of the object.
 * @param   ai_workspaceProc    Name of procedure to create workspace of an 
 *                              user.
 * @param   ai_likeName         Comparison string for all existing domains.
 *                              Each domain whose name is like the likeName
 *                              is changed to the new domain scheme.
 * @param   ai_hasCatalogManagement Shall a new domain have a catalog mmt?
 * @param   ai_hasDataInterchange Shall a new domain have a DI component?
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_DomainScheme_01$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_DomainScheme_01$new
(
    -- input parameters:
    @ai_name                NAME,
    @ai_description         DESCRIPTION,
    @ai_workspaceProc       STOREDPROCNAME,
    @ai_likeName            NAME,
    @ai_hasCatalogManagement BOOL,
    @ai_hasDataInterchange  BOOL,
    -- output parameters:
    @ao_oid                 OBJECTID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,           -- default value for no defined oid
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_NOT_OK               INT,                -- something went wrong
    @c_ALL_RIGHT            INT,                -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,                -- not enough rights for this operation
    @c_ALREADY_EXISTS       INT,                -- the object already exists
    @c_TVDomainSchemeContainer INT,             -- tVersionId of domain scheme container
    @c_TVDomainScheme       INT,                -- tVersionId of domain scheme

    -- local variables:
    @l_admin                USERID,             -- the id of the system administrator
    @l_oid                  OBJECTID,           -- the actual oid
    @l_oid_s                OBJECTIDSTRING,     -- the actual oid as string
    @l_containerId          OBJECTID,           -- the container oid
    @l_containerId_s        OBJECTIDSTRING,     -- the container oid as string
    @l_id                   ID,                 -- id of the domain scheme
    @l_validUntil           DATETIME,           -- until when is the domain scheme valid?
    @l_retValue             INT                 -- return value of this function


-- assign constants:
SELECT
    @c_NOOID = 0x0000000000000000,
    @c_NOOID_s = '0x0000000000000000',
    @c_NOT_OK = 0,
    @c_ALL_RIGHT = 1,
    @c_INSUFFICIENT_RIGHTS = 2,
    @c_ALREADY_EXISTS = 21,
    @c_TVDomainSchemeContainer = 16843025, -- 0x01010111
    @c_TVDomainScheme = 16843041        -- 0x01010121

-- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK

-- body:
    -- initialize return values:
    SELECT  @ao_oid = @c_NOOID

    -- get the system administrator:
    SELECT  @l_admin = MIN (id)
    FROM    ibs_User
    WHERE   domainId = 0

    IF (@@ROWCOUNT = 1)                 -- the administrator was found?
    BEGIN
        -- get the domain scheme container:
        SELECT  @l_containerId = oid
        FROM    ibs_Object
        WHERE   tVersionId = @c_TVDomainSchemeContainer
            AND containerId = 
                    (SELECT oid 
                    FROM    ibs_Object 
                    WHERE   containerId = @c_NOOID
                    )

        IF (@@ROWCOUNT = 1)             -- the domain scheme container was found?
        BEGIN
            -- convert container oid to string representation:
            EXEC p_byteToString @l_containerId, @l_containerId_s OUTPUT

            -- create the scheme:
            EXEC @l_retValue = p_DomainScheme_01$create @l_admin, 1, @c_TVDomainScheme, 
                @ai_name, @l_containerId_s, 1, 0, @c_NOOID_s, @ai_description, 
                @l_oid_s OUTPUT

            IF (@l_retValue = @c_ALL_RIGHT) -- domain scheme created?
            BEGIn
                -- convert string representation of oid to oid:
                EXEC p_stringToByte @l_oid_s, @l_oid OUTPUT

                -- get the data of the domain scheme:
                SELECT  @l_id = ds.id, @l_validUntil = o.validUntil
                FROM    ibs_DomainScheme_01 ds, ibs_Object o
                WHERE   ds.oid = @l_oid
                    AND o.oid = ds.oid

                IF (@@ROWCOUNT = 1)         -- the domain scheme was found?
                BEGIN
                    EXEC @l_retValue = p_DomainScheme_01$change @l_oid_s, @l_admin, 1, 
                        @ai_name, @l_validUntil, @ai_description, 0, 
                        @ai_hasCatalogManagement, @ai_hasDataInterchange, @ai_workspaceProc

                    IF (@l_retValue = @c_ALL_RIGHT) -- domain scheme changed?
                    BEGIN
                        -- set already existing domains for actual scheme:
                        UPDATE  ibs_Domain_01
                        SET     scheme = @l_id
                        WHERE   oid IN
                                (
                                    SELECT  d.oid
                                    FROM    ibs_Domain_01 d, ibs_Object o
                                    WHERE   d.oid = o.oid
                                        AND o.name LIKE @ai_likeName
                                )

                        -- set output parameter:
                        SELECT  @ao_oid = @l_oid
                    END -- if domain scheme changed
                END -- if the domain scheme was found
            END -- if domain scheme created
        END -- if the domain scheme container was found
    END -- if the administrator was found
GO
-- p_DomainScheme_01$new


/******************************************************************************
 * Get all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be retrieved.
 * @param   ai_userId           Id of the user who is getting the data.
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
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       The showInNews flag.
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out
 * @param   ao_checkOutUser     Oid of the user which checked out the object
 * @param   ai_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName name of the user which checked out the object,
 *                              is only set if this user has the right to read
 *                              the checkOut-User
 * @param   ao_hasCatalogManagement Does a domain with this scheme have a 
 *                              catalog management?
 * @param   ao_hasDataInterchange Does a domain with this scheme have a 
 *                              data interchange component?
 * @param   ao_workspaceProc    The name of the procedure for creating a
 *                              user's workspace within a domain having this
 *                              scheme?
 * @param   ao_numberOfDomains  The number of domains where this domain scheme
 *                              is used.
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_DomainScheme_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_DomainScheme_01$retrieve
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- common output parameters:
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
    -- type-specific output parameters:
    @ao_hasCatalogManagement BOOL           OUTPUT,
    @ao_hasDataInterchange   BOOL           OUTPUT,
    @ao_workspaceProc       STOREDPROCNAME  OUTPUT,
    @ao_numberOfDomains     INT             OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_oid                  OBJECTID,       -- the actual oid
    @l_id                   ID,             -- the id of the scheme
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @l_retValue = p_Object$performRetrieve
                @ai_oid_s, @ai_userId, @ai_op,
                @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT, 
                @ao_name OUTPUT, 
                @ao_containerId OUTPUT, @ao_containerName OUTPUT, @ao_containerKind OUTPUT,
                @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT, 
                @ao_owner OUTPUT, @ao_ownerName OUTPUT, 
                @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
                @ao_validUntil OUTPUT, @ao_description OUTPUT, @ao_showInNews OUTPUT, 
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT, 
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT, @ao_checkOutUserName OUTPUT, 
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- retrieve object type specific data:
            SELECT  @ao_hasCatalogManagement = hasCatalogManagement,
                    @ao_hasDataInterchange = hasDataInterchange,
                    @ao_workspaceProc = workspaceProc,
                    @l_id = id
            FROM    ibs_DomainScheme_01
            WHERE   oid = @l_oid

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT > 0)         -- everything o.k.?
                -- get the number of domains where this scheme is used:
                SELECT  @ao_numberOfDomains = COUNT (*)
                FROM    ibs_Object o, ibs_Domain_01 d
                WHERE   o.oid = d.oid
                    AND o.state = 2
                    AND d.scheme = @l_id
            ELSE                        -- no row affected
                SELECT  @l_retValue = @c_NOT_OK -- set return value
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_DomainScheme_01$retrieve


/******************************************************************************
 * Delete an object and all its values (incl. rights check). <BR>
 * This procedure also deletes all links showing to this object.
 * 
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_DomainScheme_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_DomainScheme_01$delete
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_oid                  OBJECTID,   -- the actual oid
    @l_retValue             INT         -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_oid = @c_NOOID,
    @l_retValue = @c_NOT_OK

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId, @ai_op, 
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  ibs_DomainScheme_01
            WHERE   oid NOT IN 
                    (SELECT oid 
                    FROM    ibs_Object)

            -- check if deletion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
                SELECT  @l_retValue = @c_NOT_OK -- set return value
        END -- if operation performed properly
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_DomainScheme_01$delete


/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_newOid           The oid of the copy.
 *
 * @output parameters:
 * @return A value representing the state of the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_DomainScheme_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_DomainScheme_01$BOCopy
(
    -- common input parameters:
    @ai_oid                 OBJECTID,
    @ai_userId              USERID,
    @ai_newOid              OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT             -- return value of this function

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_NOT_OK

    -- body:
    -- make an insert for all type specific tables:
    INSERT  INTO ibs_DomainScheme_01
            (oid, hasCatalogManagement, hasDataInterchange, workspaceProc)
    SELECT  @ai_newOid, hasCatalogManagement, hasDataInterchange, workspaceProc
    FROM    ibs_DomainScheme_01
    WHERE   oid = @ai_oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @l_retValue = @c_ALL_RIGHT -- set return value

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_DomainScheme_01$BOCopy
