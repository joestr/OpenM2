@echo off
rem ***************************************************************************
rem * Perform the database installation for one file.
rem *
rem * @input parameters:
rem * @param    [dbname]        The name of the database.
rem * @param    [displaytype]   The display type:
rem *                           dispno ..... don't display the executed stmts.
rem *                           dispone .... display first line of stmt.
rem *                           dispfull ... display full statement.
rem * @param    [username]      The user for connecting to the database.
rem * @param    [password]      The password for the user.
rem ***************************************************************************

setlocal

call standardStart %0 %*

rem check the syntax:
rem if "%dirName%"=="" goto syntax

rem execute the several scripts:
rem call %binDir%dropstructures     >> "%actLogDir%dropstructures_%fileName%.log"
rem %db% install\dropAllTriggers.sql
rem %db% install\dropAllFunctions.sql
rem %db% install\dropAllProcedures.sql
rem %db% install\dropAllViews.sql
rem %db% install\dropAllTables.sql
rem %db% install\dropAllProcedures.sql

rem call %binDir%systemproc         >> "%actLogDir%systemproc_%fileName%.log"
rem call %binDir%systemproc
rem %db% tables\Catalog_01.sql
rem %db% procedures\Helpers.sql
rem %db% procedures\ErrorProc.sql
rem %db% procedures\Helpers2.sql

rem %db% install\Datatypes.sql     >> "%actLogDir%datatypes_%fileName%.log"

rem %dbexec% tables                >> "%actLogDir%tables_%fileName%.log"
rem %dbexec% tables
rem %db% tables\DocumentTemplate_01.sql
rem %db% tables\Domain_01.sql
rem %db% tables\UserProfile.sql
rem %db% tables\User.sql

rem %db% always\createIndices.sql  >> "%actLogDir%indices_%fileName%.log"
rem %db% always\createIndices.sql

rem call %binDir%views              >> "%actLogDir%views_%fileName%.log"
rem call %binDir%views
rem %db% views\AttachmentContainer_01Views.sql
rem %db% views\Catalog_01Views.sql
rem %db% views\CleanContainerViews.sql
rem %db% views\MayContainViews.sql
rem %db% views\TVersionViews.sql
rem %db% views\UserContainerViews.sql

