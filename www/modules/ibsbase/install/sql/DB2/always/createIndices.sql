--------------------------------------------------------------------------------
-- create all indices of the whole base system. <BR>
--
-- @version     $Id: createIndices.sql,v 1.6 2008/02/26 15:39:30 btatzmann Exp $
--
-- @author      Marcel Samek (MS)  020921
--------------------------------------------------------------------------------
-- get and drop all indixes where dropping is possible
-- the following indixes can not be dropped:
-- * index created by 'primary key' column constraint
-- * unique index created by 'unique' column constraint
-- * index on text or image structures
-- * indexes on a non-clustered table itself
--
-- only the framework indexes will be selected and dropped
-- prefixes: IBS_
--
CALL IBSDEV1.p_dropProc ('pi_createIndices');

CREATE PROCEDURE IBSDEV1.pi_createIndices ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
  DECLARE SQLCODE INT;
  DECLARE indexName VARCHAR (64);
  DECLARE tableName VARCHAR (64);
  DECLARE l_sqlcode INT DEFAULT 0;
  DECLARE l_sqlstatus INT;
    DECLARE l_SQL VARCHAR (255);

  DECLARE cursorAllIndexes CURSOR WITH HOLD FOR 

        SELECT i.name, o.name 
        FROM QSYS2.SYSINDEXES i, QSYS2.SYSTABLES o
        WHERE o.type = 'T' 
          AND o.NAME = i.TBNAME             -- get tables for indixes
          AND (
                (o.name LIKE 'IBS_%')
             OR (o.name LIKE 'DBM_%')
              ) 
          AND (i.name NOT LIKE 'PK__%')     -- primary key constraint
          AND (i.name NOT LIKE 'UQ__%')     -- unique constraint
          AND (i.name NOT LIKE '_WA_%');    -- unknown???
--          AND  i.indid <> 255             -- without indexes for text or image
                                            -- data
--          AND i.indid <> 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

        OPEN cursorAllIndexes;
    
    -- get the first index:
  SET l_sqlcode = 0;
  FETCH FROM cursorAllIndexes INTO indexName, tableName;
  SET l_sqlstatus = l_sqlcode;
    
    WHILE l_sqlstatus <> 100 DO
    
    -- another index found?
    -- Because @@FETCH_STATUS may have one of the three values
    -- -2, -1, or 0 all of these cases must be checked.
    -- In this case the tuple is skipped if it was deleted
    -- during the execution of this procedure.
     IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 

          -- drop index:
          SET l_SQL = 'DROP INDEX ' || tableName || '.' || indexName;
          EXECUTE IMMEDIATE l_SQL;
      END IF;

      SET l_sqlcode = 0;
      FETCH FROM cursorAllIndexes INTO indexName, tableName;
      SET l_sqlstatus = l_sqlcode;
  END WHILE;

  -- loop through all found indexes:
    -- if
    
    -- get the next index:
  
    -- while another index found
    
    -- close cursor:
    
    -- remove cursor from system:
    RETURN 0;
END;




--
-- CREATE ALL INDEXES
--
CALL IBSDEV1.p_dropProc ('pi_createIndices2');

