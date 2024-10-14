/******************************************************************************
 * View getting all the data regarding all HDFiles starting from a root. <BR>
 *
 * @version     1.10.0001, 05.05.2000
 *
 * @author      Keim Christine (CK)  000505
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the files within a given root (without rights). <BR>
 */

-- create the new view:
CREATE OR REPLACE VIEW v_FilesHD$content
AS
    SELECT  o.oid, DECODE (f.attachmentType, 3, o.oid, o.containerId) AS containerId, f.fileName, f.path, root.oid AS rootOid, f.attachmentType, o.tVersionId
    FROM ibs_Attachment_01 f ,ibs_Object o , ibs_Object root
    WHERE f.attachmentType <> 2
    AND o.oid = f.oid
    AND fileSize <> 0.0
    AND o.posNoPath LIKE root.posNoPath || '%'
;
-- v_FilesHD$content

EXIT;
