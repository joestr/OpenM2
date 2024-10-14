------------------------------------------------------------------------------
 -- All views regarding the Group. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------

    -- Gets the data all users and groups in the database). <BR>
    -- 1. part -> users
    -- 2. part -> groups

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_GROUPUSER$GETALL');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_GroupUser$getAll  
AS      
    SELECT DISTINCT v.oid AS oid, v.state AS state, v.name AS name, v.userId,
                    v.rights, v.typeName, v.isLink, v.linkedObjectId, v.owner,
                    ' ' AS ownerName, '0000000000000000' AS ownerOid, 
                    ' ' AS ownerFullname, v.lastChanged, 0 AS isNew, v.icon, 
                    v.description, v.flags, u.fullname
    FROM            IBSDEV1.v_Container$rights v, IBSDEV1.ibs_User u
    WHERE           v.oid = u.oid
    AND             v.state = 2
    AND             u.state = 2
    AND             v.tVersionId = 16842913 -- tversion id of user objects
    UNION
    -- group part
    SELECT DISTINCT v.oid AS oid, v.state AS state, v.name AS name, v.userId,
                    v.rights, v.typeName, v.isLink, v.linkedObjectId, v.owner,
                    ' ' AS ownerName, '0000000000000000' AS ownerOid, 
                    ' ' AS ownerFullname, v. lastChanged, 0 AS isNew, 
                    v.icon, v.description, v.flags,
                    g.name AS fullname
    FROM            IBSDEV1.v_Container$rights v, IBSDEV1.ibs_Group g
    WHERE           v.oid = g.oid
    AND             v.state = 2
    AND             g.state = 2
    AND             v.tVersionId = 16842929;
                                             -- tversion id of group objects
