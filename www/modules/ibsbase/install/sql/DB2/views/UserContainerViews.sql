------------------------------------------------------------------------------
 -- All views regarding the UserContainer. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:59 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_USERCONTAINER_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_UserContainer_01$content  
AS      
    SELECT  u.id, u.fullname, u.domainId, u.name, o.containerId, o.oid, o.state,
            o.tVersionId, o.typeName, o.isLink, o.linkedObjectId, o.lastChanged,
            o.posNoPath, o.icon, o.userId, o.rights, o.description, o.flags, 
            o.processState, o.owner, own.name AS ownerName, own.oid AS ownerOid,
            own.fullname AS ownerFullname, 
            1 - COALESCE (objr.hasRead, 0) AS isNew
    FROM    (
             SELECT  o.containerId, o.containerKind, o.oid, o.state, o.name,
                     o.tVersionId, o.typeName, o.isContainer, o.isLink,
                     o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged,
                     o.posNoPath, o.icon, o.description, o.flags,
                     o.processState, o.creationDate, o.validUntil,
                     r.userId AS userId, r.rights                  
             FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r                          
             WHERE   o.containerKind <= 1                             
             AND o.state = 2                             
             AND o.rKey = r.rKey                             
             AND o.tVersionId = 16842913         
             ) o 
    LEFT OUTER JOIN IBSDEV1.ibs_ObjectRead objr 
    ON o.oid = objr.oid                          
    AND o.userId = objr.userId, IBSDEV1.ibs_User own, IBSDEV1.ibs_User u
    WHERE o.owner = own.id 
    AND o.oid = u.oid;
    -- v_UserContainer_01$contents