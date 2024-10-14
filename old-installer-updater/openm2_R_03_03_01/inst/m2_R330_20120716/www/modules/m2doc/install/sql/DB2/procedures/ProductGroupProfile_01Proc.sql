--------------------------------------------------------------------------------
-- Creates a ProductGroupProfile Object.
--
-- @version     $Id: ProductGroupProfile_01Proc.sql,v 1.4 2003/10/31 00:12:51 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020902
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
CALL IBSDEV1.p_dropProc ('p_ProductGrpProfile_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGrpProfile_01$create
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
    DECLARE SQLCODE         INT;

    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    ---------------------------------------------------------------------------

    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
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

    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- Insert the other values
        INSERT INTO IBSDEV1.m2_ProductGroupProfile_01 (oid, thumbAsImage)
        VALUES (l_oid, 0);
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_ProductGrpProfile_01$create


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @id                 ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         flag if object should be shown in newscontainer
--
-- @param   @prop1              Description of the first type specific property.
-- @param   @prop2              Description of the second type specific
--                              property.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductGrpProfile_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGrpProfile_01$change(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_code             VARCHAR (63),
    IN  ai_season           VARCHAR (63),
    IN  ai_thumbnail        VARCHAR (63),
    IN  ai_thumbAsImage     SMALLINT,
    IN  ai_image            VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_oldThumb      VARCHAR (63);
    DECLARE l_oldImage      VARCHAR (63);
    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE c_ALL_RIGHT     INT;
    -- define return values
    DECLARE l_retValue      INT;
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
    -- initialize return values
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN 
        SELECT thumbnail, image
        INTO l_oldThumb, l_oldImage
        FROM IBSDEV1.m2_ProductGroupProfile_01
        WHERE oid = l_oid;

        IF ai_thumbNail IS NULL THEN 
            SET ai_thumbnail = l_oldThumb;
        END IF;

        IF ai_image IS NULL THEN 
            SET ai_image = l_oldImage;
        END IF;
        -- update other values
        UPDATE IBSDEV1.m2_ProductGroupProfile_01
        SET code = ai_code,
            season = ai_season,
            thumbnail = ai_thumbnail,
            thumbAsImage = ai_thumbAsImage,
            image = ai_image
        WHERE oid = l_oid;
    END IF;

    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
--p_ProductGrpProfile_01$change

--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             Id of the user who is getting the data.
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
-- @param   @showInNews         flag if object should be shown in newscontainer
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
-- @param   @prop1              Description of the first property.
-- @param   @prop2              Description of the second property.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductGrpProfile_01$retrv');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGrpProfile_01$retrv(
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
    OUT ao_code             VARCHAR (63),
    OUT ao_season           VARCHAR (63),
    OUT ao_thumbnail        VARCHAR (63),
    OUT ao_thumbAsImage     SMALLINT,
    OUT ao_image            VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE c_ALL_RIGHT     INT;
    -- define return values
    DECLARE l_retValue      INT;
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
    -- initialize return values
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description,
        ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
        ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN 
        -----------------------specific table outread---------------------
        SELECT code, season, thumbnail, thumbAsImage, image
        INTO ao_code, ao_season, ao_thumbnail, ao_thumbAsImage, ao_image
        FROM IBSDEV1.m2_ProductGroupProfile_01
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_ProductGrpProfile_01$retrv

--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductGrpProfile_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGrpProfile_01$delete(
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
    -- definitions:
    -- define return constants
    DECLARE c_ALL_RIGHT     INT;
    -- define return values
    DECLARE l_retValue      INT;
    -- participants container
    DECLARE l_partContId    CHAR (8) FOR BIT DATA;
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
    -- initialize return values
    SET l_retValue          = c_ALL_RIGHT;
  
-- body:
    -- perform deletion of object:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN 
        -- delete object itself
        DELETE FROM IBSDEV1.m2_ProductGroupProfile_01
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_ProductGrpProfile_01$delete