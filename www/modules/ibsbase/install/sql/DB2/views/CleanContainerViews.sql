 ------------------------------------------------------------------------------
 -- All views regarding a cleancontainer. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- Gets the data of the objects which can be cleaned. <BR>
    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_CLEANCONTAINER_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_CleanContainer_01$content
AS
    SELECT  *
    FROM    IBSDEV1.v_Container$content
    WHERE ((CURRENT TIMESTAMP - validUntil) < 0);
    -- v_CleanContainer_01$content