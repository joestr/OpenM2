/******************************************************************************
 * Migration of columns with UDDT datatypes to the DUMMY datatypes
 * (DESCRIPTION, NAME, FILENAME, EMAIL, STOREDPROCNAME, PHONENUM). <BR>
 *
 * @version     $Id: U210002n_migrateColumnsWithUDDT.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Migrate all columns which uses the UDDT 'Description'
DECLARE @l_stmt NVARCHAR(4000),
        @l_error INT			-- the actual error code
		
DECLARE stmtCursor CURSOR FOR
SELECT 'EXEC uc_migrateOneColumn ''' + TABLE_SCHEMA + ''',''' + TABLE_NAME
       + ''',''' + COLUMN_NAME + ''',''DESCRIPTION'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'NDESCRIPTION'
  AND DATA_TYPE = 'NVARCHAR'
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
       + ''',''' + COLUMN_NAME + ''',''NAME'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'NNAME'
  AND DATA_TYPE = 'NVARCHAR'
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
       + ''',''' + COLUMN_NAME + ''',''FILENAME'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'NFILENAME'
  AND DATA_TYPE = 'NVARCHAR'
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
       + ''',''' + COLUMN_NAME + ''',''EMAIL'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'NEMAIL'
  AND DATA_TYPE = 'NVARCHAR'
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
       + ''',''' + COLUMN_NAME + ''',''STOREDPROCNAME'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'NSTOREDPROCNAME'
  AND DATA_TYPE = 'NVARCHAR'
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
       + ''',''' + COLUMN_NAME + ''',''PHONENUM'',''' 
       + CASE IS_NULLABLE WHEN 'YES' THEN 'YES''' ELSE 'NO''' END
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DOMAIN_NAME = 'NPHONENUM'
  AND DATA_TYPE = 'NVARCHAR'
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