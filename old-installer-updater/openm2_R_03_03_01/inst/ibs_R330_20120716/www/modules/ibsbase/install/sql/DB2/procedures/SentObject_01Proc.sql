--------------------------------------------------------------------------------
 -- All stored procedures regarding the object table. <BR>
 --
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
 --
 -- @author      MArcel Samek (MS)  020910
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
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_SentObject_01$create');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_SentObject_01$create
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
    -- specific input parameters:
    IN  ai_deleted          SMALLINT,
    IN  ai_distributeId_s   VARCHAR (18),
    IN  ai_opDistribute     INT,
    IN  ai_senderRights     INT,
    IN  ai_freeze           SMALLINT,
-- commmon output parameters:
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
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_distributeId  CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT;
    DECLARE l_id            INT;
    DECLARE l_rowcount      INT;
    DECLARE l_containerId_s CHAR(18);
    DECLARE l_activities    VARCHAR (63);
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
    SET l_containerId       = c_NOOID;
    SET l_oid               = c_NOOID;
    SET l_containerId_s     = ai_containerId_s;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- conversions (object id string) - all input object dis must be converted:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    CALL IBSDEV1.p_stringToByte (ai_distributeId_s, l_distributeId);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- set the rights of the outboxcontainer:
    SET l_sqlcode = 0;
    SELECT  outBox
    INTO    l_containerId
    FROM    IBSDEV1.ibs_Workspace
    WHERE   userId = ai_userId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in set the rights of the outboxcontainer';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- compute the right to be added or set:
    SET l_sqlcode = 0;
    SELECT  SUM (id)
    INTO    l_rights
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN
            (
                'new', 'read', 'view', 'change', 'delete', 'viewRights',
                'setRights', 'createLink', 'distribute', 'addElem', 'delElem',
                'viewElems'
            );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in compute the right to be added or set';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- set new rights:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Rights$set(l_containerId, ai_userId, l_rights, 1);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in set new rights';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- convert containerId to containerId_s:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_byteToString (l_containerId, l_containerId_s);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in convert l_containerId';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- create base object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, l_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in create base object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        -- create object type specific data:
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_SentObject_01(oid, distributeId,
                distributeTVersionId, distributeTypeName, distributeName,
                distributeIcon, activities, deleted)
        SELECT  l_oid, l_distributeId, tVersionId, typeName, name, icon,
                l_activities, ai_deleted
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_distributeId;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        -- check if insertion was performed properly:
        IF (l_rowcount <= 0)            -- no row affected?
        THEN
            SET l_retValue = c_NOT_OK;  -- set return value
        ELSE                            -- any row affected?
            IF (ai_freeze = 1)          -- freeze the distributed object?
            THEN
                -- start set rights of the distributed object
                -- compute the right you want to
                SET l_rights = ai_senderRights;
                -- set the rights of the distributed Object:
                SET l_sqlcode = 0;
                CALL IBSDEV1.p_Rights$setRights(l_distributeId,
                    ai_userId, l_rights, 1);

                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            END IF; -- freeze the distributed object?
        END IF; -- else any row affected?

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)     -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in set rights of distributed Object';
            GOTO exception1;    -- call common exception handler
        END IF; -- if any exception

    END IF; -- object created successfully?

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_SentObject_01$create',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_containerId_s', ai_containerId_s,
        'ai_tVersionId', ai_tVersionId,
        'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_containerKind', ai_containerKind, 'ai_description', ai_description,
        'ai_isLink', ai_isLink, 'ai_distributeId_s', ai_distributeId_s,
        'ai_deleted', ai_deleted, '', '',
        'ai_opDistribute', ai_opDistribute, '', '',
        'ai_senderRights', ai_senderRights, '', '',
        'ai_freeze', ai_freeze, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_SentObject_01$create

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
--

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_SentObject_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_SentObject_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_distributeId_s   VARCHAR (18),
    IN  ai_activities       VARCHAR (63),
    IN  ai_deleted          SMALLINT
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
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_dummy         CHAR (8) FOR BIT DATA;
    DECLARE l_distributeId  CHAR (8) FOR BIT DATA;
    DECLARE l_distributeTVersionId INT;
    DECLARE l_distributeTypeName VARCHAR (63);
    DECLARE l_distributeName VARCHAR (63);
    DECLARE l_distributeIcon VARCHAR (63);
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
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in convert ai_oid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_distributeId_s, l_distributeId);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in convert ai_distributeId_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- perform the change of the object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_dummy);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in perform the change of the object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- read out from distributed Object in SentObjectTable
    SET l_sqlcode = 0;
    SELECT  tVersionId, name, icon
    INTO    l_distributeTVersionId, l_distributeName, l_distributeIcon
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_distributeId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in read out from distributed Object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    UPDATE  IBSDEV1.ibs_SentObject_01
    SET     distributeId = l_distributeId,
            distributeTVersionId = l_distributeTVersionId,
            distributeName = l_distributeName,
            distributeIcon = l_distributeIcon,
            activities = ai_activities,
            deleted = ai_deleted
    WHERE   oid = l_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in UPDATE statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_SentObject_01$change',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ai_name', ai_name,
        'ai_deleted', ai_deleted, 'ai_description', ai_description,
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
-- p_SentObject_01$change

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
--
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_SentObject_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_SentObject_01$retrieve
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
    OUT ao_distributeId_s   VARCHAR (18),
    OUT ao_distributeTVersionId INT,
    OUT ao_distributeTypeName VARCHAR (63),
    OUT ao_distributeName   VARCHAR (63),
    OUT ao_distributeIcon   VARCHAR (63),
    OUT ao_activities       VARCHAR (63),
    OUT ao_deleted          SMALLINT,
    OUT ao_recipientContainerId_s VARCHAR (18)
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
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_recipientContainerId CHAR (8) FOR BIT DATA;
    DECLARE l_distributeId  CHAR (8) FOR BIT DATA;
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
    SET l_recipientContainerId = c_NOOID;
    SET l_distributeId      = c_NOOID;

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
        SET l_ePos = 'Error in UPDATE statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        SET l_sqlcode = 0;
        SELECT  distributeId, distributeTVersionId, distributeTypeName,
                distributeName, distributeIcon, activities, deleted
        INTO    l_distributeId, ao_distributeTVersionId, ao_distributeTypeName,
                ao_distributeName, ao_distributeIcon, ao_activities, ao_deleted
        FROM    IBSDEV1.ibs_SentObject_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT statement 1';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        SET l_sqlcode = 0;
        SELECT  oid
        INTO    l_recipientContainerId
        FROM    IBSDEV1.ibs_Object
        WHERE   containerId = l_oid
            AND tVersionId = 16849665;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT statement 2';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- recipientlist
        ---------------end of conversion--------------------------------------
        -- Fuer die Anzeige der gelesenen und ungelesenen Objekte ist es notwendig, dass
        -- der p_xxx$retrieve Methoden das folgende Statement einbaut:
        -- set the readdate in the related recipient_01 object of the actual user
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_Recipient_01
        SET     readDate = CURRENT TIMESTAMP
        WHERE   sentObjectId = l_oid
            AND (readDate IS NULL)
            AND recipientId =   
                (
                    SELECT  oid
                    FROM    IBSDEV1.ibs_User
                    WHERE   id = ai_userId
                );

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in UPDATE statement 2';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- convert sendedObjectId to output
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_byteToString (l_distributeId, ao_distributeId_s);

        -- convert recipientContainerId to output
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_byteToString (l_recipientContainerId,
            ao_recipientContainerId_s);
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_SentObject_01$retrieve',
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
-- p_SentObject_01$retrieve

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
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Sentobject_01$delete');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Sentobject_01$delete
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
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
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

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in convert ai_oid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- all references and the object itself are deleted (plus rights)
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op,
        l_dummy);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object_performDelete';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- delete all values of object
        SET l_sqlcode = 0;
        DELETE  FROM IBSDEV1.ibs_SentObject_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in delete all values of object';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF;

    COMMIT;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Sentobject_01$delete',
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
-- p_Sentobject_01$delete













