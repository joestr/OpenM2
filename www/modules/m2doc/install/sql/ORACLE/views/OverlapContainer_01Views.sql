/******************************************************************************
 * All views regarding an overlap container. <BR>
 *
 * @version     $Id: OverlapContainer_01Views.sql,v 1.2 2003/10/31 00:13:19 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  990315
 ******************************************************************************
 */
 
 /******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_OverlapContainer$content
AS
    SELECT  o.*, t.startDate AS startDate, 
            t.endDate AS endDate, 
            t.place AS place
    FROM    v_Container$content o, m2_Termin_01 t
    WHERE   t.oid = o.oid
;
-- v_OverlapContainer$content

EXIT;
