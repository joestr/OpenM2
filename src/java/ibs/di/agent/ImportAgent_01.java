/*
 * Class: ImportAgent_01.java
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
import ibs.di.connect.FileConnector_01;
import ibs.io.IOHelpers;
import ibs.tech.http.HttpArguments;
import ibs.tech.ntlm.NTLMClient;
import ibs.tech.ntlm.NTLMException;
import ibs.util.DateTimeHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.io.BufferedReader;
import java.io.File;
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


/******************************************************************************
 * The importAgent handles imports triggered via the operating system. <BR/>
 * See the agents usage for all command line parameters. <BR/>
 *
 * @version     $Id: ImportAgent_01.java,v 1.37 2011/11/08 13:00:55 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 990128
 ******************************************************************************
 */
public class ImportAgent_01 extends Thread
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportAgent_01.java,v 1.37 2011/11/08 13:00:55 btatzmann Exp $";


    /**
     * the connector used for the. <BR/>
     */
    private Connector_01 connector = null;


    // LOGIN PREFERENCES
    /**
     *  name of the m2 server. <BR/>
     */
    private String m2ServerName = "localhost";

    /**
     * the m2 application path. <BR/>
     * Default: <CODE>"/m2/"</CODE>
     */
    private String p_m2AppPath = "/m2/";

    /**
     *  name of the user to use for login. <BR/>
     */
    private String userName = "";

    /**
     *  password of the user. <BR/>
     */
    private String password = "";

    /**
     *  domain of the user. <BR/>
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
    };

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
     * Default:
     * {@link AgentConstants#FREQUENCYTYPE_ONCE AgentConstants.FREQUENCYTYPE_ONCE}
     * (only run once). <BR/>
     */
    private int frequencyType = AgentConstants.FREQUENCYTYPE_ONCE;


    // IMPORT PREFERENCES
    /**
     * path to source directory. <BR/>
     * default is the directory  the agent has been started from. the same
     * path will be used for the FileConnector and will be the default
     * log path if no explicit path provided. <BR/>
     */
    private String sourcePath = "";

    /**
     *  name of the import file to be used as a filter. <BR/>
     * Default: <CODE>"*.xml"</CODE>
     */
    private String p_importFileNameFilter = "*.xml";

    /**
     *  OID of the connector to be used. (alternative to the connector name). <BR/>
     */
    private OID p_connectorOid = null;

    /**
     *  name of the connector to be used. <BR/>
     */
    private String p_connectorName = "";

    /**
     * oid of the container to be used as import container
     * (alternative to importContainer path). <BR/>
     */
    private OID importContainerOid = null;

    /**
     * m2 path of the container to be used as import container. <BR/>
     * this is an alternative to using the importContainer OID. <BR/>
     */
    private String importContainerPath = "";

    /**
     * oid of the importScript to use (alternative to importscript name). <BR/>
     */
    private OID importScriptOid = null;

    /**
     * name of the importScript to use (alternative to importscript OID). <BR/>
     */
    private String importScriptName = "";


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

    /**
     *  delete import files. <BR/>
     */
    private boolean isDeleteImportFiles = false;

    /**
     * Flag to enable workflow when object is imported
     * into a xmlviewercontainer that has a workflow template associated. <BR/>
     */
    private boolean p_isEnableWorkflow = false;

    /**
     * Flag to enable sorting the import files
     */
    private int p_sortImportFiles = UtilConstants.ORDER_NONE;


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


    // LOG preferences:
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
     *  Date of last activation. <BR/>
     */
    private Date p_lastActivationDate;

    /**
     *  Date of scheduled next activation. <BR/>
     */
    private Date p_nextActivationDate;

    /**
     *  flag to show debug messages. <BR/>
     */
    private boolean p_isDebug;


    // Note that the following settings are not supported anymore!!!

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
     * error notifiation option. <BR/>
     * This will be passed to the m2 server in order to start
     * an error notification if neccessary. <BR/>
     */
    private boolean p_isNotify = false;

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
    private String p_notifySubject = "";

    /**
     * Option to activate the structure validation. <BR/>
     */
    private boolean p_isValidate = false;


    /**************************************************************************
     * Creates an ImportAgent_01 Object.. <BR/>
     */
    public ImportAgent_01 ()
    {
        // nothing to do
    } // ImportAgent_01


    /**************************************************************************
     * The main method will be executed when called from the operating
     * system. It reads the command line options and creates an agent instance.
     * There will be a loop reading commands from the commandline until the
     * key "x" is pressed. <BR/>
     *
     * @param   argv    The string array with the command line arguments.
     */
    public static void main (String[] argv)
    {
        ImportAgent_01 agent;
        Thread agentThread;
        InputStreamReader reader;

        // check if there are arguments
        if (argv.length > 0)
        {
            // create the agent object
            agent = new ImportAgent_01 ();
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
                        ImportAgent_01.printUsage ();
                        System.exit (-1);
                    } // if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_HELP) ||
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_WAIT))
                    {
                        agent.isWait = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_WAIT))
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
                        // set the name of the user to connect with
                        agent.p_m2AppPath = argv[++i];
                        // assure correct slashes
                        if (!agent.p_m2AppPath.startsWith ("/"))
                        {
                            agent.p_m2AppPath = "/" + agent.p_m2AppPath;
                        } // if
                        if (!agent.p_m2AppPath.endsWith ("/"))
                        {
                            agent.p_m2AppPath += "/";
                        } // if
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_APPPATH))
/* BB NOT SUPPORTED ANYMORE BECAUSE ALWAYS SERVLET
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION))
                    {
                        // set the name of the user to connect with
                        String connectionTypeStr = argv[++i];
                        if (connectionTypeStr.equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION_SERVLET))
                            agent.p_isServlet = true;
                        else if (connectionTypeStr.equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION_ASP))
                            agent.p_isServlet = false;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTION))
*/
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
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_ENABLEWORKFLOW))
                    {
                        agent.p_isEnableWorkflow = true;
                    } // if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_ENABLEWORKFLOW))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SORT))
                    {
                        i++;
                        if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SORT_ASC))
                        {
                            agent.p_sortImportFiles = UtilConstants.ORDER_ASC;
                        } // if
                        else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SORT_DESC))
                        {
                            agent.p_sortImportFiles = UtilConstants.ORDER_DESC;
                        } // if
                    } // if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SORT))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_PATH))
                    {
                        // set the path to the source directory
                        agent.sourcePath = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_PATH))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FILTER))
                    {
                        // set the name of the importfile
                        agent.p_importFileNameFilter = argv[++i];
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_FILTER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONTAINER))
                    {
                        i++;
                        try
                        {
                            if (AgentConstants.AGENTARG_CONTAINER != null)
                            {
                                // try to set oid of the import container
                                agent.importContainerOid = new OID (argv[i]);
                            } // if
                        } // try
                        catch (IncorrectOidException e)
                        {
                            // we assume that a path has been used
                            agent.importContainerOid = null;
                            agent.importContainerPath = argv[i];
                        } // catch
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONTAINER))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTOR))
                    {
                        i++;
                        try
                        {
                            // try to set the oid of the connector to use
                            agent.p_connectorOid = new OID (argv[i]);
                        } // try
                        catch (IncorrectOidException e)
                        {
                            agent.p_connectorOid = null;
                            agent.p_connectorName = argv[i];
                        } // catch
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTOR))
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
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SCRIPT))
                    {
                        i++;
                        try
                        {
                            // set oid of the importScript
                            agent.importScriptOid = new OID (argv[i]);
                        } // try
                        catch (IncorrectOidException e)
                        {
                            agent.importScriptOid = null;
                            agent.importScriptName = argv[i];
                        } // catch
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_SCRIPT))
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
                            agent.isWriteLog = true;
                            agent.isAppendLog = false;
                        }  // if (logTypeStr.equalsIgnoreCase ("NEW"))
                        else if (logTypeStr.equalsIgnoreCase (AgentConstants.AGENTARG_LOGGING_APPEND))
                        {
                            agent.isWriteLog = true;
                            agent.isAppendLog = true;
                        } // else if (logTypeStr.equalsIgnoreCase ("APPEND"))
                        else // no logging at all
                        {
                            agent.isWriteLog = false;
                            agent.isAppendLog = false;
                        } // else no logging at all
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_LOGGING))
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DELETE))
                    {
                        agent.isDeleteImportFiles = true;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DELETE))
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

