--------------------------------------------------------------------------------
-- All stored procedures regarding the object table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:47 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020816
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
CALL IBSDEV1.p_dropProc ('p_AC_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_AC_01$create
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
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET l_dummy             = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_dummy);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object$performCreate';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_AC_01$create',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'containerId_s', ai_containerId_s,
        'ai_tVersionId', ai_tVersionId,
        'ai_linkedObjectId_s', ai_linkedObjectId_s,
        'ai_containerKind', ai_containerKind,
        'ai_description', ai_description,
        'ai_isLink', ai_isLink, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END; 
--p_AC_01$create


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
-- @param   @showInNews         the showInNews flag    
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_AC_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_AC_01$change
(
    -- input parameters:
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
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
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
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- perform the change of the object:
    SET l_sqlcode = 0;
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_dummy);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in p_Object$performChange';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_AC_01$change',
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
-- p_AC_01$change

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
-- @param   @containerName      Name of the Container
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
-- @param   @showInNews         the showInNews flag   
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
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_AC_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_AC_01$retrieve
(
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
    OUT ao_masterId_s       VARCHAR (18),
    OUT ao_fileName         VARCHAR (255),
    OUT ao_url              VARCHAR (255),
    OUT ao_path             VARCHAR (255),
    OUT ao_attachmentType   INT
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
    DECLARE l_rowcount      INT;
    DECLARE l_masterId      CHAR (8) FOR BIT DATA;
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
    SET l_masterId          = c_NOOID;

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

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- all right with the basisobject
    THEN 
        -- check if the container has a element or not ?
        SET l_sqlcode = 0;
        SELECT  COUNT(*)
        INTO    l_rowcount
        FROM    IBSDEV1.ibs_Object
        WHERE   containerId = l_oid 
            AND tVersionId = 16842833;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in check if the container has a element';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- typeName = attachment
        IF (l_rowcount > 0)             -- AttachmentContainer not empty
        THEN 
            SET l_sqlcode = 0;
            SELECT  a.oid               -- search the master attachement
            INTO    l_masterId
            FROM    IBSDEV1.ibs_Attachment_01 a, IBSDEV1.ibs_Object o
            WHERE   o.containerId = l_oid
                AND a.isMaster = 1
                AND o.oid = a.oid
                AND o.state = 2;       -- state = ST_ACTIVE

            SELECT  COUNT(*) 
            INTO    l_rowcount
            FROM    IBSDEV1.ibs_Attachment_01 a, IBSDEV1.ibs_Object o
            WHERE   o.containerId = l_oid
                AND a.isMaster = 1
                AND o.oid = a.oid
                AND o.state = 2;        -- state = ST_ACTIVE
      
            -- check if there occurred an error:
            IF (l_sqlcode <> 0)             -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in search the master attachement';
                GOTO exception1;            -- call common exception handler
            END IF; -- if any exception

            IF (l_rowcount = 1)         -- master was found?
            THEN 
                -- get master data:
                SET l_sqlcode = 0;
                SELECT  filename, url, path, attachmentType
                INTO    ao_fileName, ao_url, ao_path, ao_attachmentType
                FROM    IBSDEV1.ibs_Attachment_01
                WHERE   oid = l_masterId;

                -- check if there occurred an error:
                IF (l_sqlcode <> 0)     -- any exception?
                THEN
                    -- create error entry:
                    SET l_ePos = 'Error in get master data:';
                    GOTO exception1;    -- call common exception handler
                END IF; -- if any exception
            ELSE                        -- else master was found? 
                IF ((l_rowcount = 0) OR (l_rowcount > 1)) 
                                        -- no or more than one master
                THEN 
                    -- get new master (attachment with oldest oid):
                    SET l_sqlcode = 0;
                    SELECT  MIN (a.oid) 
                    INTO    l_masterId
                    FROM    IBSDEV1.ibs_Attachment_01 a INNER JOIN
                            IBSDEV1.ibs_Object o ON a.oid = o.oid
                    WHERE   o.containerId = l_oid
                        AND o.state = 2; -- state is ST_ACTIVE

                    -- check if there occurred an error:
                    IF (l_sqlcode <> 0) -- any exception?
                    THEN
                        -- create error entry:
                        SET l_ePos = 'Error in get new master:';
                        GOTO exception1; -- call common exception handler
                    END IF; -- if any exception

                    -- get new master data:
                    SET l_sqlcode = 0;
                    SELECT  filename, url, path, attachmentType
                    INTO    ao_fileName, ao_url, ao_path, ao_attachmentType
                    FROM    IBSDEV1.ibs_Attachment_01
                    WHERE   oid = l_masterId;

                    -- check if there occurred an error:
                    IF (l_sqlcode <> 0) -- any exception?
                    THEN
                        -- create error entry:
                        SET l_ePos = 'Error in get new master data:';
                        GOTO exception1; -- call common exception handler
                    END IF; -- if any exception

                ELSE                    -- else or more than one master
                    SET ao_fileName = 'kein Masterfile definiert';
                    SET ao_url = 'keine MasterUrl definiert';
                END IF; -- else or more than one master
            END IF; -- else master was found? 
        END IF; -- AttachmentContainer not empty

        CALL IBSDEV1.p_byteToString (l_masterId, ao_masterId_s);
    END IF; -- all right with the basisobject
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)     -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;    -- call common exception handler
    END IF; -- if any exception
    -- finish transaction:

    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_AC_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ao_name', ao_name,
        'ao_showInNews', ao_showInNews, 'ao_description', ao_description,
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
-- p_AC_01$retrieve

