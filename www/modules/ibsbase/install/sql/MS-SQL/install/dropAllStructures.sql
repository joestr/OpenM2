/******************************************************************************
 * Drop all structures of the framework. <BR>
 *
 * @version     $Id: dropAllStructures.sql,v 1.5 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980816
 ******************************************************************************
 */

-- ä => „, ö => ”, ü => ?, î => á, - => Ž, Ö => ™, _ => š

-- don't show count messages:
SET NOCOUNT ON
GO

-------------------------------------------------------------------------------
-- DROP STORED PROCEDURES
-------------------------------------------------------------------------------

PRINT ''
PRINT ''
PRINT ''
PRINT 'DROP stored procedures...'
PRINT ''

-- declarations:
DECLARE @name NVARCHAR (61), @nameHeader NVARCHAR (75),
        @count INT, @stars NVARCHAR (15), @msg VARCHAR (255)

-- define cursor:
DECLARE nameCursor CURSOR FOR 
    SELECT  u.name + '.' + o.name AS name
    FROM    sysobjects o, sysusers u
    WHERE   o.type = 'P'
        AND o.uid = u.uid
    ORDER BY name

-- initialize counter and stars:
SELECT  @count = 0, @stars = REPLICATE (N'*', 15)

-- open the cursor:
OPEN    nameCursor

-- get the first object:
FETCH NEXT FROM nameCursor INTO @name

-- loop through all found objects:
WHILE (@@FETCH_STATUS <> -1)            -- another object found?
BEGIN
    -- Because @@FETCH_STATUS can have one of the three values 
    -- -2, -1 or 0 all these three cases must be checked.
    -- Here a tuple is ignored if it was deleted during this
    -- procedure.
    -- When the tuple was successfully gotten the specific code
    -- is executed.
    IF (@@FETCH_STATUS <> -2)           -- tuple was not deleted since opening 
                                        -- cursor?
    BEGIN
        -- print object name:
        SELECT @nameHeader = @stars + '  ' +             
            RTRIM (UPPER (@name)) + '  ' + @stars
        PRINT @nameHeader

        -- drop object:
        EXEC (N'DROP PROCEDURE ' + @name)

        -- print status message:
        SELECT @msg = 'Object removed.'
        PRINT @msg
        PRINT ' '

        -- increment counter:
        SELECT  @count = @count + 1
    END -- if tuple was not deleted since opening cursor
    -- get next object:
    FETCH NEXT FROM nameCursor INTO @name
END -- while another object found

-- print final state:
PRINT   ' '
PRINT   ' '
SELECT  @nameHeader = @stars + '  NO MORE OBJECTS'
            + '  ' + @stars
PRINT   @nameHeader
PRINT   ' '
SELECT  @msg = CONVERT (VARCHAR (255), @count) + ' procedures affected.'
PRINT   @msg
PRINT   'DROP was performed for all procedures of the application.'
PRINT ''
PRINT ''

-- deallocate cursor:
DEALLOCATE nameCursor
GO


-------------------------------------------------------------------------------
-- DROP VIEWS
-------------------------------------------------------------------------------

PRINT ''
PRINT ''
PRINT ''
PRINT 'DROP views...'
PRINT ''

-- declarations:
DECLARE @name NVARCHAR (61), @nameHeader NVARCHAR (75),
        @count INT, @stars NVARCHAR (15), @msg VARCHAR (255)

-- define cursor:
DECLARE nameCursor CURSOR FOR 
    SELECT  u.name + '.' + o.name AS name
    FROM    sysobjects o, sysusers u
    WHERE   o.type = 'V'
        AND o.uid = u.uid
    ORDER BY name

-- initialize counter and stars:
SELECT  @count = 0, @stars = REPLICATE (N'*', 15)

-- open the cursor:
OPEN    nameCursor

-- get the first object:
FETCH NEXT FROM nameCursor INTO @name

