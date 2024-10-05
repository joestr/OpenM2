/*
 * Class: IOutgoingMailServer.java
 */

// package:
package ibs.service.email;

// imports:
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


/******************************************************************************
 * Common interface for outgoing email servers.
 * 
 * @version     $Id: IOutgoingMailServer.java,v 1.1 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann /BT)
 */
public interface IOutgoingMailServer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IOutgoingMailServer.java,v 1.1 2012/09/18 14:47:50 btatzmann Exp $";


    /**************************************************************************
     * Get session for sending emails. <BR/>
     */
    public Session getSession ();


    /***************************************************************************
     * Send the provided message via the mail server.
     *
     * @param msg   the message to send
     * 
     * @throws  MessagingException
     *          A technical error occurred.
     */
    public void sendEmail (MimeMessage msg) throws MessagingException;

} // IOutgoingMailServer