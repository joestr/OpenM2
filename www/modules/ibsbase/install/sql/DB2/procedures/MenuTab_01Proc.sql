--------------------------------------------------------------------------------
-- All stored procedures regarding the MenuTab_01 Object. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020819
-------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new MenuTab_01 Object (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_MenuTab_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MenuTab_01$create
(
    -- common input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           INT,
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
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_dummy         CHAR (8) FOR BIT DATA;
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

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN 
        -- create object type specific data:
        SET l_sqlcode = 0;
        INSERT  INTO IBSDEV1.ibs_MenuTab_01 (oid, objectOid, description,
                isPrivate, priorityKey, domainId, classFront, classBack,
                fileName)
        VALUES  (l_oid, c_NOOID, ' ',
                0, 0, 0, 'groupFront.gif','groupBack.gif', 'welcome.htm');

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in create object type specific data';
            GOTO exception1;            -- call common exception handler
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
    CALL IBSDEV1.logError (500, 'p_MenuTab_01$create',
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
-- p_MenuTab_01$create


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
CALL IBSDEV1.p_dropProc ('p_MenuTab_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MenuTab_01$change
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
    IN  ai_objectoid_s      VARCHAR (18),
    IN  ai_filename         VARCHAR (255),
    IN  ai_tabpos           INT,
    IN  ai_front            VARCHAR (255),
    IN  ai_back             VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_EMPTYPOSNOPATH VARCHAR (254) DEFAULT '0000';
                                        -- default value for empty pos no path

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this procedure
    DECLARE l_posNoPath     VARCHAR (254); -- the pos no path of the object
    DECLARE l_isPrivate     INT DEFAULT 0; -- a flag which is 1 if object is
                                        -- a workspace
    DECLARE l_domainId      INT DEFAULT 0; -- id of the domain where the object
                                        -- exists
    DECLARE l_showInMenu    SMALLINT DEFAULT 0; -- the show in menu flag of the
                                        -- object
    DECLARE l_count         INT DEFAULT 0; -- counter
    DECLARE l_oldObjectId   CHAR (8) FOR BIT DATA; -- assigned oid before change
    DECLARE l_objectoid     CHAR (8) FOR BIT DATA; -- oid of new assigned object
    DECLARE l_objectname    VARCHAR (63); -- name of new assigned object
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- to save oid of created
                                        --  menutab
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
    SET l_posNoPath         = c_EMPTYPOSNOPATH;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- convert oidString to oid
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_stringToByte (ai_objectoid_s, l_objectoid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- perform the change of the object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in perform the change of the object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN 
        SET l_sqlcode = 0;
        SELECT  name, posNoPath
        INTO    l_objectname, l_posNoPath
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_objectoid;
    
        -- get the domain id:
        SELECT  d.id 
        INTO    l_domainId
        FROM    IBSDEV1.ibs_Domain_01 d, IBSDEV1.ibs_Object o
        WHERE   l_posNoPath LIKE o.posNoPath || '%'
            AND d.oid = o.oid;

        -- check if given object is a workspaceContainer
        SELECT  COUNT(*) 
        INTO    l_count
        FROM    IBSDEV1.ibs_domain_01
        WHERE   workspacesOid = l_objectoid;

        IF l_count > 0 THEN 
            SET l_isPrivate = 1;
        END IF;

        -- get oid of object which was assigned to menutab before change
        SELECT  objectoid
        INTO    l_oldObjectId
        FROM    IBSDEV1.ibs_MenuTab_01
        WHERE   oid = l_oid;

        -- don't show the object menu again:
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 1
        WHERE   oid = l_oldObjectId;

        -- ensure that ai_front and ai_back contain '.':
        SET ai_front = ai_front || '.';
        SET ai_back = ai_back || '.';

        -- set new data in menutab:
        UPDATE  IBSDEV1.ibs_MenuTab_01
        SET     objectoid = l_objectoid,
                description = l_objectname,
                prioritykey = ai_tabpos,
                filename = ai_filename,
                classfront = SUBSTR(ai_front, 1, POSSTR(ai_front, '.')-1),
                classback = SUBSTR(ai_back, 1, POSSTR(ai_back, '.')-1),
                isprivate = l_isPrivate,
                domainid = l_domainId
        WHERE   oid = l_oid;
    
        -- don't show the object in the menu because it's a menutab:
        UPDATE  IBSDEV1.ibs_Object
        SET     showInMenu = 0
        WHERE   oid = l_objectoid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in UPDATE';
            GOTO exception1;            -- call common exception handler
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
    CALL IBSDEV1.logError (500, 'p_MenuTab_01$change',
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
-- p_MenuTab_01$change

--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
--
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MenuTab_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MenuTab_01$retrieve
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
    OUT ao_objectoid        CHAR (8) FOR BIT DATA,
    OUT ao_objectname       VARCHAR (63),
    OUT ao_tabpos           INT,
    OUT ao_front            VARCHAR (255),
    OUT ao_back             VARCHAR (255),
    OUT ao_filename         VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local valriables
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- to save oid of created menutab
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
    SET l_oid               = c_NOOID;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
        ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully
    THEN 
        SET l_sqlcode = 0;

        SELECT  objectoid, prioritykey, classfront, classback, filename
        INTO    ao_objectoid, ao_tabpos, ao_front, ao_back, ao_filename
        FROM    IBSDEV1.ibs_MenuTab_01
        WHERE   oid = l_oid;

        SELECT  name
        INTO    ao_objectname
        FROM    IBSDEV1.ibs_object
        WHERE   oid = ao_objectoid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- object created successfully
  
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_MenuTab_01$retrieve',
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
-- p_MenuTab_01$retrieve;

--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
--
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MenuTab_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MenuTab_01$delete
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
                                        -- default value for no defined oid

    -- local valriables
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- to save oid of created menutab
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

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- show the object in the menu because it's no longer a menutab
    UPDATE  IBSDEV1.ibs_Object
    SET     showInMenu = 1
    WHERE   oid = 
            (
                SELECT  objectoid 
                FROM    IBSDEV1.ibs_MenuTab_01
                WHERE   oid = l_oid
            );
  
    -- important for creating the right application path in m2
    UPDATE  IBSDEV1.ibs_MenuTab_01
    SET     objectoid = c_NOOID
    WHERE   oid = l_oid;
  
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

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
    CALL IBSDEV1.logError (500, 'p_MenuTab_01$delete',
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
-- p_MenuTab_01$delete
