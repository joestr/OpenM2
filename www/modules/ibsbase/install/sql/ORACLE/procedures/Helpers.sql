/******************************************************************************
 * All stored procedures regarding basic database functions. <BR>
 *
 * @version     2.21.0015, 26.06.2002 KR
 *
 * @author      Mario Stegbauer (MS)  990805
 ******************************************************************************
 */
set define off;


/******************************************************************************
 *  
 */
CREATE OR REPLACE PROCEDURE debug (ai_text IN VARCHAR2)
AS
BEGIN
    -- set buffer size for output:
    DBMS_OUTPUT.ENABLE (100000);

    DBMS_OUTPUT.PUT_LINE (ai_text);
END;
/
show errors;


/******************************************************************************
 * Drop table including all constraints. <BR>
 *
 * @input parameters:
 * @param   @ai_procName        name of table
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_dropTable
(
    ai_tableName            VARCHAR2
)
AS
    -- local variables:
    l_count                 INTEGER;    -- FUNCTION|PROCEDURE
    l_cursor                INTEGER;    -- cursor
    l_ret                   INTEGER;    -- cursors return value
BEGIN
    -- count number of tables
    SELECT  count(*)
    INTO    l_count
    FROM    obj
    WHERE   lower (object_name) = lower (ai_tableName)
      AND   lower (object_type) = lower ('table');

    -- does table exist
    IF (l_count > 0) THEN
        -- table exists
    
        -- drop table; including all constraints
        -- open the cursor:
	    l_cursor := DBMS_SQL.OPEN_CURSOR;
	    DBMS_SQL.PARSE(l_cursor,
	         'DROP TABLE ' || ai_tableName || ' CASCADE CONSTRAINTS',
	         DBMS_SQL.V7);
	    l_ret := DBMS_SQL.EXECUTE(l_cursor);
        -- close the cursor:
	    DBMS_SQL.CLOSE_CURSOR(l_cursor);
	
	    -- commit changes
	    commit work;
    ELSE
        -- table does not exist
        -- show message
        debug ('Table ' || ai_tableName || ' does not exist.');
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        debug ('Error while dropping table ' || 
                ai_tableName ||
               '; sqlcode: ' || SQLCODE ||
               ', sqlerrm: ' || SQLERRM );
        RAISE;
END p_dropTable;
/
show errors;


/******************************************************************************
 * Drop procedure or function. Type 'FUNCTION' or 'PROCEDURE' will be
 * asserted by this procedure.<BR>
 *
 * @input parameters:
 * @param   @ai_procName        name of procedure or function
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_dropProc
(
    ai_procName            VARCHAR2
)
AS
    -- local variables:
    l_type                  VARCHAR2(255);  -- FUNCTION|PROCEDURE
    l_cursor                INTEGER;        -- cursor
    l_ret                   INTEGER;        -- cursors return value
BEGIN
-- body:
    -- get type of given object
    BEGIN
        SELECT  object_type
        INTO    l_type
        FROM    obj
        WHERE   LOWER(object_name) = LOWER(ai_procName);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            debug (l_type || ' ' || ai_procName || ' does not exist.');
            RETURN;
    END;
    
    -- drop function/procedure; create and execute
    --  dynamic sql command
    -- open the cursor:
	l_cursor := DBMS_SQL.OPEN_CURSOR;
	DBMS_SQL.PARSE(l_cursor,
	     'DROP ' || l_type || ' ' || ai_procName,
	     DBMS_SQL.V7);
	l_ret := DBMS_SQL.EXECUTE(l_cursor);
    -- close the cursor:
	DBMS_SQL.CLOSE_CURSOR(l_cursor);
	
	-- commit changes
	commit work;
EXCEPTION
    WHEN OTHERS THEN
        debug ('Error while dropping ' || l_type || ' ' ||
                ai_procName ||
               '; sqlcode: ' || SQLCODE ||
               ', sqlerrm: ' || SQLERRM );
        RAISE;
END p_dropProc;
/
show errors;



/******************************************************************************
 * Drop view. <BR>
 *
 * @input parameters:
 * @param   @ai_viewName        name of view
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_dropView
(
    ai_viewName             VARCHAR2
)
AS
    -- local variables:
    l_type                  VARCHAR2(255);  -- VIEW
    l_cursor                INTEGER;        -- cursor
    l_ret                   INTEGER;        -- cursors return value
BEGIN
-- body:
    -- get type of given object
    BEGIN
        SELECT  object_type
        INTO    l_type
        FROM    obj
        WHERE   LOWER(object_name) = LOWER(ai_viewName);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            debug ('View ' || ai_viewName || ' does not exist.');
            RETURN;
    END;
    
    -- drop function/procedure; create and execute
    -- dynamic sql command
    -- open the cursor:
	l_cursor := DBMS_SQL.OPEN_CURSOR;
	DBMS_SQL.PARSE(l_cursor,
	     'DROP ' || l_type || ' ' || ai_viewName,
	     DBMS_SQL.V7);
	l_ret := DBMS_SQL.EXECUTE(l_cursor);
    -- close the cursor:
	DBMS_SQL.CLOSE_CURSOR(l_cursor);
	
	-- commit changes
	commit work;
EXCEPTION
    WHEN OTHERS THEN
        debug ('Error while dropping view ' ||
                ai_viewName ||
               '; sqlcode: ' || SQLCODE ||
               ', sqlerrm: ' || SQLERRM );
        RAISE;
END p_dropView;
/
show errors;


/******************************************************************************
 * Drop trigger. <BR>
 *
 * @input parameters:
 * @param   @ai_viewName        name of view
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_dropTrig
(
    ai_triggerName             VARCHAR2
)
AS
    -- local variables:
    l_type                  VARCHAR2(255);  -- VIEW
    l_cursor                INTEGER;        -- cursor
    l_ret                   INTEGER;        -- cursors return value
BEGIN
-- body:
    -- get type of given object
    BEGIN
        SELECT  object_type
        INTO    l_type
        FROM    obj
        WHERE   LOWER(object_name) = LOWER(ai_triggerName);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            debug ('Trigger ' || ai_triggerName || ' does not exist.');
            RETURN;
    END;
    
    -- drop trigger; create and execute dynamic sql command
    -- open the cursor:
	l_cursor := DBMS_SQL.OPEN_CURSOR;
	DBMS_SQL.PARSE(l_cursor,
	     'DROP TRIGGER ' || ai_triggerName,
	     DBMS_SQL.V7);
	l_ret := DBMS_SQL.EXECUTE(l_cursor);
    -- close the cursor:
	DBMS_SQL.CLOSE_CURSOR(l_cursor);
	
	-- commit changes
	commit work;
EXCEPTION
    WHEN OTHERS THEN
        debug ('Error while dropping trigger ' ||
                ai_triggerName ||
               '; sqlcode: ' || SQLCODE ||
               ', sqlerrm: ' || SQLERRM );
        RAISE;
END p_dropTrig;
/
show errors;



/******************************************************************************
 *  
 */
