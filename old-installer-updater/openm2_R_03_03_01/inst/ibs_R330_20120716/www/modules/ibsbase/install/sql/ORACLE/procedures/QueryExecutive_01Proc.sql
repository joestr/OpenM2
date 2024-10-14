/******************************************************************************
 * All stored procedures regarding to the QueryExecutive for dynamic 
 * Search - Queries. <BR>
 *
 * @version     1.10.0001, 18.09.2000
 *
 * @author      Jansa Andreas (AJ)  000918
 *
 * <DT><B>Updates:</B>
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
 * @param   ao_oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_QueryExecutive_01$create
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
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- operation was o.k.
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                            -- oid of no valid object
    -- local valriables    
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, 
                        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
                        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        BEGIN
            -- create object type specific data:
            INSERT INTO ibs_QueryExecutive_01 
                        (oid, reportTemplateOid, searchValues, 
                            matchTypes, rootObjectOid)
            VALUES      (l_oid, c_NOOID, NULL, 
                            NULL, c_NOOID);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                                        'p_QueryExecutive_01$create',
                                        'Error in INSERT INTO');
            RAISE;
        END;
    END IF; -- if object created successfully

    COMMIT WORK;
    -- return the state value
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_QueryExecutive_01$create',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_QueryExecutive_01$create;
/

show errors;
-- p_QueryExecutive_01$create



/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 *
 *
 * @param   ai_queryString        desc
 * @param   ai_columnHeaders      desc
 * @param   ai_queryAttributes    desc
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_QueryExecutive_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           NUMBER,
    ai_reportTemplateOid_s  VARCHAR2,
    ai_searchValues         VARCHAR2,
    ai_matchTypes           VARCHAR2,
    ai_rootObjectOid_s      VARCHAR2
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- operation was o.k.
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                        -- oid of no valid object
    -- local valriables    
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;
    l_repTempOid            RAW (8) := c_NOOID;
    l_rootObjectOid         RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- convert oidString to oid
    p_stringToByte (ai_oid_s, l_oid);
    p_stringToByte (ai_reportTemplateOid_s, l_repTempOid);
    p_stringToByte (ai_rootObjectOid_s, l_rootObjectOid);
    
    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
            ai_validUntil, ai_description, ai_showInNews, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        BEGIN
            UPDATE  ibs_QueryExecutive_01
            SET     reportTemplateOid = l_repTempOid,
                    searchValues = ai_searchValues,
                    matchTypes = ai_matchTypes,
                    rootObjectOid = l_rootObjectOid
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                                        'p_QueryExecutive_01$change',
                                        'Error in UPDATE');
            RAISE;
        END;
    END IF; -- if object created successfully

    COMMIT WORK;
    -- return the state value
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_QueryExecutive_01$change',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_QueryExecutive_01$change;
/

show errors;
-- p_QueryExecutive_01$change



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
 * @param   ao_state               The object's state.
 * @param   ao_tVersionId          ID of the object's type (correct version).
 * @param   ao_typeName            Name of the object's type.
 * @param   ao_name                Name of the object itself.
 * @param   ao_containerId         ID of the object's container.
 * @param   ao_containerName       Name of the object's container.
 * @param   ao_containerKind       Kind of object/container relationship.
 * @param   ao_isLink              Is the object a link?
 * @param   ao_linkedObjectId      Link if isLink is true.
 * @param   ao_owner               ID of the owner of the object.
 * @param   ao_creationDate        Date when the object was created.
 * @param   ao_creator             ID of person who created the object.
 * @param   ao_lastChanged         Date of the last change of the object.
 * @param   ao_changer             ID of person who did the last change to the
 *                                 object.
 * @param   ao_validUntil          Date until which the object is valid.
 * @param   ao_description         Description of the object.
 * @param   ao_showInNews          flag if object should be shown in newscontainer
 * @param   ao_checkedOut          Is the object checked out?
 * @param   ao_checkOutDate        Date when the object was checked out
 * @param   ao_checkOutUser        id of the user which checked out the object
 * @param   ao_checkOutUserOid     Oid of the user which checked out the object
 *                                 is only set if this user has the right to READ
 *                                 the checkOut user
 * @param   ao_checkOutUserName    name of the user which checked out the object,
 *                                 is only set if this user has the right to view
 *                                 the checkOut-User
 *
 * @param   ao_repTempOid      desc
 * @param   ao_searchValues    desc
 * @param   ao_matchTypes  desc
 * @param   ao_rootObjectOid  desc
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_QueryExecutive_01$retrieve
(
    -- common input parameters:
    ai_oid_s                   VARCHAR2,
    ai_userId                  INTEGER,
    ai_op                      INTEGER,
    -- common output parameters:
    ao_state                   OUT INTEGER,
    ao_tVersionId              OUT INTEGER,
    ao_typeName                OUT VARCHAR2,
    ao_name                    OUT VARCHAR2,
    ao_containerId             OUT RAW,
    ao_containerName           OUT VARCHAR2,
    ao_containerKind           OUT INTEGER,
    ao_isLink                  OUT NUMBER,
    ao_linkedObjectId          OUT RAW,
    ao_owner                   OUT INTEGER,
    ao_ownerName               OUT VARCHAR2,
    ao_creationDate            OUT DATE,
    ao_creator                 OUT INTEGER,
    ao_creatorName             OUT VARCHAR2,
    ao_lastChanged             OUT DATE,
    ao_changer                 OUT INTEGER,
    ao_changerName             OUT VARCHAR2,
    ao_validUntil              OUT DATE,
    ao_description             OUT VARCHAR2,
    ao_showInNews              OUT NUMBER,
    ao_checkedOut              OUT NUMBER,
    ao_checkOutDate            OUT DATE,
    ao_checkOutUser            OUT INTEGER,
    ao_checkOutUserOid         OUT RAW,
    ao_checkOutUserName        OUT VARCHAR2,

    -- type-specific output attributes:
    ao_repTempOid              OUT RAW,
    ao_searchValues            OUT VARCHAR2,
    ao_matchTypes              OUT VARCHAR2,
    ao_rootObjectOid           OUT RAW
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- operation was o.k.
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                        -- oid of no valid object
    -- local valriables    
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op, ao_state ,
            ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName, 
            ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName, 
            ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged, ao_changer,
            ao_changerName, ao_validUntil, ao_description, ao_showInNews, ao_checkedOut,
            ao_checkOutDate, ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
            l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        BEGIN
            SELECT  reportTemplateOid, searchValues,
                    matchTypes, rootObjectOid 
            INTO    ao_repTempOid, ao_searchValues,
                    ao_matchTypes, ao_rootObjectOid
            FROM    ibs_QueryExecutive_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                                        'p_QueryExecutive_01$retrieve',
                                        'Error in UPDATE');
            RAISE;
        END;

    END IF; -- if object created successfully

    COMMIT WORK;
    -- return the state value
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_QueryExecutive_01$retrieve',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_QueryExecutive_01$retrieve;
/

show errors;
-- p_QueryExecutive_01$retrieve


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
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

/*
p_dropProc "p_QueryExecutive_01$delete"


CREATE OR REPLACE FUNCTION p_QueryExecutive_01$delete
(
    -- common input parameters:
    ai_oid_s                VARCHAR,
    ai_userId               INTEGER,
    ai_op                   INTEGER
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- operation was o.k.
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                        -- oid of no valid object
    -- local valriables    
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure

    -- body:
BEGIN
    COMMIT WORK;
    -- return the state value
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_QueryExecutive_01$delete',
            'Input: ai_masterId = ' || ai_masterId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_QueryExecutive_01$delete;
/

show errors;
-- p_QueryExecutive_01$delete
*/


/******************************************************************************
 * Copy an object and all its values. <BR>
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


CREATE OR REPLACE FUNCTION p_QueryExecutive_01$BOCopy
(
    -- common input parameters:
    ai_oid        RAW ,
    ai_userId     INTEGER ,
    ai_newOid     RAW 
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- operation was o.k.
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                        -- oid of no valid object
    -- local valriables    
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    -- body:
BEGIN
    BEGIN
        INSERT INTO ibs_QueryExecutive_01 
                    (oid, reportTemplateOid, searchValues, matchTypes, 
                    rootObjectOid)
        SELECT      ai_newOid, reportTemplateOid, searchValues, matchTypes, 
                    rootObjectOid
        FROM        ibs_QueryExecutive_01 
        WHERE       oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_QueryExecutive_01$BOCopy',
        'Insert INTO');

    END;

    IF  (SQL%ROWCOUNT >= 1) THEN
        l_retValue :=  c_ALL_RIGHT;
    END IF;

    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_QueryExecutive_01$BOCopy',
    ', userId = ' || ai_userId  ||
    ', newOid = ' || ai_newOid ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_QueryExecutive_01$BOCopy;
/

show errors;
-- p_QueryExecutive_01$BOCopy
/

EXIT;