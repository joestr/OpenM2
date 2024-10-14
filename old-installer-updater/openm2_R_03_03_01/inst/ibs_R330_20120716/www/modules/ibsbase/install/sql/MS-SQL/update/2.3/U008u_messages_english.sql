/******************************************************************************
 * Update the messages. <BR>
 *
 * @version     $Id: U008u_messages_english.sql,v 1.2 2004/05/17 19:57:26 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  040425
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

-- delete old messages:
DELETE  ibs_Message_01
WHERE   languageId = 0
    AND name = 'MSG_IMPORTSUCCESSFULL'
    AND classname = 'ibs.di.DIMessages'
GO

EXEC p_Message_01$new 0,'MSG_NAMEALREADYGIVEN', 'Object with the same name already exists: "<name>".', 'ibs.bo.BOMessages'
EXEC p_Message_01$new 0,'MSG_IMPORTSUCCESSFUL', 'Import completed successfully.', 'ibs.di.DIMessages'
EXEC p_Message_01$new 0,'MSG_DOMAINHOMEPAGEPATH_DESCRIPTION', 'This homepage URL describes part of the URL in order to define the domain. During runtime, the browser checks this part of the URL. In case this field is left empty during configuration, this domain can only be selected when logging on to m2 if there is no other domain whose homepage URL is contained by the browser address you have entered. <BR> In case a homepage URL is entered in this field during configuration, this domain can only be selected when logging on to m2 if the browser adress you have entered contains this homepage URL.', 'ibs.bo.BOMessages'
EXEC p_Message_01$new 0,'MSG_NORECEIVER', 'not defined', 'ibs.service.email.EMailMessages'
EXEC p_Message_01$new 0,'MSG_INCORRECTRECEIVER', 'not defined', 'ibs.service.email.EMailMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONFAILED', 'It was not possible to perform the notification.', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_EMAILSMSSUBJECT', 'm2-notification-', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONSUBJECT', 'subject:', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONREQUEST', 'request:', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONREMARK', 'remark:', 'ibs.service.notification.NotificationMessages'
GO

PRINT 'U008u: english messages updated.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
