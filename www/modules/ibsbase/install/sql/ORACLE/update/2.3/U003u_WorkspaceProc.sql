/******************************************************************************
 * All stored procedures regarding the ibs_Workspace table. <BR>
 *
 * @version     $Revision: 1.1 $, $Date: 2002/12/09 14:06:21 $
 *              $Author: kreimueller $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new workspace (incl. rights check). <BR>
 * The rights are checked against the root of the system.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the workspace.
 * @param   @op                 Operation to be performed (possibly in the 
 *                              future used for rights check).
 * @param   @wUserId            ID of the user for whom the workspace is 
 *                              created.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The workspace was not created due to an unknown 
 *                          error.
 */
CREATE OR REPLACE FUNCTION p_Workspace_01$create
(
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_wUserId              INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    c_languageId            CONSTANT INTEGER := 0; -- the current language (default)

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_rights                INTEGER := 0;
    l_domainOid             RAW (8);
    l_domainOid_s           VARCHAR2 (18);
    l_domainId              INTEGER;
    l_workspacesOid         RAW (8);
    l_workspacesOid_s       VARCHAR2 (18);
    l_admin                 INTEGER;
    l_userAdminGroup        INTEGER;
    l_workspaceProc         VARCHAR2 (63);
    l_publicWsp             RAW (8);
    l_cnt                   INTEGER := 0;
    l_workspace             RAW (8);
    l_workspace_s           VARCHAR2 (18);
    l_execStr               VARCHAR2 (255);
    l_workBox               RAW (8);
    l_outBox                RAW (8);
    l_inBox                 RAW (8);
    l_news                  RAW (8);
    l_hotList               RAW (8);
    l_profile               RAW (8);
    l_shoppingCart          RAW (8);
    l_orders                RAW (8);
    l_name                  VARCHAR2 (63);  -- name of the current object
    l_desc                  VARCHAR2 (255); -- description of the current object
l_dummyOid RAW (8);

BEGIN
    -- set domain id (division by 0x01000000 => get just the first byte):
    l_domainId := (ai_wUserId - MOD (ai_wUserId, 16777216)) / 16777216;

    BEGIN
        -- get domain info:
        SELECT  oid, workspacesOid, adminId, userAdminGroupId, workspaceProc, 
                publicOid
        INTO    l_domainOid, l_workspacesOid, l_admin, l_userAdminGroup,
                l_workspaceProc, l_publicWsp
        FROM    ibs_Domain_01 
        WHERE   id = l_domainId;

    EXCEPTION
        WHEN TOO_MANY_ROWS THEN
            ibs_error.log_error (ibs_error.error, 'p_Workspace_01$create.get domain info',
            'TOO_MANY_ROWS for domain ' || l_domainId);
            RAISE;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Workspace_01$create.get domain info',
            'OTHER error for domain ' || l_domainId);
            RAISE;
    END;

    -- convert domain oid to string value:
    p_byteToString (l_domainOid, l_domainOid_s);
    -- convert workspaces oid to string value:
    p_byteToString (l_workspacesOid, l_workspacesOid_s);

    -- get rights to be set for this user on her/his own workspace:
    BEGIN
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_rights := 0;
    END;

    -- check if there exists already a workspace for this user:
    SELECT  COUNT (*)
    INTO    l_cnt
    FROM    ibs_Workspace 
    WHERE   userId = ai_wUserId;
    IF (l_cnt > 0)                      -- workspace already exists?
    THEN
        l_retValue := c_ALREADY_EXISTS;
    ELSE                                -- workspace does not exist yet
        /*[SPCONV-ERR(110)]:BEGIN TRAN statement ignored*/
        -- create workspace of the user:
        p_ObjectDesc_01$get (c_languageId, 'OD_wspPrivate', l_name, l_desc);
        l_retValue := p_Object$performCreate (ai_userId, ai_op, stringToInt ('0x01013201'),
            l_name, l_workspacesOid_s, 1, 0, c_NOOID_s, l_desc,
            l_workspace_s, l_workspace);

        -- delete all actual defined rights:
        p_Rights$deleteObjectRights (l_workspace);

        -- set rights for user and user administrator group:
        BEGIN
            SELECT  SUM (id)
            INTO    l_rights
            FROM    ibs_Operation 
            WHERE   name IN ('view', 'read', 'viewElems');
            p_Rights$setRights (l_workspace, l_userAdminGroup, l_rights, 0);
            p_Rights$setRights (l_workspace, ai_wUserId, l_rights, 0);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;
        END;

        -- create the several components of the workspace:
        l_execStr := 
            'DECLARE l_dummy INTEGER; ' ||
            'BEGIN l_dummy := ' || l_workspaceProc ||
            ' (' || TO_CHAR (ai_userId) || ', ' || 
                    TO_CHAR (ai_op) || ', ' || 
                    TO_CHAR (ai_wUserId) || ', ' || 
                    TO_CHAR (l_domainId) || ', ' || 
                    '''' || l_workspace_s || '''' || ');' ||
            ' END;';
        EXEC_SQL (l_execStr);

        -- store the objects within the workspace:
        l_cnt := 0;
        BEGIN
            UPDATE  ibs_Workspace
            SET     workspace = l_workspace,
                    publicWsp = l_publicWsp
            WHERE   userId = ai_wUserId;

            l_cnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_Workspace_01$create.store objects',
                'OTHER error for objects ' || l_workspace || ' and ' || l_publicWsp || 
                ' for user ' || ai_wUserId);
                RAISE;
        END;

        -- check if the workspace was created:
        IF (l_cnt <= 0) THEN            -- workspace was not created?
            -- set the return value with the error code:
            l_retValue := c_NOT_OK;
        ELSE                            -- workspace was created
            -- ensure that the already taken changes cannot be undone:
            COMMIT WORK;

            -- get objects of workspace:
            BEGIN
                SELECT  workBox, outBox, inBox, news,
                        hotList, profile, shoppingCart, orders
                INTO    l_workBox, l_outBox, l_inBox, l_news, 
                        l_hotList, l_profile, l_shoppingCart, l_orders 
                FROM    ibs_Workspace          
                WHERE   userId = ai_wUserId; 

            EXCEPTION
                WHEN TOO_MANY_ROWS THEN
                    ibs_error.log_error (ibs_error.error, 'p_Workspace_01$create.get objects',
                    'TOO_MANY_ROWS for user ' || ai_wUserId);
                    RAISE;
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error, 'p_Workspace_01$create.get objects',
                    'OTHER error for user ' || ai_wUserId);
                    RAISE;
            END;

            IF (l_news <> c_NOOID)      -- the news container exists?
            THEN
                -- set rights on news container 
                -- for administrator group and the user himself:
                BEGIN
                    SELECT  SUM (id)
                    INTO    l_rights
                    FROM    ibs_Operation 
                    WHERE   name IN ('view', 'read', 'viewElems');
                    p_Rights$setRights (l_news, l_userAdminGroup, l_rights, 0);
                    p_Rights$setRights (l_news, ai_wUserId, l_rights, 0);
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        NULL;
                END;
            END IF; -- if the news container exists

            -- set rights on work box, out box, in box, hotList, 
            -- shopping cart, and orders for administrator group:
            BEGIN
                SELECT  SUM (id)
                INTO    l_rights
                FROM    ibs_Operation 
                WHERE   name IN ('new', 'view', 'read', 'change', 'delete', 
                            'viewRights', 'setRights', 'createLink', 
                            'distribute', 'addElem', 'delElem', 'viewElems', 
                            'viewProtocol');
                IF (l_workBox <> c_NOOID) -- workBox exists?
                THEN
                    p_Rights$setRights (l_workBox, l_userAdminGroup, l_rights, 0);
                END IF; -- if workBox exists
                IF (l_outBox <> c_NOOID) -- outBox exists?
                THEN
                    p_Rights$setRights (l_outBox, l_userAdminGroup, l_rights, 0);
                END IF; -- if outBox exists
                IF (l_inBox <> c_NOOID) -- inBox exists?
                THEN
                    p_Rights$setRights (l_inBox, l_userAdminGroup, l_rights, 0);
                END IF; -- if inBox exists
                IF (l_hotList <> c_NOOID) -- hotList exists?
                THEN
                    p_Rights$setRights (l_hotList, l_userAdminGroup, l_rights, 0);
                END IF; -- if hotList exists
                IF (l_shoppingCart <> c_NOOID) -- shoppingCart exists?
                THEN
                    p_Rights$setRights (l_shoppingCart, l_userAdminGroup, l_rights, 0);
                END IF; -- if shoppingCart exists
                IF (l_orders <> c_NOOID) -- orders exists?
                THEN
                    p_Rights$setRights (l_orders, l_userAdminGroup, l_rights, 0);
                END IF; -- if orders exists
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;
            END;

            IF (l_profile <> c_NOOID)   -- profile exists?
            THEN
                -- set rights on profile for administrator group:
                BEGIN
                    SELECT  SUM (id)
                    INTO    l_rights
                    FROM    ibs_Operation 
                    WHERE   name IN ('view', 'read', 'change', 
                                'viewRights', 'setRights', 'createLink', 
                                'viewElems', 'viewProtocol');
                    p_Rights$setRights (l_profile, l_userAdminGroup, l_rights, 0);
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        NULL;
                END;
            END IF; -- if profile exists

            -- set rights on work box, out box, in box, hotList, 
            -- shopping cart, and orders for the user himself:
            BEGIN
                SELECT  SUM (id)
                INTO    p_Workspace_01$create.l_rights
                FROM    ibs_Operation 
                WHERE   name IN ('new', 'view', 'read', 'change', 'delete', 
                            'createLink', 'distribute', 
                            'addElem', 'delElem', 'viewElems');
                IF (l_workBox <> c_NOOID) -- workBox exists?
                THEN
                    p_Rights$setRights (l_workBox, ai_wUserId, l_rights, 0);
                END IF; -- if workBox exists
                IF (l_outBox <> c_NOOID) -- outBox exists?
                THEN
                    p_Rights$setRights (l_outBox, ai_wUserId, l_rights, 0);
                END IF; -- if outBox exists
                IF (l_inBox <> c_NOOID) -- inBox exists?
                THEN
                    p_Rights$setRights (l_inBox, ai_wUserId, l_rights, 0);
                END IF; -- if inBox exists
                IF (l_hotList <> c_NOOID) -- hotList exists?
                THEN
                    p_Rights$setRights (l_hotList, ai_wUserId, l_rights, 0);
                END IF; -- if hotList exists
                IF (l_shoppingCart <> c_NOOID) -- shoppingCart exists?
                THEN
                    p_Rights$setRights (l_shoppingCart, ai_wUserId, l_rights, 0);
                END IF; -- if shoppingCart exists
                IF (l_orders <> c_NOOID) -- orders exists?
                THEN
                    p_Rights$setRights (l_orders, ai_wUserId, l_rights, 0);
                END IF; -- if orders exists
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;
            END;

            IF (l_profile <> c_NOOID)   -- profile exists?
            THEN
                -- set rights on user profile for the user himself:
                BEGIN
                    SELECT  SUM (id)
                    INTO    p_Workspace_01$create.l_rights
                    FROM    ibs_Operation 
                    WHERE   name IN ('view', 'read', 'change', 
                                'createLink', 'viewElems');
                    p_Rights$setRights (l_profile, ai_wUserId, l_rights, 0);
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        NULL;
                END;
            END IF; -- if profile exists
        END IF; -- else workspace was created

        -- finish transaction:
        COMMIT WORK;                    -- make changes permanent
    END IF; -- else workspace does not exist yet

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workspace_01$createObjects',
            'Input: ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_wUserId = ' || ai_wUserId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Workspace_01$create;
