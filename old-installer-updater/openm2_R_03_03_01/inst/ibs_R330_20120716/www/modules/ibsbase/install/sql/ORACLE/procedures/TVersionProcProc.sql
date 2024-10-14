/******************************************************************************
 * All stored procedures regarding the TVersionProc table. <BR>
 * 
 * @version     2.10.0001, 26.01.2001
 *
 * @author      Mario Oberdorfer (MO)  010212
 ******************************************************************************
 */

/******************************************************************************
 * Inherit the tuples from one tVersion to another tVersion. <BR>
 * If there are any tVersions currently inheriting their tuples from the second
 * tVersion they will also inherit their tuples from the first tVersion. <BR>
 * This function must be called from within a transaction handled code block
 * because it uses savepoints.
 *
 * @input parameters:
 * @param   ai_majorTVersionId  Id of the major tVersion from which the tuples
 *                              shall be inherited.
 * @param   ai_minorTVersionId  Id of minor tVersion to which the tuples shall
 *                              be inherited.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_TVersionProc$inherit
(
    -- input parameters:
    ai_majorTVersionId      INTEGER,
    ai_minorTVersionId      INTEGER
    -- output parameters:
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
    l_posNoPath             VARCHAR2 (254); -- the pos no path of the minor
                                            -- tVersion
    l_code                  VARCHAR2 (63);  -- the actually handled procedure
                                            -- code
    l_inheritedFrom         INTEGER;        -- tVersion from which the super
                                            -- tVersion inherits the entry
    -- define cursor:
    -- get all tVersions of the type which shall be deleted.
    CURSOR updateCursor IS
        SELECT  code, inheritedFrom
        FROM    ibs_TVersionProc
        WHERE   tVersionId = ai_majorTVersionId;
    l_cursorRow             updateCursor%ROWTYPE;


BEGIN
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_TVersionProc$inherit;

    -- get the data of the tVersion to which to inherit the tuples:
    BEGIN
        SELECT  posNoPath
        INTO    l_posNoPath
        FROM    ibs_TVersion
        WHERE   id = ai_minorTVersionId;

    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get minor tVersion data';
            RAISE;                      -- call common exception handler  
    END;

    -- loop through the cursor rows:
    FOR l_cursorRow IN updateCursor     -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_code := l_cursorRow.code;
        l_inheritedFrom := l_cursorRow.inheritedFrom;

        -- delete the values for the minor tVersion and all
        -- tVersions below which inherit their values from the same
        -- TVersion as that tVersion:
        BEGIN     
            DELETE  ibs_TVersionProc
            WHERE   tVersionId IN
                    (
                        SELECT  id
                        FROM    ibs_TVersion
                        WHERE   posNoPath LIKE l_posNoPath || '%'
                    )
                AND code = l_code
                AND inheritedFrom = l_inheritedFrom;

        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'delete for act tVersion and tVersions below';
                RAISE;                  -- call common exception handler          
        END;
    END LOOP; -- while another object found

    -- add the records to the minor tVersion and all tVersions
    -- below which before inherited from the same tVersion as
    -- the minor tVersion:
    BEGIN
        INSERT INTO ibs_TVersionProc
                (tVersionId, code, name, inheritedFrom)
        SELECT  tv.id, p.code, p.name, p.inheritedFrom
        FROM    ibs_TVersionProc p, ibs_TVersion tv
        WHERE   tv.posNoPath LIKE l_posNoPath || '%'
            AND tv.id NOT IN
                (
                    SELECT  tVersionId
                    FROM    ibs_TVersionProc
                    WHERE   code = p.code
                )
            AND p.tVersionId = ai_majorTVersionId;

    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'insert for act tVersion and tVersions below';
            RAISE;                      -- call common exception handler          
    END;

    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_TVersionProc$inherit;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_majorTVersionId' || ai_majorTVersionId ||
            ', ai_minorTVersionId' || ai_minorTVersionId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersionProc$inherit', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersionProc$inherit;
/

show errors;

/******************************************************************************
 * This procedure creates tuples into the TVersionProc table. <BR>
 * If there exists already an entry for the specified procedure within the
 * tVersion it is overwritten with the new value. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion for which the procedures
 *                              shall be defined.
 * @param   ai_code             Unique code of the procedure.
 * @param   ai_name             Name of the procedure.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_TVersionProc$add
(
    -- input parameters:
    ai_tVersionId           INTEGER,
    ai_code                 VARCHAR2,
    ai_name                 VARCHAR2
    -- output parameters:
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
    l_posNoPath             VARCHAR2 (254); -- the pos no path of the tVersion
    l_inheritedFrom         INTEGER;        -- tVersion from which the tVersion
                                            -- inherited the entry before
    
BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

        BEGIN
            -- get the data of the actual tVersion:
            SELECT  posNoPath
            INTO    l_posNoPath
            FROM    ibs_TVersion
            WHERE   id = ai_tVersionId;
                
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get tVersion data';
                RAISE;                  -- call common exception handler  
        END;

        BEGIN
            -- get the procedure data of the actual tVersion:
            SELECT  inheritedFrom
            INTO    l_inheritedFrom
            FROM    ibs_TVersionProc
            WHERE   tVersionId = ai_tVersionId
                AND code = ai_code;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- currently no defined procedure data
                -- set the default inherited tVersionId:
                l_inheritedFrom := 0;
            -- end when currently no defined procedure data
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get tVersion procedure data';
                RAISE;                  -- call common exception handler  
        END;

        -- at this point we know that the operation may be done
        -- update the value for the actual tVersion and all tVersions below
        -- which inherit their values from the same TVersion as this tVersion:
        -- all these tVersions inherit now from the actual tVersion
        BEGIN
            UPDATE  ibs_TVersionProc
            SET     name = ai_name,
                    inheritedFrom = ai_tVersionId
            WHERE   tVersionId IN
                    (
                        SELECT  id
                        FROM    ibs_TVersion
                        WHERE   posNoPath LIKE l_posNoPath || '%'
                    )
                AND code = ai_code
                AND inheritedFrom = l_inheritedFrom;

        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'update for act tVersion and tVersions below';
                RAISE;                  -- call common exception handler
        END;

        -- add the record to all tVersions below which currently do not have
        -- this record:
        BEGIN
            INSERT INTO ibs_TVersionProc (tVersionId, code, name, inheritedFrom)
            SELECT  id, ai_code, ai_name, ai_tVersionId
            FROM    ibs_TVersion
            WHERE   id NOT IN
                    (
                        SELECT DISTINCT tVersionId
                        FROM    ibs_TVersionProc
                        WHERE   code = ai_code
                    )
                AND posNoPath LIKE l_posNoPath || '%';
           
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'insert for act tVersion and tVersions below';
                RAISE;                  -- call common exception handler  
        END;

    -- finish the transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId' || ai_tVersionId ||
            ', ai_code' || ai_code ||
            ', ai_name' || ai_name ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersionProc$add', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersionProc$add;
/

show errors;

/******************************************************************************
 * This procedure creates tuples into the TVersionProc table. <BR>
 * It calls p_TVersionProc$add for the actual version of the type which is
 * specified by its code.
 * The tuples are stored for the actual version of that type which is identified
 * by its code.
 *
 * @input parameters:
 * @param   ai_typeCode         Code value of the type.
 * @param   ai_code             Unique code of the procedure.
 * @param   ai_name             Name of the procedure.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_TVersionProc$new
(
    -- input parameters:
    ai_typeCode             VARCHAR2,
    ai_code                 VARCHAR2,
    ai_name                 VARCHAR2
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_tVersionId            INTEGER := 0;   -- tVersionId of the tab

BEGIN
-- body:
    -- get the actual tVersion id for the type code:
    BEGIN
        SELECT  actVersion
        INTO    l_tVersionId
        FROM    ibs_Type 
        WHERE   code = ai_typeCode;

        -- add the new procedure entry to the table:
        l_retValue := p_TVersionProc$add (l_tVersionId, ai_code, ai_name);
    END;

    -- return the state value:
    RETURN  l_retValue;
END p_TVersionProc$new;
/

show errors;


/******************************************************************************
 * Delete a tVersion specific entry for a procedure. <BR>
 * The code entry of the super tVersion is inherited to the actual tVersion and
 * all tVersions below which inherited that entry from the actual tVersion.
 * If there is no entry in the super tVersion the entries in the actual
 * tVersion and all tVersions below which inherit from the actual tVersion are
 * deleted. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion for which a procedure
 *                              shall be deleted.
 * @param   ai_code             Unique code of the procedure to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 * c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
 */
