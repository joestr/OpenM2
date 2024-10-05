/*
 * Class: WorkflowService.java
 *
 * List of uncool HACKS/things in this class
 *
 * ! unregister all jobs (look for TODO)
 *
 * - protected/private/public properties and methods
 * - split WorkflowService in startermethods (interface) + engine
 * - check/debug logging-entries!
 * - Variable-methods in own class
 * - dbaction_phase should be ONE transaction
 * - ACTIONs which change data, will be performed outside of dbaction_phase (not in transaction)
 *   ==> should have undo-part
 * - separate db-action in method finalization_phase -> should not be there
 * - evaluateXPathExpression/replace/... should be in utility class
 * - Variables and xpath not usable in every XML-element
 * - When starting workflow: check if object is already in active workflow!
 *   ==> if yes: finish old wf or throw error
 *
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.ObjectInitializeException;
import ibs.bo.Operations;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.Log_01;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.notification.INotificationService;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationTemplate;
import ibs.service.observer.M2ObserverJobData;
import ibs.service.observer.M2ObserverService;
import ibs.service.observer.ObserverException;
import ibs.service.user.GroupHelpers;
import ibs.service.user.User;
import ibs.service.workflow.Action;
import ibs.service.workflow.Condition;
import ibs.service.workflow.Notify;
import ibs.service.workflow.Receiver;
import ibs.service.workflow.ReferencesList;
import ibs.service.workflow.RegisterObserverJob;
import ibs.service.workflow.RightsList;
import ibs.service.workflow.RightsMapper;
import ibs.service.workflow.State;
import ibs.service.workflow.UserInteractionRequiredException;
import ibs.service.workflow.Variable;
import ibs.service.workflow.Variables;
import ibs.service.workflow.Workflow;
import ibs.service.workflow.WorkflowConstants;
import ibs.service.workflow.WorkflowHelpers;
import ibs.service.workflow.WorkflowInstanceInformation;
import ibs.service.workflow.WorkflowMessages;
import ibs.service.workflow.WorkflowProtocolEntry;
import ibs.service.workflow.WorkflowTagConstants;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * The WorkflowService provides an Interface to all needed workflow
 * operations. <BR/> It handles everything that is needed for one instance of
 * a workflow (initialisation, forwarding).
 *
 * @version     $Id: WorkflowService.java,v 1.39 2010/11/15 16:48:43 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 1.2.2001
 ******************************************************************************
 */
