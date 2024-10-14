--------------------------------------------------------------------------------
-- Create all business object types within the framework. <BR>
--
-- @version     $Id: createBaseObjectTypes.sql,v 1.9 2003/12/30 00:09:02 klaus Exp $
--
-- @author      Marcel Samek (MS)  020921
----------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pi_createBaseObjTypes ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_languageId INT;
    DECLARE c_id_Address INT;                                               
    DECLARE c_id_ASCIITranslator_01 INT;                                    
    DECLARE c_id_Attachment INT;                                            
    DECLARE c_id_AttachmentContainer INT;                                   
    DECLARE c_id_BusinessObject INT;                                         
    DECLARE c_id_CleanContainer INT;                                        
    DECLARE c_id_Connector INT;                                             
    DECLARE c_id_ConnectorContainer INT;                                    
    DECLARE c_id_Container INT;                                             
    DECLARE c_id_DocumentTemplate INT;                                      
    DECLARE c_id_DocumentTemplateContainer INT;                             
    DECLARE c_id_Domain INT;                                                
    DECLARE c_id_DomainScheme INT;                                          
    DECLARE c_id_DomainSchemeContainer INT;                                 
    DECLARE c_id_EDISwitchConnector INT;                                    
    DECLARE c_id_ExportContainer INT;                                       
    DECLARE c_id_File INT;                                                  
    DECLARE c_id_FileConnector INT;                                         
    DECLARE c_id_FTPConnector INT;                                          
    DECLARE c_id_Group INT;                                                 
    DECLARE c_id_GroupContainer INT;                                        
    DECLARE c_id_Help_01 INT;                                               
    DECLARE c_id_HelpContainer_01 INT;                                      
    DECLARE c_id_HTTPConnector INT;                                         
    DECLARE c_id_HTTPMultipartConnector INT;                                
    DECLARE c_id_HTTPScriptConnector INT;                                   
    DECLARE c_id_ImportContainer INT;                                       
    DECLARE c_id_ImportScript INT;                                          
    DECLARE c_id_ImportScriptContainer INT;                                 
    DECLARE c_id_Inbox INT;                                                 
    DECLARE c_id_IntegratorContainer INT;                                   
    DECLARE c_id_Layout INT;                                                
    DECLARE c_id_LayoutContainer INT;                                       
    DECLARE c_id_LogContainer INT;                                          
    DECLARE c_id_MailConnector INT;                                         
    DECLARE c_id_MembershipContainer INT;                                   
    DECLARE c_id_Menu INT;                                                  
    DECLARE c_id_NewsContainer INT;                                         
    DECLARE c_id_Note INT;                                                  
    DECLARE c_id_ObjectSearchContainer INT;                                 
    DECLARE c_id_QueryCreator_01 INT;                                       
    DECLARE c_id_QueryCreatorContainer_01 INT;                              
    DECLARE c_id_QueryExecutive_01 INT;                                     
    DECLARE c_id_ReceivedObject INT;                                        
    DECLARE c_id_Recipient INT;                                             
    DECLARE c_id_RecipientContainer INT;                                    
    DECLARE c_id_Referenz INT;                                              
    DECLARE c_id_ReferenzContainer INT;                                     
    DECLARE c_id_Rights INT;                                                
    DECLARE c_id_RightsContainer INT;                                       
    DECLARE c_id_Role INT;                                                  
    DECLARE c_id_Root INT;                                                  
    DECLARE c_id_SAPBCXMLRFCConnector INT;                                  
    DECLARE c_id_SearchContainer INT;                                       
    DECLARE c_id_SentObject INT;                                            
    DECLARE c_id_SentObjectContainer INT;                                   
    DECLARE c_id_ServicePoint_01 INT;                                       
    DECLARE c_id_SimpleSearchContainer_01 INT;                              
    DECLARE c_id_Translator INT;                                            
    DECLARE c_id_TranslatorContainer INT;                                   
    DECLARE c_id_Url INT;                                                   
    DECLARE c_id_User INT;                                                  
    DECLARE c_id_UserAddress_01 INT;                                        
    DECLARE c_id_UserAdminContainer INT;                                    
    DECLARE c_id_UserContainer INT;                                         
    DECLARE c_id_UserProfile INT;                                           
    DECLARE c_id_WasteBasket_01 INT;                                        
    DECLARE c_id_Workflow INT;                                              
    DECLARE c_id_WorkflowTemplate INT;                                      
    DECLARE c_id_WorkflowTemplateContainer INT;                             
    DECLARE c_id_Workspace INT;                                             
    DECLARE c_id_XMLViewer INT;                                             
    DECLARE c_id_XMLViewerContainer_01 INT;                                 

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
  
    -- the current language
    -- local variables:
    -- initializations:
    SET c_languageId = 0;

               -- BusinessObject
    SET c_id_BusinessObject = IBSDEV1.p_hexStringToInt('01010010');
    CALL IBSDEV1.p_Type$newLang (c_id_BusinessObject, '', 0, 1,
                            0, 0, 0, 'BusinessObject',
                            'ibs.bo.BusinessObject', c_languageId, 'TN_BusinessObject');
    -- Container
    SET c_id_Container = IBSDEV1.p_hexStringToInt('01010020');
    CALL IBSDEV1.p_Type$newLang (c_id_Container, 'BusinessObject', 1, 1,
                          1, 1, 0, 'Container',
                          'ibs.bo.Container', c_languageId, 'TN_Container');
  
    -- references:
    -- Referenz
    SET c_id_Referenz = IBSDEV1.p_hexStringToInt('01010030');
    CALL IBSDEV1.p_Type$newLang (c_id_Referenz, 'BusinessObject', 0, 0,
                          0, 0, 0, 'Referenz',
                          'ibs.obj.ref.Referenz_01', c_languageId, 'TN_Referenz_01');
  
    -- ReferenzContainer
    SET c_id_ReferenzContainer = IBSDEV1.p_hexStringToInt('01010040');
    CALL IBSDEV1.p_Type$newLang (c_id_ReferenzContainer, 'Container', 1, 0,
                          0, 0, 0, 'ReferenzContainer',
                          'ibs.obj.ref.ReferenzContainer_01', c_languageId, 'TN_ReferenzContainer_01');
  
    -- attachments:
    -- Attachment
    SET c_id_Attachment = IBSDEV1.p_hexStringToInt('01010050');
    CALL IBSDEV1.p_Type$newLang (c_id_Attachment, 'BusinessObject', 0, 1,
                          1, 0, 1, 'Attachment',
                          'ibs.obj.doc.Attachment_01', c_languageId, 'TN_Attachment_01');
  
    -- AttachmentContainer
    SET c_id_AttachmentContainer = IBSDEV1.p_hexStringToInt('01010060');
    CALL IBSDEV1.p_Type$newLang (c_id_AttachmentContainer, 'Container', 1, 1,
                          0, 0, 0, 'AttachmentContainer',
                          'ibs.obj.doc.AttachmentContainer_01', c_languageId, 'TN_AttachmentContainer_01');
  
    -- menu:
    -- Menu
    SET c_id_Menu = IBSDEV1.p_hexStringToInt('01010070');
    CALL IBSDEV1.p_Type$newLang (c_id_Menu, 'Container', 1, 1,
                          0, 1, 0, 'Menu',
                          'ibs.obj.menu.Menu_01', c_languageId, 'TN_Menu_01');
    -- search:
    -- SearchContainer
    SET c_id_SearchContainer = IBSDEV1.p_hexStringToInt('01010090');
    CALL IBSDEV1.p_Type$newLang (c_id_SearchContainer, 'Container', 1, 1,
                          0, 1, 0, 'SearchContainer',
                          'ibs.obj.search.SearchContainer_01', c_languageId, 'TN_SearchContainer_01');
  
    -- user management:
    -- User
    SET c_id_User = IBSDEV1.p_hexStringToInt('010100A0');
    CALL IBSDEV1.p_Type$newLang (c_id_User, 'BusinessObject', 0, 1,
                          1, 0, 0, 'User',
                          'ibs.obj.user.User_01', c_languageId, 'TN_User_01');
    
    -- Group
    SET c_id_Group = IBSDEV1.p_hexStringToInt('010100B0');
    CALL IBSDEV1.p_Type$newLang (c_id_Group, 'BusinessObject', 0, 1,
                          1, 0, 0, 'Group',
                          'ibs.obj.user.Group_01', c_languageId, 'TN_Group_01');
  
    -- Role
    SET c_id_Role = IBSDEV1.p_hexStringToInt('010100C0');
  
    -- rights management:
    -- Rights
    SET c_id_Rights = IBSDEV1.p_hexStringToInt('010100D0');
    CALL IBSDEV1.p_Type$newLang (c_id_Rights, 'BusinessObject', 0, 0,
                          0, 0, 0, 'Rights',
                          'ibs.obj.user.Rights_01', c_languageId, 'TN_Rights_01');
  
    -- RightsContainer
    SET c_id_RightsContainer = IBSDEV1.p_hexStringToInt('010100E0');
    CALL IBSDEV1.p_Type$newLang (c_id_RightsContainer, 'Container', 1, 0,
                          0, 0, 0, 'RightsContainer',
                          'ibs.obj.user.RightsContainer_01', c_languageId, 'TN_RightsContainer_01');
  
    -- domains:
    -- Domain
    SET c_id_Domain = IBSDEV1.p_hexStringToInt('010100F0');
    CALL IBSDEV1.p_Type$newLang (c_id_Domain, 'Container', 1, 0,
                          0, 0, 0, 'Domain',
                          'ibs.obj.dom.Domain_01', c_languageId, 'TN_Domain_01');

    -- DomainSchemeContainer
    SET c_id_DomainSchemeContainer = IBSDEV1.p_hexStringToInt('01010110');
    CALL IBSDEV1.p_Type$newLang (c_id_DomainSchemeContainer, 'Container', 1, 1,
                          0, 1, 0, 'DomainSchemeContainer',
                          'ibs.obj.dom.DomainSchemeContainer_01', c_languageId, 'TN_DomainSchemeContainer_01');
  
    -- DomainScheme
    SET c_id_DomainScheme = IBSDEV1.p_hexStringToInt('01010120');
    CALL IBSDEV1.p_Type$newLang (c_id_DomainScheme, 'BusinessObject', 0, 1,
                          0, 0, 0, 'DomainScheme',
                          'ibs.obj.dom.DomainScheme_01', c_languageId, 'TN_DomainScheme_01');
  
    -- news:
    -- NewsContainer
    SET c_id_NewsContainer = IBSDEV1.p_hexStringToInt('01010800');
    CALL IBSDEV1.p_Type$newLang (c_id_NewsContainer, 'Container', 1, 1,
                          0, 1, 0, 'NewsContainer',
                          'ibs.obj.wsp.NewsContainer_01', c_languageId, 'TN_NewsContainer_01');
  
    -- distribution:
    -- RecipientContainer
    SET c_id_RecipientContainer = IBSDEV1.p_hexStringToInt('01011B00');
    CALL IBSDEV1.p_Type$newLang (c_id_RecipientContainer, 'Container', 1, 1,
                          0, 0, 0, 'RecipientContainer',
                          'ibs.obj.wsp.RecipientContainer_01', c_languageId, 'TN_RecipientContainer_01');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes2');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createBaseObjTypes2 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_languageId INT;
    DECLARE c_id_Address INT;                                               
    DECLARE c_id_ASCIITranslator_01 INT;                                    
    DECLARE c_id_Attachment INT;                                            
    DECLARE c_id_AttachmentContainer INT;                                   
    DECLARE c_id_BusinessObject INT;                                         
    DECLARE c_id_CleanContainer INT;                                        
    DECLARE c_id_Connector INT;                                             
    DECLARE c_id_ConnectorContainer INT;                                    
    DECLARE c_id_Container INT;                                             
    DECLARE c_id_DocumentTemplate INT;                                      
    DECLARE c_id_DocumentTemplateContainer INT;                             
    DECLARE c_id_Domain INT;                                                
    DECLARE c_id_DomainScheme INT;                                          
    DECLARE c_id_DomainSchemeContainer INT;                                 
    DECLARE c_id_EDISwitchConnector INT;                                    
    DECLARE c_id_ExportContainer INT;                                       
    DECLARE c_id_File INT;                                                  
    DECLARE c_id_FileConnector INT;                                         
    DECLARE c_id_FTPConnector INT;                                          
    DECLARE c_id_Group INT;                                                 
    DECLARE c_id_GroupContainer INT;                                        
    DECLARE c_id_Help_01 INT;                                               
    DECLARE c_id_HelpContainer_01 INT;                                      
    DECLARE c_id_HTTPConnector INT;                                         
    DECLARE c_id_HTTPMultipartConnector INT;                                
    DECLARE c_id_HTTPScriptConnector INT;                                   
    DECLARE c_id_ImportContainer INT;                                       
    DECLARE c_id_ImportScript INT;                                          
    DECLARE c_id_ImportScriptContainer INT;                                 
    DECLARE c_id_Inbox INT;                                                 
    DECLARE c_id_IntegratorContainer INT;                                   
    DECLARE c_id_Layout INT;                                                
    DECLARE c_id_LayoutContainer INT;                                       
    DECLARE c_id_LogContainer INT;                                          
    DECLARE c_id_MailConnector INT;                                         
    DECLARE c_id_MembershipContainer INT;                                   
    DECLARE c_id_Menu INT;                                                  
    DECLARE c_id_NewsContainer INT;                                         
    DECLARE c_id_Note INT;                                                  
    DECLARE c_id_ObjectSearchContainer INT;                                 
    DECLARE c_id_QueryCreator_01 INT;                                       
    DECLARE c_id_QueryCreatorContainer_01 INT;                              
    DECLARE c_id_QueryExecutive_01 INT;                                     
    DECLARE c_id_ReceivedObject INT;                                        
    DECLARE c_id_Recipient INT;                                             
    DECLARE c_id_RecipientContainer INT;                                    
    DECLARE c_id_Referenz INT;                                              
    DECLARE c_id_ReferenzContainer INT;                                     
    DECLARE c_id_Rights INT;                                                
    DECLARE c_id_RightsContainer INT;                                       
    DECLARE c_id_Role INT;                                                  
    DECLARE c_id_Root INT;                                                  
    DECLARE c_id_SAPBCXMLRFCConnector INT;                                  
    DECLARE c_id_SearchContainer INT;                                       
    DECLARE c_id_SentObject INT;                                            
    DECLARE c_id_SentObjectContainer INT;                                   
    DECLARE c_id_ServicePoint_01 INT;                                       
    DECLARE c_id_SimpleSearchContainer_01 INT;                              
    DECLARE c_id_Translator INT;                                            
    DECLARE c_id_TranslatorContainer INT;                                   
    DECLARE c_id_Url INT;                                                   
    DECLARE c_id_User INT;                                                  
    DECLARE c_id_UserAddress_01 INT;                                        
    DECLARE c_id_UserAdminContainer INT;                                    
    DECLARE c_id_UserContainer INT;                                         
    DECLARE c_id_UserProfile INT;                                           
    DECLARE c_id_WasteBasket_01 INT;                                        
    DECLARE c_id_Workflow INT;                                              
    DECLARE c_id_WorkflowTemplate INT;                                      
    DECLARE c_id_WorkflowTemplateContainer INT;                             
    DECLARE c_id_Workspace INT;                                             
    DECLARE c_id_XMLViewer INT;                                             
    DECLARE c_id_XMLViewerContainer_01 INT;                                 
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
  
    -- the current language
    -- local variables:
    -- initializations:
    SET c_languageId = 0;
    -- Recipient
    SET c_id_Recipient = IBSDEV1.p_hexStringToInt('01011C00');
    CALL IBSDEV1.p_Type$newLang (c_id_Recipient, 'BusinessObject', 0, 1,
                          0, 0, 0, 'Recipient',
                          'ibs.obj.wsp.Recipient_01', c_languageId, 'TN_Recipient_01');

    -- SentObjectContainer
    SET c_id_SentObjectContainer = IBSDEV1.p_hexStringToInt('01011D00');
    CALL IBSDEV1.p_Type$newLang (c_id_SentObjectContainer, 'Container', 1, 1,
                          0, 1, 0, 'SentObjectContainer',
                          'ibs.obj.wsp.SentObjectContainer_01', c_languageId, 'TN_SentObjectContainer_01');
  
    -- SentObject
    SET c_id_SentObject = IBSDEV1.p_hexStringToInt('01011E00');
    CALL IBSDEV1.p_Type$newLang (c_id_SentObject, 'BusinessObject', 0, 1,
                          0, 0, 0, 'SentObject',
                          'ibs.obj.wsp.SentObject_01', c_languageId, 'TN_SentObject_01');
  
    -- distribution:
    -- Inbox
    SET c_id_Inbox = IBSDEV1.p_hexStringToInt('01012D00');
    CALL IBSDEV1.p_Type$newLang (c_id_Inbox, 'Container', 1, 1,
                          0, 1, 0, 'Inbox',
                          'ibs.obj.wsp.Inbox_01', c_languageId, 'TN_Inbox_01');
  
    -- master data:
    -- Address
    SET c_id_Address = IBSDEV1.p_hexStringToInt('01012F00');
    CALL IBSDEV1.p_Type$newLang (c_id_Address, 'BusinessObject', 0, 1,
                          0, 0, 1, 'Address',
                          'ibs.bo.Address_01', c_languageId, 'TN_Address_01');
  
    -- user management:
    -- Workspace
    SET c_id_Workspace = IBSDEV1.p_hexStringToInt('01013200');
    CALL IBSDEV1.p_Type$newLang (c_id_Workspace, 'Container', 1, 1,
                          0, 1, 0, 'Workspace',
                          'ibs.obj.wsp.Workspace_01', c_languageId, 'TN_Workspace_01');
  
    -- UserContainer
    SET c_id_UserContainer = IBSDEV1.p_hexStringToInt('01013300');
    CALL IBSDEV1.p_Type$newLang (c_id_UserContainer, 'Container', 1, 1,
                          0, 1, 0, 'UserContainer',
                          'ibs.obj.user.UserContainer_01', c_languageId, 'TN_UserContainer_01');
  
    -- GroupContainer
    SET c_id_GroupContainer = IBSDEV1.p_hexStringToInt('01013400');
    CALL IBSDEV1.p_Type$newLang (c_id_GroupContainer, 'Container', 1, 1,
                          0, 1, 0, 'GroupContainer',
                          'ibs.obj.user.GroupContainer_01', c_languageId, 'TN_GroupContainer_01');
  
    -- UserAdminContainer
    SET c_id_UserAdminContainer = IBSDEV1.p_hexStringToInt('01013600');
    CALL IBSDEV1.p_Type$newLang (c_id_UserAdminContainer, 'Container', 1, 1,
                          0, 1, 0, 'UserAdminContainer',
                          'ibs.obj.user.UserAdminContainer_01', c_languageId, 'TN_UserAdminContainer_01');
  
    -- UserAddress_01
    -- inherits from BusinessObject
    SET c_id_UserAddress_01 = IBSDEV1.p_hexStringToInt('01012F10');
    CALL IBSDEV1.p_Type$newLang (c_id_UserAddress_01, 'BusinessObject', 0, 1,
                          0, 0, 0, 'UserAddress',
                          'ibs.obj.user.UserAddress_01', c_languageId, 'TN_UserAddress_01');

    -- UserProfile
    SET c_id_UserProfile = IBSDEV1.p_hexStringToInt('01013800');
    CALL IBSDEV1.p_Type$newLang (c_id_UserProfile, 'BusinessObject', 0, 1,
                          0, 1, 0, 'UserProfile',
                          'ibs.obj.user.UserProfile_01', c_languageId, 'TN_UserProfile_01');
  
    -- user management:
    -- MembershipContainer
    SET c_id_MembershipContainer = IBSDEV1.p_hexStringToInt('01015200');
    CALL IBSDEV1.p_Type$newLang (c_id_MembershipContainer, 'Container', 1, 1,
                          0, 0, 0, 'MembershipContainer',
                          'ibs.obj.user.MemberShip_01', c_languageId, 'TN_MemberShip_01');
  
    -- root:
    -- Root
    SET c_id_Root = IBSDEV1.p_hexStringToInt('01015300');
    CALL IBSDEV1.p_Type$newLang (c_id_Root, 'Container', 1, 0,
                          0, 1, 0, 'Root',
                          'ibs.obj.dom.Root_01', c_languageId, 'TN_Root_01');
  
    -- distribution:
    -- ReceivedObject
    SET c_id_ReceivedObject = IBSDEV1.p_hexStringToInt('01015600');
    CALL IBSDEV1.p_Type$newLang (c_id_ReceivedObject, 'BusinessObject', 0, 1,
                          0, 0, 0, 'ReceivedObject',
                          'ibs.obj.wsp.ReceivedObject_01', c_languageId, 'TN_ReceivedObject_01');
  
    -- base:
    -- CleanContainer
    SET c_id_CleanContainer = IBSDEV1.p_hexStringToInt('01015700');
    CALL IBSDEV1.p_Type$newLang (c_id_CleanContainer, 'Container', 1, 1,
                          0, 0, 0, 'CleanContainer',
                          'ibs.obj.wsp.CleanContainer_01', c_languageId, 'TN_CleanContainer_01');
  
    -- Procotol:
    -- LogContainer
    SET c_id_LogContainer = IBSDEV1.p_hexStringToInt('01015900');
    CALL IBSDEV1.p_Type$newLang (c_id_LogContainer, 'Container', 1, 1,
                          0, 0, 0, 'LogContainer',
                          'ibs.obj.log.LogContainer_01', c_languageId, 'TN_LogContainer_01');

    -- Document Management:
    -- File
    SET c_id_File = IBSDEV1.p_hexStringToInt('01016800');
    CALL IBSDEV1.p_Type$newLang (c_id_File, 'Attachment', 0, 1,
                          1, 0, 1, 'File',
                          'ibs.obj.doc.File_01', c_languageId, 'TN_File_01');
  
    -- Url
    SET c_id_Url = IBSDEV1.p_hexStringToInt('01016900');
    CALL IBSDEV1.p_Type$newLang (c_id_Url, 'Attachment', 0, 1,
                          1, 0, 1, 'Url',
                          'ibs.obj.doc.Url_01', c_languageId, 'TN_Url_01');
  
    -- user:
    -- PersonSearchContainer
    SET c_id_PersonSearchContainer = IBSDEV1.p_hexStringToInt('01016E00');
    CALL IBSDEV1.p_Type$newLang (c_id_PersonSearchContainer, 'Container', 1, 1,
                          0, 0, 0, 'PersonSearchContainer',
                          'ibs.obj.user.PersonSearchContainer_01', c_languageId, 'TN_PersonSearchContainer_01');
  
    -- layout:
    -- LayoutContainer
    SET c_id_LayoutContainer = IBSDEV1.p_hexStringToInt('01016F00');
    CALL IBSDEV1.p_Type$newLang (c_id_LayoutContainer, 'Container', 1, 1,
                          0, 1, 0, 'LayoutContainer',
                          'ibs.obj.layout.LayoutContainer_01', c_languageId, 'TN_LayoutContainer_01');
  
    -- Layout
    SET c_id_Layout = IBSDEV1.p_hexStringToInt('01017000');
    CALL IBSDEV1.p_Type$newLang (c_id_Layout, 'BusinessObject', 0, 1,
                          0, 0, 0, 'Layout',
                          'ibs.obj.layout.Layout_01', c_languageId, 'TN_Layout_01');
  
    -- Data Interchange:
    -- ObjectSearchContainer
    SET c_id_ObjectSearchContainer = IBSDEV1.p_hexStringToInt('01017310');
    CALL IBSDEV1.p_Type$newLang (c_id_ObjectSearchContainer, 'Container', 1, 1,
                          0, 0, 0, 'ObjectSearchContainer',
                          'ibs.obj.search.ObjectSearchContainer_01', c_languageId, 'TN_ObjectSearchContainer_01');
  
    -- IntegratorContainer
    SET c_id_IntegratorContainer = IBSDEV1.p_hexStringToInt('01017400');
    CALL IBSDEV1.p_Type$newLang (c_id_IntegratorContainer, 'Container', 1, 1,
                          0, 1, 0, 'IntegratorContainer',
                          'ibs.di.IntegratorContainer_01', c_languageId, 'TN_IntegratorContainer_01');
  
    -- ImportScript
    SET c_id_ImportScript = IBSDEV1.p_hexStringToInt('01017410');
    CALL IBSDEV1.p_Type$newLang (c_id_ImportScript, 'File', 0, 1,
                          0, 0, 0, 'ImportScript',
                          'ibs.di.imp.ImportScript_01', c_languageId, 'TN_ImportScript_01');
  
    -- ImportScriptContainer
    SET c_id_ImportScriptContainer = IBSDEV1.p_hexStringToInt('01017420');
    CALL IBSDEV1.p_Type$newLang (c_id_ImportScriptContainer, 'Container', 1, 1,
                          0, 1, 0, 'ImportScriptContainer',
                          'ibs.di.imp.ImportScriptContainer_01', c_languageId, 'TN_ImportScriptContainer_01');
  
    -- Connector
    SET c_id_Connector = IBSDEV1.p_hexStringToInt('01017430');
    CALL IBSDEV1.p_Type$newLang (c_id_Connector, 'BusinessObject', 0, 1,
                          0, 0, 0, 'Connector',
                          'ibs.di.connect.Connector_01', c_languageId, 'TN_Connector_01');
  
    -- ConnectorContainer
    SET c_id_ConnectorContainer = IBSDEV1.p_hexStringToInt('01017440');
    CALL IBSDEV1.p_Type$newLang (c_id_ConnectorContainer, 'Container', 1, 1,
                          0, 1, 0, 'ConnectorContainer',
                          'ibs.di.connect.ConnectorContainer_01', c_languageId, 'TN_ConnectorContainer_01');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes3');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createBaseObjTypes3 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_languageId INT;
    DECLARE c_id_Address INT;                                               
    DECLARE c_id_ASCIITranslator_01 INT;                                    
    DECLARE c_id_Attachment INT;                                            
    DECLARE c_id_AttachmentContainer INT;                                   
    DECLARE c_id_BusinessObject INT;                                         
    DECLARE c_id_CleanContainer INT;                                        
    DECLARE c_id_Connector INT;                                             
    DECLARE c_id_ConnectorContainer INT;                                    
    DECLARE c_id_Container INT;                                             
    DECLARE c_id_DocumentTemplate INT;                                      
    DECLARE c_id_DocumentTemplateContainer INT;                             
    DECLARE c_id_Domain INT;                                                
    DECLARE c_id_DomainScheme INT;                                          
    DECLARE c_id_DomainSchemeContainer INT;                                 
    DECLARE c_id_EDISwitchConnector INT;                                    
    DECLARE c_id_ExportContainer INT;                                       
    DECLARE c_id_File INT;                                                  
    DECLARE c_id_FileConnector INT;                                         
    DECLARE c_id_FTPConnector INT;                                          
    DECLARE c_id_Group INT;                                                 
    DECLARE c_id_GroupContainer INT;                                        
    DECLARE c_id_Help_01 INT;                                               
    DECLARE c_id_HelpContainer_01 INT;                                      
    DECLARE c_id_HTTPConnector INT;                                         
    DECLARE c_id_HTTPMultipartConnector INT;                                
    DECLARE c_id_HTTPScriptConnector INT;                                   
    DECLARE c_id_ImportContainer INT;                                       
    DECLARE c_id_ImportScript INT;                                          
    DECLARE c_id_ImportScriptContainer INT;                                 
    DECLARE c_id_Inbox INT;                                                 
    DECLARE c_id_IntegratorContainer INT;                                   
    DECLARE c_id_Layout INT;                                                
    DECLARE c_id_LayoutContainer INT;                                       
    DECLARE c_id_LogContainer INT;                                          
    DECLARE c_id_MailConnector INT;                                         
    DECLARE c_id_MembershipContainer INT;                                   
    DECLARE c_id_Menu INT;                                                  
    DECLARE c_id_NewsContainer INT;                                         
    DECLARE c_id_Note INT;                                                  
    DECLARE c_id_ObjectSearchContainer INT;                                 
    DECLARE c_id_QueryCreator_01 INT;                                       
    DECLARE c_id_QueryCreatorContainer_01 INT;                              
    DECLARE c_id_QueryExecutive_01 INT;                                     
    DECLARE c_id_ReceivedObject INT;                                        
    DECLARE c_id_Recipient INT;                                             
    DECLARE c_id_RecipientContainer INT;                                    
    DECLARE c_id_Referenz INT;                                              
    DECLARE c_id_ReferenzContainer INT;                                     
    DECLARE c_id_Rights INT;                                                
    DECLARE c_id_RightsContainer INT;                                       
    DECLARE c_id_Role INT;                                                  
    DECLARE c_id_Root INT;                                                  
    DECLARE c_id_SAPBCXMLRFCConnector INT;                                  
    DECLARE c_id_SearchContainer INT;                                       
    DECLARE c_id_SentObject INT;                                            
    DECLARE c_id_SentObjectContainer INT;                                   
    DECLARE c_id_ServicePoint_01 INT;                                       
    DECLARE c_id_SimpleSearchContainer_01 INT;                              
    DECLARE c_id_Translator INT;                                            
    DECLARE c_id_TranslatorContainer INT;                                   
    DECLARE c_id_Url INT;                                                   
    DECLARE c_id_User INT;                                                  
    DECLARE c_id_UserAddress_01 INT;                                        
    DECLARE c_id_UserAdminContainer INT;                                    
    DECLARE c_id_UserContainer INT;                                         
    DECLARE c_id_UserProfile INT;                                           
    DECLARE c_id_WasteBasket_01 INT;                                        
    DECLARE c_id_Workflow INT;                                              
    DECLARE c_id_WorkflowTemplate INT;                                      
    DECLARE c_id_WorkflowTemplateContainer INT;                             
    DECLARE c_id_Workspace INT;                                             
    DECLARE c_id_XMLViewer INT;                                             
    DECLARE c_id_XMLViewerContainer_01 INT;                                 
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
  
    -- the current language
    -- local variables:
    -- initializations:
    SET c_languageId = 0;  
    -- Translator
    SET c_id_Translator = IBSDEV1.p_hexStringToInt('01017450');
    CALL IBSDEV1.p_Type$newLang (c_id_Translator, 'File', 0, 1,
                          0, 0, 0, 'Translator',
                          'ibs.di.trans.Translator_01', c_languageId, 'TN_Translator_01');
  
    -- ASCIITranslator_01
    SET c_id_ASCIITranslator_01 = IBSDEV1.p_hexStringToInt('01017380');
    CALL IBSDEV1.p_Type$newLang (c_id_ASCIITranslator_01, 'Translator', 0, 1,
                          0, 0, 0, 'ASCIITranslator',
                          'ibs.di.trans.ASCIITranslator_01', c_languageId, 'TN_ASCIITranslator_01');
  
    -- TranslatorContainer
    SET c_id_TranslatorContainer = IBSDEV1.p_hexStringToInt('01017460');
    CALL IBSDEV1.p_Type$newLang (c_id_TranslatorContainer, 'Container', 1, 1,
                          0, 1, 0, 'TranslatorContainer',
                          'ibs.di.trans.TranslatorContainer_01', c_languageId, 'TN_TranslatorContainer_01');
  
    -- FileConnector
    SET c_id_FileConnector = IBSDEV1.p_hexStringToInt('01017470');
    CALL IBSDEV1.p_Type$newLang (c_id_FileConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'FileConnector', 'ibs.di.connect.FileConnector_01',
                          c_languageId, 'TN_FileConnector_01');
  
    -- FTPConnector
    SET c_id_FTPConnector = IBSDEV1.p_hexStringToInt('01017480');
    CALL IBSDEV1.p_Type$newLang (c_id_FTPConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'FTPConnector', 'ibs.di.connect.FTPConnector_01',
                          c_languageId, 'TN_FTPConnector_01');
  
    -- MailConnector
    SET c_id_MailConnector = IBSDEV1.p_hexStringToInt('01017490');
    CALL IBSDEV1.p_Type$newLang (c_id_MailConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'MailConnector', 'ibs.di.connect.MailConnector_01',
                          c_languageId, 'TN_MailConnector_01');
  
    -- HTTPConnector
    SET c_id_HTTPConnector = IBSDEV1.p_hexStringToInt('010174A0');
    CALL IBSDEV1.p_Type$newLang (c_id_HTTPConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'HTTPConnector', 'ibs.di.connect.HTTPConnector_01',
                          c_languageId, 'TN_HTTPConnector_01');
  
    -- EDISwitchConnector
    SET c_id_EDISwitchConnector = IBSDEV1.p_hexStringToInt('010174B0');
    CALL IBSDEV1.p_Type$newLang (c_id_EDISwitchConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'EDISwitchConnector', 'ibs.di.connect.EDISwitchConnector_01',
                          c_languageId, 'TN_EDISwitchConnector_01');
  
    -- HTTPScriptConnector
    SET c_id_HTTPScriptConnector = IBSDEV1.p_hexStringToInt('010174C0');
    CALL IBSDEV1.p_Type$newLang (c_id_HTTPScriptConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'HTTPScriptConnector', 'ibs.di.connect.HTTPScriptConnector_01',
                          c_languageId, 'TN_HTTPScriptConnector_01');
  
    -- SAPBCXMLRFCConnector
    SET c_id_SAPBCXMLRFCConnector = IBSDEV1.p_hexStringToInt('010174D0');
    CALL IBSDEV1.p_Type$newLang (c_id_SAPBCXMLRFCConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'SAPBCXMLRFCConnector', 'ibs.di.connect.SAPBCXMLRFCConnector_01',
                          c_languageId, 'TN_SAPBCXMLRFCConnector_01');
  
    -- HTTPMultipartConnector
    SET c_id_HTTPMultipartConnector = IBSDEV1.p_hexStringToInt('010174E0');
    CALL IBSDEV1.p_Type$newLang (c_id_HTTPMultipartConnector, 'Connector', 0,
                          1, 0, 0,
                          0, 'HTTPMultipartConnector', 'ibs.di.connect.HTTPMultipartConnector_01',
                          c_languageId, 'TN_HTTPMultipartConnector_01');
  
    -- XMLViewer
    SET c_id_XMLViewer = IBSDEV1.p_hexStringToInt('01017500');
    CALL IBSDEV1.p_Type$newLang (c_id_XMLViewer, 'BusinessObject', 0,
                          1, 1, 0,
                          1, 'XMLViewer', 'ibs.di.XMLViewer_01',
                          c_languageId, 'TN_XMLViewer_01');

    -- Data Interchange
    -- ImportContainer
    SET c_id_ImportContainer = IBSDEV1.p_hexStringToInt('01017900');
    CALL IBSDEV1.p_Type$newLang (c_id_ImportContainer, 'Container', 1,
                          1, 0, 1,
                          0, 'ImportContainer', 'ibs.di.imp.ImportContainer_01',
                          c_languageId, 'TN_ImportContainer_01');
  
    -- ExportContainer
    SET c_id_ExportContainer = IBSDEV1.p_hexStringToInt('01017A00');
    CALL IBSDEV1.p_Type$newLang (c_id_ExportContainer, 'Container', 1,
                          1, 0, 1,
                          0, 'ExportContainer', 'ibs.di.exp.ExportContainer_01',
                          c_languageId, 'TN_ExportContainer_01');
  
    -- Data Interchange:
    -- DocumentTemplate
    SET c_id_DocumentTemplate = IBSDEV1.p_hexStringToInt('01017C00');
    CALL IBSDEV1.p_Type$newLang (c_id_DocumentTemplate, 'File', 0,
                          1, 0, 0,
                          0, 'DocumentTemplate', 'ibs.di.DocumentTemplate_01',
                          c_languageId, 'TN_DocumentTemplate_01');
  
    -- DocumentTemplateContainer
    SET c_id_DocumentTemplateContainer = IBSDEV1.p_hexStringToInt('01017D00');
    CALL IBSDEV1.p_Type$newLang (c_id_DocumentTemplateContainer, 'Container', 1,
                          1, 0, 1,
                          0, 'DocumentTemplateContainer', 'ibs.di.DocumentTemplateContainer_01',
                          c_languageId, 'TN_DocumentTemplateContainer_01');
  
    -- XMLViewerContainer_01
    SET c_id_XMLViewerContainer_01 = IBSDEV1.p_hexStringToInt('01017E00');
    CALL IBSDEV1.p_Type$newLang (c_id_XMLViewerContainer_01, 'Container', 1,
                          1, 0, 1,
                          0, 'XMLViewerContainer', 'ibs.di.XMLViewerContainer_01',
                          c_languageId, 'TN_XMLViewerContainer_01');
  
    -- Help
    -- HelpContainer_01
    SET c_id_HelpContainer_01 = IBSDEV1.p_hexStringToInt('01017F00');
    CALL IBSDEV1.p_Type$newLang (c_id_HelpContainer_01, 'Container', 1,
                          1, 1, 1,
                          0, 'HelpContainer', 'ibs.obj.help.HelpContainer_01',
                          c_languageId, 'TN_HelpContainer_01');
  
    -- Help_01
    SET c_id_Help_01 = IBSDEV1.p_hexStringToInt('01017F10');
    CALL IBSDEV1.p_Type$newLang (c_id_Help_01, 'BusinessObject', 0,
                          1, 1, 0,
                          0, 'Help', 'ibs.obj.help.Help_01',
                          c_languageId, 'TN_Help_01');
  
    -- Search
    -- SimpleSearchContainer_01
    SET c_id_SimpleSearchContainer_01 = IBSDEV1.p_hexStringToInt('01017F30');
    CALL IBSDEV1.p_Type$newLang (c_id_SimpleSearchContainer_01, 'Container', 1,
                          1, 0, 1,
                          0, 'SimpleSearchContainer', 'ibs.obj.search.SimpleSearchContainer_01',
                          c_languageId, 'TN_SimpleSearchContainer_01');
  
    -- QueryCreator_01
    SET c_id_QueryCreator_01 = IBSDEV1.p_hexStringToInt('01017F20');
    CALL IBSDEV1.p_Type$newLang (c_id_QueryCreator_01, 'BusinessObject', 0,
                          1, 0, 1,
                          0, 'QueryCreator', 'ibs.obj.query.QueryCreator_01',
                          c_languageId, 'TN_QueryCreator_01');
  
    -- QueryCreatorContainer_01
    SET c_id_QueryCreatorContainer_01 = IBSDEV1.p_hexStringToInt('01017F40');
    CALL IBSDEV1.p_Type$newLang (c_id_QueryCreatorContainer_01, 'Container', 1,
                          1, 0, 1,
                          0, 'QueryCreatorContainer', 'ibs.obj.query.QueryCreatorContainer_01',
                          c_languageId, 'TN_QueryCreatorContainer_01');
  
    -- QueryExecutive_01
    -- this type does not inherit from container, because it doesn't physicaly
    -- contents m2-objects, but it's java-class extends container.
    SET c_id_QueryExecutive_01 = IBSDEV1.p_hexStringToInt('01017F50');
    CALL IBSDEV1.p_Type$newLang (c_id_QueryExecutive_01, 'BusinessObject', 0,
                          0, 0, 1,
                          0, 'QueryExecutive', 'ibs.obj.query.QueryExecutive_01',
                          c_languageId, 'TN_QueryExecutive_01');
  
    -- Workflow Management:
    -- Workflow
    SET c_id_Workflow = IBSDEV1.p_hexStringToInt('01014800');
    CALL IBSDEV1.p_Type$newLang (c_id_Workflow, 'BusinessObject', 0,
                          1, 0, 0,
                          0, 'Workflow', 'ibs.obj.workflow.Workflow_01',
                          c_languageId, 'TN_Workflow_01');
  
    -- WorkflowTemplate
    SET c_id_WorkflowTemplate = IBSDEV1.p_hexStringToInt('01014C00');
    CALL IBSDEV1.p_Type$newLang (c_id_WorkflowTemplate, 'Container', 0,
                          1, 0, 0,
                          0, 'WorkflowTemplate', 'ibs.obj.workflow.WorkflowTemplate_01',
                          c_languageId, 'TN_WorkflowTemplate_01');
  
    -- WorkflowTemplateContainer
    SET c_id_WorkflowTemplateContainer = IBSDEV1.p_hexStringToInt('01014D00');
    CALL IBSDEV1.p_Type$newLang (c_id_WorkflowTemplateContainer, 'Container', 1,
                          1, 0, 1,
                          0, 'WorkflowTemplateContainer', 'ibs.obj.workflow.WorkflowTemplateContainer_01',
                          c_languageId, 'TN_WorkflowTemplateContainer_01');
  
    -- ServicePoint_01
    -- inherits from XMLViewerContainer_01
    SET c_id_ServicePoint_01 = IBSDEV1.p_hexStringToInt('01010190');
    CALL IBSDEV1.p_Type$newLang (c_id_ServicePoint_01, 'XMLViewerContainer', 1,
                          1, 1, 0,
                          0, 'ServicePoint', 'ibs.service.servicepoint.ServicePoint_01',
                          c_languageId, 'TN_ServicePoint_01');
  
    -- WasteBasket_01
    -- inherits from Container_01
    SET c_id_WasteBasket_01 = IBSDEV1.p_hexStringToInt('010101A0');
    CALL IBSDEV1.p_Type$newLang (c_id_WasteBasket_01, 'Container', 1,
                          0, 0, 1,
                          0, 'WasteBasket', 'ibs.obj.wsp.WasteBasket_01',
                          0, 'TN_WasteBasket_01');
  
    -- documents:
    -- Note
    SET c_id_Note = IBSDEV1.p_hexStringToInt('01016B00');
    CALL IBSDEV1.p_Type$newLang (c_id_Note, 'BusinessObject', 0, 1,
                          1, 0, 0, 'Note',
                          'm2.note.Note_01', c_languageId, 'TN_Note_01');
  
