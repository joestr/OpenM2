/******************************************************************************
 * IBS-801 DB Script to delete xmldata.xml file paths from ibs_Attachment_01
 * 
 * This scipt deletes all paths to xmldata.xml files from ibs_Attachment_01. The
 * files are not needed any more.
 *
 * Repeatable:  yes (to repeat the script increment the post fix of the backup table name)
 * 
 * @version     $Id: U320006u_delXMLData_01.sql,v 1.0 2012/06/28 16:45:00 gweiss Exp $
 *
 * @author      Gottfried Weiﬂ (GW)  20120628
 ******************************************************************************/

PRINT 'starting U320006u_delXMLData_01.sql'
GO
 
-- don't show count messages:
SET NOCOUNT ON
GO

-- backup the data rows
SELECT *
INTO backup_ibs_801_delXMLData_001
FROM ibs_Attachment_01
WHERE filename LIKE N'xmldata%.xml'
GO

-- delete the data rows
DELETE FROM ibs_Attachment_01
WHERE filename LIKE N'xmldata%.xml'
GO

-- show count messages again:
SET NOCOUNT ON
GO

PRINT 'U320006u_delXMLData_01.sql finished'
GO
