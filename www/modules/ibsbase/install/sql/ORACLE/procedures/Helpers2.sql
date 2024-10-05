/******************************************************************************
 * Stored procedures regarding basic database functions. <BR>
 * These stored procedures are using error handling functionality.
 *
 * @version     2.21.0007, 18.03.2002 KR
 *
 * @author      Klaus Reimüller (KR)  001031
 ******************************************************************************
 */

set define off;
/******************************************************************************
 * Converts a string representation of an object id to its binary 
 * representation. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            The value to be converted to byte.
 *
 * @output parameters:
 * @param   ao_oid              The byte value of the oid.
 */
CREATE OR REPLACE PROCEDURE p_stringToByte
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    -- output parameters:
    ao_oid                  OUT RAW
)
AS
    -- constants: 
    c_NOOID                 CONSTANT RAW (8) := hextoraw ('0000000000000000');
                                            -- oid of not existing object
 
    -- local variables: 
    l_oid_s                 VARCHAR2 (18) := ai_oid_s; -- the local variable
    l_count                 INTEGER;        -- number of elements


-- body:
BEGIN
    -- initialize the return value:
    ao_oid := NULL;

    -- drop the leading '0x':
    IF (length (l_oid_s) = 18 AND SUBSTR (UPPER (l_oid_s), 1, 2) = '0X')
    THEN
        l_oid_s := SUBSTR (UPPER (l_oid_s), 3, LENGTH (l_oid_s) - 2);
    END IF;

    -- check if the oid has a correct length:
    IF (length (l_oid_s) = 16)          -- correct length?
    THEN
        -- compute the oid:
        ao_oid := hextoraw (l_oid_s);
    -- if correct length
    ELSE                                -- not correct length
        -- no valid oid:
        ao_oid := NULL;
    END IF; -- else not correct length

    -- check if the oid was valid:
    IF (ao_oid IS NOT NULL)             -- valid oid?
    THEN
        -- compute the length of the oid:
        l_count := length (ao_oid);
        WHILE (l_count < 16)
        LOOP
            -- add leading zeros:
            ao_oid := hextoraw ('00') || ao_oid;
            l_count := l_count + 1;
        END LOOP;
    -- if valid oid
    ELSE                                -- oid not valid
        -- set default return value:
        ao_oid := c_NOOID;
    END IF; -- else oid not valid

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_stringToByte',
            ' ai_oid_s: ' || ai_oid_s ||
            ', ao_oid: ' || ao_oid ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_stringToByte;
/
show errors;


/******************************************************************************
 * Converts a binary representation of an object id to its string
 * representation. <BR>
 *
 * @input parameters:
 * @param   ai_oid              The value to be converted to string.
 *
 * @output parameters:
 * @param   ao_oid_s            The string value of the oid.
 */
CREATE OR REPLACE PROCEDURE p_byteToString
(
    -- input parameters:
    ai_oid                  RAW,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2
)
AS
    -- constants: 
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
                                            -- no oid as string
 
    -- local variables: 
-- body:
BEGIN
    -- initialize the return value:
    ao_oid_s := c_NOOID_s;

    -- convert value to string:
    ao_oid_s := rawtohex (ai_oid);

    -- add leading zeros:
    WHILE (length (ao_oid_s) < 16)
    LOOP
        ao_oid_s := '0' || ao_oid_s;
    END LOOP;

    -- add header which indicates that the value is a hex representation:
    ao_oid_s := '0x' || ao_oid_s;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_byteToString',
            ' ai_oid: ' || ai_oid ||
            ', ao_oid_s: ' || ao_oid_s ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_byteToString;
/
show errors;


/******************************************************************************
 * Display a long string. <BR>
 * The long string is represented through a set of string variables. These
 * variables must be concatenated to get the whole string.
 *
 * @input parameters:
 * @param   ai_title            Title to be displayed before the string.
 * @param   ai_strXX            Content of the XXth part of the string.
 *
 * @output parameters:
 */
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_displayString
( 
    -- input parameters: 
    ai_title                VARCHAR2,
    ai_str                  VARCHAR2
) 
AS
    -- constants: 
 
    -- local variables: 

