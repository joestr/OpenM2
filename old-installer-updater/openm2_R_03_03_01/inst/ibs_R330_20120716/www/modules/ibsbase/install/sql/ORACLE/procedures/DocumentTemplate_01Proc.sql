/******************************************************************************
 * All stored procedures regarding the ibs_DocumentTemplate_01 table. <BR>
 *
 * @version     2.2.1.0014, 19.03.2002 KR
 *
 * @author      Michael Steiner (MS)
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block.
 *
 * @input parameters:
 * @param   userId             ID of the user who is creating the object.
 * @param   op                 Operation to be performed (used for rights
 *                              check).
 * @param   tVersionId         Type of the new object.
 * @param   name               Name of the object.
 * @param   containerId_s      ID of the container where object shall be
 *                             created in.
 * @param   containerKind      Kind of object/container relationship
 * @param   isLink             Defines if the object is a link
 * @param   linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$create
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_oid                   RAW (8) := c_NOOID;


-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    l_retValue := p_Attachment_01$create (ai_userId, ai_op, ai_tVersionId,
            ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
            ai_linkedObjectId_s, ai_description, ao_oid_s);

    IF (l_retValue = c_ALL_RIGHT)    -- no error occurred?
    THEN
        p_stringToByte (ao_oid_s, l_oid);

 	    INSERT INTO ibs_DocumentTemplate_01
 	            (oid, objectType, typeName, className, iconName, mayExistIn,
 	            mayContain, isContainerType, objectSuperType,
                isSearchable, isInheritable, showInMenu, showInNews,
 	            typeId, tVersionId, systemDisplayMode, dbMapped,
 	            tableName, procCopy, mappingInfo, workflowTemplateOid,
 	            attachmentCopy, logDirectory, showDOMTree)
	    VALUES  (l_oid, '', '', '', '', '',
	            '', 0, '',
	            0, 0, 0, 0,
	            0, 0, 0, 0,
	            '', '', ' ', c_NOOID,
	            '', '', 0);
    END IF; -- no error occurred?

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        ibs_error.log_error (ibs_error.error,
            'p_DocumentTemplate_01$create',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_DocumentTemplate_01$create;
/

show errors;
-- p_DocumentTemplate_01$create


/******************************************************************************
 * Registers a tab for a document template.<BR>
 *
 * @input parameters:
 * @param   ai_oid              The oid of the document template.
 * @param   ai_typeCode         The type code of the document template.
 * @param   ai_tabPos           The position number (id) of the tab.
 *
 * @return  A value representing the state of the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$regTab
(
    ai_oid                  RAW,
    ai_typeCode             VARCHAR2,
    ai_tabPos               INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             INTEGER := 1;   -- everything was o.k.
    c_NOT_OK                INTEGER := 0;   -- error

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_id                    INTEGER := 0;   -- the position id of the tab
    l_kind                  INTEGER := 0;   -- the kind of the tab
    l_tVersionId            INTEGER := 0;   -- the tVersionId of the tab object
    l_fct                   INTEGER := 0;   -- the function of the tab
    l_priority              INTEGER := 0;   -- the priority of the tab
    l_name                  VARCHAR2 (63) := ''; -- the name of the tab
    l_desc                  VARCHAR2 (255) := '';-- the description of the tab
    l_tabCode               VARCHAR2 (63) := ''; -- the tab code in the ibs_tab
    l_class                 VARCHAR2 (255) := ''; -- the class to show tab view
    l_multilangKey          VARCHAR2 (63) := ''; -- the multilang key in the
                                            -- table ibs_tab
    l_domainId              INTEGER := 0;   -- id of the current domain
    l_tabId                 INTEGER := 0;
    l_msg                   VARCHAR2 (255) := '';

-- body:
BEGIN
    -- ATTENTION!!
    -- !! NOT YET SUPPORTED BY THE BASE !!!
    -- The domain id must be set to the domain id of the document template
    -- object.

    -- SELECT @l_domainId = u.domainId
    -- FROM ibs_Object o, ibs_User u
    -- WHERE o.oid = @ai_oid AND u.id = o.owner
    l_domainId := 0;

    BEGIN
        -- select the tab with the given id and document template oid:
        SELECT  id, kind, tVersionId, fct, priority, name, description,
                tabCode, class
        INTO    l_id, l_kind, l_tVersionId, l_fct, l_priority, l_name, l_desc,
                l_tabCode, l_class
        FROM    ibs_TabTemplate_01
        WHERE   oid = ai_oid
            AND id = ai_tabPos;

        -- at this point we know that the tab template was found.

        -- remove all leading/trailing spaces from the tab name:
        l_name := LTRIM (RTRIM (l_name));
        -- if no tab name is given set the type name from the ibs_Type table:
        IF (l_name IS NULL OR l_name = '') -- no tab name defined?
        THEN
            BEGIN
                SELECT  t.name
                INTO    l_name
                FROM    ibs_Type t, ibs_TVersion tv
                WHERE   t.id = tv.typeId
                    AND tv.id = l_tVersionId;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'get type name for version ' || l_tVersionId;
                    RAISE;              -- call common exception handler
            END;
        END IF; -- if no tab name defined

        -- if no tabCode is given set tabcode to oid concatenated with typeCode:
        IF (l_tabCode IS NULL OR l_tabCode = '') -- no code defined?
        THEN
            -- the unique tab code is composed by the oid of the template,
            -- the type code (max. 40 chars) of the form and the id of the tab.
            p_byteToString (ai_oid, l_tabCode);
            l_tabCode := l_tabCode ||
                         '_' || SUBSTR (ai_typeCode, 1, 40) ||
                         '_' || l_id;
        END IF; -- if no code defined

        -- use the tab code as multilang key:
        l_multilangKey := l_tabCode;

        -- check if the tab already exists:
        BEGIN
            SELECT  id
            INTO    l_tabId
            FROM    ibs_Tab
            WHERE   code = l_tabCode
                AND domainId = l_domainId;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- tab does not exist
                -- register the new tab:
                l_retValue := p_Tab$new
                    (l_domainId, l_tabCode, l_kind, l_tVersionId,
                    l_fct, l_priority, l_multilangKey, 0, l_class,
                    l_tabId);
            -- when tab does not exist
        END;

        BEGIN
            SELECT name
            INTO l_multilangKey
            FROM ibs_ObjectDesc_01
            WHERE languageId = 0 AND name = l_multilangKey;

            -- try to update the tab name and description
            UPDATE ibs_ObjectDesc_01
            SET     id = 0,
                    languageId = 0,
                    name = l_multilangKey,
                    objName = l_name,
                    objDesc = l_desc,
                    className = 'ibs.bo.ObjectDesc'
            WHERE   languageId = 0
                AND name = l_multilangKey;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- the objectdesc entry was not found
                BEGIN
                    -- set the tab name and description for the new tab type:
                    INSERT INTO ibs_ObjectDesc_01
                            (id, languageId, name, objName, objDesc,
                            className)
                    VALUES  (0, 0, l_multilangKey, l_name, l_desc,
                            'ibs.bo.ObjectDesc');
                EXCEPTION
                    WHEN OTHERS THEN    -- any error
                        -- create error entry:
                        l_ePos := 'insert; l_multilangKey = ' || l_multilangKey;
                        RAISE;          -- call common exception handler
                END;
            -- when the objectdesc entry was not found
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'update; l_multilangKey = ' || l_multilangKey;
                RAISE;                  -- call common exception handler
        END;

        -- add the tab to the consistsof table:
        p_Type$addTabs (ai_typeCode, '',
            l_tabCode, '', '', '', '', '', '', '', '', '');
        -- set the return code:
        l_retValue := c_ALL_RIGHT;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get tab data for tVersion ' || l_tVersionId;
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tabPos' || ai_tabPos ||
            ', ai_typeCode' || ai_typeCode ||
            ', l_id' || l_id ||
            ', l_name' || l_name ||
            ', l_kind' || l_kind ||
            ', l_desc' || l_desc ||
            ', l_tVersionId' || l_tVersionId ||
            ', l_tabCode' || l_tabCode ||
            ', l_fct' || l_fct ||
            ', l_class' || l_class ||
            ', l_priority' || l_priority ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_DocumentTemplate_01$regTab', l_eText);
        -- return error value:
        RETURN c_NOT_OK;
END p_DocumentTemplate_01$regTab;
/

show errors;
-- p_DocumentTemplate_01$regTab


/******************************************************************************
 * Inserts/Updates the type information for the document type
 * in the ibs_Type table.<BR>
 *
 * @input parameters:
 * @param   ai_oid              The oid of the template.
 * @param   ai_typeId           The type id (0 for a new type).
 * @param   ai_typeCode         The type code.
 * @param   ai_typeName         The type name.
 * @param   ai_className        The java class name.
 * @param   ai_iconName         The icon name.
 * @param   ai_mayExistIn       List of container type codes where objects of
 *                              this type are allowed to exist in.
 * @param   ai_isContainer      true if the type is a container.
 * @param   ai_mayContain       List of object type codes whitch this container
 *                              is allowed to contain.
 *
 * @output parameters:
 * @param   ao_typeId           The resulting type id.
 * @param   ao_tVersionId       The resulting tVersion id.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$newType
(
    -- input parameters:
    ai_oid                  RAW,
    ai_typeId               INTEGER,
    ai_typeCode             VARCHAR2,
    ai_typeName             VARCHAR2,
    ai_className            VARCHAR2,
    ai_iconName             VARCHAR2,
    ai_mayExistIn           VARCHAR2,
    ai_isContainerType      INTEGER,
    ai_mayContain           VARCHAR2,
    ai_superTypeCode        VARCHAR2,
    ai_isSearchable         INTEGER,
    ai_isInheritable        INTEGER,
    ai_isShowInMenu         INTEGER,
    ai_isShowInNews         INTEGER,

    -- output parameters:
    ao_typeId               OUT INTEGER,
    ao_tVersionId           OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_TVReferenceContainer  CONSTANT INTEGER := 16842817; -- 0x01010041
    c_TVAttachmentContainer CONSTANT INTEGER := 16842849; -- 0x01010061
    c_TVRightsContainer     CONSTANT INTEGER := 16842977; -- 0x010100E1
    c_TVMembershipContainer CONSTANT INTEGER := 16863745; -- 0x01015201
    c_TVProtocolContainer   CONSTANT INTEGER := 16865537; -- 0x01015901
    c_languageId            CONSTANT INTEGER := 0; -- the current language

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_code                  VARCHAR2 (63) := '';
    l_idx                   INTEGER := 0;
    l_len                   INTEGER := 0;
    l_tabId                 INTEGER := 0;
    l_rowCount              INTEGER := 0;
    l_mayExistIn            VARCHAR2 (255);
    l_mayContain            VARCHAR2 (255);
    l_superTypeCode         VARCHAR2 (255);
    l_id                    INTEGER := 0;

    -- define cursor:
    -- get all tabs from the ibs_TabTemplate_01 table
    CURSOR  tabCursor IS
        SELECT  id
        FROM    ibs_TabTemplate_01
        WHERE   oid = ai_oid
        ORDER BY id;
    l_cursorRow             tabCursor%ROWTYPE;

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- if the typeId is 0 register the new type
    IF (ai_typeId = 0)                  -- register new type?
    THEN
        -- check if a type with this code name already exists:
        -- if the type not exists register the type
        BEGIN
            SELECT  id
            INTO    l_id
            FROM    ibs_Type
            WHERE   code = ai_typeCode;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- type does not exist
                -- drop surrounding spaces:
                l_superTypeCode := LTRIM (RTRIM (ai_superTypeCode));

                -- determinate the type id of the super type:
                -- if no super type code is set use the default type
                IF (l_superTypeCode IS NULL OR LENGTH (l_superTypeCode) <= 0)
                THEN
                    -- set the default type code of the super type:
                    IF (ai_isContainerType > 0)
                    THEN
                        l_superTypeCode := 'Container'; -- Container
                    ELSE
                        l_superTypeCode := 'XMLViewer'; -- XMLViewer_01
                    END IF;
                END IF; -- if

                -- register the new type:
                p_Type$newLang (ai_typeId, l_superTypeCode,
                        ai_isContainerType, ai_isInheritable,
                        ai_isSearchable, ai_isShowInMenu, ai_isShowInNews,
                        ai_typeCode, ai_className, c_languageId, ai_typeName);
            -- when type does not exist
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'check if type exists';
                RAISE;                  -- call common exception handler
        END;
    END IF; -- if register new type

    -- get the typeId and tVersionId of the type:
    BEGIN
        SELECT  id, actVersion
        INTO    ao_typeId, ao_tVersionId
        FROM    ibs_Type
        WHERE   code = ai_typeCode;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- type not created
            -- create error entry:
            l_ePos := 'type not created ' || ai_typeCode;
            RAISE;                      -- call common exception handler
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'error when getting type id and tVersionId for ' ||
                      ai_typeCode;
            RAISE;                      -- call common exception handler
    END;

    -- add the default tabs:
    -- if the new type is a container add the content tab, too
    IF (ai_isContainerType > 0)
    THEN
        p_Type$addTabs (ai_typeCode, '',
            'Content', 'Info', 'References', 'Rights', '', '', '', '', '', '');
    ELSE
        p_Type$addTabs (ai_typeCode, '',
            'Info', 'References', 'Rights', '', '', '', '', '', '', '');
    END IF;


    -- add the additional tabs:
    -- loop through the cursor rows:
    FOR l_cursorRow IN tabCursor        -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_tabId := l_cursorRow.id;

        -- register the tab:
        l_retValue := p_DocumentTemplate_01$regTab (ai_oid, ai_typeCode, l_tabId);
    END LOOP; -- while another tuple found


    -- set the correct type and icon name in ibs_Type:
    BEGIN
        UPDATE ibs_Type
        SET    name = ai_typeName,
               icon = ai_iconName
        WHERE  code = ai_typeCode;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'setting the type name and icon';
            RAISE;                      -- call common exception handler
    END;


    -- delete existing may contain entrys for the type
    -- p_MayContain$deleteType (ao_typeId);

    -- set the may contain entrys for the new type
    -- the parameter '@ai_mayExistIn' holds a comma separated list
    -- of the container type codes.

    -- if the container code list is empty set the default container codes
    -- like the XMLViewer_01.

    -- remove all leading and trailing spaces:
    l_mayExistIn := LTRIM (RTRIM (ai_mayExistIn));

    -- if not defined set the default type codes for form objects:
    IF (l_mayExistIn IS NULL OR LENGTH (l_mayExistIn) = 0)
    THEN
        l_mayExistIn := 'Container,ExportContainer,MasterDataContainer';
    END IF;

    WHILE (LENGTH (l_mayExistIn) > 0)
    LOOP
        -- separate the container type codes:
        l_code := '';
        l_len := LENGTH (l_mayExistIn);
        l_idx := INSTR (l_mayExistIn, ',', 1, 1);

        -- check if the comma (',') was found:
        IF (l_idx > 0)                  -- found the comma?
        THEN
            -- get the next code:
            l_code := RTRIM (SUBSTR (l_mayExistIn, 1, l_idx - 1));

            -- get the rest of the string:
            l_mayExistIn :=
                LTRIM (SUBSTR (l_mayExistIn, l_idx + 1, l_len - l_idx));
        -- if found the comma
        ELSE                            -- did not find comma
            -- get the rest of the string as code:
            l_code := RTRIM (l_mayExistIn);
            -- no more entries:
            l_mayExistIn := '';
        END IF; -- else did not find comma

        -- check if the code could be found:
        IF (LENGTH (l_code) > 0)                -- code not empty?
        THEN
            -- add the maycontain entry:
            l_retValue := p_MayContain$new (l_code, ai_typeCode);
        END IF; -- if code not empty
    END LOOP; -- while

    -- if the new type is a container set the correct maycontain entries:
    IF (ai_isContainerType > 0)         -- the type is a container?
    THEN
        -- the parameter 'ai_mayContain' holds a comma separated list
        -- of the type codes.
        -- ensure that there are no leading spaces:
        l_mayContain := LTRIM (ai_mayContain);

        WHILE (LENGTH (l_mayContain) > 0)
        LOOP
            -- separate the object type codes:
            l_code := '';
            l_len := LENGTH (l_mayContain);
            l_idx := INSTR (l_mayContain, ',', 1, 1);

            -- check if the comma (',') was found:
            IF (l_idx > 0)              -- found the comma?
            THEN
                -- get the next code:
                l_code := RTRIM (SUBSTR (l_mayContain, 1, l_idx - 1));
                -- get the rest of the string:
                l_mayContain :=
                    LTRIM (SUBSTR (l_mayContain, l_idx + 1, l_len - l_idx));
            -- if found the comma
            ELSE                        -- did not find comma
                -- get the rest of the string as code:
                l_code := RTRIM (l_mayContain);
                -- no more entries:
                l_mayContain := '';
            END IF; -- else did not find comma

            -- check if the code could be found:
            IF (l_code <> '')            -- code not empty?
            THEN
                -- add the maycontain entry:
                l_retValue := p_MayContain$new (l_code, ai_typeCode);
            END IF; -- if code not empty
        END LOOP; -- WHILE
    END IF; -- if the type is a container

    -- set the result value:
    l_retValue := c_ALL_RIGHT;

    COMMIT WORK;     -- make changes permanent

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_typeId = ' || ai_typeId ||
            ', ai_typeCode = ' || ai_typeCode ||
            ', ai_isContainerType = ' || ai_isContainerType ||
            ', ai_isSearchable = ' || ai_isSearchable ||
            ', ai_className = ' || ai_className ||
            ', ai_isInheritable = ' || ai_isInheritable ||
            ', ai_iconName = ' || ai_iconName ||
            ', ai_isShowInMenu = ' || ai_isShowInMenu ||
            ', ai_mayExistIn = ' || ai_mayExistIn ||
            ', ai_isShowInNews = ' || ai_isShowInNews ||
            ', ai_mayContain = ' || ai_mayContain ||
            ', l_tabId = ' || l_tabId ||
            ', l_code = ' || l_code ||
            ', ao_typeId = ' || ao_typeId ||
            ', ao_tVersionId = ' || ao_tVersionId ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_DocumentTemplate_01$newType', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_DocumentTemplate_01$newType;
/

show errors;
-- p_DocumentTemplate_01$newType;



/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         Display object in the news.
 *
 * ...
 *
 * @param   @objectType         the object type code
 * @param   @typeName           the object type name
 * @param   @className          the java class name
 * @param   @iconName           the icon name
 * @param   @mayExistIn         list of container type codes
 * @param   @isContainerType    defines the template a container type
 * @param   @mayContain         list of type codes that can appare in this container
 * @param   @superTypeCode      the type code of the super type
 * @param   @isSearchable       is the type searchable
 * @param   @isInheritable      is the type inheritable
 * @param   @isShowInMenu       the 'show in menu' flag for the type
 * @param   @isShowInNews       the 'show in news' flag for the type
 * @param   @systemDisplayMode  display mode for the system section of the object
 * @param   @dbMapped           objects of this type are db-mapped
 * @param   @tableName          the mapping table
 * @param   @procCopy           the name of the copy procedure
 * @param   @wfTemplateOid_s    the workflow template oid
 * @param   @attachmentCopy     the name of the copy procedure for attachments (XMLDATA)
 * @param   @showDOMTree        flag to show the dom tree at create object
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$change
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           NUMBER,

    ai_isMaster             NUMBER,
    ai_attachmentType       INTEGER,
    ai_filename	            VARCHAR2,
    ai_path	                VARCHAR2,
    ai_url 	                VARCHAR2,
    ai_filesize	            FLOAT,
    ai_isWeblink            NUMBER,

    ai_objectType           VARCHAR2,
    ai_typeName             VARCHAR2,
    ai_className            VARCHAR2,
    ai_iconName             VARCHAR2,
    ai_mayExistIn           VARCHAR2,
    ai_isContainerType      INTEGER,
    ai_mayContain           VARCHAR2,
    ai_superTypeCode        VARCHAR2,
    ai_isSearchable         INTEGER,
    ai_isInheritable        INTEGER,
    ai_isShowInMenu         INTEGER,
    ai_isShowInNews         INTEGER,
    ai_systemDisplayMode    INTEGER,
    ai_dbMapped             NUMBER,
    ai_tableName            VARCHAR2,
    ai_procCopy             VARCHAR2,
    ai_wfTemplateOid_s      VARCHAR2,
    ai_attachmentCopy       VARCHAR2,
    ai_showDOMTree          INTEGER

    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_wfTemplateOid         RAW (8) := c_NOOID;
    l_typeID                INTEGER := 0;
    l_tVersionID            INTEGER := 0;

-- body:
BEGIN
    -- perform the change of the object:
    l_retValue := p_Attachment_01$change (ai_oid_s, ai_userId, ai_op,
            ai_name, ai_validUntil, ai_description, ai_showInNews,
            ai_isMaster, ai_attachmentType, ai_filename, ai_path,
            ai_url, ai_filesize, ai_isWeblink);

    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        -- convert the parameters
        p_stringToByte (ai_oid_s, l_oid);
        p_stringToByte (ai_wfTemplateOid_s, l_wfTemplateOid);

        -- for a new type the typeID is 0 and we have
        -- to register the new type to retrieve a typeId.
        -- if the type is not new (typeId != 0) the type
        -- informations are updated
        SELECT typeId, tVersionId
        INTO l_typeId, l_tVersionId
        FROM ibs_DocumentTemplate_01
        WHERE oid = l_oid;

        IF (l_typeId IS NULL)
        THEN
            l_typeId := 0;
        END IF;

        -- register the new type with the given type code and class name
        l_retValue := p_DocumentTemplate_01$newType (
                                l_oid, l_typeID, ai_objectType,
                                ai_typeName, ai_className,
                                ai_iconName, ai_mayExistIn,
                                ai_isContainerType, ai_mayContain,
                                ai_superTypeCode,
                                ai_isSearchable,
                                ai_isInheritable,
                                ai_isShowInMenu,
                                ai_isShowInNews,
                                l_typeId, l_tVersionId);

        IF (l_retValue = c_ALL_RIGHT)   -- operation properly performed?
        THEN
            -- update further information:
            UPDATE  ibs_DocumentTemplate_01
            SET     objectType = ai_objectType,
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
            WHERE   oid = l_oid;
        END IF; -- if retValue is ok
    END IF; -- if retValue is ok

    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        ibs_error.log_error (ibs_error.error,
            'p_DocumentTemplate_01$change',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DocumentTemplate_01$change;
/

show errors;
-- p_DocumentTemplate_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Display the object in the news.
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out.
 * @param   ao_checkOutUser     Id of the user which checked out the object.
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user.
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut user.
 *
 * ...                          attachment parameters
 *
 * @param   ao_objectType       The object type name.
 * @param   ao_typeName         The object type name.
 * @param   ao_className        The java class name.
 * @param   ao_iconName         The icon name.
 * @param   ao_mayExistIn       The info for may contain.
 * @param   ao_isContainerType  true if the type is a container.
 * @param   ao_mayContain       The info for may contain.
 * @param   ao_superTypeCode    The type code of the super type.
 * @param   ao_isSearchable     Is the type searchable.
 * @param   ao_isInheritable    Is the type inheritable.
 * @param   ao_isShowInMenu     The 'show in menu' flag for the type.
 * @param   ao_isShowInNews     The 'show in news' flag for the type.
 * @param   ao_typeId           The type id.
 * @param   ao_tVersionId       The tVersion id.
 * @param   ao_systemDisplayMode Display mode for the system section of the
 *                              object.
 * @param   ao_dbMapped         Object of this type are db-mapped.
 * @param   ao_tableName        The mapping table name.
 * @param   ao_procCopy         The copy procedure for the mapping.
 * @param   ao_wfTemplateOid    The oid of the workflow template.
 * @param   ao_wfTemplateName   The name of the workflow template.
 * @param   ao_attachmentCopy   The copy procedure for attachments (XMLDATA).
 * @param   ao_logDirectory     The name of the directory where the log of
 *                              translation should be stored.
 * @param   ao_showDOMTree      Flag to show the DOM tree to the user.
 *
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_INSUFFICIENT_RIGHTS    User has no right to perform action.
 * c_OBJECTNOTFOUND         The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- output parameters
    ao_state                OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_typeName             OUT VARCHAR2,
    ao_name                 OUT VARCHAR2,
    ao_containerId          OUT RAW,
    ao_containerName        OUT VARCHAR2,
    ao_containerKind        OUT INTEGER,
    ao_isLink               OUT NUMBER,
    ao_linkedObjectId       OUT RAW,
    ao_owner                OUT INTEGER,
    ao_ownerName            OUT VARCHAR2,
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2,
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2,
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT NUMBER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,

    ao_isMaster             OUT NUMBER,
    ao_attachmentType       OUT INTEGER,
    ao_filename             OUT VARCHAR2,
    ao_path                 OUT VARCHAR2,
    ao_url                  OUT VARCHAR2,
    ao_filesize             OUT FLOAT,
    ao_isWeblink            OUT NUMBER,

    ao_objectType           OUT VARCHAR2,
    ao_objectTypeName       OUT VARCHAR2,
    ao_className            OUT VARCHAR2,
    ao_iconName             OUT VARCHAR2,
    ao_mayExistIn           OUT VARCHAR2,
    ao_isContainerType      OUT INTEGER,
    ao_mayContain           OUT VARCHAR2,
    ao_superTypeCode        OUT VARCHAR2,
    ao_isSearchable         OUT INTEGER,
    ao_isInheritable        OUT INTEGER,
    ao_isShowInMenu         OUT INTEGER,
    ao_isShowInNews         OUT INTEGER,
    ao_objectTypeId         OUT INTEGER,
    ao_objectTVersionId     OUT INTEGER,
    ao_systemDisplayMode    OUT INTEGER,
    ao_dbMapped             OUT NUMBER,
    ao_tableName            OUT VARCHAR2,
    ao_procCopy             OUT VARCHAR2,
    ao_wfTemplateOid        OUT RAW,
    ao_wfTemplateName       OUT VARCHAR2,
    ao_attachmentCopy       OUT VARCHAR2,
    ao_logDirectory         OUT VARCHAR2,
    ao_showDOMTree          OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;

-- body:
BEGIN
    -- retrieve the base object data:
    l_retValue := p_Attachment_01$retrieve (ai_oid_s, ai_userId, ai_op, ao_state,
            ao_tVersionId, ao_typeName, ao_name, ao_containerId,
            ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
            ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
            ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
            ao_isMaster, ao_attachmentType, ao_filename, ao_path, ao_url,
            ao_filesize, ao_isWeblink);

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        p_stringToByte (ai_oid_s, l_oid);

        SELECT  objectType,
                typeName,
                className,
                iconName,
                mayExistIn,
                isContainerType,
                mayContain,
                objectSuperType,
                isSearchable,
                isInheritable,
                showInMenu,
                showInNews,
                typeId,
                tVersionId,
                systemDisplayMode,
                dbMapped,
                tableName,
                procCopy,
                workflowTemplateOid,
                attachmentCopy,
                logDirectory,
                showDOMTree
        INTO    ao_objectType,
                ao_objectTypeName,
                ao_className,
                ao_iconName,
                ao_mayExistIn,
                ao_isContainerType,
                ao_mayContain,
                ao_superTypeCode,
                ao_isSearchable,
                ao_isInheritable,
                ao_isShowInMenu,
                ao_isShowInNews,
                ao_objectTypeId,
                ao_objectTVersionId,
                ao_systemDisplayMode,
                ao_dbMapped,
                ao_tableName,
                ao_procCopy,
                ao_wfTemplateOid,
                ao_attachmentCopy,
                ao_logDirectory,
                ao_showDOMTree
	    FROM    ibs_DocumentTemplate_01
	    WHERE   oid = l_oid;

        -- if a workflow is defined get the name of the workflow object
	    IF (ao_wfTemplateOid != c_NOOID)
	    THEN
	        SELECT name
	        INTO ao_wfTemplateName
	        FROM ibs_Object
	        WHERE oid = ao_wfTemplateOid;
	    END IF;
    END IF;

    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,
            'p_DocumentTemplate_01$retrieve',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DocumentTemplate_01$retrieve;
/

show errors;
-- p_DocumentTemplate_01$retrieve


/******************************************************************************
 * Deletes a DocumentTemplate_01 object. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- delete existing procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$delete
(
    -- input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER
)
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_error 	            INTEGER;
    StoO_errmsg	            VARCHAR2(255);
    -- define constants
    c_NOOID                 RAW(8)  := hexToRaw ('0000000000000000');
    c_NOT_OK                INTEGER := 0;
    c_ALL_RIGHT             INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   INTEGER := 2;
    c_ALREADY_EXISTS        INTEGER := 21;
    c_OBJECTNOTFOUND        INTEGER := 3;
    c_RIGHT_DELETE          INTEGER := 16;
    c_OBJECTISREFERENCED    INTEGER := 61;
    -- locals
    l_oid                   RAW(8)  := c_NOOID;
    l_rights                INTEGER := 0;
    l_containerId           RAW(8)  := c_NOOID;
    l_rowCount              INTEGER := 0;
    l_typeId                INTEGER := 0;
    l_tVersionId            INTEGER := 0;
    l_domainId              INTEGER := 0;
    l_typeCode              VARCHAR(63) := '';
    l_tabCode               VARCHAR(63) := '';
    -- define return values
    l_retValue              INTEGER := c_ALL_RIGHT;
BEGIN
    -- convertions: (OBJECTIDSTRING) - all input object ids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    -- transaction start
    BEGIN
        l_retValue := c_OBJECTISREFERENCED;

        -- check if template object is referenced by other objects
        SELECT COUNT (*)
        INTO l_rowCount
        FROM v_DocumentTemplate_01$ref
        WHERE oid = l_oid;

        -- no references found ?
        IF (l_rowCount = 0)
        THEN
            -- all references and the object itself are deleted (plus rights)
            l_retValue := p_Attachment_01$delete (ai_oid_s, ai_userId, ai_op);

            -- drop all type informations for this template

            IF (l_retValue = c_ALL_RIGHT)
            THEN
                BEGIN
                    -- first get the tVersionId of the type defined
                    -- by the document template
                    SELECT tVersionId, typeId, objectType
                    INTO l_tVersionId, l_typeId, l_typeCode
                    FROM ibs_DocumentTemplate_01
                    WHERE oid = l_oid;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        ibs_error.log_error (ibs_error.error,
                            'p_DocumentTemplate_01$delete',
                            'sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
                    WHEN OTHERS THEN
                        RAISE;
                END;

                -- if the template defines a valid m2 type
                IF (l_tVersionId != 0 AND l_typeId != 0)
                THEN
                    -- ATTENTION!!
                    -- !! NOT YET SUPPORTED BY THE BASE !!!
                    -- The domain id must be set to the domain id of the document template object.

                    l_domainId := 0;

                    -- SELECT  u.domainId
                    -- INTO    l_domainId
                    -- FROM    ibs_Object o, ibs_User u
                    -- WHERE   o.oid = l_oid
                    --     AND u.id = o.owner;

                    -- delete all rows in the ibs_Attachment_01 table
                    -- who are part of a xml viewer object with this template oid
                    BEGIN
                        DELETE  ibs_Attachment_01
                        WHERE   oid IN
                                (
                                    SELECT  oid
                                    FROM    ibs_XMLViewer_01
                                    WHERE   templateOid = l_oid
                                );
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            NULL;
                        WHEN OTHERS THEN
                            RAISE;
                    END;

                    -- delete all rows in the ibs_KeyMapper table
                    -- referenced by a xml viewer object with this template oid
                    BEGIN
                        DELETE  ibs_KeyMapper
                        WHERE   oid IN
                                (
                                    SELECT  oid
                                    FROM    ibs_XMLViewer_01
                                    WHERE   templateOid = l_oid
                                );
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            NULL;
                        WHEN OTHERS THEN
                            RAISE;
                    END;

                    -- delete all form objects (and tabs) with this tVersionId
                    BEGIN
                        DELETE  ibs_Object
                        WHERE   oid IN
                                (
                                    SELECT  o1.oid
                                    FROM    ibs_Object o1, ibs_Object o2
                                    WHERE   o1.posNoPath LIKE o2.posNoPath || '%'
                                        AND o2.tVersionId = l_tVersionId
                                );
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            NULL;
                        WHEN OTHERS THEN
                            RAISE;
                    END;

                    -- delete all rows in the XMLViewer_01 table
                    -- with this template oid
                    DELETE  ibs_XMLViewer_01
                    WHERE   templateOid = l_oid;

                    -- delete the m2 type
                    l_retValue := p_Type$deletePhysical (l_typeId, l_typeCode);

                    -- delete the tabs and object descriptions
                    -- ATTENTION!! this should be done by a specific delete procedure.
                    p_byteToString (l_oid, l_tabCode);
                    DELETE  ibs_Tab
                    WHERE   domainId = l_domainId
                        AND code LIKE l_tabCode || '_%';

                    -- delete consist of entries:
                    DELETE  ibs_ConsistsOf
                    WHERE   tVersionId = l_tVersionId;

                    -- delete the tab description too
                    -- ATTENTION!! this should be done by a specific delete procedure.
                    DELETE  ibs_ObjectDesc_01
                    WHERE   name LIKE l_tabCode || '_%';

                    -- delete all references from and to objects of this type:
                    p_Reference$deleteTVersion (l_tVersionId);
                END IF; -- IF (l_tVersionId != 0)

                -- delete the document template object and its tabs from ibs_object
                BEGIN
                    DELETE  ibs_Object
                    WHERE   oid IN
                            (
                                SELECT  o1.oid
                                FROM    ibs_Object o1, ibs_Object o2
                                WHERE   o1.posNoPath LIKE o2.posNoPath || '%'
                                    AND o2.oid = l_oid
                            );
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        NULL;
                    WHEN OTHERS THEN
                        RAISE;
                END;

                -- delete the document template object itself
                DELETE  ibs_DocumentTemplate_01
                WHERE   oid = l_oid;

                -- delete the row in the ibs_Attachment_01 table
                DELETE  ibs_Attachment_01
                WHERE   oid = l_oid;

                -- delete the reference in the key mapper table
                DELETE  ibs_KeyMapper
                WHERE   oid = l_oid;

                -- delete the tab templates
                DELETE  ibs_TabTemplate_01
                WHERE   oid = l_oid;
            END IF; -- IF (l_retValue = c_ALL_RIGHT)
        END IF; -- if no references found
    END;

    -- if all ok commit work otherwise rollback
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        COMMIT WORK;
    ELSE
        ROLLBACK;
    END IF;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        ibs_error.log_error (ibs_error.error,
            'p_DocumentTemplate_01$delete',
            'sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        RETURN c_NOT_OK;
END p_DocumentTemplate_01$delete;
/

show errors;
-- p_DocumentTemplate_01$delete


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @oid              ID of the object to be copied.
 * @param   @userId           ID of the user who copy the object.
 * @param   @newOid           The new Oid of the new created BusinessObject.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$BOCopy
(
    -- input parameters:
    ai_oid                  RAW,            -- the oid of Object we want to copy
    ai_userId               INTEGER,        -- the userId of the User who wants
                                            -- to copy
    ai_newOid               RAW             -- the new OID of the copied Object
)RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;

    -- body:
BEGIN
    l_retValue := p_Attachment_01$BOCopy (ai_oid, ai_userId, ai_newOid);

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            -- make a insert for all your typespecific tables:
            INSERT  INTO ibs_DocumentTemplate_01
                    (oid, objectType, systemDisplayMode, dbMapped, tableName,
                     procCopy, mappingInfo, workflowTemplateOid)
            SELECT  ai_newOid, objectType, systemDisplayMode, dbMapped, tableName,
                     procCopy, mappingInfo, workflowTemplateOid
            FROM    ibs_DocumentTemplate_01
            WHERE   oid = ai_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_retValue := c_All_RIGHT;
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error,
                                     'p_DocumentTemplate_01$BOCopy',
                                     'Error in INSERT INTO');
            RAISE;
        END;
    END IF;
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,
            'p_DocumentTemplate_01$BOCopy',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DocumentTemplate_01$BOCopy;
/

show errors;
-- p_DocumentTemplate_01$BOCopy

/******************************************************************************
 * Reads the CLOB field 'mappingInfo'.<BR>
 *
 * @input parameters:
 * @param   @ai_oid             ID of the object
 *
 * @output parameters:
 * @param   @ao_mappingInfo     the mapping info
 *
 * @returns A value representing the state of the procedure.
 */
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$getMInfo
(
    ai_oid          VARCHAR2,
    ao_mappingInfo  OUT CLOB
)
RETURN INTEGER
AS
    l_oid       RAW(8);
