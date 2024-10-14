/******************************************************************************
 * All stored procedures regarding the error handling. <BR>
 * dummyscript for ORACLE/MSSQL - installscriptcompatibility - it's needed
 * for NT/ORACLE installation and maybe there will be an errorhandling
 * on MSSQL to meanwhile ??? <BR>
 *
 * @version     2.00.0000, 13.04.2000
 *
 * @author      Andreas Jansa (AJ)  000413
 ******************************************************************************
 */

/******************************************************************************
 * Prepare an error which just occurred for later logging. <BR>
 * If the position is empty the resulting position is also empty.
 * Otherwise the created position contains the basic position and some
 * string characters (e.g. ': ') so that there can be further string
 * concatenations without the need to add some space characters. This value can
 * be directly used for the parameter ai_errorPos of procedure log_error. <BR>
 * Because the errorNo is returned as is the procedure may be called the
 * following way:
 *      EXEC @l_error = ibs_error.prepareError @@error, 'pos', @l_ePos OUTPUT
 * So the @l_error variable can be immediately used to check if there occurred
 * an error and go to the specific handler. <BR>
 * Disadvantage of this procedure: Don't use it for high performance
 * functionality because each call to such a procedure needs some time.
 *
 * @input parameters:
 * @param   ai_errorNo          The number of the error.
 * @param   ai_errorPos         The description of the position where the error
 *                              occurred.
 *
 * @output parameters:
 * @param   ao_ePos             The description of the position based on the
 *                              basic position description.
 * @return  The errorNo is returned.
 */
-- delete existing procedure:
EXEC p_dropProc N'ibs_error.prepareError'
GO

-- create the new procedure:
CREATE PROCEDURE ibs_error.prepareError
(
    -- input parameters:
    @ai_errorNo             INTEGER,
    @ai_errorPos            NVARCHAR (255),
    -- output parameters:
    @ao_ePos                NVARCHAR (255) OUTPUT
)
AS
-- body:
    -- set the text of the position:
    IF (@ai_errorPos <> NULL AND @ai_errorPos <> N'')
                                        -- there is a position name?
        -- set the text of the position:
        SELECt  @ao_ePos = @ai_errorPos -- + ': '
    ELSE                                -- there is no position
        -- clear the text of the position:
        SELECT  @ao_ePos = N''

    -- return the error number:
    RETURN  @ai_errorNo
GO
-- prepareError


/******************************************************************************
 * Prepare an error which just occurred for later logging. <BR>
 * If the position is empty the resulting position is also empty.
 * Otherwise the created position contains the basic position and some
 * string characters (e.g. ': ') so that there can be further string
 * concatenations without the need to add some space characters. This value can
 * be directly used for the parameter ai_errorPos of procedure log_error. <BR>
 * Because the errorNo and the counter value are returned as is the procedure
 * may be called the following way:
 *      EXEC @l_error = ibs_error.prepareError @@error, @@ROWCOUNT, 'pos',
 *          @l_ePos OUTPUT, @l_count OUTPUT
 * So the @l_error variable can be immediately used to check if there occurred
 * an error and go to the specific handler. <BR>
 * Disadvantage of this procedure: Don't use it for high performance
 * functionality because each call to such a procedure needs some time.
 *
 * @input parameters:
 * @param   ai_errorNo          The number of the error.
 * @param   ai_count            Some counter value.
 * @param   ai_errorPos         The description of the position where the error
 *                              occurred.
 *
 * @output parameters:
 * @param   ao_ePos             The description of the position based on the
 *                              basic position description.
 * @param   ao_count            The unchanged counter value.
 * @return  The errorNo is returned.
 */
-- delete existing procedure:
EXEC p_dropProc N'ibs_error.prepareErrorCount'
GO

-- create the new procedure:
CREATE PROCEDURE ibs_error.prepareErrorCount
(
    -- input parameters:
    @ai_errorNo             INTEGER,
    @ai_count               INTEGER,
    @ai_errorPos            NVARCHAR (255),
    -- output parameters:
    @ao_ePos                NVARCHAR (255) OUTPUT,
    @ao_count               INTEGER OUTPUT
)
AS
-- body:
    -- set the text of the position:
    IF (@ai_errorPos <> NULL AND @ai_errorPos <> N'')
                                        -- there is a position name?
        -- set the text of the position:
        SELECt  @ao_ePos = @ai_errorPos -- + ': '
    ELSE                                -- there is no position
        -- clear the text of the position:
        SELECT  @ao_ePos = N''

    -- set the count output value:
    SELECT  @ao_count = @ai_count

    -- return the error number:
    RETURN  @ai_errorNo
