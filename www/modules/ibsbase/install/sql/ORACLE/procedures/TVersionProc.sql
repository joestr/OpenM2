/******************************************************************************
 * All stored procedures regarding the tVersion table. <BR>
 * 
 * @version     2.21.0007, 21.06.2002 KR
 *
 * @author      Mario Stegbauer (MS)  980528
 ******************************************************************************
 */


/******************************************************************************
 * Create a new type version. <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_typeId           Type for which a new version shall be created.
 * @param   ai_code             Code of the type.
 * @param   ai_className        Name of java class (incl. packages) which
 *                              implements the business logic of the new type
 *                              version.
 *
 * @output parameters:
 * @return  ao_id               id of the newly created tVersion.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
CREATE OR REPLACE FUNCTION p_TVersion$new 
( 
    -- input parameters: 
    ai_typeId               INTEGER,
    ai_code                 VARCHAR2,
    ai_className            VARCHAR2,
    -- output parameters: 
    ao_id                   OUT INTEGER
) 
RETURN INTEGER 
AS 
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_noTVersionId          INTEGER := 0;   -- no version id

    -- local variables:
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_superTVersionId       INTEGER;        -- id of actual version of super
                                            -- type

BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- get the id of the actual version of the super type
    -- (if there exists a super type):
    BEGIN
        SELECT  DECODE (st.actVersion, NULL, c_noTVersionId, st.actVersion) 
        INTO    l_superTVersionId 
        FROM    ibs_Type t, ibs_Type st 
        WHERE   t.id = ai_typeId 
            AND st.id(+) = t.superTypeId; 
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'SELECT superTVersionId';
            RAISE;                      -- call common exception handler
    END;

    -- store the tVersion's data in the table:
    -- within this step the following computations are done:
    -- + the state is set to active
    -- + the code is computed
    -- + the idProperty is initialized
    -- + the nextObjectSeq is initialized
    BEGIN 
        INSERT INTO ibs_TVersion
                (state, typeId, idProperty, superTVersionId,
                className, nextObjectSeq)
        VALUES  (2, ai_typeId, 0, l_superTVersionId,
                ai_className, 1);
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'INSERT';
            RAISE;                      -- call common exception handler
    END;

    -- get the newly created id:
    BEGIN
        SELECT  MAX (id)
        INTO    ao_id
        FROM    ibs_TVersion
        WHERE   typeId = ai_typeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'SELECT ao_id';
            RAISE;                      -- call common exception handler
    END;

    -- set the code:
    BEGIN
        UPDATE  ibs_TVersion
        SET     code = ai_code ||
                REPLACE (TO_CHAR (tVersionSeq, 'S00'), '+', '_')
        WHERE   id = ao_id;
    EXCEPTION
        WHEN OTHERS THEN            -- any error
            -- log the error:
            l_ePos := 'set code';
            RAISE;                  -- call common exception handler
    END;

    IF (l_superTVersionId <> c_noTVersionId) -- super tVersion exists?
    THEN    
        -- inherit tVersionProc entries from super tVersion:
        l_retValue := p_TVersionProc$inherit (l_superTVersionId, ao_id);
        -- inherit ConsistsOf entries from super tVersion:
        l_retValue := p_ConsistsOf$inherit (l_superTVersionId, ao_id);
    END if;
    
    -- check if there occurred an error:
    IF (l_retValue <> c_ALL_RIGHT)      -- an error occurred?
    THEN
        ROLLBACK;                       -- undo changes
    END IF; -- if an error occurred

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
            '; ai_typeId = ' || ai_typeId ||
            ', ai_code = ' || ai_code ||
            ', ai_className = ' || ai_className ||
            ', ao_id = ' || ao_id ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersion$new', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersion$new; 
/

show errors;

/******************************************************************************
 * Add some tabs to a type. <BR>
 * The tabs for the types are defined. There can be up to 10 tabs defined
 * for each type.
 * In the SQL Server version the tab parameters are optional. <BR>
 * If the code of the defaultTab is not defined or it is not valid the tab
 * with the highest priority is set as default tab.
 *
 * @input parameters:
 * @param   ai_tVersionId       The id of the type version for which to add the
 *                              tabs.
 * @param   ai_defaultTab       The code of the default tab.
 * @param   ai_tabCodeX         The code of the tab, i.e. the unique name.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_TVersion$addTabs
(
    -- input parameters:
    ai_tVersionId           INTEGER,
    ai_defaultTab           VARCHAR2,
    ai_tabCode1             VARCHAR2,
    ai_tabCode2             VARCHAR2,
    ai_tabCode3             VARCHAR2,
    ai_tabCode4             VARCHAR2,
    ai_tabCode5             VARCHAR2,
    ai_tabCode6             VARCHAR2,
    ai_tabCode7             VARCHAR2,
    ai_tabCode8             VARCHAR2,
    ai_tabCode9             VARCHAR2,
    ai_tabCode10            VARCHAR2
    -- output parameters:
)
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text    
    l_id                    INTEGER;        -- the actual id

-- body:
BEGIN
/* ================
    KR:
    It is not allowed to delete the currently existing tabs because this
    procedure is used for incremental defining of tabs.
    -- drop all existing tabs:
    l_retValue := p_ConsistsOf$deleteTVersion (ai_TVersionId);
================ */

    -- create the tabs:
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode1 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode1, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode2 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode2, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode3 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode3, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode4 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode4, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode5 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode5, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode6 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode6, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode7 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode7, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode8 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode8, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode9 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode9, l_id);
    END IF; -- if
    IF ((l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND ai_tabCode10 IS NOT NULL)
    THEN
        l_retValue := p_ConsistsOf$newCode
            (ai_tVersionId, ai_tabCode10, l_id);
    END IF; -- if

    -- check for error:
    IF (l_retValue = c_ALREADY_EXISTS) -- no severe error?
    THEN
        -- everything o.k..
        l_retValue := c_ALL_RIGHT;
    END IF; -- if no severe error

    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything o.k.?
    THEN
        -- get the data of the default tab:
        l_id := 0;                      -- initializiation
        BEGIN
            SELECT  DECODE (c.id, NULL, 0, c.id)
            INTO    l_id
            FROM    ibs_ConsistsOf c, ibs_Tab t
            WHERE   t.code = ai_defaultTab
                AND t.id = c.tabId
                AND c.tVersionId = ai_tVersionId;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- the tab was not found
                -- get the tab with the highest priority:
                BEGIN
                    SELECT  DECODE (MAX (id), NULL, 0, MAX (id))
                    INTO    l_id
                    FROM    ibs_ConsistsOf
                    WHERE   tVersionId = ai_tVersionId
                        AND priority >=
                            (
                                SELECT  MAX (priority)
                                FROM    ibs_ConsistsOf
                                WHERE   tVersionId = ai_tVersionId
                            );

                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'get tab with highest priority';
                        RAISE;          -- call common exception handler  
                END;
            -- end when the tab was not found
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get data of default tab';
                RAISE;                  -- call common exception handler  
        END;

        -- set the active tab:
        BEGIN
            UPDATE  ibs_TVersion
            SET     defaultTab = l_id
            WHERE   id = ai_tVersionId;

        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'set the default tab';
                RAISE;                  -- call common exception handler 
        END;
    END IF; -- if everything o.k.

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId = ' || ai_tVersionId ||
            ', ai_defaultTab = ' || ai_defaultTab ||
            ', ai_tabCode1 = ' || ai_tabCode1 ||
            ', ai_tabCode2 = ' || ai_tabCode2 ||
            ', ai_tabCode3 = ' || ai_tabCode3 ||
            ', ai_tabCode4 = ' || ai_tabCode4 ||
            ', ai_tabCode5 = ' || ai_tabCode5 ||
            ', ai_tabCode6 = ' || ai_tabCode6 ||
            ', ai_tabCode7 = ' || ai_tabCode7 ||
            ', ai_tabCode8 = ' || ai_tabCode8 ||
            ', ai_tabCode9 = ' || ai_tabCode9 ||
            ', ai_tabCode10 = ' || ai_tabCode10 ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersion$addTabs', l_eText);
END p_TVersion$addTabs; 
/

show errors;


/******************************************************************************
 * Delete all type-versions of given type, including any entries in
 * ibs_ConsistsOf regarding this tVersion. <BR>
 *
 * IMPORTANT: should ONLY be called from p_Type$deletePhysical
 *
 * @input parameters:
 * @param   ai_typeId              Type for which all type versions shall be
 *                                 deleted.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  ALREADY_EXISTS          Found objects with this type(version)
 *                          in ibs_Object - deletion not possible
 */
CREATE OR REPLACE FUNCTION p_TVersion$deletePhysical
(
    -- input parameters:
    ai_typeId               INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_eText                 VARCHAR2 (5000); -- full error text    
    l_ePos                  VARCHAR2 (255); -- error position description      
                                            -- return value of a function
    l_rowCount              INTEGER := 0;   -- row counter

BEGIN
-- body:
    -- check if there exist active objects with 
    -- tversionid(s) of given code in ibs_Object
    BEGIN
        SELECT  count (o.oid)
        INTO    l_rowCount
        FROM    ibs_Object o, ibs_tVersion t
        WHERE   t.typeId = ai_typeId
            AND t.id = o.tVersionId
            AND o.state = 2;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check if active objects exist';
            RAISE;                      -- call common exception handler  
    END;

    IF (l_rowCount > 0) THEN
        BEGIN
            -- found tversion id(s) in ibs_object
            l_retValue := c_ALREADY_EXISTS;

            -- insert message into error log
            ibs_error.log_error ( ibs_error.error, 'p_TVersion$deletePhysical',
                         'Can not delete TVersion - found objects in ibs_Object: ' ||
                         ', ai_typeId = ' || ai_typeId ||
                         ', errorcode = ' || SQLCODE ||
                         ', errormessage = ' || SQLERRM );
        END;
    ELSE                                -- no active objects exist with given
                                        -- tVersion id(s)
        -- delete all consistsOf entries of given tVersion:
        l_retValue := p_ConsistsOf$deleteType (ai_typeId);

        -- check if there occurred an error:
        IF (l_retValue = c_ALL_RIGHT)   -- everything o.k.?
        THEN
            -- delete the tVersionProc entries for the tVersions of the type:
            l_retValue := p_TVersionProc$deleteType (ai_typeId);

            -- check if there occurred an error:
            IF (l_retValue = c_ALL_RIGHT) -- everything o.k.?
            THEN
                -- delete all entries with given type:
                BEGIN
                    DELETE  ibs_TVersion
                    WHERE   typeId = ai_typeId;
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'delete';
                        RAISE;          -- call common exception handler  
                END;
            END IF; -- if everything o.k.
        END IF; -- if everything o.k.
    END IF; -- if no active objects

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_typeId = ' || ai_typeId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TVersion$deletePhysical', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_TVersion$deletePhysical; 
/

show errors;   

EXIT; 
