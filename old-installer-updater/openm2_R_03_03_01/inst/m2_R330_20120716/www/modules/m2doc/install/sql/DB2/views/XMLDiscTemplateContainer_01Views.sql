------------------------------------------------------------------------------
-- All views regarding a xmldiscussiontemplate container. <BR>
-- 
-- @version     $Id: XMLDiscTemplateContainer_01Views.sql,v 1.3 2003/10/31 00:12:56 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
------------------------------------------------------------------------------
-- Gets the data of the objects within a given discussiontemplatecontainer
-- (incl. rights). <BR>
 
    -- delete existing view:
CALL IBSDEV1.p_dropView ('V_XMLDISCTEMPCONTAINER_01$CONT');
    -- create the new view:
CREATE VIEW IBSDEV1.v_XMLDiscTempContainer_01$cont   
AS      
    SELECT  v.*       
    FROM IBSDEV1.v_Container$content v      
    WHERE v.tVersionId = 16843537;
    -- v_XMLDiscTemplateContainer_01$cont