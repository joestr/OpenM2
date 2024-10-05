/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 07.08.2002
 * Time: 16:09:07
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.BaseObject;
import ibs.service.email.SMTPServer;
import ibs.service.observer.ObserverConstants;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/******************************************************************************
 * Holds ObserverConfiguration-data and implements methods to load these from
 * an xml file.
 *
 * @version     $Id: ObserverConfiguration.java,v 1.9 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      HORST PICHLER, 07.08.2002
 ******************************************************************************
 */
public class ObserverConfiguration extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObserverConfiguration.java,v 1.9 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * The name of the OBSERVERCONFIGURATION tag in the configuration. <BR/>
     */
    protected static final String TAG_OBSERVERLOADER = "OBSERVERLOADER";
    /**
     * The name of the OBSERVERJOBCLASS tag in the configuration. <BR/>
     */
    protected static final String TAG_OBSERVERJOBCLASS = "OBSERVERJOBCLASS";
    /**
     * The name of the OBSERVER tag in the configuration. <BR/>
     */
    protected static final String TAG_OBSERVER = "OBSERVER";
    /**
     * The name of the CLASS tag in the configuration. <BR/>
     */
    protected static final String ATTR_CLASS = "CLASS";
    /**
     * The name of the CONFIGURATIONCLASS tag in the configuration. <BR/>
     */
    protected static final String ATTR_CONFIGURATIONCLASS = "CONFIGURATIONCLASS";

    /**
     * The name of the BASE tag in the configuration. <BR/>
     */
    private static final String TAG_BASE = "BASE";
    /**
     * The name of the NAME tag in the configuration. <BR/>
     */
    private static final String TAG_NAME = "NAME";
    /**
     * The name of domain in which the observer shall run. <BR/>
     */
    private static final String TAG_DOMAIN = "DOMAIN";
    /**
     * The name of the REFRESH tag in the configuration. <BR/>
     */
    private static final String TAG_REFRESH = "REFRESH";
    /**
     * The default-value of the REFRESH tag in the configuration:
     * 300000 ms = 5 minutes.
     */
    private static final int DEFAULT_REFRESH = 300000;
    /**
     * The name of the ECHO-flag in the configuration. <BR/>
     */
    private static final String TAG_ECHO = "ECHO";
    /**
     * Value OFF for ECHO:
     */
    protected static final String ECHO_OFF = "OFF";
    /**
     * Value TRACE for ECHO:
     */
    protected static final String ECHO_TRACE = "TRACE";
    /**
     * Value OFF for ECHO:
     */
    protected static final String ECHO_DEBUG = "DEBUG";


    /**
     * The name of the NOTIFICATION tag in the configuration. <BR/>
     */
    private static final String TAG_NOTIFICATION = "NOTIFICATION";
    /**
     * The name of the MAILSERVER tag in the configuration. <BR/>
     */
    private static final String TAG_MAILSERVER = "SMTPSERVER";
    /**
     * The name of the ACCOUNT attribute name in the configuration. <BR/>
     */
    protected static final String ATTR_ACCOUNT = "ACCOUNT";
    /**
     * The name of the PASSWORD attribute tag in the configuration. <BR/>
     */
    protected static final String ATTR_PASSWORD = "PASSWORD";
    /**
     * The name of the RECEIVER tag in the configuration. <BR/>
     */
    private static final String TAG_RECEIVER = "RECEIVER";
    /**
     * The name of the SENDER tag in the configuration. <BR/>
     */
    private static final String TAG_SENDER = "SENDER";
    /**
     * The name of the SUBJECT tag in the configuration. <BR/>
     */
    private static final String TAG_SUBJECT = "SUBJECT";

    /**
     * The name of the LOGGING tag in the configuration. <BR/>
     */
    private static final String TAG_LOGGING = "LOGGING";
    /**
     * The name of the LOGDIR tag in the configuration. <BR/>
     */
    private static final String TAG_LOGDIR = "LOGDIR";
