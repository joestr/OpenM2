--------------------------------------------------------------------------------
-- procedure to create all standard querycreators
--
-- @version     $Id: createBaseQueryCreatorsProc.sql,v 1.7 2003/10/31 00:12:52 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020830
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- create all standard queryCreators (=query templates) for one domain
-- first all queryobjects in container are deleted.
-- DO NOT USE IF IN THIS CONTAINER ARE OTHER QUERYOBJECTS THEN THE STANDARD-QUERYOBJECTS!!
--
-- @input parameters:
-- @param   @ai_userId       Id of the current user
--          @ai_containerId  Id the container where querycreators should be created.
--------------------------------------------------------------------------------
 
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_createBaseQueryCreators');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_createBaseQueryCreators
(
    IN  ai_userId           INT,
    IN  ai_containerId_s    VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retVal        INT;
    DECLARE l_oid_s         VARCHAR (18); -- oidstring of current created QueryCreator
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of current created QueryCreator
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- conversion of @ai_containerId_s
    DECLARE l_tVersionId    INT DEFAULT 16875297; -- tVersionId of QueryCreator
    DECLARE l_languageId    INT DEFAULT 0; -- the current language
    DECLARE l_name          VARCHAR (63); -- the actual name
    DECLARE l_desc          VARCHAR (255); -- the actual description
    DECLARE l_typeClass     VARCHAR (255); -- classname of type (not used)
    DECLARE l_tokSearchField VARCHAR (255); -- token for any additional searchfield
    DECLARE l_tokSearchFieldII VARCHAR (255);-- token for second additional searchfield
    DECLARE l_tokName       VARCHAR (255); -- token for name
    DECLARE l_tokDesc       VARCHAR (255); -- token for description
    DECLARE l_tokOwner      VARCHAR (255); -- token for owner
    DECLARE l_tokRootObject VARCHAR (255); -- token for rootobjectselection
    DECLARE l_selectString  VARCHAR (255); -- string for selectstrings
    DECLARE l_fromString    VARCHAR (255);
    DECLARE l_whereString   VARCHAR (255);
    DECLARE l_queryType     INT;        -- integer for querytype 
    DECLARE l_groupByString VARCHAR (255); -- string for group by clause
    DECLARE l_orderByString VARCHAR (255); -- string for order by clause
    DECLARE l_columnHeaders VARCHAR (255);
    DECLARE l_queryAttrForHeaders VARCHAR (255);
    DECLARE l_queryAttrTypesForHeaders VARCHAR (255);
    DECLARE l_searchFieldTokens VARCHAR (255);-- string for l_searchFieldTokens
    DECLARE l_queryAttrForFields VARCHAR (255);
    DECLARE l_queryAttrTypesForFields VARCHAR (255);
    DECLARE l_Tempforconcat VARCHAR ( 255 );
    DECLARE l_Tempforconcat1 VARCHAR ( 255 );
    DECLARE l_Tempforconcat2 VARCHAR ( 255 );
    DECLARE l_Tempforconcat3 VARCHAR ( 255 );
    DECLARE l_Tempforconcat4 VARCHAR ( 255 );
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_oid_s             = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;
  
    SET l_sqlcode = 0;

    -- convert inputparameters
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);

    -- delete existing querycreators in this container
    DELETE  FROM IBSDEV1.ibs_Object
    WHERE   containerId = l_containerId
        AND oid IN
            (
                SELECT  oid 
                FROM    IBSDEV1.ibs_QueryCreator_01
            );

    DELETE  FROM IBSDEV1.ibs_QueryCreator_01
    WHERE   oid NOT IN
            (
                SELECT oid 
                FROM IBSDEV1.ibs_Object
            );

    -- get multilingual tokens
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_NAME', l_tokName,
        l_typeClass);
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_DESCRIPTION', l_tokDesc,
        l_typeClass);
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_OWNER', l_tokOwner,
        l_typeClass);
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_ROOTOBJECTSELECTION',
        l_tokRootObject, l_typeClass);
  
    -- concatenate strings for querycreators
    SET l_selectString =
        'o.oid, o.state, o.name AS name, o.typeName AS typeName, o.isLink, ' ||
        'o.linkedObjectId, o.owner, o.ownerName as ownerName, o.ownerOid, ' ||
        'o.ownerFullname AS ownerFullname, o.lastChanged AS lastChanged, ' ||
        'o.isNew, o.icon, o.description, o.flags, o.processState';
    SET l_fromString = 'Container$content o';
    SET l_whereString =
        'userId = #SYSVAR.USERID# AND (rights & 2) = 2 AND o.tVersionID = ';
    SET l_queryType = 3;
    SET l_groupByString = '';
    SET l_orderByString = '';
    SET l_columnHeaders = l_tokName || ',#OBJECTID,#TYPEIMAGE,#ISLINK,#ISNEW';
    SET l_queryAttrForHeaders = 'name,oid,icon,isLink,isNew';
    SET l_queryAttrTypesForHeaders = 'STRING,SYSVAR,SYSVAR,SYSVAR,SYSVAR';
    SET l_searchFieldTokens = l_tokName || ',' || l_tokDesc || ',' ||
        l_tokOwner || ',' || l_tokRootObject;
    SET l_queryAttrForFields = 'o.name,o.description,o.ownerName,o.posnopath';
    SET l_queryAttrTypesForFields = 'STRING,STRING,STRING,OBJECTPATH';


