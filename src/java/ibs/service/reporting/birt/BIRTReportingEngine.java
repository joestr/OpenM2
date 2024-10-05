/*
 * Class: BIRTReportingEngine.java
 */

// package:
package ibs.service.reporting.birt;

import ibs.bo.States;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.session.ApplicationInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.reporting.AReportingEngine;
import ibs.service.reporting.ReportingException;
import ibs.tech.html.IE302;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.Helpers;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

// imports:


/******************************************************************************
 * This class implements the support for the BIRT reporting engine. <BR/>
 *
 * @version     $Id: BIRTReportingEngine.java,v 1.9 2010/04/20 08:50:55 jzlattinger Exp $
 *
 * @author      Bernd Buchegger, 20060815
 ******************************************************************************
 */
public class BIRTReportingEngine extends AReportingEngine
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BIRTReportingEngine.java,v 1.9 2010/04/20 08:50:55 jzlattinger Exp $";

    //
    // CONSTANTS:
    //
    /**
     * parameter for query string. <BR/>
     */
    public static final String ARG_QUERY = "queryStr";

    /**
     * parameter for query name. <BR/>
     */
    public static final String ARG_QUERYNAME = "queryName";

    /**
     * parameter for query name. <BR/>
     */
    public static final String ARG_OUTPUTFORMAT = "outputFormat";

    //
    // BIRT CONSTANTS:
    //
    /**
     * parameter name for frameset. <BR/>
     */
    public static final String PARAM_FRAMESET = "frameset";

    /**
     * parameter name for report name. <BR/>
     */
    public static final String PARAM_REPORT = "__report";

    /**
     * parameter name for format. <BR/>
     */
    public static final String PARAM_OUTPUTFORMAT = "__format";

    /**
     * parameter name for overwrite. <BR/>
     */
    public static final String PARAM_OVERWRITE = "__overwrite";

    //
    // MESSAGES:
    //
    /**
     * Message for launching the reporting engine. <BR/>
     */
    private static String MSG_LAUNCH_REPORTINGENGINE = "launching reporting engine ...";

    /**
     * Message for configuration error. <BR/>
     */
    private static String MSG_CONFIG_ERROR = "BIRT ReportingEngine Configuration ERROR: ";

    /**
     * Message for error when no query set. <BR/>
     */
    private static String MSG_NO_QUERY = "Query has not been set!";

    /**
     * Message for error when configuration could not be loaded. <BR/>
     */
    private static String MSG_COULD_NOT_LOAD_CONFIGURATION = "Could not load reporting engine configuration: ";

    /**
     * Message for error when no report is set. <BR/>
     * @deprecated  This property is never used.
     */
//    private static String MSG_NO_REPORT_NAME = "Invalid report name!";

    //
    // CLASS PROPERTIES:
    //
    /**
     * The url for the reporting engine invocation. <BR/>
     */
    private String p_invocationUrl = null;

    /**
     * The directory where the reports files are stored. <BR/>
     */
    private String p_reportDir = null;

    /**
     * The extension for report files. <BR/>
     */
    private String p_fileExtension = ".rptdesign";

    /**
     * The output format for the report. <BR/>
     */
    private String p_outputFormat = "pdf";

    /**
     * Overwrite file? <BR/>
     * @deprecated  This property is never used.
     */
