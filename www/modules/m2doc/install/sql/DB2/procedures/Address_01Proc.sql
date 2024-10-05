--------------------------------------------------------------------------------
-- All stored procedures regarding the Adress_01 Object. <BR>
-- 
-- @version     $Revision: 1.6 $, $Date: 2003/10/31 16:29:02 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
--
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Address_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Address_01$create
(
    -- input parameters:
    IN  ai_USERID           INT,
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
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE SQLCODE         INT;
--    DECLARE l_containerId   INT;
--    DECLARE l_linkedObjectId INT;
  
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

--    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
--    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- set constants:
    SET c_ALL_RIGHT         = 1;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;
  
-- body:
    -- create base object:

    CALL IBSDEV1.p_Object$performCreate(ai_USERID, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN            -- object created successfully?
        -- insert the other values
        INSERT INTO IBSDEV1.m2_Address_01
            (oid, street, zip, town, mailbox, country, tel, fax, email, homepage)
        VALUES (l_oid, '', '', '', '', '', '', '', '', '');
    END IF;

    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Address_01$create


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
CALL IBSDEV1.p_dropProc ('p_Address_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Address_01$change(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_USERID           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_street           VARCHAR (63),
    IN  ai_zip              VARCHAR (15),
    IN  ai_town             VARCHAR (63),
    IN  ai_mailbox          VARCHAR (15),
    IN  ai_country          VARCHAR (31),
    IN  ai_tel              VARCHAR (63),
    IN  ai_fax              VARCHAR (63),
    IN  ai_email            VARCHAR (127),
    IN  ai_homepage         VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_name          VARCHAR (63);
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
    IF ai_name IS NULL THEN 
        SET ai_name = 'Adresse';
    END IF;
-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_USERID, ai_op, ai_name,
        ai_validUntil, '', ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN                 -- operation properly performed?
        -- update further information
        UPDATE IBSDEV1. m2_Address_01
        SET street = ai_street,
            zip = ai_zip,
            town = ai_town,
            mailbox = ai_mailbox,
            country = ai_country,
            tel = ai_tel,
            fax = ai_fax,
            email = ai_email,
            homepage = ai_homepage
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Adress_01$change


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
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Address_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Address_01$retrieve(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_USERID           INT,
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
    OUT ao_checkOutUserName   VARCHAR (63),
    OUT ao_street           VARCHAR (63),
    OUT ao_zip              VARCHAR (15),
    OUT ao_town             VARCHAR (63),
    OUT ao_mailbox          VARCHAR (15),
    OUT ao_country          VARCHAR (31),
    OUT ao_tel              VARCHAR (63),
    OUT ao_fax              VARCHAR (63),
    OUT ao_email            VARCHAR (127),
    OUT ao_homepage         VARCHAR (255)
    )
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
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_USERID, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
        ao_ownerName, ao_creationDate, ao_creator, ao_creatorName, 
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN 
        SELECT street, zip, town, mailbox, country, tel, fax, email, homepage
        INTO ao_street, ao_zip, ao_town, ao_mailbox, ao_country, ao_tel,
            ao_fax, ao_email, ao_homepage
        FROM IBSDEV1.m2_Address_01
        WHERE oid = l_oid;
   END IF;
  
    COMMIT;
  
    -- return the state value
    RETURN l_retValue;
END;
-- p_Address_01$retrieve

--------------------------------------------------------------------------------
-- Deletes a diskussion_01 object and all its values (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_Address_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Address_01$delete(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_USERID           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
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
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_rights        INT;            -- return value of called proc.
    -- define used variables
    DECLARE l_containerId   INT;
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
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_USERID, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN 
        -- delete all values of object
        DELETE FROM IBSDEV1.m2_Address_01
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Address_01$delete


--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
-- 
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Address_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Address_01$BOCopy(
    -- common input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_USERID           INT,
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
    INSERT INTO IBSDEV1.m2_Address_01
        (oid, street, zip, town, mailbox, country, tel, fax, email, homepage)
    SELECT  ai_newOid, street, zip, town, mailbox, country, tel, fax, email, homepage
    FROM IBSDEV1.m2_Address_01
    WHERE oid = ai_oid;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
  
    COMMIT;
    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN                 -- at least one row affected?
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Address_01$BOCopy
