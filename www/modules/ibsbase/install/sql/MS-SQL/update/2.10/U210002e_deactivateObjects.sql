/******************************************************************************
 * Deactivation of several objects which avert a migration of columns. <BR>
 *
 * @version     $Id: U210002e_deactivateObjects.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Deactivation of ALL constraints on ALL tables
EXEC sp_MSforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'
GO

-- Deactivation of all NONCLUSTERED INDEX on ALL tables
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE indexCursor CURSOR FOR
SELECT 'ALTER INDEX ' + i.name + ' ON ' + o.name + ' DISABLE'
FROM sysindexes i, sysobjects o
WHERE i.id = o.id
  AND indid > 1 AND indid < 255
  AND o.type = 'U'
  --ignore the indexes for the autostat
  AND (i.status & 64) = 0 
  AND (i.status & 8388608) = 0 
  AND (i.status & 16777216)= 0 
ORDER BY o.name

BEGIN
    -- Open cursor for the statements
    OPEN indexCursor
    -- Fetch first element 
    FETCH NEXT FROM indexCursor INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Deactivate index
        EXEC (@l_stmt)

		-- Check for an error
		SELECT @l_error = @@error
		IF  (@l_error <> 0)
		BEGIN
			PRINT @l_error
		END -- if

        -- Get next element
        FETCH NEXT FROM indexCursor INTO @l_stmt
    END -- while

    -- Dump cursor structures
    CLOSE indexCursor
    DEALLOCATE indexCursor

END
GO