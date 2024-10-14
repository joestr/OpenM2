/******************************************************************************
 * Procedures required for reorganizing the database. <BR>
 * Reorganizing of a table means to move the not longer needed to an archive
 * table and deleting them from the original table. The archive table is a
 * table with the same name as the original table with '_archive' concatenated.
 * <BR>
 * In this first version just the tables ibs_Object, ibs_ObjectRead,
 * ibs_RightsKeys, and ibs_RightsCum are considered to be reorganized.
 *
 * @version     2.21.0004, 11.06.2002
 *
 * @author      Mario Stegbauer (MS)  000207
 ******************************************************************************
 */

/*
 * The following procedures are implemented within this script:
 *
 * p_showMessage:
 *      Show one message.
 *      This procedure allows to display the message immediately when it is 
 *      printed. So it can be used for giving the user a feedback what the 
 *      program is doing.
 *
 * p_showActDateTime:
 *      Show one message and append the actual date and time to it.
 *      This procedure computes the actual date and time in milliseconds,
 *      appends it to the message text and sends this data to p_showMessage.
 *
 * p_reorgGetTableStats:
 *      Print some reorg statistics for a specific table.
 *
 * p_reorgGetStats:
 *      Print some reorg statistics.
 *      These statistics can be used as base to see whether a reorg is 
 *      necessary or not.
 *
 * p_reorgCreateTable:
 *      Create one reorg table for a given table.
 *      This procedure checks whether the reorg table for the given table 
 *      already exists. If it doesn't the table is created with the same 
 *      scheme as the original table.
 *
 * p_reorgTable:
 *      Perform the reorganization for one table.
 *      This procedure does everything which is necessary to reorganize one of
 *      the tables.
 *
 * p_reorgRights:
 *      Perform the reorganization of the rights.
 *      This procedure does everything which is necessary to reorganize the
 *      rights.
 *
 * p_reorg:
 *      Perform the reorganization.
 *      This procedure is the center part of the reorganization. It performs
 *      the reorg for the defined tables and gives the user the required
 *      feedback.
 *
 *
 * Standard sequence of procedure calls:
 * 0. GRANT CREATE TABLE TO sa (necessary precondition)
 * 1. p_reorgGetStats to check whether a reorg has to be performed and how
 *      much data will be moved.
 * 2. p_reorg to perform the reorg.
 * 3. p_reorgGetStats to check whether the reorg has been performed properly.
 */

CREATE OR REPLACE PROCEDURE p_showMessage
(
    ai_message              VARCHAR2,       -- the message to be printed
    ai_emptyLine            NUMBER          -- shall an empty line be printed?
)
AS
-- body:
BEGIN
    -- set buffer size for output:
    DBMS_OUTPUT.ENABLE (100000);

    debug (ai_message);

    IF (ai_emptyLine = 1)
    THEN
        DBMS_OUTPUT.NEW_LINE;
    END IF; 
END p_showMessage;
/
show errors;


CREATE OR REPLACE PROCEDURE p_showActDateTime
(
    ai_message              VARCHAR2,       -- the message to be printed
    ai_emptyLine            NUMBER          -- shall an empty line be printed?
)
AS
    -- local variables:
    l_message               VARCHAR2 (8000); -- the complete message string

-- body:
BEGIN   
    -- initializations:
    l_message := RPAD (ai_message, 50, ' ') || ' ' ||
        TO_CHAR (sysdate, 'DD.MM.YYYY - HH24:MI:SS');

    -- show the computed message:
    p_showMessage (l_message, ai_emptyLine);
END p_showActDateTime;
/
show errors;



CREATE OR REPLACE PROCEDURE p_showNumberMessage
(
    ai_text                 VARCHAR2,       -- the output message
    ai_number               NUMBER,         -- the output number
    ai_emptyLine            NUMBER          -- shall an empty line be printed?
)
AS
    -- local variables:
    l_message               VARCHAR2 (8000); -- the complete message string

-- body:
BEGIN   
    -- compute the output string:
    l_message :=
        RPAD (ai_text, 35, ' ') || ' ' || TO_CHAR (ai_number, '9999999');

    -- show the computed message:
    p_showMessage (l_message, ai_emptyLine);
END p_showNumberMessage;
/
show errors;



-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_getArchiveTableName
(
    -- input parameters:
    ai_tableName            VARCHAR2,       -- name of original table
    -- output parameters:
    ao_archiveTableName     OUT VARCHAR2    -- name of archive table
)
AS
-- body:
BEGIN
    ao_archiveTableName := SUBSTR ('ar_' || ai_tableName, 0, 30);
