/******************************************************************************
 * All stored procedures regarding basic database functions. <BR>
 *
 * @version     2.21.0013, 020508 KR
 *
 * @author      Mario Stegbauer (MS)  990805
 ******************************************************************************
 */


/******************************************************************************
 * Drop a table. <BR>
 *
 * @input parameters:
 * @param   ai_tableName        The name of the table to be dropped.
 *
 * @output parameters:
 */
-- delete existing procedure:
DECLARE
    -- local variables:
    @l_text                 NVARCHAR (255)   -- output text

-- body:
    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id ('p_getFullName')
                    AND sysstat & 0xf = 4)
    	DROP PROCEDURE p_getFullName

    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_getFullName')
                    AND sysstat & 0xf = 4)
    	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_getFullName

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting procedure p_getFullName.'
        PRINT @l_text                       -- print message
    END
GO


-- create the new procedure:
CREATE PROCEDURE p_getFullName
(
    -- input parameters:
    @ai_name                NVARCHAR (255),
    -- output parameters:
    @ao_fullName            NVARCHAR (255) OUTPUT
)
AS
-- body:
    -- set the full name:
    -- check if the name contains the name of the db user:
    IF (CHARINDEX ('.', @ai_name) > 0)  -- name contains db user?
    BEGIN
        -- use name as is:
        SELECT  @ao_fullName = @ai_name
    END -- if
    ELSE                                -- no db user in name
    BEGIN
        -- add db user to name:
        -- search for te object and the user
        SELECT  @ao_fullName = u.name + N'.' + @ai_name
        FROM    sysobjects o, sysusers u
        WHERE   o.id = object_id (@ai_name)
            AND o.uid = u.uid

        -- check if a corresponding object was found:
        IF (@@error > 0 OR @@ROWCOUNT <> 1)
        BEGIN
            SELECT  @ao_fullName = @ai_name
        END -- if
    END -- else
/* KR not always dbo as user
        SELECT  @ao_fullName = '#CONFVAR.ibsbase.dbOwner#.' + @ai_name
*/
GO
-- p_getFullName


/******************************************************************************
 * Drop a procedure. <BR>
 *
 * @input parameters:
 * @param   ai_procName         The name of the procedure to be dropped.
 *
 * @output parameters:
 */
-- delete existing procedure:
DECLARE
    -- local variables:
    @l_text                 NVARCHAR (255)   -- output text

-- body:
    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id ('p_dropProc')
                    AND sysstat & 0xf = 4)
    	DROP PROCEDURE p_dropProc

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting procedure p_dropProc.'
        PRINT @l_text                       -- print message
    END

    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_dropProc')
                    AND sysstat & 0xf = 4)
    	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_dropProc

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting procedure #CONFVAR.ibsbase.dbOwner#.p_dropProc.'
        PRINT @l_text                       -- print message
    END
GO

-- create the new procedure:
CREATE PROCEDURE p_dropProc
(
    @ai_procName            STOREDPROCNAME
)
AS
DECLARE
    -- local variables:
    @l_fullProcName         NVARCHAR (255),  -- full procedure name incl. db user
    @l_text                 NVARCHAR (255)   -- output text

-- body:
    -- get the full procedure name:
    EXEC p_getFullName @ai_procName, @l_fullProcName OUTPUT

    -- check if the procedure already exists:
    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id (@l_fullProcName)
                    AND sysstat & 0xf = 4)
    	EXEC ('DROP PROCEDURE ' + @l_fullProcName)

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting procedure ' + @ai_procName + N'.'
        PRINT @l_text                       -- print message
    END
GO
-- p_dropProc



