/******************************************************************************
 * The ibs_Attachment_01 indexes and triggers. <BR>
 * The ibs_Attachment_01 table contains the values for the base object Attachment_01.
 * 
 * @version     $Id: Attachment_01.sql,v 1.4 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Heinz Stampfer (HP)  980513
 ******************************************************************************
 */
CREATE TABLE ibs_Attachment_01
(
    oid         OBJECTID        NOT NULL UNIQUE,
    filename    NVARCHAR (255),
    path        NVARCHAR (255),
    filesize    real,
    url         NVARCHAR (255),
    attachmentType  int,
    isMaster    BOOL
)
GO
-- ibs_Attachment_01
