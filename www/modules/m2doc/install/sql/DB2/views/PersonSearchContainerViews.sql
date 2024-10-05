------------------------------------------------------------------------------
-- All views regarding a person search container. <BR>
-- 
-- @version     $Id: PersonSearchContainerViews.sql,v 1.3 2003/10/31 00:12:56 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_PERSONSEARCHCONT_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_PersonSearchCont_01$content  
AS      
    SELECT  o.*, p.fullname, p.prefix, p.title, p.position, p.company,
            p.offemail AS email, p.offhomepage AS homepage      
    FROM    IBSDEV1.v_Container$content o, IBSDEV1.mad_Person_01 p      
    WHERE   p.oid = o.oid;
    -- v_PersonSearchContainer_01$content