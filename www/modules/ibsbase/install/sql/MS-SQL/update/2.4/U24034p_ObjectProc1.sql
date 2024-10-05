/******************************************************************************
 * All stored procedures regarding the ibs_Object table. <BR>
 *
 * @version     $Id: U24034p_ObjectProc1.sql,v 1.1 2007/09/25 16:15:10 bbuchegger Exp $
 *
 * @author      Klaus Reimüller (KR)  980426
 ******************************************************************************
 */


/******************************************************************************
 * An empty Dummy because of a cyclic-dependency. <BR>
 *
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    RETURN  0
GO
-- p_Object$create



/******************************************************************************
 * Get the oid of a specific tab of an object. <BR>
 * If the tab does not exist for this object or the tab itself is not an object
 * there is no oid available an OBJECTNOTFOUND ist returned.
 *
 * @input parameters:
 * @param   ai_oid              Id of the object for which to get the tab oid.
 * @param   ai_tabCode          The code of the tab (as it is in ibs_Tab).
 *
 * @output parameters:
 * @param   ao_tabOid           The oid of the tab object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The tab object was not found.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$getTabOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$getTabOid
(
    -- input parameters:
    @ai_oid                 OBJECTID,
    @ai_tabCode             NAME,
    -- output parameters:
    @ao_tabOid              OBJECTID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_CONT_PARTOF          INT,            -- containerKind part of

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_ALREADY_EXISTS       = 21,
    @c_CONT_PARTOF          = 2

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- get the oid of the tab object:
    SELECT  @ao_tabOid = o.oid
    FROM    ibs_Object o, ibs_ConsistsOf c, ibs_Tab t
    WHERE   o.containerId = @ai_oid
        AND o.containerKind = @c_CONT_PARTOF
        AND o.consistsOfId = c.id
        AND c.tabId = t.id
        AND t.code = @ai_tabCode

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        'get tab oid', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- check if the tab object was found:
    IF (@l_rowCount <= 0)               -- the tab was not found?
    BEGIN
        -- set corresponding return value:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- if the tab was not found

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Object$getTabOid', @l_error, @l_ePos,
            '', 0,
            'ai_tabCode', @ai_tabCode
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Object$getTabOid


/******************************************************************************
 * Create a tab for an object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure ensures that a specific tab for an object exists. If the tab
 * is already there nothing is done.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the tab.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_oid              Id of the object for which the tab shall be
 *                              generated.
 * @param   ai_tVersionId       Type of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          There are no tabs to generate.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$createTab'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$createTab
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_oid                 OBJECTID,
    @ai_tabCode             NAME,
    -- output parameters:
    @ao_tabOid              OBJECTID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_TK_OBJECT            INT,            -- tab kind Object
    @c_TK_LINK              INT,            -- tab kind Link
    @c_PROC_CREATE          NAME,           -- code for stored procedure which
                                            -- creates an object

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_oid_s                OBJECTIDSTRING, -- string representation of oid
    @l_tabOid_s             OBJECTIDSTRING, -- string representation of tab oid
    @l_consistsOfId         ID,             -- id of tab in ibs_ConsistsOf
    @l_tabTVersionId        TVERSIONID,     -- tVersionId of the actual tab
    @l_tabName              NAME,           -- the tab's name
    @l_tabDescription       DESCRIPTION,    -- the tab's description
    @l_tabProc              STOREDPROCNAME, -- name of stored procedure for
                                            -- creating the tab object
    @l_state                INT             -- the state of the object where
                                            -- the tab belongs to and thus the
                                            -- state of the tab itself

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_ALREADY_EXISTS       = 21,
    @c_NOOID_s              = '0x0000000000000000',
    @c_TK_OBJECT            = 2,
    @c_TK_LINK              = 3,
    @c_PROC_CREATE          = 'create'

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- convert oid to string:
    EXEC    p_byteToString @ai_oid, @l_oid_s OUTPUT

    -- get the tab data:
    EXEC @l_retValue =
        p_Object$getTabOid @ai_oid, @ai_tabCode, @ao_tabOid OUTPUT

    -- check if the tab already exists:
    IF (@l_retValue = @c_OBJECTNOTFOUND) -- tab does not exist yet?
    BEGIN
        -- get the data of the tab:
        -- (recognize only known names of tabs which can be constructed by
        -- p_Object$create)
       SELECT @l_tabTVersionId = t.tVersionId,
                @l_tabName = d.objName, @l_tabDescription = d.objDesc,
                @l_tabProc = COALESCE (p.name, 'p_Object$create'),
                @l_consistsOfId = c.id,
                @l_state = o.state
        FROM ibs_Object o, ibs_ConsistsOf c,
              ibs_ObjectDesc_01 d,
              ibs_Tab t
              LEFT OUTER JOIN
                ibs_TVersionProc p
                ON t.tVersionId = p.tVersionId
        WHERE o.oid = @ai_oid
            AND c.tVersionId = o.tVersionId
            AND c.tabId = t.id
            AND t.kind IN (@c_TK_OBJECT, @c_TK_LINK)
            AND t.multilangKey = d.name
            AND p.code = @c_PROC_CREATE
            AND t.code = @ai_tabCode

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            'get tab data', @l_ePos OUTPUT, @l_rowCount OUTPUT
        IF (@l_error <> 0 OR @l_rowCount <= 0) -- an error occurred?
            GOTO exception          -- call common exception handler

        -- create the tab:
        EXEC @l_retValue = @l_tabProc @ai_userId, @ai_op,
                @l_tabTVersionId, @l_tabName, @l_oid_s, 2,
                0, @c_NOOID_s, @l_tabDescription,
                @l_tabOid_s OUTPUT

        -- check if there occurred an error:
        IF (@l_retValue = @c_ALL_RIGHT) -- everything o.k.?
        BEGIN
            -- convert oid_s to oid:
            EXEC p_stringToByte @l_tabOid_s, @ao_tabOid OUTPUT

            -- set the tab id and state:
            UPDATE  ibs_Object
            SET     consistsOfId = @l_consistsOfId,
                    state = @l_state
            WHERE   oid = @ao_tabOid

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                'update consistsOfId', @l_ePos OUTPUT, @l_rowCount OUTPUT
            IF (@l_error <> 0 OR @l_rowCount <= 0) -- an error occurred?
                GOTO exception          -- call common exception handler
        END -- if everything o.k.
    END -- if tab does not exist yet

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Object$createTab', @l_error, @l_ePos,
            'ai_userId', @ai_userId,
            'l_oid_s', @l_oid_s,
            'ai_op', @ai_op,
            'ai_tabCode', @ai_tabCode,
            '', 0,
            'l_tabOid_s', @l_tabOid_s
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Object$createTab


/******************************************************************************
 * Create the tabs for an object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for creating the tabs regarding a business object.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the tabs.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_oid              Id of the object for which the tabs shall be
 *                              generated.
 * @param   ai_tVersionId       Type of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          There are no tabs to generate.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$createTabs'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$createTabs
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_oid                 OBJECTID,
    @ai_tVersionId          TVERSIONID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_TK_OBJECT            INT,            -- tab kind Object
    @c_TK_LINK              INT,            -- tab kind Link

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_tabOid               OBJECTID,       -- the oid of the tab object
    @l_tabCode              NAME            -- the code for the actual tab

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_ALREADY_EXISTS       = 21,
    @c_TK_OBJECT            = 2,
    @c_TK_LINK              = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- define cursor for running through all tabs:
    DECLARE tabCursor INSENSITIVE CURSOR FOR
        SELECT  t.code AS tabCode
        FROM    ibs_ConsistsOf c, ibs_Tab t
        WHERE   c.tVersionId = @ai_tVersionId
            AND c.tabId = t.id
            AND t.kind IN (@c_TK_OBJECT, @c_TK_LINK)
        FOR READ ONLY

    -- open the cursor:
    OPEN    tabCursor

    -- get the first tab:
    FETCH NEXT FROM tabCursor INTO @l_tabCode

    IF (@@FETCH_STATUS = -1)            -- found no tab?
    BEGIN
        SELECT  @l_retValue = @c_OBJECTNOTFOUND -- set return value
    END -- if found no tab

    -- loop through all found tabs:
    WHILE   (@@FETCH_STATUS <> - 1)     -- found another tab?
    BEGIN
        -- create the tab:
        EXEC @l_retValue = p_Object$createTab @ai_userId, @ai_op, @ai_oid,
            @l_tabCode, @l_tabOid OUTPUT

        -- get the next tab:
        FETCH   NEXT FROM tabCursor INTO @l_tabCode
    END -- while found another tab

    -- close the not longer needed cursor:
    CLOSE tabCursor
    DEALLOCATE tabCursor

    -- return the state value:
    RETURN  @l_retValue

cursorException:                        -- an error occurred within cursor
    -- close the not longer needed cursor:
    CLOSE tabCursor
    DEALLOCATE tabCursor
exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Object$createTabs', @l_error, @l_ePos,
            'ai_userId', @ai_userId,
            '', '',
            'ai_op', @ai_op,
            '', '',
            'ai_tVersionId', @ai_tVersionId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Object$createTabs


/******************************************************************************
 * Delete a tab of an object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for deleting a tab from an existing object.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is deleting the tab.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_oid              Id of the object for which the tab shall be
 *                              deleted.
 * @param   ai_tabCode          The (unique) code of the tab.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          There are no tabs to generate.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$deleteTab'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$deleteTab
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_oid                 OBJECTID,
    @ai_tabCode             NAME
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_PROC_DELETE          NAME,           -- code for stored procedure which
                                            -- creates an object

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_tabOid               OBJECTID,       -- the oid of the tab object
    @l_tabOid_s             OBJECTIDSTRING, -- string representation of oid
    @l_tabProc              STOREDPROCNAME  -- name of stored procedure for
                                            -- creating the tab object


    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_PROC_DELETE          = 'delete'

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- get the tab data:
    SELECt @l_tabProc = COALESCE (p.name, 'p_Object$delete'),
            @l_tabOid = o.oid
    FROM ibs_Object o
           LEFT OUTER JOIN
           ibs_TVersionProc p
            ON o.tVersionId = p.tVersionId,
     ibs_ConsistsOf c,
     ibs_Tab t
    WHERE o.containerId = @ai_oid
        AND t.code = @ai_tabCode
        AND o.consistsOfId = c.id
        AND c.tabId = t.id
        AND p.code = @c_PROC_DELETE

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        'update consistsOfId', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler
    -- check if the tab object exists:
    IF (@l_rowCount > 0)                -- the tab object was found?
    BEGIN
        -- convert oid to oid_s:
        EXEC p_ByteToString @l_tabOid, @l_tabOid_s OUTPUT

        -- delete the tab object:
        EXEC @l_retValue = @l_tabProc @l_tabOid_s, @ai_userId, @ai_op
    END -- if the tab object was found
    ELSE                                -- the tab object was not found
    BEGIN
        -- set return value:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- the tab object was not found

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Object$createTabs', @l_error, @l_ePos,
            'ai_userId', @ai_userId,
            'ai_tabCode', @ai_tabCode,
            'ai_op', @ai_op,
            'l_tabOid_s', @l_tabOid_s,
            '', 0,
            'l_tabProc', @l_tabProc
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Object$deleteTab


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for creating a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              String representation of OID of the newly
 *                              created object.
 * [@param   @oid]              Oid of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performCreate'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performCreate
(
    -- input parameters:
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_tVersionId     TVERSIONID,
    @ai_name           NAME,
    @ai_containerId_s  OBJECTIDSTRING,
    @ai_containerKind  INT,
    @ai_isLink         BOOL,
    @ai_linkedObjectId_s OBJECTIDSTRING,
    @ai_description    DESCRIPTION,
    -- output parameters:
    @ao_oid_s          OBJECTIDSTRING OUTPUT,
    @ao_oid            OBJECTID = 0x0000000000000000 OUTPUT
)
AS
-- declarations

    -- define constants
    DECLARE @c_NOOID OBJECTID,
            @c_EMPTYPOSNOPATH VARCHAR(4),
            @c_NOT_OK INT,
            @c_ALL_RIGHT INT,
            @c_INSUFFICIENT_RIGHTS INT,
            @c_OBJECTNOTFOUND INT,
            @c_RIGHT_UPDATE RIGHTS,     -- access permission
            @c_RIGHT_INSERT RIGHTS,     -- access permission
            @c_CHECKEDOUT INT           -- 5th bit of attribute 'flags'

    SELECT  @c_NOOID = 0x0000000000000000,
            @c_EMPTYPOSNOPATH = '0000',
            @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1,
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_OBJECTNOTFOUND = 3,
            @c_RIGHT_UPDATE = 8,
            @c_RIGHT_INSERT = 1,
            @c_CHECKEDOUT = 16,
            @ao_oid_s = '0x0000000000000000',
            @ao_oid   = 0x0000000000000000

    -- define local variables
    DECLARE @l_retValue INT,        -- return value of this procedure
            @l_rights RIGHTS,       -- return value of rights proc.
            @l_co_userId USERID,
            @l_check INT,               -- check out value
            @l_fullName NAME,
            @l_icon NAME,
            @l_name NAME,
            @l_description DESCRIPTION,
            @l_containerId OBJECTID,
            @l_containerOid2 OBJECTID,
            @l_linkedObjectId OBJECTID
    SELECT  @l_retValue = @c_NOT_OK,
            @l_rights = 0,
            @l_co_userId = 0,
            @l_check = 0,
            @l_fullName = '',
            @l_icon = 'icon.gif',
            @l_name = @ai_name,
            @l_description = @ai_description,
            @l_containerId = @c_NOOID,
            @l_containerOid2 = @c_NOOID,
            @l_linkedObjectId = @c_NOOID
    --
    --  TRIGGER variables: used by trigger reimplementation
    DECLARE
        @l_id                   ID,             -- the id of the inserted object
        @l_origId               ID,             -- originally set id
        @l_typeName             NAME,           -- the name of the type
        @l_isContainer          BOOL,           -- is the object a container?
        @l_showInMenu           BOOL,           -- shall the object be shown in
                                                -- the menu?
        @l_showInNews           INT,           -- shall the object be shown in
                                                -- the news container?
        @l_oLevel               INT,            -- level of object within
                                                -- hierarchy
        @l_posNo                POSNO,          -- position of object within
                                                -- container
        @l_posNoHex             VARCHAR (4),    -- hex representation of posNo
        @l_posNoPath            POSNOPATH_VC,   -- the posNoPath
        @l_flags                INT,            -- the flag which are set
        @l_validUntil           DATETIME,       -- date until which the object
                                                -- is valid
        @l_rKey                 INT             -- rights key
    SELECT
        @l_id = 0,
        @l_origId = 0,
        @l_typeName = 'UNKNOWN',
        @l_showInMenu = 0,
        @l_showInNews = 0,
        @l_oLevel = 1,                          -- lowest possible object level
        @l_posNo = 0,
        @l_posNoHex = '0000',
        @l_posNoPath = @c_EMPTYPOSNOPATH,
        @l_flags = 0,
        @l_validUntil = getDate(),
        @l_rKey = 0
    --  TRIGGER variables (END)
    --

-- convertions:
--EXEC p_showActDateTime 'conversions '
    EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT
    EXEC p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT

--EXEC p_showActDateTime 'select first'
    -- retrieve check-out-info for new objects container?
    SELECT @l_co_userId = co.userId, @l_check = o.flags & @c_CHECKEDOUT
    FROM ibs_Object o JOIN ibs_Checkout_01 co ON o.oid = co.oid
    WHERE o.oid = @l_containerId
--EXEC p_showActDateTime 'after select'


    -- is the object checked out?
    IF ((@l_check = @c_CHECKEDOUT) AND (@l_co_userId <> @ai_userId))
        SELECT @l_retValue = @c_INSUFFICIENT_RIGHTS
    ELSE
    BEGIN
--EXEC p_showActDateTime 'check rights'
        -- container is not checked out,
        -- now check if user has permission to create object
        EXEC @l_rights = p_Rights$checkRights
            @ao_oid,                        -- given object to be accessed by user
            @l_containerId,                 -- container of given object
            @ai_userId,                     -- user_id
            @ai_op,                         -- required rights user must have to
                                            -- insert/update object
            @l_rights OUTPUT                -- returned value

        -- check if the user has the necessary rights
        IF (@l_rights = @ai_op)             -- the user has the rights?
        BEGIN
            -- add the new tuple to the ibs_Object table:
---------
--
-- START get and calculate base-data
--       (old trigger functionality!)
--
            --
            -- 1. compute id an oid for new object
            --
--EXEC p_showActDateTime 'compute oid '
            EXEC @l_id = p_ObjectId$getNext
/* KR 20050303 performance tuning:
 * use new table ibs_ObjectId to get the unique id
            SELECT  @l_id = COALESCE (MAX (id) + 1, 1)
            FROM    ibs_Object
*/

            EXEC p_createOid @ai_tVersionId, @l_id, @ao_oid OUTPUT
            EXEC p_byteToString @ao_oid, @ao_oid_s OUTPUT
