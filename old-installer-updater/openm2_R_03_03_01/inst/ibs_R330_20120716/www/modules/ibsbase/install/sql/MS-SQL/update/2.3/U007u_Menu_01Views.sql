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