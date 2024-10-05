/******************************************************************************
 * All views regarding the GroupContainer. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Klaus Reimüller (KR) 990407
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner.
 */
CREATE OR REPLACE VIEW v_GroupContainer_01$content
AS
    SELECT  o.*,
            g.domainId, g.name AS fullname, g.id
    FROM    v_Container$content o, ibs_Group g
    WHERE   o.oid = g.oid
    AND     tVersionId = 16842929
;
-- v_GroupContainer_01$content

EXIT;
