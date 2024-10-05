------------------------------------------------------------------------------
-- All views regarding a discussion container. <BR>
-- 
-- @version     $Id: DiscussionContainer_01Views.sql,v 1.3 2003/10/31 00:12:56 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
------------------------------------------------------------------------------

    -- Gets the data of all new entries within a given discussion 
    -- (incl. rights).
    -- This view also returns if the user has already read the object.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_DiscussionCONTAINER_01$RIG');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_DiscussionContainer_01$rig  
AS
    SELECT COUNT (b.discussionId) AS num, b.discussionId, o.userId, o.isNew
    FROM   (
            SELECT oid, userId, isNew
            FROM   IBSDEV1.v_Container$rightsRead
            WHERE  tVersionId = 16843777    
                                            -- Thread
            AND    isNew = 1
            AND    IBSDEV1.b_AND(rights,6) > 0
            UNION ALL
            SELECT oid, userId, isNew
            FROM   IBSDEV1.v_Container$rightsRead
            WHERE  tVersionId = 16844033    
                                            -- Article
            AND    isNew = 1
            AND    IBSDEV1.b_AND(rights,6) > 0
            UNION ALL
            SELECT oid, userId, isNew
            FROM   IBSDEV1.v_Container$rightsRead
            WHERE  tVersionId = 16872721    
                                            -- DiscXMLViewer
            AND    isNew = 1
            AND    IBSDEV1.b_AND(rights,6) > 0
           ) o, IBSDEV1.m2_Article_01 b
    WHERE o.oid = b.oid
    GROUP BY b.discussionId, o.userId, o.isNew;
    -- v_DiscussionContainer_01$rig

    -- Gets the data of the objects within a given 
    -- discussionContainer (incl. rights). <BR>
    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_DiscussionCONTAINER_01$CONT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_DiscussionContainer_01$cont   
AS
    SELECT  d.*,COALESCE (km.num, 0) AS unknownMessages,
            COALESCE (km.isNew, 1) AS msgNew
    FROM   ( 
            SELECT s.*,  
                CASE s.isLink 
                    WHEN 1 
                    THEN s.linkedObjectId
                    ELSE s.oid
                END AS joinOid
            FROM IBSDEV1.v_Container$content s
            WHERE s.tVersionId IN(16843521, 
                                            -- Discussion
                                  16845313, 
                                            -- Black Board
                                  16842801, 
                                            -- Reference
                                  16843553  
                                            -- XMLDiscussion
                                  )
            ) d
    LEFT OUTER JOIN IBSDEV1.v_DiscussionContainer_01$rig km 
    ON km.discussionId = d.joinOid 
    AND km.userId = d.userId;
    -- v_DiscussionContainer_01$cont