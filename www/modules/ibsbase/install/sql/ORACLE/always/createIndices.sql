/******************************************************************************
 * create all indices of the hole base-system. <BR>
 *
 * @version     2.2.1.0015, 20.03.2002 KR
 *
 * @author      Andreas Jansa (AJ) 000814
 ******************************************************************************
 */

--
-- get and drop all indixes where dropping is possible
-- the following indixes can not be dropped:
-- * index created by 'primary key' column constraint
-- * unique index created by 'unique' column constraint
-- * index on a lob structure
--
-- only the framework indexes will be selected and dropped
-- prefixes: IBS_
-- 
DECLARE
    -- local variables:
    l_file                  VARCHAR2 (15) := 'createIndices'; -- name of actual file
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_indexName             VARCHAR2 (50);
    l_cmdString             VARCHAR2 (2000); -- command line to be executed
    l_cursorId              INTEGER;        -- id of cmd cursor
    l_rowsProcessed         INTEGER;        -- number of rows of last cmd exec.
    l_lastErrorPos          INTEGER;        -- last error in cmd line execution

    -- define cursor:
    -- get all indexes
    CURSOR  cursorAllIndexes IS
        SELECT  index_name
        FROM    user_indexes
        WHERE   index_name NOT LIKE 'PK__%' -- primary key constraint
            AND index_name NOT LIKE 'UQ__%' -- unique constraint
            AND index_name NOT LIKE 'SYS_%' -- unknown???
            AND index_type NOT LIKE 'LOB'   -- without indexes created by LOB columns
            -- get only indexes on m2 tables :
            AND (   table_name LIKE 'IBS_%');
    l_cursorRow             cursorAllIndexes%ROWTYPE;

-- body:
BEGIN
    COMMIT WORK; -- finish previous and begin new TRANSACTION

    -- loop through the cursor rows:
    FOR l_cursorRow IN cursorAllIndexes -- another tuple found
    LOOP
        -- get the actual tuple values:
        l_indexName := l_cursorRow.index_name;

        -- create the command string for deleting the index:
        l_cmdString := 'DROP INDEX ' || l_indexName;
debug (l_file || ': ' || l_cmdString);

        -- try to delete the index:
        BEGIN
            -- open the cursor:
            l_cursorId := DBMS_SQL.OPEN_CURSOR;
            -- parse the statement and use the normal behavior of the
            -- database to which we are currently connected:
        	DBMS_SQL.PARSE (l_cursorId, l_cmdString, DBMS_SQL.NATIVE);
        	-- remember the possible error position:
            l_lastErrorPos := DBMS_SQL.LAST_ERROR_POSITION;
        	l_rowsProcessed := DBMS_SQL.EXECUTE (l_cursorId);
            -- close the cursor:
        	DBMS_SQL.CLOSE_CURSOR (l_cursorId);
        EXCEPTION
            WHEN OTHERS THEN 
                IF (DBMS_SQL.IS_OPEN (l_cursorId))
                                        -- the cursor is currently open?
                THEN
                    -- close the cursor:
                    DBMS_SQL.CLOSE_CURSOR (l_cursorId);
                END IF; -- the cursor is currently open
                -- create error entry:
                l_ePos :=
                    'Error when dropping index at ' || l_lastErrorPos;
                RAISE;                  -- call common exception handler
        END;

    END LOOP; -- while another tuple found

    -- make changes permanent and set new transaction starting point:
    COMMIT WORK;

    -- print state report:
    debug (l_file || ': ' || 'Old indexes deleted.');

