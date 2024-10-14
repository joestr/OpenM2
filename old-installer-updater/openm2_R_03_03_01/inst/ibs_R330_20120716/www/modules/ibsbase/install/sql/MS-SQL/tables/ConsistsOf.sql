/******************************************************************************
 * The ibs consistsOf table incl. indexes. <BR>
 * The consistsOf table contains the dependencies between types regarding tabs.
 *
 * @version     2.10.0001, 22.01.2001    
 *
 * @author      Klaus Reimüller (KR)  980715
 ******************************************************************************
 */
CREATE TABLE ibs_ConsistsOf
(
    id              ID              NOT NULL PRIMARY KEY,  
                                                -- unique id of the tab
    oid             OBJECTID        NULL,       -- unique object id
                                                -- (for later use)
    tVersionId      TVERSIONID      NOT NULL,   -- tVersionId of the tab
    tabId           ID              NOT NULL,   -- id of the tab in table
                                                -- ibs_Tab
    priority        INT             NOT NULL,   -- priority of the tab
    rights          RIGHTS          NOT NULL,   -- necessary rights to show the
                                                -- tab
    inheritedFrom   TVERSIONID      NOT NULL    -- id of type version from which 
                                                -- this tuple was inherited
)
GO
-- ibs_ConsistsOf
