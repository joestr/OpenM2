/******************************************************************************
 * procedure to create all standard querycreators
 *
 * version     $Id: createBaseQueryCreatorsProc.sql,v 1.10 2003/10/31 00:13:16 klaus Exp $
 *
 * author      Andreas Jansa  001104
 ******************************************************************************
 */


/******************************************************************************
 * create all standard queryCreators (=query templates) for one domain
 * first all queryobjects in container are deleted.
 * DO NOT USE IF IN THIS CONTAINER ARE OTHER QUERYOBJECTS THEN THE STANDARD-QUERYOBJECTS!!
 *
 * input parameters:
 * param   ai_userId       Id of the current user
 *          ai_containerId  Id the container where querycreators should be created.
 */
 

-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_createBaseQueryCreators
(
    -- common input parameters:
    ai_userId              INTEGER,
    ai_containerId_s       VARCHAR2
)
AS
    -- constants
    c_NOOID_s          CONSTANT VARCHAR2 (18) := '0x0000000000000000'; -- no oid as string
    c_languageId       CONSTANT INTEGER := 0;  -- the current language
    -- local variables
    l_retVal           INTEGER;
    l_oid_s            VARCHAR2 (18);          -- oidstring of current created QueryCreator
    l_oid              RAW (8);                -- oid of current created QueryCreator
    l_containerId      RAW (8);                -- convertion of ai_containerId_s
    l_tVersionId       INTEGER := 16875297;    -- tVersionId of QueryCreator
    l_name             VARCHAR2 (63);          -- the actual name
    l_desc             VARCHAR2 (255);         -- the actual description
    l_typeClass        VARCHAR2 (255);         -- classname of type (not used)
    l_tokSearchField   VARCHAR2 (255);         -- token for any additional searchfield
    l_tokSearchFieldII VARCHAR2 (255);         -- token for second additional searchfield
    l_tokName          VARCHAR2 (255);         -- token for name
    l_tokDesc          VARCHAR2 (255);         -- token for description
    l_tokOwner         VARCHAR2 (255);         -- token for owner
    l_tokRootObject    VARCHAR2 (255);         -- token for rootobjectselection
    l_selectString     VARCHAR2 (255);         -- string for selectstrings
    l_fromString       VARCHAR2 (255);
    l_whereString      VARCHAR2 (255);
    l_queryType        INTEGER;                -- integer for querytype 
    l_groupByString    VARCHAR2 (255);         -- string for group by clause
    l_orderByString    VARCHAR2 (255);         -- string for order by clause
    l_columnHeaders    VARCHAR2 (255);
    l_queryAttrForHeaders      VARCHAR2 (255);
    l_queryAttrTypesForHeaders VARCHAR2 (255);
    l_searchFieldTokens        VARCHAR2 (255);           -- string for l_searchFieldTokens
    l_queryAttrForFields       VARCHAR2 (255);
    l_queryAttrTypesForFields  VARCHAR2 (255);
        
BEGIN    

    -- convert inputparameters
    p_StringToByte (ai_containerId_s, l_containerId);

-- delete existing querycreators in this container
-- (for simpler administration)  

    DELETE ibs_Object 
    WHERE  containerId = l_containerId
      AND  oid IN (SELECT oid FROM ibs_QueryCreator_01);
      
    DELETE ibs_QueryCreator_01
    WHERE  oid NOT IN (SELECT oid FROM ibs_Object);

-- get multilingual tokens
    
    p_Token_01$get (c_languageId, 'TOK_NAME', l_tokName, l_typeClass);
    p_Token_01$get (c_languageId, 'TOK_DESCRIPTION', l_tokDesc, l_typeClass);
    p_Token_01$get (c_languageId, 'TOK_OWNER', l_tokOwner, l_typeClass);
    p_Token_01$get (c_languageId, 'TOK_ROOTOBJECTSELECTION', l_tokRootObject, l_typeClass);
    
    
-- concatinate strings for querycreators

    l_selectString := 'o.oid, o.state, o.name AS name, o.typeName AS typeName, o.isLink, o.linkedObjectId, o.owner, o.ownerName as ownerName, o.ownerOid, o.ownerFullname AS ownerFullname, o.lastChanged AS lastChanged, o.isNew, o.icon, o.description, o.flags, o.processState';
    l_fromString := 'v_Container$content o';
    l_whereString := 'userId = #SYSVAR.USERID# AND B_AND (rights, 2) = 2 AND o.tVersionID = ';
    l_queryType := 3;
    l_groupByString := ' ';
    l_orderByString := ' ';  -- standard emptystring in oracl
    l_columnHeaders := l_tokName || ',#OBJECTID,#TYPEIMAGE,#ISLINK,#ISNEW';
    l_queryAttrForHeaders := 'name,oid,icon,isLink,isNew';
    l_queryAttrTypesForHeaders := 'STRING,SYSVAR,SYSVAR,SYSVAR,SYSVAR';
    l_searchFieldTokens := l_tokName || ',' || l_tokDesc || ',' || l_tokOwner || ',' || l_tokRootObject;
    l_queryAttrForFields := 'o.name,o.description,o.ownerName,o.posnopath';
    l_queryAttrTypesForFields := 'STRING,STRING,STRING,OBJECTPATH';


