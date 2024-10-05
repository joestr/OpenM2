------------------------------------------------------------------------------
 -- All views regarding a recipient container. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------


    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_RECIPIENTCONT_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_RecipientCont_01$content  
AS      
    SELECT  o.*, r.recipientName, r.readDate, r.recipientId, r.sentObjectId,
            so.distributeId, so.distributeIcon, so.distributeName      
    FROM    IBSDEV1.v_Container$content o, 
       IBSDEV1.ibs_Recipient_01 r, IBSDEV1.ibs_SentObject_01 so
    WHERE   o.oid = r.oid              
    AND     r.sentObjectId = so.oid;
    -- v_RecipientCont_01$content