/*
 * Class: MailConnector_01.java
 */

// package:
package ibs.di.connect;

// imports:
//TODO: unsauber
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.Log_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.email.EMail;
import ibs.service.email.EMailManager;
import ibs.service.email.SMTPServer;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.util.file.FileHelpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.AddressException;

import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;


/******************************************************************************
 * The MailConnector_01 Class reads import files as email from a specified
 * email account and sends exportfiles as email to a specific email account.<BR/>
 *
 * @version     $Id: MailConnector_01.java,v 1.25 2012/09/18 14:47:49 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 991008
 ******************************************************************************
 */
public class MailConnector_01 extends Connector_01 implements ConnectorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MailConnector_01.java,v 1.25 2012/09/18 14:47:49 btatzmann Exp $";


    /**
     *  IMAP protocol
     */
    public static final String PROTOCOL_IMAP = "imap";

    /**
     *  POP3 protocol
     */
    public static final String PROTOCOL_POP3 = "pop3";


    /**
     *  protocol of mail server
     */
    public String mailProtocol = MailConnector_01.PROTOCOL_IMAP;

    /**
     *  protocol of mail server
     */
    private String[] mailProtocols =
    {
        MailConnector_01.PROTOCOL_IMAP,
        MailConnector_01.PROTOCOL_POP3,
    }; // mailProtocols

    /**
     *  name of mail server
     */
    public String mailServer = "";

    /**
     *  name of user of the mail account
     */
    public String mailUser = "";

    /**
     *  password of user of the mail account
     */
    public String mailPassword = "";

    /**
     *  sender email adress
     */
    public String mailSender = "";

    /**
     *  recipient email adress
     */
    public String mailRecipient = "";

    /**
     *  additional mail receivers
     */
    public String mailCC = "";

    /**
     *  subject of the email
     */
    public String mailSubject = "";

    /**
     * Folder to read the messages from.<BR/>
     */
    private Folder folder = null;

    /**
     * Store to read the messages from.<BR/>
     */
    private Store store = null;

    /**
     * additional attachments for the export mail.<BR/>
     * Vector that holds java.io.File instances
     * for the files to be attached to the email.<BR/>
     */
    private Vector<File> attachments = new Vector<File> ();

    /**
     * the id of the message that has been read.<BR/>
     * The -1 value indicates that no message has been read.<BR/>
     */
    private int messageId = -1;


    /**************************************************************************
     * Creates a MailConnector_01 Object. <BR/>
     */
    public MailConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // MailConnector_01


    /**************************************************************************
     * Creates a MailConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object
     * @param   user    user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public MailConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // MailConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics:
        this.connectorType = DIConstants.CONNECTORTYPE_EMAIL;

        // set stored procedure names:
        this.procCreate =    "p_Connector_01$create";
        this.procRetrieve =  "p_Connector_01$retrieve";
        this.procDelete =    "p_Connector_01$delete";
        this.procChange =    "p_Connector_01$change";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 12;
        this.specificChangeParameters = 12;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     */
    public void setArguments ()
    {
        this.mailServer = this.arg1;
        this.mailUser = this.arg2;
        this.mailPassword = this.arg3;
        this.mailSender = this.arg4;
        this.mailRecipient = this.arg5;
        this.mailCC = this.arg6;
        this.mailSubject = this.arg7;
        this.mailProtocol = this.arg8;
    } // setArguments


    /**************************************************************************
     * Represent the properties of a DocumentTemplate_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties should be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
        // loop through all properties of this object and display them:
        this.showProperty (table, DIArguments.ARG_ARG1,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSERVER, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG8,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILPROTOCOL, env),
            Datatypes.DT_TEXT, this.arg8);
        this.showProperty (table, DIArguments.ARG_ARG2,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILUSER, env),
            Datatypes.DT_TEXT, this.arg2);
        this.showProperty (table, DIArguments.ARG_ARG3,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILPASSWORD, env),
            Datatypes.DT_PASSWORD, this.arg3);
        this.showProperty (table, DIArguments.ARG_ARG4,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSENDER, env),
            Datatypes.DT_TEXT, this.arg4);
        this.showProperty (table, DIArguments.ARG_ARG5,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILRECIPIENT, env),
            Datatypes.DT_TEXT, this.arg5);
        this.showProperty (table, DIArguments.ARG_ARG6,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILCC, env),
            Datatypes.DT_TEXT, this.arg6);
        this.showProperty (table, DIArguments.ARG_ARG7, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSUBJECT, env),
            Datatypes.DT_TEXT, this.arg7);
    } //  showProperties


    /**************************************************************************
     * Represent the properties of a Connector_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     */
    protected void showFormProperties (TableElement table)
    {
        super.showFormProperties (table);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, DIArguments.ARG_ARG1,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSERVER, env),
            Datatypes.DT_NAME, this.arg1);
        this.showFormProperty (table, DIArguments.ARG_ARG8,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILPROTOCOL, env),
            Datatypes.DT_SELECT, this.arg8,
            this.mailProtocols, this.mailProtocols, 0);
        this.showFormProperty (table, DIArguments.ARG_ARG2,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILUSER, env),
            Datatypes.DT_NAME, this.arg2);
        this.showFormProperty (table, DIArguments.ARG_ARG3,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILPASSWORD, env),
            Datatypes.DT_PASSWORD, this.arg3);
        this.showFormProperty (table, DIArguments.ARG_ARG4,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSENDER, env),
            Datatypes.DT_NAME, this.arg4);
        this.showFormProperty (table, DIArguments.ARG_ARG5,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILRECIPIENT, env),
            Datatypes.DT_NAME, this.arg5);
        this.showFormProperty (table, DIArguments.ARG_ARG6,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILCC, env),
            Datatypes.DT_NAME, this.arg6);
        this.showFormProperty (table, DIArguments.ARG_ARG7,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSUBJECT, env),
            Datatypes.DT_NAME, this.arg7);
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the connector.<BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        super.showSettings (table);
        // display connector specific settings
        this.showProperty (table, DIArguments.ARG_ARG1,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSERVER, env),
            Datatypes.DT_TEXT, this.mailServer);
        this.showProperty (table, DIArguments.ARG_ARG8,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILPROTOCOL, env),
            Datatypes.DT_TEXT, this.mailProtocol);
        this.showProperty (table, DIArguments.ARG_ARG2,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILUSER, env),
            Datatypes.DT_TEXT, this.mailUser);
        this.showProperty (table, DIArguments.ARG_ARG4,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSENDER, env),
            Datatypes.DT_TEXT, this.mailSender);
        this.showProperty (table, DIArguments.ARG_ARG5,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILRECIPIENT, env),
            Datatypes.DT_TEXT, this.mailRecipient);
        this.showProperty (table, DIArguments.ARG_ARG6,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILCC, env),
            Datatypes.DT_TEXT, this.mailCC);
        this.showProperty (table, DIArguments.ARG_ARG7,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSUBJECT, env),
            Datatypes.DT_TEXT, this.mailSubject);
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log.<BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
        super.addSettingsToLog (log);
        // add connector specific settings
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSERVER, env) + ": " +
            this.mailServer, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILPROTOCOL, env) + ": " +
            this.mailProtocol, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILUSER, env) + ": " +
            this.mailUser, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSENDER, env) + ": " +
            this.mailSender, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILRECIPIENT, env) + ": " +
            this.mailRecipient, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILCC, env) + ": " +
            this.mailCC, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_MAILSUBJECT, env) + ": " +
            this.mailSubject, false);
    } // addSettingsToLog


    /**************************************************************************
     * Initializes the connector and checks the settings. <BR/>
     * The method will connect to an email server and login with a user and
     * a password. If connection was successful it tries to open the standard
     * INBOX folder. In case an error occures a
     * <pre>ConnectionFailedException</pre> will be thrown.<BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    public void initConnector ()
        throws ConnectionFailedException
    {
        Store store = null;
        Properties props = null;
        Session session = null;
        Folder folder = null;
        URLName urlname = null;

        // check first if we have a directory set where we can copy the files
        // if not create a temp directory
        if (this.isCreateTemp &&
            (this.getPath () == null || this.getPath ().length () == 0))
        {
            this.setPath (this.createTempDir ());
        } // if

        try
        {
            // get a properties object
            props = System.getProperties ();
            // Get a Session object
            session = Session.getDefaultInstance (props, null);
            session.setDebug (false);
            // construct a url name
            urlname = new URLName (this.mailProtocol, this.mailServer, -1, null,
                                   this.mailUser, this.mailPassword);
            // get a store object
            if (this.mailProtocol.equals (MailConnector_01.PROTOCOL_IMAP))
            {
                store = new IMAPStore (session, urlname);
            } // if
            else
            {
                store = new POP3Store (session, urlname);
            } // else
            // check if we could get the store
            // try to connect
            store.connect ();
            // Open the Folder
            folder = store.getDefaultFolder ();
            // check if we could get the default folder which is the root
            // of the email folders
            if (folder == null)
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_OPEN_MAIL_FOLDER, env));
            } // if (folder == null)
            // get standard INBOX folder
            folder = folder.getFolder ("INBOX");
            if (folder == null)
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_OPEN_MAIL_FOLDER, env));
            } // if (folder == null)
            try
            {
                // try to open read/write
                folder.open (Folder.READ_WRITE);
            } // try
            catch (MessagingException ex)
            {
                // if that fails try read-only
                folder.open (Folder.READ_ONLY);
            } // catch
            // set the folder
            this.folder = folder;
            // set the store
            this.store = store;
        } // try
        catch (MessagingException e)
        {
            String msg = e.getMessage ().replace ('\n', ' ');
            // pass the messaging exception message
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_MAILSERVER_CONNECTION_FAILED, env) +
                " (" + msg + ")");
        } // catch
    } // initConnector


    /**************************************************************************
     * Initializes the connector for use as backup connector. Will be
     * overwritten in the subclasses to meet the specific need of the various
     * connectors.<BR/>
     * This method must always call {@link #initConnector() initConnector}.
     *
     * @param   processId   Id of import/export process for which the connector
     *                      is used for backup.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    public void initBackupConnector (String processId)
        throws ConnectionFailedException
    {
        // call the standard initialization:
        super.initBackupConnector (processId);

        // check if the processId is defined:
        if (processId != null && processId.length () > 0)
        {
            // add processId to file prefix:
            if (this.p_filePrefix != null)
            {
                this.p_filePrefix += processId + "_";
            } // if
            else
            {
                this.p_filePrefix = processId + "_";
            } // else
        } // if
    } // initBackupConnector


    /**************************************************************************
     * Closes the connector. This method includes all actions that need to
     * be done to close a connection to a data source
     * and deletes the temp directory if applicable.<BR/>
     * For a MailConnector this includes:
     * <UL>
     * <LI>close the mail folder if available
     * <LI>close the mail store if available
     * <LI>check if there has been a temp directory created and delete it.
     * </UL>
     */
    public void close ()
    {
        try
        {
            // close the folder
            if (this.folder != null && this.folder.isOpen ())
            {
                this.folder.close (false);
            } // if
            // close the store
            if (this.store != null && this.store.isConnected ())
            {
                this.store.close ();
            } // if
        } // try
        catch (MessagingException e)
        {
            IOHelpers.showMessage ("Closing Mail connector.",
                e, this.app, this.sess, this.env, true);
        } // catch
        // check if the temp directory should be deleted
        if (this.isDeleteTemp)
        {
            this.deleteTempDir ();
        } // if
    } // close


    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings. <BR/>
     * For a MailConnector this means that a connection is opened to the mail
     * server and all available emails are read  from the mail account.
     * An entry in the resulting string array will be of the form:
     * <pre>(mail id)-(subject)</pre>. <BR/>
     *
     * @return  an array of strings of the form <pre>(mail id)-(subject)</pre>
     *          or <CODE>null</CODE> in case no messages have been retrieved
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public String[] dir ()
        throws ConnectionFailedException
    {
        try
        {
            // check if the connection to the mail server has been established
            if (this.folder == null)
            {
                this.initConnector ();
            } // if
            // get the messages from the mail folder
            Message [] messages = this.folder.getMessages ();
            // check if there have been any messages available
            if (messages == null || messages.length == 0)
            {
                return null;
            } // if
            // create the result string array that will contain
            // the email subject lines as names
            String [] names = new String [messages.length];
            // use a suitable FetchProfile
            FetchProfile fetchProfile = new FetchProfile ();
            // only fetch the envelope data
            fetchProfile.add (FetchProfile.Item.ENVELOPE);
            // get the messages with appropriate meta data
            this.folder.fetch (messages, fetchProfile);
            // loop through the messages
            for (int i = 0; i < messages.length; i++)
            {
                // check if a subject has been set
                if (messages [i].getSubject () != null)
                {
                    // construct names of the form "<id>-<subject>"
                    names [i] = messages [i].getMessageNumber () + "-" +
                                messages [i].getSubject ();
                } // if (messages [i].getSubject() != null)
                else    // no subject set
                {
                    names [i] = messages [i].getMessageNumber () + "-";
                } // else
            } // for (int i = 0; i < msgs.length; i++)
            // return the names string array
            return names;
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
        catch (MessagingException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_READ_MAIL_DIR, env));
        } // catch
        finally
        {
            try
            {
                // close the email folder and the store in order to avoid locks
                this.folder.close (false);
                this.store.close ();
            } // try
            catch (MessagingException e)
            {
                IOHelpers.showMessage ("MailConnector_01.dir closing folder",
                    e, this.app, this.sess, this.env, true);
            } // catch
        } // finally
    } // dir


    /**************************************************************************
     * Retrieves a message with a certain id from the mail server. <BR/>
     * All attachments in the mail will be written into the path set in
     * the connector. We assume that the content of the mail contains the
     * import file name and all other attachments are additional files for
     * the import.<BR/>
     *
     * @param   messageIdStr  the identifier of the message we want to read
     *                        note that this identifier has still the form
     *                        <pre>(mail id)-(subject)</pre>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    public void read (String messageIdStr)
        throws ConnectionFailedException
    {
        Message message = null;
        String methodName = "MailConnector_01.read";

        try
        {
            // check if the connection to the mail server has been established
            if (this.folder == null)
            {
                this.initConnector ();
            } // if
            // check if there is any message id set to read
            if (messageIdStr == null || messageIdStr.length () == 0)
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NOIMPORTDATA, env));
            } // if
            // cut out an identifier out of the string
            // not that the identifier has the form "(mail id)-(subject)"
            int pos = messageIdStr.indexOf ('-');
            int messageId = Integer.parseInt (messageIdStr.substring (0, pos));
            try
            {
                // set the id of the message we read
                // this can be used later to delete the mail
                this.messageId = messageId;
                // reset the filename
                this.setFileName ("");
                // get the message
                message = this.folder.getMessage (messageId);
                // check if we could get the message
                if (message == null)
                {
                    throw new ConnectionFailedException ( 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_NOIMPORTDATA, env));
                } // if (message == null)
                // read the content of the messages and write them
                // into the file system
                this.readPart (message);
                // now check if fileName has been set and if that file really exists
                if (this.getFileName ().length () > 0)
                {
                    // check if file exists
                    // if not throw an exception
                    if (!FileHelpers.exists (this.getPath () + this.getFileName ()))
                    {
                        throw new ConnectionFailedException ( 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_NOIMPORTDATA, env));
                    } // if
                } // if (getFileName ().length () > 0)
                else
                {
                    throw new ConnectionFailedException ( 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_NOIMPORTDATA, env));
                } // else
            } // try
            catch (IndexOutOfBoundsException e)
            {
                IOHelpers.showMessage (methodName,
                    e, this.app, this.sess, this.env, true);
            } // catch
        } // try
        catch (MessagingException e)
        {
            IOHelpers.showMessage (methodName,
                e, this.app, this.sess, this.env, true);
        } // catch
        catch (ConnectionFailedException e)
        {
            IOHelpers.showMessage (methodName,
                e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            try
            {
                // close the email folder and the store in order to avoid locks
                this.folder.close (false);
                this.store.close ();
            } // try
            catch (MessagingException e)
            {
                IOHelpers.showMessage ("MailConnector_01.read",
                    e, this.app, this.sess, this.env, true);
            } // catch
        } // finally
    } // read



    /**************************************************************************
     * Retrieves a message from the mail server and writes its attachments
     * into the directory from where the integrator reads the files.<BR/>
     * Additionally it sets the file name of the import file. The
     * MailConnector assumes that the name of the import file it set in the
     * body of the message. Note that this method sets the name of the import
     * file whereby the name can differ from the name set in the message body
     * in case the file already exists at the destination the file
     * should be written to. In that case a unique file name is generated
     * and this unique file name will be set as name of the importfile in
     * the connector.<BR/>
     *
     * @param   part    the message part to write
     */
    private void readPart (Part part)
    {
        String fileName;
        String newFileName = null;

        try
        {
            // check if we got a part
            if (part != null)
            {
                // try to get a fileName
                fileName = part.getFileName ();
//showDebug ("content-type: " + part.getContentType ());
//showDebug ("disposition: " + part.getDisposition ());
                // Using isMimeType to determine the content type avoids
                // fetching the actual content data until we need it.
                if (part.isMimeType ("text/plain"))
                {
                    // check if we got a fileName
                    if (fileName == null)
                    {
                        // this must be the body of the message
                        // that must contain the filename of the import file
                        // so we set the content as filename
                        this.setFileName ((String) part.getContent ());
                    } // if (fileName == null || fileName.length () == 0)
                    else    // filename is set
                    {
                        // write the content into a file
                        newFileName = this.createFile ((String) part.getContent (),
                            this.getPath (), fileName);
                    } // else filename is set
                } // if (part.isMimeType ("text/plain"))
                else if (part.isMimeType ("multipart/*"))
                {
                    // this is a multipart message
                    Multipart multipart = (Multipart) part.getContent ();
                    int count = multipart.getCount ();
                    for (int i = 0; i < count; i++)
                    {
                        this.readPart (multipart.getBodyPart (i));
                    } // for i
                } // else if (part.isMimeType ("multipart/*"))
                else if (part.isMimeType ("message/rfc822"))
                {
                    // This is a Nested Message
                    this.readPart ((Part) part.getContent ());
                } // else if (part.isMimeType("message/rfc822"))
                else    // unknown mime type
                {
                    // unkown message type
                    // get the object
                    Object obj = part.getContent ();
                    // and check if it is a string
                    if (obj instanceof String)
                    {
                        // write a string
                        newFileName = this.createFile ((String) obj,
                            this.getPath (), fileName);
                    } // if (obj instanceof String)
                    else if (obj instanceof InputStream)
                    {
                        // write an inputstream
                        newFileName = this.createFile ((InputStream) obj,
                            this.getPath (), fileName);
                    } // else if (obj instanceof InputStream)
                    else    // unknown type
                    {
                        // for all other types write a string
                        newFileName = this.createFile (obj.toString (),
                            this.getPath (), fileName);
                    } // else unkown type
                } // unknown mime type
                // now check if the import file has been written
                // and if the name changed. this can happen in order
                // to ensure unique filename
                if (this.getFileName () != null)
                {
                    // check if the import file name is the one we have written now
                    if (this.getFileName ().equals (fileName))
                    {
                        // set the new fileName because it could possibly been changed
                        // in order to ensure a unique fileName
                        this.setFileName (newFileName);
                    } // if (getFileName ().equals (fileName))
                } // if (fileName != null)
            } // if (part != null)
            else                        // part was null
            {
                // nothing to do
            } // else
        } // try
        catch (MessagingException e)
        {
            IOHelpers.showMessage ("MailConnector_01.dumpPart",
                e, this.app, this.sess, this.env, true);
        } // catch
        catch (IOException e)
        {
            IOHelpers.showMessage ("MailConnector_01.dumpPart",
                e, this.app, this.sess, this.env, true);
        } // catch
    } // dumpPart


    /**************************************************************************
     * Reads the content of an attachment via an inputStream and writes it
     * into the specified path with the specified fileName.<BR/>
     * The name of the file will be returned because it could have been changed
     * in order to ensure a unique filename and avoid overwriting an existing
     * file.<BR/>
     *
     * @param inputStream   the inputStream to read the attachment
     * @param path          the path to write the attachment to
     * @param fileName      the name of the file used for the attachment
     *                      note that this name can change in order to ensure
     *                      a unique filename
     *
     * @return  the name of the file written or <code>null</code> in case the
     *          file could not be written
     */
    private String createFile (InputStream inputStream, String path, String fileName)
    {
        BufferedInputStream bufferedInputStream;
        BufferedOutputStream bufferedOutputStream;
        int data;
        String fileN = fileName;

        // check if we got a file name
        if (fileN == null)
        {
            return null;
        } // if

        try
        {
            // set the input stream
            bufferedInputStream = new BufferedInputStream (inputStream);
            // ensure unique fileName
            fileN = FileHelpers.getUniqueFileName (path, fileN);
            // create the fileWriter
            bufferedOutputStream = new BufferedOutputStream (new FileOutputStream (path + fileN));
            // write the content into the file
            while ((data = bufferedInputStream.read ()) != -1)
            {
                bufferedOutputStream.write (data);
            } // while data
            // close the streams
            bufferedOutputStream.close ();
            bufferedInputStream.close ();
            // return the fileName because it could have been changed
            return fileN;
        } // try
        catch (IOException e)
        {
            IOHelpers.showMessage ("MailConnector: Error when creating file.",
                e, this.app, this.sess, this.env, true);
            return null;
        } // catch
    } // createFile


    /**************************************************************************
     * Write the content of an attachment into the specified path with the
     * specified fileName whereby the content is represented by a string.<BR/>
     * The name of the file will be returned because it could have been changed
     * in order to ensure a unique filename and avoid overwriting an existing
     * file.<BR/>
     *
     * @param   content     The string that contains the content.
     * @param   path        The path to write the attachment to.
     * @param   fileName    The name of the file used for the attachment. <BR/>
     *                      Note that this name can change in order to ensure
     *                      an unique file name.
     *
     * @return  The name of the file written or <code>null</code> in case the
     *          file could not be written.
     */
    public String createFile (String content, String path, String fileName)
    {
        // check if we got a file name
        String fileN = fileName;
        if (fileN == null)
        {
            return null;
        } // if

        try
        {
            // ensure unique fileName
            fileN = FileHelpers.getUniqueFileName (path, fileN);
            // create the fileWriter
            FileWriter fileWriter = new FileWriter (path + fileN);
            // write the content into the file
            fileWriter.write (content);
            // close the writer
            fileWriter.close ();
            // return the name of the file because it could have been changed
            return fileN;
        } // try
        catch (IOException e)
        {
            IOHelpers.showMessage ("MailConnector: Error when creating file",
                e, this.app, this.sess, this.env, true);
            return null;
        } // catch
    } // createFile


    /**************************************************************************
     * Writes the export file to the export destination.<BR/>
     * The method creates an email that will contain the export file and any
     * additional files as attachments. The name of the export file will be
     * set as content of the email.<BR/>
     *
     * @param   fileName    the name of the source file
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     */
    public void write (String fileName)
        throws ConnectionFailedException
    {
        EMail mail;
        File file;
        String prefix = "";

        try
        {
            // create the mail
            mail = new EMail ();
            mail.setReceiver (this.mailRecipient);
            mail.setSender (this.mailSender);
            mail.setCCReceiver (this.mailCC);
            // in case no subject has been set we use the filename
            // as subject
            if (this.mailSubject.length () > 0)
            {
                mail.setSubject (this.mailSubject);
            } // if
            else
            {
                if (this.p_filePrefix != null && this.p_filePrefix.length () > 0)
                {
                    prefix = this.p_filePrefix;
                } // if
                mail.setSubject (prefix + fileName);
            } // else

            // the name of the exportFile is set as content
            mail.setContent (fileName);
            // set the file as attachment
            file = new File (this.getPath () + fileName);
            // add the attachments in case it it valid
            if (file.exists () && file.isFile ())
            {
                // add the export file as attachment
                mail.addAttachment (file);
            } // if (file.exists () && file.isFile ())
            else
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_WRITE_FILE,
                        new String[] {fileName}, env));
            } // else
            // add any additional attachments
            for (int i = 0; i < this.attachments.size (); i++)
            {
                // add the attachment from the attachments vector
                mail.addAttachment (this.attachments.elementAt (i));
            } // for (int i = 0; i < this.attachments.size (); i++)
            // now send the mail
            EMailManager.sendMail (mail, new SMTPServer (this.mailServer));
        } // try
        catch (AddressException e)
        {
            throw new ConnectionFailedException (e.getMessage ());
        } // catch
        catch (MessagingException e)
        {
            throw new ConnectionFailedException (e.getMessage ());
        } // catch
    } // write


    /**************************************************************************
     * Read a file from the connector and copy it to the destination path.<BR/>
     * For a MailConnector this means that the file must have been an
     * attachment in the email used es import source. All attachments will be
     * written into the temporary directory of the MailConnector when reading
     * the email. This means that the file can be copied from the temporary
     * directory into the destination path.<BR/>
     *
     * @param fileName              name of the file to read
     * @param destinationPath       the path to write the file to
     * @param destinationFileName   name of the copied file.
     *                              If empty fileName will be used.
     *
     * @return the size of the file in case it could have been read successfully or
     *         -1 if an error occurred or the file has not been found
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#readFile
     */
    public long readFile (String fileName, String destinationPath,
                          String destinationFileName)
        throws ConnectionFailedException
    {
        // ensure ending file separator
        String destPath = FileHelpers.addEndingFileSeparator (destinationPath);
        // ensure unique fileName
        String destFileName =
            FileHelpers.getUniqueFileName (destPath, destinationFileName);
        // copy the file
        if (FileHelpers.copyFile (this.getPath () + fileName, destPath + destFileName))
        {
            // return the filesize to indicate that copying was successful
            return FileHelpers.getFileSize (destPath, destFileName);
        } // file could not be copied

        // return -1 to indicate that file could not be copied
        return -1;
    } // readFile


    /**************************************************************************
     * Write an file to a connector.<BR/>
     * For a MailConnector this means that the file is added to the attachments
     * vector and will be added as attachment to the email when the export
     * is written.<BR/>
     *
     * @param sourcePath        path to read the file from
     * @param fileName          name of the file to read
     *
     * @return the name of the file written or null in case it could not have
     *         been written
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#writeFile
     */
    public String writeFile (String sourcePath, String fileName)
        throws ConnectionFailedException
    {
        // ensure ending file separator
        String srcPath = FileHelpers.addEndingFileSeparator (sourcePath);
        // create the file
        File file = new File (srcPath + fileName);
        // check if file exists and is really a file and not a directory
        if (file.exists () && file.isFile ())
        {
            // add the file to the attachments vector
            this.attachments.addElement (file);
        } // if (FileHelpers.exists (sourcePath + fileName))
        else    // file does not exist
        {
            throw new ConnectionFailedException ("DIMessages.MSG_COULD_NOT_WRITE_FILE");
        } // else file does not exist
        // create the file
        return fileName;
    } // writeFile


    /**************************************************************************
     * Delete a file from its original location via the connector.<BR/>
     * For an MailConnector this means that we delete the mail that has been
     * used as import source. The mail is independed from the fileName
     * specified in the fileName parameter. The parameter only exists
     * for compatibility reasons.<BR/>
     *
     * @param fileName      the name of the file to delete
     *                      this parameter will be ignored
     *
     * @return true if the message could he deleted or false otherwiese
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#deleteFile
     */
    public boolean deleteFile (String fileName)
        throws ConnectionFailedException
    {
        Message message;
        Message [] expungedMessages;

        try
        {
            // check if the connection to the mail server has been established
            if (this.folder == null)
            {
                this.initConnector ();
            } // if

            try
            {
                // check if a messageId has been set
                if (this.messageId >= 0)
                {
                    // get the appropriate message from the folder
                    message = this.folder.getMessage (this.messageId);
                    // set the message deleted
                    message.setFlag (Flags.Flag.DELETED, true);
                    // permanently remove the message
                    expungedMessages = this.folder.expunge ();
                    // check if the message has been deleted
                    // expungedMessages must now have the length 1
                    if (expungedMessages.length > 0)
                    {
                        // reset the message id to indicate that it has
                        // already been deleted
                        this.messageId = -1;
                        return true;
                    }   // no messages expunged

                    return false;
                } // if (this.messageId > 0)
                // no message set to be deleted
                // we return true because there was no message to delete
                return true;
            } // try
            catch (IndexOutOfBoundsException e)
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_DELETE_EMAIL, env));
            } // catch
        } // try
        catch (MessagingException e)
        {
            throw new ConnectionFailedException (e.getMessage ());
        } // catch
    } // deleteFile

} // MailConnector_01
