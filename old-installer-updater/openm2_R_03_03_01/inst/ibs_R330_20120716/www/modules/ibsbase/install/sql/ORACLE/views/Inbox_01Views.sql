/******************************************************************************
 * All views regarding an inbox container. <BR>
 * 
 * @version     1.10.0001, 04.08.1999
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
CREATE OR REPLACE VIEW v_Inbox_01$content
AS
    SELECT  o.*,
            ro.distributedId, ro.distributedName, ro.activities, 
            ro.distributedTVersionId, ro.distributedTypeName, ro.sentObjectId, 
            ro.distributedIcon, ro.senderFullName AS sender
    FROM    v_Container$content o, ibs_ReceivedObject_01 ro
    WHERE   ro.oid = o.oid 
;
-- v_Inbox_01$content

EXIT;
