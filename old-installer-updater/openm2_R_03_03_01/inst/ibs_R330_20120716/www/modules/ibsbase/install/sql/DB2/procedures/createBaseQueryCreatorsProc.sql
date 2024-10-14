--------------------------------------------------------------------------------
-- procedure to create all standard querycreators
--
-- @version     $Id: createBaseQueryCreatorsProc.sql,v 1.7 2003/10/31 16:30:38 klaus Exp $
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
  
-- all types
    -- PRINT 'query creator for all types'
    CALL IBSDEV1.p_ObjectDesc_01$get(l_languageId, 'OD_queryAllTypes',
        l_name, l_desc);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId, l_name,
        ai_containerId_s, 1, 0, c_NOOID_s, 'all types', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_StringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = 'o.tVersionID = tv.id AND tv.typeId = t.id AND ' ||
            't.isSearchable = 1 AND userId = #USERID AND (rights & 2) = 2' ||
            ' AND o.tVersionId <> 16875297 AND o.tVersionId <> 16875329 ';

    UPDATE  IBSDEV1.ibs_QueryCreator_01
    SET     selectString = l_selectString,
            fromString = 'Container$content o, ibs_TVersion tv, ibs_type t',
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

-- container
    -- PRINT 'query creator for container'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Container', l_name,
        l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type container', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
    SET l_Tempforconcat =  l_whereString || '16842785 ';
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
-- attachment
    -- PRINT 'query creator for attachment'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Attachment_01',
        l_name, l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s, 'type attachment', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
    -- get token for additional searchfield
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_FILE', l_tokSearchField,
        l_typeClass);
    SET l_Tempforconcat = l_fromString || ', ibs_Attachment_01 v ';
    SET l_Tempforconcat1 =l_whereString || '16842833 AND o.oid = v.oid ';
    SET l_Tempforconcat2 =l_searchFieldTokens || ',' || l_tokSearchField;
    SET l_Tempforconcat3 =l_queryAttrForFields || ',v.filename';
    SET l_Tempforconcat4 =l_queryAttrTypesForFields || ',STRING';

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
  
-- user
    -- PRINT 'query creator for user'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_User_01', l_name,
        l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type user', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);

    SET l_Tempforconcat = l_whereString || '16842913 ';

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
  
-- file
    -- PRINT 'query creator for file'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_File_01', l_name,
        l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s, 'type file', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_FILE', l_tokSearchField,
        l_typeClass);

    SET l_Tempforconcat = l_fromString || ', ibs_Attachment_01 v ';
    SET l_Tempforconcat1 = l_whereString || '16869377 AND o.oid = v.oid ';
    SET l_Tempforconcat2 = l_searchFieldTokens || ',' || l_tokSearchField;
    SET l_Tempforconcat3 = l_queryAttrForFields || ',v.filename';
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
            queryAttrTypesForFields =l_Tempforconcat4 
    WHERE   oid = l_oid;
  
-- group
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Group_01', l_name,
        l_typeClass);
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
       l_name, ai_containerId_s, 1, 0, c_NOOID_s,
       'type group', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = l_whereString || '16842929 ';

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
  
-- url
    -- PRINT 'query creator for url'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Url_01', l_name,
        l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type url', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;

    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    SET l_Tempforconcat = l_whereString || '16869633 ';

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
  
-- note
    -- PRINT 'query creator for note'
    CALL IBSDEV1.p_TypeName_01$get(l_languageId, 'TN_Note_01', l_name,
        l_typeClass);
  
    CALL IBSDEV1.p_QueryCreator_01$create(ai_userId, 0, l_tVersionId,
        l_name, ai_containerId_s, 1, 0, c_NOOID_s,
        'type note', l_oid_s);
    GET DIAGNOSTICS l_retVal = RETURN_STATUS;
  
    CALL IBSDEV1.p_stringToByte (l_oid_s, l_oid);
  
    -- get token for additional searchfield
    CALL IBSDEV1.p_Token_01$get(l_languageId, 'TOK_CONTENT',
        l_tokSearchField, l_typeClass);

    SET l_Tempforconcat = l_fromString || ', ibs_Note_01 v ';
    SET l_Tempforconcat1 = l_whereString || '16870145 AND o.oid = v.oid ';
    SET l_Tempforconcat2 = l_searchFieldTokens || ',' || l_tokSearchField;
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
            queryAttrTypesForFields =l_Tempforconcat4 
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