/******************************************************************************
 * All views regarding a xmldiscussiontemplate container. <BR>
 *
 * @version     $Id: XMLDiscTemplateContainer_01Views.sql,v 1.3 2006/01/19 15:56:48 klreimue Exp $
 *
 * @author      Keim Christine (CK)  000927
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects within a given discussiontemplatecontainer
 * (incl. rights). <BR>
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_XMLDiscTempContainer_01$cont') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_XMLDiscTempContainer_01$cont
GO

-- create the new view:
CREATE VIEW v_XMLDiscTempContainer_01$cont 
AS
    SELECT  v.* 
    FROM v_Container$content v
    WHERE v.tVersionId = 0x01010311
GO
-- v_XMLDiscTemplateContainer_01$cont

