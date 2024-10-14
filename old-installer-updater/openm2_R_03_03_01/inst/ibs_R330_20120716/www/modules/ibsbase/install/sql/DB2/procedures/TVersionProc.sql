-------------------------------------------------------------------------------
-- All stored procedures regarding the tVersion table. <BR>
-- 
-- @version     $Revision: 1.7 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------


-------------------------------------------------------------------------------
-- creates values and makes insert to ibs_tversion. <ZK>
-- @input parameters:
-- @param   ai_state            state of the version
-- @param   ai_typeId           id of type to which the version belongs
-- @param   ai_idProperty       property which represents the id
-- @param   ai_superTVersionId  id of actual version of super type
-- @param   ai_className        class which implements the business logic of the version
-- @param   ai_nextObjectSeq    sequence number for next object with this version
--
-- result
-- c_NOT_OK     no row inserted
-- c_ALL_RIGHT  data inserted 
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersion$insert');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersion$insert
(
    -- input parameters:
    IN  ai_state            INTEGER,
    IN  ai_typeId           INTEGER,
    IN  ai_idProperty       INTEGER,
    IN  ai_superTVersionId  INTEGER,
    IN  ai_className        VARCHAR (63),
    IN  ai_nextObjectSeq    INTEGER
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_noTVersionId  INT;            -- no version id

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_ID            INTEGER;         -- id of type version
    DECLARE l_TVERSIONSEQ   INTEGER;         -- sequence number of version
    DECLARE l_IDPROPERTY    INTEGER;         -- property which represents
                                            -- the id
    DECLARE l_ORDERPROPERTY INTEGER;         -- property used for ordering
                                            -- type
    DECLARE l_CODE          VARCHAR (63);     -- code of the version
                                            -- business logic of the version
    DECLARE l_POSNO         INTEGER;         -- the posNo of the tVersion
    DECLARE l_POSNOPATH     VARCHAR (254);    -- the posNoPath of the tVersion

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    COMMIT; -- finish previous and begin new TRANSACTION  
    
    SET l_sqlcode = 0;

    -- create a new sequence number:
    SELECT coalesce(MAX (tVersionSeq) + 1, 1)
    INTO l_TVERSIONSEQ
    FROM IBSDEV1.ibs_TVersion
    WHERE typeId = ai_typeId;
    -- compute the id as sum of typeId and sequnce number:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        SET l_ePos = 'create a new sequence number:';
        GOTO Exception1;
    END IF;

    SET l_sqlcode = 0;

    -- compute the id as sum of typeId and sequnce number:

    SET l_ID = IBSDEV1.b_OR (ai_typeId, l_TVERSIONSEQ);

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        SET l_ePos = 'compute the id as sum of typeId and sequnce number:';
        GOTO Exception1;
    END IF;

    SET l_sqlcode = 0;
    -- get position number:
    SELECT coalesce(MAX (posNo) + 1, 1)       
    INTO l_posNo
    FROM IBSDEV1.ibs_TVersion     
    WHERE superTVersionId = ai_superTVersionId 
        AND ID <> l_ID;

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        SET l_ePos = 'get position number:';
        GOTO Exception1;
    END IF;

    SET l_sqlcode = 0;
    -- get position path:
    IF ai_superTVersionId <> 0 THEN      
        -- tVersion is a subtversion?
        -- compute the posNoPath as posNoPath of super tVersion concatenated by
        -- the posNo of this tVersion:    
        SELECT  posNoPath || HEX( l_posNo )
        INTO    l_posNoPath
        FROM    IBSDEV1.ibs_TVersion                    
        WHERE   id = ai_superTVersionId;
    ELSE                                
        -- type is not a subtype
        -- i.e. it is on top level
        -- compute the posNoPath as posNo of this object:
        SET l_posNoPath = HEX( l_posNo );
    END IF;                             

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        SET l_ePos = 'get position path:';
        GOTO exception1;
    END IF;

    SET l_sqlcode = 0;

    SET l_idProperty = l_ID * 2; -- * 256;
    SET l_orderProperty = l_ID * 2; -- * 256;  

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        SET l_ePos = 'compute orderProperty and idProperty';
        GOTO exception1;
    END IF;

    INSERT  INTO IBSDEV1.ibs_TVersion
            (id, state, typeId, TVersionSeq, idProperty,
            orderproperty, superTVersionId, code, className,
            nextObjectSeq, posNo, posNoPath)
    VALUES  (l_id, ai_state, ai_typeId, l_TVersionSeq, l_idProperty,
            l_orderproperty, ai_superTVersionId, l_code, ai_className,
            ai_nextObjectSeq, l_posNo, l_posNoPath); 

    GET DIAGNOSTICS l_retValue = ROW_COUNT;

    if l_retValue > 0 THEN
        SET l_retValue = c_ALL_RIGHT;
    ELSE
        SET l_retValue = c_NOT_OK;
    END IF;

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        SET l_ePos = 'Insert';
        GOTO exception1;
    END IF;

    RETURN l_retValue;
exception1:

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersion$insert', l_sqlcode, l_ePos, 'l_id',
        l_id, 'ai_className', ai_className, '',  0, '',  '',
        '', 0, '', '', 'ai_superTVersionId',  ai_superTVersionId, '', '', '', 0, '', '', '', 0, '', '',
        '',  0, '', '', '',  0, '', '', '',  0, '', '', '',   0, '', '');
  
    -- return error code:
    RETURN c_NOT_OK;
