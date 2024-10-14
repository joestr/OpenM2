--------------------------------------------------------------------------------
-- All stored procedures regarding the ASCIITranslator_01 Object. <BR>
--
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:47 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
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
-- @param   @ai_linkedObjectId_s    If the object is a link this is the ID of the
--                                  where the link shows to.
-- @param   @ai_description         Description of the object.
--
-- @output parameters:
-- @param   @ao_oid_s               OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ASCIITranslator_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ASCIITranslator_01$create
(
    -- common input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s     VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- common output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALREADY_EXISTS INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT;
    DECLARE l_actRights     INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;
  
-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    CALL IBSDEV1.p_Translator_01$create(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN 
        -- covnert the oid
        CALL IBSDEV1.p_stringToByte (ao_oid_s, l_oid);
        -- create object type specific data:
        INSERT INTO IBSDEV1.ibs_ASCIITranslator_01 (oid, separator, escapeSeparator,
            isIncludeMetadata, isIncludeHeader)
        VALUES  (l_oid, '','', 0, 0);
    END IF;
  
    -- return the state value:
    RETURN l_retValue;
END;
-- p_ASCIITranslator_01$create


--------------------------------------------------------------------------------
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
-- @param   ai_extension        The extension of the generated output file.
-- @param   @ai_separator           the separator character
-- @param   @ai_escapeSeparator     the escape sequence for the separator character
-- @param   @ai_isIncludeMetadata   option that file includes metadata
-- @param   @ai_isIncludeHeader     option that file includes header data
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ASCIITranslator_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ASCIITranslator_01$change(
    -- common input parameters:
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
    IN  ai_isWeblink        SMALLINT,
    IN  ai_extension        VARCHAR (15),
    IN  ai_separator        VARCHAR (15),
    IN  ai_escapeSeparator  VARCHAR (15),
    IN  ai_isIncludeMetadata    SMALLINT,
    IN  ai_isIncludeHeader  SMALLINT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
  
    -- define return values:
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
    -- initialize local variables:
-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Translator_01$change(ai_oid_s, ai_userId, ai_op, ai_name, ai_validUntil,
        ai_description, ai_showInNews, ai_isMaster, ai_attachmentType, ai_filename,
        ai_path, ai_url, ai_filesize, ai_isWeblink, ai_extension);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN        -- operation properly performed?
        -- create the oid
        CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
        -- update further information
        UPDATE IBSDEV1. ibs_ASCIITranslator_01
        SET separator = ai_separator,
            escapeSeparator = ai_escapeSeparator,
            isIncludeMetadata = ai_isIncludeMetadata,
            isIncludeHeader = ai_isIncludeHeader
        WHERE oid = l_oid;
        COMMIT;
    END IF;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_ASCIITranslator_01$change


--------------------------------------------------------------------------------
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
-- @param   @ao_changer             ID of person who did the last change to the
--                                  object.
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
-- @param   ao_extension        The extension of the generated output file.
-- @param   @ao_separator           the separator character
-- @param   @ao_escapeSeparator     the escape sequence for the separator character
-- @param   @ao_isIncludeMetadata   option that file includes metadata
-- @param   @ao_isIncludeHeader     option that file includes header data
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ASCIITranslator_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ASCIITranslator_01$retrieve(
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
    -- attachment specific
    OUT ao_isMaster         SMALLINT,
    OUT ao_attachmentType   INT,
    OUT ao_filename         VARCHAR (255),
    OUT ao_path             VARCHAR (255),
    OUT ao_url              VARCHAR (255),
    OUT ao_filesize         REAL,
    OUT ao_isWeblink        SMALLINT,
    -- Translator-specific output parameters:
    OUT ao_extension        VARCHAR (15),
    -- ASCIITranslator specific
    OUT ao_separator        VARCHAR (15),
    OUT ao_escapeSeparator  VARCHAR (15),
    OUT ao_isIncludeMetadata SMALLINT,
    OUT ao_isIncludeHeader   SMALLINT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue INT;
    -- define local variables:
    DECLARE l_oid CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;
    -- initialize return values:
    SET l_retValue = c_NOT_OK;
-- body:
    -- retrieve the attachment data:
    CALL IBSDEV1.p_Translator_01$retrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description,
        ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
        ao_checkOutUserOid, ao_checkOutUserName, ao_isMaster, ao_attachmentType,
        ao_filename, ao_path, ao_url, ao_filesize, ao_isWeblink, ao_extension);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN 
        -- create the oid
        CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
        -- retrieve the type specific data
        SELECT separator, escapeSeparator, isIncludeMetadata,
            isIncludeHeader
        INTO ao_separator, ao_escapeSeparator, ao_isIncludeMetadata,
            ao_isIncludeHeader
        FROM IBSDEV1.ibs_ASCIITranslator_01
        WHERE oid = l_oid;

        SELECT separator, escapeSeparator, isIncludeMetadata, isIncludeHeader
        INTO ao_separator, ao_escapeSeparator, ao_isIncludeMetadata,
            ao_isIncludeHeader
         FROM IBSDEV1.ibs_ASCIITranslator_01
         WHERE oid = l_oid;
  END IF;
  
    -- return the state value:
    RETURN l_retValue;
END;
-- p_ASCIITranslator_01$retrieve

--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
--
-- @input parameters:
-- @param   @ai_oid             ID of the object to be copy.
-- @param   @ai_userId          ID of the user who is copying the object.
-- @param   @ai_newOid          ID of the copy of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ASCIITranslator_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ASCIITranslator_01$BOCopy(
    -- common input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_newOid           CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_ePos          VARCHAR (255);
    DECLARE l_count         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
  
    -- initialize local variables:
    SET l_count             = 0;
    SET l_retValue          = c_NOT_OK;
  
-- body:
    -- copy base object:
    CALL IBSDEV1.p_Translator_01$BOCopy(ai_oid, ai_userId, ai_newOid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN        -- operation properly performed?
        -- make an insert for all type specific tables:
        -- (it's currently not possible to copy files!)
        SET l_sqlcode = 0;

        INSERT INTO IBSDEV1.ibs_ASCIITranslator_01
            (oid, separator, escapeSeparator, isIncludeMetadata,
         isIncludeHeader)
        SELECT  ai_newOid, separator, escapeSeparator,
         isIncludeMetadata, isIncludeHeader
        FROM IBSDEV1.   ibs_ASCIITranslator_01
        WHERE   oid = ai_oid;

        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        COMMIT;
    
        -- check if there occurred an error:
        IF ( l_sqlcode <> 0 AND l_sqlcode <> 100 ) OR l_count <> 1 THEN -- an error occurred?
            SET l_ePos = 'insert new ascii translator data';
            GOTO exception1;                 -- call common exception handler
        END IF;
    END IF;

    -- return the state value:
    RETURN l_retValue;
  
    exception1:                              -- an error occurred
  
    CALL IBSDEV1.logError (500, 'p_ASCIITranslator_01$BOCopy', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '');
  
    -- log the error:
    RETURN c_NOT_OK;
END;
-- p_ASCIITranslator_01$BOCopy
