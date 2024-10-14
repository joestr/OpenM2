/******************************************************************************
 * All views regarding a news container. <BR>
 *
 * @version     2.21.0005, 25.06.2002 KR
 *
 * @author      Andreas Jansa (AJ)  990315
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_NewsContainer$content
AS
    SELECT  obj.*, 
            1 - DECODE (objr.hasRead, NULL, 0, objr.hasRead) AS isNew,
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
/*
HP: removed!!                    
                    AND o.owner <> r.userId
*/                    
            ) obj, ibs_ObjectRead objr, ibs_User own
    WHERE   obj.containerKind = 1
        AND obj.state = 2
        AND B_AND(obj.flags, 4) = 4
        AND (   obj.oid = objr.oid(+)
            AND obj.userId = objr.userId(+)
            )
        AND obj.owner = own.id
;
-- v_NewsContainer$content

EXIT;
