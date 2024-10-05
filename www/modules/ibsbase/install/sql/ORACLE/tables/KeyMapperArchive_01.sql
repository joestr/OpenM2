/******************************************************************************
 * The ibs_KeyMapperArchive table. <BR>
 * This table is used to archive the external keys (EXTKEY) of deleted objects.
 *
 * @version     2.2.1.0001 020529 KR
 *
 * @author      Klaus Reimüller (KR) 020529
 ******************************************************************************
 */

CREATE TABLE /*USER*/ibs_KeyMapperArchive
(
    oid         RAW (8)             NOT NULL,
    id          VARCHAR2 (255)      NOT NULL,
    idDomain    VARCHAR2 (63)       NOT NULL
) /*TABLESPACE*/;

EXIT;
