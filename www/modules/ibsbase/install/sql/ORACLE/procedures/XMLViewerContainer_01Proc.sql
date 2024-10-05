/******************************************************************************
 * Stored procedures regarding XML ViewerContainer . <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Bernd Buchegger (BB)  990519
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */

CREATE OR REPLACE FUNCTION p_XMLViewerContainer_01$create
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
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- locals
    l_containerId       RAW(8);
    l_linkedObjectId    RAW(8);
    l_oid               RAW(8) := hexToRaw ('0000000000000000');
    l_templateOid       RAW(8) := hexToRaw ('0000000000000000');
    -- define return constants
    c_NOT_OK            INTEGER := 0;
    c_ALL_RIGHT         INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   INTEGER := 2;
    c_ALREADY_EXISTS    INTEGER := 21;
    -- define return values
    l_retValue          INTEGER := c_ALL_RIGHT;
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
            -- insert the other values
            INSERT 
            INTO ibs_XMLViewerContainer_01 (oid, useStandardHeader, 
                                            templateOid, headerFields, 
                                            workflowTemplateOid, workflowAllowed)
            VALUES (l_oid, 1, l_templateOid , '', l_templateOid, 1);
        END IF; -- if object created successfully
    END;
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_XMLViewerContainer_01$create', '');
    RETURN c_NOT_OK;
END p_XMLViewerContainer_01$create;
/

show errors;

CREATE OR REPLACE FUNCTION p_XMLViewerContainer_01$change
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           INTEGER,
    ai_useStandardHeader    NUMBER,
    ai_templateOid_s        VARCHAR2,
    ai_headerFields         VARCHAR2,
    ai_workflowTemplateOid_s VARCHAR2,
    ai_workflowAllowed      NUMBER
)
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- locals
    l_oid               RAW(8) := hexToRaw ('0000000000000000');
    l_templateOid       RAW(8) := hexToRaw ('0000000000000000');
    
    l_workflowTemplateOid RAW(8) := hexToRaw ('0000000000000000');
    -- define return constants
    c_NOT_OK            INTEGER := 0;
    c_ALL_RIGHT         INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   INTEGER := 2;
    c_ALREADY_EXISTS    INTEGER := 21;
    c_OBJECTNOTFOUND    INTEGER := 3;
    -- define return values
    l_retValue          INTEGER := c_ALL_RIGHT;
BEGIN
    -- convertions: (OBJECTIDSTRING) - all input object ids must be converted
    p_stringToByte (ai_templateOid_s, l_templateOid);
    p_stringToByte (ai_workflowTemplateOid_s, l_workflowTemplateOid);

    -- body:
    BEGIN
        -- perform the change of the object:
        l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
                                              ai_validUntil, ai_description,
                                              ai_showInNews, l_oid);

        IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
        THEN
            -- update the other values
            UPDATE  ibs_XMLViewerContainer_01
            SET     useStandardHeader = ai_useStandardHeader,
                    templateOid = l_templateOid,
                    headerFields = ai_headerFields,
                    workflowTemplateOid = l_workflowTemplateOid,
                    workflowAllowed = ai_workflowAllowed
            WHERE   oid = l_oid;
        END IF;
    END;
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_XMLViewerContainer_01$change', '');
    RETURN c_NOT_OK;
END p_XMLViewerContainer_01$change;
/

show errors;


CREATE OR REPLACE FUNCTION p_XMLViewerContainer_01$retr
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
    ao_showInNews           OUT INTEGER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,    
    ao_useStandardHeader    OUT NUMBER,
    ao_templateOid          OUT RAW,
    ao_templateTVersionId   OUT INTEGER,
    ao_headerFields         OUT VARCHAR2,
    ao_templateName         OUT VARCHAR2,
    ao_templateFileName     OUT VARCHAR2,
    ao_templatePath         OUT VARCHAR2,
    ao_workflowTemplateOid  OUT RAW,
    ao_workflowAllowed      OUT NUMBER,
    ao_workflowTemplateName     OUT VARCHAR2,
    ao_workflowTemplateFileName OUT VARCHAR2,
    ao_workflowTemplatePath     OUT VARCHAR2
)
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- define constants
    c_NOOID             RAW(8) := hexToRaw ('0000000000000000');
    c_NOT_OK            INTEGER := 0;
    c_ALL_RIGHT         INTEGER := 1;
    c_INSUFFICIENT_RIGHTS INTEGER := 2;
    c_ALREADY_EXISTS    INTEGER := 21;
    c_OBJECTNOTFOUND    INTEGER := 3;
    -- locals
    l_oid               RAW(8) := c_NOOID;
    -- define return values
    l_retValue          INTEGER := c_ALL_RIGHT;
