/******************************************************************************
 * All views regarding a xmldiscussion. <BR>
 * 
 * @version     $Id: XMLDiscussion_01Views.sql,v 1.7 2003/10/31 00:13:09 klaus Exp $
 *
 * @author      Keim Christine (CK)  001010
 ******************************************************************************
 */
-- delete existing view:
EXEC p_dropView 'v_XMLDiscussion_01$content'
GO

-- create the new view:
CREATE VIEW v_XMLDiscussion_01$content
AS
    SELECT  v.*, 0x00000001 AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE v.oid = b.oid
    AND v.containerId = b.discussionId

    UNION ALL

    SELECT  v.*, v.posNoPath AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE v.oid = b.oid
    AND (v.containerId <> b.discussionId)
GO
-- v_XMLDiscussion_01$content


-- delete existing view:
EXEC p_dropView 'v_XMLDiscussion_01$delcontent'
GO

-- die Rechte sind noch zu beruecksichtigen
-- create the new view:
CREATE VIEW v_XMLDiscussion_01$delcontent
AS
    SELECT  v.*, v.posNoPath AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE v.oid = b.oid
    AND b.discussionId = v.containerId
GO
-- v_XMLDiscussion_01$delcontent 