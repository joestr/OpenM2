-------------------------------------------------------------------------------
-- All stored procedures regarding the Note_01 Object. <BR>
--
-- @version     $Id: Note_01Proc.sql,v 1.6 2003/10/31 16:30:37 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020819
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
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
--/
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Note_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Note_01$create
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
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_actrights     INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- CONVERSIONS (OBJECTIDSTRING) 
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting ai_containerId_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in converting ai_linkedObjectId_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- create base object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object$performCreate';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- create object type specific data:
    THEN         
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_Note_01 (oid, content)
        VALUES  (l_oid, ai_description);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in INSERT statement';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Note_01$create',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_containerId_s', ai_containerId_s,
        'ai_op', ai_op, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_tVersionId', ai_tVersionId, 'ai_description', ai_description,
        'ai_containerKind', ai_containerKind, '', '',
        'ai_isLink', ai_isLink, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Note_01$create


-------------------------------------------------------------------------------
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
-- @param    @content            content of note => Text or HTML Code
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Note_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Note_01$change
(
    -- common input parameters:
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
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform the change of the object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in perform the change of the object:';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Note_01$change',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_description', ai_description,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Note_01$change

-------------------------------------------------------------------------------
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
-- @param    @content            content of note => Text or HTML Code
--                                        TYPE  TEXT is not possible
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Note_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Note_01$retrieve
(
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
    OUT ao_checkOutUserName VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve the base object data:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object$performRetrieve';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Note_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Note_01$retrieve

-------------------------------------------------------------------------------
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
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Note_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Note_01$delete
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_rowcount      INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- delete base object:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN 
        -- delete object type specific data:
        -- (deletes all type specific tuples which are not within ibs_Object)
        SET l_sqlcode = 0;
        DELETE  FROM IBSDEV1.ibs_Note_01
        WHERE   oid NOT IN
                (
                    SELECT oid 
                    FROM IBSDEV1.ibs_Object
                );

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in DELETE statement';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception

        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    
        -- check if deletion was performed properly:
        IF (l_rowcount <= 0)
        THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Note_01$delete',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Note_01$delete

-------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
-- 
-- @input parameters:
-- @param   @oid                ID of the object to be copy.
-- @param   @userId             ID of the user who is copying the object.
-- @param   @newOid             ID of the copy of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Note_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Note_01$BOCopy
(
    -- common input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_newOid           CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rowcount      INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- make an insert for all type specific tables:
    SET l_sqlcode = 0;
    INSERT  INTO IBSDEV1.ibs_Note_01
            (oid, content)
    SELECT  ai_newOid, content
    FROM    IBSDEV1.ibs_Note_01
    WHERE   oid = ai_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in make an insert for all type specific tables:';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    IF (l_rowcount >= 1)                -- if at least one row inserted
    THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF; -- if at least one row inserted

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Note_01$BOCopy',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Note_01$BOCopy

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Note_01$getExtended');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Note_01$getExtended 
( 
    -- common input parameters:
    IN  ai_oid_s            CHAR(18), 
    -- common output parameters:
    OUT ao_content          CLOB
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    SET l_sqlcode = 0;
    SELECT  content
    INTO    ao_content
    FROM    IBSDEV1.ibs_Note_01
    WHERE   oid = l_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Note_01$getExtended',
        l_sqlcode, l_ePos,
        '', 0, 'ai_oid_s', ai_oid_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');

    COMMIT;

    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_Note_01$getExtended 

