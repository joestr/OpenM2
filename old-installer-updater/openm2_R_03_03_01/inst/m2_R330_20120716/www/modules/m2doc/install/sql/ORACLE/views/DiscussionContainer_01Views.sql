/******************************************************************************
 * All views regarding a discussion container. <BR>
 *
 * @version     $Id: DiscussionContainer_01Views.sql,v 1.7 2003/10/31 00:13:19 klaus Exp $
 *
 * @author      Andreas Jansa (AJ) 990322
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of all new entries within a given discussion (incl. rights). <BR>
 * This view also returns if the user has already read the object.
 */
CREATE OR REPLACE VIEW v_DiscussionContainer_01$rig
AS
    SELECT COUNT (b.discussionId) AS num, b.discussionId, 
           o.userId, o.isNew
    FROM   (
             SELECT oid, userId, isNew
             FROM v_Container$rightsRead
             WHERE tVersionId = 16843777 -- Thread
             AND isNew = 1
             AND B_AND (rights, 6) > 0
             UNION ALL
             SELECT oid, userId, isNew
             FROM v_Container$rightsRead
             WHERE tVersionId = 16844033 -- Article
             AND isNew = 1
             AND B_AND (rights, 6) > 0
             UNION ALL
             SELECT oid, userId, isNew
             FROM v_Container$rightsRead
             WHERE tVersionId = 16872721  -- DiscXMLViewer
             AND isNew = 1
             AND B_AND (rights, 6) > 0
            ) o, m2_Article_01 b
    WHERE o.oid = b.oid  
    GROUP BY b.discussionId, o.userId, o.isNew
;
-- v_DiscussionContainer_01$rig


/******************************************************************************
 * Gets the data of the objects within a given discussion (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_DiscussionContainer_01$cont 
AS
    SELECT  d.*, DECODE (km.num, NULL, 0, km.num) AS unknownMessages, 
            DECODE (km.isNew, NULL, 1, km.isNew) AS msgNew
    FROM    
            ( SELECT s.*, DECODE (islink,1,linkedobjectId,oid) AS Joid
              FROM v_Container$content s
              WHERE s.tVersionId IN 
	      (
	      16843521, -- Discussion
	      16845313, -- Black Board
	      16842801, -- Reference
	      16843553  -- XMLDiscussion
	      )
	    ) d, v_DiscussionContainer_01$rig km
        WHERE km.discussionId(+) = d.Joid 
        AND km.userId(+) = d.userId
;
-- v_DiscussionContainer_01$cont

EXIT;