GO
-- prepareErrorCount


/******************************************************************************
 * Log an error. <BR>
 * The several description parameters are stored in the db as concatenation.
 * They are all optional. <BR>
 * There is NO COMMIT done within this procedure. To ensure that the data is
 * stored permanently in the error log there must be a commit afterwards.
 *
 * @input parameters:
 * @param   ai_errorType        The type of error.
 * @param   ai_errorProc        The procedure where the error occurred.
 * @param   ai_errorNo          The number of the error.
 * @param   ai_errorDesc1       The description of the error.
 * @param   ai_errorDesc2       The description of the error.
 * @param   ai_errorDesc3       The description of the error.
 * @param   ai_errorDesc4       The description of the error.
 * @param   ai_errorDesc5       The description of the error.
 * @param   ai_errorDesc6       The description of the error.
 * @param   ai_errorDesc7       The description of the error.
 * @param   ai_errorDesc8       The description of the error.
 * @param   ai_errorDesc9       The description of the error.
 * @param   ai_errorDesc10      The description of the error.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'ibs_error.log_error2'
GO

-- create the new procedure:
CREATE PROCEDURE ibs_error.log_error2
(
    -- input parameters:
    @ai_errorType           INTEGER,
    @ai_errorProc           NVARCHAR (254),
    @ai_errorPos            NVARCHAR (255),
    @ai_errorNo             INTEGER,
    @ai_errorDesc1          NVARCHAR (255) = N'',
    @ai_errorDesc2          NVARCHAR (255) = N'',
    @ai_errorDesc3          NVARCHAR (255) = N'',
    @ai_errorDesc4          NVARCHAR (255) = N'',
    @ai_errorDesc5          NVARCHAR (255) = N'',
    @ai_errorDesc6          NVARCHAR (255) = N'',
    @ai_errorDesc7          NVARCHAR (255) = N'',
    @ai_errorDesc8          NVARCHAR (255) = N'',
    @ai_errorDesc9          NVARCHAR (255) = N'',
    @ai_errorDesc10         NVARCHAR (255) = N''
)
AS
DECLARE
    -- constants:
    -- some important error types:
    @c_fatal_error          INT,
    @c_error                INT,
    @c_minor_error          INT,
    @c_warning              INT,
    @c_message              INT,
    @c_error_level          INT,

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_log_level            INT,
    @l_msg                  NVARCHAR (255)  -- the actual message text

    -- assign constants:
SELECT
    -- some important error types:
    @c_fatal_error          = 100,
    @c_error                = 500,
    @c_minor_error          = 1000,
    @c_warning              = 10000,
    @c_message              = 100000,
    @c_error_level          = 9999

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_log_level = @c_message

-- body:
    -- check if the error shall be logged:
    IF (@l_log_level > @ai_errorType)   -- log the error?
    BEGIN
        -- save the error and the message:
        INSERT INTO ibs_db_errors2
            (errorType, errorNo, errorDate, errorProc,
            errorPos, errorDesc)
        VALUES (@ai_errorType, @ai_errorNo, getDate (), @ai_errorProc,
                @ai_errorPos,
                @ai_errorDesc1 + @ai_errorDesc2 + @ai_errorDesc3 +
                @ai_errorDesc4 + @ai_errorDesc5 + @ai_errorDesc6 +
                @ai_errorDesc7 + @ai_errorDesc8 + @ai_errorDesc9 +
                @ai_errorDesc10)

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler
    END -- if log the error

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- no logging possible
    PRINT '-- ibs_error.log_error2 reported:'
    SELECT  @l_msg = N'   Errortype ' + CONVERT (VARCHAR, @ai_errorType) +
            N' in ' + @ai_errorProc
    PRINT @l_msg
    SELECT  @l_msg = N'   Description: ' +
            CONVERT (NVARCHAR (255), @ai_errorDesc1)
    PRINT @l_msg
GO
-- log_error2


/******************************************************************************
 * Log an error. <BR>
 * The several description parameters are stored in the db as concatenation.
 * They are all optional. <BR>
 * There is NO COMMIT done within this procedure. To ensure that the data is
 * stored permanently in the error log there must be a commit afterwards.
 *
 * @input parameters:
 * @param   ai_errorType        The type of error.
 * @param   ai_errorProc        The procedure where the error occurred.
 * @param   ai_errorNo          The number of the error.
 * @param   ai_errorDesc        The description of the error.
 * @param   ai_errorVar1        Name of a variable.
 * @param   ai_errorVal1        Value of the variable.
 * @param   ai_errorVar2        Name of a variable.
 * @param   ai_errorVal2        Value of the variable.
 * @param   ai_errorVar3        Name of a variable.
 * @param   ai_errorVal3        Value of the variable.
 * @param   ai_errorVar4        Name of a variable.
 * @param   ai_errorVal4        Value of the variable.
 * @param   ai_errorVar5        Name of a variable.
 * @param   ai_errorVal5        Value of the variable.
 * @param   ai_errorVar6        Name of a variable.
 * @param   ai_errorVal6        Value of the variable.
 * @param   ai_errorVar7        Name of a variable.
 * @param   ai_errorVal7        Value of the variable.
 * @param   ai_errorVar8        Name of a variable.
 * @param   ai_errorVal8        Value of the variable.
 * @param   ai_errorVar9        Name of a variable.
 * @param   ai_errorVal9        Value of the variable.
 * @param   ai_errorVar10       Name of a variable.
 * @param   ai_errorVal10       Value of the variable.
 * @param   ai_errorVar11       Name of a variable.
 * @param   ai_errorVal11       Value of the variable.
 * @param   ai_errorVar12       Name of a variable.
 * @param   ai_errorVal12       Value of the variable.
 * @param   ai_errorVar13       Name of a variable.
 * @param   ai_errorVal13       Value of the variable.
 * @param   ai_errorVar14       Name of a variable.
 * @param   ai_errorVal14       Value of the variable.
 * @param   ai_errorVar15       Name of a variable.
 * @param   ai_errorVal15       Value of the variable.
 * @param   ai_errorVar16       Name of a variable.
 * @param   ai_errorVal16       Value of the variable.
 * @param   ai_errorVar17       Name of a variable.
 * @param   ai_errorVal17       Value of the variable.
 * @param   ai_errorVar18       Name of a variable.
 * @param   ai_errorVal18       Value of the variable.
 * @param   ai_errorVar19       Name of a variable.
 * @param   ai_errorVal19       Value of the variable.
 * @param   ai_errorVar20       Name of a variable.
 * @param   ai_errorVal20       Value of the variable.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'ibs_error.logError'
GO

-- create the new procedure:
CREATE PROCEDURE ibs_error.logError
(
    -- input parameters:
    @ai_errorType           INTEGER,
    @ai_errorProc           NVARCHAR (254),
    @ai_errorNo             INTEGER,
    @ai_errorPos            NVARCHAR (255) = N'',
    @ai_errorVar1           NAME = N'',
    @ai_errorVal1           INTEGER = 0,
    @ai_errorVar2           NAME = N'',
    @ai_errorVal2           NVARCHAR (255) = N'',
    @ai_errorVar3           NAME = N'',
    @ai_errorVal3           INTEGER = 0,
    @ai_errorVar4           NAME = N'',
    @ai_errorVal4           NVARCHAR (255) = N'',
    @ai_errorVar5           NAME = N'',
    @ai_errorVal5           INTEGER = 0,
    @ai_errorVar6           NAME = N'',
    @ai_errorVal6           NVARCHAR (255) = N'',
    @ai_errorVar7           NAME = N'',
    @ai_errorVal7           INTEGER = 0,
    @ai_errorVar8           NAME = N'',
    @ai_errorVal8           NVARCHAR (255) = N'',
    @ai_errorVar9           NAME = N'',
    @ai_errorVal9           INTEGER = 0,
    @ai_errorVar10          NAME = N'',
    @ai_errorVal10          NVARCHAR (255) = N'',
    @ai_errorVar11          NAME = N'',
    @ai_errorVal11          INTEGER = 0,
    @ai_errorVar12          NAME = N'',
    @ai_errorVal12          NVARCHAR (255) = N'',
    @ai_errorVar13          NAME = N'',
    @ai_errorVal13          INTEGER = 0,
    @ai_errorVar14          NAME = N'',
    @ai_errorVal14          NVARCHAR (255) = N'',
    @ai_errorVar15          NAME = N'',
    @ai_errorVal15          INTEGER = 0,
    @ai_errorVar16          NAME = N'',
    @ai_errorVal16          NVARCHAR (255) = N'',
    @ai_errorVar17          NAME = N'',
    @ai_errorVal17          INTEGER = 0,
    @ai_errorVar18          NAME = N'',
    @ai_errorVal18          NVARCHAR (255) = N'',
    @ai_errorVar19          NAME = N'',
    @ai_errorVal19          INTEGER = 0,
    @ai_errorVar20          NAME = N'',
    @ai_errorVal20          NVARCHAR (255) = N''
)
AS
DECLARE
    -- constants:
    -- some important error types:
    @c_fatal_error          INT,
    @c_error                INT,
    @c_minor_error          INT,
    @c_warning              INT,
    @c_message              INT,
    @c_error_level          INT,

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_log_level            INT,
    @l_msg                  NVARCHAR (255)  -- the actual message text

    -- assign constants:
SELECT
    -- some important error types:
    @c_fatal_error          = 100,
    @c_error                = 500,
    @c_minor_error          = 1000,
    @c_warning              = 10000,
    @c_message              = 100000,
    @c_error_level          = 9999

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_log_level = @c_message

-- body:
    -- check if the error shall be logged:
    IF (@l_log_level > @ai_errorType)   -- log the error?
    BEGIN
        -- save the error and the message:
        INSERT INTO ibs_db_errors2
            (errorType, errorNo, errorDate, errorProc,
            errorPos, errorDesc)
        VALUES (@ai_errorType, @ai_errorNo, getDate (), @ai_errorProc,
                @ai_errorPos,
                N'Input: ' + @ai_errorVar1 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal1) +
                N', ' + @ai_errorVar2 + N' = ' + @ai_errorVal2 +
                N', ' + @ai_errorVar3 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal3) +
                N', ' + @ai_errorVar4 + N' = ' + @ai_errorVal4 +
                N', ' + @ai_errorVar5 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal5) +
                N', ' + @ai_errorVar6 + N' = ' + @ai_errorVal6 +
                N', ' + @ai_errorVar7 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal7) +
                N', ' + @ai_errorVar8 + N' = ' + @ai_errorVal8 +
                N', ' + @ai_errorVar9 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal9) +
                N', ' + @ai_errorVar10 + N' = ' + @ai_errorVal10 +
                N', ' + @ai_errorVar11 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal11) +
                N', ' + @ai_errorVar12 + N' = ' + @ai_errorVal12 +
                N', ' + @ai_errorVar13 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal13) +
                N', ' + @ai_errorVar14 + N' = ' + @ai_errorVal14 +
                N', ' + @ai_errorVar15 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal5) +
                N', ' + @ai_errorVar16 + N' = ' + @ai_errorVal6 +
                N', ' + @ai_errorVar17 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal17) +
                N', ' + @ai_errorVar18 + N' = ' + @ai_errorVal18 +
                N', ' + @ai_errorVar19 + N' = ' +
                    CONVERT (NVARCHAR, @ai_errorVal19) +
                N', ' + @ai_errorVar20 + N' = ' + @ai_errorVal20)

        -- check if there occurred an error:
        SELECT @l_error = @@error       -- store the error code
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call exception handler
    END -- if log the error

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- no logging possible
    PRINT '-- ibs_error.logError reported:'
    SELECT  @l_msg = N'   Errortype ' + CONVERT (VARCHAR, @ai_errorType) +
            N' in ' + @ai_errorProc + N'.' + @ai_errorPos
    PRINT @l_msg
GO
-- logError


/******************************************************************************
 * Log an error. <BR>
 * There is NO COMMIT done within this procedure. To ensure that the data is
 * stored permanently in the error log there must be a commit afterwards.
 *
 * @input parameters:
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'ibs_error.cleanErrorLog2'
GO

-- create the new procedure:
CREATE PROCEDURE ibs_error.cleanErrorLog2
AS
DECLARE
    -- constants:

    -- local variables:
    @l_error                INT             -- the actual error code

    -- assign constants:

    -- initialize local variables:
SELECT
    @l_error = 0

-- body:
    -- delete the entries of the error table:
    DELETE  ibs_db_errors2

    -- check if there occurred an error:
    SELECT @l_error = @@error           -- store the error code
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call exception handler

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    EXEC ibs_error.logError 500, N'ibs_error.clean_error_log', @l_error, N''
GO
-- cleanErrorLog2

/******************************************************************************
 * Display all errors. <BR>
 * The errors are selected out of the database and displayed in a formatted
 * manner.
 *
 * @input parameters:
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'ibs_error.printErrors'
GO

-- create the new procedure:
CREATE PROCEDURE ibs_error.printErrors
AS
-- body:
    SELECT  errorType, errorNo,
            CONVERT (VARCHAR (24), errorDate, 113) AS errorDate,
            CONVERT (NVARCHAR (25), errorProc) AS errorProc,
            CONVERT (NVARCHAR (25), errorPos) AS errorPos, errorDesc
    FROM    ibs_db_errors2
    ORDER BY errorDate ASC
GO
-- printErrors
