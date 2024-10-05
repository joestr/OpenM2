/*
 * Class: EMailManager.java
 */

// package:
package ibs.service.email;

// imports:
import ibs.BaseObject;
import ibs.io.session.ApplicationInfo;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


/******************************************************************************
 * An EMailManager object. <BR/>
 *
 * @version     $Id: EMailManager.java,v 1.12 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Monika Eisenkolb (ME)
 ******************************************************************************
 */
public abstract class EMailManager extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EMailManager.java,v 1.12 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * The standard mail server. <BR/>
     */
    protected static IOutgoingMailServer standardMailServer;


    /**************************************************************************
     * Set the standard mail server. <BR/>
     *
     * @param   value   mail server.
     */
    public static void setStandardMailServer (IOutgoingMailServer mailServer)
    {
        standardMailServer = mailServer;
    } // setStandardMailServer


    /**************************************************************************
     * This method creates a new EMailWriter object and passes on the
     * standard mail server. <BR/>
     * Then the sendMail method of the EMailWriter instance is called.
     * <BR/>
     *
     * @param   mail    Email to send.
     *
     * @throws  AddressException
     *          The email addresses of the receivers are incorrect.
     * @throws  MessagingException
     *          A technical error occurred.
     */
    public static void sendMail (EMail mail)
        throws AddressException, MessagingException
    {
    	EMailManager.sendMail (mail, standardMailServer);
    } // sendMail


    /**************************************************************************
     * This method creates a new EMailWriter object and passes on the provided
     * mail server. <BR/>
     * Then the sendMail method of the EMailWriter instance is called.
     * <BR/>
     *
     * @param   mail    Email to send
     * @param   mailServer  The mail server to use.
     *
     * @throws  AddressException
     *          The email addresses of the receivers are incorrect.
     * @throws  MessagingException
     *          A technical error occurred.
     */
    public static void sendMail (EMail mail, IOutgoingMailServer mailServer)
        throws AddressException, MessagingException
    {
        // The EMailWriter object used for assembling and sending the e-mail
        EMailWriter writer = null;

        // A new EMailWriter object is created. The name mail server is passed on.
        writer = new EMailWriter (mailServer);

        // The sendMail method of the EMailWriter instance is called.
        writer.sendMail (mail);
    } // sendMail


    /**************************************************************************
     * This method creates a new EMailWriter object and passes on the
     * standard mail server. <BR/>
     * Then the sendMail method of the EMailWriter instance is called.
     * <BR/>
     *
     * @param   mail    Email to send.
     * @param   app     Current ApplicationInfo object.
     *
     * @throws  AddressException
     *          The email addresses of the receivers are incorrect.
     * @throws  MessagingException
     *          A technical error occurred.
     * @throws  MalformedURLException
     *          A url error occured.
     * @throws  IOException
     *          Sending of e-mail was not possible.
     */
    public static void sendReportAttachedEmail (EMail mail, ApplicationInfo app)
        throws AddressException, MessagingException, MalformedURLException,
        IOException
    {
        // Send the mail by using the standard mail server
        EMailManager.sendReportAttachedEmail (mail, standardMailServer, app);
    } // sendReportAttachedEmail


    /**************************************************************************
     * This method creates a new EMailWriter object and passes on the provided
     * mail server. <BR/>
     * Then the sendMail method of the EMailWriter instance is called.
     * <BR/>
     *
     * @param   mail    Email to send.
     * @param   mailServer  The mail server to use.
     * @param   app     Current ApplicationInfo object.
     *
     * @throws  AddressException
     *          The email addresses of the receivers are incorrect.
     * @throws  MessagingException
     *          A technical error occurred.
     * @throws  MalformedURLException
     *          A url error occured.
     * @throws  IOException
     *          Sending of e-mail was not possible.
     */
    public static void sendReportAttachedEmail (EMail mail, IOutgoingMailServer mailServer, ApplicationInfo app)
        throws AddressException, MessagingException, MalformedURLException,
        IOException
    {
    	// The EMailWriter object used for assembling and sending the e-mail
    	EMailWriter writer = null;

        // A new EMailWriter object is created. The name mail server is passed on.
        writer = new EMailWriter (mailServer);

        // The sendMailBirtAttached method of the EMailWriter instance is called.
        writer.sendReportAttachedEmail (mail, app);
    } // sendReportAttachedEmail

} // class EMailManager
