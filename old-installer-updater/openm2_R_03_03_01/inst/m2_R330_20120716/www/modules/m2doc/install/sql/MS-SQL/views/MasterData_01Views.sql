/******************************************************************************
 * All views regarding a master data container. <BR>
 *
 * @version     $Id: MasterData_01Views.sql,v 1.5 2003/10/31 16:28:21 klaus Exp $
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
EXEC p_dropView 'v_MasterDataContainer_01$cont'
GO
-- create the new view:
CREATE VIEW v_MasterDataContainer_01$cont
AS
    SELECT  o.*, c.email, c.compowner
    FROM    (
                SELECT v.*, 
                        CASE WHEN isLink = 0 THEN oid ELSE linkedObjectId END AS joinOid
                FROM    v_Container$content v 
            ) o,
            (
                SELECT oid, '' AS compowner, offemail AS email
                FROM   mad_Person_01
                
                UNION
                
                SELECT f.oid, f.owner AS compowner, a.email AS email
                FROM    mad_Company_01 f, ibs_Object b, m2_Address_01 a
                WHERE   b.containerId = f.oid
                    AND b.containerKind = 2
                    AND b.tVersionId = 0x01012f01
                    AND a.oid = b.oid
                    
                UNION
                
                SELECT oid, '' AS compowner, '' AS email
                FROM    ibs_Object
                WHERE   tVersionId IN (SELECT tVersionId FROM ibs_DocumentTemplate_01)
            ) c
    WHERE c.oid = o.joinOid
GO
-- v_MasterDataContainer_01$cont
