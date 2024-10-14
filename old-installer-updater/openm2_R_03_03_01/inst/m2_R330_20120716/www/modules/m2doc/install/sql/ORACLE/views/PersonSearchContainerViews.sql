/******************************************************************************
 * All views regarding a person search container. <BR>
 *
 * @version     $Id: PersonSearchContainerViews.sql,v 1.3 2003/10/31 00:13:19 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 990407
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_PersonSearchCont_01$content
AS
    SELECT  o.*,
            p.fullname, p.prefix, p.title, p.position, p.company, 
            p.offemail AS email, p.offhomepage AS homepage
    FROM    v_Container$content o, mad_Person_01 p
    WHERE   p.oid = o.oid 
;
-- v_PersonSearchContainer_01$content

EXIT;
