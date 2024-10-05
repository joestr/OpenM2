-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_attachment_01 table. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:47 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020807
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Attachment_01Proc is splited into Attachment_01Proc1 and Attachment_01Proc2
-- because of a cyclic-dependency. <BR>
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @ai_userId              ID of the user who is creating the object.
-- @param   @ai_op                  Operation to be performed (used for rights 
--                                  check).
-- @param   @ai_tVersionId          Type of the new object.
-- @param   @ai_name                Name of the object.
-- @param   @ai_containerId_s       ID of the container where object shall be 
--                                  created in.
-- @param   @ai_containerKind       Kind of object/container relationship
-- @param   @ai_isLink              Defines if the object is a link
-- @param   @ai_linkedObjectId_s    If the object is a link this is the ID of
--                                  the where the link shows to.
-- @param   @ai_description         Description of the object.
--
-- @output parameters:
-- @param   @ao_oid_s               OID of the newly created object.
--
-- @returns A value representing the state of the procedure.
--  @c_ALL_RIGHT                    Action performed, values returned,
--                                  everything ok.
--  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
--  @c_NOT_OK                       Something went wrong.
-------------------------------------------------------------------------------
-- delete existing procedure: 
CALL IBSDEV1.p_dropProc ('p_Attachment_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Attachment_01$create
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
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_MAS_FILE      INT DEFAULT 1;  -- file Attachment
    DECLARE c_MAS_HYPERLINK INT DEFAULT 2;  -- hyperlink Attachment

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_name          VARCHAR(63);
    DECLARE l_nameLong      VARCHAR(100);
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- id of the container
                                        -- object
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA; -- id to where the link
                                        -- shows to
    DECLARE l_attachmentFor VARCHAR (255) DEFAULT ''; -- text which contains
                                        -- the name of the owner object
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
    SET l_containerId       = c_NOOID;
    SET l_linkedObjectId    = c_NOOID;
    SET l_name              = ai_name;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- CONVERSIONS (OBJECTIDSTRING) 
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting linkedObjectId_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting containerId_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get value of token 'TOK_ATTACHMENTFOR'
    SET l_sqlcode = 0;
    SELECT  value
    INTO    l_attachmentFor
    FROM    IBSDEV1.ibs_Token_01
    WHERE   name = 'TOK_ATTACHMENT_FOR';

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT value';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    --  The defaultname is set only when it is no File or Url Type 
    IF (ai_tVersionId = 16842833)
    THEN 
        SET l_sqlcode = 0;
        SELECT  l_attachmentFor || ' ' || name
        INTO    l_namelong
        FROM    IBSDEV1.ibs_object
        WHERE   oid IN
                (
                    SELECT containerId 
                    FROM IBSDEV1.ibs_Object
                    WHERE oid = l_containerId
                );

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in get Name out of DB';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        SET l_sqlcode = 0;
        SET l_name = SUBSTR( l_namelong, 1, 63 );

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in cutting the nameLong to 63';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- if is abject an attachment
  
    -- p_Object$performCreate will set l_retValue to c_ALL_RIGHT,
    -- c_INSUFFICIENT_RIGHTS or c_NOT_OK
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        l_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in performCreate of Object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT         -- if object created
    THEN 
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_Attachment_01
                (oid, filename, path, filesize, url, attachmentType, isMaster)
        VALUES  (l_oid, '', '', 0.0,'', 1, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in INSERT - Statement';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- if object created
  
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Attachment_01$create',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_containerId_s', ai_containerId_s,
        'ai_op', ai_op, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_tVersionId', ai_tVersionId, 'ai_description', ai_description,
        'ai_containerKind', ai_containerKind, '', '',
        'ai_isLink', ai_isLink, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Attachment_01$create

-------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @ai_oid_s               ID of the object to be changed.
-- @param   @ai_userId              ID of the user who is creating the object.
-- @param   @ai_op                  Operation to be performed (used for rights
--                                  check).
-- @param   @ai_name                Name of the object.
-- @param   @ai_validUntil          Date until which the object is valid.
-- @param   @ai_description         Description of the object.
-- @param   @ai_showInNews          Display object in the news.
--
-- @param   @ai_isMaster            Is true if the attachment is a master.
-- @param   @ai_attachmentType      Is the type of the attachment.
-- @param   @ai_filename            The filename of the attachment.
-- @param   @ai_path                The path of the attachment.
-- @param   @ai_url                 The Hyperlink of the attachment.
-- @param   @ai_filesize            The size of the attachment.
-- @param   @ai_isWeblink           Is true if the flag 32 is set (flag 32 is
--                                  set when the attachment is a weblink).
--
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  @c_ALL_RIGHT                    Action performed, values returned,
--                                  everything ok.
--  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
--  @c_OBJECTNOTFOUND               The required object was not found within
--                                  the database.
-------------------------------------------------------------------------------
-- delete existing procedure: 
CALL IBSDEV1.p_dropProc ('p_Attachment_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Attachment_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_isMaster         SMALLINT,
    IN  ai_attachmentType   INT,
    IN  ai_filename         VARCHAR (255),
    IN  ai_path             VARCHAR (255),
    IN  ai_url              VARCHAR (255),
    IN  ai_filesize         REAL,
    IN  ai_isWeblink        SMALLINT
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
    DECLARE c_MAS_FILE      INT DEFAULT 1;  -- file Attachment
    DECLARE c_MAS_HYPERLINK INT DEFAULT 2;  -- hyperlink Attachment
    DECLARE c_WEBLINK       INT DEFAULT 32;  -- weblink

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of function
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_tVersionId    INT;
    DECLARE l_documentId    CHAR (8) FOR BIT DATA; -- id of the owner document
    DECLARE l_masterId      CHAR (8) FOR BIT DATA DEFAULT NULL;
    DECLARE l_isMaster      SMALLINT;
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
    SET l_documentId        = c_NOOID;
    SET l_isMaster          = ai_isMaster;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform the change of the object:
    -- p_Object$performCreate will set @l_retValue to c_ALL_RIGHT,
    -- c_INSUFFICIENT_RIGHTS or c_OBJECTNOTFOUND
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed ?
    THEN
        SET l_sqlcode = 0;

        SELECT  containerId, tVersionId
        INTO    l_containerId, l_tVersionId
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT containerId, tVersionId';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception

        UPDATE  IBSDEV1. ibs_Attachment_01
        SET     attachmentType = ai_attachmentType,
                filename = ai_filename,
                path = ai_path,
                url = ai_url,
                filesize = ai_filesize
        WHERE   oid = l_oid;
    
        -- updates the flags of the object if it is a weblink
        IF (ai_isWeblink = 1)           -- is the object a weblink ?
        THEN            
            -- UPDATE IBSDEV1.s the flags of the object
            SET l_sqlcode = 0;

            UPDATE  IBSDEV1.ibs_object -- set the selected property isWeblink
            SET     flags = IBSDEV1.b_OR(flags, c_WEBLINK)
            WHERE   oid = l_oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in UPDATE ibs_object';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        ELSE                            -- the object is not a weblink
            -- updates the flags of the object
            SET l_sqlcode = 0;

            UPDATE  IBSDEV1.ibs_object -- delete the selected property
                                        -- isWeblink
            SET     flags = IBSDEV1.b_AND(
                    flags, IBSDEV1.b_XOR(2147483647, c_WEBLINK)) -- 0x7FFFFFFF
            WHERE   oid = l_oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in UPDATE ibs_object';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        END IF; -- is the object a weblink ?

        IF (l_tVersionId = 16842833)    -- is it an attachment ?
        THEN
            IF ai_isMaster = 1          -- the object is a master
            THEN         
                SET l_masterId = l_oid;
            END IF; -- the object is a master

            -- ensures that in the attachment container is a master set
            CALL IBSDEV1.p_Attachment_01$ensureMaster(l_containerId,
                l_masterId);
        ELSE -- if is it a attachment
            IF (l_tVersionId <> 16842833)
                                        -- not master or not simple ATTACHMENT
                                        -- if object if FILE or HYPERLINK

            THEN 
                -- set the flags to zero
                SET l_sqlcode = 0;

                UPDATE  IBSDEV1.  ibs_object
                SET     flags = IBSDEV1.b_AND(flags, (2147483647
                        - c_MAS_FILE - c_MAS_HYPERLINK)) -- 0x7FFFFFFF
                WHERE   oid = l_oid;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0)     -- any exception?
                THEN
                    -- create error entry:
                    SET l_ePos = 'Error in set flags to 0';
                    GOTO exception1;    -- call common exception handler
                END IF; -- if any exception

                -- FILE
                IF  ((ai_filename IS NOT NULL AND ai_filename <> ' ') 
                    AND ai_attachmentType = c_MAS_FILE)
                    -- if filename NOT NULL and attachmentType = FILE
                THEN
                    SET l_sqlcode = 0;

                    -- set the selected property isMaster
                    UPDATE  IBSDEV1.ibs_object 
                    SET     flags = IBSDEV1.b_OR(flags, c_MAS_FILE)
                    WHERE   oid = l_oid;

                    -- check if there occurred an error:
                    IF (l_sqlcode <> 0) -- any exception?
                    THEN
                        -- create error entry:
                        SET l_ePos = 'Error in set flags for FILE 2';
                        GOTO exception1; -- call common exception handler
                    END IF; -- if any exception
                END IF; -- if filename NOT NULL and attachmentType = FILE
                -- HYPERLINK
                IF  ((ai_url IS NOT NULL AND ai_url <> ' ')
                    AND ai_attachmentType = c_MAS_HYPERLINK)
                     -- IF url NOT NULL and attachmentType = Hyperlink
                THEN
                    -- set the selected property isMaster
                    SET l_sqlcode = 0;

                    UPDATE  IBSDEV1.ibs_object 
                    SET     flags = IBSDEV1.b_OR(flags, c_MAS_HYPERLINK)
                    WHERE   oid = l_oid;

                    -- check if there occurred an error:
                    IF (l_sqlcode <> 0) -- any exception?
                    THEN
                        -- create error entry:
                        SET l_ePos = 'Error in set flags for HYPERLINK';
                        GOTO exception1; -- call common exception handler
                    END IF; -- if any exception
                END IF; -- IF url NOT NULL and attachmentType = Hyperlink
            END IF; -- if object if FILE or HYPERLINK
        END IF;  -- if is it not a attachment
    END IF; -- operation properly performed ?

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Attachment_01$change',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ai_name', ai_name,
        'ai_validUntil', 0, 'ai_description', ai_description,
        'ai_isMaster', ai_isMaster, 'ai_filename', ai_filename,
        'ai_attachmentType', ai_attachmentType, 'ai_path', ai_path,
        'ai_filesize', 0, 'ai_url', ai_url,
        'ai_showInNews', ai_showInNews, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Attachment_01$change


-------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @ai_oid_s               ID of the object to be changed.
-- @param   @ai_userId              ID of the user who is creating the object.
-- @param   @ai_op                  Operation to be performed (used for rights 
--                                  check).
--
-- @output parameters:
-- @param   @ao_state               The object's state.
-- @param   @ao_tVersionId          ID of the object's type (correct version).
-- @param   @ao_typeName            Name of the object's type.
-- @param   @ao_name                Name of the object itself.
-- @param   @ao_containerId         ID of the object's container.
-- @param   @ao_containerName       Name of the object's container.
-- @param   @ao_containerKind       Kind of object/container relationship.
-- @param   @ao_isLink              Is the object a link?
-- @param   @ao_linkedObjectId      Link if isLink is true.
-- @param   @ao_owner               ID of the owner of the object.
-- @param   @ao_ownerName           Name of the owner of the object.
-- @param   @ao_creationDate        Date when the object was created.
-- @param   @ao_creator             ID of person who created the object.
-- @param   @ao_creatorName         Name of person who created the object.
-- @param   @ao_lastChanged         Date of the last change of the object.
-- @param   @ao_changer             ID of person who did the last change to 
--                                  the object.
-- @param   @ao_changerName         Nameof person who did the last change to
--                                  the object.
-- @param   @ao_validUntil          Date until which the object is valid.
-- @param   @ao_description         Description of the object.
-- @param   @ao_showInNews          Display the object in the news.
-- @param   @ao_checkedOut          Is the object checked out?
-- @param   @ao_checkOutDate        Date when the object was checked out.
-- @param   @ao_checkOutUser        ID of the user which checked out the object
-- @param   @ao_checkOutUserOid     Oid of the user which checked out the object
--                                  is only set if this user has the right to
--                                  READ the checkOut user.
-- @param   @ao_checkOutUserName    Name of the user which checked out the
--                                  object, is only set if this user has the
--                                  right to view the checkOut-User.
--
-- @param   @ao_isMaster            Is true if the attachment is a master.
-- @param   @ao_attachmentType      Is the type of the attachment.
-- @param   @ao_filename            The filename of the attachment.
-- @param   @ao_path                The path of the attachment.
-- @param   @ao_url                 The Hyperlink of the attachment.
-- @param   @ao_filesize            The size of the attachment.
-- @param   @ao_isWeblink           Is true if the flag 32 is set (flag 32 is
--                                  set when the attachment is a weblink).
--
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  @c_ALL_RIGHT                    Action performed, values returned,
--                                  everything ok.
--  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
--  @c_OBJECTNOTFOUND               The required object was not found within
--                                  the database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Attachment_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Attachment_01$retrieve
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
    OUT ao_isMaster         SMALLINT,
    OUT ao_attachmentType   INT,
    OUT ao_filename         VARCHAR (255),
    OUT ao_path             VARCHAR (255),
    OUT ao_url              VARCHAR (255),
    OUT ao_filesize         REAL,
    OUT ao_isWeblink        SMALLINT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_MAS_INNEWS    INT DEFAULT 4;
    DECLARE c_WEBLINK       INT DEFAULT 32;  -- weblink

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- converted input parameter
                                            -- oid_s
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
  
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve the base object data:
    -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
    -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed ?
    THEN        
        SET l_sqlcode = 0;

        SELECT  isMaster, attachmentType, filename, path, url, filesize
        INTO    ao_isMaster, ao_attachmentType, ao_filename, ao_path, ao_url,
                ao_filesize
        FROM    IBSDEV1.ibs_Attachment_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0) -- any exception?
        THEN
             -- create error entry:
             SET l_ePos = 'Select from ibs_attachment_01';
             GOTO exception1; -- call common exception handler
        END IF; -- if any exception

        -- get the 6th bit out of the flags
        -- it is set, when the object is a weblink
        SET l_sqlcode = 0;

        SELECT  IBSDEV1.b_AND(flags, c_WEBLINK)
        INTO    ao_isWeblink
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0) -- any exception?
        THEN
             -- create error entry:
             SET l_ePos = 'Error in SELECT flags';
             GOTO exception1; -- call common exception handler
        END IF; -- if any exception
    END IF;
  
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Attachment_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, '', '',
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
-- p_Attachment_01$retrieve


