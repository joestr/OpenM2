--------------------------------------------------------------------------------
-- All stored procedures regarding the domain scheme table. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Klaus Reimüller (KR)  000220
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is creating the object.
-- @param   ai_op               Operation to be performed (used for rights 
--                              check).
-- @param   ai_tVersionId       Type of the new object.
-- @param   ai_name             Name of the object.
-- @param   ai_containerId_s    ID of the container where object shall be 
--                              created in.
-- @param   ai_containerKind    Kind of object/container relationship
-- @param   ai_isLink           Defines if the object is a link
-- @param   ai_linkedObjectId_s If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   ai_description      Description of the object.
--
-- @output parameters:
-- @param   ao_oid_s            OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DomainScheme_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DomainScheme_01$create(
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this operation
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

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
    SET c_ALREADY_EXISTS    = 21;
  
    -- initialize local variables:
    SET l_oid               = c_NOOID;
    SET l_retValue          = c_NOT_OK;
    SET ao_oid_s            = c_NOOID_s;
  
-- body:
    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$create', l_sqlcode, 'aa', 'l_retValue', l_retValue, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$create', l_sqlcode, 'bb', 'l_retValue', l_retValue, 'ao_oid_s', ao_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    IF l_retValue = c_ALL_RIGHT THEN        -- object created successfully?
        -- create object specific data:
        -- insert just a tuple with the oid into the table and use the
        -- default values:
        SET l_sqlcode = 0;
        INSERT INTO IBSDEV1.ibs_DomainScheme_01 
            (oid, hasCatalogManagement, hasDataInterchange, workspaceProc)
        VALUES  (l_oid, 0, 0, 'p_Workspace_01$createObjects');
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$create', l_sqlcode, 'dd', 'l_retValue', l_retValue, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
-- this is old version of insert where is id=0, now there is autoincrement
--        INSERT INTO IBSDEV1.ibs_DomainScheme_01 
--            (id, oid, hasCatalogManagement, hasDataInterchange, workspaceProc)
--        VALUES  (0, l_oid, 0, 0, 'p_Workspace_01$createObjects');
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    
        -- check if insertion was performed properly:
        IF l_rowcount <= 0 THEN             -- no row affected?
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;

    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DomainScheme_01$create

--------------------------------------------------------------------------------
-- Change the attributes of an existing object (incl. rights check). <BR>
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
-- @param   ai_hasCatalogManagement Does a domain with this scheme have a 
--                              catalog management?
-- @param   ai_hasDataInterchange Does a domain with this scheme have a 
--                              data interchange component?
-- @param   ai_workspaceProc    The name of the procedure for creating a
--                              user's workspace within a domain having this
--                              scheme?
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DomainScheme_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DomainScheme_01$change(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    -- type-specific input parameters:
    IN  ai_hasCatalogManagement SMALLINT,
    IN  ai_hasDataInterchange   SMALLINT,
    IN  ai_workspaceProc    VARCHAR (63))
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
    DECLARE SQLCODE         INT;
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
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$change', l_sqlcode, 'after performChange', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, 'ai_userId', ai_userId, 'ai_name', ai_name, 'ai_op', ai_op, 'ai_description', ai_description, 'ai_hasCatalogManagement', ai_hasCatalogManagement, 'ai_workspaceProc', ai_workspaceProc, 'ai_hasDataInterchange', ai_hasDataInterchange, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    IF (l_retValue = c_ALL_RIGHT)       -- operation performed properly?
    THEN
        -- update object type specific data:
        UPDATE  IBSDEV1.ibs_DomainScheme_01
        SET     hasCatalogManagement = ai_hasCatalogManagement,
                hasDataInterChange = ai_hasDataInterChange,
                workspaceProc = ai_workspaceProc
        WHERE   oid = l_oid;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        -- check if change was performed properly:
        IF (l_rowcount <= 0)            -- no row affected?
        THEN
            SET l_retValue = c_NOT_OK;      -- set return value
--CALL IBSDEV1.logError (300, 'p_DomainScheme_01$change', l_sqlcode, 'no row affected', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, 'ai_userId', ai_userId, 'ai_name', ai_name, 'ai_op', ai_op, 'ai_description', ai_description, 'ai_hasCatalogManagement', ai_hasCatalogManagement, 'ai_workspaceProc', ai_workspaceProc, 'ai_hasDataInterchange', ai_hasDataInterchange, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        END IF;
    END IF;
  
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DomainScheme_01$change


--------------------------------------------------------------------------------
-- Creates a new domain scheme. <BR>
-- This is a shortcut procedure which can be used for batch or installation
-- scripts.
--
-- @input parameters:
-- @param   ai_name             Name of the object.
-- @param   ai_description      Description of the object.
-- @param   ai_workspaceProc    Name of procedure to create workspace of an 
--                              user.
-- @param   ai_likeName         Comparison string for all existing domains.
--                              Each domain whose name is like the likeName
--                              is changed to the new domain scheme.
-- @param   ai_hasCatalogManagement Shall a new domain have a catalog mmt?
-- @param   ai_hasDataInterchange Shall a new domain have a DI component?
--
-- @output parameters:
-- @param   ao_oid_s            OID of the newly created object.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DomainScheme_01$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DomainScheme_01$new(
    -- input parameters:
    IN  ai_name             VARCHAR (63),
    IN  ai_description      VARCHAR (255),
    IN  ai_workspaceProc    VARCHAR (63),
    IN  ai_likeName         VARCHAR (63),
    IN  ai_hasCatalogManagement SMALLINT,
    IN  ai_hasDataInterchange   SMALLINT,
    -- output parameters:
    OUT ao_oid              CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this operation
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_TVDomainSchemeContainer INT;  -- tVersionId of domain scheme container
    DECLARE c_TVDomainScheme INT;           -- tVersionId of domain scheme

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_admin         INT;            -- the id of the system administrator
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- the actual oid
    DECLARE l_oid_s         VARCHAR (18);    -- the actual oid as string
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- the container oid
    DECLARE l_containerId_s VARCHAR (18);   -- the container oid as string
    DECLARE l_id            INT DEFAULT 0;  -- id of the domain scheme
    DECLARE l_validUntil    TIMESTAMP;      -- until when is the domain scheme valid?

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
    SET c_ALREADY_EXISTS    = 21;
    SET c_TVDomainSchemeContainer = 16843025; -- 0x01010111
    SET c_TVDomainScheme    = 16843041;     -- 0x01010121
  
    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;
    SET l_oid_s             = c_NOOID_s;
    SET l_containerId       = c_NOOID;
    SET l_containerId_s     = c_NOOID_s;
    SET ao_oid              = c_NOOID;

-- body:
    -- get the system administrator:
    SET l_sqlcode = 0;

    SELECT MIN (id) 
    INTO l_admin
    FROM IBSDEV1.ibs_User
    WHERE domainId = 0;

CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '1', 'l_retValue', l_retValue, 'ai_name', ai_name, 'l_admin', l_admin, 'ai_likeName', ai_likeName, 'ai_hasCatalogManagement', ai_hasCatalogManagement, '', '', 'ai_hasDataInterchange', ai_hasDataInterchange, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    IF (l_sqlcode = 0)
    THEN
        -- get the domain scheme container:
        SELECT  oid
        INTO    l_containerId
        FROM    IBSDEV1.ibs_Object
        WHERE   tVersionId = c_TVDomainSchemeContainer
            AND containerId =
                (
                    SELECT  oid 
                    FROM    IBSDEV1.ibs_Object
                    WHERE   containerId = c_NOOID
                );

--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '2 get the domain scheme container', 'l_admin',l_admin, '', '', '',0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        IF (l_sqlcode = 0)              -- the domain scheme container was
                                        -- found?
        THEN
            -- convert container oid to string representation:
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '3 convert container oid to string representation', 'l_admin',l_admin, '', '', '',0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            CALL IBSDEV1.p_byteToString (l_containerId, l_containerId_s);
            -- create the scheme:
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '4 create the scheme', 'l_admin', l_admin, 'l_containerId_s', l_containerId_s, '',0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            CALL IBSDEV1.p_DomainScheme_01$create (l_admin, 1, c_TVDomainScheme,
                ai_name, l_containerId_s, 1, 0, c_NOOID_s,
                ai_description, l_oid_s);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '5 scheme created', 'l_retValue', l_retValue, '', '', 'l_admin', l_admin, 'l_containerId_s', l_containerId_s, '',0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

            IF (l_retValue = c_ALL_RIGHT) -- domain scheme created?
            THEN
                -- convert string representation of oid to oid:
                CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
                -- get the data of the domain scheme:
                SET l_sqlcode = 0;
                SELECT  ds.id, o.validUntil 
                INTO    l_id, l_validUntil
                FROM    IBSDEV1.ibs_DomainScheme_01 ds, IBSDEV1.ibs_Object o
                WHERE   ds.oid = l_oid AND o.oid = ds.oid;
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '6 search for scheme', 'l_retValue', l_retValue, '', '', 'l_admin', l_admin, 'l_containerId_s', l_containerId_s, '',0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
        
                IF (l_sqlcode = 0)      -- the domain scheme was found?
                THEN
                    CALL IBSDEV1.p_DomainScheme_01$change(l_oid_s, l_admin, 1,
                        ai_name, l_validUntil, ai_description, 0,
                        ai_hasCatalogManagement, ai_hasDataInterchange,
                        ai_workspaceProc);
                    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '7 change scheme', 'l_retValue', l_retValue, 'l_oid_s', l_oid_s, 'l_admin', l_admin, 'l_containerId_s', l_containerId_s, '',0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

                    IF (l_retValue = c_ALL_RIGHT) -- domain scheme changed?
                    THEN
                        -- set already existing domains for actual scheme:
                        UPDATE  IBSDEV1.ibs_Domain_01
                        SET     scheme = l_id
                        WHERE   oid IN
                                (
                                    SELECT  d.oid 
                                    FROM    IBSDEV1.ibs_Domain_01 d,
                                            IBSDEV1.ibs_Object o
                                    WHERE   d.oid = o.oid
                                        AND o.name LIKE ai_likeName
                                );
--CALL IBSDEV1.logError (100, 'p_DomainScheme_01$new', l_sqlcode, '8 update scheme', 'l_retValue', l_retValue, 'ai_likeName', ai_likeName, 'l_admin', l_admin, 'l_containerId_s', l_containerId_s, '',0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
                        COMMIT;

                        -- set output parameter:
                        SET ao_oid = l_oid;
                    END IF; -- if domain scheme changed
                END IF; -- if the domain scheme was found
            END IF; -- if domain scheme created
        END IF; -- if the domain scheme container was found
    END IF;
END;
-- p_DomainScheme_01$new


--------------------------------------------------------------------------------
-- Get all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
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
-- @param   ao_creationDate     Date when the object was created.
-- @param   ao_creator          ID of person who created the object.
-- @param   ao_lastChanged      Date of the last change of the object.
-- @param   ao_changer          ID of person who did the last change to the
--                              object.
-- @param   ao_validUntil       Date until which the object is valid.
-- @param   ao_description      Description of the object.
-- @param   ao_showInNews       The showInNews flag.
-- @param   ao_checkedOut       Is the object checked out?
-- @param   ao_checkOutDate     Date when the object was checked out
-- @param   ao_checkOutUser     Oid of the user which checked out the object
-- @param   ai_checkOutUserOid  Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   ao_checkOutUserName name of the user which checked out the object,
--                              is only set if this user has the right to read
--                              the checkOut-User
-- @param   ao_hasCatalogManagement Does a domain with this scheme have a 
--                              catalog management?
-- @param   ao_hasDataInterchange Does a domain with this scheme have a 
--                              data interchange component?
-- @param   ao_workspaceProc    The name of the procedure for creating a
--                              user's workspace within a domain having this
--                              scheme?
-- @param   ao_numberOfDomains  The number of domains where this domain scheme
--                              is used.
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DomainScheme_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DomainScheme_01$retrieve(
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
    -- type-specific output parameters:
    OUT ao_hasCatalogManagement SMALLINT,
    OUT ao_hasDataInterchange SMALLINT,
    OUT ao_workspaceProc    VARCHAR (63),
    OUT ao_numberOfDomains   INT
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
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- the actual oid
    DECLARE l_id            INT;            -- the id of the scheme

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
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;

-- body:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, 
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- check if retrieve was performed properly:
    IF l_retValue = c_ALL_RIGHT THEN        -- everything o.k.?
        -- get the number of domains where this scheme is used:

        SELECT hasCatalogManagement, hasDataInterchange, workspaceProc, id
        INTO ao_hasCatalogManagement, ao_hasDataInterchange, ao_workspaceProc,  l_id
        FROM IBSDEV1.ibs_DomainScheme_01
        WHERE oid = l_oid;

        SELECT COUNT(*) 
        INTO l_rowcount
        FROM IBSDEV1.ibs_DomainScheme_01
        WHERE oid = l_oid;

        IF l_rowcount > 0 THEN 
            -- get the number of domains where this scheme is used:
            SELECT COUNT(*) 
            INTO ao_numberOfDomains
            FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_Domain_01 d
            WHERE o.oid = d.oid AND o.state = 2 AND d.scheme = l_id;
        ELSE                                -- no row affected
            SET l_retValue = c_NOT_OK;      -- set return value
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DomainScheme_01$retrieve


--------------------------------------------------------------------------------
-- Delete an object and all its values (incl. rights check). <BR>
-- This procedure also deletes all links showing to this object.
-- 
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be deleted.
-- @param   ai_userId           ID of the user who is deleting the object.
-- @param   ai_op               Operation to be performed (used for rights 
--                              check).
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DomainScheme_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DomainScheme_01$delete(
    -- common input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT
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
    DECLARE SQLCODE         INT;
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
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;

-- body:
    -- delete base object:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN         -- operation performed properly?
        -- delete object type specific data:
        -- (deletes all type specific tuples which are not within ibs_Object)
        DELETE FROM IBSDEV1.ibs_DomainScheme_01
        WHERE oid NOT IN    (
                                SELECT oid 
                                FROM IBSDEV1.ibs_Object
                            );
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        -- check if deletion was performed properly:
        IF l_rowcount <= 0 THEN             -- no row affected?
            SET l_retValue = c_NOT_OK;      -- set return value
        END IF;
    END IF;
  
    -- if operation performed properly
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DomainScheme_01$delete


--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
-- 
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be deleted.
-- @param   ai_userId           ID of the user who is deleting the object.
-- @param   ai_newOid           The oid of the copy.
--
-- @output parameters:
-- @return A value representing the state of the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DomainScheme_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DomainScheme_01$BOCopy(
    -- common input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_newOid           CHAR (8) FOR BIT DATA
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
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

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
    SET l_retValue = c_NOT_OK;

-- body:
    INSERT  INTO ibs_DomainScheme_01
        (oid, hasCatalogManagement, hasDataInterchange, workspaceProc)
    SELECT  ai_newOid, hasCatalogManagement, hasDataInterchange, workspaceProc
    FROM IBSDEV1.   ibs_DomainScheme_01
    WHERE   oid = ai_oid;
  
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    COMMIT;
  
    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN 
        -- at least one row affected?
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DomainScheme_01$BOCopy
