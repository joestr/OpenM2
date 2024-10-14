--------------------------------------------------------------------------------
-- All stored procedures regarding the Person_01 Object. <BR>
-- 
-- @version     $Id: Person_01Proc.sql,v 1.6 2003/10/31 00:12:51 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020901
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new Person_01 Object (incl. rights check). <BR>
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
-- @param   @userOid            oid of assigned user
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Person_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Person_01$create
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
    DECLARE l_addressOid_s  VARCHAR (18);
    DECLARE l_personsOid_s  VARCHAR (18);
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
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- insert the other values
        INSERT INTO IBSDEV1.mad_Person_01 (oid, fullname, prefix, title, position, company,
            offemail, offhomepage, userOid)
        VALUES (l_oid, ai_name, '', '', '', '', '', '', c_NOOID);
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Person_01$create


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
CALL IBSDEV1.p_dropProc ('p_Person_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Person_01$change(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_title            VARCHAR (31),
    IN  ai_prefix           VARCHAR (15),
    IN  ai_position         VARCHAR (31),
    IN  ai_company          VARCHAR (63),
    IN  ai_offemail         VARCHAR (127),
    IN  ai_offhomepage      VARCHAR (255),
    IN  ai_userOid_s        VARCHAR (18)
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
    DECLARE l_userOid       CHAR (8) FOR BIT DATA;
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
    -- initialize local variables:
    CALL IBSDEV1.p_stringToByte (ai_userOid_s, l_userOid);
-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name, ai_validUntil,
       ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN 
        -- update the other values
        UPDATE IBSDEV1.mad_Person_01
        SET fullname = ai_name,
            title = ai_title,
            prefix = ai_prefix,
            position = ai_position,
            company = ai_company,
            offemail = ai_offemail,
            offhomepage = ai_offhomepage,
            userOid = userOid
        WHERE oid = oid;
    END IF;
  
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;

-- p_Person_01$change


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
-- @param   @userOid            oid of assigned user
--
-- @param   @maxlevels          Maximum of the levels allowed in the discussion
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Person_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Person_01$retrieve(
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
    OUT ao_title            VARCHAR (31),
    OUT ao_prefix           VARCHAR (15),
    OUT ao_position         VARCHAR (31),
    OUT ao_company          VARCHAR (63),
    OUT ao_offemail         VARCHAR (127),
    OUT ao_offhomepage      VARCHAR (255),
    OUT ao_adressId         CHAR (8) FOR BIT DATA,
    OUT ao_userOid          CHAR (8) FOR BIT DATA,
    OUT ao_userName         VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE l_ST_ACTIVE     INT;
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
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    SET l_ST_ACTIVE         = 2;
    -- initialize return values:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description,
        ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
        ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN 
        SELECT title, position, prefix, company, offemail, offhomepage, userOid
        INTO ao_title, ao_position, ao_prefix, ao_company, ao_offemail,
            ao_offhomepage, ao_userOid
        FROM IBSDEV1.mad_Person_01
        WHERE oid = l_oid;
    
        SELECT oid
        INTO ao_adressId
        FROM IBSDEV1.ibs_Object
        WHERE containerId = l_oid
            AND containerKind = 2 
            AND tVersionId = 16854785;
    
        -- get userName linked to the person:
        SELECT fullname
        INTO ao_userName
        FROM IBSDEV1.ibs_user
        WHERE oid = ao_userOid;
    END IF;

    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Person_01$retrieve

--------------------------------------------------------------------------------
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
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Person_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Person_01$delete(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    ---------------------------------------------------------------------------
    -- DEFINITIONS
    -- define return constants
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define right constants
    DECLARE c_RIGHT_DELETE  INT;
    -- define return values
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    -- define used variables
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
  
    -- set constants
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    SET c_RIGHT_DELETE      = 16;
    -- initialize return values
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
-- body:
    -- check if the object exists:
    IF l_rowcount > 0 THEN 
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(l_oid, l_containerId, ai_userId, ai_op, l_rights);
        -- check if the user has the necessary rights
        IF l_rights > 0 THEN 
            -- delete references to the object
            DELETE FROM IBSDEV1.ibs_Object
            WHERE linkedObjectId = l_oid;
            -- delete all values of object
            DELETE FROM IBSDEV1.mad_Person_01
            WHERE oid = l_oid;
            -- delete object itself
            DELETE FROM IBSDEV1.ibs_Object
            WHERE oid = l_oid;
            COMMIT;
        ELSE 
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Person_01$delete

--------------------------------------------------------------------------------
-- Copies a Person_01 object and all its values (incl. rights check). <BR>
--/
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Person_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Person_01$BOCopy(
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_newOid           CHAR (8) FOR BIT DATA
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
    -- make an insert for all type specific tables:
    INSERT INTO IBSDEV1.mad_Person_01 
        (oid, fullname, title, prefix, position, company, offemail,
        offhomepage, userOid)
    SELECT  ai_newOid, fullname, title, prefix, 
         position, company, offemail, offhomepage, userOid
    FROM IBSDEV1.   mad_Person_01 
    WHERE oid = ai_oid;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Person_01$BOCopy