END;

-------------------------------------------------------------------------------
-- Create a new type version. <BR>
-- This procedure contains a dynamic TRANSACTION block, so it is allowed to
-- CALL IBSDEV1.it from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_typeId           Type for which a new version shall be created.
-- @param   ai_code             Code of the type.
-- @param   ai_className        Name of java class (incl. packages) which
--                              implements the business logic of the new type
--                              version.
--
-- @output parameters:
-- @return  ao_id               id of the newly created tVersion.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersion$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersion$new(
    -- input parameters:
    IN  ai_typeId           INT,
    IN  ai_code             VARCHAR (63),
    IN  ai_className        VARCHAR (63),
    -- output parameters:
    OUT ao_id               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_noTVersionId  INT;            -- no version id

    -- local variables:
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_retValue      INT;            -- return value of this function
    DECLARE l_superTVersionId INT;          -- id of actual version of super
                                            -- type
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_noTVersionId      = 0;
  
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    COMMIT; -- finish previous and begin new TRANSACTION
    -- get the id of the actual version of the super type
    -- (if there exists a super type):
    SET l_sqlcode = 0;

    SELECT coalesce(st.actVersion,c_noTVersionId) 
    INTO l_superTVersionId
    FROM IBSDEV1.ibs_Type t LEFT OUTER JOIN IBSDEV1.ibs_Type st ON st.id = t.superTypeId
    WHERE t.id = ai_typeId;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'SELECT superTVersionId';
        GOTO exception1;
    END IF;
    -- store the tVersion's data in the table:
    -- within this step the following computations are done:
    -- + the state is set to active
    -- + the code is computed
    -- + the idProperty is initialized
    -- + the nextObjectSeq is initialized

    CALL IBSDEV1.p_TVersion$insert(2, ai_typeId, 0, l_superTVersionId,
        ai_className, 1);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- check if there occurred an error:
    IF l_retValue = c_NOT_OK THEN 
        SET l_ePos = 'p_TVersion$insert';
        GOTO exception1;
    END IF;
    -- get the newly created id:
    SET l_sqlcode = 0;

    SELECT MAX (id) 
    INTO ao_id
    FROM IBSDEV1.ibs_TVersion
    WHERE typeId = ai_typeId;
    -- check if there occurred an error:

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'SELECT ao_id';
        GOTO exception1;
    END IF;
    -- set the code:
    UPDATE  IBSDEV1.ibs_TVersion
    SET     code = ai_code || '_' ||
                CAST (CHAR (FLOOR (tVersionSeq / 10)) AS CHAR (1)) ||
                CAST (CHAR (tVersionSeq - 10 * FLOOR (tVersionSeq / 10)) AS CHAR (1))
    WHERE   id = ao_id;
  
    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'set code';
        GOTO exception1;
    END IF;

    IF l_superTVersionId <> c_noTVersionId THEN 
        -- inherit tVersionProc entries from super tVersion:
        CALL IBSDEV1.p_TVersionProc$inherit(l_superTVersionId, ao_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        -- inherit ConsistsOf entries from super tVersion:
        CALL IBSDEV1.p_ConsistsOf$inherit(l_superTVersionId, ao_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
    -- check if there occurred an error:
    IF l_retValue <> c_ALL_RIGHT THEN 
        ROLLBACK;
    END IF;
    -- finish the transaction:
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- roll back to the save point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersion$new', l_sqlcode, l_ePos, 'ai_typeId',
        ai_typeId, 'ai_code', ai_code, '',  0, 'ai_className', ai_className,
        'ao_id', ao_id, '', '', '',  0, '', '', '', 0, '', '', '', 0, '', '',
        '',  0, '', '', '',  0, '', '', '',  0, '', '', '',   0, '', '');
  
    COMMIT;
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_TVersion$new

-------------------------------------------------------------------------------
-- Add some tabs to a type. <BR>
-- The tabs for the types are defined. There can be up to 10 tabs defined
-- for each type.
-- In the SQL Server version the tab parameters are optional. <BR>
-- If the code of the defaultTab is not defined or it is not valid the tab
-- with the highest priority is set as default tab. <BR>
-- This procedure contains a dynamic TRANSACTION block, so it is allowed to
-- CALL IBSDEV1.it from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_tVersionId       The id of the type version for which to add the
--                              tabs.
-- @param   ai_defaultTab       The code of the default tab.
-- @param   ai_tabCodeX         The code of the tab, i.e. the unique name.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersion$addTabs');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersion$addTabs(
    -- input parameters:
    IN  ai_tVersionId       INT,
    IN  ai_defaultTab       VARCHAR (63),
    IN  ai_tabCode1         VARCHAR (63),
    IN  ai_tabCode2         VARCHAR (63),
    IN  ai_tabCode3         VARCHAR (63),
    IN  ai_tabCode4         VARCHAR (63),
    IN  ai_tabCode5         VARCHAR (63),
    IN  ai_tabCode6         VARCHAR (63),
    IN  ai_tabCode7         VARCHAR (63),
    IN  ai_tabCode8         VARCHAR (63),
    IN  ai_tabCode9         VARCHAR (63),
    IN  ai_tabCode10        VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_id            INT;            -- the actual id
    -- assign constants:
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_ALL_RIGHT         = 1;
    SET c_ALREADY_EXISTS    = 21;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    COMMIT; -- finish previous and begin new TRANSACTION
    -- set saveppoint:
    SAVEPOINT s_TVer_addTab ON ROLLBACK RETAIN CURSORS;
    
    -- create the tabs:
    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode1 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode1, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode2 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode2, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;

    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode3 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode3, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;

    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode4 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode4, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
  
    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS) AND
     (ai_tabCode5 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode5, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;

    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode6 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode6, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
  
    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode7 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode7, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;

    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode8 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode8, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;

    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode9 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode9, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
  
    IF (l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS)
        AND (ai_tabCode10 IS NOT NULL)
    THEN 
        CALL IBSDEV1.p_ConsistsOf$newCode(ai_tVersionId, ai_tabCode10, l_id);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    END IF;
  
    -- check for error:
    IF l_retValue = c_ALREADY_EXISTS THEN 
        -- everything o.k..
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT THEN 
        SET l_id = 0;
        SET l_sqlcode = 0;

        SELECT coalesce(c.id, 0) 
        INTO l_id
        FROM IBSDEV1.ibs_ConsistsOf c, IBSDEV1.ibs_Tab t
        WHERE t.code = ai_defaultTab
            AND t.id = c.tabId
            AND c.tVersionId = ai_tVersionId;

        SELECT coalesce(c.id, 0) 
        INTO l_rowcount
        FROM IBSDEV1.ibs_ConsistsOf c, IBSDEV1.ibs_Tab t
        WHERE t.code = ai_defaultTab
            AND t.id = c.tabId
            AND c.tVersionId = ai_tVersionId;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'get data of default tab';
            GOTO exception1;
        END IF;
        -- check if the tab was found:
        IF l_rowCount <= 0 THEN 
            -- get the tab with the highest priority:
     
            SET l_sqlcode = 0;
            SELECT coalesce(MAX (id), 0) 
            INTO l_id
            FROM IBSDEV1.ibs_ConsistsOf
            WHERE tVersionId = INTEGER(ai_defaultTab)
                AND priority >=
                    (
                        SELECT MAX (priority) 
                        FROM IBSDEV1.ibs_ConsistsOf
                        WHERE tVersionId = ai_tVersionId
                    );

            IF l_sqlcode <> 0  THEN 
                SET l_ePos = 'get tab with highest priority';
                GOTO exception1;
            END IF;
        END IF;
        -- set the active tab:
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_TVersion
        SET defaultTab = l_id
        WHERE id = ai_tVersionId;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'set the default tab';
            GOTO exception1;
        END IF;
    END IF;
  
    -- check if there occurred an error:
    IF l_retValue <> c_ALL_RIGHT THEN 
        -- roll back to the save point:
        ROLLBACK TO SAVEPOINT s_TVer_addTab;
    END IF;
    -- finish the transaction:
    COMMIT;

    -- release the savepoint:
    RELEASE s_TVer_addTab;

    -- terminate the procedure:
    RETURN 0;
exception1:
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_TVer_addTab;
    -- release the savepoint:
    RELEASE s_TVer_addTab;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_TVersion$addTabs', l_sqlcode, l_ePos,
        'ai_tVersionId', ai_tVersionId, 'ai_defaultTab', ai_defaultTab,
        '', 0, 'ai_tabCode1', ai_tabCode1, '', 0, 'ai_tabCode2', ai_tabCode2,
        '', 0, 'ai_tabCode3', ai_tabCode3, '', 0, 'ai_tabCode4', ai_tabCode4,
        '', 0, 'ai_tabCode5', ai_tabCode5, '', 0, 'ai_tabCode6', ai_tabCode6,
        '', 0, 'ai_tabCode7', ai_tabCode7, '', 0, 'ai_tabCode8', ai_tabCode8,
        '', 0, 'ai_tabCode9', ai_tabCode9);
  
    COMMIT;
END;
-- p_TVersion$addTabs

-------------------------------------------------------------------------------
-- Delete all type-versions of given type, including any entries in
-- ibs_ConsistsOf regarding this tVersion. <BR>
--
-- IMPORTANT: should ONLY be called from p_Type$deletePhysical
--
-- @input parameters:
-- @param   ai_typeId              Type for which all type versions shall be
--                                 deleted.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  ALREADY_EXISTS          Found objects with this type(version)
--                          in ibs_Object - deletion not possible
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TVersion$deletePhysical');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TVersion$deletePhysical(
    -- input parameters:
    IN  ai_typeid           INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_ALREADY_EXISTS INT;
    DECLARE l_retValue      INT;
    DECLARE l_rowCount      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
    SET c_ALL_RIGHT         = 1;
    SET c_ALREADY_EXISTS    = 21;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- check if there exist objects with tversionid(s) 
    -- of given code in ibs_Object
    SELECT INTEGER(o.oid )
    INTO l_rowCount
    FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_tVersion t
    WHERE t.typeId = ai_typeId
        AND t.id = o.tVersionId
        AND o.state = 2;
  
    IF l_rowCount > 0 THEN 
        SET l_retValue = c_ALREADY_EXISTS;
    ELSE 
        -- delete all consistOf entries of given tVersion:
        CALL IBSDEV1.p_ConsistsOf$deleteType(ai_typeId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        -- check if there occurred an error:
        IF l_retValue = c_ALL_RIGHT THEN 
            -- delete the tVersionProc entries for the tVersions of the type:
            CALL IBSDEV1.p_TVersionProc$deleteType(ai_typeId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            -- check if there occurred an error:
            IF l_retValue = c_ALL_RIGHT THEN 
                -- delete all entries with given type:
                DELETE FROM IBSDEV1.ibs_TVersion
                WHERE typeId = ai_typeId;
            END IF;
        END IF;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_TVersion$deletePhysical