create or replace function getBit ( i_integer IN INTEGER, i_bitvalue IN INTEGER )
return INTEGER as
begin
   return mod ( trunc(i_integer / i_bitvalue), 2 ) * i_bitvalue;
end;
/
show errors;
/
commit work;
/


/******************************************************************************
 * performs bit-and on two given integers.
 *
 * ATTENTION: highest value can be: 2147483647 (=0x7FFFFFFF)
 * 
 * The problem with our system is, that we theoretically higher values can 
 * be used - up to 4294967295 (=0xFFFFFFFF) - one bit more than 2147483647.
 *
 * DO NOT USE VALUES HIGHER THAN 2147483647 WITH B_AND!!!
 *
 */
create or replace function b_and ( value1 IN INTEGER, value2 IN INTEGER )
return INTEGER as
begin
    RETURN BITAND(value1, value2);
/*
    --
    -- OLD Version of BITAND functionality
    --
    
    int1 INTEGER;
    int2 INTEGER;
    result INTEGER;
    position INTEGER;
    begin
      int1 := value1;
      int2 := value2;
      result := 0;
      position := 1;
      while int1 > 0
      loop
        if ((mod(int1, 2) = 1) and (mod(int2, 2) = 1)) then
          result := result + 1 * position;
        end if;
        if (mod(int1, 2) = 1) then
          int1 := int1 - 1;
        end if;
        if (mod(int2, 2) = 1) then
          int2 := int2 - 1;
        end if;
        int1 := int1 / 2;
        int2 := int2 / 2;
        position := position * 2;
      end loop;
      return result;
    exception
      when OTHERS then
        return 0;
    end;
*/
end;
/
show errors;


