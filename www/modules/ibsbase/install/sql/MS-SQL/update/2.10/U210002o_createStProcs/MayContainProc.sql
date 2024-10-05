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
-- delete existing procedure:
EXEC p_dropProc N'p_MayContain$inherit'
GO

-- create the new procedure:
CREATE PROCEDURE p_MayContain$inherit
(
    -- input parameters:
    @ai_majorTypeId         TYPEID,
    @ai_minorTypeId         TYPEID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_count                INT,            -- counter
    @l_retValue             INT,            -- return value of this function
    @l_mayContainInheritedTypeId TYPEID,    -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    @l_minorPosNoPath       POSNOPATH,      -- the pos no path of the minor type
    @l_minorMayContainInhTypeId TYPEID      -- id of type from which the actual
                                            -- minor type originally inherits
                                            -- its may contain records

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_MayContain$inherit

    -- get the data of the type from which to inherit the tuples:
    SELECT  @l_mayContainInheritedTypeId = mayContainInheritedTypeId
    FROM    ibs_Type
    WHERE   id = @ai_majorTypeId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get major type data', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0 OR @l_count = 0)  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- get the data of the type to which to inherit the tuples:
    SELECT  @l_minorMayContainInhTypeId = mayContainInheritedTypeId,
            @l_minorPosNoPath = posNoPath
    FROM    ibs_Type
    WHERE   id = @ai_minorTypeId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'get minor type data', @l_ePos OUTPUT, @l_count OUTPUT
    IF (@l_error <> 0 OR @l_count = 0)  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- delete the current tuples of the minor type and all types which
    -- inherited their actual tuples from the minor type:
    DELETE  ibs_MayContain
    WHERE   majorTypeId IN
            (
                SELECT  id
                FROM    ibs_Type
                WHERE   id = @ai_minorTypeId
                    OR  (   CHARINDEX (@l_minorPosNoPath, posNoPath) = 1
                        AND mayContainInheritedTypeId = 
                                @l_minorMayContainInhTypeId
                        )
            )

    -- inherit the entries from the major type to the minor type and all types
    -- which beforehand inherited their tuples from the minor type:
    -- insert the new (inherited) records into the may contain table:
    INSERT INTO ibs_MayContain
            (majorTypeId, minorTypeId, isInherited)
    SELECT  t.id, m.minorTypeId, 1
    FROM    ibs_Type t, ibs_MayContain m
    WHERE   (
                t.id = @ai_minorTypeId
            OR  (   CHARINDEX (@l_minorPosNoPath, t.posNoPath) = 1
                AND t.mayContainInheritedTypeId = @l_minorMayContainInhTypeId
                )
            )
        AND m.majorTypeId = @ai_majorTypeId

    -- ensure that all types below inherit the may contain
    -- entries from the same type as the major type of the
    -- actual type:
    -- (for the actual type the attribute is also set, so that
    -- it inherits also from that type)
    UPDATE  ibs_Type
    SET     mayContainInheritedTypeId =
                @l_mayContainInheritedTypeId
    WHERE   id = @ai_minorTypeId
        OR  (   CHARINDEX (@l_minorPosNoPath, posNoPath) = 1
            AND mayContainInheritedTypeId = @l_minorMayContainInhTypeId
            )

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_MayContain$inherit
    -- log the error:
    EXEC ibs_error.logError 500, N'p_MayContain$inherit', @l_error, @l_ePos,
            N'ai_majorTypeId', @ai_majorTypeId,
            N'', N'',
            N'ai_minorTypeId', @ai_minorTypeId
    -- return the error code:
    RETURN  @c_NOT_OK
GO
-- p_MayContain$inherit


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
-- delete existing procedure:
EXEC p_dropProc N'p_MayContain$add'
GO

-- create the new procedure:
CREATE PROCEDURE p_MayContain$add
(
    -- input parameters:
    @ai_majorTypeId         TYPEID,
    @ai_minorTypeId         TYPEID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_majorPosNoPath       POSNOPATH,      -- the pos no path of the major type
    @l_mayContainInheritedTypeId TYPEID     -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- get the data of the actual major type:
    SELECT  @l_majorPosNoPath = posNoPath,
            @l_mayContainInheritedTypeId = mayContainInheritedTypeId
    FROM    ibs_Type
    WHERE   id = @ai_majorTypeId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get major type data', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO NonTransactionException    -- call common exception handler

    -- check if the record already exists:
    IF EXISTS (
        SELECT  majorTypeId
        FROM    ibs_MayContain 
        WHERE   majorTypeId = @ai_majorTypeId
            AND minorTypeId = @ai_minorTypeId
            AND isInherited = 0
    )                                   -- the required record already exists?
        -- set error code:
        SELECT  @l_retValue = @c_ALREADY_EXISTS
    ELSE                                -- the record does not exist
    BEGIN
        BEGIN TRANSACTION
            -- check if the major type currently has own records within the may
            -- contain table or inherits its records from another type:
            IF (@l_mayContainInheritedTypeId <> @ai_majorTypeId)
                                        -- records inherited from another type?
            BEGIN
                -- delete the entries within the may contain table which are
                -- inherited from above the actual type to one type which is
                -- below the actual type or to the actual type itself:
                DELETE  ibs_MayContain
                WHERE   majorTypeId IN
                        (
                            SELECT  id
                            FROM    ibs_Type
                            WHERE   CHARINDEX (@l_majorPosNoPath, posNoPath) = 1
                                AND mayContainInheritedTypeId = 
                                        @l_mayContainInheritedTypeId
                        )

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'delete inherited entries', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call common exception handler

                -- ensure that all types below inherit the may contain entries
                -- from the actual type:
                -- (for the actual type the attribute is also set, so that it
                -- inherits from itself)
                UPDATE  ibs_Type
                SET     mayContainInheritedTypeId = @ai_majorTypeId
                WHERE   CHARINDEX (@l_majorPosNoPath, posNoPath) = 1
                    AND mayContainInheritedTypeId = 
                            @l_mayContainInheritedTypeId

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'update ibs_Type.mayContaineInheritedTypeId', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call common exception handler
            END -- if records inherited from another type

            -- insert the new records of the actual type into
            -- the may contain table:
            INSERT INTO ibs_MayContain
                    (majorTypeId, minorTypeId, isInherited)
            VALUES  (@ai_majorTypeId, @ai_minorTypeId, 0)

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'insert records of actual type', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

            -- insert the new records of the sub types into
            -- the may contain table:
            INSERT INTO ibs_MayContain
                    (majorTypeId, minorTypeId, isInherited)
            SELECT  id, @ai_minorTypeId, 1
            FROM    ibs_Type
            WHERE   mayContainInheritedTypeId = @ai_majorTypeId
                AND id <> @ai_majorTypeId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'insert records of sub types', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

        COMMIT TRANSACTION
    END -- else the record does not exist

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_MayContain$add', @l_error, @l_ePos,
            N'ai_majorTypeId', @ai_majorTypeId,
            N'', N'',
            N'ai_minorTypeId', @ai_minorTypeId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_MayContain$add


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
-- delete existing procedure:
EXEC p_dropProc N'p_MayContain$new'
GO

-- create the new procedure:
CREATE PROCEDURE p_MayContain$new
(
    -- input parameters:
    @ai_majorTypeCode       NAME,
    @ai_minorTypeCode       NAME
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_majorTypeId          TYPEID,         -- stores the type id of major
                                            -- object
    @l_minorTypeId          TYPEID,         -- stores the type id of minor
                                            -- object
    @l_id                   ID              -- id of entry in mayContain table

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21
    
    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_majorTypeId = 0, 
    @l_minorTypeId = 0,
    @l_id = 0

-- body:
    -- get the type id for the majorCode:
    SELECT  @l_majorTypeId = id
    FROM    ibs_Type 
    WHERE   code = @ai_majorTypeCode

    -- Get the type id for the minorCode:
    SELECT  @l_minorTypeId = id
    FROM    ibs_Type 
    WHERE   code = @ai_minorTypeCode
    
    -- check if such a relationship is already within the database:
    IF ((@l_majorTypeId <> 0) AND (@l_minorTypeId <> 0))
                                        -- both types exist?
        EXEC @l_retValue = p_MayContain$add @l_majorTypeId, @l_minorTypeId
    ELSE                                -- one of the types does not exist
        -- set error code:
        SELECT @l_retValue = @c_NOT_OK

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_MayContain$new


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
 *                              type shall inherit the records from its super
 *                              type.
 *                              Default: 1 (= true)
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 * c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_MayContain$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_MayContain$delete
(
    -- input parameters:
    @ai_majorTypeId         TYPEID,
    @ai_minorTypeId         TYPEID,
    @ai_inheritFromSuperType BOOL = 1
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- tuple not found

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_retValueNew          INT,            -- new value of l_retValue
    @l_majorPosNoPath       POSNOPATH,      -- the pos no path of the major type
    @l_mayContainInheritedTypeId TYPEID,    -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    @l_superTypeId          TYPEID,         -- Id of super type of the actual
                                            -- type
    @l_superMayContainInhTypeId TYPEID      -- id of inherited type within the
                                            -- may contain table for the super
                                            -- type

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- get the data of the actual major type:
    SELECT  @l_majorPosNoPath = posNoPath,
            @l_mayContainInheritedTypeId = mayContainInheritedTypeId,
            @l_superTypeId = superTypeId
    FROM    ibs_Type
    WHERE   id = @ai_majorTypeId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get data of actual major type', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- check if the major type currently has own records within the may
    -- contain table or inherits its records from another type:
    IF (@l_mayContainInheritedTypeId = @ai_majorTypeId)
                                -- records are not inherited from another type?
    BEGIN
        BEGIN TRANSACTION
            -- check if the record currently exists:
            IF EXISTS (
                SELECT  majorTypeId
                FROM    ibs_MayContain 
                WHERE   majorTypeId = @ai_majorTypeId
                    AND minorTypeId = @ai_minorTypeId
            )                               -- the required record exists?
            BEGIN
                -- delete the record in the type itself and all inherited ones
                -- in the sub types:
                DELETE  ibs_MayContain
                WHERE   majorTypeId IN
                        (
                            SELECT  id
                            FROM    ibs_Type
                            WHERE   mayContainInheritedTypeId = @ai_majorTypeId
                        )
                    AND minorTypeId = @ai_minorTypeId

                    -- check if there occurred an error:
                    EXEC @l_error = ibs_error.prepareError @@error,
                        N'delete', @l_ePos OUTPUT
                    IF (@l_error <> 0)  -- an error occurred?
                        GOTO exception  -- call common exception handler
            END -- if the required record exists
            ELSE                        -- the record does not exist
            BEGIN
                -- set error code:
                SELECT  @l_retValue = @c_OBJECTNOTFOUND
            END -- else the record does not exist

            -- check if there are any records for the actual type:
            IF NOT EXISTS (
                SELECT  majorTypeId
                FROM    ibs_MayContain 
                WHERE   majorTypeId = @ai_majorTypeId
            )                           -- no record left for this type?
            BEGIN
                -- check if the type shall inherit from the super type:
                IF (@ai_inheritFromSuperType = 1 AND @l_superTypeId <> 0)
                                        -- inherit records from super type?
                BEGIN
                    -- inherit the entries from the super type:
                    EXEC @l_retValueNew =
                        p_MayContain$inherit @l_superTypeId, @ai_majorTypeId
                    -- if there is an error occurred use this value as return
                    -- value:
                    -- (otherwise the current return value stays unchanged)
                    IF (@l_retValueNew <> @c_ALL_RIGHT)
                        SELECT  @l_retValue = @l_retValueNew
                END -- if inherit records from super type
            END -- if no record left for this type

        -- finish the transaction:
        IF (@l_retValue <> @c_ALL_RIGHT AND @l_retValue <> @c_OBJECTNOTFOUND)
                                        -- there occurred a severe error?
            ROLLBACK TRANSACTION        -- undo changes
        ELSE                            -- there occurred no error
            COMMIT TRANSACTION          -- make changes permanent
    END -- if records are not inherited from another type

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_MayContain$delete', @l_error, @l_ePos,
            N'ai_majorTypeId', @ai_majorTypeId,
            N'', N'',
            N'ai_minorTypeId', @ai_minorTypeId,
            N'', N'',
            N'ai_inheritFromSuperType', @ai_inheritFromSuperType
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_MayContain$delete


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
-- delete existing procedure:
EXEC p_dropProc N'p_MayContain$deleteType'
GO

-- create the new procedure:
CREATE PROCEDURE p_MayContain$deleteType
(
    -- input parameters:
    @ai_typeId              TYPEID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_ALREADY_EXISTS       INT,            -- the object already exists

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_mayContainInheritedTypeId TYPEID,    -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    @l_superTypeId          TYPEID,         -- Id of super type of the actual
                                            -- type
    @l_superMayContainInhTypeId TYPEID      -- id of inherited type within the
                                            -- may contain table for the super
                                            -- type

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_ALREADY_EXISTS       = 21

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- get the data of the actual major type:
    SELECT  @l_mayContainInheritedTypeId = mayContainInheritedTypeId,
            @l_superTypeId = superTypeId
    FROM    ibs_Type
    WHERE   id = @ai_typeId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get data of actual major type', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- get data of super type:
    SELECT  @l_superMayContainInhTypeId = mayContainInheritedTypeId
    FROM    ibs_Type
    WHERE   id = @l_superTypeId

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        N'get data of super type', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    BEGIN TRANSACTION
        -- check if the super type was found:
        IF (@@ROWCOUNT > 0)                 -- the super type was found?
        BEGIN
            -- set the may contain inherited type id for all sub types of the
            -- current type which inherit the may contain entries from the
            -- current type:
            UPDATE  ibs_Type
            SET     mayContainInheritedTypeId =
                        @l_superMayContainInhTypeId
            WHERE   mayContainInheritedTypeId = @ai_typeId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'update', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler
        END -- if the super type was found
/*
        ELSE                            -- the super type was not found
        BEGIN
            -- nothing to do
        END -- else the super type was not found
*/

        -- delete the entries of the actual type from the may contain table:
        DELETE  ibs_MayContain
        WHERE   majorTypeId = @ai_typeId
            OR  minorTypeId = @ai_typeId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'delete', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

    -- finish the transaction:
    IF (@l_retValue <> @c_ALL_RIGHT)    -- there occurred a severe error?
        ROLLBACK TRANSACTION            -- undo changes
    ELSE                                -- there occurred no error
        COMMIT TRANSACTION              -- make changes permanent

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_MayContain$deleteType', @l_error,
            @l_ePos,
            N'ai_typeId', @ai_typeId
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_MayContain$deleteType
