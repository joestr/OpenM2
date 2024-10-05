/******************************************************************************
 * All stored procedures regarding the tab table. <BR>
 * 
 * @version     2.2.1.0006, 19.03.2002 KR
 *
 * @author      Andreas Jansa (AJ)  991027
 ******************************************************************************
 */


/******************************************************************************
 * Create a new tab. <BR>
 * If a tab with the same domainId (or in domain 0) and the specified code
 * exists already nothing is done and c_ALREADY_EXISTS is returned.
 *
 * @input parameters:
 * @param   ai_domainId         Id of domain in which the tab is valid.
 *                              0 ... tab is valid in all domains
 * @param   ai_code             The tab code. This code is unique within each
 *                              domain.
 * @param   ai_kind             Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
 * @param   ai_tVersionId       tVersionId of object which is representing the
 *                              tab (just for tabs of type OBJECT).
 * @param   ai_fct              Function to be performed when tab is selected.
 * @param   ai_priority         Priority of the tab (+oo ... -oo).
 * @param   ai_multilangKey     The code of the tab in the multilang table.
 * @param   ai_rights           Necessary permissions for the tab to be
 *                              displayed.
 *                              0 ... no rights necessary
 * @param   ai_class            the class to show viewtab
 *
 * @output parameters:
 * @param   ao_id               Id of the newly generated tab.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A tab with this code already exists.
 */
