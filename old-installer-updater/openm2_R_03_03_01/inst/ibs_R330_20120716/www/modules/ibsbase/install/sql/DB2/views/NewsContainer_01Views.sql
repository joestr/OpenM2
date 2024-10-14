 ------------------------------------------------------------------------------
 -- All views regarding a news container. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------


    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_NEWSCONTAINER$CONTENT');
    -- create the new view: 
CREATE VIEW IBSDEV1.v_NewsContainer$content  
AS      
    SELECT  obj.*, 1 - COALESCE (objr.hasRead, 0) AS isNew, 
            own.name AS ownerName, own.oid AS ownerOid,
            own.fullname AS ownerFullname
            FROM    (
                     SELECT  o.containerId, o.containerKind, o.oid, o.state,
                             o.name, o.tVersionId, o.typeName, o.isContainer, 
                             o.isLink, o.linkedObjectId, o.showInMenu, o.owner,
                             o.lastChanged, o.posNoPath, o.icon, o.description,
                             o.flags, o.validUntil, r.userId AS userId, 
                             o.processState, o.creationDate, r.rights
                     FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
                     WHERE   o.containerKind <= 1 
                     AND     o.state = 2
                     AND     o.rKey = r.rKey   
                     ) obj 
            LEFT OUTER JOIN IBSDEV1.ibs_ObjectRead objr ON obj.oid = objr.oid
            AND obj.userId = objr.userId, IBSDEV1.ibs_User own
            WHERE obj.containerKind = 1
            AND obj.state = 2
            AND IBSDEV1.b_AND(obj.flags,4) = 4  
            AND obj.owner = own.id;
    -- v_NewsContainer$content