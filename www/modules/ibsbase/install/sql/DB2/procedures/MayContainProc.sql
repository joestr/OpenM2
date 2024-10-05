--------------------------------------------------------------------------------
-- All stored procedures regarding the mayContain table. <BR>
-- 
-- @version     $Revision: 1.6 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020819
-------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Inherit the tuples from one type to another type. <BR>
-- If there are any types currently inheriting their tuples from the second
-- type they will also inherit their tuples from the first type.
-- This function must be called from within a transaction handled code block
-- because it uses savepoints.
--
-- @input parameters:
-- @param   ai_majorTypeId      Id of the major type from which the tuples shall
--                              be inherited.
-- @param   ai_minorTypeId      Id of minor type to which the tuples shall be
--                              inherited.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MayContain$inherit');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MayContain$inherit
(
    -- input parameters:
    IN  ai_majorTypeId      INT,
    IN  ai_minorTypeId      INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_count         INT;            -- counter
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_mayContainInheritedTypeId INT;/*TYPEID*/-- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    DECLARE l_minorPosNoPath CHAR (254) FOR BIT DATA;/*POSNOPATH*/
    DECLARE l_minorMayContainInhTypeId INT;/*TYPEID*/
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    
    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    SAVEPOINT s_MContPrc_inherit ON ROLLBACK RETAIN CURSORS;

    -- get the data of the type from which to inherit the tuples:
    SET l_sqlcode = 0;

    SELECT  mayContainInheritedTypeId
    INTO    l_mayContainInheritedTypeId
    FROM    IBSDEV1.ibs_Type
    WHERE   id = ai_majorTypeId;

    SELECT  COUNT(*) 
    INTO    l_rowcount
    FROM    IBSDEV1.ibs_Type
    WHERE   id = ai_majorTypeId;
    
    -- check if there occurred an error:

    IF ((l_sqlcode <> 0 AND l_sqlcode <> 100) OR l_count = 0)
    THEN                                -- an error occurred?
        SET l_ePos = 'get the data of the type from which to inherit the tuples';
        GOTO exception1;                -- call common exception handler
    END IF; -- an error occurred

    -- get the data of the type to which to inherit the tuples:
    SET l_sqlcode = 0;
    SELECT  mayContainInheritedTypeId, posNoPath
    INTO    l_minorMayContainInhTypeId, l_minorPosNoPath
    FROM    IBSDEV1.ibs_Type
    WHERE   id = ai_minorTypeId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'get minor type data';
        GOTO exception1;                -- call common exception handler
    END IF; -- an error occurred

    -- delete the current tuples of the minor type and all types which
    -- inherited their actual tuples from the minor type:
    SET l_sqlcode = 0;

    DELETE FROM IBSDEV1.ibs_MayContain
    WHERE   majorTypeId IN
            (
                SELECT  id 
                FROM    IBSDEV1.ibs_Type
                WHERE   id = ai_minorTypeId
                    OR  (   POSSTR (posNoPath, l_minorPosNoPath) = 1
                        AND mayContainInheritedTypeId =
                                l_minorMayContainInhTypeId
                        )
            );

    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'delete';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    -- inherit the entries from the major type to the minor type and all types
    -- which beforehand inherited their tuples from the minor type:
    -- insert the new (inherited) records into the may contain table:
    SET l_sqlcode = 0;

    INSERT INTO IBSDEV1.ibs_MayContain
        (majorTypeId, minorTypeId, isInherited)
    SELECT  t.id, m.minorTypeId, 1
    FROM    IBSDEV1.ibs_Type t, IBSDEV1.ibs_MayContain m
    WHERE   (
                t.id = ai_minorTypeId
                OR  (   POSSTR (t.posNoPath, l_minorPosNoPath) = 1
                    AND t.mayContainInheritedTypeId = l_minorMayContainInhTypeId
                )
            )
        AND m.majorTypeId = ai_majorTypeId;

    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'insert';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    -- ensure that all types below inherit the may contain
    -- entries from the same type as the major type of the
    -- actual type:
    -- (for the actual type the attribute is also set, so that
    -- it inherits also from that type)
    SET l_sqlcode = 0;

    UPDATE  IBSDEV1.ibs_Type
    SET     mayContainInheritedTypeId = l_mayContainInheritedTypeId
    WHERE   id = ai_minorTypeId
        OR  (   POSSTR (posNoPath, l_minorPosNoPath) = 1
            AND mayContainInheritedTypeId = l_minorMayContainInhTypeId
            );

    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'update';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    -- release the savepoint:
    RELEASE s_MContPrc_inherit;
    -- return the state value:
    RETURN l_retValue;

exception1:                             -- an error occurred
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_MContPrc_inherit;
    -- release the savepoint:
    RELEASE s_MContPrc_inherit;
    
    -- roll back to the save point:
    CALL IBSDEV1.logError (500, 'p_MayContain$inherit', l_sqlcode, l_ePos,
        'ai_majorTypeId', ai_majorTypeId, '', '',
        'ai_minorTypeId', ai_minorTypeId, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    COMMIT;
    RETURN c_NOT_OK;
END;
-- p_MayContain$inherit 
 
--------------------------------------------------------------------------------
-- This procedure creates tuples into the may contain table.
-- It contains a TRANSACTION block, so it is not allowed to call this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_majorTypeId      Id of the major type that may contain
--                              different minor types.
-- @param   ai_minorTypeId      Id of minor type.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MayContain$add');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MayContain$add
(
    IN  ai_majorTypeId      INT,
    IN  ai_minorTypeId      INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_majorPosNoPath VARCHAR (254) FOR BIT DATA;
                                            -- the pos no path of the major type
    DECLARE l_mayContainInheritedTypeId INT/*TYPEID*/;
                                            -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    COMMIT; -- finish previous and begin new TRANSACTION
    -- get the data of the actual major type:
    SET l_sqlcode = 0;

    SELECT  posNoPath, mayContainInheritedTypeId
    INTO    l_majorPosNoPath, l_mayContainInheritedTypeId
    FROM    IBSDEV1.ibs_Type
    WHERE   id = ai_majorTypeId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'get major type data';
        GOTO NonTransactionException;   -- call common exception handler
    END IF; -- if an error occurred

    -- check if the record already exists:
    IF EXISTS   (
                    SELECT  majorTypeId 
                    FROM    IBSDEV1.ibs_MayContain
                    WHERE   majorTypeId = ai_majorTypeId
                        AND minorTypeId = ai_minorTypeId
                        AND isInherited = 0
                )
    THEN                                -- the required record already exists?
        -- set error code:
        SET l_retValue = c_ALREADY_EXISTS;
    -- end if the required recored already exists
    ELSE                                -- the record does not exist
        -- check if the major type currently has own records within the may
        -- contain table or inherits its records from another type:
        IF (l_mayContainInheritedTypeId <> ai_majorTypeId)
                                        -- records inherited from another type?
        THEN
            -- delete the entries within the may contain table which are
            -- inherited from above the actual type to one type which is
            -- below the actual type or to the actual type itself:
            SET l_sqlcode = 0;
            DELETE FROM IBSDEV1.ibs_MayContain
            WHERE   majorTypeId IN
                    (
                        SELECT  id 
                        FROM    IBSDEV1.ibs_Type
                        WHERE   POSSTR (posNoPath, l_majorPosNoPath) = 1
                            AND mayContainInheritedTypeId =
                                    l_mayContainInheritedTypeId
                    );

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN                        -- an error occurred?
                SET l_ePos = 'delete inherited entries';
                GOTO exception1;        -- call common exception handler
            END IF; -- if an error occurred

            -- ensure that all types below inherit the may contain entries
            -- from the actual type:
            -- (for the actual type the attribute is also set, so that it
            -- inherits from itself)
            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.ibs_Type
            SET     mayContainInheritedTypeId = ai_majorTypeId
            WHERE   POSSTR (posNoPath,l_majorPosNoPath) = 1
                AND mayContainInheritedTypeId = l_mayContainInheritedTypeId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN                        -- an error occurred?
                SET l_ePos = 'update ibs_Type.mayContaineInheritedTypeId';
                GOTO exception1;        -- call common exception handler
            END IF; -- if an error occurred
        END IF;

        -- insert the new records of the actual type into
        -- the may contain table:
        SET l_sqlcode = 0;

        INSERT INTO IBSDEV1.ibs_MayContain
                (majorTypeId, minorTypeId, isInherited)
        VALUES  (ai_majorTypeId, ai_minorTypeId, 0);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
        THEN                            -- an error occurred?
            SET l_ePos = 'insert records of actual type';
            GOTO exception1;            -- call common exception handler
        END IF; -- if an error occurred

        -- insert the new records of the sub types into
        -- the may contain table:
        SET l_sqlcode = 0;

        INSERT INTO IBSDEV1.ibs_MayContain
                (majorTypeId, minorTypeId, isInherited)
        SELECT  id, ai_minorTypeId, 1
        FROM    IBSDEV1.ibs_Type
        WHERE   mayContainInheritedTypeId = ai_majorTypeId
            AND id <> ai_majorTypeId;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
        THEN                            -- an error occurred?
            SET l_ePos = 'insert records of sub types';
            GOTO exception1;            -- call common exception handler
        END IF; -- if an error occurred

        -- make the changes persistent:
        COMMIT;
    END IF;

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;                           -- undo changes

NonTransactionException:                -- error outside of transaction occurred
    CALL IBSDEV1.logError (500, 'p_MayContain$add', l_sqlcode, l_ePos,
        'ai_majorTypeId', ai_majorTypeId, '', '',
        'ai_minorTypeId', ai_minorTypeId, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_MayContain$add


--------------------------------------------------------------------------------
-- This procedure creates tuples into the maycontain table.
--
-- @input parameters:
-- @param   ai_majorTypeCode    Code value for the MajorType that may contain
--                              different MinorTypes 
-- @param   ai_minorTypeCode    Code value of MinorType
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MayContain$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MayContain$new
(
    IN  ai_majorTypeCode    VARCHAR (63),
    IN  ai_minorTypeCode    VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_majorTypeId   INT DEFAULT 0;  -- stores the type id of major
                                            -- object
    DECLARE l_minorTypeId   INT DEFAULT 0;  -- stores the type id of minor
                                            -- object
    DECLARE l_id            INT DEFAULT 0/*ID*/;
                                            -- id of entry in mayContain table
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;
    SET l_majorTypeId = 0;
    SET l_minorTypeId = 0;
    SET l_id = 0;

-- body:
    -- get the type id for the majorCode:
    SET l_sqlcode = 0;

    SELECT  id 
    INTO    l_majorTypeId
    FROM    IBSDEV1.ibs_Type
    WHERE   code = ai_majorTypeCode;

    -- Get the type id for the minorCode:
    SELECT  id
    INTO    l_minorTypeId
    FROM    IBSDEV1.ibs_Type
    WHERE   code = ai_minorTypeCode;

    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'get the type id';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    -- check if such a relationship is already within the database:
    IF ((l_majorTypeId <> 0) AND (l_minorTypeId <> 0))
    THEN                                -- both types exist?
        CALL IBSDEV1.p_MayContain$add (l_majorTypeId, l_minorTypeId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- end if both types exist
    ELSE                                -- one of the types does not exist
        SET l_retValue = c_NOT_OK;
    END IF; -- else one of the types does not exist

    -- return the state value:
    RETURN l_retValue;

exception1:                             -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_MayContain$add', l_sqlcode, l_ePos,
        '',0, 'ai_majorTypecode', ai_majorTypecode,
        '', 0, 'ai_minorTypecode', ai_minorTypecode,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_MayContain$new

--------------------------------------------------------------------------------
-- Delete a minor type from the specified major type.
-- If this is the last minor type defined for this major type and
-- inheritFromUpper is set to 1, the type (and its subtypes) automatically
-- inherits the records from its super type.
-- If the required tuple is not found this is no severe error. So the second
-- operation of inheriting from the super type is also done in the same way.
-- It contains a TRANSACTION block, so it is not allowed to call this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_majorTypeId      Id of the major type for which to delete a
--                              record.
-- @param   ai_minorTypeId      Id of minor type to be deleted.
-- @param   ai_inheritFromSuperType In case that there are no more records for
--                              the major type after deleting the requested
--                              record this parameter tells whether the major
--                              type shall inherit the records from its super
--                              type.
--                              Default: 1 (= true)
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
-- c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MayContain$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MayContain$delete
(
    -- input parameters:
    IN  ai_majorTypeId      INT,
    IN  ai_minorTypeId      INT,
    IN  ai_inheritFromSuperType SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- tuple not found

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_retValueNew   INT;            -- new value of l_retValue
    DECLARE l_majorPosNoPath VARCHAR (254) FOR BIT DATA/*POSNOPATH*/;
                                            -- the pos no path of the major type
    DECLARE l_mayContainInheritedTypeId INT/*TYPEID*/;
                                            -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    DECLARE l_superTypeId   INT/*TYPEID*/;  -- Id of super type of the actual
                                            -- type
    DECLARE l_superMayContainInhTypeId INT/*TYPEID*/;
                                            -- id of inherited type within the
                                            -- may contain table for the super
                                            -- type
    DECLARE l_sqlcode       INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    COMMIT; -- finish previous and begin new TRANSACTION
    -- get the data of the actual major type:
    SET l_sqlcode = 0;

    SELECT  posNoPath, mayContainInheritedTypeId, superTypeId
    INTO    l_majorPosNoPath, l_mayContainInheritedTypeId, l_superTypeId
    FROM    IBSDEV1.ibs_Type
    WHERE   id = ai_majorTypeId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'get data of actual major type';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    -- check if the major type currently has own records within the may
    -- contain table or inherits its records from another type:
    IF (l_mayContainInheritedTypeId = ai_majorTypeId)
                                -- records are not inherited from another type?
    THEN
        COMMIT; -- finish previous and begin new TRANSACTION
        -- check if the record currently exists:
        IF EXISTS   (
                        SELECT  majorTypeId 
                        FROM    IBSDEV1.ibs_MayContain
                        WHERE   majorTypeId = ai_majorTypeId
                            AND minorTypeId = ai_minorTypeId
                    )
        THEN                            -- the required record exists?
            -- delete the record in the type itself and all inherited ones
            -- in the sub types:
            SET l_sqlcode = 0;
            DELETE FROM IBSDEV1.ibs_MayContain
            WHERE   majorTypeId IN
                    (
                        SELECT  id 
                        FROM    IBSDEV1.ibs_Type
                        WHERE   mayContainInheritedTypeId = ai_majorTypeId
                    )
                AND minorTypeId = ai_minorTypeId;

            -- check if there occurred an error:

            IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
            THEN                        -- an error occurred?
                SET l_ePos = 'delete';
                GOTO exception1;        -- call common exception handler
            END IF; -- if an error occurred
        -- end if records are not inherited from another type
        ELSE                            -- the record does not exist
            -- set error code:
            SET l_retValue = c_OBJECTNOTFOUND;
        END IF; -- else the record does not exist

        -- check if there are any records for the actual type:
        IF NOT EXISTS   (
                            SELECT  majorTypeId 
                            FROM    IBSDEV1.ibs_MayContain
                            WHERE   majorTypeId = ai_majorTypeId
                        )
                                        -- no record left for this type?
        THEN
            -- check if the type shall inherit from the super type:
            IF (ai_inheritFromSuperType = 1 AND l_superTypeId <> 0)
                                        -- inherit records from super type?
            THEN 
                -- inherit the entries from the super type:
                CALL IBSDEV1.p_MayContain$inherit
                    (l_superTypeId, ai_majorTypeId);
                GET DIAGNOSTICS l_retValueNew = RETURN_STATUS;
                -- if there is an error occurred use this value as return
                -- value:
                -- (otherwise the current return value stays unchanged)
                IF (l_retValueNew <> c_ALL_RIGHT)
                THEN 
                    SET l_retValue = l_retValueNew;
                END IF;
            END IF; -- if inherit records from super type
        END IF; -- if no record left for this type

        -- finish the transaction:
        IF (l_retValue <> c_ALL_RIGHT AND l_retValue <> c_OBJECTNOTFOUND)
                                        -- there occurred a severe error?
        THEN
            -- there occurred a severe error?
            ROLLBACK;                       -- undo changes
        END IF; -- there occurred a severe error
    -- make changes permanent and set new transaction starting point:
    COMMIT;
    END IF;

    -- return the state value:
    RETURN l_retValue;

exception1:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;                               -- undo changes
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_MayContain$delete', l_sqlcode, l_ePos,
        'ai_majorTypeId', ai_majorTypeId, '', '',
        'ai_minorTypeId', ai_minorTypeId, '', '',
        'ai_inheritFromSuperType', ai_inheritFromSuperType, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set new transaction starting point:
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_MayContain$delete


--------------------------------------------------------------------------------
-- Delete all occurrences of a type out of the may contain table. <BR>
-- This function deletes occurrences of the type as major and as minor type.
-- If the type is used to inherit entries to sub types the sub types will
-- inherit their entries from the super type of the type.
-- It contains a TRANSACTION block, so it is not allowed to call this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_typeId           Id of the type to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MayContain$deleteType');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_MayContain$deleteType
(
    -- input parameters:
    IN  ai_typeId           INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT DEFAULT 21; -- the object already exists

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_mayContainInheritedTypeId INT/*TYPEID*/; -- id of type from which the actual
                                            -- major type originally inherits
                                            -- its may contain records
    DECLARE l_superTypeId   INT/*TYPEID*/;  -- Id of super type of the actual
                                            -- type
    DECLARE l_superMayContainInhTypeId INT/*TYPEID*/; -- id of inherited type within the
                                            -- may contain table for the super
                                            -- type;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    COMMIT; -- finish previous and begin new TRANSACTION

    -- get the data of the actual major type:
    SET l_sqlcode = 0;
    SELECT  mayContainInheritedTypeId, superTypeId 
    INTO    l_mayContainInheritedTypeId, l_superTypeId
    FROM    IBSDEV1.ibs_Type
    WHERE   id = ai_typeId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'get data of actual major type';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    COMMIT WORK; -- finish previous and begin new TRANSACTION

    SET l_sqlcode = 0;

    SELECT  mayContainInheritedTypeId
    INTO    l_superMayContainInhTypeId
    FROM    IBSDEV1.ibs_Type
    WHERE   id = l_superTypeId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'get data of super type';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    -- check if the super type was found:
    IF (l_sqlcode = 0)                  -- found super type?
    THEN 
        -- set the may contain inherited type id for all sub types of the
        -- current type which inherit the may contain entries from the
        -- current type:
        SET l_sqlcode = 0;

        UPDATE  IBSDEV1.ibs_Type
        SET     mayContainInheritedTypeId = l_superMayContainInhTypeId
        WHERE   mayContainInheritedTypeId = ai_typeId;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
        THEN                            -- an error occurred?
            SET l_ePos = 'update';
            GOTO exception1;            -- call common exception handler
        END IF; -- if an error occurred
    END IF; -- if found super type

    -- delete the entries of the actual type from the may contain table:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_MayContain
    WHERE   majorTypeId = ai_typeId
        OR  minorTypeId = ai_typeId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100)
    THEN                                -- an error occurred?
        SET l_ePos = 'delete';
        GOTO exception1;                -- call common exception handler
    END IF; -- if an error occurred

    -- finish the transaction:
    IF (l_retValue <> c_ALL_RIGHT)      -- there occurred a severe error?
    THEN
        ROLLBACK;                       -- undo changes
    END IF; -- there occurred a severe error

    -- make changes permanent and set new transaction starting point:
    COMMIT;

    -- return the state value:
    RETURN  l_retValue;

exception1:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_MayContain$deleteType', l_sqlcode, l_ePos,
        'ai_typeId', ai_typeId, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set new transaction starting point:
    COMMIT;
	
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_MayContain$deleteType
