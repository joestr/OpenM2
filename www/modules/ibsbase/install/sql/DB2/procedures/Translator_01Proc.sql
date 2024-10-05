--------------------------------------------------------------------------------
 -- All stored procedures regarding a Translator_01 object. <BR>
 --
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
 --
 -- author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
 -- Gets all data from a given object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   ai_oid_s            ID of the object to be changed.
 -- @param   ai_userId           ID of the user who is creating the object.
 -- @param   ai_op               Operation to be performed (used for rights
 --                              check).
 --
 -- @output parameters:
 -- @param   ao_state            The object's state.
 -- @param   ao_tVersionId       ID of the object's type (correct version).
 -- @param   ao_typeName         Name of the object's type.
 -- @param   ao_name             Name of the object itself.
 -- @param   ao_containerId      ID of the object's container.
 -- @param   ao_containerName    Name of the object's container.
 -- @param   ao_containerKind    Kind of object/container relationship.
 -- @param   ao_isLink           Is the object a link?
 -- @param   ao_linkedObjectId   Link if isLink is true.
 -- @param   ao_owner            ID of the owner of the object.
 -- @param   ao_ownerName        Name of the owner of the object.
 -- @param   ao_creationDate     Date when the object was created.
 -- @param   ao_creator          ID of person who created the object.
 -- @param   ao_creatorName      Name of person who created the object.
 -- @param   ao_lastChanged      Date of the last change of the object.
 -- @param   ao_changer          ID of person who did the last change to the
 --                              object.
 -- @param   ao_changerName      Nameof person who did the last change to
 --                              the object.
 -- @param   ao_validUntil       Date until which the object is valid.
 -- @param   ao_description      Description of the object.
 -- @param   ao_showInNews       Display the object in the news.
 -- @param   ao_checkedOut       Is the object checked out?
 -- @param   ao_checkOutDate     Date when the object was checked out.
 -- @param   ao_checkOutUser     ID of the user which checked out the object
 -- @param   ao_checkOutUserOid  Oid of the user which checked out the object
 --                              is only set if this user has the right to
 --                              READ the checkOut user.
 -- @param   ao_checkOutUserName Name of the user which checked out the
 --                              object, is only set if this user has the
 --                              right to view the checkOut-User.
 --
 -- @param   ao_isMaster         Is true if the attachment is a master.
 -- @param   ao_attachmentType   Is the type of the attachment.
 -- @param   ao_filename         The filename of the attachment.
 -- @param   ao_path             The path of the attachment.
 -- @param   ao_url              The Hyperlink of the attachment.
 -- @param   ao_filesize         The size of the attachment.
 -- @param   ao_isWeblink        Is true if the flag 32 is set (flag 32 is
 --                              set when the attachment is a weblink).
 --
 -- @param   ao_extension        The extension of the generated output file.
 --
 -- @return  A value representing the state of the procedure.
 -- c_ALL_RIGHT              Action performed, values returned, everything ok.
 -- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 -- c_OBJECTNOTFOUND         The required object was not found within the
 --                          database.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Translator_01$retrieve');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Translator_01$retrieve
(
    -- input parameters:
    IN ai_oid_s                VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT,
    -- output parameters:
    OUT ao_state            INT,
    OUT ao_tVersionId        INT,
    OUT ao_typeName        VARCHAR (63),
    OUT ao_name            VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink            SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator        INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer        INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil        TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews        SMALLINT,
    OUT ao_checkedOut        SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    -- attachment-specific output parameters:
    OUT ao_isMaster        SMALLINT,
    OUT ao_attachmentType   INT,
    OUT ao_filename        VARCHAR (255),
    OUT ao_path            VARCHAR (255),
    OUT ao_url            VARCHAR (255),
    OUT ao_filesize        REAL,
    OUT ao_isWeblink        SMALLINT,
    -- Translator-specific output parameters:
    OUT ao_extension        VARCHAR (15)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);  -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- the oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_rowCount          = 0;
    SET l_oid               = c_NOOID;

