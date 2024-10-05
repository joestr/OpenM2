/******************************************************************************
 * Change name of a table. <BR>
 *
 * @param   ai_context          Context name or description from within this
 *                              procedure was called. This string is used as
 *                              output and logging prefix.
 * @param   ai_oldTableName     The original name of the table.
 * @param   ai_newTableName     The new name of the table.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_renameTable'
GO

-- create the new procedure:
CREATE PROCEDURE p_renameTable
( 
    -- input parameters: 
    @ai_context             VARCHAR (255),
    @ai_oldTableName        VARCHAR (30),
    @ai_newTableName        VARCHAR (30)
    -- output parameters:
) 
AS
DECLARE
    -- constants: 
 
    -- local variables: 
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_eText                VARCHAR (255),  -- full error text
    @l_msg                  VARCHAR (255)   -- the actual message

    -- assign constants:

    -- initialize local variables:

-- body:
    -- check if the old and the new table name are the same:
    IF (@ai_newTableName <> @ai_oldTableName) -- the tables are different?
    BEGIN
        BEGIN TRANSACTION -- begin new TRANSACTION

            -- rename thetable:
            EXEC @l_error = sp_rename @ai_oldTableName, @ai_newTableName

            SELECT @l_ePos = 'Error in RENAME'
            IF (@l_error <> 0)      -- an error occurred?
                GOTO exception      -- call common exception handler

        -- make changes permanent:
        COMMIT TRANSACTION

        -- print state report:
        SELECT  @l_msg = @ai_context + ': table ''' + @ai_oldTableName +
                ''' renamed to ''' + @ai_newTableName + '''.'
        PRINT   @l_msg
    END -- if the tables are different
    ELSE                                -- the tables are the same
    BEGIN
        SELECT  @l_msg = @ai_context +
                ': The table names are the same -> nothing to be done.'
        PRINT   @l_msg
    END -- else the tables are the same

    -- finish the procedure:
    RETURN

exception:
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes

    -- create error entry:
    SELECT  @l_eText = @ai_context + ': Error when renaming table ''' +
        @ai_oldTableName + ''' to ''' + @ai_newTableName + ''': ' + @l_ePos +
        '; errorcode = ' + CONVERT (VARCHAR, @l_error)

    -- log the error:
    EXEC ibs_error.logError 500, 'p_renameTable',
            @l_error, @l_eText
    PRINT   @l_eText
GO
-- p_renameTable
