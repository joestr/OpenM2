/******************************************************************************
 * Procedure to migrate one single column with checks index, unique constraint
 * and default on a single column. <BR>
 *
 * @version     $Id: U210002d_migrateOneColumnProc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

/******************************************************************************
 * Function which returns whether the column is ASC or DESC  
 *
 * @input parameters:
 * @param   object_id           Id of the object.
 * @param   index_id            Id of the index.
 * @param   column_id           Id of the column. 
 *
 * @output parameters:
 * @param   NVARCHAR(5)         When ASC then '' otherwise 'DESC'
 */
-- Delete existing procedure
EXEC p_dropProc uc_migrateOneColumn
GO

-- Create procedure
CREATE PROCEDURE uc_migrateOneColumn
(
    -- input parameters:
    @tableSchema          NVARCHAR(128),
    @tableName            NVARCHAR(128),
    @columnName           NVARCHAR(128),
    @newType              NVARCHAR(128),
    @nullable             NVARCHAR(128)
    -- output parameters:
)
AS
DECLARE
    -- local variables:
    @dropIdx1             NVARCHAR(512),
    @dropIdx2             NVARCHAR(512),
    @dropIdx3             NVARCHAR(512),
    @dropIdx4             NVARCHAR(512),
    @dropIdx5             NVARCHAR(512),
    @createIdx1           NVARCHAR(512),
    @createIdx2           NVARCHAR(512),
    @createIdx3           NVARCHAR(512),
    @createIdx4           NVARCHAR(512),
    @createIdx5           NVARCHAR(512),

    @dropUIIdx1           NVARCHAR(512),
    @dropUIIdx2           NVARCHAR(512),
    @dropUIIdx3           NVARCHAR(512),
    @dropUIIdx4           NVARCHAR(512),
    @dropUIIdx5           NVARCHAR(512),
    @createUIIdx1         NVARCHAR(512),
    @createUIIdx2         NVARCHAR(512),
    @createUIIdx3         NVARCHAR(512),
    @createUIIdx4         NVARCHAR(512),
    @createUIIdx5         NVARCHAR(512),

    @dropDef              NVARCHAR(512),
    @createDef            NVARCHAR(512),

    @stmt                 NVARCHAR(4000),   
    @counterUIIdx         INT,
    @counterIdx           INT,
    @counterDef           INT,
    @msg                  NVARCHAR(512),
    @l_error              INT            -- the actual error code
    -- assign constants:

    -- initialize local variables and return values:

