/******************************************************************************
 * Task:        TASK EVN060118_04 - OutOfMemory in elak tree
 *
 * Description: To come around the problem of the OutOfMemory the shall be
 *              loaded not all-at-once. So we need a possibility for customizing
 *              the tree's behaviour during loading.
 *              This is done done through adding some attributes to the MenuTab
 *              data structure.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24026a_changeMenuTabTable.sql,v 1.1 2006/04/11 15:52:19 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060330
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- create the table:
CREATE TABLE tmp_ibs_MenuTab_01
(
    oid             OBJECTID        NOT NULL PRIMARY KEY,
    objectOid       OBJECTID,
    description     DESCRIPTION     NOT NULL,
    isPrivate       BOOL,
    priorityKey     INT,
    domainId        INT,
    classFront      DESCRIPTION     NOT NULL,
    classBack       DESCRIPTION     NOT NULL,
    fileName        DESCRIPTION     NOT NULL,
    levelStep       INT             NOT NULL,
    levelStepMax    INT             NOT NULL
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
    @l_file = 'U24023a',
    @l_error = 0,
    @l_tableName = 'ibs_MenuTab_01',
    @l_tempTableName = 'tmp_ibs_MenuTab_01'

-- body:
    -- call the procedure which changes the old table scheme to the new one:
    -- for each new attribute set a default value either as number or as string
    EXEC p_changeTable @l_file, @l_tableName, @l_tempTableName,
        'levelStep', '0',
        'levelStepMax', '0'

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
