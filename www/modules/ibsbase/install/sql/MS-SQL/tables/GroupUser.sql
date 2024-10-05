/******************************************************************************
 * The ibs groupuser table incl. indexes. <BR>
 * The groupuser table contains all relations between groups and users.
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Keim Christine (CK)  980715
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_GroupUser
(
    id           ID              NOT NULL PRIMARY KEY, -- id of the relation
    state        STATE           NOT NULL, -- state of object
    groupId      GROUPID         NOT NULL, -- id of group
    userId       USERID          NOT NULL,  -- id of user/group
    roleId       ROLEID          NULL,  -- id of role
    origGroupId  GROUPID         NOT NULL, -- id of the original group
    idPath       POSNOPATH       NOT NULL -- idPath of the Object
)
GO
-- ibs_GroupUser
