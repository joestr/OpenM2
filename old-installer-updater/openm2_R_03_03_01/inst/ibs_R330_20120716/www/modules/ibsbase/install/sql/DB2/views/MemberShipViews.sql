------------------------------------------------------------------------------
 -- All views regarding the Membership of a User. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------
 
    -- Gets the data of the objects within a given container (incl. rights).
    -- This view returns if the user has already read the object and the name of 
    -- the owner.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_MEMBERSHIP_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_MemberShip_01$content  
AS      
    SELECT  u.oid AS containerId, gu.origGroupId, g.id, o.oid, o.state, o.name,
            o.tVersionId, o.typeName, o.isLink, o.linkedObjectId, o.owner, 
            o.lastChanged, o.posNoPath, o.icon, o.userId, o.rights, o.isNew, 
            o.flags, o.ownerName, o.ownerFullname, o.ownerOid, o.description
    FROM    IBSDEV1.v_Container$content o, 
         IBSDEV1.ibs_Group g, IBSDEV1.ibs_User u, 
         IBSDEV1.ibs_GroupUser gu
    WHERE   o.tVersionId = 16842929
    AND     o.oid = g.oid
    AND     g.id = gu.groupId
    AND     u.id = gu.userId
    AND     gu.origGroupId = gu.groupId;
    -- v_MemberShip_01$content