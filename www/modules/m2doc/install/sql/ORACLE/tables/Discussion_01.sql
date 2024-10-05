/* Discussion_01 */

CREATE TABLE /*USER*/m2_Discussion_01
(
    oid                 RAW (8)             NOT NULL UNIQUE,
    maxlevels           INTEGER             NOT NULL,
    defaultView         NUMBER (1)          NOT NULL,
    refOid              RAW (8)             NULL
) /*TABLESPACE*/;

exit;

