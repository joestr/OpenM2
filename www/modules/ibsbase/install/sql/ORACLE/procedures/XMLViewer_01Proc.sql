/******************************************************************************
 * Stored procedures regarding XML Viewer. <BR>
 *
 *
 * @version     1.10.0001, 17.05.2000
 *
 * @author      Keim Christine (CK)  000517
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new XMLViewer_01 Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
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

CREATE OR REPLACE FUNCTION p_XMLViewer_01$create
(
    -- input parameters:
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_tVersionId       INTEGER,
    ai_name             VARCHAR2,
    ai_containerId_s    VARCHAR2,
    ai_containerKind    INTEGER,
    ai_isLink           NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description      VARCHAR2,
    -- output parameters:
    ao_oid_s            OUT VARCHAR2
)
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_error 	            INTEGER;
    StoO_errmsg	            VARCHAR2(255);
    -- locals
    l_path                  VARCHAR (255);
    l_containerId           RAW(8);
    l_linkedObjectId        RAW(8);
    l_oid                   RAW(8) := hexToRaw ('0000000000000000');
    l_templateOid           RAW(8);
    l_wfTemplateOid         RAW(8);
    l_containerTVersionId   INTEGER := 0;
    -- define return constants
    c_NOT_OK                INTEGER := 0;
    c_ALL_RIGHT             INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   INTEGER := 2;
    c_ALREADY_EXISTS        INTEGER := 21;
    -- define return values
    l_retValue              INTEGER := c_ALL_RIGHT;
BEGIN
    -- convertions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- start transaction
    BEGIN
        -- create base object:
        l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                                              ai_name, ai_containerId_s,
                                              ai_containerKind, ai_isLink,
                                              ai_linkedObjectId_s,
                                              ai_description, ao_oid_s, l_oid);

	    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
        THEN
	        SELECT value || 'upload/files/'
	        INTO l_path
	        FROM ibs_System
	        WHERE name = 'WWW_BASE_PATH';

            -- insert the other values
            INSERT
            INTO ibs_Attachment_01 (oid, filename, url, fileSize, path, attachmentType, isMaster)
            VALUES (l_oid, 'xmldata.xml', ' ', 1, l_path || ao_oid_s || '/', 3, 0);


            -- get the template and workflow oid associated with the form.
            -- for old forms (tVersionId == XMLViewer_01) this values are set in Java.
            -- for new forms we can do this now.
            IF (ai_tVersionId != 16872705)  -- is a new form with its own tVersionId
            THEN
                SELECT oid, workflowTemplateOid
                INTO l_templateOid, l_wfTemplateOid
                FROM ibs_DocumentTemplate_01
                WHERE tVersionId = ai_tVersionId;
            END IF;

            -- if no workflow template is defined in the document template
            -- and the container is a XMLViewerContainer_01
            -- get the workflow template from the container.
            IF (l_wfTemplateOid = hexToRaw ('0000000000000000'))
            THEN
                -- get the tVersionId of the container object
                SELECT tVersionId
                INTO l_containerTVersionId
                FROM ibs_Object
                WHERE oid = l_containerId;

                -- if the container is a XMLViewerContainer_01 or a ServicePoint_01
                -- get the workflow oid defined by the container.
                IF (l_containerTVersionId = 16875009 OR
                    l_containerTVersionId = 16843153)
                THEN
                    SELECT workflowTemplateOid
                    INTO l_wfTemplateOid
                    FROM ibs_XMLViewerContainer_01
                    WHERE oid = l_containerId;
                END IF; -- IF (l_containerTVersionId = 16875009)
            END IF; -- IF (l_wfTemplateOid = hexToRaw ('0000000000000000'))

            -- insert the template and workflow oid in the ibs_xmlviewer_01 table
            INSERT
            INTO ibs_XMLViewer_01 (oid, templateOid, workflowTemplateOid)
            VALUES (l_oid, l_templateOid, l_wfTemplateOid);
        END IF; -- if object created successfully
    END;
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        StoO_error  := SQLCODE;
        StoO_errmsg := SQLERRM;
        ibs_error.log_error ( ibs_error.error, 'p_XMLViewer_01$create', '');
        RETURN c_NOT_OK;
END p_XMLViewer_01$create;
/

show errors;
-- p_XMLViewer_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s              ID of the object to be changed.
 * @param   ai_userId             ID of the user who is changing the object.
 * @param   ai_op                 Operation to be performed (used for rights
 *                              check).
 * @param   ai_name               Name of the object.
 * @param   ai_validUntil         Date until which the object is valid.
 * @param   ai_description        Description of the object.
 * @param   ai_showInNews         Should the currrent object displayed in the news.
 * @param   ai_templateOid        oid of the document template object
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLViewer_01$change
(
    -- input parameters:
    ai_oid_s                  VARCHAR2,
    ai_userId                 INTEGER,
    ai_op                     INTEGER,
    ai_name                   VARCHAR2,
    ai_validUntil             DATE,
    ai_description            VARCHAR2,
    ai_showInNews             NUMBER,
    -- xml viewer specific input parameters:
    ai_templateOid_s          VARCHAR2,
    ai_wfTemplateOid_s        VARCHAR2
) RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_templateOid           RAW (8) := c_NOOID;
    l_wfTemplateOid         RAW (8) := c_NOOID;

    -- body:
BEGIN
        -- perform the change of the object:
        l_retValue := p_Object$change (ai_oid_s, ai_userId, ai_op, ai_name,
                ai_validUntil, ai_description, ai_showInNews);

        IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
        THEN
            BEGIN
                p_stringToByte (ai_oid_s, l_oid);
                p_stringToByte (ai_templateOid_s, l_templateOid);
                p_stringToByte (ai_wfTemplateOid_s, l_wfTemplateOid);

                -- insert the template oid in the ibs_xmlviewer_01 table
                UPDATE  ibs_XMLViewer_01
                SET templateOid = l_templateOid,
                    workflowTemplateOid = l_wfTemplateOid
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    l_retValue := c_OBJECTNOTFOUND;
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error,
                                         'p_XMLViewer_01$change',
                                         'Error in UPDATE');
                RAISE;
            END;
        END IF; -- if object created successfully
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,
            'p_XMLViewer_01$change',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLViewer_01$change;
/

show errors;
-- p_XMLViewer_01$change



/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * input parameters:
 * param   ai_oid_s              ID of the object to be changed.
 * param   ai_userId             ID of the user who is creating the object.
 * param   ai_op                 Operation to be performed (used for rights
 *                                check).
 *
 * output parameters:
 * param   ao_state              The object's state.
 * param   ao_tVersionId         ID of the object's type (correct version).
 * param   ao_typeName           Name of the object's type.
 * param   ao_name               Name of the object itself.
 * param   ao_containerId        ID of the object's container.
 * param   ao_containerKind      Kind of object/container relationship.
 * param   ao_isLink             Is the object a link?
 * param   ao_linkedObjectId     Link if isLink is true.
 * param   ao_owner              ID of the owner of the object.
 * param   ao_creationDate       Date when the object was created.
 * param   ao_creator            ID of person who created the object.
 * param   ao_lastChanged        Date of the last change of the object.
 * param   ao_changer            ID of person who did the last change to the
 *                                object.
 * param   ao_validUntil         Date until which the object is valid.
 * param   ao_description        Description of the object.
 * param   ao_showInNews         Display the object in the news.
 * param   ao_checkedOut         Is the object checked out?
 * param   ao_checkOutDate       Date when the object was checked out
 * param   ao_checkOutUser       id of the user which checked out the object
 * param   ao_checkOutUserOid    Oid of the user which checked out the object
 *                                is only set if this user has the right to
 *                                 READ the checkOut user
 * param   ao_checkOutUserName   name of the user which checked out the object,
 *                                is only set if this user has the right to view
 *                                the checkOut-User
 *
 * param   ao_templateOid        the oid of the template object
 * param   ao_wfTemplateOid      the oid of the workflow template object
 * param   ao_systemDisplayMode  the display mode for the system section
 * param   ao_dbMapped           the dbMapped attribute from the template object
 *
 * returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLViewer_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- output parameters
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
    ao_systemDisplayMode    OUT INTEGER,
    ao_dbMapped             OUT NUMBER,
    ao_showDOMTree          OUT NUMBER
)RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- operation was o.k.
    c_NOOID                 CONSTANT RAW (8) := hextoraw ('0000000000000000');
                                            -- oid of no valid object
    -- local variables:
    l_retValue             INTEGER := c_ALL_RIGHT;
                                            -- return value of this procedure
    l_oid                  RAW (8) := c_NOOID; -- converted input parameter
                                              -- oid_s


    -- body:
BEGIN
        -- retrieve the base object data:
        l_retValue := p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op,
                ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
                ao_containerName, ao_containerKind, ao_isLink,
                ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate,
                ao_creator, ao_creatorName, ao_lastChanged, ao_changer,
                ao_changerName, ao_validUntil, ao_description, ao_showInNews,
                ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
                ao_checkOutUserOid, ao_checkOutUserName, l_oid);

        IF (l_retValue = c_ALL_RIGHT)
        THEN
            -- get the template oid and the mapping attributes from the
            -- template
            p_stringToByte (ai_oid_s, l_oid);

            BEGIN
                SELECT  v.templateOid, v.workflowTemplateOid, t.systemDisplayMode, t.dbMapped, t.showDOMTree
                INTO    ao_templateOid, ao_wfTemplateOid, ao_systemDisplayMode, ao_dbMapped, ao_showDOMTree
                FROM    ibs_XMLViewer_01 v, ibs_DocumentTemplate_01 t
                WHERE   v.oid = l_oid
                  AND   t.oid = v.templateOid;
            EXCEPTION
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error,
                                         'p_XMLViewer_01$retrieve',
                                         'Error in SELECT');
                RAISE;
            END;
        END IF;
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,
            'p_XMLViewer_01$retrieve',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLViewer_01$retrieve;
/

show errors;
-- p_XMLViewer_01$retrieve



/******************************************************************************
 * Deletes a XMLViewer_01 object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
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

-- delete existing procedure:
CREATE OR REPLACE FUNCTION p_XMLViewer_01$delete
(
    -- input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId            INTEGER,
    ai_op             INTEGER
)
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- define constants
    c_NOOID             RAW(8)      := hexToRaw ('0000000000000000');
    c_NOT_OK            INTEGER     := 0;
    c_ALL_RIGHT         INTEGER     := 1;
    c_INSUFFICIENT_RIGHTS INTEGER   := 2;
    c_ALREADY_EXISTS    INTEGER     := 21;
    c_OBJECTNOTFOUND    INTEGER     := 3;
    c_RIGHT_DELETE      INTEGER     := 16;
    -- locals
    l_oid               RAW(8)      := c_NOOID;
    l_rights            INTEGER     := 0;
    l_containerId       RAW(8)      := c_NOOID;
    -- define return values
    l_retValue          INTEGER := c_ALL_RIGHT;
BEGIN
    -- convertions: (OBJECTIDSTRING) - all input object ids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    -- transaction start
    BEGIN
        -- all references and the object itself are deleted (plus rights)
        l_retValue := p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);

        IF (l_retValue = c_ALL_RIGHT)
        THEN
            -- delete all values of object
            DELETE  ibs_Attachment_01
            WHERE   oid = l_oid;
        END	IF;
    END;
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_XMLViewer_01$delete', '');
    RETURN c_NOT_OK;
END p_XMLViewer_01$delete;
/

show errors;
-- p_XMLViewer_01$delete




-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLViewer_01$BOCopy
(
    -- common input parameters:
    ai_oid            RAW,
    ai_userId         INTEGER,
    ai_newOid         RAW
)
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- define constants
    c_NOT_OK            INTEGER     := 0;
    c_ALL_RIGHT         INTEGER     := 1;
    -- define return values
    l_retValue          INTEGER := c_NOT_OK;
    -- define local variables
    l_path              VARCHAR (255);
    l_oid_s             VARCHAR (255);
    l_templateOid       RAW (8);
    l_dbMapped          NUMBER (1);
    l_showDOMTree       NUMBER (1);
    l_procCopy          VARCHAR (30);
    l_procCopyCall      VARCHAR (255);
BEGIN

    p_byteToString (ai_oid, l_oid_s);

    SELECT value || 'upload/files/'
    INTO l_path
    FROM ibs_System
    WHERE name = 'WWW_BASE_PATH';

    BEGIN
        l_retValue := c_ALL_RIGHT;
        -- make an insert for all type specific tables:
        INSERT INTO ibs_Attachment_01
                (oid, filename, url, filesize, path, attachmentType, isMaster)
        SELECT ai_newOid, b.filename, b.url, b.filesize,
                l_path || l_oid_s || '/',
                b.attachmentType, b.isMaster
        FROM    ibs_Attachment_01 b
        WHERE   b.oid = ai_oid;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_retValue := c_NOT_OK;
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,
                                 'p_XMLViewer_01$BOCopy',
                                 'Error in INSERT INTO ibs_Attachment_01');
        RAISE;
    END;

    -- check if insert was performed correctly:
    IF (l_retValue = c_ALL_RIGHT)   -- at least one row affected?
    THEN
        BEGIN
            -- make an insert in the ibs_xmlviewer_01 table
            INSERT INTO ibs_XMLViewer_01
                    (oid, templateOid, workflowTemplateOid)
            SELECT  ai_newOid, templateOid, workflowTemplateOid
            FROM    ibs_XMLViewer_01
            WHERE   oid = ai_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_retValue := c_NOT_OK;
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error,
                                     'p_XMLViewer_01$BOCopy',
                                     'Error in INSERT INTO ibs_XMLViewer_01');
            RAISE;
        END;

        -- check if insert was performed correctly:
        IF (l_retValue = c_ALL_RIGHT)   -- at least one row affected?
        THEN
            -- for db-mapped objects we have to perform a insert in the
            -- mapping table too.
            BEGIN
                SELECT  templateOid
                INTO    l_templateOid
                FROM    ibs_XMLViewer_01
                WHERE   oid = ai_newOid;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    l_retValue := c_NOT_OK;
                WHEN OTHERS THEN
                   ibs_error.log_error ( ibs_error.error,
                                         'p_XMLViewer_01$BOCopy',
                                         'Error in SELECT templateOid');
                RAISE;
            END;

            IF (l_retValue = c_ALL_RIGHT)   -- at least one row affected?
            THEN
                l_dbMapped := 0;
                l_showDOMTree := 0;
                l_procCopy := '';

                BEGIN
                    SELECT  dbMapped, showDOMTree, procCopy
                    INTO    l_dbMapped, l_showDOMTree, l_procCopy
                    FROM    ibs_DocumentTemplate_01
                    WHERE   oid = l_templateOid;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        l_retValue := c_NOT_OK;
                    WHEN OTHERS THEN
                       ibs_error.log_error ( ibs_error.error,
                                             'p_XMLViewer_01$BOCopy',
                                             'Error in SELECT dbMapped');
                    RAISE;
                END;

                -- if the object is db-mapped perform the copy in the mapping table
                -- by calling the type specific copy procedure.
                IF (l_retValue = c_ALL_RIGHT AND l_dbMapped = 1)
                THEN
                    l_procCopyCall :=
                            ' declare l_ret int; begin l_ret := ' ||
                            l_procCopy ||
                            ' (hextoraw(''' || ai_oid || '''), hextoraw(''' ||
                            ai_newOid || ''')); end;';
                    EXEC_SQL (l_procCopyCall);
                END IF;
            END IF;
        END IF;
    END IF;
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,
            'p_XMLViewer_01$BOCopy',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLViewer_01$BOCopy;
/

show errors;

exit;