public class WorkflowService extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowService.java,v 1.39 2010/11/15 16:48:43 btatzmann Exp $";


    ////////////////////////////////////////////////////////
    //
    // attributes/objects needed to perform workflow actions
    //

    /**
     * Holds the system user. <BR/>
     * The system user is the owner of the object during the
     * workflow-process.
     */
    protected User systemUser = null;

    /**
     * Holds the workflow instance object of m2-type
     * Workflow_01. <BR/>
     * Holds all instance data, which is stored/viewed
     * for this instance.
     */
    protected Workflow_01 workflowInstance = null;

    /**
     * Holds the workflow-template object. <BR/>
     * Holds xml-description of workflow.
     */
    protected WorkflowTemplate_01 workflowTemplate = null;

    /**
     * Holds java representation of xml-definition (stored in
     * the workflowTemplate) for the workflowInstance. <BR/>
     */
    protected Workflow wfdefinition = null;

    /**
     * WorkflowObjectHandler: does operations on objects and db. <BR/>
     */
    protected WorkflowObjectHandler objectHandler = null;

    /**
     * WorkflowRightsHandler: does operations on rights. <BR/>
     */
    protected WorkflowRightsHandler rightsHandler = null;

    /**
     * Holds all protocol entries (WorkflowProtocolEntry). <BR/>
     * Will be written to in dbaction_phase.
     */
    protected Vector<WorkflowProtocolEntry> protocol = null;

    /**
     * Holds log for workflow-actions, messages, errors and warnings. <BR/>
     */
    public WorkflowLog log = null;

    /**
     * An alternative start state. use the setAlternativeStartState
     * method to set the state. <BR/>
     */
    private String alternativeStartState = null;


    ////////////////////////////////////////////////////////
    //
    // attributes/objects needed to store runtime information
    //

    /**
     * Holds list of all rights for forward object. <BR/>
     *
     * This list will be filled with the current user rights
     * for the forward-object on start of the forward-action.
     *
     * During the forward-process rights will be changed in
     * the list only.
     *
     * The actual change-of-rights (in db) will be done in
     * the last phase dbaction_phase() of the forwarding process.
     */
    protected RightsList rightsList = null;

    /**
     * Holds list of all references (for CC-receivers, process-
     * mangagers, ...) which are to create during one step
     * of the workflow-instance. <BR/>
     *
     * During the forward-process references on the forward
     * object will be created 'virtually' in this list.
     *
     * The actual creation of references (in db) will be done in
     * the last phase dbaction_phase() of the forwarding process.
     */
    protected ReferencesList referencesList = null;

    /**
     * Holds mapping of m2-rights on workflow-alias-rights. <BR/>
     */
    protected RightsMapper rightsMapping = null;


    /**
     * Holds all observerjobs that shall be registered. <BR/>
     */
    protected Vector<RegisterObserverJob> registerJobs = null;

    /**
     * Holds all observerjobs that shall be unregistered. <BR/>
     */
    protected Vector<RegisterObserverJob> unregisterJobs = null;

    /**
     * If set: forward to this state. If null: forward to state given
     * in workflow-definition. <BR/>
     */
    protected String forwardToState = null;

    /**
     * Will only be used if forwardToState != null. Indicates if the
     * current state shall be finalized normally or immediately interrupted.
     */
    protected boolean interrupt = false;


    ////////////////////////////////////////////////////////
    //
    // Flags & Indicators for interruption of control-flow
    // due to necessary user-interaction

    //
    // 1. Selection of WorkflowTemplate
    //
    /**
     * Indicator flag: tells Application that the workflow-template
     * selection dialog must be showed!
     * SET IN THIS CLASS!!
    */
    public boolean showWorkflowTemplateDialog = false;

    //
    // 2. <TRANSITION type="ALTERNATIVE">
    //
    /**
     * Indicator flag: tells Application that the alternative
     * next step form has been showed
     * SET IN THIS CLASS!!
    */
    public boolean showWorkflowAlternativeForm = false;
    /**
     * Indicator flag: tells that the alternative next-step
     * has been selected (which is stored in session)
     * MUST BE SET FROM FUNCTION-CONTROL-CLASS
    */
    public boolean alternativeNexStateSelected = false;
    /**
     * alternative string: name of next state
     * MUST BE SET FROM FUNCTION-CONTROL-CLASS
    */
    public String alternativeNextStateName = WorkflowConstants.UNDEFINED;

    //
    // 3. <RECEIVER ...>#AD-HOC#</RECEIVER>
    //
    /**
     * Indicator flag: tells Application that the adhoc-receiver
     * selection form have been showed
     * SET IN THIS CLASS!!
    */
    public boolean showWorkflowAdhocForm = false;
    /**
     * Indicator flag: tells that the adhoc-receiver
     * has been selected (who is stored in session)
     * MUST BE SET FROM FUNCTION-CONTROL-CLASS
    */
    public boolean adhocReceiverSelected = false;


    ///////////////// Constructors/object initialization
    //
    //
    /**************************************************************************
     * DO NOT USE THIS CONSTRUCTOR. <BR/>
     */
    public WorkflowService ()
    {
        // nothing to do
    } // WorkflowService


    /**************************************************************************
     * Initializes WorkflowService object; creates all needed base objects
     * like WorkflowLog, etc. <BR/>
     *
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     *
     * @throws  ObjectInitializeException
     *          init failed; base member-objects could not be created
     */
    public WorkflowService (User user, Environment env, SessionInfo sess,
                            ApplicationInfo app)
        throws ObjectInitializeException
    {
        // set needed m2 environment-objects
        this.initObject (OID.getEmptyOid (), user, env, sess, app);

        // setup base objects
        this.setup ();
    } // WorkflowService

    /**************************************************************************
     * Instantiates a new object of type WorkflowGUIHandler. <BR/>
     *
     * @return  the object of type WorkflowService
     *          null if no success
     */
    public WorkflowGUIHandler createWorkflowGUIHandler ()
    {
        // create/init new workflow-service object
        WorkflowGUIHandler workflowGUIHandler;
        workflowGUIHandler
            = new WorkflowGUIHandler (this.user, this.env, this.sess, this.app);

        // exit method
        return workflowGUIHandler;
    } // WorkflowGUIHandler

    /**************************************************************************
     * This method gets the alternativeStartState. <BR/>
     *
     * @return Returns the alternativeStartState.
     */
    public String getAlternativeStartState ()
    {
        // get the property value and return the result:
        return this.alternativeStartState;
    } // getAlternativeStartState


    /**************************************************************************
     * This method sets the alternativeStartState. <BR/>
     *
     * @param alternativeStartState The alternativeStartState to set.
     */
    public void setAlternativeStartState (String alternativeStartState)
    {
        //set the property value:
        this.alternativeStartState = alternativeStartState;
    } // setAlternativeStartState


    /**************************************************************************
     * This method initalizes the workflow service. <BR/> Needed base member-
     * objects will also be created (e.g. log, objecthandler, ...).
     *
     * @exception ObjectInitializeException if initialization of one base object
     *                                      fails (not very likely though)
     */
    private void setup () throws ObjectInitializeException
    {
        // create handlers/objects to perform workflow actions
        // - log
        // - rightsHandler
        // - objectHandler

        // create new log
        this.log = new WorkflowLog ();
        // initialize log object

        // environment and session must be set,
        // because it is used in super-class
        // (should not be necessary though)
        this.log.initObject (
            OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
        // first log initialization; after retrieval of
        // xml-definition log-configuration will be set according
        // to entries in definition
        this.log.initLog ();

        // try to initiate rightsHandler
        if ((this.rightsHandler = this.getRightsHandler ()) == null)
        {
            // log entry: error - could not create
            this.log.add (DIConstants.LOG_ERROR,
                    "Creation of WorkflowRightsHandler failed",
                    true);
            // CRITICAL ERROR: exit
            ObjectInitializeException e = new ObjectInitializeException (
                "Workflow: Initialization of RightsHandler failed");
            throw e;
        } // if

        // try to initiate objectHandler
        if ((this.objectHandler = this.getObjectHandler ()) == null)
        {
            // log entry: error - could not create
            this.log.add (DIConstants.LOG_ERROR,
                    "Creation of WorkflowObjectHandler failed",
                    true);
            // CRITICAL ERROR: exit
            ObjectInitializeException e = new ObjectInitializeException (
                "Workflow: Initialization of ObjectHandler failed");
            throw e;
        } // if

        // set rights handler in object handler:
        this.objectHandler.rightsHandler = this.rightsHandler;

        // get system user for current domain
        this.systemUser = this.objectHandler.getSystemUser (this.user.domain);
        // check if system user found
        if (this.systemUser == null)
        {
            // no systemuser found - error
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_SYSTEMUSER_NOT_FOUND, env),
                true);
            // CRITICAL ERROR: exit
            ObjectInitializeException e = new ObjectInitializeException (
                "Workflow: Initialization of SystemUser failed");
            throw e;
        } // if
    } // setup


    ///////////////// call interfaces
    //
    //

    /**************************************************************************
     * Start workflow for given object, identified by its oid. <BR/>
     *
     * @param   oid     The oid of the object which shall be forwarded.
     *
     * @return  <CODE>true</CODE>   success.
     *          <CODE>false</CODE>  no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean start (OID oid)
        throws UserInteractionRequiredException
    {
        // init variables
        BusinessObject  forwardObj = null;  // object to be forwarded

        // get and retrieve object from given oid; current user MUST
        // have the rights to retrieve object
        forwardObj = this.objectHandler.fetchObject (oid, this.user);

        // check success
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // continue with object
        return this.start (forwardObj);
    } // start


    /**************************************************************************
     * Start workflow for given BusinessObject. <BR/> If workflow template is
     * not predefined (in some XMLViewer-objects) an exception will be thrown.
     *
     * @param   forwardObj the object which shall be forwarded
     *
     * @return <CODE>true</CODE>     success
     *         <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean start (BusinessObject forwardObj)
        throws UserInteractionRequiredException
    {
        // check if a workflow-template is assigned to this object-type
        // can only be for types:
        // - XML-Viewer-Type
        // - ServicePoint-Type -> forwards only XML-viewer at the moment!
        // ... otherwise show selection form
        if (forwardObj instanceof ibs.di.XMLViewer_01)
        {
            // type cast
            XMLViewer_01 viewerObj = (XMLViewer_01) forwardObj;

            // check if workflow-template oid is valid
            if (viewerObj.workflowTemplateOid != null &&
                !viewerObj.workflowTemplateOid.isEmpty ())
            {
                // template exists: connect and forward workflow
                return this.start (forwardObj, viewerObj.workflowTemplateOid);
            } // if
        } // if

        // ... otherwise proceed: userinteraction is required
        // set information for calling program/application
        this.showWorkflowTemplateDialog = true;

        // exit method: create and throw exception
        throw new UserInteractionRequiredException (
            "User Interaction required: Workflow-Template selection dialog",
            forwardObj, null);
    } // start


    /**************************************************************************
     * Start workflow for given BusinessObject with given WorkflowTemplate,
     * identified by its oid. <BR/>
     *
     * @param   forwardObj      the BusinessObject which shall be forwarded
     * @param   templateObjOid  oid of the workflow template which describes
     *                          the workflow
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean start (BusinessObject forwardObj,
                          OID templateObjOid)
        throws UserInteractionRequiredException
    {
        // init variables
        WorkflowTemplate_01 templateObj;

        // get template object; systemUser MUST have right to retrieve object
/* BB 050302: using null as user forces accessing the object with OP_NONE
        templateObj = (WorkflowTemplate_01)
          this.objectHandler.fetchObject (templateObjOid, this.systemUser);
*/
        templateObj = (WorkflowTemplate_01)
            this.objectHandler.fetchObject (templateObjOid, null);
        templateObj.user = this.systemUser;

        // check success
        if (templateObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_WORKFLOWTEMPLATE_NOTFOUND, env)
                + "[" + templateObjOid + "]",
                true);
            // CRITICAL ERROR: exit
            return false;
        } // else

        // ... proceed
        return this.start (forwardObj, templateObj);
    } // start


    /**************************************************************************
     * Start workflow for given BusinessObject (identified by oid) with given
     * WorkflowTemplate (identified by its oid). <BR/>
     *
     * @param   forwardObjOid   oif of the BusinessObject which shall
     *                          be forwarded
     * @param   templateObjOid  oid of the workflow template which describes
     *                          the workflow
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean start (OID forwardObjOid,
                          OID templateObjOid)
        throws UserInteractionRequiredException
    {
        // init variables
        BusinessObject  forwardObj = null;  // object to be forwarded
        WorkflowTemplate_01 templateObj;    // forwarding template

        // get and retrieve object from given oid;
        // current user MUST have right to retrieve object
        forwardObj = this.objectHandler.fetchObject (forwardObjOid, this.user);

        // check success
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // get template object;  systemUser MUST have right to retrieve object
/* BB 050302: using null as user forces accessing the object with OP_NONE
        templateObj = (WorkflowTemplate_01)
          this.objectHandler.fetchObject (templateObjOid, this.systemUser);
*/
        templateObj = (WorkflowTemplate_01)
            this.objectHandler.fetchObject (templateObjOid, null);
        templateObj.user = this.systemUser;

        // check success
        if (templateObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_WORKFLOWTEMPLATE_NOTFOUND, env)
                + "[" + templateObjOid + "]",
                true);
            // CRITICAL ERROR: exit
            return false;
        } // else

        // ... proceed
        return this.start (forwardObj, templateObj);
    } // start


    /**************************************************************************
     * Start workflow given by name for given BusinessObject (identified by
     * oid). <BR/>
     *
     * @param   forwardObjOid   oif of the BusinessObject which shall
     *                          be forwarded
     * @param   templateName    name of the workflow template which describes
     *                          the workflow
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean start (OID forwardObjOid,
                          String templateName)
        throws UserInteractionRequiredException
    {
        // init variables
        BusinessObject  forwardObj = null;  // object to be forwarded

        // get and retrieve object from given oid;
        // current user MUST have right to retrieve object
        forwardObj = this.objectHandler.fetchObject (forwardObjOid, this.user);

        // check success
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // ... proceed
        return this.start (forwardObj, templateName);
    } // start


    /**************************************************************************
     * Start workflow given by name for given BusinessObject (identified by
     * oid). <BR/>
     *
     * @param   forwardObj      The BusinessObject which shall
     *                          be forwarded.
     * @param   templateName    Name of the workflow template which describes
     *                          the workflow.
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean start (BusinessObject forwardObj, String templateName)
        throws UserInteractionRequiredException
    {
        // init variables
        WorkflowTemplate_01 templateObj;    // forwarding template

        // get template object by name;  systemUser MUST have right
        // to retrieve object
/* BB 050302: using null as user forces accessing the object with OP_NONE
        templateObj =
          this.objectHandler.fetchWorkflowTemplateByName (templateName, this.systemUser);
*/
        templateObj =
            this.objectHandler.fetchWorkflowTemplateByName (templateName, null);
        templateObj.user = this.systemUser;

        // check success
        if (templateObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_WORKFLOWTEMPLATE_NOTFOUND, env) 
                + "[" + templateName + "]",
                true);
            // CRITICAL ERROR: exit
            return false;
        } // else

        // ... proceed
        return this.start (forwardObj, templateObj);
    } // start


    /**************************************************************************
     * Start workflow for given BusinessObject with given WorkflowTemplate. <BR/>
     * This method creates a new workflow instance (type Workflow_01) and
     * performs the first fowarding step.
     *
     * @param   forwardObj  the BusinessObject which shall be forwarded
     * @param   templateObj the workflow template which describes the workflow
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean start (BusinessObject forwardObj,
                          WorkflowTemplate_01 templateObj)
        throws UserInteractionRequiredException
    {
        // initialize local variables
        Workflow_01 instanceObj = null; // new workflow instance

        // create new instance of workflow for given object
        // with given workflow-template:
        // object of type Workflow_01 will be created!
        instanceObj = this.objectHandler.createWorkflowInstance (
            forwardObj, templateObj);

        // check if creation of instance was successfull
        if (instanceObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_CREATION_FAILED, env) +
                "[object = " + forwardObj.oid + ", " +
                "[template = " + templateObj.oid + "]",
                true);
            // CRITICAL ERROR: exit
            return false;
        } // else

        // now perform first forwarding step
        return this.forward (forwardObj, templateObj, instanceObj);
    } // start


    /**************************************************************************
     * Forward BusinessObject which must in given workflow, which must be
     * based on given workflow-template. <BR/>
     * This method is for internal usage only. Called when starting a
     * workflow from outside - all in forward() needed objects must be set.
     *
     * @param   forwardObj  the BusinessObject which shall be forwarded
     * @param   templateObj the workflow template which describes the workflow
     * @param   instanceObj the workflow-instance object
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    private boolean forward (BusinessObject forwardObj,
                             WorkflowTemplate_01 templateObj,
                             Workflow_01 instanceObj)
        throws UserInteractionRequiredException
    {
        // set member objects
        this.workflowInstance = instanceObj;
        this.workflowTemplate = templateObj;

        return this.forwardObject (forwardObj);
    } // forward


    /**************************************************************************
     * Forward BusinessObject which must already be in an active workflow. <BR/>
     *
     * @param   forwardObjOid  oid of the BusinessObject which shall
     *                         be forwarded
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean forward (OID forwardObjOid)
        throws UserInteractionRequiredException
    {
        // init variables
        BusinessObject  forwardObj = null;  // object to be forwarded

        // get and retrieve object from given oid
        // current user MUST have right to retrieve object
        forwardObj
            = this.objectHandler.fetchObject (forwardObjOid, this.user);

        // check success
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // continue with object
        return this.forward (forwardObj);
    } // forward


    /**************************************************************************
     * Forward BusinessObject which must already be in an active workflow. <BR/>
     *
     * @param   forwardObj  the BusinessObject which shall be forwarded
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    public boolean forward (BusinessObject forwardObj)
        throws UserInteractionRequiredException
    {
        // init template/instance objects
        if (!this.initWorkflowObjects (forwardObj))
        {
            return false;
        } // if

        // continue with object
        return this.forwardObject (forwardObj);
    } // forward


    /**************************************************************************
     * Forward BusinessObject which must already be in an active workflow to
     * given state. <BR/>
     *
     * @param   forwardObjOid  oid of the BusinessObject which shall
     *                         be forwarded
     * @param   toState        name of target-state
     * @param   interrupt      indicates if the current state shall be
     *                         finalized normally or immediately interrupted.
     *
     * @return  <CODE>true</CODE> if success,
     *          <CODE>false</CODE> if no success;
     *          look for errors in services log object.
     *
     * @throws  UserInteractionRequiredException
     *          Will be thrown if a user interaction is required, e.g.
     *          selection of ad-hoc user or next-state choice.
     *
     */
    public boolean forwardToState (OID forwardObjOid, String toState, boolean interrupt)
        throws UserInteractionRequiredException
    {
        // init variables
        BusinessObject  forwardObj = null;  // object to be forwarded

        // get and retrieve object from given oid
        // current user MUST have right to retrieve object
        forwardObj
            = this.objectHandler.fetchObject (forwardObjOid, this.user);

        // check success
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // continue with object
        return this.forwardToState (forwardObj, toState, interrupt);
    } // forwardToState


    /**************************************************************************
     * Forward BusinessObject which must already be in an active workflow to
     * given state. <BR/>
     *
     * @param   forwardObj     BusinessObject which shall
     *                         be forwarded
     * @param   toState        name of target-state
     * @param   interrupt      indicates if the current state shall be
     *                         finalized normally or immediately interrupted.
     *
     * @return  <CODE>true</CODE> if success,
     *          <CODE>false</CODE> if no success;
     *          look for errors in services log object.
     *
     * @throws  UserInteractionRequiredException
     *          Will be thrown if a user interaction is required, e.g.
     *          selection of ad-hoc user or next-state choice.
     *
     */
    public boolean forwardToState (BusinessObject forwardObj, String toState,
                                   boolean interrupt)
        throws UserInteractionRequiredException
    {
        // init template/instance objects
        if (!this.initWorkflowObjects (forwardObj))
        {
            return false;
        } // if

        // set attributes of this workflow
        this.forwardToState = toState;
        this.interrupt = interrupt;

        return this.forwardObject (forwardObj);
    } // forwardToState


    /**************************************************************************
     * Finish workflow of given BusinessObject (identified by its oid )
     * which must be in an active workflow. <BR/>
     *
     * @param   forwardObjOid  oid of the BusinessObject for which the
     *                         workflow shall be finished
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     */
    public boolean finish (OID forwardObjOid)
    {
        // init variables
        BusinessObject  forwardObj = null;  // the forwarding object

        // get and retrieve object from given oid;
        // current user MUST have right to retrieve object
        forwardObj
            = this.objectHandler.fetchObject (forwardObjOid, this.user);

        // check success
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // continue with object
        return this.finish (forwardObj);
    } // finish


    /**************************************************************************
     * Finish workflow of given BusinessObject which must be in an
     * active workflow. <BR/>
     *
     * @param   forwardObj  the BusinessObject for which the workflow shall be
     *                      finished
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     */
    public boolean finish (BusinessObject forwardObj)
    {
        // init template/instance objects
        if (!this.initWorkflowObjects (forwardObj))
        {
            return false;
        } // if

        // continue with object
        return this.finishWorkflow (forwardObj);
    } // finish


    /**************************************************************************
     * Abort workflow of given BusinessObject (identified by its oid )
     * which must be in an active workflow. <BR/>
     *
     * @param   forwardObjOid  oid of the BusinessObject for which the
     *                         workflow shall be aborted
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     */
    public boolean abort1 (OID forwardObjOid)
    {
        // init variables
        BusinessObject  forwardObj = null;  // the forwarding object

        // get and retrieve object from given oid
        // current user MUST have right to retrieve object
        forwardObj
            = this.objectHandler.fetchObject (forwardObjOid, this.user);

        // check success
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // continue with object
        return this.abort1 (forwardObj);
    } // abort1


    /**************************************************************************
     * Abort workflow of given BusinessObject which must be in an
     * active workflow. <BR/>
     *
     * @param   forwardObj  the BusinessObject for which the workflow shall be
     *                      aborted
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     */
    public boolean abort1 (BusinessObject forwardObj)
    {
        // init template/instance objects
        if (!this.initWorkflowObjects (forwardObj))
        {
            return false;
        } // if

        // continue with instance object
        return this.abortWorkflow (forwardObj);
    } // abort1


    /**************************************************************************
     * Abort the given workflow-instance (=Workflow_01; identified by its oid )
     * which must be active. <BR/>
     *
     * @param   instanceObjOid  oid of the Workflow_01 instance that
     *                          shall be aborted
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     */
    public boolean abort2 (OID instanceObjOid)
    {
        // init variables
        Workflow_01 instanceObj = null;  // the forwarding object

        // get and retrieve object from given oid
        // current user MUST have right to retrieve object
        instanceObj
            = (Workflow_01) this.objectHandler.fetchObject (instanceObjOid, this.user);

        // check success
        if (instanceObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_NO_WORKFLOWINSTANCE_FOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // continue with object
        return this.abort2 (instanceObj);
    } // abort2


    /**************************************************************************
     * Abort the given workflow-instance (=Workflow_01) which must be in active.
     * <BR/>
     *
     * @param   instanceObj  the workflow that shall be aborted
     *
     * @return  <CODE>true</CODE>     success
     *          <CODE>false</CODE>    no success; look for errors in services log object
     */
    public boolean abort2 (Workflow_01 instanceObj)
    {
        // retrieve template/forward objects
        BusinessObject forwardObj = this.initWorkflowObjects (instanceObj);

        // check if returned object ist valid
        if (forwardObj == null)
        {
            return false;
        } // if

        // continue with instance object
        return this.abortWorkflow (forwardObj);
    } // abort2

/*
    // later

    public boolean pause (BusinessObject forwardObj) {};
    public boolean pause (OID forwardObjOid) {};
*/

    /**************************************************************************
     * Tells if a confirmation-alert-box is needed for this workflow. <BR/>
     * Can be used from outside to control the output-messages. The information
     * about information comes from the workflow-template.
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for errors in services log object
     */
    public boolean showConfirmActive ()
    {
        // check if wfdefinition exists
        if (this.wfdefinition != null)
        {
            // get information from the workflow-template
            return this.wfdefinition.confirmOperation;
        } // if

        // return default value:
        return true;
    } // showConfirm



    /**************************************************************************
     * This method initializes some objects in the workflow-context of the
     * given forwarding-object. <BR/>
     *
     * Retrieval of:
     * - the systemuser
     * - the workflow-instance object (this.workflowInstance)
     * - the workflow-template object (this.workflowTemplate)
     *
     * These objects will be set in workflow-services membervariables.
     *
     * @param   forwardObj      the forwarded object
     *
     * @return  <CODE>true</CODE>             initialisation/retrieval
     *          <CODE>false</CODE>            otherwise (see for error details in log-object)
     */
    private boolean initWorkflowObjects (BusinessObject forwardObj)
    {
        // init variables
        WorkflowInstanceInformation wfinfo;

        // get workflow-instance-information from given object
        wfinfo = forwardObj.getWorkflowInstanceInfo ();
        // check if instance-information for this object found
        if (wfinfo == null)
        {
            // no instance-information found - error
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_NO_WORKFLOWINSTANCE_FOUND, env)
                + "(1)",
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // retrieve workflow-instance-object;
        // current user MUST have right to retrieve object, because
        // workflow-instance object (type Workflow_01) is subobject of forward-
        // object (with same rights).
/* BB 050302: using null as user forces accessing the object with OP_NONE
        this.workflowInstance
            = (Workflow_01) this.objectHandler.fetchObject(wfinfo.instanceId, this.systemUser);
*/
        this.workflowInstance
            = (Workflow_01) this.objectHandler.fetchObject (wfinfo.instanceId, null);
        this.workflowInstance.user = this.systemUser;

        // check if workflow-instance for this object found
        if (this.workflowInstance == null)
        {
            // no instance-information found - error
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_NO_WORKFLOWINSTANCE_FOUND, env)
                + "(2)",
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // retrieve template object;
        // system-user MUST have right to retrieve object
/* BB 050302: using null as user forces accessing the object with OP_NONE
        this.workflowTemplate
            = (WorkflowTemplate_01) this.objectHandler.fetchObject(this.workflowInstance.definitionId,
                                                                   this.systemUser);
*/
        this.workflowTemplate = (WorkflowTemplate_01)
            this.objectHandler.fetchObject (this.workflowInstance.definitionId, null);
        this.workflowTemplate.user = this.systemUser;

        // check if template could be retrieved
        if (this.workflowTemplate == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_WORKFLOWTEMPLATE_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // exit
        return true;
    } // initWorkflowObjects


    /**************************************************************************
     * This method initializes some objects in the workflow-context for the
     * given workflow-object and returns the forward-object. <BR/>
     *
     * Retrieval of:
     * - the systemuser
     * - the forward object
     * - the workflow-template object (this.workflowTemplate)
     *
     * These objects will partially be set in workflow-services membervariables.
     *
     * @param   instanceObj      the forwarded object
     *
     * @return  ???
     */
    private BusinessObject initWorkflowObjects (Workflow_01 instanceObj)
    {
        // set workflow-instance property
        this.workflowInstance = instanceObj;

        // retrieve forward object;
        // system-user MUST have right to retrieve object
/* BB 050302: using null as user forces accessing the object with OP_NONE
        BusinessObject forwardObj =
            this.objectHandler.fetchObject(this.workflowInstance.objectId,
                                           this.systemUser);
*/
        BusinessObject forwardObj =
            this.objectHandler.fetchObject (this.workflowInstance.objectId, null);
        forwardObj.user = this.systemUser;

        // check if forward object could be retrieved
        if (forwardObj == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return null;
        } // if

        // retrieve template object;
        // system-user MUST have right to retrieve object
/* BB 050302: using null as user forces accessing the object with OP_NONE
        this.workflowTemplate
            = (WorkflowTemplate_01) this.objectHandler.fetchObject(instanceObj.definitionId,
                                                                   this.systemUser);
*/
        this.workflowTemplate = (WorkflowTemplate_01)
            this.objectHandler.fetchObject (instanceObj.definitionId, null);
        this.workflowTemplate.user = this.systemUser;

        // check if template could be retrieved
        if (this.workflowTemplate == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_WORKFLOWTEMPLATE_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return null;
        } // if

        // exit
        return forwardObj;
    } // initWorkflowObjects



    ///////////////// Workflow-Logic Part
    //
    //
    /**************************************************************************
     * This method prepares everything to setup the workflow-service for
     * execution of workflow-operations (forwarding, aborting, ...)
     *
     * - create needed objects: rightslist, protocol, ...
     * - creates the wfdefinition object for workflow instance/steps. <BR/>
     *   (parsing of the XML-definition)
     *
     * @return  <CODE>true</CODE>   success,
     *          <CODE>false</CODE>  no success;
     *          look for errors in services log object
     */
    private boolean initializeWorkflowInstance ()
    {
        // check if necessary objects are set:
        if (this.workflowTemplate == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_WORKFLOWTEMPLATE_NOTFOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if
        if (this.workflowInstance == null)
        {
            // log entry: error - no access
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_NO_WORKFLOWINSTANCE_FOUND, env),
                true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // reset information from template object in instance
        // (for just-started workflows and in case the name has changed
        this.workflowInstance.definitionName = this.workflowTemplate.name;
        this.workflowInstance.definitionId = this.workflowTemplate.oid;

        // 1. create handlers/objects to perform workflow actions
        // - rightsList
        // - rightsMapping
        // - referencesList

        // get list of rights for forward-object
        if ((this.rightsList = this.rightsHandler
            .getRights (this.workflowInstance.objectId)) == null)
        {
            // log entry: error - could not create
            this.log.add (DIConstants.LOG_ERROR,
                    "Creation of rightsList failed",
                    true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // get list of rights mapper (workflow rights-alias mapping)
        if ((this.rightsMapping = this.rightsHandler.getRightsMapping ()) == null)
        {
            // log entry: error - could not create
            this.log.add (DIConstants.LOG_ERROR,
                    "Creation of rightsMapping failed",
                    true);
            // CRITICAL ERROR: exit
            return false;
        } // if

        // create empty references list:
        this.referencesList = new ReferencesList ();

        // create empty protocol:
        this.protocol = new Vector<WorkflowProtocolEntry> ();

        // 2. retrieve workflow instance data
        // - get wfdefinition object (holds java representation
        //      of XML-definition + additional info)
        // - set initial instance data

        // create/instanciate object to parse xml-definition
        WorkflowParser wfparser = new WorkflowParser ();

        // environment and session must be set,
        // because it is used in super-class filter.
        // should not be necessary though
        wfparser.initObject (
            OID.getEmptyOid (), this.user, this.env, this.sess, this.app);

        // set filename and path of xml-definition (get it
        // from template-objet)
        String fileName = this.workflowTemplate.fileName;
        String path = this.workflowTemplate.path;

        // create path to xml definition
        // (build from m2AbsBasePath and relative web-upload-path)
        path = BOHelpers.getFilePath (DIHelpers.getOidFromPath (path));

        // initialize wfdefinition object (parses the xml-definition,
        // and retrieves the workflow-state-structure out of it).
        // wfdefinition holds the java-representation of the xml-structure
        this.wfdefinition = wfparser.parse (fileName,
                                       path,
                                       this.log);

        // check success
        if (this.wfdefinition == null)
        {
            return false;
        } // if

        // initialize the workflows logging features (according)
        // to the settings in the workflow-template
        this.initializeLog ();

        // add 'javascript' to clear buttonbar and tab bar
        if (this.wfdefinition.displayLog)
        {
            // create GUI handler for outpt
            WorkflowGUIHandler workflowGUIHandler
                = this.createWorkflowGUIHandler ();

            // clear the tab and the button bar
            workflowGUIHandler.clearTabAndButtonBar ();
        } // if

        // IBS-91: This has been moved to this position from WorkflowFunction.executeEvent()
        // because this is the earliest point of time where something can be written
        // to the workflow log.
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                WorkflowMessages.ML_MSG_WORKFLOW_OPERATION_STARTED, env),
            this.wfdefinition.displayLog);

        // exit method
        return true;
    } // initializeWorkflowData


    /**************************************************************************
     * This method configures the workflow log according to the entries
     * in the workflow-definition. <BR/>
     */
    private void initializeLog ()
    {
        // set path, filename for log-file
        String logAbsPath = this.app.p_system.p_m2AbsBasePath +
                             WorkflowConstants.LOG_ABSPATH_PREFIX;
        String logFileName =
            "WorkflowLog" + this.workflowInstance.objectId.toString () + ".txt";

        // reinitiliaze log with path and filename
        this.log.initLog (logAbsPath, logFileName);

        // log-indicators
        boolean logToFile;
        boolean logToDisplay;

        // set indicator for display-logging
        if (!this.wfdefinition.displayLog)
        {
            logToDisplay = false;
        } // if
        else
        {
            // enable file-logging
            logToDisplay = true;
            // write already existing log-entries to file
            this.log.displayExisting ();
        } // else

        // set indicator for file-logging
        if (!this.wfdefinition.writeLog)
        {
            logToFile = false;
        } // if
        else
        {
            // enable file-logging
            logToFile = true;
            // write already existing log-entries to file
            this.log.appendExisting ();
        } // else


        // set flags
        this.log.configureForWorkflow (logToDisplay, logToFile);
    } // initializeLog


    /**************************************************************************
     * Forward an object according to the definitions of this m2Workflow. <BR/>
     * This is the core-method of the workflow-service. It performs the
     * forwarding-process of an objects workflow-instance. The forwarding
     * process is split in 5 phases:
     *
     * 1. INIT-PHASE
     *  - fires only once when workflow starts
     *  - performs some initial actions (set processmanager, ...)
     * 2. PRESTATE-PHASE:
     *  - fires on every state except first step  (init)
     *  - performs some clean-up actions for current state (rights, ...)
     * 3. STATE-PHASE:
     *  - fires always
     *  - performs workflow-actions
     *  - does the forwarding for current state (creation of refs, ...)
     * [4. FINALIZATION-PHASE - own method called from Application]
     *  - fires only once when user chooses 'Finish Workflow' (Button 'Workflow abschliessen')
     *  - performs clean-up actions for workflow-instance (close)
     * 5. DB-ACTION-PHASE
     *  - fires always
     *  - performs critical db-operations (rights, change owner, move)
     *    all db-operations encapsuled in one block; will later probably
     *    be in one db-transaction. but currently there is still the m2-problem
     *    that some db-actions may work and others may not: incosisttencies
     *    without a possibility to check the success!!!
     *
     * @param   obj     the object which shall be forwarded
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for protocol in WorkflowProtocol object
     *
     * @exception   UserInteractionRequiredException
     *              will be thrown if a user interaction is required, e.g.
     *              selection of ad-hoc user or next-state choice.
     */
    private boolean forwardObject (BusinessObject obj)
        throws UserInteractionRequiredException
    {

        // create an HTML footer for IE7.0
        this.createHTMLHeader (this.app, this.sess, this.env);

        ////////////////////////////////////////////////
        //
        // local variables
        //
        State           currentState = null; // state of the last forwarding-
                                             // action (needed for cleans up)
        State           nextState = null;    // next state of this forwarding-
                                             // action
        String          nextStateName = null; // name of next state


        ////////////////////////////////////////////////
        //
        // initialize this instance and retreive the
        // workflow-definition data
        //
        if (!this.initializeWorkflowInstance ())
        {
            return false;
        } // if


        ////////////////////////////////////////////////
        //
        // START: retrieve states
        //  - get current and next state out of the workflow-definition
        //  - only 2 states are necessary: current & nextstate
        //  - (depends on state stored as 'currentState' in the workflow-instance)
        //
        // NOTE: the completion of states (fill objects with data from
        //       m2-environment) is done later! At this point only the
        //       xml-workflow-definition will be parsed and extracted
        //       into the java-objects, that represent the workflow.

        //
        // check if this is the first forwarding step, when creating
        // the workflow-instance the following states were set:
        // - currentStateName  = STATE_UNDEFINED
        // - workflowState = STATE_OPEN_NOTRUNNING_NOTSTARTED
        if (this.workflowInstance.currentStateName
            .equals (WorkflowConstants.STATE_UNDEFINED) &&
            this.workflowInstance.workflowState
                .equals (WorkflowConstants.STATE_OPEN_NOTRUNNING_NOTSTARTED))
        {
            // 1st state

            // initialize current state (not needed)
            currentState = null;

            // initialize variables; adds some base-system-variables
            // to wfdefinitions variables
            this.initVariables ();

            // check if an alternative start state has been set
            if (this.alternativeStartState != null)
            {
                nextState = this.wfdefinition.getState (this.alternativeStartState);
            } // if (this.alternativeStartState != null)
            else // get standard start state
            {
                // get 1st state
                nextState =
                    this.wfdefinition.getState (this.wfdefinition.startStateName);
            } // else get standard start state


            // check if start state found in definition
            if (nextState == null)
            {
                // log entry: error - unknown state
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_NO_START_STATE, env) + " " +
                    this.wfdefinition.startStateName,
                    false);

                // CRITICAL ERROR: exit
                return false;
            } // if
        } // if, 1st state
        else
        {
            // not 1st state -> retrieve currentState and nextState

            //
            // 1. try to retreive the current state; currentStateName (stored
            // in the db; attribute of this class) is the name of the current step!
            //
            currentState =
                this.wfdefinition.getState (this.workflowInstance.currentStateName);
            // check if current state found in definition
            if (currentState == null)
            {
                // log entry: error - unknown state
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_STATE_NOT_FOUND, env) + " " +
                    this.workflowInstance.currentStateName,
                    false);

                // CRITICAL ERROR: exit
                return false;
            } // if

            //
            // 2. retrieve variables from db
            // - will be stored in wf-definitions variable object
            // - will be needed if conditional NEXTSTATE-evaluation
            if (!this.retrieveVariables ())
            {
                // CRITICAL error - exit
                return false;
            } // if

            // initialize variables; adds some base-system-variables
            // to wfdefinitions variables
            this.initVariables ();

            //
            // 3. try to retrieve the next state which can be given through
            //    * nextstate/transitions of current state
            //    * forwardToState()-method: stored in this.forwardToState
            //
            if (this.forwardToState != null)
            {
                // next state given in forwardToState()-method; ignore nextstate
                // defined in workflow-definition
                nextStateName = this.forwardToState;
                nextState =
                    this.wfdefinition.getState (nextStateName);
            } // if
            else
            {
//////////////////////////////////////////////////////////
//
// HACK: SCHWAN-STABILO --> XPATH in TRANSITION CONDITIONS
//       This part replaces (eventually) occurrences of XPath-expressions in a transition-condition
//       with the evaluated XPath-value.
//
                // check if transition is conditional
                if (currentState.transition.isConditional ())
                {
                    // loop through every condition and check for XPath-expressions
                    Condition condition = null;
                    for (int i = 0; i < currentState.transition
                        .getConditionCount (); i++)
                    {
                        // get next condition
                        condition = currentState.transition.getCondition (i);

                        // check if condition set!
                        if (condition != null)
                        {
                            // exchange XPath-expression in lhs/rhs-values (if any)
                            condition.lhsValue = this.evaluateXPathExpression (
                                condition.lhsValue, obj);
                            condition.rhsValue = this.evaluateXPathExpression (
                                condition.rhsValue, obj);
                            // check for errors
                            if (condition.lhsValue == null || condition.rhsValue == null)
                            {
                                return false;
                            } // if
                        } // if
                    } // while
                } // if
//
//
//////////////////////////////////////////////////////////

                // initialize name of next state;
                nextStateName
                    = currentState.transition.getNextState (this.wfdefinition.variables);

                // distinguish type of transition of current state
                if (currentState.transition.isSequential () ||
                    currentState.transition.isConditional ())
                {
                    // get current state (is the nexstate defined in current state)
                    nextState =
                        this.wfdefinition.getState (nextStateName);
                } // if
                else if (currentState.transition.isAlternative ())
                {
                    // TRANSITION-type: ALTERNATIVE in current state

                    // user-interaction already happened?
                    if (!this.alternativeNexStateSelected)
                    {
                        // user interaction required (select alternatíve
                        // set indicator - tells Application that selection form
                        // has been showed
                        this.showWorkflowAlternativeForm = true;

                        // BREAK: of normal control-flow; comes back
                        // after selection of ad-hoc receiver and continues
                        // after this point!
                        throw new UserInteractionRequiredException (
                            "User interaction required: select alternative nextstate",
                            obj, currentState);
                    } // if

                    // user interaction already happened (alternative state selected)
                    // get next state:   name of selected alternative next-state
                    //                   is stored in class-attribute; has been
                    //                   set from application-control-class
                    nextState =
                        this.wfdefinition.getState (this.alternativeNextStateName);
                } // else if
            } // else

            // check if start state found in definition
            if (nextState == null)
            {
                // log entry: error - unknown state
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_STATE_NOT_FOUND, env) + " " +
                    this.wfdefinition.getState (nextStateName) + " in " +
                    this.workflowInstance.currentStateName,
                    false);

                // CRITICAL ERROR: exit
                return false;
            } // if

        } // else, not 1st state
        //
        // END: get current and next state out of the workflow-definition
        //
        ////////////////////////////////////////////////


        ////////////////////////////////////////////////
        //
        // check if confirmation (alert-box) for current operation
        // shall be displayed
        // (states confirmation overwrites confirmation-value of
        //  whole workflow, but only if set)
        if (nextState.confirmOperation.equalsIgnoreCase (WorkflowConstants.YES))
        {
            this.wfdefinition.confirmOperation = true;
        } // if
        else if (nextState.confirmOperation.equalsIgnoreCase (WorkflowConstants.NO))
        {
            this.wfdefinition.confirmOperation = false;
        } // if
        //
        ////////////////////////////////////////////////


        ////////////////////////////////////////////////
        //
        // START: ADHOC-receivers: in next state?
        //
        // is adhoc-receiver specified?
        if (nextState.adhocReceiver && !this.adhocReceiverSelected)
        {
            // set indicator - tells Application that selection form
            // has been showed
            this.showWorkflowAdhocForm = true;

            // BREAK: of normal control-flow; comes back
            // after selection of ad-hoc receiver and continues
            // after this point!
            throw new UserInteractionRequiredException (
                "User interaction required: select AD-HOC receiver", obj,
                nextState);

        } // if, adhoc and no selection-form done yet
        //
        // END: ADHOC-receivers: in next state?
        //
        ////////////////////////////////////////////////


        ////////////////////////////////////////////////
        //
        // PERFORM PHASES OF WORKFLOW FORWARD OPERATION
        //

        //
        // PRESTATE/INITIALIZATION PHASE
        //
        if (currentState == null)
        {
            // must be 1st forwarding step - call init-phase
            nextState = this.initialization_phase (obj, nextState);
        } // if
        else
        {
            // not first state; do some clean up actions on current state;
            // returns null if error occured
            nextState = this.prestate_phase (obj, currentState, nextState);
        } // if

        // check success
        if (nextState == null)
        {
            // CRITICAL ERROR: exit
            return false;
        } // if

        //
        // STATE PHASE
        //
        if (!this.state_phase (obj, nextState))
        {
            return false;
        } // if

        //
        // FINALIZATION PHASE
        //
        // check if finalization is necessary:
        // if (in state_phase) workflow state switched to "closed.completed"
        // then the finalization_phase must be called
        //
        if (this.workflowInstance.workflowState.equals (WorkflowConstants.STATE_CLOSED_COMPLETED))
        {
            // call finalization phase
            if (!this.finalization_phase (obj, nextState))
            {
                return false;
            } // if
        } // if ...

        ////////////////////////////////////////////////
        //
        // DBACTION PHASE
        // the workflow-phases are finished; now
        // write all necessary changes!
        // - store rights changes on forward object
        // - move object
        // - change owner
        // - store changes on workflow-instance
        // - ...
        //
        if (!this.dbaction_phase (obj, nextState))
        {
            return false;
        } // if