rem call %binDir%procedures         >> "%actLogDir%procedures_%fileName%.log"
rem call %binDir%procedures
rem %db% procedures\ObjectDesc_01Proc.sql;
rem %db% procedures\Token_01Proc.sql;
rem %db% procedures\Exception_01Proc.sql;
rem %db% procedures\Message_01Proc.sql;
rem %db% procedures\TypeName_01Proc.sql;
rem %db% procedures\KeyMapper_01Proc.sql;
rem %db% procedures\FilePathProc.sql;
rem %db% procedures\MasterDataContainer_01Proc.sql;
rem %db% procedures\OperationProc.sql;
rem %db% procedures\OrderAddress_01Proc.sql;
rem %db% procedures\TabProc.sql;
rem %db% procedures\ConsistsOfProc.sql;
rem %db% procedures\TVersionProcProc.sql;
rem %db% procedures\TVersionProc.sql;
rem %db% procedures\RightsProc.sql;
rem %db% procedures\ObjectReadProc.sql;
rem %db% procedures\Attachment_01Proc1.sql;
%db% procedures\ObjectProc1.sql;
rem %db% procedures\Rights_01Proc.sql;
rem %db% procedures\RightsContainer_01Proc.sql;
rem %db% procedures\UserAddress_01Proc.sql;
rem %db% procedures\UserAdminProc.sql;
rem %db% procedures\UserProfileProc.sql;
rem %db% procedures\GroupUserProc.sql;
rem %db% procedures\WorkspaceProc.sql;
rem %db% procedures\ContainerProc.sql;
rem %db% procedures\GroupProc.sql;
rem %db% procedures\ReferenceProc.sql;
rem %db% procedures\Referenz_01Proc.sql;
rem %db% procedures\MayContainProc.sql;
rem %db% procedures\TypeProc.sql;
rem %db% procedures\UserProc.sql;
rem %db% procedures\Address_01Proc.sql;
rem %db% procedures\AttachmentContainer_01Proc.sql;
rem %db% procedures\Attachment_01Proc2.sql;
rem %db% procedures\Beitrag_01Proc.sql;
rem %db% procedures\BlackBoard_01Proc.sql;
rem %db% procedures\Catalog_01Proc.sql;
rem %db% procedures\Company_01Proc.sql;
rem %db% procedures\Diskussion_01Proc.sql;
rem %db% procedures\Dokument_01Proc.sql;
rem %db% procedures\Help_01Proc.sql;
rem %db% procedures\Layout_01Proc.sql;
rem %db% procedures\LogContainer_01Proc.sql;
rem %db% procedures\Note_01Proc.sql;
rem %db% procedures\Order_01Proc.sql;
rem %db% procedures\ParticipantContainer_01Proc.sql;
rem %db% procedures\Participant_01Proc.sql;
rem %db% procedures\PersonContainer_01Proc.sql;
rem %db% procedures\Person_01Proc.sql; 
rem %db% procedures\Price_01Proc.sql;
rem %db% procedures\ProductBrand_01Proc.sql;
rem %db% procedures\ProductCollection_01Proc.sql;
rem %db% procedures\ProductGroup_01Proc.sql;
rem %db% procedures\ProductGroupProfile_01Proc.sql;
rem %db% procedures\ProductProfile_01Proc.sql;
rem %db% procedures\ProductProperties_01Proc.sql;
rem %db% procedures\Product_01Proc.sql;
rem %db% procedures\ReceivedObject_01Proc.sql;
rem %db% procedures\Recipient_01Proc.sql;
rem %db% procedures\SentObject_01Proc.sql;
rem %db% procedures\Termin_01Proc.sql;
rem %db% procedures\Thema_01Proc.sql;
rem %db% procedures\QueryCreator_01Proc.sql;
rem %db% procedures\DBQueryCreator_01Proc.sql;
rem %db% procedures\QueryExecutive_01Proc.sql;
rem %db% procedures\MenuTab_01Proc.sql;
rem %db% procedures\createBaseQueryCreatorsProc.sql;
rem %db% procedures\Domain_01Proc.sql;
rem %db% procedures\IntegratorProc.sql;
rem %db% procedures\XMLViewer_01Proc.sql;
rem %db% procedures\XMLViewerContainer_01Proc.sql;
rem %db% procedures\DocumentTemplate_01Proc.sql;
rem %db% procedures\Connector_01Proc.sql;
rem %db% procedures\DiscXMLViewer_01Proc.sql;
rem %db% procedures\DiskussionContainer_01Proc.sql;
rem %db% procedures\XMLDiscussion_01Proc.sql;
rem %db% procedures\XMLDiscussionTemplateProc.sql;
rem %db% procedures\Translator_01Proc.sql;
rem %db% procedures\EDITranslator_01Proc.sql;
rem %db% procedures\ASCIITranslator_01Proc.sql;
rem %db% procedures\ObjectProc2.sql;
rem %db% procedures\DomainScheme_01Proc.sql;
rem %db% procedures\SystemProc.sql;
rem %db% procedures\Workflow_01Proc.sql;
rem %db% procedures\WorkflowObjectHandlerProc.sql;
rem %db% procedures\WorkflowRightsHandlerProc.sql;
rem %db% procedures\WorkflowProtocolProc.sql;
rem %db% procedures\WorkflowVariablesProc.sql;
rem %db% procedures\PaymentType_01Proc.sql;
rem %db% procedures\ShoppingCart_01Proc.sql;
rem %db% procedures\ApplicationProc.sql;
rem %db% procedures\CounterProc.sql;

rem %dbexec% triggers              >> "%actLogDir%triggers_%fileName%.log"
rem %dbexec% triggers
rem %db% triggers\Domain_01Trig.sql
rem %db% triggers\RightsKeysTrig.sql
rem %db% triggers\UserTrig.sql


rem echo installing base data...
rem call %binDir%installbase        >> "%actLogDir%installbase_%fileName%.log"
rem %db% install\deleteAllTableContents.sql
rem %db% always\installConfig.sql
rem %db% %commonDir%install\installConfig.sql
rem %db% %appDir%sources\test\setSystemValues.sql
rem %db% always\createOperations.sql
rem %db% %appDir%sources\multilangScripts\createObjectDesc.sql
rem %db% %appDir%sources\test\cleanMultilang.sql
rem %db% multilangScripts\createObjectDesc.sql
rem %db% multilangScripts\createTokens.sql
rem %db% multilangScripts\createExceptions.sql
rem %db% multilangScripts\createMessages.sql
rem %db% multilangScripts\createTypeNames.sql
rem %db% always\createBaseObjectTypes.sql
rem %db% always\createTVersionProc.sql
rem %db% always\createRightsMapping.sql
rem %db% install\createMayContainEntries.sql
rem %db% %commonDir%test\clean.sql
rem %db% install\createBaseData.sql
rem %db% install\createSchemes.sql
rem %db% install\createBaseDomain.sql
rem %db% %appDir%sources\test\countData.sql

rem %db% procedures\DocumentTemplate_01Proc.sql
rem     >> "%actLogDir%datatypes_%fileName%.log"
echo ready.

goto end

:syntax
rem display the syntax:
set errorNum=1

:end
call %binDir%standardEnd

endlocal
