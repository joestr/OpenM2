--------------------------------------------------------------------------------
-- All stored procedures regarding the XMLViewerContainer_01 Object. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:52 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new XMLViewerContainer_01 Object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be 
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewerContainer_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLViewerContainer_01$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- id of the container
                                        -- object
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA; -- id to where the link
                                        -- shows to
    DECLARE l_templateOid   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET l_templateOid       = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- create base object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting create base object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- if object created successfully
    THEN
        -- insert the other values
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_XMLViewerContainer_01(oid,
                useStandardHeader, templateOid, headerFields,
                workflowTemplateOid, workflowAllowed)
        VALUES  (l_oid, 1, l_templateOid, '', l_templateOid, 1);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in insert the other values';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
    END IF; -- if object created successfully

   
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewerContainer_01$create',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_XMLViewerContainer_01$create


--------------------------------------------------------------------------------
 -- Changes the attributes of an existing object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   @oid_s              ID of the object to be changed.
 -- @param   @userId             ID of the user who is creating the object.
 -- @param   @op                 Operation to be performed (used for rights 
 --                              check).
 -- @param   @name               Name of the object.
 -- @param   @validUntil         Date until which the object is valid.
 -- @param   @description        Description of the object.
 -- @param   @showInNews         the showInNews flag      
 -- @param   @useStandardHeader  Flag to use standard list headers
 -- @param   @templateOid        oid of the documentTemplate to attach
 -- @param   @headerFields       alternative fields to use as header in the list  
 --  
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT               Action performed, values returned, everything ok.
 --  INSUFFICIENT_RIGHTS     User has no right to perform action.
 --  OBJECTNOTFOUND          The required object was not found within the 
 --                          database.


-- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewerContainer_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLViewerContainer_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    -- type-specific attributes:
    IN  ai_useStandardHeader SMALLINT,
    IN  ai_templateOid_s    VARCHAR (18),
    IN  ai_headerFields     VARCHAR (255),
    IN  ai_wftemplateOid_s  VARCHAR (18),
    IN  ai_workflowAllowed  SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_templateOid   CHAR (8) FOR BIT DATA;
    DECLARE l_wfTemplateOid CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET l_templateOid       = c_NOOID;
    SET l_wfTemplateOid     = c_NOOID;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- conversions: (OBJECTIDSTRING) - all input object ids must be converted
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_templateOid_s, l_templateOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting ai_containerId_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_wftemplateOid_s, l_wftemplateOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting ai_wftemplateOid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- perform the change of the object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in perform the change of the object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- update the other values
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_XMLViewerContainer_01
        SET     useStandardHeader = ai_useStandardHeader,
                templateOid = l_templateOid,
                headerFields = ai_headerFields,
                workflowTemplateOid = l_wftemplateOid,
                workflowAllowed = ai_workflowAllowed
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in update the other values';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewerContainer_01$change',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_XMLViewerContainer_01$change