/******************************************************************************
 *  
 */
create or replace function b_xor ( value1 IN INTEGER, value2 IN INTEGER )
return INTEGER as
int1 INTEGER;
int2 INTEGER;
result INTEGER;
position INTEGER;
begin
    IF (value2 > value1)
    THEN
        int1 := value2;
        int2 := value1;
    ELSE
        int1 := value1;
        int2 := value2;
    END IF;

  result := 0;
  position := 1;
  while int1 > 0
  loop
    if ((mod(int1, 2) = 1) and (mod(int2, 2) = 0)) then
      result := result + 1 * position;
    end if;
    if ((mod(int1, 2) = 0) and (mod(int2, 2) = 1)) then
      result := result + 1 * position;
    end if;
    if (mod(int1, 2) = 1) then
      int1 := int1 - 1;
    end if;
    if (mod(int2, 2) = 1) then
      int2 := int2 - 1;
    end if;
    int1 := int1 / 2;
    int2 := int2 / 2;
    position := position * 2;
  end loop;
  return result;
exception
  when OTHERS then
    return 0;
end;
/
show errors;


/******************************************************************************
 *  
 */
create or replace function b_or ( value1 IN INTEGER, value2 IN INTEGER )
return INTEGER as
int1 INTEGER;
int2 INTEGER;
result INTEGER;
position INTEGER;
begin
    IF (value2 > value1)
    THEN
        int1 := value2;
        int2 := value1;
    ELSE
        int1 := value1;
        int2 := value2;
    END IF;
  
  result := 0;
  position := 1;
  while int1 > 0
  loop
    if ((mod(int1, 2) = 1) or (mod(int2, 2) = 1)) then
      result := result + 1 * position;
    end if;
    if (mod(int1, 2) = 1) then
      int1 := int1 - 1;
    end if;
    if (mod(int2, 2) = 1) then
      int2 := int2 - 1;
    end if;
    int1 := int1 / 2;
    int2 := int2 / 2;
    position := position * 2;
  end loop;
  return result;
exception
  when OTHERS then
    return 0;
end;
/
show errors;


/******************************************************************************
 *  
 */
create or replace function oneHexDigitToChar ( input IN NUMBER )
return CHAR as
begin
  if (input = 0) then
    return ('0');
  elsif (input = 1) then
    return ('1');
  elsif (input = 2) then
    return ('2');
  elsif (input = 3) then
    return ('3');
  elsif (input = 4) then
    return ('4');
  elsif (input = 5) then
    return ('5');
  elsif (input = 6) then
    return ('6');
  elsif (input = 7) then
    return ('7');
  elsif (input = 8) then
    return ('8');
  elsif (input = 9) then
    return ('9');
  elsif (input = 10) then
    return ('A');
  elsif (input = 11) then
    return ('B');
  elsif (input = 12) then
    return ('C');
  elsif (input = 13) then
    return ('D');
  elsif (input = 14) then
    return ('E');
  elsif (input = 15) then
    return ('F');
  else
    return ('0');
  end if;
exception
  when OTHERS then
    return ('0');
end;
/
show errors;



/******************************************************************************
 *  
 */
create or replace function intToRaw ( input IN INTEGER, inlength IN INTEGER )
return RAW as
temp INTEGER;  
temp2 INTEGER;
c1 CHAR(1);
c2 CHAR(1);
retValue RAW(254);
len INTEGER;
begin
  temp := input;
  retValue := hextoraw( '00' );
  len := 0;
  while (temp > 0)
  loop
    temp2 := mod ( temp, 16 );
    temp := temp - temp2;
    temp := temp / 16;
    c1 := oneHexDigitToChar ( temp2 );
    if (temp > 0) then
      temp2 := mod (temp, 16 );
      temp := temp - temp2;
      temp := temp / 16;
      c2 := oneHexDigitToChar ( temp2 );
    else
      c2 := '0';
    end if;
    if (len = 0) then
      retValue := hextoraw (c2 || c1);
    else
      retValue := hextoraw (c2 || c1) || retValue;
    end if;
    len := len + 2;
  end loop;
  if (input = 0) then
    len := 2;
  end if;
  while (len < inlength)
  loop
    retValue := hextoraw ('00') || retValue;
    len := len + 2;
  end loop;
  return (retValue);