/* BB NOT SUPPORTED ANYMORE BECAUSE REPLACED BY THE -CONNECTOR ARGUMENT
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE))
                    {
                        i++;
                        if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_FILE))
                            agent.connectorType = DIConstants.CONNECTORTYPE_FILE;
                        else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_FTP))
                            agent.connectorType = DIConstants.CONNECTORTYPE_FTP;
                        else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_CONNECTORTYPE_MAIL))
                            agent.connectorType = DIConstants.CONNECTORTYPE_EMAIL;
                        else
                            agent.connectorType = DIConstants.CONNECTORTYPE_NONE;
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_DELETE))
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
*/
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFY))
                    {
                        // activate error notification
                        agent.p_isNotify = argv[++i].equalsIgnoreCase (AgentConstants.NOTIFY_YES);
                    } // else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_NOTIFY))
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
                    else if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_VALIDATESTRUCTURE))
                    {
                        agent.p_isValidate = true;
                    } // if (argv[i].equalsIgnoreCase (AgentConstants.AGENTARG_VALIDATESTRUCTURE))

                    else if (argv[i].equalsIgnoreCase ("-DEBUG"))
                    {
                        agent.p_isDebug = true;
                    } // else if (argv[i].equalsIgnoreCase ("-DEBUG"))
                    else    // parameter invalid
                    {
                        ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_PARAMETER + argv[i]);
                        ImportAgent_01.printUsage ();
                        System.exit (-1);
                    } // else parameter invalid
                } // for

                // all parameters read. now try to initialize the agent
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
                        agentThread = new Thread (agent, "Importagent");
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
                                    ImportAgent_01.printMenu ();
                                } // if (i == 's' || i == 'S')
                                else if (i == 'a' || i == 'A')
                                {
                                    ImportAgent_01.print ("> " + AgentTokens.TOK_LAST_ACTIVATION_AT + " " +
                                            DateTimeHelpers.dateTimeToString (agent.p_lastActivationDate) + " ...");
                                    ImportAgent_01.print ("> " + AgentTokens.TOK_NEXT_ACTIVATION_AT + " " +
                                            DateTimeHelpers.dateTimeToString (agent.p_nextActivationDate) + " ...");
                                    ImportAgent_01.printMenu ();
                                } // else if (i == 'a' || i == 'A')
                                else if (i == 'h' || i == 'H')
                                {
                                    ImportAgent_01.printUsage ();
                                    ImportAgent_01.printMenu ();
                                } // else if (i == 'h' || i == 'H')
                                // send the agent for 1 second to sleep
                                // just for case a constant waiting for input consumes to much cpu time
                                Thread.sleep (1000);
                            } // while
                        } // try
                        catch (InterruptedException e)
                        {
                            ImportAgent_01.print ("\r\n" + e.toString ());
                        } // catch
                        catch (IOException e)
                        {
                            ImportAgent_01.print ("\r\n" + e.toString ());
                        } // catch
                        // close the agent
                        agent.close ();
                        System.exit (0);
                    } // if (agent.initConnector())
                    else // could not initialize connector
                    {
                        ImportAgent_01.print (AgentMessages.MSG_INIT_FAILED);
                        ImportAgent_01.printUsage ();
                        System.exit (-1);
                    } // else could not initialize connector
                } // if (agent.checkParameter())
                else    // parameters where invalid
                {
                    ImportAgent_01.print (AgentMessages.MSG_PARAMETERCHECK_FAILED);
                    ImportAgent_01.printUsage ();
                    System.exit (-1);
                } // else parameters where invalid
            } // try
            catch (ArrayIndexOutOfBoundsException e)
            {
                ImportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_PARAMETER);
                ImportAgent_01.printUsage ();
            } // catch
        } // if argv.length > 0)
        else    // no arguments
        {
            ImportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_PARAMETER);
            ImportAgent_01.printUsage ();
        } // else no arguments
    } // main


    /**************************************************************************
     * Checks import parameter and tests if file paths exist. <BR/>
     *
     * @return  true if parameter ok or false otherwise
     */
    public boolean checkParameter ()
    {
//showDebug ("--- START checkParameter ---");

        File file;
        String dateTimeStr;
        SimpleDateFormat formatter = new SimpleDateFormat ("dd.MM.yyyy hh:mm");

        // some additional parameter checks could be done here
        if (this.userName.length () == 0)
        {
            ImportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_USERNAME);
            return false;
        } // if (this.userName.length () == 0)
        if (this.password .length () == 0)
        {
            ImportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_PASSWORD);
            this.getPassword ();
        } // if
        if (this.domain < 1)
        {
            ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_DOMAINID);
            return false;
        } // if (this.domain.length () == 0)

        if (this.importContainerOid == null && this.importContainerPath.length () == 0)
        {
            ImportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_IMPORTCONTAINER);
            return false;
        } // if (this.importContainerOid == null && this.importContainerPath.length () == 0)

