/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$content'
GO
 
CREATE VIEW v_Menu_01$content
AS
    SELECT  o.containerId, o.oid, o.name, o.posNoPath,
            o.isLink, o.linkedObjectId, o.icon, o.oLevel,
            r.userId AS userId, r.rights
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.state = 2
        AND o.showInMenu = 1 
        AND o.rKey = r.rKey
GO
-- v_Menu_01$content


/******************************************************************************
 * Get all menu objects being upto one level below. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels1'
GO
 
CREATE VIEW v_Menu_01$menuLevels1
AS
    SELECT  containerId AS oid1, oid
    FROM    ibs_Object
    WHERE   state = 2
        AND showInMenu = 1
GO
-- v_Menu_01$menuLevels1


/******************************************************************************
 * Get all menu objects being upto one level below and also check if there
 * are some subnodes. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels1Sub'
GO
 
CREATE VIEW v_Menu_01$menuLevels1Sub
AS
    SELECT  o.*, 1 AS hasSubNodes
    FROM    v_Menu_01$menuLevels1 o
    WHERE   EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
    UNION ALL
    SELECT  o.*, 0 AS hasSubNodes
    FROM    v_Menu_01$menuLevels1 o
    WHERE   NOT EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
GO
-- v_Menu_01$menuLevels1Sub


/******************************************************************************
 * Get all menu objects being upto two levels below. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels2'
GO
 
CREATE VIEW v_Menu_01$menuLevels2
AS
    SELECT  containerOid2 AS oid2, containerId AS oid1, oid
    FROM    ibs_Object
    WHERE   state = 2
        AND showInMenu = 1
GO
-- v_Menu_01$menuLevels2


/******************************************************************************
 * Get all menu objects being upto two levels below and also check if there
 * are some subnodes. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels2Sub'
GO
 
CREATE VIEW v_Menu_01$menuLevels2Sub
AS
    SELECT  o.*, 1 AS hasSubNodes
    FROM    v_Menu_01$menuLevels2 o
    WHERE   EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
    UNION ALL
    SELECT  o.*, 0 AS hasSubNodes
    FROM    v_Menu_01$menuLevels2 o
    WHERE   NOT EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
GO
-- v_Menu_01$menuLevels2Sub


/******************************************************************************
 * Get all menu objects being upto three levels below. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels3'
GO
 
CREATE VIEW v_Menu_01$menuLevels3
AS
    SELECT  o1.containerOid2 AS oid3, o1.containerId AS oid2,
            o1.oid AS oid1, o.oid
    FROM    ibs_Object o1, ibs_Object o
    WHERE   o1.oid = o.containerId
        AND o1.state = 2
        AND o.state = 2
        AND o.showInMenu = 1
GO
-- v_Menu_01$menuLevels3


/******************************************************************************
 * Get all menu objects being upto three levels below and also check if there
 * are some subnodes. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels3Sub'
GO
 
CREATE VIEW v_Menu_01$menuLevels3Sub
AS
    SELECT  o.*, 1 AS hasSubNodes
    FROM    v_Menu_01$menuLevels3 o
    WHERE   EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
    UNION ALL
    SELECT  o.*, 0 AS hasSubNodes
    FROM    v_Menu_01$menuLevels3 o
    WHERE   NOT EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
GO
-- v_Menu_01$menuLevels3Sub


/******************************************************************************
 * Get all menu objects being upto four levels below. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels4'
GO
 
CREATE VIEW v_Menu_01$menuLevels4
AS
    SELECT  o2.containerOid2 AS oid4, o2.containerId AS oid3,
            o2.oid AS oid2, o1.oid AS oid1, o.oid
    FROM    ibs_Object o2, ibs_Object o1, ibs_Object o
    WHERE   o2.oid = o1.containerId
        AND o1.oid = o.containerId
        AND o2.state = 2
        AND o1.state = 2
        AND o.state = 2
        AND o.showInMenu = 1
GO
-- v_Menu_01$menuLevels4


/******************************************************************************
 * Get all menu objects being upto four levels below and also check if there
 * are some subnodes. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels4Sub'
GO
 
CREATE VIEW v_Menu_01$menuLevels4Sub
AS
    SELECT  o.*, 1 AS hasSubNodes
    FROM    v_Menu_01$menuLevels4 o
    WHERE   EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
    UNION ALL
    SELECT  o.*, 0 AS hasSubNodes
    FROM    v_Menu_01$menuLevels4 o
    WHERE   NOT EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
GO
-- v_Menu_01$menuLevels4Sub


/******************************************************************************
 * Get all menu objects being upto five levels below. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels5'
GO
 
CREATE VIEW v_Menu_01$menuLevels5
AS
    SELECT  o3.containerOid2 AS oid5, o3.containerId AS oid4,
            o3.oid AS oid3, o2.oid AS oid2, o1.oid AS oid1, o.oid
    FROM    ibs_Object o3, ibs_Object o2, ibs_Object o1, ibs_Object o
    WHERE   o3.oid = o2.containerId
        AND o2.oid = o1.containerId
        AND o1.oid = o.containerId
        AND o3.state = 2
        AND o2.state = 2
        AND o1.state = 2
        AND o.state = 2
        AND o.showInMenu = 1
GO
-- v_Menu_01$menuLevels5


/******************************************************************************
 * Get all menu objects being upto five levels below and also check if there
 * are some subnodes. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevels5Sub'
GO
 
CREATE VIEW v_Menu_01$menuLevels5Sub
AS
    SELECT  o.*, 1 AS hasSubNodes
    FROM    v_Menu_01$menuLevels5 o
    WHERE   EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
    UNION ALL
    SELECT  o.*, 0 AS hasSubNodes
    FROM    v_Menu_01$menuLevels5 o
    WHERE   NOT EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
GO
-- v_Menu_01$menuLevels5Sub


/******************************************************************************
 * Get all menu objects being somewhere below. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevelsN'
GO
 
CREATE VIEW v_Menu_01$menuLevelsN
AS
    SELECT  o1.oid AS oidN, o1.posNoPath AS posNoPathN, o2.oid AS oid
    FROM    ibs_Object o1, ibs_Object o2
    WHERE   o2.posNoPath LIKE o1.posNoPath + '%'
        AND o1.state = 2
        AND o2.state = 2
        --AND o1.showInMenu = 1
        AND o2.showInMenu = 1
GO
-- v_Menu_01$menuLevelsN


/******************************************************************************
 * Get all menu objects being somewhere below and also check if there
 * are some subnodes. <BR>
 */
-- deletes the view if allready exists
EXEC p_dropView 'v_Menu_01$menuLevelsNSub'
GO
 
CREATE VIEW v_Menu_01$menuLevelsNSub
AS
    SELECT  o.*, 1 AS hasSubNodes
    FROM    v_Menu_01$menuLevelsN o
    WHERE   EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
    UNION ALL
    SELECT  o.*, 0 AS hasSubNodes
    FROM    v_Menu_01$menuLevelsN o
    WHERE   NOT EXISTS
            (
                SELECT  *
                FROM    v_Menu_01$menuLevels1 osub
                WHERE   osub.oid1 = o.oid
            )
GO
-- v_Menu_01$menuLevelsNSub
