--------------------------------------------------------------------------------
-- All stored procedures regarding the user table. <BR>
--
-- @version     $Revision: 1.7 $, $Date: 2009/02/10 09:31:15 $
--              $Author: btatzmann $
--
-- author      Marcel Samek (MS)  020910
-- 2002.11.11 - Change all inserts to table ibs_user
----------------------------------------------------------------------------------

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
-- @param   ai_newUserId        User id to be used. If this value is set the
--                              procedure tries to get the existing tuple to
--                              this out of the user table instead of
--                              creating a new one.
--
-- @output parameters:
-- @param   ao_oid_s            OID of the newly created object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_User_01$performCreate');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$performCreate
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
    IN  ai_newUserId        INT,
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
    DECLARE l_retValue      INT DEFAULT 0;  -- return value of function
    DECLARE l_ePos          VARCHAR (2000) DEFAULT '';
                                            -- error position description
    DECLARE l_rowCount      INT DEFAULT 0;  -- row counter
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_usersOid      CHAR (8) FOR BIT DATA;
    DECLARE l_usersOid_s    VARCHAR (18);
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_domainId      INT DEFAULT 0;
    DECLARE l_allGroupOid   CHAR (8) FOR BIT DATA;
    DECLARE l_state         INT DEFAULT 0;
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_name          VARCHAR (63) DEFAULT '';
    DECLARE l_newUserId     INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_newUserId         = ai_newUserId;
    SET l_name              = ai_name;
    SET l_containerId       = c_NOOID;
    SET l_usersOid          = c_NOOID;
    SET l_usersOid_s        = c_NOOID_s;
    SET l_oid               = c_NOOID;
    SET l_allGroupOid       = c_NOOID;
    SET l_domainId          = 0;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    IF (l_newUserId IS NULL)
    THEN
        SET l_newUserId = 0;
    END IF;

    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    -- get the domain data:
    SELECT  d.usersOid, d.id
    INTO    l_usersOid, l_domainId
    FROM    IBSDEV1.ibs_User u, IBSDEV1.ibs_Domain_01 d
    WHERE   u.id = ai_userId
        AND d.id = u.domainId;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'get domain data';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- convert oid to string:
    CALL IBSDEV1.p_byteToString (l_usersOid, l_usersOid_s);

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate
        (ai_userId, ai_op, ai_tVersionId,
        l_name, l_usersOid_s, ai_containerKind,
        ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'create base object';
        GOTO exception1;                -- call common exception handler
    END IF;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- get the state and name from ibs_Object:
        SELECT  state, name
        INTO    l_state, l_name
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_oid;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get state and name';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- try to set data of the user:
        UPDATE  IBSDEV1.ibs_User
        SET     name = l_name,
                oid = l_oid,
                state = l_state,
                fullname = l_name,
                domainId = l_domainId
        WHERE   id = l_newUserId;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        IF ((l_sqlcode <> 0 AND l_sqlcode <> 100) OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'UPDATE ibs_User';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- re-initialize sql code:
        SET l_sqlcode = 0;

        IF (l_rowcount <= 0)
        THEN
            -- create new tuple for user:
            INSERT  INTO IBSDEV1.ibs_User
                    (name, oid, state, password, fullname, domainId)
            VALUES  (l_name, l_oid, l_state, '', l_name, l_domainId);

            IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
            THEN
                -- create error entry:
                SET l_ePos = 'INSERT INTO ibs_User';
                GOTO exception1;        -- call common exception handler
            END IF;

            -- get the new id:
            SELECT  id
            INTO    l_newUserId
            FROM    IBSDEV1.ibs_User
            WHERE   oid = l_oid;

            IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
            THEN
                -- create error entry:
                SET l_ePos = 'get the new id';
                GOTO exception1;        -- call common exception handler
            END IF;
        END IF; -- if user not found

        -- set rights of user on his/her own data:
        -- (this is necessary to allow the user to add his/her own person)
        SELECT  SUM (id)
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'read', 'new', 'addElem');

        CALL IBSDEV1.p_Rights$addRights (l_oid, l_newUserId, l_rights, 1);

        -- create a new workspace:
        CALL IBSDEV1.p_Workspace_01$create (ai_userId, 0, l_newUserId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create new workspace';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- check if container is a group
        IF EXISTS
        (
            SELECT  *
            FROM    IBSDEV1.ibs_Group
            WHERE   oid = l_containerId
        )
        THEN
            -- add user to group, roleId not inserted:
            CALL IBSDEV1.p_Group_01$addUser
                (ai_userId, l_containerId, l_oid, c_NOOID);

            IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
            THEN
                -- create error entry:
                SET l_ePos = 'add user to group';
                GOTO exception1;        -- call common exception handler
            END IF;
        END IF;

        -- get group of all users of domain:
        SELECT  g.oid
        INTO    l_allGroupOid
        FROM    ibs_Group g, ibs_Domain_01 d
        WHERE   d.id = l_domainId
            AND g.id = d.allGroupId;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'get Jeder';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- put every created User in the Group Jeder
        -- add user to group, roleId not inserted:
        CALL IBSDEV1.p_Group_01$addUser
            (ai_userId, l_allGroupOid, l_oid, c_NOOID);

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'put user into all';
            GOTO exception1;            -- call common exception handler
        END IF;
    END IF; -- if object created successfully

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_User_01$performCreate', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'ao_oid_s', ao_oid_s,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_description', ai_description,
        'ai_tVersionId', ai_tVersionId, 'ai_containerId_s', ai_containerId_s,
        'ai_containerKind', ai_containerKind, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_isLink', ai_isLink, 'l_name', l_name,
        'ai_newUserId', ai_newUserId, 'l_usersOid_s', l_usersOid_s,
        'l_domainId', l_domainId, '', '',
        'l_newUserId', l_newUserId, '', '',
        'l_state', l_state, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_User_01$performCreate


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
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_User_01$create');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$create
(
    -- input parameters:
    IN userId           INT,
    IN op               INT,
    IN tVersionId       INT,
    IN name             VARCHAR (63),
    IN containerId_s    VARCHAR (18),
    IN containerKind    INT,
    IN isLink           SMALLINT,
    IN linkedObjectId_s VARCHAR (18),
    IN description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE ALL_RIGHT INT;
    DECLARE INSUFFICIENT_RIGHTS INT;
    DECLARE ALREADY_EXISTS INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE retValue INT;
    DECLARE l_sqlcode INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET ALL_RIGHT           = 1;
    SET INSUFFICIENT_RIGHTS = 2;
    SET ALREADY_EXISTS      = 21;

    -- initialize local variables and return values:
    SET retValue = ALL_RIGHT;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    CALL IBSDEV1.p_User_01$performCreate (userId, op, tVersionId, name,
        containerId_s, containerKind, isLink, linkedObjectId_s, description,
        0, ao_oid_s);
    GET DIAGNOSTICS retValue = RETURN_STATUS;
    -- return the state value:
  COMMIT;
  RETURN retValue;
END;
-- p_User_01$create


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
  -- @param   @delLink            Should linked Person be deleted ?
  --                              (0 = no, else yes)
  --
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --  OBJECTNOTFOUND          The required object was not found within the
  --                          database.
  --
 -- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_User_01$change');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$change
(
    -- input parameters:
    IN ai_oid_s         VARCHAR (18),
    IN ai_userId        INT,
    IN ai_op            INT,
    IN ai_name          VARCHAR (63),
    IN ai_validUntil    TIMESTAMP,
    IN ai_description   VARCHAR (255),
    IN ai_showInNews    SMALLINT,
    IN ai_fullname      VARCHAR (63),
    IN ai_state         INT,
    IN ai_password      VARCHAR (63),
	IN ai_changePwd     SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NAME_ALREADY_EXISTS INT DEFAULT 51;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_domainMult    INT DEFAULT 16777216;
                                            -- multiplier to compute offset of
                                            -- domain id within user id

    -- define return values:
    DECLARE l_retValue INT;
    -- define local variables:
    DECLARE l_oid       CHAR (8) FOR BIT DATA;
    DECLARE l_domainId  INT;
    DECLARE l_given     INT;
    DECLARE l_linkOid   CHAR (8) FOR BIT DATA;
    DECLARE l_linkOid_s VARCHAR (18);
    DECLARE l_sqlcode   INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET l_linkOid = c_NOOID;
    SET l_linkOid_s = c_NOOID_s;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- compute domain id:
    -- (divide user id by 0x01000000, i.e. get the first byte)
    SET l_domainId = ai_userId / c_domainMult;
    -- is the name already given in this domain?
    SELECT  COUNT(*)
    INTO    l_given
    FROM    IBSDEV1.ibs_User u, IBSDEV1.ibs_Object o
    WHERE   u.oid = o.oid
        AND o.name = ai_name
        AND u.domainId = l_domainId
        AND o.state = 2
        AND o.oid <> l_oid;

    IF (l_given > 0)                    -- name already given?
    THEN
        SET l_retValue = c_NAME_ALREADY_EXISTS;
    -- end if name already given
    ELSE                                -- name not given
        -- perform the change of the object:
        CALL IBSDEV1.p_Object$performChange (ai_oid_s, ai_userId, ai_op,
            ai_name, ai_validUntil, ai_description, ai_showInNews, l_oid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_retValue = c_ALL_RIGHT)   -- operation properly performed?
        THEN
            -- update the other values, get the state from the object:
            UPDATE  IBSDEV1.ibs_User
            SET     name = ai_name,
                    fullname = ai_fullname,
                    state = (
                                SELECT  state
                                FROM    IBSDEV1.ibs_Object
                                WHERE   oid = l_oid
                            ),
                    password = ai_password,
					changePwd = ai_changePwd
            WHERE   oid = l_oid;
        END IF; -- if operation properly performed
    END IF; -- else name not given

    -- finish transaction:
    COMMIT;                             -- make changes permanent
    
    -- return the state value:
    RETURN l_retValue;
END;
-- p_User_01$change


--------------------------------------------------------------------------------
  -- Creates a new user. <BR>
  -- This procedure also adds the user to a group and sets the rights of members
  -- of this group on the user.
  --
  -- @input parameters:
  -- @param   @userId             ID of the user who is creating the object.
  -- @param   @domainId           Id of the domain where the user shall resist.
  -- @param   @username           Name of the user.
  -- @param   @password           Password initially set for this user.
  -- @param   @fullname           Full name of the user.
  --
  -- @output parameters:
  -- @param   @oid                Oid of the newly generated user.
  -- @return  A value representing the state of the procedure.
  -- @ALL_RIGHT               Action performed, values returned, everything ok.
  -- @ALREADY_EXISTS          An user with this id already exists.
  --
 -- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_User_01$createFast');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$createFast
(
    -- input parameters:
    IN ai_userId            INT,
    IN ai_domainId          INT,
    IN ai_username          VARCHAR (63),
    IN ai_password          VARCHAR (63),
    IN ai_fullname          VARCHAR (63),
    -- output parameters:
    OUT ao_oid              CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT DEFAULT 0;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_containerId_s VARCHAR (18);
    DECLARE l_oid_s         VARCHAR (18);
    DECLARE l_groupId       INT DEFAULT 0;
    DECLARE l_groupOid      CHAR (8) FOR BIT DATA;
    DECLARE l_validUntil    TIMESTAMP;
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
    SET l_retValue          = c_ALL_RIGHT;
    SET l_containerId       = c_NOOID;
    SET l_containerId_s     = c_NOOID_s;
    SET l_oid_s             = c_NOOID_s;
    SET l_groupOid          = c_NOOID;
    SET ao_oid              = c_NOOID;

-- body:
    -- get user container:
    SET l_sqlcode = 0;

    SELECT  usersOid
    INTO    l_containerId
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   id = ai_domainId;

    -- check if the domain was found:
    IF (l_sqlcode = 0)
    THEN
        -- convert container oid to string representation:
        CALL IBSDEV1.p_byteToString (l_containerId, l_containerId_s);
        -- the current user does not need any rights because this procedure
        -- shall only be called during installation.
        CALL IBSDEV1.p_User_01$performCreate
            (ai_userId, 0, 16842913, ai_username,
            l_containerId_s, 1, 0, c_NOOID_s, '', 0, l_oid_s);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- convert user oid string to oid representation:
        CALL IBSDEV1.p_stringToByte (l_oid_s, ao_oid);

        -- check if there was an error during creation:
        IF (l_retValue = c_ALL_RIGHT)
        THEN
            -- set valid Time to one year
            SET l_validUntil = CURRENT TIMESTAMP + 12 MONTHS;
            -- store user specific data:
            -- the user has created the object, so don't check rights.
            CALL IBSDEV1.p_User_01$change (l_oid_s, ai_userId, 0,
                ai_username, l_validUntil, '', 0, ai_fullname, 2, ai_password);
        END IF;
    END IF;

    -- if the domain was found
    COMMIT;
    -- return the state value

    RETURN l_retValue;
END;
-- p_User_01$createFast


--------------------------------------------------------------------------------
-- Creates a new user. <BR>
-- This procedure also adds the user to a group and sets the rights of members
-- of this group on the user.
--
-- @input parameters:
-- @param   @domainId           Id of the domain where the user shall resist.
-- @param   @userNo             Predefined number of the user.
-- @param   @name               Name of the user.
-- @param   @password           Password initially set for this user.
-- @param   @fullname           Full name of the user.
-- @param   @group              Group to add the user to
--                              (null -> don't add user to a group).
-- @param   @rights             Rights which the members of the group shall
--                              have on this user 
--                             (null -> don't assign rights).
--
-- @output parameters:
-- @param   @newId              New id = @id if; <> null, a newly generated
--                              id otherwise
-- @return  A value representing the state of the procedure.
-- @ALL_RIGHT               Action performed, values returned, everything ok.
-- @ALREADY_EXISTS          An user with this id already exists.
--
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_User_01$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$new
(
    -- input parameters:
    IN  ai_domainId         INT,
    IN  ai_userNo           INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_password         VARCHAR (63),
    IN  ai_fullname         VARCHAR (63),
    IN  ai_group            INT,
    IN  ai_rights           INT,
    -- output parameters:
    OUT ao_id               INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
MODIFIES SQL DATA
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_domainMult    INT DEFAULT 16777216;
                                            -- multiplier to compute offset of
                                            -- domain id within user id

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT DEFAULT 0;  -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_msg           VARCHAR (255);
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_groupOid      CHAR (8) FOR BIT DATA;
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
    SET l_oid = c_NOOID;
    SET ao_id = 0;

-- body:
    -- compute id:
    IF  (ai_userNo <> 0)                -- user number defined?
    THEN
        -- set domain id as highest byte of user id and add the user number:
        SET ao_id = (ai_domainId * c_domainMult) + 8388608 + ai_userNo;
    ELSE                                -- no user number defined
        SET ao_id = 0;
    END IF; -- else no user number defined

    -- check if an user with this id already exists:
    IF EXISTS
    ( 
        SELECT  id
        FROM    IBSDEV1.ibs_User
        WHERE   id = ao_id
    )
    THEN
        -- at this point we know that an user with this id already exists.
        SET l_retValue = c_ALREADY_EXISTS;
    ELSE                                -- user id not already there?
        -- finish previous and begin new transaction:
        COMMIT;

        -- add the new user:
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_User
                (id, oid, state, domainId, name, password, fullname)
        VALUES  (ao_id, c_NOOID, 2, ai_domainId, ai_name,
                ai_password, ai_fullname);
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        IF (l_sqlcode <> 0)
        THEN
            -- create error entry:
            SET l_ePos = 'INSERT INTO ibs_User';
            GOTO exception1;            -- call common exception handler
        END IF;

        IF (l_rowcount > 0)             -- user was inserted?
        THEN
            IF  (ao_id = 0)             -- id must have been changed?
            THEN
                -- get the id of the newly inserted user:
--                SET ao_id = ai_domainId * c_domainMult + IDENTITY_VAL_LOCAL ();
                SELECT  MAX (id)
                INTO    ao_id
                FROM    ibs_User
                WHERE   state = 2
                    AND domainId = ai_domainId
                    AND name = ai_name
                    AND password = ai_password
                    AND fullname = ai_fullname;

                IF (l_sqlcode <> 0)
                THEN
                    -- create error entry:
                    SET l_ePos = 'get id';
                    GOTO exception1;    -- call common exception handler
                END IF;
            END IF; -- if id must have been changed

            -- get oid:
            SELECT  oid
            INTO    l_oid
            FROM    IBSDEV1.ibs_User
            WHERE   id = ao_id;

            IF (l_sqlcode = 100)        -- no data found?
            THEN
                -- create error entry:
                SET l_ePos = 'get oid - NO_DATA_FOUND';
                GOTO exception1;        -- call common exception handler
            -- end if no data found
            ELSEIF (l_sqlcode <> 0)     -- any other exception?
            THEN
                -- create error entry:
                SET l_ePos = 'get oid - OTHER error';
                GOTO exception1;        -- call common exception handler
            END IF; -- else if any other exception

            -- add user to a group:
            IF  (ai_group IS NOT NULL)  -- group set?
            THEN
                -- get the oid of the group:
                SELECT  oid
                INTO    l_groupOid
                FROM    IBSDEV1.ibs_Group
                WHERE   id = ai_group;

                IF (l_sqlcode = 100)    -- no data found?
                THEN
                    -- create error entry:
                    SET l_ePos = 'get group oid - NO_DATA_FOUND';
                    GOTO exception1;    -- call common exception handler
                -- end if no data found
                ELSEIF (l_sqlcode <> 0) -- any other exception?
                THEN
                    -- create error entry:
                    SET l_ePos = 'get group oid - OTHER error';
                    GOTO exception1;    -- call common exception handler
                END IF; -- else if any other exception

                -- add user to group:
                CALL IBSDEV1.p_Group_01$addUserSetRights
                    (ao_id, l_groupOid, l_oid, c_NOOID, ai_rights);
            END IF; -- if group set

            -- cumulate rights for user:
            CALL IBSDEV1.p_Rights$updateRightsCumUser (ao_id);
        END IF; -- if user was inserted

        -- finish the transaction:
        COMMIT;                         -- make changes permanent
    END IF; -- else user id not already there

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_User_01$new', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'ai_name', ai_name,
        'ai_domainId', ai_domainId, 'ai_password', ai_password,
        'ai_userNo', ai_userNo, 'ai_fullname', ai_fullname,
        'ai_group', ai_group, '', '',
        'ai_rights', ai_rights, '', '',
        'ao_id', ao_id, '', '',
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
-- p_User_01$new


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
  -- @param   @state              The user's state.
  -- @param   @tVersionId         ID of the object's type (correct version).
  -- @param   @typeName           Name of the object's type.
  -- @param   @name               Name of the user.
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
  -- @param   @fullname           Fullname of the user
  -- @param   @password           Password of the user
  -- @param   @workspaveId        Workspave ot the user
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --  OBJECTNOTFOUND          The required object was not found within the
  --                          database.
  --
 -- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$retrieve');
CALL IBSDEV1.p_dropProc ('p_User_01$retrieve');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$retrieve
(
    -- input parameters:
    IN ai_oid_s         VARCHAR (18),
    IN ai_userId        INT,
    IN ai_op            INT,
    -- output parameters
    OUT ao_state      INT,
    OUT ao_tVersionId INT,
    OUT ao_typeName   VARCHAR (63),
    OUT ao_name       VARCHAR (63),
    OUT ao_containerId CHAR (8) FOR BIT DATA,
    OUT ao_containerName VARCHAR (63),
    OUT ao_containerKind INT,
    OUT ao_isLink     SMALLINT,
    OUT ao_linkedObjectId CHAR (8) FOR BIT DATA,
    OUT ao_owner      INT,
    OUT ao_ownerName  VARCHAR (63),
    OUT ao_creationDate TIMESTAMP,
    OUT ao_creator    INT,
    OUT ao_creatorName VARCHAR (63),
    OUT ao_lastChanged TIMESTAMP,
    OUT ao_changer    INT,
    OUT ao_changerName VARCHAR (63),
    OUT ao_validUntil TIMESTAMP,
    OUT ao_description VARCHAR (255),
    OUT ao_showInNews SMALLINT,
    OUT ao_checkedOut SMALLINT,
    OUT ao_checkOutDate TIMESTAMP,
    OUT ao_checkOutUser INT,
    OUT ao_checkOutUserOid CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_fullname   VARCHAR (63),
    OUT ao_password   VARCHAR (63),
    OUT ao_workspaceId CHAR (8) FOR BIT DATA,
    OUT ao_memberShipId CHAR (8) FOR BIT DATA,
    OUT ao_personOid  CHAR (8) FOR BIT DATA),
	OUT ao_changePwd SMALLINT
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
  DECLARE SQLCODE       INT;

    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    -- define return values:
    DECLARE l_retValue INT;
    -- define local variables:
    DECLARE l_oid       CHAR (8) FOR BIT DATA;
    DECLARE l_id        INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- set constants:

        SET c_ALL_RIGHT = 1;
        SET c_INSUFFICIENT_RIGHTS = 2;
        SET c_OBJECTNOTFOUND = 3;

    -- return value of this procedure
    -- initialize return values:
        SET l_retValue = c_ALL_RIGHT;
    -- initialize local variables:
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN
        -- get object type specific data:
        SELECT id, fullname, password, changePwd
        INTO l_id, ao_fullname, ao_password, ao_changePwd
        FROM IBSDEV1.ibs_User
        WHERE oid = l_oid;

        -- get workspaceId of the user:
        SELECT workspace
        INTO ao_workspaceId
        FROM IBSDEV1.ibs_Workspace
        WHERE userId = l_id;
        -- get memberShipId of the user:
        SELECT o.oid
        INTO ao_memberShipId
        FROM IBSDEV1.ibs_Object o
        WHERE o.containerId = l_oid
            AND o.containerKind = 2
            AND o.tVersionId = 16863745;

        -- tVersionId of memberShip objects
        -- get personOid linked to the user:
        SELECT linkedObjectId, name
        INTO ao_personOid, ao_fullname
        FROM IBSDEV1.ibs_Object
        WHERE containerId = l_oid AND
            tVersionId = 16842801 AND state = 2;
    END IF;

    -- if operation properly performed

    COMMIT;
    -- return the state value

    RETURN l_retValue;
END;
    -- p_User_01$retrieve




--------------------------------------------------------------------------------
  -- Makes the login of a new user. (incl. rights check). <BR>
  --
  -- @input parameters:
  -- @param   @domainId           Domain where the user wants to be logged in.
  -- @param   @username           Required user name.
  -- @param   @password           Password typed by the user.
  --
  -- @output parameters:
  -- @param   @oid                Object id of the user object.
  -- @param   @id                 Id of the user.
  -- @param   @fullname           Full name of the user.
  -- @param   @ao_sslRequired     flag if SSL must be used for the domain or not
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --  OBJECTNOTFOUND          The required object was not found within the
  --                          database.
  --
 -- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$login');
CALL IBSDEV1.p_dropProc ('p_User_01$login');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$login
(
    -- input parameters:
    IN ai_domainId      INT,
    IN ai_username      VARCHAR (63),
    IN ai_password      VARCHAR (63),
    -- output parameters
    OUT ao_oid        CHAR (8) FOR BIT DATA,
    OUT ao_id         INT,
    OUT ao_fullname   VARCHAR (63),
    OUT ao_domainName VARCHAR (63),
    OUT ao_sslRequired SMALLINT,
	OUT ao_changePwd SMALLINT)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;

    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_WRONG_PASSWORD INT DEFAULT 11;
    DECLARE c_NOT_VALID     INT DEFAULT 41;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    -- define return values:
    DECLARE l_retValue INT;
    -- define local variables:
    DECLARE l_rights    INT;
    -- return value of called procedure
    DECLARE l_realPassword VARCHAR (63);
    DECLARE l_validUntil TIMESTAMP;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_rowcount  INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- return value of this procedure
    -- initialize return values:

    SET l_retValue = c_OBJECTNOTFOUND;
    -- initialize local variables:
    SET l_realPassword = 'unknownPassword';
    SET ao_oid = c_NOOID;
    SET ao_id = 0;
    SET ao_fullname = '';
-- body:
    -- get data of required user:
    SET l_sqlcode = 0;

    SELECT u.id, u.oid, u.password, u.fullname, u.changePwd, o.validUntil
    INTO ao_id, ao_oid, l_realPassword, ao_fullname, ao_changePwd, l_validUntil
    FROM IBSDEV1.ibs_User u, IBSDEV1.ibs_Object o
    WHERE u.name = ai_username
        AND (u.domainId = ai_domainId
        OR   u.domainId = 0)
        AND u.state = 2
        AND o.state = 2
        AND o.oid = u.oid;

    -- check if the user exists:
    IF l_sqlcode = 0 THEN
        -- check if user is valid:
        IF l_validUntil >= CURRENT TIMESTAMP THEN
            -- check password:
            IF ai_password = l_realPassword THEN
                -- get domain data:
                SET l_sqlcode = 0;

                SELECT o.name, d.sslRequired
                INTO ao_domainName, ao_sslRequired
                FROM IBSDEV1.ibs_Object o , IBSDEV1.ibs_Domain_01 d
                WHERE d.id = ai_domainId
                    AND o.oid = d.oid
                    AND o.state = 2;

                IF l_sqlcode = 100 THEN
                    SET ao_sslRequired = 0;
                END IF;
                SET l_retValue = c_ALL_RIGHT;
            ELSE

                SET ao_id = 0;
                SET ao_oid = c_NOOID;
                SET ao_fullname = '';
                SET l_retValue = c_WRONG_PASSWORD;
            END IF;
        ELSE
            SET ao_id = 0;
            SET ao_oid = c_NOOID;
            SET ao_fullname = '';
            SET l_retValue = c_NOT_VALID;
        END IF;
    ELSE
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- else user not found
    -- return the state value:

    RETURN l_retValue;
END;
-- p_User_01$login

-------------------------------------------------------------------------------
  -- Makes the logout of a online user. <BR>
  --
  -- @input parameters:
  -- @param   @id                 Id of the user.
  -- @param   @oid                Object id of the user object.
  --
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  OBJECTNOTFOUND          The required object was not found within the
  --
 -- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$logout')!
CALL IBSDEV1.p_dropProc ('p_User_01$logout');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$logout
(
    -- input parameters:
    IN ai_id            INT,
    IN ai_oid           CHAR (8) FOR BIT DATA
    )
    -- output parameters
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- definitions:
    -- define return constants:

    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    -- define return values:

    DECLARE l_retValue INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- return value of this procedure
    -- initialize return values:
    SET l_retValue = c_ALL_RIGHT;
    -- NOT IMPLEMENTED YET!
    -- return the state value:
    RETURN -1;
END;

    -- p_User_01$logout



--------------------------------------------------------------------------------
  -- Changes the password of the user. (incl. rights check). <BR>
  --
  -- @input parameters:
  -- @param   @userId             Id of the user whose password is 
  --                              to be changed.
  -- @param   @oldPassword        The old password of the user.
  -- @param   @newPassword        The new password of the user.
  --
  -- @output parameters:
  --
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  WRONGPASSWORD           The given password is wrong.
  --  OBJECTNOTFOUND          The required object was not found within the
  --                          database.
  --
 -- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$changePassword')!
CALL IBSDEV1.p_dropProc ('p_User_01$changePassword');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$changePassword
(
    -- input parameters:
    IN ai_userId        INT,
    IN ai_oldPassword   VARCHAR (63),
    IN ai_newPassword   VARCHAR (63)
)
    -- output parameters:
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_WRONG_PASSWORD INT DEFAULT 11;

    -- define return values:
    DECLARE l_retValue INT;

    -- define local variables:

    DECLARE l_rights    INT;

    -- return value of called procedure
    DECLARE ao_realPassword VARCHAR (63);
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_rowcount  INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- return value of this procedure
    -- initialize return values:
    SET l_retValue = c_OBJECTNOTFOUND;

    -- initialize local variables:
    SET ao_realPassword = 'unknownPassword';

-- body:
    -- get data of required user:
    SET l_sqlcode = 0;

    SELECT password
    INTO ao_realPassword
    FROM IBSDEV1.ibs_User
    WHERE id = ai_userId;

    -- check if the user exists:
    IF l_sqlcode = 0 THEN
        -- check password:
        IF ai_oldPassword = ao_realPassword THEN
            -- set the new password:

            UPDATE IBSDEV1.ibs_User
            SET password = ai_newPassword, changePwd = 0
            WHERE id = ai_userId;

            -- set return value:
            SET l_retValue = c_ALL_RIGHT;
        ELSE
            SET l_retValue = c_WRONG_PASSWORD;
        END IF;
    ELSE
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- else user not found
    -- return the state value:

    RETURN l_retValue;
END;
-- p_User_01$changePassword

--------------------------------------------------------------------------------
  -- Delete an object and all its values (incl. rights check). <BR>
  -- This procedure also delets all links showing to this object.
  --
  -- @input parameters:
  -- @param   ai_oid_s            ID of the object to be deleted.
  -- @param   ai_userId           ID of the user who is deleting the object.
  -- @param   ai_op               Operation to be performed (used for rights
  --                              check).
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  -- c_ALL_RIGHT              Action performed, values returned, everything ok.
  -- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
  -- c_OBJECTNOTFOUND         The required object was not found within the
  --                          database.
  --
-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$delete')!
CALL IBSDEV1.p_dropProc ('p_User_01$delete');
-- create the new procedure :
CREATE PROCEDURE IBSDEV1.p_User_01$delete
(
    -- common input parameters:
    IN ai_oid_s         VARCHAR (18),
    IN ai_userId        INT,
    IN ai_op            INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_ST_DELETED    INT DEFAULT 1;  -- state to indicate deletion of
                                            -- object
    -- local variables:
    DECLARE l_retValue  INT;                -- return value of a function
    DECLARE l_ePos      VARCHAR (255);       -- error position description
    DECLARE l_rowCount  INT;                -- row counter
    DECLARE l_id        INT;                -- the id of the user
    DECLARE l_oid       CHAR (8) FOR BIT DATA;            -- the oid of the object to be
                                            -- deleted
    DECLARE l_rights    INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;
    SET l_rights = 0;
    SET l_rowCount = 0;
-- body:
    -- conversions (VARCHAR (18)) - all input object ids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- get the user data:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_id
    FROM IBSDEV1.ibs_User
    WHERE oid = l_oid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get user data';
        GOTO NonTransactionException;
    END IF;

    -- CALL IBSDEV1.exception1 handler

    -- check if the user is a system user:
    IF EXISTS   (
                    SELECT id
                    FROM IBSDEV1.ibs_Domain_01
                    WHERE adminId = l_id
                ) THEN
        -- set corresponding return value:
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    ELSE
        -- user may be deleted

        -- begin new TRANSACTION
        -- delete base object and references:

        CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- check if there was an error:

        IF l_retValue = c_ALL_RIGHT THEN

            -- delete object type specific data:
            -- (delete all type specific tuples which are not within
            -- ibs_Object)

            -- delete all rights for the deleted user:
            CALL IBSDEV1.p_Rights$deleteAllUserRights(l_id);

            -- delete all the entries in ibs_GroupUser:
            SET l_sqlcode = 0;
            DELETE FROM IBSDEV1.ibs_GroupUser
            WHERE userId = l_id;


            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'delete group/user data';
                GOTO exception1;
            END IF;

            -- CALL IBSDEV1.exception1 handler

            -- set object as deleted:
            SET l_sqlcode = 0;
            UPDATE IBSDEV1.ibs_User
            SET state = c_ST_DELETED
            WHERE id = l_id;

            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'update user state';
                GOTO exception1;
            END IF;
        END IF;

        -- if operation properly performed
        -- check if there occurred an error:

        IF l_retValue = c_ALL_RIGHT THEN
           -- everything all right?
            COMMIT;
        ELSE
            -- an error occured
            ROLLBACK;
        END IF;
    END IF;
    -- else the user is no system user
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;

    -- undo changes

NonTransactionException:

    -- error outside of transaction occurred

    -- log the error:
    CALL IBSDEV1.ibs_erro.logError (500, 'p_User_01$delete', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s, 'ai_op', ai_op, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    COMMIT;	
    -- return error code:

    RETURN c_NOT_OK;
END;

    -- p_User_01$delete




--------------------------------------------------------------------------------
-- Copies an User_01 object and all its values (incl. rights check). <BR>
--
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_User_01$BOCopy');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$BOCopy
(
    -- common input parameters:
    IN ai_oid           CHAR (8) FOR BIT DATA,
    IN ai_userId        INT,
    IN ai_newOid        CHAR (8) FOR BIT DATA)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_ST_ACTIVE     INT DEFAULT 2; -- state value of active object
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE l_retValue  INT;                -- return value of function
    DECLARE l_name      VARCHAR (63);        -- name of user
    DECLARE l_baseName  VARCHAR (63);        -- base part of user name
    DECLARE l_count     INT;                -- counter
    DECLARE l_groupOid  CHAR (8) FOR BIT DATA;            -- oid of actual group
    DECLARE l_userId    INT;                -- id of actual user
    DECLARE l_oldUserId INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_sqlstatus INT;

    -- get all users and groups in the old group
    -- define cursor:
    DECLARE UserBOCopy_Cursor CURSOR WITH HOLD FOR
    SELECT g.oid
    FROM IBSDEV1.ibs_GroupUser gu, IBSDEV1.ibs_Group g
    WHERE gu.userId = l_oldUserId
    AND gu.origGroupId = gu.groupId
    AND gu.groupId = g.id;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- initialize local variables:
     SET l_retValue = c_ALL_RIGHT;
     SET l_count = 0;

-- body:
    -- get id of user to be copied:
    SELECT id, (name || '#copy')
    INTO l_oldUserId, l_baseName
    FROM IBSDEV1.ibs_User
    WHERE oid = ai_oid;

    -- get unique user name:

        SET l_name = l_baseName;

    -- try the temp names until an unused name is found:
    WHILE EXISTS    (
                        SELECT id
                        FROM IBSDEV1.ibs_User
                        WHERE name = l_name AND
                            state = c_ST_ACTIVE
                    ) DO
        -- compute new user name:
        SET l_count = l_count + 1;
        SET l_name = l_baseName || CAST(rtrim(CHAR (l_count)) AS VARCHAR (30));
    END WHILE;
    -- while

    -- ensure that the name in ibs_Object is correct:

    UPDATE IBSDEV1.ibs_Object
    SET name = l_name
    WHERE oid = ai_newOid;

    -- make an insert for all type specific tables:
    INSERT INTO IBSDEV1.ibs_User
                    (oid, name, state, password, fullname, domainId, changePwd)
    SELECT  ai_newOid, l_name, c_ST_ACTIVE, password, fullname, domainId, changePwd
    FROM IBSDEV1.ibs_User
    WHERE   oid = ai_oid;

    -- get the id of the new user:
    SELECT id
    INTO l_userId
    FROM IBSDEV1.ibs_User
    WHERE oid = ai_newOid;

    -- create a new worksapace and a workspace container:
    CALL IBSDEV1.p_Workspace_01$create (ai_userId, 4, l_userId);

    -- get all users and groups in the old group
    -- define cursor:

    -- open the cursor:
    OPEN UserBOCopy_Cursor;

    -- get the first user:

    SET l_sqlcode = 0;
    FETCH FROM UserBOCopy_Cursor INTO l_groupOid;
    SET l_sqlstatus = l_sqlcode;


    -- loop through all found tupels:

    WHILE l_sqlcode <> 100 DO

        -- Because;@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.

        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN
            -- add user to the current group:
            CALL IBSDEV1.p_Group_01$addUser(ai_userId, l_groupOid,
                ai_newOid, c_NOOID);
        END IF;

        -- if
        -- get next tupel:

        SET l_sqlcode = 0;
        FETCH FROM UserBOCopy_Cursor INTO l_groupOid;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;

    -- while another user found
    CLOSE UserBOCopy_Cursor;

    SET l_retValue = c_ALL_RIGHT;

    -- set return value
    COMMIT;

    -- return the state value:

  RETURN l_retValue;
END;

    -- p_User_01$BOCopy




--------------------------------------------------------------------------------
  -- Change the state of an existing object. <BR>
  --
  -- @input parameters:
  -- @param   @oid_s              ID of the object to be changed.
  -- @param   @userId             ID of the user who is changing the object.
  -- @param   @op                 Operation to be performed (used for rights
  --                              check).
  -- @param   @state              The new state of the object.
  --
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --  OBJECTNOTFOUND          The required object was not found within the
  --                          database.
  --
-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$changeState');
CALL IBSDEV1.p_dropProc ('p_User_01$changeState');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$changeState
(
    -- input parameters:
    IN oid_s    VARCHAR (18),
    IN userId   INT,
    IN op       INT,
    IN state    INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_ST_ACTIVE INT;                -- active state
    DECLARE c_ST_CREATED INT;               -- created state

    -- local variables:
    DECLARE l_retValue  INT;                -- return value of function
    DECLARE l_ePos      VARCHAR (255);

-- body:
    -- conversions: (VARCHAR (18)) - all input objectids must be converted
    DECLARE oid         CHAR (8) FOR BIT DATA;
    -- definitions:
    -- define right constants

    DECLARE RIGHT_UPDATE INT;
    DECLARE RIGHT_INSERT INT;

    -- access rights
    -- define return values
    DECLARE retValue    INT;

    -- return value of this procedure
    DECLARE rights      INT;

    -- define used variables
    DECLARE containerId CHAR (8) FOR BIT DATA;            -- id of container where the object
                                            -- resides
    DECLARE oldState    INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_rowcount  INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- error position description
    SET c_ST_ACTIVE = 2;
    SET c_ST_CREATED = 4;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;
    CALL IBSDEV1.p_stringToByte (oid_s, oid);

    -- return value of rights proc.
    -- initialize return values

    SET retValue = c_ALL_RIGHT;
    SET rights = 0;

    -- actual state of the object
    SET containerId = c_NOOID;
    SET oldState = 0;

    -- get the actual container id and state of object:
    SET l_sqlcode = 0;

    SELECT  containerId, state
    INTO    containerId, oldState
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = oid;

    -- check if the object exists:
    IF l_sqlcode = 0 THEN
    -- get rights for this user:
        CALL IBSDEV1.p_Rights$checkRights(
            oid,          -- given object to be accessed by user
            containerId,  -- container of given object
            userId,       -- user id
            op,           -- required rights user must have to
                          -- update object
            rights
            );
        GET DIAGNOSTICS rights = RETURN_STATUS;

        -- returned value
        -- check if the user has the necessary rights:

        IF rights = op THEN
            -- check if the state transition from the actual state to the new
            -- state is allowed:
            -- not implemented yet

            -- set the new state for the object and all tabs:
            UPDATE IBSDEV1.ibs_Object
            SET state = state
            WHERE oid = oid OR
                containerId = oid AND containerKind = 2 AND
                state <> state AND
                (
                    state = c_ST_ACTIVE OR
                    state = c_ST_CREATED
                );

            -- update the state of the user tuple:
            UPDATE IBSDEV1.ibs_User
            SET state = state
            WHERE oid = oid;

            COMMIT;
        ELSE
            -- set the return value with the error code:
            SET retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE
        -- set the return value with the error code:
        SET retValue = c_OBJECTNOTFOUND;
    END IF;

    -- else the object does not exist

    -- return the state value:

    RETURN retValue;
END;

    -- p_User_01$changeState


--------------------------------------------------------------------------------
  -- Delete the user from all groups where he should not be a member. <BR>
  -- The parameters represent all groups where the user may be in. If he is in
  -- one group which is not mentioned, he is dropped from that group.
  -- If one of the groupIds is 0 this means not take this 
  -- parameter into account.
  -- There is no cumulation done within this procedure.
  --
  -- @input parameters:
  -- @param   ai_userId           Id of the user, whose group memberships 
  --                              are set.
  -- @param   ai_userOid          Oid of the user, whose group memberships are
  --                              set.
  -- @param   ai_groupOid01       Oid of first group where the user may be a
  --                              member.
  -- @param   ai_groupOid02       Oid of 2nd group.
  -- @param   ai_groupOid03       Oid of 3rd group.
  -- @param   ai_groupOid04       Oid of 4th group.
  -- @param   ai_groupOid05       Oid of 5th group.
  -- @param   ai_groupOid06       Oid of 6th group.
  -- @param   ai_groupOid07       Oid of 7th group.
  -- @param   ai_groupOid08       Oid of 8th group.
  -- @param   ai_groupOid09       Oid of 9th group.
  -- @param   ai_groupOid10       Oid of 10th group.
  -- @param   ai_groupOid11       Oid of 11th group.
  -- @param   ai_groupOid12       Oid of 12th group.
  -- @param   ai_groupOid13       Oid of 13th group.
  -- @param   ai_groupOid14       Oid of 14th group.
  -- @param   ai_groupOid15       Oid of 15th group.
  --
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --
-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$delUnneededGrNoCum')!
CALL IBSDEV1.p_dropProc ('p_User_01$delUnneededGrNoCum');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$delUnneededGrNoCum
(
    -- input parameters:
    IN ai_userId        INT,
    IN ai_userOid       CHAR (8) FOR BIT DATA,
    IN ai_groupOid01    CHAR (8) FOR BIT DATA,
    IN ai_groupOid02    CHAR (8) FOR BIT DATA,
    IN ai_groupOid03    CHAR (8) FOR BIT DATA,
    IN ai_groupOid04    CHAR (8) FOR BIT DATA,
    IN ai_groupOid05    CHAR (8) FOR BIT DATA,
    IN ai_groupOid06    CHAR (8) FOR BIT DATA,
    IN ai_groupOid07    CHAR (8) FOR BIT DATA,
    IN ai_groupOid08    CHAR (8) FOR BIT DATA,
    IN ai_groupOid09    CHAR (8) FOR BIT DATA,
    IN ai_groupOid10    CHAR (8) FOR BIT DATA,
    IN ai_groupOid11    CHAR (8) FOR BIT DATA,
    IN ai_groupOid12    CHAR (8) FOR BIT DATA,
    IN ai_groupOid13    CHAR (8) FOR BIT DATA,
    IN ai_groupOid14    CHAR (8) FOR BIT DATA,
    IN ai_groupOid15    CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
                                            -- for all objects

    -- local variables:
    DECLARE l_retValue  INT;                -- return value of function
    DECLARE l_groupId   INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_sqlstatus INT;

-- body:
    -- define cursor which gets all groups where user is currently in but
    -- should not:
    DECLARE delGroupCursor CURSOR WITH HOLD FOR
    SELECT groupId
    FROM ibs_GroupUser
    WHERE userId = ai_userId AND groupId = origGroupId AND
          groupId NOT IN
          (SELECT id
           FROM IBSDEV1.ibs_Group
           WHERE oid IN
                        (
                            ai_groupOid01, ai_groupOid02, ai_groupOid03,
                            ai_groupOid04, ai_groupOid05, ai_groupOid06,
                            ai_groupOid07, ai_groupOid08, ai_groupOid09,
                            ai_groupOid10, ai_groupOid11, ai_groupOid12,
                            ai_groupOid13, ai_groupOid14, ai_groupOid15
                         ) );
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- the actual group

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- define cursor which gets all groups where user is currently in but
    -- should not:

    -- open the cursor:

    OPEN delGroupCursor;

    -- get the first object:

    SET l_sqlcode = 0;
    FETCH FROM delGroupCursor INTO l_groupId;
    SET l_sqlstatus = l_sqlcode;

    -- loop through all found objects:

    WHILE l_sqlcode <> 100 AND l_retValue = c_ALL_RIGHT DO
        -- Because;@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.

        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN
            -- delete the user from the group:

            CALL IBSDEV1.p_Group_01$delUserNoCum(l_groupId, ai_userId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;
        -- get next object:
        SET l_sqlcode = 0;
        FETCH FROM delGroupCursor INTO l_groupId;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;

    -- while another object found

    -- close and deallocate cursor to allow another cursor with the same
    -- name:

    CLOSE delGroupCursor;

    -- return the state value:

    RETURN l_retValue;
END;
-- p_User_01$delUnneededGrNoCum

--------------------------------------------------------------------------------
  -- Add the user from all groups where he is not already a member. <BR>
  -- The parameters represent all groups where the user shall be in. 
  -- If he is not
  -- in one of the mentioned groups, he is added to that group.
  -- If one of the groupIds is 0 this means not take 
  -- this parameter into account.
  -- There is no cumulation done within this procedure.
  --
  -- @input parameters:
  -- @param   ai_userId           Id of the user, whose group 
  --                              memberships are set.
  -- @param   ai_userOid          Oid of the user, whose group memberships are
  --                              set.
  -- @param   ai_groupOid01       Oid of first group where the user may be a
  --                              member.
  -- @param   ai_groupOid02       Oid of 2nd group.
  -- @param   ai_groupOid03       Oid of 3rd group.
  -- @param   ai_groupOid04       Oid of 4th group.
  -- @param   ai_groupOid05       Oid of 5th group.
  -- @param   ai_groupOid06       Oid of 6th group.
  -- @param   ai_groupOid07       Oid of 7th group.
  -- @param   ai_groupOid08       Oid of 8th group.
  -- @param   ai_groupOid09       Oid of 9th group.
  -- @param   ai_groupOid10       Oid of 10th group.
  -- @param   ai_groupOid11       Oid of 11th group.
  -- @param   ai_groupOid12       Oid of 12th group.
  -- @param   ai_groupOid13       Oid of 13th group.
  -- @param   ai_groupOid14       Oid of 14th group.
  -- @param   ai_groupOid15       Oid of 15th group.
  -- If the user is already a member of the group, nothing is changed.
  -- Otherwise he is added to the group.
  -- A groupId of 0 means not to change any membership of the user.
  -- The rights of the user are not recumulated.
  --
  -- @input parameters:
  -- @param   ai_userId           Id of the user, whose group 
  --                              memberships are set.
  -- @param   ai_groupId          Id of the group where the user shall be a
  --                              member.
  --
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --

 -- delete existing procedure:
 -- CALL IBSDEV1.p_dropProc ('p_User_01$addNeededGrNoCum')!
CALL IBSDEV1.p_dropProc ('p_User_01$addNeededGrNoCum');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$addNeededGrNoCum
(
    -- input parameters:
    IN ai_userId        INT,
    IN ai_userOid       CHAR (8) FOR BIT DATA,
    IN ai_groupOid01    CHAR (8) FOR BIT DATA,
    IN ai_groupOid02    CHAR (8) FOR BIT DATA,
    IN ai_groupOid03    CHAR (8) FOR BIT DATA,
    IN ai_groupOid04    CHAR (8) FOR BIT DATA,
    IN ai_groupOid05    CHAR (8) FOR BIT DATA,
    IN ai_groupOid06    CHAR (8) FOR BIT DATA,
    IN ai_groupOid07    CHAR (8) FOR BIT DATA,
    IN ai_groupOid08    CHAR (8) FOR BIT DATA,
    IN ai_groupOid09    CHAR (8) FOR BIT DATA,
    IN ai_groupOid10    CHAR (8) FOR BIT DATA,
    IN ai_groupOid11    CHAR (8) FOR BIT DATA,
    IN ai_groupOid12    CHAR (8) FOR BIT DATA,
    IN ai_groupOid13    CHAR (8) FOR BIT DATA,
    IN ai_groupOid14    CHAR (8) FOR BIT DATA,
    IN ai_groupOid15    CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
                                            -- for all objects

    -- local variables:
    DECLARE l_retValue  INT;                -- return value of function
    DECLARE l_groupId   INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_sqlstatus INT;

-- body:
    -- define cursor which gets all groups where user is currently in but
    -- should not:
    DECLARE addGroupCursor CURSOR WITH HOLD FOR
    SELECT id
    FROM IBSDEV1.ibs_Group
    WHERE oid IN
                (
                    ai_groupOid01, ai_groupOid02, ai_groupOid03,
                    ai_groupOid04, ai_groupOid05, ai_groupOid06,
                    ai_groupOid07, ai_groupOid08, ai_groupOid09,
                    ai_groupOid10, ai_groupOid11, ai_groupOid12,
                    ai_groupOid13, ai_groupOid14, ai_groupOid15
                )
          AND
          id NOT IN
                    (
                     SELECT groupId
                     FROM IBSDEV1.ibs_GroupUser
                     WHERE userId = ai_userId
                     AND groupId = origGroupId
                     );
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- open the cursor:


    OPEN addGroupCursor;

    -- get the first object:

    SET l_sqlcode = 0;
    FETCH FROM addGroupCursor INTO l_groupId;
    SET l_sqlstatus = l_sqlcode;

    -- loop through all found objects:

    WHILE l_sqlcode <> 100 AND l_retValue = c_ALL_RIGHT DO
        -- Because;@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.

        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN
            -- add the user to the group:
            CALL IBSDEV1.p_Group_01$addUserSetRNoCum(l_groupId, ai_userId,
                ai_userOid, 0);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;
        -- get next object:

        SET l_sqlcode = 0;
        FETCH FROM addGroupCursor INTO l_groupId;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;
        -- while another object found

        -- close and deallocate cursor to allow another cursor with the same
        -- name:

    CLOSE addGroupCursor;

    -- return the state value:


    RETURN l_retValue;
END;

    -- p_User_01$addNeededGrNoCum



--------------------------------------------------------------------------------
  -- Set the groups for a specific user. <BR>
  -- If the user is in all of these groups and no one else nothing is changed.
  -- If the user is currently not in one of the groups he is 
  -- added to this group.
  -- If the user is a member of a group, which is not mentioned here, he is
  -- removed from that group.
  -- If one of the groupIds is 0 this means not to add the user 
  -- to another group.
  -- This procedure makes use of procedures having no cumulation. The rights
  -- cumulation for the user is done after the user is assigned to the correct
  -- groups. In this way it should work most performance effective.
  --
  -- @input parameters:
  -- @param   ai_userOid          Oid of the user, whose group memberships are
  --                              set.
  -- @param   ai_groupOid01       Oid of first group where the user may be a
  --                              member.
  -- @param   ai_groupOid02       Oid of 2nd group.
  -- @param   ai_groupOid03       Oid of 3rd group.
  -- @param   ai_groupOid04       Oid of 4th group.
  -- @param   ai_groupOid05       Oid of 5th group.
  -- @param   ai_groupOid06       Oid of 6th group.
  -- @param   ai_groupOid07       Oid of 7th group.
  -- @param   ai_groupOid08       Oid of 8th group.
  -- @param   ai_groupOid09       Oid of 9th group.
  -- @param   ai_groupOid10       Oid of 10th group.
  -- @param   ai_groupOid11       Oid of 11th group.
  -- @param   ai_groupOid12       Oid of 12th group.
  -- @param   ai_groupOid13       Oid of 13th group.
  -- @param   ai_groupOid14       Oid of 14th group.
  -- @param   ai_groupOid15       Oid of 15th group.
  --
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --
-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$setGroups')!
CALL IBSDEV1.p_dropProc ('p_User_01$setGroups');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$setGroups
(
    -- input parameters:
    IN ai_userOid_s     VARCHAR (18),
    IN ai_groupOid01_s  VARCHAR (18),
    IN ai_groupOid02_s  VARCHAR (18),
    IN ai_groupOid03_s  VARCHAR (18),
    IN ai_groupOid04_s  VARCHAR (18),
    IN ai_groupOid05_s  VARCHAR (18),
    IN ai_groupOid06_s  VARCHAR (18),
    IN ai_groupOid07_s  VARCHAR (18),
    IN ai_groupOid08_s  VARCHAR (18),
    IN ai_groupOid09_s  VARCHAR (18),
    IN ai_groupOid10_s  VARCHAR (18),
    IN ai_groupOid11_s  VARCHAR (18),
    IN ai_groupOid12_s  VARCHAR (18),
    IN ai_groupOid13_s  VARCHAR (18),
    IN ai_groupOid14_s  VARCHAR (18),
    IN ai_groupOid15_s  VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_NOT_ALL       INT DEFAULT 31; -- operation could not be performed
                                            -- for all objects

    -- local variables:
    DECLARE l_retValue  INT;                -- return value of function
    DECLARE l_userId    INT;                -- id of the user
    DECLARE l_userOid   CHAR (8) FOR BIT DATA;            -- oid of the user
    DECLARE l_groupOid01 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid02 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid03 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid04 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid05 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid06 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid07 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid08 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid09 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid10 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid11 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid12 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid13 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid14 CHAR (8) FOR BIT DATA;           -- oid of a group
    DECLARE l_groupOid15 CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode   INT DEFAULT 0;

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
    CALL IBSDEV1.p_stringToByte (ai_userOid_s, l_userOid);
    CALL IBSDEV1.p_stringToByte (ai_groupOid01_s, l_groupOid01);
    CALL IBSDEV1.p_stringToByte (ai_groupOid02_s, l_groupOid02);
    CALL IBSDEV1.p_stringToByte (ai_groupOid03_s, l_groupOid03);
    CALL IBSDEV1.p_stringToByte (ai_groupOid04_s, l_groupOid04);
    CALL IBSDEV1.p_stringToByte (ai_groupOid05_s, l_groupOid05);
    CALL IBSDEV1.p_stringToByte (ai_groupOid06_s, l_groupOid06);
    CALL IBSDEV1.p_stringToByte (ai_groupOid07_s, l_groupOid07);
    CALL IBSDEV1.p_stringToByte (ai_groupOid08_s, l_groupOid08);
    CALL IBSDEV1.p_stringToByte (ai_groupOid09_s, l_groupOid09);
    CALL IBSDEV1.p_stringToByte (ai_groupOid10_s, l_groupOid10);
    CALL IBSDEV1.p_stringToByte (ai_groupOid11_s, l_groupOid11);
    CALL IBSDEV1.p_stringToByte (ai_groupOid12_s, l_groupOid12);
    CALL IBSDEV1.p_stringToByte (ai_groupOid13_s, l_groupOid13);
    CALL IBSDEV1.p_stringToByte (ai_groupOid14_s, l_groupOid14);
    CALL IBSDEV1.p_stringToByte (ai_groupOid15_s, l_groupOid15);

    -- set a save point for the current transaction:
    SAVEPOINT s_User_setGr ON ROLLBACK RETAIN CURSORS;

    -- get the user id:
    SELECT id
    INTO l_userId
    FROM IBSDEV1.ibs_User
    WHERE oid = l_userOid;

    -- delete all groups which are not needed for this user:
    CALL IBSDEV1.p_User_01$delUnneededGrNoCum(
                                      l_userId, l_userOid, l_groupOid01,
                                      l_groupOid02, l_groupOid03,
                                      l_groupOid04, l_groupOid05,
                                      l_groupOid06, l_groupOid07,
                                      l_groupOid08, l_groupOid09,
                                      l_groupOid10, l_groupOid11,
                                      l_groupOid12, l_groupOid13,
                                      l_groupOid14, l_groupOid15
                                      );
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- add the groups which are needed for this user:
    CALL IBSDEV1.p_User_01$addNeededGrNoCum(
                                    l_userId, l_userOid, l_groupOid01,
                                    l_groupOid02, l_groupOid03,
                                    l_groupOid04, l_groupOid05,
                                    l_groupOid06, l_groupOid07,
                                    l_groupOid08, l_groupOid09,
                                    l_groupOid10, l_groupOid11,
                                    l_groupOid12, l_groupOid13,
                                    l_groupOid14, l_groupOid15
                                    );
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- actualize all cumulated rights for this user:

    CALL IBSDEV1.p_Rights$updateRightsCumUser(l_userId);

    -- finish the transaction:

    IF l_retValue = c_ALL_RIGHT OR l_retValue = c_NOT_ALL THEN
        -- no severe error occurred?
        COMMIT;
    ELSE
        -- there occurred an error
        -- roll back to the save point:
        ROLLBACK TO SAVEPOINT s_User_setGr;
    END IF;
    -- release the savepoint:
    RELEASE s_User_setGr;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_User_01$setGroups

--------------------------------------------------------------------------------
  -- DELETE a user from all his groups he is a member of. <BR>
  --
  -- @input parameters:
  -- @param   @userId             ID of the user who is deleting the user.
  -- @param   @op                 Operation to be performed (used for rights
  --                              check).
  -- @param   @userOid_s          Id of the user to be deleted from 
  --                              all his groups
  --
  -- @output parameters:
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --
-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$delUserGroups');
CALL IBSDEV1.p_dropProc ('p_User_01$delUserGroups');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$delUserGroups
(
    -- input parameters:
    IN userId           INT,
    IN op               INT,
    IN uUserOid_s       VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- DEFINITIONS
    -- define return constants
    DECLARE INSUFFICIENT_RIGHTS INT;
    -- constant
    DECLARE c_ALL_RIGHT   INT;
    -- constant
    DECLARE OBJECTNOTFOUND INT;
    -- constant
    DECLARE NOT_ALL     INT;
    -- define return values
    DECLARE retValue    INT;
    -- return value of this procedure
    DECLARE rights      INT;
    -- return value of rights proc.
    DECLARE groupOid    CHAR (8) FOR BIT DATA;
    DECLARE uUserId     INT;
    DECLARE uUserOid    CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_sqlstatus INT;

    -- define cursor:
    DECLARE GroupUser_Cursor CURSOR WITH HOLD FOR
    SELECT g.oid
    FROM IBSDEV1.ibs_GroupUser gu, IBSDEV1.ibs_Group g
    WHERE gu.userId = uUserId
    AND gu.origGroupId = gu.groupId
    AND gu.groupId = g.id;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- set constants
    SET c_ALL_RIGHT = 1;
    SET INSUFFICIENT_RIGHTS = 2;
    SET OBJECTNOTFOUND = 3;
    SET NOT_ALL = 31;
    -- initialize return values
    SET retValue = c_ALL_RIGHT;
    SET rights = 0;
    CALL IBSDEV1.p_stringToByte (uUserOid_s, uUserOid);

    SELECT id
    INTO uUserId
    FROM IBSDEV1.ibs_User
    WHERE oid = uUserOid;

    -- open the cursor:

    OPEN GroupUser_Cursor;

    -- get the first group:

    SET l_sqlcode = 0;
    FETCH FROM GroupUser_Cursor INTO groupOid;
    SET l_sqlstatus = l_sqlcode;

    -- loop through all found groups:
    WHILE retValue = c_ALL_RIGHT AND l_sqlcode <> 100 DO

        -- Because;@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted during
        -- the execution of this procedure.

        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN

            -- get rights for this user
            CALL IBSDEV1.p_Rights$checkRights(
                                uUserOid,   -- given object to be accessed by user
                                groupOid,   -- container of given object
                                userId,     -- user_id
                                op,         -- required rights user must have to
                                            -- insert/update object
                                rights
                                );
            GET DIAGNOSTICS rights = RETURN_STATUS;

            -- returned value
            -- check if the user has the necessary rights
            IF rights <> op THEN
                SET retValue = NOT_ALL;
            END IF;
        END IF;

        -- get next group:
        SET l_sqlcode = 0;
        FETCH FROM GroupUser_Cursor INTO groupOid;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;

    -- while another object found

    -- close and deallocate cursor to allow another cursor with the same
    -- name:

    CLOSE GroupUser_Cursor;

    IF retValue = c_ALL_RIGHT THEN
        -- delete user from all groups:
        DELETE FROM IBSDEV1.ibs_GroupUser
        WHERE userId = uUserId;

        -- recompute the rights of the user:
        CALL IBSDEV1.p_Rights$updateRightsCumUser(uUserId);
        COMMIT;
    ELSE
        SET retValue = INSUFFICIENT_RIGHTS;
    END IF;
    -- return the state value

    RETURN retValue;
END;
-- p_User_01$delUserGroups



--------------------------------------------------------------------------------
  -- Get the basic information of an user. <BR>
  --
  -- @input parameters:
  -- @param   ai_userId           ID of the user for whom to get the info.
  -- @param   ai_domainId         Domain where the user resides.
  --
  -- @output parameters:
  -- @param   ao_userName         The name of the user.
  -- @param   ao_password         The user's password.
  -- @return  A value representing the state of the procedure.
  --  ALL_RIGHT               Action performed, values returned, everything ok.
  --  INSUFFICIENT_RIGHTS     User has no right to perform action.
  --
-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_User_01$getInfo')!
CALL IBSDEV1.p_dropProc ('p_User_01$getInfo');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_User_01$getInfo
(
    -- input parameters:
    IN ai_userId        INT,
    IN ai_domainId      INT,
    OUT ao_userName   VARCHAR (63),
    OUT ao_password   VARCHAR (63))
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.

    -- local variables:
    DECLARE l_retValue  INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_rowcount  INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET ao_userName = '';
    SET ao_password = '';

-- body:
    SET l_sqlcode = 0;
    SELECT name, password
    INTO ao_userName, ao_password
    FROM IBSDEV1.ibs_User
    WHERE id = ai_userId
        AND domainId = ai_domainId;
    
    COMMIT;
    
    IF l_sqlcode = 100 THEN
        SET l_retValue = c_NOT_OK;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
