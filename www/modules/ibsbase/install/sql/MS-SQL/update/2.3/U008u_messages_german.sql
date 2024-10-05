/******************************************************************************
 * Update the messages. <BR>
 *
 * @version     $Id: U008u_messages_german.sql,v 1.2 2004/05/17 19:57:26 klaus Exp $
 *
 * @author      Klaus Reim�ller (KR)  040425
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

EXEC p_Message_01$new 0,'MSG_NAMEALREADYGIVEN', 'Es existiert bereits ein Objekt mit demselben Namen: "<name>".', 'ibs.bo.BOMessages'
EXEC p_Message_01$new 0,'MSG_IMPORTSUCCESSFUL', 'Import erfolgreich abgeschlossen.', 'ibs.di.DIMessages'
EXEC p_Message_01$new 0,'MSG_DOMAINHOMEPAGEPATH_DESCRIPTION', 'Dieser Pfad gibt einen Teil der URL an, �ber welche diese Dom�ne aufgerufen werden kann. Zur Laufzeit wird dann �berpr�ft, ob die vom Benutzer eingegebene URL diesen Teil enth�lt. Wenn diese Feld leer gelassen wird, wird diese Dom�ne beim Anmelden nur dann zur Auswahl angezeigt, wenn es keine andere Dom�ne gibt, deren Homepage URL in der in der Browser-Adresse eingegebenen URL enthalten ist. Wird in diesem Feld eine Eintragung vorgenommen, wird diese Dom�ne immer dann beim Anmelden zur Auswahl angeboten, wenn die Homepage URL in der Browser-Adresse enthalten ist.', 'ibs.bo.BOMessages'
EXEC p_Message_01$new 0,'MSG_NORECEIVER', 'Es wurde kein Empf�nger angegeben.', 'ibs.service.email.EMailMessages'
EXEC p_Message_01$new 0,'MSG_INCORRECTRECEIVER', 'Es wurde eine falsche Empf�ngeraddresse angegeben.', 'ibs.service.email.EMailMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONFAILED', 'Die Notifikation konnte nicht durchgef�hrt werden.', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_EMAILSMSSUBJECT', 'm2-Benachrichtigung-', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONSUBJECT', 'Betreff:', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONREQUEST', 'Anforderung:', 'ibs.service.notification.NotificationMessages'
EXEC p_Message_01$new 0,'MSG_NOTIFICATIONREMARK', 'Anmerkung:', 'ibs.service.notification.NotificationMessages'
GO

PRINT 'U008u: german messages updated.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