/*
    / **
     * The name of the ERRORDIR tag in the configuration. <BR/>
     * /
    private static final String TAG_ERRORDIR = "ERRORDIR";
*/
    /**
     * The name of the LOGCYCLE tag in the configuration. <BR/>
     */
    private static final String TAG_LOGCYCLE = "LOGCYCLE";
    /**
     * The name of the KEEPFILES tag in the configuration. <BR/>
     */
    private static final String TAG_KEEPFILES = "KEEPFILES";
    /**
     * The name of the AUTHENTICATION-flag in the configuration. <BR/>
     */
    private static final String TAG_AUTHENTICATION = "AUTHENTICATION";
    /**
     * The name of the DOMAIN tag in the authentication configuration. <BR/>
     */
    private static final String TAG_NTLMDOMAIN = ObserverConfiguration.TAG_DOMAIN;
    /**
     * The name of the USERNAME tag in the authentication configuration. <BR/>
     */
    private static final String TAG_NTLMUSERNAME = "USERNAME";
    /**
     * The name of the PASSWORD tag in the authentication configuration. <BR/>
     */
    private static final String TAG_NTLMPASSWORD = "PASSWORD";

    //
    // base-information
    //
    /**
     * ClassName for observer implementation.
     */
    private String p_className = null;
    /**
     * ClassName for observer configuration implementation.
     */
    private String p_configurationClassName = null;
    /**
     * Unique name of observer.
     */
    private String p_name = null;
    /**
     * Name of domain in which observer shall run.
     */
    private String p_domain = null;
    /**
     * Refresh-cycle in milli-seconds.
     */
    private int p_refresh = ObserverConstants.UNDEFINED_INTEGER;
    /**
     * Echo-mode.
     */
    private String p_echo = null;

    //
    // notification information
    //
    /**
     * Notification indicator.
     */
    private boolean p_notify = false;
    /**
     * Mail server.
     */
    private SMTPServer p_mailServer = null;
    /**
     * Mail receiver.
     */
    private String p_mailReceiver = null;
    /**
     * Mail sender.
     */
    private String p_mailSender = null;
    /**
     * Mail subject.
     */
    private String p_mailSubject = null;

    //
    // logging information
    //
    /**
     * Logging indicator.
     */
    private boolean p_log = false;
    /**
     * Log directory.
     */
    private String p_logDir = null;
