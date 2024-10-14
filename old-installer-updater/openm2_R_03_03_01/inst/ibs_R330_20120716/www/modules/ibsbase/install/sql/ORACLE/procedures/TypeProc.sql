/******************************************************************************
 * All stored procedures regarding the type table. <BR>
 * 
 * @version     2.21.0014, 20.03.2002 KR
 *
 * @author      Klaus Reimüller (KR)  980608
 ******************************************************************************
 */


/******************************************************************************
 * Create a new type. <BR>
 * This procedure also creates a first version of the type. <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_id               Id of the type to be created.
 *                              null => create new id.
 * @param   ai_name             Name of the type.
 * @param   ai_superTypeCode    Super type of the type. '' => default super type
 *                              If there exists a type called 'BusinessObject'
 *                              this type is generally the super type of all
 *                              other types.
 *                              If this type does not exist, but there exists
 *                              another type having no super type that type is
 *                              used as super type of the new type.
 * @param   ai_isContainer      Is the type a container. 1 => true
 * @param   ai_isInheritable    May the type be inherited, i.e. a sub type be
 *                              created?
 * @param   ai_isSearchable     Is it possible to search for object of the type?
 * @param   ai_showInMenu       May instances of this type be displayed in a
 *                              menu?
 * @param   ai_showInNews       May instances of this type be included in the
 *                              news?
 * @param   ai_code             Code of the type (unique name).
 * @param   ai_className        Name of class which is responsible for the
 *                              actual (= first, if type is new) version of the
 *                              type.
 *
 * @output parameters:
 * @param   ao_newId            New id = @id if @id <> null, a newly generated
 *                              id otherwise.
 * @param   ao_newActVersionId  Id of the actual version for this type.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 *                          (=> The data of the type is changed instead of newly
 *                          created.)
 */
CREATE OR REPLACE FUNCTION p_Type$new 
( 
    -- input parameters: 
    ai_id                   INTEGER,
    ai_name                 VARCHAR2,
    ai_superTypeCode        VARCHAR2,
    ai_isContainer          NUMBER,
    ai_isInheritable        NUMBER,
    ai_isSearchable         NUMBER,
    ai_showInMenu           NUMBER,
    ai_showInNews           NUMBER,
    ai_code                 VARCHAR2,
    ai_className            VARCHAR2,
    -- output parameters:
    ao_newId                OUT INTEGER,
    ao_newActVersionId      OUT INTEGER
) 
RETURN INTEGER 
AS 
    -- constants: 
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- state of active object
    c_EMPTYPOSNOPATH        CONSTANT RAW (254) := hexToRaw ('0');
                                            -- default/invalid posNoPath
    c_noTypeId              CONSTANT INTEGER := 0; -- if of no type
    c_CommonSuperType       CONSTANT VARCHAR2 (63) := 'BusinessObject';
                                            -- name of common super type
 
    -- local variables: 
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_id                    INTEGER := ai_id; -- the id of the type
    l_superTypeId           INTEGER := 0;   -- id of super type of the
                                            -- actual type
    l_count                 INTEGER := 0;   -- counter
     
