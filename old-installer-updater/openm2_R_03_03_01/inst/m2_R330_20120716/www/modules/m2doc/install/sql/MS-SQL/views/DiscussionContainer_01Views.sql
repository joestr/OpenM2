/******************************************************************************
 * All views regarding a discussion container. <BR>
 *
 * @version     $Id: DiscussionContainer_01Views.sql,v 1.6 2006/01/19 15:56:47 klreimue Exp $
 *
 * @author      Keim Christine (CK)  980507
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of all new entries within a given discussion (incl. rights). <BR>
 * This view also returns if the user has already read the object.
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_DiscussionContainer_01$rig') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_DiscussionContainer_01$rig
GO

-- create the new view:
CREATE VIEW v_DiscussionContainer_01$rig
AS
    SELECT COUNT (b.discussionId) AS num, b.discussionId, 
           o.userId, o.isNew
    FROM   (
             SELECT oid, userId, isNew
             FROM v_Container$rightsRead
             WHERE tVersionId = 0x01010401 -- Thread
             AND isNew = 1
             AND (rights & 0x00000006) > 0

             UNION ALL

             SELECT oid, userId, isNew
             FROM v_Container$rightsRead
             WHERE tVersionId = 0x01010501 -- Article
             AND isNew = 1
             AND (rights & 0x00000006) > 0

             UNION ALL

             SELECT oid, userId, isNew
             FROM v_Container$rightsRead
             WHERE tVersionId = 0x01017511  -- DiscXMLViewer
             AND isNew = 1
             AND (rights & 0x00000006) > 0
            ) o, m2_Article_01 b
    WHERE o.oid = b.oid  
    GROUP BY b.discussionId, o.userId, o.isNew

GO
-- v_DiscussionContainer_01$rig


/******************************************************************************
 * Gets the data of the objects within a given discussionContainer (incl. rights). <BR>
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_DiscussionContainer_01$cont') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_DiscussionContainer_01$cont
GO

-- create the new view:
CREATE VIEW v_DiscussionContainer_01$cont 
AS
    SELECT  d.*, 
            COALESCE (km.num, 0) AS unknownMessages, 
            COALESCE (km.isNew, 1) AS msgNew
    FROM   ( SELECT s.*,  CASE s.isLink WHEN 1 THEN s.linkedObjectId 
                                    ELSE s.oid
                                    END AS joinOid
             FROM v_Container$content s
             WHERE s.tVersionId IN 
	     (
	     0x01010301, -- Discussion
	     0x01010A01, -- Black Board
	     0x01010031, -- Reference
	     0x01010321  -- XMLDiscussion
	     )
	   ) d
           LEFT OUTER JOIN
           v_DiscussionContainer_01$rig km
           ON km.discussionId = d.joinOid AND km.userId = d.userId
GO
-- v_DiscussionContainer_01$cont
