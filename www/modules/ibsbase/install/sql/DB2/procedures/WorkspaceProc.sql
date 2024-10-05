------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_Workspace table. <BR>
--
-- @version     $Revision: 1.7 $, $Date: 2003/10/21 22:14:52 $
--              $Author: klaus $
--
-- author      Marcel Samek (MS)  020910
------------------------------------------------------------------------------
--/

------------------------------------------------------------------------------
-- Creates a new workspace (incl. rights check). <BR>
-- The rights are checked against the root of the system.
--
-- input parameters:
-- param   userId             ID of the user who is creating the workspace.
-- param   op                 Operation to be performed (possibly in the
--                              future used for rights check).
-- param   wUserId            ID of the user for whom the workspace is
--                              created.
--
-- output parameters:
-- param   oid_s              OID of the newly created object.
-- returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The workspace was not created due to an unknown
--                         error.
--/

-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_Workspace_01$create');
CALL IBSDEV1.p_dropProc ('p_Workspace_01$create');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Workspace_01$create
(
    -- input parameters:
    IN ai_userId            INT,
    IN ai_op                INT,
    IN ai_wUserId           INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_languageId    INT DEFAULT 0;  -- the current language
    DECLARE c_domainMult    INT DEFAULT 16777216;
                                            -- multiplier to compute offset of
                                            -- domain id within user id

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT DEFAULT 0;  -- return value of function
    DECLARE l_ePos          VARCHAR (2000) DEFAULT '';
                                            -- error position description
    DECLARE l_rights        INT DEFAULT 0;
    DECLARE l_domainOid     CHAR (8) FOR BIT DATA;
    DECLARE l_domainOid_s   VARCHAR (18);
    DECLARE l_domainId      INT DEFAULT 0;
    DECLARE l_workspace     CHAR (8) FOR BIT DATA;
    DECLARE l_workspace_s   VARCHAR (18);
    DECLARE l_workspacesOid CHAR (8) FOR BIT DATA;
    DECLARE l_workspacesOid_s VARCHAR (18);
    DECLARE l_workBox       CHAR (8) FOR BIT DATA;
    DECLARE l_workBox_s     VARCHAR (18);
    DECLARE l_outBox        CHAR (8) FOR BIT DATA;
    DECLARE l_outBox_s      VARCHAR (18);
    DECLARE l_inBox         CHAR (8) FOR BIT DATA;
    DECLARE l_inBox_s       VARCHAR (18);
    DECLARE l_news          CHAR (8) FOR BIT DATA;
    DECLARE l_news_s        VARCHAR (18);
    DECLARE l_hotList       CHAR (8) FOR BIT DATA;
    DECLARE l_hotList_s     VARCHAR (18);
    DECLARE l_profile       CHAR (8) FOR BIT DATA;
    DECLARE l_profile_s     VARCHAR (18);
    DECLARE l_shoppingCart  CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCart_s VARCHAR (18);
    DECLARE l_orders        CHAR (8) FOR BIT DATA;
    DECLARE l_orders_s      VARCHAR (18);
    DECLARE l_workspaceProc VARCHAR (63) DEFAULT '';
    DECLARE l_execStr       VARCHAR (255) DEFAULT '';
    DECLARE l_publicWsp     CHAR (8) FOR BIT DATA;
    DECLARE l_admin         INT DEFAULT 0;
    DECLARE l_userAdminGroup INT DEFAULT 0;
    DECLARE l_name          VARCHAR (63) DEFAULT '';
                                            -- name of the current object
    DECLARE l_desc          VARCHAR (255) DEFAULT '';
                                            -- description of the current object
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_domainOid         = c_NOOID;
    SET l_domainOid_s       = c_NOOID_s;
    SET l_workspace         = c_NOOID;
    SET l_workspace_s       = c_NOOID_s;
    SET l_workspacesOid     = c_NOOID;
    SET l_workspacesOid_s   = c_NOOID_s;
    SET l_workBox           = c_NOOID;
    SET l_workBox_s         = c_NOOID_s;
    SET l_outBox            = c_NOOID;
    SET l_outBox_s          = c_NOOID_s;
    SET l_inBox             = c_NOOID;
    SET l_inBox_s           = c_NOOID_s;
    SET l_news              = c_NOOID;
    SET l_news_s            = c_NOOID_s;
    SET l_hotList           = c_NOOID;
    SET l_hotList_s         = c_NOOID_s;
    SET l_profile           = c_NOOID;
    SET l_profile_s         = c_NOOID_s;
    SET l_shoppingCart      = c_NOOID;
    SET l_shoppingCart_s    = c_NOOID_s;
    SET l_orders            = c_NOOID;
    SET l_orders_s          = c_NOOID_s;
    SET l_publicWsp         = c_NOOID;

-- body:
    -- set domain id:
    SET l_domainId = ai_wUserId / c_domainMult;

    -- get domain info:
    SELECT  oid, workspacesOid, adminId, userAdminGroupId,
            workspaceProc, publicOid
    INTO    l_domainOid, l_workspacesOid, l_admin, l_userAdminGroup,
            l_workspaceProc, l_publicWsp
    FROM    IBSDEV1.ibs_Domain_01
    WHERE   id = l_domainId;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'get domain info';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- convert domain oid to string value:
    CALL IBSDEV1.p_byteToString (l_domainOid, l_domainOid_s);

    -- convert workspaces oid to string value:
    CALL IBSDEV1.p_byteToString (l_workspacesOid, l_workspacesOid_s);

    -- get rights to be set for this user on her/his own workspace:
    SELECT  SUM (id)
    INTO    l_rights
    FROM    IBSDEV1.ibs_Operation;

    IF (l_sqlcode = 100)
    THEN
        SET l_rights = 0;
        SET l_sqlcode = 0;
    END IF;

    -- check if there exists already a workspace for this user:
    IF EXISTS
    (
        SELECT  userId
        FROM    IBSDEV1.ibs_Workspace
        WHERE   userId = ai_wUserId
    )                                   -- workspace already exists?
    THEN
        SET  l_retValue = c_ALREADY_EXISTS;
    -- end if workspace already exists
    ELSE                                -- workspace does not exist yet
        -- create workspace of the user:
        CALL IBSDEV1.p_ObjectDesc_01$get
            (c_languageId, 'OD_wspPrivate', l_name, l_desc);
        CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op,
            16855553, -- 01013201
            l_name, l_workspacesOid_s, 1, 0, c_NOOID_s, l_desc,
            l_workspace_s, l_workspace);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create user workspace';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- delete all actual defined rights:
        CALL IBSDEV1.p_Rights$deleteObjectRights (l_workspace);

        -- set rights for user and user administrator group:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'read', 'viewElems');

        CALL IBSDEV1.p_Rights$setRights
            (l_workspace, l_userAdminGroup, l_rights, 0);
        CALL IBSDEV1.p_Rights$setRights
            (l_workspace, ai_wUserId, l_rights, 0);

        SET l_execStr = 'CALL IBSDEV1.' || l_workspaceProc || ' (' ||
             CHAR (ai_userId) || ', ' ||
             '0, ' ||                   -- no rights necessary
             CHAR (ai_wUserId) || ', ' ||
             CHAR (l_domainId) || ', ' ||
             '''' || l_workspace_s || ''')';

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'creating execution string';
            GOTO exception1;            -- call common exception handler
        END IF;

        EXECUTE IMMEDIATE l_execStr;
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'create workspace objects';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- store the objects within the workspace:
        UPDATE  IBSDEV1.ibs_Workspace
        SET     workspace = l_workspace,
                publicWsp = l_publicWsp
        WHERE   userId = ai_wUserId;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
        THEN
            -- create error entry:
            SET l_ePos = 'UPDATE ibs_Workspace';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- check if the workspace was created:
        IF (l_rowcount <= 0)            -- workspace was not created?
        THEN
            -- set the return value with the error code:
            SET l_retValue = c_NOT_OK;
        -- end if workspace was not created
        ELSE                            -- workspace was created
            -- ensure that the already taken changes cannot be undone:
            COMMIT;

            -- get objects of workspace:
            SELECT  workBox, outBox, inBox, news, hotList, profile,
                    shoppingCart, orders
            INTO    l_workBox, l_outBox, l_inBox, l_news, l_hotList, l_profile,
                    l_shoppingCart, l_orders
            FROM    IBSDEV1.ibs_Workspace
            WHERE   userId = ai_wUserId;

            IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
            THEN
                -- create error entry:
                SET l_ePos = 'get objects of workspace';
                GOTO exception1;        -- call common exception handler
            END IF;

            IF (l_news <> c_NOOID)      -- the news container exists?
            THEN
                -- set rights on news container
                -- for administrator group and the user himself:
                SELECT  SUM (id)
                INTO    l_rights
                FROM    IBSDEV1.ibs_Operation
                WHERE   name IN ('view', 'read', 'viewElems');
                CALL IBSDEV1.p_Rights$setRights
                    (l_news, l_userAdminGroup, l_rights, 0);
                CALL IBSDEV1.p_Rights$setRights
                    (l_news, ai_wUserId, l_rights, 0);
            END IF; -- if the news container exists

            -- set rights on work box, out box, in box, hotList,
            -- shopping cart, and orders for administrator group:
            SELECT  SUM (id)
            INTO    l_rights
            FROM    IBSDEV1.ibs_Operation
            WHERE   name IN
                    (
                        'new', 'view', 'read', 'change', 'delete', 'viewRights',
                        'setRights', 'createLink', 'distribute', 'addElem',
                        'delElem', 'viewElems','viewProtocol'
                    );

            IF (l_workBox <> c_NOOID)   -- workBox exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_workBox, l_userAdminGroup, l_rights, 0);
            END IF; -- if workBox exists
            IF (l_outBox <> c_NOOID)    -- outBox exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_outBox, l_userAdminGroup, l_rights, 0);
            END IF; -- if outBox exists
            IF (l_inBox <> c_NOOID)     -- inBox exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_inBox, l_userAdminGroup, l_rights, 0);
            END IF; -- if inBox exists
            IF (l_hotList <> c_NOOID)   -- hotList exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_hotList, l_userAdminGroup, l_rights, 0);
            END IF; -- if hotList exists
            IF (l_shoppingCart <> c_NOOID) -- shoppingCart exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_shoppingCart, l_userAdminGroup, l_rights, 0);
            END IF; -- if shoppingCart exists
            IF (l_orders <> c_NOOID)    -- orders exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_orders, l_userAdminGroup, l_rights, 0);
            END IF; -- if orders exists

            IF (l_profile <> c_NOOID)   -- profile exists?
            THEN
                -- set rights on profile for administrator group:
                SELECT  SUM (id)
                INTO    l_rights
                FROM    IBSDEV1.ibs_Operation
                WHERE   name IN
                        (
                            'view', 'read', 'change', 'viewRights', 'setRights',
                            'createLink', 'viewElems', 'viewProtocol'
                        );
                CALL IBSDEV1.p_Rights$setRights
                    (l_profile, l_userAdminGroup, l_rights, 0);
            END IF; -- if profile exists

            -- set rights on work box, out box, in box, hotList,
            -- shopping cart, and orders for the user himself:
            SELECT  SUM (id)
            INTO    l_rights
            FROM    IBSDEV1.ibs_Operation
            WHERE   name IN
                    (
                        'new', 'view', 'read', 'change', 'delete',
                        'createLink', 'distribute', 'addElem', 'delElem',
                        'viewElems'
                    );

            IF (l_workBox <> c_NOOID)   -- workBox exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_workBox, ai_wUserId, l_rights, 0);
            END IF; -- if workBox exists
            IF (l_outBox <> c_NOOID)    -- outBox exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_outBox, ai_wUserId, l_rights, 0);
            END IF; -- if outBox exists
            IF (l_inBox <> c_NOOID)     -- inBox exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_inBox, ai_wUserId, l_rights, 0);
            END IF; -- if inBox exists
            IF (l_hotList <> c_NOOID)   -- hotList exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_hotList, ai_wUserId, l_rights, 0);
            END IF; -- if hotList exists
            IF (l_shoppingCart <> c_NOOID) -- shoppingCart exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_shoppingCart, ai_wUserId, l_rights, 0);
            END IF; -- if shoppingCart exists
            IF (l_orders <> c_NOOID)    -- orders exists?
            THEN
                CALL IBSDEV1.p_Rights$setRights
                    (l_orders, ai_wUserId, l_rights, 0);
            END IF; -- if orders exists

            IF (l_profile <> c_NOOID)   -- profile exists?
            THEN
                -- set rights on user profile for the user himself:
                SELECT  SUM (id)
                INTO    l_rights
                FROM    IBSDEV1.ibs_Operation
                WHERE   name IN
                        ('view', 'read', 'change', 'createLink', 'viewElems');
                CALL IBSDEV1.p_Rights$setRights
                    (l_profile, ai_wUserId, l_rights, 0);
            END IF; -- if profile exists
        END IF; -- else workspace was created

        -- finish transaction:
        COMMIT;                         -- make changes permanent
    END IF; -- else workspace does not exist yet

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Workspace_01$create1', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'l_workspace_s', l_workspace_s,
        'ai_userId', ai_userId, 'l_name', l_name,
        'ai_op', ai_op, 'l_desc', l_desc,
        'ai_wUserId', ai_wUserId, 'l_workspaceProc', l_workspaceProc,
        'l_admin', l_admin, 'l_execStr', l_execStr,
        'l_domainId', l_domainId, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    CALL IBSDEV1.logError (500, 'p_Workspace_01$create2', l_sqlcode, l_ePos,
        '', 0, 'l_workBox_s', l_workBox_s,
        '', 0, 'l_outBox_s', l_outBox_s,
        '', 0, 'l_inBox_s', l_inBox_s,
        '', 0, 'l_news_s', l_news_s,
        '', 0, 'l_hotList_s', l_hotList_s,
        '', 0, 'l_profile_s', l_profile_s,
        '', 0, 'l_shoppingCart_s', l_shoppingCart_s,
        '', 0, 'l_orders_s', l_orders_s,
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Workspace_01$create


------------------------------------------------------------------------------
-- Creates the specific data for a new workspace of this domain
-- (incl. rights check). <BR>
--
-- input parameters:
-- param   userId             ID of the user who is creating the workspace.
-- param   op                 Operation to be performed (possibly in the
--                              future used for rights check).
-- param   wUserId            ID of the user for whom the workspace is
--                              created.
-- param   domainId           ID of the domain where the user belongs to.
-- param   workspace_s        String representation of the oid of the
--                              workspace.
--
-- output parameters:
-- param   workBox            Oid of the workbox.
-- param   outBox             Oid of the outbox.
-- param   inBox              Oid of the inbox.
-- param   news               Oid of the news folder.
-- param   hotList            Oid of the hotlist.
-- param   profile            Oid of the user profile.
-- param   shoppingCart       Oid of the shopping cart.
-- param   orders             Oid of the order container.
-- returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The workspace was not created due to an unknown
--                          error.
--

-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_Workspace_01$createObjects');
CALL IBSDEV1.p_dropProc ('p_Workspace_01$createObjects');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Workspace_01$createObjects
(
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_wUserId          INT,
    IN  ai_domainId         INT,
    IN  ai_workspace_s      VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_languageId    INT DEFAULT 0;  -- the current language

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT DEFAULT 0;  -- return value of function
    DECLARE l_ePos          VARCHAR (2000) DEFAULT '';
                                            -- error position description
    DECLARE l_workBox       CHAR (8) FOR BIT DATA;
    DECLARE l_workBox_s     VARCHAR (18);
    DECLARE l_outBox        CHAR (8) FOR BIT DATA;
    DECLARE l_outBox_s      VARCHAR (18);
    DECLARE l_inBox         CHAR (8) FOR BIT DATA;
    DECLARE l_inBox_s       VARCHAR (18);
    DECLARE l_news          CHAR (8) FOR BIT DATA;
    DECLARE l_news_s        VARCHAR (18);
    DECLARE l_hotList       CHAR (8) FOR BIT DATA;
    DECLARE l_hotList_s     VARCHAR (18);
    DECLARE l_profile       CHAR (8) FOR BIT DATA;
    DECLARE l_profile_s     VARCHAR (18);
    DECLARE l_shoppingCart  CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCart_s VARCHAR (18);
    DECLARE l_orders        CHAR (8) FOR BIT DATA;
    DECLARE l_orders_s      VARCHAR (18);
    DECLARE l_name          VARCHAR (63) DEFAULT '';
                                            -- name of the current object
    DECLARE l_desc          VARCHAR (255) DEFAULT '';
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_workBox           = c_NOOID;
    SET l_workBox_s         = c_NOOID_s;
    SET l_outBox            = c_NOOID;
    SET l_outBox_s          = c_NOOID_s;
    SET l_inBox             = c_NOOID;
    SET l_inBox_s           = c_NOOID_s;
    SET l_news              = c_NOOID;
    SET l_news_s            = c_NOOID_s;
    SET l_hotList           = c_NOOID;
    SET l_hotList_s         = c_NOOID_s;
    SET l_profile           = c_NOOID;
    SET l_profile_s         = c_NOOID_s;
    SET l_shoppingCart      = c_NOOID;
    SET l_shoppingCart_s    = c_NOOID_s;
    SET l_orders            = c_NOOID;
    SET l_orders_s          = c_NOOID_s;

-- body:
    -- workBox (0x01010021 - Container)
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_wspWorkBox', l_name, l_desc);
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, 16842785, l_name,
        ai_workspace_s, 1, 0, c_NOOID_s, l_desc, l_workBox_s, l_workBox);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'ceate workbox';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- news (0x01010801 - NewsContainer)
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_wspNews', l_name, l_desc);
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, 16844801, l_name,
        ai_workspace_s,1, 0, c_NOOID_s,l_desc, l_news_s, l_news);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'ceate news';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- hotList (0x01010041 - ReferenceContainer)
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_wspHotList', l_name, l_desc);
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, 16842817, l_name,
        ai_workspace_s, 1, 0, c_NOOID_s, l_desc, l_hotList_s, l_hotList);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'ceate hotlist';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- ensure that the hotlist is displayed in the menu:
    UPDATE  IBSDEV1.ibs_Object
    SET     showInMenu = 1
    WHERE   oid = l_hotList;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'UPDATE hotlist';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- outBox (0x01011D01 - SentObjectContainer)
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_wspOutBox', l_name, l_desc);
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, 16850177, l_name,
        ai_workspace_s, 1, 0, c_NOOID_s, l_desc, l_outBox_s, l_outBox);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'ceate outbox';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- inBox (0x01012D01 - Inbox)
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_wspInBox', l_name, l_desc);
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, 16854273, l_name,
        ai_workspace_s, 1, 0, c_NOOID_s, l_desc, l_inBox_s, l_inBox);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'ceate inbox';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- profile (0x01013801 - UserProfile)
    CALL IBSDEV1.p_ObjectDesc_01$get
        (c_languageId, 'OD_wspProfile', l_name, l_desc);
    CALL IBSDEV1.p_UserProfile_01$create
        (ai_userId, ai_op, ai_wUserId, 16857089,
        l_name, ai_workspace_s, 1, 0, c_NOOID_s, l_desc, l_profile_s);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_stringToByte (l_profile_s, l_profile);

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'ceate user profile';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- store the objects within the workspace:
    INSERT  INTO ibs_Workspace
            (userId, domainId, workBox, outBox, inBox, news, hotList, profile, 
            shoppingCart, orders)
    VALUES  (ai_wUserId, ai_domainId, l_workBox, l_outBox, l_inBox, l_news,
            l_hotList, l_profile, l_shoppingCart, l_orders);

    IF (l_sqlcode <> 0 OR l_retValue <> c_ALL_RIGHT)
    THEN
        -- create error entry:
        SET l_ePos = 'INSERT INTO ibs_Workspace';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- finish the transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Workspace_01$createObjects1', l_sqlcode, l_ePos,
        'l_retValue', l_retValue, 'ai_workspace_s', ai_workspace_s,
        'ai_userId', ai_userId, 'l_name', l_name,
        'ai_op', ai_op, 'l_desc', l_desc,
        'ai_wUserId', ai_wUserId, '', '',
        'ai_domainId', ai_domainId, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    CALL IBSDEV1.logError (500, 'p_Workspace_01$createObjects2', l_sqlcode, l_ePos,
        '', 0, 'l_workBox_s', l_workBox_s,
        '', 0, 'l_outBox_s', l_outBox_s,
        '', 0, 'l_inBox_s', l_inBox_s,
        '', 0, 'l_news_s', l_news_s,
        '', 0, 'l_hotList_s', l_hotList_s,
        '', 0, 'l_profile_s', l_profile_s,
        '', 0, 'l_shoppingCart_s', l_shoppingCart_s,
        '', 0, 'l_orders_s', l_orders_s,
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_Workspace_01$createObjects


------------------------------------------------------------------------------
-- Changes the attributes of an existing workspace. <BR>
-- There is no rights check done at this time because it makes no sense to
-- check whether a user has access to his/her own workspace.
--
-- input parameters:
-- param   userId             ID of the user who is changing the workspace
--                              and whose workspace is changed.
-- param   op                 Operation to be performed (possibly in the
--                              future used for rights check).
-- param   workspace          The workspace of the user itself.
-- param   workBox            The workBox of the user.
-- param   outBox             The box for outgoing messages/objects.
-- param   inBox              The box for incoming messages/objects.
-- param   news               Everything which is new for the user.
-- param   hotList            The personalized bookmarks of the user.
-- param   profile            The user's profile.
-- param   shoppingCart       The shopping cart.
-- param   orders             The order container.
--
-- output parameters:
-- returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--


-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_Workspace_01$change');
CALL IBSDEV1.p_dropProc ('p_Workspace_01$change');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Workspace_01$change
(
    IN ai_userId        INT,
    IN ai_op            INT,
    IN ai_workspace     CHAR (8) FOR BIT DATA,
    IN ai_workBox       CHAR (8) FOR BIT DATA,
    IN ai_outBox        CHAR (8) FOR BIT DATA,
    IN ai_inBox         CHAR (8) FOR BIT DATA,
    IN ai_news          CHAR (8) FOR BIT DATA,
    IN ai_hotList       CHAR (8) FOR BIT DATA,
    IN ai_profile       CHAR (8) FOR BIT DATA,
    IN ai_shoppingCart  CHAR (8) FOR BIT DATA,
    IN ai_orders        CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- perform update:
    UPDATE IBSDEV1.ibs_Workspace
        SET workspace = ai_workspace,
           workBox = ai_workBox,
           outBox = ai_outBox,
           inBox = ai_inBox,
           news = ai_news,
           hotList = ai_hotList,
           profile = ai_profile
    WHERE  userId = ai_userId;

    IF ai_shoppingCart <> c_NOOID
    THEN

    -- shoppingCart defined?
        UPDATE IBSDEV1.ibs_Workspace
        SET    shoppingCart = ai_shoppingCart
        WHERE  userId = ai_userId;
    END IF;

    IF ai_orders <> c_NOOID
    THEN

    -- order container defined?
        UPDATE IBSDEV1.ibs_Workspace
        SET    orders = ai_orders
        WHERE  userId = ai_userId;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    END IF;

    -- check if the workspace exists:

    IF l_rowcount <= 0
    THEN

    -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- if workspace does not exist
    COMMIT;

    -- return the state value
    RETURN l_retValue;
END;
-- p_Workspace_01$change



------------------------------------------------------------------------------
-- Gets all data from a given workspace (incl. rights check). <BR>
--
-- input parameters:
-- param   oid_s              Id of the object to be retrieved.
-- param   userId             Id of the user who is getting the data.
-- param   op                 Operation to be performed (used for rights
--                              check).
--
-- output parameters:
-- param   state              The object's state.
-- param   tVersionId         ID of the object's type (correct version).
-- param   typeName           Name of the object's type.
-- param   name               Name of the object itself.
-- param   containerId        ID of the object's container.
-- param   containerName      Name of the object's container.
-- param   containerKind      Kind of object/container relationship.
-- param   isLink             Is the object a link?
-- param   linkedObjectId     Link if isLink is true.
-- param   owner              ID of the owner of the object.
-- param   ownerName          Name of the owner of the object.
-- param   creationDate       Date when the object was created.
-- param   creator            ID of person who created the object.
-- param   creatorName        Name of person who created the object.
-- param   lastChanged        Date of the last change of the object.
-- param   changer            ID of person who did the last change to the
--                              object.
-- param   changerName        Name of person who did the last change to the
--                              object.
-- param   validUntil         Date until which the object is valid.
-- param   description        Description of the object.
-- param   showInNews         show in news flag
-- param   showInNews         Display object in the news.
-- param   checkedOut         Is the object checked out?
-- param   checkOutDate       Date when the object was checked out
-- param   checkOutUser       id of the user which checked out the object
-- param   checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- param   checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
--
--
-- param   domainId           The id of the domain where the workspace
--                              belongs to.
-- param   workspace          The workspace of the user itself.
-- param   workBox            The workBox of the user.
-- param   outBox             The box for outgoing messages/objects.
-- param   inBox              The box for incoming messages/objects.
-- param   news               Everything which is new for the user.
-- param   hotList            The personalized bookmarks of the user.
-- param   profile            The user's profile.
-- param   publicWsp          The oid of public container being at the same
--                              place as this workspace.
-- param   shoppingCart       The shopping cart.
-- param   orders             The order container.
-- returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--


-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_Workspace_01$retrieve');
CALL IBSDEV1.p_dropProc ('p_Workspace_01$retrieve');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Workspace_01$retrieve
(
    IN ai_oid_s         VARCHAR (18),
    IN ai_userId        INT,
    IN ai_op            INT,
    OUT ao_state        INT,
    OUT ao_tVersionId   INT,
    OUT ao_typeName     VARCHAR (63),
    OUT ao_name         VARCHAR (63),
    OUT ao_containerId  CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink       SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner        INT,
    OUT ao_ownerName    VARCHAR (63),
    OUT ao_creationDate TIMESTAMP,
    OUT ao_creator      INT,
    OUT ao_creatorName  VARCHAR (63),
    OUT ao_lastChanged  TIMESTAMP,
    OUT ao_changer      INT,
    OUT ao_changerName  VARCHAR (63),
    OUT ao_validUntil   TIMESTAMP,
    OUT ao_description  VARCHAR (255),
    OUT ao_showInNews   SMALLINT,
    OUT ao_checkedOut   SMALLINT,
    OUT ao_checkOutDate TIMESTAMP,
    OUT ao_checkOutUser INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_domainId     INT,
    OUT ao_workspace    CHAR (8) FOR BIT DATA,
    OUT ao_workBox      CHAR (8) FOR BIT DATA,
    OUT ao_outBox       CHAR (8) FOR BIT DATA,
    OUT ao_inBox        CHAR (8) FOR BIT DATA,
    OUT ao_news         CHAR (8) FOR BIT DATA,
    OUT ao_hotList      CHAR (8) FOR BIT DATA,
    OUT ao_profile      CHAR (8) FOR BIT DATA,
    OUT ao_publicWsp    CHAR (8) FOR BIT DATA,
    OUT ao_shoppingCart CHAR (8) FOR BIT DATA,
    OUT ao_orders       CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- initialize local variables and return values:
    SET l_retValue = c_NOT_OK;

-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
                                ao_tVersionId, ao_typeName, ao_name,
                                ao_containerId, ao_containerName,
                                ao_containerKind, ao_isLink, ao_linkedObjectId,
                                ao_owner, ao_ownerName, ao_creationDate,
                                ao_creator, ao_creatorName, ao_lastChanged,
                                ao_changer, ao_changerName, ao_validUntil,
                                ao_description, ao_showInNews, ao_checkedOut,
                                ao_checkOutDate, ao_checkOutUser,
                                ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;


    IF l_retValue = c_ALL_RIGHT THEN
        SET l_sqlcode = 0;
        -- retrieve object type specific data:
        SELECT domainId, workspace, workBox, outBox, inBox, news, hotList,
            profile, publicWsp, shoppingCart, orders
        INTO    ao_domainId, ao_workspace, ao_workBox, ao_outBox, ao_inBox,
            ao_news, ao_hotList, ao_profile, ao_publicWsp, ao_shoppingCart,
            ao_orders
        FROM IBSDEV1.ibs_Workspace
        WHERE  workspace = l_oid;

        -- check if retrieve was performed properly:
        IF l_sqlcode = 100 THEN
            --  no row affected?
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;

    -- if operation properly performed
    COMMIT;

    -- return the state value:

    RETURN l_retValue;
END;
-- p_Workspace_01$retrieve


------------------------------------------------------------------------------
-- Gets all data from a given workspace for the actual user. <BR>
-- There is no rights check done at this time because it makes no sense to
-- check whether a user has access to his/her own workspace.
--
-- input parameters:
-- param   userId             ID of the user who wants to get his/her
--                              workspace data.
-- param   op                 Operation to be performed (possibly in the
--                              future used for rights check).
--
-- output parameters:
-- param   domainId           The id of the domain where the workspace
--                              belongs to.
-- param   workspace          The workspace of the user itself.
-- param   workBox            The workBox of the user.
-- param   outBox             The box for outgoing messages/objects.
-- param   inBox              The box for incoming messages/objects.
-- param   news               Everything which is new for the user.
-- param   hotList            The personalized bookmarks of the user.
-- param   profile            The user's profile.
-- param   publicWsp          The oid of public container being at the same
--                              place as this workspace.
-- param   shoppingCart       The shopping cart.
-- param   orders             The order container.
-- returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Workspace_01$retrieveForActU');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Workspace_01$retrieveForActU
(
    IN ai_userId         INT,
    IN ai_op            INT,
    OUT ao_domainId   INT,
    OUT ao_workspace  CHAR (8) FOR BIT DATA,
    OUT ao_workBox    CHAR (8) FOR BIT DATA,
    OUT ao_outBox     CHAR (8) FOR BIT DATA,
    OUT ao_inBox      CHAR (8) FOR BIT DATA,
    OUT ao_news       CHAR (8) FOR BIT DATA,
    OUT ao_hotList    CHAR (8) FOR BIT DATA,
    OUT ao_profile    CHAR (8) FOR BIT DATA,
    OUT ao_publicWsp  CHAR (8) FOR BIT DATA,
    OUT ao_shoppingCart CHAR (8) FOR BIT DATA,
    OUT ao_orders     CHAR (8) FOR BIT DATA,
    OUT ao_name       VARCHAR (63)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- definitions:
    -- define return constants

    DECLARE c_ALL_RIGHT   INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- define return values

    DECLARE l_retValue    INT;
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_rowcount  INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- set constants
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;

    -- return value of this procedure
    -- initialize return values

    SET l_retValue = c_ALL_RIGHT;
-- body:
    -- get the data of the workspace and return them
    SET l_sqlcode = 0;

    SELECT name, domainId, workspace, workBox,
         outBox, inBox, news, hotList, profile, publicWsp,
         shoppingCart, orders
    INTO   ao_name, ao_domainId, ao_workspace, ao_workBox, ao_outBox, ao_inBox, ao_news, ao_hotList,
           ao_profile, ao_publicWsp, ao_shoppingCart, ao_orders
    FROM   IBSDEV1.ibs_Workspace w, IBSDEV1.ibs_object o
    WHERE  w.userId = ai_userId AND    w.workspace = o.oid;

    -- check if the workspace exists:

    IF l_sqlcode = 100 
    THEN

    -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;

    -- if workspace does not exist
    -- return the state value
  RETURN l_retValue;
END;

-- p_Workspace_01$retrieveForActU



------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- input parameters:
-- param   oid_s              ID of the object to be deleted.
-- param   userId             ID of the user who is deleting the object.
-- param   op                 Operation to be performed (used for rights
--                              check).
--
-- output parameters:
-- returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--


-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Workspace_01$delete');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Workspace_01$delete
(
    IN ai_userId        INT,
    IN ai_op            INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;


-- definitions:
    -- define return constants

    DECLARE c_ALL_RIGHT   INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue    INT;
    DECLARE l_workspace CHAR (8) FOR BIT DATA;
    DECLARE l_workBox   CHAR (8) FOR BIT DATA;
    DECLARE l_outBox    CHAR (8) FOR BIT DATA;
    DECLARE l_inBox     CHAR (8) FOR BIT DATA;
    DECLARE l_news      CHAR (8) FOR BIT DATA;
    DECLARE l_hotList   CHAR (8) FOR BIT DATA;
    DECLARE l_profile   CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCart CHAR (8) FOR BIT DATA;
    DECLARE l_orders    CHAR (8) FOR BIT DATA;
    DECLARE l_workspace_s VARCHAR (18);
    DECLARE l_workBox_s VARCHAR (18);
    DECLARE l_outBox_s  VARCHAR (18);
    DECLARE l_inBox_s   VARCHAR (18);
    DECLARE l_news_s    VARCHAR (18);
    DECLARE l_hotList_s VARCHAR (18);
    DECLARE l_profile_s VARCHAR (18);
    DECLARE l_shoppingCart_s VARCHAR (18);
    DECLARE l_orders_s  VARCHAR (18);
    DECLARE l_sqlcode   INT DEFAULT 0;
    DECLARE l_rowcount  INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- get the data of the workspace
    SET l_sqlcode = 0;
    SELECT  workspace, workBox, outBox, inBox, news, hotList, profile,
            shoppingCart, orders
    INTO    l_workspace, l_workBox, l_outBox, l_inBox, l_news, l_hotList,
            l_profile, l_shoppingCart, l_orders
    FROM    IBSDEV1.ibs_Workspace
    WHERE   userId = ai_userId;

    -- check if the workspace exists:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN
        -- delete workspace itself
        DELETE FROM IBSDEV1.ibs_Workspace
        WHERE   userId = ai_userId;

        -- convert OBJECTIDs to OBJECTIDSTRINGs:
        CALL IBSDEV1.p_byteToString (l_workspace, l_workspace_s);
        CALL IBSDEV1.p_byteToString (l_workBox, l_workBox_s);
        CALL IBSDEV1.p_byteToString (l_outBox, l_outBox_s);
        CALL IBSDEV1.p_byteToString (l_inBox, l_inBox_s);
        CALL IBSDEV1.p_byteToString (l_news, l_news_s);
        CALL IBSDEV1.p_byteToString (l_hotList, l_hotList_s);
        CALL IBSDEV1.p_byteToString (l_profile, l_profile_s);
        CALL IBSDEV1.p_byteToString (l_shoppingCart, l_shoppingCart_s);
        CALL IBSDEV1.p_byteToString (l_orders, l_orders_s);

        -- delete belonging objects:
        CALL IBSDEV1.p_Object$delete (l_orders_s, ai_userId, ai_op);
        CALL IBSDEV1.p_Object$delete (l_shoppingCart_s, ai_userId, ai_op);
        CALL IBSDEV1.p_Object$delete (l_hotList_s, ai_userId, ai_op);
        CALL IBSDEV1.p_Object$delete (l_news_s, ai_userId, ai_op);
        CALL IBSDEV1.p_Object$delete (l_inBox_s, ai_userId, ai_op);
        CALL IBSDEV1.p_Object$delete (l_outBox_s, ai_userId, ai_op);
        CALL IBSDEV1.p_Object$delete (l_workBox_s, ai_userId, ai_op);
        CALL IBSDEV1.p_Object$delete (l_workspace_s, ai_userId, ai_op);
        CALL IBSDEV1.p_UserProfile_01$delete (l_profile_s, ai_userId, ai_op);

        COMMIT;
    ELSE
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF; -- else the workspace does not exist

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Workspace_01$delete


------------------------------------------------------------------------------
-- This procedure is used after importing a xml - structure in the
-- workspace of the user to assign standardobjects like Inbox, Outbox,
-- ShoppingCart, NewsContainer, Hotlist etc. to Workspace
-- (Table ibs_workspace).
-- The objects to be assigned are identified via their type and only the
-- objects are assigned where no other object was assigned to Workspace Table
-- in procedure p_Workspace$createObjects. <BR>
--
--
-- input parameters:
-- param   oid_s              oid of user.
--
-- output parameters:
-- returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--


-- delete existing procedure
-- CALL IBSDEV1.p_dropProc ('p_Workspace$assignStdObjects');
CALL IBSDEV1.p_dropProc ('p_Workspace_assignStdObjects');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Workspace_assignStdObjects
(
    IN ai_oid_s         VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE     INT;
    -- define return constants
    DECLARE c_ALL_RIGHT INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue  INT;
    DECLARE l_print_error VARCHAR (255);
    DECLARE l_workbox   CHAR (8) FOR BIT DATA;
    DECLARE l_outbox    CHAR (8) FOR BIT DATA;
    DECLARE l_inbox     CHAR (8) FOR BIT DATA;
    DECLARE l_news      CHAR (8) FOR BIT DATA;
    DECLARE l_profile   CHAR (8) FOR BIT DATA;
    DECLARE l_workspace CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCart CHAR (8) FOR BIT DATA;
    DECLARE l_orders    CHAR (8) FOR BIT DATA;
    DECLARE l_posnopath VARCHAR (254);
    DECLARE l_oid       CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode   INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET l_print_error = 'set newscontainer to first newscontainer to be found in workspace';

-- body:
    -- convert input oid string to oid:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- get all oids of all standardobjects in workspace
    SELECT  w.workbox, w.outbox, w.inbox, w.news, w.profile, w.shoppingCart,
            w.orders, w.workspace, ow.posnopath
    INTO    l_workbox, l_outbox, l_inbox, l_news, l_profile, l_shoppingCart,
            l_orders, l_workspace, l_posnopath
    FROM    IBSDEV1.ibs_Workspace w, IBSDEV1.ibs_User u,
            IBSDEV1.ibs_Object ow
    WHERE   w.userId = u.id
        AND u.oid = l_oid
        AND ow.oid = w.workspace;

    -- check workbox:
    IF (l_workbox = c_NOOID)
    THEN
        -- set workbox to first container to be found in workspace
        SELECT  COALESCE (MIN (o.oid), c_NOOID)
        INTO    l_oid
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_Type t
        WHERE   o.state = 2
            AND o.tVersionId = t.actVersion
            AND t.code = 'Container'
            AND o.posnopath Like l_posnopath || '%';

        UPDATE  IBSDEV1.ibs_Workspace
        SET     workbox = l_oid
        WHERE   workspace = l_workspace;
    END IF;

    -- check outbox:
    IF (l_outbox = c_NOOID)
    THEN
        -- set outbox to first SentObjectContainer to be found in workspace
        SELECT  COALESCE (MIN (o.oid), c_NOOID)
        INTO    l_oid
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_Type t
        WHERE   o.state = 2
            AND o.tVersionId = t.actVersion
            AND t.code = 'SentObjectContainer'
            AND o.posnopath LIKE l_posnopath || '%';

        UPDATE  IBSDEV1.ibs_Workspace
        SET     outbox = l_oid
        WHERE   workspace = l_workspace;
    END IF;

    -- check inbox:
    IF (l_inbox = c_NOOID)
    THEN
        -- set inbox to first Inbox to be found in workspace:
        SELECT  COALESCE (MIN (o.oid), c_NOOID)
        INTO    l_oid
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_Type t
        WHERE   o.state = 2
            AND o.tVersionId = t.actVersion
            AND t.code = 'Inbox'
            AND o.posnopath LIKE l_posnopath || '%';

        UPDATE  IBSDEV1.ibs_Workspace
        SET     inbox = l_oid
        WHERE   workspace = l_workspace;
    END IF;

    -- check news:
    IF (l_news = c_NOOID)
    THEN
        CALL IBSDEV1.p_Debug(l_print_error);

        SELECT  COALESCE (MIN (o.oid), c_NOOID)
        INTO    l_oid
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_Type t
        WHERE   o.state = 2
            AND o.tVersionId = t.actVersion
            AND t.code = 'NewsContainer'
            AND o.posnopath LIKE l_posnopath || '%';

        UPDATE  IBSDEV1.ibs_Workspace
        SET     news = l_oid
        WHERE   workspace = l_workspace;
    END IF;

    -- check profile:
    IF (l_profile = c_NOOID)
    THEN
        SELECT  COALESCE (MIN (o.oid), c_NOOID)
        INTO    l_oid
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_Type t
        WHERE   o.state = 2
            AND o.tVersionId = t.actVersion
            AND t.code = 'UserProfile'
            AND o.posnopath LIKE l_posnopath || '%';

        UPDATE  IBSDEV1.ibs_Workspace
        SET     profile = l_oid
        WHERE   workspace = l_workspace;
    END IF;

    -- return the state value:
    RETURN l_retValue;
END;
