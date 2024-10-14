-------------------------------------------------------------------------------
-- All stored procedures regarding the XMLViewer_01 Object. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:52 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
 -- Creates a new XMLViewer_01 Object (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_XMLViewer_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLViewer_01$create
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
    DECLARE l_path          VARCHAR (255);
    DECLARE l_containerTVersionId INT;
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
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- conversions (objectidstring) - all input objectids must be converted
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
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- if object created successfully
    THEN
        SET l_sqlcode = 0;
        SELECT  value || 'upload/files/'
        INTO    l_path
        FROM    IBSDEV1.ibs_System
        WHERE   name = 'WWW_BASE_PATH';

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100) -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in converting ai_linkedObjectId_s';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- insert the other values
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_Attachment_01(oid, filename, url, filesize,
                path, attachmentType, isMaster)
        VALUES  (l_oid, 'xmldata.xml', '', 1, l_path || ao_oid_s || '/', 3, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in insert the other values';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- get the template and workflow oid associated with the form.
        -- for old forms (tVersionId == XMLViewer_01) this values are set in Java.
        -- for new forms we can do this now.
        IF (ai_tVersionId <> 16872705)  -- is a new form with its own tVersionId
        THEN
            SET l_sqlcode = 0;
            SELECT  oid, workflowTemplateOid
            INTO    l_templateOid, l_wfTemplateOid
            FROM    IBSDEV1.ibs_DocumentTemplate_01
            WHERE   tVersionID = ai_tVersionId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in SELECT statement';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        END IF; -- is a new form with its own tVersionId

        -- if no workflow template is defined in the document template
        -- and the container is a XMLViewerContainer_01
        -- get the workflow template from the container.
        IF (l_wfTemplateOid = c_NOOID)
        THEN
            -- get the tVersionId of the container object
            SET l_sqlcode = 0;
            SELECT  tVersionId
            INTO    l_containerTVersionId
            FROM    IBSDEV1.ibs_Object
            WHERE   oid = l_containerId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in get the tVersionId';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- if the container is a XMLViewerContainer_01 or a ServicePoint_01
            -- get the workflow oid defined by the container.
            IF ((l_containerTVersionId = 16875009) OR
                (l_containerTVersionId = 16843153))
            THEN
                SET l_sqlcode = 0;
                SELECT  workflowTemplateOid
                INTO    l_wfTemplateOid
                FROM    IBSDEV1.ibs_XMLViewerContainer_01
                WHERE   oid = l_containerId;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0)         -- any exception?
                THEN
                    -- create error entry:
                    SET l_ePos = 'Error in get the workflowTemplateOid';
                    GOTO exception1;        -- call common exception handler
                END IF; -- if any exception
            END IF;
        END IF;

        -- insert the template and workflow oid in the ibs_xmlviewer_01 table
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_XMLViewer_01(oid, templateOid,
                workflowTemplateOid)
        VALUES  (l_oid, l_templateOid, l_wfTemplateOid);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in insert ibs_XMLViewer_01';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

    END IF; -- if object created successfully

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewer_01$create',
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
-- p_XMLViewer_01$create


-------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         Should the currrent object 
--                              displayed in the news.
-- @param   @templateOid        oid of the document template object
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewer_01$change');

-- create the new procedure
CREATE PROCEDURE IBSDEV1.p_XMLViewer_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    -- xml viewer specific input parameters:
    IN  ai_templateOid_s    VARCHAR (18),
    IN  ai_wfTemplateOid_s  VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rowCount      INT;
    DECLARE l_name          VARCHAR(63);
    DECLARE l_nameLong      VARCHAR(100);
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

    -- perform the change of the object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$change(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil,ai_description, ai_showInNews);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in perform the change of the object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- object changed successfully?
    THEN
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
        CALL IBSDEV1.p_stringToByte (ai_templateOid_s, l_templateOid);
        CALL IBSDEV1.p_stringToByte (ai_wfTemplateOid_s, l_wfTemplateOid);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in converting';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- insert the template oid in the ibs_xmlviewer_01 table
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_XMLViewer_01
        SET     templateOid = l_templateOid,
                workflowTemplateOid = l_wfTemplateOid
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in insert the template oid';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        IF (l_rowcount <= 0)
        THEN
            SET l_retValue = c_OBJECTNOTFOUND;
        END IF;

    END IF; -- object changed successfully?

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewer_01$change',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '',
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
-- p_XMLViewer_01$change

-------------------------------------------------------------------------------
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
-- @param   @showInNews         Display the object in the news.
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
-- @param   @ao_templateOid     the oid of the template object
-- @param   @ao_systemDisplayMode  the display mode for the system section
-- @param   @ao_dbMapped        the dbMapped attribute from the template object
-- @param   @ao_mappingTable    the name of the mapping table 
--                              from the template object
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.

