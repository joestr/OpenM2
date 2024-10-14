 ------------------------------------------------------------------------------
 -- All views regarding the GroupContainer. <BR>
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
CALL IBSDEV1.p_dropView ('V_GROUPCONTAINER_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_GroupContainer_01$content  
AS
    SELECT  o.*, g.domainId, g.name AS fullname, g.id
    FROM    IBSDEV1.v_Container$content o, IBSDEV1.ibs_Group g
    WHERE   o.oid = g.oid
    AND     tVersionId = 16842929;
    -- v_GroupContainer_01$content