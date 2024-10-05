/*
 * Class: AReportingEngine.java
 */

// package:
package ibs.service.reporting;

// imports:
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.service.conf.Configuration;


/******************************************************************************
 * This class implementes the abstract reporting engine to be extended
 * by reportingEngine subclasses that support a specific reportingEngine. <BR/>
 *
 * @version     $Id: AReportingEngine.java,v 1.5 2010/04/20 08:50:55 jzlattinger Exp $
 *
 * @author      Bernd Buchegger, 20060815
 ******************************************************************************
 */
public abstract class AReportingEngine implements IReportingEngine
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AReportingEngine.java,v 1.5 2010/04/20 08:50:55 jzlattinger Exp $";

    //
    // CONSTANTS:
    //
    /**
     * parameter for JDBC driver class. <BR/>
     */
    public static final String ARG_JDBCDRIVERCLASS = "paramJDBCDriverClass";

    /**
     * parameter for JDBC driver class. <BR/>
     */
    public static final String ARG_JDBCDRIVERURL = "paramJDBCDriverUrl";

    /**
     * parameter for JDBC driver class. <BR/>
     */
    public static final String ARG_JDBCUSERNAME = "paramJDBCUsername";

    /**
     * parameter for JDBC driver class. <BR/>
     */
    public static final String ARG_JDBCPASSWORD = "paramJDBCPassword";

    /**
     * parameter for JDBC driver class. <BR/>
     */
    public static final String ARG_JDBCJNDIURL = "paramJDBCJNDIUrl";

    /**
     * parameter for the local. <BR/> 
     */
    public static final String ARG_LOCALE = "paramLocale";
    
    
    //
    // MESSAGES:
    //
    /**
     * Message for launching the reporting engine. <BR/>
     */
    private static String MSG_NO_DBCONNECTION = "No DB connection set!";

    //
    // CLASS PROPERTIES:
    //
    /**
     * The activation flag used to activate/deactivate the reportingEngine.<BR/>
     */
    protected boolean p_isActivated = false;

    /**
     * The JDBC driver class. <BR/>
     */
    protected String p_jdbcDriverClass = null;

    /**
     * The JDBC driver class. <BR/>
     */
    protected String p_jdbcDriverUrl = null;

    /**
     * The JDBC username. <BR/>
     */
    protected String p_jdbcUsername = null;

    /**
     * The JDBC user password. <BR/>
     */
    protected String p_jdbcPassword = null;

    /**
     * The JDBC JNDI url. <BR/>
     */
    protected String p_jdbcJNDIUrl = null;


    /**************************************************************************
     * Constructor for the AReportingEngine object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public AReportingEngine ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's private properties if applicable.

    } // AReportingEngine


    /**************************************************************************
     * Return activation flag.<BR/>
     *
     * @return the p_isActivated
     */
    public boolean isActivated ()
    {
        return this.p_isActivated;
    } // isActivated


    /**************************************************************************
     * Set the activation flag.<BR/>
     *
     * @param activated the p_isActivated to set
     */
    public void setActivated    (boolean activated)
    {
        this.p_isActivated = activated;
    } // setActivated

    /**************************************************************************
     * Set the p_JDBCDriverClass property.
     *
     * @param   conf    Configuration for connection.
     */
    public void setJDBCConnection (Configuration conf)
    {
        // set the JDBC connection properties
        this.setJDBCDriverClass (conf.getDbJdbcDriverClass ());
        this.setJDBCDriverUrl (conf.getDbConnectionString ());
        this.setJDBCUsername (conf.getDbUserName ());
        this.setJDBCPassword (conf.getDbPassword ());
        // BB20070822: We do not have any JNDI connection information
        // Note that the DB-SID obviously cannot be passed
        //setJDBCJNDIUrl (conf.getDbSid());
    } // setJDBCDriverClass


    /**************************************************************************
     * Get the p_JDBCDriverClass property.
     *
     * @return the p_JDBCDriverClass
     */
    public String getJDBCDriverClass ()
    {
        return this.p_jdbcDriverClass;
    } // getJDBCDriverClass


    /**************************************************************************
     * Get the p_JDBCDriverUrl property.
     *
     * @return the p_JDBCDriverUrl
     */
    public String getJDBCDriverUrl ()
    {
        return this.p_jdbcDriverUrl;
    } // getJDBCDriverUrl


    /**************************************************************************
     * Get the p_JDBCUsername property.
     *
     * @return the p_JDBCUsername
     */
    public String getJDBCUsername ()
    {
        return this.p_jdbcUsername;
    } // getJDBCUsername


    /**************************************************************************
     * Get the p_JDBCPassword property.
     *
     * @return the p_JDBCPassword
     */
    public String getJDBCPassword ()
    {
        return this.p_jdbcPassword;
    } // getJDBCPassword


    /**************************************************************************
     * Get the p_JDBCJNDIUrl property.
     *
     * @return the p_JDBCJNDIUrl
     */
    public String getJDBCJNDIUrl ()
    {
        return this.p_jdbcJNDIUrl;
    } // p_JDBCJNDIUrl


    /**************************************************************************
     * Set the p_JDBCDriverClass property.
     *
     * @param driverClass the p_JDBCDriverClass to set
     */
    public void setJDBCDriverClass (String driverClass)
    {
        this.p_jdbcDriverClass = driverClass;
    } // setJDBCDriverClass


    /**************************************************************************
     * Set the p_JDBCDriverUrl property.
     *
     * @param driverUrl the p_JDBCDriverUrl to set
     */
    public void setJDBCDriverUrl (String driverUrl)
    {
        this.p_jdbcDriverUrl = driverUrl;
    } // setJDBCDriverUrl


    /**************************************************************************
     * Set the p_JDBCUsername property.
     *
     * @param username the p_JDBCUsername to set
     */
    public void setJDBCUsername (String username)
    {
        this.p_jdbcUsername = username;
    } // setJDBCUsername


    /**************************************************************************
     * Set the p_JDBCPassword property.
     *
     * @param password the p_JDBCPassword to set as byte array
     */
    public void setJDBCPassword (byte[] password)
    {
        this.p_jdbcPassword = new String (password);
    } // setJDBCPassword


    /**************************************************************************
     * Set the p_JDBCPassword property.
     *
     * @param password the p_JDBCPassword to set
     */
    public void setJDBCPassword (String password)
    {
        this.p_jdbcPassword = password;
    } // setJDBCPassword


    /**************************************************************************
     * Set the p_JDBCJNDIUrl property.
     *
     * @param url the p_JDBCJNDIUrl to set
     */
    public void setJDBCJNDIUrl (String url)
    {
        this.p_jdbcJNDIUrl = url;
    } // setJDBCJNDIUrl


    /***********************************************************************
     * Load configuration of the reporting engine. <BR/>
     *
     * @param env   the environment to read the servername from
     * @param app     the application object to read the configuration of the
     *                   db connection.
     *
     * @throws ReportingException An configuration error occurred.
     */
    public void loadConfiguration (Environment env, ApplicationInfo app)
        throws ReportingException
    {
        if (app.configuration != null)
        {
            // set the DB connection
            this.setJDBCConnection ((Configuration) app.configuration);
        } // if
        else    // no configuration set
        {
            throw new ReportingException (AReportingEngine.MSG_NO_DBCONNECTION);
        } // else no configuration set
    } // loadConfiguration

} // interface AReportingEngine
