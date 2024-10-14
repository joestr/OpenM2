/******************************************************************************
 * All views regarding a the reference-containercontent. <BR>
 *
 * @version     $Id: RefContainerViews.sql,v 1.4 2005/02/15 21:38:46 klaus Exp $
 *
 * @author      Keim Christine (CK)  990529
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_RefContainer$rights'
GO

-- create the new view:
CREATE VIEW v_RefContainer$rights
AS
    -- Get all objects and combine them with all users which have rights on 
    -- these objects.
    -- Attention: There are only those users considered which have defined 
    -- rights on an object. For users who have no rights defined on an object
    -- there are no tuples generated for this object.
    -- This means that the (java) queries on this view must consider that
    -- objects for which an user has no defined rights do not exist for this
    -- user!
    -- Take the defined rights for all users which are not owners and a
    -- special set of rights for the owner of an object. The owner is a pseudo 
    -- user with id 0x00900001.
    SELECT  v.*, s.refCOid, s.refCrights, tRefVersionId
    FROM 
            ( 
                SELECT  o.containerId AS refCOid, o.oid AS refOid,
                        o.tVersionId AS tRefVersionId,
                        r.userId, r.rights AS refCrights
                FROM    ibs_Object o, ibs_RightsCum r
                WHERE   o.containerKind = 2
                    AND o.state = 2
                    AND o.rKey = r.rKey
            ) s
            JOIN v_Container$rights v
            ON      v.userId = s.userId
                AND s.refOid = v.containerId    
/* KR tuning: no owner check necessary
    SELECT  v.*, s.refCOid, s.refCrights, tRefVersionId
    FROM 
            ( 
                SELECT  o.containerId AS refCOid, o.oid AS refOid,
                        o.tVersionId AS tRefVersionId,
                        CASE WHEN (r.userId = 0x00900001)
                        THEN o.owner ELSE r.userId END AS userId,
                        r.rights AS refCrights
                FROM    ibs_Object o, ibs_RightsCum r
                WHERE   o.containerKind = 2
                    AND o.state = 2
                    AND o.rKey = r.rKey
                    AND o.owner <> r.userId
            ) s
            JOIN v_Container$rights v
            ON      v.userId = s.userId
                AND s.refOid = v.containerId    
*/
GO
-- v_RefContainer$rights


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view also returns if the user has already read the object.
 */
-- delete existing view:
EXEC p_dropView 'v_RefContainer$rightsRead'
GO

-- create the new view:
CREATE VIEW v_RefContainer$rightsRead
AS
    SELECT  o.*,
            1 - COALESCE (r.hasRead, 0) AS isNew
    FROM    v_RefContainer$rights o
            LEFT OUTER JOIN ibs_ObjectRead r ON o.oid = r.oid AND o.userId = r.userId
GO
-- v_RefContainer$rightsRead


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner.
 */
-- delete existing view:
-- delete existing view:
EXEC p_dropView 'v_RefContainer_01$content'
GO

-- create the new view:
CREATE VIEW v_RefContainer_01$content
AS
SELECT s.* FROM
(
    -- get links wich are physically in referenceContainer
    SELECT  1 AS linkType,
            o.oid, o.state, o.name, o.typeName, o.isLink,
            o.linkedObjectId, o.owner, o.ownerName, o.ownerOid,
            o.ownerFullname, o.lastChanged, o.isNew, o.icon, o.description,
            o.flags, o.processState, o.userId, o.rights, o.containerId
    FROM    v_Container$content o
    WHERE   o.isLink = 1
    UNION ALL
    -- get links wich are pointed on main object of referenceTab and
    -- and which are physically in any tab of other object
    SELECT  2 AS linkType,
            o.oid, o.state, o.name, o.typeName, o.isLink,
            o.oid AS linkedObjectId, o.owner, o.ownerName, o.ownerOid,
            o.ownerFullname, o.lastChanged, o.isNew, o.icon, o.description,
            o.flags, o.processState, o.userId, o.rights,
            refTab.oid AS containerId
    FROM    v_Container$content o, ibs_Object tab,
            ibs_Object ref, ibs_Object refTab
    WHERE   o.oid = tab.containerId
        AND tab.containerKind = 2
        AND tab.oid = ref.containerId
        AND ref.linkedObjectId = refTab.containerId
        AND ref.isLink = 1
        AND ref.state = 2
        AND refTab.containerKind = 2
    UNION ALL
    -- get links wich are pointed on main object of referenceTab and
    -- and which are physically in any content of other object
    SELECT  3 AS linkType,
            o.oid, o.state, o.name, o.typeName, o.isLink,
            o.oid AS linkedObjectId, o.owner, o.ownerName, o.ownerOid,
            o.ownerFullname, o.lastChanged, o.isNew, o.icon, o.description,
            o.flags, o.processState, o.userId, o.rights,
            refTab.oid AS containerId
    FROM    v_Container$content o, ibs_Object ref, ibs_Object refTab
    WHERE   o.oid = ref.containerId
        AND ref.linkedObjectId = refTab.containerId
        AND ref.isLink = 1
        AND ref.state = 2
        AND refTab.containerKind = 2
) s
GO
-- v_RefContainer$content
