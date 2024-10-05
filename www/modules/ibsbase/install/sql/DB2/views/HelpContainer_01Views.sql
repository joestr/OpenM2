------------------------------------------------------------------------------
 -- All views regarding a help container. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_HELPCONT_01$CONTENT');

    -- create the new view: 
CREATE VIEW  IBSDEV1.v_HelpCont_01$content  
AS      
    SELECT  o.*, h.goal
    FROM    IBSDEV1.v_Container$content o
    LEFT OUTER JOIN IBSDEV1.ibs_Help_01 h
    ON      o.oid = h.oid;
    -- v_HelpCont_01$content