--EXEC p_showActDateTime 'computed oid'

            --
            -- 2. compute olevel, posno and posnopath
            --
            -- derive position number from other objects within container:
            -- The posNo is one more than the actual highest posNo within the
            -- container or 1 if there is no object within the container yet.
            SELECT  @l_posNo = COALESCE (MAX (posNo) + 1, 1)
            FROM    ibs_Object
            WHERE   containerId = @l_containerId
--EXEC p_showActDateTime 'after posno1'
            -- convert the position number into hex representation:
            EXEC p_IntToHexString @l_posNo, @l_posNoHex OUTPUT
--EXEC p_showActDateTime 'after posno2'

            -- derive position level and rkey from container:
            -- The level of an object is the level of the container plus 1
            -- or 0, if there is no container.
            SELECT  @l_oLevel = COALESCE (oLevel + 1, 1),
                    @l_rKey = rKey,
                    @l_validUntil = validUntil
            FROM    ibs_Object
            WHERE   oid = @l_containerId

            -- check if there were some data found:
            IF (@@ROWCOUNT = 0)             -- no data found?
            BEGIN
                -- no container found for given object;
                -- must be root-object
                SELECT @l_oLevel = 1
                SELECT @l_rKey = 0
            END -- if no data found
--EXEC p_showActDateTime 'after oLevel'

            -- calculate new position path
            IF (@l_containerId <> @c_NOOID)     -- object is within a container?
            BEGIN
                -- compute the posNoPath as posNoPath of container concatenated by
                -- the posNo of this object:
                SELECT  DISTINCT @l_posNoPath = posNoPath + @l_posNoHex,
                        @l_containerOid2 = containerId
                FROM    ibs_Object
                WHERE   oid = @l_containerId

                -- check if there were some data found:
                IF (@@ROWCOUNT = 0)             -- no data found?
                BEGIN
                    -- compute the posNoPath as posNo of this object:
                    SELECT  @l_posNoPath = @l_posNoHex
                END -- if no data found
--EXEC p_showActDateTime 'after posnopath'
            END -- if object is within a container
            ELSE                                -- object is not within a container
                                                -- i.e. it is on top level
            BEGIN
                -- compute the posNoPath as posNo of this object:
                SELECT  @l_posNoPath = @l_posNoHex
            END -- else object is not within a container


            --
            -- 3. get type-info: type name, icon and containerId, showInMenus
            --                   showInNews
            --
            SELECT  @l_typeName = t.name, @l_isContainer = t.isContainer,
                    @l_showInMenu = t.showInMenu, @l_showInNews = t.showInNews * 4,
                    @l_icon = t.icon
            FROM    ibs_Type t, ibs_TVersion tv
            WHERE   tv.id = @ai_tVersionId
                AND t.id = tv.typeId
--EXEC p_showActDateTime 'after type  '

            --
            -- 4. distinguish between reference/no-reference objects
            --
            IF (@ai_isLink = 1)                  -- link object?
            BEGIN
                --
                -- IMPORTANT: rights-key will be set in here
                --
                -- get data of linked object into link:
                -- If the linked object is itself a link the link shall point to the
                -- original linked object.
                SELECT  @l_name = name, @l_typeName = typeName,
                        @l_description = description, @l_flags = flags,
                        @l_icon = icon, @l_rKey = rKey
                FROM    ibs_Object
                WHERE   oid = @l_linkedObjectId
            END
            ELSE
            BEGIN
                IF (@l_name = '' OR @l_name = ' ' OR @l_name IS NULL)
                    SELECT @l_name = @l_typeName
            END

            -- compute correct rights key for actual user:
            EXEC p_Rights$getKeyForOwner @l_rKey, @ai_userId, @l_rKey OUTPUT

            --
            -- 5. calculate new flags value: add showInNews
            --
            SELECT @l_flags = (@l_flags & 0x7FFFFFFB) + @l_showInNews
--
-- END get and calculate base-data
--
---------
            --
            -- last but not least: insert new information
            --
--EXEC p_showActDateTime 'before insert'
            INSERT INTO ibs_Object
                   (id, oid, /*state,*/ tVersionId, typeName,
                    isContainer, name,
                    containerId, containerKind, containerOid2,
                    isLink, linkedObjectId,
                    showInMenu, flags, owner, oLevel,
                    posNo, posNoPath, creationDate, creator, lastChanged,
                    changer, validUntil, description, icon,
                    /*processState,*/ rKey)
            VALUES (@l_id, @ao_oid, /*???,*/ @ai_tVersionId, @l_typeName,
                    @l_isContainer, @l_name,
                    @l_containerId, @ai_containerKind, @l_containerOid2,
                    @ai_isLink, @l_linkedObjectId,
                    @l_showInMenu, @l_flags, @ai_userid, @l_oLevel,
                    @l_posNo, @l_posNoPath, getDate(), @ai_userId, getDate(),
                    @ai_userId, @l_validUntil, @ai_description, @l_icon,
                    /*???,*/ @l_rKey)
--EXEC p_showActDateTime 'after insert'

            --
            -- create tabs (if necessary)
            --
            IF (@ai_containerKind <> 2)     -- object is independent?
            BEGIN
--EXEC p_showActDateTime 'before tabs '
                -- create tabs for the object:
                EXEC    p_Object$createTabs @ai_userId, @ai_op,
                            @ao_oid, @ai_tVersionId
--EXEC p_showActDateTime 'after tabs  '
                --
                -- insert protocol entry
                --
                -- gather missing information for protocol-entry
                SELECT  @l_fullName = fullname
                FROM    ibs_user
                WHERE   id = @ai_userId

                -- add the new tuple to the ibs_Object table:
                INSERT INTO ibs_Protocol_01
                       (fullName, userId, oid, objectName, icon, tVersionId,
                        containerId, containerKind, owner, action, actionDate)
                VALUES (@l_fullName, @ai_userId, @ao_oid, @l_name, @l_icon,
                        @ai_tVersionId, @l_containerId, @ai_containerKind,
                        @ai_userId, @ai_op, getDate ())
--EXEC p_showActDateTime 'after protocol'
            END -- if object is independent

            -- done!
            SELECT  @l_retValue = @c_ALL_RIGHT

       END -- if the user has the rights
       ELSE                                -- the user does not have the rights
             SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
   END -- else the object is not cheked out or the user is the one who cheked the object out

   -- return the state value
   RETURN  @l_retValue
GO
-- p_Object$performCreate


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for creating a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   @oid                OID of the object to be created.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              String representation of OID of the newly
 *                              created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performCreateWithId'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performCreateWithId

