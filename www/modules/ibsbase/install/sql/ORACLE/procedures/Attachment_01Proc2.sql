/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 * 
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Attachment_01Proc is splited into Attachment_01Proc1 and Attachment_01Proc2
 * because of a cyclic-dependency. <BR>
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId                ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be 
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Attachment_01$create
(
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_tVersionId       INTEGER,
    ai_name             VARCHAR2 ,
    ai_containerId_s    VARCHAR2 ,
    ai_containerKind    INTEGER,
    ai_isLink           NUMBER,
    ai_linkedObjectId_s VARCHAR2 ,
    ai_description      VARCHAR2,
    ao_oid_s            OUT VARCHAR2
)
RETURN INTEGER
AS
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_MAS_FILE              CONSTANT INTEGER := 1;
    c_MAS_HYPERLINK         CONSTANT INTEGER := 2;

    l_name                  VARCHAR2(63);
    l_nameLong              VARCHAR2(100);
    l_oid                   RAW (8) := c_NOOID;
    l_containerId           RAW (8) := c_NOOID;
    l_linkedObjectId        RAW (8) := c_NOOID;
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_attachmentFor         VARCHAR(255) := '';

BEGIN
    l_name := ai_name;

    -- convert oids
    BEGIN
        p_stringToByte(ai_linkedObjectId_s, l_linkedObjectId);
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
                    'Error in converting linkedObjectId_s');
        RAISE;
    END;

    BEGIN
        p_stringToByte(ai_containerId_s, l_containerId);
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
                    'Error in converting containerId_s');
        RAISE;
    END;

    BEGIN
        BEGIN
            -- get value of token 'TOK_ATTACHMENTFOR'
            SELECT value
            INTO   l_attachmentFor
            FROM   ibs_Token_01
            WHERE  name = 'TOK_ATTACHMENT_FOR';
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
                        'Error in SELECT value');
            RAISE;
        END;
    END;

    IF  ( ( ai_tVersionId =  16842833))
    THEN
        BEGIN
            SELECT  l_attachmentFor || ' ' || name
            INTO    l_nameLong
            FROM    ibs_object
            WHERE   oid  IN (   SELECT  containerId
                                FROM ibs_Object
                                WHERE oid = l_containerId
                            );
        EXCEPTION      
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
                        'Error in get Name out of DB');
            RAISE;
        END;

        BEGIN
            l_name := SUBSTR (l_nameLong, 0, 63);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
                        'Error in cutting the nameLong to 63');
            RAISE;
        END;
    END IF;

    BEGIN
        l_retValue := p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
            l_name, ai_containerId_s, ai_containerKind, ai_isLink,
            ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
                    'Error in performCreate of Object');
        RAISE;
    END;

    IF  ( l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            INSERT INTO ibs_Attachment_01 (oid, filename, path, filesize, url, attachmentType, isMaster)
            VALUES (l_oid, ' ', ' ', 0.0, ' ', 1, 0);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
                        'Error in INSERT - Statement');
            RAISE;
        END;
    END IF; -- if c_ALL_RIGHT

COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$create',
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', tVersionId = ' || ai_tVersionId ||
    ', name = ' || ai_name ||
    ', containerId_s = ' || ai_containerId_s ||
    ', containerKind = ' || ai_containerKind ||
    ', isLink = ' || ai_isLink ||
    ', linkedObjectId_s = ' || ai_linkedObjectId_s ||
    ', description = ' || ai_description ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    -- return error value:
    RETURN c_NOT_OK;
END p_Attachment_01$create;
/

show errors;


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId                ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @ai_showInNews      Display object in the news.
 *
 * @param   ai_isWeblink       Is true if the flag 32 is set (flag 32 is set
 *                             when the attachment is a weblink).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Attachment_01$change