////////////////////////////////////////////////
// EUI HACK: Perform Actions after a transition. Used to set rights after
//           the DB-action phase.
// perform ACTIONS that should be executed at start of a new state
// this must be done after the dbaction_phase because changing
// rights would not have any effect
// Note that this can only be done in case the workflow is not closed
        return this.executeOnStartActions (obj, nextState);
// EUI HACK EUI
////////////////////////////////////////////////
    } // forwardObject


    /**************************************************************************
     * Execute eventually given ACTIONS of given state that should be
     * executed before the transition. <BR/>
     *
     * @param   obj       the forwarded object
     * @param   state     the state for which the actions are defined
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for protocol in WorkflowProtocol object
     */
    private boolean executeActions (BusinessObject obj, State state)
    {
        return this.executeActions (obj, state.actions);
    } // executeBeforeActions


    /**************************************************************************
     * Execute eventually given ACTIONS of given state that should be
     * executed at start of the state. <BR/>
     *
     * @param   obj       the forwarded object
     * @param   state     the state for which the actions are defined
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for protocol in WorkflowProtocol object
     */
    private boolean executeOnStartActions (BusinessObject obj, State state)
    {
        return this.executeActions (obj, state.executeOnStartActions);
    } // executeOnStartActions


    /**************************************************************************
     * Execute eventually given ACTIONS of given state. <BR/> Parameters will
     * be read/set from/in wfdefinitions variables-object.
     *
     * @param   obj       the forwarded object
     * @param   actions   a Vector holding the actions
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for protocol in WorkflowProtocol object
     */
    private boolean executeActions (BusinessObject obj, Vector<Action> actions)
    {
        // check if any actions to execute
        if (actions == null || actions.size () == 0)
        {
            return true;
        } // if

        // action succeeded: log entry
        this.log.add (DIConstants.LOG_ENTRY, " ", false);

        // initialize ActionHandler to execute actions
        WorkflowActionHandler actionHandler = this.getActionHandler ();
        Action action;
        boolean success = false;
        String inParamsValues;

        // check if initialization was successful
        if (actionHandler == null)
        {
            // log entry: error - could not create
            this.log.add (DIConstants.LOG_ERROR,
                    "Creation of WorkflowActionHandler failed",
                    true);
            // CRITICAL ERROR: exit
            return false;
        } // if (actionHandler == null)


        // iterate through all actions of next state
        // execution order is order of state.actions-vector
        for (int i = 0; i < actions.size (); i++)
        {
            // get next action
            action = actions.elementAt (i);

            // store inParams for logging (variables could change during action
            inParamsValues =
                action.valuesOfInParams (this.wfdefinition.variables);

            // check if action defined for next state
            if (!action.type.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
            {
                // catch ANY exception that occurs in external action
                // (e.g. in xpath or query, ...
                success = false;
                try
                {
                    // perfom specified action; variables-object needed for
                    // out(in)parameters
                    success =
                        actionHandler.performAction (obj, action,
                            this.wfdefinition.variables);
                } // try
                catch (Exception exc)
                {
                    // log entry: error - could not create
                    this.log.add (DIConstants.LOG_ERROR,
                                  "Java-Exception occured during action: " +
                                  "type = " + action.type +
                                  "; call = " + action.call +
                                  "; inparams = " + inParamsValues +
                                  "; outparams = " +
                                  action.valuesOfOutParams (this.wfdefinition.variables) +
                                  "; exception = " + exc.toString (),
                            this.wfdefinition.displayLog);
                    // CRITICAL ERROR: exit
                    return false;
                } // try

                // check success of action
                if (!success)
                {
                    // log entry: error - could not create
                    this.log
                        .add (
                            DIConstants.LOG_ERROR,
                            "Error During Action: " +
                            "type = " + action.type +
                            "; call = " + action.call +
                            "; inparams = " + inParamsValues +
                            "; outparams = " + action
                                .valuesOfOutParams (this.wfdefinition.variables) +
                            "; errcode = " + this.wfdefinition.variables
                                .getEntry (WorkflowConstants.VARIABLE_ERRORCODE).value +
                            "; errormessage = " + this.wfdefinition.variables
                                .getEntry (WorkflowConstants.VARIABLE_ERRORMESSAGE).value,
                            this.wfdefinition.displayLog);
                    // ERROR: exit
                    return false;
                } // if (!success)

                // action succeeded: log entry
                this.log.add (DIConstants.LOG_ENTRY, "Action performed: " +
                    "type = " + action.type +
                    "; call = " + action.call +
                    "; inparams = " + inParamsValues +
                    "; outparams = " + action.valuesOfOutParams (this.wfdefinition.variables),
                    false);
            } // if
            // else: no action defined - proceed
        } // for (iterate throug all actions)

        // action succeeded: log entry
        this.log.add (DIConstants.LOG_ENTRY, " ", false);

        // success
        return true;
    } // executeActions


    /**************************************************************************
     * Register eventually given ObserverJobs. <BR/>
     *
     * @param   obj     ???
     *
     * @return  <CODE>true</CODE> if success,
     *          <CODE>false</CODE> if no success;
     *          look for protocol in WorkflowProtocol object.
     */
    private boolean registerJobs (BusinessObject obj)
    {
        // check if any jobs to register
        if (this.registerJobs == null || this.registerJobs.size () == 0)
        {
            return true;
        } // if

        // initialize Observer to execute actions
        M2ObserverService service = new M2ObserverService (this.user,  this.env, this.sess, this.app);
        RegisterObserverJob jDef = null;
        M2ObserverJobData jData;
        boolean success = true;
        int jobId = -1;

        // iterate through jobs to register
        for (int i = 0; i < this.registerJobs.size (); i++)
        {
            jDef = this.registerJobs.elementAt (i);
            // create job-data: context(=null) will be set during register (see
            // below)
            jData = new M2ObserverJobData (null, jDef.className, jDef.name,
                obj.oid, this.workflowInstance.oid);

            try
            {
                jobId = service.registerObserverJob (jDef.observer, jData);

                // action succeeded: log entry
                this.log.add (DIConstants.LOG_ENTRY,
                              "m2ObserverJob registered: id=" + jobId +
                              "; name = " + jDef.name +
                              "; className = " + jDef.className +
                              "; observer = " + jDef.observer,
                              false);
            } // try
            catch (ObserverException e)
            {
                success = false;
                // log entry: error - could not register
                this.log.add (DIConstants.LOG_ERROR,
                              "Error while trying to register m2ObserverJob: " +
                              "id=" + jobId +
                              "; name = " + jDef.name +
                              "; className = " + jDef.className +
                              "; observer = " + jDef.observer +
                              "; error=" + e.toString (),
                              this.wfdefinition.displayLog);
            } // catch
        } // for

        // success
        return success;
    } // registerJobs


    /**************************************************************************
     * Unregister eventually given ObserverJobs. <BR/>
     *
     * @param   obj     ???
     *
     * @return  <CODE>true</CODE> if success,
     *          <CODE>false</CODE> if no success;
     *          look for protocol in WorkflowProtocol object.
     */
    private boolean unregisterJobs (BusinessObject obj)
    {
        // check if any jobs to register
        if (this.unregisterJobs == null || this.unregisterJobs.size () == 0)
        {
            return true;
        } // if

        M2ObserverService service = new M2ObserverService (this.user,  this.env, this.sess, this.app);
        RegisterObserverJob jDef;
        M2ObserverJobData jData;
        boolean success = true;

        // iterate through jobs to unregister
        for (int i = 0; i < this.unregisterJobs.size (); i++)
        {
            jDef = this.unregisterJobs.elementAt (i);
            // create job-data
            // * context(=null) will be set during unregister (see below)
            jData = new M2ObserverJobData (null, jDef.className, jDef.name,
                obj.oid, this.workflowInstance.oid);

            try
            {
                // unregister job:
                service.unregisterObserverJob (jDef.observer, jData);

                // action succeeded: log entry
                this.log.add (DIConstants.LOG_ENTRY,
                              "m2ObserverJob unregistered:" +
                              " name = " + jDef.name +
                              "; className = " + jDef.className +
                              "; observer = " + jDef.observer,
                              false);
            } // try
            catch (ObserverException e)
            {
                success = false;
                // log entry: error - could not register
                this.log.add (DIConstants.LOG_ERROR,
                              "Error while trying to unregister m2ObserverJob: " +
                              "; name = " + jDef.name +
                              "; className = " + jDef.className +
                              "; observer = " + jDef.observer +
                              "; error=" + e.toString (),
                              this.wfdefinition.displayLog);
            } // catch
        } // for

        // success
        return success;
    } // unregisterJobs


    /**************************************************************************
     * Finish a workflow instance. <BR/>
     * Calls phase 4 of workflow-process and stores status in db.
     *
     * 4. FINALIZATION-PHASE - own method called from Application
     *  - fires only once when user chooses 'Finish Workflow'
     *  - performs clean-up actions for workflow-instance (close)
     *
     * @param   obj     the object for which instance shall be finalized
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for protocol in WorkflowProtocol object
     */
    private boolean finishWorkflow (BusinessObject obj)
    {
        // local variables
        State           lastState;        // last state

        // retrieve the workflow definition data
        if (!this.initializeWorkflowInstance ())
        {
            return false;
        } // if

        // log entry: finish
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_FINISHING_HEADER, env),
            this.wfdefinition.displayLog);
        // log entry: file only
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Time: " + (new Date ()).toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[User: " + this.user.fullname + ", id=" +
                      this.user.id + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      " ",
                      false);

        // retreive additional data (from db) for workflow-definition
        if (!this.completeHeader ())
        {
            return false;
        } // if

        // check if current state is last state of definition!
        if (!this.wfdefinition.isEndState (this.workflowInstance.currentStateName))
        {
            // log entry: error - incosistency
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_INCONSISTENCY_GENERAL, env) + " " +
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_INCONSISTENCY_LASTSTATEMANDATORY, env) + " " +
                this.workflowInstance.currentStateName,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // if

        // get last state of workflowdefinition
        // try to retreive the last state (=current state)
        lastState =
            this.wfdefinition.getState (this.workflowInstance.currentStateName);
        if (lastState == null)
        {
            // log entry: error - state not found
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_STATE_NOT_FOUND, env) + " " +
                this.workflowInstance.currentStateName,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // if

