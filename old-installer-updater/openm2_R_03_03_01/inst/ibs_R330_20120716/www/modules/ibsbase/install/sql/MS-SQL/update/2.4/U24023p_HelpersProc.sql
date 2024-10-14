/******************************************************************************
 * Add the new p_refreshAllViews procedure.. <BR>
 *
 * @version     $Id:
 *
 * @author      Bernd Buchegger (DIBB) 050322
  ******************************************************************************
 */

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

/******************************************************************************
 * Refresh all views that are stored in the database. <BR>
 */
-- delete existing procedure:
EXEC p_dropProc 'p_refreshAllViews'
GO

-- create the new procedure:
CREATE PROCEDURE p_refreshAllViews
AS
DECLARE
    -- local variables:
    @cViewName            nvarchar(128),
    @cOwnerName           nvarchar(128),
    @fullname             nvarchar(256),
    @msg                  nvarchar(255),
    @l_error              INT            -- the actual error code
    -- assign constants:

    -- initialize local variables and return values:

-- body:

    PRINT 'refreshing all views in the database...'

    -- declare cursor
    DECLARE curViews CURSOR FOR
        SELECT  u.name, o.name
        FROM    sysobjects o, sysusers u
        WHERE   o.sysstat & 0xf = 2
            AND o.uid = u.uid
            AND o.status > 0
        ORDER BY u.name, o.name

    OPEN curViews
    FETCH NEXT FROM curViews INTO @cOwnerName, @cViewName
    WHILE (@@fetch_status <> -1)
    BEGIN
        IF (@@fetch_status <> -2)
        BEGIN
            SELECT @fullname = quotename(@cOwnerName) + '.' + quotename(@cViewName)
            PRINT @fullname
            EXEC ('sp_refreshview ' + '''' + @fullname + '''')
            SELECT @l_error = @@error
            IF  (@l_error <> 0)
	        BEGIN
                PRINT @l_error
            END -- if
        END -- if
        FETCH NEXT FROM curViews INTO @cOwnerName, @cViewName
    END -- while
    CLOSE curViews
    DEALLOCATE curViews

    PRINT 'finished.'
GO
-- p_refreshAllViews

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
