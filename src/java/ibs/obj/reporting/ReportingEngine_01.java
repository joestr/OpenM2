/*
 * Class: ReportingEngine_01.java
 */

// package:
package ibs.obj.reporting;

// imports:
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.service.reporting.ReportingException;
import ibs.service.reporting.birt.BIRTReportingEngine;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;


/******************************************************************************
 * ReportingEngine_01 handels the configuration of a reporting engine. <BR/>
 *
 * @version     $Id: ReportingEngine_01.java,v 1.3 2009/07/24 10:27:01 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 14.8.2006
 ******************************************************************************
 */
public class ReportingEngine_01 extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReportingEngine_01.java,v 1.3 2009/07/24 10:27:01 kreimueller Exp $";

    /**
     * field: activated. <BR/>
     */
    public static final String FLD_ACTIVATED = "activated";

    /**
     * field: invocationUrl. <BR/>
     */
    public static final String FLD_INVOCATION_URL = "invocationUrl";

    /**
     * field: reportDir. <BR/>
     */
    public static final String FLD_REPORT_DIR = "reportDir";

    /**
     * field: fileextension. <BR/>
     */
    public static final String FLD_FILE_EXTENSION = "fileextension";

    /**
     * TODO: make a multilanguage message
     * message:: reporting engine has been actualized. <BR/>
     */
    public static final String MSG_REPORTINGENGINE_ACTUALIZED = "Reporting Engine Configuration has been actualized.";

    /**
     * TODO: multilanguage message
     * message:: reporting engine has been actualized. <BR/>
     */
    public static final String MSG_REPORTINGENGINE_DEACTIVATED = "Reporting Engine  has been deactivated.";



    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // call common initializer:
        super.initClassSpecifics ();

    } // initClassSpecifics


    /**************************************************************************
     * Change the data of a business object in the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * This method tries to store the object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ibs.util.NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {

        super.performChangeData (operation);

        // TODO: change the configuration in the reportingEngine
        // stored in the application
        try
        {
            // set the report configuration with the given parameter
            this.setReportingEngine ();
        } // try
        catch (ReportingException e)
        {
            IOHelpers.showMessage (e.toString (), this.app, this.sess, this.env);
        } // catch (ReportingException e)

    } // performChangeData

    /**************************************************************************
     * Set the reporting engine in the application and and actualize it
     * with the configuration set in the fields of the object. <BR/>
     *
     * @throws ReportingException
     *            an error while setting the configuration occurred
     */
    public void setReportingEngine ()
        throws ReportingException
    {
        // CONTRAINT:check if we have a valid dataElement
        if (this.dataElement != null)
        {
            // create a new reportingEngine instance
            BIRTReportingEngine reportingEngine = new BIRTReportingEngine ();
            // set the specific configuration parameters
            // TODO: alternative solution would be via
            // reportingEngine.loadConfiguration (dataElement)
            reportingEngine.setActivated (this.dataElement
                .getImportBooleanValue (ReportingEngine_01.FLD_ACTIVATED));
            reportingEngine.setInvocationUrl (this.dataElement
                .getImportStringValue (ReportingEngine_01.FLD_INVOCATION_URL),
                this.env.getServerName ());
            reportingEngine.setReportDir (this.dataElement
                .getImportStringValue (ReportingEngine_01.FLD_REPORT_DIR));
            reportingEngine.setFileExtension (this.dataElement
                .getImportStringValue (ReportingEngine_01.FLD_FILE_EXTENSION));
            // in case the reporting engine has been activated
            // validate the configuratio
            if (reportingEngine.isActivated ())
            {
                // validate the configuration
                // in case of an error a ReportingException will be thrown
                reportingEngine.validateConfiguration ();
                // in case we reach this point everything is ok
                // set the new reporting engine in the application
                this.app.setReportingEngine (reportingEngine);
                // show a message
                IOHelpers.showMessage (
                    ReportingEngine_01.MSG_REPORTINGENGINE_ACTUALIZED,
                    this.app, this.sess, this.env);
            } // if (reportingEngine.isActivated ())
            else    // reset the reporting engine
            {
                this.app.resetReportingEngine ();
                IOHelpers.showMessage (
                    ReportingEngine_01.MSG_REPORTINGENGINE_DEACTIVATED,
                    this.app, this.sess, this.env);
            } // else
        } // if (this.dataElement != null)
        else    // no dataElement found
        {
            throw new ReportingException ("No configuration data found!");
        } // else no dataElement found
    } // setReportingEngine


} // ReportingEngine_01