-- body:
BEGIN
    -- initialize return values:
    ao_newId := 0;

    COMMIT WORK; -- finish previous and begin new TRANSACTION

    BEGIN
        -- check if a type with this id already exists:
        SELECT  id, actVersion
        INTO    l_id, ao_newActVersionId
        FROM    ibs_Type
        WHERE   (   ai_id <> c_noTypeId     -- id set
                AND id = ai_id
                )
                OR
                (   ai_id = c_noTypeId      -- no id set => use code
                AND code = ai_code
                );

        -- a type with this id already exists
        BEGIN
            -- update typecode, showInMenu and IsInheritable which is set in
            -- createBaseObjectTypes
            UPDATE  ibs_Type
            SET     isInheritable = ai_isInheritable,
                    isSearchable = ai_isSearchable,
                    showInMenu = ai_showInMenu,
                    showInNews = ai_showInNews,
                    name = ai_name
            WHERE   id = l_id;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'update type ' || l_id;
                RAISE;                  -- call common exception handler
        END;


        BEGIN
            -- update data within ibs_TVersion:
            UPDATE  ibs_TVersion
            SET     className = ai_className
            WHERE   id IN
                    (
                        SELECT  actVersion
                        FROM    ibs_Type
                        WHERE   id = l_id
                    );
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'update tversion for type ' || l_id;
                RAISE;                  -- call common exception handler
        END;

        -- set return value:
        l_retValue := c_ALREADY_EXISTS;

    EXCEPTION 
        WHEN NO_DATA_FOUND THEN         -- type id not already there
            BEGIN
                -- get the super type id:
                SELECT  id
                INTO    l_superTypeId
                FROM    ibs_Type
                WHERE   code = ai_superTypeCode;
            EXCEPTION 
                WHEN NO_DATA_FOUND THEN -- no correct super type id
                    BEGIN
                        -- get default super type:
                        SELECT  id
                        INTO    l_superTypeId
                        FROM    ibs_Type
                        WHERE   code = c_CommonSuperType;
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN -- type not found
                            -- check if there exists at least one type and get
                            -- the minimum id of all types having no super
                            -- types:
                            BEGIN
                                SELECT  MIN (id)
                                INTO    l_superTypeId
                                FROM    ibs_Type
                                WHERE   superTypeId = c_noTypeId;
                            EXCEPTION
                                WHEN NO_DATA_FOUND THEN -- no type found
                                    -- actual type has no super type:
                                    l_superTypeId := c_noTypeId;
                                WHEN OTHERS THEN -- any error
                                    -- create error entry:
                                    l_ePos := 'select minimum super type';
                                    RAISE;  -- call common exception handler
                            END;
                        -- when type not found

                        WHEN OTHERS THEN    -- any error
                            -- create error entry:
                            l_ePos := 'select default super type';
                            RAISE;          -- call common exception handler
                    END;
                -- when no correct super type id
            END;

            -- add the new type:
            -- (the id of the type itself is set as actual tVersionId until
            -- a version of the exists)
            BEGIN
                INSERT INTO ibs_Type
                        (id, state, name, idProperty,
                        superTypeId, posNo, posNoPath,
                        isContainer, isInheritable, isSearchable,
                        showInMenu, showInNews, code, nextPropertySeq,
                        actVersion, validUntil)
                VALUES (l_id, c_ST_ACTIVE, ai_name, 1,
                        l_superTypeId, 0, c_EMPTYPOSNOPATH,
                        ai_isContainer, ai_isInheritable, ai_isSearchable,
                        ai_showInMenu, ai_showInNews, ai_code, 1,
                        l_id, ADD_MONTHS (SYSDATE, 120));
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'insert error for l_id ' || l_id;
                    RAISE;              -- call common exception handler
            END;

            -- remember number of inserted rows:
            l_count := SQL%ROWCOUNT;

            -- check if the row was correctly inserted:
            IF (l_count = 1)            -- type was inserted correctly?
            THEN
                -- get the type id:
                BEGIN
                    SELECT  MAX (id)
                    INTO    l_id
                    FROM    ibs_Type
                    WHERE   code = ai_code;
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'get type id';
                        RAISE;          -- call common exception handler
                END;

                -- create the new type version:
                l_retValue := p_TVersion$new (l_id, ai_code, ai_className,
                    ao_newActVersionId);

                -- check if there was an error during creating the type version:
                IF (l_retValue = c_ALL_RIGHT) -- type version created?
                THEN
                    -- set the type version within the type:
                    BEGIN
                        UPDATE  ibs_Type
                        SET     actVersion = ao_newActVersionId
                        WHERE   id = l_id;
                    EXCEPTION
                        WHEN OTHERS THEN -- any error
                            -- create error entry:
                            l_ePos := 'set type version error for' ||
                                ' l_id = ' || l_id ||
                                ', ao_newActVersionId = ' || ao_newActVersionId;
                            RAISE;      -- call common exception handler
                    END;

                    -- inherit may contain entries from super type:
                    IF (l_superTypeId <> c_noTypeId) -- super type exists?
                    THEN
                        l_retValue :=
                                p_MayContain$inherit (l_superTypeId, l_id);
                    END IF; -- super type exists
                END IF; -- type version created
            END IF; -- if type was inserted correctly
        -- when type id not already there

        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'select_update error for l_id = ' || l_id;
            RAISE;                      -- call common exception handler
    END;

    -- check if there occurred an error:
    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
                                        -- everything all right?
    THEN
        COMMIT WORK;                    -- make changes permanent
        ao_newId := l_id;               -- set new id
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
            '; ai_id = ' || ai_id ||
            ', ai_name = ' || ai_name ||
            ', ai_superTypeCode = ' || ai_superTypeCode ||
            ', ai_isContainer = ' || ai_isContainer ||
            ', ai_isInheritable = ' || ai_isInheritable ||
            ', ai_isSearchable = ' || ai_isSearchable ||
            ', ai_showInMenu = ' || ai_showInMenu ||
            ', ai_showInNews = ' || ai_showInNews ||
            ', ai_code = ' || ai_code ||
            ', ai_className = ' || ai_className ||
            ', l_id = ' || l_id ||
            ', ao_newId = ' || ao_newId ||
            ', ao_newActVersionId = ' || ao_newActVersionId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Type$new', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK; 