--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the 
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         the showInNews flag     
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
--
-- @param   @useStandardHeader  Flag to use standard list headers
-- @param   @templateOid        oid of the documentTemplate to attach
-- @param   @templateTVersionId tVersionId from the documentTemplate attached
-- @param   @headerFields       alternative fields to use as header in the list  
-- @param   @templateName       name of the template
-- @param   @templateFileName   filename of template
-- @param   @templatePath       path of template
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewerContainer_01$retr');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLViewerContainer_01$retr
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    -- type-specific attributes:
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_useStandardHeader SMALLINT,
    OUT ao_templateOid      CHAR (8) FOR BIT DATA,
    OUT ao_templateTVersionId INT,
    OUT ao_headerFields     VARCHAR (255),
    OUT ao_templateName     VARCHAR (255),
    OUT ao_templateFileName VARCHAR (255),
    OUT ao_templatePath     VARCHAR (255),
    OUT ao_wftemplateOid    CHAR (8) FOR BIT DATA,
    OUT ao_workflowAllowed  SMALLINT,
    OUT ao_wftemplateName   VARCHAR (255),
    OUT ao_wftemplateFileName VARCHAR (255),
    OUT ao_wftemplatePath   VARCHAR (255)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_templateOid      = c_NOOID;
    SET ao_templateName     = '';
    SET ao_templateFileName = '';
    SET ao_templatePath     = '';

    SET ao_wftemplateName   = '';
    SET ao_wftemplateFileName = '';
    SET ao_wftemplatePath   = '';
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve the base object data:

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object$performRetrieve';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        SET l_sqlcode = 0;
        SELECT  useStandardHeader, templateOid, headerFields,
                workflowTemplateOid, workflowAllowed
        INTO    ao_useStandardHeader, ao_templateOid, ao_headerFields,
                ao_wftemplateOid, ao_workflowAllowed
        FROM    IBSDEV1.ibs_XMLViewerContainer_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT statement';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- check if we have a template
        IF (ao_templateOid <> c_NOOID)
        THEN

            -- get the tVersionId from the template
            SET l_sqlcode = 0;
            SELECT  tVersionID
            INTO    ao_templateTVersionId
            FROM    IBSDEV1.ibs_DocumentTemplate_01
            WHERE   oid = ao_templateOid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in get the tVersionId from the template';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- get the template name and filename
            SET l_sqlcode = 0;
            SELECT  o.name, a.filename
            INTO    ao_templateName, ao_templateFileName, ao_templatePath
            FROM    IBSDEV1.ibs_Object AS o, IBSDEV1.ibs_Attachment_01 AS a
            WHERE   o.oid = ao_templateOid AND a.oid = o.oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in get the template name and filename';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        END IF;

        -- check if we have a workflow template
        IF (ao_wftemplateOid <> c_NOOID)
        THEN
            -- get the template name and filename
            SET l_sqlcode = 0;
            SELECT  o1.name, a1.filename, a1.path
            INTO    ao_wftemplateName, ao_wftemplateFileName, ao_wftemplatePath
            FROM    IBSDEV1.ibs_Object AS o1,
                    IBSDEV1.ibs_Attachment_01 AS a1
            WHERE   o1.oid = ao_wftemplateOid AND a1.oid = o1.oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in get the template name and filename 2';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        END IF;
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewerContainer_01$retr',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_XMLViewerContainer_01$retr

-------------------------------------------------------------------------------
-- Deletes a XMLViewerContainer_01 object and all its values
-- (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
-- 
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewerContainer_01$delete');

-- create the new procedure
CREATE PROCEDURE IBSDEV1.p_XMLViewerContainer_01$delete
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_oid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- all references and the object itself are deleted (plus rights)
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object$performDelete';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- delete all values of object
        SET l_sqlcode = 0;
        DELETE  FROM IBSDEV1.ibs_XMLViewerContainer_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in DELETE statement';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewerContainer_01$delete',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_XMLViewerContainer_01$delete


--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
-- 
-- @input parameters:
-- @param   @oid              ID of the object to be copied.
-- @param   @userId           ID of the user who copy the object.
-- @param   @newOid           The new Oid of the new created BusinessObject.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewerContainer_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLViewerContainer_01$BOCopy
(
    -- common input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_newOid           CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rowcount      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- make an insert for all type specific tables:
    SET l_sqlcode = 0;
    INSERT  INTO IBSDEV1.ibs_XMLViewerContainer_01(oid, useStandardHeader,
            templateOid, headerFields, workflowTemplateOid, workflowAllowed)
    SELECT  ai_newOid, b.useStandardHeader, b.templateOid, b.headerFields,
            b.workflowTemplateOid, b.workflowAllowed
    FROM    IBSDEV1.ibs_XMLViewerContainer_01 AS b
    WHERE   b.oid = ai_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in INSERT statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    -- check if insert was performed correctly:
    IF (l_rowcount >= 1)
    THEN
        -- at least one row affected?
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewerContainer_01$BOCopy',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_XMLViewerContainer_01$BOCopy