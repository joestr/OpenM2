/******************************************************************************
 * All stored procedures regarding the ASCIITranslator_01 Object. <BR>
 *
 * @version     2.2.1.0002, 21.03.2002 KR
 *
 * @author      Bernd Buchegger    (BB)  20010307
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
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
 * @param   ao_oid_s                OID of the newly created object.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_ASCIITranslator_01$create
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
    c_NOT_OK 			    CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT 		    CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_ALREADY_EXISTS 		CONSTANT INTEGER := 21; -- the object exists already
    c_NOOID                 CONSTANT RAW (8) := hextoraw ('0000000000000000');
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   RAW (8) := c_NOOID; -- the actual oid

-- body:
BEGIN
/* no transactions allowed because already defined in p_Translator_01$create
    COMMIT WORK; -- finish previous and begin new TRANSACTION
*/

    -- create base object:
    l_retValue := p_Translator_01$create (
                    ai_userId, ai_op, ai_tVersionId, ai_name, ai_containerId_s, 
                    ai_containerKind, ai_isLink, ai_linkedObjectId_s,
                    ai_description,
                    ao_oid_s);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
	    -- convert the oid string:
	    p_stringToByte (ao_oid_s, l_oid);

    	BEGIN
            -- create object type specific data:
            INSERT INTO ibs_ASCIITranslator_01
                    (oid, separator, escapeSeparator, isIncludeMetadata,
                    isIncludeHeader)
            VALUES  (l_oid, '', '', 0, 0);
        EXCEPTION
            WHEN OTHERS THEN            -- any exception
                -- create error entry:
                l_ePos := 'Error in insert';
                RAISE;                  -- call common exception handler
        END;
    END IF; -- if object created successfully
        
/* no transactions allowed because already defined in p_Translator_01$create
    COMMIT WORK;                        -- make changes permanent
*/

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
/* no transactions allowed because already defined in p_Translator_01$create
        -- roll back to the beginning of the transaction:
        ROLLBACK;
*/
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            ', ai_tVersionId = ' || ai_tVersionId ||
            ', ai_name = ' || ai_name ||
            ', ai_containerId_s = ' || ai_containerId_s ||
            ', ai_containerKind = ' || ai_containerKind ||
            ', ai_isLink = ' || ai_isLink ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s ||
            ', ai_description = ' || ai_description ||
            ', ao_oid_s = ' || ao_oid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ASCIITranslator_01$create', l_eText);
/* no transactions allowed because already defined in p_Translator_01$create
        -- set new transaction starting point:
        COMMIT WORK;
*/
        -- return error code:
        RETURN c_NOT_OK; 
END p_ASCIITranslator_01$create;
/

show errors;


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
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
 * @param   ai_separator        The separator character.
 * @param   ai_escapeSeparator  The escape sequence for the separator character.
 * @param   ai_isIncludeMetadata Option that file includes metadata.
 * @param   ai_isIncludeHeader  Option that file includes header data.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_ASCIITranslator_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           INTEGER,
    -- attachment-specific input parameters:
    ai_isMaster             NUMBER,
    ai_attachmentType       INTEGER,
    ai_filename	            VARCHAR2,
    ai_path	                VARCHAR2,
    ai_url 	                VARCHAR2,
    ai_filesize	            FLOAT,
    ai_isWeblink            NUMBER,
    -- Translator-specific input parameters:
    ai_extension            VARCHAR2,
    -- ASCIITranslator-specific input parameters:       
    ai_separator            VARCHAR2,
    ai_escapeSeparator      VARCHAR2,    
    ai_isIncludeMetadata    NUMBER,
    ai_isIncludeHeader      NUMBER     
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			    CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT 		    CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND		CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT RAW (8) := hextoraw ('0000000000000000');
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   RAW (8) := c_NOOID; -- the actual oid

