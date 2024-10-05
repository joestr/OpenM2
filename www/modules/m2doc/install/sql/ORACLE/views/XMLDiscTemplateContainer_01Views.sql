/******************************************************************************
 * All views regarding a xmldiscussiontemplate container. <BR>
 *
 * @version     $Id: XMLDiscTemplateContainer_01Views.sql,v 1.2 2003/10/31 00:13:19 klaus Exp $
 *
 * @author      Keim Christine (CK)  000927
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given discussiontemplatecontainer
 * (incl. rights). <BR>
 */
 
-- create the new view:
CREATE OR REPLACE VIEW v_XMLDiscTempContainer_01$cont 
AS
    SELECT  v.* 
    FROM    v_Container$content v
    WHERE   v.tVersionId = 16843537
;
-- v_XMLDiscTemplateContainer_01$cont

EXIT;
