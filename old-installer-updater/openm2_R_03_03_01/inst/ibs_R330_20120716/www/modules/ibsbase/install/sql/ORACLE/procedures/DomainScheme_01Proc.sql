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
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DomainScheme_01$create
(
    -- common input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- common output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this operation
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists

    -- local variables:
    l_oid                   RAW (8) := c_NOOID; -- the actual oid
    l_retValue              INT := c_NOT_OK;    -- return value of this function


-- body:
BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, 
                        ai_name, ai_containerId_s, ai_containerKind, 
                        ai_isLink, ai_linkedObjectId_s, ai_description, 
                        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)   -- object created successfully?
    THEN
        -- create object specific data:
        -- insert just a tuple with the oid into the table and use the
        -- default values:
        BEGIN
            INSERT INTO ibs_DomainScheme_01 
                (id, oid, hasCatalogManagement, hasDataInterchange, workspaceProc)
            VALUES  (0, l_oid, 0, 0, 'p_Workspace_01$createObjects');

            -- check if insertion was performed properly:
            IF (SQL%ROWCOUNT <= 0)          -- no row affected?
            THEN
                l_retValue := c_NOT_OK;     -- set return value
            END IF; -- no row affected
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$create',
                    'Error in INSERT - Statement');
                l_retValue := c_NOT_OK;
                RAISE;
        END;
    END IF; -- object created successfully

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$create',
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            ', ai_tVersionId = ' || ai_tVersionId  ||
            ', ai_name = ' || ai_name  ||
            ', ai_containerId_s = ' || ai_containerId_s  ||
            ', ai_containerKind = ' || ai_containerKind  ||
            ', ai_isLink = ' || ai_isLink  ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s  ||
            ', ai_description = ' || ai_description  ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );
        err;
        RAISE;
END p_DomainScheme_01$create;
/

show errors;


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
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
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DomainScheme_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           NUMBER,
    -- type-specific input parameters:
    ai_hasCatalogManagement NUMBER,
    ai_hasDataInterchange   NUMBER,
    ai_workspaceProc        VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_oid                   RAW (8) := c_NOOID; -- the actual oid
    l_retValue              INT := c_NOT_OK;    -- return value of this function

-- body:
BEGIN
    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, 
            ai_op, ai_name, ai_validUntil, ai_description, ai_showInNews,
            l_oid);

    IF (l_retValue = c_ALL_RIGHT) -- operation performed properly?
    THEN
        -- update object type specific data:
        BEGIN
            UPDATE  ibs_DomainScheme_01
            SET     hasCatalogManagement = ai_hasCatalogManagement,
                    hasDataInterchange = ai_hasDataInterchange,
                    workspaceProc = ai_workspaceProc
            WHERE   oid = l_oid;

            -- check if change was performed properly:
            IF (SQL%ROWCOUNT <= 0)          -- no row affected?
            THEN
                l_retValue := c_NOT_OK;     -- set return value
            END IF; -- no row affected
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$change',
                    'Error in UPDATE - Statement');
                l_retValue := c_NOT_OK;
                RAISE;
        END;
    END IF; -- operation performed properly

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$change',
            ', ai_oid_s = ' || ai_oid_s  ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            ', ai_name = ' || ai_name  ||
            ', ai_validUntil = ' || ai_validUntil ||
            ', ai_description = ' || ai_description  ||
            ', ai_showInNews = ' || ai_showInNews  ||
            ', ai_hasCatalogManagement = ' || ai_hasCatalogManagement  ||
            ', ai_hasDataInterchange = ' || ai_hasDataInterchange  ||
            ', ai_workspaceProc = ' || ai_workspaceProc  ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );
        err;
        RAISE;
END p_DomainScheme_01$change;
/

show errors;


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
CREATE OR REPLACE PROCEDURE p_DomainScheme_01$new
(
    -- input parameters:
    ai_name                 VARCHAR2,
    ai_description          VARCHAR2,
    ai_workspaceProc        VARCHAR2,
    ai_likeName             VARCHAR2,
    ai_hasCatalogManagement NUMBER,
    ai_hasDataInterchange   NUMBER,
    -- output parameters:
    ao_oid                  OUT RAW
)
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
                                            -- no oid as string
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this operation
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists
    c_TVDomainSchemeContainer CONSTANT INTEGER := 16843025; -- 0x01010111
                                            -- tVersionId of domain scheme container
    c_TVDomainScheme        CONSTANT INTEGER := 16843041; -- 0x01010121
                                            -- tVersionId of domain scheme

    -- local variables:
    l_admin                 INT;                -- the id of the system administrator
    l_oid                   RAW (8);            -- the actual oid
    l_oid_s                 VARCHAR2 (18);      -- the actual oid as string
    l_containerId           RAW (8);            -- the container oid
    l_containerId_s         VARCHAR2 (18);      -- the container oid as string
    l_id                    INT;                -- id of the domain scheme
    l_validUntil            DATE;               -- until when is the domain scheme valid?
    l_retValue              INT := c_NOT_OK;    -- return value of this function


