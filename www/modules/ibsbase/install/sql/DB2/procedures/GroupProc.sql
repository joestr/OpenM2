--------------------------------------------------------------------------------
-- All stored procedures regarding the group table. <BR>
--
-- @version     $Revision: 1.6 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020819
-------------------------------------------------------------------------------


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
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$create');


-- delete existing procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$create
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
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_groupsOid     CHAR (8) FOR BIT DATA;
    DECLARE l_groupsOid_s   VARCHAR (18);
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_domainId      INT;
    DECLARE l_state         INT;
    DECLARE l_rights        INT;
    DECLARE l_name          VARCHAR (63);
    DECLARE l_newGroupId    INT;
    DECLARE l_groupId       INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rowCount          = 0;
    SET l_oid               = c_NOOID;
    SET l_name              = ai_name;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);

    -- get the domain data:
    SELECT  d.groupsOid, d.id 
    INTO    l_groupsOid, l_domainId
    FROM    IBSDEV1.ibs_User u, IBSDEV1.ibs_Domain_01 d
    WHERE   u.id = ai_userId
        AND d.id = u.domainId;
  
    -- convert oid to string:
    CALL IBSDEV1.p_byteToString (l_groupsOid, l_groupsOid_s);

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, ai_name,
        l_groupsOid_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- get the state and name from ibs_Object:
        SELECT  state, name
        INTO    l_state, l_name
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_oid;

        -- try to set data of the group:
        UPDATE  IBSDEV1.ibs_Group
        SET     name = l_name,
                state = l_state,
                domainId = l_domainId
        WHERE   oid = l_oid;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        IF (l_rowcount <= 0)
        THEN
            -- create new tuple for group:
            INSERT INTO IBSDEV1.ibs_Group
                    (oid, name, state, domainId)
            VALUES  (l_oid, l_name, l_state, l_domainId);
        END IF;

        -- get the id:
        SELECT  id
        INTO    l_newGroupId
        FROM    IBSDEV1.ibs_Group
        WHERE   oid = l_oid;

        -- check if container is a group:
        IF EXISTS   (
                        SELECT  *
                        FROM    IBSDEV1.ibs_Group
                        WHERE   oid = l_containerId
                    )
        THEN
            -- get the id of the container group:
            SELECT  id
            INTO    l_groupId
            FROM    IBSDEV1.ibs_Group
            WHERE   oid = l_containerId;

            -- add group:
            CALL IBSDEV1.p_Group_01$addGroupId (l_groupId, l_newGroupId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;

        -- set rights of group on its own data:
        -- (this is necessary to allow the group to be shown in some
        -- dialogs)
        SELECT  SUM (id)
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'read', 'viewElems');
        CALL IBSDEV1.p_Rights$addRights (l_oid, l_newGroupId, l_rights, 1);
    END IF; -- if object created successfully

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Group_01$create


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
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$change(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_state            INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NAME_ALREADY_EXISTS INT;      -- name of group exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_domainId      INT;            -- the id of the domain
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the converted oid_s
    DECLARE l_given         INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_ALL_RIGHT         = 1;
    SET c_NAME_ALREADY_EXISTS = 51;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_domainId          = 0;
    SET l_oid               = c_NOOID;
    SET l_given             = 0;
  
-- body:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- compute domain id:
    -- (divide user id by 0x01000000, i.e. get the first byte)
    SET l_domainId = ai_userId / 16777216;
    -- is the name already given in this domain?
    SELECT  COUNT(*) 
    INTO    l_given
    FROM    IBSDEV1.ibs_Group g
            INNER JOIN IBSDEV1.ibs_Object o ON g.oid = o.oid
    WHERE   o.name = ai_name
        AND g.domainId = l_domainId
        AND o.state = 2
        AND o.oid <> l_oid;

    IF (l_given > 0)
    THEN 
        SET l_retValue = c_NAME_ALREADY_EXISTS;
    ELSE 
        -- perform the change of the object:
        CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
            ai_validUntil, ai_description, ai_showInNews, l_oid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF l_retValue = c_ALL_RIGHT     -- operation properly performed?
        THEN
            -- update the other values, get the state from the object:
            UPDATE IBSDEV1.ibs_Group
            SET (name,state) =
            (
                SELECT  DISTINCT ai_name, o.state 
                FROM    IBSDEV1.ibs_Group g, IBSDEV1.ibs_Object o
                WHERE   g.oid = l_oid
                    AND g.oid = o.oid
            )
            WHERE EXISTS
            (
                SELECT  *  
                FROM    IBSDEV1.ibs_Group g, IBSDEV1.ibs_Object o
                WHERE   g.oid = l_oid
                    AND g.oid = o.oid
            );
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Group_01$change


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
--
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$retrieve(
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
    OUT ao_checkOutUserOid   CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName   VARCHAR (63))
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- definitions:
    -- define return constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue        INT;
    -- define local variables:
    DECLARE l_oid             CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize return values:
    SET l_retValue = c_ALL_RIGHT;
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
        ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    COMMIT;
    -- return the state value
   RETURN l_retValue;
END;
-- p_Group_01$retrieve


--------------------------------------------------------------------------------
-- Delete an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
-- 
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be deleted.
-- @param   ai_userId           ID of the user who is deleting the object.
-- @param   ai_op               Operation to be performed (used for rights 
--                              check).
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
-- c_OBJECTNOTFOUND         The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$delete(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found
                                            -- delete an object
    DECLARE c_ST_DELETED    INT;            -- state to indicate deletion of
                                            -- object

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_id            INT;            -- the id of the group
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the oid of the object to be
                                            -- deleted
    DECLARE l_rights        INT;            -- actual rights
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    SET c_ST_DELETED        = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_rowCount          = 0;
  
-- body:
    COMMIT; -- finish previous and begin new TRANSACTION

    -- conversions (VARCHAR (18)) - all input object ids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- get the group data:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_id
    FROM IBSDEV1.ibs_Group
    WHERE oid = l_oid;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get group data';
        GOTO NonTransactionException;
    END IF;
    -- check if the group is a system group:
    IF EXISTS   (
                    SELECT id 
                    FROM IBSDEV1.ibs_Domain_01
                    WHERE adminGroupId = l_id
                        OR allGroupId = l_id
                        OR userAdminGroupId = l_id
                        OR structAdminGroupId = l_id
                )
    THEN 
        -- set corresponding return value:
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    ELSE 
        -- delete base object and references:
        CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- check if there was an error:
        IF l_retValue = c_ALL_RIGHT THEN 
            -- delete object type specific data:
            -- (delete all type specific tuples which are not within
            -- ibs_Object)
            -- delete all rights for the deleted group:
            CALL IBSDEV1.p_Rights$deleteAllUserRights(l_id);

            -- actualize all cumulated rights:
            CALL IBSDEV1.p_Rights$updateRightsCumGroup(l_id);
            -- delete all the entries in ibs_GroupUser:
            SET l_sqlcode = 0;
            DELETE FROM IBSDEV1.ibs_GroupUser
            WHERE userid = l_id
                OR groupid = l_id
                OR origGroupId = l_id
                OR POSSTR(idPath, CHAR (l_id)) > 0;

            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'delete group/user data';
                GOTO exception1;
            END IF;
            -- set object as deleted:
            SET l_sqlcode = 0;
            UPDATE IBSDEV1.ibs_Group
            SET state = c_ST_DELETED
            WHERE id = l_id;
            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'update group state';
                GOTO exception1;
            END IF;
        END IF;
        -- check if there occurred an error:
        IF l_retValue = c_ALL_RIGHT THEN 
            COMMIT;
        ELSE 
            ROLLBACK;
        END IF;
    END IF;
    -- return the state value:
    RETURN l_retValue;
exception1:
  
    -- roll back to the beginning of the transaction:
    ROLLBACK;
NonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Group_01$delete', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s, 'ai_op', ai_op, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Group_01$delete

--------------------------------------------------------------------------------
-- Add a new user to a group and set the rights of the group on the user. <BR>
-- If there are already rights set the new rights are added to the existing
-- rights. <BR>
-- The rights for the user are not cumulated.
--
-- @input parameters:
-- @param   ai_groupId          Id of the group where the user shall be added.
-- @param   ai_userId           Id of the user to be added.
-- @param   ai_userOid          Oid of the user to be added.
-- @param   ai_rights           Rights to set for the group on the user.
--                              null ... don't set any rights
--                              0 ...... don't set any rights
--                              -1 ..... set default rights
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$addUserSetRNoCum');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$addUserSetRNoCum(
    IN  ai_groupId          INT,
    IN  ai_userId           INT,
    IN  ai_userOid          CHAR (8) FOR BIT DATA,
    IN  ai_rights           INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- input parameters:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_rights        INT;            -- the current rights
    DECLARE l_superGroupId  INT;            -- id of actual super group
    DECLARE l_idPath        VARCHAR (254);   -- posNoPath of the group
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    -- store the relationships with all groups which are above the actual 
    -- one:
    -- define cursor:
    DECLARE groupUserCursor CURSOR WITH HOLD FOR 
    SELECT groupId, idPath 
    FROM IBSDEV1.ibs_GroupUser
    WHERE CAST(userId AS INT) = CAST(ai_groupId AS INT);

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
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = ai_rights;

-- body:
    -- insert user into group:
    INSERT INTO IBSDEV1.ibs_GroupUser
        (state, groupId, userId, roleId, origGroupId, idPath)
    VALUES  (2, ai_groupId, ai_userId, 0, ai_groupId, CHAR (ai_groupId));
  
    -- set the rights of the group on the user:
    IF l_rights = -1 THEN 
        -- get the rights to be set:
        SELECT SUM (id) 
        INTO l_rights
        FROM IBSDEV1.ibs_Operation
        WHERE name IN ('view');
    END IF;
    IF l_rights <> 0 THEN 
        CALL IBSDEV1.p_Rights$addRights(ai_userOid, ai_groupId, l_rights, 1);
    END IF;
    -- store the relationships with all groups which are above the actual 
    -- open the cursor:
    OPEN groupUserCursor;
  
    -- get the first user:
    SET l_sqlcode = 0;
    FETCH FROM groupUserCursor INTO l_superGroupId, l_idPath;
    SET l_sqlstatus = l_sqlcode;
  
    -- loop through all found users:
    WHILE l_sqlcode <> 100 DO
        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
            -- insert user into all groups where this group is part of:
            INSERT INTO IBSDEV1.ibs_GroupUser
                (state, groupId, userId, roleId, origGroupId,
                idPath)
            VALUES  (2, l_superGroupId, ai_userId, 0, ai_groupId,
                l_idPath);
        END IF;
    
        -- get next user:
        SET l_sqlcode = 0;
        FETCH FROM groupUserCursor INTO l_superGroupId, l_idPath;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Group_01$addUserSetRNoCum


--------------------------------------------------------------------------------
-- Add a new user to a group and set the rights of the group on the user. <BR>
-- If there are already rights set the new rights are added to the existing
-- rights. <BR>
-- The rights for the user are newly cumulated at the end of this procedure.
--
-- @input parameters:
-- @param   ai_userId           Id of the user who is adding the user.
-- @param   ai_groupOid         Oid of the group where the user shall be added.
-- @param   ai_userOid          Oid of the user to be added.
-- @param   ai_roleOid          Oid of the role to be added. 
-- @param   ai_rights           Rights to set for the group on the user.
--                              null ... don't set any rights
--                              0 ...... don't set any rights
--                              -1 ..... set default rights
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$addUserSetRights');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$addUserSetRights
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_groupOid         CHAR (8) FOR BIT DATA,
    IN  ai_userOid          CHAR (8) FOR BIT DATA,
    IN  ai_roleOid          CHAR (8) FOR BIT DATA,
    IN  ai_rights           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_groupId       INT;            -- id of group to add the user
    DECLARE l_uUserId       INT;            -- id of user to be added
    DECLARE l_roleId        INT;            -- id of the role of the user
                                            -- within the group
    DECLARE l_rights        INT;            -- the current rights
    DECLARE l_superGroupId  INT;            -- id of actual super group
    DECLARE l_idPath        VARCHAR (254);   -- posNoPath of the group
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = ai_rights;

-- body:
    SELECT  id
    INTO    l_groupId
    FROM    IBSDEV1.ibs_Group
    WHERE   oid = ai_groupOid;

    -- get the id of the user to be added:
    SELECT  id
    INTO    l_uUserId
    FROM    IBSDEV1.ibs_User
    WHERE   oid = ai_userOid;
  
    -- insert user into group:
    CALL IBSDEV1.p_Group_01$addUserSetRNoCum(l_groupId, l_uUserId, ai_userOid,
        ai_rights);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- check if there was a problem:
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- actualize all cumulated rights:
        CALL IBSDEV1.p_Rights$updateRightsCumUser (l_uUserId);
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;

--------------------------------------------------------------------------------
-- Add a new user to a group. <BR>
--
-- @input parameters:
-- @param   @userId             ID of the user who is adding the user.
-- @param   @groupId            Id of the group where the user shall be added.
-- @param   @userOid            Id of the user to be added.
-- @param   @roleOid            Id of the role to be added. 
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$addUser');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$addUser
(
    -- input parameters:
    IN ai_userId            INT,
    IN ai_groupOid          CHAR (8) FOR BIT DATA,
    IN ai_userOid           CHAR (8) FOR BIT DATA,
    IN ai_roleOid           CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_ALL_RIGHT     INT;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT = 1;
    -- initialize return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- get the rights of the group on the user:
    SELECT  SUM (id)
    INTO    l_rights
    FROM    IBSDEV1.ibs_Operation
    WHERE   name IN ('view');
  
    CALL IBSDEV1.p_Group_01$addUserSetRights
        (ai_userId, ai_groupOid, ai_userOid,
        ai_roleOid, l_rights);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- return the state value
    RETURN l_retValue;
END;
-- p_Group_01$addUser


--------------------------------------------------------------------------------
-- Add a group to another group determined by their ids. <BR>
-- This function does not use any transactions, so it may be called from any
-- kind of code.
--
-- @input parameters:
-- @param   ai_majorGroupId     Id of the group where the group shall be added.
-- @param   ai_minorGroupId     Id of the group to be added.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_OK                  An error occurred.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$addGroupId');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$addGroupId(
    -- input parameters:
    IN  ai_majorGroupId INT,
    IN  ai_minorGroupId INT
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;

/*
    DECLARE groupUserCursor CURSOR WITH HOLD FOR 
                        SELECT  userId, origGroupId, idPath
                        FROM IBSDEV1.   ibs_GroupUser
                        WHERE   groupId = ai_minorGroupId 
                        UNION
                        SELECT  ai_minorGroupId AS userId,
                            ai_majorGroupId AS origGroupId, 
                            CHAR (ai_minorGroupId) AS idPath
                        FROM SYSIBM.SYSDUMMY1;
*/

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rowCount          = 0;

-- body:
    SET l_sqlcode = 0;
    SELECT  COUNT (*) 
    INTO    l_rowCount
    FROM    IBSDEV1.ibs_GroupUser
    WHERE   groupId = ai_majorGroupId
        AND userId = ai_majorGroupId
        AND groupId = origGroupId;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)
    THEN
        SET l_ePos = 'check if group relationship already exists';
        GOTO exception1;
    END IF;
  
    IF (l_rowCount = 0)
    THEN 
        -- insert the sub group into the super group and generate all inherited
        -- tuples:
        INSERT  INTO IBSDEV1.ibs_GroupUser
                (state, groupId, userId, roleId, origGroupId,
                idPath)
        SELECT  2, g.groupId, u.userId, 0, u.origGroupId,
                u.idPath || g.idPath
        FROM    (
                    SELECT  userId, origGroupId, idPath
                    FROM    IBSDEV1.ibs_GroupUser
                    WHERE   groupId = ai_minorGroupId
                    UNION
                    SELECT  ai_minorGroupId AS userId,
                            ai_majorGroupId AS origGroupId, 
                            IBSDEV1.p_intToBinary (ai_minorGroupId) AS idPath
                    FROM    SYSIBM.SYSDUMMY1
                ) u,
                (
                    SELECT  groupId, idPath
                    FROM    IBSDEV1.ibs_GroupUser
                    WHERE   LOCATE (IBSDEV1.p_intToBinary (ai_majorGroupId),
                                    idPath) = 1
                    UNION
                    SELECT  ai_majorGroupId AS groupId,
                            IBSDEV1.p_intToBinary (ai_majorGroupId) AS idPath
                    FROM    SYSIBM.SYSDUMMY1
                ) g;
/*
        OPEN groupUserCursor;

        SET l_sqlcode = 0;

        INSERT  INTO IBSDEV1.ibs_GroupUser
                (state, groupId, userId, roleId, origGroupId, idPath)
        SELECT  2, 0, userId, 0, origGroupId, idPath
        FROM    IBSDEV1.groupUserCursor;

        CLOSE groupUserCursor;    
*/
    
        -- check if there occurred an error:
        IF (l_sqlcode <> 0)
        THEN
            SET l_ePos = 'insert';
            GOTO exception1;
        END IF;
        -- actualize all cumulated rights:
        CALL IBSDEV1.p_Rights$updateRightsCumGroup (ai_minorGroupId);
    END IF;

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Group_01$addGroupId', l_sqlcode, l_ePos,
        'ai_majorGroupId', ai_majorGroupId, '', '',
        'ai_minorGroupId', ai_minorGroupId, '', '',
        'l_rowCount', l_rowCount, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Group_01$addGroupId


--------------------------------------------------------------------------------
-- Add a group to another group. <BR>
-- This procedure contains a TRANSACTION block, so it is not allowed to call it
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is adding the group.
-- @param   ai_majorGroupOid    Oid of the group where the group shall be
--                              added.
-- @param   ai_minorGroupOid    Oid of the group to be added.
-- @param   ai_roleOid          Oid of the role to be added.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_OK                  An error occurred.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$addGroup');


-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$addGroup(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_majorGroupOid    CHAR (8) FOR BIT DATA,
    IN  ai_minorGroupOid    CHAR (8) FOR BIT DATA,
    IN  ai_roleOid          CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- counter
    DECLARE l_majorGroupId  INT;            -- id of major group
    DECLARE l_minorGroupId  INT;            -- id of minor group
    DECLARE l_sqlcode       INT DEFAULT 0;
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
    SET l_retValue          = c_ALL_RIGHT;
  
-- body:
    -- get id of major group:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_majorGroupId
    FROM IBSDEV1.ibs_Group
    WHERE oid = ai_majorGroupOid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get major group id';
        GOTO nonTransactionException;
    END IF;
  
    -- get id of minor group:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_minorGroupId
    FROM IBSDEV1.ibs_Group
    WHERE oid = ai_minorGroupOid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get minor group id';
        GOTO nonTransactionException;
    END IF;
    -- add the minor group to the major group:
    CALL IBSDEV1.p_Group_01$addGroupId(l_majorGroupId, l_minorGroupId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- finish the transaction:
    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT THEN 
        COMMIT;
    ELSE 
        ROLLBACK;
    END IF;
    -- return the state value:
    RETURN l_retValue;

exception1:
  
    -- roll back to the beginning of the transaction:
    ROLLBACK;
nonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Group_01$addGroup', l_sqlcode, l_ePos,
        'l_majorGroupId', l_majorGroupId, '', '', 'l_minorGroupId', l_minorGroupId,
        '', '', 'ai_userId', ai_userId, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Group_01$addGroup

--------------------------------------------------------------------------------
-- Delete a user from a group. <BR>
-- The rights for the user are not cumulated. There is also no rights check
-- done.
--
-- @input parameters:
-- @param   ai_groupId          Id of the group where the user shall be deleted.
-- @param   ai_userId           Id of the user to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$delUserNoCum');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$delUserNoCum(
    -- input parameters:
    IN  ai_groupId          INT,
    IN  ai_userId           INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_rights        INT;            -- the current rights
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
-- body:
    -- check if the group is a system group:
    IF EXISTS   (
                    SELECT id 
                    FROM IBSDEV1.ibs_Domain_01
                    WHERE allGroupId = ai_groupId
                )
    THEN 
        -- set corresponding return value:
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    ELSE 
        -- user may be deleted
        -- delete user from all groups where the origGroupId is the GroupId:
        DELETE FROM IBSDEV1.ibs_GroupUser
        WHERE origGroupId = ai_groupId
            AND userId = ai_userId;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Group_01$delUserNoCum

--------------------------------------------------------------------------------
-- Delete a user from a group. <BR>
-- The rights for the user are newly cumulated at the end of this procedure.
--
-- @input parameters:
-- @param   ai_userId           Id of the user who is adding the user.
-- @param   ai_op               Operation to be performed (used for rights 
--                              check).
-- @param   ai_groupOid_s       Oid of the group where the user shall be
--                              deleted.
-- @param   ai_userOid_s        Oid of the group to be deleted.
-- @param   ai_roleOid_s        Oid of the role to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$delUser');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$delUser(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_groupOid_s       VARCHAR (18),
    IN  ai_userOid_s        VARCHAR (18),
    IN  ai_roleOid_s        VARCHAR (18)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_groupId       INT;            -- id of group to add the user
    DECLARE l_uUserId       INT;            -- id of user to be added
    DECLARE l_groupOid      CHAR (8) FOR BIT DATA;        -- oid of group
    DECLARE l_userOid       CHAR (8) FOR BIT DATA;        -- oid of user
    DECLARE l_rights        INT;            -- the current rights
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
-- body:
    -- convert string representations to oids:
    CALL IBSDEV1.p_stringToByte (ai_groupOid_s, l_groupOid);
    CALL IBSDEV1.p_stringToByte (ai_userOid_s, l_userOid);
    -- get the user id:

    SELECT id
    INTO l_uUserId
    FROM IBSDEV1.ibs_User
    WHERE oid = l_userOid;
  
    -- get the group id:
    SELECT id
    INTO l_groupId
    FROM IBSDEV1.ibs_Group
    WHERE oid = l_groupOid;
  
    -- check if the group is a system group:
    IF EXISTS   (
                    SELECT *
                    FROM    ibs_Domain_01
                    WHERE   allGroupId = l_groupId
                )
    THEN 
        -- set corresponding return value:
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
        SET l_rights = ai_op - 1;
    ELSE 
        -- user may be deleted
        -- get rights for this user:
        CALL IBSDEV1.p_Rights$checkRights(l_userOid, l_groupOid, ai_userId, ai_op,
            l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;
    END IF;
    -- check if the user has the necessary rights
    IF l_rights = ai_op THEN 
        -- delete user from the group:
        CALL IBSDEV1.p_Group_01$delUserNoCum(l_groupId, l_uUserId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
       -- check if everything was o.k.:
        IF l_retValue = c_ALL_RIGHT THEN 
            -- actualize all cumulated rights:
            CALL IBSDEV1.p_Rights$updateRightsCumUser(l_uUserId);
        END IF;
        COMMIT;
    ELSE 
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Group_01$delUser

--------------------------------------------------------------------------------
-- Delete a group from another group determined by their ids. <BR>
-- This function does not use any transactions, so it may be called from any
-- kind of code.
--
-- @input parameters:
-- @param   ai_majorGroupId     Id of the group where the group shall be
--                              deleted.
-- @param   ai_minorGroupId     Id of the group to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_OK                  An error occurred.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$delGroupId');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$delGroupId(
    -- input parameters:
    IN   ai_majorGroupId    INT,
    IN   ai_minorGroupId    INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;

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
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rowCount          = 0;
-- body:
    -- check if the sub group is within the super group:
    SET l_sqlcode = 0;
    SELECT COUNT(*) 
    INTO l_rowCount
    FROM IBSDEV1.ibs_GroupUser
    WHERE groupId = ai_majorGroupId
        AND userId = ai_minorGroupId
        AND groupId = origGroupId;
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'check if group relationship exists';
        GOTO exception1;
    END IF;

    IF l_rowCount > 0 THEN 
        -- delete the sub group from the super group and drop all inherited
        -- tuples:
        SET l_sqlcode = 0;
        DELETE FROM IBSDEV1.ibs_GroupUser
        WHERE POSSTR(idPath, CHAR (ai_minorGroupId) || CHAR (ai_majorGroupId)) > 0;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'delete';
            GOTO exception1;
        END IF;
        -- actualize all cumulated rights:
        CALL IBSDEV1.p_Rights$updateRightsCumGroup(ai_minorGroupId);
    END IF;
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Group_01$delGroupId', l_sqlcode, l_ePos,
        'ai_majorGroupId', ai_majorGroupId, '', '', 'ai_minorGroupId', ai_minorGroupId,
        '', '', 'l_rowCount', l_rowCount, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Group_01$delGroupId

--------------------------------------------------------------------------------
-- Delete a group from another group. <BR>
-- There is also a rights check done in this procedure. <BR>
-- This procedure contains a TRANSACTION block, so it is not allowed to call it
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is adding the group.
-- @param   ai_op               Operation to be performed (used for rights 
--                              check).
-- @param   ai_majorGroupOid    Oid of the group where the group shall be
--                              added.
-- @param   ai_minorGroupOid    Oid of the group to be added.
-- @param   ai_roleOid          Oid of the role to be added.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  NOT_OK                  An error occurred.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$delGroup');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$delGroup(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_majorGroupOid_s  VARCHAR (18),
    IN  ai_minorGroupOid_s  VARCHAR (18),
    IN  ai_roleOid_s        VARCHAR (18)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- counter
    DECLARE l_majorGroupOid CHAR (8) FOR BIT DATA;        -- oid of major group
    DECLARE l_minorGroupOid CHAR (8) FOR BIT DATA;        -- oid of minor group
    DECLARE l_majorGroupId  INT;            -- id of major group
    DECLARE l_minorGroupId  INT;            -- id of minor group
    DECLARE l_rights        INT;            -- the rights of the user on the
                                            -- current group
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- convert oid strings to oids:
    CALL IBSDEV1.p_stringToByte (ai_majorGroupOid_s, l_majorGroupOid);
    CALL IBSDEV1.p_stringToByte (ai_minorGroupOid_s, l_minorGroupOid);
    -- get id of major group:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_majorGroupId
    FROM IBSDEV1.ibs_Group
    WHERE oid = l_majorGroupOid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get major group id';
        GOTO nonTransactionException;
    END IF;
  
    -- get id of minor group:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_minorGroupId
    FROM IBSDEV1.ibs_Group
    WHERE oid = l_minorGroupOid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get minor group id';
        GOTO nonTransactionException;
    END IF;
    -- at this point we know that both the group and the sub group exist.
    -- get rights for the current user:
    CALL IBSDEV1.p_Rights$checkRights(l_minorGroupOid, l_majorGroupOid,
        ai_userId, ai_op, l_rights);
    GET DIAGNOSTICS l_rights = RETURN_STATUS;
    -- check if the user has the necessary rights
    IF l_rights = ai_op THEN 
        -- delete the minor group from the major group:
        CALL IBSDEV1.p_Group_01$delGroupId(l_majorGroupId, l_minorGroupId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        -- finish the transaction:
        -- check if there occurred an error:
        IF l_retValue = c_ALL_RIGHT THEN 
            COMMIT;
        ELSE 
            ROLLBACK;
        END IF;
    ELSE 
        -- set corresponding return value:
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF;
    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
nonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Group_01$delGroup', l_sqlcode, l_ePos,
        'l_majorGroupId', l_majorGroupId, 'ai_majorGroupOid_s', 'ai_majorGroupOid_s',
        'l_minorGroupId', l_minorGroupId, 'ai_minorGroupOid_s', 'ai_minorGroupOid_s',
        'ai_userId', ai_userId, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
  
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Group_01$delGroup

--------------------------------------------------------------------------------
-- Copies a Group_01 object and all its values (incl. rights check). <BR>
-- This procedure contains a TRANSACTION block, so it is not allowed to call it
-- from within another TRANSACTION block.
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
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$BOCopy(
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
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT;           -- tuple not found
    DECLARE c_ST_ACTIVE     INT;            -- active state of object
    
    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_groupId       INT;            -- id of copied group
    DECLARE l_newGroupId    INT;            -- new id of the group
    DECLARE l_origGroupId   INT;            -- id of original group in group
                                            -- hierarchy
    DECLARE l_idPath        VARCHAR (254);   -- posNoPath of group/user
                                            -- relationship
    DECLARE l_userId        INT;            -- the actual user within the group
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
  
    DECLARE GroupUser_Cursor CURSOR WITH HOLD FOR 
    SELECT userId,
            CASE
                WHEN INTEGER(origGroupId) = l_groupId
                    THEN l_newGroupId
                ELSE origGroupId
            END  AS origGroupId,
            idPath || CHAR (l_newGroupId) AS idPath
    FROM IBSDEV1.ibs_GroupUser
    WHERE INTEGER(groupId ) IN  (
                                    SELECT INTEGER(userId) 
                                    FROM IBSDEV1.ibs_GroupUser
                                    WHERE groupId = l_groupId AND
                                        origGroupId = l_groupId
                                )
    UNION
    SELECT  *  
    FROM (
                    SELECT  temp_table3.userId, temp_table3.origGroupId,
                        CASE
                            WHEN u.id IS NULL 
                                THEN CHAR (temp_table3.userId) || CHAR (l_newGroupId)
                            ELSE CHAR (l_newGroupId)
                        END AS idPath
                    FROM (
                                    SELECT groupId, userId, origGroupId
                                    FROM IBSDEV1.   ibs_GroupUser
                                    WHERE   origGroupId = groupId
                                        AND groupId = l_groupId
                                ) AS temp_table3
                    LEFT JOIN ibs_User u ON userId = u.id
                    LEFT JOIN ibs_Group g2 ON userId = g2.id
                ) AS temp_table2(userId, origGroupId, idPath);

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
-- assign constants:
    SET c_NOT_OK        = 0;
    SET c_ALL_RIGHT     = 1;
    SET c_OBJECTNOTFOUND = 3;
    SET c_ST_ACTIVE     = 2;
-- initialize local variables:
    SET l_retValue      = c_ALL_RIGHT;
-- body:
    COMMIT; -- finish previous and begin new TRANSACTION 
    -- get the id of the group:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_groupId
    FROM IBSDEV1.ibs_Group
    WHERE oid = ai_oid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get group id';
        GOTO exception1;
    END IF;

    SET l_sqlcode = 0;
    -- make an insert for all type specific tables:
    INSERT INTO IBSDEV1.ibs_Group (oid, state, name, domainId)
    SELECT  ai_newOid, c_ST_ACTIVE, name, domainId
    FROM IBSDEV1.   ibs_Group
    WHERE   oid = ai_oid;
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'insert new group data';
        GOTO exception1;
    END IF;
    -- get the new id of the group:
    SET l_sqlcode = 0;

    SELECT id
    INTO l_newGroupId
    FROM IBSDEV1.ibs_Group
    WHERE oid = ai_newOid;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get new group id';
        GOTO exception1;
    END IF;
    -- open the cursor:
    OPEN GroupUser_Cursor;
    -- get the first user:
    SET l_sqlcode = 0;
    FETCH FROM GroupUser_Cursor INTO l_userId, l_origGroupId, l_idPath;
    SET l_sqlstatus = l_sqlcode;
    -- loop through all found tuples:
    WHILE l_sqlcode <> 100 AND l_retValue = c_ALL_RIGHT DO
        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
            -- check if user was originally in the copied group:
            IF l_origGroupId = l_groupId THEN 
                -- use the new id of the group instead:
                SET l_origGroupId = l_newGroupId;
            END IF;
            -- insert all users of old group into the new group:
            SET l_sqlcode = 0;
            INSERT INTO IBSDEV1.ibs_GroupUser
                (state, groupId, userId, roleId, origGroupId, idPath)
            VALUES  (c_ST_ACTIVE, l_newGroupId, l_userId, 0,
                l_origGroupId, l_idPath);
            GET DIAGNOSTICS l_rowcount = ROW_COUNT;
            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'insert records';
                GOTO cursorException;
            END IF;
            -- check if insert was performed correctly:
            IF l_rowCount <= 0 THEN 
                -- set corresponding return value:
                SET l_retValue = c_NOT_OK;
            END IF;
        END IF;
    
        -- get next tuple:
        SET l_sqlcode = 0;
        FETCH FROM GroupUser_Cursor INTO l_userId, l_origGroupId, l_idPath;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;
    -- close the not longer needed cursor:
--    CLOSE GroupUser_Cursor;
    -- actualize all cumulated rights:
    CALL IBSDEV1.p_Rights$updateRightsCum();
    -- finish the transaction:
    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT THEN 
        COMMIT;
    ELSE 
        ROLLBACK;
    END IF;
    -- return the state value:
    RETURN l_retValue;

cursorException:
    -- close the not longer needed cursor:
--    CLOSE GroupUser_Cursor;
    RETURN 1;
exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Group_01$BOCopy', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '', 'l_groupId', l_groupId, '','',
        'l_newGroupId', l_newGroupId, '', '', 'l_origGroupId',l_origGroupId,
        '', '', 'l_userId', l_userId, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Group_01$BOCopy


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
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Group_01$changeState');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Group_01$changeState(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_state            INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_ST_ACTIVE     INT;            -- active state
    DECLARE c_ST_CREATED    INT;            -- created state

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
  
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_oldState      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    SET c_ST_ACTIVE         = 2;
    SET c_ST_CREATED        = 4;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_containerId       = c_NOOID;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- actual state of the object
    SET l_oldState          = 0;
    -- get the actual container id and state of object:
    SET l_sqlcode = 0;

    SELECT containerId, state
    INTO l_containerId, l_oldState
    FROM IBSDEV1.ibs_Object
    WHERE oid = oid;

    -- check if the object exists:
    IF l_sqlcode = 0 THEN 
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(l_oid, l_containerId, ai_userId, ai_op, l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;
        -- check if the user has the necessary rights
        IF l_rights = ai_op THEN 
            -- check if the state transition from the actual state to the new
            -- state is allowed:
            -- not implemented yet
            -- set the new state for the object and all tabs:
            UPDATE IBSDEV1.ibs_Object
            SET state = ai_state
            WHERE oid = l_oid
                OR containerId = l_oid
                AND containerKind = 2
                AND state <> ai_state
                AND (
                        state = c_ST_ACTIVE
                        OR state = c_ST_CREATED
                    );
            -- update the state of the group tuple:
            UPDATE IBSDEV1.ibs_Group
            SET state = ai_state
            WHERE oid = l_oid;
            COMMIT;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Group_01$changeState
