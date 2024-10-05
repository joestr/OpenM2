/*
 * Class: ServerRecord .java
 */

// package:
package ibs.service.conf;

// imports:
import ibs.BaseObject;
import ibs.service.conf.ConfigurationConstants;


/******************************************************************************
 * This class holds the configuration properties. It includes the. <BR/>
 * - p_applicationServer. <BR/>
 * - p_applicationServerPort. <BR/>
 * - p_sslServer. <BR/>
 * - p_sslServerPort. <BR/>
 * - p_ssl. <BR/>
 *
 * @version     $Id: ServerRecord.java,v 1.11 2007/07/31 19:13:58 kreimueller Exp ${cursor}
 *
 * @author      Bernd Martin (BM) 001127
 ******************************************************************************
 */
public class ServerRecord extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ServerRecord.java,v 1.11 2007/07/31 19:13:58 kreimueller Exp $";


    // these properties are 'temporary needed' properties
    // they include the entries of one configurationtuple of the ibssystem.cfg
    /**
     * The name of the applicationserver. <BR/>
     */
    protected String p_applicationServer = "";

    /**
     * The port of the applicationserver. <BR/>
     */
    protected int p_applicationServerPort = -1;

    /**
     * The name of the sslserver. <BR/>
     */
    protected String p_sslServer = "";

    /**
     * The port of the sslserver. <BR/>
     */
    protected int p_sslServerPort = -1;

    /**
     * The flag if SSL should be used or not. <BR/>
     */
    protected boolean p_ssl = ConfigurationConstants.SSL_DEFAULT;


    /**************************************************************************
     * Constructor of ServerRecord. <BR/>
     */
    public ServerRecord ()
    {
        this.p_applicationServer = "";
        this.p_applicationServerPort = -1;
        this.p_sslServer = "";
        this.p_sslServerPort = -1;
        this.p_ssl = false;
    } // ServerRecord constructor


    /**************************************************************************
     * Constructor of ServerRecord. <BR/>
     *
     * @param  applicationServer        The name of the application server.
     * @param  sslServer                The name of the p_ssl server.
     * @param  applicationServerPort    The port for the application server.
     * @param  sslServerPort            The port for the p_ssl server.
     * @param  ssl                      The flag if p_ssl must be used or not.
     */
    public ServerRecord  (String applicationServer, String sslServer,
                          String applicationServerPort, String sslServerPort,
                          String ssl)
    {
        this ();

        String applicationServerLocal = applicationServer; // variable for local assignments
        String sslServerLocal = sslServer; // variable for local assignments

        // if no server entry given (no name given) then
        // set to local host
        if ((applicationServerLocal == null || applicationServerLocal.trim ().length () == 0) &&
            (sslServerLocal == null || sslServerLocal.trim ().length () == 0))
        {
            applicationServerLocal = sslServerLocal = "localhost";
            this.p_applicationServerPort =
                ConfigurationConstants.APPLICATIONSERVERPORT_DEFAULT;
        } // if all necessary attributes are null
        else if ((sslServerLocal == null || sslServerLocal.trim ().length () == 0) &&
            (applicationServerLocal != null && applicationServerLocal.length () > 0))
        {
            sslServerLocal = applicationServerLocal;
            this.p_sslServerPort = ConfigurationConstants.SSLSERVERPORT_DEFAULT;
        } // else if p_ssl server was null

        // set the application server
        this.p_applicationServer = this.returnServerName (applicationServerLocal,
            this.p_applicationServer);

        // set the application server port
        this.p_applicationServerPort = this.returnServerPort (applicationServerPort,
            this.p_applicationServerPort);


        // set the ssl Server
        this.p_sslServer = this.returnServerName (sslServerLocal,
            this.p_sslServer);

        // set the ssl entry
        this.p_sslServerPort = this.returnServerPort (sslServerPort.trim (),
            this.p_sslServerPort);

        if (ssl != null && ssl.trim ().equalsIgnoreCase ("true"))
        {
            this.p_ssl = Boolean.getBoolean (ssl);
        } // if set to true then set it, otherwise let it false
    } // ServerRecord  constructor


    /**************************************************************************
     * Returns the servername out of the string 'in' if it was not null and
     * not the empty string. If not 'defaultValue' is returned.
     *
     * @param   in              The original string.
     * @param   defaultValue    The default value.
     *
     * @return The servername.
     */
    private String returnServerName (String in, String defaultValue)
    {
        // sets the value for the server to the in param if it is not
        // null and not empty, otherwise set it to the default value
        if ((in != null) && in.length () > 0)
        {
            return in.trim ();
        } // if an applicationserver is given

        return defaultValue;
    } // returnServerName


    /**************************************************************************
     * Returns the port number contained in 'in' if it was possible to convert
     * the string into an integer. If not or if the integer was null then the
     * defaultValue is returned.
     *
     * @param   in              The original string.
     * @param   defaultValue    The default value.
     *
     * @return The portnumber.
     */
    private int returnServerPort (String in, int defaultValue)
    {
        // if port given set it to the port number
        // if port not given set it to the default value
        if (in != null && in.length () > 0)
        {
            try
            {
                return Integer.parseInt (in.trim ());
            } // try
            catch (NumberFormatException e)
            {
                return ConfigurationConstants.APPLICATIONSERVERPORT_DEFAULT;
            } // catch
        } // if applicationServerPort set

        return ConfigurationConstants.APPLICATIONSERVERPORT_DEFAULT;
    } // getServerPort


    /**************************************************************************
     * Returns the ApplicationServer of the current object. <BR/>
     *
     * @return  The value for the ApplicationServer of the current object.
     */
    public String getApplicationServer ()
    {
        // returns the attribute p_applicationServer
        return this.p_applicationServer;
    } // getApplicationServer


    /**************************************************************************
     * Returns the SslServer of the current object. <BR/>
     *
     * @return  The value for the SslServer of the current object.
     */
    public String getSslServer ()
    {
         // returns the attribute p_sslServer
        return this.p_sslServer;
    } // getSslServer


    /**************************************************************************
     * Returns the p_applicationServerPort of the current object. <BR/>
     *
     * @return  The value for the p_applicationServerPort of the current object.
     */
    public int getApplicationServerPort ()
    {
        // returns the attribute p_applicationServerPort
        return this.p_applicationServerPort;
    } // getApplicationServerPort


    /**************************************************************************
     * Returns the SslServerPort of the current object. <BR/>
     *
     * @return  The value for the SslServerPort of the current object.
     */
    public int getSslServerPort ()
    {
        // returns the attribute p_sslServerPort
        return this.p_sslServerPort;
    } // getSslServerPort


    /**************************************************************************
     * Returns the p_ssl flag of the current object. <BR/>
     *
     * @return  The value for the p_ssl flag of the current object.
     */
    public boolean getSsl ()
    {
        // returns the attribute p_ssl
        return this.p_ssl;
    } // getSsl


    /**************************************************************************
     * Sets the ApplicationServer in the current object. <BR/>
     *
     * @param   name the name of the server
     */
    public void setApplicationServer (String name)
    {
        // sets the attribute p_applicationServer
        this.p_applicationServer = name;
    } // setApplicationServer


    /*************************************************************************
     * Sets the SslServer in the current object. <BR/>
     *
     * @param   name the name of the server
     */
    public void setSslServer (String name)
    {
        // sets the attribute p_sslServer
        this.p_sslServer = name;
    } // setSslServer


    /*************************************************************************
     * Prints the content of the current object. <BR/>
     *
     * @param   i if the object is stored in a vector the number can
     *            be given here to show it in the output
     *
     * @return the formatted string representing the object
     */
    public String toString (int i)
    {
        // this is the string which is concatenated to the token name
        String filling = ":                                                  ";

        // the variables are initialized with the appropriate token name,
        // the position in the vector, represented with the parameter i
        // and some spaces.
        String appServ = ConfigurationConstants.TOK_NAME + filling;
        String appServPort = ConfigurationConstants.TOK_PORT + filling;
        String sslServ = ConfigurationConstants.TOK_SSLNAME + filling;
        String sslServPort = ConfigurationConstants.TOK_SSLPORT + filling;
        String sslFlag = ConfigurationConstants.TOK_SSL + filling;

        // the output format is created with print
        return this.print (i, appServ, sslServ, appServPort, sslServPort,
            sslFlag);
    } // toString


    /***********************************************************************
     * Prints the content of the current object. <BR/>
     *
     * @return the formatted string representing the object
     */
    public String toString ()
    {
        // this is the string which is concatenated to the token name
        String filling = ":                                                  ";

        // the variables are initialized with the appropriate token name,
        // the position in the vector, represented with the parameter i
        // and some spaces.
        String appServ = ConfigurationConstants.TOK_NAME + filling;
        String appServPort = ConfigurationConstants.TOK_PORT + filling;
        String sslServ = ConfigurationConstants.TOK_SSLNAME + filling;
        String sslServPort = ConfigurationConstants.TOK_SSLPORT + filling;
        String sslFlag = ConfigurationConstants.TOK_SSL + filling;

        // the output format is created with print
        return this.print (-1, appServ, sslServ, appServPort, sslServPort,
            sslFlag);
    } // toString


    /***************************************************************************
     * Prints the content of the current object with the given indicators. This
     * method just simplifies the formatting process. <BR/>
     *
     * @param i Number of the server.
     * @param appServ Name of application server.
     * @param sslServ Name of SSL server.
     * @param appServPort Port for application server.
     * @param sslServPort Port for SSL server.
     * @param sslFlag The ssl flag.
     *
     * @return the formatted string representing the object
     */
    private String print (int i, String appServ, String sslServ,
                           String appServPort, String sslServPort,
                           String sslFlag)
    {
        String appServLocal = appServ;  // variable for local assignments
        String sslServLocal = sslServ;  // variable for local assignments
        String appServPortLocal = appServPort; // variable for local assignments
        String sslServPortLocal = sslServPort; // variable for local assignments
        String sslFlagLocal = sslFlag;  // variable for local assignments
        // nameLength is the same value as in the class configuration
        // in the method toString
        int nameLength = 25;
        String msg = "";
        String nl = "\n";

        // all the variables should have the same length
        appServLocal = appServLocal.substring (0, nameLength);
        appServPortLocal = appServPortLocal.substring (0, nameLength);
        sslServLocal = sslServLocal.substring (0, nameLength);
        sslServPortLocal = sslServPortLocal.substring (0, nameLength);
        sslFlagLocal = sslFlagLocal.substring (0, nameLength);

        if (i == -1)
        {
            msg = ConfigurationConstants.TOK_SERVER + nl;
        } // if no index necessary
        else
        {
            msg = ConfigurationConstants.TOK_SERVER + i + nl;
        } // else index necessary

        // show string for the application server
        msg += appServLocal + this.p_applicationServer + nl;

        // show string for the application server port
        msg += appServPortLocal + this.p_applicationServerPort + nl;

        // show string for the ssl server
        msg += sslServLocal + this.p_sslServer + nl;

        // show string for the ssl server port
        msg += sslServPortLocal + this.p_sslServerPort + nl;

        // show string for the ssl server port
        msg += sslFlagLocal + this.p_ssl + nl + nl;

        // returns the formatted string
        return msg;
    } // print


    /***************************************************************************
     * If an application server was given the return value is <code>true</code>,
     * if not it is <code>false</code>.
     *
     * @return The boolean value if an application server was given or not for
     *         the current record.
     */
    public boolean isApplicationServerAvailable ()
    {
        // check if the application server is not set
        if ((this.p_applicationServer == null) ||
            (this.p_applicationServer.length () == 0))
        {
            return false;
        } // if the application server is not set

        // the application server is set
        return true;
    } // isApplicationServerAvailable

} // ServerRecord
