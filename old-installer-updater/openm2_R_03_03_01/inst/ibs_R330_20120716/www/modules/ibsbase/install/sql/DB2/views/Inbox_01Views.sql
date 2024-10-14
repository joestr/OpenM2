------------------------------------------------------------------------------
 -- All views regarding an inbox container. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------


    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_INBOX_01$CONTENT');
    -- create the new view: 
CREATE VIEW  IBSDEV1.v_Inbox_01$content  
AS      
    SELECT  o.*, ro.distributedId, ro.distributedName, ro.activities,
            ro.distributedTVersionId, ro.distributedTypeName, 
            ro.sentObjectId, ro.distributedIcon, ro.senderFullName AS sender
    FROM    IBSDEV1.v_Container$content o, IBSDEV1.ibs_ReceivedObject_01 ro      
    WHERE   ro.oid = o.oid;
    -- v_Inbox_01$content