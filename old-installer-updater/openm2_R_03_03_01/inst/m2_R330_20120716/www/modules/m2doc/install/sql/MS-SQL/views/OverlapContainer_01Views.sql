/******************************************************************************
 * All views regarding an overlap container. <BR>
 *
 * @version     $Id: OverlapContainer_01Views.sql,v 1.3 2006/01/19 15:56:48 klreimue Exp $
 *
 * @author      Andreas Jansa (AJ)  990311
 ******************************************************************************
 */
 
 /*****************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
 -- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_OverlapContainer$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_OverlapContainer$content
GO

-- create the new view:
CREATE VIEW v_OverlapContainer$content
AS
    SELECT  o.*, t.startDate AS startDate, 
            t.endDate AS endDate, 
            t.place AS place
    FROM    v_Container$content o, m2_Termin_01 t
    WHERE   t.oid = o.oid
GO
-- v_OverlapContainer$content