//    private String p_overwrite = "true";

    /**************************************************************************
     * Constructor for the BIRTReportingEngine object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public BIRTReportingEngine ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's private properties if applicable.

    } // BIRTReportingEngine


    /**************************************************************************
     * Return the fileExtension.<BR/>
     *
     * @return the p_fileExtentsion
     */
    public String getFileExtension ()
    {
        return this.p_fileExtension;
    } // getFileExtension


    /**************************************************************************
     * Return the invocationUrl.<BR/>
     *
     * @return the p_invocationUrl
     */
    public String getInvocationUrl ()
    {
        return this.p_invocationUrl;
    } // getInvocationUrl


    /**************************************************************************
     * Return the reportDir.<BR/>
     *
     * @return the p_reportDir
     */
    public String getReportDir ()
    {
        return this.p_reportDir;
    } // getReportDir


    /**************************************************************************
     * Set the fileExtentsion. <BR/>
     *
     * @param extension the p_fileExtentsion to set
     */
    public void setFileExtension (String extension)
    {
        String extensionLocal = extension; // variable for local assignments

        // check if the fileextension has the format ".<extension>"
        if (extensionLocal != null && extensionLocal.length () > 0 &&
            !extensionLocal.startsWith ("."))
        {
            // if not add the dot
            extensionLocal = "." + extensionLocal;
        } // if (!extentsion.startsWith ("."))
        this.p_fileExtension = extensionLocal;
    } // setFileExtension


    /**************************************************************************
     * Set the invocationUrl. <BR/>
     *
     * @param url           the p_invocationUrl to set
     * @param serverName    the name of the server in case it has not been set
     *                      in the url. Use env.getServerName ()
     *                      to get the server name.
     */
    public void setInvocationUrl (String url, String serverName)
    {
        String extUrl = url;
        // check if a relative url has been set
        // in that case extend the url with the given server name
        if (!url.toUpperCase ().startsWith (IOConstants.URL_HTTP.toUpperCase ()))
        {
            extUrl = IOConstants.URL_HTTP + serverName;
            if (!url.startsWith ("/"))
            {
                extUrl += "/";
            } // if
            extUrl += url;
        } // if
        this.p_invocationUrl = extUrl;
    } // setInvocationUrl


    /**************************************************************************
     * Set the reportDir. <BR/>
     *
     * @param dir the p_reportDir to set
     */
    public void setReportDir (String dir)
    {
        String dirLocal = dir;          // variable for local assignments
        // check if the path contains a ending separator
        if (!dirLocal.endsWith (File.separator))
        {
            dirLocal += File.separator;
        } // if

        this.p_reportDir = dirLocal;
    } // setReportDir


    /***************************************************************************
     * Open the report using the given reportName and the given queryString.
     * <BR/>
     *
     * @param   reportName  the name of the report to be opened
     * @param   queryStr    the query string for the report
     * @param   env         The current environment.
     *
     * @throws ReportingException An error occurred.
     */
    public void openReport (String reportName, String queryStr, Environment env)
        throws ReportingException
    {
        // open the report with the given queryString within
        // has a query string been defined?
        if (queryStr != null)
        {
            // now write the generated response
            StringBuffer openReport =
                this.generateReportRequest (reportName, queryStr, env);
            openReport.append ("<SCRIPT LANGUAGE=\"Javascript\">")
                .append ("document.reportForm.submit ();")
                .append ("</SCRIPT>")
                .append ("</BODY></HTML>");
            env.write (openReport.toString ());

        } // if (queryStr != null);
        else // could not generate the query
        {
            throw new ReportingException (BIRTReportingEngine.MSG_NO_QUERY);
        } // else // could not generate the query
    } // openReport


    /**************************************************************************
     * Generate a request string for a report using the given reportName
     * and the given queryString. <BR/>
     *
     * @param   reportName  the name of the report to be opened
     * @param   queryStr    the query string for the report
     * @param   env         The current environment.
     *
     * @return  The generated request.
     */
    private StringBuffer generateReportRequest (String reportName,
                                                String queryStr, Environment env)
    {
         // open the report with the given queryString within
         // the BIRT reporting engine
        StringBuffer urlStr;
        StringBuffer output = null;
        // end of value and end of tag: ... "/>
        StringBuffer startInput = new StringBuffer ("<INPUT TYPE=\"HIDDEN\" VALUE=\"");
        StringBuffer nameAttr = new StringBuffer ("\" NAME=\"");
        StringBuffer endInput = new StringBuffer ("\"/>");
        // has a query string been defined?
        if (queryStr != null)
        {

            // construct a redirect to the reporting engine submitting
            // the query string in the url
            urlStr = new StringBuffer ().append (this.getInvocationUrl ());
            output = new StringBuffer ()
                .append ("<HTML><BODY>")
                .append ("<FORM METHOD=\"POST\" NAME=\"reportForm\" ACTION=\"")
                .append (urlStr)
                .append ("\">")
                .append ("<CODE>")
                .append (BIRTReportingEngine.MSG_LAUNCH_REPORTINGENGINE)
                .append ("</CODE>" + IE302.TAG_NEWLINE)
                // query definition
                .append (startInput)
                .append (StringHelpers.replace (queryStr.toString (), "\"", "&quot;"))
                .append (nameAttr).append (BIRTReportingEngine.ARG_QUERY)
                .append (endInput)
                // query name
                .append (startInput)
                .append (reportName)
                .append (nameAttr).append (BIRTReportingEngine.ARG_QUERYNAME)
                .append (endInput)
                // output format
                .append (startInput)
                .append (this.p_outputFormat)
                .append (nameAttr).append (BIRTReportingEngine.ARG_OUTPUTFORMAT)
                .append (endInput);

            // Add the JDBC connection properties
            if (this.p_jdbcDriverClass != null)
            {
                output.append (startInput)
                    .append (this.getJDBCDriverClass ())
                    .append (nameAttr).append (ARG_JDBCDRIVERCLASS)
                    .append (endInput);
            } // if (this.p_JDBCDriverClass != null)
            if (this.p_jdbcDriverUrl != null)
            {
                output.append (startInput)
                    .append (this.getJDBCDriverUrl ())
                    .append (nameAttr).append (ARG_JDBCDRIVERURL)
                    .append (endInput);
            } // if (this.p_JDBCDriverUrl != null)
            if (this.p_jdbcUsername != null)
            {
                output.append (startInput)
                    .append (this.getJDBCUsername ())
                    .append (nameAttr).append (ARG_JDBCUSERNAME)
                    .append (endInput);
            } // if (this.p_JDBCUsername != null)
            if (this.p_jdbcPassword != null)
            {
                output.append (startInput)
                    .append (this.getJDBCPassword ())
                    .append (nameAttr).append (ARG_JDBCPASSWORD)
                    .append (endInput);
            } // if (this.p_JDBCPassword != null)
            if (this.p_jdbcJNDIUrl != null)
            {
                output.append (startInput)
                    .append (this.getJDBCJNDIUrl ())
                    .append (nameAttr).append (ARG_JDBCJNDIURL)
                    .append (endInput);
            } // if (this.p_JDBCJNDIUrl != null)
            
            // Append locale
            output.append(startInput)
            	  .append(MultilingualTextProvider.getUserLocale(env).getLocale().toString())
            	  .append(nameAttr)
            	  .append(ARG_LOCALE)
            	  .append(endInput);
            
            
            output.append ("</FORM>");
            // now write the generated response
            //env.write (output.toString ());
        } // if (queryStr != null);
        // return output response
        return output;
    } // generateReportRequest


    /**************************************************************************
     * Returns a string with the params necessary when requesting a report via
     * a POST.<BR/>
     *
     * @param   reportName  the name of the report to be opened
     * @param   queryStr    the query string for the report
     * @param   env         The current environment.
     *
     * @return  The generated params.
     */
    public StringBuffer getReportRequestPostParams (String reportName,
            String queryStr, Environment env)
    {
        StringBuffer output = null;

        // has a query string been defined?
        if (queryStr != null)
        {
            output = new StringBuffer ()
                .append (BIRTReportingEngine.ARG_QUERY).append ("=");

            try
            {
                output.append (URLEncoder.encode (queryStr.toString (), "UTF-8"));
            } // try
            catch (UnsupportedEncodingException e)
            {
                //Alternative handling
                output.append (StringHelpers.replace (queryStr.toString (), "\"", "&quot;"));
            } // catch

            output
                .append ("&").append (BIRTReportingEngine.ARG_QUERYNAME)
                .append ("=").append (reportName)
                .append ("&").append (BIRTReportingEngine.ARG_OUTPUTFORMAT)
                .append ("=").append (this.p_outputFormat);

            // Add the JDBC connection properties
            if (this.p_jdbcDriverClass != null)
            {
                output
                    .append ("&").append (ARG_JDBCDRIVERCLASS)
                    .append ("=").append (this.getJDBCDriverClass ());
            } // if (this.p_JDBCDriverClass != null)
            if (this.p_jdbcDriverUrl != null)
            {
                output
                    .append ("&").append (ARG_JDBCDRIVERURL)
                    .append ("=").append (this.getJDBCDriverUrl ());
            } // if (this.p_JDBCDriverUrl != null)
            if (this.p_jdbcUsername != null)
            {
                output
                    .append ("&").append (ARG_JDBCUSERNAME)
                    .append ("=").append (this.getJDBCUsername ());
            } // if (this.p_JDBCUsername != null)
            if (this.p_jdbcPassword != null)
            {
                output
                    .append ("&").append (ARG_JDBCPASSWORD)
                    .append ("=").append (this.getJDBCPassword ());
            } // if (this.p_JDBCPassword != null)
            if (this.p_jdbcJNDIUrl != null)
            {
                output
                    .append ("&").append (ARG_JDBCJNDIURL)
                    .append ("=").append (this.getJDBCJNDIUrl ());
            } // if (this.p_JDBCJNDIUrl != null)
        } // if (queryStr != null);

        return output;
    } // getReportRequestPostParams


    /**************************************************************************
     * Check if the reporting engine has a report defined with the given
     * report name. <BR/>
     * In case the reporting engine has been deactivated the method
     * always returns <code>false</code>. <BR/>
     *
     * @param   reportName  The name of the report to be checked.
     *
     * @return <code>true</code> if report is defined or
     *            <code>false</code> otherwise
     */
    public boolean isReportDefined (String reportName)
    {
        // in case the reporting engine has been deactivated
        // the method always returns false
        if (this.isActivated ())
        {
            // check if the reporting engine has a report defined for the
            // given report name
            return FileHelpers.exists (this.getReportDir () +
                   reportName + this.getFileExtension ());
        } // if (isActivated ())

        // reporting engine not activated
        return false;
    } // isReportDefined


    /**************************************************************************
     * Validate the configuration of the ReportingEngine. <BR/>
     *
     * If the reportingEngine is not activated the configuration will not
     * be validated. <BR/>
     *
     * @throws  ReportingException
     *          An configuration error occurred.
     */
    public void validateConfiguration () throws ReportingException
    {
        String msgNoExist = "' does not exist!";

        // if the reportingEngine is not activated the configuration
        // will not be validated
        if (!this.isActivated ())
        {
            return;
        } // if

        // check the invocation url
        // CONTRAINT: the invocation url must be set
        if (this.p_invocationUrl == null || this.p_invocationUrl.length () == 0)
        {
            throw new ReportingException (BIRTReportingEngine.MSG_CONFIG_ERROR +
                      "Invocation Url has not been set!");
        } // if (this.p_invocationUrl == null || this.p_invocationUrl.equals (""))

        // check if the url is correct
        if (!Helpers.existsURL (this.p_invocationUrl))
        {
            throw new ReportingException (BIRTReportingEngine.MSG_CONFIG_ERROR +
                "Invocation Url '" + this.p_invocationUrl + msgNoExist);
        } // if (! Helpers.existsURL (this.p_invocationUrl))

        // check if the reportDir exists
        if (!FileHelpers.exists (this.p_reportDir))
        {
            throw new ReportingException (BIRTReportingEngine.MSG_CONFIG_ERROR +
                "Report Directory '" + this.p_reportDir + msgNoExist);
        } // if (! FileHelpers.exists (this.p_reportDir))

        // CONTRAINT: the extension must be set
        if (this.p_fileExtension == null || this.p_fileExtension.length () == 0)
        {
            throw new ReportingException (BIRTReportingEngine.MSG_CONFIG_ERROR +
                "A valid report file extension must be set!");
        } // if (this.p_fileExtension == null || this.p_fileExtension.equals

        // all checks successfully passed
    } // validateConfiguration


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
        // set the DB connection
        super.loadConfiguration (env, app);

        int rowCount;
        SQLAction action = null;

        // create the SQL String to select the project order
        StringBuffer queryStr = new StringBuffer ()
            .append (" SELECT re.m_activated,")
                .append (" re.m_url, re.m_reportdir, re.m_extension")
            .append (" FROM ibs_object ore, dbm_reportingengine re")
            .append (" WHERE ore.oid = re.oid ")
                .append (" AND ore.state = ").append (States.ST_ACTIVE);

        try
        {
            action = DBConnector.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // is the result exactly one row?
            if (rowCount == 1)
            {
                // set the configuration values
                this.setActivated (action.getBoolean ("m_activated"));
                this.setInvocationUrl (action.getString ("m_url"), env
                    .getServerName ());
                this.setReportDir (action.getString ("m_reportdir"));
                this.setFileExtension (action.getString ("m_extension"));
            } // if (rowCount == 1)
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            // note that this query will throw an error in case the reporting
            // engine object has not been created. In that case the
            // dbm_reportingengine table does not exist
            throw new ReportingException (
                BIRTReportingEngine.MSG_COULD_NOT_LOAD_CONFIGURATION + e.toString ());
        } // catch
        finally
        {
            try
            {
                // release the action object:
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                throw new ReportingException (
                    BIRTReportingEngine.MSG_COULD_NOT_LOAD_CONFIGURATION + e.toString ());
            } // catch
        } // finally
    } // loadConfiguration

} // interface BIRTReportingEngine
