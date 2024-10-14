------------------------------------------------------------------------------
 -- All views regarding the TVersion table. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_TVERSION$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_TVersion$content  
AS      
    SELECT  t.id AS typeId, t.name AS typeName, t.code AS typeCode,
            tv.id AS id, tv.className, tv.superTVersionId AS superTVersionId,
            tv.posNoPath AS posNoPath, dt.oid AS oid      
    FROM    IBSDEV1.ibs_Type t              
    INNER JOIN IBSDEV1.ibs_tVersion tv ON tv.typeId = t.id              
    LEFT OUTER JOIN IBSDEV1.ibs_DocumentTemplate_01 dt ON tv.id = dt.tVersionId;
    -- v_TVersion$content