BEGIN
    -- initialize output variables
    ao_templateOid := c_NOOID;
    ao_templateName := '';
    ao_templateFileName := '';
    ao_templatePath := '';           

    -- Transaction
    BEGIN
        -- retrieve the base object data:
        l_retValue := p_Object$performRetrieve (
                ai_oid_s, ai_userId, ai_op,
                ao_state, ao_tVersionId, ao_typeName, 
                ao_name, ao_containerId, ao_containerName, 
                ao_containerKind, ao_isLink, ao_linkedObjectId, 
                ao_owner, ao_ownerName, 
                ao_creationDate, ao_creator, ao_creatorName,
                ao_lastChanged, ao_changer, ao_changerName,
                ao_validUntil, ao_description, ao_showInNews,
                ao_checkedOut, ao_checkOutDate,
                ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                l_oid);

        IF (l_retValue = c_ALL_RIGHT)
        THEN
            SELECT  useStandardHeader, templateOid, headerFields, 
                    workflowTemplateOid, workflowAllowed
            INTO    ao_useStandardHeader, ao_templateOid, ao_headerFields, 
                    ao_workflowTemplateOid, ao_workflowAllowed
            FROM    ibs_XMLViewerContainer_01
            WHERE   oid = l_oid;
            
            -- check if we have a template 
            IF (ao_templateOid != c_NOOID)
            THEN
                -- get the tVersionId from the document template
                SELECT tVersionId
                INTO   ao_templateTVersionId
                FROM   ibs_DocumentTemplate_01
                WHERE  oid = ao_templateOid;

                -- get the template name and filename
                SELECT  o.name, a.filename, a.path
                INTO    ao_templateName, ao_templateFileName, ao_templatePath
                FROM    ibs_object o, ibs_attachment_01 a
                WHERE   o.oid = ao_templateOid
                AND     a.oid = o.oid;
            END IF;
            -- check if we have a workflow template 
            IF (ao_workflowTemplateOid != c_NOOID)
            THEN
                -- get the template name and filename
                SELECT  o.name, a.filename, a.path
                INTO    ao_workflowTemplateName, ao_workflowTemplateFileName, 
                        ao_workflowTemplatePath
                FROM    ibs_object o, ibs_attachment_01 a
                WHERE   o.oid = ao_workflowTemplateOid
                AND     a.oid = o.oid;
            END IF;
        END IF;
    END;
   COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_XMLViewerContainer_01$retr', '');
    RETURN c_NOT_OK;
END p_XMLViewerContainer_01$retr;
/

show errors;

CREATE OR REPLACE FUNCTION p_XMLViewerContainer_01$delete
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
            DELETE  ibs_XMLViewerContainer_01
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
    ibs_error.log_error ( ibs_error.error, 'p_XMLViewerContainer_01$delete', '');
    RETURN c_NOT_OK;
END p_XMLViewerContainer_01$delete;
/

show errors;

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLViewerContainer_01$BOCopy
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
BEGIN
    -- make an insert for all type specific tables:
    INSERT INTO ibs_XMLViewerContainer_01 
            (oid, useStandardHeader, templateOid, headerFields, 
             workflowTemplateOid, workflowAllowed)
    SELECT  ai_newOid, b.useStandardHeader, b.templateOid, b.headerFields,
            b.workflowTemplateOid, b.workflowAllowed
    FROM    ibs_XMLViewerContainer_01 b
    WHERE   b.oid = ai_oid;

    -- check if insert was performed correctly:
    IF (SQL%ROWCOUNT >= 0)                -- at least one row affected?
    THEN
        l_retValue := c_ALL_RIGHT;  -- set return value
    END IF;

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_XMLViewerContainer_01$BOCopy', '');
    RETURN c_NOT_OK;
END p_XMLViewerContainer_01$BOCopy;
/

show errors;

exit; 
