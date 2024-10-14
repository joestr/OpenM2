-------------------------------------------------------------------------------
-- All stored procedures regarding the type table. <BR>
--
-- @version     $Revision: 1.7 $, $Date: 2003/10/21 22:14:51 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020820
-------------------------------------------------------------------------------


-------------------------------------------------------------------------------
-- Insert a new type. <BR>
-- This procedure inserts row to ibs_type. <BR>
-- @input parameters:
-- @ai_ID
-- @ai_OID
-- @ai_STATE
-- @ai_NAME
-- @ai_IDPROPERTY
-- @ai_SUPERTYPEID
-- @ai_MAYCONTAININHERITEDTYPEID
-- @ai_ISCONTAINER
-- @ai_ISINHERITABLE
-- @ai_ISSEARCHABLE
-- @ai_SHOWINMENU
-- @ai_SHOWINNEWS
-- @ai_CODE
-- @ai_NEXTPROPERTYSEQ
-- @ai_ACTVERSION
-- @ai_POSNO
-- @ai_DESCRIPTION
-- @ai_ICON
-- @ai_VALIDUNTIL
-- @ai_POSNOPATH
-- Result is number of inserted rows
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$insert');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$insert(
    -- input parameters:
    IN  ai_ID           INTEGER,
    IN  ai_STATE        INTEGER,
    IN  ai_NAME         VARCHAR (63),
    IN  ai_IDPROPERTY   INTEGER,
    IN  ai_SUPERTYPEID  INTEGER,
    IN  ai_POSNO        INTEGER,
    IN  ai_POSNOPATH    VARCHAR (254),
    IN  ai_ISCONTAINER  SMALLINT,
    IN  ai_ISINHERITABLE    SMALLINT,
    IN  ai_ISSEARCHABLE SMALLINT,
    IN  ai_SHOWINMENU   SMALLINT,
    IN  ai_SHOWINNEWS   SMALLINT,
    IN  ai_CODE         VARCHAR (63),
    IN  ai_NEXTPROPERTYSEQ  INTEGER,
    IN  ai_ACTVERSION   INTEGER,
    IN  ai_VALIDUNTIL   TIMESTAMP
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_ST_ACTIVE     INT;            -- active state
    DECLARE c_EMPTYPOSNOPATH VARCHAR (254);  -- default/invalid posNoPath
    DECLARE c_noTypeId      INT;            -- id of no type
    DECLARE c_CommonSuperType VARCHAR (63);  -- name of common super type

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
                                            -- actual type
    DECLARE l_count         INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ID            INTEGER;
    DECLARE l_OID           CHAR (8) FOR BIT DATA;
    DECLARE l_POSNO         INTEGER;
    DECLARE l_ICON          VARCHAR (63);
    DECLARE l_POSNOPATH     VARCHAR (254);
--    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_ALREADY_EXISTS    = 21;
    SET c_ST_ACTIVE         = 2;
    -- initialize local variables:
    SET l_retValue          = 0;
-- body:
    SET l_ID = ai_ID;

    IF l_ID = 0 THEN                      -- no id defined?
        -- set actual id:
        SELECT  COALESCE (MIN (id) + 16, 16842768) -- 0x01010010
        INTO    l_ID
        FROM    IBSDEV1.ibs_Type
        WHERE   id + 16 NOT IN
                (
                    SELECT  id
                    FROM    IBSDEV1.ibs_Type
                )
            AND id > 0;
    END IF;

    -- get position number:
    SELECT  COALESCE (MAX (posNo) + 1, 1)
    INTO    l_posNo
    FROM    IBSDEV1.ibs_type
    WHERE   superTypeId = ai_superTypeId
        AND id <> l_ID;

    IF ai_superTypeId <> 0 THEN            -- type is a subtype?
        -- set position path:
        -- compute the posNoPath as posNoPath of super type concatenated by
        -- the posNo of this type:
        SELECT  posNoPath || SUBSTRING (IBSDEV1.p_intToBinary (l_posNo), 3, 2)
        INTO    l_posNoPath
        FROM    IBSDEV1.ibs_Type
        WHERE   id = ai_superTypeId;
    ELSE
        -- type is not a subtype
        -- i.e. it is on top level
        -- compute the posNoPath as posNo of this object:
        SET l_posNoPath = SUBSTRING (IBSDEV1.p_intToBinary (l_posNo), 3, 2);
    END IF;

    CALL IBSDEV1.p_createOid (16851713, l_id, l_oid); -- 0x01012301
    SET l_icon = ai_code || '.gif';


    INSERT INTO IBSDEV1.ibs_Type
        (id, state, name, idProperty, superTypeId, posNo, posNoPath,  
        isContainer, isInheritable, isSearchable, showInMenu,
        showInNews, code, nextPropertySeq, actVersion, validUntil, oid, icon)
    VALUES (l_ID, ai_STATE, ai_NAME, ai_IDPROPERTY, ai_SUPERTYPEID,
         l_posNo, l_posNoPath, ai_ISCONTAINER, ai_ISINHERITABLE,
         ai_ISSEARCHABLE, ai_SHOWINMENU, ai_SHOWINNEWS, ai_CODE,
         ai_NEXTPROPERTYSEQ, ai_ACTVERSION, ai_VALIDUNTIL + 120 MONTH ,
         l_oid, l_icon );

    GET DIAGNOSTICS l_retValue = ROW_COUNT;

    RETURN l_retValue;
     
