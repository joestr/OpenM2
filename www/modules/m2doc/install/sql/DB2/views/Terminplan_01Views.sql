------------------------------------------------------------------------------
-- All views regarding a TERMINPLAN container. <BR>
-- 
-- @version     $Id: Terminplan_01Views.sql,v 1.3 2003/10/31 00:12:56 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_TERMINPLAN_01$CONTENT');

    -- create the new view: 
CREATE VIEW  IBSDEV1.v_Terminplan_01$content  
AS      
    SELECT DISTINCT c.*, t.startDate, t.endDate, t.place       
    FROM            IBSDEV1.v_Container$content c, IBSDEV1.m2_Termin_01 t      
    WHERE           c.isLink = 0         
    AND             c.oid = t.oid        
    AND             c.oid = t.oid        
    UNION ALL        
    SELECT DISTINCT c.*, t.startDate, t.endDate, t.place       
    FROM            IBSDEV1.v_Container$content c, IBSDEV1.m2_Termin_01 t      
    WHERE           c.isLink = 1        
    AND             c.linkedObjectId = t.oid;
    -- v_Terminplan_01$content