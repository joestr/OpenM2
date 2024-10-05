--------------------------------------------------------------------------------
-- All stored procedures regarding the Company_01 Object. <BR>
-- 
-- @version     $Id: Company_01Proc.sql,v 1.5 2003/10/31 16:29:02 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020829
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new Company_01 Object (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_Company_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Company_01$create
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
    DECLARE l_contactsOid_s VARCHAR (18);
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

    IF l_retValue = c_ALL_RIGHT THEN 
        -- insert the other values
        INSERT INTO IBSDEV1.mad_Company_01 
            (oid, owner, manager, legal_form, mwst)
        VALUES (l_oid, '', '', '', 0);
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Company_01$create

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
CALL IBSDEV1.p_dropProc ('p_Company_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Company_01$change(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_compowner        VARCHAR (63),
    IN  ai_manager          VARCHAR (63),
    IN  ai_legal_form       VARCHAR (63),
    IN  ai_mwst             INT
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
    -- initialize local variables:
-- body:
   -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN 
        -- update the other values
        UPDATE IBSDEV1.mad_Company_01
        SET owner = ai_compowner,
            manager = ai_manager,
            legal_form = ai_legal_form,
            mwst = ai_mwst
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Company_01$change

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
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Company_01$retrieve');

-- create the  new procedure:
CREATE PROCEDURE IBSDEV1.p_Company_01$retrieve(
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
    OUT ao_compowner        VARCHAR (63),
    OUT ao_manager          VARCHAR (63),
    OUT ao_legal_form       VARCHAR (63),
    OUT ao_addressId        CHAR (8) FOR BIT DATA,
    OUT ao_contactId        CHAR (8) FOR BIT DATA,
    OUT ao_mwst             INT
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
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_ContainerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged, ao_changer,
        ao_changerName, ao_validUntil, ao_description, ao_showInNews,
        ao_checkedOut, ao_checkOutDate, ao_checkOutUser, ao_checkOutUserOid,
        ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN 

        SELECT owner, manager, legal_form, mwst
        INTO ao_compowner, ao_manager, ao_legal_form, ao_mwst
        FROM IBSDEV1.mad_Company_01
        WHERE oid = l_oid;

        SELECT oid
        INTO ao_addressId
        FROM IBSDEV1.ibs_Object
        WHERE containerKind = 2
             AND tVersionId = 16854785
             AND containerId = l_oid;

        SELECT oid
        INTO ao_contactId
        FROM IBSDEV1.ibs_Object
        WHERE containerKind = 2 
             AND tVersionId = 16853761
             AND containerId = l_oid;

    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Company_01$retrieve

--------------------------------------------------------------------------------
-- Deletes a Company_01 object and all its values (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_Company_01$delete');

-- create the  new procedure:
CREATE PROCEDURE IBSDEV1.p_Company_01$delete(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
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
    -- all references and the object itself are deleted (plus rights)
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
/*
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -- delete all values of object
            DELETE  mad_Company_01
            WHERE   oid = @oid

            DELETE m2_Address_01
            WHERE  oid IN (SELECT oid 
                           FROM ibs_Object
                           WHERE posNoPath LIKE @posNoPath + '%')

            DELETE mad_Person_01
            WHERE  oid IN (SELECT oid 
                           FROM ibs_Object 
                           WHERE posNoPath LIKE @posNoPath + '%')

 			DELETE ibs_Object
			WHERE posNoPath LIKE @posNoPath + '%'
        END	
*/        
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Company_01$delete

--------------------------------------------------------------------------------
-- Copies a Company_01 object and all its values (incl. rights check). <BR>
--------------------------------------------------------------------------------
CALL IBSDEV1.p_dropProc ('p_Company_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Company_01$BOCopy(
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
    INSERT INTO IBSDEV1.mad_Company_01 
        (oid, owner, manager, legal_form, mwst)
    SELECT  ai_newOid, owner, manager, legal_form, mwst
    FROM IBSDEV1.   mad_Company_01 
    WHERE oid = ai_oid;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    COMMIT;
    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Company_01$BOCopy
