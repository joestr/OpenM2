/******************************************************************************
 * All views regarding a TERMINPLAN container. <BR>
 *
 * @version     $Id: Terminplan_01Views.sql,v 1.4 2003/10/31 00:13:19 klaus Exp $
 *
 * @author      Horst Pichler (HP) 000127
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_Terminplan_01$content 
AS
    SELECT DISTINCT c.*, t.startDate, t.endDate, t.place 
    FROM   v_Container$content c, m2_Termin_01 t
    WHERE  c.isLink = 0 
      AND  c.oid = t.oid
    UNION ALL
    SELECT DISTINCT c.*, t.startDate, t.endDate, t.place 
    FROM   v_Container$content c, m2_Termin_01 t
    WHERE  c.isLink = 1
      AND  c.linkedObjectId = t.oid
;
-- v_Terminplan_01$content

EXIT;
