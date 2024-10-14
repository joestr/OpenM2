/******************************************************************************
 * All views regarding an attachment container. <BR>
 *
 * @version     1.01.0001, 21.03.1999
 *
 * @author      Andreas Jansa (AJ)  990315
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_AttachmentCont_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_AttachmentCont_01$content
GO

-- create the new view:
CREATE VIEW  v_AttachmentCont_01$content
AS
    SELECT  o.*,
            a.attachmentType, a.filename, a.url, a.path, a.filesize, a.isMaster,
            CASE a.attachmentType 
                WHEN 1 THEN a.filename
                WHEN 2 THEN a.url
            END AS sourceName
    FROM    v_Container$content o, ibs_Attachment_01 a
    WHERE   o.oid = a.oid
GO
-- v_AttachmentCont_01$content