-- body:
BEGIN
    -- display the title:
    debug (ai_title);

    -- display the string as is:
    debug (ai_str);
/*
    l_actLength = 0;
    -- display the several parts:
    -- display each part if it is not empty
    WHILE (l_length > l_actLength)
    LOOP
        debug (SUBSTR (ai_str, l_actLength + 1, 255));
        l_actLength := l_actLength + 255;
    END LOOP;
*/
END p_displayString;
/
show errors;


/******************************************************************************
 * Change scheme of a table. <BR>
 * This procedure compares the original table and a table containing the new
 * scheme. It then copies each attribute value of the original table to the
 * new table which also exists in the new table. Attributes which do not exist
 * in the new table are omitted. <BR>
 * For each attribute which exists just in the new table and not in the old one
 * there must be a default value within the set of parameters. If such an
 * attribute is not mentioned the whole process is cancelled. Each attribute
 * value which represents a string must also contain quotes. Otherwise a syntax
 * error will be raised.
 * E.g.: ai_attrName1 = 'name', ai_attrValue1 = '''myName'''
 *       ai_attrName2 = 'id', ai_attrValue2 = '78'
 * For SQL Server the ai_attrNameX and ai_attrValueX parameters are optional.
 * <BR>
 * After the copy process is finished the old table is deleted and the new
 * table is renamed to the name of the original table. <BR>
 * The procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block. <BR>
 * ATTENTION: Ensure that each trigger and procedure working on this table
 * are newly created.
 *
 * @input parameters:
 * @param   ai_context          Context name or description from within this
 *                              procedure was called. This string is used as
 *                              output and logging prefix.
 * @param   ai_tableName        Name of the table to be changed.
 * @param   ai_tempTableName    The temporary name of the table containing the
 *                              new structure.
 * @param   ai_attrName1        Name of attribute to be added.
 * @param   ai_attrValue1       Default value of attribute to be added.
 * @param   ai_attrName2        Name of attribute to be added.
 * @param   ai_attrValue2       Default value of attribute to be added.
 * @param   ai_attrName3        Name of attribute to be added.
 * @param   ai_attrValue3       Default value of attribute to be added.
 * @param   ai_attrName4        Name of attribute to be added.
 * @param   ai_attrValue4       Default value of attribute to be added.
 * @param   ai_attrName5        Name of attribute to be added.
 * @param   ai_attrValue5       Default value of attribute to be added.
 * @param   ai_attrName6        Name of attribute to be added.
 * @param   ai_attrValue6       Default value of attribute to be added.
 * @param   ai_attrName7        Name of attribute to be added.
 * @param   ai_attrValue7       Default value of attribute to be added.
 * @param   ai_attrName8        Name of attribute to be added.
 * @param   ai_attrValue8       Default value of attribute to be added.
 * @param   ai_attrName9        Name of attribute to be added.
 * @param   ai_attrValue9       Default value of attribute to be added.
 * @param   ai_attrName10       Name of attribute to be added.
 * @param   ai_attrValue10      Default value of attribute to be added.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_changeTable
( 
    -- input parameters: 
    ai_context              VARCHAR2,
    ai_tableName            VARCHAR2,
    ai_tempTableName        VARCHAR2,
    ai_attrName1            VARCHAR2,
    ai_attrValue1           VARCHAR2,
    ai_attrName2            VARCHAR2,
    ai_attrValue2           VARCHAR2,
    ai_attrName3            VARCHAR2,
    ai_attrValue3           VARCHAR2,
    ai_attrName4            VARCHAR2,
    ai_attrValue4           VARCHAR2,
    ai_attrName5            VARCHAR2,
    ai_attrValue5           VARCHAR2,
    ai_attrName6            VARCHAR2,
    ai_attrValue6           VARCHAR2,
    ai_attrName7            VARCHAR2,
    ai_attrValue7           VARCHAR2,
    ai_attrName8            VARCHAR2,
    ai_attrValue8           VARCHAR2,
    ai_attrName9            VARCHAR2,
    ai_attrValue9           VARCHAR2,
    ai_attrName10           VARCHAR2,
    ai_attrValue10          VARCHAR2
    -- output parameters:
) 
AS
    -- constants: 
 
    -- local variables: 
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_rowsProcessed         INTEGER;        -- number of rows of last cmd exec.
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution
    l_oldTableName          VARCHAR2 (30) := ai_tableName;
                                            -- name of table with old scheme
    l_newTableName          VARCHAR2 (30) := ai_tempTableName;
                                            -- name of table with new scheme
    l_oldAttributes         VARCHAR2 (8000) := '';
                                            -- the comma-separated attributes
                                            -- of the old table structure which
                                            -- shall be assigned to the new
                                            -- attributes
    l_newAttributes         VARCHAR2 (8000) := '';
                                            -- the comma-separated attributes
                                            -- of the new table structure
    l_addedAttributes       VARCHAR2 (8000) := '';
                                            -- the attributes which where added
    l_deletedAttributes     VARCHAR2 (8000) := '';
                                            -- the attributes which where
                                            -- deleted
    l_notFoundAttributes    VARCHAR2 (8000) := '';
                                            -- the attributes which where
                                            -- not found in the parameters
    l_attrNameNew           VARCHAR2 (30);  -- the actual attribute name in the
                                            -- new scheme
    l_attrNameOld           VARCHAR2 (30);  -- the actual attribute name in the
                                            -- old scheme
    l_attrValue             VARCHAR2 (255); -- value of actual attribute
    l_sep                   VARCHAR2 (2) := '';
                                            -- the actual attribute separator
    -- define cursor:
    CURSOR tableCursor IS
        SELECT  new.column_name as newName, old.column_name AS oldName
        FROM    (
                    SELECT  UPPER (column_name) AS column_name
                    FROM    all_col_comments
                    WHERE   UPPER (table_name) = UPPER (l_newTableName)
                ) new,
                (
                    SELECT  UPPER (column_name) AS column_name
                    FROM    all_col_comments
                    WHERE   UPPER (table_name) = UPPER (l_oldTableName)
                ) old
        WHERE   new.column_name(+) = old.column_name
        UNION
        SELECT  UPPER (column_name) AS newName, TO_CHAR (null) AS oldName
        FROM    all_col_comments
        WHERE   UPPER (table_name) = UPPER (l_newTableName)
            AND UPPER (column_name) NOT IN
                (
                    SELECT  UPPER (column_name)
                    FROM    all_col_comments
                    WHERE   UPPER (table_name) = UPPER (l_oldTableName)
                )
                ;
    l_cursorRow             tableCursor%ROWTYPE;

    -- exceptions:
    e_attributeNotFound     EXCEPTION;

-- body:
BEGIN
    -- check if the target table and the temporary table are the same:
    IF (l_newTableName <> l_oldTableName) -- the tables are different?
    THEN
        COMMIT WORK; -- finish previous and begin new TRANSACTION

/**
 * This is not necessary because the table is already created.
 * But maybe we can use this in the future for some purpose.
 *
        -- create the new table:
        l_cmdString :=
            'CREATE TABLE ' || l_tempTableName ||
            ' (' || ai_tableStructure || ') ';
        BEGIN
            -- open the cursor:
            l_cursorId := DBMS_SQL.OPEN_CURSOR;
            -- parse the statement and use the normal behavior of the
            -- database to which we are currently connected:
        	DBMS_SQL.PARSE (l_cursorId, l_cmdString, DBMS_SQL.NATIVE);
        	-- remember the possible error position:
            l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
        	l_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);
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
                l_ePos :=
                    'Error in CREATE TABLE at ' || l_lastErrorPos ||
                    ' - Input: ' || l_cmdString;
                RAISE;                  -- call common exception handler
        END;
*/

        -- check if the target table exists:
        BEGIN
            SELECt  table_name
            INTO    l_oldTableName
            FROM    user_tables
            WHERE   UPPER (table_name) = UPPER (l_oldTableName);

            -- at this point we know that the target table exists.
            -- get all attribute names of the new table scheme and the
            -- corresponding attribute names of the old scheme:
            -- loop through the cursor rows:
            FOR l_cursorRow IN tableCursor
            LOOP
                -- get the actual tuple values:
                l_attrNameNew := l_cursorRow.newName;
                l_attrNameOld := l_cursorRow.oldName;
