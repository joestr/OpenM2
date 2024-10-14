/******************************************************************************
 * All stored procedures regarding the XMLDiscussionTemplate_01 Object. <BR>
 *
 * @version     $Id: XMLDiscussionTemplateProc.sql,v 1.5 2009/12/02 18:35:03 rburgermann Exp $
 *
 * @author      Keim Christine (CK)  001006
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new XMLDiscussionTemplate_01 Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId               ID of the user who is creating the object.
 * @param   ai_op                   Operation to be performed (used for rights 
 *                                  check).
 * @param   ai_tVersionId           Type of the new object.
 * @param   ai_name                 Name of the object.
 * @param   ai_containerId_s        ID of the container where object shall be 
 *                                  created in.
 * @param   ai_containerKind        Kind of object/container relationship
 * @param   ai_isLink               Defines if the object is a link
 * @param   ai_linkedObjectId_s     If the object is a link this is the ID of
 *                                  the where the link shows to.
 * @param   ai_description          Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s                OID of the newly created object.
 *
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT                     Action performed, values returned,
 *                                  everything ok.
 *  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 *  c_NOT_OK                        Something went wrong.
 */
 
-- delete existing procedure: 
EXEC p_dropProc N'p_XMLDiscTemplate_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLDiscTemplate_01$create
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
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_retValue             ID,             -- reutrn value of this procedure
    @l_oid                  OBJECTID        -- oid of the object

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
        -- create base object:
        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_NOT_OK
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op,
                @ai_tVersionId, @ai_name, @ai_containerId_s, @ai_containerKind,
                @ai_isLink, @ai_linkedObjectId_s, @ai_description,
                @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- insert the other values
            INSERT INTO     m2_XMLDiscussionTemplate_01
                            (oid, level1, level2, level3)
            VALUES          (@l_oid, @c_NOOID, @c_NOOID, @c_NOOID)
        END -- if object created successfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_XMLDiscussionTemplate_01$create

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s                ID of the object to be changed.
 * @param   ai_userId               ID of the user who is creating the object.
 * @param   ai_op                   Operation to be performed (used for rights 
 *                                  check).
 * @param   ai_name                 Name of the object.
 * @param   ai_description          Description of the object.
 * @param   ai_validUntil           Date until which the object is valid.
 * @param   ai_showInNews           show in news flag.
 * @param   ai_level1_s             oid of the template in the first level of
 *                                  the discussion.
 * @param   ai_level2_s             oid of the template in the second level of
 *                                  the discussion.
 * @param   ai_level3_s             oid of the template in the third level of
 *                                  the discussion.
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  c_ALL_RIGHT                     Action performed, values returned,
 *                                  everything ok.
 *  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 *  c_OBJECTNOTFOUND                The required object was not found within
 *                                  the database.
 */

-- delete existing procedure: 
EXEC p_dropProc N'p_XMLDiscTemplate_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLDiscTemplate_01$change
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    @ai_level1_s            OBJECTIDSTRING,
    @ai_level2_s            OBJECTIDSTRING,
    @ai_level3_s            OBJECTIDSTRING
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_retValue             ID,             -- reutrn value of this procedure
    @l_oid                  OBJECTID,       -- oid of the object
    @l_level1               OBJECTID,       -- conterted input parameter
                                            -- @ai_level1_s
    @l_level2               OBJECTID,       -- conterted input parameter
                                            -- @ai_level2_s
    @l_level3               OBJECTID        -- conterted input parameter
                                            -- @ai_level3_s

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID,
    @l_level1               = @c_NOOID,
    @l_level2               = @c_NOOID,
    @l_level3               = @c_NOOID


    -- body:
    BEGIN TRANSACTION
        -- convert ids:
        EXEC p_stringToByte @ai_level1_s, @l_level1 OUTPUT
        EXEC p_stringToByte @ai_level2_s, @l_level2 OUTPUT
        EXEC p_stringToByte @ai_level3_s, @l_level3 OUTPUT

        -- perform the change of the object:
        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, @ai_op,
                @ai_name, @ai_validUntil, @ai_description, @ai_showInNews,
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object changed successfully?
        BEGIN
            UPDATE  m2_XMLDiscussionTemplate_01
            SET     level1 = @l_level1,
                    level2 = @l_level2,
                    level3 = @l_level3
            WHERE   oid = @l_oid
        END -- if object changed successfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
