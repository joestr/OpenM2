/*
 * Class: SMTPServer.java
 */

// package:
package ibs.service.email;

// imports:
import ibs.BaseObject;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;


/******************************************************************************
 * An SMTPServer object provides the necessary functionality for sending
 * provided emails via an SMTP server including authentication.<BR/>
 *
 * @version     $Id: SMTPServer.java,v 1.1 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT)
 ******************************************************************************
 */
public class SMTPServer extends BaseObject implements IOutgoingMailServer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SMTPServer.java,v 1.1 2012/09/18 14:47:50 btatzmann Exp $";

    /**
     * property defining the email protocol which is used. <BR/>
     */
    public static final String EMAIL_PROTOCOL = "mail.smtp.host";

    /**
     * SMTP authentication property. <BR/>
     */
    private static final Object SMTP_AUTHENTICATION = "mail.smtp.auth";

    /**
     * The name of the mail server. <BR/>
     */
    protected String serverName = "";
    
    /**
     * The name of the account used to send the mails. <BR/>
     */
    protected String accountName;
    
    /**
     * The password of the account used to send the mails. <BR/>
     */
    protected String password;

    
    /**************************************************************************
     * Creates an SMTPServer object without setting attributes. <BR/>
     */
    public SMTPServer ()
    {
        // nothing to do
    } // SMTPServer


    /**************************************************************************
     * Creates an SMTPServer object and sets the server name. <BR/>
     *
     * @param   sname   name of the mail server.
     */
    public SMTPServer (String sname)
    {
        // set the name of the mail server
        this.setServerName (sname);
    } // SMTPServer


    /**************************************************************************
     * Creates an SMTPServer object and sets server name, account name and
     * password. <BR/>
     *
     * @param   sname   name of the mail server
     * @param	aname	name of the mail account
     * @param 	pwd		password of the mail account
     */
    public SMTPServer (String sname, String aname, String pwd)
    {
        // set the name of the mail server
        this.setServerName (sname);
        // set the name of the mail server account
        this.setAccountName(aname);
        // set the password of the mail server account
        this.setPassword(pwd);
    } // SMTPServer
    
    
    /**************************************************************************
     * Set the name of the mail server. <BR/>
     *
     * @param   value   mail server name.
     */
    public void setServerName (String value)
    {
        this.serverName = value;
    } // setServerName
    
    
    /**************************************************************************
     * Set the name of the mail server account. <BR/>
     *
     * @param   value   account name.
     */
    public void setAccountName (String value)
    {
        this.accountName = value;
    } // setAccountName
    
    
    /**************************************************************************
     * Set the password of the mail server account. <BR/>
     *
     * @param   value   password.
     */
    public void setPassword (String value)
    {
        this.password = value;
    } // setPassword


    /**************************************************************************
     * Get the name of the email server. <BR/>
     *
     * @return  The server name.
     */
    public String getServerName ()
    {
        return this.serverName;
    } // getServerName

    
    /**************************************************************************
     * Get the name of the email server account. <BR/>
     *
     * @return  The account name.
     */
    protected String getAccountName ()
    {
        return this.accountName;
    } // getAccountName
    
    
    /**************************************************************************
     * Get the password of the mail server account. <BR/>
     *
     * @return  The account password.
     */
    protected String getPassword ()
    {
        return this.password;
    } // getPassword


    /**************************************************************************
     * Get session for sending emails. <BR/>
     */
    public Session getSession ()
    {
        Session session;
        
        // set properties and get the default Session
        Properties props = new Properties ();
        props.put (SMTPServer.EMAIL_PROTOCOL, this.serverName);

        // check if authentication should be performed
        if (this.accountName != null && !this.accountName.isEmpty () &&
                this.password != null && !this.password.isEmpty ())
        {
            props.put(SMTPServer.SMTP_AUTHENTICATION, "true");
            
            // getInstance must be used instead of getDefaultInstance for being able to use different server configurations in parallel 
            session = Session.getInstance (props, new SMTPAuthenticator(this.accountName, this.password));
        } // if
        else
        {
            // getInstance must be used instead of getDefaultInstance for being able to use different server configurations in parallel
            session = Session.getInstance (props, null);
        } // else

        return session;
    } // getSession

    
    /***************************************************************************
     * Send the provided message via the mail server.
     *
     * @param msg   the message to send
     * 
     * @throws  MessagingException
     *          A technical error occurred.
     */
    public void sendEmail (MimeMessage msg) throws MessagingException
    {
          Transport.send (msg);
    } // sendEmail


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the name are concatenated to create a string
     * representation according to "serverName".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        // compute the string and return it:
        return getServerName ();
    } // toString


    /**************************************************************************
     *  <code>Authenticator</code> implementation to perform
     *  SMTP server authentication via account name and password.
     */
    private class SMTPAuthenticator extends Authenticator
    {
        // the authentication info
        private PasswordAuthentication authentication;

        /**************************************************************************
         * Creates an SMTPAuthenticator by setting the necessary authentication
         * information. <BR/>
         * 
         * @param account   the account name
         * @param password  the password
         */
        public SMTPAuthenticator (String account, String password)
        {
            authentication = new PasswordAuthentication(account, password);
        } // SMTPAuthenticator


        /***************************************************************************
         * Returns the <code>PasswordAuthentication</code> object holding
         * the necessary authentication information.
         *
         * @return the <code>PasswordAuthentication</code> object holding
         * the necessary authentication information.
         * 
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        public PasswordAuthentication getPasswordAuthentication ()
        {
           return authentication;
        } // getPasswordAuthentication
    } // SMTPAuthenticator
} // class SMTPServer