/* BB NOT SUPPORTED ANYMORE
        if (this.connectorType == DIConstants.CONNECTORTYPE_NONE)
        {
            print ("\r\n" + AgentMessages.MSG_INVALID_CONNECTORTYPE);
            return false;
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_NONE)
        else if (this.connectorType == DIConstants.CONNECTORTYPE_FTP)
        {
            if (this.ftpServer.length () == 0)
            {
                print ("\r\n" + AgentMessages.MSG_MISSING_FTPSERVER);
                return false;
            } // if (this.ftpServer.length () == 0)
            if (this.ftpUser.length () == 0)
            {
                print ("\r\n" + AgentMessages.MSG_MISSING_FTPUSER);
                return false;
            } // if (this.ftpUser.length () == 0)
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_FTP)
        else if (this.connectorType == DIConstants.CONNECTORTYPE_EMAIL)
        {
            if (this.mailServer.length () == 0)
            {
                print ("\r\n" + AgentMessages.MSG_MISSING_MAILSERVER);
                return false;
            } // if (this.mailServer.length () == 0)
            if (this.mailUser.length () == 0)
            {
                print ("\r\n" + AgentMessages.MSG_MISSING_MAILUSER);
                return false;
            } // if (this.mailUser.length () == 0)
            if (this.mailProtocol.length () == 0)
            {
                print ("\r\n" + AgentMessages.MSG_MISSING_MAILPROTOCOL);
                return false;
            } // if (this.mailProtocol.length () == 0)
            else    // mailprotocol has been set
            {
                // check the mail protocol parameter
                if (! (this.mailProtocol.equalsIgnoreCase (AgentConstants.AGENTARG_MAILPROTOCOL_IMAP) ||
                       this.mailProtocol.equalsIgnoreCase (AgentConstants.AGENTARG_MAILPROTOCOL_POP3)))
                {
                    print ("\r\n" + AgentMessages.MSG_UNKOWN_MAILPROTOCOL);
                    return false;
                } // if (! (this.mailProtocol.equals (AgentConstants.AGENTARG_MAILPROTOCOL_IMAP)) || ...
            } // else mailprotocol has been set
        } // if (this.connectorType == DIConstants.CONNECTORTYPE_EMAIL)
*/

        if (this.sourcePath.length () == 0)
        {
            // no source path provided. set the current path as source path
            file = new File ("");
            // get the absolute path (will be required by the fileConnector)
            this.sourcePath = file.getAbsolutePath ();
            // set the source path as log path in case
            // the log should be written and no log path provided
            if (this.isWriteLog && this.logPath.length () == 0)
            {
                this.logPath = this.sourcePath;
            } // if
        } // else if (this.sourcePath.length () == 0)
        if (this.frequencyStr.length () > 0)
        {
            // check frequency type
            if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_ONCE))
            {
                this.frequencyType = AgentConstants.FREQUENCYTYPE_ONCE;
            } // if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_ONCE))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_MINUTES))
            {
//showDebug ("check frequency MINUTES");
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
                    ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_EVERY);
                    return false;
                } // catch (NumberFormatException e)
            } // if (this.frequencyStr.equalsIgnoreCase ("MINUTES"))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_DAY))
            {
//showDebug ("check frequency DAY");
                // set frequency type to minutes
                this.frequencyType = AgentConstants.FREQUENCYTYPE_DAY;
                // check time value
                dateTimeStr = "01.01.1970 " + this.timeStr;
//showDebug("dateTimeStr = " + dateTimeStr);
                try
                {
                    this.everyTime.setTime (formatter.parse (dateTimeStr));
//showDebug("everyTime: " + this.everyTime.getTime().toString ());
                } // try
                catch (ParseException e)
                {
                    ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_TIME);
                    return false;
                } // catch (ParseException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("DAY"))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_WEEK))
            {
//showDebug ("check frequency WEEK");
                // set frequency type to minutes
                this.frequencyType = AgentConstants.FREQUENCYTYPE_WEEK;
                String everyStrUpper = this.everyStr.toUpperCase ();
                // set the weekdays
                if (everyStrUpper.indexOf (AgentConstants.WEEKDAY_MONDAY) > -1)
                {
                    this.everyWeekday [1] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("MO") > -1)
                if (everyStrUpper.indexOf (AgentConstants.WEEKDAY_TUESDAY) > -1)
                {
                    this.everyWeekday [2] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("TU") > -1)
                if (everyStrUpper.indexOf (AgentConstants.WEEKDAY_WEDNESDAY) > -1)
                {
                    this.everyWeekday [3] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("WE") > -1)
                if (everyStrUpper.indexOf (AgentConstants.WEEKDAY_THURSDAY) > -1)
                {
                    this.everyWeekday [4] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("TH") > -1)
                if (everyStrUpper.indexOf (AgentConstants.WEEKDAY_FRIDAY) > -1)
                {
                    this.everyWeekday [5] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("FR") > -1)
                if (everyStrUpper.indexOf (AgentConstants.WEEKDAY_SATURDAY) > -1)
                {
                    this.everyWeekday [6] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("SA") > -1)
                if (everyStrUpper.indexOf (AgentConstants.WEEKDAY_SUNDAY) > -1)
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
                    ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_TIME);
                    return false;
                } // catch (ParseException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("WEEK"))
            else if (this.frequencyStr.equalsIgnoreCase (AgentConstants.AGENTARG_FREQUENCY_MONTH))
            {
//showDebug ("check frequency MONTH");
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
                    ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_TIME);
                    return false;
                } // catch (ParseException e)
                catch (NumberFormatException e)
                {
                    ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_EVERY);
                    return false;
                } // catch (NumberFormatException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("MONTH"))
            else    // unkown frequency
            {
                // set the -1 to invalidate the setting
                this.frequencyType = -1;
            } // unkown frequency
        } // else if (this.frequencyStr.length () > 0)
        if (this.frequencyType < 0)
        {
            ImportAgent_01.print ("\r\n" + AgentMessages.MSG_INVALID_FREQUENCY);
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
                ImportAgent_01.print ("\r\n" + AgentMessages.MSG_MISSING_LOGFILENAME);
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
//showDebug ("--- START initConnector ---");
        // check if a file connector should be set
        // this will only be done in case no connector has been set
        if (this.p_connectorOid == null && (this.p_connectorName.length () == 0))
        {
            // check if the connector has already been set
            if (this.connector == null)
            {
                // create a file connector
                this.connector = new FileConnector_01 ();
                // set the source path
                this.connector.setPath (this.sourcePath);
                // set the flag if importfiles should be deleted
                this.connector.isDeleteImportFiles = this.isDeleteImportFiles;
                // check if a name filter has been set
                if (this.p_importFileNameFilter != null && this.p_importFileNameFilter.length () > 0)
                {
                    this.connector.setFileFilter (this.p_importFileNameFilter);
                } // if
                // now init the connector and try to get the files
                try
                {
                    // init the connector
                    this.connector.initConnector ();
                    return true;
                } // try
                catch (ConnectionFailedException e)
                {
                    this.connector = null;
                    ImportAgent_01.print (e.toString ());
                    return false;
                } // catch
            } // if (this.connector == null)

            // connector already set
            return true;
        } // if (this.connector == null)

        // no connector to be set
        return true;
    } // initConnector


    /**************************************************************************
     * Implements the run method used for threads. The agent checks its
     * import source and activated the imports in case there are importfiles
     * found. it processes every file found sequentially. the date and time of
     * the next activation is calculated and the agent is send to sleep until
     * this the next activation date. <BR/>
     */
    public void run ()
    {
//showDebug ("--- START run ---");

        Date actualDate = null;
        Date nextActivationDate = null;
        String [] importFileNames;
        String importFileNamesStr = "";
        String comma = "";
        long timeToSleep;
        int i;
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
                // activate the agent:
                ImportAgent_01.print ("\r\n>>> " + AgentMessages.MSG_AGENT_ACTIVATED);

                // check if a connector has been set:
                if (this.connector != null) // connector set?
                {
                    importFileNames = this.getFiles ();
                    // check if we need to perform an import
                    if (importFileNames != null) // files found?
                    {
                        importFileNamesStr = "";
                        comma = "";
                        // loop through all importfiles found:
                        for (i = 0; i < importFileNames.length; i++)
                        {
                            importFileNamesStr += comma + "'" + importFileNames[i] + "'";
                            comma = ", ";
                        } // for (i = 0; i < importFileNames.length (); i ++)
                        ImportAgent_01.print (">>> " + AgentTokens.TOK_PROCESSING_IMPORTFILES + " :" + importFileNamesStr + "' ...");
                        // start the import with the file names we got
                        this.startImport (importFileNames);
                    } // if files found
                    else    // no import files found
                    {
                        ImportAgent_01.print ("> " + AgentMessages.MSG_NO_IMPORTFILES_FOUND);
                    } // else no import files found
                } // if (this.connector != null)
                else    // no connector set
                {
                    // just start the import. the m2 importIntegrator will
                    // check for files with the given the connector setting
                    this.startImport (null);
                } // no connector set
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
            this.p_lastActivationDate = actualDate;
            this.p_nextActivationDate = nextActivationDate;
            // calculate the time to sleep for the agent
            timeToSleep = nextActivationDate.getTime () - actualDate.getTime ();
            ImportAgent_01.print ("> " + AgentTokens.TOK_NEXT_ACTIVATION_AT + " " +
                    DateTimeHelpers.dateTimeToString (this.p_nextActivationDate) + " ...");
            // print the menu keys
            ImportAgent_01.printMenu ();
            try
            {
                // send the thread to sleep ...
                Thread.sleep (timeToSleep);
            } // try
            catch (InterruptedException e)
            {
                // close the agent
                this.close ();
                // exit execution
                System.exit (-1);
            } // catch
        } // while (! isInterrupted ())
        // close the agent
        this.close ();
    } // run


    /**************************************************************************
     * Closes the agent and the connector. <BR/>
     */
    public void close ()
    {
        if (this.connector != null)
        {
            this.connector.close ();
        } // if
        ImportAgent_01.print ("\r\n" + AgentMessages.MSG_AGENT_CLOSED);
    } // close


    /**************************************************************************
     * Get import files. This means that the method checks
     * if import files are available at the import source and returns the
     * names of these importfiles as an array of strings. <BR/>
     * Furthermore the method checks if there has already a connector been
     * created. <BR/>
     *
     * @return  an array with all importfile names or null otherwise
     */
    private String[] getFiles ()
    {
//showDebug ("--- START getFiles ---");

        String [] files;

        // check if the connector has already been set
        if (this.connector != null)
        {
            // get the files
            try
            {
                // get the file list from the connector:
                files = this.connector.dir ();
                // apply the connector filter:
                files = this.connector.applyFilter (files);

                // check if the files shall be sorted:
                if (this.p_sortImportFiles != UtilConstants.ORDER_NONE)
                                        // sort the files?
                {
                    this.connector.sortFiles (files);
                } // if sort the files

                // check if we got any files:
                if (files == null)
                {
                    return null;
                } // if
                else if (files.length == 0)
                {
                    return null;
                } // else if
                else    // files have been found
                {
                    // loop through the files and get them via the connector
                    // note that the file names a mailConnector generates
                    // are not the physical file names because the logical file
                    // name in a mailconnector is the subject and not the name
                    // of the importfile
                    for (int i = 0; i < files.length; i++)
                    {
                        // read the file
                        this.connector.read (files [i]);
                        // and set the name
                        files [i] = this.connector.getFileName ();
                    } // for (int i = 0; i < files.length; i++)
                } // files have been found
                // return the file name array
                return files;
            } // try
            catch (ConnectionFailedException e)
            {
                ImportAgent_01.print (e.toString ());
                return null;
            } // catch
        } // if (this.connector == null)

        // connector not available
//        print (AgentMessages.MSG_NO_CONNECTOR_AVAILABLE);
        return  null;
    } // getFiles


    /**************************************************************************
     * Performs the import with the arguments from the command line.
     * The agents acts like a web client and sends an http request to the
     * m2 server sending the arguments within the querystring. A function
     * is called at the m2 server that will first perform a login with
     * the username and password given and will start the importfunction
     * after a successfull login. the agent reads the response from the
     * webserver and prints it. Note that in the case of errors we get
     * http output from the server. the function called at the m2 server
     * will set a flag that supresses the generation of HTML output but the#
     * flag does not affect the output in case of errors. <BR/>
     *
     * @param   importFileNames Names of the import files to be processed.
     */
    private void startImport (String[] importFileNames)
    {
        URL url = null;
        String loginQueryStr = "";
        String importQueryStr = "";
        String m2ServletServerUrl;
        String inputLine;
        HttpURLConnection connection;
        BufferedReader bufferedReader;

        try
        {
            // construct the URL to the m2 server
// KR HACK: The check for http and https is missing!!!
// KR HACK: There should not be the name of the servlet!!!
            m2ServletServerUrl = "http://" + this.m2ServerName +
                this.p_m2AppPath + "ApplicationServlet";

            // we need to login first into the m2 application
            ImportAgent_01.print (">>> " + AgentMessages.MSG_TRY_IMPORT_LOGIN);
            // send the data needed for a servlet environment:
            loginQueryStr = HttpArguments.ARG_BEGIN;

            // construct the query string for the login
            loginQueryStr += BOArguments.ARG_USERNAME + HttpArguments.ARG_ASSIGN +
                IOHelpers.urlEncode (this.userName) +
                HttpArguments.createArg (BOArguments.ARG_PASSWORD,
                                         this.password) +
                HttpArguments.createArg (BOArguments.ARG_DOMAIN,
                                         this.domain) +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                                         AppFunctions.FCT_AGENTLOGINIMPORT);

            // construct the query string for the import
            // set the importcontainer oid or the path
            if (this.importContainerOid != null)
            {
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_IMPORTCONTAINERID,
                    this.importContainerOid.toString ());
            } //if (this.importContainerOid != null)
            else                        // set the import container path
            {
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_IMPORTCONTAINERPATH,
                    this.importContainerPath);
            } // else set the import container path

            // check wheather to import from the working directory or using
            // a connector
            if (this.connector != null)
            {
                // set using a directory as import source:
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_IMPORTSOURCE, DIConstants.SOURCE_DIR);
                // set the source path that will be used by the fileconnector
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_IMPORTPATH, this.sourcePath);
            } // if (this.connector != null)
            else                        // set import from connector
            {
                // set using a connector from an agent
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_IMPORTSOURCE, DIConstants.SOURCE_AGENT);
                // set the source path that will be used by the fileconnector
                if (this.p_connectorOid != null)
                {
                    importQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_CONNECTOR, this.p_connectorOid.toString ());
                } // if (this.connectorOid != null)
                else    // set a connector name
                {
                    importQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_CONNECTORNAME, this.p_connectorName);
                } // else set a connector name
            } // else set import from connector

            // set the translator oid or the name if applicable
            if (this.translatorOid != null)
            {
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_TRANSLATOR, this.translatorOid.toString ());
            } // if (this.translatorOid != null)
            else    // try to set translator name if available
            {
                if (this.translatorName.length () > 0)
                {
                    importQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_TRANSLATORNAME, this.translatorName);
                } // if
            } // else try to set translator name if available

            // set the importscript oid or the name if applicable
            if (this.importScriptOid != null)
            {
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_IMPORTSCRIPT, this.importScriptOid.toString ());
            } // if (this.importScriptOid != null)
            else    // try to set importscript name if available
            {
                if (this.importScriptName.length () > 0)
                {
                    importQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_IMPORTSCRIPTNAME, this.importScriptName);
                } // if
            } // else try to set importscript name if available

            // set the flag if the workflow should be used
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ENABLEWORKFLOW, this.p_isEnableWorkflow);
            // set the import files should be sorted
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_SORTIMPORTFILES, this.p_sortImportFiles);
            // set the delete import files flag
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_DELETEIMPORTFILE, this.isDeleteImportFiles);

            // set the backup connector:
            if (this.p_backupConnectorOid != null ||
                this.p_backupConnectorName.length () > 0)
            {
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_CREATEBACKUP, true);

                // set the backup connector oid or the name if applicable:
                if (this.p_backupConnectorOid != null)
                {
                    importQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_BACKUPCONNECTOR,
                        this.p_backupConnectorOid.toString ());
                } // if (this.connectorOid != null)
                else if (this.p_backupConnectorName.length () > 0)
                                            // backup connector name set?
                {
                    importQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_BACKUPCONNECTORNAME,
                        this.p_backupConnectorName);
                } // else if backup connector name set
            } // if

            // set the write log flag
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_WRITELOGFILE, this.isWriteLog);
            // set the flag to display the log (always true)
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_DISPLAYLOGFILE, true);
            // set the append log flag
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_APPENDLOGFILE, this.isAppendLog);
            // set the path of the log file
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_LOGFILEPATH, this.logPath);
            // set the name of the log file
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_LOGFILENAME, this.logFileName);
            // set the notify option
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFY, this.p_isNotify);
            // set notify sender
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFYSENDER, this.p_notifySender);
            // set notify receiver
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFYRECEIVER, this.p_notifyReceiver);
            // set notify subject
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_ERRORNOTIFYSUBJECT, this.p_notifySubject);
            // set validate strcuture option
            importQueryStr += HttpArguments.createArg (
                DIArguments.ARG_VALIDATESTRUCTURE, this.p_isValidate);

            // in case a connector has been set we need to set the import
            // files names
            if (this.connector != null)
            {
                // loop through the array and set the name of the import files
                for (int i = 0; i < importFileNames.length; i++)
                {
                    importQueryStr += HttpArguments.createArg (
                        DIArguments.ARG_IMPORTFILE, importFileNames[i]);
                } // for (int i = 0; i < importFileNames.length; i++)
            } // if (this.connector != null)
            else    // no connector set thus set the file filter
            {
                // set the file filter
                importQueryStr += HttpArguments.createArg (
                    DIArguments.ARG_FILEFILTER, this.p_importFileNameFilter);
            } // else no connector set thus set the file filter

