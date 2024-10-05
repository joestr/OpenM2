-------------------------------------------------------------------------------
-- All stored procedures regarding the UserAddress_01 Object. <BR>
-- 
-- @version     $Revision: 1.5 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
--
-- author       Marcel Samek (MS)  020910
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Creates a new UserAddress_01 Object (incl. rights check). <BR>
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
--/
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_UserAddress_01$create');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserAddress_01$create
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
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_addressOid_s  VARCHAR (18);
    DECLARE l_personsOid_s  VARCHAR (18);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_oid_s        VARCHAR (18);

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
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

CALL IBSDEV1.logError (100, 'p_UserAddress_01$create', l_sqlcode, 'start', 'ai_userId', ai_userId, 'ai_name', ai_name, 'ai_op', ai_op, '', '', 'ai_tVersionId', ai_tVersionId, 'ai_containerId_s', ai_containerId_s, 'ai_isLink', ai_isLink, 'ai_linkedObjectId_s', ai_linkedObjectId_s, '', 0, 'ai_description', ai_description, '', 0, 'ao_oid_s', ao_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- finish previous and begin new transaction:
    COMMIT;

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate
        (ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s,
        ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description,
        ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'p_UserAddress_01$create', l_sqlcode, 'after performCreate', 'l_retValue', l_retValue, 'ai_name', ai_name, 'ai_op', ai_op, '', '', 'ai_tVersionId', ai_tVersionId, 'ai_containerId_s', ai_containerId_s, 'ai_isLink', ai_isLink, 'ai_linkedObjectId_s', ai_linkedObjectId_s, '', 0, 'ai_description', ai_description, '', 0, 'ao_oid_s', ao_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        -- insert the other values:
        SET l_sqlcode = 0;
        INSERT INTO IBSDEV1.ibs_UserAddress_01
                (oid, email, smsemail)
        VALUES  (l_oid, '', '');

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in INSERT INTO';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
--CALL IBSDEV1.logError (100, 'p_UserAddress_01$create', l_sqlcode, 'after insert', 'l_retValue', l_retValue, 'ai_name', ai_name, 'ai_op', ai_op, '', '', 'ai_tVersionId', ai_tVersionId, 'ai_containerId_s', ai_containerId_s, 'ai_isLink', ai_isLink, 'ai_linkedObjectId_s', ai_linkedObjectId_s, '', 0, 'ai_description', ai_description, '', 0, 'ao_oid_s', ao_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    END IF; -- if object created successfully
--CALL IBSDEV1.logError (100, 'p_UserAddress_01$create', l_sqlcode, 'finish', 'l_retValue', l_retValue, 'ai_name', ai_name, 'ai_op', ai_op, '', '', 'ai_tVersionId', ai_tVersionId, 'ai_containerId_s', ai_containerId_s, 'ai_isLink', ai_isLink, 'ai_linkedObjectId_s', ai_linkedObjectId_s, '', 0, 'ai_description', ai_description, '', 0, 'ao_oid_s', ao_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL p_binaryToHexString (l_oid, el_oid_s);
    CALL IBSDEV1.logError (500, 'p_UserAddress_01$create', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_description', ai_description,
        'ai_tVersionId', ai_tVersionId, 'ao_oid_s', ao_oid_s,
        'ai_containerKind', ai_containerKind, 'ai_containerId_s', ai_containerId_s,
        'ai_isLink', ai_isLink, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        '', 0, 'l_oid', el_oid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserAddress_01$create


-------------------------------------------------------------------------------
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
-- @param   @showInNews         show in news flag.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--/
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_UserAddress_01$change');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserAddress_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_email            VARCHAR (127),
    IN  ai_smsemail         VARCHAR (127)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform the change of the object:
    CALL IBSDEV1.p_Object_performChange
        (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- update the other values:
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_UserAddress_01
        SET     email = ai_email,
                smsemail = ai_smsemail
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in UPDATE TABLE';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- if operation properly performed

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_UserAddress_01$change', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ai_name', ai_name,
        'ai_showInNews', ai_showInNews, 'ai_description', ai_description,
        'l_retValue', l_retValue, 'ai_email', ai_email,
        '', 0, 'ai_smsemail', ai_smsemail,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserAddress_01$change


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
-- @param   @containerName      Name of the object's container.
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
-- @param   @showInNews         show in news flag.
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
-- @param   @maxlevels          Maximum of the levels allowed in the discussion
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--/
-- delete procedure:
CALL IBSDEV1.p_dropProc ('p_UserAddress_01$retrieve');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserAddress_01$retrieve
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
    OUT ao_email            VARCHAR (127),
    OUT ao_smsEmail         VARCHAR (127)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_ST_ACTIVE     INT DEFAULT 2;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve the base object data:
    CALL IBSDEV1.p_Object_performRetrieve
        (ai_oid_s, ai_userId, ai_op, ao_state,
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
        ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        SET l_sqlcode = 0;
        SELECT  email, smsemail
        INTO    ao_email, ao_smsemail
        FROM    IBSDEV1.ibs_UserAddress_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in retrieve specific data';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- if operation properly performed

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_UserAddress_01$retrieve', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserAddress_01$retrieve


-------------------------------------------------------------------------------
-- Deletes a Person_01 object and all its values (incl. rights check). <BR>
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
--/
-- delete procedure:
CALL IBSDEV1.p_dropProc ('p_UserAddress_01$delete');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserAddress_01$delete
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- finish previous and begin new transaction:
    COMMIT;

    -- delete base object:
    CALL IBSDEV1.p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

/* KR should not be done because of undo functionality!
    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights
        (
            l_oid,        -- given object to be 
                          -- accessed by user
            l_containerId,-- container of given object
            ai_userId,    -- user_id
            ai_op,        -- required rights user must have to
                          -- delete object 
                          -- (operation to be perf.)
            l_rights
        );

        -- returned value
        -- check if the user has the necessary rights:
        IF (l_rights = ai_op)
        THEN
            -- delete references to the object:
            DELETE  FROM IBSDEV1.ibs_Object 
            WHERE   linkedObjectId = l_oid;
            -- delete object specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  FROM IBSDEV1.ibs_UserAddress_01
            WHERE   oid = l_oid;
            -- delete object itself:
            DELETE  FROM IBSDEV1.ibs_Object 
            WHERE   oid = l_oid;
        ELSE
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    END IF; -- if operation properly performed
*/

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_UserAddress_01$delete', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserAddress_01$delete


-------------------------------------------------------------------------------
-- Returns the settings for notification and all addresses. <BR>
--
-- @input parameters:
-- .
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
--/
-- delete procedure:
CALL IBSDEV1.p_dropProc ('p_User_01$getNotificationData');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$getNotificationData
(
    -- common input parameters:
    IN  ai_userOid_s        VARCHAR (18),
    -- output parameters
    OUT ao_username         VARCHAR (63),
    OUT ao_notificationKind INT,
    OUT ao_sendSms          SMALLINT,
    OUT ao_addWeblink       SMALLINT,
    OUT ao_email            VARCHAR (127),
    OUT ao_smsemail         VARCHAR (127)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_userId        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables and return values:
    SET l_retValue = c_NOT_OK;

-- body:;
    -- convert oidstring to binaryOID
    CALL IBSDEV1.p_stringToByte (ai_userOid_s, l_oid);

    -- make an select for all type specific tables:
    SET l_sqlcode = 0;
    SELECT  a.email, a.smsemail,u.id, u.name, p.notificationKind,
            p.sendSms, p.addWeblink
    INTO    ao_email, ao_smsemail, l_userId, ao_username,
            ao_notificationKind, ao_sendSms, ao_addWeblink
    FROM    IBSDEV1.ibs_User AS u, IBSDEV1.ibs_UserProfile AS p, 
            IBSDEV1.ibs_Object AS tabAddress,
            IBSDEV1.ibs_UserAddress_01 AS a
    WHERE   p.userId = u.id
        AND u.oid = l_oid
        AND p.oid = tabAddress.containerId
        AND tabAddress.oid = a.oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- at least one row affected?
    SET l_retValue = c_ALL_RIGHT;       -- set return value

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_User_01$getNotificationData', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'ai_userOid_s', ai_userOid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_User_01$getNotificationData