BEGIN
    p_stringToByte (ai_oid, l_oid);

    SELECT mappingInfo
    INTO  ao_mappingInfo
    FROM  ibs_DocumentTemplate_01
    WHERE oid = l_oid;

    RETURN 1;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_DocumentTemplate_01$getMInfo',
                        'Input: ' || ai_oid ||
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    p_DocumentTemplate_01$getMInfo.ao_mappingInfo := EMPTY_CLOB();
    RETURN 0;
END p_DocumentTemplate_01$getMInfo;
/

show errors;
-- p_DocumentTemplate_01$getMInfo;


/******************************************************************************
 * Insert/Update the informations for a document template tab in the table
 * ibs_TabTemplate_01.<BR>
 *
 * @input parameters:
 * @param   ai_oid_s            Oid of the associated document template.
 * @param   ai_id               The number of the tab.
 * @param   ai_kind             The tab kind.
 * @param   ai_code             The type code of the object.
 * @param   ai_fct              The function of the tab.
 * @param   ai_name             The name of the tab.
 * @param   ai_desc             The description of the tab.
 * @param   ai_code             The code of the tab.
 * @param   ai_class            The class to show the tab view.
 *
 * @return  A value representing the state of the procedure.
 */

-- create the new function:
CREATE OR REPLACE FUNCTION p_TabTemplate_01$addTab
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_id                   INTEGER,
    ai_kind                 INTEGER,
    ai_tVersionId           INTEGER,
    ai_fct                  INTEGER,
    ai_priority             INTEGER,
    ai_name                 VARCHAR2,
    ai_desc                 VARCHAR2,
    ai_code                 VARCHAR2,
    ai_class                VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             INTEGER := 1;   -- everything was o.k.
    c_NOT_OK                INTEGER := 0;   -- error

    -- local variables:
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   RAW (8);
    l_code                  VARCHAR2 (63) := ai_code;
    l_id                    INTEGER;

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- drop leading and trailing spaces from the code:
    l_code := LTRIM (RTRIM (ai_code));

    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- select the tab
        SELECT  oid
        INTO    l_oid
        FROM    ibs_TabTemplate_01
        WHERE   oid = l_oid
            AND id = ai_id;

        -- if the tab exists update the tab:

        -- get corresponding id from ibs_tab
        SELECT id
        INTO   l_id
        FROM   ibs_Tab
        WHERE  code = l_code;

        -- update the tabTemplate
        UPDATE  ibs_TabTemplate_01
        SET     kind = ai_kind,
                tVersionId = ai_tVersionId,
                fct = ai_fct,
                priority = ai_priority,
                name = ai_name,
                description = ai_desc,
                tabCode = l_code,
                class = ai_class
        WHERE   oid = l_oid
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

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- the tab does not exist
            BEGIN
                -- create the tab:
                INSERT INTO ibs_TabTemplate_01
                        (oid, id, kind, tVersionId, fct,
                        priority, name, description, tabCode, class)
                VALUES  (l_oid, ai_id, ai_kind, ai_tVersionId, ai_fct,
                        ai_priority, ai_name, ai_desc, l_code, ai_class);
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'create the new template';
                    RAISE;              -- call common exception handler
            END;
        -- when the tab does not exist
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'update the tab template';
            RAISE;                      -- call common exception handler
    END;

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN c_ALL_RIGHT;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_id = ' || ai_id ||
            ', ai_kind = ' || ai_kind ||
            ', ai_tVersionId = ' || ai_tVersionId ||
            ', ai_fct = ' || ai_fct ||
            ', ai_name = ' || ai_name ||
            ', ai_desc = ' || ai_desc ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_TabTemplate_01$addTab', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_TabTemplate_01$addTab;