-- contribution
    -- PRINT 'query creator for contribution'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Article_01', l_name,
        l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type contribution', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
    -- get token for additional searchfield
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_CONTENT',
        l_tokSearchField, l_typeClass);
  
    SET l_Tempforconcat = l_fromString || ', m2_Article_01 v';
    SET l_Tempforconcat1 = l_whereString || '16844033 AND o.oid = v.oid';
    SET l_Tempforconcat2 = l_searchFieldTokens || ',' ||     l_tokSearchField;
    SET l_Tempforconcat3 = l_queryAttrForFields || ',v.content';
    SET l_Tempforconcat4 = l_queryAttrTypesForFields || ',LONGTEXT';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_Tempforconcat,
            whereString = l_Tempforconcat1,
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_Tempforconcat2,
            queryAttrForFields = l_Tempforconcat3,
            queryAttrTypesForFields = l_Tempforconcat4
    WHERE   oid = l_oid;


-- trade market
    -- PRINT 'query creator for trade market'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_XMLDiscussion_01',
        l_name, l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type trade market', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat =l_whereString || '16843553 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- PRINT 'query creator for dicussion'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Discussion_01', l_name,
        l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type discussion', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);

    SET l_Tempforconcat = l_whereString || '16843521 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- PRINT 'query creator for document'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Document_01', l_name,
        l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type document', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);

    SET l_Tempforconcat = l_whereString || '16843009 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- PRINT 'query creator for document container'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_DocumentContainer_01',
        l_name, l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type document container', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);

    SET l_Tempforconcat = l_whereString || '16861953 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Company_01',
        l_name, l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type company', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
    -- get token for additional searchfield
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_LEGALFORM',
        l_tokSearchField, l_typeClass);
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);

    SET l_Tempforconcat = l_fromString || ', mad_Company_01 v ';
    SET l_Tempforconcat1= l_whereString || '16854017 AND o.oid = v.oid ';
    SET l_Tempforconcat2 = l_searchFieldTokens || ',' || l_tokSearchField;
    SET l_Tempforconcat3 = l_queryAttrForFields || ',v.legal_form';
    SET l_Tempforconcat4 = l_queryAttrTypesForFields || ',STRING';
    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_Tempforconcat,
            whereString = l_Tempforconcat1,
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_Tempforconcat2,
            queryAttrForFields = l_Tempforconcat3,
            queryAttrTypesForFields = l_Tempforconcat4
    WHERE   oid = l_oid;


