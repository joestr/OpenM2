 ------------------------------------------------------------------------------
 -- All views regarding a SentObject container. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_SENTOBJECTCONT_01$CONTENT');

    -- create the new view: 
CREATE VIEW  IBSDEV1.v_SentObjectCont_01$content  
AS      
    SELECT  el.oid AS recipientContainerId, 
            so.distributeId AS distributeId,
            so.distributeName AS distributeName, 
            so.distributeTVersionId AS distributeTVersionId, 
            so.distributeTypeName AS distributeTypeName,              
            so.distributeIcon AS distributeIcon,              
            so.activities AS activities, 
            o.oid AS oid, 
            o.state AS state,
            o.name AS name, 
            o.creationDate AS creationDate, 
            o.typeName AS typeName, 
            o.isLink AS isLink, 
            o.linkedObjectId AS linkedObjectID, 
            o.owner AS owner, 
            c.ownerName AS ownerName, 
            c.ownerOid AS ownerOid, 
            c.ownerFullname,
            o.lastChanged AS lastchanged, 
            c.isNew, 
            o.icon, 
            o.description,
            c.rights, 
            c.flags AS flags, 
            c.containerId AS containerId,
            c.userId AS userId      
    FROM    IBSDEV1.ibs_Object el, IBSDEV1.ibs_Object o, IBSDEV1.ibs_SentObject_01 so, 
            IBSDEV1.v_Container$content c      
    WHERE   o.oid = c.oid          
    AND     so.oid = o.oid          
    AND     o.tversionId = 16850433          
    AND     el.tVersionId = 16849665          
    AND     el.containerId = o.oid          
    AND     userId = o.creator;
    -- v_SentObjectCont_01$content