 ------------------------------------------------------------------------------
 -- All views regarding the RightsContainer. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_RIGHTSCONTAINER_01$PERSONRIG');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_RightsContainer_01$personRig  
AS        
    SELECT  p.id, p.oid, p.name, p.typeName, ro.userId, ro.rights      
    FROM   (                  
            SELECT  o.oid, r.rights,                          
                    CASE WHEN (r.userId = 9437185) 
                         THEN o.owner 
                         ELSE r.userId 
                         END AS userId       
            FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r                  
            WHERE   o.containerKind = 1                      
            AND     o.state = 2                      
            AND     o.rKey = r.rKey                      
            AND     o.owner <> r.userId 
            ) ro,              
           (                  
             SELECT  id, oid, name, 'User' AS typeName                  
             FROM    IBSDEV1.ibs_User                  
             WHERE   state = 2                  
             UNION ALL                  
             SELECT  id, oid, name, 'Group' AS typeName                  
             FROM    IBSDEV1.ibs_Group                  
             WHERE   state = 2              
            ) p      
    WHERE   ro.oid = p.oid;
    -- v_RightsContainer$personRig



    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_RIGHTSCONTAINER_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_RightsContainer_01$content  
AS      
    -- Quotation of KR remark:
    -- "Die Rechteabfrage an dieser Stelle ist wohl nicht notwendig, 
    -- wenn dieser View
    -- ohnehin nur aufgerufen wird, wenn der Benutzer 
    -- den Rechtereiter eines Objekts
    -- aufruft, zu welchem bereits die Rechte geprüft wurden."
    SELECT  o.oid, o.state, o.oid AS rOid, ro.rPersonId, ro.rights AS rRights,
            p.userId AS userId, 2147483647 AS rights, p.oid AS pOid, 
            p.name AS pName, p.typeName AS pTypeName, p.rights AS pRights     
    FROM    IBSDEV1.ibs_Object o, 
         IBSDEV1.ibs_RightsKeys ro, IBSDEV1.v_RightsContainer_01$personRig p          
    WHERE   o.containerKind = 1          
    AND     o.rKey = ro.id          
    AND     p.id = ro.rPersonId;
-- v_RightsContainer$content