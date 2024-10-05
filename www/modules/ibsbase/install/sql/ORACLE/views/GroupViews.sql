/******************************************************************************
 * All views regarding the Group. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Klaus Reimüller (KR) 990407
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_Group_01$rights
AS
    SELECT  s.id, s.domainId, s.fullname, 
            gu.groupId, gu.origGroupId, gu.idPath, 
            g.oid AS containerId, o.oid, o.state, o.name, 
            o.tVersionId, o.typeName, o.owner, o.lastChanged, 
            o.icon, o.userId, o.rights, o.flags
    FROM    v_Container$rights o, ibs_Group g,
            (   SELECT groupId, origGroupId, userId, idPath
                FROM ibs_GroupUser
                WHERE groupId = origGroupId
            ) gu,     
            (   SELECT oid, id, fullname, domainId
                FROM    ibs_User
                UNION
                SELECT  oid, id, name AS fullname, domainId
                FROM    ibs_Group) s
    WHERE   o.oid = s.oid
        AND gu.groupId = g.id
        AND s.id = gu.userId
;
-- v_Group_01$rights


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view also returns if the user has already read the object.
 */
CREATE OR REPLACE VIEW v_Group_01$rightsRead
AS
    SELECT  o.*,
            1 - DECODE (r.hasRead, NULL, 0, r.hasRead) AS isNew 
    FROM    v_Group_01$rights o, ibs_ObjectRead r
    WHERE   o.oid = r.oid(+) AND o.userId = r.userId(+)
;
-- v_Group_01$rightsRead


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner.
 */
CREATE OR REPLACE VIEW v_Group_01$content
AS
    SELECT  o.*, own.name AS ownerName,
            own.fullname AS ownerFullname, own.oid AS ownerOid,
            0 AS isLink, '0000000000000000' AS linkedObjectId, '' AS description            
    FROM    v_Group_01$rightsRead o, ibs_User own 
    WHERE   (o.owner = own.id(+))
;
-- v_Group_01$content

EXIT;
