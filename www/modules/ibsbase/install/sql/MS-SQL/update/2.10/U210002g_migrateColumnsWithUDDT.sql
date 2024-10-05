/******************************************************************************
 * Migration of columns with UDDT datatypes to the DUMMY datatypes
 * (DESCRIPTION, NAME, FILENAME, EMAIL, STOREDPROCNAME, PHONENUM). <BR>
 *
 * @version     $Id: U210002g_migrateColumnsWithUDDT.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Migrate all columns which uses the UDDT 'Description'
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME
       + ''',''' + COLUMN_NAME + ''',''NDESCRIPTION'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'DESCRIPTION'
  AND DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%') 
  AND DOMAIN_NAME IS NOT NULL

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

-- Migrate all columns which uses the UDDT 'Name'
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME
       + ''',''' + COLUMN_NAME + ''',''NNAME'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'NAME'
  AND DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%') 
  AND DOMAIN_NAME IS NOT NULL

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

-- Migrate all columns which uses the UDDT 'Filename'
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME
       + ''',''' + COLUMN_NAME + ''',''NFILENAME'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'FILENAME'
  AND DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%') 
  AND DOMAIN_NAME IS NOT NULL

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

-- Migrate all columns which uses the UDDT 'EMail'
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME
       + ''',''' + COLUMN_NAME + ''',''NEMAIL'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'EMAIL'
  AND DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%') 
  AND DOMAIN_NAME IS NOT NULL

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

-- Migrate all columns which uses the UDDT 'Storedprocname'
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME
       + ''',''' + COLUMN_NAME + ''',''NSTOREDPROCNAME'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'STOREDPROCNAME'
  AND DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%') 
  AND DOMAIN_NAME IS NOT NULL

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

-- Migrate all columns which uses the UDDT 'Phonenum'
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code

DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME
       + ''',''' + COLUMN_NAME + ''',''NPHONENUM'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'PHONENUM'
  AND DATA_TYPE = 'VARCHAR'
  AND (TABLE_NAME NOT LIKE 'v%' AND TABLE_NAME NOT LIKE 'dt%')
  AND COLUMN_NAME NOT LIKE ('%posNo%') 
  AND DOMAIN_NAME IS NOT NULL

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