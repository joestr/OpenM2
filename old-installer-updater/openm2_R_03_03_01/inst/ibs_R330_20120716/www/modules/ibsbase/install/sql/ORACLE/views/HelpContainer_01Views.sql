/******************************************************************************
 * All views regarding a help container. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Harald Buzzi (HB) 990614
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */

-- create the new view:
CREATE OR REPLACE VIEW  v_HelpCont_01$content
AS
    SELECT o.*, h.goal
    FROM v_Container$content o, ibs_Help_01 h
    WHERE   o.oid = h.oid(+);
EXIT;
-- v_HelpCont_01$content