/*
    / **
     * Log directory for errors.
     * /
    private String p_logErrorDir = null;
*/
    /**
     * Log cycle in milli-seconds.
     */
    private int p_logCycle = ObserverConstants.UNDEFINED_INTEGER;
    /**
     * Number of log files to keep.
     */
    private int p_logKeepFiles = ObserverConstants.UNDEFINED_INTEGER;
    /**
     * Holds class-names of observerjobs used in application
     */
    private Vector<String> p_observerJobClassNames = null;

    //
    // NTLM authentication information
    //
    /**
     * NTLM domain. <BR/>
     */
    private String p_ntlmDomain = null;
    /**
     * NTLM username. <BR/>
     */
    private String p_ntlmUsername = null;
    /**
     * NTLM password. <BR/>
     */
    private String p_ntlmPassword = null;

    /**
     * Error prefix: error while reading configuration. <BR/>
     */
    private static final String ERRP_CONFREAD =
        "Error while reading configuration: ";
    /**
     * Error prefix: error while setting configuration. <BR/>
     */
    private static final String ERRP_CONFSET =
        "Error while setting configuration: ";

    /**
     * Error message: expected element not found. <BR/>
     */
    private static final String ERRM_ELEM_NOTFOUND =
        " Expected element (" + UtilConstants.TAG_NAME + ") not found.";


    ///////////////////////////////////////////////////////////////////////////
    //
    // constructors
    //
    //
    /**************************************************************************
     * Constructor for an ObserverConfiguration object. <BR/>
     */
    public ObserverConfiguration ()
    {
        // nothing to do
    } // ObserverConfiguration


    /**************************************************************************
     * Creates a ObserverConfiguration object. <BR/>
     * Only for TESTING.
     *
     * @param   className       Name of observer class.
     * @param   configurationClassName Name of configuration class.
     * @param   name            Name of configuration.
     * @param   refresh       Refresh value.
     * @param   echo            Echo value.
     * @param   notificationType Type of notification.
     * @param   mailServer      Address of mail server.
     * @param   mailUser        Name of mail user.
     * @param   mailUserPwd     Password of mail user.
     * @param   mailProtocol    Protocol to be used for mailing.
     * @param   mailReceiver    Address of mail receiver.
     * @param   mailSender      Address of mail sender.
     * @param   mailSubject     Subject of mail.
     * @param   logDir          Directory for normal logging.
     * @param   logErrorDir     Directory for error logging.
     * @param   logCycle        Use cycle for logging?
     * @param   logKeepFiles    Keep log files?
     * @param   ntlmDomain      Domain for NTLM authentication.
     * @param   ntlmUsername    User name for NTLM authentication.
     * @param   ntlmPassword    Password for NTLM authentication.
     */
    protected ObserverConfiguration (String className,
                                     String configurationClassName,
                                     String name, int refresh,
                                     String echo, String notificationType,
                                     SMTPServer mailServer, String mailUser,
                                     String mailUserPwd,
                                     String mailProtocol,
                                     String mailReceiver,
                                     String mailSender, String mailSubject,
                                     String logDir, String logErrorDir,
                                     int logCycle, int logKeepFiles,
                                     String ntlmDomain,
                                     String ntlmUsername,
                                     String ntlmPassword)
    {
        this.p_className = className;
        this.p_configurationClassName = configurationClassName;

        this.p_name = name;
        this.p_refresh = refresh;
        this.p_echo = echo;

        this.p_mailServer = mailServer;
        this.p_mailReceiver = mailReceiver;
        this.p_mailSender = mailSender;
        this.p_mailSubject = mailSubject;

        this.p_logDir = logDir;
//        this.p_logErrorDir = p_logErrorDir;
        this.p_logCycle = logCycle;
        this.p_logKeepFiles = logKeepFiles;

        this.p_ntlmDomain = ntlmDomain;
        this.p_ntlmUsername = ntlmUsername;
        this.p_ntlmPassword = ntlmPassword;
    } // ObserverConfiguration


    ///////////////////////////////////////////////////////////////////////////
    //
    // getters
    //
    //

    /**************************************************************************
     * Get the name of the class which implements the observer. <BR/>
     *
     * @return  The class name.
     */
    public String getClassName ()
    {
        return this.p_className;
    } // getClassName


    /**************************************************************************
     * Get the observer name. <BR/>
     *
     * @return  The name
     */
    public String getName ()
    {
        return this.p_name;
    } // getName


    /**************************************************************************
     * Get the domain. <BR/>
     *
     * @return  The domain name.
     */
    public String getDomain ()
    {
        return this.p_domain;
    } // getDomain


    /**************************************************************************
     * Get the refresh value. <BR/>
     *
     * @return  The refresh value.
     */
    public int getRefresh ()
    {
        return this.p_refresh;
    } // getRefresh


    /**************************************************************************
     * Get the echo value. <BR/>
     *
     * @return  The echo string.
     */
    public String getEcho ()
    {
        return this.p_echo;
    } // getEcho


    /**************************************************************************
     * Check whether a notify shall be performed. <BR/>
     *
     * @return  <CODE>true</CODE> if notify shall be performed,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean getNotify ()
    {
        return this.p_notify;
    } // getNotify


    /**************************************************************************
     * Get the mail server. <BR/>
     *
     * @return  The mail server.
     */
    public SMTPServer getMailServer ()
    {
        return this.p_mailServer;
    } // getMailServer


    /**************************************************************************
     * Get the mail receiver. <BR/>
     *
     * @return  The name of the mail receiver.
     */
    public String getMailReceiver ()
    {
        return this.p_mailReceiver;
    } // getMailReceiver


    /**************************************************************************
     * Get the mail sender. <BR/>
     *
     * @return  The name of the mail sender.
     */
    public String getMailSender ()
    {
        return this.p_mailSender;
    } // getMailSender


    /**************************************************************************
     * Get the mail subject. <BR/>
     *
     * @return  The mail subject text.
     */
    public String getMailSubject ()
    {
        return this.p_mailSubject;
    } // getMailSubject


    /**************************************************************************
     * Check whether the logging is on. <BR/>
     *
     * @return  <CODE>true</CODE> if loggin is on,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean getLog ()
    {
        return this.p_log;
    } // getLog


    /**************************************************************************
     * Get the log directory. <BR/>
     *
     * @return  The path and name of the log directory.
     */
    public String getLogDir ()
    {
        return this.p_logDir;
    } // getLogDir