-- body:
BEGIN
    -- initialize return values:
    ao_oid := c_NOOID;

    BEGIN
        -- get the system administrator:
        SELECT  MIN (id)
        INTO    l_admin
        FROM    ibs_User
        WHERE   domainId = 0;

        -- get the domain scheme container:
        SELECT  oid
        INTO    l_containerId
        FROM    ibs_Object
        WHERE   tVersionId = c_TVDomainSchemeContainer
            AND containerId = 
                    (SELECT oid 
                    FROM    ibs_Object 
                    WHERE   containerId = c_NOOID
                    );

        -- convert container oid to string representation:
        p_byteToString (l_containerId, l_containerId_s);

        -- create the scheme:
        l_retValue := p_DomainScheme_01$create (l_admin, 1, c_TVDomainScheme, 
            ai_name, l_containerId_s, 1, 0, c_NOOID_s, ai_description, 
            l_oid_s);

        IF (l_retValue = c_ALL_RIGHT)   -- domain scheme created?
        THEN
            -- convert string representation of oid to oid:
            p_stringToByte (l_oid_s, l_oid);

            BEGIN
                -- get the data of the domain scheme:
                SELECT  ds.id, o.validUntil
                INTO    l_id, l_validUntil
                FROM    ibs_DomainScheme_01 ds, ibs_Object o
                WHERE   ds.oid = l_oid
                    AND o.oid = ds.oid;

                l_retValue := p_DomainScheme_01$change (l_oid_s, l_admin, 1, 
                    ai_name, l_validUntil, ai_description, 0, 
                    ai_hasCatalogManagement, ai_hasDataInterchange, ai_workspaceProc);

                IF (l_retValue = c_ALL_RIGHT) -- domain scheme changed?
                THEN
                    BEGIN
                        -- set already existing domains for actual scheme:
                        UPDATE  ibs_Domain_01
                        SET     scheme = l_id
                        WHERE   ROWID IN
                                (
                                    SELECT  d.ROWID
                                    FROM    ibs_Domain_01 d, ibs_Object o
                                    WHERE   d.oid = o.oid
                                        AND o.name LIKE ai_likeName
                                );
                    EXCEPTION
                        WHEN OTHERS THEN
                            ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$new',
                                'Error in UPDATE - Statement');
                            RAISE;
                    END;

                    -- set output parameter:
                    ao_oid := l_oid;
                END IF; -- if domain scheme changed
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$new',
                        'Error in SELECT - Statement');
                    RAISE;
            END;
        END IF; -- if domain scheme created
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$new',
                'System administrator not found.');
            RAISE;
    END;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$new',
            ', ai_name = ' || ai_name  ||
            ', ai_workspaceProc = ' || ai_workspaceProc  ||
            ', ai_likeName = ' || ai_likeName  ||
            ', ai_hasCatalogManagement = ' || ai_hasCatalogManagement  ||
            ', ai_hasDataInterchange = ' || ai_hasDataInterchange  ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );
        err;
        RAISE;
END p_DomainScheme_01$new;
/

show errors;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
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
 * @param   ao_ownerName        Name of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_creatorName      Name of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_changerName      Name of person who did the last change to the
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
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DomainScheme_01$retrieve
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- common output parameters:
    ao_state                OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_typeName             OUT VARCHAR2,
    ao_name                 OUT VARCHAR2,
    ao_containerId          OUT RAW,
    ao_containerName        OUT VARCHAR2,
    ao_containerKind        OUT INTEGER,
    ao_isLink               OUT NUMBER,
    ao_linkedObjectId       OUT RAW,
    ao_owner                OUT INTEGER,
    ao_ownerName            OUT VARCHAR2,
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2,
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2,
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT NUMBER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,
    -- type-specific output parameters:
    ao_hasCatalogManagement OUT NUMBER,
    ao_hasDataInterchange   OUT NUMBER,
    ao_workspaceProc        OUT VARCHAR2,
    ao_numberOfDomains      OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_oid                   RAW (8) := c_NOOID; -- the actual oid
    l_id                    NUMBER (10) := 0;   -- the id of the scheme
    l_retValue              INT := c_NOT_OK;    -- return value of this function

