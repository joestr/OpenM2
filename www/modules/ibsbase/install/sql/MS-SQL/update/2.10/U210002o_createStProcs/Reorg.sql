/******************************************************************************
 * Procedures required for reorganizing the database. <BR>
 * Reorganizing of a table means to move the not longer needed to an archive
 * table and deleting them from the original table. The archive table is a
 * table with the same name as the original table with 'arc_' prefixed.
 * <BR>
 * In this first version just the tables ibs_Object, ibs_ObjectRead,
 * ibs_RightsKeys, and ibs_RightsCum are considered to be reorganized.
 *
 *
 * @version     $Id: Reorg.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Klaus Reim�ller (KR)  991122
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
 * 1. p_reorgGetStats to check whether a reorg has to be performed and how
 *      much data will be moved.
 * 2. p_reorg to perform the reorg.
 * 3. p_reorgGetStats to check whether the reorg has been performed properly.
 * These calls shall be realised in different query windows of the SQL 
 * Server client.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_showMessage'
GO

-- create the new procedure:
CREATE PROCEDURE p_showMessage
(
    @ai_message         NVARCHAR (4000), -- the message to be printed
    @ai_emptyLine       BOOL = 0,       -- shall an empty line be printed?
    @ai_nowait          BOOL = 1        -- shall the message be printed
                                        -- immediately?
)
AS
    PRINT @ai_message

    IF (@ai_emptyLine = 1)
    BEGIN
        PRINT ''
    END -- if
/*
    IF (@ai_nowait = 1)
        RAISERROR (@ai_message, 1, 2) WITH NOWAIT
    ELSE
        RAISERROR (@ai_message, 1, 2)

    IF (@ai_emptyLine = 1)
    BEGIN
        IF (@ai_nowait = 1)
            RAISERROR ('', 1, 2) WITH NOWAIT
        ELSE
            RAISERROR ('', 1, 2)
    END -- if
*/
GO
-- p_showMessage



-- delete existing procedure:
EXEC p_dropProc N'p_showActDateTime'
GO

-- create the new procedure:
CREATE PROCEDURE p_showActDateTime
(
    @ai_message         NVARCHAR (4000), -- the message to be printed
    @ai_emptyLine       BOOL = 0,       -- shall an empty line be printed?
    @ai_nowait          BOOL = 1        -- shall the message be printed
                                        -- immediately?
)
AS
DECLARE
    -- local variables:
    @l_message              NVARCHAR (4000)  -- the complete message string

-- body:
    -- set the full string through adding the date and time to the message:
    SELECT
        @l_message = CONVERT (NVARCHAR (20), getDate (), 102) + N' ' +
                     CONVERT (NVARCHAR (20), getDate (), 114) + N' ' +
                     @ai_message

    -- show the computed message:
    EXEC p_showMessage @l_message, @ai_emptyLine, @ai_nowait
GO
-- p_showActDateTime