EXCEPTION 
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_file || ': ' || l_ePos ||
            '; l_indexName = ' || l_indexName ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        debug (l_eText);
        ibs_error.log_error (ibs_error.error, l_file, l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
END;
/
COMMIT WORK;


--
-- CREATE ALL INDEXES
--
-- the following error will occur for some statements:
--      ORA-01408: such column list already indexed
--
-- reason:
--      already existing indices created by 'UNIQUE' column constraints
--
-- what should you do about the errors?
--      NOTHING!
--

BEGIN
    debug ('createIndices: Creating new indexes...');
END;
/

-- 
-- MODULE: IBS
--

-- IBS_ATTACHMENT_01
CREATE INDEX INDEXATTACHMENTTYPE ON IBS_ATTACHMENT_01 (ATTACHMENTTYPE) /*TABLESPACE*/;

-- IBS_CHECKOUT_01
CREATE UNIQUE INDEX INDEXCHECKOUTOID ON IBS_CHECKOUT_01 (OID) /*TABLESPACE*/;
CREATE INDEX INDEXCHECKOUTUSERID ON ibs_Checkout_01 (userId);

-- IBS_CONNECTOR_01
CREATE UNIQUE INDEX INDEXCONNECTOROID ON IBS_CONNECTOR_01 (OID) /*TABLESPACE*/;

-- ibs_ConsistsOf
CREATE UNIQUE INDEX IndexConsistsOfId ON ibs_ConsistsOf (id) /*TABLESPACE*/;
CREATE INDEX IndexConsistsOfTVersionIdTabId ON ibs_ConsistsOf (tVersionId, tabId) /*TABLESPACE*/;

-- IBS_DOMAIN_01
CREATE UNIQUE INDEX INDEXDOMAINID ON IBS_DOMAIN_01 (ID) /*TABLESPACE*/;
CREATE UNIQUE INDEX INDEXDOMAINOID ON IBS_DOMAIN_01 (OID) /*TABLESPACE*/;
CREATE INDEX INDEXDOMAIN_01ADMINID ON IBS_DOMAIN_01 (ADMINID) /*TABLESPACE*/;

-- IBS_DOMAINSCHEME_01
CREATE UNIQUE INDEX INDEXDOMAINSCHEMEID ON IBS_DOMAINSCHEME_01 ( ID ) /*TABLESPACE*/;
CREATE UNIQUE INDEX INDEXDOMAINSCHEMEOID ON IBS_DOMAINSCHEME_01 ( OID ) /*TABLESPACE*/;

-- IBS_EXCEPTION_01
CREATE INDEX INDEXEXCEPTIONNAME ON IBS_EXCEPTION_01 ( name ) /*TABLESPACE*/;
CREATE INDEX INDEXEXCEPTIONID ON IBS_EXCEPTION_01 ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXEXCEPTIONCLASSNAME ON IBS_EXCEPTION_01 ( CLASSNAME ) /*TABLESPACE*/;

-- IBS_GROUP
CREATE UNIQUE INDEX INDEXGROUPID ON IBS_GROUP ( id ) /*TABLESPACE*/;
CREATE UNIQUE INDEX INDEXGROUPOID ON IBS_GROUP ( oid ) /*TABLESPACE*/;
CREATE INDEX INDEXGROUPSTATE ON IBS_GROUP ( state ) /*TABLESPACE*/;
CREATE INDEX INDEXGROUPDOMAINID ON IBS_GROUP ( domainId ) /*TABLESPACE*/;
CREATE INDEX INDEXGROUPNAME ON IBS_GROUP ( name ) /*TABLESPACE*/;

-- IBS_GROUPUSER
CREATE INDEX INDEXGROUPUSERID ON IBS_GROUPUSER ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXGROUPUSERORIGGROUPID ON IBS_GROUPUSER ( origGroupId ) /*TABLESPACE*/;
CREATE INDEX INDEXGROUPUSERGROUPID ON IBS_GROUPUSER ( groupId ) /*TABLESPACE*/;
CREATE INDEX INDEXGROUPUSERUSERID ON IBS_GROUPUSER ( userId ) /*TABLESPACE*/;
CREATE INDEX INDEXGROUPUSERUSERIDPATH ON IBS_GROUPUSER ( idpath ) /*TABLESPACE*/;

--IBS_HELP_01
CREATE UNIQUE INDEX INDEXHELPOID ON IBS_HELP_01 ( oid ) /*TABLESPACE*/;
CREATE INDEX INDEXHELPSEARCHCONTENT ON IBS_HELP_01 ( searchContent ) /*TABLESPACE*/;

-- IBS_KEYMAPPER
CREATE INDEX INDEXKEYMAPPEROID ON IBS_KEYMAPPER ( oid ) /*TABLESPACE*/; 
CREATE INDEX INDEXKEYMAPPERIDDOMAIN ON IBS_KEYMAPPER ( id, idDomain ) /*TABLESPACE*/; 

-- IBS_LAYOUT_01
CREATE INDEX INDEXLAYOUTNAME ON IBS_LAYOUT_01 ( name ) /*TABLESPACE*/;

-- IBS_MESSAGE_01
CREATE INDEX INDEXMESSAGENAME ON IBS_MESSAGE_01 ( name ) /*TABLESPACE*/;
CREATE INDEX INDEXMESSAGELANGUAGEID ON IBS_MESSAGE_01 ( LANGUAGEID ) /*TABLESPACE*/;
CREATE INDEX INDEXMESSAGECLASSNAME ON IBS_MESSAGE_01 ( CLASSNAME ) /*TABLESPACE*/;

-- IBS_MENUTAB_01
CREATE UNIQUE INDEX IndexMenuTab_oid ON ibs_MenuTab_01 (oid) /*TABLESPACE*/;
CREATE INDEX IndexMenuTab_priorityKey ON ibs_MenuTab_01 (priorityKey) /*TABLESPACE*/;
CREATE INDEX IndexMenuTab_domainId ON ibs_MenuTab_01 (domainId) /*TABLESPACE*/;

-- IBS_OBJECT
CREATE UNIQUE INDEX INDEXOBJECTID ON IBS_OBJECT ( id ) /*TABLESPACE*/;
CREATE UNIQUE INDEX INDEXOBJECTOID ON IBS_OBJECT ( oid ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTCONTAINERID ON IBS_OBJECT ( containerId ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTCONTAINERKIND ON IBS_OBJECT ( containerKind ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTCONTLINKEDOID ON IBS_OBJECT ( linkedObjectId ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTNAME ON IBS_OBJECT ( name ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTOWNER ON IBS_OBJECT ( owner ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTPOSNOPATH ON IBS_OBJECT ( posNoPath ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTRKEY ON IBS_OBJECT ( rKey ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTRTVERSIONID ON IBS_OBJECT ( tVersionId ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTVALIDUNTIL ON IBS_OBJECT ( validUntil ) /*TABLESPACE*/;

-- IBS_OBJECTDESC_01
CREATE INDEX indexObjectDescLanguageId ON ibs_ObjectDesc_01 (languageId) /*TABLESPACE*/;
CREATE INDEX indexObjectDescName ON ibs_ObjectDesc_01 (name) /*TABLESPACE*/;
CREATE INDEX indexObjectDescClassName ON ibs_ObjectDesc_01 (className) /*TABLESPACE*/;

-- IBS_OBJECTREAD
CREATE UNIQUE INDEX INDEXOBJECTREADOIDUSER ON IBS_OBJECTREAD ( oid,userId ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTREADOID ON IBS_OBJECTREAD ( oid ) /*TABLESPACE*/;
CREATE INDEX INDEXOBJECTREADUSER ON IBS_OBJECTREAD ( userId ) /*TABLESPACE*/;

-- IBS_OPERATION
CREATE INDEX INDEXOPERATIONID ON IBS_OPERATION ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXOPERATIONNAME ON IBS_OPERATION ( name ) /*TABLESPACE*/;

-- IBS_PROTOCOL_01
CREATE UNIQUE INDEX INDEXPROTOCOLID ON IBS_PROTOCOL_01 ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXPROTOCOLOID ON IBS_PROTOCOL_01 ( oid ) /*TABLESPACE*/;
CREATE INDEX INDEXPROTOCOLUSERID ON IBS_PROTOCOL_01 ( userid ) /*TABLESPACE*/;
CREATE INDEX INDEXPROTOCOLCONTAINERID ON IBS_PROTOCOL_01 ( containerId ) /*TABLESPACE*/;

-- IBS_RECEIVEDOBJECT_01
CREATE INDEX INDEXRECEIVEDOBJECTTVERSIONID ON IBS_RECEIVEDOBJECT_01 ( distributedTVersionId ) /*TABLESPACE*/;
CREATE INDEX INDEXRECEIVEDOBJECTDISID ON IBS_RECEIVEDOBJECT_01 ( distributedId ) /*TABLESPACE*/;
CREATE INDEX INDEXRECEIVEDOBJECTSENTID ON IBS_RECEIVEDOBJECT_01 ( sentObjectId ) /*TABLESPACE*/;

-- IBS_RECIPIENT_01
CREATE INDEX INDEXRECIPIENTREADDATE ON IBS_RECIPIENT_01 ( readDate ) /*TABLESPACE*/;
CREATE INDEX INDEXRECIPIENTRECIPIENTID ON IBS_RECIPIENT_01 ( recipientId ) /*TABLESPACE*/;
CREATE INDEX INDEXRECIPIENTSENTOBJECTOID ON IBS_RECIPIENT_01 ( sentObjectId ) /*TABLESPACE*/;

-- ibs_Reference
CREATE INDEX IndexReferenceOid ON ibs_Reference (referencingOid) /*TABLESPACE*/;
CREATE INDEX IndexReferenceRefOid ON ibs_Reference (referencedOid) /*TABLESPACE*/;

-- IBS_RIGHTSCUM
CREATE UNIQUE INDEX IndexRightsCumUidKey ON ibs_RightsCum (userId, rKey) /*TABLESPACE*/;
CREATE INDEX IndexRightsCumRKey ON ibs_RightsCum (rKey) /*TABLESPACE*/;

-- IBS_RIGHTSKEYS
CREATE UNIQUE INDEX IndexRightsKeysIdPerson ON ibs_RightsKeys (id, rPersonId) /*TABLESPACE*/;
CREATE INDEX IndexRightsKeysrPersonId ON ibs_RightsKeys (rPersonId) /*TABLESPACE*/;

-- IBS_SENTOBJECT_01
CREATE INDEX INDEXSENTOBJECTTVERSIONID ON IBS_SENTOBJECT_01 ( distributeTVersionId ) /*TABLESPACE*/;
CREATE INDEX INDEXSENTOBJECTID ON IBS_SENTOBJECT_01 ( distributeId ) /*TABLESPACE*/;

-- IBS_SYSTEM
CREATE UNIQUE INDEX INDEXSYSTEMNAME ON IBS_SYSTEM ( name ) /*TABLESPACE*/;

-- IBS_TAB
CREATE UNIQUE INDEX IndexTabId ON ibs_Tab (id) /*TABLESPACE*/;
CREATE INDEX IndexTabDomainIdCode ON ibs_Tab (domainId, code) /*TABLESPACE*/;

-- IBS_TOKEN_01
CREATE INDEX INDEXTOKENNAME ON IBS_TOKEN_01 ( name ) /*TABLESPACE*/;
CREATE INDEX INDEXTOKENLANGUAGEID ON IBS_TOKEN_01 ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXTOKENCLASSNAME ON IBS_TOKEN_01 ( CLASSNAME ) /*TABLESPACE*/;

-- IBS_TVERSION
CREATE INDEX INDEXTVERSIONSTATE ON IBS_TVERSION ( state ) /*TABLESPACE*/;
CREATE INDEX INDEXTVERSIONSUPERTVERSIONID ON IBS_TVERSION ( superTVersionId ) /*TABLESPACE*/;
CREATE UNIQUE INDEX INDEXTVERSIONID ON IBS_TVERSION ( id ) /*TABLESPACE*/;

-- ibs_TVersionProc
CREATE UNIQUE INDEX IndexTVersionProcIdCode ON ibs_TVersionProc (tVersionId, code) /*TABLESPACE*/;

-- IBS_TYPE
CREATE UNIQUE INDEX INDEXTYPEID ON IBS_TYPE ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXTYPENAME ON IBS_TYPE ( name ) /*TABLESPACE*/;
CREATE INDEX INDEXTYPESTATE ON IBS_TYPE ( state ) /*TABLESPACE*/;

-- IBS_TYPE_NAME
CREATE INDEX INDEXTYPENAMENAME ON IBS_TYPENAME_01 ( name ) /*TABLESPACE*/;
CREATE INDEX INDEXTYPENAMELANGUAGEID ON IBS_TYPENAME_01 ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXTYPENAMECLASSNAME ON IBS_TYPENAME_01 ( CLASSNAME ) /*TABLESPACE*/;

-- IBS_USER
CREATE UNIQUE INDEX INDEXUSERID ON IBS_USER ( id ) /*TABLESPACE*/;
CREATE INDEX INDEXUSEROID ON IBS_USER ( oid ) /*TABLESPACE*/;
CREATE INDEX INDEXUSERSTATE ON IBS_USER ( state ) /*TABLESPACE*/;
CREATE INDEX INDEXUSERNAME ON IBS_USER ( name ) /*TABLESPACE*/;

-- IBS_USERPROFILE
CREATE UNIQUE INDEX INDEXUSERPROFILEOID ON IBS_USERPROFILE ( oid ) /*TABLESPACE*/;

-- IBS_WORKFLOW_01
CREATE UNIQUE INDEX IndexWorkflowOid ON ibs_Workflow_01 (oid);
CREATE INDEX IndexWorkflowObjectId ON ibs_Workflow_01 (objectId);
CREATE INDEX IndexWorkflowDefinitionId ON ibs_Workflow_01 (definitionId);
CREATE INDEX IndexWorkflowStartDate ON ibs_Workflow_01 (startDate);
CREATE INDEX IndexWorkflowEndDate ON ibs_Workflow_01 (endDate);
CREATE INDEX IndexWorkflowProcessManager ON ibs_Workflow_01 (processManager);
CREATE INDEX IndexWorkflowStarter ON ibs_Workflow_01 (starter);
CREATE INDEX IndexWorkflowCurrentOwner ON ibs_Workflow_01 (currentOwner);
CREATE INDEX IndexWorkflowState ON ibs_Workflow_01 (workflowState);
-- IBS_RIGHTSMAPPING
CREATE INDEX IndexRightsMappingAliasName ON ibs_RightsMapping (aliasName);
-- IBS_WORKFLOWPROTOCOL
CREATE INDEX IndexWFProtocolInstanceId ON ibs_WorkflowProtocol (instanceId);
CREATE INDEX IndexWFProtocolObjectId ON ibs_WorkflowProtocol (objectId);
-- IBS_WORKFLOWVARIABLES
CREATE UNIQUE INDEX IndexWFVariablesIdName ON ibs_WorkflowVariables (instanceId, variableName);

-- IBS_WORKSPACE
CREATE INDEX INDEXWORKSPACEDOMAINID ON IBS_WORKSPACE ( domainId ) /*TABLESPACE*/;
CREATE INDEX INDEXWORKSPACEWORKSPACE ON IBS_WORKSPACE ( workspace ) /*TABLESPACE*/;
CREATE INDEX INDEXWORKSPACEOUTBOX ON IBS_WORKSPACE ( outBox ) /*TABLESPACE*/;
CREATE INDEX INDEXWORKSPACEINBOX ON IBS_WORKSPACE ( inBox ) /*TABLESPACE*/;

-- IBS_XMLVIEWERCONTAINER_01
CREATE UNIQUE INDEX IndexXMLViewerContainerOid ON ibs_XMLViewerContainer_01 (oid) /*TABLESPACE*/;

BEGIN
    debug ('createIndices: Indexes created.');
END;
/

COMMIT WORK;

EXIT;
