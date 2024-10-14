--------------------------------------------------------------------------------
-- All procedures regarding a log container. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020818
-------------------------------------------------------------------------------

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
CALL IBSDEV1.p_dropProc ('p_LogContainer_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_LogContainer_01$create
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
    DECLARE l_name          VARCHAR(63);
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
    SET l_name              = ai_name;  -- use input name value
-- KR HACK ... don't know why there is a hard coded name set.
--             but it's also done in SQL server and oracle code
    SET l_name = 'Protokoll';
-- ... HACK
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, l_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- check if there occurred an error:
    IF (l_retValue <> c_ALL_RIGHT)      -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object$performCreate';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_LogContainer_01$create',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_containerId_s', ai_containerId_s,
        'ai_op', ai_op, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_tVersionId', ai_tVersionId, 'ai_description', ai_description,
        'ai_containerKind', ai_containerKind, '', '',
        'ai_isLink', ai_isLink, 'ai_name', ai_name,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_LogContainer_01$create


--------------------------------------------------------------------------------
-- Clean the content of the ibs_protocol_01 table (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_LogContainer_01$clean');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_LogContainer_01$clean
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT
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

    SET l_sqlcode = 0;

    DELETE FROM IBSDEV1.ibs_protocol_01;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in DELETE - Statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception


    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_LogContainer_01$clean',
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
-- p_LogContainer_01$clean
