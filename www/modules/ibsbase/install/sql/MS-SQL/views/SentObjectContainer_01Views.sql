/******************************************************************************
 * All views regarding a SentObject container. <BR>
 *
 * @version     1.10.0001, 19.02.2000
 *
 * @author      Mario Stegbauer  000219
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */

-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_SentObjectCont_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_SentObjectCont_01$content
GO

-- create the new view:
CREATE VIEW  v_SentObjectCont_01$content
AS
    SELECT  el.oid AS recipientContainerId,
            so.distributeId AS distributeId,
            so.distributeName AS distributeName,
            so.distributeTVersionId AS distributeTVersionId,
            so.distributeTypeName AS distributeTypeName,
            so.distributeIcon AS distributeIcon,
            so.activities AS activities,
            o.oid AS oid,
            o.state AS state, 
            o.name AS name,
            o.creationDate AS creationDate,
            o.typeCode AS typeCode,
            o.typeName AS typeName, 
            o.isLink AS isLink,
            o.linkedObjectId AS linkedObjectID,
            o.owner AS owner,
            c.ownerName AS ownerName,
            c.ownerOid AS ownerOid,
            c.ownerFullname, 
            o.lastChanged AS lastchanged,
            c.isNew,
            o.icon,
            o.description,
            c.rights,
            c.flags AS flags,
            c.containerId AS containerId,
            c.userId AS userId
    FROM    ibs_Object el, ibs_Object o, ibs_SentObject_01 so, v_Container$content c
    WHERE   o.oid = c.oid
        AND so.oid = o.oid
        AND o.tversionId = 16850433
        AND el.tVersionId = 16849665
        AND el.containerId = o.oid
        AND userId = o.creator
GO
-- v_SentObjectCont_01$content
