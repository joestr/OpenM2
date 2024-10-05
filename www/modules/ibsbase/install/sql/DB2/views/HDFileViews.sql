 ------------------------------------------------------------------------------
 -- View getting all the data regarding all HDFiles starting from a root. <BR>
 -- 
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------
 

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_FILESHD$CONTENT');
    -- create the new view: 
CREATE VIEW IBSDEV1.v_FilesHD$content  
AS      
    SELECT  o.oid, 
            CASE                      
                WHEN (f.attachmentType = 3) 
                THEN o.oid
                ELSE o.containerId
                END AS containerId,
            f.fileName, f.path, root.oid AS rootOid, f.attachmentType, 
            o.tVersionId
            FROM IBSDEV1.ibs_Attachment_01 f 
            JOIN IBSDEV1.ibs_Object o ON o.oid = f.oid, IBSDEV1.ibs_Object root
            WHERE f.attachmentType <> 2
            AND o.state = 2
            AND fileSize <> 0.0
            AND o.posNoPath LIKE root.posNoPath || '%';
    -- v_FilesHD$content