(
    -- input parameters:
    @oid            OBJECTID,
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @l_containerOid2 OBJECTID
    DECLARE @linkedObjectId OBJECTID
    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    DECLARE @rights RIGHTS                  -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    DECLARE @id INT


    -- get rights for this user
    EXEC @rights = p_Rights$checkRights
        @oid,                           -- given object to be accessed by user
        @containerId,                   -- container of given object
        @userId,                        -- user_id
        @op,                            -- required rights user must have to
                                        -- insert/update object
        @rights OUTPUT                  -- returned value

    -- check if the user has the necessary rights
    IF (@rights = @op)                  -- the user has the rights?
    BEGIN
        -- Dont't set the id because this can lead to inconsistencies!
--        SELECT  @id = CONVERT (INT, SUBSTRING (@oid, 5, 4))
        SELECT  @id = 0

        -- get containerId of container:
        IF (@containerId <> 0x0000000000000000)
        BEGIN
            SELECT  @l_containerOid2 = containerId
            FROM    ibs_Object
            WHERE   oid = @containerId
        END -- if
        ELSE
        BEGIN
            SELECT @l_containerOid2 = 0x0000000000000000
        END -- else

        INSERT INTO ibs_Object
               (id, oid, tVersionId, name,
                containerId, containerKind, containerOid2,
                isLink, linkedObjectId, owner, creator, changer,
                validUntil, description)
        VALUES (@id, @oid, @tVersionId, @name,
                @containerId, @containerKind, @l_containerOid2,
                @isLink, @linkedObjectId, @userId, @userId, @userId,
                DATEADD (month, 3, getDate ()), @description)

        -- convert oid to oid_s:
        EXEC    p_byteToString @oid, @oid_s OUTPUT
    END -- if the user has the rights
    ELSE                                -- the user does not have the rights
    BEGIN
        SELECT  @retValue = @INSUFFICIENT_RIGHTS
    END -- else the user does not have the rights

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$performCreateWithId


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    BEGIN TRANSACTION
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description,
                @oid_s OUTPUT

/*
        IF (@retValue = @ALL_RIGHT)     -- no error occurred?
        BEGIN

        END -- if no error occurred
*/
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$create



/******************************************************************************
 * Stores the attributes of an existing object (incl. rights check). <BR>
 * Creates the object if not yet existing.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$store'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$store
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION
)
AS
    -- convertions: (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @containerId OBJECTID       -- id of container where the object
                                        -- resides
    SELECT  @containerId = 0x0000000000000000


    -- get container id of object:
    SELECT  @containerId = containerId
    FROM    ibs_Object
    WHERE   oid = @oid

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN

        -- get rights for this user
        EXEC @rights = p_Rights$checkRights
            @oid,                       -- given object to be accessed by user
            @containerId,               -- container of given object
            @userId,                    -- user id
            @op,                        -- required rights user must have to
                                        -- update object
            @rights OUTPUT              -- returned value

        -- check if the user has the necessary rights
        IF (@rights = @op)              -- the user has the rights?
        BEGIN
            BEGIN TRANSACTION

            -- update
            UPDATE  ibs_Object
            SET     name = @name,
                    lastChanged = getDate (),
                    validUntil = @validUntil,
                    description = @description
            WHERE   oid = @oid

            COMMIT TRANSACTION
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- set the return value with the error code:
            SELECT  @retValue = @INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$store



/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for changing a business object.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_showInNews       Display the object in the news.
 *
 * @output parameters:
 * [@param   @oid]              Oid of the changed object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performChange'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performChange
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    @ao_oid                 OBJECTID = 0x0000000000000000 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_ST_ACTIVE            INT,            -- active state
    @c_ST_CREATED           INT,            -- created state
    @c_ST_DELETED           INT,            -- deleted state
    @c_INNEWS               INT,            -- bit value for showInNews

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_count                INT,            -- counter
    @l_rKey                 ID,
    @l_rights               RIGHTS,         -- the current rights
    @l_coUserId             USERID,         -- user who checked the object out
    @l_fullName             NAME,           -- full name of user who changed the
                                            -- object
    @l_tVersionId           TVERSIONID,     -- tVersionId of object
    @l_containerId          OBJECTID,       -- oid of container
    @l_containerKind        INT,            -- kind of object/container
                                            -- relationship
    @l_icon                 NAME,           -- the icon of the object
    @l_owner                USERID,         -- the owner of the object
    @l_state                STATE,          -- the state of the object
    @l_flags                INT,            -- flags of the object
    @l_updateTabs           BOOL

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOOID                = 0x0000000000000000,
    @c_ST_ACTIVE            = 2,
    @c_ST_CREATED           = 4,
    @c_INNEWS               = 4

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_count = 0,
    @l_rights = 0,
    @l_coUserId = 0,
    @l_containerId = @c_NOOID,
    @l_flags = 0,
    @l_updateTabs = 0


-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Object$performChange

    -- conversions: (OBJECTIDSTRING) - all input object ids must be converted
    EXEC p_stringToByte @ai_oid_s, @ao_oid OUTPUT

    -- get the data of the object:
    SELECT  @l_containerId = o.containerId, @l_containerKind = o.containerKind,
            @l_tVersionId = o.tVersionId, @l_icon = o.icon, @l_owner = o.owner,
            @l_state = o.state, @l_flags = o.flags, @l_rKey = o.rKey,
            @l_coUserId = co.userId
    FROM    ibs_Object o LEFT OUTER JOIN ibs_Checkout_01 co ON o.oid = co.oid
    WHERE   o.oid = @ao_oid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        'get the data of the object', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- check if the object exists:
    IF (@l_count > 0)                   -- object exists?
    BEGIN

        -- check if object is checked out and current user is allowed to access the object:
        IF (@l_coUserId IS NOT NULL) AND (@l_coUserId <> @ai_userId)
        BEGIN
            -- set corresponding return value:
            SELECT @l_retValue = @c_INSUFFICIENT_RIGHTS
            GOTO exception                  -- call common exception handler
        END -- if

        -- get rights for this user:
        EXEC @l_rights = p_Rights$checkRKeyRights
             @l_rKey,                   -- rights key to be checked
             @ai_userId,                -- user id
             @ai_op,                    -- required rights user must have to
                                        -- update object
             @l_rights OUTPUT           -- returned value
/*
        -- get rights for this user:
        EXEC @l_rights = p_Rights$checkRights
             @ao_oid,                   -- given object to be accessed by user
             @l_containerId,            -- container of given object
             @ai_userId,                -- user id
             @ai_op,                    -- required rights user must have to
                                        -- update object
             @l_rights OUTPUT           -- returned value
*/

        -- check if the user has the necessary rights:
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            -- check if the object is a tab:
            IF (@l_containerKind = 2)   -- the object is a tab?
            BEGIN
                -- get the state of the container:
                SELECT  @l_state = state
                FROM    ibs_Object
                WHERE   oid = @l_containerId

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                    'get the state of the container',
                    @l_ePos OUTPUT, @l_count OUTPUT
                IF (@l_error <> 0 OR @l_count = 0) -- an error occurred?
                    GOTO exception      -- call common exception handler
            END -- if the object is a tab
            ELSE IF (@l_state = @c_ST_CREATED) -- object was just created?
            BEGIN
                -- set object to active:
                SELECT  @l_state = @c_ST_ACTIVE
                SELECT  @l_updateTabs = 1
            END -- else if object was just created

            -- set the showInNews flag:
            IF (@ai_showInNews = 1) -- object shall be shown in news?
            BEGIN
                -- set the showInNews bit:
        		SELECT @l_flags = (@l_flags | @c_INNEWS)
            END -- if object shall be shown in news
            ELSE                    -- object shall not be shown in news
            BEGIN
                -- drop the showInNews bit:
		        SELECT @l_flags = (@l_flags & (0x7FFFFFFF ^ @c_INNEWS))
            END -- else object shall not be shown in news

            -- store the values of the object:
            UPDATE  ibs_Object
            SET     name = @ai_name,
                    validUntil = @ai_validUntil,
                    description = @ai_description,
                    lastChanged = getDate (),
                    state = @l_state,
                    changer = @ai_userId,
                    flags = @l_flags
            WHERE   oid = @ao_oid

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                'store the values', @l_ePos OUTPUT
            IF (@l_error <> 0)      -- an error occurred?
                GOTO exception      -- call exception handler

            -- ensure that the tab objects have the correct state:
            -- do an update only in case of a change
            -- of the state from CREATED to ACTIVE
            -- and when the object itself is not a tab object
            IF (@l_updateTabs = 1)
            BEGIN
                UPDATE  ibs_Object
                SET     state = @l_state
                WHERE   containerId = @ao_oid
                    AND containerKind = 2
                    AND state <> @l_state
                    AND state IN (@c_ST_ACTIVE, @c_ST_CREATED)
            END -- if tab objects shall be updated

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                'set states of tab objects', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler

            -- get the full name of the user:
            SELECT  @l_fullName = fullname
            FROM    ibs_User
            WHERE   id = @ai_userId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
                'get the full name of the user', @l_ePos OUTPUT, @l_count OUTPUT
            IF (@l_error <> 0 OR @l_count = 0) -- an error occurred?
                GOTO exception          -- call exception handler

            -- create protocol entry:
            INSERT INTO ibs_Protocol_01
                    (fullName, userId, oid, objectName, icon,
                    tVersionId, containerId, containerKind,
                    owner, action, actionDate)
            VALUES  (@l_fullName, @ai_userId, @ao_oid, @ai_name, @l_icon,
                    @l_tVersionId, @l_containerId, @l_containerKind,
                    @l_owner, @ai_op, getDate ())

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                'create protocol entry', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call exception handler
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- set the return value with the error code:
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists
    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Object$performChange
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Object$performChange', @l_error, @l_ePos,
        'ai_userId', @ai_userId,
        'ai_oid_s', @ai_oid_s,
        'ai_op', @ai_op,
        'ai_name', @ai_name,
        'ai_showInNews', @ai_showInNews,
        'ai_description', @ai_description
    -- set error code:
    IF (@l_retValue = @c_ALL_RIGHT)     -- no error code set?
        SELECT  @l_retValue = @c_NOT_OK
    -- return error code:
    RETURN  @l_retValue
GO
-- p_Object$performChange



/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         Should the currrent object displayed in the news.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$change



/******************************************************************************
 * Move an existing object to another container (incl. rights check). <BR>
 * All sub structures are moved with the object.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be moved.
 * @param   @userId             ID of the user who is moving the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @containerId_s      ID of the container where object shall be
 *                              moved to.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$move'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$move
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @containerId_s  OBJECTIDSTRING
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID, @containerId OBJECTID, @l_containerOid2 OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT
    EXEC p_stringToByte @containerId_s, @containerId OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @oldContainerId OBJECTID    -- id of container where the object
                                        -- resides
    DECLARE @CUT_FAIL_ERROR INT, @Flag INT    -- used to signal a paste fail
    SELECT  @CUT_FAIL_ERROR = 11, @Flag = 0

    DECLARE @posNoPath POSNOPATH_VC
    DECLARE @posNoPathTarget POSNOPATH_VC

    SELECT  @oldContainerId = 0x0000000000000000

    DECLARE @CHECKEDOUT INT
    SELECT @CHECKEDOUT = 16
    -- declare local variables
    DECLARE @co_userId USERID
    DECLARE @check INTEGER
    SELECT @check = 0

    DECLARE @l_tVersionId TVERSIONID

    -- is the object checked out?
    SELECT @co_userId = co.userId, @check = o.flags & @CHECKEDOUT
    FROM ibs_Object o JOIN ibs_Checkout_01 co ON o.oid = co.oid
    WHERE o.oid = @oid

    IF (@check=@CHECKEDOUT) AND (@co_userId<>@userId)
        SELECT @retValue = @INSUFFICIENT_RIGHTS
    ELSE
    BEGIN

        -- get the actual container id of object:
        SELECT  @oldContainerId = containerId
        FROM    ibs_Object
        WHERE   oid = @oid

        -- check if the object exists:
        IF (@@ROWCOUNT > 0)                 -- object exists?
        BEGIN
            -- get rights for this user
            EXEC @rights = p_Rights$checkRights
                @oid,                       -- given object to be accessed by user
                @oldContainerId,            -- container of given object
                @userId,                    -- user id
                @op,                        -- required rights user must have to
                                            -- update object
                @rights OUTPUT              -- returned value


            -- check if the user has the necessary rights
            IF (@rights = @op)              -- the user has the rights?
            BEGIN
                BEGIN TRANSACTION
                    SELECT @posNoPath = posNoPath
                    FROM ibs_Object
                    WHERE oid = @oid

                    SELECT  @l_containerOid2 = containerId,
                            @posNoPathTarget = posNoPath
                    FROM    ibs_Object
                    WHERE   oid = @containerId

                    IF  (CHARINDEX (@posNoPath, @posNoPathTarget) <> 1)
                    BEGIN
                        -- update
                        UPDATE  ibs_Object
                        SET     containerId = @containerId,
                                containerOid2 = @l_containerOid2
                        WHERE   oid = @oid

-- ****************************************************************************
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******

                        -- Ist leider notwendig, da auf den Sonderfall eines
                        -- Attachments keine gesonderte abfrage erfolgt und
                        -- so die Icons nicht gelöscht (Infoicon bei altem
                        -- Dokument) bzw. doppelt gesetz (MasterIcon bei neuem
                        -- Dokument) werden.

                        -- get the tVerionId of the object
                        SELECT  @l_tVersionId = tVersionId
                        FROM    ibs_Object
                        WHERE   oid = @oid

                        -- ensures that the flags and a master are set
                        IF (@l_tVersionId = 0x01010051)
                        BEGIN
                            -- ensures that the old attachment owner has the
                            -- right flags set
                            EXEC p_Attachment_01$ensureMaster @oldContainerId,
                                        null
                            -- ensures that the new attachment owner has the
                            -- right flags set
                            EXEC p_Attachment_01$ensureMaster @containerId,
                                        null
                        END

-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ***** H A C K ***** H A C K ***** H A C K ***** H A C K ***** H A C K ******
-- ****************************************************************************

                    END
                    ELSE
                        SELECT @Flag = 1
                COMMIT TRANSACTION
            END -- if the user has the rights
            ELSE                            -- the user does not have the rights
            BEGIN
                -- set the return value with the error code:
                SELECT  @retValue = @INSUFFICIENT_RIGHTS
            END -- else the user does not have the rights
        END -- if object exists

        ELSE                                -- the object does not exist
        BEGIN
            -- set the return value with the error code:
            SELECT  @retValue = @OBJECTNOTFOUND
        END -- else the object does not exist
    END

    -- return the state value
    IF (@Flag <> 1 ) RETURN  @retValue
    ELSE  RETURN @CUT_FAIL_ERROR
GO
-- p_Object$move


/******************************************************************************
 * Move an existing object to another container. <BR>
 * All sub structures are moved with the object.
 *
 * @input parameters:
 * @param   @oid                ID of the object to be moved.
 * @param   @targetId           ID of the container where object shall be
 *                              moved to.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performMove'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performMove
(
    -- input parameters:
    @ai_oid                 OBJECTID,
    @ai_targetId            OBJECTID
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_containerOid2        OBJECTID        -- containerId of container

    -- get containerId of container:
    SELECT  @l_containerOid2 = c.containerId
    FROM    ibs_Object c, ibs_Object o
    WHERE   c.oid = o.containerId
        AND o.oid = @ai_oid

    -- set the new containerId of the object:
    -- (the rest does the trigger)
    UPDATE  ibs_Object
    SET     containerId = @ai_targetId,
            containerOid2 = @l_containerOid2
    WHERE   oid = @ai_oid
GO
-- p_Object$performMove



/******************************************************************************
 * Change the state of an existing object. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @state              The new state of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$changeState'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$changeState
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_state               STATE
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_ST_ACTIVE            INT,            -- active state
    @c_ST_CREATED           INT,            -- created state

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_oid                  OBJECTID,
    @l_containerId          OBJECTID,       -- id of container where the object
                                            -- resides
    @l_oldState             STATE,          -- actual state of the object
    @l_rights               RIGHTS          -- return value of rights proc.

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOOID                = 0x0000000000000000,
    @c_ST_ACTIVE            = 2,
    @c_ST_CREATED           = 4

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_containerId = @c_NOOID,
    @l_oldState = 0,
    @l_rights = 0

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

    -- get the actual container id and state of object:
    SELECT  @l_containerId = containerId, @l_oldState = state
    FROM    ibs_Object
    WHERE   oid = @l_oid


    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- check if the state shall be changed:
        IF (@ai_state <> @l_oldState)   -- state shall be changed?
        BEGIN
            -- get rights for this user
            EXEC @l_rights = p_Rights$checkRights
                @l_oid,                 -- given object to be accessed by user
                @l_containerId,         -- container of given object
                @ai_userId,             -- user id
                @ai_op,                 -- required rights user must have to
                                        -- update object
                @l_rights OUTPUT        -- returned value

            -- check if the user has the necessary rights
            IF (@l_rights = @ai_op)     -- the user has the rights?
            BEGIN
                -- check if the state transition from the actual state to the
                -- new state is allowed:
                -- not implemented yet

                BEGIN TRANSACTION

                    -- set the new state for the object and all tabs:
                    UPDATE  ibs_Object
                    SET     state = @ai_state
                    WHERE   oid = @l_oid

                    UPDATE  ibs_Object
                    SET     state = @ai_state
                    WHERE   containerId = @l_oid
                        AND containerKind = 2
                        AND (state IN (@c_ST_ACTIVE, @c_ST_CREATED))
--                        AND state <> @state

                COMMIT TRANSACTION
            END -- if the user has the rights
            ELSE                            -- the user does not have the rights
            BEGIN
                -- set the return value with the error code:
                SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
            END -- else the user does not have the rights
        END -- if state shall be changed
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Object$changeState


/******************************************************************************
 * Change the processState of an existing object. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @processState       The new process state of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$changeProcessState'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$changeProcessState
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING, -- Objectid - String
    @userId         USERID,         -- UserId
    @op             INT,            -- Operation
    @processState   INT             -- new value for processState
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @containerId OBJECTID,      -- id of container where the object
                                        -- resides
            @oldProcState INT           -- actual state of the object
    SELECT  @containerId = 0x0000000000000000, @oldProcState = 0


    -- get the actual container id and state of object:
    SELECT  @containerId = containerId, @oldProcState = processState
    FROM    ibs_Object
    WHERE   oid = @oid


    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- check if the state transition from the actual state to the new
        -- state is allowed:
        -- not implemented yet

        BEGIN TRANSACTION
            -- update
            UPDATE  ibs_Object
            SET     processState = @processState
            WHERE   oid = @oid
        COMMIT TRANSACTION

        -- insert for protocolcontainer
        DECLARE @fullName NAME
        DECLARE @icon NAME
        DECLARE @name NAME
        DECLARE @tVersionId TVERSIONID
        DECLARE @containerKind INT

        -- read attributes needed for protocol out of table ibs_Object
        SELECT @name = name, @tVersionId = tVersionId, @containerId = containerId,
               @containerKind = containerKind, @icon = icon
        FROM   ibs_Object
        WHERE  oid = @oid

        -- read out the  fullName of the User
        SELECT  @fullName = u.fullname
        FROM    ibs_user u
        WHERE   u.id = @userId

        -- add the new tuple to the ibs_Object table:
        INSERT INTO ibs_Protocol_01
               ( fullName, userId, oid, objectName, icon, tVersionId,
                 containerId, containerKind, owner, action, actionDate)

        VALUES (@fullName, @userId, @oid, @name, @icon, @tVersionId,
                @containerId, @containerKind, @userId, @op, getDate ())

    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$changeProcessState



/******************************************************************************
 * Change the owner of an existing object, including owner-information of
 * subsequent objects. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @owner              The new owner of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$changeOwnerRec'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$changeOwnerRec
(
    -- input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_owner          USERID
)
AS
    -- posNoPath of given object
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_oid                  OBJECTID,       -- oid of root object
    @l_rights               RIGHTS,         -- return value of righs procedure
    @l_oLevel               INT,            -- oLevel of actual object
    @l_posNoPath            POSNOPATH_VC,   -- posnopath of object
    @l_rKey                 ID,             -- the object's rights key
    @l_newRKey              ID              -- the new rights key for the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rights = 0


-- body:
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

    BEGIN TRANSACTION
    -- get the actual path info and owner of object:
    SELECT  @l_rKey = rKey, @l_posNoPath = posNoPath, @l_oLevel = oLevel
    FROM    ibs_Object
    WHERE   oid = @l_oid
/*
    SELECT  @l_containerId = containerId, @l_oldOwner = owner
    FROM    ibs_Object
    WHERE   oid = @l_oid
*/

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        IF (@ai_op <> 0)
        BEGIN
            -- get rights for this user
            EXEC @l_rights = p_Rights$checkRKeyRights
                @l_rKey,                -- given rKey
                @ai_userId,                -- user id
                @ai_op,                    -- required rights user must have to
                                        -- update object
                @l_rights OUTPUT          -- returned value
/*
            EXEC @l_rights = p_Rights$checkRights
                @l_oid,                   -- given object to be accessed by user
                @l_containerId,           -- container of given object
                @ai_userId,                -- user id
                @ai_op,                    -- required rights user must have to
                                        -- update object
                @l_rights OUTPUT          -- returned value
*/
        END -- if

        -- check if the user has the necessary rights
        IF (@l_rights = @ai_op)              -- the user has the rights?
        BEGIN
            -- define cursor:
            -- run through trigger for creating all non-existing rKeys:
            -- find all object rKeys which have no sibling with the required
            -- owner
-- KR 20050304: Use FAST_FORWARD (FORWARD_ONLY, READ_ONLY) to improve
--              performance
--            DECLARE updateCursor CURSOR FAST_FORWARD FOR
            DECLARE updateCursor CURSOR FOR
                SELECT  DISTINCT rKey
                FROM    ibs_Object
                WHERE   rKey NOT IN
                        (
                            SELECT  rk2.id
                            FROM    ibs_RightsKey rk1, ibs_RightsKey rk2
                            WHERE   rk1.owner = @ai_owner
                                AND rk1.rKeysId = rk2.rKeysId
                        )
                    AND owner <> @ai_owner
                    AND oLevel >= @l_oLevel
                    AND posNoPath LIKE @l_posNoPath + '%'

            -- open the cursor:
            OPEN    updateCursor

            -- get the first object:
            FETCH NEXT FROM updateCursor INTO @l_rKey

            -- loop through all objects:
            WHILE (@l_retValue = @c_ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                            -- another object found?
            BEGIN
                -- Because @@FETCH_STATUS may have one of the three values
                -- -2, -1, or 0 all of these cases must be checked.
                -- In this case the tuple is skipped if it was deleted during
                -- the execution of this procedure.
                IF (@@FETCH_STATUS <> -2)
                BEGIN
                    -- get the key containing the resulting rights:
                    -- (the key is implicitely created)
                    EXEC p_Rights$getKeyForOwner @l_rKey, @ai_owner,
                                                 @l_newRKey OUTPUT

                    -- check if there was an error:
                    IF (@l_newRKey = -1)        -- an error occurred?
                        SELECT  @l_retValue = @c_NOT_OK -- set return value
                END -- if
                -- get next tuple:
                FETCH NEXT FROM updateCursor INTO @l_rKey
            END -- while another tuple found

            -- close the not longer needed cursor:
            CLOSE updateCursor
            DEALLOCATE updateCursor

/*
            BEGIN TRANSACTION
                -- change the owner of the object
                UPDATE  ibs_Object
                SET     owner = @ai_owner
                WHERE   oid = @l_oid

                -- change owner of subsequent objects
                -- first get the posnopath information of the object
                SELECT  @l_posNoPath = posNoPath
                FROM    ibs_object
                WHERE   oid = @l_oid
*/

            -- now change every object with coinciding posnopath-prefix
            UPDATE  ibs_Object
            SET     owner = @ai_owner,
                    rKey =
                    (
                        SELECT  rk1.id
                        FROM    ibs_RightsKey rk1, ibs_RightsKey rk2
                        WHERE   rk1.owner = @ai_owner
                            AND rk2.id = rKey
                            AND rk1.rKeysId = rk2.rKeysId
                    )
            WHERE   @ai_owner <> owner
--                AND oLevel >= @l_oLevel
                AND posNoPath LIKE @l_posNoPath + '%'

/*
            COMMIT TRANSACTION
*/
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- set the return value with the error code:
            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- else the object does not exist
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Object$changeOwnerRec

/******************************************************************************
 * Change the owner of the tabs of an existing object. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @owner              The new owner of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$changeTabsOwner'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$changeTabsOwner
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @owner          USERID
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @containerId OBJECTID,      -- id of container where the object
                                        -- resides
            @oldOwner STATE             -- actual owner of the object
    SELECT  @containerId = 0x0000000000000000, @oldOwner = 0x00000000


    -- get the actual container id and owner of object:
    SELECT  @containerId = containerId, @oldOwner = owner
    FROM    ibs_Object
    WHERE   oid = @oid

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC @rights = p_Rights$checkRights
            @oid,                       -- given object to be accessed by user
            @containerId,               -- container of given object
            @userId,                    -- user id
            @op,                        -- required rights user must have to
                                        -- update object
            @rights OUTPUT              -- returned value

        -- check if the user has the necessary rights
        IF (@rights = @op)              -- the user has the rights?
        BEGIN

            BEGIN TRANSACTION
                -- update
                UPDATE  ibs_Object
                SET     owner = @owner
                WHERE   containerId = @oid
                AND     containerKind = 2   -- kind = 2 -> Tab
            COMMIT TRANSACTION
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- set the return value with the error code:
            SELECT  @retValue = @INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$changeTabsOwner

/******************************************************************************
 * Insert protocol entry for object. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object for that a protocol entry must
 *                              be inserted.
 * @param   @userId             ID of the user who is inserting.
 * @param   @op                 Operation to be performed.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$insertProtocol'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$insertProtocol
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @containerId OBJECTID       -- id of container where the object
                                        -- resides
    SELECT  @containerId = 0x0000000000000000


    BEGIN TRANSACTION
        -- insert for protocolcontainer
        DECLARE @fullName NAME
        DECLARE @icon NAME
        DECLARE @name NAME
        DECLARE @tVersionId TVERSIONID
        DECLARE @containerKind INT

        -- read out the  fullName of the User
        SELECT  @fullName = u.fullname, @icon = o.icon, @name = o.name,
                @tVersionId = o.tVersionId, @containerId = o.containerId,
                @containerKind = o.containerKind
        FROM    ibs_user u, ibs_object o
        WHERE   u.id = @userId
        AND     o.oid = @oid

        -- add the new tuple to the ibs_Protocol_01 table:
        INSERT INTO ibs_Protocol_01
               ( fullName, userId, oid, objectName, icon, tVersionId,
                 containerId, containerKind, owner, action, actionDate)

        VALUES (@fullName, @userId, @oid, @name, @icon, @tVersionId,
                @containerId, @containerKind, @userId, @op, getDate ())

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$insertProtocol

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for retrieving the data of a business object.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             ID of the user who is retrieving the object.
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
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         Display object in the news?
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * [@param   @oid]              Oid of the retrieved object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 *
 * @deprecated  This procedure is never used except p_Object$retrieve2.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performRetrieve2'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performRetrieve2
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
    @state          STATE           OUTPUT,
    @tVersionId     TVERSIONID      OUTPUT,
    @typeName       NAME            OUTPUT,
    @name           NAME            OUTPUT,
    @containerId    OBJECTID        OUTPUT,
    @containerName  NAME            OUTPUT,
    @containerKind  INT             OUTPUT,
    @isLink         BOOL            OUTPUT,
    @linkedObjectId OBJECTID        OUTPUT,
    @owner          USERID          OUTPUT,
    @ownerName      NAME            OUTPUT,
    @creationDate   DATETIME        OUTPUT,
    @creator        USERID          OUTPUT,
    @creatorName    NAME            OUTPUT,
    @lastChanged    DATETIME        OUTPUT,
    @changer        USERID          OUTPUT,
    @changerName    NAME            OUTPUT,
    @validUntil     DATETIME        OUTPUT,
    @description    DESCRIPTION     OUTPUT,
    @showInNews     BOOL            OUTPUT,
    @checkedOut     BOOL            OUTPUT,
    @checkOutDate   DATETIME        OUTPUT,
    @checkOutUser   USERID          OUTPUT,
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT,
    @icon           NAME            OUTPUT,
    @oid            OBJECTID = 0x0000000000000000 OUTPUT
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_READ RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_READ = 2                             -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of called procedure
    DECLARE @INNEWS INT                 -- Constant for showInNews flags
    DECLARE @ISCHECKEDOUT INT
    DECLARE @tempName NAME
    DECLARE @tempOid OBJECTID
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0, @INNEWS = 4


    -- get container id of object
    SELECT  @containerId = containerId
    FROM    ibs_Object
    WHERE   oid = @oid

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC p_Rights$checkRights
             @oid,                      -- given object to be accessed by user
             @containerId,              -- container of given object
             @userId,                   -- user_id
             @op,                       -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @rights OUTPUT             -- returned value

        -- check if the user has the necessary rights
        IF (@rights = @op)              -- the user has the rights?
        BEGIN
            -- get the data of the object and return it
            SELECT  @state = o.state, @tVersionId = o.tVersionId,
                    @typeName = o.typeName, @name = o.name,
                    @containerId = o.containerId,
                    @containerName = c.name,
                    @containerKind = o.containerKind, @isLink = o.isLink,
                    @linkedObjectId = o.linkedObjectId,
                    @owner = o.owner, @ownerName = own.fullname,
                    @creationDate = o.creationDate, @creator = o.creator,
                    @creatorName = cr.fullname,
                    @lastChanged = o.lastChanged, @changer = o.changer,
                    @changerName = ch.fullname,
                    @validUntil = o.validUntil, @description = o.description,
                    @showInNews = (o.flags & @INNEWS),
                    @checkedOut = (o.flags & @ISCHECKEDOUT),
                    @icon = o.icon
            FROM    ibs_Object o LEFT JOIN ibs_Object c ON o.containerId = c.oid
                        LEFT JOIN ibs_User own ON o.owner = own.id
                        LEFT JOIN ibs_User cr ON o.creator = cr.id
                        LEFT JOIN ibs_User ch ON o.changer = ch.id
            WHERE   o.oid = @oid

            IF (@checkedOut=1)
            BEGIN
                -- get the info who checked out the object
                SELECT  @checkOutDate = ch.checkout,
                        @checkOutUser = ch.userId,
                        @tempOid = u.oid,
                        @tempName = u.fullname
                FROM    ibs_CheckOut_01 ch LEFT JOIN ibs_User u ON u.id = ch.userid
                WHERE   ch.oid = @oid

                -- rights set for viewing the User?
                EXEC p_Rights$checkRights
                    @tempOid,                  -- given object to be accessed by user
                    0x0000000000000000,        -- containeroid - no oid in this case cause irrelevant
                    @userId,                   -- user_id
                    2,                         -- required rights user must have to
                                               -- view object (op. to be
                                               -- performed)
                    @rights OUTPUT             -- returned value

                -- check if the user has the necessary rights
                IF (@rights = 2)               -- the user has the rights?
                BEGIN
                    SELECT @checkOutUserName = @tempName
                END -- if the user has the rights to see the user who checked out the object

                -- rights set for reading the User?
                EXEC p_Rights$checkRights
                    @tempOid,                  -- given object to be accessed by user
                    0x0000000000000000,        -- containeroid - no oid in this case cause irrelevant
                    @userId,                   -- user_id
                    4,                         -- required rights user must have to
                                               -- retrieve object (op. to be
                                               -- performed)
                    @rights OUTPUT             -- returned value

                -- check if the user has the necessary rights
                IF (@rights = 4)               -- the user has the rights?
                BEGIN
                    SELECT @checkOutUserName = @tempName
                    SELECT @checkOutUserOid = @tempOid
                END -- if the user has the rights to read the user who checked out the object

            END -- if the object is checked out

            -- set object as already read:
            EXEC    p_setRead @oid, @userId
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- get the default data of the object and return it
            SELECT  @state = 0, @tVersionId = 0x0,
                    @typeName = '', @name = '',
                    @containerId = 0x0,
                    @containerKind = 0, @isLink = 0,
                    @linkedObjectId = 0x0,
                    @owner = 0x0, @ownerName = '',
                    @creationDate = getDate (), @creator = 0x0,
                    @creatorName = '',
                    @lastChanged = getDate (), @changer = 0x0,
                    @changerName = '',
                    @validUntil = getDate (), @description = '',
                    @showInNews = 0, @icon = null

            SELECT  @retValue = @INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$performRetrieve2


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for retrieving the data of a business object.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be retrieved.
 * @param   ai_userId           ID of the user who is retrieving the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerName    Name of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Display object in the news?
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out
 * @param   ao_checkOutUser     id of the user which checked out the object
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * [@param   ao_oid]            Oid of the retrieved object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performRetrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performRetrieve
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- output parameters:
    @ao_state               STATE           OUTPUT,
    @ao_tVersionId          TVERSIONID      OUTPUT,
    @ao_typeName            NAME            OUTPUT,
    @ao_name                NAME            OUTPUT,
    @ao_containerId         OBJECTID        OUTPUT,
    @ao_containerName       NAME            OUTPUT,
    @ao_containerKind       INT             OUTPUT,
    @ao_isLink              BOOL            OUTPUT,
    @ao_linkedObjectId      OBJECTID        OUTPUT,
    @ao_owner               USERID          OUTPUT,
    @ao_ownerName           NAME            OUTPUT,
    @ao_creationDate        DATETIME        OUTPUT,
    @ao_creator             USERID          OUTPUT,
    @ao_creatorName         NAME            OUTPUT,
    @ao_lastChanged         DATETIME        OUTPUT,
    @ao_changer             USERID          OUTPUT,
    @ao_changerName         NAME            OUTPUT,
    @ao_validUntil          DATETIME        OUTPUT,
    @ao_description         DESCRIPTION     OUTPUT,
    @ao_showInNews          BOOL            OUTPUT,
    @ao_checkedOut          BOOL            OUTPUT,
    @ao_checkOutDate        DATETIME        OUTPUT,
    @ao_checkOutUser        USERID          OUTPUT,
    @ao_checkOutUserOid     OBJECTID        OUTPUT,
    @ao_checkOutUserName    NAME            OUTPUT,
    @ao_oid                 OBJECTID = 0x0000000000000000 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_RIGHT_VIEW           RIGHTS,         -- right for viewing
    @c_RIGHT_READ           RIGHTS,         -- right for reading
    @c_INNEWS               INT,            -- bit value for showInNews
    @c_ISCHECKEDOUT         INT,            -- bit value for check out state

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_rights               RIGHTS,         -- the current rights
    @l_icon                 NAME,           -- the name of the icon
                                            -- (must be an output parameter!)
    @l_tempName             NAME,           -- temporary name
    @l_tempOid              OBJECTID        -- temporary oid


    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOOID                = 0x0000000000000000,
    @c_RIGHT_VIEW           = 2,
    @c_RIGHT_READ           = 4,
    @c_INNEWS               = 4,
    @c_ISCHECKEDOUT         = 16

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0,
    @l_rights = 0

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @ai_oid_s, @ao_oid OUTPUT

    -- get container id of object:
    SELECT  @ao_containerId = containerId
    FROM    ibs_Object
    WHERE   oid = @ao_oid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        'get containerId', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- check if the object exists:
    IF (@l_rowCount > 0)                -- object exists?
    BEGIN
        -- get rights for this user
        EXEC p_Rights$checkRights
             @ao_oid,                   -- given object to be accessed by user
             @ao_containerId,           -- container of given object
             @ai_userId,                -- user_id
             @ai_op,                    -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
             @l_rights OUTPUT           -- returned value

        -- check if the user has the necessary rights:
        IF (@l_rights = @ai_op)         -- the user has the rights?
        BEGIN
            -- get the data of the object:
            SELECT  @ao_state = o.state, @ao_tVersionId = o.tVersionId,
                    @ao_typeName = o.typeName, @ao_name = o.name,
                    @ao_containerId = o.containerId,
                    @ao_containerName = c.name,
                    @ao_containerKind = o.containerKind, @ao_isLink = o.isLink,
                    @ao_linkedObjectId = o.linkedObjectId,
                    @ao_owner = o.owner, @ao_ownerName = own.fullname,
                    @ao_creationDate = o.creationDate, @ao_creator = o.creator,
                    @ao_creatorName = cr.fullname,
                    @ao_lastChanged = o.lastChanged, @ao_changer = o.changer,
                    @ao_changerName = ch.fullname,
                    @ao_validUntil = o.validUntil,
                    @ao_description = o.description,
                    @l_icon = o.icon,
                    @ao_showInNews = (o.flags & @c_INNEWS),
                    @ao_checkedOut = (o.flags & @c_ISCHECKEDOUT)
            FROM    ibs_Object o LEFT JOIN ibs_Object c ON o.containerId = c.oid
                        LEFT JOIN ibs_User own ON o.owner = own.id
                        LEFT JOIN ibs_User cr ON o.creator = cr.id
                        LEFT JOIN ibs_User ch ON o.changer = ch.id
            WHERE   o.oid = @ao_oid

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                'get the data of the object', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

            IF (@ao_checkedOut = 1)     -- the object is checked out?
            BEGIN
                -- get the info who checked out the object
                SELECT  @ao_checkOutDate = ch.checkout,
                        @ao_checkOutUser = ch.userId,
                        @l_tempOid = u.oid,
                        @l_tempName = u.fullname
                FROM    ibs_CheckOut_01 ch LEFT JOIN ibs_User u
                        ON u.id = ch.userid
                WHERE   ch.oid = @ao_oid

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    'get the check out info', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call common exception handler

                -- rights set for viewing the User?
                EXEC p_Rights$checkRights
                    @l_tempOid,         -- given object to be accessed by user
                    @c_NOOID,           -- containeroid - no oid in this case
                                        -- cause irrelevant
                    @ai_userId,         -- user_id
                    @c_RIGHT_VIEW,      -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
                    @l_rights OUTPUT    -- returned value

                -- check if the user has the necessary rights
                -- to see the user who checked out the object:
                IF (@l_rights = @c_RIGHT_VIEW) -- the user has the rights?
                BEGIN
                    SELECT @ao_checkOutUserName = @l_tempName
                END -- if the user has the rights

                -- rights set for reading the User?
                EXEC p_Rights$checkRights
                    @l_tempOid,         -- given object to be accessed by user
                    @c_NOOID,           -- containeroid - no oid in this case
                                        -- cause irrelevant
                    @ai_userId,         -- user_id
                    @c_RIGHT_READ,      -- required rights user must have to
                                        -- retrieve object (op. to be
                                        -- performed)
                    @l_rights OUTPUT    -- returned value

                -- check if the user has the necessary rights
                -- to see the user who checked out the object:
                IF (@l_rights = @c_RIGHT_READ) -- the user has the rights?
                BEGIN
                    -- set the values:
                    SELECT  @ao_checkOutUserName = @l_tempName,
                            @ao_checkOutUserOid = @l_tempOid
                END -- if the user has the rights
            END -- if the object is checked out

            -- set object as already read:
            EXEC    p_setRead @ao_oid, @ai_userId
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- get the default data of the object and return it
            SELECT  @ao_state = 0, @ao_tVersionId = 0x0,
                    @ao_typeName = '', @ao_name = '',
                    @ao_containerId = @c_NOOID,
                    @ao_containerKind = 0, @ao_isLink = 0,
                    @ao_linkedObjectId = @c_NOOID,
                    @ao_owner = 0x0, @ao_ownerName = '',
                    @ao_creationDate = getDate (), @ao_creator = 0x0,
                    @ao_creatorName = '',
                    @ao_lastChanged = getDate (), @ao_changer = 0x0,
                    @ao_changerName = '',
                    @ao_validUntil = getDate (), @ao_description = '',
                    @ao_showInNews = 0,
                    @ao_checkedOut = 0

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                'set default return values', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

            SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists
    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Object$performRetrieve', @l_error, @l_ePos,
            'ai_userId', @ai_userId,
            'ai_oid_s', @ai_oid_s,
            'ai_op', @ai_op
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Object$performRetrieve


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             ID of the user who is retrieving the object.
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
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         Display the object in the news.
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   @icon               Icon of the object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 *
 * @deprecated  This procedure is never used.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$retrieve2'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$retrieve2
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
    @state          STATE           OUTPUT,
    @tVersionId     TVERSIONID      OUTPUT,
    @typeName       NAME            OUTPUT,
    @name           NAME            OUTPUT,
    @containerId    OBJECTID        OUTPUT,
    @containerName  NAME            OUTPUT,
    @containerKind  INT             OUTPUT,
    @isLink         BOOL            OUTPUT,
    @linkedObjectId OBJECTID        OUTPUT,
    @owner          USERID          OUTPUT,
    @ownerName      NAME            OUTPUT,
    @creationDate   DATETIME        OUTPUT,
    @creator        USERID          OUTPUT,
    @creatorName    NAME            OUTPUT,
    @lastChanged    DATETIME        OUTPUT,
    @changer        USERID          OUTPUT,
    @changerName    NAME            OUTPUT,
    @validUntil     DATETIME        OUTPUT,
    @description    DESCRIPTION     OUTPUT,
    @showInNews     BOOL            OUTPUT,
    @checkedOut     BOOL            OUTPUT,
    @checkOutDate   DATETIME        OUTPUT,
    @checkOutUser   USERID          OUTPUT,
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT,
    @icon           NAME            OUTPUT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    EXEC    @retValue = p_Object$performRetrieve2
            @oid_s, @userId, @op,
            @state OUTPUT, @tVersionId OUTPUT, @typeName OUTPUT, @name OUTPUT,
            @containerId OUTPUT, @containerName OUTPUT, @containerKind OUTPUT,
            @isLink OUTPUT, @linkedObjectId OUTPUT,
            @owner OUTPUT, @ownerName OUTPUT,
            @creationDate OUTPUT, @creator OUTPUT, @creatorName OUTPUT,
            @lastChanged OUTPUT, @changer OUTPUT, @changerName OUTPUT,
            @validUntil OUTPUT, @description OUTPUT, @showInNews OUTPUT,
            @checkedOut OUTPUT, @checkOutDate OUTPUT,
            @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT,
            @icon OUTPUT

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$retrieve2



/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             ID of the user who is retrieving the object.
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
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         Display object in the news.
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$retrieve
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
    @state          STATE           OUTPUT,
    @tVersionId     TVERSIONID      OUTPUT,
    @typeName       NAME            OUTPUT,
    @name           NAME            OUTPUT,
    @containerId    OBJECTID        OUTPUT,
    @containerName  NAME            OUTPUT,
    @containerKind  INT             OUTPUT,
    @isLink         BOOL            OUTPUT,
    @linkedObjectId OBJECTID        OUTPUT,
    @owner          USERID          OUTPUT,
    @ownerName      NAME            OUTPUT,
    @creationDate   DATETIME        OUTPUT,
    @creator        USERID          OUTPUT,
    @creatorName    NAME            OUTPUT,
    @lastChanged    DATETIME        OUTPUT,
    @changer        USERID          OUTPUT,
    @changerName    NAME            OUTPUT,
    @validUntil     DATETIME        OUTPUT,
    @description    DESCRIPTION     OUTPUT,
    @showInNews     BOOL            OUTPUT,
    @checkedOut     BOOL            OUTPUT,
    @checkOutDate   DATETIME        OUTPUT,
    @checkOutUser   USERID          OUTPUT,
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    EXEC    @retValue = p_Object$performRetrieve
            @oid_s, @userId, @op,
            @state OUTPUT, @tVersionId OUTPUT, @typeName OUTPUT, @name OUTPUT,
            @containerId OUTPUT, @containerName OUTPUT, @containerKind OUTPUT,
            @isLink OUTPUT, @linkedObjectId OUTPUT,
            @owner OUTPUT, @ownerName OUTPUT,
            @creationDate OUTPUT, @creator OUTPUT, @creatorName OUTPUT,
            @lastChanged OUTPUT, @changer OUTPUT, @changerName OUTPUT,
            @validUntil OUTPUT, @description OUTPUT, @showInNews OUTPUT,
            @checkedOut OUTPUT, @checkOutDate OUTPUT,
            @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$retrieve



/******************************************************************************
 * Determine the oid of the object which is the next container above a given
 * object. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object where the upper object's oid
 *                              shall be determined.
 *
 * @output parameters:
 * @param   @upperOid           The upper object's oid.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$getUpperOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$getUpperOid
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    -- output parameters
    @upperOid       OBJECTID        OUTPUT
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- define locally used variables:
    DECLARE @posNoPath POSNOPATH_VC


    -- get posNoPath of actual object:
    SELECT  @posNoPath = posNoPath
    FROM    ibs_Object
    WHERE   oid = @oid

    -- get the oid of the object which is the nearest container above the
    -- actual object:
    SELECT  @upperOid = containerId
    FROM    ibs_Object
    WHERE   posNoPath =
            (SELECT MAX (o.posNoPath)
            FROM    ibs_Object o
                    LEFT OUTER JOIN ibs_Object c ON o.containerId = c.oid
            WHERE   @posNoPath LIKE o.posNoPath + '%'
                AND (   o.containerKind = 1
/*
                    -- check for Thema and Beitrag:
                    OR  o.containerKind = 3
*/
                    )
                -- check for Thema and Beitrag and DiscXMLViewer:
                AND c.tVersionId NOT IN (0x01010401, 0x01010501, 0x01017511)
            )