/

show errors;

/******************************************************************************
 * Creates the specific data for a new workspace of this domain 
 * (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the workspace.
 * @param   @op                 Operation to be performed (possibly in the 
 *                              future used for rights check).
 * @param   @wUserId            ID of the user for whom the workspace is 
 *                              created.
 * @param   @domainId           ID of the domain where the user belongs to.
 * @param   @workspace_s        String representation of the oid of the 
 *                              workspace.
 *
 * @output parameters:
 * @param   @workBox            Oid of the workbox.
 * @param   @outBox             Oid of the outbox.
 * @param   @inBox              Oid of the inbox.
 * @param   @news               Oid of the news folder.
 * @param   @hotList            Oid of the hotlist.
 * @param   @profile            Oid of the user profile.
 * @param   @shoppingCart       Oid of the shopping cart.
 * @param   @orders             Oid of the order container.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The workspace was not created due to an unknown 
 *                          error.
 */
CREATE OR REPLACE FUNCTION p_Workspace_01$createObjects
(
    -- input parameters:
    userId                  INTEGER,
    op                      INTEGER,
    wUserId                 INTEGER,
    domainId                INTEGER,
    workspace_s             VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_languageId            CONSTANT INTEGER := 0; -- the current language (default)

    -- local variables:
    retValue                INTEGER := c_ALL_RIGHT; -- return value of this procedure
    oid                     RAW (8) := c_NOOID;
    workBox                 RAW (8) := c_NOOID;
    outBox                  RAW (8) := c_NOOID;
    inBox                   RAW (8) := c_NOOID;
    news                    RAW (8) := c_NOOID;
    hotList                 RAW (8) := c_NOOID;
    profile                 RAW (8) := c_NOOID;
    shoppingCart            RAW (8) := c_NOOID;
    orders                  RAW (8) := c_NOOID;
    workBox_s               VARCHAR2 (18); 
    outBox_s                VARCHAR2 (18);
    inBox_s                 VARCHAR2 (18); 
    news_s                  VARCHAR2 (18); 
    hotList_s               VARCHAR2 (18);
    profile_s               VARCHAR2 (18); 
    shoppingCart_s          VARCHAR2 (18);
    orders_s                VARCHAR2 (18);
    l_name                  VARCHAR2 (63);  -- name of the current object
    l_desc                  VARCHAR2 (255); -- description of the current object

BEGIN
    -- workBox (0x01010021 - Container)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspWorkBox', l_name, l_desc);
    retValue := p_Object$performCreate (userId, op, 16842785, 
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            workBox_s, workBox);
            
    -- news (0x01010801 - NewsContainer)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspNews', l_name, l_desc);
    retValue := p_Object$performCreate (userId, op, 16844801, 
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            news_s, news);

    -- hotList (0x01010041 - ReferenceContainer)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspHotList', l_name, l_desc);
    retValue := p_Object$performCreate (userId, op, 16842817, 
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            hotList_s, hotList);
    -- ensure that the hotlist is displayed in the menu:
    BEGIN
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = hotList;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Workspace_01$createNWR.Update showInMenu',
            'OTHER error for hotList ' || hotList);
            RAISE;
    END;

    -- outBox (0x01011D01 - SentObjectContainer)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspOutBox', l_name, l_desc);
    retValue := p_Object$performCreate (userId, op, 16850177, 
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            outBox_s, outBox);

    -- inBox (0x01012D01 - Inbox)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspInBox', l_name, l_desc);
    retValue := p_Object$performCreate (userId, op, 16854273, 
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            inBox_s, inBox);

    -- shoppingCart (0x01011401 - ShoppingCart)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspShoppingCart', l_name, l_desc);
    retValue := p_Object$performCreate (userId, op, 16847873, 
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            shoppingCart_s, shoppingCart);

    -- orders (0x01011201 - OrderContainer)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspOrders', l_name, l_desc);
    retValue := p_Object$performCreate (userId, op, 16847361, 
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            orders_s, orders);

    -- profile (0x01013801 - UserProfile)
    p_ObjectDesc_01$get (c_languageId, 'OD_wspProfile', l_name, l_desc);
    retValue := p_UserProfile_01$create (userId, op, wUserId, 16857089,
            l_name, workspace_s, 1, 0, c_NOOID_s, l_desc,
            profile_s);

    -- store the objects within the workspace:    
    p_stringToByte (profile_s, profile);     

    BEGIN
        INSERT INTO ibs_Workspace (userId, domainId,workBox, outBox,
                  inBox, news, hotList, profile, 
                  shoppingCart, orders)
        VALUES  ( wUserId, domainId,                
                  workBox, outBox,               
                  inBox, news,                
                  hotList, profile,               
                  shoppingCart, orders);
    EXCEPTION
        WHEN TOO_MANY_ROWS THEN
            ibs_error.log_error (ibs_error.error, 'p_Workspace_01$createObjects.insert',
            'TOO_MANY_ROWS for user ' || wUserId);
            RAISE;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Workspace_01$createObjects.insert',
            'OTHER error for user ' || wUserId);
            RAISE;
    END;
    
    COMMIT WORK;

    -- return the state value
    RETURN   retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workspace_01$createObjects',
            ', userId = ' || userId ||
            ', op = ' || op ||
            ', wUserId = ' || wUserId ||
            ', domainId = ' || domainId ||
            ', workspace_s = ' || workspace_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Workspace_01$createObjects;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing workspace. <BR>
 * There is no rights check done at this time because it makes no sense to
 * check whether a user has access to his/her own workspace.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is changing the workspace 
 *                              and whose workspace is changed.
 * @param   @op                 Operation to be performed (possibly in the 
 *                              future used for rights check).
 * @param   @workspace          The workspace of the user itself.
 * @param   @workBox            The workBox of the user.
 * @param   @outBox             The box for outgoing messages/objects.
 * @param   @inBox              The box for incoming messages/objects.
 * @param   @news               Everything which is new for the user.
 * @param   @hotList            The personalized bookmarks of the user.
 * @param   @profile            The user's profile.
 * @param   @shoppingCart       The shopping cart.
 * @param   @orders             The order container.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Workspace_01$change
