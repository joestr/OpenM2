--------------------------------------------------------------------------------
-- Create all entries of procedures in the framework. <BR>
--
-- @version     $Id: createTVersionProc.sql,v 1.5 2003/10/21 22:14:45 klaus Exp $
--
-- @author      Marcel Samek (MS)  020921
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pi_createTVersionProc ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';
    
    -- BusinessObject
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_create, 'p_Object$create');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_change, 'p_Object$change');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_move, 'p_Object$move');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_changeState, 'p_Object$changeState');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_changeProcessState, 'p_Object$changeProcessState');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_changeOwner, 'p_Object$changeOwnerRec');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_retrieve, 'p_Object$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_checkOut, 'p_Object$checkOut');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_InsertProtocol, 'p_Object$InsertProtocol');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_checkIn, 'p_Object$checkIn');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_delete, 'p_Object$delete');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_deleteRec, 'p_Object$delete');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_undelete, 'p_Object$undelete');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_undeleteRec, 'p_Object$undelete');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_deleteAllRefs, 'p_Object$deleteAllRefs');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_copy, 'p_Object$copy');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_getUpper, 'p_Object$getUpperOid');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_getTab, 'p_Object$getTabInfo');
    CALL IBSDEV1.p_TVersionProc$new('BusinessObject', c_PC_getMaster, 'p_Object$getMasterOid');
  
    -- Connector
    CALL IBSDEV1.p_TVersionProc$new('Connector', c_PC_create, 'p_Connector_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Connector', c_PC_retrieve, 'p_Connector_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Connector', c_PC_delete, 'p_Connector_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Connector', c_PC_change, 'p_Connector_01$change');
  
    -- DocumentTemplate
    CALL IBSDEV1.p_TVersionProc$new('DocumentTemplate', c_PC_create, 'p_DocumentTemplate_01$create');
    CALL IBSDEV1.p_TVersionProc$new('DocumentTemplate', c_PC_change, 'p_DocumentTemplate_01$change');
    CALL IBSDEV1.p_TVersionProc$new('DocumentTemplate', c_PC_retrieve, 'p_DocumentTemplate_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('DocumentTemplate', c_PC_delete, 'p_DocumentTemplate_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('DocumentTemplate', c_PC_deleteRec, 'p_DocumentTemplate_01$delete');
  
    -- FileConnector
    CALL IBSDEV1.p_TVersionProc$new('FileConnector', c_PC_create, 'p_Connector_01$create');
    CALL IBSDEV1.p_TVersionProc$new('FileConnector', c_PC_retrieve, 'p_Connector_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('FileConnector', c_PC_delete, 'p_Connector_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('FileConnector', c_PC_change, 'p_Connector_01$change');
  
    -- HTTPScriptConnector
    CALL IBSDEV1.p_TVersionProc$new('HTTPScriptConnector', c_PC_create, 'p_Connector_01$create');
    CALL IBSDEV1.p_TVersionProc$new('HTTPScriptConnector', c_PC_retrieve, 'p_Connector_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('HTTPScriptConnector', c_PC_delete, 'p_Connector_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('HTTPScriptConnector', c_PC_change, 'p_Connector_01$change');
  
    -- MailConnector
    CALL IBSDEV1.p_TVersionProc$new('MailConnector', c_PC_create, 'p_Connector_01$create');
    CALL IBSDEV1.p_TVersionProc$new('MailConnector', c_PC_retrieve, 'p_Connector_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('MailConnector', c_PC_delete, 'p_Connector_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('MailConnector', c_PC_change, 'p_Connector_01$change');
  
    
    -- XMLViewer
    CALL IBSDEV1.p_TVersionProc$new('XMLViewer', c_PC_create, 'p_XMLViewer_01$create');
    CALL IBSDEV1.p_TVersionProc$new('XMLViewer', c_PC_retrieve, 'p_XMLViewer_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('XMLViewer', c_PC_delete, 'p_XMLViewer_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('XMLViewer', c_PC_change, 'p_XMLViewer_01$change');
  
    -- XMLViewerContainer
    CALL IBSDEV1.p_TVersionProc$new('XMLViewerContainer', c_PC_create, 'p_XMLViewerContainer_01$create');
    CALL IBSDEV1.p_TVersionProc$new('XMLViewerContainer', c_PC_retrieve, 'p_XMLViewerContainer_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('XMLViewerContainer', c_PC_delete, 'p_XMLViewerContainer_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('XMLViewerContainer', c_PC_change, 'p_XMLViewerContainer_01$change');
  
    -- ReceivedObject
    CALL IBSDEV1.p_TVersionProc$new('ReceivedObject', c_PC_create, 'p_ReceivedObject_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ReceivedObject', c_PC_retrieve, 'p_ReceivedObject_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ReceivedObject', c_PC_delete, 'p_ReceivedObject_01$delete');    
    CALL IBSDEV1.p_TVersionProc$new('ReceivedObject', c_PC_change, 'p_ReceivedObject_01$change');
  
    
    -- Recipient
    CALL IBSDEV1.p_TVersionProc$new('Recipient', c_PC_create, 'p_Recipient_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Recipient', c_PC_retrieve, 'p_Recipient_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Recipient', c_PC_delete, 'p_Recipient_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Recipient', c_PC_change, 'p_Recipient_01$change');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc2');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createTVersionProc2 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';
    
    -- SentObject
    CALL IBSDEV1.p_TVersionProc$new('SentObject', c_PC_create, 'p_SentObject_01$create');
    CALL IBSDEV1.p_TVersionProc$new('SentObject', c_PC_retrieve, 'p_SentObject_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('SentObject', c_PC_delete, 'p_SentObject_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('SentObject', c_PC_change, 'p_SentObject_01$change');
  
    -- Domain
    CALL IBSDEV1.p_TVersionProc$new('Domain', c_PC_create, 'p_Domain_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Domain', c_PC_retrieve, 'p_Domain_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Domain', c_PC_delete, 'p_Domain_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Domain', c_PC_change, 'p_Domain_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Domain', c_PC_deleteRec, 'p_Domain_01$delete');
  
    
    -- DomainScheme
    CALL IBSDEV1.p_TVersionProc$new('DomainScheme', c_PC_create, 'p_DomainScheme_01$create');
    CALL IBSDEV1.p_TVersionProc$new('DomainScheme', c_PC_retrieve, 'p_DomainScheme_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('DomainScheme', c_PC_delete, 'p_DomainScheme_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('DomainScheme', c_PC_change, 'p_DomainScheme_01$change');
    CALL IBSDEV1.p_TVersionProc$new('DomainScheme', c_PC_deleteRec, 'p_DomainScheme_01$delete');
  
    
    -- Attachment
    CALL IBSDEV1.p_TVersionProc$new('Attachment', c_PC_create, 'p_Attachment_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Attachment', c_PC_retrieve, 'p_Attachment_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Attachment', c_PC_delete, 'p_Attachment_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Attachment', c_PC_change, 'p_Attachment_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Attachment', c_PC_deleteRec, 'p_Attachment_01$delete');
  
    
    -- AttachmentContainer
    CALL IBSDEV1.p_TVersionProc$new('AttachmentContainer', c_PC_create, 'p_AC_01$create');    
    CALL IBSDEV1.p_TVersionProc$new('AttachmentContainer', c_PC_retrieve, 'p_AC_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('AttachmentContainer', c_PC_delete, 'p_AC_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('AttachmentContainer', c_PC_change, 'p_AC_01$change');
  
    
    -- Help
    CALL IBSDEV1.p_TVersionProc$new('Help', c_PC_create, 'p_Help_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Help', c_PC_retrieve, 'p_Help_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Help', c_PC_delete, 'p_Help_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Help', c_PC_change, 'p_Help_01$change');
  
    -- Layout
    CALL IBSDEV1.p_TVersionProc$new('Layout', c_PC_create, 'p_Layout_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Layout', c_PC_retrieve, 'p_Layout_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Layout', c_PC_delete, 'p_Layout_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Layout', c_PC_change, 'p_Layout_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Layout', c_PC_copy, 'p_Layout_01$copy');
  
    
    -- QueryCreator
    CALL IBSDEV1.p_TVersionProc$new('QueryCreator', c_PC_create, 'p_QueryCreator_01$create');
    CALL IBSDEV1.p_TVersionProc$new('QueryCreator', c_PC_retrieve, 'p_QueryCreator_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('QueryCreator', c_PC_change, 'p_QueryCreator_01$change');
  
    
    -- DBQueryCreator
    CALL IBSDEV1.p_TVersionProc$new('DBQueryCreator', c_PC_create, 'p_DBQueryCreator_01$create');
    CALL IBSDEV1.p_TVersionProc$new('DBQueryCreator', c_PC_retrieve, 'p_DBQueryCreator_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('DBQueryCreator', c_PC_change, 'p_DBQueryCreator_01$change');
    CALL IBSDEV1.p_TVersionProc$new('DBQueryCreator', c_PC_copy, 'p_DBQueryCreator_01$BOCopy');
  
    
    -- QueryExecutive
    CALL IBSDEV1.p_TVersionProc$new('QueryExecutive', c_PC_create, 'p_QueryExecutive_01$create');
    CALL IBSDEV1.p_TVersionProc$new('QueryExecutive', c_PC_retrieve, 'p_QueryExecutive_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('QueryExecutive', c_PC_change, 'p_QueryExecutive_01$change');
  
    
    -- Referenz
    CALL IBSDEV1.p_TVersionProc$new('Referenz', c_PC_create, 'p_Referenz_01$create');
END;  

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc3');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createTVersionProc3 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';
            
    -- Rights
    CALL IBSDEV1.p_TVersionProc$new('Rights', c_PC_create, 'p_Rights_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Rights', c_PC_retrieve, 'p_Rights_01$retrieve');    
    CALL IBSDEV1.p_TVersionProc$new('Rights', c_PC_delete, 'p_Rights_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Rights', c_PC_deleteRec, 'p_Rights_01$deleteRightsRec');
    CALL IBSDEV1.p_TVersionProc$new('Rights', c_PC_change, 'p_Rights_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Rights', c_PC_getUpper, 'p_Rights_01$getUpperOid');
  
    
    -- RightsContainer
    CALL IBSDEV1.p_TVersionProc$new('RightsContainer', c_PC_retrieve, 'p_RightsContainer_01$retrieve');
  
    -- UserProfile
    CALL IBSDEV1.p_TVersionProc$new('UserProfile', c_PC_create, 'p_UserProfile_01$create');
    CALL IBSDEV1.p_TVersionProc$new('UserProfile', c_PC_retrieve, 'p_UserProfile_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('UserProfile', c_PC_delete, 'p_UserProfile_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('UserProfile', c_PC_change, 'p_UserProfile_01$change');
  
    
    -- UserAddress
    CALL IBSDEV1.p_TVersionProc$new('UserAddress', c_PC_create, 'p_UserAddress_01$create');
    CALL IBSDEV1.p_TVersionProc$new('UserAddress', c_PC_retrieve, 'p_UserAddress_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('UserAddress', c_PC_delete, 'p_UserAddress_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('UserAddress', c_PC_change, 'p_UserAddress_01$change');
  
    -- Workspace
    CALL IBSDEV1.p_TVersionProc$new('Workspace', c_PC_create, 'p_Workspace_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Workspace', c_PC_retrieve, 'p_Workspace_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Workspace', c_PC_change, 'p_Workspace_01$change');
  
    
    -- p_Group
    CALL IBSDEV1.p_TVersionProc$new('Group', c_PC_create, 'p_Group_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Group', c_PC_retrieve, 'p_Group_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Group', c_PC_delete, 'p_Group_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Group', c_PC_deleteRec, 'p_Group_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Group', c_PC_change, 'p_Group_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Group', c_PC_changeState, 'p_Group_01$changeState');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc4');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createTVersionProc4 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';  
    
    -- User
    CALL IBSDEV1.p_TVersionProc$new('User', c_PC_create, 'p_User_01$create');
    CALL IBSDEV1.p_TVersionProc$new('User', c_PC_retrieve, 'p_User_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('User', c_PC_delete, 'p_User_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('User', c_PC_deleteRec, 'p_User_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('User', c_PC_change, 'p_User_01$change');
    CALL IBSDEV1.p_TVersionProc$new('User', c_PC_changeState, 'p_User_01$changeState');
    CALL IBSDEV1.p_TVersionProc$new('User', c_PC_getNotificationData,
                              'p_User_01$getNotificationData');
  
    -- UserAdminContainer
    CALL IBSDEV1.p_TVersionProc$new('UserAdminContainer', c_PC_create, 'p_UserAdminContainer_01$create');
  
    
    -- Workflow
    CALL IBSDEV1.p_TVersionProc$new('Workflow', c_PC_create, 'p_Workflow_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Workflow', c_PC_retrieve, 'p_Workflow_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Workflow', c_PC_delete, 'p_Workflow_01$delete');
    CALL IBSDEV1.p_TVersionProc$new('Workflow', c_PC_change, 'p_Workflow_01$change');
END;

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc5');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createTVersionProc5 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;

    -- constants:
    DECLARE c_languageId    INT;            -- the current language
    DECLARE c_PC_create     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for create
    DECLARE c_PC_retrieve   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for retrieve
    DECLARE c_PC_change     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for change
    DECLARE c_PC_copy       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for copy
    DECLARE c_PC_delete     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for delete
    DECLARE c_PC_deleteRec  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for 
                                            -- recursive delete
    DECLARE c_PC_move       VARCHAR (63);   -- PROCEDURE IBSDEV1.code for move
    DECLARE c_PC_changeState VARCHAR (63);  -- PROCEDURE IBSDEV1.code for forchangeState
    DECLARE c_PC_changeProcessState VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for
                                            -- changeProcessState
    DECLARE c_PC_changeOwner VARCHAR (63);  -- PROCEDURE IBSDEV1.code for changeOwner
    DECLARE c_PC_checkOut   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkOut
    DECLARE c_PC_InsertProtocol VARCHAR (63);
                                            -- PROCEDURE IBSDEV1.code for InsertProtocol
    DECLARE c_PC_checkIn    VARCHAR (63);   -- PROCEDURE IBSDEV1.code for checkIn
    DECLARE c_PC_undelete   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for undelete
    DECLARE c_PC_undeleteRec VARCHAR (63);  -- PROCEDURE IBSDEV1.code for 
                                            -- recursive undelete
    DECLARE c_PC_deleteAllRefs VARCHAR (63);-- PROCEDURE IBSDEV1.code for deleteAllRefs
    DECLARE c_PC_getUpper   VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getUpper
    DECLARE c_PC_getTab     VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getTab
    DECLARE c_PC_getMaster  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for getMaster
    DECLARE c_PC_createQty  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createQty
    DECLARE c_PC_createVal  VARCHAR (63);   -- PROCEDURE IBSDEV1.code for createVal
    DECLARE c_PC_getNotificationData VARCHAR (63);
    DECLARE l_sqlcode 	    INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- PROCEDURE IBSDEV1.code for getNotificationData
    
    -- local variables:
    -- initializations:
  
        SET c_languageId = 0;
        SET c_PC_create = 'create';
        SET c_PC_retrieve = 'retrieve';
        SET c_PC_change = 'change';
        SET c_PC_copy = 'copy';
        SET c_PC_delete = 'delete';
        SET c_PC_deleteRec = 'deleteRec';
        SET c_PC_move = 'move';
        SET c_PC_changeState = 'changeState';
        SET c_PC_changeProcessState = 'changeProcessState';
        SET c_PC_changeOwner = 'changeOwner';
        SET c_PC_checkOut = 'checkOut';
        SET c_PC_InsertProtocol = 'insertProtocol';
        SET c_PC_checkIn = 'checkIn';
        SET c_PC_undelete = 'undelete';
        SET c_PC_undeleteRec = 'undeleteRec';
        SET c_PC_deleteAllRefs = 'deleteAllRefs';
        SET c_PC_getUpper = 'getUpper';
        SET c_PC_getTab = 'getTab';
        SET c_PC_getMaster = 'getMaster';
        SET c_PC_createQty = 'createQty';
        SET c_PC_createVal = 'createVal';
        SET c_PC_getNotificationData = 'getNotificationData';  

    -- Translator
    CALL IBSDEV1.p_TVersionProc$new('Translator', c_PC_create, 'p_Translator_01$create');
    CALL IBSDEV1.p_TVersionProc$new('Translator', c_PC_retrieve, 'p_Translator_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('Translator', c_PC_change, 'p_Translator_01$change');
    CALL IBSDEV1.p_TVersionProc$new('Translator', c_PC_copy, 'p_Translator_01$BOCopy');
  
    -- ASCIITranslator
    CALL IBSDEV1.p_TVersionProc$new('ASCIITranslator', c_PC_create, 'p_ASCIITranslator_01$create');
    CALL IBSDEV1.p_TVersionProc$new('ASCIITranslator', c_PC_retrieve, 'p_ASCIITranslator_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('ASCIITranslator', c_PC_change, 'p_ASCIITranslator_01$change');
    CALL IBSDEV1.p_TVersionProc$new('ASCIITranslator', c_PC_copy, 'p_ASCIITranslator_01$BOCopy');
  
    -- EDITranslator
    CALL IBSDEV1.p_TVersionProc$new('EDITranslator', c_PC_create, 'p_EDITranslator_01$create');
    CALL IBSDEV1.p_TVersionProc$new('EDITranslator', c_PC_retrieve, 'p_EDITranslator_01$retrieve');
    CALL IBSDEV1.p_TVersionProc$new('EDITranslator', c_PC_change, 'p_EDITranslator_01$change');
    CALL IBSDEV1.p_TVersionProc$new('EDITranslator', c_PC_copy, 'p_EDITranslator_01$BOCopy');
END;

-- execute procedures:
CALL IBSDEV1.pi_createTVersionProc;
CALL IBSDEV1.pi_createTVersionProc2;
CALL IBSDEV1.pi_createTVersionProc3;
CALL IBSDEV1.pi_createTVersionProc4;
CALL IBSDEV1.pi_createTVersionProc5;

-- delete procedures:
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc');
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc2');
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc3');
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc4');
CALL IBSDEV1.p_dropProc ('pi_createTVersionProc5');