/*
        // complete last state; use session-information
        // (means: receivers/destination with #AD-HOC# or #VARIABLE.xxx#
        lastState = this.completeState (lastState, false);
        if (lastState == null)
            // CRITICAL ERROR: exit
            return false;
*/

/*
-- TO-CHANGE: Probably later
        // unset workflow flag on object
        this.objectHandler.setWorkflowFlag (obj.oid, false);
*/

        // call finalization phase - exit
        if (this.finalization_phase (obj, lastState))
        {
            // unregister observerjobs:
            this.unregisterJobs (obj);

            // store the workflow instance during this state:
            try
            {
                // no rights required (normaly OP_CHANGE), every
                // user involved should be able to access the instance data
                this.workflowInstance.performChange (0);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                this.showNoAccessMessage (Operations.OP_CHANGE);
                // CRITICAL ERROR: exit
                return false;
            } // catch
            catch (NameAlreadyGivenException e) // no access to objects allowed
            {
                // display error message:
                this.showNameAlreadyGivenMessage ();
            } // catch

            // store all protocol entries
            this.storeProtocol ();

            // delete instance variables
            this.deleteVariables ();

            // exit
            return true;
        } // if

        // store all protocol entries
        this.storeProtocol ();

        // CRITICAL ERROR: exit
        return false;
    } // finishWorkflow


    /**************************************************************************
     * Abort a workflow instance. <BR/>
     *
     * @param   obj     The object on which to abort the workflow instance.
     *
     * @return  <CODE>true</CODE>   success
     *          <CODE>false</CODE>  no success; look for protocol in WorkflowProtocol object
     */
    private boolean abortWorkflow (BusinessObject obj)
    {
        // retrieve the workflow definition data
        if (!this.initializeWorkflowInstance ())
        {
            return false;
        } // if

        this.log.add (DIConstants.LOG_ENTRY,
                      "--------------------------------------------------------------------",
                      false);
        // log entry
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                WorkflowMessages.ML_MSG_WORKFLOW_ABORTED, env),
            this.wfdefinition.displayLog);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Time: " + (new Date ()).toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[User: " + this.workflowInstance.currentOwnerName +
                      ", id=" + this.workflowInstance.currentOwner + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      " ",
                      false);

        // add protocol entry: workflow aborted
        this.addProtocolEntry (obj,
                               this.workflowInstance.currentStateName,
                               WorkflowConstants.OP_ABORT,
                               this.user,
                               this.user, "");

        // set workflow-state to 'aborted'
        this.workflowInstance.workflowState =
            WorkflowConstants.STATE_CLOSED_ABORTED;

/*
-- TO-CHANGE: Probably later
        // unset workflow flag on object
        this.objectHandler.setWorkflowFlag (obj.oid, false);
*/

        // store the workflow instance
        try
        {
            // no rights required (normaly OP_CHANGE), every
            // user involved should be able to access the instance data
            this.workflowInstance.performChange (0);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);
            // CRITICAL ERROR: exit
            return false;
        } // catch
        catch (NameAlreadyGivenException e) // no access to objects allowed
        {
            // display error message:
            this.showNameAlreadyGivenMessage ();
        } // catch

        // store all protocol entries
        this.storeProtocol ();

        // delete instance variables
        this.deleteVariables ();

//
//      TODO: UNREGISTER ALL OBSERVERJOBS
//

        // exit:
        return true;
    } // abortWorkflow



    /***************************************************************************
     * Performs the initialization phase of the forwarding process. <BR/>
     * This includes:
     * - intializing control-data of this instance
     * - change owner of given object (recursive) to SYSTEM
     * - calculate new rights for remain-users, process-mgr, starter
     * - create references for process-mgr and starter
     *
     * @param   obj             the object to forward
     * @param   firstState      1st state
     *
     * @return  <CODE>state</CODE>   the 1st state
     *          <CODE>null</CODE>    if an error occured
     */
    protected State initialization_phase (BusinessObject obj,
                                          State firstState)
    {
        // local variables
        int rights = 0;        // for rights settings

        // log entry: workflow started
        this.log.add (DIConstants.LOG_ENTRY,
                      "--------------------------------------------------------------------",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                WorkflowMessages.ML_MSG_WORKFLOW_STARTED, env),
            this.wfdefinition.displayLog);
        // log entries: write to file only
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Time: " + (new Date ()).toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Object: " + obj.name + ", " + obj.oid.toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[WorkflowTemplate: " + this.workflowTemplate.name + ", " +
                      this.workflowTemplate.oid.toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[WorkflowInstance: " + this.workflowInstance.definitionName + ", " +
                      this.workflowInstance.definitionId.toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Details: " + this.wfdefinition.name + ", " +
                      this.wfdefinition.version + ", " + this.wfdefinition.created + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      " ",
                      false);

        // complete state

        // init current-states name with name of start-state
        this.workflowInstance.currentStateName = firstState.name;
        // init workflow state
        this.workflowInstance.workflowState = WorkflowConstants.STATE_OPEN;

        // init starter and container for this instance
        this.workflowInstance.starter = this.user.id;
        this.workflowInstance.starterName = this.user.fullname;
        this.workflowInstance.starterContainer =
            (OID) obj.containerId.clone ();

        // init current owner
        this.workflowInstance.currentOwner = this.user.id;
        this.workflowInstance.currentOwnerName = this.user.fullname;
        this.workflowInstance.currentContainer = obj.containerId;

        // complete header-definition: get missing information
        if (!this.completeHeader ())
        {
            return null;
        } // if

        // set process manager and container for this instance
        this.workflowInstance.processManager = this.wfdefinition.processMgr.user.id;
        this.workflowInstance.processManagerName = this.wfdefinition.processMgr.user.fullname;
        this.workflowInstance.processManagerCont = this.wfdefinition.processMgr.destinationId;

        // log entry: starter
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_STARTER_NAME, env) + ": " +
            this.user.fullname,
            this.wfdefinition.displayLog);
        // log entry: write to file only
        this.log.add (DIConstants.LOG_ENTRY,
                      "[User: " + this.user.username + ", oid=" + this.user.id + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Target: oid=" + this.workflowInstance.starterContainer.toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      " ",
                      false);

        // log entry: process-manager
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_PROC_MGR_NAME, env) + ": " +
            this.wfdefinition.processMgr.user.fullname,
            this.wfdefinition.displayLog);
        // log entry: write to file only
        this.log.add (DIConstants.LOG_ENTRY,
                      "[User: " + this.wfdefinition.processMgr.user.username + ", id=" +
                      this.wfdefinition.processMgr.user.id + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Target: " + this.wfdefinition.processMgr.destination +  ", id=" +
                      this.wfdefinition.processMgr.destinationId + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      " ",
                      false);


        // change existing user rights on object according
        // to tag <REMAINRIGHTSOTHERS="..."> in definition header
        // (in list only; db-access will be performed in dbaction_phase)
        rights = this.rightsMapping.getEntry (this.wfdefinition.remainRightsOthers);
        if (rights < 0)
        {
            // log entry: error - unknown rights entry
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_UNKNOWN_RIGHTS_ENTRY, env) + " " +
                WorkflowTagConstants.ATTR_REMAINRIGHTSOTHERS + ": " +
                this.wfdefinition.remainRightsOthers,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return null;
        } // if
        this.rightsList.changeAllEntries (rights);
                // !!!!IMPORTANT: all rights are changed; thus this step
                //                must be the first step


        // set rights for starter-manager on object according
        // to tag <REMAINRIGHTSSTARTER="..."> in definition header
        // (in list only; db-access will be performed in dbaction_phase)
        rights = this.rightsMapping.getEntry (this.wfdefinition.starter.remainRights);
        if (rights < 0)
        {
            // log entry: error - unknown rights entry
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_UNKNOWN_RIGHTS_ENTRY, env) + " " +
                WorkflowTagConstants.ATTR_REMAINRIGHTSSTARTER + ": " +
                this.wfdefinition.starter.remainRights,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return null;
        } // if
        this.rightsList.changeEntry (this.workflowInstance.starter, rights);


        // set rights for process-manager on object according
        // to tag <MANAGERRIGHTS="..."> in definition header
        // (in list only; db-access will be performed in dbaction_phase)
        rights = this.rightsMapping.getEntry (this.wfdefinition.processMgr.rights);
        if (rights < 0)
        {
            // log entry: error - unknown rights entry
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_UNKNOWN_RIGHTS_ENTRY, env) + " " +
                WorkflowTagConstants.ATTR_PROCESSMGRRIGHTS + ": " +
                this.wfdefinition.processMgr.rights,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return null;
        } // if
        this.rightsList.changeEntry (this.workflowInstance.processManager, rights);


        // add protocol entry: workflow started from current user!"
        this.addProtocolEntry (obj, "", WorkflowConstants.OP_START,
                               this.user, this.user, "");

        // exit method
        return firstState;
    } // initialization_phase



    /***************************************************************************
     * Performs the initialization phase of the forwarding process. <BR/>
     * Clean up actions (rights, ...) on current state, then switch to next
     * state. Attention: if the current state is the last state then the
     * current state will be returned.
     *
     * @param   obj             the forward-object
     * @param   currentState    state for clean up actions
     * @param   nextState       next state of current state
     *
     * @return  <CODE>state</CODE>   the next state
     *          <CODE>null</CODE>    an error occured
     */
    protected State prestate_phase (BusinessObject obj,
                                    State currentState,
                                    State nextState)
    {
        State currentStateLocal = currentState; // variable for local assignments
        int rights = 0;         // for rights settings

        //
        // REMARK: state/header-completion must be performed before forward
        //         (some missing data already retrieved with instance)
        //
        // complete header-definition: get missing information and fill it in workflow
        if (!this.completeHeader ())
        {
            return null;
        } // if

        // log entry: for fowarding object
        this.log.add (DIConstants.LOG_ENTRY,
                      "--------------------------------------------------------------------",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_FORWARD_HEADER, env),
            this.wfdefinition.displayLog);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Time: " + (new Date ()).toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[User: " + this.user.username + ", id=" + this.user.id + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Definition: " + this.wfdefinition.name + ", " +
                      this.wfdefinition.version + ", " + this.wfdefinition.created + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      " ",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_PREVIOUS_STATE, env) + ": " +
            currentStateLocal.name,
            this.wfdefinition.displayLog);

        // check if forward shall perform prestate-phase
        // (will be skipped if interrupt attribute of workflow is set)
        // (interrupt can be set via the forwardToState-method)
        if (!this.interrupt)
        {
            //
            // set observerjobs that shall be unregistered (in dbaction_phase)
            //
            this.unregisterJobs = currentStateLocal.unregisterJobs;

//
// MISSING: ACTIONS type="onExit"
//

            //
            // complete states: get missing information and fill it in states
            currentStateLocal = this.completeState (currentStateLocal, false);
            if (currentStateLocal == null)
            {
                return null;
            } // if

            // set remain-rights for receiver on object according
            // to tag <REMAINRIGHTS="..."> in currentState RECEIVER definition
            // (in list only; db-access will be performed in dbaction_phase)
            rights = this.rightsMapping.getEntry (currentStateLocal.receiver.remainRights);
            if (rights < 0)
            {
                // log entry: error - unknown rights entry
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_UNKNOWN_RIGHTS_ENTRY, env) + " " +
                    WorkflowTagConstants.ATTR_REMAINRIGHTSSTARTER + ": " +
                    currentStateLocal.receiver.remainRights,
                    this.wfdefinition.displayLog);

                // CRITICAL ERROR: exit
                return null;
            } // if

            this.rightsList.changeEntry (currentStateLocal.receiver.user.id, rights);

            // set remain-rights for ALL cc users on object according
            // to tag <REMAINRIGHTS="..."> in currentStates CC definition
            // (in list only; db-access will be performed in dbaction_phase)
            this.setCCRightsEntries (
                currentStateLocal, WorkflowTagConstants.ATTR_REMAINRIGHTS);

