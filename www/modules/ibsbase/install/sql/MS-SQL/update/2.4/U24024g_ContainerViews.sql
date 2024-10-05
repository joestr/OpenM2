/******************************************************************************
 * Extend v_Container$rights and v_Container$rights2 with containerOid2. <BR>
 *
 * @version     $Id:
 *
 * @author      Bernd Buchegger (DIBB)  050322
 ******************************************************************************
 */

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_Container$rights'
GO

-- create the new view:
CREATE VIEW v_Container$rights
AS
    -- Get all objects and combine them with all users which have rights on
    -- these objects.
    -- Attention: There are only those users considered which hav   e defined
    -- rights on an object. For users who have no rights defined on an object
    -- there are no tuples generated for this object.
    -- This means that the (java) queries on this view must consider that
    -- objects for which an user has no defined rights do not exist for this
    -- user!
    -- Take the defined rights for all users which are not owners and a
    -- special set of rights for the owner of an object. The owner is a pseudo
    -- user with id 0x00900001.
/* no owner check:
*/
    SELECT  o.containerId, o.containerKind, o.containerOid2,
            o.oid, o.state, o.name,
            o.tVersionId, o.typeName, o.isContainer, o.isLink,
            o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged,
            o.posNoPath, o.icon, o.description, o.flags, o.validUntil,
            r.userId,
            o.processState, o.creationDate, r.rights
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.containerKind <= 1
        AND o.state = 2
        AND o.rKey = r.rKey
GO
-- v_Container$rights


/******************************************************************************
 * Refresh all views in order to resolve dependencies.
 */
EXEC p_refreshAllViews
GO


-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