END;

-------------------------------------------------------------------------------
-- Create a new type. <BR>
-- This procedure also creates a first version of the type. <BR>
-- This procedure contains a TRANSACTION block, so it is not allowed to CALL IBSDEV1.it
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_id               Id of the type to be created.
--                              null => create new id.
-- @param   ai_name             Name of the type.
-- @param   ai_superTypeCode    Super type of the type. '' => default super type
--                              If there exists a type called 'BusinessObject'
--                              this type is generally the super type of all
--                              other types.
--                              If this type does not exist, but there exists
--                              another type having no super type that type is
--                              used as super type of the new type.
-- @param   ai_isContainer      Is the type a container. 1 => true
-- @param   ai_isInheritable    May the type be inherited, i.e. a sub type be
--                              created?
-- @param   ai_isSearchable     Is it possible to search for object of the type?
-- @param   ai_showInMenu       May instances of this type be displayed in a
--                              menu?
-- @param   ai_showInNews       May instances of this type be included in the
--                              news?
-- @param   ai_code             Code of the type (unique name).
-- @param   ai_className        Name of class which is responsible for the
--                              actual (= first, if type is new) version of the
--                              type.
--
-- @output parameters:
-- @param   ao_newId            New id = @id if @id <> null, a newly generated
--                              id otherwise.
-- @param   ao_newActVersionId  Id of the actual version for this type.
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_ALREADY_EXISTS         A type with this id already exists.
--                          (=> The data of the type is changed instead of newly
--                          created.)
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$new');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$new(
    -- input parameters:
    IN  ai_id               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_superTypeCode    VARCHAR (63),
    IN  ai_isContainer      SMALLINT,
    IN  ai_isInheritable    SMALLINT,
    IN  ai_isSearchable     SMALLINT,
    IN  ai_showInMenu       SMALLINT,
    IN  ai_showInNews       SMALLINT,
    IN  ai_code             VARCHAR (63),
    IN  ai_className        VARCHAR (63),
    -- output parameters:
    OUT ao_newId            INT,
    OUT ao_newActVersionId  INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_ST_ACTIVE     INT;            -- active state
    DECLARE c_EMPTYPOSNOPATH VARCHAR (254);  -- default/invalid posNoPath
    DECLARE c_noTypeId      INT;            -- id of no type
    DECLARE c_CommonSuperType VARCHAR (63);  -- name of common super type

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_id            INT;            -- the id of the type
    DECLARE l_superTypeId   INT;            -- id of super type of the
                                            -- actual type
    DECLARE l_count         INT;            -- counter
    DECLARE l_sqlcode       INT DEFAULT 0;
--    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_ALREADY_EXISTS    = 21;
    SET c_ST_ACTIVE         = 2;
    SET c_EMPTYPOSNOPATH    = X'00';
    SET c_noTypeId          = 0;
    SET c_CommonSuperType   = 'BusinessObject';
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_id                = ai_id;
    SET l_count             = 0;
    -- initialize return values:
    SET ao_newId            = 0;