-- create the procedure:
CREATE OR REPLACE FUNCTION p_Tab$new
(
    -- input parameters:
    ai_domainId             INTEGER,
    ai_code                 VARCHAR2,
    ai_kind                 INTEGER,
    ai_tVersionId           INTEGER,
    ai_fct                  INTEGER,
    ai_priority             INTEGER,
    ai_multilangKey         VARCHAR2,
    ai_rights               INTEGER,
    ai_class                VARCHAR2,
    -- output parameters:
    ao_id                   OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;-- the object already exists

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_kind                  INTEGER := ai_kind; -- kind of the tab

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

        BEGIN
            -- check if the tab exists already:
            SELECT  MAX (id)
            INTO    ao_id
            FROM    ibs_Tab
            WHERE   (   ai_domainId = 0 -- domain independent tab
                    AND code = ai_code
                    )
                OR  (   ai_domainId <> 0 -- domain restricted tab
                    AND domainId = ai_domainId
                    AND code = ai_code
                    );

            -- check if id was found:
            IF (ao_id IS NULL)          -- no id found?
            THEN
                ao_id := -1;            -- set corresponding return value
            END IF; -- no id found
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- no id found?
                ao_id := -1;            -- set corresponding return value
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check if tab exists already';
                RAISE;                  -- call common exception handler
        END;

        -- check if the tab was found:
        IF (ao_id = -1)                 -- tab was not found?
        THEN
            -- the tab may be inserted

            BEGIN
                -- add the tab to the table:
                INSERT INTO ibs_Tab
                        (domainId, code, kind, tVersionId, fct,
                        priority, multilangKey, rights, class)
                VALUES (ai_domainId, ai_code, l_kind, ai_tVersionId, ai_fct,
                        ai_priority, ai_multilangKey, ai_rights, ai_class);
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'insert tab into ibs_Tab';
                    RAISE;              -- call common exception handler
            END;

            BEGIN                        
                -- get the new tab id:
                SELECT  MAX (id)
                INTO    ao_id
                FROM    ibs_Tab
                WHERE   domainId = ai_domainId
                    AND code = ai_code;

                -- check if id was found:
                IF (ao_id = -1 OR ao_id IS NULL) -- no id found?
                THEN
                    ao_id := -1;        -- set corresponding return value
                END IF; -- no id found
            EXCEPTION
                WHEN NO_DATA_FOUND THEN -- no id found?
                    ao_id := -1;        -- set corresponding return value
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'get new tab id';
                    RAISE;              -- call common exception handler
            END;
        ELSE                            -- the tab exists already
            -- set return value:
            l_retValue := c_ALREADY_EXISTS;
        END IF; -- else the tab exists already

    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
    END IF; -- end if everything all right?

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_domainId' || ai_domainId ||
            ', ai_code' || ai_code ||
            ', ai_kind' || ai_kind ||
            ', ai_multilangKey' || ai_multilangKey ||
            ', ai_tVersionId' || ai_tVersionId ||
            ', ai_fct' || ai_fct ||
            ', ai_priority' || ai_priority ||
            ', ai_rights' || ai_rights ||
            ', ao_id' || ao_id ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Tab$new', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Tab$new;
/

show errors;


/******************************************************************************
 * Set the attributes of an existing tab. <BR>
 *
 * @input parameters:
 * @param   ai_domainId         Id of domain in which the tab shall be changed.
 *                              0 ... tab is valid in all domains
 * @param   ai_code             The tab code. This code is unique within each
 *                              domain.
 * @param   ai_kind             New Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
 * @param   ai_tVersionId       New tVersionId of object which is representing
 *                              the tab (just for tabs of type OBJECT).
 * @param   ai_fct              New Function to be performed when tab is
 *                              selected.
 * @param   ai_priority         New Priority of the tab (+oo ... -oo).
 * @param   ai_multilangKey     New code of the tab in the multilang table.
 * @param   ai_rights           New permissions for the tab to be
 *                              displayed.
 *                              0 ... no rights necessary
 * @param   ai_class            class to show view tab
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_OBJECTNOTFOUND        The required object was not found within the 
 *                          database.
 */
-- create the procedure:
CREATE OR REPLACE FUNCTION p_Tab$set
(
    -- input parameters:
    ai_domainId             INTEGER,
    ai_code                 VARCHAR2,
    ai_kind                 INTEGER,
    ai_tVersionId           INTEGER,
    ai_fct                  INTEGER,
    ai_priority             INTEGER,
    ai_multilangKey         VARCHAR2,
    ai_rights               INTEGER,
    ai_class                VARCHAR2
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;  -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_eText                 VARCHAR2 (5000); -- full error text
    l_ePos                  VARCHAR2 (255); -- error position description
    l_id                    INTEGER;        -- the id of the tab

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

        BEGIN
            -- check if the tab exists already:
            SELECT  MAX (id)
            INTO    l_id
            FROM    ibs_Tab
            WHERE   domainId = ai_domainId
                AND code = ai_code;


            -- check if id was found:
            IF (l_id IS NULL)           -- no id found?
            THEN
                l_id := -1;             -- set corresponding value
            END IF; -- no id found
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- no id found?
                l_id := -1;             -- set corresponding value
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check if tab exists already';
                RAISE;  
        END;

        -- check if the tab was found:
        IF (l_id = -1)                  -- tab was not found?
        THEN
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
        ELSE                            -- the tab exists already    
            -- the tab may be inserted
            -- change the tab data:
            BEGIN
                UPDATE  ibs_Tab
                SET     kind = ai_kind,
                        tVersionId = ai_tVersionId,
                        fct = ai_fct,
                        priority = ai_priority,
                        multilangKey = ai_multilangKey,
                        rights = ai_rights,
                        class = ai_class
                WHERE   id = l_id;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'change tab data';
                    RAISE;              -- call common exception handler
            END;
        END IF; -- else the tab exists already

    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT)       -- everything all right?
    THEN       
        COMMIT;                         -- make changes permanent
    ELSE                                -- an error occured
        ROLLBACK;                       -- undo changes
    END IF; --end if everything all right

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_domainId' || ai_domainId ||
            ', ai_code' || ai_code ||
            ', ai_kind' || ai_kind ||
            ', ai_multilangKey' || ai_multilangKey ||
            ', ai_tVersionId' || ai_tVersionId ||
            ', ai_fct' || ai_fct ||
            ', ai_priority' || ai_priority ||
            ', ai_rights' || ai_rights ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Tab$set', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Tab$set;
/

show errors;


/******************************************************************************
 * Get the data from a given tab. <BR>
 * This procedure gets a tuple out of ibs_Tab by using the domainId and the
 * code together as unique key.
 * If there is no tuple found the return value is c_OBJECTNOTFOUND.
 *
 * @input parameters:
 * @param   ai_domainId         Id of domain in which the tab shall be changed.
 *                              0 ... tab is valid in all domains
 * @param   ai_code             The tab code. This code is unique within each
 *                              domain.
 *
 * @output parameters:
 * @param   ao_id               The id of the tab
 * @param   ao_kind             Kind of tab (VIEW, OBJECT, LINK, FUNCTION).
 * @param   ao_tVersionId       tVersionId of object which is representing
 *                              the tab (just for tabs of type OBJECT).
 * @param   ao_fct              Function to be performed when tab is
 *                              selected.
 * @param   ao_priority         Priority of the tab (+oo ... -oo).
 * @param   ao_multilangKey     Code of the tab in the multilang table.
 * @param   ao_rights           Permissions for the tab to be displayed.
 *                              0 ... no rights necessary
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_OBJECTNOTFOUND        The required object was not found within the 
 *                          database.
 */
-- create the procedure:
CREATE OR REPLACE FUNCTION p_Tab$get
(
    -- input parameters:
    ai_domainId             INTEGER,
    ai_code                 VARCHAR2,
    -- output parameters:
    ao_id                   OUT INTEGER,
    ao_kind                 OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_fct                  OUT INTEGER,
    ao_priority             OUT INTEGER,
    ao_multilangKey         OUT VARCHAR2,
    ao_rights               OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;  -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1;  -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;  -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text

-- body:
BEGIN
    -- check if the tab exists:
    -- get the tab data:
    BEGIN
        SELECT  id, kind, tVersionId, fct, priority,
                multilangKey, rights
        INTO    ao_id, ao_kind, ao_tVersionId, ao_fct, ao_priority,
                ao_multilangKey, ao_rights
        FROM    ibs_Tab
        WHERE   domainId = ai_domainId
            AND code = ai_code;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- tab was not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
        -- when tab was not found            
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check if tab exists';
            RAISE;  
    END;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_domainId' || ai_domainId ||
            ', ai_code' || ai_code ||
            ', ao_id' || ao_id ||
            ', ao_multilangKey' || ao_multilangKey ||
            ', ao_kind' || ao_kind ||
            ', ao_tVersionId' || ao_tVersionId ||
            ', ao_fct' || ao_fct ||
            ', ao_rights' || ao_rights ||
            ', ao_priority' || ao_priority ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Tab$get', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_Tab$get;
/

show errors;


/******************************************************************************
 * Get the code from a given taboid. <BR>
 * This procedure gets a tuple out of ibs_Tab by using the taboid at ibs_object.
 *
 * If there is no tuple found the return value is c_OBJECTNOTFOUND.
 *
 * @input parameters:
 * @param   ai_tabOid           Oid of the tab to get the typecode
 * 
 *
 * @output parameters:
 * @param   ao_tabCode          code of the tab
 * 
 * 
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_OBJECTNOTFOUND        The required object was not found within the 
 *                          database.
 */
-- create the procedure:
CREATE OR REPLACE FUNCTION p_Tab$getCodeFromOid
(
    -- input parameters:
    ai_tabOid               VARCHAR2,
    -- output parameters:  
    ao_tabCode              OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_tabOid                RAW (8);        -- tabOid to hold the oid 

-- body:
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_tabOid, l_tabOid);
    
    -- get the code
    BEGIN
    	SELECT  t.code
    	INTO    ao_tabCode
    	FROM    ibs_Tab t, ibs_ConsistsOf c, ibs_Object o
    	WHERE   t.id = c.tabId
    	    AND c.id = o.consistsOfId
    	    AND o.oid = l_tabOid;

    	-- check if the tab was found:
        IF (ao_tabCode LIKE '')         -- tab was not found?
        THEN
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
        END IF; -- if tab was not found
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- tab was not found?
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
        -- when tab was not found            
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'check if tab exists';
            RAISE;  
    END;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tabOid' || ai_tabOid ||
            ', ao_tabCode' || ao_tabCode ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Tab$getCodeFromOid', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_Tab$getCodeFromOid;
/

show errors;

EXIT;