(
    ai_oid_s            VARCHAR2,
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_name             VARCHAR2,
    ai_validUntil       DATE,
    ai_description      VARCHAR2,
    ai_showInNews       NUMBER,
    ai_isMaster         NUMBER,
    ai_attachmentType   INTEGER,
    ai_filename         VARCHAR2,
    ai_path             VARCHAR2,
    ai_url              VARCHAR2,
    ai_filesize         FLOAT,
    ai_isWeblink        NUMBER
)
RETURN INTEGER
AS
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_MAS_FILE              CONSTANT INTEGER := 1;
    c_MAS_HYPERLINK         CONSTANT INTEGER := 2;
    c_WEBLINK               CONSTANT INTEGER := 32; -- constant for weblink
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8);
    l_containerId           RAW (8);
    l_tVersionId            INTEGER;
    l_documentId            RAW (8) := c_NOOID;
    l_masterId              RAW (8) := null;    
    l_isMaster              NUMBER := ai_isMaster;
BEGIN

    -- perform the change of the object:
    l_retValue := p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
                        ai_validUntil, ai_description, ai_showInNews, l_oid);

    IF  ( l_retValue = c_ALL_RIGHT)         -- operation properly performed ?
    THEN
        BEGIN
            BEGIN
                SELECT  containerId, tVersionId
                INTO    l_containerId, l_tVersionId
                FROM    ibs_Object
                WHERE   oid = l_oid;
            EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$change',
                                            'Error in SELECT containerId, tVersionId');
                    RAISE;
            END;
            
            UPDATE ibs_Attachment_01
            SET attachmentType = ai_attachmentType,
                filename = ai_filename,
                path = ai_path,
                url = ai_url,
                filesize = ai_filesize
            WHERE oid = l_oid;

            -- updates the flags of the object if it is a weblink
            IF (ai_isWeblink = 1)       -- is the object a weblink ?
            THEN
                BEGIN
                    -- updates the flags of the object
                    UPDATE  ibs_object  -- set the selected property isWeblink
                    SET     flags = B_OR (flags, c_WEBLINK)
                    WHERE   oid = l_oid;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error,
                                             'p_Attachment_01$change',
                                             'Error in UPDATE ibs_object');
                    RAISE;
                END;
            ELSE
                BEGIN
                    -- updates the flags of the object
                    UPDATE  ibs_object  -- set the selected property isWeblink
--
-- CHANGED because OF internal ORACLE error using BITAND 
-- only values up to 2147483647 (= 0x7FFFFFFF) can be used (without highest bit!)
--
--                    SET flags = B_AND(flags, B_XOR( 4294967295, c_WEBLINK)) -- 0xFFFFFFFF
--
                    SET flags = B_AND(flags, B_XOR(2147483647, c_WEBLINK)) -- 0x7FFFFFFF
                    WHERE   oid = l_oid;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error,
                                             'p_Attachment_01$change',
                                             'Error in UPDATE ibs_object');
                    RAISE;
                END;
            END IF; -- if is the object a weblink

            IF  (l_tVersionId = 16842833) -- is it an attachment ?
            THEN
            BEGIN
	            IF (ai_isMaster = 1)       -- the object is a master
	            THEN
	                l_masterId := l_oid ;
	            END IF; -- if the object is a master

                -- ensures that in the attachment container is a master set
                l_retValue := p_Attachment_01$ensureMaster 
                                            (l_containerId, l_masterId);

            END; -- if is it a attachment
            ELSIF  (l_tVersionId <> 16842833)
                                        -- not master or not simple ATTACHMENT
                                        -- if object if FILE or HYPERLINK
            
            THEN
                BEGIN
                    UPDATE ibs_object
