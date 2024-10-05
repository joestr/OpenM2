/******************************************************************************
 * create all indices of the whole base system. <BR>
 *
 * @version     $Id: U320003i_createIndices.sql,v 1.1 2012/05/15 13:00:32 btatzmann Exp $
 *
 * @author      Andreas Jansa (AJ) 000814
 ******************************************************************************
 */
 -- don't show count messages:
SET NOCOUNT ON
GO

--
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
DECLARE 
    @indexName NVARCHAR (64),
    @tableName NVARCHAR (64)

DECLARE cursorAllIndexes CURSOR FOR
    select  i.name, o.name
    from    sysindexes i, sysobjects o
    where   (   o.type = 'U'
            and o.id = i.id
            )                           -- get tables for indixes
        and (   o.name like 'IBS_%'
            )
        and i.name not like 'PK__%'     -- primary key constraint
        and i.name not like 'UQ__%'     -- unique constraint
        and i.name not like '_WA_%'     -- unknown???
        and i.indid <> 255              -- without indexes for text or image
                                        -- data
        and i.indid <> 0                -- without indexes for non-clustered
                                        -- table 

-- open the cursor:
OPEN cursorAllIndexes

-- get the first index:
FETCH NEXT FROM cursorAllIndexes INTO @indexName, @tableName

-- loop through all found indexes:
WHILE (@@FETCH_STATUS <> - 1)           -- another index found?
BEGIN
    -- Because @@FETCH_STATUS may have one of the three values
    -- -2, -1, or 0 all of these cases must be checked.
    -- In this case the tuple is skipped if it was deleted
    -- during the execution of this procedure.
    IF (@@FETCH_STATUS <> -2)
    BEGIN
        -- drop index:
        EXECUTE (N'DROP INDEX ' + @tableName + '.' + @indexName)
    END -- if

    -- get the next index:
    FETCH NEXT FROM cursorAllIndexes INTO @indexName, @tableName
END -- while another index found

-- close cursor:
CLOSE cursorAllIndexes

-- remove cursor from system:
DEALLOCATE cursorAllIndexes
GO

--
-- CREATE ALL INDEXES
--


-- 
-- MODULE: IBS
--

-- IBS_ATTACHMENT_01
CREATE INDEX IndexAttachmentType ON ibs_Attachment_01 (attachmentType)
GO

-- IBS_CHECKOUT_01
CREATE UNIQUE INDEX IndexCheckoutOid ON ibs_Checkout_01 (oid)
GO
CREATE INDEX IndexCheckoutUserId ON ibs_Checkout_01 (userId)
GO

-- IBS_CONNECTOR_01
CREATE UNIQUE INDEX IndexConnectorOid ON ibs_Connector_01 (oid)
GO

-- ibs_ConsistsOf
CREATE UNIQUE INDEX IndexConsistsOfId ON ibs_ConsistsOf (id)
GO
CREATE INDEX IndexConsistsOfTVersionIdTabId ON ibs_ConsistsOf (tVersionId, tabId)
GO

-- IBS_DOMAIN_01
CREATE UNIQUE INDEX IndexDomainId ON ibs_Domain_01 (id)
GO
CREATE UNIQUE INDEX IndexDomainOid ON ibs_Domain_01 (oid)
GO
CREATE INDEX IndexDomainAdminId ON ibs_Domain_01 (adminId)
GO

-- IBS_DOMAINSCHEME_01
CREATE UNIQUE INDEX IndexDomainSchemeId ON ibs_DomainScheme_01 (id)
GO
CREATE UNIQUE INDEX IndexDomainSchemeOid ON ibs_DomainScheme_01 (oid)
GO

-- IBS_GROUP
CREATE UNIQUE INDEX IndexGroupId ON ibs_Group (id)
GO
CREATE UNIQUE INDEX IndexGroupOid ON ibs_Group (oid)
GO
CREATE INDEX IndexGroupState ON ibs_Group (state)
GO
CREATE INDEX IndexGroupDomainId ON ibs_Group (domainId)
GO
CREATE INDEX IndexGroupName ON ibs_Group (name)
GO

-- IBS_GROUPUSER
CREATE UNIQUE INDEX IndexGroupUserId ON ibs_GroupUser (id)
GO
CREATE INDEX IndexGroupUserGroupId ON ibs_GroupUser (groupId)
GO
CREATE INDEX IndexGroupUserUserId ON ibs_GroupUser (userId)
GO
CREATE INDEX IndexGroupUserOrigGroupId ON ibs_GroupUser (origGroupId)
GO
CREATE INDEX IndexGroupUserIdPath ON ibs_GroupUser (idPath)
GO

-- IBS_HELP_01
CREATE UNIQUE INDEX IndexHelpOid ON ibs_Help_01 (oid)
GO
CREATE INDEX IndexHelpSearchContent ON ibs_Help_01 (searchContent)
GO
 
-- IBS_KEYMAPPER
CREATE INDEX IndexKeyMapperOid ON ibs_KeyMapper (oid)
GO
CREATE INDEX IndexKeyMapperIdIdDomain ON ibs_KeyMapper (id, idDomain)
GO

