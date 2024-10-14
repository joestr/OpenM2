/******************************************************************************
 * All views regarding a discussion. <BR>
 * 
 * @version     $Id: Discussion_01Views.sql,v 1.6 2006/01/19 15:56:47 klreimue Exp $
 *
 * @author      Keim Christine (CK)  980507
 ******************************************************************************
 */

-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_Discussion_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_Discussion_01$content
GO

-- create the new view:
CREATE VIEW v_Discussion_01$content
AS
    -- get all 'Thread'-objects
    SELECT  v.*, 0x00000001 AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE   v.oid = b.oid
      AND   v.tVersionId = 0x01010401
    UNION ALL
    -- get 'Article'-objects
    SELECT  v.*, v.posNoPath AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE   v.oid = b.oid
      AND   v.tVersionId = 0x01010501
GO
-- v_Discussion_01$content


-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_Discussion_01$delcontent') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_Discussion_01$delcontent
GO

-- die Rechte sind noch zu beruecksichtigen
-- create the new view:
CREATE VIEW v_Discussion_01$delcontent
AS
    SELECT  v.*, 0x00000001 AS sortPath, b.discussionId
    FROM    v_Container$content v, m2_Article_01 b
    WHERE v.tVersionId = 0x01010401  -- Thread
    AND v.oid = b.oid
    AND b.discussionId = v.containerId
GO
-- v_Discussion_01$delcontent 

