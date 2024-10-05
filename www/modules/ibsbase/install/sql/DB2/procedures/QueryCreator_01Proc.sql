--------------------------------------------------------------------------------
-- All stored procedures regarding to the QueryCreator_01 for dynamic
-- Search - Queries. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
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
--
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_QueryCreator_01$create');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_QueryCreator_01$create
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
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
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
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- create object type specific data:
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_QueryCreator_01(oid, selectString,
                fromString, whereString, queryType, groupByString,
                orderByString, columnHeaders, queryAttrTypesForHeaders,
                queryAttrForHeaders, searchFieldTokens, queryAttrForFields,
                queryAttrTypesForFields, resultCounter, enableDebug)
        VALUES (l_oid, ' ', ' ', ' ', 1, NULL, NULL, ' ', ' ', ' ', ' ', ' ',
                ' ', -1, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in create object type specific data';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

    END IF;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)             -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;            -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_QueryCreator_01$create',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_containerId_s', ai_containerId_s,
        'ai_tVersionId', ai_tVersionId,
        'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_containerKind', ai_containerKind, 'ai_description', ai_description,
        'ai_isLink', ai_isLink, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_QueryCreator_01$create


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
--
--
-- @param   @ai_queryString        desc
-- @param   @ai_columnHeaders      desc
-- @param   @ai_queryAttributes    desc
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_QueryCreator_01$change');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_QueryCreator_01$change
(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    -- typespecific input parameters
    IN  ai_queryType        INT,
    IN  ai_groupByString    VARCHAR (255),
    IN  ai_orderByString    VARCHAR (255),
    IN  ai_resultCounter    INT,
    IN  ai_enableDebug      SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
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
    SET l_oid               = c_NOOID;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description,  ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN

        SET l_sqlcode = 0;

        UPDATE IBSDEV1.ibs_QueryCreator_01
        SET queryType = ai_queryType,
            groupByString = ai_groupByString,
            orderByString = ai_orderByString,
            resultCounter = ai_resultCounter,
            enableDebug = ai_enableDebug
        WHERE oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in Error in UPDATE';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception

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
    CALL IBSDEV1.logError (500, 'p_QueryCreator_01$change',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ai_name', ai_name,
        'ai_showInNews', ai_showInNews, 'ai_description', ai_description,
        'ai_queryType', ai_queryType, 'ai_groupByString', ai_groupByString,
        'ai_resultCounter',ai_resultCounter,
        'ai_orderByString', ai_orderByString,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_QueryCreator_01$change


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @ai_oid_s              ID of the object to be retrieved.
-- @param   @ai_userId             Id of the user who is getting the data.
-- @param   @ai_op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   @ao_state              The object's state.
-- @param   @ao_tVersionId         ID of the object's type (correct version).
-- @param   @ao_typeName           Name of the object's type.
-- @param   @ao_name               Name of the object itself.
-- @param   @ao_containerId        ID of the object's container.
-- @param   @ao_containerName      Name of the object's container.
-- @param   @ao_containerKind      Kind of object/container relationship.
-- @param   @ao_isLink             Is the object a link?
-- @param   @ao_linkedObjectId     Link if isLink is true.
-- @param   @ao_owner              ID of the owner of the object.
-- @param   @ao_creationDate       Date when the object was created.
-- @param   @ao_creator            ID of person who created the object.
-- @param   @ao_lastChanged        Date of the last change of the object.
-- @param   @ao_changer            ID of person who did the last change to the
--                                 object.
-- @param   @ao_validUntil         Date until which the object is valid.
-- @param   @ao_description        Description of the object.
-- @param   @ao_showInNews         flag if object should be 
--                                 shown in newscontainer
-- @param   @ao_checkedOut         Is the object checked out?
-- @param   @ao_checkOutDate       Date when the object was checked out
-- @param   @ao_checkOutUser       id of the user which checked out the object
-- @param   @ao_checkOutUserOid    Oid of the user which checked out the object
--                                 is only set if this user has the 
--                                 right to READ
--                                 the checkOut user
-- @param   @ao_checkOutUserName   name of the user which checked 
--                                 out the object,
--                                 is only set if this user has 
--                                 the right to view
--                                 the checkOut-User
--
-- @param   @ao_queryType          integer for querytype
-- @param   @ao_groupByString      string for groupby clause
-- @param   @ao_orderByString      string for orderby clause
-- @param   @ao_resultCounter      integer number of results to be shown
-- @param   @ao_enableDebug        flag if debug should be shown
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_QueryCreator_01$retrieve');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_QueryCreator_01$retrieve
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
    OUT ao_checkOutUserName VARCHAR (63),
    -- type-specific output attributes:
    OUT ao_queryType        INT,
    OUT ao_groupByString    VARCHAR (255),
    OUT ao_orderByString    VARCHAR (255),
    OUT ao_resultCounter    INT,
    OUT ao_enableDebug      SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
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
    SET l_oid               = c_NOOID;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        SET l_sqlcode = 0;
        SELECT  queryType, groupByString, orderByString, resultCounter,
                enableDebug
        INTO    ao_queryType, ao_groupByString, ao_orderByString,
                ao_resultCounter, ao_enableDebug
        FROM    IBSDEV1.ibs_QueryCreator_01
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)                 -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT';
            GOTO exception1;                -- call common exception handler
        END IF; -- if any exception
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
    CALL IBSDEV1.logError (500, 'p_QueryCreator_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
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
-- p_QueryCreator_01$retrieve

--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
--
-- @input parameters:
-- @param   @ai_oid                ID of the object to be copy.
-- @param   @ai_userId             ID of the user who is copying the object.
-- @param   @ai_newOid             ID of the copy of the object.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_QueryCreator_01$BOCopy');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_QueryCreator_01$BOCopy
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
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- define return values:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
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

    SET l_sqlcode = 0;
    -- copy all values of querycreator
    INSERT  INTO IBSDEV1.ibs_QueryCreator_01(oid, selectString, fromString,
            whereString, queryType, groupByString, orderByString,
            columnHeaders, queryAttrTypesForHeaders, queryAttrForHeaders,
            searchFieldTokens, queryAttrForFields, queryAttrTypesForFields,
            resultCounter,   enableDebug)
    SELECT  ai_newOid, selectString, fromString, whereString, queryType,
            groupByString, orderByString, columnHeaders,
            queryAttrTypesForHeaders, queryAttrForHeaders, searchFieldTokens,
            queryAttrForFields, queryAttrTypesForFields, resultCounter,
            enableDebug
    FROM    IBSDEV1.ibs_QueryCreator_01
    WHERE   oid = ai_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN
        -- at least one row affected?
        SET l_retValue = c_ALL_RIGHT;
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_QueryCreator_01$BOCopy',
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
-- p_QueryCreator_01$BOCopy