/*
    / **************************************************************************
     * Get the log directory for errors. <BR/>
     *
     * @return  The path and name of the log directory.
     * /
    public String getLogErrorDir ()
    {
        return p_logErrorDir;
    }
*/


    /**************************************************************************
     * Get the log cycle. <BR/>
     *
     * @return  The log cycle.
     */
    public int getLogCycle ()
    {
        return this.p_logCycle;
    } // getLogCycle


    /**************************************************************************
     * Get the number of files to keep in log. <BR/>
     *
     * @return  The number of files to keep.
     */
    public int getLogKeepFiles ()
    {
        return this.p_logKeepFiles;
    } // getLogKeepFiles


    /**************************************************************************
     * Get all observer job classes. <BR/>
     *
     * @return  A vector with the class names.
     */
    public Vector<String> getObserverJobClasses ()
    {
        return this.p_observerJobClassNames;
    } // getObserverJobClasses


    /**************************************************************************
     * @return the NTLM domain
     */
    public String getNTLMDomain ()
    {
        return this.p_ntlmDomain;
    } // getNTLMDomain


    /**************************************************************************
     * @return the NTLM password
     */
    public String getNTLMPassword ()
    {
        return this.p_ntlmPassword;
    } // getNTLMPassword


    /**************************************************************************
     * @return the NTLM username
     */
    public String getNTLMUsername ()
    {
        return this.p_ntlmUsername;
    } // getNTLMUsername


    /**************************************************************************
     * @param domain    the NTLM domain
     */
    public void setNTLMDomain (String domain)
    {
        this.p_ntlmDomain = domain;
    } // setNTLMDomain


    /**************************************************************************
     * @param password  the NTLM password
     */
    public void setNTLMPassword (String password)
    {
        this.p_ntlmPassword = password;
    } // setNTLMPassword


    /**************************************************************************
     * @param username  the NTLM username
     */
    public void setNTLMUsername (String username)
    {
        this.p_ntlmUsername = username;
    } // setNTLMUsername


    /**************************************************************************
     * Adds jobclass-names to already set jobclassnames.
     *
     * @param   jobClassNames   Names of job classes to be set.
     */
    public void addObserverJobClassNames (Vector<String> jobClassNames)
    {
        if (this.p_observerJobClassNames == null)
        {
            this.p_observerJobClassNames = new Vector<String> ();
        } // if

        if (jobClassNames != null)
        {
            this.p_observerJobClassNames.addAll (jobClassNames);
        } // if
    } // addObserverJobClasses


    ///////////////////////////////////////////////////////////////////////////
    //
    // logic
    //
    //

    /**************************************************************************
     * Read the observer configuration file. Only the first OBSERVER-element
     * will be read, the rest will be ignored.
     *
     * @param   configFile  The configFile including path.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void readConfig (String configFile)
        throws ObserverException
    {
        if (configFile != null && configFile.length () > 0) // there was a path defined?
        {
            if (FileHelpers.exists (configFile)) // given file exists?
            {
                try
                {
                    // create DOM-parser and parse file (incl. dtd-validation) --> dom-tree
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
                    dbf.setValidating (true);    // check document against dtd
                    DocumentBuilder db = dbf.newDocumentBuilder ();
                    Document doc = db.parse (new File (configFile));

                    // get first OBSERVER-element
                    NodeList nodes = doc
                        .getElementsByTagName (ObserverConfiguration.TAG_OBSERVER);
                    if (nodes.getLength () < 1)  // does at least 1 exist?
                    {
                        throw new ObserverException (
                            ObserverConfiguration.ERRP_CONFREAD +
                            "No OBSERVER-element found.");
                    } // if no element

                    // extract configuration information from OBSERVER-node
                    this.setConfigurationData (nodes.item (0));
                } // try
                catch (ParserConfigurationException e)
                {
                    throw new ObserverException (
                        ObserverConfiguration.ERRP_CONFREAD + e.toString ());
                } // catch
                catch (SAXException e)
                {
                    throw new ObserverException (
                        ObserverConfiguration.ERRP_CONFREAD + e.toString ());
                } // catch
                catch (IOException e)
                {
                    throw new ObserverException (
                        ObserverConfiguration.ERRP_CONFREAD + e.toString ());
                } // catch
            } // if
            else
            {
                throw new ObserverException ("Could not find configuration-file: " + configFile + ".");
            } // else
        } // if there was a path defined
        else
        {
            throw new ObserverException ("Path to config-file not initialized.");
        } // else no path defined
    } // readConfig


    /**************************************************************************
     * Extract data from given OBSERVER-element and set it in this object. <BR/>
     *
     * @param   node    The node with the configuration data.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void setConfigurationData (Node node) throws ObserverException
    {
        Node child = null;
        Node attr = null;

        // check if given node is OBSERVER-element
        if (!ObserverConfiguration.TAG_OBSERVER.equals (node.getNodeName ()))
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                    " Expected OBSERVER-element, but got " + node.getNodeName () + ".");
        } // if

        // get attributes of OBSERVER-element
        NamedNodeMap nm = node.getAttributes ();
        if (nm == null)  // exists?
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                "No OBSERVER-attribute found.");
        } // if
        // ATTRIBUTE: CLASS
        attr = nm.getNamedItem (ObserverConfiguration.ATTR_CLASS);
        if (attr == null)
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                "OBSERVER-attribute CLASS not set.");
        } // if
        this.p_className = attr.getNodeValue ();
        // ATTRIBUTE: CONFIGURATIONCLASS
        attr = nm.getNamedItem (ObserverConfiguration.ATTR_CONFIGURATIONCLASS);
        if (attr == null)
        {
            throw new ObserverException ("Error while loading configurations: " +
                "OBSERVER-attribute CONFIGURATIONCLASS not set.");
        } // if
        this.p_configurationClassName = attr.getNodeValue ();


        if (!this.getClass ().getName ().equals (
            nm.getNamedItem (ObserverConfiguration.ATTR_CONFIGURATIONCLASS)
                .getNodeValue ()))
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                " Expected CONFIGURATIONCLASS=" +
                this.getClass ().getName () +
                ", but got " +
                nm.getNamedItem (ObserverConfiguration.ATTR_CONFIGURATIONCLASS)
                    .getNodeValue ());
        } // if

        //
        // extract information from childnodes
        //

        // <BASE>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_BASE, true);
        this.setBaseData (child);

        // <NOTIFICATION>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_NOTIFICATION, false);
        if (child != null)
        {
            this.setNotificationData (child);
        } // if

        // <LOGGING>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_LOGGING, false);
        if (child != null)
        {
            this.setLoggingData (child);
        } // if

        // <AUTHENTICATION>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_AUTHENTICATION, false);
        if (child != null)
        {
            this.setAuthenticationData (child);
        } // if (child != null)
    } // setConfigurationData

    /**************************************************************************
     * Extract data from given BASE-element and set it in this object. <BR/>
     *
     * @param   node    The node with the base data.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void setBaseData (Node node) throws ObserverException
    {
        Node child = null;
        String v = null;

        // check if given node is BASE-element
        if (!ObserverConfiguration.TAG_BASE.equals (node.getNodeName ()))
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                    " Expected OBSERVER:BASE-element, but got " + node.getNodeName () + ".");
        } // if

        //
        // extract information from childnodes
        //
        // <NAME>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_NAME, true);
        this.p_name = ObserverConfiguration.getNodeText (child, "OBSERVER:BASE:NAME", true);
        // check length: maximum of 18 characters allowed
        if (this.p_name.length () > 18)
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                    " Length of OBSERVER:NAME must be 18 characters or less: " + this.p_name + ".");
        } // if
        // <DOMAIN>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_DOMAIN, false);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:BASE:DOMAIN", false);
        this.p_domain = v;
        // <REFRESH>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_REFRESH, false);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:BASE:REFRESH", false);
        if (v == null)
        {
            this.p_refresh = ObserverConfiguration.DEFAULT_REFRESH;
        } // if
        else
        {
            this.p_refresh = this.stringToInt (v);
        } // else
        // <ECHO>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_ECHO, false);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:BASE:ECHO", false);
        if (v != null)
        {
            if (v.equalsIgnoreCase (ObserverConfiguration.ECHO_TRACE))
            {
                this.p_echo = ObserverConfiguration.ECHO_TRACE;
            } // if
            else if (v.equalsIgnoreCase (ObserverConfiguration.ECHO_DEBUG))
            {
                this.p_echo = ObserverConfiguration.ECHO_DEBUG;
            } // else if
            else
            {
                this.p_echo = ObserverConfiguration.ECHO_OFF;
            } // else
        } // if
        else
        {
            this.p_echo = ObserverConfiguration.ECHO_OFF;
        } // else
    } // setBaseData


    /**************************************************************************
     * Extract data from given NOTIFICATION-element and set it in this object. <BR/>
     *
     * @param   node    The node with the notification data.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void setNotificationData (Node node) throws ObserverException
    {
        Node child = null;
        Node attr = null;
        String v = null;

        // set notify-indicator
        this.p_notify = true;

        // check if given node is NOTIFICATION-element
        if (!ObserverConfiguration.TAG_NOTIFICATION.equals (node.getNodeName ()))
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                    " Expected OBSERVER:NOTIFICATION-element, but got " + node.getNodeName () + ".");
        } // if

        //
        // extract information from childnodes
        //
        // <MAILSERVER>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_MAILSERVER, true);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:NOTIFICATION:MAILSERVER", true);
        this.p_mailServer = new SMTPServer (v);

        // get attributes of MAILSERVER-element
        NamedNodeMap nm = child.getAttributes ();
        if (nm != null)  // exists?
        {
            // ATTRIBUTE: ACCOUNT
            attr = nm.getNamedItem (ObserverConfiguration.ATTR_ACCOUNT);
            if (attr != null && !attr.getNodeValue ().isEmpty ())
            {
                this.p_mailServer.setAccountName (attr.getNodeValue ());
            } // if
            
            // ATTRIBUTE: PASSWORD
            attr = nm.getNamedItem (ObserverConfiguration.ATTR_PASSWORD);
            if (attr != null && !attr.getNodeValue ().isEmpty ())
            {
                this.p_mailServer.setPassword (attr.getNodeValue ());
            } // if       
        } // if

        // <RECEIVER>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_RECEIVER, true);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:NOTIFICATION:RECEIVER", true);
        this.p_mailReceiver = v;
        // <SENDER>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_SENDER, true);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:NOTIFICATION:SENDER", true);
        this.p_mailSender = v;
        // <SUBJECT>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_SUBJECT, true);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:NOTIFICATION:SUBJECT", true);
        this.p_mailSubject = v;
    } // setNotificationData


    /**************************************************************************
     * Extract data from given LOGGING-element and set it in this object. <BR/>
     *
     * @param   node    The node with the logging data.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void setLoggingData (Node node) throws ObserverException
    {
        Node child = null;
        String v = null;

        // set log-indicator
        this.p_log = true;

        // check if given node is LOGGING-element
        if (!ObserverConfiguration.TAG_LOGGING.equals (node.getNodeName ()))
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                    " Expected OBSERVER:LOGGING-element, but got " + node.getNodeName () + ".");
        } // if

        //
        // extract information from childnodes
        //
        // <LOGDIR>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_LOGDIR, true);
        this.p_logDir = ObserverConfiguration.getNodeText (child, "OBSERVER:LOGGING:LOGDIR", true);
/*
        // <ERRORDIR>
        child = getChildNodeByName (node, TAG_ERRORDIR, true);
        this.p_logErrorDir = getNodeText (child, "OBSERVER:LOGGING:ERRORDIR", true);
*/
        // <LOGCYCLE>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_LOGCYCLE, false);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:LOGGING:LOGCYCLE", false);
        if (v != null)
        {
            this.p_logCycle = this.stringToInt (v);
        } // if
        // <KEEPFILES>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_KEEPFILES, false);
        v = ObserverConfiguration.getNodeText (child, "OBSERVER:LOGGING:KEEPFILES", false);
        if (v != null)
        {
            this.p_logKeepFiles = this.stringToInt (v);
        } // if
    } // setLoggingData


    /**************************************************************************
     * Extract data from given AUTHENTICATION-element and set it in this
     * object. <BR/>
     *
     * @param node  the AUTHENTICATION node to read the settings from
     *
     * @throws  ObserverException
     *          The node is no authentication node.
     */
    protected void setAuthenticationData (Node node) throws ObserverException
    {
        Node child = null;

        // set log-indicator
        this.p_log = true;

        // check if given node is LOGGING-element
        if (!ObserverConfiguration.TAG_AUTHENTICATION.equals (node.getNodeName ()))
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                    " Expected OBSERVER:AUTHENTICATION-element, but got " + node.getNodeName () + ".");
        } // if (!TAG_AUTHENTICATION.equals (node.getNodeName ()))

        // <DOMAIN>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_NTLMDOMAIN, false);
        this.p_ntlmDomain = ObserverConfiguration.getNodeText (child,
            "OBSERVER:AUTHENTICATION:DOMAIN", false);
        // <USERNAME>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_NTLMUSERNAME, false);
        this.p_ntlmUsername = ObserverConfiguration.getNodeText (child,
            "OBSERVER:AUTHENTICATION:USERNAME", false);
        // <PASSWORD>
        child = ObserverConfiguration.getChildNodeByName (node,
            ObserverConfiguration.TAG_NTLMPASSWORD, false);
        this.p_ntlmPassword = ObserverConfiguration.getNodeText (child,
            "OBSERVER:AUTHENTICATION:PASSWORD", false);
    } // aetAuthenticationData


    /**************************************************************************
     * Checks node and returns text-value of 1st child.<BR> The parameter
     * 'name' holds the name|location|description of the node - needed for
     * composition of error-string, e.g. OBSERVER:NOTIFICATION:TYPE
     *
     * @param   node        The node to be extracted.
     * @param   name        The name of the node.
     * @param   mandatory   Is the text mandatory or optional?
     *
     * @return  Text-value of 1st childnode.
     *          <CODE>null</CODE> if node not exists or its value is
     *          <CODE>null</CODE>.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected static String getNodeText (Node node, String name,
                                         boolean mandatory)
        throws ObserverException
    {
        if (node == null)          // check null
        {
            if (mandatory)                                        // must exist?
            {
                throw new ObserverException (
                    ObserverConfiguration.ERRP_CONFSET +
                    StringHelpers.replace (
                            ObserverConfiguration.ERRM_ELEM_NOTFOUND,
                            UtilConstants.TAG_NAME, name));
            } // if

            return null;
        } // if null

        // normalize the node to ensure that all text values are together.
        node.normalize ();
        Text text = (Text) node.getFirstChild ();
        if (text == null)
        {
            if (mandatory)
            {
                throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                        name + "-element is empty.");
            } // if

            return null;
        } // if

        return text.getNodeValue ();
    } // getNodeText


    /**************************************************************************
     * Get (1st) childnode with given nodeName of given rootNode.<BR> If
     * mandatory is set and node cannot be found, then an ObserverException
     * will be returned.
     *
     * @param   rootNode    The root node where to start the search.
     * @param   nodeName    The name of the node to search for.
     * @param   mandatory   Is the node mandatory or optional?
     *
     * @return  Childnode of rootNode with given nodeName.
     *          <CODE>null</CODE> if not found.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected static Node getChildNodeByName (Node rootNode, String nodeName,
                                              boolean mandatory)
        throws ObserverException
    {
        // check parameters
        if (rootNode == null || nodeName == null || nodeName.length () == 0)
        {
            throw new ObserverException ("Error in getChildNodeByName: " +
                    " Given rootNode or nodeName is not defined.");
        } // if

        // get childnode
        NodeList nl = rootNode.getChildNodes ();
        Node n = null;
        for (int i = 0; i < nl.getLength (); i++)
        {
            n = nl.item (i);
            if (n.getNodeName ().equals (nodeName))
            {
                return n;
            } // if
        } // for i

        if (mandatory)                          // must exist?
        {
            throw new ObserverException (ObserverConfiguration.ERRP_CONFSET +
                StringHelpers.replace (ObserverConfiguration.ERRM_ELEM_NOTFOUND,
                    UtilConstants.TAG_NAME, nodeName));
        } // if

        return null;
    } // getChildNodeByName


    /**************************************************************************
     * Convert given string to int, throw ObserverException if fails.
     *
     * @param   s       The string to convert.
     *
     * @return  Integer-value.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected int stringToInt (String s)
        throws ObserverException
    {
        try
        {
            return new Integer (s).intValue ();
        } // try
        catch (NumberFormatException e)
        {
            throw new ObserverException ("Error in stringToInt: " +
                    " Given value '" + s + "' can not be converted to integer.");
        } // catch
    } // stringToInt


    /**************************************************************************
     * Create an ObserverContext-object out of this configuration. <BR/>
     *
     * @return  XML-element that holds the observer context.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public ObserverContext createObserverContext () throws ObserverException
    {
        ObserverContext context = null;
        context = new ObserverContext (this.p_name, "baseDir", this.p_logDir,
            this.p_logDir + "/jobs");
        return context;
    } // createObserverContext


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[p_className=" + this.p_className +
            "; p_configClassName=" + this.p_configurationClassName +
            "; p_name=" + this.p_name + "; p_refresh=" + this.p_refresh +
            "; p_mailServer=" + this.p_mailServer +
            "; p_mailReceiver=" + this.p_mailReceiver +
            "; p_mailSender=" + this.p_mailSender +
            "; p_mailSubject=" + this.p_mailSubject +
            "; p_logDir=" + this.p_logDir +
//            "; p_logErrorDir=" + this.p_logErrorDir +
            "; p_logCycle=" + this.p_logCycle +
            "; p_logKeepFiles=" + this.p_logKeepFiles +
            "]";
    } // toString

} // ObserverConfiguration
