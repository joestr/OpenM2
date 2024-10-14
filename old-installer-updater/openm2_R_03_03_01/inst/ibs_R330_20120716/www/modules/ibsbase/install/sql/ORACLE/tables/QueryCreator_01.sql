/******************************************************************************
 * Table for dynamic Search - Queries
 *
 * @version     1.10.0001, 18.09.2000
 *
 * @author      Andreas Jansa  000918
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_QueryCreator_01
(
    -- oid of object in ibs_object
    oid                     RAW (8)         NOT NULL PRIMARY KEY,
    -- bit pattern to restrict where query could be used
    queryType               INTEGER,
    -- select string of sql-query without String 'SELECT'
    selectString            CLOB            NOT NULL,
    -- from string of sql-query without String 'FROM'
    fromString              CLOB            NOT NULL,
    -- where string of sql-query without String 'WHERE'
    whereString             CLOB            NOT NULL,
    -- groupBy string of sql-query without String 'GRUOP BY'
    groupByString           VARCHAR2 (255)  NULL,
    -- orderBy string of sql-query without String 'ORDER BY'
    orderByString           VARCHAR2 (255)  NULL,
    -- headers to be displayed in result - seperated by carrige return, line feed
    columnHeaders           CLOB            NOT NULL,
    -- mapping between headers and attributes - seperated by carrige return, line feed
    queryAttrForHeaders     CLOB            NOT NULL,
    -- SQL-Datatypes of queryAttributes which contents are displayed in resultlist.
    queryAttrTypesForHeaders CLOB           NOT NULL,
    -- tokens for searchfields (this tokens are displayed in searchform)
    searchFieldTokens       CLOB            NOT NULL,
    -- mapping between searchfieldtokens and queryAttributes
    queryAttrForFields      CLOB            NOT NULL,
    -- SQL-Datatypes of search - queryAttributes
    queryAttrTypesForFields CLOB            NOT NULL,
    -- maximum number of results
    resultCounter           INTEGER         NOT NULL,
    -- show query when executing
    enableDebug             NUMBER          NOT NULL,
    -- query category to enable grouping
    category                VARCHAR2 (63)   NULL       
    
) /*TABLESPACE*/;
-- ibs_QueryCreator_01

-- access indices:

EXIT;