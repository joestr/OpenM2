/******************************************************************************
 * All stored procedures regarding the ConsistsOf table. <BR>
 * 
 * @version     2.10.0001, 14.02.2001
 *
 * @author      Mario Oberdorfer (MO)  010209
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
CREATE OR REPLACE FUNCTION p_ConsistsOf$inherit
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
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_posNoPath             VARCHAR2 (255); -- the pos no path of the minor
                                            -- tVersion
    l_inheritedFrom         INTEGER;        -- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries

BEGIN
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_ConsistsOf$inherit;

    -- get the data of the tVersion to which to inherit the tuples:
    BEGIN
        SELECT  MIN (tv.posNoPath),
                MIN (c.inheritedFrom)
        INTO    l_posNoPath, l_inheritedFrom
        FROM    ibs_TVersion tv, ibs_ConsistsOf c
        WHERE   tv.id = ai_minorTVersionId
            AND c.tVersionId = ai_minorTVersionId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get minor tVersion data';
            RAISE;                      -- call common exception handler
    END;

    -- delete the values for the minor tVersion and all
    -- tVersions below which inherit their values from the same
    -- TVersion as that tVersion:
    BEGIN
        DELETE  ibs_ConsistsOf
        WHERE   tVersionId IN
                (
                    SELECT  id
                    FROM    ibs_TVersion
                    WHERE   id = ai_minorTVersionId
                        OR  posNoPath LIKE l_posNoPath || '%'
                )
            AND inheritedFrom = l_inheritedFrom;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'delete for act tVersion and tVersions below';
            RAISE;                      -- call common exception handler
    END;

    -- add the records to the minor tVersion and all tVersions
    -- below which before inherited from the same tVersion as
    -- the minor tVersion:
    BEGIN
        INSERT INTO ibs_ConsistsOf
                (id, tVersionId, tabId, priority, rights, inheritedFrom)
        SELECT  -B_OR (B_AND (tv.id, 1048575), B_AND (c.tabId, 4095) * 1048576),
                tv.id, c.tabId, c.priority, c.rights, c.inheritedFrom
        FROM    ibs_ConsistsOf c, ibs_TVersion tv
        WHERE   (   tv.id = ai_minorTVersionId
                OR  tv.posNoPath LIKE l_posNoPath || '%'
                )
            AND tv.id NOT IN
                (
                    SELECT  tVersionId
                    FROM    ibs_ConsistsOf
                )
            AND c.tVersionId = ai_majorTVersionId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'insert for act tVersion and tVersions below';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_ConsistsOf$inherit;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_majorTVersionId' || ai_majorTVersionId ||
            ', ai_minorTVersionId' || ai_minorTVersionId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$inherit', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$inherit;
/
show errors;


/******************************************************************************
 * Add a new tab to a type version. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of type version for which to add a new tab.
 * @param   ai_code             The unique code of the tab.
 *
 * @output parameters:
 * @param   ao_id               Id of the newly generated tuple.
 * @returns A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 */