/

show errors;
-- p_TabTemplate_01$addTab


/******************************************************************************
 * Get the oid of a specific tab of an object. <BR>
 * If the tab does not exist for this object or the tab itself is not an object
 * there is no oid available an OBJECTNOTFOUND ist returned.
 *
 * @input parameters:
 * @param   ai_tabCode          The code of the tab (as it is in ibs_Tab).
 * @param   ai_oid_s            Id of the object for which to get the tab oid.
 *
 * @output parameters:
 * @param   ao_tabOid           The oid of the tab object.
 *
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_OBJECTNOTFOUND         The tab object was not found.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DocumentTemplate_01$getTab
(
    -- input parameters:
    ai_tabCode              VARCHAR2,
    ai_oid_s                VARCHAR2,
    -- output parameters:
    ao_tabOid               OUT RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- error

    -- local variables:
    l_retValue              INTEGER;
    l_oid                   RAW (8);

-- body:
BEGIN
    p_stringToByte (ai_oid_s, l_oid);

    l_retValue := p_Object$getTabOid (l_oid, ai_tabCode, ao_tabOid);

    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error(ibs_error.error,
                'p_DocumentTemplate_01$getTab',
                'errorcode = ' || SQLCODE || ', errmsg = ' || SQLERRM );
        RETURN c_NOT_OK;
END p_DocumentTemplate_01$getTab;
/

show errors;
-- p_DocumentTemplate_01$getTab


EXIT;
