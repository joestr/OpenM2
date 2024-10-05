/******************************************************************************
 * All views regarding an attachment container. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Andreas Jansa (AJ)  990315
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804    Code cleaning.
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */

-- create the new view
CREATE OR REPLACE VIEW v_AttachmentCont_01$content
AS
    SELECT  o.*,
            a.attachmentType, a.filename, a.url, a.path, a.filesize, a.isMaster,
            DECODE (a.attachmentType, 1, a.filename, 2, a.url) AS sourceName
    FROM    v_Container$content o, ibs_Attachment_01 a
    WHERE   o.oid = a.oid
;
-- v_AttachmentCont_01$content

EXIT;
