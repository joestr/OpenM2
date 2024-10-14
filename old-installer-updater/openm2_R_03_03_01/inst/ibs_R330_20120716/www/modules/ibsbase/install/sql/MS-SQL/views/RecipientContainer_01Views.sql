/******************************************************************************
 * All views regarding a recipient container. <BR>
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
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_RecipientCont_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_RecipientCont_01$content
GO

-- create the new view:
CREATE VIEW v_RecipientCont_01$content
AS
    SELECT  o.*,
            r.recipientName, r.readDate, r.recipientId, r.sentObjectId,
            so.distributeId, so.distributeIcon, so.distributeName
    FROM    v_Container$content o, ibs_Recipient_01 r, ibs_SentObject_01 so
    WHERE   o.oid = r.oid    
        AND r.sentObjectId = so.oid
GO
-- v_RecipientCont_01$content