-------------------------------------------------------------------------------
-- The following types do not have predefined type ids.
-- This is necessary due to the fact that type ids for other object types can
-- be set dynamically and to avoid that different types have the same id.
-- menutabs:
-- MenuTabContainer
    CALL IBSDEV1.p_Type$newLang (0, 'Container', 1,
                          1, 0, 1,
                          0, 'MenuTabContainer', 'ibs.obj.menu.MenuTabContainer_01',
                          c_languageId, 'TN_MenuTabContainer_01');
  
    -- MenuTab
    CALL IBSDEV1.p_Type$newLang (0, 'BusinessObject', 0,
                          1, 0, 0,
                          0, 'MenuTab', 'ibs.obj.menu.MenuTab_01',
                          c_languageId, 'TN_MenuTab_01');
  
    -- QuerySelectContainer
    CALL IBSDEV1.p_Type$newLang (0, 'Container', 1,
                          1, 0, 0,
                          0, 'QuerySelectContainer', 'ibs.obj.menu.QuerySelectContainer_01',
                          c_languageId, 'TN_QuerySelectContainer_01');
  
    -- StateContainer_01
    CALL IBSDEV1.p_Type$newLang (0, 'Container', 1,
                          1, 0, 1,
                          0, 'StateContainer', 'ibs.bo.StateContainer_01',
                          c_languageId, 'TN_StateContainer_01');
  
    -- EDITranslator_01
    CALL IBSDEV1.p_Type$newLang (0, 'Translator', 0,
                          1, 0, 0,
                          0, 'EDITranslator', 'ibs.di.edi.EDITranslator_01',
                          c_languageId, 'TN_EDITranslator_01');
  
    -- DBQueryCreator_01
    CALL IBSDEV1.p_Type$newLang (0, 'QueryCreator', 0,
                          1, 0, 1,
                          0, 'DBQueryCreator', 'ibs.obj.query.DBQueryCreator_01',
                          c_languageId, 'TN_DBQueryCreator_01');
  
    -- WorkspaceTemplateContainer
    CALL IBSDEV1.p_Type$newLang (0, 'Container', 1,
                          1, 0, 1,
                          0, 'WorkspaceTemplateContainer', 'ibs.bo.Container',
                          c_languageId, 'TN_WorkspaceTemplateContainer_01');
  
    -- WorkspaceTemplate
    CALL IBSDEV1.p_Type$newLang (0, 'File', 0,
                          1, 0, 1,
                          0, 'WorkspaceTemplate', 'ibs.obj.user.WorkspaceTemplate_01',
                          c_languageId, 'TN_WorkspaceTemplate_01');
  
    -- SAPBCConnector
    CALL IBSDEV1.p_Type$newLang (0, 'Connector', 0,
                          1, 0, 0,
                          0, 'SAPBCConnector', 'ibs.di.connect.SAPBCConnector_01',
                          c_languageId, 'TN_SAPBCConnector_01');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes4');
    -- register all predefined tabs:
    -- EXEC @l_retValue = p_Tab$new domainId, code, kind, 
    -- tVersionId, fct, priority,multilangKey, rights, @l_tabId OUTPUT
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createBaseObjTypes4 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_OP_READ       INT;            -- operation for reading
    DECLARE c_TK_VIEW       INT;
    DECLARE c_TK_OBJECT     INT;
    DECLARE c_TK_LINK       INT;
    DECLARE c_TK_FUNCTION   INT;
    
    DECLARE c_id_binary1    INT;    
    DECLARE c_id_binary2    INT;
    DECLARE c_id_binary3    INT;    
    DECLARE c_id_binary4    INT;
    DECLARE c_id_binary5    INT;    
    DECLARE c_id_binary6    INT;
    DECLARE c_id_binary7    INT;    
    DECLARE c_id_binary8    INT;
    DECLARE c_id_binary9    INT;    
    DECLARE c_id_binary10   INT;
    DECLARE c_id_binary11   INT;    
    DECLARE c_id_binary12   INT;
    DECLARE c_id_binary13   INT;    
    DECLARE c_id_binary14   INT;
    DECLARE c_id_binary15   INT;    
    DECLARE c_id_binary16   INT;
    DECLARE c_id_binary17   INT;    
    DECLARE c_id_binary18   INT;
    DECLARE c_id_binary19   INT;    
    DECLARE c_id_binary20   INT;
    DECLARE c_id_binary21   INT;    
    DECLARE c_id_binary22   INT;
    DECLARE c_id_binary23   INT;    
    DECLARE c_id_binary24   INT;
    
    -- local variables:
    DECLARE l_retValue INT;-- return value of a function
    DECLARE l_tabId INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET l_sqlcode = SQLCODE;
  
    -- id of actual tab
    -- assign constants:
    SET c_ALL_RIGHT = 1;
    SET c_languageId = 0;
    SET c_OP_READ = 4;
    SET c_TK_VIEW = 1;
    SET c_TK_OBJECT = 2;
    SET c_TK_LINK = 3;
    SET c_TK_FUNCTION = 4;
    
    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;
    SET l_tabId = 0;

    CALL IBSDEV1.p_Tab$new(0, 'Content', c_TK_VIEW, 0, 41,
                     10000, 'OD_tabContent', 4194304, NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_Tab$new(0, 'ContentFrameset', c_TK_VIEW, 0,
                     41, 9900, 'OD_tabContentFrameset', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_Tab$new(0, 'Info', c_TK_VIEW, 0,
                     56, 9000, 'OD_tabInfo', 4,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary2 = IBSDEV1.p_hexStringToInt('01012F11');
    CALL IBSDEV1.p_Tab$new(0, 'AddressValues', c_TK_OBJECT, c_id_binary2,
                     51, 0, 'OD_tabAddressValues', 4,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary4 = IBSDEV1.p_hexStringToInt('01010061');
    CALL IBSDEV1.p_Tab$new(0, 'Attachments', c_TK_OBJECT, c_id_binary4,
                     51, 0, 'OD_tabAttachments', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary5 = IBSDEV1.p_hexStringToInt('01012601');
    CALL IBSDEV1.p_Tab$new(0, 'Attributes', c_TK_OBJECT, c_id_binary5,
                     51, 0, 'OD_tabAttributes', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary8 = IBSDEV1.p_hexStringToInt('01014A01');
    CALL IBSDEV1.p_Tab$new(0, 'Buttons', c_TK_OBJECT, c_id_binary8,
                     51, 0, 'OD_tabButtons', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary10 = IBSDEV1.p_hexStringToInt('01015501');
    CALL IBSDEV1.p_Tab$new(0, 'Containership', c_TK_OBJECT, c_id_binary10,
                     51, 0, 'OD_tabContainership', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_Tab$new(0, 'Filter', c_TK_VIEW, 0,
                     93, 0, 'OD_tabFilter', 4,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    SET c_id_binary11 = IBSDEV1.p_hexStringToInt('01013701');
    CALL IBSDEV1.p_Tab$new(0, 'Groups', c_TK_OBJECT, c_id_binary11,
                     51, 0, 'OD_tabGroups', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary12 = IBSDEV1.p_hexStringToInt('01015201');
    CALL IBSDEV1.p_Tab$new(0, 'Membership', c_TK_OBJECT, c_id_binary12,
                     51, 0, 'OD_tabMembership', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary13 = IBSDEV1.p_hexStringToInt('01012801');
    CALL IBSDEV1.p_Tab$new(0, 'Methods', c_TK_OBJECT, c_id_binary13,
                     51, 0, 'OD_tabMethods', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary14 = IBSDEV1.p_hexStringToInt('01013101');
    CALL IBSDEV1.p_Tab$new(0, 'Parameters', c_TK_OBJECT, c_id_binary14,
                     51, 0, 'OD_tabParameters', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_Tab$new(0, 'Private', c_TK_FUNCTION, 0,
                     271, 0, 'OD_tabPrivate', 4,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary19 = IBSDEV1.p_hexStringToInt('01011B01');
    CALL IBSDEV1.p_Tab$new(0, 'Recipients', c_TK_OBJECT, c_id_binary19,
                     51, 0, 'OD_tabRecipients', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary20 = IBSDEV1.p_hexStringToInt('01010041');
    CALL IBSDEV1.p_Tab$new(0, 'References', c_TK_OBJECT, c_id_binary20,
                     51, 0, 'OD_tabReferences', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary21 = IBSDEV1.p_hexStringToInt('01013A01');
    CALL IBSDEV1.p_Tab$new(0, 'Tabs', c_TK_OBJECT, c_id_binary21,
                     51, 0, 'OD_tabTabs', 4194304,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    SET c_id_binary22 = IBSDEV1.p_hexStringToInt('01017D11');
    CALL IBSDEV1.p_Tab$new(0, 'Templates', c_TK_OBJECT, c_id_binary22,
                     51, 0, 'OD_tabTemplates', 1048576,
                     NULL, l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_Tab$new(0, 'Rights', c_TK_VIEW, 0,
                     253, -9000, 'OD_tabRights',0,
                     'ibs.obj.user.RightsContainer_01', l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    CALL IBSDEV1.p_Tab$new(0, 'Protocol', c_TK_VIEW, 0,
                     253, -10000, 'OD_tabProtocol', 16777216,
                     'ibs.obj.log.LogView_01', l_tabId);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes5');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createBaseObjTypes5 ()
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
  
  
    -- set the tabs for the object types:
-- body:
    -- BusinessObject
    CALL IBSDEV1.p_Type$addTabs('BusinessObject', '', 'Info', 'References', 'Rights', 'Protocol', '', '', '', '', '', '');
    
    -- Container
    CALL IBSDEV1.p_Type$addTabs('Container', '', 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');
    
    -- references:
    -- Referenz
    CALL IBSDEV1.p_Type$addTabs('Referenz', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
    
    -- ReferenzContainer
    CALL IBSDEV1.p_Type$addTabs('ReferenzContainer', '', 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');
    
    -- menu:
    -- Menu
    CALL IBSDEV1.p_Type$addTabs('Menu', '', 'Info', 'Content', 'References', 'Rights', '', '', '', '', '', '');
    
    -- search:
    -- SearchContainer
    CALL IBSDEV1.p_Type$addTabs('SearchContainer', '', 'Content', '', '', '', '', '', '', '', '', '');
    
    -- user management:
    -- User
    CALL IBSDEV1.p_Type$addTabs('User', '', 'Info', 'References', 'Rights', 'Membership', 'Private', '', '', '', '', '');
    
    -- Group
    CALL IBSDEV1.p_Type$addTabs('Group', '', 'Info', 'Content', 'References', 'Rights', 'Protocol', '', '', '', '', '');
    
    
    -- rights management:
    -- Rights
    CALL IBSDEV1.p_Type$addTabs('Rights', '', 'Info', '', '', '', '', '', '', '', '', '');
    
    -- RightsContainer
    CALL IBSDEV1.p_Type$addTabs('RightsContainer', '', 'Info', 'Content', '', '', '', '', '', '', '', '');
    
    -- domains:
    -- Domain
    CALL IBSDEV1.p_Type$addTabs('Domain', '', 'Info', 'Content', 'Rights', '', '', '', '', '', '', '');

    -- distribution:
    -- Recipient
    CALL IBSDEV1.p_Type$addTabs('Recipient', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- SentObjectContainer
    CALL IBSDEV1.p_Type$addTabs('SentObjectContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- SentObject
    CALL IBSDEV1.p_Type$addTabs('SentObject', '', 'Info', 'References',
                                                    'Rights', 'Recipients', '', '', '', '', '', '');

    -- distribution:
    -- Inbox
    CALL IBSDEV1.p_Type$addTabs('Inbox', '', 'Info', 'Content', '', '', '', '', '', '', '', '');

    -- user management:
    -- Workspace
    CALL IBSDEV1.p_Type$addTabs('Workspace', '', 'Content', 'References',
                                                    'Rights', '', '', '', '', '', '', '');

    -- UserContainer
    CALL IBSDEV1.p_Type$addTabs('UserContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');

    -- currently not available
    -- GroupContainer
    CALL IBSDEV1.p_Type$addTabs('GroupContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- UserAdminContainer
    CALL IBSDEV1.p_Type$addTabs('UserAdminContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- UserProfile
    CALL IBSDEV1.p_Type$addTabs('UserProfile', '', 'Info', 'Rights',
                                                    'Protocol', 'AddressValues', '', '', '', '', '', '');
    
    -- Root
    CALL IBSDEV1.p_Type$addTabs('Root', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- distribution:
    -- ReceivedObject
    CALL IBSDEV1.p_Type$addTabs('ReceivedObject', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- Procotol:
    -- LogContainer
    CALL IBSDEV1.p_Type$addTabs('LogContainer', '', 'Info', 'Content',
                                         'References', 'Rights', 'Filter', '', '', '', '', '');

    -- Document Management:
    -- File
    CALL IBSDEV1.p_Type$addTabs('File', '', 'Info', 'References',
                                                    'Rights', 'Protocol', '', '', '', '', '', '');
    
    -- Url
    CALL IBSDEV1.p_Type$addTabs('Url', '', 'Info', 'References',
                                                    'Rights', 'Protocol', '', '', '', '', '', '');

    -- user:
    -- PersonSearchContainer
    CALL IBSDEV1.p_Type$addTabs('PersonSearchContainer', '', '', '', '', '', '', '', '', '', '', '');
    
    -- layout:
    -- LayoutContainer
    CALL IBSDEV1.p_Type$addTabs('LayoutContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- Layout
    CALL IBSDEV1.p_Type$addTabs('Layout', '', 'Info', 'Rights', '', '', '', '', '', '', '', '');
    
    -- menutabs
    -- MenuTabContainer
    CALL IBSDEV1.p_Type$addTabs('MenuTabContainer', '', 'Info', 'Content',
                            'Rights', '', '', '',
                            '', '', '', '');
    
    
    -- MenuTab
    CALL IBSDEV1.p_Type$addTabs('MenuTab', '', 'Info', 'Rights',
                            '', '', '', '',
                            '', '', '', '');

    CALL IBSDEV1.p_Type$addTabs('QuerySelectContainer', '', 'Info', 'Content',
                            'Rights', '', '', '',
                            '', '', '', '');

    -- ImportScript
    CALL IBSDEV1.p_Type$addTabs('ImportScript', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- Connector
    CALL IBSDEV1.p_Type$addTabs('Connector', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- Translator
    CALL IBSDEV1.p_Type$addTabs('Translator', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- EDITranslator
    CALL IBSDEV1.p_Type$addTabs('EDITranslator', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- XMLViewer
    CALL IBSDEV1.p_Type$addTabs('XMLViewer', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');

    -- Data Interchange
    -- ImportContainer
    CALL IBSDEV1.p_Type$addTabs('ImportContainer', '', 'Info', 'Content',
                                        'References', 'Rights', 'Protocol', '', '', '', '', '');

    -- ExportContainer
    CALL IBSDEV1.p_Type$addTabs('ExportContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');

    -- Data Interchange:
    -- DocumentTemplate
    CALL IBSDEV1.p_Type$addTabs('DocumentTemplate', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');

    -- DocumentTemplateContainer
    CALL IBSDEV1.p_Type$addTabs('DocumentTemplateContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- XMLViewerContainer_01
    CALL IBSDEV1.p_Type$addTabs('XMLViewerContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- Help_01
    CALL IBSDEV1.p_Type$addTabs('Help', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
    
    -- Search
    -- SimpleSearchContainer_01
    CALL IBSDEV1.p_Type$addTabs('SimpleSearchContainer', '', '', '', '', '', '', '', '', '', '', '');
    
    -- QueryExecutive_01
    -- This type does not inherit from container, because it doesn't physically
    -- contain m2 objects, but its java class extends container.
    CALL IBSDEV1.p_Type$addTabs('QueryExecutive', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');

    -- Workflow Management:
    -- Workflow
    CALL IBSDEV1.p_Type$addTabs('Workflow', '', 'Info', '', '', '', '', '', '', '', '', '');

    -- WorkflowTemplate
    CALL IBSDEV1.p_Type$addTabs('WorkflowTemplate', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');

    -- WorkflowTemplateContainer
    CALL IBSDEV1.p_Type$addTabs('WorkflowTemplateContainer', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');

    -- ServicePoint_01
    -- inherits from XMLViewerContainer_01
    CALL IBSDEV1.p_Type$addTabs('ServicePoint', '', 'Info', 'Content',
                                                    'References', 'Rights', '', '', '', '', '', '');
    
    -- StateContainer_01
    CALL IBSDEV1.p_Type$addTabs('StateContainer', '', 'Info', 'Content',
                                                    'Rights', '', '', '', '', '', '', '');

    -- documents:
    -- Note
    CALL IBSDEV1.p_Type$addTabs('Note', '', 'Info', 'References',
                                                    'Rights', '', '', '', '', '', '', '');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes6');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createBaseObjTypes6 ()
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
    -- set default tabs for all types which don't have default tabs:
    UPDATE  IBSDEV1.ibs_TVersion
    SET     defaultTab =
            (
                SELECT  COALESCE (MIN (cId), 0)
                FROM    (
                            SELECT  tVersionId AS cTVersionId, id AS cId,
                                    priority AS cPriority
                            FROM    IBSDEV1.ibs_ConsistsOf
                        ) c
                WHERE   cPriority =
                        (
                            SELECT  MAX (c2Priority)
                            FROM	(
                                        SELECT  tVersionId AS c2TVersionId,
                                                priority AS c2Priority
                                        FROM    IBSDEV1.ibs_ConsistsOf
                                    ) c2
                            WHERE   c2TVersionId = id
                        )
                    AND cTVersionId = id
            )
    WHERE   defaultTab = 0;
END;

--/////////////////////////////////////////////////////////////////////////////
-- ensure that each tVersion has a correct state
--/////////////////////////////////////////////////////////////////////////////
-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes7');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createBaseObjTypes7 ()
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
    UPDATE IBSDEV1.ibs_TVersion
    SET state = 2
    WHERE state = 4;
END;


-- execute procedures:
CALL IBSDEV1.pi_createBaseObjTypes;
CALL IBSDEV1.pi_createBaseObjTypes2;
CALL IBSDEV1.pi_createBaseObjTypes3;
CALL IBSDEV1.pi_createBaseObjTypes4;
CALL IBSDEV1.pi_createBaseObjTypes5;
CALL IBSDEV1.pi_createBaseObjTypes6;
CALL IBSDEV1.pi_createBaseObjTypes7;

-- delete procedures:
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes');
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes2');
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes3');
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes4');
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes5');
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes6');
CALL IBSDEV1.p_dropProc ('pi_createBaseObjTypes7');
