@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting procedures at %dateTime%

rem install basic procedures:
rem install other procedures:
%db% %pkgSourceDir%procedures\ObjectDesc_01Proc.sql;
%db% %pkgSourceDir%procedures\Token_01Proc.sql;
%db% %pkgSourceDir%procedures\Exception_01Proc.sql;
%db% %pkgSourceDir%procedures\Message_01Proc.sql;
%db% %pkgSourceDir%procedures\TypeName_01Proc.sql;
%db% %pkgSourceDir%procedures\KeyMapper_01Proc.sql;
%db% %pkgSourceDir%procedures\FilePathProc.sql;
%db% %pkgSourceDir%procedures\MasterDataContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\OperationProc.sql;
%db% %pkgSourceDir%procedures\OrderAddress_01Proc.sql;
%db% %pkgSourceDir%procedures\TabProc.sql;
%db% %pkgSourceDir%procedures\ConsistsOfProc.sql;
%db% %pkgSourceDir%procedures\TVersionProcProc.sql;
%db% %pkgSourceDir%procedures\TVersionProc.sql;
%db% %pkgSourceDir%procedures\RightsProc.sql;
%db% %pkgSourceDir%procedures\ObjectReadProc.sql;
%db% %pkgSourceDir%procedures\Attachment_01Proc1.sql;
%db% %pkgSourceDir%procedures\ObjectIdProc.sql;
%db% %pkgSourceDir%procedures\ObjectProc1.sql;
%db% %pkgSourceDir%procedures\Rights_01Proc.sql;
%db% %pkgSourceDir%procedures\RightsContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\UserAddress_01Proc.sql;
%db% %pkgSourceDir%procedures\UserAdminProc.sql;
%db% %pkgSourceDir%procedures\UserProfileProc.sql;
%db% %pkgSourceDir%procedures\GroupUserProc.sql;
%db% %pkgSourceDir%procedures\WorkspaceProc.sql;
%db% %pkgSourceDir%procedures\ContainerProc.sql;
%db% %pkgSourceDir%procedures\GroupProc.sql;
%db% %pkgSourceDir%procedures\ReferenceProc.sql;
%db% %pkgSourceDir%procedures\Referenz_01Proc.sql;
%db% %pkgSourceDir%procedures\MayContainProc.sql;
%db% %pkgSourceDir%procedures\TypeProc.sql;
%db% %pkgSourceDir%procedures\UserProc.sql;
%db% %pkgSourceDir%procedures\Address_01Proc.sql;
%db% %pkgSourceDir%procedures\AttachmentContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\Attachment_01Proc2.sql;
%db% %pkgSourceDir%procedures\Beitrag_01Proc.sql;
%db% %pkgSourceDir%procedures\BlackBoard_01Proc.sql;
%db% %pkgSourceDir%procedures\Catalog_01Proc.sql;
%db% %pkgSourceDir%procedures\Company_01Proc.sql;
%db% %pkgSourceDir%procedures\Diskussion_01Proc.sql;
%db% %pkgSourceDir%procedures\Dokument_01Proc.sql;
%db% %pkgSourceDir%procedures\Help_01Proc.sql;
%db% %pkgSourceDir%procedures\Layout_01Proc.sql;
%db% %pkgSourceDir%procedures\Locale_01Proc.sql;
%db% %pkgSourceDir%procedures\LogContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\Note_01Proc.sql;
%db% %pkgSourceDir%procedures\Order_01Proc.sql;
%db% %pkgSourceDir%procedures\ParticipantContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\Participant_01Proc.sql;
%db% %pkgSourceDir%procedures\PersonContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\Person_01Proc.sql; 
%db% %pkgSourceDir%procedures\Price_01Proc.sql;
%db% %pkgSourceDir%procedures\ProductBrand_01Proc.sql;
%db% %pkgSourceDir%procedures\ProductCollection_01Proc.sql;
%db% %pkgSourceDir%procedures\ProductGroup_01Proc.sql;
%db% %pkgSourceDir%procedures\ProductGroupProfile_01Proc.sql;
%db% %pkgSourceDir%procedures\ProductProfile_01Proc.sql;
%db% %pkgSourceDir%procedures\ProductProperties_01Proc.sql;
%db% %pkgSourceDir%procedures\Product_01Proc.sql;
%db% %pkgSourceDir%procedures\ReceivedObject_01Proc.sql;
%db% %pkgSourceDir%procedures\Recipient_01Proc.sql;
%db% %pkgSourceDir%procedures\SentObject_01Proc.sql;
%db% %pkgSourceDir%procedures\Termin_01Proc.sql;
%db% %pkgSourceDir%procedures\Thema_01Proc.sql;
%db% %pkgSourceDir%procedures\QueryCreator_01Proc.sql;
%db% %pkgSourceDir%procedures\DBQueryCreator_01Proc.sql;
%db% %pkgSourceDir%procedures\QueryExecutive_01Proc.sql;
%db% %pkgSourceDir%procedures\MenuTab_01Proc.sql;
%db% %pkgSourceDir%procedures\createBaseQueryCreatorsProc.sql;
%db% %pkgSourceDir%procedures\Domain_01Proc.sql;
%db% %pkgSourceDir%procedures\IntegratorProc.sql;
%db% %pkgSourceDir%procedures\XMLViewer_01Proc.sql;
%db% %pkgSourceDir%procedures\XMLViewerContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\DocumentTemplate_01Proc.sql;
%db% %pkgSourceDir%procedures\Connector_01Proc.sql;
%db% %pkgSourceDir%procedures\DiscXMLViewer_01Proc.sql;
%db% %pkgSourceDir%procedures\DiskussionContainer_01Proc.sql;
%db% %pkgSourceDir%procedures\XMLDiscussion_01Proc.sql;
%db% %pkgSourceDir%procedures\XMLDiscussionTemplateProc.sql;
%db% %pkgSourceDir%procedures\Translator_01Proc.sql;
%db% %pkgSourceDir%procedures\EDITranslator_01Proc.sql;
%db% %pkgSourceDir%procedures\ASCIITranslator_01Proc.sql;
%db% %pkgSourceDir%procedures\ObjectProc2.sql;
%db% %pkgSourceDir%procedures\DomainScheme_01Proc.sql;
%db% %pkgSourceDir%procedures\SystemProc.sql;
%db% %pkgSourceDir%procedures\Workflow_01Proc.sql;
%db% %pkgSourceDir%procedures\WorkflowObjectHandlerProc.sql;
%db% %pkgSourceDir%procedures\WorkflowRightsHandlerProc.sql;
%db% %pkgSourceDir%procedures\WorkflowProtocolProc.sql;
%db% %pkgSourceDir%procedures\WorkflowVariablesProc.sql;
%db% %pkgSourceDir%procedures\PaymentType_01Proc.sql;
%db% %pkgSourceDir%procedures\ShoppingCart_01Proc.sql;
%db% %pkgSourceDir%procedures\ApplicationProc.sql;
%db% %pkgSourceDir%procedures\CounterProc.sql;

%dbexec% procedures

call %bindir%getDateTime
echo finished procedures at %dateTime%

:end
endlocal