(
    -- common input parameters:
    userId              INTEGER,
    op                  INTEGER,
    workspace           RAW,
    workBox             RAW,
    outBox              RAW,
    inBox               RAW,
    news                RAW,
    hotList             RAW,
    profile             RAW,
    shoppingCart        RAW DEFAULT hexToRaw ('0000000000000000'),
    orders              RAW DEFAULT hexToRaw ('0000000000000000')
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    ALL_RIGHT               CONSTANT INTEGER := 1;
    INSUFFICIENT_RIGHTS     CONSTANT INTEGER := 2;
    OBJECTNOTFOUND          CONSTANT INTEGER := 3;

    -- local variables:
    retValue                INTEGER := ALL_RIGHT;
    l_cnt                   INTEGER := 0;

BEGIN
    -- perform update:
    BEGIN
        UPDATE  ibs_Workspace
        SET     workspace = workspace,
                workBox = workBox,
                outBox = outBox,
                inBox = inBox,
                news = news,
                hotList = hotList,
                profile = profile
        WHERE   userId = userId;
        l_cnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Workspace_01$change.update',
            'OTHER error for userId ' || userId);
            RAISE;
    END;

    -- check if the workspace exists:
    IF (l_cnt > 0)                     -- the workspace exists?
    THEN
        IF (shoppingCart <> c_NOOID) -- shoppingCart defined?
        THEN
            BEGIN
                UPDATE  ibs_Workspace
                SET     shoppingCart = shoppingCart
                WHERE   userId = userId;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error, 'p_Workspace_01$change.updateShoppingCart',
                    'OTHER error for userId ' || userId);
                    RAISE;
            END;
        END IF; -- if shoppingCart defined

        IF (orders <> c_NOOID) -- order container defined?
        THEN
            BEGIN
                UPDATE  ibs_Workspace
                SET     orders = orders
                WHERE   userId = userId;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error, 'p_Workspace_01$change.updateOrderContainer',
                    'OTHER error for userId ' || userId);
                    RAISE;
            END;
        END IF; -- if order container defined
    ELSE                               -- workspace does not exist
        -- set the return value with the error code:
        retValue := OBJECTNOTFOUND;
    END IF; -- else workspace does not exist

    COMMIT WORK;

    -- return the state value
    RETURN retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workspace_01$change',
            ', userId = ' || userId  ||
            ', op = ' || op ||
            ', workspace = ' || workspace ||
            ', workBox = ' || workBox ||
            ', outBox = ' || outBox ||
            ', inBox = ' || inBox ||
            ', news = ' || news ||
            ', hotList = ' || hotList ||
            ', profile = ' || profile ||
            ', shoppingCart = ' || shoppingCart ||
            ', orders = ' || orders ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Workspace_01$change;
