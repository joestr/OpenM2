/******************************************************************************
 * Task:        TASK ELAK-103 - Enable Grouping of searchqueries 
 *
 * Description: To enhance the usability when using queries the grouping
 *              of search queries into categories shall be enabled
 *              
 * Repeatable:  yes
 *
 * @version     $Id: U26002a_changeQueryCreatorTable.sql,v 1.1 2008/07/17 12:11:17 bbuchegger Exp $
 *
 * @author      Bernd Buchegger (BB) 20080613
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- create the table:
CREATE TABLE tmp_ibs_QueryCreator_01
(
    -- oid of object in ibs_object
    oid                         OBJECTID        NOT NULL PRIMARY KEY

    -- bit pattern to restrict where query could be used
    ,queryType                  INTEGER
    -- select string of sql-query without String 'SELECT'
    ,selectString               TEXT            NOT NULL
    -- from string of sql-query without String 'FROM'
    ,fromString                 TEXT            NOT NULL
    -- where string of sql-query without String 'WHERE'
    ,whereString                TEXT            NOT NULL
    -- groupBy string of sql-query without String 'GRUOP BY'
    ,groupByString              DESCRIPTION     NULL
    -- orderBy string of sql-query without String 'ORDER BY'
    ,orderByString              DESCRIPTION     NULL
    -- headers to be displayed in result - seperated by carrige return, line feed
    ,columnHeaders              TEXT            NOT NULL
    -- mapping between headers and attributes - seperated by carrige return, line feed
    ,queryAttrForHeaders        TEXT            NOT NULL
    -- SQL-Datatypes of queryAttributes which contents are displayed in resultlist.
    ,queryAttrTypesForHeaders   TEXT            NOT NULL
    -- tokens for searchfields (this tokens are displayed in searchform)
    ,searchFieldTokens          TEXT            NOT NULL
    -- mapping between searchfieldtokens and queryAttributes
    ,queryAttrForFields         TEXT            NOT NULL
    -- SQL-Datatypes of search - queryAttributes
    ,queryAttrTypesForFields    TEXT            NOT NULL
    -- number of maximum results
    ,resultCounter              INTEGER         NOT NULL
    -- show query when executing
    ,enableDebug                BOOL            NOT NULL
    -- query category to enable grouping
    ,category                   NAME            NULL
)

GO
-- tmp_ibs_MenuTab_01


DECLARE
    -- constants:

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (2000), -- error position description
    @l_msg                  VARCHAR (5000), -- the actual message
    @l_tableName            VARCHAR (30),   -- the table name
    @l_tempTableName        VARCHAR (30)    -- the temporary table name

-- assign constants:

-- initialize local variables:
SELECT
    @l_file = 'U25010a',
    @l_error = 0,
    @l_tableName = 'ibs_QueryCreator_01',
    @l_tempTableName = 'tmp_ibs_QueryCreator_01'

-- body:
    -- call the procedure which changes the old table scheme to the new one:
    -- for each new attribute set a default value either as number or as string
    EXEC p_changeTable @l_file, @l_tableName, @l_tempTableName,
        'category', ''''''

    -- ensure that the temporary table is dropped:
    EXEC p_dropTable @l_tempTableName

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos,
        '', 0,
        'l_tableName', @l_tableName,
        '', 0,
        'l_tempTableName', @l_tempTableName
    SELECT  @l_msg = @l_file + ': Error when changing table ' +
            @l_tableName + ':'
    PRINT @l_msg
    SELECT  @l_msg = 'Error ' + CONVERT (VARCHAR, @l_error) +
            '; position: ' + @l_ePos
    PRINT @l_msg

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished'
    PRINT @l_msg
GO


-- here come the trigger definitions:
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