-- body:
    COMMIT; -- finish previous and begin new TRANSACTION

    -- check if a type with this id already exists:
    SET l_sqlcode = 0;

    SELECT id, actVersion
    INTO l_id, ao_newActVersionId
    FROM IBSDEV1.ibs_Type
    WHERE ai_id <> c_noTypeId
        AND id = ai_id
        OR ai_id = c_noTypeId
        AND code = ai_code;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
        SET l_ePos = 'select 1';
        GOTO exception1;
    END IF;
    IF l_sqlcode <> 100 THEN 
        -- update typecode, showInMenu and IsInheritable which is set in
        -- createBaseObjectTypes
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_Type
        SET isInheritable = ai_isInheritable,
            isSearchable = ai_isSearchable,
            showInMenu = ai_showInMenu,
            showInNews = ai_showInNews,
            name = ai_name
        WHERE id = l_id;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'update type 2';
            GOTO exception1;
        END IF;
        -- update data within ibs_TVersion:
        SET l_sqlcode = 0;
        UPDATE IBSDEV1.ibs_TVersion
        SET className = ai_className
        WHERE id IN (   
                        SELECT actVersion 
                        FROM IBSDEV1.ibs_Type
                        WHERE id = l_id
                    );
    
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'update tversion 3';
            GOTO exception1;
        END IF;
       -- set return value:
        SET l_retValue = c_ALREADY_EXISTS;
    ELSE 
        -- get the super type id:
        SET l_sqlcode = 0;

        SELECT id
        INTO l_superTypeId
        FROM IBSDEV1.ibs_Type
        WHERE code = ai_superTypeCode;

        -- set super type id:
        IF l_sqlcode = 100 THEN 
            -- get default super type:
            SET l_sqlcode = 0;

            SELECT id 
            INTO l_superTypeId
            FROM IBSDEV1.ibs_Type
            WHERE code = c_CommonSuperType;

            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
                SET l_ePos = 'select default super type 4';
                GOTO exception1;
            END IF;

            IF l_sqlcode = 100 THEN 
                -- check if there exists at least one type and get the
                -- minimum id of all types having no super types:
                SET l_sqlcode = 0;

                SELECT coalesce(MIN (id),c_NoTypeId) 
                INTO l_superTypeId
                FROM IBSDEV1.ibs_Type
                WHERE superTypeId = c_noTypeId;

                -- check if there occurred an error:
                IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
                    SET l_ePos = 'select minimum super type 5';
                    GOTO exception1;
                END IF;
                -- check if there was at least one type found:
                IF l_sqlcode = 100 THEN 
                    -- actual type has no super type:
                    SET l_superTypeId = c_noTypeId;
                END IF;
            END IF;
        END IF;
        -- add the new type:
        -- (the id of the type itself is set as actual tVersionId until
        -- a version of the exists)
        SET l_sqlcode = 0;


        CALL IBSDEV1.p_Type$insert
            (l_id, c_ST_ACTIVE, ai_name, 1, l_supertypeid, 0, 
            c_EMPTYPOSNOPATH, ai_isContainer, ai_isInheritable,
            ai_isSearchable, ai_showInMenu, ai_showInNews, ai_code, 1, l_id,
            CURRENT TIMESTAMP);

        -- check if there occurred an error:
        GET DIAGNOSTICS l_count = RETURN_STATUS;
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
            SET l_ePos = 'insert 6';
            GOTO exception1;
        END IF;
        -- check if the row was correctly inserted:
        IF l_count > 0 THEN 
            -- get the type id:
            SET l_sqlcode = 0;
            SELECT MAX (id) 
            INTO l_id
            FROM IBSDEV1.ibs_Type
            WHERE code = ai_code;
      
            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
                SET l_ePos = 'get type id 7';
                GOTO exception1;
            END IF;
            -- create the new type version:
            CALL IBSDEV1.p_TVersion$new(l_id, ai_code, ai_className,
                ao_newActVersionId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            -- check if there was an error during creating the type version:
            IF l_retValue = c_ALL_RIGHT THEN 
                -- set the type version within the type:
                SET l_sqlcode = 0;
                UPDATE IBSDEV1.ibs_Type
                SET actVersion = ao_newActVersionId
                WHERE id = l_id;
                -- check if there occurred an error:
                IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN 
                    SET l_ePos = 'set type version 6';
                    GOTO exception1;
                END IF;
                -- inherit may contain entries from super type:
                IF l_superTypeId <> c_noTypeId THEN 
                    CALL IBSDEV1.p_MayContain$inherit(l_superTypeId, l_id);
                    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
                END IF;
            END IF;
        END IF;
    END IF;
    -- check if there occurred an error:
    IF l_retValue = c_ALL_RIGHT OR l_retValue = c_ALREADY_EXISTS THEN 
        COMMIT;
        SET ao_newId = l_id;
    ELSE 
        ROLLBACK;
    END IF;
    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;

NonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Type$new', l_sqlcode, l_ePos,
        'ai_id', ai_id, 'ai_name', ai_name, 'ai_isContainer', ai_isContainer,
        'ai_code', ai_code, 'ai_isInheritable', ai_isInheritable,
        'ai_className', ai_className, 'ai_isSearchable', ai_isSearchable,
        'ai_superTypeCode', ai_superTypeCode, 'ai_showInMenu', ai_showInMenu,
        '', '', 'ai_showInNews', ai_showInNews, '', '', 'l_id', l_id, '', '',
        'ao_newId', ao_newId, '', '', 'ao_newActVersionId',
        ao_newActVersionId, '', '', '', 0, '', '');
   COMMIT;
   -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Type$new


