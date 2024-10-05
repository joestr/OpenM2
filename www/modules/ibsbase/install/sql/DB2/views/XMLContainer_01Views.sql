------------------------------------------------------------------------------
 -- All views regarding a xml-container. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:59 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------
    -- delete existing view:
CALL IBSDEV1.p_dropView ('V_XMLCONTAINER_01$CONTENT');
    -- create the new view: 
CREATE VIEW IBSDEV1.v_XMLContainer_01$content  
AS          
    SELECT  v.oid 
    AS      refOid, v.*      
    FROM    IBSDEV1.v_Container$content v      
    WHERE   v.isLink = 0      
    UNION ALL      
    SELECT  v.linkedObjectId 
    AS      refOid, v.*      
    FROM    IBSDEV1.v_Container$content v      
    WHERE   v.isLink = 1;
    -- v_XMLContainer_01$content