CREATE PROCEDURE IBSDEV1.pi_createIndices2 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
-- 
-- MODULE: IBS
--

    -- IBS_ATTACHMENT_01
    CREATE INDEX IBSDEV1.I_AttachmentType ON ibs_Attachment_01 (attachmentType);

    -- ibs_Note_01
    CREATE INDEX IBSDEV1.I_Note_oid ON IBSDEV1.ibs_Note_01 (oid);

    -- IBS_CHECKOUT_01
    CREATE UNIQUE INDEX IBSDEV1.I_CheckoutOid ON IBSDEV1.ibs_Checkout_01 (oid);
    CREATE INDEX IBSDEV1.I_CheckoutUserId ON IBSDEV1.ibs_Checkout_01 (userId);

    -- IBS_CONNECTOR_01
    CREATE UNIQUE INDEX IBSDEV1.I_ConnectorOid ON IBSDEV1.ibs_Connector_01 (oid);

    -- ibs_ConsistsOf
    CREATE UNIQUE INDEX IBSDEV1.I_ConsistsOfId ON IBSDEV1.ibs_ConsistsOf (id);
    CREATE INDEX IBSDEV1.I_ConsistsOfTVersionIdTabId ON IBSDEV1.ibs_ConsistsOf (tVersionId, tabId);

    -- IBS_DOMAIN_01
    CREATE UNIQUE INDEX IBSDEV1.I_DomainId ON IBSDEV1.ibs_Domain_01 (id);
    CREATE UNIQUE INDEX IBSDEV1.I_DomainOid ON IBSDEV1.ibs_Domain_01 (oid);
    CREATE INDEX IBSDEV1.I_DomainAdminId ON IBSDEV1.ibs_Domain_01 (adminId);

    -- IBS_DOMAINSCHEME_01
    CREATE UNIQUE INDEX IBSDEV1.I_DomainSchemeId ON IBSDEV1.ibs_DomainScheme_01 (id);
    CREATE UNIQUE INDEX IBSDEV1.I_DomainSchemeOid ON IBSDEV1.ibs_DomainScheme_01 (oid);

    -- IBS_EXCEPTION_01
    CREATE INDEX IBSDEV1.I_ExceptionId ON IBSDEV1.ibs_Exception_01 (id);
    CREATE INDEX IBSDEV1.I_ExceptionName ON IBSDEV1.ibs_Exception_01 (name);
    CREATE INDEX IBSDEV1.I_ExceptionClassName ON IBSDEV1.ibs_Exception_01 (className);
END;
    
--
-- CREATE ALL INDEXES
--
CALL IBSDEV1.p_dropProc ('pi_createIndices3');

CREATE PROCEDURE IBSDEV1.pi_createIndices3 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;    
    -- IBS_GROUP
    CREATE UNIQUE INDEX IBSDEV1.I_GroupId ON IBSDEV1.ibs_Group (id);
    CREATE UNIQUE INDEX IBSDEV1.I_GroupOid ON IBSDEV1.ibs_Group (oid);
    CREATE INDEX IBSDEV1.I_GroupState ON IBSDEV1.ibs_Group (state);
    CREATE INDEX IBSDEV1.I_GroupDomainId ON IBSDEV1.ibs_Group (domainId);
    CREATE INDEX IBSDEV1.I_GroupName ON IBSDEV1.ibs_Group (name);

    -- IBS_GROUPUSER
    CREATE UNIQUE INDEX IBSDEV1.I_GroupUserId ON IBSDEV1.ibs_GroupUser (id);
    CREATE INDEX IBSDEV1.I_GroupUserGroupId ON IBSDEV1.ibs_GroupUser (groupId);
    CREATE INDEX IBSDEV1.I_GroupUserUserId ON IBSDEV1.ibs_GroupUser (userId);
    CREATE INDEX IBSDEV1.I_GroupUserOrigGroupId ON IBSDEV1.ibs_GroupUser (origGroupId);
    CREATE INDEX IBSDEV1.I_GroupUserIdPath ON IBSDEV1.ibs_GroupUser (idPath);

    -- IBS_HELP_01
    CREATE UNIQUE INDEX IBSDEV1.I_HelpOid ON IBSDEV1.ibs_Help_01 (oid);
    CREATE INDEX IBSDEV1.I_HelpSearchContent ON IBSDEV1.ibs_Help_01 (searchContent);

    -- IBS_KEYMAPPER
    CREATE INDEX IBSDEV1.I_KeyMapperOid ON IBSDEV1.ibs_KeyMapper (oid);
    CREATE INDEX IBSDEV1.I_KeyMapperIdIdDomain ON IBSDEV1.ibs_KeyMapper (id, idDomain);

    -- IBS_LAYOUT_01
    CREATE  INDEX IBSDEV1.I_LAYOUTNAME ON IBSDEV1.IBS_LAYOUT_01 (name);

    -- IBS_MENUTAB_01
    CREATE UNIQUE INDEX IBSDEV1.I_MenuTab_oid ON IBSDEV1.ibs_MenuTab_01 (oid);
    CREATE INDEX IBSDEV1.I_MenuTab_priorityKey ON IBSDEV1.ibs_MenuTab_01 (priorityKey);
    CREATE INDEX IBSDEV1.I_MenuTab_domainId ON IBSDEV1.ibs_MenuTab_01 (domainId);

    -- IBS_MESSAGE_01
    CREATE INDEX IBSDEV1.I_MessageLanguageId ON IBSDEV1.ibs_Message_01 (languageId);
    CREATE INDEX IBSDEV1.I_MessageName ON IBSDEV1.ibs_Message_01 (name);
    CREATE INDEX IBSDEV1.I_MessageClassName ON IBSDEV1.ibs_Message_01 (className);