/*
    SELECT  @upperOid = CASE WHEN containerKind = 1 THEN oid ELSE containerId END
    FROM    ibs_Object
    WHERE   oid = @upperOid
*/

    -- check if the object exists:
    IF (@@ROWCOUNT <= 0)                -- object does not exist?
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- if the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$getUpperOid


/******************************************************************************
 * Determine the oid of the object which represents a tab of a given object
 * determined by the object's name. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object where the tab object's oid
 *                              shall be determined.
 * @param   @name               Name of the tab object.
 *
 * @output parameters:
 * @param   @tabOid             The tab object's oid.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$getTabInfo'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$getTabInfo
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @operation      INT,
    @userId         ID,
    @name           NAME,
    -- output parameters
    @tabOid         OBJECTID        OUTPUT,
    @tabContent     INT             OUTPUT
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    -- get the oid of the object which represents a tab of the actual object
    -- determined by its name:
    SELECT  @tabOid = oid
    FROM    ibs_Object
    WHERE   containerId = @oid

--! HACK HACK HACK HB wegen Umlauten ...
        AND name LIKE @name
--! ... HACK HACK HACK wegen Umlauten
        AND containerKind = 2

    -- check if the object exists:
    IF (@@ROWCOUNT <= 0)                -- object does not exist?
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- if the object does not exist

    ELSE

    SELECT  @tabContent = COUNT (*)
   	FROM    v_Container$content
   	WHERE   containerid = @tabOid
    AND	    (rights & @operation) = @operation
    AND     userid = @userid

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$getTabInfo



/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also deletes all links showing to this object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for deleting a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * [@param   @oid]              Oid of the deleted object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performDelete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performDelete
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- output parameters:
    @ao_oid                 OBJECTID = 0x0000000000000000 OUTPUT
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_retValue             INT             -- return value of a function

    -- assign constants:

    -- initialize local variables:

-- body:
    -- perform common procedure with all options enabled:
    EXEC @l_retValue = p_Object$performDeleteCustomized
        @ai_oid_s, @ai_userId, @ai_op, 1, 1, 1, 1,
        @ao_oid OUTPUT

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Object$performDelete


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also deletes all links showing to this object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for deleting a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be deleted.
 * @param   ai_userId           ID of the user who is deleting the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_deleteRecursive  Shall the deletion be done recursively?
 *                              This is not necessary if the object does never
 *                              have subobjects.
 * @param   ai_deleteTabs       Shall the tabs be deleted?
 *                              This flag is only relevant if we are not
 *                              deleting recursively.
 * @param   ai_deleteReferences Shall the references to the object be deleted?
 *                              If there are no references this costs
 *                              performance!
 * @param   ai_writeProtocolEntry Shall a protocol entry be written?
 *
 * @output parameters:
 * [@param   @oid]              Oid of the deleted object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performDeleteCustomized'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performDeleteCustomized
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_deleteRecursive     BOOL,
    @ai_deleteTabs          BOOL,
    @ai_deleteReferences    BOOL,
    @ai_writeProtocolEntry  BOOL,
    -- output parameters:
    @ao_oid                 OBJECTID = 0x0000000000000000 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_RIGHT_DELETE         INT,            -- right for deleting
    @c_CHECKEDOUT           INT,            -- flag for object being
                                            -- checked out
    @c_NOTDELETABLE         INT,            -- flag for object not being
                                            -- deletable

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_checkedOut           INT,            -- is the object checked out?
    @l_co_userId            USERID,         -- id of check out user
    @l_notDeletable         INT,            -- is the object deletable?
    @l_adminId              USERID,         -- id of administrator
    @l_rights               INT,            -- the rights the user has
    @l_containerId          OBJECTID,       -- the object's container
    @l_posNoPath            POSNOPATH_VC,   -- the object's posNoPath
    @l_oLevel               INT,             -- the oLevel of the object

    -- local variables used to create the protocol entry
    @l_fullName             NAME,           -- fullname of the user
    @l_tVersionId           TVERSIONID,     -- tversionid of the object
    @l_containerKind        INT,            -- containerKind
    @l_name                 NAME,           -- name of the object
    @l_owner                USERID,         -- owner of the object
    @l_icon                 NAME            -- icon of the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3,
    @c_RIGHT_DELETE         = 16,
    @c_CHECKEDOUT           = 16,
    @c_NOTDELETABLE         = 128

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0,
    @l_rights = 0

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @ai_oid_s, @ao_oid OUTPUT

    -- get container id, posNoPath and level within tree of object,
    -- check if the object is checked out or not deletable?
    SELECT  @l_containerId = o.containerId, @l_posNoPath = o.posNoPath,
            @l_oLevel = o.oLevel,
            @l_co_userId = co.userId, @l_checkedOut = o.flags & @c_CHECKEDOUT,
            @l_notDeletable = o.flags & @c_NOTDELETABLE,
            @l_icon = o.icon, @l_tVersionId = o.tVersionId,
            @l_name = o.name, @l_owner = o.owner,
            @l_containerKind = o.containerKind
    FROM    ibs_Object o LEFT OUTER JOIN ibs_Checkout_01 co ON o.oid = co.oid
    WHERE   o.oid = @ao_oid

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get the id of the administrator:
        SELECT  @l_adminId = d.adminId
        FROM    ibs_Domain_01 d, ibs_User u
        WHERE   u.id = @ai_userId
            AND u.domainId = d.id

        -- check if the user may delete the object:
        IF ((@l_checkedOut = @c_CHECKEDOUT) AND (@l_co_userId <> @ai_userId)) OR
           (@l_notDeletable = @c_NOTDELETABLE AND @ai_userId <> @l_adminId)
        BEGIN
            SELECT @l_retValue = @c_INSUFFICIENT_RIGHTS
        END -- if
        ELSE
        BEGIN

-- HP 990830
-- PROBLEM: only one object right checked here - no subsequent objects!!!!

            -- get rights for this user
            EXEC p_Rights$checkRights
                 @ao_oid,               -- given object to be accessed by user
                 @l_containerId,        -- container of given object
                 @ai_userId,            -- user_id
                 @ai_op,                -- required rights user must have to
                                        -- delete object (operation to be perf.)
                 @l_rights OUTPUT       -- returned value

            -- check if the user has the necessary rights
            IF (@l_rights = @ai_op)     -- the user has the rights?
            BEGIN

/* BB: now included in the upper select
            SELECT  @l_containerId = containerId, @l_posNoPath = posNoPath,
                    @l_oLevel = oLevel
            FROM    ibs_Object
            WHERE   oid = @ao_oid
*/
-- HP 990830
-- PROBLEM: protocol only for object - no subsequent objects!!!!

                IF (@ai_writeProtocolEntry = 1) -- write protocol entry?
                BEGIN

                    -- get the full name of the user:
                    SELECT  @l_fullName = fullname
                    FROM    ibs_User
                    WHERE   id = @ai_userId

                    -- add the new tuple to the ibs_Protocol table:
                    INSERT INTO ibs_Protocol_01
                           ( fullName, userId, oid, objectName, icon, tVersionId,
                             containerId, containerKind, owner, action, actionDate)
                    VALUES (@l_fullName, @ai_userId, @ao_oid, @l_name, @l_icon,
                            @l_tVersionId, @l_containerId, @l_containerKind,
                            @ai_userId, @ai_op, getDate ())
                END -- if write protocol entry

                -- start deletion of object, subsequent objects AND references
                -- to deleted objects

                IF (@ai_deleteRecursive = 0) -- no recursive deletion?
                BEGIN
                    -- mark object as 'deleted':
                    UPDATE  ibs_Object
                    SET     state = 1,
                            changer = @ai_userId,
                            lastChanged = getDate ()
                    WHERE   oid = @ao_oid

                    IF (@ai_deleteTabs = 1) -- tabs shall be deleted?
                    BEGIN
                        -- mark object tabs as 'deleted':
                        UPDATE  ibs_Object
                        SET     state = 1,
                                changer = @ai_userId,
                                lastChanged = getDate ()
                        WHERE   containerKind = 2
                            AND containerId = @ao_oid
                    END -- if tabs shall be deleted

                    IF (@ai_deleteReferences = 1) -- delete references?
                    BEGIN
                        -- mark references to the object as 'deleted'
                        -- ATTENTION: If you change this part you must change
                        --            p_Object$deleteAllRefs as well!!
                        UPDATE  ibs_Object
                        SET     state = 1,
                                changer = @ai_userId,
                                lastChanged = getDate ()
                        WHERE   isLink = 1
                            AND linkedObjectId = @ao_oid
                    END -- if delete references
                END -- if no recursive deletion
                ELSE                    -- delete recursive
                BEGIN
                    -- mark object and subsequent objects as 'deleted' via
                    -- posnopath
                    UPDATE  ibs_Object
                    SET     state = 1,
                            changer = @ai_userId,
                            lastChanged = getDate ()
                    WHERE   posNoPath LIKE @l_posNoPath + '%'

                    IF (@ai_deleteReferences = 1) -- delete references?
                    BEGIN
                        -- mark references to the object as 'deleted'
                        -- ATTENTION: If you change this part you must change
                        --            p_Object$deleteAllRefs as well!!
                        UPDATE  ibs_Object
                        SET     state = 1,
                                changer = @ai_userId,
                                lastChanged = getDate ()
                        WHERE   isLink = 1
                            AND linkedObjectId IN
                                (
                                    SELECT  oid
                                    FROM    ibs_Object
                                    WHERE   posNoPath LIKE @l_posNoPath + '%'
                                        AND state = 1
                                        AND isLink = 0
                                )
                    END -- if delete references
                END -- else delete recursive