-- body:
BEGIN
/* no transactions allowed because already defined in p_Translator_01$change
    COMMIT WORK; -- finish previous and begin new TRANSACTION
*/

    -- perform the change of the object:
    l_retValue := p_Translator_01$change (ai_oid_s, ai_userId, ai_op, ai_name,
                	ai_validUntil, ai_description, ai_showInNews,
                	ai_isMaster, ai_attachmentType, ai_filename, 
                	ai_path, ai_url, ai_filesize, ai_isWeblink, ai_extension);

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        BEGIN
            -- convert the oid string:
            p_stringToByte (ai_oid_s, l_oid);
            -- update further information:
  	        UPDATE  ibs_ASCIITranslator_01
	        SET     separator = ai_separator,
                    escapeSeparator = ai_escapeSeparator,
                    isIncludeMetadata = ai_isIncludeMetadata,
                    isIncludeHeader = ai_isIncludeHeader
	        WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any exception
                -- create error entry:
                l_ePos := 'Error in update';
                RAISE;                  -- call common exception handler
        END;
    END IF; -- if operation properly performed

/* no transactions allowed because already defined in p_Translator_01$change
    COMMIT WORK;                        -- make changes permanent
*/

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
/* no transactions allowed because already defined in p_Translator_01$change
        -- roll back to the beginning of the transaction:
        ROLLBACK;
*/
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s  ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            ', ai_name = ' || ai_name ||
            ', ai_validUntil = ' || ai_validUntil ||
            ', ai_description = ' || ai_description ||
            ', ai_showInNews = ' || ai_showInNews ||    
            ', ai_isMaster = ' || ai_isMaster ||
            ', ai_attachmentType = ' || ai_attachmentType ||
            ', ai_filename = ' || ai_filename ||
            ', ai_path = ' || ai_path ||
            ', ai_url = ' || ai_url ||
            ', ai_filesize = ' || ai_filesize ||
            ', ai_isWeblink = ' || ai_isWeblink ||
            ', ai_extension = ' || ai_extension ||
            ', ai_separator = ' || ai_separator ||
            ', ai_escapeSeparator = ' || ai_escapeSeparator ||
            ', ai_isIncludeMetadata = ' || ai_isIncludeMetadata ||
            ', ai_isIncludeHeader = ' || ai_isIncludeHeader ||                    
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ASCIITranslator_01$change', l_eText);
/* no transactions allowed because already defined in p_Translator_01$change
        -- set new transaction starting point:
        COMMIT WORK;
*/
        -- return error code:
        RETURN c_NOT_OK; 
END p_ASCIITranslator_01$change;
/

show errors;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
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
 * @param   ao_separator        The separator character.
 * @param   ao_escapeSeparator  The escape sequence for the separator character.
 * @param   ao_isIncludeMetadata Option that file includes metadata.
 * @param   ao_isIncludeHeader  Option that file includes header data.
 * 
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_ASCIITranslator_01$retrieve
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- common output parameters:
    ao_state                OUT	INTEGER,
    ao_tVersionId           OUT	INTEGER,
    ao_typeName             OUT	VARCHAR2,
    ao_name                 OUT	VARCHAR2,
    ao_containerId          OUT	RAW,
    ao_containerName        OUT	VARCHAR2,
    ao_containerKind        OUT	INTEGER,
    ao_isLink               OUT	NUMBER,
    ao_linkedObjectId       OUT	RAW,
    ao_owner                OUT	INTEGER,
    ao_ownerName            OUT	VARCHAR2,
    ao_creationDate         OUT	DATE,
    ao_creator              OUT	INTEGER,
    ao_creatorName          OUT	VARCHAR2,
    ao_lastChanged          OUT	DATE,
    ao_changer              OUT	INTEGER,
    ao_changerName          OUT	VARCHAR2,
    ao_validUntil           OUT	DATE,
    ao_description          OUT	VARCHAR2,
    ao_showInNews           OUT NUMBER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,
    -- attachment-specific output attributes:
    ao_isMaster             OUT NUMBER,
    ao_attachmentType       OUT NUMBER,
    ao_filename             OUT VARCHAR2,
    ao_path                 OUT VARCHAR2,
    ao_url                  OUT VARCHAR2,
    ao_filesize             OUT FLOAT,
    ao_isWeblink            OUT NUMBER,
    -- Translator-specific output parameters:
    ao_extension            OUT VARCHAR2,
    -- ASCIITranslator specific
    ao_separator            OUT VARCHAR2,
    ao_escapeSeparator      OUT VARCHAR2,
    ao_isIncludeMetadata    OUT NUMBER,
    ao_isIncludeHeader      OUT NUMBER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			    CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT 		    CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND		CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT RAW (8) := hextoraw ('0000000000000000');
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   RAW (8) := c_NOOID; -- the actual oid

