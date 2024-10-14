/******************************************************************************
 * The ibs domain table incl. indexes. <BR>
 * The domain table contains all currently running domains.
 * 
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Klaus Reimüller (KR)  980725
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_Domain_01
(
    id              DOMAINID        NOT NULL PRIMARY KEY,
    oid             OBJECTID        NULL,
    scheme          ID              NOT NULL, -- scheme of the domain
    workspaceProc   NVARCHAR (63)   NULL, -- procedure for creating a workspace
                                           -- within this domain
    adminId         USERID          NOT NULL, -- default domain administrator
    adminGroupId    GROUPID         NOT NULL, -- group for domain administrators
    allGroupId      GROUPID         NOT NULL, -- group for all users
    userAdminGroupId GROUPID        NOT NULL, -- group for administring users 
                                              -- and groups
    structAdminGroupId GROUPID      NOT NULL, -- group for administring the 
                                              -- structure
    groupsOid       OBJECTID        NOT NULL, -- container for the groups
    usersOid        OBJECTID        NOT NULL, -- container for the users
    publicOid       OBJECTID        NOT NULL, -- public container of domain
    workspacesOid   OBJECTID        NOT NULL, -- container for workspaces of
                                              -- domain
    homepagePath    NAME            NULL, -- path of the homepage of the domain
    logo            NAME            NULL, -- logo for the domain
    sslRequired     BOOL            NOT NULL DEFAULT (0) -- ssl for the domain
)
GO
-- ibs_Domain_01