-- p_XMLDiscussionTemplate_01$change
GO


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s                ID of the object to be changed.
 * @param   ai_userId               ID of the user who is creating the object.
 * @param   ai_op                   Operation to be performed (used for rights 
 *                                  check).
 *
 * @output parameters:
 * @param   ao_state                The object's state.
 * @param   ao_tVersionId           ID of the object's type (correct version).
 * @param   ao_typeName             Name of the object's type.
 * @param   ao_name                 Name of the object itself.
 * @param   ao_containerId          ID of the object's container.
 * @param   ao_containerName        Name of the object's container.
 * @param   ao_containerKind        Kind of object/container relationship.
 * @param   ao_isLink               Is the object a link?
 * @param   ao_linkedObjectId       Link if isLink is true.
 * @param   ao_owner                ID of the owner of the object.
 * @param   ao_ownerName            Name of the owner of the object.
 * @param   ao_creationDate         Date when the object was created.
 * @param   ao_creator              ID of person who created the object.
 * @param   ao_creatorName          Name of person who created the object.
 * @param   ao_lastChanged          Date of the last change of the object.
 * @param   ao_changer              ID of person who did the last change to the
 *                                  object.
 * @param   ao_changerName          Name of person who did the last change to
 *                                  the object.
 * @param   ao_validUntil           Date until which the object is valid.
 * @param   ai_description          Description of the object.
 * @param   ao_showInNews           show in news flag.
 * @param   ao_checkedOut           Is the object checked out?
 * @param   ao_checkOutDate         Date when the object was checked out
 * @param   ao_checkOutUser         id of the user which checked out the object
 * @param   ao_checkOutUserOid      Oid of the user which checked out the object
 *                                  is only set if this user has the right to
 *                                  READ the checkOut user
 * @param   ao_checkOutUserName     name of the user which checked out the
 *                                  object, is only set if this user has the
 *                                  right to view the checkOut-User
 * @param   ao_level1               oid of the first level template
 * @param   ao_level1Name           Name of the first level template
 * @param   ao_level2               oid of the second level template
 * @param   ao_level2Name           Name of the second level template
 * @param   ao_level3               oid of the third level template
 * @param   ao_level3Name           Name of the third level template
 * @param   ao_numberOfReferences   The number of domains where this domain scheme
 *                                  is used.
 *
 * @returns A value representing the state of the procedure.
 *  c_ALL_RIGHT                     Action performed, values returned,
 *                                  everything ok.
 *  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 *  c_OBJECTNOTFOUND                The required object was not found within
 *                                  the database.
 */

-- delete existing procedure: 
EXEC p_dropProc N'p_XMLDiscTemplate_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLDiscTemplate_01$retrieve
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
    @ao_level1              OBJECTID        OUTPUT,
    @ao_level1Name          NAME            OUTPUT,
    @ao_level2              OBJECTID        OUTPUT,
    @ao_level2Name          NAME            OUTPUT,
    @ao_level3              OBJECTID        OUTPUT,
    @ao_level3Name          NAME            OUTPUT,
    @ao_numberOfReferences  INT             OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_retValue             ID,             -- reutrn value of this procedure
    @l_oid                  OBJECTID        -- oid of the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID


    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
        EXEC @l_retValue = p_Object$performRetrieve @ai_oid_s, @ai_userId,
                @ai_op, @ao_state OUTPUT, @ao_tVersionId OUTPUT,
                @ao_typeName OUTPUT, @ao_name OUTPUT, @ao_containerId OUTPUT,
                @ao_containerName OUTPUT, @ao_containerKind OUTPUT,
                @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT, @ao_owner OUTPUT,
                @ao_ownerName OUTPUT, @ao_creationDate OUTPUT,
                @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT,
                @ao_changerName OUTPUT, @ao_validUntil OUTPUT,
                @ao_description OUTPUT, @ao_showInNews OUTPUT,
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT,
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT,
                @ao_checkOutUserName OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object changed successfully?
        BEGIN       
            -- retrieve the 3 oids of the levels
            SELECT  @ao_level1 = level1,
                    @ao_level2 = level2,
                    @ao_level3 = level3
            FROM    m2_XMLDiscussionTemplate_01
            WHERE   oid = @l_oid

            -- check if retrieve was performed properly:
            IF (@@ROWCOUNT > 0)         -- everything o.k.?
            BEGIN
                -- retrieve the 3 names of the levels
                SELECT  @ao_level1Name = name
                FROM    ibs_Object
                WHERE   oid = @ao_level1

                -- retrieve the 3 names of the levels
                SELECT  @ao_level2Name = name
                FROM    ibs_Object
                WHERE   oid = @ao_level2

                -- retrieve the 3 names of the levels
                SELECT  @ao_level3Name = name
                FROM    ibs_Object
                WHERE   oid = @ao_level3

                -- get the number of objects where this template is used:
                SELECT  @ao_numberOfReferences = COUNT (*)
                FROM    m2_Discussion_01 d, ibs_Object o
                WHERE   d.oid = o.oid
                    AND o.state = 2
                    AND d.refOid = @l_oid
            END
            ELSE                        -- no row affected
                SELECT  @l_retValue = @c_NOT_OK -- set return value
        END -- if object changed successfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue

