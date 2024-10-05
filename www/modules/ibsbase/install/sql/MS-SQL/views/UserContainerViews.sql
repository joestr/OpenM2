/******************************************************************************
 * All views regarding the UserContainer. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Martin Centner (MC) 980707
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_UserContainer_01$content'
GO

CREATE VIEW v_UserContainer_01$content
AS
    SELECT  u.id, u.fullname, u.domainId, u.name, 
            o.containerId, o.oid, o.state, o.tVersionId, o.typeName, o.isLink,
            o.linkedObjectId, o.lastChanged, o.posNoPath, o.icon,
            o.userId, o.rights, o.description, o.flags, o.processState,
            o.owner, own.name AS ownerName, own.oid AS ownerOid,
            own.fullname AS ownerFullname,
            1 - COALESCE (objr.hasRead, 0) AS isNew
    FROM    (
                SELECT  o.containerId, o.containerKind, o.oid, o.state, o.name, 
                        o.tVersionId, o.typeName, o.isContainer, o.isLink, 
                        o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged, 
                        o.posNoPath, o.icon, o.description, o.flags, 
                        o.processState, o.creationDate, o.validUntil,
                        r.userId AS userId, r.rights
                FROM    ibs_Object o, ibs_RightsCum r
                WHERE   o.containerKind <= 1
                    AND o.state = 2
                    AND o.rKey = r.rKey
--
-- HP: removed!!                    
--                    AND o.owner <> r.userId
--                    
                    AND o.tVersionId = 16842913
            ) o LEFT OUTER JOIN ibs_ObjectRead objr ON o.oid = objr.oid 
                AND o.userId = objr.userId,
            ibs_User own, ibs_User u
    WHERE   
--
-- HP: replaced by left outer joins (see above)
--            o.oid = objr.oid(+)
--        AND o.userId = objr.userId(+)
--        AND 
--        
            o.owner = own.id
        AND o.oid = u.oid
--        AND o.tVersionId = 16842913
GO        
-- v_UserContainer_01$contents

