/******************************************************************************
 * create all indices of the hole base-system. <BR>
 *
 * @version     $Id: createIndices.sql,v 1.18 2003/10/31 00:13:10 klaus Exp $
 *
 * @author      Andreas Jansa (AJ) 000814
 ******************************************************************************
 */

--
-- get and drop all indixes where dropping is possible
-- the following indixes can not be dropped:
-- * index created by 'primary key' column constraint
-- * unique index created by 'unique' column constraint
-- * index on a lob structure
--
-- only m2 indexes will be selected and dropped
-- prefixes: M2_ MAD_ SP_
-- 
DECLARE
    -- local variables:
    l_file                  VARCHAR2 (15) := 'createIndices'; -- name of actual file
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_indexName             VARCHAR2 (50);
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_rowsProcessed         INTEGER;        -- number of rows of last cmd exec.
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution

    -- define cursor:
    -- get all indexes
    CURSOR  cursorAllIndexes IS
        SELECT  index_name
        FROM    user_indexes
        WHERE   index_name NOT LIKE 'PK__%' -- primary key constraint
            AND index_name NOT LIKE 'UQ__%' -- unique constraint
            AND index_name NOT LIKE 'SYS_%' -- unknown???
            AND index_type NOT LIKE 'LOB'   -- without indexes created by LOB columns
            -- get only indexes on m2 tables :
            AND (   table_name LIKE 'M2_%'
                OR  table_name LIKE 'MAD_%' 
                OR  table_name LIKE 'SP_%');
    l_cursorRow             cursorAllIndexes%ROWTYPE;

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- loop through the cursor rows:
    FOR l_cursorRow IN cursorAllIndexes -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_indexName := l_cursorRow.index_name;

        -- create the command string for deleting the index:
        l_cmdString := 'DROP INDEX ' || l_indexName;
debug (l_file || ': ' || l_cmdString);

        -- try to delete the index:
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
                    'Error when dropping index at ' || l_lastErrorPos;
                RAISE;                  -- call common exception handler
        END;

    END LOOP; -- while another tuple found

    -- make changes permanent and set new transaction starting point:
    COMMIT WORK;

    -- print state report:
    debug (l_file || ': ' || 'Old indexes deleted.');

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_file || ': ' || l_ePos ||
            '; l_indexName = ' || l_indexName ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        debug (l_eText);
        ibs_error.log_error (ibs_error.error, l_file, l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
END;
/
COMMIT WORK;


--
-- CREATE ALL INDEXES
--
-- the following error will occur for some statements:
--      ORA-01408: such column list already indexed
--
-- reason:
--      already existing indices created by 'UNIQUE' column constraints
--
-- what should you do about the errors?
--      NOTHING!
--

BEGIN
    debug ('createIndices: Creating new indexes...');
END;
/

-- 
-- MODULE: M2
--

-- M2_Article_01
CREATE INDEX INDEXArticleDISCUSSIONID ON M2_Article_01 ( discussionId ) /*TABLESPACE*/;

-- M2_PRODUCT_01
CREATE INDEX INDEXPRODUCT_01PRODUCTNO ON M2_PRODUCT_01 ( productNo ) /*TABLESPACE*/;

-- M2_PRODUCTCODEVALUES_01
CREATE INDEX INDEXPRODUCTCVALUES_01OID ON M2_PRODUCTCODEVALUES_01 ( productOid,categoryOid ) /*TABLESPACE*/;

-- M2_PARTICIPANT
CREATE UNIQUE INDEX INDEXPARTICIPANTOID ON M2_PARTICIPANT_01 ( oid ) /*TABLESPACE*/;

-- M2_TERMIN_01
CREATE INDEX INDEXOBJECTTERMINSTARTDATE ON M2_TERMIN_01 ( startDate ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTTERMINENDDATE ON M2_TERMIN_01 ( endDate ) /*TABLESPACE*/;


BEGIN
    debug ('createIndices: Indexes created.');
END;
/

COMMIT WORK;

EXIT;
