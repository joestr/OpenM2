/* Discussion_01 */

CREATE TABLE m2_Discussion_01
(
    oid         OBJECTID        NOT NULL UNIQUE,
    maxlevels   INT             NOT NULL,
    defaultView BOOL            NOT NULL,
    refOid      OBJECTID        NULL
)
GO

