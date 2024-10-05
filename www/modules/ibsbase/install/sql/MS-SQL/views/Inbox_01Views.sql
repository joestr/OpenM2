/******************************************************************************
 * All views regarding an inbox container. <BR>
 * 
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Andreas Jansa (AJ)  990315
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803   Code cleaning.
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */

-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_Inbox_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_Inbox_01$content
GO

-- create the new view:
CREATE VIEW  v_Inbox_01$content
AS
    SELECT  o.*,
            ro.distributedId, ro.distributedName, ro.activities, 
            ro.distributedTVersionId, ro.distributedTypeName, ro.sentObjectId, 
            ro.distributedIcon, ro.senderFullName AS sender
    FROM    v_Container$content o, ibs_ReceivedObject_01 ro
    WHERE   ro.oid = o.oid 
GO
-- v_Inbox_01$content