-- body:
BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (
            ai_oid_s, ai_userId, ai_op,
            ao_state, ao_tVersionId, ao_typeName, ao_name, 
            ao_containerId, ao_containerName, ao_containerKind, 
            ao_isLink, ao_linkedObjectId, 
            ao_owner, ao_ownerName, 
            ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName,
            ao_validUntil, ao_description, ao_showInNews, 
            ao_checkedOut, ao_checkOutDate, 
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
            l_oid);

    IF (l_retValue = c_ALL_RIGHT) -- operation performed properly?
    THEN
        -- retrieve object type specific data:
        BEGIN
            SELECT  hasCatalogManagement, hasDataInterchange, workspaceProc, id
            INTO    ao_hasCatalogManagement, ao_hasDataInterchange, 
                    ao_workspaceProc, l_id
            FROM    ibs_DomainScheme_01
            WHERE   oid = l_oid;

            -- check if retrieve was performed properly:
            IF (SQL%ROWCOUNT > 0)       -- everything o.k.?
            THEN
                BEGIN
                    -- get the number of domains where this scheme is used:
                    SELECT  COUNT (*)
                    INTO    ao_numberOfDomains
                    FROM    ibs_Object o, ibs_Domain_01 d
                    WHERE   o.oid = d.oid
                        AND o.state = 2
                        AND d.scheme = l_id;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$retrieve',
                            'Error in SELECT COUNT - Statement');
                        l_retValue := c_NOT_OK;
                        RAISE;
                END;
            ELSE                        -- no row affected
                l_retValue := c_NOT_OK; -- set return value
            END IF; -- no row affected
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$retrieve',
                    'Error in SELECT - Statement');
                l_retValue := c_NOT_OK;
                RAISE;
        END;
    END IF; -- operation performed properly

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$retrieve',
            ', ai_oid_s = ' || ai_oid_s  ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );
        err;
        RAISE;
END p_DomainScheme_01$retrieve;
/

show errors;


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DomainScheme_01$delete
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_oid                   RAW (8) := c_NOOID; -- the actual oid
    l_retValue              INT := c_NOT_OK;    -- return value of this function

-- body:
BEGIN
    -- delete base object:
    l_retValue := p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);

    IF (l_retValue = c_ALL_RIGHT)   -- operation performed properly?
    THEN
        -- delete object type specific data:
        -- (deletes all type specific tuples which are not within ibs_Object)
        BEGIN
            DELETE  ibs_DomainScheme_01
            WHERE   oid NOT IN 
                    (SELECT oid 
                    FROM    ibs_Object);

            -- check if deletion was performed properly:
            IF (SQL%ROWCOUNT <= 0)          -- no row affected?
            THEN
                l_retValue := c_NOT_OK;     -- set return value
            END IF; -- no row affected
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$delete',
                    'Error in DELETE - Statement');
                l_retValue := c_NOT_OK;
                RAISE;
        END;
    END IF; -- operation performed properly

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$delete',
            ', ai_oid_s = ' || ai_oid_s  ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );
        err;
        RAISE;
END p_DomainScheme_01$delete;
/

show errors;


/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_newOid           The oid of the copy.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DomainScheme_01$BOCopy
(
    -- common input parameters:
    ai_oid                  RAW,
    ai_userId               INTEGER,
    ai_newOid               RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                                -- default value for no defined oid
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INT := c_NOT_OK;    -- return value of this function

-- body:
BEGIN
    -- make an insert for all type specific tables:
    BEGIN
        INSERT  INTO ibs_DomainScheme_01
                (oid, hasCatalogManagement, hasDataInterchange, workspaceProc)
        SELECT  ai_newOid, hasCatalogManagement, hasDataInterchange, workspaceProc
        FROM    ibs_DomainScheme_01
        WHERE   oid = ai_oid;

        -- check if insert was performed correctly:
        IF (SQL%ROWCOUNT >= 1)          -- at least one row affected?
        THEN
            l_retValue := c_ALL_RIGHT;  -- set return value
        END IF; -- at least one row affected
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$BOCopy',
                'Error in INSERT - Statement');
            l_retValue := c_NOT_OK;
            RAISE;
    END;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_DomainScheme_01$BOCopy',
            ', ai_oid = ' || ai_oid  ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_newOid = ' || ai_newOid  ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );
        err;
        RAISE;
END p_DomainScheme_01$BOCopy;
/

show errors;

EXIT;