END p_Type$new;
/

show errors;


/******************************************************************************
 * Create a language dependent version of a type. <BR>
 * This procedure uses some mechanisms to get the required data and then
 * creates the type:
 * 1. The language dependent name of the type is determined.
 * 2. If there is no one found the type code is used as name of the type.
 * 3. The type is stored, i.e. if it exists it is changed, otherwise it is
 * created.
 *
 * @input parameters:
 * @param   ai_id               The id of the type.
 * @param   ai_superTypeCode    The code of the super type.
 * @param   ai_isContainer      Is an object of this type a container?
 * @param   ai_isInheritable    May there be a sub type of the type?
 * @param   ai_isSearchable     Is it possible to search for object of the type?
 * @param   ai_showInMenu       Is an object of this type be shown in the menu?
 * @param   ai_showInNews       May instances of this type be included in the
 *                              news?
 * @param   ai_code             The code of the type, i.e. its unique name
 * @param   ai_className        The name of the Java class which implements an
 *                              object of this type.
 * @param   ai_languageId       The language for which the type shall be
 *                              generated.
 * @param   ai_typeNameName     The id through which the language dependent name
 *                              of the type can be found.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Type$newLang
( 
    -- input parameters:
    ai_id                   INTEGER,
    ai_superTypeCode        VARCHAR2,
    ai_isContainer          NUMBER,
    ai_isInheritable        NUMBER,
    ai_isSearchable         NUMBER,
    ai_showInMenu           NUMBER,
    ai_showInNews           NUMBER,
    ai_code                 VARCHAR2,
    ai_className            VARCHAR2,
    ai_languageId           INTEGER,
    ai_typeNameName         VARCHAR2
    -- output parameters:
)
AS
    -- constants:

    -- local variables:
    l_retValue              INTEGER;        -- return value of a function
    l_id                    INTEGER;        -- the actual id
    l_actTVId               INTEGER;        -- the type version id
    l_typeName              VARCHAR2 (63) := ai_code; -- the name of the type
    l_typeClass             VARCHAR2 (255); -- the java class (deprecated)
    
-- body:
BEGIN
    -- create the type itself:
    p_TypeName_01$get (ai_languageId, ai_typeNameName,
        l_typeName, l_typeClass);
    IF (l_typeName IS NULL)
    THEN
        l_typeName := ai_code;
    END IF;

    l_retValue := p_Type$new (ai_id, l_typeName, ai_superTypeCode,
        ai_isContainer, ai_isInheritable, ai_isSearchable, ai_showInMenu,
        ai_showInNews, ai_code, ai_className,
        l_id, l_actTVId);
END p_Type$newLang;
/

show errors;


/******************************************************************************
 * Add some tabs to a type. <BR>
 * The tabs for the types are defined. There can be up to 10 tabs defined
 * for each type.
 * In the SQL Server version the tab parameters are optional. <BR>
 * This procedure gets the actual version of the type and calls
 * p_TVersion$addTabs for this type version.
 *
 * @input parameters:
 * @param   ai_code             The code of the type, i.e. its unique name
 * @param   ai_defaultTab       The code of the default tab.
 * @param   ai_tabCodeX         The code of the tab, i.e. the unique name.
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_Type$addTabs
(
    -- input parameters:
    ai_code                 VARCHAR2,
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
    c_ALL_RIGHT             INTEGER := 1;    -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_id                    INTEGER;        -- the actual id
    l_actTVId               INTEGER;        -- the type version id

-- body:
BEGIN
    -- get the type data:
    BEGIN
        SELECT  actVersion
        INTO    l_actTVId
        FROM    ibs_Type
        WHERE   code = ai_code;

    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get type data';
            RAISE;                      -- call common exception handler
    END;

    -- create the tabs:
    p_TVersion$addTabs (l_actTVId, ai_defaultTab, ai_tabCode1,
        ai_tabCode2, ai_tabCode3, ai_tabCode4, ai_tabCode5,
        ai_tabCode6, ai_tabCode7, ai_tabCode8, ai_tabCode9,
        ai_tabCode10);

EXCEPTION 
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_code' || ai_code ||
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
            ', l_actTVId = ' || l_actTVId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Type$addTabs', l_eText);
END p_Type$addTabs;
/

show errors;


/******************************************************************************
 * Create a new type (no rights check). <BR>
 * This procedure calls p_Type$new, which contains a TRANSACTION block, so it
 * is not allowed to call this procedure from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_superTypeId      Super type of the type. 0 => no super type
 * @param   ai_isContainer      Is the type a container. 1 => true
 * @param   ai_isInheritable    May the type be inherited, i.e. a sub type be
 *                              created?
 * @param   ai_isSearchable     Is it possible to search for object of the type?
 * @param   ai_showInMenu       May instances of this type be displayed in a
 *                              menu?
 * @param   ai_showInNews       May instances of this type be included in the
 *                              news?
 * @param   ai_code             Code of the type (unique name).
 * @param   ai_className        Name of class which is responsible for the
 *                              actual (= first, if type is new) version of the
 *                              type.
 * @param   ai_description      Description of the type.
 *
 * @output parameters:
 * @param   ao_id               Id of the newly created type.
 * @param   ao_actVersionId     Id of the actual version for the new type.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Type$create
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_superTypeId          INTEGER,
    ai_isContainer          NUMBER,
    ai_isInheritable        NUMBER,
    ai_isSearchable         NUMBER,
    ai_showInMenu           NUMBER,
    ai_showInNews           NUMBER,
    ai_code                 VARCHAR2,
    ai_className            VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters:
    ao_id                   OUT INTEGER,
    ao_actVersionId         OUT INTEGER
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
    l_superTypeCode         VARCHAR2 (63) := ''; -- the code of the super type

-- body:
BEGIN
    -- get the type code of the super type:
    SELECT  code
    INTO    l_superTypeCode
    FROM    ibs_Type
    WHERE   id = ai_superTypeId;

    -- create the new type and its first version:
    l_retValue := p_Type$new (0, ai_name, ai_superTypeId, 
        ai_isContainer, ai_isInheritable, ai_isSearchable, ai_showInMenu,
        ai_showInNews, ai_code, ai_className,
        ao_id, ao_actVersionId);

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op ||
            ', ai_name = ' || ai_name ||
            ', ai_superTypeId = ' || ai_superTypeId ||
            ', ai_isContainer = ' || ai_isContainer ||
            ', ai_isInheritable = ' || ai_isInheritable ||
            ', ai_showInMenu = ' || ai_showInMenu ||
            ', ai_code = ' || ai_code ||
            ', ai_className = ' || ai_className ||
            ', ai_description = ' || ai_description ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Type$create', l_eText);
        -- return error code:
        RETURN  c_NOT_OK;
END p_Type$create;
/

show errors;


/******************************************************************************
 * Change the attributes of an existing type (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_id               ID of the type to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_idProperty       Id of the property used to represent the id of
 *                              one object of this type.
 * @param   ai_superTypeId      Id of the superior type.
 * @param   ai_code             Code of the type.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- MISSING


/******************************************************************************
 * Get all data from a given type (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_id               Id of the type to be changed.
 * @param   ai_userId           Id of the user who is creating the type.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_name             Name of the object itself.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_idProperty       Id of the property used to represent the id of
 *                              one object of this type.
 * @param   ao_superTypeId      Id of the superior type.
 * @param   ao_isContainer      Is the type a container?
 * @param   ao_code             Code of the type.
 * @param   ao_nextPropertySeq  Sequence number of the next new property.
 * @param   ao_actVersionId     Id of the actual version.
 * @param   ao_actVersionSeq    Sequence number of the actual version.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- MISSING


/******************************************************************************
 * Show all types. <BR>
 *
 * @input parameters:
 *
 * @output parameters:
 */
CREATE OR REPLACE PROCEDURE p_showTypes
    -- input parameters:
    -- output parameters:
AS
    dummy1  RAW (4);
    dummy2  RAW (4);
    dummy3  RAW (4);
    dummy4  RAW (4);

-- body:
BEGIN
    -- get the types:
    SELECT  INTTORAW (id, 4) AS id,
            INTTORAW (actVersion, 4) AS tVersionId,
            INTTORAW (superTypeId, 4) AS superType,
            name
    INTO    dummy1, dummy2, dummy3, dummy4
    FROM    ibs_Type;
END p_showTypes;
/

show errors;


/******************************************************************************
 * Delete given type (including all its tversions). <BR>
 * 1. First ibs_Type is checked if there is an according id/code entry 
 *    If not: deletion will not be performed
 *    (returns TYPE_MISMATCH).
 * 2. Each tversions will be deleted - if there exist activ objects 
 *    with given type then no deletion will be performed 
 *    (returns ALREADY_EXISTS).
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_typeId           Type for which all type versions shall be
 *                              deleted.
 * @param   ai_code             Code of type (for security reasons)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_TYPE_MISMATCH          Given id/code is not valid (no entry).
 * c_ALREADY_EXISTS         Found objects with this type(version)
 *                          in ibs_Object - deletion not possible.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Type$deletePhysical
(
    -- input parameters:
    ai_typeId               INTEGER,
    ai_code                 VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists
    c_TYPE_MISMATCH         CONSTANT INTEGER := 99; -- a type mismatch occurred

    -- local variables:
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_retValue              INTEGER := c_ALL_RIGHT;
                                            -- return value of a function
    l_rowCount              INTEGER := 0;   -- row counter

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- check if the given id/code pair is an entry in ibs_Type:
    BEGIN
        SELECT  count (id)
        INTO    l_rowCount
        FROM    ibs_Type 
        WHERE   ai_code = code
            AND ai_typeId = id;

        -- check if the id/code pair was found:
        IF (l_rowCount = 0)                 -- didn't find the specific id/code
                                            -- pair?
        THEN
            -- set error code:
            l_retValue := c_TYPE_MISMATCH;

            -- insert into error log:
            ibs_error.log_error ( ibs_error.error, 'p_Type$deletePhysical',
                'Type Mismatch - :' ||
                ' ai_code = ' || ai_code ||
                ', ai_typeId = ' || ai_typeId ||
                '; errorcode = ' || SQLCODE ||
                ', errormessage = ' || SQLERRM );
        ELSE                                -- id/code pair found
            -- type can be deleted
            COMMIT WORK; -- finish previous and begin new TRANSACTION

            -- delete all type versions of given type
            -- procedure possibly returns c_ALREADY_EXISTS
            l_retValue := p_tVersion$deletePhysical (ai_typeid);

            -- problems while deleting tversions?
            IF (l_retValue = c_ALL_RIGHT)   -- everything all right?
            THEN
                -- delete the may contain entries for the type:
                l_retValue := p_MayContain$deleteType (ai_typeId);

                IF (l_retValue = c_ALL_RIGHT)   -- everything all right?
                THEN
                    -- delete the type:
                    BEGIN
                        DELETE ibs_Type
                        WHERE  id = ai_typeId;
                    EXCEPTION
                        WHEN OTHERS THEN    -- any error
                            -- create error entry:
                            l_ePos :=
                                'Error when deleting ibs_Type entry:' ||
                                ' ai_typeId = ' || ai_typeId;
                            RAISE;          -- call common exception handler
                    END;
                END IF; -- everything all right
            END IF; -- everything all right

            -- check if there occurred an error:
            IF (l_retValue = c_ALL_RIGHT)   -- everything all right?
            THEN
                COMMIT WORK;                -- make changes permanent
            ELSE                            -- an error occured
                ROLLBACK;                   -- undo changes
            END IF; -- else an error occurred
        END IF; -- else id/code pair found

    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos :=
                'Check id/code pair for ai_typeId ' || ai_typeId ||
                ', ai_code = ' || ai_code;
            RAISE;                      -- call common exception handler
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
            '; ai_code = ' || ai_code ||
            ', ai_typeId = ' || ai_typeId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Type$deletePhysical', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Type$deletePhysical;
/

show errors;

EXIT;