-- body:

    PRINT 'START migration of  column ' + @tableSchema + '.' + @tableName + '.' + @columnName + '...'

    PRINT 'Scan column for UNIQUE INDEX information ...'

    SELECT @counterUIIdx = 0

    DECLARE createUIIdx CURSOR FOR
        SELECT N'ALTER TABLE ' + 
               @tableSchema + '.' + @tableName + 
               N' ADD CONSTRAINT ' + 
               INDEX_NAME + N' UNIQUE (' + COLUMN_LIST + ');'
        FROM dbo.vAllIndexes
        WHERE IS_UNIQUE = 1 
          AND TABLE_NAME = N'' + @tableName + ''
          AND COLUMN_LIST LIKE N'%' + @columnName + '%'

    OPEN createUIIdx 
    FETCH NEXT FROM createUIIdx INTO @stmt
    WHILE (@@fetch_status <> -1)
    BEGIN
        IF (@@fetch_status <> -2)
        BEGIN
            IF (@counterUIIdx = 0)
            BEGIN            
                SELECT @createUIIdx1 = @stmt
                -- PRINT @createUIIdx1
            END -- if
            IF (@counterUIIdx = 1)
            BEGIN            
                SELECT @createUIIdx2 = @stmt
                -- PRINT @createUIIdx2 
            END -- if
            IF (@counterUIIdx = 2)
            BEGIN            
                SELECT @createUIIdx3 = @stmt
                -- PRINT @createUIIdx3 
            END -- if
            IF (@counterUIIdx = 3)
            BEGIN            
                SELECT @createUIIdx4 = @stmt
                -- PRINT @createUIIdx4 
            END -- if
            IF (@counterUIIdx = 4)
            BEGIN            
                SELECT @createUIIdx5 = @stmt
                -- PRINT @createUIIdx5 
            END -- if
        END -- if
        SELECT @counterUIIdx = @counterUIIdx + 1
        FETCH NEXT FROM createUIIdx INTO @stmt
    END -- while
    CLOSE createUIIdx 
    DEALLOCATE createUIIdx 

    IF (@counterUIIdx > 0)
    BEGIN
        PRINT 'Saved ' + CAST(@counterUIIdx AS VARCHAR(10)) + ' CREATE UNIQUE INDEX statements for recreation.'

        PRINT 'Drop all necessary unique indexes ...'

        SELECT @counterUIIdx = 0
    
        DECLARE dropUIIdx CURSOR FOR
            SELECT N'ALTER TABLE ' + 
                   @tableSchema + '.' + @tableName + 
                   N' DROP CONSTRAINT ' + 
                   INDEX_NAME + N';'
            FROM dbo.vAllIndexes
            WHERE IS_UNIQUE = 1 
              AND TABLE_NAME = N'' + @tableName + ''
              AND COLUMN_LIST LIKE N'%' + @columnName + '%'

        OPEN dropUIIdx
        FETCH NEXT FROM dropUIIdx INTO @stmt
        WHILE (@@fetch_status <> -1)
        BEGIN
            IF (@@fetch_status <> -2)
            BEGIN
                PRINT @stmt
                                EXEC (@stmt)
            END -- if
            SELECT @counterUIIdx = @counterUIIdx + 1
            FETCH NEXT FROM dropUIIdx INTO @stmt
        END -- while
        CLOSE dropUIIdx
        DEALLOCATE dropUIIdx

        PRINT 'Dropped ' + CAST(@counterUIIdx AS VARCHAR(10)) + ' unique indexes on ' + @tableSchema + '.' + @tableName + '.' + @columnName
    END
    ELSE
    BEGIN
        PRINT 'No UNIQUE INDEX on ' + @tableSchema + '.' + @tableName + '.' + @columnName + ' found!'
    END -- if

    PRINT 'Scan column for INDEX information ...'

    SELECT @counterIdx = 0
   
    DECLARE createIdx CURSOR FOR
        SELECT N'CREATE INDEX ' + 
               INDEX_NAME + N' ON ' + 
               @tableSchema + '.' + TABLE_NAME + 
               N' (' + COLUMN_LIST + ');'
        FROM dbo.vAllIndexes
        WHERE IS_UNIQUE = 0 
              AND TABLE_NAME = N'' + @tableName + ''
              AND COLUMN_LIST LIKE N'%' + @columnName + '%'

    OPEN createIdx 
    FETCH NEXT FROM createIdx INTO @stmt
    WHILE (@@fetch_status <> -1)
    BEGIN
        IF (@@fetch_status <> -2)
        BEGIN
            IF (@counterIdx = 0)
            BEGIN            
                SELECT @createIdx1 = @stmt
                -- PRINT @createIdx1 
            END -- if
            IF (@counterIdx = 1)
            BEGIN            
                SELECT @createIdx2 = @stmt
                -- PRINT @createIdx2 
            END -- if
            IF (@counterIdx = 2)
            BEGIN            
                SELECT @createIdx3 = @stmt
                -- PRINT @createIdx3 
            END -- if
            IF (@counterIdx = 3)
            BEGIN            
                SELECT @createIdx4 = @stmt
                -- PRINT @createIdx4 
            END -- if
            IF (@counterIdx = 4)
            BEGIN            
                SELECT @createIdx5 = @stmt
                -- PRINT @createIdx5 
            END -- if
        END -- if
        SELECT @counterIdx = @counterIdx + 1
        FETCH NEXT FROM createIdx INTO @stmt
    END -- while
    CLOSE createIdx 
    DEALLOCATE createIdx 

    IF (@counterIdx > 0)
    BEGIN
        PRINT 'Saved ' + CAST(@counterIdx AS VARCHAR(10)) + ' CREATE INDEX statements for recreation.'

        PRINT 'Drop all necessary indexes ...'

        SELECT @counterIdx = 0
    
        DECLARE dropIdx CURSOR FOR
            SELECT N'DROP INDEX ' + 
                   INDEX_NAME + N' ON ' + 
                   @tableSchema + '.' + TABLE_NAME + N';' 
            FROM dbo.vAllIndexes
            WHERE IS_UNIQUE = 0 
                  AND TABLE_NAME = N'' + @tableName + ''
                  AND COLUMN_LIST LIKE N'%' + @columnName + '%'

        OPEN dropIdx
        FETCH NEXT FROM dropIdx INTO @stmt
        WHILE (@@fetch_status <> -1)
        BEGIN
            IF (@@fetch_status <> -2)
            BEGIN
                PRINT @stmt
                EXEC (@stmt)
            END -- if
            SELECT @counterIdx = @counterIdx + 1
            FETCH NEXT FROM dropIdx INTO @stmt
        END -- while
        CLOSE dropIdx
        DEALLOCATE dropIdx

        PRINT 'Dropped ' + CAST(@counterIdx AS VARCHAR(10)) + ' indexes on ' + @tableSchema + '.' + @tableName + '.' + @columnName
    END
    ELSE
    BEGIN
        PRINT 'No INDEX on ' + @tableSchema + '.' + @tableName + '.' + @columnName + ' found!'
    END -- if

    PRINT 'Scan column for DEFAULT information ...'

    SELECT @counterDef = 0

    DECLARE createdef CURSOR FOR
        SELECT 'ALTER TABLE ' + 
               @tableSchema + '.' + OBJECT_NAME(a.parent_object_id) + 
               ' ADD CONSTRAINT ' + 
               OBJECT_NAME(a.OBJECT_ID) + 
               ' DEFAULT ' + 
               REPLACE(REPLACE(b.[definition],'(',''),')','') + 
               ' FOR ' + 
               c.[name] + ';'
        FROM sys.objects a, 
             sys.default_constraints b, 
             sys.columns c
        WHERE a.type_desc LIKE 'DEFAULT_CONSTRAINT' 
          AND a.object_id = b.object_id 
          AND c.object_id = b.parent_object_id 
          AND c.column_id = b.parent_column_id
          AND OBJECT_NAME(a.parent_object_id) = @tableName
          AND c.name = @columnName

    OPEN createdef 
    FETCH NEXT FROM createdef INTO @stmt
    WHILE (@@fetch_status <> -1)
    BEGIN
        IF (@@fetch_status <> -2)
        BEGIN
            SELECT @createDef = @stmt
            -- PRINT @createDef
        END -- if
        SELECT @counterDef = @counterDef + 1
        FETCH NEXT FROM createdef INTO @stmt
    END -- while
    CLOSE createdef 
    DEALLOCATE createdef 

    IF (@counterDef > 0)
    BEGIN
        PRINT 'Saved CREATE DEFAULT statement for recreation.'

        PRINT 'Drop DEFAULT definition on column ...'

        DECLARE dropDef CURSOR FOR
            SELECT 'ALTER TABLE ' + 
                   @tableSchema + '.' + OBJECT_NAME(a.parent_object_id) + 
                   ' DROP CONSTRAINT ' + 
                   OBJECT_NAME(a.OBJECT_ID) + ';'
            FROM sys.objects a, 
                 sys.default_constraints b, 
                 sys.columns c
            WHERE a.type_desc LIKE 'DEFAULT_CONSTRAINT' 
              AND a.object_id = b.object_id 
              AND c.object_id = b.parent_object_id 
              AND c.column_id = b.parent_column_id
              AND OBJECT_NAME(a.parent_object_id) = @tableName
              AND c.name = @columnName

        OPEN dropDef
        FETCH NEXT FROM dropDef INTO @stmt
        WHILE (@@fetch_status <> -1)
        BEGIN
            IF (@@fetch_status <> -2)
            BEGIN
                PRINT @stmt
                EXEC (@stmt)
            END -- if
            FETCH NEXT FROM dropDef INTO @stmt
        END -- while
        CLOSE dropDef
        DEALLOCATE dropDef

        PRINT 'Dropped DEFAULT definition on ' + @tableSchema + '.' + @tableName + '.' + @columnName + '.'
    END
    ELSE
    BEGIN
        PRINT 'No DEFAULT on ' + @tableSchema + '.' + @tableName + '.' + @columnName + ' found!'
    END -- if
    
    PRINT 'Migrate column ' + @tableSchema + '.' + @tableName + '.' + @columnName + ' to datatype ' + @newType + ' ...'

    SELECT @stmt = 'ALTER TABLE ' + @tableSchema + '.' + @tableName + ' ALTER COLUMN ' + @columnName + ' ' + @newType + ' ' + CASE @nullable WHEN 'YES' THEN 'NULL' ELSE 'NOT NULL' END + ';'
    PRINT @stmt
    EXEC (@stmt)

    PRINT 'Migration of column ' + @tableSchema + '.' + @tableName + '.' + @columnName + ' finished.'

    IF (@counterDef > 0)
    BEGIN
        PRINT 'Recreate DEFAULT definition on column ' + @tableSchema + '.' + @tableName + '.' + @columnName + '...'

        IF (@createDef IS NOT NULL)
        BEGIN
           PRINT @createDef
           EXEC (@createDef)
        END -- if

        PRINT 'Recreation of DEFAULT definition finished.'
    END -- if

    IF (@counterIdx > 0)
    BEGIN
        PRINT 'Recreate INDEX definition on column ' + @tableSchema + '.' + @tableName + '.' + @columnName + '...'

        IF (@createIdx1 IS NOT NULL)
        BEGIN
           PRINT @createIdx1
           EXEC (@createIdx1)
        END -- if

        IF (@createIdx2 IS NOT NULL)
        BEGIN
           PRINT @createIdx2
           EXEC (@createIdx2)
        END -- if

        IF (@createIdx3 IS NOT NULL)
        BEGIN
           PRINT @createIdx3
           EXEC (@createIdx3)
        END -- if

        IF (@createIdx4 IS NOT NULL)
        BEGIN
           PRINT @createIdx4
           EXEC (@createIdx4)
        END -- if

        IF (@createIdx5 IS NOT NULL)
        BEGIN
           PRINT @createIdx5
           EXEC (@createIdx5)
        END -- if

        PRINT 'Recreation of INDEX definition finished.'
    END

    IF (@counterUIIdx > 0)
    BEGIN
        PRINT 'Recreate UNIQUE INDEX definition on column ' + @tableSchema + '.' + @tableName + '.' + @columnName + '...'

        IF (@createUIIdx1 IS NOT NULL)
        BEGIN
           PRINT @createUIIdx1
           EXEC (@createUIIdx1)
        END -- if

        IF (@createUIIdx2 IS NOT NULL)
        BEGIN
           PRINT @createUIIdx2
           EXEC (@createUIIdx2)
        END -- if

        IF (@createUIIdx3 IS NOT NULL)
        BEGIN
           PRINT @createUIIdx3
           EXEC (@createUIIdx3)
        END -- if

        IF (@createUIIdx4 IS NOT NULL)
        BEGIN
           PRINT @createUIIdx4
           EXEC (@createUIIdx4)
        END -- if

        IF (@createUIIdx5 IS NOT NULL)
        BEGIN
           PRINT @createUIIdx5
           EXEC (@createUIIdx5)
        END -- if

        PRINT 'Recreation of UNIQUE INDEX definition finished.'
    END

    PRINT 'FINISHED migration of column ' + @tableSchema + '.' + @tableName + '.' + @columnName + '!'
GO
-- uc_migrateOneColumn