-------------------------------------------------------------------------------
-- Create a language dependent version of a type. <BR>
-- This procedure uses some mechanisms to get the required data and then
-- creates the type:
-- 1. The language dependent name of the type is determined.
-- 2. If there is no one found the type code is used as name of the type.
-- 3. The type is stored, i.e. if it exists it is changed, otherwise it is
-- created.
--
-- @input parameters:
-- @param   ai_id               The id of the type.
-- @param   ai_superTypeCode    The code of the super type.
-- @param   ai_isContainer      Is an object of this type a container?
-- @param   ai_isInheritable    May there be a sub type of the type?
-- @param   ai_isSearchable     Is it possible to search for object of the type?
-- @param   ai_showInMenu       Is an object of this type be shown in the menu?
-- @param   ai_showInNews       May instances of this type be included in the
--                              news?
-- @param   ai_code             The code of the type, i.e. its unique name
-- @param   ai_className        The name of the Java class which implements an
--                              object of this type.
-- @param   ai_languageId       The language for which the type shall be
--                              generated.
-- @param   ai_typeNameName     The id through which the language dependent name
--                              of the type can be found.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$newLang');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$newLang(
    -- input parameters:
    IN  ai_id               INT,
    IN  ai_superTypeCode    VARCHAR (63),
    IN  ai_isContainer      SMALLINT,
    IN  ai_isInheritable    SMALLINT,
    IN  ai_isSearchable     SMALLINT,
    IN  ai_showInMenu       SMALLINT,
    IN  ai_showInNews       SMALLINT,
    IN  ai_code             VARCHAR (63),
    IN  ai_className        VARCHAR (63),
    IN  ai_languageId       INT,
    IN  ai_typeNameName     VARCHAR (63))
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_id            INT;            -- the actual id
    DECLARE l_actTVId       INT;            -- the type version id
    DECLARE l_typeName      VARCHAR (63);    -- the name of the type
    DECLARE l_typeClass     VARCHAR (255);   -- the java class (deprecated)
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
-- body:
    CALL IBSDEV1.p_TypeName_01$get(ai_languageId, ai_typeNameName, l_typeName,
        l_typeClass);

    IF l_typeName IS NULL THEN 
        SET l_typeName = ai_code;
    END IF;
    CALL IBSDEV1.p_Type$new(ai_id, l_typeName, ai_superTypeCode, ai_isContainer,
        ai_isInheritable, ai_isSearchable, ai_showInMenu, ai_showInNews,
        ai_code, ai_className, l_id, l_actTVId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
END;
-- p_Type$newLang

-------------------------------------------------------------------------------
-- Add some tabs to a type. <BR>
-- The tabs for the types are defined. There can be up to 10 tabs defined
-- for each type.
-- In the SQL Server version the tab parameters are optional. <BR>
-- This procedure gets the actual version of the type and calls
-- p_TVersion$addTabs for this type version.
--
-- @input parameters:
-- @param   ai_code             The code of the type, i.e. its unique name
-- @param   ai_defaultTab       The code of the default tab.
-- @param   ai_tabCodeX         The code of the tab, i.e. the unique name.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$addTabs');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$addTabs(
    -- input parameters:
    IN  ai_code             VARCHAR (63),
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
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_id            INT;            -- the actual id
    DECLARE l_actTVId       INT;            -- the type version id
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- get the type data:
    SET l_sqlcode = 0;

    SELECT actVersion
    INTO l_actTVId
    FROM IBSDEV1.ibs_Type
    WHERE code = ai_code;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'get type data';
        GOTO exception1;
    END IF;
    -- create the tabs:
    CALL IBSDEV1.p_TVersion$addTabs(l_actTVId, ai_defaultTab, ai_tabCode1,
        ai_tabCode2, ai_tabCode3, ai_tabCode4, ai_tabCode5, ai_tabCode6,
        ai_tabCode7, ai_tabCode8, ai_tabCode9, ai_tabCode10);
        -- terminate the procedure:
    RETURN 0;
  
exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Type$addTabs', l_sqlcode, l_ePos,
        'l_actTVId',l_actTVId, 'ai_code', ai_code,
        '', 0, 'ai_defaultTab', ai_defaultTab, '', 0, 'ai_tabCode1', ai_tabCode1,
        '', 0, 'ai_tabCode2', ai_tabCode2, '', 0, 'ai_tabCode3', ai_tabCode3,
        '', 0, 'ai_tabCode4', ai_tabCode4, '', 0, 'ai_tabCode5', ai_tabCode5,
        '', 0, 'ai_tabCode6', ai_tabCode6, '', 0, 'ai_tabCode7', ai_tabCode7,
        '', 0, 'ai_tabCode8', ai_tabCode8);
