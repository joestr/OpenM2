/******************************************************************************
 * All views regarding a master data container. <BR>
 *
 * @version     $Id: Person_01Views.sql,v 1.5 2006/01/19 15:56:48 klreimue Exp $
 *
 * @author      Christine Keim (CK)     98????
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner. <BR>
 * It also returns the email address associated to the person or company.
 */
-- delete existing view:
IF EXISTS ( SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_PersonContainer_01$cont')
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_PersonContainer_01$cont
GO

-- create the new view:
CREATE VIEW v_PersonContainer_01$cont
AS
    SELECT  o.*, 
            CASE 
                WHEN (COALESCE (p.offemail, c.email) = '') THEN c.email
                ELSE COALESCE (p.offemail, c.email) 
            END AS email
    FROM    (
                SELECT v.*, 
                        CASE WHEN isLink = 0 THEN oid ELSE linkedObjectId END AS joinOid
                FROM    v_Container$content v 
            ) o
    LEFT JOIN mad_Person_01 p 
        ON  p.oid = o.joinOid
    LEFT JOIN
            (
                SELECT  a.email, b.containerId 
                FROM    ibs_Object b, m2_Address_01 a
                WHERE   b.containerKind = 2
                    AND a.oid = b.oid
                    AND b.tVersionId = 0x01012f01
            ) c
        ON  c.containerId = o.joinOid
GO
-- v_PersonContainer_01$cont
