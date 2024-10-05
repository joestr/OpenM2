/******************************************************************************
 * All views regarding a help container. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Mario Stegbauer  990607
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */

-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_HelpCont_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_HelpCont_01$content
GO

-- create the new view:
CREATE VIEW  v_HelpCont_01$content
AS
    SELECT  o.*, h.goal
    FROM    v_Container$content o
    LEFT OUTER JOIN ibs_Help_01 h
        ON o.oid = h.oid
GO
-- v_HelpCont_01$content
 