/

show errors;

/******************************************************************************
 * Gets all data from a given workspace (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              Id of the object to be retrieved.
 * @param   @userId             Id of the user who is getting the data.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
 * @param   @containerName      Name of the object's container.
 * @param   @containerKind      Kind of object/container relationship.
 * @param   @isLink             Is the object a link?
 * @param   @linkedObjectId     Link if isLink is true.
 * @param   @owner              ID of the owner of the object.
 * @param   @ownerName          Name of the owner of the object.
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @creatorName        Name of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the 
 *                              object.
 * @param   @changerName        Name of person who did the last change to the 
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         show in news flag
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   @domainId           The id of the domain where the workspace 
 *                              belongs to.
 * @param   @workspace          The workspace of the user itself.
 * @param   @workBox            The workBox of the user.
 * @param   @outBox             The box for outgoing messages/objects.
 * @param   @inBox              The box for incoming messages/objects.
 * @param   @news               Everything which is new for the user.
 * @param   @hotList            The personalized bookmarks of the user.
 * @param   @profile            The user's profile.
 * @param   @publicWsp          The oid of public container being at the same
 *                              place as this workspace.
 * @param   @shoppingCart       The shopping cart.
 * @param   @orders             The order container.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Workspace_01$retrieve
(
    -- common input parameters:
    oid_s          VARCHAR2,
    userId            INTEGER,
    op             INTEGER,
    -- common output parameters
    state          OUT  INTEGER,
    tVersionId     OUT  INTEGER,
    typeName       OUT  VARCHAR2,
    name           OUT  VARCHAR2,
    containerId    OUT  RAW,
    containerName  OUT  VARCHAR2,
    containerKind  OUT  INTEGER,
    isLink         OUT  NUMBER,
    linkedObjectId OUT  RAW,
    owner          OUT  INTEGER,
    ownerName      OUT  VARCHAR2,
    creationDate   OUT  DATE,
    creator        OUT  INTEGER,
    creatorName    OUT  VARCHAR2,
    lastChanged    OUT  DATE,
    changer        OUT  INTEGER,
    changerName    OUT  VARCHAR2,
    validUntil     OUT  DATE,
    description    OUT  VARCHAR2,
    showInNews     OUT  INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,    
    -- type-specific OUT  output parameters:
    domainId       OUT  INTEGER,
    workspace      OUT  RAW,
    workBox        OUT  RAW,
    outBox         OUT  RAW,
    inBox          OUT  RAW,
    news           OUT  RAW,
    hotList        OUT  RAW,
    profile        OUT  RAW,
    publicWsp      OUT  RAW,
    shoppingCart   OUT  RAW,
    orders         OUT  RAW    
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    ALL_RIGHT               CONSTANT INTEGER := 1;
    INSUFFICIENT_RIGHTS     CONSTANT INTEGER := 2;
    OBJECTNOTFOUND          CONSTANT INTEGER := 3;

    -- local variables:
    retValue                INTEGER := c_NOT_OK; -- return value of this procedure
    oid                     RAW (8);

BEGIN
    -- initialize return values:
    shoppingCart := c_NOOID;
    orders := c_NOOID;

    -- retrieve the base object data:
    retValue := p_Object$performRetrieve (
        oid_s, userId, op,
        state , tVersionId , typeName ,
        name , containerId , containerName ,
        containerKind , isLink , linkedObjectId ,
        owner , ownerName ,
        creationDate , creator , creatorName ,
        lastChanged , changer , changerName ,
        validUntil , description , 
        showInNews , 
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
        oid);

    IF (retValue = ALL_RIGHT)     -- operation properly performed?
    THEN
        -- retrieve object type specific data:
        BEGIN
            SELECT  domainId, workspace, 
                    workBox, outBox, inBox,
                    news, hotList,
                    profile, publicWsp,
                    shoppingCart, orders
            INTO    domainId, workspace,
                    workBox,  outBox, inBox, 
                    news, hotList,
                    profile, publicWsp,
                    shoppingCart, orders
            FROM    ibs_Workspace
            WHERE   workspace = oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                retValue := c_NOT_OK; -- set return value
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_Workspace_01$retrieve.select',
                'OTHER error for workspace ' || p_Workspace_01$retrieve.oid);
                RAISE;
        END;
    END IF; -- if operation properly performed

    COMMIT WORK;

    -- return the state value:
    RETURN  p_Workspace_01$retrieve.retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workspace_01$retrieve',
            ', oid_s = ' || oid_s ||
            ', userId = ' || userId  ||
            ', op = ' || op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Workspace_01$retrieve;
/

show errors;

/******************************************************************************
 * Gets all data from a given workspace for the actual user. <BR>
 * There is no rights check done at this time because it makes no sense to
 * check whether a user has access to his/her own workspace.
 *
 * @input parameters:
 * @param   @userId             ID of the user who wants to get his/her 
 *                              workspace data.
 * @param   @op                 Operation to be performed (possibly in the 
 *                              future used for rights check).
 *
 * @output parameters:
 * @param   @domainId           The id of the domain where the workspace 
 *                              belongs to.
 * @param   @workspace          The workspace of the user itself.
 * @param   @workBox            The workBox of the user.
 * @param   @outBox             The box for outgoing messages/objects.
 * @param   @inBox              The box for incoming messages/objects.
 * @param   @news               Everything which is new for the user.
 * @param   @hotList            The personalized bookmarks of the user.
 * @param   @profile            The user's profile.
 * @param   @publicWsp          The oid of public container being at the same
 *                              place as this workspace.
 * @param   @shoppingCart       The shopping cart.
 * @param   @orders             The order container.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Workspace_01$retrieveForActU
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- output parameters
    ao_domainId             OUT INTEGER,
    ao_workspace            OUT RAW,
    ao_workBox              OUT RAW,
    ao_outBox               OUT RAW,
    ao_inBox                OUT RAW,
    ao_news                 OUT RAW,
    ao_hotList              OUT RAW,
    ao_profile              OUT RAW,
    ao_publicWsp            OUT RAW,
    ao_shoppingCart         OUT RAW,
    ao_orders               OUT RAW,
    ao_name                 OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;

BEGIN
    -- initialize return values:
    ao_shoppingCart := c_NOOID;
    ao_orders := c_NOOID;

    -- get the data of the workspace and return them:
    BEGIN
        SELECT  name, domainId, workspace, workBox, outBox, 
                inBox, news, hotList, profile, publicWsp, 
                shoppingCart, orders
        INTO    ao_name, ao_domainId, ao_workspace, ao_workBox, ao_outBox,
                ao_inBox, ao_news, ao_hotList, ao_profile, ao_publicWsp,
                ao_shoppingCart, ao_orders
        FROM    ibs_Workspace w, ibs_Object o 
        WHERE   w.userId = ai_userId 
            AND w.workspace = o.oid;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- workspace does not exist?
            -- set the return value with the error code:
            l_retValue := c_OBJECTNOTFOUND;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Workspace_01$retrieveForActU.select',
            'OTHER error for userId ' || ai_userId);
            RAISE;
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workspace_01$retrieveForActU',
            'Input: ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Workspace_01$retrieveForActU;
/

show errors;

/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Workspace_01$delete
(
    ai_userId               INTEGER,
    ai_op                   INTEGER
)
RETURN INTEGER
AS
    -- define return constants
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of this procedure
    l_workspace             RAW (8);
    l_workBox               RAW (8);
    l_outBox                RAW (8);
    l_inBox                 RAW (8); 
    l_news                  RAW (8); 
    l_hotList               RAW (8);
    l_profile               RAW (8);
    l_shoppingCart          RAW (8);
    l_orders                RAW (8);
    l_workspace_s           VARCHAR2 (18); 
    l_workBox_s             VARCHAR2 (18); 
    l_outBox_s              VARCHAR2 (18);
    l_inBox_s               VARCHAR2 (18); 
    l_news_s                VARCHAR2 (18); 
    l_hotList_s             VARCHAR2 (18);
    l_profile_s             VARCHAR2 (18);
    l_shoppingCart_s        VARCHAR2 (18);
    l_orders_s              VARCHAR2 (18);
    l_dummy                 INTEGER;

BEGIN
    BEGIN
        SELECT  workspace, workBox, outBox, inBox,
                news, hotList, profile, shoppingCart,
                orders
        INTO    l_workspace, l_workBox, l_outBox, l_inBox, l_news, 
                l_hotList, l_profile, l_shoppingCart, l_orders
        FROM    ibs_Workspace
        WHERE   userId = ai_userId;

        -- at this point we know that the workspace exists.
        -- delete workspace itself:
        DELETE  ibs_Workspace
        WHERE   userId = ai_userId;

        -- convert OBJECTIDs to OBJECTIDSTRINGs:
        p_byteToString (l_workspace, l_workspace_s);
        p_byteToString (l_workBox, l_workBox_s);
        p_byteToString (l_outBox, l_outBox_s);
        p_byteToString (l_inBox, l_inBox_s);
        p_byteToString (l_news, l_news_s);
        p_byteToString (l_hotList, l_hotList_s);
        p_byteToString (l_profile, l_profile_s);
        p_byteToString (l_shoppingCart, l_shoppingCart_s);
        p_byteToString (l_orders, l_orders_s);
        -- delete belonging objects:
        l_dummy := p_Object$delete (l_orders_s, ai_userId, ai_op);
        l_dummy := p_Object$delete (l_shoppingCart_s, ai_userId, ai_op);
        l_dummy := p_Object$delete (l_hotList_s, ai_userId, ai_op);
        l_dummy := p_Object$delete (l_news_s, ai_userId, ai_op);
        l_dummy := p_Object$delete (l_inBox_s, ai_userId, ai_op);
        l_dummy := p_Object$delete (l_outBox_s, ai_userId, ai_op);
        l_dummy := p_Object$delete (l_workBox_s, ai_userId, ai_op);
        l_dummy := p_Object$delete (l_workspace_s, ai_userId, ai_op);
        l_dummy := p_UserProfile_01$delete (l_profile_s, ai_userId, ai_op);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the workspace does not exist
            -- set the return value with the error code:
            l_retValue := c_OBJECTNOTFOUND;
    END;

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workspace_01$delete',
            'Input: ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Workspace_01$delete;
/

show errors;

/******************************************************************************
 * This procedure is used after importing a xml - structure in the
 * workspace of the user to assign standardobjects like Inbox, Outbox,
 * ShoppingCart, NewsContainer, Hotlist etc. to Workspace
 * (Table ibs_workspace).
 * The objects to be assigned are identified via their type and only the
 * objects are assigned where no other object was assigned to Workspace Table
 * in procedure p_Workspace$createObjects. <BR>
 *
 *
 * @input parameters:
 * @param   @oid_s              oid of user.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Workspace$assignStdObjects
(
    ai_oid_s                VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_workbox               ibs_Workspace.workbox%TYPE;
    l_outbox                ibs_Workspace.outbox%TYPE;
    l_inbox                 ibs_Workspace.inbox%TYPE;
    l_news                  ibs_Workspace.news%TYPE;
    l_profile               ibs_Workspace.profile%TYPE;
    l_workspace             ibs_Workspace.workspace%TYPE;
    l_shoppingCart          ibs_Workspace.shoppingCart%TYPE;
    l_orders                ibs_Workspace.orders%TYPE;
    l_posnopath             ibs_Object.posNoPath%TYPE;
    l_oid                   ibs_Object.oid%TYPE;

-- body:
BEGIN
    -- convert input oid string to oid:
    p_StringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get all oids of all standardobjects in workspace
        SELECT  w.workbox, w.outbox, w.inbox, w.news, w.profile,
                w.shoppingCart, w.orders, w.workspace, ow.posnopath
        INTO    l_workbox, l_outbox, l_inbox, l_news, l_profile,
                l_shoppingCart, l_orders, l_workspace, l_posnopath
        FROM    ibs_Workspace w, ibs_User u, ibs_Object ow
        WHERE   w.userId = u.id
            AND u.oid = l_oid
            AND ow.oid = w.workspace;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the workspace object was not found
            -- set corresponding return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'get oids of workspace objects';
            RAISE;                      -- call common exception handler
        -- end when the workspace object was not found
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get oids of workspace objects';
            RAISE;                      -- call common exception handler
    END;

    -- check workbox:
    IF (l_workbox = c_NOOID)
    THEN
        BEGIN
            -- set workbox to first container to be found in workspace:
            SELECT  DECODE (MIN (o.oid), null, c_NOOID, MIN (o.oid))
            INTO    l_oid
            FROM    ibs_Object o, ibs_Type t
            WHERE   o.state = 2
                AND o.tVersionId = t.actVersion
                AND t.code = 'Container'
                AND o.posnopath LIKE l_posnopath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get workbox oid';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            UPDATE  ibs_Workspace
            SET     workbox = l_oid
            WHERE   workspace = l_workspace;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set workbox oid';
                RAISE;                  -- call common exception handler
        END;
    END IF;

    -- check outbox
    IF (l_outbox = c_NOOID)
    THEN
        BEGIN
            -- set outbox to first SentObjectContainer to be found in workspace
            SELECT  DECODE (MIN (o.oid), null, c_NOOID, MIN (o.oid))
            INTO    l_oid
            FROM    ibs_Object o, ibs_Type t
            WHERE   o.state = 2
                AND o.tVersionId = t.actVersion
                AND t.code = 'SentObjectContainer'
                AND o.posnopath LIKE l_posnopath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get outbox oid';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            UPDATE  ibs_Workspace
            SET     outbox = l_oid
            WHERE   workspace = l_workspace;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set outbox oid';
                RAISE;                  -- call common exception handler
        END;
    END IF;

    -- check inbox
    IF (l_inbox = c_NOOID)
    THEN
        BEGIN
            -- set inbox to first Inbox to be found in workspace
            SELECT  DECODE (MIN (o.oid), null, c_NOOID, MIN (o.oid))
            INTO    l_oid
            FROM    ibs_Object o, ibs_Type t
            WHERE   o.state = 2
                AND o.tVersionId = t.actVersion
                AND t.code = 'Inbox'
                AND o.posnopath LIKE l_posnopath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get inbox oid';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            UPDATE  ibs_Workspace
            SET     inbox = l_oid
            WHERE   workspace = l_workspace;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set inbox oid';
                RAISE;                  -- call common exception handler
        END;
    END IF;

    -- check news
    IF (l_news = c_NOOID)
    THEN
        debug ('set newscontainer to first newscontainer to be found in workspace');
        BEGIN
            SELECT  DECODE (MIN (o.oid), null, c_NOOID, MIN (o.oid))
            INTO    l_oid
            FROM    ibs_Object o, ibs_Type t
            WHERE   o.state = 2
                AND o.tVersionId = t.actVersion
                AND t.code = 'NewsContainer'
                AND o.posnopath LIKE l_posnopath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get news countainer oid';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            UPDATE  ibs_Workspace
            SET     news = l_oid
            WHERE   workspace = l_workspace;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set news countainer oid';
                RAISE;                  -- call common exception handler
        END;
    END IF;

    -- check profile
    IF (l_profile = c_NOOID)
    THEN
        BEGIN
            -- set profile to first userprofile to be found in workspace
            SELECT  DECODE (MIN (o.oid), null, c_NOOID, MIN (o.oid))
            INTO    l_oid
            FROM    ibs_Object o, ibs_Type t
            WHERE   o.state = 2
                AND o.tVersionId = t.actVersion
                AND t.code = 'UserProfile'
                AND o.posnopath LIKE l_posnopath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get user profile oid';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            UPDATE  ibs_Workspace
            SET     profile = l_oid
            WHERE   workspace = l_workspace;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set user profile oid';
                RAISE;                  -- call common exception handler
        END;
    END IF;

    -- check shoppingcart
    IF (l_shoppingCart = c_NOOID)
    THEN
        BEGIN
            -- set shoppingcart to first shoppingcart to be found in workspace
            SELECT  DECODE (MIN (o.oid), null, c_NOOID, MIN (o.oid))
            INTO    l_oid
            FROM    ibs_Object o, ibs_Type t
            WHERE   o.state = 2
                AND o.tVersionId = t.actVersion
                AND t.code = 'ShoppingCart'
                AND o.posnopath LIKE l_posnopath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get shopping cart oid';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            UPDATE  ibs_Workspace
            SET     shoppingcart = l_oid
            WHERE   workspace = l_workspace;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set shopping cart oid';
                RAISE;                  -- call common exception handler
        END;
    END IF;

    -- check orders
    IF (l_orders = c_NOOID)
    THEN
        BEGIN
            -- set shoppingcart to first shoppingcart to be found in workspace
            SELECT  DECODE (MIN (o.oid), null, c_NOOID, MIN (o.oid))
            INTO    l_oid
            FROM    ibs_Object o, ibs_Type t
            WHERE   o.state = 2
                AND o.tVersionId = t.actVersion
                AND t.code = 'OrderContainer'
                AND o.posnopath LIKE l_posnopath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get order container oid';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            UPDATE  ibs_Workspace
            SET     orders = l_oid
            WHERE   workspace = l_workspace;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set order container oid';
                RAISE;                  -- call common exception handler
        END;
    END IF;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Workspace$assignStdObjects', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Workspace$assignStdObjects;
/

show errors;

EXIT;