-- IBS_LAYOUT_01
CREATE  INDEX INDEXLAYOUTNAME ON IBS_LAYOUT_01 (name)
GO

-- IBS_MENUTAB_01
CREATE UNIQUE INDEX IndexMenuTab_oid ON ibs_MenuTab_01 (oid)
GO
CREATE INDEX IndexMenuTab_priorityKey ON ibs_MenuTab_01 (priorityKey)
GO
CREATE INDEX IndexMenuTab_domainId ON ibs_MenuTab_01 (domainId)
GO

-- IBS_OBJECT
CREATE UNIQUE INDEX INDEXOBJECTID ON IBS_OBJECT (id)
GO
CREATE UNIQUE INDEX INDEXOBJECTOID ON IBS_OBJECT (oid)
GO
CREATE CLUSTERED INDEX INDEXOBJECTCONTAINERID ON IBS_OBJECT (containerId)
GO
CREATE  INDEX INDEXOBJECTCONTAINERKIND ON IBS_OBJECT (containerKind)
GO
CREATE  INDEX INDEXOBJECTCONTLINKEDOID ON IBS_OBJECT (linkedObjectId)
GO
CREATE  INDEX INDEXOBJECTNAME ON IBS_OBJECT (name)
GO
CREATE  INDEX INDEXOBJECTOWNER ON IBS_OBJECT (owner)
GO
CREATE  INDEX INDEXOBJECTPOSNOPATH ON IBS_OBJECT (posNoPath)
GO
CREATE  INDEX INDEXOBJECTRKEY ON IBS_OBJECT (rKey)
GO
CREATE  INDEX INDEXOBJECTRTVERSIONID ON IBS_OBJECT (tVersionId)
GO
CREATE  INDEX INDEXOBJECTVALIDUNTIL ON IBS_OBJECT (validUntil)
GO
CREATE  INDEX INDEXOBJECTCONTAINEROID2 ON IBS_OBJECT (containerOid2)
GO

-- IBS_OBJECTDESC_01
CREATE INDEX indexObjectDescLanguageId ON ibs_ObjectDesc_01 (languageId)
GO
CREATE INDEX indexObjectDescName ON ibs_ObjectDesc_01 (name)
GO
CREATE INDEX indexObjectDescClassName ON ibs_ObjectDesc_01 (className)
GO

-- IBS_OBJECTREAD
CREATE UNIQUE INDEX IndexObjectReadObjectUser ON ibs_ObjectRead (oid, userId)
GO
CREATE INDEX IndexObjectReadObject ON ibs_ObjectRead (oid)
GO
CREATE INDEX IndexObjectReadUser ON ibs_ObjectRead (userId)
GO

-- IBS_OPERATION
CREATE UNIQUE INDEX IndexOperationId ON ibs_Operation (id)
GO
CREATE INDEX IndexOperationName ON ibs_Operation (name)
GO

-- IBS_PROTOCOL_01
CREATE UNIQUE INDEX IndexProtocolId         ON ibs_Protocol_01 (id)
GO
CREATE INDEX IndexProtocol_oid_01           ON ibs_Protocol_01 (oid)
GO
CREATE INDEX IndexProtocol_containerId_01   ON ibs_Protocol_01 (containerId)
GO
CREATE  INDEX INDEXPROTOCOLUSERID ON IBS_PROTOCOL_01 (userid)
GO

-- IBS_PROTOCOLENTRY_01
CREATE UNIQUE INDEX IndexProtocolEntryId         ON ibs_ProtocolEntry_01 (id)
GO
CREATE INDEX IndexProtocolEntry_protocolId_01    ON ibs_ProtocolEntry_01 (protocolId)
GO

-- IBS_RECEIVEDOBJECT_01
CREATE INDEX IndexReceivedObjectdisId       ON ibs_ReceivedObject_01 (distributedId)
GO
CREATE INDEX IndexReceivedObjectTVersionId  ON ibs_ReceivedObject_01 (distributedTVersionId)
GO
CREATE INDEX IndexReceivedObjectsentId      ON ibs_ReceivedObject_01 (sentObjectId)
GO
GO

-- IBS_RECIPIENT_01
CREATE INDEX IndexRecipient_recipientId ON ibs_Recipient_01 (recipientId)
GO
CREATE INDEX IndexRecipient_readDate ON ibs_Recipient_01 (readDate)
GO
CREATE INDEX IndexRecipient_sentObjectId ON ibs_Recipient_01 (sentObjectId)
GO

-- ibs_Reference
CREATE INDEX IndexReferenceOid ON ibs_Reference (referencingOid)
GO
CREATE INDEX IndexReferenceRefOid ON ibs_Reference (referencedOid)
GO

-- IBS_RIGHTSCUM
CREATE UNIQUE INDEX IndexRightsCumUserIdKey ON ibs_RightsCum (userId, rKey)
GO
CREATE INDEX IndexRightsCumKey ON ibs_RightsCum (rKey)
GO

-- IBS_RIGHTSKEYS
CREATE UNIQUE INDEX IndexRightsKeysIdPerson ON ibs_RightsKeys (id, rPersonId)
GO
CREATE INDEX IndexRightsKeysrPersonId ON ibs_RightsKeys (rPersonId)
GO