END;
-- p_Type$addTabs

-------------------------------------------------------------------------------
-- Create a new type (no rights check). <BR>
-- This procedure calls p_Type$new, which contains a TRANSACTION block, so it
-- is not allowed to CALL IBSDEV1.this procedure from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_userId           ID of the user who is creating the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_name             Name of the object.
-- @param   ai_superTypeId      Super type of the type. 0 => no super type
-- @param   ai_isContainer      Is the type a container. 1 => true
-- @param   ai_isInheritable    May the type be inherited, i.e. a sub type be
--                              created?
-- @param   ai_isSearchable     Is it possible to search for object of the type?
-- @param   ai_showInMenu       May instances of this type be displayed in a
--                              menu?
-- @param   ai_showInNews       May instances of this type be included in the
--                              news?
-- @param   ai_code             Code of the type (unique name).
-- @param   ai_className        Name of class which is responsible for the
--                              actual (= first, if type is new) version of the
--                              type.
-- @param   ai_description      Description of the type.
--
-- @output parameters:
-- @param   ao_id               Id of the newly created type.
-- @param   ao_actVersionId     Id of the actual version for the new type.
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_superTypeId      INT,
    IN  ai_isContainer      SMALLINT,
    IN  ai_isInheritable    SMALLINT,
    IN  ai_isSearchable     SMALLINT,
    IN  ai_showInMenu       SMALLINT,
    IN  ai_showInNews       SMALLINT,
    IN  ai_code             VARCHAR (63),
    IN  ai_className        VARCHAR (63),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_id               INT,
    OUT ao_actVersionId     INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_superTypeCode VARCHAR (63);    -- the code of the super type
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_aisupertypeid_s  VARCHAR (63);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_ALL_RIGHT         = 1;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_superTypeCode     = '';
    SET ao_id               = 0;
    SET ao_actVersionId     = 0;

