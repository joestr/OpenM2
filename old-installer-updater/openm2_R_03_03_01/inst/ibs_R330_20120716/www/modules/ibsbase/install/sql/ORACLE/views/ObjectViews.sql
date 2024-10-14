/******************************************************************************
 * TASK#1708 bidirectional links - update all views regarding a object. <BR>
 *
 * @version     2.2.1.0001, 22.11.2001
 *
 * @author      Andreas Jansa (AJ)  011122
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of
 * the owner.
 */
CREATE OR REPLACE VIEW v_Object$refs
AS
    SELECT  o.*, tabRef.containerid AS refCOid, tabRef.oid AS refOid,
            tabRef.tVersionId AS tRefVersionId
    FROM    v_Container$content o, ibs_object tabRef
    WHERE   o.containerId = tabRef.oid
    AND     o.tVersionId = 16842801;
-- v_Object$refs

EXIT;