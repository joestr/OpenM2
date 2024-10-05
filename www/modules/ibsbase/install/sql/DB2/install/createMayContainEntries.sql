-------------------------------------------------------------------------------
-- Create entries for type dependencies. <BR>
-- This script stores in the database all information regarding which objects
-- may be contained in which other objects.
--
-- @version     $Id: createMayContainEntries.sql,v 1.6 2004/01/16 00:40:21 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pi_createMayContainEntries');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pi_createMayContainEntries ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE c_NOT_OK INT;
    DECLARE c_ALL_RIGHT INT;
    DECLARE l_retValue INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
	SET c_NOT_OK = 0;
	SET c_ALL_RIGHT = 1;
    -- initialize local variables:
	SET l_retValue = c_ALL_RIGHT;

    -- BusinessObject:
    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Container');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'File');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Url');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'XMLViewerContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'QueryExecutive');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'StateContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('BusinessObject', 'Note');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- Container:
    CALL IBSDEV1.p_MayContain$new('Container', 'Container');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'File');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'Url');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'XMLViewerContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'QueryExecutive');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'ServicePoint');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Container', 'Note');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- ConnectorContainer:
    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'FileConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'FTPConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'MailConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'HTTPConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'EDISwitchConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'HTTPScriptConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'SAPBCXMLRFCConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'SAPBCConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ConnectorContainer', 'HTTP MultipartConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- DocumentTemplateContainer:
    CALL IBSDEV1.p_MayContain$new('DocumentTemplateContainer', 'DocumentTemplate');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- ExportContainer:
    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'BusinessObject');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Container');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Referenz');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ReferenzContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Attachment');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'AttachmentContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Menu');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'SearchContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'User');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Group');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Role');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Rights');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'RightsContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Domain');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DomainSchemeContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DomainScheme');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'NewsContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'RecipientContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Recipient');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'SentObjectContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'SentObject');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Inbox');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Workspace');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'UserContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'GroupContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'UserAdminContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'UserProfile');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Workflow');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'WorkflowTemplate');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'WorkflowTemplateContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'MembershipContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Root');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'CleanContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ReceivedObject');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'LogContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'File');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Url');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'LayoutContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Layout');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ObjectSearchContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'IntegratorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ImportScript');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ImportScriptContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Connector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ConnectorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Translator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Note');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
END;
-- pi_createMayContainEntries


-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pi_createMayContainEntries2');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createMayContainEntries2 ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE INT;
    DECLARE c_NOT_OK INT;
    DECLARE c_ALL_RIGHT INT;
    DECLARE l_retValue INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- assign constants:
	SET c_NOT_OK = 0;
	SET c_ALL_RIGHT = 1;
    -- initialize local variables:
	SET l_retValue = c_ALL_RIGHT;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ASCIITranslator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'TranslatorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'FileConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'FTPConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'MailConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'HTTPConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'EDISwitchConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'HTTPScriptConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'HTTPMultipartConnector');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ImportContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'ExportContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DocumentTemplate');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DocumentTemplateContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'XMLViewerContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'HelpContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'Help');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'QueryCreator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'DBQueryCreator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'QueryExecutive');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'StateContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'MenuTabContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'MenuTab');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'QuerySelectContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('ExportContainer', 'EDITranslator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ImportScriptContainer:
    CALL IBSDEV1.p_MayContain$new('ImportScriptContainer', 'ImportScript');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- IntegratorContainer:
    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'Container');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'IntegratorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'ImportContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'ExportContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'ImportScriptContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'ConnectorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'XMLViewerContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'DocumentTemplateContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'WorkflowTemplateContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('IntegratorContainer', 'TranslatorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- TranslatorContainer:
    CALL IBSDEV1.p_MayContain$new('TranslatorContainer', 'Translator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('TranslatorContainer', 'ASCIITranslator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('TranslatorContainer', 'EDITranslator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- XMLViewerContainer:
    --    EXEC;l_retValue = p_MayContain$new 'XMLViewerContainer', 'XMLViewer'
    -- Inbox:
    CALL IBSDEV1.p_MayContain$new('Inbox', 'ReceivedObject');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- RecipientContainer:
    CALL IBSDEV1.p_MayContain$new('RecipientContainer', 'Recipient');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- SentObjectContainer:
    CALL IBSDEV1.p_MayContain$new('SentObjectContainer', 'SentObject');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- DomainSchemeContainer:
    CALL IBSDEV1.p_MayContain$new('DomainSchemeContainer', 'DomainScheme');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Domain:
    CALL IBSDEV1.p_MayContain$new('Domain', 'Container');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Domain', 'Workspace');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- AttachmentContainer:
    CALL IBSDEV1.p_MayContain$new('AttachmentContainer', 'Attachment');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- HelpContainer:
    CALL IBSDEV1.p_MayContain$new('HelpContainer', 'Help');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('HelpContainer', 'HelpContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- LayoutContainer:
    CALL IBSDEV1.p_MayContain$new('LayoutContainer', 'Layout');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- MenuTabContainer
    CALL IBSDEV1.p_MayContain$new('MenuTabContainer', 'MenuTab');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- ReferenzContainer:
    CALL IBSDEV1.p_MayContain$new('ReferenzContainer', 'Referenz');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Workspace:
    CALL IBSDEV1.p_MayContain$new('Workspace', 'Inbox');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Workspace', 'SentObjectContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Workspace', 'Container');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Workspace', 'WasteBasket');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Group:
    CALL IBSDEV1.p_MayContain$new('Group', 'Group');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('Group', 'User');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- User:
    CALL IBSDEV1.p_MayContain$new('User', 'Person');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- WorkflowTemplateContainer:
    CALL IBSDEV1.p_MayContain$new('WorkflowTemplateContainer', 'WorkflowTemplate');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- Root:
    CALL IBSDEV1.p_MayContain$new('Root', 'Domain');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- GroupContainer:
    CALL IBSDEV1.p_MayContain$new('GroupContainer', 'Group');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- MembershipContainer:
    CALL IBSDEV1.p_MayContain$new('MembershipContainer', 'Group');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- UserAdminContainer:
    CALL IBSDEV1.p_MayContain$new('UserAdminContainer', 'GroupContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('UserAdminContainer', 'UserContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- UserContainer:
    CALL IBSDEV1.p_MayContain$new('UserContainer', 'User');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- QueryCreatorContainer:
    CALL IBSDEV1.p_MayContain$new('QueryCreatorContainer', 'QueryCreator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('QueryCreatorContainer', 'DBQueryCreator');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_MayContain$new('QueryCreatorContainer', 'QueryCreatorContainer');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- WorkspaceTemplateContainer:
    CALL IBSDEV1.p_MayContain$new('WorkspaceTemplateContainer', 'WorkspaceTemplate');
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- show count messages again:
END;
-- pi_createMayContainEntries2

-- execute procedures:
CALL IBSDEV1.pi_createMayContainEntries;
CALL IBSDEV1.pi_createMayContainEntries2;
-- delete procedures:
CALL IBSDEV1.p_dropProc ('pi_createMayContainEntries');
CALL IBSDEV1.p_dropProc ('pi_createMayContainEntries2');
