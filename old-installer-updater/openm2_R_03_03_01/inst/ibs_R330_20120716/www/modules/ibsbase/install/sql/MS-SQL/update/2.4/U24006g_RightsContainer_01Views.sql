/******************************************************************************
 * All views regarding the RightsContainer. <BR>
 *
 * @version     $Id: U24006g_RightsContainer_01Views.sql,v 1.1 2005/02/15 21:38:48 klaus Exp $
 *
 * @author      Keim Christine (CK)  980615
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_RightsContainer_01$personRig'
GO

-- create the new view:
CREATE VIEW v_RightsContainer_01$personRig
AS

    SELECT  p.id, p.oid, p.name, p.typeName, ro.userId, ro.rights
    FROM    (
                SELECT  o.oid, r.rights, r.userId
                FROM    ibs_Object o, ibs_RightsCum r
                WHERE   o.containerKind = 1
                    AND o.state = 2
                    AND o.rKey = r.rKey
            ) ro,
            (
                SELECT  id, oid, name,
                        'User' AS typeName
                FROM    ibs_User
                WHERE   state = 2
                UNION ALL
                SELECT  id, oid, name, 
                        'Group' AS typeName
                FROM    ibs_Group
                WHERE   state = 2
            ) p
    WHERE   ro.oid = p.oid 
GO
-- v_RightsContainer$personRig


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_RightsContainer_01$content'
GO

-- create the new view:
CREATE VIEW v_RightsContainer_01$content
AS
-- Quotation of KR remark:
-- 'Die Rechteabfrage an dieser Stelle ist wohl nicht notwendig, wenn dieser View
-- ohnehin nur aufgerufen wird, wenn der Benutzer den Rechtereiter eines Objekts
-- aufruft, zu welchem bereits die Rechte geprüft wurden.'
    SELECT  o.oid, o.state, o.oid AS rOid, rks.rPersonId, rks.rights AS rRights, 
            p.userId AS userId, 2147483647 AS rights,
            p.oid AS pOid, p.name AS pName, p.typeName AS pTypeName,
            p.rights AS pRights
    FROM    ibs_Object o, ibs_RightsKey rk, ibs_RightsKeys rks,
            v_RightsContainer_01$personRig p
    WHERE   o.containerKind = 1
        AND o.rKey = rk.id
        AND rk.rkeysId = rks.id
        AND p.id = rks.rPersonId 
GO
-- v_RightsContainer$content
