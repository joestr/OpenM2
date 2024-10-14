------------------------------------------------------------------------------
 -- All views regarding the type 'MayContain'. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- Get the may contain data for all types. <BR>
    -- This view gets the class name and tVersionId for 
    -- all versions of types being in the ibs_MayContain table.
    -- For the minor types the type name is computed, too.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_MAYCONTAIN$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_MayContain$content   
AS      
    SELECT  tv1.className AS majorClassName, tv1.id AS majorTVersionId,
            tv2.className AS minorClassName, tv2.id AS minorTVersionId,
            t.name AS minorName
    FROM    IBSDEV1.ibs_MayContain m, 
         IBSDEV1.ibs_TVersion tv1, IBSDEV1.ibs_Type t, IBSDEV1.ibs_TVersion tv2
    WHERE   m.majorTypeId = tv1.typeId
    AND     m.minorTypeId = tv2.typeId
    AND     tv2.id = t.actVersion
    AND     tv1.className <> 'undefined';
    -- v_MayContain$content