-- delete existing procedure:
EXEC p_dropProc N'p_reorgGetTableStats'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorgGetTableStats
(
    @ai_table               NVARCHAR (255),  -- name of table for which to get
                                             -- the statistics
    @ai_condition           NVARCHAR (4000)  -- condition for used objects
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_message              NVARCHAR (4000), -- message to be printed
    @l_tableString          NVARCHAR (63)    -- table name for display

-- body:
    SELECT  @l_tableString =
            @ai_table + N' tuples: ' + SPACE (35 - LEN (@ai_table))

    EXEC (
        -- get number of tuples in the table:
        'DECLARE @l_message NVARCHAR (4000)' +
        ' SELECT  @l_message = ''' + @l_tableString + ''' + STR (COUNT (*), 7)' +
        ' FROM    ' + @ai_table +
        ' EXEC p_showMessage @l_message, 0, 1' +

        -- get number of used tuples in the table:
        ' SELECT  @l_message = ''    Used tuples:     '' + SPACE (23) +' +
                ' STR (COUNT (*), 7)' +
        ' FROM    ' + @ai_table +
        ' WHERE   ' + @ai_condition +
        ' EXEC p_showMessage @l_message, 0, 1' +

        -- get number of used tuples in ibs_ObjectRead
        ' SELECT  @l_message = ''    Not used tuples: '' + SPACE (23) +' +
                ' STR (COUNT (*), 7)' +
        ' FROM    ' + @ai_table +
        ' WHERE   NOT (' + @ai_condition + ')' +
        ' EXEC p_showMessage @l_message, 1, 1'
    )
GO
-- p_reorgGetTableStats


-- delete existing procedure:
EXEC p_dropProc N'p_reorgCreateTable'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorgCreateTable
(
    @ai_table        NVARCHAR (255),      -- name of table for which to create 
                                          -- reorg table
    @ao_archiveTable NVARCHAR (255) OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,             -- something went wrong
    @c_ALL_RIGHT            INT,             -- everything was o.k.

    -- local variables:
    @l_error                INT,             -- the actual error code
    @l_ePos                 NVARCHAR (2000), -- error position description
    @l_rowCount             INT,             -- row counter
    @l_dbname               NVARCHAR (255),  -- the name of the actual database
    @l_archiveTable         NVARCHAR (255),  -- the name of the archive table
    @l_message              NVARCHAR (4000), -- message to be printed
    @l_colName              NVARCHAR (255),  -- column name
    @l_pos                  INT              -- position within string

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_dbname = DB_NAME (),
    @l_pos = CHARINDEX ('.', @ai_table),
    @ao_archiveTable = SUBSTRING (@ai_table, 1, @l_pos) +
        N'arc_' + SUBSTRING (@ai_table, @l_pos + 1, LEN (@ai_table)),
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- check if the table does not exist yet:
    IF NOT EXISTS (
        SELECT  * 
        FROM    sysobjects 
        WHERE   id IN (object_id (@ao_archiveTable),
                       object_id ('#CONFVAR.ibsbase.dbOwner#.' + @ao_archiveTable))
            AND sysstat & 0xf = 3)
    BEGIN
        SELECT  @l_message =
                N'    Creating archive table ' + @ao_archiveTable + N'...'
--        EXEC p_showMessage @l_message, 0, 1

        -- allow INSERT INTO statements in the actual db:
        EXEC sp_dboption @l_dbname, N'select into/bulkcopy', true
        -- create the reorg table with the same scheme as the original table:
        -- to ensure that there is no element in the table the first column
        -- must be greater than the maximum value within this column
        -- => so the result set is empty and has the correct scheme
--        SELECT  @l_isIdentity = COLUMNPROPERTY (object_id (@ai_table), colName, 'IsIdentity')
        SELECT  @l_colName = COL_NAME (object_id (@ai_table), 1)

        IF (@ai_table = N'ibs_RightsKey')
        BEGIN
            EXEC (
                'CREATE TABLE ' + @ao_archiveTable +
                ' (' +
                    ' id          ID              NOT NULL,' +
                    ' rKeysId     ID              NOT NULL,' +
                    ' owner       USERID          NOT NULL,' +
                    ' oid         OBJECTID        NULL,' +
                    ' cnt         INT             NOT NULL DEFAULT (0)' +
                ' )'
                )
        END -- if
        ELSE
        BEGIN
            EXEC (
                'SELECT  *' +
                ' INTO ' + @ao_archiveTable +
                ' FROM ' + @ai_table +
                ' WHERE ' + @l_colName + ' >' +
                ' (SELECT MAX (' + @l_colName + ')' +
                ' FROM ' + @ai_table + ')'
                )
        END -- else
        SELECT  @l_message = @l_message + N' Table created.'
        EXEC p_showMessage @l_message, 0, 1
    END -- if
GO
-- p_reorgCreateTable


-- delete existing procedure:
EXEC p_dropProc N'p_reorgTable'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorgTable
(
    @ai_table               NVARCHAR (255),  -- name of table to be reorganized
    @ai_condition           NVARCHAR (4000)  -- condition for archiving
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,             -- something went wrong
    @c_ALL_RIGHT            INT,             -- everything was o.k.

    -- local variables:
    @l_retValue             INT,             -- return value of function
    @l_error                INT,             -- the actual error code
    @l_ePos                 NVARCHAR (2000), -- error position description
    @l_rowCount             INT,             -- row counter
    @l_dbname               NVARCHAR (255),  -- the name of the actual database
    @l_archiveTable         NVARCHAR (255),  -- the name of the archive table
    @l_message              NVARCHAR (4000)  -- message to be printed

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_rowCount = 0,
    @l_retValue = @c_ALL_RIGHT,
    @l_dbname = DB_NAME ()

-- body:
    SELECT  @l_message = N'Reorganizing table ' + @ai_table + N'...'
    EXEC p_showMessage @l_message, 0, 1

    -- ensure that the archive table exists:
    EXEC p_reorgCreateTable @ai_table, @l_archiveTable OUTPUT
--    EXEC p_reorgCreateTable @ai_table, @l_archiveTable OUTPUT

    BEGIN TRANSACTION                   -- begin new TRANSACTION
        -- put not used tuples into archive table:
        EXEC (
            'INSERT INTO ' + @l_archiveTable +
            ' SELECT *' +
            ' FROM   ' + @ai_table +
            ' WHERE ' + @ai_condition
        )

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'insert tuples', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- delete the moved tuples:
        EXEC (
            'DELETE ' + @ai_table +
            ' WHERE ' + @ai_condition
        )

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            N'delete tuples', @l_ePos OUTPUT, @l_rowCount OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

    COMMIT TRANSACTION                  -- make changes permanent

    SELECT  @l_message = N'    Archiving of ' + CONVERT (VARCHAR, @l_rowCount) +
            N' tuples done at'
    EXEC p_showActDateTime @l_message, 1, 1

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_reorgTable', @l_error, @l_ePos,
            N'ai_table', @ai_table,
            N'', 0,
            N'ai_condition', @ai_condition,
            N'', 0,
            N'l_archiveTable', @l_archiveTable
    EXEC p_showMessage @l_ePos
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_reorgTable


-- delete existing procedure:
EXEC p_dropProc N'p_reorgMultipleOperation'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorgMultipleOperation
(
    @ai_operationKind       INT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,             -- something went wrong
    @c_ALL_RIGHT            INT,             -- everything was o.k.
    @c_OP_CREATETABLE       INT,             -- create archive table
    @c_OP_GETSTATS          INT,             -- get statistics
    @c_OP_REORG             INT,             -- perform reorganisation

    -- local variables:
    @l_retValue             INT,             -- return value of function
    @l_error                INT,             -- the actual error code
    @l_ePos                 NVARCHAR (2000), -- error position description
    @l_rowCount             INT,             -- row counter
    @l_name                 NVARCHAR (61),   -- name of actual table
    @l_message              NVARCHAR (4000), -- message to be printed
    @l_archiveTable         NVARCHAR (255)   -- archiving table

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OP_CREATETABLE       = 1,
    @c_OP_GETSTATS          = 2,
    @c_OP_REORG             = 3

    -- initialize local variables and return values:
SELECT
    @l_error = 0,
    @l_rowCount = 0,
    @l_retValue = @c_ALL_RIGHT

-- define cursor:
DECLARE tableCursor CURSOR FOR 
    SELECT  u.name + '.' + o.name AS name
    FROM    sysobjects o, sysusers u
    WHERE   o.type = N'U'
        AND o.uid = u.uid
        AND (
                o.name LIKE N'ibs_%'
            OR  o.name LIKE N'm2_%'
            OR  o.name LIKE N'dbm_%'
            OR  o.name LIKE N'mad_%'
            )
        AND o.name NOT IN
            (
                -- system tables, helpers:
                N'ibs_System', N'ibs_Counter', N'ibs_db_errors2',
                N'ibs_Help_01', N'ibs_FunctionHandler',
                -- multilingual texts:
                N'ibs_Exception_01', N'ibs_Message_01', N'ibs_ObjectDesc_01',
                N'ibs_Token_01', N'ibs_TypeName_01',
                -- rights:
                N'ibs_Operation', N'ibs_RightsCum', N'ibs_RightsKeys',
                N'ibs_RightsKey', N'ibs_RightsMapping',
                -- types:
                N'ibs_ConsistsOf', N'ibs_Tab', N'ibs_MayContain', N'ibs_TVersion',
                N'ibs_TVersionProc', N'ibs_Type',
                -- ibs_Object:
                N'ibs_Object', N'ibs_ObjectId', N'ibs_Copy', N'ibs_Protocol_01',
                N'ibs_Reference', N'ibs_KeyMapper', N'ibs_KeyMapperArchive',
                N'ibs_ProtocolEntry_01',
                -- help:
                N'ibs_Help_01',
                -- user management:
                N'ibs_GroupUser', N'ibs_GroupUserCum', N'ibs_Workspace',
                -- domains:
                N'ibs_Domain_01', N'ibs_DomainScheme_01',
                -- workflow:
                N'ibs_WorkflowProtocol', N'ibs_WorkflowVariables',
                -- product:
                N'm2_CatalogPayments', N'm2_PriceCodeValues_01',
                N'm2_ProductCodeValues_01', N'm2_ProductCollectionQty_01',
                N'm2_ProductCollectionValue_01', N'm2_ProfileCategory_01'
            )
        AND o.name NOT LIKE N'arc_%'
        AND o.name NOT LIKE N'%_archive'
    ORDER BY name

-- body:
    -- open the cursor:
    OPEN    tableCursor

    -- get the first object:
    FETCH NEXT FROM tableCursor INTO @l_name

    -- loop through all found objects:
    WHILE (@l_retValue = @c_ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                        -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS can have one of the three values 
        -- -2, -1 or 0 all these three cases must be checked.
        -- Here a tuple is ignored if it was deleted during this
        -- procedure.
        -- When the tuple was successfully gotten the specific code
        -- is executed.
        IF (@@FETCH_STATUS <> -2)       -- tuple was not deleted since opening 
                                        -- cursor?
        BEGIN
            -- perform the operation:
            IF (@ai_operationKind = @c_OP_CREATETABLE)
            BEGIN
                -- create reorg table:
                EXEC p_reorgCreateTable @l_name, @l_archiveTable OUTPUT
            END -- if
            ELSE IF (@ai_operationKind = @c_OP_GETSTATS)
            BEGIN
                -- get reorg statistics for actual table:
                EXEC p_reorgGetTableStats @l_name,
                    N'oid IN (SELECT oid FROM ibs_Object WHERE state = 2)'
            END -- else if
            ELSE IF (@ai_operationKind = @c_OP_REORG)
            BEGIN
                -- perform reorganisation for actual table:
                EXEC @l_retValue = p_reorgTable @l_name,
                    N'oid NOT IN (SELECT oid FROM ibs_Object)'
            END -- else if
            ELSE
            BEGIN
                SELECT  @l_message = N'Operation ' +
                        CONVERT (NVARCHAR (31), @ai_operationKind) + N' unknown!'
                EXEC p_showMessage @l_message, 1, 0

                SELECT  @l_retValue = @c_NOT_OK
            END -- else
        END -- if tuple was not deleted since opening cursor
        -- get next object:
        FETCH NEXT FROM tableCursor INTO @l_name
    END -- while another object found

    -- deallocate cursor:
    DEALLOCATE tableCursor

    -- special tables:

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_reorgMultipleOperation', @l_error, @l_ePos
    EXEC p_showMessage @l_ePos
GO
-- p_reorgMultipleOperation


-- delete existing procedure:
EXEC p_dropProc N'p_reorgGetStats'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorgGetStats
(
    @ai_maxRKey         INT = 0
)
AS
DECLARE
    -- constants:
    @c_OP_CREATETABLE       INT,             -- create archive table
    @c_OP_GETSTATS          INT,             -- get statistics
    @c_OP_REORG             INT,             -- perform reorganisation

    -- local variables:
    @l_message              NVARCHAR (4000), -- message to be printed
    @l_dbname               NVARCHAR (255),  -- the name of the actual database
    @l_maxId                INT              -- the maximum id

    -- assign constants:
SELECT
    @c_OP_CREATETABLE       = 1,
    @c_OP_GETSTATS          = 2,
    @c_OP_REORG             = 3

    -- initialize local variables:
SELECT
    @l_dbname = DB_NAME ()

-- body:
    SELECT  @l_message = N'Getting statistics for database ' + @l_dbname + N'...'
    EXEC p_showMessage @l_message, 1, 1

    -- get number of all objects
    SELECT  @l_message = N'all objects:             ' + STR (COUNT (*), 7)
    FROM    ibs_Object
    EXEC p_showMessage @l_message, 0, 1

    -- get number of unused objects
    SELECT  @l_message = N'not used objects:        ' + STR (COUNT (*), 7)
    FROM    ibs_Object
    WHERE   state <> 2
    EXEC p_showMessage @l_message, 1, 1


    -- get maximum id of rights keys:
    SELECT  @l_maxId = MAX(id)
    FROM    ibs_RightsKey
    IF (@ai_maxRKey > 0 AND @ai_maxRKey < @l_maxId)
        SELECT @l_maxId = @ai_maxRKey

    -- get number of tuples in ibs_RightsKey
    SELECT  @l_message = N'rights key tuples:       ' + STR (COUNT (*), 7)
    FROM    ibs_RightsKey
    WHERE   id < @l_maxId
    EXEC p_showMessage @l_message, 0, 1

    -- get number of used tuples in ibs_RightsKey
    SELECT  @l_message = N'used rights key tuples:  ' + STR (COUNT (*), 7)
    FROM    ibs_RightsKey
    WHERE   id < @l_maxId
        AND id IN (SELECT rKey FROM ibs_Object WHERE state = 2)
    EXEC p_showMessage @l_message, 1, 1


    -- get number of tuples in ibs_RightsKeys
    SELECT  @l_message = N'rights keys tuples:      ' + STR (COUNT (*), 7)
    FROM    ibs_RightsKeys rks, ibs_RightsKey rk
    WHERE   rk.id < @l_maxId
        AND rk.rKeysId = rks.id
    EXEC p_showMessage @l_message, 0, 1

    -- get number of used tuples in ibs_RightsKeys
    SELECT  @l_message = N'used rights keys tuples: ' + STR (COUNT (*), 7)
    FROM    ibs_RightsKeys rks, ibs_RightsKey rk
    WHERE   rk.id < @l_maxId
        AND rk.id IN (SELECT rKey FROM ibs_Object WHERE state = 2)
        AND rk.rKeysId = rks.id
    EXEC p_showMessage @l_message, 1, 1


    -- get number of tuples in ibs_RightsCum
    SELECT  @l_message = N'rights cum tuples:       ' + STR (COUNT (*), 7)
    FROM    ibs_RightsCum
    WHERE   rKey < @l_maxId
    EXEC p_showMessage @l_message, 0, 1

    -- get number of used tuples in ibs_RightsCum
    SELECT  @l_message = N'used rights cum tuples:  ' + STR (COUNT (*), 7)
    FROM    ibs_RightsCum
    WHERE   rKey < @l_maxId
        AND rKey IN (SELECT rKey FROM ibs_Object WHERE state = 2)
    EXEC p_showMessage @l_message, 1, 1


    -- get the statistics of all common tables:
    EXEC p_reorgMultipleOperation @c_OP_GETSTATS


    -- get statistics of special tables:
    EXEC p_reorgGetTableStats N'ibs_GroupUser',
        N'userId IN (SELECT u.id FROM ibs_User u, ibs_Object o WHERE u.oid = o.oid AND o.state = 2 UNION ALL SELECT g.id FROM ibs_Group g, ibs_Object o WHERE g.oid = o.oid AND o.state = 2)'

    EXEC p_reorgGetTableStats N'ibs_Workspace',
        N'userId IN (SELECT u.id FROM ibs_User u, ibs_Object o WHERE u.oid = o.oid AND o.state = 2) AND workspace IN (SELECT oid FROM ibs_Object WHERE state = 2)'

    EXEC p_reorgGetTableStats N'ibs_Reference',
        N'referencingOid IN (SELECT oid FROM ibs_Object WHERE state = 2) AND referencedOid IN (SELECT oid FROM ibs_Object WHERE state = 2)'

    -- ibs_Type: nothing to do

    EXEC p_reorgGetTableStats N'ibs_TVersion',
        N'typeId IN (SELECT id FROM ibs_Type)'

    EXEC p_reorgGetTableStats N'ibs_TVersionProc',
        N'tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type))'

    EXEC p_reorgGetTableStats N'ibs_MayContain',
        N'majorTypeId IN (SELECT id FROM ibs_Type) AND minorTypeId IN (SELECT id FROM ibs_Type)'

    -- ibs_Tab: nothing to do

    EXEC p_reorgGetTableStats N'ibs_ConsistsOf',
        N'tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type)) AND tabId IN (SELECT id FROM ibs_Tab)'

    EXEC p_reorgGetTableStats N'ibs_KeyMapper',
        N'oid IN (SELECT oid FROM ibs_Object WHERE state = 2)'

    EXEC p_reorgGetTableStats N'ibs_KeyMapperArchive',
        N'oid IN (SELECT oid FROM ibs_Object WHERE state = 2)'

-- not finished:
/*
    EXEC p_reorgGetTableStats 'ibs_Protocol_01',
        ''

    EXEC p_reorgGetTableStats 'ibs_Domain_01',
        ''

    EXEC p_reorgGetTableStats 'ibs_DomainScheme_01',
        ''

    EXEC p_reorgGetTableStats 'ibs_WorkflowProtocol',
        ''

    EXEC p_reorgGetTableStats 'ibs_WorkflowVariables',
        ''

    EXEC p_reorgGetTableStats 'm2_CatalogPayments',
        ''

    EXEC p_reorgGetTableStats 'm2_PriceCodeValues_01',
        ''

    EXEC p_reorgGetTableStats 'm2_ProductCodeValues_01',
        ''

    EXEC p_reorgGetTableStats 'm2_ProductCollectionQty_01',
        ''

    EXEC p_reorgGetTableStats 'm2_ProductCollectionValue_01',
        ''

    EXEC p_reorgGetTableStats 'm2_ProfileCategory_01',
        ''
*/
GO
-- p_reorgGetStats


-- delete existing procedure:
EXEC p_dropProc N'p_reorgRights'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorgRights
(
    @ai_maxRKey         INT = 0,
    @ai_step            INT = 100
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_retValue             INT,             -- return value of function
    @l_error                INT,             -- the actual error code
    @l_ePos                 NVARCHAR (2000), -- error position description
    @l_rowCount             INT,             -- row counter
    @l_maxId                ID,              -- the maximum id
    @l_step                 INT,             -- step for loops
    @l_i                    INT,             -- counter
    @l_name                 NVARCHAR (61),   -- name of actual table
    @l_message              NVARCHAR (4000), -- message to be printed
    @l_rightsKeyArcTable    NVARCHAR (255),  -- table for archiving ibs_RightsKey
    @l_rightsKeysArcTable   NVARCHAR (255),  -- table for archiving
                                             -- ibs_RightsKeys
    @l_rightsCumArcTable    NVARCHAR (255)   -- table for archiving ibs_RightsCum

    -- initialize local variables and return values:
SELECT
    @l_error = 0,
    @l_rowCount = 0,
    @l_maxId = 0,
    @l_i = 0

-- body:
    -- tables ibs_RightsKey, ibs_RightsKeys and ibs_RightsCum:
    -- get the maximum id
    SELECT  @l_maxId = MAX (id)
    FROM    ibs_RightsKey
    IF (@ai_maxRKey > 0 AND @ai_maxRKey < @l_maxId)
        SELECT  @l_maxId = @ai_maxRKey

    -- print message:
    SELECT  @l_message =
            N'Reorganizing ibs_RightsKey, ibs_RightsKeys and ibs_RightsCum (' +
            CONVERT (VARCHAR (7), @l_maxId) + N' keys)...'
    EXEC p_showMessage @l_message, 0, 1

    -- ensure that the archive tables exist:
    EXEC p_reorgCreateTable N'ibs_RightsKey', @l_rightsKeyArcTable OUTPUT
    EXEC p_reorgCreateTable N'ibs_RightsKeys', @l_rightsKeysArcTable OUTPUT
    EXEC p_reorgCreateTable N'ibs_RightsCum', @l_rightsCumArcTable OUTPUT

    -- loop through all tuples of the table in predefined steps:
    -- (this is necessary to ensure that the amount of data stays small)
    SELECT  @l_i = 0
    WHILE (@l_i < @l_maxId)
    BEGIN
        BEGIN TRANSACTION
            -- put not used tuples into the archive table:
            INSERT  INTO arc_ibs_RightsKey
            SELECT  *
            FROM    ibs_RightsKey
            WHERE   id >= @l_i
                AND id < (@l_i + @ai_step)
                AND id NOT IN ( SELECT  rkey
                                FROM    ibs_Object
                                WHERE   rKey >= @l_i
                                    AND rKey < (@l_i + @ai_step))
                AND id < @l_maxId

            -- delete the moved tuples:
            DELETE  ibs_RightsKey
            WHERE   id IN (SELECT id FROM arc_ibs_RightsKey)
        COMMIT TRANSACTION

        SELECT  @l_message =
                N'    ibs_RightsKey upto ' +
                CONVERT (VARCHAR (7), @l_i + @ai_step - 1) + N' done.'
        EXEC p_showMessage @l_message, 0, 1

        BEGIN TRANSACTION
            -- put not used tuples into the archive table:
            INSERT  INTO arc_ibs_RightsKeys
            SELECT  *
            FROM    ibs_RightsKeys
            WHERE   id NOT IN
                    (
                        SELECT  rKeysId
                        FROM    ibs_RightsKey
                    )

            -- delete the moved tuples:
            DELETE  ibs_RightsKeys
            WHERE   id IN (SELECT id FROM arc_ibs_RightsKeys)
        COMMIT TRANSACTION

        SELECT  @l_message =
                N'    ibs_RightsKeys upto ' +
                CONVERT (VARCHAR (7), @l_i + @ai_step - 1) + N' done.'
        EXEC p_showMessage @l_message, 0, 1

        -- table ibs_RightsCum:
        BEGIN TRANSACTION
            TRUNCATE TABLE ibs_RightsCum
/* 20080930 KR backup of rights cum table does not make sense. It can always be recalculated from RightsKeys
            -- put not used tuples into the archive table:
            INSERT  INTO arc_ibs_RightsCum
            SELECT  *
            FROM    ibs_RightsCum
            WHERE   rkey >= @l_i
                AND rkey < (@l_i + @ai_step)
                AND rkey NOT IN (SELECT id FROM ibs_RightsKey)

            -- delete the moved tuples:
            DELETE  ibs_RightsCum
            WHERE   rkey >= @l_i
                AND rkey < (@l_i + @ai_step)
                AND rkey NOT IN (SELECT id FROM ibs_RightsKey)
*/
        COMMIT TRANSACTION

        SELECT  @l_message =
                N'    ibs_RightsCum upto ' +
                CONVERT (VARCHAR (7), @l_i + @ai_step - 1) + N' done.'
        EXEC p_showMessage @l_message, 0, 1

        -- compute index for next step:
        SELECT  @l_i = @l_i + @ai_step
    END -- while

    -- recumulate all rights
    EXEC p_showActDateTime N'Recumulating all rights...', 0, 1
    BEGIN TRANSACTION
        EXEC p_Rights$updateRightsCum
    COMMIT TRANSACTION

    EXEC p_showActDateTime
            N'ibs_RightsKey, ibs_RightsKeys and ibs_RightsCum done at', 1, 1
GO
-- p_reorgRights


-- delete existing procedure:
EXEC p_dropProc N'p_reorgDeleteWorkspaces'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorgDeleteWorkspaces
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OP_CREATETABLE       INT,            -- create archive table
    @c_OP_GETSTATS          INT,            -- get statistics
    @c_OP_REORG             INT,            -- perform reorganisation

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (2000), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_oid                  OBJECTID,       -- oid of actual object
    @l_userId               USERID,         -- id of user for workspace
    @l_posNoPath            POSNOPATH_VC,   -- posNoPath of actual object
    @l_message              NVARCHAR (4000)  -- message to be printed

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OP_CREATETABLE       = 1,
    @c_OP_GETSTATS          = 2,
    @c_OP_REORG             = 3

    -- initialize local variables and return values:
SELECT
    @l_error = 0,
    @l_rowCount = 0,
    @l_retValue = @c_ALL_RIGHT

-- define cursor:
DECLARE wspCursor CURSOR FOR 
    SELECT  w.workspace AS oid, w.userId AS userId, o.posNoPath AS posNoPath
    FROM    ibs_Workspace w INNER JOIN ibs_Object o ON w.workspace = o.oid
    WHERE   (   w.userId NOT IN (SELECT id FROM ibs_User)
            OR  o.state <> 2
            )
    UNION ALL
    SELECT  o.oid AS oid, w.userId AS userId, o.posNoPath AS posNoPath
    FROM    arc_ibs_Workspace w INNER JOIN ibs_Object o ON w.workspace = o.oid
/*
    SELECT  w.workspace AS oid, w.userId AS userId, o.posNoPath AS posNoPath
    FROM    ibs_Workspace w,
            (
                SELECT  *
                FROM    ibs_Object
            UNION ALL
                SELECT  *
                FROM    arc_ibs_Object
            ) o
    WHERE   w.workspace = o.oid
        AND (   w.userId NOT IN (SELECT id FROM ibs_User)
            OR  w.workspace NOT IN
                (   SELECT  oid
                    FROM    ibs_Object
                    WHERE   state = 2
                )
            )
*/

-- body:
    -- open the cursor:
    OPEN    wspCursor

    -- get the first object:
    FETCH NEXT FROM wspCursor INTO @l_oid, @l_userId, @l_posNoPath

    -- loop through all found objects:
    WHILE (@l_retValue = @c_ALL_RIGHT AND @@FETCH_STATUS <> -1)
                                        -- another object found?
    BEGIN
        -- Because @@FETCH_STATUS can have one of the three values 
        -- -2, -1 or 0 all these three cases must be checked.
        -- Here a tuple is ignored if it was deleted during this
        -- procedure.
        -- When the tuple was successfully gotten the specific code
        -- is executed.
        IF (@@FETCH_STATUS <> -2)       -- tuple was not deleted since opening 
                                        -- cursor?
        BEGIN
            -- delete all objects below the workspace:
            UPDATE  ibs_Object
            SET     state = 1
            WHERE   posNoPath LIKE @l_posNoPath + '%'
        END -- if tuple was not deleted since opening cursor

        -- get next object:
        FETCH NEXT FROM wspCursor INTO @l_oid, @l_userId, @l_posNoPath
    END -- while another object found

    -- deallocate cursor:
    DEALLOCATE wspCursor

    -- special tables:

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_reorgDeleteWorkspaces', @l_error, @l_ePos
    EXEC p_showMessage @l_ePos
GO
-- p_reorgDeleteWorkspaces


-- delete existing procedure:
EXEC p_dropProc N'p_reorg'
GO

-- create the new procedure:
CREATE PROCEDURE p_reorg
(
    @ai_maxRKey         INT = 0
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OP_CREATETABLE       INT,            -- create archive table
    @c_OP_GETSTATS          INT,            -- get statistics
    @c_OP_REORG             INT,            -- perform reorganisation

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (2000), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_dbname               NVARCHAR (255),  -- the name of the actual database
    @l_maxId                ID,             -- the maximum id
    @l_name                 NVARCHAR (61),   -- name of actual table
    @l_message              NVARCHAR (4000), -- message to be printed
    @l_objectArcTable       NVARCHAR (255)   -- table for archiving ibs_Object
    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OP_CREATETABLE       = 1,
    @c_OP_GETSTATS          = 2,
    @c_OP_REORG             = 3

    -- initialize local variables and return values:
SELECT
    @l_error = 0,
    @l_rowCount = 0,
    @l_retValue = @c_ALL_RIGHT,
    @l_dbname = DB_NAME (),
    @l_maxId = 0

-- body:
    SET NOCOUNT ON                      -- don't show count messages

    -- show actual date + time:
    SELECT  @l_message = N'Starting reorganisation for database ' + @l_dbname + N' at'
    EXEC p_showActDateTime @l_message, 1, 1

    -- ensure that the archive table exists:
    EXEC p_reorgCreateTable N'ibs_Object', @l_objectArcTable OUTPUT

    -- ensure that all objects within unused workspaces are marked as deleted:
    EXEC p_reorgDeleteWorkspaces

    -- table ibs_Object:
    SELECT  @l_message = N'Reorganizing ibs_Object...'
    EXEC p_showMessage @l_message, 0

    -- get the maximum id
    SELECT  @l_maxId = MAX (id)
    FROM    ibs_Object
    BEGIN TRANSACTION
        -- put not used tuples into the archive table:
        INSERT  INTO arc_ibs_Object
        SELECT  *
        FROM    ibs_Object
        WHERE   state <> 2
            AND lastChanged < DATEADD (day, -1, getDate ())
            AND id < @l_maxId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'insert tuples', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- delete the moved tuples:
        DELETE  ibs_Object
        WHERE   oid IN (SELECT oid FROM arc_ibs_Object)

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
            N'delete tuples', @l_ePos OUTPUT, @l_rowCount OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

    COMMIT TRANSACTION

    SELECT  @l_message = N'    Archiving of ' + CONVERT (VARCHAR, @l_rowCount) +
            N' tuples from ibs_Object done at'
    EXEC p_showActDateTime @l_message, 1

    -- tables ibs_RightsKey, ibs_RightsKeys and ibs_RightsCum:
    EXEC    p_reorgRights @ai_maxRKey

    -- common tables:
    EXEC p_reorgMultipleOperation @c_OP_REORG

    -- special tables:
    EXEC p_reorgTable N'ibs_Workspace',
        N'workspace NOT IN (SELECT oid FROM ibs_Object)'

    EXEC p_reorgTable N'ibs_GroupUser',
        N'userId NOT IN (SELECT id FROM ibs_User UNION ALL SELECT id FROM ibs_Group)'

    EXEC p_reorgTable N'ibs_Reference',
        N'referencingOid NOT IN (SELECT oid FROM ibs_Object) OR referencedOid NOT IN (SELECT oid FROM ibs_Object)'

    -- ibs_Type: nothing to do

    EXEC p_reorgTable N'ibs_TVersion',
        N'NOT (typeId IN (SELECT id FROM ibs_Type))'

    EXEC p_reorgTable N'ibs_TVersionProc',
        N'NOT (tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type)))'

    EXEC p_reorgTable N'ibs_MayContain',
        N'NOT (majorTypeId IN (SELECT id FROM ibs_Type) AND minorTypeId IN (SELECT id FROM ibs_Type))'

    -- ibs_Tab: nothing to do

    EXEC p_reorgTable N'ibs_ConsistsOf',
        N'NOT (tVersionId IN (SELECT id FROM ibs_TVersion WHERE typeId IN (SELECT id FROM ibs_Type)) AND tabId IN (SELECT id FROM ibs_Tab))'

    EXEC p_reorgTable N'ibs_KeyMapper',
        N'NOT (oid IN (SELECT oid FROM ibs_Object))'

    -- ibs_KeyMapperArchive: nothing to do
--    EXEC p_reorgTable 'ibs_KeyMapperArchive',
--        'NOT (oid IN (SELECT oid FROM ibs_Object))'

-- not finished:
/*
    EXEC p_reorgTable 'ibs_Protocol_01',
        ''

    EXEC p_reorgTable 'ibs_Domain_01',
        ''

    EXEC p_reorgTable 'ibs_DomainScheme_01',
        ''

    EXEC p_reorgTable 'ibs_WorkflowProtocol',
        ''

    EXEC p_reorgTable 'ibs_WorkflowVariables',
        ''

    EXEC p_reorgTable 'm2_CatalogPayments',
        ''

    EXEC p_reorgTable 'm2_PriceCodeValues_01',
        ''

    EXEC p_reorgTable 'm2_ProductCodeValues_01',
        ''

    EXEC p_reorgTable 'm2_ProductCollectionQty_01',
        ''

    EXEC p_reorgTable 'm2_ProductCollectionValue_01',
        ''

    EXEC p_reorgTable 'm2_ProfileCategory_01',
        ''
*/

    EXEC p_showActDateTime N'Reorganising done at', 1

    SET NOCOUNT OFF                     -- show count messages again

    -- finish the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_reorg', @l_error, @l_ePos
    EXEC p_showMessage @l_ePos
GO
-- p_reorg


-- delete not needed triggers:
EXEC p_dropTrig N'TrigObjectDelete'
GO
