--------------------------------------------------------------------------------
 -- All procedures regarding a references. <BR>
 --
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
 --
 -- @author      MArcel Samek (MS)  020910
--------------------------------------------------------------------------------
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Referenz_01$create');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Referenz_01$create
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
    DECLARE c_REF_LINK      INT DEFAULT 1; -- reference kind: link
    DECLARE c_ST_ACTIVE     INT DEFAULT 2;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rights        INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_Init_Value      CHAR (8) FOR BIT DATA;
    DECLARE l_returnValue   INT;
    DECLARE l_linkedtVersionId INT;
    DECLARE l_targetVersionId INT;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_copyContainerId CHAR (8) FOR BIT DATA;
    DECLARE l_dummyObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_dummyObjectId_s VARCHAR (18);
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
    SET ao_oid_s            = c_NOOID_s;
    SET l_dummyObjectId     = c_NOOID;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ao_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)             -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion';
        GOTO exception1;            -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    SELECT  tVersionId
    INTO    l_linkedtVersionId
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_linkedObjectId
        AND state = c_ST_ACTIVE;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)             -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT l_linkedtVersionId';
        GOTO exception1;            -- call common exception handler
    END IF; -- if any exception

    SET l_sqlcode = 0;
    SELECT  tVersionId, containerId
    INTO    l_targetVersionId, l_copyContainerId
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_containerId
        AND state = c_ST_ACTIVE;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)             -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT l_targetVersionId, l_copyContainerId';
        GOTO exception1;            -- call common exception handler
    END IF; -- if any exception

    IF (l_targetVersionId = 16842929)
    THEN
        IF (l_linkedtVersionId = 16842929)
        THEN
            CALL IBSDEV1.p_Group_01$addGroup(ai_userId, l_containerId,
                l_linkedObjectId, c_NOOID);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;

        IF (l_linkedtVersionId = 16842913)
        THEN
            CALL IBSDEV1.p_Group_01$addUser(ai_userId, l_containerId,
                l_linkedObjectId, c_NOOID );
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;

        SET ao_oid_s = ai_linkedObjectId_s;
    ELSE
        IF (l_targetVersionId = 16842913)
        THEN

            SET l_sqlcode = 0;
            SELECT  linkedObjectId
            INTO    l_dummyObjectId
            FROM    IBSDEV1.ibs_Object
            WHERE   containerId = l_containerId
                AND tVersionId =  16842801;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in SELECT l_dummyObjectId';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            SET l_sqlcode = 0;
            DELETE  FROM IBSDEV1.ibs_Object
            WHERE   containerId = l_containerId
                AND tVersionId = 16842801
                AND state = c_ST_ACTIVE;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in DELETE';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            SET l_sqlcode = 0;
            DELETE  FROM IBSDEV1.ibs_Object
            WHERE   linkedObjectId = l_containerId
                AND containerId IN  
                    (
                        SELECT  o2.oid
                        FROM    IBSDEV1.ibs_Object AS o2
                        WHERE   o2.containerId = l_dummyObjectId
                            AND tVersionId = 16866817
                            AND state = c_ST_ACTIVE
                    )
                AND tVersionId = 16842801;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in DELETE 2';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op,
                ai_tVersionId, ai_name, ai_containerId_s, ai_containerKind,
                ai_isLink, ai_linkedObjectId_s, ai_description, ao_oid_s,
                l_dummyObjectId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;

            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.ibs_User
            SET     fullname =  
                    (
                        SELECT  name
                        FROM    IBSDEV1.ibs_Object
                        WHERE   oid = l_linkedObjectId
                            AND state = c_ST_ACTIVE
                    )
            WHERE   oid = l_containerId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in UPDATE';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            SET l_sqlcode = 0;
            SELECT  oid
            INTO    l_dummyObjectId
            FROM    IBSDEV1.ibs_Object
            WHERE   containerId = l_linkedObjectId
                AND tVersionId = 16866817
                AND state = c_ST_ACTIVE;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in SELECT l_dummyObjectId';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            CALL IBSDEV1.p_byteToString (l_dummyObjectId, l_dummyObjectId_s);
            CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op,
                ai_tVersionId, ai_name, l_dummyObjectId_s, ai_containerKind,
                ai_isLink, ai_containerId_s, ai_description, ao_oid_s,
                l_dummyObjectId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        ELSE
            IF l_targetVersionId = 16863745 THEN -- Membership
                CALL IBSDEV1.p_Group_01$addUser(ai_userId, l_linkedObjectId,
                    l_copyContainerId, c_NOOID);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
                SET ao_oid_s = ai_linkedObjectId_s;
            ELSE
                -- create a referenceobject
                CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op,
                    ai_tVersionId, ai_name, ai_containerId_s, ai_containerKind,
                    ai_isLink, ai_linkedObjectId_s, ai_description, ao_oid_s,
                    l_dummyObjectId);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;

                -- check if there occurred an error:
                IF (l_retValue = c_ALL_RIGHT)
                THEN
                    CALL IBSDEV1.p_stringToByte (ao_oid_s, l_oid);
                    CALL IBSDEV1.p_Reference$create(l_oid, NULL,
                        l_linkedObjectId, c_REF_LINK);
                END IF;
            END IF;
        END IF;
    END IF;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)         -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;        -- call common exception handler
    END IF; -- if any exception

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Referenz_01$create',
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
-- p_Referenz_01$create