-------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
-- 
-- @input parameters:
-- @param   @ai_oid_s               ID of the object to be deleted.
-- @param   @ai_userId              ID of the user who is deleting the object.
-- @param   @ai_op                  Operation to be performed (used for rights
--                                  check).
--
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  @c_ALL_RIGHT                    Action performed, values returned,
--                                  everything ok.
--  @c_INSUFFICIENT_RIGHTS          User has no right to perform action.
--  @c_OBJECTNOTFOUND               The required object was not found within
--                                  the database.
-------------------------------------------------------------------------------
-- delete existing procedure: 
CALL IBSDEV1.p_dropProc ('p_Attachment_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Attachment_01$delete
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;
    DECLARE c_MAS_FILE      INT DEFAULT 1;
    DECLARE c_MAS_HYPERLINK INT DEFAULT 1;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this procedure    
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- container Id of the
                                        -- object
    DECLARE l_tVersionId    INT;        -- tVersion Id of the object
    DECLARE l_isMaster      SMALLINT DEFAULT 0; -- converted input parameter
                                        -- @ai_isMaster
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- converted input parameter
                                        -- @ai_oid_s
    DECLARE l_documentId    CHAR (8) FOR BIT DATA;
    DECLARE l_dummy         CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_ALL_RIGHT         = 1;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET l_documentId        = c_NOOID;
  
-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
  
    SET l_sqlcode = 0;

    SELECT  tVersionId, containerId
    INTO    l_tVersionId, l_containerId
    FROM    IBSDEV1.ibs_object
    WHERE   oid = l_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0) -- any exception?
    THEN
         -- create error entry:
         SET l_ePos = 'Error in SELECT tVersionId, containerId';
         GOTO exception1; -- call common exception handler
    END IF; -- if any exception
  
    -- all references and the object itself are deleted (plus rights)
    -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
    -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_dummy);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed ?
    THEN        
        SET l_sqlcode = 0;

        SELECT isMaster
        INTO l_isMaster
        FROM IBSDEV1.ibs_Attachment_01
        WHERE oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0) -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT l_isMaster';
            GOTO exception1; -- call common exception handler
        END IF; -- if any exception

        -- if we delete a master attachment, we must find a other one
        IF ((l_isMaster = 1) AND (l_tVersionId = 16842833))
        THEN 
            CALL IBSDEV1.p_Attachment_01$ensureMaster(l_containerId, NULL);
        END IF;
    END IF;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0) -- any exception?
    THEN
         -- create error entry:
         SET l_ePos = '';
         GOTO exception1; -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value
    RETURN l_retValue;
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Attachment_01$delete',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, '', '',
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
-- p_Attachment_01$delete