GO
-- p_XMLDiscTemplate_01$retrieve



/******************************************************************************
 * Deletes a XMLDisctemplate_01 object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   ai_oid_s                ID of the object to be deleted.
 * @param   ai_userId               ID of the user who is deleting the object.
 * @param   ai_op                   Operation to be performed (used for rights 
 *                                  check).
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  c_ALL_RIGHT                     Action performed, values returned,
 *                                  everything ok.
 *  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 *  c_OBJECTNOTFOUND                The required object was not found within
 *                                  the database.
 *  c_DEPENDENT_OBJECT_EXISTS       When there are still objects which depend on
 *                                  this template
 */

-- delete existing procedure: 
EXEC p_dropProc N'p_XMLDiscTemplate_01$delete'
GO

CREATE PROCEDURE p_XMLDiscTemplate_01$delete
(
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_DEPENDENT_OBJECT_EXISTS INT,         -- value of the exception

    -- local variables:
    @l_retValue             ID,             -- reutrn value of this procedure
    @l_oid                  OBJECTID,       -- converted input parameter
                                            -- @ai_oid_s
    @l_containerId          OBJECTID,       -- oid of the container
    @l_count                INT             -- counter for select

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000,
    @c_DEPENDENT_OBJECT_EXISTS = 61

    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID,
    @l_containerId          = @c_NOOID,
    @l_count                = 0


    -- body:
    BEGIN TRANSACTION
        -- convertions (objectidstring) - all input objectids must be converted
        EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

        -- check if there are still objects which depends on this template
        SELECT  @l_count = COUNT(*)
        FROM    m2_Discussion_01 d
        JOIN    ibs_object o ON d.refOid = @l_oid
         AND    d.oid = o.oid
         AND    state = 2

        IF (@l_count < 1)                   -- are there objects referenceing on
                                            -- this template ?
        BEGIN
            -- delete base object:
            -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
            -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
            EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId,
                    @ai_op, @l_oid OUTPUT
        END -- if are there objects referenceing on this template
        ELSE
        BEGIN
            -- there are objects which referencing on this template
            SELECT @l_retValue = @c_DEPENDENT_OBJECT_EXISTS
        END
/*
        IF (@l_retValue = @c_ALL_RIGHT)     -- operation properly performed ?
        BEGIN
            -- normally delete the type specific tuples
            DELETE  m2_XMLDiscussionTemplate_01
            WHERE   oid = @l_oid
        END -- if operation properly performed
*/  
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_XMLDiscTemplate_01$delete


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s                ID of the object to be deleted.
 * @param   ai_userId               ID of the user who is deleting the object.
 * @param   ai_op                   Operation to be performed (used for rights
 *                                  check).
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  c_ALL_RIGHT                     Action performed, values returned,
 *                                  everything ok.
 *  c_NOT_OK                        Something went wrong.
 */

-- delete existing procedure: 
EXEC p_dropProc N'p_XMLDiscTemplate_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLDiscTemplate_01$BOCopy
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
    @c_NOOID                OBJECTID,       -- oid of no valid object

    -- local variables:
    @l_retValue             ID,             -- reutrn value of this procedure
    @l_oid                  OBJECTID        -- converted input parameter
                                            -- @ai_oid_s

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_NOT_OK,
    @l_oid                  = @c_NOOID


    -- body:
    -- make an insert for all type specific tables:
    INSERT INTO m2_XMLDiscussionTemplate_01 
            (oid, level1, level2, level3)
    SELECT  @ai_newOid, level1, level2, level3
    FROM    m2_XMLDiscussionTemplate_01 b
    WHERE   b.oid = @ai_oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                    -- at least one row affected?
        SELECT  @l_retValue = @c_ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_XMLDiscTemplate_01$BOCopy
