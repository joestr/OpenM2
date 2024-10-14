/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
 
CREATE OR REPLACE VIEW v_Menu_01$content
AS
    SELECT  obj.*, 
            1 AS isNew,
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
                WHERE   o.state = 2
                    AND o.rKey = r.rKey
            ) obj, ibs_User own
    WHERE   obj.state = 2
        AND obj.owner = own.id
;
-- v_Menu_01$content
/

EXIT;