/******************************************************************************
 * All views regarding the DocumentTemplate_01. <BR>
 *
 * @version     $Id: DocumentTemplate_01Views.sql,v 1.5 2003/10/19 23:47:05 klaus Exp $
 *
 * @author      Michael Steiner (MS)
 ******************************************************************************
 */

/*
 * Gets the oid of all referenced document templates.
 */
 
-- delete existing view:
EXEC p_dropView 'v_DocumentTemplate_01$ref'
GO

-- create the new view:
CREATE VIEW  v_DocumentTemplate_01$ref
AS
    SELECT s.oid FROM
    (
    -- select all document templates referenced by a object in the ibs_object
    SELECT DISTINCT t.oid
    FROM ibs_DocumentTemplate_01 t, ibs_Object o
    WHERE o.tversionId = t.tversionId AND o.state = 2
    UNION
    -- select all document templates referenced by a xml viewer container object
    SELECT DISTINCT t.oid
    FROM ibs_DocumentTemplate_01 t, ibs_XMLViewerContainer_01 x, ibs_Object o
    WHERE t.oid = x.templateOid
    AND x.oid = o.oid AND o.state = 2
    UNION
    -- select all document templates referenced by the ibs_consistsOf table
    SELECT DISTINCT t.oid
    FROM ibs_DocumentTemplate_01 t, ibs_ConsistsOf c, ibs_Tab tab
    WHERE t.tversionId = tab.tversionId AND tab.id = c.tabId
    ) s
GO