END;


--
-- CREATE ALL INDEXES
--
CALL IBSDEV1.p_dropProc ('pi_createIndices4');

CREATE PROCEDURE IBSDEV1.pi_createIndices4 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- IBS_OBJECT
    CREATE UNIQUE INDEX IBSDEV1.I_OBJECTID ON IBSDEV1.IBS_OBJECT (id);
    CREATE UNIQUE INDEX IBSDEV1.I_OBJECTOID ON IBSDEV1.IBS_OBJECT (oid);
    CREATE INDEX IBSDEV1.I_OBJECTCONTAINERID ON IBSDEV1.IBS_OBJECT (containerId);
    CREATE INDEX IBSDEV1.I_OBJECTCONTAINERKIND ON IBSDEV1.IBS_OBJECT (containerKind);
    CREATE INDEX IBSDEV1.I_OBJECTCONTLINKEDOID ON IBSDEV1.IBS_OBJECT (linkedObjectId);
    CREATE INDEX IBSDEV1.I_OBJECTNAME ON IBSDEV1.IBS_OBJECT (name);
    CREATE INDEX IBSDEV1.I_OBJECTOWNER ON IBSDEV1.IBS_OBJECT (owner);
    CREATE INDEX IBSDEV1.I_OBJECTPOSNOPATH ON IBSDEV1.IBS_OBJECT (posNoPath);
    CREATE INDEX IBSDEV1.I_OBJECTRKEY ON IBSDEV1.IBS_OBJECT (rKey);
    CREATE INDEX IBSDEV1.I_OBJECTRTVERSIONID ON IBSDEV1.IBS_OBJECT (tVersionId);
    CREATE INDEX IBSDEV1.I_OBJECTVALIDUNTIL ON IBSDEV1.IBS_OBJECT (validUntil);
    -- IBS_OBJECTDESC_01
    CREATE INDEX IBSDEV1.I_ObjectDescLanguageId ON IBSDEV1.ibs_ObjectDesc_01 (languageId);
    CREATE INDEX IBSDEV1.I_ObjectDescName ON IBSDEV1.ibs_ObjectDesc_01 (name);
    CREATE INDEX IBSDEV1.I_ObjectDescClassName ON IBSDEV1.ibs_ObjectDesc_01 (className);

    -- IBS_OBJECTREAD
    CREATE UNIQUE INDEX IBSDEV1.I_ObjectReadObjectUser ON IBSDEV1.ibs_ObjectRead (oid, userId);
    CREATE INDEX IBSDEV1.I_ObjectReadObject ON IBSDEV1.ibs_ObjectRead (oid);
    CREATE INDEX IBSDEV1.I_ObjectReadUser ON IBSDEV1.ibs_ObjectRead (userId);

    -- IBS_OPERATION
    CREATE UNIQUE INDEX IBSDEV1.I_OperationId ON IBSDEV1.ibs_Operation (id);
    CREATE INDEX IBSDEV1.I_OperationName ON IBSDEV1.ibs_Operation (name);

    -- IBS_PROTOCOL_01
    CREATE UNIQUE INDEX IBSDEV1.I_ProtocolId ON IBSDEV1.ibs_Protocol_01 (id);
    CREATE INDEX IBSDEV1.I_Protocol_oid_01 ON IBSDEV1.ibs_Protocol_01 (oid);
    CREATE INDEX IBSDEV1.I_Protocol_containerId_01 ON IBSDEV1.ibs_Protocol_01 (containerId);
    CREATE INDEX IBSDEV1.I_PROTOCOLUSERID ON IBSDEV1.IBS_PROTOCOL_01 (userid);

    -- IBS_RECEIVEDOBJECT_01
    CREATE INDEX IBSDEV1.I_ReceivedObjectdisId ON IBSDEV1.ibs_ReceivedObject_01 (distributedId);
    CREATE INDEX IBSDEV1.I_ReceivedObjectTVersionId ON IBSDEV1.ibs_ReceivedObject_01 (distributedTVersionId);
    CREATE INDEX IBSDEV1.I_ReceivedObjectsentId ON IBSDEV1.ibs_ReceivedObject_01 (sentObjectId);

END;
--
-- CREATE ALL INDEXES
--
CALL IBSDEV1.p_dropProc ('pi_createIndices5');

