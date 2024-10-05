------------------------------------------------------------------------------
 -- TASK#1708 bidirectional links - update all views regarding a object. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------


    -- Gets the data of the objects within a given container (incl. rights).
    -- This view returns if the user has already read the object and the name of
    -- the owner.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_OBJECT$REFS');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Object$refs  
AS      
    SELECT  o.*, tabRef.containerid AS refCOid, tabRef.oid AS refOid,
            tabRef.tVersionId AS tRefVersionId      
    FROM    IBSDEV1.v_Container$content o, IBSDEV1.ibs_object tabRef      
    WHERE   o.containerId = tabRef.oid      
    AND     o.tVersionId = 16842801;
    -- v_Object$refs