-- loop through all found objects:
WHILE (@@FETCH_STATUS <> -1)            -- another object found?
BEGIN
    -- Because @@FETCH_STATUS can have one of the three values 
    -- -2, -1 or 0 all these three cases must be checked.
    -- Here a tuple is ignored if it was deleted during this
    -- procedure.
    -- When the tuple was successfully gotten the specific code
    -- is executed.
    IF (@@FETCH_STATUS <> -2)           -- tuple was not deleted since opening 
                                        -- cursor?
    BEGIN
        -- print object name:
        SELECT @nameHeader = @stars + '  ' +             
            RTRIM (UPPER (@name)) + '  ' + @stars
        PRINT @nameHeader

        -- drop object:
        EXEC (N'DROP VIEW ' + @name)

        -- print status message:
        SELECT @msg = 'Object removed.'
        PRINT @msg
        PRINT ' '

        -- increment counter:
        SELECT  @count = @count + 1
    END -- if tuple was not deleted since opening cursor
    -- get next object:
    FETCH NEXT FROM nameCursor INTO @name
END -- while another object found

-- print final state:
PRINT   ' '
PRINT   ' '
SELECT  @nameHeader = @stars + '  NO MORE OBJECTS'
            + '  ' + @stars
PRINT   @nameHeader
PRINT   ' '
SELECT  @msg = CONVERT (VARCHAR (255), @count) + ' views affected.'
PRINT   @msg
PRINT   'DROP was performed for all views of the application.'

-- deallocate cursor:
DEALLOCATE nameCursor
GO


-------------------------------------------------------------------------------
-- DROP TABLES
-------------------------------------------------------------------------------

PRINT ''
PRINT ''
PRINT ''
PRINT 'DROP tables...'
PRINT ''

-- declarations:
DECLARE @name NVARCHAR (61), @nameHeader NVARCHAR (75),
        @count INT, @stars NVARCHAR (15), @msg VARCHAR (255)

-- define cursor:
DECLARE nameCursor CURSOR FOR 
    SELECT  u.name + '.' + o.name AS name
    FROM    sysobjects o, sysusers u
    WHERE   o.type = 'U'
        AND o.uid = u.uid
    ORDER BY name

-- initialize counter and stars:
SELECT  @count = 0, @stars = REPLICATE (N'*', 15)

-- open the cursor:
OPEN    nameCursor

-- get the first object:
FETCH NEXT FROM nameCursor INTO @name

-- loop through all found objects:
WHILE (@@FETCH_STATUS <> -1)            -- another object found?
BEGIN
    -- Because @@FETCH_STATUS can have one of the three values 
    -- -2, -1 or 0 all these three cases must be checked.
    -- Here a tuple is ignored if it was deleted during this
    -- procedure.
    -- When the tuple was successfully gotten the specific code
    -- is executed.
    IF (@@FETCH_STATUS <> -2)           -- tuple was not deleted since opening 
                                        -- cursor?
    BEGIN
        -- print object name:
        SELECT @nameHeader = @stars + N'  ' +             
            RTRIM (UPPER (@name)) + N'  ' + @stars
        PRINT @nameHeader

        -- drop object:
        EXEC (N'DROP TABLE ' + @name)

        -- print status message:
        SELECT @msg = 'Object removed.'
        PRINT @msg
        PRINT ' '

        -- increment counter:
        SELECT  @count = @count + 1
    END -- if tuple was not deleted since opening cursor
    -- get next object:
    FETCH NEXT FROM nameCursor INTO @name
END -- while another object found

-- print final state:
PRINT   ' '
PRINT   ' '
SELECT  @nameHeader = @stars + '  NO MORE OBJECTS'
            + '  ' + @stars
PRINT   @nameHeader
PRINT   ' '
SELECT  @msg = CONVERT (VARCHAR (255), @count) + ' tables affected.'
PRINT   @msg
PRINT   'DROP was performed for all tables of the application.'

-- deallocate cursor:
DEALLOCATE nameCursor
GO


-------------------------------------------------------------------------------
-- DROP DATA TYPES
-------------------------------------------------------------------------------

PRINT ''
PRINT ''
PRINT ''
PRINT 'DROP data types...'
PRINT ''

-- declarations:
DECLARE @name NVARCHAR (61), @nameHeader NVARCHAR (75),
        @count INT, @stars NVARCHAR (15), @msg VARCHAR (255)

-- define cursor:
DECLARE nameCursor CURSOR FOR 
    SELECT  name
    FROM    systypes
    WHERE   userType > 100
    ORDER BY userType DESC