END p_getArchiveTableName;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorgGetTableStats
(
    ai_table                VARCHAR2,       -- name of table for which to get
                                            -- the statistics
    ai_condition            VARCHAR2        -- condition for used objects
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_message               VARCHAR2 (8000); -- message to be printed
    l_tableString           VARCHAR2 (63);  -- table name for display

    -- exceptions:
    e_statementExecError    EXCEPTION;

-- body:
BEGIN
    l_tableString := ai_table || ' tuples: ';
    l_tableString := RPAD (l_tableString, 35, ' ');

    -- create the tab:
    l_cmdString :=
        ' DECLARE' ||
            ' l_rowCount INTEGER;' ||   -- the result
        ' BEGIN ' ||
            -- get number of tuples in the table:
            ' SELECT  COUNT (*)' ||
            ' INTO    l_rowCount' ||
            ' FROM    ' || ai_table || ';';

    -- check if there is a condition:
    IF (ai_condition IS NOT NULL AND '#' || ai_condition || '#' <> '##')
                                        -- condition defined?
    THEN
        l_cmdString := l_cmdString ||
            ' p_showNumberMessage (''' || l_tableString || ''', l_rowCount, 0);' ||
            -- get number of used tuples in the table:
            ' SELECT  COUNT (*)' ||
            ' INTO    l_rowCount' ||
            ' FROM    ' || ai_table ||
            ' WHERE   ' || ai_condition || ';' ||
            ' p_showNumberMessage (''.   Used tuples:'', l_rowCount, 0);' ||

            -- get number of unused tuples in the table:
            ' SELECT  COUNT (*)' ||
            ' INTO    l_rowCount' ||
            ' FROM    ' || ai_table ||
            ' WHERE   NOT (' || ai_condition || ');' ||
            ' p_showNumberMessage (''.   Not used tuples:'', l_rowCount, 1);';
    -- end if condition defined
    ELSE                                -- no condition defined
        l_cmdString := l_cmdString ||
            ' p_showNumberMessage (' || l_tableString || ', l_rowCount, 1);';
    END IF; -- else no condition defined

    l_cmdString := l_cmdString ||
        ' END;';

--p_showMessage (ai_table, 0);
--p_showMessage (ai_condition, 0);
    -- execute the query:
    l_retValue := p_execQuery
        (l_cmdString, 'Error in getting table data ' || ai_table, l_rowCount);

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; ai_table = ' || ai_table || CHR (13) ||
            ', ai_condition = ' || ai_condition || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorgGetTableStats', l_eText);
        p_showMessage ('p_reorgGetTableStats: ' || CHR (13) || l_eText, 0);
END p_reorgGetTableStats;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorgCreateTable
(
    ai_table                all_tables.table_name%TYPE
                                            -- name of table for which to create 
                                            -- reorg table
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_archiveTable          all_tables.table_name%TYPE;
                                            -- the name of the archive table
    l_message               VARCHAR2 (8000); -- message to be printed
    l_columnName            all_tab_columns.column_name%TYPE;
                                            -- the referenced table column
    l_rowsProcessed         INTEGER := 0;   -- number of rows processed in query

-- body:
BEGIN
    -- get the name of the archive table:
    p_getArchiveTableName (ai_table, l_archiveTable);

--debug ('archive table: ' || l_archiveTable);
    BEGIN
        -- check if the table does not exist yet:
        SELECT  COUNT (*)
        INTO    l_rowCount
        FROM    all_tables
        WHERE   UPPER (table_name) = UPPER (l_archiveTable);
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check if archive table exists';
            RAISE;                      -- call common exception handler
    END;

    IF (l_rowCount = 0)                 -- table does not exist yet?
    THEN
--debug ('.   create table');
        BEGIN
            -- get the column name:
            SELECT  MIN (column_name)
            INTO    l_columnName
            FROM    all_tab_columns
            WHERE   UPPER (table_name) = UPPER (ai_table)
                AND DATA_TYPE NOT IN
                    ('CLOB', 'NCLOB', 'LONG', 'BLOB', 'BFILE', 'MLSLABEL');
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get column name';
                RAISE;                  -- call common exception handler
        END;

        l_message := '    Creating archive table ' || l_archiveTable || '...';
--        p_showMessage (l_message, 0);

        -- create the reorg table with the same scheme as the original table:
        -- to ensure that there is no element in the table the first column
        -- must be greater than the maximum value within this column
        -- => so the result set is empty and has the correct scheme
        l_cmdString :=
            'CREATE TABLE ' || l_archiveTable || CHR (13) ||
            ' PARALLEL (DEGREE 3)' || CHR (13) ||
            ' AS SELECT *' || CHR (13) ||
            ' FROM  ' || ai_table || CHR (13) ||
            ' WHERE ' || l_columnName || ' >' || CHR (13) ||
            '       (SELECT MAX (' || l_columnName || ')' || CHR (13) ||
            '       FROM ' || ai_table || ')';
--debug (l_columnName);
--debug (l_cmdString);

        -- execute the query:
        l_retValue :=
            p_execQuery (l_cmdString, 'Error in creating table', l_rowCount);

        -- check if there occurred an error:
        IF (l_retValue = c_ALL_RIGHT)   -- everything o.k.?
        THEN
            l_message := l_message || ' table created.';
            p_showMessage (l_message, 0);
        END IF; -- if everything o.k.
--ELSE
--debug ('.   table exists already');
    END IF; -- if table does not exist yet

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; ai_table = ' || ai_table || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorgCreateTable', l_eText);
        p_showMessage ('p_reorgCreateTable: ' || CHR (13) || l_eText, 0);
END p_reorgCreateTable;
/
show errors;


BEGIN
    -- ensure that the most important archive tables exist:
    p_reorgCreateTable ('ibs_Object');
    p_reorgCreateTable ('ibs_RightsKeys');
    p_reorgCreateTable ('ibs_RightsCum');
END;
/

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_reorgTable
(
    ai_table                all_tables.table_name%TYPE,
                                            -- name of table to be reorganized
    ai_condition            VARCHAR2        -- condition for archiving
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
    l_rowCount              INTEGER;        -- number of rows
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_archiveTable          all_tables.table_name%TYPE;
                                            -- the name of the archive table
    l_message               VARCHAR2 (8000); -- message to be printed
    l_rowsProcessed         INTEGER := 0;   -- number of rows processed in query

-- body:
BEGIN
    p_getArchiveTableName (ai_table, l_archiveTable);

    l_message := 'Reorganizing table ' || ai_table || '...';
    p_showMessage (l_message, 0);

    -- ensure that the archive table exists:
    p_reorgCreateTable (ai_table);

--    BEGIN TRANSACTION                   -- begin new TRANSACTION
        -- put not used tuples into archive table:
        l_cmdString :=
            'INSERT INTO ' || l_archiveTable || CHR (13) ||
            ' SELECT *' || CHR (13) ||
            ' FROM   ' || ai_table || CHR (13) ||
            ' WHERE ' || ai_condition;

        -- execute the query:
        l_retValue := p_execQuery (l_cmdString, 'insert tuples', l_rowCount);

        -- check if there occurred an error:
        IF (l_retValue = c_ALL_RIGHT)   -- everything o.k.?
        THEN
            -- delete the moved tuples:
            l_cmdString :=
                'DELETE ' || ai_table ||
                ' WHERE ' || ai_condition;

            -- execute the query:
            l_retValue :=
                p_execQuery (l_cmdString, 'delete tuples', l_rowCount);
        END IF; -- everything o.k.

    -- finish the transaction:
    IF (l_retValue <> c_ALL_RIGHT)
                                        -- there occurred a severe error?
    THEN
        ROLLBACK;                       -- undo changes

        l_message := '.   Archiving could not be performed at';
    -- end if there occurred a severe error
    ELSE                                -- there occurred no error
        COMMIT WORK;                    -- make changes permanent

        l_message := '.   Archiving of ' || l_rowCount || ' tuples done at';
    END IF; -- else there occurred no error

    -- display the message:
    p_showActDateTime (l_message, 1);

    -- return the state value:
    RETURN l_retValue;

EXCEPTION                               -- an error occurred
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;                       -- undo changes
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; ai_table = ' || ai_table || CHR (13) ||
            ', ai_condition = ' || ai_condition || CHR (13) ||
            ', l_archiveTable = ' || l_archiveTable || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorgTable', l_eText);
debug ('p_reorgTable');
debug ('error: ' || SQLERRM);
debug ('condition: ' || ai_condition);
--debug ('command string: ' || l_cmdString);
DBMS_OUTPUT.PUT ('p_reorgTable: ' || CHR (13) || l_eText);
DBMS_OUTPUT.NEW_LINE ();
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_reorgTable;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorgMultipleOperation
(
    ai_operationKind        INTEGER
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OP_CREATETABLE        INTEGER := 1;   -- create archive table
    c_OP_GETSTATS           INTEGER := 2;   -- get statistics
    c_OP_REORG              INTEGER := 3;   -- perform reorg.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- number of rows
    l_message               VARCHAR2 (8000); -- message to be printed
    l_tableName             all_tables.table_name%TYPE;
                                            -- name of actual table

    -- declare cursor:
    -- define cursor for running through all tables:
    CURSOR tableCursor IS
        SELECT  table_name AS name
        FROM    all_tables
        WHERE   (
                    LOWER (table_name) LIKE 'ibs_%'
                OR  LOWER (table_name) LIKE 'm2_%'
                OR  LOWER (table_name) LIKE 'dbm_%'
                OR  LOWER (table_name) LIKE 'mad_%'
                )
            AND LOWER (table_name) NOT IN
                (
                    -- system tables, helpers:
                    'ibs_system', 'ibs_counter',
                    'ibs_db_errors', 'ibs_db_errors2',
                    -- multilingual texts:
                    'ibs_exception_01', 'ibs_message_01', 'ibs_objectdesc_01',
                    'ibs_token_01', 'ibs_typename_01',
                    -- rights:
                    'ibs_operation', 'ibs_rightscum', 'ibs_rightskeys',
                    'ibs_rightsmapping',
                    -- types:
                    'ibs_consistsof', 'ibs_tab', 'ibs_maycontain',
                    'ibs_tversion', 'ibs_tversionproc', 'ibs_type',
                    -- ibs_Object:
                    'ibs_object', 'ibs_copy', 'ibs_protocol_01',
                    'ibs_reference', 'ibs_keymapper', 'ibs_keymapperarchive',
                    -- user management:
                    'ibs_groupuser', 'ibs_workspace',
                    -- domains:
                    'ibs_domain_01', 'ibs_domainscheme_01',
                    -- workflow:
                    'ibs_workflowprotocol', 'ibs_workflowvariables',
                    -- product:
                    'm2_catalogpayments', 'm2_pricecodevalues_01',
                    'm2_productcodevalues_01', 'm2_productcollectionqty_01',
                    'm2_productcollectionvalue_01', 'm2_profilecategory_01'
                )
            AND LOWER (table_name) NOT LIKE '%_archive'
            AND (
                    (   LENGTH (table_name) = 30
                    AND LOWER (table_name) NOT LIKE '%_archiv'
                    AND LOWER (table_name) NOT LIKE '%_archi'
                    AND LOWER (table_name) NOT LIKE '%_arch'
                    AND LOWER (table_name) NOT LIKE '%_arc'
                    AND LOWER (table_name) NOT LIKE '%_ar'
                    AND LOWER (table_name) NOT LIKE '%_a'
                    )
                OR  LENGTH (table_name) <> 30
                )
        ORDER BY name ASC;

    l_cursorRow             tableCursor%ROWTYPE;

-- body:
BEGIN
    -- loop through the cursor rows:
    FOR l_cursorRow IN tableCursor      -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_tableName := l_cursorRow.name;

        -- perform the operation:
        IF (ai_operationKind = c_OP_CREATETABLE)
        THEN
            -- create reorg table:
            p_reorgCreateTable (l_tableName);
        -- end if
        ELSIF (ai_operationKind = c_OP_GETSTATS)
        THEN
            -- get reorg statistics for actual table:
            p_reorgGetTableStats (l_tableName,
                'oid IN (SELECT oid FROM ibs_Object WHERE state = 2)');
        -- end elsif
        ELSIF (ai_operationKind = c_OP_REORG)
        THEN
            -- perform reorganisation for actual table:
            l_retValue := p_reorgTable (l_tableName,
                'oid NOT IN (SELECT oid FROM ibs_Object)');
        -- end elsif
        ELSE
            l_message := 'Operation ' || ai_operationKind || ' unknown!';
            p_showMessage (l_message, 1);
        END IF; -- else
    END LOOP; -- while another tuple found

    -- special tables:

EXCEPTION                               -- an error occurred
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; ai_operationKind = ' || ai_operationKind || CHR (13) ||
            ', l_tableName = ' || l_tableName || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorgMultipleOperation', l_eText);
        p_showMessage ('p_reorgMultipleOperation: ' || CHR (13) || l_eText, 0);
END p_reorgMultipleOperation;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorgGetStats
(
    ai_maxRKey              ibs_RightsKeys.id%TYPE
)
AS
    -- constants:
    c_OP_CREATETABLE        INTEGER := 1;   -- create archive table
    c_OP_GETSTATS           INTEGER := 2;   -- get statistics
    c_OP_REORG              INTEGER := 3;   -- perform reorg.

    -- local variables:
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- number of rows
    l_message               VARCHAR2 (8000); -- message to be printed
    l_dbname                VARCHAR2 (255); -- the name of the actual database
    l_maxId                 ibs_RightsKeys.id%TYPE; -- the maximum id

-- body:
BEGIN
    -- get the name of the current database:
    SELECT  DB_NAME
    INTO    l_dbname
    FROM    SYS.DUAL;

    l_message := 'Getting statistics for database ' || l_dbname || '...';
    p_showMessage (l_message, 1);

    -- get number of all objects
    SELECT  COUNT (*)
    INTO    l_rowCount
    FROM    ibs_Object;
    p_showNumberMessage ('all objects:', l_rowCount, 0);

    -- get number of unused objects
    SELECT  COUNT (*)
    INTO    l_rowCount
    FROM    ibs_Object
    WHERE   state <> 2;
    p_showNumberMessage ('.   not used objects:', l_rowCount, 1);


    -- get maximum id of rights keys:
    SELECT  MAX (id)
    INTO    l_maxId
    FROM    ibs_RightsKeys;
    IF (ai_maxRKey > 0 AND ai_maxRKey < l_maxId)
    THEN
        l_maxId := ai_maxRKey;
    END IF;

    -- get number of tuples in ibs_RightsKeys
    SELECT  COUNT (*)
    INTO    l_rowCount
    FROM    ibs_RightsKeys
    WHERE   id < l_maxId;
    p_showNumberMessage ('rights keys tuples:', l_rowCount, 0);

    -- get number of used tuples in ibs_RightsKeys
    SELECT  COUNT (*)
    INTO    l_rowCount
    FROM    ibs_RightsKeys
    WHERE   id < l_maxId
        AND id IN (SELECT rkey FROM ibs_Object WHERE state = 2);
    p_showNumberMessage ('.   used rights keys tuples:', l_rowCount, 1);


    -- get number of tuples in ibs_RightsCum
    SELECT  COUNT (*)
    INTO    l_rowCount
    FROM    ibs_RightsCum
    WHERE   rKey < l_maxId;
    p_showNumberMessage ('rights cum tuples:', l_rowCount, 0);

    -- get number of used tuples in ibs_RightsCum
    SELECT  COUNT (*)
    INTO    l_rowCount
    FROM    ibs_RightsCum
    WHERE   rKey < l_maxId
        AND rkey IN (SELECT rkey FROM ibs_Object WHERE state = 2);
    p_showNumberMessage ('.   used rights cum tuples:', l_rowCount, 1);


    -- get the statistics of all common tables:
    p_reorgMultipleOperation (c_OP_GETSTATS);


    -- get statistics of special tables:
    p_reorgGetTableStats ('ibs_GroupUser',
        'userId IN (SELECT u.id FROM ibs_User u, ibs_Object o WHERE u.oid = o.oid AND o.state = 2 UNION SELECT g.id FROM ibs_Group g, ibs_Object o WHERE g.oid = o.oid AND o.state = 2)');

    p_reorgGetTableStats ('ibs_Workspace',
        'userId IN (SELECT u.id FROM ibs_User u, ibs_Object o WHERE u.oid = o.oid AND o.state = 2) AND workspace IN (SELECT oid FROM ibs_Object WHERE state = 2)');

    p_reorgGetTableStats ('ibs_Reference',
        'referencingOid IN (SELECT oid FROM ibs_Object WHERE state = 2) AND referencedOid IN (SELECT oid FROM ibs_Object WHERE state = 2)');

    -- ibs_Type: nothing to do

    p_reorgGetTableStats ('ibs_TVersion',
        'typeId IN (SELECT id FROM ibs_Type)');

    p_reorgGetTableStats ('ibs_TVersionProc',
        'tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type))');

    p_reorgGetTableStats ('ibs_MayContain',
        'majorTypeId IN (SELECT id FROM ibs_Type) AND minorTypeId IN (SELECT id FROM ibs_Type)');

    -- ibs_Tab: nothing to do

    p_reorgGetTableStats ('ibs_ConsistsOf',
        'tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type)) AND tabId IN (SELECT id FROM ibs_Tab)');

    p_reorgGetTableStats ('ibs_KeyMapper',
        'oid IN (SELECT oid FROM ibs_Object WHERE state = 2)');

    p_reorgGetTableStats ('ibs_KeyMapperArchive',
        'oid IN (SELECT oid FROM ibs_Object WHERE state = 2)');

-- not finished:
/*
    p_reorgGetTableStats ('ibs_Protocol_01',
        '');

    p_reorgGetTableStats ('ibs_Domain_01',
        '');

    p_reorgGetTableStats ('ibs_DomainScheme_01',
        '');

    p_reorgGetTableStats ('ibs_WorkflowProtocol',
        '');

    p_reorgGetTableStats ('ibs_WorkflowVariables',
        '');

    p_reorgGetTableStats ('m2_CatalogPayments',
        '');

    p_reorgGetTableStats ('m2_PriceCodeValues_01',
        '');

    p_reorgGetTableStats ('m2_ProductCodeValues_01',
        '');

    p_reorgGetTableStats ('m2_ProductCollectionQty_01',
        '');

    p_reorgGetTableStats ('m2_ProductCollectionValue_01',
        '');

    p_reorgGetTableStats ('m2_ProfileCategory_01',
        '');
*/
END p_reorgGetStats;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorgRights
(
    ai_maxRKey              ibs_RightsKeys.id%TYPE DEFAULT 0,
    ai_step                 ibs_RightsKeys.id%TYPE DEFAULT 100
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OP_CREATETABLE        INTEGER := 1;   -- create archive table
    c_OP_GETSTATS           INTEGER := 2;   -- get statistics
    c_OP_REORG              INTEGER := 3;   -- perform reorg.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- number of rows
    l_message               VARCHAR2 (8000); -- message to be printed
    l_name                  all_tables.table_name%TYPE;
                                            -- name of actual table
    l_maxId                 ibs_RightsKeys.id%TYPE := 0; -- the maximum id
    l_step                  ibs_RightsKeys.id%TYPE; -- step for loops
    l_i                     INTEGER := 0;   -- counter

-- body:
BEGIN
    -- tables ibs_RightsKeys and ibs_RightsCum:
    BEGIN
        -- get the maximum id:
        SELECT  MAX (id)
        INTO    l_maxId
        FROM    ibs_RightsKeys;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- no rights keys defined?
            -- set lowest possible key id:
            l_maxId := 0;
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get maximum rights key';
            RAISE;                      -- call common exception handler
    END;

    -- check if the input value is less than the maximum id:
    IF (ai_maxRKey > 0 AND ai_maxRKey < l_maxId)
    THEN
        -- set the maximum id for reorg:
        l_maxId := ai_maxRKey;
    END IF;

    -- print message:
    l_message := 'Reorganizing ibs_RightsKeys and ibs_RightsCum (' ||
            l_maxId || ' keys)...';
    p_showMessage (l_message, 0);

    -- ensure that the archive tables exist:
    p_reorgCreateTable ('ibs_RightsKeys');
    p_reorgCreateTable ('ibs_RightsCum');

    -- loop through all tuples of the table in predefined steps:
    -- (this is necessary to ensure that the amount of data stays small)
    l_i := 0;
    WHILE (l_i < l_maxId)
    LOOP
--        BEGIN TRANSACTION
            BEGIN
                -- put not used tuples into the archive table:
                INSERT  INTO ar_ibs_RightsKeys
                SELECT  *
                FROM    ibs_RightsKeys
                WHERE   id >= l_i
                    AND id < (l_i + ai_step)
                    AND id NOT IN ( SELECT  rkey 
                                    FROM    ibs_Object 
                                    WHERE   rKey >= l_i
                                        AND rKey < (l_i + ai_step))
                    AND id < l_maxId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'archive not used RightsKeys tuples';
                    RAISE;              -- call common exception handler
            END;

            BEGIN
                -- delete the moved tuples:
                DELETE  ibs_RightsKeys
                WHERE   id IN (SELECT id FROM ar_ibs_RightsKeys);
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'delete archived RightsKeys tuples';
                    RAISE;              -- call common exception handler
            END;
        COMMIT WORK;                    -- make changes permanent

        l_message :=
            '.   ibs_RightsKeys upto ' || (l_i + ai_step - 1) || ' done.';
        p_showMessage (l_message, 0);

        -- table ibs_RightsCum:
--        BEGIN TRANSACTION
            BEGIN
                -- put not used tuples into the archive table:
                INSERT  INTO ar_ibs_RightsCum
                SELECT  *
                FROM    ibs_RightsCum
                WHERE   rkey >= l_i
                    AND rkey < (l_i + ai_step)
                    AND rkey NOT IN (SELECT id FROM ibs_RightsKeys);
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'archive not used RightsCum tuples';
                    RAISE;              -- call common exception handler
            END;

            BEGIN
                -- delete the moved tuples:
                DELETE  ibs_RightsCum
                WHERE   rkey >= l_i
                    AND rkey < (l_i + ai_step)
                    AND rkey NOT IN (SELECT id FROM ibs_RightsKeys);
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'delete archived RightsCum tuples';
                    RAISE;              -- call common exception handler
            END;
        COMMIT WORK;                    -- make changes permanent

        l_message :=
            '.   ibs_RightsCum upto ' || (l_i + ai_step - 1) || ' done.';
        p_showMessage (l_message, 0);

        -- compute index for next step:
        l_i := l_i + ai_step;
    END LOOP; -- while

    -- recumulate all rights
    p_showActDateTime ('Recumulating all rights...', 0);
--    BEGIN TRANSACTION
        p_Rights$updateRightsCum ();
    COMMIT WORK;                        -- make changes permanent

    p_showActDateTime ('ibs_RightsKeys and ibs_RightsCum done at', 1);

EXCEPTION                               -- an error occurred
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; ai_maxRKey = ' || ai_maxRKey || CHR (13) ||
            ', ai_step = ' || ai_step || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorgRights', l_eText);
        p_showMessage ('p_reorgRights: ' || CHR (13) || l_eText, 0);
END p_reorgRights;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorgDeleteWorkspaces
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OP_CREATETABLE        INTEGER := 1;   -- create archive table
    c_OP_GETSTATS           INTEGER := 2;   -- get statistics
    c_OP_REORG              INTEGER := 3;   -- perform reorg.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- number of rows
    l_message               VARCHAR2 (8000); -- message to be printed
    l_oid                   ibs_Object.oid%TYPE; -- oid of actual object
    l_userId                ibs_User.id%TYPE; -- id of user for workspace
    l_posNoPath             ibs_Object.posNoPath%TYPE;
                                        -- posNoPath of actual object

    -- declare cursor:
    -- define cursor for running through all tabs:
    CURSOR wspCursor IS
        SELECT  w.workspace AS oid, w.userId AS userId, o.posNoPath AS posNoPath
        FROM    ibs_Workspace w,
                (   SELECT  *   FROM ibs_Object
                    UNION ALL
                    SELECT  *   FROM ar_ibs_Object
                ) o
        WHERE   w.workspace = o.oid
            AND (   w.userId NOT IN (SELECT id FROM ibs_User)
                OR  w.workspace NOT IN
                    (   SELECT  oid
                        FROM    ibs_Object
                        WHERE   state = 2
                    )
                );

    l_cursorRow             wspCursor%ROWTYPE;

-- body:
BEGIN
    -- loop through the cursor rows:
    FOR l_cursorRow IN wspCursor        -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_oid := l_cursorRow.oid;
        l_userId := l_cursorRow.userId;
        l_posNoPath := l_cursorRow.posNoPath;

        -- perform the operation:
        BEGIN
            -- delete all objects below the workspace:
            UPDATE  ibs_Object
            SET     state = 1
            WHERE   posNoPath LIKE l_posNoPath || '%';
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'setting object states to 1 below ' || l_oid;
                RAISE;                  -- call common exception handler
        END;
    END LOOP; -- while another tuple found

EXCEPTION                               -- an error occurred
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; l_oid = ' || l_oid || CHR (13) ||
            ', l_userId = ' || l_userId || CHR (13) ||
            ', l_posNoPath = ' || l_posNoPath || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorgDeleteWorkspaces', l_eText);
        p_showMessage ('p_reorgDeleteWorkspaces: ' || CHR (13) || l_eText, 0);
END p_reorgDeleteWorkspaces;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorgWorkspaces
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OP_CREATETABLE        INTEGER := 1;   -- create archive table
    c_OP_GETSTATS           INTEGER := 2;   -- get statistics
    c_OP_REORG              INTEGER := 3;   -- perform reorg.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- number of rows
    l_message               VARCHAR2 (8000); -- message to be printed
    l_oid                   ibs_Object.oid%TYPE; -- oid of actual object
    l_userId                ibs_User.id%TYPE; -- id of user for workspace
    l_posNoPath             ibs_Object.posNoPath%TYPE;
                                        -- posNoPath of actual object

    -- declare cursor:
    -- define cursor for running through all tabs:
    CURSOR wspCursor IS
        SELECT  w.workspace AS oid, w.userId AS userId, o.posNoPath AS posNoPath
        FROM    ibs_Workspace w,
                (   SELECT  *   FROM ibs_Object
                    UNION ALL
                    SELECT  *   FROM ar_ibs_Object
                ) o
        WHERE   w.workspace = o.oid
            AND (   w.userId NOT IN (SELECT id FROM ibs_User)
                OR  w.workspace NOT IN
                    (   SELECT  oid
                        FROM    ibs_Object
                    )
                );

    l_cursorRow             wspCursor%ROWTYPE;

-- body:
BEGIN
    -- loop through the cursor rows:
    FOR l_cursorRow IN wspCursor        -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_oid := l_cursorRow.oid;
        l_userId := l_cursorRow.userId;
        l_posNoPath := l_cursorRow.posNoPath;

        -- perform the operation:
        BEGIN
            -- delete all objects below the workspace:
            DELETE  ibs_Object
            WHERE   posNoPath LIKE l_posNoPath || '%'
                AND state <> 2
                AND oid != l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'deleting objects below ' || l_oid;
                RAISE;                  -- call common exception handler
        END;
    END LOOP; -- while another tuple found

    -- perform reorganisation for actual table:
    l_retValue := p_reorgTable ('ibs_Workspace',
        'userId NOT IN (SELECT id FROM ibs_User) OR workspace NOT IN (SELECT oid FROM ibs_Object)');

EXCEPTION                               -- an error occurred
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; l_oid = ' || l_oid || CHR (13) ||
            ', l_userId = ' || l_userId || CHR (13) ||
            ', l_posNoPath = ' || l_posNoPath || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorgWorkspaces', l_eText);
        p_showMessage ('p_reorgWorkspaces: ' || CHR (13) || l_eText, 0);
END p_reorgWorkspaces;
/
show errors;


-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_reorg
(
    ai_maxRKey              ibs_RightsKeys.id%TYPE DEFAULT 0
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OP_CREATETABLE        INTEGER := 1;   -- create archive table
    c_OP_GETSTATS           INTEGER := 2;   -- get statistics
    c_OP_REORG              INTEGER := 3;   -- perform reorg.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- number of rows
    l_message               VARCHAR2 (8000); -- message to be printed
    l_dbname                VARCHAR2 (255); -- the name of the actual database
    l_maxId                 ibs_Object.id%TYPE; -- the maximum id

-- body:
BEGIN
    -- get the name of the current database:
    SELECT  DB_NAME
    INTO    l_dbname
    FROM    SYS.DUAL;

    -- show actual date + time:
    l_message := 'Starting reorganisation for database ' || l_dbname || '...';
    p_showActDateTime (l_message, 1);

    -- ensure that the archive table exists:
    p_reorgCreateTable ('ibs_Object');

    -- ensure that all objects within unused workspaces are marked as deleted:
    p_reorgDeleteWorkspaces ();

    -- table ibs_Object:
    l_message := 'Reorganizing ibs_Object...';
    p_showMessage (l_message, 0);

    BEGIN
        -- get the maximum id:
        SELECT  MAX (id)
        INTO    l_maxId
        FROM    ibs_Object;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- no object defined?
            -- set lowest possible id:
            l_maxId := 0;
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get maximum object id';
            RAISE;                      -- call common exception handler
    END;
   
--    BEGIN TRANSACTION
        BEGIN
            -- put not used tuples into the archive table:
            -- (the tuples must be older than one day)
            INSERT  INTO ar_ibs_Object
            SELECT  *
            FROM    ibs_Object
            WHERE   state <> 2
                AND lastChanged < SYSDATE - 1
                AND id < l_maxId;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'insert tuples';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            -- delete the moved tuples:
            DELETE  ibs_Object
            WHERE   oid IN (SELECT oid FROM ar_ibs_Object);

            -- get the number of deleted tuples:
            l_rowCount := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'delete tuples';
                RAISE;                  -- call common exception handler
        END;

    COMMIT WORK;                        -- make changes permanent

    l_message :=
        '.   Archiving of ' || l_rowCount || ' tuples from ibs_Object done at';
    p_showActDateTime (l_message, 1);

    -- tables ibs_RightsKeys and ibs_RightsCum:
    p_reorgRights (ai_maxRKey);

    -- common tables:
    p_reorgMultipleOperation (c_OP_REORG);

    -- special tables:
    p_reorgWorkspaces ();

    l_retValue := p_reorgTable ('ibs_GroupUser',
        'userId NOT IN (SELECT id FROM ibs_User UNION ALL SELECT id FROM ibs_Group)');

    l_retValue := p_reorgTable ('ibs_Reference',
        'referencingOid NOT IN (SELECT oid FROM ibs_Object) OR referencedOid NOT IN (SELECT oid FROM ibs_Object)');

    -- ibs_Type: nothing to do

    l_retValue := p_reorgTable ('ibs_TVersion',
        'NOT (typeId IN (SELECT id FROM ibs_Type))');

    l_retValue := p_reorgTable ('ibs_TVersionProc',
        'NOT (tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type)))');

    l_retValue := p_reorgTable ('ibs_MayContain',
        'NOT (majorTypeId IN (SELECT id FROM ibs_Type) AND minorTypeId IN (SELECT id FROM ibs_Type))');

    -- ibs_Tab: nothing to do

    l_retValue := p_reorgTable ('ibs_ConsistsOf',
        'NOT (tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type)) AND tabId IN (SELECT id FROM ibs_Tab))');

    l_retValue := p_reorgTable ('ibs_KeyMapper',
        'NOT (oid IN (SELECT oid FROM ibs_Object))');

    -- ibs_KeyMapperArchive: nothing to do
--    l_retValue := p_reorgTable ('ibs_KeyMapperArchive',
--        'NOT (oid IN (SELECT oid FROM ibs_Object))');

-- not finished:
/*
    l_retValue := p_reorgTable ('ibs_Protocol_01',
        '');

    l_retValue := p_reorgTable ('ibs_Domain_01',
        '');

    l_retValue := p_reorgTable ('ibs_DomainScheme_01',
        '');

    l_retValue := p_reorgTable ('ibs_WorkflowProtocol',
        '');

    l_retValue := p_reorgTable ('ibs_WorkflowVariables',
        '');

    l_retValue := p_reorgTable ('m2_CatalogPayments',
        '');

    l_retValue := p_reorgTable ('m2_PriceCodeValues_01',
        '');

    l_retValue := p_reorgTable ('m2_ProductCodeValues_01',
        '');

    l_retValue := p_reorgTable ('m2_ProductCollectionQty_01',
        '');

    l_retValue := p_reorgTable ('m2_ProductCollectionValue_01',
        '');

    l_retValue := p_reorgTable ('m2_ProfileCategory_01',
        '');
*/

    p_showActDateTime ('Reorganising done at', 1);

EXCEPTION                               -- an error occurred
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;                       -- undo changes
        -- create error entry:
        l_eText := l_ePos || CHR (13) ||
            '; ai_maxRKey = ' || ai_maxRKey || CHR (13) ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_reorg', l_eText);
        p_showMessage ('p_reorg: ' || CHR (13) || l_eText, 0);
END p_reorg;
/
show errors;


-- procedure, which initiates the reorganization of the tables
CREATE OR REPLACE PROCEDURE p_reorgStart
AS
-- body:
BEGIN
    p_reorg (0);
END p_reorgStart;
/
show errors;


EXIT;
