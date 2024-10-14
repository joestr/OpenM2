/******************************************************************************
 * The ibs_Protocol_01 table incl. indexes. <BR>
 * The ibs_Protocol_01 table contains the values for the base object Protokoll_01.
 *
 * @version         1.10.0001, 03.08.1999
 *
 * @author      Heinz Josef Stampfer (HJ)  980901
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_Protocol_01
(
    id                  ID              NOT NULL UNIQUE,
    oid                 OBJECTID        NOT NULL,
    fullName            NAME            NOT NULL DEFAULT ('UNKNOWN'),
    userId              USERID          NOT NULL,
    objectName          NAME            NOT NULL DEFAULT ('UNKNOWN'),
    icon                NAME            NOT NULL DEFAULT ('icon.gif'),
    tVersionId          TVERSIONID      NOT NULL,
    containerId         OBJECTID        NOT NULL,
    containerKind       INT             NOT NULL DEFAULT (0),
    owner               USERID          NOT NULL,
    action              INT             NOT NULL DEFAULT (0),
    actionDate          DATETIME        NOT NULL DEFAULT (getDate ()),
)
GO
-- ibs_Protocol_01
