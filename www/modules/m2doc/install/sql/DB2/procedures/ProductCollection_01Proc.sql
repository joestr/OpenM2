--------------------------------------------------------------------------------
-- Creates a ProductCollection Object.
--
-- @version     $Id: ProductCollection_01Proc.sql,v 1.4 2003/10/31 00:12:51 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020902
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductCollect_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductCollect_01$create
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
    -- conversionS (VARCHAR (18)) - all input objectids must be converted
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
        INSERT INTO IBSDEV1.m2_ProductCollection_01 (oid, validFrom)
        VALUES (l_oid, CURRENT TIMESTAMP);
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_ProductCollect_01$create


--------------------------------------------------------------------------------
--
-- Changes a ProductCollection Object.
--
-- @version     1.00.0000, 26.12.1998
--
-- @author      Bernhard Walter   (BW)  981226
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
-- 
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductCollect_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductCollect_01$change(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_cost             DECIMAL(19,4),
    IN  ai_costCurrency     VARCHAR (5),
    IN  ai_totalQuantity    INT,
    IN  ai_validFrom        TIMESTAMP,
    IN  ai_categoryOidX_s   VARCHAR (18),
    IN  ai_categoryOidY_s   VARCHAR (18),
    IN  ai_nrCodes          INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_oldImage      VARCHAR (63);
    DECLARE l_categoryOidX  CHAR (8) FOR BIT DATA;
    DECLARE l_categoryOidY  CHAR (8) FOR BIT DATA;
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
    CALL IBSDEV1.p_stringToByte (ai_categoryOidX_s, l_categoryOidX);
    CALL IBSDEV1.p_stringToByte (ai_categoryOidY_s, l_categoryOidY);
    -- set constants
    SET c_ALL_RIGHT         = 1;
    -- initialize return values
    SET l_retValue          = c_ALL_RIGHT;
    ---------------------------------------------------------------------------
    -- START
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN 
        -- update other values
        UPDATE IBSDEV1.m2_ProductCollection_01
        SET cost = ai_cost,
            costCurrency = ai_costCurrency,
            validFrom = ai_validFrom,
            totalQuantity = ai_totalQuantity,
            categoryOidX = l_categoryOidX,
            categoryOidY = l_categoryOidY,
            nrCodes = ai_nrCodes
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_ProductCollect_01$change

--------------------------------------------------------------------------------
--
-- Retrieves a ProductCollection Object.
--
-- @version     1.00.0000, 26.12.1998
--
-- @author      Bernhard Walter   (BW)  981226
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
CALL IBSDEV1.p_dropProc ('p_ProductCollect_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductCollect_01$retrieve(
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
    OUT ao_productOid       CHAR (8) FOR BIT DATA,
    OUT ao_nrCodes          INT,
    OUT ao_cost             DECIMAL(19,4),
    OUT ao_costCurrency     VARCHAR (5),
    OUT ao_totalQuantity    INT,
    OUT ao_validFrom        TIMESTAMP,
    OUT ao_categoryOidX     CHAR (8) FOR BIT DATA,
    OUT ao_categoryOidY     CHAR (8) FOR BIT DATA,
    OUT ao_nrCodes2         INT
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
        SELECT containerId
        INTO ao_containerId
        FROM IBSDEV1.ibs_Object
        WHERE oid = ao_containerId;
    
        -- select the number of codes
        SELECT COUNT(*) 
        INTO ao_nrCodes
        FROM IBSDEV1.m2_ProfileCategory_01 pc INNER JOIN
            IBSDEV1.m2_Product_01 p ON pc.productProfileOid = p.productProfileOid
        WHERE p.oid = ao_productOid;
        -- select values from collection table
        SELECT cost, costCurrency,totalQuantity, validFrom,
           categoryOidX, categoryOidY, nrCodes
        INTO ao_cost, ao_costCurrency, ao_totalQuantity, ao_validFrom,
            ao_categoryOidX, ao_categoryOidY, ao_nrCodes2
        FROM IBSDEV1.m2_ProductCollection_01
        WHERE oid = l_oid;
    
        SELECT  DISTINCT SUM (quantity)
        INTO ao_totalQuantity
        FROM IBSDEV1.v_ProductCollection$content
        WHERE collectionOid = l_oid
        GROUP BY categoryname;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_ProductCollect_01$retrieve


--------------------------------------------------------------------------------
--
-- Creates a new quantity entry in a product collection.
--
-- @version     1.00.0000, 15.01.1999
--
-- @author      Bernhard Walter   (BW)  990115
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductCollect_01$createQty');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductCollect_01$createQty
(
    IN  ai_collectionOid_s  VARCHAR (18),
    IN  ai_quantity         INT,
    OUT ao_id               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_collectionOid CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET ao_id               = 0;
    SET l_collectionOid     = c_NOOID;

-- body:
    -- select a new Id:
    SELECT coalesce(MAX (id) + 1, 1) 
    INTO ao_id
    FROM IBSDEV1.m2_ProductCollectionQty_01;
    -- insert a new tuple
    CALL IBSDEV1.p_stringToByte (ai_collectionOid_s, l_collectionOid);
    INSERT INTO IBSDEV1.m2_ProductCollectionQty_01 (id, collectionOid, quantity)
    VALUES	(ao_id, l_collectionOid, ai_quantity);
    COMMIT;
END;

--------------------------------------------------------------------------------
--
-- Creates a new value entry in a product collection.
--
-- @version     1.00.0000, 15.01.1999
--
-- @author      Bernhard Walter   (BW)  990115
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductCollect_01$createVal');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductCollect_01$createVal
(
    IN  ai_id               INT,
    IN  ai_categoryOid_s    VARCHAR (18),
    IN  ai_value            VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_categoryOid   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_categoryOid       = c_NOOID;

-- body:
    -- insert a new tuple
    CALL IBSDEV1.p_stringToByte (ai_categoryOid_s, l_categoryOid);
    INSERT INTO IBSDEV1.m2_ProductCollectionValue_01 (id, categoryOid, value)
    VALUES	(ai_id, l_categoryOid, ai_value);
    COMMIT;
END;


--------------------------------------------------------------------------------
--
-- Deletes a ProductCollection Object.
--
-- @version     1.00.0000, 15.01.1999
--
-- @author      Bernhard Walter   (BW)  980115
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
-- 
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ProductCollect_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductCollect_01$delete(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
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
    -- definitions:
    -- define return constants
    DECLARE c_ALL_RIGHT     INT;
    -- define return values
    DECLARE l_retValue      INT;
    DECLARE l_Id             INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    -- define cursor:
    DECLARE Id_Cursor CURSOR WITH HOLD FOR 
    SELECT id 
    FROM IBSDEV1.m2_ProductCollectionQty_01	 
    WHERE collectionOid = l_oid;
  
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
    -- participants container

-- body:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN 
        -- open the cursor:
        OPEN Id_Cursor;
        -- get the first user:
        SET l_sqlcode = 0;
        FETCH FROM Id_Cursor INTO l_Id;
        SET l_sqlstatus = l_sqlcode;
        -- loop through all found users:
        WHILE l_sqlstatus <> 100 DO
            -- Da;@FETCH_STATUS einen der drei Werte -2, -1 oder 0
            -- besitzen kann, müssen alle drei Fälle geprüft werden.
            -- In diesem Fall wird eine Tabelle, wenn sie während der
            -- Ausführung der Prozedur gelöscht wurde, übersprungen.
            -- Ein erfolgreicher Abruf (0) veranlaßt die Ausführung
            -- von DBCC innerhalb der BEGIN..END-Schleife.
            IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
                -- delete entries in the data tables
                DELETE FROM IBSDEV1.m2_ProductCollectionQty_01
                WHERE id = l_Id;
                DELETE FROM IBSDEV1.m2_ProductCollectionValue_01
                WHERE id = l_Id;
            END IF;
            -- get next user:
            SET l_sqlcode = 0;
            FETCH FROM Id_Cursor INTO l_Id;
            SET l_sqlstatus = l_sqlcode;
        END WHILE;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_ProductCollect_01$delete

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
CALL IBSDEV1.p_dropProc ('p_ProductCollect_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ProductCollect_01$BOCopy(
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
  
    -- not implemented yet
    -- make an insert for all type specific tables:
    INSERT  INTO m2_ProductCollection_01
        (oid, cost, costCurrency, totalQuantity, validFrom,
        categoryOidX, categoryOidY, nrCodes )
    SELECT  ai_newOid,  cost, costCurrency, totalQuantity, 
        validFrom, categoryOidX, categoryOidY, nrCodes
    FROM IBSDEV1.   m2_ProductCollection_01
    WHERE   oid = ai_oid;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    COMMIT;
    IF l_rowcount >= 1 THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_ProductCollect_01$BOCopy