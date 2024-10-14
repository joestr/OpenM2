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
CREATE TABLE ibs_QueryCreator_01
(
    -- oid of object in ibs_object
    oid                         OBJECTID        NOT NULL PRIMARY KEY

    -- bit pattern to restrict where query could be used
    ,queryType                  INTEGER
    -- select string of sql-query without String 'SELECT'
    ,selectString               NTEXT           NOT NULL
    -- from string of sql-query without String 'FROM'
    ,fromString                 NTEXT           NOT NULL
    -- where string of sql-query without String 'WHERE'
    ,whereString                NTEXT           NOT NULL
    -- groupBy string of sql-query without String 'GRUOP BY'
    ,groupByString              DESCRIPTION     NULL
    -- orderBy string of sql-query without String 'ORDER BY'
    ,orderByString              DESCRIPTION     NULL
    -- headers to be displayed in result - seperated by carrige return, line feed
    ,columnHeaders              NTEXT           NOT NULL
    -- mapping between headers and attributes - seperated by carrige return, line feed
    ,queryAttrForHeaders        NTEXT           NOT NULL
    -- SQL-Datatypes of queryAttributes which contents are displayed in resultlist.
    ,queryAttrTypesForHeaders   NTEXT           NOT NULL
    -- tokens for searchfields (this tokens are displayed in searchform)
    ,searchFieldTokens          NTEXT           NOT NULL
    -- mapping between searchfieldtokens and queryAttributes
    ,queryAttrForFields         NTEXT           NOT NULL
    -- SQL-Datatypes of search - queryAttributes
    ,queryAttrTypesForFields    NTEXT           NOT NULL
    -- number of maximum results
    ,resultCounter              INTEGER         NOT NULL
    -- show query when executing
    ,enableDebug                BOOL            NOT NULL
    -- query category to enable grouping
    ,category                   NAME            NULL    
)
GO
-- ibs_QueryCreator_01

-- access indices: