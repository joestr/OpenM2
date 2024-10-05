/******************************************************************************
 * Task:        TASK CRE4 - performance tuning.
 *
 * Description: Add new table ibs_ObjectId.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24008a_createObjectId.sql,v 1.1 2005/03/04 22:06:16 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 20050303
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- create the table:
CREATE TABLE tmp_ObjectId
(
    id          ID              IDENTITY (1000001, 1) -- the object id
)
GO
-- tmp_ObjectId


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
    @l_file = 'U24008a',
    @l_error = 0,
    @l_tableName = 'ibs_ObjectId',
    @l_tempTableName = 'tmp_ObjectId'

-- body:
    -- check if the target table exists:
    IF NOT EXISTS ( SELECT  * 
                    FROM    sysobjects 
                    WHERE   id = object_id (@l_tableName) 
                        AND sysstat & 0xf = 3)
    BEGIN                       -- target table does not exist?
        -- rename temp table:
        EXEC (
            'EXEC sp_rename ' + @l_tempTableName + ', ' + @l_tableName
            )
    END -- if target table does not exist

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
    SELECT  @l_msg = @l_file + ': Error when creating table ' +
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