CREATE OR REPLACE FUNCTION p_TVersionProc$delete
(
    -- input parameters:
    ai_tVersionId           INTEGER,
    ai_code                 VARCHAR2
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;  -- tuple not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function    
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_posNoPath             VARCHAR2 (254); -- the pos no path of the tVersion    
    l_name                  VARCHAR2 (63);  -- name of procedure in super
                                            -- tVersion   
    l_inheritedFrom         INTEGER;        -- tVersion from which the super
                                            -- tVersion inherits the entry
    l_superTVersionId       INTEGER;        -- Id of super tVersion of the
                                            -- actual tVersion

BEGIN
-- body:
    -- get the data of the actual tVersion:
    BEGIN
        SELECT  posNoPath, superTVersionId
        INTO    l_posNoPath, l_superTVersionId
        FROM    ibs_TVersion
        WHERE   id = ai_TVersionId;

    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get data of actual tVersion';
            RAISE;                      -- call common exception handler  
    END;

    -- get the procedure name from the super tVersion:
    BEGIN
        SELECT  name, inheritedFrom
        INTO    l_name, l_inheritedFrom
        FROM    ibs_TVersionProc
        WHERE   tVersionId = l_superTVersionId
            AND code = ai_code;

        -- at this point we know that the operation may be done
        -- and there exists an entry in the super tVersion.
        BEGIN
            -- inherit the entry from the super tVersion to the actual tVersion
            -- and all tVersions which inherited from the actual tVersion:
            UPDATE  ibs_TVersionProc
            SET     name = l_name,
                    inheritedFrom = l_inheritedFrom
            WHERE   tVersionId = ai_tVersionId
                OR  inheritedFrom = ai_tVersionId;
                
        EXCEPTION
            WHEN OTHERS THEN        -- any error
                -- create error entry:
                l_ePos := 'inherit entry from super tVersion';
                RAISE;              -- call common exception handler  
        END;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- no entry for super tVersion
            -- delete the entry from the actual tVersion and all tVersions
            -- which inherit from the actual tVersion:
            BEGIN
                DELETE  ibs_TVersionProc
                WHERE   (   tVersionId = ai_tVersionId
                        OR  inheritedFrom = ai_tVersionId
                        )
                    AND code = ai_code;

            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'delete entry';
                    RAISE;              -- call common exception handler 
            END;
        -- end when no entry for super tVersion

        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get procedure name';
            RAISE;                      -- call common exception handler 
    END;

    -- finish the transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId' || ai_tVersionId ||
            ', ai_code' || ai_code ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersionProc$delete', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersionProc$delete;
/

show errors;

/******************************************************************************
 * Delete all occurrences of a code out of the TVersionProc table. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_code             The code to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_TVersionProc$deleteCode
(
    -- input parameters:
    ai_code                 VARCHAR2
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255);     -- error position description
    l_eText                 VARCHAR2 (5000);    -- full error text

BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

        -- delete the entries of the code from the TVersionProc table:
        BEGIN    
            DELETE  ibs_TVersionProc
            WHERE   code = ai_code;

        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'delete';
                RAISE;                  -- call common exception handler  
        END;

    -- finish the transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_code' || ai_code ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersionProc$deleteCode', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersionProc$deleteCode;
/

show errors;

/******************************************************************************
 * Delete all occurrences of a specific tVersion out of the TVersionProc table.
 * <BR>
 * If the tVersion is used to inherit entries to sub tVersions the sub tVersions
 * will inherit their entries from the super tVersions of the tVersion. <BR>
 * This function must be called from within a transaction handled code block
 * because it uses savepoints.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_TVersionProc$deleteTVersion
(
    -- input parameters:
    ai_tVersionId           INTEGER
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text    
    l_superTVersionId       INTEGER;        -- Id of super tVersion of the
                                            -- actual tVersion

BEGIN
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_TVersionProc$deleteTVersion;

    -- get the data of the tVersion:
    BEGIN
        SELECT  superTVersionId
        INTO    l_superTVersionId
        FROM    ibs_TVersion
        WHERE   id = ai_tVersionId;

       -- inherit all entries from the super tVersion:
       -- the consequence of this action is, that no sub tVersion will have
       -- inherited values from this tVersion
       l_retValue :=
            p_TVersionProc$inherit (l_superTVersionId, ai_tVersionId);

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the super tVersion was not found
            NULL;                       -- nothing to do
        -- end when the super tVersion was not found
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get data of tVersion';
            RAISE;                      -- call common exception handler
        END;

    -- check if there was an error:
    IF (l_retValue = c_ALL_RIGHT)       -- no error thus far?
    THEN        
        -- delete the entries of the actual tVersion and all entries which were
        -- inherited from this tVersion from the tVersionProc table:
        BEGIN
            DELETE  ibs_TVersionProc
            WHERE   tVersionId = ai_tVersionId
                OR  inheritedFrom = ai_tVersionId;

        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'delete';
                RAISE;                  -- call common exception handler  
        END;
    END IF; -- if no error thus far

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_TVersionProc$deleteTVersion;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId' || ai_tVersionId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersionProc$deleteTVersion', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersionProc$deleteTVersion;
/

show errors;


/******************************************************************************
 * Delete all occurrences of tVersions belonging to a specific type out of the
 * TVersionProc table. <BR>
 * If any tVersion of the type is used to inherit entries to sub tVersions the
 * sub tVersions will inherit their entries from the super tVersions of the
 * specific tVersion. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_typeId           Id of the type to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_TVersionProc$deleteType
(
    -- input parameters:
    ai_typeId               INTEGER
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_tVersionId            INTEGER;        -- id of actual tVersion
    -- define cursor:
    -- get all tVersions of the type which shall be deleted.
    CURSOR updateCursor IS
        SELECT  id
        FROM    ibs_TVersion
        WHERE   typeId = ai_typeId;
    l_cursorRow             updateCursor%ROWTYPE;

BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

        -- loop through the cursor rows:
        FOR l_cursorRow IN updateCursor -- another tuple found
        LOOP
            -- get the actual tuple values:
            l_tVersionId := l_cursorRow.id;

            -- delete the entries for the actual tVersion:
            l_retValue := p_TVersionProc$deleteTVersion (l_tVersionId);

            -- check for an error and exit the loop if there was any:
            EXIT WHEN (l_retValue <> c_ALL_RIGHT);
        END LOOP; -- while another tuple found

    -- finish the transaction:
    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
    END IF; -- else an error occurred

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_typeId' || ai_typeId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersionProc$deleteType', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersionProc$deleteType;
/

show errors;

EXIT;
