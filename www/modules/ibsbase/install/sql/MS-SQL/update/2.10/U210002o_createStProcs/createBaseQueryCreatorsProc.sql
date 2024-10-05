/******************************************************************************
 * procedure to create all standard querycreators
 *
 * @version     $Id: createBaseQueryCreatorsProc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Andreas Jansa  001104
 ******************************************************************************
 */


/******************************************************************************
 * create all standard queryCreators (=query templates) for one domain
 * first all queryobjects in container are deleted.
 * DO NOT USE IF IN THIS CONTAINER ARE OTHER QUERYOBJECTS THEN THE STANDARD-QUERYOBJECTS!!
 *
 * @input parameters:
 * @param   @ai_userId       Id of the current user
 *          @ai_containerId  Id the container where querycreators should be created.
 */
 
-- delete existing procedure:
EXEC p_dropProc N'p_createBaseQueryCreators'
GO

-- create the new procedure:
CREATE PROCEDURE p_createBaseQueryCreators
(
    -- common input parameters:
    @ai_userId              USERID,
    @ai_containerId_s       OBJECTIDSTRING
)
AS

DECLARE 
    -- constants
    @c_NOOID_s                  OBJECTIDSTRING,         -- no oid as string
    -- local variables  
    @l_retVal                   INTEGER,
    @l_oid_s                    OBJECTIDSTRING,         -- oidstring of current created QueryCreator
    @l_oid                      OBJECTID,               -- oid of current created QueryCreator
    @l_containerId              OBJECTID,               -- convertion of @ai_containerId_s
    @l_tVersionId               TVERSIONID,             -- tVersionId of QueryCreator
    @c_languageId               INT,                    -- the current language
    @l_name                     NAME,                   -- the actual name
    @l_desc                     DESCRIPTION,            -- the actual description
    @l_typeClass                FILENAME,               -- classname of type (not used)
    @l_tokSearchField           DESCRIPTION,            -- token for any additional searchfield
    @l_tokSearchFieldII         DESCRIPTION,            -- token for second additional searchfield
    @l_tokName                  DESCRIPTION,            -- token for name
    @l_tokDesc                  DESCRIPTION,            -- token for description
    @l_tokOwner                 DESCRIPTION,            -- token for owner
    @l_tokRootObject            DESCRIPTION,            -- token for rootobjectselection
    @l_selectString             DESCRIPTION,                   -- string for selectstrings
    @l_fromString               DESCRIPTION,
    @l_whereString              DESCRIPTION,
    @l_queryType                INTEGER,                -- integer for querytype 
    @l_groupByString            DESCRIPTION,            -- string for group by clause
    @l_orderByString            DESCRIPTION,            -- string for order by clause
    @l_columnHeaders            DESCRIPTION,
    @l_queryAttrForHeaders      DESCRIPTION,
    @l_queryAttrTypesForHeaders DESCRIPTION,
    @l_searchFieldTokens        DESCRIPTION,           -- string for l_searchFieldTokens
    @l_queryAttrForFields       DESCRIPTION,
    @l_queryAttrTypesForFields  DESCRIPTION
        
    
SELECT
    -- constants
    @c_NOOID_s              = '0x0000000000000000',
    -- local variables
    @l_tVersionId = 0x01017F21,     -- QueryCreator
    @c_languageId = 0               -- default language

    -- convert inputparameters
    EXEC p_StringToByte @ai_containerId_s, @l_containerId OUTPUT

-- delete existing querycreators in this container
-- (for simpler administration)  

    DELETE ibs_Object 
    WHERE  containerId = @l_containerId
      AND  oid IN (SELECT oid FROM ibs_QueryCreator_01)
      
    DELETE ibs_QueryCreator_01
    WHERE  oid NOT IN (SELECT oid FROM ibs_Object)

-- get multilingual tokens
    
    EXEC p_Token_01$get @c_languageId, N'TOK_NAME', @l_tokName OUTPUT, @l_typeClass OUTPUT
    EXEC p_Token_01$get @c_languageId, N'TOK_DESCRIPTION', @l_tokDesc OUTPUT, @l_typeClass OUTPUT
    EXEC p_Token_01$get @c_languageId, N'TOK_OWNER', @l_tokOwner OUTPUT, @l_typeClass OUTPUT
    EXEC p_Token_01$get @c_languageId, N'TOK_ROOTOBJECTSELECTION', @l_tokRootObject OUTPUT, @l_typeClass OUTPUT
    
    
