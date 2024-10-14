/******************************************************************************
 * All views regarding a discussion. <BR>
 * 
 * @version     $Id: Discussion_01Views.sql,v 1.7 2003/10/31 00:13:19 klaus Exp $
 *
 * @author      Keim Christine (CK)  990602
 ******************************************************************************
 */

-- create the new view:
CREATE OR REPLACE VIEW v_Discussion_01$content
AS
    -- get all 'Thread'-objects
    SELECT  v.*, '00000001' AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE   v.oid = b.oid
      AND   v.tVersionId = 16843777
    UNION ALL
    -- get 'Article'-objects
    SELECT  v.*, v.posNoPath AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE   v.oid = b.oid
      AND   v.tVersionId = 16844033
;
-- v_Discussion_01$content


-- create the new view:
CREATE OR REPLACE VIEW v_Discussion_01$delcontent
AS
    SELECT  v.*, '0' AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE v.tVersionId =   16843777  -- Thread
    AND v.oid = b.oid
    AND b.discussionId = v.containerId
;
-- v_Discussion_01$delcontent 

EXIT;
