/******************************************************************************
 * All views regarding the Membership of a User. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Klaus Reimüller (KR) 990407
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner.
 */
CREATE OR REPLACE VIEW v_MemberShip_01$content
AS
    SELECT  u.oid AS containerId, gu.origGroupId, g.id, 
            o.oid, o.state, o.name, o.tVersionId, o.typeName, o.isLink, 
            o.linkedObjectId, o.owner, o.lastChanged, o.posNoPath, o.icon,
            o.userId, o.rights, o.isNew, o.flags,
            o.ownerName, o.ownerFullname, o.ownerOid, o.description
    FROM    v_Container$content o, ibs_Group g, ibs_User u, ibs_GroupUser gu
    WHERE   o.tVersionId = 16842929
        AND o.oid = g.oid
        AND g.id = gu.groupId
        AND u.id = gu.userId
        AND gu.origGroupId = gu.groupId        
;
-- v_MemberShip_01$content

EXIT;
