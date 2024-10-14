/******************************************************************************
 *
 * The ibs object table incl. indexes and triggers. <BR>
 * The object table contains all currently existing system objects.
 * 
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Heinz Josef Stampfer (HJ)  980526
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803   Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_Recipient_01
(
    oid             OBJECTID        NOT NULL UNIQUE, -- oid of the business object
    recipientId     OBJECTID        NOT NULL,
    recipientName   NAME            NULL,
    readDate        DATETIME        NULL,
    sentObjectId    OBJECTID        NULL,
    deleted         BOOL
)
GO
-- ibs_Recipient_01

