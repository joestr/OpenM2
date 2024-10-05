/*
 * Class: ExportAgent_01.java
 */

// package:
package ibs.di.agent;

// imports:
//TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
import ibs.di.connect.FTPConnector_01;
import ibs.di.connect.FileConnector_01;
import ibs.di.connect.HTTPMultipartConnector_01;
import ibs.di.connect.MailConnector_01;
import ibs.di.connect.SAPBCXMLRFCConnector_01;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.service.email.EMail;
import ibs.service.email.EMailManager;
import ibs.service.email.IOutgoingMailServer;
import ibs.service.email.SMTPServer;
import ibs.tech.http.HttpArguments;
import ibs.tech.ntlm.NTLMClient;
import ibs.tech.ntlm.NTLMException;
import ibs.util.DateTimeHelpers;
import ibs.util.file.FileHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


/******************************************************************************
 * This class represents one object of type ExportAgent with version 01. <BR/>
 * ExportAgent is a standalone java application that regularily tries to call
 * a m2 application server in a certain frequency in order to invoke an export.
 * The m2 application server will perform the export and write the exportfiles
 * into the temporary directory of the agent via an file connector.
 * The export agent will write the export files from the temporary directory
 * to his own connector.
 * In case the user does not define a password in the command line arguments
 * the agent prompts the user to enter it. <BR/>
 *
 * An example call looks like this:
 * <pre>
 * java ibs.di.imp.ExportAgent_01 -user Administrator -pw A.dmin#+ -object
 * 0x0101002100000051 - object "Gruppe/Data Interchange" -frequency minutes -every 5
 * </pre>
 *
 * using different export settings
 * <pre>
 * -hierachical
 * -includeContainer
 * -singlefile
 * -recursive
 * -keymapping
 * </pre>
 *
 * using different connectortypes:
 * <pre>
 * -connectortype file
 * -connectortype ftp  -ftpserver ftp.tectum.at
 *                     -ftpuser buchegger
 *                     -ftppassword Bernd
 * -connectortype mail -mailserver mail.tectum.at
 *                     -mailsender buchegger@tectum.at
 *                     -mailreceiver luschin@tectum.at
 * -connectortype sap  -sapname ???
 *                     -sapurl ???
 * -connectortype httpmultipart
 *                     -httpmultpartiurl http://www.tectum.at/cgi.exe?action=write
 * </pre>
 *
 * using different frequency combinations:
 * <pre>
 * -frequency minutes -every 5
 * -frequency day -time 10:30
 * -frequency week -every TU,SO -time 23:00
 * -frequency month -every 1 -time 3:00
 * </pre>
 *
 * The usage description of the ExportAgent:
 * <pre>
 * Usage:
 * java ibs.di.imp.ExportAgent_01
 *    -user &lt;username> [-pw password]
 *    [-domain &lt;domainId, default: 1>]
 *    -object {&lt;exportObject OID>|&lt;m2 path to exportObject>}
 *    [-object &lt;OID or path ... can be defined multiple>]
 *    [-frequency {ONCE(=default)|MINUTES|DAY|WEEK|MONTH}]
 *    [-every {&lt;minutes>|&lt;day of month>|MO,TU,WE,TH,FR,SA,SU>] [-time &lt;time>]
 *    [-workdir &lt;working directory, default is .\\>]
 *    [-tempdir &lt;temporary directory, default is .\\temp>]
 *    [-errordir &lt;errorfiles directory, default is .\\errorfiles>]
 *    [-retry &lt;number of retries, default is 10>]
 *    [-notify &lt;YES|NO(=default)> -notifymailserver &lt;mailserver>
 *     -notifyreceiver &lt;receiver> -notifysender &lt;sender> -notifysubject &lt;subject>]
 *    [-connection {ASP(=default)|SERVLET}]
 *    [-server &lt;name of m2 server, default is localhost>]
 *    [-apppath &lt;m2 application path, default is /m2/app/>]
 *    [-translator {&lt;translator OID>|&lt;name of translator>}]
 *    [-logging {NONE(=default)|NEW|APPEND}]
 *    [-logfilename &lt;name of the log file>]
 *    [-logpath &lt;path for the log file, default is the path set in -path>]
 *    [-wait] [-hierarchical] [-singlefile] [-recursive] [-includecontainer]
 *    [-keymapping] [-delete] [-deleterecursive]
 *    [-connectortype {FILE(=default)|FTP|MAIL|SAP|HTTPMULTIPART}]
 *    [-ftpserver &lt;ftp server> -ftpuser &lt;username> -ftppassword &lt;password>
 *     -ftppath &lt;path on ftp server to read the files from>]
 *    [-mailserver &lt;mail server> -mailprotocol {IMAP|POP3}
 *     -mailuser &lt;username> -mailpassword &lt;password>
 *     -mailreceiver &lt;receiveraddress> -mailsender &lt;senderaddress>]
 *    [-sapurl &lt;SAP business connector gateway URL> -sapname &lt;name of sap server>]
 *    [-httpmultiparturl &lt;HTTP multipart connector target URL>]
 *    [-help] [--help] [-?]
 * </pre>
 *
 * @version     $Id: ExportAgent_01.java,v 1.36 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Luschin Angelika (AN), 20010104
 *
 * @see ibs.di.agent.ImportAgent_01
 ******************************************************************************
 */