exception
  when OTHERS then
    len := 0;
    while (len < inlength)
    loop
      if (len = 0 ) then
        retValue := hextoraw ('00');
      else
        retValue := retValue || hextoraw ('00');
      end if;
      len := len + 2;
    end loop;
    return retValue;
end;
/
show errors;


/******************************************************************************
 * Compute the oid out of tVersionId and id. <BR>
 * The oid is computed as concatenation of the tVersionId and the object's id.
 *
 * @input parameters:
 * @param   ai_tVersionId       The type version id.
 * @param   ai_id               The id of the object.
 *
 * @output parameters:
 * @return  The computed oid.
 *
 * @deprecated  Use p_createOid instead.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION createOid
(
    ai_tVersionId           IN NUMBER,
    ai_id                   IN NUMBER
)
RETURN RAW
AS
BEGIN
    -- convert the type version id and the id to raw and concatenate them:
    return intToRaw (ai_tVersionId, 8) || intToRaw (ai_id, 8);
END createOid;
/
show errors;



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
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_createOid
(
    -- input parameters:
    ai_tVersionId           IN NUMBER,
    ai_id                   IN NUMBER,
    -- output parameters:
    ao_oid                  OUT RAW
)
AS
-- body:
BEGIN
    -- convert the type version id and the id to raw and concatenate them:
    ao_oid := intToRaw (ai_tVersionId, 8) || intToRaw (ai_id, 8);
END p_createOid;
/
show errors;



/******************************************************************************
 *  
 */
create or replace function db_Name return varchar2 as
begin
  return ('M2');
end;
/
show errors;



/******************************************************************************
 *  
 */
create or replace function binary_char_to_integer ( value IN CHAR )
return INTEGER as
begin
  if value = '0' then
    return 0;
  elsif value = '1' then
    return 1;
  elsif value = '2' then
    return 2;
  elsif value = '3' then
    return 3;
  elsif value = '4' then
    return 4;
  elsif value = '5' then
    return 5;
  elsif value = '6' then
    return 6;
  elsif value = '7' then
    return 7;
  elsif value = '8' then
    return 8;
  elsif value = '9' then
    return 9;
  elsif value = 'A' then
    return 10;
  elsif value = 'B' then
    return 11;
  elsif value = 'C' then
    return 12;
  elsif value = 'D' then
    return 13;
  elsif value = 'E' then
    return 14;
  elsif value = 'F' then
    return 15;
  elsif value = 'a' then
    return 10;
  elsif value = 'b' then
    return 11;
  elsif value = 'c' then
    return 12;
  elsif value = 'd' then
    return 13;
  elsif value = 'e' then
    return 14;
  elsif value = 'f' then
    return 15;
  else
    return 0;
  end if;
exception
  when OTHERS then
    return 0;
end;
/
show errors;



/******************************************************************************
 *  
 */
create or replace function binary_integer_to_char ( value IN INTEGER )
return CHAR as
begin
  if value = 0 then
    return '0';
  elsif value = 1 then
    return '1';
  elsif value = 2 then
    return '2';
  elsif value = 3 then
    return '3';
  elsif value = 4 then
    return '4';
  elsif value = 5 then
    return '5';
  elsif value = 6 then
    return '6';
  elsif value = 7 then
    return '7';
  elsif value = 8 then
    return '8';
  elsif value = 9 then
    return '9';
  elsif value = 10 then
    return 'A';
  elsif value = 11 then
    return 'B';
  elsif value = 12 then
    return 'C';
  elsif value = 13 then
    return 'D';
  elsif value = 14 then
    return 'E';
  elsif value = 15 then
    return 'F';
  else
    return '0';
  end if;
exception
  when OTHERS then
    return '0';
end;
/
show errors;



/******************************************************************************
 *  
 */
create or replace function stringToInt ( value IN CHAR )
return INTEGER as
i INTEGER;
result INTEGER;
endpos INTEGER;
temp VARCHAR(254);
begin
  temp := value;
  begin
    if (( substr (temp, 1, 2) = '0x' ) or
        ( substr (temp, 1, 2) = '0X')) then
      temp := substr ( value, 3, length(value) - 2 );
    end if;
  exception
    when OTHERS then
      null;
  end;
  result := 0;
  endpos := length (temp);
  for i in 1..endpos loop
    result := result * 16 + binary_char_to_integer ( substr (temp, i, 1));
  end loop;
  return result;
exception
  when OTHERS then
    return 0;
end;
/
show errors;


