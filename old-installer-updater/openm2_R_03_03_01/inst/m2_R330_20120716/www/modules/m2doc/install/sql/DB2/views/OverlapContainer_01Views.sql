------------------------------------------------------------------------------
-- All views regarding an overlap container. <BR>
-- 
-- @version     $Id: OverlapContainer_01Views.sql,v 1.3 2003/10/31 00:12:56 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_OVERLAPCONTAINER$CONTENT');
    -- create the new view: 
CREATE VIEW IBSDEV1.v_OverlapContainer$content  
AS      
    SELECT  o.*, t.startDate AS startDate, t.endDate AS endDate, 
            t.place AS place      
    FROM    IBSDEV1.v_Container$content o, 
        IBSDEV1.m2_Termin_01 t      
    WHERE   t.oid = o.oid;
    -- v_OverlapContainer$content