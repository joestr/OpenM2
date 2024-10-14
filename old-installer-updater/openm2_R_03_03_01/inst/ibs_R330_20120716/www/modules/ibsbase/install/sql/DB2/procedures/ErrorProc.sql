-------------------------------------------------------------------------------
-- All stored procedures regarding the error handling. <BR>
-- dummyscript for ORACLE/MSSQL - installscriptcompatibility - it's needed
-- for NT/ORACLE installation and maybe there will be an errorhandling
-- on MSSQL to meanwhile ??? <BR>
--
-- @version     $Revision: 1.5 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020806
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Prepare an error which just occurred for later logging. <BR>
-- If the position is empty the resulting position is also empty.
-- Otherwise the created position contains the basic position and some
-- string characters (e.g. ': ') so that there can be further string
-- concatenations without the need to add some space characters. This value can
-- be directly used for the parameter ai_errorPos of procedure log_error. <BR>
-- Because the errorNo is returned as is the procedure may be called the
-- following way:
--      EXEC @l_error = ibs_error.prepareError;@error, 'pos', @l_ePos OUTPUT
-- So the @l_error variable can be immediately used to check if there occurred
-- an error and go to the specific handler. <BR>
-- Disadvantage of this procedure: Don't use it for high performance
-- functionality because each call to such a procedure needs some time.
--
-- @input parameters:
-- @param   ai_errorNo          The number of the error.
-- @param   ai_errorPos         The description of the position where the error
--                              occurred.
--
-- @output parameters:
-- @param   ao_ePos             The description of the position based on the
--                              basic position description.
-- @return  The errorNo is returned.
--
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('prepareError');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.prepareError(
    -- input parameters:
    IN  ai_errorNo          INT,
    IN  ai_errorPos         VARCHAR (255),
    -- output parameters:
    OUT ao_ePos             VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
-- body:
    -- set the text of the position:
    IF ai_errorPos <> CAST(NULL AS VARCHAR (255)) AND ai_errorPos <> '' THEN 
                                        -- there is a position name?
        -- set the text of the position:
        SET ao_ePos = ai_errorPos;      -- + ': '
    ELSE                                -- there is no position
        -- clear the text of the position:
        SET ao_ePos = '';
    END IF;

    -- return the error number:
    RETURN ai_errorNo;
END;
-- prepareError
--------------------------------------------------------------------------------
-- Prepare an error which just occurred for later logging. <BR>
-- If the position is empty the resulting position is also empty.
-- Otherwise the created position contains the basic position and some
-- string characters (e.g. ': ') so that there can be further string
-- concatenations without the need to add some space characters. This value can
-- be directly used for the parameter ai_errorPos of procedure log_error. <BR>
-- Because the errorNo and the counter value are returned as is the procedure
-- may be called the following way:
--      EXEC @l_error = ibs_error.prepareError;@error,;@ROWCOUNT, 'pos',
--          @l_ePos OUTPUT, @l_count OUTPUT
-- So the @l_error variable can be immediately used to check if there occurred
-- an error and go to the specific handler. <BR>
-- Disadvantage of this procedure: Don't use it for high performance
-- functionality because each call to such a procedure needs some time.
--
-- @input parameters:
-- @param   ai_errorNo          The number of the error.
-- @param   ai_count            Some counter value.
-- @param   ai_errorPos         The description of the position where the error
--                              occurred.
--
-- @output parameters:
-- @param   ao_ePos             The description of the position based on the
--                              basic position description.
-- @param   ao_count            The unchanged counter value.
-- @return  The errorNo is returned.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('prepareErrorCount');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.prepareErrorCount(
    IN  ai_errorNo          INT,
    IN  ai_count            INT,
    IN  ai_errorPos         VARCHAR (255),
    OUT ao_ePos             VARCHAR (255),
    OUT ao_count            INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- set the text of the position:
    IF ai_errorPos <> CAST(NULL AS VARCHAR (255)) AND ai_errorPos <> '' THEN 
                                        -- there is a position name?
        -- set the text of the position:
        SET ao_ePos = ai_errorPos;      -- + ': '
    ELSE 
        -- clear the text of the position:
        SET ao_ePos = '';
    END IF;
  
    -- set the count output value:
    SET ao_count = ai_count;
  
    -- return the error number:
    RETURN ai_errorNo;
END;
-- prepareErrorCount

--------------------------------------------------------------------------------
-- Log an error. <BR>
-- The several description parameters are stored in the db as concatenation.
-- They are all optional. <BR>
-- There is NO COMMIT done within this procedure. To ensure that the data is
-- stored permanently in the error log there must be a commit afterwards.
--
-- @input parameters:
-- @param   ai_errorType        The type of error.
-- @param   ai_errorProc        The procedure where the error occurred.
-- @param   ai_errorNo          The number of the error.
-- @param   ai_errorDesc1       The description of the error.
-- @param   ai_errorDesc2       The description of the error.
-- @param   ai_errorDesc3       The description of the error.
-- @param   ai_errorDesc4       The description of the error.
-- @param   ai_errorDesc5       The description of the error.
-- @param   ai_errorDesc6       The description of the error.
-- @param   ai_errorDesc7       The description of the error.
-- @param   ai_errorDesc8       The description of the error.
-- @param   ai_errorDesc9       The description of the error.
-- @param   ai_errorDesc10      The description of the error.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('log_error2');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.log_error2(
    -- input parameters:
    IN  ai_errorType        INT,
    IN  ai_errorProc        VARCHAR (254),
    IN  ai_errorPos         VARCHAR (255),
    IN  ai_errorNo          INT,
    IN  ai_errorDesc1       VARCHAR (255),
    IN  ai_errorDesc2       VARCHAR (255),
    IN  ai_errorDesc3       VARCHAR (255),
    IN  ai_errorDesc4       VARCHAR (255),
    IN  ai_errorDesc5       VARCHAR (255),
    IN  ai_errorDesc6       VARCHAR (255),
    IN  ai_errorDesc7       VARCHAR (255),
    IN  ai_errorDesc8       VARCHAR (255),
    IN  ai_errorDesc9       VARCHAR (255),
    IN  ai_errorDesc10      VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    -- some important error types:
    DECLARE c_fatal_error   INT;
    DECLARE c_error         INT;
    DECLARE c_minor_error   INT;
    DECLARE c_warning       INT;
    DECLARE c_message       INT;
    DECLARE c_error_level   INT;

    -- local variables:
    DECLARE l_log_level     INT;
    DECLARE l_msg           VARCHAR (255); -- the actual message text
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
 
    -- assign constants:
    SET c_fatal_error       = 100;
    SET c_error             = 500;
    SET c_minor_error       = 1000;
    SET c_warning           = 10000;
    SET c_message           = 100000;
    SET c_error_level       = 9999;
  
    -- initialize local variables:
    SET l_log_level         = c_message;
  
-- body:
    -- check if the error shall be logged:
    IF l_log_level > ai_errorType THEN  -- log the error?
        -- save the error and the message:
        SET l_sqlcode = 0;

        INSERT INTO IBSDEV1.ibs_db_errors
            (errorType, errorNo, errorDate, errorProc,
            errorPos, errorDesc)
        VALUES (ai_errorType, ai_errorNo, CURRENT TIMESTAMP, ai_errorProc,
            ai_errorPos, ai_errorDesc1 || ai_errorDesc2 || ai_errorDesc3 ||
            ai_errorDesc4 || ai_errorDesc5 || ai_errorDesc6 ||
            ai_errorDesc7 || ai_errorDesc8 || ai_errorDesc9 ||
            ai_errorDesc10);
    
    
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
            GOTO exception1;             -- call exception handler 
        END IF;
    END IF;                             -- if log the error
  
    -- finish the procedure:
    RETURN 0;
  
exception1:                              -- an error occurred
    -- no logging possible
    SET l_msg = 'ibs_error.logError reported:';
    CALL IBSDEV1.dbo.p_Debug( l_msg );
    SET l_msg =
        '   Errortype ' || CAST(rtrim(CHAR (ai_errorType)) AS VARCHAR (30)) ||
        ' in ' || ai_errorProc;
  
    CALL IBSDEV1.dbo.p_Debug( l_msg );
    SET l_msg = '   Description: ' || CAST(ai_errorDesc1 AS VARCHAR (255));
    CALL IBSDEV1.dbo.p_Debug( l_msg );
END;
-- log_error2

--------------------------------------------------------------------------------
-- Log an error. <BR>
-- The several description parameters are stored in the db as concatenation.
-- They are all optional. <BR>
-- There is NO COMMIT done within this procedure. To ensure that the data is
-- stored permanently in the error log there must be a commit afterwards.
--
-- @input parameters:
-- @param   ai_errorType        The type of error.
-- @param   ai_errorProc        The procedure where the error occurred.
-- @param   ai_errorNo          The number of the error.
-- @param   ai_errorDesc        The description of the error.
-- @param   ai_errorVar1        Name of a variable.
-- @param   ai_errorVal1        Value of the variable.
-- @param   ai_errorVar2        Name of a variable.
-- @param   ai_errorVal2        Value of the variable.
-- @param   ai_errorVar3        Name of a variable.
-- @param   ai_errorVal3        Value of the variable.
-- @param   ai_errorVar4        Name of a variable.
-- @param   ai_errorVal4        Value of the variable.
-- @param   ai_errorVar5        Name of a variable.
-- @param   ai_errorVal5        Value of the variable.
-- @param   ai_errorVar6        Name of a variable.
-- @param   ai_errorVal6        Value of the variable.
-- @param   ai_errorVar7        Name of a variable.
-- @param   ai_errorVal7        Value of the variable.
-- @param   ai_errorVar8        Name of a variable.
-- @param   ai_errorVal8        Value of the variable.
-- @param   ai_errorVar9        Name of a variable.
-- @param   ai_errorVal9        Value of the variable.
-- @param   ai_errorVar10       Name of a variable.
-- @param   ai_errorVal10       Value of the variable.
-- @param   ai_errorVar11       Name of a variable.
-- @param   ai_errorVal11       Value of the variable.
-- @param   ai_errorVar12       Name of a variable.
-- @param   ai_errorVal12       Value of the variable.
-- @param   ai_errorVar13       Name of a variable.
-- @param   ai_errorVal13       Value of the variable.
-- @param   ai_errorVar14       Name of a variable.
-- @param   ai_errorVal14       Value of the variable.
-- @param   ai_errorVar15       Name of a variable.
-- @param   ai_errorVal15       Value of the variable.
-- @param   ai_errorVar16       Name of a variable.
-- @param   ai_errorVal16       Value of the variable.
-- @param   ai_errorVar17       Name of a variable.
-- @param   ai_errorVal17       Value of the variable.
-- @param   ai_errorVar18       Name of a variable.
-- @param   ai_errorVal18       Value of the variable.
-- @param   ai_errorVar19       Name of a variable.
-- @param   ai_errorVal19       Value of the variable.
-- @param   ai_errorVar20       Name of a variable.
-- @param   ai_errorVal20       Value of the variable.
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('logError');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.logError (
    IN  ai_errorType        INT,
    IN  ai_errorProc        VARCHAR (254),
    IN  ai_errorNo          INT,
    IN  ai_errorPos         VARCHAR (255),
    IN  ai_errorVar1        VARCHAR (64),-- NAME
    IN  ai_errorVal1        INT,
    IN  ai_errorVar2        VARCHAR (64),-- NAME
    IN  ai_errorVal2        VARCHAR (255),
    IN  ai_errorVar3        VARCHAR (64),-- NAME
    IN  ai_errorVal3        INT,
    IN  ai_errorVar4        VARCHAR (64),-- NAME
    IN  ai_errorVal4        VARCHAR (255),
    IN  ai_errorVar5        VARCHAR (64),-- NAME
    IN  ai_errorVal5        INT,
    IN  ai_errorVar6        VARCHAR (64),-- NAME
    IN  ai_errorVal6        VARCHAR (255),
    IN  ai_errorVar7        VARCHAR (64),-- NAME
    IN  ai_errorVal7        INT,
    IN  ai_errorVar8        VARCHAR (64),-- NAME
    IN  ai_errorVal8        VARCHAR (255),
    IN  ai_errorVar9        VARCHAR (64),-- NAME
    IN  ai_errorVal9        INT,
    IN  ai_errorVar10       VARCHAR (64),-- NAME
    IN  ai_errorVal10       VARCHAR (255),
    IN  ai_errorVar11       VARCHAR (64),-- NAME
    IN  ai_errorVal11       INT,
    IN  ai_errorVar12       VARCHAR (64),-- NAME
    IN  ai_errorVal12       VARCHAR (255),
    IN  ai_errorVar13       VARCHAR (64),-- NAME
    IN  ai_errorVal13       INT,
    IN  ai_errorVar14       VARCHAR (64),-- NAME
    IN  ai_errorVal14       VARCHAR (255),
    IN  ai_errorVar15       VARCHAR (64),-- NAME
    IN  ai_errorVal15       INT,
    IN  ai_errorVar16       VARCHAR (64),-- NAME
    IN  ai_errorVal16       VARCHAR (255),
    IN  ai_errorVar17       VARCHAR (64),-- NAME
    IN  ai_errorVal17       INT,
    IN  ai_errorVar18       VARCHAR (64),-- NAME
    IN  ai_errorVal18       VARCHAR (255),
    IN  ai_errorVar19       VARCHAR (64),-- NAME
    IN  ai_errorVal19       INT,
    IN  ai_errorVar20       VARCHAR (64),-- NAME
    IN  ai_errorVal20       VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    -- some important error types:
    DECLARE c_fatal_error   INT;
    DECLARE c_error         INT;
    DECLARE c_minor_error   INT;
    DECLARE c_warning       INT;
    DECLARE c_message       INT;
    DECLARE c_error_level   INT;

    -- local variables:
    DECLARE l_log_level     INT;
    DECLARE l_msg           VARCHAR (255);
    DECLARE l_sqlcode       INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
   
    -- assign constants:
    -- some important error types:
    SET c_fatal_error       = 100;
    SET c_error             = 500;
    SET c_minor_error       = 1000;
    SET c_warning           = 10000;
    SET c_message           = 100000;
    SET c_error_level       = 9999;
    -- initialize local variables:
    SET l_log_level         = c_message;

-- body:

    IF l_log_level > ai_errorType THEN  -- log the error?
        -- save the error and the message:
        SET l_sqlcode = 0;
        INSERT INTO IBSDEV1.ibs_db_errors
            (errorType, errorNo, errorDate, errorProc,
            errorPos, errorDesc)
        VALUES (ai_errorType, ai_errorNo, CURRENT TIMESTAMP, ai_errorProc,
            ai_errorPos, 'Input: ' || ai_errorVar1 || ' = ' ||
            CHAR (ai_errorVal1) || ', ' || ai_errorVar2 || ' = ' ||
	        ai_errorVal2 || ', ' || ai_errorVar3 || ' = ' ||
	        CHAR (ai_errorVal3) || ', ' || ai_errorVar4 || ' = ' ||
	        ai_errorVal4 || ', ' || ai_errorVar5 || ' = ' ||
            CHAR (ai_errorVal5) || ', ' || ai_errorVar6 || ' = ' ||
            ai_errorVal6 || ', ' || ai_errorVar7 || ' = ' ||
            CHAR (ai_errorVal7) || ', ' || ai_errorVar8 || ' = ' ||
            ai_errorVal8 || ', ' || ai_errorVar9 || ' = ' ||
            CHAR (ai_errorVal9) || ', ' || ai_errorVar10 || ' = ' ||
            ai_errorVal10 || ', ' || ai_errorVar11 || ' = ' ||
            CHAR (ai_errorVal11) || ', ' || ai_errorVar12 || ' = ' ||
            ai_errorVal12 || ', ' || ai_errorVar13 || ' = ' ||
            CHAR (ai_errorVal13) || ', ' || ai_errorVar14 || ' = ' ||
            ai_errorVal14 || ', ' || ai_errorVar15 || ' = ' ||
            CHAR (ai_errorVal15) || ', ' || ai_errorVar16 || ' = ' ||
            ai_errorVal16 || ', ' || ai_errorVar17 || ' = ' || 
            CHAR (ai_errorVal17) || ', ' || ai_errorVar18 || ' = ' ||
            ai_errorVal18 || ', ' || ai_errorVar19 || ' = ' ||
            CHAR (ai_errorVal19) || ', ' || ai_errorVar20 || ' = ' ||
            CHAR (ai_errorVal20) );
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
            GOTO exception1;             -- call exception handler  
        END IF;
    END IF; -- if log the error

    -- finish the procedure:
    RETURN 0;

exception1:                              -- an error occurred

    -- no logging possible
    SET l_msg = ' ibs_error.logError reported:';
    CALL IBSDEV1.p_Debug( l_msg );
    SET l_msg =
        '   Errortype ' || CAST(rtrim(CHAR (ai_errorType)) AS VARCHAR (30)) ||
        ' in ' || ai_errorProc || '.' || ai_errorPos;
    CALL IBSDEV1.p_Debug( l_msg );
END;
-- logError

--------------------------------------------------------------------------------
-- Log an error. <BR>
-- There is NO COMMIT done within this procedure. To ensure that the data is
-- stored permanently in the error log there must be a commit afterwards.
--
-- @input parameters:
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('cleanErrorLog2');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.cleanErrorLog2()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_ePos VARCHAR ( 255 );
  
    -- local variables:
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables:
    SET l_sqlcode = 0;

-- body:
    -- delete the entries of the error table:
    DELETE FROM IBSDEV1.ibs_db_errors;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
        GOTO exception1;                 -- call exception handler
    END IF;

    -- finish the procedure:
    RETURN 0;
  
    exception1:                          -- an error occurred 
  
    CALL IBSDEV1.logError (500, 'ibs_error.clean_error_log', l_sqlcode, l_ePos,
        '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '');
END;
-- cleanErrorLog2

--------------------------------------------------------------------------------
-- Display all errors. <BR>
-- The errors are selected out of the database and displayed in a formatted
-- manner.
--
-- @input parameters:
--
-- @output parameters:
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('printErrors');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.printErrors()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- local variables:
    DECLARE l_sqlcode INT DEFAULT 0;
  
-- body:
    DECLARE temp_cursor CURSOR WITH HOLD  WITH RETURN FOR 
        SELECT errorType, errorNo, CHAR (errorDate) AS errorDate,
            CAST(errorProc AS VARCHAR (25)) AS errorProc,
            CAST(errorPos AS VARCHAR (25)) AS errorPos, errorDesc 
        FROM IBSDEV1.ibs_db_errors
        ORDER BY errorDate ASC;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  OPEN temp_cursor;
END;
-- printErrors