/******************************************************************************
 *  
 */
create or replace function intToString ( value IN INTEGER )
return CHAR as
result VARCHAR2 (16);
rest INTEGER;
i INTEGER;
begin
  result := '';
  rest := value;
  if value = 0 then
    result := '0';
  end if;
  while (rest >0)
  loop
    i := mod( rest, 16);
    result := concat (result, binary_integer_to_char (i));
    rest := (rest - i) / 16;
  end loop;
  while length (result) < 16
  loop
    result := concat ('0', result);
  end loop;
  return result;
exception
  when OTHERS then
    return '0000000000000000';
end;
/
show errors;



/******************************************************************************
 *  
 */
CREATE OR REPLACE PROCEDURE err
AS
BEGIN
    DBMS_OUTPUT.PUT_LINE ('Error ' || SQLCODE || ': ' || SQLERRM);
END;
/
show errors;



/******************************************************************************
 *  
 */
CREATE OR REPLACE FUNCTION LIKEFUNCTION(INSTR VARCHAR2,INSTRTWO VARCHAR)
RETURN INTEGER
AS
RETVAL INTEGER;
BEGIN
	SELECT 1 INTO LIKEFUNCTION.RETVAL FROM DUAL WHERE INSTR LIKE INSTRTWO;
	RETURN 1;
EXCEPTION
	WHEN OTHERS THEN
	RETURN 0;
END LIKEFUNCTION;
/
show errors;


 
/******************************************************************************
 *  
 */
CREATE OR REPLACE FUNCTION USER_NAME(INID INTEGER)
	RETURN VARCHAR2
AS
	OUTSTRING VARCHAR2(30);
BEGIN
	SELECT USERNAME INTO USER_NAME.OUTSTRING 
	FROM ALL_USERS WHERE USER_ID=INID;
	RETURN OUTSTRING;
EXCEPTION
	WHEN OTHERS THEN
	RETURN 'NOONE';
END USER_NAME;
/
show errors;


 
/******************************************************************************
 * Execute a query. <BR>
 *
 * @input parameters:
 * @param   ai_query            The query to be executed.
 * @param   ai_errorMessage     The message to be added to the log if something
 *                              goes wrong.
 *
 * @output parameters:
 * @param   ao_rowsProcessed    The number of processed rows.
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_NOT_OK                Some error occurred in the procedure.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_execQuery
(
    -- input parameters:
    ai_query                VARCHAR2,       -- the query to be executed
    ai_errorMessage         VARCHAR2,       -- error message if something went
                                            -- wrong
    -- output parameters:
    ao_rowsProcessed        OUT INTEGER     -- number of processed rows
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution
    l_message               VARCHAR2 (8000); -- message to be printed

-- body:
BEGIN
    BEGIN
        -- open the cursor:
        l_cursorId := DBMS_SQL.OPEN_CURSOR;
        -- parse the statement and use the normal behavior of the
        -- database to which we are currently connected:
        DBMS_SQL.PARSE (l_cursorId, ai_query, DBMS_SQL.NATIVE);
        ao_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);
        -- close the cursor:
        DBMS_SQL.CLOSE_CURSOR (l_cursorId);

    EXCEPTION
        WHEN OTHERS THEN
            IF (DBMS_SQL.IS_OPEN (l_cursorId))
                                    -- the cursor is currently open?
            THEN
                -- close the cursor:
                DBMS_SQL.CLOSE_CURSOR (l_cursorId);
            END IF; -- the cursor is currently open
            -- create error entry:
            l_ePos := ai_errorMessage || ' at ' || l_lastErrorPos || '.' ||
                '(' || ai_query || ')';
--debug (l_ePos);
            RAISE;                  -- call common exception handler
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; ai_query = ' || ai_query || CHR (13) ||
            '; errorcode = ' || SQLCODE || CHR (13) ||
            ', errormessage = ' || SQLERRM;
        debug (l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_execQuery;
/
show errors;


/******************************************************************************
 *  
 */
CREATE OR REPLACE PROCEDURE EXEC_SQL
(
    ai_query                IN VARCHAR2     -- the query to be executed
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_rowsProcessed         INTEGER := 0;   -- number of processed rows

-- body:
BEGIN
    -- execute the query:
    l_retValue := p_execQuery (ai_query, 'EXEC_SQL', l_rowsProcessed);
END EXEC_SQL;
/
show errors;

EXIT;