-------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
-- 
-- @input parameters:
-- @param   @ai_oid                 ID of the object to be copied.
-- @param   @ai_userId              ID of the user who copy the object.
-- @param   @ai_newOid              The new Oid of the new created Business
--                                  Object.
--
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  @c_ALL_RIGHT                    Action performed, values returned,
--                                  everything ok.
--  @c_NOT_OK                       Something went wrong.
-------------------------------------------------------------------------------
 
-- delete existing procedure: 
CALL IBSDEV1.p_dropProc ('p_Attachment_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Attachment_01$BOCopy
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,-- the oid of Object we want
                                        -- to copy
    IN  ai_userId           INT,        -- the userId of the User who wants
                                        -- to copy
    IN  ai_newOid           CHAR (8) FOR BIT DATA -- the new OID of the copied
                                        -- Object
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
   SET l_retValue = c_NOT_OK;
-- body:
----***************************************************************************
-- Hier sollte normalerweise ein Überprüfung erfollgen, ob die Beilage sich in
-- einem Container befindet, wo es schon einen Master gibt, wenn nicht, dann
-- sollte dieser natürlich gesetzt werden, und ebenfalls sollten die flags in
-- der Tabelle ibs_Object richtig gesetzt werden.
-- Ist zurzeit als HACK in p_Object$copy mit der StoredProzedure 
-- p_Attachment_01$ensureMaster gelöst.  DJ 24. August 2000
----***************************************************************************

    -- make a insert for all your typespecific tables:
    SET l_sqlcode = 0;

    INSERT  INTO ibs_Attachment_01(oid, filename, path, filesize, url,
            attachmentType, isMaster)
    SELECT  ai_newOid, filename, path, filesize, url, attachmentType, isMaster
    FROM    IBSDEV1.ibs_Attachment_01
    WHERE   oid = ai_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0) -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in make a insert for all your typespecific tables:';
        GOTO exception1; -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    IF l_rowcount >= 1 THEN 
        SET l_retvalue = c_ALL_RIGHT;
    ELSE
        -- create error entry:
        SET l_ePos = 'No row inserted in make a insert for all your typespecific tables:';
        GOTO exception1; -- call common exception handler
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Attachment_01$BOCopy',
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
-- p_Attachment_01$BOCopy