/* for debugging purposes:
debug ('attribute: ' || l_attrNameNew || '/' || l_attrNameOld);
*/

                -- check if the attribute exists in the new scheme:
                IF (l_attrNameNew IS NOT NULL)
                                        -- the attribute exists in new scheme?
                THEN
                    -- add the attribute to the new attribute string:
                    l_newAttributes :=
                        l_newAttributes || l_sep || l_attrNameNew;

                    -- check if the new attribute was also in the old scheme:
                    IF (l_attrNameOld IS NOT NULL)
                                        -- the attribute existed in old sch.?
                    THEN
                        -- add the attribute to the old attribute string:
                        l_oldAttributes :=
                            l_oldAttributes || l_sep || l_attrNameOld;
                    ELSE                -- attribute was not in old scheme
                        -- check if the attribute was in the attribute list:
                        IF (l_attrNameNew = UPPER (ai_attrName1))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue1;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName2))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue2;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName3))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue3;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName4))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue4;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName5))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue5;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName6))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue6;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName7))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue7;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName8))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue8;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName9))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue9;
                        ELSIF (l_attrNameNew = UPPER (ai_attrName10))
                                        -- attribute found?
                        THEN
                            -- get the attribute value:
                            l_attrValue := ai_attrValue10;
                        ELSE            -- attribute was not found
                            -- add the attribute to the not found attribute
                            -- string:
                            l_notFoundAttributes :=
                                l_notFoundAttributes || ' ' || l_attrNameNew;
                        END IF; -- else attribute was not found

                        -- add the attribute value to the old attribute string:
                        l_oldAttributes :=
                            l_oldAttributes || l_sep || l_attrValue;

                        -- add the attribute to the added attribute string:
                        l_addedAttributes :=
                            l_addedAttributes || ' ' || l_attrNameNew;
                    END IF; -- else attribute was not in old scheme

                    -- set the next separator:
                    l_sep := ',';
                ELSE                    -- attribute is not in new scheme
                    -- add the attribute to the deleted attribute string:
                    l_deletedAttributes :=
                        l_deletedAttributes || ' ' || l_attrNameOld;
                END IF; -- else attribute is not in new scheme
            END LOOP; -- while another tuple found

            -- check if all necessary parameters are there:
            IF (l_notFoundAttributes <> '') -- not all parameters are there?
            THEN
                -- create error entry:
                l_ePos :=
                    'Error in find attribute ' ||
                    ' - no value for attributes: ' || l_notFoundAttributes;
                RAISE e_attributeNotFound;
                                        -- call common exception handler
            END IF; -- not all parameters are there

            -- copy the old attribute values to the new table:
            l_cmdString :=
                'INSERT INTO ' || l_newTableName ||
                ' (' || l_newAttributes || ')' ||
                ' SELECT ' || l_oldAttributes ||
                ' FROM ' || l_oldTableName;
            BEGIN
                -- open the cursor:
                l_cursorId := DBMS_SQL.OPEN_CURSOR;
                -- parse the statement and use the normal behavior of the
                -- database to which we are currently connected:
            	DBMS_SQL.PARSE (l_cursorId, l_cmdString, DBMS_SQL.NATIVE);
            	-- remember the possible error position:
                l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
            	l_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);
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
                    l_ePos :=
                        'Error in INSERT at ' || l_lastErrorPos;
                    RAISE;              -- call common exception handler
            END;


            -- drop old table:
            p_dropTable (l_oldTableName);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- table already deleted
                debug (ai_context ||
                    ': Target table ' || l_oldTableName ||
                    ' not found -> just rename the new table.');
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos :=
                    'Error in get temp table: ' || ai_tempTableName;
                RAISE;                  -- call common exception handler
        END;

        -- rename new table:
        EXEC_SQL ('RENAME ' || l_newTableName || ' TO ' || l_oldTableName);

        -- check if the temporary table exists:
        BEGIN
            SELECT  table_name
            INTO    l_oldTableName
            FROM    user_tables
            WHERE   UPPER (table_name) = UPPER (ai_tempTableName);

            -- at this point we know that the temporary table still exists.
            -- drop the temporary table:
            p_dropTable (ai_tempTableName);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- table already deleted
                NULL;                   -- nothing to do
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos :=
                    'Error in get temp table: ' || ai_tempTableName;
                RAISE;                  -- call common exception handler
        END;

        -- make changes permanent and set new transaction starting point:
        COMMIT WORK;

        -- print state report:
        debug (ai_context || ': table ' || ai_tableName ||
            ' changed to new scheme.');
        p_displayString (' - added attributes: ', l_addedAttributes);
        p_displayString (' - deleted attributes: ', l_deletedAttributes);