-- body:
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);


    -- retrieve the attachment data:
    CALL IBSDEV1.p_Attachment_01$retrieve(ai_oid_s, ai_userId, ai_op, ao_state,
                                  ao_tVersionId, ao_typeName, ao_name,
                                  ao_containerId, ao_containerName,
                                  ao_containerKind, ao_isLink,
                                  ao_linkedObjectId, ao_owner,
                                  ao_ownerName, ao_creationDate,
                                  ao_creator, ao_creatorName,
                                  ao_lastChanged, ao_changer,
                                  ao_changerName, ao_validUntil,
                                  ao_description, ao_showInNews,
                                  ao_checkedOut, ao_checkOutDate,
                                  ao_checkOutUser, ao_checkOutUserOid,
                                  ao_checkOutUserName, ao_isMaster,
                                  ao_attachmentType, ao_filename,
                                  ao_path, ao_url, ao_filesize,
                                  ao_isWeblink);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN

    -- retrieve the type specific data:
        SELECT extension
        INTO ao_extension
        FROM IBSDEV1.ibs_Translator_01
        WHERE oid = l_oid;
    END IF;

    -- if operation performed successfully

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Translator_01$retrieve

--------------------------------------------------------------------------------
 -- Creates a new object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   ai_userId           ID of the user who is creating the object.
 -- @param   ai_op               Operation to be performed (used for rights
 --                              check).
 -- @param   ai_tVersionId       Type of the new object.
 -- @param   ai_name             Name of the object.
 -- @param   ai_containerId_s    ID of the container where object shall be
 --                              created in.
 -- @param   ai_containerKind    Kind of object/container relationship
 -- @param   ai_isLink           Defines if the object is a link
 -- @param   ai_linkedObjectId_s If the object is a link this is the ID of the
 --                              where the link shows to.
 -- @param   ai_description      Description of the object.
 --
 -- @output parameters:
 -- @param   ao_oid_s            OID of the newly created object.
 -- @return  A value representing the state of the procedure.
 -- c_ALL_RIGHT              Action performed, values returned, everything ok.
 -- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Translator_01$create');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Translator_01$create
(
    -- common input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- common output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found
    DECLARE c_ALREADY_EXISTS INT;           -- the object exists already
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);  -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- oid of the container object
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA; -- oid of the linked object
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- the oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;
    SET c_ALREADY_EXISTS = 21;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_rowCount          = 0;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    -- create base object:
    CALL IBSDEV1.p_Attachment_01$create(ai_userId, ai_op, ai_tVersionId,
                                ai_name, ai_containerId_s,
                                ai_containerKind, ai_isLink,
                                ai_linkedObjectId_s, ai_description,
                                ao_oid_s);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN
        -- convert the oid:
        CALL IBSDEV1.p_stringToByte (ao_oid_s, l_oid);

        -- initialize object type specific data:
        INSERT INTO IBSDEV1.ibs_Translator_01(oid, extension)
        VALUES (l_oid, '');
        COMMIT;
    END IF;

    -- if object created successfully

    -- return the state value:
    RETURN l_retValue;
END;


--------------------------------------------------------------------------------
 -- Changes the attributes of an existing object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   ai_oid_s            ID of the object to be changed.
 -- @param   ai_userId           ID of the user who is creating the object.
 -- @param   ai_op               Operation to be performed (used for rights
 --                              check).
 -- @param   ai_name             Name of the object.
 -- @param   ai_validUntil       Date until which the object is valid.
 -- @param   ai_description      Description of the object.
 -- @param   ai_showInNews       Display object in the news.
 --
 -- @param   ai_isMaster         Is true if the attachment is a master.
 -- @param   ai_attachmentType   Is the type of the attachment.
 -- @param   ai_filename         The filename of the attachment.
 -- @param   ai_path             The path of the attachment.
 -- @param   ai_url              The Hyperlink of the attachment.
 -- @param   ai_filesize         The size of the attachment.
 -- @param   ai_isWeblink        Is true if the flag 32 is set (flag 32 is
 --                              set when the attachment is a weblink).
 --
 -- @param   ai_extension        The extension of the generated output file.
 --
 -- @output parameters:
 -- @return  A value representing the state of the procedure.
 -- c_ALL_RIGHT              Action performed, values returned, everything ok.
 -- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 -- c_OBJECTNOTFOUND         The required object was not found within the
 --                          database.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Translator_01$change');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Translator_01$change