/* BB This is not necessary, the external keys ca be reorganized another time.
                -- the external keys of the deleted objects have to be archived
                EXEC p_KeyMapper$archiveExtKeys @l_posNoPath
*/

            END -- if the user has the rights
            ELSE                                -- the user does not have the rights
            BEGIN
                SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
            END -- else the user does not have the rights
        END
    END -- if object exists
    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @l_retValue = @c_OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Object$performDeleteCustomized


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
-- delete existing procedure:
EXEC p_dropProc 'p_Object$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    BEGIN TRANSACTION
        -- perform deletion of object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

/*
        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            ...
        END -- if operation properly performed
*/
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$delete

/******************************************************************************
 * Undeletes an object and all its values (incl. rights check). <BR>
 * This procedure also undeletes all links showing to this object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for undeleting a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be undeleted.
 * @param   @userId             ID of the user who is undeleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * [@param   @oid]              Oid of the deleted object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performUnDelete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performUnDelete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @oid            OBJECTID = 0x0000000000000000 OUTPUT
)
AS
    -- convertions: (OBJECTIDSTRING) -- all input objectids must be converted
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- define return constants
    DECLARE @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- define used variables
    DECLARE @containerId OBJECTID, @posNoPath POSNOPATH_VC, @oLevel INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @OBJECTNOTFOUND = 3
    -- init return value
    SELECT @retValue = @ALL_RIGHT

    BEGIN

        -- get container id, posNoPath and level within tree of object
        SELECT  @containerId = containerId, @posNoPath = posNoPath, @oLevel = oLevel
        FROM    ibs_Object
        WHERE   oid = @oid

        IF (@@ROWCOUNT < 1)
	    -- set the return value wirh the error code
 	    SELECT @retValue = @OBJECTNOTFOUND
	ELSE

	BEGIN
            -- start undeletion of object, subsequent objects AND references to
            -- mark object and subsequent objects as 'deleted' via posnopath
            UPDATE  ibs_Object SET state = 2
            WHERE   posNoPath LIKE @posNoPath + '%'

            -- mark references to the object as 'undeleted'
	    UPDATE  ibs_Object SET state = 2
            WHERE   linkedObjectId IN
                (SELECT oid
                 FROM    ibs_Object
                 WHERE   posNoPath LIKE @posNoPath + '%'
                 AND state = 2
                )

        END -- else the object does not exist

    END
    -- return the state value
    RETURN  @retValue
GO
-- p_Object$performDelete

/******************************************************************************
 * Undeletes an object and all its values (incl. rights check). <BR>
 * This procedure also undelets all links showing to this object.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be undeleted.
 * @param   @userId             ID of the user who is undeleting the object.
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
-- delete existing procedure:
EXEC p_dropProc 'p_Object$undelete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$undelete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    BEGIN TRANSACTION
        -- perform deletion of object:
        EXEC @retValue = p_Object$performUnDelete @oid_s, @userId, @op

/*
        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            ...
        END -- if operation properly performed
*/
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$undelete