CREATE PROCEDURE IBSDEV1.pi_createIndices5 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- IBS_RECIPIENT_01
    CREATE INDEX IBSDEV1.I_Recipient_recipientId ON IBSDEV1.ibs_Recipient_01 (recipientId);
    CREATE INDEX IBSDEV1.I_Recipient_readDate ON IBSDEV1.ibs_Recipient_01 (readDate);
    CREATE INDEX IBSDEV1.I_Recipient_sentObjectId ON IBSDEV1.ibs_Recipient_01 (sentObjectId);

    -- ibs_Reference
    CREATE INDEX IBSDEV1.I_ReferenceOid ON IBSDEV1.ibs_Reference (referencingOid);
    CREATE INDEX IBSDEV1.I_ReferenceRefOid ON IBSDEV1.ibs_Reference (referencedOid);

    -- IBS_RIGHTSCUM
    CREATE UNIQUE INDEX IBSDEV1.I_RightsCumUserIdKey ON IBSDEV1.ibs_RightsCum (userId, rKey);
    CREATE INDEX IBSDEV1.I_RightsCumKey ON IBSDEV1.ibs_RightsCum (rKey);        
    -- IBS_RIGHTSKEYS
    CREATE UNIQUE INDEX IBSDEV1.I_RightsKeysIdPerson ON IBSDEV1.ibs_RightsKeys (id, rPersonId);
    CREATE INDEX IBSDEV1.I_RightsKeysrPersonId ON IBSDEV1.ibs_RightsKeys (rPersonId);

    -- IBS_SENTOBJECT_01
    CREATE INDEX IBSDEV1.I_SentObjectId ON IBSDEV1.ibs_SentObject_01 (distributeId);
    CREATE INDEX IBSDEV1.I_SentObjectTVersionId ON IBSDEV1.ibs_SentObject_01 (distributeTVersionId);

    -- IBS_SYSTEM
    CREATE UNIQUE INDEX IBSDEV1.I_SystemName ON IBSDEV1.ibs_System (name);

    -- ibs_Tab
    CREATE UNIQUE INDEX IBSDEV1.I_TabId ON IBSDEV1.ibs_Tab (id);
    CREATE INDEX IBSDEV1.I_TabDomainIdCode ON IBSDEV1.ibs_Tab (domainId, code);

    -- IBS_TOKEN_01
    CREATE INDEX IBSDEV1.I_TokenId ON IBSDEV1.ibs_Token_01 (id);
    CREATE INDEX IBSDEV1.I_TokenName ON IBSDEV1.ibs_Token_01 (name);
    CREATE INDEX IBSDEV1.I_TokenClassName ON IBSDEV1.ibs_Token_01 (className);

    -- ibs_TVersion
    CREATE UNIQUE INDEX IBSDEV1.I_TVersionId ON IBSDEV1.ibs_TVersion (id);
    CREATE INDEX IBSDEV1.I_TVersionState ON IBSDEV1.ibs_TVersion (state);
    CREATE INDEX IBSDEV1.I_TVersionSuperTVersionId ON IBSDEV1.ibs_TVersion (superTVersionId);
    CREATE INDEX IBSDEV1.I_TVersionPosNoPath ON IBSDEV1.ibs_TVersion (posNoPath);

    -- ibs_TVersionProc
    CREATE UNIQUE INDEX IBSDEV1.I_TVersionProcIdCode ON IBSDEV1.ibs_TVersionProc (tVersionId, code);

    -- IBS_TYPE
    CREATE UNIQUE INDEX IBSDEV1.I_TypeId ON IBSDEV1.ibs_Type (id);
    CREATE INDEX IBSDEV1.I_TypeName ON IBSDEV1.ibs_Type (name);
    CREATE INDEX IBSDEV1.I_TypeState ON IBSDEV1.ibs_Type (state);

    -- IBS_TYPE_NAME
    CREATE INDEX IBSDEV1.I_TypeNameLanguageId ON IBSDEV1.ibs_TypeName_01 (languageId);
    CREATE INDEX IBSDEV1.I_TypeNameName ON IBSDEV1.ibs_TypeName_01 (name);
    CREATE INDEX IBSDEV1.I_TypeNameClassName ON IBSDEV1.ibs_TypeName_01 (className);

    -- IBS_USER
    CREATE UNIQUE INDEX IBSDEV1.I_UserId ON IBSDEV1.ibs_User (id);
    CREATE INDEX IBSDEV1.I_UserOid ON IBSDEV1.ibs_User (oid);
    CREATE INDEX IBSDEV1.I_UserState ON IBSDEV1.ibs_User (state);
    CREATE INDEX IBSDEV1.I_UserName ON IBSDEV1.ibs_User (name);

    -- IBS_USERPROFILE
    CREATE UNIQUE INDEX IBSDEV1.I_UserProfileOid ON IBSDEV1.ibs_UserProfile (oid);
