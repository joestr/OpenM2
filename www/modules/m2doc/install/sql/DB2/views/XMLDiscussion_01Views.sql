-------------------------------------------------------------------------------
-- All views regarding a xmldiscussion. <BR>
-- 
-- @version     $Id: XMLDiscussion_01Views.sql,v 1.3 2003/10/31 00:12:56 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
-------------------------------------------------------------------------------
 
-- delete existing view:
CALL IBSDEV1.p_dropView ('V_XMLDISCUSSION_01$CONTENT');

-- create the new view:
CREATE VIEW IBSDEV1.v_XMLDiscussion_01$content  
AS
    SELECT  v.*, '00000001' AS sortPath, b.discussionId      
    FROM    IBSDEV1.v_Container$content v, IBSDEV1.m2_Article_01 b      
    WHERE   v.oid = b.oid
        AND v.containerId = b.discussionId        
    UNION ALL
    SELECT  v.*, v.posNoPath 
    AS      sortPath, b.discussionId
    FROM    IBSDEV1.v_Container$content v, IBSDEV1.m2_Article_01 b
    WHERE   v.oid = b.oid
        AND (v.containerId <> b.discussionId);
-- v_XMLDiscussion_01$content


-- delete existing view:
CALL IBSDEV1.p_dropView ('V_XMLDISCUSSION_01$DELCONTENT');

-- die Rechte sind noch zu beruecksichtigen
-- create the new view:
CREATE VIEW IBSDEV1.v_XMLDiscussion_01$delcontent  
AS      
    SELECT  v.*, v.posNoPath AS sortPath, b.discussionId      
    FROM    IBSDEV1.v_Container$content v, IBSDEV1.m2_Article_01 b
    WHERE   v.oid = b.oid 
        AND b.discussionId = v.containerId;
-- v_XMLDiscussion_01$delcontent 
