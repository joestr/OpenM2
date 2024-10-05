--------------------------------------------------------------------------------
-- All stored procedures regarding the ConsistsOf table. <BR>
-- 
-- @version     $Revision: 1.7 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020817
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Inherit the tuples from one tVersion to another tVersion. <BR>
-- If there are any tVersions currently inheriting their tuples from the second
-- tVersion they will also inherit their tuples from the first tVersion. <BR>
-- This function must be called from within a transaction handled code block
-- because it uses savepoints.
--
-- @input parameters:
-- @param   ai_majorTVersionId  Id of the major tVersion from which the tuples
--                              shall be inherited.
-- @param   ai_minorTVersionId  Id of minor tVersion to which the tuples shall
--                              be inherited.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$inherit');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$inherit
(
    -- input parameters:
    IN ai_majorTVersionId   INT,
    IN ai_minorTVersionId   INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_posNoPath     VARCHAR (254)/*POSNOPATH_VC*/; 
                                            -- the pos no path of the minor
                                            -- tVersion;
    DECLARE l_inheritedFrom INT/*TVERSIONID*/; -- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    SAVEPOINT s_ConsOf_inherit ON ROLLBACK RETAIN CURSORS;
    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- get the data of the tVersion to which to inherit the tuples:
    SET l_sqlcode = 0;

    SELECT MIN (tv.posNoPath), MIN (c.inheritedFrom) 
    INTO l_posNoPath, l_inheritedFrom
    FROM IBSDEV1.ibs_TVersion tv, IBSDEV1.ibs_ConsistsOf c
    WHERE tv.id = ai_minorTVersionId AND
        c.tVersionId = ai_minorTVersionId;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 THEN -- an error occurred?
        SET l_ePos = 'get minor tVersion data';
        GOTO exception1;                    -- call common exception handler
    END IF;
    -- delete the values for the minor tVersion and all
    -- tVersions below which inherit their values from the same
    -- TVersion as that tVersion:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_ConsistsOf
    WHERE tVersionId IN (
                            SELECT id 
                            FROM IBSDEV1.ibs_TVersion
                            WHERE id = ai_minorTVersionId OR
                                posNoPath LIKE l_posNoPath || '%'
                        )
        AND inheritedFrom = l_inheritedFrom;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'delete for act tVersion and tVersions below';
        GOTO exception1;                    -- call exception handler
    END IF;

    SET l_sqlcode = 0;
    -- add the records to the minor tVersion and all tVersions
    -- below which before inherited from the same tVersion as
    -- the minor tVersion:      
    INSERT INTO IBSDEV1.ibs_ConsistsOf 
        (tVersionId, tabId, priority, rights, inheritedFrom)
    SELECT tv.id, c.tabId, c.priority, c.rights, c.inheritedFrom
    FROM    IBSDEV1.ibs_ConsistsOf c, IBSDEV1.ibs_TVersion tv
    WHERE   (   tv.id = ai_minorTVersionId
                OR  tv.posNoPath LIKE l_posNoPath || '%'
            )
            AND tv.id NOT IN    (
                                    SELECT  tVersionId
                                    FROM IBSDEV1.   ibs_ConsistsOf
                                )
            AND c.tVersionId = ai_majorTVersionId;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'insert for act tVersion and tVersions below';
        GOTO exception1;                   -- call exception handler
    END IF;

    -- release the savepoint:
    RELEASE s_ConsOf_inherit;

    -- return the state value:
    RETURN l_retValue;
exception1:                              -- an error occurred
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_ConsistsOf$inherit', l_sqlcode, l_ePos,
        'ai_majorTVersionId', ai_majorTVersionId, '', '', 'ai_minorTVersionId',
        ai_minorTVersionId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
   
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_ConsOf_inherit;
    -- release the savepoint:
    RELEASE s_ConsOf_inherit;

    -- return the error code:
    RETURN c_NOT_OK;
END;

-- p_ConsistsOf$inherit

--------------------------------------------------------------------------------
-- Add a new tab to a type version. <BR>
-- It contains a TRANSACTION block, so it is not allowed to call this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_tVersionId       Id of type version for which to add a new tab.
-- @param   ai_code             The unique code of the tab.
--
-- @output parameters:
-- @param   ao_id               Id of the newly generated tuple.
-- @returns A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$newCode');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$newCode(
    -- input parameters:
    IN  ai_tVersionId       INT,
    IN  ai_tabCode          VARCHAR (63),
    -- output parameters:
    OUT ao_id               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_tabId         INT/*ID*/;      -- the id of the tab
    DECLARE l_kind          INT;            -- kind of the tab
    DECLARE l_tVersionId    INT/*TVERSIONID*/; -- tVersionId of the tab
    DECLARE l_fct           INT;            -- function of the tab
    DECLARE l_priority      INT;            -- priority of the tab
    DECLARE l_multilangKey  VARCHAR (63)/*NAME*/; -- the language key of the tab
    DECLARE l_rights        INT/*RIGHTS*/;  -- the necessary rights to display
                                            -- the tab
    DECLARE l_posNoPath     VARCHAR (254)/*POSNOPATH_VC*/;-- posNoPath of actual tVersion
    DECLARE l_inheritedFrom INT/*TVERSIONID*/;-- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;

    -- define cursor:
    -- get all tVersions for which to set the tab.
    DECLARE updateCursor CURSOR WITH HOLD FOR 
    SELECT id 
    FROM IBSDEV1.ibs_TVersion
    WHERE posNoPath LIKE l_posNoPath || '%' AND
        (
            id = ai_tVersionId
           OR
            id NOT IN   (
                            SELECT tVersionId 
                            FROM IBSDEV1.ibs_ConsistsOf
                            WHERE tabId = l_tabId OR
                                inheritedFrom <> ai_tVersionId
                        ) 
        );

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_ALREADY_EXISTS = 21;
    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- get the tab data:

    CALL IBSDEV1.p_Tab$get(0, ai_tabCode, l_tabId, l_kind, l_tVersionId,
        l_fct, l_priority, l_multilangKey, l_rights);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN 
        -- get the data of the actual tVersion:

        SET l_sqlcode = 0;

        SELECT posNoPath
        INTO l_posNoPath
        FROM IBSDEV1.ibs_TVersion
        WHERE id = ai_tVersionId;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'get tVersion data';
            GOTO NonTransactionException;   -- call common exception handler
        END IF;
        -- get the existing relationship data:
        SET l_sqlcode = 0;

        SELECT id
        INTO ao_id
        FROM IBSDEV1.ibs_ConsistsOf
        WHERE tVersionId = ai_tVersionId AND
            inheritedFrom = tVersionId AND
            tabId = l_tabId;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
            SET l_ePos = 'get relationship data';
            GOTO NonTransactionException;   -- call exception handler
        END IF;

        -- check if the relationship already exists:
        IF l_sqlcode = 100 THEN
            -- get the consistsOf data of the actual tVersion:
            SET l_sqlcode = 0;

            SELECT MIN (inheritedFrom) 
            INTO l_inheritedFrom
            FROM IBSDEV1.ibs_ConsistsOf
            WHERE tVersionId = ai_tVersionId;

            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'get tVersion consistsOf data';
                GOTO NonTransactionException; -- call common exception handler
            END IF;

            -- at this point we know that the operation may be done
            -- check if the major tVersion currently has own records within
            -- the consists of table or inherits its records from another
            -- tVersion:
            IF l_inheritedFrom <> ai_tVersionId THEN 
                -- delete the entries within the consists of table which
                -- are inherited from above the actual tVersion to one
                -- tVersion which is below the actual tVersion or to the
                -- actual tVersion itself:
                SET l_sqlcode = 0;
                DELETE FROM IBSDEV1.ibs_ConsistsOf
                WHERE tVersionId IN (
                                        SELECT id 
                                        FROM IBSDEV1.ibs_TVersion
                                        WHERE id = ai_tVersionId OR
                                            posNoPath LIKE l_posNoPath || '%'
                                    )
                    AND inheritedFrom = l_inheritedFrom;
                -- check if there occurred an error:
                IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                    SET l_ePos = 'delete inherited entries';
                    GOTO exception1;         -- call common exception handler
                END IF;
            END IF;
            -- open the cursor:
            OPEN updateCursor;

            -- get the first object:
            SET l_sqlcode = 0;
            FETCH FROM updateCursor INTO l_tVersionId;
            SET l_sqlstatus = l_sqlcode;

            -- loop through all objects:
            WHILE l_sqlcode <> 100 DO
                IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
                    -- insert the new records of the actual tVersion and its
                    -- sub tVersions into the consists of table:
                    SET l_sqlcode = 0;

                    INSERT INTO IBSDEV1.ibs_ConsistsOf
                        (tVersionId, tabId, priority, rights,
                        inheritedFrom)
                    VALUES (l_tVersionId, l_tabId, l_priority, l_rights,
                        ai_tVersionId);

                    -- check if there occurred an error:
                    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                        SET l_ePos = 'insert records';
                        GOTO cursorException; -- call exception handler
                    END IF;
                END IF;

                -- get next object:
                SET l_sqlcode = 0;
                FETCH FROM updateCursor INTO l_tVersionId;
                SET l_sqlstatus = l_sqlcode;
            END WHILE;

            -- close the not longer needed cursor:
            CLOSE updateCursor;
            -- finish the transaction:
            COMMIT;

            -- get the existing relationship data:
            SET l_sqlcode = 0;

            SELECT id
            INTO ao_id
            FROM IBSDEV1.ibs_ConsistsOf
            WHERE tVersionId = ai_tVersionId AND
                inheritedFrom = tVersionId AND
                tabId = l_tabId;

            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'get relationship data2';
                GOTO NonTransactionException; -- call exception handler
            END IF;
        END IF;
   END IF;
   -- check if there occurred an error:
    -- return the state value:
    RETURN l_retValue;

    cursorException:                        -- an error occurred within cursor
        -- close the not longer needed cursor:
        CLOSE updateCursor;
exception1:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;                           -- undo changes
NonTransactionException:                -- error outside of transaction occurred
        -- log the error:
    CALL IBSDEV1.logError (500, 'p_ConsistsOf$newCode', l_sqlcode, l_ePos,
        'ai_tVersionId', ai_tVersionId, 'ai_tabCode', ai_tabCode, 'l_tVersionId', l_tVersionId,
        '', '', 'ao_id', ao_id, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
 
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_ConsistsOf$newCode


--------------------------------------------------------------------------------
-- Ensure that a specific tab exists. <BR>
--
-- @input parameters:
-- @param   ai_code             The unique code of the tab.
-- @param   ai_tVersionId       Id of type version if tab shall be an object.
-- @param   ai_description      Description for the created tab.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$ensureTabExists');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$ensureTabExists(
    -- input parameters:
    IN ai_code              VARCHAR (63),
    IN ai_tVersionId        INT,
    IN ai_description       VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_TK_VIEW       INT;            -- tab kind VIEW
    DECLARE c_TK_OBJECT     INT;            -- tab kind OBJECT
    DECLARE c_TK_LINK       INT;            -- tab kind LINK
    DECLARE c_TK_FUNCTION   INT;            -- tab kind FUNCTION
    DECLARE c_languageId    INT;            -- the current language

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_tabId         INT/*ID*/;      -- the id of the tab
    DECLARE l_kind          INT;            -- kind of the tab
    DECLARE l_tVersionId    INT/*TVERSIONID*/; -- tVersionId of the tab
    DECLARE l_fct           INT;            -- function of the tab
    DECLARE l_priority      INT;            -- priority of the tab
    DECLARE l_multilangKey  VARCHAR (63)/*NAME*/; -- the language key of the tab
    DECLARE l_rights        INT/*RIGHTS*/;  -- the necessary rights to display
                                            -- the tab
    DECLARE l_sqlcode INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_ALREADY_EXISTS = 21;
    SET c_TK_VIEW = 1;
    SET c_TK_OBJECT = 2;
    SET c_TK_LINK = 3;
    SET c_TK_FUNCTION = 4;
    SET c_languageId = 0;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- get the tab data:
    CALL IBSDEV1.p_Tab$get(0, ai_code, l_tabId, l_kind, l_tVersionId,
        l_fct, l_priority, l_multilangKey, l_rights);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- check if the tab was found:
    IF l_retValue <> c_ALL_RIGHT THEN       -- tab was not found?
        -- compute the several values:
        IF ai_tVersionId <> 0 THEN          -- tab is an own object?
            SET l_kind = c_TK_OBJECT;
        ELSE                                -- tab is just a view
            SET l_kind = c_TK_VIEW;
        END IF;
        -- add the tab:
        CALL IBSDEV1.p_Tab$new(0, ai_code, l_kind, ai_tVersionId,
            51, 0, l_multilangKey, 0, l_tabId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- check if there occurred an error:
        IF l_retValue = c_ALL_RIGHT THEN 
            -- get the tab data:
            CALL IBSDEV1.p_Tab$get(0, ai_code, l_tabId, l_kind,
                l_tVersionId, l_fct, l_priority, l_multilangKey,
                l_rights);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;

            -- update description for consistency between
            -- language tables and tab table:
            CALL IBSDEV1.p_ObjectDesc_01$new(c_languageId, l_multilangKey, ai_code,
                ai_description, '');
        END IF; -- if everything o.k.
    END IF; -- if tab was not found

    -- return the state value:
    RETURN l_retValue;

exception1:                              -- an error occurred
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_ConsistsOf$ensureTabExists',
        l_sqlcode, l_ePos,
        'ai_tVersionId', ai_tVersionId, 'ai_code', ai_code, '', 0, '',
        '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '',
        '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_ConsistsOf$ensureTabExists

--------------------------------------------------------------------------------
-- Create a new entry within ibs_ConsistsOf <BR>
-- It contains a TRANSACTION block, so it is not allowed to call this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_majorTVersionId  Id of majorType for which to define a minorType.
-- @param   ai_minorTVersionId  Id of minorType belonging to majorType.
-- @param   ai_name             Name of the object to be created of the 
--                              minorType.
-- @param   ai_description      Description for the created object.
--
-- @output parameters:
-- @param   ao_id               Id of the newly generated tuple.
-- @returns A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         An entry for this tab already exists.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$new(
    -- input parameters:
    IN  ai_majorTVersionId  INT,
    IN  ai_minorTVersionId  INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_id               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_languageId    INT;            -- the actual language

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_ALREADY_EXISTS = 21;
    SET c_languageId = 0;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- get the tab data:
    CALL IBSDEV1.p_ConsistsOf$ensureTabExists(ai_majorTVersionId, ai_name);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- call common procedure for creating a tVersion/tab relationship:
    CALL IBSDEV1.p_ConsistsOf$newCode(ai_majorTVersionId, ai_name, ao_id);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- return the state value:
    RETURN l_retValue;

exception1:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;                               -- undo changes

NonTransactionException:              -- error outside of transaction occurred

    CALL IBSDEV1.logError (500, 'p_ConsistsOf$new', l_sqlcode, l_ePos,
        'ai_majorTVersionId', ai_majorTVersionId, 'ai_name', ai_name,
        'ai_minorTVersionId', ai_minorTVersionId,
        'ai_description', ai_description, 'ao_id', ao_id, '', '', '', 0,
        '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0,
        '', '', '', 0, '', '', '', 0, '', '');

    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_ConsistsOf$new


--------------------------------------------------------------------------------
-- Delete a tab from a tVersion. <BR>
-- If this is the last tab defined for this tVersion and
-- inheritFromUpper is set to 1, the tVersion (and its sub tVersions)
-- automatically inherits the records from its super tVersion.
-- If the required tuple is not found this is no severe error. So the second
-- operation of inheriting from the super tVersion is also done in the same way.
-- It contains a TRANSACTION block, so it is not allowed to call this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_tVersionId       Id of the tVersion for which a procedure
--                              shall be deleted.
-- @param   ai_tabCode          Unique code of the tab to be deleted.
-- @param   ai_inheritFromSuper In case that there are no more records for
--                              the tVersion after deleting the requested
--                              record this parameter tells whether the tVersion
--                              shall inherit the records from its super
--                              tVersion or not.
--                              Default: 1 (= true)
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
-- c_OBJECTNOTFOUND         The required tuple to be deleted was not found.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$delete(
    -- input parameters:
    IN ai_tVersionId            INT,
    IN ai_tabCode               VARCHAR (63),
    IN ai_inheritFromSuper      SMALLINT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE SQLCODE         INT;            
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT;           -- tuple not found

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_tabId         INT/*ID*/;      -- the id of the tab
    DECLARE l_kind          INT;            -- kind of the tab
    DECLARE l_tVersionId    INT/*TVERSIONID*/; -- tVersionId of the tab
    DECLARE l_fct           INT;            -- function of the tab
    DECLARE l_priority      INT;            -- priority of the tab
    DECLARE l_multilangKey  VARCHAR (63)/*NAME*/;-- the language key of the tab
    DECLARE l_rights        INT/*RIGHTS*/;  -- the necessary rights to display
                                            -- the tab
    DECLARE l_posNoPath     VARCHAR (254)/*POSNOPATH_VC*/;-- the pos no path of the tVersion
    DECLARE l_inheritedFrom INT/*TVERSIONID*/;-- id of tVersion from which the
                                            -- actual tVersion has inherited
                                            -- its entries
    DECLARE l_superTVersionId INT/*TVERSIONID*/;-- Id of super tVersion of the
                                            -- actual tVersion
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_OBJECTNOTFOUND = 3;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- get the tab data:
    CALL IBSDEV1.p_Tab$get(0, ai_tabCode, l_tabId, l_kind, l_tVersionId,
        l_fct, l_priority, l_multilangKey, l_rights);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT THEN        -- everything o.k.?
        -- get the data of the actual tVersion:
        SET l_sqlcode = 0;
        SELECT tv.posNoPath, tv.superTVersionId, c.inheritedFrom 
        INTO l_posNoPath, l_superTVersionId, l_inheritedFrom
        FROM IBSDEV1.ibs_TVersion tv, IBSDEV1.ibs_ConsistsOf c
        WHERE tv.id = ai_TVersionId AND
            c.tVersionId = ai_TVersionId AND
            c.tabId = l_tabId;

        SELECT COUNT(*) 
        INTO l_rowcount
        FROM IBSDEV1.ibs_TVersion tv, IBSDEV1.ibs_ConsistsOf c
        WHERE tv.id = ai_TVersionId
            AND c.tVersionId = ai_TVersionId
            AND c.tabId = l_tabId;
    
        -- check if there occurred an error:
        IF ( l_sqlcode <> 0 AND l_sqlcode <> 100 ) OR l_rowCount <= 0 THEN -- an error occurred?
            SET l_ePos = 'get data of actual tVersion';
            GOTO NonTransactionException;   -- call exception handler
        END IF;

        -- check if the type version currently has own records within the
        -- consists of table or inherits its records from another tVersion:
        IF l_inheritedFrom = ai_tVersionId THEN 
            -- not inherited from another tVersion?
            -- at this point we know that the operation may be done
            -- delete the record in the tVersion itself and all inherited
            -- ones in the sub tVersions:
            SET l_sqlcode = 0;
            DELETE FROM IBSDEV1.ibs_ConsistsOf
            WHERE   (
                        tVersionId = ai_tVersionId OR
                        inheritedFrom = ai_tVersionId
                    ) AND
                tabId = l_tabId;

            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'delete';
                GOTO exception1;             -- call common exception handler
            END IF;

            -- check if there are any records for the actual tVersion left:
            IF NOT EXISTS   (
                                SELECT tVersionId 
                                FROM IBSDEV1.ibs_ConsistsOf
                                WHERE tVersionId = ai_tVersionId
                            )
            THEN                            -- no record left for this tVersion?
                -- check if the tVersion shall inherit from the super
                -- tVersion:
                IF ai_inheritFromSuper = 1 AND l_superTVersionId <> 0 THEN 
                    -- inherit the entries from the super tVersion:
                    CALL IBSDEV1.p_ConsistsOf$inherit(l_superTVersionId, ai_tVersionId);
                    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
                END IF; -- if inherit from super tVersion
            END IF; -- if no record left for this tVersion

            -- finish the transaction:
            IF l_retValue <> c_ALL_RIGHT AND l_retValue <> c_OBJECTNOTFOUND THEN 
                                            -- there occurred a severe error?
                ROLLBACK;                   -- undo changes
            ELSE                            -- there occurred no error
                COMMIT;                     -- make changes permanent
            END IF;
        END IF; -- if not inherited from another tVersion
    END IF;-- if everything o.k.

    -- return the state value:
    RETURN l_retValue;

exception1:                              -- an error occurred
    -- roll back to the beginning of the transaction:

    ROLLBACK;                           -- undo changes

NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    CALL IBSDEV1.logError (5008, 'p_ConsistsOf$delete', l_sqlcode, l_ePos,
        'ai_tVersionId', ai_tVersionId, 'ai_tabCode', ai_tabCode,
        'ai_inheritFromSuper', ai_inheritFromSuper, '', '', '', 0, '', '',
        '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0,
        '', '', '', 0, '', '', '', 0, '', '');

    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_ConsistsOf$delete


--------------------------------------------------------------------------------
-- Delete all occurrences of a code out of the ConsistsOf table. <BR>
-- It contains a TRANSACTION block, so it is not allowed to call this procedure
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_tabCode          The code of the tab to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$deleteCode');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$deleteCode(
    -- input parameters:
    IN ai_tabCode           VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;
-- body:
    -- delete the entries of the tab from the ConsistsOf table:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_ConsistsOf
    WHERE tabId IN  (
                        SELECT id 
                        FROM IBSDEV1.ibs_Tab
                        WHERE code = ai_tabCode
                    );

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'delete';
        GOTO exception1;                     -- call common exception handler
    END IF;

    -- finish the transaction:
    COMMIT;

    -- return the state value:
    RETURN l_retValue;

exception1:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;                           -- undo changes
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_ConsistsOf$deleteCode', l_sqlcode, l_ePos,
        '', 0, 'ai_tabCode', ai_tabCode, '', 0, '', '', '', 0, '', '', '', 0,
        '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '',
        '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_ConsistsOf$deleteCode

--------------------------------------------------------------------------------
-- Delete all occurrences of a specific tVersion out of the ConsistsOf table.
-- <BR>
-- If the tVersion is used to inherit entries to sub tVersions the sub tVersions
-- will inherit their entries from the super tVersions of the tVersion. <BR>
-- This function must be called from within a transaction handled code block
-- because it uses savepoints.
--
-- @input parameters:
-- @param   ai_tVersionId       Id of the tVersion to be deleted.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$deleteTVersion');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$deleteTVersion(
    IN ai_tVersionId INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_superTVersionId INT;/*TVERSIONID*/ -- Id of super tVersion of the
                                            -- actual tVersion;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    SAVEPOINT s_ConsOf_delTVer ON ROLLBACK RETAIN CURSORS;
    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;
    -- get the data of the tVersion:
    SET l_sqlcode = 0;

    SELECT superTVersionId
    INTO l_superTVersionId
    FROM IBSDEV1.ibs_TVersion
    WHERE id = ai_tVersionId;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
        SET l_ePos = 'get data of tVersion';
        GOTO exception1;                     -- call common exception handler
    END IF;

    -- check if the super tVersion id was found:
    IF l_sqlcode = 0 THEN 
        -- inherit all entries from the super tVersion:
        -- the consequence of this action is, that no sub tVersion will have
        -- inherited values from this tVersion
        CALL IBSDEV1.p_ConsistsOf$inherit(l_superTVersionId, ai_tVersionId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        -- check if there was an error:
        IF l_retValue <> c_ALL_RIGHT THEN   -- an error occurred?
            GOTO exception1;                 -- call common exception handler
        END IF;
    ELSE                                    -- the super tVersion was not found
        -- delete the entries of the actual tVersion and all entries which were
        -- inherited from this tVersion from the consists of table:
        SET l_sqlcode = 0;
        DELETE FROM IBSDEV1.ibs_ConsistsOf
        WHERE tVersionId = ai_tVersionId OR
            inheritedFrom = ai_tVersionId;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'delete';
            GOTO exception1;                 -- call common exception handler
        END IF;
    END IF; -- else the super tVersion was not found

    -- release the savepoint:
    RELEASE s_ConsOf_delTVer;

    -- return the state value:
    RETURN l_retValue;                      

exception1:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_ConsOf_delTVer;
    -- release the savepoint:
    RELEASE s_ConsOf_delTVer;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_ConsistsOf$deleteTVersion',
        l_sqlcode,l_ePos,
        'ai_tVersionId', ai_tVersionId, '', '', '', 0, '', '', '', 0, '', '',
        '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0,
        '', '', '', 0, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_ConsistsOf$deleteTVersion
--------------------------------------------------------------------------------
-- Delete all occurrences of tVersions belonging to a specific type out of the
-- ConsistsOf table. <BR>
-- If any tVersion of the type is used to inherit entries to sub tVersions the
-- sub tVersions will inherit their entries from the super tVersions of the
-- specific tVersion. <BR>
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
CALL IBSDEV1.p_dropProc ('p_ConsistsOf$deleteType');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ConsistsOf$deleteType(
    IN  ai_typeId           INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_tVersionId    INT;/*TVERSIONID*/ -- id of actual tVersion;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;

    -- define cursor:
    -- get all tVersions of the type which shall be deleted.
    DECLARE updateCursor CURSOR WITH HOLD FOR 
    SELECT id 
    FROM IBSDEV1.ibs_TVersion
    WHERE typeId = ai_typeId;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;
-- body:
    COMMIT; -- finish previous and begin new TRANSACTION
    -- open the cursor:
    OPEN updateCursor;

    -- get the first object:
    SET l_sqlcode = 0;
    FETCH FROM updateCursor INTO l_tVersionId;
    SET l_sqlstatus = l_sqlcode;

    -- loop through all objects:
    WHILE l_sqlcode <> 100 AND l_retValue = c_ALL_RIGHT DO
                                         -- another object found?
        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
            -- delete the entries for the actual tVersion:
            CALL IBSDEV1.p_ConsistsOf$deleteTVersion(l_tVersionId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;
        -- get next object:
        SET l_sqlcode = 0;
        FETCH FROM updateCursor INTO l_tVersionId;
        SET l_sqlstatus = l_sqlcode;
    END WHILE;
    -- close the not longer needed cursor:
    CLOSE updateCursor;

    -- finish the transaction:
    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT THEN        -- everything all right?
        COMMIT;                              -- make changes permanent
    ELSE                                    -- an error occured
        ROLLBACK;                           -- undo changes
    END IF;

    -- return the state value:
    RETURN l_retValue;
exception1:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;                           -- undo changes
NonTransactionException:                -- error outside of transaction occurred
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_ConsistsOf$deleteType', l_sqlcode, l_ePos,
    'ai_typeId', ai_typeId, '', '', '', 0, '', '', '', 0, '', '', '', 0,
    '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '',
    '', 0, '', '', '', 0, '', '');
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_ConsistsOf$deleteType
