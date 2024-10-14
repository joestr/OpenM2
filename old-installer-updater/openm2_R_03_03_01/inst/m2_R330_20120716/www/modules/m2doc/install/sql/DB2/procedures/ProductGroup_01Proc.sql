--------------------------------------------------------------------------------
-- Creates a ProductGroup_01 Object.
--
-- @version     $Id: ProductGroup_01Proc.sql,v 1.4 2003/10/31 00:12:51 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020902
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductGroup_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGroup_01$create
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

    -- locla variables:
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
        INSERT INTO IBSDEV1.m2_ProductGroup_01 (oid, productGroupProfileOid)
        VALUES (l_oid, c_NOOID);
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_ProductGroup_01$create


--------------------------------------------------------------------------------
--
-- ??Changes a ProductGroup_01 Object.
--
-- @version     1.00.0000, 24.12.1998
--
-- @author      Bernhard Walter   (BW)  981224
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
-- 
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductGroup_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGroup_01$change(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_productGroupProfileOid_s VARCHAR (18)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_productGroupProfileOid CHAR (8) FOR BIT DATA;
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

    CALL IBSDEV1.p_stringToByte (ai_productGroupProfileOid_s, l_productGroupProfileOid);
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
        -- update other values
        UPDATE IBSDEV1.m2_ProductGroup_01
        SET productGroupProfileOid = l_productGroupProfileOid
        WHERE oid = l_oid;
    END IF;
 
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_ProductGroup_01$change

--------------------------------------------------------------------------------
--
-- ??Retrieves a ProductGroup_01 Object.
--
-- @version     1.00.0000, 24.12.1998
--
-- @author      Bernhard Walter   (BW)  981224
--
-- @output parameters:
-- @param   @showInNews         Display object in the news.
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
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
-- 
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductGroup_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGroup_01$retrieve(
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
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_productGroupProfileOid_s VARCHAR (18),
    OUT ao_productGroupProfile  VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_productGroupProfileOid CHAR (8) FOR BIT DATA;
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
        -- get object specific values
        -- oid of referring ProductGroup
        SELECT pgp.oid, pgp.name 
        INTO l_productGroupProfileOid, ao_productGroupProfile
        FROM IBSDEV1.m2_ProductGroup_01 pg INNER JOIN IBSDEV1.ibs_Object pgp
            ON pg.productGroupProfileOid = pgp.oid
        WHERE pg.oid = l_oid;
        CALL IBSDEV1.p_byteToString (l_productGroupProfileOid, ao_productGroupProfileOid_s);
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_ProductGroup_01$retrieve


--------------------------------------------------------------------------------
--
-- Deletes a ProductGroup_01 Object.
--
-- @version     1.00.0000, 24.12.1998
--
-- @author      Bernhard Walter   (BW)  980524
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
-- 
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductGroup_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductGroup_01$delete(
    -- input parameters:
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
        -- delete special object
        DELETE FROM IBSDEV1.m2_ProductGroup_01
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
--p_ProductGroup_01$delete