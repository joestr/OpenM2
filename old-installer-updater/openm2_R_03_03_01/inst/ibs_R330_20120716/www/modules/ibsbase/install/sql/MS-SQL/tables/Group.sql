/******************************************************************************
 * The ibs group table incl. indexes. <BR>
 * The group table contains all currently existing groups.
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Keim Christine (CK)  980706
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_Group
(
    id          GROUPID         NOT NULL PRIMARY KEY, -- <domainid>001{21*0|1}
    oid         OBJECTID        NOT NULL, -- object id of group
    state       STATE           NOT NULL, -- state of object
    domainId    DOMAINID        NOT NULL, -- domain where the group resides
    name        NAME            NOT NULL  -- group name
)
GO
-- ibs_Group
