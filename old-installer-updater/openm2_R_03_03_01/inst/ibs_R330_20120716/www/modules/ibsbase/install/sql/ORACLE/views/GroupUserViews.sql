/******************************************************************************
 * All views regarding the Group. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Andreas Jansa (AJ)  990701
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */
 
 
 /******************************************************************************
 * Gets the data all users and groups in the database). <BR>
 * 1. part -> users
 * 2. part -> groups
 */
CREATE OR REPLACE VIEW v_GroupUser$getAll
AS
    -- user part
    SELECT DISTINCT v.oid AS oid, v.state AS state, v.name AS name, v.userId, 
            v.rights, v.typeName, v.isLink, v.linkedObjectId, v.owner,
            ' ' AS ownerName, '0000000000000000' AS ownerOid, ' ' AS ownerFullname, 
            v.lastChanged, 0 AS isNew, v.icon, v.description, v.flags,
            u.fullname
    FROM    v_Container$rights v, ibs_User u
    WHERE   v.oid = u.oid 
      AND   v.state = 2
      AND   u.state = 2
      AND   v.tVersionId = 16842913 -- tversion id of user objects
    UNION
    -- group part
    SELECT DISTINCT v.oid AS oid, v.state AS state, v.name AS name, v.userId, 
            v.rights, v.typeName, v.isLink, v.linkedObjectId, v.owner,
            ' ' AS ownerName, '0000000000000000' AS ownerOid, ' ' AS ownerFullname,
            v. lastChanged, 0 AS isNew, v.icon, v.description, v.flags,
            g.name
    FROM    v_Container$rights v, ibs_Group g
    WHERE   v.oid = g.oid
      AND   v.state = 2
      AND   g.state = 2
      AND   v.tVersionId = 16842929 -- tversion id of group objects
;
-- v_GroupUser$getAll

exit;
