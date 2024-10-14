/******************************************************************************
 * The ibs workspace table incl. indexes. <BR>
 * The workspace table contains all currently existing user workspaces.
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Klaus Reimüller (KR)  980617
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_Workspace
(
    userId      USERID          NOT NULL PRIMARY KEY,
    domainId    DOMAINID        NOT NULL,
    workspace   OBJECTID        NOT NULL,
    workBox     OBJECTID        NOT NULL,
    outBox      OBJECTID        NOT NULL,
    inBox       OBJECTID        NOT NULL,
    news        OBJECTID        NOT NULL,
    hotList     OBJECTID        NOT NULL,
    profile     OBJECTID        NOT NULL,
    publicWsp   OBJECTID        NOT NULL,
    shoppingCart OBJECTID       NOT NULL,
    orders      OBJECTID        NOT NULL
)
GO
-- ibs_Workspace

