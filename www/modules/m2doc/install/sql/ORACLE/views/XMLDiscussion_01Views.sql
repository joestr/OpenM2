/******************************************************************************
 * All views regarding a xmldiscussion. <BR>
 * 
 * @version     $Id: XMLDiscussion_01Views.sql,v 1.6 2003/10/31 00:13:20 klaus Exp $
 *
 * @author      Keim Christine (CK)  001010
 ******************************************************************************
 */

-- create the new view:
CREATE OR REPLACE VIEW v_XMLDiscussion_01$content
AS
    SELECT v.*, '00000001' AS sortPath, b.discussionId
    FROM   v_Container$content v, m2_Article_01 b
    WHERE  v.oid = b.oid
    AND    v.containerId = b.discussionId
    UNION ALL
    SELECT v.*, v.posNoPath AS sortPath, b.discussionId
    FROM   v_Container$content v, m2_Article_01 b
    WHERE  v.oid = b.oid
    AND    (v.containerId <> b.discussionId)
;
-- v_XMLDiscussion_01$content


-- die Rechte sind noch zu beruecksichtigen
-- create the new view:
CREATE OR REPLACE VIEW v_XMLDiscussion_01$delcontent
AS
    SELECT  v.*, v.posNoPath AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE   v.oid = b.oid
    AND b.discussionId = v.containerId
;
-- v_XMLDiscussion_01$delcontent 

EXIT;
