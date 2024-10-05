 ------------------------------------------------------------------------------
 -- All views regarding a the reference-containercontent. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------
    -- Gets the data of the objects within a given container (incl. rights).

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_REFCONTAINER$RIGHTS');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_RefContainer$rights  
AS                                                  
    -- Get all objects and combine them with all users which have rights on 
    -- these objects.
    -- Attention: There are only those users considered which have defined 
    -- rights on an object. For users who have no rights defined on an object
    -- there are no tuples generated for this object.
    -- This means that the (java) queries on this view must consider that
    -- objects for which an user has no defined rights do not exist for this
    -- user!
    -- Take the defined rights for all users which are not owners and a
    -- special set of rights for the owner of an object. The owner is a pseudo 
    -- user with id 9437185.
    SELECT v.*, s.refCOid, s.refCrights, tRefVersionId      
    FROM   (       
            SELECT  o.containerId AS refCOid, 
                    o.oid AS refOid, 
                    o.tVersionId AS tRefVersionId,              
                    CASE 
                        WHEN (r.userId = 9437185) 
                        THEN o.owner 
                        ELSE r.userId 
                    END AS userId,              
                    r.rights AS refCrights      
            FROM    IBSDEV1.ibs_Object o, 
                IBSDEV1.ibs_RightsCum r      
            WHERE   o.containerKind = 2          
            AND o.state = 2          
            AND o.rKey = r.rKey          
            AND o.owner <> r.userId      
            ) s      
    JOIN IBSDEV1.v_Container$rights v ON v.userId = s.userId 
    AND s.refOid = v.containerId;
    -- v_RefContainer$rights

    -- Gets the data of the objects within a given container (incl. rights). 
    -- This view also returns if the user has already read the object.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_REFCONTAINER$RIGHTSREAD');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_RefContainer$rightsRead  
AS      
    SELECT  o.*,              
            1 - COALESCE (r.hasRead, 0) AS isNew      
    FROM    IBSDEV1.v_RefContainer$rights o              
    LEFT OUTER JOIN IBSDEV1.ibs_ObjectRead r ON o.oid = r.oid 
    AND o.userId = r.userId;
    -- v_RefContainer$rightsRead

    -- Gets the data of the objects within a given container (incl. rights).
    -- This view returns if the user has already read the object and the name of 
    -- the owner.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_REFCONTAINER_01$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_RefContainer_01$content  
AS 
    -- get links wich are physically in referenceContainer 
    SELECT s.* FROM  (          
                      SELECT  1 AS linkType, o.oid, o.state, o.name, o.typeName,
                               o.isLink, o.linkedObjectId, o.owner, o.ownerName,
                               o.ownerOid, o.ownerFullname, o.lastChanged, 
                               o.isNew, o.icon, o.description, o.flags, 
                               o.processState, o.userId, o.rights, o.containerId     
                      FROM     IBSDEV1.v_Container$content o      
                      WHERE    o.isLink = 1      
                      UNION ALL              
    -- get links wich are pointet on main object of referenceTab and
    -- and which are physically in any tab of other object
                      SELECT  2 AS linkType, o.oid, o.state, o.name, o.typeName,
                              o.isLink, o.oid AS linkedObjectId, o.owner, 
                              o.ownerName, o.ownerOid, o.ownerFullname, 
                              o.lastChanged, o.isNew, o.icon, o.description,
                              o.flags, o.processState, o.userId, o.rights,
                              refTab.oid AS containerId      
                      FROM    IBSDEV1.v_Container$content o, IBSDEV1.ibs_Object tab,
                              IBSDEV1.ibs_Object ref, IBSDEV1.ibs_Object refTab      
                      WHERE   o.oid = tab.containerId          
                      AND     tab.containerKind = 2          
                      AND     tab.oid = ref.containerId          
                      AND     ref.linkedObjectId = refTab.containerId
                      AND     ref.isLink = 1          
                      AND     ref.state = 2          
                      AND     refTab.containerKind = 2      
                      UNION ALL              
    -- get links wich are pointet on main object of referenceTab and
    -- and which are physically in any content of other object
                      SELECT  3 AS linkType, o.oid, o.state, o.name, o.typeName,
                              o.isLink, o.oid AS linkedObjectId, o.owner, 
                              o.ownerName, o.ownerOid, o.ownerFullname, 
                              o.lastChanged, o.isNew, o.icon, o.description,
                              o.flags, o.processState, o.userId, o.rights,
                              refTab.oid AS containerId      
                      FROM    IBSDEV1.v_Container$content o, IBSDEV1.ibs_Object ref, 
                              IBSDEV1.ibs_Object refTab      
                      WHERE   o.oid = ref.containerId          
                      AND     ref.linkedObjectId = refTab.containerId
                      AND     ref.isLink = 1          
                      AND     ref.state = 2          
                      AND     refTab.containerKind = 2  
                     ) s;
    -- v_RefContainer$content