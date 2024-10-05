/******************************************************************************
 * All views regarding a container. <BR>
 *
 * @version     1.10.0001, 02.08.1999
 *
 * @author     Andreas Jansa (AJ)  990322
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_Container$rights
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
    -- predefined set of rights (0x7FFFFFFF) for the owner of an object.
    -- The owner is a pseudo user with id 0x00900001.
    SELECT  o.containerId, o.containerKind, o.oid, o.state, o.name, 
            o.tVersionId, o.typeName, o.isContainer, o.isLink, 
            o.linkedObjectId, o.showInMenu, o.owner, o.lastChanged, 
            o.posNoPath, o.icon, o.description, o.flags, o.validUntil,
            DECODE (r.userId, 9437185, o.owner, r.userId) AS userId,
            o.processState, o.creationDate, r.rights
    FROM    ibs_Object o, ibs_RightsCum r
    WHERE   o.containerKind <= 1
        AND o.state = 2
        AND o.rKey = r.rKey
        AND o.owner <> r.userId
;
-- v_Container$rights


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view also returns if the user has already read the object.
 */
CREATE OR REPLACE VIEW v_Container$rightsRead
AS
    SELECT  o.*,
            1 - DECODE (r.hasRead, NULL, 0, r.hasRead) AS isNew 
    FROM    v_Container$rights o, ibs_ObjectRead r
    WHERE   o.oid = r.oid(+) AND o.userId = r.userId(+)
;
-- v_Container$rightsRead


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 * This view returns if the user has already read the object and the name of 
 * the owner.
 */
CREATE OR REPLACE VIEW v_Container$content
AS 
    SELECT  o.*, own.name AS ownerName, 
            own.fullname AS ownerFullname, own.oid AS ownerOid
    FROM    v_Container$rightsRead o, ibs_User own 
    WHERE   o.owner = own.id
/*    
    -- CHANGED due to performance reasons:
    -- the outer join is not necessary, because user-objects are never
    -- physically deleted.
    -- if physically deleted: objects would 'disappear'.
    WHERE   (o.owner = own.id(+))
*/
;
-- v_Container$content


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_Container$rightsSelList
AS
    SELECT  cr.oid, cr.name, cr.tVersionId, cr.userId, cr.rights, t.posNoPath AS tPosNoPath
    FROM    v_Container$rights cr, ibs_TVersion tv, ibs_Type t
    WHERE   cr.tVersionId = tv.id(+)
      AND   tv.typeId = t.id(+)
;
-- v_Container$rightsSelList


/******************************************************************************
 * A restricted version of v_Container$rights. <BR>
 * Used only for v_ProductGroup_01$content. <BR>
 */
CREATE OR REPLACE VIEW v_Container2$rights
AS
    SELECT  *
    FROM    v_Container$rights
;
-- v_Container2$rights

EXIT;