-- initialize counter and stars:
SELECT  @count = 0, @stars = REPLICATE (N'*', 15)

-- open the cursor:
OPEN    nameCursor

-- get the first object:
FETCH NEXT FROM nameCursor INTO @name

-- loop through all found objects:
WHILE (@@FETCH_STATUS <> -1)            -- another object found?
BEGIN
    -- Because @@FETCH_STATUS can have one of the three values 
    -- -2, -1 or 0 all these three cases must be checked.
    -- Here a tuple is ignored if it was deleted during this
    -- procedure.
    -- When the tuple was successfully gotten the specific code
    -- is executed.
    IF (@@FETCH_STATUS <> -2)           -- tuple was not deleted since opening 
                                        -- cursor?
    BEGIN
        -- print object name:
        SELECT @nameHeader = @stars + N'  ' +             
            RTRIM (UPPER (@name)) + N'  ' + @stars
        PRINT @nameHeader

        -- drop object:
        EXEC sp_droptype @name

        -- print status message:
        SELECT @msg = 'Object removed.'
        PRINT @msg
        PRINT ' '

        -- increment counter:
        SELECT  @count = @count + 1
    END -- if tuple was not deleted since opening cursor
    -- get next object:
    FETCH NEXT FROM nameCursor INTO @name
END -- while another object found

-- print final state:
PRINT   ' '
PRINT   ' '
SELECT  @nameHeader = @stars + '  NO MORE OBJECTS'
            + '  ' + @stars
PRINT   @nameHeader
PRINT   ' '
SELECT  @msg = CONVERT (VARCHAR (255), @count) + ' types affected.'
PRINT   @msg
PRINT   'DROP was performed for all types of the application.'

-- deallocate cursor:
DEALLOCATE nameCursor
GO


-------------------------------------------------------------------------------
-- DROP DEFAULTS
-------------------------------------------------------------------------------

PRINT ''
PRINT ''
PRINT ''
PRINT 'DROP defaults...'
PRINT ''

-- declarations:
DECLARE @name NVARCHAR (61), @nameHeader NVARCHAR (75),
        @count INT, @stars NVARCHAR (15), @msg VARCHAR (255)

-- define cursor:
DECLARE nameCursor CURSOR FOR 
    SELECT  u.name + '.' + o.name AS name
    FROM    sysobjects o, sysusers u
    WHERE   o.type = 'D'
        AND o.uid = u.uid
    ORDER BY name

-- initialize counter and stars:
SELECT  @count = 0, @stars = REPLICATE (N'*', 15)

-- open the cursor:
OPEN    nameCursor

-- get the first object:
FETCH NEXT FROM nameCursor INTO @name

-- loop through all found objects:
WHILE (@@FETCH_STATUS <> -1)            -- another object found?
BEGIN
    -- Because @@FETCH_STATUS can have one of the three values 
    -- -2, -1 or 0 all these three cases must be checked.
    -- Here a tuple is ignored if it was deleted during this
    -- procedure.
    -- When the tuple was successfully gotten the specific code
    -- is executed.
    IF (@@FETCH_STATUS <> -2)           -- tuple was not deleted since opening 
                                        -- cursor?
    BEGIN
        -- print object name:
        SELECT @nameHeader = @stars + N'  ' +             
            RTRIM (UPPER (@name)) + N'  ' + @stars
        PRINT @nameHeader

        -- drop object:
        EXEC (N'DROP DEFAULT ' + @name)

        -- print status message:
        SELECT @msg = 'Object removed.'
        PRINT @msg
        PRINT ' '

        -- increment counter:
        SELECT  @count = @count + 1
    END -- if tuple was not deleted since opening cursor
    -- get next object:
    FETCH NEXT FROM nameCursor INTO @name
END -- while another object found

-- print final state:
PRINT   ' '
PRINT   ' '
SELECT  @nameHeader = @stars + '  NO MORE OBJECTS'
            + '  ' + @stars
PRINT   @nameHeader
PRINT   ' '
SELECT  @msg = CONVERT (VARCHAR (255), @count) + ' defaults affected.'
PRINT   @msg
PRINT   'DROP was performed for all Defaults of the application.'

-- deallocate cursor:
DEALLOCATE nameCursor
GO


-- show count messages again:
SET NOCOUNT OFF
GO
