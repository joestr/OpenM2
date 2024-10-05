/******************************************************************************
 * Check the saved list of stored procedures with the new list of procedures
 * When the new procedure is found in the saved list, remove it from the saved
 * list, otherwise add a new entry to the list with NEW
 *
 * @version     $Id: U210004a_checkStProcList.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Check the new list of stored procedures with the saved list of them
DECLARE @l_name NVARCHAR(512),
        @l_error INT            -- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT b.specific_schema + '.' + a.name
FROM sys.objects a, 
     INFORMATION_SCHEMA.ROUTINES b
WHERE a.type = 'P'
  AND a.is_ms_shipped = 0 
  AND a.name not like 'dt%'
  -- AND a.name not like 'uc_migrateOneColumn'  
  AND a.name = b.ROUTINE_NAME
ORDER BY 1

BEGIN
    -- Open cursor for the statements
    OPEN stmtCursor
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor INTO @l_name
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        IF (SELECT COUNT(*)
            FROM uc_stproclist
            WHERE procname = @l_name) > 0
        BEGIN
            DELETE FROM uc_stproclist
            WHERE procname = @l_name

            -- Check for an error
	        SELECT @l_error = @@error
	        IF  (@l_error <> 0)
	        BEGIN
	            PRINT @l_error
	        END -- if
        END -- if
        ELSE
        BEGIN
            INSERT INTO uc_stproclist 
                (procname, procstatus)
            VALUES
                (@l_name, 'NEW IN R3.0.0')

            -- Check for an error
	        SELECT @l_error = @@error
	        IF  (@l_error <> 0)
	        BEGIN
	            PRINT @l_error
	        END -- if
        END -- else 
        
        -- Get next element
        FETCH NEXT FROM stmtCursor INTO @l_name
    END -- while

    -- Dump cursor structures
    CLOSE stmtCursor
    DEALLOCATE stmtCursor

END
GO 