-- concatinate strings for querycreators

    SELECT @l_selectString = N'o.oid, o.state, o.name AS name, o.typeName AS typeName, o.isLink, o.linkedObjectId, o.owner, o.ownerName as ownerName, o.ownerOid, o.ownerFullname AS ownerFullname, o.lastChanged AS lastChanged, o.isNew, o.icon, o.description, o.flags, o.processState'
    SELECT @l_fromString = N'v_Container$content o'
    SELECT @l_whereString = N'userId = #SYSVAR.USERID# AND (rights & 2) = 2 AND o.tVersionID = '
    SELECT @l_queryType = 3
    SELECT @l_groupByString = N''
    SELECT @l_orderByString = N''
    SELECT @l_columnHeaders = @l_tokName + N',#OBJECTID,#TYPEIMAGE,#ISLINK,#ISNEW'
    SELECT @l_queryAttrForHeaders = N'name,oid,icon,isLink,isNew'
    SELECT @l_queryAttrTypesForHeaders = N'STRING,SYSVAR,SYSVAR,SYSVAR,SYSVAR'
    SELECT @l_searchFieldTokens = @l_tokName + N',' + @l_tokDesc + N',' + @l_tokOwner + N',' + @l_tokRootObject
    SELECT @l_queryAttrForFields = N'o.name,o.description,o.ownerName,o.posnopath'
    SELECT @l_queryAttrTypesForFields = N'STRING,STRING,STRING,OBJECTPATH'
    
-- all types
    -- PRINT 'query creator for all types'
    
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_queryAllTypes', @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'all types', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = N'v_Container$content o, ibs_TVersion tv, ibs_type t',
            whereString = N'o.tVersionID = tv.id AND tv.typeId = t.id AND t.isSearchable = 1 AND userId = #USERID AND (rights & 2) = 2 AND o.tVersionId <> 16875297 AND o.tVersionId <> 16875329 ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens,
            queryAttrForFields = @l_queryAttrForFields,
            queryAttrTypesForFields = @l_queryAttrTypesForFields
    WHERE   oid = @l_oid

-- container
    -- PRINT 'query creator for container'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Container', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type container', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16842785 ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens,
            queryAttrForFields = @l_queryAttrForFields,
            queryAttrTypesForFields = @l_queryAttrTypesForFields
    WHERE   oid = @l_oid

-- attachment
    -- PRINT 'query creator for attachment'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Attachment_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type attachment', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    -- get token for additional searchfield
    EXEC p_Token_01$get @c_languageId, N'TOK_FILE', @l_tokSearchField OUTPUT, @l_typeClass OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', ibs_Attachment_01 v ',
            whereString = @l_whereString + N'16842833 AND o.oid = v.oid ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens + N',' + @l_tokSearchField,
            queryAttrForFields = @l_queryAttrForFields + N',v.filename',
            queryAttrTypesForFields = @l_queryAttrTypesForFields + N',STRING'
    WHERE   oid = @l_oid

-- user
    -- PRINT 'query creator for user'

    EXEC p_TypeName_01$get @c_languageId, N'TN_User_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type user', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16842913 ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens,
            queryAttrForFields = @l_queryAttrForFields,
            queryAttrTypesForFields = @l_queryAttrTypesForFields
    WHERE   oid = @l_oid


-- file
    -- PRINT 'query creator for file'

    EXEC p_TypeName_01$get @c_languageId, N'TN_File_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type file', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    -- get token for additional searchfield
    EXEC p_Token_01$get @c_languageId, N'TOK_FILE', @l_tokSearchField OUTPUT, @l_typeClass OUTPUT


    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', ibs_Attachment_01 v ',
            whereString = @l_whereString + N'16869377 AND o.oid = v.oid ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens + N',' + @l_tokSearchField,
            queryAttrForFields = @l_queryAttrForFields + N',v.filename',
            queryAttrTypesForFields = @l_queryAttrTypesForFields + N',STRING'
    WHERE   oid = @l_oid


-- group
    -- PRINT 'query creator for group'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Group_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type group', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16842929 ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens,
            queryAttrForFields = @l_queryAttrForFields,
            queryAttrTypesForFields = @l_queryAttrTypesForFields
    WHERE   oid = @l_oid

-- url
    -- PRINT 'query creator for url'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Url_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type url', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16869633 ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens,
            queryAttrForFields = @l_queryAttrForFields,
            queryAttrTypesForFields = @l_queryAttrTypesForFields
    WHERE   oid = @l_oid

-- note
    -- PRINT 'query creator for note'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Note_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type note', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    -- get token for additional searchfield
    EXEC p_Token_01$get @c_languageId, N'TOK_CONTENT', @l_tokSearchField OUTPUT, @l_typeClass OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', ibs_Note_01 v ',
            whereString = @l_whereString + N'16870145 AND o.oid = v.oid ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens + N',' + @l_tokSearchField,
            queryAttrForFields = @l_queryAttrForFields + N',v.content',
            queryAttrTypesForFields = @l_queryAttrTypesForFields + N',LONGTEXT'
    WHERE   oid = @l_oid
GO
-- p_createBaseQueryCreators