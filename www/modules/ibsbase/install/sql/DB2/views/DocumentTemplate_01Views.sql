-------------------------------------------------------------------------------
-- All views regarding the DocumentTemplate_01. <BR>
-- 
-- @version     $Id: DocumentTemplate_01Views.sql,v 1.3 2003/10/21 22:14:58 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
-------------------------------------------------------------------------------

    -- Gets the oid of all referenced document templates.
    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_DOCUMENTTEMPLATE_01$REF');

    -- create the new view: 
CREATE VIEW  IBSDEV1.v_DocumentTemplate_01$ref
AS
    SELECT s.oid 
    FROM  (
        -- select all document templates referenced by a object in the ibs_object
           SELECT DISTINCT t.oid
           FROM            IBSDEV1.ibs_DocumentTemplate_01 t, IBSDEV1.ibs_Object o
           WHERE           o.tversionId = t.tversionId 
           AND             o.state = 2
           UNION          
        -- select all document templates referenced by a xml viewer container object
           SELECT DISTINCT t.oid
           FROM          IBSDEV1.ibs_DocumentTemplate_01 t, IBSDEV1.ibs_XMLViewerContainer_01 x,
                         IBSDEV1.ibs_Object o
           WHERE         t.oid = x.templateOid
           AND           x.oid = o.oid 
           AND           o.state = 2
           UNION
        -- select all document templates referenced by the ibs_consistsOf table
           SELECT DISTINCT t.oid
           FROM     IBSDEV1.ibs_DocumentTemplate_01 t, IBSDEV1.ibs_ConsistsOf c, IBSDEV1.ibs_tab tab
           WHERE    t.tversionId = tab.tversionId AND tab.id = c.tabId
           ) s;
