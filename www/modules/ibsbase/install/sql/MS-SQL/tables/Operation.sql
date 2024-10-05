/******************************************************************************
 * The ibs operations table incl. indexes. <BR>
 * The operations table contains the operations which may be performed within
 * the system.
 *
 * @version         1.10.0001, 02.08.1999
 *
 * @author          Klaus Reimüller (KR)    980528
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_Operation
(
    id          OPERATIONID     NOT NULL PRIMARY KEY,
    name        NAME            NOT NULL,
    description DESCRIPTION     NULL
)
GO
-- ibs_Operation