/*
            // creation of reference for receiver
            // (in list only; db-access will be performed in dbaction_phase)
            // he is probably allowed to watch the object as it proceeds
            // through the workflow
            this.referencesList.addEntry (currentState.receiver,
                                          currentState.destinationId);
*/
        } // if

        // exit method - returns next state
        return nextState;
    } // prestate_phase


    /***************************************************************************
     * Performs the state-phase of the forwarding process for the given object
     * of the given state. <BR/>
     * - performs ACTION if defined
     * - performs REGISTEROBSERVERJOB if defined
     * - Virtually sets CC and Receiver rights
     * - Virtually creates references for CC-users
     *      [Virtually means: physical db-changes will be done in db-action phase]
     *
     * @param   obj             the forward-object
     * @param   state           state for forwarding actions
     *
     * @return  <CODE>true</CODE>    everything worked
     *          <CODE>false</CODE>   an error occured
     */
    protected boolean state_phase (BusinessObject obj, State state)
    {
        State stateLocal = state;       // variable for local assignments
        int rights = 0;                 // for rights settings

        // add log-entry:
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_CURRENT_STATE, env) + ": " +
            stateLocal.name,
            this.wfdefinition.displayLog);

//
// TODO: MISSING: ACTIONS type="onEnter"
//
        //
        // perform ACTIONS that should be executed before transition
        // to next state
        //
        if (!this.executeActions (obj, stateLocal))
        {
            return false;
        } // if

        //
        // set observerjobs that shall be registered (in dbaction_phase)
        //
        this.registerJobs = stateLocal.registerJobs;

        //
        // complete states: get missing information and fill it in states
        //
        stateLocal = this.completeState (stateLocal, true);
        if (stateLocal == null)
        {
            return false;
        } // if

        // set current-state name
        this.workflowInstance.currentStateName = stateLocal.name;
        // set current owner of the workflow object
        this.workflowInstance.currentOwner = stateLocal.receiver.user.id;
        this.workflowInstance.currentContainer = stateLocal.receiver.destinationId;

        // set workflowstate according to position in definition
        // - 'normal' state             STATE_OPEN_RUNNING
        // - last state 'END'           STATE_OPEN_RUNNING_LASTSTATE
        // - last state 'END-NOCONFIRM' STATE_CLOSED_COMPLETED

        // ist this an end state?
//        if (state.name.equals(this.wfdefinition.endStateName))
        if (this.wfdefinition.isEndState (stateLocal.name))
        {
            // name of this states next state
            String nextStateName = stateLocal.transition.getNextState (null);

            // differ between next-state entry 'END' and 'END-NOCONFIRM'
            if (nextStateName
                .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY))
            {
                this.workflowInstance.workflowState =
                    WorkflowConstants.STATE_OPEN_RUNNING_LASTSTATE;
            } // if
            else
            {
                this.workflowInstance.workflowState =
                    WorkflowConstants.STATE_CLOSED_COMPLETED;
            } // else
        } // if
        else
        {
            this.workflowInstance.workflowState =
                WorkflowConstants.STATE_OPEN_RUNNING;
        } // else


        // log entry: receiver
        this.log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_FORWARD_RECEIVER, env) + ": " +
            stateLocal.receiver.user.fullname,
            this.wfdefinition.displayLog);
        // log entry: write to file only
        this.log.add (DIConstants.LOG_ENTRY,
                      "[User: " + stateLocal.receiver.user.username + ", id=" +
                      stateLocal.receiver.user.id + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      "[Target: " + stateLocal.receiver.destination +  ", oid=" +
                      stateLocal.receiver.destinationId.toString () + "]",
                      false);
        this.log.add (DIConstants.LOG_ENTRY,
                      " ",
                      false);


        // set rights for receiver on object according
        // to tag <RIGHTS="..."> in currentState RECEIVER definition
        // (in list only; db-access will be performed in dbaction_phase)
        rights = this.rightsMapping.getEntry (stateLocal.receiver.rights);
        if (rights < 0)
        {
            // log entry: error - unknown rights entry
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_UNKNOWN_RIGHTS_ENTRY, env) + " " +
                WorkflowTagConstants.ATTR_RIGHTS + " = " +
                stateLocal.receiver.rights,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // if
        this.rightsList.changeEntry (stateLocal.receiver.user.id, rights);


        // set rights for ALL cc users on object according
        // to tag <RIGHTS="..."> in currentStates CC definition
        // (in list only; db-access will be performed in dbaction_phase)
        this.setCCRightsEntries (stateLocal, WorkflowTagConstants.ATTR_RIGHTS);

        // add a reference for each cc-user to reference list
        this.addCCReferences (obj, stateLocal);

        // object movement
        //  --> in dbaction_phase

//
// TODO: MISSING: ACTIONS type="onState"
//                Attention -- must be after dbaction_phase
//

        // add protocol entry: forwarded to receiver
        this.addProtocolEntry (obj, stateLocal.name, WorkflowConstants.OP_SENTTORECEIVER,
                               this.user, stateLocal.receiver.user, "");

        // exit method:
        return true;
    } // state_phase


    /***************************************************************************
     * Performs the finalization-phase of the workflow for the
     * given object of the given end-state. <BR/>
     *
     * @param   obj             the forward-object
     * @param   state           state for forwarding actions
     *
     * @return  <CODE>true</CODE>    everything worked
     *          <CODE>false</CODE>   an error occured
     */
    protected boolean finalization_phase (BusinessObject obj, State state)
    {
        // workflow reached last state!
        // set the workflow state to 'completed'
        this.workflowInstance.workflowState =
            WorkflowConstants.STATE_CLOSED_COMPLETED;

        // set end date
        this.workflowInstance.endDate = new Date ();

////////////////////
//
// HACK-START
//
        // check if forwarded object is of type XML_Viewer
        // delete workflow-template information in XML_Viewer
        if (obj instanceof ibs.di.XMLViewer_01)
        {
            // type cast
            XMLViewer_01 viewerObj = (XMLViewer_01) obj;

            // unset workflow-template information
            viewerObj.workflowTemplateOid = OID.getEmptyOid ();

            try
            {
                // change objects information in the database
                // no rights-check necessary (op = 0)
                viewerObj.performChange (0);
            } // try
            catch (NameAlreadyGivenException e)
            {
                return false;
            } // catch
            catch (NoAccessException e)
            {
                return false;
            } // catch
        } // if
//
// HACK-END
//
////////////////////

//
// TODO: add switch to handle REMAINRIGHTS of last state
//

//
// TODO: add switch to UNREGISTER ALL OBSERVERJOBS?
//

        // add unregisterjobs of last state to list of already given
        // unregisterjobs
        if (state.unregisterJobs != null && state.unregisterJobs.size () > 0)
        {
            if (this.unregisterJobs != null)
            {
                this.unregisterJobs.addAll (state.unregisterJobs);
            } // if
            else
            {
                this.unregisterJobs = state.unregisterJobs;
            } // else
        } // if

        // add protocol entry: workflow finished
        this.addProtocolEntry (obj,
                               state.name,
                               WorkflowConstants.OP_COMPLETE,
                               this.user,
                               this.user, "");

        return true;
    } // finalization_phase


    /***************************************************************************
     * Performs the dbaction_phase of the given workflow-state for the
     * given object. <BR/>
     * the 4 workflow-phases are finished; now
     * write all necessary changes!
     * - store rights changes on forward object
     * - move object
     * - change owner
     * - store changes on workflow-instance
     * - notify users
     * - ...
     *
     * @param   obj             the forward-object
     * @param   state           state for forwarding actions
     *
     * @return  <CODE>true</CODE>    everything worked
     *          <CODE>false</CODE>   an error occured
     */
    protected boolean dbaction_phase (BusinessObject obj, State state)
    {
        // local variables
        boolean retValue = false;
//
// MISSING: write variables to db - at the end!
//
        // change owner to system-user (including subsequent objects)
        // this also includes the workflow object itself (this.)
        // because it is stored under the forward-object
        // (workflowInstance.containerId = obj.oid)
/* BB 050304: the whole performChangeOwnerRec is now deleted because
 *     it causes slow performance and is not neccessary
 *     the reason for setting the owner was to allow the workflow manager
 *     access to varios objects. this access can be solved by
 *     skipping the rights checks via using OP_NONE or fetching objects
 *     with no user forcing to use the OP_NONE operation
/
        try
        {
            obj.performChangeOwnerRec (0, this.systemUser.id);

            // change owner of this instance also!
            // because it is a subobject of the forward obect obj.
            // (workflowInstance will be stored at end of this procedure
            //  and owner would not be changed otherwise)
            workflowInstance.owner = this.systemUser;
        }
        catch (NoAccessException e) // no access to objects allowed
        {
            obj.showNoAccessMessage (Operations.OP_READ);

            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                          WorkflowMessages.MSG_CHANGEOWNER_FAILED,
                          this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // catch
*/
        this.workflowInstance.owner = this.systemUser;


//////////////////////////////////////
//
// SCHWAN-HACK: if indicated in the workflow-definition (IGNORRIGHTS="YES")
//  rights will be set according to rights of target-container
//
// BB 050304: IGNORERIGHTS is not supported anymore and replaced by SETRIGHTS

        // set rights for workflow-objects: kind of rights-setting
        // depends on setting in workflow-definition
        // SETRIGHTS="DEFAULT":  standard-behaviour; set rights according
        //                     to rights-tags in workflow
        // SETRIGHTS="INHERIT": ignore-rights; inherit rights of objects
        //                     target-container
        // SETRIGHTS="NONE": do not perform any rights operations
        if (this.wfdefinition.setRights == WorkflowConstants.SETRIGHTS_INHERIT)
        {
            // get rights of target-container and set them for
            // forwarded object + sub-objects
            retValue = this.rightsHandler.copyRightsKeyRec (
                obj.oid, state.receiver.destinationId);
        } // if (this.wfdefinition.ignoreRights ==
            // WorkflowConstants.IGNORERIGHTS_INHERIT)
        else if (this.wfdefinition.setRights == WorkflowConstants.SETRIGHTS_DEFAULT)
        {
            try
            {
                obj.performChangeOwnerRec (0, this.systemUser.id);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                obj.showNoAccessMessage (Operations.OP_READ);

                // log entry: error
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_CHANGEOWNER_FAILED, env),
                    this.wfdefinition.displayLog);

                // CRITICAL ERROR: exit
                return false;
            } // catch
            // store the rights-changes on object in the db
            retValue = this.rightsHandler.setRightsList (obj.oid,
                    this.rightsList, this.systemUser);
        } // else if (this.wfdefinition.ignoreRights == WorkflowConstants.IGNORERIGHTS_NO)
        else // no rights operations
        {
            // no rights operations. but indicate that everything went ok
            retValue = true;
        } // else no rights operations

//
//
//////////////////////////////////////

        // check success
        if (!retValue)
        {
            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_SETRIGHTS_FAILED, env),
                this.wfdefinition.displayLog);
            this.log.add (DIConstants.LOG_ERROR,
                this.rightsList.toString (),
                false);
            // CRITICAL ERROR: exit
            return false;
        } // if


        // move the object to the destination container of the
        // new currentOwner
        // IMPORTANT: move only if targetcontainer and currentcontainer
        //            are different
        if (!state.receiver.destinationId.equals (obj.containerId))
        {
            if (!this.objectHandler.moveObject (state.receiver.destinationId, obj))
            {
                // log entry: error
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_FORWARD_MOVE_FAILED, env),
                    this.wfdefinition.displayLog);

                // CRITICAL ERROR: exit
                return false;
            } // if
        } // if

        // physically create all references (stored in referencelist)
        // no error handling necessary: no critical error if creation fails!
        this.createReferences (obj);

