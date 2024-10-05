/******************************************************************************
 * The ibs Tab table incl. indexes. <BR>
 * This table contains all tabs which are available throughout the system.
 *
 * @version     2.10.0001, 22.01.2001
 *
 * @author      Mario Oberdorfer (MO)  010122
 ******************************************************************************
 */
CREATE TABLE ibs_Tab
(
    id              ID              NOT NULL PRIMARY KEY, 
                                                -- unique id of the tab
    oid             OBJECTID        NULL,       -- object id (for later use)
    domainId        DOMAINID        NOT NULL,   -- valid domain of tab
    code            NAME            NOT NULL,   -- definite description of tab 
    kind            INT             NOT NULL,   -- kind of tab
    tVersionId      TVERSIONID      NULL,       -- id of type version
    fct             INT             NOT NULL,   -- function of tab
    priority        INT             NOT NULL,   -- priority of tab
    multilangKey    NAME            NOT NULL UNIQUE,
                                                -- key for storing the
                                                -- multilang values of this tab
    rights          RIGHTS          NOT NULL,   -- necessary rights to show the
                                                -- tab
    class           DESCRIPTION     NULL        -- class to show the view tab
)
GO 
-- ibs_Tab
