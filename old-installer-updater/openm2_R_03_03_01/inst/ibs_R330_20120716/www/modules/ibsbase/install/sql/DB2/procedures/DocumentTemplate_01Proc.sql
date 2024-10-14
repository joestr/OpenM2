--------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_DocumentTemplate_01 table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2005/01/25 16:44:56 $
--              $Author: bernd $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020830
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
-- This procedure contains a TRANSACTION block, so it is not allowed to call it
-- from within another TRANSACTION block.
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_MAS_FILE      INT;
    DECLARE c_MAS_HYPERLINK INT;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_MAS_FILE          = 1;
    SET c_MAS_HYPERLINK     = 2;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    CALL IBSDEV1.p_Attachment_01$create(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN

        CALL IBSDEV1.p_stringToByte (ao_oid_s, l_oid);

        -- additional attributes for db-mapping
        INSERT INTO IBSDEV1.ibs_DocumentTemplate_01
            (oid, objectType, typeName, className, iconName, mayExistIn,
            mayContain, isContainerType, objectSuperType, isSearchable,
            isInheritable, showInMenu, showInNews, typeId, tVersionId,
            systemDisplayMode, dbMapped, tableName, procCopy, mappingInfo,
            workflowTemplateOid, attachmentCopy, logDirectory, showDOMTree)
        VALUES  (l_oid, '', '', '', '', '',  '', 0, '', 0,
            0, 0, 0, 0, 0,
            0, 0, '', '', '',
            c_NOOID, '', '', 0);
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_DocumentTemplate_01$create


--------------------------------------------------------------------------------
-- Registers a tab for a document template.<BR>
--
-- @input parameters:
-- @param   ai_oid              The oid of the document template.
-- @param   ai_typeCode         The type code of the document template.
-- @param   ai_tabPos           The position number (id) of the tab.
--
-- @return  A value representing the state of the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$regTab');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$regTab(
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_typeCode         VARCHAR (63),
    IN  ai_tabPos           INT
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOT_OK        INT;            -- error

    -- local variables:
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_count         INT;            -- counter
    DECLARE l_id            INT;            -- the position id of the tab
    DECLARE l_kind          INT;            -- the kind of the tab
    DECLARE l_tVersionId    INT;            -- the tVersionId of the tab object
    DECLARE l_fct           INT;            -- the function of the tab
    DECLARE l_priority      INT;            -- the priority of the tab
    DECLARE l_name          VARCHAR (63);    -- the name of the tab
    DECLARE l_desc          VARCHAR (255);   -- the description of the tab
    DECLARE l_tabCode       VARCHAR (63);    -- the tab code in the ibs_tab
    DECLARE l_class         VARCHAR (255);   -- the class to show tab view
    DECLARE l_multilangKey  VARCHAR (63);    -- the multilang key in the ibs_tab
    DECLARE l_domainId      INT;            -- id of the current domain
    DECLARE l_retValue      INT;
    DECLARE l_msg           VARCHAR (255);
    DECLARE l_tabId         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialization:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET l_id                = 0;
    SET l_kind              = 0;
    SET l_tVersionId        = 0;
    SET l_fct               = 0;
    SET l_priority          = 0;
    SET l_name              = '';
    SET l_desc              = '';
    SET l_tabCode           = '';
    SET l_class             = '';
    SET l_retValue          = c_NOT_OK;
    SET l_msg               = '';

-- body:

    -- ATTENTION!!
    -- !! NOT YET SUPPORTED BY THE BASE !!!
    -- The domain id must be set to the domain id of the document template
    -- object.

    -- SELECT @l_domainId = u.domainId
    -- FROM ibs_Object o, ibs_User u
    -- WHERE o.oid = @ai_oid AND u.id = o.owner
    SET l_domainId = 0;

    -- select the tab with the given id and document template oid
    SET l_sqlcode = 0;
    SELECT id, kind,tVersionId, fct, priority, name,
        description, tabCode, class
    INTO l_id, l_kind, l_tVersionId, l_fct, l_priority, l_name, l_desc,
        l_tabCode, l_class
    FROM IBSDEV1.ibs_TabTemplate_01
    WHERE oid = ai_oid
       AND id = ai_tabPos;

    SELECT COUNT(*)
    INTO l_rowcount
    FROM IBSDEV1.ibs_TabTemplate_01
    WHERE oid = ai_oid
        AND id = ai_tabPos;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
        SET l_ePos = 'select';
        GOTO exception1;
    END IF;

    IF l_count = 1 THEN
        -- remove all leading/trailing spaces from the tab name:
        SET l_name = ltrim(rtrim(l_name));
        -- if no tab name is given set the type name from the ibs_Type table:
        IF (l_name IS NULL) OR l_name = '' THEN
            SELECT name
            INTO l_name
            FROM IBSDEV1.ibs_Type t, IBSDEV1.ibs_TVersion tv
            WHERE t.id = tv.typeId
                AND tv.id = l_tVersionId;
        END IF;

        -- if no tabCode is given set tabcode to oid concatenated with typeCode:
        IF (l_tabCode IS NULL) OR l_tabCode = '' THEN
            -- the unique tab code is composed by the oid of the template,
            -- the type code of the form and the id of the tab.
            CALL IBSDEV1.p_byteToString (ai_oid, l_tabCode);

            SET l_tabCode = l_tabCode || '_' || SUBSTR(ai_typeCode, 1, 40) || '_' ||
                CAST(rtrim(CHAR (l_id)) AS VARCHAR (10));
        END IF;
        -- use the tab code as multilang key:
        SET l_multilangKey = l_tabCode;
        -- check if the tab already exists
        IF NOT EXISTS   (
                            SELECT id
                            FROM IBSDEV1.ibs_Tab
                            WHERE code = l_tabCode
                                AND domainId = l_domainId
                        ) THEN
            CALL IBSDEV1.p_Tab$new(l_domainId, l_tabCode, l_kind, l_tVersionId,
                l_fct, l_priority, l_multilangKey, 0,
                l_class, l_tabId);
        END IF;

        -- try to update the tab name and description
        SET l_sqlcode = 0;

        UPDATE IBSDEV1.ibs_ObjectDesc_01
        SET objName = l_name,
            objDesc = l_desc,
            className = 'ibs.bo.ObjectDesc'
        WHERE languageId = 0 AND name = l_multilangKey;

        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
            SET l_ePos = 'update';
            GOTO exception1;
        END IF;

        IF l_rowcount = 0 THEN
            -- set the tab name and description for the new tab type:
            SET l_sqlcode = 0;

            INSERT INTO IBSDEV1.ibs_ObjectDesc_01
                ( languageId, name, objName, objDesc, className)
            VALUES ( 0, l_multilangKey, l_name, l_desc, 'ibs.bo.ObjectDesc');

            IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN -- an error occurred?
                SET l_ePos = 'ibs.bo.ObjectDesc';
                GOTO exception1;
            END IF;
        END IF;

        -- add the tab to the consistsof table:
        CALL IBSDEV1.p_Type$addTabs(ai_typeCode, '', l_tabCode);
        -- set the return code:
        SET l_retValue = c_ALL_RIGHT;
    END IF;

    -- return the state value:
    RETURN l_retValue;

exception1:

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_DocumentTemplate_01$regTab', l_sqlcode, l_ePos,
        'ai_tabPos', ai_tabPos, 'ai_typeCode', ai_typeCode, 'l_id', l_id,
        'l_name', l_name, 'l_kind', l_kind, 'l_desc', l_desc, 'l_tVersionId', l_tVersionId,
        'l_tabCode', l_tabCode, 'l_fct', l_fct, 'l_class', l_class, 'l_priority', l_priority
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_DocumentTemplate_01$regTab


--------------------------------------------------------------------------------
-- Inserts/Updates the type information for the document type
-- in the ibs_Type table.<BR>
--
-- @input parameters:
-- @param   ai_oid              The oid of the template.
-- @param   ai_typeId           The type id (0 for a new type).
-- @param   ai_typeCode         The type code.
-- @param   ai_typeName         The type name.
-- @param   ai_className        The java class name.
-- @param   ai_iconName         The icon name.
-- @param   ai_mayExistIn       List of container type codes where objects of
--                              this type are allowed to exist in.
-- @param   ai_isContainer      true if the type is a container.
-- @param   ai_mayContain       List of object type codes whitch this container
--                              is allowed to contain.
--
-- @output parameters:
-- @param   ao_typeId           The resulting type id.
-- @param   ao_tVersionId       The resulting tVersion id.
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_NOT_OK                 Some error occurred in the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$newType');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$newType(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_typeId           INT,
    IN  ai_typeCode         VARCHAR (63),
    IN  ai_typeName         VARCHAR (63),
    IN  ai_className        VARCHAR (63),
    IN  ai_iconName         VARCHAR (63),
    IN  ai_mayExistIn       VARCHAR (2000),
    IN  ai_isContainerType  SMALLINT,
    IN  ai_mayContain       VARCHAR (255),
    IN  ai_superTypeCode    VARCHAR (63),
    IN  ai_isSearchable     SMALLINT,
    IN  ai_isInheritable    SMALLINT,
    IN  ai_isShowInMenu     SMALLINT,
    IN  ai_isShowInNews     SMALLINT,

    -- output parameters:
    OUT ao_typeId           INT,
    OUT ao_tVersionId       INT
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_languageId    INT;

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_count         INT;            -- counter
    DECLARE l_code          VARCHAR (63);
    DECLARE l_idx           INT;
    DECLARE l_len           INT;
    DECLARE l_tabId         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- add the additional tabs:
    -- define cursor: get all tabs from the ibs_TabTemplate_01 table
    DECLARE tab_Cursor CURSOR WITH HOLD FOR
    SELECT id
    FROM IBSDEV1.ibs_TabTemplate_01
    WHERE oid = ai_oid
    ORDER BY id;

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
    SET c_languageId        = 0;

    -- initialize local variables:
    SET l_retValue          = c_NOT_OK;
    SET l_count             = 0;

-- body:
    -- if the typeId is 0 register the new type
    IF ai_typeId = 0 THEN
        -- check if a type with this code name already exists:
        -- if the type not exists register the type
        IF NOT EXISTS   (
                            SELECT id
                            FROM IBSDEV1.ibs_Type
                            WHERE code = ai_typeCode
                        ) THEN
            -- drop surrounding spaces:
            SET ai_superTypeCode = ltrim(rtrim(ai_superTypeCode));
            -- determinate the type id of the super type:
            -- if no super type code is set use the default type
            IF (ai_superTypeCode IS NULL) OR ai_superTypeCode = '' THEN
                -- set the default type code of the super type:
                IF ai_isContainerType > 0 THEN
                    SET ai_superTypeCode = 'Container';
                ELSE
                    SET ai_superTypeCode = 'XMLViewer';
                END IF;
            END IF;
            -- register the new type:
            CALL IBSDEV1.p_Type$newLang(ai_typeId, ai_superTypeCode,
                ai_isContainerType, ai_isInheritable, ai_isSearchable,
                ai_isShowInMenu, ai_isShowInNews, ai_typeCode,
                ai_className, c_languageId, ai_typeName);
        END IF;
    END IF;

    -- get the typeId and tVersionId of the type:
    SET l_sqlcode = 0;

    SELECT id, actVersion
    INTO ao_typeId, ao_tVersionId
    FROM IBSDEV1.ibs_Type
    WHERE code = ai_typeCode;

    SELECT COUNT(*)
    INTO l_rowcount
    FROM IBSDEV1.ibs_Type
    WHERE code = ai_typeCode;

    -- check if there occurred an error:
    IF ( l_sqlcode <> 0 AND l_sqlcode <> 100 ) OR l_count <> 1 THEN -- an error occurred?
        SET l_ePos = 'type not created';
        GOTO exception1;
    END IF;
    -- add the default tabs:
    -- if the new type is a container add the content tab, too
    IF ai_isContainerType > 0 THEN
        CALL IBSDEV1.p_Type$addTabs(ai_typeCode, '', 'Content', 'Info',
            'References', 'Rights');
    ELSE
        CALL IBSDEV1.p_Type$addTabs(ai_typeCode, '', 'Info', 'References',
            'Rights');
    END IF;
    -- open the cursor:
    OPEN tab_Cursor;

    -- get the first tab:
    SET l_sqlcode = 0;
    FETCH FROM tab_Cursor INTO l_tabId;
    -- loop through all found tabs:
    WHILE l_sqlcode <> 100 DO
        -- register the tab:
        CALL IBSDEV1.p_DocumentTemplate_01_regTab(ai_oid, ai_typeCode, l_tabId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        -- get next tab:
        SET l_sqlcode = 0;
        FETCH FROM tab_Cursor INTO l_tabId;
    END WHILE;
    -- set the correct type and icon name in ibs_Type:
    SET l_sqlcode = 0;
    UPDATE IBSDEV1.ibs_Type
    SET name = ai_typeName,
        icon = ai_iconName
    WHERE code = ai_typeCode;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'setting the type name and icon';
        GOTO exception1;
    END IF;
    -- delete existing may contain entrys for the type
    -- EXEC p_MayContain$deleteType @ao_typeId

    -- set the may contain entrys for the new type
    -- the parameter '@ai_mayExistIn' holds a comma separated list
    -- of the container type codes.

    -- if the container code list is empty set the default container codes
    -- like the XMLViewer_01.

    -- remove all leading and trailing spaces:
    SET ai_mayExistIn = ltrim(rtrim(ai_mayExistIn));

    -- if not defined set the default type codes for form objects:
    IF ai_mayExistIn = '' THEN
        SET ai_mayExistIn = 'Container,ExportContainer,MasterDataContainer';
    END IF;

    -- ensure that there are no leading spaces:
    SET ai_mayExistIn = ltrim(ai_mayExistIn);

    WHILE ai_mayExistIn > '' DO
        -- separate the container type codes:
        SET l_code = '';
        SET l_len = LENGTH(ai_mayExistIn);

        SET l_idx = LOCATE(',', ai_mayExistIn);
        -- check if the comma (',') was found:
        IF l_idx > 0 THEN
            -- get the next code:
            SET l_code = rtrim(SUBSTR(ai_mayExistIn, 1, l_idx - 1));
            -- get the rest of the string:
            SET ai_mayExistIn =
                ltrim(SUBSTR(ai_mayExistIn, l_idx + 1, l_len - l_idx));
        ELSE
            -- get the rest of the string as code:
            SET l_code = rtrim(ai_mayExistIn);
            -- no more entries:
            SET ai_mayExistIn = '';
        END IF;
        -- check if the code could be found:
        IF l_code > '' THEN
            -- add the maycontain entry:
            CALL IBSDEV1.p_MayContain$new(l_code, ai_typeCode);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        END IF;
    END WHILE;

    -- if the new type is a container set the correct maycontain entries:
    IF ai_isContainerType > 0 THEN
        -- the parameter 'ai_mayContain' holds a comma separated list
        -- of the type codes.
        -- ensure that there are no leading spaces:
        SET ai_mayContain = ltrim(ai_mayContain);
        WHILE ai_mayContain > '' DO
            -- separate the object type codes:
            SET l_code = '';
            SET l_len = LENGTH(ai_mayContain);
            SET l_idx = LOCATE(',', ai_mayContain);
            -- check if the comma (',') was found:
            IF l_idx > 0 THEN
                -- get the next code:
                SET l_code = rtrim(SUBSTR(ai_mayContain, 1, l_idx - 1));
                -- get the rest of the string:
                SET ai_mayContain =
                    ltrim(SUBSTR(ai_mayContain, l_idx + 1, l_len - l_idx));
            ELSE
                -- get the rest of the string as code:
                SET l_code = ai_mayContain;
                -- no more entries:
                SET ai_mayContain = '';
            END IF;
            -- check if the code could be found:
            IF l_code > '' THEN
                -- add the maycontain entry:
                CALL IBSDEV1.p_MayContain$new(ai_typeCode, l_code);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            END IF;
        END WHILE;
    END IF;
    -- set the result value:
    SET l_retValue = c_ALL_RIGHT;
   -- return the state value:
    RETURN l_retValue;
exception1:
    -- roll back to the beginning of the transaction:
    ROLLBACK;
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_DocumentTemplate_01$newType', l_sqlcode, l_ePos,
        'ai_typeId', ai_typeId, 'ai_typeCode', ai_typeCode,
        'ai_isContainerType', ai_isContainerType, 'ai_typeName', ai_typeName,
        'ai_isSearchable', ai_isSearchable, 'ai_className', ai_className,
        'ai_isInheritable', ai_isInheritable, 'ai_iconName', ai_iconName,
        'ai_isShowInMenu', ai_isShowInMenu, 'ai_mayExistIn', ai_mayExistIn,
        'ai_isShowInNews', ai_isShowInNews,'ai_mayContain', ai_mayContain,
        'l_tabId', l_tabId, 'l_code', l_code, 'ao_typeId', ao_typeId,
        '', '', 'ao_tVersionId', ao_tVersionId, '', '', '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_DocumentTemplate_01$newType


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         Display object in the news.
--
-- ...
--
-- @param   @objectType         the object type code
-- @param   @typeName           the object type name
-- @param   @className          the java class name
-- @param   @iconName           the icon name
-- @param   @mayExistIn         list of container type codes
-- @param   @isContainerType    defines the template a container type
-- @param   @mayContain         list of type codes that can appare in this container
-- @param   @superTypeCode      the type code of the super type
-- @param   @isSearchable       is the type searchable
-- @param   @isInheritable      is the type inheritable
-- @param   @isShowInMenu       the 'show in menu' flag for the type
-- @param   @isShowInNews       the 'show in news' flag for the type
-- @param   @systemDisplayMode  display mode for the system section of the object
-- @param   @dbMapped           objects of this type are db-mapped
-- @param   @tableName          the mapping table
-- @param   @procCopy           the name of the copy procedure
-- @param   @wfTemplateOid_s    the workflow template oid
-- @param   @attachmentCopy     the name of the copy procedure for attachments (XMLDATA)
-- @param   @showDOMTree        flag to show the DOM tree to the user
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$change(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_isMaster         SMALLINT,
    IN  ai_attachmentType   INT,
    IN  ai_filename         VARCHAR (255),
    IN  ai_path             VARCHAR (255),
    IN  ai_url              VARCHAR (255),
    IN  ai_filesize         REAL,
    IN  ai_isWeblink        SMALLINT,
    IN  ai_objectType       VARCHAR (63),
    IN  ai_typeName         VARCHAR (63),
    IN  ai_className        VARCHAR (63),
    IN  ai_iconName         VARCHAR (63),
    IN  ai_mayExistIn       VARCHAR (255),
    IN  ai_isContainerType  SMALLINT,
    IN  ai_mayContain       VARCHAR (255),
    IN  ai_superTypeCode    VARCHAR (63),
    IN  ai_isSearchable     SMALLINT,
    IN  ai_isInheritable    SMALLINT,
    IN  ai_isShowInMenu     SMALLINT,
    IN  ai_isShowInNews     SMALLINT,
    IN  ai_systemDisplayMode INT,
    IN  ai_dbMapped         SMALLINT,
    IN  ai_tableName        VARCHAR (30),
    IN  ai_procCopy         VARCHAR (30),
    IN  ai_wfTemplateOid_s  VARCHAR (18),
    IN  ai_attachmentCopy   VARCHAR (30),
    IN  ai_showDOMTree      SMALLINT
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- operation was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- converted input parameter
    DECLARE l_wfTemplateOid CHAR (8) FOR BIT DATA;        -- converted input parameter
    DECLARE l_typeID        INT;
    DECLARE l_tVersionID    INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

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
    SET l_oid               = c_NOOID;

-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Attachment_01$change(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, ai_isMaster,
        ai_attachmentType, ai_filename, ai_path, ai_url, ai_filesize,
        ai_isWeblink);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN
        CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
        CALL IBSDEV1.p_stringToByte (ai_wfTemplateOid_s, l_wfTemplateOid);
        -- for a new type the typeId is 0 and we have
        -- to register the new type to get a new typeId.
        -- if the type is not new (typeId != 0) the type
        -- informations are updated

        SELECT typeID, tVersionId
        INTO l_typeID, l_tVersionID
        FROM IBSDEV1.ibs_DocumentTemplate_01
        WHERE oid = l_oid;

        -- register the new type with the given type code and class name
        CALL IBSDEV1.p_DocumentTemplate_01_newType(l_oid, l_typeID, ai_objectType,
            ai_typeName, ai_className, ai_iconName, ai_mayExistIn,
            ai_isContainerType, ai_mayContain, ai_superTypeCode,
            ai_isSearchable, ai_isInheritable, ai_isShowInMenu,
            ai_isShowInNews, l_typeId, l_tVersionId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF l_retValue = c_ALL_RIGHT THEN
            -- update further information:
            UPDATE IBSDEV1.ibs_DocumentTemplate_01
            SET objectType = ai_objectType,
                typeId = l_typeId,
                tVersionId = l_tVersionId,
                typeName = ai_typeName,
                className = ai_className,
                iconName = ai_iconName,
                mayExistIn = ai_mayExistIn,
                isContainerType = ai_isContainerType,
                mayContain = ai_mayContain,
                objectSuperType = ai_superTypeCode,
                isSearchable = ai_isSearchable,
                isInheritable = ai_isInheritable,
                showInMenu = ai_isShowInMenu,
                showInNews = ai_isShowInNews,
                systemDisplayMode = ai_systemDisplayMode,
                dbMapped = ai_dbMapped,
                tableName = ai_tableName,
                procCopy = ai_procCopy,
                workflowTemplateOid = l_wfTemplateOid,
                attachmentCopy = ai_attachmentCopy,
                logDirectory = ai_objectType,
                showDOMTree = ai_showDOMTree
            WHERE oid = l_oid;
        END IF;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DocumentTemplate_01$change


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   ai_oid_s            ID of the object to be changed.
-- @param   ai_userId           ID of the user who is creating the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   ao_state            The object's state.
-- @param   ao_tVersionId       ID of the object's type (correct version).
-- @param   ao_typeName         Name of the object's type.
-- @param   ao_name             Name of the object itself.
-- @param   ao_containerId      ID of the object's container.
-- @param   ao_containerKind    Kind of object/container relationship.
-- @param   ao_isLink           Is the object a link?
-- @param   ao_linkedObjectId   Link if isLink is true.
-- @param   ao_owner            ID of the owner of the object.
-- @param   ao_creationDate     Date when the object was created.
-- @param   ao_creator          ID of person who created the object.
-- @param   ao_lastChanged      Date of the last change of the object.
-- @param   ao_changer          ID of person who did the last change to the
--                              object.
-- @param   ao_validUntil       Date until which the object is valid.
-- @param   ao_description      Description of the object.
-- @param   ao_showInNews       Display the object in the news.
-- @param   ao_checkedOut       Is the object checked out?
-- @param   ao_checkOutDate     Date when the object was checked out.
-- @param   ao_checkOutUser     Id of the user which checked out the object.
-- @param   ao_checkOutUserOid  Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user.
-- @param   ao_checkOutUserName Name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut user.
--
-- ...                          attachment parameters
--
-- @param   ao_objectType       The object type name.
-- @param   ao_typeName         The object type name.
-- @param   ao_className        The java class name.
-- @param   ao_iconName         The icon name.
-- @param   ao_mayExistIn       The info for may contain.
-- @param   ao_isContainerType  true if the type is a container.
-- @param   ao_mayContain       The info for may contain.
-- @param   ao_superTypeCode    The type code of the super type.
-- @param   ao_isSearchable     Is the type searchable.
-- @param   ao_isInheritable    Is the type inheritable.
-- @param   ao_isShowInMenu     The 'show in menu' flag for the type.
-- @param   ao_isShowInNews     The 'show in news' flag for the type.
-- @param   ao_typeId           The type id.
-- @param   ao_tVersionId       The tVersion id.
-- @param   ao_systemDisplayMode Display mode for the system section of the
--                              object.
-- @param   ao_dbMapped         Object of this type are db-mapped.
-- @param   ao_tableName        The mapping table name.
-- @param   ao_procCopy         The copy procedure for the mapping.
-- @param   ao_wfTemplateOid    The oid of the workflow template.
-- @param   ao_wfTemplateName   The name of the workflow template.
-- @param   ao_attachmentCopy   The copy procedure for attachments (XMLDATA).
-- @param   ao_logDirectory     The name of the directory where the log of
--                              translation should be stored.
-- @param   ao_showDOMTree      Flag to show the DOM tree to the user.
--
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_INSUFFICIENT_RIGHTS    User has no right to perform action.
-- c_OBJECTNOTFOUND         The required object was not found within the
--                          database.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$retrieve(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_isMaster         SMALLINT,
    OUT ao_attachmentType   INT,
    OUT ao_filename         VARCHAR (255),
    OUT ao_path             VARCHAR (255),
    OUT ao_url              VARCHAR (255),
    OUT ao_filesize         REAL,
    OUT ao_isWeblink        SMALLINT,
    OUT ao_objectType       VARCHAR (63),
    OUT ao_objectTypeName   VARCHAR (63),
    OUT ao_className        VARCHAR (63),
    OUT ao_iconName         VARCHAR (63),
    OUT ao_mayExistIn       VARCHAR (255),
    OUT ao_isContainerType  SMALLINT,
    OUT ao_mayContain       VARCHAR (255),
    OUT ao_superTypeCode    VARCHAR (63),
    OUT ao_isSearchable     SMALLINT,
    OUT ao_isInheritable    SMALLINT,
    OUT ao_isShowInMenu     SMALLINT,
    OUT ao_isShowInNews     SMALLINT,
    OUT ao_objectTypeId     INT,
    OUT ao_objectTVersionId INT,
    OUT ao_systemDisplayMode INT,
    OUT ao_dbMapped         SMALLINT,
    OUT ao_tableName        VARCHAR (30),
    OUT ao_procCopy         VARCHAR (30),
    OUT ao_wfTemplateOid    CHAR (8) FOR BIT DATA,
    OUT ao_wfTemplateName   VARCHAR (63),
    OUT ao_attachmentCopy   VARCHAR (30),
    OUT ao_logDirectory     VARCHAR (255),
    OUT ao_showDOMTree      SMALLINT
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- operation was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- converted input parameter
                                            -- oid_s
    DECLARE l_sqlcode       INT DEFAULT 0;

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
    SET l_oid               = c_NOOID;

-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Attachment_01$retrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
        ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, ao_isMaster,
        ao_attachmentType, ao_filename, ao_path, ao_url, ao_filesize,
        ao_isWeblink);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN
        CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

        SELECT objectType, typeName, className, iconName,
            mayExistIn, isContainerType, mayContain, objectSuperType,
            isSearchable, isInheritable, showInMenu, showInNews,
            typeId, tVersionId, systemDisplayMode, dbMapped,
            tableName, procCopy, workflowTemplateOid,
            attachmentCopy, logDirectory, showDOMTree
        INTO ao_objectType, ao_objectTypeName, ao_className, ao_iconName,
            ao_mayExistIn, ao_isContainerType, ao_mayContain, ao_superTypeCode,
            ao_isSearchable, ao_isInheritable, ao_isShowInMenu, ao_isShowInNews,
            ao_objectTypeId, ao_objectTVersionId, ao_systemDisplayMode,
            ao_dbMapped, ao_tableName, ao_procCopy, ao_wfTemplateOid,
            ao_attachmentCopy, ao_logDirectory, ao_showDOMTree
        FROM IBSDEV1.ibs_DocumentTemplate_01
        WHERE oid = l_oid;

        -- if a workflow is defined get the name of the workflow object
        IF ao_wfTemplateOid <> c_NOOID THEN
            SELECT name
            INTO ao_wfTemplateName
            FROM IBSDEV1.ibs_Object
            WHERE oid = ao_wfTemplateOid;
        END IF;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_DocumentTemplate_01$retrieve


--------------------------------------------------------------------------------
-- Deletes a XMLViewer_01 object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$delete
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_RIGHT_DELETE  INT;
    DECLARE c_OBJECTISREFERENCED INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) for BIT DATA;
    DECLARE l_rights        INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_typeId        INT;
    DECLARE l_tVersionId    INT;
    DECLARE l_domainId      INT;
    DECLARE l_typeCode      VARCHAR (63);
    DECLARE l_tabCode       VARCHAR (63);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_help_PosNoPath VARCHAR (255);

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    SET c_RIGHT_DELETE      = 16;
    SET c_OBJECTISREFERENCED = 61;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;
    SET l_containerId       = c_NOOID;
    SET l_typeId            = 0;
    SET l_tVersionId        = 0;
    SET l_domainId          = 0;
    SET l_typeCode          = '';
    SET l_tabCode           = '';

-- body:
    ------------------------------------------------------------------------
    -- conversionS (VARCHAR (18)) - all input objectids must be converted
    ------------------------------------------------------------------------
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    SET l_retValue = c_OBJECTISREFERENCED;
    -- check if the document template is referenced by other objects
    IF EXISTS   (
                    SELECT oid
                    FROM IBSDEV1.V_DOCUMENTTEMPLATE_01$REF
                    WHERE oid = l_oid
                ) THEN
        SET l_rowcount = 1;
    ELSE
        SET l_rowcount = 0;
    END IF;

    -- no references found ?
    IF l_rowcount = 0 THEN
        -- all references and the object itself are deleted (plus rights)
        CALL IBSDEV1.p_Attachment_01$delete(ai_oid_s, ai_userId, ai_op);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        -- drop all type informations for this template
        IF l_retValue = c_ALL_RIGHT THEN
            -- first get the tVersionId of the type defined by
            -- the document template

            SELECT  tVersionId, typeId, objectType
            INTO    l_tVersionId, l_typeId, l_typeCode
            FROM    IBSDEV1.ibs_DocumentTemplate_01
            WHERE   oid = l_oid;

            -- if the template defines a valid m2 type
            IF l_tVersionId <> 0 AND l_typeId <> 0 THEN
                -- ATTENTION!!
                -- !! NOT YET SUPPORTED BY THE BASE !!!
                -- The domain id must be set to the domain id of the
                -- document template object.
                SET l_domainId = 0;
                -- SELECT @l_domainId = u.domainId
                -- FROM ibs_Object o, ibs_User u
                -- WHERE o.oid = @oid AND u.id = o.owner
                -- delete all rows in the ibs_Attachments_01 table
                -- who are part
                -- of a xml viewer object with this template oid
                DELETE FROM IBSDEV1.ibs_Attachment_01
                WHERE oid IN    (
                                    SELECT oid
                                    FROM IBSDEV1.ibs_XMLViewer_01
                                    WHERE templateOid = l_oid
                                );
                -- delete all rows in the
                -- ibs_KeyMapper table who are referenced
                -- by a xml viewer object with this template oid
                DELETE FROM IBSDEV1.ibs_KeyMapper
                WHERE oid IN    (
                                    SELECT oid
                                    FROM IBSDEV1.ibs_XMLViewer_01
                                    WHERE templateOid = l_oid
                                );

                -- delete all form objects (and tabs) with this tVersionId
                SET l_help_PosNoPath = (
                                        SELECT posNoPath
                                        FROM IBSDEV1.  ibs_Object
                                        WHERE   oid = l_oid
                                        );
                DELETE FROM IBSDEV1.ibs_Object
                WHERE oid IN    (
                                    SELECT o.oid
                                    FROM IBSDEV1. ibs_Object o
                                    WHERE o.posNoPath like l_help_PosNoPath || '%'
                                    AND   o.tVersionId = l_tVersionId
                                );

                -- delete all rows in the XMLViewer_01 table
                -- with this template oid
                DELETE FROM IBSDEV1.ibs_XMLViewer_01
                WHERE templateOid = l_oid;

                -- delete the m2 type
                CALL IBSDEV1.p_Type$deletePhysical(l_typeId, l_typeCode);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;

                -- delete the tabs and object descriptions
                -- ATTENTION!! this should be done by a
                -- specific delete procedure.
                -- and with other tabcodes - because tabcodes are not
                -- only generated, but set.
                CALL IBSDEV1.p_byteToString (l_oid, l_tabCode);

                DELETE FROM IBSDEV1.ibs_Tab
                WHERE domainId = l_domainId
                    AND code LIKE l_tabCode || '_%';

                -- delete consist of entries
                DELETE FROM IBSDEV1.ibs_ConsistsOf
                WHERE tVersionId = l_tVersionId;

                -- delete the tab description too
                -- ATTENTION!! this should be done by a
                -- specific delete procedure.
                DELETE FROM IBSDEV1.ibs_ObjectDesc_01
                WHERE name LIKE l_tabCode || '_%';

                -- delete all references from and to objects of this type:
                CALL IBSDEV1.p_Reference$deleteTVersion(l_tVersionId);
            END IF;
            -- delete the document template object and its
            -- tabs from ibs_object
            SET l_help_PosNoPath =  (
                                        SELECT posNoPath
                                        FROM IBSDEV1.  ibs_Object
                                        WHERE   oid = l_oid
                                    );
            DELETE FROM IBSDEV1.ibs_Object
            WHERE oid IN    (
                                SELECT o.oid
                                FROM IBSDEV1.ibs_Object o
                                WHERE o.posNoPath LIKE l_help_PosNoPath || '%'
                            );

            -- delete the document template object itself
            DELETE FROM IBSDEV1.ibs_DocumentTemplate_01
            WHERE oid = l_oid;

            -- delete the row in the ibs_Attachment_01 table
            DELETE FROM IBSDEV1.ibs_Attachment_01
            WHERE oid = l_oid;

            -- delete the reference in the key mapper table
            DELETE FROM IBSDEV1.ibs_KeyMapper
            WHERE oid = l_oid;

            -- delete the tab templates
            DELETE FROM IBSDEV1.ibs_TabTemplate_01
            WHERE oid = l_oid;
        END IF;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_DocumentTemplate_01$delete


--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
--
-- @input parameters:
-- @param   @oid              ID of the object to be copied.
-- @param   @userId           ID of the user who copy the object.
-- @param   @newOid           The new Oid of the new created BusinessObject.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$BOCopy(
    IN  ai_oid              CHAR (8) FOR BIT DATA, -- the oid of Object we want to copy
    IN  ai_userId           INT,            -- the userId of the User who wants to copy
    IN  ai_newOid           CHAR (8) FOR BIT DATA  -- the new OID of the copied Object
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- operation was o.k.

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_ALL_RIGHT         = 1;
-- body:
    CALL IBSDEV1.p_Attachment_01$BOCopy(ai_oid, ai_userId, ai_newOid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN
        -- make a insert for all your typespecific tables:
        INSERT  INTO ibs_DocumentTemplate_01
            (oid, objectType, systemDisplayMode, dbMapped, tableName,
             procCopy, mappingInfo, workflowTemplateOid)
        SELECT  ai_newOid, objectType, systemDisplayMode, dbMapped, tableName,
            procCopy, mappingInfo, workflowTemplateOid
        FROM IBSDEV1.   ibs_DocumentTemplate_01
        WHERE   oid = ai_oid;
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        -- check if the insert has processed correctly:
        IF l_rowcount >= 1 THEN
            SET l_retvalue = c_All_RIGHT;
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DocumentTemplate_01$BOCopy


--------------------------------------------------------------------------------
-- Insert/Update the informations for a document template tab in the table
-- ibs_TabTemplate_01.<BR>
--
-- @input parameters:
-- @param   ai_oid_s            Oid of the associated document template.
-- @param   ai_id               The number of the tab.
-- @param   ai_kind             The tab kind.
-- @param   ai_code             The type code of the object.
-- @param   ai_fct              The function of the tab.
-- @param   ai_name             The name of the tab.
-- @param   ai_desc             The description of the tab.
-- @param   ai_code             The code of the tab.
-- @param   ai_class            The class to show the tab view.
--
-- @return  A value representing the state of the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_TabTemplate_01$addTab');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_TabTemplate_01$addTab(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_id               INT,
    IN  ai_kind             INT,
    IN  ai_tVersionId       INT,
    IN  ai_fct              INT,
    IN  ai_priority         INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_desc             VARCHAR (255),
    IN  ai_code             VARCHAR (63),
    IN  ai_class            VARCHAR (255)
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOT_OK        INT;            -- error

    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_code          VARCHAR (63);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_id            INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialization:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET l_code              = ai_code;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
-- body:
    -- drop leading and trailing spaces from the code:
    SET l_code = ltrim(rtrim(ai_code));
    -- check if the tab already exists:
    IF EXISTS   (
                    SELECT oid
                    FROM IBSDEV1.ibs_TabTemplate_01
                    WHERE oid = l_oid
                        AND id = ai_id
                ) THEN
        -- if the tab exists update the information
        -- get corresponding id from ibs_tab
        SELECT id
        INTO   l_id
        FROM   ibs_Tab
        WHERE  code = l_code;

        UPDATE IBSDEV1.ibs_TabTemplate_01
        SET kind = ai_kind,
            tVersionId = ai_tVersionId,
            fct = ai_fct,
            priority = ai_priority,
            name = ai_name,
            description = ai_desc,
            tabCode = l_code,
            class = ai_class
        WHERE oid = l_oid
            AND id = ai_id;

        -- update ibs_tab
        UPDATE  ibs_Tab
        SET     kind = ai_kind,
                tVersionId = ai_tVersionId,
                fct = ai_fct,
                priority = ai_priority,
                code = l_code,
                class = ai_class
        WHERE   id = l_id;

        -- update priority in ibs_ConsistsOf
        UPDATE  ibs_ConsistsOf
        SET     priority = ai_priority
        WHERE   tabId = l_id;

    ELSE
        -- create the tab:
        INSERT INTO IBSDEV1.ibs_TabTemplate_01
            (oid, id, kind, tVersionId, fct,
            priority, name, description, tabCode, class)
        VALUES  (l_oid, ai_id, ai_kind, ai_tVersionId, ai_fct,
            ai_priority, ai_name, ai_desc, l_code, ai_class);
    END IF;
    COMMIT;
    RETURN c_ALL_RIGHT;
END;
-- p_TabTemplate_01$addTab


--------------------------------------------------------------------------------
-- Get the oid of a specific tab of an object. <BR>
-- If the tab does not exist for this object or the tab itself is not an object
-- there is no oid available an OBJECTNOTFOUND ist returned.
--
-- @input parameters:
-- @param   ai_tabCode          The code of the tab (as it is in ibs_Tab).
-- @param   ai_oid_s            Id of the object for which to get the tab oid.
--
-- @output parameters:
-- @param   ao_tabOid           The oid of the tab object.
--
-- @return  A value representing the state of the procedure.
-- c_ALL_RIGHT              Action performed, values returned, everything ok.
-- c_OBJECTNOTFOUND         The tab object was not found.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DocumentTemplate_01$getTab');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DocumentTemplate_01$getTab(
    IN  ai_tabCode          VARCHAR (255),
    IN  ai_oid_s            VARCHAR (18),
    OUT ao_tabOid           CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_Object$getTabOid(l_oid, ai_tabCode, ao_tabOid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    RETURN l_retValue;
END;
-- p_DocumentTemplate_01$getTab
