/******************************************************************************
 * Delete all data within the framework. <BR>
 *
 * @version     $Id: deleteAllTableContents.sql,v 1.6 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  980715
 ******************************************************************************
 */

-- ä => „, ö => ”, ü => ?, ß => á, Ä => Ž, Ö => ™, Ü => š

-- don't show count messages:
SET NOCOUNT ON
GO

-- declarations:
DECLARE @@TabName NVARCHAR (30), @@TabName_Kopfzeile NVARCHAR (75),
        @count INT, @stars NVARCHAR (15), @msg VARCHAR (255)

-- define cursor:
DECLARE TabNamen_Cursor CURSOR FOR 
    SELECT  name 
    FROM    sysobjects 
    WHERE   type = 'U'
        AND (name LIKE 'ibs_%')
    ORDER BY name

-- initialize counter and stars:
SELECT  @count = 0, @stars = REPLICATE (N'*', 15)

-- open the cursor:
OPEN    TabNamen_Cursor

-- get the first table:
FETCH NEXT FROM TabNamen_Cursor INTO @@TabName

-- loop through all found tables:
WHILE (@@FETCH_STATUS <> -1)            -- another table found?
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
/*    
        -- print table name:
        SELECT @@TabName_Kopfzeile = @stars + '  ' +             
            RTRIM (UPPER (@@TabName)) + '  ' + @stars
        PRINT @@TabName_Kopfzeile
*/        

        -- delete table content:
        EXEC (N'TRUNCATE TABLE ' + @@TabName)

/* the variable @@ROWCOUNT is not set by the TRUNCATE TABLE statement.
        -- print number of affected rows:
        SELECT @msg = CONVERT (VARCHAR (255), @@ROWCOUNT) + ' Reihen betroffen.'
        PRINT @msg
        PRINT ' '
*/        

        -- increment counter:
        SELECT  @count = @count + 1
    END -- if tuple was not deleted since opening cursor
    -- get next table:
    FETCH NEXT FROM TabNamen_Cursor INTO @@TabName
END -- while another table found

-- print final state:
/*
PRINT   ' '
PRINT   ' '
SELECT  @@TabName_Kopfzeile = @stars + '  KEINE WEITEREN TABELLEN'
            + '  ' + @stars
PRINT   @@TabName_Kopfzeile
PRINT   ' '
*/

SELECT  @msg = CONVERT (VARCHAR (255), @count) + ' Tabellen betroffen.'
PRINT   @msg

/*
PRINT   'TRUNCATE wurde f?r alle ibs Tabellen durchgef?hrt.'
*/

-- deallocate cursor:
DEALLOCATE TabNamen_Cursor
GO

-- show count messages again:
SET NOCOUNT OFF
GO
