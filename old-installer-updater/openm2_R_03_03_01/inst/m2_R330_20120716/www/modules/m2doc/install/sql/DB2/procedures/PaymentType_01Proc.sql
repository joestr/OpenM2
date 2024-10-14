--------------------------------------------------------------------------------
-- All stored procedures regarding the PaymentType table. <BR>
-- 
-- @version     $Id: PaymentType_01Proc.sql,v 1.4 2003/10/31 00:12:50 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020901
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_userId               ID of the user who is creating the object.
-- @param   ai_op                   Operation to be performed (used for rights 
--                                  check).
-- @param   ai_tVersionId           Type of the new object.
-- @param   ai_name                 Name of the object.
-- @param   ai_containerId_s        ID of the container where object shall be 
--                                  created in.
-- @param   ai_containerKind        Kind of object/container relationship
-- @param   ai_isLink               Defines if the object is a link
-- @param   ai_linkedObjectId_s     If the object is a link this is the ID of
--                                  the where the link shows to.
-- @param   ai_description          Description of the object.
--
-- @output parameters:
-- @param   ao_oid_s                OID of the newly created object.
--
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT                       Action performed, values returned,
--                                  everything ok.
--  INSUFFICIENT_RIGHTS             User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_PaymentType_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_PaymentType_01$create
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
    -- common output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
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

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- insert default values
        INSERT INTO IBSDEV1.m2_PaymentType_01 (oid, paymentTypeId, name)
        VALUES (l_oid, -1, '');
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_PaymentType_01$create


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_id               ID of the object to be changed.
-- @param   ai_userId           ID of the user who is changing the object.
-- @param   ai_op               Operation to be performed (used for rights 
--                              check).
-- @param   ai_name             Name of the object.
-- @param   ai_validUntil       Date until which the object is valid.
-- @param   ai_description      Description of the object.
-- @param   ai_showInNews       Shall the currrent object be displayed in the 
--                              news?
-- @param   ai_paymentTypeId    Id of the payment type.
--
-- @output parameters:
--
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT                   Action performed, values returned,
--                              everything ok.
--  INSUFFICIENT_RIGHTS         User has no right to perform action.
--  OBJECTNOTFOUND              The required object was not found within the 
--                              database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_PaymentType_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_PaymentType_01$change(
    -- create the new procedure:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_paymentTypeId    INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid

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
    SET l_oid               = c_NOOID;
    SET l_retValue          = c_NOT_OK;

-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- set the new payment type id:
        UPDATE  IBSDEV1.m2_PaymentType_01
        SET     paymentTypeId = ai_paymentTypeId,
                name = ai_name
        WHERE   EXISTS  (
                            SELECT  *
                            FROM    IBSDEV1.m2_PaymentType_01
                            WHERE   oid = l_oid
                        );
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_PaymentType_01$change


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @param   ai_oid_s            ID of the object to be retrieved.
-- @param   ai_userId           Id of the user who is getting the data.
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
-- @param   ao_showInNews       The showInNews flag.
-- @param   ao_checkedOut       Is the object checked out?
-- @param   ao_checkOutDate     Date when the object was checked out.
-- @param   ao_checkOutUser     Oid of the user which checked out the object.
-- @param   ao_checkOutUserOid  Oid of the user which checked out the object.
--                              is only set if this user has the right to READ
--                              the checkOut user.
-- @param   ao_checkOutUserName Name of the user which checked out the object,
--                              is only set if this user has the right to read
--                              the checkOut-User.
-- @param   ao_paymentTypeId    Id of the payment type.
--
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT                   Action performed, values returned,
--                              everything ok.
--  INSUFFICIENT_RIGHTS         User has no right to perform action.
--  OBJECTNOTFOUND              The required object was not found within the 
--                              database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_PaymentType_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_PaymentType_01$retrieve(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- common output parameters:
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
    OUT ao_paymentTypeId    INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this operation
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid

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

    -- initialize local variables and return values:
    SET l_oid               = c_NOOID;
    SET l_retValue          = c_NOT_OK;

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
        -- retrieve object type specific data:
        SET l_sqlcode = 0;

        SELECT paymentTypeId
        INTO ao_paymentTypeId
        FROM IBSDEV1.m2_PaymentType_01
        WHERE oid = l_oid;

        -- check if retrieve was performed properly:
        IF l_sqlcode <> 0 THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_PaymentType_01$retrieve
