--------------------------------------------------------------------------------
-- All stored procedures regarding the object table. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020903
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
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
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Recipient_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Recipient_01$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           INT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    IN  ai_recipientOid_s   VARCHAR (18),
    IN  ai_sentObjectOid_s  VARCHAR (18),
    IN  ai_recipientRights  INT,
    IN  ai_frooze           INT,
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

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_sentObjectOid CHAR (8) FOR BIT DATA;
    DECLARE l_recipientOid  CHAR (8) FOR BIT DATA;
    DECLARE l_inboxOid      CHAR (8) FOR BIT DATA;
    DECLARE l_inboxOid_s    VARCHAR (18);
    DECLARE l_recipientName VARCHAR (63);
    DECLARE l_returnValue   INT;
    DECLARE l_returnFlag    INT;
    DECLARE l_rId           INT;
    DECLARE l_ReceivedObjecttVersionId INT DEFAULT 16864769;
    DECLARE l_distributedId CHAR (8) FOR BIT DATA;
    DECLARE l_distributedTVersionId INT;
    DECLARE l_distributedTypeName VARCHAR (63);
    DECLARE l_distributedName VARCHAR (63);
    DECLARE l_distributedIcon VARCHAR (63);
    DECLARE l_activities    VARCHAR (63);
    DECLARE l_sentObjectId  CHAR (8) FOR BIT DATA;
    DECLARE l_senderFullName VARCHAR (63);
    DECLARE l_retValue      INT;
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
    SET l_inboxOid          = c_NOOID;
    SET l_oid               = c_NOOID;
    SET l_inboxOid          = c_NOOID;
    SET l_distributedId     = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    -- conversionS (OBJECTIDSTRING) 
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_recipientOid_s, l_recipientOid);
    CALL IBSDEV1.p_stringToByte (ai_sentObjectOid_s, l_sentObjectOid);

    --  find the containerId_s of the recipients
    --  the recipientContainer is Part_Of the Sentobject
    SELECT  oid
    INTO    l_containerId
    FROM    IBSDEV1.ibs_Object
    WHERE   containerId = l_sentObjectOid
        AND tVersionId = 16849665;
  
    -- create recipient
    -- read out the name of the recipient
    SELECT  fullname, id
    INTO    l_recipientName, l_rId
    FROM    IBSDEV1.ibs_User
    WHERE   oid = l_recipientOid;
  
    -- convert containerId to containerId_s:
    CALL IBSDEV1.p_byteToString (l_containerId, ai_containerId_s);
  
    -- create the recipientsObject in the Database
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN 
        -- read out data of the distributed object  and the description of
        -- sentobject_01 for the ReceivedObject_01
        SELECT  distributeId, distributeTVersionId, distributeTypeName,
                distributeName, distributeIcon,  activities
        INTO    l_distributedId, l_distributedTVersionId,
                l_distributedTypeName, l_distributedName, l_distributedIcon,
                l_activities
        FROM    IBSDEV1.ibs_SentObject_01
        WHERE   oid = l_sentObjectOid;
    
        -- create recipient
        -- read out the name and teh rId of the recipient
        SELECT  fullname, id
        INTO    l_recipientName, l_rId
        FROM    IBSDEV1.ibs_User
        WHERE   oid = l_recipientOid;
    
        -- Insert the other values
        INSERT  INTO IBSDEV1.ibs_Recipient_01 (oid, recipientId,
                recipientName, readDate, sentObjectId, deleted)
        VALUES  (l_oid, l_recipientOid , l_recipientName, null,
                l_sentObjectOid, 0);
    
        -- add the rights to the distributed Object for the recipients
        CALL IBSDEV1.p_Rights$AddRights(l_distributedId, l_rId,
            ai_recipientRights, 1);

        GET DIAGNOSTICS l_returnValue = RETURN_STATUS;
    
        -- create and update the receivedObject_01 for the inbox
        SELECT  inbox
        INTO    l_inboxOid
        FROM    IBSDEV1.ibs_Workspace
        WHERE   userId = l_rId;
    
        -- convert containerId to containerId_s:
        CALL IBSDEV1.p_byteToString (l_inboxOid, l_inboxOid_s);
    
        -- compute the right you want to add to the inboxContainer
        SELECT  SUM (id) 
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN 
                (
                    'new', 'read', 'view', 'change', 'delete', 'viewRights',
                    'setRights', 'createLink', 'distribute', 'addElem',
                    'delElem', 'viewElems'
                );
    
        -- set new rights
        CALL IBSDEV1.p_rights$set(l_inboxOid, l_rId, l_rights, 1);
        GET DIAGNOSTICS l_returnValue = RETURN_STATUS;
    END IF;
  
    -- create a ReceivedObject_01 in the InboxContainer
    CALL IBSDEV1.p_Object$performCreate(l_rId, ai_op,
        l_ReceivedObjecttVersionId, ai_name, l_inboxOid_s, ai_containerKind,
        ai_isLink, ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF (l_retValue = c_ALL_RIGHT)
    THEN 
        -- end of create and update the receivedObject_01 for the inbox 	        
        -- read out the SenderName
        SELECT  u.fullname 
        INTO    l_senderFullName
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_user u
        WHERE   o.oid = l_sentObjectOid
            AND u.id = o.owner;
    
        INSERT  INTO IBSDEV1.ibs_ReceivedObject_01 (oid, distributedId,
                distributedTVersionId, distributedTypeName, distributedName, 
                distributedIcon, activities, sentObjectId, senderFullName )
        VALUES  (l_oid, l_distributedId, l_distributedTVersionId,
                l_distributedTypeName, l_distributedName, l_distributedIcon, 
                l_activities, l_sentObjectOid, l_senderFullName);
    END IF;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Recipient_01$create',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_containerId_s', ai_containerId_s,
        'ai_tVersionId', ai_tVersionId, '', '',
        'ai_containerKind', ai_containerKind, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Recipient_01$create

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
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Recipient_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Recipient_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       INT
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

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
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

    SET l_sqlcode = 0;

    -- perform the change of the object:
--    CALL IBSDEV1.p_Object$performChange( ai_oid_s, ai_userId, ai_op,
--        ai_name, ai_validUntil, ai_description, ai_showInNews, l_oid);

--    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Recipient_01$change',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
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
-- p_Recipient_01$change

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
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Recipient_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Recipient_01$retrieve
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
    OUT ao_isLink           INT,
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
    OUT ao_showInNews       INT,
    OUT ao_checkedOut       INT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_recipientOid_s   VARCHAR (18),
    OUT ao_recipientName    VARCHAR (63),
    OUT ao_recipientPosition VARCHAR (63),
    OUT ao_recipientEmail   VARCHAR (63),
    OUT ao_recipientTitle   VARCHAR (63),
    OUT ao_recipientCompany VARCHAR (63)
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

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_recipientOid  CHAR (8) FOR BIT DATA;
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
    SET ao_recipientPosition = 'undefined';
    SET ao_recipientEmail   = 'undefined';
    SET ao_recipientTitle   = 'undefined';
    SET ao_recipientCompany = 'undefined';

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;

    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName, 
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN 
        SELECT  recipientId
        INTO    l_recipientOid
        FROM    IBSDEV1.ibs_Recipient_01
        WHERE   oid = l_oid;
    
        SELECT  fullname
        INTO    ao_recipientName
        FROM    IBSDEV1.ibs_User
        WHERE   oid = l_recipientOid;
        -- convert sentObject to output
        CALL IBSDEV1.p_byteToString (l_recipientOid, ao_recipientOid_s);
    END IF;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Recipient_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
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
-- p_Recipient_01$retrieve

--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
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
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Recipient_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Recipient_01$delete
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

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_retValue      INT;
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

-- body:

    SET l_sqlcode = 0;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- all references and the object itself are deleted (plus rights)
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN 
        -- delete all values of object
        DELETE FROM IBSDEV1.ibs_Recipient_01
        WHERE oid = l_oid;
    END IF;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Recipient_01$delete',
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
-- p_Recipient_01$delete