--
-- CHANGED because OF internal ORACLE error using BITAND 
-- only values up to 2147483647 (= 0x7FFFFFFF) can be used (without highest bit!)
--
--                    SET flags = B_AND (flags, ( 4294967295 - c_MAS_FILE - c_MAS_HYPERLINK)) -- 0xFFFFFFFF
--
                    SET flags = B_AND (flags, ( 2147483647 - c_MAS_FILE - c_MAS_HYPERLINK)) -- 0x7FFFFFFF
                    WHERE oid = l_oid;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$change',
                                            'Error in set flags to 0');
                    RAISE;
                END;

                -- FILE
                IF  ((ai_filename IS NOT NULL AND ai_filename != ' ') AND ai_attachmentType = c_MAS_FILE)
                THEN
                    BEGIN
                        UPDATE ibs_object
                        SET flags = B_OR( flags, c_MAS_FILE )
                        WHERE oid = l_oid;
                    EXCEPTION
                        WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$change',
                                            'Error in set flags for FILE 2');
                        RAISE;
                    END;
                END IF; -- if filename NOT NULL and attachmentType = FILE

                -- HYPERLINK
                IF  ((ai_url IS NOT NULL AND ai_url != ' ') AND ai_attachmentType = c_MAS_HYPERLINK)
                THEN
                    BEGIN
                        UPDATE ibs_object
                        SET flags = B_OR(flags, c_MAS_HYPERLINK )
                        WHERE oid = l_oid;
                    EXCEPTION
                        WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$change',
                                            'Error in set flags for HYPERLINK');
                        RAISE;
                    END;
                END IF; -- IF url NOT NULL and attachmentType = Hyperlink
           END IF; -- IF tVersionId = FILE or HYPERLINK
        END; -- BEGIN
    END IF; -- if operation properly performed

COMMIT WORK;
    RETURN l_retValue;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$change',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', name = ' || ai_name ||
    ', validUntil = ' || ai_validUntil  ||
    ', description = ' || ai_description ||
    ', isMaster = ' || ai_isMaster ||
    ', attachmentType = ' || ai_attachmentType ||
    ', filename = ' || ai_filename ||
    ', path = ' || ai_path ||
    ', url = ' || ai_url ||
    ', filesize = ' || ai_filesize ||
    ', showInNews = ' || ai_showInNews ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    -- return error value:
    RETURN c_NOT_OK;
END p_Attachment_01$change;
/

show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId                ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
 * @param   @containerKind      Kind of object/container relationship.
 * @param   @isLink             Is the object a link?
 * @param   @linkedObjectId     Link if isLink is true.
 * @param   @owner              ID of the owner of the object.
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the 
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @ao_showInNews      Display the object in the news.
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   ao_isWeblink       Is true if the flag 32 is set (flag 32 is set
 *                             when the attachment is a weblink).
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Attachment_01$retrieve
(
    ai_oid_s              VARCHAR2,
    ai_userId             NUMBER,
    ai_op                 NUMBER,
    ao_state              OUT NUMBER,
    ao_tVersionId         OUT NUMBER,
    ao_typeName           OUT VARCHAR2,
    ao_name               OUT VARCHAR2,
    ao_containerId        OUT RAW,
    ao_containerName      OUT VARCHAR2,
    ao_containerKind      OUT NUMBER,
    ao_isLink             OUT NUMBER,
    ao_linkedObjectId     OUT RAW,
    ao_owner              OUT NUMBER,
    ao_ownerName          OUT VARCHAR2,
    ao_creationDate       OUT DATE,
    ao_creator            OUT NUMBER,
    ao_creatorName        OUT VARCHAR2,
    ao_lastChanged        OUT DATE,
    ao_changer            OUT NUMBER,
    ao_changerName        OUT VARCHAR2,
    ao_validUntil         OUT DATE,
    ao_description        OUT VARCHAR2,
    ao_showInNews         OUT NUMBER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    ao_isMaster           OUT NUMBER,
    ao_attachmentType     OUT NUMBER,
    ao_filename           OUT VARCHAR2,
    ao_path               OUT VARCHAR2,
    ao_url                OUT VARCHAR2,
    ao_filesize           OUT FLOAT,
    ao_isWeblink            OUT NUMBER
)
RETURN INTEGER
AS
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT               CONSTANT  INTEGER := 1;
    c_INSUFFICIENT_RIGHTS     CONSTANT  INTEGER := 2;
    c_OBJECTNOTFOUND          CONSTANT  INTEGER := 3;
    c_MAS_INNEWS              CONSTANT  INTEGER := 4;
    c_WEBLINK               CONSTANT INTEGER := 32; -- constant for weblink
    l_retValue                INTEGER := c_ALL_RIGHT;
    l_oid                     RAW (8);

