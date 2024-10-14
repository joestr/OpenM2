/******************************************************************************
 * Migration of columns with VARCHAR, CHAR or TEXT fields
 *
 * @version     $Id: U210002h_migrateColumnsWithStrings.sql,v 1.2 2010/03/02 13:17:02 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Migrate all columns which uses VARCHAR fields with lower than 4000 characters
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME 
       + ''',''' + COLUMN_NAME 
       + ''',''NVARCHAR(' + CAST(CHARACTER_MAXIMUM_LENGTH AS VARCHAR(10)) + ')'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%')
  AND DOMAIN_NAME IS NULL
  AND CHARACTER_MAXIMUM_LENGTH <= 4000

BEGIN
    -- Open cursor for the statements
    OPEN stmtCursor
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Migrate the column
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

-- Migrate all columns which uses VARCHAR fields with greater than 4000 characters
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME 
       + ''',''' + COLUMN_NAME + ''',''NVARCHAR(4000)'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%')
  AND DOMAIN_NAME IS NULL
  AND CHARACTER_MAXIMUM_LENGTH > 4000

BEGIN
    -- Open cursor for the statements
    OPEN stmtCursor
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Migrate the column
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

-- Migrate all columns which uses CHAR fields
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME 
       + ''',''' + COLUMN_NAME 
       + ''',''NCHAR(' + CAST(CHARACTER_MAXIMUM_LENGTH AS VARCHAR(10)) + ')'','''
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DATA_TYPE = 'CHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%')
  AND DOMAIN_NAME IS NULL

BEGIN
    -- Open cursor for the statements
    OPEN stmtCursor
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Migrate the column
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

-- Migrate all columns which uses TEXT fields
-- Two steps are necessary because a direct migration from TEXT to NTEXT is not allowed
-- We use NVARCHAR(max) for the first step and NTEXT for the second step
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor1 CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME 
       + ''',''' + COLUMN_NAME + ''',''NVARCHAR(max)'','''
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DATA_TYPE = 'TEXT'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%')
  AND DOMAIN_NAME IS NULL

BEGIN
    -- Open cursor 1 for the statements
    OPEN stmtCursor1
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor1 INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Migrate the column
        EXEC (@l_stmt)

		-- Check for an error
		SELECT @l_error = @@error
		IF  (@l_error <> 0)
		BEGIN
			PRINT @l_error
		END -- if

        -- Get next element
        FETCH NEXT FROM stmtCursor1 INTO @l_stmt
    END -- while

    -- Dump cursor structures
    CLOSE stmtCursor1
    DEALLOCATE stmtCursor1
    
END
GO

DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor2 CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME 
       + ''',''' + COLUMN_NAME + ''',''NTEXT'','''
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DATA_TYPE = 'NVARCHAR'
  AND CHARACTER_MAXIMUM_LENGTH = -1
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%')
  AND DOMAIN_NAME IS NULL

  BEGIN

    -- Open cursor 2 for the statements
    OPEN stmtCursor2
    -- Fetch first element 
    FETCH NEXT FROM stmtCursor2 INTO @l_stmt
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        -- Migrate the column
        EXEC (@l_stmt)

		-- Check for an error
		SELECT @l_error = @@error
		IF  (@l_error <> 0)
		BEGIN
			PRINT @l_error
		END -- if

        -- Get next element
        FETCH NEXT FROM stmtCursor2 INTO @l_stmt
    END -- while

    -- Dump cursor structures
    CLOSE stmtCursor2
    DEALLOCATE stmtCursor2

END
GO