/* for debugging purposes:
p_displayString (' - new attributes: ', l_newAttributes);
p_displayString (' - old attributes: ', l_oldAttributes);
*/
    ELSE                                -- the tables are the same
        debug (ai_context ||
            ': The tables are the same -> nothing to be done.');
    END IF; -- else the tables are the same

EXCEPTION 
    WHEN e_attributeNotFound THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := ai_context || ': Error when changing table ' || 
            ai_tableName || ': ' || l_ePos ||
            '; errorcode = 0 ' ||
            ', errormessage = Attribute not found';
        ibs_error.log_error (ibs_error.error, ai_context, l_eText);
        debug (l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := ai_context || ': Error when changing table ' ||
            ai_tableName || ': ' || l_ePos ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, ai_context, l_eText);
        debug (l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
END p_changeTable;
/
show errors;


/******************************************************************************
 * Set a clob attribute of an object within a specific table. <BR>
 * The procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            String representation of oid of the object to
 *                              be changed.
 * @param   ai_tableName        Name of the table to be changed.
 * @param   ai_attrName         Name of attribute to be changed.
 * @param   ai_attrValue        New value of attribute.
 *
 * @output parameters:
 */
CREATE OR REPLACE FUNCTION p_setAttribute
( 
    -- input parameters: 
    ai_oid_s                VARCHAR2,
    ai_tableName            VARCHAR2,
    ai_attrName             VARCHAR2,
    ai_attrValue            CLOB
    -- output parameters:
) 
RETURN INTEGER
AS
    -- constants: 
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
 
    -- local variables: 
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_rowsProcessed         INTEGER;        -- number of rows of last cmd exec.
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution
    l_oid                   RAW (8);        -- the current oid
    l_tableName             VARCHAR2 (30);  -- the current table name
    l_attrName              VARCHAR2 (30);  -- the current attribute name

    -- exceptions:
    e_tableNotFound         EXCEPTION;
    e_attributeNotFound     EXCEPTION;
    e_statementExecError    EXCEPTION;

-- body:
BEGIN
    -- convert string representation of oid into binary representation:
    p_stringToByte (ai_oid_s, l_oid);

    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- check if the table exists:
    BEGIN
        SELECt  table_name
        INTO    l_tableName
        FROM    user_tables
        WHERE   UPPER (table_name) = UPPER (ai_tableName);

        -- at this point we know that the table exists.
        -- check if the attribute exists:
        BEGIN
            SELECT  UPPER (column_name)
            INTO    l_attrName
            FROM    all_col_comments
            WHERE   UPPER (table_name) = UPPER (ai_tableName)
                AND UPPER (column_name) = UPPER (ai_attrName);

            -- at this point we know that the attribute exists.
            -- set the attribute value:
            l_cmdString :=
                'UPDATE /*USER*/' || ai_tableName ||
                ' SET ' || ai_attrName || ' = :attrValue' ||
                ' WHERE oid = hextoraw (''' || rawtohex (l_oid) || ''')';
            BEGIN
                -- open the cursor:
                l_cursorId := DBMS_SQL.OPEN_CURSOR;
                -- parse the statement and use the normal behavior of the
                -- database to which we are currently connected:
                DBMS_SQL.PARSE (l_cursorId, l_cmdString, DBMS_SQL.NATIVE);
                -- remember the possible error position:
                l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
                -- bind the variable:
                DBMS_SQL.BIND_VARIABLE (l_cursorId, ':attrValue', ai_attrValue);
                l_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);
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
                    l_ePos := 'Error in UPDATE at ' || l_lastErrorPos || '.' ||
                        '(' || l_cmdString || ')';
                    RAISE e_statementExecError; -- call common exception handler
            END;

        EXCEPTION
            WHEN e_statementExecError THEN -- statement execution error
                RAISE;                  -- call common exception handler
            WHEN NO_DATA_FOUND THEN     -- attribute not found
                -- create error entry:
                l_ePos := 'Attribute not found.';
                RAISE e_attributeNotFound; -- call common exception handler
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'Other error when checking if attribute exists.';
                RAISE e_attributeNotFound; -- call common exception handler
        END;

    EXCEPTION
        WHEN e_statementExecError THEN  -- statement execution error
            RAISE;                      -- call common exception handler
        WHEN e_attributeNotFound THEN   -- attribute not found
            RAISE;                      -- call common exception handler
        WHEN NO_DATA_FOUND THEN         -- table not found
            -- create error entry:
            l_ePos := 'Table not found.';
            RAISE e_tableNotFound;      -- call common exception handler
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'Other error when checking if table exists.';
            RAISE e_tableNotFound;      -- call common exception handler
    END;

    -- make changes permanent and set new transaction starting point:
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            ' Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_tableName = ' || ai_tableName ||
            ', ai_attrName = ' || ai_attrName ||
-- it is not possible to concatenate CLOBs.
--            ', ai_attrValue = ' || ai_attrValue ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_setAttribute', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_setAttribute;
/
show errors;


/******************************************************************************
 * Get a clob attribute of an object within a specific table. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            String representation of oid of the object to
 *                              be changed.
 * @param   ai_tableName        Name of the table to be changed.
 * @param   ai_attrName         Name of attribute to be changed.
 * @param   ao_attrValue        The value of attribute.
 *
 * @output parameters:
 */
CREATE OR REPLACE FUNCTION p_getAttribute
( 
    -- input parameters: 
    ai_oid_s                VARCHAR2,
    ai_tableName            VARCHAR2,
    ai_attrName             VARCHAR2,
    -- output parameters:
    ao_attrValue            OUT CLOB
) 
RETURN INTEGER
AS
    -- constants: 
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
 
    -- local variables: 
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_rowsProcessed         INTEGER;        -- number of rows of last cmd exec.
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution
    l_oid                   RAW (8);        -- the current oid
    l_tableName             VARCHAR2 (30);  -- the current table name
    l_attrName              VARCHAR2 (30);  -- the current attribute name

    -- exceptions:
    e_tableNotFound         EXCEPTION;
    e_attributeNotFound     EXCEPTION;
    e_statementExecError    EXCEPTION;

-- body:
BEGIN
    -- convert string representation of oid into binary representation:
    p_stringToByte (ai_oid_s, l_oid);

    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- check if the table exists:
    BEGIN
        SELECt  table_name
        INTO    l_tableName
        FROM    user_tables
        WHERE   UPPER (table_name) = UPPER (ai_tableName);

        -- at this point we know that the table exists.
        -- check if the attribute exists:
        BEGIN
            SELECT  UPPER (column_name)
            INTO    l_attrName
            FROM    all_col_comments
            WHERE   UPPER (table_name) = UPPER (ai_tableName)
                AND UPPER (column_name) = UPPER (ai_attrName);

            -- at this point we know that the attribute exists.
            -- set the attribute value:
            l_cmdString :=
                'SELECT ' || ai_attrName ||
                ' FROM ' || ai_tableName ||
                ' WHERE oid = hextoraw (''' || rawtohex (l_oid) || ''')';
            BEGIN
                -- open the cursor:
                l_cursorId := DBMS_SQL.OPEN_CURSOR;
                -- parse the statement and use the normal behavior of the
                -- database to which we are currently connected:
                DBMS_SQL.PARSE (l_cursorId, l_cmdString, DBMS_SQL.NATIVE);
                -- remember the possible error position:
                l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
                -- bind the column and assign a type to it:
                DBMS_SQL.DEFINE_COLUMN (l_cursorId, 1, ao_attrValue);
                l_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);

                -- fetch the first row from the result set:
                -- (other rows are not taken into account)
                IF (DBMS_SQL.FETCH_ROWS (l_cursorId) > 0) -- a row was found
                THEN
                    -- get the value and assign it to the output variable:
                    DBMS_SQL.COLUMN_VALUE (l_cursorId, 1, ao_attrValue);
                END IF; -- a row was found
                -- close the cursor:
                DBMS_SQL.CLOSE_CURSOR (l_cursorId);
            EXCEPTION
                WHEN OTHERS THEN 
                    IF (DBMS_SQL.IS_OPEN (l_cursorId))
                                        -- the cursor is currently open
                    THEN
                        -- close the cursor:
                        DBMS_SQL.CLOSE_CURSOR (l_cursorId);
                    END IF; -- the cursor is currently open
                    -- create error entry:
                    l_ePos := 'Error in UPDATE at ' || l_lastErrorPos || '.' ||
                        '(' || l_cmdString || ')';
                    RAISE e_statementExecError; -- call common exception handler
            END;

        EXCEPTION
            WHEN e_statementExecError THEN -- statement execution error
                RAISE;                  -- call common exception handler
            WHEN NO_DATA_FOUND THEN     -- attribute not found
                -- create error entry:
                l_ePos := 'Attribute not found.';
                RAISE e_attributeNotFound; -- call common exception handler
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'Other error when checking if attribute exists.';
                RAISE e_attributeNotFound; -- call common exception handler
        END;

    EXCEPTION
        WHEN e_statementExecError THEN  -- statement execution error
            RAISE;                      -- call common exception handler
        WHEN e_attributeNotFound THEN   -- attribute not found
            RAISE;                      -- call common exception handler
        WHEN NO_DATA_FOUND THEN         -- table not found
            -- create error entry:
            l_ePos := 'Table not found.';
            RAISE e_tableNotFound;      -- call common exception handler
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'Other error when checking if table exists.';
            RAISE e_tableNotFound;      -- call common exception handler
    END;

    -- make changes permanent and set new transaction starting point:
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            ' Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_tableName = ' || ai_tableName ||
            ', ai_attrName = ' || ai_attrName ||
-- it is not possible to concatenate CLOBs.
--            ', ao_attrValue = ' || ao_attrValue ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_setAttribute', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_getAttribute;
/
show errors;


/******************************************************************************
 * Change name of a table. <BR>
 *
 * @param   ai_context          Context name or description from within this
 *                              procedure was called. This string is used as
 *                              output and logging prefix.
 * @param   ai_oldTableName     The original name of the table.
 * @param   ai_newTableName     The new name of the table.
 */
CREATE OR REPLACE PROCEDURE p_renameTable
( 
    -- input parameters: 
    ai_context              VARCHAR2,
    ai_oldTableName         VARCHAR2,
    ai_newTableName         VARCHAR2
    -- output parameters:
) 
AS
    -- constants: 
 
    -- local variables: 
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_cmdString             VARCHAR2 (2000); -- command line to be executed

-- body:
BEGIN
    -- check if the old and the new table name are the same:
    IF (ai_newTableName <> ai_oldTableName) -- the tables are different?
    THEN
        COMMIT WORK; -- finish previous and begin new TRANSACTION

        BEGIN
            -- rename the table:
            EXEC_SQL ('RENAME ' || ai_oldTableName || ' TO ' || ai_newTableName);
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- log the error:
                l_ePos := 'error in RENAME';
                RAISE;                  -- call common exception handler
        END;

        -- make changes permanent and set new transaction starting point:
        COMMIT WORK;

        -- print state report:
        debug (ai_context || ': table ''' || ai_oldTableName ||
            ''' renamed to ''' || ai_newTableName || '''.');
    ELSE                                -- the tables are the same
        debug (ai_context ||
            ': The table names are the same -> nothing to be done.');
    END IF; -- else the tables are the same

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := ai_context || ': Error when renaming table ''' ||
            ai_oldTableName || ''' to ''' || @ai_newTableName || ''': ' || l_ePos ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, ai_context, l_eText);
        debug (l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
END p_renameTable;
/
show errors;


EXIT;
