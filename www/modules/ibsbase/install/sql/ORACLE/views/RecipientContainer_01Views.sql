/******************************************************************************
 * All views regarding a recipient container. <BR>
 *
 * @version     1.10.0001, 08.04.1999
 *
 * @author      Andreas Jansa (AJ)  990315
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804    Code cleaning.
 ******************************************************************************
 */
 
/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights). <BR>
 */
CREATE OR REPLACE VIEW v_RecipientCont_01$content
AS
    SELECT  o.*,
            r.recipientName, r.readDate, r.recipientId, r.sentObjectId,
            so.distributeId, so.distributeIcon, so.distributeName
    FROM    v_Container$content o, ibs_Recipient_01 r, ibs_SentObject_01 so
    WHERE   o.oid = r.oid
        AND r.sentObjectId = so.oid
;
-- v_RecipientCont_01$content

EXIT;