-- body:
    -- get the type code of the super type:
    SELECT  code
    INTO    l_superTypeCode
    FROM    IBSDEV1.ibs_Type
    WHERE   id = ai_superTypeId;

    -- create the new type and its first version:
    SET l_aisupertypeid_s = rtrim(CHAR (ai_superTypeId));
    CALL IBSDEV1.p_Type$new(0, ai_name, l_aisupertypeid_s,
        ai_isContainer, ai_isInheritable, ai_isSearchable, ai_showInMenu,
        ai_showInNews, ai_code, ai_className, ao_id, ao_actVersionId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Type$create


-------------------------------------------------------------------------------
-- Change the attributes of an existing type (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_id               ID of the type to be changed.
-- @param   ai_userId           ID of the user who is changing the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_name             Name of the object.
-- @param   ai_validUntil       Date until which the object is valid.
-- @param   ai_description      Description of the object.
-- @param   ai_idProperty       Id of the property used to represent the id of
--                              one object of this type.
-- @param   ai_superTypeId      Id of the superior type.
-- @param   ai_code             Code of the type.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$change(
    -- input parameters:
    IN  ai_id               INT,
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    -- type-specific parameters:
    IN  ai_idProperty       INT,
    IN  ai_superTypeId      INT,
    IN  ai_code             VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- type-specific parameters:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                                -- operation
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_rights        INT;            -- rights value
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- oid of the type
    DECLARE l_superTypeID   CHAR (8) FOR BIT DATA;     
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
-- body:
    -- get object id of type:
    SET l_sqlcode = 0;

    SELECT oid
    INTO l_oid
    FROM IBSDEV1.ibs_Type
    WHERE id = ai_id;

    -- check if the type exists:
    IF l_sqlcode = 0 THEN 
        -- get rights for this user
        CALL IBSDEV1.p_intToBinary( ai_superTypeId, l_superTypeID );
        CALL IBSDEV1.p_Rights$checkRights(l_oid, l_superTypeID, ai_userId,
            ai_op, l_rights);
        GET DIAGNOSTICS l_rights = RETURN_STATUS;
        -- check if the user has the necessary rights
        IF l_rights = ai_op THEN 
            -- update the properties of the type:
            UPDATE IBSDEV1.ibs_Type
            SET name = ai_name,
                validUntil = ai_validUntil,
                description = ai_description,
                idProperty = ai_idProperty,
                superTypeId = ai_superTypeId,
                code = ai_code
            WHERE id = ai_id;
            COMMIT;
        ELSE 
            -- set the return value with the error code:
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Type$change


-------------------------------------------------------------------------------
-- Get all data from a given type (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_id               Id of the type to be changed.
-- @param   ai_userId           Id of the user who is creating the type.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   ao_state            The object's state.
-- @param   ao_name             Name of the object itself.
-- @param   ao_validUntil       Date until which the object is valid.
-- @param   ao_description      Description of the object.
-- @param   ao_idProperty       Id of the property used to represent the id of
--                              one object of this type.
-- @param   ao_superTypeId      Id of the superior type.
-- @param   ao_isContainer      Is the type a container?
-- @param   ao_code             Code of the type.
-- @param   ao_nextPropertySeq  Sequence number of the next new property.
-- @param   ao_actVersionId     Id of the actual version.
-- @param   ao_actVersionSeq    Sequence number of the actual version.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$retrieve(
    -- input parameters:
    IN  ai_id               INT,
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- common output parameters:
    OUT ao_state            INT,
    OUT ao_name             VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    -- type-specific output parameters:
    OUT ao_idProperty       INT,
    OUT ao_superTypeId      INT,
    OUT ao_isContainer      SMALLINT,
    OUT ao_code             VARCHAR (63),
    OUT ao_nextPropertySeq  INT,
    OUT ao_actVersionId     INT,
    OUT ao_actVersionSeq    INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
                                                -- operation
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_rights        INT;            -- rights value
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- oid of the type
    DECLARE l_aiID          CHAR (8) FOR BIT DATA;     
    DECLARE l_superTypeID   CHAR (8) FOR BIT DATA;     
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
-- body:
    -- get id of superior type
    SET l_sqlcode = 0;

    SELECT superTypeId
    INTO ao_superTypeId
    FROM IBSDEV1.ibs_Type
    WHERE id = ai_id;

    -- check if the type exists:
    IF l_sqlcode = 0 THEN 
        CALL IBSDEV1.p_intToBinary( ao_superTypeId, l_superTypeID );
        CALL IBSDEV1.p_intToBinary( ai_ID, l_aiID );
        CALL IBSDEV1.p_Rights$checkRights(l_aiID, l_superTypeID, ai_userId,
            ai_op, l_rights);
        -- check if the user has the necessary rights
        IF l_rights = ai_op THEN 
            -- get the data of the type and return it
            SELECT t.oid, t.state, t.name, t.validUntil, t.description,
                t.idProperty, t.superTypeId, t.isContainer, t.code,
                t.nextPropertySeq, t.actVersion, tv.tVersionSeq
            INTO l_oid, ao_state, ao_name, ao_validUntil, ao_description,
                ao_idProperty, ao_superTypeId, ao_isContainer, ao_code,
                ao_nextPropertySeq, ao_actVersionId, ao_actVersionSeq
            FROM IBSDEV1.ibs_Type t LEFT OUTER JOIN
                IBSDEV1.ibs_TVersion tv ON t.actVersion = tv.id
            WHERE t.id = ai_id;
            -- set object as already read:
            CALL IBSDEV1.p_setRead(l_oid, ai_userId);
        ELSE 
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
   -- return the state value
    RETURN l_retValue;
END;
-- p_Type$retrieve

-------------------------------------------------------------------------------
-- Show all types. <BR>
--
-- @input parameters:
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_showTypes');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_showTypes()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
  
    DECLARE temp_cursor CURSOR WITH HOLD WITH RETURN FOR 
        SELECT CAST(id AS CHAR (4)) AS id,
            CAST(actVersion AS CHAR (4)) AS tVersionId,
            CAST(superTypeId AS CHAR (4)) AS superType,
            name 
        FROM IBSDEV1.ibs_Type;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    OPEN temp_cursor;
END;
-- p_showTypes


-------------------------------------------------------------------------------
-- Delete given type (including all its tversions). <BR>
-- 1. First ibs_Type is checked if there is an according id/code entry
--    If not: deletion will not be performed
--    (returns TYPE_MISMATCH).
-- 2. Each tversions will be deleted - if there exist activ objects
--    with given type then no deletion will be performed
--    (returns ALREADY_EXISTS).
-- This procedure contains a TRANSACTION block, so it is not allowed to CALL IBSDEV1.it
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   ai_typeId           Type for which all type versions shall be
--                              deleted.
-- @param   ai_code             Code of type (for security reasons)
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_TYPE_MISMATCH          Given id/code is not valid (no entry).
-- c_ALREADY_EXISTS         Found objects with this type(version)
--                          in ibs_Object - deletion not possible.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Type$deletePhysical');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Type$deletePhysical(
    -- input parameters:
    IN  ai_typeId           INT,
    IN  ai_code             VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_TYPE_MISMATCH INT;            -- a type mismatch occurred

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
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_ALREADY_EXISTS    = 21;
    SET c_TYPE_MISMATCH     = 99;
    -- initialize local variables:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rowCount          = 0;
-- body:
    -- check if the given id/code pair is an entry in ibs_Type:
    SET l_sqlcode = 0;

    SELECT COUNT (id) 
    INTO l_rowCount
    FROM IBSDEV1.ibs_Type
    WHERE ai_code = code
        AND ai_typeId = id;
    -- check if there occurred an error:

    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'check id/code pair';
        GOTO NonTransactionException;
    END IF;
    -- check if the id/code pair was found:
    IF l_rowCount = 0 THEN 
        -- set error code:
        SET l_retValue = c_TYPE_MISMATCH;
        SET l_ePos =
            l_ePos || ' Type Mismatch:' || ' ai_typeId = ' ||
            CAST(rtrim(CHAR (ai_typeId)) AS VARCHAR (30)) || ', ai_code = ' ||
            ai_code;
    
        CALL IBSDEV1.logError (500, 'p_Type$deletePhysical', l_sqlcode, l_ePos,
            'ai_typeId', ai_typeId, 'ai_code', ai_code, '', 0, '', '', '', 0, '', '', '', 0
            , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
            , '', '', '', 0, '', '');
    ELSE 
        -- type can be deleted
        -- delete all type versions of given type
        -- procedure possibly returns c_ALREADY_EXISTS
        CALL IBSDEV1.p_tVersion$deletePhysical(ai_typeId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        -- problems while deleting tversions?
        IF l_retValue = c_ALL_RIGHT THEN 
            -- delete the may contain entries for the type:
            CALL IBSDEV1.p_MayContain$deleteType(ai_typeId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            IF l_retValue = c_ALL_RIGHT THEN 
                -- delete the type:
                SET l_sqlcode = 0;
                DELETE FROM IBSDEV1.ibs_Type
                WHERE id = ai_typeId;
                -- check if there occurred an error:
                IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                    SET l_ePos = 'Error when deleting ibs_Type entry';
                    GOTO exception1;
                END IF;
            END IF;
        END IF;
        -- check if there occurred an error:
        IF l_retValue = c_ALL_RIGHT THEN 
            COMMIT;
        ELSE 
            ROLLBACK;
        END IF;
    END IF;
    -- return the state value:
    RETURN l_retValue;
  
exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
NonTransactionException:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Type$deletePhysical', l_sqlcode, l_ePos,
        'ai_typeId', ai_typeId, 'ai_code', ai_code, '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Type$deletePhysical
