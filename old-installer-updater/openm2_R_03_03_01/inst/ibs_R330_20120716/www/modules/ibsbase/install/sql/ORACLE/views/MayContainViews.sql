/******************************************************************************
 * All views regarding the type 'MayContain'. <BR>
 *
 * @version     2.10.0001, 17.05.2000
 *
 * @author      Rahul Soni (RS)  000517
 ******************************************************************************
 */

/******************************************************************************
 * Get the may contain data for all types. <BR>
 * This view gets the class name and tVersionId for all versions of types being
 * in the ibs_MayContain table.
 * For the minor types the type name is computed, too.
 */
-- create the new view:
CREATE OR REPLACE VIEW v_MayContain$content 
AS
    SELECT  tv1.className AS majorClassName, tv1.id AS majorTVersionId,
            tv2.className AS minorClassName, tv2.id AS minorTVersionId,
            t.name AS minorName
    FROM    ibs_MayContain m, ibs_TVersion tv1, ibs_Type t, ibs_TVersion tv2
    WHERE   m.majorTypeId = tv1.typeId
        AND m.minorTypeId = tv2.typeId
        AND tv2.id = t.actVersion
        AND tv1.className <> 'undefined'
;
-- v_MayContain$content 
show errors;

EXIT;
