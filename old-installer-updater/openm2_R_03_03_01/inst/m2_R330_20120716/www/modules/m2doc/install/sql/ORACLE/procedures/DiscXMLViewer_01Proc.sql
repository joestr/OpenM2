/******************************************************************************
 * All stored procedures regarding the DiscXMLViewer_01 Object. <BR>
 *
 * @version     $Id: DiscXMLViewer_01Proc.sql,v 1.7 2003/10/31 00:13:13 klaus Exp $
 *
 * @author      Keim Christine (CK)  001010
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new DiscXMLViewer_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_DiscXMLViewer_01$create
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
    -- output parameters:
    ao_oid_s                OUT VARCHAR2
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
    l_posNoPath             VARCHAR2 (254);
    l_discussionId          RAW (8);


    -- body:
BEGIN
    -- create base object:
    l_retValue := p_XMLViewer_01$create (ai_userId, ai_op, ai_tVersionId,
                    ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
                    ai_linkedObjectId_s, ai_description, ao_oid_s);

    IF (l_retValue = c_ALL_RIGHT)     -- object and xmlviewer entries created successfully?
    THEN
        BEGIN
	    
	    p_stringToByte (ao_oid_s, l_oid);
	    
            -- retrieve the posNoPath of the entry
            SELECT  posNoPath
            INTO    l_posNoPath
            FROM    ibs_Object
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$create',
                                     'Error in SELECT posNoPath');
        END;

        BEGIN
            -- retrieve the oid of the discussion
            SELECT  oid
            INTO    l_discussionId
            FROM    ibs_Object
            WHERE   tVersionId = 16843553
              AND   l_posNoPath LIKE posNoPath || '%';
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$create',
                                     'Error in SELECT oid');
        END;

        BEGIN
            -- insert the other values
            INSERT INTO m2_Article_01 
                (oid, content, discussionId)
            VALUES
                (l_oid, ai_description, l_discussionid);
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$create',
                                     'Error in SELECT name');
        END;
    END IF; -- if object created successfully
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_DiscXMLViewer_01$create',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DiscXMLViewer_01$create;
/

show errors;
-- p_DiscXMLViewer_01$create



/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s              ID of the object to be changed.
 * @param   ai_userId             ID of the user who is creating the object.
 * @param   ai_op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @ parameters:
 * @param   ao_state              The object's state.
 * @param   ao_tVersionId         ID of the object's type (correct version).
 * @param   ao_typeName           Name of the object's type.
 * @param   ao_name               Name of the object itself.
 * @param   ao_containerId        ID of the object's container.
 * @param   ao_containerKind      Kind of object/container relationship.
 * @param   ao_isLink             Is the object a link?
 * @param   ao_linkedObjectId     Link if isLink is true.
 * @param   ao_owner              ID of the owner of the object.
 * @param   ao_creationDate       Date when the object was created.
 * @param   ao_creator            ID of person who created the object.
 * @param   ao_lastChanged        Date of the last change of the object.
 * @param   ao_changer            ID of person who did the last change to the 
 *                              object.
 * @param   ao_validUntil         Date until which the object is valid.
 * @param   ao_showInNews         show in news flag.
 * @param   ao_checkedOut         Is the object checked out?
 * @param   ao_checkOutDate       Date when the object was checked out
 * @param   ao_checkOutUser       id of the user which checked out the object
 * @param   ao_checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   ao_templateOid      Oid of the template this object uses.
 * @param   ao_wfTemplateOid_s    oid of the workflow this object uses
 * @param   ao_systemDisplayMode  should the system attributes be shown
 * @param   ao_dbMapped         Object is dbmapped
 * @param   ao_level            Discussionlevel which this object is in
 * @param   ao_hasSubEntries    Number of the subentries the object(entry) has
 * @param   ao_rights           Rights the user has for this object
 * @param   ao_discussionId     Oid of the discussion this object is pertinent to
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DiscXMLViewer_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    --  parameters
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
    ao_templateOid          OUT RAW,
    ao_wfTemplateOid        OUT RAW,
    ao_systemDisplayMode    OUT NUMBER,
    ao_dbMapped             OUT NUMBER,
    ao_showDOMTree          OUT NUMBER,
    ao_level                OUT INTEGER,
    ao_hasSubEntries        OUT INTEGER,
    ao_rights               OUT INTEGER,
    ao_discussionId         OUT RAW
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
    l_olevel                INTEGER := -1;

    -- body:
BEGIN
    -- conversions
    p_stringToByte (ai_oid_s, l_oid);
    
    -- retrieve the base object data:
    l_retValue := p_XMLViewer_01$retrieve (ai_oid_s, ai_userId, ai_op, ao_state,
            ao_tVersionId, ao_typeName, ao_name, ao_containerId,
            ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId, 
            ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
            ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate, 
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, 
            ao_templateOid, ao_wfTemplateOid ,ao_systemDisplayMode, 
            ao_dbMapped, ao_showDOMTree);

    BEGIN
        -- retrieve the discussionId of the entry
        SELECT  o.olevel, discussionId
        INTO    l_olevel, ao_discussionId
        FROM    ibs_Object o, m2_Article_01 b
        WHERE   o.oid = l_oid
	AND o.oid = b.oid;
    EXCEPTION
        WHEN OTHERS THEN
           ibs_error.log_error ( ibs_error.error, 
                                 'p_DiscXMLViewer_01$retrieve',
                                 'Error in SELECT o.olevel');
    END;

    BEGIN
        -- compute the level of the entry
	    SELECT  (l_olevel - olevel)
	    INTO    ao_level
	    FROM    ibs_Object
	    WHERE   oid = ao_discussionId;
    EXCEPTION
        WHEN OTHERS THEN
           ibs_error.log_error ( ibs_error.error, 
                                 'p_DiscXMLViewer_01$retrieve',
                                 'Error in SELECT (l_olevel - olevel)');
    END;

    BEGIN
        -- retrieve if entry has subEntries
        SELECT  COUNT(*)
        INTO    ao_hasSubEntries
        FROM    ibs_Object
        WHERE   tVersionId IN (16872721)
          AND   containerId = l_oid                    
          AND   state = 2;
    EXCEPTION
        WHEN OTHERS THEN
           ibs_error.log_error ( ibs_error.error, 
                                 'p_DiscXMLViewer_01$retrieve',
                                 'Error in SELECT COUNT(*)');
    END;
    
    -- get the rights this user has for the current object
    p_Rights$getRights (l_oid, ai_userId, ao_rights);
    
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_DiscXMLViewer_01$retrieve',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DiscXMLViewer_01$retrieve;
/

show errors;
-- p_DiscXMLViewer_01$retrieve


/******************************************************************************
 * Deletes a DiscXMLViewer_01 object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   ai_oid_s              ID of the object to be deleted.
 * @param   ai_userId             ID of the user who is deleting the object.
 * @param   ai_op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DiscXMLViewer_01$delete
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

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- convertions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);
    
    -- delete base object:
    l_retValue := p_XMLViewer_01$delete (ai_oid_s, ai_userId, ai_op);

    IF (l_retValue = c_ALL_RIGHT)           -- operation properly performed?
    THEN
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  m2_Article_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$delete',
                                     'Error in DELETE');
        END;

    END IF; -- if operation properly performed
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_DiscXMLViewer_01$delete',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DiscXMLViewer_01$delete;
/

show errors;
-- p_DiscXMLViewer_01$delete


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId                ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DiscXMLViewer_01$BOCopy
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
    l_posNoPath             VARCHAR2 (254);
    l_discussionId          RAW (8);
    l_path                  VARCHAR (255);
    l_oid_s                 VARCHAR2 (18);
    l_copiedDisc            NUMBER := 0;

    
    -- body:
BEGIN    
    -- CONVERTIONS (OBJECTID) 
    p_byteToString (ai_oid, l_oid_s);

    BEGIN
        SELECT  value || 'upload/files/'
        INTO    l_path
        FROM    ibs_System
        WHERE   name = 'WWW_BASE_PATH';
    EXCEPTION
        WHEN OTHERS THEN
           ibs_error.log_error ( ibs_error.error, 
                                 'p_DiscXMLViewer_01$BOCopy',
                                 'Error in SELECT value');
    END;

    BEGIN
        -- make an insert for all type specific tables:
        INSERT INTO ibs_Attachment_01
                (oid, filename, url, fileSize, path, attachmentType, isMaster)
        SELECT  ai_newOid, b.filename, b.url, b.fileSize, 
                l_path || l_oid_s || SUBSTR (l_path, LENGTH(l_path)),
                b.attachmentType, b.isMaster 
        FROM    ibs_Attachment_01 b
        WHERE   b.oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
           l_retValue := c_NOT_OK;    -- set return value
           ibs_error.log_error ( ibs_error.error, 
                                 'p_DiscXMLViewer_01$BOCopy',
                                 'Error in INSERT INTO ibs_Attachment_01');
    END;

    IF (l_retValue = c_ALL_RIGHT)           -- operation properly performed?
    THEN
        BEGIN
            -- retrieve the posNoPath of the entry
            SELECT  posNoPath
            INTO    l_posNoPath
            FROM    ibs_Object
            WHERE   oid = ai_newOid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$BOCopy',
                                     'Error in SELECT posNoPath');
        END;

        BEGIN
            -- retrieve the oid of the discussion
            SELECT  oid
            INTO    l_discussionId
            FROM    ibs_Object
            WHERE   tVersionId = 16843553 -- XMLDiscussion
                AND l_posNoPath LIKE posNoPath || '%';
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$BOCopy',
                                     'Error in SELECT oid');
        END;

        BEGIN
            SELECT  COUNT (*)
            INTO    l_copiedDisc 
            FROM    ibs_Copy
            WHERE   oldOid = l_discussionId;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$BOCopy',
                                     'Error in SELECT COUNT (*)');
        END;

        IF (l_copiedDisc = 1)
        THEN
            BEGIN
                SELECT  newOid
                INTO    l_discussionId
                FROM    ibs_Copy
                WHERE   oldOid = l_discussionId;
            EXCEPTION
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error, 
                                         'p_DiscXMLViewer_01$BOCopy',
                                         'Error in SELECT newOid');
            END;
        END IF;

        BEGIN
            -- make an insert for all type specific tables:
            INSERT INTO m2_Article_01
                     (oid, content, discussionId)
            SELECT  ai_newOid, b.content, l_discussionId
            FROM    m2_Article_01 b
            WHERE   b.oid = ai_oid;
        EXCEPTION
            WHEN OTHERS THEN
               l_retValue := c_NOT_OK;    -- set return value
               ibs_error.log_error ( ibs_error.error, 
                                     'p_DiscXMLViewer_01$BOCopy',
                                     'Error in INSERT INTO m2_Article_01');
        END;
    END IF; -- if operation properly performed
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_DiscXMLViewer_01$create',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DiscXMLViewer_01$BOCopy;
/

show errors;
-- p_DiscXMLViewer_01$BOCopy

EXIT;