-- person
    -- PRINT 'query creator for person'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Person_01', l_name,
        l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type person', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    -- get token for additional searchfield
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_POSITION',
        l_tokSearchField, l_typeClass);
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_PERSONFIRM',
        l_tokSearchFieldII, l_typeClass);
  
    SET l_Tempforconcat = l_fromString || ', mad_Person_01 v ';
    SET l_Tempforconcat1 = l_whereString || '16853505 AND o.oid = v.oid ';
    SET l_Tempforconcat2 = l_searchFieldTokens || ',' || l_tokSearchField ||
        ',' || l_tokSearchFieldII;
    SET l_Tempforconcat3 = l_queryAttrForFields || ',v.position,v.company';
    SET l_Tempforconcat4 = l_queryAttrTypesForFields || ',STRING,STRING';
    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_Tempforconcat,
            whereString = l_Tempforconcat1,
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_Tempforconcat2,
            queryAttrForFields = l_Tempforconcat3,
            queryAttrTypesForFields =l_Tempforconcat4 
    WHERE   oid = l_oid;
    COMMIT;


-- black board
    -- PRINT 'query creator for black board'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_BlackBoard_01', l_name,
        l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type black board', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = l_whereString || '16845313 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- PRINT 'query creator for participant'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Participant_01', l_name, l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type participant', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = l_whereString || '16854529 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- PRINT 'query creator for term'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Termin_01', l_name,
        l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type term', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    -- get token for additional searchfield
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_TERM_START_DATERANGE',
        l_tokSearchField, l_typeClass);
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_TERM_END_DATERANGE',
        l_tokSearchFieldII, l_typeClass);

    SET l_Tempforconcat = l_fromString || ', m2_Termin_01 v ';
    SET l_Tempforconcat1 = l_whereString || '16843265 AND o.oid = v.oid ';
    SET l_Tempforconcat2 = l_searchFieldTokens || ',' || l_tokSearchField ||
        ',' || l_tokSearchFieldII;
    SET l_Tempforconcat3 = l_queryAttrForFields || ',v.startDate,v.endDate';
    SET l_Tempforconcat4 = l_queryAttrTypesForFields || ',DATETIME,DATETIME';
    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_Tempforconcat,
            whereString = l_Tempforconcat1,
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_Tempforconcat2,
            queryAttrForFields = l_Tempforconcat3,
            queryAttrTypesForFields =l_Tempforconcat4 
    WHERE   oid = l_oid;
    COMMIT;


-- topic
    -- PRINT 'query creator for topic'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Thread_01', l_name,
        l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type topic', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = l_whereString || '16843777 ';
    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- PRINT 'query creator for product'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Product_01',
        l_name, l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type product', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;

    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    -- get token for additional searchfield
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_PRODUCTNO',
        l_tokSearchField, l_typeClass);
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_EAN', l_tokSearchFieldII,
        l_typeClass);
  
    SET l_Tempforconcat = l_fromString || ', m2_Product_01 v ';
    SET l_Tempforconcat1 = l_whereString || '16848129 AND o.oid = v.oid ';
    SET l_Tempforconcat2 = l_searchFieldTokens || ',' || l_tokSearchField ||
        ',' || l_tokSearchFieldII;
    SET l_Tempforconcat3 = l_queryAttrForFields || ',v.productNo,v.ean';
    SET l_Tempforconcat4 = l_queryAttrTypesForFields || ',STRING,STRING';
    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_Tempforconcat,
            whereString = l_Tempforconcat1,
            queryType = l_queryType,
            groupByString = l_groupByString,
            orderByString = l_orderByString,
            columnHeaders = l_columnHeaders,
            queryAttrForHeaders = l_queryAttrForHeaders,
            queryAttrTypesForHeaders = l_queryAttrTypesForHeaders,
            searchFieldTokens = l_Tempforconcat2,
            queryAttrForFields = l_Tempforconcat3,
            queryAttrTypesForFields = l_Tempforconcat4
    WHERE   oid = l_oid;


-- product group
    -- PRINT 'query creator for product group'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_ProductGroup_01',
        l_name, l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type product group', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = l_whereString || '16850689 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- PRINT 'query creator for catalog'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Catalog_01',
        l_name, l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type catalog', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;

    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = l_whereString || '16845825 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = l_fromString,
            whereString = l_Tempforconcat,
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
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = '';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- finish the procedure:
    RETURN;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_createBaseQueryCreators',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:

END;
-- p_createBaseQueryCreators