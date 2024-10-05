/******************************************************************************
 * Table for dynamic Reports
 *
 * @version     1.10.0001, 18.09.2000
 *
 * @author      Andreas Jansa  000918
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_QueryExecutive_01
(
    -- oid of object in ibs_object
    oid                    RAW (8) NOT NULL PRIMARY KEY,
    -- oid of assigned reporttemplate (querycreator)
    reportTemplateOid      RAW (8) NOT NULL,
    -- search values for this query, seperated with ';'
    searchValues           VARCHAR2 (255) NULL,
    -- matchTypes for this query, in same order as searchValues, seperated by ';'
    matchTypes             VARCHAR2 (255) NULL,
    -- oid of rootObject from which the search should be started, 0 if globalsearch
    rootObjectOid          RAW (8) NOT NULL
)/*TABLESPACE*/;
-- ibs_QueryExecutive_01

-- access indices:

EXIT;