/******************************************************************************
 * All views regarding a container. <BR>
 *
 * @version     $Id: U331003v_ContainerViews.sql,v 1.1 2013/01/18 10:38:17 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980507
 ******************************************************************************
 */


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
            o.tVersionId, o.typeCode, o.typeName, o.isContainer, o.isLink, 
            o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged, 
            o.oLevel, o.posNoPath, o.icon, o.description, o.flags, o.validUntil,
            r.userId,
            o.processState, o.creationDate, r.rights
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.containerKind <= 1
        AND o.state = 2
        AND o.rKey = r.rKey
/* orig:
    SELECT  o.containerId, o.containerKind, o.oid, o.state, o.name,
            o.tVersionId, o.typeName, o.isContainer, o.isLink,
            o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged,
            o.posNoPath, o.icon, o.description, o.flags, o.validUntil,
            CASE WHEN (r.userId = 0x00900001) THEN o.owner ELSE r.userId END AS userId,
            o.processState, o.creationDate, r.rights
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.containerKind <= 1
        AND o.state = 2
        AND o.rKey = r.rKey
        AND o.owner <> r.userId
*/
GO
-- v_Container$rights


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view also returns if the user has already read the object.
 */
-- delete existing view:
EXEC p_dropView 'v_Container$rightsRead'
GO

-- create the new view:
CREATE VIEW v_Container$rightsRead
AS
    SELECT  o.*,
            1 - COALESCE (r.hasRead, 0) AS isNew
    FROM    v_Container$rights o
            LEFT OUTER JOIN ibs_ObjectRead r ON o.oid = r.oid AND o.userId = r.userId
GO
-- v_Container$rightsRead


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of
 * the owner.
 */
-- delete existing view:
EXEC p_dropView 'v_Container$content'
GO

-- create the new view:
CREATE VIEW v_Container$content
AS
    SELECT  o.*, own.name AS ownerName,
            own.fullname AS ownerFullname, own.oid AS ownerOid
    FROM    v_Container$rightsRead o, ibs_User own
    WHERE   o.owner = own.id
/*
    -- CHANGED due to performance reasons:
    -- the outer join is not necessary, because user-objects are never
    -- physically deleted.
    -- if physically deleted: objects would 'disappear'.
    FROM    v_Container$rightsRead o
            LEFT OUTER JOIN ibs_User own ON o.owner = own.id
*/
GO
-- v_Container$content


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_Container$rightsSelList'
GO

-- create the new view:
CREATE VIEW v_Container$rightsSelList
AS
    SELECT  o.oid, o.name, o.tVersionId, o.userId, o.rights,
            t.posNoPath AS tPosNoPath
    FROM    v_Container$rights o, ibs_TVersion tv, ibs_Type t
    WHERE   o.tVersionId = tv.id
    AND     tv.typeId = t.id
GO
-- v_Container$rightsSelList


/******************************************************************************
 * A restricted version of v_Container$rights. <BR>
 * Used only for v_ProductGroup_01$content. <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_Container2$rights'
GO

CREATE VIEW v_Container2$rights
AS
    SELECT  *
    FROM    v_Container$rights
GO
-- v_Container2$rights
