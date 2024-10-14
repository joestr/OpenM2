/******************************************************************************
 *
 * The ibs_Copy table is used while copy m2 objects. <BR>
 * 
 *
 * @version         1.10.0001, 04.08.1999
 *
 * @author      Heinz Stampfer (HP)  980518
 *
 * <DT><B>Updates:</B>
 *
 ******************************************************************************
 */

CREATE TABLE ibs_Copy
(
    oldOid         OBJECTID        NOT NULL UNIQUE,
    newOid         OBJECTID,
    copyId         INTEGER
)
GO
-- ibs_Copy

