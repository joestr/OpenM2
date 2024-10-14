/******************************************************************************
 * All views regarding a TERMINPLAN container. <BR>
 *
 * @version     $Id: Terminplan_01Views.sql,v 1.3 2003/10/31 00:13:09 klaus Exp $
 *
 * @author      Horst Pichler (HP) 000127
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */

-- delete existing view:
EXEC p_dropView 'v_Terminplan_01$content'
GO

-- create the new view:
CREATE VIEW  v_Terminplan_01$content
AS
    SELECT DISTINCT c.*, t.startDate, t.endDate, t.place 
    FROM   v_Container$content c, m2_Termin_01 t
    WHERE  c.isLink = 0 
      AND  c.oid = t.oid
      AND  c.oid = t.oid

    UNION ALL

    SELECT DISTINCT c.*, t.startDate, t.endDate, t.place 
    FROM   v_Container$content c, m2_Termin_01 t
    WHERE  c.isLink = 1
      AND  c.linkedObjectId = t.oid
GO
-- v_Terminplan_01$content