-- contribution
    -- debug ('query creator for contribution');

    p_TypeName_01$get (c_languageId, 'TN_Article_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type contribution', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    -- get token for additional searchfield
    p_Token_01$get (c_languageId, 'TOK_CONTENT', l_tokSearchField, l_typeClass);


    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString || ', m2_Article_01 v',
            whereString = l_whereString || '16844033 AND o.oid = v.oid',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens || ',' || l_tokSearchField,
            queryAttrForFields = l_queryAttrForFields || ',v.content',
            queryAttrTypesForFields = l_queryAttrTypesForFields || ',LONGTEXT'
    WHERE   oid = l_oid;


-- trade market
    -- debug ('query creator for trade market');

    p_TypeName_01$get (c_languageId, 'TN_XMLDiscussion_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type trade market', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16843553 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;


-- discussion
    -- debug ('query creator for dicussion');

    p_TypeName_01$get (c_languageId, 'TN_Discussion_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type discussion', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16843521 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

-- document
    -- debug ('query creator for document');

    p_TypeName_01$get (c_languageId, 'TN_Document_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type document', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16843009 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

-- document container
    -- debug ('query creator for document container');

    p_TypeName_01$get (c_languageId, 'TN_DocumentContainer_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type document container', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16861953 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

-- company
    -- debug ('query creator for company');

    p_TypeName_01$get (c_languageId, 'TN_Company_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type company', l_oid_s);

    p_StringToByte (l_oid_s, l_oid);
  
    -- get token for additional searchfield
    p_Token_01$get (c_languageId, 'TOK_LEGALFORM', l_tokSearchField, l_typeClass);
       
    p_StringToByte (l_oid_s, l_oid);
    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString || ', mad_Company_01 v ',
            whereString = l_whereString || '16854017 AND o.oid = v.oid ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens || ',' || l_tokSearchField,
            queryAttrForFields = l_queryAttrForFields || ',v.legal_form',
            queryAttrTypesForFields = l_queryAttrTypesForFields || ',STRING'
    WHERE   oid = l_oid;


-- person
    -- debug ('query creator for person');

    p_TypeName_01$get (c_languageId, 'TN_Person_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type person', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    -- get token for additional searchfield
    p_Token_01$get (c_languageId, 'TOK_POSITION', l_tokSearchField, l_typeClass);
    p_Token_01$get (c_languageId, 'TOK_PERSONFIRM', l_tokSearchFieldII, l_typeClass);
    

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString || ', mad_Person_01 v ',
            whereString = l_whereString || '16853505 AND o.oid = v.oid ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens || ',' || l_tokSearchField || ',' || l_tokSearchFieldII,
            queryAttrForFields = l_queryAttrForFields || ',v.position,v.company',
            queryAttrTypesForFields = l_queryAttrTypesForFields || ',STRING,STRING'
    WHERE   oid = l_oid;

-- black board
    -- debug ('query creator for black board');

    p_TypeName_01$get (c_languageId, 'TN_BlackBoard_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type black board', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16845313 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

-- participant
    -- debug ('query creator for participant');

    p_TypeName_01$get (c_languageId, 'TN_Participant_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type participant', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16854529 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

-- term
    -- debug ('query creator for term');

    p_TypeName_01$get (c_languageId, 'TN_Termin_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type term', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    -- get token for additional searchfield
    p_Token_01$get (c_languageId, 'TOK_TERM_START_DATERANGE', l_tokSearchField  , l_typeClass);
    p_Token_01$get (c_languageId, 'TOK_TERM_END_DATERANGE',   l_tokSearchFieldII, l_typeClass);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString || ', m2_Termin_01 v ',
            whereString = l_whereString || '16843265 AND o.oid = v.oid ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens || ',' || l_tokSearchField || ',' || l_tokSearchFieldII,
            queryAttrForFields = l_queryAttrForFields || ',v.startDate,v.endDate',
            queryAttrTypesForFields = l_queryAttrTypesForFields || ',DATETIME,DATETIME'
    WHERE   oid = l_oid;


-- topic
    -- debug ('query creator for topic');

    p_TypeName_01$get (c_languageId, 'TN_Thread_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type topic', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16843777 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

-- product
    -- debug ('query creator for product');

    p_TypeName_01$get (c_languageId, 'TN_Product_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type product', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    -- get token for additional searchfield
    p_Token_01$get (c_languageId, 'TOK_PRODUCTNO', l_tokSearchField  , l_typeClass);
    p_Token_01$get (c_languageId, 'TOK_EAN',   l_tokSearchFieldII, l_typeClass);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString || ', m2_Product_01 v ',
            whereString = l_whereString || '16848129 AND o.oid = v.oid ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens || ',' || l_tokSearchField || ',' || l_tokSearchFieldII,
            queryAttrForFields = l_queryAttrForFields || ',v.productNo,v.ean',
            queryAttrTypesForFields = l_queryAttrTypesForFields || ',STRING,STRING'
    WHERE   oid = l_oid;

-- product group
    -- debug ('query creator for product group');

    p_TypeName_01$get (c_languageId, 'TN_ProductGroup_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type product group', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16850689 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

-- catalog
    -- debug ('query creator for catalog');

    p_TypeName_01$get (c_languageId, 'TN_Catalog_01', l_name, l_typeClass);
    l_retVal := p_QueryCreator_01$create (ai_userId, 0, l_tVersionId, l_name, ai_containerId_s,
        1, 0, c_NOOID_s, 'type catalog', l_oid_s);
       
    p_StringToByte (l_oid_s, l_oid);

    UPDATE  ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_whereString || '16845825 ',
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_searchFieldTokens,
            queryAttrForFields = l_queryAttrForFields,
            queryAttrTypesForFields = l_queryAttrTypesForFields
    WHERE   oid = l_oid;

    COMMIT WORK;
    

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_createBaseQueryCreators',
            ', ai_userId = ' || ai_userId  ||
            ', ai_containerId_s = ' || ai_containerId_s  ||
            ', errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM );

END p_createBaseQueryCreators;
/

show errors;


EXIT;