/******************************************************************************
 * Drop a table. <BR>
 *
 * @input parameters:
 * @param   ai_tableName     The name of the table to be dropped.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_dropTable'
GO

-- create the new procedure:
CREATE PROCEDURE p_dropTable
(
    @ai_tableName            NVARCHAR(255)
)
AS
DECLARE
    -- local variables:
    @l_fullTableName         NVARCHAR (255),  -- full table name incl. db user
    @l_text                  NVARCHAR (255)   -- output text

-- body:
    -- get the full table name:
    EXEC p_getFullName @ai_tableName, @l_fullTableName OUTPUT

    -- check if the table already exists:
    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id (@l_fullTableName)
                    AND sysstat & 0xf = 3)
    	EXEC ('DROP TABLE ' + @l_fullTableName)

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting table ' + @ai_tableName + N'.'
        PRINT @l_text                       -- print message
    END
GO
-- p_dropTable


/******************************************************************************
 * Drop a view. <BR>
 *
 * @input parameters:
 * @param   ai_viewName     The name of the view to be dropped.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_dropView'
GO

-- create the new procedure:
CREATE PROCEDURE p_dropView
(
    @ai_viewName            NAME
)
AS
DECLARE
    -- local variables:
    @l_fullViewName         NVARCHAR (255),  -- full table name incl. db user
    @l_text                 NVARCHAR (255)   -- output text

-- body:
    -- get the full view name:
    EXEC p_getFullName @ai_viewName, @l_fullViewName OUTPUT

    -- check if the view already exists:
    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id (@l_fullViewName)
                    AND sysstat & 0xf = 2)
    	EXEC ('DROP VIEW ' + @l_fullViewName)

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting view ' + @ai_viewName + N'.'
        PRINT @l_text                       -- print message
    END
GO
-- p_dropView



/******************************************************************************
 * Drop a trigger. <BR>
 *
 * @input parameters:
 * @param   ai_trigName         The name of the trigger to be dropped.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_dropTrig'
GO

-- create the new procedure:
CREATE PROCEDURE p_dropTrig
(
    @ai_trigName            STOREDPROCNAME
)
AS
DECLARE
    -- local variables:
    @l_fullTrigName         NVARCHAR (255),  -- full trigger name incl. db user
    @l_text                 NVARCHAR (255)   -- output text

-- body:
    -- get the full trigger name:
    EXEC p_getFullName @ai_trigName, @l_fullTrigName OUTPUT

    -- check if the trigger already exists:
    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id (@l_fullTrigName)
                    AND sysstat & 0xf = 8)
    	EXEC ('DROP TRIGGER ' + @l_fullTrigName)

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting trigger ' + @ai_trigName + N'.'
        PRINT @l_text                       -- print message
    END
GO
-- p_dropTrig


/******************************************************************************
 * Drop a function. <BR>
 *
 * @input parameters:
 * @param   ai_funcName         The name of the function to be dropped.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_dropFunc'
GO

-- create the new procedure:
CREATE PROCEDURE p_dropFunc
(
    @ai_funcName            STOREDPROCNAME
)
AS
DECLARE
    -- local variables:
    @l_fullFuncName         NVARCHAR (255),  -- full function name incl. db user
    @l_text                 NVARCHAR (255)   -- output text

-- body:
    -- get the full function name:
    EXEC p_getFullName @ai_funcName, @l_fullFuncName OUTPUT

    -- check if the function already exists:
    IF EXISTS ( SELECT  *
                FROM    sysobjects
                WHERE   id = object_id (@l_fullFuncName)
--                    AND sysstat & 0xf = 8
                    AND type = 'FN'
              )
    	EXEC ('DROP FUNCTION ' + @l_fullFuncName)

    -- check if there occurred an error:
    IF (@@error <> 0)                    -- no error?
    BEGIN
        SELECT  @l_text = N'Error when deleting function ' + @ai_funcName + N'.'
        PRINT @l_text                       -- print message
    END
GO
-- p_dropFunc


/******************************************************************************
 * Open a transaction. <BR>
 * This procedure checks if there is already a transaction opened. <BR>
 * If there is no transaction open a new transaction is opened. <BR>
 * If there is already a transaction open a transaction savepoint with the
 * specified name is created. (A 's_' is always put in front of the savepoint
 * name.)
 *
 * @input parameters:
 * @param   ai_name             The name for the save point.
 *
 * @output parameters:
 * @return  The id of the transaction. This id contains the information if a
 *          new transaction was opened or just a savepoint was set and must be
 *          used for committing or undoing a transaction.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_openTrans'
GO

-- create the new procedure:
CREATE PROCEDURE p_openTrans
(
    -- input parameters:
    @ai_name                NVARCHAR (30)
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NONE                 INT,            -- neither transaction nor savepoint
    @c_TRANSACTION          INT,            -- create transaction
    @c_SAVEPOINT            INT,            -- set savepoint

    -- local variables:
    @l_name                 NVARCHAR (32),  -- name of savepoint
    @l_id                   INT             -- id of transaction

    -- assign constants:
SELECT
    @c_NONE                 = 0,
    @c_TRANSACTION          = 1000,
    @c_SAVEPOINT            = 2000

    -- initialize local variables:

-- body:
    -- check if there is already a transaction open:
    IF (@@TRANCOUNT <= 0)               -- no transaction open?
    BEGIN
        SELECT  @l_id = @c_TRANSACTION

        -- open a new transaction:
        BEGIN TRANSACTION               -- begin new TRANSACTION
    END -- if no transaction open
    ELSE                                -- already a transaction open
    BEGIN
        SELECT  @l_id = @c_SAVEPOINT, @l_name = N's_' + @ai_name

        -- set a save point for the current transaction:
        SAVE TRANSACTION @l_name
    END -- already a transaction open

    -- return the transaction id:
    RETURN @l_id
GO
-- p_openTrans


/******************************************************************************
 * Commit a transaction. <BR>
 * This procedure checks if there is a transaction opened. <BR>
 * If there is no transaction open nothing is done. <BR>
 * If the id belongs to a savepoint nothing is done (savepoints cannot be
 * committed). <BR>
 * If the id belongs to a transaction the transaction is committed.
 *
 * @input parameters:
 * @param   ai_name             The name for the save point.
 * @param   ai_id               Id of the opened transaction.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_commitTrans'
GO
-- create the new procedure:
CREATE PROCEDURE p_commitTrans
(
    -- input parameters:
    @ai_id                  INTEGER,
    @ai_name                NVARCHAR (30)
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NONE                 INT,            -- neither transaction nor savepoint
    @c_TRANSACTION          INT,            -- create transaction
    @c_SAVEPOINT            INT             -- set savepoint

    -- local variables:

    -- assign constants:
SELECT
    @c_NONE                 = 0,
    @c_TRANSACTION          = 1000,
    @c_SAVEPOINT            = 2000

    -- initialize local variables:

-- body:
    -- check if there is already a transaction open:
    IF (@@TRANCOUNT > 0)                -- transaction open?
    BEGIN
        -- check if we shall roll back to a savepoint or the whole transaction:
        IF (@ai_id = @c_TRANSACTION)    -- roll back whole transaction?
        BEGIN
            -- finish the transaction:
            COMMIT TRANSACTION          -- make changes permanent
        END -- if roll back whole transaction
    END -- if transaction open
GO
-- p_commitTrans


/******************************************************************************
 * Rollback a transaction. <BR>
 * This procedure checks if there is a transaction opened. <BR>
 * If there is no transaction open nothing is done. <BR>
 * If the id belongs to a savepoint then a rollback to the savepoint is done.
 * (A 's_' is always put in front of the savepoint name.)
 * If the id belongs to a transaction a rollback is done on the transaction.
 *
 * @input parameters:
 * @param   ai_name             The name for the save point.
 * @param   ai_id               Id of the opened transaction.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_rollbackTrans'
GO
-- create the new procedure:
CREATE PROCEDURE p_rollbackTrans
(
    -- input parameters:
    @ai_id                  INTEGER,
    @ai_name                NVARCHAR (30)
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NONE                 INT,            -- neither transaction nor savepoint
    @c_TRANSACTION          INT,            -- create transaction
    @c_SAVEPOINT            INT,            -- set savepoint

    -- local variables:
    @l_name                 NVARCHAR (32)   -- name of savepoint

    -- assign constants:
SELECT
    @c_NONE                 = 0,
    @c_TRANSACTION          = 1000,
    @c_SAVEPOINT            = 2000

    -- initialize local variables:

-- body:
    -- check if there is already a transaction open:
    IF (@@TRANCOUNT > 0)                -- transaction open?
    BEGIN
        -- check if we shall roll back to a savepoint or the whole transaction:
        IF (@ai_id = @c_TRANSACTION)    -- roll back whole transaction?
        BEGIN
            -- roll back to the beginning of the transaction:
            ROLLBACK TRANSACTION        -- undo changes
        END -- if roll back whole transaction
        ELSE                            -- roll back to savepoint
        BEGIN
            -- set the savepoint name:
            SELECT  @l_name = N's_' + @ai_name

            -- roll back to the save point:
            ROLLBACK TRANSACTION @l_name
        END -- else roll back to savepoint
    END -- if transaction open
GO
-- p_rollbackTrans


/******************************************************************************
 * Compute the oid out of tVersionId and id. <BR>
 * The oid is computed as concatenation of the tVersionId and the object's id.
 *
 * @input parameters:
 * @param   ai_tVersionId       The type version id.
 * @param   ai_id               The id of the object.
 *
 * @output parameters:
 * @param   ao_oid              The computed oid.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_createOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_createOid
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID,
    @ai_id                  ID,
    -- output parameters:
    @ao_oid                 OBJECTID OUTPUT
)
AS
-- body:
    -- convert the type version id and the id to raw and concatenate them:
    SELECT  @ao_oid = CONVERT (BINARY (4), @ai_tVersionId) +
                      CONVERT (BINARY (4), @ai_id)
GO
-- p_createOid


/******************************************************************************
 * Converts a binary value into a hex string. <BR>
 *
 * @input parameters:
 * @param   ai_bin              The binary value to be converted into a hex string.
 *
 * @output parameters:
 * @param   ao_hs           The returned hex string value.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_binaryToHexString'
GO

-- create the new procedure:
CREATE PROCEDURE p_binaryToHexString
(
    -- input parameters:
    @ai_bin                 VARBINARY (254),
    -- output parameters:
    @ao_hs                  NVARCHAR (254) OUTPUT
)
AS
DECLARE
    -- local variables:
    @l_binCount             INT

    -- assign constants:

    -- initialize local variables and return values:
SELECT
    @l_binCount = 1,
    @ao_hs = ''

-- body:
    -- conversion construct:
    WHILE (@l_binCount < LEN (@ai_bin) + 1)
    BEGIN
        SELECT  @ao_hs = @ao_hs +
                CHAR (SUBSTRING (@ai_bin, @l_binCount, 1) / 16 + 48 +
                      SUBSTRING (@ai_bin, @l_binCount, 1) / 16 / 10 * 7) +
                CHAR (SUBSTRING (@ai_bin, @l_binCount, 1) % 16 + 48 +
                      SUBSTRING (@ai_bin, @l_binCount, 1) % 16 / 10 * 7)
        SELECT @l_binCount = @l_binCount + 1
    END -- while
GO
-- p_binaryToHexString


/******************************************************************************
 * Converts an int value into a hex string. <BR>
 *
 * @input parameters:
 * @param   ai_int              The int value to be converted into a hex string.
 *
 * @output parameters:
 * @param   ao_hs               The returned hex string value.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_intToHexString'
GO

-- create the new procedure:
CREATE PROCEDURE p_intToHexString
(
    -- input parameters:
    @ai_int                 INT,
    -- output parameters:
    @ao_hs                  VARCHAR (4) OUTPUT
)
AS
DECLARE
    -- local variables:
    @l_digit1               INT,
    @l_digit2               INT,
    @l_digit3               INT,
    @l_digit4               INT

    -- assign constants:

    -- initialize local variables and return values:

-- body:
    -- conversion construct:
    IF (@ai_int < 65537)
    BEGIN
        SELECT  @l_digit1 = @ai_int / 4096
        SELECT  @l_digit2 = (@ai_int - (@l_digit1 * 4096)) / 256
        SELECT  @l_digit3 =
                    (@ai_int - (@l_digit1 * 4096) - (@l_digit2 * 256)) / 16
        SELECT  @l_digit4 = @ai_int -
                    (@l_digit1 * 4096) - (@l_digit2 * 256) - (@l_digit3 * 16)

        SELECT  @ao_hs =
                CHAR (@l_digit1 + 48 + (@l_digit1 / 10 * 7)) +
                CHAR (@l_digit2 + 48 + (@l_digit2 / 10 * 7)) +
                CHAR (@l_digit3 + 48 + (@l_digit3 / 10 * 7)) +
                CHAR (@l_digit4 + 48 + (@l_digit4 / 10 * 7))
    END -- if
GO
-- p_intToHexString



/******************************************************************************
 * Refresh all views that are stored in the database. <BR>
 *
 */
-- delete existing procedure:
EXEC p_dropProc N'p_refreshAllViews'
GO

-- create the new procedure:
CREATE PROCEDURE p_refreshAllViews
AS
DECLARE
    -- local variables:
    @cViewName            NVARCHAR(128),
    @cOwnerName           NVARCHAR(128),
    @fullname             NVARCHAR(256),
    @msg                  NVARCHAR(255),
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