--------------------------------------------------------------------------------
 -- Changes the attributes of an existing object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   @ai_oid_s               ID of the object to be changed.
 -- @param   @ai_userId              ID of the user who is changing the object.
 -- @param   @ai_op                  Operation to be performed (used for rights
 --                                  check).
 -- @param   @ai_name                Name of the object.
 -- @param   @ai_validUntil          Date until which the object is valid.
 -- @param   @ai_description         Description of the object.
 -- @param   @ai_showInNews          Should the currrent object 
 --                                  displayed in the news.
 -- @param   @ai_linkedObjectId_s    The oid of the linked object.
 --
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT               Action performed, values returned, everything ok.
 --  INSUFFICIENT_RIGHTS     User has no right to perform action.
 --  OBJECTNOTFOUND          The required object was not found within the
 --                          database.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Referenz_01$change');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Referenz_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18)
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
    DECLARE c_REF_LINK      INT DEFAULT 1; -- reference kind: link
    DECLARE c_ST_ACTIVE     INT DEFAULT 2;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- oid of the actual object
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;       -- oid of the object which shall
    DECLARE l_name          VARCHAR (63);    -- the name of the referenced object
    DECLARE l_description   VARCHAR (255);   -- the description of the
    DECLARE l_typeName      VARCHAR (63);    -- the name of the type of the
    DECLARE l_icon          VARCHAR (63);    -- the name of the icon of the
    DECLARE l_flags         INT;            -- the flags of the referenced object
    DECLARE l_rKey          INT;
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
    SET l_retValue          = c_OBJECTNOTFOUND;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get data of linked object into link:
    -- If the linked object is itself a link the link shall point to the
    -- original linked object.
    SET l_sqlcode = 0;
    SELECT  name, typeName, description, flags, icon, rKey
    INTO    l_name, l_typeName, l_description, l_flags, l_icon, l_rKey
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_linkedObjectId;

    SET l_sqlcode = 0;
    SELECT  COUNT(*)
    INTO    l_rowcount
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_linkedObjectId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in get data of linked object';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    IF (l_rowcount = 1)
    THEN
        -- perform the change of the object:
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op,
            l_name, ai_validUntil, l_description, ai_showInNews,
            l_oid);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in perform the change of the object';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- if the change operation was successful
        -- change the link specific attributes to
        IF (l_retValue = c_ALL_RIGHT)
        THEN

            -- perform the changes on ibs_Object:
            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.ibs_Object
            SET     name = l_name,
                    typeName = l_typeName,
                    description = l_description,
                    flags = l_flags,
                    icon = l_icon,
                    rKey = l_rKey,
                    linkedObjectId = l_linkedObjectId
            WHERE   oid = l_oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in perform the changes on ibs_Object';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- store the reference:
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_Reference$create(l_oid, NULL, l_linkedObjectId,
                c_REF_LINK);

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in store the reference';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        END IF;
    END IF;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Referenz_01$change',
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
-- p_Referenz_01$change
