/******************************************************************************
 *
 * The ibs_KeyMapper indexes and triggers. <BR>
 *
 * @version         1.10.0001, 03.08.1999
 *
 * @author      Bernd Buchegger (BB)  980513
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803	Code cleaning.
 ******************************************************************************
 */

CREATE TABLE ibs_KeyMapper
(
    oid         OBJECTID        NOT NULL,
    id          NVARCHAR (255)  NOT NULL,
    idDomain    NVARCHAR (63)   NOT NULL
)
GO

