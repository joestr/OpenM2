/******************************************************************************
 * View getting all the data regarding all HDFiles starting from a root. <BR>
 *
 * @version     $Id: HDFileViews.sql,v 1.4 2012/06/29 13:59:08 gweiss Exp $
 *
 * @author      Keim Christine (CK)  000504
 *
 * <DT><B>Updates:</B>
 * <DD>GW 120629	Get all child objects, test for files in java code using the flags (necessary for getting rid of xmldata files)
 ******************************************************************************
 */
 
PRINT 'starting U320006v_HDFileViews.sql'
GO

/******************************************************************************
 * Gets the data of the files within a given root (without rights). <BR>
 */
-- delete existing view:
p_dropView N'v_FilesHD$content'
GO

-- create the new view:
CREATE VIEW v_FilesHD$content
AS
    SELECT o.oid,
		CASE 
			WHEN (f.fileName IS NULL ) THEN o.oid
			ELSE o.containerId 
		END AS containerId,
		f.fileName, f.path, root.oid AS rootOid, o.tVersionId, o.flags
	FROM
		ibs_Object o LEFT OUTER JOIN 
			(SELECT a.oid, a.fileName, a.path, a.fileSize
				FROM ibs_Attachment_01 a
				WHERE
				a.attachmentType <> 2
				AND a.fileSize <> 0.0
			) f
	ON o.oid = f.oid,
	ibs_Object root
	WHERE o.state = 2
	AND o.posNoPath LIKE root.posNoPath + '%'
GO
-- v_FilesHD$content

PRINT 'U320006v_HDFileViews.sql finished'
GO

