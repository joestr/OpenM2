/******************************************************************************
 * The ibs objectRead table incl. indexes. <BR>
 * The value table tells which object was already read by which user and when.
 *
 * @version     1.10.0001, 02.08.1999
 *
 * @author      Klaus Reimüller (KR)    980504
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_ObjectRead
(
    oid         OBJECTID        NOT NULL,
    userId      USERID          NOT NULL,
    hasRead     BOOL            NOT NULL DEFAULT (1),
    lastRead    DATETIME        NOT NULL DEFAULT getDate ()
)
GO
-- ibs_ObjectRead
