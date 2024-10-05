 /* Note_01 */

CREATE TABLE ibs_Note_01
(
    -- unique object id, reference to table ibs_object
    oid             OBJECTID        NOT NULL UNIQUE,
    -- content of note - normal Text or HTML-code
    content         NTEXT           NOT NULL
)
GO