-- IBS_RIGHTSKEY
CREATE UNIQUE INDEX I_RightsKeyId ON ibs_RightsKey (id)
GO
CREATE INDEX I_RightsKeyRKeysId ON ibs_RightsKey (rKeysId)
GO
CREATE INDEX I_RightsKeyRKeysIdOwner ON ibs_RightsKey (rKeysId, owner)
GO

-- IBS_SENTOBJECT_01
CREATE INDEX IndexSentObjectId          ON ibs_SentObject_01 (distributeId)
GO
CREATE INDEX IndexSentObjectTVersionId  ON ibs_SentObject_01 (distributeTVersionId)
GO

-- IBS_SYSTEM
CREATE UNIQUE INDEX IndexSystemName ON ibs_System (name)
GO

-- ibs_Tab
CREATE UNIQUE INDEX IndexTabId ON ibs_Tab (id)
GO
CREATE INDEX IndexTabDomainIdCode ON ibs_Tab (domainId, code)
GO

-- ibs_TVersion
CREATE UNIQUE INDEX IndexTVersionId ON ibs_TVersion (id)
GO
CREATE INDEX IndexTVersionState ON ibs_TVersion (state)
GO
CREATE INDEX IndexTVersionSuperTVersionId ON ibs_TVersion (superTVersionId)
GO
CREATE  INDEX IndexTVersionPosNoPath ON ibs_TVersion (posNoPath)
GO

-- ibs_TVersionProc
CREATE UNIQUE INDEX IndexTVersionProcIdCode ON ibs_TVersionProc (tVersionId, code)
GO

-- IBS_TYPE
CREATE UNIQUE INDEX IndexTypeId ON ibs_Type (id)
GO
CREATE INDEX IndexTypeName ON ibs_Type (name)
GO
CREATE INDEX IndexTypeState ON ibs_Type (state)
GO

-- IBS_TYPE_NAME
CREATE INDEX IndexTypeNameLanguageId ON ibs_TypeName_01 (languageId)
GO
CREATE INDEX IndexTypeNameName ON ibs_TypeName_01 (name)
GO
CREATE INDEX IndexTypeNameClassName ON ibs_TypeName_01 (className)
GO

-- IBS_USER
CREATE UNIQUE INDEX IndexUserId ON ibs_User (id)
GO
CREATE INDEX IndexUserOid ON ibs_User (oid)
GO
CREATE INDEX IndexUserState ON ibs_User (state)
GO
CREATE INDEX IndexUserName ON ibs_User (name)
GO

-- IBS_USERPROFILE
CREATE UNIQUE INDEX IndexUserProfileOid ON ibs_UserProfile (oid)
GO

-- IBS_WORKFLOW_01
CREATE UNIQUE INDEX IndexWorkflowOid ON ibs_Workflow_01 (oid)
GO
CREATE INDEX IndexWorkflowObjectId ON ibs_Workflow_01 (objectId)
GO
CREATE INDEX IndexWorkflowDefinitionId ON ibs_Workflow_01 (definitionId)
GO
CREATE INDEX IndexWorkflowStartDate ON ibs_Workflow_01 (startDate)
GO
CREATE INDEX IndexWorkflowEndDate ON ibs_Workflow_01 (endDate)
GO
CREATE INDEX IndexWorkflowProcessManager ON ibs_Workflow_01 (processManager)
GO
CREATE INDEX IndexWorkflowStarter ON ibs_Workflow_01 (starter)
GO
CREATE INDEX IndexWorkflowCurrentOwner ON ibs_Workflow_01 (currentOwner)
GO
CREATE INDEX IndexWorkflowState ON ibs_Workflow_01 (workflowState)
GO
-- IBS_RIGHTSMAPPING
CREATE INDEX IndexRightsMappingAliasName ON ibs_RightsMapping (aliasName)
GO
-- IBS_WORKFLOWPROTOCOL
CREATE INDEX IndexWFProtocolInstanceId ON ibs_WorkflowProtocol (instanceId)
GO
CREATE INDEX IndexWFProtocolObjectId ON ibs_WorkflowProtocol (objectId)
GO
-- IBS_WORKFLOWVARIABLES
CREATE UNIQUE INDEX IndexWFVariablesIdName ON ibs_WorkflowVariables (instanceId, variableName)
GO


-- IBS_WORKSPACE
CREATE INDEX IndexWorkspaceDomainId ON ibs_Workspace (domainId)
GO
CREATE INDEX IndexWorkspaceWorkspace ON ibs_Workspace (workspace)
GO
CREATE INDEX IndexWorkspaceOutBox ON ibs_Workspace (outBox)
GO
CREATE INDEX IndexWorkspaceInBox ON ibs_Workspace (inBox)
GO

-- IBS_XMLVIEWERCONTAINER_01
CREATE UNIQUE INDEX IndexXMLViewerContainerOid ON ibs_XMLViewerContainer_01 (oid)
GO

-- don't show count messages:
SET NOCOUNT OFF
GO