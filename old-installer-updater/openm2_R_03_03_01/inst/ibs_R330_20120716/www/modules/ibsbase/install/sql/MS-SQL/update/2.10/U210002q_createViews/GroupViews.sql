/******************************************************************************
 * All views regarding the Group. <BR>
 *
 * @version     $Id: GroupViews.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  980715
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_Group_01$rights')
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_Group_01$rights
GO

CREATE VIEW v_Group_01$rights
AS
    SELECT  s.id, s.domainId, s.fullname, 
            gu.groupId, gu.origGroupId, gu.idPath, 
            g.oid AS containerId, o.oid, o.state, o.name, 
            o.tVersionId, o.typeName, o.owner, o.lastChanged, 
            o.icon, o.userId, o.rights, o.flags
    FROM    v_Container$rights o, ibs_Group g,
            (
                SELECT  groupId, origGroupId, idPath, userId
                FROM    ibs_GroupUser
                WHERE   groupId = origGroupId
            ) gu,
            (SELECT oid, id, fullname, domainId
            FROM    ibs_User
            UNION
            SELECT  oid, id, name AS fullname, domainId
            FROM    ibs_Group) s
    WHERE   o.oid = s.oid
        AND gu.groupId = g.id
        AND s.id = gu.userId
GO
-- v_Group_01$rights


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view also returns if the user has already read the object.
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_Group_01$rightsRead') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_Group_01$rightsRead
GO

-- create the new view:
CREATE VIEW v_Group_01$rightsRead
AS
    SELECT  o.*,
            CASE r.hasRead WHEN 0 THEN 1 WHEN 1 THEN 0 END AS isNew
    FROM    v_Group_01$rights o
            LEFT OUTER JOIN ibs_ObjectRead r ON o.oid = r.oid AND o.userId = r.userId
GO
-- v_Group_01$rightsRead


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner.
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_Group_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_Group_01$content
GO

-- create the new view:
CREATE VIEW v_Group_01$content
AS
    SELECT  o.*, own.name AS ownerName,
            own.fullname AS ownerFullname, own.oid AS ownerOid,
            0 AS isLink, 0x0000000000000000 AS linkedObjectId, N'' AS description
    FROM    v_Group_01$rightsRead o 
            LEFT OUTER JOIN ibs_User own ON o.owner = own.id
GO
-- v_Group_01$content