BEGIN

    l_retValue := p_Object$performRetrieve (
                      ai_oid_s, ai_userId, ai_op,
                      ao_state, ao_tVersionId, ao_typeName,
                      ao_name, ao_containerId, ao_containerName,
                      ao_containerKind, ao_isLink, ao_linkedObjectId,
                      ao_owner, ao_ownerName, ao_creationDate,
                      ao_creator, ao_creatorName, ao_lastChanged,
                      ao_changer, ao_changerName, ao_validUntil,
                      ao_description, ao_showInNews, 
                      ao_checkedOut, ao_checkOutDate, 
                      ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                      l_oid);
    IF  ( l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            SELECT   isMaster,  attachmentType,  filename,  path,  url,  filesize
            INTO ao_isMaster, ao_attachmentType, ao_filename, ao_path, ao_url, ao_filesize
            FROM ibs_Attachment_01
            WHERE oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,'p_attachment_01$retrieve', 'Select from ibs_attachment_01');
                RAISE;
        END;

        BEGIN
	        -- get the flag 32 out of the flags
	        -- flag 32 is set, when the object is a weblink
	        SELECT  B_AND (flags, c_WEBLINK)
	        INTO    ao_isWeblink
	        FROM    ibs_Object
	        WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error,
                                     'p_attachment_01$retrieve',
                                     'Error in SELECT flags');
                RAISE;
        END;
    END IF;

    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$retrieve',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    -- return error value:
    RETURN c_NOT_OK;
END p_Attachment_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId                ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Attachment_01$delete
(
    oid_s           VARCHAR2,
    userId          INTEGER,
    op              INTEGER
) 
RETURN INTEGER
AS
    -- values:
    -- exception values:
    StoO_error 	    INTEGER;
    StoO_errmsg	    VARCHAR2(255);
    -- conversion
    oid RAW (8);
    -- constants
    c_NOOID             CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_INSUFFICIENT_RIGHTS CONSTANT INTEGER := 2;
    c_ALL_RIGHT           CONSTANT INTEGER := 1;
    c_OBJECTNOTFOUND      CONSTANT INTEGER := 3;
    c_RIGHT_DELETE        CONSTANT INTEGER := 16;
    c_MAS_FILE            CONSTANT INTEGER := 1;
    c_MAS_HYPERLINK       CONSTANT INTEGER := 2;
    -- define return values
    retValue            INTEGER := c_ALL_RIGHT; -- return value of this procedure
    rights              INTEGER := 0;       -- return value of called proc.
    -- define used variables
    containerId         RAW (8);
    tVersionId          INTEGER;
    isMaster            NUMBER;
    masterId            RAW (8) := p_Attachment_01$delete.c_NOOID;
    documentId          RAW (8) := p_Attachment_01$delete.c_NOOID;
    attachmentType      INTEGER;
    dummy		RAW (8);
    
