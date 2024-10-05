/******************************************************************************
 * All views regarding a news container. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Andreas Jansa (AJ)  990311
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_NewsContainer$content'
GO

-- create the new view:
CREATE VIEW v_NewsContainer$content
AS
    SELECT  obj.*, 
            1 - COALESCE (objr.hasRead, 0) AS isNew,
            own.name AS ownerName, 
            own.oid AS ownerOid,
            own.fullname AS ownerFullname
    FROM    (
                SELECT  o.containerId, o.containerKind, o.oid, o.state, o.name, 
                        o.tVersionId, o.typeName, o.isContainer, o.isLink, 
                        o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged, 
                        o.posNoPath, o.icon, o.description, o.flags,
                        o.validUntil,
                        r.userId AS userId,
                        o.processState, o.creationDate, r.rights
                FROM    ibs_Object o, ibs_RightsCum r
                WHERE   o.containerKind <= 1
                    AND o.state = 2
                    AND o.rKey = r.rKey
--
-- HP: removed!!                    
--                    AND o.owner <> r.userId
--                    
            ) obj LEFT OUTER JOIN ibs_ObjectRead objr ON obj.oid = objr.oid 
                  AND obj.userId = objr.userId, 
            ibs_User own
    WHERE   obj.containerKind = 1
        AND obj.state = 2
        AND (obj.flags & 4) = 4
--
-- HP: replaced by a left outer join (see above)
--        AND obj.oid = objr.oid(+)
--
        AND obj.owner = own.id
GO
-- v_NewsContainer$content

 