CREATE OR REPLACE FUNCTION p_ConsistsOf$newCode
(
    -- input parameters:
    ai_tVersionId           INTEGER,
    ai_tabCode              VARCHAR2,
    -- output parameters:
    ao_id                   OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_tabId                 INTEGER;        -- the id of the tab
    l_kind                  INTEGER;        -- kind of the tab
    l_tVersionId            INTEGER;        -- tVersionId of the tab
    l_fct                   INTEGER;        -- function of the tab
    l_priority              INTEGER;        -- priority of the tab
    l_multilangKey          VARCHAR2 (63);  -- the language key of the tab
    l_rights                INTEGER;        -- the necessary rights to display
                                            -- the tab
    l_posNoPath             VARCHAR2 (254); -- the pos no path of the minor
                                            -- tVersion
    l_inheritedFrom         INTEGER;        -- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries
    -- define cursor:
    -- get all tVersions for which to set the tab.
    CURSOR updateCursor IS
        SELECT  id
        FROM    ibs_TVersion
        WHERE   posNoPath LIKE l_posNoPath || '%'
            AND (   id = ai_tVersionId
                OR  id NOT IN
                    (   SELECT  tVersionId
                        FROM    ibs_ConsistsOf
                        WHERE   tabId = l_tabId
                            OR  inheritedFrom <> ai_tVersionId
                    )
                );
    l_cursorRow             updateCursor%ROWTYPE;

BEGIN
-- body:
   -- get the tab data:
   l_retValue := p_Tab$get (0, ai_tabCode,
                l_tabId, l_kind, l_tVersionId,
                l_fct, l_priority, l_multilangKey,
                l_rights);

    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything o.k.?
    THEN
        COMMIT WORK; -- finish previous and begin new TRANSACTION

            -- get the data of the actual tVersion:
            BEGIN
                SELECT  posNoPath
                INTO    l_posNoPath
                FROM    ibs_TVersion
                WHERE   id = ai_tVersionId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'get tVersion data';
                    RAISE;              -- call common exception handler
            END; 

            -- get the existing relationship data:
            BEGIN     
                SELECT  id
                INTO    ao_id
                FROM    ibs_ConsistsOf
                WHERE   tVersionId = ai_tVersionId
                    AND inheritedFrom = tVersionId
                    AND tabId = l_tabId;

                -- at this point we know that the relationship already exists.
                -- set error code:
                l_retValue := c_ALREADY_EXISTS;

            EXCEPTION
                WHEN NO_DATA_FOUND THEN -- relationship does not exist yet
                    -- get the consistsOf data of the actual tVersion:
                    BEGIN
                        SELECT  MIN (inheritedFrom)
                        INTO    l_inheritedFrom
                        FROM    ibs_ConsistsOf
                        WHERE   tVersionId = ai_tVersionId;

                    EXCEPTION
                        WHEN OTHERS THEN -- any error
                            -- create error entry:
                            l_ePos := 'get tVersion consistsOf data';
                            RAISE;      -- call common exception handler
                    END;

                    -- at this point we know that the operation may be done.
                    -- check if the major tVersion currently has own records within
                    -- the consists of table or inherits its records from another
                    -- tVersion:
                    IF (l_inheritedFrom <> ai_tVersionId)
                                        -- inherited from another tVersion?
                    THEN
                        -- delete the entries within the consists of table which
                        -- are inherited from above the actual tVersion to one
                        -- tVersion which is below the actual tVersion or to the
                        -- actual tVersion itself:
                        BEGIN
                            DELETE  ibs_ConsistsOf
                            WHERE   tVersionId IN
                                    (
                                        SELECT  id
                                        FROM    ibs_TVersion
                                        WHERE   id = ai_tVersionId
                                            OR  posNoPath LIKE l_posNoPath || '%'
                                    )
                                AND inheritedFrom = l_inheritedFrom;

                        EXCEPTION
                            WHEN OTHERS THEN -- any error
                                -- create error entry:
                                l_ePos := 'delete inherited entries';
                                RAISE;  -- call common exception handler
                        END;
                    END if; -- if inherited from another tVersion

                    -- loop through the cursor rows:
                    FOR l_cursorRow IN updateCursor -- another tuple found
                    LOOP
                        -- get the actual tuple values:
                        l_tVersionId := l_cursorRow.id;

                        -- insert the new records of the actual tVersion and its
                        -- sub tVersions into the consists of table:
                        BEGIN
                            INSERT INTO ibs_ConsistsOf
                                    (tVersionId, tabId, priority, rights,
                                    inheritedFrom)
                            VALUES (l_tVersionId, l_tabId, l_priority, l_rights,
                                    ai_tVersionId);

                        EXCEPTION
                            WHEN OTHERS THEN -- any error
                                -- create error entry:
                                l_ePos := 'insert records';
                                RAISE;  -- call common exception handler
                        END;
                    END LOOP; -- while another tuple found

                    -- get the existing relationship data:
                    BEGIN
                        SELECT  id
                        INTO    ao_id
                        FROM    ibs_ConsistsOf
                        WHERE   tVersionId = ai_tVersionId
                            AND inheritedFrom = tVersionId
                            AND tabId = l_tabId;

                    EXCEPTION
                        WHEN OTHERS THEN -- any error
                            -- create error entry:
                            l_ePos := 'get relationship data2';
                            RAISE;      -- call common exception handler
                    END;
                -- end when relationship does not exist yet

                WHEN OTHERS THEN            -- any error
                    -- create error entry:
                    l_ePos := 'get relationship data';
                    RAISE;                  -- call common exception handler
            END;

        -- finish the transaction:
        COMMIT WORK; 
    END IF; -- if everything o.k.

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_TVersionId' || ai_tVersionId ||
            ', ai_tabCode' || ai_tabCode ||
            ', ao_id' || ao_id ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$newCode', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$newCode;
/

show errors;


/******************************************************************************
 * Ensure that a specific tab exists. <BR>
 *
 * @input parameters:
 * @param   ai_code             The unique code of the tab.
 * @param   ai_tVersionId       Id of type version if tab shall be an object.
 * @param   ai_description      Description for the created tab.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 */
CREATE OR REPLACE FUNCTION p_ConsistsOf$ensureTabExists
(
    -- input parameters:
    ai_code                 VARCHAR2,
    ai_tVersionId           INTEGER,
    ai_description          VARCHAR2
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists
    c_TK_VIEW               CONSTANT INTEGER := 1;  -- tab kind VIEW
    c_TK_OBJECT             CONSTANT INTEGER := 2;  -- tab kind OBJECT
    c_TK_LINK               CONSTANT INTEGER := 3;  -- tab kind LINK
    c_TK_FUNCTION           CONSTANT INTEGER := 4;  -- tab kind FUNCTION
    c_languageId            CONSTANT INTEGER := 0;  -- the current language

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_tabId                 INTEGER;        -- the id of the tab
    l_kind                  INTEGER;        -- kind of the tab
    l_tVersionId            INTEGER;        -- tVersionId of the tab
    l_fct                   INTEGER;        -- function of the tab
    l_priority              INTEGER;        -- priority of the tab
    l_multilangKey          VARCHAR2 (63);  -- the language key of the tab
    l_rights                INTEGER;        -- the necessary rights to display
                                            -- the tab

BEGIN
-- body:
    -- get the tab data:
    l_retValue := p_Tab$get (0, ai_code,
            l_tabId, l_kind, l_tVersionId,
            l_fct, l_priority, l_multilangKey,
            l_rights);

    -- check if the tab was found:
    IF (l_retValue <> c_ALL_RIGHT)      -- tab was not found?
    THEN
        -- compute the several values:
        IF (ai_tVersionId <> 0)         -- tab is an own object?
        THEN
            l_kind := c_TK_OBJECT;
        -- if tab is an own object
        ELSE                            -- tab is just a view
            l_kind := c_TK_VIEW;
        END iF; -- else tab is just a view

        -- add the tab:
        l_retValue := p_Tab$new (0, ai_code, l_kind,
             ai_tVersionId, 51, 0, l_multilangKey, 0, NULL,
             l_tabId);

        -- check if there occurred an error:
        IF (l_retValue = c_ALL_RIGHT) -- everything o.k.?
        THEN
            -- get the tab data:
            l_retValue := p_Tab$get (0, ai_code,
                l_tabId, l_kind, l_tVersionId,
                l_fct, l_priority, l_multilangKey,
                l_rights);

            -- update description for consistency between
            -- language tables and tab table:
            p_ObjectDesc_01$new
                (c_languageId, l_multilangKey, ai_code,
                ai_description, '');        
        END IF; -- if everything o.k.
    END IF; -- if tab was not found

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId' || ai_tVersionId ||
            ', ai_code' || ai_code ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$ensureTabExists', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$ensureTabExists;
/

show errors;


/******************************************************************************
 * Create a new entry within ibs_ConsistsOf <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_majorTVersionId  Id of majorType for which to define a minorType.
 * @param   ai_minorTVersionId  Id of minorType belonging to majorType.
 * @param   ai_name             Name of the object to be created of the 
 *                              minorType.
 * @param   ai_description      Description for the created object.
 *
 * @output parameters:
 * @param   ao_id               Id of the newly generated tuple.
 * @returns A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         An entry for this tab already exists.
 */
CREATE OR REPLACE FUNCTION p_ConsistsOf$new
(
    -- input parameters:
    ai_majorTVersionId      INTEGER,
    ai_minorTVersionId      INTEGER,
    ai_name                 VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters:
    ao_id                   OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists
    c_languageId            CONSTANT INTEGER := 0;  -- the actual language

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text

BEGIN
-- body:
    -- get the tab data:
    l_retValue :=
            p_ConsistsOf$ensureTabExists (ai_minorTVersionId, ai_name, ai_description);

    -- call common procedure for creating a tVersion/tab relationship:
    l_retValue :=
            p_ConsistsOf$newCode (ai_majorTVersionId, ai_name, ao_id);

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_majorTVersionId' || ai_majorTVersionId ||
            ', ai_name' || ai_name ||
            ', ai_minorTVersionId' || ai_minorTVersionId ||
            ', ai_description' || ai_description ||
            ', ao_id' || ao_id ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$new', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$new;
/

show errors;


/******************************************************************************
 * Delete a tab from a tVersion. <BR>
 * If this is the last tab defined for this tVersion and
 * inheritFromUpper is set to 1, the tVersion (and its sub tVersions)
 * automatically inherits the records from its super tVersion.
 * If the required tuple is not found this is no severe error. So the second
 * operation of inheriting from the super tVersion is also done in the same way.
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tVersionId       Id of the tVersion for which a procedure
 *                              shall be deleted.
 * @param   ai_tabCode          Unique code of the tab to be deleted.
 * @param   ai_inheritFromSuper In case that there are no more records for
 *                              the tVersion after deleting the requested
 *                              record this parameter tells whether the tVersion
 *                              shall inherit the records from its super
 *                              tVersion or not.
 *                              Default: 1 (= true)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 * c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
 */
CREATE OR REPLACE FUNCTION p_ConsistsOf$delete
(
    -- input parameters:
    ai_tVersionId          INTEGER,
    ai_tabCode             VARCHAR2,
    ai_inheritFromSuper    NUMBER
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
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_tabId                 INTEGER;        -- the id of the tab
    l_kind                  INTEGER;        -- kind of the tab
    l_tVersionId            INTEGER;        -- tVersionId of the tab
    l_fct                   INTEGER;        -- function of the tab
    l_priority              INTEGER;        -- priority of the tab
    l_multilangKey          VARCHAR2 (63);  -- the language key of the tab
    l_rights                INTEGER;        -- the necessary rights to display
                                            -- the tab
    l_posNoPath             VARCHAR2 (254); -- the pos no path of the minor
                                            -- tVersion
    l_inheritedFrom         INTEGER;        -- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries
    l_superTVersionId       INTEGER;        -- Id of super tVersion of the
                                            -- actual tVersion

BEGIN
-- body:
    -- get the tab data:
    l_retValue := p_Tab$get (0, ai_tabCode,
        l_tabId, l_kind, l_tVersionId,
        l_fct, l_priority, l_multilangKey,
        l_rights);

    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything o.k.?
    THEN
        COMMIT WORK; -- finish previous and begin new TRANSACTION

            -- get the data of the actual tVersion:
            BEGIN
                SELECT  tv.posNoPath, tv.superTVersionId, c.inheritedFrom
                INTO    l_posNoPath, l_superTVersionId, l_inheritedFrom
                FROM    ibs_TVersion tv, ibs_ConsistsOf c
                WHERE   tv.id = ai_TVersionId
                    AND c.tVersionId = ai_TVersionId
                    AND c.tabId = l_tabId;

            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'get data of actual tVersion';
                    RAISE;              -- call common exception handler
            END;

            -- check if the type version currently has own records within the
            -- consists of table or inherits its records from another tVersion:
            IF (l_inheritedFrom = ai_tVersionId)
                                        -- not inherited from another tVersion?
            THEN
                -- at this point we know that the operation may be done
                -- delete the record in the tVersion itself and all inherited
                -- ones in the sub tVersions:
                BEGIN
                    DELETE  ibs_ConsistsOf
                    WHERE   (   tVersionId = ai_tVersionId
                            OR  inheritedFrom = ai_tVersionId
                            )
                        AND tabId = l_tabId;

                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'delete';
                    RAISE;              -- call common exception handler
                END;

                -- check if there are any records for the actual tVersion left:
                BEGIN
                    SELECT  COUNT (tVersionId)
                    INTO    l_rowCount
                    FROM    ibs_ConsistsOf
                    WHERE   tVersionId = ai_tVersionId;
                    
                    -- check if the tVersion shall inherit from the super
                    -- tVersion:
                    IF (l_rowCount <= 0 AND
                        ai_inheritFromSuper = 1 AND l_superTVersionId <> 0)
                                        -- inherit from super tVersion?
                    THEN
                        -- inherit the entries from the super tVersion:
                        l_retValue := p_ConsistsOf$inherit
                            (l_superTVersionId, ai_tVersionId);
                    END IF; -- if inherit from super tVersion

                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'check for other records';
                    RAISE;              -- call common exception handler
                END;
            END IF; -- if not inherited from another tVersion

        -- finish the transaction:
        IF (l_retValue <> c_ALL_RIGHT AND
            l_retValue <> c_OBJECTNOTFOUND)
                                        -- there occurred a severe error?
        THEN
            ROLLBACK;                   -- undo changes
        ELSE                            -- there occurred no error
            COMMIT WORK;                -- make changes permanent
        END IF; -- if finish the transaction:
    END IF; -- if everything o.k.

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId' || ai_tVersionId ||
            ', ai_tabCode' || ai_tabCode ||
            ', ai_inheritFromSuper' || ai_inheritFromSuper ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$delete', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$delete;
/
show errors;


/******************************************************************************
 * Delete all occurrences of a code out of the ConsistsOf table. <BR>
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_tabCode          The code of the tab to be deleted.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_ConsistsOf$deleteCode
(
    -- input parameters:
    ai_tabCode              VARCHAR2
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

BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- delete the entries of the tab from the ConsistsOf table:
    BEGIN
        DELETE  ibs_ConsistsOf
        WHERE   tabId IN     
                (   SELECT  id
                    FROM    ibs_Tab
                    WHERE   code = ai_tabCode
                );

    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'delete';
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
            '; ai_tabCode' || ai_tabCode ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$deleteCode', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$deleteCode;
/
show errors;


/******************************************************************************
 * Delete all occurrences of a specific tVersion out of the ConsistsOf table.
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
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_ConsistsOf$deleteTVersion
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
    SAVEPOINT s_ConsistsOf$deleteTVersion;

    -- get the data of the tVersion:
    BEGIN
        SELECT  superTVersionId
        INTO    l_superTVersionId
        FROM    ibs_TVersion
        WHERE   id = ai_tVersionId;

        -- at this point we know that the super tVersion was found.
        -- inherit all entries from the super tVersion:
        -- the consequence of this action is, that no sub tVersion will have
        -- inherited values from this tVersion
        l_retValue :=
            p_ConsistsOf$inherit (l_superTVersionId, ai_tVersionId);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the super tVersion was not found
            -- delete the entries of the actual tVersion and all entries which were
            -- inherited from this tVersion from the consists of table:
            BEGIN
                DELETE  ibs_ConsistsOf
                WHERE   tVersionId = ai_tVersionId
                    OR  inheritedFrom = ai_tVersionId;

            EXCEPTION
                WHEN OTHERS THEN            -- any error
                    -- create error entry:
                    l_ePos := 'delete';
                    RAISE;                  -- call common exception handler
            END;
        -- end when the super tVersion was not found
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get data of tVersion';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_ConsistsOf$deleteTVersion;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId' || ai_tVersionId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$deleteTVersion', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$deleteTVersion;
/
show errors;


/******************************************************************************
 * Delete all occurrences of tVersions belonging to a specific type out of the
 * ConsistsOf table. <BR>
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
CREATE OR REPLACE FUNCTION p_ConsistsOf$deleteType
(
    -- input parameters:
    ai_typeId               INTEGER
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
    l_tVersionId            INTEGER;        -- tVersionId of the tab
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
            l_retValue := p_ConsistsOf$deleteTVersion (l_tVersionId);
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
        ibs_error.log_error (ibs_error.error, 'p_ConsistsOf$deleteType', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_ConsistsOf$deleteType;
/

show errors;

EXIT;
