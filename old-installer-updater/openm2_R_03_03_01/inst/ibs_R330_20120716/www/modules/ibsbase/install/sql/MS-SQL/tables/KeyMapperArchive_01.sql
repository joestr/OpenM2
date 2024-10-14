/******************************************************************************
 *
 * The ibs_KeyMapperArchive table. <BR>
 * This table is used to archive the external keys (EXTKEY) of deleted objects.
 *
 * @version         1.10.0001, 30.11.2001
 *
 * @author      Michael Steiner (MS)
 *
 ******************************************************************************
 */

CREATE TABLE ibs_KeyMapperArchive
(
    oid         OBJECTID        NOT NULL,
    id          NVARCHAR (255)  NOT NULL,
    idDomain    NVARCHAR (63)   NOT NULL
)
GO