END;

--
-- CREATE ALL INDEXES
--
CALL IBSDEV1.p_dropProc ('pi_createIndices6');

CREATE PROCEDURE IBSDEV1.pi_createIndices6 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- IBS_WORKFLOW_01
    CREATE UNIQUE INDEX IBSDEV1.I_WorkflowOid ON IBSDEV1.ibs_Workflow_01 (oid);
    CREATE INDEX IBSDEV1.I_WorkflowObjectId ON IBSDEV1.ibs_Workflow_01 (objectId);
    CREATE INDEX IBSDEV1.I_WorkflowDefinitionId ON IBSDEV1.ibs_Workflow_01 (definitionId);
    CREATE INDEX IBSDEV1.I_WorkflowStartDate ON IBSDEV1.ibs_Workflow_01 (startDate);
    CREATE INDEX IBSDEV1.I_WorkflowEndDate ON IBSDEV1.ibs_Workflow_01 (endDate);
    CREATE INDEX IBSDEV1.I_WorkflowProcessManager ON IBSDEV1.ibs_Workflow_01 (processManager);
    CREATE INDEX IBSDEV1.I_WorkflowStarter ON IBSDEV1.ibs_Workflow_01 (starter);
    CREATE INDEX IBSDEV1.I_WorkflowCurrentOwner ON IBSDEV1.ibs_Workflow_01 (currentOwner);
    CREATE INDEX IBSDEV1.I_WorkflowState ON IBSDEV1.ibs_Workflow_01 (workflowState);

    -- IBS_RIGHTSMAPPING
    CREATE INDEX IBSDEV1.I_RightsMappingAliasName ON IBSDEV1.ibs_RightsMapping (aliasName);

    -- IBS_WORKFLOWPROTOCOL
    CREATE INDEX IBSDEV1.I_WFProtocolInstanceId ON IBSDEV1.ibs_WorkflowProtocol (instanceId);
    CREATE INDEX IBSDEV1.I_WFProtocolObjectId ON IBSDEV1.ibs_WorkflowProtocol (objectId);

    -- IBS_WORKFLOWVARIABLES
    CREATE UNIQUE INDEX IBSDEV1.I_WFVariablesIdName ON IBSDEV1.ibs_WorkflowVariables (instanceId, variableName);

    -- IBS_WORKSPACE
    CREATE INDEX IBSDEV1.I_WorkspaceDomainId ON IBSDEV1.ibs_Workspace (domainId);
    CREATE INDEX IBSDEV1.I_WorkspaceWorkspace ON IBSDEV1.ibs_Workspace (workspace);
    CREATE INDEX IBSDEV1.I_WorkspaceOutBox ON IBSDEV1.ibs_Workspace (outBox);
    CREATE INDEX IBSDEV1.I_WorkspaceInBox ON IBSDEV1.ibs_Workspace (inBox);

    -- IBS_XMLVIEWERCONTAINER_01
    CREATE UNIQUE INDEX IBSDEV1.I_XMLViewerContainerOid ON IBSDEV1.ibs_XMLViewerContainer_01 (oid);
END;


-- execute procedures:
CALL IBSDEV1.pi_createIndices;
CALL IBSDEV1.pi_createIndices2;
CALL IBSDEV1.pi_createIndices3;
CALL IBSDEV1.pi_createIndices4;
CALL IBSDEV1.pi_createIndices5;
CALL IBSDEV1.pi_createIndices6;

-- delete procedures:
CALL IBSDEV1.p_dropProc ('pi_createIndices');
CALL IBSDEV1.p_dropProc ('pi_createIndices2');
CALL IBSDEV1.p_dropProc ('pi_createIndices3');
CALL IBSDEV1.p_dropProc ('pi_createIndices4');
CALL IBSDEV1.p_dropProc ('pi_createIndices5');
CALL IBSDEV1.p_dropProc ('pi_createIndices6');
