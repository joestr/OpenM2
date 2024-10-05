/* Article_01 */
 
CREATE TABLE m2_Article_01
(
    oid         OBJECTID        NOT NULL UNIQUE,
    content     NTEXT           NOT NULL,
    state       STATE           NOT NULL,
    discussionId OBJECTID       NOT NULL
)
GO

