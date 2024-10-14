/******************************************************************************
 * All stored procedures regarding the ibs_Workspace table. <BR>
 *
 * @version     $Id: WorkspaceProc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  980617
 ******************************************************************************
 */


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

-- delete existing procedure
EXEC p_dropProc N'p_Workspace$assignStdObjects'
GO

CREATE PROCEDURE p_Workspace$assignStdObjects
(
    @ai_oid_s          OBJECTIDSTRING
)
AS

    -- define return constants
    DECLARE @c_ALL_RIGHT INT, @c_INSUFFICIENT_RIGHTS INT,
            @c_OBJECTNOTFOUND INT, @c_NOOID OBJECTID
    -- set constants
    SELECT  @c_ALL_RIGHT = 1, @c_INSUFFICIENT_RIGHTS = 2,   -- return values
            @c_OBJECTNOTFOUND = 3, @c_NOOID = 0x0000000000000000

    -- define return values
    DECLARE @l_retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @l_retValue = @c_ALL_RIGHT

    -- define oid values
    DECLARE @l_workbox OBJECTID, @l_outbox OBJECTID,
            @l_inbox OBJECTID, @l_news OBJECTID,
            @l_profile OBJECTID, @l_workspace OBJECTID,
            @l_shoppingCart OBJECTID, @l_orders OBJECTID,
            @l_posnopath POSNOPATH_VC, @l_oid OBJECTID

    -- convert input oid string to oid
    EXEC p_StringToByte @ai_oid_s, @l_oid OUTPUT

    -- get all oids of all standardobjects in workspace
    SELECT  @l_workbox = w.workbox, @l_outbox = w.outbox, @l_inbox = w.inbox,
            @l_news = w.news, @l_profile = w.profile,
            @l_shoppingCart = w.shoppingCart, @l_orders = w.orders,
            @l_workspace = w.workspace, @l_posnopath = ow.posnopath
    FROM    ibs_Workspace w, ibs_User u, ibs_Object ow
    WHERE   w.userId = u.id
      AND   u.oid = @l_oid
      AND   ow.oid = w.workspace

    -- check workbox
    IF (@l_workbox = @c_NOOID)
    BEGIN
        -- set workbox to first container to be found in workspace
        SELECT @l_oid = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = N'Container'
          AND  o.posnopath LIKE @l_posnopath + '%'

        UPDATE ibs_Workspace SET workbox = @l_oid
        WHERE  workspace = @l_workspace
    END

    -- check outbox
    IF (@l_outbox = @c_NOOID)
    BEGIN
        -- set outbox to first SentObjectContainer to be found in workspace
        SELECT @l_oid = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = N'SentObjectContainer'
          AND  o.posnopath LIKE @l_posnopath + '%'

        UPDATE ibs_Workspace SET outbox = @l_oid
        WHERE  workspace = @l_workspace
    END

    -- check inbox
    IF (@l_inbox = @c_NOOID)
    BEGIN
        -- set inbox to first Inbox to be found in workspace
        SELECT @l_oid = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = N'Inbox'
          AND  o.posnopath LIKE @l_posnopath + '%'

        UPDATE ibs_Workspace SET inbox = @l_oid
        WHERE  workspace = @l_workspace
    END

    -- check news
    IF (@l_news = @c_NOOID)
    BEGIN
        PRINT 'set newscontainer to first newscontainer to be found in workspace'
        SELECT @l_oid = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = N'NewsContainer'
          AND  o.posnopath LIKE @l_posnopath + '%'

        UPDATE ibs_Workspace SET news = @l_oid
        WHERE  workspace = @l_workspace
    END

    -- check profile
    IF (@l_profile = @c_NOOID)
    BEGIN
        -- set profile to first userprofile to be found in workspace
        SELECT @l_oid = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = N'UserProfile'
          AND  o.posnopath LIKE @l_posnopath + '%'

        UPDATE ibs_Workspace SET profile = @l_oid
        WHERE  workspace = @l_workspace
    END

    -- check shoppingcart
    IF (@l_shoppingCart = @c_NOOID)
    BEGIN
        -- set shoppingcart to first shoppingcart to be found in workspace
        SELECT @l_oid = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = N'ShoppingCart'
          AND  o.posnopath LIKE @l_posnopath + '%'

        UPDATE ibs_Workspace SET shoppingcart = @l_oid
        WHERE  workspace = @l_workspace
    END

    -- check orders
    IF (@l_orders = @c_NOOID)
    BEGIN
        -- set shoppingcart to first shoppingcart to be found in workspace
        SELECT @l_oid = COALESCE (min (o.oid), @c_NOOID)
        FROM   ibs_Object o, ibs_Type t
        WHERE  o.state = 2
          AND  o.tVersionId = t.actVersion
          AND  t.code = N'OrderContainer'
          AND  o.posnopath LIKE @l_posnopath + '%'

        UPDATE ibs_Workspace SET orders = @l_oid
        WHERE  workspace = @l_workspace
    END

    -- return the state value
    RETURN  @l_retValue
GO
