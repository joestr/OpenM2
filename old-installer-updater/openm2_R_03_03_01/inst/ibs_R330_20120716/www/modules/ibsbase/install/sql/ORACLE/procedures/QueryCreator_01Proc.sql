/******************************************************************************
 * All stored procedures regarding to the QueryCreator_01 for dynamic
 * Search - Queries. <BR>
 *
 * @version     2.2.1.0001, 05.10.2001
 *
 * @author      Monika Eisenkolb (ME)  051001
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId             ID of the user who is creating the object.
 * @param   ai_op                 Operation to be performed (used for rights
 *                                check).
 * @param   ai_tVersionId         Type of the new object.
 * @param   ai_name               Name of the object.
 * @param   ai_containerId_s      ID of the container where object shall be
 *                                created in.
 * @param   ai_containerKind      Kind of object/container relationship
 * @param   ai_isLink             Defines if the object is a link
 * @param   ai_linkedObjectId_s   If the object is a link this is the ID of the
 *                                where the link shows to.
 * @param   ai_description        Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_QueryCreator_01$create
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
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local valriables
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                        ai_name, ai_containerId_s, ai_containerKind,
                        ai_isLink, ai_linkedObjectId_s, ai_description,
                        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        BEGIN
            -- create object type specific data:
            INSERT INTO ibs_QueryCreator_01 (oid, queryType, selectString, fromString,
                        whereString, groupByString, orderByString,
                        columnHeaders, queryAttrTypesForHeaders,
                        queryAttrForHeaders, searchFieldTokens,
                        queryAttrForFields, queryAttrTypesForFields, resultCounter, 
                        enableDebug, category)
            VALUES      (l_oid, 1, ' ',  ' ',
                        ' ', NULL, NULL,
                        ' ', ' ',
                        ' ', ' ',
                        ' ', ' ', -1, 0, ' ');
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,
                                      'p_QueryCreator_01$create',
                                      'Error in INSERT INTO');
            RAISE;
        END;
    END IF; -- if object created successfully

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_QueryCreator_01$create',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE ||
            ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_QueryCreator_01$create;
/

show errors;
-- p_QueryCreator_01$create



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
 * @param   ai_showInNews       flag if object should be shown in newscontainer
 *
 * @param   ai_queryType        integer for querytype
 * @param   ai_groupByString    string for group by clause
 * @param   ai_orderByString    string for order by clause
 * @param   ai_resultCounter    integer number of results to be shown
 * @param   ai_enableDebug      boolean if enable debug
 * @param   ai_category      	category for grouping
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_QueryCreator_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           NUMBER,
    -- typespecific input parameters
    ai_queryType            INTEGER,
    ai_groupByString        VARCHAR2,
    ai_orderByString        VARCHAR2,
    ai_resultCounter        INTEGER,
    ai_enableDebug	    	NUMBER,
    ai_category        		VARCHAR2

/*
    -- FOLLOWING TEXT-FIELDS ARE NOT POSIBBLE

    ai_selectString                     VARCHAR2,
    ai_fromString                       VARCHAR2,
    ai_whereString                      VARCHAR2,
    ai_columnHeaders                    VARCHAR2,
    ai_queryAttributesForHeaders        VARCHAR2,
    ai_queryAttributesTypesForHeaders   VARCHAR2,

    ai_searchFieldTokens                VARCHAR2,
    ai_queryAttributesForFields         VARCHAR2,
    ai_queryAttributesTypesForFields    VARCHAR2
*/
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local valriables
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- convert oidString to oid
    p_stringToByte (ai_oid_s, l_oid);

    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
            ai_validUntil, ai_description, ai_showInNews, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        BEGIN
            UPDATE  ibs_QueryCreator_01
            SET     queryType = ai_queryType,
                    groupByString = ai_groupByString,
                    orderByString = ai_orderByString,
                    resultCounter = ai_resultCounter,
		    		enableDebug = ai_enableDebug,
		    		category = ai_category
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,
                                      'p_QueryCreator_01$change',
                                      'Error in UPDATE');
            RAISE;
        END;
    END IF; -- if object created successfully

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_QueryCreator_01$change',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE ||
            ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_QueryCreator_01$change;
/

show errors;
-- p_QueryCreator_01$change



/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s               ID of the object to be retrieved.
 * @param   ai_userId              Id of the user who is getting the data.
 * @param   ai_op                  Operation to be performed (used for rights
 *                                 check).
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
 * @param   ao_queryType           integer for querytype
 * @param   ao_groupByString       string for group by clause
 * @param   ao_orderByString       string for order by clause
 * @param   ao_resultCounter       integer number of results to be shown
 * @param   ao_enableDebug         flag if debug should be shown
 * @param   ao_category            category for grouping
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_QueryCreator_01$retrieve
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
    ao_queryType               OUT INTEGER,
    ao_groupByString           OUT VARCHAR2,
    ao_orderByString           OUT VARCHAR2,
    ao_resultCounter           OUT INTEGER,
    ao_enableDebug             OUT NUMBER,
    ao_category                OUT VARCHAR2