BEGIN
    
    -- conversions (objectidstring) - all input objectids must be converted    
    p_stringToByte (p_Attachment_01$delete.oid_s, p_Attachment_01$delete.oid);



    -- read out the tVersionID for File and URL
    SELECT tVersionId, containerId
    INTO   p_Attachment_01$delete.tVersionId, p_Attachment_01$delete.containerId
    FROM   ibs_object 
    WHERE  oid = p_Attachment_01$delete.oid;

    -- all references and the object itself are deleted (plus rights)
    p_Attachment_01$delete.retValue := p_Object$performDelete (
                p_Attachment_01$delete.oid_s, p_Attachment_01$delete.userId, 
                p_Attachment_01$delete.op,
                p_Attachment_01$delete.dummy);

    IF (p_Attachment_01$delete.retValue = c_ALL_RIGHT)
    THEN
        SELECT  isMaster
        INTO    p_Attachment_01$delete.isMaster
        FROM    ibs_Attachment_01
        WHERE   oid = p_Attachment_01$delete.oid;
        
        -- if we delete a master attachment, we must find a other one
	    IF ((p_Attachment_01$delete.isMaster = 1) AND
	        (p_Attachment_01$delete.tVersionId = 16842833))
	    THEN
             p_Attachment_01$delete.retValue := p_Attachment_01$ensureMaster
                                        (p_Attachment_01$delete.containerId,
                                         null);
        END IF;
    END IF;
	
    COMMIT WORK;
    -- return the state value
    RETURN  p_Attachment_01$delete.retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$delete',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
    -- return error value:
    RETURN c_NOT_OK;
END p_Attachment_01$delete;
/

show errors;

/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @oid              ID of the object to be copied.
 * @param   @userId              ID of the user who copy the object.
 * @param   @newOid           The new Oid of the new created BusinessObject.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
CREATE OR REPLACE FUNCTION p_Attachment_01$BOCopy
(
    -- input parameters:
    oid                 RAW,       -- the oid of Object we want to copy
    userId                INTEGER,   -- the userId of the User who wants to copy
    newOid              RAW        -- the new OID of the copied Object
)
RETURN INTEGER
AS
    -- exception values:
    StoO_error 	INTEGER;
    StoO_errmsg	VARCHAR2(255);
    -- define return values
    retValue            INTEGER;               -- return value of this procedure
    -- define constants:
    NOT_OK              INTEGER := 0;
    ALL_RIGHT           INTEGER := 1; 
    INSUFFICIENT_RIGHTS INTEGER := 2;
    OBJECTNOTFOUND      INTEGER := 3;
    dummy               INTEGER;
BEGIN

    p_Attachment_01$BOCopy.retValue :=  p_Attachment_01$BOCopy.NOT_OK;


-- ****************************************************************************
-- Hier sollte normalerweise ein Überprüfung erfollgen, ob die Beilage sich in
-- einem Container befindet, wo es schon einen Master gibt, wenn nicht, dann
-- sollte dieser natürlich gesetzt werden, und ebenfalls sollten die flags in
-- der Tabelle ibs_Object richtig gesetzt werden.
-- Ist zurzeit als HACK in p_Object$copy mit der StoredProzedure 
-- p_Attachment_01$ensureCMaster gelöst.  DJ 22. August 2000
-- ****************************************************************************

    -- make a insert for all your typespecific tables
    INSERT  INTO ibs_Attachment_01
            (oid, filename, path, filesize, url, attachmentType, isMaster)
    SELECT  p_Attachment_01$BOCopy.newOid, filename, path, filesize, 
            url, attachmentType, isMaster
    FROM    ibs_Attachment_01
    WHERE   oid = p_Attachment_01$BOCopy.oid;
    p_Attachment_01$BOCopy.dummy := SQL%ROWCOUNT;
    -- check if the insert has processed correctly
    IF (p_Attachment_01$BOCopy.dummy >=1)
    THEN
        p_Attachment_01$BOCopy.retvalue := p_Attachment_01$BOCopy.All_RIGHT;
    END IF;

COMMIT WORK;
    -- return the state value:
    RETURN p_Attachment_01$BOCopy.retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, ' p_Attachment_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
    -- return error value:
    RETURN p_Attachment_01$BOCopy.NOT_OK;
END p_Attachment_01$BOCopy;
/

show errors;

exit;
