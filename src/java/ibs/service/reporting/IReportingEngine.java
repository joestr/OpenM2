/*
 * Class: IReportingEngine.java
 */

// package:
package ibs.service.reporting;

import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;

// imports:


/******************************************************************************
 * This class implementes the interface for a reporting engine. <BR/>
 *
 * @version     $Id: IReportingEngine.java,v 1.6 2008/09/17 16:41:34 kreimueller Exp $
 *
 * @author      Bernd Buchegger, 20060815
 ******************************************************************************
 */
public interface IReportingEngine
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IReportingEngine.java,v 1.6 2008/09/17 16:41:34 kreimueller Exp $";


    /**************************************************************************
     * Open the report using the given reportName and the given queryString. <BR/>
     *
     * @param   reportName  the name of the report to be opened
     * @param   queryStr    the query string for the report
     * @param   env         the environment to write the output to
     *
     * @throws  ReportingException
     *          An error occurred.
     */
    public abstract void openReport (String reportName, String queryStr,
                                     Environment env) throws ReportingException;


    /***************************************************************************
     * Check if the reporting engine has a report defined with the given report
     * name. <BR/>
     *
     * @param   reportName  The name of the report to be checked.
     *
     * @return <code>true</code> if report is supported or <code>false</code>
     *         otherwise
     */
    public abstract boolean isReportDefined (String reportName);


    /***************************************************************************
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
                                                    String queryStr,
                                                    Environment env);


    /**************************************************************************
     * Validate the configuration of the ReportingEngine. <BR/>
     *
     * @throws  ReportingException
     *          An configuration error occurred.
     */
    public abstract void validateConfiguration ()
        throws ReportingException;


    /**************************************************************************
     * Load configuration from database. <BR/>
     *
     * @param env   the environment to read the servername from
     * @param app     the application object to read the configuration of the
     *                   db connection.
     *
     * @throws  ReportingException
     *          An error during loading configuration occurred.
     */
    public abstract void loadConfiguration (Environment env, ApplicationInfo app)
        throws ReportingException;


    /**************************************************************************
     * Return the invocationUrl.<BR/>
     *
     * @return the p_invocationUrl
     */
    public String getInvocationUrl ();

} // interface IReportingEngine