/*
TO-CHANGE: Probably later; probably never

        // make initial changes (start state only)
        if (state.name.equalsIgnoreCase(this.wfdefinition.startStateName))
        {
            // set workflow flag on object
            this.objectHandler.setWorkflowFlag (obj.oid, true);
        } // if

        // unset if workflow is completed
        if (workflowInstance.workflowState.equals (WorkflowConstants.STATE_CLOSED_COMPLETED))
        {
            // unsset workflow flag on object
            this.objectHandler.setWorkflowFlag (obj.oid, false);
        } // if
*/

        // store the workflow instance with all changes done
        // during this state
        try
        {
            // no rights required (normaly OP_CHANGE), every
            // user involved should be able to access the instance data
            this.workflowInstance.performChange (0);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);

            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_CHANGE_FAILED, env),
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // catch
        catch (NameAlreadyGivenException e) // no access to objects allowed
        {
            // display error message:
            this.showNameAlreadyGivenMessage ();
        } // catch

        // store variables in db
        if (!this.storeVariables ())
        {
            // CRITICAL ERROR: exit
            return false;
        } // if

        // delete all objectRead-entries; includes object-read entries for object
        // and all refererences on object; after forwarding object and refs will
        // appear to the user as 'NEW' (idea from Heinz)
        this.objectHandler.delObjReadEntries (obj.oid);

        //
        // register and unregister jobs
        // (failed un/register will not throw an error - only logged)
        //
        this.unregisterJobs (obj);
        this.registerJobs (obj);

        // store all protocol entries
        this.storeProtocol ();

        // delete instance-variables if workflow is completed
        if (this.workflowInstance.workflowState
            .equalsIgnoreCase (WorkflowConstants.STATE_CLOSED_COMPLETED))
        {
            // delete instance variables
            this.deleteVariables ();
        } // if

        // send notification
        if (!this.sendNotification (obj, state))
        {
            // log entry: error
            this.log.add (DIConstants.LOG_WARNING,
                          "Error during notification",
                          this.wfdefinition.displayLog);
        } // if

        // exit method
        return true;
    } // dbaction_phase


    /**************************************************************************
     * Retrieves missing definition header data from db and checks validity.
     * <BR/>
     * Reads:   - userId of the process manager
     *          - containerId of the process managers dest. container
     *
     * @return  <CODE>true</CODE>    if data is valid (objects found in db)
     *          <CODE>false</CODE>   if data is unvalid (not found in db)
     */
    protected boolean completeHeader ()
    {
        //
        // 1. get starter information
        this.wfdefinition.starter.user
            = this.objectHandler.getUserFromId (this.workflowInstance.starter);

        // check success
        if (this.wfdefinition.starter.user == null)
        {
            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_USER_NOT_FOUND, env) + " (S) " +
                this.workflowInstance.starterName,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // if

        this.wfdefinition.starter.completed = true;
        // set starters rights (rights=remainrights)
        if (this.wfdefinition.starter.rights.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
        {
            this.wfdefinition.starter.rights = WorkflowConstants.DEFAULT_REMAINRIGHTSSTARTER;
        } // if
        this.wfdefinition.starter.remainRights = this.wfdefinition.starter.rights;
        // set rest of starter-info (most of this info is not needed!)
        this.wfdefinition.starter.destinationId = this.workflowInstance.starterContainer;
        this.wfdefinition.starter.destination = this.workflowInstance.starterContainer.toString ();
        this.wfdefinition.starter.name = this.wfdefinition.starter.user.username;

        //
        // 2. get currentOwners information
        this.wfdefinition.currentOwner.user
            = this.objectHandler.getUserFromId (this.workflowInstance.currentOwner);

        // check success
        if (this.wfdefinition.currentOwner.user == null)
        {
            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_USER_NOT_FOUND, env) + " (CO) " +
                this.workflowInstance.currentOwnerName,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // if

        // set currentowner rights
        this.wfdefinition.currentOwner.rights = WorkflowConstants.UNDEFINED;
        this.wfdefinition.currentOwner.remainRights = WorkflowConstants.UNDEFINED;
        // set rest of currentowner-info (most of this info is not needed!)
        this.wfdefinition.currentOwner.destinationId = this.workflowInstance.currentContainer;
        this.wfdefinition.currentOwner.destination = this.workflowInstance.currentContainer.toString ();
        this.wfdefinition.currentOwner.name = this.wfdefinition.currentOwner.user.username;
        this.wfdefinition.currentOwner.completed = true;

        //
        // 3.a) get process managers user information
        //
        // check if processManager of this instance is defined via
        // runtime variable '#STARTER#'
        if (this.wfdefinition.processMgr.name
            .equalsIgnoreCase (WorkflowConstants.RUNTIME_STARTER))
        {
            // get the process-manager of the workflow
            this.wfdefinition.processMgr.user = this.wfdefinition.starter.user;
        } // if
        else
        {
            // get processmanager via name
            this.wfdefinition.processMgr.user =
                this.objectHandler.getUserFromName (this.wfdefinition.processMgr.name);
        } // else

        // check success
        if (this.wfdefinition.processMgr.user == null)
        {
            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_USER_NOT_FOUND, env) + " (PM) " +
                this.wfdefinition.processMgr.name,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // if

        // set this processmanager's name:
        this.wfdefinition.processMgr.name =
            this.wfdefinition.processMgr.user.username;

        //
        // 3.b) get processmanager-destination container information
        // check if receiver-destination of process-mgr is defined via
        // runtime variable '#STARTERCONTAINER#'
        if (this.wfdefinition.processMgr.destination
            .equalsIgnoreCase (WorkflowConstants.RUNTIME_STARTERCONTAINER))
        {
            // get container via processmanager-information
            this.wfdefinition.processMgr.destinationId =
                this.wfdefinition.starter.destinationId;
        } // if
        else
        {
            // get container via path-data
            this.wfdefinition.processMgr.destinationId =
                BOHelpers.resolveObjectPath (this.wfdefinition.processMgr.destination,
                    this, this.env);
        } // else

        // check success
        if (this.wfdefinition.processMgr.destinationId == null)
        {
            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_DEST_NOT_FOUND, env) + " (PM) " +
                this.wfdefinition.processMgr.destination,
                this.wfdefinition.displayLog);

            // CRITICAL ERROR: exit
            return false;
        } // if


        // set starters rights (rights=remainrights)
        if (this.wfdefinition.processMgr.rights.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
        {
            this.wfdefinition.processMgr.rights = WorkflowConstants.DEFAULT_REMAINRIGHTSSTARTER;
        } // if
        this.wfdefinition.processMgr.remainRights = this.wfdefinition.processMgr.rights;
        // set rest of procmgr-info
        this.wfdefinition.processMgr.completed = true;

        // exit
        return true;
    } // completeHeader



    /**************************************************************************
     * completes given state: Check data and fill missing data with instance-,
     * user- and object-data from the db. <BR/>
     * It also handles given RUN-TIME variables: #STARTER#, #STARTERCONTAINER#,
     *          #AD-HOC#, #VARIABLE.xxx#
     *
     * @param  state            The state.
     * @param  notInPreState    If <CODE>true</CODE>: evaluates variable-information.
     *                          means: receivers/destination with #AD-HOC#
     *                          or #VARIABLE.xxx# will be evaluated
     *                          (e.g. when in state-phase); <BR/>
     *                          otherwise it uses pre-state information
     *                          (e.g. currentOwner)
     *
     * @return  The state with the given name.
     *          <CODE>null</CODE> if there occurred an error when retrieving
     *          the state.
     */
    protected State completeState (State state, boolean notInPreState)
    {
        // init variables
        int returnValue;

        // exit parameter not set
        if (state == null)
        {
            return null;
        } // if

        /////////////////////////////////////////////////////////////////////
        //
        // 1. get and set receiver information
        //
        // get and check [missing] data from db (users, object-ids, ...);
        // set some default values for unset attributes
        //
        // if notInPreState is
        // - false: get receiver/destination according to given value
        //          (e.g. <name>, #AD-HOC#, #STARTER#, #VARIABLE.xxx#, ...)
        // - true:  receiver/destination already known from last forwarding
        //          step and stored in info of workflow-instance; get it from
        //          there
        //
        // (a) get receiver-user information
        //
        if (notInPreState)
        {
            // check if name contains #AD-HOC#
            if (WorkflowHelpers.containsIgnoreCase (state.receiver.name,
                                                   WorkflowConstants.RUNTIME_ADHOC))
            {
                // get adhoc-user information (stored in session)
                // init receiver info
                state.receiver.user = null;
                // get receiver info out of session
                // --> array String[3]: 3rd string is users id!
                // --> array has only 1 element (max)
                if (this.sess.receivers != null)
                {
                    if (this.sess.receivers.size () > 0)
                    {
                        // get string array
                        String[] elem = this.sess.receivers.firstElement ();
                        int id = new Integer (elem[2]).intValue ();
                        // get user from id
                        state.receiver.user = this.objectHandler.getUserFromId (id);
                        // set states receivername also
                        if (state.receiver != null)
                        {
                            state.receiver.name = state.receiver.user.username;
                        } // if
                        // clear session - info!
                        this.sess.receivers = null;
                    } // if
                } // if
            } // if #AD-HOC#

            // test for and replace with global/runtime variables
            // [#STARTER, #PROCESSMANAGER#, #CURRENTOWNER#, #VARIABLE.xxx#]
            else if ((returnValue = this.replaceVariableInReceiver (state.receiver)) != 0)
            {
                // check for error
                if (returnValue < 0)
                {
                    return null;
                } // if
                // otherwise: replacement successful - proceed
            } // else ... [variables in receivername]

            // otherwise get user by name
            else
            {
                // get user by name
                state.receiver.user = this.objectHandler.getUserFromName (state.receiver.name);
            } // else ... get user by name

        } // if
        else        // !notInPreState
        {
            // Set states receiver to current owner:
            // - this behaviour is necessary for previous state only
            state.receiver.user = this.wfdefinition.currentOwner.user;
        } // else
        //
        // check success
        if (state.receiver.user == null)
        {
            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_RECEIVER_NOT_FOUND, env) + " " +
                state.receiver.name,
                this.wfdefinition.displayLog);
            // CRITICAL-ERROR: exit
            return null;
        } // if

        // set this states receivername also
        state.receiver.name = state.receiver.user.username;

        //
        //
        // (b) get receivers-destination container information
        //
        if (notInPreState)
        {
            // check if destination-container contains #AD-HOC#
            if (WorkflowHelpers.containsIgnoreCase (state.receiver.destination,
                                                   WorkflowConstants.RUNTIME_ADHOC))
            {
                // replace "#AD-HOC#" with receivers name!
                state.receiver.destination = StringHelpers.replace (
                    state.receiver.destination,
                    WorkflowConstants.RUNTIME_ADHOC, state.receiver.name);
                // get container id via object path
                state.receiver.destinationId =
                    BOHelpers.resolveObjectPath (state.receiver.destination,
                        this, this.env);
            } // if #AD-HOC#
            //
            // test for and replace with global/runtime variables in receivers destination
            // #STARTER#, #STARTERCONTAINER#, #PROCESSMANAGER#, #PROCESSMANAGERCONTAINER#
            // #VARIABLE.xxx#]
            else if ((returnValue = this.replaceVariableInReceiverDestination (state.receiver)) != 0)
            {
                // check for error
                if (returnValue < 0)
                {
                    return null;
                } // if
                // otherwise: replacement successful - proceed
            } // else if ... [variables in receiverdestination]
            //
            // otherwise get destination container via object path
            else
            {
                state.receiver.destinationId =
                    BOHelpers.resolveObjectPath (state.receiver.destination,
                        this, this.env);
            } // else
        } // if
        else        // InPreState
        {
            // get container from workflow info
            state.receiver.destinationId =
                this.workflowInstance.currentContainer;
        } // else

        // check success
        if (state.receiver.destinationId == null)
        {
            // log entry: error
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_RECEIVERDEST_NOT_FOUND, env) + " " +
                state.receiver.destination,
                this.wfdefinition.displayLog);
            // CRITICAL-ERROR: exit
            return null;
        } // if
        //
        // (c) set receiver-rights information
        //
        // if receivers rights are not set, set to default values
        if (state.receiver.rights.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
        {
            state.receiver.rights = WorkflowConstants.DEFAULT_RECEIVERRIGHTS;
        } // if
        if (state.receiver.remainRights.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
        {
            state.receiver.remainRights = WorkflowConstants.DEFAULT_RECEIVERREMAINRIGHTS;
        } // if
        //
        // completed!
        state.receiver.completed = true;
        //
        // end - get receiver information
        //
        /////////////////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////////////////
        //
        // 2. now retrieve data for every CC-receiver
        // if a cc-receiver or its destination-container cannot be retrieved
        // a warning will be given (no critical-error) and the according
        // cc-entry will be removed from the cc-list
        //
        Receiver cc;  // cc-entry
        @SuppressWarnings ("unchecked")
        Vector<Receiver> v = (Vector<Receiver>) state.ccs.clone ();
        for (Iterator<Receiver> iter = v.iterator (); iter.hasNext ();)
        {
            // get next cc:
            cc = iter.next ();

            //
            // (a) get receivers user information
            //
            // test for and replace with global/runtime variables
            // [#STARTER, #PROCESSMANAGER#, #CURRENTOWNER#, #VARIABLE.xxx#]
            if ((returnValue = this.replaceVariableInReceiver (cc)) != 0)
            {
                // even on error: no exception
                // cc is not that important (warning has been written to log)
                // --> proceed
            } // if ... [variables in receivername]

            // otherwise get user-infomation by given name
            else
            {
                cc.user = this.objectHandler.getUserFromName (cc.name);
            } // else
            //
            // check success
            if (cc.user == null)
            {
                // log entry: error - cc-user not found
                this.log.add (DIConstants.LOG_WARNING,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_RECEIVER_NOT_FOUND, env) +
                    cc.name + " [CC]",
                    this.wfdefinition.displayLog);
                // remove entry

                state.ccs.removeElement (cc);
                // NON-CRITICAL-ERROR: no exit
            } // if
            else
            {
                // cc-user retrieved

                //
                // (b) now get cc-users destination container information
                //
                // test for and replace with global/runtime variables in ccs destination
                // #STARTER#, #STARTERCONTAINER#, #PROCESSMANAGER#, #PROCESSMANAGERCONTAINER#
                // #VARIABLE.xxx#]
                if ((returnValue = this.replaceVariableInReceiverDestination (cc)) != 0)
                {
                    // even on error: no exception
                    // cc is not that important (warning has been written to log)
                    // --> proceed
                } // if ... [variables in ccs destination]
                else
                {
                    // get container via path data
                    cc.destinationId = BOHelpers.resolveObjectPath (
                        cc.destination, this, this.env);
                } // else

                // check success
                if (cc.destinationId == null)
                {
                    // cc user not found - log entry: error
                    this.log.add (DIConstants.LOG_WARNING,
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_RECEIVERDEST_NOT_FOUND, env) +
                        cc.destination + " [CC]",
                        this.wfdefinition.displayLog);
                    // remove entry
                    state.ccs.removeElement (cc);
                    // NON-CRITICAL-ERROR: no exit
                } // if
            } // else
        } // for iter

        //
        //
        /////////////////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////////////////
        //
        // 3. now retrieve necessary data for every user in notification
        //    --> usernames are given; user-oids are needed for notification
        //    --> notification itself will be performed in db_actionphase
        //
        // perform only for forwarding state and when notification-information
        // is given
        if (notInPreState &&
            state.notification != null &&
            state.notification.size () > 0)
        {
            // replace every variable (#PROCESSMANAGER#, #VARIABLE.xxx#, ...)
            // in notifications user-lists
            this.replaceVariablesInNotification (state);

            // retreive user-oids for users given in states notification
            // user-lists
            this.retrieveUserOIDsForNotification (state);
        } // if (notInPreState)
        //
        //
        /////////////////////////////////////////////////////////////////////

        // exit method
        return state;
    } // completeState


    /**************************************************************************
     * Gets/Sets information about user in given receiver, if receivers name
     * holds runtime/global variables like:
     *  [#STARTER, #PROCESSMANAGER#, #VARIABLE.xxx#]
     *
     * @param receiver  the receiver-object
     *
     * @return  <CODE>1</CODE>       success; a variable was replaced
     *          <CODE>0</CODE>       nothing done; no variable found in given receiver
     *          <CODE>-1</CODE>      error; needed information not found; see log for details
     */
    protected int replaceVariableInReceiver (Receiver receiver)
    {
        // init return value
        int returnValue = 1;

        // check if name contains #STARTER#
        if (WorkflowHelpers.containsIgnoreCase (receiver.name,
                                               WorkflowConstants.RUNTIME_STARTER))
        {
            // get user who started workflow
            receiver.user = this.wfdefinition.starter.user;
        } // else if ... #STARTER#

        // check if name contains #PROCESSMANAGER#
        else if (WorkflowHelpers.containsIgnoreCase (receiver.name,
                                                    WorkflowConstants.RUNTIME_PROCESSMANAGER))
        {
            // get user who started workflow
            receiver.user = this.wfdefinition.processMgr.user;
        } // else if ... #STARTER#

        // check if name contains #VARIABLE.xxx#;
        else if (WorkflowHelpers.containsIgnoreCase (receiver.name,
                                                    WorkflowConstants.RUNTIME_PREFIX))
        {
            // get variable-object wich name is given in receivername
            Variable userVariable
                = this.wfdefinition.variables.getEntry (receiver.name);

            // check if variable exists
            if (userVariable == null)
            {
                // log entry: error
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_UNKNOWN_VARIABLE, env) + " " +
                    receiver.name,
                    this.wfdefinition.displayLog);
                // CRITICAL-ERROR: exit
                return -1;
            } // if

            // get user by name (= value of variable)
            receiver.user = this.objectHandler.getUserFromName (userVariable.value);
        } // else if ... #VARIABLE.xxx#

        // name contains no variable at all
        else
        {
            // set return value
            returnValue = 0;
        } // else ... no replacement necessary

        // exit
        return returnValue;
    } // replaceVariableInReceiver


    /**************************************************************************
     * Gets/Sets information about destination in given receiver, if
     * receiverdestination holds runtime/global variables like:
     * #STARTER#, #STARTERCONTAINER#, #PROCESSMANAGER#, #PROCESSMANAGERCONTAINER#
     * #VARIABLE.xxx#
     *
     * @param receiver  the receiver-object
     *
     * @return  <CODE>1</CODE>       success; a variable was replaced
     *          <CODE>0</CODE>       nothing; no variable found in given receiverdestination
     *          <CODE>-1</CODE>      error; needed information not found; see log for details
     */
    protected int replaceVariableInReceiverDestination (Receiver receiver)
    {
        // init return value
        int returnValue = 1;

        // check if destination-container contains #STARTERCONTAINER#
        if (WorkflowHelpers.containsIgnoreCase (receiver.destination,
                                                    WorkflowConstants.RUNTIME_STARTERCONTAINER))
        {
            // get container via starter-information
            receiver.destinationId = this.workflowInstance.starterContainer;
        } // else if ... #STARTERCONTAINER#

        // check if destination-container contains #PROCESSMANAGERCONTAINER#
        else if (WorkflowHelpers.containsIgnoreCase (receiver.destination,
                                                    WorkflowConstants.RUNTIME_PROCESSMANAGERCONTAINER))
        {
            // get container via starter-information
            receiver.destinationId = this.workflowInstance.processManagerCont;
        } // else if ... #PROCESSMANAGERCONTAINER#

        // check if destination-container contains #STARTER#
        else if (WorkflowHelpers.containsIgnoreCase (receiver.destination,
                                                    WorkflowConstants.RUNTIME_STARTER))
        {
            // replace "#STARTER#" with starters name!
            receiver.destination = StringHelpers.replace (
                receiver.destination, WorkflowConstants.RUNTIME_STARTER,
                this.wfdefinition.starter.name);
            // get container id via object path
            receiver.destinationId = BOHelpers.resolveObjectPath (
                receiver.destination, this, this.env);
        } // else if ... #STARTER#

        // check if destination-container contains #PROCESSMANAGER#
        else if (WorkflowHelpers.containsIgnoreCase (receiver.destination,
                                                    WorkflowConstants.RUNTIME_PROCESSMANAGER))
        {
            // replace #PROCESSMANAGER# with process managers name!
            receiver.destination = StringHelpers.replace (
                receiver.destination, WorkflowConstants.RUNTIME_PROCESSMANAGER,
                this.wfdefinition.processMgr.name);
            // get container id via object path
            receiver.destinationId = BOHelpers.resolveObjectPath (
                receiver.destination, this, this.env);
        } // else if ... #PROCESSMANAGER#

        // check if destination-container contains #VARIABLE.xxx#
        else if (WorkflowHelpers.containsIgnoreCase (receiver.destination,
                                                    WorkflowConstants.RUNTIME_PREFIX))
        {
            // check if string contains a valid variable
            Variable variable
                = this.wfdefinition.variables.getEntry (receiver.destination);
            if (variable == null)
            {
                // no valid variable found in destination
                // log entry: error
                this.log.add (DIConstants.LOG_WARNING,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_UNKNOWN_VARIABLE, env) + " " +
                    receiver.destination,
                    this.wfdefinition.displayLog);
                // CRITICAL-ERROR: exit
                return -1;
            } // if

            // replace variable in given string with variables value
            // variable must be defined/set!
            receiver.destination
                = this.wfdefinition.variables.replaceWithValue (receiver.destination);

            // get container id via object path
            receiver.destinationId = BOHelpers.resolveObjectPath (
                receiver.destination, this, this.env);
        } // else if ... #VARIABLE.xxx#

        // name contains no variable at all
        else
        {
            // set return value
            returnValue = 0;
        } // else ... no replacement necessary

        // exit
        return returnValue;
    } // replaceVariableInReceiverDestination


    /**************************************************************************
     * Replaces variables in notification-objects user-lists. <BR/> Only if list
     * holds runtime/global variables like:
     *  [#STARTER, #PROCESSMANAGER#, #VARIABLE.xxx#, #RECEIVER#, #CCS#]
     *
     * @param state     the state-object that holds the notification object
     *
     * @return  <CODE>1</CODE>       success; a variable was replaced
     *          <CODE>0</CODE>       success; but nothing done; no variable found in
     *                  notifications user-lists
     *          <CODE>-1</CODE>      warning; some of the needed information not found;
     *                  see log for warning-messages
     */
    protected int replaceVariablesInNotification (State state)
    {
        // initialize variables
        int returnValue = 0;
        Notify notify;
        boolean undefEntries = false;
        boolean replacesOccur = true;

        // loop through all Notify-entries in Notification-object:
        for (Iterator<Notify> iter = state.notification.iterator ();
            iter.hasNext ();)
        {
            // get next Notify-element of Notification-vector:
            notify = iter.next ();

            // check if valid
            if (notify != null && notify.users != null)
            {
                // try to replace values until no more replacements occur
                replacesOccur = true; // enter loop at least one time
                while (replacesOccur)
                {
                    // set replaces-flag
                    replacesOccur = false;

                    // check if list contains #CCS#
                    // entry will be replaces with comma-separated list of cc-receiver-names
                    if (WorkflowHelpers.containsIgnoreCase (
                        notify.users, WorkflowConstants.RUNTIME_CCS))
                    {
                        // set flag
                        replacesOccur = true;
                        // init cc-names string
                        String ccNames = "";

                        // create comma-separated list of all cc-receiver-names
                        if (state.ccs != null && state.ccs.size () > 0)
                        {
                            Receiver ccReceiver;
                            for (Iterator<Receiver> iterCc = state.ccs.iterator ();
                                iterCc.hasNext ();)
                            {
                                // get next cc-receiver:
                                ccReceiver = iterCc.next ();

                                // check if name of cc-element
                                if (ccReceiver != null && ccReceiver.name != null)
                                {
                                    ccNames += ";" + ccReceiver.user.username;
                                } // if
                            } // for iterCc
                        } // if

                        // replace with list of all cc-receiver-names
                        notify.users = StringHelpers.replace (
                            notify.users, WorkflowConstants.RUNTIME_CCS,
                            ccNames);
                        // set return value
                        returnValue = 1;
                    } // else if ... #CCS#

                    // check if name contains #VARIABLE.xxx#;
                    // each variable-entry will be replaces with its value;
                    // undefined variables will be removed and a warning will be raised in the log
                    if (WorkflowHelpers.containsIgnoreCase (notify.users,
                                                                WorkflowConstants.RUNTIME_PREFIX))
                    {
                        // set flag
                        replacesOccur = true;
                        // copy string
                        String copy = notify.users.toString ();
                        // replace every variable in string with its value
                        notify.users = this.wfdefinition.variables.replaceWithValue (notify.users);

                        // check if all variables replaced (UNDEFINED-entry if not)
                        undefEntries = false;
                        while (WorkflowHelpers.containsIgnoreCase (notify.users,
                                                                  WorkflowConstants.UNDEFINED))
                        {
                            // set flag
                            undefEntries = true;

                            //replace undefined entry with empty-string
                            if (notify.users
                                .equalsIgnoreCase (WorkflowConstants.UNDEFINED))
                            {
                                notify.users = StringHelpers.replace (
                                    notify.users, WorkflowConstants.UNDEFINED,
                                    "");
                            } // if
                            else
                            {
                                notify.users = StringHelpers.replace (
                                    notify.users, ";" +
                                        WorkflowConstants.UNDEFINED, "");
                            } // else
                        } // while

                        // add warning if neccesary
                        if (undefEntries)
                        {
                            // no valid variable found in destination
                            // log entry: error
                            this.log.add (DIConstants.LOG_ERROR,
                                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                                    WorkflowMessages.ML_MSG_UNKNOWN_VARIABLE, env) +
                                " (" + copy + ")",
                                this.wfdefinition.displayLog);
                            // NON-CRITICAL-ERROR: no exit
                        } // if

                        // set return value
                        returnValue = 1;
                    } // if ... #VARIABLE.xxx#

                    // check if list contains #STARTER#
                    if (WorkflowHelpers.containsIgnoreCase (
                        notify.users, WorkflowConstants.RUNTIME_STARTER))
                    {
                        // set flag
                        replacesOccur = true;
                        // replace with starter name
                        notify.users = StringHelpers.replace (
                            notify.users, WorkflowConstants.RUNTIME_STARTER,
                            this.wfdefinition.starter.user.username);
                        // set return value
                        returnValue = 1;
                    } // if ... #STARTER#

                    // check if list contains #PROCESSMANAGER#
                    if (WorkflowHelpers.containsIgnoreCase (
                        notify.users, WorkflowConstants.RUNTIME_PROCESSMANAGER))
                    {
                        // set flag
                        replacesOccur = true;
                        // replace with processmanager name
                        notify.users = StringHelpers.replace (
                            notify.users,
                            WorkflowConstants.RUNTIME_PROCESSMANAGER,
                            this.wfdefinition.processMgr.user.username);
                        // set return value
                        returnValue = 1;
                    } // if ... #PROCESSMANAGER#

                    // check if list contains #RECEIVER#
                    if (WorkflowHelpers.containsIgnoreCase (
                        notify.users, WorkflowConstants.RUNTIME_RECEIVER))
                    {
                        // set flag
                        replacesOccur = true;
                        // replace with receivers name
                        notify.users = StringHelpers.replace (
                            notify.users, WorkflowConstants.RUNTIME_RECEIVER,
                            state.receiver.user.username);
                        // set return value
                        returnValue = 1;
                    } // if ... #RECEIVER#
                } // while (replacesOccur)
            } // if ... valid notify-entry

            // check if groups valid
            if (notify != null && notify.groups != null)
            {
                // try to replace values until no more replacements occur
                replacesOccur = true; // enter loop at least one time
                while (replacesOccur)
                {
                    // set replaces-flag
                    replacesOccur = false;

                    // check if name contains #VARIABLE.xxx#;
                    // each variable-entry will be replaces with its value;
                    // undefined variables will be removed and a warning will be raised in the log
                    if (WorkflowHelpers.containsIgnoreCase (
                        notify.groups, WorkflowConstants.RUNTIME_PREFIX))
                    {
                        // set flag
                        replacesOccur = true;
                        // copy string
                        String copy = notify.groups.toString ();
                        // replace every variable in string with its value
                        notify.groups = this.wfdefinition.variables.replaceWithValue (notify.groups);

                        // check if all variables replaced (UNDEFINED-entry if not)
                        undefEntries = false;
                        while (WorkflowHelpers.containsIgnoreCase (
                            notify.groups, WorkflowConstants.UNDEFINED))
                        {
                            // set flag
                            undefEntries = true;

                            //replace undefined entry with empty-string
                            if (notify.groups
                                .equalsIgnoreCase (WorkflowConstants.UNDEFINED))
                            {
                                notify.groups = StringHelpers.replace (
                                    notify.groups, WorkflowConstants.UNDEFINED,
                                    "");
                            } // if
                            else
                            {
                                notify.groups = StringHelpers.replace (
                                    notify.groups, ";" +
                                        WorkflowConstants.UNDEFINED, "");
                            } // else
                        } // while

                        // add warning if neccesary
                        if (undefEntries)
                        {
                            // no valid variable found in destination
                            // log entry: error
                            this.log.add (DIConstants.LOG_ERROR,
                                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                                    WorkflowMessages.ML_MSG_UNKNOWN_VARIABLE, env) +
                                " (" + copy + ")",
                                this.wfdefinition.displayLog);
                            // NON-CRITICAL-ERROR: no exit
                        } // if

                        // set return value
                        returnValue = 1;
                    } // if ... #VARIABLE.xxx#
                } // while (replacesOccur)
            } // if ... valid notify-entry
        } // for iter

        // exit
        if (undefEntries)
        {
            returnValue = -1;
        } // if
        return returnValue;
    } // replaceVariableInNotification


    /**************************************************************************
     * Retrieves a user-oid for each entry in the states notifications
     * user-names list. <BR/> The user-oids will be stored in the userOIDs-
     * vector of the notification object. Information about not-found
     * user-oids (due to non-existent user-name) will be stored as warning
     * in the workflow log.
     *
     * @param state     the state-object that holds the notification object
     *
     * @return  <CODE>0</CODE>       success; every oid could have been retrieved
     *          <CODE>-1</CODE>      warning; some users not found; see log for details
     */
    protected int retrieveUserOIDsForNotification (State state)
    {
        // declare variables
        int retVal = 0;
        Notify notify;
        String token;
        String name;
        StringTokenizer st;
        Vector<String> userNames;
        Vector<String> userNamesNonExistent;
        Vector<OID> userOids;
        Vector<String> groupNames;
        Vector<String> groupNamesNonExistent;
        Vector<OID> groupOids;

        // loop through notification-entries:
        for (Iterator<Notify> iter = state.notification.iterator (); iter.hasNext ();)
        {
            // init vectors:
            userNames = new Vector<String> ();
            userNamesNonExistent = new Vector<String> ();
            userOids = new Vector<OID> ();

            groupNames = new Vector<String> ();
            groupNamesNonExistent = new Vector<String> ();
            groupOids = new Vector<OID> ();

            // get next element:
            notify = iter.next ();

            boolean groupDefined = notify.groups != null &&
                !notify.groups.equals (WorkflowConstants.UNDEFINED) &&
                !notify.groups.isEmpty ();

            // check if user elements set
            if (notify.users != null &&
                (!notify.users.equals (WorkflowConstants.UNDEFINED) ||
                // if users is undefined enter only if the group is also defined
                 !groupDefined))
            {
                // get user-names out of comma-separated list
                st = new StringTokenizer (notify.users, ";");
                while (st.hasMoreElements ())
                {
                    // get next token
                    token = (String) st.nextElement ();
                    // store it in vector
                    userNames.addElement (token);
                } // while

                // retrieve list of user-oids with list of given user-names;
                // names of users that do not exists (no user, deleted, ...)
                // are stored in the second parameter userNamesNonExistent)
                userOids = this.objectHandler.getUserOidsByNames (
                    userNames, userNamesNonExistent);

                // check success
                if (userOids == null)
                {
                    // set returnvalue
                    retVal = -1;
                    // reset userOids
                    userOids = new Vector<OID> ();
                    // log entry: warning
                    this.log.add (DIConstants.LOG_WARNING,
                                  "DB-ERROR when retrieving notification users: " +
                                  userNames.toString (),
                                  this.wfdefinition.displayLog);
                     // NON-CRITICAL-ERROR: no exit
                } // if

                // write warning for each entry in userNamesNonExistent:
                for (Iterator<String> iter2 = userNamesNonExistent.iterator ();
                     iter2.hasNext ();)
                {
                    // set returnvalue:
                    retVal = -1;
                    // get next:
                    name = iter2.next ();

                    // log entry: warning
                    this.log.add (DIConstants.LOG_WARNING,
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_USER_NOT_FOUND, env) +
                        name + " [NT]",
                        this.wfdefinition.displayLog);
                        // NON-CRITICAL-ERROR: no exit
                } // for iter2

                // set oid-list in notify-object
                notify.userOIDs = userOids;
            } // if
            else
            {
                // user-names list is not set; initialize lists
                notify.users = "";
                notify.userOIDs = new Vector<OID> ();
            } // else

            // check if group elements defined
            if (groupDefined)
            {
                // get user-names out of comma-separated list
                st = new StringTokenizer (notify.groups, ";");
                while (st.hasMoreElements ())
                {
                    // get next token
                    token = (String) st.nextElement ();
                    // store it in vector
                    groupNames.addElement (token);
                } // while

                // retrieve list of user-oids with list of given user-names;
                // names of users that do not exists (no user, deleted, ...)
                // are stored in the second parameter userNamesNonExistent)
                groupOids = GroupHelpers.getGroupOidsByNames (
                    groupNames, groupNamesNonExistent, this.getUser (), this.env);

                // check success
                if (groupOids == null)
                {
                    // set returnvalue
                    retVal = -1;
                    // reset userOids
                    groupOids = new Vector<OID> ();
                    // log entry: warning
                    this.log.add (DIConstants.LOG_WARNING,
                                  "DB-ERROR when retrieving notification groups: " +
                                  groupOids.toString (),
                                  this.wfdefinition.displayLog);
                     // NON-CRITICAL-ERROR: no exit
                } // if

                // write warning for each entry in userNamesNonExistent:
                for (Iterator<String> iter2 = groupNamesNonExistent.iterator ();
                     iter2.hasNext ();)
                {
                    // set returnvalue:
                    retVal = -1;
                    // get next:
                    name = iter2.next ();

                    // log entry: warning
                    this.log.add (DIConstants.LOG_WARNING,
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_GROUP_NOT_FOUND, env) +
                        name + " [NT]",
                        this.wfdefinition.displayLog);
                        // NON-CRITICAL-ERROR: no exit
                } // for iter2

                // set oid-list in notify-object
                notify.groupOIDs = groupOids;
            } // if group elements defined
            else
            {
                // user-names list is not set; initialize lists
                notify.groups = "";
                notify.groupOIDs = new Vector<OID> ();
            } // else
        } // for iter

        // exit
        return retVal;
    } // retrieveUserOIDsForNotification


    /**************************************************************************
     * Send notifications to all users specified in state. <BR/>
     *
     * @param   obj     The object, for which to send the notification.
     * @param   state   the state where the notification-object is specified
     *
     * @return  <CODE>true</CODE>    success
     *          <CODE>false</CODE>   otherwise
     */
    protected boolean sendNotification (BusinessObject obj, State state)
    {
        // init string for receivers where notification failed
        String failedReceiverNames = "";

        // retrieve the notification service from the business object
        // this allows to use different implementations for different
        // workflow objects types
        INotificationService ns = obj.getNotificationService ();
        ns.initService (this.user, this.env, this.sess, this.app);

        // add objects oid to vector
        Vector<OID> objectOids = new Vector<OID> ();
        objectOids.addElement (obj.oid);

        // loop through all notifications of given state
        // and perform the notification
        NotificationTemplate templ;
        Notify notify;

        for (Iterator<Notify> iter = state.notification.iterator ();
            iter.hasNext ();)
        {
            // get next element:
            notify = iter.next ();

            // check if valid
            if (notify != null)
            {
                // create template; add values
                templ = new NotificationTemplate (notify.subject,
                                                  notify.content,
                                                  notify.description,
                                                  notify.activity);

                // perform notification for given object to
                // given users with given templ
                try
                {
                    if (notify.userOIDs != null)
                    {
                        ns.performNotification (
                            notify.userOIDs, objectOids, templ, false);
                    } // if usersOIDs != null

                    if (notify.groupOIDs != null &&
                        notify.groupOIDs.size () > 0)
                    {
                        //retrieve userOIDs for groupOIDs
                        Vector<OID> groupMemberUserOids = GroupHelpers
                            .getUsersForGroups (notify.groupOIDs, null, this.getUser (), this.env);

                        //remove the users from defined in the users list to avoid duplicate notifications
                        if (notify.userOIDs != null)
                        {
                            groupMemberUserOids.removeAll (notify.userOIDs);
                        } // if usersOIDs != null

                        // perform notification for all group's users
                        ns.performNotification (
                            groupMemberUserOids, objectOids, templ, false);
                    } // if group.OIDs != null
                } // try
                catch (NotificationFailedException exc)
                {
                    // error during notification operation
                    // show warning for users who where not notified
                    failedReceiverNames = ns.getFailedReceiverUserNames ();
                    // log entry: warning
                    this.log.add (DIConstants.LOG_WARNING,
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_NOTIFY_FAILED, env) +
                        failedReceiverNames,
                        this.wfdefinition.displayLog);

// probably later
//                    return false;
                } // catch

            } // if
        } // for iter

        // success - exit
        return true;
    } // sendNotification


    /**************************************************************************
     * Changes/adds rights entries of every CC user in given state according to
     * the given tag (RIGHTS|REMAINRIGHTS). <BR/>
     *
     * @param state       the state object
     * @param tag         the cc-entries rights tag
     */
    protected void setCCRightsEntries (State state, String tag)
    {
        // local variables
        Receiver cc;
        int rights;

        // iterate through cc-collection and change rights
        // according to given tag
        for (int i = 0; i < state.ccs.size (); i++)
        {
            // get cc infos
            cc = state.ccs.elementAt (i);

            // if cc-user is equal to receiver: ignore!
            if (cc.user.id == state.receiver.user.id)
            {
                continue;
            } // if
            // if there is no valid user: ignore!
            if (cc.user != null)
            {
                // set remain-rights for receiver on object according
                // to given tag in states CC definition
                // (in list only; db-access will be performed in dbaction_phase)
                if (tag.equals (WorkflowTagConstants.ATTR_RIGHTS))
                {
                    rights = this.rightsMapping.getEntry (cc.rights);
                } // if
                else
                {
                    rights = this.rightsMapping.getEntry (cc.remainRights);
                } // else

                if (rights < 0)
                {
                    // log entry: unknown rights entry!
                    this.log.add (DIConstants.LOG_WARNING,
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env) + " " +
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_UNKNOWN_RIGHTS_ENTRY, env) + " " +
                        WorkflowTagConstants.ATTR_RIGHTS + ":" +
                        tag + "=" + rights,
                        this.wfdefinition.displayLog);

                    // NON-CRITICAL ERROR: no exit
                } // if
                else
                {
                    this.rightsList.changeEntry (cc.user.id, rights);
                } // else

            } // if
            else
            {
                // log entry: error - cc-user not found
                this.log.add (DIConstants.LOG_WARNING,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_RECEIVER_NOT_FOUND, env) + " (RIGHTS) " +
                    " " + cc.name,
                    this.wfdefinition.displayLog);

                // NON-CRITICAL-ERROR: no exit
            } // else
        } // for ( int i = 0; i < currentState.ccs.size(); i++)
    } // setCCRightsEntries


    /**************************************************************************
     * Add reference-entry for each CC user in given state to reference list.
     * <BR/>
     * The actual db-access will be performed as last state in the db_action
     * phase.
     *
     * @param   obj         The object for which to add the references.
     * @param   state       the state object
     */
    protected void addCCReferences (BusinessObject obj, State state)
    {
        Receiver cc;

        // iterate through cc-collection and create reference for
        // each entry
        for (int i = 0; i < state.ccs.size (); i++)
        {
            // get cc infos
            cc = state.ccs.elementAt (i);

            // if there is no valid user
            if (cc.user != null)
            {
                // create reference cc-user on object

                // creation of reference for receiver
                // (in list only; db-access will be performed in dbaction_phase)
                this.referencesList.addEntry (cc.user, cc.destinationId);

                // add protocol entry: forwarded to cc-user
                this.addProtocolEntry (obj,
                                       state.name,
                                       WorkflowConstants.OP_SENTTOCC,
                                       this.user,
                                       cc.user,
                                       "");
            } // if
            else
            {
                // invalid user
                // no error message - error message for invalid CC-user
                // entries already thrown in change CC-rights - would be
                // double entry in error log!
            } // else
        } // for ( int i = 0; i < currentState.ccs.size(); i++)
    } // addCCReferences


    /**************************************************************************
     * Physically create reference for each entry in the reference list. <BR/>
     *
     * if creation of reference fails -> NO critical error
     *
     * @param   obj     ???
     */
    protected void createReferences (BusinessObject obj)
    {
        // local variables
        User user;
        OID  targetId;

        // "clone" the references list object
        ReferencesList rl = (ReferencesList) this.referencesList.clone ();

        // loop through given keys - create reference for each entry;
        for (Iterator<User> iter = this.referencesList.keyIterator ();
             iter.hasNext ();)
        {
            // get next element:
            user = iter.next ();

            // get target container id for found user
            targetId = rl.getEntry (user);

            // check if targetId is valid
            if (targetId != null)
            {
                // create reference, check success
                if (!this.objectHandler.createReference (targetId,
                                                         obj.oid,
//
// BUG 1673 - Referenzen werden nun mit Owner SYSTEMUSER erstellt!
//
//                                                         user))
                                                         this.systemUser))
                {
                    // log entry: error - could not create link
                    this.log.add (DIConstants.LOG_WARNING,
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_CREATELINK_FAILED, env) + " " +
                        user.fullname,
                        this.wfdefinition.displayLog);
                    // NON-CRITICAL error: no exit
                } // if; failure
                else
                {
                    // log entry: ccreceiver
                    this.log.add (DIConstants.LOG_ENTRY,
                        MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                            WorkflowTokens.ML_FORWARD_CC, env) + ": " +
                        user.fullname,
                        this.wfdefinition.displayLog);
                    // log entry: write to file only
                    this.log.add (DIConstants.LOG_ENTRY,
                                  "[User: " + user.username + ", id=" +
                                  user.id + "]",
                                  false);
                    this.log.add (DIConstants.LOG_ENTRY,
                                  "[Target: oid=" +
                                  targetId.toString () + "]",
                                  false);
                    this.log.add (DIConstants.LOG_ENTRY,
                                  " ",
                                  false);
                } // else
            } // if (targetId != null)
            else
            {
                // log entry: error - could not create link
                this.log.add (DIConstants.LOG_WARNING,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_RECEIVERDEST_NOT_FOUND, env) + " (CREF) " +
                    user.fullname,
                    this.wfdefinition.displayLog);
                // NON-CRITICAL error: no exit
            } // else

            // remove entry from list
            rl.removeEntry (user, targetId);
        } // for iter
    } // createReferences



    /***************************************************************************
     * Add an entry to the workflow protocol. <BR/>
     *
     * @param   object              The object for which to add the entry.
     * @param   currentState        The current workflow state.
     * @param   operationType       Type of operation.
     * @param   fromParticipant     Starting participant.
     * @param   toParticipant       Addressed participant.
     * @param   additionalComment   Additional comment.
     */
    protected void addProtocolEntry (BusinessObject object,
                                     String currentState,
                                     int operationType,
                                     User fromParticipant,
                                     User toParticipant,
                                     String additionalComment)
    {
        // create new protocol entry
        WorkflowProtocolEntry entry
            = new WorkflowProtocolEntry (this.workflowInstance.oid,
                                    object.oid,
                                    object.name,
                                    currentState,
                                    operationType,
                                    fromParticipant,
                                    toParticipant,
                                    additionalComment);

        // add entry to protocol
        this.protocol.addElement (entry);
    } // addProtocolEntry


    /**************************************************************************
     * Stores all protocol entries in workflow protocol in DB. <BR/>.
     */
    protected void storeProtocol ()
    {
        // local variables
        WorkflowProtocolEntry entry;

        // iterate through protocol vector:
        for (Iterator<WorkflowProtocolEntry> iter = this.protocol.iterator ();
            iter.hasNext ();)
        {
            // get next entry:
            entry = iter.next ();

            // store it:
            this.objectHandler.createProtocolEntry (
                    entry.instanceId,
                    entry.objectId,
                    entry.objectName,
                    entry.currentState,
                    entry.operationType,
                    entry.fromParticipant,
                    entry.toParticipant,
                    entry.additionalComment);
        } // for iter
    } // storeProtocol


    /**************************************************************************
     * Initializes variables. <BR/>.
     * Stores additional variables to wfdefinitions variable object;
     * needed internaly for error-management.
     *
     * @return  <CODE>true</CODE>    success
     *          <CODE>false</CODE>   otherwise
     */
    private boolean initVariables ()
    {
        //
        // add default variables ERRORCODE and ERRORMESSAGE
        // to variables list of workflow-definition
        //
        Variable errorcode
            = new Variable (WorkflowConstants.VARIABLE_ERRORCODE,
                            WorkflowConstants.VARIABLETYPE_NUMBER,
                            WorkflowConstants.VARIABLETYPELENGTH_NUMBER,
                            "errorcode of action",
                            "0");
        Variable errormsg
            = new Variable (WorkflowConstants.VARIABLE_ERRORMESSAGE,
                            WorkflowConstants.VARIABLETYPE_TEXT,
                            WorkflowConstants.VARIABLETYPELENGTH_TEXT,
                            "errormessage of action",
                            "OK");

        // add to variables list
        this.wfdefinition.variables.addEntry (errorcode);
        this.wfdefinition.variables.addEntry (errormsg);

        // exit
        return true;
    } // initVariables


    /**************************************************************************
     * Retrieves workflow-instances variables from db and stores it in
     * wfdefinitions variables-object. <BR/>
     * Checks if variables from db are undefined in workflow definition.
     *
     * @return  <CODE>true</CODE>    success
     *          <CODE>false</CODE>   otherwise
     */
    private boolean retrieveVariables ()
    {
        // init variables
        Variables dbVariables;
        Variable v1 = null;
        Variable v2 = null;
        boolean found;

        // retrieve variables for current workflow-instance
        dbVariables
            = this.objectHandler.getVariablesOfInstance (this.workflowInstance);

        // check success
        if (dbVariables == null)
        {
            // log entry: error - could not create link
            this.log.add (DIConstants.LOG_ERROR,
                    "Error when retrieving Variables from DB",
                    this.wfdefinition.displayLog);
            // CRITICAL error: exit
            return false;
        } // if

        // store db-variables in wf-definition
        // check if some variables are stored in db, that are not
        // defined in workflow-definition;
        for (Iterator<Variable> iter = this.wfdefinition.variables.iterator ();
             iter.hasNext ();)
        {
            // get next entry from wfdefinition variables:
            v1 = iter.next ();

            // recheck with db-variables
            found = false;
            for (Iterator<Variable> iter2 = dbVariables.iterator ();
                iter2.hasNext ();)
            {
                // get next:
                v2 = iter2.next ();

                // check if variables are equal:
                if (v1.name.equalsIgnoreCase (v2.name))
                {
                    // set value:
                    v1.value = v2.value;
                    // equal: exit loop
                    found = true;
                    break;
                } // if
            } // for iter2

            // check if not found:
            if (!found)
            {
                // write warning
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_VARIABLEMAPPING_ERROR, env) +
                    " (" + v1.name + ")",
                    this.wfdefinition.displayLog);
                // NON-CRITICAL error: no exit
            } // if
        } // for iter

        // now the other way round: check if some variables are defined
        // that are not stored in db
        for (Iterator<Variable> iter = dbVariables.iterator (); iter.hasNext ();)
        {
            // get next entry from wfdefinition variables:
            v1 = iter.next ();

            // recheck with definitions variables
            found = false;
            for (Iterator<Variable> iter2 = this.wfdefinition.variables.iterator ();
                 iter2.hasNext ();)
            {
                // get next:
                v2 = iter2.next ();

                // check if variables are equal
                if (v1.name.equalsIgnoreCase (v2.name))
                {
                    // equal: exit loop
                    found = true;
                    break;
                } // if
            } // for iter2

             // check if not found:
            if (!found)
            {
                // write warning
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_VARIABLEMAPPING_ERROR, env) +
                    " (" + v1.name + ")",
                    this.wfdefinition.displayLog);
                // NON-CRITICAL error: no exit
            } // if
        } // for iter

        // exit
        return true;
    } // retrieveVariables


    /**************************************************************************
     * Stores variables. <BR/>
     * Stores some necessary system-variables to the wfdefinitions
     * variables-object.
     *
     * @return  <CODE>true</CODE>    success
     *          <CODE>false</CODE>   otherwise
     */
    private boolean storeVariables ()
    {
        //
        // remove default variables ERRORCODE and ERRORMESSAGE
        //
        Variable vCode
            = this.wfdefinition.variables.getEntry (WorkflowConstants.VARIABLE_ERRORCODE);
        Variable vMessage
            = this.wfdefinition.variables.getEntry (WorkflowConstants.VARIABLE_ERRORMESSAGE);

        this.wfdefinition.variables.removeEntry (vCode);
        this.wfdefinition.variables.removeEntry (vMessage);

        // store variables in db
        if (!this.objectHandler.storeVariablesOfInstance (this.workflowInstance,
                                                          this.wfdefinition.variables))
        {
            // log entry: error - could not create link
            this.log.add (DIConstants.LOG_ERROR,
                    "Error when writing Variables to DB",
                    this.wfdefinition.displayLog);
            // CRITICAL error: exit
            return false;
        } // if

        // exit
        return true;
    } // storeVariables


    /**************************************************************************
     * Delete variables. <BR/> Delete all variables for current instance.
     *
     * @return  <CODE>true</CODE>    success
     *          <CODE>false</CODE>   otherwise
     */
    private boolean deleteVariables ()
    {
        // delete all entries:
        if (!this.objectHandler.deleteVariablesOfInstance (this.workflowInstance))
        {
            // log entry: error - could not create link
            this.log.add (DIConstants.LOG_ERROR,
                    "Error when deleting Variables in DB",
                    this.wfdefinition.displayLog);
            // CRITICAL error: exit
            return false;
        } // if

        // exit:
        return true;
    } // storeVariables


    /***************************************************************************
     * Initializes and returns WorkflowObjectHandler object. <BR/>
     *
     * @return      the WorkflowObjectHandler object
     *              <CODE>null</CODE> if it could not be created
     */
    public WorkflowObjectHandler getObjectHandler ()
    {
        WorkflowObjectHandler objectHandler = null;

        // create handler:
        objectHandler = new WorkflowObjectHandler ();

        // environment and session must be set,
        // because it is used in super-class filter.
        // should not be necessary though
        objectHandler.initObject (
            OID.getEmptyOid (), this.user, this.env, this.sess, this.app);

        return objectHandler;
    } // getObjectHandler


    /***************************************************************************
     * Initializes and returns WorkflowRightsHandler object. <BR/>
     *
     * @return      the WorkflowRightsHandler object
     *              <CODE>null</CODE> if it could not be created
     */
    public WorkflowRightsHandler getRightsHandler ()
    {
        WorkflowRightsHandler rightsHandler = null;

        // create handler:
        rightsHandler = new WorkflowRightsHandler ();

        // environment and session must be set,
        // because it is used in super-class filter.
        // should not be necessary though
        rightsHandler.initObject (
            OID.getEmptyOid (), this.user, this.env, this.sess, this.app);

        return rightsHandler;
    } // getRightsHandler


    /***************************************************************************
     * Initializes and returns WorkflowActionHandler object. <BR/>
     *
     * @return      the WorkflowActionHandler object
     *              <CODE>null</CODE> if it could not be created
     */
    public WorkflowActionHandler getActionHandler ()
    {
        WorkflowActionHandler actionHandler = null;

        // create handler:
        actionHandler = new WorkflowActionHandler ();

        // environment and session must be set,
        // because it is used in super-class filter.
        // should not be necessary though
        actionHandler.initObject (
            OID.getEmptyOid (), this.user, this.env, this.sess, this.app);

        return actionHandler;
    } // getActionHandler


    /**************************************************************************
     * Initialises the log. Creates a new log object and sets the environment.
     * Additionally the m2AbsBasePath will be set in order to ensure the
     * correct log default path. <BR/>
     *
     * @return  The log object.
     */
    protected Log_01 initLog ()
    {
        Log_01 log = null;

        // create and init new log object
        // some remarks to BBs Log_01 object:
        // - should not extend BusinessObject
        // - no environment/db-access necessary
        // - should only RETURN output objects: HTML and String
        // - [probably: write to file]
        log = new Log_01 ();
        log.initObject (
            OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
/*
// not needed - no logging if not path set!
            this.log.setM2AbsBasePath (xxx);
*/

        // exit method
        return log;
    } // initLog


////////
//
// HACK: SCHWAN-STABILO --> XPATH in TRANSITION CONDITIONS
//
    /***************************************************************************
     * Evaluates given XPath-expression on the given (XMLViewer) object
     * and returns the value. <BR/>
     *
     * @param xpathExpr     XPath-expression to evaluate on given object
     * @param obj           (XMLViewer) object for xpath-evaluation
     *
     * @return      The value returned by the the XPath-evaluation;
     *              <CODE>null</CODE> if an error occured;
     *              the given text if no XPath found in expression
     */
    public String evaluateXPathExpression (String xpathExpr, BusinessObject obj)
    {
        String xpathExprLocal = xpathExpr; // variable for local assignments

        // check if given string-value is an XPATH-expression
        if (xpathExprLocal.startsWith ("#XPATH:"))
        {
            // remove leading '#XPATH' and trailing '#'
            xpathExprLocal = StringHelpers.replace (StringHelpers.replace (
                xpathExprLocal, "#XPATH:", ""), "#", "");

            // evaluate x-path-expression; result will be stored in
            // vector in order: (1)ERRORCODE (2)ERRORMESSAGE (3)XPATH-RESULT
            Vector<String> callInParam = new Vector<String> ();
            callInParam.addElement (xpathExprLocal);

            // call xpath-evaluation
            Vector<String> callOutParams = obj.performXPath (callInParam);

            // check if returned values are valid
            if (callOutParams == null)
            {
                // log entry: error while resolving xpath-expression
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env)
                    + " " + "XPATH - XPath-evaluation returned null: " + xpathExprLocal,
                    false);
                // exit
                return null;
            } // if (callOutParams == null)

            // check errorcode and errormessage (first and second parameter)
            // check if at least 2 return-values in vector
            if (callOutParams.size () < 2)
            {
                // log entry: error while resolving xpath-expression
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env)
                    + " " + "XPATH - No ERRORCODE or ERRORMESSAGE found: " +
                    callOutParams.toString (),
                    false);
                // exit
                return null;
            } // if (callOutParams.size() < 2)

            // check errorcode - exit on error
            String callOutParam = callOutParams.elementAt (0);
            if (!callOutParam.equals ("0"))
            {
                // log entry: error while resolving xpath-expression
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env)
                    + " " + "XPATH - Error during xpath-evaluation: " +
                    callOutParams.toString (),
                    false);
                // exit
                return null;
            } // if (!callOutParam.equals("0"))

            // no error: check if 3rd parameter (result) exists.
            if (callOutParams.size () != 3)
            {
                // log entry: error while resolving xpath-expression
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_GENERAL_DEFINITION_ERROR, env)
                    + " " + "XPATH - No error raised, but result is missing: " +
                    callOutParams.toString (),
                    false);
                // exit
                return null;
            } // if (callOutParams.size() != 3)

            // return evaluated xpath-expression
            return callOutParams.elementAt (2);
        } // if

        // no xpath-expression found in given string
        return xpathExprLocal;
    } // evaluateXPathExpression
//
//
//
////////

} // WorkflowService