(
    -- common input parameters:
    IN ai_oid_s                    VARCHAR (18),
    IN ai_userId                INT,
    IN ai_op                    INT,
    IN ai_name                    VARCHAR (63),
    IN ai_validUntil            TIMESTAMP,
    IN ai_description            VARCHAR (255),
    IN ai_showInNews            SMALLINT,
    -- attachment-specific input parameters:
    IN ai_isMaster                SMALLINT,
    IN ai_attachmentType        INT,
    IN ai_filename                VARCHAR (255),
    IN ai_path                    VARCHAR (255),
    IN ai_url                    VARCHAR (255),
    IN ai_filesize                REAL,
    IN ai_isWeblink                SMALLINT,
    -- Translator-specific input parameters:
    IN ai_extension                VARCHAR (15)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK            INT;           -- something went wrong
    DECLARE c_ALL_RIGHT            INT;           -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;           -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND    INT;           -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE                INT;
    DECLARE l_retValue            INT;           -- return value of a function
    DECLARE l_ePos                VARCHAR (255);  -- error position description
    DECLARE l_rowCount            INT;           -- row counter
    DECLARE l_oid                CHAR (8) FOR BIT DATA; -- the oid of the object
    DECLARE l_sqlcode            INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_rowCount          = 0;
    SET l_oid               = c_NOOID;

-- body:
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- perform the change of the object:
    CALL IBSDEV1.p_Attachment_01$change(ai_oid_s, ai_userId, ai_op, ai_name,
                                ai_validUntil, ai_description,
                                ai_showInNews, ai_isMaster,
                                ai_attachmentType, ai_filename, ai_path,
                                ai_url, ai_filesize, ai_isWeblink);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN
        -- update further information:
        UPDATE IBSDEV1.ibs_Translator_01
        SET extension = ai_extension
        WHERE oid = l_oid;
    END IF;

    -- if operation performed successfully

    -- return the state value:
    RETURN l_retValue;
END;



--------------------------------------------------------------------------------
 -- Copy an object and all its values. <BR>
 --
 -- @input parameters:
 -- @param   ai_oid              Oid of group to be copied.
 -- @param   ai_userId           Id of user who is copying the group.
 -- @param   ai_newOid           Oid of the new group.
 --
 -- @output parameters:
 -- @return  A value representing the state of the procedure.
 -- c_ALL_RIGHT              Action performed, values returned, everything ok.
 -- c_NOT_OK                 An error occurred.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Translator_01$BOCopy');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Translator_01$BOCopy
(
    -- common input parameters:
    IN ai_oid                CHAR (8) FOR BIT DATA,
    IN ai_userId            INT,
    IN ai_newOid            CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;               -- something went wrong
    DECLARE c_ALL_RIGHT        INT;               -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT;               -- tuple not found
    -- local variables:
    DECLARE l_retValue        INT;               -- return value of function
    DECLARE l_ePos            VARCHAR (255);      -- error position description
    DECLARE l_rowCount        INT;
    DECLARE l_sqlcode        INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- row counter
    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_OBJECTNOTFOUND = 3;

    -- initialize local variables:
    SET l_retValue = c_NOT_OK;
-- body:

    -- begin new TRANSACTION
    -- copy attachment object:
    CALL IBSDEV1.p_Attachment_01$BOCopy(ai_oid, ai_userId, ai_newOid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN

        -- make an insert for all type specific tables:
        -- (it's currently not possible to copy files!)
        SET l_sqlcode = 0;

        INSERT INTO IBSDEV1.ibs_Translator_01(oid, extension)
        SELECT ai_newOid, extension
        FROM IBSDEV1.ibs_Translator_01
        WHERE oid = ai_oid;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'insert new edi translator data';
            GOTO exception1;
        END IF;
    END IF;

    -- if operation properly performed
    -- finish the transaction:
    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT THEN
        -- everything all right?
        COMMIT;
    ELSE
        -- an error occured
        ROLLBACK;
    END IF;

    -- undo changes
    -- return the state value:
    RETURN l_retValue;
    exception1:

    -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;

    -- undo changes
    -- log the error:

    CALL IBSDEV1.ibs_erro.logError (500, 'p_Translator_01$BOCopy',
        l_sqlcode, l_ePos, 'ai_userId', ai_userId, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '');

    -- return error code:
    RETURN c_NOT_OK;
END;