/******************************************************************************
 * Deletes all references to an object - but not the object itself.<BR>
 * Attention: no rights check will be done!
 *
 * @input parameters:
 * @param   @oid_s              ID of the object wich refs are to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$deleteAllRefs'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$deleteAllRefs
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    BEGIN TRANSACTION
        -- mark references to the object as 'deleted'
        -- ATTENTION: used like in p_Object$performDelete
        --            if delete mechanism changes: change both!!
        UPDATE  ibs_Object
        SET     state = 1,
                changer = @userId,
                lastChanged = getDate()
        WHERE   linkedObjectId = @oid
    COMMIT TRANSACTION


    -- return the state value
    RETURN  @retValue
GO
-- p_Object$deleteAllRefs



/******************************************************************************
 * All stored procedures used for copyPaste.
 ******************************************************************************
 */

/******************************************************************************
 * Read out the masterattachment of a given businessobject. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the rootobject to be copied.
 *
 * @output parameters:
 * @param   @masterOid_s        The Oid of the masterattachment.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$getMasterOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$getMasterOid
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    -- output parameters
    @masterOid         OBJECTID  OUTPUT
)
AS
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    -- get the oid of the object which represents a tab of the actual object
    -- determined by its name:
    SELECT  @masterOid = o.oid
    FROM    ibs_Object o, ibs_Attachment_01 a
    WHERE   o.containerId =
            (SELECT oid
            FROM    ibs_Object
            WHERE   containerId = @oid
                AND tVersionId = 0x01010061)
        AND o.oid = a.oid
        AND a.isMaster = 1


    -- set object as already read:
    EXEC    p_setRead @oid, @userId

    -- check if the object exists:
    IF (@@ROWCOUNT <= 0)                -- object does not exist?
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- if the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$getMasterOid


/******************************************************************************
 * Copies a selected BusinessObject and its childs.(incl. rights check). <BR>
 * The rightcheck is done before we start to copy. When one BO of the tree is
 * not able to be copied because of a negativ rightcheckresult, the action is stopped.
 *
 * @input parameters:
 * @param   @oid_s              ID of the rootobject to be copied.
 * @param   @userId             ID of the user who is copying the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @targetId_s         Oid of the target BusinessObjectConatiner the root
 *                              object is copied to.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$test'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$test
(
    -- input parameters:
    @oid_s                  OBJECTIDSTRING,
    @containerId_s          OBJECTIDSTRING,

    -- output parameters:
    @accObjId               OBJECTID OUTPUT,
    @accObjName             NAME OUTPUT,
    @accObjContainerId      OBJECTID OUTPUT,
    @accObjContainerKind    INT OUTPUT
)
AS
    DECLARE @oid OBJECTID, @containerId OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT
    EXEC p_stringToByte @containerId_s, @containerId OUTPUT

    DECLARE @containerTVersionId TVERSIONID, @type INT

    SELECT  @containerTVersionId = tVersionId
    FROM    ibs_Object
    WHERE   oid = @containerId

    IF (@containerTVersionId = 0x01010801 OR -- news
        @containerTVersionId = 0x01012d01) -- inbox
    BEGIN
        SELECT  @accObjId = oid, @accObjName = name,
                @accObjContainerId = containerId,
                @accObjContainerKind = containerKind
        FROM    ibs_Object
        WHERE   oid = @containerId
    END -- if news or inbox
    ELSE                                -- neither news nor inbox
    BEGIN
        SELECT  @type = o.type, @accObjId = o.oid, @accObjName = o.name,
                @accObjContainerId = o.containerId,
                @accObjContainerKind = o.containerKind
        FROM    (
                    -- container:
                    SELECT  1 AS type, oid, name, containerId, containerKind
                    FROM    ibs_Object
                    WHERE   oid IN (
                            SELECT  containerId
                            FROM    ibs_Object
                            WHERE   oid = @oid)
                    UNION

                    -- link:
                    SELECT  2 AS type, oid, name, containerId, containerKind
                    FROM    ibs_Object
                    WHERE   linkedObjectId = @oid
                    UNION

                    -- sentObject
                    SELECT  3 AS type, oid, name, containerId, containerKind
                    FROM    ibs_Object
                    WHERE   oid IN (
                            SELECT  oid
                            FROM    ibs_SentObject_01
                            WHERE   distributeId = @oid)
                ) o
        WHERE   oid <> 0x0000000000000000
    END -- else neither news nor inbox
GO
-- p_Object$test


/******************************************************************************
 * Checks a business object out (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be checked out.
 * @param   @userId             ID of the user who is checking out the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @output parameters:
 * @param   @creationDate       Date when the object was checked out.
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$checkOut'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$checkOut
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
    @checkOutDate   DATETIME        OUTPUT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2   -- return values

    -- value for checked out for the flags-bitarray
    DECLARE @CHECKEDOUT INT
    SELECT @CHECKEDOUT = 16

    DECLARE @rights RIGHTS              -- return value of called procedure
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID

    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- retrieve the information if this object is already checked out
    SELECT  *
    FROM    ibs_Checkout_01
    WHERE   oid = @oid

    IF (@@ROWCOUNT <= 0)                -- object does not exist == is not checked out
    BEGIN

        -- rights set for this operation?
        EXEC p_Rights$checkRights
             @oid,                      -- given object to be accessed by user
             0x0000000000000000,        -- containeroid - no oid in this case cause irrelevant
             @userId,                   -- user_id
             @op,                       -- required rights user must have to
                                        -- checkout object (op. to be
                                        -- performed)
             @rights OUTPUT             -- returned value

        -- check if the user has the necessary rights
        IF (@rights = @op)              -- the user has the rights?
        BEGIN
            -- checkOut the object
            -- set the flag for the checkout in the ibs_object table
            UPDATE  ibs_Object
            SET     flags = flags | @CHECKEDOUT
            WHERE   oid = @oid

            -- get the current checkout date
            SELECT @checkOutDate = getDate ()

            -- add the new tuple to the ibs_CheckOut table:
            INSERT INTO ibs_Checkout_01
                   ( oid, userId, checkout)
            VALUES ( @oid, @userId, @checkOutDate)
        END -- if the user has the rights to see the user who checked out the object
        ELSE
            SELECT @retValue = @INSUFFICIENT_RIGHTS
    END -- if the object does not exist
    ELSE
        SELECT @retValue = @INSUFFICIENT_RIGHTS  -- the object was already checked out

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$checkOut



/******************************************************************************
 * Checks in a business object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             ID of the user who is retrieving the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$checkIn'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$checkIn
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2   -- return values

    -- value for checked out for the flags-bitarray
    DECLARE @CHECKEDOUT INT
    SELECT @CHECKEDOUT = 16

    DECLARE @rights RIGHTS              -- return value of called procedure
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
    DECLARE @checkedUser USERID

    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- retrieving the userId of the user who has checked out this object
    SELECT  @checkedUser = userId
    FROM    ibs_CheckOut_01
    WHERE   oid = @oid

    -- is the user who wants to perform the checkin the same with the one
    -- who has checked out the object?
    IF (@checkedUser = @userId)
    BEGIN
        -- rights set for this operation?
        EXEC p_Rights$checkRights
             @oid,                      -- given object to be accessed by user
             0x0000000000000000,        -- containeroid - no oid in this case cause irrelevant
             @userId,                   -- user_id
             @op,                       -- required rights user must have to
                                        -- checkout object (op. to be
                                        -- performed)
             @rights OUTPUT             -- returned value

        -- check if the user has the necessary rights
        IF (@rights = @op)              -- the user has the rights?
        BEGIN
            -- remove the flag for the checkout in the ibs_object table
            UPDATE  ibs_Object
            SET     flags = (flags & (0xFFFFFFFF ^ @CHECKEDOUT))
            WHERE   oid = @oid

            -- delete the tuple regarding this object from the ibs_CheckOut table:
            DELETE  ibs_Checkout_01
            WHERE   oid = @oid
        END -- if the user has the rights to see the user who checked out the object
        ELSE
            SELECT @retValue = @INSUFFICIENT_RIGHTS
    END -- the user is not the one who checked out
    ELSE
        SELECT @retValue = @INSUFFICIENT_RIGHTS

    -- return the state value
    RETURN  @retValue
GO
-- p_Object$checkIn


/******************************************************************************
 * Returns the OID of a object, regarding to its name. <BR>
 *
 * @input parameters:
 * @param   @objectName         name of the object, that should be found
 * @param   @userName           name of the user (only set, if searching for a
                                privat container
 * @param   @domainId           id of the domain, where the object should be
                                found (only set at the search for the root)
 * @param   @actContainer_s     oid of the last container found
 *
 * @output parameters:
 * @param   @containerId_s      oid of the object with @objectName
 * @param   @isContainer        1 if the object with @objectName is a container
 *
 * @returns A value representing the state of the procedure.
 *  OBJECTNOTFOUND          The object was not found
 *  TOOMANYROWS             More than 1 object was found
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$resolveObjectPath'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$resolveObjectPath
(
    -- input parameters:
    @ai_objectName          NAME,
    @ai_userName            NAME,
    @ai_domainId            INT,
    @ai_actContainerOid_s   OBJECTIDSTRING,
    -- output parameters:
    @ao_objectOid_s         OBJECTIDSTRING  OUTPUT,
    @ao_isContainer         INT  OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_TOOMANYROWS          INT,            --

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_actContainerOid      OBJECTID,       -- oid of the actual found container
    @l_objectOid            OBJECTID,       -- oid of the new actual object
    @l_domainOid            OBJECTID,       -- oid of the domain, where the
                                            -- container should be
    @l_userId               USERID          -- id of the user in whose private
                                            -- area the container should be

-- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3,
    @c_TOOMANYROWS          = 5

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    EXEC p_stringToByte @ai_actContainerOid_s, @l_actContainerOid OUTPUT

    IF (@ai_domainId = -1)              -- default case
    BEGIN
        -- get the oid of the object:
        SELECT @l_objectOid = oid, @ao_isContainer = iscontainer
        FROM   ibs_Object
        WHERE  name = @ai_objectName
           AND containerId = @l_actContainerOid
           AND state = 2
    END -- if default case
    ELSE
    BEGIN                               -- special case
        IF (@ai_userName = '')          -- not in a private area
        BEGIN
            -- get the oid of the object within the proper domain:
            SELECT  @l_objectOid = o.oid, @ao_isContainer = o.isContainer
            FROM    ibs_MenuTab_01 m, ibs_Object o
            WHERE   m.domainId = @ai_domainId
                AND m.objectOid = o.oid
                AND o.name = @ai_objectName
                AND state = 2
        END -- if not in private area
        ELSE                            -- something in a 'Privat'-container
        BEGIN
            -- get the userId from a given username:
            SELECT @l_userId = u.id
            FROM   ibs_User u, ibs_Object o
            WHERE  u.domainId = @ai_domainId
               AND u.name = @ai_userName
               AND u.oid = o.oid
               AND o.state = 2

            -- get the oid of the workspace for a given user:
            SELECT @l_objectOid = workspace
            FROM   ibs_workspace
            WHERE  userid = @l_userId

            -- the workspace is a container:
            SELECT @ao_isContainer = 1
        END -- else something in a 'Privat'-container
    END -- else special case

    IF (@@ROWCOUNT = 0)                 -- nothing found --> error
    BEGIN
        SELECT @l_retValue = @c_OBJECTNOTFOUND
    END
    ELSE IF (@@ROWCOUNT > 1)            -- found more than 1 --> error
    BEGIN
        SELECT @l_retValue = @c_TOOMANYROWS
    END
    ELSE                                -- found 1 --> OK
    BEGIN
        EXEC p_byteToString @l_objectOid, @ao_objectOid_s OUTPUT
    END

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Object$resolveObjectPath
