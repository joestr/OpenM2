CREATE OR REPLACE VIEW v_RefContainer$rights
AS  
SELECT v.*, s.refCOid, s.refCrights, tRefVersionId                              
    FROM                                                                        
    (                                                                           
    SELECT  o.containerId AS refCOid, o.oid AS refOid, o.tVersionId AS tRefVersionId,                                                                           
            DECODE (r.userId, 9437185, o.owner, r.userId) AS userId,            
            r.rights AS refCrights                                              
    FROM    ibs_Object o, ibs_RightsCum r                                       
    WHERE   o.containerKind = 2                                                 
        AND o.state = 2                                                         
        AND o.rKey = r.rKey                                                     
        AND o.owner <> r.userId                                                 
   ) s, v_Container$rights v                                                    
   WHERE v.userId = s.userId                                                    
   AND s.refOid = v.containerId;

show errors;
                                                                                
 
CREATE OR REPLACE VIEW v_RefContainer$rightsRead
AS
SELECT  o.*,                                                                    
            1 - DECODE (r.hasRead, NULL, 0, r.hasRead) AS isNew                 
    FROM    v_RefContainer$rights o, ibs_ObjectRead r                           
    WHERE   o.oid = r.oid(+) AND o.userId = r.userId(+);                         
                                                       
show errors;


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of
 * the owner.
 */
CREATE OR REPLACE VIEW v_RefContainer_01$content
AS
    SELECT  s.*
    FROM
    (
        -- get links which are physically in ReferenceContainer:
        SELECT  1 AS linkType,
                o.oid, o.state, o.name, o.typeName, o.isLink,
                o.linkedObjectId, o.owner, o.ownerName, o.ownerOid,
                o.ownerFullname, o.lastChanged, o.isNew, o.icon, o.description,
                o.flags, o.processState, o.userId, o.rights, o.containerId
        FROM    v_Container$content o
        WHERE   o.isLink = 1
        UNION ALL
        -- get links wich are pointet on main object of referenceTab and
        -- and which are physically in any tab of other object
        SELECT  2 AS linkType,
                o.oid, o.state, o.name, o.typeName, o.isLink,
                o.oid AS linkedObjectId, o.owner, o.ownerName, o.ownerOid,
                o.ownerFullname, o.lastChanged, o.isNew, o.icon, o.description,
                o.flags, o.processState, o.userId, o.rights,
                refTab.oid AS containerId
        FROM    v_Container$content o, ibs_Object tab,
                ibs_Object ref, ibs_Object refTab
        WHERE   o.oid = tab.containerId
            AND tab.containerKind = 2
            AND tab.oid = ref.containerId
            AND ref.linkedObjectId = refTab.containerId
            AND ref.isLink = 1
            AND ref.state = 2
            AND refTab.containerKind = 2
        UNION ALL
        -- get links wich are pointet on main object of referenceTab and
        -- and which are physically in any content of other object
        SELECT  3 AS linkType,
                o.oid, o.state, o.name, o.typeName, o.isLink,
                o.oid AS linkedObjectId, o.owner, o.ownerName, o.ownerOid,
                o.ownerFullname, o.lastChanged, o.isNew, o.icon, o.description,
                o.flags, o.processState, o.userId, o.rights,
                refTab.oid AS containerId
        FROM    v_Container$content o, ibs_Object ref, ibs_Object refTab
        WHERE   o.oid = ref.containerId
            AND ref.linkedObjectId = refTab.containerId
            AND ref.isLink = 1
            AND ref.state = 2
            AND refTab.containerKind = 2
    ) s;
                          
show errors;

EXIT;