-- body:
BEGIN
/* no transactions allowed because already defined in p_Translator_01$retrieve
    COMMIT WORK; -- finish previous and begin new TRANSACTION
*/

    -- retrieve the base object data:
    l_retValue := p_Translator_01$retrieve (
            ai_oid_s, ai_userId, ai_op,
            ao_state, ao_tVersionId, ao_typeName, ao_name,
            ao_containerId, ao_containerName, ao_containerKind,
            ao_isLink, ao_linkedObjectId, 
            ao_owner, ao_ownerName, 
            ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName,
            ao_validUntil, ao_description, ao_showInNews, 
            ao_checkedOut, ao_checkOutDate, ao_checkOutUser, 
            ao_checkOutUserOid, ao_checkOutUserName,
            ao_isMaster, ao_attachmentType, ao_filename, 
            ao_path, ao_url, ao_filesize, ao_isWeblink, ao_extension);

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        BEGIN            
            -- convert the oid string:
            p_stringToByte (ai_oid_s, l_oid);            
            -- get type specific data:
            SELECT  separator, escapeSeparator, isIncludeMetadata,
                    isIncludeHeader
	        INTO    ao_separator, ao_escapeSeparator, ao_isIncludeMetadata,
	                ao_isIncludeHeader                    
            FROM    ibs_ASCIITranslator_01
	        WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any exception
                -- create error entry:
                l_ePos := 'Error in select';
                RAISE;                  -- call common exception handler
        END;
    END IF; -- if operation properly performed

/* no transactions allowed because already defined in p_Translator_01$retrieve
    COMMIT WORK;                        -- make changes permanent
*/

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
/* no transactions allowed because already defined in p_Translator_01$retrieve
        -- roll back to the beginning of the transaction:
        ROLLBACK;
*/
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s  ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op ||
            ', ao_extension = ' || ao_extension ||
            ', ao_separator = ' || ao_separator ||
            ', ao_escapeSeparator = ' || ao_escapeSeparator ||
            ', ao_isIncludeMetadata = ' || ao_isIncludeMetadata ||
            ', ao_isIncludeHeader = ' || ao_isIncludeHeader ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ASCIITranslator_01$retrieve', l_eText);
/* no transactions allowed because already defined in p_Translator_01$retrieve
        -- set new transaction starting point:
        COMMIT WORK;
*/
        -- return error code:
        RETURN c_NOT_OK; 
END p_ASCIITranslator_01$retrieve;
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
CREATE OR REPLACE FUNCTION p_ASCIITranslator_01$BOCopy
(
    -- common input parameters:
    ai_oid                  RAW,
    ai_userId               INTEGER,
    ai_newOid               RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			    CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT 		    CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text

-- body:
BEGIN
/* no transactions allowed because already defined in p_Translator_01$BOCopy
    COMMIT WORK; -- finish previous and begin new TRANSACTION
*/

    -- copy base object:
    l_retValue := p_Translator_01$BOCopy (ai_oid, ai_userId, ai_newOid);

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        BEGIN
            -- make an insert for all type specific tables:
            -- (it's currently not possible to copy files!)
            INSERT INTO ibs_EDITranslator_01
                    (oid, filterFile, formatFile)
            SELECT  ai_newOid, '', ''
            FROM    ibs_EDITranslator_01
            WHERE   oid = ai_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any exception
                -- create error entry:
                l_ePos := 'insert new ascii translator data';
                RAISE;                  -- call common exception handler
        END;
     END IF; -- if operation properly performed

/* no transactions allowed because already defined in p_Translator_01$BOCopy
    COMMIT WORK;                        -- make changes permanent
*/

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
    WHEN OTHERS THEN
/* no transactions allowed because already defined in p_Translator_01$BOCopy
        -- roll back to the beginning of the transaction:
        ROLLBACK;
*/
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_newOid = ' || ai_newOid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ASCIITranslator_01$BOCopy', l_eText);
/* no transactions allowed because already defined in p_Translator_01$BOCopy
        -- set new transaction starting point:
        COMMIT WORK;
*/
        -- return error code:
        RETURN c_NOT_OK; 
END p_ASCIITranslator_01$BOCopy;
/

show errors;

exit; 
