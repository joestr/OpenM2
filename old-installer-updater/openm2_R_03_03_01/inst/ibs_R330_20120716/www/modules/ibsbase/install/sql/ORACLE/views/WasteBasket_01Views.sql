/******************************************************************************
 * All views regarding a Menu container. <BR>
 *
 * @version     1.10.0001, 20.12.2000
 *
 * @author     Mario Oberdorfer (MO), 001206
 *
 * @references:
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container. <BR>
 */

-- deletes the view if allready exists

CREATE OR REPLACE VIEW v_WasteBasket_01$delete
AS
    SELECT DISTINCT o.oid, o.state, o.name, o.typeName, o.isLink,
	   o.linkedObjectId, o.owner, o.description, o.lastChanged, 
	   o.icon, o.flags, o.containerId, o.containerKind,
	   u.name AS ownerName, u.oid AS ownerOid,
	   u.fullname AS ownerFullname, 0 AS isNew,   
	   2147483647 AS rights, o.changer, o.owner AS userId
    FROM   ibs_Object o, ibs_User u
    WHERE  o.state = 1                                  -- only deleted entries
    AND    (SYSDATE - o.lastChanged) < 8  -- deleted in last week
    AND    o.containerKind = 1                          -- no tab-objects
    AND    o.isLink = 0                                 -- no references
    AND    o.owner = u.id
    -- do not view all types
    AND    o.tVersionId <> 16842913 -- TV_User
    AND    o.tVersionId <> 16857089 -- TV_UserProfile
    AND    o.tVersionId <> 16856577 -- TV_UserAdminContainer
    AND    o.tVersionId <> 16855809 -- TV_UserContainer
    AND    o.tVersionId <> 16842929 -- TV_Group
    AND    o.tVersionId <> 16856065 -- TV_GroupContainer
    UNION
    SELECT DISTINCT o.oid, o.state, o.name, o.typeName, o.isLink,
	   o.linkedObjectId, o.owner, o.description, o.lastChanged, 
	   o.icon, o.flags, o.containerId, o.containerKind,
	   u.name AS ownerName, u.oid AS ownerOid,
	   u.fullname AS ownerFullname, 0 AS isNew,   
	   2147483647 AS rights, o.changer, o.changer AS userId
    FROM   ibs_Object o, ibs_User u
    WHERE  o.state = 1                                  -- only deleted entries
    AND    (SYSDATE - o.lastChanged) < 8  -- deleted in last week
    AND    o.containerKind = 1                          -- no tab-objects
    AND    o.isLink = 0                                 -- no references
    AND    o.owner = u.id
    -- do not view all types
    AND    o.tVersionId <> 16842913 -- TV_User
    AND    o.tVersionId <> 16857089 -- TV_UserProfile
    AND    o.tVersionId <> 16856577 -- TV_UserAdminContainer
    AND    o.tVersionId <> 16855809 -- TV_UserContainer
    AND    o.tVersionId <> 16842929 -- TV_Group
    AND    o.tVersionId <> 16856065 -- TV_GroupContainer
    ;
-- v_WasteBasket_01$delete
EXIT;