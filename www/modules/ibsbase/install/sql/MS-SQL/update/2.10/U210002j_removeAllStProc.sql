/******************************************************************************
 * Save the list of the actual stored procedures to a specific database table<BR>
 *
 * @version     $Id: U210002j_removeAllStProc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Remove all stored procedures from the database to finish UDDT migration
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'DROP PROCEDURE ' + b.specific_schema + '.' + a.name
FROM sys.objects a, INFORMATION_SCHEMA.ROUTINES b
WHERE a.type = 'P'
  AND a.is_ms_shipped = 0 
  AND a.name not like 'dt%'
  AND a.name not like 'uc_migrateOneColumn'  
  AND a.name = b.ROUTINE_NAME
ORDER BY 1

BEGIN
    -- Open cursor for the statements
    OPEN stmtCursor
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Remove the stored procedure
        EXEC (@l_stmt)
		
		-- Check for an error
		SELECT @l_error = @@error
		IF  (@l_error <> 0)
		BEGIN
			PRINT @l_error
		END -- if

        -- Get next element
        FETCH NEXT FROM stmtCursor INTO @l_stmt
    END -- while

    -- Dump cursor structures
    CLOSE stmtCursor
    DEALLOCATE stmtCursor

END
GO
