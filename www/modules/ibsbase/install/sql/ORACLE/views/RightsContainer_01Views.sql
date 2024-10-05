/******************************************************************************
 * All views regarding the RightsContainer. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Andreas Jansa (AJ)  990323
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_RightsContainer_01$personRig
AS
    SELECT  p.id, p.oid, p.name, p.typeName, ro.userId, ro.rights
    FROM    (
                SELECT  o.oid, r.rights,
                        DECODE (r.userId, 9437185, o.owner, r.userId) AS userId
                FROM    ibs_Object o, ibs_RightsCum r
                WHERE   o.containerKind = 1
                    AND o.state = 2
                    AND o.rKey = r.rKey
                    AND o.owner <> r.userId
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
;
-- v_RightsContainer$personRig


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_RightsContainer_01$content
AS
-- Quotation of KR remark:
-- "Die Rechteabfrage an dieser Stelle ist wohl nicht notwendig, wenn dieser View
-- ohnehin nur aufgerufen wird, wenn der Benutzer den Rechtereiter eines Objekts
-- aufruft, zu welchem bereits die Rechte geprüft wurden."
    SELECT  o.oid, o.state, o.oid AS rOid, ro.rPersonId, ro.rights AS rRights, 
            p.userId AS userId, 2147483647 AS rights,
            p.oid AS pOid, p.name AS pName, p.typeName AS pTypeName,
            p.rights AS pRights
    FROM    ibs_Object o, ibs_RightsKeys ro, 
            v_RightsContainer_01$personRig p
    WHERE   o.containerKind = 1
        AND o.rKey = ro.id
        AND p.id = ro.rPersonId 
;
-- v_RightsContainer$content

EXIT;
