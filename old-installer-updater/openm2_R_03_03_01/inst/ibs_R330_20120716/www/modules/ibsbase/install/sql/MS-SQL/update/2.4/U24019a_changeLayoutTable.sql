/******************************************************************************
 * Task:        TASK/BUG#xxx - Dummy file for Updatescript.
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes in the
 *              database content.
 *              Throughout this script the following tags are used:
 *              <tableName> ....... The name of the table to be updated.
 *              <tempTableName> ... The name of the temporary table containing
 *                                  the new table scheme.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24019a_changeLayoutTable.sql,v 1.1 2005/08/22 15:24:50 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 020625
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- create the table:
CREATE TABLE tmp_ibs_Layout_01
(
    oid             OBJECTID        NOT NULL UNIQUE,
    name            NAME            NOT NULL,
    domainId        DOMAINID        NOT NULL,
    isDefault       BOOL            NOT NULL DEFAULT (0)
)
GO
-- tmp_ibs_Layout_01


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
    @l_file = 'U24019a',
    @l_error = 0,
    @l_tableName = 'ibs_Layout_01',
    @l_tempTableName = 'tmp_ibs_Layout_01'

-- body:
    -- call the procedure which changes the old table scheme to the new one:
    -- for each new attribute set a default value either as number or as string
    EXEC p_changeTable @l_file, @l_tableName, @l_tempTableName,
        'isDefault', '0'

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