public class ExportAgent_01 extends Thread
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExportAgent_01.java,v 1.36 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * the connector used for the Connection. <BR/>
     */
    private Connector_01 connector = null;

    // LOGIN PREFERENCES
    /**
     * name of the m2 server. <BR/>
     */
    private String m2ServerName = "localhost";

    /**
     * name of the m2 server. <BR/>
     */
    private String m2AppPath = "/m2/app/";

    /**
     * flag to use a servlet connection. <BR/>
     * Default: <CODE>true</CODE>.
     */
    private boolean isServlet = true;

    /**
     * name of the user to use for login. <BR/>
     */
    private String userName = "";

    /**
     * password of the user. <BR/>
     */
    private String password = "";

    /**
     * domain of the user. <BR/>
     */
    private int domain = 1;


    // TIME AND FREQUENCY PREFERENCES
    /**
     * flag to wait before first activation. <BR/>
     * if set to false to agent will be activated instantly
     * after start ignoring the frequency settings. <BR/>
     */
    private boolean isWait = false;

    /**
     * time at which to start the agent. <BR/>
     * this property holds the setting in the arguments. <BR/>
     */
    private String timeStr = "";

    /**
     * value to start the agent. <BR/>
     * can be in minutes (1 ... one minute)
     * or reference a day of the month [1..31]
     * or it can be a weekday [MO|TU|WE|TH|FR|SA|SU].
     * this property holds the setting in the arguments. <BR/>
     */
    private String everyStr = "";

    /**
     * frequency to start the agent in minutes. <BR/>
     */
    private int everyMinutes = 0;

    /**
     * day of the month to start the agent (1..31). <BR/>
     */
    private int everyDay = 1;

    /**
     * array that represents the days of the week. <BR/>
     * for every day set [SU|MO|TU|WE|TH|FR|SA] the corresponding
     * array field is set. (SU sets everyWeek[0] = true). <BR/>
     */
    private boolean[] everyWeekday =
    {
        false, false, false, false, false, false, false,
    }; // everyWeekday

    /**
     * flag to indicate that a weekday filter has been set. <BR/>
     */
    private boolean isWeekdaySet = false;

    /**
     * time to start the agent. <BR/>
     */
    private GregorianCalendar everyTime = new GregorianCalendar ();

    /**
     * type of frequency to start the agent. <BR/>
     * allowed values are [MINUTES|DAY|WEEK|MONTH].
     * this property holds the setting in the arguments. <BR/>
     */
    private String frequencyStr = "";

    /**
     * type of frequency to start the agent. <BR/>
     * default value = 0 (check in 10 minutes intervals). <BR/>
     */
    private int frequencyType = 0;


    // EXPORT PREFERENCES
    /**
     * path to source directory. <BR/>
     * default is the directory  the agent has been started from. The same
     * path will be used for the FileConnector and will be the default
     * log path if no explicit path provided. <BR/>
     */
    private String workingDirectory = "";

    /**
     * path to temporary directory. <BR/>
     * Default is &lt;workingDirectory&gt;/temp. <BR/>
     */
    private String tempDirectory = "";

    /**
     * path to errorfiles directory. <BR/>
     * Default is &lt;workingDirectory&gt;/errorfiles. <BR/>
     */
    private String errorfilesDirectory = "";

    /**
     * Name of agents error log. <BR/>
     */
    private String errorLogFileName = AgentConstants.FILENAME_ERRORLOG;

    /**
     * Number of retries. <BR/>
     * Default is 10. <BR/>
     */
    private int retry = 10;

    /**
     * error notifiation option. <BR/>
     * If true agent tries to send an email to the responsible administrator. <BR/>
     */
    private boolean p_isNotify = false;

    /**
     * Outgoing mail server for error notification. <BR/>
     */
    private IOutgoingMailServer p_notifyMailServer;

    /**
     * email adress of reveiver who will get the error notification email. <BR/>
     */
    private String p_notifyReceiver = "";

    /**
     * email adress of sender who will get the error notification email. <BR/>
     */
    private String p_notifySender = "";

    /**
     * subject text of error notification email. <BR/>
     */
    private String p_notifySubject = "Agent ERROR Notification!";

    /**
     * oid of the translator to use. <BR/>
     * this is an alternative to using translator name. <BR/>
     */
    private OID translatorOid = null;

    /**
     * name of the translator to use
     * alternative to translator OID. <BR/>
     */
    private String translatorName = "";


    // backup preferences:
    /**
     * OID of the backup connector to be used.
     * (alternative to the backup connector name). <BR/>
     */
    private OID p_backupConnectorOid = null;

    /**
     * Name of the backup connector to be used. <BR/>
     */
    private String p_backupConnectorName = "";


    // LOG preferences
    /**
     *  name of the log file. <BR/>
     */
    private String logFileName = "";

    /**
     *  name of the log path. <BR/>
     */
    private String logPath = "";

    /**
     *  flag to write the log file. <BR/>
     */
    private boolean isWriteLog = false;

    /**
     *  flag to append the log to an existing log file. <BR/>
     */
    private boolean isAppendLog = false;

    /**
     *  type of connector to use. default is fileConnector. <BR/>
     */
    private int connectorType = DIConstants.CONNECTORTYPE_FILE;

    //
    // FTP connector preferences
    //
    /**
     *  name of FTP server
     */
    public String ftpServer = "";

    /**
     *  path at FTP server
     */
    public String ftpPath = "";

    /**
     *  user to login at FTP server
     */
    public String ftpUser = "";

    /**
     *  password for login at FTP server
     */
    public String ftpPassword = "";

    //
    // MAIL connector preferences
    //
    /**
     *  name of mail server
     */
    public String mailServer = "";

    /**
     *  mailaddress of the receiver
     */
    public String mailReceiver = "";

    /**
     *  mailaddress of the sender
     */
    public String mailSender = "";

    /**
     *  name of user of the mail account
     */
    public String mailUser = "";

    /**
     *  password of user of the mail account
     */
    public String mailPassword = "";

    /**
     *  mail protocol to be used in mailconnector
     */
    public String mailProtocol = "";

    //
    // SAP Business Connector XMLRFC connector preferences
    //
    /**
     *  url of the SAP Business Connector gateway
     */
    public String sapUrl = "";

    /**
     *  name of the SAP server
     */
    public String sapName = "";

    //
    // HTTP Multipart connector preferences
    //
    /**
     * Url to the target webserver. <BR/>
     */
    public String httpMultipartUrl = "";

    // EXPORT OPTIONS
    /**
     * Should it be a hierarchical export or not. That means that a hierachical
     * structure is generated. <BR/>
     */
    private boolean isHierarchical = false;

    /**
     * Objects will be wtritten into one single exportfile. <BR/>
     */
    private boolean isSingleFile = false;

    /**
     * Should all substructures of containers be exported. <BR/>
     */
    private boolean isRecursive = false;

    /**
     * Should containers be exported too. <BR/>
     */
    private boolean isIncludeContainer = false;

    /**
     * to create an unique object. <BR/>
     */
    private boolean isResolveKeyMapping = false;

    /**
     * Should objects be deleted after successful export. <BR/>
     */
    private boolean isDelete = false;

    /**
     * Should all subobjects be deleted after successful export. <BR/>
     */
    private boolean isDeleteRecursive = false;

    /**
     * Should the result of a query be exported instead of the query. <BR/>
     */
    private boolean isResolveQuery = false;

    /**
     * Should external ids be stored if possible. <BR/>
     */
    private boolean isRestoreExternalIDs = false;

    /**
     * Should references be resolved. <BR/>
     */
    private boolean isResolveReference = false;

    /**
     * Should oid of the reference be used when
     * resolving references. <BR/>
     */
    private boolean isUseReferenceOid = false;

    /**
     * Domain for NTLM authentication. <BR/>
     */
    private String p_ntlmDomain = "";

    /**
     * Username for NTLM authentication. <BR/>
     */
    private String p_ntlmUsername = "";

    /**
     * Password for NTLM authentication. <BR/>
     */
    private String p_ntlmPassword = "";

    /**
     *  flag to show debug messages. <BR/>
     */
    private boolean isDebug;

    /**
     * A vector holding the objects to be exported
     * set via the command line parameter -OBJECT. <BR/>
     */
    private Vector<String> p_objects = new Vector<String> ();


    // AGENT SPECIFIC PROPERTIES
    /**
     *  Date of last activation. <BR/>
     */
    private Date lastActivationDate;

    /**
     *  Date of scheduled next activation. <BR/>
     */
    private Date nextActivationDate;

    /**
     * Vector of fileNames that could not have been written
     * to connector. <BR/>
     * These files will not be deleted from the temporary
     * directory in order to export during the next agent
     * activation. Note that the temporary directory will not
     * be deleted in case such files exist. <BR/>
     */
    private Vector<AgentErrorFile> errorFiles = null;


    /**************************************************************************
     * Creates an ExportAgent_01 object. <BR/>
     */
    public ExportAgent_01 ()
    {
        // nothing to do
    } // Constructor - ExportAgent_01


    /**************************************************************************
     * The main method will be executed when called from the operating
     * system. It reads the command line options into the class properties.
     * Command line parameters with fixed value must be checked. In case there
     * has been an invalid value set an error message will printed together with
     * the usage of the agent.
     * An additional parameter check is done in order to control the settings
     * and a connector will be initialized with the values of the command line
     * arguments. An agent thread instance will be created and started.
     * There is a loop reading keys input in order to control a small menu. <BR/>
     *
     * @param   argv    The string array with the command line arguments.
     *
     * @see     ibs.di.agent.ImportAgent_01#main
     */
    public static void main (String[] argv)
    {
        ExportAgent_01 agent;
        Thread agentThread;
        InputStreamReader reader;

        // check if there are any arguments
        if (argv.length > 0)
        {
            // create the agent object
            agent = new ExportAgent_01 ();
            // read parameters:
            try
            {
                // loop through the rest of the parameters
                for (int i = 0; i < argv.length; i++)
                {
                    if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HELP) ||
                        argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HELPUNIX) ||
                             argv[i].equalsIgnoreCase ("-?"))
                    {
                        ExportAgent_01.printUsage ();
                        System.exit (-1);
                    } // if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HELP)
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_WAIT))
                    {
                        agent.isWait = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_WAIT))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HIERARCHICAL))
                    {
                        agent.isHierarchical = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HIERACHICAL))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SINGLEFILE))
                    {
                        agent.isSingleFile = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SINGLEFILE))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RECURSIVE))
                    {
                        agent.isRecursive = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RECURSIVE))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_INCLUDECONTAINER))
                    {
                        agent.isIncludeContainer = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_INCLUDECONTAINER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DELETERECURSIVE))
                    {
                        agent.isDeleteRecursive = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DELETERECURSIVE))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DELETE))
                    {
                        agent.isDelete = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DELETE))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RESOLVEKEYMAPPING))
                    {
                        agent.isResolveKeyMapping = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_KEYMAPPING))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RESTOREEXTERNALIDS))
                    {
                        agent.isRestoreExternalIDs = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RESTOREEXTERNALIDS))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RESOLVEQUERY))
                    {
                        agent.isResolveQuery = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RESOLVEQUERY))

                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RESOLVEREFERENCE))
                    {
                        agent.isResolveReference = true;
                    } // if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RESOLVEQUERY))

                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_USEREFERENCESOID))
                    {
                        agent.isUseReferenceOid = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_USEREFERENCESOID))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY))
                    {
                        agent.frequencyStr = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_TIME))
                    {
                        agent.timeStr = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_TIME))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_EVERY))
                    {
                        agent.everyStr = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_EVERY))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SERVER))
                    {
                        // set the name of the m2 server to connect to
                        agent.m2ServerName = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SERVER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_APPPATH))
                    {
                        // set the name of the m2AppPath to connect with
                        agent.m2AppPath = argv[++i];
                        // assure correct slashes
                        if (!agent.m2AppPath.startsWith ("/"))
                        {
                            agent.m2AppPath = "/" + agent.m2AppPath;
                        } // if
                        if (!agent.m2AppPath.endsWith ("/"))
                        {
                            agent.m2AppPath += "/";
                        } // if
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_APPPATH))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION))
                    {
                        // set the name of the connection to use
                        String connectionTypeStr = argv[++i];
                        if (connectionTypeStr.equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION_SERVLET))
                        {
                            agent.isServlet = true;
                        } // if
                        else if (connectionTypeStr.equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION_ASP))
                        {
                            agent.isServlet = false;
                        } // else if
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_USER))
                    {
                        // set the name of the user to connect with
                        agent.userName = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_USER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_PW))
                    {
                        // set the password for the user
                        agent.password = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_PW))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DOMAIN))
                    {
                        // set the domainId for the user
                        agent.domain = Integer.parseInt (argv[++i]);
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DOMAIN))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_PATH) ||
                             argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_WORKDIR))
                    {
                        // set the path of the working directory
                        agent.workingDirectory = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_PATH) ...
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_TEMPDIR))
                    {
                        // set the path of the temp directory
                        agent.tempDirectory = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_TEMPDIR))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_ERRORDIR))
                    {
                        // set the path of the errorfiles directory
                        agent.errorfilesDirectory = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_ERRORDIR))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RETRY))
                    {
                        // set the number of retries
                        agent.retry = Integer.parseInt (argv[++i]);
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_RETRY))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFY))
                    {
                        // activate error notification
                        agent.p_isNotify = argv[++i].equalsIgnoreCase (AgentConstants.NOTIFY_YES);
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFY))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYMAILSERVER))
                    {
                        // set the mail server for error notification
                        agent.p_notifyMailServer = new SMTPServer (argv[++i]);
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYMAILSERVER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYRECEIVER))
                    {
                        // mail address of receiver of error notification mail
                        agent.p_notifyReceiver = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYRECEIVER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYSENDER))
                    {
                        // mail address of sender of error notification mail
                        agent.p_notifySender = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYSENDER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYSUBJECT))
                    {
                        // subject of error notification mail
                        agent.p_notifySubject = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFYSUBJECT))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_OBJECT))
                    {
                        // object is a multiple commandline parameter
                        // to get all objects
                        // add the value to the vector
                        agent.p_objects.addElement (argv[++i]);
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_OBJECT))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_TRANSLATOR))
                    {
                        i++;
                        try
                        {
                            // set oid of the translator to use
                            agent.translatorOid = new OID (argv[i]);
                        } // try
                        catch (IncorrectOidException e)
                        {
                            agent.translatorOid = null;
                            agent.translatorName = argv[i];
                        } // catch
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_TRANSLATOR))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_BACKUPCONNECTOR))
                    {
                        i++;
                        try
                        {
                            // try to set the oid of the connector to use
                            agent.p_backupConnectorOid = new OID (argv[i]);
                        } // try
                        catch (IncorrectOidException e)
                        {
                            agent.p_backupConnectorOid = null;
                            agent.p_backupConnectorName = argv[i];
                        } // catch
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_BACKUPCONNETOR))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_LOGFILENAME))
                    {
                        // set the name of the log file
                        agent.logFileName = argv[++i];
                        // add the log file extension in case it does not already exists
                        if (!agent.logFileName.toLowerCase ().endsWith (DIConstants.LOGFILE_EXTENSION))
                        {
                            agent.logFileName += DIConstants.LOGFILE_EXTENSION;
                        } // if
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_LOGFILENAME))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_LOGPATH))
                    {
                        // set the path of the log
                        agent.logPath = FileHelpers.addEndingFileSeparator (argv[++i]);
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_LOGPATH))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_LOGGING))
                    {
                        // set the logtype
                        String logTypeStr = argv[++i];
                        if (logTypeStr.equalsIgnoreCase (AgentConstants.AGENTARG_LOGGING_NEW))
                        {
                            // logging -NEW
                            agent.isWriteLog = true;
                            agent.isAppendLog = false;
                        }  // if (logTypeStr.equalsIgnoreCase ("NEW"))
                        else if (logTypeStr.equalsIgnoreCase (AgentConstants.AGENTARG_LOGGING_APPEND))
                        {
                            // logging -APPEND
                            agent.isWriteLog = true;
                            agent.isAppendLog = true;
                        } // if (logTypeStr.equalsIgnoreCase ("APPEND"))
                        else // no logging at all
                        {
                            // logging -NONE (default)
                            agent.isWriteLog = false;
                            agent.isAppendLog = false;
                        } // else no logging at all
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_LOGGING))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE))
                    {
                        i++;
                        // is it a File-Connector
                        if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_FILE))
                        {
                            agent.connectorType = DIConstants.CONNECTORTYPE_FILE;
                        } // if
                        // is it a FTP-Connector
                        else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_FTP))
                        {
                            agent.connectorType = DIConstants.CONNECTORTYPE_FTP;
                        } // else if
                        else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_MAIL))
                        {
                            agent.connectorType = DIConstants.CONNECTORTYPE_EMAIL;
                        } // else if
                        else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_SAP))
                        {
                            agent.connectorType = DIConstants.CONNECTORTYPE_SAPBCXMLRFC;
                        } // else if
                        else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_HTTPMULTIPART))
                        {
                            agent.connectorType = DIConstants.CONNECTORTYPE_HTTPMULTIPART;
                        } // else if
                        else
                        {
                            agent.connectorType = DIConstants.CONNECTORTYPE_NONE;
                        } // else
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYP))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPSERVER))
                    {
                        agent.ftpServer = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPSERVER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPUSER))
                    {
                        agent.ftpUser = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPUSER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPPASSWORD))
                    {
                        agent.ftpPassword = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPPASSWORD))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPPATH))
                    {
                        agent.ftpPath = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FTPPATH))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILSERVER))
                    {
                        agent.mailServer = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILSERVER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILRECEIVER))
                    {
                        agent.mailReceiver = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILRECEIVER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILSENDER))
                    {
                        agent.mailSender = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILSENDER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILUSER))
                    {
                        agent.mailUser = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILUSER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILPASSWORD))
                    {
                        agent.mailPassword = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILPASSWORD))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILPROTOCOL))
                    {
                        agent.mailProtocol = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_MAILPROTOCOL))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SAPURL))
                    {
                        agent.sapUrl = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SAPURL))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SAPNAME))
                    {
                        agent.sapName = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SAPNAME))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HTTPMULTIPARTURL))
                    {
                        agent.httpMultipartUrl = argv [++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HTTPMULTIPARTURL))
                    // get user credentials for NTLM authentication
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NTDOMAIN))
                    {
                        agent.p_ntlmDomain =  argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NTDOMAIN))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NTUSER))
                    {
                        agent.p_ntlmUsername =  argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NTUSER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NTPW))
                    {
                        agent.p_ntlmPassword =  argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NTPW))
                    else if (argv[i].equalsIgnoreCase ("-DEBUG"))
                    {
                        agent.isDebug = true;
                    } // else if (argv[i].equalsIgnoreCase ("-DEBUG"))
                    else            // parameter invalid
                    {
                        ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_PARAMETER + argv[i]);
                        // if a parameter is invalid the parameterlist is
                        // indicated
                        ExportAgent_01.printUsage ();
                        System.exit (-1);
                    } // else parameter invalid
                } // for (int i = 0; i < argv.length; i++)
                // All parameters read and now try to initialize the agent
                // and create an agent instance to be started.
                // first check the parameters for validity
                if (agent.checkParameter ())
                {
                    // try to initialize the connector
                    if (agent.initConnector ())
                    {
                        // print the settings
                        agent.printSettings ();
                        // now start the import as a thread
                        agentThread = new Thread (agent, "Exportagent");
                        // do we need to start the agent as a deamon thread???
                        agentThread.setDaemon (false);
                        // now start the thread
                        agentThread.start ();
                        // and wait until the agent finished
                        char i;
                        try
                        {
                            reader =  new InputStreamReader (System.in);
                            while ((i = (char) reader.read ()) != 'x')
                            {
                                // check whether to print the agent setting
                                if (i == 's' || i == 'S')
                                {
                                    agent.printSettings ();
                                    ExportAgent_01.printMenu ();
                                } // if (i == 's' || i == 'S')
                                else if (i == 'a' || i == 'A')
                                {
                                    ExportAgent_01.print ("> " + AgentTokens.TOK_LAST_ACTIVATION_AT + " " +
                                            DateTimeHelpers.dateTimeToString (agent.lastActivationDate) + " ...");
                                    ExportAgent_01.print ("> " + AgentTokens.TOK_NEXT_ACTIVATION_AT + " " +
                                            DateTimeHelpers.dateTimeToString (agent.nextActivationDate) + " ...");
                                    ExportAgent_01.printMenu ();
                                } // else if (i == 'a' || i == 'A')
                                else if (i == 'h' || i == 'H')
                                {
                                    ExportAgent_01.printUsage ();
                                    ExportAgent_01.printMenu ();
                                } // if (i == 'h' || i == 'H')
                                // send the agent for 1 second to sleep
                                // just for case a constant waiting for input consumes to much cpu time
                                Thread.sleep (1000);
                            } // while
                        } // try
                        catch (InterruptedException e)
                        {
                            ExportAgent_01.print ("\r\n" + e.toString ());
                        } // catch
                        catch (IOException e)
                        {
                            ExportAgent_01.print ("\r\n" + e.toString ());
                        } // catch
                        // close the agent
                        agent.close ();
                        // exit from the system
                        System.exit (0);
                    } // if (agent.initConnector())
                    else // could not initialize connector
                    {
                        ExportAgent_01.print (AgentMessages.MSG_INIT_FAILED);
                        ExportAgent_01.printUsage ();
                        System.exit (-1);
                    } // else could not initialize connector
                } // if (agent.checkParameter())
                else    // parameters where invalid
                {
                    ExportAgent_01.print (AgentMessages.MSG_PARAMETERCHECK_FAILED);
                    ExportAgent_01.printUsage ();
                    System.exit (-1);
                } // else parameters where invalid
            } // try
            catch (ArrayIndexOutOfBoundsException e)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_PARAMETER);
                ExportAgent_01.printUsage ();
            } // catch
        } // if argv.length > 0)
        else                            // no arguments
        {
            ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_PARAMETER);
            ExportAgent_01.printUsage ();
        } // else no arguments
    } // main ()


    /**************************************************************************
     * Checks the command line parameter for validity. <BR/>
     * All mandatory parameter must be set. In case they are missing,
     * the user will be promted for input. <BR/>
     * The working directory will be set to the directory where the agent
     * has been started in case no other value has been defined. Otherwise
     * the method checks if the directory is valid that has been set
     * as working directory. <BR/>
     *
     * @return  true if parameters are ok or false otherwise
     */
    private boolean checkParameter ()
    {
        File file;
        String dateTimeStr;
        SimpleDateFormat formatter = new SimpleDateFormat ("dd.MM.yyyy hh:mm");

        // some additional parameter checks could be done here
        if (this.userName.length () == 0)
        {
            ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_USERNAME);
            return false;
        } // if (this.userName.length () == 0)
        // the user has to enter a password
        if (this.password.length () == 0)
        {
            this.getPassword ();
        } // if (this.password.length () == 0)
        // check if there are objects
        if (this.p_objects.isEmpty ())
        {
            ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_OBJECT);
            return false;
        } // if (this.p_objects.isEmpty())
        if (this.domain < 1)
        {
            ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_DOMAINID);
            return false;
        } // if (this.domain.length () == 0)
        if (this.connectorType == DIConstants.CONNECTORTYPE_NONE)
        {
            ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_CONNECTORTYPE);
            return false;
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_NONE)
        else if (this.connectorType == DIConstants.CONNECTORTYPE_FTP)
        {
            if (this.ftpServer.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_FTPSERVER);
                return false;
            } // if (this.ftpServer.length () == 0)
            if (this.ftpUser.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_FTPUSER);
                return false;
            } // if (this.ftpUser.length () == 0)
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_FTP)
        else if (this.connectorType == DIConstants.CONNECTORTYPE_EMAIL)
        {
            if (this.mailServer.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_MAILSERVER);
                return false;
            } // if (this.mailServer.length () == 0)
            if (this.mailReceiver.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_MAILRECEIVER);
                return false;
            } // if (this.mailReceiver.length () == 0)
            if (this.mailUser.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_MAILUSER);
                return false;
            } // if (this.mailUser.length () == 0)
            if (this.mailProtocol.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_MAILPROTOCOL);
                return false;
            } // if (this.mailProtocol.length () == 0)

            // mailprotocol has been set
            // check the mail protocol parameter
            if (!(this.mailProtocol.equalsIgnoreCase (AgentConstants.AGENTARG_MAILPROTOCOL_IMAP) ||
                  this.mailProtocol.equalsIgnoreCase (AgentConstants.AGENTARG_MAILPROTOCOL_POP3)))
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_UNKOWN_MAILPROTOCOL);
                return false;
            } // if (! (this.mailProtocol.equals (AgentConstants.AGENTARG_MAILPROTOCOL_IMAP)) || ...
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_EMAIL)
        else if (this.connectorType == DIConstants.CONNECTORTYPE_SAPBCXMLRFC)
        {
            if (this.sapUrl.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_SAPURL);
                return false;
            } // if (this.mailServer.length () == 0)
            if (this.sapName.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_SAPNAME);
                return false;
            } // if (this.mailReceiver.length () == 0)
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_SAPBCXMLRFC)
        else if (this.connectorType == DIConstants.CONNECTORTYPE_HTTPMULTIPART)
        {
            if (this.httpMultipartUrl.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_HTTPMULTIPARTURL);
                return false;
            } // if (this.httpMultipartUrl.length () == 0)
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_HTTPMULTIPART)
        // check working directory
        if (this.workingDirectory.length () == 0)
        {
            // no workingDirectory provided. Set the current path as
            // workingDirectory.
            file = new File ("");
            // get the absolute path (will be required by the fileConnector)
            this.workingDirectory = file.getAbsolutePath ();
            // set the workingDirectory as log path in case
            // the log should be written and no log path provided
            if (this.isWriteLog && this.logPath.length () == 0)
            {
                this.logPath = this.workingDirectory;
            } // if
        } // if (this.workingDirectory.length () == 0)
        else    // check if provided directory is correct
        {
            // no workingDirectory provided. Set the current path as
            // workingDirectory.
            file = new File (this.workingDirectory);
            if (file.exists () && file.isDirectory ())
            {
                this.workingDirectory = file.getAbsolutePath ();
            } // if
            else    // not a valid directory
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_WORKDIR);
                return false;
            } // else not a valid directory
        } // check if provided directory is correct

        // check temp directory
        // a temp directory is not needed in case a file connector is set
        if (this.connectorType != DIConstants.CONNECTORTYPE_FILE)
        {
            if (this.tempDirectory.length () == 0)
            {
                // set to default which is
                // <workingDirectory>
                this.tempDirectory = this.workingDirectory + AgentConstants.PATH_TEMP + File.separator;

                // check if directory exists if not create it
                if (!FileHelpers.makeDir (this.tempDirectory))
                {
                    ExportAgent_01.print ("\r\n" + AgentMessages.MSG_COULD_NOT_CREATE_TEMPDIR);
                    return false;
                } // if (! FileHelpers.makeDir (this.tempDirectory))
            } // if (this.tempDirectory.length () == 0)
            else    // a temp directory has been set
            {
                file = new File (this.tempDirectory);
                // check if directory exists
                if (file.exists () && file.isDirectory ())
                {
                    this.tempDirectory = file.getAbsolutePath ();
                } // if
                else    // directory set is invalid
                {
                    ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_TEMPDIR);
                    return false;
                } // directory set is invalid
            } // a temp directory has been set
        } // if (this.connectorType != DIConstants.CONNECTORTYPE_FILE)
        // check errorfiles directory setting
        if (this.errorfilesDirectory.length () == 0)
        {
            // set to default which is
            // <workingDirectory>
            this.errorfilesDirectory = this.workingDirectory + AgentConstants.PATH_ERRORFILES + File.separator;

            // check if directory exists if not create it
            if (!FileHelpers.makeDir (this.errorfilesDirectory))
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_COULD_NOT_CREATE_ERRORDIR);
                return false;
            } // if (! FileHelpers.makeDir (this.tempDirectory))
        } // if (this.errorDirectory.length () == 0)
        else    // a temp directory has been set
        {
            file = new File (this.errorfilesDirectory);
            // check if directory exists
            if (file.exists () && file.isDirectory ())
            {
                this.errorfilesDirectory = file.getAbsolutePath ();
            } // if
            else    // directory set is invalid
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_ERRORDIR);
                return false;
            } // directory set is invalid
        } // a errorfiles directory has been set

        // check retry settings
        if (this.retry < 1)
        {
            ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_RETRY);
            return false;
        } // if (this.retry < 0)
        // check notification
        if (this.p_isNotify)
        {
/* BB deactivated: because error notification via m2 does not require mailserver
            // mailserver must be set
            if (this.p_notifyMailServer.length () == 0)
            {
                print ("\r\n" + AgentMessages.MSG_MISSING_NOTIFYMAILSERVER);
                return false;
            } // if (this.notifyMailServer.length () == 0)
*/
            // mail receiver must be set
            if (this.p_notifyReceiver.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_NOTIFYRECEIVER);
                return false;
            } // if (this.notifyMailServer.length () == 0)
            if (this.p_notifySender.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_NOTIFYSENDER);
                return false;
            } // if (this.notifyMailServer.length () == 0)
        } // if (this.isNotify)
        // check frequency settings
        if (this.frequencyStr.length () > 0)
        {
            // check frequency type
            if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_ONCE))
            {
                // set frequency type to once
                this.frequencyType = AgentConstants.FREQUENCYTYPE_ONCE;
            } //  if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_ONCE))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_MINUTES))
            {
                // set frequency type to minutes
                this.frequencyType = AgentConstants.FREQUENCYTYPE_MINUTES;
                // check minutes value
                try
                {
                    this.everyMinutes = Integer.parseInt (this.everyStr);
                    // the value for everyMinutes must be at least 5 minutes
                    if (this.everyMinutes < 1)
                    {
                        this.everyMinutes = 1;
                    } // if
                } // try
                catch (NumberFormatException e)
                {
                    ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_EVERY);
                    return false;
                } // catch (NumberFormatException e)
            } // if (this.frequencyStr.equalsIgnoreCase ("MINUTES"))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_DAY))
            {
                // set frequency type to minutes
                this.frequencyType = AgentConstants.FREQUENCYTYPE_DAY;
                // check time value
                dateTimeStr = "01.01.1970 " + this.timeStr;

                try
                {
                    this.everyTime.setTime (formatter.parse (dateTimeStr));
                } // try
                catch (ParseException e)
                {
                    ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_TIME);
                    return false;
                } // catch (ParseException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("DAY"))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_WEEK))
            {
                // set frequency type to minutes
                this.frequencyType = AgentConstants.FREQUENCYTYPE_WEEK;
                // set the weekdays
                if (this.everyStr.toUpperCase ().indexOf (AgentConstants.WEEKDAY_MONDAY) > -1)
                {
                    this.everyWeekday [1] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("MO") > -1)
                if (this.everyStr.toUpperCase ().indexOf (AgentConstants.WEEKDAY_TUESDAY) > -1)
                {
                    this.everyWeekday [2] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("TU") > -1)
                if (this.everyStr.toUpperCase ().indexOf (AgentConstants.WEEKDAY_WEDNESDAY) > -1)
                {
                    this.everyWeekday [3] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("WE") > -1)
                if (this.everyStr.toUpperCase ().indexOf (AgentConstants.WEEKDAY_THURSDAY) > -1)
                {
                    this.everyWeekday [4] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("TH") > -1)
                if (this.everyStr.toUpperCase ().indexOf (AgentConstants.WEEKDAY_FRIDAY) > -1)
                {
                    this.everyWeekday [5] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("FR") > -1)
                if (this.everyStr.toUpperCase ().indexOf (AgentConstants.WEEKDAY_SATURDAY) > -1)
                {
                    this.everyWeekday [6] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("SA") > -1)
                if (this.everyStr.toUpperCase ().indexOf (AgentConstants.WEEKDAY_SUNDAY) > -1)
                {
                    this.everyWeekday [0] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("SU") > -1)
                // check time value
                dateTimeStr = "01.01.1970 " + this.timeStr;
                try
                {
                    this.everyTime.setTime (formatter.parse (dateTimeStr));
                } // try
                catch (ParseException e)
                {
                    ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_TIME);
                    return false;
                } // catch (ParseException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("WEEK"))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_MONTH))
            {
                // set frequency type to minutes
                this.frequencyType = AgentConstants.FREQUENCYTYPE_MONTH;
                // check the day of the month
                // check time value
                dateTimeStr = "01.01.1970 " + this.timeStr;
                try
                {
                    this.everyDay = Integer.parseInt (this.everyStr);
                    // the value for everyMinutes must be at least 5 minutes
                    if (this.everyDay < 1)
                    {
                        this.everyDay = 1;
                    } // if
                    else if (this.everyDay > 31)
                    {
                        this.everyDay = 31;
                    } // else if
                    // try to create the date
                    this.everyTime.setTime (formatter.parse (dateTimeStr));
                } // try
                catch (ParseException e)
                {
                    ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_TIME);
                    return false;
                } // catch (ParseException e)
                catch (NumberFormatException e)
                {
                    ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_EVERY);
                    return false;
                } // catch (NumberFormatException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("MONTH"))
            else    // unkown frequency type
            {
                // invalidate the frequency type by setting it to -1
                this.frequencyType = -1;
            } // unkown frequency type
        } // else if (this.frequencyStr.length () > 0)
        if (this.frequencyType < 0)
        {
            ExportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_FREQUENCY);
            return false;
        } // else if (this.frequencyType < 0)
        // in case frequency type ONCE is set the wait flag will be deactivated
        if (this.frequencyType == AgentConstants.FREQUENCYTYPE_ONCE)
        {
            this.isWait = false;
        } // if (this.frequencyType == AgentConstants.FREQUENCYTYPE_ONCE)
        if (this.isAppendLog)
        {
            // check if an logname has been provides
            if (this.logFileName == null || this.logFileName.length () == 0)
            {
                ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_LOGFILENAME);
                return false;
            } // if (this.logFileName == null || this.logFileName.length () == 0)
        } // if (this.isAppendLog)
        return true;
    } // checkParameter


    /**************************************************************************
     * Set and initializes the connector based on the arguments from the
     * command line. <BR/>
     *
     * @return  true if the connector could have been initialized or
     *          false otherwise
     */
    private boolean initConnector ()
    {
        FileConnector_01 fileConnector;
        FTPConnector_01 ftpConnector;
        MailConnector_01 mailConnector;
        SAPBCXMLRFCConnector_01 sapbcConnector;
        HTTPMultipartConnector_01 httpMulitpartConnector;

        // check if the connector has already been set
        if (this.connector == null)
        {
            // create the connector type that has been set
            switch (this.connectorType)
            {
                case DIConstants.CONNECTORTYPE_FILE:
                    // create a file connector
                    fileConnector = new FileConnector_01 ();
                    this.connector = fileConnector;
                    // set path for the connector
                    // for file connectors this will be the working directory
                    this.connector.setPath (this.workingDirectory);
                    break;
                case DIConstants.CONNECTORTYPE_FTP:
                    // create a FTP connector and set its properties
                    ftpConnector = new FTPConnector_01 ();
                    ftpConnector.ftpServer = this.ftpServer;
                    ftpConnector.ftpUser = this.ftpUser;
                    ftpConnector.ftpPassword = this.ftpPassword;
                    ftpConnector.ftpPath = this.ftpPath;
                    this.connector = ftpConnector;
                    // set path for the connector
                    this.connector.setPath (this.tempDirectory);
                    break;
                case DIConstants.CONNECTORTYPE_EMAIL:
                    // create a mail connector and set its properties
                    mailConnector = new MailConnector_01 ();
                    mailConnector.mailServer = this.mailServer;
                    mailConnector.mailRecipient = this.mailReceiver;
                    mailConnector.mailSender  = this.mailSender;
                    mailConnector.mailProtocol  = this.mailProtocol;
                    mailConnector.mailUser  = this.mailUser;
                    mailConnector.mailPassword  = this.mailPassword;
                    this.connector = mailConnector;
                    // set path for the connector
                    this.connector.setPath (this.tempDirectory);
                    break;
                case DIConstants.CONNECTORTYPE_SAPBCXMLRFC:
                    // create a sap business connector connector and set its properties
                    sapbcConnector = new SAPBCXMLRFCConnector_01 ();
                    sapbcConnector.p_sapBCGatewayUrl = this.sapUrl;
                    sapbcConnector.p_repServerName = this.sapName;
                    this.connector = sapbcConnector;
                    // set path for the connector
                    this.connector.setPath (this.tempDirectory);
                    break;
                case DIConstants.CONNECTORTYPE_HTTPMULTIPART:
                    // create a http multipart connector and set its properties
                    httpMulitpartConnector = new HTTPMultipartConnector_01 ();
                    httpMulitpartConnector.p_exportUrl = this.httpMultipartUrl;
                    this.connector = httpMulitpartConnector;
                    // set path for the connector
                    this.connector.setPath (this.tempDirectory);
                    break;
                default:
                    break;
            } // switch (this.connectorType)

            // now init the connector and try to get the files
            try
            {
                // suppress the creation of a temp directory by the connector
                this.connector.isCreateTemp = false;
                // init the connector
                this.connector.initConnector ();

                return true;
            } // try
            catch (ConnectionFailedException e)
            {
                ExportAgent_01.print (e.toString ());
                this.connector = null;
                return false;
            } // catch (ConnectionFailedException e)
        } // if (this.connector == null)

        // connector already set
        return true;
    } // initConnector


    /**************************************************************************
     * Implements the run method used for threads. The agent creates its
     * temporary directory first. the agent will be activated and tries to
     * invoke an export on the m2 server specified.
     * The date and time of the next activation is calculated and the
     * agent is send to sleep until this the next activation date. <BR/>
     */
    public void run ()
    {
        Date actualDate = null;
        Date nextActivationDate = null;
        long timeToSleep;
        boolean isFirstActivation = true;

        // endless loop for agent
        while (!this.isInterrupted ())
        {
            // check if this is the first activation and if the agent
            // should be activated or should wait first
            // this can be switched on and off through the -WAIT parameter
            // in the command line
            if (!(isFirstActivation && this.isWait))
            {
                // activate the agent
                ExportAgent_01.print ("\r\n>>> " + AgentMessages.MSG_AGENT_ACTIVATED);
                // start the export
                this.startExport ();
            } // if (! (isFirstActivation && this.isWait))
            else    // mark that first activation is over
            {
                isFirstActivation = false;
            } // else mark that first activation is over
            // check if the import should only run once
            if (this.frequencyType == AgentConstants.FREQUENCYTYPE_ONCE)
            {
                // close the agent
                this.close ();
                // exit the agent
                System.exit (0);
            } // if (this.frequencyType == AgentConstants.FREQUENCYTYPE_ONCE)
            // calculate the time the thread can sleep until next activation
            actualDate = new Date ();
            nextActivationDate = this.getNextActivationDate ();
            // store the old and the next activation date
            this.lastActivationDate = actualDate;
            this.nextActivationDate = nextActivationDate;
            // calculate the time to sleep for the agent
            timeToSleep = nextActivationDate.getTime () - actualDate.getTime ();

            ExportAgent_01.print ("> " + AgentTokens.TOK_NEXT_ACTIVATION_AT + " " +
                    DateTimeHelpers.dateTimeToString (this.nextActivationDate) + " ...");
            // print the menu keys
            ExportAgent_01.printMenu ();
            try
            {
                // send the thread to sleep ...
                Thread.sleep (timeToSleep);
            } // try
            catch (InterruptedException e)
            {
                this.close ();
                System.exit (-1);
            } // catch
        } // while
        // close the agent
        this.close ();
    } // run


    /**************************************************************************
     * Closes the agent and the connector. After the export this method removes
     * the temporary directory. <BR/>
     */
    public void close ()
    {
        // close the connector
        if (this.connector != null)
        {
            this.connector.close ();
        } // if
        // remove temporary directory after the connector
        // has finished the export
        this.deleteTempDir ();
        // remove errorfiles directory in case it is empty
        this.deleteErrorDir ();
        ExportAgent_01.print ("\r\n" + AgentMessages.MSG_AGENT_CLOSED);
    } // close


    /**************************************************************************
     * Perform the export with the arguments set via the command line. <BR/>
     * Before the agent starts the export, a temporary directory will be created
     * within the working directory. This temporary directory is used by the m2
     * server to write the generated export files into.
     * The agents acts like a web client and sends an HTTP request to the
     * m2 server sendig the parameter for the export within the querystring.
     * A certain function is called at the m2 server that will first perform
     * a login with the username and password given and will start the export,
     * in case the login has been sucessfull.
     * The agent reads the response from the webserver and prints it to the command
     * line. In case there are files they will be written to the connector.
     *
     * Note that in the case of errors it is possible that we get HTTP output
     * from the server. The function called at the m2 server will set a flag
     * that supresses the generation of HTML output but the flag does not
     * yet affect the output in case of error.
     * This should be improved in a future version of m2. <BR/>
     */
    private void startExport ()
    {
        URL url = null;
        String loginQueryStr = "";
        String exportQueryStr = "";
        String m2ASPServerUrl;
        String m2ServletServerUrl;
        String inputLine;
        HttpURLConnection connection;
        BufferedReader bufferedReader;

        try
        {
            // construct the URL to the m2 server
            m2ASPServerUrl = IOConstants.URL_HTTP + this.m2ServerName + this.m2AppPath + "m2get.asp";
// KR HACK: The check for http and https is missing!!!
// KR HACK: There should not be the name of the servlet!!!
            m2ServletServerUrl = IOConstants.URL_HTTP + this.m2ServerName +
                this.m2AppPath + "ApplicationServlet";

            // we need to login first into the m2 application
            ExportAgent_01.print (">>> " + AgentMessages.MSG_TRY_EXPORT_LOGIN);

            // check if we need to send the data needed for a servlet environment
            if (this.isServlet)
            {
                loginQueryStr = "?" + BOArguments.ARG_PATH + "=" +
                    IOHelpers.urlEncode (this.m2ServerName + this.m2AppPath) + "&";
            } // if
            else
            {
                loginQueryStr = "?";
            } // else
            // construct the query string for the login
            loginQueryStr += BOArguments.ARG_USERNAME + "=" +
                IOHelpers.urlEncode (this.userName) +
                "&" +
                BOArguments.ARG_PASSWORD + "=" +
                IOHelpers.urlEncode (this.password) +
                "&" +
                BOArguments.ARG_DOMAIN + "=" +
                IOHelpers.urlEncode ("" + this.domain) +
                "&" +
                BOArguments.ARG_FUNCTION + "=" +
                IOHelpers.urlEncode ("" + AppFunctions.FCT_AGENTLOGINEXPORT);
            // set the translator oid or the name if applicable
            if (this.translatorOid != null)
            {
                exportQueryStr += "&" + DIArguments.ARG_TRANSLATOR + "=" +
                    IOHelpers.urlEncode (this.translatorOid.toString ());
            } // if (this.translatorOid != null)
            else    // try to set translator name if available
            {
                if (this.translatorName.length () > 0)
                {
                    exportQueryStr += "&" + DIArguments.ARG_TRANSLATORNAME + "=" +
                            IOHelpers.urlEncode (this.translatorName);
                } // if
            } // else try to set translator name if available

            // set the backup connector:
            if (this.p_backupConnectorOid != null ||
                this.p_backupConnectorName.length () > 0)
            {
                exportQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_CREATEBACKUP, true);

                // set the backup connector oid or the name if applicable:
                if (this.p_backupConnectorOid != null)
                {
                    exportQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_BACKUPCONNECTOR,
                        this.p_backupConnectorOid.toString ());
                } // if (this.connectorOid != null)
                else if (this.p_backupConnectorName.length () > 0)
                                            // backup connector name set?
                {
                    exportQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_BACKUPCONNECTORNAME,
                        this.p_backupConnectorName);
                } // else if backup connector name set
            } // if

             // set the path of the log file
            exportQueryStr += "&" + DIArguments.ARG_LOGFILEPATH + "=" +
                    IOHelpers.urlEncode (this.logPath);
            // set the name of the log file
            exportQueryStr += "&" + DIArguments.ARG_LOGFILENAME + "=" +
                    IOHelpers.urlEncode (this.logFileName);
            // set the write log flag
            exportQueryStr += "&" + DIArguments.ARG_WRITELOGFILE + "=" +
                    IOHelpers.urlEncode ("" + this.isWriteLog);
            // set the flag to display the log (always true)
            exportQueryStr += "&" + DIArguments.ARG_DISPLAYLOGFILE + "=" +
                    IOHelpers.urlEncode ("" + true);
            // set the append log flag
            exportQueryStr += "&" + DIArguments.ARG_APPENDLOGFILE + "=" +
                    IOHelpers.urlEncode ("" + this.isAppendLog);
            // set the flag of singlefile
            exportQueryStr += "&" + DIArguments.ARG_EXPORTSINGLEFILE + "=" +
                    IOHelpers.urlEncode ("" + this.isSingleFile);
            // set the flag for recursive export
            exportQueryStr += "&" + DIArguments.ARG_EXPORTCONTENTRECURSIVE + "=" +
                    IOHelpers.urlEncode ("" + this.isRecursive);
            // set the flag if also a container should be exported
            exportQueryStr += "&" + DIArguments.ARG_EXPORTCONTAINER + "=" +
                    IOHelpers.urlEncode ("" + this.isIncludeContainer);
            // set the flag hierarchical export
            exportQueryStr += "&" + DIArguments.ARG_HIERARCHICALEXPORT + "=" +
                    IOHelpers.urlEncode ("" + this.isHierarchical);
            // set the flag if the objects should be deleted
            exportQueryStr += "&" + DIArguments.ARG_ISDELETE + "=" +
                    IOHelpers.urlEncode ("" + this.isDelete);
            // set the flag if the subobjects should be deleted
            exportQueryStr += "&" + DIArguments.ARG_ISDELETERECURSIVE + "=" +
                    IOHelpers.urlEncode ("" + this.isDeleteRecursive);
            // set the flag for KeyMapping
            exportQueryStr += "&" + DIArguments.ARG_ISRESOLVEKEYMAPPING + "=" +
                    IOHelpers.urlEncode ("" + this.isResolveKeyMapping);
            // set the flag for restoring external oid if applicable
            exportQueryStr += "&" + DIArguments.ARG_ISRESTOREEXTERNALID + "=" +
                    IOHelpers.urlEncode ("" + this.isRestoreExternalIDs);
            // set the flag for resolving queries
            exportQueryStr += "&" + DIArguments.ARG_ISRESOLVEQUERY + "=" +
                    IOHelpers.urlEncode ("" + this.isResolveQuery);
            // set the flag for resolving references
            exportQueryStr += "&" + DIArguments.ARG_ISRESOLVEREFERENCE + "=" +
                    IOHelpers.urlEncode ("" + this.isResolveReference);
            // set the flag for using reference oid when resolving references
            exportQueryStr += "&" + DIArguments.ARG_ISUSEREFERENCEOID + "=" +
                    IOHelpers.urlEncode ("" + this.isUseReferenceOid);
            // set the path to write the export files to
            exportQueryStr += "&" + DIArguments.ARG_EXPORTPATH + "=" +
                    IOHelpers.urlEncode (this.connector.getPath ());
            // set the notify option
            exportQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFY, this.p_isNotify);
            // set notify sender
            exportQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFYSENDER, this.p_notifySender);
            // set notify receiver
            exportQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFYRECEIVER, this.p_notifyReceiver);
            // set notify subject
            exportQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFYSUBJECT, this.p_notifySubject);

            // add the objects that are selected:
            String objectStr;
            for (Iterator<String> iter = this.p_objects.iterator (); iter.hasNext ();)
            {
                objectStr = iter.next ();
                exportQueryStr += "&" + DIArguments.ARG_OBJECT + "=" +
                        IOHelpers.urlEncode (objectStr);
            } // for iter

            // check if we need to construct an url to a servlet connection
            if (this.isServlet)
            {
                url = new URL (m2ServletServerUrl + loginQueryStr + exportQueryStr);
            } // if
            else
            {
                url = new URL (m2ASPServerUrl + loginQueryStr + exportQueryStr);
            } // else

            // open a http url connection:
            connection = (HttpURLConnection) url.openConnection ();
            // set the connection for input/output
            connection.setDoInput (true);
            connection.setDoOutput (true);

            connection.setAllowUserInteraction (false);
            connection.connect ();

            // did we get an 401 UNAUTHORIZED code back from the server?
            if (connection.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                // try to negotiate an NTLM authentication
                connection = NTLMClient.negotiateNTLM (url, this.p_ntlmDomain,
                    this.p_ntlmUsername, this.p_ntlmPassword, null);
            } // if (connection.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED)

            // check if we have a valid connection:
            if (connection != null)     // valid connection?
            {
                // now open a read connection
                bufferedReader = new BufferedReader (
                    new InputStreamReader (connection.getInputStream (), DIConstants.CHARACTER_ENCODING));
                // read the result from the login process.
                while ((inputLine = bufferedReader.readLine ()) != null)
                {
                    // print the output we got from the server
                    ExportAgent_01.print (inputLine);
                } // while

                // close the reader:
                bufferedReader.close ();
                // close the connection
                connection.disconnect ();
                // now write the files to the connector
                this.writeFiles ();
                // check if any error file exceeded the retry limit
                this.checkErrorFiles ();
                // and delete the content of the temporary directory
                this.emptyTempDir ();
            } // if valid connection
        } // try
        catch (MalformedURLException e)
        {
            IOHelpers.printError ("startExport ABORTED", this, e, true);
        } // catch
        catch (IOException e)
        {
            IOHelpers.printError ("startExport ABORTED", this, e, true);
        } // catch
        catch (NTLMException e)
        {
            IOHelpers.printError ("startExport ABORTED", this, e, true);
        } // catch
    } // startExport


    /**************************************************************************
     * Reads the content of a directory and writes the files to the connector
     * set within the agent instance. <BR/>
     * The email connector is not able to send an attachement and will therefore
     * only send the xml exportfiles.
     * All other connectors support writing of attachements. <BR/>
     * Note that in case a fileconnector has been used the method does not
     * need to write the files again because the files will automatically
     * be written into the correct directory. <BR/>
     */
    private void writeFiles ()
    {
        String [] files = null;

        // check if connector is a file connector
        if (this.connector instanceof FileConnector_01)
        {
            // this means we are ready because the files have been
            // exported to the working directory of the agent
            return;
        } // if (this.connector instanceof ibs.di.FileConnector_01)

        ExportAgent_01.print (">>> " + AgentMessages.MSG_WRITING_FILES_TO_CONNECTOR);
        // check if the connector is a mailconnector
        // in that case all non XML files must be created as attachments
        if (this.connector instanceof MailConnector_01)
        {
            // note that this can cause a problem in case
            // additional XML files have been written
            files = FileHelpers.getFilesArray (this.connector.getPath ());
            if (files != null)
            {
                // loop through the files
                for (int i = 0; i < files.length; i++)
                {
                    // check if this is not an XML file
                    if (!files [i].toUpperCase ().endsWith (".XML"))
                    {
                        try
                        {
                            // write the files to the connector. this means that they
                            // will be added as attachments
                            this.connector.writeFile (this.connector.getPath (), files [i]);
                            // delete the file from the list of files to be exported
                            files [i] = null;
                        } // try
                        catch (ConnectionFailedException e)
                        {
                            // add the file to the vector of errorfiles
                            // in order to avoid deleting the file
                            this.addErrorFile (files [i], e.getMessage ());
                        } // catch
                    } // if (! files [i].toUpperCase ().endsWith (".XML"))
                } // for (int i = 0; i < files.length; i++)
            } // if (files != null)
        } // if (this.connector.instanceof (ibs.di.MailConnector_01))
        // Check if the connector is a http multipart connector. In that case
        // all non XML files must also be added to the output stream:
        else if (this.connector instanceof HTTPMultipartConnector_01)
        {
            // note that this can cause a problem in case additional XML files
            // have been written
            files = FileHelpers.getFilesArray (this.connector.getPath ());
            if (files != null)
            {
                // loop through the files
                for (int i = 0; i < files.length; i++)
                {
                    // check if this is not an XML file
                    if (!files [i].toUpperCase ().endsWith (".XML"))
                    {
                        try
                        {
                            // write the files to the connector. this means that
                            // they will be added to the output stream:
                            this.connector.writeFile (this.connector.getPath (), files [i]);
                            // delete the file from the list of files to be exported
                            files [i] = null;
                        } // try
                        catch (ConnectionFailedException e)
                        {
                            // add the file to the vector of errorfiles
                            // in order to avoid deleting the file
                            this.addErrorFile (files [i], e.getMessage ());
                        } // catch
                    } // if (! files [i].toUpperCase ().endsWith (".XML"))
                } // for (int i = 0; i < files.length; i++)
            } // if (files != null)
        } // if (this.connector.instanceof (ibs.di.HTTPMultipartConnector_01))
        else // other connector type
        {
            // get all files from the directory
            files = FileHelpers.getFilesArray (this.connector.getPath ());
        } // else connector is a mailconnector

        // check if we have any files to export
        if (files != null)
        {
            // loop through the files
            for (int i = 0; i < files.length; i++)
            {
                // check if we have a valid entry
                if (files [i] != null)
                {
                    // check if a translator has been activated
                    if (this.translatorName.length () > 0 || this.translatorOid != null)
                    {
                        // check if the file is an original file before translation
                        // this must be done because such files will also appear
                        // in the temp directory
                        // if yes delete it from the files array
                        if (files [i].startsWith ("o_"))
                        {
                            files [i] = null;
                        } // if (files [i].startsWith ("o_"))
                    } // if (this.translatorName.length () > 0 || this.translatorOid != null)
                    // check again if the entry is still valid
                    if (files [i] != null)
                    {
                        try
                        {
                            // write the files to the connector
                            this.connector.write (files [i]);
                            // create an error message
                            ExportAgent_01.print (AgentMessages.MSG_FILE_WRITTEN +
                                ":\r\n " +
                                this.connector.getPath () + files [i]);
                            // check if external ids should be restored
                            // this can only be done when the connector has
                            // a response where the external ids can be read from
                            if (this.isRestoreExternalIDs)
                            {
                                String responseStr = this.connector.getResponseStr ();
                                // check if the connector has a response
                                if (responseStr != null && (responseStr.length () > 0))
                                {
                                    // send the response string to the m2 application
                                    this.sendResponse (responseStr);
                                } // if (responseStr != null && (responseStr.length () > 0))
                            } // if (this.isRestoreExternalIDs)
                        } // try
                        catch (ConnectionFailedException e)
                        {
                            // add the file to the vector of errorfiles
                            // in order to avoid deleting the file
                            this.addErrorFile (files [i], e.getMessage ());
                        } // catch
                        // check if we need to wait
                        // in case of an SAPConnector we wait in order to avoid
                        // a NULLPOINTER Exception
                        // this seems to be a problem of the XMLRFC Interface of the
                        // SAP Business Connector
                        if (this.connectorType == DIConstants.CONNECTORTYPE_SAPBCXMLRFC)
                        {
                            try
                            {
                                // wait 5 seconds.
                                Thread.sleep (5000);
                            } // try
                            catch (InterruptedException e)
                            {
                                this.close ();
                                System.exit (-1);
                            } // catch
                        } // if (this.connectorType == DIConstants.CONNECTORTYPE_SAPBCXMLRFC)
                    } // if (files [i] != null)
                } // if (files [i] != null)
            } // for (int i = 0; i < files.length ; i++)
        } // if (files != null)
    } // writeFiles


    /**************************************************************************
     * Send a response we got from a connector to the m2 system. <BR/>
     *
     * @param responseStr   the string containing the response to send
     */
    private void sendResponse (String responseStr)
    {
        URL url = null;
        String loginQueryStr = "";
        String exportQueryStr = "";
        String m2ServletServerUrl;
        String inputLine;
        HttpURLConnection connection;
        BufferedReader bufferedReader;

        try
        {
            // construct the URL to the m2 server with servlet support
// KR HACK: The check for http and https is missing!!!
// KR HACK: There should not be the name of the servlet!!!
            m2ServletServerUrl = IOConstants.URL_HTTP +
                this.m2ServerName + this.m2AppPath + "ApplicationServlet";

            // we need to login first into the m2 application
            ExportAgent_01.print (">>> " + AgentMessages.MSG_TRY_EXPORT_LOGIN);

            // check if we need to send the data needed for a servlet environment
            if (this.isServlet)
            {
                loginQueryStr = "?" + BOArguments.ARG_PATH + "=" +
                    IOHelpers.urlEncode (this.m2ServerName + this.m2AppPath) + "&";
            } // if
            else
            {
                loginQueryStr = "?";
            } // else
            // construct the query string for the login
            loginQueryStr += BOArguments.ARG_USERNAME + "=" +
                IOHelpers.urlEncode (this.userName) +
                "&" +
                BOArguments.ARG_PASSWORD + "=" +
                IOHelpers.urlEncode (this.password) +
                "&" +
                BOArguments.ARG_DOMAIN + "=" +
                IOHelpers.urlEncode ("" + this.domain) +
                "&" +
                BOArguments.ARG_FUNCTION + "=" +
                IOHelpers.urlEncode ("" + AppFunctions.FCT_AGENTLOGINRESTOREEXTERNALIDS);
             // set the response string
            exportQueryStr += "&" + DIArguments.ARG_RESPONSE + "=" +
                    IOHelpers.urlEncode (responseStr);
            url = new URL (m2ServletServerUrl + loginQueryStr + exportQueryStr);

            // open a http url connection
            connection = (HttpURLConnection) url.openConnection ();
            connection.setDoInput (true);
            connection.setDoOutput (true);

            // now open a read connection
            bufferedReader = new BufferedReader (
                new InputStreamReader (connection.getInputStream ()));
            // read the result from the login process.
            while ((inputLine = bufferedReader.readLine ()) != null)
            {
                // print the output we got from the server
                ExportAgent_01.print (inputLine);
            } // while ((inputLine = bufferedReader.readLine ()) != null)

            // close the reader
            bufferedReader.close ();
            // close the connection
            connection.disconnect ();
        } // try
        catch (MalformedURLException e)
        {
            ExportAgent_01.print ("ABORT: " + e.getMessage ());
        } // catch
        catch (IOException e)
        {
            ExportAgent_01.print ("ABORT: " + e.getMessage ());
        } // catch
        catch (Exception e)
        {
            ExportAgent_01.print (e.toString ());
        } // catch
    } // sendResponse


    /**************************************************************************
     * Deletes the files from the temporary directory, after the files are given
     * to the connector. The temporary directory on its own is deleted after the
     * export is finished. <BR/>
     */
    private void emptyTempDir ()
    {
        // first check if the connector is a fileconnector
        // in that case the directory the files
        // have been written to does not have to be deleted
        // if not delete the content of the temporary directory
        if (!(this.connector instanceof FileConnector_01))
        {
            // delete to content of the directory
            String [] tempFiles = FileHelpers.getFilesArray (this.tempDirectory);
            // check if the temporary directory contains any files
            if (tempFiles != null)
            {
                // loop through the files array and delete the files
                for (int i = 0; i < tempFiles.length; i++)
                {
                    // first check if the file is an error file
                    if (!this.isErrorFile (tempFiles [i]))
                    {
                        if (!FileHelpers.deleteFile (this.tempDirectory + tempFiles[i]))
                        {
                            // create an error message
                            ExportAgent_01.print (AgentMessages.MSG_COULD_NOT_DELETE_FILE + ":\r\n " +
                                this.tempDirectory + tempFiles[i]);
                        } // if (FileHelpers.deleteFile (tempDir + tempFiles[i]))
                    } // if (! isErrorFile (tempFiles[i])
                } // for (int i = 0; i < tempFiles.length ; i++)
            } // if (tempFiles != null)
        } // if (! (this.connector instanceof ibs.di.FileConnector_01))
    } // emptyTempPath


   /**************************************************************************
    * After the export is finished the temporary directory will be deleted. <BR/>
    */
    private void deleteTempDir ()
    {
        // first check if the connector is a fileconnector
        // in that case the directory the files
        // have been written to does not have to be deleted
        // if not delete the temporary directory
        if (!(this.connector instanceof FileConnector_01))
        {
            // check if error files exist
            // if yes we are not allowed to delete the temporary directory
            if (this.errorFiles != null && this.errorFiles.size () > 0)
            {
                ExportAgent_01.print (AgentMessages.MSG_ERROR_FILES_EXISTS);
            } // if (this.errorFiles != null && this.errorFiles.size () > 0)
            else    // no error files
            {
                // delete the temp directory
                if (!FileHelpers.deleteDir (this.tempDirectory))
                {
                    ExportAgent_01.print (AgentMessages.MSG_COULD_NOT_DELETE_TEMP_DIR);
                } // if
            } // no error files
        } // if (! (this.connector instanceof ibs.di.FileConnector_01))
    } // deleteTempDir


   /**************************************************************************
    * Delete the errorfiles directory in case it is not empty. <BR/>
    */
    private void deleteErrorDir ()
    {
        // check if error files exist
        // if yes we are not allowed to delete the ERRORFILES directory
        String [] files;
        // get the files in the error directory
        files = FileHelpers.getFilesArray (this.errorfilesDirectory);
        // check if the error directory contains any files
        if (files == null || files.length == 0)
        {
            if (!FileHelpers.deleteDir (this.errorfilesDirectory))
            {
                ExportAgent_01.print (AgentMessages.MSG_COULD_NOT_DELETE_ERROR_DIR);
            } // if
        } // if (files == null || files.length = 0)
    } // deleteErrorDir


    /**************************************************************************
     * Calculates the next date to activate the agent. <BR/>
     *
     * @return      the next date to activate the agent
     */
    private Date getNextActivationDate ()
    {
        GregorianCalendar actualCalendar = new GregorianCalendar ();
        GregorianCalendar newCalendar = new GregorianCalendar ();

        // determine if the export ist done frequently or at a certain time
        switch (this.frequencyType)
        {
            case AgentConstants.FREQUENCYTYPE_MINUTES:
                // type is MINUTES
                newCalendar.add (Calendar.MINUTE, this.everyMinutes);
                break;
            case AgentConstants.FREQUENCYTYPE_DAY:
                // type is DAY
                // set the time
                newCalendar.set (Calendar.HOUR, this.everyTime.get (Calendar.HOUR));
                newCalendar.set (Calendar.HOUR_OF_DAY, this.everyTime.get (Calendar.HOUR_OF_DAY));
                newCalendar.set (Calendar.MINUTE, this.everyTime.get (Calendar.MINUTE));
                newCalendar.set (Calendar.AM_PM, this.everyTime.get (Calendar.AM_PM));
                // check if we need to add a day
                if (!actualCalendar.before (newCalendar))
                {
                    newCalendar.add (Calendar.DAY_OF_MONTH, 1);
                } // if (newCalendar.after (actualCalendar))
                break;
            case AgentConstants.FREQUENCYTYPE_WEEK:
                // type is WEEK
                // set the time
                newCalendar.set (Calendar.HOUR, this.everyTime.get (Calendar.HOUR));
                newCalendar.set (Calendar.MINUTE, this.everyTime.get (Calendar.MINUTE));
                newCalendar.set (Calendar.AM_PM, this.everyTime.get (Calendar.AM_PM));
                // check if we need to add a day
                if (!actualCalendar.before (newCalendar))
                {
                    newCalendar.add (Calendar.DAY_OF_MONTH, 1);
                } // if (newCalendar.after (actualCalendar))#
                // check if a weekday filter has been set
                if (this.isWeekdaySet)
                {
                    // now test is this day is one of the weekdays that have been set
                    // BB HINT: because there must has been a weekday set we can assume
                    // that this loop comes to an end
                    while (!this.everyWeekday[newCalendar.get (Calendar.DAY_OF_WEEK) - 1])
                    {
                        newCalendar.add (Calendar.DATE, 1);
                    } // while (!this.everyWeekday [newCalendar.get(Calendar.DAY_OF_WEEK)-1])
                } // if (this.isWeekdaySet)
                break;
            case AgentConstants.FREQUENCYTYPE_MONTH:
                // type is MONTH
                // set the time
                newCalendar.set (Calendar.HOUR, this.everyTime.get (Calendar.HOUR));
                newCalendar.set (Calendar.MINUTE, this.everyTime.get (Calendar.MINUTE));
                newCalendar.set (Calendar.AM_PM, this.everyTime.get (Calendar.AM_PM));
                // set the day of the month
                newCalendar.set (Calendar.DAY_OF_MONTH, this.everyDay);
                // check if we need to add a day
                if (!actualCalendar.before (newCalendar))
                {
                    newCalendar.add (Calendar.MONTH, 1);
                } // if (newCalendar.after (actualCalendar))
                break;
            default:
                break;
        } // switch
        return newCalendar.getTime ();
    } // getNextActivationDate


    /**************************************************************************
     * Prints the settings of the exportAgent. <BR/>
     */
    public void printSettings ()
    {
        String str;

        ExportAgent_01.print ("\r\n>>> " + AgentMessages.MSG_AGENT_STARTED_WITH_SETTINGS);
        // printing source directory
        ExportAgent_01.print ("> " + AgentTokens.TOK_WORKDIR + ": " + this.workingDirectory);
        // printing temp directory
        if (this.connectorType != DIConstants.CONNECTORTYPE_FILE)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_TEMPDIR + ": " + this.tempDirectory);
        } // if
        // printing errorfiles directory
        ExportAgent_01.print ("> " + AgentTokens.TOK_ERRORFILESDIR + ": " + this.errorfilesDirectory);
        // printing retries
        ExportAgent_01.print ("> " + AgentTokens.TOK_RETRIES + ": " + this.retry);

        // print user and domain setting
        ExportAgent_01.print ("> " + AgentTokens.TOK_INVOKE_EXPORT_WITH_USER + " '" + this.userName +
               "' " + AgentTokens.TOK_AND_DOMAINID + " '" + this.domain + "'");
        // print server name
        if (this.m2ServerName.length () > 0)
        {
            str = "> " + AgentTokens.TOK_ON_SERVER + " '" + this.m2ServerName;
        } // if
        else
        {
            str = "> " + AgentTokens.TOK_ON_LOCAL_SERVER;
        } // else
        // print application path
        ExportAgent_01.print (str + "' " + AgentTokens.TOK_WITH_APPPATH + ": '" + this.m2AppPath + "'");
        // servlet or asp connection
        if (this.isServlet)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_USING_SERVLET);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_USING_ASP);
        } // else

        switch (this.connectorType)
        {
            case DIConstants.CONNECTORTYPE_FILE:
                ExportAgent_01.print ("> " + AgentMessages.MSG_USING_THE_FILECONNECTOR + "using working directory.");
                break;
            case DIConstants.CONNECTORTYPE_FTP:
                ExportAgent_01.print ("> " + AgentMessages.MSG_USING_THE_FTPCONNECTOR);
                ExportAgent_01.print ("> " + AgentTokens.TOK_FTPSERVER + ": '" + this.ftpServer + "' " +
                       AgentTokens.TOK_WITH_USER + ": '" + this.ftpUser + "'");
                if (this.ftpPath.length () > 0)
                {
                    ExportAgent_01.print ("> " + AgentTokens.TOK_USING_FTP_PATH + ": '" + this.ftpPath + "'");
                } // if
                break;
            case DIConstants.CONNECTORTYPE_EMAIL:
                ExportAgent_01.print ("> " + AgentMessages.MSG_USING_THE_MAILCONNECTOR);
                ExportAgent_01.print ("  " + AgentTokens.TOK_MAILSERVER + ": '" +
                    this.mailServer + "' " + "\r\n  "  +
                    AgentTokens.TOK_MAILPROTOCOL + ": '" + this.mailProtocol + "'\r\n  " +
                    AgentTokens.TOK_WITH_USER + ": '" + this.mailUser + "'\r\n  " +
                    AgentTokens.TOK_WITH_RECEIVER + ": '" + this.mailReceiver + "'" + "\r\n  " +
                    AgentTokens.TOK_WITH_SENDER + ": '" + this.mailSender + "'");
                break;
            case DIConstants.CONNECTORTYPE_SAPBCXMLRFC:
                ExportAgent_01.print ("> " + AgentMessages.MSG_USING_THE_SAPCONNECTOR);
                ExportAgent_01.print ("  " + AgentTokens.TOK_SAPURL + ": '" + this.sapUrl + "' " + "\r\n  "  +
                       AgentTokens.TOK_SAPNAME + ": '" + this.sapName + "'");
                break;
            case DIConstants.CONNECTORTYPE_HTTPMULTIPART:
                ExportAgent_01.print ("> " + AgentMessages.MSG_USING_THE_HTTPMULTIPARTCONNECTOR);
                ExportAgent_01.print ("  " + AgentTokens.TOK_HTTPMULTIPARTURL + ": '" + this.httpMultipartUrl + "'");
                break;
            default:
                break;
        } // switch (this.connectorType)

        // translator oid or path
        if (this.translatorOid  != null)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_USING_TRANSLATOROID + ": '" + this.translatorOid + "'");
        } // if (this.translatorOid  != null)
        else if (this.translatorName.length () > 0)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_USING_TRANSLATORNAME + ": '" + this.translatorName + "'");
        } // else if (this.translatorName.length () > 0)

        // user credentials:
        if (this.p_ntlmUsername.length () > 0)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_NT_USERCREDENTIALS + ": " +
                   this.p_ntlmDomain + "\\" + this.p_ntlmUsername);
        } // if (this.ntlmUsername.length () > 0)

        // print objects that are used for the export
        if (this.p_objects != null)
        {
            String objectStr;
            for (Iterator<String> iter = this.p_objects.iterator (); iter.hasNext ();)
            {
                objectStr = iter.next ();
                // print Objects for the export : "OID/PATH"
                ExportAgent_01.print ("> " + AgentTokens.TOK_OBJECT_FOR_EXPORT + ": '" + objectStr + "'");
            } // for iter
        } // if(this.p_objects != null)
        else
        {    // if there are no Object found
            ExportAgent_01.print ("> " + AgentTokens.TOK_OBJECT_FOR_EXPORT + AgentMessages.MSG_NO_OBJECTS_SET);
        } // else
        // the structure of the export is hierarchical if this is true only write
        // the message_hierarchical and nothing about -recursive -singleFile...
        if (this.isHierarchical)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_HIERARCHICAL);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_HIERARCHICAL);
        } // else
        // will all objects are written in one file
        if (this.isSingleFile)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_SINGLE_FILE);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_SINGLE_FILE);
        } // else
        // will structures be exported recursively
        if (this.isRecursive)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_RECURSIVE);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_RECURSIVE);
        } // else
        // will containers be included in export
        if (this.isIncludeContainer)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_INCLUDE_CONTAINER);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_INCLUDE_CONTAINER);
        } // else
        // objects after export will be deleted
        if (this.isDelete)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_DELETEOBJECTS);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_DELETEOBJECTS);
        } // else
          // objects after export will be deleted
        if (this.isDeleteRecursive)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_DELETESUBOBJECTS);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_DELETESUBOBJECTS);
        } // else
        // will external keys be resolved
        if (this.isResolveKeyMapping)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_RESOLVEKEYMAPPING);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_RESOLVEKEYMAPPING);
        } // else
        // will external keys be restored
        if (this.isRestoreExternalIDs)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_RESTOREEXTERNALIDS);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_RESTOREEXTERNALIDS);
        } // else
        // will result of a query be exported
        if (this.isResolveQuery)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_RESOLVEQUERY);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_RESOLVEQUERY);
        } // else
        // will references be resolved
        if (this.isResolveReference)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_RESOLVEREFERENCE);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_RESOLVEREFERENCE);
        } // else
        // will reference oid be used when references are resolved
        if (this.isUseReferenceOid)
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_USEREFERENCEOID);
        } // if
        else
        {
            ExportAgent_01.print ("> " + AgentMessages.MSG_NOT_USEREFERENCEOID);
        } // else

        // schedule settings
        switch (this.frequencyType)
        {
            case AgentConstants.FREQUENCYTYPE_ONCE:
                // frequency is minutes
                ExportAgent_01.print ("> " + AgentMessages.MSG_AGENT_FREQUENCY_ONCE);
                break;
            case AgentConstants.FREQUENCYTYPE_MINUTES:
                // frequency is minutes
                ExportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_EVERY + " " +
                       this.everyMinutes + " " + AgentTokens.TOK_MINUTES + ".");
                break;
            case AgentConstants.FREQUENCYTYPE_DAY:
                // frequency is day
                ExportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_EVERY_DAY + " " + this.timeStr + ".");
                break;
            case AgentConstants.FREQUENCYTYPE_WEEK:
                // frequency is week
                ExportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_AT + " " + this.timeStr);
                ExportAgent_01.print ("> " + AgentTokens.TOK_AT_WEEKDAYS + ": " + this.everyStr + ".");
                break;
            case AgentConstants.FREQUENCYTYPE_MONTH:
                // frequency is month
                ExportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_AT + " " + this.timeStr);
                ExportAgent_01.print ("> " + AgentTokens.TOK_EVERY + " " + this.everyStr + ". " +
                       AgentTokens.TOK_DAY_OF_MONTH + ".");
                break;
            default:
                break;
        } // switch (this.frequencyType)

        // check if backup connector name or oid set:
        if (this.p_backupConnectorOid != null)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_USING_BACKUPCONNECTOROID + " '" +
                   this.p_backupConnectorOid + "'");
        } // if
        else if (this.p_backupConnectorName.length () > 0)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_USING_BACKUPCONNECTORNAME + " '" +
                   this.p_backupConnectorName + "'");
        } // else if

        // errorlog file
        ExportAgent_01.print (">>> " + AgentMessages.MSG_LOG_SETTINGS);
        ExportAgent_01.print ("> " + AgentTokens.TOK_ERRORLOGFILENAME + ": " +
               this.workingDirectory + this.errorLogFileName);
        // log setting
        if (this.isWriteLog)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_LOGFILE + ": " + this.logPath + this.logFileName);
            if (this.isAppendLog)
            {
                ExportAgent_01.print ("> " + AgentMessages.MSG_LOG_WILL_BE_APPENDED);
            } // if
        } // if (this.isWriteLog)

        // print error notification settings:
        if (this.p_isNotify)
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_NOTIFY_ACTIVATED + ":");
            ExportAgent_01.print ("  " + AgentTokens.TOK_MAILSERVER + ": '" + this.p_notifyMailServer + "' " + "\r\n  "  +
                   AgentTokens.TOK_WITH_RECEIVER + ": '" + this.p_notifyReceiver + "'" + "\r\n  " +
                   AgentTokens.TOK_WITH_SENDER + ": '" + this.p_notifySender + "\r\n  " +
                   AgentTokens.TOK_MAILSUBJECT + ": '" + this.p_notifySubject + "'");
        } // if (this.isNotify)
        else    // error notification deactivated
        {
            ExportAgent_01.print ("> " + AgentTokens.TOK_NOTIFY_DEACTIVATED);
        } // else error notification deactivated
    } // printSettings


    /**************************************************************************
     * Prints the usage of the exportAgent to the system out. <BR/>
     */
    public static void printUsage ()
    {
        // print (DIMessages.MSG_EXPORTAGENT_USAGE);
        ExportAgent_01.print ("\r\n" + AgentMessages.MSG_EXPORTAGENT_USAGE);
    } // printUsage


    /**************************************************************************
     * Prints the menu keys to control the agent. <BR/>
     */
    public static void printMenu ()
    {
        // print (DIMessages.MSG_AGENT_USAGE);
        ExportAgent_01.print (AgentMessages.MSG_AGENT_MENU);
        ExportAgent_01.print ("> " + AgentMessages.MSG_AGENT_SLEEPING);
    } // printMenu


    /**************************************************************************
     * Prints a message. this is used in order to encapsulate the destination
     * of a print command. It will be System.out at the moment but can
     * could be extended in the future to print to a file. <BR/>
     *
     * @param   message     the message to be printed
     */
    public static void print (String message)
    {
        System.out.println (message);
    } // print


    /**************************************************************************
     * Prompt the user to enter a password. <BR/>
     */
    private void getPassword ()
    {
        String enteredPassword = "";

        // Message for the user to after the password
        ExportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_PASSWORD);
        // get the password from the commandline
        BufferedReader enter =
            new BufferedReader (new InputStreamReader (System.in));
        try
        {
            enteredPassword = enter.readLine ();
        } // try
        catch (IOException e)
        {
            ExportAgent_01.print ("Error when reading password: " + e);
        } // catch
        this.password = enteredPassword;
    } // getPassword


    /**************************************************************************
     * Check if logging has been activated and add a message to the log
     * file in case it exists. <BR/>
     * Note that this is a potential error source because we have no
     * control which name the log file has been assigned by the m2 server.
     * The log file name set in the export agent can differ from the
     * real log file name. <BR/>
     *
     * @param   message     The message to be displayed.
     */
    private void addLog (String message)
    {
        FileWriter fw;

        try
        {
            // create a file writer
            fw = new FileWriter (this.workingDirectory + this.errorLogFileName, true);
            // write the log text of the form
            // [DD:MM:YYYY HH:MM] <message>
            fw.write ("[" + DateTimeHelpers.dateTimeToString (new Date ()) +
                "] " + message + "\r\n\r\n");
            // close the writer
            fw.close ();
        } // try
        catch (IOException e)
        {
            ExportAgent_01.print ("ERROR: " +
                AgentMessages.MSG_COULD_NOT_WRITE_TO_ERRORLOG +
                " (" + e.getMessage () + ")");
        } // catch
    } // addLog



    /**************************************************************************
     * Check if a file is an errorFile. <BR/>
     * This is done by checking if the error files string vector contains
     * the fileName. <BR/>
     *
     * @param   fileName    The name of the file to be checked.
     *
     * @return  true if the error files string vector contains the file name set
     *          or false otherwise
     */
    private boolean isErrorFile (String fileName)
    {
        AgentErrorFile errorFile;

        // check if we got a fileName
        if (fileName == null)
        {
            return false;
        } // if

        // valid filename set
        // check if any error files exist
        if (this.errorFiles != null && this.errorFiles.size () > 0)
        {
            // loop through the error files vector and check if filename
            // matches
            for (int i = 0; i < this.errorFiles.size (); i++)
            {
                // get the errorfile instance
                errorFile = this.errorFiles.elementAt (i);
                // check if name matches
                if (fileName.equals (errorFile.fileName))
                {
                    return true;
                } // if
            } // for (int i = 0; i < this.errorFiles.size (); i++)
            return false;
        } // if (this.errorFiles != null && this.errorFiles.size () > 0)

        // no error files
        return false;
    } // isErrorFile


    /**************************************************************************
     * Check if any files in the errorFiles vector has exceeded their
     * maximal number of retries. <BR/>
     */
    private void checkErrorFiles ()
    {
        AgentErrorFile errorFile;

        // check if any errorFiles exist
        if (this.errorFiles != null)
        {
            ExportAgent_01.print (">>> " + AgentMessages.MSG_CHECKING_ERRORFILES);

            // loop through the error files vector and check if max number
            // of retries has been reached
            for (int i = 0; i < this.errorFiles.size (); i++)
            {
                // get the errorFile
                errorFile = this.errorFiles.elementAt (i);
                // check if maximal number of retries have been reached
                if (errorFile.retries >= this.retry)
                {
                    // move the file to the errorfile directory
                    this.moveErrorFile (errorFile);
                    // remove the element
                    this.errorFiles.removeElementAt (i--);
                } // if (errorFile.retries >= this.retry)
            } // for (int i = 0; i < this.errorFiles.size (); i++)
        } // if (this.errorFiles != null)
    } // checkErrorFiles


    /**************************************************************************
     * Move an error file from the temp directory into the errorfiles
     * directory and send a notification email if activated. <BR/>
     *
     * @param errorFile the AgentErrorFile instance that should be moved
     */
    private void moveErrorFile (AgentErrorFile errorFile)
    {
        String movedFileName;
        String text;

        // first create a unique filename
        movedFileName = FileHelpers.getUniqueFileName (this.errorfilesDirectory,
                                                       errorFile.fileName);
        // now move the file
        if (FileHelpers.moveFile (this.tempDirectory + errorFile.fileName,
                                  this.errorfilesDirectory + movedFileName))
        {
            text = AgentMessages.MSG_ERRORFILE_EXCEEDED_RETRIES + " [" + this.retry + "]:\r\n " +
                   this.tempDirectory + errorFile.fileName + "\r\n";
            text += AgentMessages.MSG_MOVING_ERRORFILE + ":\r\n " +
                   this.errorfilesDirectory + movedFileName;
            // print the error text and add to log
            ExportAgent_01.print (text);
            this.addLog (text);
            // now send an email
            if (this.p_isNotify)
            {
                text += "\r\n\r\n" + AgentMessages.MSG_ERRORLOG_LOCATION + "\r\n" +
                    this.workingDirectory + this.errorLogFileName;
                // send the notification text
                this.sendNotification (text);
            } // if (this.isNotify)
        } // if (FileHelpers.moveFile (this.tempDirectory + fileName, ...
        else    // could not move error file
        {
            text = AgentMessages.MSG_ERRORFILE_EXCEEDED_RETRIES + " [" + this.retry + "]:\r\n " +
                   this.tempDirectory + errorFile.fileName + "\r\n";
            text += AgentMessages.MSG_COULD_NOT_MOVE_ERRORFILE + ":\r\n " +
                   this.errorfilesDirectory + movedFileName;
            // print the error text and add to log
            ExportAgent_01.print ("ERROR! " + text);
            this.addLog (text);
            // now send an email
            if (this.p_isNotify)
            {
                text += "\r\n\r\n" + AgentMessages.MSG_ERRORLOG_LOCATION + "\r\n " +
                    this.workingDirectory + this.errorLogFileName;
                // send the notification text
                this.sendNotification (text);
            } // if (this.isNotify)
        } // could not move error file
    } // moveErrorFiles


    /**************************************************************************
     * Sends an email with an error notification. <BR/>
     *
     * @param message   the message text to be send
     */
    private void sendNotification (String message)
    {
        EMail mail = new EMail ();

        try
        {
            // create the mail
            mail.setReceiver (this.p_notifyReceiver);
            mail.setSender (this.p_notifySender);
            mail.setSubject (this.p_notifySubject);
            // set the message
            mail.setContent (message);
            // now send the mail
            EMailManager.sendMail (mail, this.p_notifyMailServer);
        } // try
        catch (AddressException e)
        {
            ExportAgent_01.print (AgentMessages.MSG_COULD_NOT_SEND_NOTIFICATION +
                   " (" + e.getMessage () + ")");
            this.addLog (AgentMessages.MSG_COULD_NOT_SEND_NOTIFICATION +
                   " (" + e.getMessage () + ")");
        } // catch
        catch (MessagingException e)
        {
            ExportAgent_01.print (AgentMessages.MSG_COULD_NOT_SEND_NOTIFICATION +
                   " (" + e.getMessage () + ")");
            this.addLog (AgentMessages.MSG_COULD_NOT_SEND_NOTIFICATION +
                   " (" + e.getMessage () + ")");
        } // catch
    } // sendNotification


    /**************************************************************************
     * Add an errorFile entry to the errorFile vector. <BR/>
     * Additionally the method generates an errormessage that will be printed
     * and added to the agents errorlog. <BR/>
     *
     * @param   fileName    The filename that caused the problem.
     * @param   message     The message to be added to the error file.
     */
    private void addErrorFile (String fileName, String message)
    {
        AgentErrorFile errorFile = null;
        boolean isFound = false;
        String errormessage;

        // check if any errorFiles vector has already been initialised
        if (this.errorFiles == null)
        {
            // create the errorFiles vector
            this.errorFiles = new Vector<AgentErrorFile> ();
            // and add an AgentErrorFile instance
            errorFile = new AgentErrorFile (fileName);
            this.errorFiles.addElement (errorFile);
        } // if (this.errorFiles == null)
        else    // errorFiles vector exists
        {
            // loop through the error files vector and check if it already
            // contains the file
            for (int i = 0; i < this.errorFiles.size () && !isFound; i++)
            {
                // get the errorFile
                errorFile = this.errorFiles.elementAt (i);

                // check if maximal number of retries have been reached
                if (errorFile.fileName.equals (fileName))
                {
                    // increase the number of retries already used
                    errorFile.increaseRetries ();
                    isFound = true;
                } // if (errorFile.fileName.equals (fileName))
            } // for (int i = 0; i < this.errorFiles.size (); i++)
            // check if the entry has been found
            if (!isFound)
            {
                // not found
                // add the error file to the vector
                errorFile = new AgentErrorFile (fileName);
                this.errorFiles.addElement (errorFile);
            } // if (!isFound)
        } // errorFiles vector exists
        // create an error message
        errormessage = "WARNING! " + AgentMessages.MSG_COULD_NOT_WRITE_FILE + ":\r\n " +
            this.connector.getPath () + errorFile.fileName +
            " [" + errorFile.retries + ". " + AgentTokens.TOK_TRY  + "]\r\n" +
            " (" + message + ")";
        // print the message
        ExportAgent_01.print (errormessage);
        // and add to log
        this.addLog (errormessage);
    } // addErrorFile


    /**************************************************************************
     * Shows a debug message. This message can be switched off and on. <BR/>
     *
     * @param   message     the debug message to be displayed
     */
    public  void showDebug (String message)
    {
        if (this.isDebug)
        {
            ExportAgent_01.print ("DEBUG: "  + message);
        } // if
    } // showDebug

}  // class ExportAgent_01
