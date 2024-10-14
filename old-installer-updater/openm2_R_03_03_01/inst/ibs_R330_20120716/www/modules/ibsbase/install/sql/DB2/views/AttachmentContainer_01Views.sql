 ------------------------------------------------------------------------------
 -- All views regarding an attachment container. <BR>
 -- 
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- Gets the data of the objects within a given container (incl. rights).

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_ATTACHMENTCONT_01$CONTENT');

    -- create the new view: 
CREATE VIEW  IBSDEV1.v_AttachmentCont_01$content
AS
    SELECT  o.*, a.attachmentType, a.filename, a.url, a.path, a.filesize, 
            a.isMaster, CASE a.attachmentType
                            WHEN 1 
                            THEN a.filename
                            WHEN 2 
                            THEN a.url
                        END AS sourceName
    FROM    IBSDEV1.v_Container$content o, IBSDEV1.ibs_Attachment_01 a
    WHERE   o.oid = a.oid;
    -- v_AttachmentCont_01$content