------------------------------------------------------------------------------
 -- View V_MENU_01$CONTENT. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

-- delete existing view: 
CALL IBSDEV1.p_dropView ('V_MENU_01$CONTENT');

-- create the new view: 
CREATE VIEW IBSDEV1.v_Menu_01$content  
AS      
    SELECT  obj.*,               
            1 AS isNew,                     
                                             -- not necessary for menu
            own.name AS ownerName,
            own.oid AS ownerOid,
            own.fullname AS ownerFullname
    FROM    (
             SELECT  o.containerId, o.containerKind, o.oid, o.state, o.name,
                     o.tVersionId, o.typeName, o.isContainer, o.isLink, 
                     o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged,
                     o.posNoPath, o.icon, o.description, o.flags, o.validUntil,
                     r.userId AS userId, o.processState, o.creationDate, 
                     r.rights
             FROM    IBSDEV1.ibs_Object o, 
                  IBSDEV1.ibs_RightsCum r
             WHERE   o.state = 2
             AND     o.rKey = r.rKey                                  
             ) obj, IBSDEV1.ibs_User own      
    WHERE   obj.state = 2          
    AND obj.owner = own.id;
    -- v_Menu_01$content