//showDebug("importQueryStr = " + importQueryStr);
            // construct an url to a servlet connection
            url = new URL (m2ServletServerUrl + loginQueryStr + importQueryStr);
//showDebug("login url = " + url.toString());
            // open a http url connection
            connection = (HttpURLConnection) url.openConnection ();
            // set the connection for input/output
            connection.setDoInput (true);
            connection.setDoOutput (true);
            connection.setAllowUserInteraction (false);
            connection.connect ();
//showDebug("openConnection () done");

            // did we get an 401 UNAUTHORIZED code back from the server?
            if (connection.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                // try to negotiate an NTLM authentication
                connection = NTLMClient.negotiateNTLM (url, this.p_ntlmDomain,
                    this.p_ntlmUsername, this.p_ntlmPassword, null);
            } // if

            // do we have a valid connection?
            if (connection != null)
            {
                // now open a read connection:
                bufferedReader = new BufferedReader (
                    new InputStreamReader (connection.getInputStream (), DIConstants.CHARACTER_ENCODING));
                // read the result from the login process.
                while ((inputLine = bufferedReader.readLine ()) != null)
                {
                    // print the output we got from the server
                    ImportAgent_01.print (inputLine);
                } // while
//showDebug("finished reading data");
                bufferedReader.close ();
                // close the connection:
                connection.disconnect ();
            } // if (connection != null)
        } // try
        catch (MalformedURLException e)
        {
            IOHelpers.printError ("startImport ABORTED", this, e, true);
        } // catch
        catch (IOException e)
        {
            IOHelpers.printError ("startImport ABORTED", this, e, true);
        } // catch
        catch (NTLMException e)
        {
            IOHelpers.printError ("startImport ABORTED", this, e, true);
        } // catch
    } // startImport


    /**************************************************************************
     * Calculates the next date to activate the agent. <BR/>
     *
     * @return      the next date to activate the agent
     */
    private Date getNextActivationDate ()
    {
//showDebug ("--- START getNextActivationDate ---");

        GregorianCalendar actualCalendar = new GregorianCalendar ();
        GregorianCalendar newCalendar = new GregorianCalendar ();

        // determine if the import ist done frequently or at a certain time
        switch (this.frequencyType)
        {
            case AgentConstants.FREQUENCYTYPE_MINUTES:
                // type is MINUTES
//showDebug("type is MINUTES");
                newCalendar.add (Calendar.MINUTE, this.everyMinutes);
                break;
            case AgentConstants.FREQUENCYTYPE_DAY:
                // type is DAY
//showDebug("type is DAY");
//showDebug ("this.everyTime: " + this.everyTime.getTime ());
                // set the time
                newCalendar.set (Calendar.HOUR, this.everyTime.get (Calendar.HOUR));
                newCalendar.set (Calendar.MINUTE, this.everyTime.get (Calendar.MINUTE));
                newCalendar.set (Calendar.AM_PM, this.everyTime.get (Calendar.AM_PM));
                newCalendar.set (Calendar.HOUR_OF_DAY, this.everyTime.get (Calendar.HOUR_OF_DAY));
                // check if we need to add a day
                if (!actualCalendar.before (newCalendar))
                {
                    newCalendar.add (Calendar.DAY_OF_MONTH, 1);
                } // if (newCalendar.after (actualCalendar))
                break;
            case AgentConstants.FREQUENCYTYPE_WEEK:
                // type is WEEK
//showDebug("type is WEEK");
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
                    while (!this.everyWeekday [newCalendar.get (Calendar.DAY_OF_WEEK) - 1])
                    {
//showDebug("new date: " + newCalendar.getTime().toString ());
//showDebug("weekday: " + (newCalendar.get(Calendar.DAY_OF_WEEK)));
//showDebug("weekday set: " + this.everyWeekday [newCalendar.get(Calendar.DAY_OF_WEEK)-1]);
                        newCalendar.add (Calendar.DATE, 1);
                    } // while (!this.everyWeekday [newCalendar.get(Calendar.DAY_OF_WEEK)-1])
                } // if (this.isWeekdaySet)
                break;
            case AgentConstants.FREQUENCYTYPE_MONTH:
                // type is MONTH
//showDebug("type is MONTH");
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
//showDebug ("return: " + newCalendar.getTime());
        return newCalendar.getTime ();
    } // getNextActivationDate


    /**************************************************************************
     * Prompt the user to enter a password. <BR/>
     */
    private void getPassword ()
    {
        String enteredPassword = "";
        int i;

        // message for the user to enter the password:
        System.out.print (AgentMessages.MSG_ENTER_PASSWORD + " ");
        BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));

        // get the password from the commandline
        try
        {
            enteredPassword = reader.readLine ();
            this.password = enteredPassword;
            // print some newlines in order to hide the passwort a bit
            // very weak protection but there is no way to securely read
            // a password from the command line:
            for (i = 0; i < 100; i++)
            {
                System.out.println ();
            } // for i
        } // try
        catch (IOException e)
        {
            IOHelpers.printError ("", this, e, true);
            this.password = "";
        } // catch
    } // getPassword


    /**************************************************************************
     * Prints the settings of the importAgent. <BR/>
     */
    public void printSettings ()
    {
        String str;

        ImportAgent_01.print ("\r\n>>> " + AgentMessages.MSG_AGENT_STARTED_WITH_SETTINGS);
        // printing source directory
        ImportAgent_01.print ("> " + AgentTokens.TOK_WORKDIRECTORY + ": " + this.sourcePath);
        // and file filter (is applicable)
        if (this.p_importFileNameFilter.length () > 0)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_FILEFILTER + ": '" + this.p_importFileNameFilter + "'");
        } // if
        // print user and domain setting
        ImportAgent_01.print ("> " + AgentTokens.TOK_INVOKE_IMPORT_WITH_USER + " '" + this.userName +
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
        ImportAgent_01.print (str + "' " + AgentTokens.TOK_WITH_APPPATH + ": '" + this.p_m2AppPath + "'");
        // servlet or asp connection

/* BB not supported anymore
        if (this.isServlet)
            print ("> " + AgentMessages.MSG_USING_SERVLET);
        else
            print ("> " + AgentMessages.MSG_USING_ASP);
*/
        // check if a file connector has been set:
        if (this.connector != null)     // file connector set?
        {
            ImportAgent_01.print ("> " + AgentMessages.MSG_USING_THE_FILECONNECTOR);
        } // if file connector set
        else                            // no file connector set
        {
            // check if connector name or oid set:
            if (this.p_connectorOid != null)
            {
                ImportAgent_01.print ("> " + AgentTokens.TOK_USING_CONNECTOROID + " '" +
                       this.p_connectorOid + "'");
            } // if
            else
            {
                ImportAgent_01.print ("> " + AgentTokens.TOK_USING_CONNECTORNAME + " '" +
                       this.p_connectorName + "'");
            } // else
        } // else no file connector set

        // check if container oid or path:
        if (this.importContainerOid != null)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_USING_IMPORTCONTAINEROID + " '" +
                   this.importContainerOid + "'");
        } // if
        else
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_USING_IMPORTCONTAINERPATH + " '" +
                   this.importContainerPath + "'");
        } // else

        // importscript oid or path?
        if (this.importScriptOid != null)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_USING_IMPORTSCRIPTOID + ": '" +
                   this.importScriptOid + "'");
        } // if (this.importScriptOid != null)
        else if (this.importScriptName.length () > 0)
        {
            ImportAgent_01.print ("> " +
                AgentTokens.TOK_USING_IMPORTSCRIPTNAME + ": '" +
                this.importScriptName + "'");
        } // else if (this.importScriptName.length () > 0)

        // translator oid or path
        if (this.translatorOid  != null)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_USING_TRANSLATOROID + ": '" +
                   this.translatorOid + "'");
        } // if
        else if (this.translatorName.length () > 0)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_USING_TRANSLATORNAME + ": '" +
                   this.translatorName + "'");
        } // else if (this.translatorName.length () > 0)

        // user credentials:
        if (this.p_ntlmUsername.length () > 0)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_NT_USERCREDENTIALS + ": " +
                   this.p_ntlmDomain + "\\" + this.p_ntlmUsername);
        } // if (this.ntlmUsername.length () > 0)

        if (this.isDeleteImportFiles)
        {
            ImportAgent_01.print ("> " + AgentMessages.MSG_FILES_WILL_BE_DELETED);
        } // if

        if (this.p_isEnableWorkflow)
        {
            ImportAgent_01.print ("> " + AgentMessages.MSG_ENABLEWORKFLOW);
        } // if

        if (this.p_isValidate)
        {
            ImportAgent_01.print ("> " + AgentMessages.MSG_VALIDATESTRUCTURE);
        } // if

        if (this.p_sortImportFiles == UtilConstants.ORDER_ASC)
        {
            ImportAgent_01.print ("> " + AgentMessages.MSG_FILES_WILL_BE_SORTED_ASC);
        } // if
        else if (this.p_sortImportFiles == UtilConstants.ORDER_DESC)
        {
            ImportAgent_01.print ("> " + AgentMessages.MSG_FILES_WILL_BE_SORTED_DESC);
        } // if

        // schedule settings:
        switch (this.frequencyType)
        {
            case AgentConstants.FREQUENCYTYPE_ONCE:
                // frequency is minutes
                ImportAgent_01.print ("> " + AgentMessages.MSG_AGENT_FREQUENCY_ONCE);
                break;
            case AgentConstants.FREQUENCYTYPE_MINUTES:
                // frequency is minutes
                ImportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_EVERY + " " +
                       this.everyMinutes + " " + AgentTokens.TOK_MINUTES + ".");
                break;
            case AgentConstants.FREQUENCYTYPE_DAY:
                // frequency is day
                ImportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_EVERY_DAY + " " + this.timeStr + ".");
                break;
            case AgentConstants.FREQUENCYTYPE_WEEK:
                // frequency is week
                ImportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_AT + " " + this.timeStr);
                ImportAgent_01.print ("> " + AgentTokens.TOK_AT_WEEKDAYS + ": " + this.everyStr + ".");
                break;
            case AgentConstants.FREQUENCYTYPE_MONTH:
                // frequency is month
                ImportAgent_01.print ("> " + AgentTokens.TOK_AGENTS_ACTIVATED_AT + " " + this.timeStr);
                ImportAgent_01.print ("> " + AgentTokens.TOK_EVERY + " " + this.everyStr + ". " +
                       AgentTokens.TOK_DAY_OF_MONTH + ".");
                break;
            default:
                break;
        } // switch (this.frequencyType)

        // check if backup connector name or oid set:
        if (this.p_backupConnectorOid != null)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_USING_BACKUPCONNECTOROID + " '" +
                   this.p_backupConnectorOid + "'");
        } // if
        else if (this.p_backupConnectorName.length () > 0)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_USING_BACKUPCONNECTORNAME + " '" +
                   this.p_backupConnectorName + "'");
        } // else if

        // log setting:
        if (this.isWriteLog)
        {
            ImportAgent_01.print (">>> " + AgentMessages.MSG_LOG_SETTINGS);
            ImportAgent_01.print ("> " + AgentTokens.TOK_LOGFILENAME + ": " + this.logFileName);
            ImportAgent_01.print ("> " + AgentTokens.TOK_LOGFILEPATH + ": " + this.logPath);
            if (this.isAppendLog)
            {
                ImportAgent_01.print ("> " + AgentMessages.MSG_LOG_WILL_BE_APPENDED);
            } // if
        } // if (this.isWriteLog)

        // print error notification settings:
        if (this.p_isNotify)
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_NOTIFY_ACTIVATED + ":");
            ImportAgent_01.print ("  " + AgentTokens.TOK_WITH_RECEIVER + ": '" + this.p_notifyReceiver + "'" + "\r\n  " +
                   AgentTokens.TOK_WITH_SENDER + ": '" + this.p_notifySender + "\r\n  " +
                   AgentTokens.TOK_MAILSUBJECT + ": '" + this.p_notifySubject + "'");
        } // if (this.p_isNotify)
        else    // error notification deactivated
        {
            ImportAgent_01.print ("> " + AgentTokens.TOK_NOTIFY_DEACTIVATED);
        } // else error notification deactivated
    } // printSettings


    /**************************************************************************
     * Prints the usage of the importAgent to the system out. <BR/>
     */
    public static void printUsage ()
    {
        // print (DIMessages.MSG_IMPORTAGENT_USAGE);
        ImportAgent_01.print ("\r\n" + AgentMessages.MSG_IMPORTAGENT_USAGE);
    } // printUsage


    /**************************************************************************
     * Prints the menu keys to control the agent. <BR/>
     */
    public static void printMenu ()
    {
        // print (DIMessages.MSG_AGENT_USAGE);
        ImportAgent_01.print (AgentMessages.MSG_AGENT_MENU);
        ImportAgent_01.print ("> " + AgentMessages.MSG_AGENT_SLEEPING);
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
        // This does not print unicode characters see also IBS-726
        System.out.println (message);
    } // print


    /**************************************************************************
     * Shows a debug message. This message can be switched off and on. <BR/>
     *
     * @param   message     the debug message to be displayed
     */
    public void showDebug (String message)
    {
        if (this.p_isDebug)
        {
            ImportAgent_01.print ("DEBUG: ImportAgent_01: "  + message);
        } // if
    } // showDebug

}  // class ImportAgent_01
