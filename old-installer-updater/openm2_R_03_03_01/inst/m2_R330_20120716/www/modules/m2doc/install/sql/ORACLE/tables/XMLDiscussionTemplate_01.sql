/* XMLDiscussionTemplate_01 */

CREATE TABLE /*USER*/m2_XMLDiscussionTemplate_01
(
    oid         RAW (8)        NOT NULL UNIQUE,
    level1      RAW (8)        NOT NULL,
    level2      RAW (8)        NOT NULL,
    level3      RAW (8)        NOT NULL
) /*TABLESPACE*/;

exit;

