------------------------------------------------------------------------------
 -- All views regarding a container. <BR>
 -- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:58 $
--              $Author: klaus $
 --
 -- @author      Marcel Samek (MS)  020816
 ------------------------------------------------------------------------------


    -- Gets the data of the objects within a given container (incl. rights).

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_CONTAINER$RIGHTS');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Container$rights  
AS
    -- Get all objects and combine them with all users which have rights on 
    -- these objects.
    -- Attention: There are only those users considered which have defined 
    -- rights on an object. For users who have no rights defined on an object
    -- there are no tuples generated for this object.
    -- This means that the (java) queries on this view must consider that
    -- objects for which an user has no defined rights do not exist for this
    -- user!
    -- Take the defined rights for all users which are not owners and a
    -- special set of rights for the owner of an object. The owner is a pseudo 
    -- user with id 9437185.
    SELECT  o.containerId, o.containerKind, o.oid, o.state, o.name,
            o.tVersionId, o.typeName, o.isContainer, o.isLink, o.linkedObjectId,
            o.showInMenu, o.owner, o.lastChanged, o.posNoPath, o.icon, 
            o.description, o.flags, o.validUntil,
            CASE 
                WHEN (r.userId=9437185) 
                THEN o.owner 
                ELSE r.userId 
            END AS userId,
            o.processState, o.creationDate, r.rights
    FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_RightsCum r
    WHERE   o.containerKind <= 1
    AND     o.state = 2
    AND     o.rKey = r.rKey
    AND     o.owner <> r.userId;
    -- v_Container$rights

    -- Gets the data of the objects within a given container (incl. rights).
    -- This view also returns if the user has already read the object.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_CONTAINER$RIGHTSREAD');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Container$rightsRead
AS
    SELECT  o.*, 
            1 - CASE 
                    WHEN (r.hasRead IS NULL) 
                    THEN 0 
                    ELSE r.hasRead 
                END AS isNew
    FROM    IBSDEV1.v_Container$rights o
    LEFT OUTER JOIN IBSDEV1.ibs_ObjectRead r ON o.oid=r.oid and o.userId = r.userId;
    -- v_Container$rightsRead

    -- Gets the data of the objects within a given container (incl. rights).
    -- This view returns if the user has already read the object and the name of 
    -- the owner.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_CONTAINER$CONTENT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Container$content
AS
    SELECT  o.*, own.name AS ownerName, own.fullname AS ownerFullname, 
            own.oid AS ownerOid
    FROM    IBSDEV1.v_Container$rightsRead o, IBSDEV1.ibs_User own
    WHERE   o.owner = own.id;
    -- v_Container$content


    -- Gets the data of the objects within a given container (incl. rights).
    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_CONTAINER$RIGHTSSELLIST');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Container$rightsSelList
AS
    SELECT  o.oid, o.name, o.tVersionId, o.userId, o.rights, 
            t.posNoPath AS tPosNoPath 
    FROM    IBSDEV1.v_Container$rights o, IBSDEV1.ibs_TVersion tv, IBSDEV1.ibs_Type t 
    WHERE   o.tVersionId = tv.id 
    AND     tv.typeId = t.id;
    -- v_Container$rightsSelList


    -- A restricted version of v_Container$rights. <BR>
    -- Used only for v_ProductGroup_01$content. <BR>

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_CONTAINER2$RIGHTS');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_Container2$rights
AS
    SELECT  * 
    FROM    IBSDEV1.v_Container$rights;
    -- v_Container2$rights
