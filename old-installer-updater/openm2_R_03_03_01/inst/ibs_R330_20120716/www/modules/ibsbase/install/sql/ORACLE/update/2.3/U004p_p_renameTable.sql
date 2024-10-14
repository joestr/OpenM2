/******************************************************************************
 * Change name of a table. <BR>
 *
 * @param   ai_context          Context name or description from within this
 *                              procedure was called. This string is used as
 *                              output and logging prefix.
 * @param   ai_oldTableName     The original name of the table.
 * @param   ai_newTableName     The new name of the table.
 */
CREATE OR REPLACE PROCEDURE p_renameTable
( 
    -- input parameters: 
    ai_context              VARCHAR2,
    ai_oldTableName         VARCHAR2,
    ai_newTableName         VARCHAR2
    -- output parameters:
) 
AS
    -- constants: 
 
    -- local variables: 
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_cmdString             VARCHAR2 (2000); -- command line to be executed

-- body:
BEGIN
    -- check if the old and the new table name are the same:
    IF (ai_newTableName <> ai_oldTableName) -- the tables are different?
    THEN
        COMMIT WORK; -- finish previous and begin new TRANSACTION

        BEGIN
            -- rename the table:
            EXEC_SQL ('RENAME ' || ai_oldTableName || ' TO ' || ai_newTableName);
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- log the error:
                l_ePos := 'error in RENAME';
                RAISE;                  -- call common exception handler
        END;

        -- make changes permanent and set new transaction starting point:
        COMMIT WORK;

        -- print state report:
        debug (ai_context || ': table ''' || ai_oldTableName ||
            ''' renamed to ''' || ai_newTableName || '''.');
    ELSE                                -- the tables are the same
        debug (ai_context ||
            ': The table names are the same -> nothing to be done.');
    END IF; -- else the tables are the same

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := ai_context || ': Error when renaming table ''' ||
            ai_oldTableName || ''' to ''' || @ai_newTableName || ''': ' || l_ePos ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, ai_context, l_eText);
        debug (l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
END p_renameTable;
/
show errors;


EXIT;
