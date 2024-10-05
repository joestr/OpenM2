/******************************************************************************
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
CREATE TABLE ibs_SentObject_01
(
    -- oid of the sent object
    oid                     OBJECTID        NOT NULL UNIQUE, 
    distributeId            OBJECTID        NOT NULL, 
    -- version of the sent object
    distributeTVersionId    TVERSIONID      NULL,        
    distributeTypeName      NAME            NULL,
    distributeName          NAME            NULL,
    distributeIcon          NAME            NULL,
    activities              NAME            NULL,
    deleted                 BOOL
)
GO
-- ibs_SentObject_01
