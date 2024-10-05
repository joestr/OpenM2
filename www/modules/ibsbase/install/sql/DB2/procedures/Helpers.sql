--------------------------------------------------------------------------------
-- All stored procedures regarding basic database functions. <BR>
--
-- @version     $Revision: 1.5 $, $Date: 2003/10/21 22:37:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020807
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
--  


-- delete existing procedure:
DROP PROCEDURE IBSDEV1.p_dropProc;

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_dropProc
(
    IN    ai_name           VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    -- local variables
    DECLARE SQLCODE         INT;
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;
    DECLARE c_NOT_OK        INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_result        INT;
    DECLARE l_Exec          VARCHAR (255);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_result = c_NOT_OK;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_result = c_NOT_OK;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_result = c_NOT_OK;

    -- initialize lokal variables and return values:
    SET l_result = c_NOT_OK;

-- body:
    IF EXISTS   (
                    SELECT * 
                    FROM QSYS2.SYSPROCS
                    WHERE UPPER( ROUTINE_NAME ) = UPPER( ai_name )
                        AND  UPPER( ROUTINE_SCHEMA ) = UPPER( 'IBSDEV1' )
                 ) THEN
        SET l_Exec = 'DROP PROCEDURE IBSDEV1.' || ai_name;
        EXECUTE IMMEDIATE l_Exec;
        SET l_result = c_ALL_RIGHT;
    END IF;

    RETURN l_result;
END;
-- p_dropProc


-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_dropFunc');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_dropFunc
(
    IN    ai_name           VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;
    DECLARE c_NOT_OK        INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_result        INT;
    DECLARE l_Exec          VARCHAR (255);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_result = c_NOT_OK;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_result = c_NOT_OK;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_result = c_NOT_OK;

    -- initialize local variables and return values:
    SET l_result = c_NOT_OK;

-- body:
    IF EXISTS   (
                    SELECT * 
                    FROM QSYS2.SYSFUNCS
                    WHERE UPPER( ROUTINE_NAME ) = UPPER( ai_name )
                        AND  UPPER( ROUTINE_SCHEMA ) = UPPER('IBSDEV1' )
                 ) THEN 
        SET l_Exec = 'DROP FUNCTION IBSDEV1.' || ai_name;
        EXECUTE IMMEDIATE l_Exec;
        SET l_result = c_ALL_RIGHT;
    END IF;

    RETURN l_result;
END;
-- p_dropFunc


-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_dropView');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_dropView
(
    IN    ai_name           VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_Exec          VARCHAR (255);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    IF EXISTS   (
                    SELECT * 
                    FROM IBSDEV1.SYSVIEWS
                    WHERE UPPER( NAME ) = UPPER( ai_name )
                 ) THEN
        SET l_Exec = 'DROP VIEW IBSDEV1.' || ai_name;
        EXECUTE IMMEDIATE l_Exec;
    END IF;

    RETURN l_sqlcode + 1;
END;
-- p_dropView


-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_dropTrig');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_dropTrig
(
    IN    ai_name           VARCHAR (255)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_Exec          VARCHAR (255);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    IF EXISTS   (
                    SELECT * 
                    FROM IBSDEV1.SYSTRIGGERS
                    WHERE UPPER( TRIGGER_NAME ) = UPPER( ai_name )
                 ) THEN
        SET l_Exec = 'DROP TRIGGER IBSDEV1.' || ai_name;
        EXECUTE IMMEDIATE l_Exec;
    END IF;

    RETURN l_sqlcode + 1;
END;
-- p_dropTrig


-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_debug');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_debug
(
    IN    ai_text           VARCHAR (255)
)
LANGUAGE SQL
READS SQL DATA
BEGIN
    -- just a dummy:
--CALL IBSDEV1.logError (200, 'p_debug', 0, ai_text, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- Here will be print to screen code
    RETURN 0;
END;
-- p_debug


CALL IBSDEV1.p_dropFunc ('B_AND');

-- create the new function:
CREATE FUNCTION IBSDEV1.b_AND
(
    ai_N1                   INTEGER,
    ai_N2                   INTEGER
)
RETURNS INTEGER
LANGUAGE SQL
DETERMINISTIC
BEGIN
    -- local variables:
    DECLARE l_Scaler        INT DEFAULT 0;
    DECLARE l_M1            INT;
    DECLARE l_M2            INT;
    DECLARE l_Result        INT DEFAULT 0;

    -- initialize local variables and return values:
    SET l_M1 = ai_N1;
    SET l_M2 = ai_N2;

-- body:
    loop_point:
    REPEAT
        SET l_Result = l_Result + MOD(l_M1,2) * MOD(l_M2,2) * power(2,l_Scaler);

        SET l_Scaler = l_Scaler + 1;
        SET l_M1 = l_M1/2;
        SET l_M2 = l_M2/2;

        UNTIL NOT ( l_M1 > 0
            AND l_M2 > 0
            AND l_Scaler < 31 )
    END REPEAT loop_point;

    RETURN l_Result;
END;
-- b_AND


--------------------------------------------------------------------------------
--  
--------------------------------------------------------------------------------
-- delete existing function:
CALL IBSDEV1.p_dropFunc ('b_XOR');

-- create the new function:
CREATE FUNCTION IBSDEV1.b_XOR
(
    ai_N1                   INTEGER,
    ai_N2                   INTEGER
)
RETURNS INTEGER
LANGUAGE SQL
DETERMINISTIC
BEGIN
    -- local variables:
    DECLARE l_Scaler        INT DEFAULT 0;
    DECLARE l_M1            INT;
    DECLARE l_M2            INT;
    DECLARE l_Result        INT DEFAULT 0;

    -- initialize local variables and return values:
    SET l_M1 = ai_N1;
    SET l_M2 = ai_N2;

-- body:
    loop_point:
    REPEAT
        SET l_Result = l_Result + MOD( (MOD(l_M1,2)+MOD(l_M2,2) ), 2 ) * power(2,l_Scaler);

        SET l_Scaler = l_Scaler + 1;
        SET l_M1 = l_M1/2;
        SET l_M2 = l_M2/2;

        UNTIL NOT ( l_Scaler < 31 )
    END REPEAT loop_point;

    RETURN l_Result;
END;
-- b_XOR


--------------------------------------------------------------------------------
--  
--------------------------------------------------------------------------------
-- delete existing function:
CALL IBSDEV1.p_dropFunc ('b_OR');

-- create the new function:
CREATE FUNCTION IBSDEV1.b_OR
(
    ai_N1                   INTEGER,
    ai_N2                   INTEGER
)
RETURNS INTEGER
LANGUAGE SQL
DETERMINISTIC
BEGIN
    -- local variables:
    DECLARE l_Scaler        INT DEFAULT 0;
    DECLARE l_M1            INT;
    DECLARE l_M2            INT;
    DECLARE l_Result        INT DEFAULT 0;

    -- initialize local variables and return values:
    SET l_M1 = ai_N1;
    SET l_M2 = ai_N2;

-- body:
    loop_point:
    REPEAT
        SET l_Result = l_Result + MOD(((MOD(l_M1,2)+MOD(l_M2,2))+(MOD(l_M1,2)*MOD(l_M2,2))),2) * power(2,l_Scaler);

        SET l_Scaler = l_Scaler + 1;
        SET l_M1 = l_M1/2;
        SET l_M2 = l_M2/2;

        UNTIL ( ( l_Scaler >= 31 ) OR ( ( l_M1 <= 0 ) AND ( l_M2 <= 0 ) ) )
    END REPEAT loop_point;

    RETURN l_Result;
END;
-- b_OR


--------------------------------------------------------------------------------
--  
--------------------------------------------------------------------------------
-- delete existing function:
CALL IBSDEV1.p_dropFunc ('createOid');

-- create the new function:
CREATE FUNCTION IBSDEV1.createOid
(
    ai_tVersionId           INT,
    ai_id                   INT
)
RETURNS CHAR (8) FOR BIT DATA
LANGUAGE SQL
DETERMINISTIC
BEGIN
    RETURN IBSDEV1.p_intToBinary (ai_tVersionId) || IBSDEV1.p_intToBinary (ai_id);
END;


--------------------------------------------------------------------------------
--  

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_getFullName');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_getFullName
(
    -- input parameters:
    IN  ai_name             VARCHAR (255),
    -- output parameters:
    OUT ao_fullName         VARCHAR (255)
)
LANGUAGE SQL 
READS SQL DATA
BEGIN 
-- body:
    -- set the full name:
    -- check if the name contains the name of the db user:
    IF (LOCATE ('.', ai_name) > 0)      -- name contains db user?
    THEN
        -- use name as is:
        SET ao_fullName = ai_name;
    -- if name contains db user?
    ELSE                                -- no db user in name
        -- add default db user to name:
        SET ao_fullName = 'dbo.' || ai_name;
    END IF; -- else no db user in name
END;
-- p_getFullName


--------------------------------------------------------------------------------
-- Compute the oid out of tVersionId and id. <BR>
-- The oid is computed as concatenation of the tVersionId and the object's id.
--
-- @input parameters:
-- @param   ai_tVersionId       The type version id.
-- @param   ai_id               The id of the object.
--
-- @output parameters:
-- @param   ao_oid              The computed oid.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_createOid');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_createOid
(
    -- input parameters:
    IN  ai_tVersionId       INT,            -- TVERSIONID
    IN  ai_id               INT,            -- ID
    -- output parameters:
    OUT ao_oid              CHAR (8) FOR BIT DATA -- OBJECTID
)
LANGUAGE SQL 
READS SQL DATA
BEGIN 
-- body:
    SET ao_oid = IBSDEV1.p_intToBinary (ai_tVersionId) || IBSDEV1.p_intToBinary (ai_id);
END;
-- p_createOid


--------------------------------------------------------------------------------
-- Converts a binary value into a hex string. <BR>
--
-- @input parameters:
-- @param   ai_bin              The binary value to be converted into a hex string.
--
-- @output parameters:
-- @param   ao_hs           The returned hex string value.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_binaryToHexString');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_binaryToHexString
(
    -- input parameters:
    IN  ai_bin              VARCHAR (254) FOR BIT DATA,
    -- output parameters:
    OUT ao_hs               VARCHAR (254)
)
LANGUAGE SQL
READS SQL DATA
BEGIN 
    -- local variables:
    DECLARE l_binCount      INT DEFAULT 1;
    DECLARE l_bin           VARCHAR (127) FOR BIT DATA;

    -- initialize local variables and return values:
    SET ao_hs = '';
    -- ensure that there are not more than 127 bytes for conversion:
    SET l_bin = CAST (ai_bin AS VARCHAR (127) FOR BIT DATA);
  
-- body:
    -- conversion construct:
    SET ao_hs = HEX (l_bin);
END;
-- p_binaryToHexString


--------------------------------------------------------------------------------
-- Converts an int value into a hex string. <BR>
--
-- @input parameters:
-- @param   ai_int              The int value to be converted into a hex string.
--
-- @output parameters:
-- @param   ao_hs               The returned hex string value.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_intToHexString');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_intToHexString
(
    -- input parameters:
    IN  ai_int              INT,
    -- output parameters:
    OUT ao_hs               VARCHAR (4)
)
LANGUAGE SQL 
READS SQL DATA
BEGIN 
-- body:
    -- conversion construct:
    SEt ao_hs = SUBSTR (HEX (ai_int), 5, 4);
END;
-- p_intToHexString


--------------------------------------------------------------------------------
-- performs bit-and on two given integers.
--
-- ATTENTION: highest value can be: 2147483647 (=0x7FFFFFFF)
-- 
-- The problem with our system is, that we theoretically higher values can 
-- be used - up to 4294967295 (=0xFFFFFFFF) - one bit more than 2147483647.
--
-- DO NOT USE VALUES HIGHER THAN 2147483647 WITH B_AND!!!
--
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_AND');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_AND
(
    -- input parameters:
    IN    ai_Value1         INTEGER,
    IN    ai_Value2         INTEGER,
    -- output parameters:
    OUT   ao_Result         INTEGER
)
LANGUAGE SQL 
READS SQL DATA
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_Value1        INTEGER;
    DECLARE l_Value2        INTEGER;
    DECLARE l_Position      INTEGER DEFAULT 1;

    -- initialize local variables and return values:
    SET l_Value1 = ai_Value1;
    SET l_Value2 = ai_Value2;
    SET ao_Result = 0;

-- body:
    WHILE (l_Value1 > 0)
    DO
        IF ((MOD (l_Value1, 2) = 1) AND (MOD (l_Value2, 2) = 1)) THEN
          SET ao_Result = ao_Result + 1 * l_Position;
        END IF;
        IF (MOD (l_Value1, 2) = 1) THEN
          SET l_Value1 = l_Value1 - 1;
        END IF;
        IF (MOD (l_Value2, 2) = 1) THEN
          SET l_Value2 = l_Value2 - 1;
        END IF;
        SET l_Value1 = l_Value1 / 2;
        SET l_Value2 = l_Value2 / 2;
        SET l_Position = l_Position * 2;
    END WHILE;
    
    RETURN ao_Result;
END;
-- p_AND


--------------------------------------------------------------------------------
--  
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_XOR');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XOR
(
    -- input parameters:
    IN    ai_Value1         INTEGER,
    IN    ai_Value2         INTEGER,
    -- output parameters:
    OUT   ao_Result         INTEGER
)
LANGUAGE SQL 
READS SQL DATA
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_Value1        INTEGER;
    DECLARE l_Value2        INTEGER;
    DECLARE l_Position      INTEGER DEFAULT 1;

    -- initialize local variables and return values:
    SET l_Value1 = ai_Value1;
    SET l_Value2 = ai_Value2;
    SET ao_Result = 0;

-- body:
    IF (ai_Value1 > ai_Value2)
    THEN
        SET l_Value1 = ai_Value2;
        SET l_Value2 = ai_Value1;
    ELSE
        SET l_Value1 = ai_Value1;
        SET l_Value2 = ai_Value2;
    END IF;

    WHILE (l_Value1 > 0)
    DO
        IF ((MOD (l_Value1, 2) = 1) AND (MOD (l_Value2, 2) = 0)) THEN
            SET ao_Result = ao_Result + 1 * l_Position;
        END IF;
        IF ((MOD (l_Value1, 2) = 0) AND (MOD (l_Value2, 2) = 1)) THEN
            SET ao_Result = ao_Result + 1 * l_Position;
        END IF;
        IF (MOD (l_Value1, 2) = 1) then
            SET l_Value1 = l_Value1 - 1;
        END IF;
        IF (MOD (l_Value2, 2) = 1) then
            SET l_Value2 = l_Value2 - 1;
        END IF;
        SET l_Value1 = l_Value1 / 2;
        SET l_Value2 = l_Value2 / 2;
        SET l_Position = l_Position * 2;
    END WHILE;

    RETURN ao_Result;
END;
-- p_XOR


--------------------------------------------------------------------------------
--  
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_OR');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_OR
(
    -- input parameters:
    IN    ai_Value1         INTEGER,
    IN    ai_Value2         INTEGER,
    -- output parameters:
    OUT   ao_Result         INTEGER
)
LANGUAGE SQL 
READS SQL DATA
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_Value1        INTEGER;
    DECLARE l_Value2        INTEGER;
    DECLARE l_Position      INTEGER DEFAULT 1;

    -- initialize local variables and return values:
    SET ao_Result = 0;

-- body:
    IF (ai_Value1 > ai_Value2)
    THEN
        SET l_Value1 = ai_Value2;
        SET l_Value2 = ai_Value1;
    ELSE
        SET l_Value1 = ai_Value1;
        SET l_Value2 = ai_Value2;
    END IF;
  
    WHILE (l_Value1 > 0)
    DO
        IF ((MOD (l_Value1, 2) = 1) OR (MOD (l_Value2, 2) = 1)) THEN
            SET ao_Result = ao_Result + 1 * l_Position;
        END IF;
        IF (MOD (l_Value1, 2) = 1) THEN
            SET l_Value1 = l_Value1 - 1;
        END IF;
        IF (MOD (l_Value2, 2) = 1) THEN
            SET l_Value2 = l_Value2 - 1;
        END IF;
        SET l_Value1 = l_Value1 / 2;
        SET l_Value2 = l_Value2 / 2;
        SET l_Position = l_Position * 2;
    END WHILE;

    RETURN ao_Result;
END;
-- p_OR


--------------------------------------------------------------------------------
-- Converts a hex string into the corresponding integer value. <BR>
--
-- @input parameters:
-- @param   ai_hs               The hex string to be converted into integer.
--
-- @output parameters:
-- @param   ao_int              The returned integer value.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropFunc ('p_hexStringToInt');

-- create the new procedure:
CREATE FUNCTION IBSDEV1.p_hexStringToInt
(
    -- input parameters:
    ai_hs               VARCHAR (10)
--    -- output parameters:
--    OUT ao_int          INTEGER
)
RETURNS INTEGER
LANGUAGE SQL
SPECIFIC IBSDEV1.p_hexStringToInt
DETERMINISTIC
BEGIN 
    -- local variables:
    DECLARE l_hs VARCHAR (10);              -- local version of the hex string
    DECLARE l_digit CHAR (1);               -- the current digit
    DECLARE pos INTEGER;                    -- the current position
    DECLARE l_mult INTEGER;                 -- the multiplier for the digit
    DECLARE l_digitVal INTEGER;             -- the integer value of the digit
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE l_int INTEGER;                  -- the return value
    DECLARE SQLCODE INT;

-- body:
    -- ensure that the string has only upper case characters:
    SET l_hs = UPPER (ai_hs);

    -- drop leading hex signs:
    IF (SUBSTR (l_hs, 1, 2) = '0x')
    THEN
        SET l_hs = SUBSTR (l_hs, 3);
    END IF;

    -- ensure correct length:
    IF (CHARACTER_LENGTH (l_hs) > 8)
    THEN
        SET l_hs = SUBSTR (l_hs, 1, 8);
    END IF;

    -- initializations:
    SET l_int = 0;
    SET pos = CHARACTER_LENGTH (ai_hs);
    SET l_mult = 1;

    -- loop through all characters of the input string:
    WHILE (pos > 0)                     -- another character exists?
    DO
        -- get the actual digit:
        SET l_digit = SUBSTRING (l_hs, pos, 1);

        -- convert the digit:
        CASE l_digit
            WHEN '0' THEN
                SET l_digitVal = 0;
            WHEN '1' THEN
                SET l_digitVal = 1;
            WHEN '2' THEN
                SET l_digitVal = 2;
            WHEN '3' THEN
                SET l_digitVal = 3;
            WHEN '4' THEN
                SET l_digitVal = 4;
            WHEN '5' THEN
                SET l_digitVal = 5;
            WHEN '6' THEN
                SET l_digitVal = 6;
            WHEN '7' THEN
                SET l_digitVal = 7;
            WHEN '8' THEN
                SET l_digitVal = 8;
            WHEN '9' THEN
                SET l_digitVal = 9;
            WHEN 'A' THEN
                SET l_digitVal = 10;
            WHEN 'B' THEN
                SET l_digitVal = 11;
            WHEN 'C' THEN
                SET l_digitVal = 12;
            WHEN 'D' THEN
                SET l_digitVal = 13;
            WHEN 'E' THEN
                SET l_digitVal = 14;
            WHEN 'F' THEN
                SET l_digitVal = 15;
        END CASE;

        -- add the digit to the number:
        SET l_int = l_int + l_digitVal * l_mult;

        -- set the next position and multiplier:
        SET pos = pos - 1;
        IF (pos > 0)
        THEN
            SET l_mult = l_mult * 16;
        END IF;
    END WHILE; -- another character exists

    -- return the computed value:
    RETURN l_int;
END;
-- p_hexStringToInt


--------------------------------------------------------------------------------
-- Converts an integer value into the corresponding binary value. <BR>
--
-- @input parameters:
-- @param   ai_int              The integer value to be converted to binary.
--
-- @output parameters:
-- @return  The computed binary value.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropFunc ('p_intToBinary');

-- create the new procedure:
CREATE FUNCTION IBSDEV1.p_intToBinary
(
    -- input parameters:
    ai_int              INTEGER
)
RETURNS CHAR (4) FOR BIT DATA
LANGUAGE SQL
SPECIFIC IBSDEV1.p_intToBinary
DETERMINISTIC
BEGIN
    -- local variables:
    DECLARE l_retVal VARCHAR (4) FOR BIT DATA DEFAULT ''; -- the return value
    DECLARE l_int INTEGER;                  -- local version of the integer val.
    DECLARE l_pos INTEGER DEFAULT 4;        -- the current position
                                            -- start with the upper most byte
    DECLARE l_mult INTEGER DEFAULT 16777216; -- the multiplier for the digit
                                            -- 0x01000000, base of first byte
    DECLARE l_digitVal INTEGER DEFAULT 0;   -- the integer value of the digit

    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE SQLCODE INT;

    -- initializations:
    SET l_int = ai_int;

-- body:
    -- loop through all characters of the input string:
    WHILE (l_pos > 0)                   -- another byte exists?
    DO
        -- get the actual digit:
        SET l_digitVal = l_int / l_mult;

        -- convert the digit:
        CASE l_digitVal
            WHEN 000 THEN
                SET l_retVal = l_retVal || X'00';
            WHEN 001 THEN
                SET l_retVal = l_retVal || X'01';
            WHEN 002 THEN
                SET l_retVal = l_retVal || X'02';
            WHEN 003 THEN
                SET l_retVal = l_retVal || X'03';
            WHEN 004 THEN
                SET l_retVal = l_retVal || X'04';
            WHEN 005 THEN
                SET l_retVal = l_retVal || X'05';
            WHEN 006 THEN
                SET l_retVal = l_retVal || X'06';
            WHEN 007 THEN
                SET l_retVal = l_retVal || X'07';
            WHEN 008 THEN
                SET l_retVal = l_retVal || X'08';
            WHEN 009 THEN
                SET l_retVal = l_retVal || X'09';
            WHEN 010 THEN
                SET l_retVal = l_retVal || X'0A';
            WHEN 011 THEN
                SET l_retVal = l_retVal || X'0B';
            WHEN 012 THEN
                SET l_retVal = l_retVal || X'0C';
            WHEN 013 THEN
                SET l_retVal = l_retVal || X'0D';
            WHEN 014 THEN
                SET l_retVal = l_retVal || X'0E';
            WHEN 015 THEN
                SET l_retVal = l_retVal || X'0F';
            WHEN 016 THEN
                SET l_retVal = l_retVal || X'10';
            WHEN 017 THEN
                SET l_retVal = l_retVal || X'11';
            WHEN 018 THEN
                SET l_retVal = l_retVal || X'12';
            WHEN 019 THEN
                SET l_retVal = l_retVal || X'13';
            WHEN 020 THEN
                SET l_retVal = l_retVal || X'14';
            WHEN 021 THEN
                SET l_retVal = l_retVal || X'15';
            WHEN 022 THEN
                SET l_retVal = l_retVal || X'16';
            WHEN 023 THEN
                SET l_retVal = l_retVal || X'17';
            WHEN 024 THEN
                SET l_retVal = l_retVal || X'18';
            WHEN 025 THEN
                SET l_retVal = l_retVal || X'19';
            WHEN 026 THEN
                SET l_retVal = l_retVal || X'1A';
            WHEN 027 THEN
                SET l_retVal = l_retVal || X'1B';
            WHEN 028 THEN
                SET l_retVal = l_retVal || X'1C';
            WHEN 029 THEN
                SET l_retVal = l_retVal || X'1D';
            WHEN 030 THEN
                SET l_retVal = l_retVal || X'1E';
            WHEN 031 THEN
                SET l_retVal = l_retVal || X'1F';
            WHEN 032 THEN
                SET l_retVal = l_retVal || X'20';
            WHEN 033 THEN
                SET l_retVal = l_retVal || X'21';
            WHEN 034 THEN
                SET l_retVal = l_retVal || X'22';
            WHEN 035 THEN
                SET l_retVal = l_retVal || X'23';
            WHEN 036 THEN
                SET l_retVal = l_retVal || X'24';
            WHEN 037 THEN
                SET l_retVal = l_retVal || X'25';
            WHEN 038 THEN
                SET l_retVal = l_retVal || X'26';
            WHEN 039 THEN
                SET l_retVal = l_retVal || X'27';
            WHEN 040 THEN
                SET l_retVal = l_retVal || X'28';
            WHEN 041 THEN
                SET l_retVal = l_retVal || X'29';
            WHEN 042 THEN
                SET l_retVal = l_retVal || X'2A';
            WHEN 043 THEN
                SET l_retVal = l_retVal || X'2B';
            WHEN 044 THEN
                SET l_retVal = l_retVal || X'2C';
            WHEN 045 THEN
                SET l_retVal = l_retVal || X'2D';
            WHEN 046 THEN
                SET l_retVal = l_retVal || X'2E';
            WHEN 047 THEN
                SET l_retVal = l_retVal || X'2F';
            WHEN 048 THEN
                SET l_retVal = l_retVal || X'30';
            WHEN 049 THEN
                SET l_retVal = l_retVal || X'31';
            WHEN 050 THEN
                SET l_retVal = l_retVal || X'32';
            WHEN 051 THEN
                SET l_retVal = l_retVal || X'33';
            WHEN 052 THEN
                SET l_retVal = l_retVal || X'34';
            WHEN 053 THEN
                SET l_retVal = l_retVal || X'35';
            WHEN 054 THEN
                SET l_retVal = l_retVal || X'36';
            WHEN 055 THEN
                SET l_retVal = l_retVal || X'37';
            WHEN 056 THEN
                SET l_retVal = l_retVal || X'38';
            WHEN 057 THEN
                SET l_retVal = l_retVal || X'39';
            WHEN 058 THEN
                SET l_retVal = l_retVal || X'3A';
            WHEN 059 THEN
                SET l_retVal = l_retVal || X'3B';
            WHEN 060 THEN
                SET l_retVal = l_retVal || X'3C';
            WHEN 061 THEN
                SET l_retVal = l_retVal || X'3D';
            WHEN 062 THEN
                SET l_retVal = l_retVal || X'3E';
            WHEN 063 THEN
                SET l_retVal = l_retVal || X'3F';
            WHEN 064 THEN
                SET l_retVal = l_retVal || X'40';
            WHEN 065 THEN
                SET l_retVal = l_retVal || X'41';
            WHEN 066 THEN
                SET l_retVal = l_retVal || X'42';
            WHEN 067 THEN
                SET l_retVal = l_retVal || X'43';
            WHEN 068 THEN
                SET l_retVal = l_retVal || X'44';
            WHEN 069 THEN
                SET l_retVal = l_retVal || X'45';
            WHEN 070 THEN
                SET l_retVal = l_retVal || X'46';
            WHEN 071 THEN
                SET l_retVal = l_retVal || X'47';
            WHEN 072 THEN
                SET l_retVal = l_retVal || X'48';
            WHEN 073 THEN
                SET l_retVal = l_retVal || X'49';
            WHEN 074 THEN
                SET l_retVal = l_retVal || X'4A';
            WHEN 075 THEN
                SET l_retVal = l_retVal || X'4B';
            WHEN 076 THEN
                SET l_retVal = l_retVal || X'4C';
            WHEN 077 THEN
                SET l_retVal = l_retVal || X'4D';
            WHEN 078 THEN
                SET l_retVal = l_retVal || X'4E';
            WHEN 079 THEN
                SET l_retVal = l_retVal || X'4F';
            WHEN 080 THEN
                SET l_retVal = l_retVal || X'50';
            WHEN 081 THEN
                SET l_retVal = l_retVal || X'51';
            WHEN 082 THEN
                SET l_retVal = l_retVal || X'52';
            WHEN 083 THEN
                SET l_retVal = l_retVal || X'53';
            WHEN 084 THEN
                SET l_retVal = l_retVal || X'54';
            WHEN 085 THEN
                SET l_retVal = l_retVal || X'55';
            WHEN 086 THEN
                SET l_retVal = l_retVal || X'56';
            WHEN 087 THEN
                SET l_retVal = l_retVal || X'57';
            WHEN 088 THEN
                SET l_retVal = l_retVal || X'58';
            WHEN 089 THEN
                SET l_retVal = l_retVal || X'59';
            WHEN 090 THEN
                SET l_retVal = l_retVal || X'5A';
            WHEN 091 THEN
                SET l_retVal = l_retVal || X'5B';
            WHEN 092 THEN
                SET l_retVal = l_retVal || X'5C';
            WHEN 093 THEN
                SET l_retVal = l_retVal || X'5D';
            WHEN 094 THEN
                SET l_retVal = l_retVal || X'5E';
            WHEN 095 THEN
                SET l_retVal = l_retVal || X'5F';
            WHEN 096 THEN
                SET l_retVal = l_retVal || X'60';
            WHEN 097 THEN
                SET l_retVal = l_retVal || X'61';
            WHEN 098 THEN
                SET l_retVal = l_retVal || X'62';
            WHEN 099 THEN
                SET l_retVal = l_retVal || X'63';
            WHEN 100 THEN
                SET l_retVal = l_retVal || X'64';
            WHEN 101 THEN
                SET l_retVal = l_retVal || X'65';
            WHEN 102 THEN
                SET l_retVal = l_retVal || X'66';
            WHEN 103 THEN
                SET l_retVal = l_retVal || X'67';
            WHEN 104 THEN
                SET l_retVal = l_retVal || X'68';
            WHEN 105 THEN
                SET l_retVal = l_retVal || X'69';
            WHEN 106 THEN
                SET l_retVal = l_retVal || X'6A';
            WHEN 107 THEN
                SET l_retVal = l_retVal || X'6B';
            WHEN 108 THEN
                SET l_retVal = l_retVal || X'6C';
            WHEN 109 THEN
                SET l_retVal = l_retVal || X'6D';
            WHEN 110 THEN
                SET l_retVal = l_retVal || X'6E';
            WHEN 111 THEN
                SET l_retVal = l_retVal || X'6F';
            WHEN 112 THEN
                SET l_retVal = l_retVal || X'70';
            WHEN 113 THEN
                SET l_retVal = l_retVal || X'71';
            WHEN 114 THEN
                SET l_retVal = l_retVal || X'72';
            WHEN 115 THEN
                SET l_retVal = l_retVal || X'73';
            WHEN 116 THEN
                SET l_retVal = l_retVal || X'74';
            WHEN 117 THEN
                SET l_retVal = l_retVal || X'75';
            WHEN 118 THEN
                SET l_retVal = l_retVal || X'76';
            WHEN 119 THEN
                SET l_retVal = l_retVal || X'77';
            WHEN 120 THEN
                SET l_retVal = l_retVal || X'78';
            WHEN 121 THEN
                SET l_retVal = l_retVal || X'79';
            WHEN 122 THEN
                SET l_retVal = l_retVal || X'7A';
            WHEN 123 THEN
                SET l_retVal = l_retVal || X'7B';
            WHEN 124 THEN
                SET l_retVal = l_retVal || X'7C';
            WHEN 125 THEN
                SET l_retVal = l_retVal || X'7D';
            WHEN 126 THEN
                SET l_retVal = l_retVal || X'7E';
            WHEN 127 THEN
                SET l_retVal = l_retVal || X'7F';
            WHEN 128 THEN
                SET l_retVal = l_retVal || X'80';
            WHEN 129 THEN
                SET l_retVal = l_retVal || X'81';
            WHEN 130 THEN
                SET l_retVal = l_retVal || X'82';
            WHEN 131 THEN
                SET l_retVal = l_retVal || X'83';
            WHEN 132 THEN
                SET l_retVal = l_retVal || X'84';
            WHEN 133 THEN
                SET l_retVal = l_retVal || X'85';
            WHEN 134 THEN
                SET l_retVal = l_retVal || X'86';
            WHEN 135 THEN
                SET l_retVal = l_retVal || X'87';
            WHEN 136 THEN
                SET l_retVal = l_retVal || X'88';
            WHEN 137 THEN
                SET l_retVal = l_retVal || X'89';
            WHEN 138 THEN
                SET l_retVal = l_retVal || X'8A';
            WHEN 139 THEN
                SET l_retVal = l_retVal || X'8B';
            WHEN 140 THEN
                SET l_retVal = l_retVal || X'8C';
            WHEN 141 THEN
                SET l_retVal = l_retVal || X'8D';
            WHEN 142 THEN
                SET l_retVal = l_retVal || X'8E';
            WHEN 143 THEN
                SET l_retVal = l_retVal || X'8F';
            WHEN 144 THEN
                SET l_retVal = l_retVal || X'90';
            WHEN 145 THEN
                SET l_retVal = l_retVal || X'91';
            WHEN 146 THEN
                SET l_retVal = l_retVal || X'92';
            WHEN 147 THEN
                SET l_retVal = l_retVal || X'93';
            WHEN 148 THEN
                SET l_retVal = l_retVal || X'94';
            WHEN 149 THEN
                SET l_retVal = l_retVal || X'95';
            WHEN 150 THEN
                SET l_retVal = l_retVal || X'96';
            WHEN 151 THEN
                SET l_retVal = l_retVal || X'97';
            WHEN 152 THEN
                SET l_retVal = l_retVal || X'98';
            WHEN 153 THEN
                SET l_retVal = l_retVal || X'99';
            WHEN 154 THEN
                SET l_retVal = l_retVal || X'9A';
            WHEN 155 THEN
                SET l_retVal = l_retVal || X'9B';
            WHEN 156 THEN
                SET l_retVal = l_retVal || X'9C';
            WHEN 157 THEN
                SET l_retVal = l_retVal || X'9D';
            WHEN 158 THEN
                SET l_retVal = l_retVal || X'9E';
            WHEN 159 THEN
                SET l_retVal = l_retVal || X'9F';
            WHEN 160 THEN
                SET l_retVal = l_retVal || X'A0';
            WHEN 161 THEN
                SET l_retVal = l_retVal || X'A1';
            WHEN 162 THEN
                SET l_retVal = l_retVal || X'A2';
            WHEN 163 THEN
                SET l_retVal = l_retVal || X'A3';
            WHEN 164 THEN
                SET l_retVal = l_retVal || X'A4';
            WHEN 165 THEN
                SET l_retVal = l_retVal || X'A5';
            WHEN 166 THEN
                SET l_retVal = l_retVal || X'A6';
            WHEN 167 THEN
                SET l_retVal = l_retVal || X'A7';
            WHEN 168 THEN
                SET l_retVal = l_retVal || X'A8';
            WHEN 169 THEN
                SET l_retVal = l_retVal || X'A9';
            WHEN 170 THEN
                SET l_retVal = l_retVal || X'AA';
            WHEN 171 THEN
                SET l_retVal = l_retVal || X'AB';
            WHEN 172 THEN
                SET l_retVal = l_retVal || X'AC';
            WHEN 173 THEN
                SET l_retVal = l_retVal || X'AD';
            WHEN 174 THEN
                SET l_retVal = l_retVal || X'AE';
            WHEN 175 THEN
                SET l_retVal = l_retVal || X'AF';
            WHEN 176 THEN
                SET l_retVal = l_retVal || X'B0';
            WHEN 177 THEN
                SET l_retVal = l_retVal || X'B1';
            WHEN 178 THEN
                SET l_retVal = l_retVal || X'B2';
            WHEN 179 THEN
                SET l_retVal = l_retVal || X'B3';
            WHEN 180 THEN
                SET l_retVal = l_retVal || X'B4';
            WHEN 181 THEN
                SET l_retVal = l_retVal || X'B5';
            WHEN 182 THEN
                SET l_retVal = l_retVal || X'B6';
            WHEN 183 THEN
                SET l_retVal = l_retVal || X'B7';
            WHEN 184 THEN
                SET l_retVal = l_retVal || X'B8';
            WHEN 185 THEN
                SET l_retVal = l_retVal || X'B9';
            WHEN 186 THEN
                SET l_retVal = l_retVal || X'BA';
            WHEN 187 THEN
                SET l_retVal = l_retVal || X'BB';
            WHEN 188 THEN
                SET l_retVal = l_retVal || X'BC';
            WHEN 189 THEN
                SET l_retVal = l_retVal || X'BD';
            WHEN 190 THEN
                SET l_retVal = l_retVal || X'BE';
            WHEN 191 THEN
                SET l_retVal = l_retVal || X'BF';
            WHEN 192 THEN
                SET l_retVal = l_retVal || X'C0';
            WHEN 193 THEN
                SET l_retVal = l_retVal || X'C1';
            WHEN 194 THEN
                SET l_retVal = l_retVal || X'C2';
            WHEN 195 THEN
                SET l_retVal = l_retVal || X'C3';
            WHEN 196 THEN
                SET l_retVal = l_retVal || X'C4';
            WHEN 197 THEN
                SET l_retVal = l_retVal || X'C5';
            WHEN 198 THEN
                SET l_retVal = l_retVal || X'C6';
            WHEN 199 THEN
                SET l_retVal = l_retVal || X'C7';
            WHEN 200 THEN
                SET l_retVal = l_retVal || X'C8';
            WHEN 201 THEN
                SET l_retVal = l_retVal || X'C9';
            WHEN 202 THEN
                SET l_retVal = l_retVal || X'CA';
            WHEN 203 THEN
                SET l_retVal = l_retVal || X'CB';
            WHEN 204 THEN
                SET l_retVal = l_retVal || X'CC';
            WHEN 205 THEN
                SET l_retVal = l_retVal || X'CD';
            WHEN 206 THEN
                SET l_retVal = l_retVal || X'CE';
            WHEN 207 THEN
                SET l_retVal = l_retVal || X'CF';
            WHEN 208 THEN
                SET l_retVal = l_retVal || X'D0';
            WHEN 209 THEN
                SET l_retVal = l_retVal || X'D1';
            WHEN 210 THEN
                SET l_retVal = l_retVal || X'D2';
            WHEN 211 THEN
                SET l_retVal = l_retVal || X'D3';
            WHEN 212 THEN
                SET l_retVal = l_retVal || X'D4';
            WHEN 213 THEN
                SET l_retVal = l_retVal || X'D5';
            WHEN 214 THEN
                SET l_retVal = l_retVal || X'D6';
            WHEN 215 THEN
                SET l_retVal = l_retVal || X'D7';
            WHEN 216 THEN
                SET l_retVal = l_retVal || X'D8';
            WHEN 217 THEN
                SET l_retVal = l_retVal || X'D9';
            WHEN 218 THEN
                SET l_retVal = l_retVal || X'DA';
            WHEN 219 THEN
                SET l_retVal = l_retVal || X'DB';
            WHEN 220 THEN
                SET l_retVal = l_retVal || X'DC';
            WHEN 221 THEN
                SET l_retVal = l_retVal || X'DD';
            WHEN 222 THEN
                SET l_retVal = l_retVal || X'DE';
            WHEN 223 THEN
                SET l_retVal = l_retVal || X'DF';
            WHEN 224 THEN
                SET l_retVal = l_retVal || X'E0';
            WHEN 225 THEN
                SET l_retVal = l_retVal || X'E1';
            WHEN 226 THEN
                SET l_retVal = l_retVal || X'E2';
            WHEN 227 THEN
                SET l_retVal = l_retVal || X'E3';
            WHEN 228 THEN
                SET l_retVal = l_retVal || X'E4';
            WHEN 229 THEN
                SET l_retVal = l_retVal || X'E5';
            WHEN 230 THEN
                SET l_retVal = l_retVal || X'E6';
            WHEN 231 THEN
                SET l_retVal = l_retVal || X'E7';
            WHEN 232 THEN
                SET l_retVal = l_retVal || X'E8';
            WHEN 233 THEN
                SET l_retVal = l_retVal || X'E9';
            WHEN 234 THEN
                SET l_retVal = l_retVal || X'EA';
            WHEN 235 THEN
                SET l_retVal = l_retVal || X'EB';
            WHEN 236 THEN
                SET l_retVal = l_retVal || X'EC';
            WHEN 237 THEN
                SET l_retVal = l_retVal || X'ED';
            WHEN 238 THEN
                SET l_retVal = l_retVal || X'EE';
            WHEN 239 THEN
                SET l_retVal = l_retVal || X'EF';
            WHEN 240 THEN
                SET l_retVal = l_retVal || X'F0';
            WHEN 241 THEN
                SET l_retVal = l_retVal || X'F1';
            WHEN 242 THEN
                SET l_retVal = l_retVal || X'F2';
            WHEN 243 THEN
                SET l_retVal = l_retVal || X'F3';
            WHEN 244 THEN
                SET l_retVal = l_retVal || X'F4';
            WHEN 245 THEN
                SET l_retVal = l_retVal || X'F5';
            WHEN 246 THEN
                SET l_retVal = l_retVal || X'F6';
            WHEN 247 THEN
                SET l_retVal = l_retVal || X'F7';
            WHEN 248 THEN
                SET l_retVal = l_retVal || X'F8';
            WHEN 249 THEN
                SET l_retVal = l_retVal || X'F9';
            WHEN 250 THEN
                SET l_retVal = l_retVal || X'FA';
            WHEN 251 THEN
                SET l_retVal = l_retVal || X'FB';
            WHEN 252 THEN
                SET l_retVal = l_retVal || X'FC';
            WHEN 253 THEN
                SET l_retVal = l_retVal || X'FD';
            WHEN 254 THEN
                SET l_retVal = l_retVal || X'FE';
            WHEN 255 THEN
                SET l_retVal = l_retVal || X'FF';
        END CASE;

        -- set the next position and multiplier:
        SET l_pos = l_pos - 1;
        SET l_int = l_int - l_digitVal * l_mult;
        SET l_mult = l_mult / 256;
    END WHILE; -- another character exists

    -- return the computed value:
    RETURN l_retVal;
END;
-- p_intToBinary
