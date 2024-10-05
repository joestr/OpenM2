/******************************************************************************
 * Task:        TASK IBS-85 - Extend the Log Container to display the field modifications..
 *
 * Description: The newly required table ibs_ProtocolEntry_01 is created.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U25004a_ProtocolEntry_01.sql,v 1.2 2008/06/03 07:36:48 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 080602
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- create the table:
CREATE TABLE tmp_ProtocolEntry_01
(
    id                  ID              NOT NULL UNIQUE,
    protocolId          ID              NOT NULL,
    fieldName           NAME            NOT NULL DEFAULT ('UNKNOWN'),
    oldValue            NAME            NOT NULL DEFAULT ('UNKNOWN'),
    newValue            NAME            NOT NULL DEFAULT ('UNKNOWN')
)
GO
-- tmp_ProtocolEntry_01


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
    @l_file = 'U25004a_ProtocolEntry_01',
    @l_error = 0,
    @l_tableName = 'ibs_ProtocolEntry_01',
    @l_tempTableName = 'tmp_ProtocolEntry_01'

-- body:
    -- call the procedure which changes the old table scheme to the new one:
    -- for each new attribute set a default value either as number or as string
    EXEC p_changeTable @l_file, @l_tableName, @l_tempTableName,
        'id', '',
        'protocolId', '',
        'fieldName', '''''',
        'oldValue', '''''',
        'newValue', ''''''

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