-- delete procedure:
CALL IBSDEV1.p_dropProc ('p_XMLViewer_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLViewer_01$retrieve
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
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_templateOid      CHAR (8) FOR BIT DATA,
    OUT ao_wfTemplateOid    CHAR (8) FOR BIT DATA,
    OUT ao_systemDisplayMode INT,
    OUT ao_dbMapped         SMALLINT,
    OUT ao_showDOMTree      SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
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
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve the base object data:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performRetrieve(
        ai_oid_s, ai_userId, ai_op, ao_state, ao_tVersionId, ao_typeName,
        ao_name, ao_containerId, ao_containerName, ao_containerKind,
        ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate,
        ao_creator, ao_creatorName, ao_lastChanged, ao_changer, ao_changerName,
        ao_validUntil, ao_description, ao_showInNews, ao_checkedOut,
        ao_checkOutDate, ao_checkOutUser, ao_checkOutUserOid,
        ao_checkOutUserName, l_oid );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in retrieve the base object data';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in retrieve the base object data';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- get the template oid and the mapping attributes from the template
        SET l_sqlcode = 0;
        SELECT  v.templateOid, v.workflowTemplateOid, t.systemDisplayMode,
                t.dbMapped, t.showDOMTree
        INTO    ao_templateOid, ao_wfTemplateOid, ao_systemDisplayMode,
                ao_dbMapped, ao_showDOMTree
        FROM    IBSDEV1.ibs_XMLViewer_01 AS v,
                IBSDEV1.ibs_DocumentTemplate_01 AS t
        WHERE   v.oid = l_oid AND t.oid = v.templateOid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in get template oid and mapping attributes';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewer_01$change',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '',
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
-- p_XMLViewer_01$retrieve

-------------------------------------------------------------------------------
-- Deletes a XMLViewer_01 object and all its values (incl. rights check). <BR>
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

-- Delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewer_01$delete');

-- Create the new perocedure
CREATE PROCEDURE IBSDEV1.p_XMLViewer_01$delete
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
    DECLARE l_rights        INT;
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
    SET l_rights            = 0;
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
        DELETE  FROM IBSDEV1.ibs_Attachment_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in DELETE statement';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewer_01$delete',
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
-- p_XMLViewer_01$delete



-- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLViewer_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLViewer_01$BOCopy
(
    -- common input parameters:
    IN ai_oid               CHAR (8) FOR BIT DATA,
    IN ai_userId            INT,
    IN ai_newOid            CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_path          VARCHAR (255);
    DECLARE l_oid_s         VARCHAR (18);
    DECLARE l_templateOid   CHAR (8) FOR BIT DATA;
    DECLARE l_dbMapped      SMALLINT;
    DECLARE l_showDOMTree   SMALLINT;
    DECLARE l_procCopy      VARCHAR (30);
    DECLARE l_procCall      VARCHAR (255);
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
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- conversionS (OBJECTID)
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_byteToString (ai_oid, l_oid_s);


    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_oid';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    SELECT  value || 'upload/files/' AS path
    INTO    l_path
    FROM    IBSDEV1.ibs_System
    WHERE   name = 'WWW_BASE_PATH';

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- make an insert for all type specific tables:
    SET l_sqlcode = 0;
    INSERT  INTO IBSDEV1.ibs_Attachment_01(oid, filename, url, filesize,
            path, attachmentType, isMaster)
    SELECT  ai_newOid, b.filename, b.url, b.filesize,
            l_path || l_oid_s || '/', b.attachmentType, b.isMaster
    FROM    IBSDEV1.ibs_Attachment_01 AS b
    WHERE   b.oid = ai_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in make an insert for all type specific tables';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    -- check if insert was performed correctly:
    IF (l_rowcount >= 1)                -- at least one row affected?
    THEN
        -- make an insert in the ibs_xmlviewer_01 table
        SET l_sqlcode = 0;
        INSERT INTO IBSDEV1.ibs_XMLViewer_01(oid, templateOid)
        SELECT ai_newOid, templateOid
        FROM IBSDEV1.ibs_XMLViewer_01
        WHERE oid = ai_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in make an insert in ibs_xmlviewer_01 table';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        -- check if insert was performed correctly:
        IF (l_rowcount >= 1)            -- at least one row affected?
        THEN
            -- for db-mapped objects we have to perform a insert in the
            -- mapping table too.
            SET l_sqlcode = 0;
            SELECT  templateOid
            INTO    l_templateOid
            FROM    IBSDEV1.ibs_XMLViewer_01
            WHERE   oid = ai_newOid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in SELECT l_templateOid';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            IF (l_sqlcode = 0)
            THEN
                SET l_sqlcode = 0;
                SET l_dbMapped = 0;
                SET l_showDOMTree = 0;
                SET l_procCopy = '';

                SELECT  dbMapped, showDOMTree, procCopy
                INTO    l_dbMapped, l_showDOMTree, l_procCopy
                FROM    IBSDEV1.ibs_DocumentTemplate_01
                WHERE   oid = l_templateOid;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0)     -- any exception?
                THEN
                    -- create error entry:
                    SET l_ePos = 'Error in SELECT l_procCopy';
                    GOTO exception1;    -- call common exception handler
                END IF; -- if any exception

                -- if the object is db-mapped perform the copy in the mapping
                -- table by calling the type specific copy procedure.
                IF ((l_sqlcode = 0) AND (l_dbMapped = 1))
                THEN
                    SET l_sqlcode = 0;
                    SET l_procCall = 'CALL IBSDEV1.' || l_procCopy ||
                        '(' || ai_oid || ', ' || ai_newoid || ' );' ;
                    EXECUTE IMMEDIATE l_procCall;

                    -- check if there occurred an error:
                    IF (l_sqlcode <> 0) -- any exception?
                    THEN
                        -- create error entry:
                        SET l_ePos = 'Error in dynamic SQL';
                        GOTO exception1; -- call common exception handler
                    END IF; -- if any exception
                END IF;
            END IF;
        END IF;
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_XMLViewer_01$BOCopy',
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
-- p_XMLViewer_01$BOCopy





