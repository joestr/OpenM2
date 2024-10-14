/******************************************************************************
 * All views regarding the GroupContainer. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Keim Christine (CK)  980615
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner.
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_GroupContainer_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_GroupContainer_01$content
GO

-- create the new view:
CREATE VIEW v_GroupContainer_01$content
AS
    SELECT  o.*,
            g.domainId, g.name AS fullname, g.id
    FROM    v_Container$content o, ibs_Group g
    WHERE   o.oid = g.oid
    AND     tVersionId = 16842929
GO
-- v_GroupContainer_01$content