--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
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
CALL IBSDEV1.p_dropProc ('p_AC_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_AC_01$delete
(
    -- input parameters:
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
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT DEFAULT 0;
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

    -- get container id of object
    SET l_sqlcode = 0;
    SELECT  containerId
    INTO    l_containerId
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid;

    -- check if the object exists:
    IF (l_sqlcode = 0)
    THEN 
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(l_oid, l_containerId,
            ai_userId, ai_op, l_rights);

        -- check if the user has the necessary rights
        IF (l_rights = ai_op)           -- the user has the rights?
        THEN            
            SET l_sqlcode = 0;
/*
             -- delete subsequent objects
             DELETE  ibs_Object
             WHERE   posNoPath LIKE @posNoPath + '%'
                 AND oid <> @oid
 
             -- delete references to the object
             DELETE  ibs_Object 
             WHERE   linkedObjectId = @oid
 
 
             -- delete object itself
             DELETE  ibs_Object 
             WHERE   oid = @oid
 */
        ELSE                            -- else the user has the rights?
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
            SET l_sqlcode = 0;
        END IF; -- else the user has the rights?
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
        SET l_sqlcode = 0;
    END IF;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)     -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;    -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_AC_01$delete',
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
--p_AC_01$delete

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
CALL IBSDEV1.p_dropProc ('p_AC_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_AC_01$BOCopy
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters:
    OUT ao_newOid           CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_RIGHT_DELETE  INT DEFAULT 16;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_name          VARCHAR (255);
    DECLARE l_containerKind INT;
    DECLARE l_isLink        SMALLINT;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_description   VARCHAR (255);
    DECLARE l_tVersionId    INT;
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

    SET l_sqlcode = 0;
    SELECT  tVersionId, name, containerId, containerKind, isLink,
            linkedObjectId, description
    INTO    l_tVersionId, l_name, l_containerId, l_containerKind, l_isLink,
            l_linkedObjectId, l_description
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = ai_oid;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    INSERT  INTO IBSDEV1.ibs_Object
            (tVersionId, name, containerId, containerKind, 
            isLink, linkedObjectId, owner, creator, changer, 
            validUntil, description)
    VALUES  (l_tVersionId, l_name, l_containerId, l_containerKind, 
            l_isLink, l_linkedObjectId, ai_userId, ai_userId, ai_userId, 
            CURRENT TIMESTAMP + 3 MONTH, l_description);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in INSERT statement';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception
  
    SET l_sqlcode = 0;
    SELECT  oid
    INTO    ao_newOid
    FROM    IBSDEV1.ibs_Object
    WHERE   id =  
            (
                SELECT  MAX (id) 
                FROM    IBSDEV1.ibs_Object
                WHERE   tVersionId = l_tVersionId
                    AND name = l_name
                    AND containerId = l_containerId
                    AND containerKind = l_containerKind
                    AND isLink = l_isLink
                    AND linkedObjectId = l_linkedObjectId
             );

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT ao_newOid';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_AC_01$BOCopy',
        l_sqlcode, l_ePos,
        '', 0, '', '',
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
--p_AC_01$BOCopy