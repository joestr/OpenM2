/******************************************************************************
 * procedure to create all standard querycreators
 *
 * @version     $Id: U300001p_createBaseQueryCreatorsProc.sql,v 1.1 2010/04/15 17:56:12 rburgermann Exp $
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

-- set tokens in default language (english)
    
    SELECT @l_tokName = 'name'
    SELECT @l_tokDesc = 'description'
    SELECT @l_tokOwner = 'owner'
    SELECT @l_tokRootObject = 'search in'
    
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
    

-- contribution
    -- PRINT 'query creator for contribution'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Article_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type contribution', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    -- set token in default language (english)
    SELECT @l_tokSearchField = 'content'


    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', m2_Article_01 v',
            whereString = @l_whereString + N'16844033 AND o.oid = v.oid',
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


-- trade market
    -- PRINT 'query creator for trade market'

    EXEC p_TypeName_01$get @c_languageId, N'TN_XMLDiscussion_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type trade market', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16843553 ',
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

    -- set token in default language (english)
    SELECT @l_tokSearchField = 'file'

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

-- discussion
    -- PRINT 'query creator for dicussion'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Discussion_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type discussion', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16843521 ',
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

-- document
    -- PRINT 'query creator for document'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Document_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type document', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16843009 ',
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

-- document container
    -- PRINT 'query creator for document container'

    EXEC p_TypeName_01$get @c_languageId, N'TN_DocumentContainer_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type document container', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16861953 ',
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

-- company
    -- PRINT 'query creator for company'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Company_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type company', @l_oid_s OUTPUT

    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT
  
    -- set token in default language (english)
    SELECT @l_tokSearchField = 'legal form'
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT
    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', mad_Company_01 v ',
            whereString = @l_whereString + N'16854017 AND o.oid = v.oid ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens + N',' + @l_tokSearchField,
            queryAttrForFields = @l_queryAttrForFields + N',v.legal_form',
            queryAttrTypesForFields = @l_queryAttrTypesForFields + N',STRING'
    WHERE   oid = @l_oid


-- person
    -- PRINT 'query creator for person'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Person_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type person', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    -- set tokens in default language (english)
    SELECT @l_tokSearchField = 'position'
    SELECT @l_tokSearchFieldII = 'company'
    

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', mad_Person_01 v ',
            whereString = @l_whereString + N'16853505 AND o.oid = v.oid ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens + N',' + @l_tokSearchField + N',' + @l_tokSearchFieldII,
            queryAttrForFields = @l_queryAttrForFields + N',v.position,v.company',
            queryAttrTypesForFields = @l_queryAttrTypesForFields + N',STRING,STRING'
    WHERE   oid = @l_oid

-- black board
    -- PRINT 'query creator for black board'

    EXEC p_TypeName_01$get @c_languageId, N'TN_BlackBoard_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type black board', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16845313 ',
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

-- participant
    -- PRINT 'query creator for participant'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Participant_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type participant', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16854529 ',
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

-- term
    -- PRINT 'query creator for term'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Termin_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type term', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    -- set tokens in default language (english)
    SELECT @l_tokSearchField = 'starts'
    SELECT @l_tokSearchFieldII = 'ends'

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', m2_Termin_01 v ',
            whereString = @l_whereString + N'16843265 AND o.oid = v.oid ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens + N',' + @l_tokSearchField + N',' + @l_tokSearchFieldII,
            queryAttrForFields = @l_queryAttrForFields + N',v.startDate,v.endDate',
            queryAttrTypesForFields = @l_queryAttrTypesForFields + N',DATETIME,DATETIME'
    WHERE   oid = @l_oid


-- topic
    -- PRINT 'query creator for topic'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Thread_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type topic', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16843777 ',
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

-- product
    -- PRINT 'query creator for product'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Product_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type product', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    -- set tokens to default language (english)
    SELECT @l_tokSearchField = 'product number'
    SELECT @l_tokSearchFieldII = 'EAN'

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString + N', m2_Product_01 v ',
            whereString = @l_whereString + N'16848129 AND o.oid = v.oid ',
            queryType = @l_queryType,
            groupByString = @l_groupByString,
            orderByString = @l_orderByString,
            columnHeaders = @l_columnHeaders,
            queryAttrForHeaders = @l_queryAttrForHeaders,
            queryAttrTypesForHeaders = @l_queryAttrTypesForHeaders,
            searchFieldTokens = @l_searchFieldTokens + N',' + @l_tokSearchField + N',' + @l_tokSearchFieldII,
            queryAttrForFields = @l_queryAttrForFields + N',v.productNo,v.ean',
            queryAttrTypesForFields = @l_queryAttrTypesForFields + N',STRING,STRING'
    WHERE   oid = @l_oid

-- product group
    -- PRINT 'query creator for product group'

    EXEC p_TypeName_01$get @c_languageId, N'TN_ProductGroup_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type product group', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16850689 ',
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

-- catalog
    -- PRINT 'query creator for catalog'

    EXEC p_TypeName_01$get @c_languageId, N'TN_Catalog_01', @l_name OUTPUT, @l_typeClass OUTPUT
    EXEC @l_retVal = p_QueryCreator_01$create @ai_userId, 0, @l_tVersionId, @l_name, @ai_containerId_s,
        1, 0, @c_NOOID_s, N'type catalog', @l_oid_s OUTPUT
       
    EXEC p_StringToByte @l_oid_s, @l_oid OUTPUT

    UPDATE  ibs_QueryCreator_01
    SET     selectString = @l_selectString,
            fromString = @l_fromString,
            whereString = @l_whereString + N'16845825 ',
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
GO
-- p_createBaseQueryCreators