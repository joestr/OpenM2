 ------------------------------------------------------------------------------
 -- All views regarding the Group. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- Gets the data of the objects within a given container (incl. rights). 

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_GROUP_01$RIGHTS');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Group_01$rights  
AS      
    SELECT  s.id, s.domainId, s.fullname, gu.groupId, gu.origGroupId, gu.idPath, 
            g.oid AS containerId, o.oid, o.state, o.name, o.tVersionId, 
            o.typeName, o.owner, o.lastChanged, o.icon, o.userId, o.rights, 
            o.flags
    FROM    IBSDEV1.v_Container$rights o, IBSDEV1.ibs_Group g,
            (
             SELECT  groupId, origGroupId, idPath, userId 
             FROM    IBSDEV1.ibs_GroupUser 
             WHERE   groupId = origGroupId
             ) gu,
            (
             SELECT oid, id, fullname, domainId
             FROM   IBSDEV1.ibs_User
             UNION
             SELECT oid, id, name AS fullname, domainId
             FROM   IBSDEV1.ibs_Group
             ) s
    WHERE  o.oid = s.oid
    AND gu.groupId = g.id
    AND s.id = gu.userId;
    -- v_Group_01$rights


    -- Gets the data of the objects within a given container (incl. rights).
    -- This view also returns if the user has already read the object.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_GROUP_01$RIGHTSREAD');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Group_01$rightsRead  
AS      
    SELECT  o.*,
            CASE r.hasRead 
                WHEN 0 
                THEN 1 
                WHEN 1 
                THEN 0 
            END AS isNew
    FROM    IBSDEV1.v_Group_01$rights o
    LEFT OUTER JOIN IBSDEV1.ibs_ObjectRead r ON o.oid = r.oid 
    AND o.userId = r.userId;
    -- v_Group_01$rightsRead

    -- Gets the data of the objects within a given container (incl. rights). 
    -- This view returns if the user has already read the object and the name of 
    -- the owner.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_GROUP_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Group_01$content  
AS      
    SELECT  o.*, own.name AS ownerName, own.fullname AS ownerFullname, 
            own.oid AS ownerOid, 0 AS isLink, 
            '0000000000000000' AS linkedObjectId, '' AS description
    FROM   IBSDEV1.v_Group_01$rightsRead o
    LEFT OUTER JOIN IBSDEV1.ibs_User own ON o.owner = own.id;
    -- v_Group_01$content