/* XMLDiscussionTemplate_01 */

CREATE TABLE m2_XMLDiscussionTemplate_01
(
    oid         OBJECTID        NOT NULL UNIQUE,
    level1      OBJECTID        NOT NULL,
    level2      OBJECTID        NOT NULL,
    level3      OBJECTID        NOT NULL
)
GO

