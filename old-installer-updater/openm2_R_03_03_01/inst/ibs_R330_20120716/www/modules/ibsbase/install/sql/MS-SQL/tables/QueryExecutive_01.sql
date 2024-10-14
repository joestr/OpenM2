/******************************************************************************
 * Table for dynamic Reports
 *
 * @version     $Id: QueryExecutive_01.sql,v 1.2 2006/01/27 17:18:09 klreimue Exp $
 *
 * @author      Andreas Jansa (AJ)  20000918
 ******************************************************************************
 */
CREATE TABLE ibs_QueryExecutive_01
(
    -- oid of object in ibs_object
    oid                 OBJECTID        NOT NULL PRIMARY KEY,
    -- oid of assigned reporttemplate (querycreator)
    reportTemplateOid   OBJECTID      NOT NULL,
    -- search values for this query, seperated with ';'
    searchValues        DESCRIPTION     NULL,
    -- matchTypes for this query, in same order as searchValues,
    -- separated by ';'
    matchTypes          DESCRIPTION     NULL,
    -- oid of rootObject from which the search should be started,
    -- 0 if globalsearch
    rootObjectOid       OBJECTID        NOT NULL,
    showSearchForm      BOOL            NOT NULL DEFAULT (0),
    showDOMTree         BOOL            NOT NULL DEFAULT (0)
)
GO
-- ibs_QueryExecutive_01

-- access indices:
