/******************************************************************************
 * All stored procedures regarding the mayContain table. <BR>
 * 
 * @version     1.10.0001, 26.06.2000
 *
 * @author      Rahul Soni (RS)  000626
 ******************************************************************************
 */

/******************************************************************************
 * Inherit the tuples from one type to another type. <BR>
 * If there are any types currently inheriting their tuples from the second
 * type they will also inherit their tuples from the first type.
 * This function must be called from within a transaction handled code block
 * because it uses savepoints.
 *
 * @input parameters:
 * @param   ai_majorTypeId      Id of the major type from which the tuples shall
 *                              be inherited.
 * @param   ai_minorTypeId      Id of minor type to which the tuples shall be
 *                              inherited.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_MayContain$inherit
(
    -- input parameters:
    ai_majorTypeId          INTEGER,
    ai_minorTypeId          INTEGER
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables: 
    l_retValue              INTEGER := c_ALL_RIGHT; 
                                            -- return value of this function
--    l_majorPosNoPath        RAW (254);      -- the pos no path of the major type
    l_mayContainInheritedTypeId INTEGER;    -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    l_minorPosNoPath        RAW (254);      -- the pos no path of the minor type
    l_minorMayContainInhTypeId INTEGER;     -- id of type from which the actual
                                            -- minor type originally inherits
                                            -- its may contain records

BEGIN
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_MayContain$inherit;

    -- get the data of the type from which to inherit the tuples:
    BEGIN
        SELECT  mayContainInheritedTypeId
        INTO    l_mayContainInheritedTypeId
        FROM    ibs_Type
        WHERE   id = ai_majorTypeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$inherit.get major type data',
                'OTHER error for type ' || ai_majorTypeId);
            RAISE;
    END;

    -- get the data of the type to which to inherit the tuples:
    BEGIN
        SELECT  mayContainInheritedTypeId, posNoPath
        INTO    l_minorMayContainInhTypeId, l_minorPosNoPath
        FROM    ibs_Type
        WHERE   id = ai_minorTypeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$inherit.get minor type data',
                'OTHER error for type ' || ai_minorTypeId);
            RAISE;
    END;

    -- delete the current tuples of the minor type and all types which
    -- inherited their actual tuples from the minor type:
    BEGIN
        DELETE  ibs_MayContain
        WHERE   majorTypeId IN
                (
                    SELECT  id
                    FROM    ibs_Type
                    WHERE   id = ai_minorTypeId
                        OR  (   INSTR (posNoPath, l_minorPosNoPath, 1, 1) = 1
                            AND mayContainInheritedTypeId = 
                                    l_minorMayContainInhTypeId
                            )
                );
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$inherit.delete',
                'OTHER error for type ' || ai_minorTypeId);
            RAISE;
    END;

    -- inherit the entries from the major type to the minor type and all types
    -- which beforehand inherited their tuples from the minor type:
    -- insert the new (inherited) records into the may contain table:
    BEGIN
        INSERT INTO ibs_MayContain
                (majorTypeId, minorTypeId, isInherited)
        SELECT  t.id, m.minorTypeId, 1
        FROM    ibs_Type t, ibs_MayContain m
        WHERE   (
                    t.id = ai_minorTypeId
                OR  (   INSTR (posNoPath, l_minorPosNoPath, 1, 1) = 1
                    AND t.mayContainInheritedTypeId = l_minorMayContainInhTypeId
                    )
                )
            AND m.majorTypeId = ai_majorTypeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$inherit.insert',
                'OTHER error for ai_majorTypeId ' || ai_majorTypeId ||
                ' and ai_minorTypeId ' || ai_minorTypeId);
            RAISE;
    END;

    -- ensure that all types below inherit the may contain
    -- entries from the same type as the major type of the
    -- actual type:
    -- (for the actual type the attribute is also set, so that
    -- it inherits also from that type)
    BEGIN
        UPDATE  ibs_Type
        SET     mayContainInheritedTypeId =
                    l_mayContainInheritedTypeId
        WHERE   id = ai_minorTypeId
            OR  (   INSTR (posNoPath, l_minorPosNoPath, 1, 1) = 1
                AND mayContainInheritedTypeId = l_minorMayContainInhTypeId
                );
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$inherit.update',
                'OTHER error for type ' || ai_minorTypeId);
            RAISE;
    END;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_MayContain$inherit;
        -- log the error:
        ibs_error.log_error (ibs_error.error, 'p_MayContain$inherit',
            'sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return the error code:
        RETURN  c_NOT_OK;
END p_MayContain$inherit;
/
show errors;


/******************************************************************************
 * This procedure creates tuples into the may contain table.
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_majorTypeId      Id of the major type that may contain
 *                              different minor types.
 * @param   ai_minorTypeId      Id of minor type.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_MayContain$add
(
    -- input parameters:
    ai_majorTypeId          INTEGER,
    ai_minorTypeId          INTEGER
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists

    -- local variables: 
    l_retValue              INTEGER := c_ALL_RIGHT; 
                                            -- return value of this function
    l_majorPosNoPath        RAW (254);      -- the pos no path of the major type
    l_mayContainInheritedTypeId INTEGER;    -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    l_count                 INTEGER := 0;   -- record counter

BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- get the data of the actual major type:
    BEGIN
        SELECT  posNoPath, mayContainInheritedTypeId
        INTO    l_majorPosNoPath, l_mayContainInheritedTypeId
        FROM    ibs_Type
        WHERE   id = ai_majorTypeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$add.get type data',
                'OTHER error for type ' || ai_majorTypeId);
            RAISE;
    END;

    -- check if the record already exists:
    BEGIN
        SELECT  COUNT (*)
        INTO    l_count
        FROM    ibs_MayContain
        WHERE   majorTypeId = ai_majorTypeId
            AND minorTypeId = ai_minorTypeId
            AND isInherited = 0;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$add.get may contain count',
                'OTHER error for ai_majorTypeId ' || ai_majorTypeId ||
                ' and ai_minorTypeId ' || ai_minorTypeId);
            RAISE;
    END;

    IF (l_count > 0)                    -- the required record already exists?
    THEN
        -- set error code:
        l_retValue := c_ALREADY_EXISTS;
    ELSE                                -- the record does not exist
        COMMIT WORK; -- finish previous and begin new TRANSACTION
            -- check if the major type currently has own records within the may
            -- contain table or inherits its records from another type:
            IF (l_mayContainInheritedTypeId <> ai_majorTypeId)
                                        -- records inherited from another type?
            THEN
                -- delete the entries within the may contain table which are
                -- inherited from above the actual type to one type which is
                -- below the actual type or to the actual type itself:
                BEGIN
                    DELETE  ibs_MayContain
                    WHERE   majorTypeId IN
                            (
                                SELECT  id
                                FROM    ibs_Type
                                WHERE   INSTR (posNoPath, l_majorPosNoPath,
                                               1, 1) = 1
                                    AND mayContainInheritedTypeId = 
                                            l_mayContainInheritedTypeId
                            );
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- log the error:
                        ibs_error.log_error (ibs_error.error,
                            'p_MayContain$add.delete',
                            'OTHER error for l_majorPosNoPath ' ||
                            l_majorPosNoPath ||
                            ' and l_mayContainInheritedTypeId ' ||
                            l_mayContainInheritedTypeId);
                        RAISE;
                END;

                -- ensure that all types below inherit the may contain entries
                -- from the actual type:
                -- (for the actual type the attribute is also set, so that it
                -- inherits from itself)
                BEGIN
                    UPDATE  ibs_Type
                    SET     mayContainInheritedTypeId = ai_majorTypeId
                    WHERE   INSTR (posNoPath, l_majorPosNoPath, 1, 1) = 1
                        AND mayContainInheritedTypeId = 
                                l_mayContainInheritedTypeId;
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- log the error:
                        ibs_error.log_error (ibs_error.error,
                            'p_MayContain$add.update',
                            'OTHER error for l_majorPosNoPath ' ||
                            l_majorPosNoPath ||
                            ' and l_mayContainInheritedTypeId ' ||
                            l_mayContainInheritedTypeId);
                        RAISE;
                END;
            END IF; -- records inherited from another type

            -- insert the new records of the actual type into
            -- the may contain table:
            BEGIN
                INSERT INTO ibs_MayContain
                        (majorTypeId, minorTypeId, isInherited)
                VALUES  (ai_majorTypeId, ai_minorTypeId, 0);
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- log the error:
                    ibs_error.log_error (ibs_error.error,
                        'p_MayContain$add.insert1',
                        'OTHER error for ai_majorTypeId ' || ai_majorTypeId ||
                        ' and ai_minorTypeId ' || ai_minorTypeId);
                    RAISE;
            END;

            -- insert the new records of the sub types into
            -- the may contain table:
            BEGIN
                INSERT INTO ibs_MayContain
                        (majorTypeId, minorTypeId, isInherited)
                SELECT  id, ai_minorTypeId, 1
                FROM    ibs_Type
                WHERE   mayContainInheritedTypeId = ai_majorTypeId
                    AND id <> ai_majorTypeId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- log the error:
                    ibs_error.log_error (ibs_error.error,
                        'p_MayContain$add.insert2',
                        'OTHER error for ai_majorTypeId ' || ai_majorTypeId ||
                        ' and ai_minorTypeId ' || ai_minorTypeId);
                    RAISE;
            END;

        -- make the changes persistent:
        COMMIT WORK;
    END IF; -- else the record does not exist

    -- return the state value:
    RETURN l_retValue;

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- log the error:
        ibs_error.log_error (ibs_error.error, 'p_MayContain$add',
            'Input: ai_majorTypeId = ' || ai_majorTypeId ||
            ', ai_minorTypeId = ' || ai_minorTypeId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_MayContain$add;
/
show errors;


/******************************************************************************
 * This procedure creates tuples into the maycontain table.
 *
 * @input parameters:
 * @param   ai_majorTypeCode    Code value for the MajorType that may contain
 *                              different MinorTypes 
 * @param   ai_minorTypeCode    Code value of MinorType
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_ALREADY_EXISTS         A type with this id already exists.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
CREATE OR REPLACE FUNCTION p_MayContain$new
(
    -- input parameters:
    ai_majorTypeCode        VARCHAR2,
    ai_minorTypeCode        VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists

    -- local variables: 
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_retValue              INTEGER := c_ALL_RIGHT; 
                                            -- return value of this function
    l_majorTypeId           INTEGER := 0;   -- stores the type id of major
                                            -- object
    l_minorTypeId           INTEGER := 0;   -- stores the type id of minor
                                            -- object
    l_id                    INTEGER := 0;   -- id of entry in mayContain table

BEGIN
-- body:
    -- get the type id for the majorCode:
    BEGIN
        SELECT  id
        INTO    l_majorTypeId
        FROM    ibs_Type 
        WHERE   code = ai_majorTypeCode;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- not all data found?
            l_retValue := c_NOT_OK;     -- set return value
            -- create error entry:
            l_ePos := 'major type id not found: ' || ai_majorTypeCode;
            RAISE;                      -- call common exception handler
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'other error when searching for major type';
            RAISE;                      -- call common exception handler
    END;

    BEGIN
        -- Get the type id for the minorCode:
        SELECT  id
        INTO    l_minorTypeId
        FROM    ibs_Type 
        WHERE   code = ai_minorTypeCode;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- not all data found?
            l_retValue := c_NOT_OK;     -- set return value
            -- create error entry:
            l_ePos := 'minor type id not found: ' || ai_minorTypeCode;
            RAISE;                      -- call common exception handler
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'other error when searching for minor type';
            RAISE;                      -- call common exception handler
    END;

    -- check if such a relationship is already within the database:
    IF ((l_majorTypeId <> 0) AND (l_minorTypeId <> 0))
                                -- both types exist?
    THEN
        l_retValue := p_MayContain$add (l_majorTypeId, l_minorTypeId);
    ELSE                        -- one of the types does not exist
        -- set error code:
        l_retValue := c_NOT_OK;
    END IF; -- else one of the types does not exist

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_majorTypeCode = ' || ai_majorTypeCode ||
            ', ai_minorTypeCode = ' || ai_minorTypeCode ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_MayContain$new', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK; 
END p_MayContain$new;
/
show errors;


/******************************************************************************
 * Delete a minor type from the specified major type.
 * If this is the last minor type defined for this major type and
 * inheritFromUpper is set to 1, the type (and its subtypes) automatically
 * inherits the records from its super type.
 * If the required tuple is not found this is no severe error. So the second
 * operation of inheriting from the super type is also done in the same way.
 * It contains a TRANSACTION block, so it is not allowed to call this procedure
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   ai_majorTypeId      Id of the major type for which to delete a
 *                              record.
 * @param   ai_minorTypeId      Id of minor type to be deleted.
 * @param   ai_inheritFromSuperType In case that there are no more records for
 *                              the major type after deleting the requested
 *                              record this parameter tells whether the major
 *                              type shall inherit the records form its super
 *                              type.
 *                              Default: 1 (= true)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 * c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_MayContain$delete
(
    -- input parameters:
    ai_majorTypeId          INTEGER,
    ai_minorTypeId          INTEGER,
    ai_inheritFromSuperType NUMBER
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- tuple not found

    -- local variables: 
    l_retValue              INTEGER := c_ALL_RIGHT; 
                                            -- return value of this function
    l_retValueNew           INTEGER;        -- new value of l_retValue
    l_majorPosNoPath        RAW (254);      -- the pos no path of the major type
    l_mayContainInheritedTypeId INTEGER;    -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    l_superTypeId           INTEGER := 0;   -- Id of super type of the actual
                                            -- type
    l_superMayContainInhTypeId INTEGER;     -- id of inherited type within the
                                            -- may contain table for the super
                                            -- type
    l_count                 INTEGER := 0;   -- record counter

BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- get the data of the actual major type:
    BEGIN
        SELECT  posNoPath, mayContainInheritedTypeId, superTypeId
        INTO    l_majorPosNoPath, l_mayContainInheritedTypeId, l_superTypeId
        FROM    ibs_Type
        WHERE   id = ai_majorTypeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$delete.get type data',
                'OTHER error for type ' || ai_majorTypeId);
            RAISE;
    END;

    -- check if the major type currently has own records within the may
    -- contain table or inherits its records from another type:
    IF (l_mayContainInheritedTypeId = ai_majorTypeId)
                                -- records are not inherited from another type?
    THEn
        COMMIT WORK; -- finish previous and begin new TRANSACTION
            -- check if the record currently exists:
            BEGIN
                SELECT  COUNT (majorTypeId)
                INTO    l_count
                FROM    ibs_MayContain 
                WHERE   majorTypeId = ai_majorTypeId
                    AND minorTypeId = ai_minorTypeId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- log the error:
                    ibs_error.log_error (ibs_error.error,
                        'p_MayContain$delete.get may contain count',
                        'OTHER error for ai_majorTypeId ' || ai_majorTypeId ||
                        ' and ai_minorTypeId ' || ai_minorTypeId);
                    RAISE;
            END;


            IF (l_count > 0)            -- the required record exists?
            THEN
                -- delete the record in the type itself and all inherited ones
                -- in the sub types:
                BEGIN
                    DELETE  ibs_MayContain
                    WHERE   majorTypeId IN
                            (
                                SELECT  id
                                FROM    ibs_Type
                                WHERE   mayContainInheritedTypeId =
                                            ai_majorTypeId
                            )
                        AND minorTypeId = ai_minorTypeId;
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- log the error:
                        ibs_error.log_error (ibs_error.error,
                            'p_MayContain$delete.delete',
                            'OTHER error for ai_majorTypeId ' ||
                            ai_majorTypeId ||
                            ' and ai_minorTypeId ' || ai_minorTypeId);
                        RAISE;
                END;
            ELSE                        -- the record does not exist
                -- set error code:
                l_retValue := c_OBJECTNOTFOUND;
            END IF; -- else the record does not exist

            -- check if there are any records for the actual type:
            BEGIN
                SELECT  COUNT (majorTypeId)
                INTO    l_count
                FROM    ibs_MayContain 
                WHERE   majorTypeId = ai_majorTypeId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- log the error:
                    ibs_error.log_error (ibs_error.error,
                        'p_MayContain$delete.get may contain count2',
                        'OTHER error for ai_majorTypeId ' || ai_majorTypeId);
                    RAISE;
            END;

            IF (l_count > 0)            -- no record left for this type?
            THEN
                -- check if the type shall inherit from the super type:
                IF (ai_inheritFromSuperType = 1 AND l_superTypeId <> 0)
                                        -- inherit records from super type?
                THEN
                    -- inherit the entries from the super type:
                    l_retValueNew :=
                        p_MayContain$inherit (l_superTypeId, ai_majorTypeId);
                    -- if there is an error occurred use this value as return
                    -- value:
                    -- (otherwise the current return value stays unchanged)
                    IF (l_retValueNew <> c_ALL_RIGHT)
                    THEN
                        l_retValue := l_retValueNew;
                    END IF;
                END IF; -- if inherit records from super type
            END IF; -- if no record left for this type

        -- finish the transaction:
        IF (l_retValue <> c_ALL_RIGHT AND l_retValue <> c_OBJECTNOTFOUND)
                                        -- there occurred a severe error?
        THEN
            ROLLBACK;                   -- undo changes
        END IF; -- there occurred a severe error
    END IF; -- if records are not inherited from another type

    -- make changes permanent and set new transaction starting point:
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- log the error:
        ibs_error.log_error (ibs_error.error, 'p_MayContain$delete',
            'Input: ai_majorTypeId = ' || ai_majorTypeId ||
            ', ai_minorTypeId = ' || ai_minorTypeId || 
            ', ai_inheritFromSuperType = ' || ai_inheritFromSuperType || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error value:
        RETURN c_NOT_OK;
END p_MayContain$delete;
/
show errors;


/******************************************************************************
 * Delete all occurrences of a type out of the may contain table. <BR>
 * This function deletes occurrences of the type as major and as minor type.
 * If the type is used to inherit entries to sub types the sub types will
 * inherit their entries from the super type of the type.
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
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_MayContain$deleteType
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
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; -- the object already exists

    -- local variables: 
    l_retValue              INTEGER := c_ALL_RIGHT; 
                                            -- return value of this function
    l_mayContainInheritedTypeId INTEGER;    -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    l_superTypeId           INTEGER := 0;   -- Id of super type of the actual
                                            -- type
    l_superMayContainInhTypeId INTEGER;     -- id of inherited type within the
                                            -- may contain table for the super
                                            -- type
    l_count                 INTEGER := 0;   -- record counter

BEGIN
-- body:
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- get the data of the actual major type:
    BEGIN
        SELECT  mayContainInheritedTypeId, superTypeId
        INTO    l_mayContainInheritedTypeId, l_superTypeId
        FROM    ibs_Type
        WHERE   id = ai_typeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$deleteType.get type data',
                'OTHER error for ai_typeId ' || ai_typeId);
            RAISE;
    END;

    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- get data of super type:
    BEGIN
        SELECT  mayContainInheritedTypeId
        INTO    l_superMayContainInhTypeId
        FROM    ibs_Type
        WHERE   id = l_superTypeId;

        -- (at this point we know that the super type exists)
        -- set the may contain inherited type id for all sub types of the
        -- current type which inherit the may contain entries from the current
        -- type:
        BEGIN
            UPDATE  ibs_Type
            SET     mayContainInheritedTypeId = l_superMayContainInhTypeId
            WHERE   mayContainInheritedTypeId = ai_typeId;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- log the error:
                ibs_error.log_error (ibs_error.error,
                    'p_MayContain$deleteType.update',
                    'OTHER error for ai_typeId ' || ai_typeId);
                RAISE;
        END;
    EXCEPTION
/*
        WHEN NO_DATA_FOUND THEN         -- the super type was not found
            -- nothing to do
*/
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$deleteType.get super type data',
                'OTHER error for l_superTypeId ' || l_superTypeId);
            RAISE;
    END;

    -- delete the entries of the actual type from the may contain table:
    BEGIN
        DELETE  ibs_MayContain
        WHERE   majorTypeId = ai_typeId
            OR  minorTypeId = ai_typeId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            ibs_error.log_error (ibs_error.error,
                'p_MayContain$deleteType.delete',
                'OTHER error for ai_typeId ' || ai_typeId);
            RAISE;
    END;

    -- finish the transaction:
    IF (l_retValue <> c_ALL_RIGHT)      -- there occurred a severe error?
    THEN
        ROLLBACK;                       -- undo changes
    END IF; -- there occurred a severe error

    -- make changes permanent and set new transaction starting point:
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- log the error:
        ibs_error.log_error (ibs_error.error, 'p_MayContain$deleteType',
            'Input: ai_typeId = ' || ai_typeId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error value:
        RETURN c_NOT_OK;
END p_MayContain$deleteType;
/
show errors;

exit;
