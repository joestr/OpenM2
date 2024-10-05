/******************************************************************************
 * All stored procedures regarding the XMLDiscussionTemplate_01 Object. <BR>
 *
 * @version     $Id: XMLDiscussionTemplateProc.sql,v 1.4 2003/10/31 00:13:16 klaus Exp $
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

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscTemplate_01$create
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- parameters:
    ai_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- definitions:
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_noOid                 RAW (8) := c_NOOID;

    -- body:
BEGIN
        -- create base object:
        -- p_Object$performCreate will set l_retValue to c_ALL_RIGHT,
        -- c_INSUFFICIENT_RIGHTS or c_NOT_OK
        l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, 
                       ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
                       ai_linkedObjectId_s, ai_description, ai_oid_s, l_oid);

        IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
        THEN
            BEGIN
                -- insert the other values
                INSERT INTO m2_XMLDiscussionTemplate_01 
                    (oid, level1, level2, level3)
                VALUES
                    (l_oid, l_noOid, l_noOid, l_noOid);
            EXCEPTION
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error, 
                                         'p_XMLDiscussionTemplate_01$create',
                                         'Error in INSERT INTO');
                RAISE;
            END;
        END IF; -- if object created successfully
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,
            'p_XMLDiscussionTemplate_01$create',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscTemplate_01$create;
/

show errors;

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

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscTemplate_01$change
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           NUMBER,
    ai_level1_s             VARCHAR2,
    ai_level2_s             VARCHAR2,
    ai_level3_s             VARCHAR2
)
RETURN INTEGER
AS
    -- definitions:
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_level1                RAW (8) := c_NOOID;
    l_level2                RAW (8) := c_NOOID;
    l_level3                RAW (8) := c_NOOID;
    
    -- body:
BEGIN
    -- convert ids:
    p_stringToByte (ai_level1_s, l_level1);
    p_stringToByte (ai_level2_s, l_level2);
    p_stringToByte (ai_level3_s, l_level3);
    
    -- perform the change of the object:
    -- p_Object$performCreate will set l_retValue to c_ALL_RIGHT,
    -- c_INSUFFICIENT_RIGHTS or c_OBJECTNOTFOUND
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
                    ai_validUntil, ai_description, ai_showInNews, l_oid);

    IF (l_retValue = c_ALL_RIGHT)           -- object changed successfully?
    THEN
        BEGIN
            UPDATE  m2_XMLDiscussionTemplate_01
               SET  level1 = l_level1,
                    level2 = l_level2,
                    level3 = l_level3
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_XMLDiscussionTemplate_01$change',
                                     'Error in INSERT INTO');
            RAISE;
        END;
    END IF; -- if object changed successfully
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,
            'p_XMLDiscussionTemplate_01$change',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscTemplate_01$change;
/

show errors;
-- p_XMLDiscussionTemplate_01$change


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
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscTemplate_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- parameters
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
    ao_level1               OUT RAW,
    ao_level1Name           OUT VARCHAR2,
    ao_level2               OUT RAW,
    ao_level2Name           OUT VARCHAR2,
    ao_level3               OUT RAW,
    ao_level3Name           OUT VARCHAR2,
    ao_numberOfReferences   OUT INTEGER
)
RETURN INTEGER
AS
    -- definitions:
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;

    -- body:
BEGIN
    -- retrieve the base object data:
    -- p_Object$performCreate will set l_retValue to c_ALL_RIGHT,
    -- c_INSUFFICIENT_RIGHTS or c_OBJECTNOTFOUND
    l_retValue := p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op, ao_state,
            ao_tVersionId, ao_typeName, ao_name, ao_containerId, 
            ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
            ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
            ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate, 
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            -- retrieve the 3 oids of the levels
            SELECT  level1, level2, level3
            INTO    ao_level1, ao_level2, ao_level3
            FROM    m2_XMLDiscussionTemplate_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_XMLDiscussionTemplate_01$retrieve',
                                     'Error in SELECT x, x, x');
            RAISE;
        END;

        -- check if retrieve was performed properly:
        IF (SQL%ROWCOUNT > 0)       -- everything o.k.?
        THEN
            BEGIN
                -- retrieve the 3 names of the levels
                SELECT  name
                INTO    ao_level1Name
                FROM    ibs_Object
                WHERE   oid = ao_level1;
            EXCEPTION
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error, 
                                         'p_XMLDiscussionTemplate_01$retrieve',
                                         'Error in first SELECT name');
                RAISE;
            END;

            BEGIN
                -- retrieve the 3 names of the levels
                SELECT  name
                INTO    ao_level2Name
                FROM    ibs_Object
                WHERE   oid = ao_level2;
            EXCEPTION
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error, 
                                         'p_XMLDiscussionTemplate_01$retrieve',
                                         'Error in second SELECT name');
                RAISE;
            END;

            BEGIN
                -- retrieve the 3 names of the levels
                SELECT  name
                INTO    ao_level3Name
                FROM    ibs_Object
                WHERE   oid = ao_level3;
            EXCEPTION
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error, 
                                         'p_XMLDiscussionTemplate_01$retrieve',
                                         'Error in third SELECT name');
                RAISE;
            END;

            BEGIN
                -- get the number of objects where this template is used:
                SELECT  COUNT (*)
                INTO    ao_numberOfReferences
                FROM    m2_Discussion_01 d, ibs_Object o
                WHERE   d.oid = o.oid
                    AND o.state = 2
                    AND d.refOid = l_oid;
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
    END IF; -- end if
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_XMLDiscussionTemplate_01$retrieve',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscTemplate_01$retrieve;
/

show errors;
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

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscTemplate_01$delete
(
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_DEPENDENT_OBJECT_EXISTS CONSTANT INTEGER := 61;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_count                 INTEGER;

    -- body:
BEGIN
    -- convertions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- check if there are still objects which depends on this template
        SELECT  COUNT(*)
        INTO    l_count
        FROM    m2_Discussion_01 d, ibs_object o 
        WHERE   d.refOid = l_oid
          AND   d.oid = o.oid
          AND   state = 2;
    EXCEPTION
        WHEN OTHERS THEN
           ibs_error.log_error ( ibs_error.error, 
                                 'p_XMLDiscussionTemplate_01$change',
                                 'Error in SELECT COUNT(*)');
        RAISE;
    END;

    IF (l_count < 1)                        -- are there objects referenceing on
                                            -- this template ?
    THEN
        -- delete base object:
        -- p_Object$performCreate will set l_retValue to c_ALL_RIGHT,
        -- c_INSUFFICIENT_RIGHTS or c_OBJECTNOTFOUND
        l_retValue := p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);
    ELSE
        -- there are objects which referencing on this template
        l_retValue := c_DEPENDENT_OBJECT_EXISTS;
    END IF; -- if are there objects referenceing on this template
/*
    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        BEGIN
            -- normally delete the type specific tuples
            DELETE  m2_XMLDiscussionTemplate_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_XMLDiscussionTemplate_01$delete',
                                     'Error in DELETE');
        END;

    END IF; -- if operation properly performed
*/
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_XMLDiscussionTemplate_01$delete',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscTemplate_01$delete;
/

show errors;
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
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscTemplate_01$BOCopy
(
    -- common input parameters:
    ai_oid            RAW,
    ai_userId         INTEGER,
    ai_newOid         RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;

    -- body:
BEGIN
    BEGIN
        -- make an insert for all type specific tables:
        INSERT INTO m2_XMLDiscussionTemplate_01 
                (oid, level1, level2, level3)
        SELECT  ai_newOid, level1, level2, level3
        FROM    m2_XMLDiscussionTemplate_01 b
        WHERE   b.oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
           l_retValue := c_NOT_OK;          -- set return value
           ibs_error.log_error ( ibs_error.error, 
                                 'p_XMLDiscussionTemplate_01$BOCopy',
                                 'Error in INSERT');
        RAISE;
     END;
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_XMLDiscussionTemplate_01$BOCopy',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscTemplate_01$BOCopy;
/

show errors;

-- p_XMLDiscTemplate_01$BOCopy

EXIT;