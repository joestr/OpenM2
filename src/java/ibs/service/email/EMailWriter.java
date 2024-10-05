/*
 * Class: EMailWriter.java
 */

// package:
package ibs.service.email;

// imports:
import ibs.BaseObject;
import ibs.bo.BOPathConstants;
import ibs.di.DIConstants;
import ibs.io.session.ApplicationInfo;
import ibs.util.file.FileHelpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/******************************************************************************
 * An EMailWriter object handles the connection and the writing process with
 * the mail server. <BR/>
 * The email addresses of the receivers are checked for correctness.
 *
 * @version     $Id: EMailWriter.java,v 1.23 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Monika Eisenkolb (ME)
 ******************************************************************************
 */
public class EMailWriter extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EMailWriter.java,v 1.23 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * The mail server. <BR/>
     */
    protected IOutgoingMailServer p_mailServer;


    /**
     * HTTP Code: Response OK. <BR/>
     */
    private final int HTTP_OK = 200;


    /**************************************************************************
     * Creates an EMailWriter object and sets the server name. <BR/>
     *
     * @param   sname   name of the mail server.
     */
    public EMailWriter (IOutgoingMailServer mailServer)
    {
        // set the mail server
        this.setMailServer (mailServer);
    } // EMailWriter
    
    
    /**************************************************************************
     * Set the email server. <BR/>
     *
     * @param   value   mail server name.
     */
    public void setMailServer (IOutgoingMailServer value)
    {
        this.p_mailServer = value;
    } // setMailServer


    /**************************************************************************
     * Get the email server. <BR/>
     *
     * @return  The server name.
     */
    public IOutgoingMailServer getMailServer ()
    {
        return this.p_mailServer;
    } // getMailServer  


    /**************************************************************************
     * An email is sent using the JavaMail classes. <BR/>
     *
     * @param   mail    E-mail to be sended.
     *
     * @throws  AddressException
     *          wrong emailaddress of a receiver.
     * @throws  MessagingException
     *          A technical error occurred.
     */
    public void sendMail (EMail mail)
        throws AddressException, MessagingException
    {
        // if there are more receivers:
        StringTokenizer st = null;
        Vector<String> receivers = new Vector<String> ();

        // add all receivers to the vector:
        st = new StringTokenizer (mail.getReceiver ().trim () +
            ";" + mail.getCCReceiver ().trim (), ";", false);
        while (st.hasMoreTokens ())
        {
            receivers.addElement (st.nextToken ().trim ());
        } // while

        // get the session from the mail server
        Session session = this.p_mailServer.getSession ();

        // create a message:
        MimeMessage msg = new MimeMessage (session);

        // set the sender:
        msg.setFrom (new InternetAddress (mail.getSender ()));

        // set all receivers:
        for (Iterator<String> iter = receivers.iterator (); iter.hasNext ();)
        {
            InternetAddress[] address =
            {
                new InternetAddress (iter.next ()),
            };
            msg.addRecipients (Message.RecipientType.TO, address);
        } // for iter

        // set the subject:
        msg.setSubject (mail.getSubject (), DIConstants.CHARACTER_ENCODING);
        // create and fill the first message part:
        MimeBodyPart mbp1 = new MimeBodyPart ();
        // set the content with the correct character set:
        mbp1.setText (mail.getContent (), DIConstants.CHARACTER_ENCODING);
        // check if an alternative content type has been set
        // else the mimetype of the email will be set to "text/plain"
        if (mail.getContentType ().length () > 0)
        {
            mbp1.setHeader ("Content-Type", mail.getContentType () + "; charset=" + DIConstants.CHARACTER_ENCODING);
        } // if (mail.getContentType () != null && (!mail.getContentType().length () == 0))
        
        // create the Multipart and its parts to it
        Multipart mp = new MimeMultipart ();
        mp.addBodyPart (mbp1);

        // add the attachments:
        if (mail.attachments != null)   // there are attachments?
        {
            for (Iterator<File> iter = mail.attachments.iterator (); iter.hasNext ();)
            {
                // create the next message part:
                MimeBodyPart mbp2 = new MimeBodyPart ();
                // attach the file to the message:
                FileDataSource fds = new FileDataSource (iter.next ());
                mbp2.setDataHandler (new DataHandler (fds));
                mbp2.setFileName (fds.getName ());
                // create the part to the Multipart:
                mp.addBodyPart (mbp2);
            } // for iter
        } // if there are attachments

        // add the Multipart to the message:
        msg.setContent (mp);
        // set the Date: header
        msg.setSentDate (new Date ());

        // send the message:
        this.p_mailServer.sendEmail (msg);
    } // sendMail

    /**************************************************************************
     * An email is sent using the JavaMail classes. <BR/>
     *
     * @param   mail    E-mail to be sended.
     * @param   app     Current ApplicationInfo object.
     *
     * @throws  AddressException
     *          wrong emailaddress of a receiver.
     * @throws  MessagingException
     *          A technical error occurred.
     * @throws  MalformedURLException
     *          wrong url.
     * @throws  IOException
     *          Sending was not possible.
     */
    public void sendReportAttachedEmail (EMail mail, ApplicationInfo app)
        throws AddressException, MessagingException, MalformedURLException,
        IOException
    {
        // List with temporary files for being able to delete them afterwards
        ArrayList<File> tempAttachmentFiles = new ArrayList<File> ();

        // if there are more receivers:
        StringTokenizer st = null;
        Vector<String> receivers = new Vector<String> ();

        // add all receivers to the vector:
        st = new StringTokenizer (mail.getReceiver ().trim () +
            ";" + mail.getCCReceiver ().trim (), ";", false);
        while (st.hasMoreTokens ())
        {
            receivers.addElement (st.nextToken ().trim ());
        } // while

        // get the session from the mail server
        Session session = this.p_mailServer.getSession ();

        // create a message:
        MimeMessage msg = new MimeMessage (session);

        // set the sender:
        msg.setFrom (new InternetAddress (mail.getSender ()));

        // set all receivers:
        for (Iterator<String> iter = receivers.iterator (); iter.hasNext ();)
        {
            InternetAddress[] address =
            {
                new InternetAddress (iter.next ()),
            };
            msg.addRecipients (Message.RecipientType.TO, address);
        } // for iter

        // set the subject:
        msg.setSubject (mail.getSubject (), DIConstants.CHARACTER_ENCODING);
        // create and fill the first message part:
        MimeBodyPart mbp1 = new MimeBodyPart ();
        // set the content with the correct character set:
        mbp1.setText (mail.getContent (), DIConstants.CHARACTER_ENCODING);
        // check if an alternative content type has been set
        // else the mimetype of the email will be set to "text/plain"
        if (mail.getContentType ().length () > 0)
        {
            mbp1.setHeader ("Content-Type", mail.getContentType () + "; charset=" + DIConstants.CHARACTER_ENCODING);
        } // if (mail.getContentType () != null && (!mail.getContentType().length () == 0))

        // create the Multipart and its parts to it
        Multipart mp = new MimeMultipart ();
        mp.addBodyPart (mbp1);

        // add the attachments:
        if (mail.attachments != null)   // there are attachments?
        {
            for (Iterator<File> iter = mail.attachments.iterator (); iter.hasNext ();)
            {
                // create the next message part:
                MimeBodyPart mbp2 = new MimeBodyPart ();
                // attach the file to the message:
                FileDataSource fds = new FileDataSource (iter.next ());
                mbp2.setDataHandler (new DataHandler (fds));
                mbp2.setFileName (fds.getName ());
                // create the part to the Multipart:
                mp.addBodyPart (mbp2);
            } // for iter
        } // if there are attachments

        // add birt attachments to email:
        if (mail.reportAttachments != null)   // there are attachments?
        {
            for (Iterator<String []> iter = mail.reportAttachments.iterator (); iter.hasNext ();)
            {
                String[] fileInfo = iter.next ();

                URL url = new URL (fileInfo[0]);

                // Open a HTTP Connection to POST the report request to the birt redirect jsp
                HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection ();

                // Set the request method to POST
                conn.setRequestMethod ("POST");
                conn.setUseCaches (false);
                conn.setDoOutput (true);
                conn.setInstanceFollowRedirects (true);

                // Open an output stream to write to the connection
                OutputStreamWriter outStream = new OutputStreamWriter (
                    new BufferedOutputStream (conn.getOutputStream ()));

                // Write the report request params to the streams
                outStream.write (fileInfo[1]);

                // Close the stream
                outStream.close ();

                // get http response
                int httpResponse = conn.getResponseCode ();

                // check if response is valid
                if (httpResponse != this.HTTP_OK)
                {
                    throw new IOException (
                        "HTTP response code: " + httpResponse);
                } // not authorized

                // set file name and extension
                String filename = fileInfo[2] + "." + fileInfo[3];

                String basePath = app.p_system.p_m2AbsBasePath;

                // Get the parent directory
                basePath = (new File (basePath)).getParent ();

                // Get a temporary folder
                basePath += File.separator + BOPathConstants.PATH_TEMP;

                if (FileHelpers.makeDir (basePath, true))
                {
                    // create new temporary file
                    File f = new File (basePath + File.separator +
                        FileHelpers.getUniqueFileName (basePath, filename));

                    // add the file to the list for being to remove it afterwards
                    tempAttachmentFiles.add (f);

                    // get output stream to the temporary file
                    FileOutputStream fos = new FileOutputStream (f);

                    // get the input stream from the HTTP connection
                    InputStream in = conn.getInputStream ();

                    // copy the connection from the connection to the file
                    byte[] buf = new byte[4096];
                    int len;

                    while ((len = in.read (buf)) > 0)
                    {
                        fos.write (buf, 0, len);
                    } // while

                    // close stream from connection
                    in.close ();

                    // close stream to file
                    fos.flush ();
                    fos.close ();

                    // create new file data source
                    FileDataSource fds = new FileDataSource (f);

                    // create new MimeBodyPart
                    MimeBodyPart mbp2 = new MimeBodyPart ();

                    // set data handler
                    mbp2.setDataHandler (new DataHandler (fds));

                    // set file name
                    mbp2.setFileName (filename);

                    // create the part to the Multipart:
                    mp.addBodyPart (mbp2);
                } // if (FileHelpers.makeDir (this.path))

                conn.disconnect ();
            } // for iter
        } // if there are birt attachments

        // add the Multipart to the message:
        msg.setContent (mp);
        // set the Date: header
        msg.setSentDate (new Date ());

        // send the message:
        this.p_mailServer.sendEmail (msg);

        // remove temporary files
        Iterator<File> it = tempAttachmentFiles.iterator ();
        while (it.hasNext ())
        {
            it.next ().delete ();
        } // while
    } // sendMail
} // class EMailWriter
