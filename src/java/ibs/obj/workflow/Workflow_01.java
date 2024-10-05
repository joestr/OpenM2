/*
 * Class: Workflow_01.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.workflow.WorkflowTokens;
import ibs.service.user.User;
import ibs.service.workflow.WorkflowConstants;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;

import java.util.Date;


/******************************************************************************
 * The m2 object Workflow_01 holds db-stored data for one workflow-instance. <BR/>
 *
 * @version     $Id: Workflow_01.java,v 1.18 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Workflow_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Workflow_01.java,v 1.18 2013/01/16 16:14:12 btatzmann Exp $";


    ////////////////////////////////////////////////////////
    //
    // db-stored attributes/properties (in ibs_Workflow_01)
    //
    //

    /**
     * OID of the object to that this workflow-instance belongs. <BR/>
     */
    public OID objectId = null;

    /**
     * OID of the template-object where the xml-definition is stored. <BR/>
     */
    public OID definitionId = null;

    /**
     * Date and time when this workflow was started/initiated. <BR/>
     */
    public Date startDate = new Date ();

    /**
     * Date and time when this workflow closed. <BR/>
     */
    public Date endDate = new Date ();

    /**
     * The state of this workflow instance (see WfMC definitions). <BR/>
     */
    public String workflowState = new String ();

    /**
     * The name of the current state of this workflow instance (see XML-file). <BR/>
     */
    public String currentStateName = new String ();

    /**
     * The process manager of this workflow instance (see XML-file). <BR/>
     */
    public int processManager = 0;

    /**
     * The path of the container for the process manager. <BR/>
     */
    public OID processManagerCont = null;

    /**
     * The starter of this workflow instance. <BR/>
     */
    public int starter = 0;

    /**
     * The oid of the container where the workflow started. <BR/>
     */
    public OID starterContainer = null;

    /**
     * The current owner of the workflow forward-object. <BR/>
     * --> currentOwner <> real m2-object owner
     * - currentOwner: only for workflow control (= current receiver)
     * - m2-object owner: when workflow starts owner is SYSTEM
     */
    public int currentOwner = 0;

    /**
     * The oid of the current owners container. <BR/>
     */
    public OID currentContainer = null;

    /**
     * Store the log-file of workflow-operations?. <BR/>
     * (will be stored in m2_absbase_path + "/worklfow/log/")
     */
    public boolean writeLog = true;


    ////////////////////////////////////////////////////////
    //
    // Additional attributes: will be set implicitely while
    // retrieval (procedure $performRetrieve)
    // - needed for viewing only!!!
    //

    /**
     * name of the object to that this workflow-instance belongs. <BR/>
     */
    public String objectName = null;

    /**
     * name of the attachment-object where the xml-definition is stored. <BR/>
     */
    public String definitionName = null;

    /**
     * full name of the process manager. <BR/>
     */
    public String processManagerName = "";

    /**
     * full name of starter. <BR/>
     */
    public String starterName = "";

    /**
     * The name of the current owner of the original object. <BR/>
     */
    public String currentOwnerName = "";


    ////////////////////////////////////////////////////////
    //
    // constructors and initialization
    //
    //

    /**************************************************************************
     * Creates a Workflow_01 Object. <BR/>
     */
    public Workflow_01 ()
    {
        super ();
        // initialize properties common to all subclasses:
    } // m2Workflow_01


    /**************************************************************************
     * Creates a Workflow_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Workflow_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // Workflow_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set stored procedure names:
        this.procCreate =     "p_Workflow_01$create";
        this.procChange =     "p_Workflow_01$change";
        this.procRetrieve =   "p_Workflow_01$retrieve";
        this.procDelete =     "p_Workflow_01$delete";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 18;
        this.specificChangeParameters   = 13;
    } // initClassSpecifics


    ////////////////////////////////////////////////////////
    //
    // methods called from application level
    //
    //

    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed, depending on user
        // and workflow state
        if (!this.workflowState.startsWith (WorkflowConstants.STATE_CLOSED) &&
            (this.user.id == this.processManager))
        {
            // process manager or current owner
            int [] buttons =
            {
                Buttons.BTN_SEARCH,
                Buttons.BTN_ABORTWORKFLOW,
            }; // buttons

            // return button array
            return buttons;
        } // if

        // other users
        int [] buttons =
        {
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    ////////////////////////////////////////////////////////
    //
    // db-methods
    //
    //

    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // input parameters
        // objectId
        BOHelpers.addInParameter (sp, this.objectId);
        // definitionId
        BOHelpers.addInParameter (sp, this.definitionId);
        // startDate
        sp.addInParameter (ParameterConstants.TYPE_DATE, this.startDate);
        // endDate
        sp.addInParameter (ParameterConstants.TYPE_DATE, this.endDate);
        // workflowState
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.workflowState);
        // currentStateName
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.currentStateName);
        // processManager
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.processManager);
        // container of proc manager
        BOHelpers.addInParameter (sp, this.processManagerCont);
        // starter
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.starter);
        // starterContainer
        BOHelpers.addInParameter (sp, this.starterContainer);
        // currentOwner
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.currentOwner);
        // currentContainer
        BOHelpers.addInParameter (sp, this.currentContainer);
        // writeLog
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.writeLog);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // set the specific output parameters:
        // objectId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // definitionId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // startDate
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // endDate
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // workflowState
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // currentStateName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // process manager
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // process manager container
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // starter
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // starterContainer
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // currentOwner
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // currentContainer
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // writeLog
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        // additional parameters (for viewing only)
        // name of object (forwarding)
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // name of template
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // process managers full name
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // starters full name
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // owners full name
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params    The array of parameters from the retrieve data stored
     *                  procedure.
     * @param lastIndex The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.objectId = SQLHelpers.getSpOidParam (params[++i]);
        this.definitionId = SQLHelpers.getSpOidParam (params[++i]);
        this.startDate = params[++i].getValueDate ();
        this.endDate = params[++i].getValueDate ();
        this.workflowState = params[++i].getValueString ();
        this.currentStateName = params[++i].getValueString ();
        this.processManager = params[++i].getValueInteger ();
        this.processManagerCont = SQLHelpers.getSpOidParam (params[++i]);
        this.starter = params[++i].getValueInteger ();
        this.starterContainer = SQLHelpers.getSpOidParam (params[++i]);
        this.currentOwner = params[++i].getValueInteger ();
        this.currentContainer = SQLHelpers.getSpOidParam (params[++i]);
        this.writeLog = params[++i].getValueBoolean ();

        // attributes needed to view object
        this.objectName = params[++i].getValueString ();
        this.definitionName = params[++i].getValueString ();
        this.processManagerName = params[++i].getValueString ();
        this.starterName = params[++i].getValueString ();
        this.currentOwnerName = params[++i].getValueString ();

        // filename and path of xml-definition have already been
        // retrieved in method: init()
    } // getSpecificRetrieveParameters


    ////////////////////////////////////////////////////////
    //
    // viewing methods
    //
    //

    /**************************************************************************
     * Represent the properties of a Workflow_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties without description !!
        super.showProperties (table);

        // show forward-object name
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_FORWARD_OBJECT_NAME, env),
                      Datatypes.DT_LINK,
                      this.objectName,
                      this.objectId);

        // show definition-template name
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_TEMPLATE_NAME, env),
                      Datatypes.DT_LINK,
                      this.definitionName,
                      this.definitionId);

        // show start date
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_STARTDATE, env),
                      Datatypes.DT_DATETIME,
                      this.startDate);

        // show end date: show only if state is something like 'closed.'
        if (this.workflowState.startsWith (WorkflowConstants.STATE_CLOSED))
        {
            this.showProperty (table,
                          "", //WorkflowArguments.xxx
                          MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                              WorkflowTokens.ML_ENDDATE, env),
                          Datatypes.DT_DATETIME,
                          this.endDate);
        } // if

        // show workflow status
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_WORKFLOW_STATE, env),
                      Datatypes.DT_TEXT,
                      this.workflowState);

        // show current status
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_CURRENT_STATE, env),
                      Datatypes.DT_TEXT,
                      this.currentStateName);

        // show currentOwner
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_CURRENT_OWNER_NAME, env),
                      Datatypes.DT_TEXT,
                      this.currentOwnerName);

        // show process manager
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_PROC_MGR_NAME, env),
                      Datatypes.DT_TEXT,
                      this.processManagerName);

        // show starter
        this.showProperty (table,
                      "", //WorkflowArguments.xxx
                      MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                          WorkflowTokens.ML_STARTER_NAME, env),
                      Datatypes.DT_TEXT,
                      this.starterName);

        // show log file: only if property writeLog true:
        if (this.writeLog)
        {
// TO-CHANGE: get homepage path not possible in BOs
            // create homepage-path; get server-name; cut "app/"
            String path = this.getUserInfo ().homepagePath;

            path = path.substring (0, path.indexOf ("app/"));

            this.showProperty (table,
                          "",   // WorkflowArguments.xxx
                          MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                              WorkflowTokens.ML_LOGFILE, env),
                          Datatypes.DT_TEXT,
                          "<A HREF=\"" + IOConstants.URL_JAVASCRIPT +
                          "top.loadFile (\'" +
                          path + WorkflowConstants.LOG_WEBPATH_PREFIX +
                          "WorkflowLog" + this.objectId.toString () + ".txt\')\">" +
                          "WorkflowLog" + this.objectId.toString () + ".txt" + "</A>");
        } // if
    } // showProperties

} // class Workflow_01
