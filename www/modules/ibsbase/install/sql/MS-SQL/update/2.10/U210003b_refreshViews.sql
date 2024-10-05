/******************************************************************************
 * Refresh all views in the database to get the new column information for 
 * every column in the view. <BR>
 *
 * @version     $Id: U210003b_refreshViews.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Refresh all views in the database
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT DISTINCT 'EXEC sp_refreshview ''' + so.name + ''''
FROM sys.objects AS so 
WHERE so.type = 'V' 
  AND so.name NOT LIKE ('%Diskussion%') 
  AND is_ms_shipped = 0

BEGIN
    -- Open cursor for the statements
    OPEN stmtCursor
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Refresh the view
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