/*
    -- FOLLOWING TEXT-FIELDS ARE NOT POSIBBLE

    ao_selectString                     OUT DESCRIPTION,
    ao_fromString                       OUT DESCRIPTION,
    ao_whereString                      OUT DESCRIPTION,
    ao_columnHeaders                    OUT DESCRIPTION,
    ao_queryAttributesForHeaders        OUT DESCRIPTION,
    ao_queryAttributesTypesForHeaders   OUT DESCRIPTION,
    ao_searchFieldTokens                OUT DESCRIPTION,
    ao_queryAttributesForFields         OUT DESCRIPTION,
    ao_queryAttributesTypesForFields    OUT DESCRIPTION

*/
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local valriables
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op, ao_state, ao_tVersionId,
            ao_typeName, ao_name, ao_containerId, ao_containerName, ao_containerKind, ao_isLink,
            ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName, ao_validUntil, ao_description,
            ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser, ao_checkOutUserOid,
            ao_checkOutUserName, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        BEGIN
            SELECT  queryType, groupByString, orderByString, 
            		resultCounter, enableDebug, category
            INTO    ao_queryType, ao_groupByString, ao_orderByString, 
            		ao_resultCounter, ao_enableDebug, ao_category 
            FROM    ibs_QueryCreator_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,
                                      'p_QueryCreator_01$retrieve',
                                      'Error in SELECT');
            RAISE;
        END;

    END IF; -- if object created successfully

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_QueryCreator_01$retrieve',
            'oid_s = ' || ai_oid_s ||
            'userId = ' || ai_userId ||
            'op = ' || ai_op ||
            '; sqlcode = ' || SQLCODE ||
            ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_QueryCreator_01$retrieve;
/

show errors;
-- p_QueryCreator_01$retrieve


CREATE OR REPLACE FUNCTION p_QueryCreator_01$BOCopy
(
    -- common input parameters:
    ai_oid        RAW,
    ai_userId     INTEGER,
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
        INSERT INTO ibs_QueryCreator_01 (oid, selectString, fromString,
                    whereString, queryType, groupByString, orderByString,
                    columnHeaders, queryAttrTypesForHeaders,
                    queryAttrForHeaders, searchFieldTokens,
                    queryAttrForFields, queryAttrTypesForFields, 
                    resultCounter, enableDebug, category)
        SELECT      ai_newOid, selectString, fromString,
                    whereString, queryType, groupByString, orderByString,
                    columnHeaders, queryAttrTypesForHeaders,
                    queryAttrForHeaders, searchFieldTokens,
                    queryAttrForFields, queryAttrTypesForFields, 
                    resultCounter, enableDebug, category
        FROM        ibs_QueryCreator_01
        WHERE       oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_QueryCreator_01$BOCopy',
        'Insert INTO');

    END;

    IF  (SQL%ROWCOUNT >= 1) THEN
        l_retValue :=  c_ALL_RIGHT;
    END IF;

    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_QueryCreator_01$BOCopy',
    ', userId = ' || ai_userId  ||
    ', newOid = ' || ai_newOid ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_QueryCreator_01$BOCopy;
/

show errors;


/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtSelect
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  selectString
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtSelect',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtSelect;
/

show errors;


/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtFrom
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  fromString
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtFrom',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtFrom;
/

show errors;


/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtWhere
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  whereString
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtWhere',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtWhere;
/

show errors;



/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtHead
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  columnHeaders
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtHead',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtHead;
/

show errors;



/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtColAt
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  queryAttrForHeaders
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtColAt',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtColAt;
/

show errors;



/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getColAtrTp
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  queryAttrTypesForHeaders
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getColAtrTp',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getColAtrTp;
/

show errors;



/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtField
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  searchFieldTokens
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtField',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtField;
/

show errors;


/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtFieAt
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  queryAttrForFields
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtFieAt',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtFieAt;
/

show errors;


/******************************************************************************
 * Retrieve CLOB-Data for current object. <BR>
 * 
 * @input parameters:
 * @param   ai_oid             ID of current object.
 * 
 * @output parameters:
 * @param   ao_content         content of CLOB.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

CREATE OR REPLACE FUNCTION p_QueryCreator_01$getExtFieTp
( 
    ai_oid      VARCHAR2, 
    ao_content  OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT CONSTANT INTEGER := 1;
    c_NOT_OK    CONSTANT INTEGER := 0;
    
    -- locals
    l_oid       RAW (8);
BEGIN
    -- convertions
    p_stringToByte (ai_oid, l_oid);

    -- get data for current attribute
    SELECT  queryAttrTypesForFields
    INTO    ao_content
    FROM    ibs_QueryCreator_01
    WHERE   oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;
EXCEPTION
WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_QueryCreator_01$getExtFieTp',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_content := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_QueryCreator_01$getExtFieTp;
/

show errors;

EXIT;
