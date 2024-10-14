/******************************************************************************
 * All views regarding a master data container. <BR>
 *
 * @version     $Id: Person_01Views.sql,v 1.4 2003/10/31 16:27:56 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  990322
 ******************************************************************************
 */
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner. <BR>
 * It also returns the email address associated to the person or company.
 */
-- create the new view:
CREATE OR REPLACE VIEW v_PersonContainer_01$cont
AS
    SELECT  o.*,
            DECODE (DECODE (p.offemail, NULL, c.email, p.offemail), '', c.email,
                    DECODE (p.offemail, NULL, c.email, p.offemail))
            AS email
    FROM    (
                SELECT v.*, DECODE (isLink, 0, oid, linkedObjectId) AS joinOid
                FROM    v_Container$content v
            ) o,
            mad_Person_01 p,
            (
                SELECT  a.email, b.containerId 
                FROM    ibs_Object b, m2_Address_01 a
                WHERE   b.containerKind = 2
                    AND a.oid = b.oid
                    AND b.tVersionId = 16854785
            ) c
    WHERE o.joinOid = p.oid(+)
      AND o.joinOid = c.containerId(+)
;

EXIT;
