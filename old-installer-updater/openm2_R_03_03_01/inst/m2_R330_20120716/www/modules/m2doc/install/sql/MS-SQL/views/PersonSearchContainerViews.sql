/******************************************************************************
 * All views regarding a person search container. <BR>
 *
 * @version     $Id: PersonSearchContainerViews.sql,v 1.4 2006/01/19 15:56:48 klreimue Exp $
 *
 * @author      Andreas Jansa (AJ)  981218
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_PersonSearchCont_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_PersonSearchCont_01$content
GO

-- create the new view:
CREATE VIEW v_PersonSearchCont_01$content
AS
    SELECT  o.*,
            p.fullname, p.prefix, p.title, p.position, p.company, 
            p.offemail AS email, p.offhomepage AS homepage
    FROM    v_Container$content o, mad_Person_01 p
    WHERE   p.oid = o.oid 
GO
-- v_PersonSearchContainer_01$content
