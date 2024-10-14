/******************************************************************************
 * All stored procedures regarding to the DBQueryCreator_01 for dynamic
 * search queries on databases. <BR>
 *
 * @version     2.21.0001, 020604 KR
 *
 * @author      Klaus Reimüller (KR)  020604
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
CREATE OR REPLACE FUNCTION p_DBQueryCreator_01$create
(
    -- common input parameters:
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_tVersionId           ibs_Object.tVersionId%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        ibs_Object.containerKind%TYPE,
    ai_isLink               ibs_Object.isLink%TYPE,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          ibs_Object.description%TYPE,
    -- common output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_containerId           ibs_Object.containerId%TYPE;
                                            -- oid of the container object
    l_linkedObjectId        ibs_Object.linkedObjectId%TYPE;
                                            -- oid of the linked object
    l_oid                   ibs_Object.oid%TYPE := c_NOOID;

-- body:
BEGIN
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- BEGIN TRANSACTION                   -- begin new TRANSACTION
        -- create base object:
        l_retValue := p_QueryCreator_01$create (ai_userId, ai_op, ai_tVersionId,
                            ai_name, ai_containerId_s, ai_containerKind,
                            ai_isLink, ai_linkedObjectId_s, ai_description,
                            ao_oid_s);

        IF (l_retValue = c_ALL_RIGHT)   -- object created successfully?
        THEN
            -- convert the oid:
            p_stringToByte (ao_oid_s, l_oid);
            BEGIN
                -- create object type specific data:
                INSERT INTO ibs_DBQueryCreator_01 (oid, connectorOid)
                VALUES  (l_oid, c_NOOID);
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'INSERT data';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- if object created successfully

    -- finish the transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId || ', ' ||
            ', ai_op = ' || ai_op || ', ' ||
            ', ai_tVersionId = ' || ai_tVersionId || ', ' ||
            ', ai_name = ' || ai_name || ', ' ||
            ', ai_containerId_s = ' || ai_containerId_s || ', ' ||
            ', ai_containerKind = ' || ai_containerKind || ', ' ||
            ', ai_isLink = ' || ai_isLink ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s ||
            ', ai_description = ' || ai_description ||
            ', ao_oid_s = ' || ao_oid_s ||
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_DBQueryCreator_01$create', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_DBQueryCreator_01$create;
/

show errors;


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
 * @param   ai_queryType        Type of the query.
 * @param   ai_groupByString    GROUP BY clause of query.
 * @param   ai_orderByString    ORDER BY clause of query.
 * @param   ai_resultCounter    Number of elements to be shown.
 * @param   ai_enableDebug      Is debugging enabled for this query?
 * @param   ai_connectorOid_s   The oid of the database connector.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DBQueryCreator_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_validUntil           ibs_Object.validUntil%TYPE,
    ai_description          ibs_Object.description%TYPE,
    ai_showInNews           ibs_Object.flags%TYPE,
    -- QueryCreator-specific input parameters:
    ai_queryType            ibs_QueryCreator_01.queryType%TYPE,
    ai_groupByString        ibs_QueryCreator_01.groupByString%TYPE,
    ai_orderByString        ibs_QueryCreator_01.orderByString%TYPE,
    ai_resultCounter        ibs_QueryCreator_01.resultCounter%TYPE,
    ai_enableDebug          ibs_QueryCreator_01.enableDebug%TYPE,
    -- DBQueryCreator-specific input parameters:
    ai_connectorOid_s       VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   ibs_Object.oid%TYPE := c_NOOID;
                                            -- the oid of the object
    l_connectorOid          ibs_DBQueryCreator_01.connectorOid%TYPE;
                                            -- the oid of the connector

-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input object ids must be converted
    p_stringToByte (ai_oid_s, l_oid);
    p_stringToByte (ai_connectorOid_s, l_connectorOid);

--    BEGIN TRANSACTION                   -- begin new TRANSACTION
        -- perform the change of the object:
        l_retValue := p_QueryCreator_01$change (ai_oid_s, ai_userId,
                ai_op, ai_name, ai_validUntil, ai_description,
                ai_showInNews,
                ai_queryType, ai_groupByString, ai_orderByString,
                ai_resultCounter, ai_enableDebug);

        IF (l_retValue = c_ALL_RIGHT)   -- operation performed successfully?
        THEN
            BEGIN
                -- update further information:
    	        UPDATE  ibs_DBQueryCreator_01
    	        SET     connectorOid = l_connectorOid
    	        WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'UPDATE data';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- if operation performed successfully
    -- finish the transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_name = ' || ai_name ||
            ', ai_validUntil = ' || ai_validUntil ||
            ', ai_description = ' || ai_description ||
            ', ai_showInNews = ' || ai_showInNews ||
            ', ai_queryType = ' || ai_queryType ||
            ', ai_groupByString = ' || ai_groupByString ||
            ', ai_orderByString = ' || ai_orderByString ||
            ', ai_resultCounter = ' || ai_resultCounter ||
            ', ai_enableDebug = ' || ai_enableDebug ||
            ', ai_connectorOid_s = ' || ai_connectorOid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_DBQueryCreator_01$change',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_DBQueryCreator_01$change;
/

show errors;


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
 * @param   ao_queryType        The query type.
 * @param   ao_groupByString    GROUP BY clause of query.
 * @param   ao_orderByString    ORDER BY clause of query.
 * @param   ao_resultCounter    Number of elements to be shown.
 * @param   ao_enableDebug      Is debugging enabled?
 * @param   ao_connectorOid     The oid of the database connector.
 *
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_DBQueryCreator_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    -- output parameters:
    ao_state                OUT ibs_Object.state%TYPE,
    ao_tVersionId           OUT ibs_Object.tVersionId%TYPE,
    ao_typeName             OUT ibs_Object.typeName%TYPE,
    ao_name                 OUT ibs_Object.name%TYPE,
    ao_containerId          OUT ibs_Object.containerId%TYPE,
    ao_containerName        OUT ibs_Object.name%TYPE,
    ao_containerKind        OUT ibs_Object.containerKind%TYPE,
    ao_isLink               OUT ibs_Object.isLink%TYPE,
    ao_linkedObjectId       OUT ibs_Object.linkedObjectId%TYPE,
    ao_owner                OUT ibs_Object.owner%TYPE,
    ao_ownerName            OUT ibs_user.fullname%TYPE,
    ao_creationDate         OUT ibs_Object.creationDate%TYPE,
    ao_creator              OUT ibs_Object.creator%TYPE,
    ao_creatorName          OUT ibs_User.fullname%TYPE,
    ao_lastChanged          OUT ibs_Object.lastChanged%TYPE,
    ao_changer              OUT ibs_Object.changer%TYPE,
    ao_changerName          OUT ibs_User.fullname%TYPE,
    ao_validUntil           OUT ibs_Object.validUntil%TYPE,
    ao_description          OUT ibs_Object.description%TYPE,
    ao_showInNews           OUT ibs_Object.flags%TYPE,
    ao_checkedOut           OUT ibs_Object.flags%TYPE,
    ao_checkOutDate         OUT ibs_CheckOut_01.checkout%TYPE,
    ao_checkOutUser         OUT ibs_CheckOut_01.userId%TYPE,
    ao_checkOutUserOid      OUT ibs_User.oid%TYPE,
    ao_checkOutUserName     OUT ibs_User.name%TYPE,
    -- QueryCreator-specific output parameters:
    ao_queryType            OUT ibs_QueryCreator_01.queryType%TYPE,
    ao_groupByString        OUT ibs_QueryCreator_01.groupByString%TYPE,
    ao_orderByString        OUT ibs_QueryCreator_01.orderByString%TYPE,
    ao_resultCounter        OUT ibs_QueryCreator_01.resultCounter%TYPE,
    ao_enableDebug          OUT ibs_QueryCreator_01.enableDebug%TYPE,
    -- DBQueryCreator-specific output parameters:
    ao_connectorOid         OUT ibs_DBQueryCreator_01.connectorOid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_oid                   ibs_Object.oid%TYPE := c_NOOID;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

--    BEGIN TRANSACTION                   -- begin new TRANSACTION
        -- retrieve the QueryCreator data:
        l_retValue := p_QueryCreator_01$retrieve (
                ai_oid_s, ai_userId, ai_op,
                ao_state, ao_tVersionId, ao_typeName, ao_name,
                ao_containerId, ao_containerName,
                ao_containerKind, ao_isLink,
                ao_linkedObjectId, ao_owner,
                ao_ownerName, ao_creationDate,
                ao_creator, ao_creatorName,
                ao_lastChanged, ao_changer,
                ao_changerName, ao_validUntil,
                ao_description, ao_showInNews,
                ao_checkedOut, ao_checkOutDate,
                ao_checkOutUser, ao_checkOutUserOid,
                ao_checkOutUserName,
                ao_queryType, ao_groupByString,
                ao_orderByString, ao_resultCounter,
                ao_enableDebug);

        IF (l_retValue = c_ALL_RIGHT)   -- operation performed successfully?
        THEN
            BEGIN
                -- retrieve the type specific data:
                SELECT  connectorOid
                INTO    ao_connectorOid
    	        FROM    ibs_DBQueryCreator_01
    	        WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'SELECT data';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- if operation performed successfully
    -- finish the transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_DBQueryCreator_01$retrieve',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_DBQueryCreator_01$retrieve;
/

show errors;


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
CREATE OR REPLACE FUNCTION p_DBQueryCreator_01$BOCopy
(
    -- common input parameters:
    ai_oid                  ibs_Object.oid%TYPE,
    ai_userId               ibs_User.id%TYPE,
    ai_newOid               ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text

-- body:
BEGIN
--    BEGIN TRANSACTION                   -- begin new TRANSACTION
    -- set a save point for the current transaction:
    SAVEPOINT s_DBQueryCreator_01$BOCopy;

        -- copy base object:
        l_retValue :=
            p_QueryCreator_01$BOCopy (ai_oid, ai_userId, ai_newOid);

        IF (l_retValue = c_ALL_RIGHT)   -- operation properly performed?
        THEN
            BEGIN
                -- make an insert for all type specific tables:
                -- (it's currently not possible to copy files!)
                INSERT INTO ibs_DBQueryCreator_01
                        (oid, connectorOid)
                SELECT  ai_newOid, connectorOid
                FROM    ibs_DBQueryCreator_01
                WHERE   oid = ai_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'insert new DBQueryCreator data';
                    RAISE;              -- call common exception handler
            END;
	    END IF; -- if operation properly performed

    -- check if there occurred an error:
    IF (l_retValue <> c_ALL_RIGHT)      -- an error occured
    THEN
        -- roll back to the save point:
        ROLLBACK TO s_DBQueryCreator_01$BOCopy; -- undo changes
    END IF; -- if an error occurred

    -- finish the transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_DBQueryCreator_01$BOCopy; -- undo changes
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_newOid = ' || ai_newOid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_DBQueryCreator_01$BOCopy',l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- finish the transaction and set new transaction starting point:
        COMMIT WORK;                        -- make changes permanent
        -- return error code:
        RETURN l_retValue;
END p_DBQueryCreator_01$BOCopy;
/

show errors;

EXIT;
