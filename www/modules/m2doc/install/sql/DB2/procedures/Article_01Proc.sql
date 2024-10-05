--------------------------------------------------------------------------------
-- All stored procedures regarding the Article_01 Object. <BR>
--
-- @version     $Id: Article_01Proc.sql,v 1.4 2003/10/31 00:12:49 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020829
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new Article_01 Object (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_Article_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Article_01$create
(
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s     VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
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
    DECLARE l_posNoPath     VARCHAR (253);
    DECLARE l_discussionId  CHAR (8) FOR BIT DATA;
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
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN 
        -- retrieve the posNoPath of the entry

        SELECT posNoPath
        INTO l_posNoPath
        FROM IBSDEV1.ibs_Object
        WHERE oid = l_oid;


        -- retrieve the oid of the discussion

        SELECT oid
        INTO l_discussionId
        FROM IBSDEV1.ibs_Object
        WHERE (
                ( tVersionId = 16843521 ) OR
                ( tVersionId = 16845313 )
               )  AND l_posNoPath LIKE posNoPath || '%';

        -- insert the other values
        INSERT INTO IBSDEV1.m2_Article_01 (oid, content, discussionId)
        VALUES(l_oid, ai_description, l_discussionid);

        IF (SUBSTR (l_containerId, 1, 4) = X'01010501')
        THEN 
            UPDATE IBSDEV1.ibs_Object
            SET name =  (
                            SELECT 'AW: ' || name 
                            FROM IBSDEV1.ibs_Object
                            WHERE oid = l_containerId
                        )
            WHERE oid = l_oid;
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Article_01$create


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
-- @param   @showInNews         show in news flag.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Article_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Article_01$change(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- define local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
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
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, '', ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    UPDATE IBSDEV1.m2_Article_01
    SET state = (
                    SELECT o.state 
                    FROM IBSDEV1.ibs_Object o
                    WHERE o.oid = l_oid
                )
    WHERE oid = l_oid;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Article_01$change

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
-- @param   ao_changerName      Name of person who did the last change to the 
--                              object.
-- @param   ao_validUntil       Date until which the object is valid.
-- @param   ao_description      Description of the object.
-- @param   ao_showInNews       Flag if object should be shown in newscontainer
-- @param   ao_checkedOut       Is the object checked out?
-- @param   ao_checkOutDate     Date when the object was checked out
-- @param   ao_checkOutUser     id of the user which checked out the object
-- @param   ao_checkOutUserOid  Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   ao_checkOutUserName Name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
-- @param   ao_discussionType   Type of discussion.
-- @param   ao_hasSubEntries    Does the object have sub entries?
-- @param   ao_rights           Permissions of the current user on the object.
-- @param   ao_discussionId     Id of discussion where the object belongs to.
-- @param   ao_containerDescription Description of container of the object
--                              (out of ibs_Object).
--
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
CALL IBSDEV1.p_dropProc ('p_Article_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Article_01$retrieve(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
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
    OUT ao_discussionType   INT,
    OUT ao_hasSubEntries    INT,
    OUT ao_rights           INT,
    OUT ao_discussionId     CHAR (8) FOR BIT DATA,
    OUT ao_containerDescription   VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_DISC_BLACKBOARD INT;
    DECLARE c_DISC_DISCUSSION INT;
    DECLARE c_ST_ACTIVE     INT;
    DECLARE l_retValue      INT;
    DECLARE l_ePos          VARCHAR (255);
    DECLARE l_TV_Discussion_01 INT;
    DECLARE l_TV_Blackboard_01 INT;
    DECLARE l_TV_DiscEntry_01 INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
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
    SET c_DISC_BLACKBOARD   = 0;
    SET c_DISC_DISCUSSION   = 1;
    SET c_ST_ACTIVE         = 2;
  
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink,ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description,
        ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
        ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN 
        -- retrieve object type specific data:
        -- get the data of the tVersions:

        SELECT actVersion
        INTO l_TV_Discussion_01
        FROM IBSDEV1.ibs_Type
        WHERE code = 'Discussion';


        SELECT actVersion
        INTO l_TV_Blackboard_01
        FROM IBSDEV1.ibs_Type
        WHERE code = 'BlackBoard';

        SELECT actVersion
        INTO l_TV_DiscEntry_01
        FROM IBSDEV1.ibs_Type
        WHERE code = 'Article';

        -- retrieve the discussionId of the entry:
        SET l_sqlcode = 0;

        SELECT discussionId
        INTO ao_discussionId
        FROM IBSDEV1.m2_Article_01
        WHERE oid = l_oid;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'get discussionId';
            GOTO exception1;
        END IF;
        -- retrieve the type of the discussion:
        SET l_sqlcode = 0;

                    SELECT  CASE
                                WHEN tVersionId = l_TV_Blackboard_01
                                THEN c_DISC_BLACKBOARD
                                ELSE c_DISC_DISCUSSION
                            END
        INTO ao_discussionType
                    FROM IBSDEV1.ibs_Object
                    WHERE tVersionId IN 
                        (l_TV_Discussion_01, l_TV_Blackboard_01)
                        AND oid = ao_discussionId;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'get discussionType';
            GOTO exception1;
        END IF;
        -- retrieve if entry has subEntries:
        IF ao_discussionType = c_DISC_DISCUSSION THEN 
            -- get the number of sub entries of the current entry:
            SET l_sqlcode = 0;
            SELECT COUNT(*) 
            INTO ao_hasSubEntries
            FROM ibs_Object
            WHERE tVersionId = l_TV_DiscEntry_01
                AND state = c_ST_ACTIVE
                AND containerId = l_oid;
            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'get subEntries';
                GOTO exception1;
            END IF;
        ELSE 
            -- there are no sub entries allowed:
            SET ao_hasSubEntries = 0;
        END IF;
        -- retrieve the description of the container:
        SET l_sqlcode = 0;

        SELECT description
        INTO ao_containerDescription
        FROM IBSDEV1.ibs_Object
       WHERE oid = ao_containerId;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'get containerDescription';
            GOTO exception1;
        END IF;
        CALL IBSDEV1.p_Rights$getRights(l_oid, ai_userId, ao_rights);
    END IF;
    -- return the state value:
    RETURN l_retValue;
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Article_01$retrieve', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s, 'ai_op', ai_op, '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Article_01$retrieve


--------------------------------------------------------------------------------
-- Deletes a Discussion_01 object and all its values (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_Article_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Article_01$delete(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- definitions:
    -- define return constants:
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- define local variables:
    -- initialize local variables:
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
  
    -- initialize return values:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- delete base object:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
/*
        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
	    DELETE  m2_Article_01
            WHERE   oid = @oid

            DELETE ibs_Object
            WHERE  oid IN (SELECT oid 
                           FROM ibs_Object 
                           LIKE posNoPath LIKE @posNoPath + '%')
        END -- if operation properly performed
*/        
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Article_01$delete

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Article_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Article_01$BOCopy(
    -- input parameters:
    IN ai_oid               CHAR (8) FOR BIT DATA,
    IN ai_userId            INT,
    IN ai_newOid            CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- local variable ... OID of the discussion in which this Article is part of
    DECLARE l_discussionId  CHAR (8) FOR BIT DATA;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_copiedDisc    SMALLINT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
    SET l_copiedDisc        = 0;
    -- retrieve the posNoPath of the entry

    SELECT posNoPath
    INTO l_posNoPath
    FROM IBSDEV1.ibs_Object
    WHERE oid = ai_newOid;

    -- retrieve the oid of the discussion

    SELECT oid
    INTO l_discussionId
    FROM IBSDEV1.ibs_Object
    WHERE (
             ( tVersionId = 16843521 )OR
             ( tVersionId = 16845313 )
          )  AND l_posNoPath LIKE (posNoPath || '%');

    SELECT COUNT(*) 
    INTO l_copiedDisc
    FROM IBSDEV1.ibs_Copy
    WHERE oldOid = l_discussionId;
  
    IF l_copiedDisc = 1 THEN 
        SELECT newOid
        INTO l_discussionId
        FROM IBSDEV1.ibs_Copy
        WHERE oldOid = l_discussionId;
    END IF;
  
    -- make an insert for all type specific tables:
    INSERT INTO IBSDEV1.m2_Article_01 
        (oid, content, discussionId)
    SELECT  ai_newOid, b.content, l_discussionId
    FROM IBSDEV1.   m2_Article_01 b
    WHERE   b.oid = ai_oid;
  
    COMMIT;
  
    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF;
  
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Article_01$BOCopy

--------------------------------------------------------------------------------
-- Change the state of an existing object. <BR>
--
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be changed.
-- @param   ai_userId           ID of the user who is changing the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_state            The new state of the object.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Article_01$changeState');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Article_01$changeState(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_state            INT
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
    DECLARE c_ST_ACTIVE     INT;            -- active state
    DECLARE c_ST_CREATED    INT;            -- created state
    DECLARE c_RIGHT_UPDATE  INT;
    DECLARE c_RIGHT_INSERT  INT;

    -- local variables:
    DECLARE SQLCODE         INT;
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
    SET c_RIGHT_UPDATE      = 8;
    SET c_RIGHT_INSERT      = 1;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_containerId       = c_NOOID;
    SET l_oldState          = 0;

-- body:
    -- conversions: (VARCHAR (18)) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- get the actual container id and state of object:
    SELECT  containerId, state
    INTO    l_containerId, l_oldState
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid;

    SELECT  COUNT(*) 
    INTO    l_rowcount
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid;

    -- check if the object exists:
    IF l_rowcount > 0 THEN 
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
                OR  (
                        containerId = l_oid 
                        AND containerKind = 2 
                        AND state <> ai_state
                        AND (
                                state = c_ST_ACTIVE
                                OR state = c_ST_CREATED
                            )
                    );
      
            -- update the state of the entry tuple:
            UPDATE IBSDEV1.m2_Article_01
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
-- p_Article_01$changeState


